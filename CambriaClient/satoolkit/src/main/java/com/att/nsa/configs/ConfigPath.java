/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.configs;

/**
 * Config objects are stored in file-system style paths.
 *
 */
public interface ConfigPath extends Comparable<ConfigPath>
{
	/**
	 * Get the parent path for this path
	 * @return the parent, or null if this is the root node
	 */
	ConfigPath getParent ();

	/**
	 * Get the name of this path (within its parent)
	 * @return
	 */
	String getName ();

	/**
	 * Get a child path of this path.
	 * @param name
	 * @return a path
	 */
	ConfigPath getChild ( String name );
}
