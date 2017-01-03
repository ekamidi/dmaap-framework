/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.metrics.impl;

import java.util.concurrent.TimeUnit;


public abstract class CdmBasePolledMetric extends CdmSimpleMetric
{
	protected CdmBasePolledMetric ( long pollEvery, TimeUnit tuPollEvery )
	{
		fPoll = pollEvery;
		fPollTimeUnit = tuPollEvery;
		fNextRunAt = 0;
	}

	@Override
	public boolean requiresScheduledEvaluation ()
	{
		return true;
	}

	@Override
	public long getDelay ( TimeUnit tu )
	{
		final long remaining = fNextRunAt - System.currentTimeMillis (); 
		return tu.convert ( remaining, TimeUnit.MILLISECONDS );
	}

	@Override
	public final void poll ()
	{
		long msToNext = TimeUnit.MILLISECONDS.convert ( fPoll, fPollTimeUnit );
		fNextRunAt = System.currentTimeMillis () + msToNext;

		doPoll ();
	}

	public long getPollTime ()
	{
		return fPoll;
	}

	public TimeUnit getPollTimeUnit ()
	{
		return fPollTimeUnit;
	}

	private final long fPoll;
	private final TimeUnit fPollTimeUnit;
	private long fNextRunAt;

	protected abstract void doPoll ();
}
