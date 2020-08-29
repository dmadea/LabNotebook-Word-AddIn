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
package com.chemistry.enotebook.experiment.datamodel.reaction;

import com.chemistry.enotebook.experiment.common.PersistableObject;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;
import com.chemistry.enotebook.experiment.datamodel.batch.AbstractBatch;
import com.chemistry.enotebook.experiment.datamodel.batch.ProductBatch;
import com.chemistry.enotebook.experiment.datamodel.transaction.AdditionTransactionStep;
import com.chemistry.enotebook.experiment.datamodel.transaction.TransactionStep;
import com.chemistry.enotebook.experiment.datamodel.transaction.TransactionStepType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * 
 *
 */
public class ReactionStep extends PersistableObject implements DeepCopy, DeepClone {
	
	private static final long serialVersionUID = 6953895161459960617L;

	private int stepNumber = 0; // 0 is default and step no for summary plan

	private ReactionScheme rxnScheme = null; // Represents the reaction at this step
	private transient ArrayList<TransactionStep> transactions; // List of transactionSteps
	private transient ArrayList products; // List of products associated with this reaction step
	private transient ArrayList reactants; // reactants(monomers) participated in the reaction step
	private String rxnProperties; // Free Text containing properties of the reaction

	private String parentId; // pid of the summary reaction ( value null if this is summary step )
	private String spid;
	private String pid;
	private String description;
	private String name = "";

	// private PlanType planType; //Defines if it is a Summary step or Intermediate step

	/**
	 * Used when no reaction scheme is preferred. Will create a ReactionStep object with a default INTENDED type reaction scheme
	 * object.
	 * 
	 */
	public ReactionStep() {
		this(new ReactionScheme(ReactionType.INTENDED));
	}

	/**
	 * Used to initalize a reaction step with a reaction scheme
	 * 
	 * @param rxnScheme
	 *            ReactionScheme object to be used instead of the default INTENDED type.
	 */
	public ReactionStep(ReactionScheme rxnScheme) {
		super();
		if (rxnScheme == null)
			rxnScheme = new ReactionScheme(ReactionType.INTENDED);
		this.rxnScheme = rxnScheme;
		rxnScheme.addObserver(this);
		transactions = new ArrayList();
		products = new ArrayList();
	}

	/**
	 * Removes all transaction steps, products and nulls the reaction scheme.
	 * 
	 */
	public void clear() {
		clearTransactionSteps();
		clearProducts();

		if (rxnScheme != null)
			rxnScheme.deleteObserver(this);
		setReactionScheme(null);
	}

	/**
	 * Removes all products associated with this step
	 * 
	 */
	public void clearProducts() {
		removeAllObservablesFromList(products);
		setModified(true);
	}

	/**
	 * Removes all transaction steps from this reaction step
	 * 
	 */
	public void clearTransactionSteps() {
		removeAllObservablesFromList(transactions);
		setModified(true);
	}

	/**
	 * @return Returns the rxnProperties.
	 */
	public String getRxnProperties() {
		return rxnProperties;
	}

	/**
	 * @param rxnProperties
	 *            The rxnProperties to set.
	 */
	public void setRxnProperties(String rxnProperties) {
		if ((this.rxnProperties == null && rxnProperties != null) || (this.rxnProperties != null && rxnProperties == null)) {
			this.rxnProperties = rxnProperties;
			setModified(true);
		} else if (!this.rxnProperties.equals(rxnProperties)) {
			this.rxnProperties = rxnProperties;
			setModified(true);
		}
	}

	/**
	 * 
	 * @return Reaction type for the step or null if none exists.
	 */
	public ReactionType getReactionType() {
		if (rxnScheme != null)
			return rxnScheme.getReactionType();
		return null;
	}

	/**
	 * 
	 * @param rsType
	 *            ReactionStepType to set this reaction step
	 */
	public void setReactionType(ReactionType rsType) {
		if (rsType != null) {
			if (rxnScheme == null)
				rxnScheme = new ReactionScheme(ReactionType.INTENDED);
			rxnScheme.addObserver(this);
			rxnScheme.setReactionType(rsType);
		}
	}

	/**
	 * @return Returns the stepNumber.
	 */
	public int getStepNumber() {
		return stepNumber;
	}

	public String toString() {
		return "Step " + stepNumber + name;
	}

