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

import com.att.nsa.metrics.CdmMetricsRegistry;
/**
 * This interface will help to generate metrics
 * @author author
 *
 */
public interface MetricsSet extends CdmMetricsRegistry{

	/**
	 * This method will setup cambria sender code
	 */
	public void setupCambriaSender ();
	/**
	 * This method will define on route complete
	 * @param name
	 * @param durationMs
	 */
	public void onRouteComplete ( String name, long durationMs );
	/**
	 * This method will help the kafka publisher while publishing the messages
	 * @param amount
	 */
	public void publishTick ( int amount );
	/**
	 * This method will help the kafka consumer while consuming the messages
	 * @param amount
	 */
	public void consumeTick ( int amount );
	/**
	 * This method will call if the kafka consumer cache missed 
	 */
	public void onKafkaConsumerCacheMiss ();
	/**
	 * This method will call if the kafka consumer cache will be hit while publishing/consuming the messages
	 */
	public void onKafkaConsumerCacheHit ();
	/**
	 * This method will call if the kafka consumer cache claimed
	 */
	public void onKafkaConsumerClaimed ();
	/**
	 * This method will call if Kafka consumer is timed out
	 */
	public void onKafkaConsumerTimeout ();



}
