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

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.client.gui.page.batch.PCeNSummaryViewControllerUtility;
import com.chemistry.enotebook.client.gui.page.batch.events.PlateSelectionChangedEvent;
import com.chemistry.enotebook.client.gui.page.batch.events.PlateSelectionChangedListener;
import com.chemistry.enotebook.client.gui.tablepreferences.TableColumnInfo;
import com.chemistry.enotebook.client.gui.tablepreferences.TablePreferenceDelegate;
import com.chemistry.enotebook.delegate.RegistrationManagerDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.container.CompoundManagementBarcodePrefixInfo;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**  vb 7/19 change is here..............
 * 
 * 1.0
 * 
 */
public class PCeNRegistration_SummaryViewControllerUtility extends PCeNSummaryViewControllerUtility {
	private NotebookPageModel pageModel;
	
	public PCeNRegistration_SummaryViewControllerUtility(NotebookPageModel pageModel) {
		super(pageModel.getAllProductPlatesAndRegPlates(), pageModel);
		this.pageModel = pageModel;
		headerNames.add(PRODUCT_PLATE_STAGE); 
		headerNames.add(CPS_ON_PLATE);
		headerNames.add(CPS_REGISTERED); 
		headerNames.add(CONTAINER_REGISTRATION); 
		headerNames.add(CPS_PURIFIED);
		headerNames.add(PURIFICATION_STATUS);		
		headerNames.add(CPS_SENT_TO_SCREENING);
		headerNames.add(SCREENING_STATUS);		
		headerNames.add(PLATE_COMMENTS); 
		headerNames.add(GENERATE_BARCODE); 
		headerNames.add("Plate Barcode"); 
		headerNames.add("CompoundManagement Container"); 
	}

	public void dispose() {
		super.dispose();
	}

	public String[] getHeaderNames() {
		return (String[])headerNames.toArray(new String[headerNames.size()]);
	}
	
	public Object getValue(int rowIndex, int colIndex) {
		if (productPlates == null)
			return " ";
		ProductPlate plate = (ProductPlate) productPlates.get(rowIndex);
		if (headerNames.get(colIndex) == "View") {//"Select to View"
			return new Boolean(plate.isSelect_reg());
		} 
		else if (headerNames.get(colIndex) ==  PRODUCT_PLATE_STAGE) {//"Product Plate Stage", //7 
			return getPlateStage(plate) + "";
		} 
		else if (headerNames.get(colIndex) == CPS_ON_PLATE) {//"Compounds on Plate", //8
				return plate.getListOfProductBatches().size() + "";
		}  else if (headerNames.get(colIndex) == CPS_REGISTERED) {//"# of Cmpds Registered", //9
			return getNoOfCmpdsRegistered(plate.getListOfProductBatches()) + "";
		} else if (headerNames.get(colIndex) == CONTAINER_REGISTRATION) {//"Container Registered", //10
			if (plate instanceof PseudoProductPlate)
				return "N/A";
			else
				return plate.getCompoundManagementRegistrationSubmissionStatus();
		} else if (headerNames.get(colIndex) == PCeNSummaryViewControllerUtility.CPS_PURIFIED) {//"# of Cmpds Registered", //9
			return getNoOfCmpdsPurified(plate.getListOfProductBatches()) + "";
		} else if (headerNames.get(colIndex) == PURIFICATION_STATUS) {//"Submitted to Purification", //11
			if (plate instanceof PseudoProductPlate)
				return "N/A";
			else
				return plate.getPurificationSubmissionStatus();
		} else if (headerNames.get(colIndex) == CPS_SENT_TO_SCREENING) { 
			return getNoOfCmpdsScreenPanelSubmitted(plate.getListOfProductBatches()) + "";
		} else if (headerNames.get(colIndex) == SCREENING_STATUS) {//"Submitted to Screen Panels", //12
			return  (plate instanceof PseudoProductPlate) ? "N/A" : plate.getScreenPanelsSubmissionStatus();  
		}  else if (headerNames.get(colIndex) == GENERATE_BARCODE) {//"Generate barcode" };//14
			return GENERATE_BARCODE;			
		} else if ("Plate Barcode".equals(headerNames.get(colIndex))) {//Plate barcode
			return plate.getPlateBarCode(); 
		}
		else if ("CompoundManagement Container".equals(headerNames.get(colIndex))) {//CompoundManagement Container
			if (!(plate instanceof PseudoProductPlate) && !plate.getContainer().isUserDefined())
				return  "Yes";
			else
				return "No";
		}
		return super.getValue(rowIndex, colIndex);
	}

