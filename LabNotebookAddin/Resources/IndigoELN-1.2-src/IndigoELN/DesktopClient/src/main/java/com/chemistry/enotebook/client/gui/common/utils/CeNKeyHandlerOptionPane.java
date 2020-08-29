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

import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountCellEditor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CeNKeyHandlerOptionPane {
	private static final Log log = LogFactory.getLog(CeNKeyHandlerOptionPane.class);
	
	static JComponent popUpComponent = null;
	
	public static Object showOptionPane(Component component, JOptionPane pane)
	{
		JDialog dialog = pane.createDialog(component, "");
		KeyListener keyListener = new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE)
					e.consume();
				else
					log.debug("KeyPressed event: " + e.getKeyCode());
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		};
		Component[] components = pane.getComponents();
		for(int i = 0; i < components.length; i++)
		{
			Component[] comps = ((Container)components[i]).getComponents();
			for(int j = 0; j < comps.length; j++)
				if(comps[j] instanceof JButton)
				{
					JButton button = (JButton)comps[j];
					button.addKeyListener(keyListener);
				}
		}
		dialog.pack();
		if (popUpComponent != null)
		{
			if (popUpComponent instanceof PAmountCellEditor)
				((PAmountCellEditor)popUpComponent).requestFocusValueField();
			else
				popUpComponent.requestFocusInWindow();
		}
        dialog.setVisible(true);
        dialog.dispose();

        Object selectedValue = pane.getValue();
        return selectedValue;
	}

	public static void setFocusComponent(JComponent popUpComponent1) {
		popUpComponent = popUpComponent1;	
	}

}
