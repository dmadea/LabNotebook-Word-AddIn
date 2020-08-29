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

import com.chemistry.enotebook.domain.ProductPlate;
import com.chemistry.enotebook.domain.container.Container;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisteredPlateSelect extends AbstractSelect {


	ContainerSelect containerSelect = null;
	public RegisteredPlateSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
		containerSelect = new ContainerSelect(dataSource,sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	
		ProductPlate rPlate = new ProductPlate(rs.getString("PLATE_KEY"));
		rPlate.setPlateType(rs.getString("CEN_PLATE_TYPE"));
		rPlate.setPlateBarCode(rs.getString("PLATE_BAR_CODE"));
		rPlate.setContainer((Container)containerSelect.mapRow(rs,rowNum));
		rPlate.setPlateNumber(rs.getString("PLATE_NUMBER"));
		rPlate.setPlateComments(rs.getString("COMMENTS"));
		rPlate.setRegisteredDate(rs.getTimestamp("REGISTERED_DATE"));
		
		rPlate.setLoadedFromDB(true);
		rPlate.setModelChanged(false);
		return rPlate;
	}
}
