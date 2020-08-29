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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;



public class CompoundManagementContainerSelect extends AbstractSelect {
	private static final Log log = LogFactory.getLog(ContainerSelect.class);

	//Key is container code.Value is a ArrayList
	private HashMap skipWellPosMap = new HashMap(100);
	
	public CompoundManagementContainerSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		Container container = new Container();
		container.setContainerCode(rs.getString("CONTAINER_TYPE_CODE"));
		container.setContainerName(rs.getString("CONTAINER_TYPE_DESCR"));
		container.setContainerType(rs.getString("CONTAINER_TYPE"));
		container.setMajorAxis(rs.getString("MAJOR_AXIS"));
		container.setXPositions(rs.getInt("X_POSITIONS"));
		container.setYPositions(rs.getInt("Y_POSITIONS"));
		container.setSiteCode(rs.getString("SITE_CODE"));
		if(container.getXPositions() == 0 || container.getYPositions() == 0)
		{
			container.setXPositions(rs.getInt("rack_x_positions"));
			container.setYPositions(rs.getInt("rack_y_positions"));	
		}
		container.setUserDefined(false);
		container.setLoadedFromDB(true);
		container.setModelChanged(false);
		String wellPos = rs.getString("WELL_POSITION");
		if(skipWellPosMap.containsKey(container.getContainerCode()))
		{
			ArrayList list = (ArrayList)skipWellPosMap.get(container.getContainerCode());
			list.add(wellPos);
		}else
		{
			ArrayList list = new ArrayList();
			list.add(wellPos);
			skipWellPosMap.put(container.getContainerCode(),list);
		}
		return container;
	}

	public HashMap getSkipWellPosMap() {
		return skipWellPosMap;
	}

	public void setSkipWellPosMap(HashMap skipWellPosMap) {
		this.skipWellPosMap = skipWellPosMap;
	}
	
	
}

