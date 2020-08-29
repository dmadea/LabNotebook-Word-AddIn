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
package com.chemistry.enotebook.delegate;

import com.chemistry.enotebook.domain.BatchVnVInfoModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.exceptions.VnvException;
import com.chemistry.enotebook.utils.SDFileGeneratorUtil;
import com.chemistry.enotebook.utils.sdf.SdfProcessor;
import com.chemistry.enotebook.vnv.classes.IVnvResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;


public class VnvDelegate {
	private static final Log log = LogFactory.getLog(VnvDelegate.class);

	private String GBL_STEREOISOMER_CODE = "COMPOUND_REGISTRATION_GLOBAL_STEREOISOMER_CODE";
	private SDFileGeneratorUtil sdfUtil = null;
	private RegistrationManagerDelegate registrationManagerDelegate;
	
	public VnvDelegate() throws VnvException{
		init();
	}
	
	public void init() throws VnvException{
		try{
			registrationManagerDelegate = new RegistrationManagerDelegate();
			sdfUtil = new SDFileGeneratorUtil();
		} catch(Exception vnverror){
			log.debug("CompoundRegistration Initialization Error:"+vnverror);
			vnverror.printStackTrace();
			throw new VnvException(vnverror.getMessage());
		}
	}
	
	public BatchVnVInfoModel performVnV(ProductBatchModel pBatch,String compoundManagementEmployeeID, NotebookPageModel pageModel) throws VnvException{
		String sduString = null;
		String molStructure = null;
		String inputSic = null;
		BatchVnVInfoModel batchVnvInfo = null;
		try
		{	
			batchVnvInfo = pBatch.getRegInfo().getBatchVnVInfo();//new BatchVnVInfoModel();
			sduString = sdfUtil.buildSDUnitForABatch(pBatch, compoundManagementEmployeeID, pageModel, false, true);
			molStructure =SdfProcessor.getMolStructure(sduString);
			inputSic = SdfProcessor.getOriFieldValue(sduString, GBL_STEREOISOMER_CODE);
			
			IVnvResult vnvr;
			if (inputSic.equals("HSREG")) {	
				vnvr = registrationManagerDelegate.validateStructureAssignStereoIsomerCode(molStructure);
			}
			else {
				vnvr = registrationManagerDelegate.validateStructureWithStereoIsomerCode(molStructure, inputSic);
			}
						
			String isPassed = vnvr.isPassed()?"Pass":"Fail";
			batchVnvInfo.setStatus(isPassed);
			if(vnvr.getVnvErrors()!= null && vnvr.getVnvErrors().length > 0){
				StringBuffer errorBuf =  new StringBuffer();
				String[] types = vnvr.getVnvErrors();
				for(int i=0; i<types.length;i++){
					errorBuf.append(types[i]+"\n");
				}
				batchVnvInfo.setErrorMsg(errorBuf.toString());
			}
			else
				batchVnvInfo.setErrorMsg("");
			
			String[] validSics = vnvr.getValidCodes();
			ArrayList<String> siclist = new ArrayList<String>();
			if(validSics != null) {
				for(int i=0; i<validSics.length;i++){
					siclist.add(validSics[i]);
				}
			}
			batchVnvInfo.setSuggestedSICList(siclist);
			batchVnvInfo.setAssignedStereoIsomerCode(vnvr.getAssignedCode());
			//System.out.println(pBatch.getBatchNumber().getBatchNumber()+":  VnV status :"+isPassed+"  SICode:"+result_sic);
			//Setting the mol data only incase of VnV Failure
			batchVnvInfo.setMolData(vnvr.getResultMol());
			//System.out.println(vnvr.getResultMol());
			batchVnvInfo.setMolWeight(vnvr.getMolWeight());
			batchVnvInfo.setMolFormula(vnvr.getMolFormula());
			batchVnvInfo.setModified(vnvr.isMolWasModified());
						
		}catch (Exception error){
			error.printStackTrace();
			throw new VnvException(error.getMessage());
		}
		
		return batchVnvInfo;
	}
	
