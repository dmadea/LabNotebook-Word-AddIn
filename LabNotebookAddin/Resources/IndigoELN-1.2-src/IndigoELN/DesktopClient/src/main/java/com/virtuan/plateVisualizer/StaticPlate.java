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
package com.virtuan.plateVisualizer;

import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionListener;
import com.chemistry.enotebook.client.gui.page.experiment.plate.WellPropertiesChangeListener;

import java.awt.*;
import java.util.ArrayList;

/**
 * Title: StaticPlate Description: This class provides a ready made interactive plate with well selection and move capabilities
 */
public class StaticPlate {
	private StaticPlateRenderer _plateRenderer;
	protected PlateModel _plateModel;

	/**
	 * Creates a new demo instance.
	 * 
	 * @param pr
	 *            Plate renderer
	 * @param swapContentsOnMove
	 *            true to swap mutable well properties and moods. false will move the properties from the start well to the end well
	 *            overwriting the end well and leaving the start well empty
	 */
	public StaticPlate(int numRows, 
				       int numColumns, 
				       PlateMood plateMood, 
				       WellMood initialWellMood, 
				       ArrayList initialWellProperties,
				       boolean displayWellProperties, 
				       String propertiesFrameTitle, 
				       Container plateFrame, 
				       BatchSelectionListener[] batchSelectionListeners,
				       WellPropertiesChangeListener[] wellPropertiesChangeListeners,  // vb 7/11
				       boolean isRowMajor) { // vb 7/11
		_plateModel = new PlateModel(numRows, numColumns, initialWellMood, initialWellProperties, isRowMajor);
		_plateRenderer = new StaticPlateRenderer(numRows, numColumns, plateMood, initialWellMood, new ArrayList(),
				displayWellProperties, propertiesFrameTitle, _plateModel, batchSelectionListeners, wellPropertiesChangeListeners);  // vb 6/21
		_plateRenderer.setPlateApplicationFrame(plateFrame);
	}

	/**
	 * method to return the plate renderer
	 * 
	 * @return PlateRenderer
	 */
	public StaticPlateRenderer getPlateRenderer() {
		return _plateRenderer;
	}

	/**
	 * method to return the plate model
	 * 
	 * @return PlateRenderer
	 */
	public PlateModel getPlateModel() {
		return _plateModel;
	}
}
