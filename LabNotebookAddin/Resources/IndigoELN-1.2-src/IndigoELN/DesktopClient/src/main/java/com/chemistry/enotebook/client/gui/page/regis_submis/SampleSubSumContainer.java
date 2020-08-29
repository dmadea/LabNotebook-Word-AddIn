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
import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.AmountCellEditor;
import com.chemistry.enotebook.client.gui.common.utils.AmountCellRenderer;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.page.SingletonNotebookPageGUI;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.common.units.UnitCache;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchSubmissionContainerInfo;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchSubmissionToBiologistTest;
import com.chemistry.enotebook.registration.ScreenSearchVO;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.SimpleJTable;
import info.clearthought.layout.TableLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class SampleSubSumContainer extends javax.swing.JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6737201544078744010L;
	private SimpleJTable jTableSampleBeingSenttoMatManage;
	private JScrollPane jScrollPane4;
	private SimpleJTable jTableSampleSentDirectToBioTest;
	private JScrollPane jScrollPane6;
	private SimpleJTable jTableSampleSentToBioTest;
	private JScrollPane jScrollPane5;
	private JRadioButton jRadioButtonStandardCuration;
	private JRadioButton jRadioButtonCuration;
	private JRadioButton jRadioButtonStandard;
	private JRadioButton jRadioButtonkNone;
	private JLabel jLabelCompoundProtection;
	private JPanel jPanelProtection;
	private JPanel jPanel5;
	private JLabel jLabel3;
	private JPanel jPanel4;
	private JLabel jLabel2;
	private JLabel jLabel1;
	private JPanel jPanel3;
	private JPanel jPanel1;
	private JScrollPane jScrollPane2;
	private SamplesInContainerTableModel samplesInContainerTableModel = new SamplesInContainerTableModel();
	private SampleToBiologyTableModel sampleToMMForBioTableModel = new SampleToBiologyTableModel();
	private DirectToBiologyTableModel sampleToBioForTestModel = new DirectToBiologyTableModel();
	private NotebookPageGuiInterface parentDialog;
	private ProductBatchModel selectedBatch;
	private JDialogLookupAddScreen jDialogLookupAddScreen;
