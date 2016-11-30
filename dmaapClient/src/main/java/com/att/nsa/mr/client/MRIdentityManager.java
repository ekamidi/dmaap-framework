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
package com.att.nsa.mr.client;

import java.io.IOException;

import com.att.nsa.apiClient.credentials.ApiCredential;
import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;

/**
 * A client for manipulating API keys.
 * @author author
 *
 */
public interface MRIdentityManager extends MRClient
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
	 * @throws MRApiException 
	 * @throws IOException 
	 */
	ApiCredential createApiKey ( String email, String description ) throws HttpException, MRApiException, IOException;
	
	/**
	 * Get basic info about a known API key
	 * @param apiKey
	 * @return the API key's info or null if it doesn't exist
	 * @throws HttpObjectNotFoundException, HttpException, MRApiException 
	 * @throws IOException 
	 */
	ApiKey getApiKey ( String apiKey ) throws HttpObjectNotFoundException, HttpException, MRApiException, IOException;

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
