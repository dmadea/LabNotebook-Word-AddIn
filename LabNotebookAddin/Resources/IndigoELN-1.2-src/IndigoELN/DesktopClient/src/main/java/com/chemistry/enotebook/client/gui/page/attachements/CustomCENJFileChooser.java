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

import sun.awt.shell.ShellFolder;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * 
 */
public class CustomCENJFileChooser extends JFileChooser {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6533871859141853879L;

	/**
	 * 
	 */
	public CustomCENJFileChooser() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public CustomCENJFileChooser(File arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public CustomCENJFileChooser(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public CustomCENJFileChooser(FileSystemView arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CustomCENJFileChooser(File arg0, FileSystemView arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CustomCENJFileChooser(String arg0, FileSystemView arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	// public void setCurrentDirectory(File dir)
	// {
	// String str = dir.getName();
	// int index = str.indexOf(".");
	// if (index != -1) {
	// String newstr = str.substring(index, str.length());
	// if (newstr.equals("lnk")) {
	// //File f = new File(".lnk");
	//				
	// try {
	// ShellFolder folder = ShellFolder.getShellFolder(dir);
	//		
	// if(folder.isLink())
	// {
	// dir = folder.getLinkLocation();
	// }
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	// super.setCurrentDirectory(dir);
	// }
	public void setSelectedFile(File file) {
		File oldValue = super.getSelectedFile();
		// super.setSelectedFile(file);
		if (file != null) {
			// for debug
			// //System.out.println(file.toString());
			String fileType = super.getTypeDescription(file);
			if (fileType.equals("Shortcut")) {
				try {
					ShellFolder folder = ShellFolder.getShellFolder(file);
					if (folder.isLink()) {
						file = folder.getLinkLocation();
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setCurrentDirectory(file);
			} else {
				super.setSelectedFile(file);
				if (file.isAbsolute() && !getFileSystemView().isParent(super.getCurrentDirectory(), super.getSelectedFile())) {
					super.setCurrentDirectory(super.getSelectedFile().getParentFile());
				}
				if (!isMultiSelectionEnabled() || getSelectedFiles() == null || getSelectedFiles().length > 1) {
					ensureFileIsVisible(super.getSelectedFile());
				}
			}
		}
		firePropertyChange(SELECTED_FILE_CHANGED_PROPERTY, oldValue, super.getSelectedFile());
	}
	// public void setCurrentDirectory(File dir)
	// {
	// File f = new File(".lnk");
	// try {
	// ShellFolder folder = ShellFolder.getShellFolder(f);
	//
	// if(folder.isLink())
	// {
	// dir = folder.getLinkLocation();
	// }
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// super.setCurrentDirectory(dir);
	// }
}