//	private String siteName = "";
	private ArrayList unitList = new ArrayList();
	private int[] selectedContainers = null;
	private int[] selectedScreens = null;
	private JPopupMenu popUpMenuScreenMM;
	private JPopupMenu popUpMenuScreenSelf;
	private JToolBar jToolBarOptions;
	private JButton jButtonSubmitToMM;
	private JButton jButtonSubmitToScreen;

	public SampleSubSumContainer() {
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jScrollPane2 = new JScrollPane();
			jPanel1 = new JPanel();
			jLabel1 = new JLabel();
			jPanel3 = new JPanel();
			jScrollPane4 = new JScrollPane();
			jTableSampleBeingSenttoMatManage = new SimpleJTable();
			jPanelProtection = new JPanel();
			jLabelCompoundProtection = new JLabel();
			jRadioButtonkNone = new JRadioButton();
			jRadioButtonStandard = new JRadioButton();
			jRadioButtonCuration = new JRadioButton();
			jRadioButtonStandardCuration = new JRadioButton();
			jLabel2 = new JLabel();
			jPanel4 = new JPanel();
			jScrollPane5 = new JScrollPane();
			jTableSampleSentToBioTest = new SimpleJTable();
			jLabel3 = new JLabel();
			jPanel5 = new JPanel();
			jScrollPane6 = new JScrollPane();
			jTableSampleSentDirectToBioTest = new SimpleJTable();
			BorderLayout thisLayout = new BorderLayout();
			setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			setPreferredSize(new java.awt.Dimension(572, 518));
			jScrollPane2.setPreferredSize(new java.awt.Dimension(572, 333));
			add(jScrollPane2, BorderLayout.CENTER);
			BoxLayout jPanel1Layout = new BoxLayout(jPanel1, 1);
			jPanel1.setLayout(jPanel1Layout);
			jPanel1.setPreferredSize(new java.awt.Dimension(250, 385));
			jScrollPane2.add(jPanel1);
			jScrollPane2.setViewportView(jPanel1);
			jLabel1.setText("A. Samples in containers being sent to Materials Management");
			jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
			jLabel1.setHorizontalTextPosition(SwingConstants.LEFT);
			jLabel1.setVisible(true);
			jLabel1.setPreferredSize(new java.awt.Dimension(428, 16));
			jLabel1.setAlignmentX(2.0f);
			jLabel1.setAlignmentY(0.5f);
			jLabel1.setOpaque(true);
			jLabel1.setSize(new java.awt.Dimension(284, 16));
			jPanel1.add(jLabel1);
			GridLayout jPanel3Layout = new GridLayout(1, 2);
			jPanel3.setLayout(jPanel3Layout);
			jPanel3Layout.setRows(1);
			jPanel3Layout.setHgap(10);
			jPanel3Layout.setVgap(0);
			jPanel3Layout.setColumns(2);
			jPanel3.setPreferredSize(new java.awt.Dimension(282, 136));
			jPanel1.add(jPanel3);
			jPanel3.add(jScrollPane4);
			jScrollPane4.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					if (isContainerEditable())
						jScrollPane4MouseClicked(evt);
				}
			});
			jScrollPane4.add(jTableSampleBeingSenttoMatManage);
			jScrollPane4.setViewportView(jTableSampleBeingSenttoMatManage);
			jTableSampleBeingSenttoMatManage.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					if (isContainerEditable())
						jTableSampleBeingSenttoMatManageMouseClicked(evt);
				}
			});
			GridLayout jPanelProtectionLayout = new GridLayout(5, 1);
			jPanelProtection.setLayout(jPanelProtectionLayout);
			jPanelProtectionLayout.setRows(5);
			jPanelProtectionLayout.setHgap(0);
			jPanelProtectionLayout.setVgap(0);
			jPanelProtectionLayout.setColumns(1);
			jPanel3.add(jPanelProtection);
			jLabelCompoundProtection.setText("*Compound Protection");
			jLabelCompoundProtection.setPreferredSize(new java.awt.Dimension(279, 27));
			jPanelProtection.add(jLabelCompoundProtection);
			jRadioButtonkNone.setText("None (No approval needed)");
			jRadioButtonkNone.setPreferredSize(new java.awt.Dimension(279, 27));
			jPanelProtection.add(jRadioButtonkNone);
			jRadioButtonkNone.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					if (isContainerEditable())
						jRadioButtonkNoneActionPerformed(evt);
				}
			});
			jRadioButtonStandard.setText("Standard (Requests for any amount - 6 months)");
			jRadioButtonStandard.setPreferredSize(new java.awt.Dimension(279, 27));
			jPanelProtection.add(jRadioButtonStandard);
			jRadioButtonStandard.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					if (isContainerEditable())
						jRadioButtonStandardActionPerformed(evt);
				}
			});
			jRadioButtonCuration.setText("Curation (Requests for >10mg or 10% of remaining balance, never expires)");
			jPanelProtection.add(jRadioButtonCuration);
			jRadioButtonCuration.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					if (isContainerEditable())
						jRadioButtonCurationActionPerformed(evt);
				}
			});
			jRadioButtonStandardCuration.setText("Standard protection for 6 months, then curation protections.");
			jRadioButtonStandardCuration.setPreferredSize(new java.awt.Dimension(279, 27));
			jPanelProtection.add(jRadioButtonStandardCuration);
			jRadioButtonStandardCuration.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					if (isContainerEditable())
						jRadioButtonStandardCurationActionPerformed(evt);
				}
			});
			jLabel2.setText("B. Samples Sent By Materials Management for biological testing");
			jLabel2.setHorizontalTextPosition(SwingConstants.LEADING);
			jLabel2.setPreferredSize(new java.awt.Dimension(374, 18));
			jLabel2.setAlignmentX(1.0f);
			jPanel1.add(jLabel2);
			BorderLayout jPanel4Layout = new BorderLayout();
			jPanel4.setLayout(jPanel4Layout);
			jPanel4Layout.setHgap(0);
			jPanel4Layout.setVgap(0);
			jPanel4.setPreferredSize(new java.awt.Dimension(255, 121));
			jPanel1.add(jPanel4);
			jPanel4.add(jScrollPane5, BorderLayout.CENTER);
			jScrollPane5.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					if (isContainerEditable())
						jScrollPane5MouseClicked(evt);
				}
			});
			jScrollPane5.add(jTableSampleSentToBioTest);
			jScrollPane5.setViewportView(jTableSampleSentToBioTest);
			jTableSampleSentToBioTest.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					if (isContainerEditable())
						jTableSampleSentToBioTestMouseClicked(evt);
				}
			});
			jLabel3.setLayout(null);
			jLabel3.setText("C. Samples sent by myself directly for biological testing");
			jLabel3.setHorizontalTextPosition(SwingConstants.LEADING);
			jLabel3.setPreferredSize(new java.awt.Dimension(566, 18));
			jLabel3.setAlignmentX(1.0f);
			jLabel3.setAlignmentY(0.5f);
			jPanel1.add(jLabel3);
			BorderLayout jPanel5Layout = new BorderLayout();
			jPanel5.setLayout(jPanel5Layout);
			jPanel5Layout.setHgap(0);
			jPanel5Layout.setVgap(0);
			jPanel1.add(jPanel5);
			jScrollPane6.setPreferredSize(new java.awt.Dimension(569, 174));
			jPanel5.add(jScrollPane6, BorderLayout.CENTER);
			jScrollPane6.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					if (isContainerEditable())
						jScrollPane6MouseClicked(evt);
				}
			});
			jScrollPane6.add(jTableSampleSentDirectToBioTest);
			jScrollPane6.setViewportView(jTableSampleSentDirectToBioTest);
			jTableSampleSentDirectToBioTest.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					if (isContainerEditable())
						jTableSampleSentDirectToBioTestMouseClicked(evt);
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
		ButtonGroup group = new ButtonGroup();
		group.add(jRadioButtonStandardCuration);
		group.add(jRadioButtonCuration);
		group.add(jRadioButtonStandard);
		group.add(jRadioButtonkNone);
		jRadioButtonkNone.setSelected(true);
		// set up table models
		jTableSampleBeingSenttoMatManage.setModel(samplesInContainerTableModel);
		jTableSampleSentToBioTest.setModel(sampleToMMForBioTableModel);
		;
		jTableSampleSentDirectToBioTest.setModel(sampleToBioForTestModel);
		// get unit list
		setUnitList((ArrayList) UnitCache.getInstance().getUnitsOfTypeSorted(UnitType.MASS));
		double toolBarSize[][] = {
				{ 20, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP,
						CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN,
						CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN }, { 20, 2 } };
		jToolBarOptions = new JToolBar();
		TableLayout jToolBarLayout = new TableLayout(toolBarSize);
		jToolBarOptions.setLayout(jToolBarLayout);
		jToolBarOptions.setBorderPainted(false);
		jToolBarOptions.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
		add(jToolBarOptions, BorderLayout.NORTH);
		// Add buttons to toolbar
		jButtonSubmitToMM = new JButton();
		jButtonSubmitToMM.setText("Submit To MM");
		jButtonSubmitToMM.setToolTipText("Submit a Vial of Compound to Materials Managment");
		jButtonSubmitToMM.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
				"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, jButtonSubmitToMM);
		jToolBarOptions.add(jButtonSubmitToMM, "2,0");
		jButtonSubmitToMM.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addItemMouseClicked(null);
			}
		});
		jButtonSubmitToScreen = new JButton();
		jButtonSubmitToScreen.setText("Submit To Screening");
		jButtonSubmitToScreen.setToolTipText("Submit Compound to Screen(s)");
		jButtonSubmitToScreen.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
				"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, jButtonSubmitToScreen);
		jToolBarOptions.add(jButtonSubmitToScreen, "4,0");
		jButtonSubmitToScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				lookupAddScreenMouseClicked(null);
			}
		});
		TableColumn tCol = jTableSampleBeingSenttoMatManage.getColumnModel().getColumn(
				SamplesInContainerTableModel.AMOUNT_IN_CONTAINER_IDX);
		tCol.setCellEditor(new AmountCellEditor());
		tCol.setCellRenderer(new AmountCellRenderer());
		jTableSampleBeingSenttoMatManage.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		tCol = jTableSampleSentToBioTest.getColumnModel().getColumn(SampleToBiologyTableModel.AMOUNT_SENT_IDX);
		tCol.setCellEditor(new AmountCellEditor());
		tCol.setCellRenderer(new AmountCellRenderer());
		jTableSampleSentToBioTest.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		tCol = jTableSampleSentDirectToBioTest.getColumnModel().getColumn(DirectToBiologyTableModel.AMOUNT_SENT_IDX);
		tCol.setCellEditor(new AmountCellEditor());
		tCol.setCellRenderer(new AmountCellRenderer());
		jTableSampleSentDirectToBioTest.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
			javax.swing.JFrame frame = new javax.swing.JFrame();
			SampleSubSumContainer inst = new SampleSubSumContainer();
			frame.setContentPane(inst);
			frame.getContentPane().setSize(inst.getSize());
			frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateBatchList(ProductBatchModel pBatch) {
		((SingletonNotebookPageGUI)parentDialog).getRegSubSum_cont().updateBatchList(pBatch);
	}

	public void updateDisply() {
		updateContainerDisplay();
		updateBioTableModelAndDisplay();
		updateProtectionCode();
	}

	private void updateProtectionCode() {
		if (getSelectedBatch().getProtectionCode().equals("NONE")) {
			jRadioButtonkNone.setSelected(true);
		} else if (getSelectedBatch().getProtectionCode().equals("BS")) {
			jRadioButtonStandard.setSelected(true);
		} else if (getSelectedBatch().getProtectionCode().equals("BC")) {
			jRadioButtonCuration.setSelected(true);
		} else if (getSelectedBatch().getProtectionCode().equals("BSC")) {
			jRadioButtonStandardCuration.setSelected(true);
		} else {
			jRadioButtonkNone.setSelected(true);
			if (!getSelectedBatch().getProtectionCode().equals("NONE")) {
				getSelectedBatch().setProtectionCode("NONE");
			}
			((SingletonNotebookPageGUI)parentDialog).getRegSubSum_cont().updateBatchList(getSelectedBatch());
			
		}
		if (selectedBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_PASSED) 
				|| (selectedBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING))) {
			updateRadioButtons(false);
		} else {
			updateRadioButtons(isContainerEditable() && isSelectedBatchEditable());
		}
	}

	/**
	 * @return Returns the parentDialog.
	 */
	public NotebookPageGuiInterface getParentDialog() {
		return parentDialog;
	}

	/**
	 * @param parentDialog
	 *            The parentDialog to set.
	 */
	public void setParentDialog(NotebookPageGuiInterface parentDialog) {
		this.parentDialog = parentDialog;
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
		jButtonSubmitToMM.setEnabled(isSelectedBatchEditable());
		jButtonSubmitToScreen.setEnabled(isSelectedBatchEditable());
	}

	/** Auto-generated event handler method */
	protected void jTableSampleBeingSenttoMatManageMouseClicked(MouseEvent evt) {
		if (showEditableDialog() && evt.getButton() == MouseEvent.BUTTON3) {
			processjTableContainerRightClicked(evt);
		}
	}

	// private void updateSelectedContainers(MouseEvent evt) {
	// selectedContainers = getSelectedContainers();
	// updateBatchContainerStatus();
	// for (int i = 0; i< selectedContainers.length; i++) {
	// //System.out.println("The selected containers are: " +
	// selectedContainers[i]);
	// }
	// }
	/** Auto-generated event handler method */
	protected void jTableSampleSentToBioTestMouseClicked(MouseEvent evt) {
		if (showEditableDialog() && evt.getButton() == MouseEvent.BUTTON3) {
			processjTableSampleBioScreenRightClicked(evt);
		}
	}

	// private void updateSelectedScreens(MouseEvent evt) {
	// selectedScreens = getSelectedScreens();
	// updateBatchScreenStatus();
	//		
	// for (int i = 0; i< selectedScreens.length; i++) {
	// //System.out.println("The selected screens are: " +
	// selectedScreens[i]);
	// }
	// }
	/** Auto-generated event handler method */
	protected void jTableSampleSentDirectToBioTestMouseClicked(MouseEvent evt) {
		if (showEditableDialog() && evt.getButton() == MouseEvent.BUTTON3) {
			processjTableSampleBioTestRightClicked(evt);
		}
	}

	/** Auto-generated event handler method */
	protected void jScrollPane5MouseClicked(MouseEvent evt) {
		if (showEditableDialog() && evt.getButton() == MouseEvent.BUTTON3) {
			processjScrollPane5MouseClicked(evt);
		}
	}

	protected void processjScrollPane5MouseClicked(MouseEvent evt) {
		Point p = evt.getPoint();
		// construct the PopUpMenu
		JPopupMenu popUpMenu = new JPopupMenu();
		JMenuItem addItem = new JMenuItem("Lookup & Add Screens");
		// addItem.addActionListener()
		addItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aevt) {
				lookupAddScreenMouseClicked(aevt);
			}
		});
		popUpMenu.add(addItem);
		popUpMenu.show(jScrollPane5, p.x + 1, p.y - 6);
	}

	/** Auto-generated event handler method */
	protected void jScrollPane6MouseClicked(MouseEvent evt) {
		if (showEditableDialog() && evt.getButton() == MouseEvent.BUTTON3) {
			processjScrollPane6MouseClicked(evt);
		}
	}

	protected void processjScrollPane6MouseClicked(MouseEvent evt) {
		// construct the PopUpMenu
		JPopupMenu popUpMenu = new JPopupMenu();
		JMenuItem addItem = new JMenuItem("Lookup & Add Screens");
		addItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aevt) {
				lookupAddScreenMouseClicked(aevt);
			}
		});
		popUpMenu.add(addItem);
		Point p = evt.getPoint();
		popUpMenu.show(jScrollPane6, p.x + 1, p.y - 6);
	}

	/** Auto-generated event handler method */
	protected void jScrollPane4MouseClicked(MouseEvent evt) {
		if (showEditableDialog() && evt.getButton() == MouseEvent.BUTTON3) {
			processjScrollPane4MouseClicked(evt);
		}
	}

	protected void processjScrollPane4MouseClicked(MouseEvent evt) {
		// construct the PopUpMenu
		JPopupMenu popUpMenu = new JPopupMenu();
		JMenuItem addItem = new JMenuItem("Add SampleContainer");
		addItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aevt) {
				addItemMouseClicked(aevt);
			}
		});
		popUpMenu.add(addItem);
		Point p = evt.getPoint();
		popUpMenu.show(jScrollPane4, p.x + 1, p.y - 6);
	}

	protected void processjTableContainerRightClicked(MouseEvent evt) {
		// construct the PopUpMenu
		JPopupMenu popUpMenu = new JPopupMenu();
		JMenuItem addItem = new JMenuItem("Add SampleContainer");
		// addItem.addActionListener()
		addItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aevt) {
				addItemMouseClicked(aevt);
			}
		});
		popUpMenu.add(addItem);
		JMenuItem cutItem = new JMenuItem("Remove selected SampleContainer(s)");
		cutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aevt) {
				cutItemMouseClicked(aevt);
			}
		});
		popUpMenu.add(cutItem);
		JMenuItem clearItem = new JMenuItem("Remove All Containers");
		clearItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aevt) {
				clearItemMouseClicked(aevt);
			}
		});
		popUpMenu.add(clearItem);
		Point p = evt.getPoint();
		popUpMenu.show((JTable) evt.getSource(), p.x + 1, p.y - 6);
	}

	/** Auto-generated event handler method */
	protected void addItemMouseClicked(ActionEvent evt) {
		NotebookPageModel page = ((SingletonNotebookPageGUI)getParentDialog()).getPageModel();
//		try {
//			siteName = NotebookUser.getInstance().getPreference(NotebookUser.PREF_LastMMSiteCode);
//		} catch (UserPreferenceException e) {
//			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
//		}
//		if (!(siteName != null && siteName.length() > 0))
//			siteName = page.getSiteCode();
		EditSampleContainerJDialog editSampleContainerJDialog = new EditSampleContainerJDialog(MasterController.getGUIComponent(),
				this);
		editSampleContainerJDialog.setVisible(true);
	}

