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
package com.chemistry.enotebook.client.gui.page.stoichiometry.search;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.stoichiometry.search.dbsetup.CheckNode;
import com.chemistry.enotebook.client.gui.page.stoichiometry.search.dbsetup.DBListCellRenderer;
import com.chemistry.enotebook.client.gui.page.stoichiometry.search.dbsetup.DBListTree;
import com.chemistry.enotebook.client.gui.page.stoichiometry.search.dbsetup.DBListTreeNodeSelectionListener;
import com.chemistry.enotebook.compoundmgmtservice.delegate.CompoundMgmtServiceDelegate;
import com.chemistry.enotebook.compoundmgmtservice.exception.CompoundMgmtServiceException;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * 
 *
 */
public class CompoundMgmtDataSourceSetupPanel extends JPanel {
	
	private static final long serialVersionUID = -4083353179372373339L;
	
	private ArrayList<String> selectedDBList = new ArrayList<String>();
	private ArrayList cenSelected = new ArrayList();
	private JTree tree = null;
	private boolean _connected = false;

	/**
	 * 
	 */
	public CompoundMgmtDataSourceSetupPanel() {
		super();
		init();
	}

	/**
	 * @param arg0
	 */
	public CompoundMgmtDataSourceSetupPanel(boolean arg0) {
		super(arg0);
		init();
	}

	/**
	 * @param arg0
	 */
	public CompoundMgmtDataSourceSetupPanel(LayoutManager arg0) {
		super(arg0);
		init();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CompoundMgmtDataSourceSetupPanel(LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
		init();
	}

	private void init() {
		// JLabel workInProgress = new JLabel(" Work in progress");
		setLayout(new BorderLayout());
		String xmlString = "";
		try {
			CompoundMgmtServiceDelegate cmpMgmtDelegate = 
				new CompoundMgmtServiceDelegate();
			xmlString = cmpMgmtDelegate.getAvailableDBList().get(0);
			//xmlString = "";
			if (StringUtils.isBlank(xmlString)) {
				tree = null;
			} else {
				// Remove reaction databases from list
				String newString = "";
				int startPos = xmlString.toLowerCase().indexOf("<reaccs");
				if (startPos >= 0) {
					newString = xmlString.substring(0, startPos);
					int endPos = xmlString.toLowerCase().indexOf("</reaccs");
					if (endPos >= 0)
						newString += xmlString.substring(endPos + "</reaccs>".length());
				} else {
					newString = xmlString;
				}
				tree = new DBListTree(newString);
				if (tree != null) {
					tree.setCellRenderer(new DBListCellRenderer());
					tree.addMouseListener(new DBListTreeNodeSelectionListener(tree, selectedDBList, cenSelected));
					_connected = true;
				}
			}
		} catch (CompoundMgmtServiceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
		if (tree != null) {
			// tree.setRowHeight(0);
			JScrollPane sPane = new JScrollPane(tree);
			sPane.setVisible(true);
			sPane.setBounds(new java.awt.Rectangle(10, 10, 800, 600));
			add(sPane, BorderLayout.CENTER);
		}
	}

	public boolean isConnected() {
		return _connected;
	}

	public ArrayList<String> getSelectedDBList() {
		return selectedDBList;
	}

	public boolean getCENSelected() {
		return !(cenSelected.isEmpty());
	}
	
	public void setDefaultSelectedDBList(List<String> defaultSelected, boolean cenSelected) {
		if (defaultSelected.size() < 1)
			defaultSelected = Arrays.asList(NotebookUser.DEFAULT_STRUCTURE_DATABASES);
		selectedDBList.addAll(defaultSelected);
		if (cenSelected) {
			this.cenSelected.add(Boolean.TRUE);		
		}
		ArrayList<String> allSelectableNodes = new ArrayList<String>();
		for(String s : selectedDBList) {
			String[] sArray = s.split("/");
			for (int i = 0; i < sArray.length; i++) {
				allSelectableNodes.add(sArray[i]);
			}
		}
		
		//TODO
		if (cenSelected) {
			allSelectableNodes.add("CEN db");
			allSelectableNodes.add("Reactions/Structures");
		}
		
		// Run through the tree
		if (tree != null) {
			TreePath path = tree.getPathForRow(0);
			CheckNode rootNode = (CheckNode) path.getPathComponent(0);
			if (allSelectableNodes.size() > 0) {
				rootNode.setSelected(true);
			}
			setNodesChecked(rootNode, allSelectableNodes);
			// tree.repaint();
		}
	}

	public void setNodesChecked(TreeNode parentNode, ArrayList<String> allSelectableNodes) {
		int childCount = parentNode.getChildCount();
		String nodeName = null;
		for (int i = 0; i < childCount; i++) {
			CheckNode childNode = (CheckNode) parentNode.getChildAt(i);
			nodeName = childNode.getUserObject() instanceof Node? ((Node) childNode.getUserObject()).getNodeName() : (String)childNode.getUserObject();

			if (allSelectableNodes.contains(nodeName)) {
				childNode.setSelected(true);
				allSelectableNodes.remove(allSelectableNodes.indexOf(nodeName));
			} else {
				childNode.setSelected(false);
			}
			
			if (!childNode.isLeaf()) {
				setNodesChecked(childNode, allSelectableNodes);
			}
		}
	}
}
