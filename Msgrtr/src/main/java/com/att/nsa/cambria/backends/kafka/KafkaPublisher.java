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

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import kafka.common.FailedToSendMessageException;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.json.JSONException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.springframework.beans.factory.annotation.Qualifier;

import com.att.nsa.cambria.backends.Publisher;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.drumlin.till.nv.rrNvReadable;

/**
 * Sends raw JSON objects into Kafka.
 * 
 * Could improve space: BSON rather than JSON?
 * 
 * @author author
 *
 */

public class KafkaPublisher implements Publisher {
	/**
	 * constructor initializing
	 * 
	 * @param settings
	 * @throws rrNvReadable.missingReqdSetting
	 */
	public KafkaPublisher(@Qualifier("propertyReader") rrNvReadable settings) throws rrNvReadable.missingReqdSetting {
		//fSettings = settings;

		final Properties props = new Properties();
		/*transferSetting(fSettings, props, "metadata.broker.list", "localhost:9092");
		transferSetting(fSettings, props, "request.required.acks", "1");
		transferSetting(fSettings, props, "message.send.max.retries", "5");
		transferSetting(fSettings, props, "retry.backoff.ms", "150"); */
		String kafkaConnUrl= com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"kafka.metadata.broker.list"); 
		System.out.println("kafkaConnUrl:- "+kafkaConnUrl);
		if(null==kafkaConnUrl){ 
 
			kafkaConnUrl="localhost:9092"; 
		}		
		transferSetting( props, "metadata.broker.list", kafkaConnUrl);
		transferSetting( props, "request.required.acks", "1");
		transferSetting( props, "message.send.max.retries", "5");
		transferSetting(props, "retry.backoff.ms", "150"); 

		props.put("serializer.class", "kafka.serializer.StringEncoder");

		fConfig = new ProducerConfig(props);
		fProducer = new Producer<String, String>(fConfig);
	}

	/**
	 * Send a message with a given topic and key.
	 * 
	 * @param msg
	 * @throws FailedToSendMessageException
	 * @throws JSONException
	 */
	@Override
	public void sendMessage(String topic, message msg) throws IOException, FailedToSendMessageException {
		final List<message> msgs = new LinkedList<message>();
		msgs.add(msg);
		sendMessages(topic, msgs);
	}

	/**
	 * method publishing batch messages
	 * 
	 * @param topic
	 * @param kms
	 * throws IOException
	 */
	public void sendBatchMessage(String topic, ArrayList<KeyedMessage<String, String>> kms) throws IOException {
		try {
			fProducer.send(kms);

		} catch (FailedToSendMessageException excp) { 
			log.error("Failed to send message(s) to topic [" + topic + "].", excp);
			throw new FailedToSendMessageException(excp.getMessage(), excp);
		}

	}

	/**
	 * Send a set of messages. Each must have a "key" string value.
	 * 
	 * @param topic
	 * @param msg
	 * @throws FailedToSendMessageException
	 * @throws JSONException
	 */
	@Override
	public void sendMessages(String topic, List<? extends message> msgs)
			throws IOException, FailedToSendMessageException {
		log.info("sending " + msgs.size() + " events to [" + topic + "]");

		final List<KeyedMessage<String, String>> kms = new ArrayList<KeyedMessage<String, String>>(msgs.size());
		for (message o : msgs) {
			final KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, o.getKey(), o.toString());
			kms.add(data);
		}
		try {
			fProducer.send(kms);

		} catch (FailedToSendMessageException excp) {
			log.error("Failed to send message(s) to topic [" + topic + "].", excp);
			throw new FailedToSendMessageException(excp.getMessage(), excp);
		}
	}

	//private final rrNvReadable fSettings;

	private ProducerConfig fConfig;
	private Producer<String, String> fProducer;

  /**
   * It sets the key value pair
   * @param topic
   * @param msg 
   * @param key
   * @param defVal
   */
	private void transferSetting(Properties props, String key, String defVal) {
		String kafka_prop= com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"kafka." + key);
		if (null==kafka_prop) kafka_prop=defVal;
		//props.put(key, settings.getString("kafka." + key, defVal));
		props.put(key, kafka_prop);
	}

	//private static final Logger log = LoggerFactory.getLogger(KafkaPublisher.class);

	private static final EELFLogger log = EELFManager.getInstance().getLogger(KafkaPublisher.class);
}
