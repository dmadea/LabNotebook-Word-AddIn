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

import com.chemistry.enotebook.domain.PseudoProductPlate;
import com.chemistry.enotebook.domain.container.Container;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PseudoProductPlateSelect extends AbstractSelect {
	private static final Log log = LogFactory.getLog(PseudoProductPlateSelect.class);

	ContainerSelect containerSelect = null;
	public PseudoProductPlateSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
		containerSelect = new ContainerSelect(dataSource,sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	
		PseudoProductPlate pPlate = new PseudoProductPlate(rs.getString("PLATE_KEY"));
		pPlate.setPlateType(rs.getString("CEN_PLATE_TYPE"));
		pPlate.setPlateBarCode(rs.getString("PLATE_BAR_CODE"));
		pPlate.setContainer((Container)containerSelect.mapRow(rs,rowNum));
		pPlate.setPlateNumber(rs.getString("PLATE_NUMBER"));
		pPlate.setPlateComments(rs.getString("COMMENTS"));
		pPlate.setRegisteredDate(rs.getTimestamp("REGISTERED_DATE"));
		pPlate.setLoadedFromDB(true);
		pPlate.setModelChanged(false);
		return pPlate;
	}

}