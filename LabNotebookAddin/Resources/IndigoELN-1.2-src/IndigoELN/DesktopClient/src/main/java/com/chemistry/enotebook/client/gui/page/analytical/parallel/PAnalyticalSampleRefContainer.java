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
package com.chemistry.enotebook.client.gui.page.analytical.parallel;

import com.chemistry.enotebook.analyticalservice.delegate.AnalyticalServiceDelegate;
import com.chemistry.enotebook.analyticalservice.exceptions.AnalyticalServiceAccessException;
import com.chemistry.enotebook.analyticalservice.exceptions.AnalyticalServiceException;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.gui.page.analytical.AnalyticalSampleReferencesTreeModel;
import com.chemistry.enotebook.client.gui.page.analytical.JDialogViewerQuery;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail.AnalyticalDetailTableView;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail.AnalyticalTreeCellRenderer;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionEvent;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionListener;
import com.chemistry.enotebook.client.gui.page.experiment.table.ParallelCeNUtilObject;
import com.chemistry.enotebook.domain.AnalysisModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.PlateWell;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.servicelocator.PropertyException;
import com.chemistry.enotebook.utils.CeNJobProgressHandler;
import com.chemistry.enotebook.utils.SSIZipLauncher;
import com.chemistry.enotebook.utils.SwingWorker;
import com.chemistry.enotebook.utils.Unzipper;
import info.clearthought.layout.TableLayout;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class PAnalyticalSampleRefContainer extends javax.swing.JPanel  implements BatchSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5937844641574392576L;
	private static final Log log = LogFactory.getLog(PAnalyticalSampleRefContainer.class);
	private static final CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	
	private NotebookPageModel pageModel = null;
	private ProductBatchModel productBatch = null;
	
	private JComboBox jComboBoxSampleRefs;
	private JLabel jLabelSampleRefs;
	private JPanel jPanelHeader;
	private JPanel jPanelTree;
	private JTree analyticalSampleReferencesTree_;
	private Rectangle rect = new Rectangle(0, 0);
	private JTextField annotation;
	private JPopupMenu jPopupMenuSaveFile;
	private JMenuItem jMenuItemSaveFile;
	private JPopupMenu optionsPopup = null;
	private JMenuItem unlinkAllItem = null;
	//private JMenu mapAllTo = null;
	//private XJPopupMenu mapAllToSubMenu = null;
	private JMenuItem uploadPDFItem = null;
	private JButton optionsButton = null;
	private JPopupMenu rendererOptionsPopup = null;
	private JMenuItem unlinkItem = null;
	//private JMenu mapToMenu = null;
	private JMenuItem mapToItem = null;
	private JMenuItem mapAllToItem = null;
	//private XJPopupMenu mapToSubMenu = null;
	private JMenuItem viewFileItem = null;
	//private NotebookPage nbPage;
	private AnalyticalSampleReferencesTreeModel analyticalSampleReferencesTreeModel_;
	private PAnalyticalUtility analyticalUtility;
	private AnalyticalDetailTableView summaryViewer;
	private AnalysisModel selectedAnalysis; // View File And UnLink
	private AnalysisModel analysisForAnnotation;
//	private boolean applyMapToAllLinks = false;
	private boolean isEditable = true;
	private boolean isClearingCombobox;
	private boolean isLoadingCombobox;
	private String selectedSampleRef = "";
	private List<AnalyticalDetailTableView> summaryViewers = new ArrayList<AnalyticalDetailTableView>();
