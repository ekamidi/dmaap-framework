/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.metrics.impl;

public class CdmCounter extends CdmSimpleMetric
{
	public CdmCounter ( String desc )
	{
		fDescription = desc;
		fCount = 0;
	}

	public synchronized void bump ()
	{
		bumpBy ( 1 );
	}

	public synchronized void bumpBy ( long amount )
	{
		fCount += amount;
	}

	public String getUnits ()
	{
		return fDescription;
	}

	public synchronized long getValue ()
	{
		return fCount;
	}

	@Override
	public String getRawValueString ()
	{
		return "" + getValue();
	}

	@Override
	public Number getRawValue ()
	{
		return getValue();
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
