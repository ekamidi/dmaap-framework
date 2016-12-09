/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.configs.confimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;

import com.att.nsa.configs.ConfigDb;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.configs.ConfigPath;
import com.att.nsa.util.StreamTools;

/**
 * Configuration DB persisted in a disk direcory.
 */
public class FileSystemConfigDb implements ConfigDb
{
	public FileSystemConfigDb ( File baseDir )
	{
		fBaseDir = baseDir;
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
		return makeNodeDirFile ( path ).exists ();
	}

	@Override
	public String load ( ConfigPath path ) throws ConfigDbException
	{
		try
		{
			final File f = makeNodeDirFile ( path );
			if ( f.exists () )
			{
				final File data = makeNodeDataFile ( f );
				if ( data.exists () )
				{
					final FileInputStream fis = new FileInputStream ( data );
					try
					{
						final byte[] bytes = StreamTools.readBytes ( fis );
						return new String ( bytes );
					}
					finally
					{
						fis.close ();
					}
				}
				else
				{
					// node exists, just data file doesn't. so it's empty.
					return "";
				}
			}
			// else: return null per interface contract
		}
		catch ( IOException e )
		{
			throw new ConfigDbException ( e );
		}
		return null;
	}

	@Override
	public Set<ConfigPath> loadChildrenNames ( ConfigPath path )
	{
		final File f = makeNodeDirFile ( path );
		if ( f.exists () )
		{
			final TreeSet<ConfigPath> result = new TreeSet<ConfigPath> ();
			for ( File child : f.listFiles () )
			{
				if ( child.isDirectory () )
				{
					result.add ( path.getChild ( child.getName() ) );
				}
				// else: only directories matter
			}
			return result;
		}
		// else: return null per interface contract

		return null;
	}

	@Override
	public Map<ConfigPath, String> loadChildrenOf ( ConfigPath key ) throws ConfigDbException
	{
		// just do this in two passes for simplicity
		final HashMap<ConfigPath,String> result = new HashMap<ConfigPath,String> ();
		final Set<ConfigPath> names = loadChildrenNames ( key );
		if ( names != null )
		{
			for ( ConfigPath child : loadChildrenNames ( key ) )
			{
				final String data = load ( child );
				result.put ( child, data );
			}
		}
		return result;
	}

	@Override
	public void store ( ConfigPath path, String content ) throws ConfigDbException
	{
		final File dir = makeNodeDirFile ( path );
		if ( dir.mkdirs () || dir.isDirectory () )
		{
			try
			{
				final File data = makeNodeDataFile ( dir );
				final FileOutputStream fos = new FileOutputStream ( data );
				try
				{
					fos.write ( content.getBytes () );
				}
				finally
				{
					fos.close ();
				}
			}
			catch ( IOException e )
			{
				throw new ConfigDbException ( e );
			}
		}
		else
		{
			throw new ConfigDbException ( "Couldn't create configuration data path." );
		}
	}

	@Override
	public boolean clear ( ConfigPath path ) throws ConfigDbException
	{
		try
		{
			final File data = makeNodeDirFile ( path );
			FileUtils.deleteDirectory ( data );
			return true;
		}
		catch ( IOException e )
		{
			throw new ConfigDbException ( e );
		}
	}

	@Override
	public long getLastModificationTime ( ConfigPath path ) throws ConfigDbException
	{
		final File f = makeNodeDirFile ( path );
		if ( f.exists () )
		{
			final File data = makeNodeDataFile ( f );
			if ( data.exists () )
			{
				final long timeMs = data.lastModified ();
				return timeMs < 1 ? -1 : (timeMs / 1000);
			}
		}
		return -1;
	}

	private final File fBaseDir;

	private File makeNodeDirFile ( ConfigPath path )
	{
		final ConfigPath p = path.getParent ();
		if ( p == null )
		{
			return fBaseDir;
		}
		return new File ( makeNodeDirFile ( p ), path.getName () ); 
	}

	private File makeNodeDataFile ( File parentFile )
	{
		return new File ( parentFile, kDataFile );
	}

	private static final String kDataFile = "data.txt";
}

