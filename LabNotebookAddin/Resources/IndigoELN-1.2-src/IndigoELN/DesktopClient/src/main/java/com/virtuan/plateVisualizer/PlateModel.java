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
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Title: PlateModel Description: XYdataset used for rendering the plate wells
 * Update for JFreeChart 1.0.0
 */
public class PlateModel extends AbstractXYDataset implements XYDataset {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5530662593267316631L;
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
	private int _numRows;
	private int _numCols;
	protected HashMap _wells;
	public static int PLATE_SERIES_NUMBER = 0;
	private static String PROPERTY_NAME_WELL_POSITION = "Position";
	private boolean isRowMajor = true;  // vb 7/11

	/**
	 * parameterized constructor
	 * 
	 * @param numberOfRows
	 * @param numberOfColumns
	 */
	public PlateModel(int numberOfRows, 
				      int numberOfColumns, 
				      WellMood initialWellMood, 
				      ArrayList initialWellProperties,
				      boolean isRowMajor) {
		_numRows = numberOfRows;
		_numCols = numberOfColumns;
		this.isRowMajor = isRowMajor;
		buildDataSet(initialWellMood, initialWellProperties);
		createDataset();
	}

	/**
	 * method for creating the dataset
	 */
	private void createDataset() {
		int numWells = _numRows * _numCols;
		this.xValues = new Double[1][numWells];
		this.yValues = new Double[1][numWells];
		this.seriesCount = 1;
		this.itemCount = numWells;
		int f = 0;
		if (isRowMajor) {
			for (int y = _numRows; y > 0; y--) {
				for (int x = 1; x < _numCols + 1; x++) {
					this.xValues[0][f] = new Double(Double.parseDouble(String.valueOf(x)));
					this.yValues[0][f] = new Double(Double.parseDouble(String.valueOf(y)));
					f++;
				}
			}
		} else {
			for (int x = 1; x < _numCols + 1; x++) {
				for (int y = _numRows; y > 0; y--) {
					this.xValues[0][f] = new Double(Double.parseDouble(String.valueOf(x)));
					this.yValues[0][f] = new Double(Double.parseDouble(String.valueOf(y)));
					f++;
				}
			}
		}
//		for (int x = 1; x < _numCols + 1; x++) {
//			for (int y = 1; y < _numRows + 1; y++) {
//				this.xValues[0][f] = new Double(Double.parseDouble(String.valueOf(x)));
//				this.yValues[0][f] = new Double(Double.parseDouble(String.valueOf(y)));
//				f++;
//			}
//		}
	}

	/**
	 * Returns the x-value for the specified series and item. Series are numbered 0, 1, but for the plate renderer there is only one
	 * series (0)
	 * 
	 * @param series
	 *            the index (zero-based) of the series.
	 * @param item
	 *            the index (zero-based) of the required item.
	 * @return the x-value for the specified series and item.
	 */
	public double getXValue(final int series, final int item) {
		return this.xValues[series][item].doubleValue();
	}

	public Number getX(final int series, final int item) {
		return this.xValues[series][item];
	}

	/**
	 * Returns the y-value for the specified series and item. Series are numbered 0, 1, but for the plate renderer there is only one
	 * series (0)
	 * 
	 * @param series
	 *            the index (zero-based) of the series.
	 * @param item
	 *            the index (zero-based) of the required item.
	 * @return the y-value for the specified series and item.
	 */
	public double getYValue(final int series, final int item) {
		return this.yValues[series][item].doubleValue();
	}

	/**
	 * Returns the y-value for the specified series and item. Series are numbered 0, 1, but for the plate renderer there is only one
	 * series (0)
	 * 
	 * @param series
	 *            the index (zero-based) of the series.
	 * @param item
	 *            the index (zero-based) of the required item.
	 * @return the y-value for the specified series and item.
	 */
	public Number getY(final int series, final int item) {
		return this.xValues[series][item];
	}

	/**
	 * Returns the number of series in the dataset. For the plate renderer, there is only one series.
	 * 
	 * @return the series count.
	 */
	public int getSeriesCount() {
		return this.seriesCount;
	}

