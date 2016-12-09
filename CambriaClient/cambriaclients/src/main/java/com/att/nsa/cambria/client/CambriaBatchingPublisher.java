/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

/**
 * A Cambria batching publisher is a publisher with additional functionality
 * for managing delayed sends.
 * 
 *
 */
public interface CambriaBatchingPublisher extends CambriaPublisher
{
	/**
	 * Get the number of messages that have not yet been sent.
	 * @return the number of pending messages
	 */
	int getPendingMessageCount ();

	/**
	 * Close this publisher, sending any remaining messages.
	 * @param timeout an amount of time to wait for unsent messages to be sent
	 * @param timeoutUnits the time unit for the timeout arg
	 * @return a list of any unsent messages after the timeout
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	List<message> close ( long timeout, TimeUnit timeoutUnits ) throws IOException, InterruptedException;
}
