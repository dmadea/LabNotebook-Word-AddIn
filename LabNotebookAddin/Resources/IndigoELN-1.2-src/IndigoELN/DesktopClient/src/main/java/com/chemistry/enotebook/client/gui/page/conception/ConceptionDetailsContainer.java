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
package com.chemistry.enotebook.client.gui.page.conception;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.MultiPeoplePicker;
import com.chemistry.enotebook.client.gui.PersonFinder;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.gui.page.ConceptionNotebookPageGUI;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.utils.CodeTableUtils;
import com.chemistry.enotebook.utils.CommonUtils;
import com.common.chemistry.codetable.CodeTableCache;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder,
 * which is free for non-commercial use. If Jigloo is being used commercially
 * (ie, by a for-profit company or business) then you should purchase a license -
 * please visit www.cloudgarden.com for details.
 */
public class ConceptionDetailsContainer extends javax.swing.JPanel implements PersonFinder {
	
	private static final long serialVersionUID = 3815355959790482871L;
	
	// public static final Color BACKGROUND_COLOR = new Color(189, 236, 214);
	public static final Font LABEL_FONT = new java.awt.Font("sansserif", 1, 11);
	public static final Font COMBOBOX_FONT = new java.awt.Font("Tahoma", 0, 12);
	public static final int TXT_WIDTH = 20;
	public static final int SEC_COLUMN = 2;
	public static final int FORTH_COLUMN = 4;
	private JTextArea jTextAreaLitRef = new JTextArea();
	private JScrollPane jScrollPaneLitRef;
	private JLabel jLabelLitRef = new JLabel("    Concept Keywords: ");

	private JPanel jPanelLitRef;
	private JLabel designersLabel = new JLabel("Designers:");
	// private JTextArea nameConceptorsTextArea = new JTextArea();
	private JTextArea designersTextArea = new JTextArea();;// 13done
	private JButton personPickerBtn = new JButton();

	private JTextField jTextFieldCreationDate = new JTextField();

	private JLabel jLabelCreationDate = new JLabel("Creation Date:");
	private JPanel jPanelLeft;
	private CeNComboBox jComboBoxProject = new CeNComboBox();
	private JLabel jLabelProject = new JLabel("Project Code & Name:");
	private CeNComboBox jComboBoxTa = new CeNComboBox();

	private JLabel jLabelTa = new JLabel("Therapeutic Area:");
	private JTextField jTextFieldSubject = new JTextField();
	private JLabel jLabelSubject = new JLabel("Concept Subject / Title:");
	private JPanel jPanelRight;
	private NotebookPageModel pageModel;
	private boolean _dataLoading = false;
	private boolean _editable = false;
	private List<String> personList = new ArrayList<String>();
		
	
	private static final Log log = LogFactory.getLog(MasterController.class);
	private static final CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	// public ConceptionDetailsContainer() {
	// }

