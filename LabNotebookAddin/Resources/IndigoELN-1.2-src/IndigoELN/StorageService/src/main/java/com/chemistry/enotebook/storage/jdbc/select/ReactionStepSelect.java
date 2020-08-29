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

import com.chemistry.enotebook.domain.BatchesList;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.ReactionSchemeModel;
import com.chemistry.enotebook.domain.ReactionStepModel;
import com.chemistry.enotebook.experiment.utils.CeNXMLParser;
import com.chemistry.enotebook.storage.query.SelectQueryGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReactionStepSelect extends AbstractSelect {
	private static final Log log = LogFactory.getLog(ReactionStepSelect.class);
	private DataSource dataSource = null;

	public ReactionStepSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
		this.dataSource=dataSource;
		// declareParameter(new SqlParameter(Types.VARCHAR));
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		// System.out.println("ReactionStepModel record---:"+rowNum);

		ReactionStepModel reactionStep = new ReactionStepModel(rs.getString("STEP_KEY"));

		reactionStep.setStepNumber(rs.getInt("SEQ_NUM"));
		reactionStep.setRxnProperties(CeNXMLParser.getXmlProperty(rs.getString("XML_METADATA"), "/Step_Properties/Meta_Data/Rxn_Properties"));
		ReactionSchemeModel rxnScheme = new ReactionSchemeModel(rs.getString("RXN_SCHEME_KEY"),rs.getString("REACTION_TYPE"));
		
		rxnScheme.setNativeSketchFormat(rs.getString("NATIVE_RXN_SKTH_FRMT"));
		rxnScheme.setStringSketchFormat(rs.getString("RXN_SKTH_FRMT"));
		rxnScheme.setReactionType(rs.getString("REACTION_TYPE"));
		
		rxnScheme.setStringSketch(rs.getBytes("RXN_SKETCH"));
		rxnScheme.setNativeSketch(rs.getBytes("NATIVE_RXN_SKETCH"));
		rxnScheme.setViewSketch(rs.getBytes("SKETCH_IMAGE"));
		
		rxnScheme.setSythesisRouteReference(rs.getString("SYNTH_ROUTE_REF"));
		rxnScheme.setVrxId(rs.getString("VRXN_ID"));
		rxnScheme.setProtocolId(rs.getString("PROTOCOL_ID"));
		rxnScheme.setReactionId(rs.getString("REACTION_ID"));

		reactionStep.setRxnScheme(rxnScheme);
		
		//Filling Empty BatchesList in the reaction step
		String sqlQuery = SelectQueryGenerator.getBatchesListQueryForStep(reactionStep.getKey());
		BatchesListSelect batchesListSelect = new BatchesListSelect(this.dataSource,sqlQuery);
		List batchesList = batchesListSelect.execute();
		ArrayList productList = new ArrayList(batchesList.size());
		ArrayList monomerList = new ArrayList(batchesList.size());
		for(int i=0;i<batchesList.size();i++){
			BatchesList listObj = (BatchesList)batchesList.get(i);
			if(listObj.getPosition() != null){
				if(listObj.getPosition().startsWith("P")){
					productList.add(listObj);
				}else if(listObj.getPosition().equals(CeNConstants.STOIC_POSITION_CONSTANT)){
					reactionStep.setStoicBatchesList(listObj);
				}else{
					monomerList.add(listObj);
				}
			}
		}
		reactionStep.setMonomers(monomerList);
		reactionStep.setProducts(productList);
		reactionStep.getRxnScheme().setLoadedFromDB(true);
		reactionStep.getRxnScheme().setModelChanged(false);
		reactionStep.setLoadedFromDB(true);
		reactionStep.setModelChanged(false);
		return reactionStep;
	}
}
