/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;
import com.att.nsa.cambria.client.CambriaConsumer;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class CambriaConsumerImpl extends CambriaBaseClient implements CambriaConsumer
{
	public static List<String> stringToList ( String str )
	{
		final LinkedList<String> set = new LinkedList<String> ();
		if ( str != null )
		{
			final String[] parts = str.trim ().split ( "," );
			for ( String part : parts )
			{
				final String trimmed = part.trim();
				if ( trimmed.length () > 0 )
				{
					set.add ( trimmed );
				}
			}
		}
		return set;
	}
	
	public CambriaConsumerImpl ( ConnectionType ct, Collection<String> hostPart, final String topic, final String consumerGroup,
		final String consumerId, int timeoutMs, int limit, String filter, String apiKey, String apiSecret, int soTimeoutMs ) throws MalformedURLException, GeneralSecurityException
	{
		super ( ct, hostPart, topic + "::" + consumerGroup + "::" + consumerId, soTimeoutMs );

		fTopic = topic;
		fGroup = consumerGroup;
		fId = consumerId;
		fTimeoutMs = timeoutMs;
		fLimit = limit;
		fFilter = filter;

		setApiCredentials ( apiKey, apiSecret );
	}

	@Override
	public Iterable<String> fetch () throws IOException
	{
		// fetch with the timeout and limit set in constructor
		return fetch ( fTimeoutMs, fLimit );
	}

	@Override
	public Iterable<String> fetch ( int timeoutMs, int limit ) throws IOException
	{
		final LinkedList<String> msgs = new LinkedList<String> ();

// FIXME: the timeout on the socket needs to be at least as long as the long poll
//		// sanity check for long poll timeout vs. socket read timeout
//		final int maxReasonableTimeoutMs = CambriaSingletonHttpClient.sfSoTimeoutMs * 9/10;
//		if ( timeoutMs > maxReasonableTimeoutMs )
//		{
//			log.warn ( "Long poll time (" + timeoutMs + ") is too high w.r.t. socket read timeout (" +
//				CambriaSingletonHttpClient.sfSoTimeoutMs + "). Reducing long poll timeout to " + maxReasonableTimeoutMs + "." );
//			timeoutMs = maxReasonableTimeoutMs;
//		}

		final String urlPath = createUrlPath ( timeoutMs, limit );

		getLog().info ( "UEB GET " + urlPath );
		try
		{
			final JSONObject o = get ( urlPath );

			if ( o != null )
			{
				final JSONArray a = o.getJSONArray ( "result" );
				if ( a != null )
				{
					for ( int i=0; i<a.length (); i++ )
					{
						msgs.add ( a.getString ( i ) );
					}
				}
			}
		}
		catch ( HttpObjectNotFoundException e )
		{
			// this can happen if the topic is not yet created.
			// we ignore the response, but delay the return up
			// to the timeout
			getLog().warn ( "Topic not found: " + e.getMessage() );
			try
			{
				Thread.sleep ( timeoutMs, 0 );
			}
			catch ( InterruptedException e1 )
			{
				getLog().warn ( "Sleep interrupted during topic-not-found wait." );
			}
		}
		catch ( JSONException e )
		{
			// unexpected response
			reportProblemWithResponse ();
		}
		catch ( HttpException e )
		{
			throw new IOException ( e );
		}

		return msgs;
	}

	private final String fTopic;
	private final String fGroup;
	private final String fId;
	private final int fTimeoutMs;
	private final int fLimit;
	private final String fFilter;

	public String createUrlPath (int timeoutMs , int limit )
	{
		final StringBuffer url = new StringBuffer ( CambriaConstants.makeConsumerUrl ( fTopic, fGroup, fId ) );
		final StringBuffer adds = new StringBuffer ();
		if ( timeoutMs > -1 ) adds.append ( "timeout=" ).append ( timeoutMs ); 
		if ( limit > -1 )
		{
			if ( adds.length () > 0 )
			{
				adds.append ( "&" );
			}
			adds.append ( "limit=" ).append ( limit );
		}
		if ( fFilter != null && fFilter.length () > 0 )
		{
			try {
				if ( adds.length () > 0 )
				{
					adds.append ( "&" );
				}
				adds.append("filter=").append(URLEncoder.encode(fFilter, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e.getMessage() + "....say whaaaat?!");
			}
		}
		if ( adds.length () > 0 )
		{
			url.append ( "?" ).append ( adds.toString () );
		}
		return url.toString ();
	}

}
