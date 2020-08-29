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
import java.util.*;

public class CodeTableFilter implements Serializable {
	
	private static final long serialVersionUID = -4114493367986259984L;
	
	private Map<String, List<String>> filters = new HashMap<String, List<String>>();

	public CodeTableFilter(String column, String newValue) {
		addFilter(column, newValue);
	}

	public CodeTableFilter(String column, String[] newValues) {
		addFilter(column, newValues);
	}

	public void addFilter(String column, String newValue) {
		if (column != null && newValue != null && column.length() > 0 && newValue.length() > 0) {
			List<String> values = filters.get(column);
			if (values == null) {
				values = new ArrayList<String>();
				values.add(newValue);
				filters.put(column, values);
			} else if (!values.contains(newValue)) {
				values.add(newValue);
			}
		}
	}

	public void addFilter(String column, String[] newValues) {
		if (column != null && newValues != null && column.length() > 0 && newValues.length > 0) {
			for (int i = 0; i < newValues.length; i++) {
				addFilter(column, newValues[i]);
			}
		}
	}

	public String[] getFilteredColumns() {
		Set<String> keyset = filters.keySet();
		Object[] keys = keyset.toArray();
		String[] results = new String[keys.length];
		for (int i = 0; i < keys.length; i++) {
			results[i] = (String) keys[i];
		}
		return results;
	}

	public String[] getColumnFilters(String column) {
		String[] results = null;

		List<String> values = filters.get(column);
		if (values != null) {
			results = new String[values.size()];
			for (int i = 0; i < values.size(); i++) {
				results[i] = (String) values.get(i);
			}
		}

		return results;
	}

	public String getColumnFilterClause(String column) {
		String result = null;

		String[] values = getColumnFilters(column);
		if (values != null && values.length > 0) {
			if (values.length == 1) {
				if (values[0].indexOf("%") >= 0) {
					result = "NOT " + column + " LIKE '" + values[0] + "' ";
				} else {
					result = column + " <> '" + values[0] + "' ";
				}
			} else {
				StringBuffer r = new StringBuffer();

				boolean hasLike = false;
				for (int i = 0; i < values.length; i++) {
					if (values[i].indexOf("%") < 0) { // Not a Like type search
						if (r.length() == 0) {
							r.append("NOT " + column + " IN (");
						} else {
							r.append(", ");
						}
						r.append("'" + values[i] + "' ");
					} else {
						hasLike = true;
					}
				}
				if (r.length() > 0) {
					r.append(") ");
				}

				if (hasLike) {
					boolean needAnd = (r.length() > 0);
					for (int i = 0; i < values.length; i++) {
						if (values[i].indexOf("%") >= 0) {
							if (needAnd) {
								r.append("AND ");
							}
							r.append("NOT " + column + " LIKE '" + values[i] + "' ");
							needAnd = true;
						}
					}
				}

				result = r.toString();
			}
		}

		return result;
	}

	public String getFilterClause() {
		String result = null;

		String[] columns = getFilteredColumns();
		if (columns != null && columns.length > 0) {
			StringBuffer r = new StringBuffer();

			for (int i = 0; i < columns.length; i++) {
				String clause = getColumnFilterClause(columns[i]);
				if (clause != null && clause.length() > 0) {
					if (i != 0) {
						r.append(" AND ");
					}
					r.append(clause);
				}
			}

			result = r.toString();
		}

		return result;
	}
}
