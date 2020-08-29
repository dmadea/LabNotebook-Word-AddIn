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

public class DesignSynthesisPlan extends CeNAbstractModel {

	public static final long serialVersionUID = 7526472295622776147L;

	private String spid; // synthesis plan id created in Design Service
	private String comments;
	private String description;
	private String notebookId;
	private String site;
	private String seriesId;
	private String protocolId;
	private String batchOwner;
	private boolean vcrRegDone; // flag wether VCR has been performed
	private String userName;
	private String vrxnId;
	private String[] designUsers;
	// private ReactionStepModel summaryPlan;
	private String summaryPlanId; // PID for summary reaction
	// private ReactionStepModel[] intermediatePlans;
	private String[] intermediatePlanIds; // PIDs of all the intermediate steps
	private String[] screenPanels; // array of screen panel codes
	private AmountModel scaleAmount = new AmountModel(UnitType.MOLES); //
	private String[] prototypeLeadIds; // lead id assigned to protoype compound

	private String taCode;
	private String projectCode;
	private String dateCreated;

	// Have summary + intermedate steps size.
	// Minimum/Default size is 1 i.e Step 0.
	// If value is 2. i.e we have Step 0 and Step 1
	int totalReactionSteps = 1;
	int version; // Design Service version. Diff from CeN NBPage Version
	String dateModified;
	String[] notifyList;

	public DesignSynthesisPlan() {

	}

	public DesignSynthesisPlan(String spid) {
		this.spid = spid;
	}

	/**
	 * @return Returns the batchOwner.
	 */
	public String getBatchOwner() {
		return batchOwner;
	}

	/**
	 * @param batchOwner
	 *            The batchOwner to set.
	 */
	public void setBatchOwner(String batchOwner) {
		this.batchOwner = batchOwner;
	}

	/**
	 * @return Returns the comments.
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments
	 *            The comments to set.
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the designUsers.
	 */
	public String[] getDesignUsers() {
		return designUsers;
	}

	/**
	 * @param designUsers
	 *            The designUsers to set.
	 */
	public void setDesignUsers(String[] designUsers) {
		this.designUsers = designUsers;
	}

	/**
	 * @return Returns the notebookId.
	 */
	public String getNotebookId() {
		return notebookId;
	}

	/**
	 * @param notebookId
	 *            The notebookId to set.
	 */
	public void setNotebookId(String notebookId) {
		this.notebookId = notebookId;
	}

	/**
	 * @return Returns the protocolId.
	 */
	public String getProtocolId() {
		return protocolId;
	}

	/**
	 * @param protocolId
	 *            The protocolId to set.
	 */
	public void setProtocolId(String protocolId) {
		this.protocolId = protocolId;
	}

	/**
	 * @return Returns the prototypeLeadIds.
	 */
	public String[] getPrototypeLeadIds() {
		return prototypeLeadIds;
	}

	/**
	 * @param prototypeLeadIds
	 *            The prototypeLeadIds to set.
	 */
	public void setPrototypeLeadIds(String[] prototypeLeadIds) {
		this.prototypeLeadIds = prototypeLeadIds;
	}

	/**
	 * @return Returns the scale.
	 */
	public AmountModel getScaleAmount() {
		return scaleAmount;
	}

	/**
	 * @param scale
	 *            The scale to set.
	 */
	public void setScaleAmount(AmountModel scaleAmount) {
		this.scaleAmount = scaleAmount;
	}

	/**
	 * @return Returns the screenPanels.
	 */
	public String[] getScreenPanels() {
		return screenPanels;
	}

	/**
	 * @param screenPanels
	 *            The screenPanels to set.
	 */
	public void setScreenPanels(String[] screenPanels) {
		this.screenPanels = screenPanels;
	}

	/**
	 * @return Returns the seriesId.
	 */
	public String getSeriesId() {
		return seriesId;
	}

	/**
	 * @param seriesId
	 *            The seriesId to set.
	 */
	public void setSeriesId(String seriesId) {
		this.seriesId = seriesId;
	}

	/**
	 * @return Returns the site.
	 */
	public String getSite() {
		return site;
	}

	/**
	 * @param site
	 *            The site to set.
	 */
	public void setSite(String site) {
		this.site = site;
	}

	/**
	 * @return Returns the spid.
	 */
	public String getSpid() {
		return spid;
	}

