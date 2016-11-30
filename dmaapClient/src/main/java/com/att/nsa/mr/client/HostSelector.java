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
package com.att.nsa.mr.client;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostSelector
{
  private final TreeSet<String> fBaseHosts;
  private final DelayQueue<BlacklistEntry> fBlacklist;
  private String fIdealHost;
  private String fCurrentHost;
  private static final Logger log = LoggerFactory.getLogger(HostSelector.class);

  public HostSelector(String hostPart)
  {
    this(makeSet(hostPart), null);
  }

  public HostSelector(Collection<String> baseHosts)
  {
    this(baseHosts, null);
  }

  public HostSelector(Collection<String> baseHosts, String signature)
  {
    if (baseHosts.size() < 1)
    {
      throw new IllegalArgumentException("At least one host must be provided.");
    }

    this.fBaseHosts = new TreeSet(baseHosts);
    this.fBlacklist = new DelayQueue();
    this.fIdealHost = null;

    if (signature == null) {
      return;
    }
    int index = Math.abs(signature.hashCode()) % baseHosts.size();

    Iterator it = this.fBaseHosts.iterator();
    while (index-- > 0)
    {
      it.next();
    }
    this.fIdealHost = ((String)it.next());
  }

  public String selectBaseHost()
  {
    if (this.fCurrentHost == null)
    {
      makeSelection();
    }
    return this.fCurrentHost;
  }

  public void reportReachabilityProblem(long blacklistUnit, TimeUnit blacklistTimeUnit)
  {
    if (this.fCurrentHost == null)
    {
      log.warn("Reporting reachability problem, but no host is currently selected.");
    }

    if (blacklistUnit > 0L)
    {
      for (BlacklistEntry be : this.fBlacklist)
      {
        if (be.getHost().equals(this.fCurrentHost))
        {
          be.expireNow();
        }
      }

      LinkedList devNull = new LinkedList();
      this.fBlacklist.drainTo(devNull);

      if (this.fCurrentHost != null)
      {
        this.fBlacklist.add(new BlacklistEntry(this.fCurrentHost, TimeUnit.MILLISECONDS.convert(blacklistUnit, blacklistTimeUnit)));
      }
    }
    this.fCurrentHost = null;
  }

  private String makeSelection()
  {
    TreeSet workingSet = new TreeSet(this.fBaseHosts);

    LinkedList devNull = new LinkedList();
    this.fBlacklist.drainTo(devNull);
    for (BlacklistEntry be : this.fBlacklist)
    {
      workingSet.remove(be.getHost());
    }

    if (workingSet.size() == 0)
    {
      log.warn("All hosts were blacklisted; reverting to full set of hosts.");
      workingSet.addAll(this.fBaseHosts);
      this.fCurrentHost = null;
    }

    String selection = null;
    if ((this.fCurrentHost != null) && (workingSet.contains(this.fCurrentHost)))
    {
      selection = this.fCurrentHost;
    }
    else if ((this.fIdealHost != null) && (workingSet.contains(this.fIdealHost)))
    {
      selection = this.fIdealHost;
    }
    else
    {
      Vector v = new Vector(workingSet);
      int index = Math.abs(new Random().nextInt()) % workingSet.size();
      selection = (String)v.elementAt(index);
    }

    this.fCurrentHost = selection;
    return this.fCurrentHost;
  }

  private static Set<String> makeSet(String s)
  {
    TreeSet set = new TreeSet();
    set.add(s);
    return set; }

  private static class BlacklistEntry implements Delayed {
    private final String fHost;
    private long fExpireAtMs;

    public BlacklistEntry(String host, long delayMs) {
      this.fHost = host;
      this.fExpireAtMs = (System.currentTimeMillis() + delayMs);
    }

    public void expireNow()
    {
      this.fExpireAtMs = 0L;
    }

    public String getHost()
    {
      return this.fHost;
    }

    public int compareTo(Delayed o)
    {
      Long thisDelay = Long.valueOf(getDelay(TimeUnit.MILLISECONDS));
      return thisDelay.compareTo(Long.valueOf(o.getDelay(TimeUnit.MILLISECONDS)));
    }

    public long getDelay(TimeUnit unit)
    {
      long remainingMs = this.fExpireAtMs - System.currentTimeMillis();
      return unit.convert(remainingMs, TimeUnit.MILLISECONDS);
    }
  }
}
