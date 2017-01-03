/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.metrics.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.json.JSONObject;

import com.att.nsa.metrics.CdmMeasuredItem;
import com.att.nsa.metrics.CdmMetricsRegistry;

/**
 * A registry for metrics.
 * 
 *
 */
public class CdmMetricsRegistryImpl implements CdmMetricsRegistry
{
	public CdmMetricsRegistryImpl ()
	{
		fItems = new HashMap<String,CdmMeasuredItem> ();
		fQueue = new DelayQueue<CdmMeasuredItem> ();
		fEvaluator = new evalThread ();
	}

	@PostConstruct
	public void start ()
	{
		fEvaluator.start ();
	}
	
	@Override
	public synchronized void putItem ( String named, CdmMeasuredItem mi )
	{
		removeItem ( named );

		fItems.put ( named, mi );
		if ( mi.requiresScheduledEvaluation () )
		{
			fQueue.add ( mi );
		}
	}

	@Override
	public synchronized CdmMeasuredItem getItem ( String named )
	{
		return fItems.get ( named );
	}

	@Override
	public synchronized void removeItem ( String named )
	{
		final CdmMeasuredItem mi = fItems.get ( named );
		if ( mi != null )
		{
			fQueue.remove ( mi );
		}
	}

	@Override
	public synchronized Map<String,CdmMeasuredItem> getItems ()
	{
		final HashMap<String,CdmMeasuredItem> items = new HashMap<String,CdmMeasuredItem> ();
		items.putAll ( fItems );
		return items;
	}

	@Override
	public synchronized int size ()
	{
		return fItems.size ();
	}

	@Override
	public List<? extends CdmMetricEntry> getEntries ()
	{
		final LinkedList<String> names = new LinkedList<String> ();
		final HashMap<String,metricEntry> map = new HashMap<String,metricEntry> ();
		for ( Entry<String, CdmMeasuredItem> e : fItems.entrySet () )
		{
			final String name = e.getKey ();
			
			int dot = 0;
			int start = 0;
			int level = 0;

			while ( dot != -1 )
			{
				dot = name.indexOf ( '.', start );
				if ( dot > -1 )
				{
					final String part = name.substring ( 0, dot );
					if ( !map.containsKey ( part ) )
					{
						map.put ( part, new metricEntry ( part, part, level ) );
						names.add ( part );
					}
					start = dot + 1;
					level++;
				}
			}

			if ( !map.containsKey ( name ) )
			{
				map.put ( name, new metricEntry ( name, name, level, e.getValue () ) );
				names.add ( name );
			}
		}

		Collections.sort ( names );
		final LinkedList<metricEntry> result = new LinkedList<metricEntry> ();
		for ( String name : names )
		{
			result.add ( map.get ( name ) );
		}
		return result;
	}

	private final HashMap<String,CdmMeasuredItem> fItems;
	private final DelayQueue<CdmMeasuredItem> fQueue;
	private evalThread fEvaluator;
	private static final Logger log = Logger.getLogger ( CdmMetricsRegistryImpl.class.getName () );

	private class evalThread extends Thread
	{
		public evalThread ()
		{
			setDaemon ( true );
		}

		@Override
	    public void run ()
	    {
			log.info ( "Metrics registry eval thread starting." );
			while ( true )
			{
				try
				{
					final CdmMeasuredItem mi = fQueue.poll ( 5, TimeUnit.SECONDS );
					if ( mi != null )
					{
						// do something
						mi.poll ();

						// then put it back in the queue for later
						fQueue.add ( mi );
					}
				}
				catch ( InterruptedException e )
				{
					log.warning ( "Metrics registry eval thread interrupted." );
				}
			}
	    }
	}

	public static class metricEntry implements CdmMetricEntry
	{
		public metricEntry ( String localName, String name, int level )
		{
			this ( localName, name, level, null );
		}

		public metricEntry ( String localName, String name, int level, CdmMeasuredItem item )
		{
			fLocalName = localName;
			fName = name;
			fValue = item;
			fLevel = level;
		}

		@Override
		public String getLocalName () { return fLocalName; }

		@Override
		public String getName () { return fName; }

		@Override
		public CdmMeasuredItem getValue () { return fValue; }

		@Override
		public boolean hasValue () { return fValue != null; }

		@Override
		public int getLevel () { return fLevel; }

		private final String fLocalName;
		private final String fName;
		private final CdmMeasuredItem fValue;
		private final int fLevel;
	}

	@Override
	public JSONObject toJson ()
	{
		final JSONObject result = new JSONObject ();
		for ( Entry<String, CdmMeasuredItem> e : getItems ().entrySet () )
		{
			final JSONObject entry = new JSONObject ();
			try
			{
				entry.put ( "summary", e.getValue().summarize () );
				
				final Number asNumber = e.getValue ().getRawValue ();
				if ( asNumber != null )
				{
					if ( asNumber instanceof Long )
					{
						entry.put ( "raw", asNumber.longValue () );
					}
					else if ( asNumber instanceof Integer )
					{
						entry.put ( "raw", asNumber.intValue () );
					}
					else if ( asNumber instanceof Double )
					{
						entry.put ( "raw", asNumber.doubleValue () );
					}
					else
					{
						entry.put ( "raw", asNumber.toString () );
					}
				}
				else
				{
					entry.put ( "raw", e.getValue().getRawValueString () );
				}
				result.put ( e.getKey(), entry );
			}
			catch ( Throwable t )
			{
				// ?
				result.put ( e.getKey(), "error" );
			}
		}
    	return result;
	}
}
