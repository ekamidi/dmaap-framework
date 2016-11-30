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
package com.att.nsa.cambria.backends.memory;

import java.util.ArrayList;
import java.util.Collection;

import com.att.nsa.cambria.backends.Consumer;
import com.att.nsa.cambria.backends.ConsumerFactory;
/**
 * 
 * @author author
 *
 */
public class MemoryConsumerFactory implements ConsumerFactory
{
	/**
	 * 
	 * Initializing constructor
	 * @param q
	 */
	public MemoryConsumerFactory ( MemoryQueue q )
	{
		fQueue = q;
	}

	/**
	 * 
	 * @param topic
	 * @param consumerGroupId
	 * @param clientId
	 * @param timeoutMs
	 * @return Consumer
	 */
	@Override
	public Consumer getConsumerFor ( String topic, String consumerGroupId, String clientId, int timeoutMs )
	{
		return new MemoryConsumer ( topic, consumerGroupId );
	}

	private final MemoryQueue fQueue;

	/**
	 * 
	 * Define nested inner class
	 *
	 */
	private class MemoryConsumer implements Consumer
	{
		/**
		 * 
		 * Initializing MemoryConsumer constructor 
		 * @param topic
		 * @param consumer
		 * 
		 */
		public MemoryConsumer ( String topic, String consumer )
		{
			fTopic = topic;
			fConsumer = consumer;
			fCreateMs = System.currentTimeMillis ();
			fLastAccessMs = fCreateMs;
		}

		@Override
		/**
		 * 
		 * return consumer details  
		 */
		public Message nextMessage ()
		{
			return fQueue.get ( fTopic, fConsumer );
		}

		private final String fTopic;
		private final String fConsumer;
		private final long fCreateMs;
		private long fLastAccessMs;

		@Override
		public void close() {
			//Nothing to close/clean up.
		}
		/**
		 * 
		 */
		public void commitOffsets()
		{
			// ignoring this aspect
		}
		/**
		 * get offset
		 */
		public long getOffset()
		{
			return 0;
		}

		@Override
		/**
		 * get consumer topic name
		 */
		public String getName ()
		{
			return fTopic + "/" + fConsumer;
		}

		@Override
		public long getCreateTimeMs ()
		{
			return fCreateMs;
		}

		@Override
		public long getLastAccessMs ()
		{
			return fLastAccessMs;
		}
	}

	@Override
	public void destroyConsumer(String topic, String consumerGroupId,
			String clientId) {
		//No cache for memory consumers, so NOOP
	}

	@Override
	public void dropCache ()
	{
		// nothing to do - there's no cache here
	}

	@Override
	/**
	 * @return ArrayList<MemoryConsumer>
	 */
	public Collection<? extends Consumer> getConsumers ()
	{
		return new ArrayList<MemoryConsumer> ();
	}
}
