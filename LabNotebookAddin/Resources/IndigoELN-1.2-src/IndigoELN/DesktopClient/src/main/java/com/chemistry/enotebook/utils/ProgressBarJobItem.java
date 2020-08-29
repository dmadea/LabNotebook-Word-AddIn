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
 * Created on Mar 28, 2005
 *
 *  To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.utils;

import com.chemistry.enotebook.client.gui.common.utilsui.ProgressStatusBarItem;

import java.util.ArrayList;

/**
 * 
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ProgressBarJobItem {
	public int NO_OF_ITEMS = 0;
	private String toolTipText;
	public ArrayList progressItemsList = new ArrayList();
	private static ProgressBarJobItem _instance = null;
	private ProgressStatusBarItem _progressItem;
	private String _currentStatus;
	private SwingWorker _job;

	private ProgressBarJobItem() {
	}

	public static ProgressBarJobItem getInstance() {
		if (_instance == null)
			_instance = new ProgressBarJobItem();
		return _instance;
	}

	public int addItem(ProgressStatusBarItem progressItem, String currentStatus) {
		_progressItem = progressItem;
		_currentStatus = currentStatus;
		progressItemsList.add(currentStatus);
		updateToolTipText();
		return progressItemsList.indexOf(currentStatus);
	}

	/**
	 * @param progressItem
	 */
	private void updateToolTipText() {
		// String imageName = "file:images/inProgress.gif";
		StringBuffer sb = new StringBuffer("<html>");
		for (int i = 0; i < progressItemsList.size(); i++) {
			sb.append(progressItemsList.get(i).toString())
			// .append("<img src="+imageName+" width=\"150\"
					// height=\"10\">")
					.append("<br>");
		}
		sb.append("</html>");
		_progressItem.setToolTipText(sb.toString());
	}

	public void removeItem(int itemIndex) {
		if (itemIndex >= 0 && itemIndex < progressItemsList.size())
			progressItemsList.remove(itemIndex);
		updateToolTipText();
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public Object submitJob() {
		return null;
	}

	/**
	 * TODO
	 * 
	 */
	public void pauseJob() {
	}

	/**
	 * TODO
	 * 
	 */
	public void removeJob() {
	}
}
