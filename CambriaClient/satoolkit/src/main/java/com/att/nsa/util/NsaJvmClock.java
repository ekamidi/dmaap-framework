/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.util;

/**
 * @deprecated use SaClock
 */
public class NsaJvmClock extends NsaClock
{
	@Override
	public long getCurrentMs () { return jvmNow (); }

	public static long jvmNow () { return System.currentTimeMillis (); }
}
