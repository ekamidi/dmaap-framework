/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.metrics.impl;

/**
 * A level is an amount that can rise and fall.
 * 
 *
 */
public class CdmHighWater extends CdmSimpleMetric
{
	public CdmHighWater ( String desc )
	{
		fDescription = desc;
		fHiWater = 0;
	}

	@Override
	public String getRawValueString ()
	{
		return "" + getValue();
	}

	@Override
	public Number getRawValue ()
	{
		return getValue ();
	}

	public void setCurrent ( long amt )
	{
		if ( amt > fHiWater )
		{
			fHiWater = amt;
			fTime = System.currentTimeMillis ();
		}
	}

	public String getUnits ()
	{
		return fDescription;
	}

	public long getValue ()
	{
		return fHiWater;
	}

	@Override
	public synchronized String summarize ()
	{
		return "" + fHiWater + " " + fDescription + ", " +
			( ( System.currentTimeMillis () - fTime ) / 1000 ) + " seconds" ;
			// humanReadableHelper.elapsedTimeSince ( new Date ( fTime ) );
	}

	@Override
	public synchronized void reset ()
	{
		fHiWater = 0;
	}

	private final String fDescription;
	private long fHiWater;
	private long fTime;
}