	/**
	 * @param stepNumber
	 *            The stepNumber to set.
	 */
	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
		setModified(true);
	}

	public ReactionScheme getReactionScheme() {
		return this.rxnScheme;
	}

	public void setReactionScheme(ReactionScheme rxnScheme) {
		this.rxnScheme = rxnScheme;
		if (rxnScheme != null)
			rxnScheme.addObserver(this);
		setModified(true);
	}

	/**
	 * @return Returns the transaction steps for this reaction step.
	 */
	public List getTransactions() {
		return transactions;
	}

	/**
	 * @param a
	 *            List containing transactionSteps for this ReactionStep.
	 */
	public void setTransactions(List transactions) {
		addAllObservablesToList(transactions, this.transactions);
		setModified(true);
	}

	/**
	 * Useful for checking whether or not the transaction exits
	 * 
	 * @param ts
	 * @return boolean true if collection contains the transaction step.
	 */
	public boolean hasTransactionStep(TransactionStep ts) {
		return transactions.contains(ts);
	}

	/**
	 * Will not allow two identical transaction steps to be added to the same reaction step
	 * 
	 * @param ts -
	 *            a valid transaction step
	 */
	public void addTransactionStep(TransactionStep ts) {
		if (!hasTransactionStep(ts)) {
			transactions.add(ts);
			reindexSteps(0);
			setModified(true);
		}
	}

	/**
	 * removes the transaction step indicated and reindexes the list
	 * 
	 * @param ts -
	 *            TransactionStep object
	 */
	public void removeTransactionStep(TransactionStep ts) {
		transactions.remove(ts);
		ts.deleteObserver(this);
		reindexSteps(ts.getStepNumber());
		setModified(true);
	}

	/**
	 * removes the transaction step at the indicated index and reindexes the rest of the list.
	 * 
	 * @param tsNumber -
	 *            integer representation of the stepNumber - should be unique across the page
	 */
	public void removeTransactionStep(int tsNumber) {
		if (tsNumber < transactions.size()) {
			TransactionStep ts = (TransactionStep) transactions.remove(tsNumber);
			if (ts != null)
				ts.deleteObserver(this);
			if (tsNumber < transactions.size())
				reindexSteps(tsNumber);
			setModified(true);
		}
	}

	private void reindexSteps(int fromStep) {
		// reindex the array starting from removed step
		TransactionStep tmpStep;
		for (int row = fromStep; row < transactions.size(); row++) {
			tmpStep = (TransactionStep) transactions.get(row);
			tmpStep.setStepNumber(row);
		}
	}

	/**
	 * Returns the TransactionStep at the stepNumber given. Returns null if the step doesn't exist in this reactionStep. Keep in
	 * mind that the transactionSteps are numbered for the whole NotebookPage. TODO: make another wrapper class that uses only local
	 * transactionStep numbers.
	 * 
	 * @param stepNumber
	 * @return null if step number is not valid. TransactionStep associated with the numbe given.
	 */
	public TransactionStep getTransactionStep(int stepNumber) {
		if (transactions.size() > stepNumber)
			return (TransactionStep) transactions.get(stepNumber);
		return null;
	}

	/**
	 * find the transaction step that added the given batch. returns the first one found.
	 * 
	 * @param searchBatch -
	 *            batch to search for
	 * @return null if no batch matches otherwise return the AdditionTransactionStep that contains the batch being searched
	 */
	public AdditionTransactionStep getTransactionStep(AbstractBatch searchBatch) {
		if (searchBatch != null)
			for (Iterator i = transactions.iterator(); i.hasNext();) {
				Object obj = i.next();
				if (obj instanceof AdditionTransactionStep && ((AdditionTransactionStep) obj).getSource().equals(searchBatch)) {
					return (AdditionTransactionStep) obj;
				}
			}
		return null;
	}

	public List getTransactionStepsByType(TransactionStepType tst) {
		ArrayList result = new ArrayList();
		TransactionStep ts = null;
		for (int i = 0; i < transactions.size(); i++) {
			ts = (TransactionStep) transactions.get(i);
			if (tst.equals(((TransactionStep) transactions.get(i)).getType()))
				result.add(transactions.get(i));
		}
		return result;
	}

	/**
	 * 
	 * @return an empty list if no additions have been made to this step a list with anything that was used on the left of the
	 *         reaction arrow if there are additions. All will be of type ReagentBatch
	 */
	public List getReactants() {
		/*
		 * ArrayList results = new ArrayList(); TransactionStep tempTS; AdditionTransactionStep ats; for (int i = 0; i <
		 * transactions.size(); i++) { tempTS = (TransactionStep) transactions.get(i); if (tempTS instanceof
		 * AdditionTransactionStep) { ats = (AdditionTransactionStep) tempTS; results.add(ats.getSource()); } } return results;
		 */
		return reactants;
	}

	/**
	 * 
	 * @return a list of ProductBatch objects associated with this step
	 */
	public List getProducts() {
		return products;
	}

	/**
	 * Clears the current ProductBatch list and adds the list provided
	 * 
	 * @param allProductsForStep -
	 *            list of ProductBatch objects in order
	 */
	public void setProducts(List allProductsForStep) {
		this.products.clear();
		addAllObservablesToList(allProductsForStep, products);
		setModified(true);
	}

	/**
	 * Same as ArrayList.addAll()
	 * 
	 * @param productList -
	 *            must be ProductBatch objects
	 */
	public void addAllProducts(List productList) {
		addAllObservablesToList(productList, products);
		setModified(true);
	}

	public ProductBatch getProduct(int leftToRightOrdinalPosition) {
		if (leftToRightOrdinalPosition < products.size())
			return (ProductBatch) products.get(leftToRightOrdinalPosition);
		return null;
	}

	public void addProduct(ProductBatch pb) {
		pb.setIntendedBatchAdditionOrder(this.products.size());
		products.add(pb.getIntendedBatchAdditionOrder(), pb);
		setModified(true);
	}

	public void removeProduct(ProductBatch pb) {
		products.remove(pb);
		pb.deleteObserver(this);
		reindexProducts();
		setModified(true);
	}

	public void removeProduct(int leftToRightOrdinalPosition) {
		ProductBatch pb = (ProductBatch) products.remove(leftToRightOrdinalPosition);
		if (pb != null)
			pb.deleteObserver(this);
		reindexProducts();
		setModified(true);
	}

	public void syncReactants(List reactantList) {
		// test the list to see if we have reactants to match.
		List stepReactants = getReactants();
		// Assumption: vector is in order from left to right.
		// Assumption: reactants are in order of addition left to right.
		for (int additionOrder = 0; additionOrder < reactantList.size(); additionOrder++) {
			// match reactant with reagent
		}
	}

	public void syncProducts(List productList) {
		for (int additionOrder = 0; additionOrder < products.size(); additionOrder++) {
			// match product by location
			// check for deletion by doing exact match before deleting
		}
	}



	/**
	 * Used to sort products by intendedAdditionOrder
	 * 
	 */
	private void sortProducts() {

	}

	private void reindexProducts() {
		// reindex the array starting from removed step
		ProductBatch batch;
		for (int row = 0; row < products.size(); row++) {
			batch = (ProductBatch) products.get(row);
			batch.setIntendedBatchAdditionOrder(row);
		}
	}

	public void dispose() {
		this.rxnScheme = null;
		Iterator cacheIterator = transactions.iterator();
		while (cacheIterator.hasNext()) {
			Object trans = cacheIterator.next();
			trans = null;
		}
		transactions.clear();
		products.clear();
	}

	// 
	// DeepCopy/DeepClone
	//
	public void deepCopy(Object resource) {
		if (resource instanceof ReactionStep) {
			ReactionStep srcStep = (ReactionStep) resource;
			stepNumber = srcStep.stepNumber;
			rxnScheme.deepCopy(srcStep.rxnScheme);
			transactions.clear();
			for (TransactionStep tStep : srcStep.transactions) {
				transactions.add((TransactionStep) tStep.deepClone());
			}
			products.clear();
			for (Iterator p = products.iterator(); p.hasNext();) {
				ProductBatch pb = (ProductBatch) p.next();
				products.add(pb.deepClone());
			}
			rxnProperties = srcStep.rxnProperties;
		}
	}

	public Object deepClone() {
		ReactionStep rxnStep = new ReactionStep();
		rxnStep.deepCopy(this);
		return rxnStep;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public ReactionScheme getRxnScheme() {
		return rxnScheme;
	}

	public void setRxnScheme(ReactionScheme rxnScheme) {
		this.rxnScheme = rxnScheme;
	}

	public String getSpid() {
		return spid;
	}

	public void setSpid(String spid) {
		this.spid = spid;
	}

	public void setProducts(ArrayList products) {
		this.products = products;
	}

	public void setReactants(ArrayList reactants) {
		this.reactants = reactants;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
