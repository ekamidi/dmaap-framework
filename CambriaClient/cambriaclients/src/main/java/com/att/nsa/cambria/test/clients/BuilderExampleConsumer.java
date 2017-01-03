/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.nsa.cambria.test.clients;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.UUID;

import com.att.nsa.cambria.client.CambriaClientBuilders;
import com.att.nsa.cambria.client.CambriaConsumer;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class BuilderExampleConsumer
{
	public static void main ( String[] args )
	{
		if ( args.length < 1 )
		{
			System.err.println ( "A topic name is required." );
			System.exit ( 1 );
		}

		final String topic = args[0];
		final String hosts = ( args.length > 1 ? args[1] : "test1.com,test2.com,test3.com" );
		final String group = ( args.length > 2 ? args[2] : UUID.randomUUID ().toString () );
		final String id = ( args.length > 3 ? args[3] : "0" );

		long count = 0;
		long nextReport = 5000;

		final long startMs = System.currentTimeMillis ();

//		CambriaClientFactory.setConnectionOptions ( 100, 16, 10000 );

		try
		{
			final CambriaConsumer cc = new CambriaClientBuilders.ConsumerBuilder()
				.usingHosts ( hosts )
//			.authenticatedBy ( apiKey, apiSecret )
				.onTopic ( topic )
				.knownAs ( group, id )
				.waitAtServer ( 15*1000 )
				.receivingAtMost ( 1000 )
				.build ();

			try
			{
				while ( true )
				{
					for ( String msg : cc.fetch () )
					{
						System.out.println ( "" + (++count) + ": " + msg );
					}

					if ( count > nextReport )
					{
						nextReport += 5000;

						final long endMs = System.currentTimeMillis ();
						final long elapsedMs = endMs - startMs;
						final double elapsedSec = elapsedMs / 1000.0;
						final double eps = count / elapsedSec;
						System.out.println ( "Consumed " + count + " in " + elapsedSec + "; " + eps + " eps" );
					}
				}
			}
			catch ( IOException x )
			{
				System.err.println ( x.getClass().getName () + ": " + x.getMessage () );
			}
		}
		catch ( MalformedURLException x )
		{
			System.err.println ( x.getClass().getName () + ": " + x.getMessage () );
		}
		catch ( GeneralSecurityException x )
		{
			System.err.println ( x.getClass().getName () + ": " + x.getMessage () );
		}
	}
}
