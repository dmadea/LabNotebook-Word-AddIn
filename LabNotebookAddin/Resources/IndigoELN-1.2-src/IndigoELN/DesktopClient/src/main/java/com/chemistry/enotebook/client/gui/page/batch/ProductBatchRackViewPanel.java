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
import com.chemistry.enotebook.client.gui.page.experiment.plate.WellPropertiesChangeEvent;
import com.chemistry.enotebook.client.gui.page.experiment.plate.WellPropertiesChangeListener;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ProductBatchRackViewPanel will render a plate view for given set of Product batchs Plate layout will be m*n, and can be
 * re-arranged
 *
 * 
 *
 */
public class ProductBatchRackViewPanel implements WellPropertiesChangeListener {
//public class ProductBatchRackViewPanel implements ActionListener {
	private CeNVisualPlateFactory plateFactory;
	private WellMood filledWellMood;
	private WellMood emptyWellMood;
	private static final Log log = LogFactory.getLog(ProductBatchRackViewPanel.class);
	private static String CP_STRUC_NAME = "Structure";
	private List mStaticPlateRenderers;
	private List mProductPlates;
	private JTabbedPane tab = new JTabbedPane();
	NotebookPageModel pageModel;
	private static final String SET_AS_FAILED = "Set as Failed";
	private static final String SET_AS_PASSED = "Set as Passed";
	private static final String SET_AS_SUSPECT = "Set as Suspect";

	public ProductBatchRackViewPanel(NotebookPageModel pageModel) {
		this.pageModel = pageModel;
		mProductPlates = pageModel.getAllProductPlatesAndRegPlates();
		plateFactory = new CeNVisualPlateFactory();
		if (mProductPlates.size() > 0) {
			tab = new JTabbedPane(JTabbedPane.TOP);
			for (int i = 0; i < mProductPlates.size(); i++) {
				ProductPlate mProductPlate = (ProductPlate) mProductPlates.get(i);
				StaticPlateRenderer mStaticPlateRenderer = buildCeNproductPlateViewer(mProductPlate, null);
				// mStaticPlateRenderers.add(mStaticPlateRenderer);
				tab.addTab(mProductPlate.getLotNo(), mStaticPlateRenderer);
			}
		}
	}

	public ProductBatchRackViewPanel() {
		plateFactory = new CeNVisualPlateFactory();
	}

	public List getStaticPlateRenderers() {
		return mStaticPlateRenderers;
	}

	public StaticPlateRenderer buildCeNproductPlateViewer(ProductPlate productPlate, BatchSelectionListener[] batchSelectionListeners) {
		int row = productPlate.getyPositions();
		int col = productPlate.getxPositions();
		// String command = plateFactory.PLATE_TYPE_WHITE_NUNC_PLATE;
		String command = plateFactory.CEN_PLATE;
		filledWellMood = plateFactory.createFilledWellMood(command);
		emptyWellMood = plateFactory.createEmptyWellMood(command);
		PlateMood plateMood = plateFactory.createPlateMood(command, "");  // vb 6/14 removed title
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
			log.error("Failed setting up well properties for ProductBatchRackViewPanel", ex);
//			ex.printStackTrace();
		}
		// AddEmptyWellRow(col, emptyWellMood, ip.getPlateModel());
		// AddSampleNumbers(col, row, ip.getPlateModel());
		StaticPlateRenderer mStaticPlateRenderer = ip.getPlateRenderer();
		setStandardPopUpMenuItems(mStaticPlateRenderer);
		// mStaticPlateRenderer.setPreferredSize(new Dimension(300, 300));
		/*
		 * JPanel panel = new JPanel(); panel.add(mStaticPlateRenderer);
		 *
		 * JScrollPane scroll = new JScrollPane(); scroll.getViewport().add(panel);
		 */
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
			//System.out.println("wellPosition *********"+wellPosition);
			if (aPlateWell.getBatch() == null)
				continue;
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

	// vb 7/11
	protected void setStandardPopUpMenuItems(StaticPlateRenderer mStaticPlateRenderer) {
		List statusSelections = ProductBatchStatusMapper.getInstance().getSelections();
		mStaticPlateRenderer.createWellPropertiesPopupMenu();
		for (Iterator it = statusSelections.iterator(); it.hasNext();) {
			mStaticPlateRenderer.addWellPropertiesMenuItem((String) it.next());
		}
//		mStaticPlateRenderer.addWellPropertiesMenuItem(SET_AS_FAILED);
//		mStaticPlateRenderer.addWellPropertiesMenuItem(SET_AS_PASSED);
//		mStaticPlateRenderer.addWellPropertiesMenuItem(SET_AS_SUSPECT);
	}

//	public void actionPerformed(ActionEvent e) {
//		log.info(e.getSource());
//		/*
//		 * StaticPlateRenderer mStaticPlateRenderer =(StaticPlateRenderer)e.getSource(); String command = e.getActionCommand();
//		 * log.info("command is " + command); WellModel selectedWellModel = mStaticPlateRenderer .getRightMouseSeletedWell(); if
//		 * (selectedWellModel != null) {
//		 *
//		 * PlateWell mPlateWell = (PlateWell) selectedWellModel .getUserWellObject(); if (mPlateWell != null) { ProductBatch
//		 * mProductbatch = (ProductBatch) mPlateWell .getBatch(); log.info("*****" + mProductbatch.getCompound()); } } else{
//		 * log.info("selectedWellModel==null"); }
//		 */
//	}

//	public static void main(String[] arg) {
//		/*
//		 * TODO Jino ParallelCeNBatchInfo batchInfo = new ParallelCeNBatchInfo(3);
//		 *
//		 *
//		 * JFrame frame = new JFrame(); frame.setTitle("Product Batch Rack View Test"); Container pane = frame.getContentPane();
//		 *
//		 * ProductBatchRackViewPanel mProductBatchRackViewPanel = new ProductBatchRackViewPanel(batchInfo);
//		 *
//		 * pane.add(mProductBatchRackViewPanel.getTab(), BorderLayout.CENTER);
//		 * frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); frame.setSize(650, 200); frame.pack();
//		 * frame.setVisible(true);
//		 *
//		 */
//	}

	public JTabbedPane getTab() {
		return tab;
	}

	public List getMProductPlates() {
		return mProductPlates;
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
			this.pageModel.setModelChanged(true);
			// The singleton page model is set to uneditable.  Why?
			// Temporary fix...
			this.pageModel.setEditable(true);
		}
	}

}
