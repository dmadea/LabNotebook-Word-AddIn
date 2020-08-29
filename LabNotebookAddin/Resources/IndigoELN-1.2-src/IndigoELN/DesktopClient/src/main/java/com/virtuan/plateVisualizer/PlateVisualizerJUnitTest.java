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

import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Title: PlateVisulaizerJUnitTest Description: A JUnit test for the com.virtuan.plateVisualizer package Note, system tests are used
 * to qualify visual functionality
 */
public class PlateVisualizerJUnitTest extends TestCase {
	/**
	 * Constructor for CEGInterfaceJUnitTest.
	 * 
	 * @param name
	 */
	public PlateVisualizerJUnitTest(String name) {
		super(name);
		testBasicVisualPlateFactory();
		testPlateModel();
		testPlateGeneralMethods();
	}

	/**
	 * Sets up the test fixture. Called before every test case method.
	 */
	public void testBasicVisualPlateFactory() {
		String title = "TEST";
		BasicVisualPlateFactory bFac = new BasicVisualPlateFactory();
		testPlateMoodCreation(bFac.createPlateMood(BasicVisualPlateFactory.PLATE_TYPE_BLACK_NUNC_PLATE, title));
		testPlateMoodCreation(bFac.createPlateMood(BasicVisualPlateFactory.PLATE_TYPE_BLUE_BOX, title));
		testPlateMoodCreation(bFac.createPlateMood(BasicVisualPlateFactory.PLATE_TYPE_CLEAR_NUNC_PLATE, title));
		testPlateMoodCreation(bFac.createPlateMood(BasicVisualPlateFactory.PLATE_TYPE_CLINPLATE_384, title));
		testPlateMoodCreation(bFac.createPlateMood(BasicVisualPlateFactory.PLATE_TYPE_EVOSCREEN_2080, title));
		testPlateMoodCreation(bFac.createPlateMood(BasicVisualPlateFactory.PLATE_TYPE_STANDARD_RACK, title));
		testPlateMoodCreation(bFac.createPlateMood(BasicVisualPlateFactory.PLATE_TYPE_WHITE_NUNC_PLATE, title));
		testWellMoodCreation(bFac.createEmptyWellMood(BasicVisualPlateFactory.PLATE_TYPE_BLACK_NUNC_PLATE));
		testWellMoodCreation(bFac.createEmptyWellMood(BasicVisualPlateFactory.PLATE_TYPE_BLUE_BOX));
		testWellMoodCreation(bFac.createEmptyWellMood(BasicVisualPlateFactory.PLATE_TYPE_CLEAR_NUNC_PLATE));
		testWellMoodCreation(bFac.createEmptyWellMood(BasicVisualPlateFactory.PLATE_TYPE_CLINPLATE_384));
		testWellMoodCreation(bFac.createEmptyWellMood(BasicVisualPlateFactory.PLATE_TYPE_EVOSCREEN_2080));
		testWellMoodCreation(bFac.createEmptyWellMood(BasicVisualPlateFactory.PLATE_TYPE_STANDARD_RACK));
		testWellMoodCreation(bFac.createEmptyWellMood(BasicVisualPlateFactory.PLATE_TYPE_WHITE_NUNC_PLATE));
		testWellMoodCreation(bFac.createFilledWellMood(BasicVisualPlateFactory.PLATE_TYPE_BLACK_NUNC_PLATE));
		testWellMoodCreation(bFac.createFilledWellMood(BasicVisualPlateFactory.PLATE_TYPE_BLUE_BOX));
		testWellMoodCreation(bFac.createFilledWellMood(BasicVisualPlateFactory.PLATE_TYPE_CLEAR_NUNC_PLATE));
		testWellMoodCreation(bFac.createFilledWellMood(BasicVisualPlateFactory.PLATE_TYPE_CLINPLATE_384));
		testWellMoodCreation(bFac.createFilledWellMood(BasicVisualPlateFactory.PLATE_TYPE_EVOSCREEN_2080));
		testWellMoodCreation(bFac.createFilledWellMood(BasicVisualPlateFactory.PLATE_TYPE_STANDARD_RACK));
		testWellMoodCreation(bFac.createFilledWellMood(BasicVisualPlateFactory.PLATE_TYPE_WHITE_NUNC_PLATE));
	}

