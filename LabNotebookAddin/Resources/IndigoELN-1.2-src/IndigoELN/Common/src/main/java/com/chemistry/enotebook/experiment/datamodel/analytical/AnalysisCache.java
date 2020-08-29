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

import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;

import java.util.*;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class AnalysisCache extends Observable implements Observer, DeepCopy, DeepClone {
	// Holds AbstractAnalysis objects categorized by key values;
	private Map analyticalList = null;
	private LinkedHashMap deletedList = null;

	// not sure if this is going to be used.
	private TreeMap keyList = null;

	public AnalysisCache() {
		if (analyticalList == null) {
			analyticalList = Collections.synchronizedMap(new LinkedHashMap());
			deletedList = new LinkedHashMap();
			keyList = new TreeMap();
		} else {
			analyticalList.clear();
			deletedList.clear();
			keyList.clear();
		}
	}

	/**
	 * Construct an analysisCache with the analyticals passed in the Map object.
	 * 
	 * @param allanalyticals
	 *            contains object of the same type: AbstractAnalysis
	 */
	public AnalysisCache(Map allAnalyticals) {
		this();
		putAll(allAnalyticals);
	}

	public void dispose() {
		analyticalList.clear();
		analyticalList = null;
		deletedList.clear();
		deletedList = null;
		keyList.clear();
		keyList = null;
	}

	/**
	 * 
	 * @param batch
	 *            AbstractAnalysis type
	 * @return true if analysis key can be found in the cache, false otherwise
	 */
	public boolean hasAnalysis(AbstractAnalysis analysis) {
		// checks to see if analysis exists in map
		return analyticalList.containsKey(analysis.getKey());
	}

	/**
	 * 
	 * @param batch
	 *            AbstractAnalysis type
	 * @return true if analysis key can be found in the cache, false otherwise
	 */
	public boolean hasAnalysis(String analysisKey) {
		// checks to see if analysis exists in map
		return analyticalList.containsKey(analysisKey);
	}

	/**
	 * 
	 * @param cyberLabId
	 * @return a new Analysis
	 * 
	 */

	public AbstractAnalysis createAnalysis(String cyberLabId) {
		AbstractAnalysis b = AnalysisFactory.getAnalysis(cyberLabId);
		b.setModified(true);
		analyticalList.put(b.getKey(), b);
		b.addObserver(this);
		return b;
	}

	/**
	 * 
	 * @param cyberLabId
	 * @return null if cyberLabId is not found, AbstractAnalysis if found
	 */
	public AbstractAnalysis getAnalysis(String analysisKey) {
		AbstractAnalysis retVal = null;
		String result = null;
		result = (String) keyList.get(analysisKey);
		if (result != null && !result.equals(""))
			retVal = (AbstractAnalysis) analyticalList.get(result);
		if (retVal == null)
			retVal = (AbstractAnalysis) analyticalList.get(analysisKey);
		return retVal;
	}

	/**
	 * Used to remove a single Analysis of AbstractAnalysis type from the cache. If the Analysis entered doesn't exist in the cache,
	 * nothing happens. Otherwiset the Analysis is stored in a deleted list in case we need to resurrect it.
	 * 
	 * @param analysis -
	 *            AbstractAnalysis object to remove
	 * @return boolean - true if successful, false otherwise.
	 */
	public boolean deleteAnalysis(AbstractAnalysis analysis) {
		boolean result = false;
		AbstractAnalysis deleted = null;
		deleted = (AbstractAnalysis) analyticalList.remove(analysis.getKey());
		if (deleted != null) {
			deleted.setDeletedFlag(true);
			deleted.deleteObserver(this);
			deletedList.put(deleted.getKey(), deleted);
			result = true;
			refreshIndex();
		}
		return result;
	}

	/**
	 * Used to remove a List of Analyses of AbstractAnalysis type from the cache. If the Analysis entered doesn't exist in the
	 * cache, nothing happens. Otherwiset the Analysis is stored in a deleted list in case we need to resurrect it.
	 * 
	 * @param analysis -
	 *            AbstractAnalysis object to remove
	 * @return boolean - true if successful, false otherwise.
	 */
	public boolean deleteAnalysis(List analyses) {
		boolean result = false;
		for (int i = 0; i < analyses.size(); i++) {
			result = deleteAnalysis((AbstractAnalysis) analyses.get(i));
		}
		return result;
	}

	/**
	 * Removes all analyticals of AbstractAnalysis type from the cache.
	 * 
	 * @param batches -
	 *            List of AbstractAnalysis objects
	 */
	public void deleteAnalyticalList(List l_analytical) {
		for (int i = 0; i < l_analytical.size(); i++) {
			if (l_analytical.get(i) instanceof AbstractAnalysis) {
				// remove batch from cache.
				deleteAnalysis((AbstractAnalysis) l_analytical.get(i));
			}
		}
	}

	public void put(AbstractAnalysis analysis) {
		if (!hasAnalysis(analysis)) {
			analyticalList.put(analysis.getKey(), analysis);
		}
		analysis.addObserver(this);
	}

	public void putAll(Map allAnalyticals) {
		for (Iterator it = allAnalyticals.keySet().iterator(); it.hasNext();)
			((AbstractAnalysis) it.next()).addObserver(this);

		analyticalList.putAll(allAnalyticals);
		refreshIndex();
	}

	public Iterator iterator() {
		return analyticalList.keySet().iterator();
	}

	public Object get(Object key) {
		return analyticalList.get(key);
	}

	public Map getMap() {
		refreshIndex();
		return analyticalList;
	}

	public HashMap getMapCopy() {
		refreshIndex();
		return (HashMap) (new HashMap(analyticalList)).clone();
	}

	public void refreshIndex() {
		keyList.clear();
		Iterator it = analyticalList.keySet().iterator();
		while (it.hasNext()) {
			AbstractAnalysis analysis = (AbstractAnalysis) analyticalList.get(it.next());
			String result = analysis.getCyberLabFileId();
			if (result != null || !result.equals(""))
				keyList.put(result, analysis);
		}
	}

	public Map getDeletedBatches() {
		return deletedList;
	}

	public List getMapSortedByBatchNumber() {
		ArrayList result = new ArrayList(getMap().values());
		Collections.sort(result);
		return result;
	}

	public void update(Observable arg0, Object arg1) {
		setChanged();
		notifyObservers(arg1);
	}

	public List getAnalyticalList() {
		List result = new ArrayList();
		Iterator it = iterator();
		while (it.hasNext()) {
			Analysis analysis = (Analysis) analyticalList.get(it.next());
			result.add(analysis);
		}
		return result;
	}

	public ArrayList getDistinctInstrumentTypes() {
		ArrayList result = new ArrayList();
		Hashtable h = new Hashtable();
		List l = getAnalyticalList();
		Iterator it = l.iterator();
		while (it.hasNext()) {
			Analysis analysis = (Analysis) it.next();
			if (analysis.getInstrumentType() == null) {
				analysis.setInstrumentType("Unknown");
			}
			if (h.put(analysis.getInstrumentType(), analysis.getInstrumentType()) == null) {// avoids duplicates
				result.add(analysis.getInstrumentType());
			}
		}
		return result;
	}

	public ArrayList getDistinctSampleRefes() {
		ArrayList result = new ArrayList();
		Hashtable h = new Hashtable();
		List l = getAnalyticalList();
		Iterator it = l.iterator();
		while (it.hasNext()) {
			Analysis analysis = (Analysis) it.next();
			if (h.put(analysis.getCenSampleRef(), analysis.getCenSampleRef()) == null) {// avoids duplicates
				result.add(analysis.getCenSampleRef());
			}
		}
		return result;
	}

	public ArrayList getAnalyticalList(String sampleRef) {
		ArrayList result = new ArrayList();
		List l = getAnalyticalList();
		Iterator it = l.iterator();
		while (it.hasNext()) {
			Analysis analysis = (Analysis) it.next();
			if (analysis.getCenSampleRef().equals(sampleRef)) {
				result.add(analysis);
			}
		}
		return result;
	}

	public ArrayList getDistinctInstrumentTypes(String sampleRef) {
		ArrayList result = new ArrayList();
		Hashtable h = new Hashtable();
		List l = getAnalyticalList();
		Iterator it = l.iterator();
		while (it.hasNext()) {
			Analysis analysis = (Analysis) it.next();
			if (analysis.getCenSampleRef().equals(sampleRef)) {
				if (analysis.getInstrumentType() == null) {
					analysis.setInstrumentType("Unknown");
				}
				if (h.put(analysis.getInstrumentType(), analysis.getInstrumentType()) == null) {// avoids duplicates
					result.add(analysis.getInstrumentType());
				}
			}
		}
		return result;
	}

	public ArrayList getAnalyticalList(String sampleRef, String insType) {
		ArrayList result = new ArrayList();
		List l = getAnalyticalList();
		Iterator it = l.iterator();
		while (it.hasNext()) {
			Analysis analysis = (Analysis) it.next();
			if (analysis.getCenSampleRef().equals(sampleRef) && analysis.getInstrumentType().equals(insType)) {
				result.add(analysis);
			}
		}
		return result;
	}

	public String getComments(String sampleRef) {
		String comments = "";
		List l = getAnalyticalList();
		Iterator it = l.iterator();
		while (it.hasNext()) {
			Analysis analysis = (Analysis) it.next();
			if (analysis.getCenSampleRef().equals(sampleRef)) {
				comments = analysis.getComments();
			}
		}
		if (comments == null)
			comments = "";
		return comments;
	}

	public Analysis isExistingAnalysis(String cyberLabId, String sampleRef) {
		Analysis returnAnalysis = null;
		ArrayList result = new ArrayList();
		List l = getAnalyticalList();
		Iterator it = l.iterator();
		while (it.hasNext()) {
			Analysis analysis = (Analysis) it.next();
			if (analysis.getCyberLabFileId().equals(cyberLabId) && analysis.getAnalyticalServiceSampleRef().equals(sampleRef)) {
				returnAnalysis = analysis;
				break;
			}
		}
		return returnAnalysis;
	}

	public Analysis isExistingAnalysisById(String cyberLabId){
		Analysis returnAnalysis=null;
		List l = getAnalyticalList();
		Iterator it = l.iterator();
		while (it.hasNext()){
			Analysis analysis = (Analysis) it.next();
			if(analysis.getCyberLabFileId().equals(cyberLabId)){
				returnAnalysis=analysis;
				break;
			}
		}
		return returnAnalysis;
	}
	
	// 
	// DeepClone/Copy Interface
	//

	public void deepCopy(Object resource) {
		if (resource instanceof AnalysisCache) {
			AnalysisCache srcCache = (AnalysisCache) resource;
			for (Iterator i = srcCache.iterator(); i.hasNext();) {
				Analysis analysis = (Analysis) srcCache.get((String) i.next());
				//Fixing unique constraint PK_ANALYSIS_KEY 
				AbstractAnalysis clonedAnalysis = (AbstractAnalysis) analysis.deepClone();
				analyticalList.put(clonedAnalysis.getKey(), clonedAnalysis);
			}
		}
	}

	public Object deepClone() {
		AnalysisCache newCache = new AnalysisCache();
		newCache.deepCopy(this);
		return newCache;
	}

	/**
	 * this method is used When a batch is deleted or When lot number is changed
	 * 
	 * @param oldBatchNo
	 * @param newBatchNo
	 *            TODO batchkey should be used
	 */
	public void updateAnalyses(String oldBatchNo, String newBatchNo) {
		if (analyticalList != null) {
			Collection c = analyticalList.values();
			Iterator it = c.iterator();
			ArrayList toDeleteList = new ArrayList();
			while (it.hasNext()) {
				AbstractAnalysis aa = (AbstractAnalysis) it.next();
				if (aa.getCenSampleRef() != null && aa.getCenSampleRef().equals(oldBatchNo)) {
					if (newBatchNo == null) {// When a batch is deleted
						toDeleteList.add(aa);
					} else {// When lot number is changed
						aa.setCenSampleRef(newBatchNo);
					}
					aa.setModified(true);
				}
			}
			deleteAnalysis(toDeleteList);
		}
	}

	/**
	 * Check if it is existing but with leading zeros
	 * 
	 * @param cenSampleRef
	 * @param analyticalServiceResultSampleRef
	 * @return
	 */
	boolean isExistingWithLeadingZeros(String cenSampleRef, String analyticalServiceResultSampleRef) {
		boolean result = false;
		if (cenSampleRef != null && analyticalServiceResultSampleRef != null && cenSampleRef.indexOf(analyticalServiceResultSampleRef) > 0) {
			int expectedNoleadingZeros = cenSampleRef.lastIndexOf(analyticalServiceResultSampleRef);
			String leadingString = cenSampleRef.substring(0, expectedNoleadingZeros);
			String blankExpected = leadingString.replaceAll("0", "");
			if (blankExpected.trim().length() == 0) {
				result = true;
			}
		}
		return result;
	}
}
