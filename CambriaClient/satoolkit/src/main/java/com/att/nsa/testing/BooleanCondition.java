/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.testing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This condition allows you to write a simple test interface. As long as the test
 * returns false, the test stays active. If the test throws any exception, the condition
 * is FAILED. If it returns true, the condition is SATISFIED.
 * 
 *
 */
public class BooleanCondition implements TestCondition
{
	public interface Test
	{
		/**
		 * Evaluate the boolean condition. Any exception FAILs the test.
		 * @return true if satisfied, false if pending. 
		 */
		boolean test ();
	}

	public BooleanCondition ( Test t )
	{
		fTest = t;
	}
	
	@Override
	public State evaluate ()
	{
		try
		{
			if ( fTest.test () )
			{
				log.info ( "BooleanCondition satisfied" );
				return State.SATISFIED;
			}
			return State.PENDING;
		}
		catch ( Throwable e )
		{
			log.warn ( "BooleanCondition failed on exception: " + e.getMessage(), e );
			return State.FAILED;
		}
	}

	private final Test fTest;
	private static final Logger log = LoggerFactory.getLogger ( BooleanCondition.class );
}
