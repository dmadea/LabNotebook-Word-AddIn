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
package com.chemistry.enotebook.client.print.gui;

import com.chemistry.enotebook.analyticalservice.delegate.AnalyticalServiceDelegate;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import com.chemistry.enotebook.servicelocator.PropertyException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class PrintExperimentSetup extends JDialog {

	private static final long serialVersionUID = 299992883819493755L;

	private static final Log log = LogFactory.getLog(PrintExperimentSetup.class);
	
	private Frame owner;
	
	private JTabbedPane tabbedPane;
	private SelectPanel selectPanel;
	private ConceptionPanel conceptionPanel;
	private SingletonPanel singletonPanel;
	private ParallelPanel parallelPanel;
	
	private ConceptionPrintOptions conceptionPrintOptions;
	private SingletonPrintOptions singletonPrintOptions;
	private ParallelPrintOptions parallelPrintOptions;
	private SynthesisPlatePrintOptions synthesisPlatePrintOptions;
	
	private List<PrintRequest> experimentsToPrint = new ArrayList<PrintRequest>();

	private JButton previewButton;
	private JButton exportButton;
	private JButton saveButton;
	private JButton printButton;
	private JButton closeButton;

	private PrintExperimentSetup(Frame owner) throws HeadlessException {
		super(owner);
		this.owner = owner;
		loadPrintOptions();
		initGUI();
	}
	
	private void initGUI() {		
		setTitle("Print Experiments");
		setResizable(false);
		setModal(true);
		setSize(new Dimension(750, 680));
		setDefaultCloseOperation(PrintExperimentSetup.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(owner);

		selectPanel = new SelectPanel(this, synthesisPlatePrintOptions);
		conceptionPanel = new ConceptionPanel(this, conceptionPrintOptions);
		singletonPanel = new SingletonPanel(this, getAnalysisInstrumentList(), singletonPrintOptions);
		parallelPanel = new ParallelPanel(this, getAnalysisInstrumentList(), parallelPrintOptions);
		
		tabbedPane = new JTabbedPane();		
		tabbedPane.addTab("Print Types", null, selectPanel, "Select report(s) to print");
		tabbedPane.addTab("Conception", null, conceptionPanel, "Define Conception Report Format");
		tabbedPane.addTab("Singleton", null, singletonPanel, "Define Singleton Report Format");
		tabbedPane.addTab("Parallel", null, parallelPanel, "Define Parallel Report Format");
		
		// Temp disabled for Parallel
		tabbedPane.setEnabledAt(tabbedPane.indexOfComponent(parallelPanel), false);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		getContentPane().add(getControlPanel(), BorderLayout.SOUTH);
		
		disableSaveButton();
	}
	
	public static void displayPrintExperimentsDialog(Frame frame, List<PrintRequest> list) {
		displayPrintExperimentsDialog(frame, list, false);
	}
	
	public static void displayPrintExperimentsDialog(Frame owner, List<PrintRequest> list, boolean setSynthesisPlates) {
		PrintExperimentSetup dlg = new PrintExperimentSetup(owner);
		
		if (setSynthesisPlates) {
			dlg.selectPanel.setSynthesisPlatesCB();
		}
		
		dlg.displayPrintExperimentsDialog(list);
		dlg.setVisible(true);
	}
	
	private void displayPrintExperimentsDialog(List<PrintRequest> list) {
		selectPanel.setExperimentsToPrint(list);
		experimentsToPrint.clear();
		experimentsToPrint.addAll(list);
		validate();
	}	
	
	private JPanel getControlPanel() {		
		previewButton = new JButton("Preview");
		previewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printExperiment(PrintSetupConstants.buttonChoices.PREVIEW);
			}
		});
		
		exportButton = new JButton("Export");
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printExperiment(PrintSetupConstants.buttonChoices.EXPORT);
			}			
		});

		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				savePreferences();
			}
		});

		printButton = new JButton("Print");
		printButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printExperiment(PrintSetupConstants.buttonChoices.PRINT);
			}
		});
		
		closeButton = new JButton("Close");
		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dispose();
			}
		});
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		leftPanel.add(previewButton);
		leftPanel.add(exportButton);
				
		JPanel fillerPanel = new JPanel();
		fillerPanel.setPreferredSize(new Dimension(30, 10));
				
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		rightPanel.add(saveButton);
		rightPanel.add(printButton);
		rightPanel.add(closeButton);
		
		JPanel controlPanel = new JPanel();
		controlPanel.add(leftPanel, BorderLayout.WEST);
		controlPanel.add(fillerPanel, BorderLayout.CENTER);
		controlPanel.add(rightPanel, BorderLayout.EAST);
		
		return controlPanel;
	}
	
	private void savePreferences() {
		try {
			conceptionPanel.updatePrintOptions();
			conceptionPrintOptions.saveOptions();
			singletonPanel.updatePrintOptions();
			singletonPrintOptions.saveOptions();
			parallelPanel.updatePrintOptions();
			parallelPrintOptions.saveOptions();
			selectPanel.updatePrintOptions();
			synthesisPlatePrintOptions.saveOptions();
			MasterController.getUser().updateUserPrefs();
			disableSaveButton();
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
			log.error("Error saving preferences:", e);
		}
	}

	/**
	 * initialize the AnalyticalService Client and get the Sites, FileTypes and instrument Types
	 * 
	 * @throws CeNAnalyticalServiceAccessException
	 * @throws PropertyException
	 * @throws RemoteException
	 * 
	 */
	public List<String> getAnalysisInstrumentList() {
		AnalyticalServiceDelegate analyticalServiceDelegate;
		try {
			analyticalServiceDelegate = new AnalyticalServiceDelegate();
			return analyticalServiceDelegate.getAllInstrumentTypesFromDB();
		} catch (Exception e) {
			// For testing
			List<String> instruments = new ArrayList<String>();
			instruments.add("NMR");
			instruments.add("LC-MS");
			return instruments;
		}
	}
   
	private void enableSaveButton(boolean enable) {
		saveButton.setEnabled(enable);
	}
	
	public void enableSaveButton() {
		enableSaveButton(true);
	}
	
	public void disableSaveButton() {
		enableSaveButton(false);
	}
	
    private void loadPrintOptions() {
    	try {
    		if (conceptionPrintOptions == null) {
    			conceptionPrintOptions = new ConceptionPrintOptions();
    			conceptionPrintOptions.loadOptions();
    		}
    		if (singletonPrintOptions == null) {
    			singletonPrintOptions = new SingletonPrintOptions();
    			singletonPrintOptions.loadOptions();
    		}
    		if (parallelPrintOptions == null) {
    			parallelPrintOptions = new ParallelPrintOptions();
    			parallelPrintOptions.loadOptions();
    		}

    		if (synthesisPlatePrintOptions == null) {
    			synthesisPlatePrintOptions = new SynthesisPlatePrintOptions();
    			synthesisPlatePrintOptions.loadOptions();
    		}
    	} catch (UserPreferenceException e) {
    		log.error("Error loading print options:", e);
    		CeNErrorHandler.getInstance().logExceptionMsg(null, e);
    	}
    }

    private ReportURLGenerator getReportURLGenerator(PrintRequest request, PrintSetupConstants.buttonChoices whichButton, String filename) {
		ReportURLGenerator urlgen = new ReportURLGenerator();
		
		urlgen.addParameter(PrintSetupConstants.NOTEBOOK_NUMBER, request.getNotebookNumber());
		urlgen.addParameter(PrintSetupConstants.PAGE_NUMBER, request.getNotebookPage());
		urlgen.addParameter(PrintSetupConstants.FILE_NAME, filename);
        urlgen.addParameter(PrintSetupConstants.STOP_AFTER_IMAGE_LOAD_ERROR, "false");
        
		if (whichButton == PrintSetupConstants.buttonChoices.EXPORT) {   							
			urlgen.addParameter(PrintSetupConstants.OUTPUT_FORMAT, ReportURLGenerator.DOC);
		} else {
			urlgen.addParameter(PrintSetupConstants.OUTPUT_FORMAT, ReportURLGenerator.PDF);
		}

		return urlgen;
    }
    
    private void printExperiment(PrintSetupConstants.buttonChoices whichButton) {
    	try {
			StringBuffer buff = new StringBuffer();
    		for (PrintRequest request : experimentsToPrint) {
    			// If experiment is opened, save it
    			NotebookPageGuiInterface nbPgGui = MasterController.getGuiController().getLoadedPageGui(request.getSiteCode(), request.getNbRef(), request.getModelVersionNumber());
    			if (nbPgGui != null && nbPgGui.getPageModel() != null && nbPgGui.getPageModel().isModelChanged()) {
        			String pageUser = nbPgGui.getPageModel().getUserName();
        			NotebookUser currentUser = MasterController.getUser();
        			
        			// If current user can save the experiment, show confirm dialog to save. 
        			// Otherwise current user cannot save this page.
        			if (StringUtils.equals(currentUser.getNTUserID(), pageUser) || currentUser.isSuperUser()) {
        				int result = JOptionPane.showConfirmDialog(this, "Page has been changed. Save changes before printing?", this.getTitle(), JOptionPane.YES_NO_CANCEL_OPTION);
    				
        				if (result == JOptionPane.YES_OPTION) {
        					MasterController.getGuiController().saveExperiment(nbPgGui.getPageModel());
        				} else if (result == JOptionPane.CANCEL_OPTION) {
        					return;
        				}
        			}
    			}
    			
    			buff.setLength(0); // resets buffer
    			buff.append(request.getNbRef().getNotebookRef());
                final String version = request.getModelVersionNumber().toString();
                buff.append("v").append(version);
    			String filename = buff.toString();
    			String pageType = request.getPageType(); 
    			
    			setVisible(false);
    			
    			if (selectPanel.printExperiment()) {
    				ReportURLGenerator urlgen = getReportURLGenerator(request, whichButton, filename);
                    urlgen.addParameter(PrintSetupConstants.PAGE_VERSION, version);
                    urlgen.addParameter(PrintSetupConstants.STOP_AFTER_IMAGE_LOAD_ERROR, "false");
                    urlgen.addParameter(PrintSetupConstants.TIME_ZONE, TimeZone.getDefault().getID());
					if (pageType.equals(CeNConstants.PAGE_TYPE_CONCEPTION)) {
    					conceptionPanel.updatePrintOptions();
    					urlgen.addParameter(PrintSetupConstants.REPORT_NAME, "conception.rptdesign");
    					Map<String, String> otherOptions = conceptionPrintOptions.getOptions();
    					for (String key : otherOptions.keySet()) {
    						urlgen.addParameter(key, otherOptions.get(key));
    					}
    				} else if (pageType.equals(CeNConstants.PAGE_TYPE_PARALLEL)) {
    					parallelPanel.updatePrintOptions();
    					urlgen.addParameter(PrintSetupConstants.REPORT_NAME, "parallel.rptdesign");
    					Map<String, String> otherOptions = parallelPrintOptions.getOptions();
    					for (String key : otherOptions.keySet()) {
    						urlgen.addParameter(key, otherOptions.get(key));
    					}
    				} else if (pageType.equals(CeNConstants.PAGE_TYPE_MED_CHEM)) {
    					singletonPanel.updatePrintOptions();
    					urlgen.addParameter(PrintSetupConstants.REPORT_NAME, "medchem.rptdesign");
    					Map<String, String> otherOptions = singletonPrintOptions.getOptions();
    					for (String key : otherOptions.keySet()) {
    						urlgen.addParameter(key, otherOptions.get(key));
    					}
    				}
					// If preview button, do not include IP or AnalyticalService attachments
   					if (whichButton == PrintSetupConstants.buttonChoices.PREVIEW) {  
   						urlgen.addParameter(PrintSetupConstants.INCLUDE_ATTACHMENTS, "false");
   						urlgen.addParameter(PrintSetupConstants.INCLUDE_ANALYTICAL_SERVICE_FILES, "false");
   					}
					if (urlgen.isComplete()) {
						PrintExperiment printexp = new PrintExperiment(filename, urlgen, whichButton);
						printexp.print();
					} // else handle error
    			} 
        		if (this.selectPanel.printSynthesisPlates()) {
        			if (pageType.equals(CeNConstants.PAGE_TYPE_PARALLEL)) {
     					selectPanel.updatePrintOptions();
     					String synthesisPlatesFilename = filename + "_SynthesisPlates";
     					ReportURLGenerator urlgen = getReportURLGenerator(request, whichButton, synthesisPlatesFilename);
    					urlgen.addParameter(PrintSetupConstants.REPORT_NAME, "synthesis.rptdesign");
    					Map<String, String> otherOptions = synthesisPlatePrintOptions.getOptions();
    					for (String key : otherOptions.keySet()) {
    						urlgen.addParameter(key, otherOptions.get(key));
    					}
   						PrintExperiment printexp = new PrintExperiment(synthesisPlatesFilename, urlgen, whichButton);
						printexp.print();   					
        			}
        		}
    		}
    	} catch (Exception e) {
    		log.error("Failed to print experiment:", e);
    	} finally {
    		dispose();
    	}
    }
}
