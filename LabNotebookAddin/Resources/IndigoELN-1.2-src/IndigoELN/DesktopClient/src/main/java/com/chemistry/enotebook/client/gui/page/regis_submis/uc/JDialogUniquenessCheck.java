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
package com.chemistry.enotebook.client.gui.page.regis_submis.uc;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.utils.CeNDialog;
import com.chemistry.enotebook.utils.SimpleJTable;
import com.chemistry.enotebook.vnv.classes.UcCompoundInfo;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class JDialogUniquenessCheck extends CeNDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 886738970908681536L;
	private SimpleJTable jTableCompoundList;
	private JPanel jPanelSpace2;
	private JScrollPane jScrollPaneList;
	private JLabel jLabelMsg;
	private JPanel jPanelSpace;
	private JButton jButtonCancel;
	private JButton jButtonOK;
	private JPanel jPanelCenter;
	private JPanel jPanelSouth;
	private JPanel jPanelNorth;
	private boolean canceled;

	public JDialogUniquenessCheck(Frame owner) {
		super(owner);
		initGUI();
		if (owner != null) {
			Point p = owner.getLocation();
			setLocation(p.x + (owner.getWidth() - getWidth()) / 2, p.y + (owner.getHeight() - getHeight()) / 2);
		}
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jPanelNorth = new JPanel();
			jPanelSpace2 = new JPanel();
			jLabelMsg = new JLabel();
			jPanelSouth = new JPanel();
			jButtonOK = new JButton();
			jPanelSpace = new JPanel();
			jButtonCancel = new JButton();
			jPanelCenter = new JPanel();
			jScrollPaneList = new JScrollPane();
			jTableCompoundList = new SimpleJTable();
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setTitle("Novelty Check on Parent Structure");
			this.setSize(new java.awt.Dimension(504, 298));
			BorderLayout jPanelNorthLayout = new BorderLayout();
			jPanelNorth.setLayout(jPanelNorthLayout);
			jPanelNorthLayout.setHgap(0);
			jPanelNorthLayout.setVgap(0);
			jPanelNorth.setPreferredSize(new java.awt.Dimension(497, 35));
			this.getContentPane().add(jPanelNorth, BorderLayout.NORTH);
			jPanelSpace2.setPreferredSize(new java.awt.Dimension(7, 39));
			jPanelNorth.add(jPanelSpace2, BorderLayout.WEST);
			BorderLayout jLabelMsgLayout = new BorderLayout();
			jLabelMsg.setLayout(jLabelMsgLayout);
			jLabelMsgLayout.setHgap(0);
			jLabelMsgLayout.setVgap(0);
			jLabelMsg.setPreferredSize(new java.awt.Dimension(490, 60));
			jLabelMsg.setMinimumSize(new java.awt.Dimension(0, 40));
			jLabelMsg.setSize(new java.awt.Dimension(490, 35));
			jPanelNorth.add(jLabelMsg, BorderLayout.CENTER);
			jPanelSouth.setPreferredSize(new java.awt.Dimension(536, 39));
			this.getContentPane().add(jPanelSouth, BorderLayout.SOUTH);
			jButtonOK.setText("OK");
			jButtonOK.setFont(new java.awt.Font("Dialog", 1, 12));
			jPanelSouth.add(jButtonOK);
			jButtonOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOKActionPerformed(evt);
				}
			});
			jPanelSpace.setVisible(true);
			jPanelSpace.setPreferredSize(new java.awt.Dimension(35, 10));
			jPanelSouth.add(jPanelSpace);
			jButtonCancel.setText("Cancel");
			jButtonCancel.setFont(new java.awt.Font("Dialog", 1, 12));
			jButtonCancel.setPreferredSize(new java.awt.Dimension(73, 26));
			jPanelSouth.add(jButtonCancel);
			jButtonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed(evt);
				}
			});
			BorderLayout jPanelCenterLayout = new BorderLayout();
			jPanelCenter.setLayout(jPanelCenterLayout);
			jPanelCenterLayout.setHgap(0);
			jPanelCenterLayout.setVgap(0);
			jPanelCenter.setPreferredSize(new java.awt.Dimension(536, 231));
			jPanelCenter.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			this.getContentPane().add(jPanelCenter, BorderLayout.CENTER);
			jScrollPaneList.setBackground(new java.awt.Color(204, 204, 204));
			jScrollPaneList.setPreferredSize(new java.awt.Dimension(493, 147));
			jScrollPaneList.setAlignmentX(0.5f);
			jScrollPaneList.setAlignmentY(0.5f);
			jScrollPaneList.setAutoscrolls(true);
			jPanelCenter.add(jScrollPaneList, BorderLayout.CENTER);
			jScrollPaneList.add(jTableCompoundList);
			jScrollPaneList.setViewportView(jTableCompoundList);
			{
				Object[][] data = new String[][] { { "0", "1" }, { "2", "3" } };
				Object[] columns = new String[] { "One", "Two" };
				javax.swing.table.TableModel dataModel = new javax.swing.table.DefaultTableModel(data, columns);
				jTableCompoundList.setModel(dataModel);
			}
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		this.setSize(new java.awt.Dimension(650, 550));
		ListSelectionModel rowSM = jTableCompoundList.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return; // Ignore extra messages.
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				jButtonOK.setEnabled(!lsm.isSelectionEmpty());
			}
		});
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
			JDialogUniquenessCheck inst = new JDialogUniquenessCheck(null);
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UniquenessCheckTableModel getModel() {
		return (UniquenessCheckTableModel) jTableCompoundList.getModel();
	}

	public void setModel(UniquenessCheckTableModel dataModel) {
		if (dataModel != null && dataModel.getUcResults() != null) {
			if (dataModel.getUcResults().size() == 1)
				jLabelMsg.setText(dataModel.getUcMessage());
			else
				jLabelMsg.setText("<html>" + dataModel.getUcMessage() + "<br>" + "Please select the desired structure.<html>");
			jTableCompoundList.setModel(dataModel);
			jTableCompoundList.setRowSelectionAllowed(true);
			jTableCompoundList.setColumnSelectionAllowed(false);
			JTableHeader th = jTableCompoundList.getTableHeader();
			th.setFont(new Font(th.getFont().getFontName(), Font.BOLD, th.getFont().getSize() + 2));
			TableColumnModel vColumModel = jTableCompoundList.getColumnModel();
			TableColumn column = vColumModel.getColumn(0);
			column.setCellRenderer(new UcStructAreaRenderer());
			column.setPreferredWidth(250);
			column = vColumModel.getColumn(1);
			column.setPreferredWidth(250);
			column.setCellRenderer(new UcPropertiesAreaRenderer());
			jTableCompoundList.setRowHeight(160);
			jTableCompoundList.setSelectionBackground(Color.white);
			jTableCompoundList.setSelectionForeground(Color.black);
			ArrayList<UcCompoundInfo> list = dataModel.getUcResults();
			if (list != null) {
				int i = 0;
				for (Iterator<UcCompoundInfo> it = list.iterator(); it.hasNext(); i++) {
					UcCompoundInfo ucRec = it.next();
					if (ucRec.isSelected()) {
						jButtonOK.setEnabled(true);
						jTableCompoundList.setRowSelectionInterval(i, i);
						break;
					}
				}
			}
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonOKActionPerformed(ActionEvent evt) {
		canceled = false;
		setVisible(false);
	}

	/** Auto-generated event handler method */
	protected void jButtonCancelActionPerformed(ActionEvent evt) {
		canceled = true;
		setVisible(false);
	}

	public UcCompoundInfo displayDialog(UniquenessCheckTableModel model) {
		UcCompoundInfo result = null;
		canceled = true;
		jButtonOK.setEnabled(false);
		setModel(model);
		setModal(true);
		setVisible(true);
		if (!canceled) {
			int row = jTableCompoundList.getSelectedRow();
			if (row >= 0) {
				result = (UcCompoundInfo) model.getValueAt(row, 0);
                if ("Drawn Structure".equalsIgnoreCase(result.getRegNumber())) {
                    result.setRegNumber("");
                }
            }
		}
		return result;
	}
}
