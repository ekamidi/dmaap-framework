/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client;

import java.io.IOException;
import java.util.Collection;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

/**
 * A Cambria publishing interface.
 */
public interface CambriaPublisher extends CambriaClient
{
	/**
	 * A simple message container 
	 */
	public static class message
	{
		public message ( String partition, String msg )
		{
			fPartition = partition == null ? "" : partition;
			fMsg = msg;
			if ( fMsg == null )
			{
				throw new IllegalArgumentException ( "Can't send a null message." );
			}
		}

		public message ( message msg )
		{
			this ( msg.fPartition, msg.fMsg );
		}

		public final String fPartition;
		public final String fMsg;
	}
	
	/**
	 * Send the given message using the given partition.
	 * @param partition
	 * @param msg
	 * @return the number of pending messages
	 * @throws IOException
	 */
	int send ( String partition, String msg ) throws IOException;

	/**
	 * Send the given message using its partition.
	 * @param msg
	 * @return the number of pending messages
	 * @throws IOException
	 */
	int send ( message msg ) throws IOException;

	/**
	 * Send the given messages using their partitions.
	 * @param msgs
	 * @return the number of pending messages
	 * @throws IOException
	 */
	int send ( Collection<message> msgs ) throws IOException;

	/**
	 * Close this publisher. It's an error to call send() after close()
	 */
	void close ();
}
