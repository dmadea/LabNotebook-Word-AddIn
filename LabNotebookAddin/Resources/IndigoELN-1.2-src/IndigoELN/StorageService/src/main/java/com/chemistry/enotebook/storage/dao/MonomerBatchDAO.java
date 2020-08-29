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
package com.chemistry.enotebook.storage.dao;

import com.chemistry.enotebook.domain.MonomerBatchModel;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.JDBCRuntimeException;
import com.chemistry.enotebook.storage.jdbc.insert.BatchAmountInsert;
import com.chemistry.enotebook.storage.jdbc.insert.BatchInsert;
import com.chemistry.enotebook.storage.jdbc.insert.StructureInsert;
import com.chemistry.enotebook.util.Stopwatch;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.util.List;

public class MonomerBatchDAO extends BatchDAO {

	private static final Log log = LogFactory.getLog(MonomerBatchDAO.class);

	public void insertMonomerBatchesThruStoredProcedure(String stepKey, String pageKey, List<MonomerBatchModel> batches) throws DAOException {
		log.debug(" INSIDE insertMonomerBatchesThruStoredProcedure");

		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("NotebookInsertDAO.insertMonomerBatches()");

		Connection dbCon = null;
		
		try {
			log.debug(" creating Object Array for monomer Batches size :"+batches.size());

			dbCon = getConnection();
			
			String batchNumber = "";
			String batchType = "";
			String structKey = "";
			String saltCode = "";
						
			StructureInsert si = new StructureInsert(getDataSource());
			BatchInsert bi = new BatchInsert(getDataSource());
			BatchAmountInsert bai = new BatchAmountInsert(getDataSource());
			
			for (MonomerBatchModel batch : batches) {				
				if (CommonUtils.isNotNull(batch.getBatchNumber()))
					batchNumber = batch.getBatchNumber().getBatchNumber();
				else
					batchNumber = "";

				if (CommonUtils.isNotNull(batch.getBatchType()))
					batchType = batch.getBatchType().toString();
				else
					batchType = "";

				if (CommonUtils.isNotNull(batch.getCompound()))
					structKey = batch.getCompound().getKey();
				else
					structKey = "";

				if (CommonUtils.isNotNull(batch.getSaltForm()))
					saltCode = batch.getSaltForm().getCode();
				else
					saltCode = "";
				
				Object[] batchObjValues = new Object[] {
						batch.getKey(), // BATCH_KEY
						pageKey, // PAGE_KEY
						batchNumber, // BATCH_NUMBER
						structKey, // STRUCT_KEY
						null, // XML_METADATA
						stepKey, // STEP_KEY
						batchType, // BATCH_TYPE
						batch.getMolecularFormula(), // MOLECULAR_FORMULA
						null,
						saltCode, // SALT_CODE
						new Double(batch.getSaltEquivs()), // SALT_EQUIVS
						batch.getListKey(), // LIST_KEY
						new Double(batch.getMolecularWeightAmount().doubleValue()), // VALUE
						batch.getMolecularWeightAmount().getUnit().getCode(), // UNIT_CODE
						new Character(batch.getMolecularWeightAmount().isCalculated()== true?'Y':'N'), // CALCULATED
						new Integer(batch.getMolecularWeightAmount().getSigDigits()), // SIG_DIGITS
						new Character(batch.getMolecularWeightAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
						new Integer(batch.getMolecularWeightAmount().getUserPrefFigs()), // USER_PREF_FIGS
						new String(batch.isLimiting()== true?"Y":"N") ,
						new String(batch.isAutoCalcOn()== true?"Y":"N"),
						batch.getSynthesizedBy(),
						batch.getSolventsAdded(),
						new Integer(batch.getNoOfTimesUsed()),
						new Integer(batch.getTransactionOrder()),
					    batch.getChloracnegenType(),
					    new String(batch.isChloracnegen()== true?"Y":"N") ,
					    new String(batch.isTestedForChloracnegen()== true?"Y":"N")
					};
				
				si.update(getOraCompound(batch.getCompound(), pageKey));
				bi.update(batchObjValues);
				bai.update(getOraMonomerBatchAmount(batch, pageKey));
			}
			
			si.flush();
			bi.flush();
			bai.flush();
			
			for (MonomerBatchModel b : batches) {
				updateBatchWithCompound(b, dbCon);
			}			
		} catch (Exception insertError) {
			log.error(CommonUtils.getStackTrace(insertError));
			throw new JDBCRuntimeException(insertError);
		} finally {
			stopwatch.stop();
			log.debug("Close ORACLE Connection in finally block: ");
			closeConnection(dbCon);
		}
	}
}
