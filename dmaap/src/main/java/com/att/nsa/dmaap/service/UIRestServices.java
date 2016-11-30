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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import kafka.common.TopicExistsException;

import org.apache.http.HttpStatus;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.service.UIService;
import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.cambria.utils.DMaaPResponseBuilder;
import com.att.nsa.configs.ConfigDbException;

/**
 * UI Rest Service
 * @author author
 *
 */
@Component
public class UIRestServices {

	/**
	 * Logger obj
	 */
	//private static final Logger LOGGER = Logger.getLogger(UIRestServices.class);
	
	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(UIRestServices.class);

	@Autowired
	private UIService uiService;
	
	/**
	 * Config Reader
	 */
	@Autowired
	@Qualifier("configurationReader")
	private ConfigurationReader configReader;

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
	 * getting the hello
	 */
	@GET
	@Path("/")
	public void hello() {
		try {
			LOGGER.info("Calling hello page.");

			uiService.hello(getDmaapContext());

			LOGGER.info("Hello page is returned.");
		} catch (IOException excp) {
			LOGGER.error("Error while calling hello page: " + excp.getMessage(), excp);
			DMaaPResponseBuilder.respondWithError(getDmaapContext(), HttpStatus.SC_NOT_FOUND,
					"Error while calling hello page: " + excp.getMessage());
		}
	}

	/**
	 * getApikeysTable
	 */
	@GET
	@Path("/ui/apikeys")
	public void getApiKeysTable() {
		try {
			LOGGER.info("Fetching list of all api keys.");

			uiService.getApiKeysTable(getDmaapContext());

			LOGGER.info("Returning list of all api keys.");
		} catch (ConfigDbException | IOException excp) {
			LOGGER.error("Error while fetching list of all api keys: " + excp.getMessage(), excp);
			DMaaPResponseBuilder.respondWithError(getDmaapContext(), HttpStatus.SC_NOT_FOUND,
					"Error while fetching list of all api keys: " + excp.getMessage());
		}
	}

	/**
	 * getApiKey
	 * 
	 * @param apiKey
	 * @exception Exception
	 */
	@GET
	@Path("/ui/apikeys/{apiKey}")
	public void getApiKey(@PathParam("apiKey") String apiKey) {
		try {
			LOGGER.info("Fetching details of api key: " + apiKey);

			uiService.getApiKey(getDmaapContext(), apiKey);

			LOGGER.info("Returning details of api key: " + apiKey);
		} catch (Exception excp) {
			LOGGER.error("Error while fetching details of api key: " + apiKey, excp);
			DMaaPResponseBuilder.respondWithError(getDmaapContext(), HttpStatus.SC_NOT_FOUND,
					"Error while fetching details of api key: " + apiKey);
		}
	}

	@GET
	@Path("/ui/topics")
	public void getTopicsTable() {
		try {
			LOGGER.info("Fetching list of all topics.");

			uiService.getTopicsTable(getDmaapContext());

			LOGGER.info("Returning list of all topics.");
		} catch (ConfigDbException | IOException excp) {
			LOGGER.error("Error while fetching list of all topics: " + excp, excp);
			DMaaPResponseBuilder.respondWithError(getDmaapContext(), HttpStatus.SC_NOT_FOUND,
					"Error while fetching list of all topics: " + excp.getMessage());
		}
	}

	/**
	 * 
	 * @param topic
	 */
	@GET
	@Path("/ui/topics/{topic}")
	public void getTopic(@PathParam("topic") String topic) {
		try {
			LOGGER.info("Fetching details of topic: " + topic);

			uiService.getTopic(getDmaapContext(), topic);

			LOGGER.info("Returning details of topic: " + topic);
		} catch (ConfigDbException | IOException | TopicExistsException excp) {
			LOGGER.error("Error while fetching details of topic: " + topic, excp);
			DMaaPResponseBuilder.respondWithError(getDmaapContext(), HttpStatus.SC_NOT_FOUND,
					"Error while fetching details of topic: " + topic);
		}
	}

	/**
	 * This method is used for taking Configuration Object,HttpServletRequest
	 * Object,HttpServletRequest HttpServletResponse Object,HttpServletSession
	 * Object.
	 * 
	 * @return DMaaPContext object from where user can get Configuration
	 *         Object,HttpServlet Object
	 * 
	 */
	private DMaaPContext getDmaapContext() {
		DMaaPContext dmaapContext = new DMaaPContext();
		dmaapContext.setConfigReader(configReader);
		dmaapContext.setRequest(request);
		dmaapContext.setResponse(response);
		return dmaapContext;
	}
}
