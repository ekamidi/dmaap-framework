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

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
//import org.apache.log4-j.Logger;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.metabroker.Broker;
import com.att.nsa.cambria.metabroker.Topic;
import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.configs.ConfigDb;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.configs.ConfigPath;
import com.att.nsa.drumlin.service.standards.HttpStatusCodes;
import com.att.nsa.drumlin.till.nv.rrNvReadable;
import com.att.nsa.security.NsaAcl;
import com.att.nsa.security.NsaAclUtils;
import com.att.nsa.security.NsaApiKey;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;

/**
 * class performing all topic operations
 * 
 * @author author
 *
 */

public class DMaaPKafkaMetaBroker implements Broker {

	//private static final Logger log = Logger.getLogger(DMaaPKafkaMetaBroker.class);
	private static final EELFLogger log = EELFManager.getInstance().getLogger(ConfigurationReader.class);
	

	/**
	 * DMaaPKafkaMetaBroker constructor initializing
	 * 
	 * @param settings
	 * @param zk
	 * @param configDb
	 */
	public DMaaPKafkaMetaBroker(@Qualifier("propertyReader") rrNvReadable settings,
			@Qualifier("dMaaPZkClient") ZkClient zk, @Qualifier("dMaaPZkConfigDb") ConfigDb configDb) {
		//fSettings = settings;
		fZk = zk;
		fCambriaConfig = configDb;
		fBaseTopicData = configDb.parse("/topics");
	}

	@Override
	public List<Topic> getAllTopics() throws ConfigDbException {
		log.info("Retrieving list of all the topics.");
		final LinkedList<Topic> result = new LinkedList<Topic>();
		try {
			log.info("Retrieving all topics from root: " + zkTopicsRoot);
			final List<String> topics = fZk.getChildren(zkTopicsRoot);
			for (String topic : topics) {
				result.add(new KafkaTopic(topic, fCambriaConfig, fBaseTopicData));
			}

			JSONObject dataObj = new JSONObject();
			dataObj.put("topics", new JSONObject());

			for (String topic : topics) {
				dataObj.getJSONObject("topics").put(topic, new JSONObject());
			}
		} catch (ZkNoNodeException excp) {
			// very fresh kafka doesn't have any topics or a topics node
			log.error("ZK doesn't have a Kakfa topics node at " + zkTopicsRoot, excp);
		}
		return result;
	}

	@Override
	public Topic getTopic(String topic) throws ConfigDbException {
		if (fZk.exists(zkTopicsRoot + "/" + topic)) {
			return getKafkaTopicConfig(fCambriaConfig, fBaseTopicData, topic);
		}
		// else: no such topic in kafka
		return null;
	}

	/**
	 * static method get KafkaTopic object
	 * 
	 * @param db
	 * @param base
	 * @param topic
	 * @return
	 * @throws ConfigDbException
	 */
	public static KafkaTopic getKafkaTopicConfig(ConfigDb db, ConfigPath base, String topic) throws ConfigDbException {
		return new KafkaTopic(topic, db, base);
	}

	/**
	 * creating topic
	 */
	@Override
	public Topic createTopic(String topic, String desc, String ownerApiKey, int partitions, int replicas,
			boolean transactionEnabled) throws TopicExistsException, CambriaApiException {
		log.info("Creating topic: " + topic);
		try {
			log.info("Check if topic [" + topic + "] exist.");
			// first check for existence "our way"
			final Topic t = getTopic(topic);
			if (t != null) {
				log.info("Could not create topic [" + topic + "]. Topic Already exists.");
				throw new TopicExistsException("Could not create topic [" + topic + "]. Topic Alreay exists.");
			}
		} catch (ConfigDbException e1) {
			log.error("Topic [" + topic + "] could not be created. Couldn't check topic data in config db.", e1);
			throw new CambriaApiException(HttpStatusCodes.k503_serviceUnavailable,
					"Couldn't check topic data in config db.");
		}

		// we only allow 3 replicas. (If we don't test this, we get weird
		// results from the cluster,
		// so explicit test and fail.)
		if (replicas < 1 || replicas > 3) {
			log.info("Topic [" + topic + "] could not be created. The replica count must be between 1 and 3.");
			throw new CambriaApiException(HttpStatusCodes.k400_badRequest,
					"The replica count must be between 1 and 3.");
		}
		if (partitions < 1) {
			log.info("Topic [" + topic + "] could not be created. The partition count must be at least 1.");
			throw new CambriaApiException(HttpStatusCodes.k400_badRequest, "The partition count must be at least 1.");
		}

		// create via kafka
		try {
			ZkClient zkClient = null;
			try {
				log.info("Loading zookeeper client for creating topic.");
				// FIXME: use of this scala module$ thing is a goofy hack to
				// make Kafka aware of the
				// topic creation. (Otherwise, the topic is only partially
				// created in ZK.)
				zkClient = new ZkClient(ConfigurationReader.getMainZookeeperConnectionString(), 10000, 10000,
						ZKStringSerializer$.MODULE$);

				log.info("Zookeeper client loaded successfully. Creating topic.");
				AdminUtils.createTopic(zkClient, topic, partitions, replicas, new Properties());
			} catch (kafka.common.TopicExistsException e) {
				log.error("Topic [" + topic + "] could not be created. " + e.getMessage(), e);
				throw new TopicExistsException(topic);
			} catch (ZkNoNodeException e) {
				log.error("Topic [" + topic + "] could not be created. The Kafka cluster is not setup.", e);
				// Kafka throws this when the server isn't running (and perhaps
				// hasn't ever run)
				throw new CambriaApiException(HttpStatusCodes.k503_serviceUnavailable,
						"The Kafka cluster is not setup.");
			} catch (kafka.admin.AdminOperationException e) {
				// Kafka throws this when the server isn't running (and perhaps
				// hasn't ever run)
				log.error("The Kafka cluster can't handle your request. Talk to the administrators: " + e.getMessage(),
						e);
				throw new CambriaApiException(HttpStatusCodes.k503_serviceUnavailable,
						"The Kafka cluster can't handle your request. Talk to the administrators.");
			} finally {
				log.info("Closing zookeeper connection.");
				if (zkClient != null)
					zkClient.close();
			}

			log.info("Creating topic entry for topic: " + topic);
			// underlying Kafka topic created. now setup our API info
			return createTopicEntry(topic, desc, ownerApiKey, transactionEnabled);
		} catch (ConfigDbException excp) {
			log.error("Failed to create topic data. Talk to the administrators: " + excp.getMessage(), excp);
			throw new CambriaApiException(HttpStatusCodes.k503_serviceUnavailable,
					"Failed to create topic data. Talk to the administrators.");
		}
	}

