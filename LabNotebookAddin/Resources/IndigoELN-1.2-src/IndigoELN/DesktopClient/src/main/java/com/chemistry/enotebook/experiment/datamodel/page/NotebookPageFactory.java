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
 * NotebookPageFactory.java
 * 
 * Created on Aug 5, 2004
 *
 * 
 */
package com.chemistry.enotebook.experiment.datamodel.page;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.delegate.NotebookDelegate;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.experiment.utils.GUIDUtil;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import com.chemistry.enotebook.storage.ValidationInfo;

import java.util.Date;

/**
 * 
 * @date Aug 5, 2004
 */
public class NotebookPageFactory {
	private NotebookPageFactory() {
	}

	public static NotebookPage createEmpty() throws NotebookPageCreateException {
		return new NotebookPage(false);
	}

	public static NotebookPage create(String nbRefStr, NotebookUser user) throws NotebookPageCreateException {
		return create(nbRefStr, user, false, true);
	}

	public static NotebookPage create(String nbRefStr, NotebookUser user, boolean updatePrefs, boolean validateNbRef)
			throws NotebookPageCreateException {
		NotebookPage nbPage = null;
		// If NotebookRef is null get last edited notebook ref and generate a
		// new page from that.
		if (nbRefStr == null || nbRefStr.length() == 0)
			throw new NotebookPageCreateException("NotebookRef is null or empty");
		else if (user == null)
			throw new NotebookPageCreateException("Notebook user must not be null");
		else {
			try {
				NotebookDelegate nd = new NotebookDelegate(user.getSessionIdentifier());
				NotebookRef nbRef = new NotebookRef(nbRefStr);
				if (validateNbRef) {
					ValidationInfo vi = nd.validateNotebook(null, nbRef.getNbNumber(), nbRef.getNbPage());
					if (vi != null) // Experiment exists?
						throw new NotebookPageCreateException("Notebook Experiment '" + nbRef.getNbNumber() + "-"
								+ nbRef.getNbPage() + "' already exists");
					else {
						vi = nd.validateNotebook(null, nbRef.getNbNumber(), null);
						if (vi == null) // Notebook exists?
							throw new NotebookPageCreateException("Notebook '" + nbRef.getNbNumber() + "' does not exist");
						else if (!vi.creator.equals(user.getNTUserID()) && !user.isSuperUser())
							throw new NotebookPageCreateException("Notebook '" + nbRef.getNbNumber() + "' is owned by another user");
					}
				}
				nbPage = new NotebookPage(nbRef, false);
				// Adds nbRef to GUID helps prevent GUID collisions when lots of
				// pages are
				// generated all at once. Of course there shouldn't be any, but
				// I like be cautious
				nbPage.setKey(GUIDUtil.generateGUID(nbPage, user.getNTUserID()));
				nbPage.setOwner(user.getNTUserID().toUpperCase()); // not
				// used by CeN kept for legacy system: Chemistry Workbook
				nbPage.setSiteCode(user.getSiteCode());
				nbPage.setLaf(CeNConstants.PAGE_TYPE_MED_CHEM);
				nbPage.setCenVersion(CeNSystemProperties.getVersionNumber());
				nbPage.setCreationDate(NotebookPageUtil.formatDate(new Date()));
				nbPage.setModificationDate(nbPage.getCreationDate());
				nbPage.setUserNTID(user.getNTUserID().toUpperCase());
				nbPage.setStatus(CeNConstants.PAGE_STATUS_OPEN);
				nbPage.setVersion(1);
				nbPage.setLatestVersion("Y");
				if (updatePrefs) {
					// Defaults for page, currently just TA, Project,
					// Source, Source Detail codes,
					// however, src/srcdetail are set in reg/batch area.
					nbPage.setTaCode(MasterController.getUser().getPreference(NotebookUser.PREF_LastTA));
					nbPage.setProjectCode(MasterController.getUser().getPreference(NotebookUser.PREF_LastProject));
					user.setPreference(NotebookUser.PREF_CurrentNbRef, nbRef.toString());
					user.setPreference(NotebookUser.PREF_CurrentNbRefVer, "1");
					user.setPreference(NotebookUser.PREF_LastNBRef, nbRef.toString());
					user.updateUserPrefs();
				}
			} catch (NotebookPageCreateException e1) {
				throw e1;
			} catch (Exception e) {
				throw new NotebookPageCreateException("Could not create experiment", e);
			}
		}
		return nbPage;
	}
}
