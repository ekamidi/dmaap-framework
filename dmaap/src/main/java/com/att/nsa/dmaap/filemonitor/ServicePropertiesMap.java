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
package com.att.nsa.dmaap.filemonitor;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ServicePropertiesMap class
 * @author author
 *
 */
@SuppressWarnings("squid:S1118") 
public class ServicePropertiesMap 
{
	private static HashMap<String, HashMap<String, String>> mapOfMaps = 
			new HashMap<String, HashMap<String, String>>();
//	static final Logger logger = LoggerFactory.getLogger(ServicePropertiesMap.class);

	private static final EELFLogger logger = EELFManager.getInstance().getLogger(ServicePropertiesMap.class);
	/**
	 * refresh method
	 * @param file file
	 * @throws Exception ex
	 */
	public static void refresh(File file) throws Exception
	{
		try
		{
			logger.info("Loading properties - " + (file != null?file.getName():""));
			
			//Store .json & .properties files into map of maps
			String filePath = file.getPath();
			
			if(filePath.lastIndexOf(".json")>0){
				
				ObjectMapper om = new ObjectMapper();
				TypeReference<HashMap<String, String>> typeRef = 
						new TypeReference<HashMap<String, String>>() {};
				HashMap<String, String> propMap = om.readValue(file, typeRef);
				HashMap<String, String> lcasePropMap = new HashMap<String, String>();
				for (String key : propMap.keySet() )
				{
					String lcaseKey = ifNullThenEmpty(key);
					lcasePropMap.put(lcaseKey, propMap.get(key));
				}
				
				mapOfMaps.put(file.getName(), lcasePropMap);
				
				
			}else if(filePath.lastIndexOf(".properties")>0){
				Properties prop = new Properties();
				FileInputStream fis = new FileInputStream(file);
				prop.load(fis);
				
				@SuppressWarnings("unchecked")
				HashMap<String, String> propMap = new HashMap<String, String>((Map)prop);
				
				mapOfMaps.put(file.getName(), propMap);
			}

			logger.info("File - " + file.getName() + " is loaded into the map and the "
					+ "corresponding system properties have been refreshed");
		}
		catch (Exception e)
		{
			logger.error("File " + (file != null?file.getName():"") + " cannot be loaded into the map ", e);
			throw new Exception("Error reading map file " + (file != null?file.getName():""), e);
		}
	}
	/**
	 * Get property
	 * @param fileName fileName
	 * @param propertyKey propertyKey
	 * @return str
	 */
	public static String getProperty(String fileName, String propertyKey)
	{
		HashMap<String, String> propMap = mapOfMaps.get(fileName);
		return propMap!=null?propMap.get(ifNullThenEmpty(propertyKey)):"";
	}
	/**
	 * get properties
	 * @param fileName fileName
	 * @return mapProp
	 */
	public static HashMap<String, String> getProperties(String fileName){
		return mapOfMaps.get(fileName);
	}
	
	private static String ifNullThenEmpty(String key) {
		if (key == null) {
			return "";
		} else {					
			return key;
		}		
	}

}