	@Override
	public void deleteTopic(String topic) throws CambriaApiException, TopicExistsException {
		log.info("Deleting topic: " + topic);
		ZkClient zkClient = null;
		try {
			log.info("Loading zookeeper client for topic deletion.");
			// FIXME: use of this scala module$ thing is a goofy hack to make
			// Kafka aware of the
			// topic creation. (Otherwise, the topic is only partially created
			// in ZK.)
			zkClient = new ZkClient(ConfigurationReader.getMainZookeeperConnectionString(), 10000, 10000,
					ZKStringSerializer$.MODULE$);

			log.info("Zookeeper client loaded successfully. Deleting topic.");
			AdminUtils.deleteTopic(zkClient, topic);
		} catch (kafka.common.TopicExistsException e) {
			log.error("Failed to delete topic [" + topic + "]. " + e.getMessage(), e);
			throw new TopicExistsException(topic);
		} catch (ZkNoNodeException e) {
			log.error("Failed to delete topic [" + topic + "]. The Kafka cluster is not setup." + e.getMessage(), e);
			// Kafka throws this when the server isn't running (and perhaps
			// hasn't ever run)
			throw new CambriaApiException(HttpStatusCodes.k503_serviceUnavailable, "The Kafka cluster is not setup.");
		} catch (kafka.admin.AdminOperationException e) {
			// Kafka throws this when the server isn't running (and perhaps
			// hasn't ever run)
			log.error("The Kafka cluster can't handle your request. Talk to the administrators." + e.getMessage(), e);
			throw new CambriaApiException(HttpStatusCodes.k503_serviceUnavailable,
					"The Kafka cluster can't handle your request. Talk to the administrators.");
		} finally {
			log.info("Closing zookeeper connection.");
			if (zkClient != null)
				zkClient.close();
		}

		// throw new UnsupportedOperationException ( "We can't programmatically
		// delete Kafka topics yet." );
	}

	//private final rrNvReadable fSettings;
	private final ZkClient fZk;
	private final ConfigDb fCambriaConfig;
	private final ConfigPath fBaseTopicData;

	private static final String zkTopicsRoot = "/brokers/topics";
	private static final JSONObject kEmptyAcl = new JSONObject();

	/**
	 * method Providing KafkaTopic Object associated with owner and
	 * transactionenabled or not
	 * 
	 * @param name
	 * @param desc
	 * @param owner
	 * @param transactionEnabled
	 * @return
	 * @throws ConfigDbException
	 */
	public KafkaTopic createTopicEntry(String name, String desc, String owner, boolean transactionEnabled)
			throws ConfigDbException {
		return createTopicEntry(fCambriaConfig, fBaseTopicData, name, desc, owner, transactionEnabled);
	}

	/**
	 * static method giving kafka topic object
	 * 
	 * @param db
	 * @param basePath
	 * @param name
	 * @param desc
	 * @param owner
	 * @param transactionEnabled
	 * @return
	 * @throws ConfigDbException
	 */
	public static KafkaTopic createTopicEntry(ConfigDb db, ConfigPath basePath, String name, String desc, String owner,
			boolean transactionEnabled) throws ConfigDbException {
		final JSONObject o = new JSONObject();
		o.put("owner", owner);
		o.put("description", desc);
		o.put("txenabled", transactionEnabled);
		db.store(basePath.getChild(name), o.toString());
		return new KafkaTopic(name, db, basePath);
	}

