/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.configs.confimpl;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import junit.framework.TestCase;

import org.junit.Test;

import com.att.nsa.configs.ConfigDbException;

public class EncryptingLayerTest extends TestCase
{
	@Test
	public void testDbStorage () throws NoSuchAlgorithmException, InvalidKeySpecException, ConfigDbException
	{
		final String kAlgo = "AES/CBC/PKCS5Padding";
		final byte[] kIv = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };

		final String base64Key = EncryptingLayer.createSecretKey ();
		System.out.println ( "key: " + base64Key );

		final MemConfigDb db = new MemConfigDb ();

		final String data = "foobar";
		
		{
			final Key key = EncryptingLayer.readSecretKey ( base64Key );
			final EncryptingLayer el = new EncryptingLayer ( db, kAlgo, key, kIv );
			el.store ( el.parse ( "/foo" ), data );
		}

		// see what mem configdb got
		final String enc = db.load ( db.parse ( "/foo" ) );
		System.out.println ( "encoded value: " + enc );

		{
			final Key key = EncryptingLayer.readSecretKey ( base64Key );
			final EncryptingLayer el = new EncryptingLayer ( db, kAlgo, key, kIv );
			final String val = el.load ( el.parse ( "/foo" ) );
			assertEquals ( data, val );
		}
	}
}
