/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.StatusLine;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.apiClient.credentials.ApiCredential;

/**
 * A client for typical server interaction.
 *
 */
@NotThreadSafe
public class HttpClient
{
	public enum ConnectionType
	{
		/**
		 * Plain HTTP connections
		 */
		HTTP,

		/**
		 * HTTPS connections
		 */
		HTTPS,
		
		/**
		 * Not recommended for production use. This mode is https but without server
		 * certificate validation.
		 */
		HTTPS_NO_VALIDATION
	}

	/**
	 * Construct an HTTP client for the given cluster with a standard service port to use
	 * when the hosts don't specify a port. This client will not use a cache by default.
	 * 
	 * @param cluster a set of hosts to use
	 * @param stdSvcPort the default port for this service if none is provided on a host entry
	 * @throws MalformedURLException 
	 * @throws GeneralSecurityException 
	 */
	public HttpClient ( ConnectionType ct, Collection<String> cluster, int stdSvcPort ) throws MalformedURLException
	{
		this ( ct, cluster, stdSvcPort, null );
	}

	/**
	 * Construct an HTTP client for a given cluster with a standard service port to use when
	 * the hosts don't specify a port and a client signature that identifies this client
	 * across runs.
	 * @param cluster
	 * @param stdSvcPort the default port for this service if none is provided on a host entry
	 * @param clientSignature an optional string that identifies this client for consistent host selection across restarts
	 * @throws MalformedURLException 
	 * @throws GeneralSecurityException 
	 */
	public HttpClient ( ConnectionType ct, Collection<String> cluster, int stdSvcPort, String clientSignature ) throws MalformedURLException
	{
		this ( ct, cluster, stdSvcPort, clientSignature, CacheUse.NONE, 1, 1, TimeUnit.MILLISECONDS, kDefault_PoolMaxInTotal, kDefault_PoolMaxPerRoute, kDefault_SocketTimeoutMs  );
	}

	/**
	 * Construct an HTTP client for the given cluster with a standard service port to use
	 * when the hosts don't specify a port. This client will use the the caching strategy
	 * provided by default with size and time limits as given.
	 * 
	 * @param cluster
	 * @param stdSvcPort the default port for this service if none is provided on a host entry
	 * @param clientSignature an optional string that identifies this client for consistent host selection
	 * @param defCacheUse
	 * @param maxObjCacheSize
	 * @param maxObjCacheTime
	 * @param maxObjCacheTimeUnit
	 * @throws MalformedURLException 
	 */
	public HttpClient ( ConnectionType ct, Collection<String> cluster, int stdSvcPort, String clientSignature,
		CacheUse defCacheUse, int maxObjCacheSize, long maxObjCacheTime, TimeUnit maxObjCacheTimeUnit  ) throws MalformedURLException
	{
		this ( ct, cluster, stdSvcPort, clientSignature, defCacheUse, maxObjCacheSize,
			maxObjCacheTime, maxObjCacheTimeUnit, kDefault_PoolMaxInTotal, kDefault_PoolMaxPerRoute, kDefault_SocketTimeoutMs );
	}

