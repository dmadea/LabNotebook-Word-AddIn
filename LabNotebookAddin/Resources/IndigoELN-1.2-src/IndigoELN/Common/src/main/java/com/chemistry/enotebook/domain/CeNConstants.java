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
package com.chemistry.enotebook.domain;

import java.awt.*;
import java.io.File;

public class CeNConstants {

	public static final String PROGRAM_NAME = "Indigo ELN";
	public static final String SHORT_PROGRAM_NAME = "IndigoELN";
	
	public static final String REPORT_STRUCTURE_LOAD_ERROR = "REPORT_STRUCTURE_LOAD_ERROR";
	
	public static final String PAGE_STATUS_OPEN = "OPEN";
	public static final String PAGE_STATUS_COMPLETE = "COMPLETE";
	public static final String PAGE_STATUS_SUBMITTED = "SUBMITTED";
	public static final String PAGE_STATUS_SUBMIT_FAILED = "SUBMIT_FAIL";
	public static final String PAGE_STATUS_SUBMIT_FAILED_REOPEN = "SUBMIT_REOPEN";
	public static final String PAGE_STATUS_SIGNED = "SIGNED";
	public static final String PAGE_STATUS_SIGNING = "SIGNING";
	public static final String PAGE_STATUS_ARCHIVING = "ARCHIVING";
	public static final String PAGE_STATUS_ARCHIVE_FAILED = "ARCHIVE_FAIL";
	public static final String PAGE_STATUS_ARCHIVED = "ARCHIVED";
	public static final String PAGE_STATUS_DELETED = "DELETED";

	public static final String PAGE_TYPE_MED_CHEM = "MED-CHEM";
	public static final String PAGE_TYPE_PARALLEL = "PARALLEL";
	public static final String PAGE_TYPE_CONCEPTION = "CONCEPTION";

	public static final int PAGE_COPY_LEVEL_TOP = 0; // only those items in this object. Shallow copy - no objects other than
	// strings
	public static final int PAGE_COPY_LEVEL_NBREF = 1; // inlcude TOP and notebook ref
	public static final int PAGE_COPY_LEVEL_REACTION = 2; // include NBREF and all reaction transaction and batch info.
	// BatchNumbers change
	public static final int PAGE_COPY_LEVEL_ATTACHMENTS = 4; // all items including attachments
	public static final int PAGE_COPY_LEVEL_CLONE_ACTUAL_BATCHES = 8; // include actual batches
	public static final int PAGE_COPY_LEVEL_CLONE_ANALYTICAL = 16; // include analytical
	public static final int PAGE_COPY_LEVEL_RETAIN_REGISTRATION_STATUS = 32; // retain registration status of batches.
	public static final int PAGE_COPY_LEVEL_VERSION_EXPERIMENT = 31; // include all batch info, and reg info, but set reg info to
	// not registered.
	public static final int PAGE_COPY_LEVEL_COMPLETE = 63; // all items including attachments and registration status

	public static final String STOICH_TABLE = "<Reactants_Table>";
	public static final String INTENDED_PRODUCTS_TABLE = "<Intended_Products_Table>";
	public static final String STOICH_TABLE_END = "</Reactants_Table>";
	public static final String INTENDED_PRODUCTS_TABLE_END = "</Intended_Products_Table>";

	public static final String BATCH_TYPE_REACTANT = "REACTANT";
	public static final String BATCH_TYPE_REAGENT = "REAGENT";
	public static final String BATCH_TYPE_SOLVENT = "SOLVENT";
	public static final String BATCH_TYPE_ACTUAL = "ACTUAL";
	public static final String BATCH_TYPE_INTENDED = "INTENDED";

	public static final String CEN_CSS_SEARCH_TYPE_REACTIONS = "REACTIONS";
	public static final String CEN_CSS_SEARCH_TYPE_PRODUCTS = "PRODUCTS";

