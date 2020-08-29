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
 * Created on Mar 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.utils;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;

import java.io.File;

/**
 * 
 * 
 * Launches given file with an external application
 */
public class SSIZipLauncher {
	private static CeNErrorHandler ceh = CeNErrorHandler.getInstance();

	/**
	 * deleteDirectoryTree - recursively deletes a directory and its contents.
	 * 
	 * @param pathname -
	 *            absolute or relative pathname of the directory to delete
	 * @return true if successful otherwise false
	 */
	public static boolean deleteDirectoryTree(String pathname) {
		boolean returnValue = false;
		if (pathname != null) {
			File dir = new File(pathname);
			if (dir != null) {
				File[] files = dir.listFiles();
				if (files != null) {
					for (int i = 0, n = files.length; i < n; i++) {
						if (files[i].isDirectory() == true) {
							deleteDirectoryTree(files[i].getPath());
						} else if (files[i].isFile() == true) {
							files[i].delete();
						}
					}
				}
				returnValue = dir.delete();
			}
		}
		return returnValue;
	}

	public static String findFirstFile(final String filename, final String directory) {
		String returnValue = null;
		if (directory != null) {
			File dir = new File(directory);
			if (dir != null) {
				File[] files = dir.listFiles();
				if (files != null) {
					for (int i = 0, n = files.length; i < n; i++) {
						if (files[i].isDirectory() == true) {
							returnValue = findFirstFile(filename, files[i].getPath());
						} else if (files[i].isFile() == true) {
							if (filename.equals(files[i].getName()) == true) {
								returnValue = files[i].getAbsolutePath();
								break;
							}
						}
					}
				}
			}
		}
		return returnValue;
	}

	public static String findZeol(final String directory)
	{
	    String returnValue = null;
		if (directory != null) {
			File dir = new File(directory);
			if (dir != null) {
				File[] files = dir.listFiles();
				if (files != null) {
					for (int i = 0, n = files.length; i < n; i++) {
						if (files[i].isDirectory() == true) {
							returnValue = findZeol(files[i].getPath());
						} else if (files[i].isFile() == true) {
							String name = files[i].getName().toUpperCase();
						    if (name.endsWith(".ALS") && !name.endsWith("_FT.ALS")) {
						        returnValue = files[i].getAbsolutePath();
						        break;
						    }
						}
					}
				}
			}
		}
	    
	    return returnValue;
	}

	public static String findNMRLaunchFile(String tempDirName)
	{
		String launchFile = null;
		
		launchFile = findFirstFile("fid", tempDirName);			
		if (launchFile == null) {								// Varian NMR?
			launchFile = findFirstFile("ser", tempDirName);		
			if (launchFile == null)								// Bruker NMR?
				launchFile = findZeol(tempDirName);				// Zeol NMR
		}
		
		return launchFile;
	}
	
	public static String findLCMSLaunchFile(String tempDirName)
	{
		String launchFile = null;
		
		launchFile = findFirstFile("_HEADER.TXT", tempDirName);		// MassLynx?
		if (launchFile == null)								
			launchFile = findFirstFile("MSD1.MS", tempDirName);		// HPLC
		
		return launchFile;
	}
	
//    public static void unzipAndLaunchFID(String fileToUnzip)
//    {
//        Unzipper unzipper = new Unzipper();
//        File tempdir;
//        try
//        {
//            tempdir = File.createTempFile("analyticalService", null);
//            String tempdirName = tempdir.getPath();
//            tempdir.delete();
//            tempdir = new File(tempdirName);
//            tempdir.mkdir();
//            unzipper.unzip(fileToUnzip, tempdir.getPath());
//            String launchFile = findFirstFile("fid", tempdir.getPath());
//            /***/
////            System.out.println("Runtie.getRuntime().exec(cmd /c " + CeNSystemProperties.getACDNMRSoftware() + " /SP\"" + launchFile + "\"");
//            Process p = Runtime.getRuntime().exec("cmd /c " + CeNSystemProperties.getACDNMRSoftware() + " /SP\"" + launchFile + "\"");
//			p.waitFor();
//			/***/
//            tempdir.deleteOnExit();
////            deleteDirectoryTree(tempdirName);
//        } catch (SystemPropertyException e) {
//            ceh.logExceptionMsg(e);
//        } catch (IOException e) {
//            ceh.logExceptionMsg(e);
//        } catch (InterruptedException e) {
//            ceh.logExceptionMsg(e);
//        }
//    }
}
