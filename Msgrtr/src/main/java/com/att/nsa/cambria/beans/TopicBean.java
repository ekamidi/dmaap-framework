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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author author
 *
 */
@XmlRootElement
public class TopicBean implements Serializable {

	private static final long serialVersionUID = -8620390377775457949L;
	private String topicName;
	private String topicDescription;

	private int partitionCount = 1; //default values
	private int replicationCount = 1; //default value

	private boolean transactionEnabled;

	/**
	 * constructor
	 */
	public TopicBean() {
		super();
	}

	/**
	 * constructor initialization with topic details name, description,
	 * partition, replication, transaction
	 * 
	 * @param topicName
	 * @param description
	 * @param partitionCount
	 * @param replicationCount
	 * @param transactionEnabled
	 */
	public TopicBean(String topicName, String topicDescription, int partitionCount, int replicationCount,
			boolean transactionEnabled) {
		super();
		this.topicName = topicName;
		this.topicDescription = topicDescription;
		this.partitionCount = partitionCount;
		this.replicationCount = replicationCount;
		this.transactionEnabled = transactionEnabled;
	}

	/**
	 * @return
	 * returns topic name which is of String type
	 */
	public String getTopicName() {
		return topicName;
	}

	/**
	 * @param topicName
	 * set topic name  
	 */
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}


	/**
	 * @return
	 * returns partition count which is of int type
	 */
	public int getPartitionCount() {
		return partitionCount;
	}

	/**
	 * @param partitionCount
	 * set partition Count 
	 */
	public void setPartitionCount(int partitionCount) {
		this.partitionCount = partitionCount;
	}
	
	/**
	 * @return
	 * returns replication count which is of int type
	 */
	public int getReplicationCount() {
		return replicationCount;
	}
	
	/**
	 * @param
	 * set replication count which is of int type
	 */
	public void setReplicationCount(int replicationCount) {
		this.replicationCount = replicationCount;
	}
	
	/**
	 * @return
	 * returns boolean value which indicates whether transaction is Enabled 
	 */
	public boolean isTransactionEnabled() {
		return transactionEnabled;
	}
	
	/**
	 * @param
	 * sets boolean value which indicates whether transaction is Enabled 
	 */
	public void setTransactionEnabled(boolean transactionEnabled) {
		this.transactionEnabled = transactionEnabled;
	}

	/**
	 * 
	 * @return returns description which is of String type
	 */
	public String getTopicDescription() {
		return topicDescription;
	}
	/**
	 * 
	 * @param topicDescription
	 * set description which is of String type
	 */
	public void setTopicDescription(String topicDescription) {
		this.topicDescription = topicDescription;
	}

}
