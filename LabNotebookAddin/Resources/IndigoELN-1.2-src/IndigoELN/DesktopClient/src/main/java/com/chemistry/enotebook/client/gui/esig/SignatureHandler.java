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
/**
 * 
 */
package com.chemistry.enotebook.client.gui.esig;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.controller.scheduler.TimerStatusHandler;
import com.chemistry.enotebook.client.datamodel.speedbar.SpeedBarNodeInterface;
import com.chemistry.enotebook.client.datamodel.speedbar.SpeedBarPage;
import com.chemistry.enotebook.client.gui.Gui;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.controller.GuiController;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.esig.delegate.SignatureDelegate;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import com.chemistry.enotebook.signature.classes.TemplateVO;
import com.chemistry.enotebook.signature.exceptions.SignatureServiceUnavailableException;
import com.chemistry.enotebook.storage.SignaturePageVO;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.util.ExceptionUtils;
import com.chemistry.enotebook.utils.CeNJobProgressHandler;
import com.chemistry.enotebook.utils.CommonUtils;
import com.common.chemistry.codetable.CodeTableCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * 
 * 
 */
public class SignatureHandler {
	private static final Log log = LogFactory.getLog(SignatureHandler.class);
	private SignatureDelegate sigObj = null;
	private List<TemplateVO> templates = null;
	private TemplateVO lastSelectedTemplate = null;

