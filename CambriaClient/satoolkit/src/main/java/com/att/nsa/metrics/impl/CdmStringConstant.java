/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.metrics.impl;

public class CdmStringConstant extends CdmSimpleMetric
{
	public CdmStringConstant ( String val )
	{
		fValue = val;
	}

	public String getUnits ()
	{
		return fValue;
	}

	public synchronized long getValue ()
	{
		return 0;
	}

	@Override
	public String getRawValueString ()
	{
		return fValue;
	}

	@Override
	public Number getRawValue ()
	{
		return getValue();
	}

	@Override
	public synchronized String summarize ()
	{
		return fValue;
	}

	@Override
	public synchronized void reset ()
	{
	}

	private final String fValue;
}
