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
package com.att.nsa.cambria.backends.memory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.att.nsa.cambria.backends.Publisher;

import kafka.producer.KeyedMessage;

/**
 * class used for logging perspective
 * 
 * @author author
 *
 */
public class MessageLogger implements Publisher {
	public MessageLogger() {
	}

	public void setFile(File f) throws FileNotFoundException {
		fStream = new FileOutputStream(f, true);
	}

	/** 
	 * 
	 * @param topic
	 * @param msg
	 * @throws IOException
	 */
	@Override
	public void sendMessage(String topic, message msg) throws IOException {
		logMsg(msg);
	}

	/**
	 * @param topic
	 * @param msgs
	 * @throws IOException
	 */
	@Override
	public void sendMessages(String topic, List<? extends message> msgs) throws IOException {
		for (message m : msgs) {
			logMsg(m);
		}
	}

	/**
	 * @param topic
	 * @param kms
	 * @throws IOException
	 */
	@Override
	public void sendBatchMessage(String topic, ArrayList<KeyedMessage<String, String>> kms) throws

	IOException {
	}

	private FileOutputStream fStream;

	/**
	 * 
	 * @param msg
	 * @throws IOException
	 */
	private void logMsg(message msg) throws IOException {
		String key = msg.getKey();
		if (key == null)
			key = "<none>";

		fStream.write('[');
		fStream.write(key.getBytes());
		fStream.write("] ".getBytes());
		fStream.write(msg.getMessage().getBytes());
		fStream.write('\n');
	}
}
