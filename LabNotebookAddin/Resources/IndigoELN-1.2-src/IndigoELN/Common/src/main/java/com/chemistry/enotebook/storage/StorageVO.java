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

package com.chemistry.enotebook.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StorageVO implements java.io.Serializable {

	private static final long serialVersionUID = 2332240673963733096L;
	
	private ArrayList rows = new ArrayList();
	private Map<String, Object> map = new HashMap<String, Object>();
	
	public StorageVO() throws SQLException {
		super();
	}

	public void populate(ResultSet rs) throws StorageException {
		rows = new ArrayList();

		try {
			int ColCount = rs.getMetaData().getColumnCount();

			while (rs.next()) {
				ArrayList cols = new ArrayList();
				for (int i = 1; i <= ColCount; i++) {
					Object obj = rs.getObject(i);
					if (obj instanceof java.sql.Blob) {
						cols.add(((java.sql.Blob) obj).getBytes(1, (int) ((java.sql.Blob) obj).length()));
					} else {
						cols.add(obj);
					}
				}

				rows.add(cols);
			}
		} catch (SQLException e) {
			throw new StorageException("toArrayList failed", e);
		}
	}
	
	public ArrayList toArrayList() {
		return rows;
	}
	
	public void setString(String key, String str) {
		setObject(key, str);
	}
	
	public String getString(String key) {
		return (String) getObject(key);
	}
	
	public void setBytes(String key, byte[] bytes) {
		setObject(key, bytes);
	}
	
	public byte[] getBytes(String key) {
		return (byte[]) getObject(key);
	}
	
	private void setObject(String key, Object object) {
		map.put(key, object);
	}
	
	private Object getObject(String key) {
		return map.get(key);
	}
}
