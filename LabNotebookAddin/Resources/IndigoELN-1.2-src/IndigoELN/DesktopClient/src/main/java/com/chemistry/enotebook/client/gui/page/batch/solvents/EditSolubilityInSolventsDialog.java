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
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.RegOperatorCache;
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.RegQualitativeCache;
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.RegUnitCache;
import com.chemistry.enotebook.domain.BatchRegInfoModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.batch.BatchSolubilitySolventModel;
import com.chemistry.enotebook.utils.CeNDialog;
import com.chemistry.enotebook.utils.CeNJTable;
import com.chemistry.enotebook.utils.CodeTableUtils;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditSolubilityInSolventsDialog  extends CeNDialog {

	private static final long serialVersionUID = -6903670815053932635L;
	
	private CeNJTable jTableSolubiltySolvents;
	private ProductBatchModel selectedBatch = new ProductBatchModel();
	private HashMap solubilitySolventMap = new HashMap();
	private SolubilitySolventTableModel solubilitySolventTableModel = new SolubilitySolventTableModel();
	private RegSubHandler regSubHandler;
	private String solubilityValueString = "";
	private ArrayList unitList = new ArrayList();
	private ArrayList operatorList = new ArrayList();
	private List<RegQualitativeCache> solventqualitativeList = new ArrayList<RegQualitativeCache>();
	private final String equalString = "=";
	private final String gtString = ">";
	private final String ltString = "<";
	private final String aproxiString = "~";
	protected int selectedSolubilityIndex = -1;
	private JFrame parent;
	private NotebookPageModel pageModel;
	private JButton addButton;
	private JButton cutButton;
	private JButton clearButton;
	private JButton quantitativeButton;
	private JButton qualitativeButton;
	private JButton requestNewButton;
	

	public EditSolubilityInSolventsDialog(JFrame owner, ProductBatchModel productBatchModel, NotebookPageModel pageModel) {
		super(owner);
		this.parent = owner;
		setSelectedBatch(productBatchModel);
		this.pageModel = pageModel;
		initGUI();
	}

	
	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			JPanel jPanelCenter = new JPanel();
			JPanel jPanelNorth = new JPanel();
			JPanel jPanelSouth = new JPanel();
			JPanel jPanelFiller = new JPanel();
			jTableSolubiltySolvents = new CeNJTable();
			JScrollPane jScrollPaneCenter = new JScrollPane(jTableSolubiltySolvents);
			JButton jButtonSave = new JButton();
			JButton jButtonCancel = new JButton();

			addButton = new JButton("Add"); 
			cutButton = new JButton("Cut");
			clearButton = new JButton("Clear");
			//quantitativeButton = new JButton("Set Quantitative Solubility");
			quantitativeButton = new JButton("Quantitative");
			//qualitativeButton = new JButton("Set Qualitative Solubility");
			qualitativeButton = new JButton("Qualitative");
			requestNewButton = new JButton("Request New");

			addButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					addSolubilityMouseClicked(arg0);
				}
			});
			cutButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					cutSolubilityMouseClicked(arg0);
				}
			});
			clearButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					clearSolubilityMouseClicked(arg0);
				}
			});
			quantitativeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setQuantitativeItemMouseClicked(arg0);
				}
			});
			qualitativeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setQualitativeItemMouseClicked(arg0);
				}
			});			
			requestNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					requestNewSolubilityMouseClicked(arg0);
				}
			});
			
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(addButton);
			buttonPanel.add(cutButton);
			buttonPanel.add(clearButton);
			buttonPanel.add(quantitativeButton);
			buttonPanel.add(qualitativeButton);
			buttonPanel.add(requestNewButton);
			
			BorderLayout thisLayout = new BorderLayout();
			getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			setTitle("Edit Solubility in Solvents Information");
			setResizable(false);
			setModal(true);
			setName("Edit Solubility in Solvents Information");
			setSize(new java.awt.Dimension(500, 250));

			BorderLayout jPanelCenterLayout = new BorderLayout();
			jPanelCenter.setLayout(jPanelCenterLayout);
			jPanelCenterLayout.setHgap(0);
			jPanelCenterLayout.setVgap(0);
			
			getContentPane().add(buttonPanel, BorderLayout.PAGE_START);
			getContentPane().add(jPanelCenter, BorderLayout.CENTER);
			jScrollPaneCenter.setPreferredSize(new java.awt.Dimension(449, 145));
			jPanelCenter.add(jScrollPaneCenter, BorderLayout.NORTH);
			jTableSolubiltySolvents.setRowHeight(18);
			jTableSolubiltySolvents.setPreferredSize(new java.awt.Dimension(449, 125));
			{
				Object[][] data = new String[][] { { "0", "1" }, { "2", "3" } };
				Object[] columns = new String[] { "One", "Two" };
				javax.swing.table.TableModel dataModel = new javax.swing.table.DefaultTableModel(data, columns);
				jTableSolubiltySolvents.setModel(dataModel);
			}
			jTableSolubiltySolvents.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jTableSolubiltySolventsMouseClicked(evt);
				}
			});
			jPanelNorth.add(jScrollPaneCenter, BorderLayout.CENTER);
			jPanelCenter.add(jPanelNorth, BorderLayout.NORTH);
			jPanelCenter.add(jPanelSouth, BorderLayout.SOUTH);
			jButtonSave.setText("Save");
			jButtonSave.setFont(new java.awt.Font("sansserif", 1, 11));
			jPanelSouth.add(jButtonSave);
			jButtonSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonSaveActionPerformed();
				}
			});
			jPanelFiller.setPreferredSize(new java.awt.Dimension(75, 10));
			jPanelSouth.add(jPanelFiller);
			jButtonCancel.setText("Cancel");
			jButtonCancel.setFont(new java.awt.Font("sansserif", 1, 11));
			jPanelSouth.add(jButtonCancel);
			jButtonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed();
				}
			});
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					EditSolubilityInSolventsDialog.this.setVisible(false);
					EditSolubilityInSolventsDialog.this.dispose();
				}

				public void windowClosed(WindowEvent e) {
					EditSolubilityInSolventsDialog.this.removeAll();
					EditSolubilityInSolventsDialog.this.setVisible(false);
				}
			});
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
		regSubHandler = new RegSubHandler();
		solubilitySolventMap = RegCodeMaps.getInstance().getSolubilitySolventMap();
		solventqualitativeList = CodeTableUtils.getQualitativeList();
		operatorList = RegCodeMaps.getInstance().getOperatorList();
		unitList = RegCodeMaps.getInstance().getUnitList();
	}
	
	public void postInitGUI() {
		jTableSolubiltySolvents.setModel(solubilitySolventTableModel);
		// populate drop downs
		populateDropDowns();
		// init solubilitySolventTableModel
		solubilitySolventTableModel.setUnitList(getUnitList());
		solubilitySolventTableModel.setOperatorList(getOperatorList());
		solubilitySolventTableModel.setSolventqualitativeList(getSolventqualitativeList());
		solubilitySolventTableModel.resetModelData();
		solubilitySolventTableModel.fireTableDataChanged();
		// reposition JDialog to the center of the screen
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = getPreferredSize();
		setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2 - (labelSize.height / 2));
		this.updateDisplay();
	}

	public void populateDropDowns() {
		// populate solubility solvent drop downs, need to set up the models
		JComboBox solubilityCombo = new JComboBox();
		CodeTableUtils.fillComboBoxWithSolubles(solubilityCombo);
		TableColumnModel solubilityColumModel = jTableSolubiltySolvents.getColumnModel();
		TableColumn solubilitycodeNameColumn = solubilityColumModel.getColumn(0);
		solubilitycodeNameColumn.setCellEditor(new DefaultCellEditor(solubilityCombo));
	}
	
	public void updateDisplay() {
		//need to check and set selected items in all the drop downs
		BatchRegInfoModel regInfo = selectedBatch.getRegInfo();
		//initialize solubilitySolventTableModel
		if (regInfo.getSolubilitySolventList().size() > 0) {
			solubilitySolventTableModel.setSolubilitySolventList(regInfo.getSolubilitySolventList());
			solubilitySolventTableModel.resetModelData();
			solubilitySolventTableModel.fireTableDataChanged();
		}
	}

	protected void jButtonSaveActionPerformed() {
		// need to validate source/source detail combination
		jTableSolubiltySolvents.stopEditing();
		ProductBatchModel batch = getSelectedBatch();
		BatchRegInfoModel regInfo = batch.getRegInfo();
		// Make sure Owner is uppercase, Empty string means use Chemist's ID
		if (!validateSolubilityInfo()) {
			JOptionPane.showMessageDialog(this, "Solubility value is required.");
		} else {
			// set up BatchRegistrationSolubilitySolvent list
			buildSolubilityVO();
			// do update
		// close
		setVisible(false);
		this.dispose();
		}
	}

	protected void jButtonCancelActionPerformed() {
		setVisible(false);
		this.dispose();
	}

	protected void jTableSolubiltySolventsMouseClicked(MouseEvent evt) {
		selectedSolubilityIndex = -1;
		JTable solubilitySolventsTable = (JTable) evt.getSource();
		selectedSolubilityIndex = solubilitySolventsTable.rowAtPoint(evt.getPoint());
		if (evt.getButton() == MouseEvent.BUTTON3) {
			processjTableSolubilitySolventsRightClicked(evt);
		}
	}
	
	protected void processjTableSolubilitySolventsRightClicked(MouseEvent evt) {
		JTable solubilitySolventsTable = (JTable) evt.getSource();
		Point p = evt.getPoint();
		selectedSolubilityIndex = solubilitySolventsTable.rowAtPoint(p);
		// construct the PopUpMenu
		JPopupMenu popUpMenu = new JPopupMenu();
		JMenuItem addItem = new JMenuItem("Add Solubility Entry");
		// addItem.addActionListener()
		addItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aevt) {
				addSolubilityMouseClicked(aevt);
			}
		});
		popUpMenu.add(addItem);
		JMenuItem cutItem = new JMenuItem("Cut Solubility Entry");
		cutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aevt) {
				cutSolubilityMouseClicked(aevt);
			}
		});
		popUpMenu.add(cutItem);
		JMenuItem clearItem = new JMenuItem("Clear Solubility Entry");
		clearItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aevt) {
				clearSolubilityMouseClicked(aevt);
			}
		});
		popUpMenu.add(clearItem);
		// add separator
		popUpMenu.addSeparator();
		JMenuItem setQuantitativeItem = new JMenuItem("Set Quantitative Solubility");
		setQuantitativeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aevt) {
				setQuantitativeItemMouseClicked(aevt);
			}
		});
		popUpMenu.add(setQuantitativeItem);
		JMenuItem setQualitativeItem = new JMenuItem("Set Qualitative Solubility");
		setQualitativeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aevt) {
				setQualitativeItemMouseClicked(aevt);
			}
		});
		popUpMenu.add(setQualitativeItem);
		// add separator
		popUpMenu.addSeparator();
		JMenuItem requestNewItem = new JMenuItem("Request New Solvent");
		requestNewItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aevt) {
				requestNewSolubilityMouseClicked(aevt);
			}
		});
		popUpMenu.add(requestNewItem);
		if (selectedSolubilityIndex == -1) {
			setQuantitativeItem.setEnabled(false);
			setQualitativeItem.setEnabled(false);
		}
		popUpMenu.show(solubilitySolventsTable, p.x + 1, p.y - 6);
	}

	/**
	 * @return Returns the unitList.
	 */
	public ArrayList getUnitList() {
		return unitList;
	}
	
	/**
	 * @return Returns the operatorList.
	 */
	public ArrayList getOperatorList() {
		return operatorList;
	}
	
	/**
	 * @return Returns the solventDescList.
	 */
	public List<RegQualitativeCache> getSolventqualitativeList() {
		return solventqualitativeList;
	}


	public ProductBatchModel getSelectedBatch() {
		return selectedBatch;
	}


	public void setSelectedBatch(ProductBatchModel selectedBatch) {
		this.selectedBatch = selectedBatch;
	}

	private boolean validateSolubilityInfo() {
		boolean isValid = true;
		int solubilitySolventListSize = solubilitySolventTableModel.getCodeAndNameList().size();
		for (int i = 0; i < solubilitySolventListSize && isValid; i++) {
			if (((String) (solubilitySolventTableModel.getValueList().get(i))).trim().length() <= 0)
				isValid = false;
		}
		return isValid;
	}

	/**
	 * 
	 */
	private void buildSolubilityVO() {
		getSelectedBatch().getRegInfo().getSolubilitySolventList().clear();
		int solubilitySolventListSize = solubilitySolventTableModel.getCodeAndNameList().size();
		for (int i = 0; i < solubilitySolventListSize; i++) {
			BatchSolubilitySolventModel solubilitySolventVO = new BatchSolubilitySolventModel();
			String valueString = (String) solubilitySolventTableModel.getValueList().get(i);
			if (valueString.startsWith(equalString) || valueString.startsWith(gtString) || valueString.startsWith(ltString)
					|| valueString.startsWith(aproxiString)) {
				String[] valueComp = valueString.split(" ");
				solubilitySolventVO.setOperator(getOperatorCodeFromDesr(valueComp[0].trim()));
				solubilitySolventVO.setSolubilityValue(new Double(valueComp[1]).doubleValue());
				solubilitySolventVO.setSolubilityUnit(getUnitCodeFromDesr(valueComp[2].trim()));
				solubilitySolventVO.setQuantitative(true);
				solubilitySolventVO.setQualitative(false);
			} else {
				solubilitySolventVO.setQualitative(true);
				solubilitySolventVO.setQuantitative(false);
				solubilitySolventVO.setQualiString(getQualitativeCodeFromDesr(valueString));
			}
			solubilitySolventVO.setComments((String) solubilitySolventTableModel.getCommentsList().get(i));
			solubilitySolventVO.setCodeAndName(getSolubilityCodeFromDecr((String) solubilitySolventTableModel.getCodeAndNameList()
					.get(i)));
			getSelectedBatch().getRegInfo().addSolubilitySolvent(solubilitySolventVO);
			
			getSelectedBatch().setModelChanged(true);
			getSelectedBatch().getRegInfo().setModelChanged(true);
		}
		pageModel.setModelChanged(true);
	}
	
	public String getOperatorCodeFromDesr(String unitDesr) {
		String unitCode = new String();
		RegOperatorCache regOperatorCache = new RegOperatorCache();
		for (int i = 0; i < getOperatorList().size(); i++) {
			regOperatorCache = (RegOperatorCache) getOperatorList().get(i);
			if (unitDesr.equals(regOperatorCache.getDescription().trim())) {
				unitCode = regOperatorCache.getCode().trim();
				break;
			}
		}
		return unitCode;
	}
	
	public String getQualitativeCodeFromDesr(String unitDesr) {
		String unitCode = new String();
		RegQualitativeCache regQualitativeCache = new RegQualitativeCache();
		for (int i = 0; i < getSolventqualitativeList().size(); i++) {
			regQualitativeCache = (RegQualitativeCache) getSolventqualitativeList().get(i);
			if (unitDesr.equals(regQualitativeCache.getDescription().trim())) {
				unitCode = regQualitativeCache.getCode().trim();
				break;
			}
		}
		return unitCode;
	}
	
	/** Auto-generated event handler method */
	protected void addSolubilityMouseClicked(ActionEvent evt) {
		solubilitySolventTableModel.addModelData();
		solubilitySolventTableModel.fireTableDataChanged();
	}

	/** Auto-generated event handler method */
	protected void cutSolubilityMouseClicked(ActionEvent evt) {
		updateSelectedIndex();
		if (selectedSolubilityIndex == -1)// If button is clicked. (Not right click on the table)
			selectedSolubilityIndex = jTableSolubiltySolvents.getSelectedRow();
		if (selectedSolubilityIndex > -1)
		{
			solubilitySolventTableModel.cutModelData(selectedSolubilityIndex);
			solubilitySolventTableModel.fireTableDataChanged();
			jTableSolubiltySolvents.validate();
		}
	}

	/** Auto-generated event handler method */
	protected void clearSolubilityMouseClicked(ActionEvent evt) {
		solubilitySolventTableModel.clearModelData();
		solubilitySolventTableModel.fireTableDataChanged();
	}

	/** Auto-generated event handler method */
	protected void requestNewSolubilityMouseClicked(ActionEvent evt) {
		JOptionPane.showMessageDialog(this, "This service is not yet available.\n"
				+ "Please call the help desk to request a new solvent be added to the list.");
	}

	/**
	 * @return Returns the solubilityValueString.
	 */
	public String getSolubilityValueString() {
		return solubilityValueString;
	}

	/**
	 * @param solubilityValueString
	 *            The solubilityValueString to set.
	 */
	public void setSolubilityValueString(String solubilityValueString) {
		this.solubilityValueString = solubilityValueString;
	}
	
	public void resetSolubilityValue() {
		solubilitySolventTableModel.setSolubilityValue(selectedSolubilityIndex, getSolubilityValueString());
		solubilitySolventTableModel.fireTableDataChanged();
	}

	protected void setQuantitativeItemMouseClicked(ActionEvent evt) {
		updateSelectedIndex();
		if (selectedSolubilityIndex == -1) {
			JOptionPane.showMessageDialog(this, "Please select a solvent first.");
		} else {
			QuantitativeSolubilityDialog quantitativeSolubilityJDialog = new QuantitativeSolubilityDialog(this);
			quantitativeSolubilityJDialog.setModal(true);
			quantitativeSolubilityJDialog.setOperatorList(getOperatorList());
			quantitativeSolubilityJDialog.setUnitList(getUnitList());
			quantitativeSolubilityJDialog.resetSolubilityValuePanelLayout();
			quantitativeSolubilityJDialog.setVisible(true);
		}
	}
	
	protected void setQualitativeItemMouseClicked(ActionEvent evt) {
		updateSelectedIndex();
		if (selectedSolubilityIndex == -1) {
			JOptionPane.showMessageDialog(this, "Please select a solvent first.");
		} else {
			QualitativeSolubilityDialog qualitativeSolubilityJDialog = new QualitativeSolubilityDialog(this);
			qualitativeSolubilityJDialog.setModal(true);
			qualitativeSolubilityJDialog.setSolventDescList(getSolventqualitativeList());
			qualitativeSolubilityJDialog.resetSolubilityValuePanelLayout();
			qualitativeSolubilityJDialog.setVisible(true);
		}
	}

	public String getUnitCodeFromDesr(String unitDesr) {
		String unitCode = new String();
		RegUnitCache regUnitCache = new RegUnitCache();
		for (int i = 0; i < getUnitList().size(); i++) {
			regUnitCache = (RegUnitCache) getUnitList().get(i);
			if (unitDesr.equals(regUnitCache.getDescription().trim())) {
				unitCode = regUnitCache.getCode().trim();
				break;
			}
		}
		return unitCode;
	}

	private String getSolubilityCodeFromDecr(String decr) {
		String codeString = "";
		try {
			codeString = CodeTableCache.getCache().getSolubilitySolventCode(decr);
		} catch (CodeTableCacheException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, "Look up code failed.", e);
		}
		return codeString;
	}

	private void updateSelectedIndex() {
		selectedSolubilityIndex = jTableSolubiltySolvents.getSelectedRow();
	}
}
