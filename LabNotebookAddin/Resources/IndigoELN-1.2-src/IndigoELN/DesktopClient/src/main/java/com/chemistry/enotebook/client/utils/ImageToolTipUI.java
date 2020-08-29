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
package com.chemistry.enotebook.client.utils;

/* 
 * This example is from javareference.com 
 * for more information visit, 
 * http://www.javareference.com 
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.plaf.metal.MetalToolTipUI;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This class extends MetalToolTipUI and provides customizes it to draw a given
 * image on it.
 * 
 */
public class ImageToolTipUI extends MetalToolTipUI {
	private Image m_image;

	private String[] strs;

	private static Log log = LogFactory.getLog(ImageToolTipUI.class.getName());

	public ImageToolTipUI(Image image) {
		if(image != null) {
			m_image = image;	
		} else {
			m_image = new ImageIcon().getImage();
		}
	}

	/**
	 * This method is overriden from the MetalToolTipUI to draw the given image
	 * and text
	 * 
	 */
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);
		FontMetrics metrics = c.getFontMetrics(g.getFont());

		Dimension size = c.getSize();
		g.setColor(c.getBackground());
		g.fillRect(0, 0, size.width, size.height);
		g.setColor(c.getForeground());

		if (strs != null) {
			for (int i = 0; i < strs.length; i++) {
				g.drawString(strs[i], 3, (metrics.getHeight()) * (i + 1));
			}
			g.drawImage(m_image, 3, metrics.getHeight() * (strs.length + 1), c);
		}
	}

	/**
	 * This method is overriden from the MetalToolTipUI to return the appropiate
	 * preferred size to size the ToolTip to show both the text and image.
	 */
	public Dimension getPreferredSize(JComponent c) {
		FontMetrics metrics = c.getFontMetrics(c.getFont());
		String tipText = ((JToolTip) c).getTipText();

		if (tipText == null) {
			tipText = "";
		}
		BufferedReader br = new BufferedReader(new StringReader(tipText));
		String line;
		int maxWidth = 0;
		Vector v = new Vector();
		try {
			while ((line = br.readLine()) != null) {
				int width = SwingUtilities.computeStringWidth(metrics, line);
				maxWidth = (maxWidth < width) ? width : maxWidth;
				v.addElement(line);
			}
		} catch (IOException ex) {
			log.error(ex);
		}
		int lines = v.size();
		if (lines < 1) {
			strs = null;
			lines = 1;
		} else {
			strs = new String[lines];
			int i = 0;
			for (Enumeration e = v.elements(); e.hasMoreElements(); i++) {
				strs[i] = (String) e.nextElement();
			}
		}
		int height = metrics.getHeight() * lines + m_image.getHeight(c) + 6;
		// this.maxWidth = maxWidth;
		maxWidth += 6;

		// int width = SwingUtilities.computeStringWidth(metrics, tipText);
		// int height = metrics.getHeight() + m_image.getHeight(c) + 6;

		if (maxWidth < m_image.getWidth(c)) {
			maxWidth = m_image.getWidth(c);
		}

		return new Dimension(maxWidth, height);
	}
}
