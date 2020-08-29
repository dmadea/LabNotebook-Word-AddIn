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
 * Created on Aug 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.sdk;

import java.util.List;

/**
 * 
 *
 * TODO Add Class Information
 */
public interface ChemistryReactionInterface {
	public void setReactionProperties(ReactionProperties rxnProp) throws ChemUtilAccessException;
	
	public void addReactantToReaction(byte[] reactant) throws ChemUtilAccessException;
	
	public void addReactantsToReaction(List<byte[]> reactants) throws ChemUtilAccessException;
	
	public void addProductToReaction(byte[] product) throws ChemUtilAccessException;
	
	public void addProductsToReaction(List<byte[]> products) throws ChemUtilAccessException;
	
	public void setReaction(byte[] reaction) throws ChemUtilAccessException;
	
	public boolean isReaction(byte[] reaction);

	public ReactionProperties extractReactionComponents(ReactionProperties rxnProp) throws ChemUtilAccessException;

	public ReactionProperties combineReactionComponents(ReactionProperties rxnProp) throws ChemUtilAccessException;
	
	public String getMDLRxnString(List<String> monomers, List<String> productes);

	public byte[] convertToPicture(byte[] chemistry, int format, int height, int width, double loss, double pixelToMM) throws ChemUtilAccessException;
	
	public void dispose();
}
