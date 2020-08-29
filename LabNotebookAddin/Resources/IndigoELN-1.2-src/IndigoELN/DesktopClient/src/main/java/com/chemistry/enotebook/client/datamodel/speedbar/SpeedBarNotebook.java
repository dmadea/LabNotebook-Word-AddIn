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

import java.util.ArrayList;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class SpeedBarNotebook extends SpeedBarExpandable {
	private ArrayList<String> NotebookData = null;
	private String _site = "";
	private String _siteCode = "";
	private String _user = "";
	private String _userID = "";

	public SpeedBarNotebook(ArrayList<String> nb, String site, String siteCode, String user, String userID) {
		NotebookData = nb;
		if (site != null)
			_site = site;
		if (siteCode != null)
			_siteCode = siteCode;
		if (user != null)
			_user = user;
		if (userID != null)
			_userID = userID;
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
		return (NotebookData.get(0) == null) ? "" : NotebookData.get(0).toString();
	}

	public String getNotebookStatus() {
		return (NotebookData.get(1) == null) ? "" : NotebookData.get(1).toString();
	}

	public String getMinExperiment() {
		return (NotebookData.get(2) == null) ? "0" : NotebookData.get(2).toString();
	}

	public String getMaxExperiment() {
		return (NotebookData.get(3) == null) ? "0" : NotebookData.get(3).toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getNotebook();
	}
}
