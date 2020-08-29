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

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.page.batch.ContainerTypeTreeModel;
import com.chemistry.enotebook.client.gui.page.batch.events.ContainerAddedEvent;
import com.chemistry.enotebook.client.gui.page.batch.events.ContainerAddedEventListener;
import com.chemistry.enotebook.client.gui.page.experiment.CompoundManagementMonomerContainer.RegistrationPlateCreateDialog;
import com.chemistry.enotebook.domain.MonomerPlate;
import com.chemistry.enotebook.domain.ReactionStepModel;
import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.service.container.ContainerService;
import com.chemistry.enotebook.utils.PPopupMenu;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.*;
import java.util.*;

public class ContainerTypeExPTree extends JTree implements MouseListener, ContainerAddedEventListener, TreeSelectionListener {
	
	private static final long serialVersionUID = -8384802549537559728L;
	
	private ContainerTypeTreeModel mContainerTypeTreeModel;
	private ContainerService containerService;
	private Container selectedContainerType;
	// private JFrame parent;
	private PPopupMenu compoundManagementPopMenu = new PPopupMenu(); // generic molecule
	// collections
	//private JMenuItem loadCompoundManagementOrderItem = new JMenuItem("Load Compound Management Order");
	private JMenuItem searchCompoundManagementItem = new JMenuItem("Search Container types");
	private JMenuItem defineOwnCTItem = new JMenuItem("Define Custom Container");
	// private JMenuItem selectCompoundManagementItem = new JMenuItem("Add Compound Management Container");
	private PPopupMenu manipulateMyCTListPopMenu = new PPopupMenu();
	private JMenuItem removeCTItem = new JMenuItem("Delete Container Type");
	//private JMenuItem createProductPlateItem = new JMenuItem("Create Product Plate ");
	private JMenu createMonomerPlate = new JMenu("Create Monomer Plate");
	private JMenuItem createMonomerPlateFromExcelItem = new JMenuItem("From Excel Spreadsheet");
	private JMenuItem createMonomerPlateFromDesignItem = new JMenuItem("From Design Service Design");
	private JMenuItem createMonomerPlateFromOtherOrder = new JMenuItem("From Other Order");
	private JMenu createProductPlate = new JMenu("Create Product Plate");
	private JMenuItem createProductPlateFromDesignItem = new JMenuItem("From Design Service Design");
	private JMenu createProductPlateFromMonomerPlateMenu = new JMenu("From Monomer Plate");
	private JMenuItem createProductPlateFromProductPlateMenu = new JMenuItem("From Product Plate");
	
	// vb 1/5
	private ReactionStepModel stepModel;
	
	/*
	private PPopupMenu monomerPlatePopMenu = new PPopupMenu();
	private JMenuItem openMonomerPlateItem = new JMenuItem("Open Monomer Plate");
	private PPopupMenu productPlatePopMenu = new PPopupMenu();
	private JMenuItem openProductPlateItem = new JMenuItem("Open Product Plate");
	private PPopupMenu registeredPlatePopMenu = new PPopupMenu();
	private JMenuItem openRegisteredPlateItem = new JMenuItem("Open Registered Plate");
	*/
	private PlateCreateInterface selectedPlateCreateInterface;
	private List mPlateCreateInterfaces = new ArrayList();
	private ContainerTreeCellRederer mContainerTreeCellRederer = new ContainerTreeCellRederer();
	private boolean isEditable;

