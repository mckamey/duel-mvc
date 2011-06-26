package com.example.mvcapp.aspects;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.jar.*;

/**
 * Adapted from:
 * http://internna.blogspot.com/2007/11/java-5-retrieving-all-classes-from.html
 */
public final class ClassEnumerator {

	public static Set<Class<?>> getClasses(String packageName)
			throws IOException, ClassNotFoundException {

		return getClasses(Thread.currentThread().getContextClassLoader(), packageName);
	}

	public static Set<Class<?>> getClasses(ClassLoader loader, String packageName)
			throws IOException, ClassNotFoundException {

		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = loader.getResources(path);
		if (resources == null) {
			return Collections.emptySet();
		}

		Set<Class<?>> classes = new HashSet<Class<?>>();
		while (resources.hasMoreElements()) {
			String filePath = resources.nextElement().getFile();
			if (filePath == null) {
				continue;
			}

			// decode URL encoded whitespace
			filePath = filePath.replaceAll("%20", " ");

			int bang = filePath.indexOf("!");
			if ((bang > 0) & (filePath.lastIndexOf(".jar") > 0)) {
				String jarPath = filePath.substring(0, bang).substring(filePath.lastIndexOf(":")+1);
				classes.addAll(getFromJARFile(jarPath, path));

			} else {
				classes.addAll(getFromDirectory(new File(filePath), packageName));
			}
		}

		return classes;
	}

	public static Set<Class<?>> getFromDirectory(File directory, String packageName)
			throws ClassNotFoundException {

		Set<Class<?>> classes = new HashSet<Class<?>>();
		if (directory.exists()) {
			for (String file : directory.list()) {
				if (file.endsWith(".class")) {
					String name = packageName + '.' + file.substring(0, file.lastIndexOf("."));;
					classes.add(Class.forName(name));
				}
			}
		}
		return classes;
	}

	public static Set<Class<?>> getFromJARFile(String jar, String packageName)
			throws FileNotFoundException, IOException, ClassNotFoundException {

		JarInputStream jarFile = new JarInputStream(new FileInputStream(jar));
		Set<Class<?>> classes = new HashSet<Class<?>>();

		JarEntry jarEntry;
		while ((jarEntry = jarFile.getNextJarEntry()) != null) {
			String className = jarEntry.getName();
			if (className.endsWith(".class")) {
				className = className.substring(0, className.lastIndexOf("."));
				if (className.startsWith(packageName)) {
					classes.add(Class.forName(className.replace('/', '.')));
				}
			}
		}

		return classes;
	}
}
