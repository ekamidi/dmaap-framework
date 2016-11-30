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
package com.att.nsa.apiServer.metrics.cambria;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.cambria.metrics.publisher.CambriaPublisher;
import com.att.nsa.cambria.metrics.publisher.DMaaPCambriaClientFactory;
import com.att.nsa.drumlin.till.nv.rrNvReadable;
import com.att.nsa.metrics.CdmMetricsRegistry;
import com.att.nsa.metrics.impl.CdmConstant;

/**
 * MetricsSender will send the given metrics registry content as an event on the
 * Cambria event broker to the given topic.
 * 
 * @author author
 *
 */
public class DMaaPMetricsSender implements Runnable {
	public static final String kSetting_CambriaEnabled = "metrics.send.cambria.enabled";
	public static final String kSetting_CambriaBaseUrl = "metrics.send.cambria.baseUrl";
	public static final String kSetting_CambriaTopic = "metrics.send.cambria.topic";
	public static final String kSetting_CambriaSendFreqSecs = "metrics.send.cambria.sendEverySeconds";

	/**
	 * Schedule a periodic send of the given metrics registry using the given
	 * settings container for the Cambria location, topic, and send frequency.
	 * <br/>
	 * <br/>
	 * If the enabled flag is false, this method returns null.
	 * 
	 * @param scheduler
	 * @param metrics
	 * @param settings
	 * @param defaultTopic
	 * @return a handle to the scheduled task
	 */
	public static ScheduledFuture<?> sendPeriodically(ScheduledExecutorService scheduler, CdmMetricsRegistry metrics,
			 String defaultTopic) {
		log.info("Inside : DMaaPMetricsSender : sendPeriodically");
	String	cambriaSetting= com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,kSetting_CambriaEnabled);
	boolean setEnable=true;
	if (cambriaSetting!=null && cambriaSetting.equals("false") )
	setEnable= false;
	//System.out.println(setEnable+"XXXXXXXXXXXXXXXX");
		if (setEnable) {
			String Setting_CambriaBaseUrl=com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,kSetting_CambriaEnabled);
			
			Setting_CambriaBaseUrl=Setting_CambriaBaseUrl==null?"localhost":Setting_CambriaBaseUrl;
			
			String Setting_CambriaTopic=com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,kSetting_CambriaTopic);
			if(Setting_CambriaTopic==null) Setting_CambriaTopic = "msgrtr.apinode.metrics.dmaap";     
			
	//		Setting_CambriaBaseUrl=Setting_CambriaBaseUrl==null?defaultTopic:Setting_CambriaBaseUrl;
			
			String Setting_CambriaSendFreqSecs=com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,kSetting_CambriaSendFreqSecs);
			
			int _CambriaSendFreqSecs =30;
			if(Setting_CambriaSendFreqSecs!=null){
				 _CambriaSendFreqSecs = Integer.parseInt(Setting_CambriaSendFreqSecs);
			}
			

			return DMaaPMetricsSender.sendPeriodically(scheduler, metrics,
					Setting_CambriaBaseUrl,Setting_CambriaTopic,_CambriaSendFreqSecs
				);
			/*return DMaaPMetricsSender.sendPeriodically(scheduler, metrics,
					settings.getString(kSetting_CambriaBaseUrl, "localhost"),
					settings.getString(kSetting_CambriaTopic, defaultTopic),
					settings.getInt(kSetting_CambriaSendFreqSecs, 30));*/
		} else {
			return null;
		}
	}

	/**
	 * Schedule a periodic send of the metrics registry to the given Cambria
	 * broker and topic.
	 * 
	 * @param scheduler
	 * @param metrics
	 *            the registry to send
	 * @param cambriaBaseUrl
	 *            the base URL for Cambria
	 * @param topic
	 *            the topic to publish on
	 * @param everySeconds
	 *            how frequently to publish
	 * @return a handle to the scheduled task
	 */
	public static ScheduledFuture<?> sendPeriodically(ScheduledExecutorService scheduler, CdmMetricsRegistry metrics,
			String cambriaBaseUrl, String topic, int everySeconds) {
		return scheduler.scheduleAtFixedRate(new DMaaPMetricsSender(metrics, cambriaBaseUrl, topic), everySeconds,
				everySeconds, TimeUnit.SECONDS);
	}

	/**
	 * Create a metrics sender.
	 * 
	 * @param metrics
	 * @param cambriaBaseUrl
	 * @param topic
	 */
	public DMaaPMetricsSender(CdmMetricsRegistry metrics, String cambriaBaseUrl, String topic) {
		try {
			fMetrics = metrics;
			fHostname = InetAddress.getLocalHost().getHostName();

			// setup a "simple" publisher that will send metrics immediately
			fCambria = DMaaPCambriaClientFactory.createSimplePublisher(cambriaBaseUrl, topic);
		} catch (UnknownHostException e) {
			log.warn("Unable to get localhost address in MetricsSender constructor.", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Send on demand.
	 */
	public void send() {
		try {
			final JSONObject o = fMetrics.toJson();
			o.put("hostname", fHostname);
			o.put("now", System.currentTimeMillis());
			o.put("metricsSendTime", addTimeStamp());
			o.put("transactionEnabled", false);
			fCambria.send(fHostname, o.toString());
		} catch (JSONException e) {
			log.warn("Error posting metrics to Cambria: " + e.getMessage());
		} catch (IOException e) {
			log.warn("Error posting metrics to Cambria: " + e.getMessage());
		}
	}

	/**
	 * Run() calls send(). It's meant for use in a background-scheduled task.
	 */
	@Override
	public void run() {
		send();
	}

	private final CdmMetricsRegistry fMetrics;
	private final CambriaPublisher fCambria;
	private final String fHostname;

	//private static final Logger log = LoggerFactory.getLogger(MetricsSender.class);

	private static final EELFLogger log = EELFManager.getInstance().getLogger(MetricsSender.class);
	/**
	 * method creates and returnd CdmConstant object using current timestamp
	 * 
	 * @return
	 */
	public CdmConstant addTimeStamp() {
		// Add the timestamp with every metrics send
		final long metricsSendTime = System.currentTimeMillis();
		final Date d = new Date(metricsSendTime);
		final String text = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").format(d);
		return new CdmConstant(metricsSendTime / 1000, "Metrics Send Time (epoch); " + text);
	}
}
