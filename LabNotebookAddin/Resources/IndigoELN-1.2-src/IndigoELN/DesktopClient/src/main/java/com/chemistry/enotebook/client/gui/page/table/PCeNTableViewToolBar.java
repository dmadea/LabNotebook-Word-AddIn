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
/**
 * 
 */
package com.chemistry.enotebook.client.gui.page.table;

import com.chemistry.enotebook.utils.DefaultPreferences;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * 
 */
public class PCeNTableViewToolBar extends JPanel {
	
	private static final long serialVersionUID = 4546260345250504646L;
	
	private boolean on = true;
	//private boolean off = false;
	private boolean includeStructure = true;
	private JCheckBox structureSwitch;
	private JSlider rowHeightSlider;// = new JSlider(JSlider.HORIZONTAL, 50,
	private PCeNTableView nonScrollingColumns;
	private PCeNTableView table;


	public PCeNTableViewToolBar() {
	}

	/**
	 * @param table
	 */
	public PCeNTableViewToolBar(PCeNTableView table) {
		this.table = table;
		structureSwitch = new JCheckBox("Show/Hide Structures");
		init();
	}

	public PCeNTableViewToolBar(PCeNTableView plateTableViewTable,
	                            PCeNTableView nonScrollingColumns) {
		this.table = plateTableViewTable;
		this.nonScrollingColumns = nonScrollingColumns;
		structureSwitch = new JCheckBox("Show/Hide Structures");
		init();
	}

	private void init() {
		table.setToolBar(this);
		rowHeightSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 400, table.getRowHeight());
		rowHeightSlider.addChangeListener(table);
		if (nonScrollingColumns != null) {
			rowHeightSlider.addChangeListener(nonScrollingColumns);
		}
		structureSwitch.setSelected(on);
		structureSwitch.setText("Hide Structures");
		structureSwitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				table.setSelectedAreas(null); 
				includeStructure = structureSwitch.isSelected();
				
				table.showColumn(PCeNTableView.STRUCTURE, includeStructure, false);
				
				rowHeightSlider.setValue(table.getRowHeight());
				rowHeightSlider.revalidate();
				table.revalidate();
				table.sizeColumnsToFit(-1);
			}// end action performed
		});// end add Action Listener

		if (includeStructure) {
			structureSwitch.setSelected(on);
			// Hide structure field by default
			if (structureSwitch.isSelected()) {
				structureSwitch.doClick();
			}				
		}
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(rowHeightSlider);
		this.add(structureSwitch);
		//TODO: add overall component height adjustment.
	} // end init

	public void changeStructureCheckBox(boolean show) {
		structureSwitch.setSelected(show);
		if (show) {
			structureSwitch.setText("Hide Structures");
			table.setRowHeight(DefaultPreferences.TALL_ROW_HEIGHT); 
		} else {
			structureSwitch.setText("Show Structures");
			table.setRowHeight(DefaultPreferences.SHORT_ROW_HEIGHT); 
		}
	}
	
	/**
	 * @return the includeStructure
	 */
	public boolean isIncludeStructure() {
		return includeStructure;
	}

	/**
	 * @param includeStructure
	 *            the includeStructure to set
	 */
	public void setIncludeStructure(boolean includeStructure) {
		this.includeStructure = includeStructure;
	}
	

}
