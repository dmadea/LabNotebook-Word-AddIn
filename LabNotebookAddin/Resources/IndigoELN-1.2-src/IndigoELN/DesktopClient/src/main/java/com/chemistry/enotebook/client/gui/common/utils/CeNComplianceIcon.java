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
package com.chemistry.enotebook.client.gui.common.utils;

import javax.swing.*;
import java.awt.*;

/**
 * 
 *
 */
public class CeNComplianceIcon 
	extends ImageIcon 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -3914209663714954246L;
	private int width = 32;
    private int height = 32;    
    private boolean inCompliance = true;
    
    
    public CeNComplianceIcon(java.net.URL url, boolean inCompliance) {
    	super(url);
    	
    	this.inCompliance = inCompliance;
    }
    
    public CeNComplianceIcon(String name, boolean inCompliance) {
    	super(name);
    	
    	this.inCompliance = inCompliance;
    }
    
    public void paintIcon(Component c, Graphics g, int x, int y) {
    	super.paintIcon(c, g, x, y);
    	
        if (!inCompliance) {
        	Graphics2D g2d = (Graphics2D) g.create();
        
            g2d.setColor(Color.RED);
            g2d.fillRect(x, y + 1, x + 2, super.getIconHeight() - 1);
        
            g2d.dispose();
        }
    }
    
    public int getIconWidth() {
        return super.getIconWidth();
    }
    
    public int getIconHeight() {
        return super.getIconHeight();
    }
}