	public ConceptionDetailsContainer(NotebookPageModel pageModel) {
		this.pageModel = pageModel;
		initGUI();
		setPageModel(pageModel);
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will
	 * disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			// This will be set in the personFinder creation.
			this.designersTextArea.setEditable(false);
			designersTextArea.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent evt) {
					inventorsTAFocusLost();
				}
			});

			jPanelLeft = new JPanel();
			jPanelRight = new JPanel();
			jPanelLitRef = new JPanel();

			jScrollPaneLitRef = new JScrollPane();

			if (pageModel != null)
				jTextFieldCreationDate.setText(pageModel.getCreationDate());

			jTextAreaLitRef.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent evt) {
					jTextAreaLitRefFocusLost();
				}
			});
			BorderLayout borderLayout = new BorderLayout();
			this.setLayout(borderLayout);
			this.setBorder(new MatteBorder(new Insets(1, 1, 1, 1), new java.awt.Color(0, 0, 0)));
			this.setName("");
			jTextFieldCreationDate.setColumns(TXT_WIDTH);

			jLabelCreationDate.setFont(LABEL_FONT);
			jTextFieldCreationDate.setEnabled(false);
			jTextFieldCreationDate.setBackground(new java.awt.Color(255, 255,
					255));

			designersLabel.setVisible(true);
			designersLabel.setFont(LABEL_FONT);
			jLabelSubject.setFont(LABEL_FONT);

			jTextFieldSubject.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent evt) {
					jTextFieldSubjectFocusLost();
				}
			});

			jLabelTa.setFont(LABEL_FONT);
			jComboBoxTa.setVisible(true);
			jComboBoxTa.setUnknownItemsAllowed(true);
			jComboBoxTa.setFont(COMBOBOX_FONT);
			jComboBoxTa.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jComboBoxTaActionPerformed(evt);
				}
			});

			jLabelProject.setVisible(true);
			jLabelProject.setFont(LABEL_FONT);
			jComboBoxProject.setUnknownItemsAllowed(true);
			jComboBoxProject.setFont(COMBOBOX_FONT);
			jComboBoxProject.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jComboBoxProjectActionPerformed(evt);
				}
			});
			
			AutoCompleteDecorator.decorate(jComboBoxTa);
			AutoCompleteDecorator.decorate(jComboBoxProject);
			
			BorderLayout bLayout = new BorderLayout();
			jPanelLitRef.setLayout(bLayout);
			// jPanelLitRef.setBackground(BACKGROUND_COLOR);
			jPanelLitRef
					.setBackground(ConceptionNotebookPageGUI.BACKGROUND_COLOR);
			jPanelLitRef.setPreferredSize(new java.awt.Dimension(500, 60));

			jLabelLitRef.setFont(LABEL_FONT);
			jPanelLitRef.add(jLabelLitRef, BorderLayout.WEST);
			jTextAreaLitRef.setLineWrap(true);
			jTextAreaLitRef.setVisible(true);
			jTextAreaLitRef.setBorder(new BevelBorder(BevelBorder.LOWERED,
					null, null, null, null));
			jPanelLitRef.add(jTextAreaLitRef, BorderLayout.EAST);
			jScrollPaneLitRef.setVisible(true);
			jScrollPaneLitRef.setPreferredSize(new java.awt.Dimension(400, 48));
			jScrollPaneLitRef
					.setBounds(new java.awt.Rectangle(100, 7, 450, 48));
			jPanelLitRef.add(jScrollPaneLitRef);
			
			jPanelLitRef.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					Component c = e.getComponent();
					// //System.out.println("componentResized W " +
					// c.getSize().width + ", H " + c.getSize().height);
					jScrollPaneLitRef.setSize(c.getSize().width
							- jLabelLitRef.getWidth() + 35, jScrollPaneLitRef
							.getHeight());
				}
			});			
			
			buildConceptionDetailsPane();

			postInitGUI();
		} catch (Exception e) {
			e.printStackTrace();
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	private void buildConceptionDetailsPane() {
		FormLayout leftPanelLayout;
		FormLayout rightPanelLayout;
		
			leftPanelLayout = new FormLayout(
					"5dlu, pref, 20dlu, fill:pref:grow, 5dlu ",// columns
					"5dlu, pref, 3dlu, pref, 5dlu, pref, 5dlu, pref, 10dlu, pref, 2dlu"); // rows
			rightPanelLayout = new FormLayout(
					"5dlu, pref, 5dlu, pref:grow, 5dlu",// columns
					"7dlu, pref, 11dlu, top:pref:grow, 11dlu"); // rows 1
				
		jPanelLeft.setLayout(leftPanelLayout); 
		// add labels and text field to the left panel
		CellConstraints cc = new CellConstraints();
		jPanelLeft.add(jLabelSubject, cc.xywh(2, 2, 3, 1));
		jPanelLeft.add(jTextFieldSubject, cc.xywh(2, 4, 3, 1));
		jPanelLeft.add(jLabelTa, cc.xy(2, 6));
		jPanelLeft.add(jComboBoxTa, cc.xy(4, 6));
		jPanelLeft.add(jLabelProject, cc.xy(2, 8));
		jPanelLeft.add(jComboBoxProject, cc.xy(4, 8));

		jPanelLeft.setVisible(true);
		jPanelLeft.setBackground(ConceptionNotebookPageGUI.BACKGROUND_COLOR);
		jPanelLeft.setBorder(new BevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		jPanelLeft.setMinimumSize(new Dimension(100, 100));

		// set right panel
    jPanelRight.removeAll();  // remove all components that have been added previously so that panel renders correctly after initial experiment load		
		jPanelRight.setLayout(rightPanelLayout); 
		CellConstraints ccRight = new CellConstraints();
		jPanelRight.add(jLabelCreationDate, ccRight.xy(2, 2));
		jPanelRight.add(jTextFieldCreationDate, ccRight.xy(4, 2));
		jPanelRight.add(designersLabel, ccRight.xy(2, 4));
		jPanelRight.add(getInventorsPanel(), ccRight.xywh(4, 4, 1, 1));

		jPanelRight.setVisible(true);
		jPanelRight.setBackground(ConceptionNotebookPageGUI.BACKGROUND_COLOR);
		jPanelRight.setBorder(new BevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		jPanelRight.setMinimumSize(new Dimension(100, 100));
		
		// add all the panels to the Main panel
//		FormLayout topLayout = new FormLayout(
//				"pref:grow(0.65), pref:grow(0.35)", "pref, pref");
//		PanelBuilder topBuilder = new PanelBuilder(topLayout); // , new
//																// FormDebugPanel());
//		topBuilder.add(jPanelLeft, cc.xy(1, 1));
//		topBuilder.add(jPanelRight, cc.xy(2, 1));
//		topBuilder.add(jPanelLitRef, cc.xywh(1, 2, 2, 1));
//		this.add(topBuilder.getPanel());
		this.add(jPanelLeft, BorderLayout.CENTER);
		this.add(jPanelRight, BorderLayout.EAST);
		this.add(jPanelLitRef, BorderLayout.SOUTH);
	}
	
	
	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		jScrollPaneLitRef
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPaneLitRef
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		jScrollPaneLitRef.getViewport().add(jTextAreaLitRef);
		jTextAreaLitRef.setWrapStyleWord(true);
		jTextAreaLitRef.addKeyListener((new KeyAdapter() {
			public void keyReleased(KeyEvent evt) {
				ConceptionDetailsContainer.this.enableSaveButton();
			}
		}));
		jTextFieldSubject.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent evt) {
				ConceptionDetailsContainer.this.enableSaveButton();
			}
		});
		setDataLoading(true);
		CodeTableUtils.fillComboBoxWithTAs(jComboBoxTa);
		// Initially entire project list is displayed since no TA has been
		// selected
		CodeTableUtils.fillComboBoxWithProjects(jComboBoxProject, null);
		// jComboBoxTa.setSelectedIndex(5);
		// jComboBoxProject.setSelectedIndex(5);
		setDataLoading(false);
	}

	/**
	 * This static method creates a new instance of this class and shows it
	 * inside a new JFrame, (unless it is already a JFrame).
	 * 
	 * It is a convenience method for showing the GUI, but it can be copied and
	 * used as a basis for your own code. * It is auto-generated code - the body
	 * of this method will be re-generated after any changes are made to the
	 * GUI. However, if you delete this method it will not be re-created.
	 */
	public static void showGUI() {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			ConceptionDetailsContainer inst = new ConceptionDetailsContainer(
					null);
			frame.setContentPane(inst);
			frame.getContentPane().setSize(400, 400);// inst.getSize());
			frame
					.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public static void main(String[] args) {
//		showGUI();
//	}

	// used by refresh
	public void setPageModel(NotebookPageModel model) {
		pageModel = model;

		setDataLoading(true);
		if (pageModel != null) {
			setSubject(pageModel.getSubject());
			setLiteratureRef(pageModel.getConceptionKeyWords());
			setTaCode(pageModel.getTaCode());
			setProjectCode(pageModel.getProjectCode());
			if (getProjectCode() == null || getProjectCode().equals("")) {
				// pageModel.setLoading(false);
				pageModel.setProjectCode("");
				// pageModel.setLoading(true);
			}
			setCreationDate(pageModel.getCreationDate());
			String namesStr = (pageModel.getPageHeader().getConceptorNames() == null ? ""
					: pageModel.getPageHeader().getConceptorNames());
			String[] names = namesStr.split(";");
			for(String name : names) {
				if(CommonUtils.isNotNull(name)) {
					this.personList.add(name);
				}
			}
			this.designersTextArea.setText(namesStr);
			// setContFromRxn(pageModel.getContinuedFromRxn());
			// setContToRxn(nbPage.getContinuedToRxn());
			// setProjectAlias(nbPage.getProjectAlias());
		} else {
			setSubject("");
			setLiteratureRef("");
			setTaCode("");
			setProjectCode("");
			setCreationDate("");
			// this.inventorsTextArea.setText("");
			// setContFromRxn("");
			// setContToRxn("");
			// setProjectAlias("");
		}

		setDataLoading(false);
		setEditableFlag();
	}

	private void setEditableFlag() {
		if (pageModel != null) {

			_editable = pageModel.isEditable();

		} else
			_editable = false;
		// jTextFieldReactionDate.setEditable(_editable);
		// nameConceptorsTextArea.setEditable(_editable);
		// nameConceptorLabel.setEditable(_editable);
		jTextAreaLitRef.setEditable(_editable);
		jTextFieldSubject.setEditable(_editable);
		// jTextFieldProjectAliasName.setEditable(_editable);
		jComboBoxTa.setEnabled(_editable);
		jComboBoxTa.setFocusable(_editable);
		jComboBoxProject.setEnabled(_editable);
		jComboBoxProject.setFocusable(_editable);
		designersTextArea.setEnabled(_editable);
		personPickerBtn.setEnabled(_editable);
	}

	private boolean isDataLoading() {
		return _dataLoading;
	}

	private void setDataLoading(boolean flag) {
		_dataLoading = flag;
	}

	public String getSubject() {
		return jTextFieldSubject.getText();
	}

	public void setSubject(String subject) {
		jTextFieldSubject.setText(subject);
	}

	public String getLiteratureRef() {
		return jTextAreaLitRef.getText();
	}

	public void setLiteratureRef(String litRef) {
		jTextAreaLitRef.setText(litRef);
		jTextAreaLitRef.setCaretPosition(0);
	}

	public String getCreationDate() {
		return jTextFieldCreationDate.getText();
	}

	public void setCreationDate(String rxnDate) {
		jTextFieldCreationDate.setText(rxnDate);
		jTextFieldCreationDate.setCaretPosition(0);
	}

	protected void jTextFieldSubjectFocusLost() {
		if (!isDataLoading()) {

			if (!getSubject().equals(pageModel.getSubject())) {
				pageModel.setSubject(getSubject());
				pageModel.setModelChanged(true);
				this.enableSaveButton();
			}
		}
	}

	/** Auto-generated event handler method */
	protected void jComboBoxTaActionPerformed(ActionEvent evt) {

		if (!isDataLoading()) {
			if (!getTaCode().equals(pageModel.getTaCode())) {
				pageModel.setTaCode(getTaCode());
				// need to update project list to display only projects
				// for this TA
				CodeTableUtils.fillComboBoxWithProjects(jComboBoxProject,
						pageModel.getTaCode());
				try {
					MasterController.getUser().setPreference(
							NotebookUser.PREF_LastTA, getTaCode());
					pageModel.setModelChanged(true);
					this.enableSaveButton();
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
				}
			}
		} else if (pageModel != null && pageModel.getTaCode() != null) {
			// need to populate the project drop down with the correct
			// project codes for the TA
			CodeTableUtils.fillComboBoxWithProjects(jComboBoxProject, pageModel
					.getTaCode());
			setProjectCode(getProjectCode());
		}

	}

	/** Auto-generated event handler method */
	protected void jComboBoxProjectActionPerformed(ActionEvent evt) {

		if (!isDataLoading()) {
			if (!getProjectCode().equals(pageModel.getProjectCode())) {
				pageModel.setProjectCode(getProjectCode());
				try {
					MasterController.getUser().setPreference(
							NotebookUser.PREF_LastProject, getProjectCode());
					pageModel.setModelChanged(true);
					this.enableSaveButton();
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
				}
			}
		}

	}

	protected void inventorsTAFocusLost() {
		if (!isDataLoading()) {
			pageModel.getPageHeader().setConceptorNames(
					designersTextArea.getText());
			this.enableSaveButton();

		}
	}

	protected void jTextAreaLitRefFocusLost() {
		if (!isDataLoading())
			if (!getLiteratureRef().equals(pageModel.getLiteratureRef())) {
				pageModel.setConceptionKeyWords(getLiteratureRef());
				pageModel.setModelChanged(true);
				this.enableSaveButton();
			}

	}

	private void enableSaveButton() {
		log.debug("enableSaveButton called ConceptionDetailsContainer");

		MasterController.getGUIComponent().enableSaveButtons();
	}

	public String getTaCode() {
		String result = "";
		if (jComboBoxTa.getSelectedIndex() != 0) {
			try {

				result = CodeTableCache.getCache().getTAsCode(
						(String) jComboBoxTa.getSelectedItem());

				if (result == null) {
					result = (String) jComboBoxTa.getSelectedItem();

				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
		return result;
	}

	// to set combo box value of TA from model
	private void setTaCode(String taCode) {
		if (taCode != null && taCode.length() > 0) {
			try {

				String descr = CodeTableCache.getCache().getTAsDescription(
						taCode);

				if (descr == null) {
					if (taCode.length() > 0) {

						jComboBoxTa.setSelectedItem(taCode);
					} else {

						jComboBoxTa.setSelectedIndex(0);
					}
				} else {

					jComboBoxTa.setSelectedItem(descr);
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		} else {

			jComboBoxTa.setSelectedIndex(0);
		}
	}

	// this is to get code from combo selection to update the value in model
	public String getProjectCode() {
		String result = "";
		String item = (String) jComboBoxProject.getSelectedItem();

		if (jComboBoxProject.getSelectedIndex() != 0 && item != null) {
			try {
				int pos = item.indexOf(" -");
				if (pos >= 0) {
					result = item.substring(0, pos);
				} else {
					result = item;
				}
				if (result == null) {

					result = "";
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}

		return result;
	}

	// set selected project code as selection, assumeing that the combo is
	// filled
	private void setProjectCode(String projCode) {

		if (projCode != null && projCode.length() > 0) {
			try {

				String descr = CodeTableCache.getCache()
						.getProjectsDescription(projCode);

				if (descr == null) {
					// System.out.println("Debug: descr == null");
					if (projCode.length() > 0)
						jComboBoxProject.setSelectedItem(projCode);
					else {
						jComboBoxProject.setSelectedIndex(0);
					}
				} else {

					jComboBoxProject.setSelectedItem(projCode + " - " + descr);
				}

			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		} else {// empty Ptoject code, select the first one
			jComboBoxProject.setSelectedIndex(0);
		}

	}

	private JPanel getInventorsPanel() {
		FormLayout layout = new FormLayout("pref:grow, 5dlu, pref",
				"pref, 15dlu, 15dlu");
		PanelBuilder builder = new PanelBuilder(layout); // , new
															// FormDebugPanel());
		CellConstraints cc = new CellConstraints();
		// text area
		JScrollPane inventorsListSP = new JScrollPane();
		inventorsListSP
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		inventorsListSP
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		inventorsListSP.setViewportView(designersTextArea);
		builder.add(inventorsListSP, cc.xywh(1, 1, 1, 3));
		// button
		if (personPickerBtn.getActionListeners().length > 0) { // remove any action listener previously added, otherwise multiple invocations of dialog will result
		  personPickerBtn.removeActionListener(personPickerBtn.getActionListeners()[0]);
		}
		personPickerBtn.setBackground(CeNConstants.BACKGROUND_COLOR);
		ImageIcon personFace = CenIconFactory
				.getImageIcon("images/clipart_ face.jpg");
		personPickerBtn.setIcon(personFace);
		personPickerBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MultiPeoplePicker picker = new MultiPeoplePicker(ConceptionDetailsContainer.this, personList);
				picker.setVisible(true); // picker.show();
			}
		});
		builder.add(personPickerBtn, cc.xy(3, 1));
		builder.getPanel().setBackground(CeNConstants.BACKGROUND_COLOR);
		builder.getPanel().setFont(new Font("Sansserif", 1, 11));
		return builder.getPanel();
	}

	public void findPersonNTID(String name) {
		designersTextArea.setText(name);

	}

	public void setPersonFullName(String name) {
		StringBuffer buff = new StringBuffer();
		buff.append(designersTextArea.getText());
		buff.append(name + "\n");
		designersTextArea.setText(buff.toString());

	}

	public void setPersonList(List<String> persons) {
		this.personList = persons;
		StringBuffer buff = new StringBuffer();
		for (String person : persons) {
			buff.append(person);
			buff.append("\n");
		}
		designersTextArea.setText(buff.toString());
		designersTextArea.setCaretPosition(0);
		// For storage in DB replace new line with semicolon
		buff = new StringBuffer();
		boolean first = true;
		for (String person : persons) {
			if (!first) {
				buff.append("; ");
			}
			buff.append(person);
			if (first)
				first = false;
		}
		this.pageModel.setConceptorNames(buff.toString());
		this.pageModel.setModelChanged(true);
		this.enableSaveButton();
	}

}
