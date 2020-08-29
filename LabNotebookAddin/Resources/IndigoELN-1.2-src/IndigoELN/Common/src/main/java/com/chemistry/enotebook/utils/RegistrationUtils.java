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
package com.chemistry.enotebook.utils;

import com.chemistry.enotebook.compoundregistration.classes.CompoundRegistrationJobStatus;
import com.chemistry.enotebook.compoundregistration.classes.CompoundRegistrationStatus;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.purificationservice.PurificationServiceCompoundInfo;
import com.chemistry.enotebook.domain.purificationservice.PurificationServicePlateInfo;
import com.chemistry.enotebook.domain.purificationservice.PurificationServiceTubeInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class RegistrationUtils {
	
	private static Log log = LogFactory.getLog(RegistrationUtils.class);
	
	public List<BatchRegInfoModel> setRegistrationDataToCeNCompoundModel(List<BatchRegInfoModel> regBatchInfoModelList, CompoundRegistrationStatus compoundRegistrationCompStatus[], CompoundRegistrationJobStatus jobStatus) {
		log.info("setRegistrationDataToCeNCompoundModel.enter()");
		
		List<BatchRegInfoModel> result = new ArrayList<BatchRegInfoModel>();
		
		for (BatchRegInfoModel regInfo : regBatchInfoModelList) {
			for (CompoundRegistrationStatus regStatus : compoundRegistrationCompStatus) {
				if (StringUtils.equals(regStatus.getInternalBatchNumber(), regInfo.getInternalBatchNumber())) {					
					if (regStatus.getBatchTrackingNumber() != null && !regStatus.getBatchTrackingNumber().equals("")) {
						regInfo.setBatchTrackingId(Long.parseLong(regStatus.getBatchTrackingNumber()));
					}

					regInfo.setStatus(regStatus.getStatus());
					
					if (regStatus.getStatus().equals(CeNConstants.REGINFO_PASSED)) {
						regInfo.setRegistrationStatus(CeNConstants.REGINFO_REGISTERED);
						regInfo.setRegistrationDate(jobStatus.getStopTime());
						regInfo.setCompoundRegistrationStatus(CeNConstants.REGINFO_SUBMISION_PASS);
					} else {
						if (regStatus.getStatus().equals(CeNConstants.REGINFO_FAILED)) {
							regInfo.setCompoundRegistrationStatus(CeNConstants.REGINFO_SUBMISION_FAIL);
						} else {
							regInfo.setCompoundRegistrationStatus(CeNConstants.REGINFO_NOT_SUBMITTED);
						}
						regInfo.setRegistrationStatus(CeNConstants.REGINFO_NOT_REGISTERED);
					}
					
					regInfo.setParentBatchNumber(regStatus.getGlobalNumber());
					regInfo.setConversationalBatchNumber(regStatus.getBatchNumber());
					regInfo.setCompoundRegistrationStatusMessage(regStatus.getDetailElement());
					regInfo.setErrorMsg(regStatus.getDetailElement());
					
					result.add(regInfo);
				}
			}	
		}
		
		for (BatchRegInfoModel regInfo : regBatchInfoModelList) {
			if (!result.contains(regInfo)) {
				regInfo.setStatus(CeNConstants.REGINFO_FAILED);
				regInfo.setCompoundRegistrationStatus(CeNConstants.REGINFO_SUBMISION_FAIL);
				regInfo.setRegistrationStatus(CeNConstants.REGINFO_NOT_REGISTERED);
				
				result.add(regInfo);
			}
		}
		
		log.info("setRegistrationDataToCeNCompoundModel.exit()");
		
		return result;
		
	}
	
	
	public int[] getOffsetsForCompoundsInAJob(List<BatchRegInfoModel> regBatchInfoModelList)
	{
		int cenModelSize = regBatchInfoModelList.size();
		int[] offsets = new int[cenModelSize];
		for(int i= 0 ; i< cenModelSize ; i ++)
		{
			offsets[i] = regBatchInfoModelList.get(i).getOffset();
		}
		
		return offsets;
	}
	
	
	/**
	 * Converts CEN ProductPlate object to PurificationService Plate model object.
	 * @param plate
	 * @return
	 */
	public static PurificationServicePlateInfo convertToPurificationServicePlateModel(ProductPlate plate)
	{
		if(plate == null || plate.getAllBatchesInThePlate() == null)
		{
			return null;
		}
		ArrayList<PurificationServiceCompoundInfo> compounds = new ArrayList<PurificationServiceCompoundInfo>();
		PurificationServicePlateInfo purificationServiceplate = new PurificationServicePlateInfo();
		purificationServiceplate.setGblPlateBarCode(plate.getPlateBarCode());
		PlateWell<ProductBatchModel> wellmodels[] = plate.getWells();
		int size = wellmodels.length;
		for(int i=0;i<size ; i ++)
		{
			if(wellmodels[i] == null || wellmodels[i].getBatch() == null)
			{
				continue;
			}
			ProductBatchModel pbmodel = wellmodels[i].getBatch();
			BatchRegInfoModel regmodel = pbmodel.getRegInfo();	
			PurificationServiceCompoundInfo compinfo = new PurificationServiceCompoundInfo();
			compinfo.setCompoundSubmissionParam(wellmodels[i].getPurificationServiceParameter());
			compinfo.setBatchNumber(regmodel.getConversationalBatchNumber());
			compinfo.setCompoundNumber(regmodel.getParentBatchNumber());
			compinfo.setBatchTrackingID(regmodel.getBatchTrackingId());
			compinfo.setNotebookBatchNumber(pbmodel.getBatchNumberAsString());
			compinfo.setSaltCode(pbmodel.getSaltForm().getCode());
			compounds.add(compinfo);
		}
		purificationServiceplate.setCompounds(compounds.toArray(new PurificationServiceCompoundInfo[]{}) );
		return purificationServiceplate;
	}
	
	/**
	 * @param plates
	 * @return
	 */
	public static PurificationServicePlateInfo[] convertToPurificationServicePlateInfoArray(ProductPlate[] plates){
		PurificationServicePlateInfo[] plateInfo = new PurificationServicePlateInfo[plates.length];
		for(int i=0; i<plates.length;i++){
			plateInfo[i] = convertToPurificationServicePlateModel(plates[i]);
		}
		return plateInfo;
	}

	public static PurificationServiceTubeInfo[] convertToPurificationServiceTubeInfoArray(List<PlateWell<ProductBatchModel>> tubes){
		List<PurificationServiceTubeInfo> purificationServiceInfo = new ArrayList<PurificationServiceTubeInfo>();
		
		for (PlateWell<ProductBatchModel> well : tubes) {
			if (well != null && well.getBatch() != null && well.getBatch().getRegInfo() != null && well.getBatch().getRegInfo().getSubmitContainerList() != null) {
				ProductBatchModel batch = well.getBatch();
				BatchRegInfoModel regInfo = batch.getRegInfo();
				List<BatchSubmissionContainerInfoModel> submitContainerList = regInfo.getSubmitContainerList(); 
				
				for (BatchSubmissionContainerInfoModel model : submitContainerList) {
					if (model != null) {
						PurificationServiceCompoundInfo info = new PurificationServiceCompoundInfo();
						info.setCompoundSubmissionParam(model.getPurificationServiceParameters());
						info.setBatchNumber(regInfo.getConversationalBatchNumber());
						info.setCompoundNumber(regInfo.getParentBatchNumber());
						info.setBatchTrackingID(regInfo.getBatchTrackingId());
						info.setNotebookBatchNumber(batch.getBatchNumberAsString());
						
						if (batch.getSaltForm() != null) {
							info.setSaltCode(batch.getSaltForm().getCode());
						}
					
						PurificationServiceTubeInfo tube = new PurificationServiceTubeInfo();
						tube.setTubeGUID(model.getTubeGuid());
						tube.setCompound(info);
						
						purificationServiceInfo.add(tube);
					}
				}
			}
		}
		
		return purificationServiceInfo.toArray(new PurificationServiceTubeInfo[0]);
	}
	
	public static String removeSpecialCharaters(String inputStr)
	{
		String input = "";
		if(inputStr == null || inputStr.equals(""))
		{
			return inputStr;
		}
		try
		{
		  input = inputStr.replaceAll("\\n", "");
		  input = inputStr.replaceAll("\\t", "");
		  input = input.trim();
		}catch(Exception e)
		{
			log.error("Failed to remove special characters from input string: " + inputStr, e);
		}
		
		return input;
	}
}
