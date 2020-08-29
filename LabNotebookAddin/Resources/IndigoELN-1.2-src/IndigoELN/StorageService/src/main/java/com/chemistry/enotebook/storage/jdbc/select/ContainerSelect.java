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

import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;



public class ContainerSelect extends AbstractSelect {
	private static final Log log = LogFactory.getLog(ContainerSelect.class);

	public ContainerSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		Container container = new Container(rs.getString("CONTAINER_KEY"));
		container.setContainerCode(rs.getString("CONTAINER_CODE"));
		container.setContainerType(rs.getString("CONTAINER_TYPE"));
		container.setMajorAxis(rs.getString("MAJOR_AXIS"));
		container.setXPositions(rs.getInt("X_POSITIONS"));
		container.setYPositions(rs.getInt("Y_POSITIONS"));
		container.setCreatorId(rs.getString("CREATOR_ID"));
		container.setUserDefined(CommonUtils.toBooleanFromChar(rs.getString("IS_USER_DEFINED")));
		container.setContainerName(rs.getString("CONTAINER_NAME"));
		container.setSkippedWellPositions(CommonUtils.getArrayListFromPipeSeperatedString(rs.getString("SKIP_WELL_POSITIONS")));
		container.setLoadedFromDB(true);
		container.setModelChanged(false);
		return container;
	}
}
