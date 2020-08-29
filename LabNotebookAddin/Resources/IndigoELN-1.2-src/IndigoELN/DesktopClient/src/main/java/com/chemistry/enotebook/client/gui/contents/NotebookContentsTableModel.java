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
package com.chemistry.enotebook.client.gui.contents;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.client.gui.query_search.ReactionPageInfoHelper;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.ContentsContext;
import com.chemistry.enotebook.storage.ReactionPageInfo;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.utils.CommonUtils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NotebookContentsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 3536258592597579225L;
	
	private String columnNames[] = { "   Notebook -\n  Experiment", "           Experiment Details", " Reaction Scheme   " };
	private Class<?> columnClasses[] = { String.class, String.class, ImageIcon.class };
	private List<ReactionPageInfo> pagesList = new ArrayList<ReactionPageInfo>();
	private String siteCode = null;
	private String notebook = null;
	private int startExp = -1;
	private int stopExp = -1;
	private String user = null;

	private JTable table;
	
	public NotebookContentsTableModel(String siteCode, String notebook, int startExperiment, int stopExperiment) {
		this.siteCode = siteCode;
		this.notebook = notebook;
		this.startExp = startExperiment;
		this.stopExp = stopExperiment;
		ContentsContext cc = new ContentsContext();
		cc.setSiteCode(siteCode);
		cc.setNotebook(notebook);
		cc.setStartExperiment(startExperiment);
		cc.setStopExperiment(stopExperiment);
		try {
			SessionIdentifier sessionIdentifier = null;
			if(MasterController.getUser() != null) {
				sessionIdentifier = MasterController.getUser().getSessionIdentifier();
			}
			StorageDelegate sd = ServiceController.getStorageDelegate(sessionIdentifier);
			cc = (ContentsContext) sd.retrieveData(cc,MasterController.getUser().getSessionIdentifier());
			
			pagesList = CommonUtils.buildReactionPagesList(cc.getResults());
			
			for (ReactionPageInfo page : pagesList)
				this.user = page.getUsername();
			
			fireTableDataChanged();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void setTable(JTable table) {
		this.table = table;
	}
	
	public void dispose() {
		pagesList = null;
	}

	public String getSiteCode() {
		return siteCode;
	}

	public String getNotebook() {
		return notebook;
	}

	public int getStartExperiment() {
		return startExp;
	}

	public int getStopExperiment() {
		return stopExp;
	}

	public String getUser() {
		return user;
	}

	public int getRowCount() {
		return pagesList.size();
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int column) {
		return (column >= 0 && column < columnNames.length) ? columnNames[column] : "";
	}

	public Class<?> getColumnClass(int column) {
		return (column >= 0 && column < columnNames.length) ? columnClasses[column] : null;
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
			// if (row.getVersion() <= 1)
			return row.getNoteBookExperiment();
			// else
			// return row.getNoteBookExperiment() + "\n v" +
			// row.getVersion();
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
		}
		return "";
	}

	public List<ReactionPageInfo> getPagesList() {
		return pagesList;
	}

	public void setPagesList(List<ReactionPageInfo> pagesList) {
		this.pagesList = pagesList;
	}
}
