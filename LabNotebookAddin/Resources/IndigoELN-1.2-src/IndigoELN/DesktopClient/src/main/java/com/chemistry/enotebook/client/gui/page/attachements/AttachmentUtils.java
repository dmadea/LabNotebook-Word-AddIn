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
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.domain.AttachmentModel;
import com.chemistry.enotebook.experiment.datamodel.attachments.AttachmentLaunchException;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.utils.CeNJobProgressHandler;
import com.chemistry.enotebook.utils.SwingWorker;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class AttachmentUtils {

	private static final Log log = LogFactory.getLog(AttachmentUtils.class);
	
	public AttachmentUtils() {
	}

	public File createTemporaryFile(final AttachmentModel doc) {
		try {
			if (doc.getOriginalFileName() != null && doc.getType() != null) {
				final File tempFile = File.createTempFile("CeNAttach", doc.getType());
				
				tempFile.deleteOnExit();
				if (tempFile.exists()) {
					final FileWriter fout = new FileWriter(tempFile);
					final FileOutputStream fos = new FileOutputStream(tempFile);
					
          if (doc.getContents() == null) { // lazy load the file
            new SwingWorker() {
              
              public Object construct() {
                NotebookUser user = MasterController.getUser();
                SessionIdentifier sessionID = user.getSessionIdentifier();
                String progressStatus = "Loading Attachment \"" + doc.getDocumentName() + "\" ...";
              
                try {
                  StorageDelegate storageDelegate = ServiceController.getStorageDelegate(sessionID);
                  
                  CeNJobProgressHandler.getInstance().addItem(progressStatus);
                  AttachmentModel attachmentModel = storageDelegate.getNotebookPageExperimentAttachment(doc.getKey());
                  
                  doc.setContents(attachmentModel.getContents());
                  fos.write(doc.getContents());
                  fos.close();
                  fout.close();
                
                  new AttachmentUtils().launchFile(tempFile, null);
                }
                catch (Exception e) {
                  JOptionPane.showMessageDialog(MasterController.getGuiComponent(), "Error: Unable to retrieve attachment from server", "File Retrieve Error", JOptionPane.ERROR_MESSAGE);
                  e.printStackTrace();
                }
                finally {
                  CeNJobProgressHandler.getInstance().removeItem(progressStatus);
                }
                
                return null;
              }
              
              public void finished() {
              }
            }.start();  
            
            return null;
          }
					
					fos.write(doc.getContents());  // file has already been loaded previously
					fos.close();
					fout.close();
				}
				return tempFile;
			} else {
				return null;
			}
		} catch (IOException e) {
			log.error("Error creating temporary file: ", e);
			return null;
		}
	}

	public static void setAttachmentIcon(AttachmentModel doc) {
		File tempFile = null;
		try {
			tempFile = File.createTempFile("icon", doc.getType());

			// get the icon
			Icon fileIcon = null;
			boolean blnFoundIcon = false;
			while (!blnFoundIcon) {
				File iconFile = new File(tempFile.getCanonicalPath());
				fileIcon = getIcon(iconFile);
				if (fileIcon instanceof ImageIcon) {
					blnFoundIcon = true;
				}
			}
			doc.setIcon(fileIcon);
		} catch (IOException e) {
			log.error("Error setting attachement icon: ", e);
		} finally {
			if (tempFile != null) {
				tempFile.delete();
			}
		}
		}

	public void launchFile(File retrievedFile, String strUrl) throws AttachmentLaunchException {
		if (retrievedFile != null || strUrl != null) {
			try {
				Desktop.getDesktop().browse(retrievedFile.toURI());
			} catch (Exception e) {
				throw new AttachmentLaunchException(e);
			}
		}
	}

	/**
	 * Gets the O.S file system view using FileSystemView class. and searches for the corresponding icon for specified file Obj.
	 */
	public static Icon getIcon(File fileName) {
		FileSystemView view = FileSystemView.getFileSystemView();
		return view.getSystemIcon(fileName);
	}

	public static boolean isWebSite(String fileName) {
		if (StringUtils.isNotBlank(fileName)) {
			String http = "http://";
			String https = "http://";
			String ftp = "ftp://";
			if (fileName.startsWith(http) || fileName.startsWith(https) || fileName.startsWith(ftp)) {
				return true;
			}
		}
		return false;
	}
}
