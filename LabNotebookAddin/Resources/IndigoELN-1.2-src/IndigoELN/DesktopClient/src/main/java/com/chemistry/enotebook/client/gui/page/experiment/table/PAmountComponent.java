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
package com.chemistry.enotebook.client.gui.page.experiment.table;

import com.chemistry.enotebook.client.gui.common.utils.AmountEditListener;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.domain.AmountModel;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.common.units.UnitCache2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.common.SignificantFigures;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;
import com.chemistry.enotebook.utils.DefaultPreferences;
import com.chemistry.enotebook.utils.NumberTextField;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

/**
 * 
 * 
 *
 */
public class PAmountComponent extends JPanel implements PopupMenuListener {

	private static final long serialVersionUID = 7445282725629252589L;

	private static Logger logger = Logger.getLogger(PAmountComponent.class.getName());

	protected UnitCache2 uc = UnitCache2.getInstance();
	protected AmountModel currentAmt = new AmountModel();
	protected NumberTextField valueView = null; // double value is
	// displayed here.
	protected JTextField txtUnitView = new JTextField();// Used to show
	// displayed Unit.
	protected JComboBox comboUnitView; // Indicates the unit value of the
	// object
	protected int maxFigs = 14;
	protected int displayFigs = 3;
	protected EmptyBorder border = new EmptyBorder(0, 0, 0, 0);
	protected boolean editableFlg = false;
	protected boolean disableCombo = false;
	protected boolean unitChanged = false;
	protected boolean inUpdate = false;
	protected int horizontalAlignment = SwingConstants.RIGHT;
	protected List<AmountEditListener> editlisteners = new Vector<AmountEditListener>();
	protected Color valueSetTextColor = Color.BLUE;
	protected int clickCountToStart = 2;
	protected boolean comboClickOverride = false;
	protected JPopupMenu popupMenu = new JPopupMenu();
	// TODO: implement log4j alternative.
	protected boolean debug = false;
	protected double dataSizeFull[][] = { { CeNGUIUtils.FILL, CeNGUIUtils.HORIZ_SPACE, CeNGUIUtils.PREF }, { CeNGUIUtils.FILL } };
	protected double dataSizeBrief[][] = { { CeNGUIUtils.FILL }, { 20 } };

	private Color background = DefaultPreferences.getEditableRowBackgroundColor(); //new Color(255, 255, 245);
	
	public PAmountComponent() {
		super();
		init();
	}

	public PAmountComponent(AmountModel amt) {
		super();
		init();
		setValue(amt);
	}

