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
package com.chemistry.enotebook.experiment.datamodel.batch;

import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;
import org.apache.commons.lang.StringUtils;

import javax.swing.event.ChangeEvent;
import java.util.*;

/**
 * 
 * 
 *
 */
public class BatchCache extends Observable implements Observer, DeepCopy, DeepClone, BatchRegistrationListener {
	// Holds AbstractBatch objects categorized by batch GUID (that means "key") values;
	private Map<String, AbstractBatch> batchList = null;
	private LinkedHashMap<String, AbstractBatch> deletedList = null;
	private ArrayList<BatchRegistrationListener> regListeners = new ArrayList<BatchRegistrationListener>();

	// Need to have a way to access list via BatchNumber instead of batch GUID.
	// Holds batchNumber Strings associated with key values.
	private TreeMap<String, String> keyList = null;

	public BatchCache() {
		if (batchList == null) {
			batchList = Collections.synchronizedMap(new LinkedHashMap<String, AbstractBatch>());
			deletedList = new LinkedHashMap<String, AbstractBatch>();
			keyList = new TreeMap<String, String>();
		} else {
			batchList.clear();
			deletedList.clear();
			keyList.clear();
		}
	}

	/**
	 * Construct a BatchCache with the batches passed in the Map object.
	 * 
	 * @param allBatches
	 *            contains object of the same type: AbstractBatch
	 */
	public BatchCache(Map<String, AbstractBatch> allBatches) {
		this();
		putAll(allBatches);
	}

	public void dispose() {
		clearAll();
		if (batchList != null)
			batchList.clear();
		batchList = null;
		if (deletedList != null)
			deletedList.clear();
		deletedList = null;
		keyList = null;
		if (regListeners != null)
			regListeners.clear();
		regListeners = null;
	}

	/**
	 * Removes batches from both deletedList and from batchList and keyList WARNING: Intellectual Property implications here. Do not
	 * use if there is the possibility of misuse.
	 */
	public void clearAll() {
		clear(false);
		deletedList.clear();
		keyList.clear();
	}

	/**
	 * Removes all active batches without transferring them to the deletedList. Automatically calls refreshIndex(). If you don't
	 * want that cost, call clear(false) WARNING: Intellectual Property implications here. Do not use if there is the possibility of
	 * misuse.
	 * 
	 */
	public void clear() {
		clear(true);
	}

	/**
	 * Removes all active batches without transferring them to the deletedList. WARNING: Intellectual Property implications here. Do
	 * not use if there is the possibility of misuse.
	 * 
	 * @param bRefreshIndex =
	 *            should the index be refreshed or not
	 */
	public void clear(boolean bRefreshIndex) {
		batchList.clear();
		if (bRefreshIndex)
			refreshIndex();
	}

	/**
	 * 
	 * @param batch
	 *            AbstractBatch type
	 * @return true if batch key can be found in the cache, false otherwise
	 */
	public boolean hasBatch(AbstractBatch batch) {
		// checks to see if batch exists in map
		if (batch != null)
			return batchList.containsKey(batch.getKey());
		else
			return false;
	}

	/**
	 * 
	 * @param batchType
	 * @return a new batch of the requested type
	 * @throws InvalidBatchTypeException -
	 *             thrown by BatchFactory.getBatch(batchType)
	 */
	public AbstractBatch createBatch(BatchType batchType) throws InvalidBatchTypeException {
		AbstractBatch b = BatchFactory.getBatch(batchType);
		batchList.put(b.getKey(), b);
		b.addObserver(this);
		if (b instanceof ProductBatch)
			((ProductBatch) b).addRegistrationListener(this);
		return b;
	}

	/**
	 * 
	 * @param guidOrBatchNumber
	 * @return null if guid or batch number is not found, AbstractBatch if found
	 */
	public AbstractBatch getBatch(String guidOrBatchNumber) {
		AbstractBatch retVal = null;
		String result = null;
		result = (String) keyList.get(guidOrBatchNumber);
		if (result != null && !result.equals(""))
			retVal = (AbstractBatch) batchList.get(result);
		if (retVal == null)
			retVal = (AbstractBatch) batchList.get(guidOrBatchNumber);
		return retVal;
	}

	/**
	 * 
	 * @param batchNumber
	 * @return null if the batchNumber is not in the cache
	 */
	public AbstractBatch getBatch(BatchNumber batchNumber) {
		return this.getBatch(batchNumber.toString());
	}

