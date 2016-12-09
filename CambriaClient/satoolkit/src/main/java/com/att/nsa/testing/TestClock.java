/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.testing;

import com.att.nsa.clock.SaClock;

/**
 * A clock for use in unit test. Instantiate this clock before using the SaClock
 * interface in your tests to control the time explicitly. (Obviously code using
 * System.currentTimeMillis() is not affected.)
 * 
 *
 */
public class TestClock extends SaClock
{
	public TestClock ()
	{
		this ( 0 );
	}

	public TestClock ( long timeMs )
	{
		fNowMs = timeMs;

		setClock ( this );
	}

	@Override
	public String toString ()
	{
		return "Test Clock, now: " + super.nowAsString ();
	}

	public synchronized void setTimeMs ( long ms )
	{
		fNowMs = ms;
	}
	
	@Override
	public synchronized long currentTimeMs ( boolean adjustedForUniqueness )
	{
		return fNowMs;
	}

	public synchronized long forward ( long ms )
	{
		setTimeMs ( currentTimeMs ( false ) + ms );
		return currentTimeMs ( false );
	}
	
	private long fNowMs;
}
