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


import com.chemistry.enotebook.LoadServiceException;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.compoundaggregation.classes.ScreenPanel;
import com.chemistry.enotebook.compoundaggregation.classes.ScreenPanelSelector;
import com.chemistry.enotebook.domain.BatchSubmissionContainerInfoModel;
import com.chemistry.enotebook.domain.BatchSubmissionToScreenModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.datamodel.common.Amount2;
import com.chemistry.enotebook.utils.CeNDialog;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * USER1
 */
public class CompoundAggregationScreenPanelSelect extends CeNDialog {
	private static final long serialVersionUID = -4990465084310647736L;

	private static ScreenPanelSelector spSelector;
    private JButton btnSelect;
    private JButton btnCancel;
	private boolean isUniqueSubmittalSets = true;
	private NotebookPageModel pageModel;
	private static final Log log = LogFactory.getLog(CompoundAggregationScreenPanelSelect.class);
	
	public CompoundAggregationScreenPanelSelect(JFrame parent, NotebookPageModel mpageModel){
		super(parent,true);
		this.pageModel = mpageModel;
	}
	
	
	public boolean initialize(String projectCode, final List<ProductBatchModel> submittalBatches) {
		boolean bRet = false;
		try {
	        spSelector = ServiceController.getRegistrationManagerDelegate(MasterController.getUser().getSessionIdentifier(), MasterController.getUser().getCompoundManagementEmployeeId()).getScreenPanelDialog();
	        ScreenPanel[] screenPanels = getCommonSubmitalSets(submittalBatches);
	        //If there are no screen panels selected already in CompoundAggregation panel
	        if(screenPanels == null || screenPanels.length == 0)
	        {
	        	screenPanels = getCompoundAggregationScreenPanelsFromSynthesisPlanScreenPanelList();
	        }
	        if (isUniqueSubmittalSets == false)
			{
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
				                              "Since the products have different submittal sets assigned,\n" +
				                              "common submittal sets (if any) only will be pre-populated.");
			}
	        
	        spSelector.initialize(projectCode, screenPanels);
	        if (!pageModel.isEditable()) {
	        	disableEditing(spSelector);
	        }
	        
	        // Add dialog buttons
	        btnCancel = new JButton("Cancel");
	        btnSelect = new JButton("Select");
	        if (!pageModel.isEditable()) {
	        	btnSelect.setEnabled(false);
	        }
	        JPanel dialogControlPanel = new JPanel(); 
	        dialogControlPanel.setLayout(new GridLayout(1,2));
	        dialogControlPanel.setBorder(BorderFactory.createEtchedBorder());
	        JPanel bottomLeft = new JPanel(new FlowLayout(FlowLayout.CENTER));
	        bottomLeft.add(btnSelect);
	        JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.CENTER));
	        bottomRight.add(btnCancel);
	        dialogControlPanel.add(bottomLeft);
	        dialogControlPanel.add(bottomRight);
	        
	        this.btnSelect.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	onSelect(submittalBatches);
	            }
	        });
	        this.btnCancel.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	onCancel();
	            }
	        });
	        this.setTitle("Screen Panel Selection");
	        this.getContentPane().add(spSelector);
	        this.getContentPane().add(dialogControlPanel,BorderLayout.SOUTH); 	        
	        this.setSize(700,600);
	        
	        bRet = true;
        } catch (Exception e) {
        	log.error("Failed dialog initialization.", e);
        }
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = getSize();
		setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2 - (labelSize.height / 2));

		return bRet;
	}

	private void disableEditing(Container container) {
		for (int i = 0; i < container.getComponentCount(); i++) {
			Component c = container.getComponent(i);
			if (c instanceof Container) {
				if (c instanceof JButton) {
					c.setEnabled(false);
				} else if (c instanceof JTree) {
					((JTree)c).setEnabled(false);
				}
				disableEditing((Container)c);
			}
		}		
	}
	
	/**
	 * 
	 * @param submittalBatches
	 * @return array of common screen panels across all submittal batches
	 * @throws LoadServiceException 
	 */
	private ScreenPanel[] getCommonSubmitalSets(List<ProductBatchModel> submittalBatches) throws LoadServiceException {
		ScreenPanel[] preselectedScreenPanels = null;
		if (submittalBatches.size() == 0 ||
			submittalBatches.get(0).getRegInfo().getNewSubmitToBiologistTestList() == null)
		{
			return null;
		}
		List<BatchSubmissionToScreenModel> firstScreenPanel = submittalBatches.get(0).getRegInfo().getNewSubmitToBiologistTestList();
		if (submittalBatches.size() == 1)
		{
			BatchSubmissionToScreenModel panel = null;
			preselectedScreenPanels = new ScreenPanel[firstScreenPanel.size()];
			for (int k=0; k<firstScreenPanel.size(); k++) {
				panel = firstScreenPanel.get(k);
				if (panel.getCompoundAggregationScreenPanelKey() == 0) //This check is reqd while dealing with legacy data which will have '0' for the key. 
					return null;
				preselectedScreenPanels[k] = getCompoundAggregationScreenPanelFromCode(panel.getCompoundAggregationScreenPanelKey()); 
			}
			return preselectedScreenPanels;
		}
		if (firstScreenPanel.size() == 0) {
			return null;
		}
		int count = 0;
		for (ProductBatchModel prodBatch : submittalBatches) {
            List<BatchSubmissionToScreenModel> nextScreenPanel = prodBatch.getRegInfo().getNewSubmitToBiologistTestList();

			if (nextScreenPanel.size() == 0) {
				return null;
			}

			if (firstScreenPanel.size() != nextScreenPanel.size()) {
				isUniqueSubmittalSets = false;
			}
			int batchSubmissionToScreenPanelCount = 0;
			for (BatchSubmissionToScreenModel firstScreenPanelBatchSubmission : firstScreenPanel) {
				// if the submission is not repeated in the next panel remove from the first?
				// this logic is odd. 
				if (isExists(firstScreenPanelBatchSubmission, nextScreenPanel) == false) {
					firstScreenPanel.set(batchSubmissionToScreenPanelCount, null);
					count++;
				}
				batchSubmissionToScreenPanelCount++;
			}
		}
		count = firstScreenPanel.size() - count;
		preselectedScreenPanels = new ScreenPanel[count];
		if (isUniqueSubmittalSets && count == firstScreenPanel.size()) {
			isUniqueSubmittalSets = true;
		} else {
			isUniqueSubmittalSets = false;
		}
		for (int i=0, j=0; i<firstScreenPanel.size(); i++)
		{
			if (firstScreenPanel.get(i) != null)
			{
				preselectedScreenPanels[j++] = getCompoundAggregationScreenPanelFromCode(firstScreenPanel.get(i).getCompoundAggregationScreenPanelKey()); 
			}
		}
		return preselectedScreenPanels;
	}

	private ScreenPanel getCompoundAggregationScreenPanelFromCode(long compoundAggregationScreenPanelKey) throws LoadServiceException {
		ScreenPanel screenPanel = null;
		try {
			if(compoundAggregationScreenPanelKey != 0)
			{
				screenPanel = ServiceController.getRegistrationManagerDelegate(MasterController.getUser().getSessionIdentifier(), MasterController.getUser().getCompoundManagementEmployeeId()).getScreenPanel(compoundAggregationScreenPanelKey);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return screenPanel;
	}


	private boolean isExists(BatchSubmissionToScreenModel batchSubmissionToScreenModel,
	                         List<BatchSubmissionToScreenModel> nextScreenPanel) {
		for (BatchSubmissionToScreenModel submission : nextScreenPanel) {
			if (batchSubmissionToScreenModel.getCompoundAggregationScreenPanelKey() == submission.getCompoundAggregationScreenPanelKey())
				return true;
		}
		return false;
	}


	public void closeDialog(){
		this.setVisible(false);
		this.dispose(); 
	}

	public void onSelect(List<ProductBatchModel> submittalBatches) {
		boolean checkResult = checkSelectedSubmittalSetsForMinValue(submittalBatches);
		if (!checkResult) {
			return;
		}
		if (!isUniqueSubmittalSets) {
			int confirmation = JOptionPane.showConfirmDialog(MasterController.getGUIComponent(), "The selection has " +
					"different submittal sets. This will overwrite all submittal sets with the selected submittal sets. Do you want to continue?", 
					"Overwrite previous submittal sets", JOptionPane.YES_NO_OPTION);
			if (confirmation == JOptionPane.NO_OPTION) {
				return;
			}
		}
		assignSubmittalSetsToBatches(submittalBatches);
		isUniqueSubmittalSets = true;
		pageModel.setModelChanged(true);
		MasterController.getGUIComponent().enableSaveButtons();
		closeDialog();
	}
	
	private boolean checkSelectedSubmittalSetsForMinValue(List<ProductBatchModel> submittalBatches) {
		boolean result = true;
		ScreenPanel[] screenPanels = spSelector.getSelectedPanels();
		if (submittalBatches != null && screenPanels != null) {
			List<ProductBatchModel> badBatches = new ArrayList<ProductBatchModel>();
			for (ScreenPanel panel : screenPanels) {
				float minPanelAmount = panel.getMinPanelAmount();
				for (ProductBatchModel batch : submittalBatches) {
					List<BatchSubmissionContainerInfoModel> containers = batch.getRegInfo().getSubmitContainerList();
					if (containers != null) {
						for (BatchSubmissionContainerInfoModel container : containers) {
							if (StringUtils.equals(container.getContainerType(), "M2D")) {
								// minPanelAmount is in uMol - so we need to convert value to uMoles
								String moleUnit = container.getMoleUnit();
								double moleValue = container.getMoleValue();
								Amount2 model = new Amount2(moleValue, new Unit2(moleUnit));
								model.setUnit(new Unit2("UMOL"));
								double value = Double.parseDouble(model.getValue());
								
								if (value < minPanelAmount) {
									if (!badBatches.contains(batch)) {
										badBatches.add(batch);
									}
									break;
								}
							}
						}
					}
				}
			}
			if (badBatches.size() > 0) {
				String message1 = "Amount in Tubes for following Batches is not sufficient for selected screens:";
				String message2 = "Do you want to proceed?";
				
				List<String> batchNumbers = new ArrayList<String>();
				for (ProductBatchModel batch : badBatches) {
					if (batch != null) {
						batchNumbers.add(batch.getBatchNumberAsString());
					}
				}
				
				JScrollPane scrollPane = new JScrollPane(new JList(batchNumbers.toArray()));
				
				JPanel messagePanel = new JPanel(new BorderLayout(6, 6));
				messagePanel.setBorder(new EmptyBorder(6, 6, 6, 6));
				messagePanel.add(new JLabel(message1), BorderLayout.NORTH);
				messagePanel.add(scrollPane, BorderLayout.CENTER);
				messagePanel.add(new JLabel(message2), BorderLayout.SOUTH);
				
				int dialogResult = JOptionPane.showConfirmDialog(this, messagePanel, getTitle(), JOptionPane.YES_NO_OPTION);
				
				result = (dialogResult == JOptionPane.YES_OPTION);
			}
		}
		return result;
	}

	public void onCancel() {
		closeDialog();
	}

	/**
	 * 
	 * @param submittalBatches
	 */
	public void assignSubmittalSetsToBatches(List<ProductBatchModel> submittalBatches)
	{
		ScreenPanel screenPanel[] = spSelector.getSelectedPanels();
		ArrayList<BatchSubmissionToScreenModel> biologicalTestList = convertCompoundAggregationScreenPanelToCeNScreenModel(screenPanel);
		for (ProductBatchModel prodBatch : submittalBatches) {
			prodBatch.getRegInfo().setSubmitToBiologistTestList(biologicalTestList);
			prodBatch.setModelChanged(true);
		}
	}

	private ArrayList<BatchSubmissionToScreenModel> convertCompoundAggregationScreenPanelToCeNScreenModel(ScreenPanel[] screenPanel) {
		ArrayList<BatchSubmissionToScreenModel> biologicalTestList = new ArrayList<BatchSubmissionToScreenModel>();
		BatchSubmissionToScreenModel screenModel = null;
		for (int i=0; i<screenPanel.length; i++)
		{
			screenModel = new BatchSubmissionToScreenModel();
			screenModel.setCompoundAggregationScreenPanelKey(screenPanel[i].getKey());
			screenModel.setScreenProtocolTitle(screenPanel[i].getName().trim());
			screenModel.setModelChanged(true);
			biologicalTestList.add(screenModel);
		}
		return biologicalTestList;
	}

	/**
	 * Need to check for closed submittal sets
	 * @return
	 */
	private ScreenPanel[] getCompoundAggregationScreenPanelsFromSynthesisPlanScreenPanelList()
	{
		ScreenPanel[] compoundAggregationPanels = null;
		//these are the screen panels selected in DSP
		String panelsStr[] = this.pageModel.getPageHeader().getScreenPanels();
		String projCode =  this.pageModel.getPageHeader().getProjectCode();
		if(panelsStr != null && panelsStr.length > 0)
		{
			compoundAggregationPanels = new ScreenPanel[panelsStr.length];
				//ProjectTrackingSession projTrakSession = compoundAggregationManager.createProjectTrackingSession();
				//ProjectTrackingData projTrackingData = projTrakSession.getProjectTrackingData(projCode);
				
			for(int i = 0; i < panelsStr.length; ++i)
			{
				if (panelsStr[i] != null) {
					String panelKeyAsStr = panelsStr[i].trim();
					if (panelKeyAsStr != null && !panelKeyAsStr.equals("") && !panelKeyAsStr.equals("null")) {
						log.debug("Screen Panel name:" + panelKeyAsStr);
						try {
							long panelKey = Long.parseLong(panelKeyAsStr);
							compoundAggregationPanels[i] = getCompoundAggregationScreenPanelFromCode(panelKey);
							if (compoundAggregationPanels[i] != null) {
								log.debug("Screen Panel name from CompoundAggregation:" + compoundAggregationPanels[i].getName());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		return compoundAggregationPanels;
	}
}

