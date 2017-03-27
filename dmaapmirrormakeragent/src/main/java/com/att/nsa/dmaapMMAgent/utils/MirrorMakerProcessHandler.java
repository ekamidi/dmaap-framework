/*******************************************************************************
 * /*******************************************************************************
 *  * BSD License
 *  *  
 *  * Copyright (c) 2016, AT&T Intellectual Property.  All other rights reserved.
 *  *  
 *  * Redistribution and use in source and binary forms, with or without modification, are permitted
 *  * provided that the following conditions are met:
 *  *  
 *  * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *  *    and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *  *    conditions and the following disclaimer in the documentation and/or other materials provided
 *  *    with the distribution.
 *  * 3. All advertising materials mentioning features or use of this software must display the
 *  *    following acknowledgement:  This product includes software developed by the AT&T.
 *  * 4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
 *  *    promote products derived from this software without specific prior written permission.
 *  *  
 *  * THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
 *  * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  * SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
 *  * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *  * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  * DAMAGE.
 *  *******************************************************************************/

package com.att.nsa.dmaapMMAgent.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.att.nsa.dmaapMMAgent.MirrorMakerAgent;

public class MirrorMakerProcessHandler {
	static final Logger logger = Logger.getLogger(MirrorMakerProcessHandler.class);

	public static boolean checkMirrorMakerProcess(String agentname) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process mmprocess = null;

			if (System.getProperty("os.name").contains("Windows")) {
				String args = "";
				args = "wmic.exe process where \"commandline like '%agentname=" + agentname
						+ "~%' and caption='java.exe'\"";
				mmprocess = rt.exec(args);
			} else {
				String args[] = { "/bin/sh", "-c", "ps -ef |grep java |grep agentname=" + agentname + "~" };
				mmprocess = rt.exec(args);
			}

			InputStream is = mmprocess.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				if (line.contains("agentname=" + agentname) && line.contains("/bin/sh -c") == false) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void stopMirrorMaker(String agentname) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process killprocess = null;

			if (System.getProperty("os.name").contains("Windows")) {
				String args = "wmic.exe process where \"commandline like '%agentname=" + agentname
						+ "~%' and caption='java.exe'\" call terminate";
				killprocess = rt.exec(args);
			} else {
				String args[] = { "/bin/sh", "-c",
						"kill -9 $(ps -ef |grep java |grep agentname=" + agentname + "~| awk '{print $2}')" };
				// args = "kill $(ps -ef |grep java |grep agentname=" +
				// agentname + "~| awk '{print $2}')";
				killprocess = rt.exec(args);
			}

			InputStream is = killprocess.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
			}

			logger.info("Mirror Maker " + agentname + " Stopped");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void startMirrorMaker(String mmagenthome, String kafkaHome, String agentName, String consumerConfig,
			String producerConfig, String whitelist) {
		try {
			Runtime rt = Runtime.getRuntime();

			if (System.getProperty("os.name").contains("Windows")) {
				String args = kafkaHome + "/bin/windows/kafka-run-class.bat -Dagentname=" + agentName
						+ "~ kafka.tools.MirrorMaker --consumer.config " + consumerConfig + " --producer.config "
						+ producerConfig + " --whitelist '" + whitelist + "' > " + mmagenthome + "/logs/" + agentName
						+ "_MMaker.log";
				final Process process = rt.exec(args);
				new Thread() {
					public void run() {
						try {
							InputStream is = process.getInputStream();
							InputStreamReader isr = new InputStreamReader(is);
							BufferedReader br = new BufferedReader(isr);
							String line;
							while ((line = br.readLine()) != null) {
								// System.out.println(line);
							}
						} catch (Exception anExc) {
							anExc.printStackTrace();
						}
					}
				}.start();
			} else {
				String args[] = { "/bin/sh", "-c",
						kafkaHome + "/bin/kafka-run-class.sh -Dagentname=" + agentName
								+ "~ kafka.tools.MirrorMaker --consumer.config " + consumerConfig
								+ " --producer.config " + producerConfig + " --whitelist '" + whitelist + "' >"
								+ mmagenthome + "/logs/" + agentName + "_MMaker.log 2>&1" };
				final Process process = rt.exec(args);
				new Thread() {
					public void run() {
						try {
							InputStream is = process.getInputStream();
							InputStreamReader isr = new InputStreamReader(is);
							BufferedReader br = new BufferedReader(isr);
							String line;
							while ((line = br.readLine()) != null) {
								// System.out.println(line);
							}
						} catch (Exception anExc) {
							anExc.printStackTrace();
						}
					}
				}.start();
			}

			logger.info("Mirror Maker " + agentName + " Started" + " WhiteListing:" + whitelist);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
