/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.nsa.cambria.test.clients;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.UUID;

import com.att.nsa.apiClient.http.HttpClient.ConnectionType;
import com.att.nsa.cambria.client.CambriaClientFactory;
import com.att.nsa.cambria.client.CambriaConsumer;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

@SuppressWarnings("deprecation")
public class SimpleExampleConsumer
{
	public static void main ( String[] args )
	{
		if ( args.length < 1 )
		{
			System.err.println ( "A topic name is required." );
			System.exit ( 1 );
		}

		final String topic = args[0];
		final String url = ( args.length > 1 ? args[1] : "ueb01hydc.it.att.com,ueb02hydc.it.att.com,ueb03hydc.it.att.com" );
		final String group = ( args.length > 2 ? args[2] : UUID.randomUUID ().toString () );
		final String id = ( args.length > 3 ? args[3] : "0" );

		long count = 0;
		long nextReport = 5000;

		final long startMs = System.currentTimeMillis ();

		final LinkedList<String> urlList = new LinkedList<String> ();
		for ( String u : url.split ( "," ) )
		{
			urlList.add ( u );
		}

//		CambriaClientFactory.setConnectionOptions ( 100, 16, 10000 );
		
		try
		{
			final CambriaConsumer cc = CambriaClientFactory.createConsumer ( ConnectionType.HTTP, urlList, topic, group, id, 10*1000, 1000 );
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
		catch ( GeneralSecurityException x )
		{
			System.err.println ( x.getClass().getName () + ": " + x.getMessage () );
		}
	}
}
