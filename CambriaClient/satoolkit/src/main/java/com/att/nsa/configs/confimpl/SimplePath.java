/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.configs.confimpl;

import com.att.nsa.configs.ConfigPath;

public class SimplePath implements ConfigPath
{
	public static SimplePath getRootPath ()
	{
		return new SimplePath ( null, "" );
	}

	public SimplePath ( SimplePath parent, String name )
	{
		fParent = parent;
		fName = name;

		if ( fName.contains ( "/" ))
		{
			throw new IllegalArgumentException ( "Name can't contain '/'." );
		}
	}

	public static SimplePath parse ( String pathAsString )
	{
		// if it's the root, handle that specially
		if ( pathAsString.equals ( "/" ) )
		{
			return getRootPath ();
		}

		if ( !pathAsString.startsWith("/") )
		{
			// absolute only
			throw new IllegalArgumentException ( "Path must begin with '/'." );
		}

		// remove the starting slash pre-split
		pathAsString = pathAsString.substring ( 1 );
		
		// remove any trailing slash
		if ( pathAsString.endsWith ( "/" ))
		{
			pathAsString = pathAsString.substring ( 0, pathAsString.length () - 1 );
		}
		
		// names can't contain slash, so split on that
		final String[] segments = pathAsString.split ( "/" );

		// walk down path
		SimplePath current = getRootPath();
		for ( String segment : segments )
		{
			if ( segment.length () < 1 )
			{
				throw new IllegalArgumentException ( "Path segments may not be empty." );
			}
			current = new SimplePath ( current, segment );
		}

		return current;
	}
	
	@Override
	public SimplePath getParent ()
	{
		return fParent;
	}

	@Override
	public String getName ()
	{
		return fName;
	}

	@Override
	public String toString ()
	{
		if ( getParent() == null ) return "/";
		return getParent().getParentName() + getName();
	}

	/**
	 * Return this node's name as a parent (w. a trailing slash)
	 * @return
	 */
	public String getParentName ()
	{
		final SimplePath parent = getParent ();
		if ( parent == null ) return "/";
		return parent.getParentName () + getName () + "/";
	}

	@Override
	public ConfigPath getChild ( String name )
	{
		return new SimplePath ( this, name );
	}

	@Override
	public int hashCode ()
	{
		return toString().hashCode ();
	}

	@Override
	public boolean equals ( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass () != obj.getClass () )
			return false;
		SimplePath other = (SimplePath) obj;

		return toString().equals ( other.toString () );
	}

	@Override
	public int compareTo ( ConfigPath that )
	{
		return toString().compareTo ( that.toString () );
	}

	private final SimplePath fParent;
	private final String fName;
}
