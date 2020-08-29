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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParallelPrintOptions  implements PrintOptions
{
	boolean includeDetails = false;
	
	boolean includeReactionSchemes = false;
	boolean includeReactionSchemeSummary = false;
	boolean includeReactionSchemeAllSteps = false;
	boolean includeReactionSchemeFinalStep = false;
	boolean includeReactionSchemeSelectSteps = false;
	List reactionSchemeSelectedStepsList = new ArrayList();
	String reactionSchemeSelectedStepsDesc = "";

	boolean includeStoicTable = false;
	boolean includeStoicTableAllSteps = false;
	boolean includeStoicTableFinalStep = false;
	boolean includeStoicTableSelectSteps = false;
	List stoicTableSelectedStepsList = new ArrayList();
	String stoicTableSelectedStepsDesc = "";	

	boolean includeMonomerBatches = false;
	boolean includeMonomerBatchesAllSteps = false;
	boolean includeMonomerBatchesFinalStep = false;
	boolean includeMonomerBatchesSelectSteps = false;
	List monomerBatchesSelectedStepsList = new ArrayList();
	String monomerBatchesSelectedStepsDesc = "";	

	boolean includeProductBatches = false;
	boolean includeProductBatchesAllSteps = false;
	boolean includeProductBatchesFinalStep = false;
	boolean includeProductBatchesSelectSteps = false;
	List productBatchesSelectedStepsList = new ArrayList();
	String productBatchesSelectedStepsDesc = "";	

	boolean includeProcedure = false;

	boolean includeAnalyticalSummary = false;
	boolean includeAnalyticalFiles = false;
	boolean includeAllAnalyticalServiceInstruments = false;
	boolean includeSelectedAnalyticalServiceInstruments = false;
	List selectedAnalyticalServiceInstruments = new ArrayList();
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
		String val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentSubject);
		includeDetails = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentReactionSchemes);
		this.includeReactionSchemes = (val != null && val.equals(YES));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentReactionSchemeSummary);
		this.includeReactionSchemeSummary = (val != null && val.equals(YES));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentReactionSchemeAllSteps);
		this.includeReactionSchemeAllSteps = (val != null && val.equals(YES));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentReactionSchemeFinalStep);
		this.includeReactionSchemeFinalStep = (val != null && val.equals(YES));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentReactionSchemeSelectSteps);
		this.includeReactionSchemeSelectSteps = (val != null && val.equals(YES));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentReactionSchemeSelectedSteps);
		this.reactionSchemeSelectedStepsDesc = (val != null ? val : "");

		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentStoicTable);
		this.includeStoicTable = (val != null && val.equals(YES));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentStoicTableAllSteps);
		this.includeStoicTableAllSteps = (val != null && val.equals(YES));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentStoicTableFinalStep);
		this.includeStoicTableFinalStep = (val != null && val.equals(YES));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentStoicTableSelectSteps);
		this.includeStoicTableSelectSteps = (val != null && val.equals(YES));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentStoicTableSelectedSteps);
		this.stoicTableSelectedStepsDesc = (val != null ? val : "");

		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentMonomerBatches);
		this.includeMonomerBatches = (val != null && val.equals(YES));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentMonomerBatchesAllSteps);
		this.includeMonomerBatchesAllSteps = (val != null && val.equals(YES));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentMonomerBatchesFinalStep);
		this.includeMonomerBatchesFinalStep = (val != null && val.equals(YES));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentMonomerBatchesSelectSteps);
		this.includeMonomerBatchesSelectSteps = (val != null && val.equals(YES));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentMonomerBatchesSelectedSteps);
		this.monomerBatchesSelectedStepsDesc = (val != null ? val : "");

		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentProductBatches);
		this.includeProductBatches = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentProductBatchesAllSteps);
		this.includeProductBatchesAllSteps = (val != null && val.equals(YES));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentProductBatchesFinalStep);
		this.includeProductBatchesFinalStep = (val != null && val.equals(YES));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentProductBatchesSelectSteps);
		this.includeProductBatchesSelectSteps = (val != null && val.equals(YES));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelExperimentProductBatchesSelectedSteps);
		this.productBatchesSelectedStepsDesc = (val != null ? val : "");		
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelAnalyticalSummary);
		includeAnalyticalSummary = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintParallelAnalyticalAnalyticalServiceDataFiles);
		includeAnalyticalFiles = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintParallelAnalyticalInstruments);
		this.includeAllAnalyticalServiceInstruments = (val != null && val.equals(NotebookUser.PREF_PrintParallelAnalyticalInstAll));
		
		val = usr.getPreference(NotebookUser.PREF_PrintParallelAnalyticalInstSel);
		this.includeAllAnalyticalServiceInstruments = (val != null && val.equals(NotebookUser.PREF_PrintParallelAnalyticalInstSel));

		analyticalInstrumentsDesc = usr.getPreference(NotebookUser.PREF_PrintParallelAnalyticalInstTypes);
		this.selectedAnalyticalServiceInstruments = createInstList(analyticalInstrumentsDesc);

		val = usr.getPreference(NotebookUser.PREF_PrintParallelRegistrationSummary);
		includeRegSummary = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintParallelRegistrationDetails);
		includeRegDetails = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintParallelRegistrationSubmissionDetails);
		includeRegSubmissionDetails = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintParallelRegistrationHazards);
		includeRegHazards = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintParallelAttach);
		attachAll = (val != null && val.equals(NotebookUser.PREF_PrintParallelAttachAll));
		attachNone = (val != null && val.equals(NotebookUser.PREF_PrintParallelAttachNone));

		val = usr.getPreference(NotebookUser.PREF_PrintParallelSignatureFooter);
		includeSignatureFooter = (val != null && val.equals(YES));
		
		return usr;
	}
	
	public NotebookUser saveOptions(NotebookUser usr) 
		throws UserPreferenceException
	{
		String val = (this.includeDetails) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentSubject, val);

		val = (this.includeReactionSchemes) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentReactionSchemes, val);
		
		val = (this.includeReactionSchemeSummary) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentReactionSchemeSummary, val);

		val = (this.includeReactionSchemeAllSteps) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentReactionSchemeAllSteps, val);

		val = (this.includeReactionSchemeFinalStep) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentReactionSchemeFinalStep, val);
	
		val = (this.includeReactionSchemeSelectSteps) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentReactionSchemeSelectSteps, val);		
		
		val = (this.reactionSchemeSelectedStepsDesc != null) ? this.reactionSchemeSelectedStepsDesc : "";
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentReactionSchemeSelectedSteps, val);				

		val = (this.includeStoicTable) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentStoicTable, val);

		val = (this.includeStoicTableAllSteps) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentStoicTableAllSteps, val);

		val = (this.includeStoicTableFinalStep) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentStoicTableFinalStep, val);
	
		val = (this.includeStoicTableSelectSteps) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentStoicTableSelectSteps, val);		
		
		val = (this.stoicTableSelectedStepsDesc != null) ? this.stoicTableSelectedStepsDesc : "";
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentStoicTableSelectedSteps, val);				

		val = (this.includeMonomerBatches) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentMonomerBatches, val);

		val = (this.includeMonomerBatchesAllSteps) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentMonomerBatchesAllSteps, val);

		val = (this.includeMonomerBatchesFinalStep) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentMonomerBatchesFinalStep, val);
	
		val = (this.includeMonomerBatchesSelectSteps) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentMonomerBatchesSelectSteps, val);		
		
		val = (this.monomerBatchesSelectedStepsDesc != null) ? this.monomerBatchesSelectedStepsDesc : "";
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentMonomerBatchesSelectedSteps, val);				

		val = (this.includeProductBatches) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentProductBatches, val);

		val = (this.includeProductBatchesAllSteps) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentProductBatchesAllSteps, val);

		val = (this.includeProductBatchesFinalStep) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentProductBatchesFinalStep, val);
	
		val = (this.includeProductBatchesSelectSteps) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentProductBatchesSelectSteps, val);		
		
		val = (this.productBatchesSelectedStepsDesc != null) ? this.productBatchesSelectedStepsDesc : "";
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentProductBatchesSelectedSteps, val);				

		val = (this.includeProcedure) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelExperimentProcedure, val);

		val = (this.includeAnalyticalSummary) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelAnalyticalSummary, val);

		val = (this.includeAnalyticalFiles) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelAnalyticalAnalyticalServiceDataFiles, val);

		if (this.includeAllAnalyticalServiceInstruments) {
			usr.setPreference(NotebookUser.PREF_PrintParallelAnalyticalContent, NotebookUser.PREF_PrintQcInstAll);
		} else if (this.includeSelectedAnalyticalServiceInstruments) {
			usr.setPreference(NotebookUser.PREF_PrintParallelAnalyticalContent, NotebookUser.PREF_PrintQcInstSel);
		}
		if (this.analyticalInstrumentsDesc == null) this.analyticalInstrumentsDesc = "";
		usr.setPreference(NotebookUser.PREF_PrintParallelAnalyticalInstTypes, this.analyticalInstrumentsDesc);

		val = (this.includeRegSummary) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelRegistrationSummary, val);

		val = (this.includeRegDetails) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelRegistrationDetails, val);

		val = (includeRegSubmissionDetails) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelRegistrationSubmissionDetails, val);

		val = (includeRegHazards) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelRegistrationHazards, val);

		if (attachAll) {
			usr.setPreference(NotebookUser.PREF_PrintParallelAttach, NotebookUser.PREF_PrintParallelAttachAll);
		} else if (attachNone) {
			usr.setPreference(NotebookUser.PREF_PrintParallelAttach, NotebookUser.PREF_PrintParallelAttachNone);
		} 

		val = (includeSignatureFooter) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintParallelSignatureFooter, val);

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
		this.includeDetails = true;
		this.includeReactionSchemes = true;
		this.includeReactionSchemeAllSteps = true;
		this.includeStoicTable = true;
		this.includeStoicTableAllSteps = true;
		this.includeProcedure = true;
		this.includeMonomerBatches = true;
		this.includeMonomerBatchesAllSteps = true;
		this.includeProductBatches = true;
		this.includeProductBatchesAllSteps = true;
		this.includeAnalyticalSummary = true;
		this.includeAnalyticalFiles = true;
		this.includeAllAnalyticalServiceInstruments = true;
		this.includeSelectedAnalyticalServiceInstruments = true;
		this.analyticalInstrumentsDesc = "";
		this.includeRegSummary = true;
		this.includeRegDetails = true;
		this.includeRegSubmissionDetails = true;
		this.includeRegHazards = true;
		this.attachAll = true;
		this.attachNone = false;
		//this.includeSignatureFooter = true;
	}

	public Map getOptions() {
		// experiment contents
		Map optionsMap = new HashMap();
		optionsMap.put(PrintSetupConstants.INCLUDE_DETAILS, this.includeDetails ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_ANALYTICAL_SUMMARY, this.includeAnalyticalSummary ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_PROCEDURE, this.includeProcedure ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_FOOTER, this.includeSignatureFooter ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_ATTACHMENTS, this.attachAll ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_ANALYTICAL_SERVICE_FILES, this.includeAnalyticalFiles ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_ALL_ANALYTICAL_SERVICE_INSTRUMENTS, this.includeAllAnalyticalServiceInstruments ? "true" : "false");
		if (! this.includeAllAnalyticalServiceInstruments) {
			optionsMap.put(PrintSetupConstants.INCLUDED_ANALYTICAL_SERVICE_INSTRUMENTS, this.analyticalInstrumentsDesc);
		}
		//optionsMap.put(PrintSetupConstants., NO)
		// show/hide reaction scheme preferences
		if (this.includeReactionSchemes) {
			optionsMap.put(PrintSetupConstants.INCLUDE_SUMMARY_REACTION, this.includeReactionSchemeSummary ? "true" : "false");
			optionsMap.put(PrintSetupConstants.INCLUDE_REACTIONS_FOR_ALL_STEPS, this.includeReactionSchemeAllSteps ? "true" : "false");
			optionsMap.put(PrintSetupConstants.INCLUDE_FINAL_STEP_REACTION, this.includeReactionSchemeFinalStep ? "true" : "false");
			optionsMap.put(PrintSetupConstants.INCLUDE_REACTION_STEPS, this.includeReactionSchemeSelectSteps ? "true" : "false");
			if (this.includeReactionSchemeSelectSteps) {
//				StringBuffer buff = new StringBuffer();
//				for (Iterator it = this.reactionSchemeSelectedStepsList.iterator(); it.hasNext();) {
//					buff.append(it.next().toString());
//					if (!it.hasNext())
//						continue;
//					else
//						buff.append(",");
//				}
				optionsMap.put(PrintSetupConstants.INCLUDED_REACTION_STEPS, this.reactionSchemeSelectedStepsDesc);
			}
		} else {
			optionsMap.put(PrintSetupConstants.INCLUDE_NO_REACTIONS, "true");
		}
		// show/hide stoic table preferences
		if (this.includeStoicTable) {
			optionsMap.put(PrintSetupConstants.INCLUDE_STOIC_FOR_ALL_STEPS, this.includeStoicTableAllSteps ? "true" : "false");
			optionsMap.put(PrintSetupConstants.INCLUDE_FINAL_STEP_STOIC, this.includeStoicTableFinalStep ? "true" : "false");
			optionsMap.put(PrintSetupConstants.INCLUDE_STOIC_STEPS, this.includeStoicTableSelectSteps ? "true" : "false");
			if (this.includeStoicTableSelectSteps) {
//				StringBuffer buff = new StringBuffer();
//				for (Iterator it = this.stoicTableSelectedStepsList.iterator(); it.hasNext();) {
//					buff.append(it.next().toString());
//					if (!it.hasNext())
//						continue;
//					else
//						buff.append(",");
//				}
				optionsMap.put(PrintSetupConstants.INCLUDED_STOIC_STEPS, this.stoicTableSelectedStepsDesc);
			}
		} else {
			optionsMap.put(PrintSetupConstants.INCLUDE_NO_STOIC, "true");
		}
		// show/hide monomer batches preferences
		if (this.includeMonomerBatches) {
			optionsMap.put(PrintSetupConstants.INCLUDE_MONOMERS_FOR_ALL_STEPS, this.includeMonomerBatchesAllSteps ? "true" : "false");
			optionsMap.put(PrintSetupConstants.INCLUDE_FINAL_STEP_MONOMERS, this.includeMonomerBatchesFinalStep ? "true" : "false");
			optionsMap.put(PrintSetupConstants.INCLUDE_MONOMERS_STEPS, this.includeMonomerBatchesSelectSteps ? "true" : "false");
			if (this.includeMonomerBatchesSelectSteps) {
//				StringBuffer buff = new StringBuffer();
//				for (Iterator it = this.monomerBatchesSelectedStepsList.iterator(); it.hasNext();) {
//					buff.append(it.next().toString());
//					if (!it.hasNext())
//						continue;
//					else
//						buff.append(",");
//				}
				optionsMap.put(PrintSetupConstants.INCLUDED_MONOMERS_STEPS, this.monomerBatchesSelectedStepsDesc);
			}
		} else {
			optionsMap.put(PrintSetupConstants.INCLUDE_NO_MONOMERS, "true");
		}
		// show/hide product batches preferences
		if (this.includeProductBatches) {
			optionsMap.put(PrintSetupConstants.INCLUDE_PRODUCTS_FOR_ALL_STEPS, this.includeProductBatchesAllSteps ? "true" : "false");
			optionsMap.put(PrintSetupConstants.INCLUDE_FINAL_STEP_PRODUCTS, this.includeProductBatchesFinalStep ? "true" : "false");
			optionsMap.put(PrintSetupConstants.INCLUDE_PRODUCTS_STEPS, this.includeProductBatchesSelectSteps ? "true" : "false");
			if (this.includeProductBatchesSelectSteps) {
				optionsMap.put(PrintSetupConstants.INCLUDED_PRODUCTS_STEPS, this.productBatchesSelectedStepsDesc);
			}
		} else {
			optionsMap.put(PrintSetupConstants.INCLUDE_NO_PRODUCTS, "true");
		}
		// show/hide registration information
		optionsMap.put(PrintSetupConstants.INCLUDE_REG_SUMMARY, this.includeRegSummary ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_REG_DETAILS, this.includeRegDetails ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_SUBMISSION_DETAILS, this.includeRegSubmissionDetails ? "true" : "false");
		
//		optionsMap.put(PrintSetupConstants.INCLUDE_ATTACHMENTS, this.includeIpRelatedAttachments ? "true" : "false");
//		optionsMap.put(PrintSetupConstants.INCLUDE_FOOTER, this.includeSignatureFooter ? "true" : "false");
//		optionsMap.put(PrintSetupConstants.INCLUDE_PROCEDURE, this.includeUtilityInventionNotes ? "true" : "false");
		return optionsMap;
	}
}