/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostInfo
{
	/**
	 * Get this system's hostname for use in applications like logging,
	 * but not as a reliable name for the system. There are too many
	 * problems with an application identifying its host's name to rely
	 * on the information retrieved from the system.
	 * @return the host's likely name
	 */
	public static String getHostNameForLog ()
	{
		try
		{
			return InetAddress.getLocalHost ().getCanonicalHostName ();
		}
		catch ( UnknownHostException e )
		{
			return "localhost";
		}
	}
}
