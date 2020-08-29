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
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.domain.CROChemistInfo;
import com.chemistry.enotebook.domain.CROPageInfo;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import com.chemistry.enotebook.storage.SpeedBarContext;
import com.chemistry.enotebook.storage.StorageVO;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.utils.SwingWorker;
import com.common.chemistry.codetable.CodeTableCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.*;

public class SpeedBarModel extends DefaultTreeModel implements TreeNodeFactory {
	
	private static final long serialVersionUID = 4155412679891070626L;
	
	private static final Log log = LogFactory.getLog(SpeedBarModel.class);
	private boolean debug = true;
	
	public final static String SB_ALL_SITES = "All Sites";
	public final static String SB_MY_BOOKMARKS = "My Bookmarks";
	public final static String SB_DSP_TREE = "Design Synthesis Plans";
	public final static String SB_EXTERNAL = "External Collaborators";
	private String rootName = null; // root Name for this SpeedBar Model
	private static StorageDelegate StorageOBJ = null; // All SpeedBar Models
	// Share a common
	// Storage delegate
	private static Vector<SwingWorker> workerList = new Vector<SwingWorker>(); // List of workers
	private ArrayList<ArrayList<String>> notebooks;

	// filling out speed Bar
	public SpeedBarModel(String name) {
		super(new DefaultMutableTreeNode(name));
		this.rootName = name;
		setAsksAllowsChildren(true);
	}

