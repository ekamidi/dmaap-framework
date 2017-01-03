/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient.http;

import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.junit.Test;

import com.att.nsa.apiClient.http.HttpClient.ConnectionType;

public class UrlBuilderTest extends TestCase
{
	@Test
	public void testHostSpecs () throws MalformedURLException
	{
		assertEquals ( "http://localhost:80", HttpClient.makeHostSpec ( ConnectionType.HTTP, "localhost", 80 ) );
		assertEquals ( "https://localhost:80", HttpClient.makeHostSpec ( ConnectionType.HTTPS, "localhost", 80 ) );
		assertEquals ( "https://localhost:80", HttpClient.makeHostSpec ( ConnectionType.HTTPS_NO_VALIDATION, "localhost", 80 ) );

		assertEquals ( "http://localhost:8888", HttpClient.makeHostSpec ( ConnectionType.HTTP, "localhost:8888", 80 ) );
		assertEquals ( "https://localhost:8888", HttpClient.makeHostSpec ( ConnectionType.HTTPS, "localhost:8888", 80 ) );

		assertEquals ( "http://localhost:8888", HttpClient.makeHostSpec ( ConnectionType.HTTP, "http://localhost:8888", 80 ) );
	}

	@Test
	public void testBadHostSpecs ()
	{
		try
		{
			HttpClient.makeHostSpec ( ConnectionType.HTTP, "ftp://localhost", 80 );
			fail ( "should have caught illegal protocol" );
		}
		catch ( MalformedURLException e )
		{
			// expected
		}

		try
		{
			HttpClient.makeHostSpec ( ConnectionType.HTTPS, "http://localhost", 80 );
			fail ( "should have caught mismatched protocol" );
		}
		catch ( MalformedURLException e )
		{
			// expected
		}
	}
}
