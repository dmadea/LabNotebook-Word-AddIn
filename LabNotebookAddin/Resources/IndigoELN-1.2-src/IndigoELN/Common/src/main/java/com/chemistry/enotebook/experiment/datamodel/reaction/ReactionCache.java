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
 * ReactionCache.java
 * 
 * Created on Aug 5, 2004
 *
 * 
 */
package com.chemistry.enotebook.experiment.datamodel.reaction;

import com.chemistry.enotebook.experiment.common.ObservableObject;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;

import java.util.*;

/**
 * 
 * @date Aug 5, 2004
 * 
 * Load all reaction information for this page.
 * 
 */

public class ReactionCache extends ObservableObject implements DeepCopy, DeepClone {
	
	private static final long serialVersionUID = 1869475202675614549L;
	
	private Map reactions = Collections.synchronizedMap(new LinkedHashMap());

	public ReactionCache() {
	}

	public ReactionCache(Map newCache) {
		reactions.putAll(newCache);

		Iterator it = reactions.keySet().iterator();
		while (it.hasNext())
			((ObservableObject) (get((String) it.next()))).addObserver(this);
	}

	public void dispose() {
		if (reactions != null) {
			Iterator cacheIterator = reactions.keySet().iterator();
			while (cacheIterator.hasNext()) {
				Reaction rxn = (Reaction) reactions.get((String) cacheIterator.next());
				rxn.dispose();
			}
			reactions.clear();
			reactions = null;
		}
	}

	public Reaction createReaction(String rxnKey) {
		Reaction r = new Reaction();
		addReaction(rxnKey, r);
		return r;
	}

	public boolean hasRxn(String rxnKey) {
		return reactions.containsKey(rxnKey);
	}

	public boolean hasRxnStep(String rxnKey) {
		boolean result = false;

		Iterator ri = reactions.keySet().iterator();
		while (ri.hasNext()) {
			String strRxnKey = (String) ri.next();
			List steps = ((Reaction) reactions.get(strRxnKey)).getRxnSteps();
			for (Iterator si = steps.iterator(); si.hasNext() && !result;) {
				ReactionStep step = (ReactionStep) si.next();
				if (step.getKey().equals(rxnKey))
					result = true;
			}
		}

		return result;
	}

	public boolean hasRxnScheme(String rxnKey) {
		boolean result = false;

		Iterator ri = reactions.keySet().iterator();
		while (ri.hasNext()) {
			String strRxnKey = (String) ri.next();
			List steps = ((Reaction) reactions.get(strRxnKey)).getRxnSteps();
			for (Iterator si = steps.iterator(); si.hasNext() && !result;) {
				ReactionStep step = (ReactionStep) si.next();
				if (step.getReactionScheme().getKey().equals(rxnKey))
					result = true;
			}
		}

		return result;
	}

	public ReactionStep getRxnStepWithKey(String rxnKey) {
		ReactionStep result = null;

		Iterator ri = reactions.keySet().iterator();
		while (ri.hasNext()) {
			String strRxnKey = (String) ri.next();
			List steps = ((Reaction) reactions.get(strRxnKey)).getRxnSteps();
			for (Iterator si = steps.iterator(); si.hasNext() && result == null;) {
				ReactionStep step = (ReactionStep) si.next();
				if (step.getKey().equals(rxnKey))
					result = step;
			}
		}

		return result;
	}

	public ReactionScheme getRxnSchemeWithKey(String rxnKey) {
		ReactionScheme result = null;

		Iterator ri = reactions.keySet().iterator();
		while (ri.hasNext()) {
			String strRxnKey = (String) ri.next();
			List steps = ((Reaction) reactions.get(strRxnKey)).getRxnSteps();
			for (Iterator si = steps.iterator(); si.hasNext() && result == null;) {
				ReactionStep step = (ReactionStep) si.next();
				if (step.getReactionScheme().getKey().equals(rxnKey))
					result = step.getReactionScheme();
			}
		}

		return result;
	}

	public Reaction getReaction(String rxnKey) {
		if (this.hasRxn(rxnKey))
			return (Reaction) reactions.get(rxnKey);
		return null;
	}

	public Map getReactions() {
		return this.reactions;
	}

	public HashMap getMapCopy() {
		return (HashMap) (new HashMap(this.reactions)).clone();
	}

	public void addReaction(String rxnKey, Reaction rxn) {
		if (!hasRxn(rxnKey)) {
			rxn.addObserver(this);
			reactions.put(rxnKey, rxn);
			setModified(true);
		}
	}

	public void addReaction(Reaction rxn) {
		addReaction(rxn.getKey(), rxn);
	}

	public void removeReaction(String rxnKey) {
		if (hasRxn(rxnKey)) {
			Reaction r = null;
			r = (Reaction) reactions.remove(rxnKey);
			r.deleteObserver(this);
			setModified(true);
		}
	}

	/**
	 * Be sure to cast it.next() to a string when passing to get(String rxnKey)
	 * 
	 * @return iterator representing the rxnKeys that are of String type.
	 */
	public Iterator iterator() {
		return reactions.keySet().iterator();
	}

	/**
	 * mimics the getReaction(String rxnKey) use in loops with the iterator. it.next() returns a string that can be passed in here.
	 * 
	 * @param rxnKey
	 * @return
	 */
	public Reaction get(String rxnKey) {
		return (Reaction) reactions.get(rxnKey);
	}

	public void clear() {
		Iterator it = reactions.keySet().iterator();
		while (it.hasNext())
			((ObservableObject) (get((String) it.next()))).deleteObserver(this);

		reactions.clear();
		setModified(true);
	}

	// 
	// DeepCopy/DeepClone
	//

	public void deepCopy(Object resource) {
		if (resource instanceof ReactionCache) {
			ReactionCache srcCache = (ReactionCache) resource;
			for (Iterator i = srcCache.iterator(); i.hasNext();) {
				Object obj = srcCache.get((String) i.next());
				if (obj instanceof Reaction) {
					addReaction((Reaction) ((Reaction) obj).deepClone());
				}
			}
		}
	}

	public Object deepClone() {
		ReactionCache rc = new ReactionCache();
		rc.deepCopy(this);
		return rc;
	}

}
