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

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.purificationservice.PurificationServiceSubmisionParameters;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.JDBCRuntimeException;
import com.chemistry.enotebook.storage.jdbc.insert.MonomerPlateInsert;
import com.chemistry.enotebook.storage.jdbc.insert.ProductPlateInsert;
import com.chemistry.enotebook.storage.jdbc.insert.RegisteredPlateInsert;
import com.chemistry.enotebook.storage.jdbc.select.*;
import com.chemistry.enotebook.storage.query.InsertQueryGenerator;
import com.chemistry.enotebook.storage.query.RemoveQueryGenerator;
import com.chemistry.enotebook.storage.query.SelectQueryGenerator;
import com.chemistry.enotebook.storage.query.UpdateQueryGenerator;
import com.chemistry.enotebook.util.Stopwatch;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.SqlUpdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlateDAO extends StorageDAO {

	private static final Log log = LogFactory.getLog(PlateDAO.class);
	
	Stopwatch watch = new Stopwatch();
	
	private AbstractSelect selector = null;

	/**
	 * Method loads Wells from CEN_PLATE_WELL table for all types of plates
	 * 
	 * @param plateKey
	 * @return
	 * @throws DAOException
	 */
	public PlateWell[] loadPlateWells(AbstractPlate plate) throws DAOException {
		PlateWell[] wellArray = null;
		Stopwatch newwatch = new Stopwatch();

		newwatch.start("loadMonomerPlates");
		try {
			log.debug("STARTED loadPlateWells for plateKey: " + plate.getKey());
			this.selector = new PlateWellSelect(super.getDataSource(), plate);
			log.debug("Executing Select on CEN_PLATE_WELL for WELLS.");
			List wells = this.selector.execute();
			log.debug("FINISHED Loading PLATE WELLS size :" + wells.size());
			wellArray = new PlateWell[wells.size()];
			for (int i = 0; i < wells.size(); i++)
				wellArray[i] = (PlateWell) wells.get(i);
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
		newwatch.stop();
		return wellArray;
	}

	/**
	 * Method loads Plates from CEN_PLATES table
	 * 
	 * @param pageKey
	 * @return
	 * @throws DAOException
	 */
	public List loadMonomerPlates(String pageKey, String stepKey, int stepNumber) throws DAOException {
		List mPlates = null;
		MonomerPlate mPlate = null;
		watch.start("loadMonomerPlates");
		try {
			log.debug("STARTED loadMonomerPlates for pageKey: " + pageKey);
			String sqlQuery = SelectQueryGenerator.getQueryForMonomerPlates(pageKey, stepKey);
			this.selector = new MonomerPlateSelect(super.getDataSource(), sqlQuery);
			log.debug("Executing Select on CEN_PLATES for MONOMER_PLATES.");
			List plateList = this.selector.execute();
			mPlates = new ArrayList(plateList.size());
			for (int i = 0; i < plateList.size(); i++) {
				mPlate = (MonomerPlate) plateList.get(i);
				mPlate.setWells(loadPlateWells(mPlate));
				mPlate.setStepNumber(stepNumber + "");
				mPlates.add(mPlate);

			}
			log.debug("FINISHED Loading MONOMER PLATES size :" + plateList.size());
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
		watch.stop();
		return mPlates;
	}

	/**
	 * Method loads Plates from CEN_PLATES table
	 * 
	 * @param pageKey
	 * @return
	 * @throws DAOException
	 */
	public List loadProductPlates(String pageKey, String stepKey, int stepNumber) throws DAOException {
		List pPlates = null;
		ProductPlate pPlate = null;
		watch.start("loadProductPlates");
		try {
			log.debug("STARTED loadProductPlates for pageKey: " + pageKey);
			String sqlQuery = SelectQueryGenerator.getQueryForProductPlatesWithSubmissionStatus(pageKey, stepKey);
			this.selector = new ProductPlateSelect(super.getDataSource(), sqlQuery);
			log.debug("Executing Select on CEN_PLATES for PRODUCT_PLATES.");
			List plateList = this.selector.execute();
			pPlates = new ArrayList(plateList.size());
			for (int i = 0; i < plateList.size(); i++) {
				pPlate = (ProductPlate) plateList.get(i);
				pPlate.setWells(loadPlateWells(pPlate));
				pPlate.setStepNumber(stepNumber + "");
				pPlates.add(pPlate);

			}
			log.debug("FINISHED Loading PRODUCT PLATES size :" + plateList.size());
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
		watch.stop();
		return pPlates;
	}

	/**
	 * Method loads Plates from CEN_PLATES table
	 * 
	 * @param pageKey
	 * @return
	 * @throws DAOException
	 */

	public ArrayList loadRegisteredPlates(String pageKey) throws DAOException {
		ArrayList rPlates = null;
		ProductPlate rPlate = null;
		watch.start("loadRegisteredPlates");
		try {
			log.debug("STARTED loadRegisteredPlates for pageKey: " + pageKey);
			String sqlQuery = SelectQueryGenerator.getQueryForRegisteredPlates(pageKey);
			this.selector = new RegisteredPlateSelect(super.getDataSource(), sqlQuery);
			log.debug("Executing Select on CEN_PLATES for REGISTERED_PLATES.");
			List plateList = this.selector.execute();
			rPlates = new ArrayList(plateList.size());
			for (int i = 0; i < plateList.size(); i++) {
				rPlate = (ProductPlate) plateList.get(i);
				rPlate.setWells(loadPlateWells(rPlate));
				rPlates.add(rPlate);
			}
			log.debug("FINISHED Loading REGISTERED_PLATES size :" + plateList.size());
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
		watch.stop();
		return rPlates;
	}

	/*
	 * insert registered plate
	 */
	public void insertRegisteredPlate(String pageKey, ProductPlate registeredPlate) {// throws DAOException {
		String insertSql = InsertQueryGenerator.getInsertRegisteredPlateQuery();
		RegisteredPlateInsert insert = new RegisteredPlateInsert(this.getDataSource(), insertSql);
		try {
			log.debug("Inserting Registered Plate with type : " + registeredPlate.getPlateType());
			insert.compile();
			insert.update(new Object[] { registeredPlate.getKey(),// PLATE_KEY
					registeredPlate.getPlateType(), // CEN_PLATE_TYPE
					pageKey, // PAGE_KEY
					registeredPlate.getContainer().getKey(), // CONTAINER_KEY
					registeredPlate.getPlateNumber(), // PLATE_NUMBER
					registeredPlate.getPlateBarCode(),// PLATE_BAR_CODE
					registeredPlate.getRegisteredDate() // REGISTERED_DATE
					});
		} catch (Exception error) {
			throw new JDBCRuntimeException(error);
		}
	}

	/*
	 * Inserts Reaction Plates in CEN_Plates table
	 * 
	 */

	public void insertRegisteredPlates(String pageKey, List registeredPlates) throws DAOException {
		log.debug("Inserting Registered Plates count:" + registeredPlates.size());

		for (int i = 0; i < registeredPlates.size(); i++) {
			ProductPlate plate = (ProductPlate) registeredPlates.get(i);
			this.insertRegisteredPlate(pageKey, plate);
			this.insertPlateWellsAndPurificationService(plate.getKey(), plate.getWells(), pageKey);
		}
	}

	public void insertProductPlate(String pageKey, String stepKey, ProductPlate productPlate) {// throws DAOException {
		String insertSql = InsertQueryGenerator.getInsertProductPlateQuery();
		ProductPlateInsert insert = new ProductPlateInsert(this.getDataSource(), insertSql);
		try {
			log.debug("Inserting Product Plate with type : " + productPlate.getPlateType());
			insert.compile();
			insert.update(new Object[] { productPlate.getKey(),// PLATE_KEY
					productPlate.getPlateType(), // CEN_PLATE_TYPE
					pageKey, // PAGE_KEY
					productPlate.getContainer().getKey(), // CONTAINER_KEY
					productPlate.getPlateNumber(), // PLATE_NUMBER
					productPlate.getPlateBarCode(), // PLATE_BAR_CODE
					stepKey // STEP_KEY
					});
		} catch (Exception insertError) {
			throw new JDBCRuntimeException(insertError);
		}
	}

	public void insertProductPlates(String pageKey, String stepKey, List productPlates) throws DAOException {
		log.debug("Inserting Product Plates count:" + productPlates.size());
		for (int i = 0; i < productPlates.size(); i++) {
			ProductPlate plate = (ProductPlate) productPlates.get(i);
			this.insertProductPlate(pageKey, stepKey, plate);
			this.insertPlateWellsAndPurificationService(plate.getKey(), plate.getWells(), pageKey);
		}

	}

	/*
	 * Inserts MonomerPlate in CEN_Plates table and returns boolean indicating the success or failure of the insert operation.
	 * 
	 * @return boolean true if insert was success.
	 */
	/*
	 * public void insertPlateWell(String plateKey, PlateWell plateWell) { String insertSql =
	 * insertQueryGenerator.getInsertPlateWellQuery(); PlatewellInsert insert = new PlatewellInsert(this.getDataSource(),
	 * insertSql); try { log.debug("Inserting Plate Well with type:" + plateWell.getWellType());
	 * 
	 * String batchKey = ""; if (CommonUtils.isNotNull(plateWell.getBatch())) { batchKey = plateWell.getBatch().getKey(); }
	 * insert.compile(); insert.update(new Object[] { plateWell.getKey(), // WELL_KEY plateKey, // PLATE_KEY new
	 * Integer(plateWell.getWellPosition()), // WELL_POSITION plateWell.getWellType(), // WELL_TYPE batchKey, // BATCH_KEY
	 * plateWell.getSolventCode(), // SOLVENT_CODE plateWell.getBarCode() // BARCODE });
	 *  // Insert PurificationService Params PurificationServiceSubmisionParameters param = plateWell.getPurificationServiceParameter(); if (param != null) { PurificationServiceParameterInsert
	 * purificationServiceInsert = new PurificationServiceParameterInsert(this.getDataSource()); purificationServiceInsert.compile(); log.debug("Inserting Plate Well PurificationService Data:");
	 * purificationServiceInsert.update(new Object[] { param.getKey(), // PURIFICATION_SERVICE_KEY CommonUtils.getAsPipeSeperateValues(param.getModifiers()), new
	 * Double(param.getPhValue()), param.getArchivePlate(), new Double(param.getArchiveVolume()), param.getSampleWorkUp(),
	 * (param.isInorganicByProductSaltPresent() == true ? "Y" : "N"), (param.isSeperateTheIsomers() == true ? "Y" : "N"),
	 * param.getDestinationLab(), plateWell.getKey() }); } else { log.debug("No PurificationService Data for plate well:"); }
	 *  } catch (Exception insertError) { throw new JDBCRuntimeException(insertError); } }
	 */

	/*
	 * Inserts PlateWells in CEN_Plate_Well table and CEN_PURIFICATION_SERVICE
	 * 
	 */
	public void insertPlateWellsAndPurificationService(String plateKey, PlateWell[] plateWells, String pageKey) {
		int wellsSize = plateWells.length;
		log.debug("Inserting Plate Wells count:" + wellsSize);
		this.insertPlateWellsUsingSpringBatching(plateKey, plateWells);

		ArrayList list = new ArrayList();
		// Sort out the wells that have PurificationService params
		for (int i = 0; i < wellsSize; i++) {
			if (plateWells[i].getPurificationServiceParameter() != null) {
				list.add(plateWells[i]);
			}
		}
		final PlateWell[] plateWellsFiltered = (PlateWell[]) list.toArray(new PlateWell[] {});
		if (plateWellsFiltered.length > 0) {
			this.insertPurificationServiceParamUsingSpringBatching(plateWellsFiltered);
		} else {
			log.debug("NO PurificationService params to insert:");
		}

	}// end of method

	/*
	 * Inserts MonomerPlate in CEN_Plates table
	 * 
	 */
	public void insertMonomerPlate(String pageKey, String stepKey, MonomerPlate plate) {

		String insertSql = InsertQueryGenerator.getInsertMonomerPlateQuery();
		MonomerPlateInsert insert = new MonomerPlateInsert(this.getDataSource(), insertSql);
		log.debug("Inserting Monomer Plate with type:" + plate.getPlateType());
		try {
			insert.compile();
			insert.update(new Object[] { plate.getKey(),// PLATE_KEY
					plate.getPlateType(), // CEN_PLATE_TYPE
					pageKey, // PAGE_KEY
					plate.getContainer().getKey(), // CONTAINER_KEY
					plate.getPlateNumber(), // PLATE_NUMBER
					plate.getPlateBarCode(), // PLATE_BAR_CODE
					stepKey, // STEP_KEY
					plate.getParentPlateKey() // PARENT_PLATE_KEY
					});
		} catch (Exception insertError) {
			throw new JDBCRuntimeException(insertError);
		}
	}

	public void displayPlateCount() {
		try {
			JdbcTemplate jt = new JdbcTemplate();
			jt.setDataSource(this.getDataSource());
			String query = "select count(*) from cen_plate";
			int result = jt.queryForInt(query);
			System.out.println("Records Count  " + result);
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	/*
	 * Inserts Monomer Plates in CEN_Plates table
	 * 
	 */
	public void insertMonomerPlates(String pageKey, String stepKey, List monomerPlates) {
		MonomerPlate plate = null;
		log.debug("Inserting Monomer Plates count:" + monomerPlates.size());
		for (int i = 0; i < monomerPlates.size(); i++) {
			plate = (MonomerPlate) monomerPlates.get(i);
			this.insertMonomerPlate(pageKey, stepKey, plate);
			this.insertPlateWellsUsingSpringBatching(plate.getKey(), plate.getWells());
		}
	}// end of method

	public void updatePlate(Object plateObject) throws DAOException {
		try {
			JdbcTemplate jt = new JdbcTemplate();
			jt.setDataSource(this.getDataSource());
			SqlUpdate su = new SqlUpdate();
			su.setJdbcTemplate(jt);
			String updateSql = UpdateQueryGenerator.getPlateUpdateQuery(plateObject);
			su.setSql(updateSql);
			su.compile();
			su.update();
		} catch (Exception updateError) {
			throw new JDBCRuntimeException(updateError);
		}
	}

	/*
	 * Loops thru the list of plates, Calls update for the list of wells contained in the Plate then calls updatePlate for each of
	 * the plate.
	 */
	public void updatePlates(List plateList) throws DAOException {
		try {
			log.debug("Updating plates:" + plateList.size());
			for (int i = 0; i < plateList.size(); i++) {
				Object obj = plateList.get(i);
				AbstractPlate plate = (AbstractPlate) obj;
				PlateWell[] wells = plate.getWells();
				updatePlateWellsAndPurificationServiceWithBatching(wells);
				updatePlate(plateList.get(i));
			}
		} catch (DAOException error) {
			throw error;
		}
	}

	/*
	 * public void updatePlateWell(PlateWell well) { try { if (well == null) return; if (well.isModelChanged()) {
	 * UpdateQueryGenerator queryGen = new UpdateQueryGenerator(); JdbcTemplate jt = new JdbcTemplate();
	 * jt.setDataSource(this.getDataSource()); SqlUpdate su = new SqlUpdate(); su.setJdbcTemplate(jt); String updateSql =
	 * queryGen.getPlateWellUpdateQuery(well); su.setSql(updateSql); su.compile(); su.update(); // update PurificationService Data // Check if
	 * Update or insert required PurificationServiceSubmisionParameters param = well.getPurificationServiceParameter(); if (param != null &&
	 * !param.isLoadedFromDB()) { PurificationServiceParameterInsert purificationServiceInsert = new PurificationServiceParameterInsert(this.getDataSource()); purificationServiceInsert.compile();
	 * log.debug("Inserting Plate Well PurificationService Data:"); purificationServiceInsert.update(new Object[] { param.getKey(), // PURIFICATION_SERVICE_KEY
	 * CommonUtils.getAsPipeSeperateValues(param.getModifiers()), new Double(param.getPhValue()), param.getArchivePlate(), new
	 * Double(param.getArchiveVolume()), param.getSampleWorkUp(), (param.isInorganicByProductSaltPresent() == true ? "Y" : "N"),
	 * (param.isSeperateTheIsomers() == true ? "Y" : "N"), param.getDestinationLab(), well.getKey() }); } else if (param != null &&
	 * param.isLoadedFromDB()) { log.debug("Updating Plate Well PurificationService Data with PurificationServicekey:" + param.getKey()); String updateSql2 =
	 * queryGen.getPurificationServiceParamUpdateQuery(param); su.setSql(updateSql2); su.compile(); su.update(); } else { log.debug("No PurificationService Data for
	 * plate well:"); } } else { log.debug("PlateWell hasn't changed.No update on well key:" + well.getKey()); } } catch (Exception
	 * updateError) { throw new JDBCRuntimeException(updateError); } }
	 */
	/*
	 * Updates PlateWells in batch mode. Also isolates PurificationServiceParam for update/insert/delete
	 */
	public void updatePlateWellsAndPurificationServiceWithBatching(PlateWell[] wells) {
		log.debug("Updating plate wells :" + wells.length);
		ArrayList insertList = new ArrayList();
		ArrayList updateList = new ArrayList();
		for (int i = 0; i < wells.length; i++) {
			PurificationServiceSubmisionParameters param = wells[i].getPurificationServiceParameter();
			if (param != null && !param.isLoadedFromDB()) {
				insertList.add(wells[i]);
			} else if (param != null && param.isLoadedFromDB()) {
				updateList.add(wells[i]);
			}// need to add condition if marked for delete
			else {
				log.debug("No PurificationService Data for plate well:");
			}
		}
		// update the plate wells amounts
		this.updatePlateWellsUsingSpringBatching(wells);
		if (insertList.size() > 0) {
			this.insertPurificationServiceParamUsingSpringBatching((PlateWell[]) insertList.toArray(new PlateWell[] {}));
		}
		if (updateList.size() > 0) {
			this.updatePurificationServiceParamsSequentialy((PlateWell[]) updateList.toArray(new PlateWell[] {}));
		}

	}

	public void removePlateWell(String wellKey) throws DAOException {
		try {
			String removeSql = RemoveQueryGenerator.getRemovePlateWellQuery(wellKey);
			SqlUpdate su = getSqlUpdate(removeSql);
			su.compile();
			su.update();
			log.debug("Completed Remove PLATE WELL with wellKey :" + wellKey);
		} catch (Exception removeError) {
			throw new JDBCRuntimeException(removeError);
		}
	}
	
	public void removePlate(String plateKey) throws DAOException {
		try {
			String removeSql = RemoveQueryGenerator.getRemovePlateQuery(plateKey);
			SqlUpdate su = getSqlUpdate(removeSql);
			su.compile();
			su.update();
			log.debug("Completed Remove PLATE with plateKey :" + plateKey);
		} catch (Exception removeError) {
			throw new JDBCRuntimeException(removeError);
		}
	}

	/*
	 * public void updatePlateWellAmounts(PlateWell pWell) throws DAOException { try { log.debug("updatePlateWellAmounts().enter");
	 * updateAmount(pWell.getContainedWeightAmount()); updateAmount(pWell.getContainedVolumeAmount());
	 * updateAmount(pWell.getContainedMolarity()); // set to true in PlateWellSelect line 35 .. pWell.setLoadingFromDB(false);
	 * log.debug("updatePlateWellAmounts().exit"); } catch (Exception updateError) { throw new JDBCRuntimeException(updateError); } }
	 */
	// amountMap will have wellkey as key and Map as Value
	// Value map will have amountName as Key and AmountModel as value
	private void loadAmountsIntoPlateWells(PlateWell pwells[], Map amountsMap) {
		/*
		 * log.debug("loadAmountsIntoPlateWells().enter"); if(pwells != null && pwells.length > 0 && amountsMap != null &&
		 * !amountsMap.isEmpty()) { int size = pwells.length; for(int i = 0; i< size ; i++) { PlateWell well = pwells[i]; Map
		 * amounts = (Map)amountsMap.get(well.getKey()); if(amounts == null || well== null) continue; // log.debug("Total no of
		 * Amount Records Loaded for Well:"+well.getKey()+ " is :"+amounts.size());
		 * well.setContainedWeightAmount((AmountModel)amounts.get(CeNConstants.WELL_WEIGHT_AMOUNT));
		 * well.setContainedVolumeAmount((AmountModel)amounts.get(CeNConstants.WELL_VOLUME_AMOUNT));
		 * well.setMolarity((AmountModel)amounts.get(CeNConstants.WELL_MOLARITY_AMOUNT)); well.setLoadingFromDB(false);
		 * //PlateWellSelect Line 35 setLoadingFromDB(true) is set. }
		 * 
		 * }else { log.debug("PlateWell array is null or empty or amountsMap is null or empty."); }
		 * log.debug("loadAmountsIntoPlateWells().exit");
		 * 
		 */
	}

	public List loadPseudoProductPlate(String pageKey) throws DAOException {
		List pPlates = null;
		PseudoProductPlate pPlate = null;
		watch.start("loadPseudoProductPlate");
		try {
			log.debug("STARTED loadProductPlates for pageKey: " + pageKey);
			String sqlQuery = SelectQueryGenerator.getQueryForPseudoProductPlateInExperimentPage(pageKey);
			this.selector = new PseudoProductPlateSelect(super.getDataSource(), sqlQuery);
			log.debug("Executing Select on CEN_PLATES for PSEUDO_PRODUCT_PLATE.");
			List plateList = this.selector.execute();
			pPlates = new ArrayList(plateList.size());
			for (int i = 0; i < plateList.size(); i++) {
				pPlate = (PseudoProductPlate) plateList.get(i);
				pPlate.setWells(loadPlateWells(pPlate));
				pPlate.setStepNumber("");
				pPlates.add(pPlate);

			}
			log.debug("FINISHED Loading PRODUCT PLATES size :" + plateList.size());
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
		watch.stop();
		return pPlates;
	}

	public void updatePseudoProductPlate(PseudoProductPlate psPlate, String pageKey, String action) throws DAOException {
		// Identify Wells that needs to be updated and Wells that need to be inserted.
		// There will not be any updates for Pseudo Plate itself
		log.debug("updatePseudoProductPlate().enter with action:" + action);
		try {
			if (psPlate != null && psPlate.getWells() != null && psPlate.getWells().length > 0) {
				PlateWell wells[] = psPlate.getWells();
				int size = wells.length;
				ArrayList wellsUpdate = new ArrayList(size);
				ArrayList wellsInsert = new ArrayList(size);
				ArrayList wellsDelete = new ArrayList(size);
				for (int i = 0; i < size; i++) {
					if (wells[i].isLoadedFromDB() && wells[i].isModelChanged()) {
						wellsUpdate.add(wells[i]);
					} else if (wells[i].isLoadedFromDB() && wells[i].isSetToDelete()) {
						wellsDelete.add(wells[i]);
					} else {
						wellsInsert.add(wells[i]);
					}
				}
				if (action.compareTo("UPDATE_DELETE") == 0) {
					log.debug("Total Wells marked for update:" + wellsUpdate.size());
					log.debug("Total Wells marked for delete:" + wellsDelete.size());
				}
				if (action.compareTo("INSERT") == 0) {
					log.debug("Total Wells marked for insert:" + wellsInsert.size());
				}
				if (wellsUpdate.size() > 0 && action.compareTo("UPDATE_DELETE") == 0) {
					// Update Plate well and Add/Update/Remove PurificationService.
					this.updatePlateWellsAndPurificationServiceWithBatching((PlateWell[]) wellsUpdate.toArray(new PlateWell[] {}));
				}
				if (wellsDelete.size() > 0 && action.compareTo("UPDATE_DELETE") == 0) {
					// Remove platewell only ( cacade delete may reomve PurificationService if exisits
					this.removePlateWellsForPseduoPlateUsingSpringBatching((PlateWell[]) wellsDelete.toArray(new PlateWell[] {}));
				}
				if (wellsInsert.size() > 0 && action.compareTo("INSERT") == 0) {
					// insert platewell only ,pseudo has no PurificationService data
					this.insertPlateWellsUsingSpringBatching(psPlate.getKey(), (PlateWell[]) wellsInsert
							.toArray(new PlateWell[] {}));
				}
				log.debug("updatePseudoProductPlate().exit");
			}
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
	}

	/*
	 * public void removePlateWellsForPseduoPlate(PlateWell wells[]) throws DAOException {
	 * log.debug("removePlateWellsForPseduoPlate().enter"); try { if (wells == null || wells.length == 0) return; int size =
	 * wells.length; for (int i = 0; i < size; i++) { String removeSql =
	 * RemoveQueryGenerator.getNewInstance().getRemovePlateWellQuery(wells[i].getKey()); SqlUpdate su = getSqlUpdate(removeSql);
	 * su.compile(); su.update(); log.debug("Completed Remove PlateWell with wellKey :" + wells[i].getKey());
	 * log.debug("removePlateWellsForPseduoPlate().exit"); } } catch (Exception removeError) { throw new
	 * JDBCRuntimeException(removeError); } }
	 */
	public void insertPlateWellsUsingSpringBatching(final String plateKey, final PlateWell[] plateWells) {
		log.debug("insertPlateWellsUsingSpringBatching().enter");
		log.debug("Inserting Plate Wells count:" + plateWells.length);
		JdbcTemplate t = new JdbcTemplate();
		t.setDataSource(super.getDataSource());

		BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
				PlateWell well = plateWells[i];
				String batchKey = null;
				if (CommonUtils.isNotNull(well.getBatch())) {
					batchKey = well.getBatch().getKey();
				}
				preparedStatement.setString(1, well.getKey());
				preparedStatement.setString(2, plateKey);
				preparedStatement.setInt(3, well.getWellPosition());
				preparedStatement.setString(4, well.getWellType());
				preparedStatement.setString(5, batchKey);
				preparedStatement.setString(6, well.getSolventCode());
				preparedStatement.setString(7, well.getBarCode());
				preparedStatement.setString(8, well.getWellNumber());

				preparedStatement.setDouble(9, well.getContainedWeightAmount().doubleValue());
				preparedStatement.setString(10, well.getContainedWeightAmount().getUnit().getCode());
				preparedStatement.setString(11, (well.getContainedWeightAmount().isCalculated()) == true ? "Y" : "N");
				preparedStatement.setInt(12, well.getContainedWeightAmount().getSigDigits());
				preparedStatement.setString(13, (well.getContainedWeightAmount().getSigDigitsSet()) == true ? "Y" : "N");
				preparedStatement.setInt(14, well.getContainedWeightAmount().getUserPrefFigs());

				preparedStatement.setDouble(15, well.getContainedVolumeAmount().doubleValue());
				preparedStatement.setString(16, well.getContainedVolumeAmount().getUnit().getCode());
				preparedStatement.setString(17, (well.getContainedVolumeAmount().isCalculated()) == true ? "Y" : "N");
				preparedStatement.setInt(18, well.getContainedVolumeAmount().getSigDigits());
				preparedStatement.setString(19, (well.getContainedVolumeAmount().getSigDigitsSet()) == true ? "Y" : "N");
				preparedStatement.setInt(20, well.getContainedVolumeAmount().getUserPrefFigs());

				preparedStatement.setDouble(21, well.getMolarity().doubleValue());
				preparedStatement.setString(22, well.getMolarity().getUnit().getCode());
				preparedStatement.setString(23, (well.getMolarity().isCalculated()) == true ? "Y" : "N");
				preparedStatement.setInt(24, well.getMolarity().getSigDigits());
				preparedStatement.setString(25, (well.getMolarity().getSigDigitsSet()) == true ? "Y" : "N");
				preparedStatement.setInt(26, well.getMolarity().getUserPrefFigs());
			}

			public int getBatchSize() {
				return plateWells.length;
			}

		};

		// execute the batch insert
		int[] rows = t.batchUpdate(InsertQueryGenerator.getInsertPlateWellPrepStmtSQL(), setter);
		log.debug("Inserted Plate Wells count:" + rows.length);
		log.debug("insertPlateWellsUsingSpringBatching().exit");
	}// end of method

	public void insertPurificationServiceParamUsingSpringBatching(final PlateWell[] plateWells) {
		log.debug("insertPurificationServiceParamUsingSpringBatching().enter");
		if (plateWells == null)
			return;
		log.debug("Inserting PurificationServicePrams count:" + plateWells.length);
		JdbcTemplate t = new JdbcTemplate();
		t.setDataSource(super.getDataSource());

		BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
				PlateWell well = plateWells[i];
				// Insert PurificationService Params
				PurificationServiceSubmisionParameters param = well.getPurificationServiceParameter();
				preparedStatement.setString(1, param.getKey());
				preparedStatement.setString(2, CommonUtils.getAsPipeSeperateValues(param.getModifiers()));
				preparedStatement.setDouble(3, param.getPhValue());
				preparedStatement.setString(4, param.getArchivePlate());
				preparedStatement.setDouble(5, param.getArchiveVolume());
				preparedStatement.setString(6, param.getSampleWorkUp());
				preparedStatement.setString(7, param.isInorganicByProductSaltPresent() == true ? "Y" : "N");
				preparedStatement.setString(8, param.isSeperateTheIsomers() == true ? "Y" : "N");
				preparedStatement.setString(9, param.getDestinationLab());
				preparedStatement.setString(10, well.getKey());

			}

			public int getBatchSize() {
				return plateWells.length;
			}

		};

		// execute the batch insert
		int[] rows = t.batchUpdate(InsertQueryGenerator.getInsertPurificationServiceParamPrepStmtSQL(), setter);
		log.debug("Inserted PurificationServiceParams count:" + rows.length);
		log.debug("insertPurificationServiceParamUsingSpringBatching().exit");
	}// end of method

	public void removePlateWellsForPseduoPlateUsingSpringBatching(final PlateWell plateWells[]) throws DAOException {
		log.debug("removePlateWellsForPseduoPlateUsingSpringBatching().enter");
		String removeSql = "DELETE FROM CEN_PLATE_WELL WHERE WELL_KEY='?";
		try {
			if (plateWells == null || plateWells.length == 0)
				return;

			JdbcTemplate t = new JdbcTemplate();
			t.setDataSource(super.getDataSource());

			BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
					preparedStatement.setString(1, plateWells[i].getKey());

				}

				public int getBatchSize() {
					return plateWells.length;
				}

			};

			// execute the batch insert
			int[] rows = t.batchUpdate(removeSql, setter);
			log.debug("Deleted wells count:" + rows.length);
			log.debug("removePlateWellsForPseduoPlateUsingSpringBatching().exit");

		} catch (Exception removeError) {
			throw new JDBCRuntimeException(removeError);
		}
	}

	public void updatePlateWellsUsingSpringBatching(final PlateWell[] plateWells) {
		log.debug("updatePlateWellsUsingSpringBatching().enter");
		log.debug("Updating Plate Wells count:" + plateWells.length);
		JdbcTemplate t = new JdbcTemplate();
		t.setDataSource(super.getDataSource());

		BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
				PlateWell well = plateWells[i];
				String batchKey = null;
				if (CommonUtils.isNotNull(well.getBatch())) {
					batchKey = well.getBatch().getKey();
				}
				preparedStatement.setString(1, batchKey);
				preparedStatement.setString(2, well.getSolventCode());
				preparedStatement.setString(3, well.getBarCode());

				preparedStatement.setDouble(4, well.getContainedWeightAmount().doubleValue());
				preparedStatement.setString(5, well.getContainedWeightAmount().getUnit().getCode());
				preparedStatement.setString(6, (well.getContainedWeightAmount().isCalculated()) == true ? "Y" : "N");
				preparedStatement.setInt(7, well.getContainedWeightAmount().getSigDigits());
				preparedStatement.setString(8, (well.getContainedWeightAmount().getSigDigitsSet()) == true ? "Y" : "N");
				preparedStatement.setInt(9, well.getContainedWeightAmount().getUserPrefFigs());

				preparedStatement.setDouble(10, well.getContainedVolumeAmount().doubleValue());
				preparedStatement.setString(11, well.getContainedVolumeAmount().getUnit().getCode());
				preparedStatement.setString(12, (well.getContainedVolumeAmount().isCalculated()) == true ? "Y" : "N");
				preparedStatement.setInt(13, well.getContainedVolumeAmount().getSigDigits());
				preparedStatement.setString(14, (well.getContainedVolumeAmount().getSigDigitsSet()) == true ? "Y" : "N");
				preparedStatement.setInt(15, well.getContainedVolumeAmount().getUserPrefFigs());

				preparedStatement.setDouble(16, well.getMolarity().doubleValue());
				preparedStatement.setString(17, well.getMolarity().getUnit().getCode());
				preparedStatement.setString(18, (well.getMolarity().isCalculated()) == true ? "Y" : "N");
				preparedStatement.setInt(19, well.getMolarity().getSigDigits());
				preparedStatement.setString(20, (well.getMolarity().getSigDigitsSet()) == true ? "Y" : "N");
				preparedStatement.setInt(21, well.getMolarity().getUserPrefFigs());

				preparedStatement.setString(22, well.getKey());
			}

			public int getBatchSize() {
				return plateWells.length;
			}

		};

		// execute the batch insert
		int[] rows = t.batchUpdate(UpdateQueryGenerator.getPlateWellUpdatePrepStmtSQL(), setter);
		log.debug("Updated Plate Wells count:" + rows.length);
		log.debug("updatePlateWellsUsingSpringBatching().exit");
	}

	public void updatePurificationServiceParamsUsingSpringBatching(final PlateWell[] plateWells) {
		log.debug("updatePurificationServiceParamsUsingSpringBatching().enter");
		log.debug("Updating PurificationServicePrams for Plate Wells count:" + plateWells.length);
		JdbcTemplate t = new JdbcTemplate();
		t.setDataSource(super.getDataSource());

		BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
				PlateWell well = plateWells[i];
				// Insert PurificationService Params
				PurificationServiceSubmisionParameters param = well.getPurificationServiceParameter();
				//if(i == 1)
				{
				log.debug("Modifiers:"+CommonUtils.getAsPipeSeperateValues(param.getModifiers()));	
				log.debug("ArchivePlate:"+param.getArchivePlate());
				log.debug("SampleWorkup:"+param.getSampleWorkUp());
				log.debug("DestinationLab:"+param.getDestinationLab());
				log.debug("Salt present:"+ (param.isInorganicByProductSaltPresent() == true ? "Y" : "N"));
				log.debug("Sep Isomers:"+(param.isSeperateTheIsomers() == true ? "Y" : "N"));
				log.debug("Key:"+param.getKey());
				}
				preparedStatement.setString(1, CommonUtils.getAsPipeSeperateValues(param.getModifiers()));
				preparedStatement.setDouble(2, param.getPhValue());
				preparedStatement.setString(3, param.getArchivePlate());
				preparedStatement.setDouble(4, param.getArchiveVolume());
				preparedStatement.setString(5, param.getSampleWorkUp());
				preparedStatement.setString(6, param.isInorganicByProductSaltPresent() == true ? "Y" : "N");
				preparedStatement.setString(7, param.isSeperateTheIsomers() == true ? "Y" : "N");
				preparedStatement.setString(8, param.getDestinationLab());
				preparedStatement.setString(9, param.getKey());

			}

			public int getBatchSize() {
				return plateWells.length;
			}

		};

		// execute the batch insert
		int[] rows = t.batchUpdate(UpdateQueryGenerator.getPurificationServiceParamUpdatePrepStmtSQL(), setter);
		log.debug("Updatedted PurificationServiceParams count:" + rows.length);
		log.debug("updatePurificationServiceParamsUsingSpringBatching().exit");
	}
	
	public void updatePurificationServiceParamsSequentialy(final PlateWell[] plateWells) {
		log.debug("updatePurificationServiceParamsSequentialy().enter");
		log.debug("Updating PurificationServicePrams for Plate Wells count:" + plateWells.length);
		Connection conn = null;
		PreparedStatement preparedStatement = null;

		try {
			conn = this.getDataSource().getConnection();
			preparedStatement = conn.prepareStatement(UpdateQueryGenerator.getPurificationServiceParamUpdatePrepStmtSQL());
        for(int i=0;i<plateWells.length;i++)
        {
				PlateWell well = plateWells[i];
				// Insert PurificationService Params
				PurificationServiceSubmisionParameters param = well.getPurificationServiceParameter();
				//if(i == 1)
				//{
				//log.debug("Modifiers:"+CommonUtils.getAsPipeSeperateValues(param.getModifiers()));	
				//log.debug("ArchivePlate:"+param.getArchivePlate());
				//log.debug("SampleWorkup:"+param.getSampleWorkUp());
				//log.debug("DestinationLab:"+param.getDestinationLab());
				//log.debug("Salt present:"+ (param.isInorganicByProductSaltPresent() == true ? "Y" : "N"));
				//log.debug("Sep Isomers:"+(param.isSeperateTheIsomers() == true ? "Y" : "N"));
				//log.debug("Key:"+param.getKey());
				//}
				preparedStatement.setString(1, CommonUtils.getAsPipeSeperateValues(param.getModifiers()));
				preparedStatement.setDouble(2, param.getPhValue());
				preparedStatement.setString(3, param.getArchivePlate());
				preparedStatement.setDouble(4, param.getArchiveVolume());
				preparedStatement.setString(5, param.getSampleWorkUp());
				preparedStatement.setString(6, param.isInorganicByProductSaltPresent() == true ? "Y" : "N");
				preparedStatement.setString(7, param.isSeperateTheIsomers() == true ? "Y" : "N");
				preparedStatement.setString(8, param.getDestinationLab());
				preparedStatement.setString(9, param.getKey());
				preparedStatement.execute();
			}
		} catch (Exception error) {
			log.error(error.getStackTrace());
			//throw new DAOException(error);
		} finally {
			this.cleanUp(conn, preparedStatement, null);
		}

		log.debug("Updatedted PurificationServiceParams count:" + plateWells.length);
		log.debug("updatePurificationServiceParamsSequentialy().exit");
	}
}
