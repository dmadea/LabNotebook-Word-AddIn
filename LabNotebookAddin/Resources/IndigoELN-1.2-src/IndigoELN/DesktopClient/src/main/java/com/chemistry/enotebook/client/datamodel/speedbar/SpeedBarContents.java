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

/**
 * 
 * 
 * TODO Add Class Information
 */
public class SpeedBarContents extends SpeedBarLoadable {
	private String _site = "";
	private String _siteCode = "";
	private String _user = "";
	private String _userID = "";
	private String _notebook = "";
	private int _startExp = -1;
	private int _endExp = -1;

	public SpeedBarContents(String site, String siteCode, String user, String userID, String notebook) {
		if (site != null)
			_site = site;
		if (siteCode != null)
			_siteCode = siteCode;
		if (user != null)
			_user = user;
		if (userID != null)
			_userID = userID;
		if (notebook != null)
			_notebook = notebook;
	}

	public SpeedBarContents(String site, String siteCode, String user, String userID, String notebook, int startExperiment,
			int endExperiment) {
		if (site != null)
			_site = site;
		if (siteCode != null)
			_siteCode = siteCode;
		if (user != null)
			_user = user;
		if (userID != null)
			_userID = userID;
		if (notebook != null)
			_notebook = notebook;
		_startExp = startExperiment;
		_endExp = endExperiment;
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

	public boolean isNotebookContents() {
		return (_startExp == -1 && _endExp == -1);
	}

	public boolean isNotebookGroupContents() {
		return (_startExp != -1 && _endExp != -1);
	}

	public int getStartExperiment() {
		return _startExp;
	}

	public int getEndExperiment() {
		return _endExp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Table of Contents";
	}
}
