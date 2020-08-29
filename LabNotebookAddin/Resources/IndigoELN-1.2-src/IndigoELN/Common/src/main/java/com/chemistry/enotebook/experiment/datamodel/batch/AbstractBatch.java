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
 * IBatch.java
 * 
 * Created on Aug 17, 2004
 *
 * 
 */
package com.chemistry.enotebook.experiment.datamodel.batch;

import com.chemistry.enotebook.experiment.common.ExternalSupplier;
import com.chemistry.enotebook.experiment.common.PersistableObject;
import com.chemistry.enotebook.experiment.common.SaltForm;
import com.chemistry.enotebook.experiment.common.TemperatureRange;
import com.chemistry.enotebook.experiment.common.interfaces.AutoCalc;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;
import com.chemistry.enotebook.experiment.datamodel.common.SignificantFigures;
import com.chemistry.enotebook.experiment.datamodel.compound.Compound;
import com.chemistry.enotebook.experiment.utils.BatchUtils;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 
 * @date Aug 17, 2004
 */
public abstract class AbstractBatch extends PersistableObject implements IBatch, Comparable, Observer, AutoCalc, DeepCopy,
		DeepClone {
	
	private static final long serialVersionUID = 1332196278621735475L;

	private static final double INVALID_VALUE = -1;

	public static final int UPDATE_TYPE_MOLES = 0;
	public static final int UPDATE_TYPE_WEIGHT = 1;
	public static final int UPDATE_TYPE_VOLUME = 2;

	protected static final int CALC_MOLES = 1;
	protected static final int CALC_WEIGHT = 2;
	protected static final int CALC_VOLUME = 4;
	protected static final int CALC_RXN_EQUIVS = 8;
	protected static final int CALC_MOL_WGT = 16;
	protected static final int CALC_DENSITY = 32;
	protected static final int CALC_MOLARITY = 64;
	protected static final int CALC_LOADING = 128;

	// holder of the batch properties. Alternative to static declaration of all of them
	private HashMap batchProperties = new HashMap(100);

	// used to dynamically set the column names when used in a table
	private String[] propertyNames;

	private Compound compound; // Holds structure molFormula wgt and other info.
	private BatchNumber batchNumber = new BatchNumber();
	private String originalBatchNumber; // No checking happens on this as there might be external values.
	private String parentBatchNumber; // No checking happens on this as there might be external values.
	private String conversationalBatchNumber;
	private String molecularFormula; // If exists it overrides formula calc.

	private final Amount molecularWeightAmount = new Amount(UnitType.SCALAR); // Holds batch molecular weight
	private final Amount moleAmount = new Amount(UnitType.MOLES); // Unitless amount indicating how much of an Avagadro's number
	// of molecules we have
	private final Amount weightAmount = new Amount(UnitType.MASS); // Amount will contain unit conversions original amount and
	// original units
	private final Amount loadingAmount = new Amount(UnitType.LOADING); // Loading is generally mmol/gram - tackles resins
	private final Amount volumeAmount = new Amount(UnitType.VOLUME); // Amount in volume
	private final Amount densityAmount = new Amount(UnitType.DENSITY); // Density of compound in g/mL
	private final Amount molarAmount = new Amount(UnitType.MOLAR); // Describes concentration of batch
	private final Amount purityAmount = new Amount(UnitType.SCALAR, "100"); // % Purity info 100 - 0
	private final Amount rxnEquivsAmount = new Amount(UnitType.SCALAR); // Represents equivalents of compound to a limiting reagent
	// in the reaction
	private final TemperatureRange meltPointRange = new TemperatureRange(); // Used for BatchInfo
	// private Amount boilingPt = new Amount(UnitType.TEMP); // Stores boiling point in temp units

	private final Amount previousMolarAmount = new Amount(UnitType.MOLAR); // Describes concentration of batch before updating with
	// latest Amount

	private final SaltForm saltForm = new SaltForm("00"); // Must be from a vetted list
	private double saltEquivs;
	private boolean saltEquivsSet = true;										// By default for back-ward compatibility this is true all new batches

	protected boolean limiting = false;
	protected boolean isIntermediate = false;
	protected boolean autoCalcOn = true;

	private String owner; // NTID of person who sponsored the creation of this batch
	private String synthesizedBy; // NTID of chemist who synthesized the compound
	private String compoundState; // String will be provided from dropdown list
	private String protectionCode; // Protection Code used to determine orderability
	private String regStatus; // Registration Status of this batch
	private String comments; // Batch Comments
	private String storageComments; // Batch Storage Comments
	private String hazardComments; // Batch Hazard Comments
	private String handlingComments;// Batch handling comments in case there is a special need
	private ExternalSupplier vendorInfo;
	private String projectTrackingCode;
	private ArrayList<String> precursors = new ArrayList<String>(); // holds compound ids that were used to create this batch.

	protected boolean inCalculation = false; // Do not disturb. Batch is in process of calculating values

	private int transactionOrder = 0;

	// uses constants from above
	private int lastUpdatedType = UPDATE_TYPE_MOLES;

	private boolean testedForChloracnegen = false; // Flag to check if batch has been tested for Chloracnegen
	private boolean chloracnegenFlag = false; // Flag to identify if this batch is a chloracnegen hit after running structure
	// through CCT
	private String chloracnegenType = ""; // Type info like Class 1 ,Class 2 etc
//	private int stepNumber; // step this batch belongs to in PMC
//	private String planId; // Plan instance Id this batch belongs to in PMC

	/*
	 * Constructors
	 */
	public AbstractBatch() {
		this("");
	}
	
	public AbstractBatch(String key){
		this(key,null);
	}

	public AbstractBatch(String batchKey, BatchNumber batchNumber) {
		this(batchKey, batchNumber, "", "", 0.0, null, null, null, null, null, 0.0);
	}

	public AbstractBatch(String batchKey, BatchNumber batchNumber, String parentBatchNumber, String saltFormCode,
			double saltEquivs, Compound compound, Amount moles, Amount weight, Amount volume) {
		this(batchKey, batchNumber, parentBatchNumber, saltFormCode, saltEquivs, compound, moles, weight, volume, null, 100.0);
	}

	public AbstractBatch(String batchKey, BatchNumber batchNumber, String parentBatchNumber, String saltFormCode,
			double saltEquivs, Compound compound, Amount moles, Amount weight, Amount volume, Amount density, double purity) {
		this(batchKey, batchNumber, parentBatchNumber, saltFormCode, saltEquivs, compound, moles, weight, volume, density, purity,
				false, "", "", "", "");
	}

	public AbstractBatch(String batchKey, BatchNumber batchNumber, String parentBatchNumber, String saltFormCode,
			double saltEquivs, Compound compound, Amount moles, Amount weight, Amount volume, Amount density, double purity,
			boolean isIntermediate, String compoundState, String protectionCode, String comments, String vendorName) {
		this(batchKey, batchNumber, parentBatchNumber, saltFormCode, saltEquivs, compound, moles, weight, volume, density, purity,
				false, compoundState, protectionCode, comments, vendorName, "", "");
	}

	public AbstractBatch(String batchKey, BatchNumber batchNumber, String parentBatchNumber, String saltFormCode,
			double saltEquivs, Compound compound, Amount moles, Amount weight, Amount volume, Amount density, double purity,
			boolean isIntermediate, String compoundState, String protectionCode, String comments, String vendorName, String owner,
			String synthesizedBy) {
		super();
		if (compound == null)
			this.setCompound(new Compound());
		else
			this.setCompound(compound);

		if (batchNumber == null)
			this.batchNumber = new BatchNumber();
		else
			this.batchNumber = batchNumber;

		if (parentBatchNumber != null)
			this.parentBatchNumber = parentBatchNumber;
		if (saltFormCode == null || saltFormCode.equals("")) {
			// saltForm.setCode("00"); // default parent salt code
			this.saltEquivs = 0.0; // no value for equivs
		} else {
			saltForm.setCode(saltFormCode);
			this.saltEquivs = saltEquivs;
		}
		this.molecularWeightAmount.setCalculated(true);
		if (moles != null)
			moleAmount.deepCopy(moles);
		if (weight != null)
			weightAmount.deepCopy(weight);
		if (volume != null)
			volumeAmount.deepCopy(volume);
		if (density != null)
			densityAmount.deepCopy(density);
		if (CeNNumberUtils.doubleEquals(purity, 0.0, 0.001)) {
			purityAmount.setValue("100");
		} else {
			purityAmount.setValue(purity);
		}
		purityAmount.setUserPrefFigs(2);
		this.setIntermediate(isIntermediate);
		if (compoundState != null)
			this.compoundState = compoundState;
		if (protectionCode != null)
			this.protectionCode = protectionCode;
		if (comments != null)
			this.comments = comments;
		if (vendorName != null) {
			if (vendorInfo != null)
				vendorInfo.deleteObserver(this);
			vendorInfo = new ExternalSupplier();
			vendorInfo.addObserver(this);
			vendorInfo.setSupplierName(vendorName);
		}
		this.rxnEquivsAmount.setSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
		this.rxnEquivsAmount.setValue("1.00");
		this.owner = owner;
		this.synthesizedBy = synthesizedBy;
		addAllObservees();
	}

	public void dispose() throws Throwable {
		removeAllObservees();
		compound = new Compound();
		batchNumber = new BatchNumber();
		parentBatchNumber = null;
		conversationalBatchNumber = null;

		vendorInfo = null;

		owner = null;
		synthesizedBy = null;
		compoundState = null;
		protectionCode = null;
		regStatus = null;
		comments = null;
		storageComments = null;
		hazardComments = null;
		handlingComments = null;

		projectTrackingCode = null;
		precursors = new ArrayList<String>();
	}

	// constructor
	private void init() {
		setChanging(true);

		setExistsInDB(false);
		setCachedLocally(false);

		inCalculation = true;
		compound = new Compound();
		moleAmount.reset();
		weightAmount.reset();
		loadingAmount.reset();
		volumeAmount.reset();
		densityAmount.reset();
		molarAmount.reset();
		purityAmount.reset();
		purityAmount.setValue(100.0);
		rxnEquivsAmount.reset();
		meltPointRange.reset();
		previousMolarAmount.reset();

		limiting = false;
		isIntermediate = false;
		comments = null;

		resetIDs();
		inCalculation = false;
		lastUpdatedType = UPDATE_TYPE_MOLES;
		inCalculation = false; // removeAllObservees();

		setChanging(false);
	}

	/**
	 * Resets numbers (batch, parent molecularWeight set by user, salt equivs), salt form, owner, synthesized by, state (compound
	 * and reg), tracking code, precursors, comments, molecularFormula if set by user.
	 * 
	 */
	public void resetIDs() {
		batchNumber = new BatchNumber();
		parentBatchNumber = null;
		conversationalBatchNumber = null;
		vendorInfo = new ExternalSupplier();
		owner = null;
		synthesizedBy = null;
		compoundState = null;
		protectionCode = null;
		regStatus = null;
		storageComments = null;
		hazardComments = null;
		handlingComments = null;
		projectTrackingCode = null;
		precursors = new ArrayList<String>();
		molecularFormula = null;
		molecularWeightAmount.reset();
		// Resetting saltForm requires setting saltEquivs to 0.0
		saltForm.setCode("00");
		saltEquivs = 0.0;
	}

	private void addAllObservees() {
		compound.addObserver(this);
		moleAmount.addObserver(this);
		weightAmount.addObserver(this);
		molecularWeightAmount.addObserver(this);
		loadingAmount.addObserver(this);
		volumeAmount.addObserver(this);
		densityAmount.addObserver(this);
		molarAmount.addObserver(this);
		purityAmount.addObserver(this);
		rxnEquivsAmount.addObserver(this);
		meltPointRange.addObserver(this);
		saltForm.addObserver(this);
		// boilingPt.addObserver(this);
		if (vendorInfo != null)
			vendorInfo.addObserver(this);
	}

	private void removeAllObservees() {
		compound.deleteObserver(this);
		moleAmount.deleteObserver(this);
		weightAmount.deleteObserver(this);
		loadingAmount.deleteObserver(this);
		volumeAmount.deleteObserver(this);
		densityAmount.deleteObserver(this);
		molarAmount.deleteObserver(this);
		purityAmount.deleteObserver(this);
		rxnEquivsAmount.deleteObserver(this);
		meltPointRange.deleteObserver(this);
		saltForm.deleteObserver(this);
		// boilingPt.deleteObserver(this);
		if (vendorInfo != null)
			vendorInfo.deleteObserver(this);
	}

	/**
	 * 
	 * @return true if batch can be altered. false otherwise
	 */
	public boolean isEditable() {
		// Overridden in ProductBatch.
		return true;
	}

	public int getLastUpdatedType() {
		return lastUpdatedType;
	}

	/**
	 * Do not use this method. Internal use only.
	 * 
	 * @param updateType
	 */
	public void setLastUpdatedType(int updateType) {
		lastUpdatedType = updateType;
	}

	/**
	 * @return Returns the batchType.
	 */
	public abstract BatchType getType();

	/**
	 * Set the batchType.
	 */
	public abstract void setType(BatchType batchType);

	public String getBatchNumberAsString() {
		return (batchNumber == null) ? "" : batchNumber.toString();
	}

	public BatchNumber getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(BatchNumber bn) {
		batchNumber = bn;
		setModified(true);

	}

	public void setBatchNumber(String bn) throws InvalidBatchNumberException {
		if (bn == null || bn.equals(""))
			batchNumber = null;
		else
			batchNumber = new BatchNumber(bn);
		setModified(true);

	}

	/**
	 * Zero based number
	 * 
	 * @return the order in which this batch was added to the notebook.
	 */
	public int getTransactionOrder() {
		return transactionOrder;
	}

	public void setTransactionOrder(int additionOrder) {
		if (additionOrder != transactionOrder) {
			transactionOrder = additionOrder;
			setModified(true);
		}
	}

	/**
	 * @return Returns the parentBatchNumber.
	 */
	public String getParentBatchNumber() {
		return (parentBatchNumber == null) ? "" : parentBatchNumber;
	}

	/**
	 * @param parentBatchNumber
	 *            The parentBatchNumber to set.
	 */
	public void setParentBatchNumber(String parentBN) {
		if (areStringsDifferent(parentBatchNumber, parentBN)) {
			parentBatchNumber = parentBN;
			setModified(true);
		}
	}

	/**
	 * @return Returns the parentBatchNumber.
	 */
	public String getOriginalBatchNumber() {
		return (originalBatchNumber == null) ? "" : originalBatchNumber;
	}

	/**
	 * @param parentBatchNumber
	 *            The parentBatchNumber to set.
	 */
	public void setOriginalBatchNumber(String originalBN) {
		if (areStringsDifferent(originalBatchNumber, originalBN)) {
			originalBatchNumber = originalBN;
			setModified(true);
		}
	}
	
	/**
	 * @return Returns the conversationalBatchNumber.
	 */
	public String getConversationalBatchNumber() {
		return (conversationalBatchNumber == null) ? "" : conversationalBatchNumber;
	}

	/**
	 * @param conversationalBatchNumber
	 *            The conversationalBatchNumber to set.
	 */
	public void setConversationalBatchNumber(String convBN) {
		if (areStringsDifferent(conversationalBatchNumber, convBN)) {
			conversationalBatchNumber = convBN;
			setModified(true);
		}
	}

	/**
	 * @return Returns the compoundState.
	 */
	public String getCompoundState() {
		return (compoundState == null) ? "" : compoundState.toString();
	}

	/**
	 * Compound State may change between batches, but is not a piece of information we wish to track with the compound as it may
	 * change over time.
	 * 
	 * @param compoundState
	 *            The compoundState to set.
	 */
	public void setCompoundState(String state) {
		if (areStringsDifferent(compoundState, state)) {
			compoundState = state;
			setModified(true);
		}
	}

	/**
	 * @see com.chemistry.enotebook.experiment.datahandlers.batch.IBatch#getRegNumber()
	 */
	public String getRegNumber() {
		if (compound != null)
			return compound.getRegNumber();
		else
			return "";
	}

	/**
	 * @return Returns the compound.
	 */
	public Compound getCompound() {
		return compound;
	}

	/**
	 * @param compound
	 *            The compound to set.
	 */
	public void setCompound(Compound cmpd) {
		// TODO: apply this to other objects of Observable type.
		if (compound != null)
			compound.deleteObserver(this);
		cmpd.addObserver(this);
		if (compound != null && compound.isModified())
			cmpd.setModified(true);
		compound = cmpd;
		// If this compound was modified from it's blank state,
		// set notify observers about this fact.
		if (!isLoading())
			setModified(true);
	}

	/**
	 * @return Returns the isIntermediate.
	 */
	public boolean isIntermediate() {
		return isIntermediate;
	}

	/**
	 * @param isIntermediate
	 *            The isIntermediate to set.
	 */
	public void setIntermediate(boolean flag) {
		if (isIntermediate != flag) {
			isIntermediate = flag;
			setModified(true);
		}
	}

	/**
	 * @return Returns Storage Comments
	 */
	public String getStorageComment() {
		return (storageComments == null) ? "" : storageComments;
	}

	/**
	 * @param comment
	 *            Set the storage comment information
	 */
	public void setStorageComment(String comments) {
		if (areStringsDifferent(storageComments, comments)) {
			storageComments = comments;
			setModified(true);
		}
	}

	/**
	 * @return Returns Hazard Comments
	 */
	public String getHazardComments() {
		return (hazardComments == null) ? "" : hazardComments;
	}

	/**
	 * @param comment
	 *            Set the hazard comment information
	 */
	public void setHazardComments(String comments) {
		if (areStringsDifferent(hazardComments, comments)) {
			hazardComments = comments;
			setModified(true);
		}
	}

	/**
	 * @return Returns Handling Comments
	 */
	public String getHandlingComments() {
		return (handlingComments == null) ? "" : handlingComments;
	}

	/**
	 * @param comment
	 *            Set the Handling comment information
	 */
	public void setHandlingComments(String comments) {
		if (areStringsDifferent(handlingComments, comments)) {
			handlingComments = comments;
			setModified(true);
		}
	}

	/**
	 * @return Returns the protectionCode.
	 */
	public String getProtectionCode() {
		return (protectionCode == null) ? "" : protectionCode;
	}

	/**
	 * @param protectionCode
	 *            The protectionCode to set.
	 */
	public void setProtectionCode(String protectCode) {
		if (areStringsDifferent(protectionCode, protectCode)) {
			protectionCode = protectCode;
			setModified(true);
		}
	}

	/**
	 * regStatus will be SDFILE for after an SDFile was produced for registration, INPROGRESS indicating last known status was that
	 * this batch info was sent for registration, and REGISTERED for when this batch has a conversational batch number
	 * 
	 * This value will always return something.
	 * 
	 * @return Returns the regStatus.
	 */
	public String getRegStatus() {
		return (regStatus == null || regStatus.length() == 0) ? BatchRegistrationInfo.NOT_REGISTERED : regStatus;
	}

	/**
	 * regStatus will be SDFILE for after an SDFile was produced for registration, INPROGRESS indicating last known status was that
	 * this batch info was sent for registration, and REGISTERED for when this batch has a conversational batch number
	 * 
	 * @param regStatus
	 *            The regStatus to set.
	 */
	public void setRegStatus(String regStat) {
		regStatus = regStat;
		setModified(true);
	}

	public String getOwner() {
		return (owner == null) ? "" : owner.toString();
	}

	public void setOwner(String ownr) {
		owner = ownr;
		setModified(true);
	}

	public String getSynthesizedBy() {
		return (synthesizedBy == null) ? "" : synthesizedBy.toString();
	}

	public void setSynthesizedBy(String synthBy) {
		if (areStringsDifferent(synthesizedBy, synthBy)) {
			synthesizedBy = synthBy;
			setModified(true);
		}
	}

	/**
	 * @return Returns the comments.
	 */
	public String getComments() {
		return (comments == null) ? "" : comments;
	}

	/**
	 * @param comments
	 *            The comments to set.
	 */
	public void setComments(String cmts) {
		if (areStringsDifferent(comments, cmts)) {
			comments = cmts;
			setModified(true);
		}
	}

	/**
	 * @return returns Project Tracking Code as set by business
	 */
	public String getProjectTrackingCode() {
		return (projectTrackingCode == null) ? "" : projectTrackingCode.toString();
	}

	/**
	 * @param projectTrackingCode -
	 *            should be a valid Project Tracking Code list can be retreived from CompoundManagement
	 */
	public void setProjectTrackingCode(String prjTrackingCode) {
		if (areStringsDifferent(projectTrackingCode, prjTrackingCode)) {
			projectTrackingCode = prjTrackingCode;
			setModified(true);
		}
	}

	/**
	 * Preferred accessor method which will get the calc'd version of the formula if not overriden by molecularFormula
	 * 
	 * @return Returns the molFormula.
	 */
	public String getMolFormula() {
		StringBuffer result = new StringBuffer("");
		if (molecularFormula != null && molecularFormula.length() > 0) {
			result.append(molecularFormula);
		} else if (compound.getMolFormula() != null) {
			result.append(compound.getMolFormula());
			if (!result.toString().equals("") && !getSaltForm().isParentForm() && getSaltEquivs() > 0 && getSaltEquivsSet()) {
				if (result.indexOf(".") == -1) {
					result.append(" . ");
					if (getSaltEquivs() > 0 && getSaltEquivs() != 1.0)
						result.append(getSaltEquivs()).append("(");
					result.append(getSaltForm().getFormula());
					if (getSaltEquivs() > 0 && getSaltEquivs() != 1.0)
						result.append(")");
				}
			}
		}
		return result.toString();
	}

	/**
	 * sets the molFormula of the compound.
	 * 
	 * @param molFormula
	 */
	public void setMolFormula(String molFormula) {
		if (molFormula == null) molFormula = "";

		// Strip Salt info or Residual Solvent info, if present
		int dot = molFormula.indexOf(".");
		if (dot >= 0) molFormula = molFormula.substring(0, dot);
		int semiPos = molFormula.indexOf(";");
		if (semiPos >= 0) molFormula = molFormula.substring(0, semiPos);

		getCompound().setMolFormula(molFormula);
	}

	/**
	 * Used on save/restore only.
	 * 
	 * @return string representing molFormula if one was provided that differs from the calc'd version. Otherwise returns empty
	 *         String object
	 */
	public String getMolecularFormula() {
		return (molecularFormula == null) ? "" : molecularFormula;
	}

	/**
	 * Overrides calc'd molFormula. Do not use unless this is the intention. Used in special cases for Analyze Reaction and Reagent
	 * Lookup returns
	 * 
	 * @param molFormula
	 */
	public void setMolecularFormula(String molFormula) {
		// TODO: Need to parse to make sure no salt formula gets into
		// the compound molFormula
		molecularFormula = molFormula;
		setModified(true);
	}

	/**
	 * If molecular weight amount isCalculated() or the batch molWgt is not set, the value will be the compound molWt and the salt
	 * code's molWt * the number of Salt Equivs. If isCalculated() is false, then the value stored with the batch will be the value
	 * returned by this method.
	 * 
	 * ShortCut for getMolecularWeightAmount().getValue();
	 * 
	 * @return the calculated molWgt if MolecularWeightAmount.isCalculated() == true.
	 */
	public double getMolWgt() {
		if (molecularWeightAmount.isValueDefault() && molecularWeightAmount.isCalculated()) {
			setMolWgtCalculated(getMolWgtCalculated());
		}
		return molecularWeightAmount.doubleValue();
		// double result = molecularWeightAmount.getValue();
		// // return 0 if no compound weight has been set -or- return weight set by user
		// if (molecularWeightAmount.isValueDefault() && molecularWeightAmount.isCalculated()) result = getMolWgtCalculated();
		// return result;
	}

	/**
	 * Used to get the amount object that holds molecularWeight for the batch if it is not calculated.
	 * 
	 * @return the calculated molWgt if MolecularWeightAmount.isCalculated() == true.
	 */
	public Amount getMolecularWeightAmount() {
		return molecularWeightAmount;
	}

	/**
	 * Take the parent molwgt and add it to (saltEquivs * salt molwgt)
	 * 
	 * @return double - zero if there isn't a parent molwgt.
	 */
	public double getMolWgtCalculated() {
		// Significant Figures for calculated molecular weight was deemed to be ignored.
		// Scientists want MW calculated out to the third place behind the decimal or 3 fixed figures if they are available.
		// Do not enforce sig figs here.
		// Don't return more than three places after decimal point
		double result = 0.0;
		if (compound.getMolWgt() > 0.0) {
        	if (saltEquivsSet)
        		result = compound.getMolWgt() + (getSaltForm().getMolWgt() * getSaltEquivs());
        	else
           		result = compound.getMolWgt();
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
		
        if (sigs.doubleValue() != molecularWeightAmount.doubleValue())
        	molecularWeightAmount.setValue(sigs.doubleValue());
	}

	/**
	 * Use when a different value of molecularWeight is desired to be displayed. Pull from getMolWgt()
	 * 
	 * @param molWgtAmt -
	 *            Amount object to use for molecular weight
	 */
	public void setMolecularWeightAmount(Amount molWgtAmt) {
		if (!molecularWeightAmount.equals(molWgtAmt)) {
			molecularWeightAmount.deepCopy(molWgtAmt);
			setModified(true);
			recalcAmounts();
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

	/**
	 * @return Returns the saltFormCode.
	 */
	public SaltForm getSaltForm() {
		return saltForm;
	}

	/**
	 * Side-Effect: This will influence the value of saltEquivs directly and will be factored into the calculation of the MolWt of
	 * the batch.
	 * 
	 * If the saltForm is null, of zero length or equal to the parent structure code: "00" then the saltEquivs should be 0.0. If the
	 * saltForm is changing from any of the values above to a valid salt code and the salt equivs are 0.0 we need to set the
	 * saltEquivs to 1.0
	 * 
	 * @param saltFormCode
	 *            The saltFormCode to set.
	 */
	public void setSaltForm(SaltForm newSaltForm) {
		if (newSaltForm != null && !saltForm.equals(newSaltForm)) {
			if (saltForm.getCode() == null || saltForm.isParentForm())
				// if the current saltForm is nothing and we are not
				// setting the saltFormCode to a valid salt we need to
				// make sure saltEquivs are minimally set to 1.
				if (saltEquivs == 0.0) {
					saltEquivs = 0.0;
					setSaltEquivsSet(false); 
				}
			saltForm.deepCopy(newSaltForm);
		}

		// If we are unsetting salt form we need to make sure there are no saltEquivs
		// Same goes with setting saltForm to ParentStructure: "00"
		if (saltForm.isParentForm())
			saltEquivs = 0.0; // no salt form := make sure equivs are 0.0
		if (molecularWeightAmount.isCalculated()) {
			setMolWgtCalculated(getMolWgtCalculated());
		}
		recalcAmounts();
		setModified(true);
	}

	/**
	 * @return Returns the saltEquiv.
	 */
	public double getSaltEquivs() {
		return saltEquivs;
	}

	/**
	 * @param saltEquiv
	 *            The saltEquiv to set.
	 */
	public void setSaltEquivs(double equivs) {
		if (isLoading() || !(saltForm.isParentForm())) {
			saltEquivs = equivs;
			if (!isLoading()) {
				recalcAmounts();
				if (molecularWeightAmount.isCalculated()) {
					setMolWgtCalculated(getMolWgtCalculated());
				}
			}
			setModified(true);
		}
	}

	/**
	 * @return Returns the saltEquivsSet.
	 */
	public boolean getSaltEquivsSet() {
		return saltEquivsSet;
	}
	/**
	 * @param equivsSet The saltEquivsSet to set.
	 */
	public void setSaltEquivsSet(boolean equivsSet) {
		boolean change = (saltEquivsSet != equivsSet);
        saltEquivsSet = equivsSet;
        if (change) setSaltEquivs(saltEquivs);
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
	 * Value defaults to 0.0 but that really means it should be 100%
	 * 
	 * @return Amount object which holds the scalar value of purity
	 */
	public Amount getPurityAmount() {
		return purityAmount;
	}

	/**
	 * value in weight percent for purity: range = 0 - 100
	 * 
	 * @param purity
	 */
	public void setPurityAmount(Amount value) {
		if (value != null) {
			if (!purityAmount.equals(value)) {
				purityAmount.deepCopy(value);
				recalcAmounts();
				setModified(true);
			}
		} else {
			purityAmount.setValue("100", true);
		}
		updateCalcFlags(purityAmount);
	}

	/**
	 * @return Returns the density.
	 */
	public double getDensity() {
		return densityAmount.doubleValue();
	}

	/**
	 * 
	 * @return Amount class of density.
	 */
	public Amount getDensityAmount() {
		return densityAmount;
	}

	/**
	 * Density is not a calculated amount. It will have been entered from a referenced program or from the user. Hence when set, in
	 * any way, its isCalculated() value becomes false.
	 * 
	 * @param density
	 *            The density to set.
	 */
	public void setDensity(double density) {
		if (density <= 0)
			densityAmount.setValue("0", true);
		else
			densityAmount.setValue(density, false);
		updateCalcFlags(densityAmount);
		recalcAmounts();
	}

	/**
	 * @param density
	 *            The density to set.
	 */
	public void setDensityAmount(Amount density) {
		if (density != null) {
			if (density.getUnitType() == UnitType.DENSITY) {
				// Check to see if it is a unit change
				if (!densityAmount.equals(density)) {
					densityAmount.deepCopy(density);
					updateCalcFlags(densityAmount);
					recalcAmounts();
					setModified(true);
				}
			}
		} else {
			densityAmount.setValue("0");
		}
	}

	/**
	 * @return Returns the mole Amount.
	 */
	public Amount getMoleAmount() {
		return moleAmount;
	}

	/**
	 * @param moles
	 *            The amount of moles to set.
	 */
	public void setMoleAmount(Amount moles) {
		if (moles != null) {
			if (moles.getUnitType() == UnitType.MOLES) {
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
	}

	/**
	 * @return Returns the weight Amount.
	 */
	public Amount getWeightAmount() {
		return weightAmount;
	}

	/**
	 * @param weight
	 *            The amount of weight to set.
	 */
	public void setWeightAmount(Amount weight) {
		if (weight != null) {
			if (weight.getUnitType() == UnitType.MASS) {
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

	public Amount getLoadingAmount() {
		return loadingAmount;
	}

	public void setLoadingAmount(Amount loadingAmount) {
		this.loadingAmount.deepCopy(loadingAmount);
		setModified(true);
	}

	/**
	 * @return Returns the volume Amount.
	 */
	public Amount getVolumeAmount() {
		return this.volumeAmount;
	}

	/**
	 * @param volume
	 *            The amount of volume to set.
	 */
	public void setVolumeAmount(Amount volume) {
		if (volume != null) {
			if (volume.getUnitType() == UnitType.VOLUME) {
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
	 * @return Returns the # of Equivs in reaction.
	 */
	public double getRxnEquivs() {
		return rxnEquivsAmount.doubleValue();
	}

	public Amount getRxnEquivsAmount() {
		return rxnEquivsAmount;
	}

	public void setRxnEquivsAmount(Amount equiv) {
		if (!rxnEquivsAmount.equals(equiv)) {
			rxnEquivsAmount.deepCopy(equiv);
			updateCalcFlags(rxnEquivsAmount);
			setModified(true);
		} else {
			this.rxnEquivsAmount.setValue("1.00");
		}
	}

	public boolean isLimiting() {
		return limiting;
	}

	public void setLimiting(boolean flag) {
		limiting = flag;
		recalcAmounts();
		setModified(true);
	}

	public TemperatureRange getMeltPointRange() {
		return meltPointRange;
	}

	public void setMeltPointRange(TemperatureRange meltRng) {
		meltPointRange.deepCopy(meltRng);
		setModified(true);
	}

	// Implements AutoCalc
	public boolean isAutoCalcOn() {
		return autoCalcOn;
	}

	public void setAutoCalcOn(boolean autoCalc) {
		this.autoCalcOn = autoCalc;
	}

	// changingAmount = amount that we are adjusting
	// need to remove dependent Calc flags for stoich model
	// recalcAmounts will take care of mole vs weight calc flags, etc.
	private void updateCalcFlags(Amount changingAmount) {
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
		ArrayList<Amount> amts = new ArrayList<Amount>();
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
		ArrayList<Amount> amts = new ArrayList<Amount>();
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
		if (!autoCalcOn || inCalculation || isLoading())
			return;

		ArrayList<Amount> amts = new ArrayList<Amount>();
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
				// update volume
				amts.add(moleAmount);
				amts.add(molarAmount);
				if (volumeAmount.isCalculated() && weightAmount.isCalculated())
					volumeAmount.setSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
				else
					applySigFigRules(volumeAmount, amts);
				amts.clear();// important to clear the amts list
				volumeAmount.SetValueInStdUnits(moleAmount.GetValueInStdUnitsAsDouble() / molarAmount.GetValueInStdUnitsAsDouble(),
						true);
			} else if (densityAmount.doubleValue() > 0) {
				amts.add(weightAmount);
				amts.add(densityAmount);
				applySigFigRules(volumeAmount, amts);
				amts.clear();// important to clear the amts list

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
		if (o != null && o instanceof AbstractBatch) {
			AbstractBatch ab = (AbstractBatch) o;
			if (this.getKey().equals(ab.getKey()))
				result = true;
		}
		return result;
	}

	// Before playing here familiarize yourself with the
	// layout rules for stoichiometry.
	public int compareTo(Object o) {
		int result = 0;
		if (o != null && o instanceof AbstractBatch) {
			AbstractBatch ab = (AbstractBatch) o;
			result = this.getType().compareTo(ab.getType());
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

	public Amount getPreviousMolarAmount() {
		return previousMolarAmount;
	}

	public void setPreviousMolarAmount(Amount preMolarAmt) {
		previousMolarAmount.deepCopy(preMolarAmt);
	}

	public Amount getMolarAmount() {
		return molarAmount;
	}

	public void setMolarAmount(Amount molarAmnt) {
		if (molarAmnt != null) {
			if (molarAmnt.getUnitType() == UnitType.MOLAR) {
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

	public ArrayList<String> getPrecursors() {
		return precursors;
	}

	public void addPrecursor(String precursor) {
		// Add to the list if not already there
		if (precursors.indexOf(precursor) < 0) {
			precursors.add(precursor);
			setModified(true);
		}
	}

	public void setPrecursors(List<String> precursorList) {
		precursors = new ArrayList<String>();
		if (precursorList != null) {
			for (Iterator it = precursorList.iterator(); it.hasNext();) {
				String precursor = (String) it.next();
				if (precursors.indexOf(precursor) < 0)
					precursors.add(precursor);
			}
			precursors.addAll(precursorList);
		}
		setModified(true);
	}

	public String getStorageComments() {
		return (storageComments == null) ? "" : storageComments.toString(); 
	}

	public void setStorageComments(String comments) {
		storageComments = comments;
		setModified(true);
	}

	public ExternalSupplier getVendorInfo() {
		return vendorInfo;
	}

	public void setVendorInfo(ExternalSupplier vendorInfo) {
		if (vendorInfo != null)
			vendorInfo.deleteObserver(this);
		this.vendorInfo = vendorInfo;
		if (vendorInfo != null)
			vendorInfo.addObserver(this);
		setModified(true);
	}

	public String getVendorName() {
		return (vendorInfo != null) ? vendorInfo.getSupplierName() : "";
	}

	public void setVendorName(String vendorName) {
		if (vendorInfo != null) {
			vendorInfo = new ExternalSupplier();
			vendorInfo.addObserver(this);
		}
		vendorInfo.setSupplierName(vendorName);
		// Side-effect should be that vendorInfo causes a modified notification
		// that this object will catch.
	}

	public boolean isVolumeConnectedToMass() {
		return (densityAmount.doubleValue() > 0 || molarAmount.doubleValue() > 0);
	}

	/**
	 * Amounts that are possibly being loaded from external sources.
	 * 
	 * @return
	 */
	protected List<Amount> getLoadedAmounts() {
		ArrayList<Amount> result = new ArrayList<Amount>();
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
	protected List<Amount> getCalculatedAmounts() {
		ArrayList<Amount> result = new ArrayList<Amount>();
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
	protected List<Amount> getMeasuredAmounts() {
		ArrayList<Amount> result = new ArrayList<Amount>();
		result.add(getWeightAmount());
		result.add(getVolumeAmount());
		return result;
	}

	/**
	 * 
	 * @param amt
	 * @return true if it is a Loaded amount false otherwise
	 */
	protected boolean isLoadedAmount(Amount amt) {
		return getLoadedAmounts().contains(amt);
	}

	/**
	 * 
	 * @param amt
	 * @return true if it is a Calculated amount false otherwise
	 */
	protected boolean isCalculatedAmount(Amount amt) {
		return getCalculatedAmounts().contains(amt);
	}

	/**
	 * 
	 * @param amt
	 * @return true if it is a Measured amount false otherwise
	 */
	protected boolean isMeasuredAmount(Amount amt) {
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
		List<Amount> amts = getCalculatedAmounts();
		for (Iterator i = amts.iterator(); i.hasNext();) {
			Amount amt = (Amount) i.next();
			if (amt.isCalculated()) {
				amt.setSigDigits(defaultSigs);
			}
		}
	}

	/**
	 * Applies SigFigs for the Amount Object. Checks if the standard sigfig rules to be applied, which checks for any user edits on
	 * Measured Amounts (Weight,Volume). else any other edit would lead to Default SigFig application on the Amount.
	 * 
	 * @param amt
	 * @param amts
	 */
	public void applySigFigRules(Amount amt, List amts) {
		if (shouldApplySigFigRules()) {
			amt.setSigDigits(CeNNumberUtils.getSmallestSigFigsFromList(amts));
		} else {
			if (shouldApplyDefaultSigFigs())
				amt.setSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
		}
	}

	//
	// Override of ObservableObject
	//
	/**
	 * This is an override of the ObservableObject. Don't use this to update the values in the object as it is meant only to
	 * indicate that the isModified() flag was set on either this object or one it contains.
	 */
	public void update(Observable observed, Object obj) {
		if (!inCalculation && !isLoading()) {
			if ((obj instanceof Compound && ((Compound) obj).isModified())
					|| (observed instanceof Compound && ((Compound) observed).isModified())) {
				// we need to recalculate our amounts based on a new molecular weight
				recalcAmounts();
				setSubObjectModified(observed);
			} else if (observed instanceof Amount) {
				// We need to tell ourselves which amount object was updated so recalc won't stomp on user input.
				// Make sure it's not just in reset.
				Amount tstAmt = (Amount) observed;
				if (!CeNNumberUtils.doubleEquals(tstAmt.getDefaultValue(), tstAmt.doubleValue())) {
					updateCalcFlags((Amount) observed);
					switch (((Amount) observed).getUnitType().getOrdinal()) {
						case UnitType.MASS_ORDINAL:
							lastUpdatedType = UPDATE_TYPE_WEIGHT;
							break;
						case UnitType.MOLES_ORDINAL:
							lastUpdatedType = UPDATE_TYPE_MOLES;
							break;
						case UnitType.VOLUME_ORDINAL:
							lastUpdatedType = UPDATE_TYPE_VOLUME;
							break;
						default:
							break;
					}
					recalcAmounts();
					// applyLatestSigDigits(getSmallestSigFigs());
				}

			}
			// Once we have finished recalculating, we can notify the rest of the model
			super.update(observed, obj);
		}
	}

	/**
	 * This is an override of the ObservableObject. Don't use this to update the values in the object as it is meant only to
	 * indicate that the isModified() flag was set on either this object or one it contains.
	 */
	public void update(Observable observed) {
		if (!inCalculation && !isLoading()) {
			if (observed instanceof Compound && ((Compound) observed).isModified()) {
				// we need to recalculate our amounts based on a new molecular weight
				recalcAmounts();
			} else if (observed instanceof Amount) {
				updateCalcFlags((Amount) observed);
			}
			// Once we have finished recalculating, we can notify the rest of the model
			super.update(observed);
		}
	}

	/**
	 * Removes all data from this batch and sets it back to its default values
	 * 
	 */
	public void clear() {
		init();
		setModified(true);
		// Request a clean-up in isle 7! We just blew away a lot of refs.
	}

	/**
	 * Copies target batch's information over to the this batch. Compound information is referenced and not copied. Convenience
	 * method for copy(AbstractBatch, boolean).
	 * 
	 * @param target -
	 *            batch to copy informtion to.
	 */
	public void deepCopy(Object source) {
		if (source != null)
			deepCopy(source, false);
	}

	/**
	 * Copies target batch's information over to the this batch. If the Compound object's information is to be copied and not just
	 * referenced, pass a true to deepCopy.
	 * 
	 * @param target -
	 *            batch to copy informtion to.
	 * @param deepCopy -
	 *            true if the Compound object is to be cloned or false if Compound is to be referenced
	 */
	public void deepCopy(Object source, boolean deepCopy) {
		if (source != null && source instanceof AbstractBatch) {
			AbstractBatch sourceInstance = (AbstractBatch) source;
			if (!sourceInstance.isDeleted()) {
				batchNumber.deepCopy(sourceInstance.batchNumber);
				if (deepCopy) {
					compound.deepCopy(sourceInstance.compound);
				} else {
					compound = sourceInstance.compound;
				}
				parentBatchNumber = sourceInstance.parentBatchNumber;
				conversationalBatchNumber = sourceInstance.conversationalBatchNumber;
				molecularFormula = sourceInstance.molecularFormula;
				molecularWeightAmount.deepCopy(sourceInstance.molecularWeightAmount);
				moleAmount.deepCopy(sourceInstance.moleAmount);
				weightAmount.deepCopy(sourceInstance.weightAmount);
				loadingAmount.deepCopy(sourceInstance.loadingAmount);
				volumeAmount.deepCopy(sourceInstance.volumeAmount);
				densityAmount.deepCopy(sourceInstance.densityAmount);
				molarAmount.deepCopy(sourceInstance.molarAmount);
				previousMolarAmount.deepCopy(sourceInstance.previousMolarAmount);
				purityAmount.deepCopy(sourceInstance.purityAmount);
				rxnEquivsAmount.deepCopy(sourceInstance.rxnEquivsAmount);
				meltPointRange.deepCopy(sourceInstance.meltPointRange);
				saltForm.deepCopy(sourceInstance.saltForm);
				saltEquivs = sourceInstance.saltEquivs;

				limiting = sourceInstance.limiting;
				isIntermediate = sourceInstance.isIntermediate;

				compoundState = sourceInstance.compoundState;
				protectionCode = sourceInstance.protectionCode;
				transactionOrder = sourceInstance.transactionOrder;
				// RegStatus should be false as it doesn't make sense to copy
				comments = sourceInstance.comments;
				storageComments = sourceInstance.storageComments;
				hazardComments = sourceInstance.hazardComments;
				vendorInfo = (ExternalSupplier) sourceInstance.vendorInfo.deepClone();
				projectTrackingCode = sourceInstance.projectTrackingCode;
				precursors = sourceInstance.precursors;

				// copy chloracnegen info
				handlingComments = sourceInstance.handlingComments;
				testedForChloracnegen = sourceInstance.testedForChloracnegen;
				chloracnegenFlag = sourceInstance.chloracnegenFlag;
				chloracnegenType = sourceInstance.chloracnegenType;
			}
		}
	}

	/**
	 * Returns an object of AbstractBatch type that has been filled with the values of this object. Not references.
	 * 
	 * Requires extending classes to carry this override.
	 */
	public abstract Object deepClone();

	public boolean isChloracnegen() {
		return chloracnegenFlag;
	}

	public boolean getChloracnegenFlag() {
		return chloracnegenFlag;
	}

	public void setChloracnegenFlag(boolean chloracnegenFlag) {
		this.chloracnegenFlag = chloracnegenFlag;
		setModified(true);
	}

	public boolean isTestedForChloracnegen() {
		return testedForChloracnegen;
	}

	public boolean getTestedForChloracnegen() {
		return testedForChloracnegen;
	}

	public void setTestedForChloracnegen(boolean testedForChloracnegen) {
		this.testedForChloracnegen = testedForChloracnegen;
	}

	public String getChloracnegenType() {
		return (chloracnegenType == null) ? "" : chloracnegenType.toString();
	}

	public void setChloracnegenType(String chloracnegenType) {
		this.chloracnegenType = chloracnegenType;
	}

	public void copyOverChloracData(AbstractBatch sourceBatch) {
		this.chloracnegenFlag = sourceBatch.getChloracnegenFlag();
		this.testedForChloracnegen = sourceBatch.getTestedForChloracnegen();
		this.chloracnegenType = sourceBatch.getChloracnegenType();
	}

	public HashMap getBatchProperties() {
		return batchProperties;
	}

	public void setBatchProperties(HashMap batchProperties) {
		this.batchProperties = batchProperties;
	}
}
