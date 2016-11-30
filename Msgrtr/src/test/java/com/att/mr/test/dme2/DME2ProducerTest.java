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
package com.att.mr.test.dme2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

import com.att.aft.dme2.api.DME2Client;
import com.att.aft.dme2.api.DME2Exception;
import com.att.aft.dme2.internal.jackson.map.ObjectMapper;
import com.att.mr.test.dmaap.DmaapAdminTest;

public class DME2ProducerTest extends TestCase {
	private static final Logger LOGGER = Logger.getLogger(DmaapAdminTest.class);

	public void testProducer() {
		DME2TopicTest topicTestObj = new DME2TopicTest();

		Properties props = LoadPropertyFile.getPropertyFileDataProducer();
		String latitude = props.getProperty("Latitude");
		String longitude = props.getProperty("Longitude");
		String version = props.getProperty("Version");
		String serviceName = props.getProperty("ServiceName");
		String env = props.getProperty("Environment");
		String partner = props.getProperty("Partner");
		String protocol = props.getProperty("Protocol");
		String url = protocol + "://DME2SEARCH/" + "service=" + serviceName + "/" + "version=" + version + "/"
				+ "envContext=" + env + "/" + "partner=" + partner;
		LoadPropertyFile.loadAFTProperties(latitude, longitude);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("AFT_DME2_EP_READ_TIMEOUT_MS", "50000");
		hm.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", "240000");
		hm.put("AFT_DME2_EP_CONN_TIMEOUT", "5000");
		// checking whether topic exist or not
		if (!topicTestObj.topicExist(url, props, hm)) {
			// if topic doesn't exist then create the topic
			topicTestObj.createTopic(url, props, hm);
			// after creating the topic publish on that topic
			publishMessage(url, props, hm);
		} else {
			// if topic already exist start publishing on the topic
			publishMessage(url, props, hm);
		}

	}

	public void publishMessage(String url, Properties props, HashMap<String, String> mapData) {
		try {
			LOGGER.info("Call to publish message ");
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(props.getProperty("MethodTypePost"));
			String subcontextpathPublish = props.getProperty("SubContextPathproducer") + props.getProperty("newTopic");
			sender.setSubContext(subcontextpathPublish);
			String jsonStringApiBean = new ObjectMapper().writeValueAsString(new ApiKeyBean("example@att.com",
					"description"));
			sender.setPayload(jsonStringApiBean);

			sender.setCredentials(props.getProperty("user"), props.getProperty("password"));
			sender.addHeader("content-type", props.getProperty("contenttype"));
			LOGGER.info("Publishing message");
			String reply = sender.sendAndWait(5000L);
			// assertTrue(LoadPropertyFile.isValidJsonString(reply));
			assertNotNull(reply);
			LOGGER.info("response =" + reply);

		} catch (DME2Exception e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
