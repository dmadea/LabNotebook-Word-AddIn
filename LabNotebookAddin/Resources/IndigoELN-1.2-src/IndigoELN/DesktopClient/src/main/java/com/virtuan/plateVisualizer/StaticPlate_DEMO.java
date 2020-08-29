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

import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Title: StandardPlateVarieties_DEMO Description: This class is the Demo class for the standard plate varieties
 */
public class StaticPlate_DEMO extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5906803154013011609L;
	private static String WELL_FRAME_NAME = "WellPeak";
	private static String WELL_TYPE_NAME = "Well Type";
	private static String WELL_TYPE_VALUE_HPE = "HPE";
	private static String WELL_TYPE_VALUE_ZPE = "ZPE";
	private static String WELL_TYPE_VALUE_SAMPLE = "SAMPLE";
	private static String TITLE = "Static Example";
	private static int PLATE_ROWS_384 = 16;
	private static int PLATE_COLUMNS_384 = 24;
	private IVisualPlateFactory _plateFactory;

	/**
	 * Creates a new demo instance.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public StaticPlate_DEMO(String title) {
		super(title);
		this.setSize(750, 500);
		_plateFactory = PlateFactoryBuilder.createPlateFactory(PlateFactoryBuilder.FACTORY_TYPE_BASIC);
		buildStaticPlate();
	}

	/**
	 * method to show the contents of the application frame
	 */
	private void setContent() {
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}

	/**
	 * method to add an empty first row
	 * 
	 * @param numColumns
	 * @param emptyWellMood
	 */
	private void AddControlWells(PlateModel plateModel) {
		WellModel well;
		for (int x = 1; x <= plateModel.getNumberOfColumns(); x++) {
			well = plateModel.getWellByCoordinate(x, 1);
			well.addProperty(new WellProperty(WELL_TYPE_NAME, WELL_TYPE_VALUE_HPE, true, 1));
			well.getWellMood().setStartWellColor(Color.RED);
			well = plateModel.getWellByCoordinate(x, plateModel.getNumberOfRows());
			well.addProperty(new WellProperty(WELL_TYPE_NAME, WELL_TYPE_VALUE_ZPE, true, 1));
			well.getWellMood().setStartWellColor(Color.BLUE);
		}
	}

	/**
	 * method to add an empty first row
	 * 
	 * @param numColumns
	 * @param emptyWellMood
	 */
	private void AddSampleWells(PlateModel plateModel) {
		WellModel well;
		try {
			for (int y = 2; y < plateModel.getNumberOfRows(); y++) {
				for (int x = 1; x <= plateModel.getNumberOfColumns(); x++) {
					well = plateModel.getWellByCoordinate(x, y);
					well.addProperty(new WellProperty(WELL_TYPE_NAME, WELL_TYPE_VALUE_SAMPLE, true, 1));
					well.getWellMood().setStartWellColor(Color.YELLOW);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * method to build a plate renderer
	 */
	private StaticPlateRenderer buildStaticPlate() {
		StaticPlate sp = new StaticPlate(PLATE_ROWS_384, PLATE_COLUMNS_384, _plateFactory.createPlateMood(
				BasicVisualPlateFactory.PLATE_TYPE_CLINPLATE_384, TITLE), _plateFactory
				.createFilledWellMood(BasicVisualPlateFactory.PLATE_TYPE_CLINPLATE_384), new ArrayList(), true, WELL_FRAME_NAME,
				this, null, null, true);  // vb 7/11
		AddControlWells(sp.getPlateModel());
		AddSampleWells(sp.getPlateModel());
		this.setContentPane(sp.getPlateRenderer());
		setContent();
		return sp.getPlateRenderer();
	}
}