	/*
	 * (non-Javadoc) @return
	 * 
	 * Method to create common delegate to process speed bar requests
	 */
	private synchronized boolean init() {
		boolean status = false;
		try {
			if (StorageOBJ == null) {
				StorageOBJ = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
			}
			status = true;
		} catch (Exception e) {
			log.info("Failed initialization of Speedbar: ", e);
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object nodeObj) {
		try {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodeObj;
			Object usrObj = node.getUserObject();
			if ((usrObj instanceof String) && !((String) usrObj).equals(rootName))
				return true;
			else if (usrObj instanceof SpeedBarPage)
				return true;
			else if (usrObj instanceof SpeedBarContents)
				return true;
			else if (usrObj instanceof CRONotebookPage)
				return true;
			else
				return false;
		} catch (RuntimeException e) {
			log.error("Failed during attempt to answer isLeaf.", e);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.client.datamodel.speedbar.TreeNodeFactory#createChildren(java.lang.Object)
	 */
	public DefaultMutableTreeNode[] createChildren(Object parentNode) throws Exception {
		DefaultMutableTreeNode[] children = null;
		try {
			Object userObject = ((DefaultMutableTreeNode) parentNode).getUserObject();
			if (userObject instanceof SpeedBarSite)
				children = getSiteUsers((SpeedBarSite) userObject);
			else if (userObject instanceof SpeedBarUser)
				children = getUserNotebooks((SpeedBarUser) userObject);
			else if (userObject instanceof SpeedBarNotebook)
				children = getNotebookExperimentGroups((SpeedBarNotebook) userObject);
			else if (userObject instanceof CROPageInfo)
				children = getAllChemistsUnderCRO ((CROPageInfo) userObject);
			else if (userObject instanceof CROChemistInfo)
				children = getAllNoteBooksUnderChemist ((CROChemistInfo) userObject);	
			else if (userObject instanceof CRONotebook)
				children = getAllPagesForNotebook((CRONotebook) userObject);
			//else if (userObject instanceof CROChemistInfo)
			//	children = getAllRequestIdsForChemist  ((CROChemistInfo) userObject);
			//else if (userObject instanceof CRORequestInfo)
			//	children = getAllPagesWithSummaryForRequestId  (((CRORequestInfo) userObject).getRequestId());
			else if (userObject instanceof SpeedBarPageGroup)
				children = getExperiments((SpeedBarPageGroup) userObject);
			else if (userObject instanceof NotebookPageModel)
				; //children = getExperiments((NotebookPageModel) userObject);
			else if (userObject instanceof SpeedBarPage)
				; // Page has no Children
			else if (userObject instanceof SpeedBarContents)
				; // Contents has no Children
			else { // Must be a Root
				if (userObject.equals(SB_ALL_SITES)) { // Build All Sites Root
					long startTime = System.currentTimeMillis();
					List<Properties> siteList = CodeTableCache.getCache().getSites();
					long endTime = System.currentTimeMillis();
					System.out.println(this.getClass().getName() + " getSites took " + (endTime - startTime));
						children = new DefaultMutableTreeNode[siteList.size()];
						for (int i = 0; i < siteList.size(); i++)
							children[i] = new DefaultMutableTreeNode(new SpeedBarSite(siteList.get(i)));
				}  else if (userObject.equals(SB_MY_BOOKMARKS)) {  // Build My Bookmarks Root
					// Build a fake recordset for user to use in User node
					NotebookUser tempUser = MasterController.getUser();
					ArrayList<String> UserLogin = new ArrayList<String>();
					UserLogin.add(tempUser.getNTUserID().toUpperCase());
					UserLogin.add(tempUser.getFullName());
					UserLogin.add(tempUser.getSiteCode());
					// Build a node for this user
					children = new DefaultMutableTreeNode[1];
					String siteName = CodeTableCache.getCache().getSiteDescription(tempUser.getSiteCode());
					children[0] = new DefaultMutableTreeNode(new SpeedBarUser(UserLogin, siteName, tempUser.getSiteCode()));
				} else if(userObject.equals(SB_EXTERNAL)){
					children = getCROIds();
				}
			}
		} catch (RuntimeException e) {
			log.error("Failed while creating Children", e);
		}
		return children;
	}
	
	

	/*
	 * (non-Javadoc) @param sbSite - Site Node @return
	 * 
	 * Method to retrieve Tree Nodes of all Users at a given Site
	 */
	private DefaultMutableTreeNode[] getSiteUsers(SpeedBarSite sbSite) {
		DefaultMutableTreeNode[] results = null; // new
		try {
			// DefaultMutableTreeNode[0];
			if (init()) {
				// Create request for a list of users for a given site
				SpeedBarContext sbCtx = new SpeedBarContext();
				sbCtx.setSiteCode(sbSite.getSiteCode());
				try {
					// Send Request
					StorageVO vo = ((SpeedBarContext) StorageOBJ.retrieveData(sbCtx, MasterController.getUser().getSessionIdentifier())).getResults();
					// Convert response to an array list of users
					ArrayList<ArrayList<String>> users = null;
					if (vo != null)
						users = vo.toArrayList();
					// Create a node for each user
					if (users != null && users.size() > 0) {
						results = new DefaultMutableTreeNode[users.size()];
						for (int i = 0; i < users.size(); i++)
							results[i] = new DefaultMutableTreeNode(new SpeedBarUser((ArrayList<String>) users.get(i), 
							                                                         sbSite.getSite(), 
							                                                         sbSite.getSiteCode()));
					}
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
				}
			}
		} catch (RuntimeException e) {
			log.error("Failed to get site users for SpeedBarSite: " + (sbSite != null ? sbSite.getSite() : "SpeedBarSite passed was null"), e);
		}
		return results;
	}

	/*
	 * (non-Javadoc) @param sbUser - User Node @return
	 * 
	 * Method to retrieve Tree Nodes for all Notebook IDs for a given User
	 */
	private DefaultMutableTreeNode[] getUserNotebooks(SpeedBarUser sbUser) {
		DefaultMutableTreeNode[] results = null; // new
		// DefaultMutableTreeNode[0];
		try {
			if (init()) {
				// Send Request
				ArrayList<ArrayList<String>> tempNotebooks = getNotebooksForUser(sbUser.getSiteCode(), sbUser.getUserID());
				
				// Create a node for each notebook
				if (tempNotebooks != null && tempNotebooks.size() > 0) {
	            	// Move the last modified Notebook to top of list for user only.
	            	if (sbUser.getUserID().equals(MasterController.getUser().getNTUserID()) && notebooks.size() > 1) {
		      			try {
			            	NotebookRef lastNbRef = null;
		      				String lastNotebook = MasterController.getUser().getPreference(NotebookUser.PREF_CurrentNbRef);
		    				try { lastNbRef = new NotebookRef(lastNotebook); } catch (Exception e) { }
		    				
		    				if (lastNbRef != null) {
		    					// Find Notebook in list and move it to beginning
		    					for (int i=0; i < notebooks.size(); i++) {
		    						ArrayList<String> list = notebooks.get(i);
			    					if (list.get(0).equals(lastNbRef.getNbNumber())) {
			    						notebooks.remove(i);
			    						notebooks.add(0, list);
			    						break;
			    					}
		    					}
		    				}
		    			} catch (Exception e) { 
		    				/* Ignored */
		    			}
	            	}

					results = new DefaultMutableTreeNode[tempNotebooks.size()];
					for (int i = 0; i < tempNotebooks.size(); i++)
						results[i] = new DefaultMutableTreeNode(
								new SpeedBarNotebook(tempNotebooks.get(i), sbUser.getSite(), 
													 sbUser.getSiteCode(), sbUser.getUser(), sbUser.getUserID()));
				}
			}
		} catch (RuntimeException e) {
			log.error("Failed retrieving user notebooks for SpeedBarUser: " + (sbUser == null ? "SpeedBarUser was null." : sbUser.getUserID()), e);
		}
		return results;
	}

	/*
	 * (non-Javadoc) @param sbNotebook - Notebook Node @return
	 * 
	 * Method to retrieve Tree Nodes for all Groups for a given Notebook. Note that it is assumed that pages are used in each page
	 * group from start to end. If this is not the case some page groups may be expanded where there are no experiments which is
	 * likely for migrated experiments/pages
	 */
	private DefaultMutableTreeNode[] getNotebookExperimentGroups(SpeedBarNotebook sbNotebook) throws Exception {
		final int groupSize = NotebookPageUtil.NB_PAGE_GROUP_SIZE;
		DefaultMutableTreeNode[] results = null;
		try {
			if (sbNotebook.getNotebook() == null || sbNotebook.getMinExperiment() == null || sbNotebook.getMaxExperiment() == null) {
				throw new Exception("Could not interpret Notebook for site=" + sbNotebook.getSiteCode() + ", user="
						+ sbNotebook.getUserID());
			} else {
				// get the start & end experiment/page #'s for this Notebook
				int startPage = new Integer(sbNotebook.getMinExperiment()).intValue();
				int stopPage = new Integer(sbNotebook.getMaxExperiment()).intValue();
				if (startPage == 0)
					startPage = 1;
				// Calculate the start experiment/page # for the first and last
				// group
				int firstGroup = ((startPage - 1) / groupSize) * groupSize + 1;
				int lastGroup = ((stopPage - 1) / groupSize) * groupSize + 1;
				// Calculate the # of groups
				int size = ((lastGroup - firstGroup) / groupSize) + 1;
				results = new DefaultMutableTreeNode[size + 1]; // +1 for the
				// contents node
				// Create the Contents node
				results[0] = new DefaultMutableTreeNode(new SpeedBarContents(sbNotebook.getSite(), sbNotebook.getSiteCode(), sbNotebook
						.getUser(), sbNotebook.getUserID(), sbNotebook.getNotebook()), false);
				// Create the Nodes for all the groups (if any) - Create in
				// descending order
				if (size > 0) {
					int i = 1;
					int grp = lastGroup;
					for (; grp >= firstGroup; i++, grp -= groupSize) {
						results[i] = new DefaultMutableTreeNode(new SpeedBarPageGroup(sbNotebook.getSite(), sbNotebook.getSiteCode(),
								sbNotebook.getUser(), sbNotebook.getUserID(), sbNotebook.getNotebook(), grp, Math.min(grp + groupSize
										- 1, NotebookPageUtil.NB_MAX_PAGE_NUMBER)));
					}
				}
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/*
	 * (non-Javadoc) @param sbPageGrp - Group Node @return
	 * 
	 * Method to retrieve Tree Nodes for all Experiments/Pages for a given Group Some groups may be empty depending how
	 * experiments/pages where migrated
	 */
	private DefaultMutableTreeNode[] getExperiments(SpeedBarPageGroup sbPageGrp) {
		DefaultMutableTreeNode[] results = null;
		if (init()) {
			// Create request for a list of Experiments/Pages within a given
			// Group
			SpeedBarContext sbCtx = new SpeedBarContext();
			sbCtx.setSiteCode(sbPageGrp.getSiteCode());
			sbCtx.setUserName(sbPageGrp.getUserID());
			sbCtx.setNotebook(sbPageGrp.getNotebook());
			sbCtx.setExperimentRange(sbPageGrp.getStartPage(), sbPageGrp.getStopPage());
			sbCtx.setIncludeOlderVersions(sbPageGrp.getUserID().equals(MasterController.getUser().getNTUserID())
					|| MasterController.getUser().isSuperUser());
			try {
				// Send Request
				StorageVO vo = ((SpeedBarContext) StorageOBJ.retrieveData(sbCtx, MasterController.getUser().getSessionIdentifier())).getResults();
				// Convert response to an array list of users
				ArrayList exp = null;
				if (vo != null)
					exp = vo.toArrayList();
				// Create a node for each experiment plus a table of contents
				// node
				if (exp != null && exp.size() > 0) {
					results = new DefaultMutableTreeNode[exp.size() + 1]; // +1
					// for contents node
					// Create Table of Contents Node for this group
					results[0] = new DefaultMutableTreeNode(new SpeedBarContents(sbPageGrp.getSite(), sbPageGrp.getSiteCode(),
							sbPageGrp.getUser(), sbPageGrp.getUserID(), sbCtx.getNotebook(), sbPageGrp.getStartPage(), sbPageGrp
							.getStopPage()), false);
					// Create Experiment/Page node
					for (int i = 0; i < exp.size(); i++)
						results[i + 1] = new DefaultMutableTreeNode(new SpeedBarPage((ArrayList) exp.get(i), sbPageGrp.getSite(),
								sbPageGrp.getSiteCode(), sbPageGrp.getUser(), sbPageGrp.getUserID(), sbCtx.getNotebook()), false);
				}
			} catch (Exception e) {
				log.info(e);
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
		return results;
	}

	/**
	 * @param path -
	 *            Tree Path for the path to expand
	 * @return
	 * 
	 * Method to expand a path immediately, i.e. not in the background. Call completes after path is expanded 1 level
	 */
	public boolean expandPathImmediate(TreePath path) {
		try {
			if (path != null) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
				expandNodeImmediate(node);
				return true;
			} else
				return false;
		} catch (RuntimeException e) {
			log.error("Oops. Can't expand immediately.", e);
		}
		return false;
	}

	/**
	 * @param node - Speedbar Node to expand
	 * @return
	 * 
	 * Method to expand a node immediately, i.e not in the background. Call completes after node is expanded 1 level
	 */
	public boolean expandNodeImmediate(DefaultMutableTreeNode node) {
		boolean result = false;
		// Check to see if this node has already been expanded
		if (node.getUserObject() instanceof SpeedBarExpandable && !((SpeedBarExpandable) node.getUserObject()).isExpandable())
			return true;
		try {
			// Create the children for this node
			DefaultMutableTreeNode[] children = createChildren(node);
			// Add the nodes children into the Tree (if any)
			if (children != null)
				ProcessNodeChildren(node, children, -1, false);
			result = true;
		} catch (Exception e) {
			log.info("Failed to expand node immediately", e);
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		return result;
	}

	/**
	 * Method to expand the Root node for this Speed Bar Model. Node is expanded immediately in that call completes after root is
	 * expanded 1 level
	 */
	public void expandRoot() {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getRoot();
		expandNodeImmediate(rootNode);
	}

	/**
	 * @param node -
	 *            Node to expand
	 * 
	 * Default method to expand a node. Expansion occurs in the background so processing returns back to caller immediately while
	 * node expands in background
	 */
	public void expandNode(DefaultMutableTreeNode node, JTree tree) {
		expandNode(node, tree, false);
	}

	private void expandNode(DefaultMutableTreeNode node, JTree tree, boolean autoExpanding) {
		// Check to see if this node has already been expanded
		if (node.getUserObject() instanceof SpeedBarExpandable && !((SpeedBarExpandable) node.getUserObject()).isExpandable())
			return;
		// Start a worker thread to exand the node
		startWorker(node, tree, autoExpanding);
	}

	/**
	 * @param parentNode -
	 *            Parent Node to add new node
	 * @param node -
	 *            New node to add
	 * @param idx -
	 *            Where in list of children to add new node
	 */
	public void addNode(DefaultMutableTreeNode parentNode, DefaultMutableTreeNode node, int idx) {
		DefaultMutableTreeNode nodeList[] = new DefaultMutableTreeNode[1];
		nodeList[0] = node;
		ProcessNodeChildren(parentNode, nodeList, idx, false);
	}

	/*
	 * (non-Javadoc) @param parentNode - Node to add children to @param children - Children to add to Parent @param addNodesAfterIdx -
	 * Index to start adding (-1 means append to end) @param removePendingNode - Flag indicating pending node should be deleted
	 * 
	 * Method to add list of children to the parent node. Idea here is to have one point where nodes are added/removed from tree.
	 */
	
	private synchronized void ProcessNodeChildrenInUIThread(final DefaultMutableTreeNode parentNode, final DefaultMutableTreeNode[] children,
			final int addNodesAfterIdx, final boolean removePendingNode) 
	{			
		Runnable doWorkRunnable = new Runnable() {
	    	public void run() {
	    		ProcessNodeChildren(parentNode, children, addNodesAfterIdx, removePendingNode);
	    	}
		};
		SwingUtilities.invokeLater(doWorkRunnable);
	}
	
	private void ProcessNodeChildren(final DefaultMutableTreeNode parentNode, DefaultMutableTreeNode[] children,
			int addNodesAfterIdx, boolean removePendingNode) {
		if (debug && children != null) {
			StringBuffer buff = new StringBuffer();
			for (int i=0; i<children.length; i++) {
				DefaultMutableTreeNode node = children[i];
				buff.append(node.getUserObject().toString()).append("  ");
			}
			//log.info("In processNodeChildren  - children: " + buff.toString());
			//log.info("removePandingNode is " + removePendingNode);
			//log.info("addNodesAfterIdx is " + addNodesAfterIdx);
			
		}
		// Remove the dummy Pending element
		if (removePendingNode && parentNode.getChildCount() > 0) {  
		    parentNode.remove(0);
		}
		if (debug) {
			//log.info("processNodeChildren processing children");
		}
		if (children != null && children.length > 0) {
			// Add the children to the Parent
			if (addNodesAfterIdx == -1)
				try {
					for (int i = 0; i < children.length; i++) {
						parentNode.add(children[i]);
					}
				} catch (Exception e) {
					System.err.println(parentNode.toString() + " " + parentNode.getAllowsChildren());
				}
			else
				for (int i = 0; i < children.length; i++, addNodesAfterIdx++) {
					// bug fix 24652,24172 Speedbar failure to show
					// notebook.
					if (addNodesAfterIdx < parentNode.getChildCount())
						parentNode.insert(children[i], addNodesAfterIdx);
					else
						parentNode.add(children[i]);
				}
			// Mark the parent as expanded
			if (parentNode.getUserObject() instanceof SpeedBarExpandable)
				((SpeedBarExpandable) parentNode.getUserObject()).setExpanded();
		}
		
		// Notify Model that the Speed bar has changed structure
		try {
			//if (debug) {
				//log.info("processNodeChildren calling nodeStructureChanged");
			//}
//			System.out.println("ProcessNodeChildren: Num Children = " + ((children == null) ? 0 : children.length));
//			System.out.println("ProcessNodeChildren: Parent Num Children = " + parentNode.getChildCount());
			nodeStructureChanged(parentNode);
		} catch (RuntimeException e) {
			log.info(e);
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * @param node -
	 *            Node to expand in the background
	 * 
	 * Method to start worker thread to expand a node
	 */
	protected void startWorker(final DefaultMutableTreeNode node, final JTree tree, final boolean autoExpanding) {
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				try {
					// Add a pending node so user knows that node is expanding
					DefaultMutableTreeNode pendingNode[] = new DefaultMutableTreeNode[1];
					pendingNode[0] = new DefaultMutableTreeNode("Pending...", false);
					ProcessNodeChildrenInUIThread(node, pendingNode, -1, false);
					// Create children for the expanded node.
					return createChildren(node);
				} catch (Exception e) {
					return e;
				}
			}

			public void finished() {
				try {
					// Get the children created by the factory and insert
					// them into the local tree model.
					ProcessNodeChildrenInUIThread(node, (DefaultMutableTreeNode[]) get(), -1, true);
				} catch (Exception ex) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, ex);
				}
				
				Runnable doWorkRunnable = new Runnable() {
			    	public void run() {
						// auto-expand first notebook when openning a user
						Object usrObj = node.getUserObject();
						if (usrObj instanceof SpeedBarUser && node.getChildCount() > 0) {
							// This will expand the First Notebook Object to show
							// all Page Groups
							DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode) node.getChildAt(0); // first Notebook
							TreePath path2;
							if (nextNode.getChildCount() > 1) // Are there any pages,
								// idx 0 is contents
								path2 = getPath(nextNode.getChildAt(1));
							else
								path2 = getPath(nextNode);
							tree.scrollPathToVisible(path2);
							expandNode(nextNode, tree, true);
						}
						if (autoExpanding && usrObj instanceof SpeedBarNotebook && node.getChildCount() > 1) { // Are there any page
							// groups, idx 0 is
							// contents
							// This will expand the First Page Group to display the
							// largest pages
							DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode) node.getChildAt(1); // first page group
							TreePath path2;
							if (nextNode.getChildCount() > 0) // Are there any pages,
								// idx 0 is contents
								path2 = getPath(nextNode.getChildAt(0));
							else
								path2 = getPath(nextNode);
							tree.scrollPathToVisible(path2);
							expandNode(nextNode, tree, true);
						}
						if (autoExpanding && usrObj instanceof SpeedBarPageGroup && tree != null) {
							TreePath path2;
							if (node.getChildCount() > 1) // Are there any pages,
								// idx 0 is contents
								path2 = getPath(node.getChildAt(1));
							else
								path2 = getPath(node);
							tree.scrollPathToVisible(path2);
						}
			    	}
				};
				SwingUtilities.invokeLater(doWorkRunnable);

				synchronized (workerList) {
					// Set the worker to null and stop the animation, but
					// only if we are the active worker.
					workerList.remove(this);
					// Stop the progress bar if this is the last worker
					if (workerList.size() == 0)
						MasterController.getGUIComponent().stopProgressBar();
				}
			}
		};
		synchronized (workerList) {
			// Start Progress bar if this is the first worker that is in
			// progress
			if (workerList.size() == 0)
				MasterController.getGUIComponent().startProgressBar();
			// Start worker, update status line
			workerList.add(worker);
		}
		worker.start();
	}

	private TreePath getPath(TreeNode node) {
		List<TreeNode> list = new ArrayList<TreeNode>();
		// Add all nodes to list
		while (node != null) {
			list.add(node);
			node = node.getParent();
		}
		Collections.reverse(list);
		// Convert array of nodes to TreePath
		return new TreePath(list.toArray());
	}

	/**
	 * Method to stop all worker threads
	 */
	protected void stopWorkers() {
		synchronized (workerList) {
			while (workerList.size() > 0) {
				SwingWorker worker = (SwingWorker) workerList.get(0);
				worker.interrupt();
				workerList.remove(worker);
			}
			MasterController.getGUIComponent().stopProgressBar();
		}
	}
	
	
	private DefaultMutableTreeNode[] getCROIds() {
		DefaultMutableTreeNode[] results = null; // new
		if (init()) {  // vb 2/1
			try {

				ArrayList<CROPageInfo> crolist = (ArrayList<CROPageInfo>) StorageOBJ.getAllCROs(MasterController.getUser().getSessionIdentifier());

				// Create a node for each notebook
				if (crolist != null && crolist.size() > 0) {
					results = new DefaultMutableTreeNode[crolist.size()];
					for (int i = 0; i < crolist.size(); i++){
						//System.out.print("**"+crolist.get(i));
						results[i] = new DefaultMutableTreeNode(crolist.get(i));
					}
				}
			} catch (Exception e) {
				log.info("Failed fetching CRO ids", e);
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
		return results;
	}
	
	private DefaultMutableTreeNode[] getAllNoteBooksUnderChemist(CROChemistInfo croChemistInfo) {
		DefaultMutableTreeNode[] results = null; // new

		try {
			List<String> croNoteBooks = StorageOBJ.getAllNotebooksForChemistId(croChemistInfo.getCroChemistID(),MasterController.getUser().getSessionIdentifier());

			// Create a node for each notebook
			if (croNoteBooks != null && croNoteBooks.size() > 0) {
				
				
				results = new DefaultMutableTreeNode[croNoteBooks.size()];
				for (int i = 0; i < croNoteBooks.size(); i++){
					//System.out.print("**"+crolist.get(i));
					String notebookid = croNoteBooks.get(i);
			
					CRONotebook sbNotebook = new CRONotebook(notebookid);
					
					results[i] = new DefaultMutableTreeNode(sbNotebook);
				}
			}
		} catch (Exception e) {
			log.info("Failed to get notebooks for chemist", e);
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}

		return results;
		
	}
	
	private DefaultMutableTreeNode[] getAllChemistsUnderCRO(CROPageInfo croPageInfo) {
		DefaultMutableTreeNode[] results = null; // new

		try {

			List<CROChemistInfo> chemists = StorageOBJ.getAllChemistsUnderCRO(croPageInfo.getCroID(),MasterController.getUser().getSessionIdentifier());

			// Create a node for each notebook
			if (chemists != null && chemists.size() > 0) {
				
				
				results = new DefaultMutableTreeNode[chemists.size()];
				for (int i = 0; i < chemists.size(); i++){
					//System.out.print("**"+crolist.get(i));
					CROChemistInfo chemist = chemists.get(i);
					results[i] = new DefaultMutableTreeNode(chemist);
				}
			}
		} catch (Exception e) {
			log.info("Failed to fetch all chemists under a CRO.", e);
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}

		return results;
		
	}
	private DefaultMutableTreeNode[] getAllPagesForNotebook(CRONotebook croNotebook) {
		DefaultMutableTreeNode[] results = null; // new
		if (init()) { // vb 2/1
			try {

				List<NotebookPageModel> croNoteBookPages = StorageOBJ.getAllPagesForNotebook(croNotebook.getNotebookNumber(),MasterController.getUser().getSessionIdentifier());

				// Create a node for each notebook
				if (croNoteBookPages != null && croNoteBookPages.size() > 0) {
					results = new DefaultMutableTreeNode[croNoteBookPages.size()];
					for (int i = 0; i < croNoteBookPages.size(); i++){
						//System.out.print("**"+crolist.get(i));
						NotebookPageModel pageModel = croNoteBookPages.get(i);

						CRONotebookPage croNotebookPage = new CRONotebookPage(pageModel);

						results[i] = new DefaultMutableTreeNode(croNotebookPage);
					}
				}
			} catch (Exception e) {
				log.info("Failed getting pages for notebook: " + 
				         (croNotebook == null ? "CRONotebook object was null." : croNotebook.getNotebookNumber()), e);
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
		return results;
		
	}
	/*
	private DefaultMutableTreeNode[] getAllChemistsUnderCRO(CROPageInfo croPageInfo) {
		DefaultMutableTreeNode[] results = null; // new

		try {

			ArrayList croChemistlist = StorageOBJ.getAllChemistsUnderCRO(croPageInfo.getCroID());

			// Create a node for each notebook
			if (croChemistlist != null && croChemistlist.size() > 0) {
				results = new DefaultMutableTreeNode[croChemistlist.size()];
				for (int i = 0; i < croChemistlist.size(); i++){
					//System.out.print("**"+crolist.get(i));
					results[i] = new DefaultMutableTreeNode((CROChemistInfo) croChemistlist.get(i));
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}

		return results;
	}
	
	private DefaultMutableTreeNode[] getAllRequestIdsForChemist (CROChemistInfo croChemistInfo) {
		DefaultMutableTreeNode[] results = null; // new
		

		try {

			List requestIds = StorageOBJ.getAllRequestIdsForChemist(croChemistInfo.getCroChemistID());

			// Create a node for each notebook
			if (requestIds != null && requestIds.size() > 0) {
				results = new DefaultMutableTreeNode[requestIds.size()];
				for (int i = 0; i < requestIds.size(); i++){
					//System.out.print("**"+crolist.get(i));
					
					//CRORequestInfo mCRORequestInfo = new CRORequestInfo();
					//mCRORequestInfo.setRequestId((CRORequestInfo) requestIds.get(i));
					results[i] = new DefaultMutableTreeNode((CRORequestInfo) requestIds.get(i));
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}

		return results;
	}
	
	
	
	private DefaultMutableTreeNode[] getAllPagesWithSummaryForRequestId (String requestId) {
		DefaultMutableTreeNode[] results = null; // new
		

		try {

			List pages = StorageOBJ.getAllPagesWithSummaryForRequestId(requestId);

			// Create a node for each notebook
			if (pages != null && pages.size() > 0) {
				results = new DefaultMutableTreeNode[pages.size()];
				for (int i = 0; i < pages.size(); i++){
					//System.out.print("**"+crolist.get(i));
					results[i] = new DefaultMutableTreeNode((NotebookPageModel) pages.get(i));
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}

		return results;
	}
	*/
	
	public void refreshNotebooksForUser(String siteCode, String fullName) {
		notebooks = null;
		getNotebooksForUser(siteCode, fullName);
	}
	
	public ArrayList<ArrayList<String>> getNotebooksForUser(String siteCode, String ntID) {
		if (init()) {
			// Create request for a list of notebook IDs for a given user at
			// a given site
			SpeedBarContext sbCtx = new SpeedBarContext();
			sbCtx.setSiteCode(siteCode);
			sbCtx.setUserName(ntID);
			try {
				// Send Request
				StorageVO vo = ((SpeedBarContext) StorageOBJ.retrieveData(sbCtx,MasterController.getUser().getSessionIdentifier())).getResults();
				// Convert response to an array list of users
				if (vo != null)
					notebooks = vo.toArrayList();
				// Create a node for each notebook
			} catch (Exception e) {
				log.info("Failed getting notebooks for user: " + ntID, e);
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}

		return notebooks;
	}
}
