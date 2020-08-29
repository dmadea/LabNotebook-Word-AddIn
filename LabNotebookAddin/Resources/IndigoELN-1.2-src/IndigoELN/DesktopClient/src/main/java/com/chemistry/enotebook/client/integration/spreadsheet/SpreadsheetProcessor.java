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
package com.chemistry.enotebook.client.integration.spreadsheet;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.integration.compoundmanagementorderingapi.CompoundManagementOrderingHandler;
import com.chemistry.enotebook.domain.AmountModel;
import com.chemistry.enotebook.domain.MonomerBatchModel;
import com.chemistry.enotebook.domain.SaltFormModel;
import com.chemistry.enotebook.experiment.common.units.UnitCache2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SpreadsheetProcessor {

	public static final String SEQUENCE = "Sequence";
	public static final String ORDER_NUMBER = "OrderNumber";
	public static final String VIAL_BARCODE = "VialBarcode";
	public static final String DESTINATION_WELL = "DestinationWell";
	public static final String COMPOUND_NUMBER = "CompoundNumber";
	public static final String MOLFORMULA = "MOLFORM"; // MOLFORM
	public static final String PARENT_WEIGHT = "ParentWght"; // ParentWght
	public static final String SALT_WEIGHT = "SaltWght"; // SaltWght
	public static final String SALT_TYPE = "SaltType"; // SaltType
	public static final String DELIVERED_MOLES = "Weight(uMol)"; // Weight(uMol)
	public static final String CONCENTRATION = "Conc (M)"; // Conc (M)
	public static final String TOTAL_VOLUME_DELIVERED = "Volume(ul)"; // Volume(ul)
	public static final String DELIVERED_WEIGHT = "Weight(mg)"; // Weight(mg)
	public static final String WEIGHT = "actual(g)"; // actual(g)

	private static final String[] headerNames = { SEQUENCE, ORDER_NUMBER,
			VIAL_BARCODE, DESTINATION_WELL, COMPOUND_NUMBER, MOLFORMULA,
			PARENT_WEIGHT, SALT_WEIGHT, SALT_TYPE, DELIVERED_MOLES,
			CONCENTRATION, TOTAL_VOLUME_DELIVERED, DELIVERED_WEIGHT, WEIGHT };
	private static final ArrayList<String> headerNamesList = new ArrayList<String>(Arrays.asList(headerNames));

	public static ArrayList<MonomerBatchModel> loadMonomersFromFile(String fileName) throws Exception {
		ArrayList<MonomerBatchModel> resultList = new ArrayList<MonomerBatchModel>();
		File file = new File(fileName);
		if (!file.exists()) {
			throw new FileNotFoundException("File not found!");
		}
		String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
		Workbook workbook = null;
		if (ext.equals("xls")) {
			workbook = new HSSFWorkbook(new FileInputStream(file));
		} else if (ext.equals("xlsx")) {
			workbook = new XSSFWorkbook(new FileInputStream(file));
		}
		Sheet sheet = workbook.getSheetAt(0);
		Row headerRow = sheet.getRow(0); // Header row
		for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); ++i) {
			Cell currentCell = headerRow.getCell(i);
			String value = currentCell.getStringCellValue();
			if (!value.equals(headerNames[i])) {
				throw new IOException("Incorrect file format!");
			}
		}
		ArrayList<String[]> notFilledWells = new ArrayList<String[]>();
		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); ++i) {
			Row currentRow = sheet.getRow(i);
			String[] readedRow = readRow(workbook, currentRow);
			MonomerBatchModel currentModel = null;
			String vialBarcode = getValue(readedRow, VIAL_BARCODE);
			String compoundNumber = getValue(readedRow, COMPOUND_NUMBER);
			if (compoundNumber.equals("")) {
				if (vialBarcode.equals("")) {
					continue;
				}
				currentModel = loadModelFromCompoundManagement(vialBarcode);
			}
			if (currentModel == null) {
				currentModel = new MonomerBatchModel(BatchType.REACTANT);
			}
			String orderNumber = getValue(readedRow, ORDER_NUMBER);
			String destinationWell = getValue(readedRow, DESTINATION_WELL);
			if (destinationWell.equals("")) {
				notFilledWells.add(readedRow);
			}
			String molFormula = getValue(readedRow, MOLFORMULA);
			String parentWeight = getValue(readedRow, PARENT_WEIGHT);
			String saltWeight = getValue(readedRow, SALT_WEIGHT);
			String saltType = getValue(readedRow, SALT_TYPE);
			String deliveredMoles = getValue(readedRow, DELIVERED_MOLES);
			String concentration = getValue(readedRow, CONCENTRATION);
			String totalVolumeDelivered = getValue(readedRow, TOTAL_VOLUME_DELIVERED);
			String deliveredWeight = getValue(readedRow, DELIVERED_WEIGHT);
			String weight = getValue(readedRow, WEIGHT);
			currentModel.setBarCode(vialBarcode);
			currentModel.setOrderId(orderNumber);
			currentModel.setMonomerId(compoundNumber);
			if (!molFormula.equals("")) {
				currentModel.setMolecularFormula(molFormula);
			}
			if (!parentWeight.equals("")) {
				AmountModel amt = new AmountModel(UnitType.SCALAR, Double.parseDouble(parentWeight));
				BatchAttributeComponentUtility.setParentMolWeight(currentModel,	amt);
			}
			if (!saltWeight.equals(""))
				currentModel.setSaltEquivs(Double.parseDouble(saltWeight));
			if (!saltType.equals(""))
				currentModel.setSaltForm(new SaltFormModel(saltType));
			if (!deliveredMoles.equals("")) {
				AmountModel amt = new AmountModel(UnitCache2.getInstance().getUnit("umol").getType(), Double.parseDouble(deliveredMoles));
				BatchAttributeComponentUtility.setRxNMoles(currentModel, amt);
			}
			if (!concentration.equals("")) {
				AmountModel amt = new AmountModel(UnitCache2.getInstance().getUnit("M").getType(), Double.parseDouble(concentration));
				BatchAttributeComponentUtility.setMolarity(currentModel, amt);
			}
			if (!totalVolumeDelivered.equals("")) { // Calculate from Conc (M), Weight(mg) and actual(g)
				AmountModel amt = new AmountModel(UnitCache2.getInstance().getUnit("uL").getType(), Double.parseDouble(totalVolumeDelivered));
				currentModel.setTotalVolumeDelivered(amt);
			} else {
				if (!concentration.equals("") && !weight.equals("")) {
					if (deliveredWeight.equals("")) {
						if (!parentWeight.equals("") && !deliveredMoles.equals("")) {
							double parWgt = Double.parseDouble(parentWeight);
							double delivMol = Double.parseDouble(deliveredMoles);
							double delivWgt = parWgt * delivMol / 1000;
							deliveredWeight = "" + delivWgt;
						}
					}
					double conc = Double.parseDouble(concentration);
					double delivWgt = Double.parseDouble(deliveredWeight);
					double wgt = Double.parseDouble(weight);
					if (conc != 0 && delivWgt != 0) {
						double totalVolDeliv = wgt / delivWgt / conc * 1000000;
						AmountModel amt = new AmountModel(UnitCache2.getInstance().getUnit("uL").getType(), totalVolDeliv);
						currentModel.setTotalVolumeDelivered(amt);
					}
				}
			}
			if (!deliveredWeight.equals("")) { // Calculate from ParentWght and Weight(uMol)
				AmountModel amt = new AmountModel(UnitCache2.getInstance().getUnit("mg").getType(), Double.parseDouble(deliveredWeight));
				currentModel.setDeliveredWeight(amt);
			} else {
				if (!parentWeight.equals("") && !deliveredMoles.equals("")) {
					double parWgt = Double.parseDouble(parentWeight);
					double delivMol = Double.parseDouble(deliveredMoles);
					double delivWgt = parWgt * delivMol / 1000;
					AmountModel amt = new AmountModel(UnitCache2.getInstance().getUnit("mg").getType(), delivWgt);
					currentModel.setDeliveredWeight(amt);
				}
			}
			resultList.add(currentModel);
		}
		if (notFilledWells.size() > 0) {
			showNotFilledWellsMessage(notFilledWells);
			resultList = null;
		}
		return resultList;
	}

	private static void showNotFilledWellsMessage(ArrayList<String[]> notFilledWells) {
		JTable table = new JTable(notFilledWells.toArray(new String[notFilledWells.size()][]), headerNames);
		for (int i = headerNamesList.indexOf(COMPOUND_NUMBER) + 1; i < headerNamesList.size(); ++i) {
			TableColumn column = table.getColumn(headerNamesList.get(i));
			table.removeColumn(column);
		}
		JScrollPane pane = new JScrollPane(table);
		JLabel topLabel = new JLabel("Destination Wells are missing for the following rows:");
		JLabel bottomLabel = new JLabel("Please enter these and re-import.");
		Object[] message = new Object[] { topLabel, pane, bottomLabel };
		JOptionPane.showMessageDialog(MasterController.getGUIComponent(), message, "Load monomers from Excel", JOptionPane.ERROR_MESSAGE);
	}

	private static String getValue(String[] array, String key) {
		String value = "";
		int position = headerNamesList.indexOf(key);
		if (position > -1 && position < array.length) {
			value = array[position];
		}
		return value;
	}

	private static String[] readRow(Workbook workbook, Row row) {
		String[] readedRow = new String[headerNames.length];
		for (int i = 0; i < headerNames.length; ++i) {
			String value = "";
			Cell cell = row.getCell(i);
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			cell = evaluator.evaluateInCell(cell);
			if (cell != null) {
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_BLANK:
					value = "";
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					value = "" + cell.getBooleanCellValue();
					break;
				case Cell.CELL_TYPE_ERROR:
					value = "" + cell.getErrorCellValue();
					break;
				case Cell.CELL_TYPE_FORMULA:
					value = "" + cell.getCellFormula();
					break;
				case Cell.CELL_TYPE_NUMERIC:
					value = "" + cell.getNumericCellValue();
					break;
				case Cell.CELL_TYPE_STRING:
					value = "" + cell.getStringCellValue();
					break;
				default:
					value = "";
					break;
				}
			}
			readedRow[i] = value;
		}
		return readedRow;
	}

	public static MonomerBatchModel loadModelFromCompoundManagement(String vialBarcode) throws Exception {
		CompoundManagementOrderingHandler handler = new CompoundManagementOrderingHandler();
		MonomerBatchModel model = handler.findModelByBarcode(vialBarcode);
		return model;
	}
}
