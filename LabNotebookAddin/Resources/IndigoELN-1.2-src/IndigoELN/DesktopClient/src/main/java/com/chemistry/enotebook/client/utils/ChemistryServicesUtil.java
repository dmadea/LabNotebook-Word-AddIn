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
package com.chemistry.enotebook.client.utils;

import com.chemistry.enotebook.domain.BatchesList;
import com.chemistry.enotebook.domain.MonomerBatchModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ReactionStepModel;
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ChemistryProperties;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.viewer.ChemistryViewer;

import java.util.List;

public class ChemistryServicesUtil {
	
	static ChemistryDelegate chemObj = null;
	static
	{
    	chemObj = new ChemistryDelegate();
//			This viewer loads all the nessesary dlls to jvm
		ChemistryViewer viewer = new ChemistryViewer("","");
    }
	
	
	public ChemistryProperties getMolecularInformation(String molFile)
    {
		ChemistryProperties prop = null;
    	try	{
            if(molFile != null)
            {
	    	prop = chemObj.getMolecularInformation(molFile.getBytes());
            }
	        System.out.print(prop.MolecularFormula);
        
		} catch (ChemUtilAccessException e)	{
			e.printStackTrace();
		}
		return prop;
    }
	
	
	public NotebookPageModel calcMolecularPropsForAllBatches(NotebookPageModel pageModel)
	{
		if(pageModel == null || pageModel.getReactionSteps() == null || pageModel.getReactionSteps().size() == 0)
		{
		 return null;	
		}
		int stepSize =  pageModel.getReactionSteps().size();
		//for each step do the calc
		for(int stepNo= 0; stepNo<stepSize ; stepNo++)
		{
			ReactionStepModel stepModel = pageModel.getReactionStep(stepNo);
			//Calc Props for monomers
			List monBatchesList = stepModel.getMonomers();
			if(monBatchesList != null && monBatchesList.size() > 0)
			{
			 int batchesListSize = 	monBatchesList.size();
			 for(int i =0 ; i <batchesListSize ; i++)
			 {
				BatchesList monBList = (BatchesList)monBatchesList.get(i);
				List monomers = monBList.getBatchModels();
				if(monomers != null && monomers.size() > 0)
				{
				  int monSize = monomers.size();
				  for(int k= 0 ; k <monSize ; k ++)
				  {
					 
					  MonomerBatchModel model = (MonomerBatchModel)monomers.get(k);
					  
					  
					  
				  }
				}
			 }
			 
			}
            //Calc Props for products
			List prodBatchesList = stepModel.getProducts();
			
		}//for each step
		
		return pageModel;
	}

}
