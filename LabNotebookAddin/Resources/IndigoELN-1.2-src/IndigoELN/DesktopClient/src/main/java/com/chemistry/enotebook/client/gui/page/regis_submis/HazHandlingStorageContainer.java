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
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.cloudgarden.layout.AnchorConstraint;
import com.cloudgarden.layout.AnchorLayout;
import info.clearthought.layout.TableLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class HazHandlingStorageContainer extends javax.swing.JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6032974612469098201L;
	private JPanel jPanelContainer;
	private JLabel jLabelStorageInstructions;
	private JTextArea jTextAreaStorageInstructions;
	private JScrollPane jScrollPaneStorage;
	private JPanel jPanelSouth;
	private JLabel jLabelHandlingPrecautions;
	private JTextArea jTextAreaHandlingPrecautions;
	private JScrollPane jScrollPanePrecautions;
	private JPanel jPanelCentre;
	private JLabel jLabelHealthHazards;
	private JTextArea jTextAreaHealthHaz;
	private JScrollPane jScrollPaneHealthHaz;
	private JPanel jPanelNorth;
	private ArrayList hazardList = new ArrayList();
	private ArrayList handlingList = new ArrayList();
	private ArrayList storageList = new ArrayList();
	private String hazardString = new String();
	private String handlingString = new String();
	private String storageString = new String();
	private HashMap hazardMap = new HashMap();
	private HashMap handlingMap = new HashMap();
	private HashMap storageMap = new HashMap();
	private NotebookPageGuiInterface parentDialog;
	private ProductBatchModel selectedBatch;
	private JToolBar jToolBarOptions;
	private JButton jButtonEditInfo;
	private NotebookPageModel pageModel;

	public HazHandlingStorageContainer(NotebookPageModel pageModel) {
		this.pageModel = pageModel;
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jPanelContainer = new JPanel();
			jPanelNorth = new JPanel();
			jScrollPaneHealthHaz = new JScrollPane();
			jTextAreaHealthHaz = new JTextArea();
			jLabelHealthHazards = new JLabel();
			jPanelCentre = new JPanel();
			jScrollPanePrecautions = new JScrollPane();
			jTextAreaHandlingPrecautions = new JTextArea();
			jLabelHandlingPrecautions = new JLabel();
			jPanelSouth = new JPanel();
			jScrollPaneStorage = new JScrollPane();
			jTextAreaStorageInstructions = new JTextArea();
			jLabelStorageInstructions = new JLabel();
			jPanelContainer.setLayout(new BorderLayout());
			jPanelContainer.setBorder(new BevelBorder(BevelBorder.LOWERED));
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setPreferredSize(new java.awt.Dimension(456, 270));
			AnchorLayout jPanelNorthLayout = new AnchorLayout();
			jPanelNorth.setLayout(jPanelNorthLayout);
			jPanelNorth.setPreferredSize(new java.awt.Dimension(456, 80));
			jPanelNorth.setSize(new java.awt.Dimension(456, 80));
			jPanelContainer.add(jPanelNorth, BorderLayout.NORTH);
			jPanelNorth.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jPanelNorthMouseClicked(evt);
				}
			});
			jScrollPaneHealthHaz.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPaneHealthHaz.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jScrollPaneHealthHaz.setVisible(true);
			jScrollPaneHealthHaz.setPreferredSize(new java.awt.Dimension(443, 58));
			jScrollPaneHealthHaz.setBounds(new java.awt.Rectangle(5, 20, 443, 58));
			jPanelNorth.add(jScrollPaneHealthHaz, new AnchorConstraint(256, 983, 981, 12, 1, 1, 1, 1));
			jTextAreaHealthHaz.setLineWrap(true);
			jTextAreaHealthHaz.setText("No Hazards currently identified");
			jTextAreaHealthHaz.setEditable(false);
			jTextAreaHealthHaz.setVisible(true);
			jScrollPaneHealthHaz.add(jTextAreaHealthHaz);
			jScrollPaneHealthHaz.setViewportView(jTextAreaHealthHaz);
			jTextAreaHealthHaz.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jTextAreaHealthHazMouseClicked(evt);
				}
			});
			jLabelHealthHazards.setText("(A) Health Hazards:");
			jLabelHealthHazards.setVisible(true);
			jLabelHealthHazards.setFont(new java.awt.Font("Dialog", 1, 12));
			jLabelHealthHazards.setPreferredSize(new java.awt.Dimension(120, 20));
			jLabelHealthHazards.setBounds(new java.awt.Rectangle(0, 0, 120, 18));
			jPanelNorth.add(jLabelHealthHazards, new AnchorConstraint(5, 264, 230, 1, 1, 1, 1, 1));
			AnchorLayout jPanelCentreLayout = new AnchorLayout();
			jPanelCentre.setLayout(jPanelCentreLayout);
			jPanelCentre.setPreferredSize(new java.awt.Dimension(456, 85));
			jPanelContainer.add(jPanelCentre, BorderLayout.CENTER);
			jPanelCentre.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jPanelCentreMouseClicked(evt);
				}
			});
			jScrollPanePrecautions.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPanePrecautions.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jScrollPanePrecautions.setVisible(true);
			jScrollPanePrecautions.setPreferredSize(new java.awt.Dimension(443, 61));
			jScrollPanePrecautions.setBounds(new java.awt.Rectangle(5, 18, 443, 62));
			jPanelCentre.add(jScrollPanePrecautions, new AnchorConstraint(222, 983, 957, 12, 1, 1, 1, 1));
			jTextAreaHandlingPrecautions.setLineWrap(true);
			jTextAreaHandlingPrecautions.setText("No Handling precautions");
			jTextAreaHandlingPrecautions.setEditable(false);
			jTextAreaHandlingPrecautions.setVisible(true);
			jScrollPanePrecautions.add(jTextAreaHandlingPrecautions);
			jScrollPanePrecautions.setViewportView(jTextAreaHandlingPrecautions);
			jTextAreaHandlingPrecautions.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jTextAreaHandlingPrecautionsMouseClicked(evt);
				}
			});
			jLabelHandlingPrecautions.setText("(B) Handling Precautions:");
			jLabelHandlingPrecautions.setHorizontalAlignment(SwingConstants.LEADING);
			jLabelHandlingPrecautions.setHorizontalTextPosition(SwingConstants.TRAILING);
			jLabelHandlingPrecautions.setVisible(true);
			jLabelHandlingPrecautions.setFont(new java.awt.Font("Dialog", 1, 12));
			jLabelHandlingPrecautions.setPreferredSize(new java.awt.Dimension(151, 16));
			jLabelHandlingPrecautions.setBounds(new java.awt.Rectangle(0, 0, 150, 16));
			jPanelCentre.add(jLabelHandlingPrecautions, new AnchorConstraint(5, 330, 196, -1, 1, 1, 1, 1));
			AnchorLayout jPanelSouthLayout = new AnchorLayout();
			jPanelSouth.setLayout(jPanelSouthLayout);
			jPanelSouth.setPreferredSize(new java.awt.Dimension(456, 86));
			jPanelContainer.add(jPanelSouth, BorderLayout.SOUTH);
			jPanelSouth.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jPanelSouthMouseClicked(evt);
				}
			});
			jScrollPaneStorage.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPaneStorage.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jScrollPaneStorage.setVisible(true);
			jScrollPaneStorage.setPreferredSize(new java.awt.Dimension(443, 60));
			jScrollPaneStorage.setBounds(new java.awt.Rectangle(5, 20, 443, 60));
			jPanelSouth.add(jScrollPaneStorage, new AnchorConstraint(238, 983, 936, 12, 1, 1, 1, 1));
			jTextAreaStorageInstructions.setLineWrap(true);
			jTextAreaStorageInstructions.setText("No special storage required");
			jTextAreaStorageInstructions.setEditable(false);
			jTextAreaStorageInstructions.setVisible(true);
			jScrollPaneStorage.add(jTextAreaStorageInstructions);
			jScrollPaneStorage.setViewportView(jTextAreaStorageInstructions);
			jTextAreaStorageInstructions.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jTextAreaStorageInstructionsMouseClicked(evt);
				}
			});
			jLabelStorageInstructions.setText("(C) Storage Instructions:");
			jLabelStorageInstructions.setVisible(true);
			jLabelStorageInstructions.setFont(new java.awt.Font("Dialog", 1, 12));
			jLabelStorageInstructions.setPreferredSize(new java.awt.Dimension(158, 17));
			jLabelStorageInstructions.setBounds(new java.awt.Rectangle(0, 0, 158, 19));
			jPanelSouth.add(jLabelStorageInstructions, new AnchorConstraint(6, 348, 224, 1, 1, 1, 1, 1));
			add(jPanelContainer, BorderLayout.CENTER);
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
		handlingList = RegCodeMaps.getInstance().getHandlingList();
		hazardList = RegCodeMaps.getInstance().getHazardList();
		storageList = RegCodeMaps.getInstance().getStorageList();
		handlingMap = RegCodeMaps.getInstance().getHandlingMap();
		hazardMap = RegCodeMaps.getInstance().getHazardMap();
		storageMap = RegCodeMaps.getInstance().getStorageMap();
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
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
		jButtonEditInfo = new JButton();
		jButtonEditInfo.setText("Edit Information");
		jButtonEditInfo.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
				"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, jButtonEditInfo);
		jToolBarOptions.add(jButtonEditInfo, "2,0");
		jButtonEditInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				launchPickerDialog();
			}
		});
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
			HazHandlingStorageContainer inst = new HazHandlingStorageContainer(null);
			frame.setContentPane(inst);
			frame.getContentPane().setSize(inst.getSize());
			frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Auto-generated event handler method */
	protected void jPanelSouthMouseClicked(MouseEvent evt) {
		if (evt.getButton() == MouseEvent.BUTTON3) {
			launchPickerDialog();
		}
	}

	/** Auto-generated event handler method */
	protected void jPanelCentreMouseClicked(MouseEvent evt) {
		if (evt.getButton() == MouseEvent.BUTTON3) {
			launchPickerDialog();
		}
	}

	/** Auto-generated event handler method */
	protected void jPanelNorthMouseClicked(MouseEvent evt) {
		if (evt.getButton() == MouseEvent.BUTTON3) {
			launchPickerDialog();
		}
	}

	protected void launchPickerDialog() {
		if (selectedBatch == null) {
			// no batch selected
			JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "Please select a batch first.");
		} else {
			if (isEditable()) {
				if (selectedBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING)) {
					JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "The selected batch is currently in the process of registration.");
				} else if (selectedBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_PASSED)) {
					JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "The selected batch is already registered.");
				} else {
					JDialogHealthHazInfo jDialogHealthHazInfo = new JDialogHealthHazInfo(MasterController.getGUIComponent(), pageModel);
					Point loc = MasterController.getGuiController().getGUIComponent().getLocation();
					Dimension dim = MasterController.getGuiController().getGUIComponent().getSize();
					jDialogHealthHazInfo.setLocation(loc.x + (dim.width - jDialogHealthHazInfo.getSize().width) / 2, loc.y
							+ (dim.height - jDialogHealthHazInfo.getSize().height) / 2);
					jDialogHealthHazInfo.setHandlingList(handlingList);
					jDialogHealthHazInfo.setHazardList(hazardList);
					jDialogHealthHazInfo.setStorageList(storageList);
					jDialogHealthHazInfo.setHandlingMap(handlingMap);
					jDialogHealthHazInfo.setHazardMap(hazardMap);
					jDialogHealthHazInfo.setStorageMap(storageMap);
					jDialogHealthHazInfo.setSelectedBatch(selectedBatch);
					if (jDialogHealthHazInfo.getHealthHazInfo()) {
						updateDisplay(); // update
						// hazHandlingStorageContainer
						// display
						updateBatchList(); // update batchlist
					}
					jDialogHealthHazInfo = null;
				}
			} else
				JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "The batch is currently not editable.");
		}
	}

	public void updateDisplay() {
		hazardString = SingletonRegSubHandler.getBatchHazardString((ProductBatchModel) selectedBatch);
		if (hazardString != null && hazardString.length() > 0) {
			jTextAreaHealthHaz.setText(hazardString);
			jTextAreaHealthHaz.setCaretPosition(0);
		} else
			jTextAreaHealthHaz.setText("No Hazards currently identified");
		handlingString = SingletonRegSubHandler.getBatchHandlingString((ProductBatchModel) selectedBatch);
		if (handlingString != null && !handlingString.equals("")) {
			jTextAreaHandlingPrecautions.setText(handlingString);
			jTextAreaHandlingPrecautions.setCaretPosition(0);
		} else
			jTextAreaHandlingPrecautions.setText("No Handling precautions");
		storageString = SingletonRegSubHandler.getBatchStorageString((ProductBatchModel) selectedBatch);
		if (storageString != null && !storageString.equals("")) {
			jTextAreaStorageInstructions.setText(storageString);
			jTextAreaStorageInstructions.setCaretPosition(0);
		} else
			jTextAreaStorageInstructions.setText("No special storage required");
		boolean isEditable = isEditable();
		jTextAreaHealthHaz.setEnabled(isEditable);
		jTextAreaHandlingPrecautions.setEnabled(isEditable);
		jTextAreaStorageInstructions.setEnabled(isEditable);
		jButtonEditInfo.setEnabled(isEditable);
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
	 * @param selectedBatch
	 *            The selectedBatch to set.
	 */
	public void setSelectedBatch(ProductBatchModel selectedBatch) {
		this.selectedBatch = selectedBatch;
	}

	public void updateBatchList() {
		/** TODO Bo Yang 3/14/07
		this.getParentDialog().getRegSubSum_cont().updateBatchList(selectedBatch);
		*/
	}

	/** Auto-generated event handler method */
	protected void jTextAreaHealthHazMouseClicked(MouseEvent evt) {
		if (evt.getButton() == MouseEvent.BUTTON3 || (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2)) {
			launchPickerDialog();
		}
	}

	/** Auto-generated event handler method */
	protected void jTextAreaHandlingPrecautionsMouseClicked(MouseEvent evt) {
		if (evt.getButton() == MouseEvent.BUTTON3 || (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2)) {
			launchPickerDialog();
		}
	}

	/** Auto-generated event handler method */
	protected void jTextAreaStorageInstructionsMouseClicked(MouseEvent evt) {
		if (evt.getButton() == MouseEvent.BUTTON3 || (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2)) {
			launchPickerDialog();
		}
	}

	private boolean isEditable() {
		/** TODO Bo Yang 3/14/07
		NotebookPage page = (NotebookPage) parentDialog.getNotebookPage();
		return (selectedBatch != null && selectedBatch.isEditable() && parentDialog != null && page != null && page
				.isPageEditable());
				*/
		return true;
	}
}