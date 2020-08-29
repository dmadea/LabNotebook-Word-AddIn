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
package com.chemistry.enotebook.client.gui.query_search;

import com.chemistry.ChemistryEditorEvent;
import com.chemistry.ChemistryEditorListener;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.PeoplePicker;
import com.chemistry.enotebook.client.gui.PersonFinder;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.gui.common.utils.JTextFieldFilter;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.client.gui.page.analytical.DateComboBox;
import com.chemistry.enotebook.compoundmgmtservice.delegate.CompoundMgmtServiceDelegate;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ReactionStepModel;
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ChemUtilInitException;
import com.chemistry.enotebook.sdk.ReactionProperties;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.searchquery.SearchQueryParams;
import com.chemistry.enotebook.storage.ReactionPageInfo;
import com.chemistry.enotebook.storage.StorageException;
import com.chemistry.enotebook.storage.exceptions.SearchTooMuchDataException;
import com.chemistry.enotebook.utils.CodeTableUtils;
import com.chemistry.enotebook.utils.SwingWorker;
import com.chemistry.viewer.ChemistryViewer;
import com.common.chemistry.codetable.CodeTableCache;
import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class Parallel_Query_SearchContainer extends JFrame implements FormConstants, PersonFinder, ChemistryEditorListener {
	
	private static final long serialVersionUID = -1720266236037313863L;
	
	private JComboBox jComboBoxSimilarityAlgol;
	private JTextField StructureSimilarity;
	private JLabel jLabelStrucSimilarity;
	private JComboBox jComboBoxOtherSearchReq;
	private JCheckBox jCheckBoxOtherReq;
	private JTextField chemNames;
	private JComboBox jComboBoxChemNamesFunction;
	private JCheckBox jCheckBoxChemNames;
	private JTextField spidNum;
	private JTextField requestId;
	private JTextField compIDs;
	private JComboBox jComboBoxCompIDsFunction;
	private JCheckBox jCheckBoxCompIDs;
	private JCheckBox jCheckBoxSpidNum;
	private JCheckBox jCheckBoxRequestId;
	private JTextField reactionProc;
	private JComboBox jComboBoxReactionProcFunction;
	private JCheckBox jCheckBoxReactionProc;
	private JTextField litRef;
	private JComboBox jComboBoxLitRefFunction;
	private JCheckBox jCheckBoxLitRef;
	private JTextField subjectTitle;
	private JComboBox jComboBoxSubjectTitleFunction;
	private JCheckBox jCheckBoxSubjectTitle;
	private JTextField jTextFieldUpperPurityLimit;
	private JTextField jTextFieldLowerPurityLimit;
	private JComboBox jComboBoxProductPurityFunction;
	private JCheckBox jCheckBoxProductPurity;
	private JTextField jTextFieldReactionUpperLimit;
	private JTextField jTextFieldReactionYieldLowerLimit;
	private JComboBox jComboBoxReactionYieldFunction;
	private JCheckBox jCheckBoxReactionYield;
	private JRadioButton jRadioButtonNotebooksOfAllChem_sites;
	private JRadioButton jRadioButtonMyNotebook;
	private JCheckBox jCheckBoxArchivedPages;
	private JCheckBox jCheckBoxSignedOffPages;
	private JCheckBox jCheckBoxCompletedPages;
	private JCheckBox jCheckBoxActivePage;
	private JCheckBox jCheckBoxCenceptionPages;
	private JCheckBox jCheckBoxParrellelPages;
	private JCheckBox jCheckBoxSingletonPages;
	private DateComboBox pageCreationDate;
	private JComboBox jComboBoxPageCreationFunction;
	private JCheckBox jCheckBoxPageCreationDate;
	private CeNComboBox jComboBoxProjectCode;
	private JCheckBox jCheckBoxProjectCode;
	private CeNComboBox jComboBoxTherapeticArea;
	private JCheckBox jCheckBoxTherapeuticArea;
	private JLabel jLabelNotebooksearchDomainTitle;
	private JLabel jLabelPageStatusFilterTitle;
	private JLabel jLabelFieldSearchTitle;
	private JLabel jLabelPageTypeTitle;
	private JLabel jLabelStrucTitle;
	private JButton jButtonCancelReactionPages;
	private JButton jButtonSearchReactionPages;
	private JButton jButtonOptionsReactionPages;
	private JTabbedPane jTabbedPaneSearchType;
	private ChemistryViewer structQueryCanvas;
	private JPanel structPanel;
	// Parallel Components
	private JPanel centerGuiPanel;
	private DefaultFormBuilder formBuilder;
	private ButtonBarBuilder buttonGroup;
	private JTextField structureLookUpTxtField;
	private JButton structureLookUpButton;
	private JLabel searchForLabel;
	private JComboBox searchForCombo;
	private ButtonGroup buttonGroupNotebookDomainSearch;
	private JRadioButton notebooksOfTheseUsers;
	private JTextField theseUsers;
	private JButton personFinder;
	private boolean advancedOptionsVisible;
	private JLabel structureLabelCompoundId;
	private JCheckBox externalCollaborators;
	public final String NONE_VALUE = "-None-";
	public final String SUB_STRUCTURE = "Substructure";
	public final String SIMILARITY = "Similarity";
	public final String EQUALITY = "Exact";
	public final String EUCLID_ALG = "Euclid";
	public final String EDS_ALG = "EDS";
	public final String TANIMOTO_ALG = "Tanimoto";
	private final String GREATER_THAN_OR_EQUAL = ">=";
	private final String LESS_THAN_OR_EQUAL = "<=";
	private final String EQUAL = "=";
	private final String BETWEEN = "Between";
	private final String CONTAINS = "Contains";
	private final String STARTS_WITH = "Starts With";
	private final String EQUALS = "Equals";
	private final String DOES_NOT_CONTAINS = "Does Not Contain";
	// private QueryResultsVO rowset = new QueryResultsVO();
	private List<String> notebookPages;
	private static Parallel_Query_SearchContainer instance_ = null;

	private Frame owner = null;
	
	public static Parallel_Query_SearchContainer getInstance(Frame owner) {
		if (instance_ == null) {
			instance_ = new Parallel_Query_SearchContainer(owner);
		}
		return instance_;
	}

	public void dispose() {
		if (instance_ != null) {
			instance_.setVisible(false);
		}
		instance_ = null;
	}

	private Parallel_Query_SearchContainer(Frame owner) {
		this.owner = owner;
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			advancedOptionsVisible = true;
			buttonGroupNotebookDomainSearch = new ButtonGroup();
			jTabbedPaneSearchType = new JTabbedPane();
			notebooksOfTheseUsers = new JRadioButton();
			notebooksOfTheseUsers.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					JRadioButton btn = (JRadioButton) evt.getSource();
					externalCollaborators.setEnabled(!btn.isSelected());
					personFinder.setEnabled(btn.isSelected());
					theseUsers.setEnabled(btn.isSelected());
				}
			});
			theseUsers = new JTextField();
			structureLookUpTxtField = new JTextField();
			externalCollaborators = new JCheckBox("Include External Collaborators");
			structureLookUpButton = new JButton();
			searchForLabel = new JLabel("Search:");
			searchForCombo = new JComboBox(new String[] { "Products", "Reactions" });
			jButtonOptionsReactionPages = new JButton();
			jButtonSearchReactionPages = new JButton();
			jButtonCancelReactionPages = new JButton();
			structureLabelCompoundId = new JLabel();
			jLabelStrucTitle = new JLabel();
			jCheckBoxOtherReq = new JCheckBox();
			jComboBoxOtherSearchReq = new JComboBox();
			jLabelStrucSimilarity = new JLabel();
			StructureSimilarity = new JTextField();
			jComboBoxSimilarityAlgol = new JComboBox();
			jLabelFieldSearchTitle = new JLabel();
			jCheckBoxTherapeuticArea = new JCheckBox();
			jComboBoxTherapeticArea = new CeNComboBox();
			jComboBoxTherapeticArea.addActionListener(new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					String item = (String)jComboBoxTherapeticArea.getSelectedItem();
					jCheckBoxTherapeuticArea.setSelected(item != null && !NONE_VALUE.equals(item));

				}
			});

			jCheckBoxProjectCode = new JCheckBox();
			jComboBoxProjectCode = new CeNComboBox();
			jComboBoxProjectCode.addActionListener(new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					String item = (String)jComboBoxProjectCode.getSelectedItem();
					jCheckBoxProjectCode.setSelected(item != null && !NONE_VALUE.equals(item));
				}
			});
			
			jCheckBoxPageCreationDate = new JCheckBox();
			jComboBoxPageCreationFunction = new JComboBox();
			pageCreationDate = new DateComboBox();
			pageCreationDate.addActionListener(new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					jCheckBoxPageCreationDate.setSelected(pageCreationDate.getSelectedItem() != null);
				}
			});

			jCheckBoxReactionYield = new JCheckBox();
			jComboBoxReactionYieldFunction = new JComboBox();
			jTextFieldReactionYieldLowerLimit = new JTextField();
			addListener(jTextFieldReactionYieldLowerLimit, jCheckBoxReactionYield);
			jTextFieldReactionUpperLimit = new JTextField();
			addListener(jTextFieldReactionUpperLimit, jCheckBoxReactionYield);
			
			jCheckBoxProductPurity = new JCheckBox();
			jComboBoxProductPurityFunction = new JComboBox();
			jTextFieldLowerPurityLimit = new JTextField();
			addListener(jTextFieldLowerPurityLimit, jCheckBoxProductPurity);
			jTextFieldUpperPurityLimit = new JTextField();
			addListener(jTextFieldUpperPurityLimit, jCheckBoxProductPurity);
			
			jCheckBoxSubjectTitle = new JCheckBox();
			jComboBoxSubjectTitleFunction = new JComboBox();
			subjectTitle = new JTextField();
			addListener(subjectTitle, jCheckBoxSubjectTitle);
			
			jCheckBoxLitRef = new JCheckBox();
			jComboBoxLitRefFunction = new JComboBox();
			litRef = new JTextField();
			addListener(litRef, jCheckBoxLitRef);
			
			jCheckBoxReactionProc = new JCheckBox();
			jComboBoxReactionProcFunction = new JComboBox();
			reactionProc = new JTextField();
			addListener(reactionProc, jCheckBoxReactionProc);
			
			jCheckBoxCompIDs = new JCheckBox();
			jCheckBoxSpidNum = new JCheckBox();
			jCheckBoxRequestId = new JCheckBox();
			jComboBoxCompIDsFunction = new JComboBox();
			compIDs = new JTextField();
			addListener(compIDs, jCheckBoxCompIDs);
			
			jCheckBoxChemNames = new JCheckBox();
			spidNum = new JTextField();
			addListener(spidNum, jCheckBoxSpidNum);

			requestId = new JTextField();
			addListener(requestId, jCheckBoxRequestId);			
			
			jComboBoxChemNamesFunction = new JComboBox();
			chemNames = new JTextField();
			addListener(chemNames, jCheckBoxChemNames);
			
			jLabelPageTypeTitle = new JLabel();
			jCheckBoxSingletonPages = new JCheckBox();
			jCheckBoxParrellelPages = new JCheckBox();
			jCheckBoxCenceptionPages = new JCheckBox();
			jLabelPageStatusFilterTitle = new JLabel();
			jCheckBoxActivePage = new JCheckBox();
			jCheckBoxCompletedPages = new JCheckBox();
			jCheckBoxSignedOffPages = new JCheckBox();
			jCheckBoxArchivedPages = new JCheckBox();
			jLabelNotebooksearchDomainTitle = new JLabel();
			jRadioButtonMyNotebook = new JRadioButton();
			jRadioButtonMyNotebook.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					JRadioButton btn = (JRadioButton) evt.getSource();
					externalCollaborators.setEnabled(!btn.isSelected());
					externalCollaborators.setSelected(!btn.isSelected());
					personFinder.setEnabled(!btn.isSelected());
					theseUsers.setEnabled(!btn.isSelected());
				}
			});
			jRadioButtonNotebooksOfAllChem_sites = new JRadioButton();
			jRadioButtonNotebooksOfAllChem_sites.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					JRadioButton btn = (JRadioButton) evt.getSource();
					externalCollaborators.setEnabled(btn.isSelected());
					personFinder.setEnabled(!btn.isSelected());
					theseUsers.setEnabled(!btn.isSelected());
				}
			});
			personFinder = new JButton("Find");
