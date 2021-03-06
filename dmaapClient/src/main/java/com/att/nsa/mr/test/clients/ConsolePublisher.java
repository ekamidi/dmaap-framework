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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.att.nsa.mr.client.MRBatchingPublisher;
import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.MRPublisher.message;

/**
 * A simple publisher that reads from std in, sending each line as a message. 
 * @author author
 */
public class ConsolePublisher
{
	public static void main ( String[] args ) throws IOException //throws IOException, InterruptedException
	{
		// read the hosts(s) from the command line
		final String hosts = ( args.length > 0 ? args[0] : "aaa.it.att.com,bbb.it.att.com,ccc.it.att.com" );

		// read the topic name from the command line
		final String topic = ( args.length > 1 ? args[1] : "TEST-TOPIC" );

		// read the topic name from the command line
		final String partition = ( args.length > 2 ? args[2] : UUID.randomUUID ().toString () );

		// set up some batch limits and the compression flag
		final int maxBatchSize = 100;
		final long maxAgeMs = 250;
		final boolean withGzip = false;

		// create our publisher
		final MRBatchingPublisher pub = MRClientFactory.createBatchingPublisher ( hosts, topic, maxBatchSize, maxAgeMs, withGzip );

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
}
