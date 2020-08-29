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
package com.chemistry.enotebook.client.gui.page.regis_submis;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.gui.common.utils.JTextFieldFilter;
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.RegOperatorCache;
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.RegQualitativeCache;
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.RegUnitCache;
import com.chemistry.enotebook.domain.BatchRegInfoModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.batch.BatchResidualSolventModel;
import com.chemistry.enotebook.domain.batch.BatchSolubilitySolventModel;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.utils.CeNDialog;
import com.chemistry.enotebook.utils.CeNJTable;
import com.chemistry.enotebook.utils.CodeTableUtils;
import com.cloudgarden.layout.AnchorLayout;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class JDialogEditCompRegInfo extends CeNDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4962847063388314025L;
	private JScrollPane jScrollPaneNorth;
	private JButton jButtonColor;
	private JTextField HitID;
	private JLabel jLabelHitID;
	private CeNComboBox jComboBoxSourceDetail;
	private JLabel jLabelSourceDetail;
	private CeNComboBox jComboBoxCompoundSrc;
	private JLabel jLabelCompoundSrc;
	private JTextField BatchLotID;
	private JLabel jLabelBatchLotID;
	private JPanel jPanelWestTop;
	private JPanel jPanelWestNorth;
	private JPanel jPanelfiller;
	private JButton jButtonCancel;
	private JButton jButtonSave;
	private JPanel jPanelSouth;
	private CeNJTable jTableSolubiltySolvents;
	private JScrollPane jScrollPaneCenter;
	private CeNJTable jTableResidualSolvents;
	private JPanel jPanelCenter;
	private JPanel jPanelWest;
	private JTextField jTextFieldOwner;
	private JLabel jLabelOwner;
	private JButton jButtonOwnerCheck;
	// private BatchRegistrationInfo registrationInfoVO = new
	// BatchRegistrationInfo();
	private ProductBatchModel selectedBatch = new ProductBatchModel();
	private RegSubHandler regSubHandler;
	private ArrayList compoundSourceList = new ArrayList();
	private ArrayList sourceAndDetailList = new ArrayList();
	private HashMap solubilitySolventMap = new HashMap();
	private HashMap residualSolventMap = new HashMap();
	private ResidualSolventTableModel residualSolventTableModel = new ResidualSolventTableModel();
	private SolubilitySolventTableModel solubilitySolventTableModel = new SolubilitySolventTableModel();
	private String solubilityValueString = "";
	private ArrayList unitList = new ArrayList();
	private ArrayList operatorList = new ArrayList();
	private ArrayList solventqualitativeList = new ArrayList();
	private final String equalString = "=";
	private final String gtString = ">";
	private final String ltString = "<";
	private final String aproxiString = "~";
	private CompoundRegInfoContainer compoundRegInfoContainer;
	protected int selectedSolubilityIndex = -1;
	protected int selectedResidualIndex = -1;

	public JDialogEditCompRegInfo(Frame owner) {
		super(owner);
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jPanelWest = new JPanel();
			jPanelWestNorth = new JPanel();
			jPanelWestTop = new JPanel();
			HitID = new JTextField();
			jLabelHitID = new JLabel();
			jLabelSourceDetail = new JLabel();
			jComboBoxCompoundSrc = new CeNComboBox();
			jComboBoxSourceDetail = new CeNComboBox();
			jLabelCompoundSrc = new JLabel();
			BatchLotID = new JTextField();
			jLabelBatchLotID = new JLabel();
			jButtonColor = new JButton();
			jPanelCenter = new JPanel();
			jScrollPaneNorth = new JScrollPane();
			jTableResidualSolvents = new CeNJTable();
			jScrollPaneCenter = new JScrollPane();
			jTableSolubiltySolvents = new CeNJTable();
			jPanelSouth = new JPanel();
			jButtonSave = new JButton();
			jPanelfiller = new JPanel();
			jButtonCancel = new JButton();
			jTextFieldOwner = new JTextField();
			jLabelOwner = new JLabel();
			jButtonOwnerCheck = new JButton();
			BorderLayout thisLayout = new BorderLayout();
			getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			setTitle("Edit Compound Registration Information");
			setResizable(false);
			setModal(true);
			setName("Edit Compound Registration Information");
			setSize(new java.awt.Dimension(713, 355));
			BoxLayout jPanelWestLayout = new BoxLayout(jPanelWest, 1);
			jPanelWest.setLayout(jPanelWestLayout);
			jPanelWest.setPreferredSize(new java.awt.Dimension(257, 328));
			getContentPane().add(jPanelWest, BorderLayout.WEST);
			GridLayout jPanelWestNorthLayout = new GridLayout(2, 1);
			jPanelWestNorth.setLayout(jPanelWestNorthLayout);
			jPanelWestNorthLayout.setRows(2);
			jPanelWestNorthLayout.setHgap(0);
			jPanelWestNorthLayout.setVgap(0);
			jPanelWestNorthLayout.setColumns(1);
			jPanelWestNorth.setPreferredSize(new java.awt.Dimension(243, 312));
			jPanelWest.add(jPanelWestNorth);
			AnchorLayout jPanelWestTopLayout = new AnchorLayout();
			jPanelWestTop.setLayout(jPanelWestTopLayout);
			jPanelWestTop.setPreferredSize(new java.awt.Dimension(214, 140));
			jPanelWestNorth.add(jPanelWestTop);
			jLabelBatchLotID.setText("Nbk Batch #:");
			jLabelBatchLotID.setFont(new java.awt.Font("sansserif", 1, 11));
			jLabelBatchLotID.setPreferredSize(new java.awt.Dimension(95, 20));
			jLabelBatchLotID.setBounds(new java.awt.Rectangle(2, 12, 114, 23));
			jPanelWestTop.add(jLabelBatchLotID);
			BatchLotID.setEditable(false);
			BatchLotID.setPreferredSize(new java.awt.Dimension(100, 20));
			BatchLotID.setBounds(new java.awt.Rectangle(127, 12, 120, 23));
			jPanelWestTop.add(BatchLotID);
			jLabelCompoundSrc.setText("Compound Source:");
			jLabelCompoundSrc.setFont(new java.awt.Font("sansserif", 1, 11));
			jLabelCompoundSrc.setPreferredSize(new java.awt.Dimension(97, 20));
			jLabelCompoundSrc.setBounds(new java.awt.Rectangle(2, 39, 117, 23));
			jPanelWestTop.add(jLabelCompoundSrc);
			jComboBoxCompoundSrc.setPreferredSize(new java.awt.Dimension(100, 20));
			jComboBoxCompoundSrc.setBounds(new java.awt.Rectangle(127, 39, 120, 23));
			jPanelWestTop.add(jComboBoxCompoundSrc);// , new
			// AnchorConstraint(250,
			// 962, 382, 495, 1, 1,
			// 1, 1));
			jLabelSourceDetail.setText("Source Detail:");
			jLabelSourceDetail.setFont(new java.awt.Font("sansserif", 1, 11));
			jLabelSourceDetail.setPreferredSize(new java.awt.Dimension(104, 23));
			jLabelSourceDetail.setBounds(new java.awt.Rectangle(3, 66, 104, 23));
			jPanelWestTop.add(jLabelSourceDetail);
			jComboBoxSourceDetail.setPreferredSize(new java.awt.Dimension(100, 20));
			jComboBoxSourceDetail.setBounds(new java.awt.Rectangle(127, 66, 120, 23));
			jPanelWestTop.add(jComboBoxSourceDetail);// , new
			// AnchorConstraint(422,
			// 962, 535, 495, 1, 1,
			// 1, 1));
			jLabelHitID.setText("Hit-ID (plate ID-well):");
			jLabelHitID.setFont(new java.awt.Font("sansserif", 1, 11));
			jLabelHitID.setPreferredSize(new java.awt.Dimension(99, 20));
			jLabelHitID.setBounds(new java.awt.Rectangle(2, 94, 119, 23));
			jPanelWestTop.add(jLabelHitID);
			HitID.setPreferredSize(new java.awt.Dimension(100, 20));
			HitID.setBounds(new java.awt.Rectangle(127, 94, 120, 23));
			jPanelWestTop.add(HitID);
			jLabelOwner.setText("NT Owner:");
			jLabelOwner.setToolTipText("NT ID for Owner of batch");
			jLabelOwner.setFont(new java.awt.Font("sansserif", 1, 11));
			jLabelOwner.setPreferredSize(new java.awt.Dimension(99, 20));
			jLabelOwner.setBounds(new java.awt.Rectangle(2, 122, 119, 23));
			jPanelWestTop.add(jLabelOwner);
			jTextFieldOwner.setPreferredSize(new java.awt.Dimension(100, 20));
			jTextFieldOwner.setBounds(new java.awt.Rectangle(127, 122, 100, 23));
			jPanelWestTop.add(jTextFieldOwner);
			// jButtonOwnerCheck
			jButtonOwnerCheck.setPreferredSize(new java.awt.Dimension(20, 20));
			jButtonOwnerCheck.setBounds(new java.awt.Rectangle(227, 122, 20, 23));
			jPanelWestTop.add(jButtonOwnerCheck);
			jButtonOwnerCheck.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOwnerCheckActionPerformed(evt);
				}
			});
			// BorderLayout jPanelWestBottomLayout = new BorderLayout();
			// jPanelWestBottom.setLayout(jPanelWestBottomLayout);
			// jPanelWestBottomLayout.setHgap(0);
			// jPanelWestBottomLayout.setVgap(0);
			// jPanelWestBottom.setPreferredSize(new
			// java.awt.Dimension(214,140));
			// jPanelWestNorth.add(jPanelWestBottom);
			//	
			// jLabelCompCol.setText("Compound Color:");
			// jLabelCompCol.setVisible(false);
			// jLabelCompCol.setFont(new java.awt.Font("sansserif",1,11));
			// jPanelWestBottom.add(jLabelCompCol, BorderLayout.NORTH);
			//	
			// jButtonColor.setVisible(false);
			// jPanelWestBottom.add(jButtonColor, BorderLayout.CENTER);
			// jButtonColor.addActionListener( new ActionListener() {
			// public void actionPerformed(ActionEvent evt) {
			// jButtonColorActionPerformed(evt);
			// }
			// });
			BorderLayout jPanelCenterLayout = new BorderLayout();
			jPanelCenter.setLayout(jPanelCenterLayout);
			jPanelCenterLayout.setHgap(0);
			jPanelCenterLayout.setVgap(0);
			getContentPane().add(jPanelCenter, BorderLayout.CENTER);
			jScrollPaneNorth.setPreferredSize(new java.awt.Dimension(449, 145));
			jPanelCenter.add(jScrollPaneNorth, BorderLayout.NORTH);
			jTableResidualSolvents.setRowHeight(18);
			jTableResidualSolvents.setPreferredSize(new java.awt.Dimension(449, 120));
			jScrollPaneNorth.add(jTableResidualSolvents);
			jScrollPaneNorth.setViewportView(jTableResidualSolvents);
			{
				Object[][] data = new String[][] { { "0", "1" }, { "2", "3" } };
				Object[] columns = new String[] { "One", "Two" };
				javax.swing.table.TableModel dataModel = new javax.swing.table.DefaultTableModel(data, columns);
				jTableResidualSolvents.setModel(dataModel);
			}
			jTableResidualSolvents.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jTableResidualSolventsMouseClicked(evt);
				}
			});
			jPanelCenter.add(jScrollPaneCenter, BorderLayout.CENTER);
			jTableSolubiltySolvents.setRowHeight(18);
			jTableSolubiltySolvents.setPreferredSize(new java.awt.Dimension(449, 125));
			jScrollPaneCenter.add(jTableSolubiltySolvents);
			jScrollPaneCenter.setViewportView(jTableSolubiltySolvents);
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
			jPanelCenter.add(jPanelSouth, BorderLayout.SOUTH);
			jButtonSave.setText("Save");
			jButtonSave.setFont(new java.awt.Font("sansserif", 1, 11));
			jPanelSouth.add(jButtonSave);
			jButtonSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonSaveActionPerformed(evt);
				}
			});
			jPanelfiller.setPreferredSize(new java.awt.Dimension(75, 10));
			jPanelSouth.add(jPanelfiller);
			jButtonCancel.setText("Cancel");
			jButtonCancel.setFont(new java.awt.Font("sansserif", 1, 11));
			jPanelSouth.add(jButtonCancel);
			jButtonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed(evt);
				}
			});
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					JDialogEditCompRegInfo.this.setVisible(false);
					JDialogEditCompRegInfo.this.dispose();
				}

				public void windowClosed(WindowEvent e) {
					JDialogEditCompRegInfo.this.removeAll();
					JDialogEditCompRegInfo.this.setVisible(false);
				}
			});
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
		regSubHandler = new RegSubHandler();
		compoundSourceList = RegCodeMaps.getInstance().getCompoundSourceList();
		sourceAndDetailList = RegCodeMaps.getInstance().getSourceAndDetailList();
		solubilitySolventMap = RegCodeMaps.getInstance().getSolubilitySolventMap();
		residualSolventMap = RegCodeMaps.getInstance().getResidualSolventMap();
		solventqualitativeList = RegCodeMaps.getInstance().getQualitativeList();
		operatorList = RegCodeMaps.getInstance().getOperatorList();
		unitList = RegCodeMaps.getInstance().getUnitList();
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		jButtonOwnerCheck.setIcon(CenIconFactory.getImageIcon(CenIconFactory.General.CHECK_MARK));
		// //set up test color
		// Color color = new Color(248, 248, 255);
		// jButtonColor.setBackground(color);
		// jButtonColor.setEnabled(false); //for current version
		// set up table models
		jTableResidualSolvents.setModel(residualSolventTableModel);
		jTableSolubiltySolvents.setModel(solubilitySolventTableModel);
		// populate drop downs
		populateDropDowns();
		// init residualSolventTableModel
		residualSolventTableModel.resetModelData();
		residualSolventTableModel.fireTableDataChanged();
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
	}

	public void populateDropDowns() {
		// populate compound source drop down
		jComboBoxCompoundSrc.removeAllItems();
		for (int i = 0; i < compoundSourceList.size(); i++) {
			jComboBoxCompoundSrc.addItem(((Object[]) compoundSourceList.get(i))[1]);
		}
		jComboBoxCompoundSrc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jComboBoxCompoundSrcItemStateChanged(evt);
			}
		});
		// populate source detail drop down
		jComboBoxSourceDetail.removeAllItems();
		jComboBoxSourceDetail.setEnabled(false);
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
		// populate solubility solvent drop downs, need to set up the models
		JComboBox solubilityCombo = new JComboBox();
		CodeTableUtils.fillComboBoxWithSolubles(solubilityCombo);
		TableColumnModel solubilityColumModel = jTableSolubiltySolvents.getColumnModel();
		TableColumn solubilitycodeNameColumn = solubilityColumModel.getColumn(0);
		solubilitycodeNameColumn.setCellEditor(new DefaultCellEditor(solubilityCombo));
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
			JDialogEditCompRegInfo inst = new JDialogEditCompRegInfo(null);
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonColorActionPerformed(ActionEvent evt) {
		// show the color dialog, this method does not return until the dialog
		// is closed
		Color newColor = JColorChooser.showDialog(this, "Colors", jButtonColor.getBackground());
		jButtonColor.setBackground(newColor);
		newColor = null;
	}

	public void updateDisplay() {
		// need to check and set selected items in all the drop downs
		ProductBatchModel batch = getSelectedBatch();
		BatchRegInfoModel regInfo = batch.getRegInfo();
		// set batch id
		BatchLotID.setText(batch.getBatchNumberAsString());
		// set hit-id
		if (regInfo.getHitId() != null && !regInfo.getHitId().equals("")) {
			HitID.setText(regInfo.getHitId());
		}
		HitID.setEditable(true);
		if (batch.getOwner() != null && batch.getOwner().length() > 0)
			jTextFieldOwner.setText(batch.getOwner());
		else
			jTextFieldOwner.setText(batch.getSynthesizedBy());
		if (regInfo.getCompoundSource() != null && !regInfo.getCompoundSource().equals("")) {
			try {
				jComboBoxCompoundSrc.setSelectedItem(CodeTableCache.getCache().getSourceDescription(regInfo.getCompoundSource()));
				jComboBoxCompoundSrcItemStateChanged(null);
				if (regInfo.getCompoundSourceDetail() != null && !regInfo.getCompoundSourceDetail().equals("")) {
					jComboBoxSourceDetail.setSelectedItem(CodeTableCache.getCache().getSourceDetailDescription(
							regInfo.getCompoundSourceDetail()));
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
		// init residualSolventTableModel
		if (regInfo.getResidualSolventList().size() > 0) {
			residualSolventTableModel.setResidualSolventVOList(regInfo.getResidualSolventList());
			residualSolventTableModel.resetModelData();
			residualSolventTableModel.fireTableDataChanged();
		}
		// init solubilitySolventTableModel
		if (regInfo.getSolubilitySolventList().size() > 0) {
			solubilitySolventTableModel.setSolubilitySolventList(regInfo.getSolubilitySolventList());
			solubilitySolventTableModel.resetModelData();
			solubilitySolventTableModel.fireTableDataChanged();
		}
	}

	/**
	 * @return Returns the residualSolventMap.
	 */
	public HashMap getResidualSolventMap() {
		return residualSolventMap;
	}

	/**
	 * @return Returns the solubilitySolventMap.
	 */
	public HashMap getSolubilitySolventMap() {
		return solubilitySolventMap;
	}

	/** Auto-generated event handler method */
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

	/** Auto-generated event handler method */
	protected void addItemMouseClicked(ActionEvent evt) {
		residualSolventTableModel.addModelData();
		residualSolventTableModel.fireTableDataChanged();
	}

	/** Auto-generated event handler method */
	protected void cutItemMouseClicked(ActionEvent evt) {
		residualSolventTableModel.cutModelData(selectedResidualIndex);
		residualSolventTableModel.fireTableDataChanged();
	}

	/** Auto-generated event handler method */
	protected void clearItemMouseClicked(ActionEvent evt) {
		residualSolventTableModel.clearModelData();
		residualSolventTableModel.fireTableDataChanged();
	}

	/** Auto-generated event handler method */
	protected void requestNewItemMouseClicked(ActionEvent evt) {
		JOptionPane.showMessageDialog(this, "This service is not yet available.\n"
				+ "Please call the help desk to request a new solvent be added to the list.");
	}

	/** Auto-generated event handler method */
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

	/** Auto-generated event handler method */
	protected void setQuantitativeItemMouseClicked(ActionEvent evt) {
		if (selectedSolubilityIndex == -1) {
			JOptionPane.showMessageDialog(this, "Please select a solvent first.");
		} else {
			QuantitativeSolubilityJDialog quantitativeSolubilityJDialog = new QuantitativeSolubilityJDialog(this);
			quantitativeSolubilityJDialog.setModal(true);
			quantitativeSolubilityJDialog.setOperatorList(getOperatorList());
			quantitativeSolubilityJDialog.setUnitList(getUnitList());
			quantitativeSolubilityJDialog.resetSolubilityValuePanelLayout();
			quantitativeSolubilityJDialog.setVisible(true);
		}
	}

	/** Auto-generated event handler method */
	protected void setQualitativeItemMouseClicked(ActionEvent evt) {
		if (selectedSolubilityIndex == -1) {
			JOptionPane.showMessageDialog(this, "Please select a solvent first.");
		} else {
			QualitativeSolubilityJDialog qualitativeSolubilityJDialog = new QualitativeSolubilityJDialog(this);
			qualitativeSolubilityJDialog.setModal(true);
			qualitativeSolubilityJDialog.setSolventDescList(getSolventqualitativeList());
			qualitativeSolubilityJDialog.resetSolubilityValuePanelLayout();
			qualitativeSolubilityJDialog.setVisible(true);
		}
	}

	public void resetSolubilityValue() {
		solubilitySolventTableModel.setSolubilityValue(selectedSolubilityIndex, getSolubilityValueString());
		solubilitySolventTableModel.fireTableDataChanged();
	}

	/** Auto-generated event handler method */
	protected void addSolubilityMouseClicked(ActionEvent evt) {
		solubilitySolventTableModel.addModelData();
		solubilitySolventTableModel.fireTableDataChanged();
	}

	/** Auto-generated event handler method */
	protected void cutSolubilityMouseClicked(ActionEvent evt) {
		solubilitySolventTableModel.cutModelData(selectedSolubilityIndex);
		solubilitySolventTableModel.fireTableDataChanged();
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

	/**
	 * @return Returns the operatorList.
	 */
	public ArrayList getOperatorList() {
		return operatorList;
	}

	/**
	 * @return Returns the solventDescList.
	 */
	public ArrayList getSolventqualitativeList() {
		return solventqualitativeList;
	}

	/**
	 * @return Returns the unitList.
	 */
	public ArrayList getUnitList() {
		return unitList;
	}

	private void jButtonOwnerCheckActionPerformed(ActionEvent evt) {
		// Make sure it's uppercase
		jTextFieldOwner.setText(jTextFieldOwner.getText().toUpperCase());
		// Empty string means use Chemist's ID
		if (jTextFieldOwner.getText().length() == 0)
			jTextFieldOwner.setText(getSelectedBatch().getSynthesizedBy());
		// Validate it (if different from chemist)
		if (!jTextFieldOwner.getText().equals(getSelectedBatch().getSynthesizedBy())) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			String empId = regSubHandler.getOwnerEmpId(jTextFieldOwner.getText());
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			if (empId == null)
				JOptionPane.showMessageDialog(this, "Owner NT UserId not found in Employee Database.");
			else
				JOptionPane.showMessageDialog(this, "Owner NT UserId is valid.");
		} else
			JOptionPane.showMessageDialog(this, "Owner NT UserId is valid.");
	}

	/** Auto-generated event handler method */
	protected void jButtonSaveActionPerformed(ActionEvent evt) {
		// need to validate source/source detail combination
		jTableResidualSolvents.stopEditing();
		jTableSolubiltySolvents.stopEditing();
		ProductBatchModel batch = getSelectedBatch();
		BatchRegInfoModel regInfo = batch.getRegInfo();
		// Make sure Owner is uppercase, Empty string means use Chemist's ID
		jTextFieldOwner.setText(jTextFieldOwner.getText().toUpperCase());
		if (jTextFieldOwner.getText().length() == 0)
			jTextFieldOwner.setText(getSelectedBatch().getSynthesizedBy());
		if (!validateResidualInfo()) {
			JOptionPane.showMessageDialog(this, "#EQ of Solvent for Residual Solvent must be greater than zero.");
		} else if (!validateSolubilityInfo()) {
			JOptionPane.showMessageDialog(this, "Solubility value is required.");
		} else if (!batch.getOwner().equals(jTextFieldOwner.getText())
				&& !jTextFieldOwner.getText().equals(getSelectedBatch().getSynthesizedBy())
				&& regSubHandler.getOwnerEmpId(jTextFieldOwner.getText()) == null) {
			JOptionPane.showMessageDialog(this, "Owner NT UserId not found in Employee Database");
		} else {
			// set up BatchRegistrationInfo
			// getRegistrationInfoVO().setCompoundColor(jButtonColor.getBackground().toString());
			try {
				String val = CodeTableCache.getCache().getSourceCode((String) jComboBoxCompoundSrc.getSelectedItem());
				regInfo.setCompoundSource(val);
				MasterController.getUser().setPreference(NotebookUser.PREF_LastSource, val);
			} catch (Exception e) {
			}
			try {
				String val = CodeTableCache.getCache().getSourceDetailCode((String) jComboBoxSourceDetail.getSelectedItem());
				regInfo.setCompoundSourceDetail(val);
				MasterController.getUser().setPreference(NotebookUser.PREF_LastSourceDetail, val);
			} catch (Exception e) {
			}
			if (HitID.getText() != null && !HitID.getText().equals("")) {
				regInfo.setHitId(HitID.getText());
			}
			// Get the new Owner
			if (batch.getOwner() == null || !batch.getOwner().equals(jTextFieldOwner.getText())) {
				batch.setOwner(jTextFieldOwner.getText());
				try {
					MasterController.getUser().setPreference(NotebookUser.PREF_LastOwner, jTextFieldOwner.getText());
				} catch (Exception e) {
				}
			}
			// set up BatchRegistrationSolubilitySolvent list
			buildSolubilityVO();
			// set up BatchRegistrationResidualSolvent list
			buildResidualVO();
			// update BatchRegistrationInfo for CompRegInfo container
			getCompoundRegInfoContainer().setSelectedBatch(batch);
			// update CompoundRegInfoContainer display
			getCompoundRegInfoContainer().updateDisplay();
			// update batchList
			getCompoundRegInfoContainer().updateBatchList(batch);
			// close
			setVisible(false);
			this.dispose();
		}
	}

	private boolean validateResidualInfo() {
		boolean isValid = true;
		int residualSolventListSize = residualSolventTableModel.getCodeAndNameList().size();
		for (int i = 0; i < residualSolventListSize && isValid; i++) {
			if (((String) (residualSolventTableModel.getEqList().get(i))).trim().length() <= 0)
				isValid = false;
		}
		return isValid;
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
		}
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

	// private String getSolubilityDecrFromCode(String code){
	//		
	// String decrString = "";
	// try {
	// decrString =
	// CodeTableCache.getCache().getSolubilitySolventDescription(code);
	// } catch (CodeTableCacheException e) {
	// CeNErrorHandler.getInstance().logExceptionMsg(this,"Look up
	// description failed.", e);
	// }
	// return decrString;
	// }
	private String getResidualCodeFromDecr(String decr) {
		String codeString = "";
		try {
			codeString = CodeTableCache.getCache().getResidualSolventCode(decr);
		} catch (CodeTableCacheException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, "Look up code failed.", e);
		}
		return codeString;
	}

	// private String getResidualDecrFromCode(String code){
	//		
	// String decrString = "";
	// try {
	// decrString =
	// CodeTableCache.getCache().getResidualSolventDescription(code);
	// } catch (CodeTableCacheException e) {
	// CeNErrorHandler.getInstance().logExceptionMsg(this,"Look up
	// description failed.", e);
	// }
	// return decrString;
	// }
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

	/**
	 * 
	 */
	private void buildResidualVO() {
		int oldListSize = getSelectedBatch().getRegInfo().getResidualSolventList().size();
		getSelectedBatch().getRegInfo().getResidualSolventList().clear();
		int residualSolventListSize = residualSolventTableModel.getCodeAndNameList().size();
		for (int i = 0; i < residualSolventListSize; i++) {
			BatchResidualSolventModel residualSolventVO = new BatchResidualSolventModel();
			residualSolventVO
					.setCodeAndName(getResidualCodeFromDecr((String) residualSolventTableModel.getCodeAndNameList().get(i)));
			residualSolventVO.setComments((String) residualSolventTableModel.getCommentsList().get(i));
			// String eqOfSolventString =
			// (String)(residualSolventTableModel.getEqList().get(i));
			// Object obj = residualSolventTableModel.getEqList().get(i);
			// residualSolventVO.setEqOfSolvent((new
			// Double(obj.toString())).doubleValue());
			// //System.out.println("The type of
			// residualSolventTableModel.getEqList().get(i) is" +
			// obj.getClass());
			residualSolventVO.setEqOfSolvent((new Double((String) (residualSolventTableModel.getEqList().get(i)))).doubleValue());
			getSelectedBatch().getRegInfo().addResidualSolvent(residualSolventVO);
		}
		// Force Recalc since Residual Solvent affects Batch MolWgt
		if (oldListSize > 0 || residualSolventListSize > 0) {
			getSelectedBatch().recalcAmounts();
			getSelectedBatch().getRegInfo().setModified(true);
		}
	}

	/**
	 * @return Returns the compoundRegInfoContainer.
	 */
	public CompoundRegInfoContainer getCompoundRegInfoContainer() {
		return compoundRegInfoContainer;
	}

	/**
	 * @param compoundRegInfoContainer
	 *            The compoundRegInfoContainer to set.
	 */
	public void setCompoundRegInfoContainer(CompoundRegInfoContainer compoundRegInfoContainer) {
		this.compoundRegInfoContainer = compoundRegInfoContainer;
	}

	/** Auto-generated event handler method */
	protected void jButtonCancelActionPerformed(ActionEvent evt) {
		setVisible(false);
		this.dispose();
	}

	/**
	 * @return Returns the selectedBatch.
	 */
	public ProductBatchModel getSelectedBatch() {
		return selectedBatch;
	}

	/**
	 * @param selectedBatch
	 *            The selectedBatch to set.
	 */
	public void setSelectedBatch(ProductBatchModel selectedBatch) {
		this.selectedBatch = selectedBatch;
	}

	/**
	 * @return Returns the sourceAndDetailList.
	 */
	public ArrayList getSourceAndDetailList() {
		return sourceAndDetailList;
	}

	/** Auto-generated event handler method */
	protected void jComboBoxCompoundSrcItemStateChanged(ActionEvent evt) {
		// populate source detail drop down based on the selection of source
		// code
		jComboBoxSourceDetail.removeAllItems();
		try {
			String source = CodeTableCache.getCache().getSourceCode((String) jComboBoxCompoundSrc.getSelectedItem());
			Iterator detailIterator = sourceAndDetailList.iterator();
			while (detailIterator.hasNext()) {
				Object[] detailCode = (Object[]) detailIterator.next();
				if (((String) (detailCode[2])).equals(source))
					jComboBoxSourceDetail.addItem((String) (detailCode[1]));
			}
			jComboBoxSourceDetail.setEnabled(jComboBoxSourceDetail.getItemCount() > 0);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void dispose() {
		this.selectedBatch = new ProductBatchModel();
		this.selectedBatch = null;
		this.regSubHandler = null;
		jTableResidualSolvents.setModel(new DefaultTableModel());
		jTableSolubiltySolvents.setModel(new DefaultTableModel());
		jTableResidualSolvents = null;
		jTableSolubiltySolvents = null;
		super.dispose();
	}
}