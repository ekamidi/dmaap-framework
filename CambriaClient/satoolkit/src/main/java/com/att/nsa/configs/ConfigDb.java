/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.configs;

import java.util.Map;
import java.util.Set;

/**
 * Abstracted storage for shared system configuration. In a cluster, this storage is expected
 * to be shared consistently across nodes by a system like Zookeeper.
 * 
 * The class uses plain strings for persistence. These strings could be structured data encoded
 * in JSON or XML, for example.
 * 
 * Note that a node can both have data and have child nodes - it's more like ZK than a file system.
 * 
 *
 */
public interface ConfigDb
{
	/**
	 * Get the root path for this DB
	 * @return a path
	 */
	ConfigPath getRoot ();

	/**
	 * Parse a config path, splitting on forward slash.
	 * @param pathAsString
	 * @return a path
	 */
	ConfigPath parse ( String pathAsString );

	/**
	 * does the given config path exist?
	 * @param path
	 * @return true if the path exists
	 */
	boolean exists ( ConfigPath path ) throws ConfigDbException;

	/**
	 * Load a config string based on its path
	 * @param key
	 * @return the string data or null
	 */
	String load ( ConfigPath key ) throws ConfigDbException;

	/**
	 * Load the paths of children of this node. If the key doesn't exist,
	 * null is returned.
	 * @param key
	 * @return a set of child paths
	 */
	Set<ConfigPath> loadChildrenNames ( ConfigPath key ) throws ConfigDbException;
	
	/**
	 * Load a map of each child key's data. If the child key has no local data,
	 * its value in the map will be null.
	 * @param key
	 * @return
	 */
	Map<ConfigPath,String> loadChildrenOf ( ConfigPath key ) throws ConfigDbException;

	/**
	 * Store a config string to a path.
	 * @param key
	 * @param data
	 * @throws ConfigDbException 
	 */
	void store ( ConfigPath key, String data ) throws ConfigDbException;
	
	/**
	 * Delete a path. If it has sub-nodes, they're deleted too.
	 * @param key
	 * @return true if anything was removed
	 */
	boolean clear ( ConfigPath key ) throws ConfigDbException;

	/**
	 * Get the last modification time for this record. 
	 * @param path
	 * @return the last mod time in epoch seconds, or a number less than 0 if the path does not exist
	 * @throws ConfigDbException
	 */
	long getLastModificationTime ( ConfigPath path ) throws ConfigDbException;
}
