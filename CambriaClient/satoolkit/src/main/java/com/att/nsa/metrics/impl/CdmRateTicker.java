/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.metrics.impl;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import com.att.nsa.clock.SaClock;

/**
 * Rate ticker produces the rate of ticks over a time window. 
 * 
 *
 */
public class CdmRateTicker extends CdmSimpleMetric
{
	final static int kTickWithoutTrimLimit = 1000;

	/**
	 * Construct a rate ticker. The period is the "per 'time'", the window is how long to average over
	 * @param desc
	 * @param periodSize
	 * @param periodTu
	 * @param windowSize
	 * @param windowTu
	 */
	public CdmRateTicker ( String desc, long periodSize, TimeUnit periodTu, long windowSize, TimeUnit windowTu )
	{
		fDescription = desc;
		fSlices = new LinkedList<slice> ();

		fSliceSize = periodSize;
		fSliceTimeUnit = periodTu;
		fSliceSizeMs = TimeUnit.MILLISECONDS.convert ( fSliceSize, fSliceTimeUnit );

		fStartTimeMs = SaClock.now ();
		fWindowSizeMs = TimeUnit.MILLISECONDS.convert ( windowSize, windowTu );
		
		fNeedRecalc = false;
		fTicksWithoutTrim = 0;
	}

	@Override
	public String getRawValueString ()
	{
		return "" + getRate();
	}

	@Override
	public Number getRawValue ()
	{
		return getRate ();
	}

	public void tick ()
	{
		tick ( 1 );
	}

	public synchronized void tick ( int amt )
	{
		fTicksWithoutTrim++;
		fNeedRecalc = true;

		// get the right slice
		getCurrentSlice ().bumpBy ( amt );

		if ( fTicksWithoutTrim > kTickWithoutTrimLimit )
		{
			trimOldSlices ();
		}
	}

	public String getUnits ()
	{
		return fDescription;
	}

	public synchronized double getRate ()
	{
		trimOldSlices ();
		if ( fNeedRecalc )
		{
			recalc ();
		}
		return fRate;
	}

	@Override
	public synchronized String summarize ()
	{
		return "" + getRate() + " " + fDescription;
	}

	@Override
	public synchronized void reset ()
	{
		fSlices.clear ();
		fRate = 0.0;
		fNeedRecalc = false;
		fStartTimeMs = SaClock.now ();
	}

	protected long getSliceCount ()
	{
		return fSlices.size ();
	}

	private final String fDescription;
	private final LinkedList<slice> fSlices;
	private final long fSliceSize;
	private final long fSliceSizeMs;
	private final TimeUnit fSliceTimeUnit;
	private final long fWindowSizeMs;
	private long fStartTimeMs;

	private double fRate;
	private boolean fNeedRecalc;
	private long fTicksWithoutTrim;

	private class slice
	{
		public slice ( long timeMs )
		{
			fTimeMs = timeMs;
			fCount = 0;
		}

		public long time() { return fTimeMs; }
		public long count() { return fCount; }
		public void bumpBy ( int amt ) { fCount+=amt; }
		
		private final long fTimeMs;
		private long fCount;
	}

	private void recalc ()
	{
		final long now = SaClock.now ();
		final long windowStartMs = now - fWindowSizeMs;
		final long earliestMs = Math.max ( windowStartMs, fStartTimeMs );

		// how many time slices since earliest?
		final double sliceCount = Math.max ( 1.0, ( now - earliestMs ) / fSliceSizeMs );

		long total = 0;
		for ( slice s : fSlices )
		{
			total += s.count();
		}

		fRate = (total * 1.0) / sliceCount;
	}

	private void trimOldSlices ()
	{
		final long oldestMs = SaClock.now () - fWindowSizeMs;
		while ( fSlices.size () > 0 && fSlices.peekFirst ().time() < oldestMs )
		{
			fSlices.removeFirst ();
			fNeedRecalc = true;
		}
		fTicksWithoutTrim = 0;
	}
	
	private slice getCurrentSlice ()
	{
		final long now = SaClock.now ();

		slice result = null;
		if ( fSlices.size() > 0 )
		{
			result = fSlices.getLast ();
			if ( now > ( result.time() + fSliceSizeMs ) )
			{
				// past this slice. find a slice that fits
				long sliceStartMs = result.time();
				while ( sliceStartMs + fSliceSizeMs < now )
				{
					sliceStartMs += fSliceSizeMs;
				}
				result = new slice(now);
				fSlices.addLast ( result );
			}
		}
		else
		{
			// no current slices. start a new one
			result = new slice(now);
			fSlices.addLast ( result );
		}
		return result;
	}
}
