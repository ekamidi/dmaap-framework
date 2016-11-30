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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.att.aft.dme2.api.DME2Exception;
import com.att.aft.dme2.api.DME2Manager;
import com.att.aft.dme2.manager.registry.DME2EndpointRegistry;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.cambria.service.impl.EventsServiceImpl;

/**
 * 
 * @author author
 *
 */
public class DME2EndPointLoader {

	private String latitude;
	private String longitude;
	private String version;
	private String serviceName;
	private String env;
	private String routeOffer;
	private String hostName;
	private String port;
	private String contextPath;
	private String protocol;
	private String serviceURL;
	private static DME2EndPointLoader loader = new DME2EndPointLoader();
//	private static final Logger LOG = LoggerFactory.getLogger(EventsServiceImpl.class);
	private static final EELFLogger LOG = EELFManager.getInstance().getLogger(EventsServiceImpl.class);
	private DME2EndPointLoader() {
	}

	public static DME2EndPointLoader getInstance() {
		return loader;
	}

	/**
	 * publishing endpoints
	 */
	public void publishEndPoints() {

		try {
			InputStream input = this.getClass().getResourceAsStream("/endpoint.properties");
			Properties props = new Properties();
			props.load(input);

			latitude = props.getProperty("Latitude");
			longitude = props.getProperty("Longitude");
			version = props.getProperty("Version");
			serviceName = props.getProperty("ServiceName");
			env = props.getProperty("Environment");
			routeOffer = props.getProperty("RouteOffer");
			hostName = props.getProperty("HostName");
			port = props.getProperty("Port");
			contextPath = props.getProperty("ContextPath");
			protocol = props.getProperty("Protocol");

			System.setProperty("AFT_LATITUDE", latitude);
			System.setProperty("AFT_LONGITUDE", longitude);
			System.setProperty("AFT_ENVIRONMENT", "AFTUAT");

			serviceURL = "service=" + serviceName + "/" + "version=" + version + "/" + "envContext=" + env + "/"
					+ "routeOffer=" + routeOffer;

			DME2Manager manager = new DME2Manager("testEndpointPublish", props);
			manager.setClientCredentials("sh301n", "");
			DME2EndpointRegistry svcRegistry = manager.getEndpointRegistry();
			// Publish API takes service name, context path, hostname, port and
			// protocol as args
			svcRegistry.publish(serviceURL, contextPath, hostName, Integer.parseInt(port), protocol);

		} catch (IOException | DME2Exception e) {
			LOG.error("Failed due to :" + e);
		}

	}
/**
 * unpublishing endpoints
 */
	public void unPublishEndPoints() {

		DME2Manager manager;
		try {
			System.setProperty("AFT_LATITUDE", latitude);
			System.setProperty("AFT_LONGITUDE", longitude);
			System.setProperty("AFT_ENVIRONMENT", "AFTUAT");

			manager = DME2Manager.getDefaultInstance();
			DME2EndpointRegistry svcRegistry = manager.getEndpointRegistry();
			svcRegistry.unpublish(serviceURL, hostName, Integer.parseInt(port));
		} catch (DME2Exception e) {
			LOG.error("Failed due to DME2Exception" + e);
		}

	}

}
