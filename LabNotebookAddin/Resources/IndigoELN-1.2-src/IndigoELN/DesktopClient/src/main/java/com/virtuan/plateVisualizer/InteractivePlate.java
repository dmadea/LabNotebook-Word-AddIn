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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Title: InteractivePlate Description: This class provides a ready made interactive plate with well selection and move capabilities
 */
public class InteractivePlate extends StaticPlate implements IWellSelectionListener {
	public static String PROPERTY_SELECTED_NAME = "Well Selected";
	public static String PROPERTY_SELECTED_VALUE = "Selected";
	public static String PROPERTY_DESELECTED_VALUE = "Unselected";
	public static String PROPERTY_FILLED_NAME = "Well Filled";
	public static String PROPERTY_FILLED_VALUE = "Filled";
	public static String PROPERTY_EMPTY_VALUE = "Empty";
	private static String WELL_FRAME_NAME = "WellPeak";
	private Color _emptyDeselectedColor;
	private Color _filledDeselectedColor;
	private Color _selectedColor;
	private HashMap _selectedWells;
	private WellModel _singleMoveTargetWell;
	private WellModel _singleMoveEndWell;
	private InteractivePlateRenderer _iPlateRenderer;
	private boolean _performSwap;

	/**
	 * Creates a new demo instance.
	 * 
	 * @param pr
	 *            Plate renderer
	 * @param swapContentsOnMove
	 *            true to swap mutable well properties and moods. false will move the properties from the start well to the end well
	 *            overwriting the end well and leaving the start well empty
	 */
	public InteractivePlate(boolean swapContentsOnMove, 
				            Color filledDeselectedColor, 
				            Color emptyDeselectedColor,
				            Color selectedColor, 
				            int numRows, 
				            int numColumns, 
				            PlateMood plateMood, 
				            WellMood filledWellMood,
				            ArrayList initialWellProperties, 
				            boolean displayWellProperties, 
				            String propertiesFrameTitle, 
				            Container plateFrame,
				            BatchSelectionListener[] batchSelectionListeners,
				            WellPropertiesChangeListener[] wellPropertiesChangeListeners, // vb 7/11
				            boolean isRowMajor) {
		super(numRows, numColumns, plateMood, filledWellMood, initialWellProperties, displayWellProperties, propertiesFrameTitle,
				plateFrame, batchSelectionListeners, wellPropertiesChangeListeners, isRowMajor); // vb 7/11
		_iPlateRenderer = new InteractivePlateRenderer(numRows, numColumns, plateMood, filledWellMood, new ArrayList(), true,
				propertiesFrameTitle, _plateModel, batchSelectionListeners, wellPropertiesChangeListeners);  
		_iPlateRenderer.setPlateApplicationFrame(plateFrame);
		_selectedWells = new HashMap();
		_iPlateRenderer.addWellSelectionListener(this);
		_filledDeselectedColor = filledDeselectedColor;
		_emptyDeselectedColor = emptyDeselectedColor;
		_selectedColor = selectedColor;
		addSelectedAndFilledProperty();
		_performSwap = swapContentsOnMove;
	}

	/**
	 * method to return the plate renderer
	 * 
	 * @return PlateRenderer
	 */
	public InteractivePlateRenderer getInteractivePlateRenderer() {
		return _iPlateRenderer;
	}

	/**
	 * method to return the plate renderer. override of superclass to return the interactive plate renderer
	 * 
	 * @return PlateRenderer
	 */
	public StaticPlateRenderer getPlateRenderer() {
		return _iPlateRenderer;
	}

	/**
	 * method to return the plate model
	 * 
	 * @return PlateRenderer
	 */
	public PlateModel getPlateModel() {
		return _plateModel;
	}

	/**
	 * method to add the selected and filled well properties to each well of the plate
	 */
	private void addSelectedAndFilledProperty() {
		WellModel well;
		try {
			WellProperty selectedProp = new WellProperty(PROPERTY_SELECTED_NAME, PROPERTY_DESELECTED_VALUE, false, 100);
			WellProperty filledProp = new WellProperty(PROPERTY_FILLED_NAME, PROPERTY_FILLED_VALUE, false, 101);
		   
			/* to fix bug with wrong well positions in plate renderer
			 * KS 12.03.09
			*/ 
			for (int currentRow = 1; currentRow <= _iPlateRenderer.getNumberOfRows(); currentRow ++){
				for (int currentColumn = 1; currentColumn <= _iPlateRenderer.getNumberOfColumns(); currentColumn ++){
					well = (WellModel) _plateModel.getWellByCoordinate(currentColumn, currentRow);
					well.addProperty((WellProperty) selectedProp.clone());
					well.addProperty((WellProperty) filledProp.clone());
				}
			}
			
			/*	for (int i = 0; i < _iPlateRenderer.getNumberOfColumns() * _iPlateRenderer.getNumberOfRows(); i++) {
				well = (WellModel) _plateModel.getWellBySetIndex(x);
				well.addProperty((WellProperty) selectedProp.clone());
				well.addProperty((WellProperty) filledProp.clone());
			}*/
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new PlateVisualizationError(e.getMessage());
		}
	}

