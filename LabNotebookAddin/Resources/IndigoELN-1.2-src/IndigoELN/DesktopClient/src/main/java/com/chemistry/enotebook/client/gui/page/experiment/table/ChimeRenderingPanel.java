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
package com.chemistry.enotebook.client.gui.page.experiment.table;

import com.chemistry.*;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class ChimeRenderingPanel extends ChemistryPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5741491012742861910L;
	private ChemistryEditorProxy chemEditor;//private ExternalChemistryEditor chemEditor;// = new
	
	// ExternalChemistryEditor();
	/*
	 * File chemistry was last read from
	 */
	private File chemFile;
	private String fileFormat;
	private File lastDir;
	private JPopupMenu menu = new JPopupMenu();
	private JMenuItem copyItem = new JMenuItem("Copy Structure");
	private JMenuItem pasteItem = new JMenuItem("Paste Structure");
	private boolean menuIteams = true;
	// private Image image = null;
	private BufferedImage image = null;// = ImageIO.read(url);

	public ChimeRenderingPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public ChimeRenderingPanel(boolean menuIteams) {
		try {
			this.menuIteams = menuIteams;
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getDialogTitle() {
		// return "External Editor Component Example Application";
		return "CeN 1.2";
	}

	void jbInit() throws Exception {
		chemEditor = new KetcherProxy();
//		chemEditor.setApplicationName(getDialogTitle());
//		chemEditor.setObjectName("Synthesis Plan Hub Chemistry");
		// Listen for events (editor started, stopped, chemistry changed
		chemEditor.addChemistryEditorListener(new ExampleAppStructureListener());
		// enable double click and launch
		// chemEditor.setChemistry("");
//		chemEditor.setViewerComponent(this);
		DropTarget dt = new DropTarget(this, new ExampleAppDropTarget());
		this.setDropTarget(dt);
		// this.setBorder(BorderFactory.createLineBorder(Color.white));
		this.setOpaque(false);
		// setBackground(ColorScheme.LIGHT_BACKGROUND);
		menu.add(copyItem);
		menu.add(pasteItem);
		// Set the component to show the popup menu.
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				// //System.out.println("mousePressed");
				if (menuIteams && evt.isPopupTrigger() && getData().length() != 0) {
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}

			public void mouseReleased(MouseEvent evt) {
				// //System.out.println("mouseReleased");
				if (menuIteams && evt.isPopupTrigger() && getData().length() != 0) {
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}

			public void mouseClicked(MouseEvent evt) {
				// //System.out.println("mouseClicked");
				ChimeRenderingPanel.this.chemEditor.setChemistry(ChimeRenderingPanel.this.getData().getBytes(), ChemistryEditorProxy.RXN_FORMAT);
			}
		});
	}

	public void setRendererData(String str) {
		setRendererData(str, false);
	}

	public void setRendererData(String str, boolean setChemEdit) {
		if (str.lastIndexOf("M  END") > 3 || str.lastIndexOf("$END MOL") > 3 || str.startsWith("$RXN") || str.endsWith("$$$$")
				|| str.endsWith("$$$$\n")) {
			setData(str);
			if (setChemEdit) {
				chemEditor.setChemistry(str.getBytes(), ChemistryEditorProxy.RXN_FORMAT);
			}
		} else if (str.endsWith("\n") || str.endsWith("\r")) {
			str = str.substring(0, str.length() - 1);
			setData(str);
			if (setChemEdit) {
				chemEditor.setChemistry(str.getBytes(), ChemistryEditorProxy.RXN_FORMAT);
			}
		} else if ("".equals(str)) {
			// //System.out.println("Chime Pro with empty String");
			setData(str);
			if (setChemEdit) {
				chemEditor.setChemistry(str.getBytes(), ChemistryEditorProxy.RXN_FORMAT);
			}
		} else {
			setData(str);
			if (setChemEdit) {
				chemEditor.setChemistry(str.getBytes(), ChemistryEditorProxy.RXN_FORMAT);
			}
		}
		/*
		 * else { //System.out.println("Chime Pro with illegal format of ***" + str + "***"); }
		 */
	}

	public void setPropertiesToolTip(Hashtable hash) {
		if ((hash.isEmpty() == false) && (hash != null)) {
			String tip = "<html><em>";
			for (Enumeration e = hash.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String value = (String) hash.get(key);
				// todo temperary solution for block displaying
				if (!key.equalsIgnoreCase("RegisteredId") && !key.equalsIgnoreCase("Clipping_Id")) {
					tip = tip + "<p><b><font color=red>" + key + ":</font>   " + value + " </b></p>";
					// tip=tip+"<p>"+value+"</p>";
				}
			}
			tip = tip + "</em></html>";
			setToolTipText(tip);
		} else {
			setToolTipText(null);
		}
	}

	String lable = "";
	private java.util.Hashtable properties;

	public void setForeground(Color p0) {
		super.setForeground(p0);
	}

	public void setLabel(String s) {
		if (s == null)
			s = "";
		lable = s;
		repaint();
	}

	public void setImage(BufferedImage s) {
		// if (s == null) s = "";
		image = s;
		repaint();
	}

	public void paint(Graphics g) {
		try {
			super.paint(g);
			int x = 6, y = 12;
			g.setColor(this.getForeground());
			if (image != null) {
				// ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
				// ColorConvertOp op = new ColorConvertOp(cs, null);
				// image = op.filter(image, null);
				g.drawImage(image, x, y, this);
				x = x + 5;
			}
			g.drawString(lable, x, y);
		} catch (Exception ex) {
			// System.out.println("Java Chime Painting problem");
			// System.out.println(this.getMolfileData());
		}
	}

	public void doNew() {
		if (chemEditor.setChemistry(new byte[0], ChemistryEditorProxy.RXN_FORMAT)) {
			chemFile = null;
			// frame.setTitle(ExampleAppView.getDialogTitle());
			// frame.appendEvent(formatter.format(new Date()) + ": New
			// Chemistry");
		}
	}

	private void doOpenImpl(File chemFile) {
		// Read in the chemistry
		try {
			if (chemEditor.setChemistry(new FileInputStream(chemFile), ChemistryEditorProxy.RXN_FORMAT)) {
				// The chemistry has been updated.
				// Save the format
				// Do not save the file until a "save"
				chemFile = null;
//				fileFormat = chemEditor.getFormat();
				// frame.setTitle(ExampleAppView.getDialogTitle());
				// frame.appendEvent(formatter.format(new Date()) + ":
				// "+fileFormat+" File Opened");
			} else {
				// JOptionPane.showMessageDialog(frame, "Failed to open file
				// "+chemFile.getName());
			}
		} catch (IOException ioEx) {
			// JOptionPane.showMessageDialog(frame, "Error opening file
			// "+chemFile.getName()+": \n"+ioEx.getMessage());
		}
	}

	private class ExampleAppStructureListener implements ChemistryEditorListener {

		public void editingStarted(ChemistryEditorEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void structureChanged(ChemistryEditorEvent e) {
			updateChemistry();
			
		}

		public void editingStopped(ChemistryEditorEvent e) {
			// TODO Auto-generated method stub
			
		}
	}

	/**
	 * Reads the chemistry in a format, then returns the bytes back
	 */
	private byte[] readChemistry(int format) {
		byte[] res;
		ByteArrayOutputStream chemStream = new ByteArrayOutputStream();
		try {
			chemEditor.writeChemistry(chemStream, format);
			res = chemStream.toByteArray();
		} catch (IOException ioEx) {
			// This shouldn't happen when using a ByteArrayOutputStream
			System.err.println("Couldn't write chemistry to ByteArrayOutputStream");
			res = null;
		}
		if (res == null) {
			res = new byte[0];
		}
		return res;
	}

	private void updateChemistry() {
		// Get the chemistry as a string
		try {

			String chemString = new String(readChemistry(ChemistryEditorProxy.MOL_FORMAT));
			setRendererData(chemString);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private class ExampleAppDropTarget extends DropTargetAdapter {
		/**
		 * Checks to see that files are being dragged. Rejects the drag otherwise.
		 */
		public void dragEnter(DropTargetDragEvent e) {
			if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
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
					List files = (List) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					File f = (File) files.get(0);
					doOpenImpl(f);
				} catch (UnsupportedFlavorException flavEx) {
					// Won't happen - the flavour was justed checked
					// JOptionPane.showMessageDialog(frame, "Drop failed:
					// "+flavEx.getMessage());
				} catch (IOException ioEx) {
					// JOptionPane.showMessageDialog(frame, "Drop failed:
					// "+ioEx.getMessage());
				} finally {
					e.dropComplete(true);
				}
			}
		}
	}
}
