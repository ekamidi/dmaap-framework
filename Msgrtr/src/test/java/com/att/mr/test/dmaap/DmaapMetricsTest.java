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
package com.att.mr.test.dmaap;

import java.io.InputStream;
import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.http.HttpStatus;

public class DmaapMetricsTest {
	/*private static final Logger LOGGER = Logger.getLogger(DmaapMetricsTest.class);
	Client client = ClientBuilder.newClient();
	WebTarget target = client.target(LoadPropertyFile.getPropertyFileData().getProperty("url"));

	public void assertStatus(Response response) {
		assertTrue(response.getStatus() == HttpStatus.SC_OK);
	}


	// 1.get metrics
	public void testMetrics() {
		LOGGER.info("test case get all metrics");
		target = target.path("/metrics");
		Response response = target.request().get();
		assertStatus(response);
		LOGGER.info("successfully returned after fetching all metrics");
		InputStream is = (InputStream) response.getEntity();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		String data = s.next();
		s.close();
		LOGGER.info("DmaapMetricTest Test all metrics" + data);
	}

	// 2.get metrics by name
	public void testMetricsByName() {
		LOGGER.info("test case get metrics by name");
		target = target.path("/metrics/startTime");
		Response response = target.request().get();
		assertStatus(response);
		LOGGER.info("successfully returned after fetching specific metrics");
		InputStream is = (InputStream) response.getEntity();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		String data = s.next();
		s.close();
		LOGGER.info("DmaapMetricTest metrics by name" + data);
	}
*/
}
