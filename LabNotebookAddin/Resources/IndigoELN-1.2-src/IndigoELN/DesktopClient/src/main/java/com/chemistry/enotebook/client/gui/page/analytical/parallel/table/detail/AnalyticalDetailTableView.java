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
package com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNStatusComboBoxRenderer;
import com.chemistry.enotebook.client.gui.common.utils.CeNTableCellAlignedComponentRenderer;
import com.chemistry.enotebook.client.gui.page.analytical.AnalyticalTableNotEditableCell;
import com.chemistry.enotebook.client.gui.page.analytical.ButtonCellRenderer;
import com.chemistry.enotebook.client.gui.page.analytical.InstrumentTypeCellRenderer;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.PAnalyticalSampleRefContainer;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.PAnalyticalUtility;
import com.chemistry.enotebook.client.gui.page.analytical.singleton.PurificationServiceDataJFrame;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableViewCellEditor;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableViewCellRenderer;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableViewHeaderRenderer;
import com.chemistry.enotebook.compoundmgmtservice.delegate.CompoundMgmtServiceDelegate;
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.utils.CeNJobProgressHandler;
import com.chemistry.enotebook.utils.DefaultPreferences;
import com.chemistry.enotebook.utils.SimpleJTable;
import com.chemistry.enotebook.utils.SwingWorker;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.rowset.CachedRowSet;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class AnalyticalDetailTableView extends SimpleJTable implements ChangeListener, AnalyticalChangeListener {

	private static final long serialVersionUID = 5916415304925593801L;

	private static final Log log = LogFactory.getLog(AnalyticalDetailTableView.class);
	
	private PCeNTableViewCellRenderer cellRenderer = new PCeNTableViewCellRenderer();
	private PCeNTableViewHeaderRenderer headerRenderer = new PCeNTableViewHeaderRenderer();
	private List<ProductBatchModel> batches = new ArrayList<ProductBatchModel>();
	
	private boolean isEditable = true;  

	private AnalyticalDetailUtility analyticalUtility;
	private PAnalyticalSampleRefContainer analyticalSampleRefContainer;
	private NotebookPageModel pageModel;
	private AnalyticalDetailTableModel tableModel;
	
	private AnalyticalDetailTableViewToolBar toolBar;
	
	private final TableColumn structureColumn;
	
	public AnalyticalDetailTableView(NotebookPageModel pageModel, List<ProductBatchModel> batches, PAnalyticalSampleRefContainer analyticalSampleRefContainer) {
		super();
		this.pageModel = pageModel;
		this.batches = batches;
		this.analyticalSampleRefContainer = analyticalSampleRefContainer;
		this.analyticalSampleRefContainer.addSummaryViewer(this);
		this.init();
		this.revalidate();

		structureColumn = getColumn(PCeNTableView.STRUCTURE);
	}
	
	public AnalyticalDetailTableView() {
		super();
		init();

		structureColumn = getColumn(PCeNTableView.STRUCTURE);
		
		log.debug(this.getClass().getName() + "  >>>>>>>>>>>>>>>>>>>>> using no argument constructor.  This should not happen!!!!!!");
	}

    public Dimension getPreferredScrollableViewportSize() {
        final Dimension d = getPreferredSize();
        return new Dimension(d.width, d.height + 2);
    }    

    public AnalyticalDetailTableViewToolBar getToolBar() {
		return toolBar;
	}
    
    public void setToolBar(AnalyticalDetailTableViewToolBar toolBar) {
		this.toolBar = toolBar;
	}
    
	/**
	 * initializes the table,
	 * 
	 */
	private void init() {
		this.setAutoCreateColumnsFromModel(true);
		this.tableModel = new AnalyticalDetailTableModel(pageModel, batches, this);
		this.setModel(tableModel);
		//this.setAutoCreateColumnsFromModel(true);
		//this.init();
		this.setSize(new Dimension(650, 400));
		//this.analyticalUtility = new PAnalyticalUtility(pageModel, batches, this);
		this.analyticalUtility = new AnalyticalDetailUtility(pageModel, this);
		this.setRenderers();
		this.setRowHeight(DefaultPreferences.TALL_ROW_HEIGHT);
		this.adjustColWidths();
		this.setAutoCreateColumnsFromModel(false);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				showPopupMenu(e);
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				showPopupMenu(e);
			}
			
			private void showPopupMenu(MouseEvent e) {
				JTable table = (JTable) e.getSource();
				table.getSelectionModel().setSelectionInterval(rowAtPoint(e.getPoint()), rowAtPoint(e.getPoint()));
				
				if (getSelectedRow() > -1) {
					String sampleRef = getValueAt(getSelectedRow(), getColIndex(PCeNTableView.NBK_BATCH_NUM)).toString();
															
					if (e.isPopupTrigger()) {
						popUp(e, sampleRef);
					}
					
					if (analyticalSampleRefContainer != null) {
						analyticalSampleRefContainer.buildAnalyticalTree(sampleRef);
					}
				}
			}
		});
			
		// Update the column model when the data model changes.  Note: if you do this automatically,
		// it sets the renderers and sizes back to defaults.
		getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				TableColumnModel columnModel = getColumnModel();
				List<String> distinctInsTypes = pageModel.getAnalysisCache().getDistinctInstrumentTypes();
				
				for (String instrumentType : distinctInsTypes) {
					addInstrument(instrumentType);
				}
				// remove unused columns
				Enumeration<TableColumn> en = columnModel.getColumns();
				while (en.hasMoreElements()) {
					TableColumn col = en.nextElement();
					String columnName = (String)col.getHeaderValue();
					boolean isColumnToRemove = !isPermanentColumn(columnName) && !distinctInsTypes.contains(columnName);
					if (isColumnToRemove) {
						removeColumn(col);
					}
				}
				reload();
			}
		});
	}
	
	private boolean isPermanentColumn(String columnName) {
		return (PCeNTableView.STRUCTURE.equals(columnName) || 
				PCeNTableView.NBK_BATCH_NUM.equals(columnName) || 
				PCeNTableView.STATUS.equals(columnName) ||
				PCeNTableView.QUICK_LINK.equals(columnName) ||
				PCeNTableView.PURIFICATION_SERVICE_DATA.equals(columnName) || 
				PCeNTableView.BATCH_ANALYTICAL_COMMENTS.equals(columnName));
	}
	
	public int getColIndex(String colName) {
		for (int i = 0; i < getColumnCount(); ++i) {
			String name = getColumnName(i);
			if (StringUtils.equals(name, colName)) {
				return i;
			}
		}
		return -1;
	}
	
	public void showStructureColumn(boolean show) {
		if (show) {
			removeColumn(structureColumn);
			addColumn(structureColumn);
			int index = -1;
			for (int i = 0; i < getColumnCount(); ++i) {
				if (StringUtils.equals(PCeNTableView.STRUCTURE, getColumnName(i))) {
					index = i;
					break;
				}
			}
			if (index != -1) {
				getColumnModel().moveColumn(index, 0);
			}
		} else {
			removeColumn(structureColumn);
		}
		if (toolBar != null) {
			toolBar.changeStructureCheckBox(show);
		}
	}
	
	public void addInstrument(String instrumentType) {
		TableColumnModel tableColModel = this.getColumnModel();
		Enumeration<TableColumn> e = tableColModel.getColumns();
		while (e.hasMoreElements()) {
			TableColumn col = e.nextElement();
			if (((String) col.getHeaderValue()).equalsIgnoreCase(instrumentType))
				return;  // instrument column already exists
		}
		// Update the table model to keep it in synch
		((AnalyticalDetailTableModel) this.getModel()).addColumn(instrumentType);
		
		int modelIndex = ((AnalyticalDetailTableModel) this.getModel()).getModelIndexFromHeaderName(instrumentType);
		if (modelIndex > 0) {
			TableColumn col = new TableColumn(modelIndex);
			// Ensure that auto-create is off
			if (this.getAutoCreateColumnsFromModel()) {
				throw new IllegalStateException();
			}	
			col.setHeaderValue(instrumentType);
			this.addColumn(col);
			this.setRenderer(instrumentType.toString());
		} else {
			log.error("addInstrument updating the column model before the table model is updated");
		}
	}
	
	protected void setRenderers() {
		int colCount = this.getModel().getColumnCount();
		for (int i = 0; i < colCount; i++) {
			String name = getColumnName(i);
			if (name == null) {
				return;
			}
			setRenderer(name);
		}
	}
	
	private void setRenderer(String colName) {
		TableColumn col = getColumn(colName);
		col.setCellRenderer(cellRenderer);
		col.setHeaderRenderer(headerRenderer);	
		if (colName.equalsIgnoreCase(PCeNTableView.STRUCTURE)) {
			col.setCellRenderer(new StructureTableCellRenderer());
			col.setCellEditor(new AnalyticalTableNotEditableCell());	
		} else if (colName.equals(PCeNTableView.NBK_BATCH_NUM)) {
			col.setCellRenderer(cellRenderer);
			col.setCellEditor(new AnalyticalTableNotEditableCell());
		} else if (colName.equals(PCeNTableView.STATUS)) {
			if (this.tableModel.isColumnEditable(colName)) {
				col.setCellEditor(new PCeNTableViewCellEditor(new JComboBox()));
				col.setCellRenderer(new CeNStatusComboBoxRenderer(new JComboBox()));
			}
		} else if (colName.equals(PCeNTableView.QUICK_LINK)) {
			if (this.tableModel.isColumnEditable(colName)) {
				col.setCellRenderer(new ButtonCellRenderer());
				col.setCellEditor(new ButtonCellEditor(this));
			} else
				col.setCellEditor(new AnalyticalTableNotEditableCell());
		} else if (colName.equals(PCeNTableView.PURIFICATION_SERVICE_DATA)) {
			if (this.tableModel.isColumnEditable(colName)) {
				col.setCellRenderer(new ButtonCellRenderer());
				col.setCellEditor(new ButtonCellEditor(this));
			} else
				col.setCellEditor(new AnalyticalTableNotEditableCell());
		} else if (colName.equals(PCeNTableView.BATCH_ANALYTICAL_COMMENTS)) {
			if (this.tableModel.isColumnEditable(colName)) {
				JTextArea textArea = new JTextArea();
				textArea.setLineWrap(true);
				textArea.setWrapStyleWord(true);
				col.setCellEditor(new PCeNTableViewCellEditor(textArea));
				textArea = new JTextArea();
				textArea.setLineWrap(true);
				textArea.setWrapStyleWord(true);
				textArea.setEnabled(false);
				col.setCellRenderer(new CeNTableCellAlignedComponentRenderer(textArea));
			} else
				col.setCellEditor(new AnalyticalTableNotEditableCell());
		} else {
			col.setCellRenderer(new InstrumentTypeCellRenderer());
			col.setCellEditor(new AnalyticalTableNotEditableCell());
		}
	}
	
	
	public String getColumnName(int column) {
		return this.getColumnModel().getColumn(column).getHeaderValue().toString();
	}
	
	public BatchModel getBatchAt(int row) {
		if (this.batches.size() > row)
			return (BatchModel) batches.get(row);
		else 
			return null;
	}
 
	/**
	 * No-op right now.
	 * @param analyticalUtility
	 */
	public void setAnalyticalDataModel(PAnalyticalUtility analyticalUtility) {
	}

	/**
	 * for "Map To"
	 * 
	 * @param e
	 * @param sampleRefFrom
	 */
	public void popUp(MouseEvent e, final String sampleRefFrom) {
		if (analyticalUtility != null && isEditable) {
			// JMenuItem uploadMenu = new JMenuItem("Upload a PDF File to AnalyticalService");
			ArrayList<String> al_AllBatches = analyticalUtility.getSampleRefsFromBatchInfo(BatchType.ACTUAL_PRODUCT);
			if (al_AllBatches.isEmpty() == false) {
				if (al_AllBatches.contains(sampleRefFrom))
					popupMenu = this.createOptionsPopup(sampleRefFrom, false);
				else { // this is from another experiment
					popupMenu = this.createOptionsPopup(sampleRefFrom, true);
				}
				
				popupMenu.setEnabled(pageModel.isEditable());
				
				if (popupMenu.getComponentCount() > 0) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
	    }		
	}

	/**
	 * refreshes the table view
	 * 
	 */
	public void reload() {
		((AnalyticalDetailTableModel) getModel()).cleanExternalBatchList();
		revalidate();
	}
	
	
//	/**
//	 * 
//	 */
//	private void buildSampleRefTree() {
//		if (analyticalSampleRefContainer != null) {
//			//analyticalSampleRefContainer.setSummaryTableViewer(this);
//			analyticalSampleRefContainer.fillAnalyticalSampleRefComboBox();
//		}
//	}

	/**
	 * initializes the column sizes
	 * 
	 * 
	 */
	public void adjustColWidths() {
		for (int colidx=0; colidx<this.getColumnCount(); colidx++) {
			String name = getColumnName(colidx);
			if (name == null)
				return;
			TableColumn col = getColumn(name);
			if (name.equalsIgnoreCase(PCeNTableView.STRUCTURE)) {
				col.setPreferredWidth(140);
			} else if (name.equalsIgnoreCase(PCeNTableView.NBK_BATCH_NUM)) {
				col.setPreferredWidth(120);
			} else if (name.equalsIgnoreCase(PCeNTableView.BATCH_ANALYTICAL_COMMENTS)) {
				col.setPreferredWidth(140);
			} else if (name.equalsIgnoreCase(PCeNTableView.STATUS)) {
				col.setPreferredWidth(100);
			} else {
				col.setPreferredWidth(40);
			}
		}
	}

	/**
	 * does the map, the from row disappears when the tabled is reloaded values can be seen for the To row
	 * 
	 * @param sampleRefFrom
	 * @param sampleRefTo
	 */
	public void performMap(String sampleRefFrom, String sampleRefTo) {
		analyticalUtility.performMap(sampleRefFrom, sampleRefTo);
		reload();
	}

	public void setAnalyticalSampleRefContainer(PAnalyticalSampleRefContainer analyticalSampleRefContainer) {
		this.analyticalSampleRefContainer = analyticalSampleRefContainer;
	}

	public void performPurificationServiceDataSearch(String sampleRef) {
		final String cenSampleRef = sampleRef;
		final String progressStatus = "Performing PurificationService Data Search ...";
		CeNJobProgressHandler.getInstance().addItem(progressStatus);
		SwingWorker quikLink = new SwingWorker() {
			String pureSampleRefStr = "";
			ArrayList<String> pureSampleRefs = new ArrayList<String>();
			ArrayList<String> results = new ArrayList<String>();
			
			public Object construct() {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				ArrayList<String> sites = new ArrayList<String>();
				sites.add(MasterController.getUser().getSiteCode());

				// Need to find the notebook batch # that corresponds to this crude notebook batch #
			    CompoundMgmtServiceDelegate cmsd;
				try {
                    cmsd = new CompoundMgmtServiceDelegate();
                    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!
                    // TODO Need to get Pure Batch No
                    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!
                    CachedRowSet ocrs = null; //cmsd.getPureBatchNoFromCrudeBatchNo(cenSampleRef);
    				if (ocrs != null) {
						ocrs.beforeFirst();
						
						while (ocrs.next()) {
							String pureSampleRef = ocrs.getString("NOTEBOOK");
							
							if (pureSampleRefStr.length() > 0) pureSampleRefStr += ", ";
							pureSampleRefStr += pureSampleRef;
							pureSampleRefs.add(pureSampleRef);
						}
    				}
    				boolean doPreQCSearch = true;
// TODO: Determine if PurificationService is implemented or not
//    				doPreQCSearch = shouldDoPreQcSearch();
    				if (pureSampleRefs.size() == 0) {
    					if (doPreQCSearch) {
	    					int answer = JOptionPane.showConfirmDialog(MasterController.getGuiController().getGUIComponent(), 
									"Unable to find any PurificationService registered batches for '" + cenSampleRef + "'\n" +
									"Do you want to search for any additional PurificationService Pre-QC Analytical data?", "PurificationService Data Search", JOptionPane.YES_NO_OPTION);
	    					if (answer == JOptionPane.YES_OPTION) {
	    						ArrayList<String> a = new ArrayList<String>();
// TODO: Determine if PurificationService is implemented or not
//	    						a = analyticalUtility.performPurificationServiceDataSearch(cenSampleRef, null, sites);
	    						if (a != null && a.size() > 0) 
	    							results.addAll(a);
	    	    				else
	    	    					JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "No additional PurificationService Pre-QC Analytical data found for '" + cenSampleRef + "'",
	    	    							 						"PurificationService Data Search", JOptionPane.INFORMATION_MESSAGE);
	    						return "";
	    					} else
	    						return null;
    					} else
       				 		JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "No PurificationService registered batches found for '" + cenSampleRef + "'",
										"PurificationService Data Search", JOptionPane.INFORMATION_MESSAGE);
    				} else {
						boolean firstTime = doPreQCSearch;
						for (String purificationServiceSampleRef : pureSampleRefs) {
    						ArrayList<String> a = new ArrayList<String>();
// TODO: Determine if PurificationService is implemented or not
//	    					ArrayList a = analyticalUtility.performPurificationServiceDataSearch((firstTime) ? cenSampleRef : null, purificationServiceSampleRef, sites);
	    					if (a != null && a.size() > 0) 
	    						results.addAll(a);
	       				 	else if (firstTime)
	       				 		JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "No additional PurificationService Pre-QC or Post-QC Analytical data found for '" + ((cenSampleRef != null) ? cenSampleRef + "' or '" : "'") +  purificationServiceSampleRef + "'",
	       				 										"PurificationService Data Search", JOptionPane.INFORMATION_MESSAGE);
	       				 	else
	       				 		JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "No additional PurificationService Post-QC Analytical data found for '" + purificationServiceSampleRef + "'",
	 										"PurificationService Data Search", JOptionPane.INFORMATION_MESSAGE);

	    					firstTime = false;
    					}
						return "";  
    				}
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "Error occurred while performing PurificationServiceDataSearch", e);
				} finally {
					cmsd = null;
				}
				
				return null;
			}
			public void finished() {
				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
				if (results.size() > 0) {
					PurificationServiceDataJFrame frame = new PurificationServiceDataJFrame(cenSampleRef, results);
					frame.setVisible(true);
				}	
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		};
		quikLink.start();
	}

	public void performQuickLink(String sampleRef) {
		final String s = sampleRef;
		final String progressStatus = "Performing Quick Link ...";
		CeNJobProgressHandler.getInstance().addItem(progressStatus);
		SwingWorker quikLink = new SwingWorker() {
			boolean success;

			public Object construct() {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				success = analyticalUtility.performQuickLink(s, null);
				return null;
			}

			public void finished() {
				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
				if (success) {
					reload();
				} else {
					JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(),
							"Unable to find any Quick Link results for batch " + s, "Quick Link", JOptionPane.INFORMATION_MESSAGE);
				}
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		};
		quikLink.start();
	}

	/**
	 * @param s
	 */
	public void updateComments(String sampleRef, String comments) {
		analyticalUtility.updateComments(sampleRef, comments);
		// reload();
	}

	public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
		try {
			Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
			if (c instanceof JComponent) {
				JComponent jc = (JComponent) c;
				String newText = this.getColumnName(vColIndex);
				jc.setToolTipText(newText);
			}
			return c;
		} catch (RuntimeException e) {
			log.error("Failed to prepare renderer for Analytical details table", e);
			return null;
		}
	}
	
	private JPopupMenu createOptionsPopup(final String sampleRef, boolean external) {
		JPopupMenu optionsPopup = new JPopupMenu();
		optionsPopup = new JPopupMenu();
		JMenuItem unlinkAllItem = new JMenuItem("Unlink All");
		unlinkAllItem.setEnabled(pageModel.isEditable());
		unlinkAllItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				handleUnlinkAllAction(sampleRef);
			}
		});
		JMenuItem mapAllTo = new JMenuItem("Map All to");
		mapAllTo.setEnabled(pageModel.isEditable());
