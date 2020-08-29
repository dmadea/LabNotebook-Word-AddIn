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

public class ReactionStepUpdate extends SqlUpdate {
	private static final Log log = LogFactory.getLog(ReactionStepUpdate.class);

	public ReactionStepUpdate(DataSource dsource) {
		super(dsource, getUpdateSql());
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // XML_METADATA(XML_TYPE)
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // STEP_KEY
		
	}
	private static String getUpdateSql() {
		StringBuffer updateSql = new StringBuffer();
		updateSql.append("UPDATE CEN_REACTION_STEPS SET ");
		updateSql.append("XML_METADATA=? ");
		updateSql.append(" WHERE STEP_KEY=?");
		log.debug(updateSql);
		return updateSql.toString();
	}
}
