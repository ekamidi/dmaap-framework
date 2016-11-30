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
import java.util.LinkedList;

import junit.framework.TestCase;

import org.junit.Test;

import com.att.nsa.mr.client.impl.MRConstants;
import com.att.nsa.mr.client.impl.MRConsumerImpl;

public class MRConsumerImplTest extends TestCase
{
	@Test
	public void testNullFilter () throws IOException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final MRConsumerImpl c = new MRConsumerImpl ( hosts, "topic", "cg", "cid", -1, -1, null, null, null );
		final String url = c.createUrlPath (MRConstants.makeConsumerUrl ( "localhost:8080", "topic", "cg", "cid","http" ), -1, -1 );
		assertEquals ("http://localhost:8080/events/" + "topic/cg/cid", url );
	}

	@Test
	public void testFilterWithNoTimeoutOrLimit () throws IOException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final MRConsumerImpl c = new MRConsumerImpl ( hosts, "topic", "cg", "cid", -1, -1, "filter", null, null );
		final String url = c.createUrlPath ( MRConstants.makeConsumerUrl ( "localhost:8080", "topic", "cg", "cid" ,"http"),-1, -1 );
		assertEquals ("http://localhost:8080/events/" + "topic/cg/cid?filter=filter", url );
	}

	@Test
	public void testTimeoutNoLimitNoFilter () throws IOException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final MRConsumerImpl c = new MRConsumerImpl ( hosts, "topic", "cg", "cid", 30000, -1, null, null, null );
		final String url = c.createUrlPath (MRConstants.makeConsumerUrl ( "localhost:8080", "topic", "cg", "cid","http" ), 30000, -1 );
		assertEquals ( "http://localhost:8080/events/"  + "topic/cg/cid?timeout=30000", url );
	}

	@Test
	public void testNoTimeoutWithLimitNoFilter () throws IOException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final MRConsumerImpl c = new MRConsumerImpl ( hosts, "topic", "cg", "cid", -1, 100, null, null, null );
		final String url = c.createUrlPath (MRConstants.makeConsumerUrl ( "localhost:8080", "topic", "cg", "cid","http" ), -1, 100 );
		assertEquals ( "http://localhost:8080/events/"  + "topic/cg/cid?limit=100", url );
	}

	@Test
	public void testWithTimeoutWithLimitWithFilter () throws IOException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final MRConsumerImpl c = new MRConsumerImpl ( hosts, "topic", "cg", "cid", 1000, 400, "f", null, null );
		final String url = c.createUrlPath (MRConstants.makeConsumerUrl ( "localhost:8080", "topic", "cg", "cid" ,"http"), 1000, 400 );
		assertEquals ("http://localhost:8080/events/"  + "topic/cg/cid?timeout=1000&limit=400&filter=f", url );
	}

	@Test
	public void testFilterEncoding () throws IOException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final MRConsumerImpl c = new MRConsumerImpl ( hosts, "topic", "cg", "cid", -1, -1, "{ \"foo\"=\"bar\"bar\" }", null, null );
		final String url = c.createUrlPath (MRConstants.makeConsumerUrl ( "localhost:8080", "topic", "cg", "cid","http" ), -1, -1 );
		assertEquals ( "http://localhost:8080/events/"  + "topic/cg/cid?filter=%7B+%22foo%22%3D%22bar%22bar%22+%7D", url );
	}
}
