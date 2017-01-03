/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

/*
 * Note: this is not a unit test. It also requires a log4j binding, which we
 * don't want built into the client package.
 */
public class SampleCambriaLogger
{
	public static void main ( String[] args ) throws InterruptedException
	{
		// load an explicit log config
		System.setProperty ( "log4j.configuration", "cambriaLogger.log4j.xml" );

		// create the logger
		final Logger log = LoggerFactory.getLogger ( SampleCambriaLogger.class );

		// go to town
		while ( true )
		{
			log.info ( "test log message" );
			Thread.sleep ( 2500 );
		}
	}
}
