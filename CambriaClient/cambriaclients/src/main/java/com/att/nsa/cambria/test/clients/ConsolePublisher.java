/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.nsa.cambria.test.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.att.nsa.apiClient.http.HttpClient.ConnectionType;
import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.cambria.client.CambriaClientFactory;
import com.att.nsa.cambria.client.CambriaPublisher.message;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

/**
 * A simple publisher that reads from std in, sending each line as a message. 
 */
@SuppressWarnings("deprecation")
public class ConsolePublisher
{
	public static void main ( String[] args ) throws IOException //throws IOException, InterruptedException
	{
		// read the hosts(s) from the command line
		final String hosts = ( args.length > 0 ? args[0] : "ueb01hydc.it.att.com,ueb02hydc.it.att.com,ueb03hydc.it.att.com" );

		// read the topic name from the command line
		final String topic = ( args.length > 1 ? args[1] : "TEST-TOPIC" );

		// read the topic name from the command line
		final String partition = ( args.length > 2 ? args[2] : UUID.randomUUID ().toString () );

		// set up some batch limits and the compression flag
		final int maxBatchSize = 100;
		final long maxAgeMs = 250;
		final boolean withGzip = false;

		// create our publisher
		try
		{
			final CambriaBatchingPublisher pub = CambriaClientFactory.createBatchingPublisher ( ConnectionType.HTTP, hosts, topic, maxBatchSize, maxAgeMs, withGzip );
	
			final BufferedReader cin = new BufferedReader ( new InputStreamReader ( System.in ) );
			try
			{
				String line = null;
				while ( ( line = cin.readLine () ) != null )
				{
					pub.send ( partition, line );
				}
			}
			finally
			{
				List<message> leftovers = null;
				try
				{
					leftovers = pub.close ( 10, TimeUnit.SECONDS );
				}
				catch ( InterruptedException e )
				{
					System.err.println ( "Send on close interrupted." );			
				}
				for ( message m : leftovers )
				{
					System.err.println ( "Unsent message: " + m.fMsg );
				}
			}
		}
		catch ( GeneralSecurityException x )
		{
			System.err.println ( x.getMessage() );
		}
	}
}
