/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.caches;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.Test;

import com.att.nsa.testing.TestClock;

public class TimeLimitedLruCacheTest extends TestCase
{
	@Test
	public void testSimpleCache ()
	{
		final TestClock testClock = new TestClock ();

		final TimeLimitedLruCache<String,String> cache = new TimeLimitedLruCache<String,String> ( 10, 10, TimeUnit.SECONDS );
		cache.put ( "A", "1" );

		assertEquals ( "1", cache.get ( "A" ) );

		testClock.forward ( 5000 );
		assertEquals ( "1", cache.get ( "A" ) );

		testClock.forward ( 6000 );
		assertNull ( cache.get ( "A" ) );
	}
}
