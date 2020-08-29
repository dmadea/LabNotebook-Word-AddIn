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

import com.virtuan.plateVisualizer.IVisualPlateFactory;
import com.virtuan.plateVisualizer.PlateMood;
import com.virtuan.plateVisualizer.WellMood;
import org.jfree.chart.ChartColor;

import java.awt.*;

public class CeNVisualPlateFactory implements IVisualPlateFactory {
	private static String DEFAULT_COLUMN_LABEL = "COLUMNS";
	private static String DEFAULT_ROW_LABEL = "ROWS";
	public static int CEN_SCALING_FACTOR = -1;
	public static int STANDARD_SCALING_FACTOR_96_WELL_PLATE = 6;
	public static int STANDARD_SCALING_FACTOR_384_WELL_PLATE = 6;
	public static int STANDARD_SCALING_FACTOR_2080_WELL_PLATE = 2;
	public static String PLATE_TYPE_BLUE_BOX = "96 Well Blue Box";
	public static String CEN_PLATE = "CEN_MONOMER_PLATE";
	public static String CEN_RACK = "CEN_MONOMER_RACK";
	public static String PLATE_TYPE_BLACK_NUNC_PLATE = "384 Well Black Nunc Plate";
	public static String PLATE_TYPE_WHITE_NUNC_PLATE = "384 Well White Nunc Plate";
	public static String PLATE_TYPE_CLEAR_NUNC_PLATE = "384 Well Clear Nunc Plate";
	public static String PLATE_TYPE_EVOSCREEN_2080 = "Evoscreen 2080 Plate";
	public static String PLATE_TYPE_STANDARD_RACK = "96 Well Standard Rack";
	public static String PLATE_TYPE_CLINPLATE_384 = "384 Well CLINPLATE";
	public static String PLATE_TYPE_CLINPLATE_1536 = "1536 Well CLINPLATE";
	private static int WELL_ROWS_96 = 8;
	private static int WELL_COLUMNS_96 = 12;
	private static int WELL_ROWS_384 = 16;
	private static int WELL_COLUMNS_384 = 24;
	private static int WELL_ROWS_1536 = 32;
	private static int WELL_COLUMNS_1536 = 48;
	private static int WELL_ROWS_2080 = 40;
	private static int WELL_COLUMNS_2080 = 52;

	/**
	 * method to return a general GUIPlateMood
	 * 
	 * @return GUIPlateMood for the blue box
	 */
	public static PlateMood createGeneralPlateMood(int numberOfRows, int numberOfColumns, String title, String rowLabel,
			String columnLabel, Color chartColor, Color borderColor, Stroke borderStroke, String plateLabelFont,
			int xLabelOffsetFromCenter, int yLabelOffsetFromCenter, boolean isAxesDisplayed, WellMood emptyWellMood,
			WellMood filledWellMood) {
		return new PlateMood(chartColor, title, rowLabel, columnLabel, numberOfColumns, numberOfRows, borderColor, borderStroke,
				plateLabelFont, xLabelOffsetFromCenter, yLabelOffsetFromCenter, isAxesDisplayed,
				STANDARD_SCALING_FACTOR_96_WELL_PLATE, emptyWellMood, filledWellMood);
	}

