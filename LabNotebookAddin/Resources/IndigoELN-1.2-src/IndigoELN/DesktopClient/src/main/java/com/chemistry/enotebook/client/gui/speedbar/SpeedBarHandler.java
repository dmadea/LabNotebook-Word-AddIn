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
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.gui.esig.SignatureHandler;
import com.chemistry.enotebook.client.print.gui.PrintRequest;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ReactionSchemeModel;
import com.chemistry.enotebook.experiment.datamodel.page.InvalidNotebookRefException;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.datamodel.reaction.Reaction;
import com.chemistry.enotebook.experiment.datamodel.reaction.ReactionCache;
import com.chemistry.enotebook.experiment.datamodel.reaction.ReactionScheme;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import com.chemistry.enotebook.experiment.utils.xml.JDomUtils;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.session.delegate.SessionTokenDelegate;
import com.chemistry.enotebook.session.security.UserData;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.SwingWorker;
import com.common.chemistry.codetable.CodeTableCache;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * 
 * 
 */
public class SpeedBarHandler extends CommonHandler { 
	
	private static final Log log = LogFactory.getLog(SpeedBarHandler.class);
	
	private static SessionTokenDelegate session; 
	
	private TreePath tool_tip_node = null;
	
	private JPopupMenu jPopupMenuUser;
	private JMenuItem jMenuItemNewNotebook;
	private JPopupMenu jPopupMenuNotebook;
	private JPopupMenu jPopupMenuExperimentGroup;
	private JMenu jMenuNewExperimentNb, jMenuNewExperimentGroup, jMenuNewExperimentEx;
	private JMenuItem jMenuItemNewNbExperiment, jMenuItemNewNbExperiment4p, jMenuItemNewNbExperiment4cr;
	private JMenuItem jMenuItemNewGroupExperiment, jMenuItemNewGroupExperiment4p, jMenuItemNewGroupExperiment4cr;
	private JMenuItem jMenuItemNewExExperiment, jMenuItemNewExExperiment4p, jMenuItemNewExExperiment4cr;
	private JMenuItem jMenuItemPrintBlockExperiment;
	private SpeedBarPagePopupMenu jPopupMenuExperiment;
	private JMenuItem jMenuItemLoadExperiment;
	private JMenuItem jMenuItemReOpenExperiment;
	private JMenuItem jMenuItemDeleteExperiment;
	private JMenuItem jMenuItemRepeatExperiment;
	private JMenuItem jMenuItemPrintExperiment;
	private JMenuItem jMenuItemExperimentComplete;
	private JMenuItem jMenuItemResubmitExperiment;
	private JMenuItem jMenuItemViewDocument;
	private JPopupMenu jPopupMenuContents;
	private JMenuItem jMenuItemLoadContents;
	private JMenuItem jMenuItemPrintContents;

	public SpeedBarHandler(JTree tree, String type) {
		super(tree, type);

		////// USER FULLNAME RIGHT-CLICK MENU
		jPopupMenuUser = new JPopupMenu();
		jMenuItemNewNotebook = new JMenuItem("Create New Notebook");
		jMenuItemNewNotebook.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.BOOK_STATUS_CLOSED));
		jMenuItemNewNotebook.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				NewNotebookActionPerformed();
			}
		});
		jPopupMenuUser.add(jMenuItemNewNotebook);
		
		if (MasterController.getUser().isSuperUser()) {
			jPopupMenuUser.addSeparator();
			JMenuItem item = new JMenuItem("Add Supervisee");
			item.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					updateSuperviseeList();
				}
			});
			jPopupMenuUser.add(item);
		}


		////// TABLE OF CONTENTS RIGHT-CLICK MENU
		jPopupMenuContents = new JPopupMenu();
		jMenuItemLoadContents = new JMenuItem("Load Table of Contents");
		jMenuItemLoadContents.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_OPEN));
		jMenuItemLoadContents.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				LoadContentsActionPerformed();
			}
		});
		jPopupMenuContents.add(jMenuItemLoadContents);
		jMenuItemPrintContents = new JMenuItem("Print Table of Contents");
		jMenuItemPrintContents.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.PRINT));
		jMenuItemPrintContents.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				PrintContentsActionPerformed();
			}
		});
		jPopupMenuContents.add(jMenuItemPrintContents);


		////// NOTEBOOK RIGHT-CLICK MENU
		jPopupMenuNotebook = new JPopupMenu();
		jMenuNewExperimentNb = new JMenu("Create New Experiment");
		jPopupMenuNotebook.add(jMenuNewExperimentNb);
		jMenuItemNewNbExperiment4cr = new JMenuItem("Conception Record");
		jMenuItemNewNbExperiment4cr.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.NEW_CONCEPT));
		jMenuItemNewNbExperiment4cr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newConceptionActionPerformed();
			}
		});
		
		jMenuItemNewNbExperiment = new JMenuItem("Singleton Chemistry");
		jMenuItemNewNbExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.NEW_SINGLETON));
		jMenuItemNewNbExperiment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newExperimentActionPerformed();
			}
		});
		
		jMenuItemNewNbExperiment4p = new JMenuItem("Parallel Chemistry");
		jMenuItemNewNbExperiment4p.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.NEW_PARALLEL));
		jMenuItemNewNbExperiment4p.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newExperiment4pActionPerformed();
			}
		});
		jMenuItemNewNbExperiment4p.setEnabled(MasterController.isParallelCreateEnabled());
		
		jMenuNewExperimentNb.add(jMenuItemNewNbExperiment);		// Singleton Chemistry
		jMenuNewExperimentNb.add(jMenuItemNewNbExperiment4p);	// Parallel Chemistry
		jMenuNewExperimentNb.add(jMenuItemNewNbExperiment4cr);	// Conception Record

		
		////// EXPERIMENT GROUP RIGHT-CLICK MENU
		jPopupMenuExperimentGroup = new JPopupMenu();
		jMenuNewExperimentGroup = new JMenu("Create New Experiment");
		jPopupMenuExperimentGroup.add(jMenuNewExperimentGroup);
		jMenuItemNewGroupExperiment4cr = new JMenuItem("Conception Record");
		jMenuItemNewGroupExperiment4cr.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.NEW_CONCEPT));
		jMenuItemNewGroupExperiment4cr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newConceptionActionPerformed();
			}
		});
		jMenuItemNewGroupExperiment = new JMenuItem("Singleton Chemistry");
		jMenuItemNewGroupExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.NEW_SINGLETON));
		jMenuItemNewGroupExperiment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newExperimentActionPerformed();
			}
		});
		jMenuItemNewGroupExperiment4p = new JMenuItem("Parallel Chemistry");
		jMenuItemNewGroupExperiment4p.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.NEW_PARALLEL));
		jMenuItemNewGroupExperiment4p.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newExperiment4pActionPerformed();
			}
		});
		jMenuItemNewGroupExperiment4p.setEnabled(MasterController.isParallelCreateEnabled());
		
		jMenuNewExperimentGroup.add(jMenuItemNewGroupExperiment);		// Singleton Chemistry		
		jMenuNewExperimentGroup.add(jMenuItemNewGroupExperiment4p);	// Parallel Chemistry
		jMenuNewExperimentGroup.add(jMenuItemNewGroupExperiment4cr);	// Conception Record
		
		jMenuItemPrintBlockExperiment = new JMenuItem("Print this Folder of Experiments");
		jMenuItemPrintBlockExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.PRINT));
		jMenuItemPrintBlockExperiment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				PrintExperimentBlockActionPerformed();
			}
		});
		jPopupMenuExperimentGroup.add(jMenuItemPrintBlockExperiment);
