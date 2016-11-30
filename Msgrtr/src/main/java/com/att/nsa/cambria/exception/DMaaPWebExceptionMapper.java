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
package com.att.nsa.cambria.exception;

import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.apache.http.HttpStatus;
//import org.apache.log-4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Exception Mapper class to handle
 * Jersey Exceptions
 * @author author
 *
 */
@Provider
@Singleton
public class DMaaPWebExceptionMapper implements ExceptionMapper<WebApplicationException>{
	
	//private static final Logger LOGGER = Logger
		//	.getLogger(DMaaPWebExceptionMapper.class);
	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(DMaaPWebExceptionMapper.class);
	private ErrorResponse errRes;
	
	@Autowired
	private DMaaPErrorMessages msgs;
	
	public DMaaPWebExceptionMapper() {
		super();
		LOGGER.info("WebException Mapper Created..");
	}

	@Override
	public Response toResponse(WebApplicationException ex) {
		
		LOGGER.info("Reached WebException Mapper");
		
		/**
		 * Resource Not Found
		 */
		if(ex instanceof NotFoundException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,DMaaPResponseCode.RESOURCE_NOT_FOUND.getResponseCode(),msgs.getNotFound());
			
			LOGGER.info(errRes.toString());
			
			return Response.status(errRes.getHttpStatusCode()).entity(errRes).type(MediaType.APPLICATION_JSON)
		            .build();
			
		}
		
		if(ex instanceof InternalServerErrorException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR,DMaaPResponseCode.SERVER_UNAVAILABLE.getResponseCode(),msgs.getServerUnav());
			
			LOGGER.info(errRes.toString());
			return Response.status(errRes.getHttpStatusCode()).entity(errRes).type(MediaType.APPLICATION_JSON)
		            .build();
			
		}
		
		if(ex instanceof NotAuthorizedException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),msgs.getAuthFailure());
			
			LOGGER.info(errRes.toString());
			return Response.status(errRes.getHttpStatusCode()).entity(errRes).type(MediaType.APPLICATION_JSON)
		            .build();
		}
		
		if(ex instanceof BadRequestException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_BAD_REQUEST,DMaaPResponseCode.INCORRECT_JSON.getResponseCode(),msgs.getBadRequest());
			
			LOGGER.info(errRes.toString());
			return Response.status(errRes.getHttpStatusCode()).entity(errRes).type(MediaType.APPLICATION_JSON)
		            .build();
		}
		if(ex instanceof NotAllowedException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_METHOD_NOT_ALLOWED,DMaaPResponseCode.METHOD_NOT_ALLOWED.getResponseCode(),msgs.getMethodNotAllowed());
			
			LOGGER.info(errRes.toString());
			return Response.status(errRes.getHttpStatusCode()).entity(errRes).type(MediaType.APPLICATION_JSON)
		            .build();
		}
		
		if(ex instanceof ServiceUnavailableException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_SERVICE_UNAVAILABLE,DMaaPResponseCode.SERVER_UNAVAILABLE.getResponseCode(),msgs.getServerUnav());
			
			LOGGER.info(errRes.toString());
			return Response.status(errRes.getHttpStatusCode()).entity(errRes).type(MediaType.APPLICATION_JSON)
		            .build();
		}
		
		
		return Response.serverError().build();
	}

	

	
}
