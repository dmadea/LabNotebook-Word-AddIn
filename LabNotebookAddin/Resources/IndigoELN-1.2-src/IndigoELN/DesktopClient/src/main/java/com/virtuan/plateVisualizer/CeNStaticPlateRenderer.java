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

import org.jfree.chart.*;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class CeNStaticPlateRenderer extends ChartPanel implements ChartMouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6641131153676344208L;
	private int _numColumns;
	private int _numRows;
	private PlateMood _plateMood;
	private JFreeChart _chart;
	private WellPropertiesStructureWindow _propertiesFrame;
	private boolean _displayWellProperties;
	// private String _propertiesFrameTitle;
	private int _currentItem;
	protected Container _plateFrame;
	protected HashMap _wells;
	protected PlateModel _plateModel;
	protected ArrayList chartMouseListeners;
	private static int RIGHT_MOUSE_BUTTON = 3;
	private static String DEFAULT_PLOT_INPUT_STRING = "Default";
	protected static String xyItemEntityName = "XYItemEntity";

	/**
	 * parameterized constructor
	 * 
	 * @param numberOfRows
	 * @param numberOfColumns
	 * @param plateMood
	 * @param initialWellMood
	 * @param initialWellProperties
	 * @param displayWellProperties
	 *            (shows a mouse over frame of displayable properties)
	 * @param propertiesFrameTitle
	 *            title of the mouse over frame
	 */
	public CeNStaticPlateRenderer(int numberOfRows, int numberOfColumns, PlateMood plateMood, WellMood initialWellMood,
			ArrayList initialWellProperties, boolean displayWellProperties// ,
			// String
			// propertiesFrameTitle
			, PlateModel plateModel) {
		super(ChartFactory.createXYAreaChart(DEFAULT_PLOT_INPUT_STRING, DEFAULT_PLOT_INPUT_STRING, DEFAULT_PLOT_INPUT_STRING,
				new PlateDataset(0, 0), PlotOrientation.HORIZONTAL, false, false, false));
		_numRows = numberOfRows;
		_numColumns = numberOfColumns;
		_plateMood = plateMood;
		_displayWellProperties = displayWellProperties;
		// _propertiesFrameTitle = propertiesFrameTitle;
		_plateModel = plateModel;
		initializePlateRenderer(initialWellMood, initialWellProperties);
		this.getPopupMenu().removeAll();
	}

	/**
	 * method to override mous pressed
	 * 
	 * @param e
	 */
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == RIGHT_MOUSE_BUTTON) {
			this.getPopupMenu().setVisible(false);
		}
	}

	/**
	 * method to set a plate mood. The mood will automatically be applied
	 * 
	 * @param plateMood
	 */
	public void setPlateMood(PlateMood plateMood) {
		_plateMood = plateMood;
		applyPlateMood();
	}

	/**
	 * method to return the current plate mood
	 * 
	 * @return PlateMood
	 */
	public PlateMood getPlateMood() {
		return _plateMood;
	}

	/**
	 * method to set the plate application frame for display
	 * 
	 * @param plateFrame
	 */
	public void setPlateApplicationFrame(Container plateFrame) {
		_plateFrame = plateFrame;
	}

	/**
	 * method to set the display of the well properties frame on mouse-over of a well
	 * 
	 * @param displayWellProperties
	 */
	public void setDisplayWellProperties(boolean displayWellProperties) {
		_displayWellProperties = displayWellProperties;
	}

	/**
	 * method to initialize the plate renderer
	 * 
	 * @param plateMood
	 * @param initialWellMood
	 */
	private void initializePlateRenderer(WellMood initialWellMood, ArrayList initialWellProperties) {
		chartMouseListeners = new ArrayList();
		createChartPanel();
		applyPlateMood();
		_propertiesFrame = new WellPropertiesStructureWindow();
		this.addChartMouseListener(this);
	}

	/**
	 * method to create a chart panel
	 * 
	 * @param plateMood
	 * @param wellMood
	 * @return ChartPanel value of the new chart panel
	 */
	private void createChartPanel() {
		_chart = ChartFactory.createXYAreaChart(_plateMood.getPlateTitle(), _plateMood.getColumnLabel(), _plateMood.getRowLabel(),
				_plateModel, _plateMood.getPlateOrientation(), false, false, false);
		XYPlot plot = (XYPlot) _chart.getPlot();
		plot.setForegroundAlpha(1.0f);
		// plot.setBackgroundAlpha(1.0f);
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		CeNWellRenderer wellRenderer = new CeNWellRenderer(this, _plateModel);
		plot.setRenderer(wellRenderer);
		this.setChart(_chart);
	}

	/**
	 * method to apply the current plate mood
	 */
	private void applyPlateMood() {
		XYPlot plot = (XYPlot) _chart.getPlot();
		plot.getDomainAxis().setLowerBound(0);
		plot.getDomainAxis().setUpperBound(_numColumns + 1);
		plot.getDomainAxis().setVisible(false);
		plot.getRangeAxis().setLowerBound(0);
		plot.getRangeAxis().setUpperBound(_numRows + 1);
		plot.getRangeAxis().setVisible(false);
		plot.setOutlinePaint(_plateMood.getBorderColor());
		plot.setOutlineStroke(_plateMood.getBorderStroke());
		plot.setBackgroundPaint(_plateMood.getPlateColor());
	}

	/**
	 * method to return the number of columns
	 * 
	 * @return
	 */
	public int getNumberOfColumns() {
		return _numColumns;
	}

	/**
	 * method to return the number of rows
	 * 
	 * @return
	 */
	public int getNumberOfRows() {
		return _numRows;
	}

	/**
	 * Receives chart mouse click events.
	 * 
	 * @param event
	 *            the event.
	 */
	public void chartMouseClicked(ChartMouseEvent event) {
	}

	/**
	 * method to display the well properties in a mouse over frame
	 * 
	 * @param event
	 * @param entity
	 */
	private void displayProperties(ChartMouseEvent event, XYItemEntity entity) {
		if (null != entity) {
			int item = entity.getItem();
			if (item != _currentItem) {
				_currentItem = item;
				int x = event.getTrigger().getX();
				int y = event.getTrigger().getY();
				Point p = this.getLocationOnScreen();
				WellModel well = _plateModel.getWellBySetIndex(item);
				_propertiesFrame.displayWellProperties(well);
				int xoffset = entity.getArea().getBounds().height;
				int yoffset = xoffset;
				if (PlateGeneralMethods.convertWellPositionToNumericValue(PlateGeneralMethods.getPositionForItem(item, _numRows),
						"x") > _numColumns / 2)
					xoffset = -xoffset - _propertiesFrame.getWidth();
				if (PlateGeneralMethods.convertWellPositionToNumericValue(PlateGeneralMethods.getPositionForItem(item, _numRows),
						"y") > _numRows / 2)
					yoffset = -yoffset - _propertiesFrame.getHeight();
				_propertiesFrame.setLocation(p.x + x + xoffset, p.y + y + yoffset);
				_propertiesFrame.setVisible(true);
			}
		}
	}

	/**
	 * Receives chart mouse moved events.
	 * 
	 * @param event
	 *            the event.
	 */
	public void chartMouseMoved(ChartMouseEvent event) {
		if (null != event.getEntity()) {
			if (event.getEntity().getClass().getName().endsWith(xyItemEntityName)) {
				XYItemEntity entity = (XYItemEntity) event.getEntity();
				if (entity != null && _displayWellProperties)
					displayProperties(event, entity);
				else {
					hidePropertiesFrame();
				}
			}
		} else if (_propertiesFrame.isShowing()) {
			hidePropertiesFrame();
		}
	}

	/**
	 * method to hide the properties frame
	 */
	private void hidePropertiesFrame() {
		if (_propertiesFrame.isShowing()) {
			_propertiesFrame.setVisible(false);
			_currentItem = -1;
		}
	}

	/**
	 * finalizer
	 */
	public void finalizer() {
		_propertiesFrame = null;
	}
}