	public ContainerTypeExPTree(ContainerTypeTreeModel containerTypeTreeModel, ReactionStepModel stepModel, boolean isEditable) {
		super(containerTypeTreeModel);
		this.stepModel = stepModel; 
		this.setRootVisible(false);
		this.isEditable = isEditable;
		mContainerTypeTreeModel = containerTypeTreeModel;
		// mPlateCreateInterface = plateCreateInterface;
		containerService = mContainerTypeTreeModel.getContainerService();
		// allCompoundManagementCTs = new JList(mContainerTypeController.getAllContainerTypeList().toArray());
		// allCompoundManagementCTs = new JList(ServiceController.getContainerService().getAllContainers());
		//compoundManagementPopMenu.add(loadCompoundManagementOrderItem);
/*		loadCompoundManagementOrderItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadCompoundManagementOrderItem_actionPerformed();
			}
		});
*/
		compoundManagementPopMenu.add(searchCompoundManagementItem);
		searchCompoundManagementItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchCompoundManagementItemActionPerformed();
			}
		});
		/*
		 * compoundManagementPopMenu.add(selectCompoundManagementItem); 
		 * selectCompoundManagementItem.addActionListener(new java.awt.event.ActionListener() { 
		 * 		public void actionPerformed(ActionEvent e) { //selectCompoundManagementItem__actionPerformed(); } }
		 * );
		 */
		compoundManagementPopMenu.add(defineOwnCTItem);
		defineOwnCTItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				defineOwnCTItem_actionPerformed();
			}
		});
		/*
		monomerPlatePopMenu.add(openMonomerPlateItem);
		openMonomerPlateItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openMonomerPlateItem_actionPerformed();
			}
		});
		productPlatePopMenu.add(openProductPlateItem);
		openProductPlateItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openProductPlateItem_actionPerformed();
			}
		});
		registeredPlatePopMenu.add(openRegisteredPlateItem);
		openRegisteredPlateItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openRegisteredPlateItem_actionPerformed();
			}
		});
		*/
		manipulateMyCTListPopMenu.add(createMonomerPlate);
		createMonomerPlate.add(createMonomerPlateFromExcelItem);
		createMonomerPlate.add(createMonomerPlateFromDesignItem);
		createMonomerPlate.add(createMonomerPlateFromOtherOrder);
		createMonomerPlateFromExcelItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createMonomerPlateFromExcelItem_actionPerformed();
			}
		});
		createMonomerPlateFromDesignItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createMonomerPlateFromDesignItemActionPerformed();
			}
		});
		createMonomerPlateFromOtherOrder.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createMonomerPlateFromOtherOrderActionPerformed();
			}
		});		
		
		manipulateMyCTListPopMenu.add(createProductPlate);
		createProductPlate.add(createProductPlateFromDesignItem);
		createProductPlate.add(createProductPlateFromMonomerPlateMenu);
		createProductPlate.add(createProductPlateFromProductPlateMenu);

		createProductPlateFromDesignItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createProductPlateFromDesignItemActionPerformed();
			}
		});

		createProductPlateFromMonomerPlateMenu.addMouseListener(new MouseAdapter() {
//			public void actionPerformed(ActionEvent e) {
//				createMonomerPlateMenuItems();
//				//createProductPlateFromMonomerPlateItemActionPerformed();
//			}

			public void mouseEntered(MouseEvent e) {
				createMonomerPlateMenuItems();
			}
		});

		createProductPlateFromProductPlateMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createProductPlateFromProductPlateItemActionPerformed();
			}
		});
//		manipulateMyCTListPopMenu.add(createProductPlateItem);
//		createProductPlateItem.addActionListener(new java.awt.event.ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// createPlateCTItem_actionPerformed();
//				createProductPlateItemActionPerformed();
//			}
//		});
		manipulateMyCTListPopMenu.add(removeCTItem);
		removeCTItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeCTItem_actionPerformed();
			}
		});
		this.addMouseListener(this);
		this.addTreeSelectionListener(this);
		ToolTipManager.sharedInstance().registerComponent(this);
		setCellRenderer(mContainerTreeCellRederer);
		expandTree(this, (DefaultMutableTreeNode)containerTypeTreeModel.getRoot());
	}
	
	/**
	 * @param tree com.sun.java.swing.JTree
	 * @param start com.sun.java.swing.tree.DefaultMutableTreeNode
	 */
	private static void expandTree(JTree tree, DefaultMutableTreeNode start) {
		for (Enumeration children = start.children(); children.hasMoreElements();) {
			DefaultMutableTreeNode dtm = (DefaultMutableTreeNode) children.nextElement();
			if (!dtm.isLeaf()) {
				//
				TreePath tp = new TreePath( dtm.getPath() );
				tree.expandPath(tp);
				//
				expandTree(tree, dtm);
			}
		}
		return; 
	}
	
	private void createMonomerPlateMenuItems() {
		createProductPlateFromMonomerPlateMenu.removeAll();
		List monomerPlates = stepModel.getMonomerPlates();
		Iterator it = monomerPlates.iterator();
		while (it.hasNext()){
			MonomerPlate mplate = (MonomerPlate) it.next();
			JMenuItem plateItem = new JMenuItem(mplate.getPlateBarCode());
			final String plateBarcode = mplate.getPlateBarCode();
			createProductPlateFromMonomerPlateMenu.add(plateItem);
			plateItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(ActionEvent e) {
					createProductPlateFromMonomerPlateItemActionPerformed(plateBarcode);
				}
			});			
		}
	}

