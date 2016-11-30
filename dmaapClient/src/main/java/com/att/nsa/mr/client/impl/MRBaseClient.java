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
package com.att.nsa.mr.client.impl;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.http.HttpException;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.internal.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.apiClient.http.CacheUse;
import com.att.nsa.apiClient.http.HttpClient;
import com.att.nsa.mr.client.MRClient;
import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.test.clients.ProtocolTypeConstants;
//import com.fasterxml.jackson.core.JsonProcessingException;

public class MRBaseClient extends HttpClient implements MRClient 
{
	
	private static final String MR_AUTH_CONSTANT = "X-CambriaAuth";
	private static final String MR_DATE_CONSTANT = "X-CambriaDate";
	
	protected MRBaseClient ( Collection<String> hosts ) throws MalformedURLException
	{
		super ( ConnectionType.HTTP, hosts, MRConstants.kStdMRServicePort );
	}

	protected MRBaseClient ( Collection<String> hosts, int stdSvcPort ) throws MalformedURLException {
		super ( ConnectionType.HTTP,hosts, stdSvcPort);

		fLog = LoggerFactory.getLogger ( this.getClass().getName () );
	}

	protected MRBaseClient ( Collection<String> hosts, String clientSignature ) throws MalformedURLException
	{
		super(ConnectionType.HTTP, hosts, MRConstants.kStdMRServicePort, clientSignature, CacheUse.NONE, 1, 1L, TimeUnit.MILLISECONDS, 32, 32, 600000);

		fLog = LoggerFactory.getLogger ( this.getClass().getName () );
	}


	@Override
	public void close ()
	{
	}

	protected Set<String> jsonArrayToSet ( JSONArray a )
	{
		if ( a == null ) return null;

		final TreeSet<String> set = new TreeSet<String> ();
		for ( int i=0; i<a.length (); i++ )
		{
			set.add ( a.getString ( i ));
		}
		return set;
	}

	public void logTo ( Logger log )
	{
		fLog = log;
		replaceLogger ( log );
	}

	protected Logger getLog ()
	{
		return fLog;
	}

	private Logger fLog;
	
	public JSONObject post(final String path, final byte[] data, final String contentType, final String username, final String password, final String protocolFlag) throws HttpException, JSONException{
		if ((null != username && null != password)) {
			WebTarget target = null;

			Response response = null;
			
			target = getTarget(path, username, password);
			String encoding = Base64.encodeAsString(username+":"+password);
			
			
			response = target.request().header("Authorization", "Basic " + encoding).post(Entity.entity(data, contentType));
			
			return getResponseDataInJson(response);
		} else {
			throw new HttpException("Authentication Failed: Username/password/AuthKey/AuthDate parameter(s) cannot be null or empty.");
		}
	}
	public String postWithResponse(final String path, final byte[] data, final String contentType, final String username, final String password, final String protocolFlag) throws HttpException, JSONException{
		String responseData = null;
		if ((null != username && null != password)) {
			WebTarget target = null;

			Response response = null;
			
			target = getTarget(path, username, password);
			String encoding = Base64.encodeAsString(username+":"+password);
			
			
			response = target.request().header("Authorization", "Basic " + encoding).post(Entity.entity(data, contentType));
			
			responseData = response.readEntity(String.class);
			return responseData;
		} else {
			throw new HttpException("Authentication Failed: Username/password/AuthKey/AuthDate parameter(s) cannot be null or empty.");
		}
	}
	public JSONObject postAuth(final String path, final byte[] data, final String contentType, final String authKey,final String authDate,final String username, final String password, final String protocolFlag) throws HttpException, JSONException{
		if ((null != username && null != password)) {
			WebTarget target = null;

			Response response = null;
				target= getTarget(path,username, password);
				response = target.request()
						.header(MR_AUTH_CONSTANT, authKey)
						.header(MR_DATE_CONSTANT, authDate)
						.post(Entity.entity(data, contentType));
				
			return getResponseDataInJson(response);
		} else {
			throw new HttpException("Authentication Failed: Username/password/AuthKey/AuthDate parameter(s) cannot be null or empty.");
		}
	}
	public String postAuthwithResponse(final String path, final byte[] data, final String contentType, final String authKey,final String authDate,final String username, final String password, final String protocolFlag) throws HttpException, JSONException{
		String responseData = null;
		if ((null != username && null != password)) {
			WebTarget target = null;

			Response response = null;
				target= getTarget(path,username, password);
				response = target.request()
						.header(MR_AUTH_CONSTANT, authKey)
						.header(MR_DATE_CONSTANT, authDate)
						.post(Entity.entity(data, contentType));
				responseData = response.readEntity(String.class);
				return responseData;
			
		} else {
			throw new HttpException("Authentication Failed: Username/password/AuthKey/AuthDate parameter(s) cannot be null or empty.");
		}
	}


