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
package com.att.nsa.cambria.security.impl;

import javax.servlet.http.HttpServletRequest;



import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.security.DMaaPAuthenticator;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.authenticators.MechIdAuthenticator;
//import com.att.nsa.security.db.NsaApiDb;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

/**
 * An authenticator for AT&T MechIds.
 * 
 * @author author
 *
 * @param <K>
 */
public class DMaaPMechIdAuthenticator <K extends NsaApiKey> implements DMaaPAuthenticator<K> {

/**
 * This is not yet implemented. by refault its returing false
 * @param req HttpServletRequest
 * @return false
 */
	public boolean qualify (HttpServletRequest req) {
		// we haven't implemented anything here yet, so there's no qualifying request
		return false;
	}
/**
 * This metod authenticate the mech id 
 * @param req
 * @return APIkey or null
 */
	public K isAuthentic (HttpServletRequest req) {
		final String remoteAddr = req.getRemoteAddr();
		authLog ( "MechId auth is not yet implemented.", remoteAddr );
		return null;
	}

	private static void authLog ( String msg, String remoteAddr )
	{
		log.info ( "AUTH-LOG(" + remoteAddr + "): " + msg );
	}

//	private final NsaApiDb<K> fDb;
	//private static final Logger log = Logger.getLogger( MechIdAuthenticator.class.toString());
	private static final EELFLogger log = EELFManager.getInstance().getLogger(MechIdAuthenticator.class);
/**
 * Curently its not yet implemented returning null
 * @param ctx DMaaP context
 * @return APIkey or null
 */
	@Override
	public K authenticate(DMaaPContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
@Override
public void addAuthenticator(DMaaPAuthenticator<K> a) {
	// TODO Auto-generated method stub
	
}

}