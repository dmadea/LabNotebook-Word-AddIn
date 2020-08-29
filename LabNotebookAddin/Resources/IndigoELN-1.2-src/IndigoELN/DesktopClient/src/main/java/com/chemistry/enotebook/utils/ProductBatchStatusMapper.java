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
import com.chemistry.enotebook.domain.ProductBatchModel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductBatchStatusMapper {
	
	private static HashMap selectivityMap = new HashMap();
	private static HashMap continueMap = new HashMap();
	private static ProductBatchStatusMapper instance = null;
	
	private final static String PASSED = "Passed";
	private final static String PASSED_REGISTERED = "Passed and Registered";
	private final static String FAILED = "Failed";
	private final static String SUSPECT = "Suspect";
	private final static String NOT_MADE = "Not Made";
	private final static String CONTINUE = "Continue";
	private final static String DISCONTINUE = "Discontinue";
	
	private ProductBatchStatusMapper() {
	}
	
	public static ProductBatchStatusMapper getInstance() {
		if (instance == null) {
			createInstance();
		}
		return instance;
	}
	
	private synchronized static void createInstance() {
		instance = new ProductBatchStatusMapper();
		init();
	}
	
	private static void init() {
		selectivityMap.put(PASSED, new Integer(CeNConstants.BATCH_STATUS_PASS));
		selectivityMap.put(PASSED_REGISTERED, new Integer(CeNConstants.BATCH_STATUS_PASSED_REGISTERED));
		selectivityMap.put(FAILED, new Integer(CeNConstants.BATCH_STATUS_FAIL));
		selectivityMap.put(SUSPECT, new Integer(CeNConstants.BATCH_STATUS_SUSPECT));
		selectivityMap.put(NOT_MADE, new Integer(CeNConstants.BATCH_STATUS_NOT_MADE));
		continueMap.put(CONTINUE, new Integer(CeNConstants.BATCH_STATUS_CONTINUE));
		continueMap.put(DISCONTINUE, new Integer(CeNConstants.BATCH_STATUS_DISCONTINUE));		
	}
	
	public void fillComboBox(JComboBox comboBox) {
		comboBox.addItem("Passed - Continue");
		comboBox.addItem("Passed and Registered - Continue");
		comboBox.addItem("Failed - Continue");
		comboBox.addItem("Failed - Discontinue");
		comboBox.addItem("Suspect - Continue");
		comboBox.addItem("Suspect - Discontinue");
		comboBox.addItem("Not Made - Discontinue");
//		for (Iterator it1 = selectivityMap.keySet().iterator(); it1.hasNext();) {
//			String key1 = (String) it1.next();
//			for (Iterator it2 = continueMap.keySet().iterator(); it2.hasNext();) {
//				String key2 = (String) it2.next();
//				comboBox.addItem(key1 + " - " + key2);
//			}
//		}
//		comboBox.remove(0);  // vbtodo FIX!!!
//		comboBox.remove(0);
//		comboBox.addItem(CeNConstants.NOT_MADE_DISCONTINUE);
	}
	
	public List getSelections() {
		ArrayList list = new ArrayList();
		list.add("Passed - Continue");
		list.add("Passed and Registered - Continue");
		list.add("Failed - Continue");
		list.add("Failed - Discontinue");
		list.add("Suspect - Continue");
		list.add("Suspect - Discontinue");
		list.add("Not Made - Discontinue");
//		for (Iterator it1 = selectivityMap.keySet().iterator(); it1.hasNext();) {
//			String key1 = (String) it1.next();
//			for (Iterator it2 = continueMap.keySet().iterator(); it2.hasNext();) {
//				String key2 = (String) it2.next();
//				list.add(key1 + " - " + key2);
//			}
//		}	
		return list;
	}
//	public Map getSelectivityMap() {
//		return new HashMap(selectivityMap); 
//	}
//	
//	public Map getContinueMap() {
//		return new HashMap(continueMap);
//	}
	
	public void setStatus(JComboBox comboBox, int selectivityStatus, int continueStatus) {
		comboBox.setSelectedItem(this.getStatusString(selectivityStatus, continueStatus));
	}
	
	public String getStatusString(int selectivityStatus, int continueStatus) {
		// Defaults
		String selectivityStr = PASSED;
		String continueStr = CONTINUE;
		// Set non-defaults if selected
		if (selectivityStatus == (new Integer(CeNConstants.BATCH_STATUS_FAIL)).intValue())
			selectivityStr = FAILED;
		else if (selectivityStatus == (new Integer(CeNConstants.BATCH_STATUS_PASSED_REGISTERED)).intValue())
			selectivityStr = PASSED_REGISTERED;
		else if (selectivityStatus == (new Integer(CeNConstants.BATCH_STATUS_NOT_MADE)).intValue())
			selectivityStr = NOT_MADE;
		else if (selectivityStatus == (new Integer(CeNConstants.BATCH_STATUS_SUSPECT)).intValue())
			selectivityStr = SUSPECT;
		if (continueStatus == (new Integer(CeNConstants.BATCH_STATUS_DISCONTINUE)).intValue())
			continueStr = DISCONTINUE;
		String itemstr = selectivityStr + " - " + continueStr;
		return itemstr;
	}
	
	public int getSelectivityStatus(String str) {
//		// Kluge to handle blank in not made
//		if (str.equals(CeNConstants.NOT_MADE_DISCONTINUE)) {
//			return ((Integer) selectivityMap.get("Not Made")).intValue();
//		}
		String selectivityKey = str.substring(0, str.indexOf("-")).trim();
		if (selectivityMap.containsKey(selectivityKey))
			return ((Integer) selectivityMap.get(selectivityKey)).intValue();
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
	
	public String getCombinedProductBatchStatus(ProductBatchModel model) {
		int selectivityStatus = model.getSelectivityStatus();
		int continueStatus = model.getContinueStatus();
		return this.getStatusString(selectivityStatus, continueStatus);
	}
}
