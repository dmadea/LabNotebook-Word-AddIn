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
package com.chemistry.enotebook.utils.jtable;

import com.chemistry.enotebook.client.gui.page.batch.table.PCeNBatch_SummaryTableView;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.storage.ReactionPageInfo;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

public class ExcelExporter {
	private static final Log log = LogFactory.getLog(ExcelExporter.class);

	public static final String STRUCTURE = "Structure";
	public static final String NOTEBOOK_BATCH_NUMBER = "NBK Batch #";
	public static final String STEREOISOMER = "stereoisomer";
	public static final String SELECT = "select";
	public static final String STATUS = "status";

	private ExcelExporter() {
	}

	public static void exportTable(JTable table, File file, boolean exportAll) throws IOException {
		boolean notParallel = !(table instanceof PCeNBatch_SummaryTableView);
		
		TableModel model = table.getModel();
		FileOutputStream fos = new FileOutputStream(file);
		
		final ExcelWriter out = new ExcelWriter(fos);
		final StringBuilder stringBuilder = new StringBuilder();
		
		try {
			HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback() {
				public void handleText(char[] data, int pos) {
					stringBuilder.append(removeStar(new String(data)));
				}
			};
			
			int skipStructureColumnIndex = -1;
			int skipSelectColumnIndex = -1;
			int skipStatusColumnIndex = -1;
			int notebookBatchColumnIndex = -1;
			
			String notebookRef = "";
			
			for (int i = 0; i < model.getColumnCount(); i++) {
				String columnName = removeStar(model.getColumnName(i));
				
				if (StringUtils.isBlank(columnName)) {
					// Do not allow columns without title
					continue;
				}
				
				if (columnName.equalsIgnoreCase(STRUCTURE)) {
					skipStructureColumnIndex = i;
					continue;
				} else if (columnName.equalsIgnoreCase(NOTEBOOK_BATCH_NUMBER)) {
					notebookBatchColumnIndex = i;
					if (PCeNTableModel.class.isInstance(model)) {
						notebookRef = ((PCeNTableModel) model).getConnector().getPageModel().getNotebookRefWithoutVersion() + "-";
					}
				} else if (columnName.equalsIgnoreCase(SELECT) || columnName.toLowerCase().contains(SELECT)) {
					skipSelectColumnIndex = i;
					continue;
				} else if (columnName.equalsIgnoreCase(STATUS) && notParallel) {
					skipStatusColumnIndex = i;
					continue;
				}
				
				if (columnName.contains(">")) {
					stringBuilder.delete(0, stringBuilder.length());
					// parse html to get text only
					new ParserDelegator().parse(new StringReader(columnName), callback, false);
					out.addHeaderCell(stringBuilder.toString());
				} else {
					out.addHeaderCell(model.getColumnName(i));
				}
			}
			
			out.newLine();

			for (int i = 0; i < model.getRowCount(); i++) {
				if (!exportAll) {
					if (skipSelectColumnIndex > -1) {
						Object selected = model.getValueAt(i, skipSelectColumnIndex);
						if (selected != null && selected instanceof Boolean && !((Boolean) selected).equals(Boolean.TRUE)) {
							continue;
						}
					}
				}
				
				for (int j = 0; j < model.getColumnCount(); j++) {
					if (skipStructureColumnIndex == j || skipSelectColumnIndex == j || (skipStatusColumnIndex == j && notParallel)) {
						continue;
					}
					
					Object value = model.getValueAt(i, j);

					if (model.getColumnName(j).equalsIgnoreCase(STEREOISOMER)) {
						value = getStereoisomervalue(value);
					}

					if (CommonUtils.isNull(value)) {
						value = "";
					}
					
					if (notebookBatchColumnIndex == j) {
						stringBuilder.delete(0, stringBuilder.length());
						stringBuilder.append(notebookRef);
						
						for (int k = value.toString().length(); k < 6; k++) {
							stringBuilder.append("0");
						}
						
						stringBuilder.append(value.toString());
						out.addCell(stringBuilder.toString());
					} else {
						if (value instanceof ImageIcon) {
							out.addCell(value);
						} else if (value instanceof ReactionPageInfo) {
							// Just do nothing, ReactionPageInfo coming from Search Results Table (and not visible in that table)
						} else if (value.toString().contains(">")) {
							stringBuilder.delete(0, stringBuilder.length());
							// parse html to get text only
							new ParserDelegator().parse(new StringReader(value.toString().replace('\t', ' ')), callback, false);
							out.addCell(stringBuilder.toString());
						} else {
							out.addCell(value.toString().replace('\t', ' '));
						}
					}
				}
				
				out.newLine();
			}
		} finally {
			try {
				out.finish();
			} catch (Throwable e) {
				log.error("Error writing Excel Workbook: ", e);
			}
			
			try {
				fos.close();
			} catch (Exception ignored) {
			}
		}
		
		log.debug("write to:  " + file);
	}

	public static String removeStar(String str) {
		if (str != null && str.startsWith("*")) {
			str = str.substring(1).trim();
		}
		return str;
	}

	private static Object getStereoisomervalue(Object value) {
		try {
			if (value instanceof ProductBatchModel) {
				String stereoisomerCode = ((ProductBatchModel) value).getCompound().getStereoisomerCode();
				
				if (CommonUtils.isNull(stereoisomerCode)) {
					stereoisomerCode = "HSREG";
				}
				
				return stereoisomerCode;
			}
		} catch (Exception ignored) {
			
		}
		
		return value;
	}

	public static void main(String[] args) {
		String[][] data = { { "Housewares", "a", "$1275.00" },
				{ "Pets", "", "$125.00" }, { "Electronics", "b", "$2533.00" },
				{ "Menswear", "c", "$497.00" } };
		String[] headers = { "Department", STRUCTURE,
				"<html><font size='3'>Design <br></br>Monomer</font></html>" };

		JFrame frame = new JFrame("JTable to Excel Hack");
		DefaultTableModel model = new DefaultTableModel(data, headers);
		final JTable table = new JTable(model);
		JScrollPane scroll = new JScrollPane(table);

		JButton export = new JButton("Export");
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					ExcelExporter.exportTable(table, new File("test.xls"), false);
				} catch (IOException ex) {
					log.error(ex);
				}
			}
		});

		frame.getContentPane().add("Center", scroll);
		frame.getContentPane().add("South", export);
		frame.pack();
		frame.setVisible(true);
	}
}
