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
package com.chemistry.enotebook.client.gui.page.experiment;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.client.gui.page.batch.events.ContainerAddedEvent;
import com.chemistry.enotebook.client.gui.page.batch.events.ContainerAddedEventListener;
import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.service.container.ContainerService;
import com.chemistry.enotebook.utils.CeNDialog;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchContainerDialog extends CeNDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 100083992975817560L;
	private static final Log log = LogFactory.getLog(SearchContainerDialog.class);
	// content
	private JTabbedPane tab = new JTabbedPane();
	private JPanel contentPane;
	// control panel for buttons
	private JPanel controllPanel = new JPanel();
	private JButton searchAddButton = new JButton("Search");
	private JButton cancelButton = new JButton("Close");
	// search panel
	private JPanel searchPanel = new JPanel();
	private JLabel searchLable = new JLabel("Container Code or Description Contains:");
	private JTextField seachtf = new JTextField();
	// result panel

	
	// private JPanel resultPanel = new JPanel();

	private ContainerTableModel containerModel = new ContainerTableModel(); 
	private JTable resultList = new JTable(containerModel);
	private JScrollPane resultListScrollPane;
	// layouts
	private BorderLayout borderLayout = new BorderLayout();
	private FlowLayout flowLayout = new FlowLayout();
	private FormLayout searchPanelFormLayout = new FormLayout("pref, 100dlu", "pref");
	private CellConstraints mCellConstraints = new CellConstraints();
	// services
	private ContainerService containerService = null;
	private ContainerAddedEventListener containerAddedEventListener;
	
	private static final String SEARCH = "Search";
	private static final String ADD_TO_LIST = "Add to List";
	
	public SearchContainerDialog(JFrame parent, ContainerAddedEventListener containerAddedEventListeners) {
		super(parent, "Search CompoundManagement Containers", true);
		try {
			containerService =  ServiceController.getContainerService(MasterController.getUser().getSessionIdentifier());
			containerAddedEventListener = containerAddedEventListeners;
			jbInit();
			pack();
		} catch (Exception e) {
			log.error("Failed to start Search Container Dialog.", e);
		}
	}

	private void jbInit() throws Exception {
//		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
//		Dimension labelSize = getPreferredSize();
//		setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2 - (labelSize.height / 2));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		// center
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(borderLayout);
		contentPane.add(tab, BorderLayout.CENTER);
		contentPane.add(controllPanel, BorderLayout.SOUTH);
		// add search components
		searchPanel.setLayout(searchPanelFormLayout);
		searchPanel.add(searchLable, mCellConstraints.xy(1, 1));
		searchPanel.add(seachtf, mCellConstraints.xy(2, 1));
		tab.addTab("Search Container Types", searchPanel);
		// add result
		resultList.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		resultListScrollPane = new JScrollPane(resultList);
		tab.addTab("Search Results", resultListScrollPane);
		//tab.addTab("Search Results", resultList);
		// controll panel
		flowLayout.setAlignment(FlowLayout.RIGHT);
		controllPanel.setLayout(flowLayout);
		controllPanel.add(searchAddButton);
		controllPanel.add(cancelButton);
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		searchAddButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchAddButton_ActionPerformed();
			}
		});
		tab.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				tabSelectionChanged(evt);
			}
		});
	}

	protected void defaultApplyAction() {
		searchAddButton_ActionPerformed();
	}
	
	protected void defaultCancelAction() {
		dispose();
	}
	
	private void tabSelectionChanged(ChangeEvent evt) {
		if (tab.getSelectedIndex() == 0) {// search cmd
			// . Change to Search
			searchAddButton.setText(SEARCH);
		} else {
			searchAddButton.setText(ADD_TO_LIST);
		}
	}

	private void searchAddButton_ActionPerformed() {
		if (tab.getSelectedIndex() == 0) {// search cmd
			try {
				// 1. go search
				containerModel = new ContainerTableModel();
				Container searchContainer = new Container();
				searchContainer.setContainerName(seachtf.getText());
				searchContainer.setContainerCode(seachtf.getText());
				List containers = containerService.searchForCompoundManagementContainers(searchContainer);
//				// Below sorting and filter logic is moved to server side.
//				Collections.sort(containers, new Comparator() {
//					public int compare(Object o1, Object o2) {
//						if (o1 instanceof com.chemistry.enotebook.domain.container.Container && 
//								o2 instanceof com.chemistry.enotebook.domain.container.Container) {
//							com.chemistry.enotebook.domain.container.Container c1 = (com.chemistry.enotebook.domain.container.Container) o1;
//							com.chemistry.enotebook.domain.container.Container c2 = (com.chemistry.enotebook.domain.container.Container) o2;
//							if (c1.getContainerName() != null && c2.getContainerName() != null)
//								return c1.getContainerName().compareTo(c2.getContainerName());
//						}
//						return 0;
//					}
//				});
//
//				String filterstr = this.seachtf.getText().toLowerCase();
//				if (filterstr != null && filterstr.length() > 0) {
//					ArrayList filteredContainers = new ArrayList();
//					for (Iterator it = containers.iterator(); it.hasNext();) {
//						com.chemistry.enotebook.domain.container.Container c = (com.chemistry.enotebook.domain.container.Container) it.next();
//						String containerName = c.getContainerName();
//						String containerType = c.getContainerType();
//						// If either container name or type is null, do not include it in list.
//						if (containerName != null && containerType != null) {
//							if (containerName.toLowerCase().indexOf(filterstr) >= 0)
//								filteredContainers.add(c);
//							else if (containerType.toLowerCase().indexOf(filterstr) >= 0)
//								filteredContainers.add(c);
//						}
//					}
//					containers.clear();
//					containers.addAll(filteredContainers);
//				}
				int size = containers.size();
				Container[] containerss = new Container[size];
				for (int i = 0; i < size; ++i) {
					containerss[i] = (Container) containers.get(i);
				}
				containerModel.setContainers(containerss);
				resultList.setModel(containerModel);
				tab.validate();
				// 2. bring up search result tab
				tab.setSelectedIndex(1);
				// 3. Change to Add to List
				searchAddButton.setText(ADD_TO_LIST);
			} catch (Exception ex) {
				log.error("Failed initialization.", ex);
			}
		} else {// in result panel
			// 1. get selected results
			Object[] objs = containerModel.getContainers(resultList.getSelectedRows());
			// 2 add to list
			ContainerAddedEvent event = new ContainerAddedEvent(this, null);
			for (int i = 0; i < objs.length; i++) {
				Container selectedCompoundManagementContainer = (Container) objs[i];
				selectedCompoundManagementContainer.setCreatorId(MasterController.getUser().getNTUserID());
				event.setContainer(selectedCompoundManagementContainer);
				containerAddedEventListener.newContainerAdded(event);
			}
			// 3. close it self
			//this.dispose();
		}
	}

}

class ContainerTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] columnNames = {"Container Code",
			"Container Name",
            "X Positions",
            "Y Positions",
            "Fill Order",
            "Skipped Wells Positions"};
	
    private ArrayList data = new ArrayList();

    public int getColumnCount() {
        return columnNames.length;
    }

    public Object[] getContainers(int[] is) {
    	Container[] selectedContainers = new Container[is.length];
    	for (int i=0; i<is.length; i++)
    	{
    		selectedContainers[i] = (Container)data.get(is[i]);
    	}
		return selectedContainers;
	}

	public void setContainers(Container[] containerss) {
		data = new ArrayList(Arrays.asList(containerss));
	}
	
	public void setContainers(ArrayList containers) {
		this.data = containers;
	}

	public int getRowCount() {
        return data.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
    	Container container = (Container) data.get(row);
    	if (col == 0)
    		return container.getContainerCode();
    	else if (col == 1)
    		return container.getContainerName();
    	else if (col == 2)
    		return container.getXPositions() + "";
    	else if (col == 3)
    		return container.getYPositions() + "";
    	else if (col == 4)
    		return container.getMajorAxis();
    	else if (col == 5)
    	{
    		if (container.getSkippedWellPositions() == null || container.getSkippedWellPositions().size() == 0 
    				|| (container.getSkippedWellPositions().size() == 1 && container.getSkippedWellPositions().get(0) == null))
    			
    			return "";
    		else
    			return container.getSkippedWellPositions();
    	}

    	else
    		return "";
    }
 }
