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

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.common.utils.CeNKeyHandlerOptionPane;
import com.chemistry.enotebook.client.gui.common.utils.CeNStatusComboBoxRenderer;
import com.chemistry.enotebook.client.gui.common.utils.CeNTableCellAlignedComponentRenderer;
import com.chemistry.enotebook.client.gui.page.analytical.AnalyticalTableNotEditableCell;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail.StructureTableCellRenderer;
import com.chemistry.enotebook.client.gui.page.batch.*;
import com.chemistry.enotebook.client.gui.page.batch.editors.ExternalSupplierDialogEditor;
import com.chemistry.enotebook.client.gui.page.batch.editors.HazardsDialogEditor;
import com.chemistry.enotebook.client.gui.page.batch.editors.MeltingPointDialogEditor;
import com.chemistry.enotebook.client.gui.page.batch.editors.PurityDialogEditor;
import com.chemistry.enotebook.client.gui.page.experiment.CustomTextField;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountCellEditor;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountCellEditorEx;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountCellRenderer;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountComponent;
import com.chemistry.enotebook.client.gui.page.experiment.table.connector.PCeNMonomerPlatesTableModelConnector;
import com.chemistry.enotebook.client.gui.page.experiment.table.connector.PCeNMonomerReactantsSummaryTableModelConnector;
import com.chemistry.enotebook.client.gui.page.experiment.table.connector.PCeNMonomerReactantsTableModelConnector;
import com.chemistry.enotebook.client.gui.page.experiment.table.connector.PCeNProductPlatesTableModelConnector;
import com.chemistry.enotebook.client.gui.page.regis_submis.JDialogHealthHazInfo;
import com.chemistry.enotebook.client.gui.page.regis_submis.PCeNRegistration_SummaryViewControllerUtility;
import com.chemistry.enotebook.client.gui.tablepreferences.*;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.common.units.UnitCache2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.utils.*;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * 
 * 
 */
public class PCeNTableView extends CeNJTable implements ChangeListener {

	private static final long serialVersionUID = -7609115532001755213L;

	public static final Log log = LogFactory.getLog(PCeNTableView.class);

	public static final String STATUS = "Status";
	public static final String SALT_CODE = "Salt Code";
	public static final String RXN_ROLE = "Rxn Role";
	public static final String TIMES_USED = "Times Used";
	public static final String EXTRA_NEEDED = "Extra Needed";
	public static final String NBK_BATCH_NUM = "Nbk Batch #";
	public static final String CAS_NUMBER = "Cas Number";
	public static final String BATCH_ANALYTICAL_COMMENTS = "Analytical Comments";
	public static final String MOLARITY = "Molarity";
	public static final String TUBE_MOLARITY = "Tube Molarity";
	public static final String PARENT_MW = "Parent MW";
	public static final String BATCH_MW = "Batch MW";
	public static final String DELIVERED_WEIGHT = "<html>Delivered  <br></br>Weight</html>";// "Delivered Weight";
	public static final String DELIVERED_VOLUME = "<html>Delivered  <br></br>Volume</html>";// "Delivered Volume";
	public static final String RXN_MOLES = "<html>In Rxn  <br></br>Moles</html>"; // "Rxn Moles";
	public static final String RXN_EQ = "EQ";
	public static final String RXN_WEIGHT = "<html>In Rxn  <br></br>Weight</html>"; // "Rxn Weight";
	public static final String RXN_VOLUME = "<html>In Rxn  <br></br>Volume</html>"; // "Rxn Volume";
	public static final String DELIVERED_MOLES = "<html>Delivered <br></br>Moles</html>";
	public static final String DEAD_VOLUME = "Dead Volume";
	public static final String PURITY = "MN Purity";
	public static final String ANALYTICAL_PURITY = "Purity";
	public static final String DENSITY = "Density";
	public static final String SALT_EQ = "Salt EQ";
	public static final String BATCH_COMMENTS = "<html>Batch <br></br>Comments</html>"; // "Batch Comments";
	public static final String DESIGN_MONOMER = "<html>Design <br></br>Monomer</html>";
	public static final String PLATE = "Plate";
	public static final String COMPOUND_ID = "Compound ID";
	public static final String WELL_POSITION = "Well #";
	public static final String DELIVERED_MONOMER = "<html>Delivered <br></br>Monomer</html>";
	public static final String LIMITING = "Limiting";
	public static final String MOL_FORMULA = "Mol Formula";
	public static final String HAZARD_COMMENTS = "Hazards";
	public static final String HANDLING_PRECAUTIONS = "Handling Precations";
	public static final String STORAGE_INSTRUCTIONS = "Storage Instructions";
	public static final String EXPTAB_HAZARD_COMMENTS = "<html>Compound <br></br>Hazards</html>"; // "Compound Hazards";
	public static final String COMMENTS = "Comments";
	public static final String STRUCTURE = "Structure";
	public static final String MOL_WEIGHT = "Mol Wgt";
	public static final String AMT_NEEDED_RXN = "Rxn Needed";
	public static final String TOTAL_ORDERED = "Total Ordered";
	public static final String TOTAL_WEIGHT = "<html><font color=\"#FFAAAA\">* </font>Total <br></br>  Weight</html>"; // "Total Amt. Made";
	public static final String TOTAL_VOLUME = "<html>Total <br></br>Volume</html>"; // "Total Vol. Made";
	public static final String TOTAL_MOLES = "Total Moles";
	public static final String VOLUME = "Volume";
	public static final String TOTAL_DELIVERED_AMT = "<html>Total <br></br>Delivered</html>";
	public static final String NMR = "NMR";
	public static final String MS = "MS";
	public static final String SFC_MS = "SFC-MS";
	public static final String HPLC = "HPLC";
	public static final String VIEW_PURIFICATION_SERVICE = "View PurificationService";
	public static final String PURIFICATION_SERVICE_DATA = "PurificationService Data";
	public static final String LINK_BATCH = "Link Batch";
	public static final String AMT_MADE = "Amt Made";
	public static final String CONVERSATIONAL_BATCH = "<html>Conversational <br></br>Batch #</html>"; // "Conversational Batch";
	public static final String REGISTRATION_DATE = "<html>Registration <br></br>Date</html>";
	public static final String VIRTUAL_COMP_NUM = "<html>Virtual <br></br>Compound Id</html>"; // "Virtual Compound ID";
	public static final String CALCULATED_BATCH_MW = "<html>Calculated <br></br>Batch MW</html>";
	public static final String CALCULATED_PARENT_MW = "<html>Calculated <br></br>Parent MW</html>";
	public static final String STEREOISOMER = "Stereoisomer";
	public static final String PRECURSORS = "Precursors";
	public static final String THEORETICAL_MMOLES = "Theo. Moles";
	public static final String PERCENT_YIELD = "%Yield";
	public static final String THEORETICAL_WEIGHT = "Theo. Wgt.";
	public static final String COMPOUND_STATE = "<html>Compound <br></br>State</html>"; // "Compound State";
	public static final String EXT_SUPPLIER = "Ext Supplier";
	public static final String BARCODE = "Barcode";
	public static final String SOURCE = "<html><font color=\"red\">* </font>Source</html>";
	public static final String SOURCE_DETAILS = "<html><font color=\"red\">* </font>Source Detail</html>";
	public static final String TA = "TA";
	public static final String PROJECT = "Project";
	// public static final String EXTERNAL_SUPPLIER = "Ext Supplier";
	public static final String COMPOUND_PROTECTION = "<html>Compound <br></br>Protection</html>"; // "Compound Protection";
	public static final String RESIDUAL_SOLVENTS = "<html>Residual <br></br>Solvents</html>"; // "Residual Solvents";
	public static final String SOLUBILITY_IN_SOLVENTS = "<html>Solubility <br></br>in Solvents</html>"; // "Solubility in Solvents";
	public static final String PARENT_MOL_WEIGHT = "ParentMol Wgt.";
	public static final String PARENT_MOL_FORMULA = "ParentMol Wgt.";
	public static final String EXACT_MASS = "Exact Mass";
	public static final String PRODUCT_PLATE = "Product Plate";
	public static final String TOTAL_MOLES_NEEDED = "<html>Total Moles <br></br>Needed</html>";
	public static final String TOTAL_WEIGHT_NEEDED = "<html>Total Wt. <br></br>Needed</html>";
	public static final String TOTAL_VOLUME_NEEDED = "<html>Total Vol. <br></br>Needed</html>";
	public static final String TOTAL_VOLUME_DELIVERED = "<html>Total Vol. <br></br>Delivered</html>";
	// public static final String TOTAL_AMT_MADE = "Total Amt Made";
	public static final String AMT_REMAINING_WEIGHT = "<html>Remaining <br></br>Weight</html>"; // "Remaining Wgt.";
	public static final String AMT_REMAINING_VOLUME = "<html>Remaining <br></br>Volume</html>"; // "Remaining Vol.";
	// public static final String AMT_IN_TUBE_WT = "<html>In Tube  <br></br>(Weight)</html>";
	public static final String AMT_IN_TUBE_VOL = "<html>In Tube  <br></br>Volume</html>";
	public static final String AMT_IN_VIAL_WT = "<html>In Vial  <br></br>Weight</html>";
	public static final String AMT_IN_WELL_WT = "<html>In Well/Tube  <br></br>Weight</html>";
	public static final String AMT_IN_WELL_VOL = "<html>In Well/Tube  <br></br>Volume</html>";
	public static final String AMT_IN_WELL_MOLARITY = "<html>In Well/Tube  <br></br>Molarity</html>";
	public static final String AMT_IN_WELL_MOLES = "<html>In Well/Tube  <br></br>Moles</html>";

