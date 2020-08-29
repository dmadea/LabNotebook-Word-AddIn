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
 * Created on Nov 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.batch.solvents;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.domain.batch.BatchResidualSolventModel;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ResidualSolventTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9177144296422999349L;
	private static String CODE_AND_NAME = "Residual Solvent Code & Name";
	private static String NUMBER_EQ_OF_SOLVENT = "# EQ. of Solvent";
	private static String COMMENTS = "Residual Solvent Comments";
	private int residualSolventCount = 0;
	private ArrayList codeAndNameList = new ArrayList();
	private ArrayList eqList = new ArrayList();
	private ArrayList commentsList = new ArrayList();
	private ArrayList residualSolventVOList = new ArrayList();

	public int getRowCount() {
		return residualSolventCount;
	}

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int column) {
		switch (column) {
			case 0:
				return ResidualSolventTableModel.CODE_AND_NAME;
			case 1:
				return ResidualSolventTableModel.NUMBER_EQ_OF_SOLVENT;
			case 2:
				return ResidualSolventTableModel.COMMENTS;
		}
		return "";
	}

	public boolean isCellEditable(int nRow, int nCol) {
		switch (nCol) {
			case 0:
				return true;
			case 1:
				return true;
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
				return this.getCodeAndNameList().get(nRow);
			case 1:
				return this.getEqList().get(nRow);
			case 2:
				return this.getCommentsList().get(nRow);
		}
		return "";
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				this.getCodeAndNameList().set(rowIndex, (String) aValue);
				break;
			case 1:
				this.getEqList().set(rowIndex, aValue);
				break;
			case 2:
				this.getCommentsList().set(rowIndex, aValue);
				break;
		}
	}

	public void resetModelData() {
		try {
			for (int i = 0; i < this.getResidualSolventVOList().size(); i++) {
				BatchResidualSolventModel residualSolventVO = (BatchResidualSolventModel) this
						.getResidualSolventVOList().get(i);
				this.getCodeAndNameList().add(getResidualDecrFromCode(residualSolventVO.getCodeAndName()));
				this.getEqList().add((new Double(residualSolventVO.getEqOfSolvent())).toString());
				this.getCommentsList().add(residualSolventVO.getComments());
			}
			this.setResidualSolventCount(this.getCodeAndNameList().size());
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void addModelData() {
		try {
			// testing
			this.getCodeAndNameList().add("");
			this.getEqList().add("");
			this.getCommentsList().add("");
			this.setResidualSolventCount(this.getCodeAndNameList().size());
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void cutModelData(int rowIndex) {
		try {
			// testing
			this.getCodeAndNameList().remove(rowIndex);
			this.getEqList().remove(rowIndex);
			this.getCommentsList().remove(rowIndex);
			this.setResidualSolventCount(this.getCodeAndNameList().size());
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void clearModelData() {
		try {
			// testing
			this.getCodeAndNameList().clear();
			this.getEqList().clear();
			this.getCommentsList().clear();
			this.setResidualSolventCount(this.getCodeAndNameList().size());
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	// private String getResidualCodeFromDecr(String decr){
	//		
	// String codeString = "";
	// try {
	// codeString = CodeTableCache.getCache().getResidualSolventCode(decr);
	// } catch (CodeTableCacheException e) {
	// CeNErrorHandler.getInstance().logExceptionMsg(null,"Look up code
	// failed.", e);
	// }
	// return codeString;
	// }
	private String getResidualDecrFromCode(String code) {
		String decrString = "";
		try {
			decrString = CodeTableCache.getCache().getResidualSolventDescription(code);
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
	public ArrayList getEqList() {
		return eqList;
	}

	/**
	 * @param eqList
	 *            The eqList to set.
	 */
	public void setEqList(ArrayList eqList) {
		this.eqList = eqList;
	}

	/**
	 * @return Returns the residualSolventCount.
	 */
	public int getResidualSolventCount() {
		return residualSolventCount;
	}

	/**
	 * @param residualSolventCount
	 *            The residualSolventCount to set.
	 */
	public void setResidualSolventCount(int residualSolventCount) {
		this.residualSolventCount = residualSolventCount;
	}

	/**
	 * @return Returns the residualList.
	 */
	public ArrayList getResidualSolventVOList() {
		return residualSolventVOList;
	}

	/**
	 * @param residualList
	 *            The residualList to set.
	 */
	public void setResidualSolventVOList(ArrayList residualList) {
		this.residualSolventVOList = residualList;
	}
}