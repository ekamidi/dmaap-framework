/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.metrics.impl;

public class CdmConstant extends CdmSimpleMetric
{
	public CdmConstant ( long constant, String desc )
	{
		fDescription = desc;
		fConstant = constant;
	}

	public String getUnits ()
	{
		return fDescription;
	}

	public synchronized long getValue ()
	{
		return fConstant;
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
		return "" + fConstant + " " + fDescription;
	}

	@Override
	public synchronized void reset ()
	{
		fConstant = 0;
	}

	private final String fDescription;
	private long fConstant;
}
