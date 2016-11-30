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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.att.nsa.cambria.metabroker.Broker;
import com.att.nsa.cambria.metabroker.Topic;
import com.att.nsa.configs.ConfigDb;
import com.att.nsa.drumlin.till.nv.rrNvReadable;
import com.att.nsa.security.NsaAcl;
import com.att.nsa.security.NsaApiKey;

/**
 * 
 * @author author
 *
 */
public class MemoryMetaBroker implements Broker {
	/**
	 * 
	 * @param mq
	 * @param configDb
	 * @param settings
	 */
	public MemoryMetaBroker(MemoryQueue mq, ConfigDb configDb) {
	//public MemoryMetaBroker(MemoryQueue mq, ConfigDb configDb, rrNvReadable settings) {
		fQueue = mq;
		fTopics = new HashMap<String, MemTopic>();
	}

	@Override
	public List<Topic> getAllTopics() {
		return new LinkedList<Topic>(fTopics.values());
	}

	@Override
	public Topic getTopic(String topic) {
		return fTopics.get(topic);
	}

	@Override
	public Topic createTopic(String topic, String desc, String ownerApiId, int partitions, int replicas,
			boolean transactionEnabled) throws TopicExistsException {
		if (getTopic(topic) != null) {
			throw new TopicExistsException(topic);
		}
		fQueue.createTopic(topic);
		fTopics.put(topic, new MemTopic(topic, desc, ownerApiId, transactionEnabled));
		return getTopic(topic);
	}

	@Override
	public void deleteTopic(String topic) {
		fTopics.remove(topic);
		fQueue.removeTopic(topic);
	}

	private final MemoryQueue fQueue;
	private final HashMap<String, MemTopic> fTopics;

	private static class MemTopic implements Topic {
		/**
		 * constructor initialization
		 * 
		 * @param name
		 * @param desc
		 * @param owner
		 * @param transactionEnabled
		 */
		public MemTopic(String name, String desc, String owner, boolean transactionEnabled) {
			fName = name;
			fDesc = desc;
			fOwner = owner;
			ftransactionEnabled = transactionEnabled;
			fReaders = null;
			fWriters = null;
		}

		@Override
		public String getOwner() {
			return fOwner;
		}

		@Override
		public NsaAcl getReaderAcl() {
			return fReaders;
		}

		@Override
		public NsaAcl getWriterAcl() {
			return fWriters;
		}

		@Override
		public void checkUserRead(NsaApiKey user) throws AccessDeniedException {
			if (fReaders != null && (user == null || !fReaders.canUser(user.getKey()))) {
				throw new AccessDeniedException(user == null ? "" : user.getKey());
			}
		}

		@Override
		public void checkUserWrite(NsaApiKey user) throws AccessDeniedException {
			if (fWriters != null && (user == null || !fWriters.canUser(user.getKey()))) {
				throw new AccessDeniedException(user == null ? "" : user.getKey());
			}
		}

		@Override
		public String getName() {
			return fName;
		}

		@Override
		public String getDescription() {
			return fDesc;
		}

		@Override
		public void permitWritesFromUser(String publisherId, NsaApiKey asUser) throws AccessDeniedException {
			if (!fOwner.equals(asUser.getKey())) {
				throw new AccessDeniedException("User does not own this topic " + fName);
			}
			if (fWriters == null) {
				fWriters = new NsaAcl();
			}
			fWriters.add(publisherId);
		}

		@Override
		public void denyWritesFromUser(String publisherId, NsaApiKey asUser) throws AccessDeniedException {
			if (!fOwner.equals(asUser.getKey())) {
				throw new AccessDeniedException("User does not own this topic " + fName);
			}
			fWriters.remove(publisherId);
		}

		@Override
		public void permitReadsByUser(String consumerId, NsaApiKey asUser) throws AccessDeniedException {
			if (!fOwner.equals(asUser.getKey())) {
				throw new AccessDeniedException("User does not own this topic " + fName);
			}
			if (fReaders == null) {
				fReaders = new NsaAcl();
			}
			fReaders.add(consumerId);
		}

		@Override
		public void denyReadsByUser(String consumerId, NsaApiKey asUser) throws AccessDeniedException {
			if (!fOwner.equals(asUser.getKey())) {
				throw new AccessDeniedException("User does not own this topic " + fName);
			}
			fReaders.remove(consumerId);
		}

		private final String fName;
		private final String fDesc;
		private final String fOwner;
		private NsaAcl fReaders;
		private NsaAcl fWriters;
		private boolean ftransactionEnabled;

		@Override
		public boolean isTransactionEnabled() {
			return ftransactionEnabled;
		}

		@Override
		public Set<String> getOwners() {
			final TreeSet<String> set = new TreeSet<String> ();
			set.add ( fOwner );
			return set;
		}
	}
}
