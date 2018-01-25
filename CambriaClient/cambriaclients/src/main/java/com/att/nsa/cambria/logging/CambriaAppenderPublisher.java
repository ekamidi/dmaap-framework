/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
/**
 * Publish log entries to Cambria, used by CambriaAppender.
 * Doesn't do any log4j logging to avoid cycles where something downstream logs,
 * which caused CambriaAppender.append() to get called recursively.
 * 
 * See CambriaAppender header for log4j configuration parameters.
 */
package com.att.nsa.cambria.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.helpers.LogLog;
import org.slf4j.Logger;

import com.att.nsa.apiClient.credentials.ApiCredential;
import com.att.nsa.apiClient.http.HttpClient;
import com.att.nsa.apiClient.http.HttpClient.ConnectionType;
import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.cambria.client.impl.CambriaConstants;
import com.att.nsa.cambria.client.impl.CambriaFormat;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class CambriaAppenderPublisher implements CambriaBatchingPublisher
{
    // Object instance builder
    public static class Builder
    {
        private List<String>fHosts = null;          // Cambria servers - no default
        private String fTopic = null;               // Cambria topic - no default
        private boolean fCompress = false;          // compress the message data before sending
        private int fMaxBatchSize = 100;            // send if there are at least this many pending
        private long fMaxBatchAgeMs = 1000;         // send if we haven't sent in this amount of time
        private long fWaitMsecsAfterError = 1000;   // wait this long after failure before trying again
//		private Level fLevel = Level.WARN;          // printing (to stdout/stderr)
        private String fApiKey = null;              // for authenticating to Cambria server
        private String fApiSecret = null;           // for authenticating to Cambria server
        private ConnectionType fConnectionType = ConnectionType.HTTP;
        
        public Builder() {}
        public Builder usingHosts(String hosts) { fHosts = Arrays.asList(hosts.trim().split("\\s*,\\s*")); return this; }
        public Builder onTopic(String topic) { fTopic = topic; return this; }
        public Builder enableCompresion(boolean compress) { fCompress = compress; return this; }
        public Builder limitBatch ( int messageCount, int ageInMs ) { fMaxBatchSize = messageCount; fMaxBatchAgeMs = ageInMs; return this; }
        public Builder waitMsecsAfterError ( long msecs ) { fWaitMsecsAfterError = msecs; return this; }
        public Builder level ( String level ) { /*fLevel = Level.toLevel(level);*/ return this; }
        public Builder authenticatedBy ( String apiKey, String apiSecret ) { fApiKey = apiKey; fApiSecret = apiSecret; return this; }
        public Builder connectionType(ConnectionType connectionType) { this.fConnectionType = connectionType; return this; }
        
        public CambriaAppenderPublisher build() throws IllegalArgumentException
        {
            CambriaAppenderPublisher publisher = new CambriaAppenderPublisher(this);
            return publisher;
        }
    }

    /**
     * Add a message to the queue to be sent.
     * @param partition  Cambria partition
     * @param msg        Log message to be sent
     */
    @Override
    public int send(String partition, String msg) throws IOException {
        return send(new message(partition, msg));
    }

    @Override
    public int send(message msg) throws IOException {
        if ( fClosed )
        {
            throw new IllegalStateException ( "The publisher was closed." );
        }
        fPending.add ( msg );
        return fPending.size();
    }
    
    /**
     * Add a bunch of messages to the queue to be sent.
     * @param msgs        Log messages to be sent
     */
    @Override
    public int send(Collection<message> msgs) throws IOException {
        if ( fClosed )
        {
            throw new IllegalStateException ( "The publisher was closed." );
        }
        for (message msg: msgs)
        {
            fPending.add ( msg );
        }
        return fPending.size();
    }

    /**
     * Send remaining messages in the queue and stop looking for new messages.
     * Wait forever for pending messages to be sent, as long
     * as sending is successful. Stop if sending fails.
     */
    @Override
    public void close() {
        try
        {
            List<message> remains = close ( Long.MAX_VALUE, TimeUnit.MILLISECONDS );
            if ( remains.size() > 0 )
            {
                logDebug("Closing publisher with " + remains.size() + " messages unsent. " );
            }
        }
        catch (InterruptedException e)
        {
            logWarn("Possible message loss closing. " + e.getMessage());
        }
    }

    /**
     * Send remaining messages in the queue and stop looking for new messages.
     * Stop when sending fails.
     * @param timeout        How much time to allow for messages to be sent
     * @param timeoutUnits
     */
    @Override
    public List<message> close(long timeout, TimeUnit timeoutUnits) throws InterruptedException
    {
        fClosed = true;

        // stop the background sender
        fExec.setContinueExistingPeriodicTasksAfterShutdownPolicy ( false );
        fExec.setExecuteExistingDelayedTasksAfterShutdownPolicy ( false );
        fExec.shutdown ();

        final long now = System.currentTimeMillis();
        final long waitInMs = TimeUnit.MILLISECONDS.convert ( timeout, timeoutUnits );
        final long timeoutAtMs = now + waitInMs;  // wait until this time, then quit

        // while there's still time and there are messages to send and we are able to send
        while ( System.currentTimeMillis() < timeoutAtMs && getPendingMessageCount() > 0  && send())
        {
            Thread.sleep ( 250 );
        }

        // Return unsent messages
        final LinkedList<message> result = new LinkedList<message> ();
        fPending.drainTo ( result );
        logDebug("number of messages remaining: " + result.size());
        return result;
    }

    @Override
    public void logTo(Logger log)
    {
        // doesn't do any logging
    }

    @Override
    public void setApiCredentials(String apiKey, String apiSecret)
    {
        if (apiKey != null && apiSecret != null)
        {
            fCredential = new ApiCredential(apiKey, apiSecret);
        }
    }

    @Override
    public void clearApiCredentials()
    {
        fCredential = null;        
    }

    @Override
	public void setHttpBasicCredentials ( String username, String password )
	{
    	if ( username != null && password != null )
    	{
    		fBasicAuthUsername = username;
    		fBasicAuthPassword = password;
    	}
	}

	@Override
	public void clearHttpBasicCredentials ()
	{
		fBasicAuthUsername = null;
		fBasicAuthPassword = null;
	}

	@Override
    public int getPendingMessageCount() {
        return fPending.size();
    }

    // Variables set from build/construction
    private String fTopic;                    // Cambria topic
    private List<String>fHosts;               // list of hosts to try sending to
    private boolean fCompress;                // compress the message data before sending
    private int fMaxBatchSize;                // send if there are at least this many pending
    private long fMaxBatchAgeMs;              // send if we haven't sent in this amount of time
    private long fWaitMsecsAfterError;        // wait this long after failure before trying again
//	private Level fLevel;                     // print debugging output to stdout/stderr
    private ApiCredential fCredential;        // for creating HTTP authentication headers
    private ConnectionType fConnectionType;   // HTTP, HTTPS or HTTPS_NO_VALIDATION
	private String fBasicAuthUsername;
	private String fBasicAuthPassword;

    // Class variables
    private int fCurrHostIndex = 0;           // send to this host in the host list
    private long fNextTimeToSend;             // send at this time if there are less than fMaxBatchSize pending
    private boolean fErrorPending = false;    // set true when failed sending and all hosts have been tried
    private volatile boolean fClosed = false; // set true when this publisher gets closed
    private LinkedBlockingQueue<message>  fPending = new LinkedBlockingQueue<message>(); // message queue
    private final ScheduledThreadPoolExecutor fExec;  // schedule checks for messages to send
    
    /**
     * Constructor
     * @param builder
     */
    private CambriaAppenderPublisher(Builder builder) throws IllegalArgumentException {
        // populate params from builder
        this.fCompress            = builder.fCompress;
        this.fHosts               = builder.fHosts;
        this.fTopic               = builder.fTopic;
        this.fMaxBatchSize        = builder.fMaxBatchSize;
        this.fMaxBatchAgeMs       = builder.fMaxBatchAgeMs;
        this.fWaitMsecsAfterError = builder.fWaitMsecsAfterError;
//		this.fLevel               = builder.fLevel;
        this.fConnectionType      = builder.fConnectionType;
        if (fConnectionType.equals(ConnectionType.HTTPS_NO_VALIDATION))
        {
            HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactoryNoValidation());
        }
        if (fHosts == null || fHosts.isEmpty())
        {
            throw new IllegalArgumentException("Cambria server list cannot be empty");
        }
        if (fTopic == null || fTopic.isEmpty())
        {
            throw new IllegalArgumentException("Cambria topic is required");
        }
        setApiCredentials(builder.fApiKey, builder.fApiSecret);
        
        // start thread that peridically checks for messages to send
        fExec = new ScheduledThreadPoolExecutor ( 1 );
        fExec.scheduleAtFixedRate ( new Runnable()
        {
            @Override
            public void run ()
            {
                send ();
            }
        }, 100, 50, TimeUnit.MILLISECONDS );
    }

    /**
     * Send the messages in the pending queue.
     * @return true if successful, false otherwise
     */
    private boolean send()
    {
        if (!shouldSendNow())
        {
            return true; // nothing to send yet
        }
        int queueSize = fPending.size(); // process messages currently in the queue
        byte [] messageToPost = cambriaMessageString(fPending, queueSize); // encode the message(s)
        for (int triedCount = 0; triedCount < fHosts.size(); triedCount++)
        {
            if (post(fHosts.get(fCurrHostIndex), messageToPost))
            {
                // Successful - remove the messages we just sent from the pending queue
                for (int ii = 0; ii < queueSize; ii++)
                {
                    fPending.remove();
                }
                fErrorPending = false;
                fNextTimeToSend = System.currentTimeMillis() + fMaxBatchAgeMs;
                return true;
            }
            // this host failed, try the next one
            fCurrHostIndex = (fCurrHostIndex + 1 ) % fHosts.size();
        }
        fErrorPending = true;
        fNextTimeToSend = System.currentTimeMillis() + fWaitMsecsAfterError;
        return false;
    }
    
    /**
     * Determine whether to send at a given time, by checking
     * the number of messages pending and the time since the last send.
     * @return true if messages should be sent now, false otherwise
     */
    private boolean shouldSendNow ()
    {
        int numMsgsPending = fPending.size();
        if (numMsgsPending == 0)
        {
            return false;
        }
        long timeNow = System.currentTimeMillis();
        // if appender is closed, or there are enough messages to send now and the last send succeeded, or if it's time to send anyway
        if (fClosed || (numMsgsPending >= fMaxBatchSize && !fErrorPending) || timeNow > fNextTimeToSend)
        {
            return true;
        }
        return false;
    }
    
    /**
     * Form a Cambria message string from a list of messages to be sent.
     * @param messages  message queue
     * @param numMsgs   number of messages to process
     * @return          Log messages with Cambria prefixs
     */
    private byte [] cambriaMessageString(Queue<message> messages, int numMsgs)
    {
        final ByteArrayOutputStream baseStream = new ByteArrayOutputStream ();
        OutputStream os;
        try
        {
            os = (fCompress ? new GZIPOutputStream ( baseStream ) : baseStream);
            for (Iterator<message> iter = messages.iterator(); iter.hasNext() && numMsgs > 0; numMsgs--)
            {
                message msg = iter.next();
                os.write ( ( "" + msg.fPartition.length () ).getBytes() );
                os.write ( '.' );
                os.write ( ( "" + msg.fMsg.length () ).getBytes() );
                os.write ( '.' );
                os.write ( msg.fPartition.getBytes() );
                os.write ( msg.fMsg.getBytes() );
                os.write ( '\n' );
            }
            os.close ();
        }
        catch (IOException e) // from new GZIPOutputStream(), should never happen cause we're writing to memory
        {
            logError("IOException creating message: " + e);
        }
        return baseStream.toByteArray();
    }
    

    /**
     * Post a message via HTTP to a Cambria server.
     * @param host         Cambria host to post to
     * @param message      Message to post
     * @return             True if successfully connected to the host, false otherwise
     *                     Doesn't return false for errors like "404 Not Found" because connecting
     *                     to a different host isn't likely to fix that.
     */
    private boolean post(String host, byte [] message)
    {
        URL url = null;
        HttpURLConnection connection = null;
        Scanner responseReader = null;
        try
        {
        	// FIXME: this code doesn't work if the user has a port specified in the host value, like
        	// "localhost:3904"
    
            // Send POST
            if (fConnectionType.equals(ConnectionType.HTTP))
            {
                url = new URL("http", host, CambriaConstants.kStdCambriaServicePort,
                        CambriaConstants.kBasePath + URLEncoder.encode(fTopic, "UTF-8"));
                connection = (HttpURLConnection) url.openConnection();
            }
            else
            {
                url = new URL("https", host, CambriaConstants.kStdCambriaHttpsServicePort,
                        CambriaConstants.kBasePath + URLEncoder.encode(fTopic, "UTF-8"));
                connection = (HttpsURLConnection) url.openConnection();
            }
            logDebug("URL: [" + url.toString() + "]");
            
            // Set the headers - just used whatever I saw when sent from CambriaSimplerBatchPublisher using Apache Http
            connection.setReadTimeout(HttpClient.kDefault_SocketTimeoutMs);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", fCompress ? CambriaFormat.CAMBRIA_ZIP.toString () : CambriaFormat.CAMBRIA.toString ());
            connection.setRequestProperty("Accept",  "application/json");
            connection.setRequestProperty("Accept-Encoding",  "gzip,deflate");
            if ( fCredential != null ) // add authentication headers
            {
                logDebug ( "authenticating with [" + fCredential.getApiKey () + "]");
                final Map<String,String> headers = fCredential.createAuthenticationHeaders ( System.currentTimeMillis () );
                for ( Entry<String, String> header : headers.entrySet () )
                {
                    connection.setRequestProperty( header.getKey(), header.getValue() );
                }
            }
            if ( fBasicAuthUsername != null )
            {
                logDebug ( "authenticating with [" + fBasicAuthUsername + "]");
				final String authString = fBasicAuthUsername + ":" + fBasicAuthPassword;
				final String encoded = Base64.encodeBase64String ( authString.getBytes () );
                connection.setRequestProperty ( "Authorization", "Basic " + encoded  );
            }
            
            // Connect and send
            logTrace("cambriaMessage: [" + message + "]");
            connection.getOutputStream().write(message);
            connection.getOutputStream().close();
            
            // Get the response
            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            logDebug("Reponse code: [" + responseCode + "] response message: [" + responseMessage + "]");
            StringBuilder response = new StringBuilder();
            try
            {
               responseReader = new Scanner(connection.getInputStream());
               while (responseReader.hasNextLine())
               {
                  response.append(responseReader.nextLine());
               }
               logDebug("RESPONSE: [" + response.toString() + "]");
            }
            catch (IOException e) // exception reading the response
            {  // check the connection's error stream for error messages from the server
               logInfo("IOException reading response: " + e);
               InputStream errorStream = connection.getErrorStream();
               if (errorStream != null) // could be null if no error reported by the server
               {
                   responseReader = new Scanner(errorStream);
                   while (responseReader.hasNextLine())
                   {
                      response.append(responseReader.nextLine());
                   }
                   logWarn("ERROR RESPONSE: [" + response.toString() + "]");
               }
               logWarn("No error response from the server");
            }
        }
        catch (MalformedURLException e) // from new URL() for unknown protocol, shouldn't happen cause we use "http"
        {
            logError("MalformedURLException, host=" + host + " topic=" + fTopic + " message=" + message + ": " + e);
            return false;
        }
        catch (IOException e)
        {
            logWarn("IOException connecting, host=" + host + " topic=" + fTopic + " message=" + message + ": " + e);
            return false;
        }
        finally
        {
            if ( connection != null )
            {
                connection.disconnect();
            }
            if (responseReader != null)
            {
                responseReader.close();
            }
        }
        return true;
    }
    
    /**
     * Create an instance of SSLSocketFactory that doesn't do any certificate validation.
     * @return SSLSocketFactory
     */
    private SSLSocketFactory sslSocketFactoryNoValidation()
    {
        SSLContext context = null;
        try
        {
            TrustManager[] trustManagers = new TrustManager[1];
            trustManagers[0] = new TrustManagerNoValidation();
            context = SSLContext.getInstance("TLS");
            context.init(null, trustManagers, null);
        } 
        catch (NoSuchAlgorithmException e)
        { // From SSLContext.getInstance() - should never happen unless "TLS" above becomes invalid somehow
        }
        catch (KeyManagementException e)
        { // From context.init() - should never happen because we give null key manager
        }
        return context.getSocketFactory();
    }
    
    /**
     * Create a TrustManager that doesn't validate anything.
     */
    private class TrustManagerNoValidation implements X509TrustManager
    {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
        {
            // Don't do anything
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
        {
            // Don't do anything
        }

        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return new X509Certificate[0];
        }
    }

    // Log4j provides a diagnostic log for log appenders. We don't want to assume that writing to 
    // stdout/stderr is okay for the application.
    private void logTrace ( String str ) { LogLog.debug ( str ); }
    private void logDebug ( String str ) { LogLog.debug ( str ); }
    private void logInfo ( String str ) { LogLog.debug ( str ); }
    private void logWarn ( String str ) { LogLog.warn ( str ); }
    private void logError ( String str ) { LogLog.error ( str ); }

//    /**
//     * Log to stdout/stderr for debugging
//     * @param level  log level
//     * @param str    message to print
//     */
//    private void logTrace(String str) { logit(Level.TRACE, str); }
//    private void logDebug(String str) { logit(Level.DEBUG, str); }
//    private void logInfo(String str)  { logit(Level.INFO, str); }
//    private void logWarn(String str)  { logit(Level.WARN, str); }
//    private void logError(String str) { logit(Level.ERROR, str); }
//    
//    private void logit(Level level, String str) {
//        PrintStream os = System.out;
//        if (level.isGreaterOrEqual(fLevel))
//        {
//            if (level.isGreaterOrEqual(Level.WARN))
//            {
//                os = System.err;
//            }
//            StackTraceElement st = Thread.currentThread().getStackTrace()[3];
//            os.println(new SimpleDateFormat("YYYYMMdd-HH:mm:ss:SSS").format(new Date(System.currentTimeMillis()))
//                    + ":" + level.toString()
//                    + ":(" + Thread.currentThread().getName() + "):"
//                    + st.getClassName() + "." + st.getMethodName() + "():" + st.getLineNumber()
//                    + ":" + str);
//        }
//    }
}