	/**
	 * Construct an HTTP client for the given cluster with a standard service port to use
	 * when the hosts don't specify a port. This client will use the the caching strategy
	 * provided by default with size and time limits as given.
	 * 
	 * @param cluster
	 * @param stdSvcPort the default port for this service if none is provided on a host entry
	 * @param clientSignature an optional string that identifies this client for consistent host selection
	 * @param defCacheUse
	 * @param maxObjCacheSize
	 * @param maxObjCacheTime
	 * @param maxObjCacheTimeUnit
	 * @param maxPoolSize for HTTP client
	 * @param maxPoolPerRoute for HTTP client
	 * @param soTimeoutMs how long to wait for bytes on a socket
	 * @throws MalformedURLException 
	 */
	public HttpClient ( ConnectionType ct, Collection<String> cluster, int stdSvcPort, String clientSignature,
		CacheUse defCacheUse, int maxObjCacheSize, long maxObjCacheTime, TimeUnit maxObjCacheTimeUnit,
		int maxPoolSize, int maxPoolPerRoute, int soTimeoutMs ) throws MalformedURLException
	{
		// fix up the hostlist to have full protocol + host + port entries up front so we don't
		// have to keep that information around and repeatedly build it.
		final TreeSet<String> selectorEntries = new TreeSet<String> ();
		for ( String host : cluster )
		{
			selectorEntries.add ( makeHostSpec ( ct, host, stdSvcPort ) );
		}

		fHostSelector = new HostSelector ( selectorEntries, clientSignature );

		fApiCreds = null;
		fHttpUsername = null;
		fHttpPassword = null;

		fLog = LoggerFactory.getLogger ( HttpClient.class );


		final RegistryBuilder<ConnectionSocketFactory> rb = RegistryBuilder.<ConnectionSocketFactory>create();
		
		// HTTP connector
		final ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
		rb.register("http", plainsf);

		// HTTPS connector
		ConnectionSocketFactory httpsCsf = null;
		if ( ct == ConnectionType.HTTPS_NO_VALIDATION )
		{
			try
			{
				final SSLContext ctx = SSLContexts
					.custom ()
					.loadTrustMaterial ( new TrustSelfSignedStrategy () )
					.build ();
				httpsCsf = new SSLConnectionSocketFactory ( ctx, NoopHostnameVerifier.INSTANCE );
			}
			catch ( KeyManagementException e )
			{
				throw new RuntimeException ( "Couldn't set up non-validating HTTPS client: " + e.getMessage (), e );
			}
			catch ( NoSuchAlgorithmException e )
			{
				throw new RuntimeException ( "Couldn't set up non-validating HTTPS client: " + e.getMessage (), e );
			}
			catch ( KeyStoreException e )
			{
				throw new RuntimeException ( "Couldn't set up non-validating HTTPS client: " + e.getMessage (), e );
			}
		}
		else
		{
			httpsCsf = SSLConnectionSocketFactory.getSocketFactory ();
		}
		rb.register("https", httpsCsf);

		// setup the connection manager
		final SocketConfig sc = SocketConfig
			.custom()
			.setSoTimeout ( soTimeoutMs )
			.build()
		;
		final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(rb.build());
		cm.setMaxTotal ( maxPoolSize );
		cm.setDefaultMaxPerRoute ( maxPoolPerRoute );
		cm.setDefaultSocketConfig ( sc );

		fClient = HttpClients.custom()
		        .setConnectionManager(cm)
		        .build();

		fObjectCache = new EntityLruCache<JSONObject> ( maxObjCacheSize,
			maxObjCacheTime, maxObjCacheTimeUnit );
		fDefCacheUse = defCacheUse;
	}

	static String makeHostSpec ( ConnectionType ct, String host, int defPort ) throws MalformedURLException
	{
		if ( host == null || host.length () == 0 ) throw new MalformedURLException ( "No host provided." );
		if ( defPort < 1 ) throw new MalformedURLException ( "Default port must be a positive integer." );

		final StringBuilder sb = new StringBuilder ();

		final int protoSeparator = host.indexOf ( "://" );

		if ( ct == null || ct == ConnectionType.HTTP )
		{
			if ( !host.startsWith ( "http://" ) )
			{
				if ( protoSeparator > -1 )
				{
					// protocol doesn't match
					throw new MalformedURLException ( "The connection type (http) doesn't match the protocol provided for " + host );
				}
				sb.append ( "http://" );
			}
		}
		else // HTTPS/HTTPS_NO_VALIDATION
		{
			if ( !host.startsWith ( "https://" ) )
			{
				if ( protoSeparator > -1 )
				{
					// protocol doesn't match
					throw new MalformedURLException ( "The connection type (https) doesn't match the protocol provided for " + host );
				}
				sb.append ( "https://" );
			}
		}

		// add the host (which may already have a protocol prefix)
		sb.append ( host );

		// check for a port specification
		String remains = protoSeparator > -1 ? host.substring ( protoSeparator + 3 ) : host;
		if ( !remains.contains ( ":" ) )
		{
			sb.append ( ":" + defPort );
		}

		return sb.toString ();
	}