	/**
	 * Returns the name of the series. Returns "Plate " + int value of the series
	 * 
	 * @param series
	 *            the index (zero-based) of the series.
	 * @return the name of the series.
	 */
	public String getSeriesName(final int series) {
		return "Plate " + series;
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
	 * method to return the number of rows
	 * 
	 * @return int
	 */
	public int getNumberOfRows() {
		return _numRows;
	}

	/**
	 * method to return the number of columns
	 * 
	 * @return int
	 */
	public int getNumberOfColumns() {
		return _numCols;
	}

	/**
	 * method to build the plate data set
	 * 
	 * @param numberOfRows
	 * @param numberOfColumns
	 * @param initialWellMood
	 */
	private void buildDataSet(WellMood initialWellMood, ArrayList initialWellProperties) {
		WellModel well;
		WellProperty wellProperty;
		try {
			_wells = new HashMap();
			int wellSequenceNumber = 0;  //KS: well sequence number in 1-D array of plate wells top -> down order in each column for freeChart use
			
			if (isRowMajor){
				for (int currentRow = 1; currentRow <= _numRows; currentRow++) {
					for (int currentColumn = 1; currentColumn <= _numCols; currentColumn++) {
						well = new WellModel((WellMood) initialWellMood.clone());
						well.addInalienableProperty(new WellProperty(PROPERTY_NAME_WELL_POSITION, getPositionForItem(wellSequenceNumber), true, 0));
						for (Iterator i = initialWellProperties.iterator(); i.hasNext();) {
							wellProperty = (WellProperty) i.next();
							well.addProperty((WellProperty) wellProperty.clone());
						}
						_wells.put(new Integer(wellSequenceNumber), well);
						wellSequenceNumber++;
					}
				}
			} else {
				for (int currentColumn = 1; currentColumn <= _numCols; currentColumn++) {
					for (int currentRow = 1; currentRow <= _numRows; currentRow++) {
						well = new WellModel((WellMood) initialWellMood.clone());
						well.addInalienableProperty(new WellProperty(PROPERTY_NAME_WELL_POSITION, getPositionForItem(wellSequenceNumber), true, 0));
						for (Iterator i = initialWellProperties.iterator(); i.hasNext();) {
							wellProperty = (WellProperty) i.next();
							well.addProperty((WellProperty) wellProperty.clone());
						}
						_wells.put(new Integer(wellSequenceNumber), well);
						wellSequenceNumber++;
					}
				}
			}
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new PlateVisualizationError(e.getMessage());
		}
	}

	/**
	 * method to get the alpha numeric position for an item
	 * 
	 * @param item
	 * @return String value of the alpha numeric position
	 */
	private String getPositionForItem(int item) {
		int column, row;
		
		if (isRowMajor){
			column = item % _numCols + 1;
			row = (item / _numCols) + 1;
		} else {
		   column = (item / _numRows) + 1;
		   row = item % _numRows + 1;
		}
		/*int column = item % _numCols + 1;
		int row = (item / _numCols) + 1;*/
		//int column = (item / _numRows) + 1;
		//int row = item % _numRows + 1;   vb 7/10
		String s = PlateGeneralMethods.convertNumericStringToALetter(String.valueOf(row)) + String.valueOf(column);
		//String s = PlateGeneralMethods.convertNumericStringToALetter(String.valueOf(column)) + String.valueOf(row);
		return s; //PlateGeneralMethods.convertNumericStringToALetter(String.valueOf(_numRows - row + 1)) + String.valueOf(column);
	}

	/**
	 * method to get the item value for a position value
	 * 
	 * @param position
	 * @return int value of the item
	 */
	private int getItemForPosition(String position) {
		int columnCoord = PlateGeneralMethods.convertWellPositionToNumericValue(position, PlateGeneralMethods.X_TEXT);
		int rowCoord = PlateGeneralMethods.convertWellPositionToNumericValue(position, PlateGeneralMethods.Y_TEXT);
		return getItemForPosition(columnCoord, rowCoord);
	}

	/**
	 * method to get the item value for a position value
	 * 
	 * @param position
	 * @return int value of the item
	 */
	private int getItemForPosition(int xCoord, int yCoord) {
		return (((xCoord - 1) * _numRows) + (_numRows - yCoord));
	}

	/**
	 * method to return a well based on the column(x) and row (y) coordinate
	 * 
	 * @param xCoord
	 * @param yCoord
	 * @return PhysicalWell
	 */
	public WellModel getWellByCoordinate(int xCoord, int yCoord) {
		return (WellModel) _wells.get(new Integer(getItemForPosition(xCoord, yCoord)));
	}

	/**
	 * method to return a well based on the item index of the set Item indexes are applied by row precedence and start at zero. For
	 * example: For a 96 well plate, well "C02" is item index = 13 and coord x = 2, y = 3 It is recommended that clients use the
	 * getWellByCoordinate(int xCoord, int yCoord) method rather than getWellBySetIndex in order to avoid confusion.
	 * 
	 * @param item
	 * @return PhysicalWell
	 */
	public WellModel getWellBySetIndex(int item) {
		return (WellModel) _wells.get(new Integer(item));
	}

	/**
	 * required method but not used
	 * 
	 * @param item
	 * @return Comparable value : null in this case
	 */
	public Comparable getSeriesKey(int item) {
		return null;
	}
}
