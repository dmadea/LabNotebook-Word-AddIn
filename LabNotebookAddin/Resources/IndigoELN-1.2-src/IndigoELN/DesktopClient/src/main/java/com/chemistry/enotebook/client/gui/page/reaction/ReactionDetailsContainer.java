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
package com.chemistry.enotebook.client.gui.page.reaction;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.PeoplePicker;
import com.chemistry.enotebook.client.gui.PersonFinder;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.common.utils.CeNLabel;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.utils.CodeTableUtils;
import com.common.chemistry.codetable.CodeTableCache;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class ReactionDetailsContainer extends javax.swing.JPanel implements PersonFinder {
	
	private static final long serialVersionUID = 4605435432393241740L;
	
	//public static final Color BACKGROUND_COLOR = new Color(189, 236, 214);
	public static final Font LABEL_FONT = new java.awt.Font("sansserif", 1, 11);
	public static final Font COMBOBOX_FONT = new java.awt.Font("Tahoma", 0, 12);
	public static final int TXT_WIDTH = 20;
	public static final int SEC_COLUMN = 2;
	public static final int FORTH_COLUMN = 4;

	private JLabel jLabelnoDesignProducts = new JLabel("No. of Products in Design:");
	private JLabel jLabelReactionDate = new CeNLabel("Creation Date:");
	private JLabel jLabelContFromRxn = new CeNLabel("Cont. FROM Rxn:");
	private JLabel jLabelContToRxn = new CeNLabel("Cont. TO Rxn:");
	private JLabel jLabelLitRef = new CeNLabel("     Literature Ref.:");
	private JLabel jLabelProject = new CeNLabel("Project Code & Name:");
	private JLabel jLabelProjectAliasName = new CeNLabel("Project Alias Name:");
	private JLabel jLabelTa = new CeNLabel("Therapeutic Area:");
	private JLabel jLabelSubject = new CeNLabel("Experiment Subject / Title:");
	JLabel newBatchOwnerLabel = new JLabel("Batch Owner:");

	private JTextArea jTextAreaLitRef = new JTextArea();// 8
	private JScrollPane jScrollPaneLitRef;

	private JPanel jPanelLitRef;

	private JTextField jTextFieldReactionDate = new JTextField();
	private JTextField jTextFieldContFromRxn = new JTextField(); // done
	private JTextField jTextFieldContToRxn = new JTextField(); // done
	private JTextField jTextFieldProjectAliasName = new JTextField();// 9 done
	private JTextField jTextFieldSubject = new JTextField();// 1 to be done

	private JPanel jPanelLeft;
	private CeNComboBox jComboBoxProject = new CeNComboBox();// 2 done
	private CeNComboBox jComboBoxTa = new CeNComboBox();// 3 done

	private JPanel jPanelRight;
	// private Object pageModel;
	private boolean _dataLoading = false;
	// private boolean _editable = true;
	// private NotebookPage page;
	private NotebookPageModel pageModel;
	// private Person user;
	// Design Service fields
	private JTextField spidtf;// 5
	private JTextField sitetf;// 6
	private JTextField protocolIdtf;// 4 done
	private JTextField seriesIdtf;// 11
	private JTextField descriptiontf;//
	private JTextField vrxnIdtf;// 10
	private JTextField summaryPlanId;//
	private JTextField designSubmittertf;// 12
	private JTextField batchCreatortf = new JTextField();// 7 done
	private JTextField batchOwnertf = new JTextField();;// 13done
	private JButton personPickerBtn = new JButton();
	private JButton loadfromExpBtn = new JButton();
	private JButton loadtoExpBtn = new JButton();
	private JPanel personPanel;

	// Design Service fields
	private JLabel spidLbl = new CeNLabel("SPID #:");
	private JLabel siteLbl = new CeNLabel("Design Site:");
	private JLabel protocolIdLbl = new CeNLabel("Protocol ID:");
	private JLabel seriesIdLbl = new CeNLabel("Series ID:");
	private JLabel vrxnIdLbl = new CeNLabel("VRXN ID:");
	private JLabel designSubmitterLbl = new CeNLabel("Design Submitter:");
	private JLabel batchCreatorLabel = new CeNLabel("Batch Creator:");

	private boolean _editable = false;
	private static final Log log = LogFactory.getLog(MasterController.class);
	
	/*
	 * public ReactionDetailsContainer(NotebookPage page) { this.page = page; pageModel = page.getPageModelPojo(); initGUI(); }
	 */
	public ReactionDetailsContainer(NotebookPageModel pageModelPojo) {
		this.pageModel = pageModelPojo;
		initGUI();
		this.refreshPageModel(pageModel);
	}
	

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			JPanel noDesignProductsPanel = new JPanel();
						
			if (pageModel.getPageType().equalsIgnoreCase(CeNConstants.PAGE_TYPE_PARALLEL)) {
				List<ProductBatchModel> list = pageModel.getAllProductBatchModelsInThisPage(CeNConstants.PRODUCTS_DESIGN_DSP);
				String noDesignProducts = list.size() + "";
				jLabelnoDesignProducts.setText(jLabelnoDesignProducts.getText() +
												" " + noDesignProducts);
				jLabelnoDesignProducts.setFont(jLabelSubject.getFont());
				jLabelnoDesignProducts.setOpaque(false);
				noDesignProductsPanel.setOpaque(false);
				noDesignProductsPanel.setLayout(new BorderLayout());
				noDesignProductsPanel.add(jLabelnoDesignProducts, BorderLayout.LINE_END);
			}
			jPanelLeft = new JPanel();
			jPanelRight = new JPanel();
			jPanelLitRef = new JPanel();
			loadfromExpBtn.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.OPEN_PAGE));
			loadtoExpBtn.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.OPEN_PAGE));
			jTextFieldContFromRxn.setDocument(new JTextFieldLimit(15));
			jTextFieldContToRxn.setDocument(new JTextFieldLimit(15));
			jTextFieldSubject.setText(pageModel.getSubject());
			jTextFieldProjectAliasName.setText(pageModel.getProjectAlias());
			batchOwnertf.setText(MasterController.getGuiController().getUsersFullName(pageModel.getBatchOwner()));
			batchOwnertf.setEditable(false);  // vb 11/8 can only change via the people finder
			batchCreatortf.setText(MasterController.getGuiController().getUsersFullName(pageModel.getBatchCreator()));
			batchCreatortf.setEditable(false);
			jTextAreaLitRef.setDocument(new JTextAreaLimit(1000));
			jTextAreaLitRef.setText(pageModel.getLiteratureRef());
			
			jScrollPaneLitRef = new JScrollPane();
			// if(pageModel instanceof DesignSynthesisPlan ) {
			// jTextFieldReactionDate.setText(DateFormat.getDateInstance(DateFormat.FULL).format(pageModel.getCreationDate()));
			jTextFieldReactionDate.setText(pageModel.getCreationDate());
			if (pageModel.getPageType().equalsIgnoreCase(CeNConstants.PAGE_TYPE_PARALLEL)) {
				createDspFields();
			} else if (pageModel.getPageType().equalsIgnoreCase(CeNConstants.PAGE_TYPE_CONCEPTION)) {
				createConceptionRecordsFields();
			} else if (pageModel.getPageType().equalsIgnoreCase(CeNConstants.PAGE_TYPE_MED_CHEM)) {
				createMedChemFields();
			}
			BorderLayout borderLayout = new BorderLayout();
			this.setLayout(borderLayout);
			this.setBorder(new MatteBorder(new Insets(1, 1, 1, 1), new java.awt.Color(0, 0, 0)));
			this.setName("");
			jTextFieldProjectAliasName.setColumns(TXT_WIDTH);
			jTextFieldReactionDate.setColumns(TXT_WIDTH);
			jTextFieldContFromRxn.setColumns(TXT_WIDTH);
			jTextFieldContToRxn.setColumns(TXT_WIDTH);

			//jLabelReactionDate.setFont(LABEL_FONT);
			jTextFieldReactionDate.setEditable(false);
			jTextFieldReactionDate.setEnabled(false);
			jTextFieldReactionDate.setBackground(new java.awt.Color(255, 255, 255));
			jTextFieldContFromRxn.setEditable(true);
			jTextFieldContFromRxn.setVisible(true);

			jTextFieldContFromRxn.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent evt) {
					jTextFieldContFromRxnFocusLost();
				}
			});
			
			

			jLabelContFromRxn.setVisible(true);
			jTextFieldContToRxn.setEditable(true);
			jTextFieldContToRxn.setVisible(true);

			jTextFieldContToRxn.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent evt) {
					jTextFieldContToRxnFocusLost();
				}
			});

			jTextFieldProjectAliasName.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent evt) {
					jTextFieldProjectAliasNameFocusLost();
				}
			});

			jLabelProjectAliasName.setVisible(true);

			jTextFieldSubject.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent evt) {
					jTextFieldSubjectChanged();
				}
			});
			loadtoExpBtn.setEnabled(false);
			loadtoExpBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					String expNumber = jTextFieldContToRxn.getText();
					if (pageModel.getNbRef().getNbRef().equals(expNumber))
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Cont. to Rxn must be another note book.");
					else
						MasterController.getGuiController().openPCeNExperimentCombinedBkndPage(pageModel.getSiteCode(), expNumber);
				}
			});
			loadfromExpBtn.setEnabled(false);
			loadfromExpBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					String expNumber = jTextFieldContFromRxn.getText();
					if (pageModel.getNbRef().getNbRef().equals(expNumber))
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Cont. from Rxn must be another note book.");
					else
						MasterController.getGuiController().openPCeNExperimentCombinedBkndPage(pageModel.getSiteCode(), expNumber);
					
				}
			});	

			//jLabelTa.setFont(LABEL_FONT);
			jComboBoxTa.setVisible(true);
			jComboBoxTa.setUnknownItemsAllowed(true);
			jComboBoxTa.setFont(COMBOBOX_FONT);
						
			jLabelProject.setVisible(true);
			//jLabelProject.setFont(LABEL_FONT);
			jComboBoxProject.setUnknownItemsAllowed(true);
			jComboBoxProject.setFont(COMBOBOX_FONT);

			AutoCompleteDecorator.decorate(jComboBoxTa);
			AutoCompleteDecorator.decorate(jComboBoxProject);
			
			BorderLayout bLayout = new BorderLayout();
			jPanelLitRef.setLayout(bLayout);
			jPanelLitRef.setBackground(CeNConstants.BACKGROUND_COLOR);
			jPanelLitRef.setPreferredSize(new java.awt.Dimension(623, 60));

			//jLabelLitRef.setFont(LABEL_FONT);
			jPanelLitRef.add(jLabelLitRef, BorderLayout.WEST);
			jTextAreaLitRef.setLineWrap(true);
			jTextAreaLitRef.setVisible(true);
			jTextAreaLitRef.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			jPanelLitRef.add(jTextAreaLitRef, BorderLayout.EAST);
			jScrollPaneLitRef.setVisible(true);
			jScrollPaneLitRef.setPreferredSize(new java.awt.Dimension(521, 48));
			jScrollPaneLitRef.setBounds(new java.awt.Rectangle(100, 7, 550, 48));
			jPanelLitRef.add(jScrollPaneLitRef);
			jPanelLitRef.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					Component c = e.getComponent();
					// //System.out.println("componentResized W " +
					// c.getSize().width + ", H " + c.getSize().height);
					jScrollPaneLitRef.setSize(c.getSize().width - jLabelLitRef.getWidth() + 35, jScrollPaneLitRef.getHeight());
				}
			});
			FormLayout leftPanelLayout = new FormLayout("5dlu,pref, 20dlu, fill:pref:grow:right, 5dlu ", // columns
					"1dlu, 1dlu, pref, 3dlu, pref, 2dlu, pref, 2dlu, pref, 3dlu, pref, 2dlu, pref, 3dlu, pref, 3dlu, pref"); // rows
			// //rows
			// 1
			// add labels and text field to the left panel
			jPanelLeft.setLayout(leftPanelLayout);
			jPanelLeft.setVisible(true);
			jPanelLeft.setBackground(CeNConstants.BACKGROUND_COLOR);
			// jPanelLeft.setPreferredSize(new java.awt.Dimension(357,158));
			jPanelLeft.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			// jPanelLeft.setSize(new java.awt.Dimension(356,158));
			CellConstraints cc = new CellConstraints();
			int row = 3;
			int colSpan = 2;
			int rowSpan = 1;
			jPanelLeft.add(jLabelSubject, cc.xywh(SEC_COLUMN, row, colSpan, rowSpan));
			if (pageModel.getPageType().equalsIgnoreCase(CeNConstants.PAGE_TYPE_PARALLEL)) {
				jPanelLeft.add(noDesignProductsPanel, cc.xywh(FORTH_COLUMN, row, 1, rowSpan));
			}
			colSpan = 3;
			rowSpan = 1;
			row += 2;
			jPanelLeft.add(jTextFieldSubject, cc.xywh(SEC_COLUMN, row, colSpan, rowSpan));
			row += 2;
			jPanelLeft.add(jLabelTa, cc.xy(SEC_COLUMN, row));
			jPanelLeft.add(jComboBoxTa, cc.xy(FORTH_COLUMN, row));
			row += 2;
			jPanelLeft.add(jLabelProject, cc.xy(SEC_COLUMN, row));
			jPanelLeft.add(jComboBoxProject, cc.xy(FORTH_COLUMN, row));
			// if(pageModel instanceof DesignSynthesisPlan) {
			if (pageModel.getPageType().equalsIgnoreCase(CeNConstants.PAGE_TYPE_PARALLEL)) {
				row += 2;
				addDspFields(jPanelLeft, cc, row, "Left");
				row += 4;
			}
			row += 2;
			jPanelLeft.add(batchCreatorLabel, cc.xy(SEC_COLUMN, row));
			jPanelLeft.add(batchCreatortf, cc.xyw(FORTH_COLUMN, row,2));
			
			// set right panel
			FormLayout rightPanelLayout = new FormLayout("5dlu, pref, 5dlu, pref, 35dlu, 5dlu",// columns
					"pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref"); // rows
			// 1
			jPanelRight.setLayout(rightPanelLayout);
			jPanelRight.setVisible(true);
			jPanelRight.setBackground(CeNConstants.BACKGROUND_COLOR);
			jPanelRight.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			row = 1;
			jPanelRight.add(jLabelReactionDate, cc.xy(SEC_COLUMN, row));
			jPanelRight.add(jTextFieldReactionDate, cc.xyw(FORTH_COLUMN, row,2));
			
			row += 2;
			jPanelRight.add(jLabelContFromRxn, cc.xy(SEC_COLUMN, row));
			jPanelRight.add(jTextFieldContFromRxn, cc.xy(FORTH_COLUMN, row));
			jPanelRight.add(loadfromExpBtn, cc.xy(5, row));	//5th_COLUMN
			row += 2;
			jPanelRight.add(jLabelContToRxn, cc.xy(SEC_COLUMN, row));
			jPanelRight.add(jTextFieldContToRxn, cc.xy(FORTH_COLUMN, row));
			jPanelRight.add(loadtoExpBtn, cc.xy(5, row));	//5th_COLUMN
			row += 2;
			jPanelRight.add(jLabelProjectAliasName, cc.xy(SEC_COLUMN, row));
			jPanelRight.add(jTextFieldProjectAliasName, cc.xyw(FORTH_COLUMN, row,2));
			// if(pageModel instanceof DesignSynthesisPlan) {
			if (pageModel.getPageType().equalsIgnoreCase(CeNConstants.PAGE_TYPE_PARALLEL)) {
				row += 2;
				addDspFields(jPanelRight, cc, row, "Right");
				row += 4;
			}
			row += 2;
			jPanelRight.add(newBatchOwnerLabel, cc.xy(SEC_COLUMN, row));
			jPanelRight.add(personPanel, cc.xywh(FORTH_COLUMN, row,2,1));
			
			// add all the panels to the Main panel
			this.add(jPanelLeft, BorderLayout.CENTER);
			this.add(jPanelRight, BorderLayout.EAST);
			this.add(jPanelLitRef, BorderLayout.SOUTH);
			jLabelContFromRxn.setToolTipText("Continued from reaction experiment.");
			jLabelContToRxn.setToolTipText("Continued to reaction experiment.");
			jTextFieldContFromRxn.setToolTipText("Continued from reaction experiment.");
			jTextFieldContToRxn.setToolTipText("Continued to reaction experiment.");
			jTextFieldContFromRxn.getDocument().addDocumentListener(new ContinuedRxnEntryListener(loadfromExpBtn));
			jTextFieldContToRxn.getDocument().addDocumentListener(new ContinuedRxnEntryListener(loadtoExpBtn));

			postInitGUI();  //  
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}


	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
        jTextFieldContFromRxn.setText(pageModel.getContinuedFromRxn());
        jTextFieldContToRxn.setText(pageModel.getContinuedToRxn());

		jScrollPaneLitRef.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPaneLitRef.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		jScrollPaneLitRef.getViewport().add(jTextAreaLitRef);
		jTextAreaLitRef.setWrapStyleWord(true);
		jTextAreaLitRef.addKeyListener((new KeyAdapter() {
			public void keyReleased(KeyEvent evt) {
				jTextAreaLitRefChanged();
			}
		}));
		setDataLoading(true);
		CodeTableUtils.fillComboBoxWithTAs(jComboBoxTa);
		setTaCode(pageModel.getTaCode());
		jComboBoxTa.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jComboBoxTaActionPerformed();
			}
		});
		// Initially entire project list is displayed since no TA has been
		// selected
		CodeTableUtils.fillComboBoxWithProjects(jComboBoxProject, pageModel.getTaCode());
		setProjectCode(pageModel.getProjectCode());
		jComboBoxProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jComboBoxProjectActionPerformed();
			}
		});
		
		setDataLoading(false);
	}

