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

import org.springframework.stereotype.Service;

import com.att.aft.dme2.internal.jettison.json.JSONException;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.service.TransactionService;
import com.att.nsa.cambria.transaction.TransactionObj;
import com.att.nsa.configs.ConfigDbException;

/**
 * Once the transaction rest gateway will be using that time it will provide all
 * the transaction details like fetching all the transactional objects or get
 * any particular transaction object details
 * 
 * @author author
 *
 */
@Service
public class TransactionServiceImpl implements TransactionService {

	@Override
	public void checkTransaction(TransactionObj trnObj) {
		/* Need to implement the method */
	}

	@Override
	public void getAllTransactionObjs(DMaaPContext dmaapContext)
			throws ConfigDbException, IOException {

		/*
		 * ConfigurationReader configReader = dmaapContext.getConfigReader();
		 * 
		 * LOG.info("configReader : "+configReader.toString());
		 * 
		 * final JSONObject result = new JSONObject (); final JSONArray
		 * transactionIds = new JSONArray (); result.put ( "transactionIds",
		 * transactionIds );
		 * 
		 * DMaaPTransactionObjDB<DMaaPTransactionObj> transDb =
		 * configReader.getfTranDb();
		 * 
		 * for (String transactionId : transDb.loadAllTransactionObjs()) {
		 * transactionIds.put (transactionId); } LOG.info(
		 * "========== TransactionServiceImpl: getAllTransactionObjs: Transaction objects are : "
		 * + transactionIds.toString()+"===========");
		 * DMaaPResponseBuilder.respondOk(dmaapContext, result);
		 */
	}

	@Override
	public void getTransactionObj(DMaaPContext dmaapContext,
			String transactionId) throws ConfigDbException, JSONException,
			IOException {

		/*
		 * if (null != transactionId) {
		 * 
		 * ConfigurationReader configReader = dmaapContext.getConfigReader();
		 * 
		 * DMaaPTransactionObj trnObj;
		 * 
		 * trnObj = configReader.getfTranDb().loadTransactionObj(transactionId);
		 * 
		 * 
		 * if (null != trnObj) { trnObj.serialize(); JSONObject result =
		 * trnObj.asJsonObject(); DMaaPResponseBuilder.respondOk(dmaapContext,
		 * result);
		 * LOG.info("========== TransactionServiceImpl: getTransactionObj : "+
		 * result.toString()+"==========="); return; }
		 * 
		 * } LOG.info(
		 * "========== TransactionServiceImpl: getTransactionObj: Error : Transaction object does not exist. "
		 * +"===========");
		 */
	}
}
