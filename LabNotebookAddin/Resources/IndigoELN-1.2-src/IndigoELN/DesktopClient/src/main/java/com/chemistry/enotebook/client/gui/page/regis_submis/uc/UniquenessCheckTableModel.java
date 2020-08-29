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
 * Created on Jan 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.regis_submis.uc;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.registration.RegistrationSvcUnavailableException;
import com.chemistry.enotebook.registration.delegate.RegistrationServiceDelegate;
import com.chemistry.enotebook.vnv.classes.UcCompoundInfo;
import com.chemistry.enotebook.vnv.classes.UniquenessCheckVO;
import com.common.chemistry.codetable.CodeTableCache;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class UniquenessCheckTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4579841604462042534L;
	private String columnNames[] = { "Structure", "Properties" };
	private Class columnClasses[] = { UcStructAreaRenderer.class, UcPropertiesAreaRenderer.class };
	private UniquenessCheckVO ucResults = null;

	public UniquenessCheckTableModel(byte[] molStruct, String isomerCode, double mw, String mf, String comment,
			boolean includeLegacy) {
		RegistrationServiceDelegate reg = null;
		try {
			if (comment == null)
				comment = "";
			if (mf == null)
				mf = "";
			reg = new RegistrationServiceDelegate();
			// Get the Results of the uniqueness Checker
			String sMolStruct = new String(molStruct);
			if (sMolStruct.startsWith("\n")) {
				sMolStruct = sMolStruct.substring(1);
				if (sMolStruct.endsWith("\n"))
					sMolStruct = sMolStruct.substring(0, sMolStruct.length() - 2);
			}
			ucResults = reg.checkUniqueness(sMolStruct, isomerCode, includeLegacy);
			if (ucResults != null)
				processResults(sMolStruct, isomerCode, mw, mf, comment);
			fireTableDataChanged();
		} catch (RegistrationSvcUnavailableException e4) {
			JOptionPane.showMessageDialog(null, "Registration Service is currently unavailable, please try again later.");
		} catch (Exception e) {
			try {
				String molData = new String(molStruct);
				ucResults = reg.checkUniqueness(molData, isomerCode, includeLegacy);
				if (ucResults != null)
					processResults(molData, isomerCode, mw, mf, comment);
				fireTableDataChanged();
			} catch (Exception error) {
				error.printStackTrace();
				CeNErrorHandler.getInstance().logErrorMsg(null,
						"The registration SOAP service is not available.\nMessage = " + getMsg(e.getMessage()),
						"External service failure", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public String getMsg(String e) {
		if (e == null)
			return "";
		int msgPos = e.indexOf("UcAccessException:");
		if (msgPos >= 0)
			return e.substring(msgPos);
		else
			return e;
	}

	public void dispose() {
		ucResults = null;
	}

	public int getRowCount() {
		return (getUcResults() != null) ? getUcResults().size() : 0;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int column) {
		return (column >= 0 && column < columnNames.length) ? columnNames[column] : "";
	}

	public Class getColumnClass(int column) {
		return (column >= 0 && column < columnNames.length) ? columnClasses[column] : null;
	}

	public boolean isCellEditable(int nRow, int nCol) {
		return false;
	}

	public Object getValueAt(int nRow, int nCol) {
		if (nRow < 0 || nRow >= getRowCount() || ucResults == null)
			return null;
		else
			return ucResults.getResults().get(nRow);
	}

	public ArrayList<UcCompoundInfo> getUcResults() {
		return (ucResults != null) ? ucResults.getResults() : null;
	}

	public String getUcMessage() {
		return (ucResults != null) ? ucResults.getMessage() : "";
	}

	private void processResults(String molStruct, String isomerCode, double mw, String mf, String comment) {
		int exactCount = 0;
		int idxExact = -1;
		int isomerCount = 0;
		// Results give us isomer code, we want description
		// We also want to determine if there are exact and/or isomer matches
		// If there are exact then we do not display drawn structure
		ArrayList<UcCompoundInfo> list = ucResults.getResults();
		if (list != null) {
			// Need the code tables for the isomer description
			CodeTableCache cache = null;
			try {
				cache = CodeTableCache.getCache();
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
			try {
				int i = 0;
				for (Iterator it = list.iterator(); it.hasNext(); i++) {
					UcCompoundInfo ucRec = (UcCompoundInfo) it.next();
					if (ucRec.isExact()) {
						if (idxExact == -1) {
							idxExact = i;
							ucRec.setIsSelected(true);
						}
						exactCount++;
					}
					if (ucRec.isIsomer())
						isomerCount++;
					if (cache != null && ucRec.getIsomerCode() != null && ucRec.getIsomerCode().length() > 0)
						ucRec.setIsomerDescr(cache.getStereoisomerDescription(ucRec.getIsomerCode()));
					else
						ucRec.setIsomerDescr(ucRec.getIsomerCode());
				}
                String description = cache.getStereoisomerDescription(isomerCode);
                if (description == null)
                    description = isomerCode;
                boolean otherIsomerSelected = (description.toLowerCase().indexOf("other") >= 0);
				if (exactCount == 0 || otherIsomerSelected) {
					UcCompoundInfo ucRec = new UcCompoundInfo();
					ucRec.setMolStruct(molStruct);
					ucRec.setIsomerCode(isomerCode);
					ucRec.setIsomerDescr(description);
					ucRec.setRegNumber("Drawn Structure");
					ucRec.setMolWgt("" + (((int) (mw * 1000.0)) / 1000.0));
					ucRec.setMolFormula(mf);
					ucRec.setComments(comment);
					if (list.size() == 0)
						ucRec.setIsSelected(true);
					list.add(0, ucRec);
				}
				if (exactCount > 0 && isomerCount > 0) { // Exact &
					// isomer
					ucResults.setMessage("Your structure has exact & Stereoisomer match(es) in the database.");
				} else if (exactCount > 0) { // Exact only
					ucResults.setMessage("Your structure has exact match(es) in the database.");
				} else if (isomerCount > 0) { // Exact only
					ucResults.setMessage("Your structure has Stereoisomer match(es) in the database.");
				} else { // Unique??
					ucResults.setMessage("Your structure is unique.");
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
	}
}