	public JSONObject get(final String path, final String username, final String password, final String protocolFlag) throws HttpException, JSONException {
		if (null != username && null != password) {
			
			WebTarget target = null;

			Response response = null;
			if (ProtocolTypeConstants.AUTH_KEY.getValue().equalsIgnoreCase(protocolFlag)) {
				target=getTarget(path);
				response = target.request()
						.header(MR_AUTH_CONSTANT, username)
						.header(MR_DATE_CONSTANT, password)
						.get();
			} else {
				target = getTarget(path, username, password);
				String encoding = Base64.encodeAsString(username+":"+password);
				
				response = target.request().header("Authorization", "Basic " + encoding).get();	
						
			}
			return getResponseDataInJson(response);
		} else {
			throw new HttpException("Authentication Failed: Username/password/AuthKey/Authdate parameter(s) cannot be null or empty.");
		}
	}
	
	
	public String getResponse(final String path, final String username, final String password, final String protocolFlag) throws HttpException, JSONException {
		String responseData = null;
		if (null != username && null != password) {
			
			WebTarget target = null;

			Response response = null;
			if (ProtocolTypeConstants.AUTH_KEY.getValue().equalsIgnoreCase(protocolFlag)) {
				target=getTarget(path);
				response = target.request()
						.header(MR_AUTH_CONSTANT, username)
						.header(MR_DATE_CONSTANT, password)
						.get();
			} else {
				target = getTarget(path, username, password);
				String encoding = Base64.encodeAsString(username+":"+password);				
				response = target.request().header("Authorization", "Basic " + encoding).get();							
			}
			MRClientFactory.HTTPHeadersMap=response.getHeaders();
		
			String transactionid=response.getHeaderString("transactionid");
				if (transactionid!=null && !transactionid.equalsIgnoreCase("")) {
					fLog.info("TransactionId : " + transactionid);
			}
			
			responseData = response.readEntity(String.class);
			return responseData;
		} else {
			throw new HttpException("Authentication Failed: Username/password/AuthKey/Authdate parameter(s) cannot be null or empty.");
		}
	}
	
	public JSONObject getAuth(final String path, final String authKey, final String authDate,final String username, final String password, final String protocolFlag) throws HttpException, JSONException {
		if (null != username && null != password) {
			
			WebTarget target = null;

			Response response = null;
				target=getTarget(path, username, password);
				response = target.request()
						.header(MR_AUTH_CONSTANT, authKey)
						.header(MR_DATE_CONSTANT, authDate)
						.get();
						
			return getResponseDataInJson(response);
		} else {
			throw new HttpException("Authentication Failed: Username/password/AuthKey/Authdate parameter(s) cannot be null or empty.");
		}
	}
	
