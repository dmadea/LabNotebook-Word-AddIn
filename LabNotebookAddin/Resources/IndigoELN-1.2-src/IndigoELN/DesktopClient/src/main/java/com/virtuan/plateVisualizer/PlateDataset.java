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

import org.jfree.data.Range;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 * Title: GUIPlateDataset Description: This class creates specific GUI representations for plates
 */
public class PlateDataset extends DefaultTableXYDataset implements XYDataset {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3688408403816313776L;
	private static String DEFAULT_SERIES_NAME = "Wells";
	private Double[][] xValues;
	private Double[][] yValues;
	private int seriesCount;
	private int itemCount;
	private Number domainMin;
	private Number domainMax;
	private Number rangeMin;
	private Number rangeMax;
	private Range domainRange;
	private Range range;
	private String _seriesName;
	private WellMood _wellMoods[];
	private int _numRows;
	private int _numColumns;
	private Double _xHighlight = null;
	private Double _yHighlight = null;

	/**
	 * parameterized constructor int numberOfSeries int numberOfWells
	 */
	public PlateDataset(int numberOfRows, int numberOfColumns) {
		_numRows = numberOfRows;
		_numColumns = numberOfColumns;
		int numWells = numberOfRows * numberOfColumns;
		xValues = new Double[1][numWells];
		yValues = new Double[1][numWells];
		_wellMoods = new WellMood[numWells];
		seriesCount = 1;
		itemCount = numWells;
		_seriesName = DEFAULT_SERIES_NAME;
		createWellData();
	}

	/**
	 * method to create the well data
	 * 
	 * @param numberOfRows
	 * @param numberOfColumns
	 */
	private void createWellData() {
		int f = 0;
		for (int x = 1; x <= _numRows; x++) {
			for (int y = 1; y <= _numColumns; y++) {
				xValues[0][f] = new Double(Double.parseDouble(String.valueOf(x)));
				yValues[0][f] = new Double(Double.parseDouble(String.valueOf(y)));
				f++;
			}
		}
	}

	/**
	 * method to set the wellMood Index
	 * 
	 * @param guiWellMood
	 * @param wellPosition
	 */
	public void setWellMoodAtIndex(WellMood guiWellMood, int wellPosition) {
		_wellMoods[wellPosition] = guiWellMood;
	}

	/**
	 * method to set the wellMood at a particular row, column
	 * 
	 * @param guiWellMood
	 * @param wellRow
	 * @param wellColumn
	 */
	public void setWellMoodAtPlatePosition(WellMood guiWellMood, int wellRow, int wellColumn) {
		int wellPosition = (((wellColumn - 1) * _numRows) + wellRow) - 1;
		_wellMoods[wellPosition] = guiWellMood;
	}

	/**
	 * method to return the wellMood Index
	 * 
	 * @param wellRow
	 * @param wellColumn
	 * @param GUIWellMood
	 *            at the given wellRow, wellColumn
	 */
	public WellMood getWellMood(int wellRow, int wellColumn) {
		int wellPosition = (((wellColumn - 1) * _numRows) + wellRow) - 1;
		return _wellMoods[wellPosition];
	}

	/**
	 * method to return the wellMood Index
	 * 
	 * @param wellRow
	 * @param wellColumn
	 */
	public WellMood getWellMood(int wellIndex) {
		return _wellMoods[wellIndex];
	}

	/**
	 * method to return the row for the given item
	 * 
	 * @param item
	 * @return
	 */
	public int getPlateRowForItem(int item) {
		return _numRows - (item / _numColumns);
	}

	/**
	 * method to return the column for the given item
	 * 
	 * @param item
	 * @return
	 */
	public int getPlateColumnForItem(int item) {
		return item % 12 + 1;
	}

	/**
	 * Returns the x-value for the specified series and item. Series are numbered 0, 1, ...
	 * 
	 * @param series
	 *            the index (zero-based) of the series.
	 * @param item
	 *            the index (zero-based) of the required item.
	 * 
	 * @return the x-value for the specified series and item.
	 */
	public double getXValue(final int series, final int item) {
		return this.xValues[series][item].doubleValue();
	}

	/**
	 * Returns the y-value for the specified series and item. Series are numbered 0, 1, ...
	 * 
	 * @param series
	 *            the index (zero-based) of the series.
	 * @param item
	 *            the index (zero-based) of the required item.
	 * 
	 * @return the y-value for the specified series and item.
	 */
	public double getYValue(final int series, final int item) {
		return this.yValues[series][item].doubleValue();
	}

	/**
	 * Returns the number of series in the dataset.
	 * 
	 * @return the series count.
	 */
	public int getSeriesCount() {
		return this.seriesCount;
	}

	/**
	 * Returns the name of the series.
	 * 
	 * @param series
	 *            the index (zero-based) of the series.
	 * @return the name of the series.
	 */
	public String getSeriesName(int series) {
		return _seriesName;
	}

	/**
	 * Returns the number of items in the specified series.
	 * 
	 * @param series
	 *            the index (zero-based) of the series.
	 * @return the number of items in the specified series.
	 */
	public int getItemCount(final int series) {
		return this.itemCount;
	}

	/**
	 * method to return the number of columns
	 * 
	 * @return int value of the number of columns
	 */
	public int getNumberOfColumns() {
		return _numColumns;
	}

	/**
	 * method to return the number of rows
	 * 
	 * @return int value of the number of rows
	 */
	public int getNumberOfRows() {
		return _numRows;
	}
	/**
	 * method to return the number of rows
	 * 
	 * @return int value of the number of rows
	 */
	// public void setHighlightLabels(Double x, Double y){
	// _xHighlight = x;
	// _yHighlight = y;
	// }
	/**
	 * method to return the number of rows
	 * 
	 * @return int value of the number of rows
	 */
	// public Double getXHighlight(){
	// return _xHighlight;
	// }
	/**
	 * method to return the number of rows
	 * 
	 * @return int value of the number of rows
	 */
	// public Double getYHighlight(){
	// return _yHighlight;
	// }
}
