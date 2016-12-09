/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient.http;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The host selector selects one out of a set of possible host/port values to use. It continually
 * returns the selected host/port unless the client calls reportReachabilityProblem. In that case,
 * the next selection call may return a new host/port. If the host is blacklisted, it won't be
 * returned for that amount of time.
 * 
 * The initial selection was random at first, but it turns out that moving client locations across
 * Cambria API nodes is a bit taxing to the service. Instead, we take client signature info
 * and map it against the number of possible entries so that a given consumer normally sticks to
 * the same API node across runs. That also helps keep the client easy to track when troubleshooting.
 * 
 */
class HostSelector
{
	public HostSelector ( String hostPart )
	{
		this ( makeSet ( hostPart ), null );
	}

	public HostSelector ( Collection<String> baseHosts )
	{
		this ( baseHosts, null );
	}

	public HostSelector ( Collection<String> baseHosts, String signature )
	{
		if ( baseHosts.size () < 1 )
		{
			throw new IllegalArgumentException ( "At least one host must be provided." );
		}

		fBaseHosts = new TreeSet<String> ( baseHosts );
		fBlacklist = new DelayQueue<BlacklistEntry> ();
		fIdealHost = null;

		if ( signature != null )
		{
			// map the signature into an index in the host set
			int index = Math.abs ( signature.hashCode () ) % baseHosts.size();

			// iterate to the selected host
			Iterator<String> it = fBaseHosts.iterator ();
			while ( index-- > 0 )
			{
				it.next ();
			}
			fIdealHost = it.next ();
		}
	}

	public String selectBaseHost ()
	{
		if ( fCurrentHost == null )
		{
			makeSelection ();
		}
		return fCurrentHost;
	}
	
	public void reportReachabilityProblem ( long blacklistUnit, TimeUnit blacklistTimeUnit )
	{
		if ( fCurrentHost == null )
		{
			log.warn ( "Reporting reachability problem, but no host is currently selected." );
		}
		
		if ( blacklistUnit > 0 )
		{
			for ( BlacklistEntry be : fBlacklist )
			{
				if ( be.getHost().equals ( fCurrentHost ) )
				{
					be.expireNow ();
				}
			}

			final LinkedList<BlacklistEntry> devNull = new LinkedList<BlacklistEntry> ();
			fBlacklist.drainTo ( devNull );

			if ( fCurrentHost != null )
			{
				fBlacklist.add ( new BlacklistEntry ( fCurrentHost, TimeUnit.MILLISECONDS.convert ( blacklistUnit, blacklistTimeUnit ) ) );
			}
		}
		fCurrentHost = null;
	}

	private final TreeSet<String> fBaseHosts;
	private final DelayQueue<BlacklistEntry> fBlacklist;
	private String fIdealHost;
	private String fCurrentHost; 

	private String makeSelection ()
	{
		final TreeSet<String> workingSet = new TreeSet<String> ( fBaseHosts );

		// empty expired blacklist items, then take the remaining out of the working set
		final LinkedList<BlacklistEntry> devNull = new LinkedList<BlacklistEntry> ();
		fBlacklist.drainTo ( devNull );
		for ( BlacklistEntry be : fBlacklist )
		{
			workingSet.remove ( be.getHost () );
		}

		// we have to have something, so if there are none, everything is fair game,
		// which means we'll wind up using the last used host (fCurrentHost). It's
		// probably better to go with the ideal (which may or may not be current),
		// so we also clear current.
		if ( workingSet.size() == 0 )
		{
			log.warn ( "All hosts were blacklisted; reverting to full set of hosts." );
			workingSet.addAll ( fBaseHosts );
			fCurrentHost = null;
		}

		// prefer the current host, then the ideal host, then choose one at random
		String selection = null;
		if ( fCurrentHost != null && workingSet.contains ( fCurrentHost ) )
		{
			selection = fCurrentHost;
		}
		else if ( fIdealHost != null && workingSet.contains ( fIdealHost ) )
		{
			selection = fIdealHost;
		}
		else
		{
			final Vector<String> v = new Vector<String> ( workingSet );
			final int index = Math.abs ( new Random ().nextInt () ) % workingSet.size ();
			selection = v.elementAt ( index );
		}

		fCurrentHost = selection;
		return fCurrentHost;
	}
	
	private static Set<String> makeSet ( String s )
	{
		final TreeSet<String> set = new TreeSet<String> ();
		set.add ( s );
		return set;
	}

	private static class BlacklistEntry implements Delayed
	{
		public BlacklistEntry ( String host, long delayMs )
		{
			fHost = host;
			fExpireAtMs = System.currentTimeMillis () + delayMs; 
		}

		public void expireNow ()
		{
			fExpireAtMs = 0;
		}

		public String getHost ()
		{
			return fHost;
		}
		
		@Override
		public int compareTo ( Delayed o )
		{
			final Long thisDelay = getDelay ( TimeUnit.MILLISECONDS );
			return thisDelay.compareTo ( o.getDelay ( TimeUnit.MILLISECONDS ) );
		}

		@Override
		public long getDelay ( TimeUnit unit )
		{
			final long remainingMs = fExpireAtMs - System.currentTimeMillis ();
			return unit.convert ( remainingMs, TimeUnit.MILLISECONDS );
		}

		private final String fHost;
		private long fExpireAtMs;
	}

	private static final Logger log = LoggerFactory.getLogger ( HostSelector.class );
}
