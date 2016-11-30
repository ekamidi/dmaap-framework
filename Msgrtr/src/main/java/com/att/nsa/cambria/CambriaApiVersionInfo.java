/*******************************************************************************
 * BSD License
 *  
 * Copyright (c) 2016, AT&T Intellectual Property.  All other rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *  
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *    and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 * 3. All advertising materials mentioning features or use of this software must display the
 *    following acknowledgement:  This product includes software developed by the AT&T.
 * 4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 *******************************************************************************/
package com.att.nsa.cambria;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
/**
 * CambriaApiVersionInfo will provide the version of cambria code
 * 
 * @author author
 *
 */
public class CambriaApiVersionInfo {
    
	/**
	 * 3 constants are defined:-
	 * PROPS,VERSION and LOG
	 */
	
	private static final Properties PROPS = new Properties();
    private static final String VERSION;


    private static final EELFLogger LOG = EELFManager.getInstance().getLogger(CambriaApiVersionInfo.class);
    
    /**
     * private constructor created with no argument
     * to avoid default constructor
     */
    private CambriaApiVersionInfo()
    {
    	
    }
    
    /**
     * returns version of String type
     */
    public static String getVersion() {
        return VERSION;
    }

    /** 
     * 
     * defines static initialization method
     * It initializes VERSION Constant
     * it handles exception in try catch block 
     * and throws IOException
     * 
     */
    
    static {
        String use = null;
        try {
            final InputStream is = CambriaApiVersionInfo.class
                    .getResourceAsStream("/cambriaApiVersion.properties");
            if (is != null) {
            	PROPS.load(is);
                use = PROPS.getProperty("cambriaApiVersion", null);
            }
        } catch (IOException e) {
            LOG.error("Failed due to IO EXception:"+e);
        }
        VERSION = use;
    }
}
