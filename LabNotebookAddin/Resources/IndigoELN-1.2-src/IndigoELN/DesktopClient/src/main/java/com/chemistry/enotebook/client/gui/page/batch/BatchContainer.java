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
package com.chemistry.enotebook.client.gui.page.batch;

import com.chemistry.ChemistryEditorEvent;
import com.chemistry.ChemistryEditorListener;
import com.chemistry.ChemistryEditorProxy;
import com.chemistry.enotebook.chloracnegen.classes.Structure;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.chlorac.ChloracnegenBatchStructure;
import com.chemistry.enotebook.client.gui.chlorac.ChloracnegenPredictor;
import com.chemistry.enotebook.client.gui.chlorac.ChloracnegenResultsViewContainer;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.errorhandler.MsgBox;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ChemUtilInitException;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.utils.*;
import com.chemistry.enotebook.utils.SwingWorker;
import com.chemistry.viewer.ChemistryViewer;
import info.clearthought.layout.TableLayout;
import org.apache.commons.lang.StringUtils;
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
import java.util.ArrayList;
import java.util.List;

public class BatchContainer extends javax.swing.JPanel implements ChemistryEditorListener {

	private static final long serialVersionUID = -1406730708083522553L;
	private static final Log log = LogFactory.getLog(BatchContainer.class);
	
	private ChemistryViewer reactionCanvas;
	
	private ReactionStepModel intendedStep = null;
	private ReactionSchemeModel intendedScheme = null;
	private boolean readOnly = false;

	private boolean isMaximized = false;
	private int contentHeight = 0;
	private boolean displayToolbar = false;
	private JToolBar jToolBarOptions;
	private JLabel jLabelMinMax;
	private JButton jButtonRxnProps;

	private ProductBatchModel  productBatchModel = null;
	private BatchEditPanel batchEditPanel = null;
	private ChemistryDelegate chemDelegate = null;
	private byte []nativeSketchBeforeEdit = null;
	private CompoundCreationHandler compoundCreationHandler = null;

	private NotebookPageModel pageModel;
	private boolean isLoading = false;
	
	public BatchContainer(boolean displayToolbar) {
		isLoading = true;
		this.displayToolbar = displayToolbar;
		initGUI();
		isLoading = false;
	}

	public BatchContainer(boolean displayToolbar, NotebookPageModel pageModel) {
		this(false);
		this.pageModel = pageModel;
	}

	public BatchContainer(boolean displayToolbar, NotebookPageModel pageModel,
			ProductBatchModel productBatchModel, BatchEditPanel batchEditPanel) {
		isLoading = true;

		this.readOnly = !pageModel.isEditable() || !productBatchModel.isEditable();
		this.displayToolbar = displayToolbar;
		this.productBatchModel = productBatchModel;
		this.batchEditPanel = batchEditPanel;
		this.pageModel = pageModel;
		initGUI();
		isLoading = false;
	}

	public BatchContainer(boolean b, NotebookPageModel pageModel, ProductBatchModel productBatchModel2, BatchEditPanel batchEditPanel2, CompoundCreationHandler compoundCreationHandler) {
		this(b, pageModel, productBatchModel2,batchEditPanel2);
		this.compoundCreationHandler = compoundCreationHandler;
	}

	public void dispose() {
		this.pageModel = null;
		this.intendedScheme = null;
		this.intendedStep = null;
	}

