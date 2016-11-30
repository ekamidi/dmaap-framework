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
import java.util.Properties;

import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.MRConsumer;
import com.att.nsa.mr.client.response.MRConsumerResponse;

public class SimpleExampleConsumerWithReturnResponse {


	static FileWriter routeWriter= null;
	static Properties props=null;	
	static FileReader routeReader=null;
	public static void main ( String[] args )
	{
	
		long count = 0;
		long nextReport = 5000;

		final long startMs = System.currentTimeMillis ();
				
		try
		{
			String routeFilePath="src/main/resources/dme2/preferredRoute.txt";
						        
			
			File fo= new File(routeFilePath);
			if(!fo.exists()){
					routeWriter=new FileWriter(new File (routeFilePath));
			}	
			routeReader= new FileReader(new File (routeFilePath));
			props= new Properties();
			final MRConsumer cc = MRClientFactory.createConsumer ( "src/main/resources/dme2/consumer.properties" );
			while ( true )
			{	
				MRConsumerResponse mrConsumerResponse = cc.fetchWithReturnConsumerResponse();
				System.out.println("mrConsumerResponse code :"+mrConsumerResponse.getResponseCode());
				
				System.out.println("mrConsumerResponse Message :"+mrConsumerResponse.getResponseMessage());
				
				System.out.println("mrConsumerResponse ActualMessage :"+mrConsumerResponse.getActualMessages());
				/*for ( String msg : mrConsumerResponse.getActualMessages() )
				{
					//System.out.println ( "" + (++count) + ": " + msg );
					System.out.println(msg);
				}*/
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
		catch ( Exception x )
		{
			System.err.println ( x.getClass().getName () + ": " + x.getMessage () );
		}
	}

}