	/**
	 * method to create plate moods
	 * 
	 * @param moodType
	 * @param numberOfRows
	 * @param numberOfColumns
	 * @param String
	 *            title
	 */
	public PlateMood createPlateMood(String moodType, String title) {
		if (moodType.equals(CEN_PLATE))
			return createCeNPlatePlateMood(WELL_ROWS_96, WELL_COLUMNS_96, title);
		else if (moodType.equals(CEN_RACK))
			return createCeNRackMood(WELL_ROWS_96, WELL_COLUMNS_96, title);
		else if (moodType.equals(PLATE_TYPE_BLUE_BOX))
			return createBlueBoxPlateMood(WELL_ROWS_96, WELL_COLUMNS_96, title);
		else if (moodType.equals(PLATE_TYPE_BLACK_NUNC_PLATE))
			return createBlackNuncPlateMood(WELL_ROWS_384, WELL_COLUMNS_384, title);
		else if (moodType.equals(PLATE_TYPE_WHITE_NUNC_PLATE))
			return createWhiteNuncPlateMood(WELL_ROWS_384, WELL_COLUMNS_384, title);
		else if (moodType.equals(PLATE_TYPE_CLEAR_NUNC_PLATE))
			return createClearNuncPlateMood(WELL_ROWS_384, WELL_COLUMNS_384, title);
		else if (moodType.equals(PLATE_TYPE_EVOSCREEN_2080))
			return createEvotec2080PlateMood(WELL_ROWS_2080, WELL_COLUMNS_2080, title);
		else if (moodType.equals(PLATE_TYPE_STANDARD_RACK))
			return createStandardRackMood(WELL_ROWS_96, WELL_COLUMNS_96, title);
		else if (moodType.equals(PLATE_TYPE_CLINPLATE_384))
			return createStd384WellPlateMood(WELL_ROWS_384, WELL_COLUMNS_384, title);
		else if (moodType.equals(PLATE_TYPE_CLINPLATE_1536))
			return createStd384WellPlateMood(WELL_ROWS_1536, WELL_COLUMNS_1536, title);
		else
			return null;
	}

	/**
	 * method to create an filled well mood
	 * 
	 * @param type
	 * @return WellMood
	 */
	public WellMood createFilledWellMood(String moodType) {
		if (moodType.equals(CEN_PLATE))
			return createCeNPlateFilledWellMood();
		else if (moodType.equals(CEN_RACK))
			return createCeNRackFilledWellMood();
		else if (moodType.equals(PLATE_TYPE_BLUE_BOX))
			return createBlueBoxFilledWellMood();
		else if (moodType.equals(PLATE_TYPE_BLACK_NUNC_PLATE))
			return createBlackNuncFilledWellMood();
		else if (moodType.equals(PLATE_TYPE_WHITE_NUNC_PLATE))
			return createWhiteNuncFilledWellMood();
		else if (moodType.equals(PLATE_TYPE_CLEAR_NUNC_PLATE))
			return createClearNuncFilledWellMood();
		else if (moodType.equals(PLATE_TYPE_EVOSCREEN_2080))
			return createEvotec2080FilledWellMood();
		else if (moodType.equals(PLATE_TYPE_STANDARD_RACK))
			return createRackFilledWellMood();
		else if (moodType.equals(PLATE_TYPE_CLINPLATE_384))
			return createStd384FilledWellMood();
		else if (moodType.equals(PLATE_TYPE_CLINPLATE_1536))
			return createStd384FilledWellMood();
		else
			return null;
	}

	/**
	 * method to create an empty well mood
	 * 
	 * @param type
	 * @return WellMood
	 */
	public WellMood createEmptyWellMood(String moodType) {
		if (moodType.equals(CEN_PLATE))
			return createCeNPlateEmptyWellMood();
		else if (moodType.equals(CEN_RACK))
			return createCeNRackEmptyWellMood();
		else if (moodType.equals(PLATE_TYPE_BLUE_BOX))
			return createBlueBoxEmptyWellMood();
		else if (moodType.equals(PLATE_TYPE_BLACK_NUNC_PLATE))
			return createBlackNuncEmptyWellMood();
		else if (moodType.equals(PLATE_TYPE_WHITE_NUNC_PLATE))
			return createWhiteNuncEmptyWellMood();
		else if (moodType.equals(PLATE_TYPE_CLEAR_NUNC_PLATE))
			return createClearNuncEmptyWellMood();
		else if (moodType.equals(PLATE_TYPE_EVOSCREEN_2080))
			return createEvotec2080EmptyWellMood();
		else if (moodType.equals(PLATE_TYPE_STANDARD_RACK))
			return createRackEmptyWellMood();
		else if (moodType.equals(PLATE_TYPE_CLINPLATE_384))
			return createStd384EmptyWellMood();
		else if (moodType.equals(PLATE_TYPE_CLINPLATE_1536))
			return createStd384EmptyWellMood();
		else
			return null;
	}

