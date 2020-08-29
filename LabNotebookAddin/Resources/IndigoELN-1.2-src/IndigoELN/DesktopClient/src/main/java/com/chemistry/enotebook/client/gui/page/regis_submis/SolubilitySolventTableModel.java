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
 * Created on Nov 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.regis_submis;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.RegOperatorCache;
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.RegQualitativeCache;
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.RegUnitCache;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchRegistrationSolubilitySolvent;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class SolubilitySolventTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9189815577931206515L;
	private static String CODE_AND_NAME = "Solubility Solvent Code & Name";
	private static String SOLUBILITY_VALUE = "Solubility Value";
	private static String COMMENTS = "Solubility Comments";
	private int solubilitySolventCount = 0;
	private ArrayList codeAndNameList = new ArrayList();
	private ArrayList valueList = new ArrayList();
	private ArrayList commentsList = new ArrayList();
	private ArrayList solubilitySolventList = new ArrayList();
	private ArrayList unitList = new ArrayList();
	private ArrayList operatorList = new ArrayList();
	private ArrayList solventqualitativeList = new ArrayList();

	public int getRowCount() {
		return solubilitySolventCount;
	}

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int column) {
		switch (column) {
			case 0:
				return SolubilitySolventTableModel.CODE_AND_NAME;
			case 1:
				return SolubilitySolventTableModel.SOLUBILITY_VALUE;
			case 2:
				return SolubilitySolventTableModel.COMMENTS;
		}
		return "";
	}

	public boolean isCellEditable(int nRow, int nCol) {
		switch (nCol) {
			case 0:
				return true;
			case 1:
				return false;
			case 2:
				return true;
		}
		return false;
	}

	public Object getValueAt(int nRow, int nCol) {
		if (nRow < 0 || nRow >= getRowCount())
			return "";
		switch (nCol) {
			case 0:
				return getCodeAndNameList().get(nRow);
			case 1:
				return getValueList().get(nRow);
			case 2:
				return getCommentsList().get(nRow);
		}
		return "";
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				getCodeAndNameList().set(rowIndex, (String) aValue);
				break;
			case 1:
				getValueList().set(rowIndex, aValue);
				break;
			case 2:
				getCommentsList().set(rowIndex, aValue);
				break;
		}
	}

	public void resetModelData() {
		try {
			for (int i = 0; i < getSolubilitySolventList().size(); i++) {
				BatchRegistrationSolubilitySolvent solubilitySolventVO = (BatchRegistrationSolubilitySolvent) getSolubilitySolventList()
						.get(i);
				getCodeAndNameList().add(getSolubilityDecrFromCode(solubilitySolventVO.getCodeAndName()));
				// getValueList().add(getOperatorDesrFromCode(solubilitySolventVO.getOperator())
				// + " " + solubilitySolventVO.getSolubilityValue() + " " +
				// getUnitDesrFromCode(solubilitySolventVO.getSolubilityUnit()));
				getCommentsList().add(solubilitySolventVO.getComments());
				// if( solubilitySolventVO.isQualitative()){
				// getValueList().add(getQualitativeDesrFromCode(solubilitySolventVO.getQualiString()));
				// }else{
				// getValueList().add(getOperatorDesrFromCode(solubilitySolventVO.getOperator())
				// + " " + solubilitySolventVO.getSolubilityValue() + " " +
				// getUnitDesrFromCode(solubilitySolventVO.getSolubilityUnit()));
				// //String valueString =
				// getOperatorDesrFromCode(solubilitySolventVO.getOperator()) +
				// " " + solubilitySolventVO.getSolubilityValue() + " " +
				// getUnitDesrFromCode(solubilitySolventVO.getSolubilityUnit());
				// }
				if (!solubilitySolventVO.getQualiString().equals("")) {
					getValueList().add(getQualitativeDesrFromCode(solubilitySolventVO.getQualiString()));
				} else {
					getValueList().add(
							getOperatorDesrFromCode(solubilitySolventVO.getOperator()) + " "
									+ solubilitySolventVO.getSolubilityValue() + " "
									+ getUnitDesrFromCode(solubilitySolventVO.getSolubilityUnit()));
					// String valueString =
					// getOperatorDesrFromCode(solubilitySolventVO.getOperator())
					// + " " + solubilitySolventVO.getSolubilityValue() + "
					// " +
					// getUnitDesrFromCode(solubilitySolventVO.getSolubilityUnit());
				}
			}
			setsolubilitySolventCount(getCodeAndNameList().size());
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public String getUnitDesrFromCode(String code) {
		String desr = new String();
		RegUnitCache regUnitCache = new RegUnitCache();
		for (int i = 0; i < getUnitList().size(); i++) {
			regUnitCache = (RegUnitCache) getUnitList().get(i);
			if (code.equals(regUnitCache.getCode().trim())) {
				desr = regUnitCache.getDescription().trim();
				break;
			}
		}
		return desr;
	}

	public String getOperatorDesrFromCode(String code) {
		String desr = new String();
		RegOperatorCache regOperatorCache = new RegOperatorCache();
		for (int i = 0; i < getOperatorList().size(); i++) {
			regOperatorCache = (RegOperatorCache) getOperatorList().get(i);
			if (code.equals(regOperatorCache.getCode().trim())) {
				desr = regOperatorCache.getDescription().trim();
				break;
			}
		}
		return desr;
	}

	public String getQualitativeDesrFromCode(String code) {
		String desr = new String();
		RegQualitativeCache regQualitativeCache = new RegQualitativeCache();
		for (int i = 0; i < getSolventqualitativeList().size(); i++) {
			regQualitativeCache = (RegQualitativeCache) getSolventqualitativeList().get(i);
			if (code.equals(regQualitativeCache.getCode().trim())) {
				desr = regQualitativeCache.getDescription().trim();
				break;
			}
		}
		return desr;
	}

	public void addModelData() {
		try {
			// testing
			getCodeAndNameList().add("");
			getValueList().add("");
			getCommentsList().add("");
			setsolubilitySolventCount(getCodeAndNameList().size());
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void cutModelData(int rowIndex) {
		try {
			// testing
			getCodeAndNameList().remove(rowIndex);
			getValueList().remove(rowIndex);
			getCommentsList().remove(rowIndex);
			setsolubilitySolventCount(getCodeAndNameList().size());
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void clearModelData() {
		try {
			// testing
			getCodeAndNameList().clear();
			getValueList().clear();
			getCommentsList().clear();
			setsolubilitySolventCount(getCodeAndNameList().size());
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void setSolubilityValue(int rowIndex, Object value) {
		try {
			getValueList().set(rowIndex, value);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	// private String getSolubilityCodeFromDecr(String decr){
	//		
	// String codeString = "";
	// try {
	// codeString =
	// CodeTableCache.getCache().getSolubilitySolventCode(decr);
	// } catch (CodeTableCacheException e) {
	// CeNErrorHandler.getInstance().logExceptionMsg(null,"Look up code
	// failed.", e);
	// }
	// return codeString;
	// }
	private String getSolubilityDecrFromCode(String code) {
		String decrString = "";
		try {
			decrString = CodeTableCache.getCache().getSolubilitySolventDescription(code);
		} catch (CodeTableCacheException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, "Look up description failed.", e);
		}
		return decrString;
	}

	/**
	 * @return Returns the codeAndNameList.
	 */
	public ArrayList getCodeAndNameList() {
		return codeAndNameList;
	}

	/**
	 * @param codeAndNameList
	 *            The codeAndNameList to set.
	 */
	public void setCodeAndNameList(ArrayList codeAndNameList) {
		this.codeAndNameList = codeAndNameList;
	}

	/**
	 * @return Returns the commentsList.
	 */
	public ArrayList getCommentsList() {
		return commentsList;
	}

	/**
	 * @param commentsList
	 *            The commentsList to set.
	 */
	public void setCommentsList(ArrayList commentsList) {
		this.commentsList = commentsList;
	}

	/**
	 * @return Returns the eqList.
	 */
	public ArrayList getValueList() {
		return valueList;
	}

	/**
	 * @param eqList
	 *            The eqList to set.
	 */
	public void setValueList(ArrayList eqList) {
		this.valueList = eqList;
	}

	/**
	 * @return Returns the solubilitySolventCount.
	 */
	public int getsolubilitySolventCount() {
		return solubilitySolventCount;
	}

	/**
	 * @param solubilitySolventCount
	 *            The solubilitySolventCount to set.
	 */
	public void setsolubilitySolventCount(int solubilitySolventCount) {
		this.solubilitySolventCount = solubilitySolventCount;
	}

	/**
	 * @return Returns the solubilitySolventList.
	 */
	public ArrayList getSolubilitySolventList() {
		return solubilitySolventList;
	}

	/**
	 * @param solubilitySolventList
	 *            The solubilitySolventList to set.
	 */
	public void setSolubilitySolventList(ArrayList solubilitySolventList) {
		this.solubilitySolventList = solubilitySolventList;
	}

	/**
	 * @return Returns the operatorList.
	 */
	public ArrayList getOperatorList() {
		return operatorList;
	}

	/**
	 * @param operatorList
	 *            The operatorList to set.
	 */
	public void setOperatorList(ArrayList operatorList) {
		this.operatorList = operatorList;
	}

	/**
	 * @return Returns the solventqualitativeList.
	 */
	public ArrayList getSolventqualitativeList() {
		return solventqualitativeList;
	}

	/**
	 * @param solventqualitativeList
	 *            The solventqualitativeList to set.
	 */
	public void setSolventqualitativeList(ArrayList solventqualitativeList) {
		this.solventqualitativeList = solventqualitativeList;
	}

	/**
	 * @return Returns the unitList.
	 */
	public ArrayList getUnitList() {
		return unitList;
	}

	/**
	 * @param unitList
	 *            The unitList to set.
	 */
	public void setUnitList(ArrayList unitList) {
		this.unitList = unitList;
	}
}