	/**
	 * Used to remove a single batch of AbstractBatch type from the cache. If the batch entered doesn't exist in the cache, nothing
	 * happens. Otherwiset the batch is stored in a deleted list in case we need to resurrect it. TODO: create resurrection of batch
	 * info.
	 * 
	 * Will not delete any batch with RegStatus other than null, "" or BatchRegistrationInfo.NOT_REGISTERED
	 * 
	 * @param batch -
	 *            AbstractBatch object to remove
	 * @return boolean - true if successful, false otherwise.
	 */
	public boolean deleteBatch(AbstractBatch batch) {
		boolean result = false;
		AbstractBatch deleted = null;
		if (batchList != null && batch != null) {
			if (batch.isEditable()) {
				deleted = (AbstractBatch) batchList.remove(batch.getKey());
				if (deleted != null) {
					deleted.setDeletedFlag(true);
					deleted.deleteObserver(this);
					if (deleted instanceof ProductBatch)
						((ProductBatch) deleted).deleteRegistrationListener(this);
					deletedList.put(deleted.getKey(), deleted);
					result = true;
					refreshIndex();
				}
			}
		}
		return result;
	}

	/**
	 * Removes all batches of AbstractBatch type from the cache.
	 * 
	 * Will not delete any batch with RegStatus other than null, "" or BatchRegistrationInfo.NOT_REGISTERED
	 * 
	 * @param batches -
	 *            List of AbstractBatch objects
	 */
	public void deleteBatches(List batches) {
		for (int i = 0; i < batches.size(); i++) {
			if (batches.get(i) instanceof AbstractBatch) {
				// remove batch from cache.
				deleteBatch((AbstractBatch) batches.get(i));
			}
		}
	}

	public void put(AbstractBatch batch) {
		if (!hasBatch(batch)) {
			batchList.put(batch.getKey(), batch);
			batch.addObserver(this);
			if (batch instanceof ProductBatch)
				((ProductBatch) batch).addRegistrationListener(this);
		}
	}

	public void putAll(Map<String, AbstractBatch> allBatches) {
		for (Iterator<AbstractBatch> it = allBatches.values().iterator(); it.hasNext();) {
			AbstractBatch batch = (AbstractBatch) it.next();
			batch.addObserver(this);
			if (batch instanceof ProductBatch)
				((ProductBatch) batch).addRegistrationListener(this);
		}

		batchList.putAll(allBatches);
		refreshIndex();
	}

	public Iterator<String> iterator() {
		return batchList.keySet().iterator();
	}

	public Object get(Object key) {
		return batchList.get(key);
	}

	public Map<String, AbstractBatch> getMap() {
		refreshIndex();
		return batchList;
	}

	public HashMap getMapCopy() {
		refreshIndex();
		return (HashMap) (new HashMap(batchList)).clone();
	}

	public void refreshIndex() {
		keyList.clear();
		Iterator<String> it = batchList.keySet().iterator();
		while (it.hasNext()) {
			AbstractBatch batch = (AbstractBatch) batchList.get(it.next());
			String result = batch.getBatchNumberAsString();
			if (StringUtils.isNotBlank(result))
				keyList.put(result, batch.getKey());
		}
	}

	public Map getDeletedBatches() {
		return deletedList;
	}

	public List getReagents() {
		List result = new ArrayList();
		Iterator it = batchList.keySet().iterator();
		while (it.hasNext()) {
			AbstractBatch batch = (AbstractBatch) batchList.get(it.next());
			if (batch instanceof ReagentBatch)
				result.add(batch);
		}
		return result;
	}

	public List getProducts() {
		List result = new ArrayList();
		Iterator it = batchList.keySet().iterator();
		while (it.hasNext()) {
			AbstractBatch batch = (AbstractBatch) batchList.get(it.next());
			if (batch instanceof ProductBatch)
				result.add(batch);
		}
		return result;
	}

	/**
	 * 
	 * @return all batches in batch cache
	 */
	public List getBatches() {
		return getBatches(-1);
	}

	public List getSortedBatchList() {
		ArrayList result = new ArrayList(getMap().values());
		Collections.sort(result);
		return result;
	}

	public List<AbstractBatch> getBatches(int batchTypes) {
		List<AbstractBatch> result = new ArrayList<AbstractBatch>();
		BatchType queryType = null;
		AbstractBatch ab = null;
		for (Iterator<String> it = iterator(); it.hasNext();) {
			ab = (AbstractBatch) getBatch((String) it.next());
			queryType = ab.getType();
			if ((queryType.getOrdinal() & batchTypes) != 0)
				result.add(ab);
		}
		return result;
	}

