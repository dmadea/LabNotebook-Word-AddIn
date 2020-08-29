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
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.client.print.gui;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;

import java.util.ArrayList;

/**
 * 
 *
 * This handles saving the user's selected options for the next time he opens the dialog; 
 * saves between sessions also
 * 
 * TODO Add Class Information
 */
public class PrintOptions
{
	boolean subject = false;
	boolean rxnSchema = false;
	boolean stoichTable = false;
	boolean stoicWStructures = false;
	boolean stoicNoStructures = false;
	boolean rxnProcedure = false;
	
	boolean batchStructData = false;

	boolean qcSummary = false;
	boolean qcFiles = false;
	boolean qcAllInstruments = false;
	boolean qcSelectInstruments = false;
	ArrayList qcInstruments = new ArrayList();
	String qcInstrumentsDesc = "";
	
	boolean regSummary = false;
	boolean regDetails = false;
	boolean regSubmissionDetails = false;
	boolean regHazards = false;
	
	boolean attachAll = false;
	boolean attachNone = false;
	boolean attachSelected = false;
	
	boolean signatureFooter = false;
	
	ArrayList attachList = new ArrayList();
	String attachListDesc = "";
	public static boolean NOPREVIEW = false;
	public static boolean PREVIEW = true;
	
	
	public void loadOptions() 
		throws UserPreferenceException
	{
		NotebookUser usr = MasterController.getUser();
		
		String val = usr.getPreference(NotebookUser.PREF_PrintDetailsSubj);
		subject = (val != null && val.equals("YES"));

		val = usr.getPreference(NotebookUser.PREF_PrintDetailsSchema);
		rxnSchema = (val != null && val.equals("YES"));

		val = usr.getPreference(NotebookUser.PREF_PrintDetailsStoich);
		stoichTable = (val != null && val.equals("YES"));

		val = usr.getPreference(NotebookUser.PREF_PrintDetailsStoichStruct);
		stoicWStructures = (val != null && val.equals(NotebookUser.PREF_PrintDetailsStoichWStructures));
		stoicNoStructures = (val != null && val.equals(NotebookUser.PREF_PrintDetailsStoichNoStructures));

		val = usr.getPreference(NotebookUser.PREF_PrintDetailsWorkup);
		rxnProcedure = (val != null && val.equals("YES"));

		val = usr.getPreference(NotebookUser.PREF_PrintBatchDetails);
		batchStructData = (val != null && val.equals("YES"));

		val = usr.getPreference(NotebookUser.PREF_PrintQcSummary);
		qcSummary = (val != null && val.equals("YES"));

		val = usr.getPreference(NotebookUser.PREF_PrintQcFiles);
		qcFiles = (val != null && val.equals("YES"));

		val = usr.getPreference(NotebookUser.PREF_PrintQcInst);
		qcAllInstruments = (val != null && val.equals(NotebookUser.PREF_PrintQcInstAll));
		qcSelectInstruments = (val != null && val.equals(NotebookUser.PREF_PrintQcInstSel));
		
		qcInstrumentsDesc = usr.getPreference(NotebookUser.PREF_PrintQcInstTypes);
		qcInstruments = createInstList(qcInstrumentsDesc);

		val = usr.getPreference(NotebookUser.PREF_PrintRegSummary);
		regSummary = (val != null && val.equals("YES"));

		val = usr.getPreference(NotebookUser.PREF_PrintRegDetails);
		regDetails = (val != null && val.equals("YES"));

		val = usr.getPreference(NotebookUser.PREF_PrintRegSubmission);
		regSubmissionDetails = (val != null && val.equals("YES"));

		val = usr.getPreference(NotebookUser.PREF_PrintRegHazards);
		regHazards = (val != null && val.equals("YES"));

		val = usr.getPreference(NotebookUser.PREF_PrintAttach);
		attachAll = (val != null && val.equals(NotebookUser.PREF_PrintAttachAll));
		attachNone = (val != null && val.equals(NotebookUser.PREF_PrintAttachNone));
		attachSelected = (val != null && val.equals(NotebookUser.PREF_PrintAttachSel));

		val = usr.getPreference(NotebookUser.PREF_PrintSignatureFooter);
		signatureFooter = (val != null && val.equals("YES"));

	}
	
