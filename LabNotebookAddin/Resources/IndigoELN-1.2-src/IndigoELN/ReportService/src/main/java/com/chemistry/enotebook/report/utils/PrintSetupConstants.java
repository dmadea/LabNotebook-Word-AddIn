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
package com.chemistry.enotebook.report.utils;

public class PrintSetupConstants {

	public enum buttonChoices { PREVIEW, EXPORT, PRINT }
	
	public final static String STEPS_DELIMITER = ",";
	
	public final static String REPORT_NAME = "ReportName";
	public final static String FILE_NAME = "filename";
	public final static String NOTEBOOK_NUMBER = "notebookNumber";
	public final static String PAGE_NUMBER = "pageNumber";
    public final static String PAGE_VERSION = "version";
	public final static String OUTPUT_FORMAT = "outputFormat";
	public final static String STOP_AFTER_IMAGE_LOAD_ERROR = "stopAfterImageLoadError";
	public final static String TIME_ZONE = "timeZone";	
	
	public final static String DESIGN_SUFFIX = "rptdesign";
	// Flags to include or hide sections
	public static final String INCLUDE_DETAILS = "details";
	public static final String INCLUDE_REACTION = "reaction";
	public static final String INCLUDE_COMPOUNDS = "compounds";
	public static final String INCLUDE_ATTACHMENTS = "attachments";
	public static final String INCLUDE_FOOTER = "footer";
	public static final String INCLUDE_PROCEDURE = "procedure";
	
	public static final String INCLUDE_SUMMARY_REACTION = "summaryReaction";
	public static final String INCLUDE_REACTIONS_FOR_ALL_STEPS = "allStepsReactions";
	public static final String INCLUDE_FINAL_STEP_REACTION = "finalStepReaction";
	public static final String INCLUDE_REACTION_STEPS = "stepReactions";
	public static final String INCLUDED_REACTION_STEPS = "includedReactionSteps";
	public static final String EXCLUDED_REACTION_STEPS = "excludedReactionSteps";
	public static final String INCLUDE_NO_REACTIONS = "noReactions";
	
	public static final String INCLUDE_STOIC_FOR_ALL_STEPS = "allStepsStoic";
	public static final String INCLUDE_FINAL_STEP_STOIC = "finalStepStoic";
	public static final String INCLUDE_STOIC_STEPS = "stepStoic";
	public static final String INCLUDED_STOIC_STEPS = "includedStoicSteps";
	public static final String INCLUDE_NO_STOIC = "noStoic";
	
	public static final String INCLUDE_MONOMERS_FOR_ALL_STEPS = "allStepsMonomers";
	public static final String INCLUDE_FINAL_STEP_MONOMERS = "finalStepMonomers";
	public static final String INCLUDE_MONOMERS_STEPS = "stepMonomers";
	public static final String INCLUDED_MONOMERS_STEPS = "includedStoicMonomers";
	public static final String INCLUDE_NO_MONOMERS = "noMonomers";

	public static final String INCLUDE_PRODUCTS_FOR_ALL_STEPS = "allStepsProducts";
	public static final String INCLUDE_FINAL_STEP_PRODUCTS = "finalStepProducts";
	public static final String INCLUDE_PRODUCTS_STEPS = "stepProducts";
	public static final String INCLUDED_PRODUCTS_STEPS = "includedStoicProducts";
	public static final String INCLUDE_NO_PRODUCTS = "noProducts";

	public static final String INCLUDE_ANALYTICAL_SUMMARY = "includeAnalyticalSummary";
	public static final String INCLUDE_ANALYTICAL_SERVICE_FILES = "includeAnalyticalServiceFiles";
	public static final String INCLUDE_ALL_ANALYTICAL_SERVICE_INSTRUMENTS = "includeAllAnalyticalServiceFiles";
	public static final String INCLUDED_ANALYTICAL_SERVICE_INSTRUMENTS = "includedAnalyticalServiceInstruments";
	
	public static final String INCLUDE_SYNTHESIS_PLATES_FOR_ALL_STEPS = "allStepsSynthesisPlates";
	public static final String INCLUDE_FINAL_STEP_SYNTHESIS_PLATES = "finalStepSynthesisPlates";
	public static final String INCLUDE_SYNTHESIS_PLATES_STEPS = "stepSynthesisPlates";
	public static final String INCLUDED_SYNTHESIS_PLATES_STEPS = "includedStoicSynthesisPlates";
	public static final String INCLUDE_NO_SYNTHESIS_PLATES = "noSynthesisPlates";

	public static final String INCLUDE_REG_SUMMARY = "includeRegSummary";
	public static final String INCLUDE_REG_DETAILS = "includeRegDetails";
	public static final String INCLUDE_SUBMISSION_DETAILS = "includeSubmissionDetails";
    public static final String INCLUDE_HAZARDS = "includeHazards";
	
	// Experiment report types
	public static final String CONCEPTION = "conception";
	public static final String SINGLETON = "singleton";
	public static final String PARALLEL = "parallel";
	// Report types
	public static final String SYNTHESIS = "synthesis";
	public static final String EXPERIMENT = "experiment";
	// Output formats
	public static final String PDF = "pdf";
	public static final String DOC = "doc";
	public static final String RTF = "rtf";
	
    //table of contents
    public static final String PRINT_TABLE_OF_CONTENTS = "printTableOfContents";
    public static final String SITE_CODE = "siteCode";
    public static final String START_PAGE_NUMBER = "startPageNumber";
    public static final String END_PAGE_NUMBER = "endPageNumber";
	
}
