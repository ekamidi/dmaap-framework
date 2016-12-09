/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.logging.log4j;

import junit.framework.TestCase;

import org.apache.log4j.Category;
import org.apache.log4j.MDC;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;

import com.att.nsa.clock.SaClock;
import com.att.nsa.testing.TestClock;

public class EcompLayoutTest extends TestCase
{
	@Test
	public void testDateFormat ()
	{
		final long timeMs = 1234567890123L;
		final String timeStr = EcompLayout.timestampMsToDate ( timeMs );
		assertEquals ( "2009-02-13T23:31:30.123+00:00", timeStr );
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testAutoPopulate ()
	{
		final TestClock tc = new TestClock ( 1234567890123L );

		final EcompLayout layout = new EcompLayout ();
		MDC.put ( EcompFields.kBeginTimestampMs, Long.toString ( SaClock.now () ) );

		tc.forward ( 60*1000L );
		layout.format ( new LoggingEvent ( "foo.bar", Category.getRoot (), Priority.INFO, "foobar", null ) );

		assertEquals ( "2009-02-13T23:31:30.123+00:00", MDC.get ( EcompFields.kBeginTimestamp ) );
		assertEquals ( "2009-02-13T23:32:30.123+00:00", MDC.get ( EcompFields.kEndTimestamp ) );
		assertEquals ( "60000", MDC.get ( EcompFields.kElapsedTimeMs ) );
	}
}
