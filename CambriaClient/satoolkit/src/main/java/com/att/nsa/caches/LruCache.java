/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.caches;

import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache<K, V> extends LinkedHashMap<K, V>
{
	public LruCache(int size) {
		//A load factor > 1 along with a size limit guarantees that the map will not be resized
		super(size, 1.25f, true);

		if (size <= 0)
			throw new IllegalArgumentException("Cache size must be greater than 0");
		
		this.MAX_ENTRIES = size;		
		this.hits = 0;
		this.misses = 0;
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
	}
	
	/**
	 * Add the (key, value) pair to the cache if there is enough space.
	 * If there is not enough space, evict the least recently used
	 * (key, value) pair in the cache before adding this (key, value) pair.
	 * @param key
	 * @param value
	 * @return 
	 */
	public V put(K key, V value) {
		
		if (value == null) {
			throw new IllegalArgumentException("Cannot store null values in cache");
		}
		
		return super.put(key, value);
	}
	
	/**
	 * Get the value for the key if it exists in the cache.  If
	 * the key does not exist in the cache, return null.  Since the null
	 * return can be ambiguous, use {@link #containsKey containsKey} to
	 * determine if a key exists in the cache.
	 * @param key
	 */
	public V get(Object key) {

		if (containsKey(key)) {
			this.hits++;
			return super.get(key);
		}
		
		this.misses++;
		return null;
	}
	
	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() >= MAX_ENTRIES;
     }

	private static final long serialVersionUID = -6592129461672856513L;

	private final int MAX_ENTRIES;
	
	private long hits;
	private long misses;
}