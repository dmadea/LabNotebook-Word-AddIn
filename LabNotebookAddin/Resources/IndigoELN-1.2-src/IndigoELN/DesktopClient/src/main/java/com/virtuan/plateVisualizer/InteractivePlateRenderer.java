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

import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionListener;
import com.chemistry.enotebook.client.gui.page.experiment.plate.WellPropertiesChangeListener;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.util.ShapeUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Title: InteractivePlateRenderer Description: This class is an enhanced plate renderer having standard interactive functionality
 */
public class InteractivePlateRenderer extends StaticPlateRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2564723185156427773L;
	//private static int RIGHT_MOUSE_BUTTON = 3;
	private ArrayList _selectionListeners;
	private ArrayList _popUpListeners;
	private Point2D zoomPoint = null;  // FIXME  this is the first and the last assignment for this field!
	private Point2D zoom1Point = null; // FIXME  this is the first and the last assignment for this field!
	private transient Rectangle2D zoomRectangle = null;
	private transient Rectangle2D zoom1Rectangle = null;
	private boolean horizontalZoom = true;
	private boolean verticalZoom = true;
	private int zoomTriggerDistance;
	private boolean fillZoomRectangle = true;
	//private double scaleX;
	//private double scaleY;
	//private Point2D anchor;
	//private ChartRenderingInfo info;
	//private List chartMouseListeners;
	//private boolean horizontalAxisTrace = false;
	//private boolean verticalAxisTrace = false;
	private transient Line2D verticalTraceLine;
	private transient Line2D horizontalTraceLine;
	//private boolean _performingMouseDrag;
	private boolean _performingTubeMove;

	/**
	 * parameterized constructor
	 * 
	 * @param numberOfRows
	 * @param numberOfColumns
	 * @param plateMood
	 * @param initialWellMood
	 * @param initialWellProperties
	 */
	public InteractivePlateRenderer(int numberOfRows, 
								    int numberOfColumns, 
								    PlateMood plateMood, 
								    WellMood initialWellMood,
								    ArrayList initialWellProperties, 
								    boolean displayWellProperties, 
								    String propertiesFrameTitle, 
								    PlateModel plateModel,
								    BatchSelectionListener[] batchSelectionListeners,
								    WellPropertiesChangeListener[] wellPropertiesChangeListeners) {  // vb 6/21
		super(numberOfRows, numberOfColumns, plateMood, initialWellMood, initialWellProperties, displayWellProperties,
				propertiesFrameTitle, plateModel, batchSelectionListeners, wellPropertiesChangeListeners);
		_selectionListeners = new ArrayList();
		_popUpListeners = new ArrayList();
	}

	/**
	 * method to add to the popup menu
	 * 
	 * @param menuItem
	 */
	public void addToPopUpMenu(JMenuItem menuItem) {
		this.getPopupMenu().add(menuItem);
	}

	/**
	 * method to add the well selection listener
	 * 
	 * @param listener
	 */
	public void addWellSelectionListener(IWellSelectionListener listener) {
		_selectionListeners.add(listener);
	}

	/**
	 * method to alert the selection listeners that a well has been selected
	 */
	private void selectWellToSelectionListeners(int item) {
		IWellSelectionListener sel;
		for (Iterator i = _selectionListeners.iterator(); i.hasNext();) {
			sel = (IWellSelectionListener) i.next();
			sel.selectSingleWell(_plateModel.getWellBySetIndex(item));
		}
	}

	/**
	 * method to alert the selection listeners that a well has been selected
	 * 
	 * @param well
	 */
	private void alertStartSingleWellMoveToSelectionListeners(int item) {
		IWellSelectionListener sel;
		for (Iterator i = _selectionListeners.iterator(); i.hasNext();) {
			sel = (IWellSelectionListener) i.next();
			sel.alertBeginSingleWellMove(_plateModel.getWellBySetIndex(item));
		}
	}

	/**
	 * method to alert the selection listeners that a well has been selected
	 * 
	 * @param well
	 */
	private void alertCompleteSingleWellMoveToSelectionListeners(WellModel well) {
		IWellSelectionListener sel;
		for (Iterator i = _selectionListeners.iterator(); i.hasNext();) {
			sel = (IWellSelectionListener) i.next();
			sel.alertEndSingleWellMove(well);
		}
	}

	/**
	 * method to alert the selection listeners that a well has been selected
	 */
	private void selectMultipleWellsToSelectionListeners(Collection wells) {
		IWellSelectionListener sel;
		for (Iterator i = _selectionListeners.iterator(); i.hasNext();) {
			sel = (IWellSelectionListener) i.next();
			sel.selectMultipleWells(wells);
		}
	}

	/**
	 * method to alert the selection listeners to deselect all wells
	 */
	private void deselectAllToSelectionListeners() {
		IWellSelectionListener sel;
		for (Iterator i = _selectionListeners.iterator(); i.hasNext();) {
			sel = (IWellSelectionListener) i.next();
			sel.deselectAllWells();
		}
	}

	/**
	 * Handles a 'mouse pressed' event.
	 * <P>
	 * This event is the popup trigger on Unix/Linux. For Windows, the popup trigger is the 'mouse released' event.
	 * 
	 * @param e
	 *            The mouse event.
	 */
