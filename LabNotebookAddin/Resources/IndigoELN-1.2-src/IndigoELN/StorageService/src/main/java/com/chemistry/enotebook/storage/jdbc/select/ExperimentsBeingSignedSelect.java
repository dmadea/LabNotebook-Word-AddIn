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

import com.chemistry.enotebook.experiment.utils.CeNXMLParser;
import com.chemistry.enotebook.storage.SignaturePageVO;
import org.apache.commons.lang.StringUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ExperimentsBeingSignedSelect extends AbstractSelect {
 
	public ExperimentsBeingSignedSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		String xmlMetadata = rs.getString("XML_METADATA");
		String key = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/Ussi_Key");
		
		SignaturePageVO vo = new SignaturePageVO();
		
		vo.setSiteCode(rs.getString("site_code"));
		vo.setNotebook(rs.getString("notebook"));
		vo.setExperiment(rs.getString("experiment"));
		vo.setVersion(rs.getInt("page_version"));
		vo.setStatus(rs.getString("page_status"));
		vo.setUssiKey(StringUtils.isBlank(key) ? "" : key);
		
		return vo;
	}
}
