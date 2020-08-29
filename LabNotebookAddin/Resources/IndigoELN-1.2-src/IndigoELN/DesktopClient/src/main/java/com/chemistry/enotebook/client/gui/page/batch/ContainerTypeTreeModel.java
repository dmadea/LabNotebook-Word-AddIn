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
package com.chemistry.enotebook.client.gui.page.batch;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.service.container.ContainerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;

public class ContainerTypeTreeModel 
	extends DefaultTreeModel // implements PlatesCreatedEventListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6919380942673319534L;

	private static Log log = LogFactory.getLog(ContainerTypeTreeModel.class);

	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Note Book Page");
	private DefaultMutableTreeNode myroot = new DefaultMutableTreeNode("My Containers");
	private DefaultMutableTreeNode compoundManagementRoot = new DefaultMutableTreeNode("Compound Management Container Types");
	
	// private DefaultMutableTreeNode monomerPlateRoot = new DefaultMutableTreeNode("Monomer Plates");
	// private DefaultMutableTreeNode productPlateRoot = new DefaultMutableTreeNode("Product Plates");
	// private DefaultMutableTreeNode registeredPlateRoot = new DefaultMutableTreeNode("Registered Plates");
	
	private NotebookPageModel mNotebookPageModel;
	private ContainerService mContainerService = null;
