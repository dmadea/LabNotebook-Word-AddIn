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
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.utils.jtable.ExcelExporter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class SimpleJTable extends JTable implements ActionListener {

	private static final long serialVersionUID = 5915771597311316872L;

	private static final Log log = LogFactory.getLog(SimpleJTable.class);
	
	private static final String EXPORT_TO_EXCEL_FILE = "Export to Excel file...";
	
	private List<Rectangle> selectedAreas = new Vector<Rectangle>();
	protected JPopupMenu popupMenu = null;
	private int selectMode = ListSelectionModel.SINGLE_SELECTION;

	public SimpleJTable() {
		setSelectionBackground(getBackground());
		setSelectionForeground(getForeground());
		setFocusable(false);
		setSelectionMode(selectMode);
		getSelectionModel().addListSelectionListener(new TableListener(this));
	}

	public SimpleJTable(JPopupMenu menu) {
		this();
		
		popupMenu = addExportToExcelMenuItem(menu);
		
		final SimpleJTable thisTable = this;
		
		this.addMouseListener(new MouseInputAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mouseAction(thisTable, e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				mouseAction(thisTable, e);
			}
		});
	}

	private void mouseAction(SimpleJTable thisTable, MouseEvent e) {
		if (e.isPopupTrigger() && popupMenu != null) {
			int row = thisTable.rowAtPoint(e.getPoint());
			if (row != -1) {
				thisTable.setRowSelectionInterval(row, row);
			}
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		} else if (thisTable.selectMode == ListSelectionModel.SINGLE_SELECTION || !(e.isControlDown() || e.isShiftDown())) {
			int row = thisTable.rowAtPoint(e.getPoint());
			if (row != -1) {
				thisTable.setRowSelectionInterval(row, row);
			}
		}
	}
	
	public JPopupMenu addExportToExcelMenuItem(JPopupMenu menu) {
		JMenuItem menuItem = new JMenuItem(EXPORT_TO_EXCEL_FILE);
		menuItem.setFont(menu.getFont());
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		return menu;
	}

	public void setSelectionMode(int mode) {
		super.setSelectionMode(mode);
		selectMode = mode;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (selectedAreas != null && selectedAreas.size() > 0) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2.setPaint(Color.BLACK);
			Stroke st = g2.getStroke();
			g2.setStroke(new BasicStroke(3.0f));
			
			for (Rectangle r : selectedAreas) {
				r.x = 0;
				r.width = getWidth() - 2;
				g2.drawRect(r.x + 1, r.y + 1, r.width, r.height - 2);
			}
			
			g2.setStroke(st);
		}
	}

	public void setSelectedAreas(List<Rectangle> areas) {
		selectedAreas = areas;
		if (areas == null) {
			clearSelection();
		}
		repaint();
	}

	public Rectangle getRect() {
		if (selectedAreas != null && selectedAreas.size() > 0) {
			return (Rectangle) selectedAreas.get(0);
		} else {
			return null;
		}
	}

	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem) (e.getSource());
		
		if (source.getText().equals(EXPORT_TO_EXCEL_FILE)) {
			if(hasSelectColumnForExport() && !hasSelectedRows()) {
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
                        "Select at least one table row to export",
                        "Has no data to export", 
                        JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			JFileChooser chooser = new JFileChooser();
			FileFilter filter = new ExtensionFileFilter("XLS files", new String[] { "XLS" });
			chooser.setFileFilter(filter);
			int returnVal = chooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				
				if ("xls".equalsIgnoreCase(getFileExtension(file.getAbsolutePath()))==false){
					file = new File(file.getAbsolutePath() + ".xls");
				}
								
				try {
					ExcelExporter.exportTable(this, file, !hasSelectColumnForExport());
				} catch (IOException e1) {
					log.error(e);
				}
			}
		}
	}
	
	public boolean hasSelectedRows() {
		TableModel model = this.getModel();
		int selectColumnIndex = -1;
		for (int i = 0; i < model.getColumnCount(); i++) {
			String columnName = model.getColumnName(i);
			if(columnName != null && columnName.toLowerCase().contains("select")) {
				selectColumnIndex = i;
				break;
			}
		}
		if(selectColumnIndex > -1) {
			for (int i = 0; i < model.getRowCount(); i++) {				
				Object selected = model.getValueAt(i, selectColumnIndex);
				if (selected != null && selected instanceof Boolean
						&& ((Boolean) selected).equals(Boolean.TRUE)) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getFileExtension(String f) {
		String ext = "";
		int i = f.lastIndexOf('.');
		if (i > 0 && i < f.length() - 1) {
			ext = f.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	private boolean hasSelectColumnForExport() {
		TableModel model = getModel();
		int columnCount = model.getColumnCount();
		if (columnCount > 0) {
			for (int i = 0; i < columnCount; ++i) {
				String columnName = ExcelExporter.removeStar(model.getColumnName(i));
				if (StringUtils.equalsIgnoreCase(columnName, ExcelExporter.SELECT) || StringUtils.equalsIgnoreCase(columnName, PCeNTableView.SELECT)) {
					return true;
				}
			}
		}
		return false;
	}
	
	class TableListener implements ListSelectionListener {
		JTable table;

		public TableListener(JTable table) {
			this.table = table;
		}

		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				List<Rectangle> v = new Vector<Rectangle>();
				int[] rows = table.getSelectedRows();
				if (rows != null && rows.length > 0) {
					for (int i = 0; i < rows.length; i++) {
						v.add(table.getCellRect(rows[i], table.getSelectedColumn(), true));
					}
				}
				((SimpleJTable) table).setSelectedAreas(v);
			}
		}
	}

	class ExtensionFileFilter extends FileFilter {
		String description;
		String extensions[];

		public ExtensionFileFilter(String description, String extension) {
			this(description, new String[] { extension });
		}

		public ExtensionFileFilter(String description, String extensions[]) {
			if (description == null) {
				this.description = extensions[0];
			} else {
				this.description = description;
			}
			this.extensions = (String[]) extensions.clone();
			toLower(this.extensions);
		}

		private void toLower(String array[]) {
			for (int i = 0, n = array.length; i < n; i++) {
				array[i] = array[i].toLowerCase();
			}
		}

		public String getDescription() {
			return description;
		}

		public boolean accept(File file) {
			if (file.isDirectory()) {
				return true;
			} else {
				String path = file.getAbsolutePath().toLowerCase();
				for (int i = 0, n = extensions.length; i < n; i++) {
					String extension = extensions[i];
					if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
						return true;
					}
				}
			}
			return false;
		}
	}
}
