/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client;

import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.TreeSet;
import java.util.UUID;

import com.att.nsa.apiClient.http.HttpClient;
import com.att.nsa.apiClient.http.HttpClient.ConnectionType;
import com.att.nsa.cambria.client.impl.CambriaConsumerImpl;
import com.att.nsa.cambria.client.impl.CambriaMetaClient;
import com.att.nsa.cambria.client.impl.CambriaSimplerBatchPublisher;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

/**
 * A factory for Cambria clients.<br>
 * <br>
 * Use caution selecting a consumer creator factory. If the call doesn't accept a consumer group name, then it creates
 * a consumer that is not restartable. That is, if you stop your process and start it again, your client will NOT receive
 * any missed messages on the topic. If you need to ensure receipt of missed messages, then you must use a consumer that's
 * created with a group name and ID. (If you create multiple consumer processes using the same group, load is split across
 * them. Be sure to use a different ID for each instance.)<br>
 * <br>
 * Publishers  
 * 
 * @deprecated Use CambriaClientBuilders
 */
@Deprecated
public class CambriaClientFactory
{
	/**
	 * Create a consumer instance with the default timeout and no limit
	 * on messages returned. This consumer operates as an independent consumer (i.e., not in a group) and is NOT re-startable
	 * across sessions.
	 * 
	 * @param hostList A comma separated list of hosts to use to connect to Cambria.
	 * You can include port numbers (3904 is the default). For example, "ueb01hydc.it.att.com:8080,ueb02hydc.it.att.com"
	 * 
	 * @param topic The topic to consume
	 * 
	 * @return a consumer
	 * @throws GeneralSecurityException 
	 * @throws MalformedURLException 
	 */
	public static CambriaConsumer createConsumer ( ConnectionType ct, String hostList, String topic ) throws MalformedURLException, GeneralSecurityException
	{
		return createConsumer ( ct, CambriaConsumerImpl.stringToList(hostList), topic );
	}

	/**
	 * Create a consumer instance with the default timeout and no limit
	 * on messages returned. This consumer operates as an independent consumer (i.e., not in a group) and is NOT re-startable
	 * across sessions.
	 * 
	 * @param hostSet The host used in the URL to Cambria. Entries can be "host:port".
	 * @param topic The topic to consume
	 * 
	 * @return a consumer
	 * @throws GeneralSecurityException 
	 * @throws MalformedURLException 
	 */
	public static CambriaConsumer createConsumer ( ConnectionType ct, Collection<String> hostSet, String topic ) throws MalformedURLException, GeneralSecurityException
	{
		return createConsumer ( ct, hostSet, topic, null );
	}

	/**
	 * Create a consumer instance with server-side filtering, the default timeout, and no limit
	 * on messages returned. This consumer operates as an independent consumer (i.e., not in a group) and is NOT re-startable
	 * across sessions.
	 * 
	 * @param hostSet The host used in the URL to Cambria. Entries can be "host:port".
	 * @param topic The topic to consume
	 * @param filter a filter to use on the server side
	 * 
	 * @return a consumer
	 * @throws GeneralSecurityException 
	 * @throws MalformedURLException 
	 */
	public static CambriaConsumer createConsumer ( ConnectionType ct, Collection<String> hostSet,
		String topic, String filter ) throws MalformedURLException, GeneralSecurityException
	{
		return createConsumer ( ct, hostSet, topic, UUID.randomUUID ().toString (), "0", -1, -1, filter, null, null );
	}

	/**
	 * Create a consumer instance with the default timeout, and no limit
	 * on messages returned. This consumer can operate in a logical group and is re-startable
	 * across sessions when you use the same group and ID on restart.
	 * 
	 * @param hostSet The host used in the URL to Cambria. Entries can be "host:port".
	 * @param topic The topic to consume
	 * @param consumerGroup The name of the consumer group this consumer is part of
	 * @param consumerId The unique id of this consume in its group
	 * 
	 * @return a consumer
	 * @throws GeneralSecurityException 
	 * @throws MalformedURLException 
	 */
	public static CambriaConsumer createConsumer ( ConnectionType ct, Collection<String> hostSet, final String topic,
		final String consumerGroup, final String consumerId ) throws MalformedURLException, GeneralSecurityException
	{
		return createConsumer ( ct, hostSet, topic, consumerGroup, consumerId, -1, -1 );
	}

	/**
	 * Create a consumer instance with the default timeout, and no limit
	 * on messages returned. This consumer can operate in a logical group and is re-startable
	 * across sessions when you use the same group and ID on restart.
	 * 
	 * @param hostSet The host used in the URL to Cambria. Entries can be "host:port".
	 * @param topic The topic to consume
	 * @param consumerGroup The name of the consumer group this consumer is part of
	 * @param consumerId The unique id of this consume in its group
	 * @param timeoutMs	The amount of time in milliseconds that the server should keep the connection open while waiting for message traffic. Use -1 for default timeout.
	 * @param limit A limit on the number of messages returned in a single call. Use -1 for no limit.
	 * 
	 * @return a consumer
	 * @throws GeneralSecurityException 
	 * @throws MalformedURLException 
	 */
	public static CambriaConsumer createConsumer ( ConnectionType ct, Collection<String> hostSet, final String topic,
		final String consumerGroup, final String consumerId, int timeoutMs, int limit) throws MalformedURLException, GeneralSecurityException
	{
		return createConsumer ( ct, hostSet, topic, consumerGroup, consumerId, timeoutMs, limit, null, null, null );
	}