//	private void addPlateCreateInterface(PlateCreateInterface newPlateCreateInterface) {
//		mPlateCreateInterfaces.add(newPlateCreateInterface);
//	}

	private void removeCTItem_actionPerformed() {
		int confirmation = JOptionPane.showConfirmDialog(MasterController.getGUIComponent(), "Do you want to remove this container?", "Remove Container", JOptionPane.YES_NO_OPTION);
		if (confirmation == JOptionPane.NO_OPTION)
			return;
		try {
			containerService.removeContainer(selectedContainerType.getKey());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showOptionDialog(MasterController.getGUIComponent(), "Unexpected error ioccured while removing a container. Please try again later.", "Unexpected error", JOptionPane.DEFAULT_OPTION , JOptionPane.ERROR_MESSAGE, null, null, null);
			return;
		}
		mContainerTypeTreeModel.removeContainer(selectedContainerType);
		selectedContainerType = null;  // vb 5/12
		//
	}

//	private void openMonomerPlateItem_actionPerformed() {
//	}
//
//	private void openProductPlateItem_actionPerformed() {
//	}
//
//	private void openRegisteredPlateItem_actionPerformed() {
//	}

//	private void loadCompoundManagementOrderItem_actionPerformed() {
//		selectedPlateCreateInterface.loadCompoundManagementOrderOptions();
//	}

	private void createMonomerPlateFromExcelItem_actionPerformed() {
		selectedPlateCreateInterface.loadOrderNoContainerFromFile(selectedContainerType);
	}

	private void defineOwnCTItem_actionPerformed() {
		selectedPlateCreateInterface.defineCustomerContainter();
	}

	private void searchCompoundManagementItemActionPerformed() {
		selectedPlateCreateInterface.searchContainerTypes();
	}

	private void createMonomerPlateFromDesignItemActionPerformed() {
		selectedPlateCreateInterface.createMonomerPlateFromSynthesisPlan(selectedContainerType);
	}

	private void createMonomerPlateFromOtherOrderActionPerformed() {
		selectedPlateCreateInterface.createMonomerPlateFromOtherOrder(selectedContainerType);
	}
	
	private void createProductPlateFromDesignItemActionPerformed() {
		selectedPlateCreateInterface.createProductPlateFromSynthesisPlan(selectedContainerType);
	}

	private void createProductPlateFromMonomerPlateItemActionPerformed(String plateNumber) {
		selectedPlateCreateInterface.createProductPlateFromMonomerPlate(selectedContainerType, plateNumber);
	}

	private void createProductPlateFromProductPlateItemActionPerformed() {
		//selectedPlateCreateInterface.createProductPlateFromProductPlate(selectedContainerType, plateNumber);
		RegistrationPlateCreateDialog plateCreateByPlate = ((CompoundManagementMonomerContainer)selectedPlateCreateInterface).new RegistrationPlateCreateDialog(MasterController.getGUIComponent(), selectedContainerType);
		plateCreateByPlate.setVisible(true);		
	}
	
	/*
	 * private void createMonomerPlateFromExcelItem_actionPerformed() { }
	 * 
	 * private void createMonomerPlateFromDesignItem_actionPerformed() { }
	 */
	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) { // vb 5/23
		int x = e.getX();
		int y = e.getY();
		TreePath path = getPathForLocation(x, y);
		if (path != null) {
			setSelectionPath(path);
			Object com = path.getLastPathComponent();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) com;
			Object userOject = node.getUserObject();
			if (userOject instanceof Container) {
				if (selectedPlateCreateInterface != null)
					this.selectedPlateCreateInterface.setSelectedContainer((Container) userOject);

				//Create the Sibling list in order to load in the Pop-up combo box.
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)path.getParentPath().getLastPathComponent();
				Vector sibilingList = new Vector(parentNode.getChildCount());
				for (int i=0;i<parentNode.getChildCount();i++)
					sibilingList.add(parentNode.getChildAt(i));
				this.selectedPlateCreateInterface.setSelectedContainerList(sibilingList);
			} 
			else
			{
				this.selectedPlateCreateInterface.setSelectedContainer(null);
				this.selectedPlateCreateInterface.setSelectedContainerList(null);
			}
		}		
	}

	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			int x = e.getX();
			int y = e.getY();
			TreePath path = getPathForLocation(x, y);
			if (path != null) {
				setSelectionPath(path);
				Object com = path.getLastPathComponent();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) com;
				Object userOject = node.getUserObject();
				if (userOject instanceof Container) {
					selectedContainerType = (Container) userOject;
					//if (selectedContainerType.getContainerType().equals("Plate") || selectedContainerType.getContainerType().equals("Rack"))
					//{
						this.selectedPlateCreateInterface.setSelectedContainer(selectedContainerType);
						if (isEditable)
							manipulateMyCTListPopMenu.show(e.getComponent(), x, y);
						DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)path.getParentPath().getLastPathComponent();
						Vector sibilingList = new Vector(parentNode.getChildCount());
						for (int i=0;i<parentNode.getChildCount();i++)
							sibilingList.add(parentNode.getChildAt(i));
						this.selectedPlateCreateInterface.setSelectedContainerList(sibilingList);
						List monomerPlates = stepModel.getMonomerPlates();
						if (monomerPlates.size() == 0)
							createProductPlateFromMonomerPlateMenu.setEnabled(false);
						else
							createProductPlateFromMonomerPlateMenu.setEnabled(true);
					//}
				} 
				
				/*
				 else if (userOject instanceof MonomerPlate) {
					monomerPlatePopMenu.show(e.getComponent(), x, y);
				} else if (userOject instanceof ProductPlate) {
					productPlatePopMenu.show(e.getComponent(), x, y);
				} else if (userOject instanceof RegisteredPlate) {
					registeredPlatePopMenu.show(e.getComponent(), x, y);
				}*/
			} else {// clicked on white space
				if (isEditable)
					compoundManagementPopMenu.show(e.getComponent(), x, y);
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public String getToolTipText(MouseEvent ev) {
		if (ev == null)
			return null;
		TreePath path = getPathForLocation(ev.getX(), ev.getY());
		if (path != null) {
			Object com = path.getLastPathComponent();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) com;
			Object userOject = node.getUserObject();
			if (userOject instanceof Container) {
				Container ct = (Container) userOject;
				String tooltip = "<html><p>Container: " + ((ct.isUserDefined()) ? "" : (ct.getContainerCode() + " : " )) + ct.getContainerName() + "(" + ct.getYPositions() + ", "
						+ ct.getXPositions() + ")</p></html>";
				return tooltip;
			} else
				return null;
		}
		return null;
	}

	public void newContainerAdded(ContainerAddedEvent eve) {
		Container newContainer = eve.getContainer();
		if (mContainerTypeTreeModel.addToMyList(newContainer))
		{
			try {
				containerService.createContainer(newContainer);
			} catch (Exception e) {
				e.printStackTrace();
				mContainerTypeTreeModel.removeContainer(newContainer);
				JOptionPane.showOptionDialog(MasterController.getGUIComponent(), "Unexpected error occured while adding the container. Please try again later.", "Unexpected error", JOptionPane.DEFAULT_OPTION , JOptionPane.ERROR_MESSAGE, null, null, null);
				return;
			}
		}
	}

	public static void main(String[] arg) {
		/*
		 * JFrame testFrame = new JFrame();
		 * 
		 * java.awt.Container cp = testFrame.getContentPane(); cp.setLayout(new BorderLayout()); ContainerTypeTreeModel
		 * mContainerTypeTreeModel = new ContainerTypeTreeModel(); ContainerTypeExPTree mContainerTypeTree = new
		 * ContainerTypeExPTree( mContainerTypeTreeModel); JScrollPane scroller = new JScrollPane(mContainerTypeTree);
		 * cp.add(scroller, BorderLayout.CENTER); testFrame.pack(); testFrame.show();
		 */
	}

	public ContainerTypeTreeModel getMContainerTypeTreeModel() {
		return mContainerTypeTreeModel;
	}

	public PlateCreateInterface getSelectedPlateCreateInterface() {
		return selectedPlateCreateInterface;
	}

	public void setSelectedPlateCreateInterface(PlateCreateInterface plateCreateInterface) {
		selectedPlateCreateInterface = plateCreateInterface;
	}

	public void valueChanged(TreeSelectionEvent event) {
		TreePath path = event.getPath();
		if (path != null) {
			setSelectionPath(path);
			Object com = path.getLastPathComponent();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) com;
			Object userOject = node.getUserObject();
			if (userOject instanceof Container) {
				if (selectedPlateCreateInterface != null)
					this.selectedPlateCreateInterface.setSelectedContainer((Container) userOject);

				//Create the Sibling list in order to load in the Pop-up combo box.
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)path.getParentPath().getLastPathComponent();
				Vector sibilingList = new Vector(parentNode.getChildCount());
				for (int i=0;i<parentNode.getChildCount();i++)
					sibilingList.add(parentNode.getChildAt(i));
				this.selectedPlateCreateInterface.setSelectedContainerList(sibilingList);
			} 
			else
			{
				this.selectedPlateCreateInterface.setSelectedContainer(null);
				this.selectedPlateCreateInterface.setSelectedContainerList(null);
			}
		}
	}
}
