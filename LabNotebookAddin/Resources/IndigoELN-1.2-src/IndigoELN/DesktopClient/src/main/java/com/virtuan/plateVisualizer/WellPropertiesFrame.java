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
import com.chemistry.enotebook.domain.PlateWell;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Title: WellPropertiesFrame Description: Frame that displays the visible well properties during a well mouse over
 */
public class WellPropertiesFrame extends JWindow {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9113900596220343003L;
	private static int DEFAULT_WIDTH = 100;
	private static int DEFAULT_HEIGHT = 70;
	private JFrame _referenceFrame;
	private int _defaultWidth;
	private int _defaultHeight;
	private String mStructure = "";
	private ChemistryPanel mChemistryPanel;

	/**
	 * parameterized constructor
	 * 
	 * @param title
	 */
	public WellPropertiesFrame(String title, ChemistryPanel chime) {
		// super(title);
		// _referenceFrame = this;
		_defaultWidth = DEFAULT_WIDTH;
		_defaultHeight = DEFAULT_HEIGHT;
		mChemistryPanel = chime;
	}

	/**
	 * method to return the list of viewable properties
	 * 
	 * @param well
	 * @return ArrayList
	 */
	private ArrayList listViewableProperties(WellModel well) {
		WellProperty property;
		ArrayList ar = new ArrayList();
		for (int x = 0; x < well.getNumberOfProperties(); x++) {
			property = (WellProperty) well.getProperty(x);
			if (property.getIsDisplayed()) {
				if (property.getName().equalsIgnoreCase("Structure")) {
					mStructure = property.getValue();
				} else {
					ar.add(property);
				}
			}
		}
		for (int y = 0; y < well.getNumberOfInalienableProperties(); y++) {
			property = (WellProperty) well.getInalienableProperty(y);
			if (property.getIsDisplayed()) {
				// if (property.getName().equalsIgnoreCase("Structure")) {
				// mStructure = property.getValue();
				// } else {
				ar.add(property);
				// }
			}
		}
		return ar;
	}

	/**
	 * method to set the reference frame for positioning the mouse-overs in a complex GUI By default the reference frame is this
	 * frame
	 * 
	 * @param frame
	 */
	public void setReferenceFrame(JFrame frame) {
		_referenceFrame = frame;
	}

	/**
	 * method to return the reference frame
	 * 
	 * @return JFrame
	 */
	public JFrame getReferenceFrame() {
		return _referenceFrame;
	}

	/**
	 * method to override the base width of the frame
	 * 
	 * @param width
	 */
	public void setDefaultWidth(int width) {
		_defaultWidth = width;
	}

	/**
	 * method to override the base height of the frame
	 * 
	 * @param width
	 */
	public void setDefaultHeight(int height) {
		_defaultHeight = height;
	}

	public void displayEmptyWell(String plateBarcode, String position) {
//		System.out.println();
	}
	
	/**
	 * method to display the WellProperties  vb 6/1
	 * 
	 * @param well
	 */
	public void displayWellProperties(PlateWell platewell, String plateBarcode) {
 
		//BatchDetailViewContainer batchViewContainer = new BatchDetailViewContainer(platewell, plateBarcode);
		JPanel panel = new BatchDetailPanel(platewell, plateBarcode);
		//panel.setLayout(new GridLayout(viewProperties.size(), 2));
		this.setVisible(false);
		this.setSize(panel.getPreferredSize());
//		for (Iterator i = viewProperties.iterator(); i.hasNext();) {
//			property = (WellProperty) i.next();
//			if (property != null) {
//				lable = new JTextField(property.getName());
//				lable.setEditable(false);
//				panel.add(lable);
//				value = new JTextField(property.getValue());
//				value.setEditable(false);
//				panel.add(value);
//				height = height + value.getFont().getSize();
//				width = Math.max(width, property.getValue().length());
//				width = Math.max(width, property.getName().length());
//			}
//		}
		//this.setSize(width * 2, _defaultHeight + (height * 2));
		/////////////////this.setSize(_defaultWidth * 2, _defaultHeight + (200));
		this.getContentPane().removeAll();
		panel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		getContentPane().add(panel);
		//if (mStructure == null || mStructure.equalsIgnoreCase("")) {
		//	getContentPane().add(panel);
		//} else {
			//panel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
			// getContentPane().setLayout(new
			// javax.swing.BoxLayout(getContentPane(),
			// javax.swing.BoxLayout.Y_AXIS));
//			GridLayout gridLayout = new GridLayout();
//			gridLayout.setColumns(1);
//			gridLayout.setRows(2);
//			getContentPane().setLayout(gridLayout);
//			getContentPane().add(panel);
			// mChemistryPanel.setSize(new java.awt.Dimension(500,500));
			//getContentPane().add(mChemistryPanel);
			//mChemistryPanel.setMolfileData(mStructure);
		//}
	}
	
	private void displayPanel(JPanel panel) {
		this.setVisible(false);
		this.setSize(panel.getPreferredSize());		
		this.getContentPane().removeAll();
		panel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		getContentPane().add(panel);
	}
}
