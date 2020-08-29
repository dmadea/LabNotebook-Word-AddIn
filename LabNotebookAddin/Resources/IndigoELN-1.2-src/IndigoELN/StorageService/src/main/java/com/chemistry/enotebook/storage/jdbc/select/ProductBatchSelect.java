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

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.utils.CeNXMLParser;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductBatchSelect extends BatchSelect {
	private int count = 0;
	private long totalTime= 0;
	private RegisteredBatchSelect rBatchSelect = null;
	private static final Log log = LogFactory.getLog(ProductBatchSelect.class);
	public ProductBatchSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
		rBatchSelect = new RegisteredBatchSelect(dataSource,sqlQuery);
		// declareParameter(new SqlParameter(Types.VARCHAR));
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		long starttime = System.currentTimeMillis();

		String xmlMetadata = rs.getString("BATCH_XML_METADATA");
		
		String enumeration_sequence = CeNXMLParser.getXmlProperty(xmlMetadata, "/Batch_Properties/Meta_Data/Enumeration_Sequence");
		String precursors = CeNXMLParser.getXmlPropertyAsXml(xmlMetadata, "/Batch_Properties/Meta_Data/Precursors");
		String reactants_for_product = CeNXMLParser.getXmlPropertyAsXml(xmlMetadata, "/Batch_Properties/Meta_Data/Reactants_For_Product");
		String analytical_purity_list = CeNXMLParser.getXmlPropertyAsXml(xmlMetadata, "/Batch_Properties/Meta_Data/Analytical_Purity_List");
		String analytical_comment = CeNXMLParser.getXmlProperty(xmlMetadata, "/Batch_Properties/Meta_Data/Analytical_Comment");
		
		BatchModel batch = (BatchModel) super.mapRow(rs, rowNum);
		ProductBatchModel productBatch = new ProductBatchModel(batch);
		//productBatch.setTheoreticalYieldPercent(CommonUtils.toDouble(rs.getString("THEORITICAL_YIELD_PERCENT")));
		productBatch.setRegInfo((BatchRegInfoModel)rBatchSelect.mapRow(rs, rowNum));
		productBatch.setPrecursors(CommonUtils.convertXMLToArrayList(precursors));
		productBatch.setReactantBatchKeys(CommonUtils.convertXMLToArrayList(reactants_for_product));
		productBatch.setSelectivityStatus(CommonUtils.toInteger(rs.getString("SELECTIVITY_STATUS")));
		productBatch.setContinueStatus(CommonUtils.toInteger(rs.getString("CONTINUE_STATUS")));
		
		productBatch.setAnalyticalPurityList(getAnalyticalPurityList(analytical_purity_list));
		productBatch.setAnalyticalComment(analytical_comment);
		//productBatch.setScreenPanelIds(getScreenPanels(rs));
		/*
		 * private boolean productFlag = true; // Use to indicate if this was a targeted product (was what user wants to identify as
		 * a final product)
		 * 
		 * private ArrayList analyticalPurityList = new ArrayList(); // Holds all purity object which indicate the techniques used
		 * to get purity designation for this batch. private BatchRegInfoModel regInfo = new BatchRegInfoModel(); private int
		 * intendedBatchAdditionOrder = 0;
		 * 
		 * private String parentKey; //GUID of its parent batch (for ActualProd it will be key to corresponding IntProd)
		 * 
		 * //Note: Use RegNumber in Compound object instead //private String compoundId; private String productId; private String
		 * synthesisPlanProductId; private int enumerationSequence; private boolean selected; private String annotation; private boolean
		 * registered; private int priority; private int occuredInStepNumber;
		 */
		String enumSequence = enumeration_sequence;
		if(enumSequence != null && !enumSequence.equals(""))
		{
			productBatch.setEnumerationSequence(Integer.parseInt(enumSequence));	
		}
		productBatch.setLoadedFromDB(true);
		productBatch.setLoadingFromDB(false);
		productBatch.setModelChanged(false);
		
		count++;
		long endtime = (System.currentTimeMillis()-starttime);
		totalTime = totalTime+endtime;
		//System.out.println(count+") time: "+totalTime+"ms ");
		return productBatch;
	}

	private long[] getScreenPanels(ResultSet rs) throws SQLException{
		String screenPanels = rs.getString("SCREEN_PANELS");
		ArrayList list = CommonUtils.convertXMLToArrayList(screenPanels);
		long[] screenPanelIds = new long[list.size()];
		for(int i=0;i<list.size();i++){
			try{
				screenPanelIds[i] = Long.parseLong((String)list.get(i));
			}catch (Exception error){
				//Ignore the exception, and move on.
				log.error(error);
			}
		}
		return screenPanelIds;
	}
	
	/*
	 * returns BatchVnVInfoModel
	 * 
	 */
	private BatchVnVInfoModel getBatchVnVInfoModel(ResultSet rs) throws SQLException {
		BatchVnVInfoModel regBatchVnVInfo = new BatchVnVInfoModel();
		regBatchVnVInfo.setStatus(rs.getString("VNV_STATUS"));
		regBatchVnVInfo.setMolData(rs.getString("VNV_MOL_DATA"));
		String sicList = rs.getString("VNV_SUGGESTED_SIC_LIST");
		regBatchVnVInfo.setSuggestedSICList(CommonUtils.convertXMLToArrayList(sicList));
		regBatchVnVInfo.setErrorMsg(rs.getString("VNV_ERROR_MSG"));

		return regBatchVnVInfo;
	}
	
	public ArrayList getAnalyticalPurityList(String xml) {
		ArrayList purityList = new ArrayList();
		boolean taskPending = true;
		String purityXml = null;
		int cutOffIndex = 0;
		if (xml == null)
			return purityList;
		// System.out.println(xml);
		if (xml.trim().equals("<Analytical_Purity_List/>")) {
			return purityList;
		}
		//removing Purity_List tags from XML
		if (xml.indexOf("<Analytical_Purity_List>") >= 0) {
			xml = xml.substring(xml.indexOf("<Analytical_Purity_List>") + 24, xml.indexOf("</Analytical_Purity_List>"));
			
			if (xml.trim().length() == 0)
				return purityList;
			
			while (taskPending) {
				cutOffIndex = xml.indexOf("</Entry>") + 8;
				// extracting Purity tags
				purityXml = xml.substring(xml.indexOf("<Entry>"), cutOffIndex);
				try {
					PurityModel model = getPurityModelFromXML(purityXml);
					if (model != null)
						purityList.add(model);
				} catch (Exception error) {
					log.error(CommonUtils.getStackTrace(error));
				}
				// extracting remaining xml
				xml = xml.substring(cutOffIndex);
				// Checking for 15 chars, "<Entry></Entry>"
				if (xml.length() < 15) {
					taskPending = false;
				}
			}
		}
		return purityList;
	}

	private PurityModel getPurityModelFromXML(String xml) throws Exception{
		if(xml.length() == 0) return null;
		String sourceFile = "";
		String dateMeasured = "";
		String comments = "";
		try {
			String genericCode = xml.substring(xml.indexOf("<Code>")+6,xml.indexOf("</Code>")); 
			String genericDesc = xml.substring(xml.indexOf("<Description>")+13,xml.indexOf("</Description>")); 
			AmountModel purityAmount = 
				getPurityAmountFromXML(xml.substring(xml.indexOf("<Purity_Value>"),xml.indexOf("</Purity_Value>")));
			String operator = xml.substring(xml.indexOf("<Operator>")+10,xml.indexOf("</Operator>")); 
			if(xml.indexOf("<DateMeasured/>")<0)
			{
			dateMeasured = xml.substring(xml.indexOf("<DateMeasured>")+14,xml.indexOf("</DateMeasured>"));
			}
			if(xml.indexOf("<SourceFile/>")<0)
			{
			sourceFile = xml.substring(xml.indexOf("<SourceFile>")+12,xml.indexOf("</SourceFile>")); 
			}
			String isRepresentativePurity = xml.substring(xml.indexOf("<IsRepresentativePurity>")+24,xml.indexOf("</IsRepresentativePurity>"));
			if(xml.indexOf("<Comments/>")<0)
			{
			comments = xml.substring(xml.indexOf("<Comments>")+10,xml.indexOf("</Comments>")); 
			}
			PurityModel purityModel = new PurityModel();
			purityModel.setCode(genericCode);
			purityModel.setDescription(genericDesc);
			purityModel.setPurityValue(purityAmount);
			purityModel.setDate(CommonUtils.toDate(dateMeasured));
			purityModel.setOperator(CommonUtils.getActualSymbolfromMaskedString(operator));
			purityModel.setSourceFile(sourceFile);
			purityModel.setRepresentativePurity(CommonUtils.toBoolean(isRepresentativePurity));
			purityModel.setComments(comments);
			return purityModel;
		} catch (Exception error){
			error.printStackTrace();
			return null;
		}
		
	}
	
	private AmountModel getPurityAmountFromXML(String xmlString) throws Exception{
		String unitCode = "";
		String unitDescription = "";
		if(xmlString.indexOf("<Code>") >0)
		unitCode = xmlString.substring(xmlString.indexOf("<Code>")+6,xmlString.indexOf("</Code>"));
		if(xmlString.indexOf("<Description>") > 0)
		unitDescription = xmlString.substring(xmlString.indexOf("<Description>")+13,xmlString.indexOf("</Description>"));
		Unit2 unit = new Unit2(UnitType.SCALAR,unitCode);
		unit.setDescription(unitDescription);
		String calculated = xmlString.substring(xmlString.indexOf("<Calculated>")+12,xmlString.indexOf("</Calculated>"));
		String defaultValue = xmlString.substring(xmlString.indexOf("<Default_Value>")+15,xmlString.indexOf("</Default_Value>"));
		String value = xmlString.substring(xmlString.indexOf("<Value>")+7,xmlString.indexOf("</Value>"));
		AmountModel purityAmt = new AmountModel(UnitType.SCALAR);
		purityAmt.setUnit(unit);
		purityAmt.setCalculated(CommonUtils.toBoolean(calculated));
		purityAmt.setDefaultValue(CommonUtils.toDouble(defaultValue));
		purityAmt.setValue(CommonUtils.toDouble(value));
		return purityAmt;
	}
	

	
}
