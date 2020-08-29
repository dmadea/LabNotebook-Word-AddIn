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
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.JDBCRuntimeException;
import com.chemistry.enotebook.storage.jdbc.insert.ListInsert;
import com.chemistry.enotebook.storage.jdbc.insert.StepBatchListInsert;
import com.chemistry.enotebook.storage.query.InsertQueryGenerator;
import com.chemistry.enotebook.storage.query.UpdateQueryGenerator;
import com.chemistry.enotebook.util.Stopwatch;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.SqlUpdate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class StorageDAO implements InitializingBean {

	private DataSource dataSource = null;
	// private static ApplicationContext context = null;
	private static final Log log = LogFactory.getLog(StorageDAO.class);

	public StorageDAO(){

	}
	public void afterPropertiesSet() throws Exception {
		if (this.dataSource == null) {
			throw new BeanCreationException("Must set dataSource on test Dao");
		}

	}

	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		return this.dataSource;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	protected Connection getConnection() throws SQLException {
		return this.getDataSource().getConnection();
	}

	/*
	 * Creates an SqlObject using the JdbcTemplate and
	 * the updateQuery sql string.
	 * @param updateQuery contains SQL query.
	 */
	protected SqlUpdate getSqlUpdate(String updateQuery)
		throws Exception {
		try{
			JdbcTemplate jt = new JdbcTemplate();
			jt.setDataSource(this.getDataSource());
			SqlUpdate su = new SqlUpdate();
			su.setJdbcTemplate(jt);
			su.setSql(updateQuery);
			return su;
		}catch(Exception error){
			throw error;
		}
	}

	public void closeConnection(Connection con) {
		try {
			log.debug("close JDBC Connection called");
			if (con != null) {
				con.close();
				log.debug("closed JDBC Connection successfully.");
			} else {
				log.debug("Failed to close JDBC Connection: Expect ConnectionLeak");
			}

		} catch (SQLException err) {
			log.error("Exception while closing JDBC Connection :"+err.getMessage());
			err.printStackTrace();
		}
	}

	/**
	 * @return the daoFactory
	 */
	public DAOFactory getDaoFactory() throws DAOException{
		DAOFactory daoFactory = DAOFactoryManager.getDAOFactory();
		return daoFactory;
	}

	/**
	 * @param daoFactory the daoFactory to set
	 */
	public void releaseDaoFactory(DAOFactory daoFactory ) {
		if(daoFactory != null){
			DAOFactoryManager.release(daoFactory);
		}
	}

	/**
	 * Inserts Compound into CEN_Step_Batches_List table and returns boolean indicating the success or failure of the insert
	 * operation.
	 *
	 * @return boolean true if insert was success.
     * @throws JDBCRuntimeException
	 */
	public void insertStepBatchesList(String pageKey, String stepKey, String listKey, String position) {

		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageDAO.insertStepBatchesList()");
		log.debug("Inserting Step Batch at position : " + position);
		String insertSql = InsertQueryGenerator.getInsertStepBatchListQuery();
		StepBatchListInsert insert = new StepBatchListInsert(this.getDataSource(), insertSql);

		try {
			// Inserting Batches List
			insert.update(new Object[] { stepKey, // STEP_KEY
					listKey, // LIST_KEY
					StringUtils.isBlank(position) ? null : position, // POSITION
					pageKey // PAGE_KEY
					});
		} catch (Exception insertError) {
			throw new JDBCRuntimeException(insertError);
		} finally {
			stopwatch.stop();
		}

	}

	/**
	 * Inserts Compound into CEN_Lists table and returns boolean indicating the success or failure of the insert operation.
	 *
     * @throws JDBCRuntimeException
	 */
	public void insertList(String listKey, String listName) {
		String insertSql = InsertQueryGenerator.getInsertListsQuery();
		ListInsert insert = new ListInsert(this.getDataSource(), insertSql);

		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageDAO.insertList()");

		try {
			insert.update(new Object[] { listKey, // STEP_KEY
					listName // LIST_NAME
					});
		} catch (Exception insertError) {
			try {
				updateList(listKey, listName);
			} catch (Exception ignore) {
				throw new JDBCRuntimeException(insertError);
			}
		} finally {
			stopwatch.stop();
		}
	}
	
	public void updateList(String listKey, String listName) {
		String insertSql = UpdateQueryGenerator.getUpdateListsQuery();
		ListInsert update = new ListInsert(this.getDataSource(), insertSql);

		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageDAO.updateList()");

		try {
			update.update(new Object[] { listKey, // STEP_KEY
					listName // LIST_NAME
					});
		} catch (Exception insertError) {
			throw new JDBCRuntimeException(insertError);
		} finally {
			stopwatch.stop();
		}
	}
	
	protected Object[] getOraMonomerBatchAmount(MonomerBatchModel batchModel, String pageKey) {
		try {
			return new Object[] {
					batchModel.getKey(), // BATCH_KEY
					pageKey, //PAGE_KEY
					new Double(batchModel.getWeightAmount().doubleValue()), // VALUE
					batchModel.getWeightAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getWeightAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getWeightAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getWeightAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getWeightAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getVolumeAmount().doubleValue()), // VALUE
					batchModel.getVolumeAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getVolumeAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getVolumeAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getVolumeAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getVolumeAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getMolarAmount().doubleValue()), // VALUE
					batchModel.getMolarAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getMolarAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getMolarAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getMolarAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getMolarAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getMoleAmount().doubleValue()), // VALUE
					batchModel.getMoleAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getMoleAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getMoleAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getMoleAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getMoleAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getDensityAmount().doubleValue()), // VALUE
					batchModel.getDensityAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getDensityAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getDensityAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getDensityAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getDensityAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
    				new Double(batchModel.getPurityAmount().doubleValue()), // VALUE
					batchModel.getPurityAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getPurityAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getPurityAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getPurityAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getPurityAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getLoadingAmount().doubleValue()), // VALUE
					batchModel.getLoadingAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getLoadingAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getLoadingAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getLoadingAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getLoadingAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getRxnEquivsAmount().doubleValue()), // VALUE
					batchModel.getRxnEquivsAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getRxnEquivsAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getRxnEquivsAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getRxnEquivsAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getRxnEquivsAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					null, // VALUE
					null,// UNIT_CODE
					new Character('Y'), // CALCULATED
					null, // SIG_DIGITS
					new Character('N'), // SIG_DIGITS_SET
					null, // USER_PREF_FIGS
					
					null, // VALUE
					null,// UNIT_CODE
					new Character('Y'), // CALCULATED
					null, // SIG_DIGITS
					new Character('N'), // SIG_DIGITS_SET
					null, // USER_PREF_FIGS
					
					new Double(batchModel.getTotalWeight().doubleValue()), // VALUE
					batchModel.getTotalWeight().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getTotalWeight().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getTotalWeight().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getTotalWeight().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getTotalWeight().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getTotalVolume().doubleValue()), // VALUE
					batchModel.getTotalVolume().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getTotalVolume().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getTotalVolume().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getTotalVolume().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getTotalVolume().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getDeliveredWeight().doubleValue()), // VALUE
					batchModel.getDeliveredWeight().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getDeliveredWeight().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getDeliveredWeight().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getDeliveredWeight().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getDeliveredWeight().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getDeliveredVolume().doubleValue()), // VALUE
					batchModel.getDeliveredVolume().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getDeliveredVolume().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getDeliveredVolume().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getDeliveredVolume().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getDeliveredVolume().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getAmountNeeded().doubleValue()), // VALUE
					batchModel.getAmountNeeded().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getAmountNeeded().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getAmountNeeded().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getAmountNeeded().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getAmountNeeded().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getExtraNeeded().doubleValue()), // VALUE
					batchModel.getExtraNeeded().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getExtraNeeded().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getExtraNeeded().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getExtraNeeded().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getExtraNeeded().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getSoluteAmount().doubleValue()), // VALUE
					batchModel.getSoluteAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getSoluteAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getSoluteAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getSoluteAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getSoluteAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getPreviousMolarAmount().doubleValue()), // VALUE
    				batchModel.getPreviousMolarAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getPreviousMolarAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getPreviousMolarAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getPreviousMolarAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getPreviousMolarAmount().getUserPrefFigs()), // USER_PREF_FIGS
					null, // VALUE
					null,// UNIT_CODE
					new Character('Y'), // CALCULATED
					null, // SIG_DIGITS
					new Character('N'), // SIG_DIGITS_SET
					null, // USER_PREF_FIGS
				};
		} catch (Exception error) {
			log.error("Failed to create 'BATCH_AMOUNT' object  : " + error.getMessage(), error);
			throw new JDBCRuntimeException(error);
		}
	}

	protected Object[] getOraProductBatchAmount(ProductBatchModel batchModel, String pageKey) {
		try {
			Double theoYld = null;
			
			if(!batchModel.getTheoreticalYieldPercentAmount().isValueDefault()) {
				theoYld = new Double(batchModel.getTheoreticalYieldPercentAmount().doubleValue());
			}
			
			return new Object[] {
					batchModel.getKey(), // BATCH_KEY
					pageKey, //PAGE_KEY
					new Double(batchModel.getWeightAmount().doubleValue()), // VALUE
					batchModel.getWeightAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getWeightAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getWeightAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getWeightAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getWeightAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getVolumeAmount().doubleValue()), // VALUE
					batchModel.getVolumeAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getVolumeAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getVolumeAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getVolumeAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getVolumeAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getMolarAmount().doubleValue()), // VALUE
					batchModel.getMolarAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getMolarAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getMolarAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getMolarAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getMolarAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getMoleAmount().doubleValue()), // VALUE
					batchModel.getMoleAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getMoleAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getMoleAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getMoleAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getMoleAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getDensityAmount().doubleValue()), // VALUE
					batchModel.getDensityAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getDensityAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getDensityAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getDensityAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getDensityAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
    				new Double(batchModel.getPurityAmount().doubleValue()), // VALUE
					batchModel.getPurityAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getPurityAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getPurityAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getPurityAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getPurityAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getLoadingAmount().doubleValue()), // VALUE
					batchModel.getLoadingAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getLoadingAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getLoadingAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getLoadingAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getLoadingAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getRxnEquivsAmount().doubleValue()), // VALUE
					batchModel.getRxnEquivsAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getRxnEquivsAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getRxnEquivsAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getRxnEquivsAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getRxnEquivsAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getTheoreticalWeightAmount().doubleValue()), // VALUE
					batchModel.getTheoreticalWeightAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getTheoreticalWeightAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getTheoreticalWeightAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getTheoreticalWeightAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getTheoreticalWeightAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getTheoreticalMoleAmount().doubleValue()), // VALUE
					batchModel.getTheoreticalMoleAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getTheoreticalMoleAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getTheoreticalMoleAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getTheoreticalMoleAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getTheoreticalMoleAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getTotalWeight().doubleValue()), // VALUE
					batchModel.getTotalWeight().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getTotalWeight().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getTotalWeight().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getTotalWeight().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getTotalWeight().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getTotalVolume().doubleValue()), // VALUE
					batchModel.getTotalVolume().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getTotalVolume().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getTotalVolume().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getTotalVolume().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getTotalVolume().getUserPrefFigs()), // USER_PREF_FIGS
					
					null, // VALUE
					null,// UNIT_CODE
					new Character('Y'), // CALCULATED
					null, // SIG_DIGITS
					new Character('N'), // SIG_DIGITS_SET
					null, // USER_PREF_FIGS
					
					null, // VALUE
    				null,// UNIT_CODE
					new Character('Y'), // CALCULATED
					null, // SIG_DIGITS
					new Character('N'), // SIG_DIGITS_SET
					null, // USER_PREF_FIGS
					
					null, // VALUE
					null,// UNIT_CODE
					new Character('Y'), // CALCULATED
					null, // SIG_DIGITS
					new Character('N'), // SIG_DIGITS_SET
					null, // USER_PREF_FIGS
					
					null, // VALUE
					null,// UNIT_CODE
					new Character('Y'), // CALCULATED
					null, // SIG_DIGITS
					new Character('N'), // SIG_DIGITS_SET
					null, // USER_PREF_FIGS
					
					new Double(batchModel.getSoluteAmount().doubleValue()), // VALUE
					batchModel.getSoluteAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getSoluteAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getSoluteAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getSoluteAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getSoluteAmount().getUserPrefFigs()), // USER_PREF_FIGS
					
					new Double(batchModel.getPreviousMolarAmount().doubleValue()), // VALUE
					batchModel.getPreviousMolarAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getPreviousMolarAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getPreviousMolarAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getPreviousMolarAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getPreviousMolarAmount().getUserPrefFigs()), // USER_PREF_FIGS
                    
					theoYld, // VALUE
					batchModel.getTheoreticalYieldPercentAmount().getUnit().getCode(), // UNIT_CODE
					new Character(batchModel.getTheoreticalYieldPercentAmount().isCalculated()== true?'Y':'N'), // CALCULATED
					new Integer(batchModel.getTheoreticalYieldPercentAmount().getSigDigits()), // SIG_DIGITS
					new Character(batchModel.getTheoreticalYieldPercentAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
					new Integer(batchModel.getTheoreticalYieldPercentAmount().getUserPrefFigs()), // USER_PREF_FIGS
				};
		} catch (Exception error) {
			log.error("Failed to create 'BATCH_AMOUNT' object  : "+error.getMessage(), error);
    		throw new JDBCRuntimeException(error);
    	}
	}

	public void cleanUp(Connection conn, Statement stmt, ResultSet rs) {
		try { if (rs != null)   { rs.close();   } } catch (Exception e) { }
		try { if (stmt != null) { stmt.close(); } } catch (Exception e) { }
		try { if (conn != null) { conn.close(); } } catch (Exception e) { }
	}
}
