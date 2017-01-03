/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.metrics;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.clock.SaClock;
import com.att.nsa.metrics.impl.CdmMovingAverage;
import com.att.nsa.metrics.impl.CdmRateTicker;

/**
 * The timer allows you to mark start/stop times on an activity in a general way,
 * and publish various derived metrics into the metrics registry.
 * 
 *
 */
public class CdmTimer implements AutoCloseable
{
	public CdmTimer ( CdmMetricsRegistry reg, String activityName )
	{
		fReg = reg;
		fStartMs = SaClock.now ();
		fName = activityName;
		fEnded = false;
	}

	/**
	 * Record the invocation and duration. Use this once per timer OR use fail(),
	 * but not both.
	 */
	public void end ()
	{
		end ( null );
	}

	/**
	 * Record the invocation and duration. Use this once per timer OR use fail(),
	 * but not both.
	 * @param where For calls that end successfully in more than one place, note where. This is appended to the base name provided to the constructor.
	 */
	public void end ( String where )
	{
		if ( endNow () )
		{
			final long endMs = SaClock.now ();
			recordTopLevelEnd ( endMs );
			recordSuccessEnd ( endMs, where );
		}
	}

	/**
	 * Record the invocation and total duration, as well as the failure. Use this once
	 * per timer OR use end(), but not both.
	 * @param why A label for the failure metric. This is appended to the base name provided to the constructor.
	 */
	public void fail ( String why )
	{
		if ( endNow () )
		{
			final long endMs = SaClock.now ();
			recordTopLevelEnd ( endMs );
			recordFailEnd ( endMs, why );
		}
	}

	/**
	 * Close the timer by calling end();
	 */
	@Override
	public void close ()
	{
		end ();
	}

	private final CdmMetricsRegistry fReg;
	private final long fStartMs;
	private final String fName;
	private boolean fEnded;

	private boolean isEnded ()
	{
		return fEnded;
	}

	/**
	 * end the timer if not already ended
	 * @return true if this call ended the timer, false if it was already ended
	 */
	private boolean endNow ()
	{
		if ( isEnded () )
		{
			log.debug ( "Timer " + fName + " already ended." );
			return false;
		}

		fEnded = true;
		return true;
	}
	
	private void recordTopLevelEnd ( long endMs )
	{
		record ( fName + ".all", endMs );
	}

	private void recordSuccessEnd ( long endMs, String where )
	{
		recordTerm ( endMs, true, where );
	}

	private void recordFailEnd ( long endMs, String problem )
	{
		recordTerm ( endMs, false, problem );
	}

	private void recordTerm ( long endMs, boolean ok, String reason )
	{
		String name = fName + (ok?".ok":".fail") + ( reason == null ? "" : "." + reason );
		record ( name, endMs );
	}

	private void record ( String metricName, long endMs )
	{
		final long durMs = endMs - fStartMs;
		final CdmRateTicker ticker = getRateTickerFor ( metricName );
		ticker.tick ();

		final CdmMovingAverage durs = getMovingAvgFor ( metricName );
		durs.tick ( durMs );
	}

	private CdmRateTicker getRateTickerFor ( String baseName )
	{
		final String name = baseName + ".rate";
		final CdmMeasuredItem i = fReg.getItem ( name );
		if ( i != null )
		{
			if ( i instanceof CdmRateTicker )
			{
				return (CdmRateTicker) i;
			}
			else
			{
				log.warn ( "Metric item [" + name + "] exists as a " + i.getClass ().getName () + ", not a CdmTicker." );
				return new CdmRateTicker("",1,TimeUnit.HOURS,1,TimeUnit.HOURS);	// throw-away
			}
		}
		else
		{
			final CdmRateTicker ticker = new CdmRateTicker ( baseName + " per min, last hour", 1, TimeUnit.MINUTES, 1, TimeUnit.HOURS );
			fReg.putItem ( name, ticker );
			return ticker;
		}
	}

	private CdmMovingAverage getMovingAvgFor ( String baseName )
	{
		final String name = baseName + ".avgMs";
		final CdmMeasuredItem i = fReg.getItem ( name );
		if ( i != null )
		{
			if ( i instanceof CdmMovingAverage )
			{
				return (CdmMovingAverage) i;
			}
			else
			{
				log.warn ( "Metric item [" + name + "] exists as a " + i.getClass ().getName () + ", not a CdmMovingAverage." );
				return new CdmMovingAverage ( "", 10, TimeUnit.MINUTES );	// throw-away
			}
		}
		else
		{
			final CdmMovingAverage ticker = new CdmMovingAverage ( baseName + " avg duration ms, last 10 minutes", 10, TimeUnit.MINUTES );
			fReg.putItem ( name, ticker );
			return ticker;
		}
	}

	private static final Logger log = LoggerFactory.getLogger ( CdmTimer.class );
}
