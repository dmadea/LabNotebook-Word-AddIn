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
package com.chemistry.enotebook.utils;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.gui.common.utilsui.ProgressStatusBarItem;
import com.chemistry.enotebook.client.gui.page.reagents.ICancel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CeNJobProgressHandler {
	
	private List<ProgressStatusBarItem> allItems = new ArrayList<ProgressStatusBarItem>();
	private CeNProgressJDialog progressDialog = new CeNProgressJDialog();
	private JPanel jPanelProgress = new JPanel();
	private static CeNJobProgressHandler _instance = null;

	public synchronized static CeNJobProgressHandler getInstance() {
		if (_instance == null) {
			_instance = new CeNJobProgressHandler();
		}
		return _instance;
	}

	public void refreshItems() {
		int size = 0;
		
		synchronized (allItems) {
			size = allItems.size();
		}
		
		if (size == 1) {
			ProgressStatusBarItem item = (ProgressStatusBarItem)jPanelProgress.getComponent(0);
			String message = item.getName();
			ProgressStatusBarItem.CancelCallback cancelCallback = item.getCancelCallback();
			
			MasterController.getGUIComponent().getProgressBar().setProgressStatus(message);
			MasterController.getGUIComponent().getProgressBar().getCancelButton().setToolTipText(message);
			MasterController.getGUIComponent().getProgressBar().setCancelCallback(cancelCallback);
			
			progressDialog.setVisible(false);
		} else if (size > 0) {
			MasterController.getGUIComponent().getProgressBar().setProgressStatus("Busy");
			MasterController.getGUIComponent().getProgressBar().setCancelCallback(null);
		} else {
			MasterController.getGUIComponent().stopProgressBar();
		}
	}
	
	public void addItem(String jobDesc) {
		// update left bottom StatusBar of the CeNDesktop
		MasterController.getGUIComponent().startProgressBar(jobDesc);
		// to show all Jobs progress
		ProgressStatusBarItem item = new ProgressStatusBarItem();
		item.setName(jobDesc);
		item.setProgressStatus(jobDesc);
		
		ProgressWorker worker = new ProgressWorker();
		worker.setItem(item);
		worker.start();
		
		jPanelProgress.add(item);
		
		synchronized (allItems) {
			allItems.add(item);
		}
		
		updateToolTipText();
		refreshItems();
	}

	public void addCancellableItem(String jobDesc, final ICancel cancelManager) {
		// update left bottom StatusBar of the CeNDesktop
		MasterController.getGUIComponent().startProgressBar(jobDesc, cancelManager);
		// to show all Jobs progress
		ProgressStatusBarItem item = new ProgressStatusBarItem();
		item.setName(jobDesc);
		item.setProgressStatus(jobDesc);

		item.setCancelCallback(new ProgressStatusBarItem.CancelCallback() {
		    public void cancelPerformed(){
		    	cancelManager.cancel();
		    }
		});
		
		AbstractButton cancelButton = item.getCancelButton();
		cancelButton.setHorizontalTextPosition(SwingConstants.CENTER);
		cancelButton.setText("");
		cancelButton.setBorderPainted(true);
		cancelButton.setMinimumSize(new java.awt.Dimension(25, 25));
		cancelButton.setMaximumSize(new java.awt.Dimension(25, 25));
		cancelButton.setSize(new java.awt.Dimension(25, 25));
		cancelButton.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.DELETE));
		
		ProgressWorker worker = new ProgressWorker();
		worker.setItem(item);
		worker.start();
		
		jPanelProgress.add(item);
		
		synchronized (allItems) {
			allItems.add(item);
		}
		
		updateToolTipText();
		refreshItems();
	}
	
	public void removeItem(String jobDesc) {
		int componentCount = jPanelProgress.getComponentCount();
		
		for (int i = 0; i < componentCount; ++i) {
			ProgressStatusBarItem item = (ProgressStatusBarItem) jPanelProgress.getComponent(i);
			if (item.getName().equals(jobDesc)) {
				synchronized (allItems) {
					allItems.remove(item);
				}
				
				jPanelProgress.remove(item);
				jPanelProgress.repaint();
				
				ProgressWorker.destroyWorker(item);
				item = null;
				
				break;
			}
		}
		
		if (MasterController.getGUIComponent() != null) {
			SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
					updateToolTipText();
			    	CeNJobProgressHandler.getInstance().refreshItems();
			    }
			});
		}
	}

	public void showProgressPanel() {
		int size = 0;
		
		synchronized (allItems) {
			size = allItems.size();
		}

		if (size > 1) {
			if (progressDialog.isVisible()) {
				progressDialog.setVisible(false);
			} else {
				progressDialog.setVisible(true);
			}
			
			if (progressDialog.isVisible()) {
				jPanelProgress.removeAll();
				
				int maxRows = 10;
				GridLayout jPanelProgressLayout = new GridLayout(0, 1);
				
				jPanelProgress.setLayout(jPanelProgressLayout);
				
				jPanelProgressLayout.setHgap(0);
				jPanelProgressLayout.setVgap(0);
				jPanelProgressLayout.setRows(size < maxRows ? maxRows : size);
				 
				synchronized (allItems) {
					for (ProgressStatusBarItem item : allItems) {
						jPanelProgress.add(item);
					}
				}
				
				progressDialog.setPanel(jPanelProgress);
			}
		}
	}

	private void updateToolTipText() {
		StringBuffer sb = new StringBuffer("<html>");
		
		for (ProgressStatusBarItem item : allItems) {
			if (item != null) {
				String name = item.getName();
				sb.append(name).append("<br>");
			}
		}
		
		sb.append("</html>");
		
		MasterController.getGUIComponent().getProgressBar().setToolTipText(sb.toString());
	}
}
