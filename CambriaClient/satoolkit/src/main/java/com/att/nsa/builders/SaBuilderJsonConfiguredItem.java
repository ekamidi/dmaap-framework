/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.builders;

import org.json.JSONObject;

/**
 * A configured item can be read from or written to a JSON object
 * 
 */
public interface SaBuilderJsonConfiguredItem
{
	/**
	 * Read a configuration of this object from a JSON object.
	 * 
	 * @param builder
	 * @param dataObject
	 * @throws SaBuilderException
	 */
	void readFrom ( SaBuilder builder, JSONObject dataObject ) throws SaBuilderException;

	/**
	 * Write a representation of this object into the JSON object. The object
	 * after write should be loadable by readFrom to restore the state.
	 * 
	 * @param dataObject 
	 */
	void writeTo ( JSONObject dataObject );
}
