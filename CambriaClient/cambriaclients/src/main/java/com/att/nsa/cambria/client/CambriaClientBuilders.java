/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client;

import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.TreeSet;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.apiClient.http.HttpClient;
import com.att.nsa.apiClient.http.HttpClient.ConnectionType;
import com.att.nsa.cambria.client.impl.CambriaConsumerImpl;
import com.att.nsa.cambria.client.impl.CambriaMetaClient;
import com.att.nsa.cambria.client.impl.CambriaSimplerBatchPublisher;
import com.att.nsa.metrics.CdmMetricsRegistry;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

/**
 * A collection of builders for various types of Cambria API clients
 * 
 */
public class CambriaClientBuilders
{
	/**
	 * A builder for a topic Consumer
	 */
	public static class ConsumerBuilder
	{
		/**
		 * Construct a consumer builder.
		 */
		public ConsumerBuilder () {}

		/**
		 * Set the host list
		 * @param hostList a comma-separated list of hosts to use to connect to Cambria
		 * @return this builder
		 */
		public ConsumerBuilder usingHosts ( String hostList ) { return usingHosts ( CambriaConsumerImpl.stringToList(hostList) ); }

		/**
		 * Set the host list
		 * @param hostSet a set of hosts to use to connect to Cambria
		 * @return this builder
		 */
		public ConsumerBuilder usingHosts ( Collection<String> hostSet ) { fHosts = hostSet; return this; }

		/**
		 * Use https to connect to Cambria
		 * @return this builder
		 */
		public ConsumerBuilder usingHttps () { return usingHttps ( true ); }

		/**
		 * Use https to connect to Cambria
		 * @return this builder
		 */
		public ConsumerBuilder usingHttps ( boolean useHttps )
		{
			return withConnectionType ( useHttps ? ConnectionType.HTTPS : ConnectionType.HTTP );
		}

		/**
		 * Allow this client to accept self-signed certificates from the Cambria API Server
		 * Don't use this unless you know what you are doing.
		 * @return this builder
		 */
		public ConsumerBuilder allowSelfSignedCertificates ()
		{
			return withConnectionType ( ConnectionType.HTTPS_NO_VALIDATION );
		}

		/**
		 * Specify the HTTP connection type.
		 * @param ct a connection type
		 * @return this builder
		 */
		public ConsumerBuilder withConnectionType ( ConnectionType ct )
		{
			fConnectionType = ct;
			return this;
		}

		/**
		 * Set the topic
		 * @param topic the name of the topic to consume
		 * @return this builder
		 */
		public ConsumerBuilder onTopic ( String topic ) { fTopic=topic; return this; }

		/**
		 * Set the consumer's group and ID
		 * @param consumerGroup The name of the consumer group this consumer is part of
		 * @param consumerId The unique id of this consumer in its group
		 * @return this builder
		 */
		public ConsumerBuilder knownAs ( String consumerGroup, String consumerId ) { fGroup = consumerGroup; fId = consumerId; return this; }

		/**
		 * Set the API key and secret for this client. If both arguments are null, this call
		 * has no effect. (If one is non-null and not both, an IllegalArgumentException is thrown.)
		 * @param apiKey
		 * @param apiSecret
		 * @return this builder
		 */
		public ConsumerBuilder authenticatedBy ( String apiKey, String apiSecret ) { fApiKey = apiKey; fApiSecret = apiSecret; return this; }

		/**
		 * Set the HTTP Basic auth for this client. If both username and password are null, this call
		 * has no effect. (If one is non-null and not both, an IllegalArgumentException is thrown.)
		 * @param username
		 * @param password
		 * @return this builder
		 */
		public ConsumerBuilder authenticatedByHttp ( String username, String password ) { fUsername = username; fPassword = password; return this; }

		/**
		 * Set the server side timeout
		 * @param timeoutMs	The amount of time in milliseconds that the server should keep the connection open while waiting for message traffic.
		 * @return this builder
		 */
		public ConsumerBuilder waitAtServer ( int timeoutMs ) { fTimeoutMs = timeoutMs; return this; };

		/**
		 * Set the maximum number of messages to receive per transaction
		 * @param limit The maximum number of messages to receive from the server in one transaction.
		 * @return this builder
		 */
		public ConsumerBuilder receivingAtMost ( int limit ) { fLimit = limit; return this; };

		/**
		 * Set a filter to use on the server
		 * @param filter a Highland Park standard library filter encoded in JSON
		 * @return this builder
		 */
		public ConsumerBuilder withServerSideFilter ( String filter ) { fFilter = filter; return this; }

