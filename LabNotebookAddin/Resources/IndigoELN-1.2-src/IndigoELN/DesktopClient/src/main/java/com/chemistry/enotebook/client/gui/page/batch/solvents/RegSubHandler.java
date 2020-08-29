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
 * Created on Oct 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.batch.solvents;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.compoundmanagement.classes.BarcodeValidationVO;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.batch.BatchResidualSolventModel;
import com.chemistry.enotebook.domain.batch.BatchSolubilitySolventModel;
import com.chemistry.enotebook.experiment.common.units.UnitCache;
import com.chemistry.enotebook.experiment.datamodel.batch.*;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;
import com.chemistry.enotebook.experiment.datamodel.compound.Compound;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.registration.RegistrationSvcUnavailableException;
import com.chemistry.enotebook.registration.RegistrationTokenInvalidException;
import com.chemistry.enotebook.registration.ScreenSearchParams;
import com.chemistry.enotebook.registration.delegate.RegistrationDelegateException;
import com.chemistry.enotebook.registration.delegate.RegistrationServiceDelegate;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.session.delegate.SessionTokenDelegate;
import com.chemistry.enotebook.session.security.CompoundManagementEmployee;
import com.chemistry.enotebook.util.ExceptionUtils;
import com.chemistry.enotebook.utils.ExperimentPageUtils;
import com.chemistry.enotebook.utils.sdf.SdUnit;
import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import javax.swing.*;
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
public class RegSubHandler {
	// private CodeTableCache codeTableCache = null;
	private RegistrationServiceDelegate registrationServiceDelegate = null;
	private NotebookPage pageModel = null;
	private String VnVResultString;
	private JFrame mainFrame = null;

	public RegSubHandler() {
		mainFrame = MasterController.getGUIComponent();
		try {
			registrationServiceDelegate = new RegistrationServiceDelegate();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(mainFrame,
					"Exception occured while instantiating registration delegate.", e);
		}
	}

	public RegSubHandler(NotebookPage nbPage) {
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
	public void setPageModel(NotebookPage nbPage) {
		this.pageModel = nbPage;
	}

	/**
	 * build batchList.
	 * 
	 */
	public ArrayList buildBatchList() {
		ArrayList batchList = new ArrayList();
		if (pageModel != null && pageModel.getBatchCache() != null)
			batchList = (ArrayList) pageModel.getBatchCache().getBatchesSorted(BatchType.ACTUAL_PRODUCT);
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
//				RemoteException error = (RemoteException) sf;
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
		} catch (Exception e) {
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
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, "The registration SOAP service is not available.", e);
		}
		return result;
	}

