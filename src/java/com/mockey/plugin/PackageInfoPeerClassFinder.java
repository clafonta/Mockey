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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;


/**
 * This class is used to search for all classes that exist in a package (i.e.
 * name-space) with a package-info.class as a peer for the purpose of
 * package-level annotations filtering. In other words, we want to find only
 * classes that exist in a package with package-level annotations.
 * 
 * @author chadlafontaine
 * 
 */
class PackageInfoPeerClassFinder {

	private static Logger logger = Logger.getLogger(PackageInfoPeerClassFinder.class);

	/**
	 * Given a path to the Mockey.jar file, and when package-info classes are
	 * found, then all peer classes are gathered into a list.
	 * 
	 * @param pathToJarContainingClasses
	 *            a list of all classes who share a package with a
	 *            package-info.class class.
	 * @return
	 * @throws Exception
	 */
	public static List<PackageInfo> findPackageInfo() throws Exception {
		List<PackageInfo> packageInfoSet = new ArrayList<PackageInfo>();
		List<String> visitedClasses = new ArrayList<String>();
		File jarFile = new File("Mockey.jar");

		if (jarFile.exists()) {

			// STEP 1: go through all classes.
			URL jar = jarFile.toURI().toURL();
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			ZipEntry entry;
			while ((entry = zip.getNextEntry()) != null) {

				if (entry.getName().endsWith("package-info.class")) {
					PackageInfo packageInfo = new PackageInfo(getPackageNameFromPackageInfoClass(entry.getName()));
					packageInfoSet.add(packageInfo);

				} else if (entry.getName().endsWith(".class")) {
					visitedClasses.add(getCleanClassName(entry.getName()));
				}
			}

			// STEP 2: add all classes with matching name-space/package name to
			// the right Package Info
			for (PackageInfo pi : packageInfoSet) {
				for (String className : visitedClasses) {
					if (className.startsWith(pi.getName())) {
						pi.addClassNameToPackage(className);
					}
				}
			}
		} else {
			
			String[] packageListToLoad = new String[] { "com.mockey.plugin",  };
			for (String pName : packageListToLoad) {
				Package p = Package.getPackage(pName);

				if (p != null) {
					PackageInfo pi = new PackageInfo(p.getName());
					pi.addClassNameToPackage(SampleRequestInspector.class.getName());
		
					packageInfoSet.add(pi);
				} else {
					logger.debug("Wow, due to lazy class loading we don't see " 
							+ SampleRequestInspector.class.getName());
				}
			}
		}
		
		
		return packageInfoSet;
	}

	/**
	 * 
	 * @param packageInfoClassName
	 *            - must include the string value 'package-info'.
	 * @return package name in the format xx.yyy.cccc.etc if available, null
	 *         otherwise.
	 */
	public static String getPackageNameFromPackageInfoClass(String packageInfoClassName) {
		String pckge = null;
		if (packageInfoClassName != null) {
			int index = packageInfoClassName.indexOf("package-info");
			if (index > -1) {
				pckge = packageInfoClassName.substring(0, index - 1);
				return getCleanClassName(pckge);
			}
		}
		return pckge;

	}

	/**
	 * Takes a string value and ensures it is has good form. For example:
	 * 
	 * <pre>
	 * 'com/xxx/yyy/ClassName.class' becomes 'com.xxx.yyy.ClassName'
	 * 'com/xxx/yyy/ClassName' becomes 'com.xxx.yyy.ClassName'
	 * 'com.xxx.yyy.ClassName.class' becomes 'com.xxx.yyy.ClassName'
	 * </pre>
	 * 
	 * @param className
	 * @return
	 */
	public static String getCleanClassName(String className) {
		if (className != null) {
			return className.replaceAll("[$].*", "").replaceAll("[.]class", "").replace('/', '.');
		} else {
			return null;
		}
	}

}
