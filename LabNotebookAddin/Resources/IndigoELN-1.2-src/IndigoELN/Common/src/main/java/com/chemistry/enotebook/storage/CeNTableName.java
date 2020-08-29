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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class CeNTableName implements Serializable {
	static final long serialVersionUID = 7962661834605729917L;

	// The typesafe enum pattern
	private final String name;

	private CeNTableName(String name) {
		this.name = name;
	}
	private static final Map INSTANCES = new HashMap();

	public String toString() {
		return name;
	}

	public static final CeNTableName CEN_PAGES = new CeNTableName("CEN_PAGES");
	public static final CeNTableName CEN_BATCHES = new CeNTableName("CEN_BATCHES");
	public static final CeNTableName CEN_STRUCTURES = new CeNTableName("CEN_STRUCTURES");
	// public static final CeNTableName CEN_CONTAINERS =
	// new CeNTableName("CEN_CONTAINERS");
	public static final CeNTableName CEN_ATTACHMENTS = new CeNTableName("CEN_ATTACHEMENTS");
	public static final CeNTableName ADD_CEN_ATTACHMENTS = new CeNTableName("ADD_CEN_ATTACHMENTS");
	public static final CeNTableName CEN_REACTION_STEPS = new CeNTableName("CEN_REACTION_STEPS");
	public static final CeNTableName CEN_REACTION_SCHEMES = new CeNTableName("CEN_REACTION_SCHEMES");
	// public static final CeNTableName CEN_TRANSACTIONS =
	// new CeNTableName("CEN_TRANSACTIONS");
	public static final CeNTableName CEN_ANALYSIS = new CeNTableName("CEN_ANALYSIS");
	// public static final CeNTableName CEN_PAGE_VERSIONS =
	// new CeNTableName("CEN_PAGE_VERSIONS");

	private static final CeNTableName[] VALS =
	// { CEN_PAGES, CEN_STRUCTURES, CEN_BATCHES, CEN_CONTAINERS, CEN_ATTACHMENTS,
	// CEN_REACTION_STEPS, CEN_REACTION_SCHEMES, CEN_TRANSACTIONS, CEN_ANALYSIS, CEN_PAGE_VERSIONS};
	{ CEN_PAGES, CEN_STRUCTURES, CEN_BATCHES, CEN_ATTACHMENTS, CEN_REACTION_STEPS, CEN_REACTION_SCHEMES, CEN_ANALYSIS };

	public static final List VALUES = Arrays.asList(VALS);

	static {
		INSTANCES.put("CEN_PAGES", CEN_PAGES);
		INSTANCES.put("CEN_BATCHES", CEN_BATCHES);
		INSTANCES.put("CEN_STRUCTURES", CEN_STRUCTURES);
		// INSTANCES.put("CEN_CONTAINERS", CEN_CONTAINERS);
		INSTANCES.put("CEN_ATTACHEMENTS", CEN_ATTACHMENTS);
		INSTANCES.put("ADD_CEN_ATTACHMENTS", ADD_CEN_ATTACHMENTS);
		INSTANCES.put("CEN_REACTION_STEPS", CEN_REACTION_STEPS);
		INSTANCES.put("CEN_REACTION_STEPS", CEN_REACTION_STEPS);
		INSTANCES.put("CEN_REACTION_SCHEMES", CEN_REACTION_SCHEMES);
		// INSTANCES.put("CEN_TRANSACTIONS", CEN_TRANSACTIONS);
		INSTANCES.put("CEN_ANALYSIS", CEN_ANALYSIS);
		// INSTANCES.put("CEN_PAGE_VERSIONS", CEN_PAGE_VERSIONS);
	}

	public static CeNTableName getInstance(String strTable) {
		return (CeNTableName) INSTANCES.get(strTable);
	}
}
