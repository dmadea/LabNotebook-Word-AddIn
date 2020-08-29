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

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class CeNWellRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
	
	private static final long serialVersionUID = -1831093557526508252L;
	
	private CeNStaticPlateRenderer _plate;
	private PlateModel _model;

	/**
	 * parameterized constructor
	 * 
	 * @param numberOfColumns
	 */
	public CeNWellRenderer(CeNStaticPlateRenderer plate, PlateModel model) {
		super();
		_plate = plate;
		_model = model;
	}

	/**
	 * Draws the visual representation of a single data item.
	 * 
	 * @param g2
	 *            the graphics device.
	 * @param state
	 *            the renderer state.
	 * @param dataArea
	 *            the area within which the data is being drawn.
	 * @param info
	 *            collects information about the drawing.
	 * @param plot
	 *            the plot (can be used to obtain standard color information etc).
	 * @param domainAxis
	 *            the domain (horizontal) axis.
	 * @param rangeAxis
	 *            the range (vertical) axis.
	 * @param dataset
	 *            the dataset.
	 * @param series
	 *            the series index (zero-based).
	 * @param item
	 *            the item index (zero-based).
	 * @param crosshairState
	 *            crosshair information for the plot ( <code>null</code> permitted).
	 * @param pass
	 *            the pass index.
	 */
	public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
			ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState,
			int pass) {
		double diameter;
		double x = dataset.getXValue(series, item);
		double y = dataset.getYValue(series, item);
		Double xn = new Double(x);
		Double yn = new Double(y);
		if (yn != null) {
			// double x = xn.doubleValue();
			// double y = yn.doubleValue();
			RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
			RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
			double transX = domainAxis.valueToJava2D(x, dataArea, xAxisLocation);
			double transY = rangeAxis.valueToJava2D(y, dataArea, yAxisLocation);
			PlotOrientation orientation = plot.getOrientation();
			WellMood wellMood = _model.getWellBySetIndex(item).getWellMood();
			diameter = PlateGeneralMethods.getGeneralWellDiameter(dataArea, getMaxPlateSideIndex(), _plate.getPlateMood()
					.getWellScalingFactor());
			double centeringOffset = wellMood.getDiameterOffset();
			double transX1 = getCenteredWellLocation(transX, diameter, centeringOffset);
			double transY1 = getCenteredWellLocation(transY, diameter, centeringOffset);
			paintWellVisualizationByOrientation((int) transY1, (int) transX1, diameter, g2, wellMood, orientation, dataset, series,
					item, state);
			boolean highlightX = false;
			boolean highlightY = false;
			paintRowColumnLabels((int) transY, (int) transX, (int) diameter, (int) centeringOffset, g2, xn, yn, highlightX,
					highlightY);
			updateCrosshairValues(crosshairState, x, y, transX, transY, orientation);
		}
	}

	/**
	 * method to draw the well labels
	 * 
	 * @param trans1
	 * @param trans2
	 * @param diameter
	 * @param centeringOffset
	 * @param g
	 * @param valueX
	 * @param valueY
	 */
	private void paintRowColumnLabels(int transY, int transX, int diameter, int centeringOffset, Graphics2D g2, Number valueX,
			Number valueY, boolean highlightX, boolean highlightY) {
		Color xColor;
		Color yColor;
		PlateMood mood = _plate.getPlateMood();
		if (highlightX)
			xColor = Color.YELLOW;
		else
			xColor = mood.getBorderColor();
		if (highlightY)
			yColor = Color.YELLOW;
		else
			yColor = mood.getBorderColor();
		if (_plate.getPlateMood().isAxesLabelsDisplayed()) {
			if (valueY.intValue() == mood.getNumberOfRows()) {
				Font labelFont = new Font(mood.getWellLabelFont(), 1, diameter / 2);
				g2.setFont(labelFont);
				g2.setColor(xColor);
				g2.drawString(String.valueOf(valueX.intValue()), transX + mood.getXLabelOffsetFromCenter(), transY - (diameter / 2)
						+ mood.getYLabelOffsetFromCenter());
			}
			if (valueX.intValue() == 1) {
				Font labelFont = new Font(mood.getWellLabelFont(), 1, diameter / 2);
				g2.setFont(labelFont);
				g2.setColor(yColor);
				g2.drawString(PlateGeneralMethods.convertNumericStringToALetter(String.valueOf(mood.getNumberOfRows()
						- valueY.intValue() + 1)), transX - (diameter) - mood.getXLabelOffsetFromCenter(), transY
						- mood.getYLabelOffsetFromCenter());
			}
		}
	}

	/**
	 * method to drive the well drawing based on plate orientation
	 * 
	 * @param trans1
	 * @param trans2
	 * @param diameter
	 * @param g2
	 * @param guiWellMood
	 * @param orientation
	 */
	private void paintWellVisualizationByOrientation(int trans1, int trans2, double diameter, Graphics2D g2, WellMood guiWellMood,
			PlotOrientation orientation, XYDataset dataset, int series, int item, XYItemRendererState state) {
		if (orientation == PlotOrientation.HORIZONTAL) {
			paintWellVisualization((int) trans1, (int) trans2, diameter, g2, guiWellMood, dataset, series, item, state);
		} else if (orientation == PlotOrientation.VERTICAL) {
			paintWellVisualization((int) trans2, (int) trans1, diameter, g2, guiWellMood, dataset, series, item, state);
		}
	}

	/**
	 * method to return the well diameter or square edge length
	 * 
	 * @param diameter
	 * @param diameterOffset
	 */
	public double getCenteredWellLocation(double transLocation, double diameter, double diameterOffset) {
		return transLocation - (diameter / 2) + (diameterOffset / 2);
	}

	/**
	 * method to paint the well visualization
	 * 
	 * @param trans1
	 * @param trans2
	 * @param diameter
	 * @param g2
	 */
	private void paintWellVisualization(int trans1, int trans2, double diameter, Graphics2D g2, WellMood guiWellMood,
			XYDataset dataset, int series, int item, XYItemRendererState state) {
		Shape shape;
		double trueDiameter = diameter - guiWellMood.getDiameterOffset();
		g2.setStroke(guiWellMood.getRimWidth());
		g2.setPaint(guiWellMood.getRimColor());
		shape = drawWell(g2, trans1 - guiWellMood.getRimOffset(), trans2 - guiWellMood.getRimOffset(), trueDiameter
				+ guiWellMood.getRimDiameterIncrease(), guiWellMood.getWellShape());
		state.getInfo().getOwner().getEntityCollection().add(new XYItemEntity(shape, dataset, series, item, null, null));
		g2.setStroke(new BasicStroke(1.0f));
		g2.setPaint(new GradientPaint((float) trans1, (float) trans2, guiWellMood.getStartWellColor(), (float) trans1
				+ (float) diameter, (float) trans2 + (float) diameter, guiWellMood.getEndWellColor()));
		shape = drawWell(g2, trans1, trans2, trueDiameter, guiWellMood.getWellShape());
		// state.getInfo().getOwner().getEntityCollection().add(
		// new XYItemEntity(shape, dataset, series, item, null,null));
	}

	/**
	 * method to draw a well
	 * 
	 * @param g2
	 * @param trans1
	 * @param trans2
	 * @param trueDiameter
	 * @param wellShape
	 */
	private Shape drawWell(Graphics2D g2, int trans1, int trans2, double trueDiameter, int wellShape) {
		if (wellShape == WellMood.SHAPE_SQUARE) {
			Rectangle2D rect = new Rectangle2D.Double(trans1, trans2, trueDiameter, trueDiameter);
			g2.fill(rect);
			return rect;
		} else {
			Ellipse2D elps = new Ellipse2D.Double(trans1, trans2, trueDiameter, trueDiameter);
			g2.fill(elps);
			return elps;
		}
	}

	/**
	 * method to return the maximum of the number of rows vs. the number of columns of the plate
	 * 
	 * @return int value of the maximum plate row/column index
	 */
	private int getMaxPlateSideIndex() {
		return Math.max(_plate.getNumberOfColumns(), _plate.getNumberOfRows());
	}

	/**
	 * Returns a clone of the renderer.
	 * 
	 * @return A clone.
	 * @throws CloneNotSupportedException
	 *             if the renderer cannot be cloned.
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