	/**
	 * method to a general GUIWellMood
	 * 
	 * @return GUIwellMood
	 */
	public static WellMood createGeneralWellMood(String title, Stroke rimWidth, Color rimColor, Color startWellColor,
			Color endWellColor, double diameterOffset, int wellShape, int rimOffset, int rimDiameterIncrease) {
		return new WellMood(title, rimWidth, rimColor, startWellColor, endWellColor, diameterOffset, wellShape, rimOffset,
				rimDiameterIncrease);
	}

	/**
	 * method to create a blue box GUIPlateMood
	 * 
	 * @return GUIPlateMood for the blue box
	 */
	public static PlateMood createCeNPlatePlateMood(int numberOfRows, int numberOfColumns, String title) {
		return new PlateMood(ChartColor.LIGHT_GRAY, title, DEFAULT_ROW_LABEL, DEFAULT_COLUMN_LABEL, numberOfColumns, numberOfRows,
				ChartColor.GRAY, new BasicStroke(5.0f), PlateMood.WELL_LABEL_FONT_HELVETICA, -4, 1, false, CEN_SCALING_FACTOR,
				createCeNPlateEmptyWellMood(), createCeNPlateFilledWellMood());
	}

	/**
	 * method to create a standard rack GUIPlateMood
	 * 
	 * @return GUIPlateMood for the blue box
	 */
	public static PlateMood createCeNRackMood(int numberOfRows, int numberOfColumns, String title) {
		return new PlateMood(ChartColor.LIGHT_GRAY, title, DEFAULT_ROW_LABEL, DEFAULT_COLUMN_LABEL, numberOfColumns, numberOfRows,
				ChartColor.GRAY, new BasicStroke(3.0f), PlateMood.WELL_LABEL_FONT_HELVETICA, -1, -5, false, CEN_SCALING_FACTOR,
				createCeNRackEmptyWellMood(), createCeNRackFilledWellMood());
	}

	/**
	 * method to create a blue box GUIPlateMood
	 * 
	 * @return GUIPlateMood for the blue box
	 */
	public static PlateMood createBlueBoxPlateMood(int numberOfRows, int numberOfColumns, String title) {
		return new PlateMood(ChartColor.LIGHT_CYAN, title, DEFAULT_ROW_LABEL, DEFAULT_COLUMN_LABEL, numberOfColumns, numberOfRows,
				ChartColor.DARK_CYAN, new BasicStroke(20.0f), PlateMood.WELL_LABEL_FONT_HELVETICA, -4, 1, false,
				STANDARD_SCALING_FACTOR_96_WELL_PLATE, createBlueBoxEmptyWellMood(), createBlueBoxFilledWellMood());
	}

	/**
	 * method to return the GUIWellMood for a filled bluebox
	 * 
	 * @return GUIwellMood for a blue box
	 */
	public static WellMood createBlueBoxFilledWellMood() {
		return new WellMood("BLUE_BOX_FILLED", new BasicStroke(0.5f), Color.BLACK, Color.WHITE, Color.DARK_GRAY, 0,
				WellMood.SHAPE_CIRCULAR, 0, 1,null);
	}

	/**
	 * method to return the GUIWellMood for an empty bluebox well
	 * 
	 * @return GUIwellMood for a blue box
	 */
	public static WellMood createBlueBoxEmptyWellMood() {
		return new WellMood("BLUE_BOX_EMPTY", new BasicStroke(2.5f), ChartColor.CYAN, ChartColor.DARK_CYAN, ChartColor.LIGHT_CYAN,
				6, WellMood.SHAPE_CIRCULAR, 0, 1,null);
	}

	/**
	 * method to create a standard rack GUIPlateMood
	 * 
	 * @return GUIPlateMood for the blue box
	 */
	public static PlateMood createStandardRackMood(int numberOfRows, int numberOfColumns, String title) {
		return new PlateMood(ChartColor.LIGHT_GRAY, title, DEFAULT_ROW_LABEL, DEFAULT_COLUMN_LABEL, numberOfColumns, numberOfRows,
				ChartColor.GRAY, new BasicStroke(5.0f), PlateMood.WELL_LABEL_FONT_HELVETICA, -1, -5, true,
				STANDARD_SCALING_FACTOR_96_WELL_PLATE, createRackEmptyWellMood(), createRackFilledWellMood());
	}