//			ImageIcon personFace = CenIconFactory.getImageIcon("images/clipart_ face.jpg");
//			personFinder.setIcon(personFace);
			final Parallel_Query_SearchContainer pQS = this;
			personFinder.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					PeoplePicker picker = new PeoplePicker(Parallel_Query_SearchContainer.this, pQS, false);
					picker.setVisible(true);
				}
			});
			personFinder.setEnabled(false);
			theseUsers.setEnabled(false);
			jTabbedPaneSearchType.setPreferredSize(new java.awt.Dimension(755, 545));
			jTabbedPaneSearchType.setMinimumSize(new java.awt.Dimension(779, 589));
			this.getContentPane().add(jTabbedPaneSearchType);
			jTabbedPaneSearchType.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent evt) {
					jTabbedPaneSearchTypeStateChanged(evt);
				}
			});
			buttonGroup = new ButtonBarBuilder();
			// // "Less Options Button"
			// jButtonOptionsReactionPages.setText("<< Less Options");
			// jButtonOptionsReactionPages.setFont(new
			// java.awt.Font("sansserif",1,11));
			// buttonGroup.addRelatedGap();
			// buttonGroup.addGlue();
			// buttonGroup.addGridded(jButtonOptionsReactionPages);
			// jButtonOptionsReactionPages.addActionListener( new
			// ActionListener() {
			// public void actionPerformed(ActionEvent evt) {
			// jButtonOptionsReactionPagesActionPerformed(evt);
			// }
			// });
			// "Search Button"
			jButtonSearchReactionPages.setText("Search");
			buttonGroup.addRelatedGap();
			buttonGroup.addGlue();
			buttonGroup.addGridded(jButtonSearchReactionPages);
			jButtonSearchReactionPages.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonSearchReactionPagesActionPerformed(evt);
				}
			});
			// "Cancel Button"
			jButtonCancelReactionPages.setText("Cancel");
			buttonGroup.addRelatedGap();
			buttonGroup.addGlue();
			buttonGroup.addGridded(jButtonCancelReactionPages);
			jButtonCancelReactionPages.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelReactionPagesActionPerformed(evt);
				}
			});
			// add filler to the left
			buttonGroup.addRelatedGap();
			buttonGroup.addGlue();
			com.jgoodies.forms.layout.FormLayout centerGuiLayout = new com.jgoodies.forms.layout.FormLayout(
					"3dlu, 47, 1dlu, 70, 1dlu, 40, 4dlu, right:90, 1dlu, 2, 2dlu, center:65, 1dlu, 30, 5dlu, 20, 2dlu, 120, 2dlu, 80, 2dlu, 40, 2dlu, 40, 2dlu, 70, 2dlu",// columns
					"1dlu, 18, 2dlu, 18, 1dlu, 18, 1dlu, 18, 1dlu, 18, 1dlu, 18,1dlu, 18, 1dlu, 18, 1dlu, 18, 1dlu, 18, 1dlu, 18, 1dlu, 18, 1dlu, 18, 1dlu, 18, 5dlu, 18, 1dlu, 18, 1dlu, 18, 1dlu, 18, 10dlu, 18, 1dlu, 18, 1dlu, 18, 1dlu, 18, 5dlu "); // 41
			// rows
			formBuilder = new DefaultFormBuilder(centerGuiLayout);
			// SECOND COLUMN, SECOND ROW
			// add Structrue Label
			jLabelStrucTitle.setText("A. Structure/Reaction Search Query:");
			jLabelStrucTitle.setVisible(true);
			addFormComponent(formBuilder, jLabelStrucTitle, SECOND, SECOND, 8, 1);
			// add Field Criteria Label
			jLabelFieldSearchTitle.setText("B. Field Search Criteria:");
			addFormComponent(formBuilder, jLabelFieldSearchTitle, SIXTEENTH, SECOND, 4, 2);
			structureLookUpTxtField.setVisible(true);
			structureLookUpButton = new JButton("Load");
