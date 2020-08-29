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
package com.chemistry.enotebook.client.gui.chlorac;

import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.utils.CeNDialog;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ChloracnegenResultsViewContainer extends CeNDialog {

	private static final long serialVersionUID = 8309921264937456692L;
	
	JPanel jPanel1;
	JPanel jPanel2;
	JPanel jPanel3;
	JPanel jPanel4;
	JLabel resultsHeaderLabel;
	JButton jButtonExit = new JButton();
	JButton jButtonLaunchURL;
	JLabel jLabelAdditionalInfo;
	JScrollPane jScrollPane1;
	JFrame owner;
	final String headerText = "<html><u><font size=4 >Chloracnegen protocol test results</font><html>";
	final String dialogTitle = "Chloracnegen Prediction Results";
	final String addtionalInfoText = "For additonal information about chloracnegens click this link:";
	final String launchURLButtonText = "<html><u>Structure alerts</u></html>";
	final String urlButtonToolTipText = "Click this link for chloracnegen information";
	// db prop

	public ChloracnegenResultsViewContainer(JFrame owner) {
		super(owner);
		this.owner = owner;
		initialiseGUI();
	}

	// destructor for the UI
	public void dispose() {
		MasterController.getGuiComponent().repaint();
		this.setVisible(false);
		// this.dispose();
	}

	public void initialiseGUI() {
		this.setTitle(dialogTitle);
		this.setSize(new java.awt.Dimension(600, 250));
		BorderLayout thisLayout = new BorderLayout();
		this.getContentPane().setLayout(thisLayout);
		jPanel1 = new JPanel();
		FlowLayout jPanel1Layout = new FlowLayout();
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setAlignment(FlowLayout.CENTER);
		jPanel1Layout.setHgap(40);
		jPanel1Layout.setVgap(5);
		jPanel1.setPreferredSize(new java.awt.Dimension(400, 30));
		// jPanel1.setBackground(Color.blue);
		this.getContentPane().add(jPanel1, BorderLayout.NORTH);
		jPanel2 = new JPanel();
		jPanel2.setPreferredSize(new Dimension(this.getWidth(), 278));
		// jPanel2.setBorder(BorderFactory.createEmptyBorder());
		this.getContentPane().add(jPanel2, BorderLayout.CENTER);
		jPanel2.setVisible(true);
		// jPanel2.setBackground(Color.blue);
		resultsHeaderLabel = new JLabel(headerText);
		jPanel1.add(resultsHeaderLabel);
		jPanel3 = new JPanel();
		// jPanel3.setBackground(Color.blue);
		jButtonExit.setText("Close");
		jButtonExit.setBorderPainted(true);
		jButtonExit.setContentAreaFilled(false);
		jButtonExit.setPreferredSize(new java.awt.Dimension(73, 20));
		jButtonExit.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		// jButtonExit.setBounds(new java.awt.Rectangle(500,140,73,20));
		jPanel3.add(jButtonExit, BorderLayout.CENTER);
		this.getContentPane().add(jPanel3, BorderLayout.SOUTH);
		jButtonExit.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evt) {
				jButtonExitMouseReleased(evt);
			}
		});
		// additional info stuff
		jLabelAdditionalInfo = new JLabel(addtionalInfoText);
		jButtonLaunchURL = new JButton(launchURLButtonText);
		jButtonLaunchURL.setBorder(BorderFactory.createEmptyBorder());
		jButtonLaunchURL.setToolTipText(this.urlButtonToolTipText);
		jPanel4 = new JPanel();
		// jPanel4.setBackground(Color.blue);
		jPanel4.add(jLabelAdditionalInfo);
		/*
		jPanel4.add(jButtonLaunchURL);		
		jButtonLaunchURL.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evt) {
				jButtonLaunchURLMouseReleased(evt);
			}
		});
		*/
	}

	protected void jButtonExitMouseReleased(MouseEvent evt) {
		this.dispose();
	}

	/*
	protected void jButtonLaunchURLMouseReleased(MouseEvent evt) {
		// launch the url in the default browser for user
		BrowserLauncher.launchBrowser(this.addtionalInfoURL);
	}
	*/

	public void showGUI() {
		this.setLocationRelativeTo(owner);
		// Display the window.
		this.pack();
		this.setVisible(true);
	}

	public void addStructures(List<ChloracnegenBatchStructure> structures) {
		if (structures == null) {
			structures = new ArrayList<ChloracnegenBatchStructure>();
		}
		ChemistryPanel structViewer = null;
		final Object[][] structArray = new Object[structures.size()][4];
		int i = 0;
		for (ChloracnegenBatchStructure struct : structures) {
			structViewer = new ChemistryPanel();
			structViewer.setSize(100, 100);
			structViewer.setMolfileData(new String(struct.getMolString()));
			structViewer.setVisible(true);
			structArray[i][0] = struct.getDispayID();
			structArray[i][1] = structViewer;
			structArray[i][2] = struct.getChloracnegenType();
			structArray[i][3] = struct.getType().toString();
			// //System.out.println("Results for Struc
			// "+String.valueOf(i+1)+" --> "+struct.getChloracnegenType());
			// //System.out.println("--------------");
			i++;
		}
		// Create table to hold structures
		JTable structureTable = new JTable(new AbstractTableModel() {
			
			private static final long serialVersionUID = 1666166419650937494L;
			
			private String[] columnNames = { "Compound Name", "Compound Structure", "Chloracnegen Type", "Batch Type" };
			private Object[][] rowData = structArray;

			public String getColumnName(int col) {
				return columnNames[col].toString();
			}

			public int getRowCount() {
				return rowData.length;
			}

			public int getColumnCount() {
				return columnNames.length;
			}

			public Object getValueAt(int row, int col) {
				return rowData[row][col];
			}

			public Class<?> getColumnClass(int column) {
				Class<?> dataType = super.getColumnClass(column);
				if (column == 1) {
					dataType = ChemistryPanel.class;
				} else {
					dataType = getValueAt(0, column).getClass();
				}
				return dataType;
			}

			public boolean isCellEditable(int row, int col) {
				return false;
			}

			public void setValueAt(Object value, int row, int col) {
				rowData[row][col] = value;
				fireTableCellUpdated(row, col);
			}
		});
		structureTable.setDefaultRenderer(ChemistryPanel.class, new ChimeProRenderer());
		structureTable.setDefaultRenderer(String.class, new MultiLineCellRenderer());
		structureTable.setRowHeight(100);
		structureTable.setPreferredScrollableViewportSize(new Dimension(500, 200));
		jScrollPane1 = new JScrollPane(structureTable);
		jScrollPane1.setVisible(true);
		jPanel2.removeAll();
		jPanel2.add(jScrollPane1, BorderLayout.CENTER);
		jPanel2.add(jPanel4, BorderLayout.SOUTH);
	}

//	// to test this gui
//	public static void main(String args[]) {
//		ChloracnegenResultsViewContainer ui = new ChloracnegenResultsViewContainer(new JFrame(), "");
//		ui.addStructures(new ArrayList<ChloracnegenBatchStructure>());
//		ui.showGUI();
//	}
}
