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
 * Created on Jan 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.utils;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.RegQualitativeCache;
import com.chemistry.enotebook.domain.ContainerTypeLocationModel;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.util.*;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class CodeTableUtils {
	private static final Log log = LogFactory.getLog(CodeTableUtils.class);
	private CodeTableUtils() {
	}

	public static void fillComboBox(JComboBox jcb, String cacheName, String codeColumn, String descColumn, boolean includeCode,
			boolean addNone) {
		fillComboBox(jcb, cacheName, codeColumn, descColumn, includeCode, addNone, null, null, null, null);
	}

	public static void fillComboBox(JComboBox jcb, String cacheName, String codeColumn, String descColumn, boolean includeCode,
			boolean addNone, String filterColumn, String filterValue, String secondFilterCol , String secondFilterValue) {
		if (jcb != null) {
			jcb.removeAllItems();
			if (addNone)
				jcb.addItem("-None-");
			try {
				List li = CodeTableCache.getInstance().getCodeTableAsList(cacheName);
				// really what we are interested in is making sure we have the
				// lock on the list object not on the method itself.
				Properties row = null;
				for (int i = 0; i < li.size(); i++) {
					row = (Properties) li.get(i);
					String item;
					if (includeCode)
						item = row.get(codeColumn) + " - " + row.get(descColumn);
					else
						item =  row.get(descColumn) + "";
					if (filterValue != null && filterValue.length() > 0) {
						if (filterValue.equals(row.get(filterColumn)) && secondFilterValue.equals(row.get(secondFilterCol)))
						{
							jcb.addItem(item);
						}
					} else
						jcb.addItem(item);
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
	}
	
	public static void fillComboBox(JComboBox jcb, List codes, List descriptions, boolean includeCode, boolean addNone) {
		if (jcb != null) {
			jcb.removeAllItems();
			if (addNone)
				jcb.addItem("-None-");
			try {
				for (int i = 0; i < descriptions.size(); i++) {
					String item = "";
					String description =  (String)descriptions.get(i);
					if (includeCode)
						item = codes.get(i) + " - " + description;
					else
						item = description;
					
					jcb.addItem(item);
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
	}
	
	public static void fillComboBox(JComboBox jcb, String cacheName, String codeColumn, String descColumn, boolean includeCode,
			boolean addNone, String filterColumn, String filterValue) {
		if (jcb != null) {
			jcb.removeAllItems();
			if (addNone)
				jcb.addItem("-None-");
			try {
				List li = CodeTableCache.getInstance().getCodeTableAsList(cacheName);
				// really what we are interested in is making sure we have the
				// lock on the list object not on the method itself.
				Properties row = null;
				for (int i = 0; i < li.size(); i++) {
					row = (Properties) li.get(i);
					String item;
					if (includeCode)
						item = row.get(codeColumn) + " - " + row.get(descColumn);
					else
						item = "" + row.get(descColumn);
					if (filterValue != null && filterValue.length() > 0) {
						if (filterValue.equals(row.get(filterColumn)))
							jcb.addItem(item);
					} else
						jcb.addItem(item);
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
	}
	
	public static void fillComboBoxWithSaltCodes(JComboBox jcb) {
		fillComboBox(jcb, CodeTableCache.SALTS, CodeTableCache.SALTS__SALT_CODE, CodeTableCache.SALTS__SALT_DESC, true, false);
	}

	public static void fillComboBoxWithCompoundStates(JComboBox jcb) {
		fillComboBox(jcb, CodeTableCache.COMPOUND_STATE, CodeTableCache.COMPOUND_STATE__COMPOUND_STATE_CODE, CodeTableCache.COMPOUND_STATE__COMPOUND_STATE_DESC, true, false);
	}

	public static void fillComboBoxWithTAs(JComboBox jcb) {
		fillComboBox(jcb, CodeTableCache.TA, CodeTableCache.TA__TA_CODE, CodeTableCache.TA__TA_DESC, false, true);
	}

	public static void fillComboBoxWithProjects(JComboBox jcb, String ta) {
		fillComboBox(jcb, CodeTableCache.PROJECTS, CodeTableCache.PROJECTS__PROJECT_CODE, CodeTableCache.PROJECTS__PROJECT_NAME_DESC, true, true, CodeTableCache.TA__TA_CODE, ta);
	}

	public static void fillComboBoxWithSites(JComboBox jcb) {
		fillComboBox(jcb, CodeTableCache.SITES, CodeTableCache.SITES__SITE_CODE, CodeTableCache.SITES__LABEL, false, true);
	}

	public static void fillComboBoxWithIsomers(JComboBox jcb) {
		fillComboBox(jcb, CodeTableCache.STEREOISOMERS, CodeTableCache.STEREOISOMERS__STEREOISOMER_CODE, CodeTableCache.STEREOISOMERS__STEREOISOMER_DESC, true, false);
	}

	public static void fillComboBoxWithResiduals(JComboBox jcb) {
		fillComboBox(jcb, CodeTableCache.RESIDUAL_SOLV, CodeTableCache.RESIDUAL_SOLV__RESIDUAL_SOLVENT_CODE, CodeTableCache.RESIDUAL_SOLV__RESIDUAL_SOLVENT_DESC, false, false);
	}

	public static void fillComboBoxWithSolubles(JComboBox jcb) {
		fillComboBox(jcb, CodeTableCache.SOLUBLE_SOLV, CodeTableCache.SOLUBLE_SOLV__SOLUBILITY_SOLVENT_CODE, CodeTableCache.SOLUBLE_SOLV__SOLUBILITY_SOLVENT_DESC, false, false);
	}
	
	public static void fillComboBoxWithContainerTypes(JComboBox jcb) {
		jcb.addItem(new ContainerTypeLocationModel("-None-", ""));
    
		try {
			String siteCode = MasterController.getUser().getSiteCode();
			
			String type = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_CONTAINER_VIAL_TYPE, siteCode);  // used on client
			String location = "";
      
			if (StringUtils.isNotBlank(type)) {
				location = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_CONTAINER_VIAL_LOCATION, siteCode);
				jcb.addItem(new ContainerTypeLocationModel(type, location));
			}
      
			type = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_CONTAINER_TUBE_TYPE, siteCode);
      
			if (StringUtils.isNotBlank(type)) {
				location = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_CONTAINER_TUBE_LOCATION, siteCode);
				jcb.addItem(new ContainerTypeLocationModel(type, location));
			}
		} catch (Exception e) {
			log.warn("Problem filling combo box", e);
		}
	}
  
	public static String getDescriptionGivenCode(String queryCode, Map map) {
		if (map.containsKey(queryCode)) {
			return (String) (map.get(queryCode));
		}
		return null;
	}
	
	
	public static String getCodeGivenDescription(String description, Map map) {
		Set s = map.keySet();
		Iterator iter = s.iterator();
		while(iter.hasNext())
		{
			String code = (String) iter.next();
			String desc = (String)map.get(code);
			if (desc.equals(description))
				return code;
		}
		return null;
	}

	public static void fillComboBoxWithSolvents(JComboBox jcb) {
		fillComboBox(jcb, CodeTableCache.SOLVENTS, CodeTableCache.SOLVENTS__SOLVENT_CODE, CodeTableCache.SOLVENTS__SOLVENT_DESC, false, true);
	}
	
	public static List<RegQualitativeCache> getQualitativeList() {
		List<RegQualitativeCache> solventqualitativeList = new ArrayList<RegQualitativeCache>();
		try {
			List<Properties> qualitativeList = CodeTableCache.getInstance().getCodeTableAsList(CodeTableCache.COMPOUND_REGISTRATION_QUAL_SOLUBILITY_CDT);
			
			for(Properties item : qualitativeList) {
				RegQualitativeCache regQualitativeCache = new RegQualitativeCache();
				regQualitativeCache.setCode(item.getProperty(CodeTableCache.COMPOUND_REGISTRATION_QUAL_SOLUBILITY_CDT_CODE));
				regQualitativeCache.setDescription(item.getProperty(CodeTableCache.COMPOUND_REGISTRATION_QUAL_SOLUBILITY_CDT_DESC));
				solventqualitativeList.add(regQualitativeCache);
			}
			
		} catch(CodeTableCacheException e) {
			log.error("Problem filling qualitative list", e);
		}
		return solventqualitativeList;
	}
}