//	public VnVResultVO processVnvResult(String vnvResult) {
//		VnVResultVO vnVResultVO = new VnVResultVO();
//		try {
//			StringReader reader = null;
//			reader = new StringReader(vnvResult);
//			SAXBuilder builder = new SAXBuilder();
//			Document doc;
//			doc = builder.build(reader);
//			Element root = doc.getRootElement();
//			Element molFileDataElement = (Element) XPath.selectSingleNode(root, "/vnvResult/molFileData");
//			vnVResultVO.setResultStructureString(molFileDataElement.getText());
//			Element molFormulaElement = (Element) XPath.selectSingleNode(root, "/vnvResult/molFormula");
//			vnVResultVO.setMolFormula(molFormulaElement.getTextNormalize());
//			Element molWeightElement = (Element) XPath.selectSingleNode(root, "/vnvResult/molWeight");
//			vnVResultVO.setMolWeight(new Double(molWeightElement.getTextNormalize()).doubleValue());
//			List errors = XPath.selectNodes(root, "/vnvResult/errorText");
//			if (errors == null || errors.size() == 0)
//				vnVResultVO.setErrorMessage(VnVResultVO.DEFAULT_MESSAGE);
//			else {
//				String msg = "";
//				for (Iterator it=errors.iterator(); it.hasNext(); ) {
//					Element errorTextElement = (Element)it.next();
//					if (msg.length() > 0) msg = msg + "\n                   ";
//					msg = msg + errorTextElement.getTextNormalize();
//				}
//				if (msg.length() == 0) msg = VnVResultVO.DEFAULT_MESSAGE;
//				vnVResultVO.setErrorMessage(msg);
//			}
//			Element validSICElement = (Element) XPath.selectSingleNode(root, "/vnvResult/validSIC");
//			vnVResultVO.setValidSIC(validSICElement.getTextNormalize());
//			Element suggestedSICElement = (Element) XPath.selectSingleNode(root, "/vnvResult/suggestedSICs");
//			vnVResultVO.setSicList(this.getSuggestedSICs(suggestedSICElement.getTextNormalize()));
//			// Check if there was an error determining a default SIC
//			if (validSICElement.getTextNormalize().startsWith("ERROR")) {
//				if (vnVResultVO.getErrorMessage().equals(VnVResultVO.DEFAULT_MESSAGE)) {
//					String msg = suggestedSICElement.getTextNormalize();
//					int colonPos = msg.indexOf(":");
//					if (colonPos >= 0)
//						msg = msg.substring(0, colonPos);
//					vnVResultVO.setErrorMessage(msg);
//				}
//			}
//			return vnVResultVO;
//		} catch (JDOMException e) {
//			CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, e);
//		} catch (IOException e) {
//			CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, e);
//		}
//		return null;
//	}

	/**
	 * util to process suggestedSICs
	 */
	public ArrayList<String> getSuggestedSICs(String suggestedSICString) {
		ArrayList<String> sicList = new ArrayList<String>();
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

	public String buildSDFIle(ProductBatch selectedBatch, boolean forCompoundRegistrationReg) {
		String result = "";
		if (selectedBatch != null && pageModel != null && selectedBatch.getCompound() != null) {
			Compound selectedCompound = selectedBatch.getCompound();
			boolean submitUsingMGs = false;
			try {
				String prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_COMPOUND_REGISTRATION_SUBMIT_MG);
				if (prop != null && prop.equals("true"))
					submitUsingMGs = true;
			} catch (Exception e) {
			}
			try {
				ChemistryDelegate chemDel = new ChemistryDelegate();
				byte[] molFile = chemDel.convertChemistry(selectedCompound.getNativeSketch(), "", "MDL Molfile");
				SdUnit sDunit = new SdUnit(new String(molFile), true);
				// 11 fields required for all types of SDFiles
				sDunit.setValue("STEREOISOMER_CODE", selectedBatch.getCompound().getStereoisomerCode());
				sDunit.setValue("COMPOUND_REGISTRATION_GLOBAL_STEREOISOMER_CODE", selectedBatch.getCompound().getStereoisomerCode());
				sDunit.setValue("COMPOUND_REGISTRATION_SITE_CODE", pageModel.getSiteCode());
				sDunit.setValue("COMPOUND_REGISTRATION_NOTEBOOK_REFERENCE", selectedBatch.getBatchNumberAsString());
				sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_SOURCE_CODE", selectedBatch.getRegInfo().getCompoundSource());
				sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_SOURCE_DETAIL_CODE", selectedBatch.getRegInfo().getCompoundSourceDetail());
				sDunit.setValue("COMPOUND_REGISTRATION_TA_CODE", pageModel.getTaCode());
				sDunit.setValue("COMPOUND_REGISTRATION_PROJECT_CODE", pageModel.getProjectCode());
				if (submitUsingMGs) {
					sDunit.setValue("COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_VALUE", (new Double(selectedBatch.getWeightAmount()
							.GetValueInStdUnitsAsDouble())).toString());
					sDunit.setValue("COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_UNIT_CODE", "MG");
				} else {
					sDunit.setValue("COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_VALUE", (new Double(selectedBatch.getWeightAmount().getValue()))
							.toString());
					sDunit.setValue("COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_UNIT_CODE", selectedBatch.getWeightAmount().getUnit().getCode());
				}
				// Check if owner is the same as chemist, if so use employeeid
				// of chemist
				if (selectedBatch.getOwner().equals(selectedBatch.getSynthesizedBy()))
					sDunit.setValue("COMPOUND_REGISTRATION_BATCH_OWNER_ID", MasterController.getUser().getPreference(NotebookUser.PREF_EmployeeID));
				else {
					String empId = getOwnerEmpId(selectedBatch.getOwner());
					if (empId != null)
						sDunit.setValue("COMPOUND_REGISTRATION_BATCH_OWNER_ID", empId);
					else {
						JOptionPane.showMessageDialog(mainFrame, "Owner NT UserId not found in Employee Database.");
						return "";
					}
				}
				// 51 fields optional for ALL types of SDFiles (NTVL, NTPL,
				// SOLPL)
				// this comment should be structure comment for compound
				sDunit.setValue("COMPOUND_REGISTRATION_STRUCTURE_COMMENT", handleGTLT(selectedBatch.getCompound().getStructureComments()).replaceAll("\n", " "));
				if (NotebookPageUtil.isValidRegNumber(selectedBatch.getCompound().getRegNumber()))
					sDunit.setValue("COMPOUND_REGISTRATION_GPN_TO_ASSIGN_NEW_BATCH", selectedBatch.getCompound().getRegNumber()); // PF-00000000-00
				// SCT commented out at request of KevinH. fields are not
				// required and will prevent us from having to strip leading 0
				// on page #
				// AJK uncommented as these are required fields for CompoundRegistration
				// Interactive registration.
				sDunit.setValue("COMPOUND_REGISTRATION_NOTEBOOK_NUMBER", NotebookPageUtil.getNotebookNumberFromBatchNumber(selectedBatch
						.getBatchNumberAsString()));
				sDunit.setValue("COMPOUND_REGISTRATION_NOTEBOOK_PAGE_NUMBER", validateNoteBookPageNumber(NotebookPageUtil
						.getNotebookPageFromBatchNumber(selectedBatch.getBatchNumberAsString())));
				sDunit.setValue("COMPOUND_REGISTRATION_NOTEBOOK_SPOT_NUMBER", NotebookPageUtil.getLotNumberFromBatchNumber(selectedBatch
						.getBatchNumberAsString()));
				sDunit.setValue("COMPOUND_REGISTRATION_SUPPLIER_CODE", selectedBatch.getVendorInfo().getCode());
				sDunit.setValue("COMPOUND_REGISTRATION_SUPPLIER_REGISTRY_NUMBER", selectedBatch.getVendorInfo().getSupplierCatalogRef());
				if (!(pageModel.getLiteratureRef() == null || pageModel.getLiteratureRef().trim().length() == 0)) {
					sDunit.setValue("COMPOUND_REGISTRATION_LITERATURE_REFERENCE_CODE", "LIT");
					sDunit.setValue("COMPOUND_REGISTRATION_LITERATURE_REFERENCE_COMMENT", handleGTLT(pageModel.getLiteratureRef()).replaceAll("\n",
							", "));
				}
				if (!selectedBatch.getCompound().getRegNumber().equals("")) {
					sDunit.setValue("COMPOUND_REGISTRATION_GPN_TO_ASSIGN_NEW_BATCH", selectedBatch.getCompound().getRegNumber());
				}
				if (selectedBatch.getSaltEquivs() > 0 && selectedBatch.getSaltEquivsSet()) {
					sDunit.setValue("COMPOUND_REGISTRATION_GLOBAL_SALT_CODE", selectedBatch.getSaltForm().getCode());
					sDunit.setValue("COMPOUND_REGISTRATION_GLOBAL_SALT_MOLE", new Double(selectedBatch.getSaltEquivs()).toString());
				} else if (selectedBatch.getSaltForm().getCode().equals("00")) {
					sDunit.setValue("COMPOUND_REGISTRATION_GLOBAL_SALT_CODE", selectedBatch.getSaltForm().getCode());
				}
				sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_STATE", selectedBatch.getCompoundState());
				// Alias_Batch_Number is an externally supplied batch number -
				// we currently use external Supplier
				// sDunit.setValue("COMPOUND_REGISTRATION_ALIAS_BATCH_NUMBER", );G00000912
				if (selectedBatch.getVendorInfo().getCode() == null || selectedBatch.getVendorInfo().getCode().length() == 0)
					sDunit.setValue("COMPOUND_REGISTRATION_BATCH_CREATOR_ID", MasterController.getUser().getPreference(NotebookUser.PREF_EmployeeID));
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_COMMENT", handleGTLT(selectedBatch.getComments()).replaceAll("\n", " "));
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_MF", selectedBatch.getMolFormula());
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_MW", Double.toString(selectedBatch.getMolWgt()));
				sDunit.setValue("COMPOUND_REGISTRATION_PARENT_MF", selectedBatch.getCompound().getMolFormula());
				if (!forCompoundRegistrationReg) {
					sDunit.setValue("CAL_PARENT_MW", Double.toString(selectedBatch.getCompound().getMolWgt()));
					sDunit.setValue("CAL_PARENT_MF", selectedBatch.getCompound().getMolFormula());
				}
				String preCursors = null;
				if (selectedBatch.getPrecursors() != null && selectedBatch.getPrecursors().size() > 0) {
					// Updated AbstractBatch to ensure no duplicate Pre-Cursors, but let's make sure
					ArrayList<String> pc = new ArrayList<String>();
					ExperimentPageUtils pageUtils = new ExperimentPageUtils();
					for (Iterator<String> it = selectedBatch.getPrecursors().iterator(); it.hasNext();) {
						String monomerKey = (String) it.next();
						MonomerBatchModel monomerBatch = pageUtils.getMonomerBatchInTheExperiment(monomerKey, pageModel.getPageModelPojo());
						pc.add(monomerBatch.getCompound().getRegNumber());
					}
					preCursors = buildTagValue(pc);
					sDunit.setValue("COMPOUND_REGISTRATION_PRECURSOR_COMPOUND", preCursors);
				}
				sDunit.setValue("COMPOUND_REGISTRATION_PROTOCOL_ID", pageModel.getProtocolID());
				// If Hit Id exists enter in as COMPOUND_REGISTRATION_HIT_ID
				// HitID is a means for med chemists to identify a plate and a well from a screening
				// process that they wish to process.
				String hitID = selectedBatch.getRegInfo().getHitId();
				if (hitID != null && hitID.length() > 0)
					sDunit.setValue("COMPOUND_REGISTRATION_HIT_ID", hitID);
				String tVal = selectedBatch.getRegInfo().getIntermediateOrTest();
				if (tVal == null || tVal.length() == 0) tVal = "U";
				sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_IS_INTERMEDIATE", tVal);
//				sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_IS_INTERMEDIATE", "U");
				// sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_IS_INTERMEDIATE", (selectedBatch.isIntermediate()) ? "Y": "N");
				// Reaction Components:
				// monomer series: A + B + C indicates specific order of
				// precursors
				// Literally a letter A, B, C, or D
				// if (preCursors != null) {
				// String rxnComponentLabels = new String();
				// List preCursorList = selectedBatch.getPrecursors();
				// for (int i=0; i < preCursorList.size(); i++) {
				// if (i > 0) rxnComponentLabels += "\n";
				// char data[] = { (char)('A' + i) };
				// rxnComponentLabels += new String(data);
				// }
				//					
				// sDunit.setValue("COMPOUND_REGISTRATION_REACTION_COMPONENT", preCursors);
				// sDunit.setValue("COMPOUND_REGISTRATION_REACTION_COMPONENT_LABEL",
				// rxnComponentLabels);
				// }
				if (selectedBatch.getAnalyticalPurityList() != null && selectedBatch.getAnalyticalPurityList().size() > 0) {
					// Purity method is not used any more - ajk 2004/12/02
					String operatorString = "";
					String valueString = "";
					String codeString = "";
					for (int i = 0; i < selectedBatch.getAnalyticalPurityList().size(); i++) {
						PurityModel pur = (PurityModel) selectedBatch.getAnalyticalPurityList().get(i);
						if (i != 0) {
							// dateString += "\n" +pur.getDate();
							operatorString += "\n" + convertPurityOperatorCode(pur.getOperator());
							valueString += "\n" + pur.getPurityValue();
							// flagString += "\n" +pur.ge;
							codeString += "\n" + pur.getCode();
						} else {
							// dateString += pur.getDate();
							operatorString += convertPurityOperatorCode(pur.getOperator());
							valueString += pur.getPurityValue();
							// flagString += pur.getFlag();
							codeString += pur.getCode();
						}
					}
					// sDunit.setValue("COMPOUND_REGISTRATION_PURIFICATION_METHOD", methodString);
					// TODO: Andy needs to update Purity
					sDunit.setValue("COMPOUND_REGISTRATION_ANALYTICAL_PURITY_CODE", codeString);
					// sDunit.setValue("COMPOUND_REGISTRATION_PURITY_DATE", dateString);
					// TODO: F = failed, P = pass, S = suspect(?)
					// sDunit.setValue("COMPOUND_REGISTRATION_PURITY_FLAG", );
					sDunit.setValue("COMPOUND_REGISTRATION_PURITY_OPERATOR", operatorString);
					sDunit.setValue("COMPOUND_REGISTRATION_PURITY_VALUE", valueString);
					// Do not allow ranges. Make upper value equal to lower
					// value - 2004/12/02 Li Su.
					// sDunit.setValue("COMPOUND_REGISTRATION_PURITY_UPPER_VALUE", valueString);
				}
				if (selectedBatch.getRegInfo().getResidualSolventList() != null
						&& selectedBatch.getRegInfo().getResidualSolventList().size() > 0) {
					String codeString = new String();
					String moleString = new String();
					for (int i = 0; i < selectedBatch.getRegInfo().getResidualSolventList().size(); i++) {
						BatchResidualSolventModel brResidualSolvent = (BatchResidualSolventModel) (selectedBatch
								.getRegInfo().getResidualSolventList().get(i));
						if (i > 0) {
							codeString += "\n" + brResidualSolvent.getCodeAndName();
							moleString += "\n" + Double.toString(brResidualSolvent.getEqOfSolvent());
						} else {
							codeString += brResidualSolvent.getCodeAndName();
							moleString += Double.toString(brResidualSolvent.getEqOfSolvent());
						}
					}
					sDunit.setValue("COMPOUND_REGISTRATION_RESIDUAL_SOLVENT_CODE", codeString);
					sDunit.setValue("COMPOUND_REGISTRATION_RESIDUAL_SOLVENT_MOLE", moleString);
				}
				if (selectedBatch.getRegInfo().getSolubilitySolventList() != null
						&& selectedBatch.getRegInfo().getSolubilitySolventList().size() > 0) {
					String codeString = "";
					String operatorString = "";
					String valueString = "";
					String upperValueString = "";
					String unitString = "";
					String qualitativeString = "";
					for (int i = 0; i < selectedBatch.getRegInfo().getSolubilitySolventList().size(); i++) {
						BatchSolubilitySolventModel brSolubilitySolvent = (BatchSolubilitySolventModel) (selectedBatch
								.getRegInfo().getSolubilitySolventList().get(i));
						if (brSolubilitySolvent.isQualitative()) {
							if (i != 0) {
								codeString += "\n" + brSolubilitySolvent.getCodeAndName();
								qualitativeString += "\n" + brSolubilitySolvent.getQualiString();
								operatorString += "\n" + "N/A";
								valueString += "\n" + "N/A";
								upperValueString += "\n" + "N/A";
								unitString += "\n" + "N/A";
							} else {
								codeString += brSolubilitySolvent.getCodeAndName();
								qualitativeString += brSolubilitySolvent.getQualiString();
								operatorString += "N/A";
								valueString += "N/A";
								upperValueString += "N/A";
								unitString += "N/A";
							}
						} else if (brSolubilitySolvent.isQuantitative()) {
							if (i != 0) {
								codeString += "\n" + brSolubilitySolvent.getCodeAndName();
								qualitativeString += "\n" + "N/A";
								operatorString += "\n" + brSolubilitySolvent.getOperator();
								valueString += "\n" + brSolubilitySolvent.getSolubilityValue();
								upperValueString += "\n"
										+ new Double(new Double(brSolubilitySolvent.getSolubilityValue()).doubleValue() + 1.0).toString();
								unitString += "\n" + brSolubilitySolvent.getSolubilityUnit();
							} else {
								codeString += brSolubilitySolvent.getCodeAndName();
								qualitativeString += "N/A";
								operatorString += brSolubilitySolvent.getOperator();
								valueString += brSolubilitySolvent.getSolubilityValue();
								upperValueString += new Double(new Double(brSolubilitySolvent.getSolubilityValue()).doubleValue() + 1.0).toString();
								unitString += brSolubilitySolvent.getSolubilityUnit();
							}
						}
					}
					sDunit.setValue("COMPOUND_REGISTRATION_SOLUBILITY_SOLVENT_CODE", codeString);
					sDunit.setValue("COMPOUND_REGISTRATION_QUALITATIVE_SOLUBILITY", qualitativeString);
					sDunit.setValue("COMPOUND_REGISTRATION_SOLUBILITY_OPERATOR", operatorString);
					sDunit.setValue("COMPOUND_REGISTRATION_SOLUBILITY_VALUE", valueString);
					sDunit.setValue("COMPOUND_REGISTRATION_SOLUBILITY_UNIT_CODE", unitString);
					sDunit.setValue("COMPOUND_REGISTRATION_SOLUBILITY_UPPER_VALUE", upperValueString);
					// Do not allow ranges. Make upper value equal to lower
					// value - 2004/12/02 Li Su.
					// sDunit.setValue("COMPOUND_REGISTRATION_SOLUBILITY_UPPER_VALUE", valueString);
				}
				sDunit.setValue("COMPOUND_REGISTRATION_HANDLING_CODE", processCodes(selectedBatch.getRegInfo().getCompoundRegistrationHandlingCodes()));
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_HANDLING_COMMENT", handleGTLT(selectedBatch.getHandlingComments()).replaceAll("\n", " "));
				sDunit.setValue("COMPOUND_REGISTRATION_STORAGE_CODE", processCodes(selectedBatch.getRegInfo().getCompoundRegistrationStorageCodes()));
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_STORAGE_COMMENT", handleGTLT(selectedBatch.getStorageComments()).replaceAll("\n", " "));
				sDunit.setValue("COMPOUND_REGISTRATION_HAZARD_CODE", processCodes(selectedBatch.getRegInfo().getCompoundRegistrationHazardCodes()));
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_HAZARD_COMMENT", selectedBatch.getHazardComments());
				sDunit.setValue("COMPOUND_REGISTRATION_PARENT_HAZARD_COMMENT", handleGTLT(selectedBatch.getCompound().getHazardComments()).replaceAll("\n", " "));
				
				// Sample Submission
				// if (selectedBatch.getRegInfo().getSubmissionStatus().equals("Submitting")) {
				// Submission for container info
				UnitCache uc = UnitCache.getInstance();
				if (selectedBatch.getRegInfo().getSubmitContainerList() != null && selectedBatch.getRegInfo().getSubmitContainerList().size() > 0) {
					StringBuffer codeString = new StringBuffer();
					StringBuffer amountValueString = new StringBuffer();
					StringBuffer amountUnitString = new StringBuffer();
					int actualCount = 0;
					for (int i = 0; i < selectedBatch.getRegInfo().getSubmitContainerList().size(); i++) {
						BatchSubmissionContainerInfoModel brBatchSubmissionContainerInfo = selectedBatch.getRegInfo().getSubmitContainerList().get(i);
						if (StringUtils.isNotBlank(brBatchSubmissionContainerInfo.getBarCode()) &&
							!brBatchSubmissionContainerInfo.getSubmissionStatus().equals(BatchSubmissionContainerInfo.SUBMITTED) &&
							!brBatchSubmissionContainerInfo.getBarCode().equals("null")) {
							if (actualCount > 0) {
								codeString.append("\n");
								amountUnitString.append("\n");
								amountValueString.append("\n");
							}
							codeString.append(brBatchSubmissionContainerInfo.getBarCode());
							if (submitUsingMGs) {
								Amount amount = new Amount(brBatchSubmissionContainerInfo.getAmountValue(), uc.getUnit(brBatchSubmissionContainerInfo.getAmountUnit()));
								amountUnitString.append("MG");
								amountValueString.append(amount.GetValueInStdUnitsAsDouble());
							} else {
								amountUnitString.append(brBatchSubmissionContainerInfo.getAmountUnit());
								amountValueString.append(Double.toString(brBatchSubmissionContainerInfo.getAmountValue()));
							}
							actualCount++;
						}
					}
					
					if (actualCount > 0) {
						sDunit.setValue("COMPOUND_REGISTRATION_BAR_CODE", codeString.toString());
						sDunit.setValue("COMPOUND_REGISTRATION_CONTAINER_AMOUNT_UNIT_CODE", amountUnitString.toString());
						sDunit.setValue("COMPOUND_REGISTRATION_CONTAINER_AMOUNT", amountValueString.toString());
					}
				}
				
				// Submission to Biologist for test
				ArrayList<BatchSubmissionToScreenModel> biologyList = selectedBatch.getRegInfo().getSubmitToBiologistTestList();
				if (biologyList != null && biologyList.size() > 0) {
					String scientistCode = "";
					String screenProtocolID = "";
					String screenDefaultAmount = "";
					String unitString = "MG";
					double total = 0.0;
					int byMMcount = 0;
					for (int i = 0; i < biologyList.size(); i++) {
						BatchSubmissionToScreenModel brSubmissionToBiologistTest = biologyList.get(i);
						if (brSubmissionToBiologistTest.isTestSubmittedByMm()) {
							if (!brSubmissionToBiologistTest.getSubmissionStatus().equals(BatchSubmissionToBiologistTest.SUBMITTED)) {
								if (byMMcount > 0) {
									scientistCode += "\n" + brSubmissionToBiologistTest.getScientistCode();
									screenProtocolID += "\n" + brSubmissionToBiologistTest.getScreenProtocolId();
									screenDefaultAmount += "\n" + Double.toString(brSubmissionToBiologistTest.getAmountValue());
								} else {
									scientistCode += brSubmissionToBiologistTest.getScientistCode();
									screenProtocolID += brSubmissionToBiologistTest.getScreenProtocolId();
									screenDefaultAmount += Double.toString(brSubmissionToBiologistTest.getAmountValue());
								}
								++byMMcount;
							}
						} else {
							if (!brSubmissionToBiologistTest.getSubmissionStatus().equals(BatchSubmissionToBiologistTest.SUBMITTED)) {
								Amount amount = new Amount(brSubmissionToBiologistTest.getAmountValue(), uc
										.getUnit(brSubmissionToBiologistTest.getAmountUnit()));
								total += amount.GetValueInStdUnitsAsDouble();
							}
						}
					}
					// sent by MM
					sDunit.setValue("COMPOUND_REGISTRATION_SCREEN_SCIENTIST_CODE", scientistCode);
					sDunit.setValue("COMPOUND_REGISTRATION_SCREEN_PROTOCOL_ID", screenProtocolID);
					sDunit.setValue("COMPOUND_REGISTRATION_SCREEN_DEFAULT_AMOUNT", screenDefaultAmount);
					// sent by self
					sDunit.setValue("COMPOUND_REGISTRATION_SENT_TO_BIOLOGISTS_AMOUNT_UNIT_CODE", unitString);
					sDunit.setValue("COMPOUND_REGISTRATION_SENT_TO_BIOLOGISTS_AMOUNT_VALUE", (new Double(total)).toString());
					// sDunit.setValue("COMPOUND_REGISTRATION_SCREEN_NUMBER", screenNumber);
				}

				if (sDunit.getValue("COMPOUND_REGISTRATION_PROTECTION_CODE") == null || sDunit.getValue("COMPOUND_REGISTRATION_PROTECTION_CODE").length() <= 0)
					sDunit.setValue("COMPOUND_REGISTRATION_PROTECTION_CODE", "NONE");
				else
					sDunit.setValue("COMPOUND_REGISTRATION_PROTECTION_CODE", selectedBatch.getProtectionCode());

				result = sDunit.toString();
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(mainFrame, e);
			}
		}
		return result;
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
   /*
	public ArrayList getRegistrationInformation(String batchNumber) {
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
    */
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
	private String buildTagValue(ArrayList<String> list) {
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

	private String processCodes(List<String> valueList) {
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
	private String getUserNTDomain(String userDomainStr) {
		String[] userDomainStringArray = userDomainStr.split(":");
		return userDomainStringArray[0].toUpperCase();
	}
 */

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

	public String getJobInfoFromMsg(String msg, ArrayList<String> jobErrors) {
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
		} catch (Exception e) {
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
	public boolean statusDirty(ArrayList<ProductBatch> batchList) {
		boolean isDirty = false;
		for (int i = 0; i < batchList.size(); i++) {
			ProductBatch pBatch = (ProductBatch) batchList.get(i);
			if (pBatch.getRegInfo().getStatus().equals(BatchRegistrationInfo.PROCESSING)) {
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

	public static String getBatchHazardString(ProductBatch batch) {
		StringBuffer hazardStr = new StringBuffer();
		HashMap hazardMap = RegCodeMaps.getInstance().getHazardMap();
		List<String> list = batch.getRegInfo().getCompoundRegistrationHazardCodes();
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
		List<String> list = batch.getRegInfo().getCompoundRegistrationHazardCodes();
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


	public static String getBatchHandlingString(ProductBatch batch) {
		StringBuffer handlingStr = new StringBuffer();
		HashMap handlingMap = RegCodeMaps.getInstance().getHandlingMap();
		List<String> list = batch.getRegInfo().getCompoundRegistrationHandlingCodes();
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
		List<String> list = batch.getRegInfo().getCompoundRegistrationHandlingCodes();
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
	
	public static String getBatchStorageString(ProductBatch batch) {
		StringBuffer storageStr = new StringBuffer();
		HashMap storageMap = RegCodeMaps.getInstance().getStorageMap();
		List<String> list = batch.getRegInfo().getCompoundRegistrationStorageCodes();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (i > 0)
					storageStr.append(", ");
				storageStr.append(((String) storageMap.get(list.get(i))).trim());
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
		HashMap storageMap = RegCodeMaps.getInstance().getStorageMap();
		List<String> list = batch.getRegInfo().getCompoundRegistrationStorageCodes();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (i > 0)
					storageStr.append(", ");
				storageStr.append(((String) storageMap.get(list.get(i))).trim());
			}
		}
		if (batch.getStorageComments() != null && batch.getStorageComments().length() > 0) {
			if (storageStr.length() > 0)
				storageStr.append(", ");
			storageStr.append(batch.getStorageComments().trim());
		}
		return storageStr.toString();
	}
}