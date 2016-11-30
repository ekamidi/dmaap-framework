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
import com.att.mr.test.dmaap.DmaapAdminTest;
//import com.ibm.disthub2.impl.client.PropSchema;

public class DME2AdminTest extends TestCase {
	private static final Logger LOGGER = Logger.getLogger(DME2AdminTest.class);

	protected String url;
	
	protected Properties props;
	
	protected HashMap<String, String> hm;
	
	protected String methodType;
	
	protected String contentType;
	
	protected String user;
	
	protected String password;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty("AFT_DME2_CLIENT_SSL_INCLUDE_PROTOCOLS", "SSLv3,TLSv1,TLSv1.1");
		System.setProperty("AFT_DME2_CLIENT_IGNORE_SSL_CONFIG", "false");
		System.setProperty("AFT_DME2_CLIENT_KEYSTORE_PASSWORD", "changeit");
		this.props = LoadPropertyFile.getPropertyFileDataProducer();
		String latitude = props.getProperty("Latitude");
		String longitude = props.getProperty("Longitude");
		String version = props.getProperty("Version");
		String serviceName = props.getProperty("ServiceName");
		serviceName = "dmaap-v1.dev.dmaap.dt.saat.acsi.att.com/admin";
		String env = props.getProperty("Environment");
		String partner = props.getProperty("Partner");
		String protocol = props.getProperty("Protocol");
		
		methodType = props.getProperty("MethodTypeGet");
		contentType = props.getProperty("contenttype");
		user = props.getProperty("user");
		password = props.getProperty("password");
		
		
		this.url = protocol + "://" + serviceName + "?" + "version=" + version + "&" + "envContext=" + env + "&"
				+ "routeOffer=" + partner + "&partner=BOT_R";
		LoadPropertyFile.loadAFTProperties(latitude, longitude);
		hm = new HashMap<String, String>();
		hm.put("AFT_DME2_EP_READ_TIMEOUT_MS", "50000");
		hm.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", "240000");
		hm.put("AFT_DME2_EP_CONN_TIMEOUT", "5000");		
	}
	
	public void testGetConsumerCache() {
		LOGGER.info("test case consumer cache started");
		
		String subContextPath = props.getProperty("SubContextPathGetAdminConsumerCache");
		try {
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(methodType);
			sender.setSubContext(subContextPath);
			sender.setPayload("");
			sender.addHeader("Content-Type", contentType);
			
			sender.addHeader("X-CambriaAuth", "rs873m:7J49YriFlyRgebyOsSJhZvY/C60=");
			sender.addHeader("X-X-CambriaDate", "2016-10-18T09:56:04-05:00");
			
			//sender.setCredentials(user, password);
			sender.setHeaders(hm);
			LOGGER.info("Getting consumer Cache");
			String reply = sender.sendAndWait(5000L);
			System.out.println(reply);
			assertTrue(LoadPropertyFile.isValidJsonString(reply));
			assertNotNull(reply);
			LOGGER.info("response from consumer cache=" + reply);

		} catch (DME2Exception e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void ttestDropConsumerCache() {
		LOGGER.info("Drom consumer cache initiated");

		String subContextPath = props.getProperty("SubContextPathDropAdminConsumerCache");

		try {
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(methodType);
			sender.setSubContext(subContextPath);
			sender.setPayload("");
			sender.addHeader("Content-Type", contentType);
			sender.setCredentials(user, password);
			sender.setHeaders(hm);

			LOGGER.info("Dropping consumer cache...........");
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