	public List<AbstractBatch> getBatches(BatchType bType) {
		List<AbstractBatch> result = new ArrayList<AbstractBatch>();
		Iterator<String> it = batchList.keySet().iterator();
		while (it.hasNext()) {
			AbstractBatch batch = (AbstractBatch) batchList.get(it.next());
			if (batch.getType().equals(bType))
				result.add(batch);
		}
		return result;
	}

	/**
	 * 
	 * @param bType
	 * @return List of batches sorted on CompareTo Function
	 */
	public List getBatchesSorted(BatchType bType) {
		List results = getBatches(bType);
		Collections.sort(results);
		return results;
	}

	/**
	 * 
	 * @param batchTypes -
	 *            Or'ed ordinal values to create mask for list
	 * @param comp -
	 *            uses comparitor to return list sorted by comparator object
	 * @return List of batches sorted on CompareTo Function
	 */
	public List getSortedBatches(int batchTypes, Comparator comp) {
		List results = new ArrayList(getBatches(batchTypes));
		Collections.sort(results, comp);
		return results;
	}

	/**
	 * 
	 * @param bType -
	 *            batch type to create a list of
	 * @param comp -
	 *            comparator to use or default if null
	 * @return - empty list or one containing sorted batches of requested type.
	 */
	public List getSortedBatches(BatchType bType, Comparator comp) {
		List results = new ArrayList(getBatches(bType));
		Collections.sort(results, comp);
		return results;
	}

	public void update(Observable arg0, Object arg1) {
		setChanged();
		notifyObservers(arg1);
	}

	// 
	// DeepCopy/DeepClone
	//

	public void deepCopy(Object resource) {
		if (resource instanceof BatchCache) {
			BatchCache srcCache = (BatchCache) resource;
			for (Iterator i = srcCache.iterator(); i.hasNext();) {
				Object obj = srcCache.get((String) i.next());
				if (obj instanceof ReagentBatch) {
					put((AbstractBatch) ((ReagentBatch) obj).deepClone());
				} else if (obj instanceof ProductBatch) {
					put((AbstractBatch) ((ProductBatch) obj).deepClone());
				}
			}
		}
	}

	public Object deepClone() {
		BatchCache bc = new BatchCache();
		bc.deepCopy(this);
		return bc;
	}

	/**
	 * @param batchNumber
	 */
	public boolean checkIfExists(BatchNumber batchNumber) {
		boolean isFound = false;// getBatchCache().getBatch(batchnumber) doesnt seem to work
		List l_actual = getBatches(BatchType.ACTUAL_PRODUCT);
		Iterator it = l_actual.iterator();
		while (it.hasNext()) {
			AbstractBatch aBatch = (AbstractBatch) it.next();
			if (aBatch.getBatchNumberAsString().equals(batchNumber.toString())) {
				isFound = true;
				break;
			}
		}
		return isFound;
	}

	public boolean hasRegisteredBatches() {
		boolean result = false;
		for (Iterator i = getProducts().iterator(); i.hasNext() && !result;) {
			ProductBatch pb = (ProductBatch) i.next();
			if (pb.getRegInfo() != null && !pb.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.NOT_REGISTERED))
				result = true;
		}
		return result;
	}

	//
	// Implements BatchRegistrationListener
	//
	/**
	 * Adds object that implements BatchRegistrationListener interface to the list of Objects to be notified when registrationStatus
	 * changes.
	 * 
	 * @param o -
	 *            object implementing BatchRegistrationListener interface
	 */
	public void addRegistrationListener(BatchRegistrationListener o) {
		if (!regListeners.contains(o))
			regListeners.add(o);
	}

	/**
	 * Removes object if it exists in the list of listeners.
	 * 
	 * @param o
	 */
	public void deleteRegistrationListener(BatchRegistrationListener o) {
		regListeners.remove(o);
	}

	public void batchRegistrationChanged(ChangeEvent e) {
		// notify listeners
		if (regListeners != null) {
			for (Iterator i = regListeners.iterator(); i.hasNext();) {
				BatchRegistrationListener batchRegListener = (BatchRegistrationListener) i.next();
				if (batchRegListener != null) {
					batchRegListener.batchRegistrationChanged(e);
				}

			}
		}
	}
}
