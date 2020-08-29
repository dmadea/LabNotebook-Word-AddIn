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

package com.chemistry.enotebook.sdk.indigo;

import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ChemistryReactionInterface;
import com.chemistry.enotebook.sdk.PictureProperties;
import com.chemistry.enotebook.sdk.ReactionProperties;
import com.ggasoftware.indigo.Indigo;
import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.IndigoRenderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChemistryReaction implements ChemistryReactionInterface {
	public void addProductToReaction(byte[] product) throws ChemUtilAccessException {
	}

	public void addProductsToReaction(List<byte[]> products) throws ChemUtilAccessException {
	}

	public void addReactantToReaction(byte[] reactant) throws ChemUtilAccessException {
	}

	public void addReactantsToReaction(List<byte[]> reactants) throws ChemUtilAccessException {
	}

	public ReactionProperties combineReactionComponents(ReactionProperties rxnProp) throws ChemUtilAccessException {
		Indigo indigo = new Indigo();
		
		@SuppressWarnings("unused")
		IndigoRenderer render = new IndigoRenderer(indigo);
		
		indigo.setOption("molfile-saving-mode", "2000");
		indigo.setOption("ignore-stereochemistry-errors", "true");
		IndigoObject handle = indigo.createReaction();
		
		// reactants
		List<byte[]> reactants = rxnProp.Reactants;
		for (Iterator<byte[]> iterator = reactants.iterator(); iterator.hasNext();) {
			byte[] reactant = iterator.next();
			IndigoObject molecule = indigo.loadMolecule(new String(reactant));
			handle.addReactant(molecule);
		}
		// products
		List<byte[]> products = rxnProp.Products;
		for (Iterator<byte[]> iterator = products.iterator(); iterator.hasNext();) {
			byte[] product = iterator.next();
			IndigoObject molecule = indigo.loadMolecule(new String(product));
			handle.addProduct(molecule);
		}
		
		indigo.setOption("render-bond-length", 30d);
		handle.layout();

		rxnProp.Reaction = handle.rxnfile().getBytes();
		return rxnProp;
	}

	public byte[] convertToPicture(byte[] chemistry, int format, int height, int width, double loss, double pixelToMM) throws ChemUtilAccessException {
		Indigo indigo = new Indigo();
		IndigoRenderer renderer = new IndigoRenderer(indigo);
		indigo.setOption("render-bond-length", -1d);
		indigo.setOption("render-label-mode", "hetero");
		indigo.setOption("ignore-stereochemistry-errors", "true");

		IndigoObject indigoObject = indigo.loadQueryReaction(chemistry);

		indigo.setOption("render-image-size", width, height);
		
		switch (format) {
		case PictureProperties.PNG:
			indigo.setOption("render-output-format", "png");
			break;
		case PictureProperties.SVG:
			indigo.setOption("render-output-format", "svg");
			break;
		default: 
			indigo.setOption("render-output-format", "png");
		}

		return renderer.renderToBuffer(indigoObject);
	}

	public void dispose() {
	}

	public ReactionProperties extractReactionComponents(ReactionProperties rxnProp) throws ChemUtilAccessException {
		Indigo indigo = new Indigo();
		indigo.setOption("molfile-saving-mode", "2000");
		indigo.setOption("ignore-stereochemistry-errors", "true");
		
		byte[] reaction = rxnProp.Reaction;
		String str = new String(reaction);
		IndigoObject handle = indigo.loadReaction(str);

		IndigoObject iterReactants = handle.iterateReactants();
		rxnProp.Reactants = new ArrayList<byte[]>();
		do {
			rxnProp.Reactants.add(iterReactants.next().molfile().getBytes());
		} while (iterReactants.hasNext());
		
		IndigoObject iterProducts = handle.iterateProducts();
		rxnProp.Products = new ArrayList<byte[]>();
		do {
			rxnProp.Products.add(iterProducts.next().molfile().getBytes());
		} while (iterProducts.hasNext());

		return rxnProp;
	}

	public boolean isReaction(byte[] reaction) {
		Indigo indigo = new Indigo();
		indigo.setOption("ignore-stereochemistry-errors", "true");
		String str = new String(reaction);
		IndigoObject handle = indigo.loadQueryReaction(str);
		return (handle.countReactants() > 0) && (handle.countProducts() > 0);
	}

	public void setReaction(byte[] reaction) throws ChemUtilAccessException {
	}

	public void setReactionProperties(ReactionProperties rxnProp) throws ChemUtilAccessException {
	}

	public String getMDLRxnString(List<String> monomers, List<String> productes) {
		Indigo indigo = new Indigo();
		indigo.setOption("molfile-saving-mode", "2000");
		indigo.setOption("ignore-stereochemistry-errors", "true");
		
		IndigoObject rxnHandle = indigo.createReaction();
		for (int i = 0; i < monomers.size(); i++) {
			String str = new StringBuffer().append(monomers.get(i)).toString();
			IndigoObject moleculeHandle = indigo.loadMolecule(str);
			rxnHandle.addReactant(moleculeHandle);
		}
		
		for (int i = 0; i < productes.size(); i++) {
			String str = new StringBuffer().append(productes.get(i)).toString();
			IndigoObject moleculeHandle = indigo.loadMolecule(str);
			rxnHandle.addProduct(moleculeHandle);
		}

		return rxnHandle.rxnfile();
	}
}
