/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient.http;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class EntityLruCache<V> extends LinkedHashMap<String, V> {

	private static final long serialVersionUID = -6592129461672856513L;

	private final int MAX_ENTRIES;
	private final long fMaxAgeMs;
	private final DelayQueue<TimerEntry> fTimers;
	private Clock fClock; // only for test
	
	private long hits;
	private long misses;

	
	public EntityLruCache ( int maxSize, long maxObjCacheTime, TimeUnit maxObjCacheTimeUnit )
	{
		//A load factor > 1 along with a size limit guarantees that the map will not be resized
		super(maxSize, 1.25f, true);

		if (maxSize <= 0)
			throw new IllegalArgumentException("Cache size must be greater than 0");
		
		this.MAX_ENTRIES = maxSize;		
		this.hits = 0;
		this.misses = 0;
		
		fMaxAgeMs = TimeUnit.MILLISECONDS.convert ( maxObjCacheTime, maxObjCacheTimeUnit );
		fTimers = new DelayQueue<TimerEntry> ();
		fClock = null;
	}
	
	/**
	 * @return the ratio of cache hits to misses.
	 */
	public double getHitRatio() {
		if (this.misses == 0)
			return (this.hits > 0) ? 1 : 0;
		
		return (double) this.hits / (double) this.misses;
	}
	
	/**
	 * Remove all (key, value) pairs from the cache.
	 */
	public void clear() {
		super.clear();
		
		this.hits = 0;
		this.misses = 0;

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
	public V put(String key, V value) {
		
		pruneTimeouts ();

		if (value == null) {
			throw new IllegalArgumentException("Cannot store null values in cache");
		}

		startTimer ( key );
		return super.put(key, value);
	}

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
	public V get(Object key) {

		pruneTimeouts ();

		if (containsKey(key)) {
			this.hits++;
			return super.get(key);
		}
		
		this.misses++;
		return null;
	}

	@Override
	public int size ()
	{
		pruneTimeouts ();
		return super.size ();
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
        return size() > MAX_ENTRIES;
        // FIXME: update timers? (without the update, the timer will expire
        // and remove a non-existent key. If the user has since re-entered
        // the key, the timer would have been restarted and it's still a valid
        // removal.)
     }

	private void startTimer ( String key )
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
		fTimers.add ( new TimerEntry ( key, now () + fMaxAgeMs ) );
	}

	private void pruneTimeouts ()
	{
		TimerEntry te = null;
		while ( null != ( te = fTimers.poll () ) )
		{
			final String key = te.getKey ();
			remove ( key );
		}
	}

	private long now ()
	{
		return fClock != null ? fClock.now () : System.currentTimeMillis ();
	}

	/**
	 * Set a test clock
	 * @param c
	 */
	void setClock ( Clock c )
	{
		fClock = c;
	}
	
	private class TimerEntry implements Delayed
	{
		public TimerEntry ( String key, long expireAtMs )
		{
			fKey = key;
			fExpiresMs = expireAtMs;
		}

		public String getKey () { return fKey; }

		@Override
		public int compareTo ( Delayed o )
		{
			return Long.compare ( getDelay(TimeUnit.MILLISECONDS), o.getDelay ( TimeUnit.MILLISECONDS ) );
		}

		@Override
		public long getDelay ( TimeUnit unit )
		{
			// return remaining delay
			return unit.convert ( fExpiresMs - now (), TimeUnit.MILLISECONDS );
		}

		private final String fKey;
		private final long fExpiresMs;
	}

	interface Clock
	{
		long now ();
	}
}
