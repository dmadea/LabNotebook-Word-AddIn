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
package com.chemistry.enotebook.client.gui.tablepreferences;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.Vector;

/**
 * 
 * 
 */
public class ManageTableColumns extends MouseAdapter {
	private JTable table = null;
	private ScrollableMenu hideColumnPopup = null;
	private JMenu unitsMenu = null;
	private JMenuItem exitButton = null;
	private UnitsMenuHandler unitsHandler = null;
	private TablePreferenceDelegate preferences = null;
	private TablePreferenceHandler myPrefHandler = null;
	private NotebookPage page = null;
	private NotebookUser user = null;
	private Integer modInd = null;

	/**
	 * Default Class Constructor.
	 * 
	 * @param table
	 *            the JTable to which the listener is been added.
	 */
	public ManageTableColumns(JTable myTable, TablePreferenceInterface preferences, NotebookPage page) {
		super();
		this.table = myTable;
		this.preferences = (TablePreferenceDelegate) preferences;
		// add Listeners to table
		table.getTableHeader().addMouseListener(this);
		// table.getColumnModel().addColumnModelListener(this.preferences);
		myPrefHandler = new TablePreferenceHandler();
		this.page = page;
		user = MasterController.getUser();
	}

	public void applyTablePreferences() throws Exception {
//		try {
//			myPrefHandler.applyTablePreferences(table, preferences);
//		} catch (UserPreferenceException usExp) {
//			CeNErrorHandler.getInstance().logExceptionWithoutDisplay(usExp, usExp.getMessage());
//		} catch (Exception ex) {
//			throw new Exception(ex.getMessage(), ex.getCause());
//		}
	}

	/** Class used by JCheckBoxMenuItem buttons action */
	private class VisibleAction implements ActionListener {
		/**
		 * Implementation of ActionListener interface used by the menu items to hide columns.
		 * 
		 * @param evt
		 *            an ActionEvent
		 */
		public void actionPerformed(ActionEvent evt) {
			int modelIndex;
			JCheckBox item = (JCheckBox) evt.getSource();
//			modelIndex = hideColumnPopup.getComponentIndex(item);
//			if (modelIndex != -1) {
//				changeColumnVisibility(modelIndex, item.isSelected());
//				modInd = new Integer(modelIndex);
//				if (page.isPageEditable()) {
//					Thread visibilitySaver = new Thread() {
//						public void run() {
//							// updates preferences in Cache
//							int columnIndex = table.convertColumnIndexToView(modInd.intValue());
//							ColumnFurniture cf = ((ColumnFurniture) table.getColumnModel().getColumn(columnIndex));
//							TableColumnInfo columnInfo = preferences.getModifiedTableColumnInfo(cf);
//							String userProperties = preferences.getNotebookUserTableProperties();
//							if (userProperties.equals(""))
//								preferences.createUserTableFromXML(columnInfo);
//							else {
//								preferences.updateColumnInfoPreferences(columnInfo);
//								user.setPrefChanges(true);
//								String tablePref = preferences.buildNotebookPagePropertiesFromUserDoc().replaceAll(" ", "")
//										.replaceAll("\r\n", "");
//								if (tablePref != null) {
//									page.setTableProperties(tablePref);
//								}// end if
//							} // end if
//						}// end run
//					};// end Runnable
//					javax.swing.SwingUtilities.invokeLater(visibilitySaver);
//				}// end if pageIsEditable
//			}// end if
		}
	}// end class

	/**
	 * Method used to dispose of system resources allocated by the class. It removes all Listeners from the JTableHeader and the
	 * JCheckBoxMenuItems. <br>
	 * <br>
	 * <b><i>THIS METHOD MUST BE CALLED WHEN THE ASSOCIATED JTABLE OBJECT IS NO LONGER IN USE (table has been disposed).</i></b>
	 */
	public void dispose() {
		if (table != null) {
			table.getTableHeader().removeMouseListener(this);
			table = null;
		}
	}