	/**
	 * method to deselect all selected wells
	 */
	public void deselectAllWells() {
		WellModel well;
		Object[] ar = _selectedWells.keySet().toArray();
		for (int x = 0; x < ar.length; x++) {
			well = (WellModel) ar[x];
			deselectWell(well);
		}
	}

	/**
	 * method to select a single well select
	 * 
	 * @param well
	 */
	private void selectWell(WellModel well) {
		if (!_selectedWells.containsKey(well)) {
			well.getWellMood().setRimColor(_selectedColor);
			well.getProperty(PROPERTY_SELECTED_NAME).setValue(PROPERTY_SELECTED_VALUE);
			_selectedWells.put(well, well);
		}
	}

	/**
	 * method to deselect a well
	 * 
	 * @param well
	 */
	private void deselectWell(WellModel well) {
		if (_selectedWells.containsKey(well)) {
			well.getProperty(PROPERTY_SELECTED_NAME).setValue(PROPERTY_DESELECTED_VALUE);
			if (well.getProperty(PROPERTY_FILLED_NAME).getValue().equals(PROPERTY_FILLED_VALUE))
				well.getWellMood().setRimColor(_filledDeselectedColor);
			else
				well.getWellMood().setRimColor(_emptyDeselectedColor);
			_selectedWells.remove(well);
		}
	}

	/**
	 * method to alert
	 * 
	 * @param well
	 */
	public void selectSingleWell(WellModel well) {
		boolean selected = false;
		if (isWellSelected(well))
			selected = true;
		deselectAllWells();
		if (!selected)
			selectWell(well);
	}

	/**
	 * method to determine whether a well is selected
	 * 
	 * @param well
	 * @return boolean true if the well is selected, false if not
	 */
	public boolean isWellSelected(WellModel well) {
		return _selectedWells.containsKey(well);
	}

	/**
	 * method to alert a multiple well seletion
	 * 
	 * @param wells
	 */
	public void selectMultipleWells(Collection wells) {
		WellModel well;
		deselectAllWells();
		for (Iterator i = wells.iterator(); i.hasNext();) {
			well = (WellModel) i.next();
			selectWell(well);
		}
	}

	/**
	 * method to perform a single well move This method will swap the well moods as well as the well mutable properties
	 * 
	 * @param endWell
	 */
	private void performSingleWellContentsSwap(WellModel endWell) {
		ArrayList ar = null;
		WellMood mood = null;
		if (null != endWell && null != _singleMoveTargetWell
				&& _singleMoveTargetWell.getProperty(PROPERTY_FILLED_NAME).getValue().equals(PROPERTY_FILLED_VALUE)
				&& endWell.getProperty(PROPERTY_FILLED_NAME).getValue().equals(PROPERTY_EMPTY_VALUE)) {
			ar = new ArrayList();
			mood = _singleMoveTargetWell.getWellMood();
			for (int x = 0; x < _singleMoveTargetWell.getNumberOfProperties(); x++) {
				ar.add(_singleMoveTargetWell.getProperty(x));
			}
			_singleMoveTargetWell.removeAllProperties();
			_singleMoveTargetWell.setWellMood(endWell.getWellMood());
			for (int y = 0; y < endWell.getNumberOfProperties(); y++) {
				_singleMoveTargetWell.addProperty(endWell.getProperty(y));
			}
			endWell.removeAllProperties();
			endWell.setWellMood(mood);
			for (int z = 0; z < ar.size(); z++) {
				endWell.addProperty((WellProperty) ar.get(z));
			}
			deselectWell(_singleMoveTargetWell);
			selectWell(endWell);
		}
	}

	/**
	 * method to perform a single well move This method will not update the well mooods, it will only move the mutable properties
	 * from the start to the end well. The start well will be empty
	 * 
	 * @param endWell
	 */
	private void performSingleWellContentsMove(WellModel endWell) {
		ArrayList ar = null;
		WellMood mood = null;
		if (null != endWell && null != _singleMoveTargetWell) {
			ar = new ArrayList();
			mood = _singleMoveTargetWell.getWellMood();
			for (int x = 0; x < _singleMoveTargetWell.getNumberOfProperties(); x++) {
				ar.add(_singleMoveTargetWell.getProperty(x));
			}
			_singleMoveTargetWell.removeAllProperties();
			for (int y = 0; y < endWell.getNumberOfProperties(); y++) {
				_singleMoveTargetWell.addProperty(endWell.getProperty(y));
			}
			endWell.removeAllProperties();
			for (int z = 0; z < ar.size(); z++) {
				endWell.addProperty((WellProperty) ar.get(z));
			}
			deselectWell(_singleMoveTargetWell);
			selectWell(endWell);
		}
	}

	/**
	 * method to note the beginning of a single well move
	 * 
	 * @param well;
	 */
	public void alertBeginSingleWellMove(WellModel well) {
		_singleMoveTargetWell = well;
	}

	/**
	 * method to note the completion of a single well mood
	 * 
	 * @param well;
	 */
	public void alertEndSingleWellMove(WellModel well) {
		if (_performSwap)
			performSingleWellContentsSwap(well);
		else
			performSingleWellContentsMove(well);
	}
}
