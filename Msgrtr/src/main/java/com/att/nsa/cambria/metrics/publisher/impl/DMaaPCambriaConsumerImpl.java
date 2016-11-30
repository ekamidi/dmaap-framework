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
package com.att.nsa.cambria.metrics.publisher.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import jline.internal.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;
import com.att.nsa.cambria.metrics.publisher.CambriaPublisherUtility;

/**
 * 
 * @author author
 *
 */
public class DMaaPCambriaConsumerImpl extends CambriaBaseClient
		implements com.att.nsa.cambria.metrics.publisher.CambriaConsumer {
	private final String fTopic;
	private final String fGroup;
	private final String fId;
	private final int fTimeoutMs;
	private final int fLimit;
	private final String fFilter;

	/**
	 * 
	 * @param hostPart
	 * @param topic
	 * @param consumerGroup
	 * @param consumerId
	 * @param timeoutMs
	 * @param limit
	 * @param filter
	 * @param apiKey
	 * @param apiSecret
	 * @throws MalformedURLException 
	 */
	public DMaaPCambriaConsumerImpl(Collection<String> hostPart, final String topic, final String consumerGroup,
			final String consumerId, int timeoutMs, int limit, String filter, String apiKey, String apiSecret) throws MalformedURLException {
		super(hostPart, topic + "::" + consumerGroup + "::" + consumerId);

		fTopic = topic;
		fGroup = consumerGroup;
		fId = consumerId;
		fTimeoutMs = timeoutMs;
		fLimit = limit;
		fFilter = filter;

		setApiCredentials(apiKey, apiSecret);
	}

	/**
	 * method converts String to list
	 * 
	 * @param str
	 * @return
	 */
	public static List<String> stringToList(String str) {
		final LinkedList<String> set = new LinkedList<String>();
		if (str != null) {
			final String[] parts = str.trim().split(",");
			for (String part : parts) {
				final String trimmed = part.trim();
				if (trimmed.length() > 0) {
					set.add(trimmed);
				}
			}
		}
		return set;
	}

	@Override
	public Iterable<String> fetch() throws IOException {
		// fetch with the timeout and limit set in constructor
		return fetch(fTimeoutMs, fLimit);
	}

	@Override
	public Iterable<String> fetch(int timeoutMs, int limit) throws IOException {
		final LinkedList<String> msgs = new LinkedList<String>();

		final String urlPath = createUrlPath(timeoutMs, limit);

		getLog().info("UEB GET " + urlPath);
		try {
			final JSONObject o = get(urlPath);

			if (o != null) {
				final JSONArray a = o.getJSONArray("result");
				if (a != null) {
					for (int i = 0; i < a.length(); i++) {
						msgs.add(a.getString(i));
					}
				}
			}
		} catch (HttpObjectNotFoundException e) {
			// this can happen if the topic is not yet created. ignore.
			Log.error("Failed due to topic is not yet created" + e);
		} catch (JSONException e) {
			// unexpected response
			reportProblemWithResponse();
			Log.error("Failed due to jsonException", e);
		} catch (HttpException e) {
			throw new IOException(e);
		}

		return msgs;
	}

	protected String createUrlPath(int timeoutMs, int limit) {
		final StringBuilder url = new StringBuilder(CambriaPublisherUtility.makeConsumerUrl(fTopic, fGroup, fId));
		final StringBuilder adds = new StringBuilder();
		if (timeoutMs > -1) {
			adds.append("timeout=").append(timeoutMs);
		}

		if (limit > -1) {
			if (adds.length() > 0) {
				adds.append("&");
			}
			adds.append("limit=").append(limit);
		}
		if (fFilter != null && fFilter.length() > 0) {
			try {
				if (adds.length() > 0) {
					adds.append("&");
				}
				adds.append("filter=").append(URLEncoder.encode(fFilter, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				Log.error("Failed due to UnsupportedEncodingException" + e);
			}
		}
		if (adds.length() > 0) {
			url.append("?").append(adds.toString());
		}
		return url.toString();
	}

}
