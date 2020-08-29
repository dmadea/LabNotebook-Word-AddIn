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
package com.chemistry.enotebook.storage.delegate;

import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.datamodel.page.InvalidNotebookRefException;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.storage.SignaturePageVO;

import java.util.List;

public interface StorageService {
	public List<SignaturePageVO> getExperimentsBeingSigned(String ntUserID)
			throws StorageAccessException;

	public NotebookPageModel getNotebookPageExperimentInfo(NotebookRef nbRef,
			String siteCode) throws StorageAccessException,
			InvalidNotebookRefException;

	public void updateNotebookPageStatus(String siteCode, String nbRefStr,
			int version, String status, int ussiKey)
			throws StorageAccessException, InvalidNotebookRefException;

	public void storeExperimentPDF(String siteCode, String nbRefStr,
			int version, byte[] pdf) throws StorageAccessException,
			InvalidNotebookRefException;
}