	/*
	 * public static final String AMT_IN_TUBE_VOL = "Amt In Tube (Volume)"; public static final String AMT_IN_VIAL_WT =
	 * "Amt In Vial (Weight)"; public static final String AMT_IN_WELL_WT = "Amt In Well/Tube (Weight)"; public static final String
	 * AMT_IN_WELL_VOL = "Amt In Well/Tube (Volume)"; public static final String AMT_IN_WELL_MOLARITY =
	 * "Amt In Well/Tube (Molarity)"; public static final String AMT_IN_WELL_MOLES = "Amt In Well/Tube (Moles)";
	 */

	public static final String REGISTRATION = "Registration";
	public static final String CONTAINER_REGISTRATION = "<html>Container <br></br>Registration</html>"; // "Container Registration";
	public static final String PURIFICATION = "Purification";
	public static final String SCREENING = "Screening";
	public static final String QQC = "<html>Quantitative <br></br>Quality Control</html>"; // "Quantitative Quality Control";
	public static final String QUICK_LINK = "Quick Link";
	public static final String PLATE_COMMENTS = "Plate Comments";
	public static final String STRUCTURE_COMMENTS = "<html>Structure <br></br>Comments</html>"; // "Structure Comments";
	public static final String MELTING_POINT = "Melting Point";
	public static final String WELL_SOLVENT = "Well Solvent";
	public static final String SELECT = "<html><font color=\"red\">* </font>Select</html>";
	public static final String SELECT_OPTION = "Select";
	public static final String PRECONDITIONS = "Missing Pre-Conditions";
	public static final String BATCH_OWNER = "Batch Owner";
	public static final String SYNTHESIZED_BY = "<html>Synthesized <br></br>By</html>"; // "Synthesized By";
	public static final String PLATE_BARCODE = "<html>Plate <br></br>Barcode</html>";
	public static final String TUBE_BARCODE = "<html>Tube <br></br>Barcode</html>";
	public static final String VIAL_BARCODE = "<html>Vial <br></br>Barcode</html>";
	
	// Tube/vial jtable column names; put here for use in updating all values for a particular column (all same value).
	public static final String WT_IN_TUBE_VIAL = "Wt. in Vial/Tube";
	public static final String MOLES_IN_TUBE_VIAL = "Moles in Vial/Tube";
	public static final String VOL_IN_TUBE_VIAL = "Vol. in Vial/Tube";
	public static final String BARCODE_TUBE_VIAL = "Barcode";
	public static final String SOLVENT = "Solvent";
	public static final String CONTAINER_TYPE = "Container Type";
	public static final String CONTAINER_LOCATION = "Container Location";
	
	public static final int heightDifference = 56;
	public static final int elevenRowsHeight = 224;
	public static final int tableViewRowHeight = 70;

	private String workingColumnName = "";
	private String sortedColumnName = "";
	protected int ROW_HEIGHT = DefaultPreferences.SHORT_ROW_HEIGHT;
	protected PCeNTableModelConnector connector;

	protected PCeNTableViewPopupMenuManager mTableViewPopupMenuManager;
	private CustomTextField timesUsedTextField = new CustomTextField(new Integer(0), new Integer(99), false,
			CustomTextField.NUMERIC);
	private TableCellEditor timesUsedCellEditor = new DefaultCellEditor(timesUsedTextField);
	// Salt code
	protected JComboBox jComboBoxSaltCd = new CeNComboBox();
	protected TableCellEditor saltCdeditor = new DefaultCellEditor(jComboBoxSaltCd);
	// Compound state
	protected JComboBox jComboCompoundState = new CeNComboBox();
	// well solvent
	protected JComboBox jComboWellSolvent = new CeNComboBox();
	protected TableCellEditor solventCellEditor;
	protected JComboBox jComboContainerType = new CeNComboBox();
	// protected TableCellEditor compoundStateEditor = new DefaultCellEditor(jComboCompoundState);
	// Compound protection
	protected JComboBox comboBoxCompoundProtection = new CeNComboBox();
	// protected CeNCompoundProtectionEditor compoundProtectionEditor = new CeNCompoundProtectionEditor(comboBoxCompoundProtection);
	// Compound source and detail
	protected JComboBox comboBoxCompoundSource = new CeNComboBox();
	// protected TableCellEditor compoundSourceEditor = new DefaultCellEditor(comboBoxCompoundSource);
	protected JComboBox comboBoxSourceDetail = new CeNComboBox();
	protected TableCellEditor sourceDetailEditor = new DefaultCellEditor(comboBoxSourceDetail);
	// Stereoisomer
	protected JComboBox comboBoxStereoisomer = new CeNComboBox();

	// Purity
	// /////////protected JTextField purityTextField = new JTextField();
	// private JPanel reagentTypePanel = new JPanel();
	private JComboBox jComboBoxReagentType = new CeNComboBox(new String[] { CeNConstants.BATCH_TYPE_REACTANT,
			CeNConstants.BATCH_TYPE_REAGENT, CeNConstants.BATCH_TYPE_SOLVENT });
	protected TableCellEditor reagentTypeEditor = new DefaultCellEditor(jComboBoxReagentType);
	private JComboBox jComboBoxStatus = new CeNComboBox();
	// private TableCellEditor statusCellEditor = new DefaultCellEditor(jComboBoxStatus);
	private JTextField textFieldAnalyticalComments = new JTextField();
	private TableCellEditor analyticalCommentsCellEditor = new DefaultCellEditor(textFieldAnalyticalComments);
	// private JTextField nbkBatchNoTextField = new JTextField();
	// private TableCellEditor nbkBatchNoCellEditor = new DefaultCellEditor(nbkBatchNoTextField);
	private JTextField batchCommentsTextField = new JTextField();
	private TableCellEditor batchCommentsCellEditor = new DefaultCellEditor(batchCommentsTextField);

	protected PCeNTableViewCellRenderer cellRenderer = new PCeNTableViewCellRenderer();
	private PCeNTableViewHeaderRenderer headerRenderer = new PCeNTableViewHeaderRenderer();
	private PCeNTableViewCellEditor cellEditor = new PCeNTableViewCellEditor();

	// table and column preferences
	private static String SHOW_HIDE_STRUCTURES = "Show/Hide Structures";
	private static String SET_DEFAULT_UNIT = "Set Default Unit";
	private static String EXIT_MENU_ITEM = "Exit";
	private TablePreferenceDelegate tablePreferenceDelegate;
	private String tableName = "";

	private final VisibleAction visibleActionListener = new VisibleAction();
	private final JCheckBox structureCheckBox = new JCheckBox();
	private PCeNTableViewToolBar toolBar;
	
	public PCeNTableView() {
		super();
	}

	JPopupMenu headerPopupMenu = new JPopupMenu();
	JMenuItem setColValueItem = new JMenuItem("Set Value for entire column");
	JMenu showDefaultUnitMenu = new JMenu(SET_DEFAULT_UNIT);
	ScrollableMenu showHideColumnsMenu = new ScrollableMenu("Show/Hide Columns");
	BatchAttributeComponentUtility batchUtility = BatchAttributeComponentUtility.getInstance();
	private NotebookPageModel pageModel;

	public PCeNTableView(PCeNTableModel model, int rowHeight, PCeNTableModelConnector connectore,
			PCeNTableViewPopupMenuManager tableViewPopupMenuManager, NotebookPageModel pageModel) {
		super();
		this.pageModel = pageModel;
		init(model, rowHeight, connectore, tableViewPopupMenuManager);
	}
	
	public NotebookPageModel getPageModel() {
		return pageModel;
	}

	public PCeNTableView(PCeNTableModel model, int rowHeight, PCeNTableModelConnector connectore,
			PCeNTableViewPopupMenuManager tableViewPopupMenuManager) {
		super();
		init(model, rowHeight, connectore, tableViewPopupMenuManager);
	}

