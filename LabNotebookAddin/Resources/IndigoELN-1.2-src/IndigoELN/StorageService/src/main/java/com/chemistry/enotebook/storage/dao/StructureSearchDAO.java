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
package com.chemistry.enotebook.storage.dao;

import com.chemistry.enotebook.storage.StorageException;
import com.chemistry.enotebook.storage.query.SearchQueryGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StructureSearchDAO extends StorageDAO {

	private static final Log LOG = LogFactory.getLog(StructureSearchDAO.class);
		
	public List<String> searchByStructure(String structure, boolean searchInProducts, String searchOperator, String searchOption) throws Exception {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;	
		
		try {
			if (structure.contains("$RXN") && searchInProducts)
				throw new StorageException("Can not search reaction in products!");
			
			con = getConnection();
			
			String dbName = con.getMetaData().getDatabaseProductName();
			
			String sql;
			
			if (searchInProducts)
				sql = SearchQueryGenerator.getSearchByStructureQuery(searchOperator, searchOption, dbName);
			else
				sql = SearchQueryGenerator.getSearchByReactionQuery(searchOperator, searchOption, dbName);
			
			st = con.prepareStatement(sql);
			st.setString(1, structure);
			
			rs = st.executeQuery();
			
			List<String> result = new ArrayList<String>();
			
			while (rs.next())
				result.add(rs.getString("nbk_ref_version"));
			
			return result;
		} catch (Exception e) {
			LOG.error("Error searching by structure: ", e);
			throw e;
		} finally {
			cleanUp(con, st, rs);
		}
	}
}
