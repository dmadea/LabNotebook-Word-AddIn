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

import org.jfree.chart.plot.PlotOrientation;

import java.awt.*;

/**
 * Title: PlateMood Description: represents the visual attributes of the plate
 */
public class PlateMood {
	public static String WELL_LABEL_FONT_HELVETICA = "Helvetica";
	public static String WELL_LABEL_FONT_COURIER = "Courier";
	public static String WELL_LABEL_FONT_ARIAL = "Arial";
	private Color _plateColor;
	private String _columnLabel;
	private String _rowLabel;
	private String _plateTitle;
	private int _numberOfColumns;
	private int _numberOfRows;
	private Color _borderColor;
	private Stroke _borderStroke;
	private String _wellLabelFontStyle;
	private int _xLabelOffsetFromCenter;
	private int _yLabelOffsetFromCenter;
	private boolean _isAxesDisplayed;
	private int _wellScalingFactor = 0;
	private WellMood _emptyWellMood;
	private WellMood _filledWellMood;

	/**
	 * parameterless constructor
	 */
	public PlateMood() {
	}

	/**
	 * parameterized constructor
	 * 
	 * @param plateColor
	 * @param plateTitle
	 * @param rowLabel
	 * @param columnLabel
	 * @param numberOfColumns
	 * @param numberOfRows
	 * @param borderColor
	 * @param borderStroke
	 * @param wellLabelFontStyle
	 * @param xLabelOffsetFromCenter
	 * @param yLabelOffsetFromCenter
	 * @param isAxesDisplayed
	 * @param wellScalingFactor
	 * @param emptyWellMood
	 * @param filledWellMood
	 */
	public PlateMood(Color plateColor, String plateTitle, String rowLabel, String columnLabel, int numberOfColumns,
			int numberOfRows, Color borderColor, Stroke borderStroke, String wellLabelFontStyle, int xLabelOffsetFromCenter,
			int yLabelOffsetFromCenter, boolean isAxesDisplayed, int wellScalingFactor, WellMood emptyWellMood,
			WellMood filledWellMood) {
		_plateColor = plateColor;
		_columnLabel = columnLabel;
		_rowLabel = rowLabel;
		_plateTitle = plateTitle;
		_numberOfColumns = numberOfColumns;
		_numberOfRows = numberOfRows;
		_borderColor = borderColor;
		_borderStroke = borderStroke;
		_wellLabelFontStyle = wellLabelFontStyle;
		_xLabelOffsetFromCenter = xLabelOffsetFromCenter;
		_yLabelOffsetFromCenter = yLabelOffsetFromCenter;
		_isAxesDisplayed = isAxesDisplayed;
		_wellScalingFactor = wellScalingFactor;
		_emptyWellMood = emptyWellMood;
		_filledWellMood = filledWellMood;
	}

	/**
	 * method to return the emptyWellMood
	 * 
	 * @return returns the actual empty well mood
	 */
	public WellMood getEmptyWellMood() {
		return _emptyWellMood;
	}

	/**
	 * method to return the filledWellMood
	 * 
	 * @return returns the actual empty well mood
	 */
	public WellMood getFilledWellMood() {
		return _filledWellMood;
	}

	/**
	 * method for returning the plate color
	 * 
	 * @return Color value of the plate color
	 */
	public Color getPlateColor() {
		return _plateColor;
	}

	/**
	 * method for returning the row label
	 * 
	 * @return String value of therow label
	 */
	public String getRowLabel() {
		return _rowLabel;
	}

	/**
	 * method for returning the column label
	 * 
	 * @return String value of the column label
	 */
	public String getColumnLabel() {
		return _columnLabel;
	}

	/**
	 * method for returning the plate title
	 * 
	 * @return String value of the plate title
	 */
	public String getPlateTitle() {
		return _plateTitle;
	}

	/**
	 * method to return the blue box plate orientation
	 * 
	 * @return PlotOrientation of vertical for the blue box
	 */
	public PlotOrientation getPlateOrientation() {
		return PlotOrientation.VERTICAL;
	}

	/**
	 * methodto return the number of plate columns
	 * 
	 * @return int value of the number of plate columns
	 */
	public int getNumberOfColumns() {
		return _numberOfColumns;
	}

	/**
	 * method to return the number of plate rows
	 * 
	 * @return int value of the number of plate rows
	 */
	public int getNumberOfRows() {
		return _numberOfRows;
	}

	/**
	 * method to return the border color
	 * 
	 * @return Color value of the border color
	 */
	public Color getBorderColor() {
		return _borderColor;
	}

