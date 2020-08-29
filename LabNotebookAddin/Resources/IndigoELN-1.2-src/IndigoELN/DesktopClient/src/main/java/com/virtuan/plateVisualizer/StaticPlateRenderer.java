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

import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionEvent;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionListener;
import com.chemistry.enotebook.client.gui.page.experiment.plate.WellPropertiesChangeEvent;
import com.chemistry.enotebook.client.gui.page.experiment.plate.WellPropertiesChangeListener;
import com.chemistry.enotebook.client.utils.Disposable;
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.PlateWell;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.ProductPlate;
import org.jfree.chart.*;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Title: PlateRenderer Description: Class responsible for the visual display of plates
 * 2.1 Update for JFreeChart 1.0.0 removed pop up menu
 * 18-May-2007 added MonomerPlate instance var and interface MonomerPlateContainer
 */
public class StaticPlateRenderer extends ChartPanel implements ChartMouseListener, Disposable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -779621108558820860L;
	private int _numColumns;
	private int _numRows;
	private PlateMood _plateMood;
	private JFreeChart _chart;
	private WellPropertiesFrame _propertiesFrame;
	private boolean _displayWellProperties;
	private String _propertiesFrameTitle;
	private int _currentItem;
	protected Container _plateFrame;
	protected HashMap _wells;
	protected PlateModel _plateModel;
	protected ArrayList chartMouseListeners;
	private static String DEFAULT_PLOT_INPUT_STRING = "Default";
	protected static String xyItemEntityName = "XYItemEntity";
	private ChemistryPanel mChemistryPanel = new ChemistryPanel();
	private XYItemEntity mXYItemEntity = null;
	private BatchSelectionListener[] batchSelectionListeners = null; 
	private WellPropertiesChangeListener[] wellPropertiesChangeListeners = null; 
	private JPopupMenu wellPropertiesPopup = new JPopupMenu();  
	private XYItemEntity popupWellEntity = null;
	private WellModel selectedWell = null;
	private WellMood savedWellMood = null;
	private PlateWell platewell;

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
	public StaticPlateRenderer(int numberOfRows, 
							   int numberOfColumns, 
							   PlateMood plateMood, 
							   WellMood initialWellMood,
							   ArrayList initialWellProperties, 
							   boolean displayWellProperties, 
							   String propertiesFrameTitle, 
							   PlateModel plateModel,
							   BatchSelectionListener[] batchSelectionListeners,
							   WellPropertiesChangeListener[] wellPropertiesChangeListeners) {
		super(ChartFactory.createXYAreaChart(DEFAULT_PLOT_INPUT_STRING, DEFAULT_PLOT_INPUT_STRING, DEFAULT_PLOT_INPUT_STRING,
				new PlateDataset(0, 0), PlotOrientation.HORIZONTAL, false, false, false));
		_numRows = numberOfRows;
		_numColumns = numberOfColumns;
		_plateMood = plateMood;
		_displayWellProperties = displayWellProperties;
		_propertiesFrameTitle = propertiesFrameTitle;
		_plateModel = plateModel;
		//_batchSelectionListener = batchSelectionListener;
		this.batchSelectionListeners = batchSelectionListeners;  // vb 6/21 change to array
		this.wellPropertiesChangeListeners = wellPropertiesChangeListeners;
		initializePlateRenderer(initialWellMood, initialWellProperties);
		this.getPopupMenu().removeAll();
	}
	
	public void clearSelection() {
		if (this.selectedWell != null) {
			WellModel well = this.selectedWell;
			if (this.savedWellMood != null)
				well.setWellMood(savedWellMood);
		}
	}
	
	public void dispose() {
		this.batchSelectionListeners = null;
		if (this.chartMouseListeners != null) {
			this.chartMouseListeners.clear();
			this.chartMouseListeners = null;
		}
		this.wellPropertiesChangeListeners = null;
		// nothing was being performed below so it was commented out
//		if (this._wells != null) {
//			for (Iterator it = _wells.keySet().iterator(); it.hasNext();) {
//				Object key = it.next();
//				System.out.println();
//			}
//		}

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
		_propertiesFrame = new WellPropertiesFrame(_propertiesFrameTitle, mChemistryPanel);
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
		WellRenderer wellRenderer = new WellRenderer(this, _plateModel);
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
		if (null == event.getEntity()) {
			forwardPopUpEventToParent(event.getTrigger());
			return;  // mouse not clicked on well
		}	
		this._propertiesFrame.setVisible(false);
		if (event.getEntity().getClass().getName().endsWith(xyItemEntityName)) {
			mXYItemEntity = (XYItemEntity) event.getEntity();
			int item = mXYItemEntity.getItem();
			// Restore previous selected well mood if it exists
			if (this.selectedWell != null)
				if (this.savedWellMood != null)
					selectedWell.setWellMood(savedWellMood);
			// Reset well mood of selected well and save it
			WellModel well = _plateModel.getWellBySetIndex(item);  
			this.selectedWell = well;
			try {
				this.savedWellMood = (WellMood)well.getWellMood().clone();
			} catch (CloneNotSupportedException e) {}
			well.getWellMood().setRimOffset(1);
			well.getWellMood().setRimDiameterIncrease(4); // vbtodo 11/28 make these constants
	
			String plateBarcode = "";
			WellProperty plateBarcodeProp = well.getProperty("Plate Barcode");
			if (plateBarcodeProp != null)
				plateBarcode = plateBarcodeProp.getValue();
			Object userWellObject = well.getUserWellObject();
			
			if (platewell != null) //Reset the batch selection when a new well is selected.
			{
				BatchModel batchModel = (BatchModel) platewell.getBatch();
				if (batchModel instanceof ProductBatchModel)
					((ProductBatchModel)batchModel).setSelected(false);
				platewell = null;
			}
			platewell = (PlateWell) userWellObject;
			if (userWellObject instanceof PlateWell) {
				// If this is a popup trigger, show popup menu
				if ((event.getTrigger().getModifiers() & InputEvent.BUTTON3_MASK) != 0) {  // right mouse clicked......		}
					this.displayWellPropertiesPopupMenu(event.getTrigger().getX(), event.getTrigger().getY());
				// Otherwise notify listeners
				} else if (! event.getTrigger().isPopupTrigger()) {
					if (this.batchSelectionListeners != null) {
						//Below two lines are required in the registration tab. 
						//In order to get the selected batches for registration. May not be useful in other tabs.
						BatchModel batchModel = (BatchModel) platewell.getBatch();
						if (batchModel instanceof ProductBatchModel)
							((ProductBatchModel)batchModel).setSelected(true);
						for (int i=0; i<this.batchSelectionListeners.length; i++) {
							if (this.batchSelectionListeners[i] == null) continue;
							this.batchSelectionListeners[i].batchSelectionChanged(new BatchSelectionEvent(this, platewell));
						}
					}
				}
			} else { // empty well
				for (int i=0; i<this.batchSelectionListeners.length; i++) {
					if (this.batchSelectionListeners[i] == null) continue;
					this.batchSelectionListeners[i].batchSelectionChanged(new BatchSelectionEvent(this, platewell));
				}				
			}
		} else {
			mXYItemEntity = null;
			// System.out.println(" event.getEntity()=null)");
		}
	}


	// Bypass super's mouse event handlers
	public void mouseReleased(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	
	private void forwardPopUpEventToParent(MouseEvent evt) {
		Container parent = this.getParent();
		while ((parent.getMouseListeners() == null) || (parent.getMouseListeners().length == 0)) {
			parent = parent.getParent(); 
			if (parent == null) {
				return;
			}
		}
		
		MouseEvent e = new MouseEvent(SwingUtilities.getRootPane(parent), 
				evt.getID(), 
				evt.getWhen(), 
				evt.getModifiers() | evt.getModifiersEx(), 
				evt.getX(), 
				evt.getY(), 
				evt.getClickCount(), 
				SwingUtilities.isRightMouseButton(evt));
		
		e = SwingUtilities.convertMouseEvent(this, e, parent);
		
		parent.dispatchEvent(e);
		
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
				WellModel well = _plateModel.getWellBySetIndex(item);  // vb 5/25
				// vb 5/25 plate attributes are not in the platewell model
				String plateBarcode = "";
				WellProperty plateBarcodeProp = well.getProperty("Plate Barcode");
				if (plateBarcodeProp != null)
					plateBarcode = plateBarcodeProp.getValue();
				Object userWellObject = well.getUserWellObject();
				if (userWellObject instanceof PlateWell) {
					PlateWell platewell = (PlateWell) userWellObject;
					// vb 5/25 plate attributes are not in the platewell model
					_propertiesFrame.displayWellProperties(platewell, plateBarcode);
				} else {  // Show well location
					WellProperty prop = well.getInalienableProperty("Position");
					// vb 6/14 need a plate in the constructor
					PlateWell platewell = new PlateWell(new ProductPlate());
					platewell.setWellNumber(prop.getValue());
					_propertiesFrame.displayWellProperties(platewell, plateBarcode);
				}
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
	 * method to display the well properties in a mouse over frame
	 * 
	 * @param event
	 * @param entity
	 */
//	private void displayProperties(XYItemEntity entity) {
//		if (null != entity) {
//			int item = entity.getItem();
//			//if (item != _currentItem) {
//			//	_currentItem = item;
//			WellModel well = _plateModel.getWellBySetIndex(item);  // vb 5/25
//			// vb 5/25 plate attributes are not in the platewell model
//			String plateBarcode = "";
//			WellProperty plateBarcodeProp = well.getProperty("Plate Barcode");
//			if (plateBarcodeProp != null)
//				plateBarcode = plateBarcodeProp.getValue();
//			Object userWellObject = well.getUserWellObject();
//			if (userWellObject instanceof PlateWell) {
//				PlateWell platewell = (PlateWell) userWellObject;
//				// vb 5/25 plate attributes are not in the platewell model
//				_propertiesFrame.displayWellProperties(platewell, plateBarcode);
//			} else {  // Show well location
//				PlateWell platewell = new PlateWell(new RegisteredPlate());
//				platewell.setWellNumber("A1");
//				_propertiesFrame.displayWellProperties(platewell, plateBarcode);
//			}
//			//}
//		}
//	}

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

	public WellModel getRightMouseSeletedWell() {
		WellModel well = null;
		if (null != mXYItemEntity) {
			int item = mXYItemEntity.getItem();
			well = _plateModel.getWellBySetIndex(item);
		}
		return well;
	}
	
    /**
     * The idea is to modify the zooming options depending on the type of chart 
     * being displayed by the panel.
     *
     * @param x  horizontal position of the popup.
     * @param y  vertical position of the popup.
     */
    public void displayWellPropertiesPopupMenu(int x, int y) {

        if (this.wellPropertiesPopup != null) {
        	this.wellPropertiesPopup.show(this, x, y);
        }
        popupWellEntity = (XYItemEntity) this.getEntityForPoint(x, y);
    }
    
    public void createWellPropertiesPopupMenu() {
    	if (this.wellPropertiesPopup == null) {
    		this.wellPropertiesPopup = new JPopupMenu();
    	} else {
    		this.wellPropertiesPopup.removeAll();
    	}
    }

    public void addWellPropertiesMenuItem(String text) {
    	JMenuItem propertyItem = new JMenuItem(text);
    	this.wellPropertiesPopup.add(propertyItem);
//    	propertyItem.addActionListener(this);
    	propertyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleWellPropertiesChange(e.getActionCommand());
			}
    	});
    }
    
    private void handleWellPropertiesChange(String command) {
    	if (popupWellEntity == null)
    		return;
		int item = popupWellEntity.getItem();
		WellModel well = _plateModel.getWellBySetIndex(item);
		popupWellEntity = null;
		Object userWellObject = well.getUserWellObject();
		PlateWell platewell = (PlateWell) userWellObject;
		if (userWellObject instanceof PlateWell) {
			if (this.wellPropertiesChangeListeners != null) {
				for (int i=0; i<this.wellPropertiesChangeListeners.length; i++) {
					this.wellPropertiesChangeListeners[i].wellPropertiesChanged(new WellPropertiesChangeEvent(this, platewell, command));
				}
			}
		}
		if (this.batchSelectionListeners != null) {
			BatchModel batchModel = (BatchModel) platewell.getBatch();
			for (int i=0; i<this.batchSelectionListeners.length; i++) {
				if (this.batchSelectionListeners[i] == null) continue;
				this.batchSelectionListeners[i].batchSelectionChanged(new BatchSelectionEvent(this, platewell));
			}
		}
    }
    

//	/* (non-Javadoc)
//	 * @see com.chemistry.enotebook.client.gui.page.experiment.table.MonomerPlateContainer#getMonomerPlate()
//	 */
//	public MonomerPlate getMonomerPlate() {
//		return monomerPlate;
//	}
//
//	/* (non-Javadoc)
//	 * @see com.chemistry.enotebook.client.gui.page.experiment.table.MonomerPlateContainer#setMonomerPlate(com.chemistry.enotebook.domain.MonomerPlate)
//	 */
//	public void setMonomerPlate(MonomerPlate monomerPlate) {
//		this.monomerPlate = monomerPlate;
//	}

//	/**
//	 * @return the _batchSelectionListener
//	 */
//	public BatchSelectionListener getBatchSelectionListener() {
//		return _batchSelectionListener;
//	}
//	
	
}
