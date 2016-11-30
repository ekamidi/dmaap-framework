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
package com.att.nsa.cambria.constants;

import org.apache.coyote.http11.Http11NioProtocol;

import com.att.nsa.cambria.utils.Utils;

/**
 * This is the constant files for all the property or parameters.
 * @author author
 *
 */
public interface CambriaConstants {

	String CAMBRIA = "Cambria";
	String DMAAP = "DMaaP";

	String kDefault_ZkRoot = "/fe3c/cambria";

	String kSetting_ZkConfigDbRoot = "config.zk.root";
	String kDefault_ZkConfigDbRoot = kDefault_ZkRoot + "/config";
String msgRtr_prop="MsgRtrApi.properties";
	String kBrokerType = "broker.type";
	
	/**
	 * value to use to signal kafka broker type.
	 */
	String kBrokerType_Kafka = "kafka";
	String kBrokerType_Memory = "memory";
	String kSetting_AdminSecret = "authentication.adminSecret";

	String kSetting_ApiNodeIdentifier = "cambria.api.node.identifier";

	/**
	 * value to use to signal max empty poll per minute
	 */
	String kSetting_MaxEmptyPollsPerMinute = "cambria.rateLimit.maxEmptyPollsPerMinute";
	double kDefault_MaxEmptyPollsPerMinute = 10.0;

	String kSetting_SleepMsOnRateLimit = "cambria.rateLimit.delay.ms";
	long kDefault_SleepMsOnRateLimit = Utils.getSleepMsForRate ( kDefault_MaxEmptyPollsPerMinute );

	String kSetting_RateLimitWindowLength = "cambria.rateLimit.window.minutes";
	int kDefault_RateLimitWindowLength = 5;

	String kConfig = "c";

	String kSetting_Port = "cambria.service.port";
	/**
	 * value to use to signal default port
	 */
	int kDefault_Port = 3904;

	String kSetting_MaxThreads = "tomcat.maxthreads";
	int kDefault_MaxThreads = -1;
	
	
//	String kSetting_TomcatProtocolClass = "tomcat.protocolClass";
	//String kDefault_TomcatProtocolClass = Http11NioProtocol.class.getName ();

	String kSetting_ZkConfigDbServers = "config.zk.servers";
	
	/**
	 * value to indicate localhost port number
	 */
	String kDefault_ZkConfigDbServers = "localhost:2181";

	/**
	 * value to use to signal Session time out
	 */
	String kSetting_ZkSessionTimeoutMs = "cambria.consumer.cache.zkSessionTimeout";
	int kDefault_ZkSessionTimeoutMs = 20 * 1000;

	/**
	 * value to use to signal connection time out 
	 */
	String kSetting_ZkConnectionTimeoutMs = "cambria.consumer.cache.zkConnectionTimeout";
	int kDefault_ZkConnectionTimeoutMs = 5 * 1000;

	String TRANSACTION_ID_SEPARATOR = "::";

	/**
	 * value to use to signal there's no timeout on the consumer request.
	 */
	public static final int kNoTimeout = 10000;

	/**
	 * value to use to signal no limit in the number of messages returned.
	 */
	public static final int kNoLimit = 0;

	/**
	 * value to use to signal that the caller wants the next set of events
	 */
	public static final int kNextOffset = -1;

	/**
	 * value to use to signal there's no filter on the response stream.
	 */
	public static final String kNoFilter = "";

	//Added for Metric publish
	public static final int kStdCambriaServicePort = 3904;
	public static final String kBasePath = "/events/";

}
