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
/**
 * 
 */
package com.chemistry.enotebook.client.gui.page.experiment;

import com.chemistry.enotebook.client.gui.common.utils.CeNLabel;
import com.chemistry.enotebook.domain.DesignSynthesisPlan;
import com.chemistry.enotebook.utils.CodeTableUtils;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;

/**
 * 
 * 
 */
public class ReactionDetails {
	public static final Color BACKGROUND_COLOR = new Color(189, 236, 214);
	public static final Font LABEL_FONT = new java.awt.Font("sansserif", 1, 11);
	public static final Font COMBOBOX_FONT = new java.awt.Font("Tahoma", 0, 12);
	// Design Service info
	private DesignSynthesisPlan dSPlan;
	// form fields
	private JTextField subjectTitle;
	private JComboBox therapeuticArea;
	private JComboBox projectCodeName;
	private JTextArea literatureRef;
	private JTextField creationDate;
	private JTextField contFromReact;
	private JTextField contToReact;
	private JTextField projectAliasName;
	private JTextField spid;
	private JTextField batchOwner;
	private JTextField protocolId;
	private JTextField site;
	private JTextField seriesId;
	private JTextField description;
	private JTextField vrxnId;
	private JTextField summaryPlanId;
	private JLabel subjectTitleLabel;
	private JLabel therapeuticAreaLabel;
	private JLabel projectCodeNameLabel;
	private JLabel literatureRefLabel;
	private JLabel creationDateLabel;
	private JLabel contFromReactLabel;
	private JLabel contToReactLabel;
	private JLabel projectAliasNameLabel;
	private JLabel spidLbl;
	private JLabel batchOwnerLbl;
	private JLabel protocolIdLbl;
	private JLabel siteLbl;
	private JLabel seriesIdLbl;
	private JLabel descriptionLbl;
	private JLabel vrxnIdLbl;
	private JLabel summaryPlanIdLbl;
	private JLabel commentsLbl;

	/**
	 * Creates a new ReactionDetails with the specified DesignSynthesisPlan
	 * 
	 * @param dSPlanj
	 *            Designed Synthesis Plan headers with all
	 * @param layout
	 */
	public ReactionDetails(DesignSynthesisPlan dSPlan, LayoutManager layout) {
		this.dSPlan = dSPlan;
		initializeFormFields();
	}

	public ReactionDetails(DesignSynthesisPlan dSPlan) {
		this.dSPlan = dSPlan;
		initializeFormFields();
	}

	/**
	 * @return Returns the dSPlan.
	 */
	public DesignSynthesisPlan getDSPlan() {
		return dSPlan;
	}

	/**
	 * @param plan
	 *            The dSPlan to set.
	 */
	public void setDSPlan(DesignSynthesisPlan plan) {
		dSPlan = plan;
	}