//	public void mousePressed(MouseEvent e) {
//		System.out.println("<<<<<<<<<<<<<<< InteractivePlateRenderer mousePressed");
//		if (e.getButton() == RIGHT_MOUSE_BUTTON) {
//			this.getPopupMenu().show(this, e.getX(), e.getY());
//		} else {
//			//System.out.println("InteractivePlateRenderer NOT right button");
//			Point2D p = this.translateScreenToJava2D(new Point(e.getX(), e.getY()));
//			int xval = new Integer(String.valueOf(Math.round(p.getX()))).intValue();
//			int yval = new Integer(String.valueOf(Math.round(p.getY()))).intValue();
//			XYItemEntity ent = getEntityAtMousePress(xval, yval);
//			if (null != ent) {
//				int item = ent.getItem();
//				selectWellToSelectionListeners(item);
//				this.alertStartSingleWellMoveToSelectionListeners(item);
//				_performingTubeMove = true;
//				this.zoomRectangle = null;
//				this.zoomPoint = null;
//			} else {
//				this.deselectAllToSelectionListeners();
//				Rectangle2D scaledPlotArea = this.getChartRenderingInfo().getPlotInfo().getPlotArea();
//				Rectangle2D scaledDataArea = this.getChartRenderingInfo().getPlotInfo().getDataArea();
//				if (scaledPlotArea.contains(p)) {
//					this.zoom1Point = ShapeUtilities.getPointInRectangle(e.getX(), e.getY(), scaledDataArea);
//					this.zoomPoint = ShapeUtilities.getPointInRectangle(Double.parseDouble(String.valueOf(xval)), Double
//							.parseDouble(String.valueOf(yval)), scaledDataArea);
//				}
//			}
//		}
//	}

	/**
	 * method for selecting the wells of the rectangular area
	 * 
	 * @param e
	 */
	private void selectRectangleArea(MouseEvent e) {
		if (this.zoomRectangle != null) {
			Point2D p = this.translateScreenToJava2D(new Point(e.getX(), e.getY()));
			long xval = Math.round(p.getX());
			long yval = Math.round(p.getY());
			boolean zoomTrigger1 = this.horizontalZoom && Math.abs(e.getX() - this.zoomPoint.getX()) >= this.zoomTriggerDistance;
			boolean zoomTrigger2 = this.verticalZoom && Math.abs(e.getY() - this.zoomPoint.getY()) >= this.zoomTriggerDistance;
			if (zoomTrigger1 || zoomTrigger2) {
				double x, y, w, h;
				Rectangle2D scaledDataArea = this.getChartRenderingInfo().getPlotInfo().getDataArea();
				if (!this.verticalZoom) {
					x = this.zoomPoint.getX();
					y = scaledDataArea.getMinY();
					w = Math.min(this.zoomRectangle.getWidth(), scaledDataArea.getMaxX() - this.zoomPoint.getX());
					h = scaledDataArea.getHeight();
				} else if (!this.horizontalZoom) {
					x = scaledDataArea.getMinX();
					y = this.zoomPoint.getY();
					w = scaledDataArea.getWidth();
					h = Math.min(this.zoomRectangle.getHeight(), scaledDataArea.getMaxY() - this.zoomPoint.getY());
				} else {
					x = this.zoomPoint.getX();
					y = this.zoomPoint.getY();
					w = Math.min(this.zoomRectangle.getWidth(), scaledDataArea.getMaxX() - this.zoomPoint.getX());
					h = Math.min(this.zoomRectangle.getHeight(), scaledDataArea.getMaxY() - this.zoomPoint.getY());
				}
				Rectangle2D zoomArea = new Rectangle2D.Double(x, y, w, h);
				selectWellsInDragArea(zoomArea);
				this.zoomPoint = null;
				this.zoom1Point = null;
				this.zoomRectangle = null;
				this.zoom1Rectangle = null;
				this.repaint();
			}
		}
	}

	/**
	 * Handles a 'mouse released' event.
	 * <P>
	 * On Windows, we need to check if this is a popup trigger, but only if we haven't already been tracking a zoom rectangle.
	 * 
	 * @param e
	 *            Information about the event.
	 */