//	private String sampleRefFrom = "";

	private ArrayList<AnalysisModel> modelList = new ArrayList<AnalysisModel>();
	
	public PAnalyticalSampleRefContainer() {
		initGUI();
	}

	/**
	 * There is one PAnalyticalSampleRefContainer and one PAnalyticalUtility, which is associated with it.
	 * @param pageModel
	 */
	public PAnalyticalSampleRefContainer(NotebookPageModel pageModel) {
		this.initGUI();
		this.pageModel = pageModel;
		this.isEditable = pageModel.isEditable();
		this.analyticalUtility = new PAnalyticalUtility(pageModel);
		this.analyticalUtility.setSampleRefContainer(this);
		this.fillAnalyticalSampleRefComboBox();
	}
	
	public PAnalyticalUtility getAnalyticalUtility() {
		return this.analyticalUtility;
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			double tableSize[][] = { { TableLayout.FILL }, { 30, 600 } };
			TableLayout layout = new TableLayout(tableSize);
			setLayout(layout);
			setSize(800, 600);
			jPanelHeader = new JPanel();
			jPanelHeader.setLayout(null);
			jPanelHeader.setVisible(true);
			jPanelHeader.setPreferredSize(new java.awt.Dimension(450, 20));
			jPanelHeader.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
			jLabelSampleRefs = new JLabel();
			jLabelSampleRefs.setText("Sample Reference: ");
			jLabelSampleRefs.setPreferredSize(new java.awt.Dimension(130, 20));
			jLabelSampleRefs.setBounds(new java.awt.Rectangle(10, 10, 130, 20));
			jPanelHeader.add(jLabelSampleRefs);
			jComboBoxSampleRefs = new JComboBox();
			jComboBoxSampleRefs.setPreferredSize(new java.awt.Dimension(160, 20));
			jComboBoxSampleRefs.setBounds(new java.awt.Rectangle(140, 10, 160, 20));
			jPanelHeader.add(jComboBoxSampleRefs);
			optionsPopup = new JPopupMenu();
			unlinkAllItem = new JMenuItem("Unlink All");
			unlinkAllItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					handleUnlinkAllAction(evt);
				}
			});
			mapAllToItem = new JMenuItem("Map All To");
	        mapAllToItem.addMouseListener(new MouseAdapter() {
	        	public void mousePressed(MouseEvent e) {
//	        		Component source = (Component) e.getSource();
// sampleRefForm was set but never used
//	        		if (jComboBoxSampleRefs.getSelectedItem() != null) {
//	        			sampleRefFrom = jComboBoxSampleRefs.getSelectedItem().toString();
//	        		}	  	        		
	        		displayMapToDialog();
	        		//mapToSubMenu.show(source, e.getX(), e.getY());
	        	}
	        });
			uploadPDFItem = new JMenuItem("Upload a PDF File to Analytical Information Service");
			uploadPDFItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					handleUploadPDFAction(evt);
				}
			});
			optionsPopup.add(unlinkAllItem);
			optionsPopup.add(mapAllToItem);
			optionsPopup.add(uploadPDFItem);
			optionsButton = new JButton("Options");
			optionsButton.setPreferredSize(new Dimension(90, 24));
			optionsButton.setMinimumSize(new Dimension(90, 24));
			optionsButton.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
					"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
			optionsButton.setIcon(CenIconFactory.getImageIcon(CenIconFactory.General.DROP_DOWN));
			optionsButton.setHorizontalAlignment(SwingConstants.LEFT);
			optionsButton.setFocusable(false);
			optionsButton.setBounds(new Rectangle(325, 10, 100, 20));
			jPanelHeader.add(optionsButton);
			optionsButton.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent evt) {
					if (pageModel.isEditable() && optionsButton.isEnabled()) {
						String batchNumber = (String) jComboBoxSampleRefs.getSelectedItem();
//						sampleRefFrom = batchNumber;
						if (analyticalUtility == null) {
							return; // vb 7/24 how is this set?
						}
						if (analyticalUtility.isAnalysisEmptyForBatch(batchNumber)) {
							unlinkAllItem.setEnabled(false);
						} else {
							unlinkAllItem.setEnabled(true);
						}
						optionsPopup.show(evt.getComponent(), 0, (evt.getComponent().getY() + evt.getComponent().getHeight() - 6));
					}
				}
			});
			add(jPanelHeader, "0,0");
			jPanelTree = new JPanel();
			jPanelTree.setLayout(null);
			jPanelTree.setVisible(true);
			jPanelTree.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
			add(jPanelTree, "0,1");
			annotation = new JTextField();
			jPopupMenuSaveFile = new JPopupMenu();
			jMenuItemSaveFile = new JMenuItem("Save File");
			jPopupMenuSaveFile.add(jMenuItemSaveFile);
			jMenuItemSaveFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					saveFile();
				}
			});
			rendererOptionsPopup = new JPopupMenu();
			unlinkItem = new JMenuItem("Unlink");
			unlinkItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					handleUnlinkAction(evt);
				}
			});
			rendererOptionsPopup.add(unlinkItem);
			mapToItem = new JMenuItem("Map To");
	        mapToItem.addMouseListener(new MouseAdapter() {
	        	public void mousePressed(MouseEvent e) {
//	        		Component source = (Component) e.getSource();
	        		displayMapToDialog();
	        		//mapToSubMenu.show(source, e.getX(), e.getY());
	        	}
	        });
	        rendererOptionsPopup.add(mapToItem);
			viewFileItem = new JMenuItem("View File");
			viewFileItem.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent evt) {
					handleViewFileAction(evt);
				}
			});
			rendererOptionsPopup.add(viewFileItem);
			jMenuItemSaveFile = new JMenuItem("Save File");
			jMenuItemSaveFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					saveFile();
				}
			});
			rendererOptionsPopup.add(jMenuItemSaveFile);
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	public void handleViewFileAction(MouseEvent evt) {
//		if (evt.isPopupTrigger()) {
//			jPopupMenuSaveFile.show(analyticalSampleReferencesTree_, evt.getX(), evt.getY());
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
			new ViewFileThread(this, selectedAnalysis, app).start();
//		}
	}

	public void handleUnlinkAction(ActionEvent evt) {
		if (isEditable) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			analyticalUtility.unLink(selectedAnalysis);
//			if (summaryViewer != null)
//				summaryViewer.reload();
			buildAnalyticalTree(selectedAnalysis.getCenSampleRef());
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			notifySummaryViewers();
		}
	}

	public void handleUnlinkAllAction(ActionEvent evt) {
		if (jComboBoxSampleRefs.getSelectedItem() != null) {
			int selection = JOptionPane.showConfirmDialog(null, "Are you sure you want to unlink all for the Sample Reference "
					+ jComboBoxSampleRefs.getSelectedItem() + "?", "UnLink All", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (selection == JOptionPane.YES_OPTION) {
				// Fixes an unlinkall problem for a sample
				performUnLinkAll(jComboBoxSampleRefs.getSelectedItem().toString());
				// performUnLinkAll(nbPage.getNotebookRefAsString());
				this.fillAnalyticalSampleRefComboBox();
				notifySummaryViewers();
			}
		}
	}

	public void handleUploadPDFAction(ActionEvent evt) {
		String sampleRefNumber = (String) jComboBoxSampleRefs.getSelectedItem();
		if (sampleRefNumber == null)
			sampleRefNumber = this.getSelectedSampleRef();
		UpLoadJDialog aJDialog = new UpLoadJDialog(sampleRefNumber, MasterController.getGUIComponent(), analyticalUtility);
		//aJDialog.setAnalyticalUtility(analyticalModel);
		aJDialog.setVisible(true);
	}

	/**
	 * @param string
	 */
	public void fillAnalyticalSampleRefComboBox() {
		isClearingCombobox = true;
		jComboBoxSampleRefs.removeAllItems();
		isClearingCombobox = false;
		if (pageModel == null || pageModel.getAnalysisCache() == null)
			return;
		// ArrayList al_sampleRefs =
		// nbPage.getAnalysisCache().getDistinctSampleRefes();
		isLoadingCombobox = true;
		// Kluge to avoid adding sampleRef more than once
				
		List<String> refs = new Vector<String>();
		
		// Add batches
		List<ProductBatchModel> allBatches = pageModel.getAllProductBatchModelsInThisPage();
		if (allBatches != null) {
			for (ProductBatchModel batchModel : allBatches) {
				if (batchModel != null) {
					String batchNumber = batchModel.getBatchNumberAsString();
					if (StringUtils.isNotBlank(batchNumber) && !refs.contains(batchNumber)) {
						refs.add(batchNumber);
					}
				}
			}
		}
		
		Collections.sort(refs);
		
		ArrayList<AnalysisModel> sampleRefs = pageModel.getAnalysisCache().getAnalyticalList();
		
		for (int i = 0; i < sampleRefs.size(); i++) {
			AnalysisModel analysisModel = sampleRefs.get(i);
			if (pageModel.getAnalysisCache().getAnalysisListForBatch(sampleRefs.get(i).getCenSampleRef()).size() < 1)
				continue;
			if (!refs.contains(analysisModel.getCenSampleRef())) {
				refs.add(analysisModel.getCenSampleRef());
			}
		}
		
		for (String ref : refs) {
			if (ref != null) {
				jComboBoxSampleRefs.addItem(ref);
			}
		}
		
		String selectedSamplRef = "";
		if (summaryViewer != null) {
			int s = summaryViewer.getSelectedRow();
			if (s > -1) {
				selectedSamplRef = summaryViewer.getValueAt(s, 0).toString();
				jComboBoxSampleRefs.setSelectedItem(selectedSamplRef);
			}
		}
		isLoadingCombobox = false;
		buildAnalyticalTree(selectedSamplRef);
	}

	/**
	 * @param string
	 */
	public void buildAnalyticalTree(final String sampleRef) {
		if (!(isClearingCombobox || isLoadingCombobox)) {
			jComboBoxSampleRefs.setSelectedItem(sampleRef);
			List<AnalysisModel> analyticalModelList = pageModel.getAnalysisCache().getAnalysisListForBatch(sampleRef);
			if (analyticalModelList.size() == 0) {
				optionsButton.setEnabled(false);
				jPanelTree.removeAll();
				jPanelTree.repaint();
				//jComboBoxSampleRefs.setSelectedItem(sampleRef);  vb 7/25 unclear about this option........
//			} else if (jComboBoxSampleRefs.getSelectedItem() == null
//					|| !jComboBoxSampleRefs.getSelectedItem().toString().equals(sampleRef)) {
//				optionsButton.setEnabled(false);
//				jPanelTree.removeAll();
//				jComboBoxSampleRefs.setSelectedItem(sampleRef);
			} else {
				//List analysisList = this.convertAnalysisModelsToAnalyses(analyticalModelList);
				optionsButton.setEnabled(pageModel.isEditable());
				unlinkItem.setEnabled(pageModel.isEditable());
				mapToItem.setEnabled(pageModel.isEditable());
				// SwingWorker buildTreeProcess = new SwingWorker() {
				String s = "Building Analytical Tree for " + sampleRef + " ...";
				// public Object construct() {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				CeNJobProgressHandler.getInstance().addItem(s);
				jPanelTree.removeAll();
				DefaultMutableTreeNode rootNode = AnalyticalSampleReferencesTreeModel.createRootNode();
				analyticalSampleReferencesTreeModel_ = new AnalyticalSampleReferencesTreeModel(rootNode);
				analyticalSampleReferencesTree_ = new JTree(analyticalSampleReferencesTreeModel_);
				ToolTipManager.sharedInstance().registerComponent(analyticalSampleReferencesTree_);
				ArrayList<String> insTypes = pageModel.getAnalysisCache().getDistinctInstrumentTypes(sampleRef);
				for (int i = 0; i < insTypes.size(); i++) {
					String insType = insTypes.get(i);
					DefaultMutableTreeNode nodeInsType = new DefaultMutableTreeNode(insTypes.get(i).toString());
					analyticalSampleReferencesTreeModel_.insertNodeInto(nodeInsType, rootNode, 0); //i);
					for (int j = 0; j < analyticalModelList.size(); j++) {
						AnalysisModel analysis = analyticalModelList.get(j);
						if (analysis.getInstrumentType().equalsIgnoreCase(insType)) {
							DefaultMutableTreeNode leafNode = new DefaultMutableTreeNode(analyticalModelList.get(j));
							analyticalSampleReferencesTreeModel_.insertNodeInto(leafNode, nodeInsType, 0);
							AnalyticalTreeCellRenderer renderer = null;
							if (modelList.contains(analysis)) {
								renderer = new AnalyticalTreeCellRenderer(isEditable, false, false);
							} else {
								renderer = new AnalyticalTreeCellRenderer(isEditable, false, true);
								modelList.add(analysis);
							}
							analyticalSampleReferencesTree_.setCellRenderer(renderer);
						}
					}
				}
				// use the following listener to find the selected
				// Node(Analysis)
				// This method also gives the selected Node's bounds, i.e.
				// rectangle;
				// Use the above Rectangle in locating the ViewFile and Unlink
				// Coordinates
				analyticalSampleReferencesTree_.addTreeSelectionListener(new TreeSelectionListener() {
					public void valueChanged(TreeSelectionEvent evt) {
						// updateAnnotation(selectedAnalysis,
						// annotation.getText());
						TreePath[] paths = evt.getPaths();
						for (int i = 0; i < paths.length; i++) {
							if (evt.isAddedPath(i)) {
								Object o = analyticalSampleReferencesTree_.getLastSelectedPathComponent();
								if (o instanceof DefaultMutableTreeNode) {
									DefaultMutableTreeNode selected = (DefaultMutableTreeNode) o;
									if (selected.getUserObject() instanceof AnalysisModel) {
										selectedAnalysis = (AnalysisModel) ((DefaultMutableTreeNode) o).getUserObject();
										// Selected Rectagle Bounds
										rect = analyticalSampleReferencesTree_.getPathBounds(paths[i]);
									}
								}
							} else {
								// This node has been deselected
							}
						}
					}
				});
				analyticalSampleReferencesTree_.addMouseListener(new MouseAdapter() {
					public void mouseReleased(MouseEvent e) {
						TreePath selPath = analyticalSampleReferencesTree_.getPathForLocation(e.getX(), e.getY());
						analyticalSampleReferencesTree_.setSelectionPath(selPath);
						AnalyticalTreeCellRenderer renderer = (AnalyticalTreeCellRenderer) analyticalSampleReferencesTree_.getCellRenderer();
						JButton optionsBtn = renderer.getOptionsButton();
						JCheckBox ipRelatedCheckBox = renderer.getCheckBoxIPRelated();
						JTextField annotationTextField = renderer.getTextFieldAnnotation();
						// IP Related CheckBox Coordinates
						int startXIP = ipRelatedCheckBox.getX() + (int) rect.getX();
						int endXIP = (ipRelatedCheckBox.getX() + ipRelatedCheckBox.getBounds().width) + (int) rect.getX();
						int startYIP = ipRelatedCheckBox.getY() + (int) rect.getY();
						int endYIP = (ipRelatedCheckBox.getY() + ipRelatedCheckBox.getBounds().height) + (int) rect.getY(); 
						// UnLink Button Coordinates
						int startXUnLink = optionsBtn.getX() + (int) rect.getX();
						int endXUnLink = (optionsBtn.getX() + optionsBtn.getBounds().width) + (int) rect.getX();
						int startYUnLink = optionsBtn.getY() + (int) rect.getY();
						int endYUnLink = (optionsBtn.getY() + optionsBtn.getBounds().height) + (int) rect.getY();
						// Annotation
						int startXAnnotation = annotationTextField.getX() + (int) rect.getX();
						int endXAnnotation = annotationTextField.getX() + annotationTextField.getBounds().width + (int) rect.getX();
						int startYAnnotation = annotationTextField.getY() + (int) rect.getY();
						int endYAnnotation = annotationTextField.getY() + annotationTextField.getBounds().height + (int) rect.getY();
						// IP Related CheckBox					
						if (e.getPoint().getX() > startXIP && e.getPoint().getX() < endXIP
								&& e.getPoint().getY() > startYIP && e.getPoint().getY() < endYIP) {
							if (pageModel.isEditable() && selectedAnalysis.getFileType().toLowerCase().equals("pdf")) {
								boolean selected = ipRelatedCheckBox.isSelected();
								ipRelatedCheckBox.setSelected(!selected);
								selectedAnalysis.setIPRelated(!selected);
								// side effect of setting IPRelated is that modified is true								
//								selectedAnalysis.setModified(true);
								pageModel.setModelChanged(true);
							}
							// FIXME: Not great workaround, but it needed to make 
							// IP Related checkbox click work in multi-row tables
							TreePath firstRowPath = analyticalSampleReferencesTree_.getPathForRow(0);
							analyticalSampleReferencesTree_.setSelectionPath(firstRowPath);
						}
						// UnLink Button
						else if (e.getPoint().getX() > startXUnLink && e.getPoint().getX() < endXUnLink
								&& e.getPoint().getY() > startYUnLink && e.getPoint().getY() < endYUnLink) {
//							if (!updateBatchListOnMenu(mapToItem, false)) {
//								rendererOptionsPopup.remove(mapToItem);
//							}
							rendererOptionsPopup.show(e.getComponent(), startXUnLink, endYUnLink);
						}
						// Annotation TextField, OnClick a textfield
						// Pops up in the same location
						// as textfield does not function inside a
						// treeCell Renderer, i.e a panel here
						else if (e.getPoint().getX() > startXAnnotation && e.getPoint().getX() < endXAnnotation
								&& e.getPoint().getY() > startYAnnotation && e.getPoint().getY() < endYAnnotation) {
							if (isEditable) {
								final JPopupMenu popupMenu = new JPopupMenu();
								popupMenu.setPreferredSize(new Dimension(annotationTextField.getBounds().width + 4, annotationTextField.getBounds().height + 4));
								popupMenu.setPopupSize(popupMenu.getPreferredSize());
								// Focus Lost
								annotation.addFocusListener(new FocusAdapter() {
									public void focusLost(FocusEvent e) {
										if (analysisForAnnotation != null) {
											updateAnnotation(analysisForAnnotation, annotation.getText());
										}
									}

									public void focusGained(FocusEvent e) {
										// A separate object is used to hold the right object to
										// update the annotation as focus lost event is
										// fired only after tree selection listener
										// as a result selected analysis is getting changed
										analysisForAnnotation = new AnalysisModel();
										analysisForAnnotation = selectedAnalysis;
									}
								});
								// When USer presses Enter
								annotation.addKeyListener(new java.awt.event.KeyAdapter() {
									public void keyTyped(KeyEvent e) {
										if (e.getKeyChar() == '\n') {
											if (analysisForAnnotation != null) {
												updateAnnotation(analysisForAnnotation, annotation.getText());
											}
										}
									}
								});
								annotation.setPreferredSize(annotationTextField.getSize());
								popupMenu.add(annotation);
								popupMenu.show(e.getComponent(), startXAnnotation - 2, startYAnnotation - 2);
								annotation.setText(selectedAnalysis.getAnnotation());
								annotation.requestFocus();
							}
						}
					}
				});
				JScrollPane sPane = new JScrollPane(analyticalSampleReferencesTree_);
				sPane.setVisible(true);
				analyticalSampleReferencesTree_.setRowHeight(0);
				analyticalSampleReferencesTreeModel_.reload();
				expandAll(analyticalSampleReferencesTree_, true);
				int height = (int) jPanelTree.getHeight();
				sPane.setBounds(new java.awt.Rectangle(10, 10, 700, height - 10));
				jPanelTree.add(sPane);
				CeNJobProgressHandler.getInstance().removeItem(s);
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}

//	private boolean updateBatchListOnMenu(JMenu menu, boolean applyToAllLinks) {
//		boolean isBatchListAvailable = false;
//		menu.removeAll();
//		List batchList = pageModel.getAllProductBatchModelsInThisPage();
//		//List batchList = nbPage.getBatchCache().getBatches(BatchType.ACTUAL_PRODUCT_ORDINAL);// 32= ACTUAL BatchType
//		// System.out.println("Actual Batches count : "+ batchList.size());
//		List tempBatchNameList = new ArrayList();
//		String selectedBatchNumber = (String) jComboBoxSampleRefs.getSelectedItem();
//		if (batchList.isEmpty() || !hasLinks(selectedBatchNumber))
//			return isBatchListAvailable;
//		//////////////////////// Collections.sort(batchList);
//		for (int i = 0; i < batchList.size(); i++) {
//			ProductBatchModel batchObj = (ProductBatchModel) batchList.get(i);
//			/////////AbstractBatch batchObj = (AbstractBatch) batchList.get(i);
//			String batchNumber = batchObj.getBatchNumber().getBatchNumber();
//			/////////String batchNumber = batchObj.getBatchNumberAsString();
//			if (batchNumber.trim().equals("") || batchNumber.equals(selectedBatchNumber))
//				continue;
//			tempBatchNameList.add(batchNumber);
//			JMenuItem batchItem = new JMenuItem(batchNumber);
//			batchItem.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent evt) {
//					handleMapToBatch(evt);
//				}
//			});
//			menu.add(batchItem);
//			isBatchListAvailable = true;
//		}
//		// Following code handles Black batches Analysis.
//		if (selectedAnalysis != null && !applyToAllLinks) {
//			// Following check will ensure that if sample reference is not
//			// available then
//			// sample reference is available in menu for mapping.
//			if (!selectedAnalysis.getAnalyticalServiceSampleRef().equals(selectedBatchNumber)) {
//				if (!tempBatchNameList.contains(selectedAnalysis.getAnalyticalServiceSampleRef())) {
//					JMenuItem batchItem = new JMenuItem(selectedAnalysis.getAnalyticalServiceSampleRef());
//					batchItem.addActionListener(new ActionListener() {
//						public void actionPerformed(ActionEvent evt) {
//							handleMapToBatch(evt);
//						}
//					});
//					menu.add(new JSeparator());
//					menu.add(batchItem);
//					isBatchListAvailable = true;
//				}
//			}
//		}
//		applyMapToAllLinks = applyToAllLinks;
//		return isBatchListAvailable;
//	}
//
//	private void handleMapToBatch(ActionEvent evt) {
//		if (!isEditable)
//			return;
//		String batchNumber = ((XCheckedButton) evt.getSource()).getText();
//		String sourceBatchNumber = (String) jComboBoxSampleRefs.getSelectedItem();
//		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//		if (applyMapToAllLinks) {
//			analyticalUtility.performMap(sourceBatchNumber, batchNumber);
//			summaryViewer.reload();
//			if (selectedAnalysis != null) {
//				buildAnalyticalTree(selectedAnalysis.getCenSampleRef());
//			}
//		} else {
//			analyticalUtility.performAnalysisMap(sourceBatchNumber, batchNumber, selectedAnalysis);
//			notifySummaryViewers();
//		}
//		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//	}
//
	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		jComboBoxSampleRefs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (jComboBoxSampleRefs.getSelectedItem() != null)
					buildAnalyticalTree(jComboBoxSampleRefs.getSelectedItem().toString());
			}
		});
		enableComponents();
	}

