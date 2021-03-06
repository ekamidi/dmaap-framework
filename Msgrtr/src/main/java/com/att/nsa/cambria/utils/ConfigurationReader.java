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
package com.att.nsa.cambria.utils;

import javax.servlet.ServletException;

import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
//import com.att.nsa.apiServer.util.Emailer;
import com.att.nsa.cambria.utils.Emailer;
import com.att.nsa.cambria.backends.ConsumerFactory;
import com.att.nsa.cambria.backends.MetricsSet;
import com.att.nsa.cambria.backends.Publisher;
import com.att.nsa.cambria.backends.kafka.KafkaConsumerCache.KafkaConsumerCacheException;
import com.att.nsa.cambria.backends.memory.MemoryConsumerFactory;
import com.att.nsa.cambria.backends.memory.MemoryMetaBroker;
import com.att.nsa.cambria.backends.memory.MemoryQueue;
import com.att.nsa.cambria.backends.memory.MemoryQueuePublisher;
//import com.att.nsa.cambria.beans.DMaaPBlacklist;
import com.att.nsa.cambria.beans.DMaaPCambriaLimiter;
import com.att.nsa.cambria.beans.DMaaPZkConfigDb;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.cambria.metabroker.Broker;
import com.att.nsa.cambria.security.DMaaPAuthenticator;
import com.att.nsa.cambria.security.impl.DMaaPOriginalUebAuthenticator;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.configs.confimpl.MemConfigDb;
import com.att.nsa.drumlin.till.nv.rrNvReadable;
import com.att.nsa.drumlin.till.nv.rrNvReadable.invalidSettingValue;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.limits.Blacklist;
import com.att.nsa.security.NsaAuthenticatorService;
//import com.att.nsa.security.authenticators.OriginalUebAuthenticator;
import com.att.nsa.security.db.BaseNsaApiDbImpl;
import com.att.nsa.security.db.NsaApiDb;
import com.att.nsa.security.db.NsaApiDb.KeyExistsException;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;
import com.att.nsa.security.db.simple.NsaSimpleApiKeyFactory;

/**
 * Class is created for all the configuration for rest and service layer
 * integration.
 *
 */
@Component
public class ConfigurationReader {

//	private rrNvReadable settings;
	private Broker fMetaBroker;
	private ConsumerFactory fConsumerFactory;
	private Publisher fPublisher;
	private MetricsSet fMetrics;
	@Autowired
	private DMaaPCambriaLimiter fRateLimiter;
	private NsaApiDb<NsaSimpleApiKey> fApiKeyDb;
	/* private DMaaPTransactionObjDB<DMaaPTransactionObj> fTranDb; */
	private DMaaPAuthenticator<NsaSimpleApiKey> fSecurityManager;
	private NsaAuthenticatorService<NsaSimpleApiKey> nsaSecurityManager;
	private static CuratorFramework curator;
	private ZkClient zk;
	private DMaaPZkConfigDb fConfigDb;
	private MemoryQueue q;
	private MemoryMetaBroker mmb;
	private Blacklist fIpBlackList;
	private Emailer fEmailer;

	private static final EELFLogger log = EELFManager.getInstance().getLogger(ConfigurationReader.class);
	//private static final Logger log = Logger.getLogger(ConfigurationReader.class.toString());

