/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.configs.confimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

import com.att.nsa.configs.ConfigDb;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.configs.ConfigPath;

public class ZkConfigDb implements ConfigDb
{
	public ZkConfigDb ( String zkServers )
	{
		this ( zkServers, zkStandardRoot );
	}

	public ZkConfigDb ( String zkServers, String rootZkNode )
	{
		fZkConnection = new ZkConnection ( zkServers );
		zkClient = new ZkClient ( fZkConnection );
		zkRoot = rootZkNode == null ? "" : rootZkNode;

		if ( rootZkNode.length() > 0 && !zkClient.exists(zkRoot) )
		{
			zkClient.createPersistent ( zkRoot, true );
		}
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
		return zkClient.exists ( pathToZk ( path ) );
	}

	@Override
	public String load ( ConfigPath path )
	{
		if ( !exists ( path ) )
		{
			return null;
		}
		return zkClient.readData ( pathToZk ( path ) );
	}

	@Override
	public Set<ConfigPath> loadChildrenNames ( ConfigPath key )
	{
		final TreeSet<ConfigPath> result = new TreeSet<ConfigPath> ();
		if ( exists ( key ) )
		{
			final List<String> children = zkClient.getChildren ( pathToZk ( key ) );
			for ( String child : children )
			{
				result.add ( key.getChild ( child ) );
			}
		}
		return result;
	}

	@Override
	public Map<ConfigPath, String> loadChildrenOf ( ConfigPath key )
	{
		final HashMap<ConfigPath,String> result = new HashMap<ConfigPath,String> ();
		for ( ConfigPath cp : loadChildrenNames ( key ) )
		{
			result.put ( cp, load ( cp ) );
		}
		return result;
	}

	@Override
	public void store ( ConfigPath path, String data )
	{
		if ( !exists ( path ) )
		{
			zkClient.createPersistent ( pathToZk ( path ), true );
		}
		zkClient.writeData ( pathToZk ( path ), data );
	}

	@Override
	public boolean clear ( ConfigPath path )
	{
		// per interface, this is required to be recursive

		for ( ConfigPath child : loadChildrenNames ( path ) )
		{
			clear ( child );
		}
		return zkClient.delete ( pathToZk ( path ) );
	}

	@Override
	public long getLastModificationTime ( ConfigPath path ) throws ConfigDbException
	{
		try
		{
			final Stat stat = fZkConnection.getZookeeper ().exists( pathToZk ( path ), false);
			if ( stat != null )
			{
				return stat.getMtime () / 1000;
			}
			return -1;
		}
		catch ( KeeperException e )
		{
			throw new ConfigDbException ( e );
		}
		catch ( InterruptedException e )
		{
			throw new ConfigDbException ( e );
		}
	}

	private final ZkConnection fZkConnection;
	private final ZkClient zkClient;
	private final String zkRoot;

	private static final String zkStandardRoot = "/fe3c";

	private String pathToZk ( ConfigPath key )
	{
		return zkRoot + key.toString ();
	}
}
