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
package com.att.nsa.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.exception.DMaaPErrorMessages;
import com.att.nsa.cambria.exception.DMaaPResponseCode;
import com.att.nsa.cambria.exception.ErrorResponse;

/**
 * Servlet Filter implementation class ContentLengthFilter
 */
public class ContentLengthFilter implements Filter {

	private DefaultLength defaultLength;

	private FilterConfig filterConfig = null;
	DMaaPErrorMessages errorMessages = null;
	//private Logger log = Logger.getLogger(ContentLengthFilter.class.toString());
	private static final EELFLogger log = EELFManager.getInstance().getLogger(ContentLengthFilter.class);
	/**
	 * Default constructor.
	 */

	public ContentLengthFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		// TODO Auto-generated method stub
		// place your code here
		log.info("inside servlet do filter content length checking before pub/sub");
		HttpServletRequest request = (HttpServletRequest) req;
		JSONObject jsonObj = null;
		int requestLength = 0;
		try {
			// retrieving content length from message header

			if (null != request.getHeader("Content-Length")) {
				requestLength = Integer.parseInt(request.getHeader("Content-Length"));
			}
			// retrieving encoding from message header
			String transferEncoding = request.getHeader("Transfer-Encoding");
			// checking for no encoding, chunked and requestLength greater then
			// default length
			if (null != transferEncoding && !(transferEncoding.contains("chunked"))
					&& (requestLength > Integer.parseInt(defaultLength.getDefaultLength()))) {
				jsonObj = new JSONObject().append("defaultlength", defaultLength)
						.append("requestlength", requestLength);
				log.error("message length is greater than default");
				throw new CambriaApiException(jsonObj);
			} else if (null == transferEncoding && (requestLength > Integer.parseInt(defaultLength.getDefaultLength()))) {
				jsonObj = new JSONObject().append("defaultlength", defaultLength.getDefaultLength()).append(
						"requestlength", requestLength);
				log.error("Request message is not chunked or request length is greater than default length");
				throw new CambriaApiException(jsonObj);
			} else {
				chain.doFilter(req, res);
			}
		} catch (CambriaApiException | NumberFormatException e) {
			log.error("message size is greater then default");
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_EXPECTATION_FAILED,
					DMaaPResponseCode.MSG_SIZE_EXCEEDS_MSG_LIMIT.getResponseCode(), errorMessages.getMsgSizeExceeds()
							+ jsonObj.toString());
			log.info(errRes.toString());
			// throw new CambriaApiException(errRes);
		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		this.filterConfig = fConfig;
		log.info("Filter Content Length Initialize");
		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(fConfig
				.getServletContext());
		DefaultLength defLength = (DefaultLength) ctx.getBean("defLength");
		DMaaPErrorMessages errorMessages = (DMaaPErrorMessages) ctx.getBean("DMaaPErrorMessages");
		this.errorMessages = errorMessages;
		this.defaultLength = defLength;

	}

}
