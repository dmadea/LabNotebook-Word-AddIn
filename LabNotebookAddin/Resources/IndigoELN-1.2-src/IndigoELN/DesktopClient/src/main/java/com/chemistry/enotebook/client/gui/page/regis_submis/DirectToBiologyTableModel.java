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
 * Created on Dec 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.regis_submis;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchSubmissionToBiologistTest;

import java.util.List;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class DirectToBiologyTableModel extends SampleToBiologyTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 813967646036971659L;

	public void resetModelData(List mmList) {
		for (int i = 0; i < mmList.size(); i++) {
			BatchSubmissionToBiologistTest batchSubmissionToBiologistTest = (BatchSubmissionToBiologistTest) mmList.get(i);
			if (!batchSubmissionToBiologistTest.isTestSubmittedByMm())
				screenData.add(batchSubmissionToBiologistTest);
		}
		fireTableDataChanged();
	}

	public void addModelData() {
		try {
			BatchSubmissionToBiologistTest sd = new BatchSubmissionToBiologistTest();
			sd.setSubmittedByMm("FALSE");
			screenData.add(sd);
			fireTableDataChanged();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}
}