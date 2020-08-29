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

package com.chemistry.enotebook.client.gui.page.stoichiometry.search;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.client.gui.page.reagents.ReagentsHandler;
import com.chemistry.enotebook.compoundmgmtservice.delegate.CompoundMgmtServiceDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.common.codetables.SaltCodeCache;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchNumberException;
import com.chemistry.enotebook.experiment.datamodel.common.SignificantFigures;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import com.chemistry.enotebook.formatter.UtilsDispatcher;
import com.chemistry.enotebook.hazard.exceptions.HazardException;
import com.chemistry.enotebook.reagent.delegate.ReagentMgmtServiceDelegate;
import com.chemistry.enotebook.search.Compound;
import com.chemistry.enotebook.storage.StorageException;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.storage.exceptions.StorageInitException;
import com.chemistry.enotebook.utils.StructureLoadAndConversionUtil;
import com.common.chemistry.codetable.CodeTableCacheException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import javax.swing.*;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompoundAndBatchInfoUpdater {
	
	private static final Log log = LogFactory.getLog(CompoundAndBatchInfoUpdater.class);
	
	private static CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	
	private CompoundMgmtServiceDelegate cmpdMgmtServiceDelegate = null;
	private static ReagentMgmtServiceDelegate hazardInfoProvider = null;
	

	public CompoundAndBatchInfoUpdater() {
		try {
			initDelegates();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initDelegates() {
		try {
			cmpdMgmtServiceDelegate = new CompoundMgmtServiceDelegate();
			hazardInfoProvider = new ReagentMgmtServiceDelegate();
		} catch (Exception e) {
			log.error(e.getMessage());
			ceh.logExceptionMsg(null, "Could not contact CompoundManagementService or StorageService", e);
		} 
	}

	/**
	 * Main entry method for this class. Used to determine what the newNumber is: batch number or some other value. Process
	 * newNumber according to its designation. If you already know, feel free to use either getCompoundInfoFromValue, or
	 * getCompoundInfoFromBatchNumber, where batch number is a CeN batch number. Using the latter will cut down on search time.
	 * 
	 * @param batch -
	 *            the batch to fill.
	 * @param newNumber -
	 *            the number on which to query.
	 * @return boolean true if all goes as planned, false if there is a problem
	 */
	public boolean getCompoundInfo(BatchModel batch, String newNumber, boolean load) {
		boolean result = false;
		if (NotebookPageUtil.isValidCeNBatchNumber(newNumber)) {
			result = getCompoundInfoFromBatchNumber(batch, newNumber, !load, !load);
		} else {
			result = getCompoundInfoFromValue(batch, newNumber, !load, !load);
		}
		return result;
	}

	/**
	 * Dissect the value to determine if it is a CeN batch number or a conversational batch number or a compound number or something
	 * else entirely. Performs a generic search on the resulting number and returns all relevant results.
	 * 
	 * @param batch -
	 *            the batch to fill.
	 * @param value -
	 *            the number on which to query.
	 * @return boolean true if all goes as planned, false if there is a problem
	 */
	public boolean getCompoundInfoFromValue(BatchModel batch, String value, boolean displayMsgFlag, boolean overWriteFlag) {
		boolean result = false;
		if (value != null && !value.equals("")) {
			List<Compound> xmlReturn = null;
			try {
				xmlReturn = cmpdMgmtServiceDelegate.getCompoundInfoByCompoundNo(value);
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
			// fill in compound information
			if (xmlReturn != null) {
				List<BatchModel> searchReturn = processSearchReturn(xmlReturn);
				if (searchReturn.size() > 1 && (!batch.isLoadedFromDB() || overWriteFlag)) {
					if (displayMsgFlag) {
						// Get the user to select one or more responses.
						CompoundIDLookupDialog inst = new CompoundIDLookupDialog(MasterController.getGUIComponent(), value,
								searchReturn);
						inst.setVisible(true);
						if (inst.getSelectedBatch() != null) {
							if(inst.getStatus() == CompoundIDLookupDialog.SAVE) {
								BatchModel returnedBatch = inst.getSelectedBatch();
								// we have something to update with...
								updateBatch(batch, returnedBatch, overWriteFlag);
							}
							result = true;
						}
					} else {
						// If we can find a compound to load, do it. Otherwise, fail.
						BatchModel returnBatch = null;
						if (StringUtils.isNotBlank(batch.getCompound().getRegNumber()))
							for (Iterator<BatchModel> it = searchReturn.iterator(); it.hasNext() && returnBatch == null;) {
								BatchModel tBatch = it.next();
								if (StringUtils.isNotBlank(tBatch.getCompound().getRegNumber()) && batch.getCompound().getRegNumber().equals(tBatch.getCompound().getRegNumber())) {
									returnBatch = tBatch;
									updateBatch(batch, returnBatch, overWriteFlag);
									result = true;
								}
							}
					}
				} else if (searchReturn.size() == 1 && (!batch.isLoadedFromDB() || overWriteFlag)) {
					// update the batch passed to us with the information we
					// retrieved.
					updateBatch(batch, searchReturn.get(0), overWriteFlag);
					result = true;
				} else if (searchReturn.size() > 0 && (batch.isLoadedFromDB() || overWriteFlag)) {
					// Exists in DB. Do not overwrite data with CSS info
					// unless field is blank.
					BatchModel returnBatch = null;
					if (StringUtils.isNotBlank(batch.getBatchNumberAsString())) {
						for (Iterator<BatchModel> it = searchReturn.iterator(); it.hasNext() && returnBatch == null;) {
							BatchModel tBatch = it.next();
							if (tBatch != null && batch.getBatchNumberAsString().equals(tBatch.getBatchNumberAsString())) {
								returnBatch = tBatch;
								if (overWriteFlag) {
									batch.deepCopy(returnBatch);
								} else {
									// We don't want to overwrite anything
									// in the compound that was put there by
									// the user
									// like comments, names, etc. We only
									// want the info from the loadSketch()
									// action already run.
									updateBatch(batch, returnBatch, false);
								}
								result = true;
							}
						}
					}
				} else {
					ceh.logMsg(null, "Failed to process: " + value, "Compound Lookup Failed");
				}
			} else {
				// no return - so lookup via ReagentMgmtService
				// TODO: Remove when CompoundMgmtService is updated.
				ReagentsHandler rh = new ReagentsHandler();
				String searchXML = "<ReagentsLookupParams><TextDatabases DoTextSearch=\"True\">"
						+ "<Database Name=\"CPI\"><SearchFields><Field ColumnName=\"CHEMICAL.CH_MDLNUM\" "
						+ "Criteria=\"Equals\" Value=\"" + value + "\"/></SearchFields></Database>"
						+ "</TextDatabases><StructureDatabases DoStructureSearch=\"False\"/>"
						+ "<Iterating LastPosition=\"-1\" ChunkSize=\"50\"/></ReagentsLookupParams>";
				byte[] bytes = rh.doReagentSearch(searchXML);
				if (bytes != null) {
					String reagents = new String(bytes);
					// get the first element in the returned xml
					try {
						StringReader reader = new StringReader(reagents);
						SAXBuilder builder = new SAXBuilder();
						Document doc = builder.build(reader);
						Element root = doc.getRootElement();
						List<Element> reagentsList = XPath.selectNodes(root, "/Reagents/Reagent/Fields");
						if (reagentsList.size() > 0) {
							Element fieldsElement = reagentsList.get(0);
							if (fieldsElement != null) {
								updateBatch(batch, rh.buildReagentBatchList(fieldsElement), overWriteFlag);
								result = true;
							}
						}
					} catch (Exception e) {
						CeNErrorHandler.getInstance().logExceptionMsg(e);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Dissect the id to determine if it is a CeN batch number or a conversational batch number or a compound number or something
	 * else entirely. Performs a generic search on the resulting number and returns all relevant results.
	 * 
	 * @param batch -
	 *            the batch to fill.
	 * @param value -
	 *            the number on which to query.
	 * @return boolean true if all goes as planned, false if there is a problem
	 */
	public List<BatchModel> getBatchesFromID(BatchModel batchTemplate, String id, boolean overWriteFlag) {
		log.debug("getBatchesFromID id = " + id);
		ArrayList<BatchModel> result = new ArrayList<BatchModel>();
		if (StringUtils.isNotBlank(id)) {
			if (!NotebookPageUtil.isValidCeNBatchNumber(id)) {
				List<Compound> xmlReturn = null;
				try {
					xmlReturn = cmpdMgmtServiceDelegate.getCompoundInfoByCompoundNo(id);
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
				}
				// fill in compound information
				if (xmlReturn != null) {
					List<BatchModel> searchReturn = processSearchReturn(xmlReturn);
					if (searchReturn.size() > 0) {
						for (Iterator<BatchModel> i = searchReturn.iterator(); i.hasNext();) {
							BatchModel tmpBatch = batchTemplate.deepClone();
							updateBatch(tmpBatch, i.next(), overWriteFlag);
							result.add(tmpBatch);
						}
					} else {
						ceh.logMsg(null, "Failed to process: " + id, "Batch ID Lookup Failed");
					}
				} else {
					// no return - so lookup via ReagentMgmtService
					ReagentsHandler rh = new ReagentsHandler();
					String searchXML = "<ReagentsLookupParams><TextDatabases DoTextSearch=\"True\">"
							+ "<Database Name=\"CPI\"><SearchFields><Field ColumnName=\"CHEMICAL.CH_MDLNUM\" "
							+ "Criteria=\"Equals\" Value=\"" + id + "\"/></SearchFields></Database>"
							+ "</TextDatabases><StructureDatabases DoStructureSearch=\"False\"/>"
							+ "<Iterating LastPosition=\"-1\" ChunkSize=\"50\"/></ReagentsLookupParams>";
					byte[] bytes = rh.doReagentSearch(searchXML);
					if (bytes != null) {
						String reagents = new String(bytes);
						// get the first element in the returned xml
						try {
							StringReader reader = new StringReader(reagents);
							SAXBuilder builder = new SAXBuilder();
							Document doc = builder.build(reader);
							Element root = doc.getRootElement();
							List<Element> reagentsList = XPath.selectNodes(root, "/Reagents/Reagent/Fields");
							for (Iterator<Element> i = reagentsList.iterator(); i.hasNext();) {
								Element fieldsElement = i.next();
								BatchModel rb = batchTemplate.deepClone();
								updateBatch(rb, rh.buildReagentBatchList(fieldsElement), overWriteFlag);
								result.add(rb);
							}
						} catch (Exception e) {
							CeNErrorHandler.getInstance().logExceptionMsg(e);
						}
					}
				}
			} else {
				BatchModel rb = batchTemplate.deepClone();
				// TODO:
				/*
				 * getCompoundInfoFromBatchNumber calls getCompoundInfoFromValue if it exists in the registered db. We don't pick up
				 * all the original batch info this way. Make sure to use only the number and conv. batch number from a css
				 * return and to get the rest of the data from cen. Perhaps I should take the compound from css too as it would be
				 * the official compound.??? Perhaps not as all info should have been checked prior to registration. The only time
				 * surplanting the data would be beneficial is if curation happened that didn't make it to cen.
				 */
				if (getCompoundInfoFromBatchNumber(rb, id, false, overWriteFlag))
					result.add(rb);
			}
		}
		return result;
	}
	
	/**
	 * Use overWriteFlag only if the whole compound can be replaced in the target batch. Updates target compound and batch info with
	 * the info from source Sets salt information, conversationalBatchNumber, and batchNumber if available.
	 * 
	 */
	public void updateBatch(BatchModel target, BatchModel source, boolean overWriteFlag) {
		// Since we don't want to lose what might have been entered we need to do most of the copy ourselves.
		if (target != null && source != null) {
			// update batch level data
			if (overWriteFlag) // && source.getBatchNumberAsString().length() > 1)
				target.setBatchNumber(source.getBatchNumber());
			if (overWriteFlag) // && source.getConversationalBatchNumber().length() > 1)
				target.setConversationalBatchNumber(source.getConversationalBatchNumber());
			if (overWriteFlag) // && (target.getParentBatchNumber().length() == 0) && source.getParentBatchNumber().length() > 0)
				target.setParentBatchNumber(source.getParentBatchNumber());
			if (overWriteFlag) { // && SaltForm.isParentCode(target.getSaltForm().getCode())) {
				target.setSaltForm(source.getSaltForm());
				target.setSaltEquivs(source.getSaltEquivs());
				target.setSaltEquivsSet(source.getSaltEquivsSet());
			}
			if (overWriteFlag) // || (target.getMolecularWeightAmount().isValueDefault() && target.getMolecularWeightAmount().isCalculated()))
				target.setMolecularWeightAmount(source.getMolecularWeightAmount());
			// Update Molecular Formula
			if (overWriteFlag) // || (target.getMolecularFormula() == null && source.getMolecularFormula() != null && source.getMolecularFormula().length() > 0))
				target.setMolecularFormula(source.getMolecularFormula());

			// Manage compound data
			if (overWriteFlag) {
				target.setCompound(source.getCompound());
				target.setHazardComments(source.getHazardComments());
			} else {
				ParentCompoundModel targetCompound = target.getCompound();
				ParentCompoundModel searchReturnCompound = source.getCompound();
				if (targetCompound.getRegNumber().length() == 0)
					targetCompound.setRegNumber(searchReturnCompound.getRegNumber());
				if (targetCompound.getChemicalName().length() == 0 && searchReturnCompound.getChemicalName().length() > 0)
					targetCompound.setChemicalName(searchReturnCompound.getChemicalName());
				targetCompound.setNativeSketch(searchReturnCompound.getNativeSketch());
				targetCompound.setViewSketch(searchReturnCompound.getViewSketch());
//				targetCompound.setSearchSketch(searchReturnCompound.getSearchSketch());
				targetCompound.setNativeSketchFormat(searchReturnCompound.getNativeSketchFormat());
				if (targetCompound.getMolFormula() == null && searchReturnCompound.getMolFormula() != null)
					targetCompound.setMolFormula(searchReturnCompound.getMolFormula());
				if (targetCompound.getMolWgt() == 0.0 && searchReturnCompound.getMolWgt() > 0.0)
					targetCompound.setMolWgt(searchReturnCompound.getMolWgt());
				targetCompound.setCASNumber(searchReturnCompound.getCASNumber());
			}
			// Check for new hazard comments if there aren't any currently associated
			if (StringUtils.isBlank(target.getHazardComments())) {
				doUpdateHazards(target);
			}
		}
	}

	/**
	 * Use this one when the batch exists and needs to be refreshed on each load Update: Batch: ParentBatchNumber,
	 * ConversationalBatchNumber, MolWgt at batch level (user settable), MolFormula at batch level (user settable), SaltForm and
	 * Equivs Compound: All sketch types, MolWgt, ExactMass, MolFormula, ChemicalName.
	 */
	public static void updateCompound(BatchModel target, BatchModel source, boolean overWriteFlag) {
		// Since we don't want to lose what might have been entered we need to
		// do most of the copy ourselves.
		if (target != null && source != null) {
			if (overWriteFlag) {
				target.setCompound(source.getCompound());
			} else {
				ParentCompoundModel targetCompound = target.getCompound();
				ParentCompoundModel searchReturnCompound = source.getCompound();
				if (StringUtils.isBlank(targetCompound.getRegNumber())) {
					targetCompound.setRegNumber(searchReturnCompound.getRegNumber());
				}
				if (StringUtils.isBlank(targetCompound.getChemicalName()) && StringUtils.isNotBlank(searchReturnCompound.getChemicalName())) {
					targetCompound.setChemicalName(searchReturnCompound.getChemicalName());
				}
				targetCompound.setNativeSketch(searchReturnCompound.getNativeSketch());
				targetCompound.setViewSketch(searchReturnCompound.getViewSketch());
//				targetCompound.setSearchSketch(searchReturnCompound.getSearchSketch());
				targetCompound.setNativeSketchFormat(searchReturnCompound.getNativeSketchFormat());
				targetCompound.setMolFormula(searchReturnCompound.getMolFormula());
				targetCompound.setMolWgt(searchReturnCompound.getMolWgt());
				targetCompound.setExactMass(searchReturnCompound.getExactMass());
				targetCompound.setCASNumber(searchReturnCompound.getCASNumber());
				targetCompound.setHazardComments(searchReturnCompound.getHazardComments());
				targetCompound.setStereoisomerCode(searchReturnCompound.getStereoisomerCode());
			}
		}
	}

	/**
	 * Uses given number after validation of format to query CeN databases for the information. Single reference is given, and only
	 * what is returned by the CompoundMgmtServiceDelegate.getCompoundNoFromBatch() is used - currently assumes one return.
	 * 
	 * @param batch -
	 *            batch to fill,
	 * @param newBN -
	 *            number to search must be a valid CeN batch number.
	 * @return boolean true if all goes well, false if there was a problem. False will be returned when the CompoundMgmtService does
	 *         not return a valid xml document, or returns no document.
	 */
	public boolean getCompoundInfoFromBatchNumber(BatchModel batch, String newBN, boolean displayMsgFlag, boolean overWriteFlag) {
		boolean result = false;
		if (StringUtils.isNotBlank(newBN)) {
			try {
				// This line might get rid of trailing zeros.
				BatchNumber bn = new BatchNumber();
				bn.setBatchNumberWithoutLotNumberPadding(newBN.trim());
				batch.setBatchNumber(bn);
				// Get local information over registered information as it is
				// more complete.
				NotebookUser user = MasterController.getUser();
				StorageDelegate storageDelegate = ServiceController.getStorageDelegate(user.getSessionIdentifier());
				ProductBatchModel resultProdModel = storageDelegate.getProductBatchForBatchNumber(newBN, MasterController.getUser().getSessionIdentifier());
				if (resultProdModel != null ) {
					// load the retrieved data to a new batch
					String name = batch.getCompound().getChemicalName();
					// copy details to the current batch but keep the
					// Structure Key
					String strKey = batch.getCompound().getKey();
					updateBatch(batch, resultProdModel, overWriteFlag, strKey);
					batch.getCompound().setCreatedByNotebook(false);
					if (StringUtils.isBlank(batch.getCompound().getChemicalName())) {
						batch.getCompound().setChemicalName(name);
					}
					result = true;
				} else {
					// TODO: expand compound mgmt service to Check CompoundManagement for a
					// registered number that meets the criteria
					if (displayMsgFlag) // batch number not found in CeN
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
						                              "Notebook batch number does not exist");
				}
				// Has the batch been registered?
				if (StringUtils.isBlank(batch.getCompound().getRegNumber()) && StringUtils.isBlank(batch.getConversationalBatchNumber())) {					
					ProductBatchModel batchModel = new StorageDelegate().getProductBatchForBatchNumber(bn.getBatchNumber(), MasterController.getUser().getSessionIdentifier());
					if (batchModel != null) {
						batch.setLoadedFromDB(true);
						updateBatch(batch, batchModel, false);
						result = true;
					}
				}
			} catch (StorageInitException e) {
				ceh.logExceptionMsg(MasterController.getGUIComponent(), "Error initiating contact with CeN Database", e);
			} catch (StorageException e) {
				ceh.logExceptionMsg(MasterController.getGUIComponent(), "Error accessing CeN Database", e);
			} catch (InvalidBatchNumberException e) {
				ceh.logExceptionMsg(MasterController.getGUIComponent(), "Could not create a valid BatchNumber from: " + newBN, e);
			} catch (Exception e) {
				ceh.logExceptionMsg(MasterController.getGUIComponent(), "Error loading in batch data", e);
			}
		}
		return result;
	}

	/**
	 * Uses given number after validation of format to query CeN databases for the information. Single reference is given, and only
	 * what is returned by the CompoundMgmtServiceDelegate.getCompoundNoFromBatch() is used - currently assumes one return.
	 * 
	 * @param batch -
	 *            batch to fill,
	 * @param newBN -
	 *            number to search must be a valid CeN batch number.
	 * @return boolean true if all goes well, false if there was a problem. False will be returned when the CompoundMgmtService does
	 *         not return a valid xml document, or returns no document.
	 */
	public boolean getCompoundInfoFromCasNumber(BatchModel batch, String newCN, boolean displayMsgFlag, boolean overWriteFlag) {
		boolean result = false;
		if (StringUtils.isNotBlank(newCN)) {
			List<Compound> xmlReturn = null;
			try {
				xmlReturn = cmpdMgmtServiceDelegate.getCompoundInfoByCasNo(newCN);
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
			// fill in compound information
			if (xmlReturn != null) {
				List<BatchModel> searchReturn = processSearchReturn(xmlReturn);
				if (searchReturn.size() > 1 && (!batch.isLoadedFromDB() || overWriteFlag)) {
					if (displayMsgFlag) {
						// Get the user to select one or more responses.
						CompoundIDLookupDialog inst = new CompoundIDLookupDialog(MasterController.getGUIComponent(),
						                                                         newCN,
						                                                         searchReturn);
						inst.setVisible(true);
						if (inst.getSelectedBatch() != null) {
							BatchModel returnedBatch = inst.getSelectedBatch();
							// we have something to update with...
							updateBatch(batch, returnedBatch, overWriteFlag);
							result = true;
						}
					} else {
						// If we can find a compound to load, do it. Otherwise, fail.
						BatchModel returnBatch = null;
						if (StringUtils.isNotBlank(batch.getCompound().getRegNumber()))
							for (Iterator<BatchModel> it = searchReturn.iterator(); it.hasNext() && returnBatch == null;) {
								BatchModel tBatch = it.next();
								if (StringUtils.isNotBlank(tBatch.getCompound().getRegNumber()) && batch.getCompound().getRegNumber().equals(tBatch.getCompound().getRegNumber())) {
									returnBatch = tBatch;
									updateBatch(batch, returnBatch, overWriteFlag);
									result = true;
								}
							}
					}
				} else if (searchReturn.size() == 1 && (!batch.isLoadedFromDB() || overWriteFlag)) {
					// update the batch passed to us with the information we retrieved.
					updateBatch(batch, searchReturn.get(0), overWriteFlag);
					result = true;
				} else if (searchReturn.size() > 0 && (batch.isLoadedFromDB() || overWriteFlag)) {
					// Exists in DB. Do not overwrite data with CSS info
					// unless field is blank.
					BatchModel returnBatch = null;
					if (batch.getBatchNumber() != null)
						for (Iterator<BatchModel> it = searchReturn.iterator(); it.hasNext() && returnBatch == null;) {
							BatchModel tBatch = it.next();
							if (batch.getBatchNumberAsString().equals(tBatch.getBatchNumberAsString())) {
								returnBatch = tBatch;
								if (overWriteFlag) {
									batch.deepCopy(returnBatch);
								} else {
									// We don't want to overwrite anything
									// in the compound that was put there by
									// the user
									// like comments, names, etc. We only
									// want the info from the loadSketch()
									// action already run.
									updateBatch(batch, returnBatch, false);
								}
								result = true;
							}
						}
				} else {
					ceh.logMsg(null, "Failed to process: " + newCN, "Compound Lookup Failed");
				}
			} else {
				// no return - so lookup via ReagentMgmtService
				// TODO: Remove when CompoundMgmtService is updated.
				ReagentsHandler rh = new ReagentsHandler();
				String searchXML = "<ReagentsLookupParams><TextDatabases DoTextSearch=\"True\">"
						+ "<Database Name=\"CPI\"><SearchFields><Field ColumnName=\"CHEMICAL.CH_MDLNUM\" "
						+ "Criteria=\"Equals\" Value=\"" + newCN + "\"/></SearchFields></Database>"
						+ "</TextDatabases><StructureDatabases DoStructureSearch=\"False\"/>"
						+ "<Iterating LastPosition=\"-1\" ChunkSize=\"50\"/></ReagentsLookupParams>";
				byte[] bytes = rh.doReagentSearch(searchXML);
				if (bytes != null) {
					String reagents = new String(bytes);
					// get the first element in the returned xml
					try {
						StringReader reader = new StringReader(reagents);
						SAXBuilder builder = new SAXBuilder();
						Document doc = builder.build(reader);
						Element root = doc.getRootElement();
						List<Element> reagentsList = XPath.selectNodes(root, "/Reagents/Reagent/Fields");
						if (reagentsList.size() > 0) {
							Element fieldsElement = reagentsList.get(0);
							if (fieldsElement != null) {
								updateBatch(batch, rh.buildReagentBatchList(fieldsElement), overWriteFlag);
								result = true;
							}
						}
					} catch (Exception e) {
						CeNErrorHandler.getInstance().logExceptionMsg(e);
					}
				}
			}
		}
		return result;
	}

	// 
	// Helper methods
	//
	/**
	 * Loops through the different TextInfo elements in the xml return and creates a batch representation of each. If there was a
	 * structure returned, that will be shared among all returned batches, but will be as separate objects.
	 * 
	 * @param batches -
	 *            empty list to be filled with batch objects.
	 * @param xml -
	 *            root element of xml document returned from ComopundMgmtService
	 * @return List of batch objects for which we have a valuable response.
	 */
	public static List<BatchModel> processSearchReturn(List<Compound> compounds) {
		// TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// TODO This method is needs to double check!
		// TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		
		log.debug("processSearchReturn");
		List<BatchModel> result = new ArrayList<BatchModel>();
		
		for (Compound compound : compounds) {
			if (compound == null)
				continue;
			
			BatchModel batchModel = new BatchModel();
			ParentCompoundModel parentCompoundModel = new ParentCompoundModel();
						
			parentCompoundModel.setRegNumber(compound.getCompoundNo());
			parentCompoundModel.setStereoisomerCode(compound.getStereoisomerCode());
			parentCompoundModel.setCASNumber(compound.getCasNo());
			parentCompoundModel.setChemicalName(compound.getChemicalName());
			parentCompoundModel.setComments(compound.getComment());
			parentCompoundModel.setHazardComments(compound.getHazardComment());
			parentCompoundModel.setStereoisomerCode(compound.getStereoisomerCode());
			
			batchModel.setConversationalBatchNumber(compound.getConversationalBatchNo());
			
			try {
				StructureLoadAndConversionUtil.loadSketch(compound.getStructure().getBytes(), 0, false, parentCompoundModel);
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(e);
			}
						
			batchModel.setCompound(parentCompoundModel);	
			
			batchModel.setBatchType(BatchType.REAGENT);		
			batchModel.setComments(compound.getComment());
			
			try {
				batchModel.setBatchNumber(compound.getBatchNo());
			} catch (InvalidBatchNumberException e) {
				log.error("Invalid batch number: " + compound.getBatchNo(), e);
			}
			
			String saltCode = compound.getSaltCode();
			if (SaltFormModel.isParentCode(saltCode) == false) {
				try {
					String descriptionCode = SaltCodeCache.getCache().getDescriptionGivenCode(saltCode);
					String molformulaCode = SaltCodeCache.getCache().getMolFormulaGivenCode(saltCode);
					double molwtCode = SaltCodeCache.getCache().getMolWtGivenCode(saltCode);
					
					SaltFormModel saltFormModel = new SaltFormModel(saltCode, descriptionCode, molformulaCode, molwtCode);
					
					batchModel.setSaltForm(saltFormModel);
					batchModel.setSaltEquivs(compound.getSaltEquivs());
					batchModel.setSaltEquivsSet(true);
				} catch (CodeTableCacheException e) {
					CeNErrorHandler.getInstance().logExceptionMsg(e);
					batchModel.setSaltEquivsSet(false);
				}
			} else {
				batchModel.setSaltEquivsSet(false);
			}
			
			SignificantFigures sigs = new SignificantFigures(parentCompoundModel.getMolWgt());
			batchModel.getMolecularWeightAmount().setSigDigits(sigs.getNumberSignificantFigures());
			batchModel.getMolecularWeightAmount().setValue(parentCompoundModel.getMolWgt(), true);
			batchModel.setMolWgtCalculated(batchModel.getMolWgtCalculated());
			
			result.add(batchModel);
		}
		
		return result;
	}

	// Overloading this method for Cen1.2
	/**
	 * Use overWriteFlag only if the whole compound can be replaced in the target batch. Updates target compound and batch info with
	 * the info from source Sets salt information, conversationalBatchNumber, and batchNumber if available.
	 * 
	 */
	public void updateBatch(MonomerBatchModel target, BatchModel source, boolean overWriteFlag) {
		// Since we don't want to lose what might have been entered we need to
		// do most of the copy ourselves.
		if (target != null && source != null) {
			// update batch level data
			// target.setLoading(true);
			if (overWriteFlag) {
				target.setBatchNumber(source.getBatchNumber());
			    target.setBatchType(source.getBatchType());
				target.setConversationalBatchNumber(source.getConversationalBatchNumber());
				target.setParentBatchNumber(source.getParentBatchNumber());
				target.setSaltForm(source.getSaltForm());
				target.setSaltEquivs(source.getSaltEquivs());
				target.setMolecularWeightAmount(source.getMolecularWeightAmount());
				target.getMolecularWeightAmount().setSigDigits(-1);
				target.getMolecularWeightAmount().setFixedFigs(3);
				target.setMolecularFormula(source.getMolecularFormula());
				target.getCompound().deepCopy(source.getCompound());
				//TODO ??
				target.getCompound().setModelChanged(true);
				// DeepCopy already populates the hazard comments for the copied compound. 
				// Why are we performing the lookup again?
				String casNumber = target.getCompound().getCASNumber();
				if (StringUtils.isNotBlank(casNumber)) {
					String hazardComments = target.getHazardComments();
					log.debug("Retrieving hazard stmt for cas#:" + casNumber);
					String hazardStatement;
					try {
						hazardStatement = hazardInfoProvider.getHazardInfo(casNumber, "cas", "EN");
						if (hazardComments.indexOf(hazardStatement) == -1) {
							target.setHazardComments(hazardComments + hazardStatement);
						}
					} catch (HazardException e) {
						e.printStackTrace();
					}
				} else {
					target.setHazardComments(source.getHazardComments());
				}
			} else {
				updateButDoNotOverwriteCompound(target.getCompound(), source.getCompound());				
			}
			// Check for new hazard comments if there aren't any currently associated
			if (StringUtils.isBlank(target.getHazardComments())) {
				doUpdateHazards(target);
			}
		}
	}//end updateBatch
	
	/**
	 * Use overWriteFlag only if the whole compound can be replaced in the target batch. Updates target compound and batch info with
	 * the info from source Sets salt information, conversationalBatchNumber, and batchNumber if available.
	 * 
	 */
	public static void updateBatch(BatchModel target, ProductBatchModel source, boolean overWriteFlag, String compoundKey) {
		// Since we don't want to lose what might have been entered we need to
		// do most of the copy ourselves.
		if (target != null && source != null) {
			// update batch level data
			target.setLoadingFromDB(true);
			if (overWriteFlag) {  
				// this section was largely overwritten so perhaps we are indeed losing what was entered
				// it has been compacted to allow for easier reading - please look to history for previous
				// logic to each section if we get complaints about overriding what was written.
				target.setBatchNumber(source.getBatchNumber());
				target.setConversationalBatchNumber(source.getConversationalBatchNumber());
				target.setParentBatchNumber(source.getParentBatchNumber());
				target.setSaltForm(source.getSaltForm());
				target.setSaltEquivs(source.getSaltEquivs());
				target.setMolecularWeightAmount(source.getMolecularWeightAmount());
				// Update Molecular Formula
				target.setMolecularFormula(source.getMolecularFormula());
			}
			target.setLoadingFromDB(false);
			// Manage compound data
			if (overWriteFlag) {
				if(StringUtils.isNotBlank(compoundKey)) {
					ParentCompoundModel tmpCompound = new ParentCompoundModel(compoundKey);
					tmpCompound.deepCopy(source.getCompound());
					target.setCompound(tmpCompound);
				} else {
					target.setCompound(source.getCompound());
				}
				String casNumber = target.getCompound().getCASNumber();
				// If we have a CAS number load corresponding hazard info
				if (StringUtils.isNotBlank(casNumber)) {
					String hazardComments = target.getHazardComments();
					log.debug("Retrieving hazard stmt for cas#:" + casNumber);
					String hazardStatement;
					try {
						hazardStatement = hazardInfoProvider.getHazardInfo(casNumber, "cas", "EN");
						if (hazardComments.indexOf(hazardStatement) == -1) {
							target.setHazardComments(hazardComments + hazardStatement);
						}
					} catch (HazardException e) {
						e.printStackTrace();
					}
				} else {
					log.debug("No cas# for the batch" +target.getCompound().getRegNumber());
					target.setHazardComments(source.getHazardComments());
				}
			} else {
				ParentCompoundModel targetCompound = target.getCompound();
				targetCompound.setLoadingFromDB(true);
				ParentCompoundModel searchReturnCompound = source.getCompound();
				updateButDoNotOverwriteCompound(targetCompound, searchReturnCompound);				
				String casNumber = target.getCompound().getCASNumber();
				if (StringUtils.isNotBlank(casNumber)) {
					String hazardComments = target.getHazardComments();
					log.debug("Retrieving hazard stmt for cas#:" + casNumber);
					String hazardStatement;
					try {
						hazardStatement = hazardInfoProvider.getHazardInfo(casNumber, "cas", "EN");
						if (hazardComments.indexOf(hazardStatement) == -1) {
							target.setHazardComments(hazardComments + hazardStatement);
						}
					} catch (HazardException e) {
						e.printStackTrace();
					}
				} else {
					log.debug("No cas# for the batch" +targetCompound.getRegNumber());
				}
				// targetCompound.setExactMass(searchReturnCompound.getExactMass());
				// targetCompound.setStereoisomerCode(searchReturnCompound.getStereoisomerCode());
				targetCompound.setLoadingFromDB(false);
			}
		}
	}
	
	/**
	 * 
	 * Overwrites any existing hazard information.
	 * @param batch
	 */
	public void doUpdateHazards(BatchModel batch) {
		StringBuffer hazardComments = new StringBuffer();
		String hazardStatement = lookupHazardInfoForBatch(batch);
		
		if (StringUtils.isNotBlank(hazardStatement)) {
			if (hazardComments.indexOf(hazardStatement) == -1) {
				if (hazardComments.length() > 0) {
					hazardComments.append(" ");
				}
				hazardComments.append(hazardStatement);
				batch.setHazardComments(hazardComments.toString());
			}
		}
	}
	
	/**
 	 * Performs a search for hazard comments Via the HazardInspector for a batch.<br>
	 * Looks for hazard information based on the existence and lack of return based on the following<br> 
	 * values: CAS number, Compound Registration Number (compoundId) and the compundId minus any salt code.<br>
	 * Maximum attempts for lookup are 3.  Min. is zero.  Will ignore whitespace in any of the above ids.<br>

	 * @param batch
	 * @return String containing any found hazard information
	 */
	public String lookupHazardInfoForBatch(BatchModel batch) {
		String casNumber = batch.getCompound().getCASNumber();
		String regNumber = batch.getCompound().getRegNumber();
		String hazardStatement = null;
		try {
			// Check CAS number first then try the compound registration number
			if (StringUtils.isNotBlank(casNumber)) {
				hazardStatement = hazardInfoProvider.getHazardInfo(casNumber, "cas", "EN");
			} else if (StringUtils.isNotBlank(regNumber)) {
				hazardStatement = hazardInfoProvider.getHazardInfo(regNumber, "cas", "EN");
			}
		} catch (HazardException e) {
			e.printStackTrace();
		}
		// if hazardStatements is blank we want to see if we get a hazard from the reg number without a salt code
		if (StringUtils.isBlank(hazardStatement) && StringUtils.isNotBlank(regNumber)) {
			String num = null;
			try {
				num = UtilsDispatcher.getFormatter().removeSaltCode(regNumber);
			} catch (Exception e) {
				log.error("Failed to strip salt code from regNumber when looking up hazard. RegNumber was: " + regNumber, e);
			}
			if (StringUtils.isNotBlank(num) && StringUtils.equals(regNumber, num) == false) {
				try {
					hazardStatement = hazardInfoProvider.getHazardInfo(num, "disc", "EN");
				} catch (HazardException e) {
					e.printStackTrace();
				}
			}
		}
		return hazardStatement;
	}
	
	private static void updateButDoNotOverwriteCompound(ParentCompoundModel targetCompound, ParentCompoundModel searchReturnCompound) {
		if (StringUtils.isBlank(targetCompound.getRegNumber())) {
			targetCompound.setRegNumber(searchReturnCompound.getRegNumber());
		}
		if (StringUtils.isBlank(targetCompound.getChemicalName()) && StringUtils.isNotBlank(searchReturnCompound.getChemicalName())) {
			targetCompound.setChemicalName(searchReturnCompound.getChemicalName());
		}
		targetCompound.setNativeSketch(searchReturnCompound.getNativeSketch());
		targetCompound.setViewSketch(searchReturnCompound.getViewSketch());
		targetCompound.setNativeSketchFormat(searchReturnCompound.getNativeSketchFormat());
		if (StringUtils.isBlank(targetCompound.getMolFormula()) && StringUtils.isNotBlank(searchReturnCompound.getMolFormula())) {
			targetCompound.setMolFormula(searchReturnCompound.getMolFormula());
		}
		if (targetCompound.getMolWgt() == 0.0 && searchReturnCompound.getMolWgt() > 0.0) {
			targetCompound.setMolWgt(searchReturnCompound.getMolWgt());
		}
	}
}
