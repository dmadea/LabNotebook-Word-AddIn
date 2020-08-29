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
package com.chemistry.enotebook.client.gui.page.table;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**This event is redundant. But, right now this is the only way to capture the selected row of table when a combo box on a cell is clicked. 
 * Note:  
 * Please observe the issue in the BatchEditPanel when a table cell combo box is clicked for row selection. It does not refresh even though the table row selected changed.*/
public class SelectionListener implements ListSelectionListener {
	PCeNTableView table;
	int previousRow = -1;
    // It is necessary to keep the table since it is not possible
    // to determine the table from the event's source
    public SelectionListener(PCeNTableView table) {
        this.table = table;
    }
    
    public void valueChanged(ListSelectionEvent e) {
        // If cell selection is enabled, both row and column change events are fired
        if (e.getSource() == table.getSelectionModel()
              && table.getRowSelectionAllowed()) {
            // Column selection changed
            int first = e.getFirstIndex();
            //int last = e.getLastIndex();
        	if (previousRow != first)
        	{
        		table.valueChanged();
        		previousRow = first;
        	}
        } else if (e.getSource() == table.getColumnModel().getSelectionModel()
               && table.getColumnSelectionAllowed() ){
            // Row selection changed
/*            int first = e.getFirstIndex();
            int last = e.getLastIndex();
*/        }

        if (e.getValueIsAdjusting()) {
            // The mouse button has not yet been released
        }
    }
}