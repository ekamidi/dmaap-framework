/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient.http;

import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.junit.Test;

import com.att.nsa.apiClient.http.HttpClient.ConnectionType;

public class HttpClientTest extends TestCase
{
	@Test
	public void testHostSpecs () throws MalformedURLException
	{
		assertEquals ( "http://foo:3904", HttpClient.makeHostSpec ( null, "foo", 3904 ) );
		assertEquals ( "http://foo:3904", HttpClient.makeHostSpec ( null, "http://foo", 3904 ) );
		assertEquals ( "http://foo:3904", HttpClient.makeHostSpec ( null, "foo:3904", 80 ) );
		assertEquals ( "http://foo:3904", HttpClient.makeHostSpec ( ConnectionType.HTTP, "foo", 3904 ) );
		assertEquals ( "https://foo:3905", HttpClient.makeHostSpec ( ConnectionType.HTTPS, "foo", 3905 ) );
		assertEquals ( "https://foo:3905", HttpClient.makeHostSpec ( ConnectionType.HTTPS_NO_VALIDATION, "foo", 3905 ) );
	}
}
