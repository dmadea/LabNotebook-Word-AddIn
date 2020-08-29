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

import com.chemistry.enotebook.experiment.common.ObservableObject;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * 
 *
 * 
 * Contains a list of ReactionStep objects The Representative Step for the Reaction object comes from the first ReactionStep which
 * should be a ReactionStep of INTENDED type.
 * 
 */
public class Reaction extends ObservableObject implements DeepCopy, DeepClone {
	
	private static final long serialVersionUID = -6026158239355486484L;

	private static final int HASH_PRIME = 12011;

	private ArrayList rxnSteps = new ArrayList(); // Series of transaction steps

	//
	// Constructors
	//
	/**
	 * Create a reaction object that contains a scheme and a step for the Intended Reaction
	 * 
	 */
	public Reaction() {
		super();
		initialize();
	}

	/**
	 * Constructor that accepts a list of ReactionStep type objects and will use those to initially populate the reaction.
	 * 
	 * @param List
	 *            of ReactionStep objects
	 */
	public Reaction(List rxnSteps) {
		super();
		this.setRxnSteps(rxnSteps);
	}

	/**
	 * Determines whether or not there is an Intended ReactionStep. If not then it creates one. If one exists, it is left alone.
	 * 
	 */
	public void initialize() {
		if (getRxnStepsByReactionType(ReactionType.INTENDED).size() == 0) {
			ReactionScheme rxnScheme = new ReactionScheme(ReactionType.INTENDED);
			ReactionStep intendedRxnStep = new ReactionStep(rxnScheme);
			addReactionStep(intendedRxnStep);
		}
	}

	/**
	 * 
	 * @return ReactionStep - INTENDED ReactionStep if it exists or null
	 */
	public ReactionStep getIntendedReactionStep() {
		ReactionStep intended = null;
		if (getRxnStepsByReactionType(ReactionType.INTENDED).size() == 1) {
			intended = (ReactionStep) getRxnStepsByReactionType(ReactionType.INTENDED).get(0);
		}
		return intended;
	}

	/**
	 * 
	 * @return the Representative ReactionStep's key
	 */
	public String getKey() {
		String key = "";
		if (rxnSteps.size() > 0)
			key = (getRepresentativeReactionStep()).getKey();
		return key;
	}

	//
	// Getters and Setters
	//
	/**
	 * @return Returns the reference to the rxnSteps.
	 */
	public List getRxnSteps() {
		return rxnSteps;
	}

	public List getRxnStepsCopy() {
		return (List) rxnSteps.clone();
	}

	/**
	 * Removes all previous ReactionStep objects and replaces them with the ones provided in the parameter.
	 * 
	 * @param rxnSteps
	 *            The rxnSteps to set.
	 */
	public void setRxnSteps(List rxnSteps) {
		removeAllObservablesFromList(this.rxnSteps);
		addAllObservablesToList(rxnSteps, this.rxnSteps);
		reindexSteps(0);
		setModified(true);
	}

	/**
	 * Uses the ReactionStep's key to retrieve the associated ReactionStep object
	 * 
	 * @param stepKey
	 * @return null if key is not matched with current set of steps
	 */
	public ReactionStep getRxnStepWithKey(String stepKey) {
		boolean found = false;
		ReactionStep rxnStep = null;
		for (int i = 0; i < rxnSteps.size() && !found; i++) {
			rxnStep = (ReactionStep) rxnSteps.get(i);
			if (stepKey.equals(rxnStep.getKey()))
				found = true;
			else
				rxnStep = null;
		}
		return rxnStep;
	}

	/**
	 * MedChem reaction types will all be ReactionType.INTENDED
	 * 
	 * @param type
	 * @return Always returns a list. It will be empty if the ReactionType is not found.
	 */
	public List getRxnStepsByReactionType(ReactionType type) {
		ArrayList result = new ArrayList();
		ReactionStep rxnStep = null;
		for (int i = 0; i < rxnSteps.size(); i++) {
			rxnStep = (ReactionStep) rxnSteps.get(i);
			if (type.equals(rxnStep.getReactionType()))
				result.add(rxnStep);
			else
				rxnStep = null;
		}
		return result;
	}

