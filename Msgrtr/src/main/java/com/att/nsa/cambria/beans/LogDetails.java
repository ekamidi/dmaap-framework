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
package com.att.nsa.cambria.beans;

import java.util.Date;

import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.cambria.utils.Utils;

/**
 * @author author
 *
 */

public class LogDetails {
	
	private String publisherId;
	private String topicId;
	private String subscriberGroupId;
	private String subscriberId;
	private String publisherIp;
	private String messageBatchId;
	private String messageSequence;
	private String messageTimestamp;
	private String consumeTimestamp;
	private String transactionIdTs;	
	private String serverIp;
	
	private long messageLengthInBytes; 
	private long totalMessageCount;
	
	private boolean transactionEnabled;
	/**
	 * This is for transaction enabled logging details
	 *
	 */
	public LogDetails() {
		super();
	}

	public String getTransactionId() {
		StringBuilder transactionId = new StringBuilder();
		transactionId.append(transactionIdTs);
		transactionId.append(CambriaConstants.TRANSACTION_ID_SEPARATOR);
		transactionId.append(publisherIp);
		transactionId.append(CambriaConstants.TRANSACTION_ID_SEPARATOR);
		transactionId.append(messageBatchId);
		transactionId.append(CambriaConstants.TRANSACTION_ID_SEPARATOR);
		transactionId.append(messageSequence);

		return transactionId.toString();
	}

	public String getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getSubscriberGroupId() {
		return subscriberGroupId;
	}

	public void setSubscriberGroupId(String subscriberGroupId) {
		this.subscriberGroupId = subscriberGroupId;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getPublisherIp() {
		return publisherIp;
	}

	public void setPublisherIp(String publisherIp) {
		this.publisherIp = publisherIp;
	}

	public String getMessageBatchId() {
		return messageBatchId;
	}

	public void setMessageBatchId(Long messageBatchId) {
		this.messageBatchId = Utils.getFromattedBatchSequenceId(messageBatchId);
	}

	public String getMessageSequence() {
		return messageSequence;
	}

	public void setMessageSequence(String messageSequence) {
		this.messageSequence = messageSequence;
	}

	public String getMessageTimestamp() {
		return messageTimestamp;
	}

	public void setMessageTimestamp(String messageTimestamp) {
		this.messageTimestamp = messageTimestamp;
	}

	public String getPublishTimestamp() {
		return Utils.getFormattedDate(new Date());
	}

	public String getConsumeTimestamp() {
		return consumeTimestamp;
	}

	public void setConsumeTimestamp(String consumeTimestamp) {
		this.consumeTimestamp = consumeTimestamp;
	}

	public long getMessageLengthInBytes() {
		return messageLengthInBytes;
	}

	public void setMessageLengthInBytes(long messageLengthInBytes) {
		this.messageLengthInBytes = messageLengthInBytes;
	}

	public long getTotalMessageCount() {
		return totalMessageCount;
	}

	public void setTotalMessageCount(long totalMessageCount) {
		this.totalMessageCount = totalMessageCount;
	}

	public boolean isTransactionEnabled() {
		return transactionEnabled;
	}

	public void setTransactionEnabled(boolean transactionEnabled) {
		this.transactionEnabled = transactionEnabled;
	}

	public String getTransactionIdTs() {
		return transactionIdTs;
	}

	public void setTransactionIdTs(String transactionIdTs) {
		this.transactionIdTs = transactionIdTs;
	}

	public String getPublisherLogDetails() {
		
			StringBuilder buffer = new StringBuilder();
			buffer.append("[publisherId=" + publisherId);
			buffer.append(", topicId=" + topicId);
			buffer.append(", messageTimestamp=" + messageTimestamp);
			buffer.append(", publisherIp=" + publisherIp);
			buffer.append(", messageBatchId=" + messageBatchId);
			buffer.append(", messageSequence=" + messageSequence );
			buffer.append(", messageLengthInBytes=" + messageLengthInBytes);
			buffer.append(", transactionEnabled=" + transactionEnabled);
			buffer.append(", transactionId=" + getTransactionId());
			buffer.append(", publishTimestamp=" + getPublishTimestamp());		
			buffer.append(", serverIp=" + getServerIp()+"]");
		return buffer.toString();
		
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public void setMessageBatchId(String messageBatchId) {
		this.messageBatchId = messageBatchId;
	}
	
}
