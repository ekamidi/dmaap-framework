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

import org.json.JSONException;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.beans.TopicBean;
import com.att.nsa.cambria.metabroker.Broker.TopicExistsException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.configs.ConfigDbException;

/**
 * interface provide all the topic related operations
 * 
 * @author author
 *
 */
public interface TopicService {
	/**
	 * method fetch details of all the topics
	 * 
	 * @param dmaapContext
	 * @throws JSONException
	 * @throws ConfigDbException
	 * @throws IOException
	 */
	void getTopics(DMaaPContext dmaapContext) throws JSONException, ConfigDbException, IOException;
	void getAllTopics(DMaaPContext dmaapContext) throws JSONException, ConfigDbException, IOException;

	/**
	 * method fetch details of specific topic
	 * 
	 * @param dmaapContext
	 * @param topicName
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 */
	void getTopic(DMaaPContext dmaapContext, String topicName)
			throws ConfigDbException, IOException, TopicExistsException;

	/**
	 * method used to create the topic
	 * 
	 * @param dmaapContext
	 * @param topicBean
	 * @throws CambriaApiException
	 * @throws TopicExistsException
	 * @throws IOException
	 * @throws AccessDeniedException
	 * @throws JSONException 
	 */

	void createTopic(DMaaPContext dmaapContext, TopicBean topicBean)
			throws CambriaApiException, TopicExistsException, IOException, AccessDeniedException;

	/**
	 * method used to delete to topic
	 * 
	 * @param dmaapContext
	 * @param topicName
	 * @throws IOException
	 * @throws AccessDeniedException
	 * @throws ConfigDbException
	 * @throws CambriaApiException
	 * @throws TopicExistsException
	 */

	void deleteTopic(DMaaPContext dmaapContext, String topicName)
			throws IOException, AccessDeniedException, ConfigDbException, CambriaApiException, TopicExistsException;

	/**
	 * method provides list of all the publishers associated with a topic
	 * 
	 * @param dmaapContext
	 * @param topicName
	 * @throws IOException
	 * @throws ConfigDbException
	 * @throws TopicExistsException
	 */
	void getPublishersByTopicName(DMaaPContext dmaapContext, String topicName)
			throws IOException, ConfigDbException, TopicExistsException;

	/**
	 * method provides details of all the consumer associated with a specific
	 * topic
	 * 
	 * @param dmaapContext
	 * @param topicName
	 * @throws IOException
	 * @throws ConfigDbException
	 * @throws TopicExistsException
	 */
	void getConsumersByTopicName(DMaaPContext dmaapContext, String topicName)
			throws IOException, ConfigDbException, TopicExistsException;

	/**
	 * method provides publishing right to a specific topic
	 * 
	 * @param dmaapContext
	 * @param topicName
	 * @param producerId
	 * @throws AccessDeniedException
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 */
	void permitPublisherForTopic(DMaaPContext dmaapContext, String topicName, String producerId)
			throws AccessDeniedException, ConfigDbException, IOException, TopicExistsException,CambriaApiException;

	/**
	 * method denies any specific publisher from a topic
	 * 
	 * @param dmaapContext
	 * @param topicName
	 * @param producerId
	 * @throws AccessDeniedException
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 */
	void denyPublisherForTopic(DMaaPContext dmaapContext, String topicName, String producerId)
			throws AccessDeniedException, ConfigDbException, IOException, TopicExistsException,CambriaApiException;

	/**
	 * method provide consuming right to a specific user on a topic
	 * 
	 * @param dmaapContext
	 * @param topicName
	 * @param consumerId
	 * @throws AccessDeniedException
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 */
	void permitConsumerForTopic(DMaaPContext dmaapContext, String topicName, String consumerId)
			throws AccessDeniedException, ConfigDbException, IOException, TopicExistsException,CambriaApiException;

	/**
	 * method denies a particular user's consuming right on a topic
	 * 
	 * @param dmaapContext
	 * @param topicName
	 * @param consumerId
	 * @throws AccessDeniedException
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 */
	void denyConsumerForTopic(DMaaPContext dmaapContext, String topicName, String consumerId)
			throws AccessDeniedException, ConfigDbException, IOException, TopicExistsException,CambriaApiException;

}
