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
 * Created on Apr 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.analytical.singleton;

import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.experiment.utils.SystemPropertyException;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AnalyticalServiceFileFilter 
	extends FileFilter
{
	private static String rawExt = null;
	private static String desc = null;
	private static ArrayList<String> ext = new ArrayList<String>();
	
	static {
		if (rawExt == null) {
			try {
				rawExt = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_ANALYTICAL_SERVICE_UPLOAD_EXT);
				if (rawExt != null && rawExt.length() > 0) {
					desc = "";
					String list[] = rawExt.split(",");
					for (int i=0; i < list.length; i++) {
						if (desc.length() > 0) desc += ",";
						desc += "*." + list[i].toLowerCase();
						ext.add(list[i].toLowerCase());
					}
				}
			} catch (SystemPropertyException e) {
				e.printStackTrace();
			}
			
			if (desc == null) {
				desc = "*.pdf";
				ext.add("pdf");
			}
		}
	}
	
	public static String getRawExtensions() { return (rawExt == null) ? "" : rawExt; }
	
	public String getDescription() {
		 return desc;
	}

	public boolean accept(File f) {
       if (f.isDirectory()) 
        	return true;
        else {
    		boolean status = false;
    		String name = f.getName().toLowerCase();
    		for (Iterator<String> it=ext.iterator(); it.hasNext() && !status; )
    			if (name.endsWith(it.next())) status = true;
    		return status;
        }
    }
}
