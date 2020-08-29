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

import com.chemistry.enotebook.domain.BatchSubmissionContainerInfoModel;
import com.chemistry.enotebook.domain.BatchSubmissionToScreenModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReactionStep {
	
	private int stepNumber;
	private List<StoichiometryData> stoichList = new ArrayList<StoichiometryData>();
	private List<MPlate> monomerPlates = new ArrayList<MPlate>();
	private List<MonomerBatchList> monomerBatchLists = new ArrayList<MonomerBatchList>();
	private List<PrintableProductBatch> productBatches = new ArrayList<PrintableProductBatch>();
	private String reactionImageUri = "";

    private static HashMap<String, String> compoundProtections = new HashMap<String, String>();

    private static final String NO_APPROVAL_NEEDED = "None (no approval needed)";

    static {
        compoundProtections.put("BS","Standard (requests for any amount - six months");
        compoundProtections.put("NONE", NO_APPROVAL_NEEDED);
        compoundProtections.put("BC","Curation (requests for >10 mg or 10% of remaining balance, never expires");
        compoundProtections.put("BSC", "Standard protection for 6 months, then curation protections");
    }
	
	public void dispose() {
		if (stoichList != null) {
			for (StoichiometryData s : stoichList)
				s.dispose();
			stoichList.clear();
		}
		if (monomerPlates != null) {
			for (MPlate p : monomerPlates)
				p.dispose();
			monomerPlates.clear();
		}

	}
	
	public int getStepNumber() {
		return stepNumber;
	}
	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}
	public List<StoichiometryData> getStoichList() {
		return stoichList;
	}
	public void setStoichList(List<StoichiometryData> stoichList) {
		this.stoichList = stoichList;
	}
	public List<MPlate> getMonomerPlates() {
		return monomerPlates;
	}
	public void setMonomerPlates(List<MPlate> monomerPlates) {
		this.monomerPlates = monomerPlates;
	}
	public String getStepName() {
		if (stepNumber == 0)
			return "" + ++stepNumber;
		return "" + stepNumber;
	}
	public List<MonomerBatchList> getMonomerBatchLists() {
		return monomerBatchLists;
	}
	public void setMonomerBatchLists(List<MonomerBatchList> monomerBatchLists) {
		this.monomerBatchLists = monomerBatchLists;
	}
	public void addMonomerBatchList(MonomerBatchList monomerBatchList) {
		this.monomerBatchLists.add(monomerBatchList);
	}
	public void setProductBatches(List<PrintableProductBatch> productBatches) {
		this.productBatches = productBatches;
	}
	public void addProductBatch(PrintableProductBatch batch) {
		this.productBatches.add(batch);
	}
	public String getReactionImageUri() {
		return reactionImageUri;
	}
	public void setReactionImageUri(String reactionImageUri) {
		this.reactionImageUri = reactionImageUri;
	}
	public String toXml() {
		StringBuffer buff = new StringBuffer("<reactionStep>");
		buff.append("<stepName>").append(this.getStepName()).append("</stepName>");
		buff.append("<reactionImageUri>").append(this.getReactionImageUri()).append("</reactionImageUri>");
		// stoichiometry
		buff.append("<stoichDataList>");
		for (StoichiometryData stoichData : stoichList) {
			buff.append(stoichData.toXml());
		}
		buff.append("</stoichDataList>");
		// monomer plates
		buff.append("<monomerPlates>");
		for (MPlate mPlate : monomerPlates) {
			buff.append(mPlate.toXml());
		}
		buff.append("</monomerPlates>");
		// monomer batch lists
		buff.append("<monomerBatchLists>");
		for (MonomerBatchList monomerBatchList : monomerBatchLists) {
			buff.append(monomerBatchList.toXml());
		}
		buff.append("</monomerBatchLists>");	
		// product batches
		buff.append("<productBatches>");
		for (PrintableProductBatch printableProductBatch : productBatches) {
			buff.append(printableProductBatch.toXml());
		}
		buff.append("</productBatches>");
		buff.append("</reactionStep>");
        buff.append("<containers>");
        for (PrintableProductBatch printableProductBatch : productBatches) {
            ProductBatchModel batch = printableProductBatch.getOriginalBatchModel();
            ArrayList<BatchSubmissionContainerInfoModel> containers = batch.getRegInfo().getSubmitContainerList();
            if (containers.size() > 0) {
                String code = batch.getProtectionCode();
                if (StringUtils.isBlank(code)) {
                    code = NO_APPROVAL_NEEDED;
                } else {
                    String description = compoundProtections.get(code);
                    if (description != null)
                        code = description;
                }
                buff.append("<row><protection>" + code + "</protection></row>");
            }

            for (BatchSubmissionContainerInfoModel container : containers) {
                buff.append("<row>");
                addField(buff, "batch", batch.getBatchNumberAsString());
                addField(buff, "type", container.getContainerType());
                addField(buff, "barcode", container.getBarCode());
                addField(buff, "amount", container.getAmountValue() + " " + container.getAmountUnit());
                buff.append("</row>");
            }
        }
        buff.append("</containers>");

        buff.append("<samples>");
        for (PrintableProductBatch printableProductBatch : productBatches) {
            ProductBatchModel batch = printableProductBatch.getOriginalBatchModel();
            for (BatchSubmissionToScreenModel sample : batch.getRegInfo().getNewSubmitToBiologistTestList()) {
                buff.append("<sample>");
                addField(buff, "batch", batch.getBatchNumberAsString());
                addField(buff, "code", sample.getScreenCode());
                addField(buff, "name", sample.getScreenProtocolTitle());
                addField(buff, "site", sample.getSiteCode() );
                addField(buff, "recipient", sample.getScientistCode());
                double amount = sample.getAmountValue();
                if (amount > 0) {
                    addField(buff, "amount", amount + " " + sample.getAmountUnit());
                }
                addField(buff, "container", sample.getContainerType());
                buff.append("</sample>");
            }
        }
        buff.append("</samples>");
		return buff.toString();
	}

    private void addField(StringBuffer buff, String tag, String value) {
        if (value != null)
            buff.append("<" + tag + ">" + value + "</" + tag + ">");
    }

}
