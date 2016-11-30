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

import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.apache.log4j.Logger;

import com.att.nsa.drumlin.till.data.sha1HmacSigner;

public class DMaapTopicTest {
	/*private static final Logger LOGGER = Logger.getLogger(DMaapTopicTest.class);
	Client client = ClientBuilder.newClient();
	String topicapikey, topicsecretKey, serverCalculatedSignature;
	Properties prop = LoadPropertyFile.getPropertyFileData();
	String topicName = prop.getProperty("topicName");
	String url = prop.getProperty("url");
	String date = prop.getProperty("date");
	WebTarget target = client.target(url);
	DmaapApiKeyTest keyInstance = new DmaapApiKeyTest();


	public void createTopic(String name) {
		if (!topicExist(name)) {
			TopicBean topicbean = new TopicBean();
			topicbean.setDescription("creating topic");
			topicbean.setPartitionCount(1);
			topicbean.setReplicationCount(1);
			topicbean.setTopicName(name);
			topicbean.setTransactionEnabled(true);
			target = client.target(url);
			target = target.path("/topics/create");
			JSONObject jsonObj = keyInstance.returnKey(new ApiKeyBean("nm254w@att.com", "topic creation"));
			topicapikey = (String) jsonObj.get("key");
			topicsecretKey = (String) jsonObj.get("secret");
			serverCalculatedSignature = sha1HmacSigner.sign(date, topicsecretKey);
			Response response = target.request().header("X-CambriaAuth", topicapikey + ":" + serverCalculatedSignature)
					.header("X-CambriaDate", date).post(Entity.json(topicbean));
			keyInstance.assertStatus(response);
		}

	}

	public boolean topicExist(String topicName) {
		target = target.path("/topics/" + topicName);
		InputStream is, issecret;
		Response response = target.request().get();
		if (response.getStatus() == HttpStatus.SC_OK) {
			is = (InputStream) response.getEntity();
			Scanner s = new Scanner(is);
			s.useDelimiter("\\A");
			JSONObject dataObj = new JSONObject(s.next());
			s.close();
			// get owner of a topic
			topicapikey = (String) dataObj.get("owner");
			target = client.target(url);
			target = target.path("/apiKeys/");
			target = target.path(topicapikey);
			Response response2 = target.request().get();
			issecret = (InputStream) response2.getEntity();
			Scanner st = new Scanner(issecret);
			st.useDelimiter("\\A");
			JSONObject dataObj1 = new JSONObject(st.next());
			st.close();
			// get secret key of this topic//
			topicsecretKey = (String) dataObj1.get("secret");
			serverCalculatedSignature = sha1HmacSigner.sign(date, topicsecretKey);
			return true;
		} else
			return false;
	}

	public void testCreateTopic() {
		LOGGER.info("test case create topic");
		createTopic(topicName);
		LOGGER.info("Returning after create topic");
	}

	public void testOneTopic() {
		LOGGER.info("test case get specific topic name " + topicName);
		createTopic(topicName);
		target = client.target(url);
		target = target.path("/topics/");
		target = target.path(topicName);
		Response response = target.request().get();
		LOGGER.info("Successfully returned after fetching topic" + topicName);
		keyInstance.assertStatus(response);
		InputStream is = (InputStream) response.getEntity();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		JSONObject dataObj = new JSONObject(s.next());
		LOGGER.info("Details of " + topicName + " : " + dataObj.toString());
		s.close();
	}

	public void testdeleteTopic() {
		LOGGER.info("test case delete topic name " + topicName);
		createTopic(topicName);
		target = client.target(url);
		target = target.path("/topics/");
		target = target.path(topicName);
		Response response = target.request().header("X-CambriaAuth", topicapikey + ":" + serverCalculatedSignature)
				.header("X-CambriaDate", date).delete();
		keyInstance.assertStatus(response);
		LOGGER.info("Successfully returned after deleting topic" + topicName);
	}

	public void testAllTopic() {
		LOGGER.info("test case fetch all topic");
		target = client.target(url);
		target = target.path("/topics");
		Response response = target.request().get();
		keyInstance.assertStatus(response);
		LOGGER.info("successfully returned after fetching all the topic");
		InputStream is = (InputStream) response.getEntity();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		JSONObject dataObj = new JSONObject(s.next());
		s.close();
		LOGGER.info("List of all topics " + dataObj.toString());
	}

	public void testPublisherForTopic() {
		LOGGER.info("test case get all publishers for topic: " + topicName);
		// creating topic to check
		createTopic(topicName);
		target = client.target(url);
		target = target.path("/topics/");
		target = target.path(topicName);
		target = target.path("/producers");
		// checking all producer for a particular topic
		Response response = target.request().get();
		keyInstance.assertStatus(response);
		LOGGER.info("Successfully returned after getting all the publishers" + topicName);
	}

	public void testPermitPublisherForTopic() {
		LOGGER.info("test case permit user for topic " + topicName);
		JSONObject jsonObj = keyInstance.returnKey(new ApiKeyBean("ai039a@att.com", "adding user to "));
		String userapikey = (String) jsonObj.get("key");
		createTopic(topicName);
		// adding user to a topic//
		target = client.target(url);
		target = target.path("/topics/");
		target = target.path(topicName);
		target = target.path("/producers/");
		target = target.path(userapikey);
		Response response = target.request().header("X-CambriaAuth", topicapikey + ":" + serverCalculatedSignature)
				.header("X-CambriaDate", date).put(Entity.json(""));
		keyInstance.assertStatus(response);
		LOGGER.info("successfully returned after permiting the user for topic " + topicName);
	}

	public void testDenyPublisherForTopic() {
		LOGGER.info("test case denying user for topic " + topicName);
		JSONObject jsonObj = keyInstance.returnKey(new ApiKeyBean("ai039a@att.com", "adding user to "));
		String userapikey = (String) jsonObj.get("key");
		createTopic(topicName);
		// adding user to a topic//
		target = client.target(url);
		target = target.path("/topics/");
		target = target.path(topicName);
		target = target.path("/producers/");
		target = target.path(userapikey);
		target.request().header("X-CambriaAuth", topicapikey + ":" + serverCalculatedSignature)
				.header("X-CambriaDate", date).put(Entity.json(""));
		// deleting user who is just added//
		target = client.target(url);
		target = target.path("/topics/");
		target = target.path(topicName);
		target = target.path("/producers/");
		target = target.path(userapikey);
		Response response2 = target.request().header("X-CambriaAuth", topicapikey + ":" + serverCalculatedSignature)
				.header("X-CambriaDate", date).delete();
		keyInstance.assertStatus(response2);
		LOGGER.info("successfully returned after denying the user for topic " + topicName);
	}

	public void testConsumerForTopic() {
		LOGGER.info("test case get all consumers for topic: " + topicName);
		// creating topic to check
		createTopic(topicName);
		target = client.target(url);
		target = target.path("/topics/");
		target = target.path(topicName);
		target = target.path("/consumers");
		// checking all consumer for a particular topic
		Response response = target.request().get();
		keyInstance.assertStatus(response);
		LOGGER.info("Successfully returned after getting all the consumers" + topicName);
	}

	public void testPermitConsumerForTopic() {
		LOGGER.info("test case get all consumer for topic: " + topicName);
		// creating user for adding to topic//
		JSONObject jsonObj = keyInstance.returnKey(new ApiKeyBean("ai039a@att.com", "adding user to "));
		String userapikey = (String) jsonObj.get("key");
		createTopic(topicName);
		// adding user to a topic//
		target = client.target(url);
		target = target.path("/topics/");
		target = target.path(topicName);
		target = target.path("/consumers/");
		target = target.path(userapikey);
		Response response = target.request().header("X-CambriaAuth", topicapikey + ":" + serverCalculatedSignature)
				.header("X-CambriaDate", date).put(Entity.json(""));
		keyInstance.assertStatus(response);
		LOGGER.info("Successfully returned after getting all the consumers" + topicName);
	}

	public void testDenyConsumerForTopic() {
		LOGGER.info("test case denying consumer for topic " + topicName);
		// creating user for adding and deleting from topic//
		JSONObject jsonObj = keyInstance.returnKey(new ApiKeyBean("ai039a@att.com", "adding user to "));
		String userapikey = (String) jsonObj.get("key");
		createTopic(topicName);
		// adding user to a topic//
		target = client.target(url);
		target = target.path("/topics/");
		target = target.path(topicName);
		target = target.path("/consumers/");
		target = target.path(userapikey);
		target.request().header("X-CambriaAuth", topicapikey + ":" + serverCalculatedSignature)
				.header("X-CambriaDate", date).put(Entity.json(""));
		// deleting user who is just added//
		target = client.target(url);
		target = target.path("/topics/");
		target = target.path(topicName);
		target = target.path("/consumers/");
		target = target.path(userapikey);
		Response response2 = target.request().header("X-CambriaAuth", topicapikey + ":" + serverCalculatedSignature)
				.header("X-CambriaDate", date).delete();
		keyInstance.assertStatus(response2);
		LOGGER.info("successfully returned after denying the consumer for topic " + topicName);
	}*/
}
