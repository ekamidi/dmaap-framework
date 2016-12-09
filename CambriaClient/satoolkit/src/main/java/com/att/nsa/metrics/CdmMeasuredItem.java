/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.metrics;

import java.util.Set;
import java.util.concurrent.Delayed;

/**
 * A measured item.
 */
public interface CdmMeasuredItem extends Delayed
{
	/**
	 * If the item needs to be evaluated periodically, return true
	 * and implement Delayed appropriately.
	 * @return true/false
	 */
	boolean requiresScheduledEvaluation ();

	/**
	 * Get the raw value for use by scripts, etc., that acquire the string
	 * over a REST interface or the like. 
	 * @return a raw value string
	 */
	String getRawValueString ();

	/**
	 * Get the raw value as a number. 
	 * @return a number, or null
	 */
	Number getRawValue ();

	/**
	 * Summarize the measurement for use on a user interface.
	 * @return
	 */
	String summarize ();

	/**
	 * Reset the measurement.
	 */
	void reset ();

	/**
	 * Poll the measured item (normally via delay queue)
	 */
	void poll ();
	
	/**
	 * A measured item can depend on one or more others.
	 * @return a set of unique dependencies.
	 */
	Set<CdmMeasuredItem> getDependencies ();
}
