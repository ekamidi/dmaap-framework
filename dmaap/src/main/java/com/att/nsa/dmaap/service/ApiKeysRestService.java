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
package com.att.nsa.dmaap.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.beans.ApiKeyBean;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.exception.DMaaPResponseCode;
import com.att.nsa.cambria.exception.ErrorResponse;
import com.att.nsa.cambria.service.ApiKeysService;
import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.db.NsaApiDb.KeyExistsException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

/**
 * This class is a CXF REST service 
 * which acts as gateway for Cambria Api
 * Keys.
 * @author author
 *
 */
@Component
@Path("/")
public class ApiKeysRestService {

	/**
	 * Logger obj
	 */
	//private Logger log = Logger.getLogger(ApiKeysRestService.class.toString());
	private static final EELFLogger log = EELFManager.getInstance().getLogger(ApiKeysRestService.class);
	/**
	 * HttpServletRequest obj
	 */
	@Context
	private HttpServletRequest request;

	/**
	 * HttpServletResponse obj
	 */
	@Context
	private HttpServletResponse response;

	/**
	 * Config Reader
	 */
	@Autowired
	@Qualifier("configurationReader")
	private ConfigurationReader configReader;

	/**
	 * ApiKeysService obj
	 */
	@Autowired
	private ApiKeysService apiKeyService;

	/**
	 * Returns a list of all the existing Api keys
	 * @throws CambriaApiException 
	 * 
	 * @throws IOException
	 * */
	@GET
	public void getAllApiKeys() throws CambriaApiException {

		log.info("Inside ApiKeysRestService.getAllApiKeys");

		try {
			apiKeyService.getAllApiKeys(getDmaapContext());
			log.info("Fetching all API keys is Successful");
		} catch (ConfigDbException | IOException e) {
			log.error("Error while retrieving API keys: " + e);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.GENERIC_INTERNAL_ERROR.getResponseCode(), 
					"Error while retrieving API keys: "+ e.getMessage());
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}

	}

	/**
	 * Returns details of a particular api key whose <code>name</code> is passed
	 * as a parameter
	 * 
	 * @param apiKeyName
	 *            - name of the api key
	 * @throws CambriaApiException 
	 * @throws IOException
	 * */
	@GET
	@Path("/{apiKey}")
	public void getApiKey(@PathParam("apiKey") String apiKeyName) throws CambriaApiException {
		log.info("Fetching details of api key: " + apiKeyName);

		try {
			apiKeyService.getApiKey(getDmaapContext(), apiKeyName);
			log.info("Fetching specific API key is Successful");
		} catch (ConfigDbException | IOException e) {
			log.error("Error while retrieving API key details: " + e);
			
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.GENERIC_INTERNAL_ERROR.getResponseCode(), 
					"Error while retrieving API key details: "+ e.getMessage());
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}
	
	

	/**
	 * Creates api key using the <code>email</code> and <code>description</code>
	 * 
	 * @param nsaApiKey
	 * @throws CambriaApiException 
	 * @throws JSONException 
	 * */
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	public void createApiKey(ApiKeyBean nsaApiKey) throws CambriaApiException, JSONException {
		log.info("Creating Api Key.");

		try {
			apiKeyService.createApiKey(getDmaapContext(), nsaApiKey);
			log.info("Creating API key is Successful");
		} catch (KeyExistsException | ConfigDbException | IOException e) {
			log.error("Error while Creating API key : " + e.getMessage(), e);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.GENERIC_INTERNAL_ERROR.getResponseCode(), 
					"Error while Creating API key : "+ e.getMessage());
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}

	}

	/**
	 * Updates an existing apiKey using the key name passed a parameter and the
	 * details passed.
	 * 
	 * @param apiKeyName
	 *            - name of the api key to be updated
	 * @param nsaApiKey
	 * @throws CambriaApiException 
	 * @throws JSONException 
	 * @throws IOException
	 * @throws AccessDeniedException
	 * */
	@PUT
	@Path("/{apiKey}")
	public void updateApiKey(@PathParam("apiKey") String apiKeyName,
			ApiKeyBean nsaApiKey) throws CambriaApiException, JSONException {
		log.info("Updating Api Key.");

		try {
			
			apiKeyService
					.updateApiKey(getDmaapContext(), apiKeyName, nsaApiKey);
			log.error("API key updated sucessfully");
		} catch (ConfigDbException | IOException | AccessDeniedException e) {
			log.error("Error while Updating API key : " + apiKeyName, e);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.GENERIC_INTERNAL_ERROR.getResponseCode(), 
					"Error while Updating API key : "+ e.getMessage());
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		}
	}

	/**
	 * Deletes an existing apiKey using the key name passed as a parameter.
	 * 
	 * @param apiKeyName
	 *            - name of the api key to be updated
	 * @throws CambriaApiException 
	 * @throws IOException
	 * @throws AccessDeniedException
	 * */
	@DELETE
	@Path("/{apiKey}")
	public void deleteApiKey(@PathParam("apiKey") String apiKeyName) throws CambriaApiException {
		log.info("Deleting Api Key: " + apiKeyName);
		try {
			apiKeyService.deleteApiKey(getDmaapContext(), apiKeyName);
			log.info("Api Key deleted successfully: " + apiKeyName);
		} catch (ConfigDbException | IOException | AccessDeniedException e) {
			log.error("Error while deleting API key : " + apiKeyName, e);

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.GENERIC_INTERNAL_ERROR.getResponseCode(), 
					"Error while deleting API key : "+ e.getMessage());
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);

		}
	}

	/**
	 * Create a dmaap context
	 * @return DMaaPContext
	 */
	private DMaaPContext getDmaapContext() {
		DMaaPContext dmaapContext = new DMaaPContext();
		dmaapContext.setConfigReader(configReader);
		dmaapContext.setRequest(request);
		dmaapContext.setResponse(response);
		return dmaapContext;
	}

}