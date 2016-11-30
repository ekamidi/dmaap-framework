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
import org.json.JSONObject;
/**
 * Represents the Error Response Object 
 * that is rendered as a JSON object when
 * an exception or error occurs on MR Rest Service.
 * @author author
 *
 */
//@XmlRootElement
public class ErrorResponse {
	
	private int httpStatusCode;
	private int mrErrorCode;
    private String errorMessage;
    private String helpURL;
    private String statusTs;
    private String topic;
    private String publisherId;
    private String publisherIp;
    private String subscriberId;
    private String subscriberIp;
	

	public ErrorResponse(int httpStatusCode, int mrErrorCode,
			String errorMessage, String helpURL, String statusTs, String topic,
			String publisherId, String publisherIp, String subscriberId,
			String subscriberIp) {
		super();
		this.httpStatusCode = httpStatusCode;
		this.mrErrorCode = mrErrorCode;
		this.errorMessage = errorMessage;
		this.helpURL = "https://wiki.web.att.com/display/DMAAP/DMaaP+Home";
		this.statusTs = statusTs;
		this.topic = topic;
		this.publisherId = publisherId;
		this.publisherIp = publisherIp;
		this.subscriberId = subscriberId;
		this.subscriberIp = subscriberIp;
	}

	public ErrorResponse(int httpStatusCode, int mrErrorCode,
			String errorMessage) {
		super();
		this.httpStatusCode = httpStatusCode;
		this.mrErrorCode = mrErrorCode;
		this.errorMessage = errorMessage;
		this.helpURL = "https://wiki.web.att.com/display/DMAAP/DMaaP+Home";
		
	}
	
	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}
	
	public int getMrErrorCode() {
		return mrErrorCode;
	}


	public void setMrErrorCode(int mrErrorCode) {
		this.mrErrorCode = mrErrorCode;
	}

	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getHelpURL() {
		return helpURL;
	}

	public void setHelpURL(String helpURL) {
		this.helpURL = helpURL;
	}

	@Override
	public String toString() {
		return "ErrorResponse {\"httpStatusCode\":\"" + httpStatusCode
				+ "\", \"mrErrorCode\":\"" + mrErrorCode + "\", \"errorMessage\":\""
				+ errorMessage + "\", \"helpURL\":\"" + helpURL + "\", \"statusTs\":\""+statusTs+"\""
				+ ", \"topicId\":\""+topic+"\", \"publisherId\":\""+publisherId+"\""
				+ ", \"publisherIp\":\""+publisherIp+"\", \"subscriberId\":\""+subscriberId+"\""
				+ ", \"subscriberIp\":\""+subscriberIp+"\"}";
	}
	
	public String getErrMapperStr1() {
		return "ErrorResponse [httpStatusCode=" + httpStatusCode + ", mrErrorCode=" + mrErrorCode + ", errorMessage="
				+ errorMessage + ", helpURL=" + helpURL + "]";
	}

	
	
	public JSONObject getErrMapperStr() {
		JSONObject o = new JSONObject();
		o.put("status", getHttpStatusCode());
		o.put("mrstatus", getMrErrorCode());
		o.put("message", getErrorMessage());
		o.put("helpURL", getHelpURL());
		return o;
	}
	
    
	
}
