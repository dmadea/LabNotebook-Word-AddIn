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
import com.chemistry.enotebook.experiment.datamodel.page.InvalidNotebookRefException;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.JDBCRuntimeException;
import com.chemistry.enotebook.storage.jdbc.update.*;
import com.chemistry.enotebook.storage.query.SelectQueryGenerator;
import com.chemistry.enotebook.storage.query.UpdateQueryGenerator;
import com.chemistry.enotebook.storage.utils.StorageUtils;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.SqlUpdate;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class NotebookUpdateDAO extends StorageDAO {

	private static final Log log = LogFactory.getLog(NotebookUpdateDAO.class);

	/**
	 * Update only NotebookPage Header data
	 * 
	 * @param pageModel
	 * @throws DAOException
	 */
	public void updateNotebookPageHeader(NotebookPageHeaderModel pageHeaderModel) throws DAOException {
		try {

			if (pageHeaderModel.isModelChanged()) {
				log.debug("NotebookPageHeader has changed..");
				pageHeaderModel.setModificationDateAsTimestamp(CommonUtils.getCurrentTimestamp());
				
				SqlUpdate su = new NotebookPageUpdate(this.getDataSource());
				int result = su.update(new Object[] { 
						pageHeaderModel.getPageStatus(), // PAGE_STATUS
						pageHeaderModel.getModificationDateAsTimestamp(), // MODIFIED_DATE
						new Integer(pageHeaderModel.getVersion()), // VERSION
						CommonUtils.replaceSpecialCharsInText(pageHeaderModel.toXML()), // XML_METADATA(XML_TYPE)
						pageHeaderModel.getLatestVersion(), // LATEST_VERSION
						pageHeaderModel.getTaCode(), // TA_CODE
						pageHeaderModel.getProjectCode(), // PROJECT_CODE
						pageHeaderModel.getLiteratureRef(), // LITERATURE_REF
						pageHeaderModel.getSubject(), // SUBJECT
						pageHeaderModel.getSeriesID(), // SERIES_ID
						pageHeaderModel.getProtocolID(), // PROTOCOL_ID
						pageHeaderModel.getSpid(), // SPID
						pageHeaderModel.getBatchOwner(), // BATCH_OWNER
						pageHeaderModel.getBatchCreator(), // BATCH_CREATOR
						pageHeaderModel.getDesignSubmitter(), // DESIGN_SUBMITTER
						pageHeaderModel.getProcedure(), // PROCEDURE
						pageHeaderModel.getKey() // PAGE_KEY
						});

				log.debug("NotebookPage Update Result :" + result + " for " + pageHeaderModel.getKey());
			} else {
				// just update the timestamp to tell Page content ( like steps etc has changed )
				pageHeaderModel.setModificationDateAsTimestamp(CommonUtils.getCurrentTimestamp());
				String updateSql = UpdateQueryGenerator.getNotebookPageModifiedDateUpdateQuery(pageHeaderModel);
				SqlUpdate su = getSqlUpdate(updateSql);
				su.setTypes(new int[] { Types.TIMESTAMP });
				su.compile();
				log.debug("Before NotebookPage Update Call");
				int result = su.update(new Object[] { pageHeaderModel.getModificationDateAsTimestamp() });

				log.debug("NotebookPage Update Result :" + result + " for " + pageHeaderModel.getKey());
			}

		} catch (Exception updateError) {
			throw new JDBCRuntimeException(updateError);
		}
	}

	/**
	 * @param pageKey
	 * @param rxnSteps
	 * @throws DAOException
	 * @throws JDBCRuntimeException
	 */
	public void updateReactionSteps(String pageKey, List<ReactionStepModel> rxnSteps) throws DAOException {
		DAOFactory factory = getDaoFactory();
		try {
			for (int i = 0; i < rxnSteps.size(); i++) {
				ReactionStepModel rxnStep = rxnSteps.get(i);
				if (rxnStep.getRxnScheme() != null && rxnStep.getRxnScheme().isModelChanged()) {
					updateReactionScheme(rxnStep.getRxnScheme());
				} else {
					log.debug("ReactionScheme model hasn't changed. No update done for step key:" + rxnStep.getKey());
				}
				if (rxnStep.isModelChanged()) {
					updateReactionStep(rxnStep);
				} else {
					log.debug("ReactionStep model hasn't changed. No update done for step key:" + rxnStep.getKey());
				}
				// updating Monomer Batches
				List<BatchesList<MonomerBatchModel>>  monomersBatches = rxnStep.getMonomers();
				List<BatchesList<MonomerBatchModel>> insertableMonomerBatchesList = new ArrayList<BatchesList<MonomerBatchModel>>(monomersBatches.size());
				for (int j = 0; j < monomersBatches.size(); j++) {
					BatchesList<MonomerBatchModel> blist = monomersBatches.get(j);
					// if Deduped = true DON'T update
					if (!blist.isDedupedList()) {
						if (blist.isLoadedFromDB()) {
							List<MonomerBatchModel> batches = blist.getBatchModels();
							updateMonomerBatches(batches);
						} else {
							insertableMonomerBatchesList.add(blist);
						}
					}
				}
				// calling insert if not already inserted
				if (insertableMonomerBatchesList.size() > 0) {
					NotebookInsertDAO insertDAO = factory.getNotebookInsertDAO();
					insertDAO.createParallelMonomerBatches(rxnStep.getKey(), pageKey, insertableMonomerBatchesList, true);
				}
				
				// updating Batches added in stoich
				this.checkAndUpdateStoicBatch(pageKey, rxnStep.getKey(), rxnStep.getStoicBatchesList());

				// updating Product Batches in each ProductBatchesList (if DSP(update),if PUA (insert,del,update))
				List<BatchesList<ProductBatchModel>> productBatchesList = rxnStep.getProducts();
				List<BatchesList<ProductBatchModel>> insertableBatchesList = new ArrayList<BatchesList<ProductBatchModel>>(productBatchesList.size());
				for (int k = 0; k < productBatchesList.size(); k++) {
					BatchesList<ProductBatchModel> blist = productBatchesList.get(k);
					// if Deduped = true DON'T update
					if (!blist.isDedupedList()) {
						if (blist.isLoadedFromDB()) {
							//This list record exisits and batches to this list needs to be handled
							//We are passing list of BatchModels in a BatchesList object
							updateProductBatches(rxnStep.getKey(), pageKey, blist.getBatchModels(), blist.getKey());
						} else {
							//This is a new List with batches that needs to be inserted.
							//We are passing list of BatchesList objects
							insertableBatchesList.add(blist);
						}
					}
				}
				// calling insert if not already inserted
				if (insertableBatchesList.size() > 0) {
					NotebookInsertDAO insertDAO = factory.getNotebookInsertDAO();
					insertDAO.createParallelProductBatches(rxnStep.getKey(), pageKey, insertableBatchesList, true);
				}
				// updating monomer plates
				List<MonomerPlate> mPlates = rxnStep.getMonomerPlates();
				if (mPlates.size() > 0) {
					updateMonomerPlates(pageKey, rxnStep.getKey(), mPlates);
				}
				// updating product plates
				List<ProductPlate> pPlates = rxnStep.getProductPlates();
				if (pPlates.size() > 0) {
					updateProductPlates(pageKey, rxnStep.getKey(), pPlates);
				}
			}
		} catch (Throwable updateError) {
			this.releaseDaoFactory(factory);
			throw new JDBCRuntimeException(updateError);
		}
	}

	/**
	 * Update only Reaction step and Reaction scheme
	 * 
	 * @param rxnStepModel
	 * @throws JDBCRuntimeException
	 */
	private void updateReactionStep(ReactionStepModel rxnStepModel) {
		try {
			SqlUpdate su = new ReactionStepUpdate(this.getDataSource());
			su.compile();
			log.debug("Before ReactionStep Update Call");

			int result = su.update(new Object[] {
					CommonUtils.replaceSpecialCharsInText(rxnStepModel.toXML()),
					rxnStepModel.getKey() });
			log.debug("ReactionStep Update result :" + result);
			log.debug("ReactionStep Update Result :" + result + " for " + rxnStepModel.getKey());
		} catch (Throwable updateError) {
			throw new JDBCRuntimeException(updateError);
		}
	}

	/**
	 * @param rxnSchemeModel
	 * @throws JDBCRuntimeException
	 */
	public void updateReactionScheme(ReactionSchemeModel rxnSchemeModel) {
		try {
			SqlUpdate su = new ReactionSchemeUpdate(this.getDataSource());
			su.compile();
			log.debug("Before ReactionScheme Update  Call");
			int result = su.update(new Object[] { rxnSchemeModel.getVrxId(), rxnSchemeModel.getProtocolId(),
					rxnSchemeModel.getStringSketch(),
					rxnSchemeModel.getNativeSketch(),
					rxnSchemeModel.getViewSketch(),
					CommonUtils.replaceSpecialCharsInText(rxnSchemeModel.toXML()).getBytes(),
					rxnSchemeModel.getStringSketchFormat(), rxnSchemeModel.getNativeSketchFormat(), null,
					rxnSchemeModel.getSythesisRouteReference(), rxnSchemeModel.getReactionId(), rxnSchemeModel.getKey() });

			log.debug("ReactionScheme Update Result :" + result + " for " + rxnSchemeModel.getKey());
		} catch (Throwable updateError) {
			throw new JDBCRuntimeException(updateError);
		}
	}

	/**
	 * Updates a set of plates( wells in it )
	 * 
	 * @param pageKey
	 * @param registeredPlates
	 * @throws DAOException
	 */
	public void updateRegisteredPlates(String pageKey, List<ProductPlate> registeredPlates) throws DAOException {
		List<ProductPlate> updatablePlates = new ArrayList<ProductPlate>();
		List<ProductPlate> insertablePlates = new ArrayList<ProductPlate>();
		PlateDAO dao = getDaoFactory().getPlateDAO();
		/*
		 * Looping to split plates in 2 different list to upate or to insert new
		 */

		for (int i = 0; i < registeredPlates.size(); i++) {
			ProductPlate plate = registeredPlates.get(i);
			if (plate.isLoadedFromDB())
				updatablePlates.add(plate);
			else
				insertablePlates.add(plate);
		}
		if (updatablePlates.size() > 0) {
			dao.updatePlates(updatablePlates);
		}
		if (insertablePlates.size() > 0) {
			dao.insertRegisteredPlates(pageKey, insertablePlates);
		}
	}

	/**
	 * @param pageKey
	 * @param stepKey
	 * @param productPlates
	 * @throws DAOException
	 */
	public void updateProductPlates(String pageKey, String stepKey, List<ProductPlate> productPlates) throws DAOException {
		List<ProductPlate> updatablePlates = new ArrayList<ProductPlate>();
		List<ProductPlate> insertablePlates = new ArrayList<ProductPlate>();
		PlateDAO dao = getDaoFactory().getPlateDAO();
		/*
		 * Looping to split plates in 2 different list to update or to insert new
		 */

		for (int i = 0; i < productPlates.size(); i++) {
			ProductPlate plate = productPlates.get(i);
			if (plate.isLoadedFromDB())
				updatablePlates.add(plate);
			else
				insertablePlates.add(plate);
		}
		if (updatablePlates.size() > 0) {
			dao.updatePlates(updatablePlates);
		}
		if (insertablePlates.size() > 0) {
			dao.insertProductPlates(pageKey, stepKey, insertablePlates);
		}
	}

	/**
	 * @param pageKey
	 * @param stepKey
	 * @param monomerPlates
	 * @throws DAOException
	 */
	public void updateMonomerPlates(String pageKey, String stepKey, List<MonomerPlate> monomerPlates) throws DAOException {
		List<MonomerPlate> updatablePlates = new ArrayList<MonomerPlate>();
		List<MonomerPlate> insertablePlates = new ArrayList<MonomerPlate>();
		PlateDAO dao = getDaoFactory().getPlateDAO();
		/*
		 * Looping to split plates in 2 different list to update or to insert new
		 */

		for (int i = 0; i < monomerPlates.size(); i++) {
			MonomerPlate plate = monomerPlates.get(i);
			if (plate.isLoadedFromDB())
				updatablePlates.add(plate);
			else
				insertablePlates.add(plate);
		}
		if (updatablePlates.size() > 0) {
			dao.updatePlates(updatablePlates);
		}
		if (insertablePlates.size() > 0) {
			dao.insertMonomerPlates(pageKey, stepKey, insertablePlates);
		}
	}

	/**
	 * Updates a product batch
	 * 
	 * @param productBatch
     * @throws JDBCRuntimeException
	 */
	public void updateProductBatch(ProductBatchModel productBatch) {
		try {
			log.debug("updateProductBatch(structure,batch,amount,registredbatch update).enter");
			ParentCompoundModel compound = productBatch.getCompound();
			if (compound != null && compound.isModelChanged()) {
				updateCompound(compound);
			} else {
				log.debug("ParentCompoundModel unchanged.Skipped Structure table update");
			}
			Double theoYldVal = null;
			if (!productBatch.getTheoreticalYieldPercentAmount().isValueDefault()) {
				theoYldVal = new Double(productBatch.getTheoreticalYieldPercentAmount().doubleValue());
			}

			SqlUpdate su = new ProductBatchUpdate(this.getDataSource());

			log.debug("Before productBatch Update  Call");
			
			String chloracnegenType = productBatch.getChloracnegenType();
			chloracnegenType = CommonUtils.convertChloracnegenType(chloracnegenType, true);
			
			int result = su.update(new Object[] {
					productBatch.getBatchNumber().getBatchNumber(), // BATCH_NUMBER
					CommonUtils.replaceSpecialCharsinXML(productBatch.toXML()), // XML_METADATA
					productBatch.getBatchType().toString(), // BATCH_TYPE
					productBatch.getMolecularFormula(), // MOLECULAR_FORMULA
					theoYldVal, // THEORITICAL_YIELD_PERCENT
					productBatch.getSaltForm().getCode(), // SALT_CODE
					new Double(productBatch.getSaltEquivs()), // SALT_EQUIVS
					new Double(productBatch.getMolecularWeightAmount().doubleValue()),
					productBatch.getMolecularWeightAmount().getUnit().getCode(),
					productBatch.getMolecularWeightAmount().isCalculated() == true ? "Y" : "N",
					new Integer(productBatch.getMolecularWeightAmount().getSigDigits()),
					productBatch.getMolecularWeightAmount().getSigDigitsSet() == true ? "Y" : "N",
					new Integer(productBatch.getMolecularWeightAmount().getUserPrefFigs()),
					productBatch.isLimiting() == true ? "Y" : "N", productBatch.isAutoCalcOn() == true ? "Y" : "N",
					productBatch.getSynthesizedBy(), productBatch.getSolventsAdded(), null,
					new Integer(productBatch.getTransactionOrder()), chloracnegenType,
					productBatch.isChloracnegen() == true ? "Y" : "N", productBatch.isTestedForChloracnegen() == true ? "Y" : "N",
					productBatch.getKey() // BATCH_KEY
					});
			log.debug("productBatch Update Result :" + result + " for " + productBatch.getKey());

			updateProductBatchAmounts(productBatch);
			BatchRegInfoModel regInfo = productBatch.getRegInfo();
			if (regInfo != null && regInfo.isModelChanged()) {
				updateRegisteredBatchInfo(regInfo, productBatch.getComments(), productBatch.getContinueStatus(), productBatch
						.getSelectivityStatus());
			} else {
				log.debug("BatchRegInfoModel unchanged.Skipped Registred batches table update");
			}
			log.debug("updateProductBatch().exit");
		} catch (Throwable updateError) {
			throw new JDBCRuntimeException(updateError);
		}
	}

	/**
	 * updates a set of product batches ( same method call for RegisteredBatches )
	 * 
	 * @param productBatches
	 * @throws DAOException
	 */
	public void updateProductBatches(String stepKey, String pageKey, List<ProductBatchModel> productBatches, String listKey) throws DAOException {
		List<ProductBatchModel> deletableBatches = new ArrayList<ProductBatchModel>(productBatches.size());
		ArrayList<ProductBatchModel> insertableBatches = new ArrayList<ProductBatchModel>(productBatches.size());
		ProductBatchModel pBatch = null;
		for (int i = 0; i < productBatches.size(); i++) {
			pBatch = productBatches.get(i);
			log.debug("updateProductBatches()--> batch key:" + pBatch.getKey() + " isSetToDelete:" + pBatch.isSetToDelete()
					+ " isLoadedFromDB:" + pBatch.isLoadedFromDB() + " isModelChanged:" + pBatch.isModelChanged());
			if (pBatch.isSetToDelete()) {
				deletableBatches.add(pBatch);
			} else if (pBatch.isLoadedFromDB() && pBatch.isModelChanged()) {
				updateProductBatch(pBatch);
			} else if (!pBatch.isLoadedFromDB()) {
				// new PUA batches added and these needs to be inserted.
				insertableBatches.add(pBatch);
			}
		}
		this.deleteBatches(deletableBatches);
		this.insertNewProductBatchesForPageUpdate(stepKey, pageKey, insertableBatches, listKey);
	}

	/**
	 * @param mbatch
     * @throws JDBCRuntimeException
	 */
	public void updateMonomerBatchAmounts(MonomerBatchModel batchModel) {
		try {
			log.debug("updateMonomerBatchAmounts().enter");

			SqlUpdate su = new CeNBatchAmountsUpdate(this.getDataSource());
			int result = su.update(new Object[] {

			new Double(batchModel.getWeightAmount().doubleValue()), // VALUE
					batchModel.getWeightAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getWeightAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getWeightAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getWeightAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getWeightAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getVolumeAmount().doubleValue()), // VALUE
					batchModel.getVolumeAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getVolumeAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getVolumeAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getVolumeAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getVolumeAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getMolarAmount().doubleValue()), // VALUE
					batchModel.getMolarAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getMolarAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getMolarAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getMolarAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getMolarAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getMoleAmount().doubleValue()), // VALUE
					batchModel.getMoleAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getMoleAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getMoleAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getMoleAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getMoleAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getDensityAmount().doubleValue()), // VALUE
					batchModel.getDensityAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getDensityAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getDensityAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getDensityAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getDensityAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getPurityAmount().doubleValue()), // VALUE
					batchModel.getPurityAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getPurityAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getPurityAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getPurityAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getPurityAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getLoadingAmount().doubleValue()), // VALUE
					batchModel.getLoadingAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getLoadingAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getLoadingAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getLoadingAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getLoadingAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getRxnEquivsAmount().doubleValue()), // VALUE
					batchModel.getRxnEquivsAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getRxnEquivsAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getRxnEquivsAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getRxnEquivsAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getRxnEquivsAmount().getUserPrefFigs()), // USER_PREF_FIGS

					null, // VALUE
					null,// UNIT_CODE
					"Y", // CALCULATED
					null, // SIG_DIGITS
					"N", // SIG_DIGITS_SET
					null, // USER_PREF_FIGS

					null, // VALUE
					null,// UNIT_CODE
					"Y", // CALCULATED
					null, // SIG_DIGITS
					"N", // SIG_DIGITS_SET
					null, // USER_PREF_FIGS

					new Double(batchModel.getTotalWeight().doubleValue()), // VALUE
					batchModel.getTotalWeight().getUnit().getCode(), // UNIT_CODE
					batchModel.getTotalWeight().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getTotalWeight().getSigDigits()), // SIG_DIGITS
					batchModel.getTotalWeight().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getTotalWeight().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getTotalVolume().doubleValue()), // VALUE
					batchModel.getTotalVolume().getUnit().getCode(), // UNIT_CODE
					batchModel.getTotalVolume().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getTotalVolume().getSigDigits()), // SIG_DIGITS
					batchModel.getTotalVolume().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getTotalVolume().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getDeliveredWeight().doubleValue()), // VALUE
					batchModel.getDeliveredWeight().getUnit().getCode(), // UNIT_CODE
					batchModel.getDeliveredWeight().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getDeliveredWeight().getSigDigits()), // SIG_DIGITS
					batchModel.getDeliveredWeight().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getDeliveredWeight().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getDeliveredVolume().doubleValue()), // VALUE
					batchModel.getDeliveredVolume().getUnit().getCode(), // UNIT_CODE
					batchModel.getDeliveredVolume().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getDeliveredVolume().getSigDigits()), // SIG_DIGITS
					batchModel.getDeliveredVolume().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getDeliveredVolume().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getAmountNeeded().doubleValue()), // VALUE
					batchModel.getAmountNeeded().getUnit().getCode(), // UNIT_CODE
					batchModel.getAmountNeeded().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getAmountNeeded().getSigDigits()), // SIG_DIGITS
					batchModel.getAmountNeeded().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getAmountNeeded().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getExtraNeeded().doubleValue()), // VALUE
					batchModel.getExtraNeeded().getUnit().getCode(), // UNIT_CODE
					batchModel.getExtraNeeded().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getExtraNeeded().getSigDigits()), // SIG_DIGITS
					batchModel.getExtraNeeded().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getExtraNeeded().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getSoluteAmount().doubleValue()), // VALUE
					batchModel.getSoluteAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getSoluteAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getSoluteAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getSoluteAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getSoluteAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getPreviousMolarAmount().doubleValue()), // VALUE
					batchModel.getPreviousMolarAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getPreviousMolarAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getPreviousMolarAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getPreviousMolarAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getPreviousMolarAmount().getUserPrefFigs()), // USER_PREF_FIGS
					null, // VALUE
					null,// UNIT_CODE
					"Y", // CALCULATED
					null, // SIG_DIGITS
					"N", // SIG_DIGITS_SET
					null, // USER_PREF_FIGS

					batchModel.getKey() // BATCH_KEY
					});
			log.debug("MonomerBatch Amount Update Result :" + result + " for " + batchModel.getKey());
			log.debug("updateMonomerBatchAmounts().exit");
		} catch (Throwable updateError) {
			throw new JDBCRuntimeException(updateError);
		}
	}

	/**
	 * @param pbatch
     * @throws JDBCRuntimeException
	 */
	public void updateProductBatchAmounts(ProductBatchModel batchModel) {
		try {
			log.debug("updateProductBatchAmounts().enter");
			SqlUpdate su = new CeNBatchAmountsUpdate(this.getDataSource());
			int result = su.update(new Object[] { new Double(batchModel.getWeightAmount().doubleValue()), // VALUE
					batchModel.getWeightAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getWeightAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getWeightAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getWeightAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getWeightAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getVolumeAmount().doubleValue()), // VALUE
					batchModel.getVolumeAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getVolumeAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getVolumeAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getVolumeAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getVolumeAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getMolarAmount().doubleValue()), // VALUE
					batchModel.getMolarAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getMolarAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getMolarAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getMolarAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getMolarAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getMoleAmount().doubleValue()), // VALUE
					batchModel.getMoleAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getMoleAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getMoleAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getMoleAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getMoleAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getDensityAmount().doubleValue()), // VALUE
					batchModel.getDensityAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getDensityAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getDensityAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getDensityAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getDensityAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getPurityAmount().doubleValue()), // VALUE
					batchModel.getPurityAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getPurityAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getPurityAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getPurityAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getPurityAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getLoadingAmount().doubleValue()), // VALUE
					batchModel.getLoadingAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getLoadingAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getLoadingAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getLoadingAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getLoadingAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getRxnEquivsAmount().doubleValue()), // VALUE
					batchModel.getRxnEquivsAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getRxnEquivsAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getRxnEquivsAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getRxnEquivsAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getRxnEquivsAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getTheoreticalWeightAmount().doubleValue()), // VALUE
					batchModel.getTheoreticalWeightAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getTheoreticalWeightAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getTheoreticalWeightAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getTheoreticalWeightAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getTheoreticalWeightAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getTheoreticalMoleAmount().doubleValue()), // VALUE
					batchModel.getTheoreticalMoleAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getTheoreticalMoleAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getTheoreticalMoleAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getTheoreticalMoleAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getTheoreticalMoleAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getTotalWeight().doubleValue()), // VALUE
					batchModel.getTotalWeight().getUnit().getCode(), // UNIT_CODE
					batchModel.getTotalWeight().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getTotalWeight().getSigDigits()), // SIG_DIGITS
					batchModel.getTotalWeight().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getTotalWeight().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getTotalVolume().doubleValue()), // VALUE
					batchModel.getTotalVolume().getUnit().getCode(), // UNIT_CODE
					batchModel.getTotalVolume().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getTotalVolume().getSigDigits()), // SIG_DIGITS
					batchModel.getTotalVolume().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getTotalVolume().getUserPrefFigs()), // USER_PREF_FIGS

					null, // VALUE
					null,// UNIT_CODE
					"Y", // CALCULATED
					null, // SIG_DIGITS
					"N", // SIG_DIGITS_SET
					null, // USER_PREF_FIGS

					null, // VALUE
					null,// UNIT_CODE
					"Y", // CALCULATED
					null, // SIG_DIGITS
					"N", // SIG_DIGITS_SET
					null, // USER_PREF_FIGS

					null, // VALUE
					null,// UNIT_CODE
					"Y", // CALCULATED
					null, // SIG_DIGITS
					"N", // SIG_DIGITS_SET
					null, // USER_PREF_FIGS

					null, // VALUE
					null,// UNIT_CODE
					"Y", // CALCULATED
					null, // SIG_DIGITS
					"N", // SIG_DIGITS_SET
					null, // USER_PREF_FIGS

					new Double(batchModel.getSoluteAmount().doubleValue()), // VALUE
					batchModel.getSoluteAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getSoluteAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getSoluteAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getSoluteAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getSoluteAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getPreviousMolarAmount().doubleValue()), // VALUE
					batchModel.getPreviousMolarAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getPreviousMolarAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getPreviousMolarAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getPreviousMolarAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getPreviousMolarAmount().getUserPrefFigs()), // USER_PREF_FIGS

					new Double(batchModel.getTheoreticalYieldPercentAmount().doubleValue()), // VALUE
					batchModel.getTheoreticalYieldPercentAmount().getUnit().getCode(), // UNIT_CODE
					batchModel.getTheoreticalYieldPercentAmount().isCalculated() == true ? "Y" : "N", // CALCULATED
					new Integer(batchModel.getTheoreticalYieldPercentAmount().getSigDigits()), // SIG_DIGITS
					batchModel.getTheoreticalYieldPercentAmount().getSigDigitsSet() == true ? "Y" : "N", // SIG_DIGITS_SET
					new Integer(batchModel.getTheoreticalYieldPercentAmount().getUserPrefFigs()), // USER_PREF_FIGS
					batchModel.getKey() // BATCH_KEY
					});
			log.debug("ProductBatch Amount Update Result :" + result + " for " + batchModel.getKey());
			log.debug("updateProductBatchAmounts().exit");
		} catch (Throwable updateError) {
			throw new JDBCRuntimeException(updateError);
		}
	}

	/**
	 * Updates registration information.
	 * 
	 * @param batchRegInfo
     * @throws JDBCRuntimeException
	 */
	public void updateRegisteredBatchInfo(BatchRegInfoModel regBatch, String batchComments, int contStatus, int selStatus)
	{
		try {
			log.debug("updateRegisteredBatchInfo().enter");
			SqlUpdate su = new RegisteredBatchUpdate(this.getDataSource());

			log.debug("Before RegisteredBatch Update  Call");
			Object[] newValues = new Object[] {
					regBatch.getBatchTrackingId() == -1 ? null : new Long(regBatch.getBatchTrackingId()), // BATCH_TRACKING_ID
					regBatch.getJobId(), // JOB_ID
					new Integer(regBatch.getOffset()), // COMPOUND_REGISTRATION_OFFSET
					regBatch.getParentBatchNumber(), // PARENT_BATCH_NUMBER
					regBatch.getConversationalBatchNumber(), // CONVERSATIONAL_BATCH_NUMBER
					regBatch.getRegistrationStatus(), // REGISTRATION_STATUS
					regBatch.getSubmissionStatus(), // SUBMISSION_STATUS
					regBatch.getStatus(), // STATUS
					StorageUtils.getMessageSubstring(regBatch.getCompoundManagementStatus(), 253), // COMPOUND_MANAGEMENT_STATUS
					StorageUtils.getMessageSubstring(regBatch.getCompoundManagementStatusMessage(), 253), // COMPOUND_MGMT_STATUS_MESSAGE
					StorageUtils.getMessageSubstring(regBatch.getCompoundAggregationStatus(), 253), // COMPOUND_AGGREGATION_STATUS
					StorageUtils.getMessageSubstring(regBatch.getCompoundAggregationStatusMessage(), 253), // CMPD_AGGREGATION_STATUS_MSG
					StorageUtils.getMessageSubstring(regBatch.getPurificationServiceStatus(), 253), // PURIFICATION_SERVICE_STATUS
					StorageUtils.getMessageSubstring(regBatch.getPurificationServiceStatusMessage(), 253), // PUR_SERVICE_STATUS_MSG
					StorageUtils.getMessageSubstring(regBatch.getCompoundRegistrationStatusMessage(), 253), // COMPOUND_REG_STATUS_MESSAGE
					CommonUtils.replaceSpecialCharsinXML(regBatch.toXML()),// XML_METADATA
					new Double(regBatch.getMeltPointRange().getLower()), new Double(regBatch.getMeltPointRange().getUpper()),
					regBatch.getMeltPointRange().getComment(), regBatch.getVendorInfo().getCode(),
					regBatch.getVendorInfo().getSupplierCatalogRef(), regBatch.getCompoundSource(),
					regBatch.getCompoundSourceDetail(), regBatch.getComments(), regBatch.getCompoundState(),
					regBatch.getHazardComments(), regBatch.getHandlingComments(), regBatch.getStorageComments(),
					regBatch.getProtectionCode(), contStatus + "", selStatus + "", regBatch.getHitId(),
					 regBatch.getBatchVnVInfo().getStatus(), regBatch.getOwner(),
					regBatch.isProductFlag() == true ? "Y" : "N", regBatch.getIntermediateOrTest(),	regBatch.getKey() // REG_BATCH_KEY
							};
			int result = su.update(newValues);
			log.debug("RegisteredBatch Update Result :" + result + " for " + regBatch.getKey() + " with jobid:"
					+ regBatch.getJobId());
			log.debug("updateRegisteredBatchInfo().exit");
		} catch (Throwable updateError) {
			throw new JDBCRuntimeException(updateError);
		}
	}

	/**
	 * @param compound
     * @throws JDBCRuntimeException
	 */
	public void updateCompound(ParentCompoundModel compound) {
		try {
			SqlUpdate su = new CompoundUpdate(this.getDataSource());

			log.debug("Before compound Update  Call");
			int result = su.update(new Object[] {
					compound.getStringSketch(), // STRUCT_SKETCH
					compound.getNativeSketch(), // NATIVE_STRUCT_SKETCH
					compound.getViewSketch(), // STRUCT_IMAGE
					CommonUtils.replaceSpecialCharsinXML(compound.toXML()), // XML_METADATA
					compound.getChemicalName(), // CHEMICAL_NAME
					compound.getMolFormula(), // MOLECULAR_FORMULA
					new Double(compound.getMolWgt()), // MOLECULAR_WEIGHT
					compound.getVirtualCompoundId(), // VIRTUAL_COMPOUND_ID
					compound.getRegNumber(), // REGISTRATION_NUMBER
					compound.getStringSketchFormat(), compound.getNativeSketchFormat(), null, compound.getHazardComments(), 
					compound.getStructureComments(), compound.getStereoisomerCode(), compound.getCompoundName(),
					new Double(compound.getBoilingPt().doubleValue()), compound.getBoilingPt().getUnit().getCode(),
					new Double(compound.getMeltingPt().doubleValue()), compound.getMeltingPt().getUnit().getCode(),
					compound.isCreatedByNotebook() == true ? "Y" : "N", new Double(compound.getExactMass()),
					compound.getCompoundParent(), compound.getKey() // STRUCT_KEY
					});

			log.debug("compound Update Result :" + result + " for " + compound.getKey());
		} catch (Throwable updateError) {
			throw new JDBCRuntimeException(updateError);
		}
	}

	/**
	 * @param monomerBatch
     * @throws JDBCRuntimeException
	 */
	public void updateMonomerBatch(MonomerBatchModel monomerBatch) {
		log.debug("updateMonomerBatch.enter()");
		try {
			ParentCompoundModel compound = monomerBatch.getCompound();
			if (compound != null && compound.isModelChanged()) {
				updateCompound(compound);
			}

			SqlUpdate su = new MonomerBatchUpdate(this.getDataSource());

			int result = su.update(new Object[] {
					monomerBatch.getBatchNumber().getBatchNumber(), // BATCH_NUMBER
					CommonUtils.replaceSpecialCharsinXML(monomerBatch.toXML()), // XML_METADATA
					monomerBatch.getBatchType().toString(), // BATCH_TYPE
					monomerBatch.getMolecularFormula(), // MOLECULAR_FORMULA
					monomerBatch.getSaltForm().getCode(), // SALT_CODE
					new Double(monomerBatch.getSaltEquivs()), // SALT_EQUIVS
					new Double(monomerBatch.getMolecularWeightAmount().doubleValue()),
					monomerBatch.getMolecularWeightAmount().getUnit().getCode(),
					monomerBatch.getMolecularWeightAmount().isCalculated() == true ? "Y" : "N",
					new Integer(monomerBatch.getMolecularWeightAmount().getSigDigits()),
					monomerBatch.getMolecularWeightAmount().getSigDigitsSet() == true ? "Y" : "N",
					new Integer(monomerBatch.getMolecularWeightAmount().getUserPrefFigs()),
					monomerBatch.isLimiting() == true ? "Y" : "N", monomerBatch.isAutoCalcOn() == true ? "Y" : "N",
					monomerBatch.getSynthesizedBy(), monomerBatch.getSolventsAdded(), new Integer(monomerBatch.getNoOfTimesUsed()),
					new Integer(monomerBatch.getTransactionOrder()), monomerBatch.getChloracnegenType(),
					monomerBatch.isChloracnegen() == true ? "Y" : "N", monomerBatch.isTestedForChloracnegen() == true ? "Y" : "N",
					monomerBatch.getKey() // BATCH_KEY
					});
			updateMonomerBatchAmounts(monomerBatch);
			log.debug("MonomerBatch Update Result :" + result + " for " + monomerBatch.getKey());
		} catch (Throwable updateError) {
			throw new JDBCRuntimeException(updateError);
		}
	}

	/**
	 * @param monomerBatches
	 * @throws DAOException
	 */
	public void updateMonomerBatches(List<MonomerBatchModel> monomerBatches) throws DAOException {
		List<MonomerBatchModel> deletableBatches = new ArrayList<MonomerBatchModel>(monomerBatches.size());
		MonomerBatchModel mBatch = null;
		for (int i = 0; i < monomerBatches.size(); i++) {
			mBatch = monomerBatches.get(i);
			if (mBatch.isSetToDelete()) {
				deletableBatches.add(mBatch);
			} else {
				updateMonomerBatch(mBatch);
			}
		}
		deleteBatches(deletableBatches);
	}

	/**
	 * @param deletableBatches
	 * @throws DAOException
	 */
	public void deleteBatches(List<? extends BatchModel> deletableBatches) throws DAOException {
		log.debug("deleteBatches().enter");
		if (deletableBatches.size() > 0) {
			DAOFactory factory = null;
			try {
				factory = DAOFactoryManager.getDAOFactory();
				NotebookRemoveDAO removeDAO = factory.getNotebookRemoveDAO();
				removeDAO.removeBatches(deletableBatches);

			} catch (Throwable error) {
				throw new DAOException(error);
			} finally {
				DAOFactoryManager.release(factory);
			}
		} else {
			log.debug("No batches marked for delete.");
		}
		log.debug("deleteBatches().exit");
	}

	/**
	 * @param pageKey
	 * @param status
	 * @param modifiedDate
	 * @throws DAOException
	 */
	public void updateNotebookPageStatus(String pageKey, String status, Timestamp modifiedDate) throws DAOException {
		try {
			String updateSql = UpdateQueryGenerator.getNotebookPageStatusUpdateQuery(pageKey, status);
			SqlUpdate su = getSqlUpdate(updateSql);
			su.setTypes(new int[] { Types.TIMESTAMP });
			su.compile();
			log.debug("Before NBKPage Status Update  Call");
			int result = su.update(new Object[] { modifiedDate });
			log.debug("NBKPage Status Update Result :" + result + " for " + pageKey);
		} catch (Throwable error) {
			throw new DAOException("Failed to update notebook page status for pageKey: " + pageKey + " to status: " + status, error);
		}
	}

	/**
	 * Checks of the batches are for update or for insert and performs the tasks.
	 * 
	 * @param pageKey
	 * @param stepKey
	 * @param batchesListObj
	 * @throws JDBCRuntimeException
	 */
	private void checkAndUpdateStoicBatch(String pageKey, String stepKey, BatchesList<MonomerBatchModel> batchesListObj) {
		DAOFactory factory = null;
		
		try {
			factory = DAOFactoryManager.getDAOFactory();
			
			List<MonomerBatchModel> batches = batchesListObj.getBatchModels();
			List<MonomerBatchModel> updatableBatches = new ArrayList<MonomerBatchModel>(batches.size());
			List<MonomerBatchModel> insertableBatches = new ArrayList<MonomerBatchModel>(batches.size());
			List<MonomerBatchModel> deletableBatches = new ArrayList<MonomerBatchModel>(batches.size());
			
			for (MonomerBatchModel batch : batches) {
				JdbcTemplate jt = new JdbcTemplate();
				jt.setDataSource(this.getDataSource());
				
				String query = "SELECT COUNT(*) FROM CEN_BATCHES WHERE BATCH_KEY='" + batch.getKey() + "'";
				log.debug(query);
				
				int result = jt.queryForInt(query);				
				if (result > 0) {
					if (batch.isToBeDeleted()) {
						deletableBatches.add(batch);
					} else {
						updatableBatches.add(batch);
					}
				} else {
					if (!batch.isToBeDeleted()) { // If page model just created and we added and then removed batches
						insertableBatches.add(batch);
					}
				}
			}
			
			if (updatableBatches.size() > 0) {
				updateMonomerBatches(updatableBatches);
			}
			
			if (insertableBatches.size() > 0) {
				NotebookInsertDAO insert = factory.getNotebookInsertDAO();
				batchesListObj.setBatchModels(insertableBatches);
				insert.createStoicMonomerBatches(stepKey, pageKey, batchesListObj);
			}
			
			if (deletableBatches.size() > 0) {
				deleteBatches(deletableBatches);
			}
		} catch (Throwable updateError) {
			log.error("Error updating stoic batches: ", updateError);
			throw new JDBCRuntimeException(updateError);
		} finally {
			DAOFactoryManager.release(factory);
		}
	}

	/**
	 * 
	 * @param stepKey
	 * @param pageKey
	 * @param productList
	 * @param listKey
	 */
	public void insertNewProductBatchesForPageUpdate(String stepKey, String pageKey, ArrayList<ProductBatchModel> productList, String listKey) {
		log.debug("insertNewProductBatchesForPageUpdate().enter");
		if (productList != null && productList.size() > 0) {
			log.debug("New ProductBatches to be inserted as part of update NBK page size:" + productList.size());
			DAOFactory factory = null;
			try {
				// Check if there is a existing List Entry
				JdbcTemplate jt = new JdbcTemplate(this.getDataSource());
				int result = jt.queryForInt(SelectQueryGenerator.getStepBatchListsCountQuery(listKey));
				if (result == 0) {
					insertList(listKey, CeNConstants.FOR_PRODUCTS_CONSTANT);
					this.insertStepBatchesList(pageKey, stepKey, listKey, CeNConstants.PRODUCTS_USER_ADDED);
					log.debug("New STEP_BATCH_LISTS record inserted for PUA");
				}
				/*
				//This is redundant since the CEN_STRUCTURES insert happens in the stored proc
				log.debug("Before Inserting Structures :" + productList.size());
				List parentCompounds = new ArrayList(productList.size());
				log.debug("Isolating Product Batches ");
				log.debug(" Size:" + productList.size());
				for (int i = 0; i < productList.size(); i++) {
					Object batch = productList.get(i);
					parentCompounds.add(((ProductBatchModel) batch).getCompound());

				}
				this.insertCompoundStructure(pageKey, parentCompounds);
				*/
				log.debug("Before Inserting Batches :" + productList.size());
				factory = this.getDaoFactory();
				ProductBatchDAO pBatchDao = factory.getProductBatchDAO();
				pBatchDao.insertProductBatchesThruStoredProcedure(stepKey, pageKey, productList, listKey);
				log.debug("insertNewProductBatchesForPageUpdate().exit");
			} catch (Throwable insertError) {
				throw new JDBCRuntimeException("Error insterting New Product batches for pageKey: " + pageKey + 
				                               " stepKey: " + stepKey + " listKey: " + listKey, insertError);
			} finally {
				this.releaseDaoFactory(factory);
			}
		} else {
			log.debug("No ProductBatches passed in the list.");
		}
	}

	public void updateRegisteredBatchRegistrationInfo(BatchRegInfoModel regBatch) throws DAOException {
		log.info("updateRegisteredBatchRegistrationInfo(BatchRegInfoModel) start");
		try {
			SqlUpdate su = new RegisteredBatchRegDataUpdate(this.getDataSource());

			if (regBatch != null) {
				log.debug("Before RegisteredBatch Update  Call");
				
				int result = su.update(new Object[] {
						regBatch.getBatchTrackingId() == -1 ? null : new Long(regBatch.getBatchTrackingId()), // BATCH_TRACKING_ID
						regBatch.getJobId(), // JOB_ID
						new Integer(regBatch.getOffset()), // COMPOUND_REGISTRATION_OFFSET
						regBatch.getParentBatchNumber(), // PARENT_BATCH_NUMBER
						regBatch.getConversationalBatchNumber(), // CONVERSATIONAL_BATCH_NUMBER
						regBatch.getRegistrationStatus(), // REGISTRATION_STATUS
						regBatch.getSubmissionStatus(), // SUBMISSION_STATUS
						regBatch.getStatus(), // STATUS
						StorageUtils.getMessageSubstring(regBatch.getCompoundManagementStatus(), 253), // COMPOUND_MANAGEMENT_STATUS
						StorageUtils.getMessageSubstring(regBatch.getCompoundManagementStatusMessage(), 253), // COMPOUND_MGMT_STATUS_MESSAGE
						StorageUtils.getMessageSubstring(regBatch.getCompoundAggregationStatus(), 253), // COMPOUND_AGGREGATION_STATUS
						StorageUtils.getMessageSubstring(regBatch.getCompoundAggregationStatusMessage(), 253), // CMPD_AGGREGATION_STATUS_MSG
						StorageUtils.getMessageSubstring(regBatch.getPurificationServiceStatus(), 253), // PURIFICATION_SERVICE_STATUS
						StorageUtils.getMessageSubstring(regBatch.getPurificationServiceStatusMessage(), 253), // PUR_SERVICE_STATUS_MSG
						StorageUtils.getMessageSubstring(regBatch.getCompoundRegistrationStatusMessage(), 253), // COMPOUND_REG_STATUS_MESSAGE
						CommonUtils.replaceSpecialCharsinXML(regBatch.toXML()), // XML_METADATA
						regBatch.getRegistrationDate(),
						regBatch.getBatchVnVInfo().getStatus(), 
						regBatch.getKey() // REG_BATCH_KEY
					});
				
				log.debug("RegisteredBatch Update Result :" + result + " for " + regBatch.getKey() + " with jobid:"	+ regBatch.getJobId());
			}
		} catch (Throwable updateError) {
			throw new JDBCRuntimeException(updateError);
		}
		log.info("updateRegisteredBatchRegistrationInfo(BatchRegInfoModel) end");
	}

	public void updateNotebookPage(String siteCode, String nbRefStr, int version, String status, byte[] pdfContent,
			String pageXMLMetadata) throws DAOException, InvalidNotebookRefException {
		log.debug("updateNotebookPage(status,pdf,xml).enter");
		if ((siteCode == null) || (siteCode.length() == 0)) {
			return;
		}
		if ((nbRefStr == null) || (nbRefStr.length() == 0)) {
			return;
		}
		if ((status == null) || (status.length() == 0)) {
			return;
		}
		if (version <= 0) {
			return;
		}
		try {
			/*
			SqlUpdate su = new NotebookPageStatusandPDFUpdate(this.getDataSource());
			int result = su.update(new Object[] { 
					status, // PAGE_STATUS
					StorageUtils.toLobObject(CommonUtils.replaceSpecialCharsInText(pageXMLMetadata).getBytes()), // XML_METADATA(XML_TYPE)
					StorageUtils.toLobObject(pdfContent), // PDF_DOCUMENT
					nbRefStr+"-"+version // NBK_REF_VERSION
					});
			 */
			//TODO workaround for STG
			//there was  exception related with update xml_meatadata
			//java.sql.SQLException: ORA-01461: can bind a LONG value only for insert into a LONG column

			SqlUpdate su = new NotebookPageStatusandPDFUpdate1(this.getDataSource());
			int result = su.update(new Object[] { 
					status, // PAGE_STATUS
					pdfContent, // PDF_DOCUMENT
					nbRefStr+"-"+version // NBK_REF_VERSION
					});


			su = new NotebookPageStatusandPDFUpdate2(this.getDataSource());
			result = su.update(new Object[] { 
					CommonUtils.replaceSpecialCharsInText(pageXMLMetadata),
					nbRefStr+"-"+version // NBK_REF_VERSION
					});
			log.debug("NotebookPage Update Result :" + result + " for " + nbRefStr);
			log.debug("updateNotebookPage(status,pdf,xml).exit");
		} catch (Throwable updateError) {
			throw new JDBCRuntimeException(updateError);
		}

	}
}
