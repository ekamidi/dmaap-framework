/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.HttpClientConnectionManager;

public class SAConnectionMonitorThread implements Runnable {

    private final HttpClientConnectionManager connMgr;
    private final long idleTimeout;
    private final TimeUnit idleTimeoutUnit;
    
	public SAConnectionMonitorThread(HttpClientConnectionManager connMgr, long idleTimeout, TimeUnit idleTimeoutUnit) {
		this.connMgr = connMgr;
		this.idleTimeout = idleTimeout;
		this.idleTimeoutUnit = idleTimeoutUnit;
	}

	@Override
	public void run() {
        // Close expired connections
        connMgr.closeExpiredConnections();
        
        // Optionally, close connections
        // that have been idle longer than 30 sec
        connMgr.closeIdleConnections(idleTimeout, idleTimeoutUnit);
    }
}