	public BatchVnVInfoModel performVnV(String molStructure,String inputSic) throws VnvException{
		
		BatchVnVInfoModel batchVnvInfo = null;
		try
		{	batchVnvInfo = new BatchVnVInfoModel();
			IVnvResult vnvr;
			if (inputSic.equals("HSREG")) {	
				vnvr = registrationManagerDelegate.validateStructureAssignStereoIsomerCode(molStructure);
			}
			else {
				vnvr = registrationManagerDelegate.validateStructureWithStereoIsomerCode(molStructure, inputSic);
			}
						
			String isPassed = vnvr.isPassed()?"Pass":"Fail";
			batchVnvInfo.setStatus(isPassed);
			if(vnvr.getVnvErrors()!= null && vnvr.getVnvErrors().length > 0){
				StringBuffer errorBuf =  new StringBuffer();
				String[] types = vnvr.getVnvErrors();
				for(int i=0; i<types.length;i++){
					errorBuf.append(types[i]+"\n");
				}
				batchVnvInfo.setErrorMsg(errorBuf.toString());
			}
			else
				batchVnvInfo.setErrorMsg("");
			
			String[] validSics = vnvr.getValidCodes();
			ArrayList<String> siclist = new ArrayList<String>();
			if(validSics != null) {
				for(int i=0; i<validSics.length;i++){
					siclist.add(validSics[i]);
				}
			}
			batchVnvInfo.setSuggestedSICList(siclist);
			batchVnvInfo.setAssignedStereoIsomerCode(vnvr.getAssignedCode());
			//System.out.println(pBatch.getBatchNumber().getBatchNumber()+":  VnV status :"+isPassed+"  SICode:"+result_sic);
			//Setting the mol data only incase of VnV Failure
			batchVnvInfo.setMolData(vnvr.getResultMol());
			//System.out.println(vnvr.getResultMol());
			
			batchVnvInfo.setMolFormula(vnvr.getMolFormula());
			batchVnvInfo.setMolWeight(vnvr.getMolWeight());
			
		}catch (Exception error){
			error.printStackTrace();
			throw new VnvException(error.getMessage());
		}
		
		return batchVnvInfo;
	}
	
public void performVnV(String molStructure,String inputSic,BatchVnVInfoModel batchVnvInfo) throws VnvException{
		
		try
		{	batchVnvInfo = new BatchVnVInfoModel();
			IVnvResult vnvr;
			if (inputSic.equals("HSREG")) {	
				vnvr = registrationManagerDelegate.validateStructureAssignStereoIsomerCode(molStructure);
			}
			else {
				vnvr = registrationManagerDelegate.validateStructureWithStereoIsomerCode(molStructure, inputSic);
			}
						
			String isPassed = vnvr.isPassed()?"Pass":"Fail";
			batchVnvInfo.setStatus(isPassed);
			if(vnvr.getVnvErrors()!= null && vnvr.getVnvErrors().length > 0){
				StringBuffer errorBuf =  new StringBuffer();
				String[] types = vnvr.getVnvErrors();
				for(int i=0; i<types.length;i++){
					errorBuf.append(types[i]+"\n");
				}
				batchVnvInfo.setErrorMsg(errorBuf.toString());
			}
			else
				batchVnvInfo.setErrorMsg("");
			
			String[] validSics = vnvr.getValidCodes();
			ArrayList<String> siclist = new ArrayList<String>();
			if(validSics != null) {
				for(int i=0; i<validSics.length;i++){
					siclist.add(validSics[i]);
				}
			}
			batchVnvInfo.setSuggestedSICList(siclist);
			batchVnvInfo.setAssignedStereoIsomerCode(vnvr.getAssignedCode());
			//System.out.println(pBatch.getBatchNumber().getBatchNumber()+":  VnV status :"+isPassed+"  SICode:"+result_sic);
			//Setting the mol data only incase of VnV Failure
			batchVnvInfo.setMolData(vnvr.getResultMol());
			//System.out.println(vnvr.getResultMol());
			
			batchVnvInfo.setMolFormula(vnvr.getMolFormula());
			batchVnvInfo.setMolWeight(vnvr.getMolWeight());
						
		}catch (Exception error){
			error.printStackTrace();
			throw new VnvException(error.getMessage());
		}
		
		
	}
}