/*	public void setPageModel(NotebookPageModel nbPage) {
		this.pageModel = nbPage;
		if (nbPage != null) {
			isEditable = nbPage.isEditable();
		} else {
			analyticalUtility.setNotebookPage(null);
		}
		if (!isEditable) {
			rendererOptionsPopup.remove(unlinkItem);
			rendererOptionsPopup.remove(mapToItem);
		}
		optionsButton.setEnabled(isEditable);
	}

	public void setSummaryTableViewer(AnalyticalSummaryViewer summaryViewer) {
		this.summaryViewer = summaryViewer;
	}

	public void setAnalyticalModel(PAnalyticalUtility analyticalModel) {
		this.analyticalModel = analyticalModel;
	}*/

	public void expandAll(JTree tree, boolean expand) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandAll(tree, new TreePath(root), expand);
	}

	private void expandAll(JTree tree, TreePath parent, boolean expand) {
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration<TreeNode> e = node.children(); e.hasMoreElements();) {
				TreeNode n = e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}
		// Expansion or collapse must be done bottom-up
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}

	public void updateAnnotation(AnalysisModel analysis, String annotationText) {
		if (annotationText != null && isEditable) {
			if (!analysis.getAnnotation().equals(annotationText)) {
				analysis.setAnnotation(annotationText);
				analysis.setModified(true);
				MasterController.getGUIComponent().enableSaveButtons();
			}
		}
	}
	
	
	class ViewFileThread extends Thread 
	{
		private JPanel parent = null;
		private AnalysisModel selectedAnalysis = null;
	    private String appType = null;

		public ViewFileThread(JPanel parent, AnalysisModel selectedAnalysis, String appType) {
			this.parent = parent;		
			this.selectedAnalysis = selectedAnalysis;
			this.appType = appType;
		}

		/**
		 * 
		 * @param query
		 * @param sites
		 */
		private void viewFile(AnalysisModel a) {
			File tempDir = null;
			String tempFileName = null;
			StringBuffer tempDirName = new StringBuffer();
//			Unzipper unzipper = new Unzipper();
			
			// int progIdx = -1;
			String progressStatus = "Retrieving file from AnalyticalService...";
			try {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				CeNJobProgressHandler.getInstance().addItem(progressStatus);

				AnalyticalServiceDelegate analyticalServiceDelegate = new AnalyticalServiceDelegate();
				log.info("Retrieving file from AnalyticalService: " + a.getAnalyticalServiceSampleRef() + " " + a.getCyberLabFileId() + " '" + a.getFileName() + "'");
				byte[] fileContents = analyticalServiceDelegate.retrieveFileContents(a.getCyberLabDomainId(), a.getCyberLabFileId(), a.getSite());
				if (fileContents != null && fileContents.length > 0) {
					CeNJobProgressHandler.getInstance().removeItem(progressStatus);

					progressStatus = "Creating temp location...";
					CeNJobProgressHandler.getInstance().addItem(progressStatus);
					String fileName = AnalyticalServiceDelegate.windowsValidFileName(a.getFileName());
					fileName = fileName.replace('|', '\\');		// replace pipe symbol with slash, this is found is ssizip files
					
					File f = new File(fileName);
					fileName = f.getName();
					fileName = fileName.replace(' ', '_');		// blanks could cause problems
					
					tempDir = File.createTempFile("analyticalService", null);
					tempDirName.append(tempDir.getPath());
					tempDir.delete(); 							// remove file so we can make a directory
					tempDir = new File(tempDirName.toString());
					tempDir.mkdir();
					tempDir.deleteOnExit();
					tempFileName = tempDirName + File.separator + fileName;
					
					// get file name from analysis object
					FileOutputStream fos = new FileOutputStream(tempFileName);
					fos.write(fileContents);
					fos.close();
					
					CeNJobProgressHandler.getInstance().removeItem(progressStatus);
					//if acd and raw nmr or raw lc need to unpack.  mestre only needs lc unpacked
					if ((a.getInstrumentType().equalsIgnoreCase("NMR") || a.getInstrumentType().equalsIgnoreCase("LC-MS")) && 
						appType.equals(JDialogViewerQuery.ACD_LABS) && a.getFileType().equals("RAW")) {
						progressStatus = "Unzipping to temp location...";
						CeNJobProgressHandler.getInstance().addItem(progressStatus);
						(new Unzipper()).unzip(tempFileName, tempDir.getPath());
						CeNJobProgressHandler.getInstance().removeItem(progressStatus);
					} else if (a.getInstrumentType().equalsIgnoreCase("LC-MS") && appType.equals(JDialogViewerQuery.MESTRE_NOVA) && a.getFileType().equals("RAW")) {
						progressStatus = "Unzipping to temp location...";
						CeNJobProgressHandler.getInstance().addItem(progressStatus);
						(new Unzipper()).unzip(tempFileName, tempDir.getPath());
						CeNJobProgressHandler.getInstance().removeItem(progressStatus);
					}
					parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
					// Launch File
					progressStatus = "Launching from temp location...";
					CeNJobProgressHandler.getInstance().addItem(progressStatus);
					if (fileName.endsWith("SSIZip") && a.getInstrumentType().equalsIgnoreCase("NMR")) {
						Process p = null;
						if (appType.equals(JDialogViewerQuery.ACD_LABS)) {
							String launchFile = SSIZipLauncher.findNMRLaunchFile(tempDirName.toString());
							if (launchFile == null) {
								JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
										"Viewer cannot be launched since SSIZip/zip file does not contain a fid, ser or .als file.",
										"Error", JOptionPane.ERROR_MESSAGE);
							} else {
								p = Runtime.getRuntime().exec("cmd /c \"" + CeNSystemProperties.getACDNMRViewerFullyQualifiedPath() + "\" /SP" + launchFile);
								
								// Log the metric of # of users who use ACD to view NMRs
								CeNErrorHandler.getInstance().sendMetricsLogToServer("VIEW NMR ACD", MasterController.getUser().getNTUserID(), MasterController.getUser().getSiteCode());
								//p.waitFor(); // This only waits for the process to be kicked off. Not for the app to open.
							}
						} else {
							p = Runtime.getRuntime().exec("cmd /c \"" + CeNSystemProperties.getMestreNovaNMRSoftware() + "\" " + tempFileName);
							
							// Log the metric of # of users who use ACD to view NMRs
							CeNErrorHandler.getInstance().sendMetricsLogToServer("VIEW NMR MN", MasterController.getUser().getNTUserID(), MasterController.getUser().getSiteCode());
							//p.waitFor(); // This only waits for the process to be kicked off. Not for the app to open.
						}
					} else if (fileName.endsWith("SSIZip") && a.getInstrumentType().equalsIgnoreCase("LC-MS")) {
						Process p = null;
						if (appType.equals(JDialogViewerQuery.ACD_LABS)) {
							String launchFile = SSIZipLauncher.findLCMSLaunchFile(tempDirName.toString());
							if (launchFile == null) {
								JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
										"Viewer cannot be launched since SSIZip/zip file does not contain a _HEADER.TXT/MSD1.MS file.",
										"Error", JOptionPane.ERROR_MESSAGE);
							} else {
								p = Runtime.getRuntime().exec("cmd /c " + CeNSystemProperties.getACDLCMSViewerFullyQualifiedPath() + " /SP\"" + launchFile + "\""); 
								
								// Log the metric of # of users who use ACD to view NMRs
								CeNErrorHandler.getInstance().sendMetricsLogToServer("VIEW LCMS ACD", MasterController.getUser().getNTUserID(), MasterController.getUser().getSiteCode());
								//p.waitFor(); // This only waits for the process to be kicked off. Not for the app to open.
							}
						} else {
							String launchFile = SSIZipLauncher.findLCMSLaunchFile(tempDirName.toString());
							if (launchFile == null) {
								JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
										"Viewer cannot be launched since SSIZip/zip file does not contain a _HEADER.TXT/MSD1.MS file.",
										"Error", JOptionPane.ERROR_MESSAGE);
							} else {
								p = Runtime.getRuntime().exec("cmd /c \"\"" + CeNSystemProperties.getMestreNovaLCMSSoftware() + "\" \"" + launchFile + "\"\"");
							
								// Log the metric of # of users who use ACD to view NMRs
								CeNErrorHandler.getInstance().sendMetricsLogToServer("VIEW LCMS MN", MasterController.getUser().getNTUserID(), MasterController.getUser().getSiteCode());
								//p.waitFor(); // This only waits for the process to be kicked off. Not for the app to open.
							}
						}
					} else if (fileName.endsWith("SSIZip")) {   // Not NMR
						// we need to launch WinZip
						File origFile = new File(tempFileName);
						File tempFile = new File(tempFileName + ".zip");
						
						// Rename file (or directory)
						boolean success = origFile.renameTo(tempFile);
						if (!success) {
							CeNErrorHandler.showMsgOptionPane(parent.getParent(), "Failed renaming file.",
									"Could not rename SSIZip to zip.  Please copy the file to a new location if you wish to keep it.\nThe file is currently at: \n"
									+ tempFile + "\nIt will be erased on exit of the application.", JOptionPane.INFORMATION_MESSAGE);
							// TODO: change to using fileChooser menu to save file to user's location.
						} else {
		
							Process p = Runtime.getRuntime().exec("cmd /c start " + tempFile.getPath());
							p.waitFor(); // This only waits for the process to be
							// kicked off. Not for the app to open.
						}
					} else {
						Process p = Runtime.getRuntime().exec("cmd /c start " + tempFileName);
						p.waitFor(); // This only waits for the process to be
						// kicked off. Not for the app to open.
					}
				} else {
					JOptionPane.showMessageDialog(parent.getParent(), "File returned from AnalyticalService/CyberLab is empty.",
							  "Error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception e) {
				String msg;
				if (e.getCause() == null) msg = e.getMessage(); else msg = e.getCause().toString();
				if (msg.indexOf("Call to CyberLab API failed") > -1) {
					JOptionPane.showMessageDialog(parent.getParent(), "File not found in AnalyticalService/CyberLab or CyberLab API Failure.",
												  "Error", JOptionPane.ERROR_MESSAGE);
					CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "Error occurred while performing viewFile, Not Displayed to User");
				} else {
					CeNErrorHandler.getInstance().logExceptionMsg(parent.getParent(), "Error occurred while performing viewFile", e);
				}
				parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
				// Cleanup now
				SSIZipLauncher.deleteDirectoryTree(tempDirName.toString());
			} finally {
				parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
			}
		}

		public void run() {
			viewFile(selectedAnalysis);
		}
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
						// int progressIndex = -1;
						public Object construct() {
							final String progressStatus = "Saving file " + saveFile + "...";
							CeNJobProgressHandler.getInstance().addItem(progressStatus);
							try {
								writeToDisk(saveFile);
							} catch (Exception e) {
								CeNErrorHandler.getInstance().logExceptionMsg(null, e);
							} finally {// in case there is an exception and
								// method finished is not reached
								CeNJobProgressHandler.getInstance().removeItem(progressStatus);
							}
							return null;
						}
					};
					saveProcess.start();
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
					JOptionPane.showMessageDialog(this, "Error: Unable to save the file to disk", "File Write Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * @param saveFile
	 * @throws CeNAnalyticalServiceAccessException
	 * @throws RemoteException
	 * @throws PropertyException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws AnalyticalServiceException 
	 */
	private void writeToDisk(final File saveFile) throws AnalyticalServiceException,  PropertyException, IOException,
			FileNotFoundException, AnalyticalServiceAccessException {
		AnalyticalServiceDelegate analyticalServiceDelegate = new AnalyticalServiceDelegate();
		byte[] fileContents = null;
		try {
			fileContents = analyticalServiceDelegate.retrieveFileContents(selectedAnalysis.getCyberLabDomainId(), selectedAnalysis.getCyberLabFileId(), selectedAnalysis.getSite());
		} catch (Throwable e) {
			if (e.getCause().toString().indexOf("CyberLab") > -1) {
				JOptionPane.showMessageDialog(null, "File not found in AnalyticalService/CyberLab or CyberLab API Failure.", "Error",
						JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing SaveFile, Not Displayed to User");
			} else {
				ceh.logExceptionMsg(null, "Error occurred while performing SaveFile", e);
			}
		}
		FileOutputStream out = new FileOutputStream(saveFile);
		out.write(fileContents);
		out.close();
	}

	/**
	 * 
	 */
	protected void performUnLinkAll(final String sampleRef) {
		final String s = "Performing UnLink All for " + sampleRef + " ...";
		CeNJobProgressHandler.getInstance().addItem(s);
		SwingWorker unLinkAll = new SwingWorker() {
			public Object construct() {
				try {
					analyticalUtility.unlinkAll(sampleRef);
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, "Exception occured UnLinkAll.", e);
				} finally {
					CeNJobProgressHandler.getInstance().removeItem(s);
				}
				return null;
			}

			public void finished() {
				notifySummaryViewers();
			}
		};
		unLinkAll.start();
	}

	/**
	 * This method is provide for the Analytical summary object where the menu options are visible on the right click on a table
	 * row.
	 * 
	 * @return Returns the optionsPopup.
	 */
	public JPopupMenu getOptionsPopupForAnalyticalSummary() {
		optionsPopup.removeAll();
		unlinkAllItem.setEnabled(true);
		mapAllToItem.setEnabled(true);
		//boolean isBatchListAvailable = updateBatchListOnMenu(mapAllToItem, true);
//		if (true) { //isBatchListAvailable) {
			optionsPopup.add(unlinkAllItem);
			optionsPopup.add(mapAllToItem);
			optionsPopup.add(uploadPDFItem);
//		} else {
//			optionsPopup.add(uploadPDFItem);
//		}
		return optionsPopup;
	}

	/**
	 * This method is provide for the Analytical summary object where
	 * the menu options are visible on the right click on a table row.
	 * @return Returns the optionsPopup.
	 */
	public JPopupMenu getOptionsPopupForAnalyticalSummary(String sampleRef) {
		unlinkAllItem.setVisible(!analyticalUtility.isAnalysisEmptyForBatch(sampleRef));
		
		mapAllToItem.setVisible(true); //updateBatchListOnMenu(mapAllToItem, true));
		
		try {
			uploadPDFItem.setVisible(true);
		} catch (Exception e) { 
			uploadPDFItem.setVisible(false); 
		}

		return optionsPopup;
	}
	
//	/**
//	 * This method returns true if the selected Analytical Batch is already associated with Links to instruments.
//	 * 
//	 * @param analyticalBatchNo
//	 * @return true if Links are associated with analticalBatchNo
//	 */
//	private boolean hasLinks(String analyticalBatchNo) {
//		//ArrayList al_sampleRefs = nbPage.getAnalysisCache().getDistinctSampleRefes();
//		ArrayList analysisModels = pageModel.getAnalysisCache().getAnalyticalList();
//		for (Iterator it = analysisModels.iterator(); it.hasNext();) {
//			AnalysisModel model = (AnalysisModel) it.next();
//			if (model.getCenSampleRef().equalsIgnoreCase(analyticalBatchNo))
//				return true;
//		}
//		return false;
//	}
//	
//	private List convertAnalysisModelsToAnalyses(List analysisModels) {
//		ArrayList convertedList = new ArrayList();
//		for (Iterator it = analysisModels.iterator(); it.hasNext();) {
//			AnalysisModel model = (AnalysisModel) it.next();
//			convertedList.add(com.chemistry.enotebook.utils.CeN11To12ConversionUtils.convertAnlaysisModelToAnalysis(model));
//		}
//		return convertedList;
//	}
	
	public void batchSelectionChanged(BatchSelectionEvent event) {
		Object obj = event.getSubObject();
		if (obj instanceof PlateWell) {			
			PlateWell well = (PlateWell) obj;
			//this.well = well;
			this.productBatch = (ProductBatchModel) well.getBatch();
		} else if (obj instanceof ProductBatchModel) {
			this.productBatch = (ProductBatchModel) obj;
		} else if (obj instanceof ParallelCeNUtilObject) {
			ParallelCeNUtilObject utilobj = (ParallelCeNUtilObject) obj;
			this.productBatch = (ProductBatchModel) utilobj.getBatch();
		} else {
			return;
		}
		if (this.productBatch != null) {
			this.buildAnalyticalTree(this.productBatch.getBatchNumber().getBatchNumber());
		}
	}

	public String getSelectedSampleRef() {
		return selectedSampleRef;
	}

	public void setSelectedSampleRef(String selectedSampleRef) {
		this.selectedSampleRef = selectedSampleRef;
	}
	
	public void addSummaryViewer(AnalyticalDetailTableView summaryViewer) {
		summaryViewers.add(summaryViewer);
	}
	
	public void removeSummaryViewer(AnalyticalDetailTableView summaryViewer) {
		if (summaryViewers.contains(summaryViewer))
			summaryViewers.remove(summaryViewer);
	}
	
	private void notifySummaryViewers() {
		for(AnalyticalDetailTableView adtv : summaryViewers) {
			adtv.updateAnalyses();
			adtv.reload();
		}
	}

	/**
	 * Use the JOptionsPane to display the list of batches that can be mapped to.
	 * @param sampleRefFrom
	 */
	private void displayMapToDialog() {  // vb 6/5 sample ref from is never set
		String sampleRefFrom = (String) jComboBoxSampleRefs.getSelectedItem();
		if (sampleRefFrom == null)
			return;
		List<String> al_allCeNSampleRefs = analyticalUtility.getAllSampleRefs();
		List<String> selectableBatchesList = new ArrayList<String>();
		if (al_allCeNSampleRefs.contains(sampleRefFrom)) {
			List<String> al_AllBatches = analyticalUtility.getSampleRefsFromBatchInfo(BatchType.ACTUAL_PRODUCT);
			for (int i=0; i < al_AllBatches.size(); i++) {
		    	if (al_AllBatches.get(i).toString().equals(sampleRefFrom)) continue;
		    	if (! selectableBatchesList.contains(al_AllBatches.get(i).toString()))
		    		selectableBatchesList.add(al_AllBatches.get(i).toString());
			}
		}
		Collections.sort(selectableBatchesList);
//		Collections.sort(selectableBatchesList, new Comparator() {
//			public int compare(Object o1, Object o2) {
//				if (o1 == null || o2 == null)
//					return 0;
//				if (o1 instanceof String && o2 instanceof String) {
//					String s1 = (String) o1;
//					String s2 = (String) o2;
//					if (s1.length() < 14 || s2.length() < 14)
//						return 0;
//					s1 = s1.substring(14, s1.length()-2);
//					s2 = s2.substring(14, s2.length()-2);
//					
//					try {
//						int i1 = Integer.parseInt(s1);
//						int i2 = Integer.parseInt(s2);
//						if (i1 > i2) return 1;
//						else if (i1 < i2) return -1;
//						else return 0;
//					} catch (NumberFormatException e) {
//						return 0;
//					}
//				}
//				return 0;
//			}
//		});
		String[] selectableBatches = (String[]) selectableBatchesList.toArray(new String[] {}); 
		JPanel mapToSelectionPanel = new JPanel();
		JList list = new JList(selectableBatches); 
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(170, 300));
		mapToSelectionPanel.add(listScroller);
		
		Object[] message = new Object[1];
		int index = 0;
		message[index++] = mapToSelectionPanel;
		// Options
		String[] options = { "OK", "Cancel" };
		int result = JOptionPane.showOptionDialog(MasterController.getGUIComponent(), // the parent that the
				// dialog blocks
				message, // the dialog message array
				"Map To Batch", // the title of the dialog window
				JOptionPane.DEFAULT_OPTION, // option type
				JOptionPane.PLAIN_MESSAGE, // message type
				null, // optional icon, use null to use the default icon
				options, // options string array, will be made into buttons
				options[1] // option that should be made into a default button
				);
		switch (result) {
			case 0: // OK
				Object[] values = list.getSelectedValues();
				Object obj = null;
				
				if (values != null && values.length > 0) {
					String sampleRefTo = values[0].toString();
					log.debug("Values: " + values[0].toString());
					
					if (analyticalSampleReferencesTree_ != null && analyticalSampleReferencesTree_.getLastSelectedPathComponent() != null) {
						obj = ((DefaultMutableTreeNode) analyticalSampleReferencesTree_.getLastSelectedPathComponent()).getUserObject();						
						if (obj instanceof AnalysisModel) {
							log.debug("Values: " + values[0].toString());
							this.performMap(sampleRefFrom, sampleRefTo, (AnalysisModel) obj);
						}
					} else {
						this.performMap(sampleRefFrom, sampleRefTo, null);
					}
				}
				
				break;
			case 1: // cancel
				break;
		}
	}
	
	/**
	 * does the map, the from row disappers when the tabled is reloaded values can be seen for the To row
	 * 
	 * @param sampleRefFrom
	 * @param sampleRefTo
	 */
	public void performMap(String sampleRefFrom, String sampleRefTo, AnalysisModel selectedAnalysis) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		//if (applyMapToAllLinks) {
		if (selectedAnalysis == null) {
			analyticalUtility.performMap(sampleRefFrom, sampleRefTo);
			this.notifySummaryViewers();
// Dead Code			
//			if (selectedAnalysis != null) {
//				buildAnalyticalTree(selectedAnalysis.getCenSampleRef());
//			}
		} else {
			analyticalUtility.performAnalysisMap(sampleRefFrom,sampleRefTo, selectedAnalysis);
			notifySummaryViewers();
		}
		this.enableSaveButton();
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		this.fillAnalyticalSampleRefComboBox();
	}
	
	private void enableSaveButton() {
		if (this.pageModel != null) {
			this.pageModel.setModelChanged(true);
			MasterController.getGUIComponent().enableSaveButtons();
		}
	}

	public void refreshPageModel(NotebookPageModel pageModel2) {
		if (pageModel != pageModel2)
			this.pageModel = pageModel2; 
		enableComponents();
	}

	private void enableComponents() {
		if (pageModel != null)
		{
			isEditable = pageModel.isEditable();
			if (!isEditable) {
				rendererOptionsPopup.remove(unlinkItem);
				rendererOptionsPopup.remove(mapToItem);
			}
			optionsButton.setEnabled(isEditable);
		}
	}
}
