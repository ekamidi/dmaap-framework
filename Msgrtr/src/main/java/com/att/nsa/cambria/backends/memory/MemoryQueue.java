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

import java.util.ArrayList;
import java.util.HashMap;

import com.att.nsa.cambria.backends.Consumer;
import com.att.nsa.cambria.backends.Publisher.message;

/**
 * When broker type is memory, then this class is doing all the topic related
 * operations
 * 
 * @author author
 *
 */
public class MemoryQueue {
	// map from topic to list of msgs
	private HashMap<String, LogBuffer> fQueue;
	private HashMap<String, HashMap<String, Integer>> fOffsets;

	/**
	 * constructor storing hashMap objects in Queue and Offsets object
	 */
	public MemoryQueue() {
		fQueue = new HashMap<String, LogBuffer>();
		fOffsets = new HashMap<String, HashMap<String, Integer>>();
	}

	/**
	 * method used to create topic
	 * 
	 * @param topic
	 */
	public synchronized void createTopic(String topic) {
		LogBuffer q = fQueue.get(topic);
		if (q == null) {
			q = new LogBuffer(1024 * 1024);
			fQueue.put(topic, q);
		}
	}

	/**
	 * method used to remove topic
	 * 
	 * @param topic
	 */
	public synchronized void removeTopic(String topic) {
		LogBuffer q = fQueue.get(topic);
		if (q != null) {
			fQueue.remove(topic);
		}
	}

	/**
	 * method to write message on topic
	 * 
	 * @param topic
	 * @param m
	 */
	public synchronized void put(String topic, message m) {
		LogBuffer q = fQueue.get(topic);
		if (q == null) {
			createTopic(topic);
			q = fQueue.get(topic);
		}
		q.push(m.getMessage());
	}

	/**
	 * method to read consumer messages
	 * 
	 * @param topic
	 * @param consumerName
	 * @return
	 */
	public synchronized Consumer.Message get(String topic, String consumerName) {
		final LogBuffer q = fQueue.get(topic);
		if (q == null) {
			return null;
		}

		HashMap<String, Integer> offsetMap = fOffsets.get(consumerName);
		if (offsetMap == null) {
			offsetMap = new HashMap<String, Integer>();
			fOffsets.put(consumerName, offsetMap);
		}
		Integer offset = offsetMap.get(topic);
		if (offset == null) {
			offset = 0;
		}

		final msgInfo result = q.read(offset);
		if (result != null && result.msg != null) {
			offsetMap.put(topic, result.offset + 1);
		}
		return result;
	}

	/**
	 * static inner class used to details about consumed messages
	 * 
	 * @author author
	 *
	 */
	private static class msgInfo implements Consumer.Message {
		/**
		 * published message which is consumed
		 */
		public String msg;
		/**
		 * offset associated with message
		 */
		public int offset;

		/**
		 * get offset of messages
		 */
		@Override
		public long getOffset() {
			return offset;
		}

		/**
		 * get consumed message
		 */
		@Override
		public String getMessage() {
			return msg;
		}
	}

 /**
 * 
 * @author author
 *
 * private LogBuffer class has synchronized push and read method
 */
	private class LogBuffer {
		private int fBaseOffset;
		private final int fMaxSize;
		private final ArrayList<String> fList;

		/**
		 * constructor initializing the offset, maxsize and list
		 * 
		 * @param maxSize
		 */
		public LogBuffer(int maxSize) {
			fBaseOffset = 0;
			fMaxSize = maxSize;
			fList = new ArrayList<String>();
		}

		/**
		 * pushing message
		 * 
		 * @param msg
		 */
		public synchronized void push(String msg) {
			fList.add(msg);
			while (fList.size() > fMaxSize) {
				fList.remove(0);
				fBaseOffset++;
			}
		}

		/**
		 * reading messages
		 * 
		 * @param offset
		 * @return
		 */
		public synchronized msgInfo read(int offset) {
			final int actual = Math.max(0, offset - fBaseOffset);

			final msgInfo mi = new msgInfo();
			mi.msg = (actual >= fList.size()) ? null : fList.get(actual);
			if (mi.msg == null)
				return null;

			mi.offset = actual + fBaseOffset;
			return mi;
		}

	}
}
