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
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.PictureProperties;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;

/**
 * 
 * 
 *
 * 
 * Contains all information related to a single reaction That means the reaction scheme, and all its steps. Includes a list of
 * batches that comprise the reaction.
 */
public class ReactionScheme extends PersistableObject implements DeepCopy, DeepClone {
	
	private static final long serialVersionUID = -5142730230838426439L;
	
	public static final int ISISDRAW = 1;
	public static final int CHEMDRAW = 2;

	private byte[] viewSketch; // possible future implementation
	private byte[] searchSketch;
	private byte[] nativeSketch; // ISIS/Draw or ChemDraw representation
	private String nativeSketchFormat; // Type of sketch stored in native format
	private ReactionType reactionType; // Type of reaction INTENDED vs GENERIC vs ACTUAL

	private String sythesisRouteReference;
	private String vrxId; // virtual reaction id for reaction
	private String protocolId;
	private String dspReactionDefinition; // Reaction structure from Design Service
	private String reactionId;

	public ReactionScheme(ReactionType reactionType) {
		super();
		this.reactionType = reactionType;
	}

	public ReactionScheme(ReactionType reactionType, byte[] searchSketch, byte[] nativeSketch, byte[] viewSketch) {
		this(reactionType);
		this.viewSketch = viewSketch;
		this.nativeSketch = nativeSketch;
		this.searchSketch = searchSketch;
	}

	/**
	 * @return Returns the jpgSketch.
	 */
	public byte[] getViewSketch() {
		return viewSketch;
	}

	/**
	 * @param jpgSketch
	 *            The jpgSketch to set.
	 */
	public void setViewSketch(byte[] sketch) {
		if (sketch != this.viewSketch) {
			this.viewSketch = sketch;
			setModified(true);
		}
	}

	/**
	 * @return Returns the nativeSketch.
	 */
	public byte[] getNativeSketch() {
		return nativeSketch;
	}

	/**
	 * @param nativeSketch
	 *            The nativeSketch to set.
	 */
	public void setNativeSketch(byte[] sketch) {
		if (sketch != this.nativeSketch) {
			this.nativeSketch = sketch;
			setModified(true);
		}
	}

	/**
	 * @return Returns the reactionType.
	 */
	public ReactionType getReactionType() {
		return reactionType;
	}

	/**
	 * @param reactionType
	 *            The reactionType to set.
	 */
	public void setReactionType(ReactionType reactionType) {
		if (!reactionType.equals(reactionType)) {
			this.reactionType = reactionType;
			setModified(true);
		}
	}

	/**
	 * @return Returns the sketch.
	 */
	public byte[] getSearchSketch() {
		return searchSketch;
	}

	/**
	 * @param sketch
	 *            The sketch to set.
	 */
	public void setSearchSketch(byte[] sketch) {
		if (sketch != this.searchSketch) {
			this.searchSketch = sketch;
			setModified(true);
		}
	}

	/**
	 * @return Returns the sketch format.
	 */
	public String getNativeSketchFormat() {
		return nativeSketchFormat;
	}

	/**
	 * @param format
	 *            The format sketch is in.
	 */
	public void setNativeSketchFormat(String format) {
		if (nativeSketchFormat == null || !nativeSketchFormat.equals(format)) {
			nativeSketchFormat = format;
			setModified(true);
		}
	}

	public void loadSketch(byte[] sketch, int format) throws ChemUtilAccessException {
		if (sketch != this.nativeSketch) {
			this.nativeSketch = sketch;
			nativeSketchFormat = (format == ISISDRAW) ? "ISIS Sketch" : "ChemDraw";

			try {
				byte[] accSketch = null;
				if (sketch != null && sketch.length > 0)
					accSketch = new ChemistryDelegate().convertChemistry(sketch, null, "Chemistry");
				this.searchSketch = accSketch;
			} catch (Exception e) {
				throw new ChemUtilAccessException("Could not load searchSketch", e);
			}

			try {
				byte[] pic = null;
				if (sketch != null && sketch.length > 0)
					pic = new ChemistryDelegate().convertReactionToPicture(sketch, PictureProperties.PNG, 30, 70, 1.0, 0.26458);
				this.viewSketch = pic;
			} catch (Exception e) {
				throw new ChemUtilAccessException("Could not load viewSketch", e);
			}

			setModified(true);
		}
	}

	public void loadSearchSketch(byte[] sketch) throws ChemUtilAccessException {
		if (sketch != this.searchSketch) {
			try {
				byte[] accSketch = null;
				if (sketch != null && sketch.length > 0)
					accSketch = new ChemistryDelegate().convertChemistry(sketch, null, "Chemistry");
				this.searchSketch = accSketch;
			} catch (Exception e) {
				throw new ChemUtilAccessException("Could not load searchSketch", e);
			}

			setModified(true);
		}
	}

	// 
	// DeepCopy/DeepClone
	//
	public void deepCopy(Object resource) {
		if (resource instanceof ReactionScheme) {
			ReactionScheme srcScheme = (ReactionScheme) resource;
			viewSketch = srcScheme.viewSketch;
			searchSketch = srcScheme.searchSketch;
			nativeSketch = srcScheme.nativeSketch;
			nativeSketchFormat = srcScheme.nativeSketchFormat;
			reactionType = srcScheme.reactionType;
		}
	}

	public Object deepClone() {
		ReactionScheme rxnScheme = new ReactionScheme(ReactionType.INTENDED);
		rxnScheme.deepCopy(this);
		return rxnScheme;
	}

	public String getDspReactionDefinition() {
		return dspReactionDefinition;
	}

	public void setDspReactionDefinition(String dspReactionDefinition) {
		this.dspReactionDefinition = dspReactionDefinition;
	}

	public String getProtocolId() {
		return protocolId;
	}

	public void setProtocolId(String protocolId) {
		this.protocolId = protocolId;
	}

	public String getReactionId() {
		return reactionId;
	}

	public void setReactionId(String reactionId) {
		this.reactionId = reactionId;
	}

	public String getSythesisRouteReference() {
		return sythesisRouteReference;
	}

	public void setSythesisRouteReference(String sythesisRouteReference) {
		this.sythesisRouteReference = sythesisRouteReference;
	}

	public String getVrxId() {
		return vrxId;
	}

	public void setVrxId(String vrxId) {
		this.vrxId = vrxId;
	}

}
