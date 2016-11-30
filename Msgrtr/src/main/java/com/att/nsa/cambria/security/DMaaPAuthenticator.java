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

import javax.servlet.http.HttpServletRequest;

import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.security.NsaApiKey;


/**
 * An interface for authenticating an inbound request.
 * @author author
 *
 * @param <K> NsaApiKey
 */
public interface DMaaPAuthenticator<K extends NsaApiKey> {

	/**
	 * Qualify a request as possibly using the authentication method that this class implements.
	 * @param req
	 * @return true if the request might be authenticated by this class
	 */
	boolean qualify ( HttpServletRequest req );
	
	/**
	 * Check for a request being authentic. If it is, return the API key. If not, return null.
	 * @param req An inbound web request
	 * @return the API key for an authentic request, or null
	 */
	K isAuthentic ( HttpServletRequest req );
	/**
	 * Check for a ctx being authenticate. If it is, return the API key. If not, return null.
	 * @param ctx
	 * @return the API key for an authentication request, or null
	 */
	K authenticate ( DMaaPContext ctx );
	
	
	void addAuthenticator(DMaaPAuthenticator<K> a);

}
