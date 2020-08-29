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

package com.common.chemistry.codetable;

import com.sun.rowset.CachedRowSetImpl;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CodeTableVO extends CachedRowSetImpl implements java.io.Serializable {
	
	private static final long serialVersionUID = 94857818647614626L;

    private HashMap<?, ?> hashmap_ = null;
	
	private String schema_ = "";
	private String tableName_ = "";
	
	private CodeTableFilter filter_ = null;
	private CodeTableSelect select_ = null;
	private CodeTableOrder  order_ = null;
	
	
	public CodeTableVO() throws SQLException {
		super();
	}
	
	public CodeTableVO(HashMap<?, ?> map) throws SQLException {
		setHashMap(map);	
	}
	
	public HashMap<?, ?> getHashMap() 		 		{ return hashmap_; }
	public void setHashMap(HashMap<?, ?> map)  		{ hashmap_ = map; }

	public String getSchema() 			 			{ return schema_; }
	public void setSchema(String schema) 			{ schema_ = schema; }

	public String getTableName() 					{ return tableName_; }
	public void setTableName(String tableName)  	{ tableName_ = tableName; }
	
	public CodeTableFilter getFilter() 			    { return filter_; }
	public void setFilter(CodeTableFilter filter)   { filter_ = filter; }

	public CodeTableSelect getSelect() 			    { return select_; }
	public void setSelect(CodeTableSelect select)   { select_ = select; }

	public CodeTableOrder getOrder() 			    { return order_; }
	public void setOrder(CodeTableOrder order)      { order_ = order; }

	public CachedRowSet getRowSetData()       		{ return this; }
		
	public long getColumnCount() throws CodeTableCacheException {
		long columnCount = -1;
		
		try	{
			columnCount = getMetaData().getColumnCount();
		} catch (SQLException e) {
			throw new CodeTableCacheException("CodeTableVO:getColumnCount failed", e);
		}
		
		return columnCount;
	}
	
	public long getRowCount() throws CodeTableCacheException {
		long rowCount = -1;
		
		try	{
			@SuppressWarnings("unused")
			long oldRow = getRow();
			
			synchronized (this) {
				afterLast();
				rowCount = getRow();
				
				beforeFirst();
			}
		} catch (SQLException e) {
			throw new CodeTableCacheException("CodeTableVO:getRowCount failed", e);
		}
		
		return rowCount;
	}
	
	public ArrayList<?> toArrayList() throws CodeTableCacheException {
		ArrayList<List<Object>> rows = new ArrayList<List<Object>>();
		
		try	{
			java.sql.ResultSetMetaData metaData = getMetaData();
			int ColCount = 0;
			if(metaData != null) {
				ColCount = getMetaData().getColumnCount();
			}

			synchronized (this) {
				beforeFirst();
				while (next()) {
					List<Object> cols = new ArrayList<Object>();
					for (int i=1; i <= ColCount; i++)
						cols.add(getObject(i));
					
					rows.add(cols);
				}
			}
		} catch (SQLException e) {
			throw new CodeTableCacheException("CodeTableVO:toArrayList failed", e);
		}
		
		return rows;
	}

	public String toXML() throws CodeTableCacheException {
		StringBuffer retVal = new StringBuffer();
		retVal.append("<RowData>\n");

		try	{
			int ColCount = getMetaData().getColumnCount();

			synchronized (this) {
				beforeFirst();
				while (next()) {
					retVal.append("    <Row>\n");
					for (int i=1; i <= ColCount; i++) {
						retVal.append("        <Column>");
						Object ans = getObject(i);
						if (ans != null) { retVal.append(ans.toString()); }
						retVal.append("</Column>\n");
					}
					retVal.append("    </Row>\n");
				}
			}
		} catch (SQLException e) {
			throw new CodeTableCacheException("CodeTableVO:toXML failed", e);
		}
		
		retVal.append("<RowData>\n");
		
    	return retVal.toString();
	}
}
