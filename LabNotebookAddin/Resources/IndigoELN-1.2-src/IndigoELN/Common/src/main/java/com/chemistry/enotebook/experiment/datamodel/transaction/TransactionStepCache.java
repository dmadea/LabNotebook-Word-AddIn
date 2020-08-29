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
package com.chemistry.enotebook.experiment.datamodel.transaction;

import com.chemistry.enotebook.experiment.common.ObservableObject;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchTypeException;

import java.util.*;

/**
 * 
 * 
 *
 * 
 * Adding steps is pretty transparent, but removing requires the rebuilding of the transaction steps. TODO: need to make a counter
 */
public class TransactionStepCache extends ObservableObject implements Observer, DeepCopy, DeepClone {

	private static final long serialVersionUID = -8517214447607431098L;
	
	// Holds AbstractBatch objects categorized by batch GUID (that means "key") values;
	private List transactionList = null;
	private int nextStep = 0;

	public TransactionStepCache() {
		if (transactionList == null) {
			transactionList = Collections.synchronizedList(new ArrayList());
		}
	}

	public TransactionStepCache(Collection txnSteps) {
		transactionList.addAll(txnSteps);
		resetIndex();
	}

	public void dispose() {
		for (Iterator i = transactionList.iterator(); i.hasNext();) {
			((TransactionStep) i.next()).dispose();
		}
	}

	public boolean hasStep(TransactionStep txnStep) {
		boolean result = false;
		// Check to see if the step has the correct index:
		// Really this should be the only test
		if (txnStep.getStepNumber() > 0 && txnStep.getStepNumber() < transactionList.size()) {
			result = (txnStep.equals((TransactionStep) transactionList.get(txnStep.getStepNumber())));
		} // If this doesn't work then the Cache isn't behaving.
		return result;
	}

	/**
	 * Creates a transactionStep of the passed in type. Adds it to the cache and passes a reference to the new step back.
	 * 
	 * @param txnStepType
	 * @return created TransactionStep
	 * @throws InvalidBatchTypeException
	 */
	public TransactionStep createStep(TransactionStepType txnStepType) throws InvalidBatchTypeException {
		TransactionStep ts = TransactionStepFactory.getTransactionStep(txnStepType);
		transactionList.add(ts.getStepNumber(), ts);
		resetIndex();
		return ts;
	}

	/**
	 * 
	 * @param stepNumber
	 * @return null if stepNumber is not found, TransactionStep if found
	 */
	public TransactionStep getStep(int stepNumber) {
		TransactionStep retVal = null;
		if (stepNumber <= transactionList.size())
			retVal = (TransactionStep) transactionList.get(stepNumber);
		return retVal;
	}

	/**
	 * Moves all transactions to the deletedTransactionList. Set clearFinally to true to remove all values from the cache without
	 * updating Observers.
	 * 
	 * @param clearFinally
	 */
	public void clear(boolean clearFinally) {
		removeAllObservablesFromList(transactionList);
	}

	/**
	 * Adds to the end of the list. Side-effect TransactionStep will have its stepNumber set
	 * 
	 * @param txnStep
	 *            object to add to the cache
	 */
	public void add(TransactionStep txnStep) {
		if (!hasStep(txnStep)) {
			txnStep.addObserver(this);
			transactionList.add(txnStep);
			resetIndex();
		}
	}

	/**
	 * Adds a list of observableObjects to the transactionList
	 * 
	 * @param allSteps
	 */
	public void addAll(List allSteps) {
		addAllObservablesToList(allSteps, transactionList);
		resetIndex();
	}

	/**
	 * Remove the step indicated by the stepNumber Resets Index on remaining active steps
	 * 
	 * @param stepNumber
	 * @return true upon success.
	 */
	public TransactionStep remove(int stepNumber) {
		TransactionStep result = null;
		if (stepNumber >= 0 && stepNumber < transactionList.size()) {
			result = (TransactionStep) transactionList.get(stepNumber);
			if (result != null)
				remove(result);
		}
		return result;
	}

	/**
	 * Remove the TransactionStep from the cache if it exists. Resets Index on remaining active steps
	 * 
	 * @param ts -
	 *            step to remove
	 * @return true upon sucess.
	 */
	public void remove(TransactionStep ts) {
		if (ts != null) {
			transactionList.remove(ts);
			ts.deleteObserver(this);
			resetIndex();
		}
	}

	public Iterator iterator() {
		return transactionList.iterator();
	}

	public Object get(int key) {
		return transactionList.get(key);
	}

	public List getStepList() {
		return transactionList;
	}

	/**
	 * Runs through the the cache reestablishing nextstep and synchronizing TransactionStep.setStepNumber() values.
	 * 
	 */
	public void resetIndex() {
		for (int i = 1; i < transactionList.size(); i++) {
			TransactionStep ts = (TransactionStep) transactionList.get(i);
			if (ts.getStepNumber() != i)
				ts.setStepNumber(i);
		}
	}

	/**
	 * Use to set a step into a
	 * 
	 */
	public void insert(TransactionStep ts, int stepNumber) {
		// Easiest thing is to add to the end.
		ts.addObserver(this);
		transactionList.add(ts);
		if (stepNumber > 0 && stepNumber <= transactionList.size()) {
			resetIndex();
		}
		setChanged();
		notifyObservers();
	}

	public List getMapSortedByBatchNumber() {
		Collections.sort(transactionList);
		return transactionList;
	}

	public void update(Observable arg0, Object arg1) {
		setChanged();
		notifyObservers(arg1);
	}

	// 
	// DeepCopy Interface
	//

	public void deepCopy(Object resource) {
		if (resource instanceof TransactionStepCache) {
			TransactionStepCache srcCache = (TransactionStepCache) resource;
			for (Iterator i = srcCache.getStepList().iterator(); i.hasNext();) {
				Object obj = i.next();
				if (obj instanceof TransactionStep) {
					add((TransactionStep) ((TransactionStep) obj).deepClone());
				}
			}
			nextStep = srcCache.nextStep;
		}
	}

	public Object deepClone() {
		TransactionStepCache newCache = new TransactionStepCache();
		newCache.deepCopy(this);
		return newCache;
	}

}