//	/**
//	 * This static method creates a new instance of this class and shows it inside a new JFrame, (unless it is already a JFrame).
//	 * 
//	 * It is a convenience method for showing the GUI, but it can be copied and used as a basis for your own code. * It is
//	 * auto-generated code - the body of this method will be re-generated after any changes are made to the GUI. However, if you
//	 * delete this method it will not be re-created.
//	 */
//	public static void showGUI() {
//		try {
//			/*
//			 * javax.swing.JFrame frame = new javax.swing.JFrame();
//			 * 
//			 * ReactionDetailsContainer inst = new
//			 * ReactionDetailsContainer(ServiceController.getStorageService().createParallelExperiment("spid_id", "USER1", new
//			 * SessionIdentifier("","","",true))); frame.setContentPane(inst);
//			 * frame.getContentPane().setSize(400,400);//inst.getSize());
//			 * frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE); frame.pack(); frame.setVisible(true);
//			 */
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	public static void main(String[] args) {
//		showGUI();
//	}

	/**
	 * Used by outside class to set the model when loading a new page
	 * 
	 * @param model
	 */
	//used by refresh
	public void refreshPageModel(NotebookPageModel model) {
		if (model != null )
		{
			if (this.pageModel != model)
			{
				this.pageModel = model;
				setDataLoading(true);
			 
				setSubject(pageModel.getSubject());
				setLiteratureRef(pageModel.getLiteratureRef());
				setTaCode(pageModel.getTaCode());
	
				String peojectCodeInModel = pageModel.getProjectCode();
				//System.out.println("peojectCodeInModel" + peojectCodeInModel);
				setProjectCode(peojectCodeInModel);
	
				/*
				 * if (getProjectCode() == null || getProjectCode().equals("")) { // pageModel.setLoading(false);
				 * pageModel.setProjectCode(""); // pageModel.setLoading(true); }
				 */
				setReactionDate(pageModel.getCreationDate());
				setContFromRxn(pageModel.getContinuedFromRxn());
				setContToRxn(pageModel.getContinuedToRxn());
				setProjectAlias(pageModel.getProjectAlias());
			}
		} else {
			setSubject("");
			setLiteratureRef("");
			setTaCode("");
			setProjectCode("");
			setReactionDate("");
			setContFromRxn("");
			setContToRxn("");
			setProjectAlias("");
		}
		setDataLoading(false);
		setEditableFlag();
	}
	
	public void setEditableFlag() {
		if (pageModel != null) {
			_editable = pageModel.isEditable();
		} else
			_editable = false;
		jTextFieldContFromRxn.setEditable(_editable);
		jTextFieldContToRxn.setEditable(_editable);
		jTextAreaLitRef.setEditable(_editable);
		jTextFieldSubject.setEditable(_editable);
		jTextFieldProjectAliasName.setEditable(_editable);
		
		jComboBoxTa.setEnabled(_editable);
		jComboBoxTa.setFocusable(_editable);
		jComboBoxProject.setEnabled(_editable);
		jComboBoxProject.setFocusable(_editable);
		jComboBoxTa.setEnabled(_editable);
		jComboBoxTa.setFocusable(_editable);
		jComboBoxProject.setEnabled(_editable);
		jComboBoxProject.setFocusable(_editable);
		
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

	// just return the combox
	public String getTaCode() {
		String result = "";
		if (jComboBoxTa.getSelectedIndex() != 0) {
			try {

				long startTime = System.currentTimeMillis();
				result = CodeTableCache.getCache().getTAsCode((String) jComboBoxTa.getSelectedItem());
				long endTime = System.currentTimeMillis();
				log.info(this.getClass().getName() + " getCache.getTAsCode took " + (endTime - startTime) + " ms");

				if (result == null) {
					result = (String) jComboBoxTa.getSelectedItem();
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
		return result;
	}

	//to set combo box value of TA from model
	private void setTaCode(String taCode) {				
		if (taCode != null && taCode.length() > 0) {
			try {
				long startTime = System.currentTimeMillis();
				String descr = CodeTableCache.getCache().getTAsDescription(taCode);
				long endTime = System.currentTimeMillis();
				log.info(this.getClass().getName() + " getCache.getTAsDescription took " + (endTime - startTime) + " ms");
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

	/**
	 * Used when loading
	 * 
	 * @param projCode
	 */
	// set selected project code as selection, assumeing that the combo is filled
	private void setProjectCode(String projCode) {

		if (projCode != null && projCode.length() > 0) {
			try {
				long startTime = System.currentTimeMillis();
				String descr = CodeTableCache.getCache().getProjectsDescription(projCode);
				long endTime = System.currentTimeMillis();
				if (log.isInfoEnabled()) 
					log.info(this.getClass().getName() + " getCache.getProjectsDescription took " + (endTime - startTime) + " ms");

				//CeNErrorHandler.getInstance().logErrorMsgWithoutDisplay(this.getClass().getName() + " getCache.getProjectsDescription took " + (endTime - startTime) + " ms", "Timer");
				if (descr == null) {
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

	public String getReactionDate() {
		return jTextFieldReactionDate.getText();
	}

	public void setReactionDate(String rxnDate) {
		jTextFieldReactionDate.setText(rxnDate);
		jTextFieldReactionDate.setCaretPosition(0);
	}

	public String getContFromRxn() {
		return jTextFieldContFromRxn.getText();
	}

	public void setContFromRxn(String rxnType) {
		jTextFieldContFromRxn.setText(rxnType);
	}

	public String getContToRxn() {
		return jTextFieldContToRxn.getText();
	}

	public void setContToRxn(String rxnType) {
		jTextFieldContToRxn.setText(rxnType);
	}

	public String getProjectAlias() {
		return jTextFieldProjectAliasName.getText();
	}

	public void setProjectAlias(String alias) {
		jTextFieldProjectAliasName.setText(alias);
	}

	/** Auto-generated event handler method */
	protected void jTextFieldSubjectChanged() {
		if (!isDataLoading()) {
			if (!getSubject().equals(pageModel.getSubject())) {
				pageModel.setSubject(getSubject());
				enableSaveButtonReactionDetailsContainer("jTextFieldSubjectFocusLost");
			}
		}
	}

	protected void protocolIdtfChanged() {
		if (!isDataLoading()) {

			pageModel.setProtocolID(protocolIdtf.getText());
			enableSaveButtonReactionDetailsContainer("protocolIdtfFocusLost");

		}
	}

	protected void batchOwnertfChanged() {
		if (!isDataLoading()) {
			pageModel.setBatchOwner(batchOwnertf.getText());
			enableSaveButtonReactionDetailsContainer("batchOwnertfFocusLost");

		}
	}

	protected void batchCreatortfChanged() {
		if (!isDataLoading()) {
			pageModel.setBatchCreator(batchCreatortf.getText());
			enableSaveButtonReactionDetailsContainer("batchCreatortfFocusLost");

		}
	}

	/** Auto-generated event handler method */
	protected void jTextAreaLitRefChanged() {
		if (!isDataLoading())
			
			if (!getLiteratureRef().equals(pageModel.getLiteratureRef())) {
				pageModel.setLiteratureRef(getLiteratureRef());
				this.enableSaveButtonReactionDetailsContainer("jTextAreaLitRefFocusLost");
			}
	}

	/** Auto-generated event handler method */
	protected void jComboBoxTaActionPerformed() {
		if (!isDataLoading()) {
			if (!getTaCode().equals(pageModel.getTaCode())) {
				pageModel.setTaCode(getTaCode());
				enableSaveButtonReactionDetailsContainer("jComboBoxTaActionPerformed1");
				// need to update project list to display only projects for this
				// TA
				CodeTableUtils.fillComboBoxWithProjects(jComboBoxProject, pageModel.getTaCode());
				try {
					MasterController.getUser().setPreference(NotebookUser.PREF_LastTA, getTaCode());
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
				}
			}
		} else if (pageModel != null && pageModel.getTaCode() != null) {
			// need to populate the project drop down with the correct
			// project codes for the TA
			CodeTableUtils.fillComboBoxWithProjects(jComboBoxProject, pageModel.getTaCode());
			setProjectCode("");// to reset the project code
			enableSaveButtonReactionDetailsContainer("jComboBoxTaActionPerformed2");
		}
	}

	/** Auto-generated event handler method */
	protected void jComboBoxProjectActionPerformed() {
		if (!isDataLoading()) {
			if (!getProjectCode().equals(pageModel.getProjectCode())) {

				String selectedProjectCode = getProjectCode();

				pageModel.setProjectCode(selectedProjectCode);
				enableSaveButtonReactionDetailsContainer("jComboBoxProjectActionPerformed");
				try {
					MasterController.getUser().setPreference(NotebookUser.PREF_LastProject, getProjectCode());
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
				}
			}
		}
	}

	/** Auto-generated event handler method */
	protected void jTextFieldContFromRxnFocusLost() {
		if (!isDataLoading())
			if (!getContFromRxn().equals(pageModel.getContinuedFromRxn())) {
				pageModel.setContinuedFromRxn(getContFromRxn());
				this.enableSaveButtonReactionDetailsContainer("jTextFieldContFromRxnFocusLost");

			}
	}

	/** Auto-generated event handler method */
	protected void jTextFieldContToRxnFocusLost() {
		if (!isDataLoading())
			if (!getContToRxn().equals(pageModel.getContinuedToRxn())) {
				pageModel.setContinuedToRxn(getContToRxn());
				this.enableSaveButtonReactionDetailsContainer("jTextFieldContToRxnFocusLost");
			}
	}

	/** Auto-generated event handler method */
	protected void jTextFieldProjectAliasNameFocusLost() {
		if (!isDataLoading())
			if (!getProjectAlias().equals(pageModel.getProjectAlias())) {
				pageModel.setProjectAlias(getProjectAlias());
				this.enableSaveButtonReactionDetailsContainer("jTextFieldProjectAliasNameFocusLost");
			}
	}

	protected void vrxnIdtfFocusLost() {
		if (!isDataLoading()) {

			pageModel.setVrxnId(vrxnIdtf.getText());
			this.enableSaveButtonReactionDetailsContainer("vrxnIdtfFocusLost");
		}

	}

	protected void designSubmittertfFocusLost() {
		if (!isDataLoading()) {

			pageModel.setDesignSubmitter(designSubmittertf.getText());
			this.enableSaveButtonReactionDetailsContainer("designSubmittertfFocusLost");
		}

	}

	protected void summaryPlanIdFocusLost() {
		if (!isDataLoading()) {

			pageModel.setSummaryPlanId(summaryPlanId.getText());
			this.enableSaveButtonReactionDetailsContainer("seriesIdtfFocusLost");
		}

	}

	protected void seriesIdtfFocusLost() {
		if (!isDataLoading()) {

			pageModel.setSeriesID(seriesIdtf.getText());
			this.enableSaveButtonReactionDetailsContainer("seriesIdtfFocusLost");
		}

	}

	protected void designSitetfFocusLost() {
		if (!isDataLoading()) {
			pageModel.setDesignSite(sitetf.getText());
			this.enableSaveButtonReactionDetailsContainer("designSitetfFocusLost");
		}
	}

	/**
	 * 
	 * 
	 */
	private void enableSaveButtonReactionDetailsContainer(String debug) {
		 //System.out.println("enableSaveButton called ReactionDetailsContainer "+debug);
		pageModel.setModelChanged(true);
		MasterController.getGUIComponent().enableSaveButtons();
	}

	/*
	 * TODO jino
	 * 
	 */
	private void createConceptionRecordsFields() {
	}

	/*
	 * TODO jino
	 * 
	 */
	private void createDspFields() {
		// this function is called within if statement that check for objects
		// class type
		// we are safe to cast without worries
		// DesignSynthesisPlan dSPlan = .getSynthesisPlan();
		// create Labels

		// set lbl fonts
		spidLbl.setFont(LABEL_FONT);
		siteLbl.setFont(LABEL_FONT);
		// create TextFields
		spidtf = new JTextField(pageModel.getSPID());
		spidtf.setEditable(false); // vb 7/25 bug 11711
		
		String protocolID = pageModel.getProtocolID();
		if (protocolID != null && !protocolID.equals("null"))
			protocolIdtf = new JTextField(protocolID);
		else
			protocolIdtf = new JTextField();
		protocolIdtf.setEditable(false);
		
		String seriesID = pageModel.getSeriesID();
		if (seriesID != null && !seriesID.equals("null"))
			seriesIdtf = new JTextField(seriesID);
		else
			seriesIdtf = new JTextField();
		seriesIdtf.setEditable(false);
		
		String designSite = pageModel.getDesignSite();
		if (designSite != null && !designSite.equals("null"))
			sitetf = new JTextField(designSite);
		else
			sitetf = new JTextField();
		sitetf.setEditable(false);
		descriptiontf = new JTextField(pageModel.getDSPDescription());
		String vrxnId = pageModel.getVrxnId();
		if (vrxnId != null && !vrxnId.equals("null"))
			vrxnIdtf = new JTextField(vrxnId);
		else
			vrxnIdtf = new JTextField();
		vrxnIdtf.setEditable(false);
		summaryPlanId = new JTextField(pageModel.getSummaryPlanId());
		String designSubmitterName = MasterController.getGuiController().getUsersFullName(pageModel.getDesignSubmitter().toUpperCase());
		if (designSubmitterName == null || designSubmitterName.length() == 0) designSubmitterName = pageModel.getDesignSubmitter();
		designSubmittertf = new JTextField(designSubmitterName);
		designSubmittertf.setEditable(false);
		protocolIdtf.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				protocolIdtfChanged();
			}
		});

		batchOwnertf.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				batchOwnertfChanged();
			}
		});
		batchCreatortf.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				batchCreatortfChanged();
			}
		});
		designSubmittertf.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				designSubmittertfFocusLost();
			}
		});
		vrxnIdtf.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				vrxnIdtfFocusLost();
			}
		});

		summaryPlanId.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				summaryPlanIdFocusLost();
			}
		});

		seriesIdtf.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				seriesIdtfFocusLost();
			}
		});
		sitetf.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				designSitetfFocusLost();
			}
		});
		
		personPickerBtn.setBackground(CeNConstants.BACKGROUND_COLOR);
		ImageIcon personFace = CenIconFactory.getImageIcon("images/clipart_ face.jpg");
		personPickerBtn.setIcon(personFace);
		personPickerBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				PeoplePicker picker = new PeoplePicker(MasterController.getGUIComponent(), ReactionDetailsContainer.this);
				picker.setVisible(true);
			}
		});

		
		spidtf.setColumns(TXT_WIDTH);
		protocolIdtf.setColumns(TXT_WIDTH);
		seriesIdtf.setColumns(TXT_WIDTH);
		sitetf.setColumns(TXT_WIDTH);
		descriptiontf.setColumns(TXT_WIDTH);
		vrxnIdtf.setColumns(TXT_WIDTH);
		summaryPlanId.setColumns(TXT_WIDTH);
		designSubmittertf.setColumns(TXT_WIDTH);
		batchCreatortf.setColumns(TXT_WIDTH);
		batchOwnertf.setColumns(TXT_WIDTH);
		// batchOwner.setBorder(BorderFactory.createLoweredBevelBorder());
		personPanel = new JPanel(new BorderLayout());
		personPanel.setPreferredSize(new Dimension((int) batchOwnertf.getPreferredSize().getWidth(), (int) (batchOwnertf
				.getPreferredSize().height - 1.0)));
		personPanel.setBackground(CeNConstants.BACKGROUND_COLOR);
		newBatchOwnerLabel.setFont(new Font("Sansserif", 1, 11));
		//personPanel.add(newBatchOwnerLabel, BorderLayout.WEST);
		personPanel.add(batchOwnertf, BorderLayout.CENTER);
		personPanel.add(personPickerBtn, BorderLayout.EAST);
	}

	private void createMedChemFields() {
		personPickerBtn.setBackground(CeNConstants.BACKGROUND_COLOR);
		ImageIcon personFace = CenIconFactory.getImageIcon("images/clipart_ face.jpg");
		personPickerBtn.setIcon(personFace);
		personPickerBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				PeoplePicker picker = new PeoplePicker(MasterController.getGUIComponent(), ReactionDetailsContainer.this);
				picker.setVisible(true);
			}
		});

		batchCreatortf.setColumns(TXT_WIDTH);
		batchOwnertf.setColumns(TXT_WIDTH);
		// batchOwner.setBorder(BorderFactory.createLoweredBevelBorder());
		personPanel = new JPanel(new BorderLayout());
		personPanel.setPreferredSize(new Dimension((int) batchOwnertf.getPreferredSize().getWidth(), (int) (batchOwnertf
				.getPreferredSize().height - 1.0)));
		personPanel.setBackground(CeNConstants.BACKGROUND_COLOR);
		newBatchOwnerLabel.setFont(new Font("Sansserif", 1, 11));
		//personPanel.add(newBatchOwnerLabel, BorderLayout.WEST);
		personPanel.add(batchOwnertf, BorderLayout.CENTER);
		personPanel.add(personPickerBtn, BorderLayout.EAST);
	}

	private void addDspFields(JComponent parent, CellConstraints cc, int row, String whichPanel) {
		if (whichPanel.equals("Left")) {
			parent.add(spidLbl, cc.xy(SEC_COLUMN, row));
			parent.add(spidtf, cc.xyw(FORTH_COLUMN, row,2));
			row += 2;
			parent.add(protocolIdLbl, cc.xy(SEC_COLUMN, row));
			parent.add(protocolIdtf, cc.xyw(FORTH_COLUMN, row,2));
			row += 2;
			parent.add(siteLbl, cc.xy(SEC_COLUMN, row));
			parent.add(sitetf, cc.xyw(FORTH_COLUMN, row,2));
		} else if (whichPanel.equals("Right")) {
			parent.add(vrxnIdLbl, cc.xy(SEC_COLUMN, row));
			parent.add(vrxnIdtf, cc.xyw(FORTH_COLUMN, row,2));
			row += 2;
			parent.add(seriesIdLbl, cc.xy(SEC_COLUMN, row));
			parent.add(seriesIdtf, cc.xyw(FORTH_COLUMN, row,2));
			row += 2;
			parent.add(designSubmitterLbl, cc.xy(SEC_COLUMN, row));
			parent.add(designSubmittertf, cc.xyw(FORTH_COLUMN, row,2));
		}
	}

	public void findPersonNTID(String name) {
		pageModel.setBatchOwner(name);
	}

	public void setPersonFullName(String name) {
		batchOwnertf.setText(name);
	}

	public void setPersonList(List<String> persons) {
		
	}
}