	public void saveOptions()
		throws UserPreferenceException
	{
		NotebookUser usr = MasterController.getUser();
		
		String val = (subject) ? "YES" : "NO";
		usr.setPreference(NotebookUser.PREF_PrintDetailsSubj, val);

		val = (rxnSchema) ? "YES" : "NO";
		usr.setPreference(NotebookUser.PREF_PrintDetailsSchema, val);

		val = (stoichTable) ? "YES" : "NO";
		usr.setPreference(NotebookUser.PREF_PrintDetailsStoich, val);

		if (stoicWStructures) 
			usr.setPreference(NotebookUser.PREF_PrintDetailsStoichStruct, NotebookUser.PREF_PrintDetailsStoichWStructures);
		 else if (stoicNoStructures) 
			usr.setPreference(NotebookUser.PREF_PrintDetailsStoichStruct, NotebookUser.PREF_PrintDetailsStoichNoStructures);
		

		val = (rxnProcedure) ? "YES" : "NO";
		usr.setPreference(NotebookUser.PREF_PrintDetailsWorkup, val);

		val = (batchStructData) ? "YES" : "NO";
		usr.setPreference(NotebookUser.PREF_PrintBatchDetails, val);

		val = (qcSummary) ? "YES" : "NO";
		usr.setPreference(NotebookUser.PREF_PrintQcSummary, val);

		val = (qcFiles) ? "YES" : "NO";
		usr.setPreference(NotebookUser.PREF_PrintQcFiles, val);

		if (qcAllInstruments) {
			usr.setPreference(NotebookUser.PREF_PrintQcInst, NotebookUser.PREF_PrintQcInstAll);
		} else if (qcSelectInstruments) {
			usr.setPreference(NotebookUser.PREF_PrintQcInst, NotebookUser.PREF_PrintQcInstSel);
		}
		if (qcInstrumentsDesc == null) qcInstrumentsDesc = "";
		usr.setPreference(NotebookUser.PREF_PrintQcInstTypes, qcInstrumentsDesc);

		val = (regSummary) ? "YES" : "NO";
		usr.setPreference(NotebookUser.PREF_PrintRegSummary, val);

		val = (regDetails) ? "YES" : "NO";
		usr.setPreference(NotebookUser.PREF_PrintRegDetails, val);

		val = (regSubmissionDetails) ? "YES" : "NO";
		usr.setPreference(NotebookUser.PREF_PrintRegSubmission, val);

		val = (regHazards) ? "YES" : "NO";
		usr.setPreference(NotebookUser.PREF_PrintRegHazards, val);

		if (attachAll) {
			usr.setPreference(NotebookUser.PREF_PrintAttach, NotebookUser.PREF_PrintAttachAll);
		} else if (attachNone) {
			usr.setPreference(NotebookUser.PREF_PrintAttach, NotebookUser.PREF_PrintAttachNone);
		} else if (attachSelected) {
			usr.setPreference(NotebookUser.PREF_PrintAttach, NotebookUser.PREF_PrintAttachSel);
		}
		
		val = (signatureFooter) ? "YES" : "NO";
		usr.setPreference(NotebookUser.PREF_PrintSignatureFooter, val);

		usr.updateUserPrefs();
	}
	
	public void addAttachment(String attach) { attachList.add(attach); }
	
	private ArrayList createInstList(String instDesc)
	{
		ArrayList result = new ArrayList();
		String[] items = instDesc.split(", ");
		if (items != null) {
			for (int i=0; i < items.length; i++)
				result.add(items[i]);
		}
		
		return result;
	}

	/**
	 * @return Returns the subject.
	 */
	public boolean isSubject() {
		return subject;
	}

	/**
	 * @param subject The subject to set.
	 */
	public void setSubject(boolean subject) {
		this.subject = subject;
	}

	/**
	 * @return Returns the rxnSchema.
	 */
	public boolean isRxnSchema() {
		return rxnSchema;
	}

	/**
	 * @param rxnSchema The rxnSchema to set.
	 */
	public void setRxnSchema(boolean rxnSchema) {
		this.rxnSchema = rxnSchema;
	}

	/**
	 * @return Returns the stoichTable.
	 */
	public boolean isStoichTable() {
		return stoichTable;
	}

	/**
	 * @param stoichTable The stoichTable to set.
	 */
	public void setStoichTable(boolean stoichTable) {
		this.stoichTable = stoichTable;
	}

	/**
	 * @return Returns the rxnProcedure.
	 */
	public boolean isRxnProcedure() {
		return rxnProcedure;
	}

	/**
	 * @param rxnProcedure The rxnProcedure to set.
	 */
	public void setRxnProcedure(boolean rxnProcedure) {
		this.rxnProcedure = rxnProcedure;
	}

	/**
	 * @return Returns the batchStructData.
	 */
	public boolean isBatchStructData() {
		return batchStructData;
	}

	/**
	 * @param batchStructData The batchStructData to set.
	 */
	public void setBatchStructData(boolean batchStructData) {
		this.batchStructData = batchStructData;
	}

	/**
	 * @return Returns the attachAll.
	 */
	public boolean isAttachAll() {
		return attachAll;
	}

	/**
	 * @param attachAll The attachAll to set.
	 */
	public void setAttachAll(boolean attachAll) {
		this.attachAll = attachAll;
	}

	/**
	 * @return Returns the qcFiles.
	 */
	public boolean isQcFiles() {
		return qcFiles;
	}

	/**
	 * @param qcFiles The qcFiles to set.
	 */
	public void setQcFiles(boolean qcFiles) {
		this.qcFiles = qcFiles;
	}

