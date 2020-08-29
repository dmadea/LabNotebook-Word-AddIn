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
import com.chemistry.enotebook.domain.BatchModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;

/**
 * Title: WellPropertiesFrame Description: Frame that displays the visible well properties during a well mouse over.
 */
public class BatchPropertyFrame extends JWindow {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2369264102304775372L;

	private static final Log log = LogFactory.getLog(BatchPropertyFrame.class);
	
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
	public BatchPropertyFrame() {
		// super(title);
		// _referenceFrame = this;
		_defaultWidth = DEFAULT_WIDTH;
		_defaultHeight = DEFAULT_HEIGHT;
		 mChemistryPanel = new ChemistryPanel();
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
		log.debug("No op for displayEmptyWell.");
	}
	
	/**
	 * method to display the WellProperties  vb 6/1
	 * 
	 * @param well
	 */
	public void displayBatchProperties(BatchModel batchModel, String text) {
 
		
		//BatchDetailViewContainer batchViewContainer = new BatchDetailViewContainer(platewell, plateBarcode);
		JPanel panel = new StructureToolTipPanel(batchModel, text);
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
