/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.configs;

import org.json.JSONObject;
import org.junit.Test;

import com.att.nsa.configs.confimpl.MemConfigDb;

import junit.framework.TestCase;

public class JsonConfigDbTest extends TestCase
{
	@Test
	public void testWriteAndRead () throws ConfigDbException
	{
		final MemConfigDb memDb = new MemConfigDb ();
		final JsonConfigDb db = new JsonConfigDb ( memDb );

		final ConfigPath path = db.getRoot ().getChild ( "test" );
		db.storeJson ( path, new JSONObject ().put ( "foo", "bar" ) );
		final String rawData = db.load ( path );
		final JSONObject obj = db.loadJson ( path );

		assertNotNull ( rawData );
		assertNotNull ( obj );

		assertEquals ( "bar", obj.get ( "foo" ) );
	}
}