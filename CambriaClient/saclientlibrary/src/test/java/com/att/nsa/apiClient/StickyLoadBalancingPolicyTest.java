/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient;

import java.net.URI;
import java.util.Arrays;

import org.apache.http.HttpHost;
import org.junit.Test;

import com.att.nsa.apiClient.lb.RoundRobinLoadBalancingPolicy;

public class StickyLoadBalancingPolicyTest {

	@Test
	public void testRoundRobinLoadBalancingPolicy() throws Exception {
		final RoundRobinLoadBalancingPolicy<URI, HttpHost> lbPolicy = new RoundRobinLoadBalancingPolicy<URI, HttpHost>(
				Arrays.asList(
						new HttpHost("localhost", 6167),
						new HttpHost("localhost", 6168), 
						new HttpHost("localhost", 6169)
				)
		);
		
		System.out.println(lbPolicy.select(new URI("/testing")));
		System.out.println(lbPolicy.select(new URI("/testing")));
		System.out.println(lbPolicy.select(new URI("/testing")));
		System.out.println(lbPolicy.select(new URI("/testing")));
		lbPolicy.onSuspend(new HttpHost("localhost", 6167));
		System.out.println(lbPolicy.select(new URI("/testing")));
		lbPolicy.onSuspend(new HttpHost("localhost", 6168));
		System.out.println(lbPolicy.select(new URI("/testing")));
		lbPolicy.onSuspend(new HttpHost("localhost", 6169));
		System.out.println(lbPolicy.select(new URI("/testing")));
		System.out.println(lbPolicy.select(new URI("/testing")));
		System.out.println(lbPolicy.select(new URI("/testing")));
		System.out.println(lbPolicy.select(new URI("/testing")));
		lbPolicy.onUp(new HttpHost("localhost", 6168));
		System.out.println(lbPolicy.select(new URI("/testing")));
		System.out.println(lbPolicy.select(new URI("/testing")));
		System.out.println(lbPolicy.select(new URI("/testing")));
		System.out.println(lbPolicy.select(new URI("/testing")));
	}
}