	/**
	 * Set credentials for any following transactions
	 * @param apiKey
	 * @param apiSecret
	 */
	public void setApiCredentials ( String apiKey, String apiSecret )
	{
		if ( apiKey != null && apiSecret != null )
		{
			fApiCreds = new ApiCredential ( apiKey, apiSecret );
		}
		else if ( apiKey != null && apiSecret == null )
		{
			throw new IllegalArgumentException ( "API key provided without API secret" );
		}
		else
		{
			clearApiCredentials ();
		}
	}

	/**
	 * Create the API credentials so that any following transactions do not authenticate.
	 */
	public void clearApiCredentials ()
	{
		fApiCreds = null;
	}

	/**
	 * Set HTTP basic auth credentials for the transaction.
	 * @param username
	 * @param password
	 */
	public void setHttpBasicCredentials ( String username, String password )
	{
		fHttpUsername = username;
		fHttpPassword = password;
	}

	/**
	 * Clear the HTTP basic auth credentials for the transaction.
	 */
	public void clearHttpBasicCredentials ()
	{
		fHttpUsername = null;
		fHttpPassword = null;
	}

	/**
	 * Install a tracer (useful for diagnostics; not a great idea for production). Only one tracer can be installed at a time.
	 * @param t
	 */
	public void installTracer ( HttpTracer t )
	{
		fTracer = t;
	}
	
	/**
	 * Remove an installed tracer.
	 */
	public void removeTracer ()
	{
		fTracer = null;
	}

	/**
	 * Replace the logger used by this class.
	 * @param log
	 */
	public void replaceLogger ( Logger log )
	{
		fLog = log;
		fLog.debug ( "HttpClient logging replaced to this log." );
	}

	/**
	 * Get an object from the cluster using the default caching strategy.
	 * @param path
	 * @return an object
	 * @throws HttpObjectNotFoundException
	 * @throws HttpException
	 * @throws IOException 
	 */
	public JSONObject get ( String path ) throws HttpObjectNotFoundException, HttpException, IOException
	{
		return get ( path, fDefCacheUse );
	}

	/**
	 * Get an object from the cluster using the caching strategy provided.
	 * @param path
	 * @param cu
	 * @return an object
	 * @throws HttpObjectNotFoundException
	 * @throws HttpException
	 * @throws IOException 
	 */
	public JSONObject get ( String path, CacheUse cu ) throws HttpObjectNotFoundException, HttpException, IOException
	{
		final String url = makeUrl ( path );
		final HttpGet get = new HttpGet ( url );
		return runCall ( get, path, true, cu, null );
	}

	public void put ( String path, JSONObject obj ) throws HttpObjectNotFoundException, HttpException, IOException
	{
		put ( path, obj, fDefCacheUse );
	}

	public void put ( String path, JSONObject obj, CacheUse cu ) throws HttpObjectNotFoundException, HttpException, IOException
	{
		try
		{
			final String url = makeUrl ( path );
			final HttpPut put = new HttpPut ( url );
			addEntity ( put, obj );
			runCall ( put, path, false, cu, obj.toString ().getBytes () );
		}
		catch ( HttpObjectNotFoundException e )
		{
			throw e;
		}
		catch ( HttpException e )
		{
			// something went wrong. at this point, don't trust the cache
			fObjectCache.remove ( path );
			throw e;
		}
	}

	public void patch ( String oid, JSONObject obj ) throws HttpObjectNotFoundException, HttpException, IOException
	{
		patch ( oid, obj, fDefCacheUse );
	}

