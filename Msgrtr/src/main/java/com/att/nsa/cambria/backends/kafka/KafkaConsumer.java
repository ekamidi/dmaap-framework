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
package com.att.nsa.cambria.backends.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import com.att.nsa.cambria.backends.Consumer;

/**
 * A consumer instance that's created per-request. These are stateless so that
 * clients can connect to this service as a proxy.
 * 
 * @author author
 *
 */
public class KafkaConsumer implements Consumer {
	private enum State {
		OPENED, CLOSED
	}

	/**
	 * KafkaConsumer() is constructor. It has following 4 parameters:-
	 * @param topic
	 * @param group
	 * @param id
	 * @param cc
	 * 
	 */
	
	public KafkaConsumer(String topic, String group, String id, ConsumerConnector cc) {
		fTopic = topic;
		fGroup = group;
		fId = id;
		fConnector = cc;

		fCreateTimeMs = System.currentTimeMillis();
		fLastTouch = fCreateTimeMs;

		fLogTag = fGroup + "(" + fId + ")/" + fTopic;
		offset = 0;

		state = KafkaConsumer.State.OPENED;

		final Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(fTopic, 1);
		final Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = fConnector
				.createMessageStreams(topicCountMap);
		final List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(fTopic);
		fStream = streams.iterator().next();
	}

	
	/** getName() method returns string type value.
	 * returns 3 parameters in string:- 
	 * fTopic,fGroup,fId
	 * @Override
	 */
	public String getName() {
		return fTopic + " : " + fGroup + " : " + fId;
	}

	/** getCreateTimeMs() method returns long type value.
	 * returns fCreateTimeMs variable value 
	 * @Override
	 * 
	 */
	public long getCreateTimeMs() {
		return fCreateTimeMs;
	}

	/** getLastAccessMs() method returns long type value.
	 * returns fLastTouch variable value 
	 * @Override
	 * 
	 */
	public long getLastAccessMs() {
		return fLastTouch;
	}

	
	/** 
	 * nextMessage() is synchronized method that means at a time only one object can access it.
	 * getName() method returns String which is of type Consumer.Message
	 * @Override
	 * */
	public synchronized Consumer.Message nextMessage() {
		if (getState() == KafkaConsumer.State.CLOSED) {
			log.warn("nextMessage() called on closed KafkaConsumer " + getName());
			return null;
		}

		try {
			ConsumerIterator<byte[], byte[]> it = fStream.iterator();
			if (it.hasNext()) {
				final MessageAndMetadata<byte[], byte[]> msg = it.next();
				offset = msg.offset();

				return new Consumer.Message() {
					@Override
					public long getOffset() {
						return msg.offset();
					}

					@Override
					public String getMessage() {
						return new String(msg.message());
					}
				};
			}
		} catch (kafka.consumer.ConsumerTimeoutException x) {
			log.debug(fLogTag + ": ConsumerTimeoutException in Kafka consumer; returning null. ");
		} catch (java.lang.IllegalStateException x) {
			log.error(fLogTag + ": Illegal state exception in Kafka consumer; dropping stream. " + x.getMessage());
		}

		return null;
	}
	
	/** getOffset() method returns long type value.
	 * returns offset variable value 
	 * @Override
	 * 
	 */
	public long getOffset() {
		return offset;
	}

	/** commit offsets 
	 * commitOffsets() method will be called on closed of KafkaConsumer.
	 * @Override
	 * 
	 */
	public void commitOffsets() {
		if (getState() == KafkaConsumer.State.CLOSED) {
			log.warn("commitOffsets() called on closed KafkaConsumer " + getName());
			return;
		}
		fConnector.commitOffsets();
	}

	/**
	 * updating fLastTouch with current time in ms
	 */
	public void touch() {
		fLastTouch = System.currentTimeMillis();
	}
	
	/** getLastTouch() method returns long type value.
	 * returns fLastTouch variable value
	 * 
	 */
	public long getLastTouch() {
		return fLastTouch;
	}

	/**
	 *   setting the kafkaConsumer state to closed
	 */
	public synchronized void close() {
		if (getState() == KafkaConsumer.State.CLOSED) {
			log.warn("close() called on closed KafkaConsumer " + getName());
			return;
		}

		setState(KafkaConsumer.State.CLOSED);
		fConnector.shutdown();
	}
	
	/**
	 * getConsumerGroup() returns Consumer group
	 * @return
	 */
	public String getConsumerGroup() {
		return fGroup;
	}
	
	/**
	 * getConsumerId returns Consumer Id
	 * @return
	 */
	public String getConsumerId() {
		return fId;
	}

	/**
	 * getState returns kafkaconsumer state
	 * @return
	 */	
	private KafkaConsumer.State getState() {
		return this.state;
	}
	
	/**
	 * setState() sets the kafkaConsumer state
	 * @param state
	 */
	private void setState(KafkaConsumer.State state) {
		this.state = state;
	}

	private ConsumerConnector fConnector;
	private final String fTopic;
	private final String fGroup;
	private final String fId;
	private final String fLogTag;
	private final KafkaStream<byte[], byte[]> fStream;
	private long fCreateTimeMs;
	private long fLastTouch;
	private long offset;
	private KafkaConsumer.State state;
	private static final EELFLogger log = EELFManager.getInstance().getLogger(KafkaConsumer.class);
	//private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
}