//	private List stepMonomerPlateNotes = new ArrayList(5);
//	private List stepProductPlateNotes = new ArrayList(5);

	public ContainerTypeTreeModel(NotebookPageModel notebookPageModel) {
		super(new DefaultMutableTreeNode());
		mNotebookPageModel = notebookPageModel;
		try {
			mContainerService = ServiceController.getContainerService(MasterController.getUser().getSessionIdentifier());
		} catch (Exception e) {
			log.error("Failed to acquire access to Container Service (StorageDelegate).", e);
		}
		setRoot(root);
		root.add(myroot);
		root.add(compoundManagementRoot);
		// root.add(monomerPlateRoot);
		// root.add(productPlateRoot);
		// root.add(registeredPlateRoot);
		construct();
	}

	private void construct() {
		ArrayList<Container> mContainers = new ArrayList<Container>();
		try {
			mContainers = (ArrayList<Container>) mContainerService.getUserContainers(MasterController.getUser().getNTUserID());
			for (int i = 0; i < mContainers.size(); i++) {
				Container tempContainerType = mContainers.get(i);
				if (!tempContainerType.isUserDefined()) {
					DefaultMutableTreeNode ctNode = new DefaultMutableTreeNode(tempContainerType);
					compoundManagementRoot.add(ctNode);
				} else {
					DefaultMutableTreeNode ctNode = new DefaultMutableTreeNode(tempContainerType);
					myroot.add(ctNode);
				}
			}
		} catch (Exception ex) {
			log.error(ex);
		}

/*		  ArrayList regPlates = mNotebookPageModel.getRegisteredPlates();
		for (int i = 0; i < regPlates.size(); i++) {
			DefaultMutableTreeNode regPlateNode = new DefaultMutableTreeNode(regPlates.get(i));
			registeredPlateRoot.add(regPlateNode);
		}
		ArrayList steps = mNotebookPageModel.getReactionSteps();
		for (int numberSteps = 0; numberSteps < steps.size(); numberSteps++) {
			String title = (numberSteps == 0) ? "SUMMARY REACTION" : "STEP " + numberSteps;
			DefaultMutableTreeNode stepNode = new DefaultMutableTreeNode(title);
			stepMonomerPlateNotes.add(stepNode);
			DefaultMutableTreeNode stepNode1 = new DefaultMutableTreeNode(title);
			stepProductPlateNotes.add(stepNode1);
			monomerPlateRoot.add(stepNode);
			productPlateRoot.add(stepNode1);
			ReactionStepModel rxnstep = (ReactionStepModel) steps.get(numberSteps);
			ArrayList monomerPlates = rxnstep.getMonomerPlates();
			for (int i = 0; i < monomerPlates.size(); i++) {
				DefaultMutableTreeNode monomerPlateNode = new DefaultMutableTreeNode(monomerPlates.get(i));
				stepNode.add(monomerPlateNode);
			}
			ArrayList productPlates = rxnstep.getProductPlates();
			for (int i = 0; i < productPlates.size(); i++) {
				DefaultMutableTreeNode prodPlateNode = new DefaultMutableTreeNode(productPlates.get(i));
				stepNode1.add(prodPlateNode);
			}
		}
*/		 
	}

	public boolean isLeaf(Object node) {
		DefaultMutableTreeNode mnode = (DefaultMutableTreeNode) node;
		Object value = mnode.getUserObject();
		if (value instanceof Container) {
			return true;
		} else
			return false;
	}

	public void addToMyList(Object[] ct) {
		for (int i = 0; i < ct.length; i++) {
			DefaultMutableTreeNode ctNode = new DefaultMutableTreeNode(ct[i]);
			Container cts = (Container) ct[i];
			if (!cts.isUserDefined()) {
				this.insertNodeInto(ctNode, compoundManagementRoot, compoundManagementRoot.getChildCount());
			} else {
				this.insertNodeInto(ctNode, myroot, myroot.getChildCount());
			}
		}
	}

	public boolean addToMyList(Object ct) {
		DefaultMutableTreeNode ctNode = new DefaultMutableTreeNode(ct);
		DefaultMutableTreeNode rootNode = null;
		Container cts = (Container) ct;
		if (cts.isUserDefined())
			rootNode = myroot;
		else
			rootNode = compoundManagementRoot;

		for (int i = 0; i < rootNode.getChildCount(); i++) {
			if (rootNode.getChildAt(i).toString().equalsIgnoreCase(ctNode.toString())) {
				JOptionPane.showOptionDialog(MasterController.getGUIComponent(), "Container must have a unique name to define.",
						"Invalid container name", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
				return false;
			}
		}
		this.insertNodeInto(ctNode, rootNode, rootNode.getChildCount());
		return true;
	}

	public void removeContainer(Object obj) {
		// DefaultMutableTreeNode ctNode = new DefaultMutableTreeNode(obj);
		DefaultMutableTreeNode rootNode = null;
		Container cts = (Container) obj;
		if (cts.isUserDefined())
			rootNode = myroot;
		else
			rootNode = compoundManagementRoot;

		int childCount = rootNode.getChildCount();
		for (int i = 0; i < childCount; i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode.getChildAt(i);
			if (node.getUserObject().toString().equalsIgnoreCase(obj.toString())) {
				this.removeNodeFromParent(node);
				return;
			}
		}
		/*
		 * childCount = myroot.getChildCount(); 
		 * for (int i = 0; i < childCount; i++) { 
		 * 		DefaultMutableTreeNode node = (DefaultMutableTreeNode) myroot.getChildAt(i); 
		 * 		if (node.getUserObject().equals(obj)) { // to do
		 * 			this.removeNodeFromParent(node); 
		 * 			return; 
		 * 		} 
		 * }
		 */
	}

	public ContainerService getContainerService() {
		return mContainerService;
	}
		
/*	public void newProductPlatesCreated(ProductBatchPlateCreatedEvent event) {
		int stepIndex = event.getStepIndex();
		List plates = event.getPlates();
		for (int i = 0; i < plates.size(); i++) {
			DefaultMutableTreeNode newPlateNode = new DefaultMutableTreeNode(plates.get(i));
			DefaultMutableTreeNode pararentNode = (DefaultMutableTreeNode) stepProductPlateNotes.get(stepIndex);
			this.insertNodeInto(newPlateNode, pararentNode, pararentNode.getChildCount());
		}
	}

	public void prodcutPlatesRemoved(ProductBatchPlateCreatedEvent event) {
	}

	public void newMonomerPlatesCreated(MonomerBatchPlateCreatedEvent event) {
		int stepIndex = event.getStepIndex();
		List plates = event.getPlates();
		for (int i = 0; i < plates.size(); i++) {
			DefaultMutableTreeNode newPlateNode = new DefaultMutableTreeNode(plates.get(i));
			DefaultMutableTreeNode pararentNode = (DefaultMutableTreeNode) stepMonomerPlateNotes.get(stepIndex);
			this.insertNodeInto(newPlateNode, pararentNode, pararentNode.getChildCount());
		}
	}

	public void monomerPlatesRemoved(MonomerBatchPlateCreatedEvent event) {
	}

	public void newRegisteredPlatesCreated(RegisteredPlateCreatedEvent event) {
	}

	public void egisteredPlatesRemoved(RegisteredPlateCreatedEvent event) {
	}
*/	 
}
