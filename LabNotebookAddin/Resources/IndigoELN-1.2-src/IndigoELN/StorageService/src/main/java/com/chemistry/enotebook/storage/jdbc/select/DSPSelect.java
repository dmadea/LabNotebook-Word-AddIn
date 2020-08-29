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
package com.chemistry.enotebook.storage.jdbc.select;

import com.chemistry.enotebook.domain.AmountModel;
import com.chemistry.enotebook.domain.DesignSynthesisPlan;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.utils.CeNXMLParser;
import com.chemistry.enotebook.utils.CommonUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DSPSelect extends AbstractSelect {
	public DSPSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		String xmlMetadata = rs.getString("XML_METADATA");

		String COMMENTS = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/DSP/Comments");
		String DESCRIPTION = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/DSP/Description");
		String DESIGN_USERS = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/DSP/designUsers");
		String SCREEN_PANELS = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/DSP/ScreenPanels");
		String DESIGN_CREATION_DATE = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/DSP/DesignCreationDate");
		String PROTOTYPE_LEAD_IDS = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/DSP/PrototypeLeadIDs");
		String VRXNID = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/DSP/VrxnID");
		String DESIGN_SITE = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/DSP/DesignSite");
		
		DesignSynthesisPlan plan = new DesignSynthesisPlan();

		plan.setSpid(rs.getString("SPID"));
		NotebookRef nbref = new NotebookRef();
		try {
			nbref.setNbNumber(rs.getString("NOTEBOOK"));
			nbref.setNbPage(rs.getString("EXPERIMENT"));
		} catch (Exception error) {
			error.printStackTrace();
		}
		plan.setNotebookId(nbref.toString());
		plan.setSeriesId(rs.getString("SERIES_ID"));
		plan.setProtocolId(rs.getString("PROTOCOL_ID"));
		plan.setTaCode(rs.getString("TA_CODE"));
		plan.setProjectCode(rs.getString("PROJECT_CODE"));
		plan.setComments(COMMENTS);
		plan.setDescription(DESCRIPTION);
		String[] designUsers = new String[1];
		designUsers[0] = DESIGN_USERS;
		plan.setDesignUsers(designUsers);
		String[] screenPanels = new String[1];
		screenPanels[0] = SCREEN_PANELS;
		plan.setScreenPanels(screenPanels);
		plan.setScaleAmount(this.getScaleAmount(rs));
		plan.setDateCreated(DESIGN_CREATION_DATE);
		String[] prototypeLeadIds = new String[1];
		prototypeLeadIds[0] = PROTOTYPE_LEAD_IDS;
		plan.setPrototypeLeadIds(prototypeLeadIds);
		plan.setVrxnId(VRXNID);
		plan.setSite(DESIGN_SITE);
		plan.setUserName(rs.getString("DESIGN_SUBMITTER"));
		
		plan.setLoadedFromDB(true);
		plan.setModelChanged(false);
		return plan;
	}
	
	private AmountModel getScaleAmount(ResultSet rs) throws SQLException {
		String xmlMetadata = rs.getString("XML_METADATA");

		String SCALE_AMOUNT_CALCULATED = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/DSP/Scale/Calculated");
		String SCALE_AMOUNT_DEFAULT_VALUE = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/DSP/Scale/Default_Value");
		String SCALE_AMOUNT_UNIT_CODE = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/DSP/Scale/Unit/Code");
		String SCALE_AMOUNT_UNIT_DESC = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/DSP/Scale/Unit/Description");
		String SCALE_AMOUNT_VALUE = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/DSP/Scale/Value");
				
		String unitCode = SCALE_AMOUNT_UNIT_CODE;
		Unit2 unit = new Unit2(UnitType.MOLES, unitCode);
		unit.setDescription(SCALE_AMOUNT_UNIT_DESC);

		AmountModel scaleAmt = new AmountModel(UnitType.MOLES);
		scaleAmt.setUnit(unit);
		scaleAmt.setCalculated(CommonUtils.toBoolean(SCALE_AMOUNT_CALCULATED));
		scaleAmt.setDefaultValue(CommonUtils.toDouble(SCALE_AMOUNT_DEFAULT_VALUE));
		scaleAmt.setValue(CommonUtils.toDouble(SCALE_AMOUNT_VALUE));

		return scaleAmt;
	}
}
