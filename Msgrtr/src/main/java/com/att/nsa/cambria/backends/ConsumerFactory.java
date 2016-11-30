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
package com.att.nsa.cambria.backends;

import java.util.Collection;

/**
 * This is the factory class to instantiate the consumer
 * 
 * @author author
 *
 */

public interface ConsumerFactory {
	public static final String kSetting_EnableCache = "cambria.consumer.cache.enabled";
	public static boolean kDefault_IsCacheEnabled = true;

	/**
	 * User defined exception for Unavailable Exception
	 * 
	 * @author author
	 *
	 */
	public class UnavailableException extends Exception {
		/**
		 * Unavailable Exception with message
		 * 
		 * @param msg
		 */
		public UnavailableException(String msg) {
			super(msg);
		}

		/**
		 * Unavailable Exception with the throwable object
		 * 
		 * @param t
		 */
		public UnavailableException(Throwable t) {
			super(t);
		}

		/**
		 * Unavailable Exception with the message and cause
		 * 
		 * @param msg
		 * @param cause
		 */
		public UnavailableException(String msg, Throwable cause) {
			super(msg, cause);
		}

		private static final long serialVersionUID = 1L;
	}

	/**
	 * For admin use, drop all cached consumers.
	 */
	public void dropCache();

	/**
	 * Get or create a consumer for the given set of info (topic, group, id)
	 * 
	 * @param topic
	 * @param consumerGroupId
	 * @param clientId
	 * @param timeoutMs
	 * @return
	 * @throws UnavailableException
	 */
	public Consumer getConsumerFor(String topic, String consumerGroupId,
			String clientId, int timeoutMs) throws UnavailableException;

	/**
	 * For factories that employ a caching mechanism, this allows callers to
	 * explicitly destory a consumer that resides in the factory's cache.
	 * 
	 * @param topic
	 * @param consumerGroupId
	 * @param clientId
	 */
	public void destroyConsumer(String topic, String consumerGroupId,
			String clientId);

	/**
	 * For admin/debug, we provide access to the consumers
	 * 
	 * @return a collection of consumers
	 */
	public Collection<? extends Consumer> getConsumers();
}
