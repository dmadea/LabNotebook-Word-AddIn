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
package com.chemistry.enotebook.domain;

import com.chemistry.enotebook.experiment.datamodel.analytical.AnalysisFactory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class AnalysisCacheModel extends CeNAbstractModel {

	private static final long serialVersionUID = 2384236456111079766L;
	
	private ArrayList<AnalysisModel> analyticalList = new ArrayList<AnalysisModel>();
	
	public AnalysisCacheModel() {
	}

	/**
	 * Construct an analysisCache with the analyticals passed in the Map object.
	 * 
	 * @param allanalyticals
	 *            contains object of the same type: AbstractAnalysis
	 */
	public AnalysisCacheModel(List<AnalysisModel> allAnalyticals) {
		this();
		analyticalList.addAll(allAnalyticals);
	}

	/**
	 * @param analysis
	 */
	public void addNewAnalysis(AnalysisModel analysis) {
		if (analysis != null) {
			this.analyticalList.add(analysis);
		}
	}
		
	/**
	 * Creates a newList from analyticalList, Checks of Pattern
	 * "########-####-%" against the CenSampleRef and if found adds into the
	 * newList.
	 * 
	 * @param nbkPageNo
	 *            should be of this format "########-####"
	 * @return
	 */
	public List<AnalysisModel> getUnmappedAnalysisList(String nbkPageNo) {
		List<AnalysisModel> newList = new ArrayList<AnalysisModel>();
		
		if (nbkPageNo != null) {
			for (AnalysisModel analysis : analyticalList) {
				if ((analysis.getCenSampleRef()).startsWith(nbkPageNo)) {
					newList.add(analysis);
				}
			}
		}
		
		return newList;
	}
	
	/**
	 * Creates a new List of Analysis matching the batch number.
	 * 
	 * @param batchNumber
	 * @return
	 */
	public List<AnalysisModel> getAnalysisListForBatch(String batchNumber) {
		List<AnalysisModel> newList = new ArrayList<AnalysisModel>();
		
		if (batchNumber != null) {
			for (AnalysisModel analysis : analyticalList) {
				if (analysis.getCenSampleRef().equals(batchNumber) && !analysis.isSetToDelete()) { // vb 12/28
					newList.add(analysis);
				}
			}
		}
		
		return newList;
	}
	
	/**
	 * Get the list of instrument types for this batch's analyses // vb 7/24
	 * 
	 * @param sampleRef
	 * @return
	 */
	public ArrayList<String> getDistinctInstrumentTypes(String batchNumber) {
		ArrayList<String> list = new ArrayList<String>();
		
		if (batchNumber != null) {
			Hashtable<String, String> h = new Hashtable<String, String>();
			for (AnalysisModel analysis : getAnalyticalList()) {
				if (analysis.getCenSampleRef().equals(batchNumber)) {
					if (analysis.getInstrumentType() == null) {
						analysis.setInstrumentType("Unknown");
					}
					if (h.put(analysis.getInstrumentType(), analysis.getInstrumentType()) == null) {// avoids duplicates
						list.add(analysis.getInstrumentType());
					}
				}
			}
		}
		
		return list;
	}
	
	/**
	 * Get the list of instrument types for all batches // vb 12/16
	 * 
	 * @param sampleRef
	 * @return
	 */
	public ArrayList<String> getDistinctInstrumentTypes() {
		ArrayList<String> list = new ArrayList<String>();
		Hashtable<String, String> h = new Hashtable<String, String>();
		
		for (AnalysisModel analysis : getAnalyticalList()) {
			if (analysis.isSetToDelete()) {
				continue;
			}
			if (analysis.getInstrumentType() == null) {
				analysis.setInstrumentType("Unknown");
			}
			if (h.put(analysis.getInstrumentType(), analysis.getInstrumentType()) == null) {// avoids duplicates
				list.add(analysis.getInstrumentType());
			}
		}
		
		return list;
	}
	
	/**
	 * Get the list of sample refs (notebook page nos) that have analyses. vb
	 * 12/16
	 * 
	 * @return
	 */
	public List<String> getDistinctSampleRefs() {
		List<String> list = new ArrayList<String>();

		for (AnalysisModel analysis : getAnalyticalList()) {
			String sampleRef = analysis.getCenSampleRef();
			if (list.contains(sampleRef)) {
				continue;
			} else {
				list.add(sampleRef);
			}
		}

		return list;
	}

	/**
	 * 
	 * @param batch
	 *            AbstractAnalysis type
	 * @return true if analysis key can be found in the cache, false otherwise
	 */
	public boolean hasAnalysis(AnalysisModel analysis) {
		// checks to see if analysis exists in map
		return analyticalList.contains(analysis);
	}
	
	/**
	 * 
	 * @param cyberLabId
	 * @return a new Analysis
	 * 
	 */
	public AnalysisModel createAnalysis(String cyberLabId) {
		AnalysisModel b = AnalysisFactory.getAnalysisModel(cyberLabId);
		this.analyticalList.add(b);
		return b;
	}

	/**
	 * @return the analyticalList
	 */
	public ArrayList<AnalysisModel> getAnalyticalList() {
		return analyticalList;
	}

	/**
	 * @param analyticalList
	 *            the analyticalList to set
	 */
	public void setAnalyticalList(ArrayList<AnalysisModel> analyticalList) {
		this.analyticalList = analyticalList;
	}

	public String toXML() {
		return "";
	}
	 	 
	public AnalysisModel isExistingAnalysis(String cyberLabId, String sampleRef) {		
		for (AnalysisModel analysis : getAnalyticalList()) {
			if (analysis.getCyberLabFileId().equals(cyberLabId) && analysis.getAnalyticalServiceSampleRef().equals(sampleRef)) {
				return analysis;
			}
		}
		
		return null;
	}
	 
	public AnalysisCacheModel getClonedAnalyticalModelsForInsertAndUpdate() {
		AnalysisCacheModel clonedCache = new AnalysisCacheModel();
		ArrayList<AnalysisModel> newlist = new ArrayList<AnalysisModel>();

		for (AnalysisModel model : analyticalList) {
			if (model.isModelChanged()) {
				AnalysisModel clonedmodel = model.deepClone();
				newlist.add(clonedmodel);
			}
		}
		
		clonedCache.setAnalyticalList(newlist);
		
		return clonedCache;
	}

	public AnalysisCacheModel deepCopy() {
		AnalysisCacheModel clonedCache = new AnalysisCacheModel();
		ArrayList<AnalysisModel> newlist = new ArrayList<AnalysisModel>();

		for (AnalysisModel model : analyticalList) {
			AnalysisModel copiedModel = new AnalysisModel();
			copiedModel.deepCopy(model);
			newlist.add(copiedModel);
		}
		
		clonedCache.setAnalyticalList(newlist);
		
		return clonedCache;
	}
	 
	/**
	 * Used to remove a single Analysis, by setting boolean flag setToDelete.
	 * Update operation would inspect this flag and perform a Delete in database
	 * 
	 * @param analysis
	 *            - AbstractAnalysis object to remove
	 * @return
	 */
	public void deleteAnalysis(AnalysisModel analysis) {
		if (analysis != null) {
			analysis.setToDelete(true);
		}
	}

	/**
	 * Used to remove a List of Analyses
	 * 
	 * @param analysis
	 *            - Analysis object to remove
	 */
	public void deleteAnalysis(List<AnalysisModel> analysesList) {
		if (analysesList != null && analysesList.size() > 0) {
			for (AnalysisModel analysis : analysesList) {
				deleteAnalysis(analysis);
			}
		}
	}
}
