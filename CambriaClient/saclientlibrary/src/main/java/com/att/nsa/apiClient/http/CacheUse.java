/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient.http;

public enum CacheUse
{
	/**
	 * The operation may read from and write to the local object cache. Use this 
	 * for typical operations.
	 */
	FULL,

	/**
	 * The operation may neither read from nor write to the local object cache. Use
	 * this for transient values.
	 */
	NONE,

	/**
	 * The operation may write to the local object cache but not read from it. This
	 * is helpful when the system knows it needs a refreshed value now (e.g. an event
	 * signaled an update), but can re-use it from the cache until another signal is
	 * received.
	 */
	WRITE_ONLY,
	
	/**
	 * The operation may use the cache specified for default setup when it
	 * does not have a specific cache use for this operation
	 */
	DEFAULT

	// Note: READ_ONLY doesn't seem useful and so isn't included here. Would an app
	// ever want to read from the cache when possible, but not also write to it on a
	// cache miss?
}
