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
package com.chemistry.enotebook.client.gui.tablepreferences;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.stoichiometry.ReactantColumnModel;
import com.chemistry.enotebook.experiment.common.units.Unit;
import com.chemistry.enotebook.experiment.common.units.UnitFactory;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class UnitsMenuHandler {
	private NotebookPage page;
	private NotebookUser user;
	private TablePreferenceDelegate preferences;
	private JTable table;
	private int modelIndex;
	private UnitType type;
	private Unit unit;
	private String[] unitCodes;
	private String[] unitCodesAsDisplayValue;
	private JMenu menu;
	ButtonGroup group;
	JRadioButton button;

	public UnitsMenuHandler() {
		modelIndex = -1;
	} // end default constructor

	public UnitsMenuHandler(NotebookPage page, TablePreferenceDelegate preferences, JTable table, JMenu menu, int modelIndex) {
		this();
		this.page = page;
		this.table = table;
		this.preferences = preferences;
		this.modelIndex = modelIndex;
		this.menu = menu;
		group = new ButtonGroup();
		user = MasterController.getUser();
		// menuItems =
	} // end constructor

	/** Class used for JRadioButtonMenuItem buttons action */
	private class UnitsAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			Object obj = evt.getSource();
			if (obj instanceof JRadioButton) {
				button = (JRadioButton) obj;
				if (page.isPageEditable()) {
					Thread unitsSaver = new Thread() {
						public void run() {
							String buttonText = button.getText();
							switch (modelIndex) {
								case ReactantColumnModel.WEIGHT:
								case ReactantColumnModel.VOLUME:
								case ReactantColumnModel.MOLES:
								case ReactantColumnModel.MOLARITY:
									ReactantColumnModel reactantModel = ReactantColumnModel.getInstance();
									type = reactantModel.getAmountUnitType(modelIndex);
							}
							try {
								String amountUnitCode = preferences.getNotebookUserPreferredUnit(type.toString());
								// updates preferences in Cache
								if (!buttonText.equals(amountUnitCode)) {
									button.setSelected(true);
									// if there are no preference for unit
									// AND
									if (!amountUnitCode.equals("")) {
										// if preferred unit not the same as the
										// current unit
										if (!amountUnitCode.equals(button.getName())) {
											String newValue = button.getName();
											user.setPreference(getXPathForUnitType(type.toString()), newValue);
											// String tablePref =
											// preferences.buildNotebookPagePropertiesFromUserDoc().replaceAll("
											// ","").replaceAll("\r\n","");
											// if(tablePref != null) {
											// page.setTableProperties(tablePref);
											// } // end if
										}// end if
									} else {
										// create user preferences since they do
										// not exist
										preferences.createAmountPreferredUnit(getXPathForUnitType(type.toString()), button
												.getName());
										user.setPrefChanges(true);
										// String tablePref =
										// preferences.buildNotebookPagePropertiesFromUserDoc().replaceAll("
										// ","").replaceAll("\r\n","");
										// if(tablePref != null) {
										// page.setTableProperties(tablePref);
										// }
									} // end if
								} // end if
							} catch (UserPreferenceException usExp) {
								CeNErrorHandler.getInstance().logExceptionWithoutDisplay(usExp, usExp.getMessage());
							}
						}// end run
					};// end Runnable
					javax.swing.SwingUtilities.invokeLater(unitsSaver);
				}// end if pageIsEditable
			} // end if instanceof JRadioButton
		} // // end actionPerformed method
	} // end inner class

	public void createUnitsMenu() {
		int rowIndex = 0;
		String amountUnitCode = null;
		if (modelIndex != -1) {
			Object value = table.getModel().getValueAt(rowIndex, modelIndex);
			if (value instanceof Amount) {
				type = ((Amount) value).getUnitType();
				unit = ((Amount) value).getUnit();
				if (type != null && unit != null) {
					unitCodesAsDisplayValue = (String[]) UnitFactory.getComboArrayForType(type);
					unitCodes = (String[]) UnitFactory.getUnitCodesOfType(type);
					Arrays.sort(unitCodes);
					if (unitCodesAsDisplayValue != null && (unitCodesAsDisplayValue.length > 1)
							&& (unitCodes != null && (unitCodes.length > 1))) {
						// get the User preferred unit from User Document in
						// Cache
						try {
							amountUnitCode = preferences.getNotebookUserPreferredUnit(type.toString());
						} catch (UserPreferenceException usExp) {
							CeNErrorHandler.getInstance().logExceptionWithoutDisplay(usExp, usExp.getMessage());
						}
						for (int i = 0; i < unitCodesAsDisplayValue.length; i++) {
							JRadioButton item = new JRadioButton((unitCodesAsDisplayValue)[i]);
							item.setName(unitCodes[i]);
							// if there is preferred unit find the
							// corresponding button and set the state to
							// selected;
							// oterwise set to the default unit
							if (!amountUnitCode.equals("") && amountUnitCode.equals(item.getName()))
								item.setSelected(true);
							else if (unit.getStdCode().equals(item.getName()))
								item.setSelected(true);
							item.addActionListener(new UnitsAction());
							item.setBorderPainted(false);
							group.add(item);
							menu.add(item);
						}// end for
					} else
						menu.setEnabled(false);
				}
			} else {
				// if table has no rows take unit type from ColumnReactantModel
				String defaultUnitCode = null;
				boolean unitIsSet = false;
				switch (modelIndex) {
					case ReactantColumnModel.WEIGHT:
						if (!unitIsSet) {
							defaultUnitCode = "mg";
							unitIsSet = true;
						}
					case ReactantColumnModel.VOLUME:
						if (!unitIsSet) {
							defaultUnitCode = "mL";
							unitIsSet = true;
						}
					case ReactantColumnModel.MOLES:
						if (!unitIsSet) {
							defaultUnitCode = "mMol";
							unitIsSet = true;
						}
					case ReactantColumnModel.MOLARITY:
						if (!unitIsSet) {
							defaultUnitCode = "M";
							unitIsSet = true;
						}
						ReactantColumnModel reactantModel = ReactantColumnModel.getInstance();
						UnitType type = reactantModel.getAmountUnitType(modelIndex);
						// create units menu
						unitCodesAsDisplayValue = (String[]) UnitFactory.getComboArrayForType(type);
						unitCodes = (String[]) UnitFactory.getUnitCodesOfType(type);
						Arrays.sort(unitCodes);
						if (unitCodesAsDisplayValue != null && (unitCodesAsDisplayValue.length > 1)
								&& (unitCodes != null && (unitCodes.length > 1))) {
							// get the User preferred unit from User Document in
							// Cache
							try {
								amountUnitCode = preferences.getNotebookUserPreferredUnit(type.toString());
							} catch (UserPreferenceException usExp) {
								CeNErrorHandler.getInstance().logExceptionWithoutDisplay(usExp, usExp.getMessage());
							}
							for (int i = 0; i < unitCodesAsDisplayValue.length; i++) {
								JRadioButton item = new JRadioButton((unitCodesAsDisplayValue)[i]);
								item.setName(unitCodes[i]);
								// if there is preferred unit find the
								// corresponding button and set the state to
								// selected;
								// oterwise set to the default unit
								if (!amountUnitCode.equals("") && amountUnitCode.equals(item.getName()))
									item.setSelected(true);
								// else if( item.getName().equals(
								// defaultUnitCode ) ) item.setSelected(true);
								item.addActionListener(new UnitsAction());
								item.setBorderPainted(false);
								group.add(item);
								menu.add(item);
							}// end for
						} // 
						break;
					default:
						menu.setEnabled(false);
				} // end switch
			}
		}// end if modelIndex != -1
	}// end method

	public String getXPathForUnitType(String unitTypeName) {
		String xPath = null;
		if (unitTypeName.equals("MASS"))
			xPath = NotebookUser.PREF_Mass_Amount_Unit_Code;
		else if (unitTypeName.equals("MOLAR"))
			xPath = NotebookUser.PREF_Molar_Amount_Unit_Code;
		else if (unitTypeName.equals("VOLUME"))
			xPath = NotebookUser.PREF_Volume_Amount_Unit_Code;
		else if (unitTypeName.equals("MOLES"))
			xPath = NotebookUser.PREF_Moles_Amount_Unit_Code;
		return xPath;
	}
}// end class
