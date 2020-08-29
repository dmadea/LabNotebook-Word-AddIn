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
package com.chemistry.enotebook.client.gui.page.experiment.plate;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.plateviewer.CeNVisualPlateFactory;
import com.chemistry.enotebook.compoundmgmtservice.delegate.CompoundMgmtServiceDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.Decoder;
import com.chemistry.enotebook.utils.MonomerBatchStatusMapper;
import com.virtuan.plateVisualizer.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CeNMonomerPlaterBuilder implements WellPropertiesChangeListener { // vb 8/21 implements ActionListener {
	private static final Log log = LogFactory.getLog(CeNMonomerPlaterBuilder.class);
	private static String CP_STRUC_NAME = "Structure";
	private WellMood filledWellMood;
	private WellMood emptyWellMood;
	private CeNVisualPlateFactory plateFactory;
	private NotebookPageModel notebookPageModel;  // vb 7/12 need this to enable save

	public CeNMonomerPlaterBuilder(NotebookPageModel notebookPageModel) {
		plateFactory = new CeNVisualPlateFactory();
		this.notebookPageModel = notebookPageModel;
	}

	public JPanel buildCeNMonomeRackViewer(Rack rack, int col, int row) throws Exception {
		String command = plateFactory.CEN_RACK;
		filledWellMood = plateFactory.createFilledWellMood(command);
		emptyWellMood = plateFactory.createEmptyWellMood(command);
		PlateMood plateMood = plateFactory.createPlateMood(command, "Rack Title");
		// vb 7/11 Does a rack have a row or column order??????????????
		InteractivePlate ip = new InteractivePlate(true, filledWellMood.getRimColor(), emptyWellMood.getRimColor(), Color.WHITE,
				row, col, plateMood, filledWellMood, new ArrayList(), true, "Well Title", null, null, null, true);
		Tube[] tubes = rack.getTubes();
		log.info("tubes.length: " + tubes.length);
		int numberofTubes = tubes.length;
		int counter = 0;
		PlateModel platemodel = ip.getPlateModel();
		getStructuresfromCLS4Rack(rack);
		for (int y = 1; y <= row; y++) {
			for (int x = 1; x <= col; x++) {
				WellModel wellModel = platemodel.getWellByCoordinate(x, y);
				if (counter < numberofTubes) {// well needs to be filled
					// 2. get handle of PlateWell (CompoundManagement/CeN well object
					// model)
					Tube tube = tubes[counter];
					// 3. set WellMood (plateVisualizer plate/well model)
					wellModel.setWellMood((WellMood) filledWellMood.clone());
					wellModel.getProperty(InteractivePlate.PROPERTY_FILLED_NAME).setValue(InteractivePlate.PROPERTY_FILLED_VALUE);
					// 4. adding properties to display (plateVisualizer
					// plate/well model)
					int z = 0;
					// wellModel.addProperty(new WellProperty(CP_LOCATION,
					// CP_PROPERTY_VALUE + "(" + x + "," + y + ")", true,
					// z));
					// z++;
					wellModel.addProperty(new WellProperty(CP_STRUC_NAME, tube.getBatch().getCompound().getMolfile(), true, z));// to
					// be
					// replace
					// by
					// CLS
					z++;
					wellModel.addProperty(new WellProperty("Bar Code", tube.getBarcode(), true, z));
					z++;
					wellModel
							.addProperty(new WellProperty("Compound Number", tube.getBatch().getCompound().getRegNumber(), true, z));
					z++;
					wellModel
							.addProperty(new WellProperty("Batch Number", tube.getBatch().getConversationalBatchNumber(), true, z));
					z++;
					wellModel.addProperty(new WellProperty("Molecular Formula", tube.getBatch().getMolecularFormula(), true, z));
					z++;
					counter++;
					// log.info(" counter < plateWellSize counter is " +
					// counter);
				} else {
					// log.info("counter is over");
					wellModel.setWellMood((WellMood) emptyWellMood.clone());
					// wellModel.removeAllProperties();
					wellModel.addProperty(new WellProperty(CP_STRUC_NAME, "", true, 0));// to be replace by
					// wellModel
					// .getProperty(InteractivePlate.PROPERTY_FILLED_NAME)
					// .setValue(InteractivePlate.PROPERTY_EMPTY_VALUE);
				}
			}
		}
		StaticPlateRenderer mStaticPlateRenderer = ip.getPlateRenderer();
		setStandardPopUpMenuItems(mStaticPlateRenderer);
		mStaticPlateRenderer.setPreferredSize(new Dimension(300, 300));
		return mStaticPlateRenderer;
	}

	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	public JPanel buildCeNMonomerPlateViewer(MonomerPlate monomerPlate, MonomerBatchDetailContainer batchDetail) {
		int row = monomerPlate.getyPositions();
		int col = monomerPlate.getxPositions();
		// String command = plateFactory.PLATE_TYPE_WHITE_NUNC_PLATE;
		String command = plateFactory.CEN_PLATE;
		filledWellMood = plateFactory.createFilledWellMood(command);
		emptyWellMood = plateFactory.createEmptyWellMood(command);
		PlateMood plateMood = plateFactory.createPlateMood(command, "");
		// String command = BasicVisualPlateFactory.PLATE_TYPE_WHITE_NUNC_PLATE;
		MonomerBatchDetailContainer[] batchDetails = new MonomerBatchDetailContainer[1];
		batchDetails[0] = batchDetail;  // vb 6/21
		// vb 7/11
		WellPropertiesChangeListener[] wellPropertiesChangeListeners = new WellPropertiesChangeListener[1];
		wellPropertiesChangeListeners[0] = this;
		boolean isRowMajor = false; // Default to Y axis
		if(monomerPlate.getContainer().getMajorAxis() != null)
		{
		isRowMajor = monomerPlate.getContainer().getMajorAxis().equals("X") ? true : false;  // vb 7/11
		}
		InteractivePlate ip = new InteractivePlate(true,
												   filledWellMood.getRimColor(),
												   emptyWellMood.getRimColor(),
												   Color.WHITE,
												   row,
												   col,
												   plateMood,
												   filledWellMood,
												   new ArrayList(),
												   true,
												   "Well Title",
												   null,
												   batchDetails,
												   wellPropertiesChangeListeners,
												   isRowMajor);
		/*
		 * StaticPlate ip = new StaticPlate(row, col, plateMood,filledWellMood,new ArrayList(), true, "Well Title", null);
		 */
		try {
			setupWellProperties(ip.getPlateModel(), monomerPlate);
		} catch (Exception ex) {
			//log.error(ex.getMessage());
			ex.printStackTrace();
		}
		// AddEmptyWellRow(col, emptyWellMood, ip.getPlateModel());
		// AddSampleNumbers(col, row, ip.getPlateModel());
		StaticPlateRenderer mStaticPlateRenderer = ip.getPlateRenderer();
		setStandardPopUpMenuItems(mStaticPlateRenderer);
		mStaticPlateRenderer.setPreferredSize(new Dimension(300, 300));
		/*
		 * JPanel panel = new JPanel(); panel.add(mStaticPlateRenderer);
		 *
		 * JScrollPane scroll = new JScrollPane(); scroll.getViewport().add(panel);
		 */
		return mStaticPlateRenderer;
	}

	private void setupWellProperties(PlateModel platemodel, MonomerPlate monomerPlate) throws Exception {
		int numRows = monomerPlate.getyPositions();
		int numColumns = monomerPlate.getxPositions();
		// log.info("monomerPlate.getyPositions()" + numRows);
		// log.info("monomerPlate.getxPositions()" + numColumns);
		// monomerPlate.
		PlateWell[] plateWells = monomerPlate.getWells();
		//log.info("plateWells.length: " + plateWells.length);
		int plateWellSize = plateWells.length;
		for (int y = 1; y <= numRows; y++) {
			for (int x = 1; x <= numColumns; x++) {
				WellModel wellModel = platemodel.getWellByCoordinate(x, y);
				wellModel.setWellMood((WellMood) emptyWellMood.clone());
				// wellModel.removeAllProperties();
				wellModel.addProperty(new WellProperty(CP_STRUC_NAME, "", true, 0));// to be replace by
			}
		}
		for (int i = 0; i < plateWellSize; i++) {
			PlateWell aPlateWell = plateWells[i];
			if (aPlateWell.getBatch() == null)
				continue;
			String wellPosition = aPlateWell.getWellNumber();

			//System.out.println("wellPosition *********"+wellPosition);
			//int row = PlateGeneralMethods.convertWellPositionToNumericValue(wellPosition, "y");
			//int col = PlateGeneralMethods.convertWellPositionToNumericValue(wellPosition, "x");
			//log.info("col " + col + "row " + row + " wellPosition " + wellPosition);

			//System.out.println("col " + col + "row " + row + " wellPosition " + wellPosition);
			//WellModel wellModel = platemodel.getWellByCoordinate(col, row);
			WellModel wellModel = platemodel.getWellBySetIndex(i);
			//System.out.println("wellModel "+wellModel);
			//System.out.println("aPlateWell "+aPlateWell);
			wellModel.setUserWellObject(aPlateWell);
			// 3. set WellMood (plateVisualizer plate/well model)
			//wellModel.setWellMood((WellMood) filledWellMood.clone());
			wellModel.getProperty(InteractivePlate.PROPERTY_FILLED_NAME).setValue(InteractivePlate.PROPERTY_FILLED_VALUE);
			int z = 0;
			// wellModel.addProperty(new WellProperty(CP_LOCATION,
			// CP_PROPERTY_VALUE + "(" + row + "," + col + ")", true, z));
			// z++;
			wellModel.addProperty(new WellProperty(CP_STRUC_NAME, Decoder.decodeString(aPlateWell.getBatch().getCompound()
					.getStringSketchAsString()), true, z));
			z++;
			wellModel.addProperty(new WellProperty("Well Number", aPlateWell.getWellNumber(), true, z));
			z++;
			/*
			 * wellModel.addProperty(new WellProperty("Batch Tracking Id", "" + aPlateWell.getBatchTrackingId(), true, z)); z++;
			 */
			wellModel.addProperty(new WellProperty("Well Position", "" + aPlateWell.getWellPosition(), true, z));
			z++;
			/*
			 * 22222 wellModel.addProperty(new WellProperty("Compound Number", aPlateWell.getBatch().getCompound().getRegNumber(),
			 * true, z)); z++; wellModel.addProperty(new WellProperty( "Batch Number", aPlateWell.getBatch()
			 * .getConversationalBatchNumber(), true, z)); z++;
			 */
			/*
			 * wellModel.addProperty(new WellProperty("Molecular Formula", aPlateWell.getBatch().getMolecularFormula(), true, z));
			 * z++;
			 */
		}
	}

//	public JScrollPane buildCeNPlateViewer(String title, int col, int row) {
//		// IVisualPlateFactory plateFactory = PlateFactoryBuilder
//		// .createPlateFactory(PlateFactoryBuilder.FACTORY_TYPE_BASIC);
//		CeNVisualPlateFactory plateFactory = new CeNVisualPlateFactory();
//		String command = plateFactory.PLATE_TYPE_BLUE_BOX;
//		WellMood filledWellMood = plateFactory.createFilledWellMood(command);
//		WellMood emptyWellMood = plateFactory.createEmptyWellMood(command);
//		PlateMood plateMood = plateFactory.createPlateMood(command, "Plate Title");
//		// String command = BasicVisualPlateFactory.PLATE_TYPE_WHITE_NUNC_PLATE;
//		InteractivePlate ip = new InteractivePlate(true, filledWellMood.getRimColor(), emptyWellMood.getRimColor(), Color.RED, row,
//				col, plateMood, filledWellMood, new ArrayList(), true, "Well Title", null, null);
//		AddEmptyWellRow_org(col, emptyWellMood, ip.getPlateModel());
//		AddSampleNumbers_org(col, row, ip.getPlateModel());
//		StaticPlateRenderer mStaticPlateRenderer = ip.getPlateRenderer();
//		setStandardPopUpMenuItems(mStaticPlateRenderer);
//		mStaticPlateRenderer.setPreferredSize(new Dimension(300, 300));
//		JPanel panel = new JPanel();
//		panel.add(mStaticPlateRenderer);
//		JScrollPane scroll = new JScrollPane();
//		scroll.getViewport().add(panel);
//		return scroll;
//	}

/*	*//**
	 * method to add an empty first row
	 *
	 * @param numColumns
	 * @param emptyWellMood
	 *//*
	private void AddEmptyWellRow_org(int numColumns, WellMood emptyWellMood, PlateModel plateModel) {
		WellModel well;
		try {
			for (int x = 1; x <= numColumns; x++) {
				well = plateModel.getWellByCoordinate(x, 1);
				well.setWellMood((WellMood) emptyWellMood.clone());
				well.getProperty(InteractivePlate.PROPERTY_FILLED_NAME).setValue(InteractivePlate.PROPERTY_EMPTY_VALUE);
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new PlateVisualizationError(e.getMessage());
		}
	}

	*//**
	 * method to add an empty first row
	 *
	 * @param numColumns
	 * @param emptyWellMood
	 *//*
	private void AddSampleNumbers_org(int numColumns, int numRows, PlateModel plateModel) {
		WellModel well;
		int z = 1;
		try {
			for (int y = 2; y <= numRows; y++) {
				for (int x = 1; x <= numColumns; x++) {
					well = plateModel.getWellByCoordinate(x, y);
					// well.addProperty(new WellProperty(CP_LOCATION,
					// CP_PROPERTY_VALUE + String.valueOf(z), true, 1));
					well.addProperty(new WellProperty(CP_STRUC_NAME, CP_STRUC_VALUE, true, 2));
					z++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/
	protected void setStandardPopUpMenuItems(StaticPlateRenderer mStaticPlateRenderer) {
		List statusSelections = MonomerBatchStatusMapper.getInstance().getSelections();
		mStaticPlateRenderer.createWellPropertiesPopupMenu();
		for (Iterator it = statusSelections.iterator(); it.hasNext();) {
			mStaticPlateRenderer.addWellPropertiesMenuItem((String) it.next());
		}
	}

	// This needs to use the MonomerBatchStatusMapper when status is broken into two fields
	// vb 8/21
	public void wellPropertiesChanged(WellPropertiesChangeEvent e) {
		PlateWell well = (PlateWell) e.getSubObject();
		MonomerBatchModel model = (MonomerBatchModel) well.getBatch();
		String command = e.getCommand();
		BatchAttributeComponentUtility.propagateMonomerBatchStatus((MonomerBatchModel) model, command, notebookPageModel);
		model.setStatus(command);
		this.enableSaveButton(model);
	}

	private void enableSaveButton(MonomerBatchModel model) {
		if (model != null) {
			MasterController.getGUIComponent().enableSaveButtons();
			model.setModelChanged(true);
			if (this.notebookPageModel != null) {
				this.notebookPageModel.setModelChanged(true);
				this.notebookPageModel.setEditable(true);
			}
			// The singleton page model is set to uneditable.  Why?
			// Temporary fix...
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		log.info("command is " + command);
	}

	public void getStructuresfromCLS4Rack(Rack rack) {
		try {
			List<String> strs = new ArrayList<String>();
			for (String compoundNo : rack.getBatchIDs())
				strs.add(new CompoundMgmtServiceDelegate().getStructureByCompoundNo(compoundNo).get(0));
			String structures[] = strs.toArray(new String[0]);
			Tube[] tubes = rack.getTubes();
			if (structures.length == tubes.length) {
				for (int i = 0; i < tubes.length; i++) {
					tubes[i].getBatch().getCompound().setMolfile(structures[i]);
				}
			}
		} catch (Exception ex) {
			log.error("Failed to retrieve structures for rack: " + 
			          (rack != null ? CommonUtils.getAsPipeSeperateValues(rack.getBatchIDs()) : "null object passed as rack"), ex);
		}
	}

	public void getStructuresfromCLS4MonomerPlate(MonomerPlate monomerPlate) {
		try {
			List<String> strs = new ArrayList<String>();
			for (String compoundNo : monomerPlate.getBatchIDs())
				strs.add(new CompoundMgmtServiceDelegate().getStructureByCompoundNo(compoundNo).get(0));
			String structures[] = strs.toArray(new String[0]);
			PlateWell[] wells = monomerPlate.getWells();
			if (structures.length == wells.length) {
				for (int i = 0; i < wells.length; i++) {
					// //System.out.println(structures[i]);
					wells[i].getBatch().getCompound().setMolfile(structures[i]);
				}
			}
		} catch (Exception ex) {
			log.error("Failed to retrieve structures for monomer plate: " + 
			          (monomerPlate != null ? monomerPlate.getPlateBarCode() : "null object passed as monomer plate"), ex);
		}
	}


}
