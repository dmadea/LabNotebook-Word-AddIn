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

package com.chemistry.enotebook.client.gui.common.utils;

import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.ParentCompoundModel;
import com.chemistry.enotebook.experiment.datamodel.compound.Compound;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.Decoder;
import com.chemistry.viewer.ReactionViewer;
import info.clearthought.layout.TableLayout;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CeNGUIUtils {
	
	private static final Log log = LogFactory.getLog(CeNGUIUtils.class);
	
	private static final BufferedImage emptyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB); 

	public static final double FILL = TableLayout.FILL; // f - FILL
	
	public static final double PREF = TableLayout.PREFERRED; // p - PREFERRED
	
	public static final double MIN = TableLayout.MINIMUM;

	public static final double BORDER = 10;     // b - border

	public static final double VERT_SPACE = 2;  // vertical space between labels and text fields
	
	public static final double VERT_GAP = 5;    // vertical gap between form elements
	
	public static final double HORIZ_SPACE = 2; // horizontal space between labels and text fields
	
	public static final double HORIZ_GAP = 5;   // horizontal gap between form elements

	private static boolean styleIsValid(int style) {
		if (style == Font.BOLD || style == Font.ITALIC || style == Font.PLAIN)
			return true;
		return false;
	}

	/**
	 * Returns empty image with height=1 and width=1
	 */
	public static Image getEmptyImage() {
		return emptyImage;
	}
	
	/**
	 * Allows you to change the component's text style. Choices are: Font.BOLD,
	 * Font.ITALIC or Font.PLAIN
	 * 
	 * @param style
	 *            - integer value of above choices. All others are ignored
	 * @param comp
	 *            - the component to change.
	 */
	public static void styleComponentText(int style, Component comp) {
		// Need to bold the headers
		Font ft = comp.getFont();
		if (ft.getStyle() != style && styleIsValid(style)) {
			comp.setFont(ft.deriveFont((int)style));
		}
	}

	/**
	 * Allows you to change the size of the font if it isn't already that size.
	 * Does not affect any other feature of the font for the component and is
	 * useful when trying to alter some default set by the Look & Feel
	 * 
	 * @param size
	 *            - integer value for size.
	 * @param comp
	 *            - component to alter
	 */
	public static void sizeComponentText(int size, Component comp) {
		// Need to bold the headers
		Font ft = comp.getFont();
		if (size != ft.getSize()) {
			comp.setFont(ft.deriveFont((float)size));
		}
	}

	/**
	 * Given a bytes to write, this method attempts to create a temp file and
	 * write the contents of the byte array to the file. Upon success the
	 * absolute path of the created file is returned. Otherwise a null string is
	 * returned.
	 * 
	 * @param picBytes
	 *            - byte[] representing the image file.
	 * @return absolute path to the created file on success, or null string on
	 *         failure.
	 */
	public static String writeImageToTempFile(byte[] picBytes) {
		String result = "";
		if (picBytes != null && picBytes.length > 0) {
			try {
				String TempDirectory = GetEnv.getProperties().getProperty("TEMP");
				File jpgFile = File.createTempFile(CeNConstants.PROGRAM_NAME, ".jpg", new File(TempDirectory));
				jpgFile.deleteOnExit();
				java.io.FileOutputStream jpgOS = new java.io.FileOutputStream(jpgFile);
				jpgOS.write(picBytes);
				jpgOS.flush();
				jpgOS.close();
				result = jpgFile.getAbsolutePath();
			} catch (FileNotFoundException e2) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, "CeNGUIUtils.writeImageToFile: failed. Cannot create/access file for write", e2);
			} catch (IOException e3) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, "CeNGUIUtils.writeImageToFile: failed. Could not write to file", e3);
			} catch (Exception e4) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, "CeNGUIUtils.writeImageToFile: failed. Unanticipated failure", e4);
			}
		}
		return result;
	}
		
	/**
	 * Formats: Encoded string sketch (Encoded MolFile and ChimeString), Native sketch (Isis draw, Chem draw)
	 */
	public static Image createImageFromSketch(byte[] sketch, int width, int height) {
		try {
			ReactionViewer viewer = new ReactionViewer("createImageFromSketchViewer", "", ReactionViewer.VIEW_NATIVE);
			if (viewer != null && !ArrayUtils.isEmpty(sketch) && width > 0 && height > 0) {
//				ImageData imagedata = viewer.getWriteOptions();
//				imagedata.setWidth(new Integer(width));
//				imagedata.setHeight(new Integer(height));
//				viewer.setWriteOptions(imagedata);
				
				String sketchString = new String(sketch);
				String decoded = Decoder.decodeString(sketchString);
				if (!StringUtils.equals(sketchString, decoded)) {
					ChemistryPanel cps = new ChemistryPanel();
					if (StringUtils.indexOf(decoded, "M  END") >= 0 || CommonUtils.isNull(decoded)) {
						cps.setMolfileData(decoded);
					} else {
						throw new UnsupportedOperationException("Supported only operations with mol or rxn file format");
					}
					viewer.setChemistry(cps.getMolfileData().getBytes());
				} else {
					viewer.setNativeSketch(sketch);
				}
				
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = image.createGraphics();
				JWindow x = new JWindow();
				x.getContentPane().add(viewer, BorderLayout.CENTER);
				x.invalidate();
				x.pack();
				viewer.setSize(width, height);
				viewer.paint(g);
				g.dispose();
				x.dispose();
				viewer.dispose();
				return image;
			}	        
		} catch (Exception e) {
			log.error("Error creating image: ", e);
		}
		return null;
	}
		
	/**
	 * Generate a HTML tool tip displaying the compound structure
	 * 
	 * @param cp
	 *            - compound to create the toolTip for
	 * @return String = the toolTip text to use to display or null if one can't
	 *         be generated.
	 */
	public static String getCompoundToolTipText(Compound cp) {
		StringBuffer newTip = new StringBuffer();
		if (cp != null && cp.getViewSketch() != null) {
			newTip.append(CreateToolTip(cp));
		} else {
			// newTip.append("Compound Not found for batch: " +
			// cp.getKey());
		}
		return (newTip.length() > 0) ? newTip.toString() : null;
	}

	private static String CreateToolTip(Compound cp) {
		StringBuffer buff = new StringBuffer();
		if (cp.getViewSketch() != null && cp.getViewSketch().length > 0) {
			buff.append("<HTML><SPAN style='font-size:7.0pt;font-family:Sansserif'>");
			if (cp.getChemicalName() != null && cp.getChemicalName().length() > 0)
				buff.append("Chemical Name = " + cp.getChemicalName() + "<BR>");
			else if (cp.getRegNumber() != null && cp.getRegNumber().length() > 0)
				buff.append("Compound ID = " + cp.getRegNumber() + "<BR>");
			else if (cp.getCASNumber() != null && cp.getCASNumber().length() > 0)
				buff.append("CASNumber = " + cp.getCASNumber() + "<BR>");
			else if (cp.getMolFormula() != null && cp.getMolFormula().length() > 0)
				buff.append("Formula = " + cp.getMolFormula() + "<BR>");
			boolean addPunc = false;
			byte[] imageBuf = cp.getViewSketch();
			if (imageBuf != null && imageBuf.length > 0) {
				StringBuffer fileURL = new StringBuffer(CeNGUIUtils.writeImageToTempFile(imageBuf));
				if (fileURL.length() > 0) {
					if (addPunc)
						buff.append(".<BR>");
					buff.append("<IMG SRC=file:" + fileURL + ">");
				} else {
					if (addPunc)
						buff.append(".");
					buff.append("No Image available");
				}
			} else {
				if (addPunc)
					buff.append(".");
				buff.append("No Image available");
			}
			buff.append("</SPAN></HTML>");
		}
		return buff.toString();
	}

	public static String getCompoundToolTipText(ParentCompoundModel compound) {
		StringBuffer newTip = new StringBuffer();
		if (compound != null && compound.getViewSketch() != null) {
			newTip.append(CreateToolTip(compound));
		} else {
			// newTip.append("Compound Not found for batch: " +
			// cp.getKey());
		}
		return (newTip.length() > 0) ? newTip.toString() : null;
	}

	private static String CreateToolTip(ParentCompoundModel cp) {
		StringBuffer buff = new StringBuffer();
		if (cp.getViewSketch() != null && cp.getViewSketch().length > 0) {
			buff.append("<HTML><SPAN style='font-size:7.0pt;font-family:Sansserif'>");
			if (cp.getChemicalName() != null && cp.getChemicalName().length() > 0)
				buff.append("Chemical Name = " + cp.getChemicalName() + "<BR>");
			else if (cp.getRegNumber() != null && cp.getRegNumber().length() > 0)
				buff.append("Compound ID = " + cp.getRegNumber() + "<BR>");
			else if (cp.getCASNumber() != null && cp.getCASNumber().length() > 0)
				buff.append("CASNumber = " + cp.getCASNumber() + "<BR>");
			else if (cp.getMolFormula() != null && cp.getMolFormula().length() > 0)
				buff.append("Formula = " + cp.getMolFormula() + "<BR>");
			boolean addPunc = false;
			byte[] imageBuf = cp.getViewSketch();
			if (imageBuf != null && imageBuf.length > 0) {
				StringBuffer fileURL = new StringBuffer(CeNGUIUtils.writeImageToTempFile(imageBuf));
				if (fileURL.length() > 0) {
					if (addPunc)
						buff.append(".<BR>");
					buff.append("<IMG SRC=file:" + fileURL + ">");
				} else {
					if (addPunc)
						buff.append(".");
					buff.append("No Image available");
				}
			} else {
				if (addPunc)
					buff.append(".");
				buff.append("No Image available");
			}
			buff.append("</SPAN></HTML>");
		}
		return buff.toString();
	}
	
	public static JComponent createGlassPane() {
		return new JComponent() {
			private static final long serialVersionUID = 6020359079084595276L;
			public void paintComponent(Graphics g) {
				g.setColor(new Color(0, 0, 0, 100));
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
	}
	
	public static Window findWindow(Component c) {
		Component parent = c;
		while (!(parent instanceof Window))
			parent = parent.getParent();
		return (Window) parent;
	}
}
