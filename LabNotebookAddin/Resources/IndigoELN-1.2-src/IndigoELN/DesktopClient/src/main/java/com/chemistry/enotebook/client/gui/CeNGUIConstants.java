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
package com.chemistry.enotebook.client.gui;

import java.awt.*;

public class CeNGUIConstants {
	
	public static final String STOIC_TOOLBAR_UPARROW = "UP_ARROW";
	public static final String STOIC_TOOLBAR_DOWNARROW = "DOWN_ARROW";
	public static final String STOIC_TOOLBAR_MYREAGENTS = "MYREAGENTS";
	public static final String STOIC_TOOLBAR_ADDTOMYREAGENTS = "ADDTOMYREAGENTS";
	public static final String STOIC_TOOLBAR_DBSEARCH = "DBSEARCH";
	public static final String STOIC_TOOLBAR_MSDSSEARCH = "MSDSSEARCH";
	public static final String STOIC_TOOLBAR_CREATERXN = "CREATEREACTION";
	public static final String STOIC_TOOLBAR_ANALYZERXN = "ANALYZEREACTION";

	public static final String NON_PLATED_BATCHES = "Non-Plated Batches";
	
	// Colors for plate wells
	public static final Color DARK_GREEN = new Color(84, 139, 93);  //dark green -"Soluble - Continue" or "Passed - Continue"
	public static final Color YELLOW = new Color(250, 250, 70); //Color.YELLOW;// yellow -"Unavailable - Discontinue" or "Failed - Discontinue"
	public static final Color DARK_BLUE = new Color(93, 84, 139); //new Color(255, 55, 255);//pink-"Passed and Registered - Continue"
	
	public static final Color RED = new Color(255, 0, 0);//red - "Insoluble - Continue" or "Failed - Continue"
	public static final Color LIGHT_RED = new Color(250, 70, 70);//dark red - "Insoluble - Discontinue"
	public static final Color LIGHT_BLUE = new Color(0, 171, 255);//light blue - "Insoluble - Continue" or "Suspect - Continue"
	
	public static final Color LIGHT_GRAY = Color.LIGHT_GRAY;//grey -"Empty Well"
	public static final Color BLUE = new Color(0, 0, 255);//blue -"Suspect - Discontinue"
	public static final Color WHITE = Color.WHITE;//white -"Not Made - Discontinue"
	
	// Colors for tables
	
	public static final Color LIGHT_GREEN = new Color(100, 220, 100);
	public static final Color VERY_LIGHT_BLUE = new Color(150, 150, 250);
	public static final Color ORANGE = new Color(250, 150, 50);
	public static final Color DARK_RED = new Color(180, 70, 70);
	
	public static final int BATCH_WT_DECIMAL_FIXED_FIGS = 3;
}
