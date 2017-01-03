/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.caches;

import junit.framework.TestCase;

import org.junit.Test;

public class LruCacheTest extends TestCase
{
	@Test
	public void testSimpleUse ()
	{
		final LruCache<String,String> cache = new LruCache<String,String> ( 10 );
		cache.put ( "A", "1" );

		assertEquals ( "1", cache.get ( "A" ) );
		assertNull ( cache.get ( "B" ) );
	}
}
