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
package com.chemistry.enotebook.client.gui.preferences.stoic;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class ColumnDisplayPrefPanel extends javax.swing.JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8815886622198443930L;
	private StoicColumnPrefViewer stoicColumnPrefViewer;
	private JScrollPane jScrollPane1;
	private JButton jButton2;
	private JButton jButton1;
	private JPanel jPanelButtons;
	private JPanel jPanelColumnPrefTable;
	private JLabel jLabel1;
	private JPanel jPanelColumnPref;
	private JScrollPane jScrollPaneColumnPref;

	public ColumnDisplayPrefPanel() {
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jScrollPaneColumnPref = new JScrollPane();
			jPanelColumnPref = new JPanel();
			jLabel1 = new JLabel();
			jPanelColumnPrefTable = new JPanel();
			jScrollPane1 = new JScrollPane();
			stoicColumnPrefViewer = new StoicColumnPrefViewer();
			jPanelButtons = new JPanel();
			jButton1 = new JButton();
			jButton2 = new JButton();
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setPreferredSize(new java.awt.Dimension(400, 200));
			this.add(jScrollPaneColumnPref, BorderLayout.CENTER);
			BorderLayout jPanelColumnPrefLayout = new BorderLayout();
			jPanelColumnPref.setLayout(jPanelColumnPrefLayout);
			jPanelColumnPrefLayout.setHgap(0);
			jPanelColumnPrefLayout.setVgap(0);
			jPanelColumnPref.setPreferredSize(new java.awt.Dimension(390, 190));
			jScrollPaneColumnPref.add(jPanelColumnPref);
			jScrollPaneColumnPref.setViewportView(jPanelColumnPref);
			jLabel1.setText("1. Display Order, Column Display Name, and Property Setups");
			jPanelColumnPref.add(jLabel1, BorderLayout.NORTH);
			BorderLayout jPanelColumnPrefTableLayout = new BorderLayout();
			jPanelColumnPrefTable.setLayout(jPanelColumnPrefTableLayout);
			jPanelColumnPrefTableLayout.setHgap(0);
			jPanelColumnPrefTableLayout.setVgap(0);
			jPanelColumnPrefTable.setPreferredSize(new java.awt.Dimension(477, 265));
			jPanelColumnPref.add(jPanelColumnPrefTable, BorderLayout.WEST);
			jPanelColumnPrefTable.add(jScrollPane1, BorderLayout.CENTER);
			jScrollPane1.add(stoicColumnPrefViewer);
			jScrollPane1.setViewportView(stoicColumnPrefViewer);
			jPanelColumnPref.add(jPanelButtons, BorderLayout.CENTER);
			jButton1.setText("Up");
			jPanelButtons.add(jButton1);
			jButton1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton1ActionPerformed(evt);
				}
			});
			jButton2.setText("Dn");
			jPanelButtons.add(jButton2);
			jButton2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton2ActionPerformed(evt);
				}
			});
			postInitGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		jButton1.setEnabled(false);
		jButton2.setEnabled(false);
		stoicColumnPrefViewer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		stoicColumnPrefViewer.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				rowSelectionChanged();
			}
		});
	}

	/** Auto-generated main method */
	public static void main(String[] args) {
		showGUI();
	}

	/**
	 * This static method creates a new instance of this class and shows it inside a new JFrame, (unless it is already a JFrame).
	 * 
	 * It is a convenience method for showing the GUI, but it can be copied and used as a basis for your own code. * It is
	 * auto-generated code - the body of this method will be re-generated after any changes are made to the GUI. However, if you
	 * delete this method it will not be re-created.
	 */
	public static void showGUI() {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			ColumnDisplayPrefPanel inst = new ColumnDisplayPrefPanel();
			frame.setContentPane(inst);
			frame.getContentPane().setSize(inst.getSize());
			frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Auto-generated event handler method */
	protected void jButton1ActionPerformed(ActionEvent evt) {
		int i = stoicColumnPrefViewer.getSelectedRow();
		if (i != -1) {
			ArrayList al_OriginalData = ((StoicColumnPrefTableModel) stoicColumnPrefViewer.getModel()).getData();
			Object temp2 = al_OriginalData.remove(i);
			Object temp1 = al_OriginalData.remove(i - 1);
			al_OriginalData.add(i - 1, temp2);
			al_OriginalData.add(i, temp1);
			((StoicColumnPrefTableModel) stoicColumnPrefViewer.getModel()).fireTableDataChanged();
			stoicColumnPrefViewer.setRowSelectionInterval(i - 1, i - 1);
		}
	}

	/** Auto-generated event handler method */
	protected void jButton2ActionPerformed(ActionEvent evt) {
		int i = stoicColumnPrefViewer.getSelectedRow();
		if (i != -1) {
			ArrayList al_OriginalData = ((StoicColumnPrefTableModel) stoicColumnPrefViewer.getModel()).getData();
			Object temp2 = al_OriginalData.remove(i + 1);
			Object temp1 = al_OriginalData.remove(i);
			al_OriginalData.add(i, temp2);
			al_OriginalData.add(i + 1, temp1);
			((StoicColumnPrefTableModel) stoicColumnPrefViewer.getModel()).fireTableDataChanged();
			stoicColumnPrefViewer.setRowSelectionInterval(i + 1, i + 1);
		}
	}

	/**
	 * 
	 */
	protected void rowSelectionChanged() {
		// TODO Auto-generated method stub
		if ((stoicColumnPrefViewer.getRowCount() - 1) > 1) {
			int i = stoicColumnPrefViewer.getSelectedRow();
			if (i == (stoicColumnPrefViewer.getRowCount() - 1)) {
				jButton1.setEnabled(true);
				jButton2.setEnabled(false);
			} else if (i == 0) {
				jButton1.setEnabled(false);
				jButton2.setEnabled(true);
			} else {
				jButton1.setEnabled(true);
				jButton2.setEnabled(true);
			}
		}
	}
}
