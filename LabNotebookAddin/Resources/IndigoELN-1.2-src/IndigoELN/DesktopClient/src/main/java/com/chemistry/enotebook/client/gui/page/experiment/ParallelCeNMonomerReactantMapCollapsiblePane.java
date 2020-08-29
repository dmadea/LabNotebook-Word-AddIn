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
package com.chemistry.enotebook.client.gui.page.experiment;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane;
import com.chemistry.enotebook.client.gui.common.utils.AmountEditListener;
import com.chemistry.enotebook.client.gui.page.experiment.stoich.MolesChangedEvent;
import com.chemistry.enotebook.client.gui.page.experiment.stoich.RxnEquivChangedEvent;
import com.chemistry.enotebook.client.gui.page.experiment.stoich.StoichDataChangesListener;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountComponent;
import com.chemistry.enotebook.client.gui.page.experiment.table.ParallelCeNTableViewToolBar;
import com.chemistry.enotebook.client.gui.page.experiment.table.connector.PCeNMonomerReactantsSummaryTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.utils.ResourceKit;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * 
 */
public class ParallelCeNMonomerReactantMapCollapsiblePane implements AmountEditListener {
	private CollapsiblePane monomerReactantMapCollpsiblePane = new CollapsiblePane("Monomer Reactants Map");
	private ReactionStepModel stepModel;
	// private ParallelNotebookPageGUI parallelPageGui;
	// private TableHeaderMouseListener monomerTableML;
	private NotebookPageModel pageModel;
	private JPanel pane;

	//
	private AmountModel scaleAmount;
	//private ScaleFieldFocusHandler scaleFieldFocusHandler = new ScaleFieldFocusHandler(scaleFieldList);	
	//////private double equivFactor;
	//private EquivFieldFocusHandler equivFocusHandler= new EquivFieldFocusHandler(eqTextfieldHash);
	private BatchesList[] bListArray = null;
	// We perform the same action on each member of tool bar list so we don't need a map
	private ArrayList toolBarList = new ArrayList();
	// We need to be able to pull the batchList for the table with the equivs change
	private transient HashMap equivsHash = new  HashMap();
	//private AmountComponent scaleAmountView;
	//private AmountModel scaleAmountModel;
	private static final String SCALE = "SCALE";
	private static final String RXN_EQUIVS = "RXN_EQUIVS";
	//private	StoichDataChangesListener stoichDataChangesListener;
	//Key is Interger(step#) and value is StoichDataChangesListener
	private HashMap stepsStoichDataChangesListenerMap = new HashMap(3);
	

	public ParallelCeNMonomerReactantMapCollapsiblePane(ReactionStepModel stepModel, NotebookPageModel pageModele) {
		this.stepModel = stepModel;
		
		pageModel = pageModele;
		// this.parallelPageGui = (ParallelNotebookPageGUI)pageGui;
		monomerReactantMapCollpsiblePane.setStyle(CollapsiblePane.TREE_STYLE);
		monomerReactantMapCollpsiblePane.setBackground(CeNConstants.BACKGROUND_COLOR);
		monomerReactantMapCollpsiblePane.setSteps(1);
		monomerReactantMapCollpsiblePane.setStepDelay(0);
		monomerReactantMapCollpsiblePane.setContentPane(createMonomerReactantsTabsMap());
	}

	private void initializeStoicDataChangesListeners()
	{
//		Intialize StoicDataChangesListeners for each step in the Page
		int stepsSize = this.pageModel.getReactionSteps().size();
		int startStep = 0;
		if(stepsSize > 1)
		{
		startStep = 1;	
		for(int i=startStep;i < stepsSize ; i++ )
		{
//			Create Stoic changes listener to handle salt , scale and equiv changes
			StoichDataChangesListener stoichDataChangesListener = new StoichDataChangesListener((ReactionStepModel)this.pageModel.getReactionSteps().get(i),CeNConstants.PAGE_TYPE_PARALLEL);
			stepsStoichDataChangesListenerMap.put(new Integer(i), stoichDataChangesListener);
		}
		}
		//one step and it is summary
		else
		{
			StoichDataChangesListener stoichDataChangesListener = new StoichDataChangesListener(stepModel,CeNConstants.PAGE_TYPE_PARALLEL);
			stepsStoichDataChangesListenerMap.put(new Integer(startStep), stoichDataChangesListener);
		}
	}
	
