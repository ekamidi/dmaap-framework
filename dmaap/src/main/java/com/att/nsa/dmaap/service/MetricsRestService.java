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
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.http.HttpStatus;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.exception.DMaaPResponseCode;
import com.att.nsa.cambria.exception.ErrorResponse;
import com.att.nsa.cambria.service.MetricsService;
import com.att.nsa.cambria.utils.ConfigurationReader;

/**
 * This class is a CXF REST service which acts 
 * as gateway for MR Metrics Service.
 * @author author
 *
 */
@Component
@Path("/")
public class MetricsRestService {

	/**
	 * Logger obj
	 */
	//private Logger log = Logger.getLogger(MetricsRestService.class.toString());
	private static final EELFLogger log = EELFManager.getInstance().getLogger(ConfigurationReader.class);
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
	 * MetricsService obj
	 */
	@Autowired
	private MetricsService metricsService;

	/**
	 * Get Metrics method
	 * @throws CambriaApiException ex
	 */
	@GET
	@Produces("text/plain")
	public void getMetrics() throws CambriaApiException {
		try {
			log.info("MetricsRestService: getMetrics : START");
			metricsService.get(getDmaapContext());
			log.info("MetricsRestService: getMetrics : Completed");
		} catch (IOException e) {
			log.error("Error while fetching metrics data : ", e);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN, 
					DMaaPResponseCode.GET_METRICS_ERROR.getResponseCode(), 
					"Error while fetching metrics data"+ e.getMessage());
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * This method is for get the metrics details by the metrics name
	 * 
	 * @param metricName
	 * @throws CambriaApiException 
	 */
	@GET
	@Path("/{metricName}")
	@Produces("text/plain")
	public void getMetricsByName(@PathParam("metricName") String metricName) 
			throws CambriaApiException {

		try {
			log.info("MetricsProducer: getMetricsByName : START");
			metricsService.getMetricByName(getDmaapContext(), metricName);
			log.info("MetricsRestService: getMetricsByName : Completed");
		} catch (IOException | CambriaApiException e) {
			log.error("Error while fetching metrics data : ", e);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.GET_METRICS_ERROR.getResponseCode(), 
					"Error while fetching metrics data"+ e.getMessage());
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
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