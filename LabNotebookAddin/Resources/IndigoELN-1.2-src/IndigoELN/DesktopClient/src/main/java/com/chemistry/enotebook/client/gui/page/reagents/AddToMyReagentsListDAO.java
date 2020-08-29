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
package com.chemistry.enotebook.client.gui.page.reagents;

import com.chemistry.enotebook.domain.BatchModel;
import org.apache.commons.lang.StringUtils;

public class AddToMyReagentsListDAO {
	private BatchModel batch = null;
	private String beginingOfReagent = null;
	private String reagentName = null;
	private String batchNumber = null;
	private String databaseName = null;  // FIXME assigned here only once!
	private String mfcdOrAcxID = null;
	private String casNumber = null;
	private String molecularFormula = null;
	private String molecularWeight = null;
	private String purity = null;
	private String density = null;
	private String concentration = null;
	private String resinLoading = null;
	private String saltCode = null;
	private String saltEquivs = null;
	private String registryNum = null;
	private String batchComment = null;
	private String batchHazardComment = null;
	private String reagentType = null;
	private String endingOfReagent = null;

	public AddToMyReagentsListDAO() {
		
	};

	public AddToMyReagentsListDAO(BatchModel batch) {
		this();
		if (batch != null) {
			this.batch = batch;
			init();
		}
	}

	private void init() {
		String regNumber = batch.getCompound().getRegNumber();
		boolean isCompoundIDSet = false;
		
		if (StringUtils.isNotBlank(regNumber) && regNumber.startsWith("ID")) {
			isCompoundIDSet = true;
		}
				
		// Begin of reagent
		beginingOfReagent = "<Reagent><Fields Database_Name=\"Stoichiometry Table\">";
		
		// Mandatory fields
		reagentName = "<Field Display_Name=\"Reagent Name\" Data_Type=\"java.lang.String\"><![CDATA[" + batch.getCompound().getChemicalName() + "]]></Field>";
		
		batchNumber = "<Field Display_Name=\"Batch Number\" Data_Type=\"java.lang.String\"><![CDATA[" + batch.getBatchNumberAsString() + "]]></Field>";
		
		if (isCompoundIDSet) {
			mfcdOrAcxID = "<Field Display_Name=\"External #\" Data_Type=\"java.lang.String\"><![CDATA[" + batch.getCompound().getRegNumber() + "]]></Field>";
		}
		
		casNumber = "<Field Display_Name=\"CAS Number\" Data_Type=\"java.lang.String\"><![CDATA[" + batch.getCompound().getCASNumber() + "]]></Field>";
		molecularFormula = "<Field Display_Name=\"Molecular Formula\" Data_Type=\"java.lang.String\"><![CDATA[" + batch.getMolecularFormula() + "]]></Field>";
		molecularWeight = "<Field Display_Name=\"Molecular Weight\" Data_Type=\"java.math.BigDecimal\"><![CDATA[" + batch.getMolWgt() + "]]></Field>";
		
		// Secondary fields
		purity = "<Field Display_Name=\"Purity\" Data_Type=\"java.math.BigDecimal\"><![CDATA[" + batch.getPurityAmount().getValue() + "]]></Field>";
		density = "<Field Display_Name=\"Density\" Data_Type=\"java.math.BigDecimal\"><![CDATA[" + batch.getDensityAmount().GetValueForDisplay() + "]]></Field>";
		concentration = "<Field Display_Name=\"Concentration\" Data_Type=\"java.math.BigDecimal\"><![CDATA[" + batch.getMolarAmount().getValue() + "]]></Field>";
		resinLoading = "<Field Display_Name=\"Resin Loading\" Data_Type=\"java.math.BigDecimal\"><![CDATA[" + batch.getLoadingAmount().getValue() + "]]></Field>";
		saltCode = "<Field Display_Name=\"Salt Code\" Data_Type=\"java.lang.String\"><![CDATA[" + batch.getSaltForm().getCode() + "]]></Field>";
		saltEquivs = "<Field Display_Name=\"Salt Equivs\" Data_Type=\"java.math.BigDecimal\"><![CDATA[" + batch.getSaltEquivs() + "]]></Field>";
		
		if (!isCompoundIDSet) {
			registryNum = "<Field Display_Name=\"Internal Registry #\" Data_Type=\"java.lang.String\"><![CDATA[" + batch.getCompound().getRegNumber() + "]]></Field>";
		}
		
		batchComment = "<Field Display_Name=\"Batch Comment\" Data_Type=\"java.lang.String\"><![CDATA[" + batch.getComments() + "]]></Field>";
		batchHazardComment = "<Field Display_Name=\"Batch Hazard Comment\" Data_Type=\"java.lang.String\"><![CDATA[" + batch.getHazardComments() + "]]></Field>";
		reagentType = "<Field Display_Name=\"Reagent Type\" Data_Type=\"java.lang.String\"><![CDATA[" + batch.getBatchType() + "]]></Field>";

		// End of reagent
		endingOfReagent = "</Fields></Reagent>";
	}

	private String buildReagentAsXMLString() {
		// Create the reagent to be appended to MyReagents List
		StringBuffer newReagent = new StringBuffer(1000);
		
		newReagent.append(beginingOfReagent);
		newReagent.append(reagentName);
		newReagent.append(batchNumber);
		newReagent.append(databaseName);
		
		if (mfcdOrAcxID != null) {
			newReagent.append(mfcdOrAcxID);
		}
		
		newReagent.append(casNumber);
		newReagent.append(molecularFormula);
		newReagent.append(molecularWeight);
		newReagent.append(purity);
		newReagent.append(density);
		newReagent.append(concentration);
		newReagent.append(resinLoading);
		newReagent.append(saltCode);
		newReagent.append(saltEquivs);
		
		if (registryNum != null) {
			newReagent.append(registryNum);
		}
		
		newReagent.append(batchComment);
		newReagent.append(batchHazardComment);
		newReagent.append(saltCode);
		newReagent.append(reagentType);
		newReagent.append(endingOfReagent);
		
		return newReagent.toString();
	}

	public String getReagentAsXMLString() {
		String result = buildReagentAsXMLString();
		return result;
	}
}
