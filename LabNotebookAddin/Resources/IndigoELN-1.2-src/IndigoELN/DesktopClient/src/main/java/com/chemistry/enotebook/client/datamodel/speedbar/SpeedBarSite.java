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

import com.common.chemistry.codetable.CodeTableCache;

import java.util.Properties;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class SpeedBarSite extends SpeedBarExpandable {
	private Properties SiteData = null;

	public SpeedBarSite(Properties site) {
		SiteData = site;
	}

	public String getSiteCode() {
		return (SiteData.get(CodeTableCache.SITES__SITE_CODE) == null) ? "" : SiteData.get(CodeTableCache.SITES__SITE_CODE).toString();
	}

	public String getSite() {
		return (SiteData.get(CodeTableCache.SITES__LABEL) == null) ? "" : SiteData.get(CodeTableCache.SITES__LABEL).toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getSite();
	}
}
