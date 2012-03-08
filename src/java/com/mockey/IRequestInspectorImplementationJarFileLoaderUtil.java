package com.mockey;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.mockey.model.IRequestInspector;

/**
 * 
 * @author clafonta REFERENCE:
 *         http://stackoverflow.com/questions/60764/how-should
 *         -i-load-jars-dynamically-at-runtime
 */
public class IRequestInspectorImplementationJarFileLoaderUtil {
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
		addURL(f.toURI().toURL());
	}// end method

	/**
	 * Adds the content pointed by the URL to the classpath.
	 * 
	 * @param u
	 *            the URL pointing to the content to be added
	 * @throws IOException
	 */
	public static void addURL(URL u) throws IOException {
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

	/**
	 * 
	 * @param jarFileName
	 *            - Jar file, hopefully, contains 1 or more classes that
	 *            implement <code>IRequestInspector</code>
	 * @return
	 * @throws IOException
	 */
	public static String[] getListOfClassesThatImplementIRequestInspector(
			String jarFileName) throws IOException {

		List<String> loadedClasses = new ArrayList<String>();
		JarFile jarFile = new JarFile(jarFileName);
		Enumeration<JarEntry> enumeration = jarFile.entries();
		while (enumeration.hasMoreElements()) {
			String x = process(enumeration.nextElement());
			if (x != null) {
				loadedClasses.add(x);
			}
		}
		return (String[]) loadedClasses
				.toArray(new String[loadedClasses.size()]);
	}

	/**
	 * 
	 * @param entry
	 * @return Class name if implements <code>IRequestInspector</code>
	 * @see com.mockey.model.IRequestInspector
	 */
	private static String process(JarEntry entry) {

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
				Class interFace = Class.forName(IRequestInspector.class
						.getName());

				boolean match = !clazz.isInterface() && !clazz.isEnum()
						&& interFace.isAssignableFrom(clazz);

				if (match) {
					classThatImplementsRequestInspector = cleanName;

				}
			}

		} catch (Exception e) {
			System.out.println("Unable to process entry with name '" + name
					+ "' -- here's the error:" + e);
		}
		return classThatImplementsRequestInspector;
	}

	/**
	 * 
	 * @param className
	 * @return Instance of a Class with 'className, if implements
	 *         <code>IRequestInspector</code>, otherwise returns null.
	 */
	public static IRequestInspector getRequestInspectorInstance(String className) {
		Constructor<?> cs;
		IRequestInspector instance = null;
		try {
			cs = ClassLoader.getSystemClassLoader().loadClass(className)
					.getConstructor();
			instance = (IRequestInspector) cs.newInstance();
		} catch (Exception e) {

			e.printStackTrace();
		}

		return instance;
	}

	public static void main(String args[]) throws IOException,
			SecurityException, ClassNotFoundException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {

		// Second test
		String jarFileName = "MobileAnalytics.jar";

		addFile(jarFileName);
		String[] infoList = getListOfClassesThatImplementIRequestInspector(jarFileName);
		for (String item : infoList) {
			System.out.println("Things that implent:" + item);
		}

		IRequestInspector instance = getRequestInspectorInstance("com.mep.analytics.AnalyticsAnalyzer");

		System.out.println("Class type is:"
				+ instance.getClass().getCanonicalName());
		System.out.println("Done");

	}
}
