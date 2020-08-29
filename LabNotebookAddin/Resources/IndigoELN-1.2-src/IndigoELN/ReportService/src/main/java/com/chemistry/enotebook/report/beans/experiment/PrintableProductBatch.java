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
package com.chemistry.enotebook.report.beans.experiment;

import com.chemistry.enotebook.domain.ProductBatchModel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;

public class PrintableProductBatch {
	private static final Log log_ = LogFactory.getLog(PrintableProductBatch.class);

	private String stepName = "";
	private String notebookBatchNumber = "";
	private String conversationalBatchNumber = "";
	private String molFormula = "";
	private String molWeight = "";
    private String exactMass = "";
	private String compoundState = "";
	private String batchComments = "";
	private String hazards = "";
	private String handling = "";
	private String storage = "";
	private String residualSolvents = "";
    private String solubilityInSolvents = "";
    private String site = "";
	private String compoundProtection = "";
	private String registrationNumber = "";
	//private String structureUrl = "";
	private String amountMade = "";
	private String percentYield = "";
	private String theoreticalWeight = "";
    private String meltingPoint = "";
	private String Purity = "";
	private String imageUri = "";
	private String comments = "";
	private String structureComments = "";
	private String source = "";
	private String sourceDetail = "";
	private String attributes = "";
	private String barcodeUri = "";


    private String hitId = "";
    private String owner = "";
    private String registrationDate = "";
    private String supplier = "";
    private String precursors = "";
    private String reactants = "";
    private String synthesizedBy = "";
    private String parentMW = "";
    private String parentMF = "";

    private String compoundSaltCode = "";
    private String compoundSaltEQ = "";
    
    public String getCompoundSaltCode() {
		return compoundSaltCode;
	}
	public void setCompoundSaltCode(String saltCode) {
		this.compoundSaltCode = saltCode;
	}
	public String getCompoundSaltEQ() {
		return compoundSaltEQ;
	}
	public void setCompoundSaltEQ(String saltEQ) {
		this.compoundSaltEQ = saltEQ;
	}
	public String getNotebookBatchNumber() {
		return notebookBatchNumber;
	}
	public void setNotebookBatchNumber(String notebookBatchNumber) {
		this.notebookBatchNumber = notebookBatchNumber;
	}
	public String getMolFormula() {
		return molFormula;
	}
	public void setMolFormula(String molFormula) {
		this.molFormula = molFormula;
	}
	public String getMolWeight() {
		return molWeight;
	}
	public void setMolWeight(String molWeight) {
		this.molWeight = molWeight;
	}

    public String getExactMass() {
        return exactMass;
    }

    public void setExactMass(String exactMass) {
        this.exactMass = exactMass;
    }

    public String getCompoundState() {
		return compoundState;
	}
	public void setCompoundState(String compoundState) {
		this.compoundState = compoundState;
	}
	public String getBatchComments() {
		return batchComments;
	}
	public void setBatchComments(String batchComments) {
		this.batchComments = batchComments;
	}
	public String getHazards() {
		return hazards;
	}
	public void setHazards(String hazards) {
		this.hazards = hazards;
	}
	public String getHandling() {
		return handling;
	}
	public void setHandling(String handling) {
		this.handling = handling;
	}
	public String getStorage() {
		return storage;
	}
	public void setStorage(String storage) {
		this.storage = storage;
	}
	public String getResidualSolvents() {
		return residualSolvents;
	}
	public void setResidualSolvents(String residualSolvents) {
		this.residualSolvents = residualSolvents;
	}

    public String getSolubilityInSolvents() {
        return solubilityInSolvents;
    }

    public void setSolubilityInSolvents(String solubilityInSolvents) {
        this.solubilityInSolvents = solubilityInSolvents;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getCompoundProtection() {
		return compoundProtection;
	}
	public void setCompoundProtection(String compoundProtection) {
		this.compoundProtection = compoundProtection;
	}
	public String getRegistrationNumber() {
		return registrationNumber;
	}
	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}
//	public String getStructureUrl() {
//		return structureUrl;
//	}
//	public void setStructureUrl(String structureUrl) {
//		this.structureUrl = structureUrl;
//	}
	public String getAmountMade() {
		return amountMade;
	}
	public void setAmountMade(String amountMade) {
		this.amountMade = amountMade;
	}
	public String getPercentYield() {
		return percentYield;
	}
	public void setPercentYield(String percentYield) {
		this.percentYield = percentYield;
	}
	public String getTheoreticalWeight() {
		return theoreticalWeight;
	}
	public void setTheoreticalWeight(String theoreticalWeight) {
		this.theoreticalWeight = theoreticalWeight;
	}

    public String getMeltingPoint() {
        return meltingPoint;
    }

    public void setMeltingPoint(String meltingPoint) {
        this.meltingPoint = meltingPoint;
    }

