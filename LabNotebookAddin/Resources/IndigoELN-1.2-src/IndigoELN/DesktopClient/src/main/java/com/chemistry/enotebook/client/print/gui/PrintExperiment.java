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
package com.chemistry.enotebook.client.print.gui;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.utils.CeNJobProgressHandler;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.SwingWorker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

public class PrintExperiment {
	public static final Log log = LogFactory.getLog(PrintExperiment.class);
	
	private ReportURLGenerator urlgen;
    private String filePath;
	private boolean status = true;
	private String experiment = "";
	
	
	public PrintExperiment(String exp, ReportURLGenerator urlgen, PrintSetupConstants.buttonChoices whichButton) {
		this.urlgen = urlgen;
		this.experiment = exp;
        
        String printPath = MasterController.getApplicationDirectory() + File.separator + "Printing-Tmp" + File.separator + exp;
		
		//	check to see if directory exists; if so, delete all files within
		if ((new File(printPath)).exists()) {
			this.cleanDir(new File(printPath));
			status = true;
		}
		
		// Create a directory; all non-existent ancestor directories are automatically created
		status = (new File(printPath)).mkdirs();
		if (!status) status = (new File(printPath)).exists();
		
		// create the printout file
		if (status) {
			String extension;
			if (whichButton == PrintSetupConstants.buttonChoices.EXPORT) {
                extension = "_" + (new Date()).getTime() + ".doc";
            } else {
                extension = "_" + (new Date()).getTime() + ".pdf";
            }
			filePath = printPath + File.separator + exp + extension; // without .rtf extension
		} else {
			log.error("Failed to create printing directory '" + printPath + "'\n");
			CeNErrorHandler.getInstance().logErrorMsg(MasterController.getGUIComponent(), "Failed to create printing directory '" + printPath + "'\n", "Printing Error", JOptionPane.ERROR_MESSAGE);
		}

	}
	
	public boolean print() {
		SwingWorker worker = new SwingWorker() {
			public Object construct() {
				if (!status) {
					return status;
				}
                
				final String progressStatus = "Printing experiment " + experiment + " ...";
				
                CeNJobProgressHandler.getInstance().addItem(progressStatus);
				
				try {
                    byte[] result = CommonUtils.getReportHttpResponseResult(urlgen.getURL());

                    long start = System.currentTimeMillis();
                    
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(filePath));
                    
                    os.write(result);
                    os.close();

                    log.debug("File saved to disk in " + (System.currentTimeMillis() - start) + "ms");
				} catch (Exception e) {
					String msg = "Report service error.";
					log.error(msg, e);
					CeNErrorHandler.getInstance().logErrorMsg(MasterController.getGUIComponent(), msg+"\n", "Printing Error", JOptionPane.ERROR_MESSAGE);
					status = false;
				} finally {
					CeNJobProgressHandler.getInstance().removeItem(progressStatus);
				}
                
				return null;
			}			
			public void finished() {
				if (status) {
					show();
				}
			}
		};
        
		worker.start();
        
		return status;
	}
		
	public void show() {
		try {
			Desktop.getDesktop().browse(new File(filePath).toURI());
		} catch (Exception e) {
			String msg = "Unable to load file '" + filePath + "'";
			log.error(msg, e);
			JOptionPane.showMessageDialog(MasterController.getGuiComponent(), msg + "\n\n" + e.getMessage(), "Printing Error", JOptionPane.ERROR_MESSAGE);			
		}
	}

    private boolean cleanDir(File dir) {
        boolean status;

        if (dir.isDirectory()) {
            String[] children = dir.list();
            
            for (String aChildren : children) {
                cleanDir(new File(dir, aChildren));
            }

            status = dir.delete();

            return status;
        } else
            return !dir.isFile() || dir.delete();
    }
}