	/**
	 * initializises all form instance members
	 * 
	 */
	private void initializeFormFields() {
		subjectTitle = new JTextField();
		therapeuticArea = new JComboBox();
		contFromReact = new JTextField();
		contToReact = new JTextField();
		projectCodeName = new JComboBox();
		projectAliasName = new JTextField();
		spidLbl = new CeNLabel("SPID");
		batchOwnerLbl = new CeNLabel("Batch Owner");
		protocolIdLbl = new CeNLabel("Protocol Id");
		seriesIdLbl = new CeNLabel("Series Id");
		siteLbl = new CeNLabel("Site");
		descriptionLbl = new CeNLabel("Description");
		vrxnIdLbl = new CeNLabel("VRXN ID");
		summaryPlanIdLbl = new CeNLabel("Summary Plan ID");
		commentsLbl = new CeNLabel("Comments");
		therapeuticAreaLabel = new CeNLabel("Therapeutic Area");
		subjectTitleLabel = new CeNLabel("Experiment Subject / Title");
		projectCodeNameLabel = new CeNLabel("Project Code & Name");
		literatureRefLabel = new CeNLabel("Literature Ref.");
		creationDateLabel = new CeNLabel("Creation Date");
		contFromReactLabel = new CeNLabel("Cont. FROM Rxn");
		contToReactLabel = new CeNLabel("Cont. TO Rxn");
		projectAliasNameLabel = new CeNLabel("Project Alias Name");
		creationDate = new JTextField();
		spid = new JTextField(dSPlan.getSpid());
		batchOwner = new JTextField(dSPlan.getBatchOwner());
		protocolId = new JTextField(dSPlan.getProtocolId());
		seriesId = new JTextField(dSPlan.getSeriesId());
		site = new JTextField(dSPlan.getSite());
		description = new JTextField(dSPlan.getDescription());
		vrxnId = new JTextField(dSPlan.getVrxnId());
		summaryPlanId = new JTextField(dSPlan.getSummaryPlanId());
		literatureRef = new JTextArea(dSPlan.getComments(), 6, 10);
		// set fonts and size
		setLabelFonts(spidLbl);
		setLabelFonts(siteLbl);
		setLabelFonts(therapeuticAreaLabel);
		setLabelFonts(subjectTitleLabel);
		setLabelFonts(projectCodeNameLabel);
		setLabelFonts(literatureRefLabel);
		setLabelFonts(creationDateLabel);
		setLabelFonts(contFromReactLabel);
		setLabelFonts(contToReactLabel);
		setLabelFonts(projectAliasNameLabel);
		setLabelFonts(projectCodeName);
		setLabelFonts(therapeuticArea);
		// Initially entire project list is displayed since no TA has been
		// selected
		CodeTableUtils.fillComboBoxWithProjects(projectCodeName, null);
		CodeTableUtils.fillComboBoxWithTAs(therapeuticArea);
	}

	public JComponent getJPanelReactionDetails() {
		JPanel result = new JPanel();
		result.setBackground(BACKGROUND_COLOR);
		// result.setFont(new java.awt.Font("sansserif",Font.BOLD,11));
		FormLayout layout = new FormLayout(
				"left:max(80dlu;pref), 1dlu, 120dlu, 20dlu, left:max(40dlu;pref),3dlu, 80dlu ",// columns
				"pref, 1dlu, pref, 1dlu, pref, 1dlu, pref, 1dlu, pref, 1dlu, pref, 1dlu, pref, 1dlu, pref, 1dlu, pref, 1dlu, pref, 1dlu, pref, 1dlu, pref,20dlu,pref"); // rows
		// 1-15
		result.setLayout(layout);
		CellConstraints cc = new CellConstraints();
		int row = 3;
		result.add(subjectTitleLabel, cc.xywh(1, row, 4, 1));
		result.add(creationDateLabel, cc.xy(5, row));
		result.add(creationDate, cc.xy(7, row));
		row += 2;
		result.add(subjectTitle, cc.xywh(1, row, 3, 1));
		result.add(contFromReactLabel, cc.xy(5, row));
		result.add(contFromReact, cc.xy(7, row));
		row += 2;
		result.add(therapeuticAreaLabel, cc.xy(1, row));
		result.add(therapeuticArea, cc.xy(3, row));
		result.add(contToReactLabel, cc.xy(5, row));
		result.add(contToReact, cc.xy(7, row));
		row += 2;
		result.add(projectCodeNameLabel, cc.xy(1, row));
		result.add(projectCodeName, cc.xy(3, row));
		result.add(projectAliasNameLabel, cc.xy(5, row));
		result.add(projectAliasName, cc.xy(7, row));
		row += 2;
		result.add(siteLbl, cc.xy(1, row));
		result.add(site, cc.xy(3, row));
		result.add(spidLbl, cc.xy(5, row));
		result.add(spid, cc.xy(7, row));
		row += 2;
		result.add(literatureRefLabel, cc.xy(1, row));
		result.add(literatureRef, cc.xywh(3, row, 5, 13));
		JPanel enclosingPanel = new JPanel();
		enclosingPanel.setLayout(new BorderLayout());
		enclosingPanel.add(result, BorderLayout.CENTER);
		return enclosingPanel;
	}

//	public static void main(String[] args) {
//		final ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
//		StorageService service = (StorageService) context.getBean("storageService");
//		DesignSynthesisPlan mDesignSynthesisPlan = null;
//		// mDesignSynthesisPlan = service.getDesignSynthesisPlanHeader("don't
//		// care");
//		JFrame frame = new JFrame();
//		frame.setTitle("Reaction Details");
//		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		JComponent panel = new ReactionDetails(mDesignSynthesisPlan).getJPanelReactionDetails();
//		frame.getContentPane().add(panel);
//		frame.setSize(800, 600);
//		frame.pack();
//		frame.setVisible(true);
//	}

