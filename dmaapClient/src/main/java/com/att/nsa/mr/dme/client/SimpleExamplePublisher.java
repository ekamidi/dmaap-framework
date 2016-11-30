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

package com.att.nsa.mr.dme.client;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MultivaluedMap;

import org.json.JSONObject;

import com.att.nsa.mr.client.MRBatchingPublisher;
import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.MRPublisher.message;

/**
 * An example of how to use the Java publisher.
 * 
 * @author author
 */
public class SimpleExamplePublisher {
	static String content = null;
	static String messageSize = null;
	static String transport = null;
	static String messageCount = null;

	public void publishMessage(String producerFilePath) throws IOException, InterruptedException, Exception {

		// create our publisher
		
		// publish some messages
		
		
		StringBuilder sb = new StringBuilder();
		final MRBatchingPublisher pub = MRClientFactory.createBatchingPublisher(producerFilePath);
		
		if (content.equalsIgnoreCase("text/plain")) {
			for (int i = 0; i < Integer.parseInt(messageCount); i++) {
				for (int j = 0; j < Integer.parseInt(messageSize); j++) {
					sb.append("T");
				}

				pub.send(sb.toString());
			}
		} else if (content.equalsIgnoreCase("application/cambria")) {
			for (int i = 0; i < Integer.parseInt(messageCount); i++) {
				for (int j = 0; j < Integer.parseInt(messageSize); j++) {
					sb.append("C");
				}

				pub.send("Key", sb.toString());
			}
		} else if (content.equalsIgnoreCase("application/json")) {
			for (int i = 0; i < Integer.parseInt(messageCount); i++) {
				
					final JSONObject msg12 = new JSONObject();
					msg12.put("Name", "DMaaP Reference Client to Test jason Message");
					
					pub.send(msg12.toString());
				
			}
		}

		// ...

		// close the publisher to make sure everything's sent before exiting.
		// The batching
		// publisher interface allows the app to get the set of unsent messages.
		// It could
		// write them to disk, for example, to try to send them later.
	/*	final List<message> stuck = pub.close(20, TimeUnit.SECONDS);
		if (stuck.size() > 0) {
			System.err.println(stuck.size() + " messages unsent");
		} else {
			System.out.println("Clean exit; all messages sent.");
		}*/

		if (transport.equalsIgnoreCase("HTTP")) {
			MultivaluedMap<String, Object> headersMap = MRClientFactory.HTTPHeadersMap;
			for (String key : headersMap.keySet()) {
				System.out.println("Header Key " + key);
				System.out.println("Header Value " + headersMap.get(key));
			}
		} else {
			Map<String, String> dme2headersMap = MRClientFactory.DME2HeadersMap;
			for (String key : dme2headersMap.keySet()) {
				System.out.println("Header Key " + key);
				System.out.println("Header Value " + dme2headersMap.get(key));
			}
		}

	}

	public static void main(String[] args) throws InterruptedException, Exception {

		String producerFilePath = args[0];
		content = args[1];
		messageSize = args[2];
		transport = args[3];
		messageCount = args[4];
		/*String producerFilePath = null;
		content = null;
		messageSize =null;
		transport =null;
		messageCount = null;*/
		SimpleExamplePublisher publisher = new SimpleExamplePublisher();

		publisher.publishMessage("D:\\SG\\producer.properties");
	}

}