		/**
		 * Set the socket read timeout for this client
		 * @param soTimeoutMs the read timeout for this client
		 * @return this builder
		 */
		public ConsumerBuilder withSocketTimeout ( int soTimeoutMs ) { fSoTimeoutMs = soTimeoutMs; return this; };
		
		/**
		 * Report metrics to the given registry with the given name prefix.
		 * @param registry
		 * @param registryEntryPrefix Use null for no prefix.
		 * @return this builder
		 */
		public ConsumerBuilder reportingMetricsTo ( CdmMetricsRegistry registry, String registryEntryPrefix )
		{
			fMetrics = registry;
			fMetricsNamePrefix = registryEntryPrefix;
			return this;
		};

		/**
		 * Build the consumer
		 * @return a consumer
		 * @throws GeneralSecurityException 
		 * @throws MalformedURLException 
		 */
		public CambriaConsumer build () throws MalformedURLException, GeneralSecurityException
		{
			if ( fHosts == null || fHosts.size() == 0 || fTopic == null )
			{
				throw new IllegalArgumentException ( "You must provide at least one host and a topic name." );
			}

			if ( fGroup == null )
			{
				fGroup = UUID.randomUUID ().toString ();
				fId = "0";
				log.info ( "Creating non-restartable client with group " + fGroup + " and ID " + fId + "." );
			}

			if ( sfConsumerMock != null ) return sfConsumerMock;
			final CambriaConsumerImpl client = new CambriaConsumerImpl ( fConnectionType, fHosts, fTopic, fGroup, fId, fTimeoutMs, fLimit, fFilter, fApiKey, fApiSecret, fSoTimeoutMs );
			if ( fUsername != null && fPassword != null )
			{
				client.setHttpBasicCredentials ( fUsername, fPassword );
			}
			else if ( fUsername != null || fPassword != null )
			{
				throw new IllegalArgumentException ( "One of Basic Auth username or password is null." );
			}

			if ( fMetrics != null )
			{
				client.sendMetricsTo ( fMetrics, fMetricsNamePrefix );
			}
			return client;
		}

		private Collection<String> fHosts = null;
		private ConnectionType fConnectionType = ConnectionType.HTTP;
		private String fTopic = null;
		private String fGroup = null;
		private String fId = null;
		private String fApiKey = null;
		private String fApiSecret = null;
		private String fUsername = null;
		private String fPassword = null;
		private int fTimeoutMs = -1;
		private int fLimit = -1;
		private int fSoTimeoutMs = HttpClient.kDefault_SocketTimeoutMs; 
		private String fFilter = null;
		private CdmMetricsRegistry fMetrics = null;
		private String fMetricsNamePrefix = null;
	}

	/*************************************************************************/
	/*************************************************************************/
	/*************************************************************************/

	/**
	 * A publisher builder
	 */
	public static class PublisherBuilder
	{
		public PublisherBuilder () {}

		/**
		 * Set the Cambria/UEB host(s) to use
		 * @param hostlist The host(s) used in the URL to Cambria. Can be "host:port", can be multiple comma-separated entries.
		 * @return this builder
		 */
		public PublisherBuilder usingHosts ( String hostlist ) { return usingHosts ( CambriaConsumerImpl.stringToList(hostlist) ); }

		/**
		 * Set the Cambria/UEB host(s) to use
		 * @param hostSet The host(s) used in the URL to Cambria. Can be "host:port"
		 * @return this builder
		 */
		public PublisherBuilder usingHosts ( String[] hostSet )
		{
			final TreeSet<String> hosts = new TreeSet<String> ();
			for ( String hp : hostSet )
			{
				hosts.add ( hp );
			}
			return usingHosts ( hosts );
		}

		/**
		 * Set the Cambria/UEB host(s) to use
		 * @param hostlist The host(s) used in the URL to Cambria. Can be "host:port".
		 * @return this builder
		 */
		public PublisherBuilder usingHosts ( Collection<String> hostlist ) { fHosts=hostlist; return this; }

		/**
		 * Set the topic to publish on
		 * @param topic The topic on which to publish messages.
		 * @return this builder
		 */
		public PublisherBuilder onTopic ( String topic ) { fTopic = topic; return this; }

		/**
		 * Use https to connect to Cambria
		 * @return this builder
		 */
		public PublisherBuilder usingHttps () { return usingHttps ( true ); }

		/**
		 * Use https to connect to Cambria
		 * @return this builder
		 */
		public PublisherBuilder usingHttps ( boolean useHttps )
		{
			return withConnectionType ( useHttps ? ConnectionType.HTTPS : ConnectionType.HTTP );
		}

		/**
		 * Specify the HTTP connection type.
		 * @param ct a connection type
		 * @return this builder
		 */
		public PublisherBuilder withConnectionType ( ConnectionType ct )
		{
			fConnectionType = ct;
			return this;
		}
		
