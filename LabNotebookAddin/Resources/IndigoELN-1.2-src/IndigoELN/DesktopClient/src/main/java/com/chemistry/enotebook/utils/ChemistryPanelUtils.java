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
/**
 * 
 */
package com.chemistry.enotebook.utils;


/**
 * 
 *
 */
public class ChemistryPanelUtils 
{
	static private String chemPanel_License = "";
	
	static public void setLicense(String license) {
		chemPanel_License = license;
	}
	
//	static public void clearPanel(ChemistryPanel pnl) {
//		if (pnl != null) {
//			boolean flag = pnl.isReadOnly();
//			try {
//				if (flag) {
//					pnl.setReadOnly(false);
//					pnl.clear();
//				} else
//					pnl.clear();
//			} catch (Exception e) { 
//			} finally {
//				pnl.setReadOnly(flag);
//			}
//			
//		}
//	}
//	
//	static public ChemistryPanel createPanel() {
//		ChemistryPanel cp = new ChemistryPanel(chemPanel_License);
//		cp.setReadOnly(true);
//	    return cp;
//	}
//	static public ChemistryPanel createPanel(String title, String content) {
//		ChemistryPanel cp = new ChemistryPanel(chemPanel_License);
//	    cp.setHostApplicationName(title);
//	    cp.setHostContentName(content);
//	    return cp;
//	}
}
