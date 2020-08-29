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

import com.chemistry.enotebook.analyticalservice.classes.AnalyticalServiceMetaData;
import com.chemistry.enotebook.analyticalservice.delegate.AnalyticalServiceDelegate;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows user to select which instrument types (NMR, LCMS, etc.).
 * 
* This code was generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a
* for-profit company or business) then you should purchase
* a license - please visit www.cloudgarden.com for details.
*/
public class PrintSelectInstTypesDialog 
	extends javax.swing.JDialog 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 159847614965573762L;
	private JPanel jPanelSpace;
	private JCheckBox jCheckBox1AddDynamically;
	private JPanel jPanelMain;
	private JPanel jPanelFiller;
	private JButton jButtonCancel;
	private JButton jButtonOk;
	private JPanel jPanelSouth;
	private JLabel jLabelNorth;
	private List<String> al_AllInsTypes;
	private ArrayList al_SelectedInsTypes;
	private JCheckBox[] jCheckBoxSites;
	private PrintOptions _prtOptions;
	
	private boolean cancelled;
	
	
	public PrintSelectInstTypesDialog() {
		initGUI();
	}

	/**
	 * @param arg0
	 * @throws java.awt.HeadlessException
	 */
	public PrintSelectInstTypesDialog(Frame arg0) throws HeadlessException {
		super(arg0);
		initGUI();
	}
	
	public PrintSelectInstTypesDialog(PrintOptions prtOpt) {
		_prtOptions = prtOpt;
		initGUI();
	}
	
	public PrintSelectInstTypesDialog(Frame arg0, PrintOptions prtOpt) {
		super(arg0);
		_prtOptions = prtOpt;
		initGUI();
	}
	
	/**
	* Initializes the GUI.
	* Auto-generated code - any changes you make will disappear.
	*/
	public void initGUI(){
		try {
			preInitGUI();
	
			jLabelNorth = new JLabel();
			jPanelSouth = new JPanel();
			jPanelFiller = new JPanel();
			jButtonOk = new JButton();
			jPanelSpace = new JPanel();
			jButtonCancel = new JButton();
			jPanelMain = new JPanel();
			jCheckBox1AddDynamically = new JCheckBox();
	
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setTitle("Select Instrument Types");
			this.setModal(true);
			this.setSize(new java.awt.Dimension(367,186));
	
			jLabelNorth.setText("<HTML><b>Select the <U>Instrument Type</U> to be included in your printouts:</html>");
			jLabelNorth.setHorizontalAlignment(SwingConstants.CENTER);
			jLabelNorth.setPreferredSize(new java.awt.Dimension(338,19));
			this.getContentPane().add(jLabelNorth, BorderLayout.NORTH);
	
			jPanelSouth.setPreferredSize(new java.awt.Dimension(338,32));
			this.getContentPane().add(jPanelSouth, BorderLayout.SOUTH);
	
			jPanelFiller.setVisible(true);
			jPanelFiller.setPreferredSize(new java.awt.Dimension(149,10));
			jPanelSouth.add(jPanelFiller);
	
			jButtonOk.setText("Ok");
			jButtonOk.setFont(new java.awt.Font("sansserif",1,11));
			jPanelSouth.add(jButtonOk);
			jButtonOk.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOkActionPerformed(evt);
				}
			});
	
			jPanelSpace.setVisible(true);
			jPanelSouth.add(jPanelSpace);
	
			jButtonCancel.setText("Cancel");
			jButtonCancel.setFont(new java.awt.Font("sansserif",1,11));
			jPanelSouth.add(jButtonCancel);
			jButtonCancel.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed(evt);
				}
			});
	
			GridLayout jPanelMainLayout = new GridLayout(1,1);
			jPanelMain.setLayout(jPanelMainLayout);
			jPanelMainLayout.setRows(1);
			jPanelMainLayout.setHgap(0);
			jPanelMainLayout.setVgap(0);
			jPanelMainLayout.setColumns(1);
			this.getContentPane().add(jPanelMain, BorderLayout.CENTER);
	
			jCheckBox1AddDynamically.setText("AddDynamically");
			jPanelMain.add(jCheckBox1AddDynamically);
	
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e){
					PrintSelectInstTypesDialog.this.setVisible(false);
					PrintSelectInstTypesDialog.this.dispose();
				}
				public void windowClosed(WindowEvent e){
					PrintSelectInstTypesDialog.this.removeAll();
					PrintSelectInstTypesDialog.this.setVisible(false);
				}
			});
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}
	
	/** Add your pre-init code in here 	*/
	public void preInitGUI()
	{
		AnalyticalServiceDelegate analyticalServiceDelegate;
		try {
			analyticalServiceDelegate = new AnalyticalServiceDelegate(); 
			AnalyticalServiceMetaData analyticalServiceServiceMetaData = analyticalServiceDelegate.retrieveAnalyticalServiceMetaData();
			al_AllInsTypes = analyticalServiceServiceMetaData.getInstrumentTypesSupported();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	/** Add your post-init code in here 	*/
	public void postInitGUI()
	{
		al_SelectedInsTypes = new ArrayList();

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		jPanelMain.removeAll();
		if (al_AllInsTypes != null) {
			if (!al_AllInsTypes.contains("All")) al_AllInsTypes.add("All");
		
			jCheckBoxSites = new JCheckBox[al_AllInsTypes.size()];
			for (int i=0, j=25; i < al_AllInsTypes.size(); i++, j+=15) {
				Action action = createAction(al_AllInsTypes.get(i).toString());
				jCheckBoxSites[i] = new JCheckBox(action);
				jCheckBoxSites[i].setText(al_AllInsTypes.get(i).toString());
				CeNGUIUtils.styleComponentText(Font.BOLD, jCheckBoxSites[i]);
				jCheckBoxSites[i].setPreferredSize(new java.awt.Dimension(30,20));
				if (i%2 == 0)
					jCheckBoxSites[i].setBounds(new java.awt.Rectangle(12,j,100,20));
				else
					jCheckBoxSites[i].setBounds(new java.awt.Rectangle(135,j-15,100,20));
				
				if (_prtOptions.qcInstruments.contains(jCheckBoxSites[i].getText())) {
					jCheckBoxSites[i].setSelected(true);
					al_SelectedInsTypes.add(jCheckBoxSites[i].getText());
				}
				
				jPanelMain.add(jCheckBoxSites[i]);
			}
		}
	}

	/** Auto-generated main method */
	public static void main(String[] args)
	{
		showGUI();
	}

	/**
	* This static method creates a new instance of this class and shows
	* it inside a new JFrame, (unless it is already a JFrame).
	*
	* It is a convenience method for showing the GUI, but it can be
	* copied and used as a basis for your own code.	*
	* It is auto-generated code - the body of this method will be
	* re-generated after any changes are made to the GUI.
	* However, if you delete this method it will not be re-created.	*/
	public static void showGUI() {
		try {
			PrintSelectInstTypesDialog inst = new PrintSelectInstTypesDialog();
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected Action createAction(String label) {
		Action action = new AbstractAction(label) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 9056601527164452641L;

			// This method is called when the button is pressed
			public void actionPerformed(ActionEvent evt) {
				// Perform action
				JCheckBox cb = (JCheckBox)evt.getSource();
    
				// Determine status
				boolean isSel = cb.isSelected();
				if (isSel) {
					al_SelectedInsTypes.add(cb.getText());
				} else {
					// The checkbox is now deselected
					al_SelectedInsTypes.remove(cb.getText());
				}
			}
		};
		return action;
	}
	
	public String formText(ArrayList al) {
		String s_return = "";
		if(al.contains("All")){
			al_AllInsTypes.remove("All");
			s_return = al_AllInsTypes.toString();
			s_return = s_return.replace('[',' ');
			s_return = s_return.replace(']',' ');
		}
		else{
			s_return = al_SelectedInsTypes.toString();
			s_return = s_return.replace('[',' ');
			s_return = s_return.replace(']',' ');
		}
		return s_return.trim();
	}

	/** Auto-generated event handler method */
	protected void jButtonCancelActionPerformed(ActionEvent evt) {
		this.dispose();
	}

	/** Auto-generated event handler method */
	protected void jButtonOkActionPerformed(ActionEvent evt) {
		cancelled = false;
		_prtOptions.qcInstruments = al_SelectedInsTypes;
		_prtOptions.qcInstrumentsDesc = formText(al_SelectedInsTypes);
		this.dispose();
	}
	
	/**
	 * @return _prtOptions
	 */
	public PrintOptions getSelectedInstrumentTypes() {
		cancelled = true;
		setVisible(true);
		if (!cancelled) {
			_prtOptions.qcAllInstruments = (_prtOptions.qcInstruments.size() == 0);
			_prtOptions.qcSelectInstruments = (_prtOptions.qcInstruments.size() > 0);
			return _prtOptions;
		} else
			return null;
	}
	
	public void dispose() {
		super.dispose();
	}
}
