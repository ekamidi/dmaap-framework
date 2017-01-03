/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.logging.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.MDC;

import com.att.nsa.logging.LoggingContext;

/**
 * A logging context for SLF4J
 *
 */
public class Slf4jLoggingContext implements LoggingContext
{
	public Slf4jLoggingContext ( LoggingContext base )
	{
		fBase = base;
		if ( fBase != null )
		{
			// pull any existing settings into our MDC
			final HashMap<String,String> map = new HashMap<String,String> ();
			fBase.populate ( map );
			for ( Map.Entry<String,String> e : map.entrySet () )
			{
				mdcWrite ( e.getKey(), e.getValue () );
			}
		}
	}

	@Override
	public LoggingContext clear ( String key )
	{
		synchronized ( this )
		{
			fLocalData.remove ( key );
		}
		onUpdate ( this, key, null );
		return this;
	}

	@Override
	public Slf4jLoggingContext put ( String key, String value )
	{
		synchronized ( this )
		{
			fLocalData.put ( key, value );
		}
		onUpdate ( this, key, value );
		return this;
	}

	@Override
	public Slf4jLoggingContext put ( String key, long value )
	{
		return put ( key, Long.toString ( value ) );
	}

	@Override
	public String get ( String key, String defaultValue )
	{
		synchronized ( this )
		{
			if ( fLocalData.containsKey ( key ) )
			{
				return fLocalData.get ( key );
			}
		}
		if ( fBase != null )
		{
			return fBase.get ( key, defaultValue );
		}
		else return defaultValue;
	}

	@Override
	public long get ( String key, long defaultValue )
	{
		final String str = get ( key, Long.toString ( defaultValue ) );
		try
		{
			return Long.parseLong ( str );
		}
		catch ( NumberFormatException x )
		{
			return defaultValue;
		}
	}

	@Override
	public void addListener ( LoggingContext listener )
	{
		fListeners.add ( listener );
	}

	@Override
	public void onUpdate ( LoggingContext source, String key, String val )
	{
		for ( LoggingContext ctx : fListeners )
		{
			ctx.onUpdate ( source, key, val );
		}

		// now flash to MDC
		final String current = get ( key, null );
		if ( current != null )
		{
			mdcWrite ( key, current );
		}
		else
		{
			mdcWrite ( key, null );
		}
	}

	@Override
	public void populate ( Map<String, String> map )
	{
		if ( fBase != null ) fBase.populate ( map );
		synchronized ( this )
		{
			for ( Entry<String, String> e : fLocalData.entrySet () )
			{
				map.put ( e.getKey(),  e.getValue () );
			}
		}
	}

	private void mdcWrite ( String key, String val )
	{
//		System.out.println ( "thread " + Thread.currentThread ().getId () + " / " + key + " / " + ( val == null ? "<null>" : val ) );
		if ( val != null )
		{
			MDC.put ( key, val );
		}
		else
		{
			MDC.remove ( key );
		}
	}
	
	private final LoggingContext fBase;
	private final HashMap<String,String> fLocalData = new HashMap<String,String> ();
	private final LinkedList<LoggingContext> fListeners = new LinkedList<LoggingContext> ();
}
