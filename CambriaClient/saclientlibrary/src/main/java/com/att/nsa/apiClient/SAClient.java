/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.apiClient.lb.LoadBalancingPolicy;
import com.att.nsa.apiClient.lb.NoHostAvailableException;
import com.att.nsa.apiClient.lb.StickyLoadBalancingPolicy;

public class SAClient {
	
	private static final Logger log = LoggerFactory.getLogger(SAClient.class);
	
	//Cache Settings
	private static final long DEFAULT_CACHE_TTL_IN_SECS = 60 * 5;
	private static final int DEFAULT_CACHE_MAX_ENTRIES = 100;
	private static final int DEFAULT_CACHE_MAX_OBJ_SIZE = 131072;
	
	//Connection Pool Settings
	private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 10;
	private static final int DEFAULT_MAX_PER_ROUTE = 10;
	private static final long DEFAULT_SWEEP_PERIOD_IN_SECS = 10;
	private static final long DEFAULT_CONN_TIMEOUT_IN_SECS = 30;
	
	//Request Settings
	private static final int DEFAULT_REQUEST_CONN_TIMEOUT_IN_MS = 30000;
	private static final int DEFAULT_REQUEST_SOCK_TIMEOUT_IN_MS = 30000;
	
	private final CloseableHttpClient fClient;
	private final LoadBalancingPolicy<URI, HttpHost> loadBalancingPolicy;
	private final ScheduledExecutorService fConnectionSweeper;
	
	public SAClient ( Collection<String> hosts, int stdSvcPort )
	{
		this ( createHostsList ( hosts, stdSvcPort ) );
	}

	public SAClient(Collection<HttpHost> hosts) {
		this.loadBalancingPolicy = new StickyLoadBalancingPolicy<URI, HttpHost> (hosts);

		final PoolingHttpClientConnectionManager fClientCm = new PoolingHttpClientConnectionManager();
		fClientCm.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
		fClientCm.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
		
		this.fClient = createDefaultClient(fClientCm);
		
		this.fConnectionSweeper = Executors.newSingleThreadScheduledExecutor();
		this.fConnectionSweeper.scheduleAtFixedRate(
				new SAConnectionMonitorThread(fClientCm, DEFAULT_CONN_TIMEOUT_IN_SECS, TimeUnit.SECONDS),
				DEFAULT_SWEEP_PERIOD_IN_SECS,
				DEFAULT_SWEEP_PERIOD_IN_SECS,
				TimeUnit.SECONDS
		);
	}
	
	public SAClient(LoadBalancingPolicy<URI, HttpHost> loadBalancingPolicy) {
		this.loadBalancingPolicy = loadBalancingPolicy;
		
		final PoolingHttpClientConnectionManager fClientCm = new PoolingHttpClientConnectionManager();
		fClientCm.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
		fClientCm.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
		
		this.fClient = createDefaultClient(fClientCm);
		
		this.fConnectionSweeper = Executors.newSingleThreadScheduledExecutor();
		this.fConnectionSweeper.scheduleAtFixedRate(
				new SAConnectionMonitorThread(fClientCm, DEFAULT_CONN_TIMEOUT_IN_SECS, TimeUnit.SECONDS),
				DEFAULT_SWEEP_PERIOD_IN_SECS,
				DEFAULT_SWEEP_PERIOD_IN_SECS,
				TimeUnit.SECONDS
		);
	}
	
	private CloseableHttpClient createDefaultClient(HttpClientConnectionManager fClientCm) {
		final CacheConfig cacheConfig = CacheConfig.custom()
				.setMaxCacheEntries(DEFAULT_CACHE_MAX_ENTRIES)
				.setMaxObjectSize(DEFAULT_CACHE_MAX_OBJ_SIZE)
				.setHeuristicCachingEnabled(true)
				.setHeuristicDefaultLifetime(DEFAULT_CACHE_TTL_IN_SECS)
				.build();
		
		final RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(DEFAULT_REQUEST_CONN_TIMEOUT_IN_MS)
				.setSocketTimeout(DEFAULT_REQUEST_SOCK_TIMEOUT_IN_MS)
				.build();
		
		return CachingHttpClients.custom()
				.setCacheConfig(cacheConfig)
				.setConnectionManager(fClientCm)
				.setDefaultRequestConfig(requestConfig)
		        .build();
	}
	
