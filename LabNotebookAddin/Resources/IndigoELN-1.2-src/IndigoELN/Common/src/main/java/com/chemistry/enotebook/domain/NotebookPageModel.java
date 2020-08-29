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

import com.chemistry.enotebook.experiment.common.MolString;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchNumberException;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.utils.GUIDUtil;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.ExperimentPageUtils;
import com.chemistry.enotebook.utils.NbkBatchNumberComparator;
import com.chemistry.enotebook.utils.PlateNumberComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.*;


public class NotebookPageModel extends CeNAbstractModel {

	private static final long serialVersionUID = 6300181929167333994L;

	private static final Log log = LogFactory.getLog(NotebookPageModel.class);

	private NotebookRef nbRef = null;

	private NotebookPageHeaderModel pageHeader = null; // Page header information

	// Test Variable need be removed later
	private String threadId = "";

	// List of ReactionStepModel objects
	private List<ReactionStepModel> reactionSteps = new ArrayList<ReactionStepModel>();
	// List of RegisteredPlateModel objects
	private List<ProductPlate> registeredPlates = new ArrayList<ProductPlate>();

	private CROPageInfo croInfo;

	private boolean isEditable = true; // Flag to indicate if this notebook page is editable by user who requested it
   
	// All monommer batches across all steps.Key is batch key and Value is MonomerBatchModel
	// transient marks a member variable not to be serialized when it is persisted to streams of bytes,
	// but we need its values when registering vials in CompoundRegistration on the server-side via EJB
	private/* transient */HashMap<String, MonomerBatchModel> monomerBatchModelMap = new HashMap<String, MonomerBatchModel>();

	// All product batches across all steps.Key is batch key and Value is ProductBatchModel
	private transient HashMap<String, ProductBatchModel> productBatchModelMap = new HashMap<String, ProductBatchModel>();

	// All Batches(Monomer,Product)Model as key and List of PlateWell objects that batch is located
	private transient HashMap<BatchModel, ArrayList<PlateWell<? extends BatchModel>>> batchPlateWellsMap = new HashMap<BatchModel, ArrayList<PlateWell<? extends BatchModel>>>();

	// What is this for?
	private HashMap<ProductBatchModel, ProductPlate> batchPlateMap;


	// AnalysisModel cache for all product batches
	private AnalysisCacheModel analysisCacheModel = new AnalysisCacheModel();

	// AnalysisModel cache for all product batches
	private AttachmentCacheModel attachmentCache = new AttachmentCacheModel();

	// All structures for all monomer batches
	private transient LinkedHashMap<String, MolString> monomerBatchMolStringMap = new LinkedHashMap<String, MolString>();

	// All structures for all product batches
	private transient LinkedHashMap<String, MolString> productBatchMolStringMap = new LinkedHashMap<String, MolString>();

	private int plateLotNum = 0;
	private boolean isChanging = false;

	private PseudoProductPlate pseudoProductPlate;

	// This model is for DesktopClient UI
	private transient PseudoProductPlate guiPseudoProductPlate;

	private transient HashMap<String, String> precursorMap = new HashMap<String, String>();

	public NotebookPageModel() {
		// Create Key for this object ( eventually page_key )
		this.key = GUIDUtil.generateGUID(this);
		this.pageHeader = new NotebookPageHeaderModel(this.key);
		this.croInfo = new CROPageInfo(this.key);
	}

	public NotebookPageModel(NotebookRef ref, String key){
		this.key = key;
		this.nbRef = ref;
		this.pageHeader = new NotebookPageHeaderModel(key);
		this.croInfo = new CROPageInfo(this.key);
	}
	
	/**
	 * @return the plateLotNum
	 */
	public int getCurrentPlateLotNum() {
		return plateLotNum;
	}

	/**
	 * @param plateLotNum the plateLotNum to set
	 */
	public void setPlateLotNum(int plateLotNum) {
		this.plateLotNum = plateLotNum;
	}

	public NotebookPageModel(NotebookRef ref) {
		this();
		this.nbRef = ref;
		this.pageHeader = new NotebookPageHeaderModel(this.key);
		this.croInfo = new CROPageInfo(this.key);
	}

	public NotebookPageModel(NotebookRef ref, NotebookPageHeaderModel pageHeader) {
		this(ref);
		this.pageHeader = pageHeader;

	}
	
	public NotebookPageModel(NotebookRef ref, NotebookPageHeaderModel pageHeader,CROPageInfo croInfo) {
		this(ref);
		this.pageHeader = pageHeader;
        this.croInfo = croInfo;
	}

	/**
	 * @return the cenVersion
	 */
	public String getCenVersion() {
		return pageHeader.getCenVersion();
	}

	/**
	 * @param cenVersion
	 *            the cenVersion to set
	 */
	public void setCenVersion(String cenVersion) {
		pageHeader.setCenVersion(cenVersion);
	}

	/**
	 * @return the completionDate
	 */
	public String getCompletionDate() {
		return pageHeader.getCompletionDate();
	}

	/**
	 * @param completionDate
	 *            the completionDate to set
	 */
	public void setCompletionDateAsTimestamp(Timestamp completionDate) {
		pageHeader.setCompletionDateAsTimestamp(completionDate);
	}

	/**
	 * @return the conceptionKeyWords
	 */
	public String getConceptionKeyWords() {
		return pageHeader.getConceptionKeyWords();
	}

	/**
	 * @param conceptionKeyWords
	 *            the conceptionKeyWords to set
	 */
	public void setConceptionKeyWords(String conceptionKeyWords) {
		pageHeader.setConceptionKeyWords(conceptionKeyWords);
	}

	/**
	 * @return the conceptorNames
	 */
	public String getConceptorNames() {
		return pageHeader.getConceptorNames();
	}

	/**
	 * @param conceptorNames
	 *            the conceptorNames to set
	 */
	public void setConceptorNames(String conceptorNames) {
		pageHeader.setConceptorNames(conceptorNames);
	}

	/**
	 * @return the continuedFromRxn
	 */
	public String getContinuedFromRxn() {
		return pageHeader.getContinuedFromRxn();
	}

	/**
	 * @param continuedFromRxn
	 *            the continuedFromRxn to set
	 */
	public void setContinuedFromRxn(String continuedFromRxn) {
		pageHeader.setContinuedFromRxn(continuedFromRxn);
	}

	/**
	 * @return the continuedToRxn
	 */
	public String getContinuedToRxn() {
		return pageHeader.getContinuedToRxn();
	}

	/**
	 * @param continuedToRxn
	 *            the continuedToRxn to set
	 */
	public void setContinuedToRxn(String continuedToRxn) {
		pageHeader.setContinuedToRxn(continuedToRxn);
	}

	/**
	 * @return the creationDate
	 */
	public String getCreationDate() {
		return pageHeader.getCreationDate();
	}
	