    public String getPurity() {
		return Purity;
	}
	public void setPurity(String purity) {
		Purity = purity;
	}
	public String getStepName() {
		return stepName;
	}
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	public String getImageUri() {
		return imageUri;
	}
	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getStructureComments() {
		return structureComments;
	}
	public void setStructureComments(String structureComments) {
		this.structureComments = structureComments;
	}
	public String getConversationalBatchNumber() {
		return conversationalBatchNumber;
	}
	public void setConversationalBatchNumber(String conversationalBatchNumber) {
		this.conversationalBatchNumber = conversationalBatchNumber;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSourceDetail() {
		return sourceDetail;
	}
	public void setSourceDetail(String sourceDetail) {
		this.sourceDetail = sourceDetail;
	}
	public String getBarcodeUri() {
		return barcodeUri;
	}
	public void setBarcodeUri(String barcodeUri) {
		this.barcodeUri = barcodeUri;
	}

    public String getHitId() {
        return hitId;
    }

    public void setHitId(String hitId) {
        this.hitId = hitId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getPrecursors() {
        return precursors;
    }

    public void setPrecursors(String precursors) {
        this.precursors = precursors;
    }

    public String getReactants() {
        return reactants;
    }

    public void setReactants(String reactants) {
        this.reactants = reactants;
    }

    public String getSynthesizedBy() {
        return synthesizedBy;
    }

    public void setSynthesizedBy(String synthesizedBy) {
        this.synthesizedBy = synthesizedBy;
    }

    public String getParentMW() {
        return parentMW;
    }

    public void setParentMW(String parentMW) {
        this.parentMW = parentMW;
    }

    public String getParentMF() {
        return parentMF;
    }

    public void setParentMF(String parentMF) {
        this.parentMF = parentMF;
    }

    public String getAttributes() {
		StringBuffer buff = new StringBuffer();
		if (this.molFormula != null && this.molFormula.length() > 0)
			buff.append("Mol Formula: ").append(this.molFormula).append("\n");
		if (this.molWeight != null && this.molWeight.length() > 0)
			buff.append("Mol Weight: ").append(this.molWeight).append("\n");
		if (this.batchComments != null && this.batchComments.length() > 0)
			buff.append("Batch Comments: ").append(this.batchComments).append("\n");
		if (this.comments != null && this.comments.length() > 0)
			buff.append("Comments: ").append(this.comments).append("\n");
		if (this.source != null && this.source.length() > 0)
			buff.append("Source: ").append(this.source).append("\n");
		if (this.sourceDetail != null && this.sourceDetail.length() > 0)
			buff.append("Source Detail: ").append(this.sourceDetail).append("\n");
		if (this.compoundProtection != null && this.compoundProtection.length() > 0)
			buff.append("Compound Protection: ").append(this.compoundProtection).append("\n");
		if (this.compoundState != null && this.compoundState.length() > 0)
			buff.append("Compound State: ").append(this.compoundState).append("\n");
		if (this.handling != null && this.handling.length() > 0) 
			buff.append("Handling: ").append(this.handling).append("\n");
		if (this.hazards != null && this.hazards.length() > 0)
			buff.append("Hazards: ").append(this.hazards).append("\n");
		if (this.storage != null && this.storage.length() > 0)
			buff.append("Storage: ").append(this.storage).append("\n");
		if (this.residualSolvents != null && this.residualSolvents.length() > 0)
			buff.append("Residual Solvents: ").append(this.residualSolvents).append("\n");
		if (this.structureComments != null && this.structureComments.length() > 0)
			buff.append("Structure Comments: ").append(this.structureComments).append("\n");
		return buff.toString();
	}
	
	public String toXml() {
		StringBuffer buff = new StringBuffer("<productBatch>");
		try {
			Class c = Class.forName(this.getClass().getName());
			Object me = this;
			Method[] allMethods = c.getDeclaredMethods();
			for (int i=0; i<allMethods.length; i++) {
				Method method = allMethods[i];
				String methodName = method.getName();
				if (methodName.startsWith("get")) {
					String fieldName = methodName.substring(3);
					fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
					buff.append("<").append(fieldName).append(">");
					try {
                        String value = (method.invoke(me, (Object[]) null)).toString();
                        if (!value.startsWith("<![CDATA")) {
                        	value = value.replaceAll("&", "&amp;");
                        	value = value.replaceAll("<", "&lt;");
                        }
                        buff.append(value);
					} catch (RuntimeException e) {
						buff.append("");
					}
					buff.append("</").append(fieldName).append(">");
				}
			}
			
		} catch (Exception e) {
			log_.error("Failed creating xml version of product batch.", e);
		}

        final String virtualCompoundId = originalBatchModel.getCompound().getVirtualCompoundId();

        if (StringUtils.isNotBlank(virtualCompoundId)) {
            buff.append("<virtualCompoundId>"  + virtualCompoundId +"</virtualCompoundId>");
        }

		buff.append("</productBatch>");
		return buff.toString();
	}

    private ProductBatchModel originalBatchModel;

    public ProductBatchModel getOriginalBatchModel() {
        return originalBatchModel;
    }

    public void setOriginalBatchModel(ProductBatchModel originalBatchModel) {
        this.originalBatchModel = originalBatchModel;
    }
}