	/**
	 * Execute the given {@code request} on a host selected by the {@code loadBalancingPolicy}.
	 * Requests are retried until they succeed or the {@code loadBalancingPolicy} gives up trying
	 * to find a suitable host.
	 * @param request The HTTP request to be executed
	 * @return The HTTP response associated with this request
	 * @throws SAClientException If there was a problem executing the {@code request}
	 */
	public CloseableHttpResponse getResponse(HttpUriRequest request) throws SAClientException {
		
		for (;;) {
			HttpHost host;
			try {
				host = loadBalancingPolicy.select(request.getURI());
			} catch (NoHostAvailableException e1) {
				throw new SAClientException("Load balancing policy gave up trying to find a host: " + e1.getMessage(), e1);
			}
			
			try {
				final CloseableHttpResponse response = fClient.execute(host, request);
				
				if (!isSane(response))
					loadBalancingPolicy.onSuspend(host);
				
				return response;
			} catch (ClientProtocolException e) {
				throw new SAClientException("Non-recoverable error occurred in SAClient: " + e.getMessage(), e);
			} catch (IOException e) {
				log.warn("Failed to execute request on host " + host.getHostName() + ":" + host.getPort(), e);
				loadBalancingPolicy.onSuspend(host);
			}
		}
	}
	
	public void close() {
		loadBalancingPolicy.close();
		fConnectionSweeper.shutdown();
		
		try {
			if (!fConnectionSweeper.awaitTermination(2 * DEFAULT_SWEEP_PERIOD_IN_SECS, TimeUnit.SECONDS)) {
				log.warn("Timed out waiting for SAClient connection sweeper to close");
			}
		} catch (InterruptedException e) {
			return;
		}
	}
	
	private boolean isSane(CloseableHttpResponse response) {
		final StatusLine statusLine = response.getStatusLine();
		
		if (statusLine == null) {
			log.warn("Status Line of response was null");
			return false;
		}
		
		final int statusCode = statusLine.getStatusCode();
		
		if (statusCode >= 100 && statusCode < 200) {
			log.warn("Response unexpectedly contained informational status code (1XX)");
		} else if (statusCode >= 200 && statusCode < 300) {
			log.trace("Response contained successful status code (2XX)");
		} else if (statusCode >= 300 && statusCode < 400) {
			log.warn("Response unexpectedly contained redirection status code (3XX)");
		} else if (statusCode >= 400 && statusCode < 500) {
			log.trace("Response contained client error status code (4XX)");
		} else if (statusCode >= 500 && statusCode < 600) {
			log.error("Response contained server error status code (5XX)");
			return false;
		} else {
			log.error("Response contained unrecognized status code " + statusCode);
			return false;
		}
		
		return true;
	}

	/**
	 * Create a list of HttpHosts from an input list of strings. Input strings have
	 * host[:port] as format. If the port section is not provided, the default port is used.
	 * 
	 * @param hosts
	 * @return a list of hosts
	 */
	private static List<HttpHost> createHostsList(Collection<String> hosts, int stdSvcPort)
	{
		final ArrayList<HttpHost> convertedHosts = new ArrayList<HttpHost> ();
		for ( String host : hosts )
		{
			if ( host.length () == 0 ) continue;
			convertedHosts.add ( hostForString ( host, stdSvcPort ) );
		}
		return convertedHosts;
	}

	/**
	 * Return an HttpHost from an input string. Input string has
	 * host[:port] as format. If the port section is not provided, the default port is used.
	 * 
	 * @param hosts
	 * @return a list of hosts
	 */
	public static HttpHost hostForString ( String host, int stdSvcPort )
	{
		if ( host.length() < 1 ) throw new IllegalArgumentException ( "An empty host entry is invalid." );
		
		String hostPart = host;
		int port = stdSvcPort;

		final int colon = host.indexOf ( ':' );
		if ( colon == 0 ) throw new IllegalArgumentException ( "Host entry '" + host + "' is invalid." );
		if ( colon > 0 )
		{
			hostPart = host.substring ( 0, colon ).trim();

			final String portPart = host.substring ( colon + 1 ).trim();
			if ( portPart.length () > 0 )
			{
				try
				{
					port = Integer.parseInt ( portPart );
				}
				catch ( NumberFormatException x )
				{
					throw new IllegalArgumentException ( "Host entry '" + host + "' is invalid.", x );
				}
			}
			// else: use default port on "foo:"
		}

		return new HttpHost ( hostPart, port );
	}
}