	/**
	 * method to test plate mood creation
	 */
	private void testPlateMoodCreation(PlateMood plate1) {
		plate1 = BasicVisualPlateFactory.createBlueBoxPlateMood(8, 12, "Test");
		PlateMood plate2 = BasicVisualPlateFactory.createGeneralPlateMood(plate1.getNumberOfRows(), plate1.getNumberOfColumns(),
				plate1.getPlateTitle(), plate1.getRowLabel(), plate1.getColumnLabel(), plate1.getPlateColor(), plate1
						.getBorderColor(), plate1.getBorderStroke(), plate1.getWellLabelFont(), plate1.getXLabelOffsetFromCenter(),
				plate1.getYLabelOffsetFromCenter(), plate1.isAxesLabelsDisplayed(), plate1.getEmptyWellMood(), plate1
						.getFilledWellMood());
		assertEquals("PlateMood.getColumnLabel() failed", plate1.getColumnLabel(), plate2.getColumnLabel());
		assertEquals("PlateMood.getColumnLabel() title failed", plate1.getPlateTitle(), plate2.getPlateTitle());
		assertEquals("PlateMood.getRowLabel() failed", plate1.getRowLabel(), plate2.getRowLabel());
		assertEquals("PlateMood.getWellLabelFont() failed", plate1.getWellLabelFont(), plate2.getWellLabelFont());
		assertEquals("PlateMood.isAxesLabelsDisplayed() failed", plate1.isAxesLabelsDisplayed(), plate2.isAxesLabelsDisplayed());
		assertEquals("PlateMood.getBorderColor() failed", plate1.getBorderColor(), plate2.getBorderColor());
		assertEquals("PlateMood.getBorderStroke()", plate1.getBorderStroke(), plate2.getBorderStroke());
		assertEquals("PlateMood.getEmptyWellMood() failed", plate1.getEmptyWellMood(), plate2.getEmptyWellMood());
		assertEquals("PlateMood.getFilledWellMood() failed", plate1.getFilledWellMood(), plate2.getFilledWellMood());
		assertEquals("PlateMood.getNumberOfRows() failed", plate1.getNumberOfRows(), plate2.getNumberOfRows());
		assertEquals("PlateMood.getNumberOfColumns() failed", plate1.getNumberOfColumns(), plate2.getNumberOfColumns());
		assertEquals("PlateMood.getPlateColor() failed", plate1.getPlateColor(), plate2.getPlateColor());
		assertEquals("PlateMood.getPlateOrientation() failed", plate1.getPlateOrientation(), plate2.getPlateOrientation());
		assertEquals("PlateMood.getWellScalingFactor() failed", plate1.getWellScalingFactor(), plate2.getWellScalingFactor());
		assertEquals("PlateMood.getXLabelOffsetFromCenter() failed", plate1.getXLabelOffsetFromCenter(), plate2
				.getXLabelOffsetFromCenter());
		assertEquals("PlateMood.getYLabelOffsetFromCenter() failed", plate1.getYLabelOffsetFromCenter(), plate2
				.getYLabelOffsetFromCenter());
	}

	/**
	 * method to test the well mood creation
	 * 
	 * @param well1
	 */
	public void testWellMoodCreation(WellMood well1) {
		WellMood well2 = BasicVisualPlateFactory.createGeneralWellMood(well1.getName(), well1.getRimWidth(), well1.getRimColor(),
				well1.getStartWellColor(), well1.getEndWellColor(), well1.getDiameterOffset(), well1.getWellShape(), well1
						.getRimOffset(), well1.getRimDiameterIncrease());
		assertEquals("WellMood.getDiameterOffset() failed", well1.getDiameterOffset(), well2.getDiameterOffset(), well2
				.getDiameterOffset() / 10);
		assertEquals("WellMood.getName() failed", well1.getName(), well2.getName());
		assertEquals("WellMood.getDiameterOffset() failed", well1.getEndWellColor(), well2.getEndWellColor());
		assertEquals("WellMood.getRimColor() failed", well1.getRimColor(), well2.getRimColor());
		assertEquals("WellMood.getRimDiameterIncrease() failed", well1.getRimDiameterIncrease(), well2.getRimDiameterIncrease());
		assertEquals("WellMood.getRimOffset() failed", well1.getRimOffset(), well2.getRimOffset());
		assertEquals("WellMood.getRimWidth() failed", well1.getRimWidth(), well2.getRimWidth());
		assertEquals("WellMood.getStartWellColor() failed", well1.getStartWellColor(), well2.getStartWellColor());
		assertEquals("WellMood.getWellShape() failed", well1.getWellShape(), well2.getWellShape());
	}

