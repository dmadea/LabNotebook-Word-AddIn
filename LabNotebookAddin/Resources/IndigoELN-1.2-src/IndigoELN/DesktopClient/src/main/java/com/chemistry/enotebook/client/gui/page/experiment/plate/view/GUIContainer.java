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
package com.chemistry.enotebook.client.gui.page.experiment.plate.view;

import javax.swing.*;

public class GUIContainer
{
	final String TITLE = "Orders, Plates, Import & Export";
    JFrame jfRender_;
    
    public GUIContainer()
    {
        jfRender_ = new JFrame(TITLE);
		jfRender_.setSize(800, 500);
		jfRender_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void init()
	{
		
        jfRender_.setVisible(true);
	}
	
	public javax.swing.JFrame getRenderer()
	{
        return jfRender_;
    }
}	