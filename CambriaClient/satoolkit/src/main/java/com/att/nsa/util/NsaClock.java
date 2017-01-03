/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.util;

/**
 * @deprecated use SaClock
 */
@Deprecated
public abstract class NsaClock
{
	/**
	 * Get the current time from the system clock.
	 * @return the current time in milliseconds. (Normally equivalent to System.currentTimeMillis())
	 */
	public static long now ()
	{
		return getSystemClock().getCurrentMs ();
	}

	public synchronized static void setSystemClock ( NsaClock clock )
	{
		if ( sfClock != null ) throw new IllegalStateException ( "The clock was already set." );
		sfClock = clock;
	}

	public static synchronized NsaClock getSystemClock ()
	{
		if ( sfClock == null )
		{
			setSystemClock ( new NsaJvmClock () );
		}
		return sfClock;
	}

	public abstract long getCurrentMs ();

	private static NsaClock sfClock = null;
}
