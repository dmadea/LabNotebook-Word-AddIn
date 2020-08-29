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

public class StepBatchListInsert extends SqlUpdate {
	public StepBatchListInsert(DataSource dsource, String sqlInsert) {
		super(dsource, sqlInsert);
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // STEP_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // LIST_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // POSITION
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_KEY
	}
}
