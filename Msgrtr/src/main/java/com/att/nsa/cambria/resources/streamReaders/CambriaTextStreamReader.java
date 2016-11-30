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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletResponse;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.backends.Publisher.message;
import com.att.nsa.cambria.beans.LogDetails;
import com.att.nsa.cambria.resources.CambriaEventSet.reader;

/**
 * This stream reader just pulls single lines. It uses the default partition if provided. If
 * not, the key is the current time, which does not guarantee ordering.
 * 
 * @author author
 *
 */
public class CambriaTextStreamReader implements reader
{
	/**
	 * This is the constructor for Cambria Text Reader format
	 * @param is
	 * @param defPart
	 * @throws CambriaApiException
	 */
	public CambriaTextStreamReader ( InputStream is, String defPart ) throws CambriaApiException
	{
		fReader = new BufferedReader ( new InputStreamReader ( is ) );
		fDefPart = defPart;
	}

	@Override
	/**
	 * next() method iterates through msg length
	 * throws IOException
	 * throws CambriaApiException
	 * 
	 */ 
	public message next () throws CambriaApiException
	{
		try
		{
			final String line = fReader.readLine ();
			if ( line == null ) return null;

			return new message ()
			{
				private LogDetails logDetails;
				private boolean transactionEnabled;

				/**
				 * returns boolean value which 
				 * indicates whether transaction is enabled
				 * @return
				 */
				public boolean isTransactionEnabled() {
					return transactionEnabled;
				}

				/**
				 * sets boolean value which 
				 * indicates whether transaction is enabled
				 */
				public void setTransactionEnabled(boolean transactionEnabled) {
					this.transactionEnabled = transactionEnabled;
				}
				
				@Override
				/**
				 * @returns key
				 * It ch4ecks whether fDefPart value is Null.
				 * If yes, it will return ystem.currentTimeMillis () else
				 * it will return fDefPart variable value
				 */
				public String getKey ()
				{
					return fDefPart == null ? "" + System.currentTimeMillis () : fDefPart;
				}

				@Override
				/**
				 * returns the message in String type object
				 * @return
				 */
				public String getMessage ()
				{
					return line;
				}

				@Override
				/**
				 * set log details in logDetails variable
				 */
				public void setLogDetails(LogDetails logDetails) {
					this.logDetails = logDetails;
				}

				@Override
				/**
				 * get the log details
				 */
				public LogDetails getLogDetails() {
					return this.logDetails;
				}
			};
		}
		catch ( IOException e )
		{
			throw new CambriaApiException ( HttpServletResponse.SC_BAD_REQUEST, e.getMessage () );
		}
	}
	
	private final BufferedReader fReader;
	private final String fDefPart;
}
