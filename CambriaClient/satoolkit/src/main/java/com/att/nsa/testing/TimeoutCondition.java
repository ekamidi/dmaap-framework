/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.testing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A condition that FAILs after the specified amount of time. Note that you can
 * set interactiveTest=true on the Java command line (-DinteractiveTest=true) to
 * prevent this condition from timing out.<br/>
 * <br/>
 * Also note, this test never returns SATISFIED.
 * 
 */
public class TimeoutCondition implements TestCondition
{
	/**
	 * Create a condition that returns FAILED after durationMs.
	 * @param durationMs
	 */
	public TimeoutCondition ( long durationMs )
	{
		// use the real clock, as this is a timeout for a test
		fTimeoutAtMs = System.currentTimeMillis () + durationMs;
	}

	@Override
	public State evaluate ()
	{
		if ( System.currentTimeMillis () > fTimeoutAtMs && !Boolean.parseBoolean ( System.getProperty ( "interactiveTest", "false" ) ) )
		{
			log.warn ( "TimeoutCondition failed" );
			return State.FAILED;
		}
		return State.PENDING;
	}

	private final long fTimeoutAtMs;
	private static final Logger log = LoggerFactory.getLogger ( TimeoutCondition.class );
}
