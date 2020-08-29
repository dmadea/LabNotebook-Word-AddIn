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
package com.chemistry.enotebook.utils;

import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.MonomerBatchModel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MonomerBatchStatusMapper {

	private static HashMap solubilityMap = new HashMap();
	private static HashMap continueMap = new HashMap();
	private static MonomerBatchStatusMapper instance = null;
	
	private final static String SOLUBLE = "Soluble";
	private final static String INSOLUBLE = "Insoluble";
	private final static String UNAVAILABLE = "Unavailable";
	private final static String CONTINUE = "Continue";
	private final static String DISCONTINUE = "Discontinue";
	
	private MonomerBatchStatusMapper() {
	}
	
	public static MonomerBatchStatusMapper getInstance() {
		if (instance == null) {
			createInstance();
		}
		return instance;
	}
	
	private synchronized static void createInstance() {
		instance = new MonomerBatchStatusMapper();
		init();
	}
	
	private static void init() {
		solubilityMap.put(SOLUBLE, new Integer(CeNConstants.BATCH_STATUS_SOLUBLE));
		solubilityMap.put(INSOLUBLE, new Integer(CeNConstants.BATCH_STATUS_INSOLUBLE));
		solubilityMap.put(UNAVAILABLE, new Integer(CeNConstants.BATCH_STATUS_UNAVAILABLE));
		continueMap.put(CONTINUE, new Integer(CeNConstants.BATCH_STATUS_CONTINUE));
		continueMap.put(DISCONTINUE, new Integer(CeNConstants.BATCH_STATUS_DISCONTINUE));		
	}
	
	public void fillComboBox(JComboBox comboBox) {
		comboBox.addItem(SOLUBLE + " - " + CONTINUE);
		comboBox.addItem(INSOLUBLE + " - " + CONTINUE);
		comboBox.addItem(INSOLUBLE + " - " + DISCONTINUE);
		comboBox.addItem(UNAVAILABLE + " - " + DISCONTINUE);
	}
	
	public List getSelections() {
		ArrayList list = new ArrayList();
		list.add(SOLUBLE + " - " + CONTINUE);
		list.add(INSOLUBLE + " - " + CONTINUE);
		list.add(INSOLUBLE + " - " + DISCONTINUE);
		list.add(UNAVAILABLE + " - " + DISCONTINUE);
		return list;
	}
	
	private String getStatusString(int solubilityStatus, int continueStatus) {
		// Defaults
		String solubilityStr = SOLUBLE;
		String continueStr = CONTINUE;
		// Set non-defaults if selected
		if (solubilityStatus == (new Integer(CeNConstants.BATCH_STATUS_INSOLUBLE)).intValue())
			solubilityStr = INSOLUBLE;
		else if (solubilityStatus == (new Integer(CeNConstants.BATCH_STATUS_UNAVAILABLE)).intValue())
			solubilityStr = UNAVAILABLE;		
		if (continueStatus == (new Integer(CeNConstants.BATCH_STATUS_DISCONTINUE)).intValue())
			continueStr = DISCONTINUE;
		String itemstr = solubilityStr + " - " + continueStr;
		return itemstr;
	}
	
	public int getSolubilityStatus(String str) {
		String selectivityKey = str.substring(0, str.indexOf(" "));
		if (solubilityMap.containsKey(selectivityKey))
			return ((Integer) solubilityMap.get(selectivityKey)).intValue();
		else 
			return -1;
	}

	public int getContinueStatus(String str) {
		try {
			String continueKey = str.substring(str.lastIndexOf(" "));
			continueKey = continueKey.trim();
			if (continueMap.containsKey(continueKey))
				return ((Integer) continueMap.get(continueKey)).intValue();
			else 
				return -1;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
	public String getCombinedProductBatchStatus(MonomerBatchModel model) {
		int solubilityStatus = 0;
		int continueStatus = 0;
		return this.getStatusString(solubilityStatus, continueStatus);
	}
}
