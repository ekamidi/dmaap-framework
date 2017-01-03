/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient.lb;



/**
 * A load balancer that selects type E elements based on some criteria C.
 * The implementation of selecting hosts does not necessarily have to select
 * based on any criteria.
 *
 * @param <T>
 * @param <E>
 */
public interface LoadBalancingPolicy<C, E> {
	public E select() throws NoHostAvailableException;
	public E select(C criteria) throws NoHostAvailableException;
	public void onUp(final E host);
	public void onDown(final E host);
	public void onSuspend(final E host);
	public void setSuspensionPeriod(int suspensionPeriodInSecs);
	public void close();
}
