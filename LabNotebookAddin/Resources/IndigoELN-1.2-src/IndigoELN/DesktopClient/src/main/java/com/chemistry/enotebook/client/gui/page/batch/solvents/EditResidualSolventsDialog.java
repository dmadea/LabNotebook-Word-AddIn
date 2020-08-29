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
package com.chemistry.enotebook.client.gui.page.batch.solvents;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.common.utils.JTextFieldFilter;
import com.chemistry.enotebook.domain.BatchRegInfoModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.batch.BatchResidualSolventModel;
import com.chemistry.enotebook.utils.CeNDialog;
import com.chemistry.enotebook.utils.CeNJTable;
import com.chemistry.enotebook.utils.CodeTableUtils;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditResidualSolventsDialog extends CeNDialog {

	private static final long serialVersionUID = -8872788594143658208L;

	protected int selectedResidualIndex = -1;

	private ProductBatchModel selectedBatch;
	private NotebookPageModel pageModel;

	private ResidualSolventTableModel residualSolventTableModel = new ResidualSolventTableModel();
	private CeNJTable jTableResidualSolvents;
	private JButton addButton;
	private JButton cutButton;
	private JButton clearButton;
	private JButton requestNewButton;

	public EditResidualSolventsDialog(JFrame owner, ProductBatchModel productBatchModel, NotebookPageModel pageModel) {
		super(owner);
		setSelectedBatch(productBatchModel);
		this.pageModel = pageModel;
		initGUI();
		setLocationRelativeTo(owner);
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will
	 * disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			
			JPanel jPanelSouth = new JPanel();
			JPanel jPanelFiller = new JPanel();
			
			jTableResidualSolvents = new CeNJTable();
			JScrollPane jScrollPaneCenter = new JScrollPane(jTableResidualSolvents);
			
			addButton = new JButton("    Add    ");
			cutButton = new JButton("    Cut    ");
			clearButton = new JButton("  Clear  ");
			requestNewButton = new JButton("Request New");
			
			JButton jButtonSave = new JButton();
			JButton jButtonCancel = new JButton();
			
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(addButton);
			buttonPanel.add(cutButton);
			buttonPanel.add(clearButton);
			buttonPanel.add(requestNewButton);

			addButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					addItemMouseClicked(arg0);
				}
			});
			
			cutButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					cutItemMouseClicked(arg0);
				}
			});
			
			clearButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					clearItemMouseClicked(arg0);
				}
			});
			
			requestNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					requestNewItemMouseClicked(arg0);
				}
			});
			
			jTableResidualSolvents.setRowHeight(18);
			jTableResidualSolvents.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jTableResidualSolventsMouseClicked(evt);
				}
			});

			jButtonSave.setText("Save");
			jPanelSouth.add(jButtonSave);
			jButtonSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonSaveActionPerformed();
				}
			});

			jPanelSouth.add(jPanelFiller);
			jButtonCancel.setText("Cancel");
			jPanelSouth.add(jButtonCancel);
			jButtonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed();
				}
			});
			
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonSave);
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonCancel);

			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(buttonPanel, BorderLayout.NORTH);
			getContentPane().add(jScrollPaneCenter, BorderLayout.CENTER);
			getContentPane().add(jPanelSouth, BorderLayout.SOUTH);
			
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	protected void defaultApplyAction() {
		jButtonSaveActionPerformed();
	}

	protected void defaultCancelAction() {
		jButtonCancelActionPerformed();
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {

	}

	public void postInitGUI() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Edit Residual Solvents Information");
		setResizable(false);
		setModal(true);
		setName("Edit Residual Solvents Information");
		setSize(new java.awt.Dimension(500, 250));
		
		jTableResidualSolvents.setModel(residualSolventTableModel);
		// populate drop downs
		populateDropDowns();
		// init residualSolventTableModel
		residualSolventTableModel.resetModelData();
		residualSolventTableModel.fireTableDataChanged();
		
		this.updateDisplay();
	}

	public void populateDropDowns() {
		// populate residual solvent drop downs, need to set up the models
		JComboBox residualSolventCombo = new JComboBox();
		CodeTableUtils.fillComboBoxWithResiduals(residualSolventCombo);
		TableColumnModel tColumModel = jTableResidualSolvents.getColumnModel();
		TableColumn codeNameColumn = tColumModel.getColumn(0);
		codeNameColumn.setCellEditor(new DefaultCellEditor(residualSolventCombo));
		// setup value column as textField with filter
		JTextField jTextFieldValue = new JTextField();
		jTextFieldValue.setText("");
		jTextFieldValue.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
		TableColumnModel vColumModel = jTableResidualSolvents.getColumnModel();
		TableColumn vColumn = vColumModel.getColumn(1);
		vColumn.setCellEditor(new DefaultCellEditor(jTextFieldValue));
	}

	public void updateDisplay() {
		// need to check and set selected items in all the drop downs
		BatchRegInfoModel regInfo = selectedBatch.getRegInfo();
		// initialize residualSolventTableModel
		if (regInfo.getResidualSolventList().size() > 0) {
			residualSolventTableModel.setResidualSolventVOList(regInfo.getResidualSolventList());
			residualSolventTableModel.resetModelData();
			residualSolventTableModel.fireTableDataChanged();
		}

	}

	protected void jTableResidualSolventsMouseClicked(MouseEvent evt) {
		if (evt.getButton() == MouseEvent.BUTTON3) {
			processjTableResidualSolventsRightClicked(evt);
		}
	}

	protected void processjTableResidualSolventsRightClicked(MouseEvent evt) {
		selectedResidualIndex = -1;
		JTable residualSolventsTable = (JTable) evt.getSource();
		Point p = evt.getPoint();
		selectedResidualIndex = residualSolventsTable.rowAtPoint(p);
		// construct the PopUpMenu
		JPopupMenu popUpMenu = new JPopupMenu();
		JMenuItem addItem = new JMenuItem("Add Residual Solvent Entry");
		// addItem.addActionListener()
		addItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aevt) {
				addItemMouseClicked(aevt);
			}
		});
		popUpMenu.add(addItem);
		JMenuItem cutItem = new JMenuItem("Cut Residual Solvent Entry");
		cutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aevt) {
				cutItemMouseClicked(aevt);
			}
		});
		popUpMenu.add(cutItem);
		JMenuItem clearItem = new JMenuItem("Clear Residual Solvent Entry");
		clearItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aevt) {
				clearItemMouseClicked(aevt);
			}
		});
		popUpMenu.add(clearItem);
		// add separator
		popUpMenu.addSeparator();
		JMenuItem requestNewItem = new JMenuItem("Request New Solvent");
		requestNewItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aevt) {
				requestNewItemMouseClicked(aevt);
			}
		});
		popUpMenu.add(requestNewItem);
		popUpMenu.show(residualSolventsTable, p.x + 1, p.y - 6);
	}

	protected void jButtonSaveActionPerformed() {
		// need to validate source/source detail combination
		jTableResidualSolvents.stopEditing();

		// Make sure Owner is uppercase, Empty string means use Chemist's ID
		if (!validateResidualInfo()) {
			JOptionPane.showMessageDialog(this, "#EQ of Solvent for Residual Solvent must be greater than zero.");
		} else {
			// set up BatchRegistrationSolubilitySolvent list
			buildResidualVO();
			// do updates
			// close
			setVisible(false);
			this.dispose();
		}
	}

	protected void jButtonCancelActionPerformed() {
		setVisible(false);
		this.dispose();
	}

	/** Auto-generated event handler method */
	protected void addItemMouseClicked(ActionEvent evt) {
		residualSolventTableModel.addModelData();
		residualSolventTableModel.fireTableDataChanged();
	}

	/** Auto-generated event handler method */
	protected void cutItemMouseClicked(ActionEvent evt) {
		selectedResidualIndex = jTableResidualSolvents.getSelectedRow();
		if (selectedResidualIndex > -1) {
			jTableResidualSolvents.stopEditing();
			residualSolventTableModel.cutModelData(selectedResidualIndex);
			residualSolventTableModel.fireTableDataChanged();
			jTableResidualSolvents.validate();
		}
	}

	/** Auto-generated event handler method */
	protected void clearItemMouseClicked(ActionEvent evt) {
		jTableResidualSolvents.stopEditing();
		residualSolventTableModel.clearModelData();
		residualSolventTableModel.fireTableDataChanged();
	}

	/** Auto-generated event handler method */
	protected void requestNewItemMouseClicked(ActionEvent evt) {
		JOptionPane.showMessageDialog(this, "This service is not yet available.\nPlease call the help desk to request a new solvent be added to the list.");
	}

	private boolean validateResidualInfo() {
		boolean isValid = true;
		int residualSolventListSize = residualSolventTableModel.getCodeAndNameList().size();
		for (int i = 0; i < residualSolventListSize && isValid; i++) {
			if (((String) (residualSolventTableModel.getEqList().get(i))).trim().length() <= 0)
				isValid = false;
			else {
				String value = ((String) (residualSolventTableModel.getEqList().get(i))).trim();
				double doubleValue = Double.valueOf(value).doubleValue();
				if (doubleValue <= 0)
					isValid = false;
			}
		}
		return isValid;
	}

	/**
	 * 
	 */
	private void buildResidualVO() {
		int oldListSize = getSelectedBatch().getRegInfo().getResidualSolventList().size();
		getSelectedBatch().getRegInfo().getResidualSolventList().clear();
		int residualSolventListSize = residualSolventTableModel.getCodeAndNameList().size();
		for (int i = 0; i < residualSolventListSize; i++) {
			BatchResidualSolventModel residualSolventVO = new BatchResidualSolventModel();
			residualSolventVO.setCodeAndName(getResidualCodeFromDecr((String) residualSolventTableModel.getCodeAndNameList().get(i)));
			residualSolventVO.setComments((String) residualSolventTableModel.getCommentsList().get(i));
			residualSolventVO.setEqOfSolvent((new Double((String) (residualSolventTableModel.getEqList().get(i)))).doubleValue());
			getSelectedBatch().getRegInfo().addResidualSolvent(residualSolventVO);
		}
		// Force Recalc since Residual Solvent affects Batch MolWgt
		if (oldListSize > 0 || residualSolventListSize > 0) {
			getSelectedBatch().recalcAmounts();
			getSelectedBatch().getRegInfo().setModified(true);
			getSelectedBatch().setModelChanged(true);
		}
		pageModel.setModelChanged(true);
	}

	/**
	 * @return Returns the selectedBatch.
	 */
	public ProductBatchModel getSelectedBatch() {
		return selectedBatch;
	}

	public void setSelectedBatch(ProductBatchModel selectedBatch) {
		this.selectedBatch = selectedBatch;
	}

	private String getResidualCodeFromDecr(String decr) {
		String codeString = "";
		try {
			codeString = CodeTableCache.getCache().getResidualSolventCode(decr);
		} catch (CodeTableCacheException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, "Look up code failed.", e);
		}
		return codeString;
	}
}
