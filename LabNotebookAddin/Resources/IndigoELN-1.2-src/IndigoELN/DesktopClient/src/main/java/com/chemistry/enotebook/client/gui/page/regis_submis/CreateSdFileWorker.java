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
package com.chemistry.enotebook.client.gui.page.regis_submis;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.utils.SwingWorker;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;


abstract class CreateSdFileWorker extends SwingWorker {
	
	public void finished() {
		String sdFile = (String) get();
		if (sdFile != null && sdFile.length() > 0) {
			JFileChooser jfc = new JFileChooser();
			jfc.setMultiSelectionEnabled(false);
			FileFilter filter = new FileFilter() {
				public boolean accept(File f) {
					if (f.isDirectory()) {
						return true;
					}
					String extension = getExtension(f);
					if (extension != null)
						return (extension.equals("sdf"));
					return false;
				}

				public String getDescription() {
					return "SD Files";
				}

				private String getExtension(File f) {
					String ext = null;
					String s = f.getName();
					int i = s.lastIndexOf('.');
					if (i > 0 && i < s.length() - 1) {
						ext = s.substring(i + 1).toLowerCase();
					}
					return ext;
				}
			};
			jfc.setFileFilter(filter);
			int result = jfc.showSaveDialog(MasterController.getGUIComponent());
			if (result == JFileChooser.APPROVE_OPTION) {
				File attachedFile = jfc.getSelectedFile();
				String filename = attachedFile.getAbsolutePath();
				if (!filename.toLowerCase().endsWith(".sdf"))
					filename += ".sdf";
				attachedFile = new File(filename);
				if (attachedFile != null) {
					if (attachedFile.exists()) {
						int response = JOptionPane.showConfirmDialog(null, "Overwrite existing file " + attachedFile.getName() + " ?", "Confirm Overwrite",
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (response == JOptionPane.CANCEL_OPTION)
							return;
					}
					try {
						java.io.FileOutputStream os = new java.io.FileOutputStream(filename);
						os.write(sdFile.getBytes());
						os.flush();
						os.close();
					} catch (java.io.IOException e) {
						CeNErrorHandler.getInstance().showMsgDialog(MasterController.getGUIComponent(),
								"Error Writing SD File", e.getMessage());
					}
				}
			}
		}
	}
}
