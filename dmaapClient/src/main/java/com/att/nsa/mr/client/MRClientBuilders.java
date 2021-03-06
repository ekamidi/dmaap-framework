/*******************************************************************************
 * BSD License
 *  
 * Copyright (c) 2016, AT&T Intellectual Property.  All other rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *  
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *    and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 * 3. All advertising materials mentioning features or use of this software must display the
 *    following acknowledgement:  This product includes software developed by the AT&T.
 * 4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 *******************************************************************************/
package com.att.nsa.mr.client;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.TreeSet;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.mr.client.impl.MRConsumerImpl;
import com.att.nsa.mr.client.impl.MRMetaClient;
import com.att.nsa.mr.client.impl.MRSimplerBatchPublisher;

/**
 * A collection of builders for various types of MR API clients
 * 
 * @author author
 */
public class MRClientBuilders
{
	/**
	 * A builder for a topic Consumer
	 * @author author
	 */
	public static class ConsumerBuilder
	{
		/**
		 * Construct a consumer builder.
		 */
		public ConsumerBuilder () {}

		/**
		 * Set the host list
		 * @param hostList a comma-separated list of hosts to use to connect to MR
		 * @return this builder
		 */
		public ConsumerBuilder usingHosts ( String hostList ) { return usingHosts ( MRConsumerImpl.stringToList(hostList) ); }

		/**
		 * Set the host list
		 * @param hostSet a set of hosts to use to connect to MR
		 * @return this builder
		 */
		public ConsumerBuilder usingHosts ( Collection<String> hostSet ) { fHosts = hostSet; return this; }

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
		 * Set the API key and secret for this client.
		 * @param apiKey
		 * @param apiSecret
		 * @return this builder
		 */
		public ConsumerBuilder authenticatedBy ( String apiKey, String apiSecret ) { fApiKey = apiKey; fApiSecret = apiSecret; return this; }

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
		 * Build the consumer
		 * @return a consumer
		 */
		public MRConsumer build ()
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
			try {
				return new MRConsumerImpl ( fHosts, fTopic, fGroup, fId, fTimeoutMs, fLimit, fFilter, fApiKey, fApiSecret );
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}

		private Collection<String> fHosts = null;
		private String fTopic = null;
		private String fGroup = null;
		private String fId = null;
		private String fApiKey = null;
		private String fApiSecret = null;
		private int fTimeoutMs = -1;
		private int fLimit = -1;
		private String fFilter = null;
	}

	/*************************************************************************/
	/*************************************************************************/
	/*************************************************************************/

	/**
	 * A publisher builder
	 * @author author
	 */
	public static class PublisherBuilder
	{
		public PublisherBuilder () {}

		/**
		 * Set the MR/UEB host(s) to use
		 * @param hostlist The host(s) used in the URL to MR. Can be "host:port", can be multiple comma-separated entries.
		 * @return this builder
		 */
		public PublisherBuilder usingHosts ( String hostlist ) { return usingHosts ( MRConsumerImpl.stringToList(hostlist) ); }

		/**
		 * Set the MR/UEB host(s) to use
		 * @param hostSet The host(s) used in the URL to MR. Can be "host:port"
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
		 * Set the MR/UEB host(s) to use
		 * @param hostlist The host(s) used in the URL to MR. Can be "host:port".
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
		 * Build the publisher
		 * @return a batching publisher
		 */
		public MRBatchingPublisher build ()
		{
			if ( fHosts == null || fHosts.size() == 0 || fTopic == null )
			{
				throw new IllegalArgumentException ( "You must provide at least one host and a topic name." );
			}

			if ( sfPublisherMock != null ) return sfPublisherMock;

			final MRSimplerBatchPublisher pub = new MRSimplerBatchPublisher.Builder ().
				againstUrls ( fHosts ).
				onTopic ( fTopic ).
				batchTo ( fMaxBatchSize, fMaxBatchAgeMs ).
				compress ( fCompress ).
				build ();
			if ( fApiKey != null )
			{
				pub.setApiCredentials ( fApiKey, fApiSecret );
			}
			return pub;
		}
		
		private Collection<String> fHosts = null;
		private String fTopic = null;
		private int fMaxBatchSize = 1;
		private int fMaxBatchAgeMs = 1;
		private boolean fCompress = false;
		private String fApiKey = null;
		private String fApiSecret = null;
	}

	/**
	 * A builder for an identity manager
	 * @author author
	 */
	public static class IdentityManagerBuilder extends AbstractAuthenticatedManagerBuilder<MRIdentityManager>
	{
		/**
		 * Construct an identity manager builder.
		 */
		public IdentityManagerBuilder () {}

		@Override
		protected MRIdentityManager constructClient ( Collection<String> hosts ) { try {
			return new MRMetaClient ( hosts );
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} }
	}

	/**
	 * A builder for a topic manager
	 * @author author
	 */
	public static class TopicManagerBuilder extends AbstractAuthenticatedManagerBuilder<MRTopicManager>
	{
		/**
		 * Construct an topic manager builder.
		 */
		public TopicManagerBuilder () {}

		@Override
		protected MRTopicManager constructClient ( Collection<String> hosts ) { try {
			return new MRMetaClient ( hosts );
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} }
	}

	/**
	 * Inject a consumer. Used to support unit tests.
	 * @param cc
	 */
	public static void $testInject ( MRConsumer cc )
	{
		sfConsumerMock = cc;
	}

	/**
	 * Inject a publisher. Used to support unit tests.
	 * @param pub
	 */
	public static void $testInject ( MRBatchingPublisher pub )
	{
		sfPublisherMock = pub;
	}

	static MRConsumer sfConsumerMock = null;
	static MRBatchingPublisher sfPublisherMock = null;

	/**
	 * A builder for an identity manager
	 * @author author
	 */
	public static abstract class AbstractAuthenticatedManagerBuilder<T extends MRClient>
	{
		/**
		 * Construct an identity manager builder.
		 */
		public AbstractAuthenticatedManagerBuilder () {}

		/**
		 * Set the host list
		 * @param hostList a comma-separated list of hosts to use to connect to MR
		 * @return this builder
		 */
		public AbstractAuthenticatedManagerBuilder<T> usingHosts ( String hostList ) { return usingHosts ( MRConsumerImpl.stringToList(hostList) ); }

		/**
		 * Set the host list
		 * @param hostSet a set of hosts to use to connect to MR
		 * @return this builder
		 */
		public AbstractAuthenticatedManagerBuilder<T> usingHosts ( Collection<String> hostSet ) { fHosts = hostSet; return this; }

		/**
		 * Set the API key and secret for this client.
		 * @param apiKey
		 * @param apiSecret
		 * @return this builder
		 */
		public AbstractAuthenticatedManagerBuilder<T> authenticatedBy ( String apiKey, String apiSecret ) { fApiKey = apiKey; fApiSecret = apiSecret; return this; }

		/**
		 * Build the consumer
		 * @return a consumer
		 */
		public T build ()
		{
			if ( fHosts == null || fHosts.size() == 0 )
			{
				throw new IllegalArgumentException ( "You must provide at least one host and a topic name." );
			}

			final T mgr = constructClient ( fHosts );
			mgr.setApiCredentials ( fApiKey, fApiSecret );
			return mgr;
		}

		protected abstract T constructClient ( Collection<String> hosts );

		private Collection<String> fHosts = null;
		private String fApiKey = null;
		private String fApiSecret = null;
	}

	private static final Logger log = LoggerFactory.getLogger ( MRClientBuilders.class );
}
