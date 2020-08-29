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
package com.chemistry.enotebook.client.gui.page.batch;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.utils.ExperimentPageUtils;
import com.chemistry.enotebook.utils.ResourceKit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.util.*;

public class ParallelCeNProductPlateBuilder {

	public static final Log log = LogFactory.getLog(ParallelCeNProductPlateBuilder.class);

	/**
	 * Create a list of plates by populating the given container with the product batches.
	 * @param productBatchList
	 * @param container
	 * @param notebookPageModel
	 * @return List of plates populated with batches
	 */
	public List getPlates(List productBatchList, Container container, NotebookPageModel notebookPageModel) {
		if (productBatchList == null || container == null)
			throw new IllegalArgumentException("ParallelCeNProductPlateBuilder have received null argument(s)");
		// Get the number of existing plates to assign the sequence number to the plate.
		//numberOfExistingPlates = notebookPageModel.getAllProductPlates().size();
		List productPlates = new ArrayList();
		productPlates.clear();
		double productBatchSize = productBatchList.size();
		double numberOfPlates = Math.ceil(productBatchSize / container.getUnSkippedWellsSize());
		int numOfPlates = (int) numberOfPlates;
		int tenthOfProductsList = 0;
		int fromIndex = 0;
		for (int i = 0; i < numOfPlates; i++) {
			tenthOfProductsList += container.getUnSkippedWellsSize();
			// retrieve a product plate for this sublist and add it to the
			// list with all plates
			if ((tenthOfProductsList + 1) > productBatchList.size()) {
				productPlates.add(getOnePlate(productBatchList.subList(fromIndex, productBatchList.size()), container,notebookPageModel, i));
			} else
				productPlates.add(getOnePlate(productBatchList.subList(fromIndex, tenthOfProductsList), container,notebookPageModel, i));
			fromIndex = tenthOfProductsList;
		}

		//System.out.println("ParallelCeNProductPlateBuilder getPlates productBatchList size "+productBatchList.size()+"numOfPlates "+productPlates.size());
		return productPlates;
	}

	/**
	 * Create a single plate from the container and populated it with the batches.
	 * @param abstractBatches
	 * @param container
	 * @param notebookPageModel
	 * @return
	 */
	private ProductPlate getOnePlate(List abstractBatches, Container container, NotebookPageModel notebookPageModel, int index) {
		long startTime = System.currentTimeMillis();
		//String plateBarcode = generatePlateBarCode(notebookPageModel);
		String plateBarcode = ""; //Plate barcode is blank until it is generated.
		String plateId = getPlateNumber(notebookPageModel,generatePlateLotNo(notebookPageModel, index));
		ProductPlate productPlate = new ProductPlate();
		productPlate.setContainer(container);
		productPlate.setRackPlateFlag("Intermediate");
		productPlate.setPlateBarCode(plateBarcode);
		productPlate.setPlateNumber(plateId);
		// productPlate.setStepNumber(""+stepNum);
		productPlate.setxPositions(container.getXPositions());
		productPlate.setyPositions(container.getYPositions());
		// vb 6/5
		boolean rowMajor = false; // to default to Y
		try {
			if(productPlate.getContainer().getMajorAxis() != null)
			rowMajor = productPlate.getContainer().getMajorAxis().equalsIgnoreCase("x") ? true : false;
		} catch (RuntimeException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Invalid container is used to create a plate.", "Plate creation failed", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		int maxmajor = rowMajor ? container.getXPositions() : container.getYPositions();
		int maxminor = rowMajor ? container.getYPositions() : container.getXPositions();
		int batchCount = 0;
		Map plateWells = new TreeMap();
		ArrayList skippedWellsPositions = container.getSkippedWellPositions();
		for (int m = 0; m < maxminor; m++) { // row or col
			for (int n = 0; n < maxmajor; n++) { // row or col from
				int i = m * maxmajor + n;
				ProductBatchModel batch  = null;
				if (batchCount < abstractBatches.size())
					batch = (ProductBatchModel) abstractBatches.get(batchCount);
					//ParentCompoundModel mCompound = batch.getCompound();
				PlateWell plateWell = null;
				if (skippedWellsPositions.indexOf((i + 1)+ "") == -1)
				{
					plateWell = new PlateWell(batch, CeNConstants.CONTAINER_TYPE_WELL);
					batchCount++;
				}
				else
					plateWell = new PlateWell(null, CeNConstants.CONTAINER_TYPE_WELL);


				//System.out.println("Each Well is inserted with 5.2 g");
				AmountModel containedAmount = new AmountModel(UnitType.MASS);
				/////containedAmount.setValue(5.2);
				plateWell.setContainedWeightAmount(containedAmount);

				int a = rowMajor ? n + 1 : m + 1;

				//System.out.println("minor = " + m + ", major = " + n);
				String wellNumber = rowMajor ? ResourceKit.getABCD(m) + ResourceKit.getNumber(a) : ResourceKit.getABCD(n) + ResourceKit.getNumber(a);
				plateWell.setWellNumber(wellNumber);
				plateWell.setWellPosition(i+1);
				plateWell.setPlate(productPlate);  // vb 8/22
				plateWells.put(new Integer(i), plateWell);
			//}
			}
		}
		productPlate.setWells((PlateWell[]) plateWells.values().toArray(new PlateWell[] {}));
		long endTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info("Product plate creation time : " + (endTime - startTime) + " ms");
		}
		return productPlate;


	}

