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

import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class SpeedBarPageGroup extends SpeedBarExpandable {
	private String _site = "";
	private String _siteCode = "";
	private String _user = "";
	private String _userID = "";
	private String _notebook = "";
	private int _startPage = -1;
	private int _stopPage = -1;
	private boolean _expanded = false; // Flag indicating this node has been

	// expanded
	// at least once
	public SpeedBarPageGroup(String site, String siteCode, String user, String userID, String notebook, int startPage, int stopPage) {
		if (site != null)
			_site = site;
		if (siteCode != null)
			_siteCode = siteCode;
		if (user != null)
			_user = user;
		if (userID != null)
			_userID = userID;
		_notebook = notebook;
		_startPage = startPage;
		_stopPage = stopPage;
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

	public boolean hasBeenExpanded() {
		return _expanded;
	}

	public void setExpanded(boolean flag) {
		if (flag)
			_expanded = true;
	}

	public int getStartPage() {
		return _startPage;
	}

	public int getStopPage() {
		return _stopPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "" + getStartPage() + " - " + getStopPage();
		// return NotebookPageUtil.formatNotebookPage("" + getStartPage()) +
		// " - " +
		// NotebookPageUtil.formatNotebookPage("" + getStopPage());
	}

	/**
	 * @param experiment -
	 *            Valid Experiment/Page #
	 * @return
	 * 
	 * Method to retrieve a Group for the given experiment. ex page/experiment 9 would return a group of 1 - 50, assuming group size
	 * is 50.
	 */
	public static String getGroupFromExperiment(String experiment) {
		final int groupSize = NotebookPageUtil.NB_PAGE_GROUP_SIZE;
		// Calculate the group based on the experiment/page # and the group size
		int groupStart = ((new Integer(experiment).intValue() - 1) / groupSize) * groupSize + 1;
		int groupEnd = groupStart + groupSize - 1;
		return "" + groupStart + " - " + groupEnd;
	}

	/**
	 * @param experiment -
	 *            Valid Experiment/Page #
	 * @return
	 * 
	 * Method to retrieve a Group Start for the given experiment. ex page/experiment 9 would return a start of group 1.
	 */
	public static int getGroupStartFromExperiment(String experiment) {
		final int groupSize = NotebookPageUtil.NB_PAGE_GROUP_SIZE;
		// Calculate the group based on the experiment/page # and the group size
		return ((new Integer(experiment).intValue() - 1) / groupSize) * groupSize + 1;
	}

	/**
	 * @param experiment -
	 *            Valid Experiment/Page #
	 * @return
	 * 
	 * Method to retrieve a Group Start for the given experiment. ex page/experiment 9 would return a end of group 50, assuming
	 * group size is 50.
	 */
	public static int getGroupEndFromExperiment(String experiment) {
		final int groupSize = NotebookPageUtil.NB_PAGE_GROUP_SIZE;
		// Calculate the group based on the experiment/page # and the group size
		int groupStart = ((new Integer(experiment).intValue() - 1) / groupSize) * groupSize + 1;
		return groupStart + groupSize - 1;
	}
}
