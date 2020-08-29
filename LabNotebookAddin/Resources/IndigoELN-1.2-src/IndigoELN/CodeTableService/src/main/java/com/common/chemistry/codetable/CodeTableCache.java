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
 * Created on Jun 8, 2004
 * 
 *
 */
package com.common.chemistry.codetable;

import com.common.chemistry.codetable.delegate.CodeTableAccessException;
import com.common.chemistry.codetable.delegate.CodeTableDelegate;
import com.common.chemistry.codetable.delegate.CodeTableInitException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.rowset.CachedRowSet;
import java.util.*;

public class CodeTableCache {
	public static String SITES = "COMPOUND_MANAGEMENT_SITE_CDT";
	public static String SALTS = "COMPOUND_MANAGEMENT_SALT_CDT";
	public static String UNITS = "COMPOUND_MANAGEMENT_UNIT_CDT";
	public static String TA = "COMPOUND_REGISTRATION_TA_CDT";
	public static String PROJECTS = "COMPOUND_REGISTRATION_PROJECT_CDT";
	public static String STEREOISOMERS = "COMPOUND_MANAGEMENT_STEREOISOMER_CDT";
	public static String COMPOUND_STATE = "COMPOUND_REGISTRATION_STATE_CDT";
	public static String ANALYTIC_PURITY = "COMPOUND_REGISTRATION_ANALYTIC_PURITY_CDT";
	public static String VENDOR = "COMPOUND_MANAGEMENT_SUPPLIER_CDT";
	public static String PROTECTION = "COMPOUND_REGISTRATION_PROTECTION_CDT";
	public static String SOURCE = "COMPOUND_REGISTRATION_SOURCE_CDT";
	public static String SOURCE_DETAIL = "COMPOUND_REGISTRATION_SOURCE_DETAIL_CDT";
	public static String RESIDUAL_SOLV = "COMPOUND_REGISTRATION_RESIDUAL_SOLVENT_CDT";
	public static String SOLUBLE_SOLV = "COMPOUND_REGISTRATION_SOLUBILITY_SOLVENT_CDT";
	public static String HANDLING = "COMPOUND_REGISTRATION_HANDLING_CDT";
	public static String HAZARD = "COMPOUND_REGISTRATION_HAZARD_CDT";
	public static String STORAGE = "COMPOUND_REGISTRATION_STORAGE_CDT";
	public static String SOLVENTS = "COMPOUND_MANAGEMENT_SOLVENT_CDT";
	public static String VIAL_CONTAINERS = "COMPOUND_MANAGEMENT_VIAL_CDT";
	//public static String LOCATIONS = "COMPOUND_MANAGEMENT_LOCATION_CDT";

	// PurificationService Code values
	public static String PURIFICATION_SERVICE_SITES_LABS = "PURIFICATION_SERVICE_SITES_LABS";
	public static String PURIFICATION_SERVICE_ARCHIVE_PLATE_CHOICE = "PURIFICATION_SERVICE_ARCHIVE_PLATE_CHOICE";
	public static String PURIFICATION_SERVICE_SAMPLE_WORKUP = "PURIFICATION_SERVICE_SAMPLE_WORKUP";
	public static String PURIFICATION_SERVICE_SUBMITTAL_TYPE = "PURIFICATION_SERVICE_SUBMITTAL_TYPE";
	public static String PURIFICATION_SERVICE_PLATE_SOURCE = "PURIFICATION_SERVICE_PLATE_SOURCE";
	
