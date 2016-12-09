/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.caches;

import java.util.LinkedList;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.att.nsa.clock.SaClock;

public class TimeLimitedLruCache<K,V> extends LruCache<K,V>
{
	public TimeLimitedLruCache ( int maxSize, long maxObjCacheTime, TimeUnit maxObjCacheTimeUnit )
	{
		super ( maxSize );
		
		fMaxAgeMs = TimeUnit.MILLISECONDS.convert ( maxObjCacheTime, maxObjCacheTimeUnit );
		fTimers = new DelayQueue<TimerEntry> ();
	}
	
	/**
	 * Remove all (key, value) pairs from the cache.
	 */
	public void clear()
	{
		super.clear();

		fTimers.clear ();
	}
	
	/**
	 * Add the (key, value) pair to the cache if there is enough space.
	 * If there is not enough space, evict the least recently used
	 * (key, value) pair in the cache before adding this (key, value) pair.
	 * @param key
	 * @param value
	 * @return the previous value, or null
	 */
	public V put ( K key, V value )
	{
		pruneTimeouts ();
		final V v = super.put ( key, value );
		startTimer ( key );
		return v;
	}

	/**
	 * Check if this cache contains the given key.
	 * @param key
	 * @return true if the object is in the cache (and not expired)
	 */
	@Override
	public boolean containsKey ( Object key )
	{
		pruneTimeouts ();
		return super.containsKey ( key );
	}
	
	/**
	 * Get the value for the key if it exists in the cache.  If
	 * the key does not exist in the cache, return null.
	 * @param key
	 */
	public V get(Object key)
	{
		pruneTimeouts ();
		return super.get ( key );
	}

	/**
	 * Get the size of this cache.
	 * @return the size of objects that are alive
	 */
	@Override
	public int size ()
	{
		pruneTimeouts ();
		return super.size ();
	}

	// removeEldestEntry...
	// FIXME: update timers? (without the update, the timer will expire
    // and remove a non-existent key. If the user has since re-entered
    // the key, the timer would have been restarted and it's still a valid
    // removal.)

	private void startTimer ( K key )
	{
		LinkedList<TimerEntry> removals = new LinkedList<TimerEntry> ();
		for ( TimerEntry te : fTimers )
		{
			if ( te.getKey ().equals ( key ) )
			{
				removals.add ( te );
			}
		}
		for ( TimerEntry te : removals )
		{
			fTimers.remove ( te );
		}
		fTimers.add ( new TimerEntry ( key, SaClock.now () + fMaxAgeMs ) );
	}

	private void pruneTimeouts ()
	{
		TimerEntry te = null;
		while ( null != ( te = fTimers.poll () ) )
		{
			final K key = te.getKey ();
			remove ( key );
		}
	}

	private class TimerEntry implements Delayed
	{
		public TimerEntry ( K key, long expireAtMs )
		{
			fKey = key;
			fExpiresMs = expireAtMs;
		}

		public K getKey () { return fKey; }

		@Override
		public int compareTo ( Delayed o )
		{
			return Long.compare ( getDelay(TimeUnit.MILLISECONDS), o.getDelay ( TimeUnit.MILLISECONDS ) );
		}

		@Override
		public long getDelay ( TimeUnit unit )
		{
			// return remaining delay
			return unit.convert ( fExpiresMs - SaClock.now (), TimeUnit.MILLISECONDS );
		}

		private final K fKey;
		private final long fExpiresMs;
	}

	interface Clock
	{
		long now ();
	}

	private static final long serialVersionUID = 1L;

	private final long fMaxAgeMs;
	private final DelayQueue<TimerEntry> fTimers;
}
