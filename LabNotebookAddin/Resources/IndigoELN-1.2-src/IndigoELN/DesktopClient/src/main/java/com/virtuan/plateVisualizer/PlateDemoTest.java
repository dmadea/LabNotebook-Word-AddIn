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

/**
 * Title: PlateDemoTest Description: Demo for testing the general functionality of the package
 */
public class PlateDemoTest {
	/**
	 * main method to perform test and demos pass in "Plate Options" at args[0] to run the plate types demo all other args[0]
	 * strings will run the active blue box demo
	 * 
	 * @param args
	 *            args[0] must have an entry
	 */
	public static void main(String[] args) {
		// String s = args[0];
		// if(s.equals("Plate Options"))
		demoActionablePlates();
		// else
		// demoStaticPlate();
	}

	/**
	 * method to demo an action-rich plate visualization for various plate types This demo creates a display panel for several types
	 * of plates that can be created directly from the GUIPlateFactory. Plate types can be selected from a pop up menu (right mouse
	 * click on the panel) "Empty" wells populate the A row at the start. Filled wells are resident at all other locations at the
	 * start
	 * 
	 * 1. Mouse click on/off to select individual wells 2. Click and Drag on the non-well space to create a selection area, mouse
	 * release selects the wells fully contained within the selection area. The drag box works from top left to bottom right only 3.
	 * Right mouse click on a tube and drag to an empty well to change the tube (or well) position. A tube move to the same well, or
	 * a well occupied by another tube, or a non well space will not cause an action 4. Right mouse click DESELECT ALL will unselect
	 * all selected wells 5. A mouse click in non-well space will deselect all wells 6. A new well(s) selection will deselect all
	 * previously selected wells
	 * 
	 */
	public static void demoActionablePlates() {
		StandardPlateVarieties_DEMO demo = new StandardPlateVarieties_DEMO("Plate Visualizer Test");
	}

	/**
	 * a simple demo of a static plate with mouse over well properties viewing
	 */
	public static void demoStaticPlate() {
		StaticPlate_DEMO demo = new StaticPlate_DEMO("Static Plate Demo");
	}
}