	public static String SALTS__SALT_CODE = "SALT_CODE";
	public static String SALTS__SALT_DESC = "SALT_DESC";
	public static String SALTS__SALT_WEIGHT = "SALT_WEIGHT";
	public static String SALTS__SALT_FORMULA = "SALT_FORMULA";
	public static String COMPOUND_STATE__COMPOUND_STATE_CODE = "COMPOUND_STATE_CODE";
	public static String COMPOUND_STATE__COMPOUND_STATE_DESC = "COMPOUND_STATE_DESC";
	public static String TA__TA_CODE = "TA_CODE";
	public static String TA__TA_DESC = "TA_DESC";
	public static String PROJECTS__PROJECT_CODE = "PROJECT_CODE";
	public static String PROJECTS__PROJECT_NAME_DESC = "PROJECT_NAME_DESC";
	public static String PROJECTS__BASE_STRUCTURE = "BASE_STRUCTURE";
	public static String STEREOISOMERS__STEREOISOMER_CODE = "STEREOISOMER_CODE";
	public static String STEREOISOMERS__STEREOISOMER_DESC = "STEREOISOMER_DESC";
	public static String RESIDUAL_SOLV__RESIDUAL_SOLVENT_CODE = "RESIDUAL_SOLVENT_CODE";
	public static String RESIDUAL_SOLV__RESIDUAL_SOLVENT_DESC = "RESIDUAL_SOLVENT_DESC";
	public static String RESIDUAL_SOLV__RESIDUAL_SOLVENT_WEIGHT = "RESIDUAL_SOLVENT_WEIGHT";
	public static String SOLUBLE_SOLV__SOLUBILITY_SOLVENT_CODE = "SOLUBILITY_SOLVENT_CODE";
	public static String SOLUBLE_SOLV__SOLUBILITY_SOLVENT_DESC = "SOLUBILITY_SOLVENT_DESC";
	public static String SOLVENTS__SOLVENT_CODE = "SOLVENT_CODE";
	public static String SOLVENTS__SOLVENT_DESC = "SOLVENT_DESC";
	public static String SOURCE_DETAIL__SOURCE_DETAIL_CODE = "SOURCE_DETAIL_CODE";
	public static String SOURCE_DETAIL__SOURCE_DETAIL_DESC = "SOURCE_DETAIL_DESC";
	public static String SOURCE_DETAIL__SOURCE_CODE = "SOURCE_CODE";
	public static String SOURCE__SOURCE_CODE = "SOURCE_CODE";
	public static String SOURCE__SOURCE_DESC = "SOURCE_DESC";
	public static String ANALYTIC_PURITY__ANALYTICAL_PURITY_CODE = "ANALYTICAL_PURITY_CODE";
	public static String ANALYTIC_PURITY__ANALYTICAL_PURITY_DESC = "ANALYTICAL_PURITY_DESC";
	public static String VENDOR__SUPPLIER_CODE = "SUPPLIER_CODE";
	public static String VENDOR__SUPPLIER_DESC = "SUPPLIER_DESC";
	public static String SITES__SITE_CODE = "SITE_CODE";	
	public static String SITES__LABEL = "LABEL";
	public static String PROTECTION__PROTECTION_CODE = "PROTECTION_CODE";
	public static String PROTECTION__PROTECTION_DESC = "PROTECTION_DESC";
	public static String HANDLING__HANDLING_CODE = "HANDLING_CODE";
	public static String HANDLING__HANDLING_DESCR = "HANDLING_DESCR";
	public static String STORAGE__STORAGE_CODE = "STORAGE_CODE";
	public static String STORAGE__STORAGE_DESCR = "STORAGE_DESCR";
	public static String HAZARD__HAZARD_CODE = "HAZARD_CODE";
	public static String HAZARD__HAZARD_DESCR = "HAZARD_DESCR";
	public static String COMPOUND_REGISTRATION_QUAL_SOLUBILITY_CDT = "COMPOUND_REGISTRATION_QUAL_SOLUBILITY_CDT";
	public static String COMPOUND_REGISTRATION_QUAL_SOLUBILITY_CDT_CODE = "QUALITATIVE_CODE";
	public static String COMPOUND_REGISTRATION_QUAL_SOLUBILITY_CDT_DESC = "QUALITATIVE_DESC";

	private static CodeTableCache _instance = null;
	private static final Log log = LogFactory.getLog(CodeTableCache.class);
	
	private Map<String, CodeTableVO> cache = Collections.synchronizedMap(new HashMap<String, CodeTableVO>());
		
	private Map<String, List<Properties>> fullCache;
	
	private CodeTableDelegate codeTableDelegate;
	
