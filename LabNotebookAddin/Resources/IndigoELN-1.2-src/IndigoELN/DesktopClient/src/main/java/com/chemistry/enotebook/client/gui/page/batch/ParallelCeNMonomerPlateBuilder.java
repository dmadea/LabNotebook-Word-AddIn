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

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.utils.ExperimentPageUtils;
import com.chemistry.enotebook.utils.ResourceKit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class ParallelCeNMonomerPlateBuilder {

	private static final String PLATE = "Plate";

	public static final Log log = LogFactory.getLog(ParallelCeNMonomerPlateBuilder.class);

	private static int plateLotNum = -1;
    private int plateSequenceNum = 0;

	public List getPlates(List monomerBatchList, Container container, String namePrefix, NotebookPageModel notebookPageModel) {
		if (monomerBatchList == null || container == null || notebookPageModel == null)
			throw new IllegalArgumentException("ParallelCeNMonomerPlateBuilder have received null argument(s)");
		List monomerPlates = new ArrayList(50);
		double monomerBatchSize = monomerBatchList.size();
		// double platesno = productBatchSize / container.getUnSkippedWellsSize();
		// //System.out.println("rint"+Math.rint(productBatchSize /
		// container.getUnSkippedWellsSize() ));
		// //System.out.println("floor"+Math.floor( productBatchSize /
		// container.getUnSkippedWellsSize()));
		double numberOfPlates = Math.ceil(monomerBatchSize / container.getUnSkippedWellsSize());
/*		System.out.println("container.getUnSkippedWellsSize()"+container.getUnSkippedWellsSize());
		System.out.println("monomerBatchList.size() "+monomerBatchList.size());
		System.out.println("numberOfPlates"+numberOfPlates);*/
		int numOfPlates = (int) numberOfPlates;
//		System.out.println("numOfPlates "+numOfPlates);
		int tenthOfmonomerList = 0;
		int fromIndex = 0;
		for (int i = 0; i < numOfPlates; i++) {
			tenthOfmonomerList += container.getUnSkippedWellsSize();
			++plateSequenceNum;
			// retrieve a product plate for this sublist and add it to the
			// list with all plates
			if (namePrefix == null || namePrefix.length() == 0)
				namePrefix = PLATE;
			if ((tenthOfmonomerList + 1) > monomerBatchList.size()) {
				monomerPlates.add(getOnePlate(monomerBatchList.subList(fromIndex, monomerBatchList.size()), container, namePrefix+"-"+plateSequenceNum, notebookPageModel));
			} else
				monomerPlates.add(getOnePlate(monomerBatchList.subList(fromIndex, tenthOfmonomerList), container, namePrefix+"-"+plateSequenceNum, notebookPageModel));
			fromIndex = tenthOfmonomerList;
		}
		return monomerPlates;
	}

	private MonomerPlate getOnePlate(List abstractBatches, Container container, String name, NotebookPageModel notebookPageModel) {
		// String plateBarcode = getPlateBarCode();
		long startTime = System.currentTimeMillis();
		String plateBarcode = name;// = getPlateBarCode();
		if (name == null || name.equalsIgnoreCase("")) {
			plateBarcode = getPlateBarCode();
		}

		//String plateDescription = "Monoemr Plate Description";  vb 5/14/07
		MonomerPlate monomerPlate = new MonomerPlate();
		monomerPlate.setContainer(container);
		// monomerPlate.setRackPlateFlag("Intermediate");
		monomerPlate.setPlateBarCode(plateBarcode);
		// monomerPlate.setPlateId(plateId);
		// productPlate.setStepNumber(""+stepNum);
		monomerPlate.setxPositions(container.getXPositions());
		monomerPlate.setyPositions(container.getYPositions());
		//monomerPlate.setDescription(plateDescription);  vb 5/14/07
		boolean rowMajor = false; //to default it to Y axis according to consensus reached by Site representatives
		//To Handle CompoundManagement container defintions that doesn't have proper axis defs
		if(monomerPlate.getContainer().getMajorAxis() != null)
		{
		rowMajor = monomerPlate.getContainer().getMajorAxis().equalsIgnoreCase("x") ? true : false;
		}
		int maxmajor = rowMajor ? container.getXPositions() : container.getYPositions();
		int maxminor = rowMajor ? container.getYPositions() : container.getXPositions();
		ArrayList skippedWellsPositions = container.getSkippedWellPositions();
		int batchCount = 0;
		Map plateWells = new TreeMap();
		//for (int m = 0; m < container.getXPositions(); m++) { // row or col
		for (int m = 0; m < maxminor; m++) { // row or col
			// from 0
			//for (int n = 0; n < container.getYPositions(); n++) { // row
			for (int n = 0; n < maxmajor; n++) { // row or col from
				// from 0
				//int i = m * container.getYPositions() + n;
				int i = m * maxmajor + n;
				if (batchCount < abstractBatches.size()) {
					// rbatch.setMolecularFormula(getMolecularFormula());
					// rbatch.setMolecularWeightAmount(new
					// Amount(getMolecularWeight(), new Unit("GM")));
					PlateWell plateWell = null;
					if (skippedWellsPositions.indexOf((i + 1)+ "") == -1)
					{
						MonomerBatchModel deliveredBatch = (MonomerBatchModel) abstractBatches.get(batchCount);
						ParentCompoundModel mCompound = deliveredBatch.getCompound();
						plateWell = new PlateWell(deliveredBatch);
						// Get the matching monomer batch in the design
						MonomerBatchModel designBatch = new ExperimentPageUtils().getMatchingMonomerBatchInTheExperiment(mCompound.getRegNumber(), notebookPageModel);
						if (designBatch != null) {
							// do something here
							plateWell.setBatch(designBatch);
						}
						plateWell.setContainedWeightAmount(deliveredBatch.getWeightAmount());
						batchCount++;
					}
					else
						plateWell = new PlateWell(null, "");

					int a = rowMajor ? n + 1 : m + 1;
					//System.out.println("minor = " + m + ", major = " + n);
					String wellNumber = rowMajor ? ResourceKit.getABCD(m) + ResourceKit.getNumber(a) : ResourceKit.getABCD(n) + ResourceKit.getNumber(a);
					plateWell.setWellNumber(wellNumber);
					//System.out.println("m = " + m + ", n = " + n + ", i = " + i + " + wellNumber = " + wellNumber);
					//System.out.println("index = " + i + ", batchId = " + plateWell.getBatch().getCompound().getRegNumber() + " => " + wellNumber);
					plateWell.setWellPosition(i+1);
					plateWell.setPlate(monomerPlate);
					plateWells.put(new Integer(i), plateWell);
				}
			}
		}
		monomerPlate.setWells((PlateWell[]) plateWells.values().toArray(new PlateWell[] {}));
		long endTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info("Monomer plate create time: " + (endTime - startTime) + " ms");
		}
		return monomerPlate;
	}

	private String getPlateBarCode() {
		return "PL_" + plateSequenceNum;
	}

	/**
	 * @return the plateSequenceNum
	 */
	public int getPlateSequenceNum() {
		return plateSequenceNum;
	}

	/**
	 * @param plateSequenceNum the plateSequenceNum to set
	 */
	public void setPlateSequenceNum(List monomerPlates) {
		int maxSeqNumber = 0;
		for (Iterator it = monomerPlates.iterator(); it.hasNext();) {
			MonomerPlate monomerPlate = (MonomerPlate) it.next();
			String barcode = monomerPlate.getPlateBarCode();
			String txtnum= barcode.substring(barcode.indexOf("-") + 1);
			try {
				int index = Integer.valueOf(txtnum).intValue();
				if (index > maxSeqNumber)
					maxSeqNumber = index;
			} catch (NumberFormatException ex) {}
		}
		this.plateSequenceNum = maxSeqNumber;
	}

	public void assignPlateNumbers(MonomerPlate[] monomerPlates)
	{
		String namePrefix = PLATE;
		for (MonomerPlate monomerPlate : monomerPlates)
		{
			++plateSequenceNum;
			monomerPlate.setPlateNumber(namePrefix+"-"+plateSequenceNum);
			monomerPlate.setPlateBarCode(namePrefix+"-"+plateSequenceNum);
		}
	}
}