	/**
	 * method to return the border Stroke
	 * 
	 * @return Stroke value of the border Stroke
	 */
	public Stroke getBorderStroke() {
		return _borderStroke;
	}

	/**
	 * method to get the well Label font
	 * 
	 * @return Font value of the well label font
	 */
	public String getWellLabelFont() {
		return _wellLabelFontStyle;
	}

	/**
	 * method to return the xLabelOffsetFromCenter
	 * 
	 * @return int value of the xLabelOffsetfromCenter
	 */
	public int getXLabelOffsetFromCenter() {
		return _xLabelOffsetFromCenter;
	}

	/**
	 * method to return the yLabelOffsetFromCenter
	 * 
	 * @return int value of the yLabelOffsetfromCenter
	 */
	public int getYLabelOffsetFromCenter() {
		return _yLabelOffsetFromCenter;
	}

	/**
	 * method to return whether the axes are displayed
	 * 
	 * @return boolean true if displayed, false if not
	 */
	public boolean isAxesLabelsDisplayed() {
		return _isAxesDisplayed;
	}

	/**
	 * method to return whether the well scaling fator decrease this value if wells are running together in the visualization
	 * 
	 * @return int value of the well scaling factor
	 */
	public int getWellScalingFactor() {
		return _wellScalingFactor;
	}

	/**
	 * method to set the emptyWellMood
	 * 
	 * @param emptyWellMood
	 */
	public void setEmptyWellMood(WellMood emptyWellMood) {
		_emptyWellMood = emptyWellMood;
	}

	/**
	 * method to set the filledWellMood
	 * 
	 * @param filledWellMood
	 */
	public void setFilledWellMood(WellMood filledWellMood) {
		_filledWellMood = filledWellMood;
	}

	/**
	 * method to set the plate color
	 * 
	 * @param plateColor
	 */
	public void setPlateColor(Color plateColor) {
		_plateColor = plateColor;
	}

	/**
	 * method to set the row label
	 * 
	 * @param rowLabel
	 */
	public void setRowLabel(String rowLabel) {
		_rowLabel = rowLabel;
	}

	/**
	 * method to set the column label
	 * 
	 * @param columnLabel
	 */
	public void setColumnLabel(String columnLabel) {
		_columnLabel = columnLabel;
	}

	/**
	 * method to set the plate title
	 * 
	 * @param plateTitle
	 */
	public void setPlateTitle(String plateTitle) {
		_plateTitle = plateTitle;
	}

	/**
	 * method to set the number of plate columns
	 * 
	 * @param numberOfColumns
	 */
	public void setNumberOfColumns(int numberOfColumns) {
		_numberOfColumns = numberOfColumns;
	}

	/**
	 * method to set the number of plate rows
	 * 
	 * @param numberOfRows
	 */
	public void setNumberOfRows(int numberOfRows) {
		_numberOfRows = numberOfRows;
	}

	/**
	 * method to set the border color
	 * 
	 * @param borderColor
	 */
	public void setBorderColor(Color borderColor) {
		_borderColor = borderColor;
	}

	/**
	 * method to set the border Stroke
	 * 
	 * @param borderStroke
	 */
	public void setBorderStroke(Stroke borderStroke) {
		_borderStroke = borderStroke;
	}

	/**
	 * method to set the well Label font Use the public static attributes of this class
	 * 
	 * @param wellLabelFontStyle
	 */
	public void setWellLabelFont(String wellLabelFontStyle) {
		_wellLabelFontStyle = wellLabelFontStyle;
	}

	/**
	 * method to set the xLabelOffsetFromCenter
	 * 
	 * @param xLableOffsetFromCenter
	 */
	public void setXLabelOffsetFromCenter(int xLableOffsetFromCenter) {
		_xLabelOffsetFromCenter = xLableOffsetFromCenter;
	}

	/**
	 * method to set the yLabelOffsetFromCenter
	 * 
	 * @param yLabelOffsetFromCenter
	 */
	public void setYLabelOffsetFromCenter(int yLabelOffsetFromCenter) {
		_yLabelOffsetFromCenter = yLabelOffsetFromCenter;
	}

	/**
	 * method to set whether the axes are displayed
	 * 
	 * @param isAxesLabelsDisplayed
	 *            boolean true if displayed, false if not
	 */
	public void setIsAxesLabelsDisplayed(boolean isAxesLabelsDisplayed) {
		_isAxesDisplayed = isAxesLabelsDisplayed;
	}

	/**
	 * method to set the well scaling fator decrease this value if wells are running together in the visualization
	 * 
	 * @param wellScalingFactor
	 */
	public void setWellScalingFactor(int wellScalingFactor) {
		_wellScalingFactor = wellScalingFactor;
	}
}