	private JComponent createMonomerReactantsTabsMap()  {
		pane = new JPanel();
		pane.setBackground(new Color(189, 236, 214));
		pane.setLayout(new BorderLayout());
		//pane.setPreferredSize(new Dimension(10000, 200));  // this will shorten the table but lengthen the reaction scheme
		JTabbedPane monomerTableViewTabs = new JTabbedPane(SwingConstants.TOP);
		//This method initializes Stoic changes listeners for each step in Page
		initializeStoicDataChangesListeners();
		ArrayList monomerLists = stepModel.getMonomers();
		bListArray = new BatchesList[monomerLists.size()];
		for (int i = 0; i < monomerLists.size(); i++) {
			BatchesList bList = (BatchesList) monomerLists.get(i);
			bListArray[i] = bList;	
			
			PCeNMonomerReactantsSummaryTableModelConnector connector = new PCeNMonomerReactantsSummaryTableModelConnector(bList, pageModel);
			try {
				//get the step number this list belongs to
				int stepNum = this.pageModel.getStepNumberThisMonomerBelongsTo(bList);
				//get the right step stoic listeners and pass to this connector
				StoichDataChangesListener stoichDataChangesListener = (StoichDataChangesListener)stepsStoichDataChangesListenerMap.get(new Integer(stepNum));
				connector.addStoichDataChangesListener(stoichDataChangesListener);
			} catch (Exception e) {
				e.printStackTrace();
			}
			PCeNTableModel model = new PCeNTableModel(connector);
			PCeNTableView tableView = new PCeNTableView(model, 50, connector,null);
			// monomerTableML = new TableHeaderMouseListener(tableView);
			// tableView.getTableHeader().addMouseListener(monomerTableML);
			ReactantsToolBar toolBar = new ReactantsToolBar(bList, i);
			// Put the info for this tab in the list
			toolBarList.add(toolBar);
			ParallelCeNTableViewToolBar tableViewToolBar = new ParallelCeNTableViewToolBar(tableView, toolBar);
			JScrollPane sPane = new JScrollPane(tableView);
			JPanel toolPanel = new JPanel(new BorderLayout());
			toolPanel.add(tableViewToolBar, BorderLayout.CENTER);
			JPanel mPanel = new JPanel(new BorderLayout());
			mPanel.add(toolPanel, BorderLayout.NORTH);
			mPanel.add(sPane, BorderLayout.CENTER);
			monomerTableViewTabs.addTab(ResourceKit.getABCDRoot(i), mPanel);
		}
		monomerTableViewTabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JTabbedPane tabbedPane = (JTabbedPane) evt.getSource();
				int selectedIndex = tabbedPane.getSelectedIndex();
				// Get current tab
				for (int c = 0; c < tabbedPane.getTabCount(); c++) {
					tabbedPane.setBackgroundAt(c, Color.LIGHT_GRAY);
				}
				tabbedPane.setBackgroundAt(selectedIndex, Color.WHITE);
				//Also refresh the scale value in this tab from DSP scale
				ReactantsToolBar toolBar = (ReactantsToolBar)toolBarList.get(selectedIndex);
				toolBar.refreshScaleAmountInTab();
				
			}
		});
		pane.add(monomerTableViewTabs, BorderLayout.CENTER);
		return pane;
	}

	/**
	 * @return Returns the stoichPane.
	 */
	public CollapsiblePane geMonomerReactantPane() {
		return monomerReactantMapCollpsiblePane;
	}

	
	private void enableSaveButton() {
		//System.out.println("enableSaveButton called CompoundManagementMonomerContainer");
		pageModel.setModelChanged(true);
		MasterController.getGUIComponent().enableSaveButtons();
		//this.revalidate();
	}
	
	private void handleScaleChange(AmountModel newMoleAmountModel) {
		double newMoleAmount = newMoleAmountModel.GetValueInStdUnitsAsDouble();
		//String newUnitCode = newMoleAmountModel.getUnit().getCode();
		pageModel.getPageHeader().setScale(newMoleAmountModel);
		//set this scale as moles to all monomers Lists
		if (this.bListArray == null)
			return;
		int bListSize = this.bListArray.length;
		for(int i = 0;i<bListSize ; i ++)
		{
			BatchesList bList = this.bListArray[i];
			AmountModel currentMoleAmount = bList.getStoicMoleAmount();
			if (currentMoleAmount.GetValueInStdUnitsAsDouble() != newMoleAmount) {
				//set List level amount since 'setStoicMoleAmount()' is not used
				bList.setStoicMoleAmount(newMoleAmountModel);
				//call StoicDataChange Listener
				MolesChangedEvent rce = new MolesChangedEvent(this, bList);
//				get the step number this list belongs to
				int stepNum = this.pageModel.getStepNumberThisMonomerBelongsTo(bList);
				//get the right step stoic listeners and pass to this connector
				StoichDataChangesListener stoichDataChangesListener = (StoichDataChangesListener)stepsStoichDataChangesListenerMap.get(new Integer(stepNum));
				stoichDataChangesListener.stoicElementMolesChanged(rce);
	
			}
		}
		enableSaveButton();
	}

	private void handleRxnEquivsChange(PAmountComponent amountView) {
		AmountModel newEquivAmountModel = amountView.getAmount();
		Integer tabIndex = (Integer) equivsHash.get(amountView.getName());
		if (tabIndex == null)
			return;
		int index = Integer.parseInt(tabIndex.toString());
		BatchesList bList = this.bListArray[index];
		bList.setStoicRxnEquivsAmount(newEquivAmountModel);
		//call StoicDataChange Listener
		RxnEquivChangedEvent rce = new RxnEquivChangedEvent(this, bList);
//		get the step number this list belongs to
		int stepNum = this.pageModel.getStepNumberThisMonomerBelongsTo(bList);
		//get the right step stoic listeners and pass to this connector
		StoichDataChangesListener stoichDataChangesListener = (StoichDataChangesListener)stepsStoichDataChangesListenerMap.get(new Integer(stepNum));
		stoichDataChangesListener.stoicElementRxnEquivsChanged(rce);
		pageModel.getPageHeader().setModelChanged(true); //way to force the save button ..fix it
		enableSaveButton();
		
	}	

	