jMenuItemPrintBlockExperiment.setVisible(false);

		
		////// EXPERIMENT RIGHT-CLICK MENU
		jPopupMenuExperiment = new SpeedBarPagePopupMenu();
		jMenuItemLoadExperiment = new JMenuItem("Open this Experiment");
		jMenuItemLoadExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_OPEN));
		jMenuItemLoadExperiment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				OpenExperimentActionPerformed(false);
			}
		});
		jPopupMenuExperiment.add(jMenuItemLoadExperiment);
						
		jMenuItemReOpenExperiment = new JMenuItem("Re-Open this Experiment");
		jMenuItemReOpenExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_OPEN));
		jMenuItemReOpenExperiment.setVisible(false);
		jMenuItemReOpenExperiment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				OpenExperimentActionPerformed(true);
			}
		});
		jPopupMenuExperiment.add(jMenuItemReOpenExperiment);
		
		jMenuNewExperimentEx = new JMenu("Create New Experiment");
		jPopupMenuExperiment.add(jMenuNewExperimentEx);
		jMenuItemNewExExperiment4cr = new JMenuItem("Conception Record");
		jMenuItemNewExExperiment4cr.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.NEW_CONCEPT));
		jMenuItemNewExExperiment4cr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newConceptionActionPerformed();
			}
		});
		jMenuItemNewExExperiment = new JMenuItem("Singleton Chemistry");
		jMenuItemNewExExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.NEW_SINGLETON));
		jMenuItemNewExExperiment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newExperimentActionPerformed();
			}
		});
		jMenuItemNewExExperiment4p = new JMenuItem("Parallel Chemistry");
		jMenuItemNewExExperiment4p.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.NEW_PARALLEL));
		jMenuItemNewExExperiment4p.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newExperiment4pActionPerformed();
			}
		});
		jMenuItemNewExExperiment4p.setEnabled(MasterController.isParallelCreateEnabled());
		
		jMenuNewExperimentEx.add(jMenuItemNewExExperiment);		// Singleton Chemistry		
		jMenuNewExperimentEx.add(jMenuItemNewExExperiment4p);	// Parallel Chemistry
		jMenuNewExperimentEx.add(jMenuItemNewExExperiment4cr);	// Conception Record
	
		jMenuItemDeleteExperiment = new JMenuItem("Delete this Experiment");
		jMenuItemDeleteExperiment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				deleteExperimentActionPerformed();
			}
		});
		if (MasterController.getUser().isSuperUser()) jPopupMenuExperiment.add(jMenuItemDeleteExperiment);
		
		jMenuItemRepeatExperiment = new JMenuItem("Repeat this Experiment");
		jMenuItemRepeatExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.COPY));
		jMenuItemRepeatExperiment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				RepeatExperimentActionPerformed();
			}
		});
		jPopupMenuExperiment.add(jMenuItemRepeatExperiment);
		jMenuItemPrintExperiment = new JMenuItem("Print this Experiment");
		jMenuItemPrintExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.PRINT));
		jMenuItemPrintExperiment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				PrintExperimentActionPerformed();
			}
		});
		jPopupMenuExperiment.add(jMenuItemPrintExperiment);
		
		jPopupMenuExperiment.addSeparator();
		
		// Set as Active experiment
		// Set as Completed experiment
		jMenuItemExperimentComplete = new JMenuItem("Mark this Experiment Complete");
		jMenuItemExperimentComplete.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_COMPLETE));
		jMenuItemExperimentComplete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ChangeExperimentStatusActionPerformed();
			}
		});
		jPopupMenuExperiment.add(jMenuItemExperimentComplete);
		jMenuItemResubmitExperiment = new JMenuItem("Resubmit Record for Signatures");
		jMenuItemResubmitExperiment.setVisible(false);
		jMenuItemResubmitExperiment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ResubmitExperimentActionPerformed();
			}
		});
		jPopupMenuExperiment.add(jMenuItemResubmitExperiment);
		jMenuItemViewDocument = new JMenuItem("View Record Submitted");
		jMenuItemViewDocument.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.VIEW_ARCHIVE));
		jMenuItemViewDocument.setVisible(false);
		jMenuItemViewDocument.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ViewDocumentActionPerformed();
			}
		});
		jPopupMenuExperiment.add(jMenuItemViewDocument);
	}

	public void mouseEntered(MouseEvent evt) {
	}

	public void mouseExited(MouseEvent evt) {
	}

	public void mouseReleased(MouseEvent evt) {
		mouseAction(evt);
	}
	
	public void mousePressed(MouseEvent evt) {
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
				NotebookUser user = MasterController.getUser();
				if (mouseNode.getUserObject() instanceof SpeedBarUser) { // if its a user
					try {
						// Determine if New Notebook is allowed
						String prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_NEW_NOTEBOOK);
						if ((prop != null && prop.equalsIgnoreCase("true")) || user.isSuperUser()) {
							String sbUser = ((SpeedBarUser) mouseNode.getUserObject()).getUserID();
							jMenuItemNewNotebook.setEnabled(user.getNTUserID().equals(sbUser) || user.isSuperUser());
							jPopupMenuUser.show(evt.getComponent(), evt.getX(), evt.getY());
							updatePosition(jPopupMenuUser);
						}
					} catch (Exception e) { /* ignored */
					}
				} else if (mouseNode.getUserObject() instanceof SpeedBarNotebook) { // if its a notebook or page group
					String sbUser = ((SpeedBarNotebook) mouseNode.getUserObject()).getUserID();
					jMenuNewExperimentNb.setEnabled(user.getNTUserID().equals(sbUser) || user.isSuperUser());
					jPopupMenuNotebook.show(evt.getComponent(), evt.getX(), evt.getY());
					updatePosition(jPopupMenuNotebook);
				} else if (mouseNode.getUserObject() instanceof SpeedBarPageGroup) {
					String sbUser = ((SpeedBarPageGroup) mouseNode.getUserObject()).getUserID();
					jMenuNewExperimentGroup.setEnabled(user.getNTUserID().equals(sbUser) || user.isSuperUser());
					jPopupMenuExperimentGroup.show(evt.getComponent(), evt.getX(), evt.getY());
					updatePosition(jPopupMenuExperimentGroup);
				} else if (mouseNode.getUserObject() instanceof SpeedBarPage) { // if its an experiment
					SpeedBarPage sbp = (SpeedBarPage) mouseNode.getUserObject();
					String sbUser = sbp.getUserID();
					try {
						boolean validPage = NotebookPageUtil.isValidNotebookRef(user.getPreference(NotebookUser.PREF_LastNBRef));
						boolean forParallelWhetherRepeatPropertyEnabled = !NotebookPage.TYPE_PARALLEL.equals(sbp.getPageType()) || MasterController.isParallelRepeatEnabled(); 
						jMenuItemRepeatExperiment.setEnabled(
								validPage &&
								sbp.isLatestVersion() &&
								forParallelWhetherRepeatPropertyEnabled
						);
					} catch (UserPreferenceException e) {
						jMenuItemRepeatExperiment.setEnabled(false);
						CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), e);
					}
					// jMenuItemRemoveBookmark.setEnabled(user.getNTUserID().equals(sbUser) || user.isSuperUser());
					jMenuNewExperimentEx.setEnabled(user.getNTUserID().equals(sbUser) || user.isSuperUser());
					jMenuItemReOpenExperiment.setEnabled(user.getNTUserID().equals(sbUser));
					jMenuItemExperimentComplete.setEnabled(user.getNTUserID().equals(sbUser) && !sbp.isCompleteInProgress());
					
					if (sbp.getUserID().equals(MasterController.getUser().getNTUserID())) {
						if (!sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_OPEN)) {
							// Need version process to be by latest version and not status == open.  
							// Problem with update of page status when versioned page happens while submitting.
							if (sbp.isLatestVersion() && sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_OPEN) == false) {
								jMenuItemExperimentComplete.setText("Version this Experiment");
							} else {
								jMenuItemExperimentComplete.setText("Experiment is Complete");
								jMenuItemExperimentComplete.setEnabled(false);
							}
						} else {
							jMenuItemExperimentComplete.setText("Mark this Experiment Complete");
						}
						
						if (sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED) ||
							sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED_REOPEN)) {
							jMenuItemResubmitExperiment.setText("Resubmit Record for Signatures");  
							jMenuItemResubmitExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_SIGNING));
							jMenuItemResubmitExperiment.setVisible(true);
						} else if (sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_ARCHIVE_FAILED)) {
							jMenuItemResubmitExperiment.setText("Resubmit Record for Archiving");
							jMenuItemResubmitExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_PARALLEL_ARCHIVED));
							jMenuItemResubmitExperiment.setVisible(true);
						} else {
							jMenuItemResubmitExperiment.setVisible(false);
						}
					} else if (user.isSuperUser() || user.isSupervisorFor(sbp.getUserID())) {
						String msg = (user.isSupervisorFor(sbp.getUserID())) ? "(Supervisor)" : "(Superuser)";
						if (!sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_OPEN))
							jMenuItemExperimentComplete.setText("Experiment is Complete");
						else {
							jMenuItemExperimentComplete.setText("Mark this Experiment Complete " + msg);
							jMenuItemExperimentComplete.setEnabled(true);
						}
						
						if (sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_SUBMITTED) ||
							sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED) ||
							sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED_REOPEN)) {
							jMenuItemResubmitExperiment.setText("Resubmit Record for Signatures " + msg);
							jMenuItemResubmitExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_SIGNING));
							jMenuItemResubmitExperiment.setVisible(true);
						} else if (sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_ARCHIVING) ||
								   sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_ARCHIVE_FAILED)) {
							jMenuItemResubmitExperiment.setText("Resubmit Record for Archiving " + msg);
							jMenuItemResubmitExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_PARALLEL_ARCHIVED));
							jMenuItemResubmitExperiment.setVisible(true);
						} else
							jMenuItemResubmitExperiment.setVisible(false);
					} else {
						if (!sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_OPEN))
							jMenuItemExperimentComplete.setText("Experiment is Complete");
						else
							jMenuItemExperimentComplete.setText("Experiment is Open");
						
						jMenuItemResubmitExperiment.setVisible(false);
					}
					
					if (sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_SUBMITTED) || 
							sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED) ||
							sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_SIGNED) ||
							sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED_REOPEN)) {
						jMenuItemViewDocument.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_SIGNING));
						jMenuItemViewDocument.setText("View Record Submitted for Signatures");
						jMenuItemViewDocument.setVisible(true);
					} else if (sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_ARCHIVING) ||
							   sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_ARCHIVE_FAILED)) {
						jMenuItemViewDocument.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_PARALLEL_ARCHIVED));
						jMenuItemViewDocument.setText("View Record Submitted for Archival");
						jMenuItemViewDocument.setVisible(true);
					} else if (sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_ARCHIVED)) {
						jMenuItemViewDocument.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_PARALLEL_ARCHIVED));
						jMenuItemViewDocument.setText("View Record Archived");
						jMenuItemViewDocument.setVisible(true);
					} else
						jMenuItemViewDocument.setVisible(false);
					
					if(sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED)) {
						jMenuItemLoadExperiment.setText("Open this Experiment for View");
						jMenuItemReOpenExperiment.setVisible(true);
					} else {
						jMenuItemLoadExperiment.setText("Open this Experiment");
						jMenuItemReOpenExperiment.setVisible(false);
					}
					
					if (tree.getSelectionCount() > 1)
						jMenuItemPrintExperiment.setText("Print these Experiments");
					else
						jMenuItemPrintExperiment.setText("Print this Experiment");
					
					if(!NotebookPage.TYPE_PARALLEL.equalsIgnoreCase(sbp.getPageType()) || MasterController.isParallelViewEnabled()) {
						jPopupMenuExperiment.setSpeedBarPage(sbp);
						jPopupMenuExperiment.show(evt.getComponent(), evt.getX(), evt.getY());
						updatePosition(jPopupMenuExperiment);
					}
				} else if (mouseNode.getUserObject() instanceof SpeedBarContents) {
					jPopupMenuContents.show(evt.getComponent(), evt.getX(), evt.getY());
					updatePosition(jPopupMenuContents);
				}
			}
		}
		MasterController.getGUIComponent().refreshIcons();
	}

	private ImageIcon iconForStatus(String pageStatus, String pageType, boolean isInCompliance)
	{
	    if (pageType.equals(NotebookPage.TYPE_CONCEPTION)) {
		    if (pageStatus.equals(CeNConstants.PAGE_STATUS_OPEN))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_CONCEPT_OPEN, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_COMPLETE))
				return CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_CONCEPT_COMPLETE);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_SUBMITTED))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_CONCEPT_SIGNING, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_SIGNING))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_CONCEPT_SIGNING, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_SIGNED))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_CONCEPT_SIGNED, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_CONCEPT_SIGN_ERROR, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_ARCHIVING))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_CONCEPT_ARCHIVING, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_ARCHIVE_FAILED))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_CONCEPT_ARCHIVE_ERROR, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_ARCHIVED))
				return CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_CONCEPT_ARCHIVED);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED_REOPEN))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_CONCEPT_OPEN, isInCompliance);
	    } else if (pageType.equals(NotebookPage.TYPE_MED_CHEM)) {
		    if (pageStatus.equals(CeNConstants.PAGE_STATUS_OPEN))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_OPEN, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_COMPLETE))
				return CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_COMPLETE);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_SUBMITTED))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_SIGNING, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_SIGNING))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_SIGNING, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_SIGNED))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_SIGNED, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_SIGN_ERROR, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_ARCHIVING))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_ARCHIVING, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_ARCHIVE_FAILED))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_ARCHIVE_ERROR, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_ARCHIVED))
				return CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_ARCHIVED);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED_REOPEN))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_OPEN, isInCompliance);
	    } else if (pageType.equals(NotebookPage.TYPE_PARALLEL)) {
		    if (pageStatus.equals(CeNConstants.PAGE_STATUS_OPEN))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_PARALLEL_OPEN, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_COMPLETE))
				return CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_PARALLEL_COMPLETE);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_SUBMITTED))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_PARALLEL_SIGNING, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_SIGNING))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_PARALLEL_SIGNING, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_SIGNED))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_PARALLEL_SIGNED, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_PARALLEL_SIGN_ERROR, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_ARCHIVING))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_PARALLEL_ARCHIVING, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_ARCHIVE_FAILED))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_PARALLEL_ARCHIVE_ERROR, isInCompliance);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_ARCHIVED))
				return CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_PARALLEL_ARCHIVED);
			else if (pageStatus.equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED_REOPEN))
				return CenIconFactory.getComplianceImageIcon(CenIconFactory.SpeedBar.PS_PARALLEL_OPEN, isInCompliance);
	    }	
	    
	    return null;
	}
	
	public TreeCellRenderer getNewCellRenderer() {
		return new SpeedBarCellRenderer();
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
	
	public TreePath getToolTipNode() {
		return this.tool_tip_node;
	}

	
	private class SpeedBarCellRenderer	extends DefaultTreeCellRenderer
	{
		private static final long serialVersionUID = 3275146463956129844L;

		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
		 */
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus)
		{
			super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

			ImageIcon sbIcon = null;
			String sbToolTip = null;

			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
			Object usrObj = treeNode.getUserObject();

			if (leaf  &&  usrObj instanceof SpeedBarPage) {
				SpeedBarPage p = (SpeedBarPage)usrObj;

				if (p.getPageStatus().equals(CeNConstants.PAGE_STATUS_OPEN) || p.getPageStatus().equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED_REOPEN))
					sbIcon = iconForStatus(p.getPageStatus(), p.getPageType(), p.isInCompliance()); 
				else if (p.getPageStatus().equals(CeNConstants.PAGE_STATUS_COMPLETE))
					sbIcon = iconForStatus(p.getPageStatus(), p.getPageType(), p.isInCompliance()); 
				else if (p.getPageStatus().equals(CeNConstants.PAGE_STATUS_SUBMITTED))
					sbIcon = iconForStatus(p.getPageStatus(), p.getPageType(), p.isInCompliance());
				else if (p.getPageStatus().equals(CeNConstants.PAGE_STATUS_SIGNING))
					sbIcon = iconForStatus(p.getPageStatus(), p.getPageType(), p.isInCompliance());
				else if (p.getPageStatus().equals(CeNConstants.PAGE_STATUS_SIGNED))
				     sbIcon = iconForStatus(p.getPageStatus(), p.getPageType(), p.isInCompliance()); 
				else if (p.getPageStatus().equals(CeNConstants.PAGE_STATUS_ARCHIVING))
					sbIcon = iconForStatus(p.getPageStatus(), p.getPageType(), p.isInCompliance()); 
				else if (p.getPageStatus().equals(CeNConstants.PAGE_STATUS_ARCHIVED))
					sbIcon = iconForStatus(p.getPageStatus(), p.getPageType(), p.isInCompliance()); 
				else if (p.getPageStatus().equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED))
					sbIcon = iconForStatus(p.getPageStatus(), p.getPageType(), p.isInCompliance()); 
				else if (p.getPageStatus().equals(CeNConstants.PAGE_STATUS_ARCHIVE_FAILED))
					sbIcon = iconForStatus(p.getPageStatus(), p.getPageType(), p.isInCompliance()); 
				else
					sbIcon = CenIconFactory.getImageIcon(CenIconFactory.General.UNKNOWN_DOCUMENT);
				
				if(NotebookPage.TYPE_PARALLEL.equalsIgnoreCase(p.getPageType())) {
					this.setEnabled(MasterController.isParallelViewEnabled());
				} 
				
				if (hasFocus  &&  !jPopupMenuExperiment.isVisible()) {
					sbToolTip = CreateToolTip(p, null);
				}

			} else if (usrObj instanceof SpeedBarNotebook) {
				if (expanded)
					sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.BOOK_STATUS_OPEN);
				else
					sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.BOOK_STATUS_CLOSED);
			} else if (usrObj instanceof SpeedBarPageGroup)
				sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PAGES);
			else if (usrObj instanceof SpeedBarUser)
				sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.USER);
			else if (usrObj instanceof SpeedBarSite)
				sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.SITE);
			else if (usrObj instanceof SpeedBarContents)
				sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.CONTENTS);
			if (sbIcon != null)
				setIcon(sbIcon);
			else if (!usrObj.toString().equals("Pending..."))
				log.error("Icon creation failed for " + usrObj.toString());

			setToolTipText(sbToolTip);
			return this;
		}
	}
	
	private String CreateToolTip(SpeedBarPage sbp, String image) {
		String lineSeparator = System.getProperty("line.separator");
		String lineTab = "    ";
		
		StringBuffer buff = new StringBuffer();		
		boolean addPunc = false;

		buff.append(sbp.getNotebook() + "-" + sbp.getPage() + " v" + sbp.getVersion() + lineTab + lineSeparator);
		 
		if (StringUtils.isNotBlank(sbp.getSubject())) {
			buff.append(sbp.getSubject() + lineTab + lineSeparator);
		}

		String date = sbp.getCreationDate();
		
		try {
			date = CommonUtils.toTimestampString(new Timestamp(Date.parse(date)));
		} catch (Exception e) {
			// Nothing to do, show date as is
		}
		
		if (StringUtils.isNotBlank(date)) { 
			buff.append(date);  
			addPunc = true; 
		}
		
		String status = sbp.getPageStatus();
		
		if (StringUtils.isNotBlank(status)) {
			if (addPunc) {
				buff.append(", ");
			}
			
//			if (StringUtils.equals(status, CeNConstants.PAGE_STATUS_SIGNED)) {
//				status = CeNConstants.PAGE_STATUS_SIGNING;
//			}
			
			buff.append(status);
			addPunc = true;
		}
		
		if (addPunc) {
			buff.append(lineTab + lineSeparator);
		}

		if (StringUtils.isNotBlank(sbp.getProject())) {
			String projDesc = null;
			
			try {
				projDesc = CodeTableCache.getCache().getProjectsDescription(sbp.getProject());
			} catch (Exception e) {
				// Do nothing
			}
			
			if (StringUtils.isBlank(projDesc)) { 
				projDesc = sbp.getProject();
			} else {
				projDesc = sbp.getProject() + " - " + projDesc;
			}
			
			buff.append(projDesc);
			buff.append(lineTab + lineSeparator);
		}
		
		addPunc = false;
		
		String type = sbp.getPageType();
		
		if (StringUtils.isNotBlank(type)) {
			buff.append("Type: " + type);
		}

 		return buff.toString();
	}

	
	public SpeedBarNodeInterface speedBarNavigateTo(String site, String users_fullname, NotebookRef nbRef, int version) {
		NotebookUser user = MasterController.getUser();
		// final int groupSize = NotebookPageUtil.NB_PAGE_GROUP_SIZE;
		String nb = nbRef.getNbNumber();
		String ex = nbRef.getNbPage();
		String group = SpeedBarPageGroup.getGroupFromExperiment(ex);
		if (version > 1) {
			ex = ex + " v" + version;
		}
		TreePath path = null;
		if (sbType.equals(SpeedBarModel.SB_MY_BOOKMARKS)) {
			String[] expandPath = { user.getFullName(), nb, group, ex };
			path = expandPath(expandPath, false);
		} else {
			try {
				if (session == null) {
					session = new SessionTokenDelegate();
				}
				if (!StringUtils.contains(users_fullname, ',')) {
					users_fullname = session.getUser(users_fullname).getFullUserName();
				}
			} catch (Exception e) {
			}
			path = expandPath(new String[] { site, users_fullname, nb, group, ex }, false);
			if (path == null) {
				try {
					site = CodeTableCache.getCache().getSiteDescription(site);
				} catch (Exception e) {
				}
				path = expandPath(new String[] { site, users_fullname, nb, group, ex }, false);
			}
		}
		if (path != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			return (SpeedBarNodeInterface) node.getUserObject();
		} else {
			return null;
		}
	}

	/**
	 * @param page -
	 *            Notebook Page being added
	 * @param expandFlag -
	 *            Flag indicating that tree should be expanded to this page after addition
	 * 
	 * Method to add an Experiment/Page to the tree. This is to give the impression to the user that the page exists. should the
	 * page not be saved on reload of speedbar, the page will not appear.
	 */
	public void addPage(NotebookPage page, boolean expandFlag) {
		TreePath path = null;
		try {
			String site = CodeTableCache.getCache().getSiteDescription(page.getSiteCode());
			String user = MasterController.getUser().getFullName();
			String rootName = ((TreeNode) tree.getModel().getRoot()).toString();
			if (rootName.equals(SpeedBarModel.SB_ALL_SITES))
				path = findByName(new String[] { rootName, site, user, page.getNotebookRef().getNbNumber() });
			else
				path = findByName(new String[] { rootName, user, page.getNotebookRef().getNbNumber() });
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		if (path != null) {
			model.expandPathImmediate(path);
			DefaultMutableTreeNode newNode = null;
			DefaultMutableTreeNode notebookNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			SpeedBarNotebook sbNotebook = (SpeedBarNotebook) notebookNode.getUserObject();
			DefaultMutableTreeNode parentNode = notebookNode;
			if (parentNode.getChildCount() == 0) {
				// Need to add Notebook Table of Contents Node
				newNode = new DefaultMutableTreeNode(new SpeedBarContents(sbNotebook.getSite(), sbNotebook.getSiteCode(),
						sbNotebook.getUser(), sbNotebook.getUserID(), page.getNotebookRef().getNbNumber()), false);
				model.addNode(parentNode, newNode, 0);
				// Need to add First Page Grouping
				newNode = new DefaultMutableTreeNode(new SpeedBarPageGroup(sbNotebook.getSite(), sbNotebook.getSiteCode(),
						sbNotebook.getUser(), sbNotebook.getUserID(), page.getNotebookRef().getNbNumber(), 1,
						NotebookPageUtil.NB_PAGE_GROUP_SIZE), true);
				model.addNode(parentNode, newNode, 1);
				parentNode = newNode;
				// Need to add first group Table of Contents Node
				newNode = new DefaultMutableTreeNode(new SpeedBarContents(sbNotebook.getSite(), sbNotebook.getSiteCode(),
						sbNotebook.getUser(), sbNotebook.getUserID(), page.getNotebookRef().getNbNumber(), 1,
						NotebookPageUtil.NB_PAGE_GROUP_SIZE), false);
				model.addNode(parentNode, newNode, 0);
			} else if (parentNode.getChildCount() == 1) {
				// Need to add First Page Grouping
				newNode = new DefaultMutableTreeNode(new SpeedBarPageGroup(sbNotebook.getSite(), sbNotebook.getSiteCode(),
						sbNotebook.getUser(), sbNotebook.getUserID(), page.getNotebookRef().getNbNumber(), 1,
						NotebookPageUtil.NB_PAGE_GROUP_SIZE), true);
				model.addNode(parentNode, newNode, 1);
				parentNode = newNode;
				// Need to add first group Table of Contents Node
				newNode = new DefaultMutableTreeNode(new SpeedBarContents(sbNotebook.getSite(), sbNotebook.getSiteCode(),
						sbNotebook.getUser(), sbNotebook.getUserID(), page.getNotebookRef().getNbNumber(), 1,
						NotebookPageUtil.NB_PAGE_GROUP_SIZE), false);
				model.addNode(parentNode, newNode, 0);
			} else if (parentNode.getChildCount() >= 2) {
				if (page.getVersion() == 1) { // Grab the top page grouping node, new experiments go there
					parentNode = (DefaultMutableTreeNode) parentNode.getChildAt(1);
				} else { // Need to figure out which page group this goes in
					DefaultMutableTreeNode tNode = null;
					String group = SpeedBarPageGroup.getGroupFromExperiment(page.getNotebookRef().getNbPage());
					for (int i = 0; i < parentNode.getChildCount() && tNode == null; i++)
						if (parentNode.getChildAt(i).toString().equals(group))
							tNode = (DefaultMutableTreeNode) parentNode.getChildAt(i);
					if (tNode != null)
						parentNode = tNode;
					else
						parentNode = (DefaultMutableTreeNode) parentNode.getChildAt(1);
				}
				SpeedBarPageGroup groupNode = (SpeedBarPageGroup) parentNode.getUserObject();
				// Need to verify group is not full, if full need to create a
				// new Page group
				int experimentNum = (new Integer(page.getNotebookRef().getNbPage())).intValue();
				if (experimentNum > groupNode.getStopPage()) {
					// move back up to notebook
					parentNode = notebookNode;
					// Need to add First Page Grouping
					groupNode = new SpeedBarPageGroup(sbNotebook.getSite(), sbNotebook.getSiteCode(), sbNotebook.getUser(),
								sbNotebook.getUserID(), page.getNotebookRef().getNbNumber(), 
								SpeedBarPageGroup.getGroupStartFromExperiment("" + experimentNum), 
								SpeedBarPageGroup.getGroupEndFromExperiment("" + experimentNum));
					newNode = new DefaultMutableTreeNode(groupNode, true);
					model.addNode(parentNode, newNode, 1);
					parentNode = newNode;
					// Need to add Table of Contents Node for the Grouping
					newNode = new DefaultMutableTreeNode(new SpeedBarContents(sbNotebook.getSite(), sbNotebook.getSiteCode(),
								sbNotebook.getUser(), sbNotebook.getUserID(), page.getNotebookRef().getNbNumber(), 
								groupNode.getStartPage(), groupNode.getStopPage()), false);
					model.addNode(parentNode, newNode, 0);
				} else {
					// Need to verify that the page group has a table of
					// contents node
					if (parentNode.getChildCount() == 0) {
						TreePath tPath = path.pathByAddingChild(parentNode);
						model.expandPathImmediate(tPath);
						if (parentNode.getChildCount() == 0) {
							newNode = new DefaultMutableTreeNode(new SpeedBarContents(sbNotebook.getSite(), 
									sbNotebook.getSiteCode(), sbNotebook.getUser(), sbNotebook.getUserID(), 
									page.getNotebookRef().getNbNumber(), SpeedBarPageGroup.getGroupStartFromExperiment("" + experimentNum),
									SpeedBarPageGroup.getGroupEndFromExperiment("" + experimentNum)), false);
							model.addNode(parentNode, newNode, 0);
						}
					}
				}
			}
			// Add the Page group to the Path
			path = path.pathByAddingChild(parentNode);
			ReactionScheme intendedScheme = null;
			ReactionCache cache = page.getReactionCache();
			if (cache != null) {
				Map<String, Reaction> rxns = cache.getReactions();
				Reaction rxn = null;
				if (!rxns.isEmpty()) {
					rxn = (Reaction) rxns.get(rxns.keySet().iterator().next());
					intendedScheme = rxn.getRepresentativeReactionStep().getReactionScheme();
				}
			}
			// Need to add page
			ArrayList<Object> PageData = new ArrayList<Object>();
			PageData.add(page.getNotebookRef().getNbPage());
			PageData.add(new BigDecimal(page.getVersion()));
			PageData.add(page.getLatestVersion());
			PageData.add(page.getLaf());
			PageData.add(page.getStatus());
			PageData.add(page.getCreationDate());
			PageData.add(page.getSubject());
			PageData.add(page.getProjectCode());
			if (intendedScheme == null || intendedScheme.getViewSketch() == null || intendedScheme.getViewSketch().length == 0)
				PageData.add(null);
			else
				PageData.add(intendedScheme.getViewSketch());
			PageData.add(page.getModificationDate());
			DefaultMutableTreeNode pageNode = new DefaultMutableTreeNode(new SpeedBarPage(PageData, sbNotebook.getSite(),
					sbNotebook.getSiteCode(), sbNotebook.getUser(), sbNotebook.getUserID(), page.getNotebookRef().getNbNumber()),
					false);
			// Find the location to insert this node so that the experiments are sorted
			int i;
			String newExp = page.getNotebookRef().getNbPage();
			for (i = 0; i < parentNode.getChildCount(); i++) {
				SpeedBarNodeInterface sbi = (SpeedBarNodeInterface) ((DefaultMutableTreeNode) parentNode.getChildAt(i)).getUserObject();
				if (sbi instanceof SpeedBarPage) {
					if (newExp.compareTo(((SpeedBarPage) sbi).getPage()) >= 0) {
						model.addNode(parentNode, pageNode, i);
						break;
					}
				}
			}
			if (i >= parentNode.getChildCount())
				model.addNode(parentNode, pageNode, parentNode.getChildCount());
			// Add the Page to the Path
			path = path.pathByAddingChild(pageNode);
			if (expandFlag) {
				tree.scrollPathToVisible(path);
				setSelectedPath(path);
			}
		}
	}

	/**
	 * @param site -
	 *            Site name for this Notebook
	 * @param user -
	 *            User's full name
	 * @param notebook -
	 *            Notebook to be added
	 * @param expandFlag -
	 *            Flag indicating that tree should be expanded to this notebook after addition
	 * 
	 * Method to add a notebook to the tree.
	 */
	public void addNotebook(String site, String user, String notebook, boolean expandFlag) {
		TreePath path = null;
		String rootName = ((TreeNode) tree.getModel().getRoot()).toString();
		if (rootName.equals(SpeedBarModel.SB_ALL_SITES))
			path = findByName(new String[] { rootName, site, user });
		else
			path = findByName(new String[] { rootName, user });
		if (path != null) {
			model.expandPathImmediate(path);
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			SpeedBarUser sbUser = (SpeedBarUser) parentNode.getUserObject();
			// Need to add Notebook Node
			ArrayList<String> nbData = new ArrayList<String>();
			nbData.add(notebook);
			SpeedBarNotebook sbNotebook = new SpeedBarNotebook(nbData, sbUser.getSite(), sbUser.getSiteCode(), 
									sbUser.getUser(), sbUser.getUserID());
			sbNotebook.setExpanded();
			DefaultMutableTreeNode nbNode = new DefaultMutableTreeNode(sbNotebook, true);
			// Find the location to insert this node so that the notebooks are sorted
			int i;
			for (i = 0; i < parentNode.getChildCount(); i++) {
				SpeedBarNodeInterface sbi = (SpeedBarNodeInterface) ((DefaultMutableTreeNode) parentNode.getChildAt(i)).getUserObject();
				if (sbi instanceof SpeedBarNotebook) {
					if (notebook.compareTo(((SpeedBarNotebook) sbi).getNotebook()) >= 0) {
						model.addNode(parentNode, nbNode, i);
						break;
					}
				}
			}
			if (i >= parentNode.getChildCount())
				model.addNode(parentNode, nbNode, parentNode.getChildCount());
			// Add the Notebook to the Path
			path = path.pathByAddingChild(nbNode);
			// Need to add Table of Contents Node
			DefaultMutableTreeNode contentNode = new DefaultMutableTreeNode(new SpeedBarContents(sbUser.getSite(), 
								sbUser.getSiteCode(), sbUser.getUser(), sbUser.getUserID(), notebook), false);
			model.addNode(nbNode, contentNode, 0);
			// Add the Table of Contents to the Path (for a new Notebook
			// this will be the default selected)
			path = path.pathByAddingChild(nbNode);
			if (expandFlag) {
				tree.scrollPathToVisible(path);
				setSelectedPath(path);
			}
		}
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

	public SpeedBarNodeInterface getLastSelectedUserObject() {
		if (lastClickedPath != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) lastClickedPath.getLastPathComponent();
			return (SpeedBarNodeInterface) node.getUserObject();
		} else
			return null;
	}

	private void setSelectedPath(TreePath path) {
		lastClickedPath = path;
		tree.setSelectionPath(lastClickedPath);
	}

	protected void OpenTableContentsActionPerformed() {
		LoadContentsActionPerformed();
	}
	
	private void LoadContentsActionPerformed() {
		SpeedBarContents sbc = (SpeedBarContents) getLastSelectedUserObject();
		if (sbc != null) {
			MasterController.getGuiController().openContents(sbc.getSiteCode(), sbc.getNotebook(), sbc.getStartExperiment(), sbc.getEndExperiment());
		}
	}

	private void PrintContentsActionPerformed() {
		SpeedBarContents sbc = (SpeedBarContents) getLastSelectedUserObject();
		if (sbc != null)
			MasterController.getGuiController().printContents(sbc.getSiteCode(), sbc.getNotebook(), sbc.getStartExperiment(),
					sbc.getEndExperiment());
	}

	private void NewNotebookActionPerformed() {
		SpeedBarUser sbu = (SpeedBarUser) getLastSelectedUserObject();
		if (sbu != null)
		{
			MasterController.getGuiController().createNewNotebook(sbu.getSite(), sbu.getSiteCode(), sbu.getUser(), sbu.getUserID());
			final NotebookUser loginUser = MasterController.getUser();
			getModel().refreshNotebooksForUser(loginUser.getSiteCode(), loginUser.getNTUserID());
			fireNewNotebookCreatedEvent();
		}
	}

	private void fireNewNotebookCreatedEvent() {
		MasterController.getGuiController().getGUIComponent().loadSubMenusForCreateNewExp();
	}

	private void newExperimentActionPerformed() {
		MasterController.getGuiController().createNewSingletonExperiment();
	}
	private void newExperiment4pActionPerformed() {
		MasterController.getGuiController().createNewExperiment4PCeNNoSPID("");
	}

	private void newConceptionActionPerformed() {
		MasterController.getGuiController().createNewConception();
	}
	private void deleteExperimentActionPerformed(){
		int confirm = JOptionPane.showConfirmDialog(MasterController.getGUIComponent(), "Are you sure you want to permanently delete this Experiment?");
		if (confirm==JOptionPane.YES_OPTION){
			//to do the delete
		}
		
	}
	protected void OpenExperimentActionPerformed(boolean submitFailedReopen) {
		SpeedBarNodeInterface sel[] = getSelectedUserObjects();
		if (sel != null) {
			for (int i = 0; i < sel.length; i++) {
				if (sel[i] instanceof SpeedBarPage) {
					SpeedBarPage sbp = (SpeedBarPage) sel[i];
					if (sbp.getPageType().equals(CeNConstants.PAGE_TYPE_MED_CHEM)) {
						if (sbp.isLoadable()){//same as the the 1.2
							MasterController.getGuiController().openPCeNExperiment(sbp.getSiteCode(), sbp.getNotebook(), sbp.getPage(), sbp.getVersion(), submitFailedReopen);
						}
					}
						// TODO:
					//	JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
					//			"Warning: Combi-Chem pages cannot be retrieved at this time.", "Experiment Retrieve",
					//			JOptionPane.ERROR_MESSAGE);
					else if(sbp.getPageType().equals(CeNConstants.PAGE_TYPE_PARALLEL)) {
						if(MasterController.isParallelViewEnabled()) {
							MasterController.getGuiController().openPCeNExperiment(sbp.getSiteCode(), sbp.getNotebook(), sbp.getPage(), sbp.getVersion(), submitFailedReopen);
						}
					}
					else if(sbp.getPageType().equals(CeNConstants.PAGE_TYPE_CONCEPTION)) {
						if (sbp.isLoadable()){//same as the the 1.2
							MasterController.getGuiController().openPCeNExperiment(sbp.getSiteCode(), sbp.getNotebook(), sbp.getPage(), sbp.getVersion(), submitFailedReopen);
						}
					}
					else{
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
											"Warning: Unknow Page Type", "Experiment Retrieve",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}
	
	private void ReOpenExperimentActionPerformed() {
		OpenExperimentActionPerformed(true);
		
	}

	private void RepeatExperimentActionPerformed() {
		SpeedBarPage sbp = (SpeedBarPage) getLastSelectedUserObject();
		if (sbp != null)
			MasterController.getGuiController().repeatExperiment(sbp.getSiteCode(), sbp.getUser(), sbp.getNotebook(),
					sbp.getPage(), sbp.getVersion());
	}

	private void PrintExperimentBlockActionPerformed() {
		SpeedBarNodeInterface sel[] = getSelectedUserObjects();
		if (sel != null) {
			ArrayList<Object[]> list = new ArrayList<Object[]>();
			for (int i = 0; i < sel.length; i++) {
				if (sel[i] instanceof SpeedBarPageGroup) {
					SpeedBarPageGroup sbpg = (SpeedBarPageGroup) sel[i];
					ArrayList<String> pages = new ArrayList<String>();
					for (int j = sbpg.getStartPage(); j <= sbpg.getStopPage(); j++)
						pages.add("" + j);
					list.add(new Object[] { sbpg.getSiteCode(), sbpg.getNotebook(), pages });
				}
			}
			if (list.size() > 0)
				MasterController.getGuiController().printPageGroups(list);
		}
	}

	private void PrintExperimentActionPerformed() {
		SpeedBarNodeInterface sel[] = getSelectedUserObjects();
		if (sel != null) {
			ArrayList<PrintRequest> list = new ArrayList<PrintRequest>();
			NotebookRef ref = null;
			StringBuffer errorPages = new StringBuffer();
			for (int i = 0; i < sel.length; i++) {
				if (sel[i] instanceof SpeedBarPage) {
					SpeedBarPage sbp = (SpeedBarPage) sel[i];
					try
					{
						ref = new NotebookRef(sbp.getNotebook(), sbp.getPage());
						
						list.add(new PrintRequest ( sbp.getSiteCode(), 
													ref, 
													new Integer(sbp.getVersion()), 
													sbp.getPageType() ));
					}
					catch (InvalidNotebookRefException e)
					{
						if(errorPages.length() > 0) { errorPages.append("\n"); }
						errorPages.append("Notebook: " + sbp.getNotebook() + "-" + sbp.getPage() );
						log.error("Failed ot create print request for notebook reference: " + sbp.getNotebook() + "-" + sbp.getPage(), e);
					}
				}
			}
			if(errorPages.length() > 0) {
				CeNErrorHandler.getInstance().logErrorMsg(null, 
				                                          "Failed to create print request for invalid notebook reference!\n" + errorPages.toString(),
				                                          "Possible failure in print request", 
				                                          JOptionPane.ERROR_MESSAGE);

			}
			if (list.size() > 0)
				MasterController.getGuiController().printExperiments(list);
		}
	}

	private void ChangeExperimentStatusActionPerformed() {
		SpeedBarPage sbp = jPopupMenuExperiment.getSpeedBarPage();
		if (sbp == null) {
			sbp = (SpeedBarPage) getLastSelectedUserObject();
		}
		if (sbp != null) {
			if (sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_OPEN)) {
				sbp.setCompleteInProgress(true);
				MasterController.getGuiController().markCompleteExperiment(sbp.getSite(), sbp.getUser(), sbp.getNotebook(), sbp.getPage(), sbp.getVersion(), sbp);
			} else {
				MasterController.getGuiController().versionExperiment(sbp.getSiteCode(), sbp.getSite(), sbp.getUser(), sbp.getNotebook(), sbp.getPage(), sbp.getVersion());
			}
		}
	}

	private void ResubmitExperimentActionPerformed() {
		final SpeedBarPage sbp = (SpeedBarPage) getLastSelectedUserObject();
		if (sbp != null) {
			if (sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_SUBMITTED)
					|| sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED)
					|| sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED_REOPEN)) {
				doRepublishInSwingWorker(sbp);
			} else if (sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_ARCHIVING)
					|| sbp.getPageStatus().equals(CeNConstants.PAGE_STATUS_ARCHIVE_FAILED)) {
				doRearchiveInSwingWorker(sbp);
			}
		}
	}
	
	private void doRepublishInSwingWorker(final SpeedBarPage sbp) {
		SwingWorker worker = new SwingWorker() {
			public Object construct() {
				SignatureHandler sigHandler = new SignatureHandler();
				sigHandler.republishDocument(sbp, sbp.getSiteCode(), sbp.getNotebook(), sbp.getPage(), sbp.getVersion());
				return null;
			}
			public void finished() {}
	    };
	    worker.start();
	}
	
	private void doRearchiveInSwingWorker(final SpeedBarPage sbp) {
		SwingWorker worker = new SwingWorker() {
			public Object construct() {
				SignatureHandler sigHandler = new SignatureHandler();
				sigHandler.rearchiveDocument(sbp.getSiteCode(), sbp.getNotebook(), sbp.getPage(), sbp.getVersion());
				return null;
			}
			public void finished() {}
	    };
	    worker.start();
	}

	private void ViewDocumentActionPerformed() {
		SpeedBarPage sbp = (SpeedBarPage) getLastSelectedUserObject();
		if (sbp != null) {
			SignatureHandler sigHandler = new SignatureHandler();
			sigHandler.viewPDF(sbp.getSiteCode(), sbp.getNotebook(), sbp.getPage(), sbp.getVersion());
		}
	}

	public SpeedBarNodeInterface getNotebookIfOnlyOneExists() {
		SpeedBarNodeInterface result = null;
		int count = tree.getRowCount();
		ArrayList<SpeedBarNodeInterface> speedBarNotebookList = new ArrayList<SpeedBarNodeInterface>();
		for (int i=0; i<count; i++)
		{
			TreePath tempPath = tree.getPathForRow(i);
			SpeedBarNodeInterface tempSpeedBarNodeInterface = (SpeedBarNodeInterface)((DefaultMutableTreeNode) tempPath.getLastPathComponent()).getUserObject();
			if (tempSpeedBarNodeInterface instanceof SpeedBarNotebook)
				speedBarNotebookList.add(tempSpeedBarNodeInterface);
		}
		if (speedBarNotebookList.size() == 1) {
			result = speedBarNotebookList.get(0);
		}
		return result;
	}

	public void addPage(NotebookPageModel page, boolean expandFlag) {
		TreePath path = null;
		try {
			String site = CodeTableCache.getCache().getSiteDescription(page.getSiteCode());
			String user = MasterController.getUser().getFullName();
			String rootName = ((TreeNode) tree.getModel().getRoot()).toString();
			if (rootName.equals(SpeedBarModel.SB_ALL_SITES))
				path = findByName(new String[] { rootName, site, user, page.getNbRef().getNbNumber() });
			else
				path = findByName(new String[] { rootName, user, page.getNbRef().getNbNumber() });
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		if (path != null) {
			model.expandPathImmediate(path);
			DefaultMutableTreeNode newNode = null;
			DefaultMutableTreeNode notebookNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			SpeedBarNotebook sbNotebook = (SpeedBarNotebook) notebookNode.getUserObject();
			DefaultMutableTreeNode parentNode = notebookNode;
			if (parentNode.getChildCount() == 0) {
				// Need to add Notebook Table of Contents Node
				newNode = new DefaultMutableTreeNode(new SpeedBarContents(sbNotebook.getSite(), sbNotebook.getSiteCode(),
						sbNotebook.getUser(), sbNotebook.getUserID(), page.getNbRef().getNbNumber()), false);
				model.addNode(parentNode, newNode, 0);
				// Need to add First Page Grouping
				newNode = new DefaultMutableTreeNode(new SpeedBarPageGroup(sbNotebook.getSite(), sbNotebook.getSiteCode(),
						sbNotebook.getUser(), sbNotebook.getUserID(), page.getNbRef().getNbNumber(), 1,
						NotebookPageUtil.NB_PAGE_GROUP_SIZE), true);
				model.addNode(parentNode, newNode, 1);
				parentNode = newNode;
				// Need to add first group Table of Contents Node
				newNode = new DefaultMutableTreeNode(new SpeedBarContents(sbNotebook.getSite(), sbNotebook.getSiteCode(),
						sbNotebook.getUser(), sbNotebook.getUserID(), page.getNbRef().getNbNumber(), 1,
						NotebookPageUtil.NB_PAGE_GROUP_SIZE), false);
				model.addNode(parentNode, newNode, 0);
			} else if (parentNode.getChildCount() == 1) {
				// Need to add First Page Grouping
				newNode = new DefaultMutableTreeNode(new SpeedBarPageGroup(sbNotebook.getSite(), sbNotebook.getSiteCode(),
						sbNotebook.getUser(), sbNotebook.getUserID(), page.getNbRef().getNbNumber(), 1,
						NotebookPageUtil.NB_PAGE_GROUP_SIZE), true);
				model.addNode(parentNode, newNode, 1);
				parentNode = newNode;
				// Need to add first group Table of Contents Node
				newNode = new DefaultMutableTreeNode(new SpeedBarContents(sbNotebook.getSite(), sbNotebook.getSiteCode(),
						sbNotebook.getUser(), sbNotebook.getUserID(), page.getNbRef().getNbNumber(), 1,
						NotebookPageUtil.NB_PAGE_GROUP_SIZE), false);
				model.addNode(parentNode, newNode, 0);
			} else if (parentNode.getChildCount() >= 2) {
				if (page.getVersion() == 1) { // Grab the top page grouping node, new experiments go there
					parentNode = (DefaultMutableTreeNode) parentNode.getChildAt(1);
				} else { // Need to figure out which page group this goes in
					DefaultMutableTreeNode tNode = null;
					String group = SpeedBarPageGroup.getGroupFromExperiment(page.getNbRef().getNbPage());
					for (int i = 0; i < parentNode.getChildCount() && tNode == null; i++)
						if (parentNode.getChildAt(i).toString().equals(group))
							tNode = (DefaultMutableTreeNode) parentNode.getChildAt(i);
					if (tNode != null)
						parentNode = tNode;
					else
						parentNode = (DefaultMutableTreeNode) parentNode.getChildAt(1);
				}
				SpeedBarPageGroup groupNode = (SpeedBarPageGroup) parentNode.getUserObject();
				// Need to verify group is not full, if full need to create a
				// new Page group
				int experimentNum = (new Integer(page.getNbRef().getNbPage())).intValue();
				if (experimentNum > groupNode.getStopPage()) {
					// move back up to notebook
					parentNode = notebookNode;
					// Need to add First Page Grouping
					groupNode = new SpeedBarPageGroup(sbNotebook.getSite(), sbNotebook.getSiteCode(), sbNotebook.getUser(),
								sbNotebook.getUserID(), page.getNbRef().getNbNumber(), 
								SpeedBarPageGroup.getGroupStartFromExperiment("" + experimentNum), 
								SpeedBarPageGroup.getGroupEndFromExperiment("" + experimentNum));
					newNode = new DefaultMutableTreeNode(groupNode, true);
					model.addNode(parentNode, newNode, 1);
					parentNode = newNode;
					// Need to add Table of Contents Node for the Grouping
					newNode = new DefaultMutableTreeNode(new SpeedBarContents(sbNotebook.getSite(), sbNotebook.getSiteCode(),
								sbNotebook.getUser(), sbNotebook.getUserID(), page.getNbRef().getNbNumber(), 
								groupNode.getStartPage(), groupNode.getStopPage()), false);
					model.addNode(parentNode, newNode, 0);
				} else {
					// Need to verify that the page group has a table of
					// contents node
					if (parentNode.getChildCount() == 0) {
						TreePath tPath = path.pathByAddingChild(parentNode);
						model.expandPathImmediate(tPath);
						if (parentNode.getChildCount() == 0) {
							newNode = new DefaultMutableTreeNode(new SpeedBarContents(sbNotebook.getSite(), 
									sbNotebook.getSiteCode(), sbNotebook.getUser(), sbNotebook.getUserID(), 
									page.getNbRef().getNbNumber(), SpeedBarPageGroup.getGroupStartFromExperiment("" + experimentNum),
									SpeedBarPageGroup.getGroupEndFromExperiment("" + experimentNum)), false);
							model.addNode(parentNode, newNode, 0);
						}
					}
				}
			}
			// Add the Page group to the Path
			path = path.pathByAddingChild(parentNode);
			ReactionSchemeModel intendedScheme = page.getSummaryReactionStep().getRxnScheme();
/*			ReactionCache cache = page.getReactionStep(0).getRxnScheme();
			if (cache != null) {
				Map rxns = cache.getReactions();
				Reaction rxn = null;
				if (!rxns.isEmpty()) {
					rxn = (Reaction) rxns.get(rxns.keySet().iterator().next());
					intendedScheme = rxn.getRepresentativeReactionStep().getReactionScheme();
				}
			}*/
			// Need to add page
			ArrayList<Object> PageData = new ArrayList<Object>();
			PageData.add(page.getNbRef().getNbPage());
			PageData.add(new BigDecimal(page.getVersion()));
			PageData.add(page.getLatestVersion());
			PageData.add(page.getPageType());
			PageData.add(page.getStatus());
			PageData.add(page.getCreationDate());
			PageData.add(page.getSubject());
			PageData.add(page.getProjectCode());
			if (intendedScheme == null || intendedScheme.getViewSketch() == null || intendedScheme.getViewSketch().length == 0)
				PageData.add(null);
			else
				PageData.add(intendedScheme.getViewSketch());
			PageData.add(page.getModificationDate());
            final SpeedBarPage speedBarPage = new SpeedBarPage(PageData, sbNotebook.getSite(),
                    sbNotebook.getSiteCode(), sbNotebook.getUser(), sbNotebook.getUserID(), page.getNbRef().getNbNumber());
            speedBarPage.setPageModel(page);
            DefaultMutableTreeNode pageNode = new DefaultMutableTreeNode(speedBarPage,
					false);
			// Find the location to insert this node so that the experiments are sorted
			int i;
			String newExp = page.getNbRef().getNbPage();
			for (i = 0; i < parentNode.getChildCount(); i++) {
				SpeedBarNodeInterface sbi = (SpeedBarNodeInterface) ((DefaultMutableTreeNode) parentNode.getChildAt(i)).getUserObject();
				if (sbi instanceof SpeedBarPage) {
					if (newExp.compareTo(((SpeedBarPage) sbi).getPage()) >= 0) {
						model.addNode(parentNode, pageNode, i);
						break;
					}
				}
			}
			if (i >= parentNode.getChildCount())
				model.addNode(parentNode, pageNode, parentNode.getChildCount());
			// Add the Page to the Path
			path = path.pathByAddingChild(pageNode);
			if (expandFlag) {
				tree.scrollPathToVisible(path);
				setSelectedPath(path);
			}
		}
	}
	
	private void updateSuperviseeList()
	{
		final SpeedBarUser sbu = (SpeedBarUser)getLastSelectedUserObject();
		if (sbu != null) {
			SwingWorker worker = new SwingWorker() {
				public Object construct() {
					try { 
						SessionTokenDelegate sd = new SessionTokenDelegate();
						UserData ud = sd.getUser(sbu.getUserID());			
						String prefs = ud.getXmlMetaData();
						Document userPrefs = JDomUtils.getDocFromString(prefs);

						String supervisees = JDomUtils.getPrefFromDoc(userPrefs, NotebookUser.PREF_SupervisorFor);
						if (supervisees == null) supervisees = "";

						supervisees = (String) JOptionPane.showInputDialog(MasterController.getGUIComponent(),
								"Enter a comma separated list of NT IDs:", "Assign Supervisee for " + sbu.getUser(),
								JOptionPane.QUESTION_MESSAGE, null, null, supervisees);
						if (supervisees != null) {
							supervisees = supervisees.replaceAll(" ", "").toUpperCase();
							JDomUtils.setPrefInDoc(userPrefs, NotebookUser.PREF_SupervisorFor, supervisees);
							StringWriter writer = new StringWriter();
					    	XMLOutputter outp = new XMLOutputter();
							outp.output(userPrefs, writer);
							prefs = writer.toString();
							ud.setXmlMetaData(prefs);
							sd.updateUser(ud);
						}				
			        } catch(Exception ex) {
						CeNErrorHandler.getInstance().logExceptionMsg(null, ex);
			        } 
			        return null;
				}

				public void finished() {
				}
			};
			worker.start();
		}
	}
	
	private class SpeedBarPagePopupMenu extends JPopupMenu {
		
		private static final long serialVersionUID = 3620774441682725742L;
		
		private SpeedBarPage speedBarPage;
				
		public void setSpeedBarPage(SpeedBarPage speedBarPage) {
			this.speedBarPage = speedBarPage;
		}
		
		public SpeedBarPage getSpeedBarPage() {
			return speedBarPage;
		}
	}
}
