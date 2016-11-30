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
package com.att.nsa.cambria.service;

import java.io.IOException;

import com.att.nsa.cambria.beans.ApiKeyBean;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.db.NsaApiDb.KeyExistsException;

/**
 * Declaring all the method in interface that is mainly used for authentication
 * purpose.
 *
 *
 */

public interface ApiKeysService {
	/**
	 * This method declaration for getting all ApiKey that has generated on
	 * server.
	 * 
	 * @param dmaapContext
	 * @throws ConfigDbException
	 * @throws IOException
	 */

	public void getAllApiKeys(DMaaPContext dmaapContext)
			throws ConfigDbException, IOException;

	/**
	 * Getting information about specific ApiKey
	 * 
	 * @param dmaapContext
	 * @param apikey
	 * @throws ConfigDbException
	 * @throws IOException
	 */

	public void getApiKey(DMaaPContext dmaapContext, String apikey)
			throws ConfigDbException, IOException;

	/**
	 * Thid method is used for create a particular ApiKey
	 * 
	 * @param dmaapContext
	 * @param nsaApiKey
	 * @throws KeyExistsException
	 * @throws ConfigDbException
	 * @throws IOException
	 */

	public void createApiKey(DMaaPContext dmaapContext, ApiKeyBean nsaApiKey)
			throws KeyExistsException, ConfigDbException, IOException;

	/**
	 * This method is used for update ApiKey that is already generated on
	 * server.
	 * 
	 * @param dmaapContext
	 * @param apikey
	 * @param nsaApiKey
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws AccessDeniedException
	 * @throws com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException 
	 */
	public void updateApiKey(DMaaPContext dmaapContext, String apikey,
			ApiKeyBean nsaApiKey) throws ConfigDbException, IOException,AccessDeniedException
			;

	/**
	 * This method is used for delete specific ApiKey
	 * 
	 * @param dmaapContext
	 * @param apikey
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws AccessDeniedException
	 */

	public void deleteApiKey(DMaaPContext dmaapContext, String apikey)
			throws ConfigDbException, IOException,AccessDeniedException;
}
