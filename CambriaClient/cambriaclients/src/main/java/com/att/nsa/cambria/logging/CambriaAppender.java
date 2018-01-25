/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
/**
 * Log4J appender that outputs log data to a Cambria server.
 * Add this to log4j.xml for configuration (defaults are shown):
 * 
    <appender name="CAMBRIA" class="com.att.nsa.cambria.logging.CambriaAppender">
        <!-- Cambria topic and partition -->
        <param name="Topic" value="Log4J_Topic"/>
        <param name="Partition" value="1"/>
        
        <!-- Comma separated list of Cambria servers, no default -->
        <param name="Hosts" value="hostname"/>
        
        <!-- Send a batch when the number of outstanding log messages reaches MaxBatchSize -->
        <param name="MaxBatchSize" value="100"/>
        
        <!-- Maximum amount of time before sending whatever log messages are pending -->
        <param name="MaxAgeMs" value="1000"/>
        
        <!-- Compress the data sent to Cambria -->
        <param name="Compress" value="false"/>
        
        <!-- CambriaAppenderPublisher stdout/stderr log output: DEBUG, INFO, WARN (default), ERROR, FATAL or OFF -->
        <param name="Level" value="WARN"/>
        
        <!-- Use CambriaAppenderPublisher to publish to Cambria -->
        <param name="CambriaAppenderPublisher" value="true"/>
        
        <!-- Cambria client authentication (no defaults) -->
        <param name="ApiKey" value="someKey"/>
        <param name="ApiSecret" value="someSecret"/>
        
        <!-- ConnectionType: HTTP, HTTPS or HTTPS_NO_VALIDATION -->
        <param name="ConnectionType" value="HTTP"/>
    </appender>
 */
package com.att.nsa.cambria.logging;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.helpers.NOPLogger;

import com.att.nsa.apiClient.http.HttpClient.ConnectionType;
import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.cambria.client.CambriaClientBuilders;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class CambriaAppender extends AppenderSkeleton {

	/**
	 * 
	 */
	public CambriaAppender() {
		super();
	}

	/**
	 * @param isActive
	 */
	public CambriaAppender(boolean isActive) {
		super(isActive);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#close()
	 */
	@Override
	public void close()
	{
		synchronized ( fPublisher )
		{
			if (!this.closed) {
				this.closed = true;
				fPublisher.close();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	@Override
	public boolean requiresLayout() {
		return false;
	}


	/* (non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	protected void append ( LoggingEvent event )
	{
	    if (fInAppend) return;  // prevent recursive call to append()
	    
		try {
            synchronized (fPublisher) // because the publisher service thread locks it too
            {
            	// FIXME: some processes write really quickly. can this grow above the emergency flush size
            	// before an attempt to publish is made?

                if (fPublisher.getPendingMessageCount() > emergencyFlushSize) {
                    try {
                        LogLog.error(
                                "Cambria appender is unable to send. Flushing queued messages.");
                        fPublisher.close(0, TimeUnit.SECONDS);
                        fPublisher = null;
                        buildPublisher();
                    } catch (IOException e) {
                        LogLog.warn("Problem flushing send queue: "
                                + e.getMessage(), e);
                    } catch (InterruptedException e) {
                        LogLog.warn("Problem flushing send queue: "
                                + e.getMessage(), e);
                    }
                }

                final String message = layout == null
                        ? event.getRenderedMessage() : layout.format(event);
                try {
                    fPublisher.send(partition, message);
                } catch (IOException e) {
                    LogLog.warn("Problem sending message: " + e.getMessage(), e);
                }
            } 
        } finally {
            fInAppend = false;
        }
	}

	@Override
	public void activateOptions ()
	{
		// yes this is redundant, but keep in mind that <0 is not set
		// and that's different from too low.
		if ( emergencyFlushSize < 0 || emergencyFlushSize < maxBatchSize )
		{
			emergencyFlushSize = maxBatchSize * 10;
		}

		buildPublisher ();
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getApiKey ()
	{
		return apiKey;
	}

	public void setApiKey ( String apiKey )
	{
		this.apiKey = apiKey;
	}

	public String getApiSecret ()
	{
		return apiSecret;
	}

	public void setApiSecret ( String apiSecret )
	{
		this.apiSecret = apiSecret;
	}

	public String getPartition() {
		return partition;
	}

	public void setPartition(String partition) {
		this.partition = partition;
	}

	public String getHosts() {
		return hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}
	
	public int getMaxBatchSize() {
		return maxBatchSize;
	}

	public void setMaxBatchSize(int maxBatchSize) {
		this.maxBatchSize = maxBatchSize;
	}

	public void setEmergencyFlushSize(int size) {
		this.emergencyFlushSize = size;
	}

	public int getEmergencyFlushSize() {
		return this.emergencyFlushSize;
	}

	public int getMaxAgeMs() {
		return maxAgeMs;
	}
	
	public String getLevel() {
	    return level;
	}
	
    public String getConnectionType() {
        return connectionType;
    }

	public void setMaxAgeMs(int maxAgeMs) {
		this.maxAgeMs = maxAgeMs;
	}	
	
	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public void setLevel(String level) {
	    this.level = level;
	}
	
    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }
	
	public void setCambriaAppenderPublisher(boolean useCambraAppenderPublisher) {
	    this.cambriaAppenderPublisher = useCambraAppenderPublisher;
	}
	private CambriaBatchingPublisher fPublisher;

    private boolean fInAppend = false; // use to prevent recursive call to 
    
	//Provided through log4j configuration
	private String topic;
	private String partition;
	private String hosts;
	private int maxBatchSize = 100;
	private int maxAgeMs = 500;
	private int emergencyFlushSize = -1;
	private boolean compress = false;
	private String apiKey = null;
	private String apiSecret = null;
	private String level = "ERRROR";
	private boolean cambriaAppenderPublisher = true;
    private String connectionType = "HTTP";

	private void buildPublisher ()
	{
		if ( hosts != null && topic != null && partition != null )
		{
			try
			{
			    if (cambriaAppenderPublisher) // non-log4j logging publisher
			    {
			        fPublisher = new CambriaAppenderPublisher.Builder()
	                    .usingHosts ( hosts )
	                    .onTopic ( topic )
	                    .authenticatedBy ( apiKey, apiSecret )
	                    .limitBatch ( maxBatchSize, maxAgeMs )
	                    .enableCompresion ( compress )
                        .level ( level )
                        .connectionType(connectionType.equalsIgnoreCase("HTTPS") ? ConnectionType.HTTPS :
                                (connectionType.equalsIgnoreCase("HTTPS_NO_VALIDATION") ? ConnectionType.HTTPS_NO_VALIDATION
                                        : ConnectionType.HTTP))
	                    .build ();
			    }
			    else
			    {
    				fPublisher = new CambriaClientBuilders.PublisherBuilder ()
    					.usingHosts ( hosts )
    					.onTopic ( topic )
    					.authenticatedBy ( apiKey, apiSecret )
    					.limitBatch ( maxBatchSize, maxAgeMs )
    					.enableCompresion ( compress )
    					.logTo ( NOPLogger.NOP_LOGGER )
    					.build ();
			    }
			}
			catch ( MalformedURLException e )
			{
				LogLog.error ( e.getMessage (), e );
			}
			catch ( GeneralSecurityException e )
			{
				LogLog.error ( e.getMessage (), e );
			}
		}
		else
		{
			LogLog
				.error ( "The Hosts, Topic, and Partition parameter are required to create a Cambria Log4J Appender" );
		}
	}
}
