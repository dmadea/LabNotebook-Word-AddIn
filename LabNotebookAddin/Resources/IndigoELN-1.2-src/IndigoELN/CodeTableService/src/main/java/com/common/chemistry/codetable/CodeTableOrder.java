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

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class CodeTableOrder implements Serializable {
	
	private static final long serialVersionUID = -6004199605730950000L;
	
	private List<String> columns = new Vector<String>();

	public CodeTableOrder(String newValue) {
		addOrder(newValue);
	}

	public CodeTableOrder(String[] newValues) {
		addOrder(newValues);
	}

	public void addOrder(String newValue) {
		if (newValue != null && newValue.length() > 0) {
			if (!columns.contains(newValue)) {
				columns.add(newValue);
			}
		}
	}

	public void addOrder(String[] newValues) {
		if (newValues != null && newValues.length > 0) {
			for (int i = 0; i < newValues.length; i++) {
				addOrder(newValues[i]);
			}
		}
	}

	public String[] getOrderedColumns() {
		String[] results = new String[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			results[i] = (String) columns.get(i);
		}
		return results;
	}

	public String getOrderClause() {
		String result = null;

		String[] values = getOrderedColumns();
		if (values != null && values.length > 0) {
			StringBuffer r = new StringBuffer();

			for (int i = 0; i < values.length; i++) {
				if (i != 0) {
					r.append(", ");
				}
				r.append(values[i]);
			}

			result = r.toString() + " ";
		}

		return result;
	}
}