	public String getAuthResponse(final String path, final String authKey, final String authDate,final String username, final String password, final String protocolFlag) throws HttpException, JSONException {
		String responseData = null;
		if (null != username && null != password) {
			
			WebTarget target = null;

			Response response = null;
				target=getTarget(path, username, password);
				response = target.request()
						.header(MR_AUTH_CONSTANT, authKey)
						.header(MR_DATE_CONSTANT, authDate)
						.get();
				
				MRClientFactory.HTTPHeadersMap=response.getHeaders();
				
				String transactionid=response.getHeaderString("transactionid");
					if (transactionid!=null && !transactionid.equalsIgnoreCase("")) {
						fLog.info("TransactionId : " + transactionid);
				}
						
				responseData = response.readEntity(String.class);
				return responseData;
		} else {
			throw new HttpException("Authentication Failed: Username/password/AuthKey/Authdate parameter(s) cannot be null or empty.");
		}
	}

	private WebTarget getTarget(final String path, final String username, final String password) {

		Client client = ClientBuilder.newClient();

		
			// Using UNIVERSAL as it supports both BASIC and DIGEST authentication types.
			HttpAuthenticationFeature feature = HttpAuthenticationFeature.universal(username, password);
			client.register(feature);
		
		return client.target(path);
	}


	private WebTarget getTarget(final String path) {

		Client client = ClientBuilder.newClient();
		return client.target(path);
	}
	private JSONObject getResponseDataInJson(Response response) throws JSONException {
		try {
			MRClientFactory.HTTPHeadersMap=response.getHeaders();
		//	fLog.info("DMAAP response status: " + response.getStatus());
			
						
			//MultivaluedMap<String, Object> headersMap = response.getHeaders();
			//for(String key : headersMap.keySet()) {
			String transactionid=response.getHeaderString("transactionid");
			if (transactionid!=null && !transactionid.equalsIgnoreCase("")) {
				fLog.info("TransactionId : " + transactionid);
			}

			/*final String responseData = response.readEntity(String.class);
			JSONTokener jsonTokener = new JSONTokener(responseData);
			JSONObject jsonObject = null;
			final char firstChar = jsonTokener.next();
			jsonTokener.back();
			if ('[' == firstChar) {
				JSONArray jsonArray = new JSONArray(jsonTokener);
				jsonObject = new JSONObject();
				jsonObject.put("result", jsonArray);
			} else {
				jsonObject = new JSONObject(jsonTokener);
			}

			return jsonObject;*/
			

			if(response.getStatus()==403) {
				JSONObject jsonObject = null;
				jsonObject = new JSONObject();
				JSONArray jsonArray = new JSONArray();
				jsonArray.put(response.getEntity());
				jsonObject.put("result", jsonArray);
				jsonObject.put("status", response.getStatus());
				return jsonObject;
			}
			String responseData = response.readEntity(String.class);
				
			JSONTokener jsonTokener = new JSONTokener(responseData);
			JSONObject jsonObject = null;
			final char firstChar = jsonTokener.next();
	    	jsonTokener.back();
			if ('[' == firstChar) {
				JSONArray jsonArray = new JSONArray(jsonTokener);
				jsonObject = new JSONObject();
				jsonObject.put("result", jsonArray);
				jsonObject.put("status", response.getStatus());
			} else {
				jsonObject = new JSONObject(jsonTokener);
				jsonObject.put("status", response.getStatus());
			}

			return jsonObject;
		} catch (JSONException excp) {
			fLog.error("DMAAP - Error reading response data.", excp);
			return null;
		}

	}
	
	public String getHTTPErrorResponseMessage(String responseString){
		
		String response = null;
		int beginIndex = 0;
		int endIndex = 0;
		if(responseString.contains("<body>")){
			
			beginIndex = responseString.indexOf("body>")+5;
			endIndex = responseString.indexOf("</body");
			response = responseString.substring(beginIndex,endIndex);
		}
		
		return response;
		
	}
	
	public String getHTTPErrorResponseCode(String responseString){
		
		String response = null;
		int beginIndex = 0;
		int endIndex = 0;
		if(responseString.contains("<title>")){
			beginIndex = responseString.indexOf("title>")+6;
			endIndex = responseString.indexOf("</title");
			response = responseString.substring(beginIndex,endIndex);
		}
				
		return response;		
	}
	
}
