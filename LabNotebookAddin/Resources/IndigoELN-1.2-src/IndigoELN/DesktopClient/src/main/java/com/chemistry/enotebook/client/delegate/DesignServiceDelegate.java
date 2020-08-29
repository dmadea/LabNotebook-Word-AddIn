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
package com.chemistry.enotebook.client.delegate;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.client.gui.page.experiment.stoich.PCeNStoichCalculator;
import com.chemistry.enotebook.design.delegate.DesignServiceException;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.sdk.PictureProperties;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.util.Stopwatch;
import com.chemistry.enotebook.utils.ChimeUtils;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.ParallelExpDuperDeDuper;
import com.chemistry.enotebook.utils.ParallelExpModelUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DesignServiceDelegate {
	SessionIdentifier sid = null;
	com.chemistry.enotebook.design.delegate.DesignServiceDelegate designService;

	public DesignServiceDelegate() throws DesignServiceException, Exception {
		this.designService = new com.chemistry.enotebook.design.delegate.DesignServiceDelegate();
	}

	public DesignServiceDelegate(SessionIdentifier vsid) {
		this.sid = vsid;
	}

	private static final Log log = LogFactory
			.getLog(DesignServiceDelegate.class);
	private static final CeNErrorHandler ceh = CeNErrorHandler.getInstance();

	public NotebookPageModel getNotebookPageModelFromDSP(
			SessionIdentifier sessionid, String spid, String notebook,
			String sitecode, String ntid) throws DesignServiceException,
			Exception {

		NotebookPageModel pageModel = null;
		try {
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start("DesignService.getExperimentFromDesignService() - "
					+ spid);

			pageModel = designService
					.getExperimentFromDesignService(spid, true);
			stopwatch.stop();
			// Get next experiment for the notebook
			long starttime = System.currentTimeMillis();
			StorageDelegate storageDelegate = null;
			if (this.sid == null) {
				storageDelegate = ServiceController.getStorageDelegate();
			} else {
				storageDelegate = new StorageDelegate();
			}
			int exp = storageDelegate
					.getNextExperimentForNotebook(notebook);
			long halttime2 = System.currentTimeMillis() - starttime;
			log.debug("(" + new Time(System.currentTimeMillis())
					+ ")-> getNextExperimentForNotebook :" + halttime2 / 1000
					+ "secs");
			starttime = System.currentTimeMillis();
			NotebookRef nbRef = new NotebookRef(notebook + "-" + exp);
			if (pageModel != null) {
				pageModel.setNbRef(nbRef);
			}

			// Also set the userid and site code for the page
			pageModel.setUserName(ntid);
			pageModel.setBatchOwner(ntid);
			pageModel.setBatchCreator(ntid);
			pageModel.setSiteCode(sitecode);
			Timestamp ts = CommonUtils.getCurrentTimestamp();
			pageModel.setCreationDateAsTimestamp(ts);
			pageModel.setModificationDateAsTimestamp(ts);
			pageModel.setPageType(CeNConstants.PAGE_TYPE_PARALLEL);
			// Asign notebookbatch # for the products
			this.setNotebookBatchNumbers(pageModel, nbRef);
			// Create a PseudoPlate
			List list = pageModel.getAllProductBatchModelsInThisPage();
			PseudoProductPlate psPlate = new PseudoProductPlate(
					(ArrayList) list);
			pageModel.setPseudoProductPlate(psPlate);
			// Sort the MonomerBatch List
			ParallelExpDuperDeDuper deduper = new ParallelExpDuperDeDuper();
			deduper.sortMonomerBatchesInSteps(pageModel);

			ParallelExpModelUtils utils = new ParallelExpModelUtils(pageModel);
			utils.setOrRefreshGuiPseudoProductPlate();
			utils.populateMonomerAndProductHashMaps();
			utils.linkProductBatchesAnalyticalModelInAnalysisCache();
			utils.linkBatchesWithPlateWells();
			utils.updateBatchesListAmountFlags();
			utils = null;
			deduper = null;
			calcStoichBasedonDSPScale(pageModel);
			generateImagesForReactionsAndBatches(pageModel);
		} catch (DesignServiceException de) {
			ceh.logExceptionWithoutDisplay(de, null);
			throw de;
		} catch (Exception e) {
			ceh.logExceptionWithoutDisplay(e, null);
			throw e;
		}
		return pageModel;
	}

	public NotebookPageModel getNotebookPageModelFromDSPForConception(
			SessionIdentifier sessionid, String spid, String notebook,
			String sitecode, String ntid, NotebookRef nbRef)
			throws DesignServiceException, Exception {

		NotebookPageModel pageModel = null;
		try {
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start("DesignService.getExperimentFromDesignService() - "
					+ spid);
			pageModel = designService
					.getExperimentFromDesignService(spid, true);
			stopwatch.stop();
			// Asign notebookbatch # for the products
			this.setNotebookBatchNumbers(pageModel, nbRef);
			generateImagesForReactionsAndBatches(pageModel);
		} catch (DesignServiceException de) {
			ceh.logExceptionWithoutDisplay(de, null);
			throw de;
		} catch (Exception e) {
			ceh.logExceptionWithoutDisplay(e, null);
			throw e;
		}
		return pageModel;
	}

	// This is where the lotNumber is created
	private void setNotebookBatchNumbers(NotebookPageModel pageModel,
			NotebookRef nbRef) {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.setNotebookBatchNumbers() - "
				+ nbRef.getNotebookRef());
		if (pageModel != null && nbRef != null) {
			ArrayList steps = pageModel.getIntermediateReactionSteps();
			ReactionStepModel sumStep = pageModel.getSummaryReactionStep();
			// If only summary step is present
			if (steps != null && steps.size() < 2 && sumStep != null
					&& sumStep.getMonomers() != null) {
				ArrayList productBatchesList = sumStep.getProducts();
				if (productBatchesList != null) {
					int size = productBatchesList.size();
					int lot = 1;
					for (int i = 0; i < size; i++) {
						BatchesList prodList = (BatchesList) productBatchesList
								.get(i);
						for (int k = 0; k < prodList.getBatchModels().size(); k++) {
							ProductBatchModel model = (ProductBatchModel) prodList
									.getBatchModels().get(k);
							try {
								model.setBatchNumber(new BatchNumber(nbRef
										.getNotebookRef() + "-" + lot + "A1"));
							} catch (Exception e) {
								log.error("Error creating and assigning Note book Batch number"
										+ e.getMessage());
							}
							lot = lot + 1;
						}
					}
					log.info("Final lot number after notebook batch assignment :"
							+ lot);
				}

			}// end if
				// if has intermediate steps
				// Intermediate steps products should be assigned Notebook
				// Batch#
			else {
				// Assign NB batch# first for step1 to last but one of final
				// step.
				int lot = 1;
				int totalInterimSteps = steps.size();
				int finalStepNo = totalInterimSteps;
				// start numbering from last step
				// for (int i = 1; i < totalInterimSteps; i++) {
				//
				// ReactionStepModel step = (ReactionStepModel)
				// pageModel.getReactionStep(i);
				// ArrayList productBatchesList = step.getProducts();
				// if (productBatchesList != null) {
				// int size = productBatchesList.size();
				// for (int m = 0; m < size; m++) {
				// BatchesList prodList = (BatchesList)
				// productBatchesList.get(m);
				// for (int k = 0; k < prodList.getBatchModels().size(); k++) {
				// ProductBatchModel model = (ProductBatchModel)
				// prodList.getBatchModels().get(k);
				// try {
				// model.setBatchNumber(new BatchNumber(nbRef.getNotebookRef() +
				// "-" + lot + "A1"));
				// } catch (Exception e) {
				// log.error("Error creating and assigning Note book Batch number"
				// + e.getMessage());
				// }
				// lot = lot + 1;
				// }
				// }
				// }
				//
				// }
				// Assign same NB batch# for summary products and final step
				// products
				ReactionStepModel finalstep = (ReactionStepModel) pageModel
						.getReactionStep(finalStepNo);
				if (finalstep != null) {
					ArrayList productBatchesList = finalstep.getProducts();
					// ArrayList finalproductBatchesList =
					// sumStep.getProducts();
					//
					for (int n = 0; n < productBatchesList.size(); n++) {
						BatchesList prodList = (BatchesList) productBatchesList
								.get(n);
						for (int k = 0; k < prodList.getBatchModels().size(); k++) {
							ProductBatchModel model = (ProductBatchModel) prodList
									.getBatchModels().get(k);
							try {
								model.setBatchNumber(new BatchNumber(nbRef
										.getNotebookRef() + "-" + lot + "A1"));
							} catch (Exception e) {
								log.error("Error creating and assigning Note book Batch number"
										+ e.getMessage());
							}
							lot = lot + 1;
						}
					}

					for (int i = 1; i < totalInterimSteps; i++) {

						ReactionStepModel step = (ReactionStepModel) pageModel
								.getReactionStep(i);
						productBatchesList = step.getProducts();
						if (productBatchesList != null) {
							int size = productBatchesList.size();
							for (int m = 0; m < size; m++) {
								BatchesList prodList = (BatchesList) productBatchesList
										.get(m);
								for (int k = 0; k < prodList.getBatchModels()
										.size(); k++) {
									ProductBatchModel model = (ProductBatchModel) prodList
											.getBatchModels().get(k);
									try {
										model.setBatchNumber(new BatchNumber(
												nbRef.getNotebookRef() + "-"
														+ lot + "A1"));
									} catch (Exception e) {
										log.error("Error creating and assigning Note book Batch number"
												+ e.getMessage());
									}
									lot = lot + 1;
								}
							}
						}

					}
				}
				log.info("Final lot number after notebook batch assignment :"
						+ lot);
			}// else
		}// if has data

		stopwatch.stop();
	}

	public void calcStoichBasedonDSPScale(NotebookPageModel pageModel) {
		log.info("calcStoichBasedonDSPScale(NBKPageModel).enter");
		// Check if model has data
		if (pageModel == null) {
			return;
		}
		// check if model has Design Service scale
		if (pageModel.getPageHeader().getScale() == null
				|| pageModel.getPageHeader().getScale()
						.GetValueInStdUnitsAsDouble() == 0.0) {
			return;
		}
		if (pageModel.getReactionSteps() != null) {
			int stepSize = pageModel.getReactionSteps().size();
			int stepNo = 0;
			if (stepSize > 1) {
				// Handle single step and multistep.
				stepNo = 1;
			}
			// for each step do the recursion
			for (; stepNo < stepSize; stepNo++) {
				ReactionStepModel stepModel = pageModel.getReactionStep(stepNo);
				List monList = stepModel
						.getStoicElementListInTransactionOrder();
				if (monList != null && monList.size() > 0) {
					// get the first monomer in the list and set it the Design Service
					// scale as it moles
					StoicModelInterface stoicM = (StoicModelInterface) monList
							.get(0);
					stoicM.setStoicLimiting(true);
					// set the Design Service scale
					AmountModel dspScale = pageModel.getPageHeader().getScale();
					dspScale.setLoadedFromDB(false);
					stoicM.setStoicMoleAmount(dspScale);
					log.info("Setting DSP scale" + dspScale.doubleValue()
							+ " as moles to first monomer in step:" + stepNo);
					PCeNStoichCalculator calc = new PCeNStoichCalculator(
							stepModel, CeNConstants.PAGE_TYPE_PARALLEL);
					calc.recalculateStoichBasedOnBatch(stoicM, true);
				}

			}
		}
		log.info("calcStoichBasedonDSPScale(NBKPageModel).exit");
	}

	// this method generates images for ReactionSchemes and Product Batches.
	// Monomer batch images can
	// be obtained from CLS.
	public void generateImagesForReactionsAndBatches(NotebookPageModel pageModel) {
		log.info("generateImagesForReactionsAndBatches(NBKPageModel).enter");
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("generateImagesForReactionsAndBatches(NBKPageModel)");
		// Check if model has data
		if (pageModel == null) {
			return;
		}
		// check if model has Design Service scale
		if (pageModel.getPageHeader().getScale() == null
				|| pageModel.getPageHeader().getScale()
						.GetValueInStdUnitsAsDouble() == 0.0) {
			return;
		}

		if (pageModel.getReactionSteps() != null) {
			int stepSize = pageModel.getReactionSteps().size();
			int stepNo = 0;
			for (; stepNo < stepSize; stepNo++) {
				ReactionStepModel stepModel = pageModel.getReactionStep(stepNo);
				// generate reaction scheme image
				byte[] pic = null;
				try {
					if (stepModel.getRxnScheme().getNativeSketch() != null
							&& stepModel.getRxnScheme().getNativeSketch().length > 0) {

						pic = new ChemistryDelegate().convertReactionToPicture(
								stepModel.getRxnScheme().getNativeSketch(),
								PictureProperties.PNG, 1000, 2000, 1.0, 0.26458);
						stepModel.getRxnScheme().setViewSketch(pic);
					} else if (stepModel.getRxnScheme().getStringSketch() != null
							&& stepModel.getRxnScheme().getStringSketch().length > 0) {
						byte[] rxnContent = ChimeUtils
								.toMoleFileFormatForReaction(new String(
										stepModel.getRxnScheme()
												.getStringSketch()), stepModel
										.getRxnScheme().getVrxId());
						if (rxnContent != null) {
							stepModel.getRxnScheme()
									.setNativeSketch(rxnContent);
							pic = new ChemistryDelegate()
									.convertReactionToPicture(rxnContent,
											PictureProperties.PNG, 1000, 2000,
											1.0, 0.26458);
							stepModel.getRxnScheme().setViewSketch(pic);
						}
					}
				} catch (Exception e) {
					log.error("Unable to make ReactionScheme JPEG Images:"
							+ e.getMessage());
					e.printStackTrace();
				}

			}

			// if has more than one step just generate images in intermediate
			// steps.
			if (stepSize > 1) {
				stepNo = 1;
			} else {
				stepNo = 0;
			}

			for (; stepNo < stepSize; stepNo++) {
				ReactionStepModel stepModel = pageModel.getReactionStep(stepNo);
				// generate products images

				List<ProductBatchModel> prodList = stepModel
						.getProductBatches();
				int listSize = prodList.size();
				for (int i = 0; i < listSize; i++) {
					ProductBatchModel model = prodList.get(i);
					byte[] pic = null;
					try {
						if (model.getCompound().getNativeSketch() != null
								&& model.getCompound().getNativeSketch().length > 0) {

							pic = new ChemistryDelegate()
									.convertStructureToPicture(model
											.getCompound().getNativeSketch(),
											PictureProperties.PNG, 1000, 1000,
											1.0, 0.26458);
							model.getCompound().setViewSketch(pic);
						} else if (model.getCompound().getStringSketch() != null
								&& model.getCompound().getStringSketch().length > 0) {
							byte[] MolFile = ChimeUtils
									.toMoleFileFormatForCompound(new String(
											model.getCompound()
													.getStringSketch()), model
											.getProductId());
							model.getCompound().setNativeSketch(MolFile);
							pic = new ChemistryDelegate()
									.convertStructureToPicture(MolFile,
											PictureProperties.PNG, 1000, 1000,
											1.0, 0.26458);
							model.getCompound().setViewSketch(pic);
						}
					} catch (Exception e) {
						log.error("Unable to make Product Batch JPEG Images:"
								+ e.getMessage());
					}
				}
			}
		}
		stopwatch.stop();
		log.info("generateImagesForReactionsAndBatches(NBKPageModel).exit");
	}

	private static void writeRxn(String filename, byte[] sketch) {
		try {
			FileOutputStream skcOutStrm = new FileOutputStream(filename);
			skcOutStrm.write(sketch);
			skcOutStrm.flush();
			skcOutStrm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static byte[] readIsisSketch(String filename) {
		byte[] result = null;

		try {
			File file = new File(filename);
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			int numBytes = (int) file.length();
			byte[] buff = new byte[numBytes];
			int readBytes = bis.read(buff);
			bis.close();
			result = buff;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private static String getRXNFromTheDSPDecodeRXN(String dspRXN) {
		if (dspRXN != null && !dspRXN.equals("")) {
			int index1 = dspRXN.indexOf("$RXN");
			int index2 = dspRXN.lastIndexOf(" END");
			String cleanRXN = dspRXN.substring(index1, index2 + 4);
			return cleanRXN;

		} else {
			return dspRXN;
		}
	}

	public boolean isAvailable() {
		return designService.isDesignServiceAvailable();
	}

}
