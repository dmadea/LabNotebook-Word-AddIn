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
package com.chemistry.enotebook.client.gui.page.experiment.table;

import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.client.gui.tablepreferences.ColumnFurniture;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;

/**
 * 
 * 
 */
public class ParallelCeNTableViewToolBar extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4813299718357377675L;
	//private JComponent comp;
	private boolean on = true;
	private boolean includeStructure = true;
	private JCheckBox structureSwitch;
	private JSlider rowHeightSlider;// = new JSlider(JSlider.HORIZONTAL, 50,
	//private JButton columnPrefButton = new JButton("Column Prefs");
	//private ScrollableMenu hideColumnPopup = new ScrollableMenu("Column Prefs");
	//private JMenuItem exitButton;
	private PCeNTableView table;
	private static String SHOW_HIDE_STRUCTURES = "Show/Hide Structures";
	private static String SHOW_HIDE_COLUMNS = "Show/Hide Columns";

	public ParallelCeNTableViewToolBar() {
	}

	public ParallelCeNTableViewToolBar(PCeNTableView table) {
		this.table = table;
		this.setLayout(new BorderLayout());
		structureSwitch = new JCheckBox(SHOW_HIDE_STRUCTURES);
		init();
	}

	public ParallelCeNTableViewToolBar(PCeNTableView table, JPanel otherElements) {
		this(table);
		this.add(otherElements, BorderLayout.LINE_END);
	}

	private void init() {
//		if (comp == null)
//			return;
//		if (comp instanceof PCeNTableView) {
//			table = comp;
			JPanel tempPanel = new JPanel(new FlowLayout());
			//final PCeNTableView pTable = (PCeNTableView) comp;
			//PCeNTableModel tModel = (PCeNTableModel) table.getModel();
			final TableColumnModel columnModel = table.getColumnModel();
			//final PCeNTableModelConnector connector = tModel.getConnector();
			if (includeStructure) {
				//final TableColumn structureColumn = table.getColumn(PCeNTableView.STRUCTURE);
				Object structureColumn = table.getColumn("Structure");
				if (structureColumn instanceof ColumnFurniture) {
					if (((ColumnFurniture) structureColumn).isVisible()) {
						structureSwitch.setSelected(true);
						structureSwitch.setText("Hide Structures");
					} else {
						structureSwitch.setSelected(false);
						structureSwitch.setText("Show Structures");
					}
				} else {
					structureSwitch.setSelected(true);
					structureSwitch.setText("Hide Structures");
				}
//				structureSwitch.setSelected(on);
//				structureSwitch.setText("Hide Structures");
//				structureSwitch.addActionListener(new ActionListener() {
//					public void actionPerformed(ActionEvent evt) {
//						table.setSelectedAreas(null);  
//						if (structureSwitch.isSelected()) {
//							//columnModel.addColumn(structureColumn);
//							int columnIndex = columnModel.getColumnIndex(PCeNTableView.STRUCTURE);
//							//columnModel.moveColumn(columnIndex, 0);
//							if (table.getTablePreferenceDelegate() != null)
//								table.getTablePreferenceDelegate().changeColumnVisibility(table, columnIndex, true);
//							else
//								TablePreferenceHandler.changeColumnVisibility(table, columnIndex, true);
//							structureSwitch.setText("Hide Structures");
//							table.setRowHeight(DefaultPreferences.TALL_ROW_HEIGHT); 
//
//						}// end if
//						else{
//							//////columnModel.removeColumn(structureColumn);
//							int columnIndex = columnModel.getColumnIndex(PCeNTableView.STRUCTURE);
//							if (table.getTablePreferenceDelegate() != null)
//								table.getTablePreferenceDelegate().changeColumnVisibility(table, columnIndex, false);
//							else
//								TablePreferenceHandler.changeColumnVisibility(table, columnIndex, false);
//							structureSwitch.setText("Show Structures");
//							table.setRowHeight(DefaultPreferences.SHORT_ROW_HEIGHT); 
//						}
//						table.sizeColumnsToFit(-1);
//					}// end action performed
//				}// end actionListener
//				);// end addAL
			}
			rowHeightSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 400, table.getRowHeight());
			rowHeightSlider.addChangeListener(table);
			tempPanel.add(rowHeightSlider);
			//tempPanel.add(structureSwitch);
			this.add(tempPanel, BorderLayout.LINE_START);

//		}// end if instanceof
	} // end init

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
