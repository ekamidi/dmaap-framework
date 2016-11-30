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
package com.att.nsa.dmaap;


import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.http.HttpStatus;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.att.nsa.cambria.exception.DMaaPErrorMessages;
import com.att.nsa.cambria.exception.DMaaPResponseCode;
import com.att.nsa.cambria.exception.ErrorResponse;

/**
 * Exception Mapper class to handle
 * Web Exceptions
 * @author author
 *
 */
@Provider
@Singleton
public class DMaaPWebExceptionMapper implements ExceptionMapper<WebApplicationException>{
	
	/**
	 * Logger obj
	 */

	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(DMaaPWebExceptionMapper.class);
	/**
	 * Error response obj
	 */
	private ErrorResponse errRes;
	/**
	 * Error msg obj
	 */
	@Autowired
	private DMaaPErrorMessages msgs;
	
	/**
	 * Contructor for DMaaPWebExceptionMapper
	 */
	public DMaaPWebExceptionMapper() {
		super();
		LOGGER.info("WebException Mapper Created..");
	}

	/**
	 * The toResponse method is called when 
	 * an exception of type WebApplicationException
	 * is thrown.This method will send a custom error
	 * response to the client
	 */
	@Override
	public Response toResponse(WebApplicationException ex) {
		
		LOGGER.info("Reached WebException Mapper");
		
		/**
		 * Resource Not Found
		 */
		if(ex instanceof NotFoundException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,DMaaPResponseCode.RESOURCE_NOT_FOUND.
					getResponseCode(),msgs.getNotFound());
			
			LOGGER.info(errRes.toString());
			Response response = Response.status(errRes.getHttpStatusCode()).header("exception", 
					errRes.getErrMapperStr()).build();
			
			return response;
			
		}
		/**
		 * Internal Server Error
		 */
		if(ex instanceof InternalServerErrorException)
		{
		
			int errCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
			int dmaapErrCode = DMaaPResponseCode.SERVER_UNAVAILABLE.getResponseCode();
			String errMsg = msgs.getServerUnav();
			
		
			if(ex.getCause().toString().contains("Json")) {
				errCode = HttpStatus.SC_BAD_REQUEST;
				dmaapErrCode = DMaaPResponseCode.INCORRECT_JSON.getResponseCode();
				errMsg = ex.getCause().getMessage().substring(0, ex.getCause().getMessage().indexOf("[Source")-3);
			}
			else if (ex.getCause().toString().contains("UnrecognizedPropertyException")) {
				errCode = HttpStatus.SC_BAD_REQUEST;
				dmaapErrCode = DMaaPResponseCode.INCORRECT_JSON.getResponseCode();
				errMsg = ex.getCause().getMessage().substring(0, ex.getCause().getMessage().indexOf("[Source")-3);
			}
			errRes = new ErrorResponse(errCode,dmaapErrCode,errMsg);
			
			LOGGER.info(errRes.toString());
			Response response = Response.status(errRes.getHttpStatusCode()).header("exception", 
					errRes.getErrMapperStr()).build();
			
			return response;
			
		}
		/**
		 * UnAuthorized 
		 */
		if(ex instanceof NotAuthorizedException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,DMaaPResponseCode.ACCESS_NOT_PERMITTED.
					getResponseCode(),msgs.getAuthFailure());
			
			LOGGER.info(errRes.toString());
			Response response = Response.status(errRes.getHttpStatusCode()).header("exception", 
					errRes.getErrMapperStr()).build();
			
			return response;
		}
		/**
		 * Malformed request
		 */
		if(ex instanceof BadRequestException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_BAD_REQUEST,DMaaPResponseCode.INCORRECT_JSON.
					getResponseCode(),msgs.getBadRequest());
			
			LOGGER.info(errRes.toString());
			Response response = Response.status(errRes.getHttpStatusCode()).header("exception", 
					errRes.getErrMapperStr()).build();
			
			return response;
		}
		/**
		 * HTTP Method not allowed
		 */
		if(ex instanceof NotAllowedException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_METHOD_NOT_ALLOWED,DMaaPResponseCode.METHOD_NOT_ALLOWED.
					getResponseCode(),msgs.getMethodNotAllowed());
			
			LOGGER.info(errRes.toString());
			Response response = Response.status(errRes.getHttpStatusCode()).header("exception", 
					errRes.getErrMapperStr()).build();
			
			return response;
		}
		
		/**
		 * Server unavailable
		 */
		if(ex instanceof ServiceUnavailableException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_SERVICE_UNAVAILABLE,DMaaPResponseCode.SERVER_UNAVAILABLE.
					getResponseCode(),msgs.getServerUnav());
			
			LOGGER.info(errRes.toString());
			Response response = Response.status(errRes.getHttpStatusCode()).header("exception", 
					errRes.getErrMapperStr()).build();
			
			return response;
		}
		
		
		
		return Response.serverError().build();
	}

	

	
}