	public void patch ( String path, JSONObject obj, CacheUse cu ) throws HttpObjectNotFoundException, HttpException, IOException
	{
		try
		{
			final String url = makeUrl ( path );
			final HttpPatch put = new HttpPatch ( url );
			addEntity ( put, obj );
			runCall ( put, path, false, cu, obj.toString ().getBytes () );
		}
		catch ( HttpObjectNotFoundException e )
		{
			throw e;
		}
		catch ( HttpException e )
		{
			// something went wrong. at this point, don't trust the cache
			fObjectCache.remove ( path );
			throw e;
		}
	}

	public JSONObject post ( String path, JSONObject obj, boolean expectEntity ) throws HttpObjectNotFoundException, HttpException, IOException
	{
		return post ( path, "application/json", obj.toString().getBytes (), expectEntity );
	}

	public JSONObject post ( String path, JSONArray obj, boolean expectEntity ) throws HttpObjectNotFoundException, HttpException, IOException
	{
		return post ( path, "application/json", obj.toString().getBytes (), expectEntity );
	}
	
	public JSONObject post ( String path, String contentType, byte[] rawBytes, boolean expectEntity ) throws HttpObjectNotFoundException, HttpException, IOException
	{
		try
		{
			final String url = makeUrl ( path );
			final HttpPost post = new HttpPost ( url );
			addEntity ( post, contentType, rawBytes );
			return runCall ( post, path, expectEntity, CacheUse.NONE, rawBytes );
		}
		catch ( HttpObjectNotFoundException e )
		{
			throw e;
		}
		catch ( HttpException e )
		{
			// something went wrong. at this point, don't trust the cache
			fObjectCache.remove ( path );
			throw e;
		}
	}

	public void delete ( String path ) throws HttpException, IOException
	{
		try
		{
			final String url = makeUrl ( path );
			final HttpDelete del = new HttpDelete ( url );
			runCall ( del, path, false, fDefCacheUse, null );
		}
		finally
		{
			// whether the call ran properly or not, clear this item from our cache
			fObjectCache.remove ( path );
		}
	}
	
	public EntityLruCache<JSONObject> getCache() {
		return fObjectCache;
	}

	private final CloseableHttpClient fClient;
	private final HostSelector fHostSelector;
	private ApiCredential fApiCreds;
	private String fHttpUsername;
	private String fHttpPassword;
	private HttpTracer fTracer;
	private final CacheUse fDefCacheUse;
	private EntityLruCache<JSONObject> fObjectCache;
	private Logger fLog;

	private static boolean fTracing = Boolean.parseBoolean ( System.getProperty ( "saclient.trace", "false" ) );
	private void trace ( String msg )
	{
		if ( fTracing )
		{
			fLog.info ( "saHttpClient: " + msg );
		}
	}

	protected void reportProblemWithResponse ()
	{
		fLog.warn ( "There was a problem with the server response. Blacklisting for 3 minutes." );
		fHostSelector.reportReachabilityProblem ( 3, TimeUnit.MINUTES );
	}

	private String makeUrl ( String path )
	{
		final String host = fHostSelector.selectBaseHost ();

		final StringBuilder sb = new StringBuilder ();
		sb.append ( host );

		// might need a path separator...
		if ( !host.endsWith ( "/") && !path.startsWith ( "/" ) )
		{
			sb.append ( "/" );
		}

		sb.append ( path );

		return sb.toString ();
	}

