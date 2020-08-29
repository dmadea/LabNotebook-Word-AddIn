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
 * Created on 11-May-2004
 * 
 *
 */
package com.chemistry.enotebook.experiment.datamodel.user;

import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.utils.xml.JDomUtilException;
import com.chemistry.enotebook.experiment.utils.xml.JDomUtils;
import com.chemistry.enotebook.session.delegate.AuthenticationException;
import com.chemistry.enotebook.session.delegate.SessionTokenAccessException;
import com.chemistry.enotebook.session.delegate.SessionTokenDelegate;
import com.chemistry.enotebook.session.security.HttpUserProfile;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.session.security.SessionToken;
import com.chemistry.enotebook.session.security.UserData;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotebookUser implements Serializable {

	private static final long serialVersionUID = -1312923854707511178L;

	private static final Log log = LogFactory.getLog(NotebookUser.class);
	
	// XML_METADATA fields:

	public static final String PREF_HEALTH_CHECK = "/User_Properties/Preferences/Startup/PerfromHealthCheck";
	public static final String PREF_SB_OUTLOOK_BAR = "/User_Properties/Preferences/Startup/DefaultSpeedBar";
	public static final String MY_BOOKMARKS = "MY_BOOKMARKS";
	public static final String ALL_SITES = "ALL_SITES";
	public static final String PREF_DEFAULT_START = "/User_Properties/Preferences/Startup/DefaultDisplay";
	public static final String WELCOME = "WELCOME"; // Flag to indicate what to display in speedbar: PREF_DEFAULT_START's value
	public static final String LAST_EXPERIMENT = "LAST_EXPERIMENT"; // Flag to indicate what to display in speedbar:
	// PREF_DEFAULT_START's value

	public static final String PREF_DEFAULT_EDITOR = "/User_Properties/Preferences/DefaultEditor";
	public static final String KETCHER = "KETCHER";
	public static final String ISIS_DRAW = "ISIS_DRAW";

	public static final String PREF_GUI_WIDTH = "/User_Properties/Preferences/Gui_Width";
	public static final String PREF_GUI_HEIGHT = "/User_Properties/Preferences/Gui_Height";

	public static final String PREF_AUTOSAVE_INTERVAL = "/User_Properties/Auto-Save/Interval";

	public static final String PREF_VIEW_MEM_STATS = "/User_Properties/Preferences/ViewMemoryStats";
	public static final String PREF_ENABLE_OFFLINE_REPORT = "/User_Properties/Preferences/OfflineReport";	
	public static final String PREF_ESIG_USE = "/User_Properties/Preferences/ESignature/UseOnComplete";
	public static final String PREF_ESIG_LAUNCH_URL = "/User_Properties/Preferences/ESignature/LaunchEsigONComplete";
	public static final String PREF_ESIG_LAST_TEMPLATE = "/User_Properties/Preferences/ESignature/LastTemplate";


	public static final String PREF_STAT_NUM_ACCESSES = "/User_Properties/Statistics/NumTimesAccessed";
	public static final String PREF_STAT_LAST_ACCESS = "/User_Properties/Statistics/LastTimeAccessed";

	public static final String PREF_EmployeeID = "/User_Properties/EmployeeID";
	public static final String PREF_SuperUser = "/User_Properties/Preferences@SuperUser";
	public static final String PREF_NBLookNFeel = "/User_Properties/Preferences/NB@Look-N-Feel";
	public static final String PREF_CurrentNbRef = "/User_Properties/Preferences/NB/Current_Nb"; // Notebook REf of last
	// experiment modified
	public static final String PREF_CurrentNbRefVer = "/User_Properties/Preferences/NB/Current_Nb@Version"; // Notebook REf of last
	// experiment modified
	public static final String PREF_LastNBRef = "/User_Properties/Preferences/NB/Last_Nb_Ref"; // Notebook Ref of last experiment
	// created
	public static final String PREF_OwnerNTUserID = "/User_Properties/NotebookOwnerUserID";
	public static final String PREF_LastSite = "/User_Properties/Preferences/NB/Last_Nb_Site";
	public static final String PREF_LastCGB = "/User_Properties/Preferences/NB/Last_CGB";
	public static final String PREF_LastProtocolID = "/User_Properties/Preferences/NB/Last_ProtocolID";
	public static final String PREF_LastSeriesID = "/User_Properties/Preferences/NB/Last_SeriesID";
	public static final String PREF_SupervisorFor = "/User_Properties/SupervisorFor";

	public static final String PREF_LastMMSiteCode = "/User_Properties/Preferences/NB/Last_Mm_SiteCode";

	public static final String PREF_UseTA = "/User_Properties/Preferences/NB/Use_TA_Code";
	public static final String PREF_TA = "/User_Properties/Preferences/NB/TA_Code";
	public static final String PREF_PC = "/User_Properties/Preferences/NB/Proj_Code";
	
	public static final String PREF_LastTA = "/User_Properties/Preferences/NB/Last_TA_Code";
	public static final String PREF_LastProject = "/User_Properties/Preferences/NB/Last_Project_Code";
	public static final String PREF_LastSource = "/User_Properties/Preferences/NB/Last_Source_Code";
	public static final String PREF_LastSourceDetail = "/User_Properties/Preferences/NB/Last_SourceDetail_Code";
	public static final String PREF_LastOwner = "/User_Properties/Preferences/NB/Last_Owner";
	// Table Preferences
	// public static final String PREF_Table_Properties = "/User_Properties/Preferences/Table_Properties";
	public static final String PREF_Intended_Products = "/User_Properties/Preferences/Intended_Products_Table";
	public static final String PREF_Reactants_Viewer = "/User_Properties/Preferences/Reactants_Table";
	public static final String PREF_Mass_Amount_Unit_Code = "/User_Properties/Preferences/Mass_Amount_Unit_Code";
	public static final String PREF_Volume_Amount_Unit_Code = "/User_Properties/Preferences/Volume_Amount_Unit_Code";
	public static final String PREF_Molar_Amount_Unit_Code = "/User_Properties/Preferences/Molar_Amount_Unit_Code";
	public static final String PREF_Moles_Amount_Unit_Code = "/User_Properties/Preferences/Moles_Amount_Unit_Code";

	// Print options for Print Types tab
	public static final String PREF_PrintTypesExperiment = "/User_Properties/Preferences/Print/Options/PrintTypes/Experiment";
	public static final String PREF_PrintTypesSynthPlates = "/User_Properties/Preferences/Print/Options/PrintTypes/SynthPlates/SynthPlates";
	public static final String PREF_PrintTypesSynthPlatesSteps = "/User_Properties/Preferences/Print/Options/PrintTypes/SynthPlates/Steps";
	public static final String PREF_PrintTypesSynthPlatesAllSteps = "ALL";
	public static final String PREF_PrintTypesSynthPlatesSelSteps = "SELECTED";
	public static final String PREF_PrintTypesSynthPlatesFinalStep = "FINAL";
	public static final String PREF_PrintTypesSynthPlatesStepsText = "/User_Properties/Preferences/Print/Options/PrintTypes/SynthPlates/StepsText";
	
	// Print options for conception experiment
	public static final String PREF_PrintConceptionConceptDetails = "/User_Properties/Preferences/Print/Options/Conception/ConceptDetails";
	public static final String PREF_PrintConceptionReactionTarget = "/User_Properties/Preferences/Print/Options/Conception/ReactionTarget";
	public static final String PREF_PrintConceptionUtilityInventionNotes = "/User_Properties/Preferences/Print/Options/Conception/UtilityInventionNotes";
	public static final String PREF_PrintConceptionPreferredCompounds = "/User_Properties/Preferences/Print/Options/Conception/PreferredCompounds";
	public static final String PREF_PrintConceptionIPRelatedAttachments = "/User_Properties/Preferences/Print/Options/Conception/IPRelatedAttachments";
	public static final String PREF_PrintConceptionSignatureFooter = "/User_Properties/Preferences/Print/Options/Conception/SignatureFooter";

	// Print options for singleton experiment
	public static final String PREF_PrintSingletonExperimentSubject = "/User_Properties/Preferences/Print/Options/Singleton/Experiment/Subject";	
	public static final String PREF_PrintSingletonExperimentReactionScheme = "/User_Properties/Preferences/Print/Options/Singleton/Experiment/ReactionScheme";	
	public static final String PREF_PrintSingletonExperimentStoicTable = "/User_Properties/Preferences/Print/Options/Singleton/Experiment/StoicTable";	
	public static final String PREF_PrintSingletonExperimentProcedure = "/User_Properties/Preferences/Print/Options/Singleton/Experiment/Procedure";	
	public static final String PREF_PrintSingletonExperimentBatchDetails = "/User_Properties/Preferences/Print/Options/Singleton/Experiment/BatchDetails";

	public static final String PREF_PrintSingletonAnalyticalSummary = "/User_Properties/Preferences/Print/Options/Singleton/Analytical/Summary";
	public static final String PREF_PrintSingletonAnalyticalAnalyticalServiceDataFiles = "/User_Properties/Preferences/Print/Options/Singleton/Analytical/AnalyticalServiceDataFiles";
	public static final String PREF_PrintSingletonAnalyticalInstruments = "/User_Properties/Preferences/Print/Options/Singleton/Analytical/Instruments";
	public static final String PREF_PrintSingletonAnalyticalContent = "/User_Properties/Preferences/Print/Options/Singleton/Analytical/Content";
	public static final String PREF_PrintSingletonAnalyticalInstAll = "ALL";
	public static final String PREF_PrintSingletonAnalyticalInstSel = "SELECTED";
	public static final String PREF_PrintSingletonAnalyticalInstTypes = "/User_Properties/Preferences/Print/Options/Singleton/Analytical/Instruments/Types";

	public static final String PREF_PrintSingletonRegistrationSummary = "/User_Properties/Preferences/Print/Options/Singleton/Registration/Summary";
	public static final String PREF_PrintSingletonRegistrationDetails = "/User_Properties/Preferences/Print/Options/Singleton/Registration/RegDetails";
	public static final String PREF_PrintSingletonRegistrationSubmissionDetails = "/User_Properties/Preferences/Print/Options/Singleton/Registration/SubDetails";
	public static final String PREF_PrintSingletonRegistrationHazards = "/User_Properties/Preferences/Print/Options/Singleton/Registration/Hazards";
	
	public static final String PREF_PrintSingletonAttach = "/User_Properties/Preferences/Print/Options/Singleton/Attach_Details";
	public static final String PREF_PrintSingletonAttachAll = "ALL";
	public static final String PREF_PrintSingletonAttachNone = "NONE";

	public static final String PREF_PrintSingletonSignatureFooter = "/User_Properties/Preferences/Print/Options/Singleton/Print_Signature";
	
	// Print options for Parallel experiment
	public static final String PREF_PrintParallelExperimentSubject = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/Subject";	
	public static final String PREF_PrintParallelExperimentReactionSchemes = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/ReactionScheme/ReactionSchemes";
	public static final String PREF_PrintParallelExperimentReactionSchemeSummary = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/ReactionScheme/Summary";	
	public static final String PREF_PrintParallelExperimentReactionSchemeAllSteps = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/ReactionScheme/AllSteps";
	public static final String PREF_PrintParallelExperimentReactionSchemeFinalStep = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/ReactionScheme/FinalStep";
	public static final String PREF_PrintParallelExperimentReactionSchemeSelectSteps = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/ReactionScheme/SelectSteps";
	public static final String PREF_PrintParallelExperimentReactionSchemeSelectedSteps = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/ReactionScheme/SelectedSteps";

	public static final String PREF_PrintParallelExperimentStoicTable = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/StoicTable/StoicTable";
	public static final String PREF_PrintParallelExperimentStoicTableAllSteps = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/StoicTable/AllSteps";
	public static final String PREF_PrintParallelExperimentStoicTableFinalStep = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/StoicTable/FinalStep";
	public static final String PREF_PrintParallelExperimentStoicTableSelectSteps = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/StoicTable/SelectSteps";
	public static final String PREF_PrintParallelExperimentStoicTableSelectedSteps = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/StoicTable/SelectedSteps";

	public static final String PREF_PrintParallelExperimentMonomerBatches = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/MonomerBatches/MonomerBatches";
	public static final String PREF_PrintParallelExperimentMonomerBatchesAllSteps = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/MonomerBatches/AllSteps";
	public static final String PREF_PrintParallelExperimentMonomerBatchesFinalStep = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/MonomerBatches/FinalStep";
	public static final String PREF_PrintParallelExperimentMonomerBatchesSelectSteps = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/MonomerBatches/SelectSteps";
	public static final String PREF_PrintParallelExperimentMonomerBatchesSelectedSteps = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/MonomerBatches/SelecteeSteps";

	public static final String PREF_PrintParallelExperimentProductBatches = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/ProductBatches/ProductBatches";
	public static final String PREF_PrintParallelExperimentProductBatchesAllSteps = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/ProductBatches/AllSteps";
	public static final String PREF_PrintParallelExperimentProductBatchesFinalStep = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/ProductBatches/FinalStep";
	public static final String PREF_PrintParallelExperimentProductBatchesSelectSteps = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/ProductBatches/SelectSteps";
	public static final String PREF_PrintParallelExperimentProductBatchesSelectedSteps = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/ProductBatches/SelectedSteps";

	public static final String PREF_PrintParallelExperimentProcedure = "/User_Properties/Preferences/Print/Options/Parallel/Experiment/Procedure";	

	public static final String PREF_PrintParallelAnalyticalSummary = "/User_Properties/Preferences/Print/Options/Parallel/Analytical/Summary";
	public static final String PREF_PrintParallelAnalyticalAnalyticalServiceDataFiles = "/User_Properties/Preferences/Print/Options/Parallel/Analytical/AnalyticalServiceDataFiles";
	public static final String PREF_PrintParallelAnalyticalInstruments = "/User_Properties/Preferences/Print/Options/Parallel/Analytical/Instruments";
	public static final String PREF_PrintParallelAnalyticalContent = "/User_Properties/Preferences/Print/Options/Parallel/Analytical/Content";
	public static final String PREF_PrintParallelAnalyticalInstAll = "ALL";
	public static final String PREF_PrintParallelAnalyticalInstSel = "SELECTED";
	public static final String PREF_PrintParallelAnalyticalInstTypes = "/User_Properties/Preferences/Print/Options/Parallel/Analytical/Instruments/Types";

	public static final String PREF_PrintParallelRegistrationSummary = "/User_Properties/Preferences/Print/Options/Parallel/Registration/Summary";
	public static final String PREF_PrintParallelRegistrationDetails = "/User_Properties/Preferences/Print/Options/Parallel/Registration/Details";
	public static final String PREF_PrintParallelRegistrationSubmissionDetails = "/User_Properties/Preferences/Print/Options/Parallel/Registration/SubmissionDetails";
	public static final String PREF_PrintParallelRegistrationHazards = "/User_Properties/Preferences/Print/Options/Parallel/Registration/Hazards";
	
	public static final String PREF_PrintParallelAttach = "/User_Properties/Preferences/Print/Options/Parallel/Attach_Details";
	public static final String PREF_PrintParallelAttachAll = "ALL";
	public static final String PREF_PrintParallelAttachNone = "NONE";

	public static final String PREF_PrintParallelSignatureFooter = "/User_Properties/Preferences/Print/Options/Parallel/Print_Signature";
	
	// CeN 1.1
	public static final String PREF_PrintDetailsSubj = "/User_Properties/Preferences/Print/Options/Experiment_Details/Subject";
	public static final String PREF_PrintDetailsSchema = "/User_Properties/Preferences/Print/Options/Experiment_Details/RxnSchema";
	public static final String PREF_PrintDetailsStoich = "/User_Properties/Preferences/Print/Options/Experiment_Details/Stoichiometry";
	public static final String PREF_PrintDetailsStoichStruct = "/User_Properties/Preferences/Print/Options/Experiment_Details/StoichStructures";
	public static final String PREF_PrintDetailsStoichWStructures = "WITH";
	public static final String PREF_PrintDetailsStoichNoStructures = "WITHOUT";
	public static final String PREF_PrintDetailsWorkup = "/User_Properties/Preferences/Print/Options/Experiment_Details/RxnWorkup";

	public static final String PREF_PrintBatchDetails = "/User_Properties/Preferences/Print/Options/Batch_Details/StructDetails";

	public static final String PREF_PrintQcSummary = "/User_Properties/Preferences/Print/Options/Qc_Details/Summary";
	public static final String PREF_PrintQcFiles = "/User_Properties/Preferences/Print/Options/Qc_Details/DataFiles";
	public static final String PREF_PrintQcInst = "/User_Properties/Preferences/Print/Options/Qc_Details/Instruments";
	public static final String PREF_PrintQcInstAll = "ALL";
	public static final String PREF_PrintQcInstSel = "SELECTED";
	public static final String PREF_PrintQcInstTypes = "/User_Properties/Preferences/Print/Options/Qc_Details/Instruments/Types";

	public static final String PREF_PrintRegSummary = "/User_Properties/Preferences/Print/Options/Reg_Details/Summary";
	public static final String PREF_PrintRegDetails = "/User_Properties/Preferences/Print/Options/Reg_Details/RegDetails";
	public static final String PREF_PrintRegSubmission = "/User_Properties/Preferences/Print/Options/Reg_Details/SubDetails";
	public static final String PREF_PrintRegHazards = "/User_Properties/Preferences/Print/Options/Reg_Details/Hazards";

	public static final String PREF_PrintAttach = "/User_Properties/Preferences/Print/Options/Attach_Details";
	public static final String PREF_PrintAttachAll = "ALL";
	public static final String PREF_PrintAttachNone = "NONE";
	public static final String PREF_PrintAttachSel = "SELECTED";

	public static final String PREF_PrintSignatureFooter = "/User_Properties/Preferences/Print/Options/Print_Signature";

	public static final String PREF_STRUCTURE_DATABASES = "/User_Properties/Preferences/Structure_Databases";
	public static final String PREF_CEN_STRUCTURE_DATABASES = "Cen";	
	public static final String PREF_STRUCTURE_DATABASES_CHILD="Database";
	//XXX need correct default values
	public static final String[] DEFAULT_STRUCTURE_DATABASES = {
			"DBF_10/DBF_1/DB_2/", "DBF_10/DBF_1/DBF_2/DB_5/", "DBF_10/DBF_1/DBF_2/DB_6/",
			"DBF_10/DBF_1/DBF_2/DB_7/", "DBF_10/DBF_1/DBF_2/DB_8/",
			"DBF_10/DBF_1/DBF_2/DB_10/", "DBF_10/DBF_1/DBF_2/DB_19/",
			"DBF_10/DBF_1/DBF_2/DB_20/", "DBF_10/DBF_1/DBF_2/DB_21/",
			"DBF_10/DBF_1/DBF_3/DB_9/", "DBF_10/DBF_1/DBF_3/DB_13/",
			"DBF_10/DBF_4/DB_100/" };
 
	public static final String PREF_PATH_ANALYTICAL_UPLOAD = "/User_Properties/Preferences/Paths/UploadAnalytical";
	public static final String PREF_PATH_ANALYTICAL_SAVE = "/User_Properties/Preferences/Paths/DownloadAnalytical";
	public static final String PREF_PATH_ATTACHEMENT_UPLOAD = "/User_Properties/Preferences/Paths/UploadAttachement";
	public static final String PREF_PATH_ATTACHEMENT_SAVE = "/User_Properties/Preferences/Paths/DownloadAttachement";
	public static final String PREF_PATH_REG_SDF_SAVE = "/User_Properties/Preferences/Paths/RegistrationSDF";

	
	private String ntUserID = ""; // NT User ID.
	private String fullName = ""; // Name for user from db
	private String displayName = ""; // First Middle Initial Last
	private String siteCode = ""; // SiteCode related to db field
	private String ntDomain = ""; // NT Domain name
	private boolean valid = false; // Indicates whether user is allowed to login.
	private Document userPrefs = null; // User preferences info from DB via session manager
	private boolean superUserFlagSet = false; // Double checked superUser status.

	private SessionToken userSessionToken = null; // Login information from csl
	private UserData userPreferenceData = null;

//	private static NotebookUser _instance = null;
	private boolean prefChanges = false;

	public NotebookUser() {
	}

	public NotebookUser(String ntUserID) {
		setNTUserID(ntUserID);
	}

	public String getNTUserID() {
		return ntUserID;
	}

	public void setNTUserID(String id) {
		if (id != null)
			id = id.toUpperCase();
		ntUserID = id;
	}

	/**
	 * 
	 * @return String representing the NT Domain to which the user belongs
	 */
	public String getNtDomain() {
		return ntDomain;
	}

	public boolean authenticate(String password) 
		throws LoginException, SessionTokenAccessException, AuthenticationException
	{
		return authenticate(password, null); 
	}

	public boolean authenticate(String password, String impersonateUser) 
		throws LoginException, SessionTokenAccessException, AuthenticationException 
	{
		// Authenticate the user
		SessionToken token = null;
		try {
        	if (impersonateUser != null)
				token = new SessionTokenDelegate().impersonate(getNTUserID(), password, impersonateUser);
        	else
        		token = new SessionTokenDelegate().login(getNTUserID(), password);
		} catch (Exception e2) {
			userSessionToken = null;
			valid = false;
			throw new LoginException("Failure processing login information: " + e2.getLocalizedMessage(), e2);
		}
		InetAddress addr = null;
		try{
			token.getSessionIdentifier().setPassword(password);
			addr = InetAddress.getLocalHost();
			HttpUserProfile userProfile = new HttpUserProfile(addr.getHostAddress(),token.getNtUser());
			token.getSessionIdentifier().setUserProfile(userProfile);
		}catch(Exception error){
			log.error("Failure getting Session Identifier (token) for user: " + ntUserID + 
			          (addr != null ? " at address: " + addr.getHostAddress() : ""));
		}
	    
		
		
		if (token != null)
			setToken(token);

		return true;
	}

	public void logout() throws SessionTokenAccessException {
		if (getUserSessionToken() != null) {
			try {
                new SessionTokenDelegate().logout(getUserSessionToken().getSessionIdentifier());
			} catch (Exception e) {
				throw new SessionTokenAccessException("", e);
			}
			userSessionToken = null;
			valid = false;
		}
	}

	public boolean isSessionValid() {
		return (userSessionToken != null);
	}

	public boolean isSuperUser() {
		return isSessionValid() && (superUserFlagSet || userSessionToken.getSessionIdentifier().isSuperUser());
	}

	public void setSuperUserFlag(boolean flag) {
		superUserFlagSet = flag;
	}

	public SessionIdentifier getSessionIdentifier() {
		return (isSessionValid()) ? userSessionToken.getSessionIdentifier() : null;
	}

	/**
	 * @param token
	 *            The token to set.
	 */
	private void setToken(SessionToken token) throws LoginException, SessionTokenAccessException {
		userSessionToken = token;

		if (token != null) {
			userPreferenceData = token.getUserData();
			ntDomain = token.getNtDomain();
			if (userPreferenceData != null) {
				setFullName(userPreferenceData.getFullUserName());
				siteCode = userPreferenceData.getSiteCode();
				valid = userPreferenceData.isValidUser();

				try {
					userPrefs = JDomUtils.getDocFromString(userPreferenceData.getXmlMetaData());
				} catch (IOException e) {
					throw new LoginException("Could not read string: userData.getXmlMetaData():\n"
							+ userPreferenceData.getXmlMetaData(), e);
				} catch (Exception e) {
					throw new LoginException("Error for input = " + userPreferenceData.getXmlMetaData(), e);
				}

				try {
					// TODO: Note: load testing showed a problem with threading here.
					String sNumAccesses = getPreference(PREF_STAT_NUM_ACCESSES);
					if (sNumAccesses.length() == 0)
						sNumAccesses = "0";
					int iNumAccesses = (new Integer(sNumAccesses).intValue()) + 1;
					setPreference(PREF_STAT_NUM_ACCESSES, "" + iNumAccesses);
					setPreference(PREF_STAT_LAST_ACCESS, new Date().toString());
				} catch (Exception e) {
					throw new LoginException("Failed to update number times accessed value.", e);
				}

				try {
					updateUserPrefs();
				} catch (Exception e) {
					throw new LoginException("Failed to update db with user preference changes.", e);
				}
			}
		}
	}

	public void dispose() {
		userPreferenceData = null;
		userSessionToken = null;
//		_instance = null;
	}

	/**
	 * @return Returns the fullName.
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName
	 *            The fullName to set.
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
		displayName = getDisplayNameFromFullName(fullName);
	}

	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return Returns the siteCode.
	 */
	public String getSiteCode() {
		return siteCode;
	}

	/**
	 * @param siteCode
	 *            The siteCode to set.
	 */
	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	/**
	 * @return Returns the valid.
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid
	 *            The valid to set.
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	private String getDisplayNameFromFullName(String fullName) {
		StringBuffer displayThis = new StringBuffer();
		if (fullName != null) {
			String[] results = fullName.split(" ");
			if (results.length > 0) {
				// pick off last name and strip the comma
				String lastName = results[0].substring(0, results[0].length() - 1);
				String firstName = "";
				String middleInit = "";
				if (results.length > 1)
					firstName = results[1];
				// Check that First name is used name
				if (results.length > 2)
					// there is a middle initial
					middleInit = results[2];
				if (firstName.length() == 1) // this is an initial
					if (middleInit.length() > 1) // use middle name as first
					{
						firstName = middleInit;
						middleInit = "";
					}
				// assemble displayName
				if (firstName.length() > 0)
					displayThis.append(firstName);
				if (middleInit.length() > 0)
					displayThis.append(" ").append(middleInit);
				if (lastName.length() > 0)
					displayThis.append(" ").append(lastName);
			}
		}
		return displayThis.toString();
	}

	private String getUserPrefsAsXML() throws IOException {
		String pref = null;
		if (userPrefs != null) {
			StringWriter writer = new StringWriter();
			XMLOutputter outp = new XMLOutputter();
			outp.output(userPrefs, writer);
			pref = writer.toString();
		}
		return pref;
	}

	public void updateUserPrefs() throws UserPreferenceException {
		if (userPreferenceData != null && prefChanges) {
			try {
				String pref = getUserPrefsAsXML();
				if (pref != null && pref.length() > 0) {
					synchronized (userPreferenceData) {
						userPreferenceData.setXmlMetaData(pref);
                        new SessionTokenDelegate().updateUser(userPreferenceData);
					}
				}
			} catch (Exception e) {
				log.error("Failure updating user preferences to: \n" + userPreferenceData.getXmlMetaData(), e);
				throw new UserPreferenceException("Failure updating user Preferences: " + e.getMessage(), e);
			}
		}
	}

	// XML parsing methods

	/**
	 * @param xpathVal -
	 *            path to node of interest. All inclusive.
	 * @return String representing the value in the node requested.
	 * @throws UserPreferenceException -
	 *             Thrown if the xpathVal cannot be found. Just means it doesn't exist or that the user object hasn't been properly
	 *             updated.
	 * 
	 */
	public String getPreference(String xpathVal) throws UserPreferenceException {
		try {
			String result = JDomUtils.getPrefFromDoc(userPrefs, xpathVal);
			if (result == null)
				result = "";
			return result;
		} catch (JDomUtilException e) {
			throw new UserPreferenceException("Failed to get user preference value for xPath: " + xpathVal, e);
		}
	}

	public String getUserPropertiesAsXml() {
		String result = null;
		try {
			result = getUserPrefsAsXML();
		} catch (IOException io) {
			log.error("Failed to get user preferences.", io);
		}
		return result;
	}

	/**
	 * Precondition : xPathVal is compared based on PREF_Table_Properties_xxx constant where "xxx" is the the element that we are
	 * interested in Only fully qualified NotebookUser XPath should be passed as argument
	 * 
	 * @param xPathVal
	 *            full XPath to the table child name inclusive
	 * @return
	 */
	public String getTablePropertiesDescendant(String xPathVal) {
		String result = null;
		String tablePrefs = null;
		try {
			tablePrefs = getUserPrefsAsXML();
			if ((tablePrefs.indexOf("<" + xPathVal.substring(29) + ">") == -1))
				result = "";
			else
				result = getPreference(xPathVal);
		} catch (Exception e) {
			log.error("Failed to find value for xPath: " + xPathVal + " in user preferences.", e);
		}
		return result;
	}

	/**
	 * 
	 * @param xpathVal
	 * @param val
	 * @throws UserPreferenceException -
	 *             Thrown if the xpathVal cannot be found. Just means it doesn't exist or that the user object hasn't been properly
	 *             updated.
	 * 
	 */
	public void setPreference(String xpathVal, String val) throws UserPreferenceException {
		try {
			prefChanges = true;
			JDomUtils.setPrefInDoc(userPrefs, xpathVal, val);
		} catch (JDomUtilException e) {
			throw new UserPreferenceException("Failed to set preference for xPath: " + xpathVal + " to: " + val + " in user preferences.", e);
		}
	}

	public void setPreference(String xpathVal, String childElementName, ArrayList<String> al) throws UserPreferenceException {
		try {
			prefChanges = true;
			JDomUtils.setAttributeValues(userPrefs, xpathVal, childElementName, al);
		} catch (JDOMException e) {
			throw new UserPreferenceException("Failure while processing xPathVal: " + xpathVal + 
			                                  " child element name: " + childElementName + 
			                                  " while update user preferences", e);
		}
	}

	/**
	 * Will return default Compound.ISISDRAW if no preference for Compound.CHEMDRAW is indicated. If Compound.CHEMDRAW is set and
	 * not returning there is an error in set or save.
	 * 
	 * @return returns Compound.ISISDRAW - default or Compound.CHEMDRAW if set.
	 */
	public int getPreferredDrawingTool() {
		int editor = 2; // KETCHER
		String sPref = "";
		try {
			sPref = getPreference(NotebookUser.PREF_DEFAULT_EDITOR);
			if (sPref.toUpperCase().equals(NotebookUser.ISIS_DRAW))
				editor = 1; // ISIS_DRAW
		} catch (UserPreferenceException e) {
			// User has no preference set.
			// This is ok. Just return the default.
		}

		return editor;
	}

	public List<String> getPreferencesList(String xpathVal, String childElementName) throws UserPreferenceException, JDOMException {
		try {
			List<String> result = JDomUtils.getAttributeValues(userPrefs, childElementName, xpathVal);
			return result;
		} catch (Exception e) {
			throw new UserPreferenceException("Failure while lookinf for attribute values for element: " + childElementName + 
			                                  " with xPath Value: " + xpathVal, e);
		}
	}

	/**
	 * @return Returns the userSessionToken.
	 */
	public SessionToken getUserSessionToken() {
		return userSessionToken;
	}

	public void setUserSessionToken(SessionToken uSessionToken) {
		this.userSessionToken = uSessionToken;
	}

	public String getTableAsString(String xPath) throws UserPreferenceException {
		String result = null;
		try {
			result = (String) JDomUtils.getElementValue(userPrefs, xPath);
		} catch (JDOMException jdom) {
			throw new UserPreferenceException("Failed to Retrieve Element value for xPath: " + xPath);
		}

		return result;
	}

	public String getPreferredUnitAsString(UnitType type) throws UserPreferenceException {
		String result = null;
		String xPath = null;
		try {
			if (type.equals(UnitType.MASS)) {
				xPath = NotebookUser.PREF_Mass_Amount_Unit_Code;
			} else if (type.equals(UnitType.MOLAR)) {
				xPath = NotebookUser.PREF_Molar_Amount_Unit_Code;
			} else if (type.equals(UnitType.VOLUME)) {
				xPath = NotebookUser.PREF_Volume_Amount_Unit_Code;
			} else if (type.equals(UnitType.MOLES)) {
				xPath = NotebookUser.PREF_Moles_Amount_Unit_Code;
			} else {
				log.debug("Failed to match unit type to UnitType: " + type.toString());
			}
			if (xPath != null) {
				Object tmp = JDomUtils.getSingleElementAsXMLString(userPrefs, xPath);
				if(tmp == null) {
					result = null;
				} else {
					result = (String) tmp;
				}
			} else {
				log.debug("Failed to find preferred unit for UnitType: " + type.toString() + "  Returning null as preferred unit");
			}
		} catch (JDOMException jdom) {
			throw new UserPreferenceException("Failed to Retrieve single element as XML string for xPath: " + xPath);
		}

		return result;
	}

	public Element getUserPropertiesAsElement() {
		Element el = null;
		try {
			el = userPrefs.getRootElement();
		} catch (IllegalStateException isex) {
			log.error("Failed to find root element for user preferences.");
		}
		return el;
	}

	public boolean isPrefChanges() {
		return prefChanges;
	}

	public void setPrefChanges(boolean prefChanges) {
		this.prefChanges = prefChanges;
	}

	public void createAmountPreferredUnitElement(String xPath, String value) throws UserPreferenceException {
		Element elem = JDomUtils.getElementAt(userPrefs, xPath);
		if (elem != null)
			elem.setText(value);
		else
			throw new UserPreferenceException(
					"Could not create Amount Preferred Unit in NotebookUser - Method: createAmountPreferredUnit-");
	}

	public boolean isEsigEnabled() {
		boolean useSig = false;
		
		try {
			String str = getPreference(NotebookUser.PREF_ESIG_USE);
			useSig = StringUtils.equalsIgnoreCase(str, "YES");
		} catch (UserPreferenceException e) {
			log.error("Failed to retrieve property: " + NotebookUser.PREF_ESIG_USE + "\nPerhaps the propertiess are not loaded?", e);
		}
		
		return useSig;
	}
	
	public boolean isEsigLaunchUrl() {
		boolean launchUrl = false;
		
		try {
			 String str = getPreference(NotebookUser.PREF_ESIG_LAUNCH_URL);
			 launchUrl = StringUtils.equalsIgnoreCase(str, "YES");
		} catch (Exception e) {
			log.error("Failed to retrieve property: " + NotebookUser.PREF_ESIG_LAUNCH_URL + "\nPerhaps the propertiess are not loaded?", e);
		}
		
		return launchUrl;
	}
	
	public boolean isSupervisorFor(String userid) {
		boolean status = false;
		try {
			String str = getPreference(NotebookUser.PREF_SupervisorFor);
			if (str == null)
				str = "";
			str = str.toUpperCase() + ",";
			
			status = (str.indexOf(userid + ",") >= 0);
		} catch (UserPreferenceException e) {
			log.error("Failed to retrieve property: " + NotebookUser.PREF_SupervisorFor + "\nPerhaps the propertiess are not loaded?", e);
		}
		return status;
	}

	public String getCompoundManagementEmployeeId() {
		String employeeID = "";
		try {
			employeeID = getPreference(NotebookUser.PREF_EmployeeID);
		} catch (UserPreferenceException e) {
			log.error("Failed to retrieve property: " + NotebookUser.PREF_EmployeeID + "\nPerhaps the propertiess are not loaded?", e);
		}
		return employeeID;
	}

}
