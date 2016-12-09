/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.testing;

/**
 * An abstract test condition. When evaluated, the condition returns its
 * State. 
 * 
 *
 */
public interface TestCondition
{
	public enum State
	{
		/**
		 * More work is to be done before this test result is available.
		 */
		PENDING,

		/**
		 * The test condition is satisfied.
		 */
		SATISFIED,

		/**
		 * The test is complete and the condition has failed.
		 */
		FAILED
	}

	/**
	 * Return the current state of this test condition.
	 * @return
	 */
	State evaluate ();
}
