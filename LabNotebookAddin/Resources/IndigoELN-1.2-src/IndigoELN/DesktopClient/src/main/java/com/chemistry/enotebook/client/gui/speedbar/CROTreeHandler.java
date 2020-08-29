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
package com.chemistry.enotebook.client.gui.speedbar;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.datamodel.speedbar.CRONotebook;
import com.chemistry.enotebook.client.datamodel.speedbar.CRONotebookPage;
import com.chemistry.enotebook.client.datamodel.speedbar.SpeedBarNodeInterface;
import com.chemistry.enotebook.client.datamodel.speedbar.SpeedBarPageGroup;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.print.gui.PrintRequest;
import com.chemistry.enotebook.domain.CROChemistInfo;
import com.chemistry.enotebook.domain.CROPageInfo;
import com.chemistry.enotebook.domain.CRORequestInfo;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.datamodel.page.InvalidNotebookRefException;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.common.chemistry.codetable.CodeTableCache;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * 
 * 
 */
public class CROTreeHandler extends CommonHandler { //implements MouseListener, TreeWillExpandListener {
//	private SpeedBarModel model = null;
//	private String sbType = null;
//	private JTree tree = null;
	private static String tool_tip_page_num = "";
	//private TreePath lastClickedPath = null;
	private JPopupMenu jPopupMenuExperiment;
	private JMenuItem jMenuItemLoadExperiment;
	private JMenuItem jMenuItemPrintExperiment;
	private TreePath tool_tip_node;


