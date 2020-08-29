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
package com.chemistry.enotebook.client.gui.chlorac;

import com.chemistry.enotebook.chloracnegen.classes.Structure;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.utils.CeNJobProgressHandler;
import com.chemistry.enotebook.utils.SwingWorker;

import java.util.ArrayList;

public class SDFileChloracnegenTester {
	
	private static ChemistryDelegate chemDelegate = null;
	 //run chloracnegen test if the structure is updated
	 //this method launches chloracnegen test only for intended products
	public static void launchChloracnegenCheckerForBatches(final ProductBatchModel[] productBatchModelsArr) {
		final String progressStatus = "Running Chloracnegen Prediction ...";
		SwingWorker worker = new SwingWorker() {
			public Object construct() {
				try {
					CeNJobProgressHandler.getInstance().addItem(progressStatus);
					 //initialize ChemistryDelegate
					if (chemDelegate == null)
						chemDelegate = new ChemistryDelegate();
					ProductBatchModel batchModel = null;
					ChloracnegenBatchStructure cbatch = null;
					ArrayList chloracBatchStrucList = new ArrayList();
					
					ArrayList<ChloracnegenBatchStructure> failedBatchesList = new ArrayList();					
					for (int i=0; i < productBatchModelsArr.length; i++)
					{
						batchModel = (ProductBatchModel)productBatchModelsArr[i];
						byte[] nativeStruct = batchModel.getCompound().getNativeSketch();
						//convert nativestruc into mol struc
						byte[] molFile = chemDelegate.convertChemistry(nativeStruct, "", "MDL Molfile");
						//System.out.println("Intended product mol struc:"+new
						//String(molFile));
						// make a sync call to cct
						//String molStr = new String(molFile);
						cbatch = new ChloracnegenBatchStructure(batchModel.getCompound().getNativeSketch(), molFile, batchModel.getBatchType());
						cbatch.setNBKBatchNumber(batchModel.getBatchNumberAsString());
						chloracBatchStrucList.add(cbatch);
						
						Structure structure = ChloracnegenPredictor.getInstance().checkChloracnegen(new String(molFile));
						if (structure.isChloracnegenicStructure()) {
							batchModel.setChloracnegenFlag(true);
							failedBatchesList.add(cbatch);
						} else {
							batchModel.setChloracnegenFlag(false);
						}
						batchModel.setChloracnegenType(structure.getResults());
						batchModel.setTestedForChloracnegen(true);
						cbatch.setChloracnegenFlag(true);
						
					}
					
					if (failedBatchesList.size() > 0) {
						alertAboutChloracnegens(failedBatchesList);
					}
				} catch (ChemUtilAccessException e) {
					CeNErrorHandler.getInstance().logExceptionMsg(e);
				}				
				return null;
			}
			public void finished() {
				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
			}
		};	      
		worker.start();
	} //end of method
	
	private static void alertAboutChloracnegens(ArrayList strucList) {
		if (strucList != null && strucList.size() > 0) {
			ChloracnegenResultsViewContainer ui = new ChloracnegenResultsViewContainer(MasterController.getGUIComponent());
			ui.addStructures(strucList);
			ui.showGUI();
		}
	}
}
