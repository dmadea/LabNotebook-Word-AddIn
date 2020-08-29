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
package com.chemistry.enotebook.client.gui.page.reagents;

import com.chemistry.enotebook.domain.MonomerBatchModel;
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ChemUtilInitException;
import com.common.chemistry.codetable.CodeTableCacheException;
import org.jdom.Element;
import org.jdom.JDOMException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.python.modules.synchronize;
//import com.chemistry.enotebook.utils.CommonUtils;

public class ReagentsHandlerCache {
	private static Object lock = new Object();
	private static ReagentsHandlerCache instance;
	private ReagentsHandler reagentsHandler;
	private ReagentsFrame reagentsFrame;
	private String myReagents;
	private Map<String, String> reagentPropertiesMap;
	
	private ReagentsHandlerCache() {
		reagentsHandler = new ReagentsHandler();
		getMyReagentList();
		buildReagentParamsMap();
	}
	
	public static ReagentsHandlerCache getInstance() {
		synchronized(lock) {
			if(instance == null) {
				instance = new ReagentsHandlerCache();
			}
			return instance;
		}
	}
	
	public static void buildInSeparateThread() {
		new Thread() {
			public void run() {
				synchronized(lock) {
					if(instance == null) {
						instance = new ReagentsHandlerCache();
					}
				}
			}
		}.start();
	}
	
	public ReagentsHandler getReagentsHandler() {
		return reagentsHandler;
	}
	
	public Map<String, String> buildReagentParamsMap() {
		if(reagentPropertiesMap == null) {
			reagentPropertiesMap = reagentsHandler.buildReagentParamsMap();
		}
		return reagentPropertiesMap;
	}
	
	public String getMyReagentList() {
		if(myReagents == null) {
			myReagents = reagentsHandler.getMyReagentList();
		}
		return myReagents;
	}
	
	public void updateMyReagentList(String reagentList) {
		if(reagentList != null) {
			myReagents = reagentList;
			reagentsHandler.updateMyReagentList(reagentList);
		}
	}
	
	public void updateMyReagentListAndReagentsFrameIfExist(String reagentList) {
		updateMyReagentList(reagentList);
		if(reagentList!=null && reagentsFrame != null) {
			reagentsFrame.setMyReagentsListWithoutUpdatingHandler(reagentList);
		}
	}
	
	public MonomerBatchModel buildReagentBatchList(Element selectedReagent) throws JDOMException, CodeTableCacheException,
					ChemUtilInitException, ChemUtilAccessException {
		return reagentsHandler.buildReagentBatchList(selectedReagent);
	}
	
	public void removeReagentMgmtServiceEJB() {
		reagentsHandler.removeReagentMgmtServiceEJB();
	}
	
	public String getCompoundNo(List<String> nameList, List<String> valueList) {
		return reagentsHandler.getCompoundNo(nameList,valueList);
	}
	
	public String getStructureByCompoundNo(String compoundNumber) {
		return reagentsHandler.getStructureByCompoundNo(compoundNumber);
	}
	
	public int getInitialChunkSize() {
		return reagentsHandler.getInitialChunkSize();
	}
	
	public int getChunkSize(int total, int threadCount) {
		return reagentsHandler.getChunkSize(total, threadCount);
	}
	
	public int getLastPosition() {
		return reagentsHandler.getLastPosition();
	}
	
	public boolean isSearchCancelled() {
		return reagentsHandler.isSearchCancelled();
	}
	
	public boolean getHasMore() {
		return reagentsHandler.getHasMore();
	}
	
	public void updateIteratingInfo() {
		reagentsHandler.updateIteratingInfo();
	}
	
	public String getReagentsList() {
		return reagentsHandler.getReagentsList();
	}

	public HashMap<String, String> getTotalDBMap() {
		return reagentsHandler.getTotalDBMap();
	}
	
	public byte[] doReagentSearch(String searchParamsXML) {
		return reagentsHandler.doReagentSearch(searchParamsXML);
	}
	
	public void cancelReagentSearch() {
		reagentsHandler.cancelReagentSearch();
	}
	
	public ArrayList<String> getMandatoryFields() {
		return reagentsHandler.getMandatoryFields();
	}
	
	public ArrayList<String> buildMandatoryFieldsMap() {
		return reagentsHandler.buildMandatoryFieldsMap();
	}
	
	public HashMap<String, String> buildDBMap() {
		return reagentsHandler.buildDBMap();
	}
	
	public void buildCompoundNumberMap() {
		reagentsHandler.buildCompoundNumberMap();
	}
	
	public Element buildDBXMLRoot() {
		return reagentsHandler.buildDBXMLRoot();
	}
	
	public boolean isTimedOutExceptionOccured() {
		return reagentsHandler.isTimedOutExceptionOccured;
	}
	
	public int getTotal() {
		return reagentsHandler.getTotal();
	}
	
	public Map<String, String> getSdfMap() {
		return reagentsHandler.getSdfMap();
	}

	public void storeFrameLink(ReagentsFrame reagentsFrame) {
		this.reagentsFrame = reagentsFrame;
	}
	
	public void removeFrameLink() {
		reagentsFrame = null;
	}
}
