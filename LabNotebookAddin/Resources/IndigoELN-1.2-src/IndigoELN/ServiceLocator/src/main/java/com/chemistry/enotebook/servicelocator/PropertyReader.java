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
 * Created on Aug 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.servicelocator;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyReader {

    public static final String PROPERTIES_FILE = "application.properties";

    public static final String DATA_SOURCE = "DATASOURCE";
    
    public static final String SERVICE_URL = "SERVICE_URL";
    public static final String SERVICE_USERNAME = "SERVICE_USERNAME";
    public static final String SERVICE_PASSWORD = "SERVICE_PASSWORD";

    private static final Map<String, Properties> propertiesCache = new HashMap<String, Properties>();
    private static final Object propertiesLock = new Object();
    
    public static String getHttpServiceUrl() {
        try {
            return getProperties(PROPERTIES_FILE).getProperty(SERVICE_URL);
        } catch (Exception e) {
            return "http://localhost:8080/indigoeln";
        }
    }
    
    public static String getHttpServiceUsername() {
        try {
            return getProperties(PROPERTIES_FILE).getProperty(SERVICE_USERNAME);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String getHttpServicePassword() {
        try {
            return getProperties(PROPERTIES_FILE).getProperty(SERVICE_PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String getReportUrl() {
        return getHttpServiceUrl() + "/report";
    }

    public static synchronized String getJNDI(String name) throws PropertyException {
        try {
            return getProperties(PROPERTIES_FILE).getProperty(name);
        } catch (Exception e) {
            throw new PropertyException(e.getMessage(), e);
        }
    }

    public static Properties getProperties(String fileName) throws Exception {
        synchronized (propertiesLock) {
            Properties properties = propertiesCache.get(fileName);
            
            if (properties == null) {
                InputStream is = null;

                try {
                    is = ServiceLocator.class.getClassLoader().getResourceAsStream(fileName);

                    properties = new Properties();
                    properties.load(is);

                    propertiesCache.put(fileName, properties);
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            }
            
            return properties;
        }
    }
}
