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
package com.chemistry.enotebook.client.gui.page.batch;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountComponent;
import com.chemistry.enotebook.client.gui.page.regis_submis.RegCodeMaps;
import com.chemistry.enotebook.client.utils.AmountModelCalculator;
import com.chemistry.enotebook.compoundmgmtservice.delegate.CompoundMgmtServiceDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.batch.BatchSolubilitySolventModel;
import com.chemistry.enotebook.experiment.common.MolString;
import com.chemistry.enotebook.experiment.common.codetables.SaltCodeCache;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchRegistrationResidualSolvent;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchNumberException;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.utils.*;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.util.*;

/**
 * Make this a singleton to cache the lists.  11/21
 * 
 *
 */
public class BatchAttributeComponentUtility {

	private static final Log log = LogFactory.getLog(BatchAttributeComponentUtility.class);

	private static BatchAttributeComponentUtility myself = null;
	private static int monomerCount = 0;
	private static final int WEIGHT = 1;
	private static final int VOLUME = 2;
	private static final int MOLES = 3;
	
	private HashMap sicMap;  // stereoisomer code map
	
	private ArrayList compoundSourceList;
	private ArrayList sourceAndDetailList;
	private ArrayList residualSolventsList;
	private ArrayList solubilitySolventsList;
	private List<Properties> externalSuppliers;
	// saltCodes => 0 through 2 are strings: Code, Description, Formula. The last is a BigDecimal for MolWgt
	private List<Properties> saltCodes;  
	private ArrayList hazardsList;
	private ArrayList handlingList;
	private ArrayList storageList;
	private HashMap hazardsMap;
	private HashMap handlingMap;
	private HashMap storageMap;	
	private static String[] compoundProtections;
		
	public static BatchAttributeComponentUtility getInstance() {
		if (myself == null)
			myself = new BatchAttributeComponentUtility();
		return myself;
	}
	
	/**
	 * 
	 *
	 */
	private BatchAttributeComponentUtility() {
		this.initData();
	}
	
