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

import com.chemistry.enotebook.domain.BatchesList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BatchesListSelect extends AbstractSelect {
	private static final Log log = LogFactory.getLog(BatchesListSelect.class);

	public BatchesListSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		BatchesList batchesList = new BatchesList(rs.getString("LIST_KEY"));
		batchesList.setPosition(rs.getString("POSITION"));
		
		batchesList.setLoadedFromDB(true);
		batchesList.setModelChanged(false);
		log.debug("BatchesList key is:"+batchesList.getKey()+ " position is:"+batchesList.getPosition());
		return batchesList;
	}
}