	private boolean init() {
		if (sigObj == null) {
			try {
				sigObj = new SignatureDelegate();

				if (!isAvailable()) {
					JOptionPane.showMessageDialog(	MasterController.getGUIComponent(),
													"Signature Service is currently unavailable, please try again later.",
													"Signature Service",
													JOptionPane.ERROR_MESSAGE);
					return false;
				} else
					return true;
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), e);
				return false;
			}
		} else
			return true;
	}

	private void changNbPageStatus(NotebookPageModel pageModel, SpeedBarPage sbp, String status, boolean onlyRefresh) {
		// Change the status and record the completion date
		pageModel.setStatus(status);
		boolean success = false;
		if (!onlyRefresh) {
			try {
				// completion date should have been set before now.				
				// there is no indication of !Open before setting the completion date here.				
//								pageModel.setCompletionDateAsTimestamp(new Timestamp(new Date().getTime()));
				// Store the Page status
				StorageDelegate sDel = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
				sDel.updateNotebookPageStatus(pageModel.getSiteCode(), pageModel.getNbRef().getNbRef(), pageModel.getVersion(), status);
				success = true;
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}

		Gui GUI = MasterController.getGUIComponent();
		if (success && !pageModel.isModelChanged()) {
			GUI.refreshIcons();
			SpeedBarNodeInterface sbi = GUI.getMySpeedBar().speedBarNavigateTo(sbp.getSite(), 
			                                                                   sbp.getUser(), 
			                                                                   pageModel.getNbRef(), 	
			                                                                   pageModel.getVersion());
			if (sbi != null && sbi instanceof SpeedBarPage) {
				((SpeedBarPage) sbi).setPageStatus(status);
			}
			GUI.getMySpeedBar().refreshCurrentNode();
			sbi = GUI.getAllSitesSpeedBar().speedBarNavigateTo(	sbp.getSite(),
																sbp.getUser(),
																pageModel.getNbRef(),
																pageModel.getVersion());
			if (sbi != null && sbi instanceof SpeedBarPage) {
				((SpeedBarPage) sbi).setPageStatus(status);
			}
			GUI.getAllSitesSpeedBar().refreshCurrentNode();
		} else {
			GUI.refreshIcons();
		}
		pageModel.setChanging(false);
		sbp.setCompleteInProgress(false);
	}

	private String publishDocument(NotebookPageModel page, byte[] buffer, String docName, Date modDate) {
		String result = null;
		try {
			result = sigObj.publishDocument(page, buffer, docName, modDate, lastSelectedTemplate, MasterController.getUser());
		} catch (Exception e) {
			log.error("Error publishing document:", e);
		}
		SignatureTimerHandler.startSignatureTimer(true);
		return result;
	}

	private void saveExperiment(SpeedBarPage sbp) {
		try {
			NotebookPageModel pageModel = MasterController.getGuiController()
			.getLoadedPageGui(sbp.getSiteCode(), new NotebookRef(sbp.getNotebook() +
																	"-" + sbp.getPage()), sbp.getVersion()).getPageModel();
			if (pageModel.isModelChanged()) {
				MasterController.getGuiController().saveExperiment(pageModel);
			}
		} catch (Exception e) {
			log.error("Problem loading page: " +
						sbp.getNotebook() + "-" + sbp.getPage() + "  Probably logged before now.  Message = " +
						e.getLocalizedMessage());
		}
	}

	//TODO it is the same code as complete in GuiController - lets remove it?
	public boolean republishDocument(SpeedBarPage sbp, String siteCode, String notebook, String page, int version)
	{
		if (!init()) return false;

		int status = canDoSubmissions();
		if (status != 1) {
			if (status == -2)
				JOptionPane.showMessageDialog(	MasterController.getGUIComponent(),
												"Submission to Signature Service is currently unavailable, please try again later.",
												"Experiment Republish",
												JOptionPane.ERROR_MESSAGE);
			return false;
		}

		String nbRefStr = notebook + "-" + page;
		String progressStatus = "Re-Publishing " + nbRefStr + " for Signatures ...";
		CeNJobProgressHandler.getInstance().addItem(progressStatus);

		saveExperiment(sbp);

		String pageStatus;

		// Get the page status to ensure it is SUBMIT FAIL
		StorageDelegate sDel = null;
		byte[] buffer = null;
		try {
			sDel = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());

			SignaturePageVO vo = sDel.getNotebookPageStatus(siteCode, nbRefStr, version);
			if (vo == null
					|| !(vo.getStatus().equals(CeNConstants.PAGE_STATUS_SUBMITTED) ||
							vo.getStatus().equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED) ||
							vo.getStatus().equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED_REOPEN))) {
				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
				return false;
			}
			pageStatus = vo.getStatus();
			buffer = sDel.generateExperimentPDF(siteCode, nbRefStr, version, TimeZone.getDefault().getID());
		} catch (Exception e) {
			GuiController.errorCompleting(null, true, e);
			CeNJobProgressHandler.getInstance().removeItem(progressStatus);
			return false;
		}

		if (buffer != null &&
			buffer.length > 0) {
			TemplateVO template = null;
			if (!hasTemplates()) {
				Object[] options = { "OK", "Goto Signature Service" };
				int result = JOptionPane.showOptionDialog(MasterController.getGUIComponent(),
						"Electronic Signature is enabled, however, you have no Signature Templates defined\n" +
						"that contain at least one signature block named 'Author'.\n\n" +
						"Please go to Signature Service and define a template that meets these criteria then try again.\n" +
						"If you do not wish to use eSigs, you can go to Tools | Options and disable it.", "eSignature Re-Submit", 
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, null);
				if (result == JOptionPane.NO_OPTION)
					launchSAFESign();
				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
				return false;
			}

			template = selectTemplate();
			if (template == null) {
				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
				return false;
			}

				Date date = new Date();
				try {
					date = NotebookPageUtil.getLocalDate(sbp.getModifiedDate());
				} catch (Exception e) {
					// failed to get local date so we fall back to the one we have.
				}
				
				NotebookRef notebookRef = null;
				try {
					notebookRef = new NotebookRef(nbRefStr);
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, "Failed to create notebook ref for string value: " + nbRefStr + "\nPublishing Failed.", e);
				}
				
				if (notebookRef != null) {
				notebookRef.setVersion(version);
				try {
				NotebookPageModel pageModel = sDel.getNotebookPageExperimentInfo(notebookRef, siteCode, MasterController.getUser().getSessionIdentifier());
				
				String docName = makeDocName(notebook, page, version) + ".pdf";
				
				String result = publishDocument(pageModel, buffer, docName, date);

					sDel.updateNotebookPage(pageModel.getSiteCode(),
											pageModel.getNotebookRefWithoutVersion(),
											pageModel.getVersion(),
											CeNConstants.PAGE_STATUS_SUBMITTED,
											buffer,
											result);
					sDel.storeExperimentPDF(siteCode, nbRefStr, version, buffer);
					changNbPageStatus(pageModel, sbp, CeNConstants.PAGE_STATUS_SUBMITTED, true);
					//TODO					
					pageModel = sDel.getNotebookPageExperimentInfo(pageModel.getNbRef(), pageModel.getSiteCode(), MasterController.getUser().getSessionIdentifier());
					
					if (result != null && template != null) {
						TimerStatusHandler.getInstance().addSignatureTask(pageModel.getNotebookRefAsString());
					}
					  
					pageStatus = sDel.getNotebookPageCompleteStatus(pageModel.getSiteCode(), pageModel.getNotebookRefAsString(), pageModel.getVersion());
					
					TimerStatusHandler.updateSpeedBarWithStatus(pageStatus, pageModel.getSiteCode(), MasterController.getUser().getFullName(), pageModel.getNbRef(), pageModel.getVersion());
					
					if (pageModel.getSignatureUrl() != null && shouldLaunchUrl()) {
							fixAndLaunchUrl(pageModel.getSignatureUrl());
					
				}
				} catch (Throwable e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
				}
			}
		} else {
			// Nothing to resubmit so unmark complete so user can start
			// again
			pageStatus = CeNConstants.PAGE_STATUS_OPEN;
			try {
				sDel.updateNotebookPageStatus(siteCode, nbRefStr, version, pageStatus, -1);
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}

		// Update Speedbar Icon(s)
		Gui GUI = MasterController.getGUIComponent();
		String site = "";
		NotebookRef nbRef = null;
		try {
			nbRef = new NotebookRef(nbRefStr);
		} catch (Exception e) {
			// intentionally ignored
		}
		try {
			site = CodeTableCache.getCache().getSiteDescription(siteCode);
		} catch (Exception e) {
			// intentionally ignored
		}
		String user = MasterController.getUser().getFullName();
		GUI.refreshIcons();
		SpeedBarNodeInterface sbi = null;
		sbi = GUI.getMySpeedBar().speedBarNavigateTo(site, user, nbRef, version);
		if (sbi != null &&
			sbi instanceof SpeedBarPage)
			((SpeedBarPage) sbi).setPageStatus(pageStatus);
		GUI.getMySpeedBar().refreshCurrentNode();

		sbi = GUI.getAllSitesSpeedBar().speedBarNavigateTo(site, user, nbRef, version);
		if (sbi != null &&
			sbi instanceof SpeedBarPage)
			((SpeedBarPage) sbi).setPageStatus(pageStatus);
		GUI.getAllSitesSpeedBar().refreshCurrentNode();
		CeNJobProgressHandler.getInstance().removeItem(progressStatus);
		// Start the Timer that will monitor whether there are any Signature
		// Changes.
		// SignatureTimerHandler.startSignatureTimer(true);
		return pageStatus.equals(CeNConstants.PAGE_STATUS_SUBMITTED);
	}

	/*
	public boolean archiveDocument(String siteCode, String nbRefStr, int version, int ussiKey) {
		if (!init())
			return false;
		String pageStatus = CeNConstants.PAGE_STATUS_ARCHIVING;
		String progressStatus = "Archiving " + nbRefStr + " ...";
		CeNJobProgressHandler.getInstance().addItem(progressStatus);
		byte[] buffer = null;
		try {
			buffer = sigObj.getFile(ussiKey, SignatureDelegate.FILE_STANDARD);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			CeNJobProgressHandler.getInstance().removeItem(progressStatus);
			return false;
		}

		// Save the Signed PDF into the database for later access
		StorageDelegate sDel = null;
		try {
			sDel = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
			sDel.storeExperimentPDF(siteCode, nbRefStr, version, buffer);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			CeNJobProgressHandler.getInstance().removeItem(progressStatus);
			return false;
		}

		// Issue Archive Request to Signature Service
		try {
			NotebookPageModel pageModel = sDel.getNotebookPageExperimentInfo(new NotebookRef(nbRefStr), siteCode);			
			sigObj.archiveFile(MasterController.getUser().getUserSessionToken().getSmtpEmail(), ussiKey, pageModel, MasterController.getUser());
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			pageStatus = CeNConstants.PAGE_STATUS_ARCHIVE_FAILED;
		}
		// Store the Page status
		try {
			sDel.updateNotebookPageStatus(siteCode, nbRefStr, version, pageStatus);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}

		// Update Speedbar Icon(s)
		Gui GUI = MasterController.getGUIComponent();
		String site = "";
		NotebookRef nbRef = null;
		try {
			nbRef = new NotebookRef(nbRefStr);
		} catch (Exception e) {
		}
		try {
			site = CodeTableCache.getCache().getSiteDescription(siteCode);
		} catch (Exception e) {
		}
		String user = MasterController.getUser().getFullName();
		GUI.refreshIcons();
		SpeedBarNodeInterface sbi = null;
		sbi = GUI.getMySpeedBar().speedBarNavigateTo(site, user, nbRef, version);
		if (sbi != null &&
			sbi instanceof SpeedBarPage)
			((SpeedBarPage) sbi).setPageStatus(pageStatus);
		GUI.getMySpeedBar().refreshCurrentNode();
		sbi = GUI.getAllSitesSpeedBar().speedBarNavigateTo(site, user, nbRef, version);
		if (sbi != null &&
			sbi instanceof SpeedBarPage)
			((SpeedBarPage) sbi).setPageStatus(pageStatus);
		GUI.getAllSitesSpeedBar().refreshCurrentNode();
		CeNJobProgressHandler.getInstance().removeItem(progressStatus);
		return pageStatus.equals(CeNConstants.PAGE_STATUS_ARCHIVING);
	}
	*/

	public boolean rearchiveDocument(String siteCode, String notebook, String page, int version) {
		if (!init())
			return false;
		String pageStatus = CeNConstants.PAGE_STATUS_ARCHIVE_FAILED;
		String nbRefStr = notebook + "-" + page;
		String progressStatus = "Re-Archiving " + nbRefStr + " ...";
		CeNJobProgressHandler.getInstance().addItem(progressStatus);
		// Get the page status to ensure it is ARCHIVE FAIL
		StorageDelegate sDel = null;
		String ussiKey = "";
		try {
			sDel = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
			SignaturePageVO vo = sDel.getNotebookPageStatus(siteCode, nbRefStr, version);
			if (vo == null
					|| !(vo.getStatus().equals(CeNConstants.PAGE_STATUS_ARCHIVING) || vo.getStatus().equals(
							CeNConstants.PAGE_STATUS_ARCHIVE_FAILED))) {
				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
				return false;
			}
			ussiKey = vo.getUssiKey();
			pageStatus = vo.getStatus();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			CeNJobProgressHandler.getInstance().removeItem(progressStatus);
			return false;
		}

		NotebookPageModel pageModel = null;
		
		// Issue Archive Request to Signature Service
		try {
			pageModel = sDel.getNotebookPageExperimentInfo(new NotebookRef(nbRefStr), siteCode, MasterController.getUser().getSessionIdentifier());
			sigObj.archiveFile(MasterController.getUser().getNTUserID(), ussiKey, pageModel, MasterController.getUser());
			pageStatus = CeNConstants.PAGE_STATUS_ARCHIVING;
		} catch (Exception e) {
			// Check if this was a glitch
			if (ExceptionUtils.getStackTrace(e).indexOf("The document has already been archived") > 0) {
				try {
					// Check if it is actually in the archive
					if (sigObj.isDocumentArchived(pageModel.getUssiKey())) {
						// Yes, just continue the process
						pageStatus = CeNConstants.PAGE_STATUS_ARCHIVING;
					} else {
						CeNErrorHandler.getInstance().logExceptionMsg(null, e);
						CeNJobProgressHandler.getInstance().removeItem(progressStatus);
						return false;
					}
				} catch (Exception e2) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e2);
					CeNJobProgressHandler.getInstance().removeItem(progressStatus);
					return false;
				}
			} else {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
				return false;
			}
		}
		// Store the Page status
		try {
			sDel.updateNotebookPageStatus(siteCode, nbRefStr, version, pageStatus);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		// Update Speedbar Icon(s)
		Gui GUI = MasterController.getGUIComponent();
		String site = "";
		NotebookRef nbRef = null;
		try {
			nbRef = new NotebookRef(nbRefStr);
		} catch (Exception e) {
			// intentionally ignored
		}
		try {
			site = CodeTableCache.getCache().getSiteDescription(siteCode);
		} catch (Exception e) {
			// intentionally ignored
		}
		String user = MasterController.getUser().getFullName();
		GUI.refreshIcons();
		SpeedBarNodeInterface sbi = null;
		sbi = GUI.getMySpeedBar().speedBarNavigateTo(site, user, nbRef, version);
		if (sbi != null &&
			sbi instanceof SpeedBarPage)
			((SpeedBarPage) sbi).setPageStatus(pageStatus);
		GUI.getMySpeedBar().refreshCurrentNode();
		sbi = GUI.getAllSitesSpeedBar().speedBarNavigateTo(site, user, nbRef, version);
		if (sbi != null &&
			sbi instanceof SpeedBarPage)
			((SpeedBarPage) sbi).setPageStatus(pageStatus);
		GUI.getAllSitesSpeedBar().refreshCurrentNode();
		CeNJobProgressHandler.getInstance().removeItem(progressStatus);
		// Start the Timer that will monitor whether there are any Signature Changes.
		// SignatureTimerHandler.startSignatureTimer(true);
		return pageStatus.equals(CeNConstants.PAGE_STATUS_ARCHIVING);
	}

	/*
	public void documentArchived(String siteCode, String nbRefStr, int version, int ussiKey) {
		if (!init())
			return;
		// Get Signed Document (Flat Version)
		byte[] buffer = null;
		try {
			buffer = sigObj.getFile(ussiKey, SignatureDelegate.FILE_FLATTENED);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			return;
		}
		// Save the Signed (flat version) PDF into the database for later access
		try {
			StorageDelegate sDel = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
			sDel.storeExperimentPDF(siteCode, nbRefStr, version, buffer);
			sDel.updateNotebookPageStatus(siteCode, nbRefStr, version, CeNConstants.PAGE_STATUS_ARCHIVED);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			return;
		}
		// Update Speedbar Icon
		Gui GUI = MasterController.getGUIComponent();
		String site = "";
		NotebookRef nbRef = null;
		try {
			nbRef = new NotebookRef(nbRefStr);
		} catch (Exception e) {
		}
		try {
			site = CodeTableCache.getCache().getSiteDescription(siteCode);
		} catch (Exception e) {
		}
		String user = MasterController.getUser().getFullName();
		GUI.refreshIcons();
		SpeedBarNodeInterface sbi = null;
		sbi = GUI.getMySpeedBar().speedBarNavigateTo(site, user, nbRef, version);
		if (sbi != null &&
			sbi instanceof SpeedBarPage)
			((SpeedBarPage) sbi).setPageStatus(CeNConstants.PAGE_STATUS_ARCHIVED);
		GUI.getMySpeedBar().refreshCurrentNode();
		sbi = GUI.getAllSitesSpeedBar().speedBarNavigateTo(site, user, nbRef, version);
		if (sbi != null &&
			sbi instanceof SpeedBarPage)
			((SpeedBarPage) sbi).setPageStatus(CeNConstants.PAGE_STATUS_ARCHIVED);
		GUI.getAllSitesSpeedBar().refreshCurrentNode();
	}
	*/

	/*
	public void documentRejected(String siteCode, String nbRefStr, int version, boolean ask) {
		if (!init())
			return;

		String status = CeNConstants.PAGE_STATUS_SUBMIT_FAILED;

		if (ask) {
			Object[] options = { "Re-open", "Remain Rejected" };
			int result = JOptionPane.showOptionDialog(MasterController.getGUIComponent(),
					"Experiment " + nbRefStr + "v" + version + " has been Rejected in Signature Service either by you and/or your witness.\n\n" +
					"Would you like Re-open the experiment or leave it Marked Rejected to re-submit?", "eSignature Rejection", 
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, null);
			if (result == JOptionPane.YES_OPTION)
				status = CeNConstants.PAGE_STATUS_OPEN;
		}

		// Store the Page status
		try {
			StorageDelegate sDel = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());

			sDel.updateNotebookPageStatus(siteCode, nbRefStr, version, status);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}

		// Update Speedbar Icon
		Gui GUI = MasterController.getGUIComponent();
		String site = "";
		NotebookRef nbRef = null;
		try {
			nbRef = new NotebookRef(nbRefStr);
		} catch (Exception e) {
			// intentionally ignored
		}
		try {
			site = CodeTableCache.getCache().getSiteDescription(siteCode);
		} catch (Exception e) {
			// intentionally ignored
		}
		String user = MasterController.getUser().getFullName();

		if (MasterController.getGuiController().getPageCache().hasPage(siteCode, nbRef, version)) {
	        NotebookPageModel pageModel = MasterController.getGuiController().getLoadedPageGui(siteCode, nbRef, version).getPageModel();
			NotebookPageGuiInterface pageGUI = MasterController.getGuiController().getLoadedPageGui(siteCode, nbRef, version);

			if (pageModel != null) {
				pageModel.setStatus(status);
				if (pageGUI != null)
					pageGUI.refreshPage();
			}
		}
		GUI.refreshIcons();
		SpeedBarNodeInterface sbi = GUI.getMySpeedBar().speedBarNavigateTo(site, user, nbRef, version);
		if (sbi != null &&
			sbi instanceof SpeedBarPage) {		
			((SpeedBarPage) sbi).setPageStatus(status);
		}
		GUI.getMySpeedBar().refreshCurrentNode();
		sbi = GUI.getAllSitesSpeedBar().speedBarNavigateTo(site, user, nbRef, version);
		if (sbi != null &&
			sbi instanceof SpeedBarPage) {
			((SpeedBarPage) sbi).setPageStatus(status);
		}
		GUI.getAllSitesSpeedBar().refreshCurrentNode();
	}
	*/

	public void viewPDF(final String siteCode, final String notebook, final String page, final int version)
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Retrieve the File from CeN
				byte[] buffer = null;
				try {
					StorageDelegate cenDel = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
					buffer = cenDel.getExperimentPDF(siteCode, notebook + "-" + page, version);
				} catch (Throwable e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
					return;
				}
				// Save to Local Disk
				try {
					String pageName = makeDocName(notebook, page, version);
					File file = new File(System.getProperty("java.io.tmpdir"), pageName + ".pdf");
					for (int i = 1; file.exists(); i++)
						file = new File(System.getProperty("java.io.tmpdir"), pageName + "-" + i + ".pdf");
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(buffer);
					fos.close();
					launchUrl(file.toURI());
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
					return;
				}
			}
		}).start();
	}

	private void retrieveTemplates() {
		if (CommonUtils.isNull(templates)) {
			try {
				templates = sigObj.getTemplates(MasterController.getUser().getNTUserID());
				if (templates != null) {
					if (MasterController.getUser().isSuperUser()) {
						return;
					}
					ArrayList<TemplateVO> a = new ArrayList<TemplateVO>();
					for (int i = 0; i < templates.size(); i++) {
						// Filter out templates that have 0 or 1 Signature
						// Blocks
						if (templates.get(i).getSignatureBlockCount() > 0) {
							// Filter out templates that do not have an
							// Author Signature Block
							String[] blockNames = templates.get(i).getSignatureBlockNames();
							for (int j = 0; j < templates.get(i).getSignatureBlockCount(); j++) {
								if ("Author".equalsIgnoreCase(blockNames[j])) {
									a.add(templates.get(i));
									break;
								}
							}
						}
					}
										
					templates = null;
					if (a.size() > 0) {
						templates = new ArrayList<TemplateVO>(a.size());
						for(TemplateVO template : a) {
							templates.add(template);
						}
					}
				}
			} catch (SignatureServiceUnavailableException e1) {
				JOptionPane.showMessageDialog(	MasterController.getGUIComponent(),
												"Electronic Signatures is currently unavailable, please try again later.",
												"Experiment Completion",
												JOptionPane.ERROR_MESSAGE);
			} catch (Throwable e) {
				CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), e);
			}
		}
	}

	public boolean hasTemplates() {
		if (!init())
			return false;
		retrieveTemplates();
		return (templates != null && templates.size() > 0);
	}

	public TemplateVO selectTemplate() {
		retrieveTemplates();
		
		if (templates != null && templates.size() > 0) {
			SignatureTemplateDialog dlg = new SignatureTemplateDialog(MasterController.getGUIComponent(), this);
			
			dlg.setAvailableTemplates(templates.toArray(new TemplateVO[0]));
			dlg.setVisible(true);
			
			lastSelectedTemplate = dlg.getSelectedTemplate();
			
			return lastSelectedTemplate;
		} else {
			return null;
		}
	}
	
	public boolean isAvailable() {
		if (!init())
			return false;
		try {
			return sigObj.isServiceAvailable();
		} catch (SignatureServiceUnavailableException e) {
			return false;
		}
	}

	public int canDoSubmissions() {
		if (!init())
			return -1;
		try {
			return (sigObj.areSubmissionsAllowed()) ? 1 : -2;
		} catch (Exception e) {
			return -1;
		}
	}

	public void launchSAFESign() {
		if (isAvailable()) {
			MasterController.getGUIComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						String url = new SignatureDelegate().getServiceUrl();
						Desktop.getDesktop().browse(new URI(url));
					} catch (Exception e) {
						CeNErrorHandler.getInstance().logExceptionMsg(null, e);
					}
					MasterController.getGUIComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			});
		}
	}

	public void launchUssiSignatureQueue() {
		if (isAvailable()) {
			MasterController.getGUIComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						String url = new SignatureDelegate().getServiceUrl();
						Desktop.getDesktop().browse(new URI(url));
					} catch (Exception e) {
						CeNErrorHandler.getInstance().logExceptionMsg(null, e);
					}
					MasterController.getGUIComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			});
		}
	}

	public static void launchUrl(final String url) {
		MasterController.getGUIComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Desktop.getDesktop().browse(new URI(url));
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
				}
				MasterController.getGUIComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
	}

	public static void launchUrl(final URI uri) {
		MasterController.getGUIComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Desktop.getDesktop().browse(uri);
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
				}
				MasterController.getGUIComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
	}
	
	private String makeDocName(String notebook, String page, int version) {
		return notebook + "-" + page + "v" + version;
	}

//	private String makeDocName(NotebookPageModel pageModel) {
//		return pageModel.getNotebookRefAsString();
//	}
//
//	private void purgeModel(NotebookPageModel pageModel) {
//		MasterController.getGuiController().getPageCache().removeNotebookPage(pageModel.getSiteCode(), 
//		                                                                      pageModel.getNbRef(),
//		                                                                      pageModel.getVersion());
//		try {
//			pageModel.dispose();
//		} catch (Throwable e) {
//			log.error("Failed to purge page from page cache: " + (pageModel == null ? "null" : pageModel.getNotebookRefAsString()), e);
//		}
//	}

	public static void fixAndLaunchUrl(String url) {
		launchUrl(fixUrl(url));
	}

	private static String fixUrl(String url) {
		if (url == null ||
			url.length() == 0)
			return url;

		return url.replaceAll("&amp;", "&").replaceAll("&", "\"&\"");
	}

	public boolean shouldLaunchUrl() {
		boolean result = true;

		try {
			result = MasterController.getUser().isEsigLaunchUrl();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		return result;
	}
}