	private CodeTableCache() {
		 try {
			codeTableDelegate = new CodeTableDelegate();
			reloadCache();
		} catch (CodeTableInitException e) {
			log.error("Can't init CodeTableDelegate: ", e);
		} catch (CodeTableAccessException e) {
			log.error("Can't get the full cache: ", e);
		}
	}

	// Synchronized to ensure only one cache is created - SCT
	public static CodeTableCache getCache() throws CodeTableCacheException {
		if (_instance == null) {
			getInstance();
		}
		return _instance;
	}

	public static synchronized CodeTableCache getInstance() throws CodeTableCacheException {
		if (_instance == null) {
			_instance = new CodeTableCache();
		}
		return _instance;
	}

	public void reloadCache() throws CodeTableAccessException {
		try {
			fullCache = codeTableDelegate.getFullCache();
		} catch (Exception e) {
			throw new CodeTableAccessException(e);
		}
	}
	
//	private void init() throws CodeTableCacheException {
//
//		Stopwatch stopwatch = new Stopwatch();
//        stopwatch.start("init() SITES,PROJECTS,SALTS");
//		// need to setup and construct cache objects from server
//		// create connection
//		try {
//			// Now you have a reference to the DemoHome object
//			// factory use it to ask the container to create an
//			// instance of the Demo bean
//			CodeTableDelegate ctd = new CodeTableDelegate();
//			cache.put(CodeTableCache.SITES, ctd.getCodeTable(CodeTableCache.SITES));
//			cache.put(CodeTableCache.PROJECTS, ctd.getCodeTable(CodeTableCache.PROJECTS));
//			cache.put(CodeTableCache.SALTS, ctd.getCodeTable(CodeTableCache.SALTS));
//			log.debug("init() SITES,PROJECTS,SALTS complete");
//			HashMap<String, ArrayList<ArrayList<String>>> map = ctd.getPurificationServiceCodeValues(getStringArrayOfSiteCodes(getSites()));
//			stopwatch.stop();
//			if (map != null) {
//				cache.put(CodeTableCache.PURIFICATION_SERVICE_SITES_LABS, map.get(CodeTableCache.PURIFICATION_SERVICE_SITES_LABS));
//				cache.put(CodeTableCache.PURIFICATION_SERVICE_ARCHIVE_PLATE_CHOICE, map.get(CodeTableCache.PURIFICATION_SERVICE_ARCHIVE_PLATE_CHOICE));
//				cache.put(CodeTableCache.PURIFICATION_SERVICE_SAMPLE_WORKUP, map.get(CodeTableCache.PURIFICATION_SERVICE_SAMPLE_WORKUP));
//				cache.put(CodeTableCache.PURIFICATION_SERVICE_SUBMITTAL_TYPE, map.get(CodeTableCache.PURIFICATION_SERVICE_SUBMITTAL_TYPE));
//				cache.put(CodeTableCache.PURIFICATION_SERVICE_PLATE_SOURCE, map.get(CodeTableCache.PURIFICATION_SERVICE_PLATE_SOURCE));
//
//			}
//		} catch (Exception e) {
//			throw new CodeTableCacheException(e.getMessage(), e);
//		}
//		log.debug("init() complete");
//	}

	protected String[] getStringArrayOfSiteCodes(ArrayList<ArrayList<String>> sites) {
		// Sites are arranged as a list of siteCode and siteDescription in positions 0 and 1 of a two element list.
		String[] siteArray = new String[sites.size()];
		for(int siteCount = 0; siteCount < sites.size(); siteCount++) {
			siteArray[siteCount] = sites.get(siteCount).get(0);
		}
		return siteArray;
	}
	
	public List<Properties> getSites() throws CodeTableCacheException {
		return getCodeTableAsList(CodeTableCache.SITES);
	}

	public List<Properties> getSalts() throws CodeTableCacheException {
		return getCodeTableAsList(CodeTableCache.SALTS);
	}

	public List<Properties> getUnits() throws CodeTableCacheException {
		return getCodeTableAsList(CodeTableCache.UNITS);
	}

