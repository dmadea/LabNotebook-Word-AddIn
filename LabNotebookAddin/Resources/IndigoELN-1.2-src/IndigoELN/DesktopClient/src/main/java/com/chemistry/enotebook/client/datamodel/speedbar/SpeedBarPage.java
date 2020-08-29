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
/*
 * Created on Jun 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.client.datamodel.speedbar;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ReactionSchemeModel;
import com.chemistry.enotebook.domain.ReactionStepModel;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.Date;
import java.util.List;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class SpeedBarPage extends SpeedBarLoadable {
	private List<Object> PageData = null;
	private String _site = "";
	private String _siteCode = "";
	private String _user = "";
	private String _userID = "";
	private String _notebook = "";
	private byte[] currentSketch = null;
	private String subject = null;
	private String project = null;
	private boolean isCompleteInProgress = false;

    private NotebookPageModel pageModel = null;

	private boolean complianceFlag = true;   // purely based on Date, page status will further dictate compliance
	
	public SpeedBarPage(List<Object> page, String site, String siteCode, String user, String userID, String notebook) {
		PageData = page;
		if (site != null) 	  _site = site;
		if (siteCode != null) _siteCode = siteCode;
		if (user != null)     _user = user;
		if (userID != null)   _userID = userID;
		if (notebook != null) _notebook = notebook;
		
        try { 
	        Date modDate = NotebookPageUtil.getLocalDate(getModifiedDate()); 
			Date curDate = new Date(System.currentTimeMillis());
				
			complianceFlag = (curDate.getTime() - modDate.getTime()) < (14 * 24 * 60 * 60 * 1000);
//	        System.out.println(getPage() + " " + modDate.toString() + "(" + modDate.getTime() + ") " + 
//	        					  				 curDate.toString() + "(" + curDate.getTime() + ") " + complianceFlag);
	    } catch (Exception e) { /* assume compliance */ }
	}

	public String getSite() {
		return _site;
	}

	public String getSiteCode() {
		return _siteCode;
	}

	public String getUser() {
		return _user;
	}

	public String getUserID() {
		return _userID;
	}

	public String getNotebook() {
		return _notebook;
	}

	public String getPage() {
		return (PageData.get(0) == null) ? "" : PageData.get(0).toString();
	}

	public int getVersion() {
		return (PageData.get(1) == null) ? 0 : new Integer(PageData.get(1).toString()).intValue();
	}

	public boolean isLatestVersion() {
		return (PageData.get(2) == null || ((String) PageData.get(2)).equalsIgnoreCase("Y"));
	}

	public void setLatestVersion(String val) {
		PageData.set(2, val);
	}

    public void setPageModel(NotebookPageModel pageModel) {
        this.pageModel = pageModel;
    }

    public String getPageType() {
		return (PageData.get(3) == null) ? "" : PageData.get(3).toString();
	}

	public String getPageStatus() {
		return (PageData.get(4) == null) ? "" : PageData.get(4).toString();
	}

	public void setPageStatus(String status) {
		PageData.set(4, status);
	}

	public String getCreationDate() {
		return (PageData.get(5) == null) ? "" : PageData.get(5).toString();
	}

	public String getSubject() {
		updatePageModel();
        if (pageModel != null)
            return pageModel.getSubject();
		if (subject == null)
			subject = (String) PageData.get(6);
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getProject() {
		updatePageModel();
        if (pageModel != null)
            return pageModel.getProjectCode();
		if (project == null)
			project = (String) PageData.get(7);
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}
	
	public byte[] getNativeSketch() {
		updatePageModel();
		if (pageModel != null) {
            final ReactionStepModel summaryReactionStep = pageModel.getSummaryReactionStep();
            if (summaryReactionStep != null) {
                final ReactionSchemeModel rxnScheme = summaryReactionStep.getRxnScheme();
                if (rxnScheme != null) {
                	currentSketch = rxnScheme.getNativeSketch();
                }
            }
        }
		if (currentSketch == null) {
			currentSketch = (byte[]) PageData.get(8);
		}
		
		return currentSketch;
	}

	public String getModifiedDate() {
		return (PageData.get(9) == null) ? "" : PageData.get(9).toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (getVersion() > 1 || !isLatestVersion())
			return getPage() + " v" + getVersion();
		else
			return getPage();
	}

	public boolean isCompleteInProgress() {
		return isCompleteInProgress;
	}

	public void setCompleteInProgress(boolean isCompleteInProgress) {
		this.isCompleteInProgress = isCompleteInProgress;
	}

	public boolean isInCompliance() {
        if (getPageStatus().equals(CeNConstants.PAGE_STATUS_ARCHIVED) || getPageStatus().equals(CeNConstants.PAGE_STATUS_COMPLETE))
        	return true;
        else if ((getPageStatus().equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED) || getPageStatus().equals(CeNConstants.PAGE_STATUS_ARCHIVE_FAILED)) &&
        	     !isLatestVersion())
        	return true;
        else
        	return complianceFlag;
 	}
	
	private void updatePageModel() {
		JInternalFrame[] frames = MasterController.getGUIComponent().getDesktopWindows();
		if (!ArrayUtils.isEmpty(frames)) {
			for (JInternalFrame frame : frames) {
				if (frame instanceof NotebookPageGuiInterface) {
					NotebookPageModel page = ((NotebookPageGuiInterface) frame).getPageModel();
					if (page != null && this.pageModel == null) {
						if (StringUtils.equals(getNotebook(), page.getNbRef().getNbNumber()) &&
								StringUtils.equals(getPage(), page.getNbRef().getNbPage()) &&
								getVersion() == page.getNbRef().getVersion()) {
							setPageModel(page);
						}
					}
				}
			}
		}
	}
}
