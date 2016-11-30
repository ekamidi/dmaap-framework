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
package com.att.nsa.cambria.beans;

import java.security.Key;

//import org.apache.log4-j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.configs.ConfigDb;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.configs.confimpl.EncryptingLayer;
import com.att.nsa.drumlin.till.nv.rrNvReadable;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.security.db.BaseNsaApiDbImpl;
import com.att.nsa.security.db.EncryptingApiDbImpl;
import com.att.nsa.security.db.NsaApiDb;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;
import com.att.nsa.security.db.simple.NsaSimpleApiKeyFactory;
import com.att.nsa.util.rrConvertor;

/**
 * 
 * @author author
 *
 */
public class DMaaPNsaApiDb {
	
	//private rrNvReadable settings;
	private DMaaPZkConfigDb cdb;
	
	//private static final Logger log = Logger
		//	.getLogger(DMaaPNsaApiDb.class.toString());
	private static final EELFLogger log = EELFManager.getInstance().getLogger(DMaaPNsaApiDb.class);
	
/**
 * 
 * Constructor initialized
 * @param settings
 * @param cdb
 */
	@Autowired
	public DMaaPNsaApiDb(rrNvReadable settings, DMaaPZkConfigDb cdb) {
		//this.setSettings(settings);
		this.setCdb(cdb);
	}
	/**
	 * 
	 * @param settings
	 * @param cdb
	 * @return
	 * @throws ConfigDbException
	 * @throws missingReqdSetting
	 */
	public static NsaApiDb<NsaSimpleApiKey> buildApiKeyDb(
			rrNvReadable settings, ConfigDb cdb) throws ConfigDbException,
			missingReqdSetting {
		// Cambria uses an encrypted api key db

		//final String keyBase64 = settings.getString("cambria.secureConfig.key",			null);
		final String keyBase64 =com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"cambria.secureConfig.key");
		
		
	//	final String initVectorBase64 = settings.getString(				"cambria.secureConfig.iv", null);
	final String initVectorBase64 =com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"cambria.secureConfig.iv");
		// if neither value was provided, don't encrypt api key db
		if (keyBase64 == null && initVectorBase64 == null) {
			log.info("This server is configured to use an unencrypted API key database. See the settings documentation.");
			return new BaseNsaApiDbImpl<NsaSimpleApiKey>(cdb,
					new NsaSimpleApiKeyFactory());
		} else if (keyBase64 == null) {
			// neither or both, otherwise something's goofed
			throw new missingReqdSetting("cambria.secureConfig.key");
		} else if (initVectorBase64 == null) {
			// neither or both, otherwise something's goofed
			throw new missingReqdSetting("cambria.secureConfig.iv");
		} else {
			log.info("This server is configured to use an encrypted API key database.");
			final Key key = EncryptingLayer.readSecretKey(keyBase64);
			final byte[] iv = rrConvertor.base64Decode(initVectorBase64);
			return new EncryptingApiDbImpl<NsaSimpleApiKey>(cdb,
					new NsaSimpleApiKeyFactory(), key, iv);
		}
	}

	/**
	 * @return
	 * returns settings
	 */
/*	public rrNvReadable getSettings() {
		return settings;
	}*/

	/**
	 * @param settings
	 * set settings
	 */
	/*public void setSettings(rrNvReadable settings) {
		this.settings = settings;
	}*/

	 /**
	 * @return
	 * returns cbd
	 */
	public DMaaPZkConfigDb getCdb() {
		return cdb;
	}
	/**
	 * @param cdb
	 * set cdb
	 */
	public void setCdb(DMaaPZkConfigDb cdb) {
		this.cdb = cdb;
	}


}
