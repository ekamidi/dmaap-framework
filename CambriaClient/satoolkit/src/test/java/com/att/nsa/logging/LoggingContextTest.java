/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.logging;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingContextTest extends TestCase
{
	@Test
	public void testThreadLocalContexts () throws InterruptedException
	{
		final LoggingContext commonContext = new LoggingContextFactory.Builder()
			.build ()
			.put ( "common", "commonValue" )
			.put ( "shared", "commonShare" )
		;

		final LoggingContext middleContext = new LoggingContextFactory.Builder()
			.withBaseContext ( commonContext )
			.build ()
			.put ( "middle", "middleValue" )
		;

		final Thread t1 = new Thread ()
		{
			public void run ()
			{
				new LoggingContextFactory.Builder()
					.withBaseContext ( middleContext )
					.build ()
					.put ( "t1Only", "t1" )
					.put ( "shared", "t1" )
				;
				try
				{
					// just to make sure t2 is setup too
					Thread.sleep ( 1000 );
				}
				catch ( InterruptedException e )
				{
				}
				log.info ( "t1 output" );
			}
		};

		final Thread t2 = new Thread ()
		{
			public void run ()
			{
				new LoggingContextFactory.Builder()
					.withBaseContext ( middleContext )
					.build ()
					.put ( "t2Only", "t2" )
					.put ( "shared", "t2" )
				;
				try
				{
					// just to make sure t1 is setup too
					Thread.sleep ( 1000 );
				}
				catch ( InterruptedException e )
				{
				}
				log.info ( "t2 output" );
			}
		};

		t1.start ();
		t2.start ();

		t1.join ();
		t2.join ();

		log.info ( "post thread output" );
	}

	private static final Logger log = LoggerFactory.getLogger ( LoggingContextTest.class );
}

