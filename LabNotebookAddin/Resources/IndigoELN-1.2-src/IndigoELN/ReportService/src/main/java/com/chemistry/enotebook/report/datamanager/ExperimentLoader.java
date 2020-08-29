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

import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.util.Stopwatch;
import com.chemistry.enotebook.utils.NbkBatchNumberComparator;
import com.chemistry.enotebook.utils.ParallelExpModelUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ExperimentLoader {

	public static final Log log = LogFactory.getLog(ExperimentLoader.class);

	private static StorageDelegate storageDelegate = null;
	
	public static StorageDelegate getStorageDelegate() throws Exception {
		return getStorageDelegate(null);
	}
	
	public static StorageDelegate getStorageDelegate(String hostName) throws Exception {
		if (storageDelegate == null) {
			try {
				storageDelegate = new StorageDelegate();
			} catch (Exception e) {
				log.error("StorageDelegate access failed", e);
				throw new Exception("StorageDelegate access failed", e);
			}
		}
		return storageDelegate;
	}
	
	public NotebookPageModel loadExperiment(NotebookRef nbref, String site) throws Exception {
		try {
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start("ExperimentLoader.loadExperiment");
			NotebookPageModel pageModel = getStorageDelegate().getNotebookPageExperimentInfo(nbref, site,null);
			log.info("Loaded " + pageModel.getPageType() + " exp:" + pageModel.getNbRef().toString());
			stopwatch.stop();
			return pageModel;
		} catch (Exception e) {
			log.error("Unable to load NotebookPageModel", e);
			throw new Exception("Unable to load experiment", e);
		}
	}	
	
	public NotebookPageModel loadExperiment(String nbkNumber, String pageNumber, String version, String hostName) throws Exception {
		try {
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start("ExperimentLoader.loadExperiment");
			NotebookRef nbkRef = new NotebookRef(nbkNumber, pageNumber);
            try {
                nbkRef.setVersion(Integer.parseInt(version));
            } catch (NumberFormatException e) {
                log.error("Wrong notebook version: " + version, e);
            }
            log.info("Loading experiment - " + nbkNumber + "-" + pageNumber);
			NotebookPageModel pageModel = getStorageDelegate(hostName).getNotebookPageExperimentInfo(nbkRef, "NOT_USED",null);
			new ParallelExpModelUtils(pageModel).sortProductBatchListsByBatchNumber(new ExNbkBatchNumberComparator());
			log.info("Loaded " + pageModel.getPageType() + " exp:" + pageModel.getNbRef().toString());
			stopwatch.stop();
			return pageModel;
		} catch (Exception e) {
			log.error("Unable to load NotebookPageModel", e);
			throw new Exception("Unable to load experiment", e);
		}
	}

	public NotebookPageModel loadSerializedExperiment(String filename) throws Exception {
		try {
			NotebookPageModel pageModel = null;
			pageModel = (NotebookPageModel )this.deSerializeObject(filename);
			// These are normally run by the StorageDelegate
			ParallelExpModelUtils utils = new ParallelExpModelUtils(pageModel);
			utils.sortProductBatchesList();
			utils.setOrRefreshGuiPseudoProductPlate();
			utils.populateMonomerAndProductHashMaps();
			utils.linkBatchesWithPlateWells();
			utils.linkProductBatchesAnalyticalModelInAnalysisCache();
			return pageModel;
		} catch (Exception e) {
			log.error("Unable to load NotebookPageModel", e);
			throw new Exception("Unable to load experiment", e);
		}
	}
	
	public Object deSerializeObject(String serFileName) throws ClassNotFoundException, IOException {
		Object obj = null;
		try {
			File file = new File(serFileName);
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			// Deserialize the object
			obj = in.readObject();
			in.close();

		} catch (ClassNotFoundException e) {
			log.error("Failed deserializing object: " + serFileName, e);
			throw e;
		} catch (IOException e) {
			log.error("Failed deserializing object: " + serFileName, e);
			throw e;
		}
		return obj; // Call should be able to cast to proper class
	}

	private class ExNbkBatchNumberComparator extends NbkBatchNumberComparator {
		protected String getIntegerPart(String s) {
			StringBuffer buff = new StringBuffer();
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c >= '0' && c <= '9')
					buff.append(c);
			}
			return buff.toString();
		}
	}
}
