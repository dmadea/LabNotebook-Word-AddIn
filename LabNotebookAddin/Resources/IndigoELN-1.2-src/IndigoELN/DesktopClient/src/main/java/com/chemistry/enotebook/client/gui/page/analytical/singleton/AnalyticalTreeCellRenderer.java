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
package com.chemistry.enotebook.client.gui.page.analytical.singleton;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.experiment.datamodel.analytical.Analysis;
import info.clearthought.layout.TableLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.io.File;

/**
 * This code was generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a
 * for-profit company or business) then you should purchase
 * a license - please visit www.cloudgarden.com for details.
 */
public class AnalyticalTreeCellRenderer 
	extends javax.swing.JPanel
	implements TreeCellRenderer 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1371425268891892742L;
	private JLabel jLabelFileType;
	private JLabel jLabelFileSize;
	private JLabel jLabelFileName;
	private JLabel jLabelExperiment;
	private JLabel jLabelDate;
	private JLabel jLabelInstrument;
	private JLabel jLabelGroupId;
	private JLabel jLabelUserId;
	private JLabel jLabelInsType;
	private JLabel jLabelSampleRef;
	private JLabel jLabelAnnotation;
	private JTextField jTextFieldAnnotation;
	
	public JButton optionsButton = null;
	public String analyticalServiceSampleReference = null;
	
	boolean canUnlink, purificationServiceFlag;
	
	
	public AnalyticalTreeCellRenderer(boolean canUnlink) {
		super();
		this.canUnlink = canUnlink;
		this.purificationServiceFlag = false;
	}
	public AnalyticalTreeCellRenderer(boolean canUnlink, boolean purificationServiceFlag) {
		super();
		this.canUnlink = canUnlink;
		this.purificationServiceFlag = purificationServiceFlag;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.CeNTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object obj, 
							boolean selected, boolean expanded, boolean leaf, 	int row, boolean hasFocus) 
	{
		//start with a fresh panel
		removeAll();
		setBorder(BorderFactory.createEmptyBorder());	
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)obj;
		Object nodeUserObject = node.getUserObject();

		if (selected == true && nodeUserObject instanceof Analysis) {
			setOpaque(true);
			setForeground(Color.white);
			setBackground(Color.yellow);
		} else {
			setOpaque(false);
			setForeground(Color.black);
			setBackground(Color.white);
		}
		
		if (nodeUserObject instanceof Analysis) {
			Analysis a = (Analysis)nodeUserObject;
			jLabelSampleRef = new JLabel();
			jLabelInsType = new JLabel();
			jLabelUserId = new JLabel();
			jLabelGroupId = new JLabel();
			jLabelInstrument = new JLabel();
			jLabelDate = new JLabel();
			jLabelExperiment = new JLabel();
			jLabelFileName = new JLabel();
			jLabelFileSize = new JLabel();
			jLabelFileType = new JLabel();
			jLabelAnnotation = new JLabel();
			jTextFieldAnnotation = new JTextField();
			setLayout(null);
			setPreferredSize(new java.awt.Dimension(640, 120));
			
			analyticalServiceSampleReference = a.getAnalyticalServiceSampleRef();
			if (analyticalServiceSampleReference.length() > 18) {
				String displaySampleRef = analyticalServiceSampleReference.substring(0, 15);
				jLabelSampleRef.setText("<html><font size=2><b>Sample Ref</b>:</font> <font size=3>" + displaySampleRef + "..." + "</font></html>");
			} else {
				jLabelSampleRef.setText("<html><font size=2><b>Sample Ref</b>:</font> <font size=3>" + analyticalServiceSampleReference + "</font></html>");
			}
			jLabelSampleRef.setPreferredSize(new java.awt.Dimension(200, 20));
			jLabelSampleRef.setBounds(new java.awt.Rectangle(10, 10, 200, 20));
			add(jLabelSampleRef);
			
			String txtVal = a.getInstrumentType();  if (txtVal == null) txtVal = "";
			jLabelInsType.setText("<html><font size=2><b>Instrument Type</b>:</font> <font size=3>" + txtVal + "</font></html>");
			jLabelInsType.setPreferredSize(new java.awt.Dimension(200, 20));
			jLabelInsType.setBounds(new java.awt.Rectangle(10, 35, 200, 20));
			add(jLabelInsType);
			
			txtVal = a.getUserId();  if (txtVal == null) txtVal = "";
			jLabelUserId.setText("<html><font size=2><b>User ID</b>:</font> <font size=3>" + txtVal + "</font></html>");
			jLabelUserId.setPreferredSize(new java.awt.Dimension(200, 20));
			jLabelUserId.setBounds(new java.awt.Rectangle(10, 60, 200, 20));
			add(jLabelUserId);
			
			txtVal = a.getGroupId();  if (txtVal == null) txtVal = "";
			jLabelGroupId.setText("<html><font size=2><b>Group ID</b>:</font> <font size=3>" + txtVal + "</font></html>");
			jLabelGroupId.setPreferredSize(new java.awt.Dimension(200, 20));
			jLabelGroupId.setBounds(new java.awt.Rectangle(10, 85, 200, 20));
			add(jLabelGroupId);
			
			txtVal = a.getInstrument();  if (txtVal == null) txtVal = "";
			jLabelInstrument.setText("<html><font size=2><b>Instrument</b>:</font> <font size=3>" + txtVal + "</font></html>");
			jLabelInstrument.setPreferredSize(new java.awt.Dimension(200, 20));
			jLabelInstrument.setBounds(new java.awt.Rectangle(210, 10, 200, 20));
			add(jLabelInstrument);
			
			txtVal = a.getExperimentTime();  if (txtVal == null) txtVal = "";
			jLabelDate.setText("<html><font size=2><b>Date</b>:</font> <font size=3>" + txtVal + "</font></html>");
			jLabelDate.setPreferredSize(new java.awt.Dimension(200, 20));
			jLabelDate.setBounds(new java.awt.Rectangle(210, 35, 200, 20));
			add(jLabelDate);
			
			txtVal = a.getExperiment();  if (txtVal == null) txtVal = "";
			int pos = txtVal.lastIndexOf(File.separator);
			if (pos >= 0) txtVal = txtVal.substring(pos + 1, txtVal.length());
			if (txtVal.length() > 19) txtVal = txtVal.substring(0, Math.min(a.getExperiment().length(), 19));
			jLabelExperiment.setText("<html><font size=2><b>Experiment</b>:</font> <font size=3>" + txtVal + "</font></html>");
			jLabelExperiment.setPreferredSize(new java.awt.Dimension(200, 20));
			jLabelExperiment.setBounds(new java.awt.Rectangle(210, 60, 200, 20));
			add(jLabelExperiment);
			
			txtVal = a.getFileName();  if (txtVal == null) txtVal = "";
//			String toolTip = txtVal;
			if (txtVal.length() > 28) {
				txtVal = txtVal.substring(0, 25);
				jLabelFileName.setText("<html><font size=2><b>File Name</b>:</font> <font size=3>" + txtVal + "...</font></html>");
			} else
				jLabelFileName.setText("<html><font size=2><b>File Name</b>:</font> <font size=3>" + txtVal + "</font></html>");
//			jLabelFileName.setToolTipText(toolTip);
			jLabelFileName.setPreferredSize(new java.awt.Dimension(270, 20));
			jLabelFileName.setBounds(new java.awt.Rectangle(400, 10, 270, 20));
			add(jLabelFileName);
			
			txtVal = ""+a.getFileSize();  if (txtVal == null) txtVal = "";
			jLabelFileSize.setText("<html><font size=2><b>File Size</b>:</font> <font size=3>" + txtVal + "</font></html>");
			jLabelFileSize.setPreferredSize(new java.awt.Dimension(200, 20));
			jLabelFileSize.setBounds(new java.awt.Rectangle(400, 35, 200, 20));
			add(jLabelFileSize);
			
			txtVal = a.getFileType();  if (txtVal == null) txtVal = "";
			jLabelFileType.setText("<html><font size=2><b>File Type</b>:</font> <font size=3>" + txtVal + "</font></html>");
			jLabelFileType.setPreferredSize(new java.awt.Dimension(200, 20));
			jLabelFileType.setBounds(new java.awt.Rectangle(400, 60, 200, 20));
			add(jLabelFileType);
			
			if (!purificationServiceFlag) {
				jLabelAnnotation.setText("<html><font size=2><b>Annotation</b>:</font> </html>");
				jLabelAnnotation.setPreferredSize(new java.awt.Dimension(200, 20));
				jLabelAnnotation.setBounds(new java.awt.Rectangle(210, 85, 200, 20));
				add(jLabelAnnotation);
				jTextFieldAnnotation.setText(a.getAnnotation());
				jTextFieldAnnotation.setPreferredSize(new java.awt.Dimension(210, 20));
				jTextFieldAnnotation.setBounds(new java.awt.Rectangle(275, 85, 210, 20));
				add(jTextFieldAnnotation);
			}
			
			optionsButton = new JButton("Options");
			optionsButton.setPreferredSize(new Dimension(90, 24));
			optionsButton.setMinimumSize(new Dimension(90, 24));
			optionsButton.setBorder(new TitledBorder(null, "",  TitledBorder.LEADING, TitledBorder.TOP, 
					new java.awt.Font("MS Sans Serif", 0, 11),  new java.awt.Color(0, 0, 0)));
			optionsButton.setIcon(CenIconFactory.getImageIcon(CenIconFactory.General.DROP_DOWN));
			optionsButton.setHorizontalAlignment(SwingConstants.LEFT);
			optionsButton.setFocusable(false);
			optionsButton.setBounds(new java.awt.Rectangle(500, 85, 80, 20));
			add(optionsButton);
//			optionsButton.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent evt) {
//					System.out.println("Option clicked");
//				}
//			});
			
			txtVal = a.getFileType();  if (txtVal == null) txtVal = "";
			Border border = BorderFactory.createLineBorder(Color.black);
			setBorder(border);
			setToolTipText("<html>" + a.getAnalyticalServiceSampleRef() + "<br>" + 
					((a.getFileName() == null) ? "" : a.getFileName() + "<br>") + 
					((a.getExperiment() == null) ? "" : a.getExperiment() + "<br>") + 
					"Version: " + a.getVersion() + ", \t\t Site: " + a.getSite() + "<br>" +
					((MasterController.getUser().isSuperUser()) ? "<br>CyberLab Id: " + a.getCyberLabFileId() : "") +
					"</html>");
		} else {
			removeAll();
			setPreferredSize(new Dimension(200, 10));
			double colSize[] = {200}; 
			double rowSize[] = {10}; 
			TableLayout layout = new TableLayout();
			layout.setColumn(colSize);
			layout.setRow(rowSize);
			setLayout(layout);				
			
			// just display text label
			String strText = obj.toString();
			JLabel lbl = new JLabel(strText);
			add(lbl, "0, 0");
		}
		return this;
	}
}
