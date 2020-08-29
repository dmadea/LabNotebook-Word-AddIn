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
package com.chemistry.enotebook.client.gui.page.analytical.singleton;

import com.chemistry.enotebook.analyticalservice.delegate.AnalyticalServiceDelegate;
import com.chemistry.enotebook.analyticalservice.exceptions.AnalyticalServiceAccessException;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.gui.page.analytical.JDialogViewerQuery;
import com.chemistry.enotebook.experiment.datamodel.analytical.AbstractAnalysis;
import com.chemistry.enotebook.experiment.datamodel.analytical.Analysis;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.servicelocator.PropertyException;
import com.chemistry.enotebook.utils.CeNJobProgressHandler;
import com.chemistry.enotebook.utils.SwingWorker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If
 * Jigloo is being used commercially (ie, by a corporation, company or business for any purpose whatever) then you should purchase a
 * license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of Jigloo implies acceptance of these
 * licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class PurificationServiceDataJFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9085753222651956535L;
	private JPanel jPanelTree;
	private Analysis selectedAnalysis = null;
	private JTree tree = null;
	private Rectangle rect = new Rectangle(0, 0);
	private JPopupMenu rendererOptionsPopup = null;
	private JPopupMenu jPopupMenuSaveFile = null;

	private ArrayList preModel = new ArrayList();
	private ArrayList postModel = new ArrayList();

	public PurificationServiceDataJFrame(String cenSampleRef, ArrayList model) {
		super();
		
		initGUI();
		
		for (Iterator it=model.iterator(); it.hasNext(); ) {
			AbstractAnalysis a = (AbstractAnalysis)it.next();
			
			if (cenSampleRef != null && a.getAnalyticalServiceSampleRef().equals(cenSampleRef))
				preModel.add(a);
			else
				postModel.add(a);
		}
		
		if (preModel.size() > 0 && postModel.size() > 0)
			setTitle("PurificationService Pre-QC and Post-QC Analytical Data for " + cenSampleRef);
		else if (preModel.size() == 0 && postModel.size() > 0)
			setTitle("PurificationService Post-QC Analytical Data for " + cenSampleRef);
		else if (preModel.size() > 0 && postModel.size() == 0)
			setTitle("PurificationService Pre-QC Analytical Data for " + cenSampleRef);
		else
			setTitle("PurificationService Analytical Data for " + cenSampleRef);

		buildAnalyticalTree();
	}

	private void initGUI() {
		try {
			setIconImage(CenIconFactory.getImage(CenIconFactory.General.APPLICATION));
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setSize(740, 550);
			setLocation(10, 10);
			
			jPanelTree = new JPanel();
			jPanelTree.setLayout(new BorderLayout());
			jPanelTree.setVisible(true);
			jPanelTree.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
			getContentPane().add(jPanelTree, BorderLayout.CENTER);
			
			rendererOptionsPopup = new JPopupMenu();
			JMenuItem viewFileItem = new JMenuItem("View File");
			viewFileItem.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent evt) {
					handleViewFileAction(evt);
				}
			});
			rendererOptionsPopup.add(viewFileItem);
			
			JMenuItem jMenuItemSaveFile = new JMenuItem("Save File");
			rendererOptionsPopup.add(jMenuItemSaveFile);
			jMenuItemSaveFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					saveFile();
				}
			});
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, "Error occurred while creating PurificationService data report", e);
		}
	}
	
	private ArrayList getDistinctInstrumentTypes(ArrayList list) {
		ArrayList result = new ArrayList();
		Hashtable h  = new Hashtable();

		for (Iterator it=list.iterator(); it.hasNext(); ) {
			Analysis analysis = (Analysis) it.next();
			
			if (analysis.getInstrumentType() == null) 
				analysis.setInstrumentType("Unknown");

			if (h.put(analysis.getInstrumentType(), analysis.getInstrumentType()) == null)
				result.add(analysis.getInstrumentType());
		}
		
		return result;
	}
	
	private ArrayList getAnaltyicalForInstrumentType(ArrayList list, String insType) {
		ArrayList result = new ArrayList();
		
		for (Iterator it=list.iterator(); it.hasNext(); ) {
			Analysis analysis = (Analysis) it.next();
			
			if (analysis.getInstrumentType().equals(insType))
				result.add(analysis);
		}
		
		return result;
	}

	private void buildAnalyticalTree() {
		DefaultMutableTreeNode rootNode = AnalyticalSampleReferencesTreeModel.createRootNode();
		AnalyticalSampleReferencesTreeModel treeModel = new AnalyticalSampleReferencesTreeModel(rootNode);		
		tree = new JTree(treeModel);
		ToolTipManager.sharedInstance().registerComponent(tree);
		tree.setCellRenderer(new AnalyticalTreeCellRenderer(false, true));
		
		if (preModel.size() > 0) {
			DefaultMutableTreeNode preParentNode = new DefaultMutableTreeNode("PurificationService Pre-QC");
			rootNode.add(preParentNode);
			
			ArrayList insTypes = getDistinctInstrumentTypes(preModel);
			for (int i=0; i < insTypes.size(); i++) {
				DefaultMutableTreeNode nodeInsType = new DefaultMutableTreeNode(insTypes.get(i).toString());
				preParentNode.add(nodeInsType);
				
				ArrayList analyticalList = getAnaltyicalForInstrumentType(preModel, insTypes.get(i).toString());
				for (int j=0; j < analyticalList.size(); j++) {
					DefaultMutableTreeNode leafNode = new DefaultMutableTreeNode(analyticalList.get(j));
					nodeInsType.add(leafNode);
				}
			}
		}
		
		if (postModel.size() > 0) {
			DefaultMutableTreeNode postParentNode = new DefaultMutableTreeNode("PurificationService Post-QC");
			rootNode.add(postParentNode);
			
			ArrayList insTypes = getDistinctInstrumentTypes(postModel);
			for (int i=0; i < insTypes.size(); i++) {
				DefaultMutableTreeNode nodeInsType = new DefaultMutableTreeNode(insTypes.get(i).toString());
				postParentNode.add(nodeInsType);
				
				ArrayList analyticalList = getAnaltyicalForInstrumentType(postModel, insTypes.get(i).toString());
				for (int j=0; j < analyticalList.size(); j++) {
					DefaultMutableTreeNode leafNode = new DefaultMutableTreeNode(analyticalList.get(j));
					nodeInsType.add(leafNode);
				}
			}
		}

		JScrollPane sPane = new JScrollPane(tree);
		sPane.setVisible(true);
		tree.setRowHeight(0);
		treeModel.reload();
		expandAll(tree, true);
		int height = (int) jPanelTree.getHeight();
		sPane.setBounds(new java.awt.Rectangle(10, 10, 700, height - 10));
		jPanelTree.add(sPane);
			
		// use the following listener to find the selected Node(Analysis)
		// This method also gives the selected Node's bounds, i.e. rectangle;
		// Use the above Rectangle in locating the ViewFile and Unlink Coordinates
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent evt) {
				TreePath[] paths = evt.getPaths();
				for (int i = 0; i < paths.length; i++) {
					if (evt.isAddedPath(i)) {
						Object o = tree.getLastSelectedPathComponent();
						if (o instanceof DefaultMutableTreeNode) {
							DefaultMutableTreeNode selected = (DefaultMutableTreeNode) o;
							
							if (selected.getUserObject() instanceof Analysis) {
								selectedAnalysis = (Analysis) ((DefaultMutableTreeNode) o).getUserObject();
								rect = tree.getPathBounds(paths[i]);
							}
						}
					} else {
						// This node has been deselected
					}
				}
			}
		});
		
		tree.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
					tree.setSelectionPath(selPath);
				}
				
				AnalyticalTreeCellRenderer renderer = (AnalyticalTreeCellRenderer)tree.getCellRenderer();
				JButton optionsBut = renderer.optionsButton;
				
				if (optionsBut != null) {
					// Options Button Coordinates
					int startXOpt = optionsBut.getX() + (int) rect.getX();
					int endXOpt = (optionsBut.getX()+ optionsBut.getBounds().width) + (int) rect.getX();
					int startYOpt = optionsBut.getY() + (int) rect.getY();
					int endYOpt = (optionsBut.getY()+ optionsBut.getBounds().height) + (int) rect.getY();
					
					// Options Button
					if (e.getPoint().getX() > startXOpt && e.getPoint().getX() < endXOpt
							&& e.getPoint().getY() > startYOpt && e.getPoint().getY() < endYOpt) {
						rendererOptionsPopup.show(e.getComponent(), startXOpt, endYOpt);
					}
				}
			}
		});
	}
	
	private void handleViewFileAction(MouseEvent evt){
//		if (evt.isPopupTrigger()) {
//			saveFile();
//			jPopupMenuSaveFile.show(tree, evt.getX(), evt.getY());			
//		} else {
			String app = "";
			if ((selectedAnalysis.getInstrumentType().equalsIgnoreCase("NMR") || selectedAnalysis.getInstrumentType().equalsIgnoreCase("LC-MS")) && selectedAnalysis.getFileType().equals("RAW")) {
				app = JDialogViewerQuery.ACD_LABS;
				String pathMestreNova = null;
				try { pathMestreNova = CeNSystemProperties.getMestreNovaNMRSoftware(); } catch (Exception e) { }
				if (pathMestreNova != null && pathMestreNova.length() > 0) {
					File dir = new File(pathMestreNova);
					if (dir != null && dir.exists()) {
						String answer = (new JDialogViewerQuery(MasterController.getGUIComponent(), selectedAnalysis.getInstrumentType())).getViewerChoice();
						if (answer == null) return;
						app = answer;
					}
				}
			}
			new ViewFileThread(jPanelTree, selectedAnalysis, app).start();
//		}
	}

	public void saveFile() {
		JFileChooser jfc = new JFileChooser();
		try {
			String sDir = MasterController.getUser().getPreference(NotebookUser.PREF_PATH_ANALYTICAL_SAVE);
			if (sDir != null && sDir.length() > 0)
				jfc.setCurrentDirectory(new File(sDir));
			else {
				sDir = MasterController.getUser().getPreference(NotebookUser.PREF_PATH_ANALYTICAL_UPLOAD);
				if (sDir != null && sDir.length() > 0)
					jfc.setCurrentDirectory(new File(sDir));
			}
		} catch (UserPreferenceException e1) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e1);
		}
		jfc.setSelectedFile(new File(AnalyticalServiceDelegate.windowsValidFileName(selectedAnalysis.getFileName())));
		jfc.setMultiSelectionEnabled(false);

		int result = jfc.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			final File saveFile = jfc.getSelectedFile();
			if (saveFile != null) {
				try {
					SwingWorker saveProcess = new SwingWorker() {
						public Object construct() {
							final String progressStatus = "Saving file " + saveFile + "...";
							CeNJobProgressHandler.getInstance().addItem( progressStatus);

							try {
								writeToDisk(saveFile);

								try {
									MasterController.getUser().setPreference(NotebookUser.PREF_PATH_ANALYTICAL_SAVE, saveFile.getAbsolutePath());
								} catch (UserPreferenceException e1) {
									CeNErrorHandler.getInstance().logExceptionMsg(null, e1);
								}
							} catch (Exception e) {
								CeNErrorHandler.getInstance().logExceptionMsg( null, e);
							} finally {// in case there is an exception and method finished is not reached
								CeNJobProgressHandler.getInstance().removeItem( progressStatus);
							}
							return null;
						}
					};
					saveProcess.start();

				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
					JOptionPane.showMessageDialog(this, "Error: Unable to save the file to disk",
							"File Write Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private void writeToDisk(final File saveFile)
		throws AnalyticalServiceAccessException,  PropertyException, IOException, FileNotFoundException 
	{
		AnalyticalServiceDelegate analyticalServiceDelegate = new AnalyticalServiceDelegate();
		byte[] fileContents = null;
		try {
			fileContents = analyticalServiceDelegate.retrieveFileContents(selectedAnalysis.getCyberLabDomainId(), selectedAnalysis.getCyberLabFileId(), selectedAnalysis.getSite());
		} catch (Throwable e) {
			if (e.getCause().toString().indexOf("Call to CyberLab API failed") > -1) {
				JOptionPane.showMessageDialog(null, "File not found in AnalyticalService/CyberLab or CyberLab API Failure.",
						"Error", JOptionPane.ERROR_MESSAGE);
				CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "Error occurred while performing SaveFile, Not Displayed to User");
			} else {
				CeNErrorHandler.getInstance().logExceptionMsg(null, "Error occurred while performing SaveFile", e);
			}
		}
		FileOutputStream out = new FileOutputStream(saveFile);
		out.write(fileContents);
		out.close();
	}

	private void expandAll(JTree tree, boolean expand) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandAll(tree, new TreePath(root), expand);
	}

	private void expandAll(JTree tree, TreePath parent, boolean expand) {
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}
		
		// Expansion or collapse must be done bottom-up
		if (expand)
			tree.expandPath(parent);
		else
			tree.collapsePath(parent);
	}
}