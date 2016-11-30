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
package com.att.nsa.cambria.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletResponse;

import com.att.nsa.apiServer.streams.ChunkedInputStream;
import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.backends.Publisher.message;
import com.att.nsa.cambria.resources.streamReaders.CambriaJsonStreamReader;
import com.att.nsa.cambria.resources.streamReaders.CambriaRawStreamReader;
import com.att.nsa.cambria.resources.streamReaders.CambriaStreamReader;
import com.att.nsa.cambria.resources.streamReaders.CambriaTextStreamReader;
import com.att.nsa.drumlin.service.standards.HttpStatusCodes;

/**
 * An inbound event set.
 * 
 * @author author
 */
public class CambriaEventSet {
	private final reader fReader;

	/**
	 * constructor initialization
	 * 
	 * @param mediaType
	 * @param originalStream
	 * @param chunked
	 * @param defPartition
	 * @throws CambriaApiException
	 */
	public CambriaEventSet(String mediaType, InputStream originalStream,
			boolean chunked, String defPartition) throws CambriaApiException {
		InputStream is = originalStream;
		if (chunked) {
			is = new ChunkedInputStream(originalStream);
		}

		if (("application/json").equals(mediaType)) {
			if (chunked) {
				throw new CambriaApiException(
						HttpServletResponse.SC_BAD_REQUEST,
						"The JSON stream reader doesn't support chunking.");
			}
			fReader = new CambriaJsonStreamReader(is, defPartition);
		} else if (("application/cambria").equals(mediaType)) {
			fReader = new CambriaStreamReader(is);
		} else if (("application/cambria-zip").equals(mediaType)) {
			try {
				is = new GZIPInputStream(is);
			} catch (IOException e) {
				throw new CambriaApiException(HttpStatusCodes.k400_badRequest,
						"Couldn't read compressed format: " + e);
			}
			fReader = new CambriaStreamReader(is);
		} else if (("text/plain").equals(mediaType)) {
			fReader = new CambriaTextStreamReader(is, defPartition);
		} else {
			fReader = new CambriaRawStreamReader(is, defPartition);
		}
	}

	/**
	 * Get the next message from this event set. Returns null when the end of
	 * stream is reached. Will block until a message arrives (or the stream is
	 * closed/broken).
	 * 
	 * @return a message, or null
	 * @throws IOException
	 * @throws CambriaApiException
	 */
	public message next() throws IOException, CambriaApiException {
		return fReader.next();
	}

	/**
	 * 
	 * @author author
	 *
	 */
	public interface reader {
		/**
		 * 
		 * @return
		 * @throws IOException
		 * @throws CambriaApiException
		 */
		message next() throws IOException, CambriaApiException;
	}
}
