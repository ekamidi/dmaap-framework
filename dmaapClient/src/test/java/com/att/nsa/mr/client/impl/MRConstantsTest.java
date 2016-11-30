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
package com.att.nsa.mr.client.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.http.HttpHost;
import org.junit.Test;

import com.att.nsa.mr.client.impl.MRConstants;

public class MRConstantsTest extends TestCase
{
	@Test
	public void testPlainHost () throws IOException
	{
		final String rawTopic = "bar";
		final String result = MRConstants.makeUrl ( rawTopic );
		assertEquals ( "/events/"  + "bar", result );
	}

	@Test
	public void testHostWithProtocol () throws IOException
	{
		final String rawTopic = "bar";
		final String result = MRConstants.makeUrl (  rawTopic );
		assertEquals ( "/events/"  + "bar", result );
	}

	@Test
	public void testHostWithProtocolAndPort () throws IOException
	{
		final String rawTopic = "bar";
		final String result = MRConstants.makeUrl ( rawTopic );
		assertEquals ( "/events/" + "bar", result );
	}

	@Test
	public void testHostWithPort () throws IOException
	{
		final String rawTopic = "bar";
		final String result = MRConstants.makeUrl ( rawTopic );
		assertEquals ( "/events/" + "bar", result );
	}

	@Test
	public void testHostWithPortAndEscapedTopic () throws IOException
	{
		final String rawTopic = "bar?bell";
		final String result = MRConstants.makeUrl ( rawTopic );
		assertEquals ( "/events/" + "bar%3Fbell", result );
	}

	@Test
	public void testConsumerPlainHost () throws IOException
	{
		final String rawTopic = "bar";
		final String rawGroup = "group";
		final String rawId = "id";
		final String result = MRConstants.makeConsumerUrl ( rawTopic, rawGroup, rawId );
		assertEquals ( "/events/" + "bar/group/id", result );
	}

	@Test
	public void testCreateHostList ()
	{
		final ArrayList<String> in = new ArrayList<String> ();
		in.add ( "foo" );
		in.add ( "bar" );
		in.add ( "baz:80" );

		final Collection<HttpHost> hosts = MRConstants.createHostsList ( in );
		assertEquals ( 3, hosts.size () );

		final Iterator<HttpHost> it = hosts.iterator ();
		final HttpHost first = it.next ();
		assertEquals ( MRConstants.kStdMRServicePort, first.getPort () );
		assertEquals ( "foo", first.getHostName () );

		final HttpHost second = it.next ();
		assertEquals ( MRConstants.kStdMRServicePort, second.getPort () );
		assertEquals ( "bar", second.getHostName () );

		final HttpHost third = it.next ();
		assertEquals ( 80, third.getPort () );
		assertEquals ( "baz", third.getHostName () );
	}

	private static final String[][] hostTests =
	{
		{ "host", "host", "" + MRConstants.kStdMRServicePort },
		{ ":oops", null, "-1" },
		{ "host:1.3", null, "-1" },
		{ "host:13", "host", "13" },
		{ "host:", "host", "" + MRConstants.kStdMRServicePort },
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
				final HttpHost hh = MRConstants.hostForString ( hostIn );
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
