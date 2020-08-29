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
package com.chemistry.enotebook.client.gui;

import com.chemistry.enotebook.client.gui.page.batch.BatchContainer;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.domain.NotebookPageModel;

import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.Map;

public interface NotebookPageGuiInterface {
	void dispose();

	boolean isPageEditable();

	NotebookPageModel getPageModel();

	void setPageModel(NotebookPageModel model);

	Component getComponent();

	void refreshPage();
	
	Map<String, BatchContainer> getBatchContainerCache();
	
	public void showTab(int i);
	public PCeNTableView getCurrentBatchDetailsTableView();
	
	public boolean isClosing();
	public void setClosing(boolean close);
	public boolean isEnabled();
	public void setEnabled(boolean enabled);

	public boolean isVisible();
	public void setVisible(boolean visibleFlag);
	public boolean isSelected();
	public void setSelected(boolean selectedFlag) throws PropertyVetoException;
}
