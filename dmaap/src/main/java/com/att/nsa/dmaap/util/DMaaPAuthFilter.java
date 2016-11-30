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
package com.att.nsa.dmaap.util;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.springframework.stereotype.Component;

import com.att.cadi.filter.CadiFilter;
import javax.servlet.FilterConfig;


/**
	 * This is a Servlet Filter class
	 * overriding the AjscCadiFilter
	 */
@Component	
public class DMaaPAuthFilter extends CadiFilter {

		
		//private Logger log = Logger.getLogger(DMaaPAuthFilter.class.toString());

		private static final EELFLogger log = EELFManager.getInstance().getLogger(DMaaPAuthFilter.class);
				
		final Boolean enabled = "authentication-scheme-1".equalsIgnoreCase(System.getProperty("CadiAuthN"));
		/**
		 * This method will disable Cadi Authentication 
		 * if cambria headers are present in the request
		 * else continue with Cadi Authentication
		 */
	public void init(FilterConfig filterConfig) throws ServletException {
        
		try {
			super.init(filterConfig);
            
		} catch (Exception ex) {
			log.info("Ajsc Cadi Filter Exception:" + ex.getMessage());
		
		}
	}		 
		@Override
		public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
				ServletException {
			
			log.info("inside servlet filter Cambria Auth Headers checking before doing other Authentication");
			HttpServletRequest request = (HttpServletRequest) req;
			
				boolean forceAAF = Boolean.valueOf(System.getProperty("forceAAF"));
				
				if (forceAAF || null != request.getHeader("Authorization") || (null != request.getHeader("AppName") &&  
				request.getHeader("AppName").equalsIgnoreCase("invenio") && null != request.getHeader("cookie")))
				{
								
					if (!enabled || request.getMethod().equalsIgnoreCase("head") || request.getHeader("DME2HealthCheck") != null) {
						chain.doFilter(req, res);
					} else {
						super.doFilter(req, res, chain);
					}
				} else { 
				
					System.setProperty("CadiAuthN", "authentication-scheme-2");
					chain.doFilter(req, res);
					
				} 

		}

	}

