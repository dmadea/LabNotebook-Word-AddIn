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

import com.chemistry.enotebook.client.gui.CeNGUIConstants;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WellColorLegendDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1843731782017705980L;
	// content
	private JPanel contentPane;
	// control panel for buttons
	private JPanel controllPanel = new JPanel();

	private JButton closeButton = new JButton("Close");

	private FormLayout formLayout = new FormLayout("15dlu, 5dlu,pref, 35dlu, 15dlu, 5dlu,380", "");//dynamiclly grow rows
	private CellConstraints mCellConstraints = new CellConstraints();
	
//	private Color C1 = new Color(84, 139, 93);//dark green -"Soluble - Continue" or "Passed - Continue"
//	private Color C2 = Color.YELLOW;// yellow -"Unavailable - Discontinue" or "Failed - Discontinue"
//	private Color C3 = new Color(93, 84, 139); //new Color(255, 55, 255);//pink-"Passed and Registered - Continue"
//	
//	private Color C4 = new Color(255, 0, 0);//red - "Insoluble - Continue" or "Failed - Continue"
//	private Color C5 = new Color(155, 0, 0);//dark red - "Insoluble - Discontinue"
//	private Color C6 = new Color(0, 171, 255);//light blue - "Insoluble - Continue" or "Suspect - Continue"
//	
//	private Color C7 = Color.LIGHT_GRAY;//grey -"Empty Well"
//	private Color C8 = new Color(0, 0, 255);//blue -"Suspect - Discontinue"
//	private Color C9 = Color.WHITE;//white -"Not Made - Discontinue"
	
	private RoundColorIcon greenRoundColorIcon = new RoundColorIcon(CeNGUIConstants.LIGHT_GREEN, 6);//dark green
	//private RoundColorIcon green2RoundColorIcon = new RoundColorIcon(new Color(84,139,93),6);	
	private RoundColorIcon yellowRoundColorIcon = new RoundColorIcon(CeNGUIConstants.YELLOW, 6);//yellow-"Unavailable - Discontinue"
	private RoundColorIcon pinkRoundColorIcon = new RoundColorIcon(CeNGUIConstants.DARK_BLUE, 6);//pink
	private RoundColorIcon redRoundColorIcon = new RoundColorIcon(CeNGUIConstants.LIGHT_RED, 6);//red -Insoluble - Continue"
	private RoundColorIcon darkredRoundColorIcon = new RoundColorIcon(CeNGUIConstants.ORANGE, 6);//dark red
	private RoundColorIcon lightBlueRoundColorIcon = new RoundColorIcon(CeNGUIConstants.LIGHT_BLUE, 6);//light blue
	private RoundColorIcon greyRoundColorIcon = new RoundColorIcon(CeNGUIConstants.LIGHT_GRAY, 6);//grey
	private RoundColorIcon blueRoundColorIcon = new RoundColorIcon(CeNGUIConstants.BLUE, 6);//blue
	private RoundColorIcon whiteRoundColorIcon = new RoundColorIcon(CeNGUIConstants.WHITE, 6);//

	private BorderLayout borderLayout = new BorderLayout();
	private FlowLayout flowLayout = new FlowLayout();

	private Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	public WellColorLegendDialog(JFrame parent) {
		super(parent, "Well Color Legend", true);
		try {
			jbInit();
			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JComponent getIconComponent(Icon icon) {
		JLabel label = new JLabel();

		label.setOpaque(true);

		label.setBorder(noFocusBorder);
		label.setIcon(icon);
		return label;
	}

	private void jbInit() throws Exception {
		this.setResizable(false);
		// center
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeButton_actionPerformed();
			}
		});
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(borderLayout);
		DefaultFormBuilder builder = new DefaultFormBuilder(formLayout);
		builder.setDefaultDialogBorder();
		//builder.appendSeparator("Segment");
		builder.nextColumn(2);
		builder.append("Monomers");
		builder.nextColumn(2);
		builder.append("Products");
		builder.appendSeparator();
		builder.nextLine();
		//
		builder.nextColumn(1);
		builder.append(getIconComponent(greenRoundColorIcon));
		//builder.nextColumn();
		builder.append("Soluble - Continue");
		//builder.nextColumn();
		builder.append(getIconComponent(greenRoundColorIcon));
		//builder.nextColumn();
		builder.append("Passed - Continue");

		builder.nextLine();
		//builder.addSeparator("");
		builder.nextColumn(1);
		builder.append(getIconComponent(redRoundColorIcon));
		//builder.nextColumn();
		builder.append("Unavailable - Discontinue");
		//builder.nextColumn();
		builder.append(getIconComponent(pinkRoundColorIcon));
		//builder.nextColumn();
		builder.append("Passed and Registered - Continue");

		builder.nextLine();
		//builder.addSeparator("");
		builder.nextColumn(1);
		builder.append(getIconComponent(lightBlueRoundColorIcon));
		//builder.nextColumn();
		builder.append("Insoluble - Continue");
		//builder.nextColumn();
		builder.append(getIconComponent(yellowRoundColorIcon));
		//builder.nextColumn();
		builder.append("Failed - Continue");

		builder.nextLine();
		//builder.addSeparator("");
		builder.nextColumn(1);
		builder.append(getIconComponent(darkredRoundColorIcon));
		//builder.nextColumn();
		builder.append("Insoluble - Discontinue");
		//builder.nextColumn();
		builder.append(getIconComponent(redRoundColorIcon));
		//builder.nextColumn();
		builder.append("Failed - Discontinue");

		builder.nextLine();
		//builder.addSeparator("");
		builder.nextColumn(1);
		builder.append(getIconComponent(greyRoundColorIcon));
		//builder.nextColumn();
		builder.append("Empty Well");
		//builder.nextColumn();
		builder.append(getIconComponent(lightBlueRoundColorIcon));
		//builder.nextColumn();
		builder.append("Suspect - Continue");

		builder.nextLine();
		//builder.addSeparator("");
		builder.append("");
		builder.append("");
		builder.append(getIconComponent(blueRoundColorIcon));
		//builder.nextColumn();
		builder.append("Suspect - Discontinue");

		builder.nextLine();
		builder.append("");
		builder.append("");
		builder.append(getIconComponent(whiteRoundColorIcon));
		builder.append("Not Made - Discontinue");

		builder.nextLine();
		builder.append("");
		builder.append("");
		builder.append(getIconComponent(greyRoundColorIcon));
		//builder.nextColumn();
		builder.append("Empty Well");

		flowLayout.setAlignment(FlowLayout.RIGHT);
		controllPanel.setLayout(flowLayout);
		controllPanel.add(this.closeButton);

		contentPane.add(builder.getPanel(), BorderLayout.CENTER);
		contentPane.add(this.controllPanel, BorderLayout.SOUTH);

		;
	}
	private void closeButton_actionPerformed(){
		this.dispose();
	}
	public static void main(String[] args) {
		/*
		 * JFrame frame = new JFrame("CompoundManagementMonomerContainer"); // ContainerTypeExPTree containerTypeTree = new
		 * ContainerTypeExPTree(new // ContainerTypeTreeModel()); CompoundManagementMonomerContainer compoundManagementMonomercontainer = new CompoundManagementMonomerContainer(
		 * ServiceController.getStorageService().createParallelExperiment( "spid_id", "USER1"),1);
		 * frame.getContentPane().add(compoundManagementMonomercontainer); frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		 * frame.pack(); frame.setVisible(true);
		 */
		JFrame frame = new JFrame();
		WellColorLegendDialog mWellColorLegendDialog = new WellColorLegendDialog(frame);
		Dimension frmSize = mWellColorLegendDialog.getSize();
		Dimension dlgSize = new Dimension(550, 300);
		mWellColorLegendDialog.setSize(dlgSize);
		mWellColorLegendDialog.setLocation(200, 200);
		mWellColorLegendDialog.setVisible(true);
	}

}
