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
package com.chemistry.enotebook.client.gui.page.experiment.table;

public class StoicConstants {

	public static final String MENU_MY_REAGENTS = "My Reagent List";
	public static final String MENU_ADD_TO_MY_REAGENTS = "Add To My Reagent List";
	public static final String MENU_SEARCH_DBS = "Search DBs";
	public static final String MENU_ANALYZE_RXN = "Analyze Rxn";
	public static final String MENU_CREATE_RXN = "Create Rxn";
	public static final String MENU_MSDS_SEARCH = "MSDS Search";
	public static final String MENU_CLEAR_ROW = "Clear Row";
	public static final String MENU_DEL_ROW = "Delete Row";
	public static final String MENU_ADD_ROW_BEFORE = "Add Row Before";
	public static final String MENU_ADD_ROW_AFTER = "Add Row After";
	public static final String MENU_APPEND_ROW = "Append Row";
	public static final String MENU_AUTOCALC = "Autocalculate";
	public static final String MENU_CALC_TRIGGER = "Calculate Now";
	public static final String MENU_PROPERTIES = "Properties";
	public static final int TOOLBAR_HEIGHT = 22;
	public static final int TITLE_HEIGHT = 20;
	public static final int TABLE_ROW_HEIGHT = 18;
	// 4 rows Reagents <- includes header
	public static final int REAGENT_ROWS_MIN = 3;
	public static final int REAGENTS_EMPTY_HEIGHT = TABLE_ROW_HEIGHT * REAGENT_ROWS_MIN;
	// 3 rows Product
	public static final int PRODUCT_ROWS_MIN = 1;
	public static final int PRODUCTS_EMPTY_HEIGHT = TABLE_ROW_HEIGHT * PRODUCT_ROWS_MIN;
	// 2 header sections 42 <- The answer to the question of the meaning of
	// life and everything.
	// does not include 20 for scroll bars. Need to add if present.
	public static final int MEDCHEM_STOICH_EMPTY_HEIGHT = REAGENTS_EMPTY_HEIGHT + PRODUCTS_EMPTY_HEIGHT + TOOLBAR_HEIGHT + TITLE_HEIGHT;
	public static final int STOICH_EMPTY_WIDTH = 515;
	
	
	public static final int PARALLEL_STOICH_EMPTY_HEIGHT = REAGENTS_EMPTY_HEIGHT + + TOOLBAR_HEIGHT + TITLE_HEIGHT;
	
	public static final int STOICELEMENTS_EMPTY_HEIGHT = TABLE_ROW_HEIGHT * 5;
	public static final String MENU_REMOVE_SOLVENT = "Remove Solvent";
}
