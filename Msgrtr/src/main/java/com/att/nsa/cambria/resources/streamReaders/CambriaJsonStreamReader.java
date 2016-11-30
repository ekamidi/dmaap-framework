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
package com.att.nsa.cambria.resources.streamReaders;

import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.backends.Publisher.message;
import com.att.nsa.cambria.beans.LogDetails;
import com.att.nsa.cambria.resources.CambriaEventSet.reader;

/**
 * 
 * @author author
 *
 */
public class CambriaJsonStreamReader implements reader {
	private final JSONTokener fTokens;
	private final boolean fIsList;
	private long fCount;
	private final String fDefPart;
	public static final String kKeyField = "cambria.partition";

	/**
	 * 
	 * @param is
	 * @param defPart
	 * @throws CambriaApiException
	 */
	public CambriaJsonStreamReader(InputStream is, String defPart) throws CambriaApiException {
		try {
			fTokens = new JSONTokener(is);
			fCount = 0;
			fDefPart = defPart;

			final int c = fTokens.next();
			if (c == '[') {
				fIsList = true;
			} else if (c == '{') {
				fTokens.back();
				fIsList = false;
			} else {
				throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST, "Expecting an array or an object.");
			}
		} catch (JSONException e) {
			throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		}
	}

	@Override
	public message next() throws CambriaApiException {
		try {
			if (!fTokens.more()) {
				return null;
			}

			final int c = fTokens.next();
			
			/*if (c ==','){
				fCloseCount++;
				System.out.println("fCloseCount=" + fCloseCount +" fCount "+fCount);
			}*/
			if (fIsList) {
				if (c == ']' || (fCount > 0 && c == 10))
					return null;


				if (fCount > 0 && c != ',' && c!= 10) {
					throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST,
							"Expected ',' or closing ']' after last object.");
				}

				if (fCount == 0 && c != '{' && c!= 10  && c!=32) {
					throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST, "Expected { to start an object.");
				}
			} else if (fCount != 0 || c != '{') {
				throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST, "Expected '{' to start an object.");
			}

			if (c == '{') {
				fTokens.back();
			}
			final JSONObject o = new JSONObject(fTokens);
			fCount++;
			return new msg(o);
		} catch (JSONException e) {
			throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

		}
	}

	private class msg implements message {
		private final String fKey;
		private  String fMsg;
		private LogDetails logDetails;
		private boolean transactionEnabled;

		/**
		 * constructor
		 * 
		 * @param o
		 */
		//public msg(JSONObject o){}
		
		
		public msg(JSONObject o) {
			String key = o.optString(kKeyField, fDefPart);
			if (key == null) {
				key = "" + System.currentTimeMillis();
			}
			fKey = key;
					
				fMsg = o.toString().trim();
			
		}

		@Override
		public String getKey() {
			return fKey;
		}

		@Override
		public String getMessage() {
			return fMsg;
		}

		@Override
		public boolean isTransactionEnabled() {
			return transactionEnabled;
		}

		@Override
		public void setTransactionEnabled(boolean transactionEnabled) {
			this.transactionEnabled = transactionEnabled;
		}

		@Override
		public void setLogDetails(LogDetails logDetails) {
			this.logDetails = logDetails;
		}

		@Override
		public LogDetails getLogDetails() {
			return logDetails;
		}
	}
}
