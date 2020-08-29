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
package com.chemistry.enotebook.storage.jdbc.select;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.object.MappingSqlQuery;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * 
 */
public abstract class AbstractSelect extends MappingSqlQuery {
	private static final Log log = LogFactory.getLog(AbstractSelect.class);
	
	public AbstractSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}

	// Subclass is required to implement this method
	public abstract Object mapRow(ResultSet resultSet, int rowNumber) throws SQLException;

	/**
	 * Helper method to extract an element's value if found in a string
	 * @param xmlString
	 * @param tag
	 * @return
	 */
	public String getXMLElementValueFromString(String xmlString, String tag) {
		String result = null;
		if (StringUtils.isNotBlank(xmlString)
				&& StringUtils.isNotBlank(tag)) {
			String startTag = "<" + tag.trim() + ">";
			String endTag = "</" + tag.trim() + ">";
			xmlString = xmlString.trim();
			startTag = startTag.trim();
			endTag = endTag.trim();
			if (xmlString.indexOf(startTag) >= 0) {
				result = xmlString.substring(xmlString.indexOf(startTag)
						+ startTag.length(), xmlString.indexOf(endTag));
			} else {
				log.debug("returning null for value for start tag: " + startTag
						+ " not found in xmlString: " + xmlString);
			}
		} else {
			log.warn("Missing tag info or xml structure from which to get element value:"
							+ "\n"
							+ "xmlString = "
							+ xmlString
							+ "\n"
							+ "tag: " + tag);
		}
		return result;
	}
}