//			ImageIcon searchIcon = CenIconFactory.getImageIcon("images/Benzene.png");
//			structureLookUpButton.setIcon(searchIcon);
			structureLookUpButton.setVisible(true);
			structureLookUpButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					structureLookup();
				}
			});
			searchForLabel.setVisible(true);
			searchForCombo.setVisible(true);
			structureLabelCompoundId.setVisible(true);
			structureLabelCompoundId.setText("Comp.ID");
			// SECOND COLUMN
			// TWENTYTH ROW
			// add Structure Look up
			addFormComponent(formBuilder, structureLabelCompoundId, SECOND, TWENTY_SIXTH, 1, 1);
			addFormComponent(formBuilder, structureLookUpTxtField, FOURTH, TWENTY_SIXTH, 1, 1);
			// SIXTH COLUMN
			addFormComponent(formBuilder, structureLookUpButton, SIXTH, TWENTY_SIXTH, 1, 1);
			// EIGHTH COLUMN
			addFormComponent(formBuilder, searchForLabel, EIGHTH, TWENTY_SIXTH, 1, 1);
			// TENTH COLUMN
			addFormComponent(formBuilder, searchForCombo, TENTH, TWENTY_SIXTH, 5, 1);
			jCheckBoxOtherReq.setVisible(true);
			addFormComponent(formBuilder, jCheckBoxOtherReq, SECOND, TWENTY_EIGHTH, 1, 1);
			jComboBoxOtherSearchReq.setVisible(true);
			jComboBoxOtherSearchReq.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					String item = (String)jComboBoxOtherSearchReq.getSelectedItem();
					jCheckBoxOtherReq.setSelected(item != null && !NONE_VALUE.equals(item));
					jComboBoxOtherSearchReqItemStateChanged(evt);
				}
			});
			addFormComponent(formBuilder, jComboBoxOtherSearchReq, FOURTH, TWENTY_EIGHTH, 3, 1);
			jLabelStrucSimilarity.setText("Similarity %");
			// to be changed with form layout
			addFormComponent(formBuilder, jLabelStrucSimilarity, TWELFTH, TWENTY_EIGHTH, 1, 1);
			StructureSimilarity.setText("95");
			// to be changed with form layout
			addFormComponent(formBuilder, StructureSimilarity, FOURTEENTH, TWENTY_EIGHTH, 1, 1);
			addFormComponent(formBuilder, jComboBoxSimilarityAlgol, EIGHTH, TWENTY_EIGHTH, 2, 1);
			// SIXTEENTH Column , FORTH ROW
			jCheckBoxTherapeuticArea.setText("Therapeutic Area ");
			addFormComponent(formBuilder, jCheckBoxTherapeuticArea, SIXTEENTH, FOURTH, 4, 1);
			// TWENTYTH Column , FORTH ROW
			jComboBoxTherapeticArea.setVisible(true);
			jComboBoxTherapeticArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			addFormComponent(formBuilder, jComboBoxTherapeticArea, TWENTYTH, FOURTH, 8, 1);
			jComboBoxTherapeticArea.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					String taString = (String) jComboBoxTherapeticArea.getSelectedItem();
					String taCode = "";
					try {
						taCode = CodeTableCache.getCache().getTAsCode(taString);
					} catch (Exception e) { /* Ignored */
					}
					CodeTableUtils.fillComboBoxWithProjects(jComboBoxProjectCode, taCode);
				}
			});
			// SIXTEENTH Column , SIXTH ROW
			jCheckBoxProjectCode.setText("Project Code & Name");
			jCheckBoxProjectCode.setVisible(true);
			addFormComponent(formBuilder, jCheckBoxProjectCode, SIXTEENTH, SIXTH, 4, 1);
			// TWENTYTH Column
			jComboBoxProjectCode.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			addFormComponent(formBuilder, jComboBoxProjectCode, TWENTYTH, SIXTH, 8, 1);
			jCheckBoxPageCreationDate.setText("Page Creation Date");
			addFormComponent(formBuilder, jCheckBoxPageCreationDate, SIXTEENTH, EIGHTH, 4, 1);
			addFormComponent(formBuilder, jComboBoxPageCreationFunction, TWENTYTH, EIGHTH, 2, 1);
			addFormComponent(formBuilder, pageCreationDate, TWENTY_SECOND, EIGHTH, 6, 1);
			jCheckBoxReactionYield.setText("Reaction Yield %");
			addFormComponent(formBuilder, jCheckBoxReactionYield, SIXTEENTH, TENTH, 4, 1);
			addFormComponent(formBuilder, jComboBoxReactionYieldFunction, TWENTYTH, TENTH, 2, 1);
			jComboBoxReactionYieldFunction.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					jComboBoxReactionYieldFunctionItemStateChanged(evt);
				}
			});
			//jTextFieldReactionYieldLowerLimit.setText("0");
			addFormComponent(formBuilder, jTextFieldReactionYieldLowerLimit, TWENTY_SECOND, TENTH, 2, 1);
			addFormComponent(formBuilder, jTextFieldReactionUpperLimit, TWENTY_FOURTH, TENTH, 4, 1);
			jCheckBoxProductPurity.setText("Product Purity %");
			addFormComponent(formBuilder, jCheckBoxProductPurity, SIXTEENTH, TWELFTH, 4, 1);
			addFormComponent(formBuilder, jComboBoxProductPurityFunction, TWENTYTH, TWELFTH, 2, 1);
			jComboBoxProductPurityFunction.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					jComboBoxProductPurityFunctionItemStateChanged(evt);
				}
			});
			//jTextFieldLowerPurityLimit.setText("0");
			addFormComponent(formBuilder, jTextFieldLowerPurityLimit, TWENTY_SECOND, TWELFTH, 2, 1);
			addFormComponent(formBuilder, jTextFieldUpperPurityLimit, TWENTY_FOURTH, TWELFTH, 4, 1);
			jCheckBoxSubjectTitle.setText("Subject Title");
			addFormComponent(formBuilder, jCheckBoxSubjectTitle, SIXTEENTH, FOURTEENTH, 4, 1);
			addFormComponent(formBuilder, jComboBoxSubjectTitleFunction, TWENTYTH, FOURTEENTH, 2, 1);
			addFormComponent(formBuilder, subjectTitle, TWENTY_SECOND, FOURTEENTH, 6, 1);
			jCheckBoxLitRef.setText("Literature Ref.");
			addFormComponent(formBuilder, jCheckBoxLitRef, SIXTEENTH, SIXTEENTH, 4, 1);
			addFormComponent(formBuilder, jComboBoxLitRefFunction, TWENTYTH, SIXTEENTH, 2, 1);
			addFormComponent(formBuilder, litRef, TWENTY_SECOND, SIXTEENTH, 6, 1);
			jCheckBoxReactionProc.setText("Reaction Procedure");
			addFormComponent(formBuilder, jCheckBoxReactionProc, SIXTEENTH, EIGHTEENTH, 4, 1);
			addFormComponent(formBuilder, jComboBoxReactionProcFunction, TWENTYTH, EIGHTEENTH, 2, 1);
			addFormComponent(formBuilder, reactionProc, TWENTY_SECOND, EIGHTEENTH, 6, 1);
			jCheckBoxCompIDs.setText("Compound ID");
			addFormComponent(formBuilder, jCheckBoxCompIDs, SIXTEENTH, TWENTYTH, 4, 1);
			addFormComponent(formBuilder, jComboBoxCompIDsFunction, TWENTYTH, TWENTYTH, 2, 1);
			addFormComponent(formBuilder, compIDs, TWENTY_SECOND, TWENTYTH, 6, 1);
			jCheckBoxChemNames.setText("Chemical Name");
			addFormComponent(formBuilder, jCheckBoxChemNames, SIXTEENTH, TWENTY_SECOND, 4, 1);
			addFormComponent(formBuilder, jComboBoxChemNamesFunction, TWENTYTH, TWENTY_SECOND, 2, 1);
			addFormComponent(formBuilder, chemNames, TWENTY_SECOND, TWENTY_SECOND, 6, 1);
			jCheckBoxSpidNum.setText("SPID Number");
			addFormComponent(formBuilder, jCheckBoxSpidNum, SIXTEENTH, TWENTY_FOURTH, 4, 1);
			addFormComponent(formBuilder, spidNum, TWENTYTH, TWENTY_FOURTH, 8, 1);
			jCheckBoxRequestId.setText("Request ID");
			addFormComponent(formBuilder, jCheckBoxRequestId, SIXTEENTH, TWENTY_SIXTH, 4, 1);
			addFormComponent(formBuilder, requestId, TWENTYTH, TWENTY_SIXTH, 8, 1);
			jLabelPageTypeTitle.setText("C. Experiment Type Search Filters:");
			jLabelPageTypeTitle.setVisible(true);
			addFormComponent(formBuilder, jLabelPageTypeTitle, SECOND, THIRTHY, 8, 1);
			jCheckBoxSingletonPages.setVisible(true);
			jCheckBoxSingletonPages.setText("MedChem Experiments");
			// jCheckBoxSingletonPages.setSelected(true);
			addFormComponent(formBuilder, jCheckBoxSingletonPages, SECOND, THIRTHY_SECOND, 6, 1);
			jCheckBoxParrellelPages.setVisible(true);
			jCheckBoxParrellelPages.setText("Parallel Experiments");
			jCheckBoxParrellelPages.setEnabled(MasterController.isParallelViewEnabled());
			addFormComponent(formBuilder, jCheckBoxParrellelPages, SECOND, THIRTHY_FOURTH, 6, 1);
			jCheckBoxCenceptionPages.setVisible(true);
			jCheckBoxCenceptionPages.setText("Conception Records");
			addFormComponent(formBuilder, jCheckBoxCenceptionPages, SECOND, THIRTHY_SIXTH, 6, 1);
			jLabelPageStatusFilterTitle.setText("D. Experiment Status Search Filters:");
			addFormComponent(formBuilder, jLabelPageStatusFilterTitle, SIXTEENTH, THIRTHY, 8, 1);
			jCheckBoxActivePage.setText("Active Experiments");
			jCheckBoxActivePage.setSelected(true);
			addFormComponent(formBuilder, jCheckBoxActivePage, SIXTEENTH, THIRTHY_SECOND, 6, 1);
			jCheckBoxCompletedPages.setText("Completed Experiments");
			jCheckBoxCompletedPages.setSelected(true);
			addFormComponent(formBuilder, jCheckBoxCompletedPages, SIXTEENTH, THIRTHY_FOURTH, 6, 1);
			jLabelNotebooksearchDomainTitle.setText("E. Notebook Searching Domain:");
			addFormComponent(formBuilder, jLabelNotebooksearchDomainTitle, SECOND, THIRTHY_EIGHT, 8, 1);
			jRadioButtonMyNotebook.setText("My own Notebooks only");
			jRadioButtonMyNotebook.setSelected(true);
			buttonGroupNotebookDomainSearch.add(jRadioButtonMyNotebook);
			addFormComponent(formBuilder, jRadioButtonMyNotebook, SECOND, FOURTHY, 6, 1);
			jRadioButtonNotebooksOfAllChem_sites.setText("Notebooks of chemists across all sites");
			buttonGroupNotebookDomainSearch.add(jRadioButtonNotebooksOfAllChem_sites);
			addFormComponent(formBuilder, jRadioButtonNotebooksOfAllChem_sites, SECOND, FOURTHY_SECOND, 10, 1);
			externalCollaborators.setEnabled(false);
			addFormComponent(formBuilder, externalCollaborators, SIXTEENTH, FOURTHY_SECOND, 10, 1);
			notebooksOfTheseUsers.setText("Notebooks Of These Users:");
			buttonGroupNotebookDomainSearch.add(notebooksOfTheseUsers);
			addFormComponent(formBuilder, notebooksOfTheseUsers, SECOND, FOURTHY_FOURTH, 8, 1);
			addFormComponent(formBuilder, theseUsers, TENTH, FOURTHY_FOURTH, 9, 1);
			addFormComponent(formBuilder, personFinder, TWENTYTH, FOURTHY_FOURTH, 1, 1);
			// apply default selection
			setDefaults();
			centerGuiPanel = formBuilder.getPanel();
			this.getContentPane().add(centerGuiPanel, BorderLayout.CENTER);
			this.getContentPane().add(buttonGroup.getPanel(), BorderLayout.SOUTH);
			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setTitle("Query & Search");
			this.setSize(new java.awt.Dimension(790, 550));
			this.setResizable(false);
			postInitGUI();
			pack();
			jButtonSearchReactionPages.requestFocusInWindow();
			this.setLocationRelativeTo(owner);
			this.setVisible(true);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	private void structureLookup() {
		String id = structureLookUpTxtField.getText();
		try {
			String struc = (new CompoundMgmtServiceDelegate()).getStructureByCompoundNo(id).get(0);
			//String struc = CLSLookup2.getInstance().getStructurebyId(id);
			if (StringUtils.isNotEmpty(struc)) {
				structQueryCanvas.setChemistry(struc.getBytes());
			} else {
				JOptionPane.showMessageDialog(this, "Can't find structure with this ID.");
			}
		} catch (Exception ex) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, ex);
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
		getRootPane().setGlassPane(CeNGUIUtils.createGlassPane());
	}

	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		this.getRootPane().getGlassPane().setVisible(!b);
	}
	
	/** Add your post-init code in here */
	public void postInitGUI() {
		structQueryCanvas = new ChemistryViewer("Query & Search", "Structure in Query & Search");
		structQueryCanvas.setEditorType(MasterController.getGuiController().getDrawingTool());
		structQueryCanvas.addChemistryEditorListener(this);
		
		structPanel = new JPanel(new BorderLayout());
		structPanel.setBorder(new MatteBorder(new Insets(1, 1, 1, 1), new Color(0, 0, 0)));
		structPanel.add(structQueryCanvas, BorderLayout.CENTER);
		
		setIconImage(CenIconFactory.getImage(CenIconFactory.General.APPLICATION));
		populateDropdown();
		
		StructureSimilarity.setVisible(false);
		jComboBoxSimilarityAlgol.setVisible(false);
		jLabelStrucSimilarity.setVisible(false);
		
		StructureSimilarity.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));

		// add structure renderer
		try {			
			addFormComponent(formBuilder, structPanel, SECOND, FOURTH, 13, 21);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void editingStarted(ChemistryEditorEvent e) {
	}

	public void editingStopped(ChemistryEditorEvent e) {
		this.toFront();
	}
	
	public void structureChanged(ChemistryEditorEvent e) {
		jCheckBoxOtherReq.setSelected(true);
		
		if (((String) jComboBoxOtherSearchReq.getSelectedItem()).equals(NONE_VALUE))
			jComboBoxOtherSearchReq.setSelectedItem(SUB_STRUCTURE);
		
		structQueryCanvas.updateChemistry();
		repaint();
	}
		
	/**
	 * 
	 */
	private void populateDropdown() {
		// structure search type
		jComboBoxOtherSearchReq.addItem(this.NONE_VALUE);
		jComboBoxOtherSearchReq.addItem(this.SUB_STRUCTURE);
		jComboBoxOtherSearchReq.addItem(this.SIMILARITY);
		jComboBoxOtherSearchReq.addItem(this.EQUALITY);
		
		// structure search algorithm type
		jComboBoxSimilarityAlgol.addItem(this.TANIMOTO_ALG);
		jComboBoxSimilarityAlgol.addItem(this.EUCLID_ALG);
		jComboBoxSimilarityAlgol.addItem(this.EDS_ALG);
		// TA code population
		CodeTableUtils.fillComboBoxWithTAs(jComboBoxTherapeticArea);
		// project code list population
		CodeTableUtils.fillComboBoxWithProjects(jComboBoxProjectCode, null);
		// creation date operator drop down
		jComboBoxPageCreationFunction.addItem(this.EQUAL);
		jComboBoxPageCreationFunction.addItem(this.GREATER_THAN_OR_EQUAL);
		jComboBoxPageCreationFunction.addItem(this.LESS_THAN_OR_EQUAL);
		// reaction yield drop down
		jComboBoxReactionYieldFunction.addItem(this.BETWEEN);
		jComboBoxReactionYieldFunction.addItem(this.EQUAL);
		jComboBoxReactionYieldFunction.addItem(this.GREATER_THAN_OR_EQUAL);
		jComboBoxReactionYieldFunction.addItem(this.LESS_THAN_OR_EQUAL);
		jComboBoxReactionYieldFunction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jTextFieldReactionUpperLimit.setEnabled(jComboBoxReactionYieldFunction.getSelectedIndex() == 0);
			}
		});
		// product purity drop down
		jComboBoxProductPurityFunction.addItem(this.BETWEEN);
		jComboBoxProductPurityFunction.addItem(this.EQUAL);
		jComboBoxProductPurityFunction.addItem(this.GREATER_THAN_OR_EQUAL);
		jComboBoxProductPurityFunction.addItem(this.LESS_THAN_OR_EQUAL);
		jComboBoxProductPurityFunction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jTextFieldUpperPurityLimit.setEnabled(jComboBoxProductPurityFunction.getSelectedIndex() == 0);
			}
		});
		jComboBoxSubjectTitleFunction.addItem(this.CONTAINS);
		jComboBoxSubjectTitleFunction.addItem(this.EQUALS);
		jComboBoxSubjectTitleFunction.addItem(this.STARTS_WITH);
		jComboBoxSubjectTitleFunction.addItem(this.DOES_NOT_CONTAINS);
		jComboBoxLitRefFunction.addItem(this.CONTAINS);
		jComboBoxLitRefFunction.addItem(this.EQUALS);
		jComboBoxLitRefFunction.addItem(this.STARTS_WITH);
		jComboBoxLitRefFunction.addItem(this.DOES_NOT_CONTAINS);
		jComboBoxReactionProcFunction.addItem(this.CONTAINS);
		jComboBoxReactionProcFunction.addItem(this.EQUALS);
		jComboBoxReactionProcFunction.addItem(this.STARTS_WITH);
		jComboBoxReactionProcFunction.addItem(this.DOES_NOT_CONTAINS);
		jComboBoxCompIDsFunction.addItem(this.CONTAINS);
		jComboBoxCompIDsFunction.addItem(this.EQUALS);
		jComboBoxCompIDsFunction.addItem(this.STARTS_WITH);
		jComboBoxCompIDsFunction.addItem(this.DOES_NOT_CONTAINS);
		jComboBoxChemNamesFunction.addItem(this.CONTAINS);
		jComboBoxChemNamesFunction.addItem(this.EQUALS);
		jComboBoxChemNamesFunction.addItem(this.STARTS_WITH);
		jComboBoxChemNamesFunction.addItem(this.DOES_NOT_CONTAINS);
	}

	/** Auto-generated event handler method */
	protected void jTabbedPaneSearchTypeStateChanged(ChangeEvent evt) {
		JTabbedPane pane = (JTabbedPane) evt.getSource();
		// Get current tab
		for (int c = 0; c <= pane.getTabCount() - 1; c++) {
			pane.setBackgroundAt(c, Color.LIGHT_GRAY);
		}
		pane.setBackgroundAt(pane.getSelectedIndex(), Color.WHITE);
	}

	/** Auto-generated event handler method */
	protected void jButtonOptionsReactionPagesActionPerformed(ActionEvent evt) {
		if (advancedOptionsVisible) {
			setDefaults();
			setAdvancedOptionsToVisible();
			jButtonOptionsReactionPages.setText("More Options >>");
		} else {
			setAdvancedOptionsToVisible();
			jButtonOptionsReactionPages.setText("<< Less Options");
		}
	}

	/**
	 * set the visibility of advanced search options. If button "Less Options" is pressed the advanced options fields are
	 * invisibiliated :)
	 * 
	 * @param advancedOptionsVisible
	 */
	private void setAdvancedOptionsToVisible() {
		advancedOptionsVisible = !advancedOptionsVisible;
		jCheckBoxSubjectTitle.setEnabled(advancedOptionsVisible);
		jComboBoxSubjectTitleFunction.setEnabled(advancedOptionsVisible);
		subjectTitle.setEnabled(advancedOptionsVisible);
		jCheckBoxLitRef.setEnabled(advancedOptionsVisible);
		jComboBoxLitRefFunction.setEnabled(advancedOptionsVisible);
		litRef.setEnabled(advancedOptionsVisible);
		jCheckBoxReactionProc.setEnabled(advancedOptionsVisible);
		jComboBoxReactionProcFunction.setEnabled(advancedOptionsVisible);
		reactionProc.setEnabled(advancedOptionsVisible);
		jCheckBoxCompIDs.setEnabled(advancedOptionsVisible);
		jCheckBoxSpidNum.setEnabled(advancedOptionsVisible);
		jCheckBoxRequestId.setEnabled(advancedOptionsVisible);
		jComboBoxCompIDsFunction.setEnabled(advancedOptionsVisible);
		compIDs.setEnabled(advancedOptionsVisible);
		jCheckBoxChemNames.setEnabled(advancedOptionsVisible);
		spidNum.setEnabled(advancedOptionsVisible);
		requestId.setEnabled(advancedOptionsVisible);
		jComboBoxChemNamesFunction.setEnabled(advancedOptionsVisible);
		chemNames.setEnabled(advancedOptionsVisible);
		jLabelPageTypeTitle.setEnabled(advancedOptionsVisible);
		jCheckBoxSingletonPages.setEnabled(advancedOptionsVisible);
		jCheckBoxParrellelPages.setEnabled(advancedOptionsVisible);
		jCheckBoxCenceptionPages.setEnabled(advancedOptionsVisible);
		jLabelPageStatusFilterTitle.setEnabled(advancedOptionsVisible);
		jCheckBoxActivePage.setEnabled(advancedOptionsVisible);
		jCheckBoxCompletedPages.setEnabled(advancedOptionsVisible);
		jCheckBoxSignedOffPages.setEnabled(advancedOptionsVisible);
		jCheckBoxArchivedPages.setEnabled(advancedOptionsVisible);
		jLabelNotebooksearchDomainTitle.setEnabled(advancedOptionsVisible);
		jRadioButtonMyNotebook.setEnabled(advancedOptionsVisible);
		jRadioButtonNotebooksOfAllChem_sites.setEnabled(advancedOptionsVisible);
		notebooksOfTheseUsers.setEnabled(advancedOptionsVisible);
		theseUsers.setEnabled(advancedOptionsVisible);
		personFinder.setEnabled(advancedOptionsVisible);
	}

	protected void jButtonCancelReactionPagesActionPerformed(ActionEvent evt) {
		setVisible(false);
		dispose();
	}

	protected void jButtonSearchReactionPagesActionPerformed(ActionEvent evt) {
		// Check valid page options
		// at least one page status and at least one page type to be selected
		if ((jCheckBoxSingletonPages.isSelected() || jCheckBoxParrellelPages.isSelected() || jCheckBoxCenceptionPages.isSelected())
				&& (jCheckBoxActivePage.isSelected() || jCheckBoxArchivedPages.isSelected() || jCheckBoxCompletedPages.isSelected() || jCheckBoxSignedOffPages
						.isSelected()))
			doSearching();
		else
			JOptionPane.showMessageDialog(null,
					"Please ensure you have selected at least one Page Type and at least one Page Status Filter");
	}

	public void doSearching() {
		SwingWorker worker = new SwingWorker() {
			public Object construct() {
				Object objLatch = new Object();
				try {
					SearchQueryParams params = buildQuerySearch();
					
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					jButtonSearchReactionPages.setEnabled(false);
					
					try {
						byte[] chemistry = params.getQueryChemistry();
						
						String structure = "";
						if (!ArrayUtils.isEmpty(chemistry))
							structure = new String(chemistry);
						
						boolean searchInProducts = StringUtils.equals(params.getCssCenDbSearchType(), "Products");
						
						if (structure.contains("$RXN") && searchInProducts) {
							JOptionPane.showMessageDialog(Parallel_Query_SearchContainer.this, "Can not search reaction in products!");
						} else {
							notebookPages = new ArrayList<String>();
							
							if (StringUtils.isBlank(structure)) {
								notebookPages.addAll(ServiceController.getStorageDelegate().searchReactionInfo(params));
							} else if (!structure.contains("$RXN") && !searchInProducts) {
								String struct = convertMolToRxn(structure, true);
								params.setQueryChemistry(struct.getBytes());
								notebookPages.addAll(ServiceController.getStorageDelegate().searchReactionInfo(params));
								
								struct = convertMolToRxn(structure, false);
								params.setQueryChemistry(struct.getBytes());
								notebookPages.addAll(ServiceController.getStorageDelegate().searchReactionInfo(params));
							} else {
								notebookPages.addAll(ServiceController.getStorageDelegate().searchReactionInfo(params));
							}
							
							setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							jButtonSearchReactionPages.setEnabled(true);
							
							if (notebookPages.size() > 50) {
								int okoption = JOptionPane.showConfirmDialog(Parallel_Query_SearchContainer.this, "This search will return more than 50 results. Continue?", "Search Results", JOptionPane.OK_CANCEL_OPTION);
								if (okoption == JOptionPane.OK_OPTION)
									displayNotebookPages(notebookPages);
							} else {
								displayNotebookPages(notebookPages);
							}
						}
					} catch (SearchTooMuchDataException e3) {
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						jButtonSearchReactionPages.setEnabled(true);
						JOptionPane.showMessageDialog(Parallel_Query_SearchContainer.this, "Over 1000 results found, please refine your search criteria.");
					} catch (StorageException e1) {
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						jButtonSearchReactionPages.setEnabled(true);
						CeNErrorHandler.getInstance().logExceptionMsg(Parallel_Query_SearchContainer.this, "Search exception occured:\n\n" + e1.getMessage(), e1);
					}
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(Parallel_Query_SearchContainer.this, "Search exception occured:\n\n" + e.getMessage(), e);
				} finally {
					synchronized (objLatch) {
						objLatch.notify();
					}
				}
				
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				jButtonSearchReactionPages.setEnabled(true);
				
				return new Object();
			}
		};
		
		worker.start();
	}


	private String convertMolToRxn(String mol, boolean isReactant) throws ChemUtilAccessException, ChemUtilInitException {
		List<byte[]> molList = Arrays.asList(new byte[][] { mol.getBytes() });

		ReactionProperties rxnProp = new ReactionProperties();
		ChemistryDelegate delegate = new ChemistryDelegate();
		
		if (isReactant)
			rxnProp.Reactants = molList;
		else
			rxnProp.Products = molList;
		
		ReactionProperties rp = delegate.combineReactionComponents(rxnProp);
		
		return new String(delegate.convertChemistry(rp.Reaction, "", "MDL Rxnfile"));
	}
	
	private SearchQueryParams buildQuerySearch() {
		// build query search vo
		SearchQueryParams params = new SearchQueryParams();
		params.setOwner(MasterController.getUser().getNTUserID());
		// build SPID
		if (jCheckBoxSpidNum.isSelected())
			params.setSpid(spidNum.getText());
		if (jCheckBoxRequestId.isSelected()) {
			params.setRequestId(requestId.getText());
		}
		// build rection structure search params
		if (jCheckBoxOtherReq.isSelected()) {
			// set upstructure data
			try {
				String struSearchType = (String) (jComboBoxOtherSearchReq.getSelectedItem());
				params.setStructureSearchType(struSearchType);
				if(!struSearchType.equals(this.NONE_VALUE)){
					byte[] drawnChemist = structQueryCanvas.getChemistry();
					if (drawnChemist != null) {
						
						ChemistryDelegate chemDel = new ChemistryDelegate();
						byte[] molBytes = chemDel.convertChemistry(structQueryCanvas.getChemistry(), "", "MDL Molfile");
						params.setQueryChemistry(molBytes);
						// build search Reactions or Products
						params.setCssCenDbSearchType((String) searchForCombo.getSelectedItem());
						
						if (struSearchType.equals(this.SIMILARITY)) {
							String similarityPerc = StructureSimilarity.getText().trim();
							if (similarityPerc.equals("")) {
								similarityPerc = "95";
							}
							params.setStrucSimilarity(similarityPerc);
							params.setStrucSimilarityAlgorithm((String) jComboBoxSimilarityAlgol.getSelectedItem());
						}
					}
				}
				// if (chemDel.isReaction(structQueryCanvas.getChemistry())){
				// 		params.setDoingReactionSearch(true);
				// } else {
				// 		params.setDoingStructSearch(true);
				// }
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
		//
		// search for selected page types (Singleton, Parallel, Conception)
		List<String> allPageTypes = new ArrayList<String>(4);
		if (jCheckBoxSingletonPages.isSelected())
			allPageTypes.add(CeNConstants.PAGE_TYPE_MED_CHEM);
		if (jCheckBoxParrellelPages.isSelected())
			allPageTypes.add(CeNConstants.PAGE_TYPE_PARALLEL);
		if (jCheckBoxCenceptionPages.isSelected())
			allPageTypes.add(CeNConstants.PAGE_TYPE_CONCEPTION);
		params.setPageTypes(allPageTypes);
		// search page status
		List<String> pageStatusList = new ArrayList<String>();
		if (jCheckBoxCompletedPages.isSelected()) 
			pageStatusList.add("COMPLETE");
		
		if (jCheckBoxActivePage.isSelected()) 
			pageStatusList.add("OPEN");
		
		params.setPageStatus(pageStatusList);
		
		// search domain
		if (jRadioButtonMyNotebook.isSelected()) {
			params.setSearchAllSite(false);
			params.setSearchAllChemistsOnTheSite(false);
			// set user name
			List<String> nameList = new ArrayList<String>();
			nameList.add(MasterController.getUser().getNTUserID());
			params.setUserNameList(nameList);
			// set site code list
			List<String> codeList = new ArrayList<String>();
			codeList.add(MasterController.getUser().getSiteCode());
			params.setSiteCodeList(codeList);
		} else if (jRadioButtonNotebooksOfAllChem_sites.isSelected()) {
			params.setSearchAllSite(true);
		}
		// text field search
		// TA code
		if (jCheckBoxTherapeuticArea.isSelected()) {
			try {
				List<String> taList = new ArrayList<String>();
				taList.add(CodeTableCache.getCache().getTAsCode((String) jComboBoxTherapeticArea.getSelectedItem()));
				params.setTACodeList(taList);
			} catch (Exception e) { /* ignored */
			}
		}
		// project code
		if (jCheckBoxProjectCode.isSelected()) {
			try {
				List<String> projectList = new ArrayList<String>();
				projectList.add(getProjectCode());
				params.setProjectCodeList(projectList);
			} catch (Exception e) { /* ignored */
			}
		}
		// creation date
		if (jCheckBoxPageCreationDate.isSelected() && pageCreationDate.getSelectedItem() != null) {
			params.setPageCreationDate(pageCreationDate.getSelectedItem().toString().trim());
			// //System.out.println("The new date is: " +
			// params.getPageCreationDate());
			params.setDateOperator((String) (jComboBoxPageCreationFunction.getSelectedItem()));
		}
		// reaction yield
		if (jCheckBoxReactionYield.isSelected()) {
			params.setYieldOperator((String) (jComboBoxReactionYieldFunction.getSelectedItem()));
			params.setReactionYieldLowerValue(jTextFieldReactionYieldLowerLimit.getText().trim());
			if (params.getYieldOperator().equals(this.BETWEEN)) {
				params.setReactionYieldUpperValue(jTextFieldReactionUpperLimit.getText().trim());
			}
		}
		// purity
		if (jCheckBoxProductPurity.isSelected()) {
			params.setPurityOperator((String) (jComboBoxProductPurityFunction.getSelectedItem()));
			params.setProductPurityLowerValue(jTextFieldLowerPurityLimit.getText().trim());
			if (params.getPurityOperator().equals(this.BETWEEN)) {
				params.setProductPurityUpperValue(jTextFieldUpperPurityLimit.getText().trim());
			}
		}
		// subject title
		if (jCheckBoxSubjectTitle.isSelected()) {
			params.setSubjectTitle(subjectTitle.getText().trim());
			params.setSubjectTitleOperator((String)(jComboBoxSubjectTitleFunction.getSelectedItem()));			
		}
		// literature reference
		if (jCheckBoxLitRef.isSelected()) {
			params.setLitReference(litRef.getText().trim());
			params.setLitReferenceOperator((String)(jComboBoxLitRefFunction.getSelectedItem()));			
		}
		// reaction procedure
		if (jCheckBoxReactionProc.isSelected()) {
			params.setReactionProcedure(reactionProc.getText().trim());
			params.setReactionProcedureOperator((String)(jComboBoxReactionProcFunction.getSelectedItem()));			
		}
		// compound ID
		if (jCheckBoxCompIDs.isSelected()) {
			params.setCompoundID(compIDs.getText().trim());
			params.setCompoundIDOperator((String)(jComboBoxCompIDsFunction.getSelectedItem()));
		}
		// chemical name
		if (jCheckBoxChemNames.isSelected()) {
			params.setChemicalName(chemNames.getText().trim());
			params.setChemicalNameOperator((String)(jComboBoxChemNamesFunction.getSelectedItem()));
			// System.out.println("Chemical Name before formatting: " + chemNames.getText().trim());
		}
		// list of Users Names
		if (theseUsers != null) {
			String users = theseUsers.getText().trim();
			if(!users.equals("")){
				StringTokenizer singleUsers = new StringTokenizer(users, ",");
				List<String> listOfUsers = new ArrayList<String>(singleUsers.countTokens());
				while (singleUsers.hasMoreTokens()) {
					String userName = (String) singleUsers.nextElement();
					if (!userName.equalsIgnoreCase(""))
						listOfUsers.add(userName);
				} // end while
				params.setUserNameList(listOfUsers);
			}
		}
		return params;
	}

	private String getProjectCode() {
		String result = "";
		String item = (String) jComboBoxProjectCode.getSelectedItem();
		if (jComboBoxProjectCode.getSelectedIndex() != 0 && item != null) {
			try {
				result = item.substring(0, item.indexOf(" -"));
				if (result == null)
					result = "";
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
		return result;
	}

	/** Auto-generated event handler method */
	/** Auto-generated event handler method */
	protected void jButtonCancelSynTreeActionPerformed(ActionEvent evt) {
		setVisible(false);
		this.dispose();
	}

	/** Auto-generated event handler method */
	/** Auto-generated event handler method */
	protected void jComboBoxOtherSearchReqItemStateChanged(ItemEvent evt) {
		if (jComboBoxOtherSearchReq.getSelectedItem().equals(this.SIMILARITY)) {
			StructureSimilarity.setVisible(true);
			StructureSimilarity.setText("95");
//			jComboBoxSimilarityAlgol.setVisible(true);//temporary commented due to functionality of similarity algorithm hasn't been implemented yet 
			jLabelStrucSimilarity.setVisible(true);
		} else {
			StructureSimilarity.setVisible(false);
			jComboBoxSimilarityAlgol.setVisible(false);
			jLabelStrucSimilarity.setVisible(false);
		}
	}

	/** Auto-generated event handler method */
	protected void jComboBoxReactionYieldFunctionItemStateChanged(ItemEvent evt) {
		if (((String) jComboBoxReactionYieldFunction.getSelectedItem()).equals(this.BETWEEN)) {
			jTextFieldReactionUpperLimit.setVisible(true);
		} else {
			jTextFieldReactionUpperLimit.setVisible(false);
		}
	}

	/** Auto-generated event handler method */
	protected void jComboBoxProductPurityFunctionItemStateChanged(ItemEvent evt) {
		if (jComboBoxProductPurityFunction.getSelectedItem().equals(this.BETWEEN)) {
			jTextFieldUpperPurityLimit.setVisible(true);
		} else {
			jTextFieldUpperPurityLimit.setVisible(false);
		}
	}

	private void addFormComponent(DefaultFormBuilder formBuilder, JComponent comp, int col, int row, int colSpan, int rowSpan) {
		// set location
		formBuilder.setRow(row);
		formBuilder.setColumn(col);
		formBuilder.setRowSpan(rowSpan);
		formBuilder.setColumnSpan(colSpan);
		formBuilder.add(comp);
	}

	private List buildReactionPageInfo(List notebookPagesRight) {
		List notebookPages = notebookPagesRight;
		NotebookPageModel page;
		List result = null;
		if (notebookPages == null)
			throw new IllegalArgumentException("List of NotebookPages is null in buildReactionPageInfo");
		// System.out.println("Incomming Results
		// size:"+notebookPagesRight.size());
		result = new ArrayList(notebookPages.size());
		for (int i = 0; i < notebookPages.size(); i++) {
			page = (NotebookPageModel) notebookPages.get(i);
			ReactionPageInfo smallNotebookPage = new ReactionPageInfo();
			smallNotebookPage.setVersion(new Integer(page.getVersion()).toString());
			smallNotebookPage.setNoteBookExperiment(page.getNbRef().getNotebookRef());
			smallNotebookPage.setUsername(page.getUserName());
			smallNotebookPage.setSitecode(page.getSiteCode());
			List steps = page.getReactionSteps();
			ReactionStepModel sumStep = page.getSummaryReactionStep();
			if (steps != null && steps.size() > 0) {
				smallNotebookPage.setReactionSketch(sumStep.getRxnScheme().getNativeSketch());
				// System.out.println("Sketch size :"+
				// ((ReactionStepModel)steps.get(0)).getRxnScheme().getNativeSketch().length
				// );
			}
			// System.out.println(page.getVersion()+" "+page.getUserNTID()+"
			// "+page.getSiteCode());
			result.add(smallNotebookPage);
		} // end for
		// System.out.println("Results size:"+result.size());
		return result;
	}

	// PersonFinder implemetation
	public void findPersonNTID(String name) {
		if (StringUtils.isNotBlank(theseUsers.getText())) {
			theseUsers.setText(theseUsers.getText() + "," + name);
		} else {
			theseUsers.setText(name);
		}
	}

	// set defaults when Less Options button clicked
	private void setDefaults() {
		jCheckBoxSubjectTitle.setSelected(false);
		jCheckBoxLitRef.setSelected(false);
		jCheckBoxReactionProc.setSelected(false);
		jCheckBoxCompIDs.setSelected(false);
		jCheckBoxSpidNum.setSelected(false);
		jCheckBoxRequestId.setSelected(false);
		jCheckBoxChemNames.setSelected(false);
		jCheckBoxSingletonPages.setSelected(true);
		jCheckBoxParrellelPages.setSelected(MasterController.isParallelViewEnabled());
		jCheckBoxCenceptionPages.setSelected(true);
		jCheckBoxActivePage.setSelected(true);
		jCheckBoxCompletedPages.setSelected(true);
		jCheckBoxSignedOffPages.setSelected(true);
		jCheckBoxArchivedPages.setSelected(true);
		jRadioButtonMyNotebook.setSelected(true);
	}

	private void displayNotebookPages(final List notebookPages) {
		final JFrame owner = this;
		if (notebookPages != null) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					List smallNotebooks = null;
					if (notebookPages.size() > 0) {
						// smallNotebooks =
						// buildReactionPageInfo(notebookPages);
						ReactionPagesSearchResultJDialog reactionPagesSearchResultJDialog = new ReactionPagesSearchResultJDialog(
								Parallel_Query_SearchContainer.this);
						reactionPagesSearchResultJDialog.displaySearchResults(notebookPages);
						reactionPagesSearchResultJDialog.setVisible(true);
					} else {
						JOptionPane.showMessageDialog(Parallel_Query_SearchContainer.this,
								"No results found, please refine your search criteria.");
					}
				}
			});
		} else {
			JOptionPane.showMessageDialog(owner, "No Results found", "Search Results", JOptionPane.PLAIN_MESSAGE);
		}
	}

	public void setPersonFullName(String name) {
		// TODO Auto-generated method stub
		
	}

	public void setPersonList(List persons) {
		// TODO Auto-generated method stub
		
	}

	private void addListener(final JTextField textField, final JCheckBox checkBox) {
		textField.addKeyListener(new KeyAdapter() {
		    public void keyTyped(KeyEvent e) {
		    	char code = e.getKeyChar();
		    	if ((textField.getText().length() == 0 && Character.isLetterOrDigit(code)) || textField.getText().length() > 0) {
		    		checkBox.setSelected(true);
		    	} else {
		    		checkBox.setSelected(false);
		    	}
		    }				
		});
	}

}