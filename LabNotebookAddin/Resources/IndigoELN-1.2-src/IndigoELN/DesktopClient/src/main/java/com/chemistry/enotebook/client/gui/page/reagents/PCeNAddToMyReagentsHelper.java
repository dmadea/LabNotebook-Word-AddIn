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
import com.chemistry.enotebook.domain.MonomerBatchModel;
import com.chemistry.enotebook.domain.SaltFormModel;
import com.chemistry.enotebook.domain.StoicModelInterface;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.utils.xml.JDomUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;

import javax.swing.*;
import java.util.List;

public class PCeNAddToMyReagentsHelper {
	
	public static final Log log = LogFactory.getLog(PCeNAddToMyReagentsHelper.class);
	
	private static final String START_REAGENTS_TAG = "<Reagents>";
	private static final String END_REAGENTS_TAG = "</Reagents>";
	private static final String EMPTY_REAGENTS_TAG = "<Reagents />";
	
	public PCeNAddToMyReagentsHelper() {
		
	}

	private BatchModel validateStociModel(StoicModelInterface stoicModel) throws Exception {
		BatchModel result = (BatchModel) stoicModel;

		result.setBatchType(BatchType.getBatchType(stoicModel.getStoicReactionRole()));
		result.setComments(stoicModel.getStoichComments());
		result.setHazardComments(stoicModel.getStoicHazardsComments());
		result.setPurity(stoicModel.getStoicPurityAmount().GetValueInStdUnitsAsDouble());
		result.setDensityAmount(stoicModel.getStoicDensityAmount());
		
		if (stoicModel instanceof MonomerBatchModel) {
			MonomerBatchModel stoicMonomerModel = (MonomerBatchModel) stoicModel;
			SaltFormModel saltForm = stoicMonomerModel.getSaltForm();
			result.setSaltForm(new SaltFormModel(saltForm.getCode(), saltForm.getDescription(), saltForm.getFormula(), saltForm.getMolWgt()));
			result.setSaltEquivs(stoicMonomerModel.getSaltEquivs());
			result.setSaltEquivsSet(stoicMonomerModel.getSaltEquivsSet());
			result.setMolWgt(stoicMonomerModel.getMolWgt());
		}		
		
		if (StringUtils.isBlank(result.getCompound().getChemicalName()) &&
		    StringUtils.isBlank(result.getCompound().getCASNumber()) &&
		    StringUtils.isBlank(result.getCompound().getRegNumber())) 
		{
			result = null;
			JOptionPane.showMessageDialog(null,	"Please enter at least one of the following fields: Chemical Name, CAS Number, or Compound ID",	"Missing Mandatory Batch Fields", JOptionPane.ERROR_MESSAGE);
		}
		
		return result;
	}

	public void addToMyReagentsList(StoicModelInterface stoicModel) throws Exception {
		BatchModel batch = validateStociModel(stoicModel);
		StringBuffer myReagentsList = new StringBuffer(AddToMyReagentsListHelper.getMyReagentsListFromDB());
		String newReagent = null;
		if (!reagentExists(batch.getCompound().getChemicalName(), batch.getCompound().getRegNumber(), batch.getBatchType().toString(), myReagentsList.toString())) {
			int startIndexOfEndReagentsTAG = myReagentsList.indexOf(PCeNAddToMyReagentsHelper.END_REAGENTS_TAG);
			if (startIndexOfEndReagentsTAG < 0) { // Probably empty xml "Reagents" tag
				int startIndexOfEmptyReagentsTag = myReagentsList.indexOf(EMPTY_REAGENTS_TAG);
				int endIndexOfEmptyReagentsTag = startIndexOfEmptyReagentsTag + EMPTY_REAGENTS_TAG.length();
				if (startIndexOfEmptyReagentsTag > -1) {
					myReagentsList.delete(startIndexOfEmptyReagentsTag, endIndexOfEmptyReagentsTag);
					myReagentsList.append(START_REAGENTS_TAG);
					myReagentsList.append(END_REAGENTS_TAG);
					startIndexOfEndReagentsTAG = myReagentsList.indexOf(PCeNAddToMyReagentsHelper.END_REAGENTS_TAG);
				}
			}
			int endIndexOfEndReagentsTAG = myReagentsList.length();
			if (batch != null) {
				newReagent = getBatchAsXMLString(batch);
				myReagentsList.replace(startIndexOfEndReagentsTAG, endIndexOfEndReagentsTAG, "");
				myReagentsList.append(newReagent);
				myReagentsList.append(PCeNAddToMyReagentsHelper.END_REAGENTS_TAG);
				AddToMyReagentsListHelper.updateMyReagentsList(myReagentsList.toString());
			}
		} else {
			JOptionPane.showMessageDialog(null, "This Reagent already exist in Your Reagents' List", "Duplicate Reagent",
					JOptionPane.INFORMATION_MESSAGE);
		}
	} // end method

	private String getBatchAsXMLString(BatchModel batch) {
		String result = null;
		result = AddToMyReagentsListHelper.getBatchAsXMLString(batch);
		return result;
	}

	private boolean reagentExists(String name, String id, String type, String reagentList) {
		try {
			// We need to parse XML to compare by (Name or Id) with Type compare	
			
			// Reagent list is:
			// <Reagents><Reagent><Fields> <Field>value</Field> <Field>value</Field> </Fields></Reagent></Reagents>
			
			Document reagentListDoc = JDomUtils.getDocFromString(reagentList);
			List<Element> reagents = JDomUtils.getChildNodes(reagentListDoc, "/Reagents/Reagent");
			
			for (Content reagent : reagents) {  // "<Reagent>"
				if (reagent instanceof Element) {
					List<Content> fields = ((Element)reagent).getContent();
					
					String reagentName = "";
					String reagentId = "";
					String reagentType = "";
							
					for (Content field : fields) { // "<Fields>"
						if (field instanceof Element) {
							List<Content> fieldContents = ((Element)field).getContent();							
							for (Content fieldContent : fieldContents) { // "<Field>"
								if (fieldContent instanceof Element) {
									String displayName = ((Element)fieldContent).getAttributeValue("Display_Name");
									String value = ((Element)fieldContent).getValue();									
									if (StringUtils.equals("Internal Registry #", displayName)) {
										reagentId = value;
									}
									if (StringUtils.equals("Reagent Name", displayName)) {
										reagentName = value;
									}
									if (StringUtils.equals("Reagent Type", displayName)) {
										reagentType = value;
									}
								}
							}
						}
					}
					
					if (StringUtils.equals(reagentType, type)) {
						if (StringUtils.isNotBlank(name) && StringUtils.equals(reagentName, name)) {
							return true;
						}
						if (StringUtils.isNotBlank(id) && StringUtils.equals(reagentId, id)) {
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Error parsing Reagent List as XML: ", e);
			// XML parsing failed, trying the old method, without Type comparing
			if (StringUtils.isNotBlank(reagentList)) {
				if (StringUtils.isNotBlank(name) && StringUtils.contains(reagentList, name)) {
					return true;
				}
				if (StringUtils.isNotBlank(id) && StringUtils.contains(reagentList, id)) {
					return true;
				}
			}
		}
		if(StringUtils.isBlank(name) && StringUtils.isBlank(id)) {
			return true;
		}
		return false;
	}
}
