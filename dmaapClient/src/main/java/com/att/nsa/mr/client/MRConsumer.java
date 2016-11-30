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
package com.att.nsa.mr.client;

import java.io.IOException;

import com.att.nsa.mr.client.response.MRConsumerResponse;

public interface MRConsumer extends MRClient
{
	/**
	 * Fetch a set of messages. The consumer's timeout and message limit are used if set in the constructor call. 

	 * @return a set of messages
	 * @throws IOException
	 */
	Iterable<String> fetch ()  throws IOException, Exception;

	/**
	 * Fetch a set of messages with an explicit timeout and limit for this call. These values
	 * override any set in the constructor call.
	 * 
	 * @param timeoutMs	The amount of time in milliseconds that the server should keep the connection
	 * open while waiting for message traffic. Use -1 for default timeout (controlled on the server-side).
	 * @param limit A limit on the number of messages returned in a single call. Use -1 for no limit.
	 * @return a set messages
	 * @throws IOException if there's a problem connecting to the server
	 */
	Iterable<String> fetch ( int timeoutMs, int limit )  throws IOException, Exception;
	
	MRConsumerResponse fetchWithReturnConsumerResponse ();
	
	
	MRConsumerResponse fetchWithReturnConsumerResponse ( int timeoutMs, int limit );
}
