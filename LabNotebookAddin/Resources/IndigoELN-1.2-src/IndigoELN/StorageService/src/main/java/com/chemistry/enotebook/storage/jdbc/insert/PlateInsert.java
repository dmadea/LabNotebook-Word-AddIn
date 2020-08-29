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
package com.chemistry.enotebook.storage.jdbc.insert;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;

import javax.sql.DataSource;
import java.sql.Types;


public abstract class PlateInsert extends SqlUpdate {

	public PlateInsert(DataSource dsource, String sqlInsert) {
		super(dsource, sqlInsert);
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PLATE_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // CEN_PLATE_TYPE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // CONTAINER_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PLATE_NUMBER
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PLATE_BAR_CODE
	}
}