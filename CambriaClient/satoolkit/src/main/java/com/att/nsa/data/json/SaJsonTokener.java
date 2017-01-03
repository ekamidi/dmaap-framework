/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.data.json;

import java.io.InputStream;
import java.io.Reader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Custom JSON tokener for more explicit parse control.
 *
 */
public class SaJsonTokener extends JSONTokener
{
	public SaJsonTokener ( Reader reader )
	{
		super ( reader );
	}

	public SaJsonTokener ( String s )
	{
		super ( s );
	}

	public SaJsonTokener ( InputStream is )
	{
		super ( is );
	}

	/**
	 * Perform a more strict parse that doesn't allow unquoted strings.
	 * @return this tokener
	 */
	public SaJsonTokener stricter ()
	{
		fNoUnquotedStrings = true;
		return this;
	}

	private boolean fNoUnquotedStrings = false;

	@Override
	public Object nextValue () throws JSONException
	{
		/*
		 * This implementation comes from JSONTokener from version 20131018.
		 * The difference between this and the base class is in the fNoUnquotedStrings
		 * block below. -pc569h 2016-11-12
		 */

        char c = this.nextClean();
        String string;

        switch (c) {
            case '"':
            case '\'':
                return this.nextString(c);
            case '{':
                this.back();
                return new JSONObject(this);
            case '[':
                this.back();
                return new JSONArray(this);
        }

        /*
         * Handle unquoted text. This could be the values true, false, or
         * null, or it can be a number. An implementation (such as this one)
         * is allowed to also accept non-standard forms.
         *
         * Accumulate characters until we reach the end of the text or a
         * formatting character.
         */

        StringBuffer sb = new StringBuffer();
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            sb.append(c);
            c = this.next();
        }
        this.back();

        string = sb.toString().trim();
        if ("".equals(string)) {
            throw this.syntaxError("Missing value");
        }

        final Object result = JSONObject.stringToValue(string);

		// at this point, the allowed values for string are null, true,
		// false, or numbers. If the caller has specified no unquoted strings,
		// and the value is not one of these, it's an error.
        if ( fNoUnquotedStrings && ( result instanceof String ) )
		{
			throw new JSONException ( "Unquoted string found with stricter parsing enabled." );
		}

		return result;
	}
}
