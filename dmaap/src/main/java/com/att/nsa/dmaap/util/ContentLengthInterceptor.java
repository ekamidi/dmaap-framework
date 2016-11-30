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
package com.att.nsa.dmaap.util;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.exception.DMaaPResponseCode;
import com.att.nsa.cambria.exception.ErrorResponse;
import ajsc.beans.interceptors.AjscInterceptor;

/**
 * AJSC Intercepter implementation of ContentLengthFilter
 */
@Component
public class ContentLengthInterceptor implements AjscInterceptor{

	
	private String defLength;
	//private Logger log = Logger.getLogger(ContentLengthInterceptor.class.toString());
	private static final EELFLogger log = EELFManager.getInstance().getLogger(ContentLengthInterceptor.class);


	/**
	 * Intercepter method to intercept requests before processing
	 */
	@Override
	public boolean allowOrReject(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse,
			Map map) throws Exception {
				
		log.info("inside Interceptor allowOrReject content length checking before pub/sub");
		
		JSONObject jsonObj = null;
		int requestLength = 0;
		setDefLength(System.getProperty("maxcontentlength"));
		try {
			// retrieving content length from message header

			if (null != httpservletrequest.getHeader("Content-Length")) {
				requestLength = Integer.parseInt(httpservletrequest.getHeader("Content-Length"));
			}
			// retrieving encoding from message header
			String transferEncoding = httpservletrequest.getHeader("Transfer-Encoding");
			// checking for no encoding, chunked and requestLength greater then
			// default length
				if (null != transferEncoding && !(transferEncoding.contains("chunked"))
						&& (requestLength > Integer.parseInt(getDefLength()))) {
					jsonObj = new JSONObject().append("defaultlength", getDefLength())
							.append("requestlength", requestLength);
					log.error("message length is greater than default");
					throw new CambriaApiException(jsonObj);
				} 
				else if (null == transferEncoding && (requestLength > Integer.parseInt(getDefLength()))) 
				{
					jsonObj = new JSONObject().append("defaultlength", getDefLength()).append(
							"requestlength", requestLength);
					log.error("Request message is not chunked or request length is greater than default length");
					throw new CambriaApiException(jsonObj);
				
				
				} 
				else 
				{
				//chain.doFilter(req, res);
				return true;
				}
			
		} catch (CambriaApiException | NumberFormatException | JSONException e) {
			
			log.info("Exception obj--"+e);
			log.error("message size is greater then default"+e.getMessage());
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_REQUEST_TOO_LONG,
					DMaaPResponseCode.MSG_SIZE_EXCEEDS_MSG_LIMIT.getResponseCode(), System.getProperty("msg_size_exceeds")
							+ jsonObj.toString());
			log.info(errRes.toString());
			
			
			map.put(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"test");
			httpservletresponse.setStatus(HttpStatus.SC_REQUEST_TOO_LONG);
			httpservletresponse.getOutputStream().write(errRes.toString().getBytes());
			return false;
		}

		
		
	}

	
	/**
	 * Get Default Content Length
	 * @return defLength
	 */
	public String getDefLength() {
		return defLength;
	}
	/**
	 * Set Default Content Length
	 * @param defLength
	 */
	public void setDefLength(String defLength) {
		this.defLength = defLength;
	}

	

}
