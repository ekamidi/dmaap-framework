/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.metrics.impl;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.att.nsa.metrics.CdmMeasuredItem;

public abstract class CdmSimpleMetric implements CdmMeasuredItem
{
	@Override
	public String toString ()
	{
		return summarize ();
	}

	@Override
	public boolean requiresScheduledEvaluation ()
	{
		return false;
	}

	@Override
	public void reset ()
	{
	}

	@Override
	public void poll ()
	{
	}

	@Override
	public Set<CdmMeasuredItem> getDependencies ()
	{
		return new TreeSet<CdmMeasuredItem> ();
	}

	@Override
	public long getDelay ( TimeUnit arg0 )
	{
		return -1;
	}

	@Override
	public int compareTo ( Delayed arg0 )
	{
		return new Long ( getDelay ( TimeUnit.MILLISECONDS ) ).compareTo (
			new Long ( arg0.getDelay ( TimeUnit.MILLISECONDS  ) ) );
	}
}