	public void initGUI() {
		try {
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setPreferredSize(new java.awt.Dimension(517, 200));
			this.setBorder(new MatteBorder(new Insets(1, 1, 1, 1), new java.awt.Color(0, 0, 0)));
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	public void postInitGUI() {
		try {
			String content = "Batch";
			if (this.productBatchModel != null)
				content += (" number: " + productBatchModel.getBatchNumberAsString());
			
			reactionCanvas = new ChemistryViewer(MasterController.getGUIComponent().getTitle(), content) {

				private static final long serialVersionUID = -1804941235953975709L;
				
				private List<MyMenuItem> intendedItems;
				
				protected JPopupMenu createPopupMenu() {
					JPopupMenu popupMenu = super.createPopupMenu();
					if (!readOnly) {
						if (intendedItems == null) {
							intendedItems = new ArrayList<MyMenuItem>();
						}
						popupMenu.addSeparator();
						if (pageModel != null) {
							List<ReactionStepModel> stepModel = pageModel.getReactionSteps();
							for (ReactionStepModel reactionStepModel : stepModel) {
								List<ProductBatchModel> batchModel = reactionStepModel.getAllIntendedProductBatchModelsInThisStep();
								for (ProductBatchModel productBatchModel : batchModel) {
									MyMenuItem item = new MyMenuItem(productBatchModel);
									intendedItems.add(item);
									popupMenu.add(item);								
								}
							}
						}	
					}
					return popupMenu;
				}

				protected void checkMenuItems() {
					super.checkMenuItems();
					if (readOnly && intendedItems != null) {
						for (MyMenuItem item : intendedItems) {
							popupMenu.remove(item);
						}
					}
				}
			};
			
			reactionCanvas.setEditorType(MasterController.getGuiController().getDrawingTool());
			reactionCanvas.setReadOnly(readOnly);
			
			add(reactionCanvas, BorderLayout.CENTER);
			
			if (!readOnly) {
				reactionCanvas.addChemistryEditorListener(this);
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
		
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
							|| intendedStep.getRxnProperties().length() == 0) {
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "There are no Reaction Properties.",
								"Reaction Properties", JOptionPane.INFORMATION_MESSAGE);
					} else {
						MsgBox.showMsgBox(MasterController.getGUIComponent(), "Reaction Properties", intendedStep
								.getRxnProperties());
					}
				}
			});
		}
		if (productBatchModel != null) {
			refreshChemitry();			
		}	
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		if (this.reactionCanvas != null) {
			this.reactionCanvas.setReadOnly(readOnly);
		}
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public void refreshChemitry() {
		isLoading = true;
		String stringSketch = this.productBatchModel.getCompound().getStringSketchAsString();
		if (StringUtils.isNotBlank(stringSketch)) {
			int index1 = stringSketch.indexOf("M  END");
			int index2 = stringSketch.indexOf("AccObj");			
			if (index1 == -1 && index2 == -1) {
				stringSketch = Decoder.decodeString(stringSketch);
			} else {
				if (index2 != -1) {
					byte[] sketch = productBatchModel.getCompound().getStringSketch();
					try {
						if (chemDelegate == null) {
							chemDelegate = new ChemistryDelegate();
						}
						sketch = chemDelegate.convertChemistry(sketch, "", "MDL Molfile");
						stringSketch = new String(sketch);
					} catch (Exception e) {
						CeNErrorHandler.getInstance().logExceptionMsg(this, e);
					}
				}
			}			
		}
		reactionCanvas.setChemistry(stringSketch.getBytes());
		isLoading = false;
	}
	
	public void setReactionStep(ReactionStepModel step) {
		intendedStep = step;
	
		intendedScheme = step.getRxnScheme();
		
		if (intendedStep != null) {			
			byte[] nativeSktch = getNativeSketch();
			if (nativeSktch == null || nativeSktch.length==0) {
				setStringFormatForChime();
			} else {
				reactionCanvas.setChemistry(nativeSktch);
			}
			reactionCanvas.setReadOnly(readOnly);
		} else{
			dispose();
		}
	}

	public void editingStarted(ChemistryEditorEvent structureeditorevent) {
	}

	public synchronized void structureChanged(ChemistryEditorEvent structureeditorevent) {
		if (isLoading || productBatchModel == null) {
			return;
		}
		if (newString(reactionCanvas.getChemistry(ChemistryEditorProxy.MOL_FORMAT)).equals(newString(productBatchModel.getCompound().getStringSketch()))) {
			return;
		}
		batchEditPanel.clearVnV();
		updateCompoundStructure(structureeditorevent);
		pageModel.setModelChanged(true);
		productBatchModel.setModelChanged(true);
		batchEditPanel.populate();
	}
	
	private String newString(byte[] src) {
		if(src == null) src = new byte[0];
		return new String(src);
	}

	public void editingStopped(ChemistryEditorEvent structureeditorevent) {
		enableSaveButtons();
	}

	private void setStringFormatForChime() {
		try {
			reactionCanvas.setChemistry(ChimeUtils.toMoleFileFormatFromChime(intendedStep.getRxnScheme().getStringSketchAsString()).getBytes());
		} catch (Exception e) {
			log.error("Failed to format string for intended reaction step: " + intendedStep.getStepNumber() + " for page: " + pageModel.getNotebookRefAsString(), e);
		}
	}
	
	public byte[] getNativeSketch() {
		if (intendedScheme == null) {
			return new byte[0];
		} else {
			return intendedScheme.getNativeSketch();
		}
	}

	public void setNativeSketch(byte[] sketch, int format) {
		intendedScheme.setNativeSketch(sketch);
		intendedScheme.setNativeSketchFormat("" + format);
	}
	
	private void enableSaveButtons() {
		MasterController.getGUIComponent().enableSaveButtons();
	}
	
	public byte[] getSearchSketch() {
		if (intendedScheme == null) {
			return new byte[0];
		} else {
			return intendedScheme.getStringSketch();
		}
	}

	public void setSearchSketch(byte[] sketch) {
		intendedScheme.setStringSketch(sketch);
	}

	public ChemistryViewer getReactionViewer() {
		return reactionCanvas;
	}
	
	public void clearSketch() {
		if (reactionCanvas != null) {
			isLoading = true;
			reactionCanvas.setChemistry(null);
			reactionCanvas.repaint();
			isLoading = false;
		}
	}
	
	public void updateCompoundStructure(ChemistryEditorEvent structureeditorevent) {
		ParentCompoundModel c = null;

			if (productBatchModel == null) {
				return;
			}
			if (!readOnly && productBatchModel.getCompound() != null ) {
				AmountModel amountModel = productBatchModel.getMolecularWeightAmount();
				if(amountModel != null) {
					productBatchModel.setMolecularWeightAmount(new AmountModel(amountModel.getUnitType(), 0));
					productBatchModel.setMolecularFormula(null);
				}

				c = productBatchModel.getCompound();

				 //save the previous structure before overwriting with changes
				nativeSketchBeforeEdit = c.getStringSketch();
				try {	
				StructureLoadAndConversionUtil.loadSketch(reactionCanvas.getChemistry(ChemistryEditorProxy.MOL_FORMAT), reactionCanvas.getEditorType(), true, "MDL Molfile",c);

				batchEditPanel.populate();
				//Refresh the Molstring map.
				BatchAttributeComponentUtility.getCachedProductBatchesToMolstringsMap(productBatchModel, pageModel, true);
				
				// launch the cct checker thread only if there are
				 //products
				if (productBatchModel.getCompound().getStringSketch() != null) {
					 //use worker thread to run this
					SwingWorker calcDelegateProcess = new SwingWorker() {
						public Object construct() {
							launchChloracnegenCheckerForBatches(nativeSketchBeforeEdit);
							return null;
						}

						public void finished() {
						}
					};
					calcDelegateProcess.start();
				}
				productBatchModel.recalcAmounts();
			 } catch (ChemUtilInitException e) {
				 CeNErrorHandler.getInstance().logExceptionMsg(this,
						 "Could not load Sketch due to a failure to initialize the Chemistry Service", e);
			 } catch (ChemUtilAccessException e) {
				 CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGuiComponent(),
						 "Error getting molecular information. Seems like this is not valid molecule", e);
			}
			MasterController.getGUIComponent().enableSaveButtons();
			if (compoundCreationHandler != null) {
				compoundCreationHandler.fireCompoundUpdated(new CompoundCreationEvent(this, productBatchModel, null));
			}
		}
			
		batchEditPanel.updateBatchTable();
	}
	
	 //run chloracnegen test if the structure is updated
	 //this method launches chloracnegen test only for intended products
	public void launchChloracnegenCheckerForBatches(byte[] nativeSketchBeforeEdit) {
		final String progressStatus = "Running Chloracnegen Prediction ...";
		CeNJobProgressHandler.getInstance().addItem(progressStatus);
		//initialize ChemistryDelegate
		if (chemDelegate == null) {
			chemDelegate = new ChemistryDelegate();
		}
		try {
			List<ChloracnegenBatchStructure> list = new ArrayList<ChloracnegenBatchStructure>();
			 //Get the native structure
			byte[] nativeStruct = productBatchModel.getCompound().getStringSketch();
			 //launch checker if the old/new struc aren't same
			if (chemDelegate.areMoleculesEqual(nativeSketchBeforeEdit, nativeStruct)) {
				return;
			}
			 //convert nativestruc into mol struc
			byte[] molFile = chemDelegate.convertChemistry(nativeStruct, "", "MDL Molfile");

			// make a sync call to cct
			String molStr = new String(molFile);
			ChloracnegenBatchStructure cbatch = new ChloracnegenBatchStructure(nativeStruct, molFile, productBatchModel.getBatchType());
			cbatch.setNBKBatchNumber(productBatchModel.getBatchNumberAsString());
			Structure struc = ChloracnegenPredictor.getInstance().checkChloracnegen(molStr);
			if (struc != null && struc.isChloracnegenicStructure())  //only if class 1,class2	 display
			{
				productBatchModel.setChloracnegenFlag(true);
				productBatchModel.setChloracnegenType(struc.getResults());
				productBatchModel.setTestedForChloracnegen(true);
				cbatch.setChloracnegenFlag(true);
				cbatch.setChloracnegenType(struc.getResults());
				list.add(cbatch);
			} else if (struc != null) {
				productBatchModel.setChloracnegenFlag(false);
				productBatchModel.setChloracnegenType(struc.getResults());
				productBatchModel.setTestedForChloracnegen(true);
				cbatch.setChloracnegenFlag(true);
				cbatch.setChloracnegenType(struc.getResults());
			}
			// now alert the user with a JDialog
			if (list.size() > 0) {
				alertAboutChloracnegens(list);
			}
		} catch (Exception e) {
			log.error("Problem launching Chloracnegen Checker for batches.", e);
		} finally {
			CeNJobProgressHandler.getInstance().removeItem(progressStatus);
		}
	} //end of method

	private class MyMenuItem extends JMenuItem {
		
		private static final long serialVersionUID = -3601954772400153912L;

		MyMenuItem(final ProductBatchModel productBatchModel) {
			super("P"+productBatchModel.getStoicTransactionOrder());
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					reactionCanvas.setChemistry(productBatchModel.getCompound().getStringSketch());
				}
			});
		}		
	}
	
	private void alertAboutChloracnegens(List<ChloracnegenBatchStructure> strucList) {
		if (strucList != null && strucList.size() > 0) {
			ChloracnegenResultsViewContainer ui = new ChloracnegenResultsViewContainer(MasterController.getGUIComponent());
			ui.addStructures(strucList);
			ui.showGUI();
		}
	}
}