//		mapAllTo.addMouseListener(new MouseAdapter() {
//			public void actionPerformed(ActionEvent evt) {
//				displayMapToDialog(sampleRef);
//			}			
//		});
		mapAllTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				displayMapToDialog(sampleRef);
			}
		});
		JMenuItem uploadPDFItem = new JMenuItem("Upload a PDF File to Analytical Information Service");
		uploadPDFItem.setEnabled(pageModel.isEditable());
		if (!external) {
			uploadPDFItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					handleUploadPDFAction(sampleRef);
				}
			});
		}
		optionsPopup.add(unlinkAllItem);
		unlinkAllItem.setVisible(!analyticalUtility.isAnalysisEmptyForBatch(sampleRef));
		optionsPopup.add(mapAllTo);
		mapAllTo.setVisible(!analyticalUtility.isAnalysisEmptyForBatch(sampleRef));
		//mapAllTo.setVisible(this.updateBatchListOnMenu(menu, applyToAllLinks)(mapAllTo, true));
		if (!external)
			optionsPopup.add(uploadPDFItem);
		return optionsPopup;
	}
	
	/**
	 * This method is provide for the Analytical summary object where
	 * the menu options are visible on the right click on a table row.
	 * @return Returns the optionsPopup.
	 *
	public JPopupMenu getOptionsPopupForAnalyticalSummary(String sampleRef) {
		unlinkAllItem.setVisible(!analyticalUtility.isAnalysisEmptyForBatch(sampleRef));
		
		mapAllTo.setVisible(updateBatchListOnMenu(mapAllTo, true));
		
		try {
			uploadPDFItem.setVisible(true);
		} catch (Exception e) { 
			uploadPDFItem.setVisible(false); 
		}

		return optionsPopup;
	} */

