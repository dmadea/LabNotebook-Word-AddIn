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
package com.chemistry.enotebook.client.gui.common.ekit;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.delegate.NotebookDelegate;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.ConceptionNotebookPageGUI;
import com.chemistry.enotebook.client.gui.page.LoadPageJDialog;
import com.chemistry.enotebook.client.gui.page.ParallelNotebookPageGUI;
import com.chemistry.enotebook.client.gui.page.SingletonNotebookPageGUI;
import com.chemistry.enotebook.client.gui.page.batch.CompoundCreationHandler;
import com.chemistry.enotebook.client.gui.page.procedure.JDialogAddBatchLotProc;
import com.chemistry.enotebook.client.gui.page.procedure.NewBatchJDialog;
import com.chemistry.enotebook.client.gui.page.procedure.cenURLStreamHandler;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.ProductPlate;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import com.chemistry.enotebook.storage.ValidationInfo;
import com.chemistry.enotebook.utils.CommonUtils;
import com.hexidec.ekit.EkitCore;
import com.hexidec.ekit.EkitCoreCen;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcedureContainer extends javax.swing.JPanel implements FocusListener {
	
	private static final long serialVersionUID = -3350115733199367540L;

	private static final Log log = LogFactory.getLog(ProcedureContainer.class);
	
	private JPanel jPanelProcedure;
	private JPanel jPaneMain;
	private EkitCoreCen edit;
	private NotebookPageModel pageModel;
	private boolean _readOnly = false;
	private boolean _modified = false;
	private boolean keyTyped = false;
	private PropertyChangeSupport changes;
	private String compareText = "";
	private boolean isParallel;
	private JPopupMenu jPopupMenuStoicElementOptions;

	private CompoundCreationHandler compoundCreationHandler = null;
	
	public ProcedureContainer(NotebookPageModel pageModel, CompoundCreationHandler compoundCreationHandler, boolean isParallel) {
		this(pageModel,isParallel);
		this.compoundCreationHandler = compoundCreationHandler;
	}
	
	public ProcedureContainer(NotebookPageModel pageModel,boolean isParallel) {
		this.putClientProperty("keyTyped", new Integer(-1));
		this.pageModel = pageModel;
		this.edit = initEkit(this.pageModel);
		this.setProcedure(pageModel);
		changes = new PropertyChangeSupport(this);
		setReadOnly(!pageModel.isEditable());
		this.isParallel = isParallel;
		
		initGUI();
	}
	
	private EkitCoreCen initEkit(NotebookPageModel notebookPageModel) {
		String toolBar = "CT|CP|PS|SP|UN|RE|FN|SP|UC|UM|LK|TI|SP|TE|CE|RI|CI|RD|CD|*|BL|IT|UD|SP|SK|SU|SB|SP|AL|AC|AR|AJ|SP|UL|OL|*|ST|SP|FO";
		Vector<String> menuBar = new Vector<String>(Arrays.asList(new String[] {
				EkitCore.KEY_MENU_EDIT, EkitCore.KEY_MENU_FONT,
				EkitCore.KEY_MENU_FORMAT, EkitCore.KEY_MENU_SEARCH,
				EkitCore.KEY_MENU_INSERT, EkitCore.KEY_MENU_TABLE })); // Add EkitCore.KEY_MENU_TOOLS for spellchecker
		
		EkitCoreCen ekitCore = new EkitCoreCen(false, null, null, null, null, null, true, false, false, true, null, null, false, false, true, true, toolBar, false);
		ekitCore.setProcedureContainer(this);
		ekitCore.getCustomMenuBar(menuBar);

		return ekitCore;
	}
	
	private void initEkitGUI() {
		boolean paramBoolean1 = true;
		jPanelProcedure.setLayout(new GridBagLayout());
		GridBagConstraints localGridBagConstraints = new GridBagConstraints();
		localGridBagConstraints.fill = 2;
		localGridBagConstraints.anchor = 11;
		localGridBagConstraints.gridheight = 1;
		localGridBagConstraints.gridwidth = 1;
		localGridBagConstraints.weightx = 1.0D;
		localGridBagConstraints.weighty = 0.0D;
		localGridBagConstraints.gridx = 1;

		localGridBagConstraints.gridy = 1;
		jPanelProcedure.add(
				this.edit.getToolBarMain(paramBoolean1),
				localGridBagConstraints);

		localGridBagConstraints.gridy = 2;
		jPanelProcedure.add(
				this.edit.getToolBarFormat(paramBoolean1),
				localGridBagConstraints);

		localGridBagConstraints.gridy = 3;
		jPanelProcedure.add(
				this.edit.getToolBarStyles(paramBoolean1),
				localGridBagConstraints);

		localGridBagConstraints.anchor = 15;
		localGridBagConstraints.fill = 1;
		localGridBagConstraints.weighty = 1.0D;
		localGridBagConstraints.gridy = 4;
		jPanelProcedure.add(this.edit, localGridBagConstraints);
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			jPaneMain = new JPanel();
			jPanelProcedure = new JPanel();
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setPreferredSize(new java.awt.Dimension(520, 594));
			this.setBorder(new MatteBorder(new Insets(1, 1, 1, 1), new java.awt.Color(0, 0, 0)));
			BorderLayout jPaneMainLayout = new BorderLayout();
			jPaneMain.setLayout(jPaneMainLayout);
			jPaneMainLayout.setHgap(0);
			jPaneMainLayout.setVgap(0);
			this.add(jPaneMain, BorderLayout.CENTER);
			BorderLayout jPanelProcedureLayout = new BorderLayout();
			jPanelProcedure.setLayout(jPanelProcedureLayout);
			jPanelProcedureLayout.setHgap(0);
			jPanelProcedureLayout.setVgap(0);
			jPaneMain.add(edit.getMenuBar(), BorderLayout.NORTH);
			jPaneMain.add(jPanelProcedure, BorderLayout.CENTER);
			initEkitGUI();
			postInitGUI();
			refresh();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		// setup ekit & add listener
		try {
			// setup locale for English char set
			Locale.setDefault(new Locale("en"));
			
			edit.addFocusListener(this);
			edit.getTextPane().addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					keyReleasedActionPerformed(e);
				}
			});
			
			edit.getTextPane().addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					mouseClickedActionPerformed(evt);
				}
				
				public void mouseReleased(MouseEvent evt) {

					if (evt.isPopupTrigger()) {
						preparePopupMenuAndOptions(evt);
						jPopupMenuStoicElementOptions.show(evt.getComponent(), evt.getX(), evt.getY());
					}
				}
			});
			
			JButton viewProtocolButton = new JButton("  View Protocol  ");
			viewProtocolButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					showProtocolDocument();
				}
			});
			
			JComponent editToolBar = edit.getToolBar(false);
			if(editToolBar != null) {
				Component[] components = editToolBar.getComponents();
				
				if (components != null && components.length > 0) {
					JToolBar toolBar = (JToolBar) editToolBar.getComponent(0);
					if (isParallel) {
						toolBar.add(viewProtocolButton);
					}
				}
			}
		} catch (Exception error) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, "Procedure GUI Error", error);
		}
		
		try {
			if (this.pageModel != null &&
				this.pageModel.getProcedure() != null)
				edit.setDocumentText(pageModel.getProcedure());
			else
				edit.setDocumentText("");
		} catch (Exception e) {
			log.error("Failure while updating document with procedure", e);
			CeNErrorHandler.getInstance().logExceptionMsg(null, "Procedure Text Error", e);
		}
	}

	public void setProcedure(NotebookPageModel model) {
		this.pageModel = model;
		if (model != null && model.getProcedure()!=null) {
			setDocumentText(model.getProcedure());
		} else {
			setDocumentText("");
		}
	}

	/**
	 * @return Returns the edit.
	 */
	public EkitCore getEdit() {
		return edit;
	}

	/**
	 * @param arg0
	 */
	public String getDocumentText() {
		return edit.getDocumentText();
	}

	private void setDocumentText(String doc) {
		try {
			edit.setDocumentText((doc.replaceAll("iso-8859-1", "UTF-8")));
			String oldProc = getDocumentText();
			String newProc = setupLinks(oldProc);
			edit.setDocumentText(newProc);
			compareText = this.getDocumentText();
		} catch (Exception e) {
			log.error("Error setting document text: ", e);
		}
	}

	public boolean getReadOnly() {
		return _readOnly;
	}

	public void setReadOnly(boolean flag) {
		_readOnly = flag;
		
		edit.getTextPane().setEditable(!_readOnly);
		edit.getMenuBar().setVisible(!_readOnly);
		
		JToolBar toolBar = edit.getToolBar(false);
		if(toolBar != null) {
			edit.getToolBar(false).setVisible(!_readOnly);
		}
	}

	public void focusGained(FocusEvent arg0) {
		if (!isModified() && !compareText.equals(getDocumentText()))
			setModified(true);
	}

	public void focusLost(FocusEvent arg0) {
		if (this.isModified()) {
			 pageModel.setProcedure(getDocumentText());
			 pageModel.setProcedureWidth(edit.getWidth());//edit.getHTMLPane().getWidth());
		}
	}

	protected void customMenuActionPerformed(TextEvent evt) {
		if (evt.getActionCommand() == TextEvent.CUSTOM_ACTION && !_readOnly) {
			if (evt.getExtraString().equals("new_batch_lot_entry:")) {
				NewBatchJDialog.showGUI(pageModel, edit, pageModel.getNotebookRefAsString(), compoundCreationHandler);
//			} else if (evt.getExtraString().equals("insert_experiment_link")) {
//				JTextField noteBookRefField = new JTextField();
//				String message = "Enter a Notebook Reference(########-####)";
//				Object[] options = { "OK", "Cancel" };
//				int result = JOptionPane.showOptionDialog(this, new Object[] { message, noteBookRefField },
//						"Open an Existing Experiment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
//						"");
//				
//				if (result == JOptionPane.OK_OPTION) {
//					NotebookRef currentNbRef = null;
//					String nbRefNo = noteBookRefField.getText().trim();
//					try {
//						currentNbRef = new NotebookRef(nbRefNo);
//					} catch (InvalidNotebookRefException e) {
//						JOptionPane.showOptionDialog(MasterController.getGUIComponent(), "Invalid Notebook number entered.", "Invalid Notebook", JOptionPane.DEFAULT_OPTION , JOptionPane.ERROR_MESSAGE, null, null, null);
//						e.printStackTrace();
//					}
//					
//					if (pageModel.getNbRef().getNbRef().equals(nbRefNo))
//					{
//						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Notebook reference must be another note book.");
//						return;	
//					}
//					ValidationInfo validationInfo = null;
//	 	 			try {
//	 	 				StorageDelegate storageDelegate = ServiceController.getStorageDelegate();
//	 	 				validationInfo = storageDelegate.validateNotebook(null, currentNbRef.getNbNumber(), currentNbRef.getNbPage());
//	 	 			} catch (Exception e) {
//	 	 				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
//	 	 			}
//	 	 			
//	                if (validationInfo != null) {
//						String insertString = "&nbsp;<A ephoxclickable=" + '"' + '"' + " href=" + '"' + "cen://" + currentNbRef.getNotebookRef() + '"' + ">" + currentNbRef.getNotebookRef() + "</A>&nbsp;";
//						edit.insertHTMLAtCursor(insertString);
//	                } else {
//	 					JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
//	 							"Notebook Reference '" + currentNbRef.getNbRef() + "' does not exist.", 
//								"Insert Reference Error", JOptionPane.ERROR_MESSAGE);
//	                }
//				}
				/**
				 * TODO create logic to open an existing experiment at the time of coding this part loading with new POJO model is
				 * not completed
				 */
			} else if (evt.getExtraString().equals("insert_plate_link:")) {
				JDialogAddBatchLotProc AddBatchDia = new JDialogAddBatchLotProc(MasterController.getGuiController().getGUIComponent(), pageModel, false);
				Point loc = MasterController.getGuiController().getGUIComponent().getLocation();
				Dimension dim = MasterController.getGuiController().getGUIComponent().getSize();
				AddBatchDia.setLocation(loc.x + (dim.width - AddBatchDia.getSize().width)/2, loc.y + (dim.height - AddBatchDia.getSize().height)/2);
				AddBatchDia.setVisible(true);
				if (!AddBatchDia.isDia_cancelled()) {
					//dialog not cancelled
					if (AddBatchDia.getSelectedBatch().length() != 0) {
						String insertString = "&nbsp;<A ephoxclickable=" + '"' + '"' + " href=" + '"' + "cen://" + AddBatchDia.getSelectedBatch() + '"' + ">" + AddBatchDia.getSelectedBatch() + "</A>&nbsp;";
						this.insertHTMLAtCursor(insertString);
					}
					AddBatchDia.dispose();
				}			
			} else if (evt.getExtraString().equals("insert_batch_link:")) {
				JDialogAddBatchLotProc AddBatchDia = new JDialogAddBatchLotProc(MasterController.getGuiController().getGUIComponent(), pageModel, true);
				Point loc = MasterController.getGuiController().getGUIComponent().getLocation();
				Dimension dim = MasterController.getGuiController().getGUIComponent().getSize();
				AddBatchDia.setLocation(loc.x + (dim.width - AddBatchDia.getSize().width)/2, loc.y + (dim.height - AddBatchDia.getSize().height)/2);
				AddBatchDia.setVisible(true);
				if (!AddBatchDia.isDia_cancelled()) {
					//dialog not cancelled
					if (AddBatchDia.getSelectedBatch().length() != 0) {
						//String insertString = "<SPAN ephoxclickable=" + '"' + '"' + '>' + "<A ephoxclickable=" + '"' + '"' + " href=" + '"' + "cen://" + AddBatchDia.getSelectedBatch() + '"' + ">" + AddBatchDia.getSelectedBatch() + "</A>&nbsp;</SPAN>";
						String insertString = "&nbsp;<A ephoxclickable=" + '"' + '"' + " href=" + '"' + "cen://" + AddBatchDia.getSelectedBatch() + '"' + ">" + AddBatchDia.getSelectedBatch() + "</A>&nbsp;";
						this.insertHTMLAtCursor(insertString);
					}
					AddBatchDia.dispose();
				}
			} else if (evt.getExtraString().equals("insert_notebookref:")) {
		 	 	LoadPageJDialog dlg = new LoadPageJDialog(MasterController.getGUIComponent());
		 	 	if (dlg.getPageToLoad() && dlg.getNotebookRef() != null) {
		 	 		String newNbRef = dlg.getNotebookRef();
	 	 			NotebookRef nbRef = null;
	 	 			try {
	 	 				nbRef = new NotebookRef(newNbRef);
	 	 			} catch (Exception e) {
	 					JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
	 							"Notebook Reference '" + newNbRef + "' is Invalid.", "Insert Reference Error", JOptionPane.ERROR_MESSAGE);
	 	 				return;
	 	 			}
	 	 			
	 	 			ValidationInfo vInfo = null;
	 	 			try {
	 	 	            NotebookDelegate nbDel = new NotebookDelegate(MasterController.getUser().getSessionIdentifier());
	
	 	 				vInfo = nbDel.validateNotebook(null, nbRef.getNbNumber(), nbRef.getNbPage(), (newNbRef.contains("v") ? "" + nbRef.getVersion() : null));
	 	 			} catch (Exception e) {
	 	 				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
	 	 			}
	 	 			
	                if (experimentExists(vInfo)) {
	                	if(userHasPrivilegesToOpenThisVersionOfExperiment(vInfo)) {
		                	String url = nbRef.getNotebookRef() + (newNbRef.contains("v") ? "v" + nbRef.getVersion() : "");
							String insertString = "&nbsp;<A ephoxclickable=" + '"' + '"' + " href=" + '"' + "cen://" + url + '"' + ">" + url + "</A>&nbsp;";
							this.insertHTMLAtCursor(insertString);
	                	} else {
	                		JOptionPane.showMessageDialog(this, "User has no access to this version of experiment.");
	                	}
	                } else {
	 					JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
	 							"Notebook Reference '" + nbRef.getNbRef() + "' does not exist.", 
								"Insert Reference Error", JOptionPane.ERROR_MESSAGE);
	                }
		 	 	} 
			} else if (evt.getExtraString().equals("insert_datestamp:")) {
				String timestamp = NotebookPageUtil.formatDateWithoutTime(new Date());
				String insertString = timestamp;
				this.insertHTMLAtCursor(insertString);
			} else if (evt.getExtraString().equals("insert_timestamp:")) {
			    SimpleDateFormat df = new SimpleDateFormat("MMMMM dd, yyyy HH:mm:ss");		//SCT replaced back to dateFormat since NotebookPageUtil.FormatDate method is used by other classes
				String insertString = df.format(new Date());
				this.insertHTMLAtCursor(insertString);
			} else if (evt.getExtraString().equals("add_batch_lot_entry:")) {
				// check to see if page has batches if not ask to see if want to create a batch
				// List tempList = pageModel.getBatchCache().getBatches(BatchType.ACTUAL_PRODUCT);
				// if (!tempList.iterator().hasNext()){  // do you want to create a batch ?
				// Modal dialog with yes/no button
				int answer = JOptionPane.showConfirmDialog(MasterController.getGuiController().getGUIComponent(),
						"Do you want to create a new batch?", "New Batch?", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (answer == JOptionPane.YES_OPTION) {
					NewBatchJDialog.showGUI(pageModel, edit, pageModel.getNotebookRefAsString(), compoundCreationHandler);
					/*
					 * // User clicked YES. //need to call the create batch functionality 
					 * ParallelNotebookPageGUI page = (ParallelNotebookPageGUI)MasterController.getGUIComponent().jDesktopPane1.getSelectedFrame();
					 * page.getBatchInfo_cont().jButtonAddBatchReleased(null); 
					 * String firBatch = page.getBatchInfo_cont().getFirstBatch(); 
					 * if (firBatch != null) { //nothing went wrong on getting first batch
					 *   if (firBatch.length()!=0) { 
					 *     //String insertString = "<SPAN CONTENTEDITABLE=" + '"' + "false" + '"' + '>' + "<A CONTENTEDITABLE=" + '"' + "false" + '"' + 
					 *     					" href=" + '"' + "cen://" + firBatch + '"' + ">" + firBatch + "</A></SPAN>" + "<p>&nbsp;</p>";
					 * 	   String insertString = "<SPAN ephoxclickable=" + '"' + '"' + '>' + "<A ephoxclickable=" + '"' + '"' + "href=" + '"' + 
					 * 						"cen://" + firBatch + '"' + ">" + firBatch + "</A>&nbsp;</SPAN>" + " ";
					 * 	   edit.insertHTMLAtCursor(insertString); 
					 *   } 
					 * }
					 */
				} else if (answer == JOptionPane.NO_OPTION) {
					// show the add batch/lot dialog with the current page model
					//	JDialogAddBatchLotProc AddBatchDia = new JDialogAddBatchLotProc(MasterController.getGuiController().getGUIComponent());
/*					Point loc = MasterController.getGuiController().getGUIComponent().getLocation();
					Dimension dim = MasterController.getGuiController().getGUIComponent().getSize();
					AddBatchDia.setLocation(loc.x + (dim.width - AddBatchDia.getSize().width) / 2, loc.y
							+ (dim.height - AddBatchDia.getSize().height) / 2);
					AddBatchDia.show();
					if (!AddBatchDia.isDia_cancelled()) {
						// dialog not cancelled
						if (AddBatchDia.getSelectedBatch().length() != 0) {
							// String insertString = "<SPAN ephoxclickable="
							// + '"' + '"' + '>' + "<A ephoxclickable=" +
							// '"' + '"' + " href=" + '"' + "cen://" +
							// AddBatchDia.getSelectedBatch() + '"' + ">" +
							// AddBatchDia.getSelectedBatch() +
							// "</A>&nbsp;</SPAN>";
							String insertString = "&nbsp;<A ephoxclickable=" + '"' + '"' + " href=" + '"' + "cen://"
									+ AddBatchDia.getSelectedBatch() + '"' + ">" + AddBatchDia.getSelectedBatch() + "</A>&nbsp;";
							edit.insertHTMLAtCursor(insertString);
						}
						AddBatchDia.dispose();
					}*/
				}
			} else if (evt.getExtraString().equals("view_protocol_text:"))  {
				showProtocolDocument();
//			} else {
				// //show the add batch/lot dialog with the current page model
				// JDialogAddBatchLotProc AddBatchDia = new JDialogAddBatchLotProc(MasterController.getGuiController().getGUIComponent(), pageModel);
				// Point loc = MasterController.getGuiController().getGUIComponent().getLocation();
				// Dimension dim = MasterController.getGuiController().getGUIComponent().getSize();
				// AddBatchDia.setLocation(loc.x + (dim.width - AddBatchDia.getSize().width)/2, loc.y + (dim.height -
				// 						AddBatchDia.getSize().height)/2);
				// AddBatchDia.show();
				// if (!AddBatchDia.isDia_cancelled()) { //dialog not cancelled
				// 		if (AddBatchDia.getSelectedBatch().length()!=0) {
				// 			//String insertString = "<SPAN ephoxclickable=" + '"' + '"' + '>' + "<A ephoxclickable=" + '"' + '"' + " href=" + '"' +
				// 						"cen://" + AddBatchDia.getSelectedBatch() + '"' + ">" + AddBatchDia.getSelectedBatch() + "</A>&nbsp;</SPAN>";
				// 			String insertString = "&nbsp;<A ephoxclickable=" + '"' + '"' + " href=" + '"' + "cen://" + AddBatchDia.getSelectedBatch() + '"' + 
				//							">" + AddBatchDia.getSelectedBatch() + "</A>&nbsp;";
				// 			edit.insertHTMLAtCursor(insertString);
				//		}
				// 		AddBatchDia.dispose();
				// }
			}
		}
		// add handler to detect normal hyperlink insert
		// if (evt.getActionCommand() == TextEvent.HYPERLINK_ACTION) {
		// 		//System.out.println("Testing " + evt.getExtraString() + " " +
		// 		evt.toString());
		// }

		if (evt.getActionCommand() == TextEvent.LINK_CLICKED) {
			evt.setHandled(true);
			// System.out.println(evt.getExtraString());
			Object evtSrc = evt.getSource();
			if (evtSrc instanceof MouseEvent) {
				if (((MouseEvent) evtSrc).getClickCount() == 2) {
					try {
						URL clickedURL = new URL(null, evt.getExtraString(), new cenURLStreamHandler("cen"));
						if (clickedURL.getProtocol().equals("cen")) {
							// System.out.println("go to batch " + clickedURL.getAuthority());
							// do something interesting here - on clicking batch link
							if (MasterController.getGUIComponent().getJDesktopPane1().getSelectedFrame() instanceof ParallelNotebookPageGUI) {
									ParallelNotebookPageGUI page = (ParallelNotebookPageGUI) MasterController.getGUIComponent().getJDesktopPane1().getSelectedFrame();
								// to do need a better way to select batch tab
								if (clickedURL.getAuthority().length() < 5) { //Plate
									if (plateExists(clickedURL.getAuthority())) {
										page.setSelectedIndex(1);
										page.getBatchDetailsViewContainer().selectPlatesView();
										page.getPlateSummaryViewContainer().selectGivenPlate(clickedURL.getAuthority());
									} else {
										JOptionPane.showMessageDialog(this, "This plate does not exist in the platesummary table");
									}
								} else if (clickedURL.getAuthority().length() > NotebookPageUtil.NB_REF_MAX_LENGTH+1) { //Batch
									if (batchExists(clickedURL.getAuthority())) {
										page.setSelectedIndex(1);
										page.getBatchDetailsViewContainer().selectBatch(clickedURL.getAuthority());
									} else {
										JOptionPane.showMessageDialog(this, "This batch does not exist in the batch table");
									}								
								} else {
									MasterController.getGuiController().openPCeNExperimentCombinedBkndPage(null, clickedURL.getAuthority().trim());
								}
							}
							else if (MasterController.getGUIComponent().getJDesktopPane1().getSelectedFrame() instanceof SingletonNotebookPageGUI)
							{
								SingletonNotebookPageGUI page = (SingletonNotebookPageGUI) MasterController.getGUIComponent().getJDesktopPane1().getSelectedFrame();
								String url = clickedURL.getAuthority(); 
								if(NotebookPageUtil.isValidCeNBatchNumber(url)) { //Batch
									if (batchExists(url)) {
										page.setSelectedIndex(1);
										page.getBatchDetailsViewContainer().selectBatch(clickedURL.getAuthority());
									} else {
										JOptionPane.showMessageDialog(this, "This batch does not exist in the batch table");
									}								
								} else if(NotebookPageUtil.isValidNotebookRef(url)) {
									openClickedExperimentLink(url);
								} else {
									MasterController.getGuiController().openPCeNExperimentCombinedBkndPage(null, clickedURL.getAuthority().trim() );
								}
							}
							else if (MasterController.getGUIComponent().getJDesktopPane1().getSelectedFrame() instanceof ConceptionNotebookPageGUI)
							{
								ConceptionNotebookPageGUI page = (ConceptionNotebookPageGUI) MasterController.getGUIComponent().getJDesktopPane1().getSelectedFrame();
								String url = clickedURL.getAuthority();
								if(NotebookPageUtil.isValidCeNBatchNumber(url)) {
									if (batchExists(url)) {
										page.setSelectedIndex(1);
										page.getConceptionCompoundInfoContainer().selectBatch(clickedURL.getAuthority());
									} else {
										JOptionPane.showMessageDialog(this, "This batch does not exist in the batch table");
									}
								} else if(NotebookPageUtil.isValidNotebookRef(url)) {
									openClickedExperimentLink(url);
								}
							}
						}
						if (clickedURL.getProtocol().equals("http")) {
							// System.out.println("go to link " +
							// clickedURL.toString());
							try {
								// display in link in iexplore
								Runtime.getRuntime().exec("cmd /c start iexplore " + '"' + clickedURL.toString() + '"');
							} catch (IOException e) {
								CeNErrorHandler.getInstance().logExceptionMsg(null, e);
							}
						}
					} catch (MalformedURLException e) {
						CeNErrorHandler.getInstance().logExceptionMsg(null, e);
					}
				}
			}
		}
	}
	
	private void openClickedExperimentLink(String url) {
		ValidationInfo vInfo = getValidationInfo(url);
		if (experimentExists(vInfo)) {
			if(userHasPrivilegesToOpenThisVersionOfExperiment(vInfo)) {
				NotebookRef nbRef = null;
				try {
					nbRef = new NotebookRef(url);
					boolean openLastVersion = !url.contains("v");
					
					MasterController.getGuiController().openPCeNExperiment(null, nbRef.getNbNumber(), nbRef.getNbPage(), nbRef.getVersion(), false, openLastVersion);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(this, "User has no access to this version of experiment.");
			}
		} else {
			JOptionPane.showMessageDialog(this, "This experiment does not exist.");
		}
	}
	
	private boolean experimentExists(ValidationInfo vInfo) {
		return vInfo != null;
	}
	
	private ValidationInfo getValidationInfo(String notebookReference) {
		NotebookRef nbRef = null;
		try {
			nbRef = new NotebookRef(notebookReference);
			if(!notebookReference.contains("v")) {
				nbRef.setVersion(0);
			}
		} catch (Exception e) {
			return null;
		}
		
		ValidationInfo vInfo = null;
		try {
            NotebookDelegate nbDel = new NotebookDelegate(MasterController.getUser().getSessionIdentifier());

			vInfo = nbDel.validateNotebook(null, nbRef.getNbNumber(), nbRef.getNbPage(), notebookReference.contains("v") ? "" + nbRef.getVersion() : null);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		return vInfo;
	}
	
	private boolean userHasPrivilegesToOpenThisVersionOfExperiment(ValidationInfo vInfo) {
		return vInfo.latestVersion || MasterController.getUser().getNTUserID().equalsIgnoreCase(vInfo.creator) || CommonUtils.isNull(vInfo.creator);
	}
	
	private void showProtocolDocument() {
		if (pageModel.getProtocolID() == null || pageModel.getProtocolID().equals("")) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "No Protocol ID Specified.");
			return;
		} else {
			try {
//				String baseURL = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_GRC_PROTOCOL_URL);		
//				String protocolURL = baseURL + "?protocolId=" + pageModel.getProtocolID(); 
				// display in link via iexplore
//				Runtime.getRuntime().exec("cmd /c start iexplore " + protocolURL);
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
	}

	private boolean plateExists(String authority) {
		List<ProductPlate> productPlatesList = pageModel.getAllProductPlatesAndRegPlates();
		ProductPlate productPlate = null;
		for (int i=0; i<productPlatesList.size(); i++)
		{
			productPlate = productPlatesList.get(i);
			if (productPlate.getLotNo().equals(authority))
				return true;
		}
		return false;
	}

	private boolean batchExists(String authority) {
		//ArrayList productBatchModelsList =  (ArrayList) pageModel.getAllProductBatchModelsInThisPage(BatchType.ACTUAL_PRODUCT);
		List<ProductBatchModel> productBatchModelsList = pageModel.getAllProductBatchModelsInThisPage();
		ProductBatchModel productBatchModel = null;
		for (int i=0; i<productBatchModelsList.size(); i++)
		{
			productBatchModel = productBatchModelsList.get(i);
			if (productBatchModel.getBatchNumber().getBatchNumber().equals(authority))
				return true;
		}
		return false;
	}

	public void editorUpdated() {
		if (pageModel != null) {
			if (!StringUtils.equals(pageModel.getProcedure(), getDocumentText())) {
				pageModel.setProcedure(getDocumentText());
				setModified(true);
				enableSaveButton();
			}
		}
	}
	
	public void keyReleasedActionPerformed(KeyEvent e) {
		editorUpdated();
	}
	
	private void enableSaveButton() {
		pageModel.setModelChanged(true);
		MasterController.getGUIComponent().enableSaveButtons();

	}
	public void mouseClickedActionPerformed(MouseEvent evt) {
		if (!isModified() && !compareText.equals(getDocumentText()))
			setModified(true);
		if (edit != null) {
			JTextPane obj = edit.getTextPane();//.getHTMLPane();
			if (obj != null) {
				if (evt.getClickCount() == 1 && evt.getButton() != MouseEvent.BUTTON3) {
					int pos = obj.getCaretPosition();
					String oldProc = edit.getDocumentBody();//.getDocument();
					// setup all <a> links to be clickable
					String newProc = setupLinks(oldProc);
					edit.setDocumentText(newProc);//setDocument(newProc);
					if (obj.getText().length() > pos)
						try {
							obj.setCaretPosition(pos);
						} catch (IllegalArgumentException e) {
							// don't care. don't report
						}
				}// end if empty selection
			}// end if null
		}// end if null
	}

	public boolean isModified() {
		return _modified;
	}

	public void setModified(boolean modified) {
		_modified = modified;
	}

	/**
	 * 
	 */
	public void refresh() {
		if (pageModel != null) {
			int pos = edit.getTextPane().getCaretPosition();//.getHTMLPane().getCaretPosition();
			if (pageModel.getProcedure() != null)
			{		
				setDocumentText(pageModel.getProcedure());
				try {
					edit.getTextPane().setCaretPosition(pos); //.getHTMLPane().setCaretPosition(pos);
				} catch (RuntimeException e) {
					log.error("Failed refresh", e);
					//When invalid position is passed, this exception is obvious and need to check and avoid it.  
				}
			}
		}
	}

	private String setupLinks(String inputStr) {
		// //System.out.println("Running");
		// CharSequence inputStr = "<HTML><HEAD></HEAD><BODY>frefd<A
		// ephoxclickable=\"true\" HREF=\"../index.html\">List of
		// Packages</A>tedt<A HREF=\"../index.html\">List </A></BODY></HTML>";
		String patternStr = "<A(.*?)>";
		// Compile regular expression
		Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		// Replace all occurrences of pattern in input
		StringBuffer buf = new StringBuffer();
		while (matcher.find()) {
			// Get the match result
			String replaceStr = matcher.group();
			// has this string got a ephoxclickable attribute, if not add
			// one
			if (!hasEditable(replaceStr)) {
				// do some work
				// Convert to uppercase
				// replaceStr = replaceStr.toUpperCase();
				// add the ephoxclicable="true" attribute to the tag
				StringBuffer insBuf = new StringBuffer(replaceStr);
				int index = replaceStr.length() - 1;
				insBuf.insert(index, " ephoxclickable=\"true\" ");
				replaceStr = insBuf.toString();
				// Insert replacement
				matcher.appendReplacement(buf, replaceStr);
			}
		}
		matcher.appendTail(buf);
		// Get result
		String result = buf.toString();
		// //System.out.println(result);
		return result;
	}

	private boolean hasEditable(String inStr) {
		// Compile regular expression
		String patternStr = "(.*?)ephoxclickable(.*?)";
		Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
		// Determine if there is an exact match
		Matcher matcher = pattern.matcher(inStr);
		boolean matchFound = matcher.matches();
		return matchFound;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

	/**
	 * @return the keyTyped
	 */
	public boolean getKeyTyped() {
		return keyTyped;
	}

	/**
	 * @param keyTyped
	 *            the keyTyped to set
	 */
	
	//*************************//
/*	public void setKeyTyped(boolean keyTyped) {
		Integer stepNumber = new Integer(this.stepNumber);
		this.keyTyped = keyTyped;
		Integer newValue = new Integer(-1);
		changes.firePropertyChange("keyTyped", stepNumber, newValue);
	}*/
	
	public boolean isReadOnly()
	{
		return _readOnly;
	}

	public void refreshPageModel(NotebookPageModel pageModel2) {
		this.pageModel = pageModel2;
		
		if (pageModel != null) {
			//setDocumentText(pageModel.getProcedure());
			setReadOnly(!pageModel.isEditable());

		} else {
			setDocumentText("");
			setReadOnly(true);
		}
	}
		
	public void insertHTMLAtCursor(String insertString) {		
		insertHTMLAtCursor(edit, insertString);
	}
	
	public static void insertHTMLAtCursor(EkitCore edit, String insertString) {
		if (insertString.contains("<") && insertString.contains(">")) { // html
			HTMLEditorKit kit = new HTMLEditorKit();
			HTMLDocument doc = (HTMLDocument) edit.getTextPane().getStyledDocument();
			try {
				kit.insertHTML(doc, doc.getLength(), insertString, 0, 0, null);
			} catch (Exception e) {
				log.error("Error inserting string into document: ", e);
			} 
		} else { // plain text
			edit.getTextPane().replaceSelection(insertString);
		}
		edit.refreshOnUpdate();
	}

	private void preparePopupMenuAndOptions(MouseEvent evt) {
		if(jPopupMenuStoicElementOptions == null) {
			JMenuItem createNewBatchLotEntry = new JMenuItem("Create New Batch/Lot Entry");		
			createNewBatchLotEntry.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					customMenuActionPerformed(new TextEvent(this, TextEvent.CUSTOM_ACTION, "new_batch_lot_entry:", 0));
				}
			});

			JMenuItem insertBatchLink = new JMenuItem("Insert Batch Link");		
			insertBatchLink.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					customMenuActionPerformed(new TextEvent(this, TextEvent.CUSTOM_ACTION, "insert_batch_link:", 0));
				}
			});

			JMenuItem insertNotebookReference = new JMenuItem("Insert Notebook Reference");		
			insertNotebookReference.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					customMenuActionPerformed(new TextEvent(this, TextEvent.CUSTOM_ACTION, "insert_notebookref:", 0));
				}
			});

			JMenuItem insertDateStamp = new JMenuItem("Insert Date Stamp");		
			insertDateStamp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					customMenuActionPerformed(new TextEvent(this, TextEvent.CUSTOM_ACTION, "insert_datestamp:", 0));
				}
			});

			JMenuItem insertDateTimeStamp = new JMenuItem("Insert Date/Time Stamp");		
			insertDateTimeStamp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					customMenuActionPerformed(new TextEvent(this, TextEvent.CUSTOM_ACTION, "insert_timestamp:", 0));
				}
			});
			
			JMenu styleSubMenu = new JMenu("Style");
			JMenuItem styleNormal = new JMenuItem("Normal");
			JMenuItem styleHeading1 = new JMenuItem("Heading 1");
			JMenuItem styleHeading2 = new JMenuItem("Heading 2");
			JMenuItem styleHeading3 = new JMenuItem("Heading 3");
			JMenuItem styleHeading4 = new JMenuItem("Heading 4");
			JMenuItem styleHeading5 = new JMenuItem("Heading 5");
			JMenuItem styleHeading6 = new JMenuItem("Heading 6");
			JMenuItem styleFormatted = new JMenuItem("Formatted");
			JMenuItem styleAddress = new JMenuItem("Address");
			
			styleNormal.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					//TODO
				}
			});
			styleHeading1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					edit.performAction("Heading1");
				}
			});
			styleHeading2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					edit.performAction("Heading2");
				}
			});
			styleHeading3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					edit.performAction("Heading3");
				}
			});
			styleHeading4.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					edit.performAction("Heading4");
				}
			});
			styleHeading5.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					edit.performAction("Heading5");
				}
			});
			styleHeading6.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					edit.performAction("Heading6");
				}
			});
			styleFormatted.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					//TODO
				}
			});
			styleAddress.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					//TODO
				}
			});
			
			styleSubMenu.add(styleNormal);
			styleSubMenu.add(styleHeading1);
			styleSubMenu.add(styleHeading2);
			styleSubMenu.add(styleHeading3);
			styleSubMenu.add(styleHeading4);
			styleSubMenu.add(styleHeading5);
			styleSubMenu.add(styleHeading6);
			styleSubMenu.add(styleFormatted);
			styleSubMenu.add(styleAddress);
			
			JMenuItem removeFormatting = new JMenuItem("Remove Formatting");		
			removeFormatting.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					//TODO
				}
			});
			
			JMenuItem undo = new JMenuItem("Undo");		
			undo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					edit.performUndoAction();
					if (!pageModel.getProcedure().equals(getDocumentText())) {
						setModified(true);
						enableSaveButton() ;
					}
				}
			});
			
			JMenuItem cut = new JMenuItem("Cut");		
			cut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					edit.actionPerformed(new ActionEvent(new Object(),0, "textcut"));
				}
			});
			
			JMenuItem copy = new JMenuItem("Copy");		
			copy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					edit.actionPerformed(new ActionEvent(new Object(),0, "textcopy"));
				}
			});
			
			JMenuItem paste = new JMenuItem("Paste");		
			paste.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					edit.actionPerformed(new ActionEvent(new Object(),0, "textpaste"));
				}
			});

			JMenuItem selectAll = new JMenuItem("Select All");		
			selectAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					edit.performSelectAllAction();
				}
			});
			
			jPopupMenuStoicElementOptions = new JPopupMenu();
			jPopupMenuStoicElementOptions.add(createNewBatchLotEntry);
			jPopupMenuStoicElementOptions.add(insertBatchLink);
			jPopupMenuStoicElementOptions.add(insertNotebookReference);
			jPopupMenuStoicElementOptions.add(insertDateStamp);
			jPopupMenuStoicElementOptions.add(insertDateTimeStamp);
			jPopupMenuStoicElementOptions.addSeparator();
			jPopupMenuStoicElementOptions.add(styleSubMenu);
			jPopupMenuStoicElementOptions.addSeparator();
			jPopupMenuStoicElementOptions.add(removeFormatting);
			jPopupMenuStoicElementOptions.addSeparator();
			jPopupMenuStoicElementOptions.add(undo);
			jPopupMenuStoicElementOptions.addSeparator();
			jPopupMenuStoicElementOptions.add(cut);
			jPopupMenuStoicElementOptions.add(copy);
			jPopupMenuStoicElementOptions.add(paste);
			jPopupMenuStoicElementOptions.addSeparator();
			jPopupMenuStoicElementOptions.add(selectAll);
			jPopupMenuStoicElementOptions.addSeparator();
		}
	}
}