	public CROTreeHandler(JTree tree, String type) {
		super(tree, type);
		jPopupMenuExperiment = new JPopupMenu();
		jMenuItemLoadExperiment = new JMenuItem("Open this Experiment");
		jMenuItemLoadExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_OPEN));
		jMenuItemLoadExperiment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				OpenExperimentActionPerformed();
			}
		});
		jMenuItemPrintExperiment = new JMenuItem("Print this Experiment");
		jMenuItemPrintExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.PRINT));
		jMenuItemPrintExperiment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PrintExperimentActionPerformed();
			}
		});
		
		jPopupMenuExperiment.add(jMenuItemLoadExperiment);
		jPopupMenuExperiment.add(jMenuItemPrintExperiment);
	}

	
	protected void PrintExperimentActionPerformed() {
		SpeedBarNodeInterface sel[] = getSelectedUserObjects();
		if (sel != null) {
			ArrayList<PrintRequest> list = new ArrayList<PrintRequest>();
			NotebookRef ref = null;
			StringBuffer errorPages = new StringBuffer();
			for (int i = 0; i < sel.length; i++) {
				if (sel[i] instanceof CRONotebookPage) {
					CRONotebookPage sbp = (CRONotebookPage) sel[i];
					try {
						ref = new NotebookRef(sbp.getNotebook(), sbp.getPage());
						list.add(new PrintRequest(sbp.getSiteCode(), ref, new Integer(sbp.getVersion()), sbp.getPageType()));
					} catch (InvalidNotebookRefException e) {
						if (errorPages.length() > 0) {
							errorPages.append("\n");
						}
						errorPages.append("Notebook: " + sbp.getNotebook() + "-" + sbp.getPage());
						log.error("Failed ot create print request for notebook reference: " + sbp.getNotebook() + "-" + sbp.getPage(), e);
					}
				}
			}
			if (errorPages.length() > 0) {
				CeNErrorHandler.getInstance().logErrorMsg(null, "Failed to create print request for invalid notebook reference!\n" + errorPages.toString(), "Possible failure in print request", JOptionPane.ERROR_MESSAGE);
			}
			if (list.size() > 0) {
				MasterController.getGuiController().printExperiments(list);
			}
		}
	}

	public SpeedBarNodeInterface[] getSelectedUserObjects() {
		SpeedBarNodeInterface results[] = null;
		TreePath paths[] = tree.getSelectionPaths();
		if (paths != null) {
			results = new SpeedBarNodeInterface[paths.length];
			for (int i = 0; i < paths.length; i++)
				results[i] = (SpeedBarNodeInterface) ((DefaultMutableTreeNode) paths[i].getLastPathComponent()).getUserObject();
		}
		return results;
	}

	public void mousePressed(MouseEvent evt) {
		//if (evt.getButton() != MouseEvent.BUTTON3) {
			lastClickedPath = tree.getPathForLocation(evt.getX(), evt.getY());
			updateSelectionPath((JTree) evt.getSource(), lastClickedPath, evt.isShiftDown(), evt.isControlDown());
		//}
			
			mouseAction(evt);
	}

	public TreePath getToolTipNode() {
		return this.tool_tip_node;
	}
	
	public Point getToolTipLocation(MouseEvent e) {
		Point result = null;
		tool_tip_node = tree.getClosestPathForLocation(e.getX(), e.getY());
		int row = tree.getRowForLocation(e.getX(), e.getY());
		if (row != 0) {
			Rectangle r = tree.getRowBounds(row);
			if (r != null) result = new Point(r.x + r.width + 5, r.y + (r.height / 2));
		}                
		
		tool_tip_node = tree.getClosestPathForLocation(e.getX(), e.getY());

		return result;
	}
	
	public void mouseEntered(MouseEvent evt) {
	}

	public void mouseExited(MouseEvent evt) {
	}

	public void mouseReleased(MouseEvent evt) {
		mouseAction(evt);		
	}
	
	private void mouseAction(MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			JTree tree = (JTree) evt.getSource();
			lastClickedPath = tree.getPathForLocation(evt.getX(), evt.getY());
			if (lastClickedPath != null) {
				DefaultMutableTreeNode mouseNode = (DefaultMutableTreeNode) lastClickedPath.getLastPathComponent();
				// Right click does not auto-select the node so we should do it
				// if there is no other selection
				// or the node right clicked is not in selection list
				if (!tree.isPathSelected(lastClickedPath))
					updateSelectionPath(tree, lastClickedPath, false, false);
				if (mouseNode.getUserObject() instanceof CRONotebookPage) {// if its an experiment
					jPopupMenuExperiment.show(evt.getComponent(), evt.getX(), evt.getY());
					updatePosition(jPopupMenuExperiment);
				} 
			}
		}
		MasterController.getGUIComponent().refreshIcons();
	}
	
	public SpeedBarNodeInterface speedBarNavigateTo(String site, String users_fullname, NotebookRef nbRef, int version) {
		NotebookUser user = MasterController.getUser();
		// final int groupSize = NotebookPageUtil.NB_PAGE_GROUP_SIZE;
		String nb = nbRef.getNbNumber();
		String ex = nbRef.getNbPage();
		String group = SpeedBarPageGroup.getGroupFromExperiment(ex);
		if (version > 1)
			ex = ex + " v" + version;
		TreePath path = null;
		if (sbType.equals(SpeedBarModel.SB_MY_BOOKMARKS)) {
			String[] expandPath = { user.getFullName(), nb, group, ex };
			path = expandPath(expandPath, false);
		} else {
			// need to translate userid to a user's fullname
			String[] expandPath = { site, users_fullname, nb, group, ex };
			path = expandPath(expandPath, false);
		}
		if (path != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			return (SpeedBarNodeInterface) node.getUserObject();
		} else
			return null;
	}

	
	public String[] getNotebooksForUser(String siteCode, String user) {
		TreePath path = null;
		String[] result = null;
		try {
			String site = CodeTableCache.getCache().getSiteDescription(siteCode);
			String rootName = ((TreeNode) tree.getModel().getRoot()).toString();
			String[] userBranch = null;
			if (rootName.equals(SpeedBarModel.SB_ALL_SITES))
				userBranch = new String[] { rootName, site, user };
			else
				userBranch = new String[] { rootName, user };
			expandPath(userBranch, false);
			path = findByName(userBranch);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		if (path != null) {
			DefaultMutableTreeNode userNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (userNode != null && userNode.getChildCount() > 0) {
				result = new String[userNode.getChildCount()];
				for (int i = 0; i < userNode.getChildCount(); i++)
					result[i] = userNode.getChildAt(i).toString();
			}
		}
		return result;
	}

//	public Object[] getSelectedUserObjects() {
//		Object results[] = null;
//		TreePath paths[] = tree.getSelectionPaths();
//		if (paths != null) {
//			results = new Object[paths.length];
//			for (int i = 0; i < paths.length; i++)
//				results[i] = ((DefaultMutableTreeNode) paths[i].getLastPathComponent()).getUserObject();
//		}
//		return results;
//	}

	public Object getLastSelectedUserObject() {
		if (lastClickedPath != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) lastClickedPath.getLastPathComponent();
			return  node.getUserObject();
		} else
			return null;
	}

	protected void OpenExperimentActionPerformed() {
		Object sel[] = getSelectedUserObjects();
		if (sel != null) {
			for (int i = 0; i < sel.length; i++) {
				if (sel[i] instanceof CRONotebookPage) {
					CRONotebookPage mCRONotebookPage = (CRONotebookPage)sel[i];
					NotebookPageModel sbp = mCRONotebookPage.getPageModel();
					MasterController.getGuiController().openPCeNExperimentCombinedBkndPage("", sbp.toString(),sbp.getVersion());
					
				}
			}
		}
	}
	
	public TreeCellRenderer getNewCellRenderer() {
		return new CROSpeedBarCellRenderer();
	}
	
	private String CreateToolTip(CRONotebookPage sbp, String image) {
		String lineSeparator = System.getProperty("line.separator");
		String lineTab = "    ";
		
		StringBuffer buff = new StringBuffer();
		NotebookPageModel page = sbp.getPageModel();
		
		buff.append(page.getNotebookRefAsString() + lineSeparator);
		
		if (StringUtils.isNotBlank(page.getSubject())) {
			buff.append(page.getSubject() + lineSeparator);
		}
		
		boolean addPunc = false;
		
		String date = page.getCreationDate();
		
		if (StringUtils.isNotBlank(date)) {
			buff.append(date);
			addPunc = true;
		}
		
		String status = page.getPageStatus();
		
		if (StringUtils.isNotBlank(status)) {
			if (addPunc) {
				buff.append(", ");
			}
			buff.append(status);
			addPunc = true;
		}
		
		if (addPunc) {
			buff.append(lineTab + lineSeparator);
		}
		
		if (StringUtils.isNotBlank(page.getProjectCode())) {
			String projDesc = null;
			
			try {
				projDesc = CodeTableCache.getCache().getProjectsDescription(page.getProjectCode());
			} catch (Exception e) {
				// Do nothing
			}
			
			if (StringUtils.isBlank(projDesc)) {
				projDesc = page.getProjectCode();
			} else {
				projDesc = page.getProjectCode() + " - " + projDesc;
			}
			
			buff.append(projDesc);
			buff.append(lineTab + lineSeparator);
		}
		
		addPunc = false;
		
		String type = page.getPageType();
		
		if (StringUtils.isNotBlank(type)) {
			buff.append("Type: " + type);
			addPunc = true;
		}
		
		if (page.getCroInfo() != null && StringUtils.isNotBlank(page.getCroInfo().getRequestId())) {
			if (addPunc) {
				buff.append(lineTab + lineSeparator);
			}
			buff.append("Request id: " + page.getCroInfo().getRequestId());
			addPunc = true;
		}
		
		if (addPunc) {
			buff.append(lineTab + lineSeparator);
		}
		
		return buff.toString();
	}
	
	private class CROSpeedBarCellRenderer extends DefaultTreeCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7748885994208453984L;

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
				int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			ImageIcon sbIcon = null;
			String sbToolTip = null;
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
			Object usrObj = treeNode.getUserObject();
			sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.CONTENTS);
			
			if (usrObj instanceof CROPageInfo) {
				sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.WORLD);
			} else if (usrObj instanceof CRONotebookPage) {
				sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_COMPLETE);
				// handle tooltip
				CRONotebookPage p = (CRONotebookPage) usrObj;
				if (hasFocus && !jPopupMenuExperiment.isVisible()) {
					if (!tool_tip_page_num.equals(p.getPageModel().getNbRef().getNbPage())) {
						sbToolTip = CreateToolTip(p, null);
					}
				}
			} else if (usrObj instanceof CROChemistInfo) {
				sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.USER);
			} else if (usrObj instanceof CRORequestInfo) {
				sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_OPEN);
			} else if (usrObj instanceof CRONotebook) {
				sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.BOOK_STATUS_CLOSED);
			} else {
				if (expanded)
					sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.WORLD);
				else
					sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.WORLD);
			}
			
			setIcon(sbIcon);
			setToolTipText(sbToolTip);
//			lastToolTip = sbToolTip;		
			return this;
		}
	}
}
