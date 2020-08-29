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

import com.chemistry.enotebook.experiment.common.SaltForm;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchNumberException;
import com.chemistry.enotebook.experiment.datamodel.common.Amount2;
import com.chemistry.enotebook.experiment.datamodel.common.SignificantFigures;
import com.chemistry.enotebook.experiment.utils.BatchUtils;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;
import com.chemistry.enotebook.experiment.utils.GUIDUtil;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BatchModel extends CeNAbstractModel implements Comparable<BatchModel>, StoicModelInterface {

	public static final long serialVersionUID = 7526472295622776147L;

	// holder of the batch properties. Alternative to static declaration of all of them
	private HashMap<String, String> batchProperties = new HashMap<String, String>(100); 
	protected String batchId = null; // used by product and monomer batch models for respective productId and monomerId
	private BatchType batchType = null;
	// used to dynamically set the column names when used in a table
	private String[] propertyNames;

	private ParentCompoundModel compound; // Holds structure molFormula wgt and other info.
	private BatchNumber batchNumber = new BatchNumber();
	private String originalBatchNumber; 
	private String parentBatchNumber = ""; // No checking happens on this as there might be external values.
	private String conversationalBatchNumber = "";
	private String molecularFormula = ""; // If exists it overrides formula calc.
	private AmountModel molecularWeightAmount = new AmountModel(UnitType.SCALAR); // Holds batch molecular weight
	private AmountModel moleAmount = new AmountModel(UnitType.MOLES); // Unitless amount indicating how much of an Avagadro's
	// number of molecules we have
	private AmountModel weightAmount = new AmountModel(UnitType.MASS); // AmountModel will contain unit conversions original amount
	// and original units
	private AmountModel loadingAmount = new AmountModel(UnitType.LOADING); // Loading is generally mmol/gram - tackles resins
	private AmountModel volumeAmount = new AmountModel(UnitType.VOLUME); // AmountModel in volume
	private AmountModel densityAmount = new AmountModel(UnitType.DENSITY); // Density of compound in g/mL
	private AmountModel molarAmount = new AmountModel(UnitType.MOLAR); // Describes concentration of batch
	private AmountModel purityAmount = new AmountModel(UnitType.SCALAR, 100); // % Purity info 100 - 0
	private AmountModel rxnEquivsAmount = new AmountModel(UnitType.SCALAR, 1.0, 1.0); // Represents equivalants of compound to a
	// limiting
	// reagent in the reaction
	
	private SaltFormModel saltForm = new SaltFormModel("00"); // Must be from a vetted list
	private double saltEquivs;
	private boolean saltEquivsSet = true;
	protected boolean limiting = false;
	protected boolean isIntermediate = false;
	protected boolean autoCalcOn = true;

	private String owner = ""; // NTID of person who sponsored the creation of this batch
	private String comments = ""; // Batch Comments
	private String stoichComments = "";
	
	private String projectTrackingCode = ""; // project codes
	// Like MN-23423,PF-2423 etc
	private List<String> precursors = new ArrayList<String>(); // holds compound ids that were used to create this batch.

	protected boolean inCalculation = false; // Do not disturb. Batch is in process of calculating values

	private int transactionOrder = 0;

	private boolean testedForChloracnegen = false; // Flag to check if batch has been tested for Chloracnegen
	private boolean chloracnegenFlag = false; // Flag to identify if this batch is a chloracnegen hit after running structure
	// through CCT
	private String chloracnegenType = ""; // Type info like Class 1 ,Class 2 etc
	private int stepNumber = -1; // step this batch belongs to in PMC

	private String position = "";

	/*
	 * This will hold the List Key corresponding the Batch. It will be used while loading Batches to determine the listkey to which
	 * the batch is assigned to, which helps in building the BatchesList object.
	 */
	private String listKey = "";

	/*
	 * Added the following as of the new requirement in stoic 1.2
	 */
	private AmountModel soluteAmount = new AmountModel(UnitType.MASS);
	private String solute = "";
	private AmountModel totalVolume = new AmountModel(UnitType.VOLUME);
	private AmountModel totalWeight = new AmountModel(UnitType.MASS);
	private AmountModel totalMolarity = new AmountModel(UnitType.MOLAR); // Total Amount made molarity
	private String barCode = "";

	//	private static final double INVALID_VALUE = -1;

	public static final int UPDATE_TYPE_MOLES = 0;
	public static final int UPDATE_TYPE_WEIGHT = 1;
	public static final int UPDATE_TYPE_VOLUME = 2;

	public static final int UPDATE_TYPE_TOTAL_WEIGHT = 3; // vb 2/2
	public static final int UPDATE_TYPE_TOTAL_VOLUME = 4;
	public static final int UPDATE_TYPE_TOTAL_MOLARITY = 5;

	protected static final int CALC_MOLES = 1;
	protected static final int CALC_WEIGHT = 2;
	protected static final int CALC_VOLUME = 4;
	protected static final int CALC_RXN_EQUIVS = 8;
	protected static final int CALC_MOL_WGT = 16;
	protected static final int CALC_DENSITY = 32;
	protected static final int CALC_MOLARITY = 64;
	protected static final int CALC_LOADING = 128;

	// uses constants from above
	private int lastUpdatedType = UPDATE_TYPE_MOLES;
	// Describes concentration of batch before updating with latests Amount
	private final AmountModel previousMolarAmount = new AmountModel(UnitType.MOLAR); 

	// Solvents and mixtures that were added . Only one batch can be added. 
	// If more than one is needed then it should be a mixture type
	private String solventsAdded = "";
	private String synthesizedBy = ""; // NTID of chemist who synthesized the compound
	// This flag will be set if this Batch is to be deleted from DB. This is required
	// for removals of Reagents/Solvents from the stoic table
	private boolean toBeDeleted = false;

	private String stoicLabel = null;

	/*
	 * Constructors
	 */
	public BatchModel() {
		// Batch_Key in CeN table
		this.key = GUIDUtil.generateGUID(this);
		this.compound = new ParentCompoundModel();
	}

	public BatchModel(String key) {
		this(key, null);
	}

	public BatchModel(String batchKey, BatchNumber batchNumber) {
		this(batchKey, batchNumber, "", "", 0.0, null, null, null, null, null, 0.0);
	}

	public BatchModel(String batchKey, BatchNumber batchNumber, String parentBatchNumber, String saltFormCode, double saltEquivs,
			ParentCompoundModel compound, AmountModel moles, AmountModel weight, AmountModel volume) {
		this(batchKey, batchNumber, parentBatchNumber, saltFormCode, saltEquivs, compound, moles, weight, volume, null, 100.0);
	}

	public BatchModel(String batchKey, BatchNumber batchNumber, String parentBatchNumber, String saltFormCode, double saltEquivs,
			ParentCompoundModel compound, AmountModel moles, AmountModel weight, AmountModel volume, AmountModel density,
			double purity) {
		this(batchKey, batchNumber, parentBatchNumber, saltFormCode, saltEquivs, compound, moles, weight, volume, density, purity,
				false, "", "", "", "");
	}

	public BatchModel(String batchKey, BatchNumber batchNumber, String parentBatchNumber, String saltFormCode, double saltEquivs,
			ParentCompoundModel compound, AmountModel moles, AmountModel weight, AmountModel volume, AmountModel density,
			double purity, boolean isIntermediate, String compoundState, String protectionCode, String comments, String vendorName) {
		this(batchKey, batchNumber, parentBatchNumber, saltFormCode, saltEquivs, compound, moles, weight, volume, density, purity,
				false, compoundState, protectionCode, comments, vendorName, "", "");
	}

	public BatchModel(String batchKey, BatchNumber batchNumber, String parentBatchNumber, String saltFormCode, double saltEquivs,
			ParentCompoundModel compound, AmountModel moles, AmountModel weight, AmountModel volume, AmountModel density,
			double purity, boolean isIntermediate, String compoundState, String protectionCode, String comments, String vendorName,
			String owner, String synthesizedBy) {
		super();
		this.key = batchKey;
		if (compound == null)
			this.setCompound(new ParentCompoundModel());
		else
			this.setCompound(compound);

		if (batchNumber == null)
			this.batchNumber = new BatchNumber();
		else
			this.batchNumber = batchNumber;

		if (parentBatchNumber != null)
			this.parentBatchNumber = parentBatchNumber;
		if (SaltForm.isParentCode(saltFormCode)) {
			// saltForm.setCode("00"); // default parent salt code
			this.saltEquivs = 0.0; // no value for equivs
		} else {
			saltForm.setCode(saltFormCode);
			this.saltEquivs = saltEquivs;
		}
		// this.molecularWeightAmount.setCalculated(true);
		if (moles != null)
			moleAmount.deepCopy(moles);
		if (weight != null)
			weightAmount.deepCopy(weight);
		if (volume != null)
			volumeAmount.deepCopy(volume);
		if (density != null)
			densityAmount.deepCopy(density);
		if (CeNNumberUtils.doubleEquals(purity, 0.0, 0.001)) {
			purityAmount.setValue(100);
		} else {
			purityAmount.setValue(purity);
		}
		// purityAmount.setUserPrefFigs(2);
		this.setIntermediate(isIntermediate);
		if (comments != null)
			this.comments = comments;
		
		// this.rxnEquivsAmount.setSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
		this.rxnEquivsAmount.setValue(1.00);
		this.owner = owner;
		this.synthesizedBy = synthesizedBy;

	}

	protected String getBatchId() {
		return batchId;
	}

	protected void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public BatchModel(BatchModel model) {
		this(model.getKey());
		setBeingCloned(true);
		setCompound(model.getCompound());
		setSaltForm(model.getSaltForm());
		setBatchNumber(model.getBatchNumber());
		setMolecularWeightAmount(model.getMolecularWeightAmount());
		setMoleAmount(model.getMoleAmount());
		setWeightAmount(model.getWeightAmount());
		setLoadingAmount(model.getLoadingAmount());
		setVolumeAmount(model.getVolumeAmount());
		setDensityAmount(model.getDensityAmount());
		setMolarAmount(model.getMolarAmount());
		setPurityAmount(model.getPurityAmount());
		setRxnEquivsAmount(model.getRxnEquivsAmount());
		// batchModel.setBatchKey(rs.getString("BATCH_KEY"));
		setParentBatchNumber(model.getParentBatchNumber());
		setConversationalBatchNumber(model.getConversationalBatchNumber());
		setMolecularFormula(model.getMolecularFormula());
		setSaltEquivs(model.getSaltEquivs());
		setLimiting(model.isLimiting());
		setAutoCalcOn(model.isAutoCalcOn());
		setSynthesizedBy(model.getSynthesizedBy());
		setProjectTrackingCode(model.getProjectTrackingCode());
		setPrecursors(model.getPrecursors());
		setTransactionOrder(model.getTransactionOrder());
		setBatchType(model.getBatchType());
		setListKey(model.getListKey());
		setPosition(model.getPosition());
		setSolventsAdded(model.getSolventsAdded());
		setHazardComments(model.getHazardComments());
		setComments(model.getComments());
		setStoichComments(model.getStoichComments());
		setStoicLabel(model.getStoicLabel());
		this.setChloracnegenFlag(model.isChloracnegen());
		this.setTestedForChloracnegen(model.isTestedForChloracnegen());
		this.setChloracnegenType(model.getChloracnegenType());
		setLoadingFromDB(model.isLoadingFromDB());
		setBeingCloned(false);
	}

	public ParentCompoundModel getCompound() {
		return compound;
	}

	public void setCompound(ParentCompoundModel compound) {
		if (compound == null)
			return;
		this.compound = compound;
		this.modelChanged = true;
	}

	public boolean isIntermediate() {
		return isIntermediate;
	}

	public void setIntermediate(boolean isIntermediate) {
		this.isIntermediate = isIntermediate;
		this.modelChanged = true;
	}

	public BatchNumber getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(BatchNumber batchNumber) {
		if (batchNumber == null)
			this.batchNumber = new BatchNumber(); 
		else 
			this.batchNumber = batchNumber;
		this.modelChanged = true;
	}

	public HashMap<String, String> getBatchProperties() {
		return batchProperties;
	}

	public void setBatchProperties(HashMap<String, String> batchProperties) {
		if (batchProperties == null)
			return;
		this.batchProperties = batchProperties;
		this.modelChanged = true;
	}

	public boolean isChloracnegenFlag() {
		return chloracnegenFlag;
	}

	public void setChloracnegenFlag(boolean chloracnegenFlag) {
		this.chloracnegenFlag = chloracnegenFlag;
		this.modelChanged = true;
	}

	public String getChloracnegenType() {
		return chloracnegenType;
	}

	public void setChloracnegenType(String chloracnegenType) {
		if (chloracnegenType == null)
			return;
		this.chloracnegenType = chloracnegenType;
		this.modelChanged = true;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		if (comments == null)
			return;
		this.comments = comments;
		this.modelChanged = true;
	}

	

	public String getConversationalBatchNumber() {
		return conversationalBatchNumber;
	}

	public void setConversationalBatchNumber(String conversationalBatchNumber) {
		if (conversationalBatchNumber == null)
			return;
		this.conversationalBatchNumber = conversationalBatchNumber;
		this.modelChanged = true;
	}

	public AmountModel getDensityAmount() {
		return densityAmount;
	}

	public boolean isLimiting() {
		return limiting;
	}

	public void setLimiting(boolean limiting) {
		this.limiting = limiting;
		setModified(true);
	}

	public AmountModel getLoadingAmount() {
		return loadingAmount;
	}

	public AmountModel getMolarAmount() {
		return molarAmount;
	}

	public AmountModel getMoleAmount() {
		return moleAmount;
	}
	
	/**
	 * Clears the molecularForumla attribute for recalculation.
	 * Prefer using this method to the side-effects in others, but look up getMolecularFormula for 
	 * further side-effect warnings.
	 * TODO: eliminate side-effects where appropriate.  To do this change the logic to 
	 * refer to parent formula differently from batch forumla and know when to use each.
	 * Parent formula by business rule shouldn't include salt information but currently does. 
	 */
	public void updateMolecularFormula() {
		// TODO should be special method for molformula updating
		molecularFormula = "";
		molecularFormula = getMolecularFormula();
	}
	
	/**
	 * 
	 * Side-Effect warning: changes molecularFormula attribute in the compound.
	 * The original intent of this method was to tack on the salt code to the parent compound, 
	 * not to update the parent compound molecular formula with a salt code.
	 * TODO: remove this side-effect and get applications to rely on updateMolecularFormula instead.
	 *       Logic should not store salts in the compound molecular formula
	 *        
	 * @return
	 */
	public String getMolecularFormula() {
		StringBuffer result = new StringBuffer("");
		if (StringUtils.isNotBlank(molecularFormula)) {
			// returning batch molecular formula
			result.append(molecularFormula.trim());
		} else {
			// returns parent molecular formula
			result.append(compound.getMolFormula());
		}
		if (StringUtils.isNotBlank(result.toString()) && !getSaltForm().isParentForm() && getSaltEquivs() > 0) {
				if (result.indexOf(".") != -1) {
					result.delete(result.indexOf(".") - 1, result.length());
				}
				result.append(" . ");
				if (getSaltEquivs() > 1) {
					result.append(getSaltEquivs()).append("(");
				}
				result.append(getSaltForm().getFormula());
				if (getSaltEquivs() > 1) {
					result.append(")");
				}
			}
			setMolFormula(result.toString().trim());
		return result.toString().trim();
	}
	
	/**
	 * Sets the parent molecular formula - the one in the compound object.
	 * @param molFormula
	 */
	public void setMolFormula(String molFormula) {
		if (StringUtils.isBlank(molFormula)) {
			molFormula = "";
		}
		
		// Strip Salt info or Residual Solvent info, if present
		int dot = molFormula.indexOf(".");
		if (dot >= 0) {
			molFormula = molFormula.substring(0, dot);
		}
		int semiPos = molFormula.indexOf(";");
		if (semiPos >= 0) {
			molFormula = molFormula.substring(0, semiPos);
		}

		molFormula = molFormula.trim();
		
		getCompound().setMolFormula(molFormula);
	}

	/**
	 * Overrides calc'd molFormula. Do not use unless this is the intention. 
	 * Used in special cases for Analyze Reaction and Reagent Lookup returns
	 * 
	 * @param molFormula
	 */
	public void setMolecularFormula(String molecularFormula) {
		this.molecularFormula = molecularFormula == null ? "" : molecularFormula.trim();
		setModified(true);
	}

	public AmountModel getMolecularWeightAmount() {
		double result = molecularWeightAmount.GetValueInStdUnitsAsDouble();
		if (result == 0) {
			result = this.getMolWgtCalculated();
		}
		molecularWeightAmount.setValue(result);
		return molecularWeightAmount;
	}

    public AmountModel getExactMassAmount() {
        AmountModel result = (AmountModel)molecularWeightAmount.deepClone();
        result.setValue(getCompound().getExactMass());
        return result;
    }

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		if (owner == null)
			return;
			this.owner = owner;
			this.modelChanged = true;
		}

	public String getParentBatchNumber() {
		return parentBatchNumber;
	}

	public void setParentBatchNumber(String parentBatchNumber) {
		if (parentBatchNumber == null)
			return;
			this.parentBatchNumber = parentBatchNumber;
			this.modelChanged = true;
		}

	public List<String> getPrecursors() {
		return precursors;
	}

	public void setPrecursors(List<String> precursors) {
		if (precursors == null)
			return;
		this.precursors = precursors;
		this.modelChanged = true;
	}

	public String getProjectTrackingCode() {
		return projectTrackingCode;
	}

	public void setProjectTrackingCode(String projectTrackingCode) {
		if (projectTrackingCode == null)
			return;
			this.projectTrackingCode = projectTrackingCode;
			this.modelChanged = true;
		}

	public String[] getPropertyNames() {
		return propertyNames;
	}

	public void setPropertyNames(String[] propertyNames) {
		if (propertyNames == null)
			return;
		this.propertyNames = propertyNames;
		this.modelChanged = true;
	}

	

	public AmountModel getPurityAmount() {
		return purityAmount;
	}

	

	public AmountModel getRxnEquivsAmount() {
		return rxnEquivsAmount;
	}

	public double getSaltEquivs() {
		return saltEquivs;
	}

	public void setSaltEquivs(double equivs) {
		if (saltForm.isParentForm() == false) {
			this.saltEquivs = equivs;
			if (molecularWeightAmount.isCalculated() && isLoadingFromDB() == false && isBeingCloned() == false) {
				setMolWgtCalculated(getMolWgtCalculated());
			}
			recalcAmounts();
			this.modelChanged = true;
			// calc new batch MF;
			updateMolecularFormula();
		}
	}

	public SaltFormModel getSaltForm() {
		return saltForm;
	}

	public int getStepNumber() {
		return stepNumber;
	}

	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
		this.modelChanged = true;
	}


	public String getSynthesizedBy() {
		return synthesizedBy;
	}

	public void setSynthesizedBy(String synthesizedBy) {
		if (synthesizedBy == null)
			return;
			this.synthesizedBy = synthesizedBy;
			this.modelChanged = true;
		}

	public boolean isTestedForChloracnegen() {
		return testedForChloracnegen;
	}

	public void setTestedForChloracnegen(boolean testedForChloracnegen) {
		this.testedForChloracnegen = testedForChloracnegen;
		this.modelChanged = true;
	}

	
	public AmountModel getVolumeAmount() {
		return volumeAmount;
	}

	public AmountModel getWeightAmount() {
		return weightAmount;
	}

	/**
	 * @return the autoCalcOn
	 */
	public boolean isAutoCalcOn() {
		return autoCalcOn;
	}

	/**
	 * @param autoCalcOn
	 *            the autoCalcOn to set
	 */
	public void setAutoCalcOn(boolean autoCalcOn) {
		this.autoCalcOn = autoCalcOn;
		this.modelChanged = true;
	}

	/**
	 * @return the transactionOrder
	 */
	public int getTransactionOrder() {
		return transactionOrder;
	}

	/**
	 * @param transactionOrder
	 *            the transactionOrder to set
	 */
	public void setTransactionOrder(int transactionOrder) {
		this.transactionOrder = transactionOrder;
		this.modelChanged = true;
	}

	/**
	 * @return the batchType
	 */
	public BatchType getBatchType() {
		return batchType;
	}

	/**
	 * @param batchType
	 *            the batchType to set
	 */
	public void setBatchType(BatchType batchType) {
		if (batchType == null)
			return;
		this.batchType = batchType;
		this.modelChanged = true;
	}

	/**
	 * @param densityAmount
	 *            the densityAmount to set
	 */
	public void setDensityAmount(AmountModel density) {
		if (density != null) {
			if (density.getUnitType().getOrdinal() == UnitType.DENSITY.getOrdinal()) {
				// Check to see if it is a unit change
				if (!densityAmount.equals(density)) {
					densityAmount.deepCopy(density);
					updateCalcFlags(densityAmount);
					recalcAmounts();
					this.modelChanged = true;
				}
			}
		} else {
			densityAmount.setValue("0");
		}

	}

	/**
	 * @param loadingAmount
	 *            the loadingAmount to set
	 */
	public void setLoadingAmount(AmountModel loadingAmount) {
		if (loadingAmount != null) {
			if (!this.loadingAmount.equals(loadingAmount)) {
				this.modelChanged = true;
				this.loadingAmount.deepCopy(loadingAmount);
				recalcAmounts();
			}
		}
	}

	

	/**
	 * @param molarAmount
	 *            the molarAmount to set
	 */
	public void setMolarAmount(AmountModel molarAmnt) {
		if (molarAmnt != null) {
			if (molarAmnt.getUnitType().getOrdinal() == UnitType.MOLAR.getOrdinal()) {
				// Check to see if it is a unit change
				if (!molarAmount.equals(molarAmnt)) {
					boolean unitChange = BatchUtils.isUnitOnlyChanged(molarAmount, molarAmnt);
					molarAmount.deepCopy(molarAmnt);
					if (!unitChange) {
						recalcAmounts();
						setModified(true);
					}
				}
			}
		} else {
			molarAmount.setValue("0");
		}
	}

	/**
	 * @param moleAmount
	 *            the moleAmount to set
	 */
	public void setMoleAmount(AmountModel moles) {
		if (moles != null) {
			if (moles.getUnitType().getOrdinal() == UnitType.MOLES.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!moleAmount.equals(moles)) {
					unitChange = BatchUtils.isUnitOnlyChanged(moleAmount, moles);
					moleAmount.deepCopy(moles);
					if (!unitChange) {
						lastUpdatedType = UPDATE_TYPE_MOLES;
						updateCalcFlags(moleAmount);
						setModified(true);
					}
				}
				if (!unitChange)
					recalcAmounts();
			}
		} else {
			moleAmount.setValue("0");
		}
		setModelChanged(true);
	}

	/**
	 * @param molecularWeightAmount
	 *            the molecularWeightAmount to set
	 */
	public void setMolecularWeightAmount(AmountModel molecularWeight) {
		if (molecularWeightAmount != null) {
			if (!this.molecularWeightAmount.equals(molecularWeight)) {
				this.molecularWeightAmount.deepCopy(molecularWeight);
				setModified(true);
			}
		}
	}

	/**
	 * @param purityAmount
	 *            the purityAmount to set
	 */
	public void setPurityAmount(AmountModel purity) {
		if (purity != null) {
			if (!purityAmount.equals(purity)) {
				purityAmount.deepCopy(purity);
				recalcAmounts();
				setModified(true);
			}
		} else {
			purityAmount.setValue("100", true);
		}
		updateCalcFlags(purityAmount);
	}

	public void setPurity(double value) {

		if (value > 0.0 && value <= 100.0) {
			purityAmount.setValue(value, false);
		} else {
			purityAmount.setValue("100", true);
		}
		updateCalcFlags(purityAmount);
	}

	/**
	 * @param rxnEquivsAmount
	 *            the rxnEquivsAmount to set
	 */
	public void setRxnEquivsAmount(AmountModel equiv) {
		if (!rxnEquivsAmount.equals(equiv)) {
			rxnEquivsAmount.deepCopy(equiv);
			updateCalcFlags(rxnEquivsAmount);
			setModified(true);
		} else if (equiv == null) {
			this.rxnEquivsAmount.setValue("1.00");
		} else {
			// do nothing as no change is needed
		}

	}

	/**
	 * Side-Effect Warning: calling this method currently updates the molecularFormula attribute 
	 *                      also causes a recalculation of amounts.
	 *                      see updateMolecularFormula() for further side-effects.
	 * @param saltForm
	 *            the saltForm to set
	 */
	public void setSaltForm(SaltFormModel msaltForm) {
		if (msaltForm != null && !saltForm.equals(msaltForm)) {
			if (saltForm.getCode() == null || saltForm.isParentForm()) {
				// if the current saltForm is nothing and we are not
				// setting the saltFormCode to a valid salt we need to
				// make sure saltEquivs are minimally set to 1.
				if (saltEquivs == 0.0) {
					saltEquivs = 0.0;
					setSaltEquivsSet(false);
				}
			}
			saltForm.deepCopy(msaltForm);
		}

		// If we are unsetting salt form we need to make sure there are no saltEquivs
		// Same goes with setting saltForm to ParentStructure: "00"
		if (saltForm.isParentForm())
			saltEquivs = 0.0; // no salt form := make sure equivs are 0.0
		if (molecularWeightAmount.isCalculated() && isLoadingFromDB() == false && isBeingCloned() == false) {
			setMolWgtCalculated(getMolWgtCalculated());
		}
		recalcAmounts();
		// calc new batch MF;
		updateMolecularFormula();
		setModified(true);
	}

	/**
	 * @param volumeAmount
	 *            the volumeAmount to set
	 */
	public void setVolumeAmount(AmountModel volume) {
		if (volume != null) {
			if (volume.getUnitType().getOrdinal() == UnitType.VOLUME.getOrdinal()) {
				boolean unitChange = false;
				if (!volumeAmount.equals(volume)) {
					unitChange = BatchUtils.isUnitOnlyChanged(volumeAmount, volume);
					volumeAmount.deepCopy(volume);
					if (!unitChange) {
						lastUpdatedType = UPDATE_TYPE_VOLUME;
						updateCalcFlags(volumeAmount);
						setModified(true);
					}
				}
				if (!unitChange)
					recalcAmounts();
			}
		} else {
			this.volumeAmount.setValue("0");
		}
	}

	/**
	 * @param weightAmount
	 *            the weightAmount to set
	 */
	public void setWeightAmount(AmountModel weight) {
		if (weight != null) {
			if (weight.getUnitType().getOrdinal() == UnitType.MASS.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!weightAmount.equals(weight)) {
					unitChange = BatchUtils.isUnitOnlyChanged(weightAmount, weight);
					weightAmount.deepCopy(weight);
					if (!unitChange) {
						lastUpdatedType = UPDATE_TYPE_WEIGHT;
						updateCalcFlags(weightAmount);
						setModified(true);
					}
				}
				if (!unitChange)
					recalcAmounts();
			}
		} else {
			weightAmount.setValue("0");
		}
	}

	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append(CeNConstants.XML_VERSION_TAG);
		xmlbuff.append("<Batch_Properties>");
		xmlbuff.append("<Meta_Data>");
		xmlbuff.append("<Owner>" + this.owner + "</Owner>");
		xmlbuff.append("<Comments>");
		xmlbuff.append(this.getComments());
		xmlbuff.append("</Comments>");
		//In case of monomer batches this would be the vial barcode they were delviered.
		//This barcode is diffrent from the compoundManagement vial barcodes entered in registration tab
		xmlbuff.append("<Container_Barcode>" + this.barCode + "</Container_Barcode>");
		xmlbuff.append("<Stoich_Comments>");
		xmlbuff.append(this.getStoichComments());
		xmlbuff.append("</Stoich_Comments>");
		xmlbuff.append("<Stoic_Label>");
		xmlbuff.append(this.getStoicLabel());
		xmlbuff.append("</Stoic_Label>");
		// These tags will be closed in the models that extend this base class
		return xmlbuff.toString();
	}

	/**
	 * @return the listKey
	 */
	public String getListKey() {
		return this.listKey;
	}

	/**
	 * @param listKey
	 *            the listKey to set
	 */
	public void setListKey(String listKey) {
		this.listKey = listKey;
	}

	/**
	 * @return the position
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(String position) {
		this.position = position;
	}

	public BatchModel deepClone() {
		BatchModel batchmodel = new BatchModel(this.key);
		//no auto calc. just set the amounts being called
		batchmodel.setBeingCloned(true);
		if (this.compound.isModelChanged()) {
			batchmodel.setCompound((ParentCompoundModel) compound.deepClone());
		}
		batchmodel.setBatchNumber(this.batchNumber);
		batchmodel.setBatchType(this.batchType);
		batchmodel.setChloracnegenFlag(this.chloracnegenFlag);
		batchmodel.setChloracnegenType(this.chloracnegenType);
		batchmodel.setComments(this.comments);
		batchmodel.setConversationalBatchNumber(this.conversationalBatchNumber);
		batchmodel.setDensityAmount(this.densityAmount);
		batchmodel.setIntermediate(this.isIntermediate);
		batchmodel.setLimiting(this.limiting);
		batchmodel.setListKey(this.listKey);
		batchmodel.setLoadingAmount(this.loadingAmount);
		batchmodel.setMolarAmount(this.molarAmount);
		batchmodel.setMoleAmount(this.moleAmount);
		batchmodel.setMolecularFormula(this.molecularFormula);
		batchmodel.setMolecularWeightAmount(this.molecularWeightAmount);
		batchmodel.setOwner(this.owner);
		batchmodel.setParentBatchNumber(this.parentBatchNumber);
		batchmodel.setPosition(this.position);
		batchmodel.setStoicLabel(this.stoicLabel);
		batchmodel.setProjectTrackingCode(this.projectTrackingCode);
		batchmodel.setPurityAmount(this.purityAmount);
		batchmodel.setRxnEquivsAmount(this.rxnEquivsAmount);
		batchmodel.setSaltForm(this.saltForm);
		//first set saltform and then equivs.
		batchmodel.setStoichComments(this.stoichComments);
		batchmodel.setSaltEquivs(this.saltEquivs);
		batchmodel.setStepNumber(this.stepNumber);
		batchmodel.setSynthesizedBy(this.synthesizedBy);
		batchmodel.setTestedForChloracnegen(this.testedForChloracnegen);
		batchmodel.setTransactionOrder(this.transactionOrder);
		// this.setVendorInfo(vendorInfo);
		batchmodel.setVolumeAmount(this.volumeAmount);
		batchmodel.setWeightAmount(this.weightAmount);
		batchmodel.setSolventsAdded(this.solventsAdded);
		// this should be last statement since previous setters will mark modelChanged to true even though
		// the orginal model modelChanged is false;
		batchmodel.setModelChanged(this.modelChanged);
		batchmodel.setBeingCloned(false);
		return batchmodel;
	}

	/** 
	 * to be implemented by extending classes
	 * @param model
	 * @return
	 */
	public void deepCopy(BatchModel model) {
	}
	
	/**
	 * @return the solute
	 */
	public String getSolute() {
		return solute;
	}

	/**
	 * @param solute
	 *            the solute to set
	 */
	public void setSolute(String solute) {
		if (StringUtils.equals(this.solute, solute) == false) {
			this.solute = solute;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the soluteAmount
	 */
	public AmountModel getSoluteAmount() {
		return soluteAmount;
	}

	/**
	 * @param soluteAmount
	 *            the soluteAmount to set
	 */
	public void setSoluteAmount(AmountModel vsoluteAmount) {

		if (vsoluteAmount != null) {
			if (vsoluteAmount.getUnitType().getOrdinal() == UnitType.MASS.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!soluteAmount.equals(vsoluteAmount)) {
					unitChange = BatchUtils.isUnitOnlyChanged(soluteAmount, vsoluteAmount);
					soluteAmount.deepCopy(vsoluteAmount);
					if (!unitChange) {
						this.modelChanged = true;
					}
				}
			}
		} else {
			soluteAmount.setValue("0");
		}
	}

	public void clearData() {
		parentBatchNumber = "";
		conversationalBatchNumber = "";
		molecularFormula = "";

		molecularWeightAmount.reset();
		moleAmount.reset();
		weightAmount.reset();
		loadingAmount.reset();
		volumeAmount.reset();
		densityAmount.reset();
		molarAmount.reset();
		purityAmount.reset();
		rxnEquivsAmount.reset();

		saltForm.setCode("00");
		saltEquivs = 0;
		limiting = false;
		isIntermediate = false;
		autoCalcOn = true;
		comments = "";
		projectTrackingCode = "";
		precursors = new ArrayList<String>();
		inCalculation = false;
		transactionOrder = 0;
		testedForChloracnegen = false;
		chloracnegenFlag = false;
		chloracnegenType = "";
		position = "";
		soluteAmount = new AmountModel(UnitType.MASS);
		solute = "";
	}

	// For CompoundId
	public String getCompoundId() {
		return this.getCompound().getRegNumber();
	}

	public void setCompoundId(String compoundId) {
		this.getCompound().setRegNumber(compoundId);
		this.modelChanged = true;
	}

	/**
	 * @return the toBeDeleted
	 */
	public boolean isToBeDeleted() {
		return this.toBeDeleted;
	}

	/**
	 * @param deleteFlag
	 *            true would indicate, on next update This batch should be deleted from database
	 */
	public void markToBeDeleted(boolean deleteFlag) {
		this.toBeDeleted = deleteFlag;
	}

	public String getStoicBatchCASNumber() {
		return this.getCompound().getCASNumber();
	}

	public void setStoicBatchCASNumber(String casNumber) {
		if (casNumber == null)
			return;
		this.getCompound().setCASNumber(casNumber);
		this.modelChanged = true;
	}

	public Double getStoicBatchSaltEquivs() {
		return new Double(this.getSaltEquivs());
	}

	public void setStoicBatchSaltEquivs(double salteq) {
		this.setSaltEquivs(salteq);
	}

	public SaltFormModel getStoicBatchSaltForm() {
		return this.getSaltForm();
	}

	public void setStoicBatchSaltform(SaltFormModel salt) {
		if (salt == null)
			return;
		this.setSaltForm(salt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.domain.StoicModelInterface#getLabel()
	 */
	public String getStoicLabel() {
//		if (stoicLabel == null) {
//			return getPosition();
//		} else {
//			return stoicLabel;
//		}
		return stoicLabel;
	}

	public void setStoicLabel(String label) {
		this.stoicLabel = label;
		this.modelChanged = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.domain.StoicModelInterface#getReactionRole()
	 */
	public String getStoicReactionRole() {
		if (this.getBatchType() != null)
			return this.getBatchType().toString();
		else
			return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.domain.StoicModelInterface#isList()
	 */
	public boolean isList() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.domain.StoicModelInterface #setStoichReactionRole(java.lang.String)
	 */
	public void setStoicReactionRole(String rxnRole) {
		if (rxnRole == null)
			return;
		this.setBatchType(BatchType.getBatchType(rxnRole));
	}

	// For TotalVolume
	public AmountModel getTotalVolume() {
		return this.totalVolume;
	}

	public void setTotalVolume(AmountModel vtotalVolume) {
		if (vtotalVolume != null) {
			if (vtotalVolume.getUnitType().getOrdinal() == UnitType.VOLUME.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!totalVolume.equals(vtotalVolume)) {
					unitChange = BatchUtils.isUnitOnlyChanged(totalVolume, vtotalVolume);
					totalVolume.deepCopy(vtotalVolume);
					if (!unitChange) {
						this.modelChanged = true;
					}
				}
			}
		} else {
			totalVolume.setValue("0");
		}
	}

	// For BarCode
	public String getBarCode() {
		return this.barCode;
	}

	public void setBarCode(String barCode) {
		if (barCode == null)
			return;
		this.barCode = barCode;
		this.modelChanged = true;
	}

	// For ChemicalName
	public String getChemicalName() {
		return this.getCompound().getChemicalName();
	}

	public void setChemicalName(String chemName) {
		if (chemName == null)
			return;
		this.getCompound().setChemicalName(chemName);
		this.modelChanged = true;
	}

	public void setTotalWeight(AmountModel vtotalWeight) {
		if (vtotalWeight != null) {
			if (vtotalWeight.getUnitType().getOrdinal() == UnitType.MASS.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!totalWeight.equals(vtotalWeight)) {
					unitChange = BatchUtils.isUnitOnlyChanged(totalWeight, vtotalWeight);
					totalWeight.deepCopy(vtotalWeight);
					if (!unitChange) {
						setModified(true);
					}
				}
			}
		} else {
			totalWeight.setValue("0");
		}
	}

	public AmountModel getTotalWeight() {
		return totalWeight;
	}

	public String getBatchNumberAsString() {
		if (batchNumber == null)
			return "";
		else
			return this.batchNumber.getBatchNumber();
	}

	public boolean getSaltEquivsSet() {
		if (saltEquivs == 0.0) {
			return false;
		} else
			return true;
	}

	public String getGUIDKey() {
		return this.key;
	}

	private void updateCalcFlags(Amount2 changingAmount) {
		if (!changingAmount.isCalculated()) {
			if (changingAmount.equals(rxnEquivsAmount)) {
				moleAmount.setCalculated(true);
				if (!this.isLimiting())
					weightAmount.setCalculated(true);
				if (!volumeAmount.isCalculated() && isVolumeConnectedToMass())
					volumeAmount.setCalculated(true);
			} else if (changingAmount.equals(weightAmount) || changingAmount.equals(moleAmount)) {
				rxnEquivsAmount.setCalculated(true);
				if (!volumeAmount.isCalculated() && isVolumeConnectedToMass())
					volumeAmount.setCalculated(true);
			} else if (changingAmount.equals(volumeAmount) && isVolumeConnectedToMass()) {
				rxnEquivsAmount.setCalculated(true);
				weightAmount.setCalculated(true);
				moleAmount.setCalculated(true);
			}
			// applyLatestSigDigits(getSmallestSigFigs());
		}
	}

	protected int getCalcFlagMask() {
		int result = 0;
		if (moleAmount.isCalculated())
			result = (result & CALC_MOLES);
		if (weightAmount.isCalculated())
			result = (result & CALC_WEIGHT);
		if (volumeAmount.isCalculated())
			result = (result & CALC_VOLUME);
		if (rxnEquivsAmount.isCalculated())
			result = (result & CALC_RXN_EQUIVS);
		if (molecularWeightAmount.isCalculated())
			result = (result & CALC_MOL_WGT);
		if (densityAmount.isCalculated())
			result = (result & CALC_MOLARITY);
		if (molarAmount.isCalculated())
			result = (result & CALC_MOLARITY);
		return result;
	}

	private void updateWeightFromMoles() {
		weightAmount.setCalculated(true);
		List<AmountModel> amts = new ArrayList<AmountModel>();
		// weight = weight/mole * moles
		// In this case moles are always mMoles and the default return of mole amount is in mmoles
		// Hence when we setValue for weight we are doing so in mg and no conversion is necessary.
		double result = getMolWgt() * getMoleAmount().GetValueInStdUnitsAsDouble();
		if (loadingAmount.doubleValue() > 0.0) {
			// calc by loadingAmount mmol/g - doesn't use molWgt.
			// mg = 1000 mg/g * mmol/(mmol/g)
			result = (1000 * getMoleAmount().GetValueInStdUnitsAsDouble() / loadingAmount.GetValueInStdUnitsAsDouble());
			amts.add(loadingAmount);
			amts.add(moleAmount);
		} else if (getPurityAmount().doubleValue() < 100d && getPurityAmount().doubleValue() > 0.0) {
			result = result / (getPurityAmount().doubleValue() / 100);
			amts.add(moleAmount);
			amts.add(purityAmount);
			amts.add(molecularWeightAmount);
		} else {
			amts.add(moleAmount);
			amts.add(molecularWeightAmount);
		}
		// Applies SignificantFigures to weightAmount
		applySigFigRules(weightAmount, amts);
		amts.clear();

		// We just got the result for mg * purity),
		// Now we need to make sure the answer makes sense for the units of weight currently being used.
		weightAmount.SetValueInStdUnits(result, true);
	}

	private void updateMolesFromWeight() {
		double result = 0d;
		List<AmountModel> amts = new ArrayList<AmountModel>();
		// weight std unit = mg; density std unit = mg/mmol
		// Weight = 9.82m/s^2 * mass
		if (loadingAmount.doubleValue() > 0.0) {
			// mmoles = (mmol/g) * (g/1000mg) * (weightAmount in std units = mg)
			result = loadingAmount.GetValueInStdUnitsAsDouble() * (weightAmount.GetValueInStdUnitsAsDouble() / 1000);
			amts.add(loadingAmount);
			amts.add(weightAmount);
		} else if (getMolWgt() > 0.0) {
			// TODO: Warning. Using getMolWgt() in the future may not return
			// standard units. Make sure this changes if the
			// users ever get the opportunity to change units of
			// MolWt.
			result = weightAmount.GetValueInStdUnitsAsDouble() / getMolWgt();
			amts.add(weightAmount);
			amts.add(molecularWeightAmount);
			// we just got the result for mg/mmole,
			// now we need to make sure the answer makes sense for the units of
			// weight currently being used.
			if (getPurityAmount().doubleValue() < 100d && purityAmount.doubleValue() > 0.0) {
				result = result * (purityAmount.doubleValue() / 100);
				amts.add(purityAmount);
			}
		}
		applySigFigRules(moleAmount, amts);
		amts.clear();
		moleAmount.SetValueInStdUnits(result, true);
	}

	public void recalcAmounts() {
		// Make sure we don't get into a loop!
		// And that we don't lose information on load.
		if (!autoCalcOn || inCalculation || isLoadingFromDB() || isBeingCloned())
			return;

		//if This batch is a SOLVENT, then do not recalculate its amounts.
	    if(getBatchType() == null || getBatchType().equals(BatchType.SOLVENT))
	    	return;
	    
		List<AmountModel> amts = new ArrayList<AmountModel>();
		inCalculation = true;
		// Check which value was set by hand: solid or liquid
		// Molar type as last updated not considered here because moles is considered driver
		// when there is a tie in flags.
		if (lastUpdatedType == UPDATE_TYPE_VOLUME && !volumeAmount.isCalculated()) {
			// We need to update moles and weight from volume
			// Molarity takes precedence over density
			if (molarAmount.doubleValue() > 0) {
				amts.add(molarAmount);
				amts.add(volumeAmount);
				applySigFigRules(moleAmount, amts);
				amts.clear(); // important to clear the amts list
				// Update mole amount
				// Std unit for molar is mMolar
				//
				// mMoles = (mole/L) * mL
				moleAmount.SetValueInStdUnits(molarAmount.GetValueInStdUnitsAsDouble() * volumeAmount.GetValueInStdUnitsAsDouble(),
						true);
				updateWeightFromMoles();
			} else if (densityAmount.doubleValue() > 0) {
				// find governing sig figs
				amts.add(volumeAmount);
				amts.add(densityAmount);
				applySigFigRules(weightAmount, amts);
				amts.clear();// important to clear the amts list
				// mg = (mL * g/mL)/ (1000 mg/g)
				weightAmount.SetValueInStdUnits(1000 * volumeAmount.GetValueInStdUnitsAsDouble()
						* densityAmount.GetValueInStdUnitsAsDouble(), true);
				updateMolesFromWeight();
			}
			// SourceForge 3.4 bug 28030 - remove data not connected
			if (!isVolumeConnectedToMass()) {
				// set weight, moles and rxn equivs to default values.
				weightAmount.reset();
				moleAmount.reset();
				rxnEquivsAmount.reset();
			}
		} else {
			if ((lastUpdatedType != UPDATE_TYPE_WEIGHT && !moleAmount.isCalculated()) || !rxnEquivsAmount.isCalculated()
					|| moleAmount.doubleValue() > 0 && lastUpdatedType != UPDATE_TYPE_WEIGHT) {
				updateWeightFromMoles();
			} else {
				updateMolesFromWeight();
			}
			// Now that the solids are straightened out, we can calc the liquid
			if (molarAmount.doubleValue() > 0) {
				// Due to artf60208 :Singleton: correct sig fig processing for Molarity in Stoich Table (CEN-705) comment sig fig calculation
				//				// update volume
				//				amts.add(moleAmount);
				//				amts.add(molarAmount);
				//				if (volumeAmount.isCalculated() && weightAmount.isCalculated())
				//					volumeAmount.setSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
				//				else
				//					applySigFigRules(volumeAmount, amts);

				//				amts.clear();// important to clear the amts list
				volumeAmount.setSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
				volumeAmount.SetValueInStdUnits(moleAmount.GetValueInStdUnitsAsDouble() / molarAmount.GetValueInStdUnitsAsDouble(),
						true);
			} else if (densityAmount.doubleValue() > 0) {
				// Due to artf60208 :Singleton: correct sig fig processing for Molarity in Stoich Table (CEN-705) comment sig fig calculation
				//				amts.add(weightAmount);
				//				amts.add(densityAmount);
				//				applySigFigRules(volumeAmount, amts);
				//				amts.clear();// important to clear the amts list

				volumeAmount.setSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
				// update volume from weight value: mg /(1000mg/g)* (g/mL) = mL
				volumeAmount.SetValueInStdUnits(weightAmount.GetValueInStdUnitsAsDouble()
						/ (1000 * densityAmount.GetValueInStdUnitsAsDouble()), true);
			}
			if (!isVolumeConnectedToMass()) {
				// set weight, moles and rxn equivs to default values.
				volumeAmount.softReset();
			}
		}
		inCalculation = false;
	}

	public boolean equals(Object o) {
		boolean result = false;
		if (o != null && o instanceof BatchModel) {
			BatchModel ab = (BatchModel) o;
			if (this.getKey().equals(ab.getKey()))
				result = true;
		}
		return result;
	}

	// Before playing here familiarize yourself with the
	// layout rules for stoichiometry.
	public int compareTo(BatchModel ab) {
		int result = 0;
		if (ab != null) {
			result = this.getBatchType().compareTo(ab.getBatchType());
			// Precedence should be batchNumber if Product otherwise (Transaction Step Number) for now: Compound Number then batch
			// then molFormula
			if (result == 0) {
				if (getBatchNumber() != null)
					if (ab.getBatchNumber() != null)
						result = (getBatchNumber().compareTo(ab.getBatchNumber()));
					else
						result = 1;
				else if (getConversationalBatchNumber() != null && !getConversationalBatchNumber().equals(""))
					if (ab.getConversationalBatchNumber() != null && !ab.getConversationalBatchNumber().equals(""))
						result = (getConversationalBatchNumber().compareTo(ab.getConversationalBatchNumber()));
					else
						result = 1;
				else if (getCompound() != null)
					if (ab.getCompound() != null)
						result = (getCompound().compareTo(ab.getCompound()));
					else
						result = 1;
			}
		}
		return result;
	}

	public boolean isVolumeConnectedToMass() {
		return (densityAmount.doubleValue() > 0 || molarAmount.doubleValue() > 0);
	}

	/**
	 * Amounts that are possibly being loaded from external sources.
	 * 
	 * @return
	 */
	protected List<AmountModel> getLoadedAmounts() {
		List<AmountModel> result = new ArrayList<AmountModel>();
		result.add(getMolecularWeightAmount()); // Lookup or calc'd. Don't play with sig digits.
		result.add(getLoadingAmount()); // My Reagents - treat as user set sig digits
		result.add(getDensityAmount()); // My Reagents - treat as user set sig digits
		result.add(getPurityAmount()); // My Reagents - treat as user set sig digits
		result.add(getMolarAmount()); // My Reagents - treat as user set sig digits
		return result;
	}

	/**
	 * Amounts that are all interrelated regarding batch calculations. SigDigits apply.
	 * 
	 * @return
	 */
	protected List<AmountModel> getCalculatedAmounts() {
		List<AmountModel> result = new ArrayList<AmountModel>();
		result.add(getMoleAmount());
		result.add(getWeightAmount());
		result.add(getVolumeAmount());
		result.add(getRxnEquivsAmount()); // Use this to set initial sigdigits = 3 by setting to 1.00 as default.
		return result;
	}

	/**
	 * 
	 * @return List of amount objects that are measured values
	 */
	protected List<AmountModel> getMeasuredAmounts() {
		List<AmountModel> result = new ArrayList<AmountModel>();
		result.add(getWeightAmount());
		result.add(getVolumeAmount());
		return result;
	}

	/**
	 * 
	 * @param amt
	 * @return true if it is a Loaded amount false otherwise
	 */
	protected boolean isLoadedAmount(Amount2 amt) {
		return getLoadedAmounts().contains(amt);
	}

	/**
	 * 
	 * @param amt
	 * @return true if it is a Calculated amount false otherwise
	 */
	protected boolean isCalculatedAmount(Amount2 amt) {
		return getCalculatedAmounts().contains(amt);
	}

	/**
	 * 
	 * @param amt
	 * @return true if it is a Measured amount false otherwise
	 */
	protected boolean isMeasuredAmount(Amount2 amt) {
		return getMeasuredAmounts().contains(amt);
	}

	/**
	 * Do we apply sig fig rules to calculations?
	 * 
	 */
	public boolean shouldApplySigFigRules() {
		return (!weightAmount.isCalculated() || !volumeAmount.isCalculated());
	}

	/**
	 * 
	 */
	public boolean shouldApplyDefaultSigFigs() {
		return (!this.rxnEquivsAmount.isCalculated() || !this.moleAmount.isCalculated() || !this.molarAmount.isCalculated());
	}

	/**
	 * For all calculated amounts find those that are calculated and set default number of significant digits to the value passed.
	 * 
	 * @param defaultSigs
	 */
	public void applyLatestSigDigits(int defaultSigs) {
		List<AmountModel> amts = getCalculatedAmounts();
		for (AmountModel amt : amts) {
			if (amt.isCalculated()) {
				amt.setSigDigits(defaultSigs);
			}
		}
	}

	/**
	 * Note: Avoid Side-effects.  If your intention is to trigger a new value for MolecularWeight, please set it explicitly.
	 * Prefer accessing the actual AmountModel object.doubleValue() to calling this method unless your intention is to recalculate.
	 * 
	 * @return Returns molecularWeightAmount.doubleValue() or the calculated amount if the double Value = the defaultValue of Zero
	 */
	public double getMolWgt() {
		 double result = molecularWeightAmount.doubleValue();
		 // return 0 if no compound weight has been set -or- return weight set by user
		 if (molecularWeightAmount.isValueDefault() && molecularWeightAmount.isCalculated()) {
			 result = getMolWgtCalculated();
		 }
		 return result;
	}

	/** 
	 * 
	 * @return weight based on molecularWeight of the compound + (Number of Salt Equivalents * Salt MW)
	 */
	public double getMolWgtCalculated() {
		// Significant Figures for calculated molecular weight was deemed to be ignored.
		// Scientists want MW calculated out to the third place behind the decimal or 3 fixed figures if they are available.
		// Do not enforce sig figs here.
		// Don't return more than three places after decimal point
		double result = 0.0;
		if (compound.getMolWgt() > 0.0) {
			result = compound.getMolWgt() + (getSaltForm().getMolWgt() * getSaltEquivs());
			String test = Double.toString(result);
			if (test.indexOf(".") > 0)
				if (test.substring(test.indexOf(".") + 1, test.length()).length() > 3)
					result = new BigDecimal(result).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		return result;
	}

	public void setMolWgtCalculated(double value) {
		SignificantFigures sigs = new SignificantFigures(value);
		// Only set if sigfigs are greater than zero.
		if (sigs.getNumberSignificantFigures() > 0)
			molecularWeightAmount.setSigDigits(sigs.getNumberSignificantFigures());
		molecularWeightAmount.setValue(sigs.doubleValue());
	}

	// All stoic interface methods. these methods are delegated to internal getter/setters

	public void setStoicMoleAmount(AmountModel molAmt) {
		this.setMoleAmount(molAmt);
	}

	public AmountModel getStoicMoleAmount() {
		return this.getMoleAmount();
	}

	public void setStoicRxnEquivsAmount(AmountModel rxnEquivAmt) {
		this.setRxnEquivsAmount(rxnEquivAmt);
	}

	public AmountModel getStoicRxnEquivsAmount() {
		return this.getRxnEquivsAmount();
	}

	public void setStoicLimiting(boolean isLimiting) {
		this.setLimiting(isLimiting);
	}

	public boolean isStoicLimiting() {
		return this.isLimiting();
	}

	public void setStoicMolarAmount(AmountModel molarAmt) {
		this.setMolarAmount(molarAmt);
	}

	public AmountModel getStoicMolarAmount() {
		return this.getMolarAmount();
	}

	public void setStoicSoluteAmount(AmountModel soluteAmt) {
		this.setSoluteAmount(soluteAmt);
	}

	public AmountModel getStoicSoluteAmount() {
		return this.getSoluteAmount();
	}

	public void setStoicSolute(String solute) {
		this.setSolute(solute);
	}

	public String getStoicSolute() {
		return this.getSolute();
	}

	public String getStoicChemicalName() {
		return this.getChemicalName();
	}

	public void setStoicChemicalName(String chemName) {
		this.setChemicalName(chemName);
	}

	public String getStoicMolecularFormula() {
		return this.getMolecularFormula();
	}

	public void setStoicMolecularFormula(String molFormula) {
		this.setMolecularFormula(molFormula);
	}

	public String getStoicCompoundId() {
		return this.getCompoundId();
	}

	public void setStoicCompoundId(String compoundId) {
		this.setCompoundId(compoundId);
	}

	public BatchNumber getStoicBatchNumber() {
		return this.getBatchNumber();
	}

	public void setStoicBatchNumber(BatchNumber nbkBatchNo) {
		this.setBatchNumber(nbkBatchNo);
	}

	public AmountModel getStoicMolecularWeightAmount() {
		return this.getMolecularWeightAmount();
	}

	public void setStoicMolecularWeightAmount(AmountModel molWeight) {
		this.setMolecularWeightAmount(molWeight);
	}

	public AmountModel getStoicWeightAmount() {
		return this.getWeightAmount();
	}

	public void setStoicWeightAmount(AmountModel weight) {
		this.setWeightAmount(weight);
	}

	public AmountModel getStoicDensityAmount() {
		return this.getDensityAmount();
	}

	public void setStoicDensityAmount(AmountModel density) {
		this.setDensityAmount(density);
	}

	public AmountModel getStoicVolumeAmount() {
		return this.getVolumeAmount();
	}

	public void setStoicVolumeAmount(AmountModel volume) {
		this.setVolumeAmount(volume);
	}

	public String getStoicHazardsComments() {
		return this.getHazardComments();
	}

	public void setStoicHazardsComments(String hazards) {
		this.setHazardComments(hazards);
	}

	public int getStoicTransactionOrder() {
		return this.getTransactionOrder();
	}

	public void setStoicTransactionOrder(int transactionOrder) {
		this.setTransactionOrder(transactionOrder);
		
	}

	public void clearStoicData() {
		// Need to save TransactionOrder to not lost order of batches in stoic table
		int transOrder = getStoicTransactionOrder();
		
		clearData();
		
		setStoicTransactionOrder(transOrder);
		
		compound = new ParentCompoundModel();
		
		setStoicLabel("");
		setStoicChemicalName("");
		setStoicCompoundId("");
		setStoicMolecularFormula("");
		setStoicReactionRole("");
		setStoicHazardsComments("");
		setStoichComments("");
		setStoicBatchCASNumber("");
		setStoicSolventsAdded("");
		
		try {
			setStoicBatchNumber("");
		} catch (InvalidBatchNumberException e) {
			// Do nothing
		}
		
		getStoicMolecularWeightAmount().reset();
		getStoicMoleAmount().reset();
		getStoicDensityAmount().reset();
		getStoicRxnEquivsAmount().reset();
		getStoicWeightAmount().reset();
		getStoicPurityAmount().reset();
		getStoicMolarAmount().reset();
		getStoicLoadingAmount().reset();
		getStoicVolumeAmount().reset();
		
		getStoicBatchSaltForm().setCode("00");
		setStoicBatchSaltEquivs(0);
				
		setStoicLimiting(false);
		setModified(true);
	}

	public AmountModel getStoicPurityAmount() {
		return this.getPurityAmount();
	}

	public void setStoicPurityAmount(AmountModel purityModel) {
		this.setPurityAmount(purityModel);
	}

	public AmountModel getStoicLoadingAmount() {
		return this.getLoadingAmount();
	}

	public void setStoicLoadingAmount(AmountModel loadingModel) {
		this.setLoadingAmount(loadingModel);
	}

	public void setPreviousMolarAmount(AmountModel vpreMolarAmount) {
		if (vpreMolarAmount != null) {
			if (vpreMolarAmount.getUnitType().getOrdinal() == UnitType.MOLAR.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!previousMolarAmount.equals(vpreMolarAmount)) {
					unitChange = BatchUtils.isUnitOnlyChanged(previousMolarAmount, vpreMolarAmount);
					previousMolarAmount.deepCopy(vpreMolarAmount);
					if (!unitChange) {
						setModified(true);
					}
				}
			}
		} else {
			previousMolarAmount.setValue("0");
		}
	}

	public AmountModel getPreviousMolarAmount() {
		return previousMolarAmount;
	}

	public void applySigFigRules(AmountModel amt, List<AmountModel> amts) {
		if (shouldApplySigFigRules()) {
			amt.setSigDigits(CeNNumberUtils.getSmallestSigFigsFromAmountModelList(amts));
		} else {
			if (shouldApplyDefaultSigFigs())
				amt.setSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
		}
	}

	public void setSaltEquivsSet(boolean equivsSet) {
		boolean change = (saltEquivsSet != equivsSet);
		saltEquivsSet = equivsSet;
		if (change)
			setSaltEquivs(saltEquivs);
	}

	public boolean isChloracnegen() {
		return chloracnegenFlag;
	}

	// empty Impl. ProdBAtchModel will override with correct impl
	public AmountModel getTheoreticalWeightAmount() {
		return null;
	}

	public void setTheoreticalWeightAmount(AmountModel theoreticalWeightAmount) {

	}

	public AmountModel getTheoreticalMoleAmount() {
		return null;
	}

	public void setTheoreticalMoleAmount(AmountModel theoreticalMoleAmount) {

	}

	public String getSolventsAdded() {
		return solventsAdded;
	}

	public void setSolventsAdded(String solventsAdded) {
		this.solventsAdded = solventsAdded;
		this.modelChanged = true;
	}

	public String getStoicSolventsAdded() {
		return this.getSolventsAdded();
	}

	public void setStoicSolventsAdded(String vsolventsAdded) {
		this.setSolventsAdded(vsolventsAdded);
	}

	public AmountModel getTotalMolarity() {
		return totalMolarity;
	}

	public void setTotalMolarity(AmountModel vtotalMolarAmount) {
		if (vtotalMolarAmount != null) {
			if (vtotalMolarAmount.getUnitType().getOrdinal() == UnitType.MOLAR.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!totalMolarity.equals(vtotalMolarAmount)) {
					unitChange = BatchUtils.isUnitOnlyChanged(totalMolarity, vtotalMolarAmount);
					totalMolarity.deepCopy(vtotalMolarAmount);
					if (!unitChange) {
						setModified(true);
					}
				}
			}
		} else {
			totalMolarity.setValue("0");
		}
	}

	/**
	 * @param molarAmount
	 *            the molarAmount to set as part of BatchesList update
	 */
	public void setListMolarAmount(AmountModel molarAmnt) {
		if (molarAmnt != null) {
			if (molarAmnt.getUnitType().getOrdinal() == UnitType.MOLAR.getOrdinal()) {
				// Check to see if it is a unit change
				if (!molarAmount.equals(molarAmnt)) {
					boolean unitChange = BatchUtils.isUnitOnlyChanged(molarAmount, molarAmnt);
					molarAmount.deepCopyWithoutKeys(molarAmnt);
					if (!unitChange) {
						recalcAmounts();
						setModified(true);
					}
				}
			}
		} else {
			molarAmount.setValue("0");
		}
	}

	public void setListPurityAmount(AmountModel purity) {
		if (purity != null) {
			if (!purityAmount.equals(purity)) {
				purityAmount.deepCopyWithoutKeys(purity);
				recalcAmounts();
				setModified(true);
			}
		} else {
			purityAmount.setValue("100", true);
		}
		updateCalcFlags(purityAmount);
	}

	public void setListMoleAmount(AmountModel moles) {
		if (moles != null) {
			if (moles.getUnitType().getOrdinal() == UnitType.MOLES.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!moleAmount.equals(moles)) {
					unitChange = BatchUtils.isUnitOnlyChanged(moleAmount, moles);
					moleAmount.deepCopyWithoutKeys(moles);
					if (!unitChange) {
						lastUpdatedType = UPDATE_TYPE_MOLES;
						updateCalcFlags(moleAmount);
						setModified(true);
					}
				}
				if (!unitChange)
					recalcAmounts();
			}
		} else {
			moleAmount.setValue("0");
		}
	}

	public void setListRxnEquivsAmount(AmountModel equiv) {
		if (!rxnEquivsAmount.equals(equiv)) {
			rxnEquivsAmount.deepCopyWithoutKeys(equiv);
			updateCalcFlags(rxnEquivsAmount);
			setModified(true);
		} else {
			this.rxnEquivsAmount.setValue("1.00");
		}

	}

	public void setListVolumeAmount(AmountModel volume) {
		if (volume != null) {
			if (volume.getUnitType().getOrdinal() == UnitType.VOLUME.getOrdinal()) {
				boolean unitChange = false;
				if (!volumeAmount.equals(volume)) {
					unitChange = BatchUtils.isUnitOnlyChanged(volumeAmount, volume);
					volumeAmount.deepCopyWithoutKeys(volume);
					if (!unitChange) {
						lastUpdatedType = UPDATE_TYPE_VOLUME;
						updateCalcFlags(volumeAmount);
						setModified(true);
					}
				}
				if (!unitChange)
					recalcAmounts();
			}
		} else {
			this.volumeAmount.setValue("0");
		}
	}

	/**
	 * @param weightAmount
	 *            the weightAmount to set
	 */
	public void setListWeightAmount(AmountModel weight) {
		if (weight != null) {
			if (weight.getUnitType().getOrdinal() == UnitType.MASS.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!weightAmount.equals(weight)) {
					unitChange = BatchUtils.isUnitOnlyChanged(weightAmount, weight);
					weightAmount.deepCopyWithoutKeys(weight);
					if (!unitChange) {
						lastUpdatedType = UPDATE_TYPE_WEIGHT;
						updateCalcFlags(weightAmount);
						setModified(true);
					}
				}
				if (!unitChange)
					recalcAmounts();
			}
		} else {
			weightAmount.setValue("0");
		}
	}

	public void setListDensityAmount(AmountModel density) {
		if (density != null) {
			if (density.getUnitType().getOrdinal() == UnitType.DENSITY.getOrdinal()) {
				// Check to see if it is a unit change
				if (!densityAmount.equals(density)) {
					densityAmount.deepCopyWithoutKeys(density);
					updateCalcFlags(densityAmount);
					recalcAmounts();
					setModified(true);
				}
			}
		} else {
			densityAmount.setValue("0");
		}

	}

	public void setListLoadingAmount(AmountModel loadingAmount) {
		if (loadingAmount != null) {
			if (!this.loadingAmount.equals(loadingAmount)) {
				this.modelChanged = true;
				this.loadingAmount.deepCopyWithoutKeys(loadingAmount);
			}
		}
	}

	/**
	 * Works the same as setMolecularWeightAmount() except it allows you to set a double in the amount field. If you intend to have
	 * this show up as user input, use:
	 * 
	 * setMolWgt(double, boolean)
	 * 
	 * @param molWgt
	 *            The molWgt to set.
	 */
	public void setMolWgt(double molWgt) {
		setMolWgt(molWgt, molecularWeightAmount.isCalculated());
	}

	/**
	 * Use this method if you want to control the isCalculated flag for the amount.
	 * 
	 * @param molWgt
	 * @param isCalculated -
	 *            True will allow this value to change in the future.
	 */
	public void setMolWgt(double molWgt, boolean isCalculated) {
		SignificantFigures sigs = new SignificantFigures(molWgt);
		if (sigs.getNumberSignificantFigures() > 0)
			molecularWeightAmount.setSigDigits(sigs.getNumberSignificantFigures());
		molecularWeightAmount.setValue(molWgt, isCalculated);
		recalcAmounts();
		setModified(true); // Modified first allows previous lastUpdatedType to remain unchanged.
	}

	public void setBatchNumber(String bn) throws InvalidBatchNumberException {
		if (bn == null || bn.equals("")) {
			this.batchNumber = new BatchNumber();
		} else {
			this.batchNumber = new BatchNumber(bn);
		}
		this.modelChanged = true;
	}
	
	public String getStoicBatchNumberAsString()
	{
		return (this.batchNumber == null) ? "" : this.batchNumber.toString();
	}
	
	public void setStoicBatchNumber(String nbkBatchNo) throws InvalidBatchNumberException
	{
		if (StringUtils.isBlank(nbkBatchNo)) {
			this.batchNumber = new BatchNumber();
		} else {
			this.batchNumber = new BatchNumber();
			this.batchNumber.setBatchNumberWithoutLotNumberPadding(nbkBatchNo);
		}
		this.modelChanged = true;		
	}

	public int getStoicListSize() {
		return 1;
	}
	
	public void resetAmount(String amountName)
	{
		if (amountName.equals(CeNConstants.MOLE_AMOUNT)) {
			this.getMoleAmount().reset();
		} else if (amountName.equals(CeNConstants.RXN_EQUIVS_AMOUNT)) {
// ????
		}
	}
	
	/**
	 * @return Returns the Hazard comment.
	 */
	public String getHazardComments() {
		return compound == null ? "" : compound.getHazardComments();
	}

	/**
	 * @param comment
	 *            The Hazard comment to set.
	 */
	public void setHazardComments(String hazardComments) {
		if (compound != null) {
			compound.setHazardComments(hazardComments);
			this.modelChanged = true;
		}
	}

	public ParentCompoundModel getStoichCompoundModel() {
		return compound;
	}

	public String getStoichComments() {
		return stoichComments;
	}

	public void setStoichComments(String comments) {
		stoichComments = (StringUtils.isBlank(comments)) ? "": comments;
		this.modelChanged = true;
	}

	/**
	 * @return Returns the originalBatchNumber.
	 */
	public String getOriginalBatchNumber() {
		return (originalBatchNumber == null) ? "" : originalBatchNumber;
	}

	/**
	 * @param originalBN
	 *            The original Batch Number to set.
	 */
	public void setOriginalBatchNumber(String originalBN) {
		if (!StringUtils.equals(originalBatchNumber, originalBN)) {
			originalBatchNumber = originalBN;
			setModified(true);
		}
	}
	
	public String toString() {
		return getBatchNumberAsString() + ":" + batchType;
	}
}