	/**
	 * Representative reaction for a MedChem notebook page is the only reaction of the page. Returns the ReagentBatch objects of the
	 * Representative ReactionStep if it exists. Otherwise return an empty list.
	 * 
	 * @return List of ReagentBatch objects or empty list.
	 */
	public List getRepresentativeReagents() {
		ReactionStep result = getRepresentativeReactionStep();
		if (result != null)
			return result.getReactants();
		return new ArrayList();
	}

	/**
	 * Representative reaction for a MedChem notebook page is the only reaction of the page. Returns the ProductBatch objects of the
	 * Representative ReactionStep if it exists. Otherwise it returns an empty list.
	 * 
	 * @return List of ProductBatch objects or empty list.
	 */
	public List getRepresentativeProducts() {
		ReactionStep result = getRepresentativeReactionStep();
		if (result != null)
			return result.getProducts();
		return new ArrayList();
	}

	/**
	 * Each Notebook page has a representative or governing ReactionStep. That ReactionStep is held in the first ReactionStep and
	 * typed ReactionStepType.INTENDED.
	 * 
	 * Currently This is effectively the same as getIntendedReactionStep() In the future we may decide that the representative step
	 * is something else entirely. Please use getIntendedReactionStep() if that is what you really mean.
	 * 
	 * This retrieves that ReactionStep if it exists otherwise returns null.
	 * 
	 * @return ReactionStep object of type INTENDED or null
	 */
	public ReactionStep getRepresentativeReactionStep() {
		return getIntendedReactionStep();
	}

	/**
	 * Adds the specified ReactionStep object to the reaction unless it is a type INTENDED. Only one INTENDED ReactionStep is
	 * allowed. Hence if one exists it is replaced by the incoming INTENDED ReactionStep
	 * 
	 * @param rxnStep
	 */
	public void addReactionStep(ReactionStep rxnStep) {
		if (rxnStep.getReactionType() == ReactionType.INTENDED && getIntendedReactionStep() != null) {
			// make sure there is only one.
			removeReactionStep(getIntendedReactionStep());
		}
		rxnStep.setStepNumber(rxnSteps.size());
		rxnStep.addObserver(this);
		rxnSteps.add(rxnStep);
		setModified(true);
	}

	/**
	 * Removes observer from ReactionStep and removes the step from the list.
	 * 
	 * @param rxnStep
	 */
	public void removeReactionStep(ReactionStep rxnStep) {
		rxnSteps.remove(rxnStep);
		rxnStep.deleteObserver(this);
		reindexSteps(rxnStep.getStepNumber());
		setModified(true);
	}

	/**
	 * Uses the index to get the ReactionStep object to remove. Then it removes the observer from the ReactionStep and removes that
	 * object from the Reaction.
	 * 
	 * @param stepNumber
	 */
	public void removeReactionStep(int stepNumber) {
		removeReactionStep((ReactionStep) rxnSteps.get(stepNumber));
	}

	private void reindexSteps(int fromStep) {
		// reindex the array starting from removed step
		ReactionStep tmpStep;
		for (int row = fromStep; row < rxnSteps.size(); row++) {
			tmpStep = (ReactionStep) rxnSteps.get(row);
			tmpStep.setStepNumber(row);
		}
	}

	//
	// Method Overrides
	//

	public boolean equals(Object other) {
		boolean result = false;
		if (other != null && other instanceof Reaction)
			result = (this.hashCode() == ((Reaction) other).hashCode());
		return result;
	}

	public int hashCode() {
		int result = HASH_PRIME;
		if (this.rxnSteps != null)
			result = result * this.rxnSteps.hashCode();
		return result;
	}

	public void dispose() {
		Iterator cacheIterator = rxnSteps.iterator();
		while (cacheIterator.hasNext()) {
			ReactionStep rxnStep = (ReactionStep) cacheIterator.next();
			rxnStep.dispose();
		}
		rxnSteps.clear();
	}

	// 
	// DeepCopy/DeepClone
	//

	public void deepCopy(Object resource) {
		if (resource instanceof Reaction) {
			Reaction srcRxn = (Reaction) resource;
			for (Iterator i = srcRxn.getRxnSteps().iterator(); i.hasNext();) {
				Object obj = i.next();
				if (obj instanceof ReactionStep) {
					addReactionStep((ReactionStep) ((ReactionStep) obj).deepClone());
				}
			}
		}
	}

	public Object deepClone() {
		Reaction rxn = new Reaction();
		rxn.deepCopy(this);
		return rxn;
	}

}
