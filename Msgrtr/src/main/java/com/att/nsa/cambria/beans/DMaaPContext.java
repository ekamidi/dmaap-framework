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
package com.att.nsa.cambria.beans;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.att.nsa.cambria.utils.ConfigurationReader;

/**
 * DMaaPContext provide and maintain all the configurations , Http request/response
 * Session and consumer Request Time
 * @author author
 *
 */
public class DMaaPContext {

    private ConfigurationReader configReader;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private String consumerRequestTime;
    static int i=0;
    
    public synchronized static long getBatchID() {
    	try{
    		final long metricsSendTime = System.currentTimeMillis();
    		final Date d = new Date(metricsSendTime);
    		final String text = new SimpleDateFormat("ddMMyyyyHHmmss").format(d);
    		long dt= Long.valueOf(text)+i;
    		i++;
    		return dt;
    	}
    	catch(NumberFormatException ex){
    		return 0;
    	}
    }
    
    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public HttpSession getSession() {
        this.session = request.getSession();
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public ConfigurationReader getConfigReader() {
        return configReader;
    }

    public void setConfigReader(ConfigurationReader configReader) {
        this.configReader = configReader;
    }

    public String getConsumerRequestTime() {
        return consumerRequestTime;
    }

    public void setConsumerRequestTime(String consumerRequestTime) {
        this.consumerRequestTime = consumerRequestTime;
    }
    
    
}
