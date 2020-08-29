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

package com.chemistry.viewer;

import com.chemistry.*;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.List;
import java.util.Set;


public class ChemistryViewer extends JPanel {
	
	private static final long serialVersionUID = -3338415086730024582L;
	
	public static final int NO_EDITOR         = -1;
	public static final int ISISDRAW_EDITOR   = 1;
	public static final int KETCHER_EDITOR    = 2;
	//public static final int CHEMDRAW_EDITOR   = 3;
	
	private int editorType = NO_EDITOR;
    private DataFlavor CXFFlavor = new DataFlavor("chemical/x-cxf", "CXF");
    private DataFlavor ChemDrawInterchangeFlavor = new DataFlavor("chemical/x-cdx", "ChemDraw Interchange Format");
    private DataFlavor MDLSKFlavor = new DataFlavor("chemical/x-mdl-sketch", "MDLSK");
    private DataFlavor MDLCTFlavor = new DataFlavor("chemical/x-mdl-molfile", "MDLCT");

    protected ChemistryEditorProxy chemEditor;	// Interface to communicate with native Chemistry Editors

    private KetcherProxy ketcherEditor;
    private ISISDrawProxy isisEditor;
    
    private byte[] currentChemistry = null;				// Current raw chemistry currently being displayed
	
    protected JPopupMenu popupMenu = null;
	private Action menuItemEdit = null;
	private Action cutItem = null;
	private Action copyItem = null;
	private Action pasteItem = null;
	
	private Color backgroundColor = Color.WHITE;
	
	private MouseListener editorListener = null;
	
	private ChemistryPanel viewer;
	
	private String title;
	private String content;
	
	public ChemistryViewer(String title, String content) {
		this(title, content, getCurrentEditor());
	}
	
