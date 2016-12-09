/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client;

import java.io.IOException;

import com.att.nsa.apiClient.credentials.ApiCredential;
import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

/**
 * A client for manipulating API keys.
 *
 */
public interface CambriaIdentityManager extends CambriaClient
{
	/**
	 * An API Key record
	 */
	public interface ApiKey
	{
		/**
		 * Get the email address associated with the API key
		 * @return the email address on the API key or null
		 */
		String getEmail ();

		/**
		 * Get the description associated with the API key
		 * @return the description on the API key or null
		 */
		String getDescription ();
	}

	/**
	 * Create a new API key on the UEB cluster. The returned credential instance
	 * contains the new API key and API secret. This is the only time the secret
	 * is available to the client -- there's no API for retrieving it later -- so
	 * your application must store it securely. 
	 * 
	 * @param email
	 * @param description
	 * @return a new credential
	 * @throws HttpException 
	 * @throws CambriaApiException 
	 * @throws IOException 
	 */
	ApiCredential createApiKey ( String email, String description ) throws HttpException, CambriaApiException, IOException;
	
	/**
	 * Get basic info about a known API key
	 * @param apiKey
	 * @return the API key's info or null if it doesn't exist
	 * @throws HttpObjectNotFoundException, HttpException, CambriaApiException 
	 * @throws IOException 
	 */
	ApiKey getApiKey ( String apiKey ) throws HttpObjectNotFoundException, HttpException, CambriaApiException, IOException;

	/**
	 * Update the record for the API key used to authenticate this request. The UEB
	 * API requires that you authenticate with the same key you're updating, so the
	 * API key being changed is the one used for setApiCredentials.
	 * 
	 * @param email use null to keep the current value
	 * @param description use null to keep the current value
	 * @throws IOException 
	 * @throws HttpException 
	 * @throws HttpObjectNotFoundException 
	 */
	void updateCurrentApiKey ( String email, String description ) throws HttpObjectNotFoundException, HttpException, IOException;

	/**
	 * Delete the *current* API key. After this call returns, the API key
	 * used to authenticate will no longer be valid.
	 * 
	 * @throws IOException 
	 * @throws HttpException 
	 */
	void deleteCurrentApiKey () throws HttpException, IOException;
}
