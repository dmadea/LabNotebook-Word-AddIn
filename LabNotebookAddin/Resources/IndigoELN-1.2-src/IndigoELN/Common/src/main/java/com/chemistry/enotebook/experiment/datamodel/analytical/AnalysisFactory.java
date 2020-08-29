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
 * Created on Nov 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.experiment.datamodel.analytical;

import com.chemistry.enotebook.domain.AnalysisModel;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class AnalysisFactory {
	private AnalysisFactory() {
	}

	public static AbstractAnalysis getAnalysis(String cyberLabFileID) {
		AbstractAnalysis result = null;
		result = new Analysis();
		result.setCyberLabFileId(cyberLabFileID);
		return result;
	}
	
	public static AnalysisModel getAnalysisModel(String cyberLabFileID) {
		AnalysisModel result =  new AnalysisModel();
		result.setCyberLabFileId(cyberLabFileID);
		return result;
	}
	
}
