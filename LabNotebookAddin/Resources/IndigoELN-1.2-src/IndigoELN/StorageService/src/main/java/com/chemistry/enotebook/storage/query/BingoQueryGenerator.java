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
package com.chemistry.enotebook.storage.query;

import org.apache.commons.lang.StringUtils;

public class BingoQueryGenerator {

	private static final String BINGO_SCHEMA = "bingo";

	private static final String EXACT = "exact";
	private static final String SUBSTRUCTURE = "sub";
	private static final String SIMILARITY = "sim";
	
	private static final String DEFAULT_SIMILARITY = "'tanimoto'";
	private static final String DEFAULT_EXACT = "'ALL'";

	private static final String DB_ORACLE = "Oracle";
	private static final String DB_POSTGRESQL = "PostgreSQL";
	
	public static String getExactQuery(boolean isReaction, String fieldName, String dbName) {
		return getBingoQuery(isReaction, fieldName, EXACT, null, null, dbName);
	}

	public static String getSubstructureQuery(boolean isReaction, String fieldName, String dbName) {
		return getBingoQuery(isReaction, fieldName, SUBSTRUCTURE, null, null, dbName);
	}

	public static String getSimilarityQuery(String fieldName, Double bottomValue, Double topValue, String dbName) {
		return getBingoQuery(false, fieldName, SIMILARITY, bottomValue, topValue, dbName);
	}

	private static String getBingoQuery(boolean isReaction, String fieldName, String searchFunctionName, Double bottomValue, Double topValue, String dbName) {
		if (StringUtils.isBlank(dbName))
			dbName = DB_POSTGRESQL;
		
		StringBuilder sb = new StringBuilder();
		
		if (StringUtils.equals(dbName, DB_ORACLE)) {
			sb.append(" dbms_lob.getlength(" + fieldName + ") > 0 and ");
			
			sb.append(BINGO_SCHEMA);
			sb.append(".");

			if (isReaction)
				sb.append("r");

			sb.append(searchFunctionName);
			sb.append("(");
			sb.append(fieldName);
			sb.append(", ?");

			if (EXACT.equals(searchFunctionName))
				sb.append(", ").append(DEFAULT_EXACT);

			if (SIMILARITY.equals(searchFunctionName)) {
				sb.append(", ").append(DEFAULT_SIMILARITY);
				
				String query = "";

				if (bottomValue != null && topValue != null) {
					query += "between " + bottomValue + " and " + topValue;
				} else if (bottomValue != null) {
					query += "> " + bottomValue;
				} else if (topValue != null) {
					query += "< " + topValue;
				} else {
					String errorMessage = "Both bottomValue and topValue are null in similarity search";
					throw new IllegalArgumentException(errorMessage);
				}
				
				sb.append(") ");
				sb.append(query);
			} else
				sb.append(") = 1");
		}

		if (StringUtils.equals(dbName, DB_POSTGRESQL)) {
			sb.append(" length(" + fieldName + ") > 0 and ");
			
			sb.append(fieldName);
			
			sb.append(" @ ");
			sb.append("(");
			
			if (SIMILARITY.equals(searchFunctionName)) {
				sb.append(bottomValue);
				sb.append(", ");
				sb.append(topValue);
				sb.append(", ");
			}
			
			sb.append("?, ");
			
			if (EXACT.equals(searchFunctionName))
				sb.append(DEFAULT_EXACT);
			else if (SIMILARITY.equals(searchFunctionName))
				sb.append(DEFAULT_SIMILARITY);
			else
				sb.append("''");
			
			sb.append(")");
			
			sb.append("::");
			sb.append(BINGO_SCHEMA);
			sb.append(".");
			if (isReaction)
				sb.append("r");
			sb.append(searchFunctionName);
		}
		
		return sb.toString();
	}
}
