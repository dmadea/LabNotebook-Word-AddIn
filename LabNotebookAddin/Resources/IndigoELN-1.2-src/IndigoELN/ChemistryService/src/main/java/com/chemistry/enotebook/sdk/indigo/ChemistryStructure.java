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

import com.chemistry.enotebook.sdk.*;
import com.ggasoftware.indigo.Indigo;
import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.IndigoRenderer;

public class ChemistryStructure implements ChemistryStructureInterface {
	public boolean areMoleculesEqual(byte[] chemistry1, byte[] chemistry2) throws ChemUtilAccessException {
		if (chemistry1 == null && chemistry2 == null) 
			return true;
		if (chemistry1 == null || chemistry2 == null) 
			return false;
		
		Indigo indigo = new Indigo();
		indigo.setOption("ignore-stereochemistry-errors", "true");
		String str1 = new String(chemistry1);
		String str2 = new String(chemistry2);
		IndigoObject handle1 = indigo.loadMolecule(str1);
		IndigoObject handle2 = indigo.loadMolecule(str2);
		return indigo.exactMatch(handle1, handle2) != null;
	}

	public ChemistryProperties getMolecularInformation(byte[] chemistry) throws ChemUtilAccessException {
		Indigo indigo = new Indigo();
		indigo.setOption("ignore-stereochemistry-errors", "true");
		String str = new String(chemistry);
		IndigoObject handle = indigo.loadMolecule(str);
		ChemistryProperties result = new ChemistryProperties();
		result.Name = handle.name();
		result.MolecularFormula = handle.grossFormula();
		result.MolecularWeight = handle.molecularWeight();
		result.ExactMolecularWeight = handle.monoisotopicMass();
		return result;
	}

	public boolean isMoleculeChiral(byte[] chemistry) throws ChemUtilAccessException {
		Indigo indigo = new Indigo();
		indigo.setOption("ignore-stereochemistry-errors", "true");
		IndigoObject indigoObject = indigo.loadMolecule(chemistry);
		return indigoObject.isChiral();
	}

	public byte[] setChiralFlag(byte[] chemistry, boolean flag) throws ChemUtilAccessException {
//		IndigoObject indigoObject = indigo.loadReaction(chemistry);
		return null;
	}
	
	public byte[] convertToPicture(byte[] chemistry, int format, int height, int width, double loss, double pixelToMM) throws ChemUtilAccessException {
		Indigo indigo = new Indigo();
		IndigoRenderer renderer = new IndigoRenderer(indigo);
		indigo.setOption("render-bond-length", -1d);
		indigo.setOption("render-label-mode", "hetero");
		indigo.setOption("ignore-stereochemistry-errors", "true");	

		IndigoObject indigoObject = indigo.loadQueryMolecule(chemistry);

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

	public MoleculePropertyInfo getMoleculeInfofromMDLString(String mdlMolString) throws ChemUtilAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public MoleculePropertyInfo getMoleculefromChimeString(String chimeString) throws ChemUtilAccessException {
		// TODO Auto-generated method stub
		return null;
	}
}