//	private boolean updateBatchListOnMenu(JMenu menu, boolean applyToAllLinks) {
//		boolean isBatchListAvailable = false;
//		menu.removeAll();
//		List batchList = pageModel.getAllProductBatchModelsInThisPage();
//		//List batchList = nbPage.getBatchCache().getBatches(BatchType.ACTUAL_PRODUCT_ORDINAL);// 32= ACTUAL BatchType
//		// System.out.println("Actual Batches count : "+ batchList.size());
//		List tempBatchNameList = new ArrayList();
////		String selectedBatchNumber = (String) jComboBoxSampleRefs.getSelectedItem();
////		if (batchList.isEmpty() || !hasLinks(selectedBatchNumber))
////			return isBatchListAvailable;
////		//////////////////////// Collections.sort(batchList);
////		for (int i = 0; i < batchList.size(); i++) {
////			ProductBatchModel batchObj = (ProductBatchModel) batchList.get(i);
////			/////////AbstractBatch batchObj = (AbstractBatch) batchList.get(i);
////			String batchNumber = batchObj.getBatchNumber().getBatchNumber();
////			/////////String batchNumber = batchObj.getBatchNumberAsString();
////			if (batchNumber.trim().equals("") || batchNumber.equals(selectedBatchNumber))
////				continue;
////			tempBatchNameList.add(batchNumber);
////			JMenuItem batchItem = new JMenuItem(batchNumber);
////			batchItem.addActionListener(new ActionListener() {
////				public void actionPerformed(ActionEvent evt) {
////					handleMapToBatch(evt);
////				}
////			});
////			menu.add(batchItem);
////			isBatchListAvailable = true;
////		}
////		// Following code handles Black batches Analysis.
////		if (selectedAnalysis != null && !applyToAllLinks) {
////			// Following check will ensure that if sample reference is not
////			// available then
////			// sample reference is available in menu for mapping.
////			if (!selectedAnalysis.getAnalyticalServiceSampleRef().equals(selectedBatchNumber)) {
////				if (!tempBatchNameList.contains(selectedAnalysis.getAnalyticalServiceSampleRef())) {
////					JMenuItem batchItem = new JMenuItem(selectedAnalysis.getAnalyticalServiceSampleRef());
////					batchItem.addActionListener(new ActionListener() {
////						public void actionPerformed(ActionEvent evt) {
////							handleMapToBatch(evt);
////						}
////					});
////					menu.add(new JSeparator());
////					menu.add(batchItem);
////					isBatchListAvailable = true;
////				}
////			}
////		}
////		applyMapToAllLinks = applyToAllLinks;
//		/////return isBatchListAvailable;
//		return false;
//	}
	/**
	 * 
	 * @param evt
	 */
	protected void handleUploadPDFAction(String sampleRef) {
		AnalysisUploadDialog aJDialog = new AnalysisUploadDialog(sampleRef, MasterController.getGUIComponent(), analyticalUtility);
		//aJDialog.setAnalyticalUtility(analyticalUtility);
		aJDialog.setVisible(true);
	}
	
	public void handleUnlinkAllAction(String sampleRef) {
		//if (jComboBoxSampleRefs.getSelectedItem() != null) {
			int selection = JOptionPane.showConfirmDialog(null, "Are you sure you want to unlink all for the Sample Reference "
					+ sampleRef + "?", "UnLink All", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (selection == JOptionPane.YES_OPTION) {
				// Fixes an unlinkall problem for a sample
				performUnLinkAll(sampleRef);
				updateAnalyses();
				// performUnLinkAll(nbPage.getNotebookRefAsString());
			}
		//}
	}

	/**
	 * 
	 */
	protected void performUnLinkAll(final String sampleRef) {
		final String s = "Performing UnLink All for " + sampleRef + " ...";
		CeNJobProgressHandler.getInstance().addItem(s);
		SwingWorker unLinkAll = new SwingWorker() {
			public Object construct() {
				try {
					analyticalUtility.unlinkAll(sampleRef);
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, "Exception occured UnLinkAll.", e);
				} finally {
					CeNJobProgressHandler.getInstance().removeItem(s);
				}
				return null;
			}

			public void finished() {
				//summaryViewer.reload();
			}
		};
		unLinkAll.start();
	}

	/**
	 * Use the JOptionsPane to display the list of batches that can be mapped to.
	 * @param sampleRefFrom
	 */
	private void displayMapToDialog(String sampleRefFrom) {
		List<String> al_allCeNSampleRefs = analyticalUtility.getAllSampleRefs();
		List<String> selectableBatchesList = new ArrayList<String>();
		if (al_allCeNSampleRefs.contains(sampleRefFrom)) {
			ArrayList<String> al_AllBatches = analyticalUtility.getSampleRefsFromBatchInfo(BatchType.ACTUAL_PRODUCT);
			for (int i=0; i < al_AllBatches.size(); i++) {
		    	if (al_AllBatches.get(i).toString().equals(sampleRefFrom)) continue;
		    	if (selectableBatchesList.contains(al_AllBatches.get(i)) == false)
		    		selectableBatchesList.add(al_AllBatches.get(i));
			}
		}
		Collections.sort(selectableBatchesList);
//		Collections.sort(selectableBatchesList, new Comparator<String>() {
//			public int compare(String o1, String o2) {
//				if (o1 == null || o2 == null)
//					return 0;
//				if (o1 instanceof String && o2 instanceof String) {
//					String s1 = o1;
//					String s2 = o2;
//					if (s1.length() < 14 || s2.length() < 14)
//						return 0;
//					s1 = s1.substring(14, s1.length()-2);
//					s2 = s2.substring(14, s2.length()-2);
//					
//					try {
//						int i1 = Integer.parseInt(s1);
//						int i2 = Integer.parseInt(s2);
//						if (i1 > i2) return 1;
//						else if (i1 < i2) return -1;
//						else return 0;
//					} catch (NumberFormatException e) {
//						return 0;
//					}
//				}
//				return 0;
//			}
//		});
		String[] selectableBatches = (String[]) selectableBatchesList.toArray(new String[] {}); 
		JPanel mapToSelectionPanel = new JPanel();
		JList list = new JList(selectableBatches); 
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(170, 300));
		mapToSelectionPanel.add(listScroller);
		
		Object[] message = new Object[1];
		int index = 0;
		message[index++] = mapToSelectionPanel;
		// Options
		String[] options = { "OK", "Cancel" };
		int result = JOptionPane.showOptionDialog(MasterController.getGUIComponent(), // the parent that the
				// dialog blocks
				message, // the dialog message array
				"Map To Batch", // the title of the dialog window
				JOptionPane.DEFAULT_OPTION, // option type
				JOptionPane.PLAIN_MESSAGE, // message type
				null, // optional icon, use null to use the default icon
				options, // options string array, will be made into buttons
				options[1] // option that should be made into a default button
				);
		switch (result) {
			case 0: // OK
				Object[] values = list.getSelectedValues();
				if (values != null && values.length > 0) {
					String sampleRefTo = values[0].toString();
					System.out.println(values[0].toString());
					this.performMap(sampleRefFrom, sampleRefTo);
				}
				break;
			case 1: // cancel
				break;
		}
	}

	public void stateChanged(ChangeEvent e) {
		int height = ((JSlider) e.getSource()).getValue();
		setRowHeight(height);
		this.setSelectedAreas(null);
		repaint();
	}

	public void setValueAt(Object value, int row, int column) {
		((AnalyticalDetailTableModel) getModel()).setValueAt(value, row, column);
	}
	

	public void refresh() {
		// check for discrepancy in column model
		((AnalyticalDetailTableModel) getModel()).cleanExternalBatchList();
		reload();
	}
		
//	public void refreshSampleList() {
//		//this.analyticalSampleRefContainer.fillAnalyticalSampleRefComboBox();
//		this.updateAnalyses();
//	}
	
	public void updateAnalyses() {
		tableModel.updateAnalyses();
		this.analyticalSampleRefContainer.fillAnalyticalSampleRefComboBox();
		if (getSelectedRow() > -1) {
			if (analyticalSampleRefContainer != null) {
				String sampleRef = "";
				if (getColumnName(0).equalsIgnoreCase(PCeNTableView.STRUCTURE)) 
					sampleRef = getValueAt(getSelectedRow(), 1).toString();
				else
					sampleRef = getValueAt(getSelectedRow(), 0).toString();
				analyticalSampleRefContainer.buildAnalyticalTree(sampleRef);
			}
		}
		this.reload();
	}
/*	
	No-op
	public void updateAnalyses(List analysisList) {
		if (log.isDebugEnabled()) {
			log.debug("updateAnalyses not implemented");
		}
	}
*/
	public void updateCompounds() {
		((AnalyticalDetailTableModel) this.getModel()).updateCompounds(this.pageModel.getAllProductBatchModelsInThisPage());
	}
}