	private void init() {
		// UnitCache uc = UnitCache.getInstance();
		valueView = new NumberTextField();
		valueView.setToDouble(true);
		resetLayout(dataSizeFull);
		setBorder(border);
		// valueView.setText(currentAmt.getValue());
		valueView.setHorizontalAlignment(horizontalAlignment);
		valueView.setBorder(border);
		valueView.setEditable(editableFlg);
		valueView.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == KeyEvent.VK_TAB) {
					stopEditing();
				} else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
					// This condition is never fired
					cancelEditing();
				} else {
					fireKeyTypedEvent(e);
				}
			}

			// KeyPressed Handles the ESCAPE Key
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
					cancelEditing();
				}
			}
		});
		valueView.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				JTextField f = (JTextField) e.getComponent();
				f.setSelectionStart(0);
				f.setSelectionEnd(f.getText().length());
				fireFocusGainedEvent(e);
			}

			public void focusLost(FocusEvent e) {
				if (!unitChanged)
					stopEditing();
				fireFocusLostEvent(e);
			}
		});
		CellConstraints cc = new CellConstraints();
		this.add(valueView, cc.xy(1, 1));
		////add(valueView, "0, 0");
		txtUnitView.setText("");
		txtUnitView.setEditable(false);
		txtUnitView.setFocusable(false);
		txtUnitView.setBorder(border);
		txtUnitView.setBackground(background); // vb 11/2 (valueView.getBackground());
		txtUnitView.setHorizontalAlignment(horizontalAlignment);
		txtUnitView.setVisible(false);
		////add(txtUnitView, "2, 0");
		this.add(txtUnitView, cc.xy(3, 1));
		comboUnitView = new CeNComboBox();
		comboUnitView.setBorder(new EmptyBorder(1, 1, 1, 1));
		comboUnitView.setVisible(false);
		// Set Editable to false to indicate no typing in the field is allowed.
		// Does not restrict user from choosing values in the combo box.
		comboUnitView.setEditable(false);
		comboUnitView.addPopupMenuListener(this);
		comboUnitView.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				fireFocusGainedEvent(e);
			}

			public void focusLost(FocusEvent e) {
				fireFocusLostEvent(e);
			}
		});
		this.add(txtUnitView, cc.xy(3, 1));
		//add(comboUnitView, "2, 0");
		setColors();  // vb 11/14
		refresh();
	}

	public void dispose() {
		valueView = null;
		txtUnitView = null;
		comboUnitView = null;
		currentAmt = null;
		uc = null;
	}

	public boolean isComboDisabled() {
		return disableCombo;
	}

	/**
	 * Allows you to control the appearence of the component. If flag is true then only the text version of the unit will be
	 * displayed. False means the combo will be used when here are more than one option for units.
	 * 
	 * @param flag
	 *            true or false
	 */
	public void setDisableCombo(boolean flag) {
		disableCombo = flag;
		updateDisplay();
	}

	/**
	 * Use when click count to start is not important.
	 * 
	 * @return - true or false
	 */
	public boolean isEditable() {
		return editableFlg;
	}

	/**
	 * Use only when you have the mouse event that is in the coordinate space of this component.
	 * 
	 * This method must be overridden to be able to deal with a mouse event properly Otherwise the comboBox will require a
	 * clickToStart number of clicks to allow a selection.
	 * 
	 * @param anEvent -
	 *            mouse event if you want to process click count to start.
	 * @return - true or false
	 */
	public boolean isEditable(EventObject anEvent) {
		boolean result = editableFlg;
		if (anEvent instanceof MouseEvent && result) {
			// Mouse in txt field: apply standard clickCount to start
			result = result && (((MouseEvent) anEvent).getClickCount() >= clickCountToStart);
		}
		return result;
	}

	/**
	 * Flag to set to make sure this component is not editable.
	 * 
	 * @param val -
	 *            false means don't allow edits.
	 */
	public void setEditable(boolean val) {
		editableFlg = val;
		if (valueView != null) {
			valueView.setEditable(editableFlg);
			txtUnitView.setEditable(false);
			comboUnitView.setEnabled(editableFlg);
		}
	}

	public void setUnitEditable(boolean val) {
		if (comboUnitView != null)
		{
			comboUnitView.setEnabled(val);
			comboUnitView.setEditable(val);
		}
	}

	/**
	 * Specifies the number of clicks needed to start editing. Using convention from DefaultCellEditor Works only if event object is
	 * used
	 * 
	 * @param count
	 *            an int specifying the number of clicks needed to start editing
	 * @see #getClickCountToStart
	 */
	public void setClickCountToStart(int count) {
		clickCountToStart = count;
	}

	/**
	 * Using convention from DefaultCellEditor
	 * 
	 * Returns the number of clicks needed to start editing.
	 * 
	 * @return the number of clicks needed to start editing
	 */
	public int getClickCountToStart() {
		return clickCountToStart;
	}

	public boolean isEnabled() {
		return (valueView != null && valueView.isEnabled());
	}

	public void setEnabled(boolean enableFlag) {
		super.setEnabled(enableFlag);
		if (valueView != null) {
			valueView.setVisible(enableFlag);
			txtUnitView.setVisible(enableFlag);
			comboUnitView.setVisible(enableFlag);
		}
	}

	public boolean isFocusable() {
		return (valueView != null && valueView.isEditable());
	}

	public void setFocusable(boolean focusFlag) {
		if (valueView != null) {
			valueView.setFocusable(focusFlag);
			comboUnitView.setFocusable(focusFlag);
		}
	}

	public void setPreferredSize(Dimension d) {
		super.setPreferredSize(d);
		if (valueView != null) {
			Dimension cuvd = comboUnitView.getPreferredSize();
			comboUnitView.setPreferredSize(new Dimension(cuvd.width, d.height));
		}
	}

	public double getValue() throws NumberFormatException {
		if (debug)
			logger.debug("AmountComponent.getAmount(): currentAmt.getValue() = " + currentAmt.doubleValue());
		return currentAmt.doubleValue();
	}

	/**
	 * Makes a copy of an Amount object if that is what is being set. Will allow you to change unit displayed as long as that unit
	 * is of the same type as the one stored.
	 * 
	 * @param value =
	 *            Amount Object
	 */
	public void setValue(Object value) {
		if (debug) {
			logger.debug("AmountComponent.setValue(Object) current value = " + currentAmt.getValue() + " unit = "
					+ currentAmt.getUnit());
			logger.debug("AmountComponent.setValue(Object) setting object of type: " + value.getClass());
		}
		if (value instanceof AmountModel) {
			if (debug)
				logger.debug("AmountComponent.setValue(Object) setting Amount of : " + ((AmountModel) value).getValue()
						+ " unit = " + ((AmountModel) value).getUnit());
			if (currentAmt == null || !currentAmt.equals(value)) {
				currentAmt.deepCopy((AmountModel) value);
			}
		} else if (value instanceof Unit2) {
			if (debug)
				logger.debug("AmountComponent.setValue(Object) setting unit of : " + value);
			if (!((Unit2) value).equals(currentAmt.getUnit()))
				currentAmt.setUnit((Unit2) value);
		} 
		updateDisplay();
	}

	protected Unit2 getUnit() {
		return currentAmt.getUnit();
	}

	public int getFixedFigs() {
		return currentAmt.getFixedFigs();
	}

	public void setFixedFigs(int fixedFigs) {
		currentAmt.setFixedFigs(fixedFigs);
		updateDisplay();
	}

	/**
	 * Exposes the comboBoxBorder for alteration
	 * 
	 * @param b -
	 *            Border type to display on combo box.
	 */
	public void setComboBoxBorder(Border b) {
		comboUnitView.setBorder(b);
		updateDisplay();
	}

	public void setForeground(Color fgColor) {
		super.setForeground(fgColor);
		// Info is set when look&feel is passed to this component
		// do not pass the info on to the combo box.
		if (valueView != null) {
			valueView.setForeground(fgColor);
			txtUnitView.setForeground(fgColor);
		}
	}

	public void setBackground(Color bgColor) {
		super.setBackground(bgColor);
		if (valueView != null) {
			valueView.setBackground(bgColor);
			txtUnitView.setBackground(bgColor);
			comboUnitView.setBackground(bgColor);
		}
	}

	public void setSelectedTextColor(Color c) {
		valueView.setSelectedTextColor(c);
		txtUnitView.setSelectedTextColor(c);
	}

	public Color getSelectedTextColor() {
		return valueView.getSelectedTextColor();
	}

	public void setSelectionColor(Color c) {
//		setSelectionColor(c);
		valueView.setSelectionColor(c);
		txtUnitView.setSelectionColor(c);
	}

	public Color getSelectionColor() {
		return valueView.getSelectionColor();
	}

	public void setValueSetTextColor(Color c) {
		valueSetTextColor = c;
	}

	protected void setColors() {
		if (!currentAmt.isCalculated()) {
			// We want blue font
			setFontColor(valueSetTextColor);
		} else {
			// Use null to tell the text field to use LAF setting
			setFontColor(Color.BLACK);
		}
	}

	public void setFontColor(Color clr) {
		valueView.setForeground(clr);
		txtUnitView.setForeground(clr);
	}

	public void setHorizontalAlignment(int alignment) {
		horizontalAlignment = alignment;
		valueView.setHorizontalAlignment(horizontalAlignment);
		txtUnitView.setHorizontalAlignment(horizontalAlignment);
	}

	public AmountModel getAmount() {
		// need to test the displayed value against the stored value and see if
		// it needs updating.
		return currentAmt;
	}

	// TODO: put this in a swing worker thread so it doesn't hold up the
	// GUI.
	public void updateDisplay() {
		if (debug) {
			debugState("AmountComponent.updateDisplay() -- Before");
		}
		if (currentAmt != null) {
			// Update Value only if one is available for display
			// valueView.setText(Double.toString(currentAmt.GetValueForDisplay()));
			// vb 11/7 replace the or with an and
			if ( ! ( currentAmt.isCalculated()  
					 && CeNNumberUtils.doubleEquals(currentAmt.doubleValue(), 0.0, 0.00001) 
					 && CeNNumberUtils.doubleEquals(currentAmt.getDefaultValue(), 0.0, 0.00001) ) ) {
				// should set display value instead of string Version of Amount
				valueView.setText(currentAmt.GetValueForDisplay()); // currentAmt.toString()
			} else
				valueView.setText("");
			setBackground(background); // vb 11/2 (valueView.getBackground());
			setForeground(valueView.getForeground());
			// Setup the view for the rest of the cell.
			// Update the Unit area
			List<Unit2> li = uc.getUnitsOfTypeSorted(currentAmt.getUnitType());
			// TODO: If users decide more than one mole unit is desired,
			// change
			// this.
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// vb 8/21 making changes so that scalar works the way I think it should (probably wrong) and volume works
			// todo:  figure out why the original logic is not working
			if (currentAmt.getUnitType().getOrdinal() == UnitType.SCALAR_ORDINAL) {
				resetLayout(dataSizeBrief);
				resetTxtUnitView(false);
				resetComboView(false);				
			//} else if (currentAmt.getUnitType().getOrdinal() != UnitType.VOLUME_ORDINAL && (currentAmt.getUnitType().getOrdinal() & (UnitType.MOLES_ORDINAL | UnitType.SCALAR_ORDINAL)) > 0) {
			//	resetLayout(dataSizeBrief);
			//	resetTxtUnitView(false);
			//	resetComboView(false);
			} else if (li.size() == 1 || (li.size() > 0 && isComboDisabled())) {
				resetLayout(dataSizeFull);
				resetComboView(false);
				resetTxtUnitView(true);
			} else if (li.size() > 1) {
				resetLayout(dataSizeFull);
				resetTxtUnitView(false);
				resetComboView(true);
			}
		} else {
			valueView.setText("");
			resetTxtUnitView(false);
			resetComboView(true);
		}
		valueView.setHorizontalAlignment(horizontalAlignment);
		valueView.revalidate();
		//add(valueView, "0, 0");  
		CellConstraints cc = new CellConstraints();
		this.add(valueView, cc.xy(1, 1));
		// turns on the tool tip for amount component in debug mode
		// if(debug){
		if (valueView != null & currentAmt != null & this != null & debug) {
			valueView.setToolTipText(currentAmt.getValue());
			setToolTipText(currentAmt.getValue());
		}// end if
		setColors();  // vb 11/14
		refresh();
	}

	protected void resetLayout(double[][] size) {
		//setLayout(new TableLayout(size));
		// vb 11/6
		this.setLayout(new FormLayout("fill:pref:grow, 2dlu, right:pref", "pref"));

	}

	protected void resetComboView(boolean visibleFlag) {
		comboUnitView.setVisible(visibleFlag);
		if (visibleFlag) {
			fillComboBoxWithUnit(comboUnitView, currentAmt.getUnit());
			//add(comboUnitView, "2, 0");
			CellConstraints cc = new CellConstraints();
			this.add(comboUnitView, cc.xy(3, 1));
			comboUnitView.revalidate();
		} else {
			comboUnitView.removeAllItems();
			remove(comboUnitView);
		}
	}

	protected void resetTxtUnitView(boolean visibleFlag) {
		txtUnitView.setVisible(visibleFlag);
		if (visibleFlag) {
			txtUnitView.setText(currentAmt.getUnit().getDisplayValue());
			txtUnitView.setHorizontalAlignment(horizontalAlignment);
			CellConstraints cc = new CellConstraints();
			this.add(txtUnitView, cc.xy(3, 1));
			//add(txtUnitView, "2, 0");
			txtUnitView.revalidate();
		} else {
			txtUnitView.setText("");
			remove(txtUnitView);
		}
	}

	protected boolean setUnitInCombo(JComboBox jcb, Unit2 selectUnit) {
		boolean result = false;
		Object tmpObj = jcb.getSelectedItem();
		if (tmpObj != null && tmpObj instanceof Unit2) {
			if (!currentAmt.getUnitType().equals(((Unit2) tmpObj).getType()) || !currentAmt.getUnit().equals(tmpObj)) {
				for (int i = 0; i < jcb.getItemCount(); i++) {
					if (currentAmt.getUnit().equals((Unit2) jcb.getItemAt(i))) {
						if (debug)
							logger.debug("AmountComponent.setUnitInCombo(): Selected index = " + i);
						jcb.setSelectedIndex(i);
						if (debug)
							logger.debug("AmountComponent.setUnitInCombo(): unit selected is "
									+ ((Unit2) jcb.getSelectedItem()));
						result = true;
					}
				}
			}
		}
		tmpObj = null;
		return result;
	}

	/**
	 * The combo box is filled with unit types and the current unit is selected.
	 * 
	 */
	protected void fillComboBoxWithUnit(JComboBox jcb, Unit2 fillUnit) {
		if (jcb != null && fillUnit != null) {
			if (debug)
				logger.debug("AmountComponent.fillComboBox: Combo exits fillUnit = " + fillUnit + " of UnitType."
						+ fillUnit.getType());
			{
				jcb.removeAllItems();
				if (debug)
					logger.debug("AmountComponent.fillComboBox: === Start ===");
				List<Unit2> li = uc.getUnitsOfTypeSorted(fillUnit.getType());
				if (debug)
					logger.debug("AmountComponent.fillComboBox: list of units as being entered: " + li);
				for (int i = 0; i < li.size(); i++) {
					jcb.addItem(li.get(i));
				}
				li = null;
				setUnitInCombo(jcb, fillUnit);
				debugCombo(jcb);
				if (debug) {
					logger.debug("AmountComponent.fillComboBox() setting selected item in combo box");
					logger.debug("AmountComponent.fillComboBox() current selected index = " + jcb.getSelectedIndex());
				}
				if (debug)
					logger.debug("AmountComponent.fillComboBox: === End ===");
			}
		}
	}

	public void refresh() {
		revalidate();
	}

	//
	// Editing Functions
	//
	public void addAmountEditListener(AmountEditListener ael) {
		if (!editlisteners.contains(ael))
			editlisteners.add(ael);
	}

	public void removeAmountEditListener(AmountEditListener ael) {
		if (editlisteners.size() > 0)
			editlisteners.remove(ael);
	}

	// This method is called when editing is completed.
	// It must return the new value to be stored in the cell.
	public Object getComponentDisplayedValue() throws NumberFormatException {
		// Amount tmpAmt = null;
		AmountModel tmpAmt = (AmountModel) currentAmt.deepClone();
		// The following assumes that comboUnitView is emptied when it is not
		// visible
		if (unitChanged && comboUnitView.getSelectedItem() instanceof Unit2) {
			tmpAmt.setUnit((Unit2) comboUnitView.getSelectedItem());
			// tmpAmt = new Amount(currentAmt.getValue(), (Unit)
			// comboUnitView.getSelectedItem());
			// tmpAmt.setCalculated(currentAmt.isCalculated());
		} else {
			if (valueView.getText().length() > 0) {
				try {
					SignificantFigures sigs = new SignificantFigures(valueView.getText());
					tmpAmt.setSigDigits(sigs.getNumberSignificantFigures());
					tmpAmt.setValue(valueView.getText(), false);
				} catch (NumberFormatException error) {
					logger.error("Failed getting value from Stoich Table: " + valueView.getText(), error);
					tmpAmt.setValue("", false);
					throw error;
				}
				// tmpAmt = new
				// Amount(getBigDecimal(valueView.getText()).toString(),
				// getUnit());
				// tmpAmt.setCalculated(false);
			} else {
				tmpAmt.softReset(); // doesn't change unit
				// tmpAmt = new Amount(currentAmt.getDefaultValue(), getUnit());
				// tmpAmt.setCalculated(true);
			}
		}
		return tmpAmt;
	}

	public void cancelEditing() {
		if (debug)
			logger.debug("AmountComponent.cancelEditing() calling setAmount()");
		fireEditingCanceled();
		// Always update display after everything has settled down.
		updateDisplay();
	}

	public boolean stopEditing() {
		if (debug)
			// logger.debug("AmountComponent.stopEditing()");
			// Either there is a unit change or a value change
			if (debug) {
				// logger.debug("CurrentAmount ----:" + currentAmt);
				// logger.debug("CurAmount Sigfig----:"
				// + currentAmt.getSigDigits());
			}
		AmountModel newAmount = null;
		try {
			newAmount = (AmountModel) getComponentDisplayedValue();
		} catch (NumberFormatException error) {
			logger.debug("Entered Invalid Number");
			// JOptionPane.showMessageDialog(null, "Invalid number format
			// entered.","Invalid Entry", JOptionPane.ERROR_MESSAGE);
		}
		// newAmount.setCalculated(false);
		if (debug) {
			// logger.debug(" New Amount-----:" + newAmount);
			// logger.debug(" NewAmount SigFig-----:"
			// + newAmount.getSigDigits());
			// logger.debug("Any Difference--:"
			// + newAmount.equals(currentAmt));
		}
		if (newAmount != null && !newAmount.equals(currentAmt) && isEditable()) {
			// Has the user indicated they want to erase user setting?
			if (debug) {
				logger.debug("AmountComponent.stopEditing(): sensed amount changed");
				logger.debug("AmountComponent.stopEditing(): unitChanged = " + unitChanged);
			}
			if (unitChanged) {
				if (debug)
					logger.debug("AmountComponent.stopEditing()");
					// Need to make sure we set the isCalculated flag to
					// false
					// before allowing any calcs to be done.
				currentAmt.setUnit((Unit2) comboUnitView.getSelectedItem());  // vb 8/3 this had only occurred if debug was true
				unitChanged = false;
			} else {
				if (debug)
					logger.debug("AmountComponent.stopEditing(): calling setValue(newAmount.getValue)");
				SignificantFigures sigs = new SignificantFigures(newAmount.getValue());
				currentAmt.setSigDigits(sigs.getNumberSignificantFigures());
				currentAmt.setValue(newAmount.getValue(), newAmount.isCalculated());
			}
			fireEditingStopped();
			// Always update display after everything has settled down.
			updateDisplay();
		} else {
			if (debug)
				logger.debug("AmountComponent.stopEditing(): calling fireEditingCancelled()");
			if (isEditable())
				fireEditingCanceled();
		}
		return true;
	}

	protected void fireEditingCanceled() {
		debugState("AmountComponent.fireEditingCanceled before resetting values");
		ChangeEvent ce = new ChangeEvent(this);
		debugState("AmountComponent.fireEditingCanceled before notifying listeners");
		for (int i = editlisteners.size() - 1; i >= 0; i--)
			((AmountEditListener) editlisteners.get(i)).editingCanceled(ce);
		debugState("AmountComponent.fireEditingCanceled after notifying listeners");
	}

	protected void fireEditingStopped() {
		ChangeEvent ce = new ChangeEvent(this);
		debugState("AmountComponent.fireEditingStopped before notifying listeners");
		// TableModel is notified here.
		for (int i = editlisteners.size() - 1; i >= 0; i--) {
			((AmountEditListener) editlisteners.get(i)).editingStopped(ce);
		}
		debugState("AmountComponent.fireEditingStopped after notifying listeners");
	}

	protected boolean compareValue(Object value) {
		boolean result = false;
		if (currentAmt != null) {
			if (value instanceof AmountModel) {
				if (debug)
					logger.debug("AmountComponent.compareValue(Object) comparing Amount of : " + ((AmountModel) value).getValue()
							+ " with " + currentAmt.getValue());
				result = CeNNumberUtils.doubleEquals(((AmountModel) value).doubleValue(), currentAmt.doubleValue(), 0.000000001);
			} else if (value instanceof String) {
				if (debug)
					logger.debug("AmountComponent.compareValue(Object) comparing String value of : " + value + " with "
							+ currentAmt.GetValueForDisplay());
				result = (!((String) value).equals(currentAmt.GetValueForDisplay()));
			} else if (value instanceof Double) {
				if (debug)
					logger.debug("AmountComponent.compareValue(Object) comparing Double value of : " + value + " with "
							+ currentAmt.GetValueForDisplay());
				result = (!(value.equals(currentAmt.GetValueForDisplay())));
			} else if (value instanceof Unit2) {
				if (debug)
					logger.debug("AmountComponent.compareValue(Object) comparing Unit value of : " + value + " with "
							+ currentAmt.getUnit());
				result = (!((Unit2) value).equals(currentAmt.getUnit()));
			}
		}
		return result;
	}

	private void UnitSelectionChanged(PopupMenuEvent evt) {
		// //logger.debug("--sigfigcount--:"+currentAmt.getSigDigits() );
		if (evt.getSource() instanceof JComboBox) {
			if (currentAmt != null && comboUnitView.getItemCount() > 0
					&& !currentAmt.getUnit().equals(comboUnitView.getSelectedItem())) {
				unitChanged = true;
				stopEditing();
			} else {
				cancelEditing();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#hasFocus()
	 */
	public boolean hasFocus() {
		return (valueView == null) ? false : valueView.hasFocus();
	}

	//
	// Utility functions
	//
	protected BigDecimal getBigDecimal(String val) {
		BigDecimal result = getDefaultBigDecimal();
		if (val.length() > 0) {
			try {
				result = new BigDecimal(val);
			} catch (NumberFormatException e) {
				// not a number should use defaultBigDecimal();
			}
			// following code in the if block sets the scale of 14(maxFigs)
			// to
			// the
			// number. which may not be mandatory. trying to comment and see
			// if
			// it works
			// if (!Double.isInfinite(result.doubleValue()) &&
			// !Double.isNaN(result.doubleValue())) {
			// result = result.setScale(maxFigs,
			// BigDecimal.ROUND_HALF_EVEN);
			// }
		}
		return result;
	}

	protected BigDecimal getDefaultBigDecimal() {
		BigDecimal result = new BigDecimal(0.0);
		return result.setScale(maxFigs, BigDecimal.ROUND_HALF_UP);
	}

	private void fireFocusGainedEvent(FocusEvent e) {
		debugState("AmountComponent.fireFocusLostEvent before notifying listeners");
		// TableModel is notified here.
		Object[] listeners = getFocusListeners();
		for (int i = listeners.length - 1; i >= 0; i--)
			((FocusListener) listeners[i]).focusGained(e);
		debugState("AmountComponent.fireFocusLostEvent after notifying listeners");
	}

	private void fireFocusLostEvent(FocusEvent e) {
		debugState("AmountComponent.fireFocusLostEvent before notifying listeners");
		// TableModel is notified here.
		Object[] listeners = getFocusListeners();
		for (int i = listeners.length - 1; i >= 0; i--)
			((FocusListener) listeners[i]).focusLost(e);
		debugState("AmountComponent.fireFocusLostEvent after notifying listeners");
	}

	private void fireKeyTypedEvent(KeyEvent e) {
		debugState("AmountComponent.fireKeyTypedEvent before notifying listeners");
		// TableModel is notified here.
		Object[] listeners = getKeyListeners();
		for (int i = listeners.length - 1; i >= 0; i--)
			((KeyListener) listeners[i]).keyTyped(e);
		debugState("AmountComponent.fireKeyTypedEvent after notifying listeners");
	}

	//
	// Pop-up menu Listener interface impl
	//
	public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
		if (debug)
			logger.debug("AmountComponent.popupMenuWillBecomeVisible(): called filling PopUp items");
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
		if (debug)
			logger.debug("AmountComponent.popupMenuWillBecomeInvisible(): called edit of Unit should be captured. Update = "
					+ inUpdate);
		// TODO: called twice: second time causes a nullpointer exception. Why?
		if (!inUpdate) {
			inUpdate = true;
			UnitSelectionChanged(arg0);
			inUpdate = false;
		}
	}

	public void popupMenuCanceled(PopupMenuEvent arg0) {
		if (debug)
			logger.debug("AmountComponent.popupMenuCanceled(): cancelling editing session");
		cancelEditing();
	}

	protected void debugState(String source) {
		// TODO: change to use ceh.debugInfo
		if (debug) {
			// logger.debug("AmountComponent.debugState()");
			// logger.debug("AmountComponent.debugState(): Called from
			// "
			// + source);
			logger.debug("AmountComponent.debugState(): valueView.getText() = " + valueView.getText());
			logger.debug("AmountComponent.debugState(): txtUnitView.isVisible = " + txtUnitView.isVisible());
			logger.debug("AmountComponent.debugState(): comboUnitView.isVisible = " + comboUnitView.isVisible());
			debugCombo(comboUnitView);
			logger.debug("AmountComponent.debugState(): currentAmt.getValue() = " + currentAmt.getValue());
			logger.debug("AmountComponent.debugState(): currentAmt.GetValueForDisplay() = " + currentAmt.GetValueForDisplay());
			// logger.debug("AmountComponent.debugState(): Unit = "
			// + getUnit().getCode());
			// logger.debug("AmountComponent.debugState(): UnitType =
			// "
			// + getUnit().getType());
			logger.debug("AmountComponent.debugState(): UnitType Amount = " + currentAmt.getUnitType());
			// logger.debug("AmountComponent.debugState()");
		}
	}

	protected void debugCombo(JComboBox combo) {
		if (debug && comboUnitView.isVisible()) {
			// logger.debug("AmountComponent.debugCombo() === Start
			// ===");
			for (int i = 0; i < combo.getItemCount(); i++) {
				logger.debug("AmountComponent.debugCombo(): combo.getItemAt(" + i + ") = " + combo.getItemAt(i));
			}
			// logger.debug("AmountComponent.debugCombo()");
			logger.debug("AmountComponent.debugCombo(): combo.getSelectedIndex() = " + combo.getSelectedIndex());
			logger.debug("AmountComponent.debugCombo(): combo.getSelectedItem() = " + combo.getSelectedItem());
			// logger.debug("AmountComponent.debugCombo() === End
			// ===");
		}
	}
}
