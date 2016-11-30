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

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.drumlin.till.nv.rrNvReadable;

/**
 * 
 * 
 * @author author
 *
 *
 */
public class DMaaPCuratorFactory {
	/**
	 * 
	 * method provide CuratorFramework object
	 * 
	 * @param settings
	 * @return
	 * 
	 * 
	 * 
	 */
	public static CuratorFramework getCurator(rrNvReadable settings) {
		String Setting_ZkConfigDbServers =com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, CambriaConstants.kSetting_ZkConfigDbServers);
		 
		if(null==Setting_ZkConfigDbServers)
			 Setting_ZkConfigDbServers =CambriaConstants.kDefault_ZkConfigDbServers; 
		
		String strSetting_ZkSessionTimeoutMs = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, CambriaConstants.kSetting_ZkSessionTimeoutMs);
		if (strSetting_ZkSessionTimeoutMs==null) strSetting_ZkSessionTimeoutMs = CambriaConstants.kDefault_ZkSessionTimeoutMs+"";
		int Setting_ZkSessionTimeoutMs = Integer.parseInt(strSetting_ZkSessionTimeoutMs);
		
		String str_ZkConnectionTimeoutMs = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, CambriaConstants.kSetting_ZkSessionTimeoutMs);
		if (str_ZkConnectionTimeoutMs==null) str_ZkConnectionTimeoutMs = CambriaConstants.kDefault_ZkConnectionTimeoutMs+"";
		int setting_ZkConnectionTimeoutMs = Integer.parseInt(str_ZkConnectionTimeoutMs);
		
		
		CuratorFramework curator = CuratorFrameworkFactory.newClient(
				Setting_ZkConfigDbServers,Setting_ZkSessionTimeoutMs,setting_ZkConnectionTimeoutMs
				,new ExponentialBackoffRetry(1000, 5));
		return curator;
	}
}
