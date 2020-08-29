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
package com.chemistry.enotebook.client.gui.page.reagents;

import com.chemistry.ChemistryEditorEvent;
import com.chemistry.ChemistryEditorListener;
import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.LoadServiceException;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.CeNInternalFrame;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.JTextFieldFilter;
import com.chemistry.enotebook.client.gui.page.ContainerFrame;
import com.chemistry.enotebook.domain.MonomerBatchModel;
import com.chemistry.enotebook.formatter.UtilsDispatcher;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.util.Stopwatch;
import com.chemistry.enotebook.utils.CeNJTable;
import com.chemistry.enotebook.utils.NumberTextField;
import com.chemistry.enotebook.utils.SwingWorker;
import com.chemistry.enotebook.utils.jtable.JSortTable;
import com.chemistry.viewer.ChemistryViewer;
import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import info.clearthought.layout.TableLayout;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.*;
import java.util.List;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class ReagentsFrame extends CeNInternalFrame implements MyReagentPropertyListener {
	
	private static final long serialVersionUID = -454803241481142720L;
	
	private static CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	private static final Log log = LogFactory.getLog(ReagentsFrame.class);
	
	public static final String REGISTERED_COMPOUND_HEADING = "Internal Registry #";
	public static final String ACD_HEADING = "External #";
	public static final int MY_REAGENTS_TAB = 0;
	public static final int SEARCH_REAGENTS_TAB = 1;
	public static final int LOOKUP_RESULTS_TAB = 2;
	public static final int THREAD_COUNT = 3;

	public final String NONE_VALUE = "-None-";
	public final String SUB_STRUCTURE = "Substructure";
	public final String SIMILARITY = "Similarity";
	public final String EQUALITY = "=";
	
	private JTextPane jTextPaneTextInstruction;
	private JPanel jPanelTextInstruction;
	private JPanel jPanelTextSearchMain;
	private JSortTable jTableReagentResults;
	private JScrollPane jScrollPane3;
	private JSortTable jTableMyReagents;
	private JScrollPane jScrollPane1;
	private CeNJTable jTableMyReagentProps;
	private JScrollPane jScrollPane2;
	private JPanel jPanelStructQuery;
	private ChemistryPanel chimeProResult;
	private ChemistryPanel chimeProMyReagent;
	private JButton jButtonResultBackToStoich;
	private JTable jTableReagentValues;
	private JScrollPane jScrollPane4;
	private JPanel jPanel26;
	private JLabel jLabel9;
	private JPanel jPanel25;
	private JPanel jPanel24;
	private JPanel jPanel23;
	private JLabel jLabel8;
	private JPanel jPanel22;
	private JPanel jPanel21;
	private JButton jButtonAddToStoich;
	private JButton jButtonAddToReagentList;
	private JPanel jPanel19;
	private JPanel jPanel18;
	private JLabel jLabelSimilarity;
	private JTextField jTextFieldSimilarity;
	private JComboBox jComboBoxSearchType;
	private JLabel jLabel5;
	private JPanel jPanelStructQueryAttrib;
	private JPanel jPanel15;
	private JPanel jPanelTestSearchItem;
	private JPanel jPanel14;
	private JPanel jPanelTextSearchCrit;
	private JLabel jLabel4;
	private JPanel jPanel13;
	private JLabel jLabel3;
	private JPanel jPanelRegCatalogs;
	private JPanel jPanel10;
	private JPanel jPanel9;
	private JButton jButtonLookupBackToStoich;
	private JButton jButtonSearch;
	private JPanel jPanel8;
	private JPanel jPanel7;
	private JPanel jPanel6;
	private JLabel jLabel2;
	private JButton jButtonMyRgtBackToStoich;
	private JButton jButtonAddtoStoich;
	private JPanel jPanel5;
	private JPanel jPanel4;
	private JLabel jLabel1;
	private JPanel jPanel3;
	private JPanel jPanel2;
	private JPanel jPanel1;
	private JPanel jPanelLookupResults;
	private JPanel jPanelLookupQuery;
	private JPanel jPanelReagentList;
	private JTabbedPane jTabbedPaneMain;
	private ChemistryViewer structQueryCanvas;
	private JPopupMenu popUpMenu;
	private JFrame parent;
	private ProgressBarDialog progressBarDialog = null;
	// data attributes
	private String myReagents = null;
	private MyReagentsTableModel myReagentsTableModel;
	private MyReagentsPropertyTableModel myReagentsPropertyTableModel;
	private ReagentsResultTableModel reagentResultsTableModel;
	private ReagentPropertyTableModel reagentResultsPropertyTableModel;
	private ReagentsHandlerCache reagentsHandler;
	private HashMap<String, String> searchTypeMap = new HashMap<String, String>();
	private String searchParamsXML = null;
	private HashMap<String, String> totalDBMap = new HashMap<String, String>();
	private ArrayList<MonomerBatchModel> reagentBatchList = new ArrayList<MonomerBatchModel>();
	private ArrayList<ReagentAdditionListener> reagentAdditionListeners = new ArrayList<ReagentAdditionListener>();
	private int selectedReagentIndex = -1;
	private int lastPosition = -1;
	private boolean doneDisplay = false;
	private boolean deleteInProgress = false;
	private boolean myReagentsPropChanging = false;
	private ArrayList<JCheckBox> catalogCheckBoxList = new ArrayList<JCheckBox>();
	private ArrayList<JCheckBox> criteriaCheckBoxList = new ArrayList<JCheckBox>();
	private ArrayList<JComboBox> criteriaComboBoxList = new ArrayList<JComboBox>();
	private ArrayList<JTextField> criteriaTextfieldList = new ArrayList<JTextField>();
	private ArrayList<String> selectedDBList = new ArrayList<String>();
	private ArrayList<TextSearchParamsVO> TextSerachParamsList = new ArrayList<TextSearchParamsVO>();
	private Map<String, String> reagentPropertiesMap;
    private static boolean isSolventsFrame = false;
    
	public ReagentsFrame(JFrame parent) {
		this.parent = parent;
		reagentsHandler = ReagentsHandlerCache.getInstance();
		searchTypeMap.put(SUB_STRUCTURE, "SUBSTRUCTURE");
		searchTypeMap.put(SIMILARITY, "SIMILARITY");
		searchTypeMap.put(EQUALITY, "EXACT");
		initData();
		initGUI();
		reagentsHandler.storeFrameLink(this);
	}

	/**
	 * @param int
	 *            tab
	 * @throws java.awt.HeadlessException
	 */
	public ReagentsFrame(JFrame parent, int tab) throws HeadlessException {
		this(parent);
		setSelectedTab(tab);
	}

	/**
	 * Initializes the Data.
	 */
	public void initData() {
		try {
			myReagents = reagentsHandler.getMyReagentList();
            reagentPropertiesMap = reagentsHandler.buildReagentParamsMap();
			reagentsHandler.buildDBXMLRoot();
			reagentsHandler.buildCompoundNumberMap();
			totalDBMap = reagentsHandler.buildDBMap();
			reagentsHandler.buildMandatoryFieldsMap();
		} catch (Exception e) {
			ceh.logExceptionMsg(this, e);
		}
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).setNorthPane(null);
			this.setBorder(null);
			jTabbedPaneMain = new JTabbedPane();
			jPanelReagentList = new JPanel();
			jPanel1 = new JPanel();
			jScrollPane1 = new JScrollPane();
			jTableMyReagents = new JSortTable();
			jPanel2 = new JPanel();
			jPanel3 = new JPanel();
			jLabel1 = new JLabel();
			chimeProMyReagent = new ChemistryPanel();
			jPanel4 = new JPanel();
			jLabel2 = new JLabel();
			jScrollPane2 = new JScrollPane();
			NumberTextField numtf = new NumberTextField();
			numtf.setToFloat(true);
			final DefaultCellEditor numEditor = new DefaultCellEditor(numtf);
			JTextField strtf = new JTextField();
			final DefaultCellEditor strEditor = new DefaultCellEditor(strtf);
			jTableMyReagentProps = new CeNJTable() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -1415899932874072862L;
				String text = null;
				String datatype = null;

				public TableCellEditor getCellEditor(int row, int column) {
					if (column > 0) {
						text = jTableMyReagentProps.getValueAt(row, 0).toString();
						datatype = (String) reagentPropertiesMap.get(text);
						if (datatype != null)
							if (datatype.equals("number"))
								return numEditor;
							else if (!datatype.equals("string")) {
								// If it's not a Number or String it's a combo-box.
								String[] values = datatype.split("\\|");
								if (values != null && values.length > 0)
									return new DefaultCellEditor(new JComboBox(values));
							}
					}
					return strEditor;
				}
			};
			jTableMyReagentProps.setRowHeight(20);
			jPanel5 = new JPanel();
			jButtonAddtoStoich = new JButton();
			jButtonMyRgtBackToStoich = new JButton();
			jPanelLookupQuery = new JPanel();
			jPanel6 = new JPanel();
			jLabel3 = new JLabel();
			jPanelRegCatalogs = new JPanel();
			jPanel7 = new JPanel();
			jPanel13 = new JPanel();
			jLabel4 = new JLabel();
			jPanel14 = new JPanel();
			jPanelTextSearchCrit = new JPanel();
			jPanelTextSearchMain = new JPanel();
			jPanelTextInstruction = new JPanel();
			jTextPaneTextInstruction = new JTextPane();
			jPanelTestSearchItem = new JPanel();
			jPanel15 = new JPanel();
			jPanelStructQueryAttrib = new JPanel();
			jLabel5 = new JLabel();
			jComboBoxSearchType = new JComboBox();
			jTextFieldSimilarity = new JTextField();
			jLabelSimilarity = new JLabel();
			jPanelStructQuery = new JPanel();
			jPanel8 = new JPanel();
			jPanel9 = new JPanel();
			jButtonSearch = new JButton();
			jPanel10 = new JPanel();
			jButtonLookupBackToStoich = new JButton();
			jPanelLookupResults = new JPanel();
			jScrollPane3 = new JScrollPane();
			jTableReagentResults = new JSortTable();
			
			jPanel18 = new JPanel();
			jPanel21 = new JPanel();
			jPanel22 = new JPanel();
			jLabel8 = new JLabel();
			jPanel23 = new JPanel();
			chimeProResult = new ChemistryPanel();
			jPanel24 = new JPanel();
			jPanel25 = new JPanel();
			jLabel9 = new JLabel();
			jPanel26 = new JPanel();
			jScrollPane4 = new JScrollPane();
			jTableReagentValues = new JTable();
			jPanel19 = new JPanel();
			jButtonAddToReagentList = new JButton();
			jButtonAddToStoich = new JButton();
			jButtonResultBackToStoich = new JButton();
			this.setSize(new java.awt.Dimension(750, 407));
			jTabbedPaneMain.setFont(new java.awt.Font("sansserif", 1, 11));
			jTabbedPaneMain.setPreferredSize(new java.awt.Dimension(720, 362));
			jTabbedPaneMain.setIgnoreRepaint(false);
			this.getContentPane().add(jTabbedPaneMain);
			BorderLayout jPanelReagentListLayout = new BorderLayout();
			jPanelReagentList.setLayout(jPanelReagentListLayout);
			jPanelReagentListLayout.setHgap(0);
			jPanelReagentListLayout.setVgap(0);
			jPanelReagentList.setPreferredSize(new java.awt.Dimension(715, 336));
			jTabbedPaneMain.add(jPanelReagentList);
			jTabbedPaneMain.setTitleAt(MY_REAGENTS_TAB, "My Reagent List");
			BorderLayout jPanel1Layout = new BorderLayout();
			jPanel1.setLayout(jPanel1Layout);
			jPanel1Layout.setHgap(0);
			jPanel1Layout.setVgap(0);
			jPanel1.setPreferredSize(new java.awt.Dimension(577, 120));
			jPanelReagentList.add(jPanel1, BorderLayout.NORTH);
			jScrollPane1.setPreferredSize(new java.awt.Dimension(715, 120));
			jPanel1.add(jScrollPane1, BorderLayout.CENTER);
			jScrollPane1.add(jTableMyReagents);
			jScrollPane1.setViewportView(jTableMyReagents);
			jTableMyReagents.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					jTableMyReagentsMouseClicked(e);
				}
				@Override
				public void mousePressed(MouseEvent e) {
					jTableMyReagentsMouseClicked(e);
				}
			});
			jTableMyReagents.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent evt) {
					if (!deleteInProgress && !evt.getValueIsAdjusting())
						jTableMyReagentsRowChanged(evt);
				}
			});
			BorderLayout jPanel2Layout = new BorderLayout();
			jPanel2.setLayout(jPanel2Layout);
			jPanel2Layout.setHgap(0);
			jPanel2Layout.setVgap(0);
			jPanelReagentList.add(jPanel2, BorderLayout.CENTER);
			BorderLayout jPanel3Layout = new BorderLayout();
			jPanel3.setLayout(jPanel3Layout);
			jPanel3Layout.setHgap(0);
			jPanel3Layout.setVgap(0);
			jPanel3.setPreferredSize(new java.awt.Dimension(338, 181));
			jPanel2.add(jPanel3, BorderLayout.WEST);
			jLabel1.setLayout(null);
			jLabel1.setText("     Reagent Structure:");
			jLabel1.setHorizontalAlignment(SwingConstants.LEADING);
			jLabel1.setFont(new java.awt.Font("sansserif", 1, 13));
			jPanel3.add(jLabel1, BorderLayout.NORTH);
			jPanel3.add(chimeProMyReagent, BorderLayout.CENTER);
			BorderLayout jPanel4Layout = new BorderLayout();
			jPanel4.setLayout(jPanel4Layout);
			jPanel4Layout.setHgap(0);
			jPanel4Layout.setVgap(0);
			jPanel4.setPreferredSize(new java.awt.Dimension(230, 199));
			jPanel2.add(jPanel4, BorderLayout.CENTER);
			jLabel2.setText("Reagent Property Values:");
			jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
			jLabel2.setFont(new java.awt.Font("sansserif", 1, 13));
			jLabel2.setPreferredSize(new java.awt.Dimension(346, 18));
			jPanel4.add(jLabel2, BorderLayout.NORTH);
			jScrollPane2.setPreferredSize(new java.awt.Dimension(346, 163));
			jPanel4.add(jScrollPane2, BorderLayout.CENTER);
			jScrollPane2.add(jTableMyReagentProps);
			jScrollPane2.setViewportView(jTableMyReagentProps);
			jPanelReagentList.add(jPanel5, BorderLayout.SOUTH);
			jButtonAddtoStoich.setText("Add To Stoich. Table");
			jButtonAddtoStoich.setFont(new java.awt.Font("sansserif", 1, 11));
			jButtonAddtoStoich.setPreferredSize(new java.awt.Dimension(150, 25));
			jPanel5.add(jButtonAddtoStoich);
			jButtonAddtoStoich.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonAddMyReagentsToStoichActionPerformed(evt);
				}
			});
			jButtonMyRgtBackToStoich.setText("Back To Stoich. Table");
			jButtonMyRgtBackToStoich.setFont(new java.awt.Font("sansserif", 1, 11));
			jButtonMyRgtBackToStoich.setPreferredSize(new java.awt.Dimension(150, 25));
			jPanel5.add(jButtonMyRgtBackToStoich);
			jButtonMyRgtBackToStoich.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonBackToStoichActionPerformed(evt);
				}
			});
			BorderLayout jPanelLookupQueryLayout = new BorderLayout();
			jPanelLookupQuery.setLayout(jPanelLookupQueryLayout);
			jPanelLookupQueryLayout.setHgap(0);
			jPanelLookupQueryLayout.setVgap(0);
			jPanelLookupQuery.setPreferredSize(new java.awt.Dimension(577, 315));
			jTabbedPaneMain.add(jPanelLookupQuery);
			jTabbedPaneMain.setTitleAt(SEARCH_REAGENTS_TAB, "Compound Management Query");
			BorderLayout jPanel6Layout = new BorderLayout();
			jPanel6.setLayout(jPanel6Layout);
			jPanel6Layout.setHgap(0);
			jPanel6Layout.setVgap(0);
			jPanel6.setPreferredSize(new java.awt.Dimension(577, 79));
			jPanelLookupQuery.add(jPanel6, BorderLayout.NORTH);
			FlowLayout jLabel3Layout = new FlowLayout();
			jLabel3.setLayout(jLabel3Layout);
			jLabel3Layout.setAlignment(FlowLayout.CENTER);
			jLabel3Layout.setHgap(5);
			jLabel3Layout.setVgap(5);
			jLabel3.setText("Reagent Catalogs To Search:");
			jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
			jLabel3.setHorizontalTextPosition(SwingConstants.CENTER);
			jLabel3.setVisible(true);
			jLabel3.setFont(new java.awt.Font("sansserif", 1, 13));
			jPanel6.add(jLabel3, BorderLayout.NORTH);
			GridLayout jPanelRegCatalogsLayout = new GridLayout(1, 1);
			jPanelRegCatalogs.setLayout(jPanelRegCatalogsLayout);
			jPanelRegCatalogsLayout.setRows(1);
			jPanelRegCatalogsLayout.setHgap(0);
			jPanelRegCatalogsLayout.setVgap(0);
			jPanelRegCatalogsLayout.setColumns(1);
			jPanelRegCatalogs.setPreferredSize(new java.awt.Dimension(715, 56));
			jPanelRegCatalogs.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			jPanel6.add(jPanelRegCatalogs, BorderLayout.CENTER);
			BorderLayout jPanel7Layout = new BorderLayout();
			jPanel7.setLayout(jPanel7Layout);
			jPanel7Layout.setHgap(0);
			jPanel7Layout.setVgap(0);
			jPanel7.setPreferredSize(new java.awt.Dimension(577, 204));
			jPanelLookupQuery.add(jPanel7, BorderLayout.CENTER);
			BoxLayout jPanel13Layout = new BoxLayout(jPanel13, 0);
			jPanel13.setLayout(jPanel13Layout);
			jPanel13.setToolTipText("Text Search Criteria:");
			jPanel13.setPreferredSize(new java.awt.Dimension(715, 16));
			jPanel13.setOpaque(false);
			jPanel7.add(jPanel13, BorderLayout.NORTH);
			jLabel4.setText("Text Search Criteria:");
			jLabel4.setHorizontalAlignment(SwingConstants.CENTER);
			jLabel4.setFont(new java.awt.Font("sansserif", 1, 13));
			jLabel4.setPreferredSize(new java.awt.Dimension(372, 16));
			jPanel13.add(jLabel4);
			jPanel14.setVisible(true);
			jPanel14.setPreferredSize(new java.awt.Dimension(389, 16));
			jPanel14.setOpaque(false);
			jPanel13.add(jPanel14);
			BorderLayout jPanelTextSearchCritLayout = new BorderLayout();
			jPanelTextSearchCrit.setLayout(jPanelTextSearchCritLayout);
			jPanelTextSearchCritLayout.setHgap(0);
			jPanelTextSearchCritLayout.setVgap(0);
			jPanel7.add(jPanelTextSearchCrit, BorderLayout.CENTER);
			BorderLayout jPanelTextSearchMainLayout = new BorderLayout();
			jPanelTextSearchMain.setLayout(jPanelTextSearchMainLayout);
			jPanelTextSearchMainLayout.setHgap(0);
			jPanelTextSearchMainLayout.setVgap(0);
			jPanelTextSearchMain.setVisible(true);
			jPanelTextSearchMain.setPreferredSize(new java.awt.Dimension(418, 204));
			jPanelTextSearchCrit.add(jPanelTextSearchMain, BorderLayout.WEST);
			BorderLayout jPanelTextInstructionLayout = new BorderLayout();
			jPanelTextInstruction.setLayout(jPanelTextInstructionLayout);
			jPanelTextInstructionLayout.setHgap(0);
			jPanelTextInstructionLayout.setVgap(0);
			jPanelTextInstruction.setVisible(true);
			jPanelTextSearchMain.add(jPanelTextInstruction, BorderLayout.NORTH);
			jTextPaneTextInstruction
					.setText("Please use correct case and add spaces between each element in the Molecular Formula, ex. 'C8 H9 Br' and use % character as wildcard between elements.  A Chemical Name 'Contains Search' performs a word search unless % is used to indicate a partial word, ex. 'hydroxyisoq%' for hydroxyisoquinoline, also some symbols affect 'Contains Search' search behavior so use a space instead.");
			jTextPaneTextInstruction.setEnabled(false);
			jTextPaneTextInstruction.setEditable(false);
			jTextPaneTextInstruction.setBackground(jPanelTextInstruction.getBackground());
			jPanelTextInstruction.add(jTextPaneTextInstruction, BorderLayout.CENTER);
			BorderLayout jPanelTestSearchItemLayout = new BorderLayout();
			jPanelTestSearchItem.setLayout(jPanelTestSearchItemLayout);
			jPanelTestSearchItemLayout.setHgap(0);
			jPanelTestSearchItemLayout.setVgap(0);
			jPanelTestSearchItem.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			jPanelTextSearchMain.add(jPanelTestSearchItem, BorderLayout.CENTER);
			BorderLayout jPanel15Layout = new BorderLayout();
			jPanel15.setLayout(jPanel15Layout);
			jPanel15Layout.setHgap(0);
			jPanel15Layout.setVgap(0);
			jPanel15.setPreferredSize(new java.awt.Dimension(306, 203));
			jPanelTextSearchCrit.add(jPanel15, BorderLayout.EAST);
			jPanelStructQueryAttrib.setPreferredSize(new java.awt.Dimension(295, 31));
			jPanel15.add(jPanelStructQueryAttrib, BorderLayout.NORTH);
			jLabel5.setText("Structure Search:");
			jLabel5.setFont(new java.awt.Font("sansserif", 1, 13));
			jLabel5.setPreferredSize(new java.awt.Dimension(111, 18));
			jPanelStructQueryAttrib.add(jLabel5);
			jComboBoxSearchType.setPreferredSize(new java.awt.Dimension(87, 21));
			jComboBoxSearchType.setName("");
			jPanelStructQueryAttrib.add(jComboBoxSearchType);
			jComboBoxSearchType.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jComboBoxSearchTypeActionPerformed(evt);
				}
			});
			jTextFieldSimilarity.setText("100");
			jTextFieldSimilarity.setVisible(false);
			jTextFieldSimilarity.setPreferredSize(new java.awt.Dimension(27, 21));
			jPanelStructQueryAttrib.add(jTextFieldSimilarity);
			jLabelSimilarity.setText("% Similar");
			jLabelSimilarity.setVisible(false);
			jLabelSimilarity.setFont(new java.awt.Font("sansserif", 1, 13));
			jLabelSimilarity.setPreferredSize(new java.awt.Dimension(60, 18));
			jPanelStructQueryAttrib.add(jLabelSimilarity);
			BorderLayout jPanelStructQueryLayout = new BorderLayout();
			jPanelStructQuery.setLayout(jPanelStructQueryLayout);
			jPanelStructQueryLayout.setHgap(0);
			jPanelStructQueryLayout.setVgap(0);
			jPanelStructQuery.setBackground(new java.awt.Color(255, 255, 255));
			jPanelStructQuery.setPreferredSize(new java.awt.Dimension(295, 173));
			jPanelStructQuery.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			jPanel15.add(jPanelStructQuery, BorderLayout.CENTER);
			jPanel8.setPreferredSize(new java.awt.Dimension(715, 37));
			jPanelLookupQuery.add(jPanel8, BorderLayout.SOUTH);
			jPanel9.setVisible(true);
			jPanel9.setPreferredSize(new java.awt.Dimension(62, 10));
			jPanel8.add(jPanel9);
			jButtonSearch.setText("Search");
			jButtonSearch.setFont(new java.awt.Font("sansserif", 1, 11));
			jButtonSearch.setPreferredSize(new java.awt.Dimension(75, 27));
			jPanel8.add(jButtonSearch);
			jButtonSearch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonSearchActionPerformed(evt);
				}
			});
			jPanel10.setVisible(true);
			jPanel10.setPreferredSize(new java.awt.Dimension(62, 10));
			jPanel8.add(jPanel10);
			jButtonLookupBackToStoich.setText("Back to Stoich. Table");
			jButtonLookupBackToStoich.setFont(new java.awt.Font("sansserif", 1, 11));
			jPanel8.add(jButtonLookupBackToStoich);
			jButtonLookupBackToStoich.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonBackToStoichActionPerformed(evt);
				}
			});
			// add new Tab to Reagents Frame
			BorderLayout jPanelLookupResultsLayout = new BorderLayout();
			jPanelLookupResults.setLayout(jPanelLookupResultsLayout);
			jPanelLookupResultsLayout.setHgap(0);
			jPanelLookupResultsLayout.setVgap(0);
			jPanelLookupResults.setPreferredSize(new java.awt.Dimension(577, 315));
			jTabbedPaneMain.add(jPanelLookupResults);
			jTabbedPaneMain.setTitleAt(LOOKUP_RESULTS_TAB, "Lookup Results");
			jScrollPane3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jScrollPane3.setPreferredSize(new java.awt.Dimension(715, 129));
			jScrollPane3.setAutoscrolls(true);
			jPanelLookupResults.add(jScrollPane3, BorderLayout.NORTH);
			jScrollPane3.add(jTableReagentResults);
			jScrollPane3.setViewportView(jTableReagentResults);
			jTableReagentResults.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent evt) {
					if (!evt.getValueIsAdjusting())
						jTableReagentResultsRowChanged(evt);
				}
			});
			BorderLayout jPanel18Layout = new BorderLayout();
			jPanel18.setLayout(jPanel18Layout);
			jPanel18Layout.setHgap(0);
			jPanel18Layout.setVgap(0);
			jPanel18.setPreferredSize(new java.awt.Dimension(715, 167));
			jPanelLookupResults.add(jPanel18, BorderLayout.CENTER);
			BorderLayout jPanel21Layout = new BorderLayout();
			jPanel21.setLayout(jPanel21Layout);
			jPanel21Layout.setHgap(0);
			jPanel21Layout.setVgap(0);
			jPanel18.add(jPanel21, BorderLayout.WEST);
			jPanel21.add(jPanel22, BorderLayout.NORTH);
			jLabel8.setText("Reagent Structure");
			jLabel8.setHorizontalTextPosition(SwingConstants.TRAILING);
			jLabel8.setFont(new java.awt.Font("sansserif", 1, 13));
			jLabel8.setPreferredSize(new java.awt.Dimension(125, 18));
			jPanel22.add(jLabel8);
			jPanel23.setPreferredSize(new java.awt.Dimension(83, 10));
			jPanel22.add(jPanel23);
			chimeProResult.setPreferredSize(new java.awt.Dimension(262, 123));
			jPanel21.add(chimeProResult, BorderLayout.CENTER);
			BorderLayout jPanel24Layout = new BorderLayout();
			jPanel24.setLayout(jPanel24Layout);
			jPanel24Layout.setHgap(0);
			jPanel24Layout.setVgap(0);
			jPanel24.setPreferredSize(new java.awt.Dimension(370, 142));
			jPanel18.add(jPanel24, BorderLayout.EAST);
			jPanel24.add(jPanel25, BorderLayout.NORTH);
			jLabel9.setText("Reagent Property Values");
			jLabel9.setFont(new java.awt.Font("sansserif", 1, 13));
			jPanel25.add(jLabel9);
			BorderLayout jPanel26Layout = new BorderLayout();
			jPanel26.setLayout(jPanel26Layout);
			jPanel26Layout.setHgap(0);
			jPanel26Layout.setVgap(0);
			jPanel24.add(jPanel26, BorderLayout.CENTER);
			jPanel26.add(jScrollPane4, BorderLayout.CENTER);
			jScrollPane4.add(jTableReagentValues);
			jScrollPane4.setViewportView(jTableReagentValues);
			jPanel19.setPreferredSize(new java.awt.Dimension(715, 37));
			jPanelLookupResults.add(jPanel19, BorderLayout.SOUTH);
			jButtonAddToReagentList.setText("Add to My Reagent List");
			jButtonAddToReagentList.setFont(new java.awt.Font("sansserif", 1, 11));
			jButtonAddToReagentList.setPreferredSize(new java.awt.Dimension(160, 27));
			jPanel19.add(jButtonAddToReagentList);
			jButtonAddToReagentList.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonAddToMyReagentListActionPerformed(evt);
				}
			});
			jButtonAddToStoich.setText("Add To Stoich. Table");
			jButtonAddToStoich.setFont(new java.awt.Font("sansserif", 1, 11));
			jButtonAddToStoich.setPreferredSize(new java.awt.Dimension(161, 27));
			jPanel19.add(jButtonAddToStoich);
			jButtonAddToStoich.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonAddResultsToStoichActionPerformed(evt);
				}
			});
			jButtonResultBackToStoich.setText("Back To Stoich. Table");
			jButtonResultBackToStoich.setFont(new java.awt.Font("sansserif", 1, 11));
			jButtonResultBackToStoich.setPreferredSize(new java.awt.Dimension(164, 27));
			jPanel19.add(jButtonResultBackToStoich);
			jButtonResultBackToStoich.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonBackToStoichActionPerformed(evt);
				}
			});
			postInitGUI();
		} catch (Exception e) {
			ceh.logExceptionMsg(this, e);
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
		reagentResultsTableModel = new ReagentsResultTableModel();
		reagentResultsPropertyTableModel = new ReagentPropertyTableModel();
		myReagentsTableModel = new MyReagentsTableModel();
		myReagentsPropertyTableModel = new MyReagentsPropertyTableModel();
		myReagentsPropertyTableModel.setMyReagentsTableModel(myReagentsTableModel);
		// set up init map
		myReagentsPropertyTableModel.setMandatoryFields(reagentsHandler.getMandatoryFields());
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosed(InternalFrameEvent evt) {
				ReagentsFrame.this.setVisible(false);
			}
		});
		initTextSearchGUI();
		// reset jpanel2, 3, 4 layout
		resetMyReagentsPanelLayout();
		// reset search criteria panel layout
		resetSearchPanelLayout();
		// reset search result panel layout
		resetReagentsResultPanelLayout();
		try {
			structQueryCanvas = new ChemistryViewer(parent.getTitle(), "Structure in " + parent.getTitle());
			structQueryCanvas.setEditorType(MasterController.getGuiController().getDrawingTool());
			jPanelStructQuery.add(structQueryCanvas, BorderLayout.CENTER);
			// final CeNInternalFrame frame = this;
			structQueryCanvas.addChemistryEditorListener(new ChemistryEditorListener() {
				public void structureChanged(ChemistryEditorEvent arg0) {
					if (jComboBoxSearchType.getSelectedIndex() <= 0)
						jComboBoxSearchType.setSelectedItem(EQUALITY);
				}

				public void editingStarted(ChemistryEditorEvent arg0) {
				}

				public void editingStopped(ChemistryEditorEvent arg0) {
					ReagentsFrame.this.toFront();
				}
			});
		} catch (Exception e) {
			ceh.logExceptionMsg(this, e);
		}
		// set up table models
		jTableReagentResults.setModel(reagentResultsTableModel);
		jTableReagentValues.setModel(reagentResultsPropertyTableModel);
		myReagentsTableModel.setMyReagentsList(myReagents);
		jTableMyReagents.setModel(myReagentsTableModel);
		jTableMyReagentProps.setModel(myReagentsPropertyTableModel);
		jTableMyReagentProps.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (myReagentsPropertyTableModel != null && myReagentsPropertyTableModel.getSelectedRowOfMyReagent() != -1)
					jTableMyReagents.setRowSelectionInterval(myReagentsPropertyTableModel.getSelectedRowOfMyReagent(), myReagentsPropertyTableModel.getSelectedRowOfMyReagent());

