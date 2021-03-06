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
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.backends.Consumer;
import com.att.nsa.cambria.backends.Consumer.Message;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.cambria.metabroker.Topic;
import com.att.nsa.cambria.utils.DMaaPResponseBuilder.StreamWriter;
import com.att.nsa.cambria.utils.Utils;


/**
 * class used to write the consumed messages
 * 
 * @author author
 *
 */
public class CambriaOutboundEventStream implements StreamWriter {
	private static final int kTopLimit = 1024 * 4;

	/**
	 * 
	 * static innerclass it takes all the input parameter for kafka consumer
	 * like limit, timeout, meta, pretty
	 * 
	 * @author author
	 *
	 */
	public static class Builder {

		// Required
		private final Consumer fConsumer;
		//private final rrNvReadable fSettings;   // used during write to tweak
												// format, decide to explicitly
												// close stream or not

		// Optional
		private int fLimit;
		private int fTimeoutMs;
		private String fTopicFilter;
		private boolean fPretty;
		private boolean fWithMeta;

		// private int fOffset;
		/**
		 * constructor it initializes all the consumer parameters
		 * 
		 * @param c
		 * @param settings
		 */
		public Builder(Consumer c) {
			this.fConsumer = c;
			//this.fSettings = settings;

			fLimit = CambriaConstants.kNoTimeout;
			fTimeoutMs = CambriaConstants.kNoLimit;
			fTopicFilter = CambriaConstants.kNoFilter;
			fPretty = false;
			fWithMeta = false;
			// fOffset = CambriaEvents.kNextOffset;
		}

		/**
		 * 
		 * constructor initializes with limit
		 * 
		 * @param l
		 *            only l no of messages will be consumed
		 * @return
		 */
		public Builder limit(int l) {
			this.fLimit = l;
			return this;
		}

		/**
		 * constructor initializes with timeout
		 * 
		 * @param t
		 *            if there is no message to consume, them DMaaP will wait
		 *            for t time
		 * @return
		 */
		public Builder timeout(int t) {
			this.fTimeoutMs = t;
			return this;
		}

		/**
		 * constructor initializes with filter
		 * 
		 * @param f
		 *            filter
		 * @return
		 */
		public Builder filter(String f) {
			this.fTopicFilter = f;
			return this;
		}

		/**
		 * constructor initializes with boolean value pretty
		 * 
		 * @param p
		 *            messages print in new line
		 * @return
		 */
		public Builder pretty(boolean p) {
			fPretty = p;
			return this;
		}

		/**
		 * constructor initializes with boolean value meta
		 * 
		 * @param withMeta,
		 *            along with messages offset will print
		 * @return
		 */
		public Builder withMeta(boolean withMeta) {
			fWithMeta = withMeta;
			return this;
		}