	/**
	 * method to return the GUIWellMood for a filled rack
	 * 
	 * @return GUIwellMood for a rack
	 */
	public static WellMood createRackFilledWellMood() {
		return new WellMood("RACK_FILLED", new BasicStroke(2.0f), Color.BLACK, Color.GRAY, Color.BLACK, 0, WellMood.SHAPE_CIRCULAR,
				1, 4);
	}

	/**
	 * method to return the GUIWellMood for an empty bluebox well
	 * 
	 * @return GUIwellMood for a blue box
	 */
	public static WellMood createCeNPlateEmptyWellMood() {
		return new WellMood("CeN_Plate_EMPTY", new BasicStroke(2.5f),
		// ChartColor.LIGHT_CYAN, ChartColor.BLACK,
				// ChartColor.VERY_LIGHT_CYAN,
				Color.GRAY, Color.LIGHT_GRAY, Color.WHITE, 6, WellMood.SHAPE_CIRCULAR, 0, 1);
	}

	/**
	 * method to return the GUIWellMood for a filled bluebox
	 * 
	 * @return GUIwellMood for a blue box
	 */
	public static WellMood createCeNPlateFilledWellMood() {
		/*
		if(status == 0 ){
			return new WellMood("CeN Plate", new BasicStroke(2.5f), Color.BLACK, Color.WHITE,new Color(255, 0, 0), 0,
					WellMood.SHAPE_CIRCULAR, 0, 1);
		}
		else if(status == 1){
			return new WellMood("CeN Plate", new BasicStroke(2.5f),Color.RED, Color.WHITE,  new Color(255, 55, 255), 0,
					WellMood.SHAPE_CIRCULAR, 0, 1);
		}
		
		
		else{*/
		return new WellMood("CeN Plate", new BasicStroke(2.5f), Color.BLACK, Color.WHITE, Color.DARK_GRAY, 0,
				WellMood.SHAPE_CIRCULAR, 0, 1);
		//}
	}

	/**
	 * method to return the GUIWellMood for a filled rack
	 * 
	 * @return GUIwellMood for a rack
	 */
	public static WellMood createCeNRackFilledWellMood() {
		return new WellMood("CeN_RACK_FILLED", new BasicStroke(2.0f), Color.BLACK, Color.GRAY, Color.BLACK, 0,
				WellMood.SHAPE_SQUARE, 1, 1);
	}

	/**
	 * method to return the GUIWellMood for an empty bluebox well
	 * 
	 * @return GUIwellMood for an empty rack
	 */
	public static WellMood createCeNRackEmptyWellMood() {
		return new WellMood("CeN_RACK_EMPTY", new BasicStroke(2.0f), Color.GRAY, Color.LIGHT_GRAY, Color.WHITE, 0,
				WellMood.SHAPE_SQUARE, 1, 1);
	}

	/**
	 * method to return the GUIWellMood for an empty bluebox well
	 * 
	 * @return GUIwellMood for an empty rack
	 */
	public static WellMood createRackEmptyWellMood() {
		return new WellMood("RACK_EMPTY", new BasicStroke(2.0f), Color.GRAY, Color.LIGHT_GRAY, Color.WHITE, 0,
				WellMood.SHAPE_CIRCULAR, 1, 4);
	}

	/**
	 * method to create a Black Nunc Plate GUIPlateMood
	 * 
	 * @return GUIPlateMood for the blue box
	 */
	public static PlateMood createBlackNuncPlateMood(int numberOfRows, int numberOfColumns, String title) {
		return new PlateMood(ChartColor.BLACK, title, DEFAULT_ROW_LABEL, DEFAULT_COLUMN_LABEL, numberOfColumns, numberOfRows,
				ChartColor.GRAY, new BasicStroke(2.0f), PlateMood.WELL_LABEL_FONT_HELVETICA, -1, -3, true,
				STANDARD_SCALING_FACTOR_384_WELL_PLATE, createBlackNuncEmptyWellMood(), createBlackNuncFilledWellMood());
	}

