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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;


import com.att.ajsc.beans.PropertiesMapBean;
import com.att.nsa.dmaap.filemonitor.ServicePropertiesMap;

/**
 * Example JAX-RS Service
 * @author author
 *
 */
@Path("/jaxrs-services")
public class JaxrsEchoService {
  
	/**
	 * Logger obj
	 */
	/*private static final Logger LOGGER = Logger
			.getLogger(JaxrsEchoService.class);*/
	
	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(JaxrsEchoService.class);
	
	/**
    * Method ping 
    * @param input input
    * @return str
    */
	@GET
    @Path("/echo/{input}")
    @Produces("text/plain")
    public String ping(@PathParam("input") String input) {
        return "Hello, " + input + ".";
    }
    
   /**
    * Method to fetch property
    * @param fileName file
    * @param input input
    * @return prop
    */
    @GET
    @Path("/property/{fileName}/{input:.*}")
    @Produces("text/plain")
    public String getProperty(@PathParam("fileName") String fileName, @PathParam("input") String input) {
    	String val=null;
    	try {
    		val = ServicePropertiesMap.getProperty(fileName, input);
    		if(val == null || val.isEmpty() || val.length() < 1){
    			val = PropertiesMapBean.getProperty(fileName, input);
    		}
    	}
    	catch(Exception ex) {
    		LOGGER.info("*** Error retrieving property "+input+": "+ex);
    		
    	} 	 
    	if (val ==null) {
   		 	return "Property is not available";
    	}
    	return "Property value is, " + val +".";
    }  
    
}