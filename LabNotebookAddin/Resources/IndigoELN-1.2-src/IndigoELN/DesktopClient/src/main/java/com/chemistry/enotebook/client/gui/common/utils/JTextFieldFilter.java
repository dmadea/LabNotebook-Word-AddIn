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
 * Created on Oct 25, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.client.gui.common.utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class JTextFieldFilter extends PlainDocument {
	
	private static final long serialVersionUID = 6102549093058636814L;
	
	public static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	public static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String ALPHA = LOWERCASE + UPPERCASE;
	public static final String NUMERIC = "0123456789";
	public static final String FLOAT = NUMERIC + ".";
	public static final String ALPHA_NUMERIC = ALPHA + NUMERIC;
	public static final String COMMA_SEPARATED_NUMBERS = NUMERIC + ",";
	protected String acceptedChars = null;
	protected boolean negativeAccepted = false;
	protected int maxLength = -1;

	public JTextFieldFilter() {
		this(ALPHA_NUMERIC);
	}

	public JTextFieldFilter(String acceptedchars) {
		acceptedChars = acceptedchars;
	}

	public JTextFieldFilter(String acceptedChars, int maxLength) {
		this.acceptedChars = acceptedChars;
		this.maxLength = maxLength;
	}
	
	public void setNegativeAccepted(boolean negativeaccepted) {
		if (acceptedChars.equals(NUMERIC) || acceptedChars.equals(FLOAT) || acceptedChars.equals(ALPHA_NUMERIC)) {
			negativeAccepted = negativeaccepted;
			acceptedChars += "-";
		}
	}
	
	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		if (str == null)
			return;
		if (acceptedChars.equals(UPPERCASE))
			str = str.toUpperCase();
		else if (acceptedChars.equals(LOWERCASE))
			str = str.toLowerCase();
		for (int i = 0; i < str.length(); i++) {
			if (acceptedChars.indexOf(String.valueOf(str.charAt(i))) == -1)
				return;
		}
		if (acceptedChars.equals(FLOAT) || (acceptedChars.equals(FLOAT + "-") && negativeAccepted)) {
			if (str.indexOf(".") != -1) {
				if (getText(0, getLength()).indexOf(".") != -1) {
					return;
				}
			}
		}
		if (negativeAccepted && str.indexOf("-") != -1) {
			if (str.indexOf("-") != 0 || offset != 0) {
				return;
			}
		}
		if (maxLength > 0 && super.getLength() >= maxLength) {
			return;
		}
		super.insertString(offset, str, attr);
	}
}
