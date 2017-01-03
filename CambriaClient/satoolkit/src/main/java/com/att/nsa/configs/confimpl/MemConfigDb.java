/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.configs.confimpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.att.nsa.clock.SaClock;
import com.att.nsa.configs.ConfigDb;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.configs.ConfigPath;

/**
 * This is a test-only implementation of a config db, because its
 * content is not persisted.
 */
public class MemConfigDb implements ConfigDb
{
	public MemConfigDb ()
	{
		fMap = new HashMap<String,Entry> ();
	}

	@Override
	public ConfigPath getRoot ()
	{
		return SimplePath.getRootPath ();
	}

	@Override
	public ConfigPath parse ( String pathAsString )
	{
		return SimplePath.parse ( pathAsString );
	}

	@Override
	public boolean exists ( ConfigPath path )
	{
		return fMap.containsKey ( path.toString () );
	}

	@Override
	public String load ( ConfigPath path )
	{
		final Entry e = fMap.get ( path.toString () );
		return e == null ? null : e.fData;
	}

	@Override
	public Set<ConfigPath> loadChildrenNames ( ConfigPath key )
	{
		final TreeSet<ConfigPath> result = new TreeSet<ConfigPath> ();
		final String prefix = key.toString () + "/";
		for ( String someKey : fMap.keySet () )
		{
			if ( someKey.startsWith ( prefix ) )
			{
				// but make sure it's an immediate child
				final ConfigPath child = parse ( someKey );
				if ( child.getParent ().equals ( key ) )
				{
					result.add ( child );
				}
			}
		}
		return result;
	}

	@Override
	public Map<ConfigPath, String> loadChildrenOf ( ConfigPath key )
	{
		// just do this in two passes for simplicity
		final HashMap<ConfigPath,String> result = new HashMap<ConfigPath,String> (); 
		for ( ConfigPath child : loadChildrenNames ( key ) )
		{
			final String data = load ( child );
			result.put ( child, data );
		}
		return result;
	}

	@Override
	public void store ( ConfigPath path, String data )
	{
		buildPath ( path.getParent() );
		fMap.put ( path.toString(), new Entry ( data ) );
	}

	@Override
	public boolean clear ( ConfigPath path )
	{
		final String prefix = path.toString ();
		boolean removed = ( null != fMap.remove ( prefix ) );

		final TreeSet<String> removals = new TreeSet<String> ();
		final String prefixAsParent = prefix + "/";
		for ( String key : fMap.keySet () )
		{
			if ( key.startsWith ( prefixAsParent ) )
			{
				removals.add ( key );
			}
		}
		for ( String key : removals )
		{
			fMap.remove ( key );
			removed = true;
		}

		return removed;
	}

	@Override
	public long getLastModificationTime ( ConfigPath path ) throws ConfigDbException
	{
		final Entry e = fMap.get ( path.toString () );
		return e == null ? -1 : e.fTimeMs;
	}

	private class Entry
	{
		public Entry ( String data ) { fData = data; fTimeMs = SaClock.now (); }
		public final String fData;
		public final long fTimeMs;
	}

	private final HashMap<String, Entry> fMap;

	// build a node for each parent that doesn't exist, using null as the data value
	private void buildPath ( ConfigPath path )
	{
		if ( path == null ) return;

		buildPath ( path.getParent () );

		final String key = path.toString ();
		if ( !fMap.containsKey ( key ) )
		{
			fMap.put ( key, null );
		}
	}
}