	/**
	 * Create a consumer instance with the default timeout, and no limit
	 * on messages returned. This consumer can operate in a logical group and is re-startable
	 * across sessions when you use the same group and ID on restart. This consumer also uses
	 * server-side filtering.
	 * 
	 * @param hostList A comma separated list of hosts to use to connect to Cambria.
	 * You can include port numbers (3904 is the default). For example, "ueb01hydc.it.att.com:8080,ueb02hydc.it.att.com"
	 * @param topic The topic to consume
	 * @param consumerGroup The name of the consumer group this consumer is part of
	 * @param consumerId The unique id of this consume in its group
	 * @param timeoutMs	The amount of time in milliseconds that the server should keep the connection open while waiting for message traffic. Use -1 for default timeout.
	 * @param limit A limit on the number of messages returned in a single call. Use -1 for no limit.
	 * @param filter A Highland Park filter expression using only built-in filter components. Use null for "no filter".
	 * 
	 * @return a consumer
	 * @throws GeneralSecurityException 
	 * @throws MalformedURLException 
	 */
	public static CambriaConsumer createConsumer ( ConnectionType ct, String hostList, final String topic, final String consumerGroup,
		final String consumerId, int timeoutMs, int limit, String filter, String apiKey, String apiSecret ) throws MalformedURLException, GeneralSecurityException
	{
		return createConsumer ( ct, CambriaConsumerImpl.stringToList(hostList), topic, consumerGroup,
			consumerId, timeoutMs, limit, filter, apiKey, apiSecret );
	}

	/**
	 * Create a consumer instance with the default timeout, and no limit
	 * on messages returned. This consumer can operate in a logical group and is re-startable
	 * across sessions when you use the same group and ID on restart. This consumer also uses
	 * server-side filtering.
	 * 
	 * @param hostSet The host used in the URL to Cambria. Entries can be "host:port".
	 * @param topic The topic to consume
	 * @param consumerGroup The name of the consumer group this consumer is part of
	 * @param consumerId The unique id of this consume in its group
	 * @param timeoutMs	The amount of time in milliseconds that the server should keep the connection open while waiting for message traffic. Use -1 for default timeout.
	 * @param limit A limit on the number of messages returned in a single call. Use -1 for no limit.
	 * @param filter A Highland Park filter expression using only built-in filter components. Use null for "no filter".
	 * 
	 * @return a consumer
	 * @throws GeneralSecurityException 
	 * @throws MalformedURLException 
	 */
	public static CambriaConsumer createConsumer ( ConnectionType ct, Collection<String> hostSet, final String topic, final String consumerGroup,
		final String consumerId, int timeoutMs, int limit, String filter, String apiKey, String apiSecret ) throws MalformedURLException, GeneralSecurityException
	{
		if ( CambriaClientBuilders.sfConsumerMock != null ) return CambriaClientBuilders.sfConsumerMock;
		return new CambriaConsumerImpl ( ct, hostSet, topic, consumerGroup, consumerId, timeoutMs, limit, filter, apiKey, apiSecret,
			HttpClient.kDefault_SocketTimeoutMs );
	}

	/*************************************************************************/
	/*************************************************************************/
	/*************************************************************************/
	
	/**
	 * Create a publisher that sends each message (or group of messages) immediately. Most
	 * applications should favor higher latency for much higher message throughput and the
	 * "simple publisher" is not a good choice. 
	 *  
	 * @param hostlist The host used in the URL to Cambria. Can be "host:port", can be multiple comma-separated entries.
	 * @param topic The topic on which to publish messages.
	 * @return a publisher
	 * @throws GeneralSecurityException 
	 * @throws MalformedURLException 
	 */
	public static CambriaBatchingPublisher createSimplePublisher ( ConnectionType ct, String hostlist, String topic ) throws MalformedURLException, GeneralSecurityException
	{
		return createBatchingPublisher ( ct, hostlist, topic, 1, 1 );
	}

	/**
	 * Create a publisher that batches messages. Be sure to close the publisher to
	 * send the last batch and ensure a clean shutdown. Message payloads are not compressed.
	 * 
	 * @param hostlist The host used in the URL to Cambria. Can be "host:port", can be multiple comma-separated entries.
	 * @param topic The topic on which to publish messages.
	 * @param maxBatchSize The largest set of messages to batch
	 * @param maxAgeMs The maximum age of a message waiting in a batch
	 * 
	 * @return a publisher
	 * @throws GeneralSecurityException 
	 * @throws MalformedURLException 
	 */
	public static CambriaBatchingPublisher createBatchingPublisher ( ConnectionType ct, String hostlist, String topic, int maxBatchSize, long maxAgeMs ) throws MalformedURLException, GeneralSecurityException
	{
		return createBatchingPublisher ( ct, hostlist, topic, maxBatchSize, maxAgeMs, false );
	}

