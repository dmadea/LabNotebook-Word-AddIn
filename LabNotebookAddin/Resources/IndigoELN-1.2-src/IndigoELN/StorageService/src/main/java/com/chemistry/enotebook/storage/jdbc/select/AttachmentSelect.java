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

import com.chemistry.enotebook.domain.AttachmentModel;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AttachmentSelect extends AbstractSelect {
	private static final Log log = LogFactory.getLog(AttachmentSelect.class);

	public AttachmentSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		AttachmentModel attachment = new AttachmentModel(rs.getString("ATTACHEMENT_KEY"));//ATTACHEMENT_KEY
		attachment.setDateModified(rs.getString("DATE_MODIFIED"));//DATE_MODIFIED
		attachment.setDocumentDescription(rs.getString("DOCUMENT_DESCRIPTION"));//DOCUMENT_DESCRIPTION
		attachment.setDocumentName(rs.getString("DOCUMENT_NAME"));//DOCUMENT_NAME
		String ipRel = rs.getString("IP_RELATED");
		if(ipRel != null && ipRel.equals("Y"))
		{
		attachment.setIpRelated(true);//IP_RELATED
		}else
		{
		attachment.setIpRelated(false);	
		}
		attachment.setOriginalFileName(rs.getString("ORIGINAL_FILE_NAME"));//ORIGINAL_FILE_NAME
		attachment.setSize(CommonUtils.toInteger(rs.getString("DOCUMENT_SIZE")));//DOCUMENT_SIZE
		attachment.setType(rs.getString("DOCUMENT_TYPE"));//DOCUMENT_TYPE
		
		int colIndex = -1;
		
		try {  // for lazy load, blob data is not loaded initially
		  colIndex = rs.findColumn("BLOB_DATA");
		}
		catch (SQLException e) {  // blob data not being selected initially for lazy load
		}
		if (colIndex != -1) {
  			attachment.setContents(rs.getBytes("BLOB_DATA"));
  		}
		
		attachment.setLoadedFromDB(true);	
		attachment.setModelChanged(false);
		return attachment;
	}
}
