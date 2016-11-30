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
package com.att.nsa.cambria.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

/**
 * This is the Cambria Servlet Context Listner which helpes while loading the app which provide the endpoints 
 * @author author
 *
 */
public class CambriaServletContextListener implements ServletContextListener {
	
	DME2EndPointLoader loader = DME2EndPointLoader.getInstance();
//	private static Logger log = Logger.getLogger(CambriaServletContextListener.class);
	private static final EELFLogger log = EELFManager.getInstance().getLogger(CambriaServletContextListener.class);
	

	@Override
	
	/**
	 * contextDestroyed() loads unpublished end points
	 * @param arg0
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		log.info("CambriaServletContextListener contextDestroyed");
		
		loader.unPublishEndPoints();
	}

	@Override
	/**
	 * contextInitialized() loads published end points
	 * @param arg0
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		log.info("CambriaServletContextListener contextInitialized");
		loader.publishEndPoints();
	}

}
