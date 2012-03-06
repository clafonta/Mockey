package com.mockey;

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

public class ClassPathHack {
	private static final Class[] parameters = new Class[] { URL.class };

	// FROM
	// http://stackoverflow.com/questions/60764/how-should-i-load-jars-dynamically-at-runtime
	public static void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}

	public static void addFile(File f) throws IOException {

		addURL(f.toURI().toURL());
	}

	public static void addURL(URL u) throws IOException {
		URLClassLoader sysloader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;

		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { u });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException(
					"Error, could not add URL to system classloader");
		}

	}

	public static String[] getInfo(String jarFileName) throws IOException {

		List<String> loadedClasses = new ArrayList<String>();
		JarFile jarFile = new JarFile(jarFileName);
		Enumeration<JarEntry> enumeration = jarFile.entries();
		while (enumeration.hasMoreElements()) {
			process(enumeration.nextElement());
		}
		return (String[]) loadedClasses
				.toArray(new String[loadedClasses.size()]);
	}

	private static void process(JarEntry entry) {

		String name = entry.getName();
		String[] tempClass = name.toString().split(".class");

		try {
			long size = entry.getSize();
			long compressedSize = entry.getCompressedSize();
			System.out.println(name + "\t" + size + "\t" + compressedSize);
			if (tempClass.length > 1) {
				String cleanName = tempClass[0].replace("/", ".").replace('\\', '.');
				Class clazz = (Class) Class.forName(cleanName);

				// Class interFace =
				// Class.forName("com.mockey.model.IRequestInspector");
				Class interFace = Class
						.forName("com.mockey.model.PersistableItem");

				boolean match = !clazz.isInterface() && !clazz.isEnum()
						&& interFace.isAssignableFrom(clazz);

				if (match) {
					System.out
							.println("XXXXXX Found class with matching interface: "
									+ name);

				}
			}

		} catch (Exception e) {
			System.out.println("Unable to process entry with name '" + name
					+ "' -- here's the error:" + e);
		}

	}

	public static void main(String[] args) throws ClassNotFoundException {
		String name = "com/mockey/model/Service.class";
		String[] tempClass = name.toString().split(".class");
		
		
		String cleanName = tempClass[0].replace("/", ".").replace('\\', '.');
		Class clazz = (Class) Class.forName(cleanName);
		Class interFace = Class
				.forName("com.mockey.model.PersistableItem");
		boolean match = !clazz.isInterface() && !clazz.isEnum()
				&& interFace.isAssignableFrom(clazz);
		System.out.println(match);
	}
}
