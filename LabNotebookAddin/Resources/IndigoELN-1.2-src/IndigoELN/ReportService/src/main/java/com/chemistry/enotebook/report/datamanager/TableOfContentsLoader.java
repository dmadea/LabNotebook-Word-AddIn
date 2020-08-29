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
package com.chemistry.enotebook.report.datamanager;

import com.chemistry.enotebook.storage.ContentsContext;
import com.chemistry.enotebook.storage.ReactionPageInfo;
import com.chemistry.enotebook.utils.CommonUtils;

import java.util.List;

public class TableOfContentsLoader {
	
	public List<ReactionPageInfo> loadTableOfContentsInfo(String siteCode, String notebook, int startExperiment, int stopExperiment) {		
		ContentsContext cc = new ContentsContext();
		cc.setSiteCode(siteCode);
		cc.setNotebook(notebook);
		cc.setStartExperiment(startExperiment);
		cc.setStopExperiment(stopExperiment);
		
		try {
			cc = (ContentsContext) ExperimentLoader.getStorageDelegate().retrieveData(cc, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return CommonUtils.buildReactionPagesList(cc.getResults());
	}
}
