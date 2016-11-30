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
package com.att.nsa.cambria.transaction;

import org.json.JSONObject;
/**
 * This is an interface for DMaaP transactional logging object class.
 * @author author
 *
 */
public interface DMaaPTransactionObj {
	/**
	 * This will get the transaction id
	 * @return id transactionId
	 */
	String getId();
	/**
	 * This will set the transaction id
	 * @param id transactionId
	 */
	void setId(String id);
	/**
	 * This will sync the transaction object mapping
	 * @return String or null
	 */
	String serialize();
	/**
	 * get the total message count once the publisher published
	 * @return long totalMessageCount
	 */
	long getTotalMessageCount();
	/**
	 * set the total message count once the publisher published
	 * @param totalMessageCount
	 */
	void setTotalMessageCount(long totalMessageCount);
	/**
	 * get the total Success Message Count once the publisher published
	 * @return getSuccessMessageCount
	 */
	long getSuccessMessageCount();
	/**
	 * set the total Success Message Count once the publisher published
	 * @param successMessageCount
	 */
	void setSuccessMessageCount(long successMessageCount);
	/**
	 * get the failure Message Count once the publisher published
	 * @return failureMessageCount
	 */
	long getFailureMessageCount();
	/**
	 * set the failure Message Count once the publisher published
	 * @param failureMessageCount
	 */
	void setFailureMessageCount(long failureMessageCount);

	/**
	 * wrapping the data into json object
	 * @return JSONObject
	 */
	JSONObject asJsonObject();

}
