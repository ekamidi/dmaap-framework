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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kafka.producer.KeyedMessage;

import com.att.nsa.cambria.beans.LogDetails;

/**
 * A publisher interface. Publishers receive messages and post them to a topic.
 * @author author
 */
public interface Publisher
{
	/**
	 * A message interface. The message has a key and a body.
	 * @author author
	 */
	public interface message
	{
		/**
		 * Get the key for this message. The key is used to partition messages
		 * into "sub-streams" that have guaranteed order. The key can be null,
		 * which means the message can be processed without any concern for order.
		 * 
		 * @return a key, possibly null
		 */
		String getKey();

		/**
		 * Get the message body.
		 * @return a message body
		 */
		String getMessage();
		/**
		 * set the logging params for transaction enabled logging 
		 * @param logDetails
		 */
		void setLogDetails (LogDetails logDetails);
		/**
		 * Get the log details for transaction enabled logging
		 * @return LogDetails
		 */
		LogDetails getLogDetails ();
		
		/**
		 * boolean transactionEnabled
		 * @return true/false
		 */
		boolean isTransactionEnabled();
		/**
		 * Set the transaction enabled flag from prop file or topic based implementation
		 * @param transactionEnabled
		 */
		void setTransactionEnabled(boolean transactionEnabled);
	}

	/**
	 * Send a single message to a topic. Equivalent to sendMessages with a list of size 1.
	 * @param topic
	 * @param msg
	 * @throws IOException
	 */
	public void sendMessage ( String topic, message msg ) throws IOException;

	/**
	 * Send messages to a topic.
	 * @param topic
	 * @param msgs
	 * @throws IOException
	 */
	public void sendMessages ( String topic, List<? extends message> msgs ) throws IOException;
	
	public void sendBatchMessage(String topic ,ArrayList<KeyedMessage<String,String>> kms) throws IOException;
}
