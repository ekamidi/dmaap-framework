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
package com.att.nsa.mr.dme.client;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.aft.dme2.api.util.DME2ExchangeFaultContext;
import com.att.aft.dme2.api.util.DME2ExchangeReplyHandler;
import com.att.aft.dme2.api.util.DME2ExchangeResponseContext;
import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.impl.MRSimplerBatchPublisher;

public class PreferredRouteReplyHandler implements DME2ExchangeReplyHandler {
	private Logger fLog = LoggerFactory.getLogger ( this.getClass().getName () );
	@Override public void handleReply(DME2ExchangeResponseContext responseData) {
		
		if(responseData != null) {
			MRClientFactory.DME2HeadersMap=responseData.getResponseHeaders();
			if (responseData.getResponseHeaders().get("transactionId")!=null)
				
			fLog.info("Transaction_Id : " + responseData.getResponseHeaders().get("transactionId"));
			
			if(responseData.getRouteOffer() != null ){
				routeWriter("preferredRouteKey",responseData.getRouteOffer());
				
			}
		}
}
	
	@Override public void handleFault(DME2ExchangeFaultContext responseData) {
		// TODO Auto-generated method stub
 //StaticCache.getInstance().setHandleFaultInvoked(true);
 }
	@Override public void handleEndpointFault(DME2ExchangeFaultContext responseData) {
		// TODO Auto-generated method stub
		//StaticCache.getInstance().setHandleEndpointFaultInvoked(true); 
	}
	public void routeWriter(String routeKey, String routeValue){

		try{
		
			FileWriter	routeWriter=new FileWriter(new File (MRSimplerBatchPublisher.routerFilePath));
			routeWriter.write(routeKey+"="+routeValue);
			routeWriter.close();
		
		}catch(Exception ex){
			fLog.error("Reply Router Error " + ex.toString() );
		}
		
	}
}
