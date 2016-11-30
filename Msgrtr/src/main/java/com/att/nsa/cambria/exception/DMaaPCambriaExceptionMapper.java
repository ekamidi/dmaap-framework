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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.apache.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;

import com.att.nsa.cambria.CambriaApiException;

/**
 * Exception Mapper class to handle
 * CambriaApiException 
 * @author author
 *
 */
@Provider
@Singleton
public class DMaaPCambriaExceptionMapper implements ExceptionMapper<CambriaApiException>{

private ErrorResponse errRes;

//private static final Logger LOGGER = Logger.getLogger(DMaaPCambriaExceptionMapper.class);
private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(DMaaPCambriaExceptionMapper.class);
	
	@Autowired
	private DMaaPErrorMessages msgs;
	
	public DMaaPCambriaExceptionMapper() {
		super();
		LOGGER.info("Cambria Exception Mapper Created..");
	}
	
	@Override
	public Response toResponse(CambriaApiException ex) {

		LOGGER.info("Reached Cambria Exception Mapper..");
		
		/**
		 * Cambria Generic Exception
		 */
		if(ex instanceof CambriaApiException)
		{
			
			errRes = ex.getErrRes();
			if(errRes!=null) {
				
				return Response.status(errRes.getHttpStatusCode()).entity(errRes).type(MediaType.APPLICATION_JSON)
		            .build();
			}
			else
			{
				return Response.status(ex.getStatus()).entity(ex.getMessage()).type(MediaType.APPLICATION_JSON)
			            .build();
			}
			
			
		}
		else
		{
			errRes = new ErrorResponse(HttpStatus.SC_EXPECTATION_FAILED, DMaaPResponseCode.SERVER_UNAVAILABLE.getResponseCode(), msgs.getServerUnav());
			return Response.status(HttpStatus.SC_EXPECTATION_FAILED).entity(errRes).type(MediaType.APPLICATION_JSON).build();
		}
		
	}

	
}