	/**
	 * class performing all user opearation like user is eligible to read,
	 * write. permitting a user to write and read,
	 * 
	 * @author author
	 *
	 */
	public static class KafkaTopic implements Topic {
		/**
		 * constructor initializes
		 * 
		 * @param name
		 * @param configdb
		 * @param baseTopic
		 * @throws ConfigDbException
		 */
		public KafkaTopic(String name, ConfigDb configdb, ConfigPath baseTopic) throws ConfigDbException {
			fName = name;
			fConfigDb = configdb;
			fBaseTopicData = baseTopic;

			String data = fConfigDb.load(fBaseTopicData.getChild(fName));
			if (data == null) {
				data = "{}";
			}

			final JSONObject o = new JSONObject(data);
			fOwner = o.optString("owner", "");
			fDesc = o.optString("description", "");
			fTransactionEnabled = o.optBoolean("txenabled", false);// default
																	// value is
																	// false
			// if this topic has an owner, it needs both read/write ACLs. If there's no
						// owner (or it's empty), null is okay -- this is for existing or implicitly
						// created topics.
						JSONObject readers = o.optJSONObject ( "readers" );
						if ( readers == null && fOwner.length () > 0 ) readers = kEmptyAcl;
						fReaders = fromJson ( readers );

						JSONObject writers = o.optJSONObject ( "writers" );
						if ( writers == null && fOwner.length () > 0 ) writers = kEmptyAcl;
						fWriters = fromJson ( writers );
		}
		 private NsaAcl fromJson(JSONObject o) {
				NsaAcl acl = new NsaAcl();
				if (o != null) {
					JSONArray a = o.optJSONArray("allowed");
					if (a != null) {
						for (int i = 0; i < a.length(); ++i) {
							String user = a.getString(i);
							acl.add(user);
						}
					}
				}
				return acl;
			}
		@Override
		public String getName() {
			return fName;
		}

		@Override
		public String getOwner() {
			return fOwner;
		}

		@Override
		public String getDescription() {
			return fDesc;
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
		public void checkUserRead(NsaApiKey user) throws AccessDeniedException  {
			NsaAclUtils.checkUserAccess ( fOwner, getReaderAcl(), user );
		}

		@Override
		public void checkUserWrite(NsaApiKey user) throws AccessDeniedException  {
			NsaAclUtils.checkUserAccess ( fOwner, getWriterAcl(), user );
		}

		@Override
		public void permitWritesFromUser(String pubId, NsaApiKey asUser)
				throws ConfigDbException, AccessDeniedException {
			updateAcl(asUser, false, true, pubId);
		}

		@Override
		public void denyWritesFromUser(String pubId, NsaApiKey asUser) throws ConfigDbException, AccessDeniedException {
			updateAcl(asUser, false, false, pubId);
		}

		@Override
		public void permitReadsByUser(String consumerId, NsaApiKey asUser)
				throws ConfigDbException, AccessDeniedException {
			updateAcl(asUser, true, true, consumerId);
		}

		@Override
		public void denyReadsByUser(String consumerId, NsaApiKey asUser)
				throws ConfigDbException, AccessDeniedException {
			updateAcl(asUser, true, false, consumerId);
		}

		private void updateAcl(NsaApiKey asUser, boolean reader, boolean add, String key)
				throws ConfigDbException, AccessDeniedException{
			try
			{
				final NsaAcl acl = NsaAclUtils.updateAcl ( this, asUser, key, reader, add );
	
				// we have to assume we have current data, or load it again. for the expected use
				// case, assuming we can overwrite the data is fine.
				final JSONObject o = new JSONObject ();
				o.put ( "owner", fOwner );
				o.put ( "readers", safeSerialize ( reader ? acl : fReaders ) );
				o.put ( "writers", safeSerialize ( reader ? fWriters : acl ) );
				fConfigDb.store ( fBaseTopicData.getChild ( fName ), o.toString () );
				
				log.info ( "ACL_UPDATE: " + asUser.getKey () + " " + ( add ? "added" : "removed" ) + ( reader?"subscriber":"publisher" ) + " " + key + " on " + fName );
	
			}
			catch ( ConfigDbException x )
			{
				throw x;
			}
			catch ( AccessDeniedException x )
			{
				throw x;
			}
			
		}

		private JSONObject safeSerialize(NsaAcl acl) {
			return acl == null ? null : acl.serialize();
		}

		private final String fName;
		private final ConfigDb fConfigDb;
		private final ConfigPath fBaseTopicData;
		private final String fOwner;
		private final String fDesc;
		private final NsaAcl fReaders;
		private final NsaAcl fWriters;
		private boolean fTransactionEnabled;

		public boolean isTransactionEnabled() {
			return fTransactionEnabled;
		}

		@Override
		public Set<String> getOwners() {
			final TreeSet<String> owners = new TreeSet<String> ();
			owners.add ( fOwner );
			return owners;
		}
	}

}
