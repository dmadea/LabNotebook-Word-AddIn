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

import com.chemistry.enotebook.experiment.utils.GUIDUtil;




public class ReactionSchemeModel extends CeNAbstractModel {

	public static final long serialVersionUID = 7526472295622776147L;

	public static final int ISISDRAW = 1;
	public static final int CHEMDRAW = 2;

	private byte[] viewSketch; // possible future implementation
	private byte[] stringSketch; // String representation of Sketch.( Compressed/encoded chime,Mol etc )
	private byte[] nativeSketch; // ISIS/Draw or ChemDraw representation
	private String nativeSketchFormat; // Type of sketch stored in native format
	private String reactionType = CeNConstants.REACTIONTYPE_INTENDED; // Type of reaction INTENDED vs GENERIC vs ACTUAL defined in CeNConstants

	private String sythesisRouteReference;
	private String vrxId; // virtual reaction id for reaction
	private String protocolId;
	private String stringSketchFormat; // Compressed/encoded chime,Mol etc
	private String reactionId;
	private boolean toInsertToCus;

	public ReactionSchemeModel() {
		// Create Key for this object ( eventually step_key )
		this.key = GUIDUtil.generateGUID(this);
	}

	public ReactionSchemeModel(String key, String reactionType) {
		this.key = key;
		this.reactionType = reactionType;
	}

	public ReactionSchemeModel(String reactionType) {
		this();
		this.reactionType = reactionType;
	}

	public ReactionSchemeModel(String reactionType, byte[] stringSketch, byte[] nativeSketch, byte[] viewSketch) {
		this(reactionType);
		this.viewSketch = viewSketch;
		this.nativeSketch = nativeSketch;
		this.stringSketch = stringSketch;
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
			this.modelChanged = true;
		}
	}

	public String getStringSketchFormat() {
		return stringSketchFormat;
	}

	public void setStringSketchFormat(String stringSketchFormat) {
		this.stringSketchFormat = stringSketchFormat;
		this.modelChanged = true;
	}

	public String getNativeSketchFormat() {
		return nativeSketchFormat;
	}

	public void setNativeSketchFormat(String nativeSketchFormat) {
		this.nativeSketchFormat = nativeSketchFormat;
		this.modelChanged = true;
	}

	public String getProtocolId() {
		return protocolId;
	}

	public void setProtocolId(String protocolId) {
		this.protocolId = protocolId;
		this.modelChanged = true;
	}

	public String getReactionId() {
		return reactionId;
	}

	public void setReactionId(String reactionId) {
		this.reactionId = reactionId;
		this.modelChanged = true;
	}

	public String getReactionType() {
		return reactionType;
	}

	public void setReactionType(String reactionType) {
		this.reactionType = reactionType;
		this.modelChanged = true;
	}

	public byte[] getStringSketch() {
		return stringSketch;
	}

	public String getStringSketchAsString() {
		if(stringSketch != null)
		{
			return new String(stringSketch);	
		}else
		{
			return "";	
		}
		
	}
	
	public void setStringSketch(byte[] searchSketch) {
		this.stringSketch = searchSketch;
		this.modelChanged = true;
	}

	public String getSythesisRouteReference() {
		return sythesisRouteReference;
	}

	public void setSythesisRouteReference(String sythesisRouteReference) {
		this.sythesisRouteReference = sythesisRouteReference;
		this.modelChanged = true;
	}

	public byte[] getViewSketch() {
		return viewSketch;
	}

	public void setViewSketch(byte[] viewSketch) {
		this.viewSketch = viewSketch;
		this.modelChanged = true;
	}

	public String getVrxId() {
		return vrxId;
	}

	public void setVrxId(String vrxId) {
		this.vrxId = vrxId;
		this.modelChanged = true;
	}

	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append(CeNConstants.XML_VERSION_TAG);
		xmlbuff.append("<Reaction_Properties>");
		xmlbuff.append("<Meta_Data>");
		xmlbuff.append("</Meta_Data>");
		xmlbuff.append("</Reaction_Properties>");
		return xmlbuff.toString();
	}

	public Object deepClone()
	{
		//Scheme key should be the same
		ReactionSchemeModel schemeModel = new ReactionSchemeModel(this.getKey(),this.reactionType);
		schemeModel.deepCopy(this);
	    return schemeModel;	
	}

	public void deepCopy(ReactionSchemeModel src) {
		setNativeSketch(src.nativeSketch);
		setNativeSketchFormat(src.nativeSketchFormat);
		setStringSketch(src.stringSketch);
		setStringSketchFormat(src.stringSketchFormat);
		setViewSketch(src.viewSketch);
		setProtocolId(src.protocolId);
		setReactionId(src.reactionId);
		setVrxId(src.vrxId);
		setSythesisRouteReference(src.sythesisRouteReference);
		setModelChanged(src.modelChanged);
		setLoadedFromDB(src.isLoadedFromDB());
	}

	public boolean isToInsertToCus() {
		return toInsertToCus;
	}
	
	public void setToInsertToCus(boolean toInsertToCus) {
		this.toInsertToCus = toInsertToCus;
	}
	
}