	public static final String CENSTR_ID_PREFIX = "CENSTR";
	public static final String CENRXN_ID_PREFIX = "CENRXN";
	
	public static final int EQUALS = 1;
	public static final int STARTS_WITH = 0;
	public static final int CONTAIN = 2;
	public static final int IS_NOT_CONTAIN = 3;

	public static final int BATCH_STATUS_PASS = 0;
	public static final int BATCH_STATUS_FAIL = 1;
	public static final int BATCH_STATUS_SUSPECT = 2;
	public static final int BATCH_STATUS_NOT_MADE = 3;
	public static final int BATCH_STATUS_PASSED_REGISTERED = 4;
	
	public static final int BATCH_STATUS_SOLUBLE = 0;
	public static final int BATCH_STATUS_INSOLUBLE = 1;
	public static final int BATCH_STATUS_UNAVAILABLE = 2;
	
	public static final int BATCH_STATUS_DISCONTINUE = 0;
	public static final int BATCH_STATUS_CONTINUE = 1;
	
	public static final String REG_BATCH_KEY_SUFFIX = "77";
	
	public static final int INVALID_NUMBER_INTEGER = -1;
	public static final long INVALID_NUMBER_LONG = -1L;
	public static final double INVALID_NUMBER_DOUBLE = -1D;
	// vb 11/15 add string constants for the product batch statuses.
	// Note:  these will have to be updated if the status ints change.
	
	public static final String PASS_CONTINUE = "Passed - Continue";
	public static final String PASS_REGISTERED_CONTINUE = "Passed and Registered - Continue";
	//public static final String PASS_DISCONTINUE = "Pass - Discontinue";
	public static final String SUSPECT_CONTINUE = "Suspect - Continue";
	public static final String SUSPECT_DISCONTINUE = "Suspect - Discontinue";
	public static final String FAIL_CONTINUE = "Failed - Continue";
	public static final String FAIL_DISCONTINUE = "Failed - Discontinue";
	public static final String NOT_MADE_DISCONTINUE = "Not Made - Discontinue";
	
	// vb 8/21 these should be replaced eventually with numbers similar to the 
	// product batch statuses.  Currently only a string is stored in the single 
	// status field of the monomer batch model.
	
	public static final String SOLUBLE_CONTINUE = "Soluble - Continue";
	public static final String INSOLUBLE_CONTINUE = "Insoluble - Continue";
	public static final String INSOLUBLE_DISCONTINUE = "Insoluble - Discontinue";
	public static final String UNAVAILABLE_DISCONTINUE = "Unavailable - Discontinue";

	public static final String CSS_SEARCH_EXACT = "Exact";
	public static final String CSS_SEARCH_SUBSTRUCTURE = "Substructure";
	public static final String CSS_SEARCH_SIMILARITY = "Similarity";

	public static final String CSS_SEARCH_ALGORITHM_EUCLID = "Euclid";
	public static final String CSS_SEARCH_ALGORITHM_TANIMOTO = "Tanimoto";
	public static final String CSS_SEARCH_ALGORITHM_EDS = "EDS";

	public static final int TABLE_VIEW_WIDTH = 550;
	public static final int TABLE_VIEW_ROW_HIGHT = 30;

	public static final String REACTIONTYPE_INTENDED = "INTENDED";
	public static final String REACTIONTYPE_STEP = "STEP";
	public static final String REACTIONTYPE_CONCEPTION = "CONCEPTION";
	public static final String REACTIONTYPE_ACTUAL = "ACTUAL";
	public static final String REACTIONTYPE_GENERIC = "GENERIC";

	public static final String REGINFO_NOT_SUBMITTED = "Not Submitted";
	public static final String REGINFO_SUBMITTED = "Submitted";
	public static final String REGINFO_SUBMITTING = "Submitting";
	public static final String REGINFO_POST_REGISTERING = "Post Registering";
	public static final String REGINFO_POST_SUBMITTING = "Post Submitting";
	public static final String REGINFO_NOT_REGISTERED = "Not Registered";
	public static final String REGINFO_REGISTERED = "Registered";
	public static final String REGINFO_REGISTERING = "Registering";
	public static final String REGINFO_PASSED = "PASSED";
	public static final String REGINFO_FAILED = "FAILED";
	public static final String REGINFO_PENDING = "Pending";	
	public static final String REGINFO_PROCESSING = "PROCESSING";
	