	// note entity is just sent in for use by the tracer
	private JSONObject runCall ( HttpRequestBase req, String entityId, boolean expectEntity, CacheUse cu, byte[] entity ) throws HttpObjectNotFoundException, HttpException, IOException
	{
		try
		{
			// Set to default Cacheuse if none is specified
			if ( cu == CacheUse.DEFAULT)
				cu = fDefCacheUse;
	
			// if we can read this entity from the cache, and it's in there,
			// then off we go with it.
			if ( req.getMethod().equalsIgnoreCase ( HttpGet.METHOD_NAME ) )
			{
				if ( canReadCache ( cu ) && fObjectCache.containsKey ( entityId ) )
				{
					fLog.info ( req.getMethod() + " " + entityId + ": found in cache" );
					return fObjectCache.get ( entityId );
				}
			}
			else
			{
				// this is a PUT, PATCH, POST or DELETE. Clear the item from the cache in either
				// case (a PUT might put a new version back in)
				trace ( "clearing " + entityId + " from cache becuse this is not a GET" );
				fObjectCache.remove ( entityId );
			}

			// cache missed, or was not appropriate/allowed, so hit the server

			// setup API credentials if provided
			boolean authSetup = false;
			if ( fApiCreds != null )
			{
				trace ( "authenticating with " + fApiCreds.getApiKey () );
				final Map<String,String> headers = fApiCreds.createAuthenticationHeaders ( System.currentTimeMillis () );
				for ( Entry<String, String> header : headers.entrySet () )
				{
					req.addHeader ( header.getKey(), header.getValue() );
				}
				authSetup = true;
			}

			// setup HTTP Basic Auth credentials if provided
			if ( fHttpUsername != null && fHttpPassword != null )
			{
				trace ( "authenticating with HTTP Basic " + fHttpUsername );
				
				final String authString = fHttpUsername + ":" + fHttpPassword;
				final String encoded = Base64.encodeBase64String ( authString.getBytes () );
				req.addHeader ( "Authorization", "Basic " + encoded );
				authSetup = true;
			}
			else if ( fHttpUsername != null || fHttpPassword != null )
			{
				fLog.warn ( "HTTP Basic Auth credentials are only partly provided. Ignored." );
			}

			// warn if creds are not protected
			if ( authSetup && !req.getURI ().toString ().startsWith ( "https://" ) )
			{
				fLog.warn ( req.getMethod() + " " + req.getURI().toString() + " will send credentials over a clear channel." );
			}
			
			// we're basically always looking for a JSON response
			req.addHeader ( "Accept", "application/json" );

			// run the HTTP(S) transaction
			fLog.info ( req.getMethod() + " " + req.getURI().toString() +
				( fApiCreds != null ? " (as " + fApiCreds.getApiKey () + ")" : " (anonymous)" ) +
				" ..." );

			// tracing
			if ( fTracer != null )
			{
				final URI uri = req.getURI ();
				final String method = req.getMethod ();
				final Map<String,List<String>> headers = new HashMap<String,List<String>> ();
				for ( Header header : req.getAllHeaders () )
				{
					final String name = header.getName ();
					List<String> list = headers.get ( name );
					if ( list == null )
					{
						list = new LinkedList<String>();
						headers.put ( name, list );
					}
					for ( HeaderElement he : header.getElements () )
					{
						list.add ( he.getName () );
					}
				}
				fTracer.outbound ( uri, headers, method, entity );
			}

			final CloseableHttpResponse reply = fClient.execute ( req, HttpClientContext.create() );
			try
			{
				final StatusLine sl = reply.getStatusLine ();
				final int sc = sl.getStatusCode ();
				
				fLog.info ( "\t--> " + sl.toString() );

				if ( sc >= 200 && sc <= 299 )
				{
					JSONObject result = null;
					if ( expectEntity )
					{
						try
						{
							final JSONTokener t = new JSONTokener ( reply.getEntity ().getContent () );

							// some services return an array rather than the more typical object
							// container for a result. to deal with this in a consistent way, we
							// wrap this kind of result in an object.
							final char firstChar = t.next ();
							t.back ();

							if ( firstChar == '[' )
							{
								trace ( "server responded with a JSON array. wrapping it." );

								final JSONArray toWrap = new JSONArray ( t );
								result = new JSONObject ();
								result.put ( "result", toWrap );
							}
							else
							{
								result = new JSONObject ( t );
							}
							trace ( "server response: " + result.toString () );
						}
						catch ( JSONException x )
						{
							// this is a server error, treat it like a 500
							// server says its having trouble
							fLog.warn ( "Server response wasn't good JSON." );
							fHostSelector.reportReachabilityProblem ( 5, TimeUnit.SECONDS );
							throw new HttpException ( 500, "Bad JSON from server (client side exception)." );
						}

						// on a GET or PUT, make sure the cache has the new value
						if ( req.getMethod().equalsIgnoreCase ( HttpGet.METHOD_NAME ) ||
							req.getMethod().equalsIgnoreCase ( HttpPut.METHOD_NAME ) )
						{
							if ( canWriteCache ( cu ) || fObjectCache.containsKey ( entityId ) )
							{
								trace ( "adding " + entityId + " to cache" );
								fObjectCache.put ( entityId, result );
							}
						}
					}
					// else: not expecting a response

					trace ( "runCall() completed normally" );
					
					if ( fTracer != null )
					{
						final Map<String,List<String>> headers = new HashMap<String,List<String>> ();
						for ( Header header : reply.getAllHeaders () )
						{
							final String name = header.getName ();
							List<String> list = headers.get ( name );
							if ( list == null )
							{
								list = new LinkedList<String>();
								headers.put ( name, list );
							}
							for ( HeaderElement he : header.getElements () )
							{
								list.add ( he.getName () );
							}
						}

						byte[] traceReplyEntity = result == null ? null : result.toString ().getBytes ();
						fTracer.inbound ( headers, sc, sl.toString(), traceReplyEntity );
					}
					
					return result;
				}
				else if ( sc == 404 )
				{
					// 404 is a special case; we want to report that the object wasn't 
					// found rather than a general client or server error
					trace ( "object not found" );
					throw new HttpObjectNotFoundException ( entityId );
				}
				else
				{
					// this can be any number of things from a bad request to a
					// server malfunction.
					if ( sc >= 400 && sc <= 499 )
					{
						// server says its our error
						throw new HttpException ( sl, reply.getEntity () );
					}
					else if ( sc >= 500 )
					{
						// server says its having trouble
						fHostSelector.reportReachabilityProblem ( 5, TimeUnit.SECONDS );
						throw new HttpException ( sl, reply.getEntity () );
					}
					else
					{
						// no idea...
						throw new HttpException ( sl, reply.getEntity () );
					}
				}
			}
			finally
			{
				fLog.debug ( "Consuming the entity." );
				EntityUtils.consumeQuietly ( reply.getEntity () );
			}
		}
		catch ( UnknownHostException x )
		{
			fLog.warn ( "Unknown host " + req.getURI().getHost() + "; blacklisting for 10 minutes" );
			fHostSelector.reportReachabilityProblem ( 10, TimeUnit.MINUTES );
			throw x;
		}
		catch ( IOException x )
		{
			fLog.warn ( "Error executing HTTP request. " + x.getMessage() + "; blacklisting for 2 minutes"  );
			fHostSelector.reportReachabilityProblem ( 2, TimeUnit.MINUTES );
			throw x;
		}
	}

