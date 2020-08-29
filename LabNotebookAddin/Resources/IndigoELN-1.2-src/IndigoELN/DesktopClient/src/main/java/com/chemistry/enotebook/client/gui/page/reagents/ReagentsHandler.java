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
 * Created on Sep 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.reagents;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.stoichiometry.search.CompoundAndBatchInfoUpdater;
import com.chemistry.enotebook.compoundmgmtservice.exception.CompoundNotFoundException;
import com.chemistry.enotebook.domain.AmountModel;
import com.chemistry.enotebook.domain.MonomerBatchModel;
import com.chemistry.enotebook.domain.ParentCompoundModel;
import com.chemistry.enotebook.domain.SaltFormModel;
import com.chemistry.enotebook.experiment.common.SaltForm;
import com.chemistry.enotebook.experiment.common.codetables.SaltCodeCache;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchNumberException;
import com.chemistry.enotebook.experiment.datamodel.common.SignificantFigures;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;
import com.chemistry.enotebook.reagent.delegate.ReagentCallBackObject;
import com.chemistry.enotebook.reagent.delegate.ReagentMgmtServiceDelegate;
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ChemUtilInitException;
import com.chemistry.enotebook.util.ExceptionUtils;
import com.chemistry.enotebook.utils.StructureLoadAndConversionUtil;
import com.common.chemistry.codetable.CodeTableCacheException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import javax.swing.*;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ReagentsHandler {

    private static final Log log = LogFactory.getLog(ReagentsHandler.class);

	public static final int ISISDRAW = 1;
	public static final int CHEMDRAW = 2;

	private String myReagents = null;
	private static String reagentConfigXML = null;
	private String userName = null;
	private String reagentsList = null;
	private HashMap<String, String> compundNumberMap = new HashMap<String, String>();
	private HashMap<String, String> totalDBMap = new HashMap<String, String>();
	private ArrayList<String> mandatoryFields = new ArrayList<String>();
	private ArrayList<String> allFieldsList = new ArrayList<String>();
	private ReagentCallBackObject callBackObj = null;
	private int chunkSize = 50;
	private int lastPosition = -1;
	private boolean hasMore = false;
	private int total = 0;
	private Element configXMLRoot = null;
	private HashMap<String, String> sdfMap = new HashMap<String, String>();
	// private static final String NUMBER_FORMAT_ERROR_MESSAGE = "A field
	// was incorrectly entered as text when expecting a number. \nIt was
	// ignored. Please check your values and try again \nif ignoring the
	// value adversely affects the entry.";
	private static final String NUMBER_FORMAT_ERROR_MESSAGE = " field has invalid number format. \n Please check the value and try again ";
	boolean isTimedOutExceptionOccured = false; // This flag is used to stop
	private CompoundAndBatchInfoUpdater batchInfoUpdater = null;
	private ReagentMgmtServiceDelegate reagentMgmtServiceDelegate = null;

	// further errors, When there is
	// a timedoutException.
	// any subsequent EJB call is giving exceptions and making the Reagents
	// frame freeze
	public ReagentsHandler() {
		userName = MasterController.getUser().getNTUserID();
		batchInfoUpdater = new CompoundAndBatchInfoUpdater();
		try {
			reagentMgmtServiceDelegate = new ReagentMgmtServiceDelegate();
			callBackObj = new ReagentCallBackObject();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * get Reagent DB configuration XML.
	 * 
	 */
	public String getDBConfigXML() {
		try {
            if (reagentConfigXML == null)
			    reagentConfigXML = reagentMgmtServiceDelegate.getDBInfo("GBL");
		} catch (Exception e1) {
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "The reagent service failed return configuration information.", e1);
		}
		return reagentConfigXML;
	}

	/*
	 * Builds a map containing the reagent property display name and the datatype.
	 */
	public Map<String, String> buildReagentParamsMap() {
        Map<String, String> reagentPropertiesMap = new HashMap<String, String>();
		try {
			StringReader reader = new StringReader(getDBConfigXML());
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(reader);
			Element configXMLRoot = doc.getRootElement();
			List<Element> propertyList = XPath.selectNodes(configXMLRoot, "/ReagentsDatabaseInfo/MyReagents_Mandatory_Result_Fields/Field");
			for (int j = 0; j < propertyList.size(); j++) {
				Element dbElement = propertyList.get(j);
				String displayName = dbElement.getAttributeValue("Display_Name");
				String dataType = dbElement.getAttributeValue("DataType");
				String combo = dbElement.getAttributeValue("Combo");
				if (combo != null && combo.length() > 0)
					reagentPropertiesMap.put(displayName, combo);
				else
					reagentPropertiesMap.put(displayName, dataType);
			}
		} catch (Exception error) {
			log.error("Failed to build reagent parameters map.", error);
		}
        return reagentPropertiesMap;
	}

	/**
	 * get MyReagent list.
	 * 
	 */
	public String getMyReagentList() {
		try {
			myReagents = new String(ZIPUtil.unZip(reagentMgmtServiceDelegate.getMyReagentList(userName)));
		} catch (Exception e1) {
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "The reagent service failed to return 'My Reagent' list.", e1);
		}
		return myReagents;
	}

	/**
	 * update MyReagent list.
	 * 
	 */
	public void updateMyReagentList(String rgtList) {
		if (rgtList == null)
			return;
		try {
			// compress and send it to server
			reagentMgmtServiceDelegate.UpdateMyReagentList(userName, ZIPUtil.zip(rgtList.getBytes()));
		} catch (Exception e1) {
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "The reagent service failed to save 'My Reagent' list.", e1);
		}
	}

	public void updateIteratingInfo() {
		try {
			if (reagentsList != null) {
				StringReader reader = null;
				// Might be odd characters in the chemical names which cause a
				// parseException
				reader = new StringReader(reagentsList.replaceAll("\1", "").replaceAll("\0", ""));
				SAXBuilder builder = new SAXBuilder();
				Document doc = builder.build(reader);
				Element root = doc.getRootElement();
				Element interatingElement = (Element) XPath.selectSingleNode(root, "/Reagents/IteratingInfo");
				if (interatingElement != null) {
					hasMore = new Boolean(interatingElement.getAttributeValue("HasMore")).booleanValue();
					lastPosition = new Integer(interatingElement.getAttributeValue("LastPosition")).intValue();
					total = new Integer(interatingElement.getAttributeValue("Total")).intValue();
				} else {
					hasMore = false;
					lastPosition = 0;
					total = -1;
				}
			} else {
				hasMore = false;
				lastPosition = 0;
				total = -1;
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), e);
		}
	}

	/**
	 * get Reagent DB configuration XML root and db detail.
	 * 
	 */
	public Element buildDBXMLRoot() {
		try {
			StringReader reader = null;
			reader = new StringReader(reagentConfigXML);
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(reader);
			configXMLRoot = doc.getRootElement();
			return configXMLRoot;
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), e);
		}
		return null;
	}

	/**
	 * get Reagent list.
	 * 
	 */
	public byte[] doReagentSearch(String searchParamsXML) {
		byte[] result = null;
		callBackObj.setCancelReagentsSearch(false);
		isTimedOutExceptionOccured = false;
		try {
			byte[] originalBytes = reagentMgmtServiceDelegate.doReagentsSearch(searchParamsXML, callBackObj);
			if (originalBytes != null) {
				byte[] bytes = ZIPUtil.unZip(originalBytes);
				reagentsList = new String(bytes);
				result = bytes;
			}
		} catch (Exception e1) {
			String trace = ExceptionUtils.getStackTrace(e1);
			if (trace.indexOf("purge_calls") >= 0 || trace.indexOf("LockTimedOutException") >= 0 || trace.indexOf("Connection timed out") >= 0) {
				isTimedOutExceptionOccured = true;
				CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e1, "Reagent search request timed out, please refine your search.");
			} else
				CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e1, "The reagent service failed to perform search.");
		}
		return result;
	}

	/**
	 * Cancelling the search operation.
	 * 
	 */
	public void cancelReagentSearch() {
		callBackObj.setCancelReagentsSearch(true);
	}

	public boolean isSearchCancelled() {
		try {
			return callBackObj.isSearchCancelled();
		} catch (Exception exep) {
			return false;
		}
	}

	/**
	 * get Reagent structure by given compound number.
	 * 
	 */
	public String getStructureByCompoundNo(String compoundNumber) {
		isTimedOutExceptionOccured = false;
		if (StringUtils.isNotBlank(compoundNumber)) { 
			try {
				return reagentMgmtServiceDelegate.getStructureByCompoundNo(compoundNumber.trim());
			} catch (CompoundNotFoundException e1) {
				// Ignored
			} catch (Exception e2) {
				String trace = ExceptionUtils.getStackTrace(e2);
				if (trace.indexOf("purge_calls") >= 0 || trace.indexOf("LockTimedOutException") >= 0) {
					isTimedOutExceptionOccured = true;
					CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "Reagent Structure search request timed out, please refine your search.", e2);
				} else
					CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "The reagent service failed to return requested structure.", e2);
			}
		}
		return "";
	}

	/**
	 * get Reagent structure by given compound number.
	 * 
	 */
	public String getCompoundNo(List<String> nameList, List<String> valueList) {
		String compoundNum = null;
		try {
			for (int i = 0; i < nameList.size(); i++) {
				String displayName = nameList.get(i);
				if (compundNumberMap.containsKey(displayName)) {
					compoundNum = valueList.get(i).trim();
					if (compoundNum.length() > 0)
						break;
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), e);
		}
		return compoundNum;
	}

	public void buildCompoundNumberMap() {
		try {
			List<Element> relationList = XPath.selectNodes(getConfigXMLRoot(),
					"/ReagentsDatabaseInfo/Text_Structure_Relationships/Text_Structure_Relationship");
			List<Element> resultFiledList = XPath.selectNodes(getConfigXMLRoot(),
					"/ReagentsDatabaseInfo/Databases/Database/Tables/Table/Result_Fields/Field");
			for (int i = 0; i < resultFiledList.size(); i++) {
				Element resultFiledElement = resultFiledList.get(i);
				String columnName = resultFiledElement.getAttributeValue("Column_Name");
				for (int j = 0; j < relationList.size(); j++) {
					Element relationElement = relationList.get(j);
					String relationName = relationElement.getAttributeValue("Relation");
					if (relationName.equals(columnName)) {
						compundNumberMap.put(resultFiledElement.getAttributeValue("Display_Name"), columnName);
						break;
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), e);
		}
		// Map some legacy registry # display names
		compundNumberMap.put("CAS Registry#", "");
		compundNumberMap.put("Internal Registry #", "");
	}

	public HashMap<String, String> buildDBMap() {
		try {
			// construct Database List
			List<Element> totalDBList = XPath.selectNodes(getConfigXMLRoot(), "/ReagentsDatabaseInfo/Databases/Database");
			for (int j = 0; j < totalDBList.size(); j++) {
				Element dbElement = totalDBList.get(j);
				String dbDisplayName = dbElement.getAttributeValue("Display_Name");
				String dbName = dbElement.getAttributeValue("Name");
				totalDBMap.put(dbDisplayName, dbName);
			}
		} catch (JDOMException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), e);
		}
		return getTotalDBMap();
	}

	public ArrayList<String> buildMandatoryFieldsMap() {
		mandatoryFields.clear();
		try {
			// construct Mandatory Fields List
			List<Element> mandatoryFieldsList = XPath.selectNodes(getConfigXMLRoot(),
					"/ReagentsDatabaseInfo/MyReagents_Mandatory_Result_Fields/Field");
			for (int j = 0; j < mandatoryFieldsList.size(); j++) {
				Element mandatoryFieldsElement = mandatoryFieldsList.get(j);
				String mandatoryFieldsDisplayName = mandatoryFieldsElement.getAttributeValue("Display_Name");
				mandatoryFields.add(mandatoryFieldsDisplayName);
			}
		} catch (JDOMException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), e);
		}
		return mandatoryFields;
	}

	/**
	 * remove reagent service EJB.
	 * 
	 */
	public void removeReagentMgmtServiceEJB() {
//		try {
//			reagentMgmtServiceDelegate.removeEJB();
//		} catch (Exception e) {
//			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), e);
//		}
	}

	/**
	 * getters and setters
	 * 
	 */
	public String getMyReagents() {
		return myReagents;
	}

	public HashMap<String, String> getTotalDBMap() {
		return totalDBMap;
	}

	public String getReagentConfigXML() {
		return reagentConfigXML;
	}

	public String getReagentsList() {
		return reagentsList;
	}

	public String getcurrentReagentsList() {
		return reagentsList;
	}

	public int getInitialChunkSize() {
		return chunkSize;
	}
	
	public int getChunkSize(int total, int threadCount) {
		//return chunkSize;
		return total/threadCount;
	}

	public int getLastPosition() {
		return lastPosition;
	}

	public int getTotal() {
		return total;
	}

	public boolean getHasMore() {
		return hasMore;
	}

	public void setMyReagents(String myReagentList) {
		myReagents = myReagentList;
	}

	public void setReagentConfigXML(String configXML) {
		reagentConfigXML = configXML;
	}

	public void setReagentsList(String rList) {
		reagentsList = rList;
	}

	public void setAllFieldsList(ArrayList<String> aList) {
		allFieldsList = aList;
	}

	/*
	public void setChunkSize(int cSize) {
		chunkSize = cSize;
	}
	*/

	public void setTotal(int tl) {
		total = tl;
	}

	public void setLastPosition(int lPosition) {
		lastPosition = lPosition;
	}

	public void setHasMore(boolean hasmore) {
		hasMore = hasmore;
	}

	/**
	 * @return Returns the compundNumberMap.
	 */
	public HashMap<String, String> getCompundNumberMap() {
		return compundNumberMap;
	}

	/**
	 * @param compundNumberMap
	 *            The compundNumberMap to set.
	 */
	public void setCompundNumberMap(HashMap<String, String> compundNumberMap) {
		this.compundNumberMap = compundNumberMap;
	}

	/**
	 * @return Returns the reagentMgmtServiceDelegate.
	 */
	public ReagentMgmtServiceDelegate getReagentMgmtServiceDelegate() {
		return reagentMgmtServiceDelegate;
	}

	/**
	 * @param reagentMgmtServiceDelegate
	 *            The reagentMgmtServiceDelegate to set.
	 */
	public void setReagentMgmtServiceDelegate(ReagentMgmtServiceDelegate reagentMgmtServiceDelegate) {
		this.reagentMgmtServiceDelegate = reagentMgmtServiceDelegate;
	}

	/**
	 * @return Returns the allFieldsList.
	 */
	public ArrayList<String> getAllFieldsList() {
		return allFieldsList;
	}

	/**
	 * @return Returns the mandatoryFieldsMap.
	 */
	public ArrayList<String> getMandatoryFields() {
		return mandatoryFields;
	}

	/**
	 * @return Returns the configXMLRoot.
	 */
	public Element getConfigXMLRoot() {
		return configXMLRoot;
	}

	/**
	 * @param configXMLRoot
	 *            The configXMLRoot to set.
	 */
	public void setConfigXMLRoot(Element configXMLRoot) {
		this.configXMLRoot = configXMLRoot;
	}

	/**
	 * Use this only to load reagents that weren't created by the notebook. i.e. those with regNumbers
	 * 
	 * @param selectedReagent
	 * @return
	 * @throws JDOMException
	 */
	public MonomerBatchModel buildReagentBatchList(Element selectedReagent) throws JDOMException, CodeTableCacheException,
			ChemUtilInitException, ChemUtilAccessException {
		boolean invalidNumericValue = false;
		String fieldName = null;
		StringBuffer formula = new StringBuffer();
		StringBuffer batchMolWeight = new StringBuffer();
		StringBuffer parentMolWeight = new StringBuffer();
		MonomerBatchModel reagentBatch = new MonomerBatchModel();
		ParentCompoundModel compound = reagentBatch.getCompound();
		List<Element> fieldList = XPath.selectNodes(selectedReagent, "child::Field");
		for (int j = 0; j < fieldList.size(); j++) {
			Element fieldElement = fieldList.get(j);
			fieldName = fieldElement.getAttributeValue("Display_Name");
			String fieldValue = fieldElement.getText().trim();
//System.out.println("The field name is: " + fieldName + "\nThe field value is: " + fieldValue);
			if (fieldName.equals("Reagent Name")) {
				compound.setChemicalName(fieldValue);
			} else if (fieldName.equals("External #") || fieldName.equals("Internal Registry #")) {
				if (fieldValue.length() > 0) {
					compound.setRegNumber(fieldValue);
				}
			} else if (fieldName.equals("Molecular Formula")) {
				formula.append(fieldValue);
			} else if (fieldName.equals("Molecular Weight")) {
				if (StringUtils.isNotBlank(fieldValue)) {
					batchMolWeight.append(fieldValue);
					if (!validateNumericField(batchMolWeight.toString())) {
						invalidNumericValue = true;
						break;
					}
				}
			} else if (fieldName.equalsIgnoreCase("CAS Number")) {
				compound.setCASNumber(fieldValue);
			} else if (fieldName.equals("Parent Molecular Weight")) {
				if (StringUtils.isNotBlank(fieldValue)) {
					parentMolWeight.append(fieldValue);
					if (!validateNumericField(parentMolWeight.toString())) {
						invalidNumericValue = true;
						break;
					}
				}
			} else if (fieldName.equals("Melting Point")) {
				invalidNumericValue = !processAmount(reagentBatch.getCompound().getMeltingPt(), fieldValue);
			} else if (fieldName.equals("Boiling Point")) {
				invalidNumericValue = !processAmount(reagentBatch.getCompound().getBoilingPt(), fieldValue);
			} else if (fieldName.equals("Salt Code")) {
				try {
					if (StringUtils.isNotBlank(fieldValue)) {
						SaltCodeCache scc = SaltCodeCache.getCache();
						reagentBatch.setSaltForm(new SaltFormModel(fieldValue, 
						                                      scc.getDescriptionGivenCode(fieldValue),
						                                      scc.getMolFormulaGivenCode(fieldValue),
						                                      scc.getMolWtGivenCode(fieldValue)));
					} else {
						reagentBatch.setSaltForm(new SaltFormModel());
					}
				} catch (Exception e) {
		      		CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), e);
				}
			} else if (fieldName.equals("Salt Equivs")) {
				if (StringUtils.isBlank(fieldValue)) {
					reagentBatch.setSaltEquivs(0.0);
//TODO					reagentBatch.setSaltEquivsSet(false);
				} else {
					if (!validateNumericField(fieldValue)) {
						invalidNumericValue = true;
						break;
					} else {
						reagentBatch.setSaltEquivs((new Double(fieldValue)).doubleValue());
//TODO						reagentBatch.setSaltEquivsSet(true);
					}
				}
			} else if (fieldName.equals("Batch Comment")) {
				reagentBatch.setComments(fieldValue);
//			} else if (fieldName.equals("Batch Storage Comment")) {
//				reagentBatch.setStorageComment(fieldValue);
			} else if (fieldName.equals("Batch Hazard Comment")) {
				reagentBatch.setHazardComments(fieldValue);
//			} else if (fieldName.equals("Compound State")) {
//				reagentBatch.setCompoundState(fieldValue);
			} else if (fieldName.equals("Purity")) {
				invalidNumericValue = !processAmount(reagentBatch.getPurityAmount(), fieldValue);
			} else if (fieldName.equals("Concentration")) {
				invalidNumericValue = !processAmount(reagentBatch.getMolarAmount(), fieldValue);
			} else if (fieldName.equals("Density")) {
				invalidNumericValue = !processAmount(reagentBatch.getDensityAmount(), fieldValue);
			} else if (fieldName.equals("Resin Loading")) {
				invalidNumericValue = !processAmount(reagentBatch.getLoadingAmount(), fieldValue);
			} else if (fieldName.equals("Reagent Type")) {
				if (fieldValue.trim().equalsIgnoreCase("solvent"))
					reagentBatch.setBatchType(BatchType.SOLVENT);
				else if (fieldValue.trim().equalsIgnoreCase("reagent"))
					reagentBatch.setBatchType(BatchType.REAGENT);
				else
					reagentBatch.setBatchType(BatchType.REACTANT); // Default if not specified
			// No longer used but kept in case some users MyReagent list still reference them.
			} else if (fieldName.equals("Chirality Code")) {
				compound.setStereoisomerCode(fieldValue);
			} else if (fieldName.equals("Batch Number")) {
				try {
					reagentBatch.setBatchNumber(fieldValue);
				} catch (Exception e) {
				}
// Parent Batch Set below				
//			} else if (fieldName.equals("Parent Batch Number")) {
//				reagentBatch.setParentBatchNumber(fieldValue);
//			} else if (fieldName.equals("Vendor Name")) {
//				ExternalSupplier externalSupplier = new ExternalSupplier();
//				externalSupplier.setSupplierName(fieldValue);
//				reagentBatch.setVendorInfo(externalSupplier);
//			} else if (fieldName.equals("Protection Code")) {
//				reagentBatch.setProtectionCode(fieldValue);
//			} else if (fieldName.equals("Reg Status")) {
//				reagentBatch.setRegStatus(fieldValue);
			} else if (fieldName.equals("Project Tracking Code")) {
				reagentBatch.setProjectTrackingCode(fieldValue);
			} else if (fieldName.equals("Parent Batch Number")) {
				reagentBatch.setParentBatchNumber(fieldValue);
			} else if (fieldName.equals("Precursors")) {
				String[] precursorsArr = fieldValue.split(",");
				ArrayList<String> precursors = new ArrayList<String>();
				if (precursorsArr != null && precursorsArr.length > 0)
					for (int i = 0; i < precursorsArr.length; i++)
						precursors.add(precursorsArr[i]);
				reagentBatch.setPrecursors(precursors);
			}
		}
		// Check for new hazard comments if there aren't any currently associated
		if (StringUtils.isBlank(reagentBatch.getHazardComments())) {
			batchInfoUpdater.doUpdateHazards(reagentBatch);
		}
		// add structure
		String molFile = getStructureByCompoundNo(compound.getRegNumber());
		if (StringUtils.isNotBlank(molFile)) {
			try {
				compound.setMolfile(molFile);
				// Indigo loads name from MolFile, but we already have a name in MyReagentList (may be user-defined) and should use it
				String savedChemicalName = compound.getChemicalName();
				StructureLoadAndConversionUtil.loadSketch(molFile.getBytes(), ISISDRAW, false, compound);
				compound.setChemicalName(savedChemicalName);
			} catch (ChemUtilInitException e) {
				CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "Could not complete load of structure info.", e);
			} catch (ChemUtilAccessException e) {
				CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "Could not complete load of structure info.", e);
			}
		}
		if (invalidNumericValue) {
			JOptionPane.showMessageDialog(null, fieldName + NUMBER_FORMAT_ERROR_MESSAGE, "Invalid number format",
					JOptionPane.INFORMATION_MESSAGE);
		}
		reagentBatch.setCompound(compound);
		if (validateNumericField(batchMolWeight.toString()) && validateNumericField(parentMolWeight.toString()))
			processMolFormulaAndWeights(reagentBatch, formula.toString(), batchMolWeight.toString(), parentMolWeight.toString());
		if (reagentBatch.getBatchType().equals(BatchType.SOLVENT)) {
			reagentBatch.getWeightAmount().softReset();
			reagentBatch.getMoleAmount().softReset();
			reagentBatch.getRxnEquivsAmount().softReset();
			// if solvent is selected following Amounts are meaningless.
			reagentBatch.getDensityAmount().softReset();
			reagentBatch.getPurityAmount().softReset();
			reagentBatch.getLoadingAmount().softReset();
		}
		return reagentBatch;
	}

	private boolean validateNumericField(String numericStr) {
		if (StringUtils.isBlank(numericStr)) {
			return true;
		}
		if (numericStr.equals(".") || numericStr.endsWith(".")) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param amountModel - amount object to be altered
	 * @param value - string value to set.
	 * @return - true if a valid number was entered, false if not. Null and the empty string will reset the amount object.
	 */
	private boolean processAmount(AmountModel amountModel, String value) {
		boolean result = true;
		if (StringUtils.isBlank(value)) {
			amountModel.softReset();
		} else {
			if (!validateNumericField(value)) {
				result = false;
			} else {
				SignificantFigures sigs = new SignificantFigures(value);
				amountModel.setSigDigits(sigs.getNumberSignificantFigures());
				amountModel.setValue(value);
				// use if you want to set these values in the grid as user
				// defined.
				// amt.setValue(value, false);
			}
		}
		return result;
	}

	// We need to process MolFormula and Weight at the same time because
	// ACD and CompoundManagement do not tell us directly what the parent MolWgt and 
	// BatchMolWts are. Nor do they tell us the salt information other 
	// than what is given in the formula. Here we do our best to interpret
	// the responses.
	// We don't handle multiple salt types.
	// Don't overwrite a salt code that isn't parentForm.
	private void processMolFormulaAndWeights(MonomerBatchModel reagentBatch, String formula, String strBatchMW, String strParentMW)
			throws CodeTableCacheException {
		if (reagentBatch != null) {
			double batchMW = 0.0;
			double parentMW = 0.0;
			boolean processBatchMWFlag = (strBatchMW != null && strBatchMW.length() > 0);
			boolean processParentMWFlag = (strParentMW != null && strParentMW.length() > 0);
			boolean processFormulaFlag = (formula != null && formula.length() > 0);
			// CompoundManagement holds parent and batch molecular info. Just not for all compounds.
			if (processBatchMWFlag)
				batchMW = Double.parseDouble(strBatchMW);
			if (processParentMWFlag)
				parentMW = Double.parseDouble(strParentMW);
			if (CeNNumberUtils.doubleEquals(parentMW, 0.0, 0.0001))
				parentMW = reagentBatch.getCompound().getMolWgt();
			// StringBuffer parentFormula = new StringBuffer();
			// Default salt form is parent. If it is not a parent then we
			// need not process
			if (reagentBatch.getSaltForm().isParentForm() && StringUtils.isNotBlank(reagentBatch.getCompoundId())) {
				String[] regNumber = reagentBatch.getCompoundId().split("-");
				if (regNumber.length > 2 && regNumber[2].length() == 2 && !regNumber[2].equals(reagentBatch.getSaltForm().getCode())) { 
					// we have a salt
					SaltCodeCache scc = SaltCodeCache.getCache();
					SaltFormModel newSalt = new SaltFormModel(regNumber[2], 
					                                          scc.getDescriptionGivenCode(regNumber[2]),
					                                          scc.getMolFormulaGivenCode(regNumber[2]),
					                                          scc.getMolWtGivenCode(regNumber[2]));
					if (!newSalt.isParentForm())
						reagentBatch.setSaltForm(newSalt);
				} else { // regNumber doesn't account for salt
					// and we may only have one value for weight so we need to get the salt info
					// Check formula for salt.
					// Don't process salts
					// List saltsFound = getSaltFromFormula(formula);
					// // Do we have a salt to work with?
					// if (saltsFound.size() == 1) {
					//   // TODO: handle multiple salt types
					//   SaltForm newSalt = (SaltForm) saltsFound.get( 0 );
					//   if (!newSalt.isParentForm()) {
					//     parentFormula.append(stripSaltFromMolFormula(formula, (SaltForm) saltsFound.get( 0 ) ));
					//     if (!rb.getSaltForm().getCode().equalsIgnoreCase(newSalt.getCode() ))
					//       rb.setSaltForm( newSalt );
					//     if (parentFormula.length() > 0 && !parentFormula.toString().equalsIgnoreCase(
					//           											rb.getCompound().getMolFormula() ))
					//       rb.getCompound().setMolFormula(parentFormula.toString());
					//   }
					// } else
					// If number of salt molecs is > 1 we can't deal with it. Just over write the formula.
					// If there aren't any salts found we and the formulas don't match. over write it.
					if (processFormulaFlag && !formula.equalsIgnoreCase(reagentBatch.getCompound().getMolFormula()))
						reagentBatch.setMolFormula(formula);
				}
			}
			// Salt Code will be set by this point unless it already exists.
			// Now we turn to salt equivs. Parent weight will come from
			// either the calc'd version or
			// the one read from input. In either case, batchMW > parentMW indicates salt.
			// If batch weight > parent weight then we most likely have a salt
			if (!reagentBatch.getSaltForm().isParentForm() && batchMW > parentMW && !CeNNumberUtils.doubleEquals(parentMW, 0.0, 0.000001)) {
				if (!CeNNumberUtils.doubleEquals(reagentBatch.getSaltForm().getMolWgt(), 0.0, 0.0001)) {
					BigDecimal bd = new BigDecimal((batchMW - parentMW) / reagentBatch.getSaltForm().getMolWgt());
					double newEquivs = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					if (reagentBatch.getSaltEquivs() < newEquivs) {
						reagentBatch.setSaltEquivs(newEquivs);
					}
				}
			}
			// Now that we have weights accounted for we can set batch and
			// parent weights if inconsistent with entered fields.
			if (processBatchMWFlag && !CeNNumberUtils.doubleEquals(reagentBatch.getMolWgt(), batchMW, 0.0001)
					&& reagentBatch.getMolecularWeightAmount().isCalculated()) {
				processAmount(reagentBatch.getMolecularWeightAmount(), strBatchMW);
				// rb.getMolecularWeightAmount().setValue( batchMW );
			}
			reagentBatch.getMolecularWeightAmount().setUserPrefFigs(3);  // User request to have 3 fixed figs for Molecular weight displayed.
			if (processParentMWFlag && !CeNNumberUtils.doubleEquals(reagentBatch.getCompound().getMolWgt(), parentMW, 0.001))
				reagentBatch.getCompound().setMolWgt(parentMW);
		}
	}

	/**
	 * Returns a formula with the given salt form's mol formula removed. Relies on use of " . " notation to separate molecules and
	 * distinguish salt.
	 * 
	 * @param formula
	 * @return a list of matched salts if there are any otherwise an empty list is returned
	 */
	public String stripSaltFromMolFormula(String formula, SaltForm sf) {
		StringBuffer result = new StringBuffer();
		// We need to check each formula for the possibility of a salt.
		String[] formulas = formula.split("[.]");
		// Assume first entry will always be the product
		if (formulas.length >= 0)
			result.append(formulas[0].trim());
		for (int i = 1; i < formulas.length; i++) {
			// Remove any number from the lead of the string as it is equivs
			// notation.
			String tstSaltCode = formulas[i].trim();
			// Find first letter by stripping of everything before it.
			while (!Character.isLetter(tstSaltCode.charAt(0)))
				tstSaltCode = tstSaltCode.substring(1).trim();
			// Remove anything like end parenthesis.
			while (!Character.isLetterOrDigit(tstSaltCode.charAt(tstSaltCode.length() - 1)))
				tstSaltCode = tstSaltCode.substring(0, tstSaltCode.length() - 1).trim();
			// Hopefully we now have a code to test
			if (!sf.getFormula().equalsIgnoreCase(tstSaltCode)) {
				result.append(" . ");
				result.append(formulas[i]);
			}
		}
		return result.toString();
	}

	/**
	 * Takes a string formula and splits it by the '.' value and compares each component against the formulas for salts in the
	 * cache. Will not check the left most formula for salt.
	 * 
	 * @param formula
	 * @return a list of matched salts if there are any otherwise an empty list is returned
	 */
	public List<SaltFormModel> getSaltFromFormula(String formula) throws CodeTableCacheException {
		List<SaltFormModel> results = new ArrayList<SaltFormModel>();
		// We need to check each formula for the possibility of a salt.
		String[] formulas = formula.split("[.]");
		// Assume first entry will always be the product
		for (int i = 1; i < formulas.length; i++) {
			// Remove any number from the lead of the string as it is
			// extraneous.
			String tstSaltCode = formulas[i].trim();
			while (!(Character.isLetter(tstSaltCode.charAt(0)))) {
				tstSaltCode = tstSaltCode.substring(1).trim();
			}
			SaltCodeCache scc = SaltCodeCache.getCache();
			SaltFormModel sf = new SaltFormModel(tstSaltCode, 
			                                scc.getDescriptionGivenCode(tstSaltCode),
			                                scc.getMolFormulaGivenCode(tstSaltCode),
			                                scc.getMolWtGivenCode(tstSaltCode));
			if (sf != null)
				results.add(sf);
		}
		return results;
	}

	public MonomerBatchModel buildReagentBatchList(Element selectedReagent, HashMap<String, Integer> batchFieldsNumberMap) throws JDOMException {
		MonomerBatchModel reagentBatch = new MonomerBatchModel();
		ParentCompoundModel compound = new ParentCompoundModel();
		List<Element> fieldList = XPath.selectNodes(selectedReagent, "child::Field");
		for (int j = 0; j < fieldList.size(); j++) {
			Element fieldElement = fieldList.get(j);
			String fieldName = fieldElement.getAttributeValue("Display_Name");
			String fieldValue = fieldElement.getText().trim();
			// System.out.println("The field name is: " + fieldName);
			// System.out.println("The field value is: " + fieldValue);
			int fieldNumber = batchFieldsNumberMap.get(fieldName).intValue();
			// System.out.println("The field mapping number is: " + fieldNumber);
			switch (fieldNumber) {
				case 0:
					reagentBatch.setComments(fieldValue);
					break;
				case 1:
					reagentBatch.setHazardComments(fieldValue);
					break;
				case 2:
					reagentBatch.setConversationalBatchNumber(fieldValue);
					break;
				case 3:
//					reagentBatch.setStorageComment(fieldValue);
					break;
				case 4:
					processAmount(compound.getBoilingPt(), fieldValue);
					break;
				case 5:
					compound.setCASNumber(fieldValue);
					break;
				case 6:
					compound.setRegNumber(fieldValue);
					break;
				case 7:
					compound.setStereoisomerCode(fieldValue);
					break;
				case 8:
//					reagentBatch.setCompoundState(fieldValue);
					break;
				case 9:
					processAmount(reagentBatch.getMolarAmount(), fieldValue);
					break;
				case 10:
					processAmount(reagentBatch.getDensityAmount(), fieldValue);
					break;
				case 11:
					processAmount(compound.getMeltingPt(), fieldValue);
					break;
				case 12:
					compound.setRegNumber(fieldValue);
					// try {
					// 		compound.loadSketch(((String)(getSdfMap().get(fieldValue))).getBytes(), 0 );
					// } catch (ChemUtilInitException e) {
					// 		ceh.logExceptionMsg(this, "Could not complete load of structure info.", e);
					// } catch (ChemUtilAccessException e) {
					// 		ceh.logExceptionMsg(this, "Could not complete load of structure info.", e);
					// }
					break;
				case 13:
					compound.setMolFormula(fieldValue);
					break;
				case 14:
					// if (fieldValue == null || fieldValue.equals("")) {
					// 		reagentBatch.setMolWgt(0.0);
					// } else {
					// 		reagentBatch.setMolWgt((new Double(fieldValue)).doubleValue());
					// }
					processAmount(reagentBatch.getMolecularWeightAmount(), fieldValue);
					break;
				case 15:
					try {
						reagentBatch.setBatchNumber(fieldValue);
					} catch (InvalidBatchNumberException e) {
						CeNErrorHandler.getInstance().logExceptionMsg(e);
					}
					break;
				case 16:
					reagentBatch.setParentBatchNumber(fieldValue);
					break;
				case 17:
					if (fieldValue == null || fieldValue.equals("")) {
						compound.setMolWgt(0.0);
					} else {
						compound.setMolWgt((new Double(fieldValue)).doubleValue());
					}
					break;
				case 18:
					String[] precursorsArr = fieldValue.split(",");
					ArrayList<String> precursors = new ArrayList<String>();
					if (precursorsArr != null && precursorsArr.length > 0)
						for (int i = 0; i < precursorsArr.length; i++)
							precursors.add(precursorsArr[i]);
					reagentBatch.setPrecursors(precursors);
					break;
				case 19:
					reagentBatch.setProjectTrackingCode(fieldValue);
					break;
				case 20:
//					reagentBatch.setProtectionCode(fieldValue);
					break;
				case 21:
					processAmount(reagentBatch.getPurityAmount(), fieldValue);
					break;
				case 22:
					compound.setChemicalName(fieldValue);
					break;
				case 23:
//					reagentBatch.setRegStatus(fieldValue);
					break;
				case 24:
					try {
						SaltCodeCache scc = SaltCodeCache.getCache();
						reagentBatch.setSaltForm(new SaltFormModel(fieldValue, 
						                                      scc.getDescriptionGivenCode(fieldValue),
						                                      scc.getMolFormulaGivenCode(fieldValue),
						                                      scc.getMolWtGivenCode(fieldValue)));
					} catch (Exception e) {
			      		CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), e);
					}
					break;
				case 25:
					if (fieldValue == null || fieldValue.equals("")) {
						reagentBatch.setSaltEquivs(0.0);
//TODO						reagentBatch.setSaltEquivsSet(false);
					} else {
						reagentBatch.setSaltEquivs((new Double(fieldValue)).doubleValue());
//TODO						reagentBatch.setSaltEquivsSet(true);
					}
					break;
				case 26:
//					ExternalSupplier externalSupplier = new ExternalSupplier();
//					externalSupplier.setSupplierName(fieldValue);
//					reagentBatch.setVendorInfo(externalSupplier);
					break;
				default:
			}
		}
		// add structure
		String molFile = getStructureByCompoundNo(compound.getRegNumber());
		if (StringUtils.isNotBlank(molFile)) {
			try {
				compound.setMolfile(molFile);
				StructureLoadAndConversionUtil.loadSketch(molFile.getBytes(), ISISDRAW, false, compound);
			} catch (ChemUtilInitException e) {
				CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "Could not complete load of structure info.", e);
			} catch (ChemUtilAccessException e) {
				CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "Could not complete load of structure info.", e);
			}
		}
		reagentBatch.setCompound(compound);
		return reagentBatch;
	}
	
	public Map<String, String> getSdfMap() {
		return sdfMap;
	}

	/**
	 * This method is used in the reagents frame.
	 * 
	 * @return
	 */
	boolean isSearchTimedOut() {
		return isTimedOutExceptionOccured;
	}
}