	/**
	 * constructor to initialize all the values
	 * 
	 * @param settings
	 * @param fMetrics
	 * @param zk
	 * @param fConfigDb
	 * @param fPublisher
	 * @param curator
	 * @param fConsumerFactory
	 * @param fMetaBroker
	 * @param q
	 * @param mmb
	 * @param fApiKeyDb
	 * @param fSecurityManager
	 * @throws missingReqdSetting
	 * @throws invalidSettingValue
	 * @throws ServletException
	 * @throws KafkaConsumerCacheException
	 * @throws ConfigDbException 
	 */
	@Autowired
	public ConfigurationReader(@Qualifier("propertyReader") rrNvReadable settings,
			@Qualifier("dMaaPMetricsSet") MetricsSet fMetrics, @Qualifier("dMaaPZkClient") ZkClient zk,
			@Qualifier("dMaaPZkConfigDb") DMaaPZkConfigDb fConfigDb, @Qualifier("kafkaPublisher") Publisher fPublisher,
			@Qualifier("curator") CuratorFramework curator,
			@Qualifier("dMaaPKafkaConsumerFactory") ConsumerFactory fConsumerFactory,
			@Qualifier("dMaaPKafkaMetaBroker") Broker fMetaBroker, @Qualifier("q") MemoryQueue q,
			@Qualifier("mmb") MemoryMetaBroker mmb, @Qualifier("dMaaPNsaApiDb") NsaApiDb<NsaSimpleApiKey> fApiKeyDb,
			/*
			 * @Qualifier("dMaaPTranDb")
			 * DMaaPTransactionObjDB<DMaaPTransactionObj> fTranDb,
			 */
			@Qualifier("dMaaPAuthenticatorImpl") DMaaPAuthenticator<NsaSimpleApiKey> fSecurityManager
			)
					throws missingReqdSetting, invalidSettingValue, ServletException, KafkaConsumerCacheException, ConfigDbException {
		//this.settings = settings;
		this.fMetrics = fMetrics;
		this.zk = zk;
		this.fConfigDb = fConfigDb;
		this.fPublisher = fPublisher;
		ConfigurationReader.curator = curator;
		this.fConsumerFactory = fConsumerFactory;
		this.fMetaBroker = fMetaBroker;
		this.q = q;
		this.mmb = mmb;
		this.fApiKeyDb = fApiKeyDb;
		/* this.fTranDb = fTranDb; */
		this.fSecurityManager = fSecurityManager;
		
		long allowedtimeSkewMs=600000L;
		String strallowedTimeSkewM= com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"authentication.allowedTimeSkewMs");
		if(null!=strallowedTimeSkewM)allowedtimeSkewMs= Long.parseLong(strallowedTimeSkewM);
				
	//	boolean requireSecureChannel = true;
		//String strrequireSecureChannel= com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"aauthentication.requireSecureChannel");
		//if(strrequireSecureChannel!=null)requireSecureChannel=Boolean.parseBoolean(strrequireSecureChannel);
		//this.nsaSecurityManager = new NsaAuthenticatorService<NsaSimpleApiKey>(this.fApiKeyDb, settings.getLong("authentication.allowedTimeSkewMs", 600000L), settings.getBoolean("authentication.requireSecureChannel", true));
		//this.nsaSecurityManager = new NsaAuthenticatorService<NsaSimpleApiKey>(this.fApiKeyDb, allowedtimeSkewMs, requireSecureChannel);
		
