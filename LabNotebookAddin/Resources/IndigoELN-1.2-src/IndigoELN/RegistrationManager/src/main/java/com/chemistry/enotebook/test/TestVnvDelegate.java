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
package com.chemistry.enotebook.test;

import com.chemistry.enotebook.delegate.VnvDelegate;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.ReactionStepModel;
import com.chemistry.enotebook.exceptions.VnvException;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;

import java.util.List;

public class TestVnvDelegate {

	public static NotebookPageModel getNotebookPage(NotebookRef nbref) throws Exception {
		SessionIdentifier sidentifier = new SessionIdentifier("DOMAIN", "USER", "token_string", true);
		StorageDelegate ssi = new StorageDelegate();
		long startTime = System.currentTimeMillis();
		NotebookPageModel page = ssi.getNotebookPageExperimentInfo(nbref, sidentifier.getSiteCode(),null);// getNotebookPageExperimentInfo(nbref,
																										// sidentifier);
		System.out.println(System.currentTimeMillis() - startTime + " ms elapsed for [ NotebookPage Load " + nbref + "]");
		System.out.println("Loaded Page " + page.getUserName() + " : " + page.getNbRef().getNbRef());

		// CommonUtils.printNBPageData(page);
		return page;
	}

	public static List<ProductBatchModel> getProductBatches(NotebookPageModel page) throws Exception {
		ReactionStepModel step = page.getSummaryReactionStep();
		List<ProductBatchModel> list = step.getAllProductBatchModelsInThisStep();
		// BatchesList blist = (BatchesList)list.get(0);
		return list;
	}

	public static void main(String[] args) throws Exception {
		NotebookRef nbref = new NotebookRef();
		nbref.setNbNumber("27071975");
		nbref.setNbPage("0910");
		System.out.println("loading Notebook Page :" + nbref);
		NotebookPageModel page = getNotebookPage(nbref);
		List<ProductBatchModel> list = getProductBatches(page);
		for (int i = 0; i < list.size(); i++) {
			ProductBatchModel pBatch = (ProductBatchModel) list.get(i);
			try {
				VnvDelegate vnvd = new VnvDelegate();
				vnvd.performVnV(pBatch, "USER", page);
				// System.out.println("VNV Done!! for ProductBatch");
			} catch (VnvException error) {
				error.printStackTrace();
			}
		}
	}
}