	public static final String REGINFO_SUBMISION_PASS = "Submitted - PASSED"; 
	public static final String REGINFO_SUBMISION_FAIL = "Submitted - FAILED";
	public static final String REGINFO_SUBMISION_PENDING = "Submitted - Pending"; 
	public static final String REGINFO_SUBMISION_WAITING = "Submitted - Waiting";

	
	// Constants
	// Each is independent of the others.
	public static final byte PAGE_BYTE_RETRIEVE_BRIEF_LEVEL = 1; // Load top level information
	public static final byte PAGE_BYTE_RETRIEVE_CONTAINERS = 3; // Load container info
	public static final byte PAGE_BYTE_RETRIEVE_BATCHES = 5; // Load reaction information
	public static final byte PAGE_BYTE_RETRIEVE_RXNS = 9; // Load reaction information
	public static final byte PAGE_BYTE_RETRIEVE_TRANSACTIONS = 17; // Load transaction level info
	public static final byte PAGE_BYTE_RETRIEVE_ANALYTICAL = 33; // Load analytical info
	public static final byte PAGE_BYTE_RETRIEVE_ATTACHMENTS = 65; // Load Attachements
	public static final byte PAGE_BYTE_RETRIEVE_ALL = 127; // Load All Page Information
	public static final int AVERAGE_PLATE_SIZE = 40;

	// XML data constants
	public static final String XML_VERSION_TAG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	
	//CUS key seperator 
	public static final String PUBLISHER_KEY_SPERATOR = "_";
	public static final String PUBLISHER_STRUCTURE_TYPE_REACTION = "REACTION";
	public static final String PUBLISHER_STRUCTURE_TYPE_BATCH = "BATCH";
	
	public static final String STRING_SKETCH_FORMAT_ENCODEDCHIME = "ENCODED CHIME";
	public static final String STRING_SKETCH_FORMAT_ENCODEDMOL = "ENCODED MOL";
	public static final String STRING_SKETCH_FORMAT_MOL = "MOL";
	
	public static final String CONTAINER_TYPE_PLATE = "PLATE";
	public static final String CONTAINER_TYPE_RACK = "RACK";
	public static final String CONTAINER_TYPE_VIAL = "VIAL";
	public static final String CONTAINER_TYPE_TUBE = "TUBE";
	public static final String CONTAINER_TYPE_WELL = "WELL";	
	public static final String CONTAINER_TYPE_TOTE = "TOTE";
	
	public static final String CONTAINER_MAJOR_AXIS_X = "X";
	public static final String CONTAINER_MAJOR_AXIS_Y = "Y";
	public static final String CONTAINER_MAJOR_AXIS_NONE = "N";
	
//	High level models interested in to find if update happened. Granularity of update required
	public static final String UPDATE_NOTEBOOKPAGEHEADER = "NBK_HEADER";
	public static final String UPDATE_REACTIONSTEP_SCHEME = "STEP_SCHEME";
	public static final String UPDATE_MONOMER_BATCH = "MONM_BATCH";
	public static final String UPDATE_PROD_BATCH = "PROD_BATCH";
	public static final String UPDATE_REGT_BATCH = "REGT_BATCH";
	public static final String UPDATE_NBK_COMPLETE = "NBK_COMPLETE";
	public static final String UPDATE_NBK_NOTREQUIRED = "NBK_NONE";
	public static final String UPDATE_REACTIONSTEP_COMPLETE = "STEP_COMPLETE";
	
	public static final String PAGE_LATEST_VERSION_YES = "Y";
	public static final String PAGE_LATEST_VERSION_NO = "N";
	
