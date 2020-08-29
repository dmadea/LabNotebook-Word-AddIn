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

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.experiment.datamodel.attachments.Attachment;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Allows the user to select which attachments to include in the printout.
 * 
* This code was generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a
* for-profit company or business) then you should purchase
* a license - please visit www.cloudgarden.com for details.
*/

// note:  this isn't being used yet; user option is all or no attachments

public class PrintSelectAttachmentsDialog extends javax.swing.JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7473987510028330699L;
	private JCheckBox jCheckBoxAddDynamically;
	private JPanel jPanelFiller;
	private JButton jButtonCancel;
	private JButton jButtonOk;
	private JPanel jPanelSouth;
	private JPanel jPanelMain;
	private JLabel jLabelSelectAttachments;
	private PrintOptions _prtOptions;
	private ArrayList atList;
	private ArrayList atResList;
	private NotebookPage _exp;
	private JCheckBox[] jCheckBoxAttach;
	
	public PrintSelectAttachmentsDialog() {
		initGUI();
	}
	
	public PrintSelectAttachmentsDialog(Frame arg0, PrintOptions prtOpt) {
		super(arg0);
		_prtOptions = prtOpt;
		initGUI();
	}
	
	public PrintSelectAttachmentsDialog(Frame arg0, NotebookPage exp, PrintOptions prtOpt) {
		super(arg0);
		_prtOptions = prtOpt;
		_exp = exp;
		initGUI();
	}

	/**
	* Initializes the GUI.
	* Auto-generated code - any changes you make will disappear.
	*/
	public void initGUI(){
		try {
			preInitGUI();
	
			jLabelSelectAttachments = new JLabel();
			jPanelMain = new JPanel();
			jCheckBoxAddDynamically = new JCheckBox();
			jPanelSouth = new JPanel();
			jPanelFiller = new JPanel();
			jButtonOk = new JButton();
			jButtonCancel = new JButton();
	
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setModal(true);
			this.setSize(new java.awt.Dimension(345,262));
	
			jLabelSelectAttachments.setText("<HTML>Select the <U>Attachments</U> to be included in your printouts:");
			jLabelSelectAttachments.setHorizontalAlignment(SwingConstants.CENTER);
			this.getContentPane().add(jLabelSelectAttachments, BorderLayout.NORTH);
	
			BoxLayout jPanelMainLayout = new BoxLayout(jPanelMain,1);
			jPanelMain.setLayout(jPanelMainLayout);
			this.getContentPane().add(jPanelMain, BorderLayout.CENTER);
	
			jCheckBoxAddDynamically.setText("AddDynamically");
			jCheckBoxAddDynamically.setPreferredSize(new java.awt.Dimension(338,185));
			jPanelMain.add(jCheckBoxAddDynamically);
	
			this.getContentPane().add(jPanelSouth, BorderLayout.SOUTH);
	
			jPanelFiller.setVisible(true);
			jPanelFiller.setPreferredSize(new java.awt.Dimension(149,10));
			jPanelSouth.add(jPanelFiller);
	
			jButtonOk.setText("Ok");
			jPanelSouth.add(jButtonOk);
			jButtonOk.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOkActionPerformed(evt);
				}
			});
	
			jButtonCancel.setText("Cancel");
			jPanelSouth.add(jButtonCancel);
			jButtonCancel.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed(evt);
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
		atList = new ArrayList();
		//get list of IPP related attachments
		if (_exp.getAttachmentCache().size()!=0) {
			for (int c=0;c<=_exp.getAttachmentCache().size()-1;c++) {
				Attachment tmpAttach = (Attachment)_exp.getAttachmentCache().getAttachment(c);
//				check to see if attachment is IP related
				//if so store in atList
				if (tmpAttach.isIpRelated()){
					atList.add(tmpAttach);
				}
			}			
		}
	}

	/** Add your post-init code in here 	*/
	public void postInitGUI()
	{
		this.jPanelMain.removeAll();
		jCheckBoxAttach = new JCheckBox[atList.size()];
		for(int i=0,j=25;i<atList.size(); i++,j+=15){
			Action action = createAction(((Attachment)atList.get(i)).getKey());
			action.putValue(((Attachment)atList.get(i)).getDocumentName(), (Attachment)atList.get(i));
			jCheckBoxAttach[i] = new JCheckBox(action);
			jCheckBoxAttach[i].setText(((Attachment)atList.get(i)).getDocumentName());
			CeNGUIUtils.styleComponentText(Font.BOLD, jCheckBoxAttach[i]);
			jCheckBoxAttach[i].setPreferredSize(new java.awt.Dimension(30,20));
			if(i%2 ==0)
				jCheckBoxAttach[i].setBounds(new java.awt.Rectangle(12,j,100,20));
			else
				jCheckBoxAttach[i].setBounds(new java.awt.Rectangle(135,j-15,100,20));
			this.jPanelMain.add(jCheckBoxAttach[i]);
		}
		atResList = new ArrayList();
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
	public static void showGUI(){
		try {
			PrintSelectAttachmentsDialog inst = new PrintSelectAttachmentsDialog();
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * @return Returns the _prtOptions.
	 */
	public PrintOptions get_prtOptions() {
		return _prtOptions;
	}
	
	protected Action createAction(String label){
		Action action = new AbstractAction(label) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -5172719186502036178L;

			// This method is called when the button is pressed
			public void actionPerformed(ActionEvent evt) {
				// Perform action
				JCheckBox cb = (JCheckBox)evt.getSource();
    
				// Determine status
				boolean isSel = cb.isSelected();
				if (isSel) {
					atResList.add(this.getValue(cb.getText()));
				} else {
					// The checkbox is now deselected
					atResList.remove(this.getValue(cb.getText()));
				}
			}
		};
		return action;
	}
	
	public String formText(ArrayList al){
		String s_return = "";
		for (int c=0;c<=al.size()-1;c++) {
			s_return = s_return + ((Attachment)atResList.get(c)).getDocumentName();
			if (c!=al.size()-1) {
				s_return = s_return + ", ";
			}
		}
//		s_return = atResList.toString();
//		
//		s_return = s_return.replace('[',' ');
//		s_return = s_return.replace(']',' ');

		return s_return.trim();
	}

	/** Auto-generated event handler method */
	protected void jButtonCancelActionPerformed(ActionEvent evt){
		this.setVisible(false);
	}

	/** Auto-generated event handler method */
	protected void jButtonOkActionPerformed(ActionEvent evt){
		System.out.println("For debug");
		_prtOptions.attachList = atResList;
		_prtOptions.attachListDesc = formText(atResList);
		this.setVisible(false);
	}
}