	/**
	 * method to return the GUIWellMood for a Black filled rack
	 * 
	 * @return GUIwellMood for a rack
	 */
	public static WellMood createBlackNuncFilledWellMood() {
		return new WellMood("BLACK_NUNC_FILLED", new BasicStroke(2.0f), Color.GRAY, Color.GRAY, Color.DARK_GRAY, 1,
				WellMood.SHAPE_SQUARE, 2, 3,null);
	}

	/**
	 * method to return the GUIWellMood for an empty Back Nunc well
	 * 
	 * @return GUIwellMood for an empty rack
	 */
	public static WellMood createBlackNuncEmptyWellMood() {
		return new WellMood("BLACK_NUNC_EMPTY", new BasicStroke(2.0f), Color.GRAY, Color.BLACK, Color.DARK_GRAY, 1,
				WellMood.SHAPE_SQUARE, 2, 3,null);
	}

	/**
	 * method to create a White Nunc Plate GUIPlateMood
	 * 
	 * @return GUIPlateMood
	 */
	public static PlateMood createWhiteNuncPlateMood(int numberOfRows, int numberOfColumns, String title) {
		return new PlateMood(ChartColor.WHITE, title, DEFAULT_ROW_LABEL, DEFAULT_COLUMN_LABEL, numberOfColumns, numberOfRows,
				ChartColor.LIGHT_GRAY, new BasicStroke(2.0f), PlateMood.WELL_LABEL_FONT_HELVETICA, -1, -3, true,
				STANDARD_SCALING_FACTOR_384_WELL_PLATE, createWhiteNuncEmptyWellMood(), createWhiteNuncFilledWellMood());
	}

	/**
	 * method to return the GUIWellMood for a White filled rack
	 * 
	 * @return GUIwellMood
	 */
	public static WellMood createWhiteNuncFilledWellMood() {
		return new WellMood("WHITE_NUNC_FILLED", new BasicStroke(2.0f), Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.GRAY, 1,
				WellMood.SHAPE_SQUARE, 2, 3);
	}

	/**
	 * method to return the GUIWellMood for an empty White Nunc well
	 * 
	 * @return GUIwellMood for an empty rack
	 */
	public static WellMood createWhiteNuncEmptyWellMood() {
		return new WellMood("WHITE_NUNC_EMPTY", new BasicStroke(2.0f), Color.LIGHT_GRAY, Color.WHITE, Color.GRAY, 1,
				WellMood.SHAPE_SQUARE, 2, 3);
	}

	/**
	 * method to create a Clear Nunc Plate GUIPlateMood
	 * 
	 * @return GUIPlateMood
	 */
	public static PlateMood createClearNuncPlateMood(int numberOfRows, int numberOfColumns, String title) {
		return new PlateMood(ChartColor.LIGHT_GRAY, title, DEFAULT_ROW_LABEL, DEFAULT_COLUMN_LABEL, numberOfColumns, numberOfRows,
				ChartColor.GRAY, new BasicStroke(2.0f), PlateMood.WELL_LABEL_FONT_HELVETICA, -1, -3, true,
				STANDARD_SCALING_FACTOR_384_WELL_PLATE, createClearNuncEmptyWellMood(), createClearNuncFilledWellMood());
	}

	/**
	 * method to create a Clear Nunc Plate GUIPlateMood
	 * 
	 * @return GUIPlateMood
	 */
	public static PlateMood createStd1536WellPlateMood(int numberOfRows, int numberOfColumns, String title) {
		return new PlateMood(ChartColor.LIGHT_GRAY, title, DEFAULT_ROW_LABEL, DEFAULT_COLUMN_LABEL, numberOfColumns, numberOfRows,
				ChartColor.GRAY, new BasicStroke(2.0f), PlateMood.WELL_LABEL_FONT_HELVETICA, -1, -3, true,
				STANDARD_SCALING_FACTOR_384_WELL_PLATE, createStd384EmptyWellMood(), createStd384FilledWellMood());
	}

