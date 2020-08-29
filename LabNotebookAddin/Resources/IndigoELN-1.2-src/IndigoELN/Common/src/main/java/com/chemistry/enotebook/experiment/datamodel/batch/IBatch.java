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
import com.chemistry.enotebook.experiment.common.SaltForm;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;
import com.chemistry.enotebook.experiment.datamodel.compound.Compound;

/**
 * 
 * @date Aug 17, 2004
 */
public interface IBatch {
	// Identifiers
	public String getRegNumber(); // returns the compound's registered number

	public BatchNumber getBatchNumber();

	public void setBatchNumber(String BatchNumber) throws InvalidBatchNumberException;

	public void setBatchNumber(BatchNumber newNumber);

	// Tracking info
	public String getParentBatchNumber();

	public void setParentBatchNumber(String ParentBatchNumber);

	public String getConversationalBatchNumber();

	public void setConversationalBatchNumber(String conversationalBatchNumber);

	// Compound Access
	public String getCompoundState();

	public void setCompoundState(String compoundState);

	public Compound getCompound();

	public void setCompound(Compound compound);

	// Handling info
	public BatchType getType();

	public void setType(BatchType batchType);

	public boolean isIntermediate();

	public void setIntermediate(boolean isIntermediate);

	public String getProtectionCode();

	public void setProtectionCode(String protectionCode);

	public String getRegStatus();

	public void setRegStatus(String regStatus);

	public String getVendorName();

	public void setVendorName(String vendorName);

	public ExternalSupplier getVendorInfo();

	public void setVendorInfo(ExternalSupplier vendorInfo);

	public String getComments();

	public void setComments(String comments);

	public String getHazardComments();

	public void setHazardComments(String comment);

	public String getProjectTrackingCode();

	public void setProjectTrackingCode(String projectTrackingCode);

	// Chemical info not found in Compound
	public String getMolFormula();

	public void setMolFormula(String molFormula);

	public void setSaltForm(SaltForm saltForm);

	public SaltForm getSaltForm(); // Two character string from cache.

	public double getSaltEquivs();

	public void setSaltEquivs(double saltEquiv);

	public double getMolWgt();

	public void setMolWgt(double molWgt);

	public Amount getPurityAmount(); // Assumed 100% where not indicated.

	public void setPurityAmount(Amount value);

	public double getDensity(); // Assumed 1 for calculations where not indicated.

	public void setDensity(double density);

	public Amount getWeightAmount();

	public void setWeightAmount(Amount weight);

	public Amount getVolumeAmount();

	public void setVolumeAmount(Amount volume);

	public Amount getRxnEquivsAmount();

	public void setRxnEquivsAmount(Amount equivs);

	public boolean isLimiting();

	public void setLimiting(boolean flag);
}
