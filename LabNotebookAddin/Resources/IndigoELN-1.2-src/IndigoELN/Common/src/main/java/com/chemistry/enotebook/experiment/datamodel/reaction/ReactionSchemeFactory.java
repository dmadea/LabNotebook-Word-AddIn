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

import com.chemistry.enotebook.experiment.utils.GUIDUtil;

/**
 * 
 * 
 *
 */
public class ReactionSchemeFactory {
	// WARNING: this does not fully configure the Reaction Scheme.
	// The pageKey must be set for it to be valid!
	private ReactionSchemeFactory() {
	}

	public static ReactionScheme getReactionScheme(String type) {
		ReactionType rt = null;
		if (ReactionType.INTENDED.toString().equalsIgnoreCase(type))
			rt = ReactionType.INTENDED;
		if (ReactionType.STEP.toString().equalsIgnoreCase(type))
			rt = ReactionType.STEP;
		if (ReactionType.CONCEPTION.toString().equalsIgnoreCase(type))
			rt = ReactionType.CONCEPTION;
		if (ReactionType.ACTUAL.toString().equalsIgnoreCase(type))
			rt = ReactionType.ACTUAL;
		if (ReactionType.GENERIC.toString().equalsIgnoreCase(type))
			rt = ReactionType.GENERIC;
		return getReactionScheme(rt);
	}

	public static ReactionScheme getReactionScheme(ReactionType type) {
		return new ReactionScheme(type);
	}

	public static ReactionScheme getNewReactionScheme(ReactionType type) {
		ReactionScheme scheme = new ReactionScheme(type);
		scheme.setKey(GUIDUtil.generateGUID(scheme));
		return scheme;
	}
}
