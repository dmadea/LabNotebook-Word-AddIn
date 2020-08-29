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
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.MonomerBatchModel;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.utils.CeNXMLParser;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MonomerBatchSelect extends BatchSelect {
	private static final Log log = LogFactory.getLog(MonomerBatchSelect.class);

	public MonomerBatchSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
		// declareParameter(new SqlParameter(Types.VARCHAR));
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		try {
			String xmlMetadata = rs.getString("BATCH_XML_METADATA");
			
			String solute = CeNXMLParser.getXmlProperty(xmlMetadata, "/Batch_Properties/Meta_Data/Solute");
			String generated_products = CeNXMLParser.getXmlPropertyAsXml(xmlMetadata, "/Batch_Properties/Meta_Data/Generated_Products");
			String delivered_monomer_id = CeNXMLParser.getXmlProperty(xmlMetadata, "/Batch_Properties/Meta_Data/Delivered_Monomer");
			String total_volume_delivered = CeNXMLParser.getXmlProperty(xmlMetadata, "/Batch_Properties/Meta_Data/Total_Volume_Delivered");
			
			
			// System.out.println("ProductBatch record---:"+rowNum);
			BatchModel batch = (BatchModel) super.mapRow(rs, rowNum);
			MonomerBatchModel monomerBatch = new MonomerBatchModel(batch);
			monomerBatch.setSolute(solute);
			String generatedProducts = generated_products;
			monomerBatch.setGenratedProductBatchKeys(CommonUtils.convertXMLToArrayList(generatedProducts));
			monomerBatch.setNoOfTimesUsed(CommonUtils.toInteger(rs.getString("NO_OF_TIMES_USED")));
			monomerBatch.setDeliveredMonomerID(delivered_monomer_id);
//			log.info("Solute total Volume Delivered string value is: \"" + rs.getString("TOTAL_VOLUME_DELIVERED") + "\"" );
			monomerBatch.setTotalVolumeDelivered(new AmountModel(UnitType.VOLUME, 
																 CommonUtils.toDouble(total_volume_delivered)));
			monomerBatch.setLoadedFromDB(true);
			monomerBatch.setLoadingFromDB(false);
			monomerBatch.setModelChanged(false);
			return monomerBatch;
		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new SQLException(e.getMessage());
		}
	}

	
	
	

}