//	public void mouseReleased(MouseEvent e) {
//		System.out.println("InteractivePlateRenderef mouseReleased");
//		Point2D p = this.translateScreenToJava2D(new Point(e.getX(), e.getY()));
//		Rectangle2D scaledPlotArea = this.getChartRenderingInfo().getPlotInfo().getPlotArea();
//		if (_performingTubeMove) {
//			long xval = Math.round(p.getX());
//			long yval = Math.round(p.getY());
//			XYItemEntity ent = getEntityAtMousePress(new Integer(String.valueOf(xval)).intValue(),
//					new Integer(String.valueOf(yval)).intValue());
//			if (null != ent) {
//				this.alertCompleteSingleWellMoveToSelectionListeners(_plateModel.getWellBySetIndex(ent.getItem()));
//			} else {
//				this.alertCompleteSingleWellMoveToSelectionListeners(null);
//			}
//			_performingTubeMove = false;
//			this.repaint();
//		} else if (scaledPlotArea.contains(p)) {
//			selectRectangleArea(e);
//		}
//	}

	/**
	 * method to extend the drag area on a mouse click occuring outside the well space
	 * 
	 * @param e
	 */
	private void extendDragArea(MouseEvent e) {
		Point2D p = this.translateScreenToJava2D(new Point(e.getX(), e.getY()));
		Graphics2D g2 = (Graphics2D) getGraphics();
		g2.setXORMode(java.awt.Color.gray);
		if (this.zoomRectangle != null) {
			if (this.fillZoomRectangle) {
				g2.fill(this.zoom1Rectangle);
			} else {
				g2.draw(this.zoom1Rectangle);
			}
		}
		Rectangle2D scaledDataArea = getScreenDataArea();
		if (this.horizontalZoom && this.verticalZoom) {
			double xmax = Math.min(p.getX(), scaledDataArea.getMaxX());
			double ymax = Math.min(p.getY(), scaledDataArea.getMaxY());
			this.zoomRectangle = new Rectangle2D.Double(this.zoomPoint.getX(), this.zoomPoint.getY(), xmax - this.zoomPoint.getX(),
					ymax - this.zoomPoint.getY());
			double x1max = Math.min(e.getX(), scaledDataArea.getMaxX());
			double y1max = Math.min(e.getY(), scaledDataArea.getMaxY());
			this.zoom1Rectangle = new Rectangle2D.Double(this.zoom1Point.getX(), this.zoom1Point.getY(), x1max
					- this.zoom1Point.getX(), y1max - this.zoom1Point.getY());
		}
		if (this.zoomRectangle != null) {
			if (this.fillZoomRectangle) {
				g2.fill(this.zoom1Rectangle);
			} else {
				g2.draw(this.zoom1Rectangle);
			}
		}
		g2.dispose();
	}

	/**
	 * method to move the target on the mouse pointer
	 * 
	 * @param e
	 */
	private void moveMousePointer(MouseEvent e) {
		Graphics2D g2 = (Graphics2D) getGraphics();
		g2.setXORMode(java.awt.Color.gray);
		double x = e.getX();
		double y = e.getY();
		double diameter = PlateGeneralMethods.getGeneralWellDiameter(this.getBounds(), Math.max(this.getNumberOfColumns(), this
				.getNumberOfRows()), BasicVisualPlateFactory.STANDARD_SCALING_FACTOR_96_WELL_PLATE);
		double radius = diameter / 2;
		Shape target = null;
		if (this.getPlateMood().getFilledWellMood().getWellShape() == WellMood.SHAPE_CIRCULAR) {
			target = new Ellipse2D.Double(x - radius, y - radius, diameter, diameter);
		} else {
			target = new Rectangle2D.Double(x - radius, y - radius, diameter, diameter);
		}
		if (this.fillZoomRectangle)
			g2.fill(target);
		else
			g2.draw(target);
		g2.dispose();
		g2 = null;
		target = null;
		this.repaint();
	}

	/**
	 * Handles a 'mouse dragged' event.
	 * 
	 * @param e
	 *            the mouse event.
	 */
	public void mouseDragged(MouseEvent e) {
		if (null != this.zoomPoint) {
			extendDragArea(e);
		} else if (_performingTubeMove) {
			moveMousePointer(e);
		}
	}

	/**
	 * Draws a vertical line used to trace the mouse position to the horizontal axis.
	 * 
	 * @param x
	 *            the x-coordinate of the trace line.
	 */
	private void drawHorizontalAxisTrace(int x) {
		Graphics2D g2 = (Graphics2D) getGraphics();
		// Rectangle2D dataArea = getScaledDataArea();
		Rectangle2D dataArea = getScreenDataArea();
		g2.setXORMode(java.awt.Color.orange);
		if (((int) dataArea.getMinX() < x) && (x < (int) dataArea.getMaxX())) {
			if (this.verticalTraceLine != null) {
				g2.draw(this.verticalTraceLine);
				this.verticalTraceLine.setLine(x, (int) dataArea.getMinY(), x, (int) dataArea.getMaxY());
			} else {
				this.verticalTraceLine = new Line2D.Float(x, (int) dataArea.getMinY(), x, (int) dataArea.getMaxY());
			}
			g2.draw(this.verticalTraceLine);
		}
	}

	/**
	 * Draws a horizontal line used to trace the mouse position to the vertical axis.
	 * 
	 * @param y
	 *            the y-coordinate of the trace line.
	 */
	private void drawVerticalAxisTrace(int y) {
		Graphics2D g2 = (Graphics2D) getGraphics();
		// Rectangle2D dataArea = getScaledDataArea();
		Rectangle2D dataArea = getScreenDataArea();
		g2.setXORMode(java.awt.Color.orange);
		if (((int) dataArea.getMinY() < y) && (y < (int) dataArea.getMaxY())) {
			if (this.horizontalTraceLine != null) {
				g2.draw(this.horizontalTraceLine);
				this.horizontalTraceLine.setLine((int) dataArea.getMinX(), y, (int) dataArea.getMaxX(), y);
			} else {
				this.horizontalTraceLine = new Line2D.Float((int) dataArea.getMinX(), y, (int) dataArea.getMaxX(), y);
			}
			g2.draw(this.horizontalTraceLine);
		}
	}

	/**
	 * method to select the wells in the drag area
	 * 
	 * @param selectionArea
	 *            (Rectangle2D)
	 */
	private void selectWellsInDragArea(Rectangle2D selectionArea) {
		ArrayList wells = null;
		EntityCollection ec = this.getChartRenderingInfo().getEntityCollection();
		if (ec.getEntities().size() > 0) {
			Iterator i = ec.getEntities().iterator();
			wells = new ArrayList();
			while (i.hasNext()) {
				Object o = i.next();
				if (o.getClass().getName().endsWith(xyItemEntityName)) {
					XYItemEntity entity = (XYItemEntity) o;
					if (selectionArea.contains(entity.getArea().getBounds2D())) {
						wells.add(_plateModel.getWellBySetIndex(entity.getItem()));
					}
				}
			}
			selectMultipleWellsToSelectionListeners(wells);
		}
	}

	/**
	 * Receives chart mouse moved events.
	 * 
	 * @param event
	 *            the event.
	 */
	public void chartMouseMoved(ChartMouseEvent event) {
		super.chartMouseMoved(event);
	}

	/**
	 * method to return the entity at a location
	 * 
	 * @param x
	 * @param y
	 * @return XYItemEntity value of the item index
	 */
	private XYItemEntity getEntityAtMousePress(int x, int y) {
		EntityCollection ec = this.getChartRenderingInfo().getEntityCollection();
		Iterator i = ec.getEntities().iterator();
		if (ec.getEntities().size() > 0) {
			while (i.hasNext()) {
				Object o = i.next();
				if (o.getClass().getName().endsWith(xyItemEntityName)) {
					XYItemEntity entity = (XYItemEntity) o;
					double xx = new Double(x).doubleValue();
					double yy = new Double(y).doubleValue();
					Point2D pont = ShapeUtilities.getPointInRectangle(new Double(x).doubleValue(), new Double(y).doubleValue(),
							this.getScreenDataArea());
					if (entity.getArea().contains(
							ShapeUtilities.getPointInRectangle(new Double(x).doubleValue(), new Double(y).doubleValue(), this
									.getScreenDataArea()))) {
						return entity;
					}
				}
			}
		}
		return null;
	}
}