	/**
	 * method to create a Clear Nunc Plate GUIPlateMood
	 * 
	 * @return GUIPlateMood
	 */
	public static PlateMood createStd384WellPlateMood(int numberOfRows, int numberOfColumns, String title) {
		return new PlateMood(ChartColor.LIGHT_GRAY, title, DEFAULT_ROW_LABEL, DEFAULT_COLUMN_LABEL, numberOfColumns, numberOfRows,
				ChartColor.GRAY, new BasicStroke(2.0f), PlateMood.WELL_LABEL_FONT_HELVETICA, -1, -3, true,
				STANDARD_SCALING_FACTOR_384_WELL_PLATE, createStd384EmptyWellMood(), createStd384FilledWellMood());
	}

	/**
	 * method to return the GUIWellMood for a Clear filled rack
	 * 
	 * @return GUIwellMood
	 */
	public static WellMood createStd384FilledWellMood() {
		return new WellMood("CLINPLATE_FILLED", new BasicStroke(2.0f), Color.GRAY, Color.WHITE, Color.GRAY, 1,
				WellMood.SHAPE_CIRCULAR, 1, 2);
	}

	/**
	 * method to return the GUIWellMood for an empty Clear Nunc well
	 * 
	 * @return GUIwellMood for an empty rack
	 */
	public static WellMood createStd384EmptyWellMood() {
		return new WellMood("CLINPLATE_EMPTY", new BasicStroke(2.0f), Color.GRAY, Color.GRAY, Color.LIGHT_GRAY, 1,
				WellMood.SHAPE_CIRCULAR, 2, 3);
	}

	/**
	 * method to return the GUIWellMood for a Clear filled rack
	 * 
	 * @return GUIwellMood
	 */
	public static WellMood createClearNuncFilledWellMood() {
		return new WellMood("CLEAR_NUNC_FILLED", new BasicStroke(2.0f), Color.GRAY, Color.WHITE, Color.GRAY, 1,
				WellMood.SHAPE_SQUARE, 1, 2);
	}

	/**
	 * method to return the GUIWellMood for an empty Clear Nunc well
	 * 
	 * @return GUIwellMood for an empty rack
	 */
	public static WellMood createClearNuncEmptyWellMood() {
		return new WellMood("CLEAR_NUNC_EMPTY", new BasicStroke(2.0f), Color.GRAY, Color.GRAY, Color.LIGHT_GRAY, 1,
				WellMood.SHAPE_SQUARE, 2, 3);
	}

	/**
	 * method to create a standard rack GUIPlateMood
	 * 
	 * @return GUIPlateMood for the blue box
	 */
	public static PlateMood createEvotec2080PlateMood(int numberOfRows, int numberOfColumns, String title) {
		return new PlateMood(ChartColor.WHITE, title, DEFAULT_ROW_LABEL, DEFAULT_COLUMN_LABEL, numberOfColumns, numberOfRows,
				ChartColor.DARK_BLUE, new BasicStroke(5.0f), PlateMood.WELL_LABEL_FONT_HELVETICA, 0, 0, false,
				STANDARD_SCALING_FACTOR_2080_WELL_PLATE, createEvotec2080EmptyWellMood(), createEvotec2080FilledWellMood());
	}

	/**
	 * method to return the GUIWellMood for a filled rack
	 * 
	 * @return GUIwellMood for a rack
	 */
	public static WellMood createEvotec2080FilledWellMood() {
		return new WellMood("EVOTEC_2080_FILLED", new BasicStroke(0.05f), Color.GRAY, Color.LIGHT_GRAY, Color.GRAY, 0,
				WellMood.SHAPE_CIRCULAR, 0, 0);
	}

	/**
	 * method to return the GUIWellMood for an empty bluebox well
	 * 
	 * @return GUIwellMood for an empty rack
	 */
	public static WellMood createEvotec2080EmptyWellMood() {
		return new WellMood("EVOTEC_2080_EMPTY", new BasicStroke(2.0f), Color.LIGHT_GRAY, Color.WHITE, Color.LIGHT_GRAY, 0,
				WellMood.SHAPE_CIRCULAR, 0, 0);
	}
}
