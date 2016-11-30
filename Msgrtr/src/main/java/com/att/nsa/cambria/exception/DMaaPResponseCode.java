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
package com.att.nsa.cambria.exception;

/**
 * Define the Error Response Codes for MR
 * using this enumeration
 * @author author
 *
 */
public enum DMaaPResponseCode {
	
	  
	  /**
	   * GENERIC
	   */
	  RESOURCE_NOT_FOUND(3001),
	  SERVER_UNAVAILABLE(3002),
	  METHOD_NOT_ALLOWED(3003),
	  GENERIC_INTERNAL_ERROR(1004),
	  /**
	   * AAF
	   */
	  INVALID_CREDENTIALS(4001),
	  ACCESS_NOT_PERMITTED(4002),
	  UNABLE_TO_AUTHORIZE(4003),
	  /**
	   * PUBLISH AND SUBSCRIBE
	   */
	  MSG_SIZE_EXCEEDS_BATCH_LIMIT(5001),
	  UNABLE_TO_PUBLISH(5002),
	  INCORRECT_BATCHING_FORMAT(5003),
	  MSG_SIZE_EXCEEDS_MSG_LIMIT(5004),
	  INCORRECT_JSON(5005),
	  CONN_TIMEOUT(5006),
	  PARTIAL_PUBLISH_MSGS(5007),
	  CONSUME_MSG_ERROR(5008),
	  PUBLISH_MSG_ERROR(5009), 
	  RETRIEVE_TRANSACTIONS(5010),
	  RETRIEVE_TRANSACTIONS_DETAILS(5011),
	  TOO_MANY_REQUESTS(5012),
	  
	  RATE_LIMIT_EXCEED(301),
	 
	  /**
	   * TOPICS
	   */
	GET_TOPICS_FAIL(6001),
	GET_TOPICS_DETAILS_FAIL(6002),
	CREATE_TOPIC_FAIL(6003),
	DELETE_TOPIC_FAIL(6004),
	GET_PUBLISHERS_BY_TOPIC(6005),
	GET_CONSUMERS_BY_TOPIC(6006),
	PERMIT_PUBLISHER_FOR_TOPIC(6007),
	REVOKE_PUBLISHER_FOR_TOPIC(6008),
	PERMIT_CONSUMER_FOR_TOPIC(6009),
	REVOKE_CONSUMER_FOR_TOPIC(6010),
	GET_CONSUMER_CACHE(6011),
	DROP_CONSUMER_CACHE(6012),
	GET_METRICS_ERROR(6013),
	GET_BLACKLIST(6014),
	ADD_BLACKLIST(6015),
	REMOVE_BLACKLIST(6016),
	TOPIC_NOT_IN_AAF(6017);
	private int responseCode;
	
	public int getResponseCode() {
		return responseCode;
	}
	private DMaaPResponseCode (final int code) {
		responseCode = code;
	}

}
