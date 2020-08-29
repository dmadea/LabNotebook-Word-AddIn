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
package com.chemistry.enotebook.client.datamodel.speedbar;

import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ReactionSchemeModel;
import com.chemistry.enotebook.domain.ReactionStepModel;

public class CRONotebookPage extends SpeedBarLoadable {

	private NotebookPageModel pageModel;

	public CRONotebookPage(NotebookPageModel pageModel) {
		this.pageModel = pageModel;
	}

	public NotebookPageModel getPageModel() {
		return pageModel;
	}

	public void setPageModel(NotebookPageModel pageModel) {
		this.pageModel = pageModel;
	}

	public String toString() {
		return (pageModel == null ? "pageModel == null" : pageModel.getNbRef().getNbPage());
	}

	public byte[] getNativeSketch() {
		if (pageModel != null) {
			final ReactionStepModel summaryReactionStep = pageModel.getSummaryReactionStep();
			if (summaryReactionStep != null) {
				final ReactionSchemeModel rxnScheme = summaryReactionStep.getRxnScheme();
				if (rxnScheme != null) {
					return rxnScheme.getNativeSketch();
				}
			}
		}
		return null;
	}

	public String getNotebook() {
		return (pageModel == null ? "" : pageModel.getNbRef().getNbNumber());
	}
	
	public String getPage() {
		return (pageModel == null ? "" : pageModel.getNbRef().getNbPage());
	}
	
	public int getVersion() {
		return (pageModel == null ? 1 : pageModel.getNbRef().getVersion());
	}
	
	public String getSiteCode() {
		return (pageModel == null ? "" : pageModel.getSiteCode());
	}
	
	public String getPageType() {
		return (pageModel == null ? "" : pageModel.getPageType());
	}
}

