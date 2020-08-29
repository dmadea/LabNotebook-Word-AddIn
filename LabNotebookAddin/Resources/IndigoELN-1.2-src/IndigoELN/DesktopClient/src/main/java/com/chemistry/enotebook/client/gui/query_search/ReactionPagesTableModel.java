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
package com.chemistry.enotebook.client.gui.query_search;

import com.chemistry.enotebook.storage.ReactionPageInfo;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ReactionPagesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 5999696809406908228L;
	
	public static final String NOTEBOOK_EXPERIMENT = "   Notebook -\n  Experiment";
	public static final String PAGE_INFO = "Chemist, Site, Project, Date, Subject, Lit. Ref., TA";
	public static final String REACTION_SCHEME = "Reaction Scheme   ";
	private List<ReactionPageInfo> pagesList = new ArrayList<ReactionPageInfo>();

	private JTable table;
	
	public ReactionPagesTableModel(JTable table) {
		this.table = table;
	}
	
	public int getRowCount() {
		return pagesList.size();
	}

	public int getColumnCount() {
		return 4;
	}

	public String getColumnName(int column) {
		switch (column) {
			case 0:
				return ReactionPagesTableModel.NOTEBOOK_EXPERIMENT;
			case 1:
				return ReactionPagesTableModel.PAGE_INFO;
			case 2:
				return ReactionPagesTableModel.REACTION_SCHEME;
			case 3:
				return "";
		}
		return "";
	}

	public Class getColumnClass(int column) {
		Class dataType = super.getColumnClass(column);
		if (column == 2)
			dataType = ImageIcon.class;
		else if (column == 3)
			dataType = ReactionPageInfo.class;
		return dataType;
	}

	public boolean isCellEditable(int nRow, int nCol) {
		return false;
	}

	public Object getValueAt(int nRow, int nCol) {
		if (nRow < 0 || nRow >= getRowCount())
			return "";
		ReactionPageInfo row = (ReactionPageInfo) this.pagesList.get(nRow);
		switch (nCol) {
		case 0:
			// if (row.getVersion() > 1)
			return row.getNoteBookExperiment() + "\n v" + row.getVersion();
			// // else
			// return row.getNoteBookExperiment();
		case 1:
			return row.getPageInfo();
		case 2:
			if (table != null) {
				int width = table.getColumnModel().getColumn(nCol).getWidth();
				int height = table.getRowHeight(nRow);
				row.setHeight(height);
				row.setWidth(width);
				ReactionPageInfoHelper reactionPageInfoHelper = new ReactionPageInfoHelper(row);
				return new ImageIcon(reactionPageInfoHelper.getReactionImage(new Dimension(width, height)));
			} else {
				ReactionPageInfoHelper reactionPageInfoHelper = new ReactionPageInfoHelper(row);
				return new ImageIcon(reactionPageInfoHelper.getReactionImage(null));
			}
		case 3:
			return row;
		}
		return "";
	}

	public void resetModelData() {
	}

	public void buildReactionPagesList(List smallNotebookPages) {
		if (smallNotebookPages == null)
			throw new IllegalArgumentException("List of NotebookPages is null in buildReactionPageList");
		if (smallNotebookPages.size() > 0) {
			pagesList = smallNotebookPages;
			fireTableDataChanged();
		}
	}
}