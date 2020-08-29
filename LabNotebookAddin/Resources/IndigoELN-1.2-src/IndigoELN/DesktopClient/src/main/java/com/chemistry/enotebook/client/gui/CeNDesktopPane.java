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
 * Created on Sep 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.client.gui;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class CeNDesktopPane extends JDesktopPane {

	private static final long serialVersionUID = 7235588617361749785L;

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		String str;
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Font f_arial_12 = new Font("sans", Font.BOLD, 12);
		Font f_arial_12i = new Font("sans", Font.BOLD, 12);
		Font f_arial_16 = new Font("sans", Font.BOLD, 16);
		Font f_arial_36 = new Font("sans", Font.BOLD, 32);
		Font f_arial_48 = new Font("serif", Font.BOLD, 48);
		str = "Welcome to";
		g2.setPaint(Color.WHITE);
		g2.setFont(f_arial_36);
		FontMetrics fm36 = getFontMetrics(f_arial_36);
		g2.drawString(str, getWidth() - fm36.stringWidth(str) - 25, 200);
		str = CeNConstants.PROGRAM_NAME;
		g2.setPaint(Color.ORANGE);
		g2.setFont(f_arial_48);
		FontMetrics fm48 = getFontMetrics(f_arial_48);
		g2.drawString(str, getWidth() - fm48.stringWidth(str) - 25, 250);
		try {
			str = CeNSystemProperties.getRunMode() + " System " + MasterController.getVersionInfoAsString();
			g2.setPaint(Color.BLACK);
			g2.setFont(f_arial_12i);
			FontMetrics fm12 = getFontMetrics(f_arial_12i);
			g2.drawString(str, getWidth() - fm12.stringWidth(str) - 45, 280);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		// Set the starting position to draw
		int width = 450;
		int x = getWidth() - width;
		int y = 300;
		try {
			str = CeNSystemProperties.getSystemMessage();
			if (str != null && str.length() > 0) {
				AttributedString attributedString = new AttributedString(str);
				attributedString.addAttribute(TextAttribute.FONT, f_arial_12);
				attributedString.addAttribute(TextAttribute.FOREGROUND, Color.CYAN);
				// Get iterator for string
				AttributedCharacterIterator characterIterator = attributedString.getIterator();
				// Get font context from graphics
				FontRenderContext fontRenderContext = g2.getFontRenderContext();
				// Create measurer
				LineBreakMeasurer measurer = new LineBreakMeasurer(characterIterator, fontRenderContext);
				while (measurer.getPosition() < characterIterator.getEndIndex()) {
					TextLayout textLayout = measurer.nextLayout(width); // Get
					// line
					y += textLayout.getAscent(); // Move down to baseline
					textLayout.draw(g2, x, y); // Draw line
					y += textLayout.getDescent() + textLayout.getLeading(); // Move
					// down
					// to
					// top
					// of
					// next
					// line
				}
				y += 30;
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		try {
			str = CeNSystemProperties.getSystemAlertMessage();
			if (str != null && str.length() > 0) {
				AttributedString attributedString = new AttributedString(str);
				attributedString.addAttribute(TextAttribute.FONT, f_arial_16);
				attributedString.addAttribute(TextAttribute.FOREGROUND, Color.RED);
				// Get iterator for string
				AttributedCharacterIterator characterIterator = attributedString.getIterator();
				// Get font context from graphics
				FontRenderContext fontRenderContext = g2.getFontRenderContext();
				// Create measurer
				LineBreakMeasurer measurer = new LineBreakMeasurer(characterIterator, fontRenderContext);
				while (measurer.getPosition() < characterIterator.getEndIndex()) {
					TextLayout textLayout = measurer.nextLayout(width); // Get
					// line
					y += textLayout.getAscent(); // Move down to baseline
					textLayout.draw(g2, x, y); // Draw line
					y += textLayout.getDescent() + textLayout.getLeading(); // Move
					// down
					// to
					// top
					// of
					// next
					// line
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}
}
