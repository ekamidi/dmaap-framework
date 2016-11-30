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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.att.nsa.mr.client.MRBatchingPublisher;
import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.MRPublisher.message;

/**
 * An example of how to use the Java publisher. 
 * @author author
 */
public class SimpleExamplePublisher
{
	static FileWriter routeWriter= null;
	static Properties props=null;	
	static FileReader routeReader=null;
	public void publishMessage ( String producerFilePath  ) throws IOException, InterruptedException, Exception
	{
				
		// create our publisher
		final MRBatchingPublisher pub = MRClientFactory.createBatchingPublisher (producerFilePath);	
		// publish some messages
		final JSONObject msg1 = new JSONObject ();
		msg1.put ( "Name", "Sprint" );
		//msg1.put ( "greeting", "Hello  .." );
		pub.send ( "First cambria messge" );
		pub.send ( "MyPartitionKey", msg1.toString () );

		final JSONObject msg2 = new JSONObject ();
		//msg2.put ( "mrclient1", System.currentTimeMillis () );
		
        
		// ...

		// close the publisher to make sure everything's sent before exiting. The batching
		// publisher interface allows the app to get the set of unsent messages. It could
		// write them to disk, for example, to try to send them later.
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
	
	public static void main(String []args) throws InterruptedException, Exception{

		String routeFilePath="/src/main/resources/dme2/preferredRoute.txt";

		SimpleExamplePublisher publisher = new SimpleExamplePublisher();

		
		File fo= new File(routeFilePath);
		if(!fo.exists()){
				routeWriter=new FileWriter(new File (routeFilePath));
		}	
		routeReader= new FileReader(new File (routeFilePath));
		props= new Properties();
		publisher.publishMessage("/src/main/resources/dme2/producer.properties");
		}
	
}

