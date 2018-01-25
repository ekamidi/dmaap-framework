/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.data.json;

import java.util.List;

import org.json.JSONObject;
import org.junit.Test;

import junit.framework.TestCase;

public class SaJsonUtilTest extends TestCase
{
	@Test
	public void testSegmenter ()
	{
		final JSONObject o = new JSONObject ()
			.put ( "foo", new JSONObject ()
				.put ( "bar", new JSONObject ()
					.put ( "baz", 0 )
				)
			)
			.put ( "foo.bar", new JSONObject ()
				.put ( "bat", 3	)
			)
		;

		List<String> segs = SaJsonUtil.getNameSegments ( o, "foo.bar.baz" );
		assertNotNull ( segs );
		assertEquals ( 3, segs.size () );
		assertEquals ( "foo", segs.get ( 0 ) );
		assertEquals ( "bar", segs.get ( 1 ) );
		assertEquals ( "baz", segs.get ( 2 ) );

		segs = SaJsonUtil.getNameSegments ( o, "foo.bar.bat" );
		assertNotNull ( segs );
		assertEquals ( 2, segs.size () );
		assertEquals ( "foo.bar", segs.get ( 0 ) );
		assertEquals ( "bat", segs.get ( 1 ) );
	}
}