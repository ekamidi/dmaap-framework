/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.timedata.impl.mem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.att.nsa.clock.SaClock;
import com.att.nsa.timedata.TimeSeriesDb;
import com.att.nsa.timedata.TimeSeriesEntry;

public class MemTsDb<T> implements TimeSeriesDb<T>
{
	public MemTsDb ()
	{
		fValues = new HashMap<String,ArrayList<MemEntry<T>>> ();
	}

	@Override
	public synchronized void clear ()
	{
		fValues.clear ();
	}
	
	@Override
	public synchronized void clear ( String entityId )
	{
		fValues.remove ( entityId );
	}

	@Override
	public synchronized void clearOlderThan ( String entityId, long epochTimestamp )
	{
		final List<MemEntry<T>> list = get ( entityId, epochTimestamp, SaClock.now () );
		if ( list == null )
		{
			clear ( entityId );
		}
		else
		{
			clear ( entityId );
			for ( MemEntry<T> e : list )
			{
				put ( entityId, e.getEpochTimestamp (), e.getValue () );
			}
		}
	}

	@Override
	public synchronized void put ( String entityId, long epochTimestamp, T value )
	{
		if ( !fValues.containsKey ( entityId ) )
		{
			fValues.put ( entityId, new ArrayList<MemEntry<T>> () );
		}
		final ArrayList<MemEntry<T>> values = fValues.get ( entityId );
		if ( values.size () > 0 && epochTimestamp < values.get ( values.size() - 1 ).getEpochTimestamp () )
		{
			throw new IllegalArgumentException ( "You must add a newer time series entry." );
		}
		values.add ( new MemEntry<T> ( epochTimestamp, value ) );
	}

	@Override
	public synchronized TimeSeriesEntry<T> get ( String entityId, long epochTimestamp )
	{
		final ArrayList<MemEntry<T>> values = fValues.get ( entityId );
		if ( values == null ) return null;

		final int index = getIndexOf ( values, epochTimestamp );
		return index >= 0 ? values.get ( index ) : null;
	}

	@Override
	public synchronized List<MemEntry<T>> get ( String entityId, long epochStart, long epochEnd )
	{
		final LinkedList<MemEntry<T>> result = new LinkedList<MemEntry<T>> (); 

		final ArrayList<MemEntry<T>> values = fValues.get ( entityId );
		if ( values == null ) return result;

		final int index = getIndexOf ( values, epochStart );
		int startIndex = -1;
		if ( index >= 0 )
		{
			// found exact match
			startIndex = index;
		}
		else
		{
			// didn't find, but index is (-(insertion point) - 1).
			startIndex = -1 * ( index + 1 );
		}

		// forward thru list until the timestamp is > epochEnd
		int current = startIndex;
		boolean foundEnd = false;
		while ( current < values.size() && !foundEnd )
		{
			final MemEntry<T> me = values.get ( current++ );
			foundEnd = me.getEpochTimestamp () > epochEnd;
			if ( !foundEnd )
			{
				result.add ( me );
			}
		}

		return result;
	}

	private final HashMap<String,ArrayList<MemEntry<T>>> fValues;

	private static <T> int getIndexOf ( ArrayList<MemEntry<T>> values, long epochTimestamp )
	{
		final MemEntry<T> key = new MemEntry<T> ( epochTimestamp, null );
		final int index = Collections.binarySearch ( values, key, new Comparator<MemEntry<T>> ()
		{
			@Override
			public int compare ( MemEntry<T> o1, MemEntry<T> o2 )
			{
				return Long.compare ( o1.getEpochTimestamp (), o2.getEpochTimestamp () );
			}
		} );
		return index;
	}
}
