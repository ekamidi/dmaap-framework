/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.metrics.impl;

import com.att.nsa.clock.SaClock;

/**
 * Time since a starting point.
 * 
 *
 */
public class CdmTimeSince extends CdmSimpleMetric
{
	public CdmTimeSince ( String desc )
	{
		this ( SaClock.now(), desc );
	}

	public CdmTimeSince ( long startTimeMs, String desc )
	{
		fStartedMs = startTimeMs;
		fDescription = desc;
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

	public String getUnits ()
	{
		return fDescription;
	}

	public long getValue ()
	{
		return ( SaClock.now() - fStartedMs ) / 1000;
	}

	@Override
	public synchronized String summarize ()
	{
		return "" + getValue() + " " + fDescription;
	}

	@Override
	public synchronized void reset ()
	{
	}

	private final long fStartedMs;
	private final String fDescription;
}