	public void setValue(Object value, int rowIndex, int colIndex) {
		ProductPlate plate = (ProductPlate) productPlates.get(rowIndex);
		if (headerNames.get(colIndex) == SCREENING_STATUS) {//"Plate Comments" };//11
			plate.setCompoundManagementRegistrationSubmissionMessage((String) value);
		} 
		 else if (headerNames.get(colIndex) == GENERATE_BARCODE) {//"Plate barcode" };//11
				plate.setPlateBarCode(plate.getPlateBarCode());
		}
		 else
			 super.setValue(value, rowIndex, colIndex);
	}

	public String getToolTip(int rowIndex, int colIndex) {
		if (productPlates == null)
			return "";
		ProductPlate plate = (ProductPlate) productPlates.get(rowIndex);
		String toolTipText = "";
		if (headerNames.get(colIndex) == CONTAINER_REGISTRATION) {//Container Registration
			if (!(plate != null && plate instanceof PseudoProductPlate))
				toolTipText = ((plate.getCompoundManagementRegistrationSubmissionStatus()+"").equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_FAILED) ? plate.getCompoundManagementRegistrationSubmissionMessage() : "");
		} else if (headerNames.get(colIndex) == PURIFICATION_STATUS) {//Submitted to Purification
			if (!(plate != null && plate instanceof PseudoProductPlate))
				toolTipText = ((plate.getPurificationSubmissionStatus()+"").equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_FAILED) ? plate.getPurificationSubmissionMessage() : "");
		} else if (headerNames.get(colIndex) == SCREENING_STATUS) {//Submitted to Screen Panels
			if (!(plate != null && plate instanceof PseudoProductPlate))
				toolTipText = ((plate.getScreenPanelsSubmissionStatus() +"").equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_FAILED) ? plate.getScreenPanelSubmissionMessage() : "");
		}
		else
			toolTipText = getValue(rowIndex, colIndex) + "";
		
		//Screen panels is a number right now. Change it if it has to show the registration. 