	public List<Properties> getTa() throws CodeTableCacheException {
		return getCodeTableAsList(CodeTableCache.TA);
	}

	public List<Properties> getProjects() throws CodeTableCacheException {
		return getCodeTableAsList(CodeTableCache.PROJECTS);
	}

	public List<Properties> getCompoundStates() throws CodeTableCacheException {
		return getCodeTableAsList(CodeTableCache.COMPOUND_STATE);
	}

	public List<Properties> getPurityDeterminedBy() throws CodeTableCacheException {
		return getCodeTableAsList(CodeTableCache.ANALYTIC_PURITY);
	}

	public List<Properties> getVendors() throws CodeTableCacheException {
		return getCodeTableAsList(CodeTableCache.VENDOR);
	}

	public List<Properties> getProtectionCodes() throws CodeTableCacheException {
		return getCodeTableAsList(CodeTableCache.PROTECTION);
	}

	public List<Properties> getSourceCodes() throws CodeTableCacheException {
		return getCodeTableAsList(CodeTableCache.SOURCE);
	}

	public List<Properties> getSourceDetailCodes() throws CodeTableCacheException {
		return getCodeTableAsList(CodeTableCache.SOURCE_DETAIL);
	}

	public List<Properties> getSolvents() throws CodeTableCacheException {
		return getCodeTableAsList(CodeTableCache.SOLVENTS);
	}
	
	public List<Properties> getVialContainers() throws CodeTableCacheException {
		return getCodeTableAsList(CodeTableCache.VIAL_CONTAINERS);
	}
	
	/*
	public ArrayList getLocations() throws CodeTableCacheException {
		return getCacheAsArrayList(CodeTableCache.LOCATIONS);
	}
	*/

	/*
	public String getLocationsBySite(String code) throws CodeTableCacheException {
		ArrayList codeTable = getCacheAsArrayList(LOCATIONS);
		int SIDE_CODE_COLUMN = 3;
		int LOCATION_CODE_COLUMN = 0;
		if (codeTable != null && codeTable.size() > 0) {
			for (int i = 0; i < codeTable.size(); i++) {
				ArrayList row = (ArrayList) codeTable.get(i);
				String val = (String) row.get(SIDE_CODE_COLUMN);
				if (val != null && val.equals(code)) {
					Object res = row.get(LOCATION_CODE_COLUMN);
					if (res != null && res.toString().toUpperCase().endsWith("LAB")) {
						return res.toString();
					}
				}
			}
		}
		return null;
	}
	*/
	
	private CodeTableVO getCache(String CacheName) throws CodeTableCacheException {
		CodeTableVO results = null;
		//log.debug("getCache() begin"); 
		// synchronized to ensure only one instance of a specific code table is
		// added to the cache. Actual issue is the time between containsKey and
		// cache.put - SCT
		synchronized (cache) {
			if (!cache.containsKey(CacheName)) {
				log.debug("getCache():"+CacheName + " Not available in local cache.Calling CodeTable EJB");
				try {
//					CodeTableDelegate ctd = new CodeTableDelegate();
					cache.put(CacheName, new CodeTableVO());
				} catch (Exception e) {
					throw new CodeTableCacheException(e.getMessage(), e);
				}
			} /*else
				log.debug("getCache():"+CacheName + " Available in local cache.");*/
		}

		try {
			results = (CodeTableVO) cache.get(CacheName);

			// removed since we really want the beforeFirst to be part of the processing
			// of the rowset so that two threads are not changing the position while it
			// it is being processed - SCT
			// // We need to be sure we have an exclusive lock on this object
			// // not on the method.
			// synchronized (results) {
			// if (results != null) results.beforeFirst();
			// }
		} catch (Exception e) {
			throw new CodeTableCacheException(e.getMessage(), e);
		}
		//log.debug("getCache() end:"+CacheName);
		return results;
	}

	public CachedRowSet getCacheAsRowSet(String CacheName) throws CodeTableCacheException {
		CachedRowSet results = null;

		CodeTableVO vo = getCache(CacheName);
		if (vo != null)
			results = vo.getRowSetData();

		return results;
	}

