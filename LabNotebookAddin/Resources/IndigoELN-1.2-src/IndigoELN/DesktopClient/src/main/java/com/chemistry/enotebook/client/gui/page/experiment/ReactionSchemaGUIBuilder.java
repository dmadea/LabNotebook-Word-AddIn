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

import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane;
import com.chemistry.enotebook.client.gui.common.ekit.ProcedureContainer;
import com.chemistry.enotebook.client.gui.page.batch.ReactionStepJTabPaneFactory;
import com.chemistry.enotebook.domain.NotebookPageModel;

import javax.swing.*;

/**
 * 
 * 
 */
public class ReactionSchemaGUIBuilder {
	// private int displayMode;
	private static final int STOICH = 0;
	private static final int MONOMER_MAP = 1;
	// private ParallelNotebookPageGUI pNotebookPageGUI;
	private JTabbedPane stepTabs;
	private ParallelCeNMonomerReactantMapCollapsiblePane monomerReactMapPane;
	private CollapsiblePane monomerReactWorkingPane;
	private ProcedureContainer Proc_cont_summary;
	private CollapsiblePane proc_cont_Coll_Pane;
	private JPanel mPanel;
	// private NotebookPageModel pageModel;
	private ReactionStepJTabPaneFactory tabFactory;

	public JTabbedPane createRxnScemaTabbedPane(NotebookPageModel pageModel, int displayMode) {
		tabFactory = new ReactionStepJTabPaneFactory(null, pageModel);
		return stepTabs;
	}

	/**
	 * @return the monomerReactWorkingPane
	 */
	public CollapsiblePane getMonomerReactWorkingPane() {
		return monomerReactWorkingPane;
	}

	/**
	 * @return the stoichWorkingPane TODO Jino
	 * 
	 * public CollapsiblePane getStoichWorkingPane() { return stoichWorkingPane; }
	 */
	/**
	 * @return the proc_cont_Coll_Pane
	 */
	public CollapsiblePane getProc_cont_Coll_Pane() {
		return proc_cont_Coll_Pane;
	}
}
