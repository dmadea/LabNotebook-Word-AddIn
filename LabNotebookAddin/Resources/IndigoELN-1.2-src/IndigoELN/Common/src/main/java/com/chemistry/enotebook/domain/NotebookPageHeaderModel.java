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

import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.utils.CommonUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class NotebookPageHeaderModel extends CeNAbstractModel {

	private static final long serialVersionUID = 7059070862057732242L;
	
	private int version = 1;
	private String latestVersion = "Y";
	private String userName = ""; //NTID of person repsponsible for the actual creation of page and has edit privs.This canot be changed
	private String siteCode = "";
	private String batchOwner = ""; //NTID of the designated OWNER to this Exp
	private String pageStatus = "";
	private String pageType = ""; // Defines if it is MED-CHEM,PARALLEL,CONCEPTION
	private String cenVersion = "";
	private String subject = "";
	private String literatureRef = "";
	private String taCode = "";
	private String projectCode = "";
	private Timestamp creationDate = null;
	private Timestamp completionDate = null;
	private Timestamp modificationDate = null;
	private String continuedFromRxn = "";
	private String continuedToRxn = "";
	private String projectAlias = "";
	private String protocolID = "";
	private String seriesID = "";
	
	private String batchCreator; //NTID of the designated Creator.

	// Conception exp specific data
	private String conceptorNames = "";
	private String conceptionKeyWords = "";

	private boolean autoCalcOn = true;

	private String tableProperties = ""; // Stoich table preferences
	private String ussiKey = "0";
	private String archiveDate = "";
	private String signatureUrl = "";
	
	private String spid; // synthesis plan id created in Design Service
	private String comments;
	private String description;
	private String designSite;
	private boolean vcrRegDone; // flag weather VCR has been performed
	private String vrxnId;
	private String[] designUsers;
	private String summaryPlanId; // PID for summary reaction
	private String[] intermediatePlanIds; // PIDs of all the intermediate steps
	private String[] screenPanels; // array of screen panel codes
	private AmountModel scale = new AmountModel(UnitType.MOLES); //
	private String[] prototypeLeadIds; // lead id assigned to prototype compound
	private String designSubmitter = "";
	private String procedure;
	private int procedureWidth;
	private List<ProcedureImage> procedureImages;
	
	// Have summary + intermediate steps size.
	// Minimum/Default size is 1 i.e Step 0.
	// If value is 2. i.e we have Step 0 and Step 1
	int totalReactionSteps = 1;
	String[] notifyList;
	
	public NotebookPageHeaderModel(String key) {
		this.key = key;
	}

	public NotebookPageHeaderModel(DesignSynthesisPlan dsp,String key) {
		this(dsp);
		this.key = key;
	}
	
	public NotebookPageHeaderModel(DesignSynthesisPlan dsp) {
		setDSPData(dsp);
		
	}
	
	public void setDSPData(DesignSynthesisPlan dsp) {
		this.spid = dsp.getSpid();
		this.comments = dsp.getComments();
		this.description = dsp.getDescription();
		this.designSite = dsp.getSite();
		this.vcrRegDone = dsp.isVcrRegDone(); 
		this.vrxnId = dsp.getVrxnId();
		this.designUsers = dsp.getDesignUsers();
		this.summaryPlanId = dsp.getSummaryPlanId();
		this.intermediatePlanIds= dsp.getIntermediatePlanIds(); 
		this.screenPanels = dsp.getScreenPanels(); 
		this.scale = dsp.getScaleAmount();
		this.prototypeLeadIds = dsp.getPrototypeLeadIds();
		this.taCode = dsp.getTaCode();
		this.projectCode = dsp.getProjectCode();
		this.designSubmitter = dsp.getUserName();
		this.totalReactionSteps = dsp.getTotalReactionSteps();
			
	}

	public String getBatchCreator() {
		return batchCreator == null ? "" : batchCreator;
	}

	public void setBatchCreator(String batchCreator) {
		this.batchCreator = batchCreator;
		this.modelChanged = true;
	}

	public String getBatchOwner() {
		return batchOwner == null ? "" : batchOwner;
	}

	public void setBatchOwner(String batchOwner) {
		this.batchOwner = batchOwner;
		this.modelChanged = true;
	}

	public String getCenVersion() {
		return cenVersion;
	}

	public void setCenVersion(String cenVersion) {
		this.cenVersion = cenVersion;
	}

	public String getCompletionDate() {
		return CommonUtils.toTimestampString(completionDate);
	}

		
	public Timestamp getCompletionDateAsTimeStamp() {
		return completionDate;
	}

	public void setCompletionDateAsTimestamp(Timestamp completionDate) {
		this.completionDate = completionDate;
		this.modelChanged = true;
	}

	public String getConceptionKeyWords() {
		return conceptionKeyWords;
	}

	public void setConceptionKeyWords(String conceptionKeyWords) {
		this.conceptionKeyWords = conceptionKeyWords;
		this.modelChanged = true;
	}

	public String getConceptorNames() {
		return conceptorNames;
	}

	public void setConceptorNames(String conceptorNames) {
		this.conceptorNames = conceptorNames;
		this.modelChanged = true;
	}

	public String getContinuedFromRxn() {
		return continuedFromRxn;
	}

	public void setContinuedFromRxn(String continuedFromRxn) {
		this.continuedFromRxn = continuedFromRxn;
		this.modelChanged = true;
	}

	public String getContinuedToRxn() {
		return continuedToRxn;
	}

	public void setContinuedToRxn(String continuedToRxn) {
		this.continuedToRxn = continuedToRxn;
		this.modelChanged = true;
	}

	public String getCreationDate() {
		return CommonUtils.toTimestampString(creationDate);
	}
   
	public Timestamp getCreationDateAsTimestamp() {
		return creationDate;
	}
	
	
	public void setCreationDateAsTimestamp(Timestamp creationDate) {
		this.creationDate = creationDate;
		this.modelChanged = true;
	}
	
	public String getLatestVersion() {
		return latestVersion;
	}

	public void setLatestVersion(String latestVersion) {
		this.latestVersion = latestVersion;
		this.modelChanged = true;
	}

	public String getLiteratureRef() {
		return literatureRef;
	}

	public void setLiteratureRef(String literatureRef) {
		this.literatureRef = literatureRef;
		this.modelChanged = true;
	}

	public String getModificationDate() {
		return CommonUtils.toTimestampString(modificationDate);
	}

	
	public Timestamp getModificationDateAsTimestamp() {
		return modificationDate;
	}

	public void setModificationDateAsTimestamp(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		this.modelChanged = true;
	}
	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public String getProjectAlias() {
		return projectAlias;
	}

	public void setProjectAlias(String projectAlias) {
		this.projectAlias = projectAlias;
		this.modelChanged = true;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
		this.modelChanged = true;
	}

	public String getProtocolID() {
		return protocolID;
		
	}

	public void setProtocolID(String protocolID) {
		this.protocolID = protocolID;
		this.modelChanged = true;
	}

	public String getSeriesID() {
		return seriesID;
	}

	public void setSeriesID(String seriesID) {
		this.seriesID = seriesID;
		this.modelChanged = true;
	}

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
		this.modelChanged = true;
	}

	public String getPageStatus() {
		return pageStatus;
	}

	public void setPageStatus(String status) {
		this.pageStatus = status;
		this.modelChanged = true;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
		this.modelChanged = true;
	}

	public String getTaCode() {
		return taCode;
	}

	public void setTaCode(String taCode) {
		this.taCode = taCode;
		this.modelChanged = true;
	}

	/**
	 * Equivalent to User's NT Id.
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userNTID) {
		this.userName = userNTID;
		this.modelChanged = true;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
		this.modelChanged = true;
	}

	public boolean isAutoCalcOn() {
		return autoCalcOn;
	}

	public void setAutoCalcOn(boolean autoCalcOn) {
		this.autoCalcOn = autoCalcOn;
	}

	public String getArchiveDate() {
		return archiveDate;
	}

	public void setArchiveDate(String archiveDate) {
		this.archiveDate = archiveDate;
	}

	public String getSignatureUrl() {
		return signatureUrl;
	}

	public void setSignatureUrl(String signatureUrl) {
		this.signatureUrl = signatureUrl;
	}

	public String getTableProperties() {
		return tableProperties;
	}

	public void setTableProperties(String tableProperties) {
		this.tableProperties = tableProperties;
	}

	public String getUssiKey() {
		return ussiKey;
	}

	public void setUssiKey(String ussiKey) {
		this.ussiKey = ussiKey;
	}

	
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String[] getDesignUsers() {
		return designUsers;
	}

	public void setDesignUsers(String[] designUsers) {
		this.designUsers = designUsers;
	}

	public String[] getIntermediatePlanIds() {
		return intermediatePlanIds;
	}

	public void setIntermediatePlanIds(String[] intermediatePlanIds) {
		this.intermediatePlanIds = intermediatePlanIds;
	}

	public String[] getNotifyList() {
		return notifyList;
	}

	public void setNotifyList(String[] notifyList) {
		this.notifyList = notifyList;
	}

	public String[] getPrototypeLeadIds() {
		return prototypeLeadIds;
	}

	public void setPrototypeLeadIds(String[] prototypeLeadIds) {
		this.prototypeLeadIds = prototypeLeadIds;
	}

	public AmountModel getScale() {
		return scale;
	}

	public void setScale(AmountModel scaleAmnt) {
		if (scaleAmnt != null) {
			if (scaleAmnt.getUnitType().getOrdinal() == UnitType.MOLES.getOrdinal()) {
				// Check to see if it is a unit change
				if (!scale.equals(scaleAmnt)) {
					//boolean unitChange = BatchUtils.isUnitOnlyChanged(scale, scaleAmnt);
					scale.deepCopy(scaleAmnt);
					setModified(true);
					
				}
			}
		} else {
			scale.setValue("0");
		}
	}

	public String[] getScreenPanels() {
		return screenPanels;
	}

	public void setScreenPanels(String[] screenPanels) {
		this.screenPanels = screenPanels;
	}

	public String getDesignSite() {
		return designSite;
	}

	public void setDesignSite(String site) {
		this.designSite = site;
	}

	public String getSpid() {
		return spid;
	}

	public void setSpid(String spid) {
		this.spid = spid;
	}

	public String getSummaryPlanId() {
		return summaryPlanId;
	}

	public void setSummaryPlanId(String summaryPlanId) {
		this.summaryPlanId = summaryPlanId;
	}

	public int getTotalReactionSteps() {
		return totalReactionSteps;
	}

	public void setTotalReactionSteps(int totalReactionSteps) {
		this.totalReactionSteps = totalReactionSteps;
	}

	public boolean isVcrRegDone() {
		return vcrRegDone;
	}

	public void setVcrRegDone(boolean vcrRegDone) {
		this.vcrRegDone = vcrRegDone;
	}

	public String getVrxnId() {
		if(this.vrxnId != null && !this.vrxnId.toUpperCase().startsWith("VRXN"))
		{
			return "User Drawn";
		}
		return vrxnId;
	}

	public void setVrxnId(String vrxnId) {
		this.vrxnId = vrxnId;
	}

	public List<ProcedureImage> getProcedureImages() {
		return procedureImages;
	}

	public void setProcedureImages(List<ProcedureImage> procedureImages) {
		this.procedureImages = procedureImages;
	}

	public String getDesignSubmitter() {
		return designSubmitter;
	}

	public void setDesignSubmitter(String designSubmitter) {
		this.designSubmitter = designSubmitter;
	}
	
	public String toXML(){
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append(CeNConstants.XML_VERSION_TAG);
		xmlbuff.append("<Page_Properties>\n");
		xmlbuff.append("<Meta_Data>\n");
		xmlbuff.append(toXMLContents());
		xmlbuff.append("</Meta_Data>\n");
		xmlbuff.append("</Page_Properties>\n");

		//For test
		//System.out.println(new XMLOutputter().outputString(this.createDocument()));
		
		return xmlbuff.toString();
//		return new XMLOutputter().outputString(this.createDocument());
	}
	
//	private Document createDocument() {
//		Element pageProperties = new Element(XmlConstants.PAGE_PROPERTIES);
//		Document pageDocument = new Document(pageProperties);
//		Element metaData = pageProperties.addContent(new Element(XmlConstants.META_DATA));
//		metaData.addContent(new Element(XmlConstants.ARCHIVE_DATE).setText(this.archiveDate));
//		metaData.addContent(new Element(XmlConstants.SIGNATURE_URL).setText(this.signatureUrl));
//		metaData.addContent(new Element(XmlConstants.TABLE_PROPERTIES).setText(this.tableProperties));
//		metaData.addContent(new Element(XmlConstants.USSI_KEY).setText(this.ussiKey));
//		metaData.addContent(new Element(XmlConstants.AUTO_CALC_ON).setText(String.valueOf(this.autoCalcOn)));
//		metaData.addContent(new Element(XmlConstants.CEN_VERSION).setText(this.cenVersion));
//		metaData.addContent(new Element(XmlConstants.COMPLETION_DATE).setText(this.getCompletionDate()));
//		metaData.addContent(new Element(XmlConstants.CONTINUED_FROM_RXN).setText(this.getContinuedFromRxn()));
//		metaData.addContent(new Element(XmlConstants.CONTINUED_TO_RXN).setText(this.getContinuedToRxn()));
//		metaData.addContent(new Element(XmlConstants.PROJECT_ALIAS).setText(this.projectAlias));
//		Element dsp = metaData.addContent(new Element(XmlConstants.DSP));
//		dsp.addContent(new Element(XmlConstants.DESCRIPTION).setText(this.description));
//		dsp.addContent(new Element(XmlConstants.DESIGN_USERS).setText(CommonUtils.getValuesAsMultilineString(this.designUsers)));
//		dsp.addContent(new Element(XmlConstants.SCREEN_PANELS).setText(CommonUtils.getValuesAsMultilineString(this.screenPanels)));
//		Element scale = dsp.addContent(new Element(XmlConstants.SCALE));
//		scale.addContent(new Element(XmlConstants.CALCULATED).setText(String.valueOf(this.scale.isCalculated())));
//		scale.addContent(new Element(XmlConstants.DEFAULT_VALUE).setText(String.valueOf(this.scale.getDefaultValue())));
//		scale.addContent(this.scale.getUnit().toElement());
//		scale.addContent(new Element(XmlConstants.VALUE).setText(this.scale.getValue()));
//		dsp.addContent(new Element(XmlConstants.PROTOTYPE_LEAD_IDS).setText(CommonUtils.getValuesAsMultilineString(this.prototypeLeadIds)));
//		dsp.addContent(new Element(XmlConstants.DESIGN_SITE).setText(this.designSite));
//		dsp.addContent(new Element(XmlConstants.DESIGN_CREATION_DATE).setText(this.creationDate.toString()));
//		dsp.addContent(new Element(XmlConstants.PID).setText(CommonUtils.getValuesAsMultilineString(this.intermediatePlanIds)));
//		dsp.addContent(new Element(XmlConstants.SUMMARY_PID).setText(this.summaryPlanId));
//		dsp.addContent(new Element(XmlConstants.VRXN_ID).setText(this.vrxnId));
//		metaData.addContent(new Element(XmlConstants.CONCEPTION_KEY_WORDS).setText(this.conceptionKeyWords));
//		metaData.addContent(new Element(XmlConstants.CONCEPTOR_NAMES).setText(this.conceptorNames));
//		return pageDocument;
//	}
	
	protected String toXMLContents() {
		SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy HH:mm:ss z");
		StringBuffer xmlbuff = new StringBuffer();
		if(CommonUtils.isNotNull(this.archiveDate))
			xmlbuff.append("<Archive_Date>" + this.archiveDate + "</Archive_Date> \n");
		else xmlbuff.append("<Archive_Date/> \n");
		if(CommonUtils.isNotNull(this.signatureUrl))
			xmlbuff.append("<Signature_Url>" + CommonUtils.replaceAmpersandSymbol(this.signatureUrl) + "</Signature_Url>\n");
		else xmlbuff.append("<Signature_Url/> \n");
		if(CommonUtils.isNotNull(this.tableProperties))
			xmlbuff.append("<Table_Properties>" + this.tableProperties + "</Table_Properties>\n");
		else xmlbuff.append("<Table_Properties/> \n");
		if(CommonUtils.isNotNull(this.ussiKey))
			xmlbuff.append("<Ussi_Key>" + this.ussiKey + "</Ussi_Key>\n");
		else xmlbuff.append("<Ussi_Key/> \n");
		xmlbuff.append("<Auto_Calc_On>" + this.autoCalcOn + "</Auto_Calc_On>\n");
		if(CommonUtils.isNotNull(this.cenVersion))
			xmlbuff.append("<Cen_Version>" + this.cenVersion + "</Cen_Version>\n");
		else xmlbuff.append("<Cen_Version/> \n");
		if(CommonUtils.isNotNull(this.completionDate))
			xmlbuff.append("<Completion_Date>" + ((completionDate != null) ? df.format(completionDate) : "") + "</Completion_Date>\n");
		else xmlbuff.append("<Completion_Date/> \n");
		if(CommonUtils.isNotNull(this.continuedFromRxn))
			xmlbuff.append("<Continued_From_Rxn>" + this.continuedFromRxn + "</Continued_From_Rxn>\n");
		else xmlbuff.append("<Continued_From_Rxn/> \n");
		if(CommonUtils.isNotNull(this.continuedToRxn))
			xmlbuff.append("<Continued_To_Rxn>" + this.continuedToRxn + "</Continued_To_Rxn>\n");
		else xmlbuff.append("<Continued_To_Rxn/> \n");
		if(CommonUtils.isNotNull(this.projectAlias))
			xmlbuff.append("<Project_Alias>" + this.projectAlias + "</Project_Alias>\n");
		else xmlbuff.append("<Project_Alias/> \n");
		xmlbuff.append("<DSP>\n");
		if(CommonUtils.isNotNull(this.comments))
			xmlbuff.append("<Comments>" + this.comments + "</Comments>\n");
		else xmlbuff.append("<Comments/> \n");
		if(CommonUtils.isNotNull(this.description))
			xmlbuff.append("<Description>" + this.description + "</Description>\n");
		else xmlbuff.append("<Description/> \n");
		xmlbuff.append("<Procedure_Width>" + this.procedureWidth + "</Procedure_Width>\n");
		xmlbuff.append("<designUsers>" + CommonUtils.getValuesAsMultilineString(this.designUsers) + "</designUsers>\n");
		xmlbuff.append("<ScreenPanels>" + CommonUtils.getValuesAsMultilineString(this.screenPanels) + "</ScreenPanels>\n");
		xmlbuff.append("<Scale>");
		xmlbuff.append("<Calculated>"+this.scale.isCalculated()+"</Calculated>");
		xmlbuff.append("<Default_Value>"+this.scale.getDefaultValue()+"</Default_Value>");
		xmlbuff.append(this.scale.getUnit().toXML());
		xmlbuff.append("<Value>"+this.scale.getValue()+"</Value>");
		xmlbuff.append("</Scale>\n");
		xmlbuff.append("<PrototypeLeasdIDs>" + CommonUtils.getValuesAsMultilineString(this.prototypeLeadIds)
				+ "</PrototypeLeasdIDs>\n");
		if(CommonUtils.isNotNull(this.designSite))
			xmlbuff.append("<DesignSite>" + this.designSite + "</DesignSite>\n");
		else xmlbuff.append("<DesignSite/> \n");
		if(CommonUtils.isNotNull(this.creationDate))
			xmlbuff.append("<DesignCreationDate>" + df.format(this.creationDate) + "</DesignCreationDate>\n");
		else xmlbuff.append("<DesignCreationDate/> \n");
		xmlbuff.append("<PID>" + CommonUtils.getValuesAsMultilineString(this.intermediatePlanIds) + "</PID>\n");
		xmlbuff.append("<SummaryPID>" + this.summaryPlanId + "</SummaryPID>\n");
		if(CommonUtils.isNotNull(this.vrxnId))
			xmlbuff.append("<VrxnID>" + this.vrxnId + "</VrxnID>\n");
		else xmlbuff.append("<VrxnID/> \n");
		xmlbuff.append("</DSP> \n");
		// Add conception record fields
		if (CommonUtils.isNotNull(this.conceptionKeyWords))
			xmlbuff.append("<ConceptionKeyWords>").append(this.conceptionKeyWords).append("</ConceptionKeyWords>");
		else xmlbuff.append("<ConceptionKeyWords/> \n");
		if (CommonUtils.isNotNull(this.conceptorNames))
			xmlbuff.append("<ConceptorNames>").append(this.conceptorNames).append("</ConceptorNames>");
		else xmlbuff.append("<ConceptorNames/> \n");		
		return xmlbuff.toString();
	}
	
	public Object deepClone()
	{
		//Page key should be the same
		NotebookPageHeaderModel pageHeader = new NotebookPageHeaderModel(this.getKey());
		pageHeader.deepCopy(this);
		return pageHeader;
	}


	/**
	 * @return the procedure
	 */
	public String getProcedure() {
		return procedure == null ? "" : procedure;
	}

	/**
	 * @param procedure
	 *            the procedure to set
	 */
	public void setProcedure(String procedure) {
		this.procedure = procedure;
		this.modelChanged = true;
	}

	/**
	 * @return the procedureWidth
	 */
	public int getProcedureWidth() {
		return procedureWidth;
	}

	/**
	 * @param procedureWidth
	 *            the procedureWidth to set
	 */
	public void setProcedureWidth(int procedureWidth) {
		this.procedureWidth = procedureWidth;
		this.modelChanged = true;
	}
	
	public void deepCopy(NotebookPageHeaderModel src) {
		setVersion(src.version) ;
		setLatestVersion(src.latestVersion);
		setUserName(src.userName) ; 
		setDesignSite(src.siteCode) ;
		setSiteCode(src.siteCode);
		setTotalReactionSteps(src.totalReactionSteps);
		setPageStatus(src.pageStatus) ;
		setPageType(src.pageType) ; 
		setCenVersion(src.cenVersion) ;
		setSubject(src.subject);
		setLiteratureRef(src.literatureRef) ;
		setTaCode(src.taCode) ;
		setProjectCode(src.projectCode) ;
		setCreationDateAsTimestamp(src.creationDate) ;
		setCompletionDateAsTimestamp(src.completionDate) ;
		setModificationDateAsTimestamp(src.modificationDate) ;
		setContinuedFromRxn(src.continuedFromRxn) ;
		setContinuedToRxn(src.continuedToRxn) ;
		setProjectAlias(src.projectAlias) ;
		setProtocolID(src.protocolID) ;
		setSeriesID(src.seriesID) ;
		setBatchOwner(src.batchOwner);
		setBatchCreator(src.batchCreator);
		setConceptorNames(src.conceptorNames) ;
		setConceptionKeyWords(src.conceptionKeyWords) ;
		setAutoCalcOn(src.autoCalcOn) ;
		setTableProperties(src.tableProperties) ; 
		setUssiKey(src.ussiKey) ;
		setArchiveDate(src.archiveDate);
		setSignatureUrl(src.signatureUrl) ;
		setSpid(src.spid); 
		setComments(src.comments);
		setDescription(src.description);
		setDesignSite(src.designSite);
		setVcrRegDone(src.vcrRegDone); 
		setVrxnId(src.vrxnId);
		setDesignUsers(src.designUsers);
		setSummaryPlanId(summaryPlanId); 
		setIntermediatePlanIds(intermediatePlanIds); 
		setScreenPanels(src.screenPanels); 
		setScale(src.scale); 
		setPrototypeLeadIds(src.prototypeLeadIds); 
		setDesignSubmitter(src.designSubmitter);
		setNotifyList(src.notifyList);
		setProcedure(src.procedure);
		setProcedureWidth(src.procedureWidth);
		setLoadedFromDB(src.isLoadedFromDB());
		setProcedureImages(src.getProcedureImages());
	}
}
