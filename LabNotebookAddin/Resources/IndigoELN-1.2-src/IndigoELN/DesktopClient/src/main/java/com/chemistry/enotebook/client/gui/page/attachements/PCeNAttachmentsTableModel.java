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
package com.chemistry.enotebook.client.gui.page.attachements;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.domain.AttachmentCacheModel;
import com.chemistry.enotebook.domain.AttachmentModel;
import com.chemistry.enotebook.domain.NotebookPageModel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * 
 * 
 */
public class PCeNAttachmentsTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6527323574807400741L;
	private boolean readOnly = false;
	private ArrayList rowData = new ArrayList();
	private String[] columnNames = { "Type", "Name", "Size", "Modified", "IP related", "Description", "OriginalName" };
	private AttachmentCacheModel attachmentList;
	private ArrayList<AttachmentModel> visibleAttachmentList; //attachments which are not set for delete. Expected to be mirror of rowData but with real attachments
	private NotebookPageModel pageModel;
	private JTable tableView;

	public PCeNAttachmentsTableModel() {
	}

	public PCeNAttachmentsTableModel(AttachmentCacheModel attachments, boolean readOnly, NotebookPageModel pageModel) {
		this.readOnly = readOnly;
		visibleAttachmentList = extractActualAttachments(attachments);
		populateModel(attachments);
		attachmentList = attachments;
		this.pageModel = pageModel;
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
		AttachmentModel doc = visibleAttachmentList.get(row);
		switch (col) {
			case 1: {
				doc.setDocumentName((String) value);
				break;
			}
			case 4: {
			  if (((Boolean) value).booleanValue()) {  // when mouse listener was used, syn problems between the table and listener could manifest
	        TableColumnModel columnModel = tableView.getColumnModel();
	        int viewColumn = tableView.convertColumnIndexToView(col);

	        if (((String) columnModel.getColumn(viewColumn).getHeaderValue()).equals("IP related")) {
	          // Check the document type.
	          String strType = visibleAttachmentList.get(row).getType();
	          
	          if (!strType.toUpperCase().equals(".PDF")) {
	            JOptionPane.showMessageDialog(tableView, "Only PDF documents can be marked as IP related",
	                "Invalid Document Type", JOptionPane.PLAIN_MESSAGE);
	            value = new Boolean(false);
	            tableView.getModel().setValueAt(value, row, col);
	          }
	        }
	    	}
			  
				doc.setIpRelated(((Boolean) value).booleanValue());
				break;
			}
			case 5: {
				doc.setDocumentDescription((String) value);
				break;
			}
		}
		fireTableCellUpdated(row, col);
		enableSaveButton();
	}

	public void populateModel(AttachmentCacheModel attachments) {
		rowData.clear();
		visibleAttachmentList.clear();
		if (attachments != null) {
			Iterator<AttachmentModel> attachmentIterator = attachments.getAttachmentList().iterator();
			while (attachmentIterator.hasNext()) {
				AttachmentModel attachment = attachmentIterator.next();

				if (!attachment.isSetToDelete()) {
					AttachmentUtils.setAttachmentIcon(attachment);
					addAttachment(attachment);
					visibleAttachmentList.add(attachment);
				}
			}
		}
		fireTableDataChanged();
	}

	//	private String[] columnNames = { "Type", "Name", "Size", "Modified", "IP related", "Description", "OriginalName" };
	private void addAttachment(AttachmentModel attachment) {
		ArrayList<Object> attachColumns = new ArrayList<Object>();
		attachColumns.add(attachment.getIcon());
		attachColumns.add(attachment.getDocumentName());
		int attachmentSize = attachment.getSize();
		if (attachmentSize >= 1000 && attachmentSize < 1000000) {
			attachColumns.add(Integer.toString(attachmentSize / 1000) + " KB");
		} else if (attachmentSize >= 1000000) {
			attachColumns.add(Integer.toString(attachmentSize / 1000000) + " MB");
		} else {
			attachColumns.add(attachmentSize + " bytes");
		}
		attachColumns.add(attachment.getDateModified());
		attachColumns.add(new Boolean(attachment.isIpRelated()));
		attachColumns.add(attachment.getDocumentDescription());
		attachColumns.add(attachment.getOriginalFileName());
		attachColumns.add(attachment.getKey()); // seventh column is not displayed
		rowData.add(attachColumns);
	}

	public void createAttachment(String docName, String docDesc, String fileName) {
		File attachedFile = new File(fileName);
		AttachmentModel newAttachmentModel = new AttachmentModel();

		newAttachmentModel.setDocumentDescription(docDesc);
		newAttachmentModel.setOriginalFileName(attachedFile.getName());
		newAttachmentModel.setDocumentName(docName);
				
		if (!fileName.startsWith("http") && !fileName.startsWith("https") && !fileName.startsWith("ftp")) {
			newAttachmentModel.setIcon(AttachmentUtils.getIcon(attachedFile));
		}

		if (fileName.substring(0, 7).equals("http://")) {
			newAttachmentModel.setType(".html");
			newAttachmentModel.setDateModified((new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")).format(new Date()));
			newAttachmentModel.setOriginalFileName(fileName);
		} else {
			try {
				if (attachedFile.exists()) {

					// doc.saveFileIcon((ImageIcon) jfc.getIcon(attachedFile));
					BufferedInputStream bis = new BufferedInputStream(new FileInputStream(attachedFile));
					int bytes = (int) attachedFile.length();
					byte[] buffer = new byte[bytes];
					int readBytes = bis.read(buffer);
					newAttachmentModel.setContents(buffer);
					newAttachmentModel.setDateModified((new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")).format(new Date(attachedFile.lastModified())));
					newAttachmentModel.setSize((int) attachedFile.length());
					newAttachmentModel.setOriginalFileName(attachedFile.getPath());
					newAttachmentModel.setType(attachedFile.getName().substring(attachedFile.getName().lastIndexOf(".")));
					
					if (attachedFile.getName().toLowerCase().endsWith(".pdf")) {
					  newAttachmentModel.setIpRelated(true);
					}
				}
			} catch (Exception e) {
				// We don't do this if the file doesn't exist
			}
		}

		attachmentList.addNewAttachment(newAttachmentModel);
		//AttachmentModel attachment = attachmentList.createAttachment(docName, docDesc, fileName);
		//addAttachment(newAttachmentModel);
		populateModel(attachmentList);
		enableSaveButton();
		//fireTableDataChanged();
	}

	private void enableSaveButton() {
		pageModel.setModelChanged(true);
		MasterController.getGUIComponent().enableSaveButtons();
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
		if (!pageModel.isEditable())
			return false;
		switch (col) {
			case 4: // IP Related Column
				return !readOnly;
			default:
				return false;
		}
	}
	
	public AttachmentModel getAttachmentModelWhichInTableRow(int row) {
		return visibleAttachmentList.get(row);
	}

	private ArrayList<AttachmentModel> extractActualAttachments(
			AttachmentCacheModel attachments) {
		ArrayList<AttachmentModel> newList = new ArrayList<AttachmentModel>();
		for(AttachmentModel attachment : attachments.getAttachmentList()) {
			if(!attachment.isSetToDelete()) {
				newList.add(attachment);
			}
			
		}
		return newList;
	}

	// Add a mouse listener to the table columns
	public void addMouseListenerToColumnsInTable(JTable table) {
	  tableView = table;  // use setValue() instead of mouse listener - much more reliable
	}

	public void dispose() {
		this.attachmentList = null;
	}
}
