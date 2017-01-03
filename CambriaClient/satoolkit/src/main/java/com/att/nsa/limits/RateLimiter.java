/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.limits;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.att.nsa.metrics.impl.CdmRateTicker;

/**
 * A class to flag high rates
 *
 */
public class RateLimiter
{
	/**
	 * Construct a rate limiter. 
	 * @param maxClicks  Pass <= 0 to deactivate rate limiting.
	 */
	public RateLimiter ( double maxClicks, long clickWindowLength, TimeUnit clickWindowUnit )
	{
		fTicker = new HashMap<String,CdmRateTicker> ();
		fMaxClicks = Math.max ( 0, maxClicks );
		fClickWindowLengthMs = TimeUnit.MILLISECONDS.convert ( clickWindowLength, clickWindowUnit );
	}

	/**
	 * Click the rate limiter.
	 * @return true if over the limit
	 */
	public boolean click ( String key )
	{
		getTickerFor(key).tick ();
		return overage ( key );
	}

	/**
	 * check the rate limit state
	 * @return true if this rate limiter is over the rate limit
	 */
	public boolean overage ( String key )
	{
		return getTickerFor(key).getRate () > fMaxClicks;
	}

	/**
	 * reset the limiter for a given key
	 */
	public void reset ( String key )
	{
		getTickerFor(key).reset ();
	}

	/**
	 * Remove a key from the rate limiter
	 * @param key
	 */
	public void remove ( String key )
	{
		fTicker.remove ( key );
	}
	
	private CdmRateTicker getTickerFor ( String key )
	{
		if ( !fTicker.containsKey ( key ))
		{
			fTicker.put ( key, new CdmRateTicker ( "", fClickWindowLengthMs, TimeUnit.MILLISECONDS,
				fClickWindowLengthMs, TimeUnit.MILLISECONDS ) );
		}
		return fTicker.get ( key );
	}
	
	private final HashMap<String,CdmRateTicker> fTicker;
	private final double fMaxClicks;
	private final long fClickWindowLengthMs;
}
