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
/*
 * Created on May 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.regis_submis;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.*;
import com.common.chemistry.codetable.CodeTableCache;
import org.apache.commons.collections.FastHashMap;

import javax.sql.rowset.CachedRowSet;
import java.util.*;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class RegCodeMaps {
	private static RegCodeMaps instance = null;
	private CodeTableCache codeTableCache = null;
	private ArrayList solubilitySolventList = null;
	private ArrayList residualSolventList = null;
	private ArrayList compoundSourceList = null;
	private ArrayList storageList = null;
	private ArrayList handlingList = null;
	private ArrayList sourceAndDetailList = null;
	private ArrayList quanlitiveList = null;
	private ArrayList unitList = null;
	private ArrayList operatorList = null;
	private ArrayList hazardList = null;
	private HashMap<String, String> storageMap = null;
	private HashMap<String, String> handlingMap = null;
	private HashMap<String, String> hazardMap = null;
	private HashMap residualSolventMap = null;
	private HashMap<String, String> stereoisomerCodeMap = null;
	private Map<String, String> saltCodeMap = null;
	private HashMap solubilitySolventMap = null;

	private RegCodeMaps() {
		try {
			codeTableCache = CodeTableCache.getCache();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public static RegCodeMaps getInstance() {
		if (instance == null)
			instance = createInstance();
		return instance;
	}

	public static synchronized RegCodeMaps createInstance() {
		if (instance == null)
			instance = new RegCodeMaps();
		return instance;
	}

	/**
	 * build Stereoisomer code and description map.
	 * 
	 */
	private void buildSICMap() {
		try {
			stereoisomerCodeMap = new FastHashMap();
			List<Properties> codeTables = codeTableCache.getCodeTableAsList(CodeTableCache.STEREOISOMERS);
			if (codeTables != null) {
				for (Properties codeTable : codeTables) {
					stereoisomerCodeMap.put(codeTable.getProperty("STEREOISOMER_CODE"), codeTable.getProperty("STEREOISOMER_DESC"));					
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * build salt code and name map.
	 * 
	 */
	public void buildSaltCodeMap() {
		try {
			saltCodeMap = new FastHashMap();
			CachedRowSet saltRowSet = codeTableCache.getCacheAsRowSet(CodeTableCache.SALTS);
			if (saltRowSet != null) {
				synchronized (saltRowSet) {
					saltRowSet.beforeFirst();
					while (saltRowSet.next()) {
						saltCodeMap.put(saltRowSet.getString("SALT_CODE"), saltRowSet.getString("SALT_DESC"));
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * build solubility solvent code and name map.
	 * 
	 */
	public void buildSolubilitySolventMap() {
		try {
			solubilitySolventMap = new FastHashMap();
			CachedRowSet solventRowSet = codeTableCache.getCacheAsRowSet("COMPOUND_REGISTRATION_SOLUBILITY_SOLVENT_CDT");
			if (solventRowSet != null) {
				synchronized (solventRowSet) {
					solventRowSet.beforeFirst();
					while (solventRowSet.next()) {
						solubilitySolventMap.put(solventRowSet.getString("SOLUBILITY_SOLVENT_CODE"), solventRowSet
								.getString("SOLUBILITY_SOLVENT_DESC"));
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * build residual solvent code and name map.
	 * 
	 */
	public void buildResidualSolventMap() {
		try {
			residualSolventMap = new FastHashMap();
			CachedRowSet solventRowSet = codeTableCache.getCacheAsRowSet("COMPOUND_REGISTRATION_RESIDUAL_SOLVENT_CDT");
			if (solventRowSet != null) {
				synchronized (solventRowSet) {
					solventRowSet.beforeFirst();
					while (solventRowSet.next()) {
						residualSolventMap.put(solventRowSet.getString("RESIDUAL_SOLVENT_CODE"), solventRowSet
								.getString("RESIDUAL_SOLVENT_DESC"));
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * build solubility solvent list.
	 * 
	 */
	public void buildSolubilitySolventList() {
		try {
			solubilitySolventList = new ArrayList();
			CachedRowSet solventRowSet = codeTableCache.getCacheAsRowSet("COMPOUND_REGISTRATION_SOLUBILITY_SOLVENT_CDT");
			if (solventRowSet != null) {
				synchronized (solventRowSet) {
					solventRowSet.beforeFirst();
					while (solventRowSet.next()) {
						RegSolubilitySolventCache regSolubilitySolventCache = new RegSolubilitySolventCache();
						regSolubilitySolventCache.setCode(solventRowSet.getString("SOLUBILITY_SOLVENT_CODE").trim());
						regSolubilitySolventCache.setDescription(solventRowSet.getString("SOLUBILITY_SOLVENT_DESC").trim());
						solubilitySolventList.add(regSolubilitySolventCache);
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * build residual solvent list.
	 * 
	 */
	public void buildResidualSolventList() {
		try {
			residualSolventList = new ArrayList();
			CachedRowSet solventRowSet = codeTableCache.getCacheAsRowSet("COMPOUND_REGISTRATION_RESIDUAL_SOLVENT_CDT");
			if (solventRowSet != null) {
				synchronized (solventRowSet) {
					solventRowSet.beforeFirst();
					while (solventRowSet.next()) {
						RegResidaulSolventCache regResidaulSolventCache = new RegResidaulSolventCache();
						regResidaulSolventCache.setCode(solventRowSet.getString("RESIDUAL_SOLVENT_CODE").trim());
						regResidaulSolventCache.setDescription(solventRowSet.getString("RESIDUAL_SOLVENT_DESC").trim());
						residualSolventList.add(regResidaulSolventCache);
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * build compound source list.
	 * 
	 */
	public void buildCompoundSourceList() {
		try {
			compoundSourceList = new ArrayList();
			List<Properties> list = codeTableCache.getSourceCodes();
			for (Iterator it = list.iterator(); it.hasNext();) {
				Properties rec = (Properties) it.next();
				compoundSourceList.add(new Object[] { rec.get(CodeTableCache.SOURCE__SOURCE_CODE), rec.get(CodeTableCache.SOURCE__SOURCE_DESC)});
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * build compound source detail and source relation List.
	 * 
	 */
	public void buildSourceAndDetailList() {
		try {
			sourceAndDetailList = new ArrayList();
			List<Properties> list = codeTableCache.getSourceDetailCodes();
			for (Iterator it = list.iterator(); it.hasNext();) {
				Properties rec = (Properties) it.next();
				sourceAndDetailList.add(new Object[] { 
						rec.get(CodeTableCache.SOURCE_DETAIL__SOURCE_DETAIL_CODE), 
						rec.get(CodeTableCache.SOURCE_DETAIL__SOURCE_DETAIL_DESC), 
						rec.get(CodeTableCache.SOURCE_DETAIL__SOURCE_CODE) });
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * build unit list.
	 * 
	 */
	public void buildUnitList() {
		try {
			unitList = new ArrayList();
			CachedRowSet unitRowSet = codeTableCache.getCacheAsRowSet("COMPOUND_MANAGEMENT_UNIT_CDT");
			if (unitRowSet != null) {
				synchronized (unitRowSet) {
					unitRowSet.beforeFirst();
					while (unitRowSet.next()) {
						RegUnitCache regUnitCache = new RegUnitCache();
						regUnitCache.setCode(unitRowSet.getString("UNIT_CODE").trim());
						regUnitCache.setDescription(unitRowSet.getString("UNIT_DISPLAY").trim());
						unitList.add(regUnitCache);
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * build operator list.
	 * 
	 */
	public void buildOperatorList() {
		// try {
		// CachedRowSet operatorRowSet = new CachedRowSet();
		// operatorRowSet =
		// codeTableCache.getCacheAsRowSet("COMPOUND_REGISTRATION_OPERATOR_CDT");
		// / while (operatorRowSet.next()) {
		// RegOperatorCache regOperatorCache = new RegOperatorCache();
		// regOperatorCache.setCode(operatorRowSet.getString("OPERATOR").trim());
		// if (operatorRowSet.getString("OPERATOR").equals("EQ") ) {
		// regOperatorCache.setDescription("=");
		// } else if (operatorRowSet.getString("OPERATOR").equals("LT") ) {
		// regOperatorCache.setDescription("<");
		// } else if (operatorRowSet.getString("OPERATOR").equals("GT") ) {
		// regOperatorCache.setDescription(">");
		// } else if (operatorRowSet.getString("OPERATOR").equals("AP") ) {
		// regOperatorCache.setDescription("~");
		// }
		// operatorList.add(regOperatorCache);
		// }
		//
		// } catch (Exception e) {
		// CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		// }
		operatorList = new ArrayList();
		RegOperatorCache regOperatorCache3 = new RegOperatorCache();
		regOperatorCache3.setCode("GT");
		regOperatorCache3.setDescription(">");
		operatorList.add(regOperatorCache3);
		RegOperatorCache regOperatorCache2 = new RegOperatorCache();
		regOperatorCache2.setCode("LT");
		regOperatorCache2.setDescription("<");
		operatorList.add(regOperatorCache2);
		RegOperatorCache regOperatorCache1 = new RegOperatorCache();
		regOperatorCache1.setCode("EQ");
		regOperatorCache1.setDescription("=");
		operatorList.add(regOperatorCache1);
		RegOperatorCache regOperatorCache4 = new RegOperatorCache();
		regOperatorCache4.setCode("AP");
		regOperatorCache4.setDescription("~");
		operatorList.add(regOperatorCache4);
	}

	/**
	 * build solvent qualitative list.
	 * 
	 */
	public void buildSolventQualitativeList() {
		try {
			quanlitiveList = new ArrayList();
			CachedRowSet solventDescRowSet = codeTableCache.getCacheAsRowSet("COMPOUND_REGISTRATION_QUAL_SOLUBILITY_CDT");
			if (solventDescRowSet != null) {
				synchronized (solventDescRowSet) {
					solventDescRowSet.beforeFirst();
					while (solventDescRowSet.next()) {
						RegQualitativeCache regQualitativeCache = new RegQualitativeCache();
						regQualitativeCache.setCode(solventDescRowSet.getString("QUALITATIVE_CODE").trim());
						regQualitativeCache.setDescription(solventDescRowSet.getString("QUALITATIVE_DESC").trim());
						quanlitiveList.add(regQualitativeCache);
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * build hazard list.
	 * 
	 */
	public void buildHazardList() {
		try {
			hazardList = new ArrayList();
			CachedRowSet hazardRowSet = codeTableCache.getCacheAsRowSet("COMPOUND_REGISTRATION_HAZARD_CDT");
			if (hazardRowSet != null) {
				synchronized (hazardRowSet) {
					hazardRowSet.beforeFirst();
					while (hazardRowSet.next()) {
						// this.hazardMap.put(hazardRowSet.getString("HAZARD_CODE"),hazardRowSet.getString("HAZARD_DESCR"));
						RegHazardCodeCache regHazardCodeCache = new RegHazardCodeCache();
						regHazardCodeCache.setCode(hazardRowSet.getString("HAZARD_CODE").trim());
						regHazardCodeCache.setDescription(hazardRowSet.getString("HAZARD_DESCR").trim());
						hazardList.add(regHazardCodeCache);
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * build handling list.
	 * 
	 */
	public void buildHandlingList() {
		try {
			handlingList = new ArrayList();
			CachedRowSet handlingRowSet = codeTableCache.getCacheAsRowSet("COMPOUND_REGISTRATION_HANDLING_CDT");
			if (handlingRowSet != null) {
				synchronized (handlingRowSet) {
					handlingRowSet.beforeFirst();
					while (handlingRowSet.next()) {
						// this.handlingMap.put(handlingRowSet.getString("HANDLING_CODE"),handlingRowSet.getString("HANDLING_DESCR"));
						RegHandlingCodeCache regHandlingCodeCache = new RegHandlingCodeCache();
						regHandlingCodeCache.setCode(handlingRowSet.getString("HANDLING_CODE").trim());
						regHandlingCodeCache.setDescription(handlingRowSet.getString("HANDLING_DESCR").trim());
						handlingList.add(regHandlingCodeCache);
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * build storage list.
	 * 
	 */
	public void buildStorageList() {
		try {
			storageList = new ArrayList();
			CachedRowSet storageRowSet = codeTableCache.getCacheAsRowSet("COMPOUND_REGISTRATION_STORAGE_CDT");
			if (storageRowSet != null) {
				synchronized (storageRowSet) {
					storageRowSet.beforeFirst();
					while (storageRowSet.next()) {
						// this.storageMap.put(storageRowSet.getString("STORAGE_CODE"),storageRowSet.getString("STORAGE_DESCR"));
						RegStorageCodeCache regStorageCodeCache = new RegStorageCodeCache();
						regStorageCodeCache.setCode(storageRowSet.getString("STORAGE_CODE").trim());
						regStorageCodeCache.setDescription(storageRowSet.getString("STORAGE_DESCR").trim());
						storageList.add(regStorageCodeCache);
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * build hazard map.
	 * 
	 */
	public void buildHazardMap() {
		try {
			hazardMap = new FastHashMap();
			CachedRowSet hazardRowSet = codeTableCache.getCacheAsRowSet("COMPOUND_REGISTRATION_HAZARD_CDT");
			if (hazardRowSet != null) {
				synchronized (hazardRowSet) {
					hazardRowSet.beforeFirst();
					while (hazardRowSet.next()) {
						hazardMap.put(hazardRowSet.getString("HAZARD_CODE"), hazardRowSet.getString("HAZARD_DESCR"));
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * build handling map.
	 * 
	 */
	public void buildHandlingMap() {
		try {
			handlingMap = new FastHashMap();
			CachedRowSet handlingRowSet = codeTableCache.getCacheAsRowSet("COMPOUND_REGISTRATION_HANDLING_CDT");
			if (handlingRowSet != null) {
				synchronized (handlingRowSet) {
					handlingRowSet.beforeFirst();
					while (handlingRowSet.next()) {
						handlingMap.put(handlingRowSet.getString("HANDLING_CODE"), handlingRowSet.getString("HANDLING_DESCR"));
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * build storage map.
	 * 
	 */
	public void buildStorageMap() {
		try {
			storageMap = new FastHashMap();
			CachedRowSet storageRowSet = codeTableCache.getCacheAsRowSet("COMPOUND_REGISTRATION_STORAGE_CDT");
			if (storageRowSet != null) {
				synchronized (storageRowSet) {
					storageRowSet.beforeFirst();
					while (storageRowSet.next()) {
						storageMap.put(storageRowSet.getString("STORAGE_CODE"), storageRowSet.getString("STORAGE_DESCR"));
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * @return Returns the compoundSourceList.
	 */
	public ArrayList getCompoundSourceList() {
		if (compoundSourceList == null)
			buildCompoundSourceList();
		return compoundSourceList;
	}

	/**
	 * @return Returns the handlingList.
	 */
	public ArrayList getHandlingList() {
		if (hazardList == null)
			buildHandlingList();
		return handlingList;
	}

	/**
	 * @return Returns the handlingMap.
	 */
	public HashMap<String, String> getHandlingMap() {
		if (handlingMap == null)
			buildHandlingMap();
		return handlingMap;
	}

	/**
	 * @return Returns the hazardList.
	 */
	public ArrayList getHazardList() {
		if (hazardList == null)
			buildHazardList();
		return hazardList;
	}

	/**
	 * @return Returns the hazardMap.
	 */
	public HashMap<String, String> getHazardMap() {
		if (hazardMap == null)
			buildHazardMap();
		return hazardMap;
	}

	/**
	 * @return Returns the operatorList.
	 */
	public ArrayList getOperatorList() {
		if (operatorList == null)
			buildOperatorList();
		return operatorList;
	}

	/**
	 * @return Returns the qualitativeList.
	 */
	public ArrayList getQualitativeList() {
		if (quanlitiveList == null)
			buildSolventQualitativeList();
		return quanlitiveList;
	}

	/**
	 * @return Returns the residualSolventList.
	 */
	public ArrayList getResidualSolventList() {
		if (residualSolventList == null)
			buildResidualSolventList();
		return residualSolventList;
	}

	/**
	 * @return Returns the residualSolventMap.
	 */
	public HashMap getResidualSolventMap() {
		if (residualSolventMap == null)
			buildResidualSolventMap();
		return residualSolventMap;
	}

	/**
	 * @return Returns the saltCodeMap.
	 */
	public Map<String, String> getSaltCodeMap() {
		if (saltCodeMap == null)
			buildSaltCodeMap();
		return saltCodeMap;
	}

	/**
	 * @return Returns the solubilitySolventList.
	 */
	public ArrayList getSolubilitySolventList() {
		if (solubilitySolventList == null)
			buildSolubilitySolventList();
		return solubilitySolventList;
	}

	/**
	 * @return Returns the solubilitySolventMap.
	 */
	public HashMap getSolubilitySolventMap() {
		if (solubilitySolventMap == null)
			buildSolubilitySolventMap();
		return solubilitySolventMap;
	}

	/**
	 * @return Returns the sourceAndDetailList.
	 */
	public ArrayList getSourceAndDetailList() {
		if (sourceAndDetailList == null)
			buildSourceAndDetailList();
		return sourceAndDetailList;
	}

	/**
	 * @return Returns the stereoisomerCodeMap.
	 */
	public HashMap<String, String> getStereoisomerCodeMap() {
		if (stereoisomerCodeMap == null || stereoisomerCodeMap.isEmpty())
			buildSICMap();
		return stereoisomerCodeMap;
	}

	/**
	 * @return Returns the storageList.
	 */
	public ArrayList getStorageList() {
		if (storageList == null)
			buildStorageList();
		return storageList;
	}

	/**
	 * @return Returns the storageMap.
	 */
	public HashMap<String, String> getStorageMap() {
		if (storageMap == null)
			buildStorageMap();
		return storageMap;
	}

	/**
	 * @return Returns the unitList.
	 */
	public ArrayList getUnitList() {
		if (unitList == null)
			buildUnitList();
		return unitList;
	}
}
