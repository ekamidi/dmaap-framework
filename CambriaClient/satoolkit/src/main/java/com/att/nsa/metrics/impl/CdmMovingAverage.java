/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.metrics.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.att.nsa.clock.SaClock;
import com.att.nsa.timedata.TimeSeriesEntry;
import com.att.nsa.timedata.impl.mem.MemTsDb;

/**
 * A moving average.
 * 
 *
 */
public class CdmMovingAverage extends CdmSimpleMetric
{
	final static int kTickWithoutTrimLimit = 1000;

	public CdmMovingAverage ( String desc, long windowSize, TimeUnit windowTu )
	{
		fDb = new MemTsDb<Long> ();

		fDescription = desc;

		fStartTimeMs = SaClock.now ();
		fWindowSizeMs = TimeUnit.MILLISECONDS.convert ( windowSize, windowTu );
		
		fNeedRecalc = false;
		fTicksWithoutTrim = 0;
	}

	@Override
	public String getRawValueString ()
	{
		return "" + getAverage ();
	}

	@Override
	public Number getRawValue ()
	{
		return getAverage ();
	}

	public synchronized void tick ( long amtMs )
	{
		fTicksWithoutTrim++;
		fNeedRecalc = true;

		fDb.put ( kEntityId, SaClock.now(), amtMs );
		
		if ( fTicksWithoutTrim > kTickWithoutTrimLimit )
		{
			trimOldSlices ();
		}
	}

	public String getUnits ()
	{
		return fDescription;
	}

	public synchronized double getAverage ()
	{
		trimOldSlices ();
		if ( fNeedRecalc )
		{
			recalc ();
		}
		return fAvg;
	}

	@Override
	public synchronized String summarize ()
	{
		return "" + getAverage() + " " + fDescription;
	}

	@Override
	public synchronized void reset ()
	{
		fDb.clear ();
		fAvg = 0.0;
		fNeedRecalc = false;
		fStartTimeMs = SaClock.now ();
	}

	private final String fDescription;
	private final MemTsDb<Long> fDb;

	private final long fWindowSizeMs;
	private long fStartTimeMs;

	private double fAvg;
	private boolean fNeedRecalc;
	private long fTicksWithoutTrim;

	private void recalc ()
	{
		trimOldSlices ();

		final long nowMs = SaClock.now ();
		final long oldestMs = nowMs - fWindowSizeMs;

		long total = 0;

		final List<? extends TimeSeriesEntry<Long>> entries = fDb.get ( kEntityId, oldestMs, nowMs );
		for ( TimeSeriesEntry<Long> e : entries )
		{
			total += e.getValue ();
		}
		fAvg = total / ((double)entries.size ());
	}

	private void trimOldSlices ()
	{
		final long oldestMs = SaClock.now () - fWindowSizeMs;
		final long earliestMs = Math.max ( oldestMs, fStartTimeMs );
		fDb.clearOlderThan ( kEntityId, earliestMs );
		fTicksWithoutTrim = 0;
	}

	private static final String kEntityId = "avgData";
}