class ContinuedRxnEntryListener extends PlainDocument implements DocumentListener {
    
	private static final long serialVersionUID = -7878710867550439622L;
	
	JButton linkButton;
    
    ContinuedRxnEntryListener(JButton linkButton)
    {
    	this.linkButton = linkButton;
    }
    
 
    public void insertUpdate(DocumentEvent e) {
        enableButton(e);
    }
    public void removeUpdate(DocumentEvent e) {
    	enableButton(e);
    }
    public void changedUpdate(DocumentEvent e) {
        //Plain text components don't fire these events
    }

    public void enableButton(DocumentEvent e) {
      	linkButton.setEnabled(e.getDocument().getLength() > 0);
    }
}

class JTextFieldLimit extends PlainDocument {
    
	private static final long serialVersionUID = 5799584707683172343L;
	
	private int limit;
    // optional uppercase conversion
    private boolean toUppercase = false;
    
    JTextFieldLimit(int limit) {
        super();
        this.limit = limit;
    }
    
    JTextFieldLimit(int limit, boolean upper) {
        super();
        this.limit = limit;
        toUppercase = upper;
    }
    
    public void insertString
            (int offset, String  str, AttributeSet attr)
            throws BadLocationException {
        if (str == null) return;
        
        if ((getLength() + str.length()) <= limit) {
            if (toUppercase) str = str.toUpperCase();
            super.insertString(offset, str, attr);
        }
    }
}
class JTextAreaLimit extends PlainDocument {
    
	private static final long serialVersionUID = 6615843551710496200L;
	
	private int limit;
    // optional uppercase conversion
    private boolean toUppercase = false;
    
    JTextAreaLimit(int limit) {
        super();
        this.limit = limit;
    }
    
    JTextAreaLimit(int limit, boolean upper) {
        super();
        this.limit = limit;
        toUppercase = upper;
    }
    
    public void insertString
            (int offset, String  str, AttributeSet attr)
            throws BadLocationException {
        if (str == null) 
        	return;
        int choice = -1;
        if ((getLength() + str.length()) > limit) {
        	choice = JOptionPane.showConfirmDialog(MasterController.getGUIComponent(), "Literature Ref. must be less than 1000 characters long. Do you want to trim the input to 1000 chars and contine?", "Literature Ref. exceeds limit", JOptionPane.YES_NO_OPTION);
        	str = str.substring(0, Math.max(0, (limit - (getLength()) )));
        }
    	if (choice == JOptionPane.YES_OPTION || choice == -1)
    	{
    		if (toUppercase) str = str.toUpperCase();
    		super.insertString(offset, str, attr);
    	}
    }    
}