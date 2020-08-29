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
package com.chemistry.enotebook.client.gui.page.regis_submis;

	/*
	 * Created on Oct 14, 2004
	 *
	 * TODO To change the template for this generated file go to
	 * Window - Preferences - Java - Code Style - Code Templates
	 */

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.compoundmanagement.classes.BarcodeValidationVO;
import com.chemistry.enotebook.compoundregistration.classes.RegistrationDetailsVO;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchTypeFactory;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchTypeException;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.registration.RegistrationSvcUnavailableException;
import com.chemistry.enotebook.registration.RegistrationTokenInvalidException;
import com.chemistry.enotebook.registration.ScreenSearchParams;
import com.chemistry.enotebook.registration.delegate.RegistrationDelegateException;
import com.chemistry.enotebook.registration.delegate.RegistrationServiceDelegate;
import com.chemistry.enotebook.session.delegate.SessionTokenDelegate;
import com.chemistry.enotebook.session.security.CompoundManagementEmployee;
import com.chemistry.enotebook.util.ExceptionUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import javax.swing.*;
import java.io.IOException;
import java.io.StringReader;
import java.rmi.UnmarshalException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

	/**
	 * 
	 * 
	 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
	 */
	public class SingletonRegSubHandler {
		// private CodeTableCache codeTableCache = null;
		private RegistrationServiceDelegate registrationServiceDelegate = null;
		private NotebookPageModel pageModel = null;
		private String VnVResultString;
		private JFrame mainFrame = null;

		public SingletonRegSubHandler() {
			mainFrame = MasterController.getGUIComponent();
			try {
				registrationServiceDelegate = new RegistrationServiceDelegate();
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(mainFrame,
						"Exception occured while instantiating registration delegate.", e);
			}
		}

		public SingletonRegSubHandler(NotebookPageModel nbPage) {
			this();
			this.pageModel = nbPage;
		}

		public void dispose() {
			registrationServiceDelegate = null;
			pageModel = null;
			VnVResultString = null;
		}

		/**
		 * @return Returns the registrationServiceDelegate.
		 */
		public RegistrationServiceDelegate getRegistrationServiceDelegate() {
			try {
				if (registrationServiceDelegate == null) {
					registrationServiceDelegate = new RegistrationServiceDelegate();
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, e);
			}
			return registrationServiceDelegate;
		}

		/**
		 * @param nbPage
		 *            The nbPage to set.
		 */
		public void setPageModel(NotebookPageModel nbPage) {
			this.pageModel = nbPage;
		}

		/**
		 * build batchList.
		 * 
		 */
		public List<ProductBatchModel> buildBatchList() {
			List<ProductBatchModel> batchList = pageModel.getSingletonBatchs();
			if (pageModel != null &&  batchList != null)
				batchList =  getUserAddedBatchesSorted(batchList);
			return batchList;
		}

		public String doVnV(String sdFile, String sic) {
			try {
				return getRegistrationServiceDelegate().executeVnV(sdFile, sic);
			} catch (RegistrationSvcUnavailableException e4) {
				JOptionPane.showMessageDialog(mainFrame, "Registration Service is currently unavailable, please try again later.");
			} catch (Exception e) {
				Throwable t = ExceptionUtils.getRootCause(e);
				if (t.getMessage().indexOf("WSDLParseException") >= 0) {
					CeNErrorHandler.showMsgOptionPane(mainFrame, "CompoundRegistration Service is unavailable.",
							"Unable to link to CompoundRegistration at this time, please try again later.", JOptionPane.ERROR_MESSAGE);
					CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e,
							"Unable to link to CompoundRegistration at this time, please try again later.");
				} else if (e.getCause() instanceof UnmarshalException) {
					Throwable sf = e.getCause();
					sf.printStackTrace();
					Exception error = (Exception) sf;
					if (sf.getMessage().indexOf("SOAPFaultException") >= 0) {
						StringBuffer text = new StringBuffer("Trying to register an invalid molecule, Server returned with a fault\n");
						text.append("Possible cause : Attempt to VnV multiple molecules as a single compound.\n");
						text.append("Workaround		: Make sure single contiguous molecule is passed to VnV (Parent).");
						CeNErrorHandler.getInstance().logErrorMsg(mainFrame, text.toString(), "Invalid Compound !!",
								JOptionPane.ERROR_MESSAGE);
					} else
						CeNErrorHandler.getInstance().logErrorMsg(mainFrame, e.toString(), "Invalid Compound !!",
								JOptionPane.ERROR_MESSAGE);
				} else
					CeNErrorHandler.getInstance().logErrorMsg(mainFrame, e.toString(),
							"Unable to link to CompoundRegistration at this time, please try again later.", JOptionPane.ERROR_MESSAGE);
			} 
			return null;
		}

		public String ifVnVFailed(String vnvResult) {
			String errorMsg = "NO";
			StringReader reader = null;
			reader = new StringReader(vnvResult);
			SAXBuilder builder = new SAXBuilder();
			Document doc;
			try {
				doc = builder.build(reader);
				Element root = doc.getRootElement();
				Element molFileDataElement = (Element) XPath.selectSingleNode(root, "/error");
				if (molFileDataElement != null) {
					if (molFileDataElement.getText() != null) {
						errorMsg = molFileDataElement.getText();
					}
				}
			} catch (JDOMException e) {
				CeNErrorHandler.getInstance().logErrorMsg(mainFrame, e.toString(), "The registration SOAP service is not available.",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e) {
				CeNErrorHandler.getInstance().logErrorMsg(mainFrame, e.toString(), "The registration SOAP service is not available.",
						JOptionPane.INFORMATION_MESSAGE);
			}
			return errorMsg;
		}

		public String ifVnVValid(String vnvResult) {
			String result = "ERROR";
			StringReader reader = null;
			reader = new StringReader(vnvResult);
			SAXBuilder builder = new SAXBuilder();
			Document doc;
			try {
				doc = builder.build(reader);
				Element root = doc.getRootElement();
				Element molFileDataElement = (Element) XPath.selectSingleNode(root, "/vnvResult/numErrors");
				if (molFileDataElement == null || molFileDataElement.getText() == null || molFileDataElement.getText().length() == 0) {
					result = "ERROR processing VnV Results";
				} else {
					if ((new Integer(molFileDataElement.getText())).intValue() == 0)
						result = ""; // No Errors
					else {
						List errors = XPath.selectNodes(root, "/vnvResult/errorText");
						if (errors == null || errors.size() == 0)
							result = "ERROR unknown";
						else {
							result = "";
							for (Iterator it=errors.iterator(); it.hasNext(); ) {
								Element errorTextElement = (Element)it.next();
								
								if (result.length() > 0) result = result + "\n                   ";
								result = result + errorTextElement.getTextNormalize();
							}
							if (result.length() == 0) result = "ERROR unknown";
						}
					}
				}
			} catch (JDOMException e) {
				CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, "The registration SOAP service is not available.", e);
			} catch (IOException e) {
				CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, "The registration SOAP service is not available.", e);
			}
			return result;
		}

		public VnVResultVO processVnvResult(String vnvResult) {
			VnVResultVO vnVResultVO = new VnVResultVO();
			try {
				StringReader reader = null;
				reader = new StringReader(vnvResult);
				SAXBuilder builder = new SAXBuilder();
				Document doc;
				doc = builder.build(reader);
				Element root = doc.getRootElement();
				Element molFileDataElement = (Element) XPath.selectSingleNode(root, "/vnvResult/molFileData");
				vnVResultVO.setResultStructureString(molFileDataElement.getText());
				Element molFormulaElement = (Element) XPath.selectSingleNode(root, "/vnvResult/molFormula");
				vnVResultVO.setMolFormula(molFormulaElement.getTextNormalize());
				Element molWeightElement = (Element) XPath.selectSingleNode(root, "/vnvResult/molWeight");
				vnVResultVO.setMolWeight(new Double(molWeightElement.getTextNormalize()).doubleValue());
				List errors = XPath.selectNodes(root, "/vnvResult/errorText");
				if (errors == null || errors.size() == 0)
					vnVResultVO.setErrorMessage(VnVResultVO.DEFAULT_MESSAGE);
				else {
					String msg = "";
					for (Iterator it=errors.iterator(); it.hasNext(); ) {
						Element errorTextElement = (Element)it.next();						
						if (msg.length() > 0) msg = msg + "\n                   ";
						msg = msg + errorTextElement.getTextNormalize();
					}
					if (msg.length() == 0) msg = VnVResultVO.DEFAULT_MESSAGE;
					vnVResultVO.setErrorMessage(msg);
				}
				Element validSICElement = (Element) XPath.selectSingleNode(root, "/vnvResult/validSIC");
				vnVResultVO.setValidSIC(validSICElement.getTextNormalize());
				Element suggestedSICElement = (Element) XPath.selectSingleNode(root, "/vnvResult/suggestedSICs");
				vnVResultVO.setSicList(this.getSuggestedSICs(suggestedSICElement.getTextNormalize()));
				// Check if there was an error determining a default SIC
				if (validSICElement.getTextNormalize().startsWith("ERROR")) {
					if (vnVResultVO.getErrorMessage().equals(VnVResultVO.DEFAULT_MESSAGE)) {
						String msg = suggestedSICElement.getTextNormalize();
						int colonPos = msg.indexOf(":");
						if (colonPos >= 0)
							msg = msg.substring(0, colonPos);
						vnVResultVO.setErrorMessage(msg);
					}
				}
				return vnVResultVO;
			} catch (JDOMException e) {
				CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, e);
			} catch (IOException e) {
				CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, e);
			}
			return null;
		}

		/**
		 * util to process suggestedSICs
		 */
		public ArrayList getSuggestedSICs(String suggestedSICString) {
			ArrayList sicList = new ArrayList();
			if (suggestedSICString != null && !suggestedSICString.equals("")) {
				int startP = suggestedSICString.indexOf("(");
				int endP = suggestedSICString.indexOf(")");
				String codeListString = (startP < 0) ? suggestedSICString : suggestedSICString.substring(startP + 1, endP);
				//System.out.println("The list string is: " + codeListString);
				String[] result;
				boolean colonFound = (codeListString.indexOf(":") >= 0);	// old service used spaces new one may use colons
				if (colonFound) 
					result = codeListString.split(":"); 
				else
					result = codeListString.split("\\s");
				for (int x = 0; x < result.length; x++) {
					sicList.add(result[x]);
				}
			}
			return sicList;
		}

		// Method to ensure that value does not contain >, <, >=, <= since these
		// will cause issue with
		// how SD files are processed.
		public String handleGTLT(String value) {
			return value;
			// if (value == null) value = "";
			// return value.replaceAll(">=", "GE").replaceAll("<=",
			// "LE").replaceAll(">", "GT").replaceAll("<", "LT");
		}

		public String buildSDFIle(ProductBatchModel selectedBatch, boolean forCompoundRegistrationReg) {
			RegistrationHandler handler = new RegistrationHandler(pageModel);
			return handler.buildSDFile(selectedBatch, forCompoundRegistrationReg);
	
		}

		private String convertPurityOperatorCode(String purityOperator) {
			if (purityOperator.equals("=")) {
				return "EQ";
			} else if (purityOperator.equals("<")) {
				return "LT";
			} else if (purityOperator.equals(">")) {
				return "GT";
			} else if (purityOperator.equals("~")) {
				return "AP";
			}
			return purityOperator;
		}

		private String validateNoteBookPageNumber(String npNumber) {
			String newString = npNumber;
			String prop = null;
			try {
				prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_COMPOUND_REGISTRATION_PAGE_TRUNCATE);
			} catch (Exception e) {
			}
			if (prop != null && prop.equalsIgnoreCase("true")) {
				if (npNumber.length() > 3)
					newString = npNumber.substring(npNumber.length() - 3, npNumber.length());
			}
			return newString;
		}
	   
		public RegistrationDetailsVO getRegistrationInformation(String batchNumber) {
			try {
				return registrationServiceDelegate.getRegistrationInformation(batchNumber);
			} catch (RegistrationSvcUnavailableException e4) {
				JOptionPane.showMessageDialog(mainFrame, "Registration Service is currently unavailable, please try again later.");
			} catch (RegistrationDelegateException e) {
				CeNErrorHandler.getInstance().logExceptionMsg(mainFrame,
						"Exception occured while performing interactive registration.", e);
			}
			return null;
		}
	   
		// private String buildTagValue(String[] valueArray)
		// {
		// String tagValueString = new String();
		// for (int i = 0; i< valueArray.length; i++) {
		// if (i > 0 ) {
		// tagValueString += "\n" + valueArray[i];
		// } else {
		// tagValueString += valueArray[i];
		// }
		// }
		// return tagValueString;
		// }
		private String buildTagValue(ArrayList list) {
			String tagValueString = new String();
			for (int i = 0; i < list.size(); i++) {
				if (i > 0) {
					tagValueString += "\n" + list.get(i);
				} else {
					tagValueString += list.get(i);
				}
			}
			return tagValueString;
		}

		private String processCodes(List valueList) {
			String tagValueString = new String();
			for (int i = 0; i < valueList.size(); i++) {
				if (i > 0) {
					tagValueString += "\n" + valueList.get(i);
				} else {
					tagValueString += valueList.get(i);
				}
			}
			return tagValueString;
		}
	/*
		public String performInteractiveRegistration(String batchSDF) throws RegistrationDelegateException {
			try {
				return registrationServiceDelegate.submitSingletonForRegistration(batchSDF, "INTERACTIVE", MasterController.getUser()
						.getNTUserID());
			} catch (RegistrationSvcUnavailableException e4) {
				JOptionPane.showMessageDialog(mainFrame, "Registration Service is currently unavailable, please try again later.");
			} catch (RegistrationDelegateException e) {
				CeNErrorHandler.getInstance().logExceptionMsg(mainFrame,
						"Exception occured while performing interactive registration.", e);
			} catch (RegistrationTokenInvalidException e1) {
				CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, "The session token is invalid.", e1);
			}
			return null;
		}
	*/
		private String getUserNTDomain(String userDomainStr) {
			String[] userDomainStringArray = userDomainStr.split(":");
			return userDomainStringArray[0].toUpperCase();
		}

		public ArrayList performScreenSearch(ScreenSearchParams params) throws RegistrationDelegateException {
			try {
				return (ArrayList) this.getRegistrationServiceDelegate().searchForScreens(params);
			} catch (RegistrationSvcUnavailableException e4) {
				JOptionPane.showMessageDialog(mainFrame, "Registration Service is currently unavailable, please try again later.");
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, "Exception occured while performing screen search.", e);
			}
			return new ArrayList();
		}

		public String getJobInfoFromMsg(String msg, ArrayList jobErrors) {
			String result = "-2";
			try {
				if (msg.endsWith(")"))
					msg = msg.substring(0, msg.lastIndexOf(")") - 1);
				StringReader reader = new StringReader(msg);
				SAXBuilder builder = new SAXBuilder();
				Document doc = builder.build(reader);
				Element root = doc.getRootElement();
				Element jobIdElement = (Element) XPath.selectSingleNode(root, "/jobIdReturn/jobId");
				if (jobErrors != null) {
					Element msgElement = (Element) XPath.selectSingleNode(root, "/jobIdReturn/message");
					if (msgElement != null) {
						Attribute attrib = msgElement.getAttribute("type");
						jobErrors.add((attrib == null) ? "" : msgElement.getAttribute("type").getValue());
						jobErrors.add(msgElement.getTextTrim());
					}
				}
				result = jobIdElement.getText().trim();
			} catch (JDOMException e) {
				CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, e);
			} catch (IOException e) {
				CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, e);
			}
			return result;
		}
	/*
		public void performRegistrationStatusUpdate() {
			ArrayList batchList = buildBatchList();
			if (this.statusDirty(batchList)) {
				ArrayList statuVOList = this.pullRegistrationStatus(batchList);
				updateBatchRegStatus(statuVOList, batchList);
			}
		}
	*/
		public boolean statusDirty(ArrayList batchList) {
			boolean isDirty = false;
			for (int i = 0; i < batchList.size(); i++) {
				ProductBatchModel pBatch = (ProductBatchModel) batchList.get(i);
				if (pBatch.getRegInfo().getStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING)) {
					isDirty = true;
					break;
				}
			}
			return isDirty;
		}
	/*
		public ArrayList pullRegistrationStatus(ArrayList batchList) {
			ArrayList jobIdList = new ArrayList();
			ArrayList resultList = new ArrayList();
			for (int i = 0; i < batchList.size(); i++) {
				ProductBatch pBatch = (ProductBatch) batchList.get(i);
				if (pBatch.getRegInfo().getStatus().equals(BatchRegistrationInfo.PROCESSING)) {
					jobIdList.add(pBatch.getRegInfo().getJobId());
				}
			}
			if (jobIdList.size() > 0) {
				// pull registration status info and update the batch
				try {
					RegistrationServiceDelegate reg = new RegistrationServiceDelegate(MasterController.getUser().getSessionIdentifier());
					resultList = reg.getRegistrationStatus(jobIdList);
					// updateBatchRegStatus(resultList,batchList);
				} catch (RegistrationSvcUnavailableException e4) {
					JOptionPane.showMessageDialog(mainFrame, "Registration Service is currently unavailable, please try again later.");
				} catch (Exception e1) {
					CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, "Error occured while pulling status of registration.", e1);
				}
			}
			return resultList;
		}

		private void updateBatchRegStatus(ArrayList batchInfoVOList, ArrayList batchList) {
			if (batchInfoVOList.size() <= 0) {
				return;
			} else {
				for (int i = 0; i < batchList.size(); i++) {
					ProductBatch iBatch = (ProductBatch) batchList.get(i);
					for (int j = 0; j < batchInfoVOList.size(); j++) {
						BatchRegStatusTrackingVO regStatus = (BatchRegStatusTrackingVO) batchInfoVOList.get(j);
						if (regStatus.getJobID().equals(iBatch.getRegInfo().getJobId())) {
							if (regStatus.getCompoundStatus().equals("PASSED")) {
								iBatch.setConversationalBatchNumber(regStatus.getBatchNumber());
								iBatch.setParentBatchNumber(regStatus.getCompoundParent());
								iBatch.getCompound().setRegNumber(regStatus.getCompoundNumber());
								iBatch.getRegInfo().setRegistrationDate(regStatus.getRegDate());
								iBatch.getRegInfo().setJobId(regStatus.getJobID());
								iBatch.getRegInfo().setBatchTrackingId(regStatus.getBatchTrackingID());
								iBatch.getRegInfo().setStatus(regStatus.getCompoundStatus());
								iBatch.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.REGISTERED);
								if (iBatch.getRegInfo().getSubmissionStatus().equals(BatchRegistrationInfo.POST_SUBMITTING)) {
									// update submission status and model, calculate
									// total amount of sample, update GUI
									iBatch.getRegInfo().setSubmissionStatus(BatchRegistrationInfo.SUBMITTED);
								}
								// updateBatchList(iBatch);
							} else if (regStatus.getCompoundStatus().equals("FAILED")) {
								iBatch.getRegInfo().setStatus(regStatus.getCompoundStatus());
								iBatch.getRegInfo().setErrorMsg(regStatus.getDetailString());
								iBatch.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.NOT_REGISTERED);
								if (iBatch.getRegInfo().getSubmissionStatus().equals(BatchRegistrationInfo.POST_SUBMITTING)) {
									// update submission status and model, calculate
									// total amount of sample, update GUI
									iBatch.getRegInfo().setSubmissionStatus(BatchRegistrationInfo.NOT_SUBMITTED);
								}
								// updateBatchList(iBatch);
							}
						}
					}
				}
			}
		}
		*/

		/**
		 * @return Returns the vnVResultString.
		 */
		public String getVnVResultString() {
			return VnVResultString;
		}

		/**
		 * @param vnVResultString
		 *            The vnVResultString to set.
		 */
		public void setVnVResultString(String vnVResultString) {
			VnVResultString = vnVResultString;
		}
		
		public BarcodeValidationVO validateBarcode(BarcodeValidationVO barcodeVO) {
			RegistrationServiceDelegate reg;
			try {
				reg = new RegistrationServiceDelegate();
				return reg.validateBarcode(barcodeVO);
			} catch (RegistrationSvcUnavailableException e4) {
				JOptionPane.showMessageDialog(mainFrame, "Registration Service is currently unavailable, please try again later.");
			} catch (RegistrationTokenInvalidException e1) {
				CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, "The session token is invalid.", e1);
			} catch (Exception e1) {
				CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, "Error occured while validating barcode.", e1);
			}
			return barcodeVO;
		}

		public String getOwnerEmpId(String ntName) {
			String empId = null;
			if (ntName != null && ntName.length() > 0) {
				try {
					SessionTokenDelegate smObj = new SessionTokenDelegate();
					CompoundManagementEmployee emp = smObj.getCompoundManagementEmployeeID(ntName);
					if (emp != null) {
						empId = emp.getEmployeeId();
						if (empId != null && empId.trim().length() == 0)
							empId = null;
					}
				} catch (Exception e) {
					if (e.getLocalizedMessage().indexOf("User not in CEN_USERS and not in COMPOUND_MANAGEMENT_EMPLOYEE") < 0)
						CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, "Employee ID look up failed.", e);
				}
			}
			return empId;
		}

		public static String getBatchHazardString(ProductBatchModel batch) {
			StringBuffer hazardStr = new StringBuffer();
			HashMap hazardMap = RegCodeMaps.getInstance().getHazardMap();
			List list = batch.getRegInfo().getCompoundRegistrationHazardCodes();
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					if (i > 0)
						hazardStr.append(", ");
					hazardStr.append(((String) hazardMap.get(list.get(i))).trim());
				}
			}
			if (batch.getHazardComments() != null && batch.getHazardComments().length() > 0) {
				if (hazardStr.length() > 0)
					hazardStr.append(", ");
				hazardStr.append(batch.getHazardComments().trim());
			}
			if (batch.getCompound().getHazardComments() != null && batch.getCompound().getHazardComments().length() > 0) {
				if (hazardStr.length() > 0)
					hazardStr.append(", ");
				hazardStr.append(batch.getCompound().getHazardComments().trim());
			}
			return hazardStr.toString();
		}
		
		// vb added 11/21 for models
		public static String getProductBatchModelHazardString(ProductBatchModel batch) {
			StringBuffer hazardStr = new StringBuffer();
			HashMap hazardMap = RegCodeMaps.getInstance().getHazardMap();
			List list = batch.getRegInfo().getCompoundRegistrationHazardCodes();
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					if (i > 0)
						hazardStr.append(", ");
					hazardStr.append(((String) hazardMap.get(list.get(i))).trim());
				}
			}
			if (batch.getHazardComments() != null && batch.getHazardComments().length() > 0) {
				if (hazardStr.length() > 0)
					hazardStr.append(", ");
				hazardStr.append(batch.getHazardComments().trim());
			}
			if (batch.getCompound().getHazardComments() != null && batch.getCompound().getHazardComments().length() > 0) {
				if (hazardStr.length() > 0)
					hazardStr.append(", ");
				hazardStr.append(batch.getCompound().getHazardComments().trim());
			}
			return hazardStr.toString();
		}


		public static String getBatchHandlingString(ProductBatchModel batch) {
			StringBuffer handlingStr = new StringBuffer();
			HashMap handlingMap = RegCodeMaps.getInstance().getHandlingMap();
			List list = batch.getRegInfo().getCompoundRegistrationHandlingCodes();
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					if (i > 0)
						handlingStr.append(", ");
					handlingStr.append(((String) handlingMap.get(list.get(i))).trim());
				}
			}
			if (batch.getHandlingComments() != null && batch.getHandlingComments().length() > 0) {
				if (handlingStr.length() > 0)
					handlingStr.append(", ");
				handlingStr.append(batch.getHandlingComments().trim());
			}
			return handlingStr.toString();
		}

		// vb 11/21 for models
		public static String getProductBatchModelHandlingString(ProductBatchModel batch) {
			StringBuffer handlingStr = new StringBuffer();
			HashMap handlingMap = RegCodeMaps.getInstance().getHandlingMap();
			List list = batch.getRegInfo().getCompoundRegistrationHandlingCodes();
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					if (i > 0)
						handlingStr.append(", ");
					handlingStr.append(((String) handlingMap.get(list.get(i))).trim());
				}
			}
			if (batch.getHandlingComments() != null && batch.getHandlingComments().length() > 0) {
				if (handlingStr.length() > 0)
					handlingStr.append(", ");
				handlingStr.append(batch.getHandlingComments().trim());
			}
			return handlingStr.toString();
		}
		
		public static String getBatchStorageString(ProductBatchModel batch) {
			StringBuffer storageStr = new StringBuffer();
			HashMap<String, String> storageMap = RegCodeMaps.getInstance().getStorageMap();
			List list = batch.getRegInfo().getCompoundRegistrationStorageCodes();
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					if (i > 0)
						storageStr.append(", ");
					storageStr.append((storageMap.get(list.get(i))).trim());
				}
			}
			if (batch.getStorageComments() != null && batch.getStorageComments().length() > 0) {
				if (storageStr.length() > 0)
					storageStr.append(", ");
				storageStr.append(batch.getStorageComments().trim());
			}
			return storageStr.toString();
		}
		
		public static String getProductBatchModelStorageString(ProductBatchModel batch) {
			StringBuffer storageStr = new StringBuffer();
			HashMap<String, String> storageMap = RegCodeMaps.getInstance().getStorageMap();
			List<String> list = batch.getRegInfo().getCompoundRegistrationStorageCodes();
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					if (i > 0)
						storageStr.append(", ");
					storageStr.append((storageMap.get(list.get(i))).trim());
				}
			}
			if (batch.getStorageComments() != null && batch.getStorageComments().length() > 0) {
				if (storageStr.length() > 0)
					storageStr.append(", ");
				storageStr.append(batch.getStorageComments().trim());
			}
			return storageStr.toString();
		}
		
		/* 
		 * @param bType
		 * @return List of batches sorted on CompareTo Function
		 */
		public ArrayList<ProductBatchModel> getUserAddedBatchesSorted(List<ProductBatchModel> batchesList) {
			ArrayList<ProductBatchModel> userAddedBatchesList = new ArrayList<ProductBatchModel>();
			for (int i=0; i<batchesList.size(); i++)
			{
				if (isUserAddedBatch(batchesList.get(i)))
					userAddedBatchesList.add(batchesList.get(i));
			}
			/*Collections.sort(userAddedBatchesList);*/ //Need to fix this Jags_todo...
			return userAddedBatchesList;
		}
		
		private boolean isUserAddedBatch(ProductBatchModel productBatchModel) {
			BatchType userAddedBatchType = null;
			try {
				userAddedBatchType = BatchTypeFactory.getBatchType(CeNConstants.BATCH_TYPE_ACTUAL);
			} catch (InvalidBatchTypeException e) {
				e.printStackTrace();
			}
			BatchType batchType = productBatchModel.getBatchType();
			if (batchType.compareTo(userAddedBatchType) == 0)
				return true;
			else
				return false;
		}
	}
