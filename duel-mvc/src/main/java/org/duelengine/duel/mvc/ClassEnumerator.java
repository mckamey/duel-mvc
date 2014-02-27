package org.duelengine.duel.mvc;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

/**
 * Utility for enumerating available classes in a particular package
 */
final class ClassEnumerator {

	private static final String CLASS_FILE = ".class";
	private static final String JAR_FILE = ".jar";

	/**
	 * Loads classes in the specified package from the current thread ClassLoader
	 * @param packageName
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Set<Class<?>> getClasses(String packageName)
			throws IOException, ClassNotFoundException {

		return getClasses(packageName, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Loads classes in the specified package from the specified ClassLoader
	 * @param packageName
	 * @param loader
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Set<Class<?>> getClasses(String packageName, ClassLoader loader)
			throws IOException, ClassNotFoundException {

		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = loader.getResources(path);
		if (resources == null) {
			return Collections.emptySet();
		}

		Set<Class<?>> classes = new HashSet<Class<?>>();
		while (resources.hasMoreElements()) {
			String filePath = resources.nextElement().getPath();
			if (filePath == null) {
				continue;
			}

			// decode URL encoded characters
			filePath = URLDecoder.decode(filePath, "UTF-8");

			int bang = filePath.indexOf('!');
			if ((bang > 0) & (filePath.lastIndexOf(JAR_FILE) > 0)) {
				String jarPath = filePath.substring(0, bang).substring(filePath.lastIndexOf(':')+1);
				classes.addAll(getFromJAR(path, jarPath));

			} else {
				classes.addAll(getFromDirectory(packageName, new File(filePath)));
			}
		}

		return classes;
	}

	/**
	 * Loads classes in the specified package from the specified directory
	 * @param packageName
	 * @param directory
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Set<Class<?>> getFromDirectory(String packageName, File directory)
			throws ClassNotFoundException {

		Set<Class<?>> classes = new HashSet<Class<?>>();
		if (directory.exists()) {
			for (String file : directory.list()) {
				if (file.endsWith(CLASS_FILE)) {
					String name = packageName + '.' + file.substring(0, file.lastIndexOf('.'));;
					classes.add(Class.forName(name));
				}
			}
		}
		return classes;
	}

	/**
	 * Loads classes in the specified package from the specified JAR
	 * @param packageName
	 * @param jar
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Set<Class<?>> getFromJAR(String packageName, String jar)
			throws FileNotFoundException, IOException, ClassNotFoundException {

		Set<Class<?>> classes = new HashSet<Class<?>>();
		JarInputStream jarFile = new JarInputStream(new FileInputStream(jar));
		try {

			JarEntry jarEntry;
			while ((jarEntry = jarFile.getNextJarEntry()) != null) {
				String className = jarEntry.getName();
				if (className.endsWith(CLASS_FILE)) {
					className = className.substring(0, className.lastIndexOf('.'));
					if (className.startsWith(packageName)) {
						classes.add(Class.forName(className.replace('/', '.')));
					}
				}
			}
			
		} finally {
			if (jarFile != null) {
				jarFile.close();
			}
		}

		return classes;
	}
}
