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

import com.chemistry.enotebook.domain.CROChemistInfo;
import com.chemistry.enotebook.domain.CROPageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CROPageSelect extends AbstractSelect {
	private static final Log log = LogFactory.getLog(CROPageSelect.class);

	public CROPageSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		CROPageInfo cro = 
			new CROPageInfo(rs.getString("PAGE_KEY"));
		CROChemistInfo croChemist = new CROChemistInfo();
		croChemist.setCroChemistID(rs.getString("VENDOR_CHEMIST_ID"));
		croChemist.setCroChemistDisplayName(rs.getString("VENDOR_CHEMIST_DISPLAY_NAME"));
		cro.setCroChemistInfo(croChemist);
		cro.setCroID(rs.getString("VENDOR_ID"));
		cro.setCroDisplayName(rs.getString("VENDOR_DISPLAY_NAME"));
		cro.setCroAplicationSourceName(rs.getString("VENDOR_APPLICATION_SOURCE"));
		cro.setRequestId(rs.getString("REQUEST_ID"));
		cro.setAdditionalXMLInfo(rs.getString("XML_METADATA"));
		
		cro.setLoadedFromDB(true);
		cro.setModelChanged(false);
		return cro;
	}

}
