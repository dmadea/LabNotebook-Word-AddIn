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
package com.chemistry.enotebook;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.util.*;

/**
 * Class for read config file service_implementation.properties
 */
public class PropertyReader {

	private static final Log log = LogFactory.getLog(PropertyReader.class);

	public static String SERVICE_IMPL_PROPERTY_FILE = "service_implementation.properties";
	public static String SERVICE_CLEAN_PROPERTY_FILE = "service_clean.properties";

	private static Map<String, Properties> propertiesCache = Collections
			.synchronizedMap(new HashMap<String, Properties>());

	private PropertyReader() {
	}

	public static String getServiceImpl(String serviceName)
			throws PropertyException {
		return getServerProperty(serviceName);
	}

	public static String[] getServiceMultipleImpl(String serviceName)
			throws PropertyException {
		List<String> result = new ArrayList<String>();
		String line = getServerProperty(serviceName);
		if (line != null && line.trim().length() > 0) {
			String[] splitted = line.split(",");
			for (String s : splitted) {
				String trimmed = s.trim();
				if (trimmed.length() > 0) {
					result.add(trimmed);
				}
			}
		}
		return result.toArray(new String[0]);
	}

	private static String getServerProperty(String property)
			throws PropertyException {
		Properties prop = getProperties(SERVICE_IMPL_PROPERTY_FILE);
		if (prop == null)
			prop = getProperties(SERVICE_CLEAN_PROPERTY_FILE);
		if (property != null && property.trim().length() > 0) {
			return prop.getProperty(property);
		}
		return null;
	}

	private static Properties getProperties(String file)
			throws PropertyException {
		Properties prop = (Properties) propertiesCache.get(file);
		if (prop == null) {
			log.info("Retrieving Properties from " + file + " file");
			try {
				InputStream ins = PropertyReader.class.getClassLoader()
						.getResourceAsStream(file);
				if (ins == null) {
					ins = ClassLoader.getSystemResourceAsStream(file);
				}
				if (ins != null) {
					prop = new Properties();
					prop.load(ins);
					propertiesCache.put(file, prop);
				}
			} catch (Exception ex) {
				log.error("Failed to open properties: " + file + "\n"
						+ ex.toString());
				log.error(ex.getMessage());
				throw new PropertyException(
						"PropertyReader::getProperties Could not open the properties file "
								+ file, ex);
			}
		}
		return prop;
	}
}
