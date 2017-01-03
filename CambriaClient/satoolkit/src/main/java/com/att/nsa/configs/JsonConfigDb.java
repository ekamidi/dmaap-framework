/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.configs;

import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A lightweight wrapper around a configdb for systems that store JSON
 *
 */
public class JsonConfigDb implements ConfigDb
{
	public JsonConfigDb ( ConfigDb db )
	{
		fDb = db;
	}

	@Override
	public ConfigPath getRoot () { return fDb.getRoot (); }

	@Override
	public ConfigPath parse ( String pathAsString ){ return fDb.parse ( pathAsString ); }

	@Override
	public boolean exists ( ConfigPath path ) throws ConfigDbException { return fDb.exists ( path ); }

	@Override
	public String load ( ConfigPath key ) throws ConfigDbException { return fDb.load ( key ); }

	public JSONObject loadJson ( ConfigPath key ) throws ConfigDbException
	{
		try
		{
			return new JSONObject ( load ( key ) );
		}
		catch ( JSONException e )
		{
			throw new ConfigDbException ( e );
		}
	}

	@Override
	public Set<ConfigPath> loadChildrenNames ( ConfigPath key ) throws ConfigDbException { return fDb.loadChildrenNames ( key ); }

	@Override
	public Map<ConfigPath, String> loadChildrenOf ( ConfigPath key ) throws ConfigDbException { return fDb.loadChildrenOf ( key ); }

	@Override
	public void store ( ConfigPath key, String data ) throws ConfigDbException { fDb.store ( key, data ); }

	/**
	 * store a JSON object into the config db
	 * @param key
	 * @param data
	 * @throws ConfigDbException
	 */
	public void storeJson ( ConfigPath key, JSONObject data ) throws ConfigDbException
	{
		store ( key,
			sfPrettyStore ? data.toString ( 4 ) : data.toString ()
		);
	}

	@Override
	public boolean clear ( ConfigPath key ) throws ConfigDbException { return fDb.clear ( key ); }

	@Override
	public long getLastModificationTime ( ConfigPath path ) throws ConfigDbException { return fDb.getLastModificationTime ( path ); }

	private ConfigDb fDb;
	private static final boolean sfPrettyStore = Boolean.parseBoolean ( System.getProperty ( "configdb.pretty", "false" ) );
}
