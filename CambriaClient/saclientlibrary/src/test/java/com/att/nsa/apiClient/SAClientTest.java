/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient;

import java.io.IOException;
import java.util.Arrays;

import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

public class SAClientTest {

	public static void main(String[] args) {
		final SAClient client = new SAClient(Arrays.asList(new HttpHost("localhost", 6168)));
		
		try {
			final CloseableHttpResponse response = client.getResponse(new HttpGet("/metrics"));
			System.out.println(EntityUtils.toString(response.getEntity()));
			EntityUtils.consumeQuietly(response.getEntity());
		} catch (SAClientException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		client.close();
	}
}
