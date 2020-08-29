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
package com.chemistry.enotebook.client.gui.plateviewer;

import com.virtuan.plateVisualizer.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CeNPlateViewerGenerator {
	private static String WELL_FRAME_NAME = "CeN Plate Viewer";
	private static String CP_PROPERTY_NAME = "Sample";
	private static String CP_PROPERTY_VALUE = "S-";
	private static String CP_STRUCTURE_NAME = "Structure";
	private static String CP_STRUCTURE_VALUE = "CHIME STRING HERE";

	/**
	 * Creates a new demo instance.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public static JScrollPane buildCeNPlateViewer(String title, int col, int row) {
		IVisualPlateFactory plateFactory = PlateFactoryBuilder.createPlateFactory(PlateFactoryBuilder.FACTORY_TYPE_BASIC);
		String command = BasicVisualPlateFactory.PLATE_TYPE_BLUE_BOX;
		// String command = BasicVisualPlateFactory.PLATE_TYPE_WHITE_NUNC_PLATE;
		StaticPlateRenderer monomerA_Plate = buildCeNPlate(plateFactory.createPlateMood(command, "Rack A"), plateFactory
				.createFilledWellMood(command), plateFactory.createEmptyWellMood(command), title, col, row);
		StaticPlateRenderer monomerB_Plate = buildCeNPlate(plateFactory.createPlateMood(command, "Rack B"), plateFactory
				.createFilledWellMood(command), plateFactory.createEmptyWellMood(command), title, col, row);
		StaticPlateRenderer product_Plate = buildCeNPlate(plateFactory.createPlateMood(command, "Products Rack"), plateFactory
				.createFilledWellMood(command), plateFactory.createEmptyWellMood(command), title, col, row);
		JPanel panel = new JPanel();
		panel.add(monomerA_Plate);
		panel.add(monomerB_Plate);
		panel.add(product_Plate);
		JScrollPane scroll = new JScrollPane();
		scroll.getViewport().add(panel);
		return scroll;
	}

	/**
	 * method to add an empty first row
	 * 
	 * @param numColumns
	 * @param emptyWellMood
	 */
	private static void AddEmptyWellRow(int numColumns, WellMood emptyWellMood, PlateModel plateModel) {
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

	/**
	 * method to add an empty first row
	 * 
	 * @param numColumns
	 * @param emptyWellMood
	 */
	private static void AddSampleNumbers(int numColumns, int numRows, PlateModel plateModel) {
		WellModel well;
		int z = 1;
		try {
			for (int y = 2; y <= numRows; y++) {
				for (int x = 1; x <= numColumns; x++) {
					well = plateModel.getWellByCoordinate(x, y);
					well.addProperty(new WellProperty(CP_PROPERTY_NAME, CP_PROPERTY_VALUE + String.valueOf(z), true, 1));
					well.addProperty(new WellProperty(CP_STRUCTURE_NAME, CP_STRUCTURE_VALUE, true, 2));
					z++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * method to build a plate renderer
	 * 
	 * @param numRows
	 * @param numColumns
	 * @param plateMood
	 * @param filledWellMood
	 * @param emptyWellMood
	 * @param wellFrameName
	 * @return
	 */
	private static StaticPlateRenderer buildCeNPlate(PlateMood plateMood, WellMood filledWellMood, WellMood emptyWellMood,
			String wellFrameName, int col, int row) {
		// vb 7/11 is this used??????????????  Defaulting to row major
		InteractivePlate ip = new InteractivePlate(true, filledWellMood.getRimColor(), emptyWellMood.getRimColor(), Color.RED, row,
				col, plateMood, filledWellMood, new ArrayList(), true, wellFrameName, null, null, null, true);
		AddEmptyWellRow(col, emptyWellMood, ip.getPlateModel());
		AddSampleNumbers(col, row, ip.getPlateModel());
		// setStandardPopUpMenuItems(ip.getPlateRenderer());
		StaticPlateRenderer mStaticPlateRenderer = ip.getPlateRenderer();
		mStaticPlateRenderer.setPreferredSize(new Dimension(300, 300));
		return mStaticPlateRenderer;
	}
}
