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
 * This is the class which will have the transaction enabled logging object
 * details
 * 
 * @author author
 *
 */
public class TransactionObj implements DMaaPTransactionObj {

	private String id;
	private String createTime;
	private long totalMessageCount;
	private long successMessageCount;
	private long failureMessageCount;
	private JSONObject fData = new JSONObject();
	private TrnRequest trnRequest;
	private static final String kAuxData = "transaction";

	/**
	 * Initializing constructor  
	 * put the json data for transaction enabled logging
	 * 
	 * @param data
	 */
	public TransactionObj(JSONObject data) {
		fData = data;

		// check for required fields (these throw if not present)
		getId();
		getTotalMessageCount();
		getSuccessMessageCount();
		getFailureMessageCount();

		// make sure we've got an aux data object
		final JSONObject aux = fData.optJSONObject(kAuxData);
		if (aux == null) {
			fData.put(kAuxData, new JSONObject());
		}
	}

	/**
	 * this constructor will have the details of transaction id,
	 * totalMessageCount successMessageCount, failureMessageCount to get the
	 * transaction object
	 * 
	 * @param id
	 * @param totalMessageCount
	 * @param successMessageCount
	 * @param failureMessageCount
	 */
	public TransactionObj(String id, long totalMessageCount, long successMessageCount, long failureMessageCount) {
		this.id = id;
		this.totalMessageCount = totalMessageCount;
		this.successMessageCount = successMessageCount;
		this.failureMessageCount = failureMessageCount;

	}

	/**
	 * The constructor passing only transaction id
	 * 
	 * @param id
	 */
	public TransactionObj(String id) {
		this.id = id;
	}

	/**
	 * Wrapping the data into json object
	 * 
	 * @return JSONObject
	 */
	public JSONObject asJsonObject() {
		final JSONObject full = new JSONObject(fData, JSONObject.getNames(fData));
		return full;
	}

	/**
	 * To get the transaction id
	 */
	public String getId() {
		return id;
	}

	/**
	 * To set the transaction id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public String getCreateTime() {
		return createTime;
	}

	/**
	 * 
	 * @param createTime
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public String serialize() {
		fData.put("transactionId", id);
		fData.put("totalMessageCount", totalMessageCount);
		fData.put("successMessageCount", successMessageCount);
		fData.put("failureMessageCount", failureMessageCount);
		return fData.toString();
	}

	public long getTotalMessageCount() {
		return totalMessageCount;
	}

	public void setTotalMessageCount(long totalMessageCount) {
		this.totalMessageCount = totalMessageCount;
	}

	public long getSuccessMessageCount() {
		return successMessageCount;
	}

	public void setSuccessMessageCount(long successMessageCount) {
		this.successMessageCount = successMessageCount;
	}

	public long getFailureMessageCount() {
		return failureMessageCount;
	}

	/**
	 * @param failureMessageCount
	 */
	public void setFailureMessageCount(long failureMessageCount) {
		this.failureMessageCount = failureMessageCount;
	}

	/**
	 * 
	 * @return JSOnObject fData
	 */
	public JSONObject getfData() {
		return fData;
	}

	/**
	 * set the json object into data
	 * 
	 * @param fData
	 */
	public void setfData(JSONObject fData) {
		this.fData = fData;
	}

	/**
	 * 
	 * @return
	 */
	public TrnRequest getTrnRequest() {
		return trnRequest;
	}

	/**
	 * 
	 * @param trnRequest
	 */
	public void setTrnRequest(TrnRequest trnRequest) {
		this.trnRequest = trnRequest;
	}

}
