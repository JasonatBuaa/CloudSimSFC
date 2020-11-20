/*
 * Title:        CloudSimSDN
 * Description:  SDN extension for CloudSim
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2017, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.sdn;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

public class LogWriter {
	private PrintStream out = null;

	private static HashMap<String, LogWriter> map = new HashMap<String, LogWriter>();

	private LogWriter(String name) {
		out = openfile(name);

	}

	/**
	 * Jason: Todo
	 * 
	 * @param name
	 * @return
	 */
	public static LogWriter getLogger(String name) {
		String exName = Configuration.workingDirectory + Configuration.experimentName + name;
		LogWriter writer = map.get(exName);
		if (writer != null)
			return writer;

		// Jason: Todo -- change log directory -- moving into the experiment directory
		System.out.println("Creating logger..:" + exName);
		writer = new LogWriter(exName);
		map.put(exName, writer);

		// if (writer == null) {
		// System.out.println("hello");
		// }
		return writer;

	}

	public void print(String s) {
		if (out == null)
			System.err.println("WorkloadResultWriter: " + s);
		else
			out.print(s);
	}

	public void printLine() {
		if (out == null)
			System.err.println("");
		else
			out.println();
	}

	public void printLine(String s) {
		out.println(s);
	}

	private PrintStream openfile(String name) {
		PrintStream out = null;
		// Jason: create file if not exist
		File file = new File(name);
		if (!file.getParentFile().exists() || !file.exists()) {
			try {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
			} catch (IOException e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}

		// if()
		try {
			out = new PrintStream(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}

	public static String getExtension(String fullPath) {
		int dot = fullPath.lastIndexOf(".");
		return fullPath.substring(dot + 1);
	}

	public static String getBaseName(String fullPath) { // gets filename without extension
		int dot = fullPath.lastIndexOf(".");
		int sep = fullPath.lastIndexOf("/");
		return fullPath.substring(sep + 1, dot);
	}

	public static String getPath(String fullPath) {
		int sep = fullPath.lastIndexOf("/");
		return fullPath.substring(0, sep);
	}
}
