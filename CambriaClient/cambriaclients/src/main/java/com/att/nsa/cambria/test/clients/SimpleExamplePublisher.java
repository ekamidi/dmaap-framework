/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.nsa.cambria.test.clients;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.cambria.client.CambriaClientBuilders;
import com.att.nsa.cambria.client.CambriaPublisher.message;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

/**
 * An example of how to use the Java publisher. 
 */
public class SimpleExamplePublisher
{
	public static void main ( String[] args ) throws IOException, InterruptedException, GeneralSecurityException
	{
		// read the hosts(s) from the command line
		final String hosts = ( args.length > 0 ? args[0] : "uebsb91kcdc.it.att.com,uebsb92kcdc.it.att.com,uebsb93kcdc.it.att.com" );

		// read the topic name from the command line
		final String topic = ( args.length > 1 ? args[1] : "TEST-TOPIC" );

		// set up some batch limits and the compression flag
		final int maxBatchSize = 100;
		final int maxAgeMs = 250;
		final boolean withGzip = false;

		// create our publisher using a builder
		final CambriaBatchingPublisher pub = new CambriaClientBuilders.PublisherBuilder ()
			.usingHosts ( hosts )
			.onTopic ( topic )
			.limitBatch ( maxBatchSize, maxAgeMs )
			.enableCompresion ( withGzip )
//			.authenticatedBy ( apiKey, apiSecret )
			.build ()
		;

		// publish some messages
		final JSONObject msg1 = new JSONObject ();
		msg1.put ( "name", "UEB Example Publisher" );
		msg1.put ( "greeting", "Hello, world." );
		pub.send ( "MyPartitionKey", msg1.toString () );

		final JSONObject msg2 = new JSONObject ();
		msg2.put ( "now", System.currentTimeMillis () );
		pub.send ( "MyOtherPartitionKey", msg2.toString () );

		// ...

		// close the publisher. The batching publisher does not send events
		// immediately, so you MUST use close to send any remaining messages.
		// You provide the amount of time you're willing to wait for the sends
		// to succeed before giving up. If any messages are unsent after that time,
		// they're returned to your app. You could, for example, persist to disk
		// and try again later.
		final List<message> stuck = pub.close ( 20, TimeUnit.SECONDS );
		if ( stuck.size () > 0 )
		{
			System.err.println ( stuck.size() + " messages unsent" );
		}
		else
		{
			System.out.println ( "Clean exit; all messages sent." );
		}
	}
}