//	public void updateUserSite(String siteCode) {
//		try {
//			siteName = NotebookUser.getInstance().getPreference(NotebookUser.PREF_LastMMSiteCode);
//			if (!(siteName != null && siteName.length() > 0))
//				NotebookUser.getInstance().setPreference(NotebookUser.PREF_LastMMSiteCode, siteCode);
//			else if (!siteName.equals(siteCode))
//				NotebookUser.getInstance().setPreference(NotebookUser.PREF_LastMMSiteCode, siteCode);
//		} catch (UserPreferenceException e) {
//			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
//		}
//	}

	/**
	 * 
	 */
	public void updateContainerDisplay() {
		samplesInContainerTableModel.clearModelData();
		samplesInContainerTableModel.resetModelData(getSelectedBatch().getRegInfo().getSubmitContainerList());
		samplesInContainerTableModel.setReadOnly(!(getSelectedBatch().isUserAdded() && getSelectedBatch().isEditable()));
	}

	public void addContainerInfo(BatchSubmissionContainerInfo containerInfo) {
		//getSelectedBatch().getRegInfo().addSubmitContainer(containerInfo);
		updateBatchList(getSelectedBatch());
	}

	/** Auto-generated event handler method */
	protected void cutItemMouseClicked(ActionEvent evt) {
		int[] selectedRows = jTableSampleBeingSenttoMatManage.getSelectedRows();
		if (selectedRows != null && selectedRows.length > 0) {
			ArrayList containerList = getSelectedBatch().getRegInfo().getSubmitContainerList();
			for (int j = selectedRows.length - 1; j >= 0; j--) {
				if (((BatchSubmissionContainerInfo) containerList.get(selectedRows[j])).getSubmissionStatus().equals(
						BatchSubmissionContainerInfo.SUBMITTED)) {
					JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "This container has already been submitted.");
				} else {
					samplesInContainerTableModel.cutModelData(selectedRows[j]);
					getSelectedBatch().getRegInfo().removeSubmitContainer(selectedRows[j]);
					updateBatchList(getSelectedBatch());
				}
			}
		}
	}

	/** Auto-generated event handler method */
	protected void clearItemMouseClicked(ActionEvent evt) {
		boolean isSubmitted = false;
        for (BatchSubmissionContainerInfoModel model : getSelectedBatch().getRegInfo().getSubmitContainerList()) {
			if (model.getSubmissionStatus().equals(BatchSubmissionContainerInfo.SUBMITTED)) {
				isSubmitted = true;
				break;
			}
		}
		if (isSubmitted) {
			JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "Some or all containers have already been submitted.");
		} else {
			samplesInContainerTableModel.clearModelData();
			getSelectedBatch().getRegInfo().setSubmitContainerList(new ArrayList());
			updateBatchList(getSelectedBatch());
		}
	}

	// Samples sent to Screens by MM
	protected void processjTableSampleBioScreenRightClicked(MouseEvent evt) {
		// construct the PopUpMenu
		if (popUpMenuScreenSelf == null) {
			popUpMenuScreenSelf = new JPopupMenu();
			JMenuItem addItem = new JMenuItem("Lookup & Add Screens");
			// addItem.addActionListener()
			addItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent aevt) {
					lookupAddScreenMouseClicked(aevt);
				}
			});
			popUpMenuScreenSelf.add(addItem);
			JMenuItem cutItem = new JMenuItem("Remove Selected Screen(s)");
			cutItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent aevt) {
					cutScreenMouseClicked(aevt);
				}
			});
			popUpMenuScreenSelf.add(cutItem);
			JMenuItem clearItem = new JMenuItem("Remove All Screens");
			clearItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent aevt) {
					clearScreenMouseClicked(aevt);
				}
			});
			popUpMenuScreenSelf.add(clearItem);
		}
		Point p = evt.getPoint();
		popUpMenuScreenSelf.show((JTable) evt.getSource(), p.x + 1, p.y - 6);
	}

	/** Auto-generated event handler method */
	protected void cutScreenMouseClicked(ActionEvent evt) {
		int[] selectedRows = jTableSampleSentToBioTest.getSelectedRows();
		if (selectedRows != null && selectedRows.length > 0) {
			List biologyList = getSelectedBatch().getRegInfo().getNewSubmitToBiologistTestList();
			for (int j = selectedRows.length - 1; j >= 0; j--) {
				if (((BatchSubmissionToBiologistTest) biologyList.get(selectedRows[j])).getSubmissionStatus().equals(
						BatchSubmissionToBiologistTest.SUBMITTED))
					JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "This screen is already submitted.");
				else {
					sampleToMMForBioTableModel.cutModelData(selectedRows[j]);
					// need to find the right one
					int indexOfRemoved = -1;
					for (int i = 0; i < biologyList.size(); i++) {
						BatchSubmissionToBiologistTest batchSubmissionToBiologistTest = (BatchSubmissionToBiologistTest) biologyList
								.get(i);
						if (batchSubmissionToBiologistTest.isTestSubmittedByMm()) {
							++indexOfRemoved;
							if (indexOfRemoved == selectedRows[j]) {
								getSelectedBatch().getRegInfo().removeSubmitToBiologistTest(i);
								break;
							}
						}
					}
					updateBatchList(getSelectedBatch());
				}
			}
		}
	}

	/** Auto-generated event handler method */
	protected void clearScreenMouseClicked(ActionEvent evt) {
		boolean isSubmitted = false;
		List<BatchSubmissionToScreenModel> biologyList = getSelectedBatch().getRegInfo().getNewSubmitToBiologistTestList();
		for (int i = 0; i < biologyList.size(); i++) {
			if (((BatchSubmissionToScreenModel) biologyList.get(i)).getSubmissionStatus().equals(
					BatchSubmissionToBiologistTest.SUBMITTED))
				isSubmitted = true;
		}
		if (isSubmitted)
			JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "Some screens are already submitted.");
		else {
			sampleToMMForBioTableModel.clearModelData();
			for (int i = biologyList.size() - 1; i > -1; i--) {
				BatchSubmissionToScreenModel batchSubmissionToBiologistTest = (BatchSubmissionToScreenModel) getSelectedBatch()
						.getRegInfo().getNewSubmitToBiologistTestList().get(i);
				if (batchSubmissionToBiologistTest.isTestSubmittedByMm())
					getSelectedBatch().getRegInfo().removeSubmitToBiologistTest(i);
			}
			updateBatchList(getSelectedBatch());
		}
	}

	// Samples sent to Screens direct
	protected void processjTableSampleBioTestRightClicked(MouseEvent evt) {
		// construct the PopUpMenu
		if (popUpMenuScreenMM == null) {
			popUpMenuScreenMM = new JPopupMenu();
			JMenuItem addItem = new JMenuItem("Lookup & Add Screens");
			addItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent aevt) {
					lookupAddScreenMouseClicked(aevt);
				}
			});
			popUpMenuScreenMM.add(addItem);
			JMenuItem cutItem = new JMenuItem("Remove Selected Screen(s)");
			cutItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent aevt) {
					cutScreenTestMouseClicked(aevt);
				}
			});
			popUpMenuScreenMM.add(cutItem);
			JMenuItem clearItem = new JMenuItem("Remove All Screens");
			clearItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent aevt) {
					clearScreenTestMouseClicked(aevt);
				}
			});
			popUpMenuScreenMM.add(clearItem);
		}
		Point p = evt.getPoint();
		popUpMenuScreenMM.show((JTable) evt.getSource(), p.x + 1, p.y - 6);
	}

	/** Auto-generated event handler method */
	protected void cutScreenTestMouseClicked(ActionEvent evt) {
		int[] selectedRows = jTableSampleSentDirectToBioTest.getSelectedRows();
		if (selectedRows != null && selectedRows.length > 0) {
			for (int j = selectedRows.length - 1; j >= 0; j--) {
				sampleToBioForTestModel.cutModelData(selectedRows[j]);
				// need to find the right one
				int indexOfRemoved = -1;
				List biologyList = getSelectedBatch().getRegInfo().getNewSubmitToBiologistTestList();
				for (int i = 0; i < biologyList.size(); i++) {
					BatchSubmissionToBiologistTest batchSubmissionToBiologistTest = (BatchSubmissionToBiologistTest) biologyList
							.get(i);
					if (!batchSubmissionToBiologistTest.isTestSubmittedByMm()) {
						++indexOfRemoved;
						if (indexOfRemoved == selectedRows[j]) {
							getSelectedBatch().getRegInfo().removeSubmitToBiologistTest(i);
							break;
						}
					}
				}
			}
			updateBatchList(getSelectedBatch());
		}
	}

	/** Auto-generated event handler method */
	protected void clearScreenTestMouseClicked(ActionEvent evt) {
		sampleToBioForTestModel.clearModelData();
		List biologyList = getSelectedBatch().getRegInfo().getNewSubmitToBiologistTestList();
		// //System.out.println("Before: the list size is: " +
		// biologyList.size());
		for (int i = biologyList.size() - 1; i >= 0; i--) {
			BatchSubmissionToBiologistTest batchSubmissionToBiologistTest = (BatchSubmissionToBiologistTest) biologyList.get(i);
			if (!batchSubmissionToBiologistTest.isTestSubmittedByMm()) {
				getSelectedBatch().getRegInfo().removeSubmitToBiologistTest(i);
			}
		}
		// //System.out.println("After: the list size is: " +
		// biologyList.size());
		updateBatchList(getSelectedBatch());
	}

	/** Auto-generated event handler method */
	protected void lookupAddScreenMouseClicked(ActionEvent evt) {
		// need to pop up lookup/edit dialog
		jDialogLookupAddScreen = new JDialogLookupAddScreen(MasterController.getGUIComponent(), this);
		jDialogLookupAddScreen.setVisible(true);
	}

	public void addScreenInfo(ScreenSearchVO screenInfo, boolean sentByMM) {
		BatchSubmissionToScreenModel batchSubmissionToBiologistTest = new BatchSubmissionToScreenModel();
		batchSubmissionToBiologistTest.setScreenProtocolId(screenInfo.getScreenProtocolID());
		batchSubmissionToBiologistTest.setScreenCode(screenInfo.getScreenCode());
		batchSubmissionToBiologistTest.setScreenProtocolTitle(screenInfo.getScreenProtocolTitle());
		batchSubmissionToBiologistTest.setScientistCode(screenInfo.getScientistCode());
		batchSubmissionToBiologistTest.setScientistName(screenInfo.getRecipientName());
		batchSubmissionToBiologistTest.setSiteCode(screenInfo.getRecipientSiteCode());
		batchSubmissionToBiologistTest.setMinAmountValue(screenInfo.getMinAmountValue());
		batchSubmissionToBiologistTest.setMinAmountUnit(screenInfo.getMinAmountUnit());
		batchSubmissionToBiologistTest.setAmountValue(screenInfo.getMinAmountValue());
		batchSubmissionToBiologistTest.setAmountUnit(screenInfo.getMinAmountUnit());
		batchSubmissionToBiologistTest.setContainerType(screenInfo.getContainerTypeCode());
		if (sentByMM)
			batchSubmissionToBiologistTest.setSubmittedByMm("true");
		else
			batchSubmissionToBiologistTest.setSubmittedByMm("false");
		getSelectedBatch().getRegInfo().addSubmitToBiologistTest(batchSubmissionToBiologistTest);
	}

	public void updateBioTableModelAndDisplay() {
		// update by mm
		List bioTestInfoList = getSelectedBatch().getRegInfo().getNewSubmitToBiologistTestList();
		sampleToMMForBioTableModel.clearModelData();
		sampleToMMForBioTableModel.resetModelData(bioTestInfoList);
		sampleToMMForBioTableModel.setReadOnly(!(getSelectedBatch().isUserAdded() && getSelectedBatch().isEditable()));
		// update by mself
		sampleToBioForTestModel.clearModelData();
		sampleToBioForTestModel.resetModelData(bioTestInfoList);
		sampleToBioForTestModel.setReadOnly(!(getSelectedBatch().isUserAdded() && getSelectedBatch().isEditable()));
	}

	public int[] getSelectedScreens() {
		return jTableSampleSentToBioTest.getSelectedRows();
	}

	public int[] getSelectedContainers() {
		return jTableSampleBeingSenttoMatManage.getSelectedRows();
	}

	public void updateBatchScreenStatus() {
		if (selectedScreens != null) {
			for (int i = 0; i < selectedScreens.length; i++) {
				BatchSubmissionToScreenModel batchSubmissionToBiologistTest = getSelectedBatch().getRegInfo().getNewSubmitToBiologistTestList().get(selectedScreens[i]);
				if (batchSubmissionToBiologistTest.getSubmissionStatus().equals(BatchSubmissionToBiologistTest.NOT_SUBMITTED)) {
					batchSubmissionToBiologistTest.setSubmissionStatus(BatchSubmissionToBiologistTest.SUBMITTING);
				}
			}
			updateBatchList(getSelectedBatch());
		}
	}

	public void resetScreenStatus() {
		if (selectedScreens != null) {
			for (int i = 0; i < selectedScreens.length; i++) {
				BatchSubmissionToScreenModel batchSubmissionToBiologistTest = getSelectedBatch().getRegInfo().getNewSubmitToBiologistTestList().get(selectedScreens[i]);
				if (batchSubmissionToBiologistTest.getSubmissionStatus().equals(BatchSubmissionToBiologistTest.SUBMITTING)) {
					batchSubmissionToBiologistTest.setSubmissionStatus(BatchSubmissionToBiologistTest.NOT_SUBMITTED);
				}
			}
			updateBatchList(getSelectedBatch());
		}
		selectedScreens = null;
	}

	public void updateBatchScreenStatus(ProductBatchModel pBatch, String newStatus) {
		int[] selectedRows = getSelectedScreens();
		for (int i = 0; i < selectedRows.length; i++) {
			BatchSubmissionToScreenModel batchSubmissionToBiologistTest = getSelectedBatch().getRegInfo().getNewSubmitToBiologistTestList().get(selectedRows[i]);
			if (batchSubmissionToBiologistTest.getSubmissionStatus().equals(BatchSubmissionToBiologistTest.NOT_SUBMITTED)) {
				batchSubmissionToBiologistTest.setSubmissionStatus(BatchSubmissionToBiologistTest.SUBMITTING);
			}
		}
		for (int i = 0; i < pBatch.getRegInfo().getNewSubmitToBiologistTestList().size(); i++) {
			BatchSubmissionToScreenModel batchSubmissionToBiologistTest = getSelectedBatch().getRegInfo().getNewSubmitToBiologistTestList().get(i);
			if (batchSubmissionToBiologistTest.getSubmissionStatus().equals(BatchSubmissionToBiologistTest.SUBMITTING)) {
				if (newStatus.equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_FAILED)) {
					batchSubmissionToBiologistTest.setSubmissionStatus(BatchSubmissionToBiologistTest.NOT_SUBMITTED);
				} else if (newStatus.equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_PASSED)) {
					batchSubmissionToBiologistTest.setSubmissionStatus(BatchSubmissionToBiologistTest.SUBMITTED);
				}
			}
		}
		updateBatchList(getSelectedBatch());
	}

