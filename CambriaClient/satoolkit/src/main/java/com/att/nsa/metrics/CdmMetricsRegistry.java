/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.metrics;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * A registry for named measured items (metrics).
 *  
 *
 */
public interface CdmMetricsRegistry
{
	/**
	 * Put a measured item into the registry.
	 * @param named
	 * @param mi
	 */
	void putItem ( String named, CdmMeasuredItem mi );

	/**
	 * Get a measured item from the registry.
	 * @param named
	 * @return a measured item
	 */
	CdmMeasuredItem getItem ( String named );

	/**
	 * Remove a named measured item from the registry
	 * @param named
	 */
	void removeItem ( String named );

	/**
	 * Get all measured items
	 * @return a list of measured items
	 */
	Map<String,CdmMeasuredItem> getItems ();

	/**
	 * Get the number of items in this registry.
	 * @return the number of measured items in this registry
	 */
	int size ();

	/**
	 * Metrics entries are organized into a hierarchy based on "." separators
	 * in their names.
	 * 
	 *
	 */
	public interface CdmMetricEntry
	{
		String getName ();
		String getLocalName ();
		boolean hasValue ();
		int getLevel();
		CdmMeasuredItem getValue ();
	};

	/**
	 * Get an ordered list of metric entries
	 * @return
	 */
	List<? extends CdmMetricEntry> getEntries ();

	/**
	 * Create a JSON object that captures this registry's current values. 
	 * @return a JSON object
	 */
	JSONObject toJson();
}
