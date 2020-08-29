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
/**
 * 
 */
package com.chemistry.enotebook.client.gui.page.analytical.singleton;

import com.chemistry.enotebook.analyticalservice.delegate.AnalyticalServiceDelegate;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.analytical.JDialogViewerQuery;
import com.chemistry.enotebook.experiment.datamodel.analytical.Analysis;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.utils.CeNJobProgressHandler;
import com.chemistry.enotebook.utils.SSIZipLauncher;
import com.chemistry.enotebook.utils.Unzipper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 
 *
 */
public class ViewFileThread 
	extends Thread 
{
	private JPanel parent = null;
	private Analysis selectedAnalysis = null;
    private static final Log log = LogFactory.getLog(ViewFileThread.class);
    private String appType = null;
    
	
	public ViewFileThread(JPanel parent, Analysis selectedAnalysis, String appType) {
		this.parent = parent;		
		this.selectedAnalysis = selectedAnalysis;
		this.appType = appType;
	}
	
	/**
	 * 
	 * @param query
	 * @param sites
	 */
	private void viewFile(Analysis a) {
		File tempDir = null;
		String tempFileName = null;
		StringBuffer tempDirName = new StringBuffer();
		
		// int progIdx = -1;
		String progressStatus = "Retrieving file from AnalyticalService...";
		try {
			parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));		
			CeNJobProgressHandler.getInstance().addItem(progressStatus);
			
			AnalyticalServiceDelegate analyticalServiceDelegate = new AnalyticalServiceDelegate();
			log.info("Retrieving file from AnalyticalService: " + a.getAnalyticalServiceSampleRef() + " " + a.getCyberLabFileId() + " '" + a.getFileName() + "'");
			byte[] fileContents = analyticalServiceDelegate.retrieveFileContents(a.getCyberLabDomainId(), a.getCyberLabFileId(), a.getSite());
			if (fileContents != null && fileContents.length > 0) {
				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
				
				progressStatus = "Creating temp location...";
				CeNJobProgressHandler.getInstance().addItem(progressStatus);
				String fileName = AnalyticalServiceDelegate.windowsValidFileName(a.getFileName());
				fileName = fileName.replace('|', '\\');		// replace pipe symbol with slash, this is found is ssizip files
				
				File f = new File(fileName);
				fileName = f.getName();
				fileName = fileName.replace(' ', '_');		// blanks could cause problems
				
				tempDir = File.createTempFile("analyticalService", null);
				tempDirName.append(tempDir.getPath());
				tempDir.delete(); 							// remove file so we can make a directory
				tempDir = new File(tempDirName.toString());
				tempDir.mkdir();
				tempDir.deleteOnExit();
				tempFileName = tempDirName + File.separator + fileName;
				
				// get file name from analysis object
				FileOutputStream fos = new FileOutputStream(tempFileName);
				fos.write(fileContents);
				fos.close();
				
				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
				//if acd and raw nmr or raw lc need to unpack.  mestre only needs lc unpacked
				if ((a.getInstrumentType().equalsIgnoreCase("NMR") || a.getInstrumentType().equalsIgnoreCase("LC-MS")) && 
					appType.equals(JDialogViewerQuery.ACD_LABS) && a.getFileType().equals("RAW")) {
					progressStatus = "Unzipping to temp location...";
					CeNJobProgressHandler.getInstance().addItem(progressStatus);
					(new Unzipper()).unzip(tempFileName, tempDir.getPath());
					CeNJobProgressHandler.getInstance().removeItem(progressStatus);
				} else if (a.getInstrumentType().equalsIgnoreCase("LC-MS") && appType.equals(JDialogViewerQuery.MESTRE_NOVA) && a.getFileType().equals("RAW")) {
					progressStatus = "Unzipping to temp location...";
					CeNJobProgressHandler.getInstance().addItem(progressStatus);
					(new Unzipper()).unzip(tempFileName, tempDir.getPath());
					CeNJobProgressHandler.getInstance().removeItem(progressStatus);
				}
				parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	
				// Launch File
				progressStatus = "Launching from temp location...";
				CeNJobProgressHandler.getInstance().addItem(progressStatus);
				if (fileName.endsWith("SSIZip") && a.getInstrumentType().equalsIgnoreCase("NMR")) {
					Process p = null;
					if (appType.equals(JDialogViewerQuery.ACD_LABS)) {
						String launchFile = SSIZipLauncher.findNMRLaunchFile(tempDirName.toString());
						if (launchFile == null) {
							JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
									"Viewer cannot be launched since SSIZip/zip file does not contain a fid, ser or .als file.",
									"Error", JOptionPane.ERROR_MESSAGE);
						} else {
							p = Runtime.getRuntime().exec("cmd /c \"" + CeNSystemProperties.getACDNMRViewerFullyQualifiedPath() + "\" /SP" + launchFile);
							
							// Log the metric of # of users who use ACD to view NMRs
							CeNErrorHandler.getInstance().sendMetricsLogToServer("VIEW NMR ACD", MasterController.getUser().getNTUserID(), MasterController.getUser().getSiteCode());
							//p.waitFor(); // This only waits for the process to be kicked off. Not for the app to open.
						}
					} else {
						p = Runtime.getRuntime().exec("cmd /c \"" + CeNSystemProperties.getMestreNovaNMRSoftware() + "\" " + tempFileName);
						
						// Log the metric of # of users who use ACD to view NMRs
						CeNErrorHandler.getInstance().sendMetricsLogToServer("VIEW NMR MN", MasterController.getUser().getNTUserID(), MasterController.getUser().getSiteCode());
						//p.waitFor(); // This only waits for the process to be kicked off. Not for the app to open.
					}
				} else if (fileName.endsWith("SSIZip") && a.getInstrumentType().equalsIgnoreCase("LC-MS")) {
					Process p = null;
					if (appType.equals(JDialogViewerQuery.ACD_LABS)) {
						String launchFile = SSIZipLauncher.findLCMSLaunchFile(tempDirName.toString());
						if (launchFile == null) {
							JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
									"Viewer cannot be launched since SSIZip/zip file does not contain a _HEADER.TXT/MSD1.MS file.",
									"Error", JOptionPane.ERROR_MESSAGE);
						} else {
							p = Runtime.getRuntime().exec("cmd /c " + CeNSystemProperties.getACDLCMSViewerFullyQualifiedPath() + " /SP\"" + launchFile + "\""); 
							
							// Log the metric of # of users who use ACD to view NMRs
							CeNErrorHandler.getInstance().sendMetricsLogToServer("VIEW LCMS ACD", MasterController.getUser().getNTUserID(), MasterController.getUser().getSiteCode());
							//p.waitFor(); // This only waits for the process to be kicked off. Not for the app to open.
						}
					} else {
						String launchFile = SSIZipLauncher.findLCMSLaunchFile(tempDirName.toString());
						if (launchFile == null) {
							JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
									"Viewer cannot be launched since SSIZip/zip file does not contain a _HEADER.TXT/MSD1.MS file.",
									"Error", JOptionPane.ERROR_MESSAGE);
						} else {
							p = Runtime.getRuntime().exec("cmd /c \"\"" + CeNSystemProperties.getMestreNovaLCMSSoftware() + "\" \"" + launchFile + "\"\"");
						
							// Log the metric of # of users who use ACD to view NMRs
							CeNErrorHandler.getInstance().sendMetricsLogToServer("VIEW LCMS MN", MasterController.getUser().getNTUserID(), MasterController.getUser().getSiteCode());
							//p.waitFor(); // This only waits for the process to be kicked off. Not for the app to open.
						}
					}
				} else if (fileName.endsWith("SSIZip")) {   // Not NMR
					// we need to launch WinZip
					File origFile = new File(tempFileName);
					File tempFile = new File(tempFileName + ".zip");
					
					// Rename file (or directory)
					boolean success = origFile.renameTo(tempFile);
					if (!success) {
						CeNErrorHandler.showMsgOptionPane(parent.getParent(), "Failed renaming file.",
								"Could not rename SSIZip to zip.  Please copy the file to a new location if you wish to keep it.\nThe file is currently at: \n"
								+ tempFile + "\nIt will be erased on exit of the application.", JOptionPane.INFORMATION_MESSAGE);
						// TODO: change to using fileChooser menu to save file to user's location.
					} else {
	
						Process p = Runtime.getRuntime().exec("cmd /c start " + tempFile.getPath());
						p.waitFor(); // This only waits for the process to be
						// kicked off. Not for the app to open.
					}
				} else {
					Process p = Runtime.getRuntime().exec("cmd /c start " + tempFileName);
					p.waitFor(); // This only waits for the process to be
					// kicked off. Not for the app to open.
				}
			} else {
				JOptionPane.showMessageDialog(parent.getParent(), "File returned from AnalyticalService/CyberLab is empty.",
						  "Error", JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			String msg;
			if (e.getCause() == null) msg = e.getMessage(); else msg = e.getCause().toString();
			if (msg.indexOf("Call to CyberLab API failed") > -1) {
				JOptionPane.showMessageDialog(parent.getParent(), "File not found in AnalyticalService/CyberLab or CyberLab API Failure.",
											  "Error", JOptionPane.ERROR_MESSAGE);
				CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "Error occurred while performing viewFile, Not Displayed to User");
			} else {
				CeNErrorHandler.getInstance().logExceptionMsg(parent.getParent(), "Error occurred while performing viewFile", e);
			}
			parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			// Cleanup now
			SSIZipLauncher.deleteDirectoryTree(tempDirName.toString());
		} finally {
			parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			CeNJobProgressHandler.getInstance().removeItem(progressStatus);
		}
	}
	
	public void run() {
		viewFile(selectedAnalysis);
	}
}