/*		if (headerNames[colIndex].equals(headerNames[11])) {//Screen Panels
			if (!(plate instanceof PseudoProductPlate))
				toolTipText = plate.getScreenPanelComments();
		}*/
		return toolTipText; 
	}
	
	private int getNoOfCmpdsScreenPanelSubmitted(List<ProductBatchModel> listOfProductBatches) {
		int cmpdsScreenPanelled = 0;
		for (ProductBatchModel batchModel : listOfProductBatches)
		{
			if (batchModel.getRegInfo().getCompoundAggregationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_PASSED))
				cmpdsScreenPanelled += 1;
		}
		return cmpdsScreenPanelled;
	}

	
	private int getNoOfCmpdsPurified(List<ProductBatchModel> listOfProductBatches) {
		int cmpdsPurified = 0;
		for (ProductBatchModel batchModel : listOfProductBatches)
		{
			if (batchModel.getRegInfo().getPurificationServiceStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_PASSED))
				cmpdsPurified += 1;
		}
		return cmpdsPurified;
	}
	
	private String getPlateStage(ProductPlate plate2) {
		if (plate2 instanceof PseudoProductPlate)
			return "";
		List<ReactionStepModel> list = pageModel.getReactionSteps();
		ReactionStepModel lastReactionStepModel = (ReactionStepModel) list.get(list.size() - 1); 
		if (lastReactionStepModel.getProductPlates().contains(plate2) || plate2.getPlateType().equals(CeNConstants.PLATE_TYPE_REGISTRATION))
			return "Product";
		else
			return "Intermediate";
	}

	private int getNoOfCmpdsRegistered(List<ProductBatchModel> listOfProductBatches) {
		int cmpdsRegistered = 0;
		for (ProductBatchModel batchModel : listOfProductBatches)
		{
			if (batchModel.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_PASSED))
				cmpdsRegistered += 1;
		}
		return cmpdsRegistered;
	}

	public boolean isCellEditable(int rowIndex, int colIndex) {
		ProductPlate plate = (ProductPlate) productPlates.get(rowIndex);
		if (!plate.isEditable())
			return false;
		if (headerNames.get(colIndex)== GENERATE_BARCODE 
			|| headerNames.get(colIndex)== "Plate Barcode")
		{
			if (plate instanceof PseudoProductPlate)
				return false;
			else
				return true;
		}	
		else if ("View".equals(headerNames.get(colIndex))
				|| PRODUCT_PLATE_STAGE.equals(headerNames.get(colIndex))
				|| CPS_ON_PLATE.equals(headerNames.get(colIndex))
				|| CPS_REGISTERED.equals(headerNames.get(colIndex))
				|| CONTAINER_REGISTRATION.equals(headerNames.get(colIndex))
				|| CPS_PURIFIED.equals(headerNames.get(colIndex))
				|| PURIFICATION_STATUS.equals(headerNames.get(colIndex))
				|| CPS_SENT_TO_SCREENING.equals(headerNames.get(colIndex))
				|| SCREENING_STATUS.equals(headerNames.get(colIndex))
				|| "CompoundManagement Container".equals(headerNames.get(colIndex))
				)
		{
			return false;
		}
		else
		{
			String columnName = (String) headerNames.get(colIndex);
			return super.isCellEditable(columnName);
		}
	}

	List<ProductPlate> getSelectedPlates() {
		ArrayList<ProductPlate> selectedPlates = new ArrayList<ProductPlate>();
		for (ProductPlate plate : productPlates) {
			if (plate.isSelect_reg()) {
				selectedPlates.add(plate);
			}
		}
		return selectedPlates;
	}

	public void setSelectValue(int rowIndex) {
		ProductPlate plate = productPlates.get(rowIndex);
		plate.setSelect_reg(!plate.isSelect_reg());
		
		List<ProductPlate> selectedPlates = getSelectedPlates();
		Collections.sort(selectedPlates);
		PlateSelectionChangedEvent mPlateSelectionChangedEvent = new PlateSelectionChangedEvent(this, plate, selectedPlates);

		for (PlateSelectionChangedListener plateSelectionChangedListener : plateSelectionChangedListeners) {
			if (plateSelectionChangedListener != null) {
				plateSelectionChangedListener.plateSelectionChanged(mPlateSelectionChangedEvent);
			}
		}
	}

	public void getNewGBLPlateBarCodesFromCompoundManagement(ArrayList<ProductPlate> productPlatesList) {
		if (productPlatesList == null)
			productPlatesList = getOnlyProductPlates();
		if (productPlatesList.size() < 1)
		{
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
			                              "No Plates are selected/Created to get Plate BarCode!");
			return;
		}
		CompoundManagementBarcodePrefixInfo selectedBarcodePrefixInfo = getPlateBarcodePrefix(productPlatesList);
		if (selectedBarcodePrefixInfo == null)
			return;
		String[] barCodes = null;
		try {
			NotebookUser user = MasterController.getUser();
			RegistrationManagerDelegate regManager = ServiceController.getRegistrationManagerDelegate(user.getSessionIdentifier(),
			                                                                                          user.getCompoundManagementEmployeeId());
			//barCodes = regManager.getNewGBLPlateBarCodesFromCompoundManagement("GALT", productPlatesList.size(), "SITE1");
			barCodes = regManager.getNewGBLPlateBarCodesFromCompoundManagement(selectedBarcodePrefixInfo.getPrefix(), productPlatesList.size(), pageModel.getSiteCode());
		} catch (Exception e) {
			log.error("Failed to get New GBL plate bar codes from Compound Management!", e);
			//JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Unable to get the Plate BarCode. Please try again later.");
			//return;
		}
		if (barCodes == null)
		{
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Unable to get the Plate BarCode. Please try again later.");
			return;
		}
		
		//For Testing..... 
/*		barCodes = new String[2];
		barCodes[0] = "123456789012";
		barCodes[1] = "abcdefghijkl";	*/
		
		for (int i=0; i < productPlatesList.size(); i++)
		{
			Object obj = productPlatesList.get(i);
			((ProductPlate)obj).setPlateBarCode(barCodes[i]);
			((ProductPlate)obj).setModelChanged(true);
		}
		pageModel.setModelChanged(true);
	}

	public CompoundManagementBarcodePrefixInfo getPlateBarcodePrefix(ArrayList<ProductPlate> productPlatesList) {
		ArrayList<CompoundManagementBarcodePrefixInfo> barcodePrefixInfoAll =  null;
		try {
			NotebookUser user = MasterController.getUser();
			RegistrationManagerDelegate regManager = ServiceController.getRegistrationManagerDelegate(user.getSessionIdentifier(), 
			                                                                                          user.getCompoundManagementEmployeeId());
			barcodePrefixInfoAll = new ArrayList<CompoundManagementBarcodePrefixInfo>(Arrays.asList(regManager.getCompoundManagementBarcodePrefixes("PLATE", pageModel.getSiteCode())));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Unable to get the Plate BarCode Prefix!\n", "Compound Management Barcode service error", JOptionPane.ERROR_MESSAGE);
			CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "submitBatchesForRegistration unexpected exception");
			log.error("Unable to get the Plate BarCode Prefix!", e);
			return null;
		}
		if (barcodePrefixInfoAll == null)
		{
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Unable to get the Plate BarCode Prefix!\n");
			return null;
		}
		
		CompoundManagementBarcodePrefixInfo selectedBarcodePrefixInfo = null;
		ArrayList<CompoundManagementBarcodePrefixInfo> barcodePrefixInfo = filterBarCodePfrefixesBySiteandType(pageModel.getSiteCode(),"PLATE",barcodePrefixInfoAll);
		if(barcodePrefixInfo == null && barcodePrefixInfoAll != null)
		{
			barcodePrefixInfo = barcodePrefixInfoAll;
			log.debug("No barcode prefixes found for site:"+pageModel.getSiteCode()+ " Enabling all available prefixes.");
		}
		Collections.sort(barcodePrefixInfo);
		
		//For Testing:
/*		barcodePrefixInfo = new CompoundManagementBarcodePrefixInfo[2];
		CompoundManagementBarcodePrefixInfo test1 = new CompoundManagementBarcodePrefixInfo();
		CompoundManagementBarcodePrefixInfo test2 = new CompoundManagementBarcodePrefixInfo();
		test1.setPrefix("Prfix1");
		test2.setPrefix("Prfix2");
		barcodePrefixInfo[0]=test1;
		barcodePrefixInfo[1]=test2;
*/		
		if (barcodePrefixInfo != null && barcodePrefixInfo.size() > 1)
		{
			CeNComboBox customContainerMajorAxisCombo = new CeNComboBox(barcodePrefixInfo);
			Object[] message = new Object[2];
			int index = 0;
			message[index++] = "Pick any one Plate Bar Code Prefix: ";
			message[index++] = customContainerMajorAxisCombo;
			String[] options = { "Select", "Cancel" };
			int result = JOptionPane.showOptionDialog(MasterController.getGUIComponent(), // the parent that the dialog blocks
					message, // the dialog message array
					"Select the Barcode Prefix", // the title of the dialog window
					JOptionPane.DEFAULT_OPTION, // option type
					JOptionPane.INFORMATION_MESSAGE, // message type
					null, // optional icon, use null to use the default icon
					options, // options string array, will be made into buttons
					options[1] // option that should be made into a default button
					);
			switch (result) {
				case 0: // add result
					selectedBarcodePrefixInfo = (CompoundManagementBarcodePrefixInfo)customContainerMajorAxisCombo.getSelectedItem();
					break;
				case 1: // cancel
					return null;
			}
		}
		else if (barcodePrefixInfo.size() > 0) {
			selectedBarcodePrefixInfo = barcodePrefixInfo.get(0);
			return selectedBarcodePrefixInfo;
		} 
		return selectedBarcodePrefixInfo;
	}

	private ArrayList<ProductPlate> getOnlyProductPlates() {
		ArrayList<ProductPlate> productPlatesWithoutPseudoPlate = new ArrayList<ProductPlate>();
		for (ProductPlate plate : productPlates)
		{
			if (! (plate instanceof PseudoProductPlate))
				productPlatesWithoutPseudoPlate.add(plate);
		}
		return productPlatesWithoutPseudoPlate;
	}

	public boolean isColumnEditable(String columnName) {
		if (columnName.equals(PLATE_COMMENTS)  || columnName.equals("Plate Barcode")) {//only getRegComments and Plate Barcode
			return true;
		} else
			return false;
	}

	public TableColumnInfo getColumnInfoFromModelIndex(int modelIndex) {
		return null;
	}

	public TablePreferenceDelegate getTablePreferenceDelegate() {
		return null;
	}
	
	
	private ArrayList<CompoundManagementBarcodePrefixInfo> filterBarCodePfrefixesBySiteandType(String site, 
	                                                                            String type, 
	                                                                            List<CompoundManagementBarcodePrefixInfo> barcodePrefixInfo)
	{
		ArrayList<CompoundManagementBarcodePrefixInfo> list = new ArrayList<CompoundManagementBarcodePrefixInfo>();
		for (CompoundManagementBarcodePrefixInfo prefix : barcodePrefixInfo)
		{
			if(prefix.getSiteCode().compareToIgnoreCase(site) == 0 && prefix.getType().compareToIgnoreCase(type) == 0)
			{
				list.add(prefix);
			}
		}
		
		return list;
	}
	
	public StoicModelInterface getProductBatchModel(int selectedRowIndex) {
		return null;
   }
	
	
}
