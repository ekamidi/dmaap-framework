/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.configs.confimpl;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.att.nsa.configs.ConfigDb;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.configs.ConfigPath;
import com.att.nsa.util.rrConvertor;

/**
 * The encrypting layer config db encrypts and decrypts data in storage. Note that paths
 * are not encrypted, just the data stored at a path.
 * 
 */
public class EncryptingLayer implements ConfigDb
{
	/**
	 * Construct an encrypting layer over another ConfigDb. 
	 * @param storageLayer the storage config db
	 * @param algorithm the cipher algorithm (passed to Cipher.getInstance)
	 * @param key your application's key
	 * @param iv an initialization vector (use the same value for each instantiation)
	 */
	public EncryptingLayer ( ConfigDb storageLayer, String algorithm, Key key, byte[] iv )
	{
		fStorage = storageLayer;
		fAlgo = algorithm;
		fKey = key;
		fIV = new IvParameterSpec ( iv );
	}

	/**
	 * Create an AES key for use in this encryption layer
	 * @return a base64 encoded string for the created key
	 * @throws NoSuchAlgorithmException 
	 */
	public static String createSecretKey () throws NoSuchAlgorithmException
	{
		final KeyGenerator keygen = KeyGenerator.getInstance ( "AES" );
		keygen.init ( 128 );
		final SecretKey key = keygen.generateKey ();
		final byte[] skBytes = key.getEncoded ();
		final String skBytesBase64 = rrConvertor.base64Encode ( skBytes );
		return skBytesBase64;
	}

	/**
	 * Read a secret key that was originally created with createSecretKey
	 * @param base64Key
	 * @return a key
	 */
	public static Key readSecretKey ( String base64Key )
	{
		final byte[] bytes = rrConvertor.base64Decode ( base64Key );
		final SecretKeySpec spec = new SecretKeySpec ( bytes, 0, bytes.length, "AES" );
		return spec;
	}

	@Override
	public ConfigPath getRoot ()
	{
		return fStorage.getRoot ();
	}

	@Override
	public ConfigPath parse ( String pathAsString )
	{
		return fStorage.parse ( pathAsString );
	}

	@Override
	public boolean exists ( ConfigPath path ) throws ConfigDbException
	{
		return fStorage.exists ( path );
	}

	@Override
	public String load ( ConfigPath key ) throws ConfigDbException
	{
		final String enc = fStorage.load ( key );
		return enc == null ? null : decrypt ( enc );
	}

	@Override
	public Set<ConfigPath> loadChildrenNames ( ConfigPath key ) throws ConfigDbException
	{
		return fStorage.loadChildrenNames ( key );
	}

	@Override
	public Map<ConfigPath, String> loadChildrenOf ( ConfigPath key ) throws ConfigDbException
	{
		final HashMap<ConfigPath,String> result = new HashMap<ConfigPath,String> ();
		for ( Entry<ConfigPath,String> e : result.entrySet () )
		{
			result.put ( e.getKey(), decrypt(e.getValue()) );
		}
		return result;
	}

	@Override
	public void store ( ConfigPath key, String data ) throws ConfigDbException
	{
		fStorage.store ( key, encrypt(data) );
	}

	@Override
	public boolean clear ( ConfigPath key ) throws ConfigDbException
	{
		return fStorage.clear ( key );
	}

	@Override
	public long getLastModificationTime ( ConfigPath offset ) throws ConfigDbException
	{
		return fStorage.getLastModificationTime ( offset );
	}

	private final ConfigDb fStorage;
	private final String fAlgo;
	private final Key fKey;
	private final IvParameterSpec fIV;

	private String encrypt ( String data ) throws ConfigDbException
	{
		try
		{
			final Cipher c = Cipher.getInstance ( fAlgo );
			c.init ( Cipher.ENCRYPT_MODE, fKey, fIV );
			final byte[] encVal = c.doFinal ( data.getBytes () );
			return rrConvertor.base64Encode ( encVal );
		}
		catch ( GeneralSecurityException e )
		{
			throw new ConfigDbException ( e );
		}
	}

	private String decrypt ( String data ) throws ConfigDbException
	{
		try
		{
			final Cipher c = Cipher.getInstance ( fAlgo );
			c.init ( Cipher.DECRYPT_MODE, fKey, fIV );
			final byte[] dataBytes = rrConvertor.base64Decode ( data );
			byte[] decValue = c.doFinal ( dataBytes );
			return new String ( decValue );
		}
		catch ( GeneralSecurityException e )
		{
			throw new ConfigDbException ( e );
		}
	}
}
