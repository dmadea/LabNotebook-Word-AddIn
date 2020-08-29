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
import com.chemistry.enotebook.client.datamodel.speedbar.*;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;

public class CommonHandler implements CommonHandlerInterface, MouseListener, TreeWillExpandListener {

	public static final Log log = LogFactory.getLog(CommonHandler.class);

	protected SpeedBarModel model = null;
	protected String sbType = null;
	protected JTree tree = null;
	protected TreePath lastClickedPath = null;

	public CommonHandler(JTree tree, String type) {
		this.tree = tree;
		this.sbType = type;
		resetModel();
	}

	public void resetModel() {
		model = new SpeedBarModel(sbType);
		tree.setModel(model);
	}

	public SpeedBarModel getModel() {
		return model;
	}

	public String getSpeedBarType() {
		return sbType;
	}

	public void refresh() {
		String[] pathNodes = null;
		// save off the current path, if we are at the page level just navigate
		// the grouping
		TreePath path = tree.getSelectionPath();
		if (path != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			// if (node.getUserObject() instanceof SpeedBarPage) path =
			// path.getParentPath();
		}
		if (path != null && path.getPathCount() > 1) {
			pathNodes = new String[path.getPathCount() - 1];
			for (int i = 1; i < path.getPathCount(); i++)
				pathNodes[i - 1] = path.getPathComponent(i).toString();
		}
		// clear out old data
		resetModel();
		getModel().expandRoot();
		// re-create the route
		final String[] pn = pathNodes;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (pn != null) {
					expandPath(pn, false);
				}
			}
		});
	}

	/*
	 * (non-Javadoc) @param expandPath - List of node names (as part of a path) to expand @return
	 * 
	 * Method to expand Tree based on each identifier in expand Path
	 */
	private boolean expandFullPath(String[] expandPath) {
		boolean status = false;
		TreePath path = findByName(expandPath);
		if (path != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (node.getChildCount() == 0) {
				status = model.expandNodeImmediate(node);
			} else
				status = true;
		}
		return status;
	}

	/**
	 * @param expandPath -
	 *            List of node names (as part of a path) to expand
	 * @param openFlag -
	 *            Flag indicating that last entry should be openned after expansion
	 * 
	 * Method to expand the Tree based on each identifier in expand path and potentially open the last node
	 */
	public TreePath expandPath(String[] expandPath, boolean openFlag) {
		TreePath result = null;
		try {
			// Add Root name to path
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
			String rootName = rootNode.toString();
			String[] fullPath = new String[expandPath.length + 1];
			fullPath[0] = rootName;
			for (int j = 0; j < expandPath.length; j++)
				fullPath[j + 1] = expandPath[j];
			// process the expandPath recursively, expanding each node in
			// the path
			int i;
			String[] prevPath = null;
			String[] currentPath = null;
			for (i = 0; i < fullPath.length; i++) {
				prevPath = currentPath;
				currentPath = new String[i + 1];
				for (int j = 0; j <= i; j++)
					currentPath[j] = fullPath[j];
				if (!expandFullPath(currentPath))
					break;
			}
			// Verify we were able to expand each element
			if (i == fullPath.length) {
				// Now Select and make visible the path designated.
				TreePath path = findByName(fullPath);
				if (path != null) {
					tree.scrollPathToVisible(path);
					setSelectedPath(path);
					if (openFlag)
						OpenExperimentActionPerformed();
					result = path;
				}
			} else if (prevPath != null && i > 1) {
				TreePath path = findByName(prevPath);
				if (path != null)
					tree.scrollPathToVisible(path);
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		return result;
	}

	protected void updateSelectionPath(JTree tree, TreePath path, boolean altDown, boolean ctrlDown) {
		if (path != null) {
			if (altDown || ctrlDown) {
				// Alt or Ctrl is down which means there are possibly multiple
				// things selected
				// Validate that all of them are allowed to be selected
				TreePath paths[] = tree.getSelectionPaths();
				if (paths != null) {
					boolean contentsFound = false;
					boolean pagesFound = false;
					for (int i = 0; i < paths.length; i++) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
						if (node.getUserObject() instanceof SpeedBarSite || node.getUserObject() instanceof SpeedBarNotebook
								|| node.getUserObject() instanceof SpeedBarUser
								|| node.getUserObject() instanceof SpeedBarPageGroup) {
							tree.removeSelectionPath(paths[i]);
						} else if (node.getUserObject() instanceof SpeedBarContents)
							contentsFound = true;
						else if (node.getUserObject() instanceof SpeedBarPage)
							pagesFound = true;
					}
					if (pagesFound && contentsFound)
						for (int i = 0; i < paths.length; i++) {
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
							if (node.getUserObject() instanceof SpeedBarContents)
								tree.removeSelectionPath(paths[i]);
						}
				}
			} else {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) lastClickedPath.getLastPathComponent();
				if (node.getUserObject() instanceof SpeedBarContents || node.getUserObject() instanceof SpeedBarPage || node.getUserObject() instanceof CRONotebookPage || node.getUserObject() instanceof SpeedBarNotebook|| node.getUserObject() instanceof SpeedBarPageGroup)
					setSelectedPath(lastClickedPath);
				else if (node.getUserObject() instanceof SpeedBarSite || node.getUserObject() instanceof SpeedBarNotebook
						|| node.getUserObject() instanceof SpeedBarUser )
					tree.removeSelectionPath(lastClickedPath);
			}
		}
	}

	public TreePath findByName(String[] names) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		return find2(new TreePath(root), names, 0);
	}

	private TreePath find2(TreePath parent, Object[] nodes, int depth) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		Object o = node.toString();
		// If equal, go down the branch
		if (o.equals(nodes[depth])) {
			// If at end, return match
			if (depth == nodes.length - 1) {
				return parent;
			}
			// Traverse children
			if (node.getChildCount() >= 0) {
				for (Enumeration e = node.children(); e.hasMoreElements();) {
					TreeNode n = (TreeNode) e.nextElement();
					TreePath path = parent.pathByAddingChild(n);
					TreePath result = find2(path, nodes, depth + 1);
					// Found a match
					if (result != null)
						return result;
				}
			}
		}
		// No match at this branch
		return null;
	}

	public void refreshCurrentNode() {
		TreePath path = tree.getSelectionPath();
		if (path != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (node != null)
				model.nodeChanged(node);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouse*(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent evt) {
		// check for double-clicks
		if (evt.getClickCount() == 2) {
			JTree tree = (JTree) evt.getSource();
			TreePath path = tree.getPathForLocation(evt.getX(), evt.getY());
			if (path != null) {
				lastClickedPath = path;
				DefaultMutableTreeNode mouseNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				if (mouseNode.getUserObject() instanceof CRONotebookPage) {
					OpenExperimentActionPerformed();
				} else if (mouseNode.getUserObject() instanceof SpeedBarPage) {
					OpenExperimentActionPerformed();
				} else if (mouseNode.getUserObject() instanceof SpeedBarContents)
					OpenTableContentsActionPerformed();
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mousePressed(MouseEvent evt) {
		if (evt.getButton() != MouseEvent.BUTTON3) {
			lastClickedPath = tree.getPathForLocation(evt.getX(), evt.getY());
			updateSelectionPath((JTree) evt.getSource(), lastClickedPath, evt.isShiftDown(), evt.isControlDown());
		}
	}

	public void mouseReleased(MouseEvent e) {
		// override
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(javax.swing.event.TreeExpansionEvent)
	 */
	public void treeWillExpand(TreeExpansionEvent evt) throws ExpandVetoException {
		boolean veto = false; // Cancel the operation if desired
		JTree tree = (JTree) evt.getSource();
		// Get the path that will be expanded
		TreePath path = evt.getPath();
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		if (parentNode.getChildCount() == 0)
			model.expandNode(parentNode, tree);
		if (veto)
			throw new ExpandVetoException(evt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.TreeWillExpandListener#treeWillCollapse(javax.swing.event.TreeExpansionEvent)
	 */
	public void treeWillCollapse(TreeExpansionEvent evt) throws ExpandVetoException {
		boolean veto = false; // Cancel the operation if desired
		JTree tree = (JTree) evt.getSource();
		// Get the path that will be collapsed
		TreePath path = evt.getPath();
		if (veto)
			throw new ExpandVetoException(evt);
	}



	public Point getToolTipLocation(MouseEvent e) {
		Point result = null;
		int row = tree.getRowForLocation(e.getX(), e.getY());
		if (row != 0) {
			Rectangle r = tree.getRowBounds(row);
			if (r != null)
				result = new Point(r.x + r.width + 5, r.y + (r.height / 2));
		}
		return result;
	}

	public Object[] getSelectedUserObjects()
	{
		Object results[] = null;
		
		TreePath paths[] = tree.getSelectionPaths();
		if (paths != null) {
			results = new SpeedBarNodeInterface[paths.length];
			for (int i=0; i < paths.length; i++) 
				results[i] = (SpeedBarNodeInterface)((DefaultMutableTreeNode) paths[i].getLastPathComponent()).getUserObject();
		}
		
		return results;
	}

	private void setSelectedPath(TreePath path) {
		lastClickedPath = path;
		tree.setSelectionPath(lastClickedPath);
	}

	// overridden only for user interacted handlers.  Below is needed for auto-load of a page on startup (Called via Gui/CommonHander.ExpandPath)
	protected void OpenExperimentActionPerformed() { 
		Object sel[] = getSelectedUserObjects();
		if (sel != null) {
			for (int i=0; i < sel.length; i++) {
				if (sel[i] instanceof SpeedBarPage) {
					SpeedBarPage sbp = (SpeedBarPage) sel[i];
					if (sbp.isLoadable()) 
						MasterController.getGuiController().openPCeNExperiment(sbp.getSiteCode(), sbp.getNotebook(), sbp.getPage(), sbp.getVersion());
				}
			}
		}
	}
	
	//override
	protected void OpenExperimentActionPerformed(boolean bool) { 
	}
  
	//override
	protected void OpenTableContentsActionPerformed() {
	}
	
	// helper functions to move a rectangle onto the screen
	protected void updatePosition(JPopupMenu menu) {
		// determine boundaries
		Point point = menu.getLocationOnScreen();
		Dimension size = menu.getSize();
		Rectangle oldRect = new Rectangle(point.x, point.y, size.width, size.height);
		Rectangle newRect = ensureRectIsVisible(oldRect);
		// rects differ, need moving
		if (!oldRect.equals(newRect)) {
			Window window = SwingUtilities.getWindowAncestor(menu);
			if (window != null)
				window.setLocation(newRect.x, newRect.y);
		}
	}
	
	private Rectangle ensureRectIsVisible(Rectangle bounds) {
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		return new Rectangle(Math.max(0, Math.min(size.width - bounds.width, bounds.x)), Math.max(0, Math.min(size.height
				- bounds.height, bounds.y)), bounds.width, bounds.height);
	}


	public SpeedBarNodeInterface getSpeedBarNode(String[] names) {
		TreePath path = findByName(names);
		if (path != null)
			return (SpeedBarNodeInterface) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
		else
			return null;
	}
	
	public JTree getTree() {
		return tree;
	}
}
