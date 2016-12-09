/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpHost;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class CambriaConstants
{
	public static final String kBasePath = "/events/";
	public static final int kStdCambriaServicePort = 3904;
	public static final int kStdCambriaHttpsServicePort = 3905;

	public static String escape ( String s )
	{
		try
		{
			return URLEncoder.encode ( s, "UTF-8");
		}
		catch ( UnsupportedEncodingException e )
		{
			throw new RuntimeException ( e );
		}
	}

	public static String makeUrl ( String rawTopic )
	{
		final String cleanTopic = escape ( rawTopic );
		
		final StringBuffer url = new StringBuffer().
			append ( CambriaConstants.kBasePath ).
			append ( cleanTopic );
		return url.toString ();
	}

	public static String makeConsumerUrl ( String topic, String rawConsumerGroup, String rawConsumerId )
	{
		final String cleanConsumerGroup = escape ( rawConsumerGroup );
		final String cleanConsumerId = escape ( rawConsumerId );
		return CambriaConstants.kBasePath + topic + "/" + cleanConsumerGroup + "/" + cleanConsumerId;
	}

	/**
	 * Create a list of HttpHosts from an input list of strings. Input strings have
	 * host[:port] as format. If the port section is not provided, the default port is used.
	 * 
	 * @param hosts
	 * @return a list of hosts
	 */
	public static List<HttpHost> createHostsList(Collection<String> hosts)
	{
		final ArrayList<HttpHost> convertedHosts = new ArrayList<HttpHost> ();
		for ( String host : hosts )
		{
			if ( host.length () == 0 ) continue;
			convertedHosts.add ( hostForString ( host ) );
		}
		return convertedHosts;
	}

	/**
	 * Return an HttpHost from an input string. Input string has
	 * host[:port] as format. If the port section is not provided, the default port is used.
	 * 
	 * @param host
	 * @return a list of hosts
	 */
	public static HttpHost hostForString ( String host )
	{
		if ( host.length() < 1 ) throw new IllegalArgumentException ( "An empty host entry is invalid." );
		
		String hostPart = host;
		int port = kStdCambriaServicePort;

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