	private void addEntity ( HttpEntityEnclosingRequestBase req, JSONObject o )
	{
		addEntity ( req, "application/json", o.toString().getBytes () );
	}

	private void addEntity ( HttpEntityEnclosingRequestBase req, String contentType, byte[] o )
	{
		final ByteArrayEntity input = new ByteArrayEntity ( o );
		input.setContentType ( contentType );
		req.addHeader ( "Content-Type", contentType );
		req.setEntity ( input );
	}

	private static boolean canReadCache ( CacheUse cu )
	{
		return cu != null && cu.equals ( CacheUse.FULL );
	}
	
	private static boolean canWriteCache ( CacheUse cu )
	{
		return cu != null && cu.equals ( CacheUse.FULL ) || cu.equals ( CacheUse.WRITE_ONLY );
	}

	protected String getCurrentApiKey ()
	{
		return fApiCreds.getApiKey ();
	}

	protected static final int kDefault_PoolMaxInTotal = 32;
	protected static final int kDefault_PoolMaxPerRoute = 32;

	// this was 30 seconds originally, but that was causing some "mechanical
	// sympathy" with a 30 second timeout in the UEB API server. This timeout
	// is really just about not letting a process hang for hours after it
	// misses a packet, so it can be quite a bit higher.
	public static final int kDefault_SocketTimeoutMs = 1000 * 60 * 10;
}
