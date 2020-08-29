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

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.domain.AmountModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.PurityModel;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.utils.CeNDialog;
import com.common.chemistry.codetable.CodeTableCache;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class PurityInfoJDialog extends CeNDialog {//javax.swing.JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -135726353026910656L;
	private ProductBatchModel ab;
	private PurityModel[] purities;
	private List<Properties> al_Purities = new ArrayList<Properties>();
	private JLabel jLabelPurityComments;
	private JLabel jLabelPurityPerc;
	private JLabel jLabelOperator;
	private JLabel jLabelPtDetBy;
	private JLabel jLabelPurity[];
	private JTextField jTextFieldPComment[];
	private JLabel jLabelSourceFile;
	private JLabel jLabelRepPurity;
	private JComboBox jComboBoxOperator[];
	private JTextField jTextFieldPurityPerct[];
	private JPanel jPanel1;
	private JPanel jPanel2;
	private JRadioButton jRadioButtonPurityNo;
	private JRadioButton jRadioButtonPurityYes;
	private JLabel jLabelPurityInfo;
	private JToolBar jToolBar1;
	private JButton buttonOK;
	private JButton buttonCancel;
	private ButtonGroup group;
	private JComponent jComponent;
	private HashMap<String, Properties> purityCache;
	private JTextField jTextFieldSourceFile[];
	private JCheckBox  JCheckBoxRepPurity[];
	//private ButtonGroup groupRepPurityCheckBox;
	private NotebookPageModel pageModel;

	public PurityInfoJDialog(Frame owner, ProductBatchModel ab, List<Properties> al_purities, JComponent jComponent, NotebookPageModel pageModel) {
		super(owner);
		this.ab = ab;
		this.al_Purities = al_purities;
		this.jComponent = jComponent;
		this.pageModel = pageModel;
		initCache();
		initGUI();
		setValues();// Set the values if batch has purity info already
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			reArrange();
			jPanel1 = new JPanel();
			jPanel2 = new JPanel();
			jLabelPtDetBy = new JLabel();
			jLabelOperator = new JLabel();
			jLabelPurityPerc = new JLabel();
			jLabelPurityComments = new JLabel();
			buttonCancel = new JButton("Cancel");
			CeNGUIUtils.styleComponentText(Font.BOLD, buttonCancel);
			buttonOK = new JButton("OK");
			CeNGUIUtils.styleComponentText(Font.BOLD, buttonOK);
			group = new ButtonGroup();
			//groupRepPurityCheckBox = new ButtonGroup();
			BorderLayout thisLayout = new BorderLayout();
			getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setSize(923, 400);
			setLocation(300, 300);
			setTitle("Purity % Info");
			jPanel2.setLayout(null);
			jPanel2.setPreferredSize(new java.awt.Dimension(721, 49));
			buttonOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOkActionPerformed();
				}
			});
			buttonOK.setPreferredSize(new java.awt.Dimension(60, 20));
			buttonOK.setBounds(new java.awt.Rectangle(300, 10, 57, 20));
			jPanel2.add(buttonOK);
			buttonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed();
				}
			});
			buttonCancel.setPreferredSize(new java.awt.Dimension(80, 20));
			buttonCancel.setBounds(new java.awt.Rectangle(400, 10, 80, 20));
			jPanel2.add(buttonCancel);
			jPanel1.setLayout(null);
			jLabelPtDetBy.setText("Purity Determined By");
			jLabelPtDetBy.setPreferredSize(new java.awt.Dimension(120, 20));
			jLabelPtDetBy.setRequestFocusEnabled(false);
			jLabelPtDetBy.setBounds(new java.awt.Rectangle(40, 5, 120, 20));
			jPanel1.add(jLabelPtDetBy);
			jLabelOperator.setText("Operator");
			jLabelOperator.setPreferredSize(new java.awt.Dimension(60, 20));
			jLabelOperator.setBounds(new java.awt.Rectangle(200, 5, 57, 20));
			jPanel1.add(jLabelOperator);
			jLabelPurityPerc.setText("Purity%");
			jLabelPurityPerc.setPreferredSize(new java.awt.Dimension(60, 20));
			jLabelPurityPerc.setBounds(new java.awt.Rectangle(300, 5, 60, 20));
			jPanel1.add(jLabelPurityPerc);
			jLabelPurityComments.setText("Purity Comments");
			jLabelPurityComments.setHorizontalAlignment(SwingConstants.CENTER);
			jLabelPurityComments.setBounds(399, 0, 168, 28);
			jPanel1.add(jLabelPurityComments);
			jLabelRepPurity = new JLabel();
			jPanel1.add(jLabelRepPurity);
			jLabelRepPurity.setText("<html>Representative<br>Purity</htm>");
			jLabelRepPurity.setBounds(580, 0, 120, 32);
			jLabelRepPurity.setFont(jLabelPurityComments.getFont());
			jLabelRepPurity.setForeground(jLabelPurityComments.getForeground());
		    jLabelSourceFile = new JLabel();
			jPanel1.add(jLabelSourceFile);
			jLabelSourceFile.setText("Source File");
			jLabelSourceFile.setBounds(720, 0, 77, 28);
					
			jLabelPurity = new JLabel[al_Purities.size()];
			jComboBoxOperator = new JComboBox[al_Purities.size()];
			jTextFieldPurityPerct = new JTextField[al_Purities.size()];
			jTextFieldPComment = new JTextField[al_Purities.size()];
			jTextFieldSourceFile = new JTextField[al_Purities.size()];
			JCheckBoxRepPurity	= new JCheckBox[al_Purities.size()];
			int rectHeight = 30;
			for (int i = 0; i < al_Purities.size(); i++, rectHeight += 30) {
				Properties listPurities = (Properties) al_Purities.get(i);
				jLabelPurity[i] = new JLabel();
				jComboBoxOperator[i] = new JComboBox();
				jTextFieldPurityPerct[i] = new JTextField();
				jTextFieldPurityPerct[i].addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) {
						JTextField jt = (JTextField) e.getSource();
						checkIfPurityIsUnknown(jt);
					}
				});
				jTextFieldPComment[i] = new JTextField();
				jTextFieldSourceFile[i] = new JTextField();
				JCheckBoxRepPurity[i] = new JCheckBox();
				JCheckBoxRepPurity[i].addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						checkBoxItemStateChanged(e);
					}
				});
				jLabelPurity[i].setText((String) listPurities.get(CodeTableCache.ANALYTIC_PURITY__ANALYTICAL_PURITY_CODE));
				String toolTip = "";
				try {
					String code = listPurities.getProperty(CodeTableCache.ANALYTIC_PURITY__ANALYTICAL_PURITY_CODE);
					toolTip = ((Properties)purityCache.get(code)).getProperty(CodeTableCache.ANALYTIC_PURITY__ANALYTICAL_PURITY_DESC);
				} catch (Exception e) {
					// ignore , not available;
					toolTip = "Not Available in Global Code Tables";
				}
				jLabelPurity[i].setToolTipText(toolTip);
				jLabelPurity[i].setHorizontalAlignment(SwingConstants.RIGHT);
				jLabelPurity[i].setPreferredSize(new java.awt.Dimension(100, 20));
				jLabelPurity[i].setBounds(new java.awt.Rectangle(40, rectHeight, 100, 20));
				jPanel1.add(jLabelPurity[i]);
				jComboBoxOperator[i].setPreferredSize(new java.awt.Dimension(60, 20));
				jComboBoxOperator[i].addItem(">");
				jComboBoxOperator[i].addItem("<");
				jComboBoxOperator[i].addItem("=");
				jComboBoxOperator[i].addItem("~");
				jComboBoxOperator[i].setBounds(new java.awt.Rectangle(200, rectHeight, 60, 20));
				jPanel1.add(jComboBoxOperator[i]);
				jTextFieldPurityPerct[i].setPreferredSize(new java.awt.Dimension(60, 20));
				jTextFieldPurityPerct[i].setBounds(new java.awt.Rectangle(300, rectHeight, 60, 20));
				jPanel1.add(jTextFieldPurityPerct[i]);
				jTextFieldPComment[i].setPreferredSize(new java.awt.Dimension(170, 20));
				jTextFieldPComment[i].setBounds(new java.awt.Rectangle(400, rectHeight, 170, 20));
				jPanel1.add(jTextFieldPComment[i]);
				
				//add rep purity and source file
				JCheckBoxRepPurity[i].setPreferredSize(new java.awt.Dimension(60, 20));
				JCheckBoxRepPurity[i].setBounds(new java.awt.Rectangle(600, rectHeight, 60, 20));
				jPanel1.add(JCheckBoxRepPurity[i]);
				jTextFieldSourceFile[i].setPreferredSize(new java.awt.Dimension(200, 20));
				jTextFieldSourceFile[i].setBounds(new java.awt.Rectangle(680, rectHeight, 170, 20));
				jPanel1.add(jTextFieldSourceFile[i]);
				//groupRepPurityCheckBox.add(JCheckBoxRepPurity[i]);
				
				// if (i==1 || i==2) {
				// jComboBoxOperator[i].setEnabled(false);
				// jTextFieldPurityPerct[i].setEnabled(false);
				// jTextFieldPComment[i].setEditable(false);
				// }
			}
			jPanel1.setPreferredSize(new java.awt.Dimension(800, 602));
			jPanel1.setSize(800, 602);
			JScrollPane sp1 = new JScrollPane(jPanel1);
			sp1.setPreferredSize(new java.awt.Dimension(600, 250));
			getContentPane().add(sp1, BorderLayout.CENTER);
			JScrollPane sp2 = new JScrollPane(jPanel2);
			getContentPane().add(sp2, BorderLayout.SOUTH);
			{
				jToolBar1 = new JToolBar();
				getContentPane().add(jToolBar1, BorderLayout.NORTH);
				{
					jLabelPurityInfo = new JLabel();
					jToolBar1.add(jLabelPurityInfo);
					jLabelPurityInfo.setText("Purity% Information");
					jLabelPurityInfo.setFont(new java.awt.Font("Dialog", 1, 14));
					jLabelPurityInfo.setPreferredSize(new java.awt.Dimension(400, 20));
					jLabelPurityInfo.setBounds(new java.awt.Rectangle(100, 10, 400, 20));
				}
				{
					jRadioButtonPurityYes = new JRadioButton();
					jToolBar1.add(jRadioButtonPurityYes);
					jRadioButtonPurityYes.setText("Purity Available");
					jRadioButtonPurityYes.setPreferredSize(new java.awt.Dimension(100, 20));
					group.add(jRadioButtonPurityYes);
				}
				{
					jRadioButtonPurityNo = new JRadioButton();
					jToolBar1.add(jRadioButtonPurityNo);
					jRadioButtonPurityNo.setText("Purity Unknown");
					jRadioButtonPurityNo.setPreferredSize(new java.awt.Dimension(100, 20));
					jRadioButtonPurityNo.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							purityUnKnownClicked();
						}
					});
					group.add(jRadioButtonPurityNo);
				}
				jToolBar1.setPreferredSize(new java.awt.Dimension(722, 40));
				jToolBar1.setBounds(new java.awt.Rectangle(10, 10, 700, 40));
			}
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					PurityInfoJDialog.this.setVisible(false);
					PurityInfoJDialog.this.dispose();
				}

				public void windowClosed(WindowEvent e) {
					PurityInfoJDialog.this.removeAll();
					PurityInfoJDialog.this.setVisible(false);
				}
			});
			
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	protected void defaultApplyAction() {
		jButtonOkActionPerformed();
	}

	protected void defaultCancelAction() {
		jButtonCancelActionPerformed();
	}	
	
	
	/**
	 * @param i
	 */
	protected void checkIfPurityIsUnknown(JTextField textPerc) {
		if (textPerc.getText().trim().length() > 0 && jRadioButtonPurityNo.isSelected() == true) {
			jRadioButtonPurityNo.setSelected(false);
			jRadioButtonPurityYes.setSelected(true);
		}
	}

	/**
	 * wipe out the values upon confirmataion
	 * 
	 */
	protected void purityUnKnownClicked() {
		boolean isDataEntered = false;
		for (int i = 0; i < al_Purities.size(); i++) {
			if (jTextFieldPurityPerct[i].getText() != null && jTextFieldPurityPerct[i].getText().trim().length() > 0) {
				isDataEntered = true;
				break;
			}
		}
		if (isDataEntered) {
			int selection = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear the data?", "Warning",
					JOptionPane.YES_NO_OPTION);
			if (selection == JOptionPane.YES_OPTION)
				clear();
			else {
				jRadioButtonPurityNo.setSelected(false);
				jRadioButtonPurityYes.setSelected(true);
			}
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		setModal(true);
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dlgSize = this.getPreferredSize();
		setLocation(screenSize.width / 2 - (dlgSize.width / 2), screenSize.height / 2 - (dlgSize.height / 2));
	}

	/**
	 * This static method creates a new instance of this class and shows it inside a new JFrame, (unless it is already a JFrame).
	 * 
	 * It is a convenience method for showing the GUI, but it can be copied and used as a basis for your own code. * It is
	 * auto-generated code - the body of this method will be re-generated after any changes are made to the GUI. However, if you
	 * delete this method it will not be re-created.
	 * @param pageModel 
	 */
	public static void showGUI(Frame owner, ProductBatchModel ab, List<Properties> al_purities, JComponent jComponent, NotebookPageModel pageModel) {
		try {
			PurityInfoJDialog inst = new PurityInfoJDialog(owner, ab, al_purities, jComponent, pageModel);
			inst.setVisible(true);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonCancelActionPerformed() {
		setVisible(false);
		this.dispose();
	}

	/** Auto-generated event handler method */
	protected void jButtonOkActionPerformed() {
		if (ab == null) {
			setVisible(false);
			this.dispose();
			return;
		}
		String puirty = "";
		String operator = "";
		String purityPerc = "";
		double d_purityPerc = 0;
		String comments = "";
		ProductBatchModel pb = (ProductBatchModel) ab;
		if (jRadioButtonPurityNo.isSelected())
		{
			JTextArea jep = (JTextArea) jComponent;
			jep.setText("");
			jep.setToolTipText("");
			pb.setAnalyticalPurityList(new ArrayList<PurityModel>());
			pageModel.setModelChanged(true);
			this.dispose();
			return;
		}
		ArrayList<PurityModel> selectedPurities = new ArrayList<PurityModel>();
		boolean isRepPuritySelected = false;
		for (int i = 0; i < al_Purities.size(); i++) {
			purityPerc = jTextFieldPurityPerct[i].getText();
			try {
				if (purityPerc != null && purityPerc.trim().length() > 0)
					d_purityPerc = Double.parseDouble(purityPerc);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Purity(%) is invalid for " + jLabelPurity[i].getText(), "Input Error",
						JOptionPane.ERROR_MESSAGE);
				this.dispose();
				return;
			}
			
			//validate if the representative purity is selected and purity info not added
			boolean isRepresentativePurity = JCheckBoxRepPurity[i].isSelected();
			if (isRepresentativePurity) {
				isRepPuritySelected = true;
			}
			String sourceFileName = jTextFieldSourceFile[i].getText();
			if(isRepresentativePurity)
			{
				if(StringUtils.isBlank(purityPerc))
				{
					JOptionPane.showMessageDialog(this, 
                            "Purity(%) is required for representative purity " + jLabelPurity[i].getText(), 
                            "Input Error",
							JOptionPane.ERROR_MESSAGE);
					//this.dispose();
					jTextFieldPurityPerct[i].requestFocus(); 
					return;
				}
				
			}
			if (StringUtils.isNotBlank(purityPerc)) {
				puirty = jLabelPurity[i].getText();
				String dscr = purityCache.get(puirty).getProperty(CodeTableCache.ANALYTIC_PURITY__ANALYTICAL_PURITY_CODE);
				operator = jComboBoxOperator[i].getSelectedItem().toString();
				comments = jTextFieldPComment[i].getText();
				AmountModel amodel = new AmountModel(UnitType.SCALAR);
				amodel.setValue(d_purityPerc);
				PurityModel p = new PurityModel(puirty, 
							                    dscr, 
							                    operator, 
							                    amodel,
							                    null, 
							                    comments,
							                    isRepresentativePurity,
							                    sourceFileName);
				//Purity p = new PurityModel(puirty, dscr, operator, new Amount(d_purityPerc,(new Amount(UnitType.SCALAR)).getUnit()),
				//		null, comments,isRepresentativePurity,sourceFileName);
				selectedPurities.add(p);
			}
			
			
		}
		if (!isRepPuritySelected)
		{
			JOptionPane.showMessageDialog(this, "A Single Representative Purity(%) is required.", "Input Error",
					JOptionPane.ERROR_MESSAGE);	
			return;
		}		
		// after finalizing the AbstractBatch class
		setVisible(false);
		JTextArea jep = (JTextArea) jComponent;
		jep.setText(BatchAttributeComponentUtility.getPuritiesListAsString(selectedPurities));
		jep.setToolTipText(PurityModel.toToolTipString(selectedPurities));
		jep.setCaretPosition(0);
		pb.setAnalyticalPurityList(selectedPurities);
		pageModel.setModelChanged(true);
		this.dispose();
	}

	public ProductBatchModel getAbstractBatch() {
		return ab;
	}

	public void setAbstractBatch(ProductBatchModel ab) {
		this.ab = ab;
	}

	public PurityModel[] getPurities() {
		return purities;
	}

	public void setPurities(PurityModel[] purities) {
		this.purities = purities;
	}

	public List<Properties> getALPurities() {
		return al_Purities;
	}

	public void setALPurities(ArrayList<Properties> al_Purities) {
		this.al_Purities = al_Purities;
	}

	public void reArrange() {
		ArrayList<Properties> sortedList = new ArrayList<Properties>();
		if (al_Purities == null)
			al_Purities = new ArrayList<Properties>();
		Properties prop = new Properties();
		prop.setProperty(CodeTableCache.ANALYTIC_PURITY__ANALYTICAL_PURITY_CODE, "LCMS-UV");
		sortedList.add(prop);
		
		prop = new Properties();
		prop.setProperty(CodeTableCache.ANALYTIC_PURITY__ANALYTICAL_PURITY_CODE, "NMR");
		sortedList.add(prop);
		
		prop = new Properties();
		prop.setProperty(CodeTableCache.ANALYTIC_PURITY__ANALYTICAL_PURITY_CODE, "HPLC");
		sortedList.add(prop);
		
		prop = new Properties();
		prop.setProperty(CodeTableCache.ANALYTIC_PURITY__ANALYTICAL_PURITY_CODE, "LCMS-UV");
		sortedList.add(prop);
		
		List codes = new ArrayList();
		codes.add("LCMS-ELS");
		codes.add("LCMS-UC");
		codes.add("NMR");
		codes.add("HPLC");
		
		for (int i = 0; i < al_Purities.size(); i++) {
			Properties al1 = (Properties) al_Purities.get(i);
			String code = al1.getProperty(CodeTableCache.ANALYTIC_PURITY__ANALYTICAL_PURITY_CODE);
			if (!codes.contains(code)) {
				sortedList.add(al1);
			}
		}
		al_Purities = sortedList;
	}

	/**
	 * populates the fields with the existing values
	 * 
	 */
	public void setValues() {
		ProductBatchModel pb = (ProductBatchModel) ab;
		if (pb != null) {
			ArrayList<PurityModel> al_current = (ArrayList<PurityModel>) pb.getAnalyticalPurityList();
			if (al_current.size() == 0) {
				jRadioButtonPurityNo.setSelected(true);
				return;
			} else
				jRadioButtonPurityYes.setSelected(true);
			for (int i = 0; i < al_current.size(); i++) {
				PurityModel p =  al_current.get(i);
				for (int j = 0; j < al_Purities.size(); j++) {
					Properties al1 = (Properties) al_Purities.get(j);
					if (al1.containsKey(p.getCode())) {
						jComboBoxOperator[j].setSelectedItem(p.getOperator());
						jTextFieldPurityPerct[j].setText(p.getPurityValue().toString());
						jTextFieldPComment[j].setText(p.getComments());
						if(p.isRepresentativePurity())
						{
						 this.JCheckBoxRepPurity[j].setSelected(true);	
						}
						this.jTextFieldSourceFile[j].setText(p.getSourceFile());
						break;
					}
				}
			}
		}
	}

	public void clear() {
		for (int i = 0; i < al_Purities.size(); i++) {
			jTextFieldPurityPerct[i].setText("");
			jTextFieldPComment[i].setText("");
			jTextFieldSourceFile[i].setText("");
			JCheckBoxRepPurity[i].setSelected(false);
		}
	}

	public void initCache() {
		purityCache = new HashMap<String, Properties>();
		for (int i = 0; i < al_Purities.size(); i++) {
			// first element of the ArrayList is the Purity Code
			purityCache.put(((Properties) al_Purities.get(i)).get(CodeTableCache.ANALYTIC_PURITY__ANALYTICAL_PURITY_CODE).toString(), al_Purities.get(i));
		}
	}

	public void dispose() {
		this.ab = new ProductBatchModel();
		this.jComponent.removeAll();
		this.jComponent = null;
		super.dispose();
	}
	
	
	
	protected void checkBoxItemStateChanged(ItemEvent e)
	{
	    Object source = e.getItemSelectable();

	   if (e.getStateChange() == ItemEvent.SELECTED)
	    {
		   for (int i = 0; i < al_Purities.size(); i++) {
			   if(source != JCheckBoxRepPurity[i])
			   {  if(JCheckBoxRepPurity[i].isSelected())
			   {
				   JCheckBoxRepPurity[i].setSelected(false);  
			   }
			   }
		   }
	    }
	      
	}
}