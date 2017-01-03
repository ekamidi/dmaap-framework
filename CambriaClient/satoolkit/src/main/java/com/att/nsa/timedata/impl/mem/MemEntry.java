/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.timedata.impl.mem;

import com.att.nsa.timedata.TimeSeriesEntry;

class MemEntry<T> implements TimeSeriesEntry<T>
{
	public MemEntry ( long ts, T val )
	{
		fTs = ts;
		fVal = val;
	}
	
	@Override
	public long getEpochTimestamp ()
	{
		return fTs;
	}

	@Override
	public T getValue ()
	{
		return fVal;
	}

	private final long fTs;
	private final T fVal;
}
