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
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import com.att.nsa.mr.client.MRBatchingPublisher;
import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.response.MRPublisherResponse;
	/**
	 *An example of how to use the Java publisher. 
	 * @author author
	 *
	 */
	public class SimpleExamplePublisherWithResponse
	{
		static FileWriter routeWriter= null;
		static Properties props=null;	
		static FileReader routeReader=null;
		
		public static void main(String []args) throws InterruptedException, Exception{
			
			String routeFilePath="src/main/resources/dme2/preferredRoute.txt";
			String msgCount = args[0];
			SimpleExamplePublisherWithResponse publisher = new SimpleExamplePublisherWithResponse();
			File fo= new File(routeFilePath);
			if(!fo.exists()){
					routeWriter=new FileWriter(new File (routeFilePath));
			}	
			routeReader= new FileReader(new File (routeFilePath));
			props= new Properties();
			int i=0;
			while (i< Integer.valueOf(msgCount))
			{
				publisher.publishMessage("src/main/resources/dme2/producer.properties",Integer.valueOf(msgCount));
				i++;
			}
		}
		
		public void publishMessage ( String producerFilePath , int count ) throws IOException, InterruptedException, Exception
		{
			// create our publisher
			final MRBatchingPublisher pub = MRClientFactory.createBatchingPublisher (producerFilePath,true);	
			// publish some messages
			final JSONObject msg1 = new JSONObject ();

			msg1.put ( "Partition:1", "Message:"+count);
			msg1.put ( "greeting", "Hello  .." );
			
			
			pub.send ( "1", msg1.toString());
			pub.send ( "1", msg1.toString());
			
			MRPublisherResponse res= pub.sendBatchWithResponse();
			
			System.out.println("Pub response->"+res.toString());
		}
		
		
	}
