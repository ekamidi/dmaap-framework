/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package saToolkit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.logging.rolling.ExternalLogRollInterface;

public class SampleExternalLogRotate
{
	public static void main ( String[] args ) throws InterruptedException
	{
		// setup the interface for catching an external Log4J rotate
		final ExternalLogRollInterface elri = new ExternalLogRollInterface ( "HUP" );
		elri.install ();

		// setup the CLASSPATH to include src/example/resources to catch the log4j.xml
		final Logger log = LoggerFactory.getLogger ( SampleExternalLogRotate.class );
		while ( true )
		{
			log.info ( "test 1 2 3" );
			Thread.sleep ( 1000 );
		}
	}
}
