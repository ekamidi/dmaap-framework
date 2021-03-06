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
package com.att.nsa.cambria.service.impl;

import java.io.IOException;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.backends.MetricsSet;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.service.MetricsService;
import com.att.nsa.cambria.utils.DMaaPResponseBuilder;
import com.att.nsa.metrics.CdmMeasuredItem;

/**
 * 
 * 
 * This will provide all the generated metrics details also it can provide the
 * get metrics details
 * 
 * 
 * @author author
 *
 *
 */
@Component
public class MetricsServiceImpl implements MetricsService {

	//private static final Logger LOG = Logger.getLogger(MetricsService.class.toString());
	private static final EELFLogger LOG = EELFManager.getInstance().getLogger(MetricsService.class);
	/**
	 * 
	 * 
	 * @param ctx
	 * @throws IOException
	 * 
	 * 
	 * get Metric details
	 * 
	 */
	@Override
	
	public void get(DMaaPContext ctx) throws IOException {
		LOG.info("Inside  : MetricsServiceImpl : get()");
		final MetricsSet metrics = ctx.getConfigReader().getfMetrics();
		DMaaPResponseBuilder.setNoCacheHeadings(ctx);
		final JSONObject result = metrics.toJson();
		DMaaPResponseBuilder.respondOk(ctx, result);
		LOG.info("============ Metrics generated : " + result.toString() + "=================");

	}


	@Override
	/**
	 * 
	 * get Metric by name
	 * 
	 * 
	 * @param ctx
	 * @param name
	 * @throws IOException
	 * @throws CambriaApiException
	 * 
	 * 
	 */
	public void getMetricByName(DMaaPContext ctx, String name) throws IOException, CambriaApiException {
		LOG.info("Inside  : MetricsServiceImpl : getMetricByName()");
		final MetricsSet metrics = ctx.getConfigReader().getfMetrics();

		final CdmMeasuredItem item = metrics.getItem(name);
		/**
		 * check if item is null
		 */
		if (item == null) {
			throw new CambriaApiException(404, "No metric named [" + name + "].");
		}

		final JSONObject entry = new JSONObject();
		entry.put("summary", item.summarize());
		entry.put("raw", item.getRawValueString());

		DMaaPResponseBuilder.setNoCacheHeadings(ctx);

		final JSONObject result = new JSONObject();
		result.put(name, entry);

		DMaaPResponseBuilder.respondOk(ctx, result);
		LOG.info("============ Metrics generated : " + entry.toString() + "=================");
	}

}
