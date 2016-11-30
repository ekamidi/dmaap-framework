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
package com.att.nsa.cambria.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.att.nsa.cambria.beans.DMaaPContext;
/**
 * This is an utility class for various operations for formatting
 * @author author
 *
 */
public class Utils {

	private static final String DATE_FORMAT = "dd-MM-yyyy::hh:mm:ss:SSS";
	public static final String CAMBRIA_AUTH_HEADER = "X-CambriaAuth";
	private static final String BATCH_ID_FORMAT = "000000";

	private Utils() {
		super();
	}

	/**
	 * Formatting the date 
	 * @param date
	 * @return date or null
	 */
	public static String getFormattedDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		if (null != date){
			return sdf.format(date);
		}
		return null;
	}
	/**
	 * to get the details of User Api Key
	 * @param request
	 * @return authkey or null
	 */
	public static String getUserApiKey(HttpServletRequest request) {
		final String auth = request.getHeader(CAMBRIA_AUTH_HEADER);
		if (null != auth) {
			final String[] splittedAuthKey = auth.split(":");
			return splittedAuthKey[0];
		}else if (null!=request.getHeader("Authorization")){
			/**
			 * AAF implementation enhancement
			 */
			 String user= request.getUserPrincipal().getName().toString();
			return user.substring(0, user.lastIndexOf("@"));
		}
		return null;
	}
	/**
	 * to format the batch sequence id
	 * @param batchId
	 * @return batchId
	 */
	public static String getFromattedBatchSequenceId(Long batchId) {
		DecimalFormat format = new DecimalFormat(BATCH_ID_FORMAT);
		return format.format(batchId);
	}

	/**
	 * to get the message length in bytes
	 * @param message
	 * @return bytes or 0
	 */
	public static long messageLengthInBytes(String message) {
		if (null != message) {
			return message.getBytes().length;
		}
		return 0;
	}
	/**
	 * To get transaction id details
	 * @param transactionId
	 * @return transactionId or null
	 */
	public static String getResponseTransactionId(String transactionId) {
		if (null != transactionId && !transactionId.isEmpty()) {
			return transactionId.substring(0, transactionId.lastIndexOf("::"));
		}
		return null;
	}

	/**
	 * get the thread sleep time
	 * @param ratePerMinute
	 * @return ratePerMinute or 0
	 */
	public static long getSleepMsForRate ( double ratePerMinute )
	{
		if ( ratePerMinute <= 0.0 ) return 0;
		return Math.max ( 1000, Math.round ( 60 * 1000 / ratePerMinute ) );
	}

	  public static String getRemoteAddress(DMaaPContext ctx)
	  {
	    String reqAddr = ctx.getRequest().getRemoteAddr();
	    String fwdHeader = getFirstHeader("X-Forwarded-For",ctx);
	    return ((fwdHeader != null) ? fwdHeader : reqAddr);
	  }
	  public static String getFirstHeader(String h,DMaaPContext ctx)
	  {
	    List l = getHeader(h,ctx);
	    return ((l.size() > 0) ? (String)l.iterator().next() : null);
	  }
	  public static List<String> getHeader(String h,DMaaPContext ctx)
	  {
	    LinkedList list = new LinkedList();
	    Enumeration e = ctx.getRequest().getHeaders(h);
	    while (e.hasMoreElements())
	    {
	      list.add(e.nextElement().toString());
	    }
	    return list;
	  }
}
