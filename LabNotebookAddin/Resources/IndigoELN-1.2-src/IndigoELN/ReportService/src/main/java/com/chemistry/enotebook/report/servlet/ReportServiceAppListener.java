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
package com.chemistry.enotebook.report.servlet;

import com.chemistry.enotebook.report.birt.BirtEngine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.report.engine.api.IReportEngine;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ReportServiceAppListener implements ServletContextListener {

	public static final Log log = LogFactory.getLog(ReportServiceAppListener.class);

	/* Application Startup Event */
	//TODO = may be move to servlet init?
	public void contextInitialized(ServletContextEvent ce) {
		try {

			log.debug("contextInitialized");
			ServletContext context = ce.getServletContext();
			if(context==null){
				log.debug("ServletContext is null");
			}
			IReportEngine engine = (IReportEngine) context.getAttribute("birtEngine");
			if (engine == null) {
				engine = BirtEngine.createBirtEngine(context);
				context.setAttribute("birtEngine", engine);
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

	/* Application Shutdown Event */
	public void contextDestroyed(ServletContextEvent ce) {
		log.debug("contextDestroyed");
		ServletContext context = ce.getServletContext();
		IReportEngine engine = (IReportEngine) context.getAttribute("birtEngine");
		if (engine != null) {
			BirtEngine.destroyBirtEngine(engine);
			context.setAttribute("birtEngine", null);
		}
	}
}
