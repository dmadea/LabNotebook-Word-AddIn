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
package com.chemistry.enotebook.searchquery;

import com.chemistry.enotebook.utils.CommonUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchQueryParams implements Serializable {

	private static final long serialVersionUID = -8087069515584705799L;

	private List<String> siteCodeList; // list of site codes

	private List<String> userNameList; // List of users notebooks to search for ( NTID of the users list )
	
	private String owner;	

	private boolean isSearchAllSite;

	private boolean isSearchAllChemistsOnTheSite;

	private List<String> pageStatus; // ARCHIVED,COMPLETE,OPEN

	private byte[] queryChemistry; // structure data of reaction

	private List<String> TACodeList; // TA codes to be included in search

	private List<String> projectCodeList; // Project codes to be included in search

	private String pageCreationDate;

	private String dateOperator;

	private String reactionYieldLowerValue;

	private String reactionYieldUpperValue;

	private String yieldOperator;

	private String productPurityLowerValue;

	private String productPurityUpperValue;

	private String purityOperator;

	private String subjectTitle;
	private String subjectTitleOperator;
	
	private String litReference;
	private String litReferenceOperator;	
	
	private String reactionProcedure;
	private String reactionProcedureOperator;
	
	private String compoundID;
	private String compoundIDOperator;

	private String chemicalName;
	private String chemicalNameOperator;

	private String structureSearchType; // Substructure, Similarity, Equal

	private String cssCenDbSearchType = null; // will be Reactions(Reaction Search), Products(Strucuture Search)

	private String strucSimilarity;

	private String strucSimilarityAlgorithm; // EDS, Tanimoto, Euclid

	private boolean isDoingStructSearch;

	private boolean isDoingReactionSearch;

	// SynthesisplanID of the experiment
	private String spid;
	
	private String requestId;

	// Page types to include in the search
	private List<String> pageTypes; // Singleton MedChem, Paralell MedChem, Conception Record

	public SearchQueryParams() {
		siteCodeList = new ArrayList<String>(); // list of site codes

		userNameList = new ArrayList<String>();

		isSearchAllSite = false;

		isSearchAllChemistsOnTheSite = false;

		pageStatus = new ArrayList<String>(); // ARCHIVED,COMPLETE,OPEN

		pageTypes = new ArrayList<String>();

		queryChemistry = null;// structure data of reaction

		TACodeList = new ArrayList<String>();

		projectCodeList = new ArrayList<String>();

		pageCreationDate = "";

		dateOperator = "";

		reactionYieldLowerValue = "";

		reactionYieldUpperValue = "";

		yieldOperator = "";

		productPurityLowerValue = "";

		productPurityUpperValue = "";

		purityOperator = "";

		subjectTitle = "";

		litReference = "";

		reactionProcedure = "";

		compoundID = "";
		compoundIDOperator = "";

		chemicalName = "";
		chemicalNameOperator = "";

		structureSearchType = ""; // Substructure, Similarity, Equal

		strucSimilarity = "";

		strucSimilarityAlgorithm = "";

		isDoingReactionSearch = false;

		isDoingStructSearch = false;

		spid = "";
		
		requestId = "";

	}

	/**
	 * @return Returns the chemicalName.
	 */
	public String getChemicalName() {
		return chemicalName;
	}

	/**
	 * @param chemicalName
	 *            The chemicalName to set.
	 */
	public void setChemicalName(String chemicalName) {
		this.chemicalName = chemicalName;
	}

	/**
	 * @return Returns the chemicalNameOperator.
	 */
	public String getChemicalNameOperator() {
		return chemicalNameOperator;
	}
	/**
	 * @param chemicalNameOperator The chemicalNameOperator to set.
	 */
	public void setChemicalNameOperator(String chemicalNameOperator) {
		this.chemicalNameOperator = chemicalNameOperator;
	}

	/**
	 * @return Returns the compoundID.
	 */
	public String getCompoundID() {
		return compoundID;
	}

	/**
	 * @param compoundID
	 *            The compoundID to set.
	 */
	public void setCompoundID(String compoundID) {
		this.compoundID = compoundID;
	}

	/**
	 * @return Returns the compoundIDOperator.
	 */
	public String getCompoundIDOperator() {
		return compoundIDOperator;
	}
	/**
	 * @param compoundIDOperator The compoundIDOperator to set.
	 */
	public void setCompoundIDOperator(String compoundIDOperator) {
		this.compoundIDOperator = compoundIDOperator;
	}

	/**
	 * @return Returns the isSearchAllChemistsOnTheSite.
	 */
	public boolean isSearchAllChemistsOnTheSite() {
		return isSearchAllChemistsOnTheSite;
	}

	/**
	 * @param isSearchAllChemistsOnTheSite
	 *            The isSearchAllChemistsOnTheSite to set.
	 */
	public void setSearchAllChemistsOnTheSite(boolean isSearchAllChemistsOnTheSite) {
		this.isSearchAllChemistsOnTheSite = isSearchAllChemistsOnTheSite;
	}

	/**
	 * @return Returns the isSearchAllSite.
	 */
	public boolean isSearchAllSite() {
		return isSearchAllSite;
	}

	/**
	 * @param isSearchAllSite
	 *            The isSearchAllSite to set.
	 */
	public void setSearchAllSite(boolean isSearchAllSite) {
		this.isSearchAllSite = isSearchAllSite;
	}

	/**
	 * @return Returns the litReference.
	 */
	public String getLitReference() {
		return litReference;
	}

	/**
	 * @param litReference
	 *            The litReference to set.
	 */
	public void setLitReference(String litReference) {
		this.litReference = litReference;
	}

	/**
	 * @return Returns the pageCreationDate.
	 */
	public String getPageCreationDate() {
		return pageCreationDate;
	}

	/**
	 * @param pageCreationDate
	 *            The pageCreationDate to set.
	 */
	public void setPageCreationDate(String pageCreationDate) {
		this.pageCreationDate = pageCreationDate;
	}

	/**
	 * @return Returns the pageType.
	 */
	public List<String> getPageTypes() {
		return pageTypes;
	}

	/**
	 * @param pageType
	 *            The pageType to set.
	 */
	public void setPageTypes(List<String> pageTypes) {
		this.pageTypes = pageTypes;
	}

	/**
	 * @return Returns the productPurity.
	 */
	public String getProductPurityLowerValue() {
		return productPurityLowerValue;
	}

	/**
	 * @param productPurity
	 *            The productPurity to set.
	 */
	public void setProductPurityLowerValue(String productPurity) {
		this.productPurityLowerValue = productPurity;
	}

	/**
	 * @return Returns the projectCodeList.
	 */
	public List<String> getProjectCodeList() {
		return projectCodeList;
	}

	/**
	 * @param projectCodeList
	 *            The projectCodeList to set.
	 */
	public void setProjectCodeList(List<String> projectCodeList) {
		this.projectCodeList = projectCodeList;
	}

	/**
	 * @return Returns the reactionProcedure.
	 */
	public String getReactionProcedure() {
		return reactionProcedure;
	}

	/**
	 * @param reactionProcedure
	 *            The reactionProcedure to set.
	 */
	public void setReactionProcedure(String reactionProcedure) {
		this.reactionProcedure = reactionProcedure;
	}

	/**
	 * @return Returns the reactionYield.
	 */
	public String getReactionYieldLowerValue() {
		return reactionYieldLowerValue;
	}

	/**
	 * @param reactionYield
	 *            The reactionYield to set.
	 */
	public void setReactionYieldLowerValue(String reactionYield) {
		this.reactionYieldLowerValue = reactionYield;
	}

	/**
	 * @return Returns the siteCodeList.
	 */
	public List<String> getSiteCodeList() {
		return siteCodeList;
	}

	/**
	 * @param siteCodeList
	 *            The siteCodeList to set.
	 */
	public void setSiteCodeList(List<String> siteCodeList) {
		this.siteCodeList = siteCodeList;
	}

	/**
	 * @return Returns the structureString.
	 */
	public byte[] getQueryChemistry() {
		return queryChemistry;
	}

	/**
	 * @param structureString
	 *            The structureString to set.
	 */
	public void setQueryChemistry(byte[] queryChemistry) {
		this.queryChemistry = queryChemistry;
	}

	/**
	 * @return Returns the subjectTitle.
	 */
	public String getSubjectTitle() {
		return subjectTitle;
	}

	/**
	 * @param subjectTitle
	 *            The subjectTitle to set.
	 */
	public void setSubjectTitle(String subjectTitle) {
		this.subjectTitle = subjectTitle;
	}

	public void setReactionProcedureOperator(String reactionProcedureOperator) {
		this.reactionProcedureOperator = reactionProcedureOperator;
	}

	public String getReactionProcedureOperator() {
		return reactionProcedureOperator;
	}

	public void setLitReferenceOperator(String litReferenceOperator) {
		this.litReferenceOperator = litReferenceOperator;
	}

	public String getLitReferenceOperator() {
		return litReferenceOperator;
	}

	public void setSubjectTitleOperator(String subjectTitleOperator) {
		this.subjectTitleOperator = subjectTitleOperator;
	}

	public String getSubjectTitleOperator() {
		return subjectTitleOperator;
	}	
	/**
	 * @return Returns the tACodeList.
	 */
	public List<String> getTACodeList() {
		return TACodeList;
	}

	/**
	 * @param codeList
	 *            The tACodeList to set.
	 */
	public void setTACodeList(List<String> codeList) {
		TACodeList = codeList;
	}

	/**
	 * @return Returns the userNameList.
	 */
	public List<String> getUserNameList() {
		return userNameList;
	}

	/**
	 * @param userNameList
	 *            The userNameList to set.
	 */
	public void setUserNameList(List<String> userNameList) {
		this.userNameList = userNameList;
	}

	public String getOwner() {
		return owner;
	}

	/**
	 * @param userNameList
	 *            The userNameList to set.
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return Returns the strucSimilarity.
	 */
	public String getStrucSimilarity() {
		return strucSimilarity;
	}

	/**
	 * @param strucSimilarity
	 *            The strucSimilarity to set.
	 */
	public void setStrucSimilarity(String strucSimilarity) {
		this.strucSimilarity = strucSimilarity;
	}

	/**
	 * @return Returns the strucSimilarityAlgorithm.
	 */
	public String getStrucSimilarityAlgorithm() {
		return strucSimilarityAlgorithm;
	}

	/**
	 * @param strucSimilarityAlgorithm
	 *            The strucSimilarityAlgorithm to set.
	 */
	public void setStrucSimilarityAlgorithm(String strucSimilarityAlgorithm) {
		this.strucSimilarityAlgorithm = strucSimilarityAlgorithm;
	}

	/**
	 * @return Returns the structureSearchType.
	 */
	public String getStructureSearchType() {
		return structureSearchType;
	}

	/**
	 * @param structureSearchType
	 *            The structureSearchType to set.
	 */
	public void setStructureSearchType(String structureSearchType) {
		this.structureSearchType = structureSearchType;
	}

	/**
	 * @return Returns the dateOperator.
	 */
	public String getDateOperator() {
		return dateOperator;
	}

	/**
	 * @param dateOperator
	 *            The dateOperator to set.
	 */
	public void setDateOperator(String dateOperator) {
		this.dateOperator = dateOperator;
	}

	/**
	 * @return Returns the productPurityUpperValue.
	 */
	public String getProductPurityUpperValue() {
		return productPurityUpperValue;
	}

	/**
	 * @param productPurityUpperValue
	 *            The productPurityUpperValue to set.
	 */
	public void setProductPurityUpperValue(String productPurityUpperValue) {
		this.productPurityUpperValue = productPurityUpperValue;
	}

	/**
	 * @return Returns the purityOperator.
	 */
	public String getPurityOperator() {
		return purityOperator;
	}

	/**
	 * @param purityOperator
	 *            The purityOperator to set.
	 */
	public void setPurityOperator(String purityOperator) {
		this.purityOperator = purityOperator;
	}

	/**
	 * @return Returns the reactionYieldUpperValue.
	 */
	public String getReactionYieldUpperValue() {
		return reactionYieldUpperValue;
	}

	/**
	 * @param reactionYieldUpperValue
	 *            The reactionYieldUpperValue to set.
	 */
	public void setReactionYieldUpperValue(String reactionYieldUpperValue) {
		this.reactionYieldUpperValue = reactionYieldUpperValue;
	}

	/**
	 * @return Returns the yieldOperator.
	 */
	public String getYieldOperator() {
		return yieldOperator;
	}

	/**
	 * @param yieldOperator
	 *            The yieldOperator to set.
	 */
	public void setYieldOperator(String yieldOperator) {
		this.yieldOperator = yieldOperator;
	}

	/**
	 * @return Returns the isDoingStrucSearch.
	 */
	public boolean isDoingStructSearch() {
		return isDoingStructSearch;
	}

	/**
	 * @param isDoingStrucSearch
	 *            The isDoingStrucSearch to set.
	 */
	public void setDoingStructSearch(boolean doingStructSearch) {
		this.isDoingStructSearch = doingStructSearch;
	}

	/**
	 * @param pageStatus
	 *            The pageStatus to set.
	 */
	public void setPageStatus(List<String> pageStatus) {
		this.pageStatus = pageStatus;
	}

	/**
	 * @return Returns the pageStatus.
	 */
	public List<String> getPageStatus() {
		return pageStatus;
	}

	/**
	 * @return Returns the doingReactionSearch.
	 */
	public boolean isDoingReactionSearch() {
		return isDoingReactionSearch;
	}

	/**
	 * @param doingReactionSearch
	 *            The doingReactionSearch to set.
	 */
	public void setDoingReactionSearch(boolean doingReactionSearch) {
		this.isDoingReactionSearch = doingReactionSearch;
	}

	/**
	 * @return the cssCenDbSearchType
	 */
	public String getCssCenDbSearchType() {
		return cssCenDbSearchType;
	}

	/**
	 * @param cssCenDbSearchType
	 *            the cssCenDbSearchType to set
	 */
	public void setCssCenDbSearchType(String cssCenDbSearchType) {
		this.cssCenDbSearchType = cssCenDbSearchType;
	}

	/**
	 * @return the spid
	 */
	public String getSpid() {
		return spid;
	}

	/**
	 * @param spid
	 *            the spid to set
	 */
	public void setSpid(String spid) {
		this.spid = spid;
	}

	public String getRequestId() {
		return requestId;
	}
	
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		
		buff.append("List of site codes: " + CommonUtils.getConcatenatedString(siteCodeList) + "\n");

		buff.append("List of users notebooks to search for" + CommonUtils.getConcatenatedString(userNameList) + "\n");

		buff.append("isSearchAllSite: " + isSearchAllSite + "\n");

		buff.append("isSearchAllChemistsOnTheSite: " + isSearchAllChemistsOnTheSite + "\n");

		buff.append("pageStatus: " + CommonUtils.getConcatenatedString(pageStatus) + "\n");

		buff.append("structure data of reaction: " + queryChemistry + "\n");

		buff.append("TA codes to be included in search: " + CommonUtils.getConcatenatedString(TACodeList) + "\n");

		buff.append("Project codes to be included in search: " + CommonUtils.getConcatenatedString(projectCodeList) + "\n");

		buff.append("pageCreationDate: " + pageCreationDate + "\n");

		buff.append("dateOperator: " + dateOperator + "\n");

		buff.append("reactionYieldLowerValue: " + reactionYieldLowerValue + "\n");

		buff.append("reactionYieldUpperValue: " + reactionYieldUpperValue + "\n");

		buff.append("yieldOperator: " + yieldOperator + "\n");

		buff.append("productPurityLowerValue: " + productPurityLowerValue + "\n");

		buff.append("productPurityUpperValue: " + productPurityUpperValue + "\n");

		buff.append("purityOperator: " + purityOperator + "\n");

		buff.append("subjectTitle: " + subjectTitle + "\n");

		buff.append("litReference: " + litReference + "\n");

		buff.append("reactionProcedure: " + reactionProcedure + "\n");

		buff.append("compoundID: " + compoundID + "\n");

		buff.append("chemicalName: " + chemicalName + "\n");

		buff.append("structureSearchType: " + structureSearchType + "\n");

		buff.append("cssCenDbSearchType: " + cssCenDbSearchType + "\n");

		buff.append("strucSimilarity: " + strucSimilarity + "\n");

		buff.append("strucSimilarityAlgorithm: " + strucSimilarityAlgorithm + "\n");

		buff.append("isDoingStructSearch: " + isDoingStructSearch + "\n");

		buff.append("isDoingReactionSearch: " + isDoingReactionSearch + "\n");

		buff.append("SynthesisplanID of the experiment: " + spid + "\n");
		
		buff.append("Request ID of the experiment: " + requestId + "\n");

		buff.append("Page types to include in the search: " + CommonUtils.getConcatenatedString(pageTypes) + "\n");

		return buff.toString();
	}
}