//	/**
//	 * @return Returns the siteName.
//	 */
//	public String getSiteName() {
//		return siteName;
//	}
//
//	/**
//	 * @param siteName
//	 *            The siteName to set.
//	 */
//	public void setSiteName(String siteName) {
//		this.siteName = siteName;
//	}

	/** Auto-generated event handler method */
	protected void jRadioButtonkNoneActionPerformed(ActionEvent evt) {
		getSelectedBatch().setProtectionCode("NONE");
		updateBatchList(getSelectedBatch());
	}

	/** Auto-generated event handler method */
	protected void jRadioButtonStandardActionPerformed(ActionEvent evt) {
		getSelectedBatch().setProtectionCode("BS");
		updateBatchList(getSelectedBatch());
	}

	/** Auto-generated event handler method */
	protected void jRadioButtonCurationActionPerformed(ActionEvent evt) {
		getSelectedBatch().setProtectionCode("BC");
		updateBatchList(getSelectedBatch());
	}

	/** Auto-generated event handler method */
	protected void jRadioButtonStandardCurationActionPerformed(ActionEvent evt) {
		getSelectedBatch().setProtectionCode("BSC");
		updateBatchList(getSelectedBatch());
	}

	public void updateRadioButtons(boolean isEnabled) {
		jRadioButtonStandard.setEnabled(isEnabled);
		jRadioButtonCuration.setEnabled(isEnabled);
		jRadioButtonStandardCuration.setEnabled(isEnabled);
		jRadioButtonkNone.setEnabled(isEnabled);
	}

	/**
	 * @return Returns the unitList.
	 */
	public ArrayList getUnitList() {
		return unitList;
	}

	/**
	 * @param unitList
	 *            The unitList to set.
	 */
	public void setUnitList(ArrayList unitList) {
		this.unitList = unitList;
	}

	private boolean showEditableDialog() {
		boolean result = isContainerEditable();
		if (result)
			result = showBatchEditableInfo();
		return result;
	}

	private boolean showBatchEditableInfo() {
		boolean result = (selectedBatch != null);
		if (!result) {
			// no batch selected
			JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "Please select a batch first.");
		} else {
			if (selectedBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING)) {
				result = false;
				JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "The selected batch is currently in the process of registration.");
			} else if (selectedBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_PASSED)) {
				result = false;
				JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "The selected batch is already registered.");
			}
		}
		return result;
	}

	private boolean isSelectedBatchEditable() {
		NotebookPageModel page = ((SingletonNotebookPageGUI)parentDialog).getPageModel();
		return (CommonUtils.getProductBatchModelEditableFlag(selectedBatch, page));
	}

	private boolean isContainerEditable() {
		NotebookPageModel page = ((SingletonNotebookPageGUI)parentDialog).getPageModel();
		return (parentDialog != null && page != null && page.isEditable());

	}
}
