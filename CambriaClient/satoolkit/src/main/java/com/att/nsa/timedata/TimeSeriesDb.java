/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.timedata;

import java.util.List;

/**
 * A time-series data service
 */
public interface TimeSeriesDb<T>
{
	/**
	 * Clear the database.
	 */
	void clear ();

	/**
	 * Clear the database.
	 * @param entitiyId
	 */
	void clear ( String entitiyId );

	/**
	 * Clear entries older than the given time
	 * @param entitiyId
	 * @param epochTimestamp
	 */
	void clearOlderThan ( String entitiyId, long epochTimestamp );

	/**
	 * Put a data point into the service.
	 * @param entitiyId
	 * @param epochTimestamp
	 * @param value
	 */
	void put ( String entitiyId, long epochTimestamp, T value );

	/**
	 * Get a specific value from the time series
	 * @param entityId
	 * @param epochTimestamp
	 * @return a specific value, or null if this timestamp doesn't exist
	 */
	TimeSeriesEntry<T> get ( String entityId, long epochTimestamp );

	/**
	 * Get a value set from the time series
	 * @param entityId
	 * @param epochStart
	 * @param epochEnd
	 * @return a range of values
	 */
	List<? extends TimeSeriesEntry<T>> get ( String entityId, long epochStart, long epochEnd );
}