		/**
		 * Batch message sends with the given limits.
		 * @param messageCount The largest set of messages to batch.
		 * @param ageInMs The maximum age of a message waiting in a batch.
		 * @return this builder
		 */
		public PublisherBuilder limitBatch ( int messageCount, int ageInMs ) { fMaxBatchSize = messageCount; fMaxBatchAgeMs = ageInMs; return this; }

		/**
		 * Compress transactions
		 * @return this builder
		 */
		public PublisherBuilder withCompresion () { return enableCompresion(true); }

		/**
		 * Do not compress transactions
		 * @return this builder
		 */
		public PublisherBuilder withoutCompresion () { return enableCompresion(false); }

		/**
		 * Set the compression option
		 * @param compress true to gzip compress transactions
		 * @return this builder
		 */
		public PublisherBuilder enableCompresion ( boolean compress ) { fCompress = compress; return this; }

		/**
		 * Set the API key and secret for this client.
		 * @param apiKey
		 * @param apiSecret
		 * @return this builder
		 */
		public PublisherBuilder authenticatedBy ( String apiKey, String apiSecret ) { fApiKey = apiKey; fApiSecret = apiSecret; return this; }

		/**
		 * Set the HTTP Basic auth for this client.
		 * @param username
		 * @param password
		 * @return this builder
		 */
		public PublisherBuilder authenticatedByHttp ( String username, String password ) { fUsername = username; fPassword = password; return this; }


		/**
		 * Set the number of consecutive publish failures before the publisher logs an error that a log
		 * monitoring system should watch for.
		 * 
		 * @param threshold
		 * @return this builder
		 */
		public PublisherBuilder logSendFailuresAfter ( int threshold ) { fFailLogThreshold = threshold; return this; }

		/**
		 * provide a specific logger
		 * @param log
		 * @return this builder
		 */
		public PublisherBuilder logTo ( Logger log )
		{
			fLog = log;
			return this;
		}
		/**
		 * Set the socket read timeout for this client
		 * @param soTimeoutMs the read timeout for this client
		 * @return this builder
		 */
		public PublisherBuilder withSocketTimeout ( int soTimeoutMs ) { fSoTimeoutMs = soTimeoutMs; return this; };

		/**
		
		/**
		 * Report metrics to the given registry with the given name prefix.
		 * @param registry
		 * @param registryEntryPrefix Use null for no prefix.
		 * @return this builder
		 */
		public PublisherBuilder reportingMetricsTo ( CdmMetricsRegistry registry, String registryEntryPrefix )
		{
			fMetrics = registry;
			fMetricsNamePrefix = registryEntryPrefix;
			return this;
		};

		/**
		 * Build the publisher
		 * @return a batching publisher
		 * @throws GeneralSecurityException 
		 * @throws MalformedURLException 
		 */
		public CambriaBatchingPublisher build () throws MalformedURLException, GeneralSecurityException
		{
			if ( fHosts == null || fHosts.size() == 0 || fTopic == null )
			{
				throw new IllegalArgumentException ( "You must provide at least one host and a topic name." );
			}

			if ( sfPublisherMock != null ) return sfPublisherMock;

			final CambriaSimplerBatchPublisher pub = new CambriaSimplerBatchPublisher.Builder ()
				.againstUrls ( fHosts )
				.withConnectionType ( fConnectionType )
				.onTopic ( fTopic )
				.batchTo ( fMaxBatchSize, fMaxBatchAgeMs )
				.compress ( fCompress )
				.logSendFailuresAfter ( fFailLogThreshold )
				.logTo ( fLog )
				.timeoutSocketAfter ( fSoTimeoutMs )
				.metricsTo ( fMetrics, fMetricsNamePrefix )
				.build ();
			if ( fApiKey != null && fApiKey.length () > 0 )
			{
				pub.setApiCredentials ( fApiKey, fApiSecret );
			}
			if ( (fUsername != null && fUsername.length () > 0)|| fPassword != null )
			{
				pub.setHttpBasicCredentials ( fUsername, fPassword );
			}
			return pub;
		}
		
		private Collection<String> fHosts = null;
		private String fTopic = null;
		private int fMaxBatchSize = 1;
		private int fMaxBatchAgeMs = 1;
		private boolean fCompress = false;
		private int fFailLogThreshold = 10;
		private String fApiKey = null;
		private String fApiSecret = null;
		private String fUsername = null;
		private String fPassword = null;
		private ConnectionType fConnectionType = ConnectionType.HTTP;
		private Logger fLog = null;
		private CdmMetricsRegistry fMetrics = null;
		private String fMetricsNamePrefix = null;
		private int fSoTimeoutMs = HttpClient.kDefault_SocketTimeoutMs; 
	}