	/**
	 * Create a single plate from a monomer plate.  Populate the wells with the product batches which have the monomer as a
	 * precursor.  (NOTE:  if the monomer plate has more than one batch, there can be multiple product batches from the plate.
	 * Currently we are simply selecting the first match.  The method returns a List so it can be changed to make more than a
	 * single product plate from a monomer plate.)
	 * @param notebookPageModel
	 * @param stepModel
	 * @param monomerPlate
	 * @return
	 */
	public List createPlatesFromMonomerPlate(NotebookPageModel notebookPageModel, ReactionStepModel stepModel, MonomerPlate monomerPlate) {
		List newPlates;
		long startTime = System.currentTimeMillis();
		newPlates = new ArrayList();
		//numberOfExistingPlates = notebookPageModel.getAllProductPlates().size();
		String plateBarcode = ""; //Plate barcode is blank until it is generated.
		String plateId = getPlateNumber(notebookPageModel,
				generatePlateLotNo(notebookPageModel, 0));
		ProductPlate productPlate = new ProductPlate();
		productPlate.setContainer(monomerPlate.getContainer());
		productPlate.setRackPlateFlag("Intermediate");
		productPlate.setPlateBarCode(plateBarcode);
		productPlate.setPlateNumber(plateId);
		// productPlate.setStepNumber(""+stepNum);
		productPlate.setxPositions(monomerPlate.getContainer().getXPositions());
		productPlate.setyPositions(monomerPlate.getContainer().getYPositions());
		List productBatches = stepModel.getAllProductBatchModelsInThisStep();
		PlateWell[] wells = monomerPlate.getWells();
		Map monomerToProductsMap = this.mapMonomerBatchesToProducts(
				notebookPageModel, stepModel
						.getAllMonomerBatchModelsInThisStep(), productBatches);

		Map plateWells = new TreeMap();

		for (int i = 0; i < wells.length; i++) {
			PlateWell monomerWell = wells[i];
			BatchModel monomerBatch = monomerWell.getBatch();
			if (monomerBatch == null)	//Skipped wells.
				continue;
			String key = monomerBatch.getKey();
			ProductBatchModel productBatch = (ProductBatchModel) monomerToProductsMap
					.get(key);
			if (productBatch == null)
				continue;
			PlateWell productWell = new PlateWell(productBatch, CeNConstants.CONTAINER_TYPE_WELL);
			productWell.setContainedWeightAmount(monomerWell.getContainedWeightAmount());
			productWell.setWellNumber(monomerWell.getWellNumber());
			productWell.setWellPosition(i + 1);
			productWell.setPlate(productPlate);
			plateWells.put(new Integer(i), productWell);
		}
		productPlate.setWells((PlateWell[]) plateWells.values().toArray(
				new PlateWell[] {}));
		newPlates.add(productPlate);
		long endTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info("execution time: " + (endTime - startTime) + " ms");
		}
		return newPlates;
	}

	/**
	 * Create a map of productBatches indexed by monomerBatch key.  NOTE:  This should
	 * be rewritten and put in a utility that caches all the mappings.
	 * @param pageModel
	 * @param monomerBatches
	 * @param productBatches
	 * @return
	 */
	private Map mapMonomerBatchesToProducts(NotebookPageModel pageModel, List monomerBatches, List productBatches) {
		Map monomerToProductsMap;
		long startTime = System.currentTimeMillis();
		monomerToProductsMap = new HashMap();
		Iterator pit = productBatches.iterator();
		ExperimentPageUtils pageUtils = new ExperimentPageUtils();
		while (pit.hasNext()) {
			ProductBatchModel productBatch = (ProductBatchModel) pit.next();
			List monomerBatchKeys = productBatch.getReactantBatchKeys();
			for (Iterator iit = monomerBatchKeys.iterator(); iit.hasNext();) {
				String monomerKey = (String) iit.next();
				MonomerBatchModel monomerBatch = pageUtils
						.getMonomerBatchInTheExperiment(monomerKey, pageModel);
				//if (monomerBatches.contains(monomerBatch))
				monomerToProductsMap.put(monomerBatch.getKey(), productBatch);
			}
		}
		long endTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info("mapMonomerBatchesToProducts execution time: " + (endTime - startTime) + " ms");
		}
		return monomerToProductsMap;

	}

	/**
	 * vb 11/23 make this unique
	 * @param notebookPageModel
	 * @return
	 */
	private String generatePlateLotNo(NotebookPageModel notebookPageModel, int index) {
		//int numberOfNewPlates = this.productPlates.size();
		int lot = 1;
		ArrayList allProductPlates = (ArrayList) notebookPageModel.getAllProductPlatesAndRegPlates();
		if (allProductPlates.size() > 0)
		{
			ProductPlate lastProductPlate = (ProductPlate) allProductPlates.get(allProductPlates.size() - 1);
			String lastLotNo = lastProductPlate.getLotNo();
			String strLotNo = lastLotNo.substring(2, lastLotNo.length());
			lot = Integer.parseInt(strLotNo) + 1;
		}
		lot += index;
		if(lot <= 9) {
			return"PL0" + lot ;
		}
		else {
			return"PL" + lot ;
		}

	}
	private String getPlateNumber(NotebookPageModel notebookPageModel, String plateLotNo) {
		String usr = MasterController.getUser().getNTUserID().substring(0, 3);
		String nbkref = notebookPageModel.getNbRef().getNotebookRef();
		//return "HXB061612-112-PL00" + plateLotNum;
		return usr+nbkref+"-"+plateLotNo;
	}

	public List getCompressedPlatesForWells(ArrayList sourcePlateWellsList,
			Container container, NotebookPageModel pageModel) {
		if (sourcePlateWellsList == null || container == null)
			throw new IllegalArgumentException("ParallelCeNProductPlateBuilder have received null argument(s)");
		List productPlates = new ArrayList();
		productPlates.clear();

		double newPlateWellSize = sourcePlateWellsList.size();
		double numberOfPlates = Math.ceil(newPlateWellSize / container.getUnSkippedWellsSize());
		int numOfPlates = (int) numberOfPlates;
		int tenthOfProductsList = 0;
		int fromIndex = 0;
		for (int i = 0; i < numOfPlates; i++) {
			tenthOfProductsList += container.getUnSkippedWellsSize();
			// retrieve a product plate for this sublist and add it to the
			// list with all plates
			if ((tenthOfProductsList + 1) > sourcePlateWellsList.size()) {
				productPlates.add(getOnePlateFromWells(sourcePlateWellsList.subList(fromIndex, sourcePlateWellsList.size()), container,pageModel, i));
			} else
				productPlates.add(getOnePlateFromWells(sourcePlateWellsList.subList(fromIndex, tenthOfProductsList), container,pageModel, i));
			fromIndex = tenthOfProductsList;
		}

		return productPlates;
	}

	private ProductPlate getOnePlateFromWells(List sourceWellsList, Container container, NotebookPageModel pageModel, int index) {
		long startTime = System.currentTimeMillis();
		//String plateBarcode = generatePlateBarCode(notebookPageModel);
		String plateBarcode = ""; //Plate barcode is blank until it is generated.
		String plateId = getPlateNumber(pageModel, generatePlateLotNo(pageModel, index));
		ProductPlate productPlate = new ProductPlate();
		productPlate.setContainer(container);
		productPlate.setRackPlateFlag("Intermediate");
		productPlate.setPlateBarCode(plateBarcode);
		productPlate.setPlateNumber(plateId);
		// productPlate.setStepNumber(""+stepNum);
		productPlate.setxPositions(container.getXPositions());
		productPlate.setyPositions(container.getYPositions());
		// vb 6/5
		boolean rowMajor = false; //to default to Y
		try {
			if(productPlate.getContainer().getMajorAxis() != null)
			rowMajor = productPlate.getContainer().getMajorAxis().equalsIgnoreCase("x") ? true : false;
		} catch (RuntimeException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Invalid container is used to create a plate.", "Plate creation failed", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		int maxmajor = rowMajor ? container.getXPositions() : container.getYPositions();
		int maxminor = rowMajor ? container.getYPositions() : container.getXPositions();
		int sourceWellCount = -1;
		Map plateWells = new TreeMap();
		ArrayList skippedWellsPositions = container.getSkippedWellPositions();

		PlateWell sourceWell = null;
		ProductBatchModel sourceBatch = null;
		PlateWell targetWell = null;

		for (int m = 0; m < maxminor; m++) { // row or col
			for (int n = 0; n < maxmajor; n++) { // row or col from
				int i = m * maxmajor + n;

				sourceWell = null;
				sourceBatch = null;
				targetWell = null;

				if (skippedWellsPositions.indexOf((i + 1)+ "") == -1)
				{
					if (sourceWellCount + 1 < sourceWellsList.size())
					{
						sourceWellCount++;
						sourceWell = (PlateWell)sourceWellsList.get(sourceWellCount);
						if (sourceWell.getBatch() != null)
							sourceBatch = (ProductBatchModel)sourceWell.getBatch() ;
					}

					targetWell = new PlateWell(sourceBatch, CeNConstants.CONTAINER_TYPE_WELL);
					if (sourceBatch != null)
					{
						targetWell.setContainedWeightAmount(sourceWell.getContainedWeightAmount());
						targetWell.setContainedVolumeAmount(sourceWell.getContainedVolumeAmount());
						targetWell.setContainedMolarity(sourceWell.getContainedMolarity());
						targetWell.setContainerType(sourceWell.getContainerType());
						targetWell.setSolventCode(sourceWell.getSolventCode());

						sourceWell.getContainedWeightAmount().SetValueInStdUnits(0.0);
						sourceWell.getContainedVolumeAmount().SetValueInStdUnits(0.0);
						sourceWell.getContainedMolarity().SetValueInStdUnits(0.0);
					}
				}
				else
				{
					targetWell = new PlateWell(null, CeNConstants.CONTAINER_TYPE_WELL);
					AmountModel containedAmount = new AmountModel(UnitType.MASS);
					targetWell.setContainedWeightAmount(containedAmount);
				}


				int a = rowMajor ? n + 1 : m + 1;

				//System.out.println("minor = " + m + ", major = " + n);
				String wellNumber = rowMajor ? ResourceKit.getABCD(m) + ResourceKit.getNumber(a) : ResourceKit.getABCD(n) + ResourceKit.getNumber(a);
				targetWell.setWellNumber(wellNumber);
				targetWell.setWellPosition(i+1);
				targetWell.setPlate(productPlate);  // vb 8/22
				plateWells.put(new Integer(i), targetWell);
			//}
			}
		}
		productPlate.setWells((PlateWell[]) plateWells.values().toArray(new PlateWell[] {}));
		productPlate.setPlateType(CeNConstants.PLATE_TYPE_REGISTRATION);
		long endTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info("Product plate creation time : " + (endTime - startTime) + " ms");
		}
		return productPlate;
	}

	public String validateRelativePlatesSkippWells(ArrayList<PlateWell> sourceWellsList, Container container, NotebookPageModel pageModel) {
		if (sourceWellsList == null || container == null)
			throw new IllegalArgumentException("ParallelCeNProductPlateBuilder have received null argument(s)");
		List<ProductPlate> productPlates = new ArrayList<ProductPlate>();
		ProductPlate currentSourcePlate = null;
		ProductPlate prevSourcePlate = null;
		ProductPlate targetPlate = null;
		Container currentSourceContainer = null;
		int xIndex = 0;
		int yIndex = 0;
		int count = 0;

		for (int p=0; p< sourceWellsList.size(); p++)
		{
			PlateWell sourceWell = (PlateWell)sourceWellsList.get(p);
			currentSourcePlate = (ProductPlate)sourceWell.getPlate();
			currentSourceContainer = currentSourcePlate.getContainer();
			if (currentSourceContainer.getUnSkippedWellsSize() > container.getUnSkippedWellsSize())
				return "The size of the selected Container must be bigger than all Plates.";
			if (prevSourcePlate == null) //First time
			{
				targetPlate = createNewPlate(pageModel, container, productPlates.size());
				productPlates.add(targetPlate);
			}
			else if (prevSourcePlate != currentSourcePlate) //Next plate, so change positions
			{
				xIndex += prevSourcePlate.getxPositions();
				yIndex += prevSourcePlate.getyPositions();
				currentSourceContainer = currentSourcePlate.getContainer();
				if (currentSourceContainer.getUnSkippedWellsSize() > container.getUnSkippedWellsSize())
					return "The size of the selected Container must be bigger than all Plates.";
				int remainingXWells = container.getXPositions() - xIndex;
				int remainingYWells = container.getYPositions() - yIndex;
				if (container.getMajorAxis().equalsIgnoreCase("X"))
				{
					if (remainingXWells < currentSourceContainer.getXPositions())
					{
						if (remainingYWells < currentSourceContainer.getYPositions() )
						{
							targetPlate = createNewPlate(pageModel, container, productPlates.size());
							productPlates.add(targetPlate);
							xIndex = 0;
							yIndex = 0;
							count = 0;
						}
						else
						{
							yIndex++;//Bcas Y starts with 0.
							xIndex = 0;
						}
					}
					else
					{
						yIndex -= prevSourcePlate.getyPositions();
					}
				}
				else
				{
					if (remainingYWells < currentSourceContainer.getYPositions())
					{
						if (remainingXWells < currentSourceContainer.getXPositions() )
						{
							targetPlate = createNewPlate(pageModel, container, productPlates.size());
							productPlates.add(targetPlate);
							xIndex = 0;
							yIndex = 0;
							count = 0;
						}
						else
						{
							yIndex = 0;
						}
					}
					else
					{
						xIndex -= prevSourcePlate.getxPositions();
					}
				}
			}
			prevSourcePlate = currentSourcePlate;
			PlateWell targetWell = null;
			int plateWellPosition = 0;
			if (container.getMajorAxis().equalsIgnoreCase("X"))
				plateWellPosition = ((getIndexPositionOf(sourceWell.getWellNumber().substring(0, 1)) + yIndex) * container.getXPositions()) + (xIndex - 1) + Integer.valueOf(sourceWell.getWellNumber().substring(1)).intValue();
			else
				plateWellPosition = (getIndexPositionOf(sourceWell.getWellNumber().substring(0, 1)) + (yIndex)) + (((Integer.valueOf(sourceWell.getWellNumber().substring(1)).intValue() - 1) + xIndex) * container.getYPositions());

			targetWell = targetPlate.getWells()[plateWellPosition];

			if ((currentSourceContainer.getSkippedWellNumbers().indexOf(sourceWell.getWellNumber())> -1)
				&& (container.getSkippedWellNumbers().indexOf(targetWell.getWellNumber())== -1)||
				((container.getSkippedWellNumbers().indexOf(targetWell.getWellNumber())> -1))
				&& (currentSourceContainer.getSkippedWellNumbers().indexOf(sourceWell.getWellNumber())== -1))
				return "Skip wells of the Reformatted Plate must match the Source Plate skipp wells.";

			count++;
		}
		return "";
	}

	public List getRelativePlatesForWells(ArrayList<PlateWell> sourceWellsList, Container container, NotebookPageModel pageModel) {
		if (sourceWellsList == null || container == null)
			throw new IllegalArgumentException("ParallelCeNProductPlateBuilder have received null argument(s)");
		List<ProductPlate> productPlates = new ArrayList<ProductPlate>();
		long startTime = System.currentTimeMillis();
		ProductPlate currentSourcePlate = null;
		ProductPlate prevSourcePlate = null;
		ProductPlate targetPlate = null;
		Container currentSourceContainer = null;
		int xIndex = 0;
		int yIndex = 0;
		int count = 0;

		for (int p=0; p< sourceWellsList.size(); p++)
		{
			PlateWell sourceWell = (PlateWell)sourceWellsList.get(p);
			currentSourcePlate = (ProductPlate)sourceWell.getPlate();
			if (prevSourcePlate == null) //First time
			{
				targetPlate = createNewPlate(pageModel, container, productPlates.size());
				productPlates.add(targetPlate);
			}
			else if (prevSourcePlate != currentSourcePlate) //Next plate, so change positions
			{
				xIndex += prevSourcePlate.getxPositions();
				yIndex += prevSourcePlate.getyPositions();
				currentSourceContainer = currentSourcePlate.getContainer();
				int remainingXWells = container.getXPositions() - xIndex;
				int remainingYWells = container.getYPositions() - yIndex;
				if (container.getMajorAxis().equalsIgnoreCase("X"))
				{
					if (remainingXWells < currentSourceContainer.getXPositions())
					{
						if (remainingYWells < currentSourceContainer.getYPositions() )
						{
							targetPlate = createNewPlate(pageModel, container, productPlates.size());
							productPlates.add(targetPlate);
							xIndex = 0;
							yIndex = 0;
							count = 0;
						}
						else
						{
							yIndex++;//Bcas Y starts with 0.
							xIndex = 0;
						}
					}
					else
					{
						yIndex -= prevSourcePlate.getyPositions();
					}
				}
				else
				{
					if (remainingYWells < currentSourceContainer.getYPositions())
					{
						if (remainingXWells < currentSourceContainer.getXPositions() )
						{
							targetPlate = createNewPlate(pageModel, container, productPlates.size());
							productPlates.add(targetPlate);
							xIndex = 0;
							yIndex = 0;
							count = 0;
						}
						else
						{
							yIndex = 0;
						}
					}
					else
					{
						xIndex -= prevSourcePlate.getxPositions();
					}
				}
			}
			prevSourcePlate = currentSourcePlate;
			ProductBatchModel sourceBatch = null;
			PlateWell targetWell = null;
			int plateWellPosition = 0;
			if (container.getMajorAxis().equalsIgnoreCase("X"))
				plateWellPosition = ((getIndexPositionOf(sourceWell.getWellNumber().substring(0, 1)) + yIndex) * container.getXPositions()) + (xIndex - 1) + Integer.valueOf(sourceWell.getWellNumber().substring(1)).intValue();
			else
				plateWellPosition = (getIndexPositionOf(sourceWell.getWellNumber().substring(0, 1)) + (yIndex)) + (((Integer.valueOf(sourceWell.getWellNumber().substring(1)).intValue() - 1) + xIndex) * container.getYPositions());

			if (sourceWell.getBatch() != null)
				sourceBatch = (ProductBatchModel)sourceWell.getBatch();

			targetWell = targetPlate.getWells()[plateWellPosition];
			targetWell.setBatch(sourceBatch);
			if (sourceBatch != null)
			{
				targetWell.setContainedWeightAmount(sourceWell.getContainedWeightAmount());
				targetWell.setContainedVolumeAmount(sourceWell.getContainedVolumeAmount());
				targetWell.setContainedMolarity(sourceWell.getContainedMolarity());
				targetWell.setContainerType(sourceWell.getContainerType());
				targetWell.setSolventCode(sourceWell.getSolventCode());

				sourceWell.getContainedWeightAmount().SetValueInStdUnits(0.0);
				sourceWell.getContainedVolumeAmount().SetValueInStdUnits(0.0);
				sourceWell.getContainedMolarity().SetValueInStdUnits(0.0);
			}
			count++;
		}

		long endTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info("Product plate creation time : " + (endTime - startTime) + " ms");
		}
		return productPlates;
	}

	private ProductPlate createNewPlate(NotebookPageModel pageModel, Container container, int index) {
		//String plateBarcode = generatePlateBarCode(notebookPageModel);
		String plateBarcode = ""; //Plate barcode is blank until it is generated.
		String plateId = getPlateNumber(pageModel, generatePlateLotNo(pageModel, index));
		ProductPlate targetPlate = new ProductPlate();
		targetPlate.setContainer(container);
		targetPlate.setRackPlateFlag("Intermediate");
		targetPlate.setPlateBarCode(plateBarcode);
		targetPlate.setPlateNumber(plateId);
		// productPlate.setStepNumber(""+stepNum);
		targetPlate.setxPositions(container.getXPositions());
		targetPlate.setyPositions(container.getYPositions());
		targetPlate.setPlateType(CeNConstants.PLATE_TYPE_REGISTRATION);
		PlateWell targetWells[] = new PlateWell[container.getXPositions() * container.getYPositions()];

		boolean rowMajor;
		try {
			rowMajor = container.getMajorAxis().equalsIgnoreCase("x") ? true : false;
		} catch (RuntimeException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Invalid container is used to create a plate.", "Plate creation failed", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		int maxmajor = rowMajor ? container.getXPositions() : container.getYPositions();
		int maxminor = rowMajor ? container.getYPositions() : container.getXPositions();

		for (int m = 0; m < maxminor; m++) { // row or col
			for (int n = 0; n < maxmajor; n++) { // row or col from
				int i = m * maxmajor + n;
				targetWells[i] = new PlateWell(null, CeNConstants.CONTAINER_TYPE_WELL);
				targetWells[i].setPlate(targetPlate);
				AmountModel containedAmount = new AmountModel(UnitType.MASS);
				targetWells[i].setContainedWeightAmount(containedAmount);

				int a = rowMajor ? n + 1 : m + 1;
				String wellNumber = rowMajor ? ResourceKit.getABCD(m) + ResourceKit.getNumber(a) : ResourceKit.getABCD(n) + ResourceKit.getNumber(a);
				targetWells[i].setWellNumber(wellNumber);
				targetWells[i].setWellPosition(i + 1);
			}
		}
		targetPlate.setWells(targetWells);
		return targetPlate;
	}

	private int getIndexPositionOf(String indexChar) {
		String [] abcd = ResourceKit.ABCD;
		for (int i=0; i<abcd.length; i++)
		{
			if (abcd[i].equals(indexChar))
				return i;
		}
	return 0;
	}
}
