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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.att.nsa.apiServer.metrics.cambria.DMaaPMetricsSender;
import com.att.nsa.cambria.CambriaApiVersionInfo;
import com.att.nsa.cambria.backends.MetricsSet;
import com.att.nsa.drumlin.till.nv.rrNvReadable;
import com.att.nsa.metrics.impl.CdmConstant;
import com.att.nsa.metrics.impl.CdmCounter;
import com.att.nsa.metrics.impl.CdmMetricsRegistryImpl;
import com.att.nsa.metrics.impl.CdmMovingAverage;
import com.att.nsa.metrics.impl.CdmRateTicker;
import com.att.nsa.metrics.impl.CdmSimpleMetric;
import com.att.nsa.metrics.impl.CdmStringConstant;
import com.att.nsa.metrics.impl.CdmTimeSince;

/*@Component("dMaaPMetricsSet")*/
/**
 * Metrics related information
 * 
 * @author author
 *
 */
public class DMaaPMetricsSet extends CdmMetricsRegistryImpl implements MetricsSet {

	private final CdmStringConstant fVersion;
	private final CdmConstant fStartTime;
	private final CdmTimeSince fUpTime;

	private final CdmCounter fRecvTotal;
	private final CdmRateTicker fRecvEpsInstant;
	private final CdmRateTicker fRecvEpsShort;
	private final CdmRateTicker fRecvEpsLong;

	private final CdmCounter fSendTotal;
	private final CdmRateTicker fSendEpsInstant;
	private final CdmRateTicker fSendEpsShort;
	private final CdmRateTicker fSendEpsLong;

	private final CdmCounter fKafkaConsumerCacheMiss;
	private final CdmCounter fKafkaConsumerCacheHit;

	private final CdmCounter fKafkaConsumerClaimed;
	private final CdmCounter fKafkaConsumerTimeout;

	private final CdmSimpleMetric fFanOutRatio;

	private final HashMap<String, CdmRateTicker> fPathUseRates;
	private final HashMap<String, CdmMovingAverage> fPathAvgs;

	private rrNvReadable fSettings;

	private final ScheduledExecutorService fScheduler;