	public static final String PLATE_TYPE_MONOMER = "MONOMER";
	public static final String PLATE_TYPE_PRODUCT = "PRODUCT";
	public static final String PLATE_TYPE_REGISTRATION = "REGISTRATION";
	public static final String PLATE_TYPE_PRODUCT_PSEUDO = "PRODUCT_PSEUDO";
	public static final String PSEUDO_PLATE_LABEL = "Non-Plated Batches";
	
	public static final String STOIC_POSITION_CONSTANT= "STE";
	public static final String FOR_MONOMERS_CONSTANT= "For Monomers";
	public static final String FOR_PRODUCTS_CONSTANT= "For Products";
	public static final String FOR_STOIC_MONOMERS_CONSTANT= "For Stoic Monomers";
	public static final String PRODUCTS_USER_ADDED = "PUA";
	public static final String PRODUCTS_DESIGN_DSP = "P1";
	public static final String PRODUCTS_SYNC_INTENDED =  "PSI";
	
	//MonBatch,ProdBatch,PlateWell statuses
	public static final String MONOMER_BATCH_STATUS_NOT_AVAILABLE = "NOT_AVAILABLE";
	
	// Plate creation source
	public static final String PLATE_CREATED_FROM_FILE = "File";
	public static final String PLATE_CREATED_FROM_SYNTHESIS_PLAN = "SynthesisPlan";
	public static final String PLATE_CREATED_FROM_OTHER = "Other";
	
	//CEN_AMOUNTS table AMOUNT_NAME column value mapping to Amount type attributes in Model( BAtch,Monomer,Product)
	public static final String MOLECULAR_WEIGHT_AMOUNT = "MOLECULAR_WEIGHT_AMOUNT";
	public static final String MOLE_AMOUNT = "MOLE_AMOUNT";
	public static final String WEIGHT_AMOUNT = "WEIGHT_AMOUNT";
	public static final String LOADING_AMOUNT = "LOADING_AMOUNT";
	public static final String VOLUME_AMOUNT = "VOLUME_AMOUNT";
	public static final String DENSITY_AMOUNT = "DENSITY_AMOUNT";
	public static final String MOLAR_AMOUNT = "MOLAR_AMOUNT";
	public static final String PURITY_AMOUNT = "PURITY_AMOUNT";
	public static final String RXN_EQUIVS_AMOUNT = "RXN_EQUIVS_AMOUNT";
	public static final String THEORETICAL_WEIGHT_AMOUNT = "THEORETICAL_WEIGHT_AMOUNT";
	public static final String THEORETICAL_MOLE_AMOUNT = "THEORETICAL_MOLE_AMOUNT";
	public static final String NEEDED_AMOUNT = "NEEDED_AMOUNT";
	public static final String EXTRA_NEEDED_AMOUNT = "EXTRA_NEEDED_AMOUNT";
	public static final String SOLUTE_AMOUNT = "SOLUTE_AMOUNT";
	public static final String BOILING_POINT_AMOUNT = "BOILING_POINT_AMOUNT";
	public static final String MELTING_POINT_AMOUNT = "MELTING_POINT_AMOUNT";
	public static final String TOTAL_VOLUME = "TOTAL_VOLUME_AMOUNT";
	public static final String TOTAL_WEIGHT = "TOTAL_WEIGHT_AMOUNT";
	public static final String TOTAL_MOLAR_AMOUNT = "TOTAL_MOLAR_AMOUNT";
	public static final String PREVIOUS_MOLAR_AMOUNT = "PREVIOUS_MOLAR_AMOUNT";
	public static final String TOTAL_ORDERED = "TOTAL_ORDERED";
	public static final String DELIVERED_WEIGHT = "DELIVERED_WEIGHT";
	public static final String DELIVERED_VOLUME = "DELIVERED_VOLUME";
	public static final String TOTAL_WELL_WEIGHT_AMOUNT = "TOTAL_WELL_WEIGHT_AMOUNT";
	public static final String TOTAL_TUBE_WEIGHT_AMOUNT = "TOTAL_TUBE_WEIGHT_AMOUNT";
	public static final String TOTAL_WELL_VOLUME_AMOUNT = "TOTAL_WELL_VOLUME_AMOUNT";
	public static final String TOTAL_TUBE_VOLUME_AMOUNT = "TOTAL_TUBE_VOLUME_AMOUNT";
	
