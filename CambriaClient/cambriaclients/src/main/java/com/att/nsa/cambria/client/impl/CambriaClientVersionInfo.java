/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class CambriaClientVersionInfo
{
	public static String getVersion ()
	{
		return version;
	}

	private static final Properties props = new Properties();
	private static final String version;
	static
	{
		String use = null;
		try
		{
			final InputStream is = CambriaClientVersionInfo.class.getResourceAsStream ( "/cambriaClientVersion.properties" );
			if ( is != null )
			{
				props.load ( is );
				use = props.getProperty ( "cambriaClientVersion", null );
			}
		}
		catch ( IOException e )
		{
		}
		version = use;
	}
}
