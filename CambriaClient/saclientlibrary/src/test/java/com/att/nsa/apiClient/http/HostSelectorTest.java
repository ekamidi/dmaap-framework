/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient.http;

import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.Test;

public class HostSelectorTest extends TestCase
{
	@Test
	public void testStickySelector ()
	{
		final TreeSet<String> urls = new TreeSet<String> ();
		urls.add ( "A" );
		urls.add ( "B" );
		urls.add ( "C" );
		urls.add ( "D" );

		// first run
		final HostSelector selector1 = new HostSelector ( urls, "topic/group/id" );
		final String choice1 = selector1.selectBaseHost ();

		// second run
		final HostSelector selector2 = new HostSelector ( urls, "topic/group/id" );
		final String choice2 = selector2.selectBaseHost ();

		assertEquals ( choice1, choice2 );
	}

	@Test
	public void testFailoverSelector ()
	{
		final TreeSet<String> urls = new TreeSet<String> ();
		urls.add ( "A" );
		urls.add ( "B" );
		urls.add ( "C" );

		// get a host and mark it down
		final HostSelector selector = new HostSelector ( urls, "topic/group/id" );
		final String choice1 = selector.selectBaseHost ();
		assertNotNull ( choice1 );
		selector.reportReachabilityProblem ( 24, TimeUnit.HOURS );

		// get a new host
		final String choice2 = selector.selectBaseHost ();
		assertNotNull ( choice2 );
		assertFalse ( choice1.equals ( choice2 ) );

		// report a problem with the new host
		selector.reportReachabilityProblem ( 24, TimeUnit.HOURS );
		final String choice3 = selector.selectBaseHost ();
		assertNotNull ( choice3 );
		assertFalse ( choice1.equals ( choice3 ) );
		assertFalse ( choice2.equals ( choice3 ) );

		// report a problem with the last host, so next pick should be 
		// back to the ideal host (because all are blacklisted). Ideal
		// was choice1.
		selector.reportReachabilityProblem ( 24, TimeUnit.HOURS );
		final String choice4 = selector.selectBaseHost ();
		assertNotNull ( choice4 );
		assertEquals ( choice1, choice4 );
	}
}
