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
package com.chemistry.enotebook.client.controller;

import com.chemistry.enotebook.client.gui.common.utils.GetEnv;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.experiment.utils.SystemPropertyException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * 
 * This class extends Thread and its purpose is to retrieve and delete temporary
 * files if and only if the number of Temporary files exceeds threshold constant
 * as well as the passed list of file prefixes is met. If the mastercontoller
 * exits, the isAlive method is called and the thread is interrupted
 * 
 * 
 * 
 */
public class DeleteTempFiles 
	extends Thread 
	implements FileFilter 
{
//	// starts to delete tempfiles from this number up
//	private static int threshold = 0;

	private final static boolean debug = false;

	private static int numDelFile = 0;

	// the file name pattern
	private List<String> filePattern = null;
	
    // holder for all the temporary files which met the file filter pattern
	private File[] tempFiles = null;

	/**
	 * Recursively deletes all the files in a Temp's subdirectories
	 */
	private static void recursiveDelete(File rootDir, long now) 
		throws IOException 
	{
		// Select all the files
		File[] files = rootDir.listFiles();
		if (files != null) { 
			for (int i = 0; i < files.length; i++) {
				// If the file is a directory, we will recursively call delete on it.
				if (files[i].isDirectory()) {
					recursiveDelete(files[i], now);
				} else {
					// if the current temp file was create it more than an hour ago it safe to delete it
					if (files[i].lastModified() < (now - 432000000)) {
						// It is just a file so we are safe to delete it
						if (!files[i].delete()) {
							// delete access was denied
							System.out.println("Could not delete file: " + files[i].getAbsolutePath());
						}
					}
				}
				numDelFile++;
			}
		}

		// Finally, delete the root directory now that all of the files in the
		// directory have been properly deleted.
		if (rootDir.lastModified() < (now - 432000000) && !rootDir.delete())
			System.out.println("Could not delete directory: " + rootDir.getAbsolutePath());
		
		numDelFile++;
	}

	public void run() 
	{
		try {
			Properties envProperties = (Properties) GetEnv.getProperties();
			String tempDirName = (String) envProperties.getProperty("TEMP");
			filePattern = CeNSystemProperties.getTempFileExtentions();
//			threshold = Integer.parseInt(CeNSystemProperties.getTemFilesThreshold());
			File myTempDir = new File(tempDirName);
			DateFormat dform = DateFormat.getInstance();
			long lastMod = 0;
			long now = new Date().getTime();
			
			// if is not directory throws null
			if (myTempDir.isDirectory())
				tempFiles = (File[]) myTempDir.listFiles((FileFilter) this);

			if (debug) {
				System.out.println("tempFiles is null? " + (tempFiles == null));
				if (tempFiles != null) System.out.println("Total Number of Temporary Files: " + tempFiles.length);
			}
			
			// while there is more than the threshold number of temporary files
			// delete all of the excess files
			if (tempFiles != null) {
				for (int i = 0; i < tempFiles.length; i++) {
					if (interrupted())
						break;
					
					try {
						// if the current temp file was create it more than 5 days ago it safe to delete it						
						if (tempFiles[i].isDirectory()) {
							recursiveDelete(tempFiles[i], now);
						} else if (tempFiles[i].lastModified() < (now - 432000000)) {
							// it is a file delete it and proceed to the next file
							tempFiles[i].delete();
							numDelFile++;
							if (debug) {
								System.out.println("\nDeleting file  :"
										+ tempFiles[i].getName()
										+ "\nFile was created on :"
										+ dform.format(new Date(lastMod)));
							}
						}
					} catch (SecurityException secEx) {
						// unable to delete the current file go to the next file
						System.out.println("Could not delete file: " + tempFiles[i] + " " + secEx.getMessage());
//						CeNErrorHandler.getInstance().logExceptionMsg(null, secEx);
					} catch (IOException io) {
						// it could not delete a file
						System.out.println("Could not delete file: " + tempFiles[i] + " " + io.getMessage());
//						CeNErrorHandler.getInstance().logExceptionMsg(null, io);
					}
				}
			}
		} catch (SecurityException secEx) {
			System.out.println("Could not delete file." + secEx.getMessage());
//			CeNErrorHandler.getInstance().logExceptionMsg(null, secEx);
		} catch (SystemPropertyException sysEx) {
			System.out.println("Could not delete file." + sysEx.getMessage());
//			CeNErrorHandler.getInstance().logExceptionMsg(null, sysEx);
		} catch (NumberFormatException nx) {
			System.out.println("Could not delete file." + nx.getMessage());
//			CeNErrorHandler.getInstance().logExceptionMsg(null, nx);
		} catch (NullPointerException nullExc) {
			System.out.println("Could not delete file." + nullExc.getMessage());
//			CeNErrorHandler.getInstance().logExceptionMsg(null, nullExc);
		} catch (IOException io) {
			System.out.println("Could not delete file." + io.getMessage());
//			CeNErrorHandler.getInstance().logExceptionMsg(null, io);
		} catch (Exception e) {
			System.out.println("Could not delete file." + e.getMessage());
//			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		
		if (debug) System.out.println("Total Number of Deleted Files :" + numDelFile);
	}

	public boolean accept(File pathname) 
	{
		boolean match = false;
		Object filePrefix = null;
		for (Iterator<String> i = filePattern.iterator(); !match && i.hasNext();) {
			filePrefix = (i.next().toString()).toUpperCase();
			if ((pathname.getName().toUpperCase()).startsWith(filePrefix.toString())) {
				if (debug) {
					System.out.println("The file prefix is: " + filePrefix);
					System.out.println("File accepted: " + pathname.getName());
				}

				match = true;
			}
		}
		return match;
	}
}
