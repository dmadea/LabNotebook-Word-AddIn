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

public class ContainerInsert extends SqlUpdate {
	public ContainerInsert(DataSource dsource, String sqlInsert) {
		super(dsource, sqlInsert);
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // CONTAINER_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // CONTAINER_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // CREATOR_ID
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // CONTAINER_NAME
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // IS_USER_DEFINED
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // X_POSITIONS
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // Y_POSITIONS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // MAJOR_AXIS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // CONTAINER_TYPE
		this.declareParameter(new SqlParameter(Types.VARCHAR));
	}
}