	/**
	 * method to test the plate model, well model
	 */
	public void testPlateModel() {
		int testRows = 8;
		int testColumns = 12;
		String name = "TestName1";
		String name2 = "TestName2";
		String value = "TestValue1";
		WellProperty wp1 = new WellProperty(name, value, true, 1);
		testWellProperty(wp1);
		WellProperty wp2 = new WellProperty(name2, value, false, 2);
		testWellProperty(wp2);
		WellMood wm = BasicVisualPlateFactory.createBlackNuncEmptyWellMood();
		ArrayList ar = new ArrayList();
		ar.add(wp1);
		ar.add(wp2);
		PlateModel pm = new PlateModel(testRows, testColumns, wm, ar, true); // vb 7/11
		assertEquals("PlateModel.getNumberOfColumn() failed", pm.getNumberOfColumns(), testColumns);
		assertEquals("PlateModel.getNumberOfRows() failed", pm.getNumberOfRows(), testRows);
		assertEquals("PlateModel.getItemCount() failed", pm.getItemCount(0), testRows * testColumns);
		WellModel wm1 = pm.getWellByCoordinate(1, 1);
		WellModel wm2 = pm.getWellByCoordinate(testColumns, testRows);
		WellModel wm3 = pm.getWellBySetIndex(0);
		WellModel wm4 = pm.getWellBySetIndex(1);
		assertEquals("PlateModel.getWellByCoordinate", wm1.getInalienableProperty(0).getValue(), "A1");
		assertEquals("PlateModel.getWellByCoordinate", wm2.getInalienableProperty(0).getValue(), "H12");
		assertEquals("PlateModel.getWellBySetIndex", wm3.getInalienableProperty(0).getValue(), "H1");
		assertEquals("PlateModel.getWellBySetIndex", wm4.getInalienableProperty(0).getValue(), "G1");
		assertEquals("WellModel.getNumberOfInalienAbleProperties", wm1.getNumberOfInalienableProperties(), 1);
		assertEquals("WellModel.getNumberOfProperties", wm1.getNumberOfProperties(), 2);
	}

	/**
	 * method to test the well property
	 * 
	 * @param wp
	 */
	public void testWellProperty(WellProperty wp) {
		try {
			WellProperty wp2 = (WellProperty) wp.clone();
			assertEquals("WellProperty.getName() failed", wp.getName(), wp2.getName());
			assertEquals("WellProperty.getValue() failed", wp.getValue(), wp2.getValue());
			assertEquals("WellProperty.getIsDisplayed() failed", wp.getIsDisplayed(), wp2.getIsDisplayed());
			assertEquals("WellProperty.getOrder() failed", wp.getOrder(), wp2.getOrder());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testPlateGeneralMethods() {
		assertEquals("PlateGeneralMethods.convertLetterStringToANumber() failed", PlateGeneralMethods
				.convertLetterStringToANumber("A"), 1);
		assertEquals("PlateGeneralMethods.convertLetterStringToANumber() failed", PlateGeneralMethods
				.convertLetterStringToANumber("AA"), 27);
		assertEquals("PlateGeneralMethods.convertNumericStringToALetter() failed", PlateGeneralMethods
				.convertNumericStringToALetter("1"), "A");
		assertEquals("PlateGeneralMethods.convertNumericStringToALetter() failed", PlateGeneralMethods
				.convertNumericStringToALetter("27"), "AA");
		assertEquals("PlateGeneralMethods.convertWellPositionToNumericValue() failed", PlateGeneralMethods
				.convertWellPositionToNumericValue("A1", "X"), 1);
		assertEquals("PlateGeneralMethods.convertWellPositionToNumericValue() failed", PlateGeneralMethods
				.convertWellPositionToNumericValue("A1", "Y"), 1);
		assertEquals("PlateGeneralMethods.convertWellPositionToNumericValue() failed", PlateGeneralMethods
				.convertWellPositionToNumericValue("H3", "X"), 3);
		assertEquals("PlateGeneralMethods.convertWellPositionToNumericValue() failed", PlateGeneralMethods
				.convertWellPositionToNumericValue("H3", "Y"), 8);
		assertEquals("PlateGeneralMethods.convertPositionForItem() failed", PlateGeneralMethods.getPositionForItem(0, 8), "H1");
		assertEquals("PlateGeneralMethods.convertPositionForItem() failed", PlateGeneralMethods.getPositionForItem(10, 8), "F2");
		assertEquals("PlateGeneralMethods.getXorYStringValue() failed", PlateGeneralMethods.getXorYStringValue("A1", "X"), "1");
		assertEquals("PlateGeneralMethods.getXorYStringValue() failed", PlateGeneralMethods.getXorYStringValue("AA10", "X"), "10");
		assertEquals("PlateGeneralMethods.getXorYStringValue() failed", PlateGeneralMethods.getXorYStringValue("A1", "Y"), "A");
		assertEquals("PlateGeneralMethods.getXorYStringValue() failed", PlateGeneralMethods.getXorYStringValue("AA10", "Y"), "AA");
	}

	/**
	 * main for the test
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(PlateVisualizerJUnitTest.class);
	}
}