	private void init(PCeNTableModel model, int rowHeight, PCeNTableModelConnector connectore,
			PCeNTableViewPopupMenuManager tableViewPopupMenuManager) {
		ToolTipHeader tth = new ToolTipHeader(this.getColumnModel());
		this.setTableHeader(tth);
		setSelectionBackground(getBackground());
		setSelectionForeground(getForeground());
		setFocusable(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		connector = connectore;
		mTableViewPopupMenuManager = tableViewPopupMenuManager;
		if (mTableViewPopupMenuManager != null)
			mTableViewPopupMenuManager.addMouseListener(this);
		this.setModel(model);
		this.ROW_HEIGHT = rowHeight;
		// Fill salt code combo
		CodeTableUtils.fillComboBoxWithSaltCodes(jComboBoxSaltCd);
		if (connector instanceof PCeNMonomerPlatesTableModelConnector
				|| connector instanceof PCeNMonomerReactantsSummaryTableModelConnector
				|| connector instanceof PCeNMonomerReactantsTableModelConnector)
			MonomerBatchStatusMapper.getInstance().fillComboBox(jComboBoxStatus);
		else
			ProductBatchStatusMapper.getInstance().fillComboBox(jComboBoxStatus);
		// Fill compound state combo
		CodeTableUtils.fillComboBoxWithCompoundStates(this.jComboCompoundState);
		jComboCompoundState.insertItemAt("", 0);
		// Fill well solvent
		CodeTableUtils.fillComboBoxWithSolvents(jComboWellSolvent);
		jComboWellSolvent.setSelectedIndex(-1);
		// Fill stereoisomer
		batchUtility.fillStereoisomerComboBox(comboBoxStereoisomer);
		comboBoxStereoisomer.insertItemAt("", 0);
		// Fill vial/tube container type
		CodeTableUtils.fillComboBoxWithContainerTypes(jComboContainerType);
		jComboContainerType.setSelectedIndex(-1);

		solventCellEditor = new DefaultCellEditor(jComboWellSolvent);
		// Fill compound protection combo box
		batchUtility.fillProtectionCodesComboBox(this.comboBoxCompoundProtection);
		// Fill compound source and details comboBoxes
		batchUtility.fillCompoundSourceComboBox(this.comboBoxCompoundSource);
		comboBoxCompoundSource.insertItemAt(null, 0);
		comboBoxCompoundSource.setSelectedIndex(0);
		comboBoxCompoundSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				batchUtility.fillSourceDetailComboBox(comboBoxCompoundSource, comboBoxSourceDetail);
			}
		});
		this.setAutoResizeMode(AUTO_RESIZE_OFF);
		setAutoCreateColumnsFromModel(true);
		this.setSize(new Dimension(650, 400));
		// setTableViewCellRenderers();

		this.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				tableHeaderRightClicked(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				tableHeaderRightClicked(e);
			}
		});
		this.setRowHeight(ROW_HEIGHT);
		this.setColumnSelectionAllowed(false);
		this.setCellSelectionEnabled(false);
		this.setRowSelectionAllowed(true);
		this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.addMouseListener(new MouseAdapter() {
			private MouseEvent lastEvent; //workaround
			public void mouseReleased(MouseEvent evt) {
				if (evt.getClickCount() == 1) {
					valueChanged();
				}
				lastEvent = evt;				
			}
				
			public void mouseClicked(MouseEvent evt) {
				if(lastEvent == null || evt.getWhen() != lastEvent.getWhen()) {
					if (evt.getClickCount() == 1) {
						valueChanged();
					}
				}
				lastEvent = null;				
			} // end mouseClicked
		});
		this.convertColumnModelsToColumnFurniture();

		this.tablePreferenceDelegate = this.connector.getTablePreferenceDelegate();

		if (this.connector.getTablePreferenceDelegate() != null)
			this.columnModel.addColumnModelListener(new TableColumnModelListener() {
				public void columnAdded(TableColumnModelEvent e) {
				}

				public void columnMarginChanged(ChangeEvent e) {
					TableColumn column = getTableHeader().getResizingColumn();
					if (column != null)
						if (column instanceof ColumnFurniture) {
							// System.out.println(((ColumnFurniture) column).toString() + "  -   " + column.getWidth());
							int prefwidth = ((ColumnFurniture) column).getPreferredWidth();
							if (Math.abs(prefwidth - column.getWidth()) <= 5) {
								// skip the small changes
								return;
							}
							if (tablePreferenceDelegate != null) {
								// System.out.println(((ColumnFurniture) column).toString() + "  -   " + column.getWidth());
								try {
									tablePreferenceDelegate.resetTableColumnOnMarginChanged((ColumnFurniture) column);
								} catch (RuntimeException e1) {
									log.error("Failed to reset table column on margin change", e1);
								}
							}
						}
				}

				public void columnMoved(TableColumnModelEvent e) {
					if (tablePreferenceDelegate != null) {
						// System.out.println(((ColumnFurniture) column).toString() + "  -   " + column.getWidth());
						tablePreferenceDelegate.columnMoved(e);
					}
				}

				public void columnRemoved(TableColumnModelEvent e) {
				}

				public void columnSelectionChanged(ListSelectionEvent e) {
				}

			});

		// Hide structure column by default
