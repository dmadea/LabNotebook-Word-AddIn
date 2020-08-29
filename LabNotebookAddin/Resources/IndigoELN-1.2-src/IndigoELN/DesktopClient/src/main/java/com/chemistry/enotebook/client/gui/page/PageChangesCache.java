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
package com.chemistry.enotebook.client.gui.page;

import com.chemistry.enotebook.domain.NotebookPageModel;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class PageChangesCache {

	private static final Map<String, PageChanges> cache = new HashMap<String, PageChanges>();
	
	public static void storePage(NotebookPageModel pageModel) {
		if (pageModel != null) {
			PageChanges changes = cache.get(pageModel.getKey());
			if (changes == null) {
				changes = new PageChanges();
			}
			changes.pageKey = new String(pageModel.getKey());
			changes.stringSketch = ArrayUtils.clone(pageModel.getSummaryReactionStep().getRxnScheme().getStringSketch());
			changes.nativeSketch = ArrayUtils.clone(pageModel.getSummaryReactionStep().getRxnScheme().getNativeSketch());
			changes.viewSketch = ArrayUtils.clone(pageModel.getSummaryReactionStep().getRxnScheme().getViewSketch());
			changes.subject = new String(pageModel.getSubject() == null ? "" : pageModel.getSubject());
			changes.taCode = new String(pageModel.getTaCode() == null ? "" : pageModel.getTaCode());
			changes.projectCode = new String(pageModel.getProjectCode() == null ? "" : pageModel.getProjectCode());
			cache.put(pageModel.getKey(), changes);
		}
	}
	
	public static void restorePage(NotebookPageModel pageModel) {
		if (pageModel != null) {
			PageChanges changes = cache.get(pageModel.getKey());
			if (changes != null && StringUtils.equals(pageModel.getKey(), changes.pageKey)) {
				pageModel.setSubject(new String(changes.subject == null ? "" : changes.subject));
				pageModel.setTaCode(new String(changes.taCode == null ? "" : changes.taCode));
				pageModel.setProjectCode(new String(changes.projectCode == null ? "" : changes.projectCode));
				pageModel.getSummaryReactionStep().getRxnScheme().setStringSketch(ArrayUtils.clone(changes.stringSketch));
				pageModel.getSummaryReactionStep().getRxnScheme().setNativeSketch(ArrayUtils.clone(changes.nativeSketch));
				pageModel.getSummaryReactionStep().getRxnScheme().setViewSketch(ArrayUtils.clone(changes.viewSketch));
			}
		}
	}
	
	private static class PageChanges {
		private String pageKey;
		private byte[] stringSketch;
		private byte[] nativeSketch;
		private byte[] viewSketch;
		private String subject;
		private String taCode;
		private String projectCode;
	}
}