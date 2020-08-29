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

import com.chemistry.enotebook.domain.ProcedureImage;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProcedureImageSelect extends AbstractSelect {

	public ProcedureImageSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		ProcedureImage newImage = new ProcedureImage(rs.getString("IMAGE_KEY"), rs.getString("IMAGE_TYPE"));

		newImage.setImageData(rs.getBytes("IMAGE_DATA"));
		
		return newImage;
	}
}
