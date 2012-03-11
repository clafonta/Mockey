/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2012  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.mockey.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.mockey.model.Service;
import com.mockey.runner.BSC;

/**
 * Manages plugins.
 * 
 * @author chadlafontaine
 * 
 */
public class PluginStore {
	private static Logger logger = Logger.getLogger(PluginStore.class);
	private static PluginStore pluginStoreInstance = new PluginStore();
	private List<String> reqInspectorClassNameList = new ArrayList<String>();

	/**
	 * Basic singleton.
	 * 
	 * @return
	 */
	public static PluginStore getInstance() {
		return PluginStore.pluginStoreInstance;
	}

	private PluginStore() {
		// We initialize the store with one Example/Sample implementation.
		this.saveOrUpdateIReqInspectorImplClassName(SampleRequestInspector.class
				.getName());
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getRequestInspectorImplClassNameList() {
		return this.reqInspectorClassNameList;
	}

	/**
	 * 
	 * @param arg
	 */
	public void saveOrUpdateIReqInspectorImplClassName(String reqInspectImplName) {

		if (!this.reqInspectorClassNameList.contains(reqInspectImplName)) {
			this.reqInspectorClassNameList.add(reqInspectImplName);
		}

	}

	/**
	 * Runs through 0 or more instances of <code>IRequestInspector</code> as
	 * defined in the plugin store.
	 * 
	 * @param service
	 *            - The Mockey service that will process this request.
	 * @param request
	 *            - Request to evaluate
	 * @return
	 * @see com.mockey.model.Service
	 */
	public RequestInspectionResult processRequestInspectors(Service service,
			HttpServletRequest request) {

		RequestInspectionResult result = new RequestInspectionResult();

		// Global inspectors
		for (String item : this.getRequestInspectorImplClassNameList()) {
			try {
				IRequestInspector iri = (IRequestInspector) this
						.createInspectorInstance(item);
				// Run if the Request inspector is global (applicable to all
				// services) OR if this particular service is associated to a
				// specific inspector.
				if (iri != null) {
					if (iri.isGlobal()
							|| iri.getClass().getCanonicalName()
									.equals(service.getRequestInspectorName())) {

						iri.analyze(request);
						result.addResultMessage(iri
								.getPostAnalyzeResultMessage());
					}
				}

			} catch (Exception e) {
				logger.error(
						"Unable to instantiate a class that implements "
								+ IRequestInspector.class.getName()
								+ " with this name: "
								+ service.getRequestInspectorName(), e);
			}
		}
		return result;

	}

	/**
	 * 
	 * @param className
	 * @return Instance of a Class with 'className, if implements
	 *         <code>IRequestInspector</code>, otherwise returns null.
	 */
	public IRequestInspector loadClass(String className) {
		Constructor<?> cs;
		IRequestInspector instance = null;
		try {
			cs = ClassLoader.getSystemClassLoader().loadClass(className)
					.getConstructor();
			instance = (IRequestInspector) cs.newInstance();
		} catch (Exception e) {

			logger.error("Unable to create an instance of a class w/ name "
					+ className, e);
		}

		return instance;
	}

	/**
	 * 
	 * @param className
	 * @return Instance of a Class with 'className, if implements
	 *         <code>IRequestInspector</code>, otherwise returns null.
	 */
	public IRequestInspector createInspectorInstance(String className) {
		Constructor<?> cs;
		IRequestInspector instance = null;
		try {
			Class theClass = Class.forName(className);
			instance = (IRequestInspector) theClass.newInstance();
		} catch (Exception e) {

			logger.error("Unable to create an instance of a class w/ name "
					+ className, e);
		}

		return instance;
	}

	/**
	 * 
	 * @param filePath
	 *            - can be a File (a jar filled with your plugins) or a
	 *            Directory, where Mockey will try to iterate through all found
	 *            sub files.
	 * @throws IOException
	 */
	public void initializeOrUpdateStore(String filePath) {

		
		File x = new File(filePath);

		if (x.isDirectory()) {
			String parentPath = x.getAbsolutePath()
					+ System.getProperty("file.separator");
			for (String childFileName : x.list()) {
				File childFile = new File(parentPath + childFileName);
				logger.debug("Plugin : reading file " + childFile.getAbsolutePath());
				initializeOrUpdateStore(childFile);
			}

		} else {
			initializeOrUpdateStore(x);
		}

	}

	private void initializeOrUpdateStore(File jarFile) {

		try {
			// Step 1. Load jar File
			PluginFileLoaderUtil.addFile(jarFile);
			// Step 2. Get list of class names that implement
			// IRequestInspector
			List<String> validRequestInspectors = PluginFileLoaderUtil
					.getListOfClassesThatImplementIRequestInspector(jarFile);
			if (validRequestInspectors != null) {
				for (String reqInspectImplName : validRequestInspectors) {
					this.saveOrUpdateIReqInspectorImplClassName(reqInspectImplName);
				}
			}

		} catch (IOException e) {
			logger.error(
					"PluginStore: Unable to add plugin file: "
							+ jarFile.getAbsolutePath(), e);

		}

	}

	/**
	 * Looks to the default plug-in directory location to initialize this store
	 */
	public void initializeOrUpdateStore() {
		File pluginDir = new File(BSC.PLUGINDIR);
		if (pluginDir.exists() && pluginDir.isDirectory()) {
			logger.debug("Mockey plugin directory is here:"
					+ pluginDir.getAbsolutePath());
		} else {
			boolean success = pluginDir.mkdir();
			if (!success) {
				logger.error("Unable to create or access the Mockey plugin directory located here: "
						+ pluginDir.getAbsolutePath());
			}
		}
		initializeOrUpdateStore(pluginDir);
	}
}
