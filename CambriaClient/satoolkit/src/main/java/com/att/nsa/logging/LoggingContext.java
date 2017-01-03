/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.logging;

import java.util.Map;

/**
 * An interface for providing data into the underlying logging context.  Systems should use
 * this interface rather than log-system-specific MDC solutions in order to limit dependency
 * on a particular log implementation.
 * 
 * A LoggingContext is specific to the calling thread.
 * 
 *
 */
public interface LoggingContext
{
	/**
	 * Clear a key/value pair from the logging context.
	 * @param key
	 * @return this
	 */
	LoggingContext clear ( String key );

	/**
	 * Put a key/value pair into the logging context, replacing an entry with the same key.
	 * @param key
	 * @param value
	 * @return this
	 */
	LoggingContext put ( String key, String value );

	/**
	 * Put a key/value pair into the logging context, replacing an entry with the same key.
	 * @param key
	 * @param value
	 * @return this
	 */
	LoggingContext put ( String key, long value );
	
	/**
	 * Get a string value, returning the default value if the value is missing.
	 * @param key
	 * @param defaultValue
	 * @return a string value
	 */
	String get ( String key, String defaultValue );
	
	/**
	 * Get a long value, returning the default value if the value is missing or not a long.
	 * @param key
	 * @param defaultValue
	 * @return a long value
	 */
	long get ( String key, long defaultValue );

	/**
	 * Add a listener context that will get onUpdate calls when something changes on
	 * this context, or one that it subscribes to.
	 * @lc the context to subscribe to
	 */
	void addListener ( LoggingContext lc );

	/**
	 * Called when a subscribed context changes.
	 * @param source
	 * @param key
	 * @param val
	 */
	void onUpdate ( LoggingContext source, String key, String val );

	/**
	 * Populate the given map with settings from this context and its base, if any.
	 * @param map
	 */
	void populate ( Map<String, String> map );
}
