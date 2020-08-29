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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Title: StandardPlateVarieties_DEMO Description: This class is the Demo class for the standard plate varieties
 */
public class StandardPlateVarieties_DEMO extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 904558500838817241L;
	private static String WELL_FRAME_NAME = "WellPeak";
	private static String CP_PROPERTY_NAME = "Sample";
	private static String CP_PROPERTY_VALUE = "S-";
	private static String CP_STRUCTURE_NAME = "Structure";
	private static String CP_STRUCTURE_VALUE = "CHIME STRING HERE";
	private IVisualPlateFactory _plateFactory;

	/**
	 * Creates a new demo instance.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public StandardPlateVarieties_DEMO(String title) {
		super(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(750, 500);
		_plateFactory = PlateFactoryBuilder.createPlateFactory(PlateFactoryBuilder.FACTORY_TYPE_BASIC);
		ActionEvent ae = new ActionEvent(this, 0, BasicVisualPlateFactory.PLATE_TYPE_BLUE_BOX);
		actionPerformed(ae);
	}

	/**
	 * method to create the pop up menu to select the various standard plate types
	 * 
	 * @param panel
	 */
	protected void setStandardPopUpMenuItems(StaticPlateRenderer panel) {
		panel.getPopupMenu().removeAll();
		JMenuItem blueBox = new JMenuItem(BasicVisualPlateFactory.PLATE_TYPE_BLUE_BOX);
		blueBox.addActionListener(this);
		JMenuItem blackNunc = new JMenuItem(BasicVisualPlateFactory.PLATE_TYPE_BLACK_NUNC_PLATE);
		blackNunc.addActionListener(this);
		JMenuItem whiteNunc = new JMenuItem(BasicVisualPlateFactory.PLATE_TYPE_WHITE_NUNC_PLATE);
		whiteNunc.addActionListener(this);
		JMenuItem clearNunc = new JMenuItem(BasicVisualPlateFactory.PLATE_TYPE_CLEAR_NUNC_PLATE);
		clearNunc.addActionListener(this);
		JMenuItem evoscreen2080 = new JMenuItem(BasicVisualPlateFactory.PLATE_TYPE_EVOSCREEN_2080);
		evoscreen2080.addActionListener(this);
		JMenuItem standardRack = new JMenuItem(BasicVisualPlateFactory.PLATE_TYPE_STANDARD_RACK);
		standardRack.addActionListener(this);
		JMenuItem standard384 = new JMenuItem(BasicVisualPlateFactory.PLATE_TYPE_CLINPLATE_384);
		standard384.addActionListener(this);
		JMenuItem standard1536 = new JMenuItem(BasicVisualPlateFactory.PLATE_TYPE_CLINPLATE_1536);
		standard1536.addActionListener(this);
		panel.getPopupMenu().add(blueBox);
		panel.getPopupMenu().add(blackNunc);
		panel.getPopupMenu().add(whiteNunc);
		panel.getPopupMenu().add(clearNunc);
		panel.getPopupMenu().add(evoscreen2080);
		panel.getPopupMenu().add(standardRack);
		panel.getPopupMenu().add(standard384);
		panel.getPopupMenu().add(standard1536);
	}

	/**
	 * method to show the contents of the application frame
	 */
	private void setContent() {
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
		this.setVisible(true);
	}

	/**
	 * method to add an empty first row
	 * 
	 * @param numColumns
	 * @param emptyWellMood
	 */
	private void AddEmptyWellRow(int numColumns, WellMood emptyWellMood, PlateModel plateModel) {
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
	private void AddSampleNumbers(int numColumns, int numRows, PlateModel plateModel) {
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
	private StaticPlateRenderer buildTestPlate(PlateMood plateMood, WellMood filledWellMood, WellMood emptyWellMood,
			String wellFrameName) {
		/*
		 * InteractivePlate ip = new InteractivePlate(true, filledWellMood.getRimColor() ,emptyWellMood.getRimColor(), Color.RED,
		 * plateMood.getNumberOfRows() ,plateMood.getNumberOfColumns(),plateMood, filledWellMood, new ArrayList() ,true,
		 * wellFrameName, this);
		 * 
		 * 
		 * 
		 * AddEmptyWellRow(plateMood.getNumberOfColumns(), emptyWellMood, ip.getPlateModel());
		 * AddSampleNumbers(plateMood.getNumberOfColumns(), plateMood.getNumberOfRows(), ip.getPlateModel());
		 */
		InteractivePlate ip = new InteractivePlate(true, filledWellMood.getRimColor(), emptyWellMood.getRimColor(), Color.RED, 5,
				8, plateMood, filledWellMood, new ArrayList(), true, wellFrameName, this, null, null, true); // vb 7/11
		AddEmptyWellRow(8, emptyWellMood, ip.getPlateModel());
		AddSampleNumbers(8, 5, ip.getPlateModel());
		setStandardPopUpMenuItems(ip.getPlateRenderer());
		return ip.getPlateRenderer();
	}

	/**
	 * list Selection event receiver.
	 * 
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		this.setContentPane(buildTestPlate(_plateFactory.createPlateMood(command, command), _plateFactory
				.createFilledWellMood(command), _plateFactory.createEmptyWellMood(command), WELL_FRAME_NAME));
		this.setVisible(true);
	}
}
