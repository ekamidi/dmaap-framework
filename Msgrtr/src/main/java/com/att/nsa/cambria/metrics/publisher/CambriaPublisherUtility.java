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
package com.att.nsa.cambria.metrics.publisher;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpHost;
/**
 * 
 * @author author
 *
 */
public class CambriaPublisherUtility
{
	public static final String kBasePath = "/events/";
	public static final int kStdCambriaServicePort = 3904;
/**
 * 
 * Translates a string into <code>application/x-www-form-urlencoded</code>
 * format using a specific encoding scheme.
 * @param s
 * @return
 * 
 */
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
/**
 * 
 * building url
 * @param rawTopic
 * @return
 */
	public static String makeUrl ( String rawTopic )
	{
		final String cleanTopic = escape ( rawTopic );
		
		final StringBuffer url = new StringBuffer().
			append ( CambriaPublisherUtility.kBasePath ).
			append ( cleanTopic );
		return url.toString ();
	}
/**
 * 
 * building consumerUrl
 * @param topic
 * @param rawConsumerGroup
 * @param rawConsumerId
 * @return
 */
	public static String makeConsumerUrl ( String topic, String rawConsumerGroup, String rawConsumerId )
	{
		final String cleanConsumerGroup = escape ( rawConsumerGroup );
		final String cleanConsumerId = escape ( rawConsumerId );
		return CambriaPublisherUtility.kBasePath + topic + "/" + cleanConsumerGroup + "/" + cleanConsumerId;
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
	 * @param hosts
	 * @return a list of hosts
	 * if host.length<1 throws IllegalArgumentException
	 * 
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