		// public Builder atOffset ( int pos )
		// {
		// fOffset = pos;
		// return this;
		// }
		/**
		 * method returs object of CambriaOutboundEventStream
		 * 
		 * @return
		 * @throws CambriaApiException
		 */
		public CambriaOutboundEventStream build() throws CambriaApiException {
			return new CambriaOutboundEventStream(this);
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param builder
	 * @throws CambriaApiException
	 * 
	 */
	private CambriaOutboundEventStream(Builder builder) throws CambriaApiException {
		fConsumer = builder.fConsumer;
		fLimit = builder.fLimit;
		fTimeoutMs = builder.fTimeoutMs;
		//fSettings = builder.fSettings;
		fSent = 0;
		fPretty = builder.fPretty;
		fWithMeta = builder.fWithMeta;
		
//		if (CambriaConstants.kNoFilter.equals(builder.fTopicFilter)) {
//			fHpAlarmFilter = null;
//			fHppe = null;
//		} else {
//			try {
//				final JSONObject filter = new JSONObject(new JSONTokener(builder.fTopicFilter));
//				HpConfigContext<HpEvent> cc = new HpConfigContext<HpEvent>();
//				fHpAlarmFilter = cc.create(HpAlarmFilter.class, filter);
//				final EventFactory<HpJsonEvent> ef = new HpJsonEventFactory();
//				fHppe = new HpProcessingEngine<HpJsonEvent>(ef);
//			} catch (HpReaderException e) {
//				// JSON was okay, but the filter engine says it's bogus
//				throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST,
//						"Couldn't create filter: " + e.getMessage());
//			} catch (JSONException e) {
//				// user sent a bogus JSON object
//				throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST,
//						"Couldn't parse JSON: " + e.getMessage());
//			}
//		}
	}

	/**
	 * 
	 * interface provides onWait and onMessage methods
	 *
	 */
	public interface operation {
		/**
		 * Call thread.sleep
		 * @throws IOException
		 */
		void onWait() throws IOException;
/**
 * provides the output based in the consumer paramter
 * @param count
 * @param msg
 * @throws IOException
 */
		void onMessage(int count, Message msg) throws IOException;
	}

	/**
	 * 
	 * @return
	 */
	public int getSentCount() {
		return fSent;
	}

	@Override
	/**
	 * 
	 * @param os
	 * throws IOException
	 */
	public void write(final OutputStream os) throws IOException {
		//final boolean transactionEnabled = topic.isTransactionEnabled();
		//final boolean transactionEnabled = isTransEnabled();
		final boolean transactionEnabled = istransEnable;
		os.write('[');

		fSent = forEachMessage(new operation() {
			@Override
			public void onMessage(int count, Message msg) throws IOException, JSONException {

				String message = "";
				JSONObject jsonMessage = null;
				if (transactionEnabled) {
					jsonMessage = new JSONObject(msg.getMessage());
					message = jsonMessage.getString("message");
				}

				if (count > 0) {
					os.write(',');
				}

				if (fWithMeta) {
					final JSONObject entry = new JSONObject();
					entry.put("offset", msg.getOffset());
					entry.put("message", message);
					os.write(entry.toString().getBytes());
				} else {
					//os.write(message.getBytes());
					 String jsonString = "";
					if(transactionEnabled){
						jsonString= JSONObject.valueToString(message);
					}else{
						jsonString = JSONObject.valueToString (msg.getMessage());
						}
				 	os.write ( jsonString.getBytes () );
				}

				if (fPretty) {
					os.write('\n');
				}

				
				String metricTopicname= com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"metrics.send.cambria.topic");
				if (null==metricTopicname)
           		  metricTopicname="msgrtr.apinode.metrics.dmaap";
           	 
           	 if (!metricTopicname.equalsIgnoreCase(topic.getName())) {
				if (transactionEnabled) {
					final String transactionId = jsonMessage.getString("transactionId");
					responseTransactionId = transactionId;

					StringBuilder consumerInfo = new StringBuilder();
					if (null != dmaapContext && null != dmaapContext.getRequest()) {
						final HttpServletRequest request = dmaapContext.getRequest();
						consumerInfo.append("consumerIp= \"" + request.getRemoteHost() + "\",");
						consumerInfo.append("consServerIp= \"" + request.getLocalAddr() + "\",");
						consumerInfo.append("consumerId= \"" + Utils.getUserApiKey(request) + "\",");
						consumerInfo.append(
								"consumerGroup= \"" + getConsumerGroupFromRequest(request.getRequestURI()) + "\",");
						consumerInfo.append("consumeTime= \"" + Utils.getFormattedDate(new Date()) + "\",");
					}

					log.info("Consumer [" + consumerInfo.toString() + "transactionId= \"" + transactionId
							+ "\",messageLength= \"" + message.length() + "\",topic= \"" + topic.getName() + "\"]");
				}
           	 }

			}

			@Override
			/**
			 * 
			 * It makes thread to wait
			 * @throws IOException
			 */
			public void onWait() throws IOException {
				os.flush(); // likely totally unnecessary for a network socket
				try {
					// FIXME: would be good to wait/signal
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		});

		//if (null != dmaapContext && isTransactionEnabled()) {
			if (null != dmaapContext && istransEnable) {
			
			dmaapContext.getResponse().setHeader("transactionId",
					Utils.getResponseTransactionId(responseTransactionId));
		}

		os.write(']');
		os.flush();

		boolean close_out_stream = true;
		String strclose_out_stream = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"close.output.stream");
		if(null!=strclose_out_stream)close_out_stream=Boolean.parseBoolean(strclose_out_stream);
		
		//if (fSettings.getBoolean("close.output.stream", true)) {
				if (close_out_stream) {
			os.close();
		}
	}

	/**
	 * 
	 * @param requestURI
	 * @return
	 */
	private String getConsumerGroupFromRequest(String requestURI) {
		if (null != requestURI && !requestURI.isEmpty()) {

			String consumerDetails = requestURI.substring(requestURI.indexOf("events/") + 7);

			int startIndex = consumerDetails.indexOf("/") + 1;
			int endIndex = consumerDetails.lastIndexOf("/");
			return consumerDetails.substring(startIndex, endIndex);
		}
		return null;
	}
/**
 * 
 * @param op
 * @return
 * @throws IOException
 * @throws JSONException 
 */
	public int forEachMessage(operation op) throws IOException, JSONException {
		final int effectiveLimit = (fLimit == 0 ? kTopLimit : fLimit);

		int count = 0;
		boolean firstPing = true;

		final long startMs = System.currentTimeMillis();
		final long timeoutMs = fTimeoutMs + startMs;

		while (firstPing || (count == 0 && System.currentTimeMillis() < timeoutMs)) {
			if (!firstPing) {
				op.onWait();
			}
			firstPing = false;

			Consumer.Message msg = null;
			while (count < effectiveLimit && (msg = fConsumer.nextMessage()) != null) {

				
				String message = "";
			//	if (topic.isTransactionEnabled() || true) {
				if (istransEnable) {
					// As part of DMaaP changes we are wrapping the original
					// message into a json object
					// and then this json object is further wrapped into message
					// object before publishing,
					// so extracting the original message from the message
					// object for matching with filter.
					final JSONObject jsonMessage = new JSONObject(msg.getMessage());
					message = jsonMessage.getString("message");
				} else {
					message = msg.getMessage();
				}

				// If filters are enabled/set, message should be in JSON format
				// for filters to work for
				// otherwise filter will automatically ignore message in
				// non-json format.
				if (filterMatches(message)) {
					op.onMessage(count, msg);
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * 
	 * Checks whether filter is initialized
	 */
//	private boolean isFilterInitialized() {
//		return (fHpAlarmFilter != null && fHppe != null);
//	}

	/**
	 * 
	 * @param msg
	 * @return
	 */
	private boolean filterMatches(String msg) {
		boolean result = true;
//		if (isFilterInitialized()) {
//			try {
//				final HpJsonEvent e = new HpJsonEvent("e", new JSONObject(msg));
//				result = fHpAlarmFilter.matches(fHppe, e);
//			} catch (JSONException x) {
//				// the msg may not be JSON
//				result = false;
//				log.error("Failed due to " + x.getMessage());
//			} catch (Exception x) {
//				log.error("Error using filter: " + x.getMessage(), x);
//			}
//		}

		return result;
	}

	public DMaaPContext getDmaapContext() {
		return dmaapContext;
	}

	public void setDmaapContext(DMaaPContext dmaapContext) {
		this.dmaapContext = dmaapContext;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}
	
	public void setTopicStyle(boolean aaftopic) {
		this.isAAFTopic = aaftopic;
	}
	
	public void setTransEnabled ( boolean transEnable) {
		this.istransEnable = transEnable;
	}

	/*private boolean isTransactionEnabled() {
		//return topic.isTransactionEnabled();
		return true; // let metrics creates for all the topics
	}*/

	private boolean isTransEnabled() {
		String istransidUEBtopicreqd = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"transidUEBtopicreqd");
		boolean istransidreqd=false;
		if ((null != istransidUEBtopicreqd && istransidUEBtopicreqd.equalsIgnoreCase("true")) || isAAFTopic){
			istransidreqd = true; 
		}
		
		return istransidreqd;

	}
	
	private final Consumer fConsumer;
	private final int fLimit;
	private final int fTimeoutMs;
	//private final rrNvReadable fSettings;
	private final boolean fPretty;
	private final boolean fWithMeta;
	private int fSent;
//	private final HpAlarmFilter<HpJsonEvent> fHpAlarmFilter;
//	private final HpProcessingEngine<HpJsonEvent> fHppe;
	private DMaaPContext dmaapContext;
	private String responseTransactionId;
	private Topic topic;
	private boolean isAAFTopic = false;
	private boolean istransEnable = false;
	

	//private static final Logger log = Logger.getLogger(CambriaOutboundEventStream.class);
	
	private static final EELFLogger log = EELFManager.getInstance().getLogger(CambriaOutboundEventStream.class);
}