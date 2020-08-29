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
package com.chemistry.enotebook.servicelocator;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ServiceLocator {

    public static final String CEN_DS_JNDI = PropertyReader.DATA_SOURCE;

    private final Object datasourceLock = new Object();
    private final Object serviceLock = new Object();

    private Map<String, Object> datasourceCache = new HashMap<String, Object>();
    private Map<String, Object> serviceCache = new HashMap<String, Object>();

    private static final Object instanceLock = new Object();

    private static ServiceLocator instance;

    private ServiceLocator() {
    }

    public static ServiceLocator getInstance() {
        synchronized (instanceLock) {
            if (instance == null) {
                instance = new ServiceLocator();
            }
            return instance;
        }
    }

    public DataSource defaultDataSource() throws PropertyException, NamingException {
        return locateDataSource(PropertyReader.getJNDI(ServiceLocator.CEN_DS_JNDI));
    }

    public DataSource locateDataSource(String name) throws NamingException {
        synchronized (datasourceLock) {
            Object dataSource = datasourceCache.get(name);

            if (dataSource == null) {
                dataSource = new ClassPathXmlApplicationContext("database/xml/eln-datasource-context.xml").getBean(name);
                datasourceCache.put(name, dataSource);
            }

            return (DataSource) dataSource;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T locateService(String serviceName, Class<T> serviceClass) {
        synchronized (serviceLock) {
            try {
                Object service = serviceCache.get(serviceClass.getName());

                if (service == null) {
                    final Properties properties = new Properties();

                    properties.setProperty("ELN_SERVICE_URL", PropertyReader.getHttpServiceUrl());
                    properties.setProperty("ELN_SERVICE_AUTHORIZATION", getServiceAuthorizationString());
                    properties.setProperty("ELN_SERVICE_NAME", serviceName);
                    properties.setProperty("ELN_SERVICE_CLASS", serviceClass.getName());

                    service = new ClassPathXmlApplicationContext("xml/eln-client-context.xml") {
                        @Override
                        protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
                            super.prepareBeanFactory(beanFactory);
                            ((PropertyPlaceholderConfigurer) beanFactory.getBean("properties")).setProperties(properties);
                        }
                    }.getBean("elnService");

                    serviceCache.put(serviceClass.getName(), service);
                }

                return (T) service;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    public String getServiceAuthorizationString() {
        return "Basic " + new String(Base64.encodeBase64((PropertyReader.getHttpServiceUsername() + ":" + PropertyReader.getHttpServicePassword()).getBytes()));
    }
}
