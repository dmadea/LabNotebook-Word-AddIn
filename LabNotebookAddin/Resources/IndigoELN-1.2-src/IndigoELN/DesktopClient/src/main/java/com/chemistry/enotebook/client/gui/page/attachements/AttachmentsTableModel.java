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
 * Created on 13-Dec-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.attachements;

import com.chemistry.enotebook.experiment.datamodel.attachments.Attachment;
import com.chemistry.enotebook.experiment.datamodel.attachments.AttachmentCache;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class AttachmentsTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5663196803262502785L;
	private boolean readOnly = false;
	private ArrayList rowData = new ArrayList();
	private String[] columnNames = { "Type", "Name", "Size", "Modified", "IP related", "Description", "OriginalName" };
	private AttachmentCache attachmentList;

	public AttachmentsTableModel() {
	}

	public AttachmentsTableModel(AttachmentCache attachments, boolean readOnly) {
		this.readOnly = readOnly;
		populateModel(attachments);
		attachmentList = attachments;
	}

	public int getRowCount() {
		return rowData.size();
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		Object result = null;
		if (row >= 0 && row < rowData.size()) {
			ArrayList attachment = (ArrayList) rowData.get(row);
			if (attachment != null) {
				result = attachment.get(col);
			}
		}
		return result;
	}

	public void setValueAt(Object value, int row, int col) {
		ArrayList attachment = (ArrayList) rowData.get(row);
		attachment.set(col, value);
		Attachment doc = attachmentList.getAttachment(row);
		switch (col) {
			case 1: {
				doc.setDocumentName((String) value);
				break;
			}
			case 4: {
				boolean val = ((Boolean)value).booleanValue();
				if (val) {
					if (doc.getType().toUpperCase().equals(".PDF"))
						doc.setIpRelated(val);
					else {
						attachment.set(col, new Boolean(false));
						fireTableDataChanged();
					}
				} else 
					doc.setIpRelated(val);
				break;
			}
			case 5: {
				doc.setDocumentDescription((String) value);
				break;
			}
		}
		fireTableCellUpdated(row, col);
	}

	public void populateModel(AttachmentCache attachments) {
		rowData.clear();
		if (attachments != null) {
			Iterator attachmentIterator = attachments.iterator();
			while (attachmentIterator.hasNext()) {
				Attachment attachment = attachments.getAttachment((String) attachmentIterator.next());
				addAttachment(attachment);
			}
		}
		fireTableDataChanged();
	}

	private void addAttachment(Attachment attachment) {
		ArrayList attachColumns = new ArrayList();
		attachColumns.add(attachment.getIcon());
		attachColumns.add(attachment.getDocumentName());
		attachColumns.add(attachment.getSize());
		attachColumns.add(attachment.getDateModified());
		attachColumns.add(new Boolean(attachment.isIpRelated()));
		attachColumns.add(attachment.getDocumentDescription());
		attachColumns.add(attachment.getOriginalFileName());
		attachColumns.add(attachment.getKey()); // seventh column is not
		// displayed
		rowData.add(attachColumns);
	}

	public void createAttachment(String docName, String docDesc, String fileName) {
		Attachment attachment = attachmentList.createAttachment(docName, docDesc, fileName);
		addAttachment(attachment);
		fireTableDataChanged();
	}

	public Class getColumnClass(int column) {
		Class dataType = Object.class;
		if (column == 0) {
			dataType = Icon.class;
		} else {
			Object o = getValueAt(0, column);
			if (o != null)
				dataType = o.getClass();
		}
		return dataType;
	}

	public boolean isCellEditable(int row, int col) {
		switch (col) {
			case 4: // IP Related Column
				return !readOnly;
			default:
				return false;
		}
	}

	// Add a mouse listener to the table columns
	public void addMouseListenerToColumnsInTable(JTable table) {
		final JTable tableView = table;
		
		MouseAdapter listMouseListener = new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				TableColumnModel columnModel = tableView.getColumnModel();
				int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				int column = tableView.convertColumnIndexToModel(viewColumn);
				if (e.getButton() == 1 && ((String) columnModel.getColumn(column).getHeaderValue()).equals("IP related")) {
					// Check the document type.
					int row = tableView.rowAtPoint(e.getPoint());
					String strType = attachmentList.getAttachment(row).getType();
					if (!strType.toUpperCase().equals(".PDF")) {
						JOptionPane.showMessageDialog(tableView, "Only PDF documents can be marked as IP related",
								"Invalid Document Type", JOptionPane.PLAIN_MESSAGE);
						tableView.getModel().setValueAt(new Boolean(false), row, column);
					}
				}
			}
		};
		tableView.addMouseListener(listMouseListener);
	}

	public void dispose() {
		this.attachmentList = null;
	}
}
