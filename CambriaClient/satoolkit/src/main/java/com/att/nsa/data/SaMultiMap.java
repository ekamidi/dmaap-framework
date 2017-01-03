/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.data;

import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SaMultiMap<K,V>
{
	public SaMultiMap ()
	{
		fMultiMap = new Hashtable<K,List<V>> ();
	}

	public SaMultiMap ( SaMultiMap<K,V> mm )
	{
		fMultiMap = new Hashtable<K,List<V>> ();
		for ( Entry<K, List<V>> m : mm.getValues ().entrySet () )
		{
			fMultiMap.put ( m.getKey(), m.getValue () );
		}
	}

	@Override
	public String toString ()
	{
		return fMultiMap.toString ();
	}
	
	public synchronized void put ( K k )
	{
		getOrCreateFor ( k );
	}

	public synchronized void put ( K k, V v )
	{
		LinkedList<V> list = new LinkedList<V>();
		list.add ( v );
		put ( k, list );
	}

	public synchronized void put ( K k, Collection<V> v )
	{
		List<V> itemList = getOrCreateFor ( k );
		itemList.removeAll ( v );	// only one of a given value allowed
		itemList.addAll ( v );
	}

	public synchronized boolean containsKey ( K k )
	{
		return fMultiMap.containsKey ( k );
	}

	/**
	 * Get a list of values for the given key. Note this is a copy of the data
	 * stored in the map -- changes to it do not impact the map.
	 * @param k
	 * @return a list of values
	 */
	public synchronized List<V> get ( K k )
	{
		List<V> itemList = new LinkedList<V> ();
		if ( fMultiMap.containsKey ( k ) )
		{
			itemList = getOrCreateFor ( k );
		}
		return itemList;
	}

	public synchronized Collection<K> getKeys ()
	{
		return fMultiMap.keySet ();
	}

	public synchronized Map<K,List<V>> getValues ()
	{
		return fMultiMap;
	}

	public synchronized List<V> remove ( K k )
	{
		return fMultiMap.remove ( k );
	}

	public synchronized void remove ( K k, V v )
	{
		List<V> itemList = getOrCreateFor ( k );
		itemList.remove ( v );
	}

	public synchronized void clear ()
	{
		fMultiMap.clear ();
	}

	public synchronized int size ()
	{
		return fMultiMap.size ();
	}

	public synchronized int size ( K k )
	{
		return getOrCreateFor ( k ).size ();
	}

	public int valueCount ()
	{
		int size = 0;
		for ( List<V> l : fMultiMap.values () )
		{
			size += l.size();
		}
		return size;
	}

	private final Hashtable<K,List<V>> fMultiMap;

	private synchronized List<V> getOrCreateFor ( K k )
	{
		List<V> itemList = fMultiMap.get ( k );
		if ( itemList == null )
		{
			itemList = new LinkedList<V> ();
			fMultiMap.put ( k, itemList );
		}
		return itemList;
	}
}
