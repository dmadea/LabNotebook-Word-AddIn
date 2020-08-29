/****************************************************************************
 * Copyright (C) 2009-2015 EPAM Systems
 * 
 * This file is part of Indigo ELN.
 * 
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.GPL included in the
 * packaging of this file.
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/
/*
 * Created on Sep 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.client.gui.common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class GetEnv {
	private static Properties envVars = null;

	// public Properties getEnvProperties()
	// throws Exception
	// {
	// Properties envProps = new Properties();
	//		
	// // get runtime ...
	// Runtime r = Runtime.getRuntime();
	//		
	// // start new shell with /c switch and parameters( only work around
	// for win...)
	// // for linux/unix... get OS type and invoke Shell accordingly...
	// Process p = r.exec("cmd /c set>temp.env");
	//		
	// // Give some time for above process to finish, say 0.5 sec
	// Thread.sleep(500);
	//		
	// // load property with this temp file...
	// FileInputStream in = new FileInputStream("temp.env");
	// envProps.load(in);
	// in.close();
	//		
	// // clean up the mesh....
	// new File("temp.env").delete();
	//		
	// // return the environment properties...
	// return envProps;
	// }
	public static Properties getProperties() throws Exception {
		if (envVars == null)
			envVars = getEnvProperties();
		return envVars;
	}

	private static Properties getEnvProperties() throws Exception {
		Process p = null;
		Properties envVars = new Properties();
		Runtime r = Runtime.getRuntime();
		String OS = System.getProperty("os.name").toLowerCase();
		// //System.out.println(OS);
		if (OS.indexOf("windows 9") > -1) {
			p = r.exec("command.com /c set");
		} else if ((OS.indexOf("nt") > -1) || (OS.indexOf("windows 2000") > -1) || (OS.indexOf("windows xp") > -1)
				|| (OS.indexOf("windows 2003") > -1)) {
			// thanks to JuanFran for the xp fix!
			p = r.exec("cmd.exe /c set");
		} else {
			// our last hope, we assume Unix (thanks to H. Ware for the fix)
			p = r.exec("env");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = br.readLine()) != null) {
			int idx = line.indexOf('=');
			String key = line.substring(0, idx);
			String value = line.substring(idx + 1);
			envVars.setProperty(key, value);
			// //System.out.println( key + " = " + value );
		}
		return envVars;
	}

	public static void main(String[] args) {
		try {
			// Usage of GetEnv Class...
			Properties envProps = GetEnv.getProperties();
			Enumeration names = envProps.propertyNames();
			for (Enumeration e = names; e.hasMoreElements();) {
				String name = (String) e.nextElement();
				String prop = (String) envProps.getProperty(name);
				// System.out.println(name + " = " + prop);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
