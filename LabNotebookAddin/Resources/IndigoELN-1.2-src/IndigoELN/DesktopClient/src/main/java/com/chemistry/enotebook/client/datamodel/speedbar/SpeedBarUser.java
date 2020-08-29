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
public class SpeedBarUser extends SpeedBarExpandable {
	private ArrayList<String> UserData = null;
	private String _site = "";
	private String _siteCode = "";

	public SpeedBarUser(ArrayList<String> user, String site, String siteCode) {
		UserData = user;
		if (site != null)
			_site = site;
		if (siteCode != null)
			_siteCode = siteCode;
	}

	public String getSite() {
		return _site;
	}

	public String getSiteCode() {
		return _siteCode;
	}

	public String getUserID() {
		return (UserData.get(0) == null) ? "" : UserData.get(0).toString();
	}

	public String getUser() {
		return (UserData.get(1) == null) ? "" : UserData.get(1).toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getUser();
	}
}
