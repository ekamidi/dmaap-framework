/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedList;

import junit.framework.TestCase;

import org.junit.Test;

import com.att.nsa.apiClient.http.HttpClient;
import com.att.nsa.apiClient.http.HttpClient.ConnectionType;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class CambraConsumerImplTest extends TestCase
{
	@Test
	public void testNullFilter () throws IOException, GeneralSecurityException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final CambriaConsumerImpl c = new CambriaConsumerImpl ( ConnectionType.HTTP, hosts, "topic", "cg", "cid", -1, -1, null, null, null, HttpClient.kDefault_SocketTimeoutMs );
		final String url = c.createUrlPath ( -1, -1 );
		assertEquals ( CambriaConstants.kBasePath + "topic/cg/cid", url );
	}

	@Test
	public void testFilterWithNoTimeoutOrLimit () throws IOException, GeneralSecurityException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final CambriaConsumerImpl c = new CambriaConsumerImpl ( ConnectionType.HTTP, hosts, "topic", "cg", "cid", -1, -1, "filter", null, null, HttpClient.kDefault_SocketTimeoutMs );
		final String url = c.createUrlPath ( -1, -1 );
		assertEquals ( CambriaConstants.kBasePath + "topic/cg/cid?filter=filter", url );
	}

	@Test
	public void testTimeoutNoLimitNoFilter () throws IOException, GeneralSecurityException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final CambriaConsumerImpl c = new CambriaConsumerImpl ( ConnectionType.HTTP, hosts, "topic", "cg", "cid", 30000, -1, null, null, null, HttpClient.kDefault_SocketTimeoutMs );
		final String url = c.createUrlPath ( 30000, -1 );
		assertEquals ( CambriaConstants.kBasePath + "topic/cg/cid?timeout=30000", url );
	}

	@Test
	public void testNoTimeoutWithLimitNoFilter () throws IOException, GeneralSecurityException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final CambriaConsumerImpl c = new CambriaConsumerImpl ( ConnectionType.HTTP, hosts, "topic", "cg", "cid", -1, 100, null, null, null, HttpClient.kDefault_SocketTimeoutMs );
		final String url = c.createUrlPath ( -1, 100 );
		assertEquals ( CambriaConstants.kBasePath + "topic/cg/cid?limit=100", url );
	}

	@Test
	public void testWithTimeoutWithLimitWithFilter () throws IOException, GeneralSecurityException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final CambriaConsumerImpl c = new CambriaConsumerImpl ( ConnectionType.HTTP, hosts, "topic", "cg", "cid", 1000, 400, "f", null, null, HttpClient.kDefault_SocketTimeoutMs );
		final String url = c.createUrlPath ( 1000, 400 );
		assertEquals ( CambriaConstants.kBasePath + "topic/cg/cid?timeout=1000&limit=400&filter=f", url );
	}

	@Test
	public void testFilterEncoding () throws IOException, GeneralSecurityException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final CambriaConsumerImpl c = new CambriaConsumerImpl ( ConnectionType.HTTP, hosts, "topic", "cg", "cid", -1, -1, "{ \"foo\"=\"bar\"bar\" }", null, null, HttpClient.kDefault_SocketTimeoutMs );
		final String url = c.createUrlPath ( -1, -1 );
		assertEquals ( CambriaConstants.kBasePath + "topic/cg/cid?filter=%7B+%22foo%22%3D%22bar%22bar%22+%7D", url );
	}
}
