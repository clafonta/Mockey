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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;

import com.mockey.model.RequestFromClient;
import com.mockey.model.Service;

/**
 * Manages plugins.
 * 
 * @author chadlafontaine
 * 
 */
public class PluginStore {
	private static Logger logger = Logger.getLogger(PluginStore.class);
	private static PluginStore pluginStoreInstance = new PluginStore();
	private List<Class<?>> reqInspectorClassNameList = new ArrayList<Class<?>>();

	/**
	 * Basic singleton.
	 * 
	 * @return
	 */
	public static PluginStore getInstance() {
		return PluginStore.pluginStoreInstance;
	}

	/**
	 * Basic constructor
	 */
	private PluginStore() {

	}

	/**
	 * 
	 * @return a list of found Class objects that implement
	 */
	public List<Class<?>> getRequestInspectorImplClassList() {
		return this.reqInspectorClassNameList;
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
			RequestFromClient request) {

		RequestInspectionResult result = new RequestInspectionResult();

		// Global Java inspectors
		for (Class<?> item : this.getRequestInspectorImplClassList()) {
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

		// JSON Inspectors
		if (service.isRequestInspectorJsonRulesEnableFlag()) {

			try {
				RequestInspectorDefinedByJson jsonRulesInspector = new RequestInspectorDefinedByJson(
						service.getRequestInspectorJsonRules());
				jsonRulesInspector.analyze(request);
				result.addResultMessage(jsonRulesInspector
						.getPostAnalyzeResultMessage());
			} catch (JSONException e) {
				String msg = "Unable to parse JSON rules from service: "
						+ service.getServiceName();
				result.addResultMessage(msg);
				logger.debug(msg, e);
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
	private Class<?> doesThisImplementIRequestInspector(String className) {

		try {
			try {
				// HACK:
				Class<?> xx = Class.forName(className);
				if (!xx.getName().equalsIgnoreCase(
						RequestInspectorDefinedByJson.class.getName()) && (!xx.isInterface()
						&& IRequestInspector.class.isAssignableFrom(xx)) ) {
					return xx;
				}
			} catch (ClassNotFoundException e) {
				Class<?> xx = ClassLoader.getSystemClassLoader().loadClass(
						className);
				if (!xx.isInterface()
						&& IRequestInspector.class.isAssignableFrom(xx)) {
					return xx;
				}
			}
		} catch (java.lang.NoClassDefFoundError classDefNotFound) {
			logger.debug("Unable to create class: " + className
					+ "; reason: java.lang.NoClassDefFoundError");
		} catch (Exception e) {
			logger.error("Unable to create an instance of a class w/ name "
					+ className, e);
		}

		return null;
	}

	/**
	 * 
	 * @param className
	 * @return Instance of a Class with 'className, if implements
	 *         <code>IRequestInspector</code>, otherwise returns null.
	 */
	private IRequestInspector createInspectorInstance(Class<?> clazz) {

		IRequestInspector instance = null;
		// HACK:
		if (!clazz.getName().equalsIgnoreCase(
				RequestInspectorDefinedByJson.class.getName())) {

			try {
				if (!clazz.isInterface()
						&& IRequestInspector.class.isAssignableFrom(clazz)) {
					instance = (IRequestInspector) clazz.newInstance();
				}
			} catch (Exception e) {

				logger.error("Unable to create an instance of a class w/ name "
						+ clazz.getName(), e);
			}
		}
		return instance;
	}

	/**
	 * Looks to the default plug-in directory location to initialize this store
	 */
	public void initializeOrUpdateStore() {

		try {
			List<PackageInfo> list = PackageInfoPeerClassFinder
					.findPackageInfo();
			for (PackageInfo pi : list) {
				for (String className : pi.getClassList()) {

					try {
						// If we don't have the class
						Class<?> o = Class.forName(className);
						if (o == null) {
							throw new Exception("Class not available");
						}
					} catch (NoClassDefFoundError ncdfe) {
						// By Design: gobbling up this error to reduce the
						// non-needed noise upon startup. If there is a real
						// issue, then it will bubble up somewhere else.
					} catch (Exception e) {
						// Explicitly load classes from packages that have
						// package-info
						try {
							ClassLoader.getSystemClassLoader().loadClass(
									className);
						} catch (java.lang.NoClassDefFoundError ncdfe) {
							// By Design: gobbling up this error to reduce the
							// non-needed noise upon startup. If there is a real
							// issue, then it will bubble up somewhere else.
						}
					}

					Package packageItem = Package.getPackage(pi.getName());
					if (null != packageItem
							.getAnnotation(MockeyRequestInspector.class)) {
						Class<?> x = doesThisImplementIRequestInspector(className);
						if (x != null
								&& !this.reqInspectorClassNameList.contains(x)) {
							this.reqInspectorClassNameList.add(x);
							logger.debug("Plugin added: " + className);
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("Found a Mockey.jar, but unable read mockey jar", e);
		}
	}

}