	public boolean refreshCache() {
		// refreshes all the caches
		return false;
	}

	public boolean refreshCache(String CacheName) {
		// refresh cache given
		return false;
	}
	
	public synchronized List<Properties> getCodeTableAsList(String cacheName) {
		try {
			if (fullCache == null) {
				reloadCache();
			}
			return fullCache.get(cacheName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String codeLookup(String cacheName, String codeColumn, String searchCode, String valueColumn) throws CodeTableCacheException {
		String result = null;
		List<Properties> codeTable = getCodeTableAsList(cacheName);
		
		if (codeTable != null && codeTable.size() > 0) {
			for (Properties row : codeTable) {
				if (row != null) {					
					if (row.get(codeColumn).equals(searchCode)) {
						Object val = row.get(valueColumn);
						result = val.toString();
						break;
					}
				}
			}
		}
		
		return result;
	}

    public String getSiteDescription(String code) 		   throws CodeTableCacheException  
    {
    	return codeLookup(SITES, SITES__SITE_CODE, code, SITES__LABEL); 
    }
    public String getSiteCode(String descr) 		       throws CodeTableCacheException  
    {
    	return codeLookup(SITES, SITES__LABEL, descr, SITES__SITE_CODE); 
    }

	public String getTAsDescription(String code) throws CodeTableCacheException {
		return codeLookup(TA, TA__TA_CODE, code, TA__TA_DESC);
	}

//	public String getTAsCodeAt(int idx) throws CodeTableCacheException {
//		return codeAt(TA, idx, 0);
//	}

	public String getTAsCode(String ta) throws CodeTableCacheException {
		return codeLookup(TA, TA__TA_DESC, ta, TA__TA_CODE);
	}

	public String getProjectsDescription(String code) throws CodeTableCacheException {
		return codeLookup(PROJECTS, PROJECTS__PROJECT_CODE, code, PROJECTS__PROJECT_NAME_DESC);
	}
	
	public String getProjectsCode(String descr) throws CodeTableCacheException {
		return codeLookup(PROJECTS, PROJECTS__PROJECT_NAME_DESC, descr, PROJECTS__PROJECT_CODE);
	}
	
//	public String getCompoundStateDescription(String code) throws CodeTableCacheException {
//		return codeLookup(COMPOUND_STATE, 0, code, 1);
//	}

//	public String getCompoundStateCode(String descr) throws CodeTableCacheException {
//		return codeLookup(COMPOUND_STATE, 1, descr, 0);
//	}

	public String getProtectionDescription(String code) throws CodeTableCacheException {
		return codeLookup(PROTECTION, PROTECTION__PROTECTION_CODE, code, PROTECTION__PROTECTION_DESC);
	}

	public String getProtectionCode(String descr) throws CodeTableCacheException {
		return codeLookup(PROTECTION, PROTECTION__PROTECTION_DESC, descr, PROTECTION__PROTECTION_CODE);
	}

	public String getSourceDescription(String code) throws CodeTableCacheException {
		return codeLookup(SOURCE, SOURCE__SOURCE_CODE, code, SOURCE__SOURCE_DESC);
	}

	public String getSourceCode(String descr) throws CodeTableCacheException {
		return codeLookup(SOURCE, SOURCE__SOURCE_DESC, descr, SOURCE__SOURCE_CODE);
	}

	public String getSourceDetailDescription(String code) throws CodeTableCacheException {
		return codeLookup(SOURCE_DETAIL, SOURCE_DETAIL__SOURCE_DETAIL_CODE, code, SOURCE_DETAIL__SOURCE_DETAIL_DESC);
	}

	public String getSourceDetailCode(String descr) throws CodeTableCacheException {
		return codeLookup(SOURCE_DETAIL, SOURCE_DETAIL__SOURCE_DETAIL_DESC, descr, SOURCE_DETAIL__SOURCE_DETAIL_CODE);
	}

	public String getStereoisomerDescription(String code) throws CodeTableCacheException {
		return codeLookup(STEREOISOMERS, STEREOISOMERS__STEREOISOMER_CODE, code, STEREOISOMERS__STEREOISOMER_DESC);
	}

//	public String getStereoisomerCode(String descr) throws CodeTableCacheException {
//		return codeLookup(STEREOISOMERS, 1, descr, 0);
//	}

	public String getResidualSolventMolWgt(String code) throws CodeTableCacheException {
		return codeLookup(RESIDUAL_SOLV, RESIDUAL_SOLV__RESIDUAL_SOLVENT_CODE, code, RESIDUAL_SOLV__RESIDUAL_SOLVENT_WEIGHT);
	}

	public String getResidualSolventDescription(String code) throws CodeTableCacheException {
		return codeLookup(RESIDUAL_SOLV, RESIDUAL_SOLV__RESIDUAL_SOLVENT_CODE, code, RESIDUAL_SOLV__RESIDUAL_SOLVENT_DESC);
	}

	public String getResidualSolventCode(String descr) throws CodeTableCacheException {
		return codeLookup(RESIDUAL_SOLV, RESIDUAL_SOLV__RESIDUAL_SOLVENT_DESC, descr, RESIDUAL_SOLV__RESIDUAL_SOLVENT_CODE);
	}

	public String getSolubilitySolventDescription(String code) throws CodeTableCacheException {
		return codeLookup(SOLUBLE_SOLV, SOLUBLE_SOLV__SOLUBILITY_SOLVENT_CODE, code, SOLUBLE_SOLV__SOLUBILITY_SOLVENT_DESC);
	}

	public String getSolubilitySolventCode(String descr) throws CodeTableCacheException {
		return codeLookup(SOLUBLE_SOLV, SOLUBLE_SOLV__SOLUBILITY_SOLVENT_DESC, descr, SOLUBLE_SOLV__SOLUBILITY_SOLVENT_CODE);
	}

	public String getHandlingDescription(String code) throws CodeTableCacheException {
		return codeLookup(HANDLING, HANDLING__HANDLING_CODE, code, HANDLING__HANDLING_DESCR);
	}

	public String getHandlingCode(String descr) throws CodeTableCacheException {
		return codeLookup(HANDLING, HANDLING__HANDLING_DESCR, descr, HANDLING__HANDLING_CODE);
	}

	public String getStorageDescription(String code) throws CodeTableCacheException {
		return codeLookup(STORAGE, STORAGE__STORAGE_CODE, code, STORAGE__STORAGE_DESCR);
	}

	public String getStorageCode(String descr) throws CodeTableCacheException {
		return codeLookup(STORAGE, STORAGE__STORAGE_DESCR, descr, STORAGE__STORAGE_CODE);
	}

	public String getHazardDescription(String code) throws CodeTableCacheException {
		return codeLookup(HAZARD, HAZARD__HAZARD_CODE, code, HAZARD__HAZARD_DESCR);
	}

	public String getHazardCode(String descr) throws CodeTableCacheException {
		return codeLookup(HAZARD, HAZARD__HAZARD_DESCR, descr, HAZARD__HAZARD_CODE);
	}
	
	public String getSolventDescription(String code) throws CodeTableCacheException {
		return codeLookup(SOLVENTS, SOLVENTS__SOLVENT_CODE, code, SOLVENTS__SOLVENT_DESC);
	}

	public String getSolventCode(String descr) throws CodeTableCacheException {
		return codeLookup(SOLVENTS, SOLVENTS__SOLVENT_DESC, descr, SOLVENTS__SOLVENT_CODE);
	}
	
//	public String getVialDescription(String code) throws CodeTableCacheException {
//		return codeLookup(VIAL_CONTAINERS, 0, code, 1);
//	}

//	public String getVialCode(String descr) throws CodeTableCacheException {
//		return codeLookup(VIAL_CONTAINERS, 1, descr, 0);
//	}
	
	/*
	public String getLocationDescription(String code) throws CodeTableCacheException {
		return codeLookup(LOCATIONS, 0, code, 1);
	}
	*/

	/*
	public String getLocationCode(String descr) throws CodeTableCacheException {
		return codeLookup(LOCATIONS, 1, descr, 0);
	}
	*/
}
