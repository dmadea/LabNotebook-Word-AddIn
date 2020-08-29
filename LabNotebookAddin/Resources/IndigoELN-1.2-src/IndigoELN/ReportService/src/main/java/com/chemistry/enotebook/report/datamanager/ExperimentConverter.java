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
package com.chemistry.enotebook.report.datamanager;

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.PlateWell;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.common.SignificantFigures;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;
import com.chemistry.enotebook.person.PersonServiceFactory;
import com.chemistry.enotebook.person.classes.IPerson;
import com.chemistry.enotebook.report.beans.experiment.*;
import com.chemistry.enotebook.report.beans.synthesis.*;
import com.chemistry.enotebook.report.utils.ImageMapper;
import com.chemistry.enotebook.report.utils.TextUtils;
import com.chemistry.enotebook.report.utils.UserNameCache;
import com.chemistry.enotebook.storage.ReactionPageInfo;
import com.chemistry.enotebook.util.Stopwatch;
import com.chemistry.enotebook.utils.CommonUtils;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class ExperimentConverter {

    private static final Log log = LogFactory.getLog(ExperimentConverter.class);
    private AnalyticalSummaryList alist = new AnalyticalSummaryList();

    private static int LOT_NUMBER_START_INDEX = 14;
    private PrintableExperiment printableExp;
    private Map<String, String> precursorMap;
    private Map<String, String> idsToNamesMap;
    private Map<String, String> ntIdsToNamesMap = new HashMap<String, String>();    
    private Map<String, ArrayList<DispensingDirections>> dispensingMap;
    private SynthesisPlateReport report;
    
    // for code table access
    private static final String SITE = "site";
    private static final String TA = "ta";
    private static final String PROJECT = "project";
    private static final String COMPOUND_STATE = "compoundState";
//	private static final String HANDLING = "handling";
//	private static final String HAZARDS = "hazards";
//	private static final String STORAGE = "storaage";
    private static final String PROTECTION = "protection";
//	private static final String SOLVENT = "solvent";
    private static final String RESIDUAL_SOLVENT = "residualSolvent";
    private static final String SOLUBILITY_SOLVENT = "solubilitySolvent";
    private static final String SOURCE = "source";
    private static final String SOURCE_DETAIL = "sourceDetail";
    private static final String STEREOISOMER = "stereoisomer";

    private static final String OPEN_STATUS = "OPEN";
    
    /**
     * Create the worklist consisting of dispensing (monomer) plates listing the destinations for the monomer batch in each well and
     * the synthesis plates showing the sources for each well's batch. This method creates the data sets used by BIRT to generate
     * the report.
     *
     * @param pageModel
     * @return
     */
    public SynthesisPlateReport extractSynthesisPlateReportFromPageModel(NotebookPageModel pageModel, Map<String, Object> imageKeys) {
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start("ExperimentConverter.extractSynthesisPlateReportFromPageModel");
        report = new SynthesisPlateReport();
        report.setExperimentNumber(pageModel.getNotebookRefAsString());
        idsToNamesMap = this.convertIdsToNames(pageModel);
        String[] author = new String[1];
        author[0] = pageModel.getUserName();
        String[] fullnames = UserNameCache.getUsersFullName(author);
        if (fullnames.length > 0)
            report.setOwner(fullnames[0] != null ? fullnames[0] : pageModel.getPageHeader().getUserName());
        report.setProcedure(getExperimentProcedure(pageModel, imageKeys));
        Map<String, ArrayList<DispensingDirections>> dispensingMap = this.getMonomerDispensingMap(pageModel);
        List<ReactionStepModel> reactionSteps = pageModel.getReactionSteps();
//		int startIndex = 0;
//		if (reactionSteps.size() > 1) {
//			startIndex = 1;
//		}
        // /////////////////////////////////////
        // For each reaction step
        // /////////////////////////////////////
        for (ReactionStepModel reactionStepModel : reactionSteps) {
            SynthesisReactionStep reactionStep = new SynthesisReactionStep();
            report.addStep(reactionStep);
            reactionStep.setStepNumber(reactionStepModel.getStepNumber());
            // //////////////////////////////
            // Get stoic information
            // //////////////////////////////
            BatchesList<MonomerBatchModel> stoicBatchesList = reactionStepModel.getStoicBatchesList();
            List<MonomerBatchModel> batchModels = stoicBatchesList.getBatchModels();
            for (MonomerBatchModel stoicBatch : batchModels) {
                ReagentSolvent rs = new ReagentSolvent();
                // ////////////////////////////////////////////////////////////////////////////////
                // Get the data for Reagent/Solvent column which is a combination of fields
                // ////////////////////////////////////////////////////////////////////////////////
                StringBuffer buff = new StringBuffer();
                List<String> namesList = new ArrayList<String>();
                String compoundId = stoicBatch.getCompoundId();
                if (compoundId != null && compoundId.length() > 0)
                    namesList.add(compoundId);
                String chemicalName = stoicBatch.getChemicalName();
                if (chemicalName != null && chemicalName.length() > 0)
                    namesList.add(chemicalName);
                String nbkBatchNumber = stoicBatch.getBatchNumberAsString();
                if (nbkBatchNumber != null && nbkBatchNumber.length() > 0)
                    namesList.add(nbkBatchNumber);
                String casNumber = stoicBatch.getCompound().getCASNumber();
                if (casNumber != null && casNumber.length() > 0)
                    namesList.add("CAS No: " + casNumber);
                for (String name : namesList) {
                    if (buff.length() > 0) {
                        buff.append("\n");
                    }
                    buff.append(name);
                }
                rs.setName(buff.toString());
                rs.setHazards(stoicBatch.getHazardComments());
                rs.setMolarity(this.formatAmount(stoicBatch.getMolarAmount()));
                rs.setMoles(this.formatAmount(stoicBatch.getMoleAmount()));
                rs.setSolvent(stoicBatch.getSolventsAdded() != null ? stoicBatch.getSolventsAdded() : "");
                rs.setStep(reactionStep.getStepName());
                // vb 12/18 this data is not in the stoic batch model. Is it someplace else?
                rs.setDeadVolume("");
                // Compute total volume (Total Vol = (Vol/Well x #products) + Dead Vol)
                int numProductsInThisStep = reactionStepModel.getAllProductBatchModelsInThisStep().size();
                double volumePerWell = stoicBatch.getVolumeAmount().GetValueInStdUnitsAsDouble();
                double totalVolume = numProductsInThisStep * volumePerWell; // NOTE that dead volume needs to be added when it
                // exists in model
                // Clone amount model to get the sig figs right
                AmountModel volumeModel = (AmountModel) stoicBatch.getVolumeAmount().deepClone();
                volumeModel.setValue(totalVolume);
                rs.setVolume(this.formatAmount(volumeModel));
                rs.setVolumeInWell(this.formatAmount(stoicBatch.getVolumeAmount()));
                // Compute total weight (Total Wt = Total Vol x Molarity x Mol Wt )
                double totalWeight = totalVolume * stoicBatch.getMolarAmount().GetValueInStdUnitsAsDouble()
                        * stoicBatch.getMolWgt();
                AmountModel weightAmount = (AmountModel) stoicBatch.getWeightAmount().deepClone();
                weightAmount.setValue(totalWeight);
                rs.setWeight(this.formatAmount(weightAmount));
                rs.setWeightInWell(this.formatAmount(stoicBatch.getWeightAmount()));
                reactionStep.addReagentOrSolvent(rs);
            }

            // ///////////////////////////////////////////////
            // For each monomer plate in the reaction
            // ///////////////////////////////////////////////
            List<MonomerPlate> monomerPlates = reactionStepModel.getMonomerPlates();
            for (MonomerPlate plate : monomerPlates) {
                // //////////////////////////////////////////
                // Get information to generate the rows
                // //////////////////////////////////////////
                int numRows = plate.getyPositions();
                int numCols = plate.getxPositions();
                PlateRow[] rows = new PlateRow[numRows]; // should be numRows
                for (int k = 0; k < rows.length; k++)
                    rows[k] = new PlateRow();
                int currentRow = 0;
                int currentColumn = 0;
                String rowId = "";
                DispensingPlate dispensingPlate = new DispensingPlate();
                DispensingPlateTable plateTable = new DispensingPlateTable();
                plateTable.setMonomerListName("");
                plateTable.setPlateKey(plate.getKey());
                dispensingPlate.setPlateTable(plateTable);
                reactionStep.addDispensingPlate(dispensingPlate);
                dispensingPlate.setKey(plate.getKey());
                dispensingPlate.setName(plate.getPlateBarCode());
                dispensingPlate.setStep(reactionStep.getStepName());
                PlateWell<?>[] wells = plate.getWells();
                for (int row = 0; row < plate.getContainer().getYPositions(); row++) {
                    for (int col = 0; col < plate.getContainer().getXPositions(); col++) {
                        DispensingPlateWell dispensingPlateWell = null;
                        int wellIndex = row * numCols + col;
                        if (wellIndex < wells.length) {
                            PlateWell<?> well = wells[wellIndex];
                            dispensingPlateWell = new DispensingPlateWell();
                            dispensingPlateWell.setPosition(well.getWellNumber());
                            if (rowId.length() == 0)
                                rowId = well.getWellNumber().substring(0, 1);
                            MonomerBatchModel batch = (MonomerBatchModel) well.getBatch();
                            if (batch != null) {
                                dispensingPlateWell.setCompoundId(batch.getCompoundId());
                                dispensingPlateWell.setRowId(rowId);
                                dispensingPlateWell.setPlateKey(plate.getKey());
                                dispensingPlateWell.setColumn("" + (currentColumn + 1));
                                // ///////////////////////////////
                                // Insert dispensing directions
                                // ///////////////////////////////
                                int numberOfProductWells = 0;
                                if (dispensingMap.containsKey(batch.getCompoundId())) {
                                    List<DispensingDirections> dispensingList = dispensingMap.get(batch.getCompoundId());
                                    for (DispensingDirections dd : dispensingList) {
                                        dispensingPlateWell.addDispensingDirections(dd);
                                        numberOfProductWells += 1;
                                    }
                                }
                                // Change this if these are not the values Connie wants-- vb 12/18
                                AmountModel deliveredWeight = batch.getDeliveredWeight();
                                double totalWeightForCalc = 0;
                                if (deliveredWeight != null && deliveredWeight.GetValueInStdUnitsAsDouble() > 0) {
                                    dispensingPlateWell.setTotalWeight(this.formatAmount(deliveredWeight));
                                    totalWeightForCalc = deliveredWeight.GetValueInStdUnitsAsDouble();
                                } else {
                                    dispensingPlateWell.setTotalWeight(this.formatAmount(batch.getTotalWeightNeeded()));
                                    totalWeightForCalc = batch.getTotalWeightNeeded().GetValueInStdUnitsAsDouble();
                                }
//								AmountModel totalWeight = batch.getTotalWeight();  // currently not used.  Not sure why.
                                AmountModel deliveredVolume = batch.getDeliveredVolume();
                                if (deliveredVolume != null && deliveredVolume.GetValueInStdUnitsAsDouble() > 0)
                                    dispensingPlateWell.setTotalVolume(this.formatAmount(deliveredVolume));
                                else { // compute it : total volume = total weight/molarity/molecular weight
                                    try {
                                        double molarity = batch.getMolarAmount().GetValueInStdUnitsAsDouble();
                                        double molecularWeight = batch.getMolecularWeightAmount().GetValueInStdUnitsAsDouble();
                                        double totalVolume = totalWeightForCalc / (molarity / molecularWeight);
                                        AmountModel volumeNeeded = (AmountModel) batch.getTotalVolume().deepClone();
                                        volumeNeeded.setValue(totalVolume);
                                        dispensingPlateWell.setTotalVolume(this.formatAmount(volumeNeeded));
                                    } catch (Exception e) {
                                        log.error("Error calculating volume (zero divide?)", e);
                                    }
                                }
                            }
                        } else {
                            dispensingPlateWell = new DispensingPlateWell();
                            dispensingPlateWell.setPlateKey(plate.getKey());
                            dispensingPlateWell.setRowId(rowId);
                            dispensingPlateWell.setColumn("" + (currentColumn + 1));
                        }
                        rows[currentRow].addWell(dispensingPlateWell);
                        if (++currentColumn == numCols) {
                            currentColumn = 0;
                            rows[currentRow].setPlateKey(dispensingPlate.getKey());
                            rows[currentRow].setRowId(rowId);
                            rowId = "";
                            dispensingPlate.addRow(rows[currentRow]);
                            plateTable.addRow(rows[currentRow]);
                            currentRow++;
                        }
                    }
                }
            }
            // ///////////////////////////////////////////////
            // For each product plate in the reaction
            // ///////////////////////////////////////////////
            List<ProductPlate> productPlates = reactionStepModel.getProductPlates();
            for (ProductPlate plate : productPlates) {
                // Get information to generate the rows
                int numRows = plate.getyPositions();
                int numCols = plate.getxPositions();
                PlateRow[] rows = new PlateRow[numRows];
                for (int k = 0; k < rows.length; k++)
                    rows[k] = new PlateRow();
                int currentRow = 0;
                int currentColumn = 0;
                String rowId = "";
                SynthesisPlate synthesisPlate = new SynthesisPlate();
                SynthesisPlateTable plateTable = new SynthesisPlateTable();
                plateTable.setPlateKey(plate.getKey());
                synthesisPlate.setPlateTable(plateTable);
                reactionStep.addSynthesisPlate(synthesisPlate);
                synthesisPlate.setKey(plate.getKey());
                synthesisPlate.setName(plate.getPlateNumber());
                synthesisPlate.setStep(reactionStep.getStepName());
                PlateWell<?>[] wells = plate.getWells();
                int numcols = plate.getContainer().getXPositions();
                for (int row = 0; row < plate.getContainer().getYPositions(); row++) {
                    for (int col = 0; col < plate.getContainer().getXPositions(); col++) {
                        SynthesisPlateWell synthesisPlateWell = new SynthesisPlateWell();
                        // for (int j = 0; j<wells.length; j++) {
                        int wellIndex = row * numcols + col;
                        if (wellIndex < wells.length) {
                            // SynthesisPlateWell synthesisPlateWell = new SynthesisPlateWell();
                            PlateWell<?> well = wells[wellIndex];
                            synthesisPlateWell.setPosition(well.getWellNumber());
                            if (rowId.length() == 0)
                                rowId = well.getWellNumber().substring(0, 1);
                            ProductBatchModel batch = (ProductBatchModel) well.getBatch();
                            if (batch != null) {
                                synthesisPlateWell.setMolecularWeight("Mol Wgt: "
                                        + batch.getMolecularWeightAmount().GetValueForDisplay());
                                synthesisPlateWell.setVcNumber(batch.getCompound().getVirtualCompoundId());
                                synthesisPlateWell.setBatchNumber(batch.getBatchNumberAsString());
                                synthesisPlateWell.setRowId(rowId);
                                synthesisPlateWell.setPlateKey(plate.getKey());
                                synthesisPlateWell.setColumn("" + (currentColumn + 1));
                                List<String> precursors = batch.getReactantBatchKeys();
                                for (Iterator<String> pit = precursors.iterator(); pit.hasNext();) {
                                    String precursorKey = (String) pit.next();
                                    BatchModel precursorBatch = (BatchModel) pageModel.getMonomerBatchModelMap().get(precursorKey);
                                    if (precursorBatch == null) {
                                        // throw exception
                                    } else {
                                        SynthesisPrecursor synthesisPrecursor = new SynthesisPrecursor();
                                        synthesisPrecursor.setBatchId(precursorBatch.getCompound().getRegNumber());
                                        synthesisPrecursor.setMoleAmount(precursorBatch.getStoicMoleAmount().GetValueForDisplay()
                                                + " " + precursorBatch.getStoicMoleAmount().getUnit().toString());
                                        AmountModel weightAmount = precursorBatch.getWeightAmount();
                                        if (weightAmount == null || weightAmount.GetValueForDisplay().equals("0"))
                                            synthesisPrecursor.setWeightAmount("");
                                        else
                                            synthesisPrecursor.setWeightAmount(weightAmount.GetValueForDisplay() + " "
                                                    + precursorBatch.getWeightAmount().getUnit().toString());
                                        AmountModel volumeAmount = precursorBatch.getVolumeAmount();
                                        if (volumeAmount == null || volumeAmount.GetValueForDisplay().equals("0"))
                                            synthesisPrecursor.setVolumeAmount("");
                                        else
                                            synthesisPrecursor.setVolumeAmount(volumeAmount.GetValueForDisplay());
                                        synthesisPlateWell.addPrecursor(synthesisPrecursor);
                                    }
                                }
                            }

                        } else {
                            synthesisPlateWell.setRowId(rowId);
                            synthesisPlateWell.setPlateKey(plate.getKey());
                            synthesisPlateWell.setColumn("" + (currentColumn + 1));
                        }
                        rows[currentRow].addWell(synthesisPlateWell);
                        if (++currentColumn == numCols) {
                            currentColumn = 0;
                            rows[currentRow].setPlateKey(synthesisPlate.getKey());
                            rows[currentRow].setRowId(rowId);
                            rowId = "";
                            synthesisPlate.addRow(rows[currentRow]);
                            plateTable.addRow(rows[currentRow]);
                            currentRow++;
                        }
                    }
                }
            }
            System.out.println();
        }
        stopwatch.stop();
        return report;
    }

    /**
     * Convert the NotebookPageModel into a printableExperiment that can be converted to XML.
     *
     * @param pageModel
     * @return
     */
    public PrintableExperiment convertPageModelToPrintableExperiment(NotebookPageModel pageModel, boolean includeMonomerWells, boolean includeProductBatches, Map<String, Object> imageKeys, String timeZone) {
    	Stopwatch stopwatch = new Stopwatch();
        stopwatch.start("ExperimentConverter.convertPageModelToPrintableExperiment");
        // Create map of product precursors to get the VC # in the case of monomers that are previous step products.
        idsToNamesMap = convertIdsToNames(pageModel);
        precursorMap = getProductPrecursorMap(pageModel);
        printableExp = new PrintableExperiment();
        printableExp.setHeader(getExperimentHeader(pageModel, idsToNamesMap));
        printableExp.setDetails(getExperimentDetails(pageModel, idsToNamesMap, timeZone));
        printableExp.setProcedure(getExperimentProcedure(pageModel, imageKeys));
        List<ReactionStepModel> reactionSteps = pageModel.getReactionSteps();
        List<ReactionStep> printableReactionSteps = new ArrayList<ReactionStep>();
        // ////////////////////////////////////////////////////////////////////////////
        // Set starting index differently for single step and multistep experiments
        // ///////////////////////////////////////////////////////////////////////////
        int startIndex = 0;
        if (reactionSteps.size() > 1) {
            startIndex = 1;
        }
		
        // /////////////////////////////////////////
        // Get summary data
        // /////////////////////////////////////////
        ReactionStepModel summaryStepModel = (ReactionStepModel) reactionSteps.get(0);
        ReactionSchemeModel summaryRxnSchemeModel = summaryStepModel.getRxnScheme();
        ReactionSummary reactionSummary = new ReactionSummary();
        
        String key = summaryRxnSchemeModel.getKey();        
        byte[] viewSketch = summaryRxnSchemeModel.getViewSketch();
                
        if (StringUtils.isNotBlank(key) && !ArrayUtils.isEmpty(viewSketch)) {
        	reactionSummary.setReactionImageUri("<![CDATA[" + key + ".jpg]]>");
			imageKeys.put(key, viewSketch);
			
	        printableExp.setReactionSummary(reactionSummary);	
		} else {
			reactionSummary.setReactionImageUri("<![CDATA[" + ImageMapper.EMPTY_IMAGE + ".jpg]]>");
			imageKeys.put(ImageMapper.EMPTY_IMAGE, ImageMapper.getEmptyImage());
		}
        
        for (int i = startIndex; i < reactionSteps.size(); i++) {
            log.debug(">>>>>>>>>>>>>>> Step " + i);
            ReactionStepModel reactionStepModel = (ReactionStepModel) reactionSteps.get(i);
            ReactionStep step = new ReactionStep();
            step.setStepNumber(i);
            
            key = reactionStepModel.getRxnScheme().getKey();
            viewSketch = reactionStepModel.getRxnScheme().getViewSketch();
            
            if (StringUtils.isNotBlank(key) && !ArrayUtils.isEmpty(viewSketch)) {
            	step.setReactionImageUri("<![CDATA[" + key + ".jpg]]>");
                imageKeys.put(key, viewSketch);
            } else {
            	step.setReactionImageUri("<![CDATA[" + ImageMapper.EMPTY_IMAGE + ".jpg]]>");
                imageKeys.put(ImageMapper.EMPTY_IMAGE, ImageMapper.getEmptyImage());
            }

            
            // ////////////////////////////////////////////////////////////
            // Convert stoic data into beans. Note there are two types
            // of stoic data, batch lists and batches.
            // ////////////////////////////////////////////////////////////
            if (!pageModel.isConceptionExperiment()) {
	            List<StoicModelInterface> stoicModelList = reactionStepModel.getStoicModelList();
	            // sort the list in stoic transaction order
	            Collections.sort(stoicModelList, new Comparator<StoicModelInterface>() {
					public int compare(StoicModelInterface o1, StoicModelInterface o2) {
						int o1TO = o1.getStoicTransactionOrder();
						int o2TO = o2.getStoicTransactionOrder();
						return Integer.valueOf(o1TO).compareTo(Integer.valueOf(o2TO));
					}
	            });
	            
	            List<StoichiometryData> stoicDataList = new ArrayList<StoichiometryData>();
	            for (Iterator<StoicModelInterface> it1 = stoicModelList.iterator(); it1.hasNext();) {
	                StoicModelInterface obj = it1.next();
	                // ///////////////////////////////////
	                // handle monomer batch lists
	                // ///////////////////////////////////
	                if (obj instanceof BatchesList<?>) {
	                    BatchesList<MonomerBatchModel> stoicModel = (BatchesList<MonomerBatchModel>) obj;
	                    List<MonomerBatchModel> batches = stoicModel.getBatchModels();
	                    if (batches.size() > 1) { // This is a list
	                        stoicDataList.add(getStoicData(step, stoicModel, imageKeys));
	                    } else if (batches.size() == 1) {
	                        stoicDataList.add(getStoicData(step, batches.get(0), imageKeys));
	                    }
	                    // //////////////////////////////////
	                    // handle reagent batches
	                    // /////////////////////////////////
	                } else if (obj instanceof MonomerBatchModel) {
	                    MonomerBatchModel batch = (MonomerBatchModel) obj;
	                    stoicDataList.add(getStoicData(step, batch, imageKeys));
	                } else {
	                    log.error("getStoicModelList returned unexpected type: " + obj.getClass().getName());
	                }
	            }
	            step.setStoichList(stoicDataList);
            }

            // ////////////////////////////////////
            // Create monomer batch list beans
            // ////////////////////////////////////
            // List monomerBatchLists = reactionStepModel.getMonomers();
            // for (Iterator it2 = monomerBatchLists.iterator(); it2.hasNext();) {
            // BatchesList monomerBatchList = (BatchesList) it2.next();
            // MonomerBatchList batchList = new MonomerBatchList();
            // batchList.setStepName(step.getStepName());
            // batchList.setListName(monomerBatchList.getStoicLabel());
            // batchList.setKey(monomerBatchList.getKey());
            // List batches = monomerBatchList.getBatchModels();
            // for (Iterator it3 = batches.iterator(); it3.hasNext();) {
            // MonomerBatchModel batchModel = (MonomerBatchModel) it3.next();
            // String compoundId = batchModel.getCompound().getRegNumber();
            // if (compoundId.indexOf("[") >= 0) { // This is a product batch from an intermediate step.
            // if (precursorMap.containsKey(compoundId)) {
            // String vcNumber = (String) precursorMap.get(compoundId);
            // if (vcNumber == null) vcNumber = "";
            // batchModel.setCompoundId(vcNumber);
            // }
            // }
            // batchList.addMonomerBatch(this.getMonomerBatch(step, batchList, batchModel));
            // }
            // step.addMonomerBatchList(batchList);
            // }
            // ////////////////////////////////////
            // Create monomer plate beans
            // ////////////////////////////////////
            if (includeMonomerWells) {
                List<MonomerPlate> monomerPlates = reactionStepModel.getMonomerPlates();
                List<MPlate> printableMonomerPlates = new ArrayList<MPlate>();
                for (int j = 0; j < monomerPlates.size(); j++) {
                    MonomerPlate mplate = (MonomerPlate) monomerPlates.get(j);
                    MPlate printableMonomerPlate = getMonomerPlate(step, mplate, imageKeys);
                    this.getMonomerPlateTable(printableMonomerPlate);
                    printableMonomerPlates.add(printableMonomerPlate);
                }
                step.getMonomerPlates().addAll(printableMonomerPlates);
            }
            // ////////////////////////////////////
            // Create product batch list beans
            // ////////////////////////////////////
            if (includeProductBatches) {
                List<ProductBatchModel> productBatchList = reactionStepModel.getAllProductBatchModelsInThisStep();
                List<PrintableProductBatch> productBatches = new ArrayList<PrintableProductBatch>();
                String site = pageModel.getSiteCode();
                try {
                    site = CodeTableCache.getCache().getSiteDescription(site);
                } catch (CodeTableCacheException e) {
                    log.error("Error loading site code for " + site, e);
                }

                StringBuilder reactants = new StringBuilder();
                for (BatchModel batch : reactionStepModel.getBatchesFromStoicBatchesList()) {
                    if (batch.getBatchType() != null && batch.getCompound() != null && batch.getBatchType().equals(BatchType.REACTANT)) {
                        String regNumer = batch.getCompound().getRegNumber();
						//TODO workaround for CENSTR
						if (StringUtils.isNotBlank(regNumer) && !regNumer.startsWith(CeNConstants.CENSTR_ID_PREFIX)) {
                            reactants.append(" ").append(regNumer);
                        }
                    }
                }

                for (ProductBatchModel batchModel : productBatchList) {
                    if (BatchType.ACTUAL_PRODUCT.equals(batchModel.getBatchType())) {
                    	PrintableProductBatch printableBatch = getProductBatch(pageModel, step, batchModel, imageKeys, timeZone);
                        printableBatch.setSite(site);
                        printableBatch.setReactants(reactants.toString());
                        productBatches.add(printableBatch);
                    }
                }
                step.setProductBatches(productBatches);
                // printableReactionSteps.add(step);
            }
            printableReactionSteps.add(step);
            // ///////////////////////////////////////////////
            // Add the analysisModels for this step
            // ///////////////////////////////////////////////
            for (ProductBatchModel batchModel : reactionStepModel.getAllProductBatchModelsInThisStep()) {
                if (batchModel.getAnalysisModelList() != null && batchModel.getAnalysisModelList().size() > 0) {
                    this.addAnalyticalResults(batchModel);
                }
            }
        } // end step
        printableExp.getReactionSteps().addAll(printableReactionSteps);

        // ///////////////////////////////////////////////
        // For each registered plate, add screening and
        // PurificationService results.
        // ///////////////////////////////////////////////
        List<ProductPlate> productPlates = pageModel.getAllProductPlates();
        for (ProductPlate plate : productPlates) {
            if (plate.getScreenPanelsSubmissionStatus().equalsIgnoreCase(CeNConstants.REGINFO_SUBMISION_PASS)
                    || plate.getPurificationSubmissionStatus().equalsIgnoreCase(CeNConstants.REGINFO_SUBMISION_PASS)) {
                RegisteredPlate rplate = new RegisteredPlate();
                rplate.setBarcode(plate.getPlateBarCode());
                rplate.setPlateKey(plate.getKey());
                rplate.setName(plate.getPlateNumber());
                if (plate.getScreenPanelsSubmissionStatus().equalsIgnoreCase(CeNConstants.REGINFO_SUBMISION_PASS))
                    rplate.setSubmittalSets(this.getScreenResultsForPlate(plate));
                if (plate.getPurificationSubmissionStatus().equalsIgnoreCase(CeNConstants.REGINFO_SUBMISION_PASS))
                    rplate.setPurificationServiceSubmissions(this.getPurificationServiceSubmissionsForPlate(plate));
                printableExp.addRegisteredPlate(rplate);
            }
        }
        // ////////////////////////////////////////////////
        // Add the analytical results summary
        // ////////////////////////////////////////////////
        printableExp.setAnalyticalSummaryList(alist);
		
        stopwatch.stop();
        return printableExp;
    }

    public void dispose() {
        if (this.alist != null)
            this.alist.getList().clear();
        if (this.printableExp != null)
            this.printableExp.dispose();
        if (this.precursorMap != null)
            this.precursorMap.clear();
        if (this.idsToNamesMap != null)
            this.idsToNamesMap.clear();
        if (this.dispensingMap != null)
            this.dispensingMap.clear();
        if (this.report != null)
            this.report.dispose();
    }

    private Map<String, String> convertIdsToNames(NotebookPageModel pageModel) {
        Map<String, String> idsToNamesMap = new HashMap<String, String>();
        List<String> ids = new ArrayList<String>();
        ids.add(pageModel.getUserName());
        ids.add(pageModel.getBatchOwner());
        ids.add(pageModel.getBatchCreator());
        if (pageModel.getDesignSubmitter() != null)
            ids.add(pageModel.getDesignSubmitter());
        String[] idsArray = (String[]) ids.toArray(new String[0]);
        String[] namesArray = idsArray; // //////////////////////
        namesArray = UserNameCache.getUsersFullName(idsArray);
        for (int i = 0; i < idsArray.length; i++) {
            idsToNamesMap.put(idsArray[i], namesArray[i] != null && namesArray[i].length() > 0 ? namesArray[i] : idsArray[i]);
        }
        return idsToNamesMap;
    }

    private ExperimentHeader getExperimentHeader(NotebookPageModel pageModel, Map idsToNamesMap) {
        ExperimentHeader header = new ExperimentHeader();
        NotebookPageHeaderModel headerModel = pageModel.getPageHeader();
        String[] author = new String[1];
        author[0] = headerModel.getUserName();
        String[] fullnames = UserNameCache.getUsersFullName(author);
        if (fullnames.length > 0)
            header.setAuthor(fullnames[0] != null ? fullnames[0] : headerModel.getUserName());
        header.setNotebookExperiment(pageModel.getNotebookRefAsString());
        String sitename = headerModel.getSiteCode();
        sitename = this.getDescriptionForCode(sitename, SITE);
        header.setSite(sitename);
        String status = pageModel.getStatus();
        if (OPEN_STATUS.equalsIgnoreCase(status) || "REOPEN".equalsIgnoreCase(status) || "SUBMIT_REOPEN".equalsIgnoreCase(status))
            status = OPEN_STATUS;
        else
            status = "COMPLETE";
        header.setStatus(status);
        header.setSubject(headerModel.getSubject());
        return header;
    }

    private String getDescriptionForCode(String code, String table) {
        try {
            String desc = "";
            if (table.equals(SITE))
                desc = CodeTableCache.getCache().getSiteDescription(code);
            else if (table.equals(TA))
                desc = CodeTableCache.getCache().getTAsDescription(code);
            else if (table.equals(PROJECT))
                desc = CodeTableCache.getCache().getProjectsDescription(code);
            else if (table.equals(PROTECTION))
                desc = CodeTableCache.getCache().getProtectionDescription(code);
            else if (table.equals(RESIDUAL_SOLVENT))
                desc = CodeTableCache.getCache().getResidualSolventDescription(code);
            else if (table.equals(SOLUBILITY_SOLVENT))
                desc = CodeTableCache.getCache().getSolubilitySolventDescription(code);
            else if (table.equals(SOURCE))
                desc = CodeTableCache.getCache().getSourceDescription(code);
            else if (table.equals(SOURCE_DETAIL))
                desc = CodeTableCache.getCache().getSourceDetailDescription(code);
            else if (table.equals(STEREOISOMER))
                desc = CodeTableCache.getCache().getStereoisomerDescription(code);
            if (desc != null && desc.length() > 0)
                return desc;
            else
                return code;
        } catch (CodeTableCacheException e) {
            log.error("Code Table Cache lookup failed for: code: " + code + " and table: " + table, e);
            return code;
        }
    }

    /**
     * Get the fields from the notebook page header.
     *
     * @param pageModel
     * @return
     */
    private ExperimentDetails getExperimentDetails(NotebookPageModel pageModel, Map<String, String> idsToNamesMap, String timeZone) {
        ExperimentDetails expDetails = new ExperimentDetails();
        NotebookPageHeaderModel headerModel = pageModel.getPageHeader();
        expDetails.setBatchCreator(idsToNamesMap.containsKey(pageModel.getBatchCreator()) ? idsToNamesMap.get(
                pageModel.getBatchCreator()).toString() : pageModel.getBatchCreator());
        expDetails.setBatchOwner(idsToNamesMap.containsKey(pageModel.getBatchOwner()) ? idsToNamesMap
                .get(pageModel.getBatchOwner()).toString() : pageModel.getBatchOwner());
        expDetails.setContinuedFrom(headerModel.getContinuedFromRxn());
        expDetails.setContinuedTo(headerModel.getContinuedToRxn());
        expDetails.setCreationDate(TextUtils.getTimeForTimeZone(pageModel.getCreationDateAsTimestamp(), timeZone));
        expDetails.setProjectCode(headerModel.getProjectCode());
        expDetails.setTherapeuticArea(headerModel.getTaCode());
        expDetails.setProjectAlias(headerModel.getProjectAlias());
        if (headerModel.getPageType().equalsIgnoreCase("PARALLEL")) {
            // Code table translations required here!!!
            expDetails.setDesignSite(headerModel.getDesignSite());
            expDetails.setDesignSumbitter(idsToNamesMap.containsKey(pageModel.getDesignSubmitter()) ? idsToNamesMap.get(
                    pageModel.getDesignSubmitter()).toString() : pageModel.getDesignSubmitter());
            expDetails.setSpId(headerModel.getSpid());
            expDetails.setSeriesId(headerModel.getSeriesID());
            expDetails.setVrxId(headerModel.getVrxnId());
        } else if (headerModel.getPageType().equalsIgnoreCase("CONCEPTION")) {
            expDetails.setConceptKeywords(headerModel.getConceptionKeyWords());
            expDetails.setInventors(headerModel.getConceptorNames());
        }
        String projectCodeAndDesc = headerModel.getProjectCode();
        projectCodeAndDesc = this.getDescriptionForCode(projectCodeAndDesc, PROJECT);
        expDetails.setProjectCode(headerModel.getProjectCode());
        expDetails.setProtocolId(headerModel.getProtocolID());
        String taDesc = headerModel.getTaCode();
        taDesc = this.getDescriptionForCode(taDesc, TA);
        expDetails.setTherapeuticArea(taDesc);
        expDetails.setLiteratureReference(headerModel.getLiteratureRef());
        return expDetails;
    }

    private ExperimentProcedure getExperimentProcedure(NotebookPageModel pageModel, Map<String, Object> imageKeys) {
        ExperimentProcedure proc = new ExperimentProcedure();
        
        String modelProcText = convertImageKeysToPaths(pageModel, imageKeys);
        if (modelProcText != null)
            proc.setProcedureText(modelProcText);
        
        return proc;
    }

    /**
     * Returns Procedure Area text with Procedure Images paths from Rest API
     * @see CeNReportServlet.clearProcedureImageFiles()
     * @param pageModel
     * @return
     */
    private String convertImageKeysToPaths(NotebookPageModel pageModel, Map<String, Object> imageKeys) {
    	log.debug("convertImageKeysToPaths(NotebookPageModel) start");
    	
    	String result = pageModel.getProcedure();
		List<ProcedureImage> procedureImages = pageModel.getPageHeader().getProcedureImages();
		
		String tempDir = System.getProperty("java.io.tmpdir");

		for (ProcedureImage procedureImage : procedureImages) {
			String key = procedureImage.getKey();
			byte[] data = procedureImage.getImageData();
						
			String path = tempDir + File.separator + key + "." + procedureImage.getImageType();
						
			try {
				File f = new File(path);
				if (f.exists()) {
					f.delete();
					f.createNewFile();
				}

				FileOutputStream out = new FileOutputStream(f);
				out.write(data);
				out.flush();
				out.close();
				
				path = "file:///" + path.replace('\\', '/');
				result = result.replaceAll(key, path);
			} catch (Exception e) {
				log.error("Error writing procedure image file!", e);
			}
		}
		
		log.debug("convertImageKeysToPaths(NotebookPageModel) end");
		return result;
	}

 	/**
     * Convert the stoic batch data into a printable bean.
     *
     * @param step
     * @param batch
     * @return
     */
    private StoichiometryData getStoicData(ReactionStep step, BatchModel batch, Map<String, Object> imageKeys) {
        StoichiometryData stoicData = new StoichiometryData();
        stoicData.setIsList(false);
        stoicData.setReactionRole(batch.getStoicReactionRole());
       
        if (!BatchType.SOLVENT.equals(batch.getBatchType())) {
            stoicData.setEq(batch.getStoicRxnEquivsAmount().GetValueForDisplay());
        }
        
        stoicData.setHazards(batch.getStoicHazardsComments());
        stoicData.setLoadingFactor(this.getAmountAndUnit(batch.getStoicLoadingAmount()));
        stoicData.setMMoles(this.getAmountAndUnit(batch.getStoicMoleAmount()));
        stoicData.setMolecularWeight(this.getAmountAndUnit(batch.getStoicMolecularWeightAmount()));
        stoicData.setWeight(this.getAmountAndUnit(batch.getWeightAmount()));
        stoicData.setVolume(this.getAmountAndUnit(batch.getVolumeAmount()));
        stoicData.setMolarity(this.getAmountAndUnit(batch.getMolarAmount()));
        
        if (batch.getCompoundId().startsWith(CeNConstants.CENSTR_ID_PREFIX)) { 
        	stoicData.setName("");
        } else {
        	stoicData.setName(batch.getCompoundId());
        }
        
        stoicData.setStepName(step.getStepName());
        stoicData.setChemicalName(batch.getChemicalName());
        stoicData.setMolecularFormula(batch.getMolecularFormula());
        stoicData.setCasNumber(batch.getStoicBatchCASNumber());
        String purityText = getAmountAndUnit(batch.getStoicPurityAmount());
        
        if (StringUtils.isBlank(purityText)) {
            purityText = "100";
        }
        
        stoicData.setPurity(purityText);
        stoicData.setDensity(getAmountAndUnit(batch.getDensityAmount()));
        
		String key = batch.getCompoundId();
		
		if (StringUtils.isBlank(key))
			key = batch.getBatchNumberAsString();
		
		if (StringUtils.isBlank(key))
			key = batch.getStoicBatchCASNumber();
		
		if (StringUtils.isBlank(key))
			key = batch.getKey();
		
		byte[] viewSketch = batch.getCompound().getViewSketch();
		
		if (StringUtils.isNotBlank(key) && !ArrayUtils.isEmpty(viewSketch)) {
			stoicData.setImageUri("<![CDATA[" + key + ".jpg]]>");
			imageKeys.put(key, viewSketch);
		} else {
			stoicData.setImageUri("<![CDATA[" + ImageMapper.EMPTY_IMAGE + ".jpg]]>");
			imageKeys.put(ImageMapper.EMPTY_IMAGE, ImageMapper.getEmptyImage());
		}
        
        if (batch.getSaltForm() != null && !SaltFormModel.isParentCode(batch.getSaltForm().getCode())) {
            stoicData.setSaltCode(batch.getSaltForm().getCode() + " - " + batch.getSaltForm().getDescription());
        }
        
        stoicData.setSaltEq("" + batch.getSaltEquivs());
        stoicData.setComments(batch.getStoichComments());
        stoicData.setNotebookBatchNumber(batch.getBatchNumberAsString());
        
        return stoicData;
    }

    /**
     * Convert the stoic batch list data into a printable bean.
     *
     * @param step
     * @param stoicModel
     * @return
     */
    private StoichiometryData getStoicData(ReactionStep step, BatchesList<?> stoicModel, Map<String, Object> imageKeys) {
        StoichiometryData stoicData = new StoichiometryData();
        stoicData.setIsList(true);
        stoicData.setReactionRole(stoicModel.getBatchType().toString()); // ?????
        stoicData.setEq(stoicModel.getStoicRxnEquivsAmount().GetValueForDisplay());
        stoicData.setHazards(stoicModel.getStoicHazardsComments());
        stoicData.setLoadingFactor(stoicModel.getStoicLoadingAmount().GetValueForDisplay());
        stoicData.setMMoles(""); // stoicModel.getStoicMoleAmount().GetValueForDisplay());
        stoicData.setMolecularWeight(""); // stoicModel.getStoicMolecularWeightAmount().GetValueForDisplay());
        stoicData.setWeight(""); // stoicModel.getStoicWeightAmount().GetValueForDisplay());
        stoicData.setVolume(""); // stoicModel.getStoicVolumeAmount().GetValueForDisplay());
        stoicData.setName("Monomers List " + stoicModel.getPosition());
        
        stoicData.setImageUri("<![CDATA[" + ImageMapper.EMPTY_IMAGE + ".jpg]]>");
        imageKeys.put(ImageMapper.EMPTY_IMAGE, ImageMapper.getEmptyImage());
        
        stoicData.setStepName(step.getStepName());
        if (!stoicModel.getStoicBatchSaltForm().getDescription().equals("null"))
            stoicData.setSaltCode(stoicModel.getStoicBatchSaltForm().getCode() + " - " + stoicModel.getStoicBatchSaltForm().getDescription());
        if (stoicModel.getStoicBatchSaltEquivs() != null)
            stoicData.setSaltEq(stoicModel.getStoicBatchSaltEquivs().toString());
        return stoicData;
    }

    /**
     * Convert the MonomerPlate model into a printable bean.
     *
     * @param step
     * @param plateModel
     * @return
     */
    private MPlate getMonomerPlate(ReactionStep step, MonomerPlate plateModel, Map<String, Object> imageKeys) {
        MPlate mplate = new MPlate();
        mplate.setKey(plateModel.getKey());
        mplate.setName(plateModel.getPlateBarCode());
        mplate.setType(plateModel.getPlateType());
        mplate.setStepName(step.getStepName());
        PlateWell<?>[] wells = plateModel.getWells();
        List<MonomerPlateWell> printableWells = new ArrayList<MonomerPlateWell>();
        for (int k = 0; k < wells.length; k++) {
            printableWells.add(getMonomerPlateWell(mplate, wells[k], imageKeys));
        }
        mplate.setWells(printableWells);
        this.assignRowAndColumnToPlateWell(printableWells, 5);
        return mplate;
    }

    private MonomerPlateTable getMonomerPlateTable(MPlate monomerPlate) {
        MonomerPlateTable plateTable = new MonomerPlateTable();
        plateTable.setPlateKey(monomerPlate.getKey());
        monomerPlate.setPlateTable(plateTable);
        int numCols = 5; // >>> constant or parameter
        int numRows = (monomerPlate.getWells().size() / numCols);
        // Add an extra row in last row is incomplete.
        if (numRows == 0 || (monomerPlate.getWells().size() % numCols) > 0)
            numRows += 1;
        MonomerPlateRow[] rows = new MonomerPlateRow[numRows];
        for (int k = 0; k < rows.length; k++) {
            MonomerPlateRow row = new MonomerPlateRow();
            row.setRowId("" + (k + 1));
            rows[k] = row;
        }
        int currentRow = 0;
        int currentColumn = 0;
        for (Iterator<MonomerPlateWell> it = monomerPlate.getWells().iterator(); it.hasNext();) {
            MonomerPlateWell well = it.next();
            well.setColumn("" + (currentColumn + 1));
            rows[currentRow].addWell(well);
            currentColumn += 1;
            currentColumn = currentColumn % numCols;
            if (currentColumn == 0 || !(it.hasNext()))
                plateTable.addRow(rows[currentRow++]);
        }
        return plateTable;
    }

//    private PrintableMonomerBatch getMonomerBatch(ReactionStep step, MonomerBatchList batchList, MonomerBatchModel batchModel, Map<String, Object> imageKeys) {
//        PrintableMonomerBatch batch = new PrintableMonomerBatch();
//        batch.setListKey(batchList.getKey());
//        batch.setStepName(step.getStepName());
//        batch.setListName(batchList.getListName());
//        batch.setCompoundId(batchModel.getCompound().getRegNumber());
//        batch.setMMoles(this.formatAmount(batchModel.getMoleAmount()));
//        batch.setWeight(this.formatAmount(batchModel.getWeightAmount()));
//        // AmountModel molWeight = batchModel.getMolecularWeightAmount();
//        // molWeight.setSigDigits(6);
//        // batch.setMolecularWeight("" + molWeight.GetValueInStdUnitsAsDouble());
//        batch.setMolecularWeight(batchModel.getMolecularWeightAmount().GetValueForDisplay());
//        batch.setMolecularFormula(batchModel.getMolecularFormula());
//        
//        String key = batchModel.getCompoundId();
//        batch.setImageUri("<![CDATA[" + key + ".jpg]]>");
//        imageKeys.put(key, batchModel.getCompound().getViewSketch());
//        
//        return batch;
//    }

    private PrintableProductBatch getProductBatch(NotebookPageModel pageModel, ReactionStep step, ProductBatchModel batchModel, Map<String, Object> imageKeys, String timeZone ) {
        PrintableProductBatch batch = new PrintableProductBatch();
        // RegSubHandler regSubHandler = new RegSubHandler();
        batch.setStepName(step.getStepName());
        batch.setAmountMade(this.formatAmount(batchModel.getTotalWeight()));
        batch.setComments(batchModel.getComments());
        ParentCompoundModel parent = batchModel.getCompound();
        batch.setStructureComments(parent.getStructureComments());
        batch.setCompoundProtection(this.getDescriptionForCode(batchModel.getProtectionCode(), PROTECTION));
        batch.setCompoundState(this.getDescriptionForCode(batchModel.getCompoundState(), COMPOUND_STATE));

        final AmountModel molWeight = batchModel.getMolecularWeightAmount();
        molWeight.setSigDigits(-1);
        molWeight.setFixedFigs(3);
        batch.setMolWeight(this.formatAmount(molWeight));
        final AmountModel exactMass = batchModel.getExactMassAmount();
        exactMass.setFixedFigs(3);
        batch.setExactMass(this.formatAmount(exactMass));
        if (batchModel.getSaltForm() != null && !SaltFormModel.isParentCode(batchModel.getSaltForm().getCode())) {
            batch.setCompoundSaltCode(batchModel.getSaltForm().getCode() + " - " + batchModel.getSaltForm().getDescription());
            batch.setCompoundSaltEQ("" + batchModel.getSaltEquivs());
        }
        batch.setMolFormula(batchModel.getMolecularFormula());
        batch.setNotebookBatchNumber(batchModel.getBatchNumberAsString());
        batch.setTheoreticalWeight(this.formatAmount(batchModel.getTheoreticalWeightAmount()));
        batch.setPercentYield(batchModel.getTheoreticalYieldPercentAmount().GetValueForDisplay() + "%");
        batch.setMeltingPoint(batchModel.getMeltPointRange().toString());
        List<PurityModel> purityModels = batchModel.getAnalyticalPurityList();
        StringBuffer purityDesc = new StringBuffer("");
        for (PurityModel pm : purityModels) {
            if (purityDesc.length() > 0) {
                purityDesc.append("\n");
            }
            purityDesc.append(pm.getOperator() + " " + pm.getPurityValue().GetValueInStdUnitsAsDouble() + "% " + pm.getCode());
        }
        batch.setPurity(purityDesc.toString());
        // batch.setPurity(this.formatAmount(batchModel.getPurityAmount()));
        // buff.append(" purity " + next.getOperator() + " " + next.getPurityValue().GetValueInStdUnitsAsDouble() + "% " +
        // next.getComments());
        // batch.setStorage(batchModel.getStorageComments());

        BatchRegInfoModel regInfo = batchModel.getRegInfo();
        String compoundSource = regInfo.getCompoundSource() != null ? regInfo.getCompoundSource()
                : "";
        batch.setSource(getDescriptionForCode(compoundSource, SOURCE));

        String sourceDetail = regInfo.getCompoundSourceDetail() != null ? regInfo
                .getCompoundSourceDetail() : "";
        batch.setSourceDetail(getDescriptionForCode(sourceDetail, SOURCE_DETAIL));

        // if there is a conversational batch number we can get the structure through the api via
        // the conversationalBatchNumber
        // Otherwise we need to pull it from the local CeN database
        String convBatchNumber = regInfo.getConversationalBatchNumber();
        if (convBatchNumber != null && convBatchNumber.length() > 0) {
            batch.setConversationalBatchNumber(convBatchNumber);
            String key = convBatchNumber;
            batch.setBarcodeUri("<![CDATA[" + key + ".jpg]]>");
            imageKeys.put(key, ImageMapper.getBarcodeImage(key));
        } else {
            batch.setBarcodeUri("<![CDATA[" + ImageMapper.EMPTY_IMAGE + ".jpg]]>");
            imageKeys.put(ImageMapper.EMPTY_IMAGE, ImageMapper.getEmptyImage());
        }
        
		batch.setImageUri("");
		
		String key = parent.getKey();
		
		if (StringUtils.isNotBlank(key) && !ArrayUtils.isEmpty(parent.getViewSketch())) {
			batch.setImageUri("<![CDATA[" + key + ".jpg]]>");
			imageKeys.put(key, parent.getViewSketch());
		} else {
			batch.setImageUri("<![CDATA[" + ImageMapper.EMPTY_IMAGE + ".jpg]]>");
		    imageKeys.put(ImageMapper.EMPTY_IMAGE, ImageMapper.getEmptyImage());
		}
		
        // if (batchModel.getAnalysisModelList() != null && batchModel.getAnalysisModelList().size() > 0) {
        // this.addAnalyticalResults(batchModel);
        // }

        try {
            CodeTableCache codeCache = CodeTableCache.getCache();
            List<String> hazardCodes = regInfo.getCompoundRegistrationHazardCodes();
            StringBuffer hazards = new StringBuffer();
            for (String code : hazardCodes) {
                if (hazards.length() > 0) {
                    hazards.append(", ");
                }
                hazards.append(codeCache.getHazardDescription(code));
            }
            if (StringUtils.isNotBlank(regInfo.getHazardComments())) {
            	if (hazards.length() > 0) hazards.append(", ");
            	hazards.append(regInfo.getHazardComments());
            }
            if (batchModel != null && 
            	batchModel.getCompound() != null && 
            	StringUtils.isNotBlank(batchModel.getCompound().getHazardComments()))
            {
            	if (hazards.length() > 0) hazards.append(", ");
            	hazards.append(batchModel.getCompound().getHazardComments());
            }
//            hazards.append(", " + regInfo.getHazardComments() + ", " + batchModel.getCompound().getHazardComments());
            batch.setHazards((hazards.length() > 0) ? hazards.toString() : "-none-");
            
            List<String> handlingCodes = regInfo.getCompoundRegistrationHandlingCodes();
            StringBuffer handling = new StringBuffer();
            for (String code : handlingCodes) {
                if (handling.length() > 0) {
                    handling.append(", ");
                }
                handling.append(codeCache.getHandlingDescription(code));
            }
            if (StringUtils.isNotBlank(regInfo.getHandlingComments())) {
            	if (handling.length() > 0) handling.append(", ");
            	handling.append(regInfo.getHandlingComments());
            }
//            handling.append(", " + regInfo.getHandlingComments());
            batch.setHandling((handling.length() > 0) ? handling.toString() : "-none-");
            
            List<String> storageCodes = regInfo.getCompoundRegistrationStorageCodes();
            StringBuffer storage = new StringBuffer();
            for (String code : storageCodes) {
                if (storage.length() > 0) {
                    storage.append(", ");
                }
                storage.append(codeCache.getStorageDescription(code));
            }
            if (StringUtils.isNotBlank(regInfo.getStorageComments())) {
            	if (storage.length() > 0) storage.append(", ");
            	storage.append(regInfo.getStorageComments());
            }
//            storage.append(", " + regInfo.getStorageComments());
            batch.setStorage((storage.length() > 0) ? storage.toString() : "-none-");

            batch.setResidualSolvents(CommonUtils.getResidualSolventsList(batchModel));
            batch.setSolubilityInSolvents(CommonUtils.getSolubilitySolventList(batchModel));

            batch.setHitId(regInfo.getHitId());

            //Optimization - do not need these fields for Conception Experiment - and this is slow code  
            if (!pageModel.isConceptionExperiment()) {
            	batch.setOwner(getUserName(batchModel.getOwner()));
            	batch.setSynthesizedBy(getUserName(batchModel.getSynthesizedBy()));
            }
            
            batch.setRegistrationDate(TextUtils.getTimeForTimeZone(regInfo.getRegistrationDate(), timeZone));
            batch.setSupplier(batchModel.getVendorInfo().getCode());
            
            batch.setParentMW(SignificantFigures.format(Double.toString(parent.getMolWgt()), 6));
            batch.setParentMF(parent.getMolFormula());
            batch.setOriginalBatchModel(batchModel);
        } catch (CodeTableCacheException e) {
            log.error("Error loading codes", e);
        }

        return batch;
    }

    
    private String getUserName(String login) {
    	if (ntIdsToNamesMap.containsKey(login)) {
    		return ntIdsToNamesMap.get(login); 
    	} else {
	        try {
	        	if (StringUtils.isNotEmpty(login)) {
		            IPerson person = PersonServiceFactory.getService().userIDtoPerson(login);
		            if(person != null) {
		                String s = person.getLastName() + ", " + person.getFirstName();
		                ntIdsToNamesMap.put(login, s);
		                return s;
		            } else {
		            	return login;
		            }
	        	} else {
	        		return null;
	        	}
	        } catch (Exception e) {
	           log.error("Failed to load user name for " + login, e);
	        }
    	}
        return null;
    }

    // private PrintableProductBatch getProductBatch(ReactionStep step, ProductBatch batch) {
    // return null;
    // }

    /**
     * Convert the monomer PlateWell into a printable bean.
     *
     * @param mplate
     * @param well
     * @return
     */
    private MonomerPlateWell getMonomerPlateWell(MPlate mplate, PlateWell<?> well, Map<String, Object> imageKeys) {
        MonomerPlateWell mpwell = new MonomerPlateWell();
        mpwell.setPlateKey(mplate.getKey());
        mpwell.setCompoundId(well.getBatch().getCompoundId());
        mpwell.setMMoles(this.formatAmount(well.getBatch().getMoleAmount()));
        mpwell.setWeight(this.formatAmount(well.getBatch().getWeightAmount()));
        mpwell.setMolecularWeight(well.getBatch().getMolecularWeightAmount().GetValueForDisplay());
        mpwell.setMolecularFormula(well.getBatch().getMolecularFormula());
        mpwell.setPosition("" + well.getNumber());
        String key = well.getBatch().getCompoundId();
        mpwell.setImageUri("<![CDATA[" + key + ".jpg]]>");
        imageKeys.put(key, well.getBatch().getCompound().getViewSketch());
        return mpwell;

    }

    private void assignRowAndColumnToPlateWell(List<MonomerPlateWell> wells, int numcols) {
        int currentRow = 1;
        int currentCol = 1;
        for (MonomerPlateWell well : wells) {
            well.setRowid("" + currentRow);
            well.setColid("" + currentCol++);
            if (currentCol > numcols) {
                currentCol = 1;
                currentRow += 1;
            }
        }

    }

    private Map<String, ScreenSubmittalSet> getScreenResultsForPlate(ProductPlate plate) {
        Map<String, ScreenSubmittalSet> submittalSets = new HashMap<String, ScreenSubmittalSet>();
        PlateWell<?>[] wells = plate.getWells();
        for (int i = 0; i < wells.length; i++) {
            PlateWell<?> well = wells[i];
            if (well.getBatch() == null)
                continue;
            BatchRegInfoModel info = ((ProductBatchModel) well.getBatch()).getRegInfo();
            List<BatchSubmissionToScreenModel> compoundAggregationList = info.getNewSubmitToBiologistTestList();
            for (BatchSubmissionToScreenModel submission : compoundAggregationList) {
                String key = String.valueOf(submission.getCompoundAggregationScreenPanelKey());
                ScreenSubmittalSet sset;
                if (submittalSets.containsKey(key)) {
                    sset = submittalSets.get(key);
                } else {
                    sset = new ScreenSubmittalSet();
                    sset.setCompoundAggregationScreenPanelKey(key);
                    sset.setRecipient(submission.getScientistName());
                    sset.setScreenCode(submission.getScreenCode());
                    sset.setScreenName(submission.getScreenProtocolTitle());
                    sset.setSite(submission.getSiteCode());
                    submittalSets.put(key, sset);
                }
                sset.addBatch(this.getLotNumber(well.getBatch().getBatchNumberAsString()));
            }
        }
        return submittalSets;
    }

    private PurificationServiceSubmissions getPurificationServiceSubmissionsForPlate(ProductPlate plate) {
        PurificationServiceSubmissions submissions = new PurificationServiceSubmissions();
        PlateWell<?>[] wells = plate.getWells();
        for (int i = 0; i < wells.length; i++) {
            PlateWell<?> well = wells[i];
            if (well.getBatch() == null)
                continue;
            submissions.addSubmittedBatch(this.getLotNumber(well.getBatch().getBatchNumberAsString()));
        }
        return submissions;
    }
    
    private String formatAmount(AmountModel amount) {
        StringBuffer buff = new StringBuffer();
        // Add code here to distinguish between zero and null values in the AmountModel.
        buff.append(amount.GetValueForDisplay()).append(" ").append(amount.getUnit().getDisplayValue());
        return buff.toString();
    }

    /**
     * Create a map where the key is the compoundId (concatenated monomer precursors) and the value is the virtual compound number.
     *
     * @param pageModel
     * @return Map
     */
    private Map<String, String> getProductPrecursorMap(NotebookPageModel pageModel) {
        Map<String, String> precursorMap = new HashMap<String, String>();
        List<ReactionStepModel> reactionSteps = pageModel.getReactionSteps();
        int startIndex = 0;
        if (reactionSteps.size() > 1) {
            startIndex = 1;
        }
        // This code would only work for parallel experiments
        for (int i = startIndex; i < reactionSteps.size(); i++) {
            ReactionStepModel stepModel = reactionSteps.get(i);
            List<ProductBatchModel> productBatches = stepModel.getProductBatches();
            for (ProductBatchModel productBatch : productBatches) {
                List<String> reactantKeys = productBatch.getReactantBatchKeys();
                StringBuffer buff = new StringBuffer("[");
                for (String thingy : reactantKeys) {
                    if (StringUtils.isNotBlank(thingy)) {
                        MonomerBatchModel monomerBatch = pageModel.getMonomerBatchModelMap().get((thingy).trim());
                        if (monomerBatch != null && monomerBatch.getCompound() != null) {
                        	String compoundId = monomerBatch.getCompound().getRegNumber();
                        	//TODO workaround for CENSTR
    						if (StringUtils.isNotBlank(compoundId) && !compoundId.startsWith(CeNConstants.CENSTR_ID_PREFIX)) {
	                            if (buff.length() > 1)
	                                buff.append(":");
	                            buff.append(compoundId);
	                        }
	                    }
	                }
                }
                buff.append("]");
                log.debug(buff.toString());
                String vcNumber = productBatch.getCompound().getVirtualCompoundId();
                if (vcNumber == null)
                    vcNumber = "";
                precursorMap.put(buff.toString(), vcNumber);
            }
        }

        return precursorMap;
    }

    private Map<String, ArrayList<DispensingDirections>> getMonomerDispensingMap(NotebookPageModel pageModel) {
        Map<String, ArrayList<DispensingDirections>> dispensingMap = new HashMap<String, ArrayList<DispensingDirections>>();
        List<ReactionStepModel> reactionSteps = pageModel.getReactionSteps();
        int startIndex = 0;
        if (reactionSteps.size() > 1) {
            startIndex = 1;
        }
        for (int i = startIndex; i < reactionSteps.size(); i++) {
            ReactionStepModel stepModel = reactionSteps.get(i);
            List<ProductPlate> productPlates = stepModel.getProductPlates();
            for (ProductPlate productPlate : productPlates) {
                PlateWell<?>[] wells = productPlate.getWells();
                for (int j = 0; j < wells.length; j++) {
                    PlateWell<?> well = wells[j];
                    ProductBatchModel batch = (ProductBatchModel) well.getBatch();
                    if (batch == null)
                        continue;
                    List<String> reactantKeys = batch.getReactantBatchKeys();
                    for (String key : reactantKeys) {
                        MonomerBatchModel monomerBatch = pageModel.getMonomerBatchModelMap().get(key);
                        String compoundId = monomerBatch.getCompound().getRegNumber();
                        ArrayList<DispensingDirections> dispenseToList = null;
                        if (dispensingMap.containsKey(compoundId)) {
                            dispenseToList = dispensingMap.get(compoundId);
                        } else {
                            dispenseToList = new ArrayList<DispensingDirections>();
                        }
                        // Create the dispensing directions
                        DispensingDirections dd = new DispensingDirections();
                        dd.setPlateKey(productPlate.getKey());
                        dd.setPlateName(productPlate.getPlateNumber());
                        dd.setPlateWell(well.getNumber());
                        if (monomerBatch.getTotalWeight() == null || monomerBatch.getTotalWeight().GetValueForDisplay().equals("0"))
                            dd.setTotalWeight("");
                        else
                            dd.setTotalWeight(formatAmount(monomerBatch.getTotalWeight()));
                        if (monomerBatch.getTotalVolume() == null || monomerBatch.getTotalVolume().GetValueForDisplay().equals("0"))
                            dd.setTotalVolume("");
                        else
                            dd.setTotalVolume(formatAmount(monomerBatch.getTotalVolume()));
                        if (monomerBatch.getVolumeAmount() == null
                                || monomerBatch.getVolumeAmount().GetValueForDisplay().equals("0"))
                            dd.setVolumeAmount("");
                        else
                            dd.setVolumeAmount(formatAmount(monomerBatch.getVolumeAmount()));
                        if (monomerBatch.getWeightAmount() == null
                                || monomerBatch.getWeightAmount().GetValueForDisplay().equals("0"))
                            dd.setWeightAmount("");
                        else
                            dd.setWeightAmount(formatAmount(monomerBatch.getWeightAmount()));
                        dd.setMoleAmount(formatAmount(monomerBatch.getMoleAmount()));
                        dispenseToList.add(dd);
                        dispensingMap.put(compoundId, dispenseToList);
                    }
                }
            }
        }

        return dispensingMap;
    }

    private void addAnalyticalResults(ProductBatchModel batchModel) {
        AnalyticalSummary asummary = new AnalyticalSummary();
        List<AnalysisModel> analysisModels = batchModel.getAnalysisModelList();
        asummary.setNotebookBatchNumber(batchModel.getBatchNumberAsString());
        if (batchModel.getAnalyticalComment() != null && (!batchModel.getAnalyticalComment().equalsIgnoreCase("null"))
                && batchModel.getAnalyticalComment().length() > 0) {
            asummary.setComments(batchModel.getAnalyticalComment());
        }
        for (AnalysisModel amodel : analysisModels) {
            if (amodel.getInstrumentType() != null && amodel.getInstrumentType().length() > 0)
                asummary.addInstrumentType(amodel.getInstrumentType());
        }
        alist.add(asummary);
    }

    private String getAmountAndUnit(AmountModel amount) {
        StringBuffer buff = new StringBuffer();
        // Code to distinguish between user entered zeros and empty values.
        if (!(amount.isCalculated() && CeNNumberUtils.doubleEquals(amount.doubleValue(), 0.0, 0.00001) && CeNNumberUtils
                .doubleEquals(amount.getDefaultValue(), 0.0, 0.00001))) {
            // should set display value instead of string Version of Amount
            buff.append(amount.GetValueForDisplay()); // currentAmt.toString()
            buff.append(" ");
            buff.append(amount.getUnit().getDisplayValue());
        }
        return buff.toString();
    }

    private String getLotNumber(String batchId) {
        if (batchId.length() <= LOT_NUMBER_START_INDEX)
            return batchId;
        else
            return batchId.substring(LOT_NUMBER_START_INDEX);
    }
    
	public String convertNotebookContentsTableModelToPrintableXMLAndConstructImageUrlKeys(Map<String, Object> imageKeys, List<ReactionPageInfo> pagesList, String notebook, int startExperiment, int stopExperiment) {
		StringBuilder sb = new StringBuilder();
		sb.append("<printableTableOfContents>");
		sb.append(tableContentsHeaderToXml(notebook, startExperiment, stopExperiment));
		
		sb.append("<tableOfContentsDetails>");
		for(ReactionPageInfo page : pagesList) {
			sb.append(tableContentsPageToXml(page, imageKeys));
		}		
		sb.append("</tableOfContentsDetails>");		
		sb.append("</printableTableOfContents>");
		return sb.toString();
	}
	
	private String tableContentsHeaderToXml(String notebook, int startExperiment, int stopExperiment) {
		StringBuilder sb = new StringBuilder();
		sb.append("<tableHeader>");
		
		sb.append("<notebook>").append(notebook).append("</notebook>");
		sb.append("<startPageNumber>").append(startExperiment).append("</startPageNumber>");
		sb.append("<endPageNumber>").append(stopExperiment).append("</endPageNumber>");
		
		sb.append("</tableHeader>");
		return sb.toString();
	}
	
	private String tableContentsPageToXml(ReactionPageInfo page, Map<String, Object> imageKeys) {
		StringBuilder sb = new StringBuilder();
		sb.append("<pageDetails>");
		
		sb.append("<notebookBatchNumber>").append(page.getNoteBookExperiment()).append("</notebookBatchNumber>");
		sb.append("<description><![CDATA[").append(page.getPageInfo()).append("]]></description>");
		
		String uri = null;
		
		if (CommonUtils.isNotNull(page.getReactionSketch())) {
			uri = page.getReactionSchemeKey();
			imageKeys.put(uri, page.getReactionImage());
		} else {
			uri = ImageMapper.EMPTY_IMAGE;
			imageKeys.put(uri, ImageMapper.getEmptyImage());
		}

        sb.append("<imageUri><![CDATA[").append(uri).append(".jpg]]></imageUri>");		
		
		sb.append("</pageDetails>");
		return sb.toString();
	}

    // private byte[] getBarcodeImage(String stringToTranslate) {
    // byte[] image = null;
    // try {
    // JBarcodeBean bb = new JBarcodeBean();
    // bb.setCodeType(new Code128());
    // bb.setShowText(false);
    // bb.setNarrowestBarWidth(1);
    // bb.setCode(stringToTranslate);
    //
    // ByteArrayOutputStream bos = new ByteArrayOutputStream();
    // bb.gifEncode(bos);
    // image = bos.toByteArray();
    // bos.close();
    //
    // if (image != null) image = ConvertGIFtoJPG.convertImageInMemory(image);
    // } catch (Exception e) {
    // log.error(e);
    // }
    // return image;
    // }

    // private AnalyticalSummaryList getAnalyticalSummaryList(NotebookPageModel pageModel) {
    // AnalyticalSummaryList alist = new AnalyticalSummaryList();
    //
    // AnalysisCacheModel acache = pageModel.getAnalysisCache();
    // List analyticalList = acache.getAnalyticalList();
    // Map batchAnalyticalMap = new HashMap();
    // for (Iterator it = analyticalList.iterator(); it.hasNext();) {
    // AnalysisModel amodel = (AnalysisModel) it.next();
    // String batchNumber = amodel.getCenSampleRef();
    // // Do something here to get the analyticalComments
    // AnalyticalSummary asummary;
    // if (batchAnalyticalMap.containsKey(batchNumber)) {
    // asummary = (AnalyticalSummary) batchAnalyticalMap.get(batchNumber);
    // } else {
    // asummary = new AnalyticalSummary();
    // asummary.setNotebookBatchNumber(amodel.getCenSampleRef());
    // }
    // if (amodel.getComments() != null && amodel.getComments().length() > 0)
    // asummary.setComments(amodel.getComments());
    // if (amodel.getInstrumentType() != null && amodel.getInstrumentType().length() > 0)
    // asummary.addInstrumentType(amodel.getInstrumentType());
    // batchAnalyticalMap.put(batchNumber, asummary);
    // }
    // for (Iterator it = batchAnalyticalMap.keySet().iterator(); it.hasNext();) {
	// alist.add((AnalyticalSummary) batchAnalyticalMap.get((String) it.next()));
	// }
	// return alist;
	// }

}
