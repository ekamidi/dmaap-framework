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

import java.util.Set;
import java.util.TreeSet;

import com.att.nsa.cambria.transaction.DMaaPTransactionFactory;
import com.att.nsa.cambria.transaction.DMaaPTransactionObj;
import com.att.nsa.cambria.transaction.DMaaPTransactionObjDB;
import com.att.nsa.cambria.transaction.TransactionObj;
import com.att.nsa.configs.ConfigDb;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.configs.ConfigPath;

/**
 * Persistent storage for Transaction objects built over an abstract config db.
 * 
 * @author author
 *
 * @param <K>
 */
public class BaseTransactionDbImpl<K extends DMaaPTransactionObj> implements DMaaPTransactionObjDB<K> {

	private final ConfigDb fDb;
	private final ConfigPath fBasePath;
	private final DMaaPTransactionFactory<K> fKeyFactory;

	private static final String kStdRootPath = "/transaction";

	private ConfigPath makePath(String transactionId) {
		return fBasePath.getChild(transactionId);
	}

	/**
	 * Construct an Transaction db over the given config db at the standard
	 * location
	 * 
	 * @param db
	 * @param keyFactory
	 * @throws ConfigDbException
	 */
	public BaseTransactionDbImpl(ConfigDb db, DMaaPTransactionFactory<K> keyFactory) throws ConfigDbException {
		this(db, kStdRootPath, keyFactory);
	}

	/**
	 * Construct an Transaction db over the given config db using the given root
	 * location
	 * 
	 * @param db
	 * @param rootPath
	 * @param keyFactory
	 * @throws ConfigDbException
	 */
	public BaseTransactionDbImpl(ConfigDb db, String rootPath, DMaaPTransactionFactory<K> keyFactory)
			throws ConfigDbException {
		fDb = db;
		fBasePath = db.parse(rootPath);
		fKeyFactory = keyFactory;

		if (!db.exists(fBasePath)) {
			db.store(fBasePath, "");
		}
	}

	/**
	 * Create a new Transaction Obj. If one exists,
	 * 
	 * @param id
	 * @return the new Transaction record
	 * @throws ConfigDbException
	 */
	public synchronized K createTransactionObj(String id) throws KeyExistsException, ConfigDbException {
		final ConfigPath path = makePath(id);
		if (fDb.exists(path)) {
			throw new KeyExistsException(id);
		}

		// make one, store it, return it
		final K newKey = fKeyFactory.makeNewTransactionId(id);
		fDb.store(path, newKey.serialize());
		return newKey;
	}

	/**
	 * Save an Transaction record. This must be used after changing auxiliary
	 * data on the record. Note that the transaction object must exist (via
	 * createTransactionObj).
	 * 
	 * @param transaction
	 *            object
	 * @throws ConfigDbException
	 */
	@Override
	public synchronized void saveTransactionObj(K trnObj) throws ConfigDbException {
		final ConfigPath path = makePath(trnObj.getId());
		if (!fDb.exists(path) || !(trnObj instanceof TransactionObj)) {
			throw new IllegalStateException(trnObj.getId() + " is not known to this database");
		}
		fDb.store(path, ((TransactionObj) trnObj).serialize());
	}

	/**
	 * Load an Transaction record based on the Transaction Id value
	 * 
	 * @param transactionId
	 * @return an Transaction Object record or null
	 * @throws ConfigDbException
	 */
	@Override
	public synchronized K loadTransactionObj(String transactionId) throws ConfigDbException {
		final String data = fDb.load(makePath(transactionId));
		if (data != null) {
			return fKeyFactory.makeNewTransactionObj(data);
		}
		return null;
	}

	/**
	 * Load all transactions known to this database. (This could be expensive.)
	 * 
	 * @return a set of all Transaction objects
	 * @throws ConfigDbException
	 */
	public synchronized Set<String> loadAllTransactionObjs() throws ConfigDbException {
		final TreeSet<String> result = new TreeSet<String>();
		for (ConfigPath cp : fDb.loadChildrenNames(fBasePath)) {
			result.add(cp.getName());
		}
		return result;
	}

}
