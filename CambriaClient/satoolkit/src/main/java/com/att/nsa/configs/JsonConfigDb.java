/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.configs;

import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.att.nsa.data.json.SaJsonTokener;

/**
 * A lightweight wrapper around a configdb for systems that store JSON
 * @author peter
 *
 */
public class JsonConfigDb implements ConfigDb
{
	/**
	 * Construct a JsonConfigDb wrapper over another config db.
	 * @param db
	 */
	public JsonConfigDb ( ConfigDb db )
	{
		fDb = db;
	}

	/**
	 * Load the configuration object's data as a JSON object. If the loaded data is not valid
	 * JSON, a ConfigDbException is thrown.
	 * 
	 * @param key
	 * @return a JSON object or null
	 * @throws ConfigDbException
	 */
	public JSONObject loadJson ( ConfigPath key ) throws ConfigDbException
	{
		try
		{
			final String value = load ( key );
			return value == null ? null : new JSONObject ( new SaJsonTokener ( value ) );
		}
		catch ( JSONException e )
		{
			throw new ConfigDbException ( e );
		}
	}

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
	public ConfigPath getRoot () { return fDb.getRoot (); }

	@Override
	public ConfigPath parse ( String pathAsString ){ return fDb.parse ( pathAsString ); }

	@Override
	public boolean exists ( ConfigPath path ) throws ConfigDbException { return fDb.exists ( path ); }

	@Override
	public String load ( ConfigPath key ) throws ConfigDbException { return fDb.load ( key ); }

	@Override
	public Set<ConfigPath> loadChildrenNames ( ConfigPath key ) throws ConfigDbException { return fDb.loadChildrenNames ( key ); }

	@Override
	public Map<ConfigPath, String> loadChildrenOf ( ConfigPath key ) throws ConfigDbException { return fDb.loadChildrenOf ( key ); }

	@Override
	public void store ( ConfigPath key, String data ) throws ConfigDbException { fDb.store ( key, data ); }

	@Override
	public boolean clear ( ConfigPath key ) throws ConfigDbException { return fDb.clear ( key ); }

	@Override
	public long getLastModificationTime ( ConfigPath path ) throws ConfigDbException { return fDb.getLastModificationTime ( path ); }

	private ConfigDb fDb;
	private static final boolean sfPrettyStore = Boolean.parseBoolean ( System.getProperty ( "configdb.pretty", "false" ) );
}
