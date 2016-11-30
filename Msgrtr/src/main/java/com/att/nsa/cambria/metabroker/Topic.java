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
package com.att.nsa.cambria.metabroker;

import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.NsaAcl;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.ReadWriteSecuredResource;
/**
 * This is the interface for topic and all the topic related operations
 * get topic name, owner, description, transactionEnabled etc.
 * @author author
 *
 */
public interface Topic extends ReadWriteSecuredResource
{	
	/**
	 * User defined exception for access denied while access the topic for Publisher and consumer
	 * @author author
	 *
	 *//*
	public class AccessDeniedException extends Exception
	{	
		*//**
		 * AccessDenied Description
		 *//*
		public AccessDeniedException () { super ( "Access denied." ); } 
		*//**
		 * AccessDenied Exception for the user while authenticating the user request
		 * @param user
		 *//*
		public AccessDeniedException ( String user ) { super ( "Access denied for " + user ); } 
		private static final long serialVersionUID = 1L;
	}*/

	/**
	 * Get this topic's name
	 * @return
	 */
	String getName ();

	/**
	 * Get the API key of the owner of this topic.
	 * @return
	 */
	String getOwner ();

	/**
	 * Get a description of the topic, as set by the owner at creation time.
	 * @return
	 */
	String getDescription ();
	
	/**
	 * If the topic is transaction enabled
	 * @return boolean true/false
	 */
	boolean isTransactionEnabled();
	
	/**
	 * Get the ACL for reading on this topic. Can be null.
	 * @return
	 */
	NsaAcl getReaderAcl ();

	/**
	 * Get the ACL for writing on this topic.  Can be null.
	 * @return
	 */
	NsaAcl getWriterAcl ();

	/**
	 * Check if this user can read the topic. Throw otherwise. Note that
	 * user may be null.
	 * @param user
	 */
	void checkUserRead ( NsaApiKey user ) throws AccessDeniedException;

	/**
	 * Check if this user can write to the topic. Throw otherwise. Note
	 * that user may be null.
	 * @param user
	 */
	void checkUserWrite ( NsaApiKey user ) throws AccessDeniedException;

	/**
	 * allow the given user to publish
	 * @param publisherId
	 * @param asUser
	 */
	void permitWritesFromUser ( String publisherId, NsaApiKey asUser ) throws AccessDeniedException, ConfigDbException;

	/**
	 * deny the given user from publishing
	 * @param publisherId
	 * @param asUser
	 */
	void denyWritesFromUser ( String publisherId, NsaApiKey asUser ) throws AccessDeniedException, ConfigDbException;

	/**
	 * allow the given user to read the topic
	 * @param consumerId
	 * @param asUser
	 */
	void permitReadsByUser ( String consumerId, NsaApiKey asUser ) throws AccessDeniedException, ConfigDbException;

	/**
	 * deny the given user from reading the topic
	 * @param consumerId
	 * @param asUser
	 * @throws ConfigDbException 
	 */
	void denyReadsByUser ( String consumerId, NsaApiKey asUser ) throws AccessDeniedException, ConfigDbException;
}
