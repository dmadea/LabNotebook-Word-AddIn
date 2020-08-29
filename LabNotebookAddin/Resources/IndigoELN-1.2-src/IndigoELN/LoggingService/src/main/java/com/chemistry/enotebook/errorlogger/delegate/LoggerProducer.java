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
package com.chemistry.enotebook.errorlogger.delegate;

import com.chemistry.enotebook.errorlogger.interfaces.LoggingService;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggerProducer {

    private static final Log log = LogFactory.getLog(LoggerProducer.class);
    
    public static final String WARNING = "Warning";
    public static final String EXCEPTION = "Exception";
    public static final String INFO = "Information";
    public static final String METRICS = "Metrics";

    public static void logMessage(final String xmlMessage) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    ServiceLocator.getInstance().locateService("LoggingService", LoggingService.class).log(xmlMessage);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }).start();
    }
}