	/**
	 * Read in the code lists (probably class should be a singleton)
	 *
	 */
	private void initData() {
		sicMap = RegCodeMaps.getInstance().getStereoisomerCodeMap();
		compoundSourceList = RegCodeMaps.getInstance().getCompoundSourceList();
		sourceAndDetailList = RegCodeMaps.getInstance().getSourceAndDetailList();	
		try {
			saltCodes = SaltCodeCache.getCache().getCodesAsList();
			
			List<Properties> protections = CodeTableCache.getInstance().getProtectionCodes();
			List<String> prots = new ArrayList<String>();
			
			for (Properties p : protections)
				prots.add(p.getProperty(CodeTableCache.PROTECTION__PROTECTION_CODE) + " - " + p.getProperty(CodeTableCache.PROTECTION__PROTECTION_DESC));
			
			compoundProtections = prots.toArray(new String[0]);
		} catch (CodeTableCacheException e) {
			log.error("Failed to initialize data.", e);
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * Fill in the combo box with the stereoisomer codes
	 * @param comboBox
	 */
	public void fillStereoisomerComboBox(JComboBox comboBox) {
		CodeTableUtils.fillComboBoxWithIsomers(comboBox);	
	}

	/**
	 * Fill in the combo box with the protection codes which are HARDCODED above.
	 * @param comboBox
	 * @param batchModel
	 */
	public void fillProtectionCodesComboBox(JComboBox comboBox) {
		ComboBoxModel model = new DefaultComboBoxModel(compoundProtections);
		comboBox.setModel(model);
		comboBox.setSelectedIndex(0);
	}
	
	/**
	 * Fill in the salt code combo box using utilities below
	 * @param comboBox
	 * @param model
	 */
	public void fillSaltCodesComboBox(JComboBox comboBox, ProductBatchModel model) {
		if (model.getSaltForm() != null && model.getSaltForm().getCode() != null)
			comboBox.setSelectedItem(getCodeAndDesc(model.getSaltForm().getCode(), saltCodes, CodeTableCache.SALTS__SALT_CODE, CodeTableCache.SALTS__SALT_DESC));
	}
	
	/**
	 * Fill in the compound source and detail combo boxes, which are related.
	 * @param model
	 * @param comboBoxSource
	 * @param comboBoxDetail
	 * @param editable
	 * @param enabled
	 */
	public void fillCompoundSourceAndDetailComboBoxes(ProductBatchModel model,
													  CeNComboBox comboBoxSource,
													  CeNComboBox comboBoxDetail,
													  boolean editable,
													  boolean enabled) {
		if (comboBoxSource.getModel().getSize() == 0) {
			for (int i=0; i < compoundSourceList.size(); i++) {
				comboBoxSource.addItem(((Object[]) compoundSourceList.get(i))[1]);
			}
		}
		//comboBoxSource.enablePopUpCombo(200);
		//comboBoxDetail.setEditable(true);
		//comboBoxDetail.enablePopUpCombo(300);
		
		if (model != null)
			this.updateCompoundSourceAndDetailComboBox(model, comboBoxSource, comboBoxDetail, editable, enabled);
	}
	
	/**
	 * 
	 * @param comboBoxSource
	 */
	public void fillCompoundSourceComboBox(JComboBox comboBoxSource) {
		if (comboBoxSource.getModel().getSize() == 0) {
			for (int i=0; i < compoundSourceList.size(); i++) {
				comboBoxSource.addItem(((Object[]) compoundSourceList.get(i))[1]);
			}
		}	
	}
	
	/**
	 * Source detail depends on source.
	 * @param comboBoxSource
	 * @param comboBoxSourceDetail
	 */
	public void fillSourceDetailComboBox(JComboBox comboBoxSource, JComboBox comboBoxSourceDetail) {
		updateComboSourceDetail(comboBoxSource, comboBoxSourceDetail, true);
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	public List getCompoundSourceList() {
		ArrayList sourceList = new ArrayList();
		for (int i=0; i < compoundSourceList.size(); i++) {
			sourceList.add(((Object[]) compoundSourceList.get(i))[1]);
		}
		return sourceList;
	}
	
	/**
	 * 
	 * @param source
	 * @return
	 */
	public List getCompoundDetailsListForSource(String source) {
		ArrayList detailsList = new ArrayList();
		Iterator detailIterator = sourceAndDetailList.iterator();
		while (detailIterator.hasNext()) {
			Object[] detailCode = (Object[]) detailIterator.next();
			if (((String) (detailCode[2])).equals(source))
				detailsList.add((String) (detailCode[1]));
		}
		return detailsList;
	}

	/**
	 * Populate the compound source detail based on the selected compound source code
	 * @param comboBoxSource
	 * @param comboBoxDetail
	 */
	protected synchronized void updateComboSourceDetail(JComboBox comboBoxSource, JComboBox comboBoxDetail, boolean editable) {
		// populate source detail drop down based on the selection of source code
		comboBoxDetail.removeAllItems();
		try {
			String source = CodeTableCache.getCache().getSourceCode((String) comboBoxSource.getSelectedItem());
			Iterator detailIterator = sourceAndDetailList.iterator();
			comboBoxDetail.addItem(null);
			while (detailIterator.hasNext()) {
				Object[] detailCode = (Object[]) detailIterator.next();
				if (((String) (detailCode[2])).equals(source))
					comboBoxDetail.addItem((String) (detailCode[1]));
			}
			if (editable)
				comboBoxDetail.setEnabled(comboBoxDetail.getItemCount() > 1);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * Populate the compound source detail based on the selected compound source code
	 * @param comboBoxSource
	 * @param comboBoxDetail
	 * @param selectedSourceDetail 
	 */
	public synchronized void updateComboSourceDetail(String compoundSource, JComboBox comboBoxDetail, String selectedSourceDetailCode) {
		// populate source detail drop down based on the selection of source code
		comboBoxDetail.removeAllItems();
		Object selectedSourceDetailDesc = "";
		try {
			//String source = CodeTableCache.getCache().getSourceCode((String) compoundSource); 
			String source = compoundSource;
			Iterator detailIterator = sourceAndDetailList.iterator();
			comboBoxDetail.addItem(null);
			while (detailIterator.hasNext()) {
				Object[] detailCode = (Object[]) detailIterator.next();
				if (((String) (detailCode[2])).equals(source))
				{
					comboBoxDetail.addItem((String) (detailCode[1]));
					if (selectedSourceDetailCode != null && selectedSourceDetailCode.equals(detailCode[0]))
						selectedSourceDetailDesc = detailCode[1];
				}
				
			}
			comboBoxDetail.setEnabled(comboBoxDetail.getItemCount() > 1);
			comboBoxDetail.setSelectedItem(selectedSourceDetailDesc);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * Update the related compound source and detail combo boxes.
	 * @param model
	 * @param comboBoxSource
	 * @param comboBoxDetail
	 * @param editable
	 * @param enabled
	 */
	public synchronized void updateCompoundSourceAndDetailComboBox(ProductBatchModel model,
										   			  JComboBox comboBoxSource,
										   			  JComboBox comboBoxDetail,
										   			  boolean editable,
										   			  boolean enabled) {
		String selectedSourceDesc = "";
		if (comboBoxSource.getItemAt(0) != null)
			comboBoxSource.insertItemAt(null, 0);
		if (model.getRegInfo().getCompoundSource() != null && ! model.getRegInfo().getCompoundSource().equals("")) {
			try {
				comboBoxSource.setSelectedItem(CodeTableCache.getCache().getSourceDescription(model.getRegInfo().getCompoundSource()));
				if (model.getRegInfo().getCompoundSourceDetail() != null && ! model.getRegInfo().getCompoundSourceDetail().equals("")) {
					selectedSourceDesc = CodeTableCache.getCache().getSourceDetailDescription(model.getRegInfo().getCompoundSourceDetail());
				}
			} catch (Exception e) {
				e.printStackTrace();
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		} else {
			comboBoxSource.setSelectedIndex(0);
		}
		this.updateComboSourceDetail(comboBoxSource, comboBoxDetail, editable);
		if (model.getRegInfo().getCompoundSourceDetail() != null && ! model.getRegInfo().getCompoundSourceDetail().equals("")) {
			comboBoxDetail.setSelectedItem(selectedSourceDesc);
			}
	}
	
	/**
	 * Get just the compound source -- used for non-combo box presentations (non editable)
	 * @param model
	 * @return
	 */
	public String getCompoundSource(ProductBatchModel model) {
		try {
			return CodeTableCache.getCache().getSourceDescription(model.getRegInfo().getCompoundSource());
		} catch (CodeTableCacheException e) {
			e.printStackTrace();
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		return "";
	}
	
	/**
	 * Get just the source detail -- used for non-combo box presentations (non editable)
	 * @param model
	 * @return
	 */
	public String getSourceDetail(ProductBatchModel model) {
		try {
			String sourceDetail = CodeTableCache.getCache().getSourceDetailDescription(model.getRegInfo().getCompoundSourceDetail());
			if (sourceDetail != null)
				return sourceDetail;
		} catch (Exception e) {
			e.printStackTrace();
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		return "";
	}
	
	/**
	 * Update the stereoisomer combo box with the codes and descriptions
	 * @param model
	 * @param comboBox
	 * @param editable
	 * @param enabled
	 * @return
	 */
	public static synchronized JComboBox updateStereoisomerComboBox(BatchModel model,
											      JComboBox comboBox) {
		String stereoisomerCode = model.getCompound().getStereoisomerCode();
        updateStereoisomerComboBox(comboBox, stereoisomerCode);        
		return comboBox;
	}

    public static synchronized JComboBox updateReadOnlyStereoisomerComboBox(JComboBox comboBox, BatchModel model) {        
        String stereoisomerCode = model.getCompound().getStereoisomerCode();
        if (StringUtils.isBlank(stereoisomerCode) || "null".equals(stereoisomerCode)) {
            stereoisomerCode = "";
        } else {
            try {
                String descr = CodeTableCache.getCache().getStereoisomerDescription(stereoisomerCode);
                if (descr != null) {
                    stereoisomerCode += " - " + descr;
                }
            } catch (CodeTableCacheException e) {
                log.error("Error loading stereoisomer code for " + stereoisomerCode);
            }
        }
        comboBox.removeAllItems();

        comboBox.addItem(stereoisomerCode);
        return comboBox;
    }

    public static void updateStereoisomerComboBox(JComboBox comboBox, String stereoisomerCode) {
        if (StringUtils.isBlank(stereoisomerCode) || "null".equals(stereoisomerCode)) {
            stereoisomerCode = "";
        }

        for (int i = 0; i < comboBox.getItemCount(); i++) {
            final Object item = comboBox.getItemAt(i);
            if (item.toString().startsWith(stereoisomerCode)) {
                comboBox.setSelectedItem(item);
                break;
            }
        }

        if (StringUtils.isBlank(stereoisomerCode)) {
            updateStereoisomerComboBox(comboBox, "HSREG");
        }
    }

    /**
	 * Get the stereoisomer description from the code (for non editable presentation)
	 * @param code
	 * @return
	 */
	public synchronized String getStereoisomerDescriptionFromCode(String code) {
		if (code != null && ! code.equals("null") && code.length() > 0) {
			try {
				String description = CodeTableCache.getCache().getStereoisomerDescription(code);
				if (description != null) {
					return code + " - " + description;
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
		return code;
	}
	
	/**
	 * Update salt equiv field, disabling it if the salt code is parent (00)
	 * @param model
	 * @param comboBox
	 * @param saltEquivsAmount
	 */
	public synchronized void updateSaltEquivs(BatchModel model, JComboBox comboBox, PAmountComponent saltEquivsAmount) {
		String selectedSaltCode = (String) comboBox.getSelectedItem();
		String code = getCode(selectedSaltCode);
		if (code.equals("00")) {
			//jTextFieldAmountInWell.setText("" + well.getContainedAmount().getValue());
			saltEquivsAmount.setValue(new AmountModel(UnitType.SCALAR));
			saltEquivsAmount.setEditable(false);
		} else {
			double se = model.getSaltEquivs();
			AmountModel amountModel = new AmountModel(UnitType.SCALAR);
			amountModel.setValue(se);
			AmountModelCalculator.setSigFigsFromDouble(amountModel, se);
			saltEquivsAmount.setValue(amountModel);
			saltEquivsAmount.setEditable(true);
		}
	}

	/**
	 * 
	 * @param comboBox
	 */
	public synchronized void fillExternalSupplierComboBox(CeNComboBox comboBox) {
		if (externalSuppliers == null || externalSuppliers.size() == 0)
			externalSuppliers = this.getExternalSuppliersList();
		for (Iterator it = externalSuppliers.iterator(); it.hasNext();) {
			List supplierAttributes = (List) it.next();
			if (supplierAttributes.size() > 1 && supplierAttributes.get(1) != null) {
				comboBox.addItem(supplierAttributes.get(0) + " - " + supplierAttributes.get(1));
			} else {
				//System.out.println("Supplier attributes size problem...");
			}
		}
	}
	
	/**
	 * 
	 * @param model
	 * @param comboBox
	 */
	public synchronized void updateCompoundStatesComboBox(ProductBatchModel model, CeNComboBox comboBox) {
		String compoundState = model.getCompoundState();
		if (compoundState == null || compoundState.equalsIgnoreCase("null") || compoundState.length() == 0)
			comboBox.setSelectedIndex(0);
		else {
			// There must be a better way.  Fix!
			ComboBoxModel cbModel = comboBox.getModel();
			for (int i=0; i<cbModel.getSize(); i++) {
				String cstate = (String) cbModel.getElementAt(i);
				if (cstate.indexOf(compoundState) >= 0) {
					comboBox.setSelectedIndex(i);
					return;
				}
			}
			comboBox.setSelectedIndex(0);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public List getExternalSuppliersList() {
		try {
			if (externalSuppliers == null || externalSuppliers.size() == 0)
				externalSuppliers = CodeTableCache.getInstance().getVendors();
		} catch (CodeTableCacheException e) {
			log.debug("Failed to fetch vendor list.", e);
		}
		return externalSuppliers;  // will not be null
	}
	
	/**
	 * 
	 * @param model
	 * @param comboBox
	 */
	public synchronized void updateExternalSupplierComboBox(ProductBatchModel model, CeNComboBox comboBox) {
		if (model.getVendorInfo() != null) {
			ExternalSupplierModel exSupModel = model.getVendorInfo();
			String code = exSupModel.getCode();
			String codeAndDescription = this.getCodeAndDesc(code, (ArrayList) externalSuppliers, CodeTableCache.VENDOR__SUPPLIER_CODE, CodeTableCache.VENDOR__SUPPLIER_DESC);
			comboBox.setSelectedItem(codeAndDescription);
		} else {
			comboBox.setSelectedIndex(0);
		}
	}

	/**
	 * 
	 * @param model
	 * @param solubilityTtextArea
	 */
	public synchronized void updateSolubilityInSolventsList(ProductBatchModel model, JTextArea solubilityTtextArea) {
		ArrayList<BatchSolubilitySolventModel> list = model.getRegInfo().getSolubilitySolventList();
		if (list.size() != 0) {
			String solubilityString = new String();
			for (int i = 0; i < list.size(); i++) {
				BatchSolubilitySolventModel solubilitySolventVO = list.get(i);
				if (solubilitySolventVO.isQuantitative()) {
					if (solubilityString.length() > 0)
						solubilityString += ", ";
					solubilityString += solubilitySolventVO.getOperator() + " " + solubilitySolventVO.getSolubilityValue() + " "
					+ solubilitySolventVO.getSolubilityUnit() + " in " + solubilitySolventVO.getCodeAndName();
				} else {
					if (solubilityString.length() > 0)
						solubilityString += ", ";
					solubilityString += solubilitySolventVO.getQualiString() + " in " + solubilitySolventVO.getCodeAndName();
				}
			}
			solubilityTtextArea.setText(solubilityString);
			solubilityTtextArea.setToolTipText(solubilityTtextArea.getText());
		} else {
			solubilityTtextArea.setText("-none-");
			solubilityTtextArea.setToolTipText(null);
		}
	}

	/**
	 * 
	 * @param model
	 * @param residualSolventsTextArea
	 */
	public synchronized void updateResidualSolventsList(ProductBatchModel model, JTextArea residualSolventsTextArea) {
		// residual solvents
		ArrayList list = model.getRegInfo().getResidualSolventList();
		if (list.size() != 0) {
			String residualString = new String();
			for (int i = 0; i < list.size(); i++) {
				BatchRegistrationResidualSolvent residualSolventVO = (BatchRegistrationResidualSolvent) list.get(i);
				if (residualString.length() > 0)
					residualString += ", ";
				residualString += residualSolventVO.getEqOfSolvent() + " mols of " + residualSolventVO.getResidualDescription();
			}
			residualSolventsTextArea.setText(residualString);
			residualSolventsTextArea.setToolTipText(residualSolventsTextArea.getText());
		} else {
			residualSolventsTextArea.setText("-none-");
			residualSolventsTextArea.setToolTipText(null);
		}
	}	
	
	/**
	 * 
	 * @return
	 */
	public ArrayList getResidualSolventsList() {
		if (this.residualSolventsList == null || this.residualSolventsList.size() == 0) {
			residualSolventsList = RegCodeMaps.getInstance().getResidualSolventList();
		}
		return residualSolventsList;
	}

    /**
	 * 
	 * @return
	 */
	public ArrayList getSolubilitySolventsList() {
		if (this.solubilitySolventsList == null || this.solubilitySolventsList.size() == 0) {
			solubilitySolventsList = RegCodeMaps.getInstance().getSolubilitySolventList();
		}
		return solubilitySolventsList;
	}

    public ArrayList getHazardsList() {
		if (this.hazardsList == null || this.hazardsList.size() == 0)
			this.hazardsList = RegCodeMaps.getInstance().getHazardList();
		return hazardsList;
	}

	public ArrayList getHandlingList() {
		if (this.handlingList == null || this.handlingList.size() == 0)
			this.handlingList = RegCodeMaps.getInstance().getHandlingList();
		return handlingList;
	}
	
	public ArrayList getStorageList() {
		if (this.storageList == null || this.storageList.size() == 0)
			this.storageList = RegCodeMaps.getInstance().getStorageList();
		return storageList;		
	}

	public HashMap getHazardsMap() {
		if (this.hazardsMap == null || this.hazardsMap.size() == 0)
			this.hazardsMap = RegCodeMaps.getInstance().getHazardMap();
		return hazardsMap;
	}

	public HashMap getHandlingMap() {
		if (this.handlingMap == null || this.handlingMap.size() == 0)
			this.handlingMap = RegCodeMaps.getInstance().getHandlingMap();
		return handlingMap;
	}
	
	public HashMap<String, String> getStorageMap() {
		if (this.storageMap == null || this.storageMap.size() == 0)
			this.storageMap = RegCodeMaps.getInstance().getStorageMap();
		return storageMap;		
	}
	
	/**
	 * used for ComboBox.setSelectedItem Batch object only deals with codes, on the Panel Code & desc are displayed
	 * 
	 * @param code
	 * @param codes
	 * @return
	 */
	public static synchronized String getCodeAndDesc(String code, List codes, String codeCol, String descrCol) {
		String returnStr = "";
		if (codes != null) {
			for (int i = 0; i < codes.size(); i++) {
				Properties alInner = (Properties) codes.get(i);
				String code1 = (String) alInner.get(codeCol);
				if (code1.equals(code)) {
					returnStr = code + " - " + alInner.get(descrCol);// desc
				}
			}
		}
		return returnStr;
	}
	
	public static synchronized String getCodeAndDescFromStrArr(String code, ArrayList<String> codes) {
		String returnStr = "";
		if (codes != null) {
			for (int i = 0; i < codes.size(); i++) {
				String alInnerString = (String) codes.get(i);
				String[] alInner =  alInnerString.split(" - ");
				String code1 = (String) alInner[0];// Code
				if (code1.equals(code)) {
					returnStr = code + " - " + alInnerString.substring(alInnerString.indexOf(" - ") + 2, alInnerString.length() - 1);// desc
				}
			}
		}
		return returnStr;
	}
	
	
	public static synchronized String getDescFromStrArr(String code, ArrayList<Object> codes) {
		String returnStr = "";
		if (codes != null) {
			for (int i = 0; i < codes.size(); i++) {
				String alInnerString = (String) codes.get(i);
				String[] alInner =  alInnerString.split(" - ");
				String code1 = (String) alInner[0];// Code
				if (code1.equals(code)) {
					returnStr = alInnerString.substring(alInnerString.indexOf(" - ") + 2, alInnerString.length() - 1);// desc
				}
			}
		}
		return returnStr;
	}	
	
	public String getSaltDescFromCode(String code) {
		return getCodeAndDesc(code, saltCodes, CodeTableCache.SALTS__SALT_CODE, CodeTableCache.SALTS__SALT_DESC);
	}
	
	/**
	 * 
	 * @param codeAndDesc
	 * @return
	 */
	public static String getCode(String codeAndDesc) {
		if (codeAndDesc.indexOf(" -") > 0)
			if (!(codeAndDesc.substring(0, codeAndDesc.indexOf(" -")).equals("NONE")))
					return codeAndDesc.substring(0, codeAndDesc.indexOf(" -"));	
			return "";
	}


	public static AmountModel getMolWgt(MonomerBatchModel monomerBatchModel) {
		AmountModel parentMWModel = new AmountModel(UnitType.SCALAR);
		parentMWModel.setValue(monomerBatchModel.getCompound().getMolWgt());
		return parentMWModel;
	}
	
	// Amount getters
	public static AmountModel getTotalAmountMolarity(BatchModel batch) {
		return batch.getTotalMolarity();
	}

	public static AmountModel getTotalMoles(ProductBatchModel batch) {
		double totalMoles = batch.getTotalWeight().GetValueInStdUnitsAsDouble() / batch.getMolWgt();
		AmountModel molesAmount = new AmountModel(UnitType.MOLES, totalMoles);
		molesAmount.setUnit(batch.getTheoreticalMoleAmount().getUnit());
		return molesAmount;
	}	
	
	public static boolean setTotalMoles(BatchModel batch, AmountModel totalMolesAmountModel, PlateWell<? extends BatchModel> well) {
		double totalWeightInStdUnits = totalMolesAmountModel.GetValueInStdUnitsAsDouble() * batch.getMolWgt();
		if (well != null && (totalWeightInStdUnits < well.getContainedWeightAmount().GetValueInStdUnitsAsDouble()))
		{
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Total Amount Made must be equal to or more than the Amount In Well.");
			totalMolesAmountModel.SetValueInStdUnits(batch.getTotalWeight().GetValueInStdUnitsAsDouble() / batch.getMolWgt());
			return false;
		}
		AmountModel newTotalWeightAmountModel = new AmountModel(UnitType.MASS, totalWeightInStdUnits);
		newTotalWeightAmountModel.setUnit( batch.getTotalWeight().getUnit()); //Do not change unit based on moles. Total wt unit takes precedence.
		newTotalWeightAmountModel.setSigDigits(batch.getTotalWeight().getSigDigits()); 
		if (batch instanceof ProductBatchModel) {
			setTotalAmountMadeWeight((ProductBatchModel) batch, newTotalWeightAmountModel, (PlateWell<ProductBatchModel>) well);
		}
		batch.getTheoreticalMoleAmount().setUnit(totalMolesAmountModel.getUnit());
		return true;
	}
	
	public static AmountModel getTotalAmountMadeWeight(BatchModel batch) {
		return batch.getTotalWeight();
	}
	
	public static AmountModel getTotalAmountMadeVolume(BatchModel batch) {
		return batch.getTotalVolume();
	}
	
	public static AmountModel getWeightRemaining(BatchModel batch) {
		batch.getTotalWeight().setCalculated(true);
		return batch.getTotalWeight();
	}
	
	public static AmountModel getVolumeRemaining(BatchModel batch) {
		batch.getTotalVolume().setCalculated(true);
		return batch.getTotalVolume();
	}
	
	public static AmountModel getWeightRemaining(ProductPlate plate, BatchModel batch) {
		double totalStandaloneContainersAmount = 0;		
		double totalContainedAmount = 0.0;
    		
		if (batch instanceof ProductBatchModel) {
			ProductBatchModel prodBatch = (ProductBatchModel) batch;
			BatchRegInfoModel regInfo = prodBatch.getRegInfo();

			if (regInfo != null) {
				List<BatchSubmissionContainerInfoModel> standaloneContainers = regInfo.getSubmitContainerList();

				if (standaloneContainers != null) {
					Iterator<BatchSubmissionContainerInfoModel> iter = standaloneContainers.iterator();

					while (iter.hasNext()) {
						BatchSubmissionContainerInfoModel bsci = iter.next();
						double amount = bsci.getAmountValue();
						String unit = bsci.getAmountUnit();
						AmountModel amt = new AmountModel(UnitType.MASS);

						amt.setUnit(new Unit2(unit));
						amt.setValue(amount);
						totalStandaloneContainersAmount += amt.GetValueInStdUnitsAsDouble();
					}
				}
			}

			if (plate != null) {
				List<PlateWell<ProductBatchModel>> wells = plate.getPlateWellsforBatch(prodBatch);
				for(PlateWell<ProductBatchModel> well : wells) {
					AmountModel containedAmount = well.getContainedWeightAmount();
					totalContainedAmount += containedAmount.GetValueInStdUnitsAsDouble();
				}
			}
		}
		log.debug("total standalone container amount: " + totalStandaloneContainersAmount);
		if (totalStandaloneContainersAmount > 0) {
			log.debug("batch total weight sig figs: " +	batch.getTotalWeight().getSigDigits());
		}
		double remainingWeightAmount = batch.getTotalWeight().GetValueInStdUnitsAsDouble() -
										totalContainedAmount - totalStandaloneContainersAmount;
		AmountModel am = new AmountModel(UnitType.MASS);
    
		am.setValue(remainingWeightAmount);
		am.setUnit(batch.getTotalWeight().getUnit());
		am.setSigDigits(batch.getTotalWeight().getSigDigits());
		am.setCalculated(true);
		
		return am;
	}
	
	public static AmountModel getVolumeRemaining(ProductPlate plate, BatchModel batch) {
		double totalStandaloneContainersVolume = 0;
		double totalContainedAmount = 0.0;

		if (batch instanceof ProductBatchModel) {
			ProductBatchModel prodBatch = (ProductBatchModel) batch;
			BatchRegInfoModel regInfo = prodBatch.getRegInfo();

			if (regInfo != null) {
				List standaloneContainers = regInfo.getSubmitContainerList();

				if (standaloneContainers != null) {
					Iterator iter = standaloneContainers.iterator();

					while (iter.hasNext()) {
						BatchSubmissionContainerInfoModel bsci = (BatchSubmissionContainerInfoModel) iter.next();
						double amount = bsci.getVolumeValue();
						String unit = bsci.getVolumeUnit();
						AmountModel amt = new AmountModel(UnitType.VOLUME);

						amt.setUnit(new Unit2(unit));
						amt.setValue(amount);
						totalStandaloneContainersVolume += amt.GetValueInStdUnitsAsDouble();
					}
				}
			}
			if (plate != null) {
				List<PlateWell<ProductBatchModel>> wells = plate.getPlateWellsforBatch(prodBatch);
				for(PlateWell<ProductBatchModel> well : wells) {
					AmountModel containedAmount = well.getContainedVolumeAmount();
					totalContainedAmount += containedAmount.GetValueInStdUnitsAsDouble();
				}
			}
		}
		log.debug("total standalone container volume: " + totalStandaloneContainersVolume);
		if (totalStandaloneContainersVolume > 0) {
			log.debug("batch total volume sig figs: " + batch.getTotalVolume().getSigDigits());
		}

		double remainingVolumeAmount = batch.getTotalVolume().GetValueInStdUnitsAsDouble() -
										totalContainedAmount - totalStandaloneContainersVolume;
		AmountModel am = new AmountModel(UnitType.VOLUME);

		am.setValue(remainingVolumeAmount);
		am.setUnit(batch.getTotalVolume().getUnit());
		am.setSigDigits(batch.getTotalVolume().getSigDigits());
		am.setCalculated(true);

		return am;
	}
	
	public static boolean setAmountInWellOrTubeMoles(PlateWell well, AmountModel value, BatchModel batchModel) {
		double amtInWellWeightInStdUnits = value.GetValueInStdUnitsAsDouble() * batchModel.getMolWgt();
		if(batchModel instanceof ProductBatchModel)
		{
		if (amtInWellWeightInStdUnits > batchModel.getTotalWeight().GetValueInStdUnitsAsDouble())
		{
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Amount In Well must be less than the Total Amount Made.");
			value.setValue(well.getContainedWeightAmount().GetValueInStdUnitsAsDouble() / batchModel.getMolWgt());
			return false;
		}
		}
		else
		{
			if (amtInWellWeightInStdUnits > batchModel.getTotalWeight().GetValueInStdUnitsAsDouble())
			{
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Amount In Well must be less than the Total Amount Available.");
				value.setValue(well.getContainedWeightAmount().GetValueInStdUnitsAsDouble() / batchModel.getMolWgt());
				return false;
			}	
		}
		AmountModel newWellWeightAmountModel = new AmountModel(UnitType.MASS, amtInWellWeightInStdUnits);
		newWellWeightAmountModel.setUnit(well.getContainedWeightAmount().getUnit()); //Do not change unit based moles. Total wt unit takes precedence.
		newWellWeightAmountModel.setSigDigits(well.getContainedWeightAmount().getSigDigits());
		setAmountInWellOrTubeWeight(well, newWellWeightAmountModel, batchModel);
		batchModel.getTheoreticalMoleAmount().setUnit(value.getUnit());
		return true;
	}
	
/*	public static AmountModel getWeightRemaining(Tube tube, BatchModel batch) {
		AmountModel am = null;
		am = batch.getTotalWeight();
		am.setCalculated(true);
		return am;
	}*/
	
/*	public static AmountModel getVolumeRemaining(Tube tube, BatchModel batch) {
		AmountModel am = null;
		am = batch.getTotalVolume();
		am.setCalculated(true);
		return am;
	}*/
	
	public static AmountModel getAmountInWellOrTubeWeight(PlateWell well) {
		if (well != null)
			return (AmountModel) well.getContainedWeightAmount();
		else
		{
			AmountModel am = new AmountModel(UnitType.MASS);
			am.setValue(0.0d);
			return am;
		}
	}

	public static AmountModel getAmountInWellOrTubeMoles(PlateWell well, BatchModel batch) {
		if (well != null)
		{
			double moles = well.getContainedWeightAmount().GetValueInStdUnitsAsDouble() / batch.getMolWgt();
			AmountModel molesAmount = new AmountModel(UnitType.MOLES, moles);
			molesAmount.setUnit(batch.getTheoreticalMoleAmount().getUnit());
			return molesAmount;
		}
		else
		{
			AmountModel am = new AmountModel(UnitType.MOLES);
			am.setValue(0.0d);
			am.setUnit(batch.getTheoreticalMoleAmount().getUnit());
			return am;
		}
	}
	
	public static AmountModel getAmountInWellOrTubeVolume(PlateWell well) {
		return well.getContainedVolumeAmount();
	}
	
/*	public static AmountModel getAmountInVialWeight(BatchModel batch) {
		AmountModel am = new AmountModel(UnitType.MASS);
	
		return am;
	}*/
	
	public static AmountModel getAmountInWellMolarity(PlateWell<? extends BatchModel> well) {
		return well.getMolarity();
	}
	
	// amount setters
	
	public static boolean setTotalAmountMolarity(ProductBatchModel batch, AmountModel amount) {
		batch.setTotalMolarAmount( amount);
		batch.getTotalMolarity().setCalculated(false);
		return true;  
	}
	
	public static boolean setTotalAmountMadeWeight(ProductBatchModel batch, AmountModel amount, PlateWell<ProductBatchModel> well) {
		if (well != null && (amount.GetValueInStdUnitsAsDouble() < well.getContainedWeightAmount().GetValueInStdUnitsAsDouble()))
		{
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Total Amount Made must be equal to or more than the Amount In Well.");
			amount.setValue(batch.getTotalWeight().getValue());
			return false;
		}
		batch.setTotalWeightAmount(amount);
		batch.getTotalWeight().setCalculated(false);
		batch.recalcAmounts();
		//Sync all units
		ArrayList objectList = new ArrayList();
		objectList.add(batch);
		objectList.add(well);
		BatchAttributeComponentUtility.syncUnitsAndSigDigits(objectList, amount, WEIGHT);
		return true;  
	}
	
	public static boolean setTotalAmountMadeVolume(ProductBatchModel batch, AmountModel amount, PlateWell<ProductBatchModel> well) {
		if (well != null && (amount.GetValueInStdUnitsAsDouble() < well.getContainedVolumeAmount().GetValueInStdUnitsAsDouble()))
		{
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Total Amount Made must be equal to or more than the Amount In Well.");
			amount.setValue(batch.getTotalVolume().getValue());
			return false;
		}
		batch.setTotalVolumeAmount(amount);
		batch.getTotalVolume().setCalculated(false);
		batch.recalcAmounts();
		//Sync all units
		ArrayList objectList = new ArrayList();
		objectList.add(batch);
		objectList.add(well);
		BatchAttributeComponentUtility.syncUnitsAndSigDigits(objectList, amount, VOLUME);		
		return true;  
	}
	
	public static boolean setAmountInWellOrTubeWeight(PlateWell<? extends BatchModel> well, 
	                                                  AmountModel amount, 
	                                                  BatchModel batchModel) 
	{
		
		if (amount.GetValueInStdUnitsAsDouble() > batchModel.getTotalWeight().GetValueInStdUnitsAsDouble())
		{
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Amount In Well must be less than the Total.");
			amount.setValue(well.getContainedWeightAmount().getValue());
			return false;
		}
		
		well.setContainedWeightAmount(amount);
		if (well.getWellType() == null || well.getWellType().equals(""))//Pseudo Product Plate; Set it to Vial.
			well.setWellType(CeNConstants.CONTAINER_TYPE_VIAL);
		well.getContainedWeightAmount().setCalculated(false);
		//Sync all units
		ArrayList objectList = new ArrayList();
		objectList.add(batchModel);
		objectList.add(well);
		BatchAttributeComponentUtility.syncUnitsAndSigDigits(objectList, amount, WEIGHT);
		return true;  
	}
		
	public static boolean setAmountInWellOrTubeVolume(PlateWell<? extends BatchModel> well, 
	                                                  AmountModel amount, 
	                                                  BatchModel batchModel) {
		if ((amount.GetValueInStdUnitsAsDouble() > batchModel.getTotalVolume().GetValueInStdUnitsAsDouble()))
		{
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Amount In Well must be less than the Total.");
			amount.setValue(well.getContainedVolumeAmount().getValue());
			return false;
		}
		well.setContainedVolumeAmount(amount);
		well.getContainedVolumeAmount().setCalculated(false);
		//Sync all units
		ArrayList objectList = new ArrayList();
		objectList.add(batchModel);
		objectList.add(well);
		BatchAttributeComponentUtility.syncUnitsAndSigDigits(objectList, amount, VOLUME);
		return true;  
	}
	
	public static boolean setAmountInWellOrTubeMolarity(PlateWell<? extends BatchModel> well, AmountModel amount) {
		well.setContainedMolarity(amount);
		well.getMolarity().setCalculated(false);
		return true;  		
	}
	
	public static boolean setTheoreticalMoleAmount(ProductBatchModel batch, AmountModel amount) {
		batch.setTheoreticalMoleAmount(amount);
		batch.getTheoreticalMoleAmount().setCalculated(false);
		return true;
	}
	
	// vb 2/2 is this correct?
	public static void setDefaultTheoreticalAmounts(ProductBatchModel batch, NotebookPageModel pageModel) {
		List<String> monomerBatchKeys = batch.getReactantBatchKeys();
		ExperimentPageUtils pageUtils = new ExperimentPageUtils();
		for (String monomerKey : monomerBatchKeys) {
			MonomerBatchModel monomerBatch = pageUtils.getMonomerBatchInTheExperiment(monomerKey, pageModel);
			if (monomerBatch.isLimiting()) {
				batch.setTheoreticalMoleAmount(monomerBatch.getMoleAmount());
				batch.getTheoreticalMoleAmount().setCalculated(true);
				batch.getTheoreticalWeightAmount().SetValueInStdUnits(batch.getTheoreticalMoleAmount().GetValueInStdUnitsAsDouble() * batch.getMolWgt());
				batch.getTheoreticalWeightAmount().setCalculated(true);
				batch.recalcAmounts();
				break;
			}
		}
	}

	public static AmountModel getTheoreticalMoleAmount(ProductBatchModel productBatchModel) {
		return productBatchModel.getTheoreticalMoleAmount();
	}
	
	/**
	 * Get a string representation of the precursors of this batch.
	 * @param batch
	 * @param pageModel
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String getPrecursors(ProductBatchModel batch, NotebookPageModel pageModel) throws IllegalArgumentException  {
		StringBuffer buff = new StringBuffer();
		//it should be precursors rather than getReactantBatchKeys() or getPrecursorBatchKeys(). Note both are different
		List<String> monomerBatchKeys = batch.getPrecursors();
		ExperimentPageUtils pageUtils = new ExperimentPageUtils();
		if(monomerBatchKeys == null || monomerBatchKeys.size() == 0)
		{
			//precurosrs are empty for some reason try to use ReactantBatches.
			//IF this is also empty then there is some issue with logic or spid data
			monomerBatchKeys = batch.getReactantBatchKeys();
			log.debug("Pulling precursors from batch keys");
			for (String monomerKey : monomerBatchKeys) {
				MonomerBatchModel monomerBatch = pageUtils.getMonomerBatchInTheExperiment(monomerKey, pageModel);
				if (monomerBatch != null) {				
					if (buff.length() > 0) buff.append("  ");
					log.debug("reporting reg number for compound: " + monomerBatch.getMonomerId());
					buff.append(monomerBatch.getMonomerId());
				}
			}
			// ensure we did not miss any reactants ...
			List<ReactionStepModel> steps = pageModel.getReactionSteps();

			for (ReactionStepModel step : steps) {
				BatchesList<MonomerBatchModel> stoicBatches = step.getStoicBatchesList();
				List<MonomerBatchModel> batches = stoicBatches.getBatchModels();

				for (MonomerBatchModel batch2 : batches) {
					if (batch2.getBatchType() != null &&
						batch2.getCompound() != null && 
						batch2.getBatchType().equals(BatchType.REACTANT)) 
					{
						String regNumer = batch2.getCompound().getRegNumber();
						if (StringUtils.isNotBlank(regNumer) &&
							buff.indexOf(regNumer) < 0) 
						{
							if (buff.length() > 0) {
								buff.append("  ");
							}
							buff.append(regNumer);
						}
					}
				}
			}
		}
		//Precurosr CompoundIDs are already identifed and added(esp after exp load from DB)
		else
		{
			log.debug("Pulling precursors from batch");
			for (String monomerKey : batch.getPrecursors()) {
				MonomerBatchModel monomerBatch = pageUtils.getMonomerBatchInTheExperiment(monomerKey, pageModel);
				if (monomerBatch != null) {
					if (buff.length() > 0) buff.append("  ");
					buff.append(monomerBatch.getMonomerId());
					log.debug("appending precursor: " + monomerBatch.getMonomerId());
				}
			}
		}
		return buff.toString();
	}
	
	/**
	 * Get the NotebookBatchNumber editable part.  The NBK batch number only exists for product
	 * batches.  Return an empty string if it doesn't exist.
	 * @param batch
	 * @param value
	 * @return
	 */
	public static <T extends BatchModel>  String getNotebookBatchNumber(T batch) {
		if (StringUtils.isNotBlank(batch.getBatchNumberAsString())) {
			String nbkBatchNumber =  batch.getBatchNumber().getBatchNumber();
			if (nbkBatchNumber != null)
				return nbkBatchNumber.substring(nbkBatchNumber.lastIndexOf("-") + 1);
		}
		return "";
	}
	
	/**
	 * Set the editable part of the notebook batch number.  If the update is not successful, restore the old number.
	 * @param batch
	 * @param value
	 * @param pageModel 
	 * @return
	 * @throws InvalidBatchNumberException
	 * @throws IllegalArgumentException
	 */
	public static boolean setNotebookBatchNumber(BatchModel batch, String value, NotebookPageModel pageModel)
		throws InvalidBatchNumberException 
	{
		String oldBatchNumber = batch.getBatchNumber().getBatchNumber();
		String newBatchNumber = oldBatchNumber.substring(0, oldBatchNumber.lastIndexOf("-") + 1) + (String)value;
		BatchNumber nbkBatchNumber = new BatchNumber();
		//check if new batch # and existing batch # are the same
		if (batch.getBatchNumber().getBatchNumber().equals(newBatchNumber))
			return false;
		boolean successFlag = true;
		try {
			if (!ResourceKit.iaValidBatchNumber(newBatchNumber))
			{
				String msg = "The notebook batch number " + value + " is invalid.";
				throw new InvalidBatchNumberException(msg);
			}
			if (isExist(pageModel, newBatchNumber))
			{
				String msg = "The notebook batch number " + value + " exists in the system. Please try another number.";
				throw new InvalidBatchNumberException(msg);
			}
			nbkBatchNumber.setBatchNumber(newBatchNumber);
		} catch (InvalidBatchNumberException e) {
			successFlag = false;
			throw e;
		}
		if (!successFlag) {
			try {
				nbkBatchNumber.setBatchNumber(oldBatchNumber);
			} catch (InvalidBatchNumberException e) {
				// this should always work because it's the current one before the change
			}
		}
		batch.setBatchNumber(nbkBatchNumber);		
		return successFlag;
	}
	
	private static boolean isExist(NotebookPageModel pageModel, String newBatchNumber) {
		List<ProductBatchModel> batchesList = pageModel.getAllProductBatchModelsInThisPage();
		for (ProductBatchModel batchModel : batchesList)
		{
			if (batchModel != null && batchModel.getBatchNumberAsString().equals(newBatchNumber))
				return true;
		}
		return false;
	}

	/**
	 * Set the parent molecular weight, which is in the batch's compound 
	 * @param batch
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static boolean setParentMolWeight(BatchModel batch, AmountModel newVal) throws IllegalArgumentException {
		//AmountModel parentMWModel = new AmountModel(UnitType.SCALAR);  // vb 11/14 was mass
		//parentMWModel.setValue(batch.getCompound().getMolWgt());
		if (newVal.GetValueInStdUnitsAsDouble() != batch.getCompound().getMolWgt()) {
			batch.getCompound().setMolWgt(newVal.GetValueInStdUnitsAsDouble());
			return true;
		}
		return false;
	}
	
	/**
	 * Set the molecular weight of the batch
	 * @param batch
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static boolean setMolWeight(BatchModel batch, AmountModel newVal) throws IllegalArgumentException {
			batch.setMolecularWeightAmount(newVal);
			return true;
	}
	
	public static <T extends BatchModel> boolean setRxNWeight(T batch, AmountModel newVal, PlateWell<T> well) {
		batch.setWeightAmount(newVal);		
		//Loop thru all the plates and find out wells if null is passed for a well.
		//Sync Amounts
		ArrayList objectList = new ArrayList();
		objectList.add(batch);
		objectList.add(well);
		syncUnitsAndSigDigits(objectList, newVal, WEIGHT);
		return true;
	}

	public static boolean setRxNVolume(BatchModel batch, AmountModel newVal) {
		batch.setVolumeAmount(newVal);
		return true;
	}
	
	public static boolean setRxNMoles(BatchModel batch, AmountModel newVal) {
		batch.setMoleAmount(newVal);
		return true;
	}

	public static boolean setRxNEQ(BatchModel batch, AmountModel newVal) {
		batch.setRxnEquivsAmount(newVal);
		return true;
	}

	public static boolean setMonomerWellWeight(PlateWell well, AmountModel newVal) {
		well.setContainedWeightAmount(newVal);
		//((MonomerBatchModel)batch).setDeliveredWeight(deliveredWeightAmountModel);
		
		//Sync Units
		ArrayList objectList = new ArrayList();
		objectList.add(well.getBatch());
		objectList.add(well);
		syncUnitsAndSigDigits(objectList, well.getContainedWeightAmount(), WEIGHT);
		return true;
	}

	public static boolean setMonomerWellVolume(PlateWell well, Object value) {
		if (! (value instanceof AmountModel) ) 
			throw new IllegalArgumentException();
		AmountModel newVal = (AmountModel)value;
		well.setContainedVolumeAmount(newVal);
		//((MonomerBatchModel)batch).setDeliveredWeight(deliveredWeightAmountModel);
		
		//Sync Units
		ArrayList objectList = new ArrayList();
		objectList.add(well.getBatch());
		objectList.add(well);
		syncUnitsAndSigDigits(objectList, well.getContainedWeightAmount(), WEIGHT);
		return true;	
	}
	
	public static boolean setDeliveredMoles(PlateWell<MonomerBatchModel> well, AmountModel newVal) {
		MonomerBatchModel batch = well.getBatch();
		double weight =  newVal.GetValueInStdUnitsAsDouble() * batch.getMolWgt();
		well.getContainedWeightAmount().SetValueInStdUnits(weight);
		batch.getMoleAmount().setUnit(newVal.getUnit());
		//Sync Units
		ArrayList objectList = new ArrayList();
		objectList.add(batch);
		objectList.add(well);
		syncUnitsAndSigDigits(objectList, well.getContainedWeightAmount(), WEIGHT);
		return true;		
	}
	
	public static boolean setDeliveredWeight(MonomerBatchModel batch, AmountModel newVal) {
		batch.setDeliveredWeight(newVal);
		return true;
	}
	
	public static boolean setDeliveredVolume(MonomerBatchModel batch, AmountModel newVal) {
		batch.setDeliveredVolume(newVal);
		return true;
	}
	
	public static boolean setDeliveredMoles(MonomerBatchModel batch, AmountModel newVal) {
		double weight =  newVal.GetValueInStdUnitsAsDouble() * batch.getMolWgt();
		batch.getDeliveredWeight().SetValueInStdUnits(weight);
		batch.getMoleAmount().setUnit(newVal.getUnit());
		return true;
	}

	public static boolean setDensity(MonomerBatchModel batch, AmountModel newVal) {
		if (batch.getStoicReactionRole().equals(BatchType.SOLVENT.toString()))
		{
			return false;
		} else
		{
			batch.setDensityAmount(newVal);
			return true;
		}
	}
	
	public static boolean setMolarity(BatchModel batch, AmountModel newVal) {
		batch.setMolarAmount(newVal);
		return true;
	}	
	

	public static boolean setPurity(BatchModel batch, AmountModel newVal) {
		batch.setPurityAmount(newVal);
		return true;
	}
	
	/**
	 * If the salt code has changed, set it to the new value and return true.  Otherwise return false.
	 * @param batch
	 * @param value
	 * @return
	 */
	public static boolean setSaltCode(BatchModel batch, String value) {
		String[] values = value.split(" ");
		if (values.length > 0 && !values[0].equals(batch.getSaltForm().getCode())) {
			batch.setSaltForm(new SaltFormModel(values[0]));
			try {
				SaltFormModel salt = batch.getSaltForm();
				SaltCodeCache scc = SaltCodeCache.getCache();
			    salt.setDescription(scc.getDescriptionGivenCode(salt.getCode()));
			    salt.setFormula(scc.getMolFormulaGivenCode(salt.getCode()));
			    salt.setMolWgt(scc.getMolWtGivenCode(salt.getCode()));
			} catch (Throwable e) {
				log.error("Failed to fill in salt form information for batch: " + batch.getBatchNumberAsString() + " salt code: " + batch.getSaltForm().getCode(), e);
			}
			// If the salt code has been set back to the parent code, set the salt equiv to null
			if (SaltFormModel.isParentCode(values[0])) {
				batch.setSaltEquivs(0.0);
			}
			return true;
		} else return false;
	}
	
	public static boolean setCompoundState(ProductBatchModel batch, String selectedCompoundState) throws  IllegalArgumentException 
		{
			if (selectedCompoundState == null) selectedCompoundState = "";
			int index1 = selectedCompoundState.indexOf(" -");
			if (index1 > 0) {
				String compoundCode = selectedCompoundState.substring(0, index1);
				batch.setCompoundState(compoundCode);
			} else {
				batch.setCompoundState("");
			}
			return true;
		}
	
	public static String getCompoundSourceDisplayValue(String value)  
	{
		try {
			return CodeTableCache.getCache().getSourceDescription(value);
		} catch (CodeTableCacheException e) {
			log.warn("Failed to set source description.", e);
			return "";
		}		
		
/*		String selectedCompoundStateCode = batch.getCompoundState().toString();
		List li;
		try {
			li = CodeTableCache.getCache().getCacheAsArrayList(CodeTableCache.COMPOUND_STATE);
		} catch (CodeTableCacheException e) {
			e.printStackTrace();
			return "";
		}
		ArrayList row = null;
		for (int i = 0; i < li.size(); i++) {
			row = (ArrayList) li.get(i);
			String item = (String) row.get(0);
			if (item.equals(selectedCompoundStateCode))
				return (String)row.get(1);
		}
		return "";*/
	}	

	public static String getCompoundSourceDetailsDisplayValue(BatchModel batch)  
	{
		try {
			return CodeTableCache.getCache().getSourceDetailDescription(((ProductBatchModel)batch).getRegInfo().getCompoundSourceDetail());
		} catch (CodeTableCacheException e) {
			log.warn("Failure setting compound source detail description for display", e);
			return "";
		}
	}
	
	/**
	 * If the salt equivs has changed, set it to the new value and return true.  Otherwise return false.
	 * @param batch
	 * @param value
	 * @return
	 * @throws CodeTableCacheException
	 * @throws IllegalArgumentException
	 */
	public static boolean setSaltEquiv(BatchModel batch,  AmountModel newVal) throws CodeTableCacheException, IllegalArgumentException {
	    // won't work for salt equiv because it isn't stored as an amount model
//		SignificantFigures sigs = new SignificantFigures(newVal.getValue());
//		newVal.setSigDigits(sigs.getNumberSignificantFigures());
    	if(batch.getSaltEquivs() != newVal.GetValueInStdUnitsAsDouble()) {
    		batch.setSaltEquivs(newVal.GetValueInStdUnitsAsDouble());
    		return true;
    	} else { 
    		return false;
    	}
	}
	
	public static boolean setProductBatchStatus(ProductBatchModel batch, String selectedItem) throws IllegalArgumentException {
		int selectivityStatus = ProductBatchStatusMapper.getInstance().getSelectivityStatus(selectedItem);
		if (selectivityStatus >= 0) 
			batch.setSelectivityStatus(selectivityStatus);
		int continueStatus = ProductBatchStatusMapper.getInstance().getContinueStatus(selectedItem);
		if (continueStatus >= 0)
			batch.setContinueStatus(continueStatus);
		return true;
	}
	
//	public static boolean setTotalWeightMade(BatchModel batch, Object value) {
//		if (! (value instanceof AmountModel)) throw new IllegalArgumentException();
//		int sigDigits = batch.getTotalWeight().getSigDigits();
//		batch.getTotalWeight().deepCopy((AmountModel) value);
//		batch.getTotalWeight().setCalculated(false);
//		batch.getTotalWeight().setSigDigits(sigDigits);
//		// Set total volume to zero
////		sigDigits = batch.getTotalVolume().getSigDigits();
////		AmountModel totalVolumeModel = new AmountModel(UnitType.MASS);
////		totalVolumeModel.setSigDigits(sigDigits);
////		batch.getTotalVolume().deepCopy(totalVolumeModel);		
//		return true;
//	}
	
//	public static boolean setTotalVolumeMade(BatchModel batch, Object value) {
//		if (! (value instanceof AmountModel)) throw new IllegalArgumentException();
//		int sigDigits = batch.getTotalVolume().getSigDigits();
//		batch.getTotalVolume().deepCopy((AmountModel) value);
//		batch.getTotalVolume().setCalculated(false);
//		batch.getTotalVolume().setSigDigits(sigDigits);
//		// Set total weight to zero
////		sigDigits = batch.getTotalWeight().getSigDigits();
////		AmountModel totalWeightModel = new AmountModel(UnitType.MASS);
////		totalWeightModel.setSigDigits(sigDigits);
////		batch.getTotalWeight().deepCopy(totalWeightModel);		
//		((ProductBatchModel) batch).recalcTotalAmounts();
//		return true;
//	}	
	
	/**
	 * Get a map of the batch mol strings indexed by the batch key.  Make it a 
	 * linked map for sorting purposes.
	 * Note:  There are two ways of getting the mol strings depending on whether the batch
	 * is a simple compound or a set of compounds.
	 * Note even more:  Differentiating between the two depends on the String representation
	 * of the set of compounds.  It has to contain a ":" for this code to recognize it.
	 * @param batches
	 * @return
	 */
	public static LinkedHashMap<String, MolString> getCachedMonomerBatchesToMolstringsMap(List<MonomerBatchModel> batches, NotebookPageModel pageModel) {
		//return getMonomerBatchesToMolstringsMap(batches);
		boolean clsErrorFlag = false;
		long startTime = System.currentTimeMillis();
		LinkedHashMap<String, MolString> batchesToMolStringsMap = pageModel.getMonomerBatchMolStringMap();
		if (batchesToMolStringsMap == null) {
			batchesToMolStringsMap = new LinkedHashMap<String, MolString>();
			pageModel.setMonomerBatchMolStringMap(batchesToMolStringsMap);
		}
		for (MonomerBatchModel batchModel : batches) {
			if (batchesToMolStringsMap.containsKey(batchModel.getKey())) {
				continue;
			}
			String first_id = batchModel.getMonomerId();
			if (first_id.indexOf(":") < 0) {//assuming this is a id list
				String[] compoundIDs = new String[batches.size()] ;
				String strucs [] = null;
				for (int i = 0; i < batches.size(); i++) {
					batchModel = (MonomerBatchModel) batches.get(i);
					compoundIDs[i] = batchModel.getMonomerId();
				}
				try {
					List<String> structures = new ArrayList<String>();
					for (String compoundNo : compoundIDs)
						structures.add(new CompoundMgmtServiceDelegate().getStructureByCompoundNo(compoundNo).get(0));
					strucs = structures.toArray(new String[0]);
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "CLS lookup failed");
					clsErrorFlag = true;
				}
				for (int i=0; i<batches.size(); i++) {
					batchModel = (MonomerBatchModel) batches.get(i);
					MolString molString = null;
					if (strucs != null && strucs[i] != null)
						molString = new MolString(strucs[i], i);
					else
						molString = new MolString("", i);
					batchModel.getCompound().setMolfile(molString.toString());  
					batchesToMolStringsMap.put(batchModel.getKey(), molString);
				}
			} else {
				// Compound structures of form [MN-001234:MN-002345]
//					for (int i = 0; i < batches.size(); i++) {
//						batchModel = (MonomerBatchModel) batches.get(i);
					String conpressedStructureFromDSP = batchModel.getCompound().getStringSketchAsString();
					String decodedString = "";
					if(conpressedStructureFromDSP!=null ){
						decodedString =Decoder.decodeString(conpressedStructureFromDSP);
					}
					batchModel.getCompound().setMolfile(decodedString);  
					MolString molString = new MolString(decodedString, ++monomerCount);
					batchesToMolStringsMap.put(batchModel.getKey(), molString);
//					}
			}
		}
		if (clsErrorFlag)
		{
			CeNErrorHandler.showMsgOptionPane(MasterController.getGUIComponent(), "CLS Lookup Error", "CLS lookup failed", JOptionPane.ERROR_MESSAGE);
		}
		long endTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info(" getCachedMonomerBatchesToMolstringsMap " + (endTime - startTime) + " ms");
		}
		return batchesToMolStringsMap;
	}
	
	public static LinkedHashMap getMonomerBatchesToMolstringsMap(List<MonomerBatchModel> batches) {
		LinkedHashMap batchesToMolStringsMap = new LinkedHashMap();
		MonomerBatchModel batchModel = batches.get(0);
		String first_id = batchModel.getMonomerId();
		// Simple monomer ids of form MN-001234
		if (first_id.indexOf(":") < 0) {//assuming this is a id list
			String[] compoundIDs = new String[batches.size()] ;
			String strucs [] = null;
			for (int i = 0; i < batches.size(); i++) {
				batchModel = (MonomerBatchModel) batches.get(i);
				compoundIDs[i] = batchModel.getMonomerId();
			}
			try {
				List<String> structures = new ArrayList<String>();
				for (String compoundNo : compoundIDs)
					structures.add(new CompoundMgmtServiceDelegate().getStructureByCompoundNo(compoundNo).get(0));
				strucs = structures.toArray(new String[0]);
			} catch (Exception e) {
				CeNErrorHandler.showMsgOptionPane(MasterController.getGUIComponent(), "CLS Lookup Error", "CLS lookup failed", JOptionPane.ERROR_MESSAGE);
				CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "CLS lookup failed");
				log.error("CLS lookup failed");
			}
			for (int i=0; i<batches.size(); i++) {
				batchModel = (MonomerBatchModel) batches.get(i);
				MolString molString = new MolString(strucs[i], i);
				batchesToMolStringsMap.put(batchModel.getKey(), molString);
			}
		} else {
			// Compound structures of form [MN-001234:MN-002345]
			for (int i = 0; i < batches.size(); i++) {
				batchModel = (MonomerBatchModel) batches.get(i);
				String conpressedStructureFromDSP = batchModel.getCompound().getStringSketchAsString();
				String decodedString = "";
				if(conpressedStructureFromDSP!=null ){
					decodedString =Decoder.decodeString(conpressedStructureFromDSP);
				}
				batchModel.getCompound().setMolfile(decodedString);  
				MolString molString = new MolString(decodedString, i);
				batchesToMolStringsMap.put(batchModel.getKey(), molString);
			}
		}
		return batchesToMolStringsMap;
	}
	
	/**
	 * Get a map of the batch mol strings indexed by the batch key.  Make it a 
	 * linked map for sorting purposes.
	 * Note:  There are two ways of getting the mol strings depending on whether the batch
	 * is a simple compound or a set of compounds.
	 * Note even more:  Differentiating between the two depends on the String representation
	 * of the set of compounds.  It has to contain a ":" for this code to recognize it.
	 * @param batches
	 * @return
	 */
	public static LinkedHashMap getProductBatchesToMolstringsMap(List<ProductBatchModel> batches) {
		LinkedHashMap batchesToMolStringsMap = new LinkedHashMap();
		//BatchModel batchModel = (BatchModel) batches.get(0);
		BatchModel batchModel = null;
		for (int i = 0; i < batches.size(); i++) {
			batchModel = batches.get(i);
			String dspStructures = batchModel.getCompound().getStringSketchAsString();
			batchesToMolStringsMap.put(batchModel.getKey(), new MolString(Decoder.decodeString(dspStructures), i));
		}
		return batchesToMolStringsMap;
	}

	public static LinkedHashMap<String, MolString> getCachedProductBatchesToMolstringsMap(List<ProductBatchModel> batches, 
	                                                                                      NotebookPageModel pageModel) {
		//return getProductBatchesToMolstringsMap(batches);
		long startTime = System.currentTimeMillis();
		LinkedHashMap<String, MolString> batchesToMolStringsMap = pageModel.getProductBatchMolStringMap();
		if (batchesToMolStringsMap == null) {
			batchesToMolStringsMap = new LinkedHashMap<String, MolString>();
			pageModel.setProductBatchMolStringMap(batchesToMolStringsMap);
		}
		for (ProductBatchModel batchModel : batches) {
			if (batchesToMolStringsMap.containsKey(batchModel.getKey()) && 
			    StringUtils.isNotBlank(batchesToMolStringsMap.get(batchModel.getKey()).getMolString())) 
			{
				continue;
			}
			String dspStructures = batchModel.getCompound().getStringSketchAsString();
			if (StringUtils.isNotBlank(dspStructures)) {
				try {
					/*
					int index = dspStructures.indexOf("M  END");
					if (index == -1) {
						dspStructures = Decoder.decodeString(dspStructures);
					}
					*/
					int index1 = dspStructures.indexOf("M  END");
					int index2 = dspStructures.indexOf("AccObj");			
					if (index1 == -1 && index2 == -1) {
						dspStructures = Decoder.decodeString(dspStructures);
					} else {
						//TODO workaround for migrated data
						if (index2 != -1) {
							byte[] sketch = batchModel.getCompound().getStringSketch();
							try {
								ChemistryDelegate	chemDelegate = new ChemistryDelegate();
								sketch = chemDelegate.convertChemistry(sketch, "", "MDL Molfile");
								dspStructures = new String(sketch);
							} catch (Exception e) {
								CeNErrorHandler.getInstance().logExceptionMsg(e);						
								//e.printStackTrace();
							}
						}
					}
					
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "Decode of Design Service structure failed.");
					log.warn("Decode of Design Service structure failed. key = " + batchModel.getKey() + " see the database log for the exception.");
				}
			}
			batchesToMolStringsMap.put(batchModel.getKey(),
			                           new MolString(dspStructures,
			                                         getSeqNumberFromLotNumber(batchModel)));
		}
		long endTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info(" getCachedProductBatchesToMolstringsMap " + (endTime - startTime) + " ms");
		}
		return batchesToMolStringsMap;
	}
	

	public static LinkedHashMap removeBatchFromCachedProductBatchesToMolstringsMap(BatchModel batchModel, NotebookPageModel pageModel) {
		long startTime = System.currentTimeMillis();
		LinkedHashMap batchesToMolStringsMap = pageModel.getProductBatchMolStringMap();
		if (batchesToMolStringsMap == null) {
			batchesToMolStringsMap = new LinkedHashMap();
			pageModel.setProductBatchMolStringMap(batchesToMolStringsMap);
		}
		try {
			if (!batchesToMolStringsMap.containsKey(batchModel.getKey()))
				return batchesToMolStringsMap;
			Set batcheKeys = batchesToMolStringsMap.keySet();
			Iterator iter = batcheKeys.iterator();
			String batchKey = "";
			//String dspStructures = "";
			while (iter.hasNext())
			{
				batchKey = (String)iter.next();
				if (!batchKey.equals(batchModel.getKey()))
					continue;
				
				while (iter.hasNext())
				{
					batchKey = (String)iter.next();
					MolString molString = (MolString) batchesToMolStringsMap.get(batchKey);  
					molString.setIndex(molString.getIndex() - 1);
/*					dspStructures = batchModel.getCompound().getStringSketchAsString();
					batchesToMolStringsMap.put(batchModel.getKey(),
							new MolString(Decoder.decodeString(dspStructures),
									getSeqNumberFromLotNumber(batchModel)));
*/					
				}
				batchesToMolStringsMap.remove(batcheKeys);
				return batchesToMolStringsMap;
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "Decode of Design Service structure failed.");
			log.error("Decode of Design Service structure failed.");
		}
		long endTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info(" getCachedProductBatchesToMolstringsMap " + (endTime - startTime) + " ms");
		}
		return batchesToMolStringsMap;
	}
	
	
	private static int getSeqNumberFromLotNumber(BatchModel batchModel) {
		try {
			String lotnumstr = batchModel.getBatchNumber().getLotNumber();
			int lotnum = 0;
			try {
				StringBuffer buff = new StringBuffer();
				for (int i=0; i<lotnumstr.length(); i++) {
					char c = lotnumstr.charAt(i);
					if (c >= '0' && c <= '9')
						buff.append(c);
					else
						break;
				}
				if (buff.length() < 1)
					return 0;
				String numberPart = buff.toString();
				lotnum = Integer.parseInt(numberPart);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			return lotnum;
		} catch (RuntimeException e) {
			log.error("getSeqNumberFromLotNumber exception: " + e);
			return 0;
		}
	}

	public static boolean setCompoundSource(BatchModel batch, Object value) {
/*		if (! (value instanceof String) ) throw new IllegalArgumentException();
		if (! (batch instanceof ProductBatchModel) ) throw new IllegalArgumentException();
*/		
		try {
			String source = CodeTableCache.getCache().getSourceCode((String) value);
			((ProductBatchModel)batch).getRegInfo().setCompoundSource(source);
			((ProductBatchModel)batch).getRegInfo().setCompoundSourceDetail("");
			batch.setModelChanged(true);
		} catch (CodeTableCacheException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean setCompoundSourceDetails(BatchModel batch,
			Object value) {
		//if (! (value instanceof String) ) throw new IllegalArgumentException();
		//if (! (batch instanceof ProductBatchModel) ) throw new IllegalArgumentException();
/*		if (value != null && !value.equals(""))
		{
*/			String val;
			try {
				val = CodeTableCache.getCache().getSourceDetailCode((String) value);
			} catch (CodeTableCacheException e) {
				
				e.printStackTrace();
				return false;
			}
			((ProductBatchModel)batch).getRegInfo().setCompoundSourceDetail(val);
			batch.setModelChanged(true);
			return true;
/*		}
		else
			return false;
*/	}

	public static String getProtectionCode(ProductBatchModel batch) {
		if (batch.getProtectionCode() == null || batch.getProtectionCode().equals(""))
			return "NONE";
		else
			return batch.getProtectionCode();
	}

	public static String getCompoundProtectionCodeAndDesc(String code) {
		return getCodeAndDesc(code, compoundProtections);
	}

	public static synchronized String getCodeAndDesc(String code, String[] codes) {
		String returnStr = "";
		if (codes != null) {
			for (int i = 0; i < codes.length; i++) {
				String []StrArray = codes[i].split(" - ");
				String code1 =  (String) StrArray[0];// Code
				if (code1.equals(code)) {
					returnStr = codes[i];// desc
				}
			}
		}
		return returnStr;
	}

	public static void getCachedProductBatchesToMolstringsMap(ProductBatchModel productBatchModel,
			NotebookPageModel pageModel, boolean reloadFlag) {
		ArrayList<ProductBatchModel> list = new ArrayList<ProductBatchModel>();
		list.add(productBatchModel);
		//In order to reload the map, Molstring has to be empty. Next line will reload the right map.
		getCachedProductBatchesToMolstringsMap(list, pageModel).put(productBatchModel.getKey(),	new MolString("", 0));
		getCachedProductBatchesToMolstringsMap(list, pageModel);
	}
	
	public static String getPuritiesListAsString(List<PurityModel> purities) {
		StringBuffer result = new StringBuffer("");
		if (purities != null && purities.isEmpty() == false) {
			for (PurityModel purity : purities) {
				result.append(getFormattedPurityString(purity) + "\n");
			}
		}
		return result.toString();
	}
	
	public static String getPuritiesListAsHTMLString(List purities) {
		StringBuffer result = new StringBuffer("");
		result.append("<HTML><FONT TYPE=" + CommonUtils.getStandardTableCellFont() + ">");
		if (purities != null && purities.size() > 0) {
			for (Iterator i = purities.iterator(); i.hasNext();) {
				result.append((getFormattedPurityHTMLString((PurityModel) i.next())) + "<BR>");
			}
		}
		result.append("</HTML>");
		return result.toString();
	}

	private static String getFormattedPurityHTMLString(PurityModel next) {
		StringBuffer buff = new StringBuffer();

		if (next.isRepresentativePurity()) 
			buff.append("<B>");
		
		buff.append(next.getCode());
		
		if (next.isRepresentativePurity()) 
			buff.append("</B>");
		
		buff.append(" purity " + next.getOperator() + " " + next.getPurityValue().GetValueInStdUnitsAsDouble() + "% " + next.getComments());
		return buff.toString();
	}
	
	private static String getFormattedPurityString(PurityModel next) {
		StringBuffer buff = new StringBuffer();
		buff.append(next.getCode());
		buff.append(" purity " + next.getOperator() + " " + next.getPurityValue().GetValueInStdUnitsAsDouble() + "% " + next.getComments());
		if (next.isRepresentativePurity()) 
			buff.append("(representative purity)");
		return buff.toString();
	}

	public static ProductBatchModel syncBatch(ProductBatchModel sourceBatchModel, ReactionStepModel reactionStepModel) {
		
		ProductBatchModel newBatchModel = new ProductBatchModel();
		newBatchModel.setProductFlag(true);
		newBatchModel.setComments(sourceBatchModel.getComments());
		newBatchModel.setSaltForm(sourceBatchModel.getSaltForm());
		newBatchModel.setSaltEquivs(sourceBatchModel.getSaltEquivs());
		
		ParentCompoundModel sourceParentCompoundModel = sourceBatchModel.getCompound();
		syncParentCompoundModel(sourceParentCompoundModel, newBatchModel); 
		
		newBatchModel.setHazardComments(sourceBatchModel.getHazardComments());
		newBatchModel.setHandlingComments(sourceBatchModel.getHandlingComments());
		newBatchModel.setStorageComments(sourceBatchModel.getStorageComments());
		newBatchModel.setBatchType(BatchType.ACTUAL_PRODUCT);
		newBatchModel.setChloracnegenFlag(sourceBatchModel.isChloracnegenFlag());
		newBatchModel.setChloracnegenType(sourceBatchModel.getChloracnegenType());
		newBatchModel.setTestedForChloracnegen(sourceBatchModel.isTestedForChloracnegen());
		newBatchModel.setSynthesizedBy(sourceBatchModel.getSynthesizedBy());
		newBatchModel.setOwner(sourceBatchModel.getOwner());
		newBatchModel.setPrecursors(getPreCursorsForReaction(reactionStepModel, sourceBatchModel));
		String wellKey = "";
		syncAmounts(sourceBatchModel.getMoleAmount(), newBatchModel.getMoleAmount(), newBatchModel.getKey(), wellKey);
		syncAmounts(sourceBatchModel.getTheoreticalMoleAmount(), newBatchModel.getTheoreticalMoleAmount(), newBatchModel.getKey(), wellKey);
		newBatchModel.getTheoreticalMoleAmount().setCalculated(true);
		newBatchModel.setParentKey(sourceBatchModel.getKey());
		newBatchModel.setLoadedFromDB(false);
		newBatchModel.setModelChanged(true);
		return newBatchModel;
	}

	/**
	 * 
	 * @param sourceBatchModel 
	 * @param reactionStepModel 
	 * @return List of strings representing the precursors of each reactant and each reactant in that structurally contribute to the
	 *         final product.
	 */
	private static ArrayList<String> getPreCursorsForReaction(ReactionStepModel reactionStepModel, ProductBatchModel sourceBatchModel) {
		ArrayList<String> tmpList = new ArrayList<String>();
		List<String> smPrecursors = null;
		// Process all the Reagents to ensure the order is correct
		MonomerBatchModel[] monomerBatchModel = reactionStepModel.getReactantsForAProduct(sourceBatchModel);
		
		for (int i=0; i<monomerBatchModel.length; i++) {
			// Does this reactant have precursors? If so put them into
			// the precursor list instead of the reactant.
			smPrecursors = monomerBatchModel[i].getPrecursors();
			if (smPrecursors != null && smPrecursors.size() > 0) {
				for (String testStr : smPrecursors) {
					if (tmpList.indexOf(testStr) < 0)
						tmpList.add(testStr);
				}
			} else {
				// No precursors to add so we will try to add the reactant designator instead.
				// If the regNumber nor batch designator exist we can't add anything.
				if (StringUtils.isNotBlank(monomerBatchModel[i].getMonomerId())) {
					if (tmpList.indexOf(monomerBatchModel[i].getMonomerId()) < 0)
						tmpList.add(monomerBatchModel[i].getMonomerId());
					// Notified that batch numbers shouldn't appear in precursor list.
					// } else if (rb.getBatchNumberAsString() != null && rb.getBatchNumberAsString().length() > 0) {
					// if (tmpList.indexOf(rb.getBatchNumberAsString()) < 0) tmpList.add( rb.getBatchNumberAsString() );
				}
			}
			// clean up.
			smPrecursors.clear();
			smPrecursors = null;
//			monomerBatchModel = null;
		}

		return tmpList;
	}
	
	public static void syncParentCompoundModel(ParentCompoundModel sourceParentCompoundModel, ProductBatchModel newBatchModel) {
		String wellKey = "";
		ParentCompoundModel compModel = newBatchModel.getCompound();
		syncAmounts(sourceParentCompoundModel.getBoilingPt(), compModel.getBoilingPt(), newBatchModel.getKey(), wellKey);
		compModel.setCASNumber(sourceParentCompoundModel.getCASNumber());
		compModel.setChemicalName(sourceParentCompoundModel.getChemicalName());
		compModel.setComments(sourceParentCompoundModel.getComments());
		compModel.setCompoundName(sourceParentCompoundModel.getCompoundName());
		compModel.setCompoundParent(sourceParentCompoundModel.getCompoundParent());
		compModel.setCreatedByNotebook(sourceParentCompoundModel.isCreatedByNotebook());
		compModel.setExactMass(sourceParentCompoundModel.getExactMass());
		compModel.setHazardComments(sourceParentCompoundModel.getHazardComments());
		syncAmounts(sourceParentCompoundModel.getMeltingPt(), compModel.getMeltingPt(), newBatchModel.getKey(), wellKey);
		compModel.setMolfile(sourceParentCompoundModel.getMolfile());
		compModel.setMolFormula(sourceParentCompoundModel.getMolFormula());
		compModel.setMolWgt(sourceParentCompoundModel.getMolWgt());
		compModel.setNativeSketch(sourceParentCompoundModel.getNativeSketch());
		compModel.setNativeSketchFormat(sourceParentCompoundModel.getNativeSketchFormat());
		compModel.setRegNumber(sourceParentCompoundModel.getRegNumber());
		compModel.setStereoisomerCode(sourceParentCompoundModel.getStereoisomerCode());
		compModel.setStringSketch(sourceParentCompoundModel.getStringSketch());
		compModel.setStringSketchFormat(sourceParentCompoundModel.getStringSketchFormat());
		compModel.setStructureComments(sourceParentCompoundModel.getStructureComments());
		compModel.setViewSketch(sourceParentCompoundModel.getViewSketch());
		compModel.setVirtualCompoundId(sourceParentCompoundModel.getVirtualCompoundId());
		compModel.setModelChanged(true);
	}
	
	private static void syncAmounts(AmountModel sourceAmountModel,	AmountModel  newAmountModel, String batchKey, String wellKey) {
		newAmountModel.setValue(sourceAmountModel.getValue().toString());
		newAmountModel.setDefaultValue(sourceAmountModel.getDefaultValue());
		newAmountModel.setDisplayedFigs(sourceAmountModel.getDisplayedFigs());
		newAmountModel.setSigDigitsSet(sourceAmountModel.getSigDigitsSet());
		newAmountModel.setSigDigits(sourceAmountModel.getSigDigits());
		newAmountModel.setUserPrefFigs(sourceAmountModel.getUserPrefFigs());
		newAmountModel.setCalculated(sourceAmountModel.isCalculated());
		newAmountModel.getUnit().deepCopy(sourceAmountModel.getUnit());

	}
	
	public static void propagateMonomerBatchStatus(MonomerBatchModel batch, String newStatus, NotebookPageModel pageModel) {
		String currentStatus = batch.getStatus();
		if (currentStatus == null)
			currentStatus = "";
		// If the monomer batch status was changed back, reset the derived product statuses
		if (currentStatus.equals(CeNConstants.INSOLUBLE_DISCONTINUE) &&  !(newStatus.equals(CeNConstants.INSOLUBLE_DISCONTINUE))) {
			ExperimentPageUtils utils = new ExperimentPageUtils();
			List productBatches = utils.getProductsForPrecursor(batch.getKey(), pageModel);
			for (Iterator it = productBatches.iterator(); it.hasNext();) {
				ProductBatchModel pbatch = (ProductBatchModel) it.next();
				pbatch.setContinueStatus(CeNConstants.BATCH_STATUS_CONTINUE);
				pbatch.setSelectivityStatus(CeNConstants.BATCH_STATUS_PASS);
			}					
		} else if (newStatus.equals(CeNConstants.INSOLUBLE_DISCONTINUE) || newStatus.equals(CeNConstants.UNAVAILABLE_DISCONTINUE)) {
			ExperimentPageUtils utils = new ExperimentPageUtils();
			List productBatches = utils.getProductsForPrecursor(batch.getKey(), pageModel);
			for (Iterator it = productBatches.iterator(); it.hasNext();) {
				ProductBatchModel pbatch = (ProductBatchModel) it.next();
				pbatch.setContinueStatus(CeNConstants.BATCH_STATUS_DISCONTINUE);
				pbatch.setSelectivityStatus(CeNConstants.BATCH_STATUS_NOT_MADE);
			}
		} 
		
	}
	
	
	public static String getDeliveredMonomer(NotebookPageModel pageModel, List<MonomerPlate> monomerPlates, MonomerBatchModel batch) {
		HashMap<BatchModel, ArrayList<PlateWell<? extends BatchModel>>> batchPlatesWellsMap = pageModel.getBatchPlateWellsMap();
		int plateCount = 0;
		for (int i=0; i<monomerPlates.size(); i++)
		{
			MonomerPlate plate = monomerPlates.get(i);
			if (plate.getPlateBarCode().startsWith("Plate-"))	//Only the ASDI and Other plates should be considered. Not the conception plates from Synthesis Plan.
				continue;
			else
				plateCount++;
		}
		if (plateCount > 0)
		{
			ArrayList<PlateWell<? extends BatchModel>> plateWellList = batchPlatesWellsMap.get(batch);
			if (plateWellList != null)
			{
				for (int j=0; j<plateWellList.size(); j++)
				{
					PlateWell<? extends BatchModel> well = plateWellList.get(j);
					if (well.getBatch().getCompoundId().equals(batch.getCompoundId()))
					{
						return well.getBatch().getCompoundId();
					}
				}
			}
		}
		return "";
	}

	public static void syncUnitsAndSigDigits(ArrayList objectList, AmountModel amount, int property) {
		Unit2 unit = amount.getUnit();
		int sigDigits = amount.getSigDigits();
		for (int i=0; i<objectList.size(); i++)
		{
			Object object = objectList.get(i);
			if (object == null)
				continue;
			switch (property)
			{
				case WEIGHT:
					if (object instanceof ProductBatchModel)
					{
						ProductBatchModel productBatchModel = (ProductBatchModel)object;
						productBatchModel.getTotalWeight().setUnit(unit);
						productBatchModel.getTotalWeight().setSigDigits(sigDigits);
						productBatchModel.getTheoreticalWeightAmount().setUnit(unit);
						productBatchModel.getTheoreticalWeightAmount().setSigDigits(sigDigits);
					}
					else if (object instanceof MonomerBatchModel)
					{
						MonomerBatchModel monomerBatchModel = (MonomerBatchModel)object;
						monomerBatchModel.getStoicWeightAmount().setUnit(unit);
						monomerBatchModel.getStoicWeightAmount().setSigDigits(sigDigits);
					}
					else if (object instanceof PlateWell)
					{
						PlateWell well = (PlateWell)object;
						well.getContainedWeightAmount().setUnit(unit);
						well.getContainedWeightAmount().setSigDigits(sigDigits);
					}
					break;
					
				case VOLUME:
					if (object instanceof ProductBatchModel)
					{
						ProductBatchModel productBatchModel = (ProductBatchModel)object;
						productBatchModel.getTotalVolume().setUnit(unit);
						productBatchModel.getTotalVolume().setSigDigits(sigDigits);
						//productBatchModel.getVolumeAmount().setUnit(unit);
					}
					else if (object instanceof MonomerBatchModel)
					{
						
					}
					else if (object instanceof PlateWell)
					{
						PlateWell well = (PlateWell)object;
						well.getContainedVolumeAmount().setUnit(unit);
						well.getContainedVolumeAmount().setSigDigits(sigDigits);
					}
					break;
					
				case MOLES:
					if (object instanceof ProductBatchModel)
					{
						//ProductBatchModel productBatchModel = (ProductBatchModel)object;
						//productBatchModel.getTotalVolume().setUnit(unit);
						//productBatchModel.getVolumeAmount().setUnit(unit);
					}
					else if (object instanceof MonomerBatchModel)
					{
						MonomerBatchModel monomerBatchModel = (MonomerBatchModel)object;
						monomerBatchModel.getStoicMoleAmount().setUnit(unit);
						monomerBatchModel.getStoicMoleAmount().setSigDigits(sigDigits);
					}
					else if (object instanceof PlateWell)
					{
						PlateWell well = (PlateWell)object;
						well.getContainedVolumeAmount().setUnit(unit);
						well.getContainedVolumeAmount().setSigDigits(sigDigits);
					}
					break;
			}
		}
	}
	

	public static String getSolubilityCodeFromDecr(String decr) {
		String codeString = "";
		try {
			codeString = CodeTableCache.getCache().getSolubilitySolventCode(decr);
		} catch (CodeTableCacheException e) {
			CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "Look up code failed.");
		}
		return codeString;
	}
	
	public static String getSolubilityDecrFromCode(String code) {
		String decrString = "";
		try {
			decrString = CodeTableCache.getCache().getSolubilitySolventDescription(code);
		} catch (CodeTableCacheException e) {
			CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "Look up description failed.");
		}
		return decrString;
	}

	public static String getWellSolventCodeFromDecr(String decr) {
		String codeString = "";
		try {
			codeString = CodeTableCache.getCache().getSolventCode(decr);
		} catch (CodeTableCacheException e) {
			CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "Look up code failed.");
		}
		return codeString;
	}
	
	public static String getWellSolventDecrFromCode(String code) {
		String decrString = "";
		try {
			decrString = CodeTableCache.getCache().getSolventDescription(code);
		} catch (CodeTableCacheException e) {
			CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "Look up description failed.");
		}
		return decrString;
	}
}