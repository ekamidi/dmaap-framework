/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client;

import java.io.IOException;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public interface CambriaConsumer extends CambriaClient
{
	/**
	 * Fetch a set of messages. The consumer's timeout and message limit are used if set in the constructor call. 

	 * @return a set of messages
	 * @throws IOException
	 */
	Iterable<String> fetch () throws IOException;

	/**
	 * Fetch a set of messages with an explicit timeout and limit for this call. These values
	 * override any set in the constructor call.
	 * 
	 * @param timeoutMs	The amount of time in milliseconds that the server should keep the connection
	 * open while waiting for message traffic. Use -1 for default timeout (controlled on the server-side).
	 * @param limit A limit on the number of messages returned in a single call. Use -1 for no limit.
	 * @return a set messages
	 * @throws IOException if there's a problem connecting to the server
	 */
	Iterable<String> fetch ( int timeoutMs, int limit ) throws IOException;
}