	/**
	 * Method called by the garbage collector when it determines that there are no more references to the object. It calls the
	 * dispose() method to dispose of system resources.
	 * 
	 * @exception Throwable -
	 *                the Exception raised by this method
	 */
	protected void finalize() throws Throwable {
		dispose();
	}

	/**
	 * Implementation of MouseListener interface used to show the popup menu.
	 * 
	 * @param e
	 *            a MouseEvent
	 */
	public void mouseClicked(MouseEvent e) {
		if (((e.getModifiers() & MouseEvent.BUTTON3_MASK) == 4) || e.isPopupTrigger()) {
			// if ( hideColumnPopup == null ) {
			initPopupMenu(e);
			// }
			//hideColumnPopup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

//	/**
//	 * Initialize the JPopupMenu before each right click.
//	 */
//	private void initPopupMenu(MouseEvent e) {
//		// init all menus
//		hideColumnPopup = new ScrollableMenu("Show/Hide SubMenu Columns");
//		unitsMenu = new JMenu("Set Default Unit");
//		// unitsMenu.setAlignmentX(JComponent.LEFT_ALIGNMENT);
//		unitsMenu.setRolloverEnabled(true);
//		unitsMenu.setSelected(false);
//		unitsMenu.addMouseListener(new MouseListener() {
//			public void mouseClicked(MouseEvent e) {
//			};
//
//			public void mousePressed(MouseEvent e) {
//			};
//
//			public void mouseReleased(MouseEvent e) {
//			};
//
//			public void mouseEntered(MouseEvent e) {
//				unitsMenu.setPopupMenuVisible(true);
//			};
//
//			public void mouseExited(MouseEvent e) {
//				JMenu menuItem = (JMenu) e.getSource();
//				Point p = e.getPoint();
//				if (p.x >= 0 && p.x < menuItem.getWidth() + 50 && p.y >= 0 && p.y < menuItem.getHeight()) {
//					menuItem.setPopupMenuVisible(true);
//				} else
//					menuItem.setPopupMenuVisible(false);
//			}
//		});
//		// end MouseListener Implementation
//		int modelIndex, columnIndex;
//		if (e.getSource() instanceof JTableHeader) {
//			columnIndex = ((JTableHeader) e.getSource()).columnAtPoint(e.getPoint());
//			modelIndex = table.convertColumnIndexToModel(columnIndex);
//			unitsHandler = new UnitsMenuHandler(page, preferences, table, unitsMenu, modelIndex);
//			unitsHandler.createUnitsMenu();
//		}
//		ColumnFurniture column = null;
//		JCheckBox item = null;
//		Vector list = new Vector();
//		list.setSize(table.getColumnModel().getColumnCount());
//		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
//			column = (ColumnFurniture) table.getColumnModel().getColumn(i);
//			String str = column.getHeaderValue().toString();
//			item = new JCheckBox("Show " + str);
//			item.setSelected(column.isVisible());
//			item.setBorderPainted(false);
//			item.setHorizontalAlignment(SwingConstants.CENTER);
//			item.addActionListener(new VisibleAction());
//			list.setElementAt(item, column.getModelIndex());
//		}
//		for (Iterator it = list.iterator(); it.hasNext();)
//			hideColumnPopup.add((JCheckBox) it.next());
//		hideColumnPopup.add(unitsMenu);
//		hideColumnPopup.addSeparator();
//		exitButton = new JMenuItem("Exit");
//		exitButton.setRolloverEnabled(false);
//		exitButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent event) {
//				JMenuItem button = (JMenuItem) event.getSource();
//				if (button.getParent() instanceof JPopupMenu)
//					button.getParent().setVisible(false);
//			}
//		});
//		hideColumnPopup.add(exitButton);
//		hideColumnPopup.setLightWeightPopupEnabled(true);
//		hideColumnPopup.setLabel("Show/Hide Columns");
//		hideColumnPopup.setName("Show/Hide Columns");
//		// hideColumnPopup.setPreferredSize(new Dimension(80, 180));
//		// hideColumnPopup.scrollRectToVisible(new Rectangle(120, 60));
//	}// end method

	
	/**
	 * Initialize the JPopupMenu before each right click.
	 */
	private void initPopupMenu(MouseEvent e) 	{
		// init all menus
		hideColumnPopup = new ScrollableMenu("Show/Hide SubMenu Columns");
		unitsMenu   = new JMenu("Set Default Unit");
		//unitsMenu.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		unitsMenu.setRolloverEnabled(true);
		unitsMenu.setSelected(false);
		
		unitsMenu.addMouseListener(
				new MouseListener() {
					 public void mouseClicked(MouseEvent e) {  };
					 
					    public void mousePressed(MouseEvent e) {  };
					    
					    public void mouseReleased(MouseEvent e) { };
					 
					    public void mouseEntered(MouseEvent e) {
					    	unitsMenu.setPopupMenuVisible(true);
					    };
					 
					    public void mouseExited(MouseEvent e) {
					    	JMenu menuItem = (JMenu)e.getSource();
					    	Point p = e.getPoint(); 

					    	if(p.x >= 0 && p.x < menuItem.getWidth()+ 50 
					    				&& 	p.y >= 0 && p.y < menuItem.getHeight()) { 
					    		menuItem.setPopupMenuVisible(true);
					    	}else menuItem.setPopupMenuVisible(false);
					}
				}
		);
					
		// end MouseListener Implementation
		
		int modelIndex, columnIndex;
		if(e.getSource() instanceof JTableHeader) { 
			 columnIndex = ((JTableHeader)e.getSource()).columnAtPoint(e.getPoint());
			 modelIndex = table.convertColumnIndexToModel(columnIndex);
			 unitsHandler = new UnitsMenuHandler(page, preferences, table, unitsMenu, modelIndex);
			 unitsHandler.createUnitsMenu();
		}
		ColumnFurniture column = null;
		JCheckBox item = null;
		
		Vector list = new Vector();
		list.setSize(table.getColumnModel().getColumnCount());
		
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			column = (ColumnFurniture)table.getColumnModel().getColumn(i);

			String str = column.getHeaderValue().toString();
			// Strip out html tags
			str = str.replaceAll("\\<.*?>","");

			item = new JCheckBox(str);
			item.setSelected(column.isVisible());
			item.setBorderPainted(false);
			item.setHorizontalAlignment(SwingConstants.CENTER);
			item.addActionListener( new VisibleAction() );
			
			list.setElementAt(item, column.getModelIndex());
		}

		for (Iterator it=list.iterator(); it.hasNext(); )
			hideColumnPopup.add((JCheckBox)it.next());
		
		hideColumnPopup.add(unitsMenu);
		hideColumnPopup.addSeparator();
		exitButton = new JMenuItem("Exit");
		exitButton.setRolloverEnabled(false);
		exitButton.addActionListener( 
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						JMenuItem button = (JMenuItem)event.getSource();
						if( button.getParent() instanceof JPopupMenu )
							 button.getParent().setVisible(false);
					}
		});
		
		hideColumnPopup.add( exitButton );
		//hideColumnPopup.setLightWeightPopupEnabled(true);
		//hideColumnPopup.setLabel("Show/Hide Columns");
		hideColumnPopup.setName("Show/Hide Columns");
		//hideColumnPopup.setPreferredSize(new Dimension(80, 180));
		//hideColumnPopup.scrollRectToVisible(new Rectangle(120, 60));
	}//end method
	private void changeColumnVisibility(int modelIndex, boolean visible) { // update
		// table's
		// view
		int columnIndex = table.convertColumnIndexToView(modelIndex);
		ColumnFurniture cf = ((ColumnFurniture) table.getColumnModel().getColumn(columnIndex));
		cf.setVisible(visible);
		myPrefHandler.updateColumn(cf, table);
	}
} // End class ManageTableColumns
