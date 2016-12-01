/*******************************************************************************
 * BSD License
 *  
 * Copyright (c) 2016, AT&T Intellectual Property.  All other rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *  
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *    and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 * 3. All advertising materials mentioning features or use of this software must display the
 *    following acknowledgement:  This product includes software developed by the AT&T.
 * 4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 *******************************************************************************/
package com.att.nsa.mr.test.clients;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.mr.client.MRBatchingPublisher;
import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.MRClientBuilders.PublisherBuilder;
import com.att.nsa.mr.client.MRPublisher.message;

public class SamplePublisher {
	public static void main ( String[] args ) throws IOException, InterruptedException
	{
		final Logger LOG = LoggerFactory.getLogger(SampleConsumer.class);
		// read the hosts(s) from the command line
		final String hosts = ( args.length > 0 ? args[0] : "localhost:8181" );

		// read the topic name from the command line
		//final String topic = ( args.length > 1 ? args[1] : "MY-EXAMPLE-TOPIC" );
		final String topic = ( args.length > 1 ? args[1] : "com.att.app.dmaap.mr.testingTopic" );

		// set up some batch limits and the compression flag
		final int maxBatchSize = 100;
		final int maxAgeMs = 250;
		final boolean withGzip = false;

		// create our publisher
	
		final MRBatchingPublisher pub = new PublisherBuilder ().
				usingHosts ( hosts ).
				onTopic ( topic ).limitBatch(maxBatchSize, maxAgeMs).				
				authenticatedBy ( "CG0TXc2Aa3v8LfBk", "pj2rhxJWKP23pgy8ahMnjH88" ).
				build ()
			;
		// publish some messages
		final JSONObject msg1 = new JSONObject ();
		msg1.put ( "name", "tttttttttttttttt" );
		msg1.put ( "greeting", "ooooooooooooooooo" );
		pub.send ( "MyPartitionKey", msg1.toString () );

		final JSONObject msg2 = new JSONObject ();
		msg2.put ( "now", System.currentTimeMillis () );
		pub.send ( "MyOtherPartitionKey", msg2.toString () );

		// ...

		// close the publisher to make sure everything's sent before exiting. The batching
		// publisher interface allows the app to get the set of unsent messages. It could
		// write them to disk, for example, to try to send them later.
		final List<message> stuck = pub.close ( 20, TimeUnit.SECONDS );
		if ( stuck.size () > 0 )
		{
			LOG.warn ( stuck.size() + " messages unsent" );
		}
		else
		{
			LOG.info ( "Clean exit; all messages sent." );
		}
	}
}
