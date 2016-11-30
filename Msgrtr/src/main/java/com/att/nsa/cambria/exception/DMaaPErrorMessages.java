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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This Class reads the error message properties
 * from the properties file
 * @author author
 *
 */
@Component
public class DMaaPErrorMessages {

	@Value("${resource.not.found}")
	private String notFound;
	
	@Value("${server.unavailable}")
	private String serverUnav;
	
	@Value("${http.method.not.allowed}")
	private String methodNotAllowed;
	
	@Value("${incorrect.request.json}")
	private String badRequest;
	
	@Value("${network.time.out}")
	private String nwTimeout;
	
	@Value("${get.topic.failure}")
	private String topicsfailure;
	
	@Value("${not.permitted.access.1}")
	private String notPermitted1;
	
	@Value("${not.permitted.access.2}")
	private String notPermitted2;
	
	@Value("${get.topic.details.failure}")
	private String topicDetailsFail;
	
	@Value("${create.topic.failure}")
	private String createTopicFail;
	
	@Value("${delete.topic.failure}")
	private String deleteTopicFail;
	
	@Value("${incorrect.json}")
	private String incorrectJson;
	
	@Value("${consume.msg.error}")
	private String consumeMsgError;
	
	@Value("${publish.msg.error}")
	private String publishMsgError;
	
	
	@Value("${publish.msg.count}")
	private String publishMsgCount;
	
	
	@Value("${authentication.failure}")
	private String authFailure;
	@Value("${msg_size_exceeds}")
	private String msgSizeExceeds;
	
	
	@Value("${topic.not.exist}")
	private String topicNotExist;
	
	public String getMsgSizeExceeds() {
		return msgSizeExceeds;
	}

	public void setMsgSizeExceeds(String msgSizeExceeds) {
		this.msgSizeExceeds = msgSizeExceeds;
	}

	public String getNotFound() {
		return notFound;
	}

	public void setNotFound(String notFound) {
		this.notFound = notFound;
	}

	public String getServerUnav() {
		return serverUnav;
	}

	public void setServerUnav(String serverUnav) {
		this.serverUnav = serverUnav;
	}

	public String getMethodNotAllowed() {
		return methodNotAllowed;
	}

	public void setMethodNotAllowed(String methodNotAllowed) {
		this.methodNotAllowed = methodNotAllowed;
	}

	public String getBadRequest() {
		return badRequest;
	}

	public void setBadRequest(String badRequest) {
		this.badRequest = badRequest;
	}

	public String getNwTimeout() {
		return nwTimeout;
	}

	public void setNwTimeout(String nwTimeout) {
		this.nwTimeout = nwTimeout;
	}

	public String getNotPermitted1() {
		return notPermitted1;
	}

	public void setNotPermitted1(String notPermitted1) {
		this.notPermitted1 = notPermitted1;
	}

	public String getNotPermitted2() {
		return notPermitted2;
	}

	public void setNotPermitted2(String notPermitted2) {
		this.notPermitted2 = notPermitted2;
	}

	public String getTopicsfailure() {
		return topicsfailure;
	}

	public void setTopicsfailure(String topicsfailure) {
		this.topicsfailure = topicsfailure;
	}

	public String getTopicDetailsFail() {
		return topicDetailsFail;
	}

	public void setTopicDetailsFail(String topicDetailsFail) {
		this.topicDetailsFail = topicDetailsFail;
	}

	public String getCreateTopicFail() {
		return createTopicFail;
	}

	public void setCreateTopicFail(String createTopicFail) {
		this.createTopicFail = createTopicFail;
	}

	public String getIncorrectJson() {
		return incorrectJson;
	}

	public void setIncorrectJson(String incorrectJson) {
		this.incorrectJson = incorrectJson;
	}

	public String getDeleteTopicFail() {
		return deleteTopicFail;
	}

	public void setDeleteTopicFail(String deleteTopicFail) {
		this.deleteTopicFail = deleteTopicFail;
	}

	public String getConsumeMsgError() {
		return consumeMsgError;
	}

	public void setConsumeMsgError(String consumeMsgError) {
		this.consumeMsgError = consumeMsgError;
	}

	public String getPublishMsgError() {
		return publishMsgError;
	}

	public void setPublishMsgError(String publishMsgError) {
		this.publishMsgError = publishMsgError;
	}

	public String getPublishMsgCount() {
		return publishMsgCount;
	}

	public String getAuthFailure() {
		return authFailure;
	}

	public void setAuthFailure(String authFailure) {
		this.authFailure = authFailure;
	}

	public void setPublishMsgCount(String publishMsgCount) {
		this.publishMsgCount = publishMsgCount;
	}

	public String getTopicNotExist() {
		return topicNotExist;
	}

	public void setTopicNotExist(String topicNotExist) {
		this.topicNotExist = topicNotExist;
	}
	
	
	
	
}
