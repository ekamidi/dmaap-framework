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
package com.att.nsa.cambria.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.drumlin.till.nv.impl.nvPropertiesFile;
import com.att.nsa.drumlin.till.nv.impl.nvReadableStack;
import com.att.nsa.drumlin.till.nv.impl.nvReadableTable;

/**
 * 
 * @author 
 *
 *
 */
public class PropertyReader extends nvReadableStack {
	/**
	 * 
	 * initializing logger
	 * 
	 */
	//private static final Logger LOGGER = Logger.getLogger(PropertyReader.class);
	private static final EELFLogger log = EELFManager.getInstance().getLogger(PropertyReader.class);
//	private static final String MSGRTR_PROPERTIES_FILE = "msgRtrApi.properties";

	/**
	 * constructor initialization
	 * 
	 * @throws loadException
	 * 
	 */
	public PropertyReader() throws loadException {
	/*	Map<String, String> argMap = new HashMap<String, String>();
		final String config = getSetting(argMap, CambriaConstants.kConfig, MSGRTR_PROPERTIES_FILE);
		final URL settingStream = findStream(config, ConfigurationReader.class);
		push(new nvPropertiesFile(settingStream));
		push(new nvReadableTable(argMap));*/
	}

	/**
	 * 
	 * 
	 * @param argMap
	 * @param key
	 * @param defaultValue
	 * @return
	 * 
	 */
	@SuppressWarnings("unused")
	private static String getSetting(Map<String, String> argMap, final String key, final String defaultValue) {
		String val = (String) argMap.get(key);
		if (null == val) {
			return defaultValue;
		}
		return val;
	}

	/**
	 * 
	 * @param resourceName
	 * @param clazz
	 * @return
	 * @exception MalformedURLException
	 * 
	 */
	/*public static URL findStream(final String resourceName, Class<?> clazz) {
		try {
			File file = new File(resourceName);

			if (file.isAbsolute()) {
				return file.toURI().toURL();
			}

			String filesRoot = System.getProperty("RRWT_FILES", null);

			if (null != filesRoot) {

				String fullPath = filesRoot + "/" + resourceName;

				LOGGER.debug("Looking for [" + fullPath + "].");

				file = new File(fullPath);
				if (file.exists()) {
					return file.toURI().toURL();
				}
			}

			URL res = clazz.getClassLoader().getResource(resourceName);

			if (null != res) {
				return res;
			}

			res = ClassLoader.getSystemResource(resourceName);

			if (null != res) {
				return res;
			}
		} catch (MalformedURLException e) {
			LOGGER.error("Unexpected failure to convert a local filename into a URL: " + e.getMessage(), e);
		}
		return null;
	}
*/
}
