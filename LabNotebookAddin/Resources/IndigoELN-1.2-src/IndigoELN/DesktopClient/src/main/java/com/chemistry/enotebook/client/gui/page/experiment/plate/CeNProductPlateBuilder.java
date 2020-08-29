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
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionListener;
import com.chemistry.enotebook.client.gui.plateviewer.CeNVisualPlateFactory;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.PlateWell;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.ProductPlate;
import com.chemistry.enotebook.utils.Decoder;
import com.chemistry.enotebook.utils.ProductBatchStatusMapper;
import com.virtuan.plateVisualizer.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class CeNProductPlateBuilder implements WellPropertiesChangeListener {

	public static final Log log = LogFactory.getLog(CeNProductPlateBuilder.class);

	private CeNVisualPlateFactory plateFactory;
	private WellMood filledWellMood;
	private WellMood emptyWellMood;
	private static String CP_STRUC_NAME = "Structure";
	//private List mStaticPlateRenderers;
	//private List mProductPlates;
	private JTabbedPane tab = new JTabbedPane();
	NotebookPageModel pageModel;

	public CeNProductPlateBuilder(NotebookPageModel pageModel) {
		this.pageModel = pageModel;
		//mProductPlates = pageModel.getAllProductPlates();
		plateFactory = new CeNVisualPlateFactory();
	}

	public CeNProductPlateBuilder() {
		plateFactory = new CeNVisualPlateFactory();
	}

//	public List getStaticPlateRenderers() {
//		return mStaticPlateRenderers;
//	}

	public StaticPlateRenderer buildCeNproductPlateViewer(ProductPlate productPlate, BatchSelectionListener[] batchSelectionListeners) {
		long startTime = System.currentTimeMillis();
		int row = productPlate.getyPositions();
		int col = productPlate.getxPositions();
		// String command = plateFactory.PLATE_TYPE_WHITE_NUNC_PLATE;
		String command = plateFactory.CEN_PLATE;
		filledWellMood = plateFactory.createFilledWellMood(command);
		emptyWellMood = plateFactory.createEmptyWellMood(command);
		PlateMood plateMood = plateFactory.createPlateMood(command, ""); // vb 6/14 removed title
		// String command = BasicVisualPlateFactory.PLATE_TYPE_WHITE_NUNC_PLATE;
		boolean isRowMajor = false; // Default to Y axis
		if(productPlate.getContainer().getMajorAxis() != null)
		isRowMajor = productPlate.getContainer().getMajorAxis().equals("X") ? true : false;  // vb 7/11
		// vb 7/11
		WellPropertiesChangeListener[] wellPropertiesChangeListeners = new WellPropertiesChangeListener[1];
		wellPropertiesChangeListeners[0] = this;
		InteractivePlate ip = new InteractivePlate(true, filledWellMood.getRimColor(), emptyWellMood.getRimColor(), Color.WHITE,
				row, col, plateMood, filledWellMood, new ArrayList(), true, "Well Title", null, batchSelectionListeners, wellPropertiesChangeListeners, isRowMajor);
		/*
		 * StaticPlate ip = new StaticPlate(row, col, plateMood,filledWellMood,new ArrayList(), true, "Well Title", null);
		 */
		try {
			setupWellProperties(ip.getPlateModel(), productPlate);
		} catch (Exception ex) {
			//log.error(ex.getMessage());
			ex.printStackTrace();
		}
		// AddEmptyWellRow(col, emptyWellMood, ip.getPlateModel());
		// AddSampleNumbers(col, row, ip.getPlateModel());
		StaticPlateRenderer mStaticPlateRenderer = ip.getPlateRenderer();
////////////		setStandardPopUpMenuItems(mStaticPlateRenderer);
		mStaticPlateRenderer.setPreferredSize(new Dimension(300, 300));
		setStandardPopUpMenuItems(mStaticPlateRenderer);
		/*
		 * JPanel panel = new JPanel(); panel.add(mStaticPlateRenderer);
		 *
		 * JScrollPane scroll = new JScrollPane(); scroll.getViewport().add(panel);
		 */
		long endTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info("Product plate view create time : " + (endTime - startTime) + " ms");
		}
		return mStaticPlateRenderer;
	}

	private void setupWellProperties(PlateModel platemodel, ProductPlate productPlate) throws Exception {
		int numRows = productPlate.getyPositions();
		int numColumns = productPlate.getxPositions();
		// log.info("monomerPlate.getyPositions()" + numRows);
		// log.info("monomerPlate.getxPositions()" + numColumns);
		// monomerPlate.
		PlateWell[] plateWells = productPlate.getWells();
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
			String wellPosition = aPlateWell.getWellNumber();
			if (aPlateWell.getBatch() == null)
				continue;
			//System.out.println("wellPosition *********"+wellPosition);
			int row = PlateGeneralMethods.convertWellPositionToNumericValue(wellPosition, "y");
			int col = PlateGeneralMethods.convertWellPositionToNumericValue(wellPosition, "x");
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

	public void actionPerformed(ActionEvent e) {
		log.info(e.getSource());
		/*
		 * StaticPlateRenderer mStaticPlateRenderer =(StaticPlateRenderer)e.getSource(); String command = e.getActionCommand();
		 * log.info("command is " + command); WellModel selectedWellModel = mStaticPlateRenderer .getRightMouseSeletedWell(); if
		 * (selectedWellModel != null) {
		 *
		 * PlateWell mPlateWell = (PlateWell) selectedWellModel .getUserWellObject(); if (mPlateWell != null) { ProductBatch
		 * mProductbatch = (ProductBatch) mPlateWell .getBatch(); log.info("*****" + mProductbatch.getCompound()); } } else{
		 * log.info("selectedWellModel==null"); }
		 */
	}


	public JTabbedPane getTab() {
		return tab;
	}

//	public List getMProductPlates() {
//		return mProductPlates;
//	}

	// vb 7/11
	protected void setStandardPopUpMenuItems(StaticPlateRenderer mStaticPlateRenderer) {
		List statusSelections = ProductBatchStatusMapper.getInstance().getSelections();
		mStaticPlateRenderer.createWellPropertiesPopupMenu();
		for (Iterator it = statusSelections.iterator(); it.hasNext();) {
//			String status = (String) it.next();
//			if (status.indexOf("Pass") > 0 )
			mStaticPlateRenderer.addWellPropertiesMenuItem((String) it.next());
		}
	}


	// This needs to use the ProductBatchStatusMapper
	// vb 7/11
	public void wellPropertiesChanged(WellPropertiesChangeEvent e) {
		PlateWell well = (PlateWell) e.getSubObject();
		ProductBatchModel model = (ProductBatchModel) well.getBatch();
		String command = e.getCommand();
		ProductBatchStatusMapper statusMapper = ProductBatchStatusMapper.getInstance();
		model.setSelectivityStatus(statusMapper.getSelectivityStatus(command));
		model.setContinueStatus(statusMapper.getContinueStatus(command));
		this.enableSaveButton(model);
	}

	private void enableSaveButton(ProductBatchModel model) {
		if (model != null) {
			MasterController.getGUIComponent().enableSaveButtons();
			model.setModelChanged(true);
			if (this.pageModel != null) {
				this.pageModel.setModelChanged(true);
				this.pageModel.setEditable(true);
			}
			// The singleton page model is set to uneditable.  Why?
			// Temporary fix...
		}
	}
}
