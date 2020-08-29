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

import com.chemistry.ChemistryEditorEvent;
import com.chemistry.ChemistryEditorListener;
import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.errorhandler.MsgBox;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.gui.page.ConceptionNotebookPageGUI;
import com.chemistry.enotebook.client.gui.page.ParallelNotebookPageGUI;
import com.chemistry.enotebook.client.gui.page.SingletonNotebookPageGUI;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ReactionSchemeModel;
import com.chemistry.enotebook.domain.ReactionStepModel;
import com.chemistry.enotebook.sdk.PictureProperties;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.Decoder;
import com.chemistry.viewer.ReactionViewer;
import info.clearthought.layout.TableLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class ReactionSchemaContainer extends javax.swing.JPanel implements ChemistryEditorListener, ReactionSchemeModelChangeListener {
	
	private static final long serialVersionUID = -1690185026747869328L;
	
	private static final Log log = LogFactory.getLog(ReactionSchemaContainer.class);
	private static ChemistryDelegate chemDelegate = null;

	// private JPanel jPanelReaction;
	private ReactionViewer reactionCanvas;
	private ChemistryPanel chimeProSwing = new ChemistryPanel();

	// private NotebookPageModel notebookPageModelPojo = null;
	private ReactionStepModel intendedStep = null;
	private ReactionSchemeModel intendedScheme = null;
	private boolean readOnly = false;
	private boolean rxnUpdateInProgress = false;
	private boolean isMaximized = false;
	private int contentHeight = 0;
	private boolean displayToolbar = false;
	private JToolBar jToolBarOptions;
	private JLabel jLabelMinMax;
	private JButton jButtonRxnProps;
	NotebookPageGuiInterface notebookGUI;
	private static List<ReactionSchemaContainer> selfListeners = new Vector<ReactionSchemaContainer>();
	
	static {
		chemDelegate = new ChemistryDelegate();
	}
	
	// private JLabel jLabelDisplayTypeMsg;
	public ReactionSchemaContainer(boolean displayToolbar) {
		this.displayToolbar = displayToolbar;
		initGUI();
	}

	public ReactionSchemaContainer(boolean displayToolbar, boolean editable) {
		this.readOnly = !editable;
		this.displayToolbar = displayToolbar;
		initGUI();
	}
	
	public ReactionSchemaContainer(boolean displayToolbar, boolean editable, NotebookPageGuiInterface mnotebookGUI) {
		this.readOnly = !editable;
		this.displayToolbar = displayToolbar;
		notebookGUI = mnotebookGUI;
		initGUI();
	}
	
	public void dispose() {
		// this.pageModel = null;
		this.intendedScheme = null;
		this.intendedStep = null;
		selfListeners.remove(this);
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			// jPanelReaction = new JPanel();
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setPreferredSize(new java.awt.Dimension(517, 200));
			this.setBorder(new MatteBorder(new Insets(1, 1, 1, 1), new java.awt.Color(0, 0, 0)));
			// BorderLayout jPanelReactionLayout = new BorderLayout();
			// jPanelReaction.setLayout(jPanelReactionLayout);
			// jPanelReactionLayout.setHgap(0);
			// jPanelReactionLayout.setVgap(0);
			// jPanelReaction.setBackground(new java.awt.Color(255, 255, 255));
			// this.add(jPanelReaction, BorderLayout.CENTER);
			
			selfListeners.add(this);
			
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
		try {
			String content = "Reaction";
			
			if (notebookGUI != null && notebookGUI.getPageModel() != null)
				content += (" in " + notebookGUI.getPageModel().getNotebookRefAsString());
			
			reactionCanvas = new ReactionViewer(MasterController.getGUIComponent().getTitle(), content, ReactionViewer.VIEW_NATIVE);
			reactionCanvas.setEditorType(MasterController.getGuiController().getDrawingTool());
			reactionCanvas.setReadOnly(readOnly);
			
			this.add(reactionCanvas, BorderLayout.CENTER);
			
			if (!readOnly)
				reactionCanvas.addChemistryEditorListener(this);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
		// jMenuItemImportSynthesisPlan = new JMenuItem("Import Synthesis Plan PSDef File");
		// jMenuItemImportSynthesisPlan.setIconTextGap(0);
		// jMenuItemEditAttrib = new JMenuItem("Edit Scheme Attributes");
		// jMenuItemShowOrig = new JMenuItem("Show Original Drawing");
		// jMenuItemShowParsed = new JMenuItem("Show Parsed Scheme");
		// jMenuItemShowParsedInter = new JMenuItem("Show Parsed
		// Intermediates");
		// jMenuItemViewProperties = new JMenuItem("View Reaction Properties");
		// jLabelDisplayTypeMsg = new JLabel("Current display: Original
		// Drawing");
		if (displayToolbar) {
			double toolBarSize[][] = {
					{ 20, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP,
							CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN,
							CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN }, { 20, 2 } };
			jToolBarOptions = new JToolBar();
			TableLayout jToolBarLayout = new TableLayout(toolBarSize);
			jToolBarOptions.setLayout(jToolBarLayout);
			jToolBarOptions.setBorderPainted(false);
			jToolBarOptions.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			this.add(jToolBarOptions, BorderLayout.NORTH);
			// Add buttons to toolbar
			jLabelMinMax = new JLabel();
			jLabelMinMax.setToolTipText("Maximize");
			jLabelMinMax.setIcon(CenIconFactory.getImageIcon(CenIconFactory.General.MAXIMIZE));
			jToolBarOptions.add(jLabelMinMax, "0,0");
			jLabelMinMax.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent evt) {
					CollapsiblePane cp = (CollapsiblePane) getParent().getParent();
					if (isMaximized) {
						// code here expand content up
						cp.setContentPaneHeight(contentHeight);
						isMaximized = false;
						jLabelMinMax.setToolTipText("Maximize");
						jLabelMinMax.setIcon(CenIconFactory.getImageIcon(CenIconFactory.General.MAXIMIZE));
					} else {
						// code here scroll up so title is at top then expand
						// content down
						contentHeight = cp.getContentPaneHeight();
						JViewport jv = (JViewport) cp.getParent().getParent();
						Dimension d = jv.getExtentSize();
						jv.setViewPosition(new Point(0, cp.getY()));
						cp.setContentPaneHeight(d.height - 25); // 25 = height
						// of pane
						// header
						isMaximized = true;
						jLabelMinMax.setToolTipText("Restore");
						jLabelMinMax.setIcon(CenIconFactory.getImageIcon(CenIconFactory.General.RESTORE));
					}
					cp.invalidate();
				}
			});
			jButtonRxnProps = new JButton();
			jButtonRxnProps.setText("Reaction Properties");
			jButtonRxnProps.setToolTipText("Reaction Properties for Legacy Cambridge eNotebook Experiments");
			jButtonRxnProps.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
					"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonRxnProps);
			jToolBarOptions.add(jButtonRxnProps, "2,0");
			jButtonRxnProps.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					if (intendedStep == null || intendedStep.getRxnProperties() == null
							|| intendedStep.getRxnProperties().length() == 0)
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "There are no Reaction Properties.",
								"Reaction Properties", JOptionPane.INFORMATION_MESSAGE);
					else
						MsgBox.showMsgBox(MasterController.getGUIComponent(), "Reaction Properties", intendedStep
								.getRxnProperties());
				}
			});
			// jLabelDisplayTypeMsg.setVisible(true);
			// jToolBarOptions.add(jLabelDisplayTypeMsg, "4,0");
		}
	}

	public void destroy() {
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
			ReactionSchemaContainer inst = new ReactionSchemaContainer(true);
			frame.setContentPane(inst);
			frame.getContentPane().setSize(inst.getSize());
			frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public ReactionStepModel getReactionStepModel() {
		return intendedStep;
	}
	
	public void setReactionStep(ReactionStepModel step) {
		//System.err.println("setReactionStep " + step.hashCode());
		intendedStep = step;
		if (intendedStep != null){
			intendedScheme = step.getRxnScheme();
		} else {
			intendedScheme = null;
		}
		refresh();
	}
	
	public void refresh() {
		// System.out.println("intendedScheme "+intendedScheme.getDspReactionDefinition());
		if (intendedStep != null) {
			// Load the step & Scheme (if any)
			// loadIntendedScheme();
			// Store the Native & Parsed sketches in the Viewer
			rxnUpdateInProgress = true;

			byte[] nativeSktch = getNativeSketch();
			if (nativeSktch == null || nativeSktch.length == 0) {
				setStringFormatForChime();
			} else {
				reactionCanvas.setNativeSketch(nativeSktch);
			}
			/*
			 * reactionCanvas.setParsedSketch(getSearchSketch()); rxnUpdateInProgress = false; // Switch viewer to desired mode if
			 * (reactionCanvas.getPreferredViewMode() == ReactionViewer.VIEW_NATIVE) doShowOriginalDrawing(null); else
			 * doShowParsedDrawing(null);
			 */

			reactionCanvas.setReadOnly(readOnly);
			
			rxnUpdateInProgress = false;
		} else {
			dispose();
			// System.out.println("intendedStep ==null");
		}
	}

	public void editingStarted(ChemistryEditorEvent structureeditorevent) {
	}

	public void structureChanged(ChemistryEditorEvent structureeditorevent) {
		// System.out.println("StructChngd: ");
		if (!rxnUpdateInProgress) {
			if (!readOnly) {
				if (reactionCanvas.getPreferredViewMode() == ReactionViewer.VIEW_NATIVE)
					setNativeSketch(reactionCanvas.getChemistry(), reactionCanvas.getEditorType());
				else
					setSearchSketch(reactionCanvas.getNativeSketch());
			}
			fireStructureChangedEventToOtherComponents();
			fireStepModelChanged(new ReactionStepModelChangeEvent(this));
		}
	}

	public void editingStopped(ChemistryEditorEvent structureeditorevent) {
		enableSaveButtons();
	}

	/**
	 * Retired Bo Yang 3/15/07 private void loadIntendedScheme() { //ReactionCache rxncache = pageModel.getReactionCache();
	 * 
	 * //Map rxns = rxncache.getReactions(); //if (!rxns.isEmpty()) { Reaction rxn = (Reaction)
	 * rxns.get(rxns.keySet().iterator().next()); intendedStep = rxn.getRepresentativeReactionStep(); intendedScheme =
	 * intendedStep.getReactionScheme(); if (intendedScheme != null) intendedScheme.addObserver(this); //else
	 * //rxncache.addObserver(this); //}
	 *  }
	 */
	/**
	 * Retired Bo Yang 3/15/07 private void initIntendedScheme() { if (intendedScheme == null) { ReactionCache rxncache =
	 * pageModel.getReactionCache(); if (rxncache != null) { Map rxns = rxncache.getReactions(); Reaction rxn = null; if
	 * (rxns.isEmpty()) { // No reaction associated with this page, Create one rxn = new Reaction(); rxn.initialize(); // creates
	 * the Representative // ReactionStep = ReactionType.INTENDED rxncache.addReaction(rxn.getKey(), rxn); } else rxn = (Reaction)
	 * rxns.get(rxns.keySet().iterator().next()); intendedStep = rxn.getRepresentativeReactionStep(); intendedScheme =
	 * intendedStep.getReactionScheme(); rxncache.deleteObserver(this); intendedScheme.addObserver(this); } } }
	 */
	/*
	 * private void postInitGUI() { try { reactionCanvas = new ReactionViewer("Magnesium TriChloride", ReactionViewer.VIEW_NATIVE);
	 * reactionCanvas.setEditorType(Compound.ISISDRAW); reactionCanvas.setReadOnly(readOnly); // System.out.println("Inter Step Rxn //
	 * "+intermediateStep.getStepNumber()+" // "+intermediateStep.getRxnScheme().getDspReactionDefinition()); String decodedString =
	 * Decoder.decodeString(intermediateStep.getRxnScheme().getDspReactionDefinition()); int index = decodedString.indexOf("M END");
	 * if (index >= 0) { chimeProSwing.setMolfileData(decodedString); } else { // System.out.println("Gui::createToolTip
	 * Decoder.decodeString(rxn) = "+rxn); chimeProSwing.setChimeString(decodedString); } //
	 * System.out.println("ReactionSchemaViewer::createpostInitGUIToolTip Decoder.decodeString //
	 * intermediateStep.getRxnScheme().getDspReactionDefinition = "+decodedString); // chimeProSwing.setMolfileData(decodedString);
	 * reactionCanvas.setChemistry(chimeProSwing.getMolfileData().getBytes()); jPanelReaction.add(reactionCanvas,
	 * BorderLayout.CENTER); } catch (Exception e) { CeNErrorHandler.getInstance().logExceptionMsg(jPanelReaction, e); } }
	 */

	private void setStringFormatForChime() {
		String decodedString = Decoder.decodeString(intendedStep.getRxnScheme().getStringSketchAsString());
		int index = decodedString.indexOf("M  END");
		if (index >= 0 || CommonUtils.isNull(decodedString)) {
			chimeProSwing.setMolfileData(decodedString);
		} else {
			throw new UnsupportedOperationException("Supported only operations with mol or rxn file format");
		}
		// System.out.println("ReactionSchemaViewer::createpostInitGUIToolTip Decoder.decodeString
		// intermediateStep.getRxnScheme().getDspReactionDefinition = "+decodedString);
		// chimeProSwing.setMolfileData(decodedString);
		// reactionCanvas.setEditorType(Compound.ISISDRAW);
		reactionCanvas.setChemistry(chimeProSwing.getMolfileData().getBytes());
	}

	public byte[] getNativeSketch() {
		if (intendedScheme == null)
			return new byte[0];
		else {
			// System.out.println("intendedScheme.getNativeSketch() length"+intendedScheme.getNativeSketch().length);
			return intendedScheme.getNativeSketch();
		}
	}

	public void setNativeSketch(byte[] sketch, int format) {
		// try {
		// initIntendedScheme();
		// intendedScheme.loadSketch(sketch, format);

		// System.out.println("intendedScheme "+intendedScheme);
		// System.out.println("sketch length"+sketch.length);
		// System.out.println("format "+format);
		intendedScheme.setNativeSketch(sketch);
		intendedScheme.setNativeSketchFormat("" + format);

		byte[] pic = null;
		try {
			pic = chemDelegate.convertReactionToPicture(sketch, PictureProperties.PNG, 1000, 2000, 1.0, 0.26458);
		} catch (Exception e) {
			try {
				pic = chemDelegate.convertStructureToPicture(sketch, PictureProperties.PNG, 1000, 2000, 1.0, 0.26458);
			} catch (Exception e2) {
				log.error("Unable to make ReactionScheme JPEG Images:" + e2.getMessage());
			}
		}
		
		intendedScheme.setViewSketch(pic);
		
		// } catch (ChemUtilAccessException e) {
		// CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		// }
	}
	
	private void enableSaveButtons() {
		MasterController.getGUIComponent().enableSaveButtons();
	}

	public byte[] getSearchSketch() {
		if (intendedScheme == null)
			return new byte[0];
		else
			return intendedScheme.getStringSketch();
	}

	public void setSearchSketch(byte[] sketch) {
		// try {
		// initIntendedScheme();
		// intendedScheme.loadSearchSketch(sketch);
		intendedScheme.setStringSketch(sketch);
		// } catch (ChemUtilAccessException e) {
		// CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		// }
	}

	/**
	 * Retired Bo Yang 3/15/07 public void update(Observable watchedObj, Object obj) { rxnUpdateInProgress = true;
	 * reactionCanvas.setNativeSketch(getNativeSketch()); reactionCanvas.setParsedSketch(getSearchSketch()); rxnUpdateInProgress =
	 * false; }
	 */

	public ReactionViewer getReactionViewer() {
		return reactionCanvas;
	}

	public void reactionSchemeModelChanged(ReactionSchemeModelChangeEvent structureevent) {
		System.out.println("reactionSchemeModelChanged: ");
		// reactionCanvas.getPreferredViewMode() + " " + intendedScheme);
		if (reactionCanvas.getPreferredViewMode() == ReactionViewer.VIEW_NATIVE) {
			reactionCanvas.setNativeSketch(intendedScheme.getNativeSketch());
			// reactionCanvas.setEditorType(intendedScheme.getNativeSketchFormat());
		} else {
			reactionCanvas.setNativeSketch(intendedScheme.getStringSketch());
		}
		
		fireStepModelChanged(new ReactionStepModelChangeEvent(this));
	}
	
	
	private void fireStepModelChanged(ReactionStepModelChangeEvent stepEvent) {
		for (Iterator<ReactionSchemaContainer> iterator = selfListeners.iterator(); iterator.hasNext();) {
			ReactionSchemaContainer reactionSchemaContainer = (ReactionSchemaContainer) iterator.next();
			if (!this.equals(reactionSchemaContainer)) {
				reactionSchemaContainer.reactionStepModelChanged(stepEvent);
			}
		}
	}
	
	private void reactionStepModelChanged(ReactionStepModelChangeEvent stepEvent) {
		if (getReactionStepModel() != null && getReactionStepModel().equals(stepEvent.getReactionStepModel())) {
			refresh();
		}
	}
	
	
	private void fireStructureChangedEventToOtherComponents() {
		if (notebookGUI != null && notebookGUI.getPageModel() != null && !notebookGUI.getPageModel().isLoadingFromDB())
			notebookGUI.getPageModel().setModelChanged(true);
		if(notebookGUI instanceof SingletonNotebookPageGUI) {
		  ((SingletonNotebookPageGUI)notebookGUI).reactionSchemaStructureChanged(new ReactionSchemaStructureChangeEvent(this));
		  ((SingletonNotebookPageGUI)notebookGUI).reactionSchemeModelChangedOnExpTab(new ReactionSchemeModelChangeEvent(this));
		} else if(notebookGUI instanceof ParallelNotebookPageGUI) {
			//((ParallelNotebookPageGUI)notebookGUI)
		} else if(notebookGUI instanceof ConceptionNotebookPageGUI) {
		 ((ConceptionNotebookPageGUI)notebookGUI).fireReactionChanged(intendedScheme);
		}
	}

	public void refreshPageModel(NotebookPageModel pageModelPojo, boolean isAlwaysReadOnly) {
		if(isAlwaysReadOnly) {
			reactionCanvas.setReadOnly(true);
			reactionCanvas.invalidate();
			return;
		}
		
		readOnly = !pageModelPojo.isEditable();
		if (reactionCanvas.isReadOnly() != readOnly)
		{
			reactionCanvas.setReadOnly(readOnly);
			reactionCanvas.invalidate();
		}
	}

	class ReactionStepModelChangeEvent extends EventObject{
		/**
		 * 
		 */
		private static final long serialVersionUID = -7457659548972465043L;

		public ReactionStepModelChangeEvent(Object obj){
			 super(obj);
		}
		
		public ReactionStepModel getReactionStepModel() {
			return ((ReactionSchemaContainer)getSource()).getReactionStepModel();
		}
	}
}
