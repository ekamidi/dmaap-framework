/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.clock;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.LoggerFactory;

/**
 * A clock that returns the current time in epoch milliseconds.
 */
public class SaClock
{
	public static SaClock getIt ()
	{
		// FIXME: safe singleton access via injection system
		if ( sClock == null )
		{
			sClock = new SaClock ();
		}
		return sClock;
	}

	/**
	 * Get the current time in millis,
	 * @return current time as milliseconds since epoch
	 */
	public static long now ()
	{
		return now ( false );
	}

	/**
	 * Get the current time in millis, optionally adjusted to be unique.
	 * @param adjustedForUniqueness
	 * @return the time in millis possibly adjusted
	 */
	public static long now ( boolean adjustedForUniqueness )
	{
		return getIt().currentTimeMs ( adjustedForUniqueness );
	}

	/**
	 * Get the current time formatted as a string. This is a convenience
	 * method and the string format is fixed to "yyyy-MM-dd'T'HH:mm:ss.000+00:00" 
	 * @return the current time as a string
	 */
	public static String nowAsString ()
	{
		return sdf.format ( new Date ( now () ) );
	}

	/**
	 * Get the current time in milliseconds since the epoch, optionally adjusted
	 * to be unique. If a prior call returned the same value, a millisecond is added.
	 * This can float the clock forward substantially under load.
	 * 
	 * @param adjustedForUniqueness
	 * @return the current time in milliseconds since the epoch
	 */
	public long currentTimeMs ( boolean adjustedForUniqueness )
	{
		final long actual = System.currentTimeMillis ();
		if ( adjustedForUniqueness )
		{
			if ( fUniqueTimes && actual <= fLastIssue )
			{
				if ( fLastIssue > actual + 1000 )
				{
					LoggerFactory.getLogger ( SaClock.class ).warn ( "The SaClock is more than a second ahead of actual time." );
				}
				fLastIssue = fLastIssue + 1;
			}
			else
			{
				fLastIssue = actual;
			}
			return fLastIssue;
		}
		return actual;
	}

	private long fLastIssue;
	private final boolean fUniqueTimes;

	protected SaClock ()
	{
		this ( true );
	}

	protected SaClock ( boolean uniqueTimes )
	{
		fLastIssue = -1;
		fUniqueTimes = uniqueTimes;
	}
	
	protected synchronized static void setClock ( SaClock cc )
	{
		sClock = cc;
	}

	private static SaClock sClock = null;
	private static SimpleDateFormat sdf = new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ss.000+00:00" );
}