	//PLATE_WELL Amounts
	public static final String WELL_WEIGHT_AMOUNT = "WELL_WEIGHT_AMOUNT";
	public static final String WELL_VOLUME_AMOUNT = "WELL_VOLUME_AMOUNT";
	public static final String WELL_MOLARITY_AMOUNT = "WELL_MOLARITY_AMOUNT";
	
	// Registration job status
	public static final String JOB_OPEN = "OPEN";
	public static final String JOB_PROGRESS = "PROGRESS";
	public static final String JOB_FINISHED = "FINISHED";
	public static final String JOB_FAILED = "FAILED";
		
	// Registration workflow names
	public static final String WORKFLOW_COMPOUND_REGISTRATION = "CompoundRegistration";
	public static final String WORKFLOW_COMPOUND_MANAGEMENT = "CompoundManagement";
	public static final String WORKFLOW_PURIFICATION_SERVICE = "PurificationService";
	public static final String WORKFLOW_COMPOUND_AGGREGATION = "CompoundAggregation";
	
	//overall registration status
	public static final String PASS = "PASS";
	public static final String FAIL = "FAIL";
	
	//CompoundRegistration Registration Status
	public static final String COMPOUND_REGISTRATION_REGISTERED = "Registered";
	public static final String COMPOUND_REGISTRATION_NOT_REGISTERED = "NotRegistered";
	
	//CompoundRegistration Submission Status
	public static final String COMPOUND_REGISTRATION_SUBMITTED = "Submitted";
	public static final String COMPOUND_REGISTRATION_NOT_SUBMITTED = "NotSubmitted";
	
	//CompoundRegistration Job Status
	public static final String COMPOUND_REGISTRATION_JOB_STATUS_COMPLETE = "Complete";
	public static final String COMPOUND_REGISTRATION_JOB_STATUS_PENDING = "Pending";
	
	//CompoundRegistration Error STATUS
	public static final String COMPOUND_REGISTRATION_LOAD_FAILED = "LOAD_FAILED";
	
	//AnalyticalService Instrument types
	public static final String INSTRUMENT_TYPE_NMR = "NMR";
	public static final String INSTRUMENT_TYPE_HPLC = "HPLC";
	public static final String INSTRUMENT_TYPE_MS = "MS";
	public static final String INSTRUMENT_TYPE_SFC_MS = "SFC-MS";
	
	public static final Color BACKGROUND_COLOR = new Color(122, 194, 174); //new Color(190, 190, 220);
	
	public static final int JETTY_STATUS_LISTENER_PORT = 65432;
	
	// Sorting criteria
	public static final String SORT_BY_BATCH_NUMBER = "Synthesis Plan Sequence";
	public static final String SORT_BY_COMPOUND_NUMBER = "Compound ID";
	public static final String SORT_BY_MW = "Molecular Weight";
	public static final String SORT_BY_ORDER_SEQ = "Order Sequence";
	public static final String SORT_BY_SYNTHESIS_PLAN_SEQ = "Synthesis Plan Sequence";
	
	//Jetty Message constants
	public static final String JETTY_MESSAGE_REGISTRATION_ACTION = "Registration Status Update";
	
	//Fonts
	//Table Cell fonts
	public static final String TABLE_CELL_FONT = "Arial";
	//Other fonts.

	public static String getApplicationDirectory() {
		return System.getProperty("user.home") + File.separator + "." + CeNConstants.SHORT_PROGRAM_NAME;
	}
}
