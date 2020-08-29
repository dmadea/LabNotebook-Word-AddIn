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

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.page.batch.events.CompoundCreatedEventListener;
import com.chemistry.enotebook.client.gui.page.batch.table.ProductTablePopupMenuManager;
import com.chemistry.enotebook.client.gui.page.batch.table.SingletonCeNProductBatchTableViewControllerUtility;
import com.chemistry.enotebook.client.gui.page.experiment.CompoundCreateInterface;
import com.chemistry.enotebook.client.gui.page.experiment.table.ParallelCeNTableViewToolBar;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.client.utils.ReactionStepModelUtils;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchTypeFactory;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

//public class SingletonBatchInfoContainer extends JPanel implements CompoundCreateInterface, CompoundCreatedEventListener {
public class SingletonBatchInfoContainer extends JPanel implements CompoundCreateInterface, CompoundCreatedEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1108461902178097123L;

	private static final Log log = LogFactory.getLog(SingletonBatchInfoContainer.class);
	// public static final String BATCHTABLE_PANE = "Product Nbk Batchs";
	// public static final String BATCHDETAILS_PANE = "Product Batch Details";

	private JPanel productTablePanel;
	protected NotebookPageModel pageModelPojo;
	private BatchSelectionListener batchSelectionListener;

	private PCeNTableView productTableView;
	PCeNTableModel parallelCeNTableModel;
	private ParallelCeNTableViewToolBar productTableViewToolBar;
	private PCeNTableModelConnector connector;
	private ProductTablePopupMenuManager singletonProductTablePopupMenuManager = new ProductTablePopupMenuManager(this);
	private BorderLayout borderLayout = new BorderLayout();

	private JButton syncIntendedProductsBtn = new JButton("Sync with Intended Products");
	private JButton addNewCompoundBtn = new JButton("Add New Batch");
	protected JButton deleteCompoundBtn = new JButton("Delete Batch");
	private JButton importSDFileBtn = new JButton("Import SD File");
	private CompoundCreationHandler compoundCreationHandler;

	public SingletonBatchInfoContainer(NotebookPageModel pageModelPojoe, BatchSelectionListener bBatchSelectionListener,
			CompoundCreationHandler compoundCreationHandler) {
		pageModelPojo = pageModelPojoe;
		this.batchSelectionListener = bBatchSelectionListener;  // vb 7/6
		initGUI();
		this.compoundCreationHandler = compoundCreationHandler;
	}

	public void initGUI() {
		try {
			this.setLayout(borderLayout);
			if (pageModelPojo.getSummaryReactionStep().getProducts().size() > 0) {
				ArrayList<BatchesList<ProductBatchModel>> batchesListList = pageModelPojo.getSummaryReactionStep().getProducts();
				ArrayList<ProductBatchModel> batchesList = new ArrayList<ProductBatchModel>();
				for (int i=0; i< batchesListList.size(); i++){
					BatchesList<ProductBatchModel> tempBatchesList = batchesListList.get(i);
					if (tempBatchesList.getPosition() != null && (tempBatchesList.getPosition().equals(CeNConstants.PRODUCTS_SYNC_INTENDED) || tempBatchesList.getPosition().equals(CeNConstants.PRODUCTS_USER_ADDED)))
						for ( int j=0; j<tempBatchesList.getBatchModels().size(); j++)
							batchesList.add(tempBatchesList.getBatchModels().get(j));
				}
				connector = new SingletonCeNProductBatchTableViewControllerUtility(batchesList, pageModelPojo);
				// connector = new ParallelCeNProductBatchTableViewControllerUtility(pageModelPojo
				// .getSummaryReactionStep().getProducts());
			} else
				connector = new SingletonCeNProductBatchTableViewControllerUtility(new ArrayList<ProductBatchModel>(), pageModelPojo);
			// List abstractBatches = tableController.getProductBatches();
			// String[] headerNames2 = mParallelCeNProductBatchTableViewController
			// .getHeaderNames();
			parallelCeNTableModel = new PCeNTableModel(connector);
			productTableView = new PCeNTableView(parallelCeNTableModel, 70, connector, null);
			// vb 7/6
			productTableView.setProductBatchDetailsContainerListener(this.batchSelectionListener);
			singletonProductTablePopupMenuManager.addMouseListener(productTableView, pageModelPojo.getSummaryReactionStep());
			productTableViewToolBar = new ParallelCeNTableViewToolBar(productTableView, createToolBarElements());
			syncIntendedProductsBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					syncIntendedProducts();
				}
			});
			if (pageModelPojo.getExistingSyncIntendedBatches() != null)
				syncIntendedProductsBtn.setEnabled(false);
			addNewCompoundBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					createCompound();
				}
			});
			deleteCompoundBtn.setEnabled(false); //Initially disabled, Enabled only when a row is selected.
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
			add(productTableViewToolBar, BorderLayout.NORTH);
			add(scrollTableViewPane, BorderLayout.CENTER);
			scrollTableViewPane.setPreferredSize(new java.awt.Dimension(500, 300));
			//scrollTableViewPane.setBounds(new java.awt.Rectangle(100, 7, 550, 200));


		} catch (Exception e) {
			// ceh.logExceptionMsg(this, "Error occurred while initializing the Batch tables", e);
			e.printStackTrace();
		}
	}

	public void syncIntendedProducts() {
		ArrayList<ProductBatchModel> productBatchesList = getIntendedProducts(pageModelPojo.getSingletonBatchs());
		if (productBatchesList.size() != 0)
		{
			ProductBatchModel newBatch, productBatchModel = null;
			for (int i=0; i<productBatchesList.size(); i++)
			{
				productBatchModel = productBatchesList.get(i);
				newBatch = BatchAttributeComponentUtility.syncBatch(productBatchModel, pageModelPojo.getSummaryReactionStep());
				newBatch.setBatchType(BatchType.getBatchType("ACTUAL"));
				newBatch.setParentKey(productBatchModel.getKey());
				BatchesList<ProductBatchModel> batchesList = pageModelPojo.getNewSyncIntendedBatchesList();
				batchesList.addBatch(newBatch);
				initializeAndDisplay(newBatch);
				//newBatch.setParentBatchNumber(productBatchModel.getBatchNumber().getBatchNumber());// May be required.
			}
			syncIntendedProductsBtn.setEnabled(false);
		}
		else
			JOptionPane.showMessageDialog( MasterController.getGUIComponent(), "There are no intended products exist to sync.");
	}

	private ArrayList<ProductBatchModel> getIntendedProducts(List<ProductBatchModel> singletonBatchs) {
		ArrayList<ProductBatchModel> productbatchModelList = new ArrayList<ProductBatchModel>();
		for (ProductBatchModel batchModel : singletonBatchs) {
			if (batchModel != null && batchModel.getBatchType().equals(BatchType.INTENDED_PRODUCT))  
			{
				productbatchModelList.add(batchModel);
			}
		}
		return productbatchModelList;
	}

	public void deleteCompound() {
		singletonProductTablePopupMenuManager.deleteBatch();
	}

	public void deleteCompound(ProductBatchModel batch) {
		deleteCompound();
	}

	protected void importSDFile(File file) {
		log.debug("import SD File name: " + file.getName());
		throw new RuntimeException("Feature not implemented.");

	}

	private JPanel createToolBarElements() {
		JPanel equivsPanel = new JPanel();
		equivsPanel.add(syncIntendedProductsBtn);
		equivsPanel.add(addNewCompoundBtn);
		equivsPanel.add(deleteCompoundBtn);
		equivsPanel.add(importSDFileBtn);
		return equivsPanel;
	}

	public void batchSelectionChanged(BatchModel batchModel) {
		ArrayList<BatchesList<ProductBatchModel>> products = this.pageModelPojo.getReactionStep(0).getProducts();
		if (products.size() == 0) {
			BatchSelectionEvent event = new BatchSelectionEvent(this, null);
			batchSelectionListener.batchSelectionChanged(event);
		}
		for (BatchesList<ProductBatchModel> batchesList : products) {
			List<ProductBatchModel> batchModels = batchesList.getBatchModels();
			for (ProductBatchModel productBatchModel : batchModels) {
				BatchSelectionEvent event = new BatchSelectionEvent(this, productBatchModel);
				batchSelectionListener.batchSelectionChanged(event);
			}
		}
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

	public void createCompound() {
		ProductBatchModel newBatch = new ProductBatchModel();
		initializeAndDisplay(newBatch);
		BatchesList<ProductBatchModel> batchesList = pageModelPojo.getUserAddedBatchesList();
		batchesList.addBatch(newBatch);
		batchSelectionChanged(null);
	}

	private void initializeAndDisplay(ProductBatchModel newBatch)
	{
		try {
			BatchNumber batchNumber = pageModelPojo.getNextBatchNumberForSingletonProductBatch();
			newBatch.setBatchNumber(batchNumber);
			newBatch.setBatchType(BatchTypeFactory.getBatchType(CeNConstants.BATCH_TYPE_ACTUAL));
			newBatch.setUserAdded(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Batch number creation failed.!");
			ex.printStackTrace();
			return;
		}
		ReactionStepModel step = pageModelPojo.getSummaryReactionStep();
		String owner = null;
		try {
			owner = MasterController.getUser().getPreference(NotebookUser.PREF_LastOwner);
		} catch (Exception e) {
		}
		if (owner == null || owner.length() == 0)
			newBatch.setOwner(pageModelPojo.getPageHeader().getUserName());
		else
			newBatch.setOwner(owner);

		newBatch.setSynthesizedBy(pageModelPojo.getPageHeader().getUserName());
		newBatch.getCompound().setCreatedByNotebook(true);
		//Ask Sumant and set the Stoich attributes. Jags_todo...
/*		newBatch.setPrecursors(stoichModel.getPreCursorsForReaction());*/
		newBatch.setProductFlag(true);
		
		newBatch.getCompound().setVirtualCompoundId("");
		newBatch.getRegInfo().resetRegistrationInfo();
		
		StoicModelInterface stoichModel = ReactionStepModelUtils.findLimitingReagent(step);
		if (stoichModel != null)
		{
			newBatch.getTheoreticalMoleAmount().setCalculated(true);
			newBatch.setTheoreticalMoleAmount(stoichModel.getStoicMoleAmount());
		}
		parallelCeNTableModel.fireTableDataChanged();
		if (this.compoundCreationHandler != null) {
			CompoundCreationEvent event = new CompoundCreationEvent(this, newBatch, null, null);
			compoundCreationHandler.fireNewCompoundCreated(event);
		}
		pageModelPojo.getSummaryReactionStep().setModelChanged(true);
		batchSelectionChanged(null);
		// the event is fired in the calling method
//		if (compoundCreationHandler != null)
//		{
//			CompoundCreationEvent event = new CompoundCreationEvent(this, newBatch, step);
//			fireNewCompoundCreated(event);
//		}
		productTableView.highlightLastRow();
		productTableView.scrollRectToVisible(new Rectangle(10 * 10,productTableView.getRowHeight()*(productTableView.getRowCount()),5,productTableView.getRowHeight()));
		setDeleteCompoundButtonEnabled(true);
		enableSaveButton();
	}

	public void setDeleteCompoundButtonEnabled(boolean b) {
		deleteCompoundBtn.setEnabled(b);

	}

	public void compoundRemoved(CompoundCreationEvent event) {

	}

	public void compoundUpdated(CompoundCreationEvent event) {

	}

	public void newCompoundCreated(CompoundCreationEvent event) {
		ProductBatchModel batchModel = (ProductBatchModel)event.getBatch();
		PCeNTableModel summaryModel = (PCeNTableModel) productTableView.getModel();
		SingletonCeNProductBatchTableViewControllerUtility connector = (SingletonCeNProductBatchTableViewControllerUtility) summaryModel
				.getConnector();
		connector.addProductBatchModel(batchModel);
		summaryModel.fireTableDataChanged();
	}

	public void selectGivenBatch(String batchNumber) {
		List<ReactionStepModel> reactionSteps = pageModelPojo.getReactionSteps();
		int batchRowNo = -1;
		ReactionStepModel reactionStep = reactionSteps.get(0);
		List<ProductBatchModel> productBatchModelList = reactionStep.getAllProductBatchModelsInThisStep();
		for (int j=0; j<productBatchModelList.size(); j++)
		{
			ProductBatchModel batchModel = productBatchModelList.get(j);
			if (batchModel.getBatchNumber().getBatchNumber().equals(batchNumber))
			{
				batchRowNo = j;
				break;
			}
		}
		productTableView.scrollRectToVisible(new Rectangle(10 * 10,productTableView.getRowHeight()*(batchRowNo),5,productTableView.getRowHeight()));

        if (batchRowNo > 0)
        	productTableView.setRowSelectionInterval(batchRowNo - 1, batchRowNo);
        else
        	productTableView.setRowSelectionInterval(batchRowNo, batchRowNo);
	}

	public void duplicateCompound(ProductBatchModel batch) {
		ProductBatchModel newBatch = new ProductBatchModel();
		newBatch.deepCopy(batch);
		newBatch.setRegInfo(new BatchRegInfoModel(newBatch.getKey()));
		initializeAndDisplay(newBatch);
		BatchesList<ProductBatchModel> batchesList = pageModelPojo.getUserAddedBatchesList();
		batchesList.addBatch(newBatch);
		batchSelectionChanged(newBatch);
	}

	public void updateSyncWithIntendedProductsActionState() {
		// TODO Auto-generated method stub
		
	}

	public void setVnvButtonEnabled(boolean b) {
		// Do nothing - we don't have vnv button here
	}
}
