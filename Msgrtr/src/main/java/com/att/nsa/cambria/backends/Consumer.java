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
package com.att.nsa.cambria.backends;

/**
 * A consumer interface. Consumers pull the next message from a given topic.
 * @author author
 */
public interface Consumer
{	
	/**
	 * A message interface provide the offset and message
	 * @author author
	 *
	 */
	public interface Message
	{	
		/**
		 * returning the offset of that particular message 
		 * @return long
		 */
		long getOffset ();
		/**
		 * returning the message 
		 * @return message
		 */
		String getMessage ();
	}

	/**
	 * Get this consumer's name
	 * @return name
	 */
	String getName ();

	/**
	 * Get creation time in ms
	 * @return
	 */
	long getCreateTimeMs ();

	/**
	 * Get last access time in ms
	 * @return
	 */
	long getLastAccessMs ();
	
	/**
	 * Get the next message from this source. This method must not block.
	 * @return the next message, or null if none are waiting
	 */
	Message nextMessage ();

	/**
	 * Get the next message from this source. This method must not block.
	 * @param atOffset start with the next message at or after atOffset. -1 means next from last request
	 * @return the next message, or null if none are waiting
	 */
//	Message nextMessage ( long atOffset );

	/**
	 * Close/clean up this consumer
	 */
	void close();
	
	/**
	 * Commit the offset of the last consumed message
	 * 
	 */
	void commitOffsets();
	
	/**
	 * Get the offset this consumer is currently at
	 * @return offset
	 */
	long getOffset();
}
