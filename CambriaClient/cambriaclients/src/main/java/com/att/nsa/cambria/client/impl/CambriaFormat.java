/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client.impl;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public enum CambriaFormat
{
	/**
	 * Messages are sent using Cambria's message format.
	 */
	CAMBRIA
	{
		public String toString() { return "application/cambria"; }
	},

	/**
	 * Messages are sent using Cambria's message format with compression.
	 */
	CAMBRIA_ZIP
	{
		public String toString() { return "application/cambria-zip"; }
	},

	/**
	 * messages are sent as simple JSON objects.
	 */
	JSON
	{
		public String toString() { return "application/json"; }
	}
}
