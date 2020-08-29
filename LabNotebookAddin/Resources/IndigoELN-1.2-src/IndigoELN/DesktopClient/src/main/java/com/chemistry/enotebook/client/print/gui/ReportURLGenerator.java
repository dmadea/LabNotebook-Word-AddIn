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
package com.chemistry.enotebook.client.print.gui;

import com.chemistry.enotebook.experiment.utils.SystemPropertyException;
import com.chemistry.enotebook.servicelocator.PropertyReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ReportURLGenerator {

	public static final Log log = LogFactory.getLog(ReportURLGenerator.class);

	public final static String PDF = "pdf";
	public final static String RTF = "rtf";
	public final static String DOC = "doc";
	public final static String AMPERSAND = "&";
	private Map<String, String> parameterMap = new HashMap<String, String>();

	public void dispose() {
		parameterMap.clear();
		parameterMap = null;
	}

	public void addParameter(String name, String value) {
		parameterMap.put(name, value);
	}

	public String getURL() throws SystemPropertyException {
		String result = null;
		try {

			StringBuffer buff = new StringBuffer(PropertyReader.getReportUrl());

			//for debug
			//buff = new StringBuffer("http://localhost:7001/report/run");
			
			buff.append("?");
			for (String key : parameterMap.keySet()) {
				buff.append(key).append("=").append(this.parameterMap.get(key)).append(AMPERSAND);
			}
			buff = buff.deleteCharAt(buff.length() - 1); // delete the last ampersand
			URL url = new URL(buff.toString());
			result = url.toString();
			log.debug("Report URL:  " + result);
		} catch (Exception e) {
			log.error("Failed creating URL for report service", e);
			throw new SystemPropertyException("Failed to create report service URL", e);
		}
		return result;
	}

	public boolean isComplete() {
		if (!parameterExists(PrintSetupConstants.REPORT_NAME))
			return false;
		if (!parameterExists(PrintSetupConstants.NOTEBOOK_NUMBER))
			return false;
		if (!parameterExists(PrintSetupConstants.PAGE_NUMBER))
			return false;
		// if (! parameterExists(PrintSetupConstants.OUTPUT_FORMAT))
		// return false;
		return true;
	}

	private boolean parameterExists(String paramName) {
		Object obj = this.parameterMap.get(paramName);
		if (obj == null)
			return false;
		if (obj instanceof String) {
			String s = (String) obj;
			if (s.length() == 0)
				return false;
		}
		return true;
	}

}
