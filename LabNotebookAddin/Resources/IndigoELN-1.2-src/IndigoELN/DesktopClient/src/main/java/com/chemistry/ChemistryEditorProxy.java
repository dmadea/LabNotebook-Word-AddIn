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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

public abstract class ChemistryEditorProxy {
	
	private static final Log LOG = LogFactory.getLog(ChemistryEditorProxy.class);
	
	public static final int MOL_FORMAT         =  1;
	public static final int RXN_FORMAT         =  2;
	
	private String info;
	
	private Component editComponent;
	
	protected Set<ChemistryEditorListener> listeners;
	
	public ChemistryEditorProxy() {
		super();
		listeners = new HashSet<ChemistryEditorListener>();
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}	

	public abstract byte[] getChemistry(int format);
	
	protected abstract boolean assignChemistry(byte[] chemistry, int format);
	
	/**
	 * Calls editor and waits for editing finish.
	 * @return true, if object was modified
	 *         false, if object wasn't modified
	 */
	protected abstract boolean callEditor();


	public void addChemistryEditorListener(ChemistryEditorListener chemistryEditorListener) {
		if (!listeners.contains(chemistryEditorListener))
			listeners.add(chemistryEditorListener);
	}

	public Set<ChemistryEditorListener> getListeners() {
		return listeners;
	}
	
	public void removeChemistryEditorListener(ChemistryEditorListener chemistryEditorListener) {
		listeners.remove(chemistryEditorListener);
	}
	
	public boolean setChemistry(InputStream inputStream, int format) throws IOException {
		int size = inputStream.available();
		if (size > 0) {
			byte[] data = new byte[size];
			inputStream.read(data);
			return setChemistry(data, format);
		}
		return false;
	}

	public void writeChemistry(OutputStream outputStream, int format) throws IOException {
		byte[] bytes = getChemistry(format);
		
		if (bytes == null) {
			throw new RuntimeException("Can't write chemistry at selected format");
		}
		
		outputStream.write(bytes);
	}

	public void edit() {
		// call listeners
		for (ChemistryEditorListener listener : listeners) {
			try {
				listener.editingStarted(new ChemistryEditorEvent());
			} catch (Throwable t) {}
		}

		// wait for exit
		new Thread(new Runnable() {
			public void run() {
				boolean result = false;
				
				try {
					result = callEditor();
				} catch (Exception e) {
					LOG.error("Error opening chemistry editor: ", e);
					JOptionPane.showMessageDialog(MasterController.getGuiComponent(), e.getMessage(), "Error executing Chemistry Editor", JOptionPane.ERROR_MESSAGE);
				}
				
				if (result) { // wait for editing finish
					// if object was modified
					
					// call listeners
					for (ChemistryEditorListener listener : listeners) {
						try {
							listener.structureChanged(new ChemistryEditorEvent());
						} catch (Throwable t) {}
					}						
				}
				// call listeners
				for (ChemistryEditorListener listener : listeners) {
					try {
						listener.editingStopped(new ChemistryEditorEvent());
					} catch (Throwable t) {}
				}
			}
		}).start();
	}
	
	public boolean setChemistry(byte[] chemistry, int format) {
		if (assignChemistry(chemistry, format)) {
			// chemistry successfully assigned
			
			// call listeners
			for (ChemistryEditorListener listener : listeners) {
				try {
					listener.structureChanged(new ChemistryEditorEvent());
				} catch (Throwable t) {}
			}
			
			return true;
		} else {
			// chemistry was not assigned
			return false;
		}
	}
	
	protected byte[] getFileContents(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		byte[] bytes = new byte[fis.available()];
		fis.read(bytes);
		fis.close();
		return bytes;
	}
	
	public void cut() {
		copy();
		setChemistry(new byte[] { ' ' }, ChemistryEditorProxy.RXN_FORMAT);
	}
	
	public void copy() {
		byte[] currentChemistry = this.getChemistry(ChemistryEditorProxy.RXN_FORMAT);
		if (currentChemistry == null) {
			currentChemistry = this.getChemistry(ChemistryEditorProxy.MOL_FORMAT);
		}
		
		StringSelection textTransfer = new StringSelection(new String(currentChemistry));		
    	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    	if (clipboard != null) {
    		clipboard.setContents(textTransfer, textTransfer);
    	}
	}
	
	public void paste() {
		Transferable transferable = null;
    	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    	if (clipboard != null) {
    		transferable = clipboard.getContents(null);
    		
    		if(transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        		try {
    	    		String data = (String)transferable.getTransferData(DataFlavor.stringFlavor);
    	    		this.setChemistry(data.getBytes(), ChemistryEditorProxy.RXN_FORMAT);
        		} catch(Exception e) {}
        	}
    	}
	}

	public Component getEditComponent() {
		return editComponent;
	}

	public void setEditComponent(Component editComponent) {
		this.editComponent = editComponent;
	}
	
	public void enableWindow() {
		Window disabledWindow = CeNGUIUtils.findWindow(getEditComponent());
		
		// Enable window
		disabledWindow.setEnabled(true);
		
		// Hack if toFront doesn't work
		disabledWindow.setAlwaysOnTop(true);
		disabledWindow.setAlwaysOnTop(false);
		
		// Standard way
		disabledWindow.toFront();
		disabledWindow.requestFocus();
	}
}
