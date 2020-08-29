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

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.contents.NotebookContentsTableModel;
import com.chemistry.enotebook.client.gui.page.NotebookPageLoaderAction;
import com.chemistry.enotebook.client.gui.page.NotebookPageLoaderEvent;
import com.chemistry.enotebook.client.gui.page.NotebookPageLoaderListener;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.utils.CeNJobProgressHandler;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.SwingWorker;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * This dialog is used for both table of contents printing and experiment printing.
 * 
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
public class PrintExperimentDialog extends JDialog 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3443948085545811942L;
	private JLabel jLabelSep;
	private JTextField jTextFieldContentsEnd;
	private JTextField jTextFieldContentsStart;
	private JLabel jLabelContentsNB;
	private JPanel jPanelContentsInfo;
	private JTextArea jTextAreaNBKExp;
	private JLabel jLabelNBKExp;
	private JButton jButtonSetupPrintCont;
	private JLabel ExpDetails;
	private JLabel BatchDataStruc;
	private JLabel QCAnalData;
	private JLabel RegSub;
	private JLabel Attachments;
	private JLabel jLabelAttachments;
	private JLabel jLabelRegSub;
	private JLabel jLabelQCAnalData;
	private JButton jButtonExport;
	private JLabel SignatureFooter;
	private JLabel jLabelFooter;
	private JLabel jLabelBatchDataStruc;
	private JLabel jLabelExpDetails;
	private JLabel jLabelPrintContInc;
	private JPanel jPanelContents;
	private JPanel jPanelRange;
	private JLabel jLabelPrintRange;
	private JPanel jPanelMainCenter;
	private JPanel jPanelFiller2;
	private JPanel jPanelFiller1;
	private JPanel jPanelFiller3;
	private JButton jButtonPreview;
	private JButton jButtonCancel;
	private JButton jButtonOk;
	private JPanel jPanelMainSouth;

	private boolean      expDialog = true;

	private TreeMap      expList = null;
	private PrintOptions printOptions = null;

	private String siteCode = null;
	private int whichButton;

	private static PrintExperimentDialog instance_ = null;


	/**
	 * @param arg0
	 * @throws java.awt.HeadlessException
	 */
	private PrintExperimentDialog(Frame arg0) 
	throws HeadlessException 
	{
		super(arg0);
		initGUI();
	}

	public void dispose() {
		super.dispose();
	}

	/**
	 * Initializes the GUI.
	 * Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI(){
		try {
			preInitGUI();

			jPanelMainCenter = new JPanel();
			jLabelPrintRange = new JLabel();
			jPanelRange = new JPanel();
			jLabelNBKExp = new JLabel();
			jPanelContentsInfo = new JPanel();
			jLabelContentsNB = new JLabel();
			jTextFieldContentsStart = new JTextField();
			jTextFieldContentsEnd = new JTextField();
			jLabelSep = new JLabel();
			jTextAreaNBKExp = new JTextArea();
			jPanelContents = new JPanel();
			jLabelPrintContInc = new JLabel();
			jLabelExpDetails = new JLabel();
			jLabelBatchDataStruc = new JLabel();
			jLabelQCAnalData = new JLabel();
			jLabelRegSub = new JLabel();
			jLabelAttachments = new JLabel();
			jLabelFooter = new JLabel();
			Attachments = new JLabel();
			RegSub = new JLabel();
			QCAnalData = new JLabel();
			BatchDataStruc = new JLabel();
			ExpDetails = new JLabel();
			SignatureFooter = new JLabel();
			jButtonSetupPrintCont = new JButton();
			jPanelMainSouth = new JPanel();
			jButtonPreview = new JButton();
			jButtonExport = new JButton();
			jPanelFiller2 = new JPanel();
			jButtonOk = new JButton();
			jPanelFiller1 = new JPanel();
			jPanelFiller3 = new JPanel();
			jButtonCancel = new JButton();

			BorderLayout thisLayout = new BorderLayout();
			getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			setTitle("Print Experiments");
			setResizable(false);
			setModal(true);
			setSize(new java.awt.Dimension(530,380));

			jPanelMainCenter.setLayout(null);
			jPanelMainCenter.setVisible(true);
			jPanelMainCenter.setPreferredSize(new java.awt.Dimension(525, 287));
			getContentPane().add(jPanelMainCenter, BorderLayout.CENTER);
			jPanelMainCenter.setSize(522, 287);

			jLabelPrintRange.setText("Print Range and Contents:");
			jLabelPrintRange.setFont(new java.awt.Font("sansserif",1,12));
			jLabelPrintRange.setOpaque(true);
			jLabelPrintRange.setBounds(14, 5, 154, 21);
			jPanelMainCenter.add(jLabelPrintRange);

			jPanelRange.setLayout(null);
			jPanelRange.setVisible(true);
			jPanelRange.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			jPanelRange.setName("");
			jPanelRange.setBounds(7, 14, 511, 112);
			jPanelMainCenter.add(jPanelRange);

			jLabelNBKExp.setText("NBK-Experiments:");
//			jLabelNBKExp.setText("<HTML><u>NBK-Experiments</u>:</HTML>");
			jLabelNBKExp.setVisible(true);
			jLabelNBKExp.setFont(new java.awt.Font("sansserif",1,11));
			jLabelNBKExp.setForeground(new java.awt.Color(0,0,255));
			jLabelNBKExp.setPreferredSize(new java.awt.Dimension(104,20));
			jLabelNBKExp.setBounds(new java.awt.Rectangle(7,13,104,20));
			jPanelRange.add(jLabelNBKExp);

			jPanelContentsInfo.setLayout(null);
			jPanelContentsInfo.setVisible(true);
			jPanelContentsInfo.setPreferredSize(new java.awt.Dimension(343,33));
			jPanelContentsInfo.setBounds(new java.awt.Rectangle(22,40,343,33));
			jPanelRange.add(jPanelContentsInfo);

			jLabelContentsNB.setText("00000000");
			jLabelContentsNB.setPreferredSize(new java.awt.Dimension(65,18));
			jLabelContentsNB.setBounds(new java.awt.Rectangle(38,7,65,18));
			jPanelContentsInfo.add(jLabelContentsNB);

			jTextFieldContentsStart.setPreferredSize(new java.awt.Dimension(45,20));
			jTextFieldContentsStart.setBounds(new java.awt.Rectangle(105,7,45,20));
			jPanelContentsInfo.add(jTextFieldContentsStart);
			jTextFieldContentsStart.addFocusListener( new FocusAdapter() {
				public void focusGained(FocusEvent evt) {
					jTextFieldContentsStartFocusGained(evt);
				}
			});

			jTextFieldContentsEnd.setPreferredSize(new java.awt.Dimension(45,20));
			jTextFieldContentsEnd.setBounds(new java.awt.Rectangle(173,7,45,20));
			jPanelContentsInfo.add(jTextFieldContentsEnd);
			jTextFieldContentsEnd.addFocusListener( new FocusAdapter() {
				public void focusGained(FocusEvent evt) {
					jTextFieldContentsEndFocusGained(evt);
				}
			});

			jLabelSep.setText("-");
			jLabelSep.setFont(new java.awt.Font("sansserif",1,12));
			jLabelSep.setPreferredSize(new java.awt.Dimension(8,20));
			jLabelSep.setBounds(new java.awt.Rectangle(159,5,8,20));
			jPanelContentsInfo.add(jLabelSep);

			jTextAreaNBKExp.setText("-none-");
			jTextAreaNBKExp.setEditable(false);
			jTextAreaNBKExp.setVisible(true);
			jTextAreaNBKExp.setPreferredSize(new java.awt.Dimension(480,62));
			jTextAreaNBKExp.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
			jTextAreaNBKExp.setBounds(new java.awt.Rectangle(20,38,480,62));
			jTextAreaNBKExp.setFocusable(false);
			jPanelRange.add(jTextAreaNBKExp);

			jPanelContents.setLayout(null);
			jPanelContents.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			jPanelContents.setBounds(7, 133, 511, 168);
			jPanelMainCenter.add(jPanelContents);

			jLabelPrintContInc.setText("Print Contents Include:");
			jLabelPrintContInc.setFont(new java.awt.Font("sansserif",1,11));
			jLabelPrintContInc.setForeground(new java.awt.Color(0,0,255));
			jLabelPrintContInc.setBounds(7, 6, 133, 21);
			jPanelContents.add(jLabelPrintContInc);

			jLabelExpDetails.setText("Experiment Details:");
			jLabelExpDetails.setFont(new java.awt.Font("sansserif",0,11));
			jLabelExpDetails.setForeground(new java.awt.Color(0,0,0));
			jLabelExpDetails.setBounds(11, 35, 133, 14);
			jPanelContents.add(jLabelExpDetails);

			jLabelBatchDataStruc.setText("Batch Data/Structures:");
			jLabelBatchDataStruc.setFont(new java.awt.Font("sansserif",0,11));
			jLabelBatchDataStruc.setForeground(new java.awt.Color(0,0,0));
			jLabelBatchDataStruc.setBounds(11, 54, 133, 14);
			jPanelContents.add(jLabelBatchDataStruc);

			jLabelQCAnalData.setText("Analytical Data:");
			jLabelQCAnalData.setFont(new java.awt.Font("sansserif",0,11));
			jLabelQCAnalData.setForeground(new java.awt.Color(0,0,0));
			jLabelQCAnalData.setBounds(11, 75, 133, 14);
			jPanelContents.add(jLabelQCAnalData);

			jLabelRegSub.setText("Registration/Submission:");
			jLabelRegSub.setFont(new java.awt.Font("sansserif",0,11));
			jLabelRegSub.setForeground(new java.awt.Color(0,0,0));
			jLabelRegSub.setBounds(11, 98, 133, 14);
			jPanelContents.add(jLabelRegSub);

			jLabelAttachments.setText("Attachments:");
			jLabelAttachments.setFont(new java.awt.Font("sansserif",0,11));
			jLabelAttachments.setForeground(new java.awt.Color(0,0,0));
			jLabelAttachments.setBounds(11, 119, 133, 14);
			jPanelContents.add(jLabelAttachments);

			Attachments.setText("-none-");
			Attachments.setBounds(140, 118, 362, 17);
			jPanelContents.add(Attachments);

			RegSub.setText("-none-");
			RegSub.setBounds(140, 98, 364, 14);
			jPanelContents.add(RegSub);

			QCAnalData.setText("-none-");
			QCAnalData.setBounds(140, 75, 362, 17);
			jPanelContents.add(QCAnalData);

			BatchDataStruc.setText("-none-");
			BatchDataStruc.setBounds(140, 53, 362, 17);
			jPanelContents.add(BatchDataStruc);

			ExpDetails.setText("-none-");
			ExpDetails.setBounds(140, 35, 364, 14);
			jPanelContents.add(ExpDetails);

			jButtonSetupPrintCont.setText("Setup Print Contents  ...");
			jButtonSetupPrintCont.setFont(new java.awt.Font("sansserif",1,11));
			jButtonSetupPrintCont.setPreferredSize(new java.awt.Dimension(166,19));
			jButtonSetupPrintCont.setBounds(new java.awt.Rectangle(340,6,166,19));
			jPanelContents.add(jButtonSetupPrintCont);

			jPanelContents.add(jLabelFooter);
			jLabelFooter.setText("Signature/Witness: ");
			jLabelFooter.setBounds(11, 140, 133, 14);

			jPanelContents.add(SignatureFooter);
			SignatureFooter.setText("-none-");
			SignatureFooter.setBounds(140, 140, 350, 14);

			jButtonSetupPrintCont.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonSetupPrintContActionPerformed(evt);
				}
			});

			jPanelMainSouth.setVisible(true);
			jPanelMainSouth.setPreferredSize(new java.awt.Dimension(525,40));
			getContentPane().add(jPanelMainSouth, BorderLayout.SOUTH);

			jButtonPreview.setText("Preview");
			jButtonPreview.setVisible(true);
			jButtonPreview.setFont(new java.awt.Font("sansserif",1,11));
			jButtonPreview.setForeground(new java.awt.Color(0,0,255));
			jPanelMainSouth.add(jButtonPreview);
			jButtonPreview.setSize(71, 23);
			jButtonPreview.setPreferredSize(new java.awt.Dimension(80, 23));

			jPanelFiller3.setVisible(true);
			jPanelFiller3.setPreferredSize(new java.awt.Dimension(16,10));
			jPanelMainSouth.add(jPanelFiller3);

			jPanelMainSouth.add(jButtonExport);
			jButtonExport.setText("Export");
			jButtonExport.setForeground(new java.awt.Color(0,0,255));
			jButtonExport.setPreferredSize(new java.awt.Dimension(79, 23));
			jButtonExport.setFont(new java.awt.Font("Sansserif",1,11));
			jButtonExport.setSize(79, 23);

			jButtonExport.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonExportActionPerformed(evt);
				}
			});

			jButtonPreview.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonPreviewActionPerformed(evt);
				}
			});

			jPanelFiller2.setVisible(true);
			jPanelFiller2.setPreferredSize(new java.awt.Dimension(128, 10));
			jPanelMainSouth.add(jPanelFiller2);

			jButtonOk.setEnabled(true);
			jButtonOk.setText("Print");
			jButtonOk.setFont(new java.awt.Font("sansserif",1,11));
			jButtonOk.setPreferredSize(new java.awt.Dimension(71, 23));
			jPanelMainSouth.add(jButtonOk);
			jButtonOk.setSize(71, 23);
			jButtonOk.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOkActionPerformed(evt);
				}
			});

			jPanelFiller1.setVisible(true);
			jPanelFiller1.setPreferredSize(new java.awt.Dimension(16,10));
			jPanelMainSouth.add(jPanelFiller1);

			jButtonCancel.setEnabled(true);
			jButtonCancel.setText("Cancel");
			jButtonCancel.setFont(new java.awt.Font("sansserif",1,11));
			jPanelMainSouth.add(jButtonCancel);
			jButtonCancel.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed(evt);
				}
			});

			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e){
					PrintExperimentDialog.this.setVisible(false);
					PrintExperimentDialog.this.dispose();
				}
				public void windowClosed(WindowEvent e){
					PrintExperimentDialog.this.removeAll();
					PrintExperimentDialog.this.setVisible(false);
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
	}

	/** Add your post-init code in here 	*/
	public void postInitGUI()
	{
		if (expDialog){
			jButtonPreview.setToolTipText("This will open the experiment only, without Analytical or Attachments in PDF (Acrobat) format");
			jButtonExport.setToolTipText("This will open the experiment only, without Analytical or Attachments in RTF (Word) format");
			jButtonOk.setToolTipText("This will open the experiment with Analytical & Attachments in PDF (Acrobat) format");
		}
		else {
			jButtonPreview.setToolTipText("This will open the table of contents in PDF (Acrobat) format");
			jButtonExport.setToolTipText("This will open the table of contents in RTF (Word) format");
			jButtonOk.setToolTipText("This will open the table of contents in PDF (Acrobat) format");
		}
		getRootPane().setDefaultButton(jButtonOk);
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
			PrintExperimentDialog inst = new PrintExperimentDialog(null);
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonSetupPrintContActionPerformed(ActionEvent evt)
	{
		//show the print setup dialog
		PrintExperimentSetupDialog printSetupDlg = new PrintExperimentSetupDialog(MasterController.getGuiController().getGUIComponent(), printOptions);
		Point loc = MasterController.getGuiController().getGUIComponent().getLocation();
		Dimension dim = MasterController.getGuiController().getGUIComponent().getSize();
		printSetupDlg.setLocation(loc.x + (dim.width - printSetupDlg.getSize().width)/2, loc.y + (dim.height - printSetupDlg.getSize().height)/2);
		printSetupDlg.setVisible(true);

		displaySelectedPrintOptions();
	}

	/** Auto-generated event handler method */
	protected void jButtonPreviewActionPerformed(ActionEvent evt)
	{
		whichButton = 0;
		if (expDialog)
			previewExperiments();
		else
			previewContents();

		setVisible(false);	
//		((Frame)getParent()).toBack();
	}

	/** Auto-generated event handler method */
	protected void jButtonExportActionPerformed(ActionEvent evt)
	{
		whichButton = 1;
		if (expDialog)
			exportExperiments();
		else
			exportContents();

		setVisible(false);	
//		((Frame)getParent()).toBack();
	}

	/** Auto-generated event handler method */
	protected void jButtonOkActionPerformed(ActionEvent evt)
	{
		whichButton = 2;
		if (expDialog)
			printExperiments();
		else
			printContents();

		dispose();	
//		((Frame)getParent()).toBack();
	}

	/** Auto-generated event handler method */
	protected void jButtonCancelActionPerformed(ActionEvent evt)
	{
		dispose();
	}

	// External Access method to preview/print Experiments
	static public void displayPrintExperimentsDialog(Frame frame, ArrayList list) 
	{
		if (instance_ == null) {
			instance_ = new PrintExperimentDialog(frame);
			instance_.displayPrintExperimentsDialog(list);
			instance_ = null;
		}
	}
	private void displayPrintExperimentsDialog(ArrayList list) 
	{
		expDialog = true;
		setTitle("Print Experiment(s)");

		jPanelRange.setBounds(7, 14, 511, 112);
		setSize(new Dimension(530, 398));
		jPanelContents.setVisible(true);
		jPanelContentsInfo.setVisible(false);
		jTextAreaNBKExp.setVisible(true);
		validate();

		expList = new TreeMap();
		for (Iterator i=list.iterator(); i.hasNext(); ) {
			Object[] expParams = (Object[])i.next();
			expList.put((String)expParams[0]+(String)expParams[1]+(String)expParams[2], expParams);
		}

		// Load user's default print options
		try {
			if (printOptions == null) {
				printOptions = new PrintOptions();
				printOptions.loadOptions();
			}

			displayNotebookRange();
			displaySelectedPrintOptions();

			Point loc = getParent().getLocation();
			Dimension dim = getParent().getSize();
			setLocation(loc.x + (dim.width - getSize().width)/2, loc.y + (dim.height - getSize().height)/2);

			SwingWorker worker = new SwingWorker() {
				public Object construct() {
//					this is how you do a progress bar message in CeN --
//					final String progressbarStatus =  "Retrieving Printer Information ...";
//					CeNJobProgressHandler.getInstance().addItem(progressbarStatus);
					return null;
				}

				public void finished() {
					PrintExperimentDialog.super.setVisible(true);
				}
			};
			worker.start();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	// External Access method to preview/print Table of Contents
	static public void displayPrintContentsDialog(Frame frame, String siteCode, String notebook, int start, int end) 
	{
		if (instance_ == null) {
			instance_ = new PrintExperimentDialog(frame);
			instance_.displayPrintContentsDialog(siteCode, notebook, start, end);
			instance_ = null;
		}
	}

	private void displayPrintContentsDialog(String siteCode, String notebook, int start, int end)
	{
		expDialog = false;
		setTitle("Print Table of Contents");

		jPanelContents.setVisible(false);
		setSize(new Dimension(500, 200));
		jTextAreaNBKExp.setVisible(false);
		jPanelContentsInfo.setVisible(true);

//		jPanelRange.setBounds(new java.awt.Rectangle(7,15,379,52));

//		jPanelFiller2.setPreferredSize(new java.awt.Dimension(207,10));
		jPanelFiller2.setPreferredSize(new java.awt.Dimension(60,10));
		jPanelMainCenter.setSize(522, 178);
		jPanelRange.setBounds(7, 14, 480, 112);

		jButtonPreview.setToolTipText("This will open the table of contents in PDF (Acrobat) format");
		jButtonExport.setToolTipText("This will open the table of contents in RTF (Word) format");
		jButtonOk.setToolTipText("This will open the table of contents in PDF (Acrobat) format");

		validate();

		this.siteCode = siteCode;
		jLabelContentsNB.setText(notebook);
		if (start == -1) jTextFieldContentsStart.setText("");
		else  			 jTextFieldContentsStart.setText(""+start);
		if (end == -1)   jTextFieldContentsEnd.setText("");
		else  			 jTextFieldContentsEnd.setText(""+end);

		try {			
			Point loc = getParent().getLocation();
			Dimension dim = getParent().getSize();
			setLocation(loc.x + (dim.width - getSize().width)/2, loc.y + (dim.height - getSize().height)/2);

			SwingWorker worker = new SwingWorker() {
				public Object construct() {
					return null;
				}

				public void finished() {
					PrintExperimentDialog.super.setVisible(true);
				}
			};
			worker.start();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}


	// experiment methods:  --------------------

	private void previewExperiments()
	{
		Iterator i=expList.keySet().iterator();
		if (i.hasNext()) {
			Object[] expParams = (Object[])expList.get((String)i.next());

			NotebookPageLoaderListener listener = new NotebookPageLoaderAction() {
				public void PageLoaded(final NotebookPageLoaderEvent evt) {
					if (evt.getModel() != null) {
						SwingWorker worker = new SwingWorker() {
							public Object construct() {
								System.out.println("Preview Page Loaded: " + evt.getModel().getNotebookRefAsString());
								final String progressStatus = "Previewing experiment " + evt.getNotebookRef().toString() + " ...";
								CeNJobProgressHandler.getInstance().addItem(progressStatus);

								try {
									/////////////////printExperiment(evt.getModel());
								} catch (Exception e) {
									CeNErrorHandler.getInstance().logExceptionMsg(PrintExperimentDialog.this, e);
								}

								CeNJobProgressHandler.getInstance().removeItem(progressStatus);
								return null;
							}

							public void finished() {
							}
						};
						worker.start();
					}
				}
				public void PageLoadFailed(NotebookPageLoaderEvent evt) { 
					System.out.println("Preview Page Load Failed: " + evt.getNotebookRef().toString()); 
				}
			};

//			// vb printtodo
//			PrintController.loadExperiment((String)expParams[0], (String)expParams[1], 
//					(String)expParams[2], ((Integer)expParams[3]).intValue(), listener);
//			MasterController.getGuiController().openExperiment((String)expParams[0], (String)expParams[1], 
//			(String)expParams[2], ((Integer)expParams[3]).intValue());
		}
	}

	private void exportExperiments()
	{
//		Iterator i=expList.keySet().iterator();
//		if (i.hasNext()) {
//			Object[] expParams = (Object[])expList.get((String)i.next());
//
//			NotebookPageLoaderListener obj = new NotebookPageLoaderAction() {
//				public void PageLoaded(final NotebookPageLoaderEvent evt) {
//					if (evt.getModel() != null) {
//						SwingWorker worker = new SwingWorker() {
//							public Object construct() throws Exception {
//								System.out.println("Export Page Loaded: " + evt.getModel().getNotebookRefAsString());
//								final String progressStatus = "Exporting experiment " + evt.getNotebookRef().toString() + " ...";
//								CeNJobProgressHandler.getInstance().addItem(progressStatus);
//
//								try {
//									/////////////////printExperiment(evt.getModel());
//								} catch (Exception e) {
//									CeNErrorHandler.getInstance().logExceptionMsg(PrintExperimentDialog.this, e);
//								}
//
//								CeNJobProgressHandler.getInstance().removeItem(progressStatus);
//								return null;
//							}
//
//							public void finished() {
//							}
//						};
//						worker.start();
//					}
//				}
//				public void PageLoadFailed(NotebookPageLoaderEvent evt) { 
//					System.out.println("Export Experiment Load Failed: " + evt.getNotebookRef().toString()); 
//				}
//			};
////			vb printtodo
////			MasterController.getGuiController().loadExperiment((String)expParams[0], (String)expParams[1], 
////			(String)expParams[2], ((Integer)expParams[3]).intValue(), obj);
//			MasterController.getGuiController().openExperiment((String)expParams[0], (String)expParams[1], 
//					(String)expParams[2], ((Integer)expParams[3]).intValue());
//		}
	}

	private void printExperiments()
	{
//		int j=0;
//		for (Iterator i=expList.keySet().iterator(); i.hasNext(); j++) {
//			Object[] expParams = (Object[])expList.get((String)i.next()); 
//
//			final NotebookPageLoaderListener obj = new NotebookPageLoaderAction() {
//				public void PageLoaded(final NotebookPageLoaderEvent evt) {
//					if (evt.getModel() != null) {
//						SwingWorker worker = new SwingWorker() {
//							public Object construct() throws Exception {
//								System.out.println("Page Loaded: " + evt.getModel().getNotebookRefAsString());
//								final String progressStatus = "Printing experiment " + evt.getNotebookRef().toString() + " ...";
//								CeNJobProgressHandler.getInstance().addItem(progressStatus);
//
//								try {
//									/////////////////printExperiment(evt.getModel());
//								} catch (Exception e) {
//									CeNErrorHandler.getInstance().logExceptionMsg(PrintExperimentDialog.this, e);
//								}
//
//								CeNJobProgressHandler.getInstance().removeItem(progressStatus);
//								return null;
//							}
//
//							public void finished() {
//							}
//						};
//						worker.start();
//					}
//				}
//				public void PageLoadFailed(NotebookPageLoaderEvent evt) { 
//					System.out.println("Page Load Failed: " + evt.getNotebookRef().toString()); 
//				}
//			};
//
////			MasterController.getGuiController().loadExperiment((String)expParams[0], (String)expParams[1], 
////			(String)expParams[2], ((Integer)expParams[3]).intValue(), obj);
//			MasterController.getGuiController().openExperiment((String)expParams[0], (String)expParams[1], 
//					(String)expParams[2], ((Integer)expParams[3]).intValue());
//		}
	}

	private void printExperiment(NotebookPageModel experiment)
	throws Exception
	{
		//send those to print_experiment
		//run a preview of the experiment
		if (experiment != null) {
//			Print_Experiment prnExp = new Print_Experiment(experiment, printOptions);
//			if (!MasterController.getGuiController().isPageGuiLoaded(experiment.getSiteCode(), experiment.getNotebookRef(), experiment.getVersion()))
//				MasterController.getGuiController().getPageCache().removeNotebookPage(experiment.getSiteCode(), experiment.getNotebookRef(), experiment.getVersion());

//			prnExp.run(whichButton);
		}
	}


	// table of contents methods:  --------------------

	private void loadContents(final int whichButton) 
	{
		String notebook = jLabelContentsNB.getText();
		int start = -1;
		int end = -1;

		try {
			String val = jTextFieldContentsStart.getText();
			if (val.trim().length() == 0) start = -1; else { start = new Integer(val).intValue();  if (start == 0) start = -1; }
			val = jTextFieldContentsEnd.getText();
			if (val.trim().length() == 0) end = -1; else { end = new Integer(val).intValue();  if (end == 0) end = -1; }
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
			return;
		}
		
		loadContentsByCeNReportService(siteCode, notebook, start, end, whichButton);
	}

	private void previewContents() { loadContents(0); }
	private void exportContents() { loadContents(1); }
	private void printContents()   { loadContents(2); }

	private void printContents(NotebookContentsTableModel model, int whichButton)
	{
		//send those to print_contents
		//run a preview of the experiment
//		if (model != null) {
//			Print_Contents prnCont = new Print_Contents(model);
//			try {
//				prnCont.run(whichButton);
//			} catch (Exception e) {
//				CeNErrorHandler.getInstance().logExceptionMsg(this, e);
//			}
//		}
	}


	private void displayNotebookRange()
	{
		String lastNb = "";
		int rangeStartPage = 0, currentPage = 0, lastPage = 0;
		StringBuffer nbRangeMsg = new StringBuffer();
		for (Iterator i=expList.keySet().iterator(); i.hasNext(); ) {
			Object[] expParams = (Object[])expList.get((String)i.next()); 
			currentPage = (new Integer((String)expParams[2])).intValue();

			if (lastNb.equals(expParams[1])) {
				if (currentPage != lastPage + 1) {
					if (lastPage == rangeStartPage + 1)
						nbRangeMsg.append(", " + lastPage);
					else if (lastPage != rangeStartPage)
						nbRangeMsg.append("-" + lastPage);

					rangeStartPage = currentPage;
					nbRangeMsg.append(", " + rangeStartPage);
				}
			} else {
				if (!lastNb.equals("")) {
					if (lastPage == rangeStartPage + 1)
						nbRangeMsg.append(", " + lastPage);
					else if (lastPage != rangeStartPage)
						nbRangeMsg.append("-" + lastPage);

					nbRangeMsg.append(", ");
				}

				lastNb = (String)expParams[1];
				rangeStartPage = currentPage;

				//int lastNbVal = (new Integer(lastNb)).intValue();
				nbRangeMsg.append("<" + lastNb + "> " + rangeStartPage);
			}

			lastPage = currentPage;
		}
		if (expList.size() > 0) {
			if (lastPage == rangeStartPage + 1)
				nbRangeMsg.append(", " + lastPage);
			else if (lastPage != rangeStartPage)
				nbRangeMsg.append("-" + lastPage);
		}

		jTextAreaNBKExp.setText(nbRangeMsg.toString());
		jTextAreaNBKExp.setCaretPosition(0);
	}

	private void displaySelectedPrintOptions() 
	{
		StringBuffer strBuff;

		strBuff = new StringBuffer();
		if (printOptions.isSubject()) appendOption(strBuff, "Experiment Subject/Title");
		if (printOptions.isRxnSchema()) appendOption(strBuff, "Reaction Scheme");
		if (printOptions.isStoichTable()) {
			appendOption(strBuff, "Stoichiometry Table");
			if (printOptions.isStoicWStructures()) appendOption(strBuff, "with Structures");			
			else appendOption(strBuff, "without Structures");			
		}
		if (printOptions.isRxnProcedure()) appendOption(strBuff, "Reaction & Workup Procedure");
		if (strBuff.length() == 0) strBuff.append("-none-");
		ExpDetails.setText(strBuff.toString());
		ExpDetails.setToolTipText(strBuff.toString());

		strBuff = new StringBuffer();
		if (printOptions.isBatchStructData()) appendOption(strBuff, "Batch Data/Structure Details");
		if (strBuff.length() == 0) strBuff.append("-none-");
		BatchDataStruc.setText(strBuff.toString());
		BatchDataStruc.setToolTipText(strBuff.toString());

		strBuff = new StringBuffer();
		if (printOptions.isQcSummary()) appendOption(strBuff, "Analytical Summary");
		if (printOptions.isQcFiles()) {
			appendOption(strBuff, "AnalyticalService Data Files");
			if (printOptions.isQcAllInstruments()) appendOption(strBuff, "All Instrument Types");
			else appendOption(strBuff, printOptions.getQcInstrumentsDesc());
		}
		if (strBuff.length() == 0) strBuff.append("-none-");
		QCAnalData.setText(strBuff.toString());
		QCAnalData.setToolTipText(strBuff.toString());

		strBuff = new StringBuffer();
		if (printOptions.isRegSummary()) appendOption(strBuff, "Registration/Submission Summary");
//		if (printOptions.isRegDetails()) appendOption(strBuff, "Registration Details");
//		if (printOptions.isRegSubmissionDetails()) appendOption(strBuff, "Submission Details");
//		if (printOptions.isRegHazards()) appendOption(strBuff, "Hazards/Handling/Storage Details");
		if (strBuff.length() == 0) strBuff.append("-none-");
		RegSub.setText(strBuff.toString());
		RegSub.setToolTipText(strBuff.toString());

		strBuff = new StringBuffer();
		if (printOptions.isAttachAll()) appendOption(strBuff, "All IP Attachments");
		if (printOptions.isAttachNone()) appendOption(strBuff, "None");
		if (strBuff.length() == 0) strBuff.append("-none-");
		Attachments.setText(strBuff.toString());
		Attachments.setToolTipText(strBuff.toString());

		strBuff = new StringBuffer();
		if (printOptions.isSignatureFooter()) appendOption(strBuff, "Signature & Witness Footer");
		if (strBuff.length() == 0) strBuff.append("-none-");
		SignatureFooter.setText(strBuff.toString());
		SignatureFooter.setToolTipText(strBuff.toString());

	}

	private void appendOption(StringBuffer buff, String str) {
		if (buff.length() > 0) buff.append(", ");
		buff.append(str);
	}

	/** Auto-generated event handler method */
	protected void jTextFieldContentsStartFocusGained(FocusEvent evt){
		jTextFieldContentsStart.setSelectionStart(jTextFieldContentsStart.getText().length());
		jTextFieldContentsStart.setSelectionStart(0);
	}

	/** Auto-generated event handler method */
	protected void jTextFieldContentsEndFocusGained(FocusEvent evt){
		jTextFieldContentsEnd.setSelectionStart(jTextFieldContentsEnd.getText().length());
		jTextFieldContentsEnd.setSelectionStart(0);
	}
	
	private void loadContentsByCeNReportService(String siteCode, String notebook, int start, int end, int whichButton) {
		String fileName = notebook + "-(" + start + "-" + end + ")";
		ReportURLGenerator urlgen = new ReportURLGenerator();
		urlgen.addParameter(PrintSetupConstants.PRINT_TABLE_OF_CONTENTS, "true");
		if(CommonUtils.isNotNull(siteCode)) {
			urlgen.addParameter(PrintSetupConstants.SITE_CODE, siteCode);
		}
		urlgen.addParameter(PrintSetupConstants.NOTEBOOK_NUMBER, notebook);
		urlgen.addParameter(PrintSetupConstants.START_PAGE_NUMBER, start + "");
		urlgen.addParameter(PrintSetupConstants.END_PAGE_NUMBER, end  + "");
		urlgen.addParameter(PrintSetupConstants.FILE_NAME, fileName);
		urlgen.addParameter(PrintSetupConstants.STOP_AFTER_IMAGE_LOAD_ERROR, "false");
		urlgen.addParameter(PrintSetupConstants.REPORT_NAME, "tableOfContents.rptdesign");
		
		if(whichButton == 1) {
			urlgen.addParameter(PrintSetupConstants.OUTPUT_FORMAT, ReportURLGenerator.RTF);
		} else {
			urlgen.addParameter(PrintSetupConstants.OUTPUT_FORMAT, ReportURLGenerator.PDF);
		}
		
		PrintExperiment printexp = new PrintExperiment(fileName, urlgen, whichButtonEnum(whichButton));
		printexp.print();		
	}
	
	private PrintSetupConstants.buttonChoices whichButtonEnum(int whichButton) {
		switch (whichButton) {
		case 2:return PrintSetupConstants.buttonChoices.PRINT;
		case 1:return PrintSetupConstants.buttonChoices.EXPORT;
		case 0:return PrintSetupConstants.buttonChoices.PREVIEW;
		default:return PrintSetupConstants.buttonChoices.PREVIEW;
		}		
	}
}