	/**
	 * @return Returns the batchOwner.
	 */
	public JTextField getBatchOwner() {
		return batchOwner;
	}

	/**
	 * @param batchOwner
	 *            The batchOwner to set.
	 */
	public void setBatchOwner(JTextField batchOwner) {
		this.batchOwner = batchOwner;
	}

	/**
	 * @return Returns the contFromReact.
	 */
	public JTextField getContFromReact() {
		return contFromReact;
	}

	/**
	 * @param contFromReact
	 *            The contFromReact to set.
	 */
	public void setContFromReact(JTextField contFromReact) {
		this.contFromReact = contFromReact;
	}

	/**
	 * @return Returns the contToReact.
	 */
	public JTextField getContToReact() {
		return contToReact;
	}

	/**
	 * @param contToReact
	 *            The contToReact to set.
	 */
	public void setContToReact(JTextField contToReact) {
		this.contToReact = contToReact;
	}

	/**
	 * @return Returns the creationDate.
	 */
	public JTextField getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            The creationDate to set.
	 */
	public void setCreationDate(JTextField creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return Returns the literatureRef.
	 */
	public JTextArea getLiteratureRef() {
		return literatureRef;
	}

	/**
	 * @param literatureRef
	 *            The literatureRef to set.
	 */
	public void setLiteratureRef(JTextArea literatureRef) {
		this.literatureRef = literatureRef;
	}

	/**
	 * @return Returns the projectAliasName.
	 */
	public JTextField getProjectAliasName() {
		return projectAliasName;
	}

	/**
	 * @param projectAliasName
	 *            The projectAliasName to set.
	 */
	public void setProjectAliasName(JTextField projectAliasName) {
		this.projectAliasName = projectAliasName;
	}

	/**
	 * @return Returns the projectCodeName.
	 */
	public JComboBox getProjectCodeName() {
		return projectCodeName;
	}

	/**
	 * @param projectCodeName
	 *            The projectCodeName to set.
	 */
	public void setProjectCodeName(JComboBox projectCodeName) {
		this.projectCodeName = projectCodeName;
	}

	/**
	 * @return Returns the site.
	 */
	public JTextField getSite() {
		return site;
	}

	/**
	 * @param site
	 *            The site to set.
	 */
	public void setSite(JTextField site) {
		this.site = site;
	}

	/**
	 * @return Returns the spid.
	 */
	public JTextField getSpid() {
		return spid;
	}

	/**
	 * @param spid
	 *            The spid to set.
	 */
	public void setSpid(JTextField spid) {
		this.spid = spid;
	}

	/**
	 * @return Returns the subjectTitle.
	 */
	public JTextField getSubjectTitle() {
		return subjectTitle;
	}

	/**
	 * @param subjectTitle
	 *            The subjectTitle to set.
	 */
	public void setSubjectTitle(JTextField subjectTitle) {
		this.subjectTitle = subjectTitle;
	}

	/**
	 * @return Returns the therapeuticArea.
	 */
	public JComboBox getTherapeuticArea() {
		return therapeuticArea;
	}

	/**
	 * @param therapeuticArea
	 *            The therapeuticArea to set.
	 */
	public void setTherapeuticArea(JComboBox therapeuticArea) {
		this.therapeuticArea = therapeuticArea;
	}

	private void setLabelFonts(JComponent field) {
		if (field instanceof JLabel) {
			field.setFont(LABEL_FONT);
		} else if (field instanceof JComboBox) {
			field.setFont(COMBOBOX_FONT);
		}
	}
}