	public ChemistryViewer(String title, String content, int editorType) {
		super();
		this.setTitle(title);
		this.setContent(content);
		this.setEditorType(editorType);
		
		chemEditor.setEditComponent(this);
		chemEditor.setInfo(content);
		chemEditor.addChemistryEditorListener(new ChemistryEditorListener() {
		    public void editingStarted(ChemistryEditorEvent e) { 
		    	setBackground(Color.decode("0xE0E0E0")); 
		    }
			public void structureChanged(ChemistryEditorEvent e) {
				updateChemistry(); 
			}
			public void editingStopped(ChemistryEditorEvent e) { 
		    	ChemistryViewer.this.setBackground(backgroundColor); 
		    	ChemistryViewer.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});

		viewer = new ChemistryPanel();
        setLayout(new FlowLayout());
        add(viewer);
        
		// Setup popup Menu 
		popupMenu = createPopupMenu();

		// Workaround to allow for readonly to work.
        editorListener = new MouseAdapter() {
        	        	
        	@Override
			public void mouseClicked(MouseEvent e) {
				if (!isReadOnly() && SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
					setEditorType(getCurrentEditor());
					chemEditor.edit();
				}
			}

        	@Override
        	public void mousePressed(MouseEvent e) {
        		showPopupMenu(e);
        	}
        	
        	@Override
        	public void mouseReleased(MouseEvent e) {
        		showPopupMenu(e);
        	}
        	
			private void showPopupMenu(MouseEvent e) {
				if (e.isPopupTrigger()) {
					checkMenuItems();
					popupMenu.show(e.getComponent(), e.getX(), e.getY());					
				}
			}
		};
		
		addMouseListener(editorListener);
      
        // Add to the chemisty viewer event map
// FIX        
//        registerKeyboardAction(chemEditor.getEditAction(), KeyStroke.getKeyStroke("ctrl E"), JComponent.WHEN_IN_FOCUSED_WINDOW);
//        registerKeyboardAction(chemEditor.getCopyAction(), KeyStroke.getKeyStroke("ctrl C"), JComponent.WHEN_IN_FOCUSED_WINDOW);
//        registerKeyboardAction(chemEditor.getCutAction(), KeyStroke.getKeyStroke("ctrl X"), JComponent.WHEN_IN_FOCUSED_WINDOW);
//        registerKeyboardAction(chemEditor.getPasteAction(""), KeyStroke.getKeyStroke("ctrl V"), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		// Create and set a drop target to listen for file drops
		DropTarget dt = new DropTarget(this, new AppDropTarget());
		this.setDropTarget(dt);
	
		// Add flavors for determining if Chemistry is on clipboard
        SystemFlavorMap systemflavormap = (SystemFlavorMap)SystemFlavorMap.getDefaultFlavorMap();
        systemflavormap.addFlavorForUnencodedNative("CXF", CXFFlavor);
        systemflavormap.addUnencodedNativeForFlavor(CXFFlavor, "CXF");
        systemflavormap.addFlavorForUnencodedNative("ChemDraw Interchange Format", ChemDrawInterchangeFlavor);
        systemflavormap.addUnencodedNativeForFlavor(ChemDrawInterchangeFlavor, "ChemDraw Interchange Format");
        systemflavormap.addFlavorForUnencodedNative("MDLSK", MDLSKFlavor);
        systemflavormap.addUnencodedNativeForFlavor(MDLSKFlavor, "MDLSK");
        systemflavormap.addFlavorForUnencodedNative("MDLCT", MDLCTFlavor);
        systemflavormap.addUnencodedNativeForFlavor(MDLCTFlavor, "MDLCT");
    }

	public void setBackground(Color color) {
		super.setBackground(color);
        if (viewer != null) 
        	viewer.setBackground(color);
	}
	
	public void dispose() 
	{
		if (chemEditor != null) {
			popupMenu = null;
			currentChemistry = null;
			//chemEditor.setViewerComponent(null);
			chemEditor = null;
		}
	}
	
	public void setSize(int width, int height) {
		super.setSize(width, height);
		viewer.setSize(width, height);
	}
	
    public JPopupMenu getPopupMenu() {
    	return popupMenu;
    }
    
    protected JPopupMenu createPopupMenu() {
		JPopupMenu popup = new JPopupMenu();
		
		menuItemEdit = new AbstractAction("Edit") {
			private static final long serialVersionUID = 8757681750037591940L;
			public void actionPerformed(ActionEvent e) {
				edit();
			}
		};
		menuItemEdit.putValue("Name", "Edit");
		menuItemEdit.putValue("ShortDescription", "Edit");
		menuItemEdit.putValue("LongDescription", "Edit chemistry with the current editor");
		menuItemEdit.putValue("AcceleratorKey", KeyStroke.getKeyStroke("ctrl E"));
		menuItemEdit.putValue("MnemonicKey", new Integer(69));

		cutItem = new AbstractAction("Cut") {
			private static final long serialVersionUID = 8080765279379427491L;
			public void actionPerformed(ActionEvent e) {
				cut();
			}
		};
		cutItem.putValue("Name", "Cut");
		cutItem.putValue("ShortDescription", "Cut");
		cutItem.putValue("LongDescription", "Cut the current chemistry to the clipboard");
	    cutItem.putValue("AcceleratorKey", KeyStroke.getKeyStroke("ctrl X"));
	    cutItem.putValue("MnemonicKey", new Integer(84));

	    copyItem = new AbstractAction("Copy") {
			private static final long serialVersionUID = 7529160242383504474L;
			public void actionPerformed(ActionEvent e) {
				copy();
			}
		};
		copyItem.putValue("Name", "Copy");
		copyItem.putValue("ShortDescription", "Copy");
		copyItem.putValue("LongDescription", "Copy the current chemistry to the clipboard");
		copyItem.putValue("AcceleratorKey", KeyStroke.getKeyStroke("ctrl C"));
		copyItem.putValue("MnemonicKey", new Integer(67));

		pasteItem = new AbstractAction("Paste") {
			private static final long serialVersionUID = 4098178067771076902L;
			public void actionPerformed(ActionEvent e) {
				paste();
			}
		};
		pasteItem.putValue("Name", "Paste");
		pasteItem.putValue("ShortDescription", "Paste");
		pasteItem.putValue("LongDescription", "Paste chemistry from the clipboard");
		pasteItem.putValue("AcceleratorKey", KeyStroke.getKeyStroke("ctrl V"));
		pasteItem.putValue("MnemonicKey", new Integer(80));

	    menuItemEdit.putValue("Name", "Edit/Add Reaction Scheme");
		cutItem.putValue("Name", "Cut Reaction Scheme");
		copyItem.putValue("Name", "Copy Reaction Scheme");
		pasteItem.putValue("Name", "Paste Reaction Scheme");
		
		popup.add(menuItemEdit);
		popup.add(cutItem);
		popup.add(copyItem);
		popup.add(pasteItem);
		
		return popup;
	}

	protected void checkMenuItems() {
		if (cutItem == null || copyItem == null || pasteItem == null)
			return;
		
		cutItem.setEnabled(!isReadOnly() && currentChemistry != null && currentChemistry.length > 0);
		copyItem.setEnabled(currentChemistry != null && currentChemistry.length > 0);
		
		if (isReadOnly())
			pasteItem.setEnabled(false);
		else
	        pasteItem.setEnabled(isValidChemistryOnClipboard());
		
		if (menuItemEdit != null)
			menuItemEdit.setEnabled(!isReadOnly());
	}
	
	private static int getCurrentEditor() {
		try {
			String editor = MasterController.getUser().getPreference(NotebookUser.PREF_DEFAULT_EDITOR);
			return NotebookUser.ISIS_DRAW.equals(editor) ? ISISDRAW_EDITOR : KETCHER_EDITOR;
		} catch (UserPreferenceException e) {
			// do nothing
		}
		return KETCHER_EDITOR;
	}
	
	private boolean isDataFlavorSupported(Object o) {
		if (o == null) return false;
		if (o instanceof Transferable) {
			Transferable t = (Transferable) o;
	        return (t.isDataFlavorSupported(CXFFlavor) ||
				    t.isDataFlavorSupported(ChemDrawInterchangeFlavor) ||
				    t.isDataFlavorSupported(MDLSKFlavor) ||
				    t.isDataFlavorSupported(MDLCTFlavor) ||
				    t.isDataFlavorSupported(DataFlavor.imageFlavor));
		} else if (o instanceof DropTargetDragEvent) {
			DropTargetDragEvent e = (DropTargetDragEvent) o;
	        return (e.isDataFlavorSupported(CXFFlavor) ||
				    e.isDataFlavorSupported(ChemDrawInterchangeFlavor) ||
				    e.isDataFlavorSupported(MDLSKFlavor) ||
				    e.isDataFlavorSupported(MDLCTFlavor) ||
				    e.isDataFlavorSupported(DataFlavor.imageFlavor));
		} else
			return false;
	}
	
	public boolean isValidChemistryOnClipboard() {
    	Transferable transferable = null;
    	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    	if (clipboard != null) transferable = clipboard.getContents(null);
    	
    	boolean isRxnString = false;
    	if(transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
    		try {
	    		String data = (String)transferable.getTransferData(DataFlavor.stringFlavor);
	    		if(data.contains("M  END")) {
	    			isRxnString = true;
	    		}
    		} catch(Exception e) {}
    	}
    	return isDataFlavorSupported(transferable) || isRxnString;
	}
	
	public void setPreferredBackgroundColor(Color val) {
		backgroundColor = val;
		setBackground(val);
	}
	
	public boolean isReadOnly() {
		return !isEnabled();
	}
	
	public void setReadOnly(boolean flag) { 
		if (!isEnabled() != flag) {
			setEnabled(!flag); 
//			if (!flag) {
//				addMouseListener(editorListener);
//			} else {
//				removeMouseListener(editorListener);
//			}
		}
	}
	
	/**
	 * gets the chemistry in a format, then returns the bytes back
	 */
	public byte[] getChemistry() {
		byte[] result = chemEditor.getChemistry(ChemistryEditorProxy.RXN_FORMAT);
		if (result == null || result.length == 0) {
			result = chemEditor.getChemistry(ChemistryEditorProxy.MOL_FORMAT);
		}
		return result;
	}
	
	public byte[] getChemistry(int format) {
		byte[] data = null;
		
		ByteArrayOutputStream chemStream = new ByteArrayOutputStream();
		try {
			chemEditor.writeChemistry(chemStream, format);
			data = chemStream.toByteArray();
		} catch (IOException ioEx) { /* shouldn't happen */ }
		
		if (data == null) data = new byte[0];
		
		return data;
	}
	
	/**
	 * Sets the chemistry
	 */
	public void setChemistry(byte[] data) {
		if (data == null) 
			data = new byte[0];	
		
		ByteArrayInputStream chemStream = new ByteArrayInputStream(data);
		
		try {
			chemEditor.setChemistry(chemStream, ChemistryEditorProxy.RXN_FORMAT);
		} catch (IOException ioEx) {
			/* shouldn't happen */ 
		}
	}
	
	/**
	 * Sets native sketch
	 */
	public void setNativeSketch(byte[] sketch) {
		if (sketch == null)
			sketch = new byte[0];
		
		chemEditor.setChemistry(sketch, ISISDrawProxy.RXN_FORMAT);
	}
	
	
	public boolean readChemistry(InputStream is) throws IOException	{
		return chemEditor.setChemistry(is, ChemistryEditorProxy.RXN_FORMAT);
	}

    public void writeChemistry(OutputStream os, int format) throws IOException {
        chemEditor.writeChemistry(os, format);
    }
	
	/**
	 * Updates chemistry view
	 */
	public void updateChemistry() {
		currentChemistry = getChemistry();
		if (currentChemistry != null && currentChemistry.length > 0) {
			viewer.setData(currentChemistry);
		}
	}
	
	/*
	 * Helper functions to check for and set the editor
	 */
	public boolean isEditorPresent(String editor) {
		return true;
		//return (editor == null || editor.length() == 0) ? false : chemEditor.isEditorPresent(editor);
	}
	
	public int getEditorType() {
		return editorType;
	}
	
	public void setEditorType(int type) { 
		editorType = type;
		
		if (ketcherEditor == null)
			ketcherEditor = new KetcherProxy();
		
		// ISIS/Draw is windows only
		if (isisEditor == null && MasterController.isRunningOnWindows())
			isisEditor = new ISISDrawProxy();
		
		ChemistryEditorProxy oldEditor = chemEditor;
		
		switch (type) {
		case KETCHER_EDITOR:
			chemEditor = ketcherEditor;
			break;
		case ISISDRAW_EDITOR:
			// ISIS/Draw is windows only
			if (MasterController.isRunningOnWindows())
				chemEditor = isisEditor;
			else
				chemEditor = ketcherEditor;
			break;
		default:
			break;
		}
		
		if (oldEditor != null) {
			chemEditor.setInfo(oldEditor.getInfo());
			chemEditor.setEditComponent(oldEditor.getEditComponent());
			chemEditor.setChemistry(oldEditor.getChemistry(ChemistryEditorProxy.RXN_FORMAT), ChemistryEditorProxy.RXN_FORMAT);
			
			Set<ChemistryEditorListener> listeners = oldEditor.getListeners();
			for (ChemistryEditorListener listener : listeners)
				chemEditor.addChemistryEditorListener(listener);
		}
	}
	
//	public String getEditor() {
//		return chemEditor.getEditor();
//	}
//	
//	public String getChemistryFormat() {
//		return chemEditor.getFormat();
//	}
	
//	public ImageData getWriteOptions() {
//		return currentWriteOptions;
//	}
	
//    public void setWriteOptions(ImageData options) {
//    	if (options == null) {
//			options = new ImageData(new Integer(getWidth()), new Integer(getHeight()));
//    		options.setShowReactionMapNumbers(Boolean.TRUE);
//    		options.setShowQueryAttributes(Boolean.FALSE);
//    		options.setShowCIP(Boolean.TRUE);
//    		options.setShowHydrogens(Boolean.FALSE);
//    	}
//    	currentWriteOptions = options;
//    	//chemEditor.setWriteOptions(options);
//    	updateChemistry();
//    }

    public void cut() {
    	chemEditor.cut();
    }

    public void copy() {
    	chemEditor.copy();
    }
    
	public void paste() {
		if(isValidChemistryOnClipboard()) {
			chemEditor.paste();
		}
	}

	public void edit() {
		setEditorType(getCurrentEditor());
		chemEditor.edit();
	}

//	public Action getEditAction() {
//		return chemEditor.getEditAction();
//	}
//
//	public Action getCutAction() {
//		return chemEditor.getCutAction();
//	}
//
//	public Action getCopyAction() {
//		return chemEditor.getCopyAction();
//	}
//
//	public Action getPasteAction() {
//		return chemEditor.getPasteAction("");
//	}

	/**
	 * Builds up a popup menu with actions from the external editor component
	 */
//	private void addPopupActions(JPopupMenu editMenu) {
//		editMenu.add(chemEditor.getCutAction());
//		editMenu.add(chemEditor.getCopyAction());
//		// Empty string passed as format so that format on clipboard is used
//		editMenu.add(chemEditor.getPasteAction(""));
//	}
//
//	private Action getIsisDrawAction() {
//		Action res = new AbstractAction() {
//			public void actionPerformed(ActionEvent e) {
//				setEditor(ISISDRAW_EDITOR);
//			}
//		};
//		res.putValue(Action.NAME, "Use ISIS/Draw");
//		res.putValue(Action.SHORT_DESCRIPTION, "Use ISIS/Draw");
//		res.putValue(Action.LONG_DESCRIPTION, "Use ISIS/Draw for editing");
//		return res;
//	}
//
//	private Action getChemDrawAction() {
//		Action res = new AbstractAction() {
//			public void actionPerformed(ActionEvent e) {
//				setEditor(CHEMDRAW_EDITOR);
//			}
//		};
//		res.putValue(Action.NAME, "Use ChemDraw");
//		res.putValue(Action.SHORT_DESCRIPTION, "Use ChemDraw");
//		res.putValue(Action.LONG_DESCRIPTION, "Use ChemDraw for editing");
//		return res;
//	}

	public void addChemistryEditorListener(
			ChemistryEditorListener listener) {
		chemEditor.addChemistryEditorListener(listener);
	}

	public void removeChemistryEditorListener(
			ChemistryEditorListener listener) {
		chemEditor.removeChemistryEditorListener(listener);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	/*
	 * Listener for drag and drop operations
	 */
	private class AppDropTarget extends DropTargetAdapter {
		/**
		 * Checks to see that files are being dragged. Rejects the drag
		 * otherwise.
		 */
		public void dragEnter(DropTargetDragEvent e) {
			if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				e.acceptDrag(DnDConstants.ACTION_COPY);
			} else if (isDataFlavorSupported(e)) {
				e.acceptDrag(DnDConstants.ACTION_COPY);
			} else {
				e.rejectDrag();
			}
		}

		/**
		 * Checks to see that files are being dragged and opens the first one.
		 */
		public void drop(DropTargetDropEvent e) {
			if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				e.acceptDrop(DnDConstants.ACTION_COPY);
				try {
					List files = (List) e.getTransferable().getTransferData(
							DataFlavor.javaFileListFlavor);
					File f = (File) files.get(0);
					doOpenImpl(f);
				} catch (UnsupportedFlavorException flavEx) {
					// Won't happen - the flavour was justed checked
					JOptionPane.showMessageDialog(null, "Drop failed: "
							+ flavEx.getMessage());
				} catch (IOException ioEx) {
					JOptionPane.showMessageDialog(null, "Drop failed: "
							+ ioEx.getMessage());
				} finally {
					e.dropComplete(true);
				}
			}
		}

		private void doOpenImpl(File chemFile) {
			try {
				if (!readChemistry(new FileInputStream(chemFile)))
					JOptionPane.showMessageDialog(null, "Failed to open file "
							+ chemFile.getName());
			} catch (IOException ioEx) {
				JOptionPane.showMessageDialog(null, "Error opening file "
						+ chemFile.getName() + ": \n" + ioEx.getMessage());
			}
		}
	}
}

//class SVGViewer extends JPanel {
//	private Image image;
//	
//	void setImage(Image image) {
//		this.image = image;
//		repaint();
//	}
//	
//	@Override
//	public Dimension getPreferredSize() {
//		Container parent = getParent();
//		Dimension size = parent.getSize();
//		size.width -= 5;
//		size.height -= 5;
//		
//		Dimension parentParentSize = parent.getParent().getSize();		
//		int maxWidth = parentParentSize.width - 5;
//		int maxHeight = parentParentSize.height - 5;
//		
//		if (size.width > maxWidth) {
//			size.width = Math.max(0, maxWidth);
//		}
//		if (size.height > maxHeight) {
//			size.height = Math.max(0, maxHeight);
//		}
//
//		return size;
//	}
//	
//	public void paintComponent(java.awt.Graphics graphics) {
//		super.paintComponent(graphics);
//		if (image != null) {
//			graphics.drawImage(image, 0, 0, null);
//		}
//	}
//}
