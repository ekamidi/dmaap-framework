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
package com.att.nsa.cambria.metrics.publisher;

//import org.slf4j.Logger;

//
import com.att.eelf.configuration.EELFLogger;
//import com.att.eelf.configuration.EELFManager;

/**
 * 
 * @author author
 *
 */
public interface CambriaClient {
	/**
	 * An exception at the Cambria layer. This is used when the HTTP transport
	 * layer returns a success code but the transaction is not completed as
	 * expected.
	 */
	public class CambriaApiException extends Exception {
		/**
		 * 
		 * @param msg
		 */
		public CambriaApiException(String msg) {
			super(msg);
		}

		/**
		 * 
		 * @param msg
		 * @param t
		 */
		public CambriaApiException(String msg, Throwable t) {
			super(msg, t);
		}

		private static final long serialVersionUID = 1L;
	}

	/**
	 * Optionally set the Logger to use
	 * 
	 * @param log
	 */
	void logTo(EELFLogger  log);

	/**
	 * Set the API credentials for this client connection. Subsequent calls will
	 *  include authentication headers.who i
	 * 
	 * @param apiKey
	 * @param apiSecret
	 */
	void setApiCredentials(String apiKey, String apiSecret);

	/**
	 * Remove API credentials, if any, on this connection. Subsequent calls will
	 * not include authentication headers.
	 */
	void clearApiCredentials();

	/**
	 * Close this connection. Some client interfaces have additional close
	 * capability.
	 */
	void close();
}
