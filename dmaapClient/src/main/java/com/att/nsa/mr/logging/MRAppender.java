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
/**
 * 
 */
package com.att.nsa.mr.logging;

import java.io.IOException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.MRPublisher;

/**
 * @author author
 *
 */
public class MRAppender extends AppenderSkeleton {

	private MRPublisher fPublisher;

	//Provided through log4j configuration
	private String topic;
	private String partition;
	private String hosts;
	private int maxBatchSize = 1;
	private int maxAgeMs = 1000;
	private boolean compress = false;

	/**
	 * 
	 */
	public MRAppender() {
		super();
	}

	/**
	 * @param isActive
	 */
	public MRAppender(boolean isActive) {
		super(isActive);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#close()
	 */
	@Override
	public void close() {
		if (!this.closed) {
			this.closed = true;
			fPublisher.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	@Override
	public boolean requiresLayout() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	protected void append(LoggingEvent event) {
		final String message;
		
		if (this.layout == null) {
			message = event.getRenderedMessage();
		} else {
			message = this.layout.format(event);
		}
		
		try {
			fPublisher.send(partition, message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void activateOptions() {
		if (hosts != null && topic != null && partition != null) {
			fPublisher = MRClientFactory.createBatchingPublisher(hosts.split(","), topic, maxBatchSize, maxAgeMs, compress);
		} else {
			LogLog.error("The Hosts, Topic, and Partition parameter are required to create a MR Log4J Appender");
		}
	}
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getPartition() {
		return partition;
	}

	public void setPartition(String partition) {
		this.partition = partition;
	}

	public String getHosts() {
		return hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}
	
	public int getMaxBatchSize() {
		return maxBatchSize;
	}

	public void setMaxBatchSize(int maxBatchSize) {
		this.maxBatchSize = maxBatchSize;
	}

	public int getMaxAgeMs() {
		return maxAgeMs;
	}

	public void setMaxAgeMs(int maxAgeMs) {
		this.maxAgeMs = maxAgeMs;
	}	
	
	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

}
