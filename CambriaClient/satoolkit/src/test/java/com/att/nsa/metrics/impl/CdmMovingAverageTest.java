/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.metrics.impl;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;

import com.att.nsa.util.NsaTestClock;

@Deprecated
@Ignore
public class CdmMovingAverageTest extends TestCase
{
	@Test
	public void testMovingAvg ()
	{
		final NsaTestClock clock = new NsaTestClock ();
		clock.setTo ( 1441228602 );

		final CdmMovingAverage a = new CdmMovingAverage ( "test", 60, TimeUnit.SECONDS );

		a.tick ( 15 );
		clock.addMs ( 1 );	// FIXME: the memory time series db uses a binary search that doesn't work correctly if multiple entries have the same time

		a.tick ( 10 );
		clock.addMs ( 1 );

		a.tick ( 5 );
		clock.addMs ( 1 );

		final double avg = a.getAverage ();
		assertEquals ( 10.0, avg );
	}
}
