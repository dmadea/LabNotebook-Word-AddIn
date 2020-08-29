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
package com.chemistry.enotebook.client.gui.page.procedure;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.ParallelNotebookPageGUI;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.ProductPlate;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class JDialogAddBatchLotProc extends javax.swing.JDialog implements ListSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1643436439252662430L;
	private JList jListBatchList;
	private JScrollPane jScrollPaneBatchList;
	private JPanel jPanelCenter;
	private JButton jButtonCancel;
	private JPanel jPanelSplitter;
	private JButton jButtonOk;
	private JPanel jPanelSouth;
	private JLabel jLabelBatchEntry;
	private NotebookPageModel pageModel = null;
	private List batchListModel = null;
	private boolean dia_cancelled = false;
	private boolean batchFlag = false;
	private String itemString = "Batch";

	public JDialogAddBatchLotProc() {
		initGUI();
	}

	public JDialogAddBatchLotProc(JFrame arg0, NotebookPageModel _pageModel, boolean batchFlag) {
		super(arg0);
		pageModel = _pageModel;
		this.batchFlag = batchFlag; 
		if (!batchFlag)
			itemString = "Plate"; 
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jPanelSouth = new JPanel();
			jButtonOk = new JButton();
			jPanelSplitter = new JPanel();
			jButtonCancel = new JButton();
			jPanelCenter = new JPanel();
			jLabelBatchEntry = new JLabel();
			jScrollPaneBatchList = new JScrollPane();
			jListBatchList = new JList();
			jListBatchList.addListSelectionListener(this);
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setTitle("Insert Nbk" + itemString + " Entry");
			this.setUndecorated(false);
			this.setModal(true);
			this.setSize(new java.awt.Dimension(393, 156));
			this.setResizable(false);
			jPanelSouth.setPreferredSize(new java.awt.Dimension(386, 35));
			this.getContentPane().add(jPanelSouth, BorderLayout.SOUTH);
			jButtonOk.setText("Insert " + itemString);
			jButtonOk.setFont(new java.awt.Font("sansserif", 1, 11));
			jButtonOk.setPreferredSize(new java.awt.Dimension(102, 25));
			jPanelSouth.add(jButtonOk);
			jButtonOk.setEnabled(false);
			jButtonOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOkActionPerformed(evt);
				}
			});
			jPanelSplitter.setPreferredSize(new java.awt.Dimension(62, 10));
			jPanelSouth.add(jPanelSplitter);
			jButtonCancel.setText("Cancel");
			jButtonCancel.setFont(new java.awt.Font("sansserif", 1, 11));
			jPanelSouth.add(jButtonCancel);
			jButtonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed(evt);
				}
			});
			jPanelCenter.setLayout(null);
			jPanelCenter.setPreferredSize(new java.awt.Dimension(386, 94));
			this.getContentPane().add(jPanelCenter, BorderLayout.CENTER);
			jLabelBatchEntry.setText("Add " + itemString + " Entry:");
			jLabelBatchEntry.setFont(new java.awt.Font("sansserif", 1, 11));
			jLabelBatchEntry.setPreferredSize(new java.awt.Dimension(123, 22));
			jLabelBatchEntry.setBounds(new java.awt.Rectangle(16, 3, 123, 22));
			jPanelCenter.add(jLabelBatchEntry);
			jScrollPaneBatchList.setPreferredSize(new java.awt.Dimension(363, 59));
			jScrollPaneBatchList.setBounds(new java.awt.Rectangle(12, 28, 363, 59));
			jPanelCenter.add(jScrollPaneBatchList);
			jScrollPaneBatchList.add(jListBatchList);
			jScrollPaneBatchList.setViewportView(jListBatchList);
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dia_cancelled = true;
					JDialogAddBatchLotProc.this.setVisible(false);
					JDialogAddBatchLotProc.this.dispose();
				}

				public void windowClosed(WindowEvent e) {
					JDialogAddBatchLotProc.this.removeAll();
					JDialogAddBatchLotProc.this.setVisible(false);
				}
			});
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		refresh_This();
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
			JDialogAddBatchLotProc inst = new JDialogAddBatchLotProc();
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonOkActionPerformed(ActionEvent evt) {
		this.setVisible(false);
	}

	/** Auto-generated event handler method */
	protected void jButtonCancelActionPerformed(ActionEvent evt) {
		dia_cancelled = true;
		this.dispose();
	}

	public String getSelectedBatch() {
		if (!jListBatchList.isSelectionEmpty()) {
			return (String) jListBatchList.getModel().getElementAt(jListBatchList.getSelectedIndex());
		} else {
			return "";
		}
	}

	/**
	 * @return Returns the dia_cancelled.
	 */
	public boolean isDia_cancelled() {
		return dia_cancelled;
	}

	/** Auto-generated event handler method */
	protected void NewBatchJButtonActionPerformed(ActionEvent evt) {
		// need to insert a new batch, plus update gui
		ParallelNotebookPageGUI fred = (ParallelNotebookPageGUI) MasterController.getGUIComponent().getJDesktopPane1()
				.getSelectedFrame();
		//fred.getBatchInfo_cont().addBatch();
		refresh_This();
	}

	/**
	 * Small class that compare objects for batchListModel list sort<br>
	 * Compares only by numbers (BatchNumber or LotNo)
	 */
	private class BatchListModelComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			if (o1 instanceof ProductBatchModel && o2 instanceof ProductBatchModel) {
				BatchNumber firstBatchNumber = ((ProductBatchModel)o1).getBatchNumber();
				BatchNumber secondBatchNumber = ((ProductBatchModel)o2).getBatchNumber();
				return firstBatchNumber.compareTo(secondBatchNumber);
			} else if (o1 instanceof ProductPlate && o2 instanceof ProductPlate) {
				String firstLotNo = ((ProductPlate)o1).getLotNo();
				String secondLotNo = ((ProductPlate)o2).getLotNo();
				return firstLotNo.compareTo(secondLotNo);
			}
			return 0;
		}
	}
	
	private void refresh_This() {
		if (batchFlag) {
			// batchListModel = pageModel.getAllProductBatchModelsInThisPage(BatchType.ACTUAL_PRODUCT);
			batchListModel = pageModel.getAllProductBatchModelsInThisPage();
		} else {
			batchListModel = pageModel.getAllProductPlatesAndRegPlates();
		}
		// Sort list ONLY by numbers (BatchNumber or LotNo)
		Collections.sort(batchListModel, new BatchListModelComparator());
		DefaultListModel tempMod = new DefaultListModel();
		for (Object object : batchListModel) {
			if (batchFlag) {
				ProductBatchModel batch = (ProductBatchModel) object;
				tempMod.addElement(batch.getBatchNumberAsString());
			} else {
				ProductPlate plate = (ProductPlate) object;
				tempMod.addElement(plate.getLotNo());
			}
		}
		jListBatchList.setModel(tempMod);
	}

	public void dispose() {
		this.pageModel = null;
		try {
			jScrollPaneBatchList.remove(jListBatchList);
//			this.jListBatchList = new JList();
			this.jListBatchList = null;
//			this.dispose();
		} catch (Throwable e) {
			//e.printStackTrace();
		}
		super.dispose();
	}

	public void valueChanged(ListSelectionEvent arg0) {
		if (jListBatchList.getSelectedIndex() > -1)
			jButtonOk.setEnabled(true);
		else
			jButtonOk.setEnabled(false);
	}
}