//	private class ScaleFieldFocusHandler implements FocusListener, ActionListener {
//		ArrayList scaleFieldList = null;
//		public ScaleFieldFocusHandler(ArrayList scaleFieldList) {
//			this.scaleFieldList = scaleFieldList;
//		}
//		String existingText = "";
//		public void focusGained(FocusEvent e) {
//			existingText = ((JTextField)e.getSource()).getText();
//		}
//
//		public void focusLost(FocusEvent e) {
//			String enteredText = ((JTextField)e.getSource()).getText();
//			if (!existingText.equals(enteredText))
//			{
//				for (int i=0; i<scaleFieldList.size(); i++)
//					((JTextField)scaleFieldList.get(i)).setText(enteredText);
//				AmountModel amountModel = pageModel.getPageHeader().getScale();
//				amountModel.setValue(Double.valueOf(enteredText).doubleValue());
//				pageModel.getPageHeader().setModelChanged(true);
//				enableSaveButton();
//			}
//		}
//		
//		public void actionPerformed(ActionEvent e) {
//			focusLost(new FocusEvent((JTextField)e.getSource(), e.getID()));
//		}		
//	}

//	private class EquivFieldFocusHandler implements FocusListener, ActionListener {
//
//		private HashMap eqTextfieldHash = null;
//
//		public EquivFieldFocusHandler(HashMap eqTextfieldHash) {
//			this.eqTextfieldHash  = eqTextfieldHash;
//		}
//
//		public void focusGained(FocusEvent arg0) {
//		}
//
//		public void focusLost(FocusEvent evt) {
//			Object source = evt.getSource();
//			if(source instanceof JTextField ){
//				JTextField equivTextField = (JTextField)source;
//				BatchesList bList = (BatchesList)eqTextfieldHash.get(equivTextField);
//				AmountModel amountModel = bList.getStoicRxnEquivsAmount();
//				String newEQ = equivTextField.getText();
//				
//				if (!newEQ.equals("") &&  amountModel.GetValueInStdUnitsAsDouble() != Double.valueOf(newEQ).doubleValue())
//				{
//					amountModel.setValue(Double.valueOf(newEQ).doubleValue());
//					bList.setStoicRxnEquivsAmount(amountModel);
//					enableSaveButton();
//				}
//			}
//		}
//		public void actionPerformed(ActionEvent e) {
//			focusLost(new FocusEvent((JTextField)e.getSource(), e.getID()));
//		}
//	}
	
	private PAmountComponent makeAmountComponent() {
		PAmountComponent amountComponent = new PAmountComponent();
		amountComponent.setEditable(true);
		amountComponent.setValueSetTextColor(null);
		amountComponent.addAmountEditListener(this);
		amountComponent.setBorder(BorderFactory.createLoweredBevelBorder());	
		return amountComponent;
	}

	public void editingCanceled(ChangeEvent e) {

	}

	/**
	 * Handler for the amountComponent change.  There is only 
	 * one amountComponent in the panel -- the scale.
	 */
	public void editingStopped(ChangeEvent e) {

		Object o = e.getSource();
		// NOTE:  There is only one PAmountComponent which is the scale.  If
		// that changes, this has to be fixed.  vbtodo fix it anyway.
		if (o instanceof PAmountComponent) {
			PAmountComponent amountComp = (PAmountComponent) o;
			AmountModel amountModel = amountComp.getAmount();
			if (amountModel.getUnitType().getOrdinal() == UnitType.MOLES.getOrdinal())
				this.handleScaleChange(amountModel);
			else if (amountModel.getUnitType().getOrdinal() == UnitType.SCALAR.getOrdinal())
				this.handleRxnEquivsChange(amountComp);  // we need the component to key into hash map
		}
	}
	
	class ReactantsToolBar extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6763080734524134063L;
		//////private AmountModel scaleAmountModel;
		private PAmountComponent scaleAmountView;
		private AmountModel equivsAmountModel;
		private PAmountComponent equivsAmountView;
	
		public ReactantsToolBar(BatchesList bList, int index) {
			this.setLayout(new BorderLayout());
			JLabel scaleLabel = new JLabel("Initial Scale:");
			JLabel equivLabel = new JLabel("EQ:");
			equivLabel.setPreferredSize(new Dimension(25, 20));
			// create scale amount component
			///////scaleAmountModel = new AmountModel(UnitType.MOLES);
			///////scaleAmountModel.setName(SCALE);
			///////scaleFactor = pageModel.getPageHeader().getScale().GetValueInStdUnitsAsDouble();
			scaleAmount = (AmountModel) pageModel.getPageHeader().getScale().deepClone();
			//////scaleAmountModel.setValue(scaleFactor);
			scaleAmountView = makeAmountComponent();
			//////scaleAmountView.setValue(scaleAmountModel);
			scaleAmountView.setValue(scaleAmount);
			// create stoic rxn equives amount component
			equivsAmountModel = new AmountModel(UnitType.SCALAR);
			AmountModel amountModelFromDB = bList.getStoicRxnEquivsAmount();
			equivsAmountModel.deepCopy(amountModelFromDB);
			equivsAmountView = null;
			equivsAmountView = makeAmountComponent();
			equivsAmountView.setValue(equivsAmountModel);
			equivsAmountView.setName("" + index);
			// Store this in the hash so the outer class can figure out which tab it is in
			equivsHash.put(equivsAmountView.getName(), new Integer(index));
//			CustomTextField equivTextField = new CustomTextField(new Double(0.0),
//					new Double(99.99), new Double(amountModel.getValue()), false,
//					new DecimalFormat("##.##"),CustomTextField.FLOAT);
//			equivTextField.addFocusListener(equivFocusHandler);
//			equivTextField.addActionListener(equivFocusHandler);
//			eqTextfieldHash.put(equivTextField, bList);//tf is the key and BatchList is the value
//			equivTextField.setPreferredSize(new Dimension(35, 20));
					
			//JPanel eqPanel = new JPanel();
			JPanel commonPanel = new JPanel();
			
			//equivsPanel.setLayout(new BorderLayout());
			//FormLayout fl = new FormLayout("pref, 4dlu, 25dlu, 10dlu","13dlu, pref");
			//eqPanel.setLayout(fl);
			commonPanel.setLayout(new FormLayout("pref, 4dlu, 70dlu, 10dlu, pref","pref"));
			CellConstraints cc = new CellConstraints();
			//commonPanel.setBorder(new TitledBorder("All Monomers all Steps"));
			//commonPanel.add(scaleLabel, cc.xy(1, 1));
			//commonPanel.add(scaleField, cc.xy(3, 1));
			//commonPanel.add(scaleAmountView, cc.xy(3, 1));
			
			JButton expOtherButton = new JButton("Export in Other Format");
			expOtherButton.setAlignmentX(SwingConstants.LEFT);
			expOtherButton.setPreferredSize(new Dimension(160, 20));
			expOtherButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser expOtherFileChooser = new JFileChooser();
					FileFilter filter = new FileFilter() {
						public boolean accept(File f) {
							if (f.isDirectory()) {
								return true;
							}
							String extension = getExtension(f);
							if (extension != null)
								return (extension.equals("csv"));
							return false;
						}

						public String getDescription() {
							return "CSV Files";
						}

						private String getExtension(File f) {
							return "";
/*							String ext = null;
							String s = f.getName();
							int i = s.lastIndexOf('.');
							if (i > 0 && i < s.length() - 1) {
								ext = s.substring(i + 1).toLowerCase();
							}
							return ext;
*/						}
					};
					expOtherFileChooser.setFileFilter(filter);
					int returnValue = expOtherFileChooser.showSaveDialog(MasterController.getGUIComponent());
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						File file = null;
						BufferedWriter out = null;
						try {
							String compoundId = "";
							String saltCode = "";
							//String unit = "";
							List abstractBatches = null;
							MonomerBatchModel batch = null;
							
							file = expOtherFileChooser.getSelectedFile();
							if (file == null)
								return;
							
							String fileBaseName = file.getName();
							ArrayList fileNamesList = getFileNames(fileBaseName, bListArray.length);
							String filePath = file.getPath();
							File[] files = new File[bListArray.length];
							filePath = file.getCanonicalPath();
							for (int i=0; i<bListArray.length; i++)
							{
								files[i] = new File(filePath.substring(0 , filePath.indexOf(""+file.getName())) + fileNamesList.get(i));
								if (out != null)
								{
									out.flush();
									out.close();
								}
								out = new BufferedWriter(new FileWriter(files[i]));
								abstractBatches = bListArray[i].getBatchModels(); 
								for (int j = 0; j < abstractBatches.size(); j++) {
									batch = (MonomerBatchModel) abstractBatches.get(j);
									compoundId = batch.getCompound().getRegNumber();
									saltCode = batch.getSaltForm().getCode();
									Unit2 unit = new Unit2(UnitType.MOLES, "mmol");
									AmountModel totalMolesAmount = new AmountModel();
									totalMolesAmount.setUnit(unit);
									totalMolesAmount.setValue((batch.getMoleAmount().GetValueInStdUnitsAsDouble() * batch.getNoOfTimesUsed()) + batch.getExtraNeeded().GetValueInStdUnitsAsDouble());
									totalMolesAmount.setUnit(new Unit2("umol"));
									out.write(compoundId + "-" + saltCode + "," + totalMolesAmount.GetValueForDisplay() +" \n");
								}
								batch = null;
							}
							abstractBatches  = null;
						}
						catch (IOException e) {
							e.printStackTrace();
						}
						finally{
							try {
								if (out != null) 
									out.close();
							} catch (IOException e) {
								System.out.println("Error closing Other file....");
							}
						}
					}
				}

				private ArrayList getFileNames(String fileName, int length) {
					//ugly and quick way. Change it when to for loop. Jags_todo...
					String extn = ".csv";
					ArrayList<String> list = new ArrayList<String>();
					if (length == 1)
						list.add(fileName + "_A" + extn);
					else if (length == 2)
					{
						list.add(fileName + "_A" + extn);
						list.add(fileName  + "_B" + extn);
					}
					else if (length == 3)
					{
						list.add(fileName +   "_A" + extn);
						list.add(fileName + "_B" + extn);
						list.add(fileName + "_C" + extn);
					}
					else if (length == 4)
					{
						list.add(fileName + "_A" + extn);
						list.add(fileName  + "_B" + extn);
						list.add(fileName  + "_C" + extn);
						list.add(fileName  + "_D" + extn);
					}	
					return list;
				}
			});
			commonPanel.add(expOtherButton, cc.xy(5, 1));
			
			this.add(commonPanel, BorderLayout.CENTER);		
			//eqPanel.add(equivLabel, cc.xy(1,2));
			//eqPanel.add(equivTextField, cc.xy(3,2));
			//eqPanel.add(this.equivsAmountView, cc.xy(3,2));
			//this.add(eqPanel, BorderLayout.WEST);

//			return equivsPanel;
		}

//		public AmountModel getScaleAmountModel() {
//			return scaleAmountModel;
//		}
//
//		public void setScaleAmountModel(AmountModel scaleAmountModel) {
//			this.scaleAmountModel = scaleAmountModel;
//			scaleAmountView.setValue(scaleAmountModel);
//		}	
//		
		public void refreshScaleAmountInTab()
		{
			scaleAmount = (AmountModel) pageModel.getPageHeader().getScale().deepClone();
			scaleAmountView.setValue(scaleAmount);
		}
	}

}
