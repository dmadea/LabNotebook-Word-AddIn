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
package com.chemistry;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ISISDrawProxy extends ChemistryEditorProxy {
	private static final String ISISDRAW_REGISTRY = "\\ISISServer\\shell\\open\\command";
	private static final String STATUS_OK = "OK";
	private static final String STATUS_CANCEL = "CANCEL";

	private byte[] molBytes;
	private byte[] rxnBytes;
	private int rxnIndex = -1;

	private static String executablePath;

	private static int newFilesCounter = 0;

	private static final File tempFilesDirectory = new File(MasterController.getApplicationDirectory(), "temp_chemistry_files");
	private static final File ISISLaunchFile = new File(tempFilesDirectory, "LAUNCH.EPL");
	private File ISISParametersFile = new File(System.getProperty("user.home") + "\\Local Settings\\Temp\\IsisDrawLauncher.ctl");

	static {
		// create directory for temp files
		if (! tempFilesDirectory.exists()) 
			tempFilesDirectory.mkdirs();
		if (ISISLaunchFile.exists())
			ISISLaunchFile.delete();
		loadISISLaunchFile(ISISLaunchFile);
		// get ISIS_Draw location
		String isisLocation = null;
		try {
			isisLocation = Advapi32Util.registryGetStringValue(WinReg.HKEY_CLASSES_ROOT, ISISDRAW_REGISTRY, "");
		} catch (Exception e) {
			// ISIS/Draw is not installed
		}
		if (isisLocation != null) {
			isisLocation = isisLocation.replaceAll("\"", "");
			executablePath = isisLocation.split(" %")[0];
		}
	}

	public ISISDrawProxy() {
		super();
		rxnIndex = ++newFilesCounter;
	}

	private static void loadISISLaunchFile(File ISISLaunchFile) {
		try {
			// reading resource
			InputStream inputStream = ISISDrawProxy.class.getClassLoader().getResourceAsStream("data/LAUNCH.EPL");
			ArrayList<Byte> contents = new ArrayList<Byte>();
			int numRead = 0;
			try {
				while ((numRead = inputStream.read()) != -1) {
					contents.add(new Byte((byte) numRead));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			Object[] array = contents.toArray();
			byte[] toWrite = new byte[array.length];
			for (int i = 0; i < array.length; ++i) {
				toWrite[i] = ((Byte) array[i]).byteValue();
			}

			// writing file
			ISISLaunchFile.createNewFile();
			FileOutputStream fileStream = new FileOutputStream(ISISLaunchFile);
			fileStream.write(toWrite);
			fileStream.close();
		} catch (IOException e) {
		}
	}

	@Override
	public byte[] getChemistry(int format) {
		switch (format) {

		case MOL_FORMAT: return molBytes;

		case RXN_FORMAT: return rxnBytes;

		default: return null;

		}
	}

	@Override
	protected boolean assignChemistry(byte[] chemistry, int format) {
		switch (format) {
		case MOL_FORMAT:
		case RXN_FORMAT:
			return setMolRxnData(chemistry);
		default:
			return false;
		}
	}

	/**
	 * Calls editor and waits for editing finish.
	 * @return true, if object was modified
	 *         false, if object wasn't modified
	 */
	@Override
	protected boolean callEditor() {
		enableWindow(false);
		
		if (executablePath == null)
			newRuntimeException("Can't find ISIS/Draw editor");
		if (!ISISLaunchFile.exists()) {
			loadISISLaunchFile(ISISLaunchFile);
			if (!ISISLaunchFile.exists()) 
				newRuntimeException("Can't find ISISDraw launch file in the resources");
		}

		File inRxnFile = new File(tempFilesDirectory, "in" + rxnIndex + ".rxn");
		File statusFile = new File(tempFilesDirectory, "status" + rxnIndex + ".sts");
		File outRxnFile = new File(tempFilesDirectory, "out" + rxnIndex + ".rxn");
		File outMolFile = new File(tempFilesDirectory, "out" + rxnIndex + ".mol");

		// Prepare RXN file
		try {
			FileOutputStream fout = new FileOutputStream(inRxnFile);
			if (rxnBytes != null && rxnBytes.length > 0) 
				fout.write(rxnBytes, 0, rxnBytes.length);
			else if (molBytes != null && molBytes.length > 0) 
				fout.write(molBytes, 0, molBytes.length);
			fout.close();
		} catch (IOException e) {
			newRuntimeException("Failed to create input RXN/MOL file");
		}

		fillISISParametersFile(inRxnFile, statusFile, outRxnFile, outMolFile);
		if (!ISISParametersFile.exists())
			newRuntimeException("Failed to create ISISDraw parameters file");

		String status = null;

		try {
			Process process = 
				Runtime.getRuntime().exec(
						new String[] {executablePath, ISISLaunchFile.getAbsolutePath()});
		
			// Wait for status file created and filled up
			while (true) {
				if (statusFile.exists()) {
					// Read status file
					try {
						BufferedReader reader = new BufferedReader(new FileReader(statusFile));
						status = reader.readLine();
						reader.close();
					} catch (FileNotFoundException e) {
						newRuntimeException("ISIS/Draw status file is not found");
					} catch (IOException e) {
						newRuntimeException("Failed to read ISIS/Draw status file");
					}
					process.destroy();
					break;
				}

				Thread.sleep(100);

				// if process was stopped then break the cycle
				try {
					process.exitValue();
					status = STATUS_CANCEL;
					break;
				} catch(IllegalThreadStateException e) {
					// the process is still running
				}
			}
		} catch (IOException e) {
			newRuntimeException("Error executing ISIS/Draw", e);			
		} catch (InterruptedException e) {
			newRuntimeException("Error executing ISIS/Draw", e);			
		}

		if (STATUS_OK.equals(status)) { 
			// sketch was changed
			try {
				rxnBytes = readFile(outRxnFile);
				molBytes = readFile(outMolFile);
			} catch (IOException e) {
				newRuntimeException("Failed to read RXN or MOL file");
			}
			deleteTempFiles(inRxnFile, statusFile, outRxnFile, outMolFile);
			enableWindow(true);
			return true;
		} else if (STATUS_CANCEL.equals(status)) {
			// sketch wasn't changed
			deleteTempFiles(inRxnFile, statusFile, outRxnFile, outMolFile);
			enableWindow(true);
			return false;
		} else {
			deleteTempFiles(inRxnFile, statusFile, outRxnFile, outMolFile);
			newRuntimeException("Unknown status from ISIS/Draw");
		}
		
		enableWindow(true);
		return false;
	}

	private void newRuntimeException(String s) {
		newRuntimeException(s, null);
	}
	
	private void newRuntimeException(String s, Throwable t) {
		enableWindow(true);
		if (t != null)
			throw new RuntimeException(s, t);
		else
			throw new RuntimeException(s);
	}
	
	private void enableWindow(boolean b) {
		if (getEditComponent() != null) {
			CeNGUIUtils.findWindow(getEditComponent()).setEnabled(b);
			if (b)
				enableWindow();
		}
	}
	
	private void deleteTempFiles(File inRxnFile, File statusFile, File outRxnFile, File outMolFile) {
		if (inRxnFile.exists()) inRxnFile.delete();
		if (statusFile.exists()) statusFile.delete();
		if (outRxnFile.exists()) outRxnFile.delete();
		if (outMolFile.exists()) outMolFile.delete();
	}

	private boolean fillISISParametersFile(File inRxnFile, File statusFile, File outRxnFile, File outMolFile) {
		try {
			if (ISISParametersFile.exists())
				ISISParametersFile.delete();
			ISISParametersFile.createNewFile();
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter(ISISParametersFile));

			fileWriter.write(inRxnFile.getAbsolutePath());	// in rxn
			fileWriter.newLine();
			
			fileWriter.newLine();							// dll with icon image
			
			fileWriter.newLine();							// if rxn shift needed
			
			fileWriter.write(statusFile.getAbsolutePath());	// out status file
			fileWriter.newLine();
			
			fileWriter.write(outRxnFile.getAbsolutePath());	// out rxn file
			fileWriter.newLine();
			
			fileWriter.write(outMolFile.getAbsolutePath());	// out mol file

			
			fileWriter.close();

		} catch (IOException e) {
			return false;			
		} 
		return true;
	}

	private boolean setMolRxnData(byte[] chemistry) {
		if (Arrays.equals(rxnBytes, chemistry))
			return true;

		molBytes = chemistry;
		rxnBytes = chemistry;
		return true;
	}

	private byte[] readFile(File file) throws IOException {
		FileInputStream fin = new FileInputStream(file);
		byte fileContent[] = new byte[(int)file.length()];
		fin.read(fileContent);
		fin.close();
		return fileContent;
	}
}