	/**
	 * A builder for an identity manager
	 */
	public static class IdentityManagerBuilder extends AbstractAuthenticatedManagerBuilder<CambriaIdentityManager>
	{
		/**
		 * Construct an identity manager builder.
		 */
		public IdentityManagerBuilder () {}

		@Override
		protected CambriaIdentityManager constructClient ( ConnectionType ct, Collection<String> hosts )
			throws MalformedURLException, GeneralSecurityException
		{
			return new CambriaMetaClient ( ct, hosts );
		}
	}

	/**
	 * A builder for a topic manager
	 */
	public static class TopicManagerBuilder extends AbstractAuthenticatedManagerBuilder<CambriaTopicManager>
	{
		/**
		 * Construct an topic manager builder.
		 */
		public TopicManagerBuilder () {}

		@Override
		protected CambriaTopicManager constructClient ( ConnectionType ct, Collection<String> hosts )
			throws MalformedURLException, GeneralSecurityException
		{
			return new CambriaMetaClient ( ct, hosts );
		}
	}

	/**
	 * Inject a consumer. Used to support unit tests.
	 * @param cc
	 */
	public static void $testInject ( CambriaConsumer cc )
	{
		sfConsumerMock = cc;
	}

	/**
	 * Inject a publisher. Used to support unit tests.
	 * @param pub
	 */
	public static void $testInject ( CambriaBatchingPublisher pub )
	{
		sfPublisherMock = pub;
	}

	static CambriaConsumer sfConsumerMock = null;
	static CambriaBatchingPublisher sfPublisherMock = null;

	/**
	 * A builder for an identity manager
	 */
	public static abstract class AbstractAuthenticatedManagerBuilder<T extends CambriaClient>
	{
		/**
		 * Construct an identity manager builder.
		 */
		public AbstractAuthenticatedManagerBuilder () {}

		/**
		 * Set the host list
		 * @param hostList a comma-separated list of hosts to use to connect to Cambria
		 * @return this builder
		 */
		public AbstractAuthenticatedManagerBuilder<T> usingHosts ( String hostList ) { return usingHosts ( CambriaConsumerImpl.stringToList(hostList) ); }

		/**
		 * Set the host list
		 * @param hostSet a set of hosts to use to connect to Cambria
		 * @return this builder
		 */
		public AbstractAuthenticatedManagerBuilder<T> usingHosts ( Collection<String> hostSet ) { fHosts = hostSet; return this; }

		/**
		 * Use https to connect to Cambria
		 * @return this builder
		 */
		public AbstractAuthenticatedManagerBuilder<T> usingHttps () { fConnectionType = ConnectionType.HTTPS; return this; }

		/**
		 * Allow this client to accept self-signed certificates from the Cambria API Server
		 * Don't use this unless you know what you are doing.
		 * @return this builder
		 */
		public AbstractAuthenticatedManagerBuilder<T> allowSelfSignedCertificates () { fConnectionType = ConnectionType.HTTPS_NO_VALIDATION; return this; }

		/**
		 * Set the API key and secret for this client.
		 * @param apiKey
		 * @param apiSecret
		 * @return this builder
		 */
		public AbstractAuthenticatedManagerBuilder<T> authenticatedBy ( String apiKey, String apiSecret ) { fApiKey = apiKey; fApiSecret = apiSecret; return this; }

		/**
		 * Set the HTTP Basic auth for this client.
		 * @param username
		 * @param password
		 * @return this builder
		 */
		public AbstractAuthenticatedManagerBuilder<T> authenticatedByHttp ( String username, String password ) { fUsername = username; fPassword = password; return this; }

		/**
		 * Build the consumer
		 * @return a consumer
		 * @throws GeneralSecurityException 
		 * @throws MalformedURLException 
		 */
		public T build () throws MalformedURLException, GeneralSecurityException
		{
			if ( fHosts == null || fHosts.size() == 0 )
			{
				throw new IllegalArgumentException ( "You must provide at least one host and a topic name." );
			}

			final T mgr = constructClient ( fConnectionType, fHosts );
			mgr.setApiCredentials ( fApiKey, fApiSecret );
			mgr.setHttpBasicCredentials ( fUsername, fPassword );
			return mgr;
		}

		protected abstract T constructClient ( ConnectionType ct, Collection<String> hosts ) throws MalformedURLException, GeneralSecurityException;

		private Collection<String> fHosts = null;
		private ConnectionType fConnectionType = ConnectionType.HTTP;
		private String fApiKey = null;
		private String fApiSecret = null;
		private String fUsername = null;
		private String fPassword = null;
	}

	private static final Logger log = LoggerFactory.getLogger ( CambriaClientBuilders.class );
}
