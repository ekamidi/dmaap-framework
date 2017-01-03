/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.limits;

import java.util.Set;
import java.util.TreeSet;

import org.json.JSONArray;

import com.att.nsa.configs.ConfigDb;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.configs.ConfigPath;

/**
 * A blacklist is really just a simple set of strings.
 *
 */
public class Blacklist
{
	public Blacklist ( ConfigDb db, ConfigPath path ) throws ConfigDbException
	{
		fSet = new TreeSet<String> ();
		fDb = db;
		fPath = path;

		load ();
	}

	public void add ( String ip ) throws ConfigDbException
	{
		if ( ip != null && ip.length () > 0 )
		{
			if ( fSet.add ( ip ) )
			{
				store ();
			}
		}
	}

	public boolean contains ( String ip )
	{
		return fSet.contains ( ip );
	}

	public void remove ( String ip ) throws ConfigDbException
	{
		if ( ip != null && ip.length () > 0 )
		{
			if ( fSet.remove ( ip ) )
			{
				store ();
			}
		}
	}

	public Set<?> asSet ()
	{
		return new TreeSet<String> ( fSet );
	}

	private final TreeSet<String> fSet;
	private final ConfigDb fDb;
	private final ConfigPath fPath;

	private void load () throws ConfigDbException
	{
		final String data = fDb.load ( fPath );
		if ( data != null )
		{
			final JSONArray a = new JSONArray ( data );
			for ( int i=0; i<a.length (); i++ )
			{
				final Object item = a.get ( i );
				if ( item != null )
				{
					fSet.add ( item.toString () );
				}
			}
		}
	}

	private void store () throws ConfigDbException
	{
		final JSONArray a = new JSONArray ();
		for ( String item : fSet )
		{
			a.put ( item );
		}
		fDb.store ( fPath, a.toString () );
	}
}
