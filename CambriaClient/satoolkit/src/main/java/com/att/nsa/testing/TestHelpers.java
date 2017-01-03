/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.testing;

import com.att.nsa.testing.TestCondition.State;

public class TestHelpers
{
	/**
	 * Wait for one of the conditions to be satisfied.
	 * @param conditions
	 */
	public static boolean waitForAnySuccess ( TestCondition... conditions )
	{
		while ( true )
		{
			for ( TestCondition tc : conditions )
			{
				final State s = tc.evaluate ();
				switch ( s )
				{
					case SATISFIED:
						// the condition is satisfied, that's all we need
						return true;

					case FAILED:
						// this condition failed, fail the test
						return false;

					case PENDING:
					default:
						// wait
						break;
				}
			}

			try
			{
				Thread.sleep ( 500 );
			}
			catch ( InterruptedException e )
			{
				return false;
			}
		}
	}
}
