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
 * Created on Sep 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.utils;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class NumberTextField extends JTextField implements FocusListener {

	private static final long serialVersionUID = 8110761992997008909L;
	
//	private boolean inUse_ = false;
	private boolean floatAllowed_ = false;
	private boolean doubleAllowed_ = false;
	private boolean longAllowed_ = false;
	private boolean isNegativeAllowed_ = true;

	public NumberTextField() {
		super();
		addFocusListener(this);
		addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				//System.out.println("keyTyped: action key: " + e.isActionKey() + ", keyCode: " + e.getKeyCode());
				if (e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == KeyEvent.VK_TAB) {
					handleDanglingDot();
				}
			}

			// KeyPressed Handles the ESCAPE Key
			public void keyPressed(KeyEvent e) {
				//System.out.println("keyPressed: action key: " + e.isActionKey() + ", keyCode: " + e.getKeyCode());
				if (e.getKeyCode() == KeyEvent.VK_ENTER)  // vb 1/22 to not move selection down in the tables
					e.consume();
				if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
					// handle Escape
				}
			}
		});
	}

	public void setToFloat(boolean flag) {
		floatAllowed_ = flag;
	}

	public void setToDouble(boolean flag) {
		doubleAllowed_ = flag;
	}

	public void setToLong(boolean flag) {
		longAllowed_ = flag;
	}

	public void setNegativeAllowed(boolean flag) {
		isNegativeAllowed_ = flag;
	}

	public int getNumber() {
		int number = 0;
		try {
			number = Integer.parseInt(getText());
		} catch (Exception error) {
			return number;
		}
		return number;
	}

	public float getFloatValue() {
		float floatValue = (float) 0.0;
		try {
			floatValue = Float.parseFloat(getText());
			;
		} catch (Exception error) {
			return floatValue;
		}
		return floatValue;
	}

	public double getDoubleValue() {
		double doubleValue = (float) 0.0;
		try {
			doubleValue = Double.parseDouble(getText());
			;
		} catch (Exception error) {
			return doubleValue;
		}
		return doubleValue;
	}

	public long getLongValue() {
		long longValue = 0;
		try {
			longValue = Long.parseLong(getText());
		} catch (Exception error) {
			return longValue;
		}
		return longValue;
	}

	protected Document createDefaultModel() {
		return new NumberDocument();
	}

	public void setDefaultProperties() {
		setText("");
		setToFloat(false);
		setToDouble(false);
	}

	public void clearText() {
		setText("");
	}

	public void focusGained(FocusEvent e) {
		// do nothing
	}

	/*
	 * This method would handle incomplete user entered number format.
	 * 
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
		handleDanglingDot();
	}

	public void handleDanglingDot() {
		String enteredText = this.getText().trim();
		if (!enteredText.equals("")) {
			if (enteredText.equals(".")) {
				this.setText("0.0");
			} else if (enteredText.startsWith(".")) {
				this.setText("0" + enteredText);
			} else if (enteredText.endsWith(".")) {
				this.setText(enteredText + "0");
			}
		}
	}

	public static void main(String[] args) {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		NumberTextField specialText = new NumberTextField();
		// specialText.setNegativeAllowed(false);
		specialText.setToDouble(true);
		frame.setSize(400, 400);
		frame.getContentPane().add(specialText);
		frame.setVisible(true);
	}

	class NumberDocument extends PlainDocument {
		
		private static final long serialVersionUID = -5556253309461798936L;
		
		String oldtext = null;
		String newtext = null;

		public void insertString(int offset, String str, AttributeSet attrSet) throws BadLocationException {
			if (str == null)
				return;
			oldtext = getText(0, getLength()).trim();
			newtext = (oldtext.substring(0, offset) + str + oldtext.substring(offset)).trim();
			try {
				if (newtext.equals(""))
					return;
				if (floatAllowed_) {
					if (!isNegativeAllowed_) {
						float floatValue = Float.parseFloat(newtext);
						if (floatValue < 0.0)
							return;
					} else {
						Float.parseFloat(newtext + "0");
					}
				} else if (doubleAllowed_) {
					if (!isNegativeAllowed_) {
						double doubleValue = Double.parseDouble(newtext);
						if (doubleValue < 0.0)
							return;
					} else
						Double.parseDouble(newtext + "0");
				} else if (longAllowed_) {
					if (!isNegativeAllowed_) {
						long longValue = Long.parseLong(newtext);
						if (longValue < 0)
							return;
					} else
						Long.parseLong(newtext + "0");
				} else {
					if (!isNegativeAllowed_) {
						int intValue = Integer.parseInt(newtext);
						if (intValue < 0)
							return;
					} else
						Integer.parseInt(newtext + "0");
				}// end of else if block
				super.insertString(offset, str, attrSet);
			} catch (NumberFormatException numberError) {
				// numberError.printStackTrace();
			}// end of catch
		}// end of method
	}// end of inner class
}
