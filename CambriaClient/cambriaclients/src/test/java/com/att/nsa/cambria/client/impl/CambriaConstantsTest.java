/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.http.HttpHost;
import org.junit.Test;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class CambriaConstantsTest extends TestCase
{
	@Test
	public void testPlainHost () throws IOException
	{
		final String rawTopic = "bar";
		final String result = CambriaConstants.makeUrl ( rawTopic );
		assertEquals ( CambriaConstants.kBasePath + "bar", result );
	}

	@Test
	public void testHostWithProtocol () throws IOException
	{
		final String rawTopic = "bar";
		final String result = CambriaConstants.makeUrl (  rawTopic );
		assertEquals ( CambriaConstants.kBasePath + "bar", result );
	}

	@Test
	public void testHostWithProtocolAndPort () throws IOException
	{
		final String rawTopic = "bar";
		final String result = CambriaConstants.makeUrl ( rawTopic );
		assertEquals ( CambriaConstants.kBasePath + "bar", result );
	}

	@Test
	public void testHostWithPort () throws IOException
	{
		final String rawTopic = "bar";
		final String result = CambriaConstants.makeUrl ( rawTopic );
		assertEquals ( CambriaConstants.kBasePath + "bar", result );
	}

	@Test
	public void testHostWithPortAndEscapedTopic () throws IOException
	{
		final String rawTopic = "bar?bell";
		final String result = CambriaConstants.makeUrl ( rawTopic );
		assertEquals ( CambriaConstants.kBasePath + "bar%3Fbell", result );
	}

	@Test
	public void testConsumerPlainHost () throws IOException
	{
		final String rawTopic = "bar";
		final String rawGroup = "group";
		final String rawId = "id";
		final String result = CambriaConstants.makeConsumerUrl ( rawTopic, rawGroup, rawId );
		assertEquals ( CambriaConstants.kBasePath + "bar/group/id", result );
	}

	@Test
	public void testCreateHostList ()
	{
		final ArrayList<String> in = new ArrayList<String> ();
		in.add ( "foo" );
		in.add ( "bar" );
		in.add ( "baz:80" );

		final Collection<HttpHost> hosts = CambriaConstants.createHostsList ( in );
		assertEquals ( 3, hosts.size () );

		final Iterator<HttpHost> it = hosts.iterator ();
		final HttpHost first = it.next ();
		assertEquals ( CambriaConstants.kStdCambriaServicePort, first.getPort () );
		assertEquals ( "foo", first.getHostName () );

		final HttpHost second = it.next ();
		assertEquals ( CambriaConstants.kStdCambriaServicePort, second.getPort () );
		assertEquals ( "bar", second.getHostName () );

		final HttpHost third = it.next ();
		assertEquals ( 80, third.getPort () );
		assertEquals ( "baz", third.getHostName () );
	}

	private static final String[][] hostTests =
	{
		{ "host", "host", "" + CambriaConstants.kStdCambriaServicePort },
		{ ":oops", null, "-1" },
		{ "host:1.3", null, "-1" },
		{ "host:13", "host", "13" },
		{ "host:", "host", "" + CambriaConstants.kStdCambriaServicePort },
	};

	@Test
	public void testHostParse ()
	{
		for ( String[] test : hostTests )
		{
			final String hostIn = test[0];
			final String hostOut = test[1];
			final int portOut = Integer.parseInt ( test[2] );

			try
			{
				final HttpHost hh = CambriaConstants.hostForString ( hostIn );
				assertEquals ( hostOut, hh.getHostName () );
				assertEquals ( portOut, hh.getPort () );
			}
			catch ( IllegalArgumentException x )
			{
				assertEquals ( -1, portOut );
			}
		}
	}
}