	/**
	 * @param spid
	 *            The spid to set.
	 */
	public void setSpid(String spid) {
		this.spid = spid;
	}

	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            The userName to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return Returns the vcrRegDone.
	 */
	public boolean isVcrRegDone() {
		return vcrRegDone;
	}

	/**
	 * @param vcrRegDone
	 *            The vcrRegDone to set.
	 */
	public void setVcrRegDone(boolean vcrRegDone) {
		this.vcrRegDone = vcrRegDone;
	}

	/**
	 * @return Returns the vrxnId.
	 */
	public String getVrxnId() {
		return vrxnId;
	}

	/**
	 * @param vrxnId
	 *            The vrxnId to set.
	 */
	public void setVrxnId(String vrxnId) {
		this.vrxnId = vrxnId;
	}

	public String[] getIntermediatePlanIds() {
		return intermediatePlanIds;
	}

	public void setIntermediatePlanIds(String[] intermediatePlanIds) {
		this.intermediatePlanIds = intermediatePlanIds;
	}

	public String getSummaryPlanId() {
		return summaryPlanId;
	}

	public void setSummaryPlanId(String summaryPlanId) {
		this.summaryPlanId = summaryPlanId;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getTaCode() {
		return taCode;
	}

	public void setTaCode(String taCode) {
		this.taCode = taCode;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public int getTotalReactionSteps() {
		return totalReactionSteps;
	}

	public void setTotalReactionSteps(int totalReactionSteps) {
		this.totalReactionSteps = totalReactionSteps;
	}

	public String getDateModified() {
		return dateModified;
	}

	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}

	public String[] getNotifyList() {
		return notifyList;
	}

	public void setNotifyList(String[] notifyList) {
		this.notifyList = notifyList;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
 
	//To hanlde new Design Service API changes that sends Screen panel keys which are long type
	public void setScreenPanels(long[] screenPanels) {
		if(screenPanels != null && screenPanels.length > 0)
		{
			int size = screenPanels.length;
			String[] screenPanelKeysStr = new String[size];
			for(int i = 0 ; i < size ; i ++)
			{
				screenPanelKeysStr[i] = screenPanels[i]+"";
			}
			this.screenPanels = screenPanelKeysStr;
		}
		
	}
	
	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append("<DSP>\n");
		xmlbuff.append("<NotebookId>" + this.notebookId + "</NotebookId>\n");
		xmlbuff.append("<Comments>" + this.comments + "</Comments>\n");
		xmlbuff.append("<Description>" + this.description + "</Description>\n");
		xmlbuff.append("<designUsers>" + CommonUtils.getValuesAsMultilineString(this.designUsers) + "</designUsers>\n");
		xmlbuff.append("<ScreenPanels>" + CommonUtils.getValuesAsMultilineString(this.screenPanels) + "</ScreenPanels>\n");
		xmlbuff.append("<Scale>" );
		xmlbuff.append("<Calculated>"+ this.scaleAmount.isCalculated() +"</Calculated>");
		xmlbuff.append("<Default_Value>"+this.scaleAmount.getDefaultValue()+"</Default_Value>");
		xmlbuff.append(this.scaleAmount.getUnit().toXML());
		xmlbuff.append("<Value>"+this.scaleAmount.getValue()+"</Value>");
		xmlbuff.append("</Scale>\n");
		xmlbuff.append("<PrototypeLeasdIDs>" + CommonUtils.getValuesAsMultilineString(this.prototypeLeadIds)
				+ "</PrototypeLeasdIDs>\n");
		xmlbuff.append("<DesignSite>" + this.site + "</DesignSite>\n");
		xmlbuff.append("<DesignCreationDate>" + this.dateCreated + "</DesignCreationDate>\n");
		xmlbuff.append("<SPID>" + this.spid + "</SPID>\n");
		xmlbuff.append("<PID>" + CommonUtils.getValuesAsMultilineString(this.intermediatePlanIds) + "</PID>\n");
		xmlbuff.append("<SummaryPID>" + this.summaryPlanId + "</SummaryPID>\n");
		xmlbuff.append("<DesignSubmitter>" + this.userName + "</DesignSubmitter>\n");
		xmlbuff.append("<VrxnID>" + this.vrxnId + "</VrxnID>\n");
		xmlbuff.append("</DSP>");
		return xmlbuff.toString();
	}

}