		servletSetup();
	}

	protected void servletSetup()
			throws rrNvReadable.missingReqdSetting, rrNvReadable.invalidSettingValue, ServletException, ConfigDbException {
		try {

			fMetrics.toJson();
			fMetrics.setupCambriaSender();

			// add the admin authenticator
				//		final String adminSecret = settings.getString ( CambriaConstants.kSetting_AdminSecret, null );
						final String adminSecret = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,CambriaConstants.kSetting_AdminSecret);
						//adminSecret = "fe3cCompound";
						if ( adminSecret != null && adminSecret.length () > 0 )
						{
							try
							{
								
								final NsaApiDb<NsaSimpleApiKey> adminDb = new BaseNsaApiDbImpl<NsaSimpleApiKey> ( new MemConfigDb(), new NsaSimpleApiKeyFactory() );
								adminDb.createApiKey ( "admin", adminSecret );
								//nsaSecurityManager.addAuthenticator ( new OriginalUebAuthenticator<NsaSimpleApiKey> ( adminDb, 10*60*1000 ) );
						        fSecurityManager.addAuthenticator ( new DMaaPOriginalUebAuthenticator<NsaSimpleApiKey> ( adminDb, 10*60*1000 ) );
							}
							catch ( KeyExistsException e )
							{
								throw new RuntimeException ( "This key can't exist in a fresh in-memory DB!", e );
							}
						}
						
			// setup a backend
			//final String type = settings.getString(CambriaConstants.kBrokerType, CambriaConstants.kBrokerType_Kafka);
			 String type = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,CambriaConstants.kBrokerType);
			if (type==null) type = CambriaConstants.kBrokerType_Kafka;
			if (CambriaConstants.kBrokerType_Kafka.equalsIgnoreCase(type)) {
				log.info("Broker Type is:" + CambriaConstants.kBrokerType_Kafka);

			} else if (CambriaConstants.kBrokerType_Memory.equalsIgnoreCase(type)) {
				log.info("Broker Type is:" + CambriaConstants.kBrokerType_Memory);

				fPublisher = new MemoryQueuePublisher(q, mmb);
				fMetaBroker = mmb;
				fConsumerFactory = new MemoryConsumerFactory(q);
			} else {
				throw new IllegalArgumentException(
						"Unrecognized type for " + CambriaConstants.kBrokerType + ": " + type + ".");
			}
			
			fIpBlackList = new Blacklist ( getfConfigDb(), getfConfigDb().parse ( "/ipBlacklist" ) );
			this.fEmailer = new Emailer();
			
			log.info("Broker Type is:" + type);

		} catch (SecurityException e) {
			throw new ServletException(e);
		}
	}

	/**
	 * method returns metaBroker
	 * 
	 * @return
	 */
	public Broker getfMetaBroker() {
		return fMetaBroker;
	}

	/**
	 * method to set the metaBroker
	 * 
	 * @param fMetaBroker
	 */
	public void setfMetaBroker(Broker fMetaBroker) {
		this.fMetaBroker = fMetaBroker;
	}

	/**
	 * method to get ConsumerFactory Object
	 * 
	 * @return
	 */
	public ConsumerFactory getfConsumerFactory() {
		return fConsumerFactory;
	}

	/**
	 * method to set the consumerfactory object
	 * 
	 * @param fConsumerFactory
	 */
	public void setfConsumerFactory(ConsumerFactory fConsumerFactory) {
		this.fConsumerFactory = fConsumerFactory;
	}

	/**
	 * method to get Publisher object
	 * 
	 * @return
	 */
	public Publisher getfPublisher() {
		return fPublisher;
	}

	/**
	 * method to set Publisher object
	 * 
	 * @param fPublisher
	 */
	public void setfPublisher(Publisher fPublisher) {
		this.fPublisher = fPublisher;
	}

	/**
	 * method to get MetricsSet Object
	 * 
	 * @return
	 */
	public MetricsSet getfMetrics() {
		return fMetrics;
	}

	/**
	 * method to set MetricsSet Object
	 * 
	 * @param fMetrics
	 */
	public void setfMetrics(MetricsSet fMetrics) {
		this.fMetrics = fMetrics;
	}

	/**
	 * method to get DMaaPCambriaLimiter object
	 * 
	 * @return
	 */
	public DMaaPCambriaLimiter getfRateLimiter() {
		return fRateLimiter;
	}

	/**
	 * method to set DMaaPCambriaLimiter object
	 * 
	 * @param fRateLimiter
	 */
	public void setfRateLimiter(DMaaPCambriaLimiter fRateLimiter) {
		this.fRateLimiter = fRateLimiter;
	}

	/**
	 * Method to get DMaaPAuthenticator object
	 * 
	 * @return
	 */
	public DMaaPAuthenticator<NsaSimpleApiKey> getfSecurityManager() {
		return fSecurityManager;
	}

	/**
	 * method to set DMaaPAuthenticator object
	 * 
	 * @param fSecurityManager
	 */
	public void setfSecurityManager(DMaaPAuthenticator<NsaSimpleApiKey> fSecurityManager) {
		this.fSecurityManager = fSecurityManager;
	}

	/**
	 * method to get rrNvReadable object
	 * 
	 * @return
	 */
	/*public rrNvReadable getSettings() {
		return settings;
	}*/

	/**
	 * method to set rrNvReadable object
	 * 
	 * @param settings
	 */
	/*public void setSettings(rrNvReadable settings) {
		this.settings = settings;
	}*/

	/**
	 * method to get CuratorFramework object
	 * 
	 * @return
	 */
	public static CuratorFramework getCurator() {
		return curator;
	}

	/**
	 * method to set CuratorFramework object
	 * 
	 * @param curator
	 */
	public static void setCurator(CuratorFramework curator) {
		ConfigurationReader.curator = curator;
	}

	/**
	 * method to get ZkClient object
	 * 
	 * @return
	 */
	public ZkClient getZk() {
		return zk;
	}

	/**
	 * method to set ZkClient object
	 * 
	 * @param zk
	 */
	public void setZk(ZkClient zk) {
		this.zk = zk;
	}

	/**
	 * method to get DMaaPZkConfigDb object
	 * 
	 * @return
	 */
	public DMaaPZkConfigDb getfConfigDb() {
		return fConfigDb;
	}

	/**
	 * method to set DMaaPZkConfigDb object
	 * 
	 * @param fConfigDb
	 */
	public void setfConfigDb(DMaaPZkConfigDb fConfigDb) {
		this.fConfigDb = fConfigDb;
	}

	/**
	 * method to get MemoryQueue object
	 * 
	 * @return
	 */
	public MemoryQueue getQ() {
		return q;
	}

	/**
	 * method to set MemoryQueue object
	 * 
	 * @param q
	 */
	public void setQ(MemoryQueue q) {
		this.q = q;
	}

	/**
	 * method to get MemoryMetaBroker object
	 * 
	 * @return
	 */
	public MemoryMetaBroker getMmb() {
		return mmb;
	}

	/**
	 * method to set MemoryMetaBroker object
	 * 
	 * @param mmb
	 */
	public void setMmb(MemoryMetaBroker mmb) {
		this.mmb = mmb;
	}

	/**
	 * method to get NsaApiDb object
	 * 
	 * @return
	 */
	public NsaApiDb<NsaSimpleApiKey> getfApiKeyDb() {
		return fApiKeyDb;
	}

	/**
	 * method to set NsaApiDb object
	 * 
	 * @param fApiKeyDb
	 */
	public void setfApiKeyDb(NsaApiDb<NsaSimpleApiKey> fApiKeyDb) {
		this.fApiKeyDb = fApiKeyDb;
	}

	/*
	 * public DMaaPTransactionObjDB<DMaaPTransactionObj> getfTranDb() { return
	 * fTranDb; }
	 * 
	 * public void setfTranDb(DMaaPTransactionObjDB<DMaaPTransactionObj>
	 * fTranDb) { this.fTranDb = fTranDb; }
	 */
	/**
	 * method to get the zookeeper connection String
	 * 
	 * @param settings
	 * @return
	 */
	public static String getMainZookeeperConnectionString() {
		//return settings.getString(CambriaConstants.kSetting_ZkConfigDbServers,			CambriaConstants.kDefault_ZkConfigDbServers);
		
		 String typeVal = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,CambriaConstants.kSetting_ZkConfigDbServers);
		 if (typeVal==null) typeVal=CambriaConstants.kDefault_ZkConfigDbServers;
		 
		 return typeVal;
	}

	public static String getMainZookeeperConnectionSRoot(){
		String strVal=com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,CambriaConstants.kSetting_ZkConfigDbRoot);
	
		if (null==strVal)
			strVal=CambriaConstants.kDefault_ZkConfigDbRoot;
	
		return strVal;
	}
	
	public Blacklist getfIpBlackList() {
		return fIpBlackList;
	}

	public void setfIpBlackList(Blacklist fIpBlackList) {
		this.fIpBlackList = fIpBlackList;
	}

	public NsaAuthenticatorService<NsaSimpleApiKey> getNsaSecurityManager() {
		return nsaSecurityManager;
	}

	public void setNsaSecurityManager(NsaAuthenticatorService<NsaSimpleApiKey> nsaSecurityManager) {
		this.nsaSecurityManager = nsaSecurityManager;
	}
	
	public Emailer getSystemEmailer()
	  {
	    return this.fEmailer;
	  }


}
