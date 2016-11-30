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
import java.util.Properties;
import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.http.HttpStatus;
import org.json.JSONObject;

import com.att.nsa.drumlin.till.data.sha1HmacSigner;

public class DmaapApiKeyTest {
	/*
	private static final Logger LOGGER = Logger.getLogger(DmaapApiKeyTest.class);
	Client client = ClientBuilder.newClient();
	Properties prop = LoadPropertyFile.getPropertyFileData();
	String url = prop.getProperty("url");
	WebTarget target = client.target(url);
	String date = prop.getProperty("date");


	public JSONObject returnKey(ApiKeyBean apikeybean) {
		LOGGER.info("Call to return newly created key");
		target = client.target(url);
		target = target.path("/apiKeys/create");
		Response response = target.request().post(Entity.json(apikeybean));
		assertStatus(response);
		LOGGER.info("successfully created keys");
		InputStream is = (InputStream) response.getEntity();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		JSONObject dataObj = new JSONObject(s.next());
		s.close();
		LOGGER.info("key details :" + dataObj.toString());
		return dataObj;
	}

	// 1. create key
	public void testCreateKey() {
		LOGGER.info("test case create key");
		ApiKeyBean apiKeyBean = new ApiKeyBean("nm254w@att.com", "Creating Api Key.");
		returnKey(apiKeyBean);
		LOGGER.info("Successfully returned after creating key");
	}

	public void assertStatus(Response response) {
		assertTrue(response.getStatus() == HttpStatus.SC_OK);
	}

	// 2. get Allkey details
	public void testAllKey() {
		LOGGER.info("test case get all key");
		target = target.path("/apiKeys");
		Response response = target.request().get();
		assertStatus(response);
		LOGGER.info("successfully returned after get all key");
		InputStream is = (InputStream) response.getEntity();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		LOGGER.info("Details of key: " + s.next());
		s.close();

	}

	// 3. get specific key
	public void testSpecificKey() {
		LOGGER.info("test case get specific key");
		String apiKey = "";
		ApiKeyBean apiKeyBean = new ApiKeyBean("ai039@att.com", "Creating Api Key.");

		apiKey = (String) returnKey(apiKeyBean).get("key");
		target = client.target(url);
		target = target.path("/apiKeys/");
		target = target.path(apiKey);
		Response response = target.request().get();
		assertStatus(response);
		LOGGER.info("successfully returned after fetching specific key");
	}

	// 4. update key

	public void testUpdateKey() {
		LOGGER.info("test case update key");
		String apiKey = "";
		String secretKey = "";
		final String serverCalculatedSignature;
		final String X_CambriaAuth;
		final String X_CambriaDate;
		JSONObject jsonObj;

		ApiKeyBean apiKeyBean = new ApiKeyBean("ai039@att.com", "Creating Api Key for update");
		ApiKeyBean apiKeyBean1 = new ApiKeyBean("ai03911@att.com", "updating Api Key.");
		jsonObj = returnKey(apiKeyBean);
		apiKey = (String) jsonObj.get("key");
		secretKey = (String) jsonObj.get("secret");

		serverCalculatedSignature = sha1HmacSigner.sign(date, secretKey);
		X_CambriaAuth = apiKey + ":" + serverCalculatedSignature;
		X_CambriaDate = date;
		target = client.target(url);
		target = target.path("/apiKeys/" + apiKey);
		Response response1 = target.request().header("X-CambriaAuth", X_CambriaAuth)
				.header("X-CambriaDate", X_CambriaDate).put(Entity.json(apiKeyBean1));
		assertStatus(response1);
		LOGGER.info("successfully returned after updating key");
	}

	// 5. delete key
	public void testDeleteKey() {
		LOGGER.info("test case delete key");
		String apiKey = "";
		String secretKey = "";
		final String serverCalculatedSignature;
		final String X_CambriaAuth;
		final String X_CambriaDate;
		JSONObject jsonObj;
		ApiKeyBean apiKeyBean = new ApiKeyBean("ai039@att.com", "Creating Api Key.");
		jsonObj = returnKey(apiKeyBean);
		apiKey = (String) jsonObj.get("key");
		secretKey = (String) jsonObj.get("secret");
		serverCalculatedSignature = sha1HmacSigner.sign(date, secretKey);
		X_CambriaAuth = apiKey + ":" + serverCalculatedSignature;
		X_CambriaDate = date;
		target = client.target(url);
		target = target.path("/apiKeys/" + apiKey);
		Response response2 = target.request().header("X-CambriaAuth", X_CambriaAuth)
				.header("X-CambriaDate", X_CambriaDate).delete();
		assertStatus(response2);
		LOGGER.info("successfully returned after deleting key");
	}
*/
}