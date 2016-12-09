/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.metrics.impl;

/**
 * A level is an amount that can rise and fall.
 * 
 *
 */
public class CdmLevel extends CdmSimpleMetric
{
	public CdmLevel ( String desc )
	{
		fDescription = desc;
		fCount = 0;
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

	public void up ()
	{
		adjust ( 1 );
	}

	public void up ( long amt )
	{
		adjust ( amt );
	}

	public void down ()
	{
		adjust ( -1 );
	}

	public void down ( long amt )
	{
		adjust ( -1 * amt );
	}

	public synchronized void adjust ( long amt )
	{
		fCount += amt;
	}

	public String getUnits ()
	{
		return fDescription;
	}

	public long getValue ()
	{
		return fCount;
	}

	@Override
	public synchronized String summarize ()
	{
		return "" + fCount + " " + fDescription;
	}

	@Override
	public synchronized void reset ()
	{
		fCount = 0;
	}

	private final String fDescription;
	private long fCount;
}
