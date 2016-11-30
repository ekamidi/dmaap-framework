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
package com.att.nsa.dmaap.filemonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;


//import com.att.ssf.filemonitor.FileChangedListener;
//import com.att.ssf.filemonitor.FileMonitor;

/**
 * ServicePropertyService class
 * @author author
 *
 */
public class ServicePropertyService {
	private boolean loadOnStartup;
	private ServicePropertiesListener fileChangedListener;
	private ServicePropertiesMap filePropertiesMap;
	private String ssfFileMonitorPollingInterval;
	private String ssfFileMonitorThreadpoolSize;
	private List<File> fileList;
	private static final String FILE_CHANGE_LISTENER_LOC = System
			.getProperty("AJSC_CONF_HOME") + "/etc";
	private static final String USER_CONFIG_FILE = "service-file-monitor.properties";

	private static final EELFLogger logger = EELFManager.getInstance().getLogger(ServicePropertyService.class);

	// do not remove the postConstruct annotation, init method will not be
	// called after constructor
	/**
	 * Init method
	 * @throws Exception ex
	 */
	@PostConstruct
	public void init() throws Exception {

		try {
			getFileList(FILE_CHANGE_LISTENER_LOC);

//			for (File file : fileList) {
//					FileChangedListener fileChangedListener = this.fileChangedListener;
//					Object filePropertiesMap = this.filePropertiesMap;
//					Method m = filePropertiesMap.getClass().getMethod(
//							"refresh", File.class);
//					m.invoke(filePropertiesMap, file);
//					FileMonitor fm = FileMonitor.getInstance();
//					fm.addFileChangedListener(file, fileChangedListener,
//							loadOnStartup);
//				
//			}
		} catch (Exception ex) {
			logger.error("Error creating property map ", ex);
		}

	}

	private void getFileList(String dirName) throws IOException {
		File directory = new File(dirName);
		FileInputStream fis = null;

		if (fileList == null)
			fileList = new ArrayList<File>();

		// get all the files that are ".json" or ".properties", from a directory
		// & it's sub-directories
		File[] fList = directory.listFiles();

		for (File file : fList) {
			// read service property files from the configuration file
			if (file.isFile() && file.getPath().endsWith(USER_CONFIG_FILE)) {
				try {
					fis = new FileInputStream(file);
					Properties prop = new Properties();
					prop.load(fis);

					for (String filePath : prop.stringPropertyNames()) {
						fileList.add(new File(prop.getProperty(filePath)));
					}
				} catch (Exception ioe) {
					logger.error("Error reading the file stream ", ioe);
				} finally {
					fis.close();
				}
			} else if (file.isDirectory()) {
				getFileList(file.getPath());
			}
		}

	}

	public void setLoadOnStartup(boolean loadOnStartup) {
		this.loadOnStartup = loadOnStartup;
	}

	public void setSsfFileMonitorPollingInterval(
			String ssfFileMonitorPollingInterval) {
		this.ssfFileMonitorPollingInterval = ssfFileMonitorPollingInterval;
	}

	public void setSsfFileMonitorThreadpoolSize(
			String ssfFileMonitorThreadpoolSize) {
		this.ssfFileMonitorThreadpoolSize = ssfFileMonitorThreadpoolSize;
	}

	public boolean isLoadOnStartup() {
		return loadOnStartup;
	}

	public String getSsfFileMonitorPollingInterval() {
		return ssfFileMonitorPollingInterval;
	}

	public String getSsfFileMonitorThreadpoolSize() {
		return ssfFileMonitorThreadpoolSize;
	}

	public ServicePropertiesListener getFileChangedListener() {
		return fileChangedListener;
	}

	public void setFileChangedListener(
			ServicePropertiesListener fileChangedListener) {
		this.fileChangedListener = fileChangedListener;
	}

	public ServicePropertiesMap getFilePropertiesMap() {
		return filePropertiesMap;
	}

	public void setFilePropertiesMap(ServicePropertiesMap filePropertiesMap) {
		this.filePropertiesMap = filePropertiesMap;
	}
}