//		hideStructureColumn();

		// Set up the header popup menu and its submenus
		// NOTE: this can only be done after the column models have been converted to column furniture
				
		headerPopupMenu.add(setColValueItem);
		headerPopupMenu.add(showDefaultUnitMenu);
		this.initHideColumnMenu(showHideColumnsMenu);
		headerPopupMenu.add(showHideColumnsMenu);
		addExportToExcelMenuItem(headerPopupMenu);

		setColValueItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setColValueItemActionPerformed(setColValueItem.getText());
			}
		});
		this.tableName = this.connector.getClass().getName();
		if (this.tablePreferenceDelegate != null)
			this.tablePreferenceDelegate.initColumnVisibility(this);
		this.initColumnLocation();
		this.setTableViewCellRenderers();
		if (this.tablePreferenceDelegate != null)
			this.adjustColumnWidths();
		else
			this.setDefaultColumnWidths();
		this.revalidate();
		
		hideStatusColumnIfSingleton();
	}

	private void convertColumnModelsToColumnFurniture() {
		TableColumnModel tcm = this.getColumnModel();
		Vector<ColumnFurniture> v = new Vector<ColumnFurniture>();
		v.setSize(tcm.getColumnCount());
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			TableColumn col = tcm.getColumn(i);
			TableColumnInfo tci = ((PCeNTableModel) this.getModel()).getColumnInfoFromModelIndex(col.getModelIndex());
			if (tci != null) {
				String name = tci.getLabel();
				// If the name isn't in the TableColumnInfo, get it from the model.
				if (name == null || name.length() == 0)
					name = this.getModel().getColumnName(i);
				ColumnFurniture cf = new ColumnFurniture(col, tci.isVisible(), tci.getColumnIndex(), tci.getPreferredWidth(), name);
				v.setElementAt(cf, tci.getModelIndex());
			} else {
				ColumnFurniture cf = new ColumnFurniture(col, true, i, 100, this.getModel().getColumnName(i));
				v.setElementAt(cf, i);
			}
		}
		// Remove all the column models from the table
		for (int i = tcm.getColumnCount() - 1; i >= 0; i--) {
			TableColumn col = tcm.getColumn(i);
			tcm.removeColumn(col);
		}
		// Substitute the column furniture
		for (Iterator<ColumnFurniture> it = v.iterator(); it.hasNext();)
			tcm.addColumn((ColumnFurniture) it.next());

	}

	// Rearrange the position of the columns
	public void initColumnLocation() {
		TableColumnModel tcm = this.getColumnModel();
		ColumnFurniture[] columns = new ColumnFurniture[tcm.getColumnCount()];
		// Check whether all of the column locations are filled.
		// If any are not, it will cause a NPE. In that case revert
		// to the model indexes.
		boolean columnModelOK = true;
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			ColumnFurniture column = (ColumnFurniture) tcm.getColumn(i);
			if (columns[column.getColumnIndex()] == null)
				columns[column.getColumnIndex()] = column;
			else
				columnModelOK = false;
		}
		if (columnModelOK) {
			// Remove all the column models from the table
			for (int i = tcm.getColumnCount() - 1; i >= 0; i--) {
				TableColumn col = tcm.getColumn(i);
				tcm.removeColumn(col);
			}
			// Substitute the column furniture
			for (int i = 0; i < columns.length; i++)
				tcm.addColumn(columns[i]);
		} else { // revert back to model indexes
			for (int i = 0; i < tcm.getColumnCount(); i++) {
				ColumnFurniture column = (ColumnFurniture) tcm.getColumn(i);
				column.setColumnIndex(column.getModelIndex());
			}
		}
	}

	public void dispose() {

	}

	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component comp = super.prepareRenderer(renderer, row, column);
		if (!isCellEditable(row, column))
			// comp.setBackground(new Color(220, 220, 210)); // vb 11/2
			comp.setBackground((DefaultPreferences.getNonEditableRowBackgroundColor()));
		// highlightLastRow(row);
		return comp;
	}

	/**
	 * Adjust column widths using table preferences. If they do not exist, invoke the setDefaultColumnWidths method. Note that
	 * visible and invisible columns have to be handled differently.
	 */
	private void adjustColumnWidths() {
		TableColumnModel tcm = this.getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			ColumnFurniture cf = (ColumnFurniture) tcm.getColumn(i);
			String name = cf.getLabel();
			if (name == null || name.length() == 0)
				name = cf.getHeaderValue().toString();
			TableColumn col = null;
			// temporary!!!
			try {
				col = this.getColumn(name);
			} catch (RuntimeException e) {

			}
			if (col != null)
				if (cf.isVisible())
					col.setPreferredWidth(cf.getPreferredWidth());
		}
	}

	/**
	 *
	 */
	private void setDefaultColumnWidths() {
		for (int colidx = 0; colidx < this.getColumnCount(); colidx++) {
			String name = getColumnName(colidx);
			if (name == null)
				return;
			TableColumn col = getColumn(name);
			if (name.equalsIgnoreCase(STRUCTURE)) {
				col.setPreferredWidth(120);
			} else if (name.equalsIgnoreCase(NBK_BATCH_NUM)) {
				col.setPreferredWidth(80);
			} else if (name.equalsIgnoreCase("MW")) {
				col.setPreferredWidth(80);
			} else if (name.equalsIgnoreCase(MOL_WEIGHT)) {
				col.setPreferredWidth(80);
			} else if (name.equalsIgnoreCase(MOL_FORMULA)) {
				col.setPreferredWidth(100);
			} else if (name.equalsIgnoreCase(TIMES_USED)) {
				col.setPreferredWidth(80);
			} else if (name.equalsIgnoreCase(WELL_POSITION)) {
				col.setPreferredWidth(80);
			} /*
			 * else if (name.equalsIgnoreCase(WELL_POSITION)) { col.setPreferredWidth(90); }
			 */else if (name.equalsIgnoreCase(STATUS)) {
				col.setPreferredWidth(120);
			} else if (name.equalsIgnoreCase(COMPOUND_ID)) {
				col.setPreferredWidth(100);
			} else if (name.equalsIgnoreCase(PLATE)) {
				col.setPreferredWidth(80);
			} else if (name.equalsIgnoreCase(SALT_CODE)) {
				col.setPreferredWidth(120);
			} else if (name.equalsIgnoreCase(SALT_EQ)) {
				col.setPreferredWidth(80);
			} else if (name.equalsIgnoreCase(PRODUCT_PLATE)) {
				col.setPreferredWidth(150);
			} else {
				col.setPreferredWidth(100);
			}
		}
	}

	public void highlightLastRow() {
		int lastrow = this.getModel().getRowCount();
		if (lastrow > 0)
			this.setRowSelectionInterval(lastrow - 1, lastrow - 1);
	}

	private void setColValueItemActionPerformed(String message) {
		PCeNTableModel model = (PCeNTableModel) this.getModel();
		// String message = "Set Value for Column";
		String SET = "Set";
		String CANCEL = "Cancel";
		Object[] options = { SET, CANCEL };
		JComponent popUpComponent = null;
		ButtonGroup group = null;
		if (message.endsWith(SALT_CODE) || message.endsWith(SALT_EQ)) {
			FormLayout pLayout = new FormLayout("5dlu, pref, 5dlu, pref, 5dlu", "5dlu,pref, 5dlu,pref, 5dlu");
			JPanel p = new JPanel();
			CellConstraints mCellConstraints = new CellConstraints();
			JLabel saltCodeLabel = new JLabel(SALT_CODE);
			JLabel saltEQLabel = new JLabel(SALT_EQ);
			final CeNComboBox tempjComboBoxSaltCd = new CeNComboBox();
			final PAmountComponent saltEqamountComponent = new PAmountComponent(new AmountModel(UnitType.SCALAR));
			// saltEqamountComponent.addAmountEditListener(this);
			saltEqamountComponent.setBorder(BorderFactory.createLoweredBevelBorder());
			tempjComboBoxSaltCd.setPrototypeDisplayValue("wwwwwwwww");
			CodeTableUtils.fillComboBoxWithSaltCodes(tempjComboBoxSaltCd);
			tempjComboBoxSaltCd.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent itemevent) {
					if (tempjComboBoxSaltCd.getSelectedIndex() > 0)
						saltEqamountComponent.setEnabled(true);
					else
						saltEqamountComponent.setEnabled(false);
				}
			});
			p.setLayout(pLayout);
			p.add(saltCodeLabel, mCellConstraints.xy(2, 2));
			p.add(tempjComboBoxSaltCd, mCellConstraints.xy(4, 2));
			p.add(saltEQLabel, mCellConstraints.xy(2, 4));
			p.add(saltEqamountComponent, mCellConstraints.xy(4, 4));
			popUpComponent = p;
			saltEqamountComponent.setEditable(true);
			// saltEqamountComponent.setEnabled(false);
		} else if (message.endsWith(EXTRA_NEEDED)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.MOLES, "MMOL");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(PARENT_MW)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.SCALAR, "");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(BATCH_MW)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.SCALAR, "");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(DELIVERED_WEIGHT)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.MASS, "MG");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(TOTAL_VOLUME_DELIVERED)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.VOLUME, "ML");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(RXN_WEIGHT)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.MASS, "MG");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(TOTAL_WEIGHT)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			/*
			 * amountComponent.setEditable(true); amountComponent.setValueSetTextColor(null);
			 * amountComponent.setBorder(BorderFactory.createLoweredBevelBorder());
			 */Unit2 un = new Unit2(UnitType.MASS, "MG");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(TOTAL_VOLUME)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.VOLUME, "ML");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(TOTAL_MOLES)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.MOLES, "MMOL");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(AMT_IN_WELL_WT)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.MASS, "MG");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(AMT_IN_WELL_MOLES)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.MOLES, "MMOL");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(AMT_IN_VIAL_WT)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.MASS, "MG");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(AMT_IN_WELL_VOL)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.VOLUME, "ML");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(TOTAL_MOLES)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.MOLES, "MMOL");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(RXN_MOLES)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.MOLES, "MMOL");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(RXN_EQ)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.MOLAR, "");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(RXN_VOLUME)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.VOLUME, "ML");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(DELIVERED_MOLES)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.MOLES, "MMOL");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(DEAD_VOLUME)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.VOLUME, "ML");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(DENSITY)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.DENSITY, "GML");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(AMT_IN_WELL_MOLARITY)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.MOLAR, "M");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(AMT_MADE)) {
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.MASS, "MG");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(WELL_SOLVENT)) {
			popUpComponent = jComboWellSolvent;
		} else if (message.endsWith(COMPOUND_STATE)) {
			popUpComponent = jComboCompoundState;
		} else if (message.endsWith(STATUS)) {
			popUpComponent = jComboBoxStatus;
		} else if (message.endsWith(STRUCTURE_COMMENTS)) {
			popUpComponent = new JTextField();
		} else if (message.endsWith(STEREOISOMER)) {
			popUpComponent = comboBoxStereoisomer;
		} else if (message.endsWith(PURITY)) { // NOTE !!! This has to come before analytical purity
			PAmountCellEditor amountComponent = new PAmountCellEditor();
			Unit2 un = new Unit2(UnitType.SCALAR, "");
			amountComponent.getAmount().setUnit(un);
			amountComponent.updateDisplay();
			popUpComponent = amountComponent;
		} else if (message.endsWith(ANALYTICAL_PURITY)) {
			List<Properties> al_purities = new ArrayList<Properties>();
			try {
				al_purities = CodeTableCache.getCache().getPurityDeterminedBy();
			} catch (CodeTableCacheException e1) {
				log.error("Failed to get purity types from code table cache.", e1);
				return;
			}
			ProductBatchModel productBatch = new ProductBatchModel(); // old - non-model
			// NB -- does not use the popup component
			PurityInfoJDialog.showGUI(MasterController.getGUIComponent(), productBatch, al_purities, new JTextArea(), pageModel);
			List<PurityModel> newPurityModelList = productBatch.getAnalyticalPurityList();
			// ArrayList newPurityModelList = (ArrayList)
			// CeN11To12ConversionUtils.convertPurityListToPurityModelList(newPurityList);
			if (newPurityModelList != null && newPurityModelList.size() > 0) {
				model.updateColumn(workingColumnName, newPurityModelList);
				model.fireTableDataChanged();
			}
			return;
		} else if (message.endsWith(MELTING_POINT)) {
			ProductBatchModel productBatch = new ProductBatchModel(); // old - non-model
			JLabel jLabelC = new JLabel();
			Font PLAIN_LABEL_FONT = new java.awt.Font(CommonUtils.getStandardTableCellFont(), Font.PLAIN, 12);
			jLabelC.setFont(PLAIN_LABEL_FONT);
			MeltingPointJDialog.showGUI(MasterController.getGUIComponent(), productBatch, new JTextArea(), jLabelC, pageModel);
			TemperatureRangeModel newTempRangeModel = productBatch.getMeltPointRange();
			if (newTempRangeModel != null) {
				model.updateColumn(workingColumnName, newTempRangeModel);
				model.fireTableDataChanged();
			}
			return;
		} else if (message.endsWith(PCeNTableView.PRECURSORS)) {
			String precursorsStr = "";
			if(!model.getConnector().getPageModel().isParallelExperiment()) {
				precursorsStr = model.getConnector().getPageModel().constructCommonSingletonPrecursorsString();
			}
			popUpComponent = new JTextField(precursorsStr, 20);
		} else if (message.endsWith(PCeNTableView.HAZARD_COMMENTS) && message.indexOf("Compound") < 0) {
			ProductBatchModel associatedBatch = new ProductBatchModel();
			associatedBatch.setRegInfo(new BatchRegInfoModel(associatedBatch.getKey()));
			JDialogHealthHazInfo jDialogHealthHazInfo = new JDialogHealthHazInfo(MasterController.getGUIComponent(), pageModel);
			Point loc = MasterController.getGuiController().getGUIComponent().getLocation();
			Dimension dim = MasterController.getGuiController().getGUIComponent().getSize();
			jDialogHealthHazInfo.setLocation(loc.x + (dim.width - jDialogHealthHazInfo.getSize().width) / 2, loc.y
					+ (dim.height - jDialogHealthHazInfo.getSize().height) / 2);
			jDialogHealthHazInfo.setHandlingList(batchUtility.getHandlingList());
			jDialogHealthHazInfo.setHazardList(batchUtility.getHazardsList());
			jDialogHealthHazInfo.setStorageList(batchUtility.getStorageList());
			jDialogHealthHazInfo.setHandlingMap(batchUtility.getHandlingMap());
			jDialogHealthHazInfo.setHazardMap(batchUtility.getHazardsMap());
			jDialogHealthHazInfo.setStorageMap(batchUtility.getStorageMap());
			jDialogHealthHazInfo.setSelectedBatch(associatedBatch);
			if (jDialogHealthHazInfo.getHealthHazInfo()) {
				// updateDisplay(); // update
				try {
					model.updateColumn(workingColumnName, associatedBatch);
					model.updateColumn(HANDLING_PRECAUTIONS, associatedBatch);
					model.updateColumn(STORAGE_INSTRUCTIONS, associatedBatch);
					model.fireTableDataChanged();
				} catch (NumberFormatException e1) {
					CeNErrorHandler.getInstance().logExceptionMsg(e1);
				} catch (Exception e1) {
					CeNErrorHandler.getInstance().logExceptionMsg(e1);
				}
			}
			associatedBatch.setRegInfo(null);
			associatedBatch = null;
			jDialogHealthHazInfo = null;
			return;
		} else if (message.endsWith(EXT_SUPPLIER)) {
			List<Properties> extSupplierList = new ArrayList<Properties>();
			try {
				extSupplierList = CodeTableCache.getCache().getVendors();
			} catch (CodeTableCacheException e1) {
				log.error("Failed to get external supliers list from code table cache.", e1);
				return;
			}
			ProductBatchModel productBatchModel = new ProductBatchModel();
			ExtSuplInfoJDialog.showGUI(MasterController.getGUIComponent(),
			                           productBatchModel, 
			                           extSupplierList,
			                           new JTextArea(),
			                           pageModel);
			ExternalSupplierModel externalSupplierModel = productBatchModel.getVendorInfo();
			// if (externalSupplierModel != null) {
			model.updateColumn(workingColumnName, externalSupplierModel);
			model.fireTableDataChanged();
			// }
			return;
		} else if (message.endsWith(SOURCE) || message.endsWith(SOURCE_DETAILS)) {
			JPanel p = new JPanel(new GridLayout(2, 2));
			JLabel sourceLabel = new JLabel(SOURCE);
			JLabel sourceDetailsLabel = new JLabel(SOURCE_DETAILS);
			/*
			 * comboBoxCompoundSource.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent evt) {
			 * System.out.println("Action Listener invoked"); batchUtility.fillSouceDetailComboBox(comboBoxCompoundSource,
			 * comboBoxSourceDetail); } });
			 */
			p.add(sourceLabel);
			p.add(comboBoxCompoundSource);
			p.add(sourceDetailsLabel);
			p.add(comboBoxSourceDetail);
			popUpComponent = p;
			// ((JComboBox)popUpComponent).setPrototypeDisplayValue("wwwwwwwww");
			// ((JComboBox)popUpComponent).setPrototypeDisplayValue("wwwwwwwww");
		} else if (message.endsWith(COMPOUND_PROTECTION)) {
			popUpComponent = comboBoxCompoundProtection;
			((JComboBox) popUpComponent).setPrototypeDisplayValue("wwwwwwwww");
			model.fireTableDataChanged();
		} else if (message.endsWith("Plate Barcode")) {
			((PCeNRegistration_SummaryViewControllerUtility) connector).getNewGBLPlateBarCodesFromCompoundManagement(null);
			// ParallelCeNTableModel model = (ParallelCeNTableModel) this.getModel();
			return;
		} else if (message.endsWith(PCeNTableView.SELECT) || message.endsWith(LIMITING) || message.endsWith(PCeNTableView.SELECT_OPTION)) {
			JPanel p = new JPanel(new GridLayout(0, 1, 3, 3));
			JRadioButton b1 = new JRadioButton("Select All");
			JRadioButton b2 = new JRadioButton("Deselect All");
			b1.setSelected(true);

			p.add(b1);
			p.add(b2);

			group = new ButtonGroup();
			group.add(b1);
			group.add(b2);
			popUpComponent = p;
		}
		// else if (message.endsWith("Extra Needed(mMol)")) {
		// popUpComponent = extraNeededTextField;
		// }
		else if (message.endsWith(WT_IN_TUBE_VIAL)) {
		  PAmountCellEditor amountComponent = new PAmountCellEditor();
      Unit2 un = new Unit2(UnitType.MASS, "MG");
      
      amountComponent.getAmount().setUnit(un);
      amountComponent.updateDisplay();
      popUpComponent = amountComponent;
		}
		else if (message.endsWith(VOL_IN_TUBE_VIAL)) {
      PAmountCellEditor amountComponent = new PAmountCellEditor();
      Unit2 un = new Unit2(UnitType.VOLUME, "ML");
      
      amountComponent.getAmount().setUnit(un);
      amountComponent.updateDisplay();
      popUpComponent = amountComponent;
		}
		else if (message.endsWith(BARCODE_TUBE_VIAL)) {
      popUpComponent = new JTextField(20);
		}
		else if (message.endsWith(SOLVENT)) {
      popUpComponent = jComboWellSolvent;
		}
		else if (message.endsWith(CONTAINER_TYPE)) {
		  popUpComponent = jComboContainerType;
		}
		else
			popUpComponent = new JTextField(20);
		JOptionPane pane = new JOptionPane(new Object[] { message, popUpComponent }, JOptionPane.QUESTION_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION, null, options);
		CeNKeyHandlerOptionPane.setFocusComponent(popUpComponent);
		Object result = CeNKeyHandlerOptionPane.showOptionPane(MasterController.getGUIComponent(), pane);
		// vb 12/5 The JOptionPane closes if the uses presses the enter key, so replace it with a dialog that ignore enter key and
		// space key.
		// int result = JOptionPane.showOptionDialog(MasterController.getGUIComponent(), new Object[] { message, popUpComponent },
		// workingColumnName, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
		// options[0]);

		if (((String) result != null) && ((String) result).equalsIgnoreCase(SET)) {
			if (popUpComponent instanceof JTextField) {
				model.updateColumn(workingColumnName, ((JTextField) popUpComponent).getText());
				model.fireTableDataChanged();
			} else if (popUpComponent instanceof PAmountCellEditor) {
				model.updateColumn(workingColumnName, (PAmountCellEditor) popUpComponent);
				model.fireTableDataChanged();
			} else if (popUpComponent instanceof JPanel) {
				if (workingColumnName.equals(SOURCE + " and " + SOURCE_DETAILS)) {
					String source = comboBoxCompoundSource.getSelectedItem() == null ? "" : comboBoxCompoundSource
							.getSelectedItem().toString();
					String sourceDetails = comboBoxSourceDetail.getSelectedItem() == null ? "" : comboBoxSourceDetail
							.getSelectedItem().toString();
					model.updateColumn(SOURCE, source);
					model.updateColumn(SOURCE_DETAILS, sourceDetails.toString());
				} else if (workingColumnName.equals(SALT_CODE + " and " + SALT_EQ)) {
					CeNComboBox tempjComboBoxSaltCd = (CeNComboBox) popUpComponent.getComponent(1);
					String saltCode = tempjComboBoxSaltCd.getSelectedItem() == null ? "" : tempjComboBoxSaltCd.getSelectedItem()
							.toString();
					PAmountComponent saltEQ = (PAmountComponent) popUpComponent.getComponent(3);
					model.updateColumn(SALT_CODE, saltCode);
					model.updateColumn(SALT_EQ, saltEQ);
				} else // Registration tab Batch selections
				{
					JRadioButton selectedButton = getSelection(group);
					if (selectedButton.getText().equals("Select All"))
						model.updateColumn(workingColumnName, "true");
					else
						model.updateColumn(workingColumnName, "false");
				}
				model.fireTableDataChanged();
			} else if (popUpComponent instanceof JComboBox) {
				JComboBox comboBox = (JComboBox) popUpComponent;
				model.updateColumn(workingColumnName, comboBox.getSelectedItem());
				model.fireTableDataChanged();
			} else {
				// model.updateColumn(workingColumnName,
				// (((JComboBox)popUpComponent).getModel().getSelectedItem()).toString().substring(0, 2));
				// model.fireTableDataChanged();
			}

		}
	}

	// This method returns the selected radio button in a button group
	private static JRadioButton getSelection(ButtonGroup group) {
		for (Enumeration<AbstractButton> e = group.getElements(); e.hasMoreElements();) {
			JRadioButton b = (JRadioButton) e.nextElement();
			if (b.getModel() == group.getSelection()) {
				return b;
			}
		}
		return null;
	}

	/**
	 * Actually sets the table cell editors except in the case of amounts and combo boxes, which get non default renderers.
	 */
	protected void setTableViewCellRenderers() {

		int colCount = getColumnCount();
		for (int i = 0; i < colCount; i++) {
			String name = getColumnName(i);
			if (name == null) {
				return;
			}
			setRenderers(name);
		}// end for
	}// end for

	public void setRenderers(String name) {
		TableColumn col = getColumn(name);
		col.setCellRenderer(cellRenderer);
		col.setHeaderRenderer(headerRenderer);

		if (name.equalsIgnoreCase(STRUCTURE)) {
			col.setCellRenderer(new StructureTableCellRenderer());
			col.setCellEditor(new AnalyticalTableNotEditableCell());	
		} else if (name.equalsIgnoreCase(SALT_CODE)) {
			// this.jComboBoxSaltCd.setPopupWidth(200);
			col.setCellRenderer(new CeNTableCellAlignedComponentRenderer(new CeNComboBox()));
			col.setCellEditor(new PCeNTableViewCellEditor(new CeNComboBox()));
			// } else if (name.equalsIgnoreCase(COMPOUND_STATE)) {
			// //this.jComboCompoundState.setPopupWidth(150);
			// col.setCellEditor(this.compoundStateEditor);
			// col.setCellRenderer(new ComboBoxRenderer());
		} else if (name.equalsIgnoreCase(COMPOUND_PROTECTION)) {
			// this.comboBoxCompoundProtection.setPopupWidth(150);
			col.setCellEditor(new PCeNTableViewCellEditor(new CeNComboBox()));
			// col.setCellRenderer(new CeNTableCellAlignedComponentRenderer(comboBoxCompoundProtection));
			col.setCellRenderer(new CeNTableCellAlignedComponentRenderer(new CeNComboBox()));
		} else if (name.equalsIgnoreCase(RXN_ROLE)) {
			col.setCellEditor(reagentTypeEditor);
		} else if (name.equalsIgnoreCase(TIMES_USED)) {
			col.setCellEditor(timesUsedCellEditor);
		} else if (name.equalsIgnoreCase(NBK_BATCH_NUM)) {
			// col.setCellEditor(nbkBatchNoCellEditor);
			JTextField textField = new JTextField();
			textField.setHorizontalAlignment(JTextField.CENTER);
			textField.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					if (getSelectedRow() != seletedRow) {
						valueChanged();
					}
				}
				public void focusLost(FocusEvent e) {
					if (getSelectedRow() == seletedRow) {
						valueChanged();
					}
				}
			});
			col.setCellEditor(new PCeNTableViewCellEditor(textField));
			/*
			 * textArea = new JTextArea(); textArea.setLineWrap(true); textArea.setWrapStyleWord(true); textArea.setEnabled(false);
			 * col.setCellRenderer(new CeNTableCellAlignedComponentRenderer(textArea));
			 */
		} else if (name.equalsIgnoreCase(TOTAL_VOLUME_NEEDED)) {
			col.setCellRenderer(new PAmountCellRenderer());
		} else if (name.equalsIgnoreCase(DELIVERED_MONOMER)) {
			col.setCellEditor(new PCeNTableViewCellEditor(new JTextField()));
			col.setCellRenderer(new CeNTableCellAlignedComponentRenderer(new JLabel()));
		} else if (name.equalsIgnoreCase(ANALYTICAL_PURITY)) {
			col.setCellEditor(new PurityDialogEditor(pageModel));
			col.setCellRenderer(new CeNTableCellAlignedComponentRenderer(new JLabel()));
		} else if (name.equalsIgnoreCase(MELTING_POINT)) {
			col.setCellEditor(new MeltingPointDialogEditor(pageModel));
			col.setCellRenderer(new CeNTableCellAlignedComponentRenderer(new JLabel()));
		} else if (name.equalsIgnoreCase(STATUS)) {
			CeNStatusComboBoxRenderer renderer = new CeNStatusComboBoxRenderer(new CeNComboBox());
			col.setCellRenderer(renderer);
			col.setCellEditor(new PCeNTableViewCellEditor(new CeNComboBox(), renderer));
			// col.setCellRenderer(new CeNStatusComboBoxRenderer(CeNComboBoxStatus));
		} else if (name.equalsIgnoreCase(BATCH_ANALYTICAL_COMMENTS)) {
			col.setCellEditor(analyticalCommentsCellEditor);
		} else if (name.equalsIgnoreCase(COMPOUND_STATE)) {
			col.setCellEditor(new PCeNTableViewCellEditor(new CeNComboBox()));
			// col.setCellRenderer(compoundStateRenderer);
			col.setCellRenderer(new CeNTableCellAlignedComponentRenderer(new CeNComboBox()));
		} else if (name.equalsIgnoreCase(WELL_SOLVENT)) {
			col.setCellRenderer(new CeNTableCellAlignedComponentRenderer(new CeNComboBox()));
			col.setCellEditor(new PCeNTableViewCellEditor(new CeNComboBox()));
		} else if (name.equalsIgnoreCase(MOLARITY) || name.equalsIgnoreCase(TUBE_MOLARITY) || name.equalsIgnoreCase(PARENT_MW)
				|| name.equalsIgnoreCase(BATCH_MW) || name.equalsIgnoreCase(DELIVERED_WEIGHT) 
				|| name.equalsIgnoreCase(TOTAL_VOLUME_DELIVERED) 
				|| name.equalsIgnoreCase(DELIVERED_VOLUME) || name.equalsIgnoreCase(VOLUME) || name.equalsIgnoreCase(RXN_MOLES)
				|| name.equalsIgnoreCase(RXN_EQ) || name.equalsIgnoreCase(RXN_WEIGHT) || name.equalsIgnoreCase(RXN_VOLUME)
				|| name.equalsIgnoreCase(RXN_MOLES) || name.equalsIgnoreCase(DELIVERED_MOLES) || name.equalsIgnoreCase(DEAD_VOLUME)
				|| name.equalsIgnoreCase(DENSITY) || name.equalsIgnoreCase(SALT_EQ) || name.equalsIgnoreCase(EXTRA_NEEDED)
				|| name.equalsIgnoreCase(TOTAL_ORDERED)
				|| name.equalsIgnoreCase(TOTAL_WEIGHT)
				|| name.equalsIgnoreCase(TOTAL_VOLUME)
				|| name.equalsIgnoreCase(TOTAL_MOLES)
				|| name.equalsIgnoreCase(TOTAL_DELIVERED_AMT)
				|| name.equalsIgnoreCase(AMT_NEEDED_RXN)
				|| name.equalsIgnoreCase(AMT_MADE)
				|| name.equalsIgnoreCase(PURITY)
				// || name.equalsIgnoreCase(TOTAL_AMT_MADE)
				|| name.equalsIgnoreCase(TOTAL_MOLES_NEEDED) || name.equalsIgnoreCase(TOTAL_WEIGHT_NEEDED)
				|| name.equalsIgnoreCase(AMT_REMAINING_WEIGHT)
				|| name.equalsIgnoreCase(AMT_REMAINING_VOLUME)
				|| name.equalsIgnoreCase(AMT_IN_WELL_WT)
				|| name.equalsIgnoreCase(AMT_IN_WELL_VOL)
				// || name.equalsIgnoreCase(AMT_IN_TUBE_WT)
				|| name.equalsIgnoreCase(AMT_IN_TUBE_VOL) || name.equalsIgnoreCase(AMT_IN_VIAL_WT)
				|| name.equalsIgnoreCase(AMT_IN_WELL_MOLARITY) || name.equalsIgnoreCase(AMT_IN_WELL_MOLES)
				//|| name.equalsIgnoreCase(THEORETICAL_WEIGHT) || name.equalsIgnoreCase(THEORETICAL_MMOLES)
				|| name.equalsIgnoreCase(PERCENT_YIELD)) {
			col.setCellEditor(new PAmountCellEditor());
			col.setCellRenderer(new PAmountCellRenderer());
		} else if (name.equalsIgnoreCase(THEORETICAL_WEIGHT) || name.equalsIgnoreCase(THEORETICAL_MMOLES)) {
			col.setCellEditor(new PAmountCellEditorEx());
			col.setCellRenderer(new PAmountCellRenderer());
		} else if (name.equalsIgnoreCase(BATCH_COMMENTS)) {
			col.setCellEditor(batchCommentsCellEditor);
			/*
			 * col.setCellEditor(new PCeNTableViewCellEditor(new CeNComboBox())); col.setCellRenderer(new
			 * CeNTableCellAlignedComponentRenderer(new JTextArea()));
			 */} else if (name.equalsIgnoreCase(STRUCTURE_COMMENTS)) {
			JTextArea textArea = new JTextArea();
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					if (getSelectedRow() != seletedRow) {
						valueChanged();
					}
				}
				public void focusLost(FocusEvent e) {
					if (getSelectedRow() == seletedRow) {
						valueChanged();
					}
				}
			});
			col.setCellEditor(new PCeNTableViewCellEditor(textArea));

			textArea = new JTextArea();
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setEnabled(false);
			col.setCellRenderer(new CeNTableCellAlignedComponentRenderer(textArea));
		} else if (name.equalsIgnoreCase(HAZARD_COMMENTS)) {
			col.setCellEditor(new HazardsDialogEditor(pageModel));
		} else if (name.equalsIgnoreCase(EXT_SUPPLIER)) {
			col.setCellEditor(new ExternalSupplierDialogEditor(pageModel));
		} else if (name.equalsIgnoreCase(SOURCE)) {
			col.setCellEditor(new PCeNTableViewCellEditor(new CeNComboBox()));
			col.setCellRenderer(new CeNTableCellAlignedComponentRenderer(new CeNComboBox()));
		} else if (name.equalsIgnoreCase(SOURCE_DETAILS)) {
			// col.setCellEditor(new CeNCompoundSourceDetailsEditor());
			col.setCellEditor(new PCeNTableViewCellEditor(new CeNComboBox()));
			// col.setCellRenderer(new CeNTableCellAlignedComponentRenderer(comboBoxSourceDetail));
			col.setCellRenderer(new CeNTableCellAlignedComponentRenderer(new CeNComboBox()));
		} else if (name.equalsIgnoreCase(COMMENTS)) {
			JTextArea textArea = new JTextArea();
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					if (getSelectedRow() != seletedRow) {
						valueChanged();
					}
				}
				public void focusLost(FocusEvent e) {
					if (getSelectedRow() == seletedRow) {
						valueChanged();
					}
				}
			});
			col.setCellEditor(new PCeNTableViewCellEditor(textArea));

			textArea = new JTextArea();
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setEnabled(false);
			col.setCellRenderer(new CeNTableCellAlignedComponentRenderer(textArea));
		} else if (name.equalsIgnoreCase(PLATE_COMMENTS) || name.equalsIgnoreCase(PCeNSummaryViewControllerUtility.PLATE_COMMENTS)) {
			col.setCellRenderer(cellRenderer);
			col.setCellEditor(super.getCellEditor());
		} else if (name.equalsIgnoreCase(VIAL_BARCODE)) {
			col.setCellEditor(new DefaultCellEditor(new JTextField()));
		} else {
			col.setCellEditor(cellEditor);
		}
	}

	public String getSortedColumnName() {
		return sortedColumnName;
	}

	private void tableHeaderRightClicked(MouseEvent e) {
		// ParallelCeNTableModel model = ((ParallelCeNTableModel)
		if (e.isPopupTrigger()) {

			JTableHeader header = this.getTableHeader();
			int x = e.getX();
			int y = e.getY();
			int index0 = header.columnAtPoint(new Point(x, y));
			workingColumnName = getColumnName(index0);
			if (!this.getConnector().getPageModel().isEditable()) {
				setColValueItem.setText("<html><font name=" + CommonUtils.getStandardTableCellFont() + ">"
						+ "This experiment is not editable");
				setColValueItem.setEnabled(false);
			} else if (this.getConnector().isColumnEditable(workingColumnName) && (!workingColumnName.equals(NBK_BATCH_NUM))) {// Exclusions
				// if (e.isPopupTrigger()) { //&& tableModel.isColumnEditable(index0)) {// show popup
				if (workingColumnName.equals(SOURCE) || workingColumnName.equals(SOURCE_DETAILS))
					workingColumnName = SOURCE + " and " + SOURCE_DETAILS;
				else if (workingColumnName.equals(SALT_CODE) || workingColumnName.equals(SALT_EQ))
					workingColumnName = SALT_CODE + " and " + SALT_EQ;
				setColValueItem.setText("<html><font name=" + CommonUtils.getStandardTableCellFont() + ">Set value for "
						+ workingColumnName);
				setColValueItem.setEnabled(true);
				// }
			} else {
				setColValueItem.setText("<html><font name=" + CommonUtils.getStandardTableCellFont() + ">"
						+ workingColumnName + " is not editable");
				setColValueItem.setEnabled(false);
			}
			headerPopupMenu.show(e.getComponent(), e.getX(), e.getY());

			if (getConnector().getRowCount() == 0) {
				showDefaultUnitMenu.setEnabled(false);
			} else {
				int realIndex = ((PCeNTableModel)getModel()).getModelIndexFromHeaderName(workingColumnName);
				if (realIndex > -1) {
					CellEditor cE = getCellEditor(0, realIndex);
					if (!(cE instanceof PAmountCellEditor)) {
						showDefaultUnitMenu.setEnabled(false);
					} else {
						PAmountCellEditor pACE = (PAmountCellEditor)cE;
						pACE.setValue(getConnector().getValue(0, realIndex));
						if (pACE.isComboDisabled()) {
							showDefaultUnitMenu.setEnabled(false);
						} else {
							fillMenuWithUnit(showDefaultUnitMenu, realIndex, pACE.getAmount().getUnitType());
							showDefaultUnitMenu.setEnabled(showDefaultUnitMenu.getItemCount() > 0);
						}
					}
				}
			}
		}

		/*
		 * fix bug artf54926 : Product Batch Detail should display correct value following edit of source/source detail in table 16
		 * MAR 09 KS
		 */
		/*---------------------------------------------*/

		if (this.getParent().getComponents()[0] instanceof PCeNBatchInfoTableView) {

			PCeNBatchInfoTableView container = (PCeNBatchInfoTableView) this.getParent().getComponents()[0];

			this.clearSelection(); // reset chosen rows
			BatchSelectionEvent batchSelectionEvent = new BatchSelectionEvent(this, null);
			for (int i = 0; i < container.getProductBatchDetailsContainerListenerList().length; i++) {
				BatchSelectionListener batchSelectionListener = container.getProductBatchDetailsContainerListenerList()[i];
				batchSelectionListener.batchSelectionChanged(batchSelectionEvent);
			}
		}

		/*----------------------------------------------*/

	}

	/**
	 * The menu is filled with unit types and the current unit is selected.
	 */
	private void fillMenuWithUnit(JMenu menu, final int columnIndex, UnitType unitType) {
		if (menu != null && unitType != null) {
			menu.removeAll();
			List<Unit2> li = UnitCache2.getInstance().getUnitsOfTypeSorted(unitType);
			for (int i = 0; i < li.size(); i++) {
				final Unit2 unit = li.get(i);
				if ("".equals(unit.toString())) {
					continue;
				}
				JMenuItem menuItem = new JMenuItem(unit.toString()); 
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						for (int i = 0; i < getConnector().getRowCount(); i++) {
							AmountModel amountModel = (AmountModel)((AmountModel)getConnector().getValue(i, columnIndex)).deepClone();
							amountModel.setUnit(unit);
							getConnector().setValue(amountModel, i, columnIndex);
						}
					}
				});
				menu.add(menuItem);
			}
		}
	}

	
	public void stateChanged(ChangeEvent e) {
		int height = ((JSlider) e.getSource()).getValue();
		setRowHeight(height);
		this.setSelectedAreas(null);
		repaint();
	}

	public PCeNTableModelConnector getConnector() {
		return connector;
	}

	public void mouseReleased(MouseEvent evt) {
		mTableViewPopupMenuManager.mouseReleased(evt);
	}

	private BatchSelectionListener batchSelectionListener = null;
	private int seletedRow = -1;

	public void setProductBatchDetailsContainerListener(BatchSelectionListener batchSelectionListener) {
		this.batchSelectionListener = batchSelectionListener;
	}

	/**
	 * Used to signal batch info panels that the selected batch has changed.
	 */
	// This method is not called from anywhere. Need to check and remove this.
	public void valueChanged() {
		// if (event.getSource() instanceof ProductBatchDetailsContainer) {
		if (batchSelectionListener != null) {
			int i = this.getSelectedRow();

			if (i == -1)
				return;
			//if (i != seletedRow) {
			seletedRow = i;

			if (getModel() instanceof PCeNTableModel) {

				PCeNTableModel model = (PCeNTableModel) getModel();
				PCeNTableModelConnector connector = model.getConnector();
				if (connector instanceof PCeNProductPlatesTableModelConnector) {
					PCeNProductPlatesTableModelConnector utility = (PCeNProductPlatesTableModelConnector) connector;
					BatchSelectionEvent batchSelectionEvent = new BatchSelectionEvent(this, utility.getPlateWell(i));
					batchSelectionListener.batchSelectionChanged(batchSelectionEvent);

					/*
					 * } else if (connector instanceof ParallelCeNProductBatchTableViewControllerUtility) {
					 * ParallelCeNProductBatchTableViewControllerUtility utility =
					 * (ParallelCeNProductBatchTableViewControllerUtility) connector; Object obj =
					 * utility.getListOfBatchesWithMolStrings().get(i); BatchSelectionEvent batchSelectionEvent = new
					 * BatchSelectionEvent(this, utility.getListOfBatchesWithMolStrings().get(i));
					 * batchSelectionListener.batchSelectionChanged(batchSelectionEvent);
					 */// vb 7/23
				} /*
				 * else if (connector instanceof PCeNAnalytical_BatchDetailsPlateTableViewControllerUtility) {
				 * PCeNAnalytical_BatchDetailsPlateTableViewControllerUtility utility =
				 * (PCeNAnalytical_BatchDetailsPlateTableViewControllerUtility) connector; BatchSelectionEvent
				 * batchSelectionEvent = new BatchSelectionEvent(this, utility.getListOfBatchesWithMolStrings().get(i));
				 * batchSelectionListener.batchSelectionChanged(batchSelectionEvent); } else if (connector instanceof
				 * PCeNAnalytical_BatchDetailsProductTableViewControllerUtility) { // vb 8/9
				 * PCeNAnalytical_BatchDetailsProductTableViewControllerUtility utility =
				 * (PCeNAnalytical_BatchDetailsProductTableViewControllerUtility) connector; BatchSelectionEvent
				 * batchSelectionEvent = new BatchSelectionEvent(this, utility.getListOfBatchesWithMolStrings().get(i));
				 * batchSelectionListener.batchSelectionChanged(batchSelectionEvent); }
				 */
				else {
					BatchSelectionEvent batchSelectionEvent = new BatchSelectionEvent(this, connector.getAbstractBatches().get(i));
					batchSelectionListener.batchSelectionChanged(batchSelectionEvent);

					if(this.getColumnName(this.getSelectedColumn()).equals(PCeNTableView.SELECT_OPTION)) {
						ProductBatchModel productBatchModel = (ProductBatchModel)((PCeNTableModelConnector)connector).getBatchModel(seletedRow);
						productBatchModel.setSelected(!productBatchModel.isSelected());
						this.repaint();
					}
					
				}
			}
			//}
		}
		// }
	}
	/**
	 * For tool tips on headers to work, you have to subclass the JTableHeader and override the getToolTipText method. NOTE: This
	 * can be improved by checking if the column has changed.
	 */
	class ToolTipHeader extends JTableHeader {

		private static final long serialVersionUID = 1268029641089441019L;
		
		TableColumn col = null;

		ToolTipHeader(TableColumnModel tcm) {
			// Pass the TableColumnModel object to the superclass, which
			// takes care of that object.

			super(tcm);
		}

		// The following method is automatically called when the mouse
		// cursor hotspot moves over any one of the header rectangles in a
		// table header.

		public String getToolTipText(MouseEvent e) {
			// Return the pixel position of the mouse cursor hotspot.

//			Point p = e.getPoint();

			JTable table = getTable();
			TableColumnModel colModel = table.getColumnModel();
			int modelColumnIndex = colModel.getColumnIndexAtX(e.getX());

			if (modelColumnIndex >= 0) {
				col = colModel.getColumn(modelColumnIndex);
			}

			if (col != null && col.getHeaderValue() != null
					&& connector.getTableHeaderTooltip((String) col.getHeaderValue()) != null) {
				String toolTip = connector.getTableHeaderTooltip((String) col.getHeaderValue());

				if (toolTip == null || toolTip.length() == 0)
					return super.getToolTipText(e);
				else
					return toolTip;
			} else
				return "";
		}
	}

	public BatchModel getBatchAt(int selectedRow) {
		PCeNTableModel model = (PCeNTableModel) getModel();
		PCeNTableModelConnector connector = model.getConnector();
		return (BatchModel) connector.getBatchModel(selectedRow);
	}
	
	private void initHideColumnMenu(final JMenu menu) {
		int columnCount = getColumnModel().getColumnCount();
		JPanel hideColumnsPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(hideColumnsPanel, BoxLayout.Y_AXIS);
		hideColumnsPanel.setLayout(boxLayout);
		JMenuItem exitItem = new JMenuItem(EXIT_MENU_ITEM);
		exitItem.addActionListener(visibleActionListener);
		for (int i = 0; i < columnCount; ++i) {
			ColumnFurniture column = (ColumnFurniture) getColumnModel().getColumn(i);
			String headerValue = (String) column.getHeaderValue();
			
			JCheckBox item = null;
			
			if (StringUtils.equals(headerValue, PCeNTableView.STRUCTURE)) {
				item = structureCheckBox;
			} else {
				item = new JCheckBox();
			}
			
			item.setSelected(column.isVisible());
			item.setText(headerValue.replaceAll("\\<.*?>", ""));
			item.addMouseListener(new WorkaroundMouseAdapter());
			item.addActionListener(visibleActionListener);
			
			hideColumnsPanel.add(item);
		}
		menu.add(hideColumnsPanel);
		menu.add(exitItem);
		menu.setName(SHOW_HIDE_STRUCTURES);
	}

	public void showColumn(String columnName, boolean show, boolean updatePreferences) {
		int modelIndex = getIndexFromPanel(columnName);
		
		if (modelIndex != -1) {
			if (tablePreferenceDelegate == null) {
				TablePreferenceHandler.changeColumnVisibility(this, modelIndex, show);
			} else {
				tablePreferenceDelegate.changeColumnVisibility(this, modelIndex, show, updatePreferences);
			}
		}
		
		if (StringUtils.equals(columnName, PCeNTableView.STRUCTURE)) {
			if (modelIndex != -1) {
				getColumnModel().moveColumn(modelIndex, 0);
			}
			structureCheckBox.setSelected(show);
			toolBar.changeStructureCheckBox(show);			
		}
	}
	
	// Structure column is always hidden by default so no visibility update is needed
	public void showStructureColumn(boolean show) {
		int modelIndex = getIndexFromPanel(PCeNTableView.STRUCTURE);
		
		if (modelIndex != -1) {
			ColumnFurniture cf = ((ColumnFurniture) getColumnModel().getColumn(modelIndex));
			cf.setVisible(false);
			cf.setMinWidth(0);
			cf.setMaxWidth(0);
			cf.setPreferredWidth(0);

			getColumnModel().moveColumn(modelIndex, 0);		
			structureCheckBox.setSelected(show);
			toolBar.changeStructureCheckBox(show);			
		}
	}
	
	/** Hides column called "View" */
	public void hideViewColumn() {
		hideColumnAndRemoveCheckBoxFromMenu("View");
	}
	
	private void hideStatusColumn() {
		hideColumnAndRemoveCheckBoxFromMenu(STATUS);
	}

	/** Hides column called "Status" if this is a Singleton experiment */
	private void hideStatusColumnIfSingleton() {
		if (pageModel != null && pageModel.isSingletonExperiment()) {
			hideStatusColumn();
		}
	}
	
	private void hideColumnAndRemoveCheckBoxFromMenu(String columnName) {
		TableColumnModel columnModel = getColumnModel();
		if (columnModel != null) {
			int columnCount = columnModel.getColumnCount();
			for (int i = 0; i < columnCount; ++i) {
				ColumnFurniture column = (ColumnFurniture) columnModel.getColumn(i);
				if (StringUtils.equalsIgnoreCase(column.getHeaderValue().toString(), columnName)) {
					TablePreferenceHandler.changeColumnVisibility(PCeNTableView.this, i, false);
					removeCheckBoxFromMenu(columnName);
				}
			}
		}
	}
	
	/** Removes checkbox from Show/Hide columns menu */
	private void removeCheckBoxFromMenu(String columnName) {
		if (showHideColumnsMenu != null) {
			JPanel panelFromMenu = (JPanel) showHideColumnsMenu.getMenuComponent(0);
			int index = getIndexFromPanel(columnName);
			if (index != -1) {
				Component component = panelFromMenu.getComponent(index);
				if (component instanceof JCheckBox) {
					component.setVisible(false);
				}
			}
		}
	}

	/** Returns index of checkbox from Show/Hide columns menu */
	private int getIndexFromPanel(String text) {
		if (showHideColumnsMenu != null) {
			JPanel panelFromMenu = (JPanel) showHideColumnsMenu.getMenuComponent(0);
			for (int i = 0; i < panelFromMenu.getComponentCount(); ++i) {
				Component component = panelFromMenu.getComponent(i);
				if (component instanceof JCheckBox) {
					JCheckBox item = (JCheckBox) component;
					if (StringUtils.equalsIgnoreCase(item.getText(), text)) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	/** Class used by JCheckBoxMenuItem buttons action */
	private class VisibleAction implements ActionListener {
		/**
		 * Implementation of ActionListener interface used by the menu items to hide columns.
		 * 
		 * @param evt
		 *            an ActionEvent
		 */
		public void actionPerformed(ActionEvent evt) {
			Object source = evt.getSource();
			String command = evt.getActionCommand();
			if (source instanceof JCheckBox) {
				JCheckBox item = (JCheckBox) source;
				showColumn(command, item.isSelected(), true);
			} else if (source instanceof JCheckBoxMenuItem && command.equals(EXIT_MENU_ITEM)) {
				if (showHideColumnsMenu != null) {
					showHideColumnsMenu.setVisible(false);
				}
			}
		}
	}

	public TablePreferenceDelegate getTablePreferenceDelegate() {
		return tablePreferenceDelegate;
	}

	public void setTablePreferenceDelegate(TablePreferenceDelegate tablePreferenceDelegate) {
		this.tablePreferenceDelegate = tablePreferenceDelegate;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setValueAt(Object value, int row, int column) {
		super.setValueAt(value, row, column);
		seletedRow = row;
		this.valueChanged();
	}
	
	/**
	 * Workaround class for Java 5 bug <a
	 * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6222765"
	 * >http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6222765</a><br>
	 * If mouseReleased not fired after click on AbstractButton subclasses, it fires
	 * mouseReleased in mouseClicked
	 * 
	 * @see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6222765
	 * @see AbstractButton
	 */
	public class WorkaroundMouseAdapter extends MouseAdapter {

		private boolean released = false;
		private boolean clicked = false;

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!released) {
				Component source = e.getComponent();
				if (source instanceof AbstractButton) {
					AbstractButton button = (AbstractButton) source;
					MouseListener[] listeners = button.getMouseListeners();
					for (MouseListener listener : listeners) {
						clicked = true;
						listener.mouseReleased(e);
					}
				}
			}
			released = false;
			clicked = false;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			released = clicked;
		}
	}

	public void setToolBar(PCeNTableViewToolBar toolBar) {
		this.toolBar = toolBar;
	}
}
