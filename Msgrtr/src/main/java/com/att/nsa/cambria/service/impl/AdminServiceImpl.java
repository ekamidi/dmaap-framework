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
package com.att.nsa.cambria.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.cambria.backends.Consumer;
import com.att.nsa.cambria.backends.ConsumerFactory;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.security.DMaaPAuthenticatorImpl;
import com.att.nsa.cambria.service.AdminService;
import com.att.nsa.cambria.utils.DMaaPResponseBuilder;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.limits.Blacklist;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

/**
 * @author author
 *
 */
@Component
public class AdminServiceImpl implements AdminService {

	//private Logger log = Logger.getLogger(AdminServiceImpl.class.toString());
	private static final EELFLogger log = EELFManager.getInstance().getLogger(AdminServiceImpl.class);
	/**
	 * getConsumerCache returns consumer cache
	 * @param dMaaPContext context
	 * @throws IOException ex
	 * @throws AccessDeniedException 
	 */
	@Override	
	public void showConsumerCache(DMaaPContext dMaaPContext) throws IOException, AccessDeniedException {
		adminAuthenticate(dMaaPContext);
		
		JSONObject consumers = new JSONObject();
		JSONArray jsonConsumersList = new JSONArray();

		for (Consumer consumer : getConsumerFactory(dMaaPContext).getConsumers()) {
			JSONObject consumerObject = new JSONObject();
			consumerObject.put("name", consumer.getName());
			consumerObject.put("created", consumer.getCreateTimeMs());
			consumerObject.put("accessed", consumer.getLastAccessMs());
			jsonConsumersList.put(consumerObject);
		}

		consumers.put("consumers", jsonConsumersList);
		log.info("========== AdminServiceImpl: getConsumerCache: " + jsonConsumersList.toString() + "===========");
		DMaaPResponseBuilder.respondOk(dMaaPContext, consumers);
	}

	/**
	 * 
	 * dropConsumerCache() method clears consumer cache
	 * @param dMaaPContext context
	 * @throws JSONException ex
	 * @throws IOException ex
	 * @throws AccessDeniedException 
	 * 
	 */
	@Override
	public void dropConsumerCache(DMaaPContext dMaaPContext) throws JSONException, IOException, AccessDeniedException {
		adminAuthenticate(dMaaPContext);
		getConsumerFactory(dMaaPContext).dropCache();
		DMaaPResponseBuilder.respondOkWithHtml(dMaaPContext, "Consumer cache cleared successfully");
		// log.info("========== AdminServiceImpl: dropConsumerCache: Consumer
		// Cache successfully dropped.===========");
	}

	/** 
	 * getfConsumerFactory returns CosnumerFactory details
	 * @param dMaaPContext contxt
	 * @return ConsumerFactory obj
	 * 
	 */
	private ConsumerFactory getConsumerFactory(DMaaPContext dMaaPContext) {
		return dMaaPContext.getConfigReader().getfConsumerFactory();
	}
	
	/**
	 * return ipblacklist
	 * @param dMaaPContext context
	 * @return blacklist obj
	 */
	private static Blacklist getIpBlacklist(DMaaPContext dMaaPContext) {
		return dMaaPContext.getConfigReader().getfIpBlackList();
	}
	
	
	/**
	 * Get list of blacklisted ips
	 */
	@Override
	public void getBlacklist ( DMaaPContext dMaaPContext ) throws IOException, AccessDeniedException
	{
		adminAuthenticate ( dMaaPContext );

		DMaaPResponseBuilder.respondOk ( dMaaPContext,
			new JSONObject().put ( "blacklist", setToJsonArray ( getIpBlacklist (dMaaPContext).asSet() ) ) );
	}
	
	/**
	 * Add ip to blacklist
	 */
	@Override
	public void addToBlacklist ( DMaaPContext dMaaPContext, String ip ) throws IOException, ConfigDbException, AccessDeniedException
	{
		adminAuthenticate ( dMaaPContext );

		getIpBlacklist (dMaaPContext).add ( ip );
		DMaaPResponseBuilder.respondOkNoContent ( dMaaPContext );
	}
	
	/**
	 * Remove ip from blacklist
	 */
	@Override
	public void removeFromBlacklist ( DMaaPContext dMaaPContext, String ip ) throws IOException, ConfigDbException, AccessDeniedException
	{
		adminAuthenticate ( dMaaPContext );

		getIpBlacklist (dMaaPContext).remove ( ip );
		DMaaPResponseBuilder.respondOkNoContent ( dMaaPContext );
	}
	
	/**
	 * Authenticate if user is admin
	 * @param dMaaPContext context
	 * @throws AccessDeniedException ex
	 */
	private static void adminAuthenticate ( DMaaPContext dMaaPContext ) throws AccessDeniedException
	{
		
		final NsaApiKey user = DMaaPAuthenticatorImpl.getAuthenticatedUser(dMaaPContext);
		if ( user == null || !user.getKey ().equals ( "admin" ) )
		{
			throw new AccessDeniedException ();
		}
	}
	
	public static JSONArray setToJsonArray ( Set<?> fields )
	{
		return collectionToJsonArray ( fields );
	}

	public static JSONArray collectionToJsonArray ( Collection<?> fields )
	{
		final JSONArray a = new JSONArray ();
		if ( fields != null )
		{
			for ( Object o : fields )
			{
				a.put ( o );
			}
		}
		return a;
	}

}