	/**
	 * Create a publisher that batches messages. Be sure to close the publisher to
	 * send the last batch and ensure a clean shutdown. 
	 * 
	 * @param hostlist The host used in the URL to Cambria. Can be "host:port", can be multiple comma-separated entries.
	 * @param topic The topic on which to publish messages.
	 * @param maxBatchSize The largest set of messages to batch
	 * @param maxAgeMs The maximum age of a message waiting in a batch
	 * @param compress use gzip compression
	 * 
	 * @return a publisher
	 * @throws GeneralSecurityException 
	 * @throws MalformedURLException 
	 */
	public static CambriaBatchingPublisher createBatchingPublisher ( ConnectionType ct, String hostlist, String topic, int maxBatchSize, long maxAgeMs, boolean compress ) throws MalformedURLException, GeneralSecurityException
	{
		return createBatchingPublisher ( ct, CambriaConsumerImpl.stringToList(hostlist), topic, maxBatchSize, maxAgeMs, compress );
	}

	/**
	 * Create a publisher that batches messages. Be sure to close the publisher to
	 * send the last batch and ensure a clean shutdown. 
	 * 
	 * @param hostSet A set of hosts to be used in the URL to Cambria. Can be "host:port". Use multiple entries to enable failover.
	 * @param topic The topic on which to publish messages.
	 * @param maxBatchSize The largest set of messages to batch
	 * @param maxAgeMs The maximum age of a message waiting in a batch
	 * @param compress use gzip compression
	 * 
	 * @return a publisher
	 * @throws GeneralSecurityException 
	 * @throws MalformedURLException 
	 */
	public static CambriaBatchingPublisher createBatchingPublisher ( ConnectionType ct, String[] hostSet, String topic, int maxBatchSize, long maxAgeMs, boolean compress ) throws MalformedURLException, GeneralSecurityException
	{
		final TreeSet<String> hosts = new TreeSet<String> ();
		for ( String hp : hostSet )
		{
			hosts.add ( hp );
		}
		return createBatchingPublisher ( ct, hosts, topic, maxBatchSize, maxAgeMs, compress );
	}

	/**
	 * Create a publisher that batches messages. Be sure to close the publisher to
	 * send the last batch and ensure a clean shutdown. 
	 * 
	 * @param hostSet A set of hosts to be used in the URL to Cambria. Can be "host:port". Use multiple entries to enable failover.
	 * @param topic The topic on which to publish messages.
	 * @param maxBatchSize The largest set of messages to batch
	 * @param maxAgeMs The maximum age of a message waiting in a batch
	 * @param compress use gzip compression
	 * 
	 * @return a publisher
	 * @throws GeneralSecurityException 
	 * @throws MalformedURLException 
	 */
	public static CambriaBatchingPublisher createBatchingPublisher ( ConnectionType ct, Collection<String> hostSet, String topic, int maxBatchSize, long maxAgeMs, boolean compress ) throws MalformedURLException, GeneralSecurityException
	{
		return new CambriaSimplerBatchPublisher.Builder ().
			againstUrls ( hostSet ).
			withConnectionType ( ct ).
			onTopic ( topic ).
			batchTo ( maxBatchSize, maxAgeMs ).
			compress ( compress ).
			build ();
	}

	
	/**
	 * Create an identity manager client to work with API keys.
	 * @param hostSet A set of hosts to be used in the URL to Cambria. Can be "host:port". Use multiple entries to enable failover.
	 * @param apiKey Your API key
	 * @param apiSecret Your API secret
	 * @return an identity manager
	 * @throws GeneralSecurityException 
	 * @throws MalformedURLException 
	 */
	public static CambriaIdentityManager createIdentityManager ( ConnectionType ct, Collection<String> hostSet, String apiKey, String apiSecret ) throws MalformedURLException, GeneralSecurityException
	{
		final CambriaIdentityManager cim = new CambriaMetaClient ( ct, hostSet );
		cim.setApiCredentials ( apiKey, apiSecret );
		return cim;
	}

	/**
	 * Create a topic manager for working with topics.
	 * @param hostSet A set of hosts to be used in the URL to Cambria. Can be "host:port". Use multiple entries to enable failover.
	 * @param apiKey Your API key
	 * @param apiSecret Your API secret
	 * @return a topic manager
	 * @throws GeneralSecurityException 
	 * @throws MalformedURLException 
	 */
	public static CambriaTopicManager createTopicManager ( ConnectionType ct, Collection<String> hostSet, String apiKey, String apiSecret ) throws MalformedURLException, GeneralSecurityException
	{
		final CambriaMetaClient tmi = new CambriaMetaClient ( ct, hostSet );
		tmi.setApiCredentials ( apiKey, apiSecret );
		return tmi;
	}

	/**
	 * Inject a consumer. Used to support unit tests.
	 * @param cc
	 */
	public static void $testInject ( CambriaConsumer cc )
	{
		CambriaClientBuilders.sfConsumerMock = cc;
	}
}
