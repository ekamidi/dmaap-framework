/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.att.nsa.apiClient.http.HttpClient;
import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;
import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.clock.SaClock;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class CambriaSimplerBatchPublisher extends CambriaBaseClient implements CambriaBatchingPublisher
{
	public static class Builder 
	{
		public Builder ()
		{
		}

		public Builder againstUrls ( Collection<String> baseUrls )
		{
			fUrls = baseUrls;
			return this;
		}

		public Builder withConnectionType ( ConnectionType ct )
		{
			fConnectionType = ct;
			return this;
		}

		public Builder usingHttp ()
		{
			fConnectionType = ConnectionType.HTTP;
			return this;
		}

		public Builder usingHttps ()
		{
			fConnectionType = ConnectionType.HTTPS;
			return this;
		}

		public Builder allowingSelfSignedCerts ()
		{
			fConnectionType = ConnectionType.HTTPS_NO_VALIDATION;
			return this;
		}

		public Builder onTopic ( String topic )
		{
			fTopic = topic;
			return this;
		}

		public Builder batchTo ( int maxBatchSize, long maxBatchAgeMs )
		{
			fMaxBatchSize = maxBatchSize;
			fMaxBatchAgeMs = maxBatchAgeMs;
			return this;
		}

		public Builder compress ( boolean compress )
		{
			fCompress = compress;
			return this;
		}

		public Builder authenticatedBy ( String apiKey, String apiSecret )
		{
			fApiKey = apiKey;
			fApiSecret = apiSecret;
			return this;
		}

		public Builder logSendFailuresAfter ( int threshold )
		{
			fFailLogThreshold = threshold;
			return this;
		}

		public Builder logTo ( Logger log )
		{
			fLogTo = log;
			return this;
		}

		public CambriaSimplerBatchPublisher build () throws MalformedURLException, GeneralSecurityException
		{
			final CambriaSimplerBatchPublisher pub =
				new CambriaSimplerBatchPublisher ( fConnectionType, fUrls, fTopic, fMaxBatchSize, fMaxBatchAgeMs,
					fCompress, fFailLogThreshold );
			if ( fApiKey != null )
			{
				pub.setApiCredentials ( fApiKey, fApiSecret );
			}
			if ( fLogTo != null )
			{
				pub.logTo ( fLogTo );
			}
			return pub;
		}

		private ConnectionType fConnectionType = ConnectionType.HTTP;
		private Collection<String> fUrls;
		private String fTopic;
		private int fMaxBatchSize = 100;
		private long fMaxBatchAgeMs = 1000;
		private boolean fCompress = false;
		private String fApiKey = null;
		private String fApiSecret = null;
		private int fFailLogThreshold = 10;
		private Logger fLogTo = null;
	};

	@Override
	public int send ( String partition, String msg )
	{
		return send ( new message ( partition, msg ) );
	}

	@Override
	public int send ( message msg )
	{
		final LinkedList<message> list = new LinkedList<message> ();
		list.add ( msg );
		return send ( list );
	}

	@Override
	public synchronized int send ( Collection<message> msgs )
	{
		if ( fClosed )
		{
			throw new IllegalStateException ( "The publisher was closed." );
		}
		
		for ( message userMsg : msgs )
		{
			fPending.add ( new TimestampedMessage ( userMsg ) );
		}
		return getPendingMessageCount ();
	}

	@Override
	public synchronized int getPendingMessageCount ()
	{
		return fPending.size ();
	}

	@Override
	public void close ()
	{
		try
		{
			final List<message> remains = close ( Long.MAX_VALUE, TimeUnit.MILLISECONDS );
			if ( remains.size() > 0 )
			{
				getLog().warn ( "Closing publisher with " + remains.size() + " messages unsent. "
					+ "Consider using CambriaBatchingPublisher.close( long timeout, TimeUnit timeoutUnits ) to recapture unsent messages on close." );
			}
		}
		catch ( InterruptedException e )
		{
			getLog().warn ( "Possible message loss. " + e.getMessage(), e );
		}
		catch ( IOException e )
		{
			getLog().warn ( "Possible message loss. " + e.getMessage(), e );
		}
	}

	@Override
	public List<message> close ( long time, TimeUnit unit ) throws IOException, InterruptedException
	{
		synchronized ( this )
		{
			fClosed = true;

			// stop the background sender
			fExec.setContinueExistingPeriodicTasksAfterShutdownPolicy ( false );
			fExec.setExecuteExistingDelayedTasksAfterShutdownPolicy ( false );
			fExec.shutdown ();
		}

		final long now = SaClock.now ();
		final long waitInMs = TimeUnit.MILLISECONDS.convert ( time, unit );
		final long timeoutAtMs = now + waitInMs;

		while ( SaClock.now() < timeoutAtMs && getPendingMessageCount() > 0 )
		{
			send ( true );
			Thread.sleep ( 250 );
		}

		synchronized ( this )
		{
			final LinkedList<message> result = new LinkedList<message> ();
			fPending.drainTo ( result );
			return result;
		}
	}

	/**
	 * Possibly send a batch to the cambria server. This is called by the background thread
	 * and the close() method
	 * 
	 * @param force
	 */
	private synchronized void send ( boolean force )
	{
		if ( force || shouldSendNow () )
		{
			if ( !sendBatch () )
			{
				getLog().warn ( "Send failed, " + fPending.size() + " message to send." );

				// note the time for back-off
				fDontSendUntilMs = sfWaitAfterError + SaClock.now ();
				fFailureCount++;
				if ( fFailureCount > fFailureCountThreshold )
				{
					getLog ().error ( kFailTag + " Send failure count is " + fFailureCount + ", above threshold " + fFailureCountThreshold + "." );
				}
			}
			else
			{
				if ( fFailureCount > fFailureCountThreshold )
				{
					getLog ().error ( kFailTag + " resetting failure count." );
				}
				fFailureCount = 0;
			}
		}
	}

	private synchronized boolean shouldSendNow ()
	{
		boolean shouldSend = false;
		if ( fPending.size () > 0 )
		{
			final long nowMs = SaClock.now ();

			shouldSend = ( fPending.size() >= fMaxBatchSize );
			if ( !shouldSend )
			{
				final long sendAtMs = fPending.peek().timestamp + fMaxBatchAgeMs;
				shouldSend = sendAtMs <= nowMs;
			}

			// however, wait after an error
			shouldSend = shouldSend && nowMs >= fDontSendUntilMs; 
		}
		return shouldSend;
	}

	private synchronized boolean sendBatch ()
	{
		// it's possible for this call to be made with an empty list. in this case, just return.
		if ( fPending.size() < 1 )
		{
			return true;
		}

		final long nowMs = SaClock.now ();
		final String url = CambriaConstants.makeUrl ( fTopic );

		getLog().info ( "sending " + fPending.size() + " msgs to " + url + ". Oldest: " + ( nowMs - fPending.peek().timestamp ) + " ms"  );

		try
		{
			final String contentType =
				fCompress ?
					CambriaFormat.CAMBRIA_ZIP.toString () :
					CambriaFormat.CAMBRIA.toString () 
			;

			final ByteArrayOutputStream baseStream = new ByteArrayOutputStream ();
			OutputStream os = baseStream;
			if ( fCompress )
			{
				os = new GZIPOutputStream ( baseStream );
			}
			for ( TimestampedMessage m : fPending )
			{
				os.write ( ( "" + m.fPartition.length () ).getBytes() );
				os.write ( '.' );
				os.write ( ( "" + m.fMsg.length () ).getBytes() );
				os.write ( '.' );
				os.write ( m.fPartition.getBytes() );
				os.write ( m.fMsg.getBytes() );
				os.write ( '\n' );
			}
			os.close ();

			final long startMs = SaClock.now ();
			final JSONObject result = post ( url, contentType, baseStream.toByteArray(), true );
			final String logLine = "cambria reply ok (" + (SaClock.now()-startMs) + " ms):" + result.toString ();
			getLog().info ( logLine );
			fPending.clear ();
			return true;
		}
		catch ( IllegalArgumentException x )
		{
			getLog().warn ( x.getMessage(), x );
		}
		catch ( HttpObjectNotFoundException x )
		{
			getLog().warn ( x.getMessage(), x );
		}
		catch ( HttpException x )
		{
			getLog().warn ( x.getMessage(), x );
		}
		catch ( IOException x )
		{
			getLog().warn ( x.getMessage(), x );
		}
		return false;
	}

	private final String fTopic;
	private final int fMaxBatchSize;
	private final long fMaxBatchAgeMs;
	private final boolean fCompress;
	private boolean fClosed;

	private final LinkedBlockingQueue<TimestampedMessage> fPending;
	private long fDontSendUntilMs;
	private final ScheduledThreadPoolExecutor fExec;

	private int fFailureCount;
	private final int fFailureCountThreshold;
	private static final String kFailTag = "PUB_CHRONIC_FAILURE:";	// caution changing this - tools may monitor logs for it

	private static final long sfWaitAfterError = 1000;

	private CambriaSimplerBatchPublisher ( ConnectionType ct, Collection<String> hosts, String topic, int maxBatchSize,
		long maxBatchAgeMs, boolean compress, int failureLogThreshold ) throws MalformedURLException, GeneralSecurityException
	{
		super ( ct, hosts, HttpClient.kDefault_SocketTimeoutMs );

		if ( topic == null || topic.length() < 1 )
		{
			throw new IllegalArgumentException ( "A topic must be provided." );
		}
		
		fClosed = false;
		fTopic = topic;
		fMaxBatchSize = maxBatchSize;
		fMaxBatchAgeMs = maxBatchAgeMs;
		fCompress = compress;

		fPending = new LinkedBlockingQueue<TimestampedMessage> ();
		fDontSendUntilMs = 0;

		fFailureCount = 0;
		fFailureCountThreshold = failureLogThreshold;

		fExec = new ScheduledThreadPoolExecutor ( 1 );
		fExec.scheduleAtFixedRate ( new Runnable()
		{
			@Override
			public void run ()
			{
				send ( false );
			}
		}, 100, 50, TimeUnit.MILLISECONDS );
	}

	private static class TimestampedMessage extends message
	{
		public TimestampedMessage ( message m )
		{
			super ( m );
			timestamp = SaClock.now();
		}
		public final long timestamp;
	}
}
