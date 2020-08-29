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

import javax.swing.*;
import java.awt.*;

public class CeNLabel extends JLabel
{
	/** CeN common Label.
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Font defaultLabelFont = new Font("Sansserif", 1, 11);
	private static final String COLON = ":";
	
	public CeNLabel(String caption)
	{
		super(caption);
		super.setFont(defaultLabelFont);
	}
	
	public CeNLabel()
	{
		super.setFont(defaultLabelFont);
	}
	public void setText(String caption)
	{
		if (!caption.endsWith(COLON))
			super.setText(caption + COLON);
		else
			super.setText(caption );
	}
}