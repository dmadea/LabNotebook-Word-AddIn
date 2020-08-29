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
import com.chemistry.enotebook.client.gui.chlorac.SDFileChloracnegenTester;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.batch.BatchEditPanel;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionEvent;
import com.chemistry.enotebook.client.gui.page.batch.CompoundCreationEvent;
import com.chemistry.enotebook.client.gui.page.batch.CompoundCreationHandler;
import com.chemistry.enotebook.client.gui.page.batch.events.CompoundCreatedEventListener;
import com.chemistry.enotebook.client.gui.page.batch.table.ProductTablePopupMenuManager;
import com.chemistry.enotebook.client.gui.page.experiment.CompoundCreateInterface;
import com.chemistry.enotebook.client.gui.page.table.PCeNProductTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableViewToolBar;
import com.chemistry.enotebook.client.utils.ReactionStepModelUtils;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchTypeFactory;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.utils.CeNJobProgressHandler;
import com.chemistry.enotebook.utils.SDFileGeneratorUtil;
import com.chemistry.enotebook.utils.StructureLoadAndConversionUtil;
import com.chemistry.enotebook.utils.SwingWorker;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConceptionCompoundInfoContainer extends JPanel implements CompoundCreateInterface, CompoundCreatedEventListener {
	// public static final String BATCHTABLE_PANE = "Product Nbk Batchs";
	// public static final String BATCHDETAILS_PANE = "Product Batch Details";

	private static final long serialVersionUID = 1415773244148886313L;
	
	protected NotebookPageModel pageModelPojo;
	private BatchEditPanel batchSelectionListener;

	private PCeNTableView productTableView;
	private PCeNTableModel parallelCeNTableModel;
	private ConceptionCeNProductBatchTableViewControllerUtility connector;
	private ProductTablePopupMenuManager conceptionProductTablePopupMenuManager = new ProductTablePopupMenuManager(this);
	private BorderLayout borderLayout = new BorderLayout();

	private JButton registerVCBtn = new JButton("Register VC");
    private JButton vnvBtn = new JButton("VnV");
	private JButton addNewCompoundBtn = new JButton("Add New Compound");
	protected JButton deleteCompoundBtn = new JButton("Delete Compound(s)");	
	private JButton importSDFileBtn = new JButton("Import SD File");
	private CompoundCreationHandler compoundCreationHandler;

	public ConceptionCompoundInfoContainer(NotebookPageModel pageModelPojoe, BatchEditPanel bBatchSelectionListener,
			CompoundCreationHandler compoundCreationHandler) 
	{
		pageModelPojo = pageModelPojoe;
		batchSelectionListener = bBatchSelectionListener;		
		initGUI();
		this.compoundCreationHandler = compoundCreationHandler;
	}

	public void initGUI() 
	{
		try {
			this.setLayout(borderLayout);
			if (pageModelPojo.getSummaryReactionStep().getProducts().size() > 0) {
				List<BatchesList<ProductBatchModel>> batchesListList = pageModelPojo.getSummaryReactionStep().getProducts();
				List<ProductBatchModel> batchesList = new ArrayList<ProductBatchModel>();
				for (int i=0; i< batchesListList.size(); i++){
					BatchesList<ProductBatchModel> tempBatchesList = batchesListList.get(i);
					for ( int j=0; j<tempBatchesList.getBatchModels().size(); j++)
						batchesList.add(tempBatchesList.getBatchModels().get(j));
				}
				connector = new ConceptionCeNProductBatchTableViewControllerUtility(batchesList, pageModelPojo);
				// connector = new ParallelCeNProductBatchTableViewControllerUtility(pageModelPojo.getSummaryReactionStep().getProducts());
			} else {
				connector = new ConceptionCeNProductBatchTableViewControllerUtility(new ArrayList<ProductBatchModel>(), pageModelPojo);
			}
			
			// List abstractBatches = tableController.getProductBatches();
			// String[] headerNames2 = mParallelCeNProductBatchTableViewController.getHeaderNames();
			parallelCeNTableModel = new PCeNTableModel(connector);
			productTableView = new ConceptionProductTableView(parallelCeNTableModel, 70, connector, null, pageModelPojo);
			// vb 7/6

// For the case when it's not possible to remove batches in conception experiments
//			BatchSelectionListener[] listeners = new BatchSelectionListener[2];
//			listeners[0] = batchSelectionListener;
//			listeners[1] = new BatchSelectionListener() {
//				public void batchSelectionChanged(BatchSelectionEvent event) {
//					if (event == null)
//						return;
//
//					Object obj = event.getSubObject();
//					ProductBatchModel batch = null;
//					if (obj instanceof PlateWell) {
//						PlateWell well = (PlateWell) obj;
//						batch = (ProductBatchModel) well.getBatch();
//					} else if (obj instanceof ProductBatchModel) {
//						batch = (ProductBatchModel) obj;
//					}
//					if (batch != null) {
//						if (batch.isUserAdded())
//							setDeleteCompoundButtonEnabled(true);
//						else
//							setDeleteCompoundButtonEnabled(false);
//					}
//				}
//			};
//			productTableView.setProductBatchDetailsContainerListenerList(listeners);
//
			
			productTableView.setProductBatchDetailsContainerListener(this.batchSelectionListener);
			conceptionProductTablePopupMenuManager.addMouseListener(productTableView, pageModelPojo.getSummaryReactionStep());
			registerVCBtn.setEnabled(false);

            registerVCBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    registerVC();
                }
            });

            vnvBtn.setEnabled(false);
            vnvBtn.setToolTipText("Auto assign stereoisomer codes");
            vnvBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    doVnv();
                }
            });

			addNewCompoundBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					createCompound();
				}
			});
			
			deleteCompoundBtn.setEnabled(true);
			deleteCompoundBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					deleteCompound();
				}
			});			
			
			importSDFileBtn.addActionListener(new ActionListener()	{
				public void actionPerformed(ActionEvent evt) {
					final JFileChooser fileChooser = new JFileChooser();
					int returnVal = fileChooser.showOpenDialog(MasterController.getGUIComponent());
					if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File file = fileChooser.getSelectedFile();
		                importSDFile(file);
		            } 			
				}
			});

			this.setPreferredSize(new java.awt.Dimension(550, 350));
			JScrollPane scrollTableViewPane = new JScrollPane(productTableView);
			setBackground(Color.LIGHT_GRAY);
			
			PCeNTableViewToolBar hideStructToolBar = new PCeNTableViewToolBar(productTableView);

			JPanel toolBar = new JPanel(new BorderLayout());
			toolBar.add(hideStructToolBar, BorderLayout.WEST);
			toolBar.add(createToolBarElements(), BorderLayout.EAST);
			
			add(toolBar, BorderLayout.NORTH);			
			add(scrollTableViewPane, BorderLayout.CENTER);
			
			scrollTableViewPane.setPreferredSize(new java.awt.Dimension(500, 300));
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, "Error occurred while initializing the Batch tables", e);
		}
		refreshPageModel(pageModelPojo);
	}

	public void updateRegisterVCButton(BatchModel batchModel) {
		if (batchModel != null && batchModel.getCompound() != null && StringUtils.isEmpty(batchModel.getCompound().getVirtualCompoundId())) {
			if (pageModelPojo.isEditable()) {
				registerVCBtn.setEnabled(true);
			} else {
				registerVCBtn.setEnabled(false);
			}
		} else {
			registerVCBtn.setEnabled(false);
		}
	}
	
	public PCeNTableView getProductTableView() {
		return productTableView;
	}
	
	private List<ProductBatchModel> getCheckboxedCompounds() {
		List<ProductBatchModel> checkboxedCompounds = new ArrayList<ProductBatchModel>();		
		List<?> compoundsList = productTableView.getConnector().getAbstractBatches();
		for(Object o : compoundsList) {
			if(o != null && o instanceof ProductBatchModel) {
				ProductBatchModel batchModel = (ProductBatchModel)o;
				if(batchModel.isSelected()) {
					checkboxedCompounds.add(batchModel);
				}
			}
		}		
		return checkboxedCompounds;
	}

	protected void registerVC() {
	        final String progressStatus = "Registering virtual compound...";
	        try {
	        	CeNJobProgressHandler.getInstance().addItem(progressStatus);	        	
	    		new SwingWorker() {
	    			public Object construct() {
			        	batchSelectionListener.handleVCRegMultiple(getCheckboxedCompounds());
			        	batchSelectionChanged(null);
			        	return null;
	    			}
	    			public void finished() {
	    				productTableView.validate();
	    				productTableView.repaint();
	    			}
	    		}.start();	        	
	        } finally{
	        	CeNJobProgressHandler.getInstance().removeItem(progressStatus);	        	
	        }
	}

    protected void doVnv() {
        final String progressStatus = "Performing VnV for virtual compounds...";
        try {
            CeNJobProgressHandler.getInstance().addItem(progressStatus);
            new SwingWorker() {
                public Object construct() {
                    batchSelectionListener.handleVnVMultiple(getCheckboxedCompounds());
                    batchSelectionChanged(null);
                    return null;
                }

                public void finished() {
                    productTableView.validate();
                    productTableView.repaint();
                }
            }.start();
        } finally {
            CeNJobProgressHandler.getInstance().removeItem(progressStatus);
        }
    }

	
	public void deleteCompound() {
		List<ProductBatchModel> selected = getCheckboxedCompounds();
		if(selected == null || selected.isEmpty()) {
			JOptionPane.showMessageDialog(MasterController.getGuiComponent(), "No selected compounds.", MasterController.getGUIComponent().getTitle(), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if(deleteConfirmation(selected.size())) {
			for(ProductBatchModel batchModel : selected) {
				internalDeleteCompound(batchModel);
			}
			enableSaveButton();
		}
	}

	public void deleteCompound(ProductBatchModel productBatchModel) {
		if(deleteConfirmation(1)) {
			internalDeleteCompound(productBatchModel);
			enableSaveButton();
		}
	}
	
	private boolean deleteConfirmation(int selectedCompounds) {
		String compoundWord = "compound" + (selectedCompounds > 1 ? "s" : "");
		int result = JOptionPane.showConfirmDialog(MasterController.getGuiComponent(), 
        		"Do you want to delete " + (selectedCompounds > 1 ? "these " + selectedCompounds + " " : "this ") + compoundWord + "?", 
        		"Delete " + compoundWord,
        		JOptionPane.YES_NO_OPTION,
        		JOptionPane.WARNING_MESSAGE);
		return result == JOptionPane.YES_OPTION;
	}

	private void internalDeleteCompound(BatchModel batch) {
		List<ReactionStepModel> reactionSteps = pageModelPojo.getReactionSteps();
		// The added compound should be in the last step
		ReactionStepModel reactionStepModel = (ReactionStepModel) reactionSteps.get(reactionSteps.size() - 1);
		List<BatchesList<ProductBatchModel>> products = reactionStepModel.getProducts();
		
		if (batch != null) {
			BatchesList<ProductBatchModel> batchesList = pageModelPojo.getUserAddedBatchesList();

			if (!batch.isLoadedFromDB()) {
				batchesList.removeBatch(batch);
			} else {
				batch.markToBeDeleted(true);
			}

			if (batchesList.getBatchModels().size() == 0 && batchesList.isLoadedFromDB() == false) {
				products.remove(batchesList);
			}
			
			productTableView.stopEditing();
			PCeNProductTableModelConnector controller = (PCeNProductTableModelConnector) productTableView.getConnector();
			controller.removeProductBatchModel((ProductBatchModel) batch); // PCeNProductsTableModelController
			PCeNTableModel model = (PCeNTableModel) productTableView.getModel();
			model.fireTableDataChanged();

			batch.setToDelete(true);
			batch.setModelChanged(true);
			pageModelPojo.setModelChanged(true);
			pageModelPojo.getPseudoProductPlate(false).removeBatch((ProductBatchModel) batch);
			
			batchSelectionListener.batchSelectionChanged(new BatchSelectionEvent(this, null));

			CompoundCreationEvent event = new CompoundCreationEvent(this, batch, reactionStepModel);
			fireCompoundRemoved(event);
		}
	}

	protected void importSDFile(File file) {
		FileInputStream inputStream = null;
		ProductBatchModel[] batchModels = null;

		try {
			inputStream = new FileInputStream(file);
			SDFileGeneratorUtil fileGenerator = new SDFileGeneratorUtil();
			batchModels = fileGenerator.getProductBatchModelsFromSDFile(inputStream);
			// Do all necessary conversions
			for (int i = 0; i < batchModels.length; i++) {
				StructureLoadAndConversionUtil.loadSketch(batchModels[i].getCompound().getStringSketch(),
						0, batchModels[i].getCompound()); // Default (0) is SDfile.
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
							"Invalid file selected. Please check the file and fix errors.");
			ex.printStackTrace();
			return;
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		SDFileChloracnegenTester.launchChloracnegenCheckerForBatches(batchModels);
			
		createCompound(batchModels, true);

		// Assign flags and other properties
		// Trigger event. Refer CreateCompound() functionality.
	}

	private JPanel createToolBarElements() {
		JPanel equivsPanel = new JPanel();
		equivsPanel.add(registerVCBtn);
        equivsPanel.add(vnvBtn);
		equivsPanel.add(addNewCompoundBtn);
		equivsPanel.add(deleteCompoundBtn);
		equivsPanel.add(importSDFileBtn);		
		return equivsPanel;
	}

	public void switchToProductsFromPageModel(NotebookPageModel pageModel) {
		List<ReactionStepModel> reactionSteps = pageModel.getReactionSteps();
		if (reactionSteps == null || reactionSteps.size() < 1) {
			// log no products error
			return;
		}

		// read the last step products
		ReactionStepModel reactionStep = (ReactionStepModel) reactionSteps.get(0);
		List<ProductBatchModel> batches = reactionStep.getAllActualProductBatchModelsInThisStep();

        connector.removeAllBatches();

        for (Iterator<ProductBatchModel> it = batches.iterator(); it.hasNext();) {
			connector.addProductBatchModel((ProductBatchModel)it.next());
		}

		parallelCeNTableModel.fireTableDataChanged();

		batchSelectionChanged(null);
		pageModelPojo.getSummaryReactionStep().setModelChanged(true);

		productTableView.highlightLastRow();
		
		enableSaveButton();
	}
	
	public void batchSelectionChanged(BatchModel batchModel) {
		BatchSelectionEvent event = new BatchSelectionEvent(this, batchModel);
		batchSelectionListener.batchSelectionChanged(event);
	}
	
	public void selectBatch(String batchNumber) {
		if(batchNumber != null) {
			List<?> compoundsList = productTableView.getConnector().getAbstractBatches();
			int batchRowNo = -1;
			int index = -1;
			for(Object o : compoundsList) {
				if(o != null && o instanceof ProductBatchModel) {
					ProductBatchModel batchModel = (ProductBatchModel)o;
					index++;
					if(batchNumber.equals(getButchNumberString(batchModel.getBatchNumber()))) {
						batchRowNo = index;
					}
				}
			}
			productTableView.scrollRectToVisible(new Rectangle(10 * 10,productTableView.getRowHeight()*(batchRowNo),5,productTableView.getRowHeight()));
			if (batchRowNo > 0)
				productTableView.setRowSelectionInterval(batchRowNo - 1, batchRowNo);
			else
				productTableView.setRowSelectionInterval(batchRowNo, batchRowNo);
			
			productTableView.valueChanged();
		}
	}
	
	private String getButchNumberString(BatchNumber batchNumber) {
		StringBuilder result = new StringBuilder();
		return result.append(batchNumber.getNbRef().getNbNumber())
					 .append("-")
					 .append(batchNumber.getNbRef().getNbPage())
					 .append("-")
					 .append(batchNumber.getLotNumber())
				.toString();
	}

	private void enableSaveButton() {
		pageModelPojo.setModelChanged(true);
		MasterController.getGUIComponent().enableSaveButtons();
	}
	
	public void addCompoundCreatedEventListener(CompoundCreatedEventListener l) {
		compoundCreationHandler.addCompoundCreatedEventListener(l);
	}

	public void removeCompoundCreatedEventListener(CompoundCreatedEventListener l) {
		compoundCreationHandler.removeCompoundCreatedEventListener(l);
	}

	protected void fireNewCompoundCreated(CompoundCreationEvent event) {
		compoundCreationHandler.fireNewCompoundCreated(event);
	}

	public void fireCompoundRemoved(CompoundCreationEvent event) {
		if (compoundCreationHandler != null)
			compoundCreationHandler.fireCompoundDeleted(event);
	}

	public void createCompound(ProductBatchModel[] productBatchModels, boolean fromSDFile) {
		ProductBatchModel newBatch = null;
		ReactionStepModel step = pageModelPojo.getSummaryReactionStep();
		BatchesList<ProductBatchModel> batchesList = pageModelPojo.getUserAddedBatchesList();
		
	    for (int i=0; i<productBatchModels.length; i++) {
			newBatch = productBatchModels[i];
			try {
				BatchNumber batchNumber = pageModelPojo.getNextBatchNumberForSingletonProductBatch();
				newBatch.setBatchNumber(batchNumber);
				newBatch.setBatchType(BatchTypeFactory.getBatchType(CeNConstants.BATCH_TYPE_ACTUAL));
				newBatch.setUserAdded(true);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Compound number creation failed.");
				ex.printStackTrace();
				return;
			}
			batchesList.addBatch(newBatch);
			String owner = null;
			try {
				owner = MasterController.getUser().getPreference(NotebookUser.PREF_LastOwner);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (owner == null || owner.length() == 0)
				newBatch.setOwner(pageModelPojo.getPageHeader().getUserName());
			else
				newBatch.setOwner(owner);

			newBatch.setSynthesizedBy(pageModelPojo.getPageHeader().getUserName());
			newBatch.getCompound().setCreatedByNotebook(true);
			newBatch.setLoadedFromDB(false);
			
			if (!fromSDFile) {
				newBatch.getCompound().setVirtualCompoundId("");
				newBatch.getRegInfo().resetRegistrationInfo();
				// TODO - workaround for duplicated batches 
				newBatch.getCompound().setStereoisomerCode("");
			}
			
			newBatch.setSelected(false);
			
			//Ask Sumant and set the Stoich attributes. Jags_todo... 
			/*		newBatch.setPrecursors(stoichModel.getPreCursorsForReaction());*/
			newBatch.setProductFlag(true);
			StoicModelInterface stoichModel = ReactionStepModelUtils.findLimitingReagent(step);				
			if (stoichModel != null) {
				newBatch.setTheoreticalMoleAmountFromStoicAmount(stoichModel.getStoicMoleAmount());
			}
			parallelCeNTableModel.fireTableDataChanged();
			if (compoundCreationHandler != null) {
				CompoundCreationEvent event = new CompoundCreationEvent(this, newBatch, step);
				fireNewCompoundCreated(event);
			}
	    }
	    batchSelectionChanged(newBatch);
		pageModelPojo.getSummaryReactionStep().setModelChanged(true);
		productTableView.highlightLastRow();
		//registerVCBtn.setEnabled(true); Disable it for RC
		productTableView.scrollRectToVisible(new Rectangle(10 * 10,productTableView.getRowHeight()*(productTableView.getRowCount()),5,productTableView.getRowHeight()));
		enableSaveButton();
        final boolean isEditable = pageModelPojo.isEditable();
        registerVCBtn.setEnabled(isEditable);
        vnvBtn.setEnabled(isEditable);
	}

    public void setRegisterCompoundButtonEnabled(boolean b) {
        registerVCBtn.setEnabled(b);
    }

    public void setVnvButtonEnabled(boolean b) {
        vnvBtn.setEnabled(b);
    }

	public void compoundRemoved(CompoundCreationEvent event) {
		// TODO Auto-generated method stub
	}

	public void compoundUpdated(CompoundCreationEvent event) {
		// TODO Auto-generated method stub
	}

	public void newCompoundCreated(CompoundCreationEvent event) {
		ProductBatchModel batchModel = (ProductBatchModel)event.getBatch();
		PCeNTableModel summaryModel = (PCeNTableModel) productTableView.getModel();
		ConceptionCeNProductBatchTableViewControllerUtility connector = (ConceptionCeNProductBatchTableViewControllerUtility) summaryModel.getConnector();
		connector.addProductBatchModel(batchModel);
		summaryModel.fireTableDataChanged();
	}

	public void createCompound() {
		ProductBatchModel[] productBatchModels = new ProductBatchModel[1];
		productBatchModels[0] = new ProductBatchModel();
		createCompound(productBatchModels, false);
	}

	public void refreshPageModel(NotebookPageModel pageModelPojo2) {
		boolean isEditable = pageModelPojo2.isEditable();
		registerVCBtn.setEnabled(isEditable);
        vnvBtn.setEnabled(isEditable);
		addNewCompoundBtn.setEnabled(isEditable);
		deleteCompoundBtn.setEnabled(isEditable);	
		importSDFileBtn.setEnabled(isEditable);
	}

	public void duplicateCompound(ProductBatchModel batch) {
		ProductBatchModel newBatch = new ProductBatchModel();
		newBatch.deepCopy(batch);  // RegInfo copied in deepCopy()
		createCompound(new ProductBatchModel[] { newBatch }, false);
	}

	public void syncIntendedProducts() {
		// Not implemented
	}

	public void updateSyncWithIntendedProductsActionState() {
		// TODO Auto-generated method stub
		
	}	
}
