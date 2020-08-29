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

import com.chemistry.enotebook.domain.batch.BatchResidualSolventModel;
import com.chemistry.enotebook.domain.batch.BatchSolubilitySolventModel;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BatchRegInfoModel extends CeNAbstractModel {

	private static final long serialVersionUID = -7307976701854364091L;

	private String batchKey = "";//BatchKey of ProductBatch
	private String internalBatchNumber = "";
	private String compoundSource = "";
	private String compoundSourceDetail = "";
	private Timestamp registrationDate; // Should initially be null
	private String registrationStatus ="";
	private String submissionStatus = "";
	private String status = "";
	private String jobId = "-1";
	private long batchTrackingId = CeNConstants.INVALID_NUMBER_LONG;
	private String errorMsg = "";
	private String hitId = "";
	private ArrayList<BatchResidualSolventModel> residualSolventList = new ArrayList<BatchResidualSolventModel>();
	private ArrayList<BatchSolubilitySolventModel> solubilitySolventList = new ArrayList<BatchSolubilitySolventModel>();

	private List<BatchSubmissionToScreenModel> submitToBiologistTestList = new ArrayList<BatchSubmissionToScreenModel>();
	private ArrayList<BatchSubmissionContainerInfoModel> submitContainerList = new ArrayList<BatchSubmissionContainerInfoModel>();
	
	private BatchVnVInfoModel batchVnVInfo = new BatchVnVInfoModel();

	// The following codes must be of GenericCode type
	private ArrayList<String> compoundRegistrationHazardCodes = new ArrayList<String>(); // List of two digit codes that correspond to a COMPOUND_REGISTRATION_HAZARD_CDT table
	private ArrayList<String> compoundRegistrationHandlingCodes = new ArrayList<String>(); // List of two digit codes that correspond to a COMPOUND_REGISTRATION_HANDLING_CDT table
	private ArrayList<String> compoundRegistrationStorageCodes = new ArrayList<String>(); // List of two digit codes that correspond to a COMPOUND_REGISTRATION_STORAGE_CDT table
	private String storageComments = ""; // Batch Storage Comments
	private String hazardComments = ""; // Batch Hazard Comments
	private String handlingComments = "";// Batch handling comments in case there is a special need
	
	private String intermediate_or_test="U";

	//status CompoundAggregationSubmission
	private String compoundAggregationStatus = CeNConstants.REGINFO_NOT_SUBMITTED;
	private String compoundAggregationStatusMessage = CeNConstants.REGINFO_NOT_SUBMITTED;
	private String compoundRegistrationStatus = CeNConstants.REGINFO_NOT_SUBMITTED;
	private String compoundRegistrationStatusMessage = CeNConstants.REGINFO_NOT_SUBMITTED;
	private String purificationServiceStatus = CeNConstants.REGINFO_NOT_SUBMITTED;
	private String purificationServiceStatusMessage = CeNConstants.REGINFO_NOT_SUBMITTED;
	private String compoundManagementStatus = CeNConstants.REGINFO_NOT_SUBMITTED;
	private String compoundManagementStatusMessage = CeNConstants.REGINFO_NOT_SUBMITTED;
	private int offset = -1;
	
	//PF-03456796
	private String parentBatchNumber="";
	//PF-03456796-44-0002
	private String conversationalBatchNumber = "";
	private TemperatureRangeModel meltPointRange = new TemperatureRangeModel(); // Used for BatchInfo
	private String compoundState = ""; // String will be provided from dropdown list
	private String protectionCode = ""; // Protection Code used to determine orderability
	private String regStatus = ""; // Registration Status of this batch
	private ExternalSupplierModel vendorInfo = new ExternalSupplierModel();
	private boolean productFlag = true; // Use to indicate if this was a
	// targeted product (was what user wants
	// to identify as a
	// final product)
	private String owner = ""; // NTID of person who sponsored the creation of this batch
	private String comments = ""; // Batch Comments

	public BatchRegInfoModel(String key) {
		this.key = key + CeNConstants.REG_BATCH_KEY_SUFFIX;
		this.setBatchKey(key);
	}

	public BatchRegInfoModel(String key, boolean isloadedfromdb) {
		this.key = key;
	}
	
	/**
	 * Does not catch if the assigned stereoisomer code is invalid.  Just whether or not we have a 
	 * status back from a VnV run.  
	 * 
	 * @return true if batchVnVInfo != null && batchVnVInfo.getStatus == BatchVnVInfoModel.VNV_PASS
	 */
	public boolean isVnVValid() {
		return batchVnVInfo != null && StringUtils.equalsIgnoreCase(BatchVnVInfoModel.VNV_PASS, batchVnVInfo.getStatus());
	}
	/**
	 * @return the batchTrackingId
	 */
	public long getBatchTrackingId() {
		return batchTrackingId;
	}

	/**
	 * @return the batchVnVInfo
	 */
	public BatchVnVInfoModel getBatchVnVInfo() {
		return batchVnVInfo;
	}

	/**
	 * @param batchVnVInfo
	 *            the batchVnVInfo to set
	 */
	public void setBatchVnVInfo(BatchVnVInfoModel batchVnVInfo) {
		this.batchVnVInfo = batchVnVInfo;
		setModified(true);
	}

	/**
	 * @return the compoundSource
	 */
	public String getCompoundSource() {
		return compoundSource;
	}

	/**
	 * @param compoundSource
	 *            the compoundSource to set
	 */
	public void setCompoundSource(String compoundSource) {
		this.compoundSource = compoundSource;
		setModified(true);
	}

	/**
	 * @return the compoundSourceDetail
	 */
	public String getCompoundSourceDetail() {
		return compoundSourceDetail;
	}

	/**
	 * @param compoundSourceDetail
	 *            the compoundSourceDetail to set
	 */
	public void setCompoundSourceDetail(String compoundSourceDetail) {
		this.compoundSourceDetail = compoundSourceDetail;
		setModified(true);
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg
	 *            the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
		setModified(true);
	}

	/**
	 * @return the compoundRegistrationHandlingCodes
	 */
	public ArrayList<String> getCompoundRegistrationHandlingCodes() {
		return compoundRegistrationHandlingCodes;
	}

	/**
	 * @param compoundRegistrationHandlingCodes
	 *            the compoundRegistrationHandlingCodes to set
	 */
	public void setCompoundRegistrationHandlingCodes(ArrayList<String> compoundRegistrationHandlingCodes) {
		this.compoundRegistrationHandlingCodes = compoundRegistrationHandlingCodes;
		setModified(true);
	}

	/**
	 * @return the compoundRegistrationHazardCodes
	 */
	public ArrayList<String> getCompoundRegistrationHazardCodes() {
		return compoundRegistrationHazardCodes;
	}

	/**
	 * @param compoundRegistrationHazardCodes
	 *            the compoundRegistrationHazardCodes to set
	 */
	public void setCompoundRegistrationHazardCodes(ArrayList<String> compoundRegistrationHazardCodes) {
		this.compoundRegistrationHazardCodes = compoundRegistrationHazardCodes;
		setModified(true);
	}

	/**
	 * @return the compoundRegistrationStorageCodes
	 */
	public ArrayList<String> getCompoundRegistrationStorageCodes() {
		return compoundRegistrationStorageCodes;
	}

	/**
	 * @param compoundRegistrationStorageCodes
	 *            the compoundRegistrationStorageCodes to set
	 */
	public void setCompoundRegistrationStorageCodes(ArrayList<String> compoundRegistrationStorageCodes) {
		this.compoundRegistrationStorageCodes = compoundRegistrationStorageCodes;
		setModified(true);
	}

	/**
	 * @return the hitId
	 */
	public String getHitId() {
		return hitId;
	}

	/**
	 * @param hitId
	 *            the hitId to set
	 */
	public void setHitId(String hitId) {
		this.hitId = hitId;
		setModified(true);
	}

	/**
	 * @return the jobId
	 */
	public String getJobId() {
		return jobId;
	}

	/**
	 * @param jobId
	 *            the jobId to set
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
		setModified(true);
	}

	/**
	 * @return the registrationDate
	 */
	public Timestamp getRegistrationDate() {
		return registrationDate;
	}

	/**
	 * @param registrationDate
	 *            the registrationDate to set
	 */
	public void setRegistrationDate(Timestamp registrationDate) {
		this.registrationDate = registrationDate;
		setModified(true);
	}

	/**
	 * @return the registrationStatus
	 */
	public String getRegistrationStatus() {
		return registrationStatus;
	}

	/**
	 * @param registrationStatus
	 *            the registrationStatus to set
	 */
	public void setRegistrationStatus(String registrationStatus) {
		this.registrationStatus = registrationStatus;
		setModified(true);
	}

	/**
	 * @return the residualSolventList
	 */
	public ArrayList<BatchResidualSolventModel> getResidualSolventList() {
		return residualSolventList;
	}
	/**
	 * @param container
	 *            The MM Container to add to the list.
	 */
	public void addResidualSolvent(BatchResidualSolventModel residualSolvent) {
		residualSolventList.add(residualSolvent);
		//addObserver(residualSolvent);
		setModified(true);
	}

	/**
	 * @param residualSolventList
	 *            the residualSolventList to set
	 */
	public void setResidualSolventList(ArrayList<BatchResidualSolventModel> residualSolventList) {
		this.residualSolventList = residualSolventList;
		setModified(true);
	}

	/**
	 * @return the solubilitySolventList
	 */
	public ArrayList<BatchSolubilitySolventModel> getSolubilitySolventList() {
		return solubilitySolventList;
	}


	/**
	 * @param container
	 *            The MM Container to add to the list.
	 */
	public void addSolubilitySolvent(BatchSolubilitySolventModel solubilitySolvent) {
		solubilitySolventList.add(solubilitySolvent);
		setModified(true);
	}
	
	/**
	 * @param solubilitySolventList
	 *            the solubilitySolventList to set
	 */
	public void setSolubilitySolventList(ArrayList<BatchSolubilitySolventModel> solubilitySolventList) {
		this.solubilitySolventList = solubilitySolventList;
		setModified(true);
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
		setModified(true);
	}

	/**
	 * @return the submissionStatus
	 */
	public String getSubmissionStatus() {
		return submissionStatus;
	}

	/**
	 * @param submissionStatus
	 *            the submissionStatus to set
	 */
	public void setSubmissionStatus(String submissionStatus) {
		this.submissionStatus = submissionStatus;
		setModified(true);
	}

	/**
	 * @return the submitContainerList
	 */
	public ArrayList<BatchSubmissionContainerInfoModel> getSubmitContainerList() {
        synchronized (submitContainerList) {
            return (ArrayList<BatchSubmissionContainerInfoModel>)submitContainerList.clone();
        }
    }

    public int getSubmitContainerListSize() {
        synchronized (submitContainerList) {
            return submitContainerList.size();
        }
    }

    public BatchSubmissionContainerInfoModel getSubmitContainerListRow(int row) {
        synchronized (submitContainerList) {
            return submitContainerList.get(row);
        }
    }

    public void clearEmptyElementsFromSubmitContainerList() {
        synchronized (submitContainerList) {
            for (Iterator<BatchSubmissionContainerInfoModel> iter = submitContainerList.iterator(); iter.hasNext();) {
                BatchSubmissionContainerInfoModel bsciModel = iter.next();
                if (StringUtils.isBlank(bsciModel.getBarCode()) && StringUtils.isBlank(bsciModel.getContainerType()) &&
                        StringUtils.isBlank(bsciModel.getSolvent()) && bsciModel.getAmountValue() == 0 &&
                        bsciModel.getMoleValue() == 0 && bsciModel.getVolumeValue() == 0) {
                    iter.remove();
                }
            }
        }
    }

	/**
	 * @param container
	 *            The MM Container to add to the list.
	 */
	public void addSubmitContainer(BatchSubmissionContainerInfoModel container) {
        synchronized (submitContainerList) {
		    submitContainerList.add(container);
        }
		//addObserver(container);
		setModified(true);
	}	
	
	/**
	 * @param submitContainerList
	 *            the submitContainerList to set
	 */
	public void setSubmitContainerList(ArrayList<BatchSubmissionContainerInfoModel> submitContainerList) {
		this.submitContainerList = submitContainerList;
		setModified(true);
	}

	/**
	 * @param test
	 *            The Biologist test to add to the list.
	 */
	public void removeSubmitContainer(int index) {
        synchronized (submitContainerList) {
            if (index >= 0 && index < submitContainerList.size()) {
                submitContainerList.remove(index);
            }
        }
		setModified(true);
	}

    public void clearSubmitContainerListRow(int index) {
        synchronized (submitContainerList) {
            if (index >= 0 && index < submitContainerList.size()) {
                submitContainerList.remove(index);
                submitContainerList.add(index, new BatchSubmissionContainerInfoModel());
            }
        }
        setModified(true);
    }

	public List<BatchSubmissionToScreenModel> getOldSubmitToBiologistTestList() {
        return getSubmitToBiologistTestList(false);
	}

    public List<BatchSubmissionToScreenModel> getNewSubmitToBiologistTestList() {
        return getSubmitToBiologistTestList(true);
    }

    private List<BatchSubmissionToScreenModel> getSubmitToBiologistTestList(boolean isNew) {
        List<BatchSubmissionToScreenModel> ans = new ArrayList<BatchSubmissionToScreenModel>();
        if (submitToBiologistTestList != null) {
            for (BatchSubmissionToScreenModel submission : submitToBiologistTestList) {
                if (submission.isNew() == isNew) {
                    ans.add(submission);
                }
            }
        }
        return ans;
    }

	public void setSubmitToBiologistTestList(ArrayList<BatchSubmissionToScreenModel> submitToBiologistTestList) {
        List<BatchSubmissionToScreenModel> newList = getOldSubmitToBiologistTestList();
        if (submitToBiologistTestList != null) {
            for (BatchSubmissionToScreenModel submission : submitToBiologistTestList) {
            	//TODO
                //if (submission.isNew()) {
                    newList.add(submission);
                //}
            }
        }
		this.submitToBiologistTestList = newList;
		setModified(true);
	}

	/**
	 * @param test
	 *            The Biologist test to add to the list.
	 */
	public void removeSubmitToBiologistTest(int index) {
		if (index >= 0 && index < submitToBiologistTestList.size()) {
			//ObservableObject o = (ObservableObject) submitToBiologistTestList.get(index);
			//deleteObserver(o);
			submitToBiologistTestList.remove(index);
			setModified(true);
		}
	}
	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append(CeNConstants.XML_VERSION_TAG);
		xmlbuff.append("<Registered_Batch_Properties>");
		xmlbuff.append("<Meta_Data>");
		xmlbuff.append("<Reg_Info>");
	
		xmlbuff.append("<Error_Msg>");
		if(StringUtils.isNotBlank(this.getErrorMsg()))
			xmlbuff.append(this.getErrorMsg());
		xmlbuff.append("</Error_Msg>");
				  
		xmlbuff.append("<Residual_Solvent_List>");
		if(CommonUtils.isNotNull(this.getResidualSolventList()))
			xmlbuff.append(getBatchResidualSolventXML());
		xmlbuff.append("</Residual_Solvent_List>");
		
		xmlbuff.append("<Solubility_Solvent_List>");
		if(CommonUtils.isNotNull(this.getSolubilitySolventList()))
			xmlbuff.append(getBatchSolubilitySolventXML());
		xmlbuff.append("</Solubility_Solvent_List>");
		
		xmlbuff.append("<Submit_To_Biologist_Test_List>");
		if(CommonUtils.isNotNull(submitToBiologistTestList)) {
			xmlbuff.append(getScreenSubmissionXML());
		}
		xmlbuff.append("</Submit_To_Biologist_Test_List>");
		
		xmlbuff.append("<Submit_Container_List>");
		if(CommonUtils.isNotNull(this.getSubmitContainerList()))
			xmlbuff.append(this.getContainerSubmissionXML());
		xmlbuff.append("</Submit_Container_List>");
		
		xmlbuff.append("<Batch_VnV_Info>");
		xmlbuff.append("<Status>");
		if(StringUtils.isNotBlank(this.getBatchVnVInfo().getStatus()))
			xmlbuff.append(this.getBatchVnVInfo().getStatus());
		xmlbuff.append("</Status>");
		
		xmlbuff.append("<Mol_Data>");
		if(CommonUtils.isNotNull(this.getBatchVnVInfo().getMolData()))
			xmlbuff.append(this.getBatchVnVInfo().getMolData());
		xmlbuff.append("</Mol_Data>");
		
		xmlbuff.append("<Suggested_SICList>");
		if(CommonUtils.isNotNull(this.getBatchVnVInfo().getSuggestedSICList()))
			xmlbuff.append(this.getBatchVnVInfo().getSuggestedSICList());
		xmlbuff.append("</Suggested_SICList>");
		
		xmlbuff.append("<Assigned_Stereo_Isomer_Code>");
			xmlbuff.append(this.getBatchVnVInfo().getAssignedStereoIsomerCode());
		xmlbuff.append("</Assigned_Stereo_Isomer_Code>");
		
		xmlbuff.append("<Error_Msg>");
		if(StringUtils.isNotBlank(this.getBatchVnVInfo().getErrorMsg()))
			xmlbuff.append(this.getBatchVnVInfo().getErrorMsg());
		xmlbuff.append("</Error_Msg>");
		
		xmlbuff.append("</Batch_VnV_Info>");
		
		xmlbuff.append("<CompoundRegistration_Hazard_Codes>");
		if(CommonUtils.isNotNull(this.getCompoundRegistrationHazardCodes()))
			xmlbuff.append(this.getHazardCodesXML());
		xmlbuff.append("</CompoundRegistration_Hazard_Codes>");
		
		xmlbuff.append("<CompoundRegistration_Handling_Codes>");
		if(CommonUtils.isNotNull(this.getCompoundRegistrationHandlingCodes()))
			xmlbuff.append(this.getHandlingCodesXML());
		xmlbuff.append("</CompoundRegistration_Handling_Codes>");
		
		xmlbuff.append("<CompoundRegistration_Storage_Codes>");
		if(CommonUtils.isNotNull(this.getCompoundRegistrationStorageCodes()))
			xmlbuff.append(this.getStorageCodesXML());
		xmlbuff.append("</CompoundRegistration_Storage_Codes>");
		
		xmlbuff.append("</Reg_Info>");
		xmlbuff.append("</Meta_Data>");
		xmlbuff.append("</Registered_Batch_Properties>");

		return xmlbuff.toString();
	}


	/**
	 * @param batchTrackingId the batchTrackingId to set
	 */
	public void setBatchTrackingId(long batchTrackingId) {
		this.batchTrackingId = batchTrackingId;
		setModified(true);
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
		setModified(true);
	}

	/**
	 * @return the compoundAggregationStatus
	 */
	public String getCompoundAggregationStatus() {
		if(compoundAggregationStatus == null )
			return CeNConstants.REGINFO_NOT_SUBMITTED;
		return compoundAggregationStatus;
	}
	/**
	 * @param compoundAggregationStatus the compoundAggregationStatus to set
	 */
	public void setCompoundAggregationStatus(String compoundAggregationStatus) {
		this.compoundAggregationStatus = compoundAggregationStatus;
		setModified(true);
	}
	/**
	 * @param compoundAggregationStatus the compoundAggregationStatus to set
	 */
	public void setCompoundAggregationStatusMessage(String compoundAggregationStatusMessage) {
		this.compoundAggregationStatusMessage = compoundAggregationStatusMessage;
		setModified(true);
	}

	/**
	 * @return the compoundAggregationStatus
	 */
	public String getCompoundAggregationStatusMessage() {
		if(compoundAggregationStatusMessage == null)
			return "";
		return compoundAggregationStatusMessage;
	}
	
	/**
	 * 
	 * @return true if compoundAggregationStatus == CeNConstants.REGINFO_SUBMITTED
	 */
	public boolean isCompoundAggregationSubmitted() {
		return StringUtils.contains(compoundAggregationStatus, CeNConstants.REGINFO_SUBMITTED);
	}
	
	/**
	 * 
	 * @return true if StringUtils.equals(compoundAggregationStatus, CeNConstants.REGINFO_SUBMISION_PENDING)
	 */
	public boolean isCompoundAggregationSubmitPending() {
		return StringUtils.equals(compoundAggregationStatus, CeNConstants.REGINFO_SUBMISION_PENDING);
	}
	/**
	 * 
	 * @return true if compoundAggregationStatus == CeNConstants.REGINFO_SUBMISION_PASS
	 */
	public boolean isCompoundAggregationSubmittedSuccessfully() {
		return StringUtils.contains(compoundAggregationStatus, CeNConstants.REGINFO_SUBMISION_PASS);
	}

	/**
	 * @return the compoundRegistrationStatus
	 */
	public String getCompoundRegistrationStatus() {
		if(compoundRegistrationStatus == null)
			return "";
		if(StringUtils.isNotBlank(this.submissionStatus))
			return this.status != null ? this.submissionStatus + " - " + this.status : this.submissionStatus;
		else 
			return CeNConstants.REGINFO_NOT_SUBMITTED;
	}

	/**
	 * @param compoundRegistrationStatus the compoundRegistrationStatus to set
	 */
	public void setCompoundRegistrationStatus(String compoundRegistrationStatus) {
		this.compoundRegistrationStatus = compoundRegistrationStatus;
		setModified(true);
	}
	
	/**
	 * @return the compoundRegistrationStatusMessage
	 */
	public String getCompoundRegistrationStatusMessage() {
		if(compoundRegistrationStatusMessage == null)
			return "";
		return compoundRegistrationStatusMessage;
	}

	/**
	 * @param compoundRegistrationStatusMessage the compoundRegistrationStatusMessage to set
	 */
	public void setCompoundRegistrationStatusMessage(String compoundRegistrationStatusMessage) {
		this.compoundRegistrationStatusMessage = compoundRegistrationStatusMessage;
		setModified(true);
	}

	/**
	 * 
	 * @return true if compoundRegistrationStatus == CeNConstants.REGINFO_SUBMITTED
	 */
	public boolean isCompoundRegistrationSubmitted() {
		return StringUtils.contains(compoundRegistrationStatus, CeNConstants.REGINFO_SUBMITTED);
	}
	
	/**
	 * 
	 * @return true if StringUtils.equals(regInfo.getCompoundRegistrationStatus(), CeNConstants.REGINFO_SUBMISION_PENDING)
	 */
	public boolean isCompoundRegistrationSubmitPending() {
		return StringUtils.equals(compoundRegistrationStatus, CeNConstants.REGINFO_SUBMISION_PENDING);
	}
	/**
	 * 
	 * @return true if compoundRegistrationStatus == CeNConstants.REGINFO_SUBMISION_PASS
	 */
	public boolean isCompoundRegistrationSubmittedSuccessfully() {
		return StringUtils.contains(compoundRegistrationStatus, CeNConstants.REGINFO_SUBMISION_PASS);
	}


	/**
	 * @return the purificationServiceStatus
	 */
	public String getPurificationServiceStatus() {
		if(purificationServiceStatus == null)
			return CeNConstants.REGINFO_NOT_SUBMITTED;
		return purificationServiceStatus;
	}

	/**
	 * @param purificationServiceStatus the purificationServiceStatus to set
	 */
	public void setPurificationServiceStatus(String purificationServiceStatus) {
		this.purificationServiceStatus = purificationServiceStatus;
		setModified(true);
	}


	/**
	 * @return the purificationServiceStatusMessage
	 */
	public String getPurificationServiceStatusMessage() {
		if(purificationServiceStatusMessage == null)
			return "";
		return purificationServiceStatusMessage;
	}

	/**
	 * @param purificationServiceStatusMessage the purificationServiceStatusMessage to set
	 */
	public void setPurificationServiceStatusMessage(String purificationServiceStatusMessage) {
		this.purificationServiceStatusMessage = purificationServiceStatusMessage;
		setModified(true);
	}

	/**
	 * 
	 * @return true if purificationSubmissionStatus == CeNConstants.REGINFO_SUBMITTED
	 */
	public boolean isPurificationServiceSubmitted() {
		return StringUtils.contains(purificationServiceStatus, CeNConstants.REGINFO_SUBMITTED);
	}
	/**
	 * 
	 * @return true if purificationSubmissionStatus == CeNConstants.REGINFO_SUBMISION_PASS
	 */
	public boolean isPurificationServiceSubmittedSuccessfully() {
		return StringUtils.contains(purificationServiceStatus, CeNConstants.REGINFO_SUBMISION_PASS);
	}

	/**
	 * @return the compoundManagementStatus
	 */
	public String getCompoundManagementStatus() {
		if(compoundManagementStatus == null)
			return CeNConstants.REGINFO_NOT_SUBMITTED;
		return compoundManagementStatus;
	}

	/**
	 * @param compoundManagementStatus the compoundManagementStatus to set
	 */
	public void setCompoundManagementStatus(String compoundManagementStatus) {
		this.compoundManagementStatus = compoundManagementStatus;
		setModified(true);
	}

	/**
	 * @return the compoundManagementStatusMessage
	 */
	public String getCompoundManagementStatusMessage() {
		if(compoundManagementStatusMessage == null)
			return "";
		return compoundManagementStatusMessage;
	}

	/**
	 * @param compoundManagementStatusMessage the compoundManagementStatusMessage to set
	 */
	public void setCompoundManagementStatusMessage(String compoundManagementStatusMessage) {
		this.compoundManagementStatusMessage = compoundManagementStatusMessage;
		setModified(true);
	}
	
	/**
	 * 
	 * @return true if compoundManagementStatus == CeNConstants.REGINFO_NOT_SUBMITTED of it is blank
	 */
	public boolean isCompoundManagementNotSubmitted() {
		return StringUtils.isBlank(compoundManagementStatus) || StringUtils.contains(compoundManagementStatus, CeNConstants.REGINFO_NOT_SUBMITTED);
	}
	
	/**
	 * 
	 * @return true if compoundManagementStatus == CeNConstants.REGINFO_SUBMITTED
	 */
	public boolean isCompoundManagementSubmitted() {
		return StringUtils.contains(compoundManagementStatus, CeNConstants.REGINFO_SUBMITTED);
	}
	
	/**
	 * Return true if we have registered this tube with CompoundManagement - does this even make sense?  CompoundManagement is containers.
	 * @return true if compoundManagementStatus == CeNConstants.REGINFO_SUBMISION_PASS
	 */
	public boolean isCompoundManagementSubmittedSuccessfully() {
		return StringUtils.contains(compoundManagementStatus, CeNConstants.REGINFO_SUBMISION_PASS);
	}

	public String getConversationalBatchNumber() {
		return conversationalBatchNumber;
	}

	public void setConversationalBatchNumber(String conversationalBatchNumber) {
		{
			this.conversationalBatchNumber = conversationalBatchNumber;
			setModified(true);
		}
	}

	public String getParentBatchNumber() {
		return parentBatchNumber;
	}

	public void setParentBatchNumber(String parentBatchNumber) {
		this.parentBatchNumber = parentBatchNumber;
		setModified(true);
	}

	/**
	 * @return the batchKey
	 */
	public String getBatchKey() {
		return batchKey;
	}

	/**
	 * @param batchKey the batchKey to set
	 */
	public void setBatchKey(String batchKey) {
		this.batchKey = batchKey;
		setModified(true);
	}

	/** 
	 * Information here indicates that the batch is in a registered state or 
	 * is in the process of being registerred.
	 * 
	 * @return false if registerred or in process.
	 */
	public boolean allowBatchEdits() {
		if (getRegistrationStatus() != null && getStatus() != null) {
			if (StringUtils.equals(getRegistrationStatus(), CeNConstants.REGINFO_NOT_REGISTERED)) {
				return true;
			}
			if (StringUtils.equals(getStatus(), CeNConstants.REGINFO_SUBMISION_PENDING) ||
					StringUtils.equals(getStatus(), CeNConstants.REGINFO_PENDING) ||
					StringUtils.equals(getStatus(), CeNConstants.REGINFO_PASSED)) {
				return false;
			}
			if (StringUtils.equals(getStatus(), CeNConstants.REGINFO_SUBMISION_FAIL)) {
				int jobID = Integer.parseInt(getJobId());
				if (jobID > 0) {
					return false;
				}
			}
		}
		return true;
	}
	

	/**
	 * @param test
	 *            The Biologist test to add to the list.
	 */
	public void addSubmitToBiologistTest(BatchSubmissionToScreenModel test) {
		submitToBiologistTestList.add(test);
		//addObserver(test);
		setModified(true);
	}
	
	public String getScreenSubmissionXML()
	{
		StringBuffer xmlbuff = new StringBuffer();
		int listSize = this.submitToBiologistTestList.size();
		for(int i = 0; i < listSize ; i ++)
		{
			BatchSubmissionToScreenModel model = (BatchSubmissionToScreenModel)this.submitToBiologistTestList.get(i);
			if (model != null) {
				xmlbuff.append("<Entry>");
				xmlbuff.append("<Addition_Order>"+ (i+1) +"</Addition_Order>");
				xmlbuff.append(model.toXML());
				xmlbuff.append("</Entry>");
			}
		}
		
		return xmlbuff.toString();
	}
	
	
	public String getBatchResidualSolventXML()
	{
		StringBuffer xmlbuff = new StringBuffer();
		int listSize = this.residualSolventList.size();
		for(int i = 0; i < listSize ; i ++)
		{
			BatchResidualSolventModel model = (BatchResidualSolventModel)this.residualSolventList.get(i);
			xmlbuff.append("<Entry>");
			xmlbuff.append("<Addition_Order>"+ (i+1) +"</Addition_Order>");
			xmlbuff.append(model.toXML());
			xmlbuff.append("</Entry>");
		}
		
		return xmlbuff.toString();
	}
	
	public String getBatchSolubilitySolventXML()
	{
		StringBuffer xmlbuff = new StringBuffer();
		int listSize = this.solubilitySolventList.size();
		for(int i = 0; i < listSize ; i ++)
		{
			BatchSolubilitySolventModel model = (BatchSolubilitySolventModel)this.solubilitySolventList.get(i);
			xmlbuff.append("<Entry>");
			xmlbuff.append("<Addition_Order>"+ (i+1) +"</Addition_Order>");
			xmlbuff.append(model.toXML());
			xmlbuff.append("</Entry>");
		}
		
		return xmlbuff.toString();
	}

	/**
	 * @return Returns the intermediate_or_test.
	 */
	public String getIntermediateOrTest() { return intermediate_or_test; }
	
	/**
	 * @param intermediate The intermediate to set.
	 */
	public void setIntermediateOrTest(String intermediate_or_test) {
		if (intermediate_or_test == null) {
			intermediate_or_test = "U";
		}
		if (!StringUtils.equals(this.intermediate_or_test, intermediate_or_test)) {
			this.intermediate_or_test = intermediate_or_test;
			setModified(true);
		}
	}

	public void deepCopy(BatchRegInfoModel srcBatchRegInfoModel) {
		compoundSource = srcBatchRegInfoModel.getCompoundSource(); 
		compoundSourceDetail = srcBatchRegInfoModel.getCompoundSourceDetail();
		registrationDate = srcBatchRegInfoModel.getRegistrationDate();
		registrationStatus = srcBatchRegInfoModel.getRegistrationStatus();
		submissionStatus = srcBatchRegInfoModel.getSubmissionStatus();
		status = srcBatchRegInfoModel.getStatus();
		jobId = srcBatchRegInfoModel.getJobId();
		batchTrackingId = srcBatchRegInfoModel.getBatchTrackingId();
		errorMsg = srcBatchRegInfoModel.getErrorMsg();
		hitId = srcBatchRegInfoModel.getHitId();
		residualSolventList = srcBatchRegInfoModel.getResidualSolventList();
		solubilitySolventList = srcBatchRegInfoModel.getSolubilitySolventList();
		intermediate_or_test = srcBatchRegInfoModel.getIntermediateOrTest();;

		submitToBiologistTestList = srcBatchRegInfoModel.getOldSubmitToBiologistTestList();
        submitToBiologistTestList.addAll(srcBatchRegInfoModel.getNewSubmitToBiologistTestList());
		submitContainerList = srcBatchRegInfoModel.getSubmitContainerList();
		batchVnVInfo = srcBatchRegInfoModel.getBatchVnVInfo();

		//compoundRegistration codes must be of GenericCode type
		compoundRegistrationHazardCodes = srcBatchRegInfoModel.getCompoundRegistrationHazardCodes(); // List of two digit codes that correspond to a COMPOUND_REGISTRATION_HAZARD_CDT table
		compoundRegistrationHandlingCodes = srcBatchRegInfoModel.getCompoundRegistrationHandlingCodes(); // List of two digit codes that correspond to a COMPOUND_REGISTRATION_HANDLING_CDT table
		compoundRegistrationStorageCodes = srcBatchRegInfoModel.getCompoundRegistrationStorageCodes(); // List of two digit codes that correspond to a COMPOUND_REGISTRATION_STORAGE_CDT table
		storageComments = srcBatchRegInfoModel.getStorageComments(); // Batch Storage Comments
		hazardComments = srcBatchRegInfoModel.getHazardComments(); // Batch Hazard Comments
		handlingComments = srcBatchRegInfoModel.getHandlingComments();// Batch handling comments in case there is a special need
		
		//submission
		compoundAggregationStatus = srcBatchRegInfoModel.getCompoundAggregationStatus();
		compoundAggregationStatusMessage = srcBatchRegInfoModel.getCompoundAggregationStatusMessage();
		compoundRegistrationStatus = getCompoundRegistrationStatus();
		compoundRegistrationStatusMessage = srcBatchRegInfoModel.getCompoundRegistrationStatusMessage();
		purificationServiceStatus = srcBatchRegInfoModel.getPurificationServiceStatus();
		purificationServiceStatusMessage = srcBatchRegInfoModel.getPurificationServiceStatusMessage();
		compoundManagementStatus = srcBatchRegInfoModel.getCompoundManagementStatus();
		compoundManagementStatusMessage = srcBatchRegInfoModel.getCompoundManagementStatusMessage();
		offset = srcBatchRegInfoModel.getOffset();
		
		
		parentBatchNumber = srcBatchRegInfoModel.getParentBatchNumber();
		//4-0002
		conversationalBatchNumber = srcBatchRegInfoModel.getConversationalBatchNumber();
		meltPointRange = srcBatchRegInfoModel.getMeltPointRange(); // Used for BatchInfo
		compoundState = srcBatchRegInfoModel.getCompoundState(); // String will be provided from dropdown list
		protectionCode = srcBatchRegInfoModel.getProtectionCode(); // Protection Code used to determine orderability
		regStatus = srcBatchRegInfoModel.getRegStatus(); // Registration Status of this batch
		vendorInfo = srcBatchRegInfoModel.getVendorInfo();
		productFlag = srcBatchRegInfoModel.productFlag; // Use to indicate if this was a
		owner = srcBatchRegInfoModel.getOwner(); // NTID of person who sponsored the creation of this batch
		comments = srcBatchRegInfoModel.getComments(); // Batch Comments
	}
	
	public String getHandlingComments() {
		return handlingComments;
	}

	public void setHandlingComments(String handlingComments) {
		if (handlingComments == null)
			return;
		this.handlingComments = handlingComments;
		this.modelChanged = true;
	}

	public String getHazardComments() {
		return hazardComments;
	}

	public void setHazardComments(String hazardComments) {
		if (hazardComments == null)
			return;
		this.hazardComments = hazardComments;
		this.modelChanged = true;
	}

	public String getStorageComments() {
		return storageComments;
	}

	public void setStorageComments(String storageComments) {
		if (storageComments == null)
			return;
		this.storageComments = storageComments;
		this.modelChanged = true;
	}

	/**
	 * @param meltPointRange
	 *            the meltPointRange to set
	 */
	public void setMeltPointRange(TemperatureRangeModel meltPointRange) {
		this.meltPointRange = meltPointRange;
		this.modelChanged = true;
	}

	public TemperatureRangeModel getMeltPointRange() {
		return meltPointRange;
	}

	public String getCompoundState() {
		return compoundState;
	}

	public void setCompoundState(String compoundState) {
		if (compoundState == null)
			return;
		this.compoundState = compoundState;
		this.modelChanged = true;
	}

	public String getProtectionCode() {
		return protectionCode;
	}

	public void setProtectionCode(String protectionCode) {
		if (protectionCode == null)
			return;
		this.protectionCode = protectionCode;
		this.modelChanged = true;
	}

	public String getRegStatus() {
		return regStatus;
	}

	public void setRegStatus(String regStatus) {
		if (regStatus == null)
			return;
		this.regStatus = regStatus;
		this.modelChanged = true;
	}

	public ExternalSupplierModel getVendorInfo() {
		return vendorInfo;
	}

	public void setVendorInfo(ExternalSupplierModel vendorInfo) {
		if (vendorInfo == null)
			vendorInfo = new ExternalSupplierModel();
		this.vendorInfo = vendorInfo;
		this.modelChanged = true;
	}

	/**
	 * @return the productFlag
	 */
	public boolean isProductFlag() {
		return productFlag;
	}

	/**
	 * @param productFlag
	 *            the productFlag to set
	 */
	public void setProductFlag(boolean productFlag) {
		this.productFlag = productFlag;
		this.modelChanged = true;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		if (owner == null)
			return;
		this.owner = owner;
		this.modelChanged = true;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		if (comments == null)
			return;
		this.comments = comments;
		this.modelChanged = true;
	}
	
	public String getHazardCodesXML() {
		StringBuffer xmlbuff = new StringBuffer();
		
		for (String genCode : compoundRegistrationHazardCodes) {
			if (StringUtils.isNotBlank(genCode)) {
				xmlbuff.append("<Entry>");
				xmlbuff.append(genCode);
				xmlbuff.append("</Entry>");
			}
		}

		return xmlbuff.toString();
	}
	
	public String getHandlingCodesXML() {
		StringBuffer xmlbuff = new StringBuffer();

		for (String genCode : compoundRegistrationHandlingCodes) {
			if (StringUtils.isNotBlank(genCode)) {
				xmlbuff.append("<Entry>");
				xmlbuff.append(genCode);
				xmlbuff.append("</Entry>");
			}
		}

		return xmlbuff.toString();
	}
	
	public String getStorageCodesXML() {
		StringBuffer xmlbuff = new StringBuffer();

		for (String genCode : compoundRegistrationStorageCodes) {
			if (StringUtils.isNotBlank(genCode)) {
				xmlbuff.append("<Entry>");
				xmlbuff.append(genCode);
				xmlbuff.append("</Entry>");
			}
		}

		return xmlbuff.toString();
	}
	
	public String getContainerSubmissionXML() {
		StringBuffer xmlbuff = new StringBuffer();

		for (int i = 0; i < this.submitContainerList.size(); ++i) {
			BatchSubmissionContainerInfoModel model = this.submitContainerList.get(i);
			xmlbuff.append("<Entry>");
			xmlbuff.append("<Addition_Order>" + (i + 1) + "</Addition_Order>");
			xmlbuff.append(model.toXML());
			xmlbuff.append("</Entry>");
		}

		return xmlbuff.toString();
	}

	public void resetRegistrationInfo() {
		conversationalBatchNumber = "";
		parentBatchNumber="";
		errorMsg = "";
		offset = CeNConstants.INVALID_NUMBER_INTEGER;
		jobId = "-1";
		hitId = "";
		registrationStatus = null;
		registrationDate = null;
		batchTrackingId = CeNConstants.INVALID_NUMBER_LONG;
		compoundAggregationStatus = CeNConstants.REGINFO_NOT_SUBMITTED;
		compoundAggregationStatusMessage = CeNConstants.REGINFO_NOT_SUBMITTED;
		compoundRegistrationStatus = CeNConstants.REGINFO_NOT_SUBMITTED;
		compoundRegistrationStatusMessage = CeNConstants.REGINFO_NOT_SUBMITTED;
		purificationServiceStatus = CeNConstants.REGINFO_NOT_SUBMITTED;
		purificationServiceStatusMessage = CeNConstants.REGINFO_NOT_SUBMITTED;
		compoundManagementStatus = CeNConstants.REGINFO_NOT_SUBMITTED;
		compoundManagementStatusMessage = CeNConstants.REGINFO_NOT_SUBMITTED;
		submissionStatus = "";
		status = "";

		submitToBiologistTestList = new ArrayList<BatchSubmissionToScreenModel>();
		submitContainerList = new ArrayList<BatchSubmissionContainerInfoModel>();
		
		batchVnVInfo = new BatchVnVInfoModel();
	}

	public void setInternalBatchNumber(String internalBatchNumber) {
		this.internalBatchNumber = internalBatchNumber;
	}

	public String getInternalBatchNumber() {
		return internalBatchNumber;
	}
}
