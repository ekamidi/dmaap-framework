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
package com.att.nsa.cambria.security;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.cambria.exception.DMaaPResponseCode;
import com.att.nsa.cambria.exception.ErrorResponse;
import com.att.nsa.cambria.utils.Utils;


/**
 * 
 * @author author
 *
 */
public class DMaaPAAFAuthenticatorImpl implements DMaaPAAFAuthenticator {

	/**
	 * @param req
	 * @param role
	 */
	@Override
	public boolean aafAuthentication(HttpServletRequest req, String role) {
		boolean auth = false;
		if(req.isUserInRole(role))
		{
			
			auth = true;
		}
		return auth;
	}

	@Override
	public String aafPermissionString(String topicName, String action) throws CambriaApiException {
		
		
		String permission = "";
		String nameSpace ="";
		if(topicName.contains(".") && topicName.contains("com.att")) {
			//String topic = topicName.substring(topicName.lastIndexOf(".")+1);
			nameSpace = topicName.substring(0,topicName.lastIndexOf("."));
		}
		else {
			nameSpace = null;
			 nameSpace= com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"defaultNSforUEB");
			
			if(null==nameSpace)nameSpace="com.att.dmaap.mr.ueb";
			
			
			/*ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN,
					DMaaPResponseCode.TOPIC_NOT_IN_AAF.getResponseCode(), "Topic does not exist in AAF"
							, null, Utils.getFormattedDate(new Date()), topicName,
					null, null, null, null);
					
			throw new CambriaApiException(errRes);*/
		}
		
		permission = nameSpace+".mr.topic|:topic."+topicName+"|"+action;
		return permission;
		
	}
	
	

}
