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
package com.chemistry.enotebook.storage.utils.print;

import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingletonPrintOptions implements PrintOptions
{
	public static final Log log = LogFactory.getLog(SingletonPrintOptions.class);

	boolean includeSubjectTitle = false;
	boolean includeReactionScheme = false;
	boolean includeStoichTable = false;
	boolean includeProcedure = false;
	boolean includeBatchDetails = false;

	boolean includeAnalyticalSummary = false;
	boolean includeAnalyticalFiles = false;
	boolean includeAnalyticalAllInstruments = false;
	boolean includeAnalyticalSelectInstruments = false;
	ArrayList analyticalInstruments = new ArrayList();
	String analyticalInstrumentsDesc = "";

	boolean includeRegSummary = false;
	boolean includeRegDetails = false;
	boolean includeRegSubmissionDetails = false;
	boolean includeRegHazards = false;

	boolean attachAll = false;
	boolean attachNone = false;

	boolean includeSignatureFooter = false;

	ArrayList attachList = new ArrayList();
	String attachListDesc = "";
	public static boolean NOPREVIEW = false;
	public static boolean PREVIEW = true;

	private static String YES = "YES";
	private static String NO = "NO";
	
	public NotebookUser loadOptions(NotebookUser usr)
		throws UserPreferenceException
	{
		String val = usr.getPreference(NotebookUser.PREF_PrintSingletonExperimentSubject);
		includeSubjectTitle = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintSingletonExperimentReactionScheme);
		includeReactionScheme = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintSingletonExperimentStoicTable);
		includeStoichTable = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintSingletonExperimentProcedure);
		includeProcedure = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintSingletonExperimentBatchDetails);
		includeBatchDetails = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintSingletonAnalyticalSummary);
		includeAnalyticalSummary = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintSingletonAnalyticalAnalyticalServiceDataFiles);
		includeAnalyticalFiles = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintSingletonAnalyticalInstruments);
		includeAnalyticalAllInstruments = (val != null && val.equals(NotebookUser.PREF_PrintSingletonAnalyticalInstAll));
		includeAnalyticalSelectInstruments = (val != null && val.equals(NotebookUser.PREF_PrintSingletonAnalyticalInstSel));

		analyticalInstrumentsDesc = usr.getPreference(NotebookUser.PREF_PrintSingletonAnalyticalInstTypes);
		analyticalInstruments = createInstList(analyticalInstrumentsDesc);

		val = usr.getPreference(NotebookUser.PREF_PrintSingletonRegistrationSummary);
		includeRegSummary = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintSingletonRegistrationDetails);
		includeRegDetails = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintSingletonRegistrationSubmissionDetails);
		includeRegSubmissionDetails = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintSingletonRegistrationHazards);
		includeRegHazards = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintSingletonAttach);
		attachAll = (val != null && val.equals(NotebookUser.PREF_PrintSingletonAttachAll));
		attachNone = (val != null && val.equals(NotebookUser.PREF_PrintSingletonAttachNone));

		val = usr.getPreference(NotebookUser.PREF_PrintSingletonSignatureFooter);
		includeSignatureFooter = (val != null && val.equals(YES));
		
		return usr;
	}

	public NotebookUser saveOptions(NotebookUser usr) 
		throws UserPreferenceException
	{
		String val = (this.includeSubjectTitle) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintSingletonExperimentSubject, val);

		val = (this.includeReactionScheme) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintSingletonExperimentReactionScheme, val);

		val = (this.includeStoichTable) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintSingletonExperimentStoicTable, val);

		val = (this.includeProcedure) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintSingletonExperimentProcedure, val);

		val = (this.includeBatchDetails) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintSingletonExperimentBatchDetails, val);

		val = (this.includeAnalyticalSummary) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintSingletonAnalyticalSummary, val);

		val = (this.includeAnalyticalFiles) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintSingletonAnalyticalAnalyticalServiceDataFiles, val);

		if (this.includeAnalyticalAllInstruments) {
			usr.setPreference(NotebookUser.PREF_PrintSingletonAnalyticalContent, NotebookUser.PREF_PrintSingletonAnalyticalInstAll);
		} else if (this.includeAnalyticalSelectInstruments) {
			usr.setPreference(NotebookUser.PREF_PrintSingletonAnalyticalContent, NotebookUser.PREF_PrintSingletonAnalyticalInstSel);
		}
		if (this.analyticalInstrumentsDesc == null) this.analyticalInstrumentsDesc = "";
		usr.setPreference(NotebookUser.PREF_PrintSingletonAnalyticalInstTypes, this.analyticalInstrumentsDesc);

		val = (this.includeRegSummary) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintSingletonRegistrationSummary, val);

		val = (this.includeRegDetails) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintSingletonRegistrationDetails, val);

		val = (includeRegSubmissionDetails) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintSingletonRegistrationSubmissionDetails, val);

		val = (includeRegHazards) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintSingletonRegistrationHazards, val);

		if (attachAll) {
			usr.setPreference(NotebookUser.PREF_PrintSingletonAttach, NotebookUser.PREF_PrintSingletonAttachAll);
		} else if (attachNone) {
			usr.setPreference(NotebookUser.PREF_PrintSingletonAttach, NotebookUser.PREF_PrintSingletonAttachNone);
		} 

		val = (includeSignatureFooter) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintSingletonSignatureFooter, val);

		usr.updateUserPrefs();
		
		return usr;
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
	
	public void setOptionsRequiredForIP() {
		this.includeSubjectTitle = true;
		this.includeReactionScheme = true;
		this.includeBatchDetails = true;
		this.includeProcedure = true;
		this.includeRegSummary = true;
		this.includeRegDetails = true;
		this.includeRegSubmissionDetails = true;
		this.includeRegHazards = true;
		this.includeAnalyticalSummary = true;
		this.includeAnalyticalAllInstruments = true;
		this.attachAll = true;
        this.includeStoichTable = true;
        this.includeAnalyticalFiles = true;
//		this.includeSignatureFooter = true;
	}

	public Map getOptions() {
		// experiment contents
		Map optionsMap = new HashMap();
		optionsMap.put(PrintSetupConstants.INCLUDE_DETAILS, this.includeSubjectTitle ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_ANALYTICAL_SUMMARY, this.includeAnalyticalSummary ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_PROCEDURE, this.includeProcedure ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_FOOTER, this.includeSignatureFooter ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_ATTACHMENTS, this.attachAll ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_ANALYTICAL_SERVICE_FILES, this.includeAnalyticalFiles ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_ALL_ANALYTICAL_SERVICE_INSTRUMENTS, this.includeAnalyticalAllInstruments ? "true" : "false");
		if (! this.includeAnalyticalAllInstruments) {
			optionsMap.put(PrintSetupConstants.INCLUDED_ANALYTICAL_SERVICE_INSTRUMENTS, this.analyticalInstrumentsDesc);
		}

		if (this.includeReactionScheme) {
			optionsMap.put(PrintSetupConstants.INCLUDE_FINAL_STEP_REACTION, this.includeReactionScheme ? "true" : "false");
		} else {
			optionsMap.put(PrintSetupConstants.INCLUDE_NO_REACTIONS, "true");
		}
		// show/hide stoic table preferences
		if (this.includeStoichTable) {		
			optionsMap.put(PrintSetupConstants.INCLUDE_FINAL_STEP_STOIC, this.includeStoichTable ? "true" : "false");
		} else {
			optionsMap.put(PrintSetupConstants.INCLUDE_NO_STOIC, "true");
		}
		// show/hide monomer batches preferences
		if (this.includeBatchDetails) {
			optionsMap.put(PrintSetupConstants.INCLUDE_FINAL_STEP_MONOMERS, this.includeBatchDetails ? "true" : "false");
		} else {
			optionsMap.put(PrintSetupConstants.INCLUDE_NO_MONOMERS, "true");
		}
		// show/hide product batches preferences
		if (this.includeBatchDetails) {			
			optionsMap.put(PrintSetupConstants.INCLUDE_FINAL_STEP_PRODUCTS, this.includeBatchDetails ? "true" : "false");
		} else {
			optionsMap.put(PrintSetupConstants.INCLUDE_NO_PRODUCTS, "true");
		}
		// show/hide registration information
		optionsMap.put(PrintSetupConstants.INCLUDE_REG_SUMMARY, this.includeRegSummary ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_REG_DETAILS, this.includeRegDetails ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_SUBMISSION_DETAILS, this.includeRegSubmissionDetails ? "true" : "false");
        optionsMap.put(PrintSetupConstants.INCLUDE_HAZARDS, this.includeRegHazards ? "true" : "false");

//		optionsMap.put(PrintSetupConstants.INCLUDE_ATTACHMENTS, this.includeIpRelatedAttachments ? "true" : "false");
//		optionsMap.put(PrintSetupConstants.INCLUDE_FOOTER, this.includeSignatureFooter ? "true" : "false");
//		optionsMap.put(PrintSetupConstants.INCLUDE_PROCEDURE, this.includeUtilityInventionNotes ? "true" : "false");
		return optionsMap;
	}
}