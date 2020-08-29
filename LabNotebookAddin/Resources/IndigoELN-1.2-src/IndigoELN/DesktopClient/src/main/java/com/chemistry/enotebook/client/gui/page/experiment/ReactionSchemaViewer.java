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

import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.domain.ReactionStepModel;
import com.chemistry.enotebook.experiment.datamodel.compound.Compound;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.Decoder;
import com.chemistry.viewer.ReactionViewer;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * 
 * 
 */
public class ReactionSchemaViewer {
	private JPanel jPanelReaction;
	private ChemistryPanel chimeProSwing;
	private ReactionViewer reactionCanvas;
	private ReactionStepModel intermediateStep = null;
	private boolean readOnly = false;

	public ReactionSchemaViewer() {
		this.intermediateStep = new ReactionStepModel();
		chimeProSwing = new ChemistryPanel();
		initGUI();
	}

	public ReactionSchemaViewer(ReactionStepModel intermediateStep) {
		this.intermediateStep = intermediateStep;
		chimeProSwing = new ChemistryPanel();
		initGUI();
	}

	private void initGUI() {
		try {
			jPanelReaction = new JPanel();
			BorderLayout jPanelReactionLayout = new BorderLayout();
			jPanelReaction.setLayout(jPanelReactionLayout);
			jPanelReactionLayout.setHgap(0);
			jPanelReactionLayout.setVgap(0);
			jPanelReaction.setPreferredSize(new java.awt.Dimension(517, 200));
			jPanelReaction.setBackground(new java.awt.Color(255, 255, 255));
			jPanelReaction.setBorder(new MatteBorder(new Insets(1, 1, 1, 1),
					new java.awt.Color(0, 0, 0)));
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(jPanelReaction, e);
		}
		postInitGUI();
	}

	private void postInitGUI() {
		try {
			reactionCanvas = new ReactionViewer("Magnesium TriChloride", "", 
					ReactionViewer.VIEW_NATIVE);
			reactionCanvas.setEditorType(Compound.ISISDRAW);
			reactionCanvas.setReadOnly(readOnly);
			// System.out.println("Inter Step Rxn
			// "+intermediateStep.getStepNumber()+"
			// "+intermediateStep.getRxnScheme().getDspReactionDefinition());
			String decodedString = Decoder.decodeString(intermediateStep
					.getRxnScheme().getStringSketchAsString());
			int index = decodedString.indexOf("M  END");
			if (index >= 0 || CommonUtils.isNull(decodedString)) {
				chimeProSwing.setMolfileData(decodedString);
			} else {
				throw new UnsupportedOperationException("Supported only operations with mol or rxn file format");
			}
			// System.out.println("ReactionSchemaViewer::createpostInitGUIToolTip
			// Decoder.decodeString
			// intermediateStep.getRxnScheme().getDspReactionDefinition =
			// "+decodedString);
			// chimeProSwing.setMolfileData(decodedString);
			reactionCanvas.setChemistry(chimeProSwing.getMolfileData()
					.getBytes());
			jPanelReaction.add(reactionCanvas, BorderLayout.CENTER);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(jPanelReaction, e);
		}
	}

	/**
	 * @return Returns the jPanelReaction.
	 */
	public JPanel getJPanelReactionSchema() {
		return jPanelReaction;
	}

	/**
	 * @param panelReaction
	 *            The jPanelReaction to set.
	 */
	public void setJPanelReaction(JPanel panelReaction) {
		jPanelReaction = panelReaction;
	}

	public static void main(String[] args) {
		/*
		 * final ApplicationContext context = new
		 * ClassPathXmlApplicationContext( "applicationContext.xml");
		 * StorageService service = (StorageService) context
		 * .getBean("storageService"); DesignSynthesisPlan mDesignSynthesisPlan
		 * = (DesignSynthesisPlan) service
		 * .getDesignSynthesisPlanWithSummaryReaction("don't care", true);
		 * 
		 * JFrame frame = new JFrame(); frame.setTitle("Reaction Sheme");
		 * frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		 * ReactionStep summaryStep = mDesignSynthesisPlan.getSummaryPlan();
		 * JComponent panel = new ReactionSchemaViewer(summaryStep)
		 * .getJPanelReactionSchema(); frame.getContentPane().add(panel);
		 * frame.setSize(517, 200); frame.pack(); frame.setVisible(true);
		 */
	}
}
