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
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
//import org.apache.log4j.Logger;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.exception.DMaaPResponseCode;
import com.att.nsa.cambria.exception.ErrorResponse;
import com.att.nsa.cambria.service.AdminService;
import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

/**
 * Rest Service class
 * for Admin Services
 * @author author
 *
 */
@Component
@Path("/")
public class AdminRestService {

	/**
	 * Logger obj
	 */
	//private static final Logger LOGGER = Logger
		//	.getLogger(AdminRestService.class);
	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(AdminRestService.class);
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
	 * AdminService obj
	 */
	@Autowired
	private AdminService adminService;

	/**
	 * Fetches a list of all the registered consumers along with their created
	 * time and last accessed details
	 * 
	 * @return consumer list in json string format
	 * @throws CambriaApiException 
	 * @throws AccessDeniedException 
	 * @throws IOException
	 * */
	@GET
	@Path("/consumerCache")
	//@Produces(MediaType.TEXT_PLAIN)
	public void getConsumerCache() throws CambriaApiException, AccessDeniedException {
		LOGGER.info("Fetching list of registered consumers.");
		try {
			adminService.showConsumerCache(getDMaaPContext());
			LOGGER.info("Fetching Consumer Cache Successfully");
		} catch (IOException e) {
			LOGGER.error("Error while Fetching list of registered consumers : "
					+ e.getMessage(), e);
			
					
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.GET_CONSUMER_CACHE.getResponseCode(), 
					"Error while Fetching list of registered consumers " + e.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * Clears consumer cache
	 * @throws CambriaApiException ex
	 * @throws AccessDeniedException 
	 * 
	 * @throws IOException ex
	 * @throws JSONException ex
	 * */
	@POST
	@Path("/dropConsumerCache")
	//@Produces(MediaType.TEXT_PLAIN)
	public void dropConsumerCache() throws CambriaApiException, AccessDeniedException {
		LOGGER.info("Dropping consumer cache");
		try {
			adminService.dropConsumerCache(getDMaaPContext());
			LOGGER.info("Dropping Consumer Cache successfully");
		} catch ( AccessDeniedException   excp) {
			LOGGER.error("Error while dropConsumerCache : "
					+ excp.getMessage(), excp);
		
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED, 
					DMaaPResponseCode.GET_BLACKLIST.getResponseCode(), 
					"Error while Fetching list of blacklist ips " + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		} catch (JSONException | IOException e) {
			LOGGER.error(
					"Error while Dropping consumer cache : " + e.getMessage(),
					e);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.DROP_CONSUMER_CACHE.getResponseCode(), 
					"Error while Dropping consumer cache " + e.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}
	
	/**
	 * Get list of blacklisted ips
	 * @throws CambriaApiException excp
	 */
	@GET
	@Path("/blacklist")
	//@Produces(MediaType.TEXT_PLAIN)
	public void getBlacklist() throws CambriaApiException {
		LOGGER.info("Fetching list of blacklist ips.");
		try {
			Enumeration headerNames = getDMaaPContext().getRequest().getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String key = (String) headerNames.nextElement();
				String value = request.getHeader(key);
			
			}
			
			adminService.getBlacklist(getDMaaPContext());
			LOGGER.info("Fetching list of blacklist ips Successfully");
		}catch ( AccessDeniedException   excp) {
			LOGGER.error("Error while Fetching list  of blacklist ips : "
					+ excp.getMessage(), excp);
		
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED, 
					DMaaPResponseCode.GET_BLACKLIST.getResponseCode(), 
					"Error while Fetching list of blacklist ips " + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		} catch ( IOException excp) {
			LOGGER.error("Error while Fetching list  of blacklist ips : "
					+ excp.getMessage(), excp);
		
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.GET_BLACKLIST.getResponseCode(), 
					"Error while Fetching list of blacklist ips " + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		}
			
	}

	/**
	 * Add ip to list of blacklist ips
	 * @param ip ip
	 * @throws CambriaApiException excp
	 */
	@POST
	@Path("/blacklist/{ip}")
	//@Produces(MediaType.TEXT_PLAIN)
	public void addToBlacklist (@PathParam("ip") String ip ) throws CambriaApiException
	{
		LOGGER.info("Adding ip to list of blacklist ips.");
		try {
			adminService.addToBlacklist(getDMaaPContext(), ip);
			LOGGER.info("Fetching list of blacklist ips Successfully");
		} catch ( AccessDeniedException   excp) {
			LOGGER.error("Error while blacklist : "
					+ excp.getMessage(), excp);
		
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED, 
					DMaaPResponseCode.GET_BLACKLIST.getResponseCode(), 
					"Error while Fetching list of blacklist ips " + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		} catch (IOException |  ConfigDbException excp) {
			LOGGER.error("Error while adding ip to list of blacklist ips : "
					+ excp.getMessage(), excp);
		
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.ADD_BLACKLIST.getResponseCode(), 
					"Error while adding ip to list of blacklist ips " + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		}
		
	}
	/**
	 * Remove ip from blacklist
	 * @param ip ip
	 * @throws CambriaApiException excp
	 * @throws AccessDeniedException excp
	 * @throws ConfigDbException excp
	 */
	@DELETE
	@Path("/blacklist/{ip}")
	//@Produces(MediaType.TEXT_PLAIN)
	public void removeFromBlacklist(@PathParam("ip") String ip) throws CambriaApiException, AccessDeniedException, ConfigDbException {
		LOGGER.info("Fetching list of blacklist ips.");
		try {
			adminService.removeFromBlacklist(getDMaaPContext(), ip);
			LOGGER.info("Fetching list of blacklist ips Successfully");
		}catch ( AccessDeniedException   excp) {
			LOGGER.error("Error while blacklist : "
					+ excp.getMessage(), excp);
		
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED, 
					DMaaPResponseCode.GET_BLACKLIST.getResponseCode(), 
					"Error while removeFromBlacklist list of blacklist ips " + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		}  catch (IOException |  ConfigDbException excp) {
			LOGGER.error("Error while removing ip from list of blacklist ips : "
					+ excp.getMessage(), excp);
		
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.REMOVE_BLACKLIST.getResponseCode(), 
					"Error while removing ip from list of blacklist ips " + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		}
	}

	/**
	 * Create a dmaap context
	 * @return DMaaPContext
	 */
	private DMaaPContext getDMaaPContext() {
		DMaaPContext dmaaPContext = new DMaaPContext();
		dmaaPContext.setConfigReader(configReader);
		dmaaPContext.setRequest(request);
		dmaaPContext.setResponse(response);
		return dmaaPContext;
	}

}
