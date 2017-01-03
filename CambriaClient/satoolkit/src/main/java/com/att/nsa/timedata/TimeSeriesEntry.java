/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.timedata;

public interface TimeSeriesEntry<T>
{
	/**
	 * The timestamp for this entry
	 * @return
	 */
	long getEpochTimestamp ();

	/**
	 * The value for this entry 
	 * @return
	 */
	T getValue ();
}