	/**
	 * Constructor initialization
	 * 
	 * @param cs
	 */
	//public DMaaPMetricsSet() {
		public DMaaPMetricsSet(rrNvReadable cs) {
		//fSettings = cs;

		fVersion = new CdmStringConstant("Version " + CambriaApiVersionInfo.getVersion());
		super.putItem("version", fVersion);

		final long startTime = System.currentTimeMillis();
		final Date d = new Date(startTime);
		final String text = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").format(d);
		fStartTime = new CdmConstant(startTime / 1000, "Start Time (epoch); " + text);
		super.putItem("startTime", fStartTime);

		fUpTime = new CdmTimeSince("seconds since start");
		super.putItem("upTime", fUpTime);

		fRecvTotal = new CdmCounter("Total events received since start");
		super.putItem("recvTotalEvents", fRecvTotal);

		fRecvEpsInstant = new CdmRateTicker("recv eps (1 min)", 1, TimeUnit.SECONDS, 1, TimeUnit.MINUTES);
		super.putItem("recvEpsInstant", fRecvEpsInstant);

		fRecvEpsShort = new CdmRateTicker("recv eps (10 mins)", 1, TimeUnit.SECONDS, 10, TimeUnit.MINUTES);
		super.putItem("recvEpsShort", fRecvEpsShort);

		fRecvEpsLong = new CdmRateTicker("recv eps (1 hr)", 1, TimeUnit.SECONDS, 1, TimeUnit.HOURS);
		super.putItem("recvEpsLong", fRecvEpsLong);

		fSendTotal = new CdmCounter("Total events sent since start");
		super.putItem("sendTotalEvents", fSendTotal);

		fSendEpsInstant = new CdmRateTicker("send eps (1 min)", 1, TimeUnit.SECONDS, 1, TimeUnit.MINUTES);
		super.putItem("sendEpsInstant", fSendEpsInstant);

		fSendEpsShort = new CdmRateTicker("send eps (10 mins)", 1, TimeUnit.SECONDS, 10, TimeUnit.MINUTES);
		super.putItem("sendEpsShort", fSendEpsShort);

		fSendEpsLong = new CdmRateTicker("send eps (1 hr)", 1, TimeUnit.SECONDS, 1, TimeUnit.HOURS);
		super.putItem("sendEpsLong", fSendEpsLong);

		fKafkaConsumerCacheMiss = new CdmCounter("Kafka Consumer Cache Misses");
		super.putItem("kafkaConsumerCacheMiss", fKafkaConsumerCacheMiss);

		fKafkaConsumerCacheHit = new CdmCounter("Kafka Consumer Cache Hits");
		super.putItem("kafkaConsumerCacheHit", fKafkaConsumerCacheHit);

		fKafkaConsumerClaimed = new CdmCounter("Kafka Consumers Claimed");
		super.putItem("kafkaConsumerClaims", fKafkaConsumerClaimed);

		fKafkaConsumerTimeout = new CdmCounter("Kafka Consumers Timedout");
		super.putItem("kafkaConsumerTimeouts", fKafkaConsumerTimeout);

		// FIXME: CdmLevel is not exactly a great choice
		fFanOutRatio = new CdmSimpleMetric() {
			@Override
			public String getRawValueString() {
				return getRawValue().toString();
			}

			@Override
			public Number getRawValue() {
				final double s = fSendTotal.getValue();
				final double r = fRecvTotal.getValue();
				return r == 0.0 ? 0.0 : s / r;
			}

			@Override
			public String summarize() {
				return getRawValueString() + " sends per recv";
			}

		};
		super.putItem("fanOut", fFanOutRatio);

		// these are added to the metrics catalog as they're discovered
		fPathUseRates = new HashMap<String, CdmRateTicker>();
		fPathAvgs = new HashMap<String, CdmMovingAverage>();

		fScheduler = Executors.newScheduledThreadPool(1);
	}

	@Override
	public void setupCambriaSender() {
		DMaaPMetricsSender.sendPeriodically(fScheduler, this,  "cambria.apinode.metrics.dmaap");
	}

	@Override
	public void onRouteComplete(String name, long durationMs) {
		CdmRateTicker ticker = fPathUseRates.get(name);
		if (ticker == null) {
			ticker = new CdmRateTicker("calls/min on path " + name + "", 1, TimeUnit.MINUTES, 1, TimeUnit.HOURS);
			fPathUseRates.put(name, ticker);
			super.putItem("pathUse_" + name, ticker);
		}
		ticker.tick();

		CdmMovingAverage durs = fPathAvgs.get(name);
		if (durs == null) {
			durs = new CdmMovingAverage("ms avg duration on path " + name + ", last 10 minutes", 10, TimeUnit.MINUTES);
			fPathAvgs.put(name, durs);
			super.putItem("pathDurationMs_" + name, durs);
		}
		durs.tick(durationMs);
	}

	@Override
	public void publishTick(int amount) {
		if (amount > 0) {
			fRecvTotal.bumpBy(amount);
			fRecvEpsInstant.tick(amount);
			fRecvEpsShort.tick(amount);
			fRecvEpsLong.tick(amount);
		}
	}

	@Override
	public void consumeTick(int amount) {
		if (amount > 0) {
			fSendTotal.bumpBy(amount);
			fSendEpsInstant.tick(amount);
			fSendEpsShort.tick(amount);
			fSendEpsLong.tick(amount);
		}
	}

	@Override
	public void onKafkaConsumerCacheMiss() {
		fKafkaConsumerCacheMiss.bump();
	}

	@Override
	public void onKafkaConsumerCacheHit() {
		fKafkaConsumerCacheHit.bump();
	}

	@Override
	public void onKafkaConsumerClaimed() {
		fKafkaConsumerClaimed.bump();
	}

	@Override
	public void onKafkaConsumerTimeout() {
		fKafkaConsumerTimeout.bump();
	}

}
