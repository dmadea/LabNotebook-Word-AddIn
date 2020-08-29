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
package com.chemistry.enotebook.report.birt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.IPlatformContext;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.framework.PlatformServletContext;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

public class BirtEngine {

	private static final Log log = LogFactory.getLog(BirtEngine.class);

	private static Properties configProps = new Properties();

	private final static String configFile = "BirtConfig.properties";

	private static synchronized void initBirtConfig() {
		loadEngineProps();
	}

	public static synchronized IReportEngine createBirtEngine(ServletContext sc) {
		IReportEngine engine = null;
		initBirtConfig();
		EngineConfig config = new EngineConfig();
		if (configProps != null) {
			String logLevel = configProps.getProperty("logLevel");
			Level level = Level.OFF;
			if ("SEVERE".equalsIgnoreCase(logLevel)) {
				level = Level.SEVERE;
			} else if ("WARNING".equalsIgnoreCase(logLevel)) {
				level = Level.WARNING;
			} else if ("INFO".equalsIgnoreCase(logLevel)) {
				level = Level.INFO;
			} else if ("CONFIG".equalsIgnoreCase(logLevel)) {
				level = Level.CONFIG;
			} else if ("FINE".equalsIgnoreCase(logLevel)) {
				level = Level.FINE;
			} else if ("FINER".equalsIgnoreCase(logLevel)) {
				level = Level.FINER;
			} else if ("FINEST".equalsIgnoreCase(logLevel)) {
				level = Level.FINEST;
			} else if ("OFF".equalsIgnoreCase(logLevel)) {
				level = Level.OFF;
			}
			config.setLogConfig(null, level);

		}
		

//        HashMap hm = config.getAppContext();
//        hm.put( EngineConstants.APPCONTEXT_CLASSLOADER_KEY, BirtEngine.class.getClassLoader());
//        config.setAppContext(hm);
	
		config.setEngineHome("");
		IPlatformContext context = new PlatformServletContext(sc);
		config.setPlatformContext(context);

		try {
			log.debug("Platform.startup");
			
			//Platform.setContextClassLoader(BirtEngine.class.getClassLoader());
			Platform.startup(config);
			
		
			IReportEngineFactory factory = (IReportEngineFactory) Platform
					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			if (factory == null) {
				throw new RuntimeException("IReportEngineFactory not initialized");
			}
			engine = factory.createReportEngine(config);
			log.debug("engine created");
		} catch (BirtException e) {
			log.error("BirtEngine creation failure", e);
		}
		return engine;

	}

	public static synchronized void destroyBirtEngine(IReportEngine e) {
		log.debug("destroy");
		e.destroy();
		Platform.shutdown();

	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	private static void loadEngineProps() {

		// Config File must be in classpath
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream in = null;
		in = cl.getResourceAsStream(configFile);

		String line = null;
		try {
			configProps.load(in);
			if (configProps.size() == 0) {
				throw new RuntimeException(configFile + " not loaded");
			}
		} catch (IOException e) {
			log.error("Failed loading BirtEndinge properties from: " + configFile, e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				log.error("Failed closing Input Stream to file: " + configFile, e);
			}
		}

	}

}
