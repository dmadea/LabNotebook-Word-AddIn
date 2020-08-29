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
package com.virtuan.plateVisualizer;

import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.ParentCompoundModel;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;

public class StructureToolTipPanel extends JPanel {
	
	private static final long serialVersionUID = 5069201547570637723L;
	
	public static final Font LABEL_FONT = new java.awt.Font("sansserif", 1, 11);
	
	public StructureToolTipPanel(BatchModel batchModel, String text) {
		this.add(this.getPanel(batchModel, text));
	}

	/**
	 * Get the batch molecular diagram.
	 * @return
	 */
	private JPanel getPanel(BatchModel batchModel, String text) {
		String id = text;
		ParentCompoundModel compound = batchModel.getCompound();
		JPanel panel = new JPanel(new BorderLayout());
		ChemistryPanel chime = new ChemistryPanel(); //ChimeRenderer.getInstance();
		String stringSketch = "";
		
		if (compound != null) {; // This is a product batch
			//getStringSketchAsString();
			//stringSketch = Decoder.decodeString(stringSketch);
			//Handle all possible struc formats
			if(compound.getMolfile() != null && !compound.getMolfile().equals(""))
			{
				stringSketch = compound.getMolfile();
			}else if ((compound.getMolfile() == null || compound.getMolfile().equals(""))&& compound.getNativeSketch() != null)
			{
				//When a reagent is added from my reagents the Molfile is added as NativeSkectch
				//ReagentsHandler.java line 676 ( String molFile = getStructureByCompoundNo(compound.getRegNumber()); )
				stringSketch = new String(compound.getNativeSketch());
				if(stringSketch.indexOf("END") <= 0)
				{
					//Handle nativeStruc as nativeStruc.Convert native to Molfile for time being.USER2
					try
					{
					ChemistryDelegate chemDel = new ChemistryDelegate();
					byte molConverted[] = chemDel.convertChemistry(compound.getNativeSketch(), "", "MDL Molfile");
					stringSketch = new String(molConverted);
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					
				}
			}else 
			{
			 	
			}
			

		}
			
		if (StringUtils.isNotEmpty(id)) {
			//TODO workaround for CENSTR
			if (id.startsWith(CeNConstants.CENSTR_ID_PREFIX)) {
				id = "";
			}
		}		
		JLabel idLabel = new JLabel(id, JLabel.CENTER);
		idLabel.setFont(LABEL_FONT);
		panel.add(idLabel, BorderLayout.NORTH);
		chime.setMolfileData(stringSketch);
		panel.add(chime,BorderLayout.CENTER);
		if(batchModel.isChloracnegen())
		{
			JLabel idLabe2 = new JLabel("Chloracnegen Type:"+batchModel.getChloracnegenType(), JLabel.CENTER);
			panel.add(idLabe2,BorderLayout.SOUTH);
			panel.setBackground(Color.RED);
		}
		return panel;
	}
	
}
