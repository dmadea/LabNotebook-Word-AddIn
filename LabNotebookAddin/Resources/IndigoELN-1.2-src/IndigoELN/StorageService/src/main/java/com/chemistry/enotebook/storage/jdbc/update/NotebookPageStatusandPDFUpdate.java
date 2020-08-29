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
package com.chemistry.enotebook.storage.jdbc.update;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;

import javax.sql.DataSource;
import java.sql.Types;

public class NotebookPageStatusandPDFUpdate extends SqlUpdate {
	private static final Log log = LogFactory.getLog(NotebookPageStatusandPDFUpdate.class);

	public NotebookPageStatusandPDFUpdate(DataSource dsource) {
		super(dsource, getUpdateSql());
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_STATUS
		//this.declareParameter(new SqlParameter(Types.TIMESTAMP)); // MODIFIED_DATE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // XML_METADATA(XML_TYPE)
		this.declareParameter(new SqlParameter(Types.BINARY)); // PDF_DOCUMENT
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_KEY

	}

	static private String getUpdateSql() {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE CEN_PAGES SET ");
		sql.append("PAGE_STATUS = ?, ");
		sql.append("XML_METADATA = ?, ");
		sql.append("PDF_DOCUMENT = ? ");
		sql.append(" WHERE NBK_REF_VERSION = ?");
		if (log.isDebugEnabled()) {
			log.debug(sql.toString());
		}

		return sql.toString();
	}

}