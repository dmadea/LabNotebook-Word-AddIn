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
package com.virtuan.plateVisualizer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class WellPropertiesStructureWindow extends JWindow {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6230741195952136163L;
	private static int DEFAULT_WIDTH = 100;
	private static int DEFAULT_HEIGHT = 30;
	private int _defaultWidth;
	private int _defaultHeight;

	/**
	 * parameterized constructor
	 * 
	 * @param title
	 */
	public WellPropertiesStructureWindow() {
		_defaultWidth = DEFAULT_WIDTH;
		_defaultHeight = DEFAULT_HEIGHT;
	}

	/**
	 * method to return the list of viewable properties
	 * 
	 * @param well
	 * @return ArrayList
	 */
	private ArrayList listViewableProperties(WellModel well) {
		WellProperty property;
		ArrayList ar = new ArrayList();
		for (int x = 0; x < well.getNumberOfProperties(); x++) {
			property = (WellProperty) well.getProperty(x);
			if (property.getIsDisplayed())
				ar.add(property);
		}
		for (int y = 0; y < well.getNumberOfInalienableProperties(); y++) {
			property = (WellProperty) well.getInalienableProperty(y);
			if (property.getIsDisplayed())
				ar.add(property);
		}
		return ar;
	}

	/**
	 * method to override the base width of the frame
	 * 
	 * @param width
	 */
	public void setDefaultWidth(int width) {
		_defaultWidth = width;
	}

	/**
	 * method to override the base height of the frame
	 * 
	 * @param width
	 */
	public void setDefaultHeight(int height) {
		_defaultHeight = height;
	}

	/**
	 * method to display the WellProperties
	 * 
	 * @param well
	 */
	public void displayWellProperties(WellModel well) {
		JTextField lable;
		JTextField value;
		WellProperty property;
		int height = 0;
		int width = _defaultWidth;
		ArrayList viewProperties = listViewableProperties(well);
		Collections.sort(viewProperties);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(viewProperties.size(), 2));
		this.setVisible(false);
		this.setSize(0, 0);
		for (Iterator i = viewProperties.iterator(); i.hasNext();) {
			property = (WellProperty) i.next();
			lable = new JTextField(property.getName());
			lable.setEditable(false);
			panel.add(lable);
			value = new JTextField(property.getValue());
			value.setEditable(false);
			panel.add(value);
			height = height + value.getFont().getSize();
			width = Math.max(width, property.getValue().length());
			width = Math.max(width, property.getName().length());
		}
		this.setSize(width * 2, _defaultHeight + (height * 2));
		this.getContentPane().removeAll();
		this.getContentPane().add(panel);
	}
}
