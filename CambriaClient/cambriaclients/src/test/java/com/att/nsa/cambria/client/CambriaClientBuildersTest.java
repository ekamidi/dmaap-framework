/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client;

import java.net.MalformedURLException;
import java.security.GeneralSecurityException;

import junit.framework.TestCase;

import org.junit.Test;

import com.att.nsa.cambria.client.impl.CambriaConsumerImpl;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class CambriaClientBuildersTest extends TestCase
{
	@Test
	public void testConsumerTimeoutSetup () throws MalformedURLException, GeneralSecurityException
	{
		final CambriaConsumer cc = new CambriaClientBuilders.ConsumerBuilder ()
			.usingHosts ( "host" )
			.onTopic ( "TEST-TOPIC" )
			.knownAs ( "cg", "cid" )
			.receivingAtMost ( 100 )
			.waitAtServer ( 23456 )
			.usingHttps ( true )
			.authenticatedBy ( "key", "secret" )
			.authenticatedByHttp ( "id", "password" )
			.withSocketTimeout ( 123456 )
			.build ();
		assertTrue ( cc instanceof CambriaConsumerImpl );
		final CambriaConsumerImpl cci = (CambriaConsumerImpl) cc;

		final String path = cci.createUrlPath ( 23456, 100 );
		assertEquals ( "/events/TEST-TOPIC/cg/cid?timeout=23456&limit=100", path );
	}
}
