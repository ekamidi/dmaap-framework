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
package com.att.nsa.cambria.metabroker;

import java.util.List;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

/**
 * A broker interface to manage metadata around topics, etc.
 * 
 * @author author
 *
 */
public interface Broker {
	/**
	 * 
	 * @author author
	 *
	 */
	public class TopicExistsException extends Exception {
		/**
		 * 
		 * @param topicName
		 */
		public TopicExistsException(String topicName) {
			super("Topic " + topicName + " exists.");
		}

		private static final long serialVersionUID = 1L;
	}

	/**
	 * Get all topics in the underlying broker.
	 * 
	 * @return
	 * @throws ConfigDbException
	 */
	List<Topic> getAllTopics() throws ConfigDbException;

	/**
	 * Get a specific topic from the underlying broker.
	 * 
	 * @param topic
	 * @return a topic, or null
	 */
	Topic getTopic(String topic) throws ConfigDbException;

	/**
	 * create a  topic
	 * 
	 * @param topic
	 * @param description
	 * @param ownerApiKey
	 * @param partitions
	 * @param replicas
	 * @param transactionEnabled
	 * @return
	 * @throws TopicExistsException
	 * @throws CambriaApiException
	 */
	Topic createTopic(String topic, String description, String ownerApiKey, int partitions, int replicas,
			boolean transactionEnabled) throws TopicExistsException, CambriaApiException;

	/**
	 * Delete a topic by name
	 * 
	 * @param topic
	 */
	void deleteTopic(String topic) throws AccessDeniedException, CambriaApiException, TopicExistsException;
}
