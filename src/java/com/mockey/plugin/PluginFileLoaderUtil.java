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
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

/**
 * Loads a Jar file.
 * 
 * @author clafonta REFERENCE:
 *         http://stackoverflow.com/questions/60764/how-should
 *         -i-load-jars-dynamically-at-runtime
 */
public class PluginFileLoaderUtil {
	private static Logger logger = Logger.getLogger(PluginFileLoaderUtil.class);

	/**
	 * Parameters of the method to add an URL to the System classes.
	 */
	private static final Class<?>[] parameters = new Class[] { URL.class };

	/**
	 * Adds a file to the classpath.
	 * 
	 * @param s
	 *            a String pointing to the file
	 * @throws IOException
	 */
	public static void addFile(String s) throws IOException {

		File f = new File(s);
		addFile(f);

	}// end method

	/**
	 * Adds a file to the classpath
	 * 
	 * @param f
	 *            the file to be added
	 * @throws IOException
	 */
	public static void addFile(File f) throws IOException {

		if (f.isDirectory()) {
			for (String childFileName : f.list()) {
				File childFile = new File(f.getAbsolutePath()
						+ System.getProperty("file.separator") + childFileName);
				addURL(childFile.toURI().toURL());
			}
		} else {
			addURL(f.toURI().toURL());
		}

	}// end method

	/**
	 * Adds the content pointed by the URL to the classpath.
	 * 
	 * @param u
	 *            the URL pointing to the content to be added
	 * @throws IOException
	 */
	private static void addURL(URL u) throws IOException {
		URLClassLoader sysloader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;
		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { u });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException(
					"Error, could not add URL to system classloader");
		}// end try catch
	}// end method

	public static List<String> getListOfClassesThatImplementIRequestInspector(
			File file) {
		List<String> clazzList = new ArrayList<String>();
		if (file.isDirectory()) {
			for (String childFileName : file.list()) {
				File childFile = new File(file.getAbsolutePath()
						+ System.getProperty("file.separator") + childFileName);
				List<String> subChildClassList = getListOfClassesThatImplementIRequestInspector(
						childFile, IRequestInspector.class.getName());
				for (String x : subChildClassList) {
					clazzList.add(x);
				}
			}
		} else {
			clazzList = getListOfClassesThatImplementIRequestInspector(file,
					IRequestInspector.class.getName());
		}
		return clazzList;
	}

	/**
	 * 
	 * @param jarFileName
	 *            - Jar file, hopefully, contains 1 or more classes that
	 *            implement <code>IRequestInspector</code>
	 * @return
	 * @throws IOException
	 */
	private static List<String> getListOfClassesThatImplementIRequestInspector(
			File file, String interfaceName) {

		try {
			List<String> loadedClasses = new ArrayList<String>();
			JarFile jarFile = new JarFile(file);
			Enumeration<JarEntry> enumeration = jarFile.entries();
			while (enumeration.hasMoreElements()) {
				String x = process(enumeration.nextElement(), interfaceName);
				if (x != null) {
					loadedClasses.add(x);
				}
			}
			return loadedClasses;
		} catch (Exception e) {
			logger.error("Plugin loader: unable to parse/load jar of name: "
					+ file.getAbsolutePath(), e);
		}
		return null;
	}

	/**
	 * 
	 * @param entry
	 * @return Class name if implements <code>IRequestInspector</code>
	 * @see com.mockey.plugin.IRequestInspector
	 */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private static String process(JarEntry entry, String interfaceName) {

		String classThatImplementsRequestInspector = null;
		String name = entry.getName();
		String[] tempClass = name.toString().split(".class");

		try {
			long size = entry.getSize();
			long compressedSize = entry.getCompressedSize();
			// System.out.println(name + "\t" + size + "\t" + compressedSize);
			if (tempClass.length >= 1 && name.endsWith(".class")) {
				String cleanName = tempClass[0].replace("/", ".").replace('\\',
						'.');
				Class clazz = (Class) Class.forName(cleanName);
				Class interFace = Class.forName(interfaceName);
				logger.debug("Plugin: processing " + cleanName);
				boolean match = !clazz.isInterface() && !clazz.isEnum()
						&& interFace.isAssignableFrom(clazz);

				if (match) {
					logger.debug("Plugin: valid implementation of  "
							+ interfaceName + ": "
							+ cleanName);
					classThatImplementsRequestInspector = cleanName;

				}
			}

		} catch (java.lang.NoClassDefFoundError ncdfe) {
			logger.error("Plugin: missing class? Or could already be loaded.",
					ncdfe);
		} catch (Exception e) {
			logger.error("Unable to process entry with name '" + name
					+ "' -- here's the error:" + e, e);
		}
		return classThatImplementsRequestInspector;
	}

}