//				if (evt.getOldValue() != null && evt.getNewValue() != null && !evt.getNewValue().equals(evt.getOldValue())) {
//					myReagentsPropChanging = true;
//					myReagentsPropChanging = false;
//				}
			}
		});
		// set cell renderer for tables
		setTableHeader(jTableReagentResults);
		setTableHeader(jTableReagentValues);
		setTableHeader(jTableMyReagents);
		setTableHeader(jTableMyReagentProps);
		jTextFieldSimilarity.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
		jTableMyReagents.clearSelection();
	}

	private void resetSearchPanelLayout() {
		double border = 4;
		double size[][] = { { border, TableLayout.PREFERRED, 4, TableLayout.FILL, border }, // Columns
				{ TableLayout.FILL } }; // Rows
		jPanelTextSearchCrit.setLayout(new TableLayout(size));
		// jPanelTextSearchCrit.add(jPanelTestSearchItem, "1,0");
		jPanelTextSearchCrit.add(this.jPanelTextSearchMain, "1,0");
		jPanelTextSearchCrit.add(jPanel15, "3,0");
	}

	private void resetMyReagentsPanelLayout() {
		double border = 4;
		double size[][] = { { border, 0.35, 0.04, TableLayout.FILL, border }, // Columns
				{ TableLayout.FILL } }; // Rows
		jPanel2.setLayout(new TableLayout(size));
		jPanel2.add(jPanel3, "1,0");
		jPanel2.add(jPanel4, "3,0");
	}

	private void resetReagentsResultPanelLayout() {
		double border = 4;
		double size[][] = { { border, 0.35, 0.04, TableLayout.FILL, border }, // Columns
				{ TableLayout.FILL } }; // Rows
		jPanel18.setLayout(new TableLayout(size));
		jPanel18.add(jPanel21, "1,0");
		jPanel18.add(jPanel24, "3,0");
	}

	/**
	 * 
	 */
	private void initTextSearchGUI() {
		List<Element> dbslist = null;
		try {
			// catalogCheckBoxList construct dbList
			dbslist = XPath.selectNodes(reagentsHandler.buildDBXMLRoot(), "/ReagentsDatabaseInfo/Databases/Database");
		} catch (JDOMException e) {
			ceh.logExceptionMsg(this, e);
		}
		jPanelRegCatalogs.setLayout(new GridLayout(Math.round(dbslist.size() / 4.0f), 4));
		Font f_arial_12 = new Font("Arial", 0, 12);
		for (int i = 0; i < dbslist.size(); i++) {
			Element dbElment = dbslist.get(i);
			String dbName = dbElment.getAttributeValue("Display_Name");
			JCheckBox catalogCheckBox = new JCheckBox();
			catalogCheckBox.setFont(f_arial_12);
			catalogCheckBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					catalogCheckBoxActionPerformed(evt);
				}
			});
			catalogCheckBox.setText(dbName);
			catalogCheckBoxList.add(catalogCheckBox);
			jPanelRegCatalogs.add(catalogCheckBox);
		}
		// structure search type
		jComboBoxSearchType.addItem(NONE_VALUE);
		jComboBoxSearchType.addItem(SUB_STRUCTURE);
		jComboBoxSearchType.addItem(SIMILARITY);
		jComboBoxSearchType.addItem(EQUALITY);
		// set up Text Search Criteria
		constructTextSearchCriteriaPanel();
	}

	// use TableLayout
	private void constructTextSearchCriteriaPanel() {
		try {
			// construct Database List
			List<Element> searchFiledList = XPath.selectNodes(reagentsHandler.buildDBXMLRoot(),
					"/ReagentsDatabaseInfo/Databases/Database/Tables/Table/Search_Fields/Field");
			Set<String> fieldMap = new HashSet<String>();
			double border = 4;
			double size[][] = { { border, TableLayout.PREFERRED, 1, TableLayout.PREFERRED, 1, TableLayout.FILL, border }, // Columns
					{ 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20 } }; // Rows
			jPanelTestSearchItem.setLayout(new TableLayout(size));
			int rowcounter = 0;
			Font f_arial_12 = new Font("Arial", 0, 12);
			for (int i = 0; i < searchFiledList.size(); i++) {
				Element fieldElment = (Element) searchFiledList.get(i);
				String fieldName = (String) fieldElment.getAttributeValue("Display_Name");
				// construct search criteria list for this filed
				if (fieldName.equals("Chemical Name") || fieldName.equals("Molecular Formula")
						|| fieldName.equals("Molecular Weight")) {
					if (!fieldMap.contains(fieldName)) {
						final JCheckBox jCheckBox1 = new JCheckBox();
						jCheckBox1.setFont(f_arial_12);
						jCheckBox1.setText(fieldName);
						jCheckBox1.setPreferredSize(new java.awt.Dimension(128, 21));
						jCheckBox1.setMinimumSize(new java.awt.Dimension(128, 21));
						jPanelTestSearchItem.add(jCheckBox1, "1, " + rowcounter);
						criteriaCheckBoxList.add(jCheckBox1);
						JComboBox jComboBox = new JComboBox();
						jComboBox.setPreferredSize(new java.awt.Dimension(138, 21));
						jComboBox.setMinimumSize(new java.awt.Dimension(138, 21));
						jPanelTestSearchItem.add(jComboBox, "3, " + rowcounter);
						List<Element> searchCriteriaList = XPath.selectNodes(fieldElment, "//Field[@Display_Name='" + fieldName
								+ "']/Search_Conditions/Search_Condition");
						Set<String> criteriaMap = new HashSet<String>();
						for (int j = 0; j < searchCriteriaList.size(); j++) {
							String criteriaName = searchCriteriaList.get(j).getAttributeValue("Display_Name");
							if (!criteriaMap.contains(criteriaName)) {
								jComboBox.addItem(criteriaName);
								criteriaMap.add(criteriaName);
							}
						}
						criteriaComboBoxList.add(jComboBox);
						JTextField jTextFieldtest = new JTextField();
						jTextFieldtest.setText("");
						jTextFieldtest.setPreferredSize(new java.awt.Dimension(113, 21));
						jPanelTestSearchItem.add(jTextFieldtest, "5, " + rowcounter);
						criteriaTextfieldList.add(jTextFieldtest);
						jTextFieldtest.addKeyListener(new KeyListener() {
							public void keyPressed(KeyEvent evt) {
							}

							public void keyReleased(KeyEvent evt) {
							}

							public void keyTyped(KeyEvent evt) {
								jCheckBox1.setSelected(true);
							}
						});
						fieldMap.add(fieldName);
						rowcounter++;
					}
				}
			}
		} catch (Exception e) {
			ceh.logExceptionMsg(this, e);
		}
	}

	/** Auto-generated event handler method */
	protected void catalogCheckBoxActionPerformed(ActionEvent evt) {
		JCheckBox catalogCheckBox = (JCheckBox) evt.getSource();
		String checkBoxName = catalogCheckBox.getText();
		boolean isSelected = catalogCheckBox.isSelected();
		if (isSelected) {
			selectedDBList.add(checkBoxName);
			addTextSearchCriteria(checkBoxName);
		} else {
			selectedDBList.remove(checkBoxName);
			removeTextSearchCriteria(checkBoxName);
		}
	}

	// use TableLayout
	private void addTextSearchCriteria(String checkBoxName) {
		try {
			// construct Database List
			List<Element> searchFiledList = XPath.selectNodes(reagentsHandler.buildDBXMLRoot(),
					"/ReagentsDatabaseInfo/Databases/Database[@Display_Name='" + checkBoxName
							+ "']/Tables/Table/Search_Fields/Field");
//			int newRowSize = searchFiledList.size() - 3 + criteriaCheckBoxList.size();
			int preRowsize = criteriaComboBoxList.size();
			// //System.out.println("The preRowsize is: " + preRowsize);
			Set<String> fieldMap = new HashSet<String>();
			int addRowcounter = 0;
			Font f_arial_12 = new Font("Arial", 0, 12);
			for (int i = 0; i < searchFiledList.size(); i++) {
				Element fieldElment = searchFiledList.get(i);
				String fieldName = fieldElment.getAttributeValue("Display_Name");
				// construct search criteria list for this filed
				if (!fieldName.equals("Chemical Name") && !fieldName.equals("Molecular Formula")
						&& !fieldName.equals("Molecular Weight")) {
					if (!fieldMap.contains(fieldName)) {
						final JCheckBox jCheckBox1 = new JCheckBox();
						jCheckBox1.setFont(f_arial_12);
						jCheckBox1.setText(fieldName);
						jCheckBox1.setPreferredSize(new java.awt.Dimension(128, 21));
						jCheckBox1.setMinimumSize(new java.awt.Dimension(128, 21));
						int currentRowSize = preRowsize + addRowcounter;
						// //System.out.println("The current row is: " +
						// currentRowSize);
						jPanelTestSearchItem.add(jCheckBox1, "1," + currentRowSize);
						criteriaCheckBoxList.add(jCheckBox1);
						JComboBox jComboBox = new JComboBox();
						jComboBox.setPreferredSize(new java.awt.Dimension(138, 21));
						jComboBox.setMinimumSize(new java.awt.Dimension(138, 21));
						jPanelTestSearchItem.add(jComboBox, "3," + currentRowSize);
						List<Element> searchCriteriaList = XPath.selectNodes(fieldElment, "//Field[@Display_Name='" + fieldName
								+ "']/Search_Conditions/Search_Condition");
						Set<String> criteriaMap = new HashSet<String>();
						for (int j = 0; j < searchCriteriaList.size(); j++) {
							String criteriaName = searchCriteriaList.get(j).getAttributeValue("Display_Name");
							if (!criteriaMap.contains(criteriaName)) {
								jComboBox.addItem(criteriaName);
								criteriaMap.add(criteriaName);
							}
						}
						criteriaComboBoxList.add(jComboBox);
						JTextField jTextFieldtest = new JTextField();
						jTextFieldtest.setText("");
						jTextFieldtest.setPreferredSize(new java.awt.Dimension(113, 21));
						jPanelTestSearchItem.add(jTextFieldtest, "5," + currentRowSize);
						criteriaTextfieldList.add(jTextFieldtest);
						jTextFieldtest.addKeyListener(new KeyListener() {
							public void keyPressed(KeyEvent evt) {
							}

							public void keyReleased(KeyEvent evt) {
							}

							public void keyTyped(KeyEvent evt) {
								jCheckBox1.setSelected(true);
							}
						});
						fieldMap.add(fieldName);
						addRowcounter++;
					}
				}
			}
			jPanelTestSearchItem.repaint();
		} catch (Exception e) {
			ceh.logExceptionMsg(this, e);
		}
	}

	private void removeTextSearchCriteria(String checkBoxName) {
		try {
			// construct Database List
			List<Element> searchFiledList = XPath.selectNodes(reagentsHandler.buildDBXMLRoot(),
					"/ReagentsDatabaseInfo/Databases/Database[@Display_Name='" + checkBoxName
							+ "']/Tables/Table/Search_Fields/Field");
			// reset layout
			// jPanelTestSearchItem = new JPanel();
			jPanelTestSearchItem.removeAll();
			jPanelTestSearchItem.repaint();
			double border = 4;
			double size[][] = { { border, TableLayout.PREFERRED, 1, TableLayout.PREFERRED, 1, TableLayout.FILL, border }, // Columns
					{ 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20 } }; // Rows
			jPanelTestSearchItem.setLayout(new TableLayout(size));
			Font f_arial_12 = new Font("Arial", 0, 12);
			for (int i = 0; i < searchFiledList.size(); i++) {
				Element fieldElment = searchFiledList.get(i);
				String fieldName = fieldElment.getAttributeValue("Display_Name");
				// construct search criteria list for this filed
				if (!fieldName.equals("Chemical Name") && !fieldName.equals("Molecular Formula")
						&& !fieldName.equals("Molecular Weight")) {
					for (int k = 0; k < criteriaCheckBoxList.size(); k++) {
						JCheckBox jCheckBox = (JCheckBox) criteriaCheckBoxList.get(k);
						jCheckBox.setFont(f_arial_12);
						if (jCheckBox.getText().equals(fieldName)) {
							// reset lists
							criteriaCheckBoxList.remove(k);
							criteriaComboBoxList.remove(k);
							criteriaTextfieldList.remove(k);
							break;
						}
					}
				}
			}
			for (int m = 0; m < criteriaCheckBoxList.size(); m++) {
				jPanelTestSearchItem.add((JCheckBox) criteriaCheckBoxList.get(m), "1," + m);
				jPanelTestSearchItem.add((JComboBox) criteriaComboBoxList.get(m), "3," + m);
				jPanelTestSearchItem.add((JTextField) criteriaTextfieldList.get(m), "5," + m);
			}
			jPanelTestSearchItem.repaint();
			repaint();
		} catch (Exception e) {
			ceh.logExceptionMsg(this, e);
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonSearchActionPerformed(ActionEvent evt) {
		TextSerachParamsList.clear();
		clearDetail();
		String textValue = null;
		int itemListSize = criteriaCheckBoxList.size();
		for (int i = 0; i < itemListSize; i++) {
			JCheckBox jcheckBox = (JCheckBox) criteriaCheckBoxList.get(i);
			if (jcheckBox.isSelected()) {
				TextSearchParamsVO textSearchParamsVO = new TextSearchParamsVO();
				textSearchParamsVO.setColDisplayName(jcheckBox.getText());
				JComboBox jComboBox = (JComboBox) criteriaComboBoxList.get(i);
				JTextField jTextField = (JTextField) criteriaTextfieldList.get(i);
				textValue = jTextField.getText().trim();
				if (textValue.length() == 0) {
					JOptionPane.showMessageDialog(this, " Search criterion not specified for \"" + jcheckBox.getText()
							+ "\", \n Please enter a value and try again.", "Blank Search", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				textSearchParamsVO.setSearchCriteria(jComboBox.getSelectedItem().toString());
				textSearchParamsVO.setSearchValue(jTextField.getText());
				TextSerachParamsList.add(textSearchParamsVO);
			}
		}
		boolean validStruct = false;
		if (!((String) jComboBoxSearchType.getSelectedItem()).equals(NONE_VALUE)) {
			if (structQueryCanvas.getChemistry().length > 0) {
				try {
					validStruct = !(new ChemistryDelegate()).isChemistryEmpty(structQueryCanvas.getChemistry());
				} catch (Exception e) {
					ceh.logExceptionMsg(ReagentsFrame.this, e);
				}
			}
		}
		// need to validate the user input for search parameters
		if (selectedDBList.size() <= 0) {
			// no databases selected to search against
			JOptionPane.showMessageDialog(this, "Please select at least one Reagent Catalog to search.");
		} else if (TextSerachParamsList.size() == 0 && ((String) jComboBoxSearchType.getSelectedItem()).equals(NONE_VALUE)) {
			// no search options or parameters specified
			JOptionPane.showMessageDialog(this, "Please specify at least one search option.");
		} else if (!((String) jComboBoxSearchType.getSelectedItem()).equals(NONE_VALUE) && !validStruct) {
			JOptionPane.showMessageDialog(this, "Please specify the structure to search.");
		} else {
			jButtonSearch.setEnabled(false);
			boolean paramStatus = buildSearchParams();
			if (!paramStatus) {
				jButtonSearch.setEnabled(true);
				return;
			}
			// //System.out.println("The search params xml is: " +
			// searchParamsXML);
			ActionListener cancelListener = new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					reagentsHandler.cancelReagentSearch();
					((JButton) event.getSource()).setEnabled(false);
					progressBarDialog.setTitle("Canceling Search, please wait....");
				}
			};
			progressBarDialog = new ProgressBarDialog(parent, cancelListener);
			progressBarDialog.setTitle("Searching, please wait....");
			doneDisplay = false;
			doSearching();
			progressBarDialog.setVisible(true);
		}
	}

	/**
	 * 
	 */
	private void clearDetail() {
		reagentResultsPropertyTableModel.clearModel();
		chimeProResult.setMolfileData("");
		chimeProResult.repaint();
	}

	public void doSearching() {
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				Object objLatch = new Object();
				try {
					byte[] bytes = reagentsHandler.doReagentSearch(searchParamsXML);
					if (bytes != null) {
						reagentsHandler.updateIteratingInfo();
						if (reagentsHandler.getTotal() <= 0) {
							javax.swing.SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									progressBarDialog.setVisible(false);
									progressBarDialog.dispose();
									progressBarDialog = null;
									jButtonSearch.setEnabled(true);
									String errorMessage = null;
									// System.out.println("reagentsHandler.getTotal--:
									// " +
									// reagentsHandler.getTotal());
									if (reagentsHandler.getTotal() <= 0 && reagentsHandler.isSearchCancelled()) {
										errorMessage = "No data, Search Cancelled.";
									} else if (reagentsHandler.getTotal() == 0) {
										errorMessage = "No data found, Please refine your search criteria.";
									} else {
										errorMessage = "Total record count is over 5000. Search criteria too broad, Please refine your search criteria.";
									}
									JOptionPane.showMessageDialog(ReagentsFrame.this, errorMessage);
								}
							});
						} else {
							final String reagentsList = reagentsHandler.getReagentsList();
							javax.swing.SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									progressBarDialog.setVisible(false);
									progressBarDialog.dispose();
									progressBarDialog = null;
									reagentResultsTableModel.setReagentsList(reagentsList);
									reagentResultsTableModel.resetModelData(true);
									reagentsHandler.updateIteratingInfo();
									jButtonSearch.setEnabled(true);
									
									setSelectedTab(LOOKUP_RESULTS_TAB); // bug artf55313 : lookup reagents search displays wrong tab fixed 11/03
									
									if (reagentsHandler.getHasMore() && !reagentsHandler.isSearchCancelled()) {
										int total = reagentsHandler.getTotal();
										//TODO
										int threadCount = THREAD_COUNT; //calculateIteratingTrips(total, reagentsHandler.getChunkSize());
										int lastPosition = reagentsHandler.getLastPosition() + 1;
										for (int j = 0; j < threadCount; j++) {
											updatesearchParams(lastPosition + reagentsHandler.getChunkSize(total, threadCount) * j, reagentsHandler.getChunkSize(total, threadCount));
											if (reagentsHandler.isSearchCancelled())
												break;
											doIteratingSearching();
										}
									} else if (reagentsHandler.isSearchCancelled()) {
										setSelectedTab(SEARCH_REAGENTS_TAB);
										JOptionPane.showMessageDialog(ReagentsFrame.this, "No Data,Search Cancelled.");
									} 
								}
							});
						}
					} else {
						javax.swing.SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								progressBarDialog.setVisible(false);
								progressBarDialog.dispose();
								progressBarDialog = null;
								jButtonSearch.setEnabled(true);
							}
						});
					}
				} catch (Exception e) {
					progressBarDialog.setVisible(false);
					progressBarDialog.dispose();
					progressBarDialog = null;
					ceh.logExceptionMsg(ReagentsFrame.this, e);
				} finally {
					synchronized (objLatch) {
						objLatch.notify();
					}
				}
				return null;
			}
		};
		worker.start();
	}

	public int calculateIteratingTrips(int total, int chunkSize) {
		return Math.round((total * 1.0f) / (chunkSize * 1.0f)) - 1;
	}

	public void doIteratingSearching() {
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				try {
					byte[] bytes = reagentsHandler.doReagentSearch(searchParamsXML);
					if (bytes != null) {
						reagentResultsTableModel.setReagentsList(reagentsHandler.getReagentsList());
						reagentResultsTableModel.resetModelData(false);
						reagentsHandler.updateIteratingInfo();
						javax.swing.SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								if (!doneDisplay) {
									if(jTableReagentResults.getRowCount() > 0) {
										jTableReagentResults.setRowSelectionInterval(0, 0);
										ReagentsFrame.this.displaySelectedReagent(0);
									} else {
										log.debug("No Results were found to select at end of Search.");
									}
									doneDisplay = true;
								}
							}
						});
						Thread.sleep(5000);
					}
					// //System.out.println("The hasMore is: " +
					// reagentsHandler.getHasMore());
				} catch (Exception e) {
					ceh.logExceptionMsg(ReagentsFrame.this, e);
				}
				return null;
			}
		};
		worker.start();
	}

	public void updatesearchParams(int lastPosition, int chunkSize) {
		try {
			StringReader reader = new StringReader(searchParamsXML);
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(reader);
			Element root = doc.getRootElement();
			Element interatingElement = (Element) XPath.selectSingleNode(root, "/ReagentsLookupParams/Iterating");
			interatingElement.getAttribute("LastPosition").setValue((new Integer(lastPosition)).toString());
			interatingElement.getAttribute("ChunkSize").setValue((new Integer(chunkSize)).toString());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			XMLOutputter outputter = new XMLOutputter();
			outputter.output(doc, out);
			out.flush();
			searchParamsXML = out.toString();
		} catch (Exception e) {
			ceh.logExceptionMsg(this, e);
		}
	}

	public boolean buildSearchParams() {
		StringBuffer sb = new StringBuffer();
		sb.append("<ReagentsLookupParams>");
		if (TextSerachParamsList.size() != 0) {
			boolean buildStatus = buildTextSearchParams(sb);
			if (!buildStatus) {
				sb.append("</ReagentsLookupParams>");
				searchParamsXML = sb.toString();
				return false;
			}
		} else {
			sb.append("<TextDatabases DoTextSearch=\"False\"/>");
		}
		if (!((String) jComboBoxSearchType.getSelectedItem()).equals(NONE_VALUE)) {
			buildStructureSearchParams(sb);
		} else {
			sb.append("<StructureDatabases DoStructureSearch=\"False\"/>");
		}
		sb.append("<Iterating LastPosition=\"-1\" ChunkSize=\"" + reagentsHandler.getInitialChunkSize() + "\"/>");
		sb.append("</ReagentsLookupParams>");
		searchParamsXML = sb.toString();
		return true;
	}

	public void buildStructureSearchParams(StringBuffer sb) {
		// need to convert structure data format using local interface to
		// the chemistry service. there is a method to convertformats
		// pass in null for the from format it will figure it out itself
		// and then pass in "MDL Molfile" for the to format.
		try {
			sb.append("<StructureDatabases DoStructureSearch=\"True\">");
			sb.append("<DatabaseTypes>");
			sb.append("<DatabaseType Name=\"2D Collections\">");
			sb.append("<DatabaseList>");
			// construct Database List
			List<Element> relationList = XPath.selectNodes(reagentsHandler.buildDBXMLRoot(),
					"/ReagentsDatabaseInfo/Text_Structure_Relationships/Text_Structure_Relationship");
			for (int j = 0; j < relationList.size(); j++) {
				Element relationElement = relationList.get(j);
				String textdbName = relationElement.getAttributeValue("Text");
				String structuredbName = relationElement.getAttributeValue("Structure");
				for (int k = 0; k < selectedDBList.size(); k++) {
					if (StringUtils.equals(totalDBMap.get(selectedDBList.get(k)), textdbName)) {
						// construct db node
						sb.append("<Database>" + structuredbName + "</Database>");
					}
				}
			}
			sb.append("</DatabaseList>");
			sb.append("<SearchType>" + searchTypeMap.get(jComboBoxSearchType.getSelectedItem()) + "</SearchType>");
			sb.append("<MolDefn FileType=\"MDL Molfile\">");
			ChemistryDelegate chemDel = new ChemistryDelegate();
			byte[] molFile = chemDel.convertChemistry(structQueryCanvas.getChemistry(), "", "MDL Molfile");
			sb.append("<![CDATA[" + new String(molFile) + "]]>");
			sb.append("</MolDefn>");
			if (((String) jComboBoxSearchType.getSelectedItem()).equals(SIMILARITY)) {
				sb.append("<SearchOptionValue>" + jTextFieldSimilarity.getText());
				sb.append("</SearchOptionValue>");
			} else {
				sb.append("<SearchOptionValue/>");
			}
			sb.append("<FieldList>");
			sb.append("<Field>compoundID</Field>");
			sb.append("</FieldList>");
			sb.append("<ReturnAsXML>False</ReturnAsXML>");
			sb.append("</DatabaseType>");
			sb.append("</DatabaseTypes>");
			sb.append("</StructureDatabases>");
		} catch (Exception e) {
			ceh.logExceptionMsg(this, e);
		}
	}

	public boolean buildTextSearchParams(StringBuffer sb) {
		if (validateSearchDBList()) {
			sb.append("<TextDatabases DoTextSearch=\"True\">");
			try {
				// construct Database List
				List<Element> totalDBList = XPath.selectNodes(reagentsHandler.buildDBXMLRoot(), "/ReagentsDatabaseInfo/Databases/Database");
				for (int j = 0; j < totalDBList.size(); j++) {
					Element dbElement = totalDBList.get(j);
					String dbDisplayName = dbElement.getAttributeValue("Display_Name");
					String dbName = dbElement.getAttributeValue("Name");
					for (int k = 0; k < selectedDBList.size(); k++) {
						if (StringUtils.equals(selectedDBList.get(k), dbDisplayName)) {
							// construct db node
							sb.append("<Database Name=\"" + dbName + "\">");
							sb.append("<SearchFields>");
							List<Element> searchFieldList = XPath.selectNodes(dbElement,
							                                                  "//Database[@Name='" + dbName + "']/Tables/Table/Search_Fields/Field");
							for (int m = 0; m < searchFieldList.size(); m++) {
								Element fieldElement = searchFieldList.get(m);
								String fieldDisplayName = fieldElement.getAttributeValue("Display_Name");
								String colName = fieldElement.getAttributeValue("Column_Name");
								String useUpperCase = fieldElement.getAttributeValue("Use_Upper");
								for (int n = 0; n < TextSerachParamsList.size(); n++) {
									TextSearchParamsVO textSerachParamsVO = (TextSearchParamsVO) TextSerachParamsList.get(n);
									if (fieldDisplayName.equals(textSerachParamsVO.getColDisplayName())) {
										// Strip out spaces from Mol Formula for CompoundManagement search
										// if(fieldDisplayName.equals("Molecular Formula") && dbName.equals("CompoundManagement")) {
										//    String strTemp = textSerachParamsVO.getSearchValue().replaceAll(" ", "");
										//    textSerachParamsVO.setSearchValue(strTemp);
										// }
										// Format compound numbers for equals operator
										if (textSerachParamsVO.getSearchCriteria().equals("Equals")) {
											if (fieldDisplayName.equals(REGISTERED_COMPOUND_HEADING)
													|| fieldDisplayName.equals(ACD_HEADING)) {
//												try {
//													textSerachParamsVO.setSearchValue(CNFHelper.formatCompoundNumber(textSerachParamsVO
//															.getSearchValue().trim()));
//												} catch (CNFError cnfe) { /* Ignored */
//												}
												textSerachParamsVO.setSearchValue(UtilsDispatcher.getFormatter().formatCompoundNumber(textSerachParamsVO
														.getSearchValue().trim()));
											}
										}
										String value = textSerachParamsVO.getSearchValue().trim();
										if (useUpperCase == null)
											useUpperCase = "";
										useUpperCase = useUpperCase.toLowerCase();
										if (useUpperCase.equals("inline")) {
											value = value.toUpperCase();
											useUpperCase = "false";
										}
										String upCaseAttrib = (useUpperCase.equals("true")) ? " UseUpper=\"true\"" : "";
										// if(fieldDisplayName.equals("Molecular Formula")) value = value.replaceAll(" ", "%");
										// Escape the < and > symbols
										value = value.replaceAll(">", "&gt;").replaceAll("<", "&lt;");
										sb.append("<Field ColumnName=\"" + colName + "\"" + " Criteria=\""
												+ textSerachParamsVO.getSearchCriteria() + "\"" + " Value=\"" + value + "\""
												+ upCaseAttrib + "/>");
										break;
									}
								}
							}
							sb.append("</SearchFields>");
							sb.append("</Database>");
							break;
						}
					}
				}
				sb.append("</TextDatabases>");
			} catch (JDOMException e) {
				ceh.logExceptionMsg(this, e);
			} catch (LoadServiceException e) {
				ceh.logExceptionMsg(this, e);
			}
		} else {
			return false;
		}
		return true;
	}

	private boolean validateSearchDBList() {
		try {
			// construct Database List
			List<Element> totalDBList = XPath.selectNodes(reagentsHandler.buildDBXMLRoot(), "/ReagentsDatabaseInfo/Databases/Database");
			for (int j = 0; j < totalDBList.size(); j++) {
				Element dbElement = totalDBList.get(j);
				String dbDisplayName = dbElement.getAttributeValue("Display_Name");
				String dbName = dbElement.getAttributeValue("Name");
				for (String selectedDB : selectedDBList) {
					if (StringUtils.equals(selectedDB, dbDisplayName)) {
						boolean isValid = false;
						List<Element> searchFieldList = XPath.selectNodes(dbElement, 
						                                                  "//Database[@Name='" + dbName + "']/Tables/Table/Search_Fields/Field");
						for (int m = 0; isValid == false && m < searchFieldList.size(); m++) {
							Element fieldElement = searchFieldList.get(m);
							String fieldDisplayName = fieldElement.getAttributeValue("Display_Name");
							for (int n = 0; isValid == false && n < TextSerachParamsList.size(); n++) {
								TextSearchParamsVO textSerachParamsVO = TextSerachParamsList.get(n);
								isValid = StringUtils.equals(fieldDisplayName.trim(), textSerachParamsVO.getColDisplayName().trim());
							}
						}
						// System.out.println("The isValid value is: " + isValid + "for db: " + selectedDBList.get(k));
						if (!isValid) {
							int result = JOptionPane.showOptionDialog(this,
											"Selected parameter(s) cannot be searched in "
													+ selectedDB
													+ " Database. \n"
													+ "Choose :\n YES for continuing the search with selected databases \n NO to avoid searching in "
													+ selectedDB + " \n CANCEL to stop the search ", "Reagent Search",
											JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
							if (result == JOptionPane.YES_OPTION) {
								for (int n = 0; n < catalogCheckBoxList.size(); n++) {
									if (StringUtils.equals(selectedDB, catalogCheckBoxList.get(n).getText())) {
										JCheckBox dbCheckBox = catalogCheckBoxList.get(n);
										dbCheckBox.setSelected(false);
										removeTextSearchCriteria(dbCheckBox.getText());
									}
								}
								selectedDBList.remove(selectedDB);
							} else if (result == JOptionPane.CANCEL_OPTION) {
								return false;
							}
						}
						break;  // Why is this here?
					}
				}
			}
		} catch (JDOMException e) {
			ceh.logExceptionMsg(this, e);
		}
		return true;
	}

	/** Auto-generated event handler method */
	protected void jTableReagentResultsRowChanged(ListSelectionEvent evt) {
		ListSelectionModel lsm = (ListSelectionModel) evt.getSource();
		displaySelectedReagent(lsm.getMinSelectionIndex());
	}

	/**
	 * @param selectedRow
	 */
	private void displaySelectedReagent(int selectedRow) {
		chimeProResult.setMolfileData("");
		chimeProResult.repaint();
		if (selectedRow != -1) {
			Element selectedReagent = reagentResultsTableModel.getSelectedReagentInfo(selectedRow);
			reagentResultsPropertyTableModel.setFieldsElement(selectedReagent);
			
			String compoundStructure = "";
			
			try {
				List<Element> fieldList = XPath.selectNodes(selectedReagent, "child::Field");
				
				for (Element fieldElement : fieldList) {
					String displayName = fieldElement.getAttributeValue("Display_Name");
					
					if (displayName.equals("Structure"))
						compoundStructure = fieldElement.getText();
				}
			} catch (JDOMException e) {
			}
			
			chimeProResult.setMolfileData(compoundStructure);
			chimeProResult.repaint();
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonAddToMyReagentListActionPerformed(ActionEvent evt) {
		// check how many rows are selected:
		// if only one is selected, do the existence check and pop up the
		// warning message
		// if multiple rows are selected, add not duplicated rows silently
		int[] selectedRows = jTableReagentResults.getSelectedRows();
		if (selectedRows.length == 1) {
			String name = reagentResultsTableModel.getSelectedReagentName(selectedRows[0]);
			if (myReagentsTableModel.checkIfReagentExist(name))
				JOptionPane.showMessageDialog(this, "This reagent already exists in your reagents list.");
			else {
				Element selectedReagent = reagentResultsTableModel.getSelectedReagentInfo(selectedRows[0]);
				myReagentsTableModel.addModelData(selectedReagent);
			}
		} else {
			for (int i = selectedRows.length - 1; i >= 0; i--) {
				String name = reagentResultsTableModel.getSelectedReagentName(selectedRows[i]);
				if (!myReagentsTableModel.checkIfReagentExist(name)) {
					Element selectedReagent = reagentResultsTableModel.getSelectedReagentInfo(selectedRows[i]);
					myReagentsTableModel.addModelData(selectedReagent);
				}
			}
			// save my reagents list to database
			if (!reagentsHandler.isTimedOutExceptionOccured()) {
				saveMyReagents();
			} else {
				JOptionPane.showMessageDialog(this, "Failed to store the updated the MyReagent list.");
			}
		}
	}

	public void saveMyReagents() {
		if(myReagentsTableModel != null) {
			myReagents = myReagentsTableModel.buildMyReagents();
			reagentsHandler.updateMyReagentList(myReagents);
		}
	}

	/** Auto-generated event handler method */
	protected void jTableMyReagentsMouseClicked(MouseEvent e) {
		if (e.isPopupTrigger()) {
			showMyReagentsPopupMenu(e);
		}
	}

	protected void jTableMyReagentsRowChanged(ListSelectionEvent evt) {
		ListSelectionModel lsm = (ListSelectionModel) evt.getSource();
		int selectedIndex = lsm.getMinSelectionIndex();
		// remeber selected reagent
		if (selectedIndex != -1) {
			jTableMyReagentProps.stopEditing();
			setSelectedRowOfMyreagents(selectedIndex);
			// show the properties and structure of selected reagent
			displaySelectedReagentDetail(selectedIndex);
		}
		// if (selectedIndex != -1) displaySelectedReagentDetail(selectedIndex);
	}

	public void setSelectedRowOfMyreagents(int row) {
		myReagentsPropertyTableModel.setSelectedRowOfMyReagent(row);
	}

	/** Auto-generated event handler method */
	protected void deleteItemActionPerformed(ActionEvent evt) {
		JOptionPane deleteConfirmPane = new JOptionPane("Are you sure you want to delete this reagent from your reagents list?");
		Object[] options = { "Yes", "No" };
		deleteConfirmPane.setOptions(options);
		JDialog deleteConfirmPanedialog = deleteConfirmPane.createDialog(this, "Delete");
		deleteConfirmPanedialog.setVisible(true);
		Object selectedValue = deleteConfirmPane.getValue();
		if (((String) selectedValue).equals("Yes")) {
			myReagentsPropertyTableModel.clearModelData();
			deleteInProgress = true;
			// delete from myReagentTable
			myReagentsTableModel.deleteSelectedReagent(selectedReagentIndex);
			selectedReagentIndex = -1;
			// delete from database
			saveMyReagents();
			// remove structure
			chimeProMyReagent.setMolfileData("");
			chimeProMyReagent.repaint();
			deleteInProgress = false;
		}
	}

	/**
	 * @param MouseEvent
	 *            evt
	 */
	private void showMyReagentsPopupMenu(MouseEvent mevt) {
		JTable myReagentTable = (JTable) mevt.getSource();
		final Point p = mevt.getPoint();
		selectedReagentIndex = myReagentTable.rowAtPoint(mevt.getPoint());
		if (selectedReagentIndex >= 0 && selectedReagentIndex < myReagentTable.getRowCount()) {
			myReagentTable.setRowSelectionInterval(selectedReagentIndex, selectedReagentIndex);
			// construct the PopUpMenu
			popUpMenu = new JPopupMenu();
			// popUpMenu.addMouseListener(new MouseAdapter() {
			// public void mouseExited(MouseEvent e) {
			// Point point = e.getPoint();
			// popUpMenu.setVisible(point.x >= ( p.x + 1 +
			// popUpMenu.getPreferredSize().width)?true:false);
			// }
			// });
			JMenuItem deleteItem = new JMenuItem("Delete Reagent");
			deleteItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent aevt) {
					deleteItemActionPerformed(aevt);
				}
			});
			popUpMenu.add(deleteItem);
			JMenuItem addReagentItem = new JMenuItem("Add Reagent");
			addReagentItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent aevt) {
					/** TODO add Add Reagent Handler code here */
				}
			});
			popUpMenu.add(addReagentItem);
			popUpMenu.add(deleteItem);
			JMenuItem duplicateReagentItem = new JMenuItem("Duplicate Reagent");
			addReagentItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent aevt) {
					/** TODO add Duplicate Reagent Handler code here */
				}
			});
			popUpMenu.add(duplicateReagentItem);
			JMenuItem addReagentsFromItem = new JMenuItem("Add Reagents from...");
			addReagentsFromItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent aevt) {
					addReagentsFromActionPerformed(p.x + 1, p.y - 6);
				}
			});
			popUpMenu.add(addReagentsFromItem);
			popUpMenu.show(myReagentTable, p.x + 1, p.y - 6);
		} else
			selectedReagentIndex = -1;
	}

	/**
	 * @param MouseEvent
	 *            evt
	 */
	protected void updateReagentDisplay(MouseEvent evt) {
		JTable myReagentTable = (JTable) evt.getSource();
		displaySelectedReagentDetail(myReagentTable.getSelectedRow());
	}

	/**
	 * @param selectedRow
	 */
	private void displaySelectedReagentDetail(int selectedRow) {
		Element selectedReagent = myReagentsTableModel.getSelectedReagentInfo(selectedRow);
		myReagentsPropertyTableModel.setFieldsElement(selectedReagent);
		
		// now to do the structure search use chimeProResult to display
		new SwingWorker() {
			public Object construct() {
				Stopwatch stopwatch = new Stopwatch();
				stopwatch.start("ReagentsFrame.displaySelectedReagentDetail() structure search");
				
				String compoundNum = reagentsHandler.getCompoundNo(myReagentsPropertyTableModel.getResultNameList(),
                            myReagentsPropertyTableModel.getResultValueList());

				String compoundStructure = "";
				if (reagentsHandler.getSdfMap().containsKey(compoundNum)) {
					compoundStructure = reagentsHandler.getSdfMap().get(compoundNum);
				} else {
					//clean panel before invocation operation which can take a while 
					chimeProMyReagent.setMolfileData("");
					chimeProMyReagent.repaint();
					
					compoundStructure = reagentsHandler.getStructureByCompoundNo(compoundNum);
					reagentsHandler.getSdfMap().put(compoundNum, compoundStructure);
				}
				chimeProMyReagent.setMolfileData(compoundStructure);
				chimeProMyReagent.repaint();
				stopwatch.stop();
				return null;
			}
			public void finished() {
				System.out.println("Swing worker finished");
			}
		}.start();
	}

	/** Auto-generated event handler method */
	protected void ReagentsFrameWindowClosing() {
		// save the changes for properties and kill the EJB romote reference
		if (!reagentsHandler.isTimedOutExceptionOccured()) {
			saveMyReagents();
			reagentsHandler.removeReagentMgmtServiceEJB();
		} else
			JOptionPane.showMessageDialog(this, "Failed to store the updated the MyReagent list.");
		myReagentsTableModel = null;
		myReagentsPropertyTableModel = null;
		reagentResultsTableModel = null;
		reagentResultsPropertyTableModel = null;
		jTableMyReagentProps.setModel(new DefaultTableModel());
		jTableReagentResults.setModel(new DefaultTableModel());
		jTableMyReagents.setModel(new DefaultTableModel());
		jTableReagentValues.setModel(new DefaultTableModel());
	}

	// /** Auto-generated event handler method */
	// protected void ReagentsDialogWindowClosing(WindowEvent evt)
	// {
	// //save the changes for properties and kill the EJB romote reference
	// if (!reagentsHandler.isTimedOutExceptionOccured) {
	// saveMyReagents();
	// reagentsHandler.removeReagentMgmtServiceEJB();
	// } else
	// JOptionPane.showMessageDialog(this, "Failed to store the updated the
	// MyReagent list.");
	// }
	public void setTableHeader(JTable table) {
		JTableHeader header = table.getTableHeader();
		final Font boldFont = header.getFont().deriveFont(Font.BOLD);
		final TableCellRenderer headerRenderer = header.getDefaultRenderer();
		header.setDefaultRenderer(new TableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				Component comp = headerRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				comp.setFont(boldFont);
				return comp;
			}
		});
	}

	/** Auto-generated event handler method */
	protected void jButtonBackToStoichActionPerformed(ActionEvent evt) {
		// save the changes for properties and kill the EJB romote reference
		// then close the ReagentsDialog
		backToStoich();
	}

	protected void backToStoich() {
		if (shouldClose()) {
			dispose();
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonAddResultsToStoichActionPerformed(ActionEvent evt) {
		ArrayList<MonomerBatchModel> reagentBatchList = new ArrayList<MonomerBatchModel>();
		int[] selectedRows = jTableReagentResults.getSelectedRows();
		try {
			for (int i = 0; i < selectedRows.length; i++) {
				Element selectedReagent = ((ReagentsResultTableModel) jTableReagentResults.getModel()).getSelectedReagentInfo(selectedRows[i]);
				reagentBatchList.add(reagentsHandler.buildReagentBatchList(selectedReagent));
			}
		} catch (Exception e) {
			log.error("Failed to add reagents from search results to stoichiometry table.  Count = " + reagentBatchList.size(), e);
			JOptionPane.showMessageDialog(null, "Could not add reagents to experiment.", "Could not add reagents to experiment.",
					JOptionPane.INFORMATION_MESSAGE);
		}
		notifyReagentAdditionListeners(reagentBatchList);
		toBack();
	}

	/** Auto-generated event handler method */
	protected void jButtonAddMyReagentsToStoichActionPerformed(ActionEvent evt) {
		ArrayList<MonomerBatchModel> reagentBatchList = new ArrayList<MonomerBatchModel>();
		int[] selectedRows = jTableMyReagents.getSelectedRows();
		try {
			for (int i = 0; i < selectedRows.length; i++) {
				Element selectedReagent = ((MyReagentsTableModel) jTableMyReagents.getModel()).getSelectedReagentInfo(selectedRows[i]);
				reagentBatchList.add(reagentsHandler.buildReagentBatchList(selectedReagent));
			}
		} catch (Exception e) {
			log.error("Failed to add my reagents to stoichiometry table.  Count = " + reagentBatchList.size(), e);
			JOptionPane.showMessageDialog(null, "Could not add reagent(s) to experiment.", "Could not add reagents to experiment.",
					JOptionPane.INFORMATION_MESSAGE);
		}
		if(isSolventsFrame == false) {
			notifyReagentAdditionListeners(reagentBatchList);
		} else {
			notifyReagentAdditionListenersAboutSolvent(reagentBatchList);
		}
		// show other gui
		toBack();
	}

	public ArrayList<MonomerBatchModel> getReagentBatchList() {
		return reagentBatchList;
	}

	public void setReagentBatchList(ArrayList<MonomerBatchModel> batchList) {
		reagentBatchList = batchList;
	}

	public void addReagentAdditionActionListener(ReagentAdditionListener newListener) {
		if (reagentAdditionListeners.contains(newListener) == false) {
			reagentAdditionListeners.add(newListener);
		}
	}

	public void removeReagentAdditionActionListener(ReagentAdditionListener target) {
		reagentAdditionListeners.remove(target);
	}

	public void notifyReagentAdditionListeners(ArrayList<MonomerBatchModel> reagentsToAdd) {
		for (int i = 0; i < reagentAdditionListeners.size(); i++) {
			reagentAdditionListeners.get(i).addReagentsFromList(reagentsToAdd);
		}
	}
	
	public void notifyReagentAdditionListenersAboutSolvent(ArrayList<MonomerBatchModel> reagentsToAdd) {
		for (int i = 0; i < reagentAdditionListeners.size(); i++) {
			reagentAdditionListeners.get(i).addSolventsFromList(reagentsToAdd);
		}
	}

	/** Auto-generated event handler method */
	protected void jComboBoxSearchTypeActionPerformed(ActionEvent evt) {
		if (jComboBoxSearchType.getSelectedItem().equals(SIMILARITY)) {
			jTextFieldSimilarity.setVisible(true);
			jLabelSimilarity.setVisible(true);
			jPanelStructQueryAttrib.revalidate();
		} else {
			jTextFieldSimilarity.setVisible(false);
			jLabelSimilarity.setVisible(false);
			jPanelStructQueryAttrib.revalidate();
		}
	}

	public void setSelectedTab(int tab) {
		jTabbedPaneMain.setSelectedIndex(tab);
	}

	public void dispose() {
		ReagentsFrameWindowClosing();
		chimeProMyReagent.setVisible(false);
		chimeProResult.setVisible(false);
		jPanel3.remove((Component) chimeProMyReagent);
		jPanel21.remove((Component) chimeProResult);
		chimeProMyReagent = null;
		chimeProResult = null;
		for (int i = 0; i < reagentAdditionListeners.size(); i++) {
			removeReagentAdditionActionListener((ReagentAdditionListener) reagentAdditionListeners.get(i));
		}
		ReagentsFrame.this.setVisible(false);
		if (parent != null) {
			parent.setVisible(false);
			parent.dispose();
			parent = null;
		}
		reagentsHandler.removeFrameLink();
		super.dispose();
	}

	public boolean shouldClose() {
		return (progressBarDialog == null);
	}

	public void internalFrameClosing() {
		myReagentsTableModel = null;
		myReagentsPropertyTableModel = null;
		reagentResultsTableModel = null;
		reagentResultsPropertyTableModel = null;
		jTableMyReagentProps.setModel(new DefaultTableModel());
		jTableReagentResults.setModel(new DefaultTableModel());
		jTableMyReagents.setModel(new DefaultTableModel());
		jTableReagentValues.setModel(new DefaultTableModel());

		dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.client.gui.page.reagents.MyReagentPropertyListener#updateReagentProperty()
	 */
	public void updateReagentProperty() {
	}
	
	public void setMyReagentsListWithoutUpdatingHandler(String reagentList) {
		myReagentsTableModel.setMyReagentsList(reagentList);
	}

	private class AddReagentsFromGUI extends JFrame {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4416594449630151351L;
		private JPanel mainPanel;
		private JTable table;
		private JScrollPane sPane;
		private FormLayout formLayout;
		private ButtonBarBuilder buttonGroup;
		private JButton addReagentsButton, cancelButton;
		private JTextField chemistName;
		private JLabel chemistNameLabel;

		private AddReagentsFromGUI(int x, int y) {
			super("Add Reagent List");
			initGUI();
			this.setSize(new Dimension(200, 350));
			this.setLocation(x - 30, y + 340);
			this.pack();
			this.setVisible(true);
		}

		private void initGUI() {
			mainPanel = new JPanel();
			formLayout = new FormLayout("1dlu, pref, 1dlu", // columns
					"2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu"); // rows
			CellConstraints cc = new CellConstraints();
			mainPanel.setLayout(formLayout);
			chemistNameLabel = new JLabel("Type Name or Select from List:");
			mainPanel.add(chemistNameLabel, cc.xy(2, 2));
			chemistName = new JTextField();
			chemistName.setColumns(10);
			mainPanel.add(chemistName, cc.xy(2, 4));
			String[] columnNames = { "Name" };
			Object[][] data = {{"User", "Name"}};
			table = new JTable(data, columnNames);
			table.setRowSelectionAllowed(true);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setSize(new Dimension(150, 250));
			table.setPreferredScrollableViewportSize(new Dimension(140, 200));
			sPane = new JScrollPane(table);
			sPane.setPreferredSize(new Dimension(150, 210));
			mainPanel.add(sPane, cc.xy(2, 6));
			cancelButton = new JButton("Cancel");
			addReagentsButton = new JButton("Add Reagents");
			buttonGroup = new ButtonBarBuilder();
			buttonGroup.addRelatedGap();
			buttonGroup.addGlue();
			buttonGroup.addGridded(addReagentsButton);
			buttonGroup.addRelatedGap();
			buttonGroup.addGlue();
			buttonGroup.addGridded(cancelButton);
			mainPanel.add(buttonGroup.getPanel(), cc.xy(2, 8));
			Container cont = AddReagentsFromGUI.this.getContentPane();
			cont.add(mainPanel);
		}
	}

	private void addReagentsFromActionPerformed(int x, int y) {
		AddReagentsFromGUI reagentsFrom = new AddReagentsFromGUI(x, y);
	}
	
	
	public static void viewReagentsFrame(final int tab, final ReagentAdditionListener listener, final String expName) {
		final Stopwatch stopwatch = new Stopwatch();		
		SwingWorker openFrameProcess = new SwingWorker() {
			public Object construct() {
				stopwatch.start("ReagentsFrame viewReagentsFrame");
				viewReagentsFrame(tab, listener, expName, false);
				return null;
			}
			public void finished() {
				stopwatch.stop();
			}
		};
		openFrameProcess.start();
	}

	public static void viewReagentsFrame(int tab, ReagentAdditionListener listener, String expName , boolean isSolventColumn) {
		//reset every time
		isSolventsFrame = false;
		ReagentsFrame rf = null;
		Frame[] frames = JFrame.getFrames();
		isSolventsFrame = isSolventColumn;
		// Find an open ReagentsFrame object and associate with the calling nb page.
		for (int i = 0; i < frames.length; i++) {
			if (frames[i] instanceof ContainerFrame && frames[i].isVisible()) {
				ContainerFrame cf = (ContainerFrame) frames[i];
				if (cf.getInternalFrame() instanceof ReagentsFrame) {
					cf.setTitle("Lookup Reagents (" + expName + ")");
					rf = (ReagentsFrame) cf.getInternalFrame();
					ReagentAdditionListener reagentListener = rf.reagentAdditionListeners.get(0);
					if (!(listener.equals(reagentListener))) {
						rf.removeReagentAdditionActionListener(reagentListener);
						rf.addReagentAdditionActionListener(listener);
					}
					rf.setSelectedTab(tab);
					cf.setLocationRelativeTo(null);
					cf.setVisible(true);
					break;
				}
			}
		}
		// If no frame was found create a new one.
		if (rf == null) {
			ContainerFrame containerFrame = new ContainerFrame();
			containerFrame.setTitle("Lookup Reagents (" + expName + ")");
			containerFrame.setSize(new java.awt.Dimension(750, 407));
			rf = new ReagentsFrame(containerFrame, tab);
			rf.addReagentAdditionActionListener(listener);
			containerFrame.addInternalFrame(rf);
			containerFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			if(isSolventColumn)
			{
				rf.jButtonAddtoStoich.setText("Add as Solvent");
				//total 4 tabs. Want to keep first tab. As we delete tabs the index is reduced.
				rf.jTabbedPaneMain.remove(1);
				rf.jTabbedPaneMain.remove(1);
				if (rf.jTabbedPaneMain.getTabCount() >1) {
					rf.jTabbedPaneMain.remove(1);
				}
			}
			rf.show();
			containerFrame.setLocationRelativeTo(null);
			containerFrame.setVisible(true);
		}
	}
}