	public Timestamp getCreationDateAsTimestamp() {
		return pageHeader.getCreationDateAsTimestamp();
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDateAsTimestamp(Timestamp creationDate) {
		pageHeader.setCreationDateAsTimestamp(creationDate);
	}

	

	/**
	 * @return the latestVersion
	 */
	public String getLatestVersion() {
		return pageHeader.getLatestVersion();
	}

	/**
	 * @param latestVersion
	 *            the latestVersion to set
	 */
	public void setLatestVersion(String latestVersion) {
		pageHeader.setLatestVersion(latestVersion);
	}

	/**
	 * @return the literatureRef
	 */
	public String getLiteratureRef() {
		return pageHeader.getLiteratureRef();
	}

	/**
	 * @param literatureRef
	 *            the literatureRef to set
	 */
	public void setLiteratureRef(String literatureRef) {
		pageHeader.setLiteratureRef(literatureRef);
	}

	/**
	 * @return the modificationDate
	 */
	public String getModificationDate() {
		return pageHeader.getModificationDate();
	}

	/**
	 * @param modificationDate
	 *            the modificationDate to set
	 */
	public void setModificationDateAsTimestamp(Timestamp modificationDate) {
		pageHeader.setModificationDateAsTimestamp(modificationDate);
	}
	
	public Timestamp getModificationDateAsTimestamp() {
		return pageHeader.getModificationDateAsTimestamp();
	}

	/**
	 * @return the nbRef
	 */
	public NotebookRef getNbRef() {
		return nbRef;
	}

	/**
	 * @param nbRef
	 *            the nbRef to set
	 */
	public void setNbRef(NotebookRef nbRef) {
		this.nbRef = nbRef;
	}

	
	/**
	 * @return the pageType
	 */
	public String getPageType() {
		return (pageHeader == null ? "" : pageHeader.getPageType());
	}

	/**
	 * @param pageType
	 *            the pageType to set
	 */
	public void setPageType(String pageType) {
		pageHeader.setPageType(pageType);
	}

	/**
	 * @return the projectAlias
	 */
	public String getProjectAlias() {
		return pageHeader.getProjectAlias();
	}

	/**
	 * @param projectAlias
	 *            the projectAlias to set
	 */
	public void setProjectAlias(String projectAlias) {
		pageHeader.setProjectAlias(projectAlias);
	}

	/**
	 * @return the projectCode
	 */
	public String getProjectCode() {
		return pageHeader.getProjectCode();
	}

	/**
	 * @param projectCode
	 *            the projectCode to set
	 */
	public void setProjectCode(String projectCode) {
		pageHeader.setProjectCode(projectCode);
		
		List<ProductPlate> productPlates = getAllProductPlates();
		for (Iterator<ProductPlate> pIt = productPlates.iterator(); pIt.hasNext(); ) {
			Object plate = pIt.next();
			if (plate instanceof ProductPlate) {
				((ProductPlate)plate).setProjectTrackingCode(projectCode);
			}
		}
	}

	/**
	 * @return the protocolID
	 */
	public String getProtocolID() {
		return pageHeader.getProtocolID();
	}

	/**
	 * @param protocolID
	 *            the protocolID to set
	 */
	public void setProtocolID(String protocolID) {
		pageHeader.setProtocolID(protocolID);
	}

	/**
	 * @return the reactionSteps
	 */
	public List<ReactionStepModel> getReactionSteps() {
		return reactionSteps;
	}

	/**
	 * @param reactionSteps
	 *            the reactionSteps to set
	 */
	public void setReactionSteps(List<ReactionStepModel> reactionSteps) {
		this.reactionSteps = reactionSteps;
	}

	/**
	 * @return the seriesID
	 */
	public String getSeriesID() {
		return pageHeader.getSeriesID();
	}

	/**
	 * @param seriesID
	 *            the seriesID to set
	 */
	public void setSeriesID(String seriesID) {
		pageHeader.setSeriesID(seriesID);
	}

	/**
	 * @return the signatureUrl
	 */
	public String getSignatureUrl() {
		return this.pageHeader.getSignatureUrl();
	}

	/**
	 * @param signatureUrl
	 *            the signatureUrl to set
	 */
	public void setSignatureUrl(String signatureUrl) {
		this.pageHeader.setSignatureUrl(signatureUrl);
	}

	/**
	 * @return the siteCode
	 */
	public String getSiteCode() {
		return pageHeader.getSiteCode();
	}

	/**
	 * @param siteCode
	 *            the siteCode to set
	 */
	public void setSiteCode(String siteCode) {
		pageHeader.setSiteCode(siteCode);
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return pageHeader.getPageStatus();
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		pageHeader.setPageStatus(status);
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return pageHeader.getSubject();
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		pageHeader.setSubject(subject);
	}

	/**
	 * @return the tableProperties
	 */
	public String getTableProperties() {
		return this.pageHeader.getTableProperties();
	}

	/**
	 * @param tableProperties
	 *            the tableProperties to set
	 */
	public void setTableProperties(String tableProperties) {
		this.pageHeader.setTableProperties(tableProperties);
	}

	/**
	 * @return the taCode
	 */
	public String getTaCode() {
		return pageHeader.getTaCode();
	}

	/**
	 * @param taCode
	 *            the taCode to set
	 */
	public void setTaCode(String taCode) {
		pageHeader.setTaCode(taCode);
	}

	/**
	 * @return the userNTID
	 */
	public String getUserName() {
		return pageHeader.getUserName();
	}

	/**
	 * @param userNTID
	 *            the userNTID to set
	 */
	public void setUserName(String userName) {
		pageHeader.setUserName(userName);
	}

	/**
	 * @return the ussiKey
	 */
	public String getUssiKey() {
		return this.pageHeader.getUssiKey();
	}

	/**
	 * @param ussiKey
	 *            the ussiKey to set
	 */
	public void setUssiKey(String ussiKey) {
		this.pageHeader.setUssiKey(ussiKey);
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return pageHeader.getVersion();
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(int version) {
		pageHeader.setVersion(version);
	}

	public String getArchiveDate() {
		return this.pageHeader.getArchiveDate();
	}

	public void setArchiveDate(String archiveDate) {
		this.pageHeader.setArchiveDate(archiveDate);
	}

	public List<ProductPlate> getRegisteredPlates() {
		return registeredPlates;
	}

	public void setRegisteredPlates(ArrayList<ProductPlate> registeredPlates) {
		this.registeredPlates = registeredPlates;
	}

	public String getBatchCreator() {
		return pageHeader.getBatchCreator();
	}

	public void setBatchCreator(String batchCreator) {
		pageHeader.setBatchCreator(batchCreator);
	}

	public String getBatchOwner() {
		return pageHeader.getBatchOwner();
	}

	public void setBatchOwner(String batchOwner) {
		pageHeader.setBatchOwner(batchOwner);
	}

	public List<ProductPlate> getAllProductPlates() {
		List<ProductPlate> productBatchPlates = new ArrayList<ProductPlate>(CeNConstants.AVERAGE_PLATE_SIZE);
		for (ReactionStepModel reactionStep : reactionSteps) {
			productBatchPlates.addAll(reactionStep.getProductPlates());
		}
		Collections.sort(productBatchPlates, new PlateNumberComparator());
		return productBatchPlates;
	}

	public List<MonomerPlate> getAllMonomerPlates() {
		List<MonomerPlate> monomerPlates = new ArrayList<MonomerPlate>(CeNConstants.AVERAGE_PLATE_SIZE);
		for (ReactionStepModel reactionStep : reactionSteps) {
			monomerPlates.addAll(reactionStep.getMonomerPlates());
		}
		return monomerPlates;
	}

	public void addReactionStep(ReactionStepModel stepModel) {
		this.reactionSteps.add(stepModel);
	}

	public void addReactionSteps(ArrayList<ReactionStepModel> stepModels) {
		this.reactionSteps.addAll(stepModels);
	}

	/**
	 * @return the pageHeader
	 */
	public NotebookPageHeaderModel getPageHeader() {
		return pageHeader;
	}

	/**
	 * @param pageHeader
	 *            the pageHeader to set
	 */
	public void setPageHeader(NotebookPageHeaderModel pageHeader) {
		this.pageHeader = pageHeader;
	}

	
	// When Loading NotebookPageModel from DB use this constructor.
	// This is the only way cen table key can be set to this object
	public NotebookPageModel(String key) {
		this.pageHeader = new NotebookPageHeaderModel(key);
		this.key = key;

	}

	public void addRegisteredPlate(ProductPlate rPlate) {
		this.registeredPlates.add(rPlate);
		this.modelChanged = true;
	}
	
	public void addRegisteredPlate(List<ProductPlate> rPlate) {
		this.registeredPlates.addAll(rPlate);
		this.modelChanged = true;
	}

	public boolean deleteRegistrationPlate(ProductPlate rPlate) {
		if (this.registeredPlates.remove(rPlate))
		{
			this.modelChanged = true;
			return true;
		}
		else
			return false;
	}
	
	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append(CeNConstants.XML_VERSION_TAG);
		xmlbuff.append("<Page_Properties>\n");
		xmlbuff.append("<Meta_Data>\n");
		xmlbuff.append(this.pageHeader.toXMLContents());
		xmlbuff.append("<Plate_Lot_Number>" + this.getCurrentPlateLotNum() + "</Plate_Lot_Number>\n");
		xmlbuff.append("</Meta_Data>\n");
		xmlbuff.append("</Page_Properties>\n");
		return xmlbuff.toString();
	}

	public ReactionStepModel getSummaryReactionStep() {
		ReactionStepModel stepModel = null;
		if (this.reactionSteps != null && this.reactionSteps.size() > 0) {
			int stepSize = this.reactionSteps.size();
			for (int i = 0; i < stepSize; i++) {
				stepModel = this.reactionSteps.get(i);
				if (stepModel.isSummaryStep())
					break;

			}
		}
		return stepModel;

	}

	public ArrayList<ReactionStepModel> getIntermediateReactionSteps() {
		ReactionStepModel stepModel = null;
		ArrayList<ReactionStepModel> intermediateSteps = new ArrayList<ReactionStepModel>();
		if (this.reactionSteps != null && !(this.reactionSteps.size() == 0 || this.reactionSteps.size() == 1)) {
			int stepSize = this.reactionSteps.size();
			for (int i = 0; i < stepSize; i++) {
				stepModel = this.reactionSteps.get(i);
				if (stepModel.isSummaryStep()) {
					continue;
				} else {
					intermediateSteps.add(stepModel);
				}

			}
		}
		return intermediateSteps;

	}

	public String getSPID() {
		if (this.isParallelExperiment() || this.isConceptionExperiment()) {
			return this.pageHeader.getSpid();
		} else {
			return "";
		}
	}
	
	
	
	public ReactionStepModel getReactionStep(int stepNo)
	{
		ReactionStepModel result = null;
		if (this.reactionSteps != null && this.reactionSteps.size() > 0 )
		{
			for(ReactionStepModel model : this.reactionSteps) {
				if(model.getStepNumber() == stepNo) 
				{
					result = model;
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * Flag currently used to indicate a page is in the process of being completed.
	 * This currently goes through a signing process that is asynchronous so the page
	 * may be in the changing state for some time based on signing progress.
	 * 
	 * This is also used to indicate that a page is to display the save background icon.
	 * 
	 * @return
	 */
	public boolean isChanging() {
		return isChanging;
	}

	/**
	 * Flag currently used to indicate a page is in the process of being completed.
	 * This currently goes through a signing process that is asynchronous so the page
	 * may be in the changing state for some time based on signing progress.
	 * 
	 * @param isChanging
	 */
	public void setChanging(boolean isChanging) {
		this.isChanging = isChanging;
	}
	
	public Object deepClone()
	{
		NotebookPageModel pageModel = new NotebookPageModel(this.getKey());
		pageModel.setLoadedFromDB(this.isLoadedFromDB());
		return pageModel;
	}
	
	public boolean isVcrRegDone() {
		return this.pageHeader.isVcrRegDone();
	}

	public void setVcrRegDone(boolean vcrRegDone) {
		this.pageHeader.setVcrRegDone(vcrRegDone);
	}

	public String getVrxnId() {
		return this.pageHeader.getVrxnId();
	}

	public void setVrxnId(String vrxnId) {
		this.pageHeader.setVrxnId(vrxnId);
	}

	
	public String getDesignSubmitter() {
		return this.pageHeader.getDesignSubmitter();
	}

	public void setDesignSubmitter(String designSubmitter) {
		this.pageHeader.setDesignSubmitter(designSubmitter);
	}
	
	public String getSummaryPlanId() {
		return this.pageHeader.getSummaryPlanId();
	}

	public void setSummaryPlanId(String summaryPlanId) {
		this.pageHeader.setSummaryPlanId(summaryPlanId);
	}
	
	public String getDSPDescription() {
		return this.pageHeader.getDescription();
	}

	public void setDSPDescription(String description) {
		this.pageHeader.setDescription(description);
	}
	public String getPageStatus() {
		return this.pageHeader.getPageStatus();
	}

	public void setPageStatus(String status) {
		this.pageHeader.setPageStatus(status);
		this.modelChanged = true;
	}
	
	/**
	 * @return the designSite
	 */
	public String getDesignSite() {
		return pageHeader.getDesignSite();
	}

	/**
	 * @param designSite
	 *            the designSite to set
	 */
	public void setDesignSite(String designSite) {
		pageHeader.setDesignSite(designSite);
		this.modelChanged = true;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public CROPageInfo getCroInfo() {
		return croInfo;
	}

	public void setCroInfo(CROPageInfo croInfo) {
		this.croInfo = croInfo;
	}

	public HashMap<String, MonomerBatchModel> getMonomerBatchModelMap() {
		return monomerBatchModelMap;
	}

	public void setMonomerBatchModelMap(HashMap<String, MonomerBatchModel> monomerBatchModelMap) {
		this.monomerBatchModelMap = monomerBatchModelMap;
	}

	public HashMap<String, ProductBatchModel> getProductBatchModelMap() {
		return productBatchModelMap;
	}

	public void setProductBatchModelMap(HashMap<String, ProductBatchModel> productBatchModelMap) {
		this.productBatchModelMap = productBatchModelMap;
	}

	public void setRegisteredPlates(List<ProductPlate> registeredPlates) {
		this.registeredPlates = registeredPlates;
	}

	public HashMap<BatchModel, ArrayList<PlateWell<? extends BatchModel>>> getBatchPlateWellsMap() {
		return batchPlateWellsMap;
	}

	public void setBatchPlateWellsMap(HashMap<BatchModel, ArrayList<PlateWell<? extends BatchModel>>> batchPlateWellsMap) {
		this.batchPlateWellsMap = batchPlateWellsMap;
	}
	
	//This method will set the modelChanged flag to false after initial load from DB.
	//Since setter methods are used to populate the data while loading from DB and these setters
	//will cause the modelChanged flag to true. Same case is true with After "Save" .
	//Once the model is successfully saved need to reset the flags as false;
	public void onLoadonAfterSaveSetModelChanged(boolean modelChangedFlag,boolean isLoadedFromDBFlag) {
		//Set PageModel editability		
		if (getLatestVersion().equals("N") || CeNConstants.PAGE_STATUS_OPEN.equals(getStatus()) == false)
		{
			setEditable(false);
		}

		try {
			// vb 5/21  This is the flag used by the refreshIcons to enable or disable the save buttons
			this.setModelChanged(modelChangedFlag);
			this.setLoadedFromDB(isLoadedFromDBFlag);
			
			//PageHeader Object
			this.getPageHeader().setModelChanged(modelChangedFlag);
			this.getPageHeader().setLoadedFromDB(isLoadedFromDBFlag);
			
			//CRO object
			this.getCroInfo().setModelChanged(modelChangedFlag);
			this.getCroInfo().setLoadedFromDB(isLoadedFromDBFlag);
			
			// Analysis models vb 12/4
			List<AnalysisModel> analysisModels = this.analysisCacheModel.getAnalyticalList();
			for (Iterator<AnalysisModel> it = analysisModels.iterator(); it.hasNext();) {
				it.next().modelChanged = false;
			}
			
			//For each step loop through
			List<ReactionStepModel> stepList = this.getReactionSteps();
			int size = stepList.size();
			for (int stepNo = 0; stepNo < size; stepNo++) {
				ReactionStepModel stepModel = this.getReactionStep(stepNo);
				stepModel.setModelChanged(modelChangedFlag);
				stepModel.setLoadedFromDB(isLoadedFromDBFlag);
	
				//ReactionScheme
				stepModel.getRxnScheme().setModelChanged(modelChangedFlag);
				stepModel.getRxnScheme().setLoadedFromDB(isLoadedFromDBFlag);
				
				//Each Monomer BatchesList
				ArrayList<BatchesList<MonomerBatchModel>> actualMonBatchesList = stepModel.getMonomers();
				int sizeMonBList = actualMonBatchesList.size();
				for(int i= 0; i < sizeMonBList ; i ++)
				{
					BatchesList<MonomerBatchModel> origList = actualMonBatchesList.get(i);
					//BatchesList
					origList.setModelChanged(modelChangedFlag);
					origList.setLoadedFromDB(isLoadedFromDBFlag);
					int sizeMons = origList.getBatchModels().size();
					for(int k = 0; k <sizeMons ; k ++)
					{
						MonomerBatchModel model = origList.getBatchModels().get(k);
						//ParentCompoundModel. This should Change all the AmountModels in it to same status.
						model.getCompound().setModelChanged(modelChangedFlag);
						model.getCompound().setLoadedFromDB(isLoadedFromDBFlag);
						//MonomerBatchModel
						model.setModelChanged(modelChangedFlag);
						model.setLoadedFromDB(isLoadedFromDBFlag);
					}
					
				}
				
				//Each Product BatchesList
				List<BatchesList<ProductBatchModel>> prodBatchesList = stepModel.getProducts();
				int sizeProdBList = prodBatchesList.size();
				for(int i= 0; i < sizeProdBList ; i ++)
				{
					BatchesList<ProductBatchModel> origList = prodBatchesList.get(i);
					//BatchesList
					origList.setModelChanged(modelChangedFlag);
					origList.setLoadedFromDB(isLoadedFromDBFlag);
					int sizeMons = origList.getBatchModels().size();
					for(int k = 0; k <sizeMons ; k ++)
					{
						ProductBatchModel model = (ProductBatchModel)origList.getBatchModels().get(k);
						
						/***************************User added batch flag update**************/
						if (origList.getPosition() != null && (origList.getPosition().equals(CeNConstants.PRODUCTS_USER_ADDED) || origList.getPosition().equals(CeNConstants.PRODUCTS_SYNC_INTENDED)))// Set User Added batch flag based on Batches List. 
							model.setUserAdded(true);
						/**********************************************************************/
						
						//ParentCompoundModel. This should Change all the AmountModels in it to same status.
						model.getCompound().setModelChanged(modelChangedFlag);
						model.getCompound().setLoadedFromDB(isLoadedFromDBFlag);
						//ProductBatchModel
						model.setModelChanged(modelChangedFlag);
						model.setLoadedFromDB(isLoadedFromDBFlag);
						
						setAllAmountModels(model, modelChangedFlag, isLoadedFromDBFlag);
					}
				}
				
				//For StoicElementBatches
				BatchesList<MonomerBatchModel> batchesList =  stepModel.getStoicBatchesList();
				if(batchesList != null){
					batchesList.setModelChanged(modelChangedFlag);
					batchesList.setLoadedFromDB(isLoadedFromDBFlag);
					for(MonomerBatchModel model : batchesList.getBatchModels()) {
						//parentCompound of the model
						model.getCompound().setModelChanged(modelChangedFlag);
						model.getCompound().setLoadedFromDB(isLoadedFromDBFlag);
						//MonomerBatchModel
						model.setModelChanged(modelChangedFlag);
						model.setLoadedFromDB(isLoadedFromDBFlag);
					}
				}
				stepModel.clearDeletedBatches();
				
				//Each Monomer Plate
				List<MonomerPlate> actualMonPlatesList = stepModel.getMonomerPlates();
				if (actualMonPlatesList != null && actualMonPlatesList.size() > 0) {
					int sizeMon = actualMonPlatesList.size();
					for (int i = 0; i < sizeMon; i++) {
						
						MonomerPlate plate = actualMonPlatesList.get(i);
						//MonomerPlate
						plate.setModelChanged(modelChangedFlag);
						plate.setLoadedFromDB(isLoadedFromDBFlag);
						PlateWell<MonomerBatchModel> wells[] = plate.getWells();
						if(wells != null && wells.length > 0)
						{
							int wellsSize = wells.length;
							for(int k=0;k < wellsSize ; k ++)
							{
								PlateWell<MonomerBatchModel> well = wells[k];
								//PlateWell
								well.setModelChanged(modelChangedFlag);
								well.setLoadedFromDB(isLoadedFromDBFlag);
								//BAtch in the well. Linking should take care of this
								if(well.getBatch() != null)
								{
								well.getBatch().setModelChanged(modelChangedFlag);
								well.getBatch().setLoadedFromDB(isLoadedFromDBFlag);
								}
							}
						}
						
					}
					
				}
				
				//Each Product Plate
				List<ProductPlate> prodPlatesList = stepModel.getProductPlates();
				if (prodPlatesList != null && prodPlatesList.size() > 0) {
					int sizeMon = prodPlatesList.size();
					for (int i = 0; i < sizeMon; i++) {
						
						ProductPlate plate = prodPlatesList.get(i);
						//MonomerPlate
						plate.setModelChanged(modelChangedFlag);
						plate.setLoadedFromDB(isLoadedFromDBFlag);
						PlateWell<ProductBatchModel> wells[] = plate.getWells();
						if(wells != null && wells.length > 0)
						{
							for(PlateWell<ProductBatchModel> well : wells) {
								//PlateWell
								well.setModelChanged(modelChangedFlag);
								well.setLoadedFromDB(isLoadedFromDBFlag);
								//BAtch in the well. Linking should take care of this
								if (well.getBatch() != null)
								{
									well.getBatch().setModelChanged(modelChangedFlag);
									well.getBatch().setLoadedFromDB(isLoadedFromDBFlag);
								}
	
								if (well.getPurificationServiceParameter() != null)
								{
									well.getPurificationServiceParameter().setModelChanged(modelChangedFlag);
									well.getPurificationServiceParameter().setLoadedFromDB(isLoadedFromDBFlag);
								}
	
							}
						}
						
					}
					
				}
				
			}//For each step
					
			//For PseudoPlate
			if(this.getPseudoProductPlate(false) != null)
			{
				PseudoProductPlate psPlate = this.getPseudoProductPlate(false);
				psPlate.setModelChanged(modelChangedFlag);
				psPlate.setLoadedFromDB(isLoadedFromDBFlag);
				PlateWell<ProductBatchModel> wells[] = psPlate.getWells();
				if(wells != null && wells.length > 0 )
				{
				int sizePSWells = wells.length;
				for(int i =0 ; i < sizePSWells ; i ++)
				{
					if(wells[i]!= null)
					{
						wells[i].setModelChanged(modelChangedFlag);
						wells[i].setLoadedFromDB(isLoadedFromDBFlag);
						if (wells[i].getPurificationServiceParameter() != null)
						{
							wells[i].getPurificationServiceParameter().setModelChanged(modelChangedFlag);
							wells[i].getPurificationServiceParameter().setLoadedFromDB(isLoadedFromDBFlag);
						}
					}
				}
				}
			}
			
			//Registered Plates
			if(this.getRegisteredPlates() != null)
			{
				ArrayList<ProductPlate> registerPlates = (ArrayList<ProductPlate>) this.getRegisteredPlates();
				for (int k = 0; k<registerPlates.size(); k++)
				{
					ProductPlate psPlate = registerPlates.get(k);
					psPlate.setModelChanged(modelChangedFlag);
					psPlate.setLoadedFromDB(isLoadedFromDBFlag);
					PlateWell<ProductBatchModel> wells[] = psPlate.getWells();
					if(wells != null && wells.length > 0 )
					{
						int sizePSWells = wells.length;
						for(int i =0 ; i < sizePSWells ; i ++)
						{
							if(wells[i]!= null)
							{
								wells[i].setModelChanged(modelChangedFlag);
								wells[i].setLoadedFromDB(isLoadedFromDBFlag);
								if (wells[i].getPurificationServiceParameter() != null)
								{
									wells[i].getPurificationServiceParameter().setModelChanged(modelChangedFlag);
									wells[i].getPurificationServiceParameter().setLoadedFromDB(isLoadedFromDBFlag);
								}
							}
						}
					}
				}
			}

			// Attachments
			if (this.getAttachmentCache() != null) {
				for (int i = 0; i < this.getAttachmentCache().getAttachmentList().size(); i++) {
					AttachmentModel model = (AttachmentModel) this.getAttachmentCache().getAttachmentList().get(i);
					if (model != null) {
						model.setModelChanged(modelChangedFlag);
						model.setLoadedFromDB(isLoadedFromDBFlag);
					}
				}
			}
		} catch (Throwable t) {
		 	log.error("Failed :", t);	
		}
	}
	private void setAllAmountModels(ProductBatchModel model, boolean modelChangedFlag,
			boolean isLoadedFromDBFlag) {
		model.getLoadingAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getLoadingAmount().setModelChanged(modelChangedFlag);
		model.getMolarAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getMolarAmount().setModelChanged(modelChangedFlag);
		model.getMoleAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getMoleAmount().setModelChanged(modelChangedFlag);
		model.getMolecularWeightAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getMolecularWeightAmount().setModelChanged(modelChangedFlag);
		model.getPreviousMolarAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getPreviousMolarAmount().setModelChanged(modelChangedFlag);
		model.getPurityAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getPurityAmount().setModelChanged(modelChangedFlag);
		model.getRxnEquivsAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getRxnEquivsAmount().setModelChanged(modelChangedFlag);
		model.getSoluteAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getSoluteAmount().setModelChanged(modelChangedFlag);
		model.getStoicDensityAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getStoicDensityAmount().setModelChanged(modelChangedFlag);
		model.getStoicLoadingAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getStoicLoadingAmount().setModelChanged(modelChangedFlag);
		model.getStoicMolarAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getStoicMolarAmount().setModelChanged(modelChangedFlag);
		model.getStoicMoleAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getStoicMoleAmount().setModelChanged(modelChangedFlag);
		model.getStoicMolecularWeightAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getStoicMolecularWeightAmount().setModelChanged(modelChangedFlag);
		model.getStoicPurityAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getStoicPurityAmount().setModelChanged(modelChangedFlag);
		model.getStoicRxnEquivsAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getStoicRxnEquivsAmount().setModelChanged(modelChangedFlag);
		model.getStoicSoluteAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getStoicSoluteAmount().setModelChanged(modelChangedFlag);
		model.getStoicVolumeAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getStoicVolumeAmount().setModelChanged(modelChangedFlag);
		model.getStoicWeightAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getStoicWeightAmount().setModelChanged(modelChangedFlag);
		model.getTheoreticalMoleAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getTheoreticalMoleAmount().setModelChanged(modelChangedFlag);
		model.getTheoreticalWeightAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getTheoreticalWeightAmount().setModelChanged(modelChangedFlag);
		model.getTheoreticalYieldPercentAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getTheoreticalYieldPercentAmount().setModelChanged(modelChangedFlag);
		model.getTotalMolarAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getTotalMolarAmount().setModelChanged(modelChangedFlag);
		model.getTotalMolarity().setLoadedFromDB(isLoadedFromDBFlag);
		model.getTotalMolarity().setModelChanged(modelChangedFlag);
		model.getTotalTubeVolumeAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getTotalTubeVolumeAmount().setModelChanged(modelChangedFlag);
		model.getTotalTubeWeightAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getTotalTubeWeightAmount().setModelChanged(modelChangedFlag);
		model.getTotalVolume().setLoadedFromDB(isLoadedFromDBFlag);
		model.getTotalVolume().setModelChanged(modelChangedFlag);
		model.getTotalWeight().setLoadedFromDB(isLoadedFromDBFlag);
		model.getTotalWeight().setModelChanged(modelChangedFlag);
		model.getTotalWellVolumeAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getTotalWellVolumeAmount().setModelChanged(modelChangedFlag);
		model.getTotalWellWeightAmount().setLoadedFromDB(isLoadedFromDBFlag);
		model.getTotalWellWeightAmount().setModelChanged(modelChangedFlag);
	}

	public String toString(){
		return this.getNbRef().getNbRef();
	}
	
	// Used by createCompound (vbtodo FIX THIS!)
	public String getNextBatchNumberForProductBatch() throws InvalidBatchNumberException
	{
		String batchNumberString = "";
		String nbf = this.getNbRef().getNotebookRef();
		int lot = getMaxLotNumberUsedInThisPage();
		lot = lot + 1; // increment by one to the max lot number
		String lotstr = "";
		if (lot < 10)
			lotstr = "00" + lot;
		else if (lot < 100)
			lotstr = "0" + lot;
		else //if (lot < 1000) //This part is commented to be in sync with current format & until we get clarity about the exact format.
			lotstr = "" + lot;
/*		else
			lotstr = "" + lot;
*/		BatchNumber batchNum = new BatchNumber();
		batchNum.setBatchNumber(nbf + "-" + lotstr + "A1");
		batchNumberString = batchNum.getBatchNumber();
		return batchNumberString;
		
	}

	// Used by createCompound (vbtodo FIX THIS!)
	// vb 6/24 change return type to a batch number instead of a string
	public BatchNumber getNextBatchNumberForSingletonProductBatch() throws InvalidBatchNumberException
	{
		String nbf = this.getNbRef().getNotebookRef();
		int lot = getMaxLotNumberUsedInThisPage();
		lot = lot + 1; // increment by one to the max lot number
		BatchNumber batchNum = new BatchNumber();
		String lotstr = "";
		if (lot < 10)
			lotstr = "00" + lot;
		else if (lot < 100)
			lotstr = "0" + lot;
		else 
			lotstr = "" + lot;
		batchNum.setBatchNumber(nbf + "-" + lotstr);
		return batchNum;
	}
	
	public List<ProductBatchModel> getAllProductBatchModelsInThisPage()
	{
		List<ReactionStepModel> stepsList = this.getReactionSteps();
		int size = stepsList.size();
		ArrayList<ProductBatchModel> pbModels = new ArrayList<ProductBatchModel>();
		Set<ProductBatchModel> hashSet = new HashSet<ProductBatchModel>();
		for(int i = 0 ; i < size ; i ++)
		{
			ReactionStepModel stepModel = stepsList.get(i);
			//Set is used to get rid of redundant PBModels between Summary and final step etc ..
			for(ProductBatchModel batch : stepModel.getAllProductBatchModelsInThisStep()) {
				if(batch != null && batch.isSetToDelete() == false) {
					hashSet.add(batch);
				}
			}
		}
		pbModels.addAll(hashSet);
		return pbModels;
	}
	
	public int getMaxLotNumberUsedInThisPage()
	{
		List<ProductBatchModel> pbList = getAllProductBatchModelsInThisPage();    
		if (pbList.size() < 1)
			return 0;
		if (pbList.size() > 1)
			Collections.sort(pbList, new NbkBatchNumberComparator<ProductBatchModel>());
		
		ProductBatchModel model = null; 
		//if (pbList.size() > 0)
		for (int i= 1; i <= pbList.size(); i++)
		{
			model = (ProductBatchModel) pbList.get(pbList.size() - i);
			if (model != null) {
				BatchNumber bnum = model.getBatchNumber();
				if(bnum != null ) {
					String lotnum = bnum.getLotNumber();
					if(lotnum != null && !lotnum.equals("")) {
						int lot = 0;;
						try {
							lot = getLotAsInt(lotnum);
							
						}
						catch(NumberFormatException e)
						{
							continue;
						}
						return lot;
					}
				}
			}
		}
		return 0;
	}
	
	
	private int getLotAsInt(String lotnum)
	{
		int lot = 0;
		String s = "";
		if (lotnum.length() > 3)
			s = lotnum.substring(0, (lotnum.length() - 2));
		else 
			s = lotnum;
		try {
			lot = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw e;
		}
		return lot;
	}

	public AnalysisCacheModel getAnalysisCache() {
		return analysisCacheModel;
	}

	public void setAnalysisCache(AnalysisCacheModel analysisCacheModel) {
		this.analysisCacheModel = analysisCacheModel;
	}
	
	public List<AnalysisModel> getAnalysisListForBatch(String batchNumber){
		return this.analysisCacheModel.getAnalysisListForBatch(batchNumber);
	}
		
	public String getNotebookRefAsString() {
        return this.nbRef.getNbRef() + "v" + getVersion();
    }

    public String getNotebookRefWithoutVersion() {
        return this.nbRef.getNbRef();
    }
    public boolean isSameNotebook(String ref, int version) {
        return (version == getVersion()) && (nbRef.getNbRef().equals(ref));
    }

    public boolean isLatestVesrion(String ref) {
        return ("Y".equals(getLatestVersion())) && (nbRef.getNbRef().equals(ref));
    }

	/**
	 * @return the attachmentCache
	 */
	public AttachmentCacheModel getAttachmentCache() {
		return attachmentCache;
	}

	/**
	 * @param attachmentCache the attachmentCache to set
	 */
	public void setAttachmentCache(AttachmentCacheModel attachmentCache) {
		this.attachmentCache = attachmentCache;
	}

	/**
	 *Test methods need be removed
	 * @return the threadId 
	 */
	public String getThreadId() {
		return threadId;
	}

	/**
	 * Test Methods need be removed.
	 * @param threadId the threadId to set
	 */
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	
	/**
	 * @return the procedure
	 */
	public String getProcedure() {
		return pageHeader.getProcedure();
	}

	/**
	 * @param procedure
	 *            the procedure to set
	 */
	public void setProcedure(String procedure) {
		pageHeader.setProcedure(procedure);
	}

	/**
	 * @return the procedureWidth
	 */
	public int getProcedureWidth() {
		return pageHeader.getProcedureWidth();
	}

	/**
	 * @param procedureWidth
	 *            the procedureWidth to set
	 */
	public void setProcedureWidth(int procedureWidth) {
		pageHeader.setProcedureWidth(procedureWidth);
	}
	
	public int getNextPlateLotNum() {
		if (plateLotNum < 0)
			plateLotNum = 0;
		plateLotNum = plateLotNum+1;
		return plateLotNum;
	}

	public PseudoProductPlate getPseudoProductPlate(boolean reLoadFlag) {
		//if (!this.getPageType().equals(CeNConstants.PAGE_TYPE_PARALLEL))
		//	return null;
		if (pseudoProductPlate == null || reLoadFlag)
		{
			ArrayList<ProductBatchModel> nonPlatedBatches = getNonPlatedBatches();
			pseudoProductPlate = new PseudoProductPlate(nonPlatedBatches);
		}
		return pseudoProductPlate;
	}	
	
	public ArrayList<ProductBatchModel> getNonPlatedBatches() {
		ArrayList<ProductBatchModel> nonPlatedBatches = new ArrayList<ProductBatchModel>();
		if(!this.getPageType().equals(CeNConstants.PAGE_TYPE_PARALLEL))
		{
			//Since there is no plating concept all ACTUAL batches are non plated
			nonPlatedBatches = getSingletonExpActualProductBatches();
		}else
		{
			List<ProductBatchModel> allProductBatches = getAllProductBatchModelsInThisPage();
			List<ProductBatchModel> platedBatches = getPlatedBatches();
			
			for (ProductBatchModel batch : allProductBatches) {
				if (! platedBatches.contains(batch) && ! nonPlatedBatches.contains(batch)) // batches may be duplicated!!! 
					nonPlatedBatches.add(batch);
			}
		}
		Collections.sort(nonPlatedBatches, new NbkBatchNumberComparator<ProductBatchModel>());
		return nonPlatedBatches;
	}
	
	public List<ProductBatchModel> getPlatedBatches() {
		Set<ProductBatchModel> tempSet = new HashSet<ProductBatchModel>();
		List<ProductBatchModel> list = new ArrayList<ProductBatchModel>();
		List<ProductPlate> plateList = this.getAllProductPlatesAndRegPlates();//This may add multiple plates for a single batch.
		for (ProductPlate plate : plateList) {
			PlateWell<ProductBatchModel>[] wells = plate.getWells();
			for (PlateWell<ProductBatchModel> well : wells) {
				ProductBatchModel batch  = well.getBatch();
				if (batch != null) {
					tempSet.add(batch);// Avoid duplicates. A batch can be in more than one plate.
				}
			}
		}
		list.addAll(tempSet);
		return list;
	}

	/** 
	 * 
	 * @return all ProductBatchModels in the summary reaction step
	 */
	public List<ProductBatchModel> getSingletonBatchs() {
		return getSummaryReactionStep().getAllProductBatchModelsInThisStep();
	}

	public LinkedHashMap<String, MolString> getMonomerBatchMolStringMap() {
		return monomerBatchMolStringMap;
	}

	public LinkedHashMap<String, MolString> getProductBatchMolStringMap() {
		return productBatchMolStringMap;
	}

	public void setMonomerBatchMolStringMap(LinkedHashMap<String, MolString> monomerBatchMolStringMap) {
		this.monomerBatchMolStringMap = monomerBatchMolStringMap;
	}

	public void setProductBatchMolStringMap(LinkedHashMap<String, MolString> productBatchMolStringMap) {
		this.productBatchMolStringMap = productBatchMolStringMap;
	}
	
	public ArrayList<ProductBatchModel> getUserAddedBatches() {
		ArrayList<ProductBatchModel> userAddedBatches = new ArrayList<ProductBatchModel>();
		for (BatchesList<ProductBatchModel> tempBatchesList : getLastStep().getProducts()) {
			if (tempBatchesList.getPosition().equals(CeNConstants.PRODUCTS_USER_ADDED))	// User Added Batches. PUA
			{
				for (ProductBatchModel batchModel : tempBatchesList.getBatchModels()) {
					if(batchModel.isUserAdded()) 
						userAddedBatches.add(batchModel);
				}
			}
		}
		return userAddedBatches;
	}
	
	public BatchesList<ProductBatchModel> getUserAddedBatchesList() {
		return getBatchesList(CeNConstants.PRODUCTS_USER_ADDED);
	}	
	
	public BatchesList<ProductBatchModel> getNewSyncIntendedBatchesList() {
		String productsSyncIntended = CeNConstants.PRODUCTS_SYNC_INTENDED;
		BatchesList<ProductBatchModel> batchesList = getExistingSyncIntendedBatches();
		if (batchesList == null)
		{
			batchesList = new BatchesList<ProductBatchModel>();
			batchesList.setPosition(productsSyncIntended);
			getLastStep().addProductBatchesList(batchesList);
		}
		return batchesList;
	}	
	
	public BatchesList<ProductBatchModel> getExistingSyncIntendedBatches() {
		return getBatchesList(CeNConstants.PRODUCTS_SYNC_INTENDED);
	}

	private BatchesList<ProductBatchModel> getBatchesList(String typeName) {
		// recoded to flow through and avoid a try/catch inside a for loop.
		BatchesList<ProductBatchModel> returnList = null;
		if (getLastStep() != null) {
			ArrayList<BatchesList<ProductBatchModel>> productBatchesList = getLastStep().getProducts();
			if(productBatchesList != null) {
				for (Iterator<BatchesList<ProductBatchModel>> batchListItr = productBatchesList.iterator(); returnList == null && batchListItr.hasNext();)
				{
					BatchesList<ProductBatchModel> batchList = batchListItr.next();
					if (StringUtils.equals(typeName, batchList.getPosition()))// User Added Batches. PUA
						returnList = batchList;
				}
				//New Batch list creation for first time (Yes but it was never added to the page model!)
				if(returnList == null) {
					// No batchesList found for position.  Need to create one.
					returnList = new BatchesList<ProductBatchModel>();
					returnList.setPosition(typeName);
					productBatchesList.add(returnList); // vb 6/12 add it to the model
				}
			}
		}
		return returnList;
    }
	
	public ReactionStepModel getLastStep()
	{
		ReactionStepModel lastStep = null;
		int noOfSteps = getReactionSteps().size();
		if (noOfSteps == 1) {
			lastStep = getReactionStep(0);
		} else {
			lastStep = getReactionStep(noOfSteps);
		}
		
		return lastStep;
	}
	
	public void dispose() {
		this.clearMap(monomerBatchModelMap);
		this.monomerBatchModelMap = null;
		this.clearMap(monomerBatchMolStringMap);
		this.monomerBatchMolStringMap = null;
		this.clearMap(productBatchModelMap);
		this.productBatchModelMap = null;
		this.clearMap(productBatchMolStringMap);
		this.productBatchMolStringMap = null;
		this.clearMap(batchPlateWellsMap);
		this.batchPlateWellsMap = null;
		
	}
	
	private void clearMap(Map<?, ?> m) {
		try {
			// This portion of the code does nothing.
//			Iterator it = m.keySet().iterator();
//			while (it.hasNext()) {
//				Object key = it.next();
//				Object val = m.get(key);
//				val = null;
//				key = null;
//			}
			m.clear();
		} catch (RuntimeException e) {
			// swallow (faster than doing null checks
		}
	}
	
	public HashMap<ProductBatchModel, ProductPlate> getAllProductBatchesAndPlatesMap(boolean reloadFlag) {
		if (!(batchPlateMap == null || batchPlateMap.size() == 0 || reloadFlag))
			return batchPlateMap;
			
		batchPlateMap = new HashMap<ProductBatchModel, ProductPlate>();
		List<ProductBatchModel> productBatcheModelsList = getAllProductBatchModelsInThisPage();
		List<ProductPlate> productPlatesList = getAllProductPlatesAndRegPlates();
		boolean isPlated = false;
		
		for (ProductBatchModel productBatchModel : productBatcheModelsList) {
			isPlated = false;
			if (productPlatesList.size() == 0)
			{
				batchPlateMap.put(productBatchModel, getGuiPseudoProductPlate());
			} else {
				outer:
				for (ProductPlate productPlate : productPlatesList) {
					ProductBatchModel[] productBatchModels = productPlate.getAllBatchesInThePlate();
					
					for (int r = 0; r <productBatchModels.length; r++)
					{
						if (productBatchModels[r] == productBatchModel)
						{
							batchPlateMap.put(productBatchModel, productPlate);
							isPlated = true;
							break outer;
						}
					}
				}
				if (!isPlated)
				{
					batchPlateMap.put(productBatchModel, getGuiPseudoProductPlate());
				}
			}
		}
		return batchPlateMap;
	}

	public void setPseudoProductPlate(PseudoProductPlate pseudoProductPlate) {
		this.pseudoProductPlate = pseudoProductPlate;
//		Set the batches from PlateWells
		if(pseudoProductPlate != null && 
		   pseudoProductPlate.getWells() != null && 
		   pseudoProductPlate.getWells().length > 0)
		{
			int size = pseudoProductPlate.getWells().length;
			PlateWell<ProductBatchModel> wells[] = pseudoProductPlate.getWells();
			ArrayList<ProductBatchModel> batchesList = new ArrayList<ProductBatchModel>(size);
			for(int i = 0; i< size && wells[i].getBatch() != null && wells[i].getBatch() instanceof ProductBatchModel; i ++)
			{
				batchesList.add(wells[i].getBatch());
			}
			this.pseudoProductPlate.setBatches(batchesList);
		}
	}

	public PseudoProductPlate getGuiPseudoProductPlate() {
		return guiPseudoProductPlate;
	}

	public void setGuiPseudoProductPlate(PseudoProductPlate guiPSPlate) {
		this.guiPseudoProductPlate = guiPSPlate;
	}

	public PlateWell<ProductBatchModel>[] getMatchingPlateWellsFromPseudoPlate(List<ProductBatchModel> batchesList) {
		
		ArrayList<PlateWell<ProductBatchModel>> wellsList = new ArrayList<PlateWell<ProductBatchModel>>(); 
		
		for (ProductBatchModel batchModel : batchesList) {
			List<PlateWell<ProductBatchModel>> tempWellsList = this.getPseudoProductPlate(false).getPlateWellsforBatch(batchModel);
			if (tempWellsList != null &&
				tempWellsList.size() > 0) 
			{
				wellsList.add(tempWellsList.get(0));
			} else {
				tempWellsList = this.getPseudoProductPlate(false).getPlateWellsforBatchByBatchNum(batchModel);
				if (tempWellsList != null &&
					tempWellsList.size() > 0)
				{
					wellsList.add(tempWellsList.get(0));
				}
			}
		}
		
		return wellsList.toArray(new PlateWell[wellsList.size()]);
	}

	public List<ProductBatchModel> getAllProductBatchModelsInThisPage(String position) {
		List<ReactionStepModel> stepsList = this.getReactionSteps();
		int size = stepsList.size();
		ArrayList<ProductBatchModel> pbModels = new ArrayList<ProductBatchModel>();
		Set<ProductBatchModel> hashSet = new HashSet<ProductBatchModel>();
		for(int i = 0 ; i < size ; i ++)
		{
			ReactionStepModel stepmodel = stepsList.get(i);
			//Set is used to get rid of redundant PBModels between Summary and final step etc ..
			hashSet.addAll(stepmodel.getAllProductBatchModelsInThisStep());
		}
		
		for(ProductBatchModel batch : hashSet) {
			//When first time exp is created 'P1' position would not have been assigned. Bug. This is a temp fix.
			if (batch.getPosition() != null && 
			   (batch.getPosition().equals(position) || 
			    StringUtils.isBlank(batch.getPosition()))) 
			{
				pbModels.add(batch);
			}
		}
		return pbModels;
	}

	public HashMap<String, String> getPrecursorMap() {
		if (precursorMap == null) {
			precursorMap = new HashMap<String, String>();
		}
		return precursorMap;
	}

	public void setPrecursorMap(HashMap<String, String> precursorMap) {
		this.precursorMap = precursorMap;
	}

	public List<ProductPlate> getAllProductPlatesAndRegPlates() {
		List<ProductPlate> list = getAllProductPlates();
		list.addAll(getRegisteredPlates());
		Collections.sort(list, new PlateNumberComparator());
		return list;
	}
	
	public ArrayList<StoicModelInterface> getLimitingReagentsInAllSteps()
	{
		ArrayList<StoicModelInterface> limitReagList = new ArrayList<StoicModelInterface>();
		if(isParallelExperiment()) {
			ArrayList<BatchesList<MonomerBatchModel>> list = getParallelExpMonomerBatchesInAllSteps();
			list = getParallelExpMonomerBatchesInAllSteps();
			if(list != null && list.size() > 0) {
				for(StoicModelInterface iface : list) {
					if(iface.isStoicLimiting()) {
						limitReagList.add(iface);	
					}
				}
			}
		} else {
			limitReagList.add(getSummaryReactionStep().getLimitingReagent());
		}
		
		return limitReagList;
	}
	
	public ArrayList<BatchesList<MonomerBatchModel>> getParallelExpMonomerBatchesInAllSteps()
	{
		List<ReactionStepModel> stepsList = this.getReactionSteps();
		int size = stepsList.size();
		int startStep = 0;
		Set<BatchesList<MonomerBatchModel>> hashSet = new HashSet<BatchesList<MonomerBatchModel>>();
		ArrayList<BatchesList<MonomerBatchModel>> monBatchesList = new ArrayList<BatchesList<MonomerBatchModel>>();
		for(int i = startStep ; i < size ; i ++)
		{
			ReactionStepModel stepmodel = stepsList.get(i);
//			Set is used to get rid of redundant BatchesList between Summary and final step etc ..
			hashSet.addAll(stepmodel.getMonomers());
		}
		for(BatchesList<MonomerBatchModel> batchList : hashSet) {
			monBatchesList.add(batchList);
		}
		return monBatchesList;
	}
	
	public int getStepNumberThisMonomerBelongsTo(BatchesList<MonomerBatchModel> blist)
	{
		List<ReactionStepModel> stepsList = this.getReactionSteps();
		int size = stepsList.size();
		int startStep = 0;
		int matchStep = 0;
		//has only summary step
		if(size == 1)
		{
			//This batch should belong to this step since there is only one step
			return matchStep;
		}
		//Has Summary step plus one step ( transition step )
		else if (size == 2 )
		{
			for(int i = startStep ; i < size ; i ++)
			{
				ReactionStepModel stepmodel = stepsList.get(i);
				for(BatchesList<MonomerBatchModel> monBList : stepmodel.getMonomers()) {
					if(monBList.getKey().equals(blist.getKey()))
					{
						//Don't quit here. See if the list is part of an intermediate step.
						matchStep = i;
					}
				}
			}
			
		}
		//Summary step + 2 or more intermediate steps
		else 
		{
			startStep = 1;
			for(int i = startStep ; i < size ; i ++)
			{
				ReactionStepModel stepmodel = stepsList.get(i);
				for(BatchesList<MonomerBatchModel> monBList : stepmodel.getMonomers()) {
					if(monBList.getKey().equals(blist.getKey()))
					{
						//Don't quit here. See if the list is part of an intermediate step.
						matchStep = i;
					}
				}
			}
		}
		
		return matchStep;
	}

	public void deepCopy(NotebookPageModel actualPageModel) {
		getNbRef().deepCopy(actualPageModel.getNbRef());
		if (!this.isChanging()) {

			this.getPageHeader().deepCopy(actualPageModel.getPageHeader());
			
			//Check for Analytical Model changes
			setAnalysisCache(actualPageModel.getAnalysisCache().deepCopy());
			
			//Check for Attachment Model changes
			setAttachmentCache(actualPageModel.getAttachmentCache().deepCopy());
			
			// check for scheme changes. ( Step need to be included by default all the time)
			List<ReactionStepModel> stepList = actualPageModel.getReactionSteps();
			int size = stepList.size();
			for (int stepNo = 0; stepNo < size; stepNo++) {
				
				ReactionStepModel actualStepModel = actualPageModel.getReactionStep(stepNo);
				ReactionStepModel copiedStepModel = new ReactionStepModel();
				copiedStepModel.deepCopy(actualStepModel);
				ReactionSchemeModel schemeModel = new ReactionSchemeModel(actualStepModel.getRxnScheme().getReactionType());
				schemeModel.deepCopy(actualStepModel.getRxnScheme());
				copiedStepModel.setRxnScheme(schemeModel);
				
				//Check for MonomerBatches List and BatchModel 
				ArrayList<BatchesList<MonomerBatchModel>> copiedMonBatchesList = new ArrayList<BatchesList<MonomerBatchModel>>();
				for(BatchesList<MonomerBatchModel> origList : actualStepModel.getMonomers()) {
					BatchesList<MonomerBatchModel> copiedList = new BatchesList<MonomerBatchModel>();
					copiedList.deepCopy(origList);
					copiedMonBatchesList.add(copiedList);
				}
				//add cloned monomers to cloned step
				copiedStepModel.setMonomers(copiedMonBatchesList);
				
				//Check for ProductBatches List and BatchModel changes
				ArrayList<BatchesList<ProductBatchModel>> copiedProdBatchesList = new ArrayList<BatchesList<ProductBatchModel>>();
				for(BatchesList<ProductBatchModel> origList : actualStepModel.getProducts()) {
					BatchesList<ProductBatchModel> copiedList = new BatchesList<ProductBatchModel>();
					copiedList.deepCopy(origList);
					copiedProdBatchesList.add(copiedList);
				}
				//add cloned products to cloned step
				copiedStepModel.setProducts(copiedProdBatchesList);
				
				//Check for StoichElement batches changes
				ArrayList<MonomerBatchModel> copiedStoichElementList = new ArrayList<MonomerBatchModel>();
				BatchesList<MonomerBatchModel> actualStoicBatchesList = actualStepModel.getStoicBatchesList();
				for(MonomerBatchModel actualModel : actualStoicBatchesList.getBatchModels()) {
					MonomerBatchModel copiedMonModel = new MonomerBatchModel();
					copiedMonModel.deepCopy(actualModel);
					copiedStoichElementList.add(copiedMonModel);
				}
				BatchesList<MonomerBatchModel> copiedStoicBatchesList = new BatchesList<MonomerBatchModel>();
				copiedStoicBatchesList.setPosition(actualStoicBatchesList.getPosition());
				copiedStoicBatchesList.addAllBatches(copiedStoichElementList);
				//add these Stoichelement to cloned step
				copiedStepModel.setStoicBatchesList(copiedStoicBatchesList);
				
				
				// Check for Monomer Plate/Plate well changes in each Step
				List<MonomerPlate> actualMonPlatesList = actualStepModel.getMonomerPlates();
				List<MonomerPlate> copiedMonPlatesList = new ArrayList<MonomerPlate>();
				if (actualMonPlatesList != null && actualMonPlatesList.size() > 0) {
					for (MonomerPlate plate : actualMonPlatesList) {
						MonomerPlate  copiedPlate = new MonomerPlate();
						copiedPlate.deepCopy(plate);
						//setPlatesIntoWells(copiedPlate);
						copiedMonPlatesList.add(copiedPlate);
					}
					copiedStepModel.addMonomerPlates(copiedMonPlatesList);
				}
				// Check for Product Plate/Plate well changes in each Step
				List<ProductPlate> actualProdPlatesList = actualStepModel.getProductPlates();
				ArrayList<ProductPlate> copiedProdPlatesList = new ArrayList<ProductPlate>();
				if (actualProdPlatesList != null && actualProdPlatesList.size() > 0) {
					for (ProductPlate plate : actualProdPlatesList) {
						ProductPlate copiedPlate = new ProductPlate();
						copiedPlate.deepCopy(plate);
						//setPlatesIntoWells(copiedPlate);
						copiedProdPlatesList.add(copiedPlate);
					}
					copiedStepModel.addProductPlates(copiedProdPlatesList);
				}
             
				addReactionStep(copiedStepModel);	
			}// for each step iterate

			// Check for Registered Plate/PlateWell
			List<ProductPlate> actualRegisterPlatesList = actualPageModel.getRegisteredPlates();
			ArrayList<ProductPlate> copiedRegisterPlatesList = new ArrayList<ProductPlate>();
			if (actualRegisterPlatesList != null && actualRegisterPlatesList.size() > 0) {
				int sizeReg = actualRegisterPlatesList.size();
				for (int i = 0; i < sizeReg; i++) {
					ProductPlate plate = actualRegisterPlatesList.get(i);
					ProductPlate copiedPlate = new ProductPlate();
					copiedPlate.deepCopy(plate);
					//setPlatesIntoWells(copiedPlate);
					copiedRegisterPlatesList.add(copiedPlate);
				}
				setRegisteredPlates(copiedRegisterPlatesList);
			}
			
			//Clone PseudoPlate
			PseudoProductPlate plate = actualPageModel.getPseudoProductPlate(false);
			if(plate != null )
			{
				PseudoProductPlate copiedPlate = new PseudoProductPlate();
				copiedPlate.deepCopy(plate);
				//setPlatesIntoWells(copiedPlate);
				setPseudoProductPlate(copiedPlate);
			}
		}
	}
/*
	private void setPlatesIntoWells(AbstractPlate copiedPlate) {
		for (PlateWell plateWell : copiedPlate.getWells())
		{
			plateWell.setPlate(copiedPlate);
		}
	}*/
	
	
	public ArrayList<ProductBatchModel> getSingletonExpActualProductBatches() {
		ArrayList<ProductBatchModel> batchList = new ArrayList<ProductBatchModel>();
		ReactionStepModel summaryStep = getSummaryReactionStep();
		if (summaryStep != null && summaryStep.getProducts() != null) {
			for (BatchesList<ProductBatchModel> batchesList : summaryStep.getProducts()) {
				if (batchesList != null && batchesList.getPosition() != null && (batchesList.getPosition().equals(CeNConstants.PRODUCTS_SYNC_INTENDED) || batchesList.getPosition().equals(CeNConstants.PRODUCTS_USER_ADDED))) {
					for (ProductBatchModel batch : batchesList.getBatchModels()) {
						if (batchList.contains(batch) == false) {
							batchList.add(batch);
						}
					}
				}
			}
		}
		return batchList;
	}
	
	public String constructCommonSingletonPrecursorsString() {
		StringBuffer buff = new StringBuffer();
		if (!this.isParallelExperiment()) {
			ReactionStepModel step = this.getReactionSteps().get(0);
			BatchesList<MonomerBatchModel> stoicBatches = step.getStoicBatchesList();
			List<MonomerBatchModel> batches = stoicBatches.getBatchModels();
			for (BatchModel batch : batches) {
				if (batch.getBatchType() != null && batch.getCompound() != null && batch.getBatchType().equals(BatchType.REACTANT)) {
					String regNumer = batch.getCompound().getRegNumber();
					if (StringUtils.isNotBlank(regNumer)) {
						buff.append("  ").append(regNumer);
					}
				}
			}
		}
		return buff.toString();
	}
	
	public String getSingletonPrecursorsString(ProductBatchModel productBatchModel) {
		if(productBatchModel != null && CommonUtils.isNull(productBatchModel.getCustomSingletonPrecursorsString())) {
			return this.constructCommonSingletonPrecursorsString();
		} else {
			return productBatchModel.getCustomSingletonPrecursorsString();
		}
	}
	
	public ArrayList<String> processSingletonPrecursorsUpdate(String precursorValues, ProductBatchModel productBatchModel) {
		String[] precursorsArray = precursorValues.trim().split("[ \t]+");
		StringBuilder newPrecursors = new StringBuilder();
		ArrayList<String> precursors = new ArrayList<String>();
		ExperimentPageUtils pageUtils = new ExperimentPageUtils();
		for (String s : precursorsArray) {
			MonomerBatchModel model = pageUtils.getMatchingStoicBatchInTheSingletonExperiment(s, this);
			String key = (model == null ? null : model.getKey());
			if (key != null && precursors.indexOf(key) == -1) {
				precursors.add(key);
				newPrecursors.append(s).append("  ");
			}
		}
		productBatchModel.setCustomSingletonPrecursorsString(newPrecursors.toString());
		this.setModelChanged(true);
		return precursors; 
	}

	public boolean isSingletonExperiment() {
		return StringUtils.equalsIgnoreCase(getPageType(), CeNConstants.PAGE_TYPE_MED_CHEM);
	}

	public boolean isParallelExperiment() {
		return StringUtils.equalsIgnoreCase(getPageType(), CeNConstants.PAGE_TYPE_PARALLEL);
	}

	public boolean isConceptionExperiment() {
		return StringUtils.equalsIgnoreCase(getPageType(), CeNConstants.PAGE_TYPE_CONCEPTION);
	}
}
