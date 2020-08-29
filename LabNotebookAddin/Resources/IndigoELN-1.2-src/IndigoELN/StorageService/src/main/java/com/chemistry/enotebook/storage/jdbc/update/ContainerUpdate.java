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

public class ContainerUpdate extends SqlUpdate {
	private static final Log log = LogFactory.getLog(ContainerUpdate.class);

	public ContainerUpdate(DataSource dsource) {
		super(dsource, getUpdateSql());
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // CREATOR_ID
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // CONTAINER_NAME
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // IS_USER_DEFINED
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // X_POSITIONS
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // Y_POSITIONS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // MAJOR_AXIS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // CONTAINER_TYPE
		this.declareParameter(new SqlParameter(Types.VARCHAR));
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // CONTAINER_CODE

	}

	static protected String getUpdateSql() {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE CEN_CONTAINER SET ");
		sql.append("CREATOR_ID = ?, ");
		sql.append("CONTAINER_NAME = ?, ");
		sql.append("IS_USER_DEFINED = ?, ");
		sql.append("X_POSITIONS = ?, ");
		sql.append("Y_POSITIONS = ?, ");
		sql.append("MAJOR_AXIS = ?, ");
		sql.append("CONTAINER_TYPE = ? ,");
		sql.append("SKIP_WELL_POSITIONS = ? ");
		sql.append(" WHERE CONTAINER_CODE = ? ");

		if (log.isDebugEnabled()) {
			log.debug(sql.toString());
		}

		return sql.toString();
	}
//
//	public String getContainerUpdateQuery(Container container) {
//		StringBuffer updateSql = new StringBuffer();
//		ArrayList updateParams = new ArrayList();
//		updateSql.append("UPDATE CEN_CONTAINER SET ");
//		if (CommonUtils.isNotNull(container.getCreatorId()))
//			updateParams.add("CREATOR_ID ='" + container.getCreatorId() + "'");
//		if (CommonUtils.isNotNull(container.getContainerName()))
//			updateParams.add("CONTAINER_NAME ='" + container.getContainerName() + "'");
//
//		updateParams.add("IS_USER_DEFINED ='" + CommonUtils.toCharFromBoolean(container.isUserDefined()) + "'");
//		updateParams.add("X_POSITIONS =" + container.getXPositions());
//		updateParams.add("Y_POSITIONS =" + container.getYPositions());
//		if (CommonUtils.isNotNull(container.getMajorAxis()))
//			updateParams.add("MAJOR_AXIS ='" + container.getMajorAxis() + "'");
//		if (CommonUtils.isNotNull(container.getContainerType()))
//			updateParams.add("CONTAINER_TYPE ='" + container.getContainerType() + "'");
//		updateSql.append(getCommaDelimitedString(updateParams));
//		updateSql.append("WHERE CONTAINER_CODE ='" + container.getContainerCode() + "'");
//		log.debug(updateSql);
//		// System.out.println(updateSql);
//		return updateSql.toString();
//	}
}
