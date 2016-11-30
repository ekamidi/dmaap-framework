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
package com.att.nsa.cambria.transaction;

import java.util.Set;

import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.NsaSecurityManagerException;


/**
 * Persistent storage for Transaction Object and secrets built over an abstract config db. Instances
 * of this DB must support concurrent access.
 * @author author
 *
 * @param <K> DMaaPTransactionObj
 */
public interface DMaaPTransactionObjDB <K extends DMaaPTransactionObj> {


	/**
	 * Create a new Transaction Object. If one exists, 
	 * @param id
	 * @return the new Transaction record
	 * @throws ConfigDbException 
	 */
	K createTransactionObj (String id) throws KeyExistsException, ConfigDbException;


	/**
	 * An exception to signal a Transaction object already exists 
	 * @author author
	 *
	 */
	public static class KeyExistsException extends NsaSecurityManagerException
	{
		/**
		 * If the key exists
		 * @param key
		 */
		public KeyExistsException ( String key ) { super ( "Transaction Object " + key + " exists" ); }
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Save a Transaction Object record. This must be used after changing auxiliary data on the record.
	 * Note that the transaction must exist (via createTransactionObj). 
	 * @param transactionObj
	 * @throws ConfigDbException 
	 */
	void saveTransactionObj ( K transactionObj ) throws ConfigDbException;
	
	/**
	 * Load an Transaction Object record based on the Transaction ID value
	 * @param transactionId
	 * @return a transaction record or null
	 * @throws ConfigDbException 
	 */
	K loadTransactionObj ( String transactionId ) throws ConfigDbException;
	
	/**
	 * Load all Transaction objects.
	 * @return
	 * @throws ConfigDbException
	 */
	Set<String> loadAllTransactionObjs () throws ConfigDbException;
}