	/**
	 * @return Returns the qcSummary.
	 */
	public boolean isQcSummary() {
		return qcSummary;
	}

	/**
	 * @param qcSummary The qcSummary to set.
	 */
	public void setQcSummary(boolean qcSummary) {
		this.qcSummary = qcSummary;
	}

	/**
	 * @return Returns the regDetails.
	 */
	public boolean isRegDetails() {
		return regDetails;
	}

	/**
	 * @param regDetails The regDetails to set.
	 */
	public void setRegDetails(boolean regDetails) {
		this.regDetails = regDetails;
	}

	/**
	 * @return Returns the regHazards.
	 */
	public boolean isRegHazards() {
		return regHazards;
	}

	/**
	 * @param regHazards The regHazards to set.
	 */
	public void setRegHazards(boolean regHazards) {
		this.regHazards = regHazards;
	}

	/**
	 * @return Returns the regSubmissionDetails.
	 */
	public boolean isRegSubmissionDetails() {
		return regSubmissionDetails;
	}

	/**
	 * @param regSubmissionDetails The regSubmissionDetails to set.
	 */
	public void setRegSubmissionDetails(boolean regSubmissionDetails) {
		this.regSubmissionDetails = regSubmissionDetails;
	}

	/**
	 * @return Returns the regSummary.
	 */
	public boolean isRegSummary() {
		return regSummary;
	}

	/**
	 * @param regSummary The regSummary to set.
	 */
	public void setRegSummary(boolean regSummary) {
		this.regSummary = regSummary;
	}
	
	/**
	 * @return Returns the signatureFooter.
	 */
	public boolean isSignatureFooter() {
		return signatureFooter;
	}

	/**
	 * @param signatureFooter The signatureFooter to set.
	 */
	public void setSignatureFooter(boolean signatureFooter) {
		this.signatureFooter = signatureFooter;
	}

	/**
	 * @return Returns the attachSelected.
	 */
	public boolean isAttachSelected() {
		return attachSelected;
	}

	/**
	 * @param attachSelected The attachSelected to set.
	 */
	public void setAttachSelected(boolean attachSelected) {
		this.attachSelected = attachSelected;
	}

	/**
	 * @return Returns the qcSelectInstruments.
	 */
	public boolean isQcSelectInstruments() {
		return qcSelectInstruments;
	}

	/**
	 * @param qcSelectInstruments The qcSelectInstruments to set.
	 */
	public void setQcSelectInstruments(boolean qcSelectInstruments) {
		this.qcSelectInstruments = qcSelectInstruments;
	}

	/**
	 * @return Returns the stoicWStructures.
	 */
	public boolean isStoicWStructures() {
		return stoicWStructures;
	}

	/**
	 * @param stoicWStructures The stoicWStructures to set.
	 */
	public void setStoicWStructures(boolean stoicWStructures) {
		this.stoicWStructures = stoicWStructures;
	}

	/**
	 * @return Returns the stoicNoStructures.
	 */
	public boolean isStoicNoStructures() {
		return stoicNoStructures;
	}

	/**
	 * @param stoicNoStructures The stoicNoStructures to set.
	 */
	public void setStoicNoStructures(boolean stoicNoStructures) {
		this.stoicNoStructures = stoicNoStructures;
	}

	/**
	 * @return Returns the qcAllInstruments.
	 */
	public boolean isQcAllInstruments() {
		return qcAllInstruments;
	}

	/**
	 * @param qcAllInstruments The qcAllInstruments to set.
	 */
	public void setQcAllInstruments(boolean qcAllInstruments) {
		this.qcAllInstruments = qcAllInstruments;
	}

	/**
	 * @return Returns the qcInstrumentsDesc.
	 */
	public String getQcInstrumentsDesc() {
		return qcInstrumentsDesc;
	}

	/**
	 * @param qcInstrumentsDesc The qcInstrumentsDesc to set.
	 */
	public void setQcInstrumentsDesc(String qcInstrumentsDesc) {
		this.qcInstrumentsDesc = qcInstrumentsDesc;
	}

	/**
	 * @return Returns the attachNone.
	 */
	public boolean isAttachNone() {
		return attachNone;
	}

	/**
	 * @param attachNone The attachNone to set.
	 */
	public void setAttachNone(boolean attachNone) {
		this.attachNone = attachNone;
	}

	/**
	 * @return Returns the QC instrument(s) used
	 */
	public ArrayList getQcInstruments() {
		return qcInstruments;
	}

	/**
	 * @return Returns the list of attachments
	 */
	public ArrayList getAttachList() {
		return attachList;
	}

	public void setOptionsRequiredForIP() {
		subject = true;
		rxnSchema = true;
		stoichTable = true;
		stoicWStructures = true;
		rxnProcedure = true;
		batchStructData = true;
		qcSummary = true;
		qcAllInstruments = true;
		regSummary = true;
		regDetails = true;
		regSubmissionDetails = true;
		regHazards = true;		
		attachAll = true;
	}
}
