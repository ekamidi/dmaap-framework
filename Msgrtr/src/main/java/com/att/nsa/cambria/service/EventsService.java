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
package com.att.nsa.cambria.service;

import java.io.IOException;
import java.io.InputStream;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.backends.ConsumerFactory.UnavailableException;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.metabroker.Broker.TopicExistsException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;

/**
 * 
 * @author author
 *
 */
public interface EventsService {
	/**
	 * 
	 * @param ctx
	 * @param topic
	 * @param consumerGroup
	 * @param clientId
	 * @throws ConfigDbException
	 * @throws TopicExistsException
	 * @throws AccessDeniedException
	 * @throws UnavailableException
	 * @throws CambriaApiException
	 * @throws IOException
	 */
	public void getEvents(DMaaPContext ctx, String topic, String consumerGroup, String clientId)
			throws ConfigDbException, TopicExistsException,UnavailableException,
			CambriaApiException, IOException,AccessDeniedException;

	/**
	 * 
	 * @param ctx
	 * @param topic
	 * @param msg
	 * @param defaultPartition
	 * @param requestTime
	 * @throws ConfigDbException
	 * @throws AccessDeniedException
	 * @throws TopicExistsException
	 * @throws CambriaApiException
	 * @throws IOException
	 */
	public void pushEvents(DMaaPContext ctx, final String topic, InputStream msg, final String defaultPartition,
			final String requestTime) throws ConfigDbException, AccessDeniedException, TopicExistsException,
					CambriaApiException, IOException,missingReqdSetting;

}
