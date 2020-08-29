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
package com.chemistry.enotebook.extcol;

import com.chemistry.enotebook.domain.NotebookPageModel;

public class ExternalCollaboratorServiceFakeImpl implements ExternalCollaboratorService {

	public int[] getExternalColaboratorsRequestIDs(String requestStatus, String startDate,
			String endDate) throws ExternalCollaboratorServiceException {
		return new int[] {1};
	}

	public NotebookPageModel[] getNotebookPagesForRequest(int requestID)
			throws ExternalCollaboratorServiceException {
		return new NotebookPageModel[]{};
	}

	public RequestDTOInfo getRequestDTOInfoForRequest(int requestID)
			throws ExternalCollaboratorServiceException {
		return new RequestDTOInfo("name", 1, 2, 3);
	}

}