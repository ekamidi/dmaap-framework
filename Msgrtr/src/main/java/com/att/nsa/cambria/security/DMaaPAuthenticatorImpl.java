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

import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.security.impl.DMaaPOriginalUebAuthenticator;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.NsaAuthenticator;
import com.att.nsa.security.authenticators.OriginalUebAuthenticator;
import com.att.nsa.security.db.NsaApiDb;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;

/**
 * 
 * @author author
 *
 * @param <K>
 */
public class DMaaPAuthenticatorImpl<K extends NsaApiKey> implements DMaaPAuthenticator<K> {

	private final LinkedList<DMaaPAuthenticator<K>> fAuthenticators;
	


	// Setting timeout to a large value for testing purpose.
	// private static final long kDefaultRequestTimeWindow = 1000 * 60 * 10; //
	// 10 minutes
	private static final long kDefaultRequestTimeWindow = 1000 * 60 * 10 * 10 * 10 * 10 * 10;

	/**
	 * Construct the security manager against an API key database
	 * 
	 * @param db
	 *            the API key db
	 */
	public DMaaPAuthenticatorImpl(NsaApiDb<K> db) {
		this(db, kDefaultRequestTimeWindow);
	}

	
	
	
	/**
	 * Construct the security manager against an API key database with a
	 * specific request time window size
	 * 
	 * @param db
	 *            the API key db
	 * @param authTimeWindowMs
	 *            the size of the time window for request authentication
	 */
	public DMaaPAuthenticatorImpl(NsaApiDb<K> db, long authTimeWindowMs) {
		fAuthenticators = new LinkedList<DMaaPAuthenticator<K>>();

		fAuthenticators.add(new DMaaPOriginalUebAuthenticator<K>(db, authTimeWindowMs));
	}

	/**
	 * Authenticate a user's request. This method returns the API key if the
	 * user is authentic, null otherwise.
	 * 
	 * @param ctx
	 * @return an api key record, or null
	 */
	public K authenticate(DMaaPContext ctx) {
		final HttpServletRequest req = ctx.getRequest();
		for (DMaaPAuthenticator<K> a : fAuthenticators) {
			if (a.qualify(req)) {
				final K k = a.isAuthentic(req);
				if (k != null)
					return k;
			}
			// else: this request doesn't look right to the authenticator
		}
		return null;
	}

	/**
	 * Get the user associated with the incoming request, or null if the user is
	 * not authenticated.
	 * 
	 * @param ctx
	 * @return
	 */
	public static NsaSimpleApiKey getAuthenticatedUser(DMaaPContext ctx) {
		final DMaaPAuthenticator<NsaSimpleApiKey> m = ctx.getConfigReader().getfSecurityManager();
		return m.authenticate(ctx);
	}

	/**
	 * method by default returning false
	 * @param req
	 * @return false
	 */
	public boolean qualify(HttpServletRequest req) {
		return false;
	}
/**
 * method by default returning null
 * @param req
 * @return null
 */
	public K isAuthentic(HttpServletRequest req) {
		return null;
	}
	
	public void addAuthenticator ( DMaaPAuthenticator<K> a )
	{
		this.fAuthenticators.add(a);
	}

}
