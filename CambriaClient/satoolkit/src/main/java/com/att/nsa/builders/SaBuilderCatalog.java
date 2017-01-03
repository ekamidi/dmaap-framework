/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.builders;

import java.util.Set;

import org.json.JSONObject;

/**
 * The builder system (originally from Highland Park Core) manages "plug-ins" via catalogs. There
 * are 0 or more catalogs registered with the builder, and each catalog has 0 or more class types
 * that it can build. Each catalog builds classes of a single interface, known as T.
 *  
 *
 * @param <T>
 */
public interface SaBuilderCatalog<T>
{
	/**
	 * Get the class of objects provided by the catalog. For example, "Filters" or "Processors" 
	 * @return the class of object provided by the catalog
	 */
	Class<?> getCatalogType ();

	/**
	 * Get the names of items in the catalog.
	 * @return a set of catalog entries.
	 */
	Set<String> getCatalogTypes ();

	/**
	 * Check if this catalog creates objects of the class with the given name.
	 * @param name a short name or fully qualified class name
	 * @return true if this catalog creates the class named by name
	 */
	boolean creates ( String name );

	/**
	 * Create an object from the catalog.
	 * @param name
	 * @param config
	 * @param cc
	 * @return the new object
	 * @throws SaBuilderException
	 */
	T create ( String name, JSONObject config, SaBuilder cc ) throws SaBuilderException;
}
