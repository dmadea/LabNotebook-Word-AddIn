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
package com.chemistry.enotebook.client.gui.page.analytical.parallel.batch;

import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.batch.BatchEditListener;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionEvent;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionListener;
import com.chemistry.enotebook.client.gui.page.experiment.table.ParallelCeNUtilObject;
import com.chemistry.enotebook.domain.AnalysisModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.PlateWell;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.utils.Decoder;
import com.chemistry.enotebook.utils.ProductBatchStatusMapper;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Iterator;
import java.util.List;

public class AnalyticalBatchEditPanel extends javax.swing.JPanel implements BatchSelectionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3645819916921449074L;
	public static final Font BOLD_LABEL_FONT = new java.awt.Font("Arial", Font.BOLD, 12);
	public static final Font PLAIN_LABEL_FONT = new java.awt.Font("Arial", Font.PLAIN, 12);
	public static final Font EDIT_FONT_SMALL = new java.awt.Font("Arial", Font.PLAIN, 11);
	public static final Font EDIT_FONT_LARGE = new java.awt.Font("Arial", Font.PLAIN, 12);
	public static final Font COMBO_FONT = new java.awt.Font("Arial", Font.BOLD, 11);
	public static final Font BUTTON_FONT = new java.awt.Font("Arial", Font.BOLD, 11);
	//private String chimeString = "pVTLasNADLwb/A865JpF0r6PJe2p2C2k9AdCKYH+/7la22m3tg7uRggjxuNhpJW37wDOl+vbx+f1Aj/Bj33Xd0AWyAFglb+Rc4Z3RkQhSqAJkZMURzTZ+Txj8hrhBH819FzJSMXeUSXzsl9GPASOXCoyIbe6kY8zx3BrKtVuxn+6cbFU1lC7G2uYvZ19RUR3R1PsZsE7mipueGmK2904w8GH2Rch125OX/v3RjxU6xewcf2cbIuNi0zK1ChTBkvLRCym0CgjJ7U0tdni/SdF5TeW5wqeMdqygafcsJ0Oe0VEqEGBhRoVEaEmHc6KSJzYa5gQiHSYdViZiSgo7LNckM+vAk/l+DDAMB4Rowu27waAp1Euy4NE330D";
	private static final int NUM_ROWS = 16;
	
	private ProductBatchModel productBatchModel;  // vb
	private PlateWell well = null;
	private NotebookPageModel pageModel = null;
	private BatchAttributeComponentUtility componentUtility = null;

	// Attribute name constants
	private static final String NOTEBOOK_BATCH_NUM = "Notebook Batch #:";
	private static final String STATUS = "Status:";
	private static final String BATCH_ANALYTICAL_COMMENTS = "Batch Analytical Comments:";
	private static final String NUM_LINKED_ANALYTICAL_SERVICE_FILES = "# Linked AnalyticalService Files";
	private static final String VIEW_PURIFICATION_SERVICE_DATA = "View PurificationService Data:";
	private static final String QUICK_LINK_BATCH = "Quick Link Batch:";
	
	private static final String NMR = "NMR";
	private static final String MS = "MS";
	private static final String LC_MS = "LC-MS";
	private static final String GC_MS = "GC-MS";
	private static final String SFC_MS = "SFC-MS";
	private static final String HPLC = "HPLC";
	private static final String IR = "IR";
	private static final String UV_VIS = "UV-VIS";
	private String[] analyticalServiceInstr = {NMR, MS, LC_MS, GC_MS, SFC_MS, HPLC, IR, UV_VIS};
	private String[] analyticalServiceCounts = new String[9];

	// Flags
	private boolean isEditable = true;
	private boolean isLoading = false;
	private boolean isChanging = false;
	
	// GUI components

	// Notebook batch number
	private JLabel jLabelNbkBatchNum;
	private JLabel jLabelNbkBatchNumNbkPageValue;
	// Status
	private JLabel jLabelStatus;
	private CeNComboBox jComboBoxStatus;
	// Batch comments
	private JLabel jLabelBatchComments;
	private JTextArea jTextAreaBatchComments;
	private JScrollPane jScrollPaneBatchComments;
	private JPanel jPanelBatchComments;
	// AnalyticalService
	private JLabel jLabelNumLinkedAnalyticalServiceFiles;
	private JLabel[] analyticalServiceSources = new JLabel[analyticalServiceInstr.length];
	// Buttons
	private JLabel jLabelViewPurificationServiceData;
	private JButton jButtonViewPurificationServiceData;
	private JLabel jLabelQuickLinkBatch;
	private JButton jButtonQuickLinkBatch;

	/**
	 * Create a new BatchEditPanel -- This should
	 * only happen once when an experiment loads.
	 */
	public AnalyticalBatchEditPanel(NotebookPageModel pageModel) {
	//public AnalyticalBatchEditPanel() {
		this.isLoading = true;  // vb 7/16
		this.pageModel = pageModel;
		//this.componentUtility = new BatchAttributeComponentUtility();
		this.initGui();
		//this.populate();
		this.setLayout(new FormLayout("left:5dlu, pref", "pref"));
		CellConstraints cc = new CellConstraints();
		this.add(this.getPanel(), cc.xy(2, 1));
		this.isLoading = false; // vb 7/16
		this.populate();
	}
	
	/**
	 * Batch selection has changed.  Recreate the panel and refresh the parent's panel.
	 * @param event contains the PlateWell object
	 */
	public void batchSelectionChanged(BatchSelectionEvent event) {
		Object obj = event.getSubObject();
		if (obj instanceof PlateWell) {			
			PlateWell well = (PlateWell) obj;
			this.well = well;
			this.productBatchModel = (ProductBatchModel) well.getBatch();
		} else if (obj instanceof ProductBatchModel) {
			this.productBatchModel = (ProductBatchModel) obj;
		} else if (obj instanceof ParallelCeNUtilObject) {
			ParallelCeNUtilObject utilObj = (ParallelCeNUtilObject) obj;
			this.productBatchModel = (ProductBatchModel) utilObj.getBatch();
		} else {
			this.productBatchModel = null;
		}
		this.populate();
		this.removeAll();
		this.setLayout(new FormLayout("left:5dlu, pref", "pref"));
		CellConstraints cc = new CellConstraints();
		this.add(this.getPanel(), cc.xy(2, 1));
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Initialize the panel widgets.
	 *
	 */
	private void initGui() {
		jLabelNbkBatchNum = makeLabel(NOTEBOOK_BATCH_NUM, PLAIN_LABEL_FONT);
		jLabelNbkBatchNumNbkPageValue = makeLabel("", PLAIN_LABEL_FONT);
		jLabelStatus = makeLabel(STATUS, PLAIN_LABEL_FONT);
		jComboBoxStatus = new CeNComboBox();
		jComboBoxStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isLoading)
					updateProperty(AnalyticalBatchEditPanel.STATUS);
			}
		});	
		// Batch comments
		jLabelBatchComments = makeLabel(BATCH_ANALYTICAL_COMMENTS, PLAIN_LABEL_FONT);
		jTextAreaBatchComments = this.makeTextArea(BATCH_ANALYTICAL_COMMENTS, true); //new JTextArea();
		jTextAreaBatchComments.setText("-none-");
		jScrollPaneBatchComments = new JScrollPane(jTextAreaBatchComments);
		jScrollPaneBatchComments.setBounds(new java.awt.Rectangle(0, 350, 295, 60));
		jScrollPaneBatchComments.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		jPanelBatchComments = new JPanel();
		jPanelBatchComments.setLayout(null);
		jPanelBatchComments.setPreferredSize(new java.awt.Dimension(300, 414));
		jPanelBatchComments.add(jScrollPaneBatchComments);
		// AnalyticalService file totals
		jLabelNumLinkedAnalyticalServiceFiles = makeLabel(NUM_LINKED_ANALYTICAL_SERVICE_FILES, PLAIN_LABEL_FONT);
		for (int i=0; i<this.analyticalServiceInstr.length; i++) {
			this.analyticalServiceSources[i] = this.makeLabel(analyticalServiceInstr[i], PLAIN_LABEL_FONT);
		}
		// Buttons
		jLabelViewPurificationServiceData = makeLabel(VIEW_PURIFICATION_SERVICE_DATA, PLAIN_LABEL_FONT);
		jButtonViewPurificationServiceData = new JButton("P");
		jButtonViewPurificationServiceData.setFont(BUTTON_FONT);
		jLabelQuickLinkBatch = makeLabel(QUICK_LINK_BATCH, PLAIN_LABEL_FONT);
		jButtonQuickLinkBatch = new JButton("Q");
		jButtonQuickLinkBatch.setFont(BUTTON_FONT);
		jButtonQuickLinkBatch.setPreferredSize(new Dimension(20, 20));
	    ProductBatchStatusMapper.getInstance().fillComboBox(this.jComboBoxStatus);
	}

	/**
	 * method populates the batchEditPanel Pass null object to blank the values.
	 * 
	 */
	public void populate() {
		try {
			if (isLoading)
				return;
			isLoading = true;
			isEditable = this.pageModel.isEditable();
			if (this.productBatchModel == null) {
				isEditable = false;
				jLabelNbkBatchNumNbkPageValue.setText("");
				this.analyticalServiceCounts[0] = "";
				this.analyticalServiceCounts[1] = "";
				this.analyticalServiceCounts[2] = "";
				this.analyticalServiceCounts[3] = "";
				this.analyticalServiceCounts[4] = "";
				this.analyticalServiceCounts[5] = "";
				this.analyticalServiceCounts[6] = "";
				this.analyticalServiceCounts[7] = "";
				this.jTextAreaBatchComments.setText("");
				this.enableComponents(false);
			} else {
				this.enableComponents(isEditable);
				String nbkBatchNumber = this.productBatchModel.getBatchNumber().getBatchNumber();
				//String conststr = nbkBatchNumber.substring(0, nbkBatchNumber.lastIndexOf("-") + 1);
				jLabelNbkBatchNumNbkPageValue.setText(nbkBatchNumber);
				// Get the selectivity and continue status
				int selectivityStatus = this.productBatchModel.getSelectivityStatus();
				int continueStatus = this.productBatchModel.getContinueStatus();
				ProductBatchStatusMapper.getInstance().setStatus(this.jComboBoxStatus, selectivityStatus, continueStatus);
				// Analysis -- linked AnalyticalService files
				int numNMRFiles = 0;
				int numMSFiles = 0;
				int numLCMSFiles = 0;
				int numCompoundManagementSFiles = 0;
				int numSFCMSFiles = 0;
				int numHPLCFiles = 0;
				int numIRFiles = 0;
				int numUVVISFiles = 0;
				//List analysisModelList = this.productBatchModel.getAnalysisModelList();
				List analysisModelList = this.pageModel.getAnalysisListForBatch(productBatchModel.getBatchNumberAsString());
				for (Iterator it = analysisModelList.iterator(); it.hasNext();) {
					AnalysisModel analysisModel = (AnalysisModel) it.next();
					String instrumentType = analysisModel.getInstrumentType().toUpperCase();
					if (instrumentType.indexOf(NMR) >= 0) numNMRFiles++;
					else if (instrumentType.equals(MS)) numMSFiles++;
					else if (instrumentType.indexOf(LC_MS) >= 0) numLCMSFiles++;
					else if (instrumentType.indexOf(GC_MS) >= 0) numCompoundManagementSFiles++;
					else if (instrumentType.indexOf(SFC_MS) >= 0) numSFCMSFiles++;
					else if (instrumentType.indexOf(HPLC) >= 0) numHPLCFiles++;
					else if (instrumentType.indexOf(IR) >= 0) numIRFiles++;
					else if (instrumentType.indexOf(UV_VIS) >= 0) numUVVISFiles++;
				}
				this.analyticalServiceCounts[0] = "" + numNMRFiles;
				this.analyticalServiceCounts[1] = "" + numMSFiles;
				this.analyticalServiceCounts[2] = "" + numLCMSFiles;
				this.analyticalServiceCounts[3] = "" + numCompoundManagementSFiles;
				this.analyticalServiceCounts[4] = "" + numSFCMSFiles;
				this.analyticalServiceCounts[5] = "" + numHPLCFiles;
				this.analyticalServiceCounts[6] = "" + numIRFiles;
				this.analyticalServiceCounts[7] = "" + numUVVISFiles;
				String batchComments = productBatchModel.getAnalyticalComment();
				if (batchComments != null && ! batchComments.equalsIgnoreCase("null"))
					this.jTextAreaBatchComments.setText(batchComments);
				else
					this.jTextAreaBatchComments.setText("");
				// Enable the components 
				//this.enableComponents(true);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isLoading = false;
	}
	
	private void enableComponents(boolean enable) {
		this.jComboBoxStatus.setEnabled(enable);
		this.jButtonQuickLinkBatch.setEnabled(enable);
		this.jTextAreaBatchComments.setEnabled(enable);
	}
	
	/**
	 * Builds the panel using the Form Layout.
	 * @return
	 */
	private JPanel getPanel() {
		// Set row format
		StringBuffer rowFormatBuff = new StringBuffer();
		for (int i=0; i<NUM_ROWS-1; i++) {
			rowFormatBuff.append("pref, ");
		}
		rowFormatBuff.append("pref");
		FormLayout layout = new FormLayout("35dlu, 35dlu, 35dlu, 40dlu, 7dlu, pref " +  // 1, 2 (col 1 and space)
										   "pref, 25dlu, 35dlu, 7dlu", // 3, 4 (col 2 and col 3)
										   rowFormatBuff.toString());						   
		// Set row groups (all rows are in group)
		int[][] rowGroups = new int[1][NUM_ROWS];
		for (int i=0; i<NUM_ROWS; i++) {
			rowGroups[0][i] = i+1;
		}
		layout.setRowGroups(rowGroups);

		int row = 2;
		int col1 = 1;
		int col2 = 2;
		PanelBuilder builder = new PanelBuilder(layout);
		// For debug
		//PanelBuilder builder = new PanelBuilder(layout, new FormDebugPanel());
		CellConstraints cc = new CellConstraints();
		builder.add(this.getChimePanel(),               cc.xywh(7,   row,   3,  6)); row++;
		builder.add(new JLabel("Notebook"),             cc.xyw  (1,   row, 2));   row++;
		builder.add(new JLabel("Batch #"),              cc.xyw  (1,   row, 2));   
		builder.add(jLabelNbkBatchNumNbkPageValue,      cc.xyw (2,   row,   3));  row++; 
		builder.add(jLabelStatus,                       cc.xy  (1,   row));
		builder.add(jComboBoxStatus,    				cc.xyw (2,   row,  3));      row++; row++;
		// AnalyticalService file counts
		builder.add(jLabelNumLinkedAnalyticalServiceFiles,          cc.xyw (col1,   row,  4));      
		builder.add(new JLabel("____________________"), cc.xyw(col1, row, 4)); row++; 
		int rightIndex = analyticalServiceSources.length/2;
		for (int leftIndex=0; leftIndex<this.analyticalServiceSources.length/2; leftIndex++) {
			builder.add(analyticalServiceSources[leftIndex],                cc.xy(1, row)); 
			builder.add(new JLabel(analyticalServiceCounts[leftIndex]),     cc.xy(2, row));
			builder.add(analyticalServiceSources[rightIndex],             cc.xy(3, row)); 
			builder.add(new JLabel(analyticalServiceCounts[rightIndex++]),     cc.xy(4, row)); row++;
		}
		row++;
		builder.add(this.jLabelBatchComments,           cc.xyw (col1,   row, 4)); row++;
		builder.add(this.jScrollPaneBatchComments,      cc.xywh(col2, row, 8, 3));
		builder.add(this.jLabelViewPurificationServiceData,             cc.xyw  (7, 9, 1));
		builder.add(this.jButtonViewPurificationServiceData,            cc.xywh  (8, 9, 1, 2));
		builder.add(this.jLabelQuickLinkBatch,          cc.xyw  (7, 11, 1));
		builder.add(this.jButtonQuickLinkBatch,         cc.xywh   (8, 11, 1, 2));
		
		return builder.getPanel();
	}
	
    /**
     * Create a label object.
     * @param text
     * @param font
     * @return
     */	
	private JLabel makeLabel(String text, Font font) {
		JLabel label = new JLabel(text);
		label.setFont(font);
		return label;
	}

//	/**
//	 * Create a tex field object and set listeners.
//	 * @param text
//	 * @param size
//	 * @param property
//	 * @return
//	 */
//	private JTextField makeTextField(String text, int size, String property) {
//		JTextField textField = new JTextField(text, size);
//		textField.setFont(EDIT_FONT_SMALL);
//		textField.getDocument().addDocumentListener(new BatchDocumentListener());
//		textField.addFocusListener(new PropertyFocusListener(property));
//		return textField;
//	}
//
//    /**
//     * This will go away when amount widget is developed.
//     * @param items
//     * @return
//     */
//	private JComboBox makeComboBox(String[] items) {
//		JComboBox comboBox = new JComboBox();
//		comboBox.setFont(COMBO_FONT);
//		//JComboBoxModel model = new JComboBoxModel();
//		ComboBoxModel model = new DefaultComboBoxModel(items);
//		comboBox.setModel(model);
//		return comboBox;
//	}
//
//	/**
//	 * Create a text area object and set its listeners.
//	 * @param text
//	 * @param rows
//	 * @param cols
//	 * @param property
//	 * @return
//	 */
//	private JTextArea makeTextArea(String text, int rows, int cols, String property, boolean inScrollPane) {
//		JTextArea textArea = new JTextArea(text, rows, cols);
//		textArea.setFont(EDIT_FONT_LARGE);
//		textArea.setLineWrap(true);
//		if (! inScrollPane)
//			textArea.setBorder(new BevelBorder(BevelBorder.LOWERED, null, Color.lightGray, null, Color.black));
//		textArea.getDocument().addDocumentListener(new BatchDocumentListener());
//		textArea.addFocusListener(new PropertyFocusListener(property));
//		return textArea;
//	}
//	
//	private JScrollPane makeScrollPane(JTextArea textarea) {
//		JScrollPane sp = new JScrollPane(textarea);
//		sp.setBounds(new java.awt.Rectangle(0, 350, 295, 60));
//		sp.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
//		//sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		//sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//		//sp.setViewportBorder(new EmptyBorder(new Insets(0,0,0,0)));
//		return sp;
//	}
//

	protected boolean isEditing() {
		//	return (jTextFieldLotNo.hasFocus() || amtCompBatchWt.hasFocus());
		return true;
	}

	protected void stopEditing() {
//	if (!isLoading) {
//		if (jTextFieldLotNo.hasFocus()) {
//			updateBatchObject(BatchEditPanel.LOT);
//			fireEditingStopped();
//		}
//		if (amtCompBatchWt.hasFocus()) {
//			amtCompBatchWt.stopEditing();
//		}
//		this.requestFocus();
	}
	
	public void addBatchEditListener(BatchEditListener o) {
//		if (!editListeners.contains(o))
//			editListeners.add(o);
	}
	
	public boolean isLoading() {
		return isLoading;
	}
	
	/**
	 * @param pageModel the pageModel to set
	 */
	public void setPageModel(NotebookPageModel pageModel) {
		this.pageModel = pageModel;
	}
	
	
	private void insertBlankItem(JComboBox comboBox) {
		comboBox.insertItemAt("", 0);
	}
	
	/**
	 * Get the batch molecular diagram.
	 * @return
	 */
	public JPanel getChimePanel() {
		ChemistryPanel chime = new ChemistryPanel();
		String stringSketch = "";
		if (this.productBatchModel != null) {
			stringSketch = this.productBatchModel.getCompound().getStringSketchAsString();
			stringSketch = Decoder.decodeString(stringSketch);
		}
		chime.setMolfileData(stringSketch);
		return chime;
	}
	
	private void enableSaveButton() {
		if (this.productBatchModel != null) {
			MasterController.getGUIComponent().enableSaveButtons();
			this.productBatchModel.setModelChanged(true);
			this.pageModel.setModelChanged(true);
			// The singleton page model is set to uneditable.  Why?
			// Temporary fix...
			this.pageModel.setEditable(true);
		}
	}
	
	private void updateProperty(String property) {
		if (isLoading || !isEditable || this.productBatchModel == null)
			return;
		isLoading = true;
		if (property != null && property.length() > 0) {
			try {
				if (property.equals(STATUS)) {
					String selectedItem = (String) jComboBoxStatus.getSelectedItem();
					int selectivityStatus = ProductBatchStatusMapper.getInstance().getSelectivityStatus(selectedItem);
					if (selectivityStatus >= 0) 
						this.productBatchModel.setSelectivityStatus(selectivityStatus);
					int continueStatus = ProductBatchStatusMapper.getInstance().getContinueStatus(selectedItem);
					if (continueStatus >= 0)
						this.productBatchModel.setContinueStatus(continueStatus);
					enableSaveButton();
				} else if (property.equals(BATCH_ANALYTICAL_COMMENTS)) {
					productBatchModel.setAnalyticalComment(this.jTextAreaBatchComments.getText());
					enableSaveButton();
				} 
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		isLoading = false;
	}
	
	private String processString(String instr) {
		if (instr == null)
			return "";
		else if (instr.equalsIgnoreCase("null"))
			return "";
		else return instr;
	}

// To make this work you have to remove pageModel
//
//	public static void main(String[] args) {
//		AnalyticalBatchEditPanel panel = new AnalyticalBatchEditPanel();
//		JFrame frame = new JFrame();
//		frame.setTitle("Reaction Details");
//		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		frame.getContentPane().add(panel);
//		frame.setSize(800, 600);
//		frame.pack();
//		frame.setVisible(true);		
//
	
	/**
	 * Create a text area object and set its listeners.
	 * @param text
	 * @param rows
	 * @param cols
	 * @param property
	 * @return
	 */
	private JTextArea makeTextArea(String property, boolean inScrollPane) {
		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setFont(EDIT_FONT_LARGE);
		if (! inScrollPane)
			textArea.setBorder(new BevelBorder(BevelBorder.LOWERED, null, Color.lightGray, null, Color.black));
		textArea.getDocument().addDocumentListener(new BatchDocumentListener());
		textArea.addFocusListener(new PropertyFocusListener(property));
		return textArea;
	}
	
	class PropertyFocusListener extends FocusAdapter {
		String property = "";
		public PropertyFocusListener(String property) {
			this.property = property;
		}

		public void focusGained(FocusEvent evt) {
			//if (property != null && property.length() > 0)
			//	validatePropertyPreConditions(property);
			//return;
		}

		public void focusLost(FocusEvent evt) {
			if (isChanging) {
				if (property != null && property.length() > 0)
					updateProperty(property);
			}
			isChanging = false;
		}
	}

	/**
	 *  Enable the save button when a text field or text area is changed.
	 */
	public class BatchDocumentListener implements DocumentListener {
		public void changedUpdate(DocumentEvent e) {
			isChanging = true;
		}
		public void insertUpdate(DocumentEvent e) {
			isChanging = true;
		}
		public void removeUpdate(DocumentEvent e) {
			isChanging = true;
		}
		
	}

//	/**
//	 *  Enable the save button when a text field or text area is changed.
//	 */
//	class BatchDocumentListener implements DocumentListener {
//		public void changedUpdate(DocumentEvent e) {
//			isChanging = true;
//		}
//		public void insertUpdate(DocumentEvent e) {
//			isChanging = true;
//		}
//		public void removeUpdate(DocumentEvent e) {
//			isChanging = true;
//		}
//		
//	}
//	
}
