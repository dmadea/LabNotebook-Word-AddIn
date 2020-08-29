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
package com.chemistry.enotebook.client.gui.page.regis_submis;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.page.stoichiometry.search.CompoundAndBatchInfoUpdater;
import com.chemistry.enotebook.compoundregistration.classes.BatchRegStatusTrackingVO;
import com.chemistry.enotebook.compoundregistration.classes.RegistrationDetailsVO;
import com.chemistry.enotebook.compoundregistration.classes.RegistrationVialDetailsVO;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.batch.BatchResidualSolventModel;
import com.chemistry.enotebook.domain.batch.BatchSolubilitySolventModel;
import com.chemistry.enotebook.experiment.common.codetables.SaltCodeCache;
import com.chemistry.enotebook.experiment.common.units.UnitCache;
import com.chemistry.enotebook.experiment.common.units.UnitCache2;
import com.chemistry.enotebook.experiment.datamodel.batch.*;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.registration.delegate.RegistrationServiceDelegate;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.utils.*;
import com.chemistry.enotebook.utils.SwingWorker;
import info.clearthought.layout.TableLayout;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * No longer being used.  Used to be called by ParalleNotebookPageGUI object.  
 */
public class Reg_subsumContainer extends JPanel implements BatchRegistrationListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1689086732950053288L;
	private JScrollPane jScrollPane;
	private SimpleJTable jTableRegBatches;
	private int selectedRow = -1;
	private RegSubHandler regSubHandler = new RegSubHandler();
	private Reg_subsumContainerTableModel reg_subsumContainerTableModel = new Reg_subsumContainerTableModel();
	private ArrayList<ProductBatchModel> batchList = new ArrayList<ProductBatchModel>();
	private HashMap<String, String> jobIdBatchNoMap = new HashMap<String, String>(); // <JobId, batchNumberAsString>
	private boolean isReEntrent = false;
	private boolean isEditable = false;
	private NotebookPageGuiInterface parentDialog;
	private NotebookPageModel nbPage = null;
	private ProductBatchModel selectedBatch = null;
	private JToolBar jToolBarOptions;
	private JButton jButtonRegSub;
	private JButton jButtonRegSDF;
	private JButton jButtonRegPull;
	private SwingWorker worker1 = null;

	public Reg_subsumContainer() {
		initGUI();
	}

	public void dispose() {
		if (worker1 != null)
			worker1.interrupt();
		worker1 = null;
		nbPage = null;
		setSelectedBatch(null);
		if (regSubHandler != null)
			regSubHandler.dispose();
		if (batchList != null)
			batchList.clear();
		batchList = null;
		if (jobIdBatchNoMap != null)
			jobIdBatchNoMap.clear();
		jobIdBatchNoMap = null;
		regSubHandler = null;
		parentDialog = null;
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			setLayout(new BorderLayout());
			jScrollPane = new JScrollPane();
			jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			setPreferredSize(new java.awt.Dimension(540, 150));
			setIgnoreRepaint(true);
			add(jScrollPane, BorderLayout.CENTER);
			jTableRegBatches = new SimpleJTable() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -9099053659009439087L;

				public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
					Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
					return thisRenderer(c, renderer, rowIndex, vColIndex);
				}
			};
			jTableRegBatches.setAutoResizeMode(0);
			jTableRegBatches.setPreferredScrollableViewportSize(new java.awt.Dimension(1000, 400));
			jScrollPane.add(jTableRegBatches);
			jScrollPane.setViewportView(jTableRegBatches);
			jTableRegBatches.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jTableRegBatchesMouseClicked(evt);
				}
			});
			jTableRegBatches.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			double toolBarSize[][] = {
					{ 20, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP,
							CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN,
							CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN }, { 20, 2 } };
			jToolBarOptions = new JToolBar();
			TableLayout jToolBarLayout = new TableLayout(toolBarSize);
			jToolBarOptions.setLayout(jToolBarLayout);
			jToolBarOptions.setBorderPainted(false);
			jToolBarOptions.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			add(jToolBarOptions, BorderLayout.NORTH);
			// Add buttons to toolbar
			jButtonRegSub = new JButton();
			jButtonRegSub.setText("Register & Submit");
			jButtonRegSub.setToolTipText("Register & Submit Checked Batches");
			jButtonRegSub.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
					"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonRegSub);
			jToolBarOptions.add(jButtonRegSub, "2,0");
			jButtonRegSub.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					doRegistration();
				}
			});
			jButtonRegSDF = new JButton();
			jButtonRegSDF.setText("Create SD File");
			jButtonRegSDF.setToolTipText("Create SD File for Selected Batches");
			jButtonRegSDF.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
					"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonRegSDF);
			jToolBarOptions.add(jButtonRegSDF, "4,0");
			jButtonRegSDF.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					createRegistrationSDFile();
				}
			});
			jButtonRegPull = new JButton();
			jButtonRegPull.setText("Retrieve Reg Info");
			jButtonRegPull.setToolTipText("Retrieve Existing Registration Information for all Batches");
			jButtonRegPull.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
					"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonRegPull);
			jToolBarOptions.add(jButtonRegPull, "6,0");
			jButtonRegPull.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					pullRegistrationDetails(true);
				}
			});
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		// set table model
		refresh();
	}

	/**
	 * Curreantly called only when Registration/Submission TabbedPane is clicked Refreshes the TableView and latest values of the
	 * Product batches are copied.
	 * 
	 */
	public void refresh() {
		if (getNbPage() != null) {
			batchList = regSubHandler.buildBatchList();
			int i = jTableRegBatches.getSelectedRow();
			// table settings
			reg_subsumContainerTableModel.setBatchList(batchList);
			reg_subsumContainerTableModel.setEditable(isEditable);
			reg_subsumContainerTableModel.fireTableDataChanged();
			jTableRegBatches.setModel(reg_subsumContainerTableModel);
			jTableRegBatches.setRowHeight(20);
			setHeaderSize();
			// bug fix start
			// Artifact ID: 22431 by pavan
			if (jTableRegBatches.getRowCount() > 0 && i >= 0 && i < jTableRegBatches.getRowCount()) {
				jTableRegBatches.setRowSelectionInterval(i, i);
				setSelectedBatch(reg_subsumContainerTableModel.getBatchList().get(i));
				// if (selectedBatch!=null) {
				// updateDependents(selectedBatch);
				// }
			}
			if (!isReEntrent) {
				if (statusDirty(batchList)) {
					buildJobIdBatchNumberMap();
					getRegistrationStatus();
				}
				isReEntrent = true;
			}
			jButtonRegSub.setEnabled(selectedBatch.isEditable() && isEditable && jTableRegBatches.getRowCount() > 0 && !MasterController.getUser().getNTUserID().startsWith("E_"));
			jButtonRegPull.setEnabled(selectedBatch.isEditable() && isEditable && jTableRegBatches.getRowCount() > 0);
			jButtonRegSDF.setEnabled(jTableRegBatches.getRowCount() > 0);
			if (i == -1 && jTableRegBatches.getRowCount() > 0) {
				jTableRegBatches.setRowSelectionInterval(0, 0);
				setSelectedBatch(reg_subsumContainerTableModel.getBatchList().get(0));
				// if (selectedBatch!=null) {
				// updateDependents(selectedBatch);
				// }
			} else if (batchList.size() == 0 && selectedBatch != null && selectedBatch.getBatchNumberAsString().length() > 0) {
				selectedBatch = new ProductBatchModel();
				selectedBatch.setBatchType(BatchType.ACTUAL_PRODUCT);
				selectedBatch.getRegInfo().setStatus(BatchRegistrationInfo.PROCESSING);
				selectedBatch.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.PROCESSING);
			}
			if (!nbPage.isLoadingFromDB())
				updateDependents();
			setUpCellRenderer();
			// jTableRegBatches.setDefaultRenderer(String.class, new
			// WhiteCellRenderer());
			// bug fix end
		}
	}

	/**
	 * updates the dependent containers with the selected batch
	 * 
	 * @param selectedBatch
	 */
	private void updateDependents() {
		/*TODO Bo Yang 3/14/07
		getParentDialog().getRegVnV_cont().setSelectedBatch(selectedBatch);
		getParentDialog().getRegVnV_cont().updateBatchDisplay();
		getParentDialog().getCompReg_cont().setSelectedBatch(selectedBatch);
		getParentDialog().getCompReg_cont().updateDisplay();
		getParentDialog().getHazHandling_cont().setSelectedBatch(selectedBatch);
		getParentDialog().getHazHandling_cont().updateDisplay();
		getParentDialog().getSampleSubSum_cont().setSelectedBatch(selectedBatch);
		getParentDialog().getSampleSubSum_cont().updateDisply();
		*/
	}

	/**
	 * 
	 */
	private void setUpCellRenderer() {
		DefaultTableCellRenderer strRendr = (DefaultTableCellRenderer) jTableRegBatches.getDefaultRenderer(String.class);
		strRendr.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumnModel vColumModel = jTableRegBatches.getColumnModel();
		TableColumn column0 = vColumModel.getColumn(Reg_subsumContainerTableModel.BATCH_ID_IDX);
		// column4.setCellRenderer(new GrayOutCellRender());
		column0.setPreferredWidth(140);
		TableColumn column4 = vColumModel.getColumn(Reg_subsumContainerTableModel.SELECT_TO_REGISTER_IDX);
		// column4.setCellRenderer(new GrayOutCellRender());
		column4.setPreferredWidth(120);
		// TableColumn column6 =
		// vColumModel.getColumn(Reg_subsumContainerTableModel.SELECT_TO_SUBMIT_IDX);
		// //column6.setCellRenderer(new GrayOutCellRender());
		// column6.setPreferredWidth(110);
		TableColumn column7 = vColumModel.getColumn(Reg_subsumContainerTableModel.REGISTERY_NUMBER_IDX);
		// column7.setCellRenderer(new GrayOutCellRender());
		column7.setPreferredWidth(140);
		TableColumn column8 = vColumModel.getColumn(Reg_subsumContainerTableModel.STATUS_IDX);
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.LEFT);
		column8.setCellRenderer(renderer);
		column8.setPreferredWidth(160);
	}

	public void setHeaderSize() {
		// Get the column model.
		TableColumnModel colModel = jTableRegBatches.getColumnModel();
		JTableHeader th = jTableRegBatches.getTableHeader();
		// Need to bold the headers
		CeNGUIUtils.styleComponentText(Font.BOLD, th);
		// sets the header and column size as per the Header text
		for (int i = 0; i < colModel.getColumnCount(); i++) {
			// Get the column name of the given column.
			String value = jTableRegBatches.getColumnName(i);
			// Calculate the width required for the column.
			FontMetrics metrics = jTableRegBatches.getFontMetrics(jTableRegBatches.getFont());
			int width = metrics.stringWidth(value) + (40 * colModel.getColumnMargin());
			// Get the column at index pColumn, and set its preferred width.
			colModel.getColumn(i).setPreferredWidth(width);
		}
	}

	private Component thisRenderer(Component c, TableCellRenderer renderer, int rowIndex, int vColIndex) {
		if (c instanceof JComponent) {
			JComponent jc = (JComponent) c;
			String newText = null;
			if (rowIndex >= 0 && rowIndex < jTableRegBatches.getRowCount()
					&& vColIndex == Reg_subsumContainerTableModel.BATCH_ID_IDX
					|| vColIndex == Reg_subsumContainerTableModel.REGISTERY_NUMBER_IDX) {
				ProductBatchModel ab = (((Reg_subsumContainerTableModel) jTableRegBatches.getModel()).getBatchList()
						.get(rowIndex));
				if (ab != null)
					newText = CeNGUIUtils.getCompoundToolTipText(ab.getCompound());
			}
			jc.setToolTipText(newText);
		}
		return c;
	}

	public class WhiteCellRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7593638996568644043L;
		private ProductBatchModel selectedBatch;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component result = super.getTableCellRendererComponent(table, value, false, hasFocus, row, column);
			if (column == Reg_subsumContainerTableModel.AMOUNT_SUBMITTED_IDX
					|| column == Reg_subsumContainerTableModel.REGISTERY_NUMBER_IDX)
				result.setBackground(Color.white);
			// if (value.equals("Registering") ) {
			//	        	
			// }
			setSelectedBatch(((Reg_subsumContainerTableModel) table.getModel()).getBatchList().get(row));
			if (column == Reg_subsumContainerTableModel.SELECT_TO_REGISTER_IDX) {
				if (selectedBatch.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.REGISTERED)) {
					result.setEnabled(false);
					result.setBackground(Color.GRAY);
				}
			}
			// if (column ==
			// Reg_subsumContainerTableModel.SELECT_TO_SUBMIT_IDX) {
			// if
			// (selectedBatch.getRegInfo().getSubmissionStatus().equals(BatchRegistrationInfo.SUBMITTED)
			// ) {
			// result.setEnabled(false);
			// result.setBackground(Color.GRAY);
			// }
			// }
			return result;
		}

		/**
		 * @return Returns the selectedBatch.
		 */
		public ProductBatchModel getSelectedBatch() {
			return selectedBatch;
		}

		/**
		 * @param selectedBatch
		 *            The selectedBatch to set.
		 */
		public void setSelectedBatch(ProductBatchModel selectedBatch) {
			this.selectedBatch = selectedBatch;
		}
	}
//this is not used in 1.2
	private void doRegistration() {
		// registration process needs to check the number os selected batches
		// if that is less than 20, then perform interactive registration,
		// otherwise
		// batch registration since singleton process is faster.
		jButtonRegSub.setEnabled(false);
		interactiveRegistration();
		getRegistrationStatus();
	}
//This is not used in 1.2
	public void interactiveRegistration() {
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				try {
					// //System.out.println("Inside the registration
					// thread...");
					boolean processedRec = false;
					for (int i = 0; i < reg_subsumContainerTableModel.getRowCount(); i++) {
						ProductBatchModel iBatch = reg_subsumContainerTableModel.getBatchList().get(i);
						if (iBatch.getRegInfo().getRegistrationStatus() != null
								&& !iBatch.getRegInfo().getRegistrationStatus().equals("")) {
							if (iBatch.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.REGISTERING)) {
								processedRec = true;
//								NotebookUser nbu = MasterController.getUser();
//								String batchSDF = buildSDFile(iBatch, true);
								// System.out.println("CompoundRegistration User: " +
								// nbu.getNTUserID() + ", Domain: " +
								// getUserNTDomain(nbu.getNtDomain()));
								String singletonResult = "";
								//singletonResult =	Reg_subsumContainer.this.regSubHandler.performInteractiveRegistration(batchSDF);
								ArrayList<String> jobErrors = new ArrayList<String>();
								String jobid = null;
								// System.out.println("the interactive reg
								// return is: " + singletonResult);
								if (singletonResult != null) {
									jobid = regSubHandler.getJobInfoFromMsg(singletonResult, jobErrors);
									// System.out.println("CompoundRegistration Job ID: " +
									// jobid);
									if (jobid.equals("-1") || jobid.equals("-2")) {
										updateRegSubStatusByJobId(iBatch, jobid, jobErrors);
									} else {
										jobIdBatchNoMap.put(jobid, iBatch.getBatchNumberAsString());
										// reset registration and submission
										// status to prevent user from changing
										// status and reselecting
										iBatch.getRegInfo().setStatus(BatchRegistrationInfo.PROCESSING);
										iBatch.getRegInfo().setJobId(jobid);
										iBatch.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.POST_REGISTERING);
										if (iBatch.getRegInfo().getSubmissionStatus().equals(BatchRegistrationInfo.SUBMITTING)) {
											iBatch.getRegInfo().setSubmissionStatus(BatchRegistrationInfo.POST_SUBMITTING);
										}
									}
									// Reg_subsumContainer.this.updateBatchList(iBatch);
									// return iBatch;
								}// end if singletonResult not null
							} else if (iBatch.getRegInfo().getStatus().equals(BatchRegistrationInfo.PASSED)) {
								// if
								// (iBatch.getRegInfo().getSubmissionStatus().equals(BatchRegistrationInfo.SUBMITTING))
								// {
								// //
								// Reg_subsumContainer.this.getParentDialog().getSampleSubSum_cont().updateBatchContainerStatus();
								// //
								// Reg_subsumContainer.this.getParentDialog().getSampleSubSum_cont().updateBatchScreenStatus();
								// //submit sample to CompoundManagement
								// SampleForTestVO sampleForTestVO = new
								// SampleForTestVO();
								// sampleForTestVO.setBatchTrackingId(iBatch.getRegInfo().getBatchTrackingId());
								//									
								// ArrayList samplesList =
								// iBatch.getRegInfo().getSubmitToBiologistTestList();
								// for (int j = 0; j<samplesList.size(); j++) {
								// BatchSubmissionToBiologistTest
								// batchSubmissionToBiologistTest =
								// (BatchSubmissionToBiologistTest)samplesList.get(j);
								// if
								// (batchSubmissionToBiologistTest.getSubmittedByMM()
								// &&
								// batchSubmissionToBiologistTest.getSubmissionStatus().equals(BatchSubmissionToBiologistTest.SUBMITTING))
								// {
								// sampleForTestVO.setProtocolId(batchSubmissionToBiologistTest.getScreenProtocolID());
								// sampleForTestVO.setScientistCode(batchSubmissionToBiologistTest.getScientistCode());
								// sampleVOList.add(sampleForTestVO);
								// sampleForTestVO.setContainer(false);
								// }
								// }
								//									
								// ArrayList containerList =
								// iBatch.getRegInfo().getSubmitContainerList();
								// for (int j = 0; j<containerList.size(); j++)
								// {
								// BatchSubmissionContainerInfo
								// batchSubmissionContainerInfo =
								// (BatchSubmissionContainerInfo)containerList.get(j);
								// if
								// (batchSubmissionContainerInfo.getSubmissionStatus().equals(BatchSubmissionToBiologistTest.SUBMITTING))
								// {
								// sampleForTestVO.setBarCode(batchSubmissionContainerInfo.getBarCode());
								// sampleForTestVO.setAmountInMg(batchSubmissionContainerInfo.getAmountValue());
								// sampleForTestVO.setContainer(true);
								// sampleVOList.add(sampleForTestVO);
								// }
								// }
								// }
							}
						}
					}// end of for
					if (!processedRec) {
						JOptionPane.showMessageDialog((JInternalFrame)parentDialog,
								"Please first select the desired batches that should be registered by clicking the checkbox(es).");
					} 
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
				}
				return "";
			}

			public void finished() {
				jButtonRegSub.setEnabled(true);
				// Reg_subsumContainer.this.refresh();
			}
		};
		worker1 = worker;
		worker.start();
	}

	private void createRegistrationSDFile() {
		final SwingWorker worker = new CreateSdFileWorker() {
			public Object construct() {
				StringBuffer sdFile = new StringBuffer();
				try {
					int[] selectedRows = jTableRegBatches.getSelectedRows();
					if (selectedRows.length <= 0) {
						JOptionPane.showMessageDialog((JInternalFrame)parentDialog,
										"Please select the desired batches that should be in the SD File\nUse the ctrl/shift key to select indivdual/range of batches.");
					} else {
						for (int i = 0; i < selectedRows.length; i++) {
							ProductBatchModel iBatch = reg_subsumContainerTableModel.getBatchList().get(selectedRows[i]);
							String batchSDF = buildSDFile(iBatch, false);
							if (batchSDF != null && batchSDF.length() > 0)
								sdFile.append(batchSDF);
						}
					}
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
				}
				return (sdFile.length() > 0) ? sdFile.toString() : null;
			}

		};
		
		worker.start();
	}

	private void pullRegistrationDetails(final boolean displayMessages) {
//		int count = 0;
//		for (int i = 0; i < reg_subsumContainerTableModel.getRowCount(); i++) {
//			ProductBatch iBatch = (ProductBatch) reg_subsumContainerTableModel.getBatchList().get(i);
//			if (iBatch.getRegInfo().getRegistrationStatus() == null
//					|| !iBatch.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.REGISTERED)) {
//				ArrayList result = new ArrayList();
//				//result = regSubHandler.getRegistrationInformation(iBatch.getBatchNumberAsString());
//				if (result == null)
//					break;
//				if (result.size() > 0) {
//					iBatch.setParentBatchNumber((String) result.get(0));
//					iBatch.getCompound().setRegNumber((String) result.get(1));
//					iBatch.setConversationalBatchNumber((String) result.get(2));
//					iBatch.getRegInfo().setBatchTrackingId((String) result.get(3));
//					try {
//						iBatch.getRegInfo().setRegistrationDate(NotebookPageUtil.getLocalDate((String) result.get(4)));
//					} catch (Exception e) { /* ignored */
//					}
//					iBatch.getRegInfo().setStatus(BatchRegistrationInfo.PASSED);
//					iBatch.getRegInfo().setErrorMsg("");
//					iBatch.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.REGISTERED);
//					reg_subsumContainerTableModel.fireTableDataChanged();
//					updateBatchList(iBatch);
//					count++;
//				}
//			}
//		}
//		JOptionPane.showMessageDialog(Reg_subsumContainer.this, "" + count + " Record(s) updated.");
    	final String progressStatus = "Retrieving Registration Details ...";
		if (displayMessages) CeNJobProgressHandler.getInstance().addItem(progressStatus);

    	SwingWorker worker = new SwingWorker() {
     		public Object construct() {
     	    	int count = 0;
     			for (int i=0; i < reg_subsumContainerTableModel.getRowCount(); i++) {
     				ProductBatchModel iBatch = reg_subsumContainerTableModel.getBatchList().get(i);
//     				if (iBatch.getRegInfo().getRegistrationStatus() == null || 
//     					!iBatch.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.REGISTERED)) {
 						RegistrationDetailsVO result = null;
 						try {
 							result = regSubHandler.getRegistrationInformation(iBatch.getBatchNumberAsString());
 						} catch (Exception e) {
 							result = null;
 							CeNErrorHandler.getInstance().logExceptionMsg(null, e);
 						}
     					if (result != null) {
     						iBatch.setParentBatchNumber(result.getGlobalNumber());
     						iBatch.getCompound().setRegNumber(result.getGlobalCompoundNumber());
     						iBatch.setConversationalBatchNumber(result.getBatchNumber());
     						if(StringUtils.isNumeric(result.getBatchTrackingNo())) {
     							iBatch.getRegInfo().setBatchTrackingId(Long.parseLong(result.getBatchTrackingNo()));
     						}
         					iBatch.setComments(result.getBatchComment());
         					iBatch.getRegInfo().setCompoundSource(result.getSource());
         					iBatch.getRegInfo().setCompoundSourceDetail(result.getSourceDetail());
     						try {
     							iBatch.getRegInfo().setRegistrationDate(result.getLoadDate());
     						} catch (Exception e) { /* ignored */ }
     						iBatch.getRegInfo().setStatus(BatchRegistrationInfo.PASSED);
     						iBatch.getRegInfo().setErrorMsg("");
     						iBatch.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.REGISTERED);
     						
     						String unit = result.getAmountMadeUnit();
     						if (unit == null || unit.length() == 0) unit = "MG";
     						
     						AmountModel newAmount = new AmountModel(UnitCache2.getInstance().getUnit(unit).getType(), result.getAmountMade());
     						newAmount.setUnit(iBatch.getWeightAmount().getUnit());
     						newAmount.setSigDigits(iBatch.getWeightAmount().getSigDigits());
    						iBatch.setWeightAmount(newAmount);
     						
     						// Check Structure, isomer code, Structure Comment
     						try {
	     						ProductBatchModel btch = new ProductBatchModel();
	     						ParentCompoundModel cmpd = new ParentCompoundModel();
	     						btch.setCompound(cmpd);
	     						cmpd.setRegNumber(result.getGlobalCompoundNumber());
	   							if (NumberUtils.nvl(cmpd.getRegNumber()).length() > 0) {
	     							(new CompoundAndBatchInfoUpdater()).getCompoundInfoFromValue(btch, cmpd.getRegNumber(), false, false);
	     							ChemistryDelegate cDel = new ChemistryDelegate();
	     							if (!cDel.isChemistryEmpty(cmpd.getNativeSketch())) {
	     								if (!cDel.areMoleculesEqual(cmpd.getNativeSketch(), iBatch.getCompound().getNativeSketch())) {
	     									byte[] sk = cDel.convertChemistry(cmpd.getNativeSketch(), null, iBatch.getCompound().getNativeSketchFormat());
	     									StructureLoadAndConversionUtil.loadSketch(sk, iBatch.getCompound().getFormat(), true, iBatch.getCompound());
	     								}
	     								if (!cDel.areMoleculesEqual(cmpd.getNativeSketch(), iBatch.getRegInfo().getBatchVnVInfo().getMolData().getBytes())) {
	     									byte[] mol = cDel.convertChemistry(cmpd.getNativeSketch(), null, "MDL Molfile");
	     									iBatch.getRegInfo().getBatchVnVInfo().setMolData(new String(mol));
	     								}
	     							}
	   							}
     						} catch (Exception e) { }
     						if (!iBatch.getCompound().getStereoisomerCode().equals(result.getStereoisomerCode()))
     							iBatch.getCompound().setStereoisomerCode(result.getStereoisomerCode());
     						if (!iBatch.getCompound().getStructureComments().equals(result.getStructureComment()))
     							iBatch.getCompound().setStructureComments(result.getStructureComment());

     						// Check Salt Code & Equiv
     						try {
	     						if (!iBatch.getSaltForm().getCode().equals(result.getSaltCode())) {
	     							SaltCodeCache scc = SaltCodeCache.getCache();
	     							iBatch.setSaltForm(new SaltFormModel(result.getSaltCode(), 
	     							                                scc.getDescriptionGivenCode(result.getSaltCode()),
	     							                                scc.getMolFormulaGivenCode(result.getSaltCode()),
	     							                                scc.getMolWtGivenCode(result.getSaltCode())));
	     						}
     						} catch (Exception e) { }
         					if (iBatch.getSaltEquivs() != result.getSaltMole()) {
         						iBatch.setSaltEquivs(result.getSaltMole());
    //TODO     						iBatch.setSaltEquivsSet(true);
         					}

         					// Process all Submitted Containers to check for changes or additions
         					ArrayList<BatchSubmissionContainerInfoModel> vials = iBatch.getRegInfo().getSubmitContainerList();
         					for (Iterator<RegistrationVialDetailsVO> nV=result.getVials().iterator(); nV.hasNext(); ) {
         						RegistrationVialDetailsVO nC = nV.next();
         						boolean newFlag = true;
         						for (Iterator<BatchSubmissionContainerInfoModel> v=vials.iterator(); v.hasNext(); ) {
         							BatchSubmissionContainerInfoModel c = v.next();
         							if (c.getBarCode().equals(nC.getLocalBarCode())) {
//         								if (!c.getSiteCode().equals(nC.getSiteCode()))
//         									c.setSiteCode(nC.getSiteCode());
//         								
         								if (c.getAmountValue() != nC.getAmountAvailable())
         									c.setAmountValue(nC.getAmountAvailable());
         								
         								if (!c.getAmountUnit().equals(nC.getAmountUnitCode()))
             								c.setAmountUnit(nC.getAmountUnitCode());
         								
             							newFlag = false;
             							break;
         							}
         						}
         						
         						if (newFlag) {
         							BatchSubmissionContainerInfoModel containerInfo = new BatchSubmissionContainerInfoModel();
         							containerInfo.setSiteCode(nC.getSiteCode());
         							containerInfo.setBarCode(nC.getLocalBarCode());
         							containerInfo.setAmountValue(nC.getAmountAvailable());
         							containerInfo.setAmountUnit(nC.getAmountUnitCode());
         							containerInfo.setSubmissionStatus(BatchRegistrationInfo.SUBMITTED);
         							
         							iBatch.getRegInfo().addSubmitContainer(containerInfo);
         						}
         					}
     						reg_subsumContainerTableModel.fireTableDataChanged();
     						
     						iBatch.setPrecursors(result.getPrecursors());
     						
     						List<List<String>> list = result.getResidualSolvents();
     						if (list.size() > 0) {
     							ArrayList<BatchResidualSolventModel> newList = new ArrayList<BatchResidualSolventModel>();
     							
     							for (Iterator<List<String>> it=list.iterator(); it.hasNext(); ) {
     								List<String> entry = it.next();
     								String code = entry.get(0);
     								
             						boolean newFlag = true;
             						for (Iterator<BatchResidualSolventModel> it2=iBatch.getRegInfo().getResidualSolventList().iterator(); it2.hasNext(); ) {
             							BatchResidualSolventModel s = it2.next();
             							if (s.getCodeAndName().equals(code)) {
             								s.setEqOfSolvent(Double.parseDouble((String)entry.get(1)));
             								newFlag = false;
             								newList.add(s);
             								break;
             							}
             						}
             						
             						if (newFlag) {
             							BatchResidualSolventModel s = new BatchResidualSolventModel();
	     								s.setCodeAndName(code);
	     								s.setEqOfSolvent(Double.parseDouble((String)entry.get(1)));
	     								newList.add(s);
             						}
     							}
     							
     							iBatch.getRegInfo().setResidualSolventList(newList);
     						}
     						
     						list = result.getSolubilitySolvents();
     						if (list.size() > 0) {
     							ArrayList<BatchSolubilitySolventModel> newList = new ArrayList<BatchSolubilitySolventModel>();
     							
     							for (Iterator<List<String>> it=list.iterator(); it.hasNext(); ) {
     								List<String> entry = it.next();
     								String code = (String)entry.get(0);
     								
             						boolean newFlag = true;
             						for (Iterator<BatchSolubilitySolventModel> it2=iBatch.getRegInfo().getSolubilitySolventList().iterator(); it2.hasNext(); ) {
             							BatchSolubilitySolventModel s = it2.next();
             							if (s.getCodeAndName().equals(code)) {
             								if (entry.get(4) != null) {
             									s.setQualitative(true);
             									s.setQualiString((String)entry.get(4));
              								} else {
             									s.setQuantitative(true);
                 								s.setOperator((String)entry.get(1));
                 								s.setSolubilityValue(Double.parseDouble((String)entry.get(2)));
                 								s.setSolubilityUnit((String)entry.get(5));
                 								if (entry.get(3) != null) s.setSolubilityValue(Double.parseDouble(entry.get(3))); else s.setSolubilityValue(0);
             								}
             								newFlag = false;
             								newList.add(s);
             								break;
             							}
             						}
             						
             						if (newFlag) {
             							BatchSolubilitySolventModel s = new BatchSolubilitySolventModel();
	     								s.setCodeAndName(code);
         								if (entry.get(4) != null) {
         									s.setQualitative(true);
         									s.setQualiString((String)entry.get(4));
          								} else {
         									s.setQuantitative(true);
             								s.setOperator((String)entry.get(1));
             								s.setSolubilityValue(Double.parseDouble((String)entry.get(2)));
             								s.setSolubilityUnit((String)entry.get(5));
             								if (entry.get(3) != null) s.setSolubilityValue(Double.parseDouble((String)entry.get(3))); else s.setSolubilityValue(0);
         								}
	     								newList.add(s);
             						}
             						
         							iBatch.getRegInfo().setSolubilitySolventList(newList);
    							}
     						}
     						
         					// Check MW and MF
     						if (iBatch.getMolecularWeightAmount().GetValueInStdUnitsAsDouble() != result.getMolecularWeight()) {
     							iBatch.getMolecularWeightAmount().setCalculated(false);
     							iBatch.getMolecularWeightAmount().setValue(result.getMolecularWeight());
     						}
     						if (!iBatch.getMolecularFormula().equals(result.getMolecularFormula())) {
     							iBatch.setMolFormula(result.getMolecularFormula());
     						}

 							String oldStatus = iBatch.getRegInfo().getStatus();
 							String oldRegStatus = iBatch.getRegInfo().getRegistrationStatus();
 							iBatch.getRegInfo().setStatus("CURATION");
 							iBatch.getRegInfo().setRegistrationStatus("CURATION");
 							boolean bCalcFlg = iBatch.getTheoreticalWeightAmount().isCalculated();
 							iBatch.getTheoreticalWeightAmount().setCalculated(true);
 							iBatch.recalcAmounts();
							iBatch.getTheoreticalWeightAmount().setCalculated(bCalcFlg);
 							iBatch.getRegInfo().setStatus(oldStatus);
 							iBatch.getRegInfo().setRegistrationStatus(oldRegStatus);

     						updateBatchList(iBatch);
//TODO     						getParentDialog().getRegVnV_cont().updateBatchDisplay();
//     						getParentDialog().getCompReg_cont().updateDisplay();
//      						getParentDialog().getHazHandling_cont().updateDisplay();     						
//      						getParentDialog().getSampleSubSum_cont().updateDisply();
     						count++;
     					}
//     				}
     			}
		    	
		    	return new Integer(count);
     		}
    
     		public void finished() {
     			if (displayMessages) {
	     			CeNJobProgressHandler.getInstance().removeItem(progressStatus);
	     			int count = 0;
	     			try { count = ((Integer)get()).intValue(); } catch (Exception e) { }
	     			if (count == 0)
	     				JOptionPane.showMessageDialog(Reg_subsumContainer.this, "No New Registration Details found.");
	     			else
	     				JOptionPane.showMessageDialog(Reg_subsumContainer.this, "" + count + " Record(s) updated.");
     			}
     	    }
     	};
     	worker.start();
	}

	public void updateRegSubStatusByJobId(ProductBatchModel iBatch, String jobId, ArrayList<String> jobErrors) {
		iBatch.getRegInfo().setStatus(BatchRegistrationInfo.FAILED);
		String errorMsg = "Job Id = " + jobId + ", Registration failed.";
		if (jobErrors.size() > 0) {
			errorMsg += "\nException = " + jobErrors.get(0);
			errorMsg += "\nMessage = " + jobErrors.get(1);
		}
		iBatch.getRegInfo().setErrorMsg(errorMsg);
		iBatch.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.NOT_REGISTERED);
		if (iBatch.getRegInfo().getSubmissionStatus().equals(BatchRegistrationInfo.POST_SUBMITTING)) {
			// update submission status and model, calculate total amount of
			// sample, update GUI
			iBatch.getRegInfo().setSubmissionStatus(BatchRegistrationInfo.NOT_SUBMITTED);
		}
		reg_subsumContainerTableModel.fireTableDataChanged();
		this.updateBatchList(iBatch);
	}

	public void getRegistrationStatus() {
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				if (worker1 != null) {
					worker1.get();
				}
				ArrayList<String> jobIdList = new ArrayList<String>();
				int NUM_LOOPS = 15;
				int POLLING_INTERVAL = 30000;
				try {
					RegistrationServiceDelegate reg = new RegistrationServiceDelegate();
					for (int j = 0; j < NUM_LOOPS; j++) {
						// //System.out.println("Inside the status checking
						// thread...");
						jobIdList.clear();
						Iterator<String> jobIdIterator = Reg_subsumContainer.this.jobIdBatchNoMap.keySet().iterator();
						while (jobIdIterator.hasNext()) {
							jobIdList.add(jobIdIterator.next());
						}
						if (jobIdList.size() > 0) {
							// pull registration status info and update the
							// batch
							try {
								ArrayList<BatchRegStatusTrackingVO> resultList = new ArrayList<BatchRegStatusTrackingVO>();
								//ArrayList resultList = reg.getRegistrationStatus(jobIdList);
								updateBatchRegStatus(resultList);
								// testupdateBatchStatus();
								reg_subsumContainerTableModel.fireTableDataChanged();
								keepSelectedRow();
							}/* catch (RegistrationSvcUnavailableException e4) {
								JOptionPane.showMessageDialog(Reg_subsumContainer.this,
										"Registration Service is currently unavailable, please try again later.");
								Thread.currentThread().interrupt();
							} */catch (Exception e1) {
								CeNErrorHandler.getInstance().logExceptionMsg(Reg_subsumContainer.this, e1);
							}
						} else {
							NUM_LOOPS = 5;
							Thread.currentThread().interrupt();
						}
						if (Thread.interrupted()) {
							throw new InterruptedException();
						}
						Thread.sleep(POLLING_INTERVAL);
					}
				} catch (InterruptedException e) {
					return "Interrupted";
					// } finally {
					// //reset registration and submission status to
					// NOT_REGISTERED and NOT_SUBMITTED respectly
					// //if the batch does not get registration and
					// submission status updated to REGISTERED or SUBMITTED
					// //after 5 times polling
					// //System.out.println("Inside the finally block of the
					// status checking thread...");
					// for (int i = 0; i <
					// reg_subsumContainerTableModel.getRowCount(); i++) {
					// ProductBatch iBatch = (ProductBatch)
					// reg_subsumContainerTableModel
					// .getBatchList().get(i);
					// if (iBatch.getRegInfo().getRegistrationStatus() !=
					// null
					// &&
					// !iBatch.getRegInfo().getRegistrationStatus().equals(""))
					// {
					// if
					// (iBatch.getRegInfo().getRegistrationStatus().equals(
					// BatchRegistrationInfo.POST_REGISTERING)) {
					// iBatch.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.NOT_REGISTERED);
					// if
					// (iBatch.getRegInfo().getSubmissionStatus().equals(BatchRegistrationInfo.POST_SUBMITTING))
					// {
					// iBatch.getRegInfo().setSubmissionStatus(BatchRegistrationInfo.NOT_SUBMITTED);
					// }
					// }
					// }
					// }
				} catch (Exception e) { 
					CeNErrorHandler.getInstance().logExceptionMsg(Reg_subsumContainer.this, e);
				}
				return "All Done";
			}

			public void finished() {
				// update model and GUI
			}
		};
		worker.start();
	}

	private void keepSelectedRow() {
		if (jTableRegBatches.getRowCount() > 0 && selectedRow >= 0 && selectedRow < jTableRegBatches.getRowCount()) {
			jTableRegBatches.setRowSelectionInterval(selectedRow, selectedRow);
		}
	}

	private void updateBatchRegStatus(ArrayList<BatchRegStatusTrackingVO> batchInfoVOList) {
		if (batchInfoVOList.size() <= 0) {
			return;
		} else {
			for (int i = 0; i < reg_subsumContainerTableModel.getRowCount(); i++) {
				ProductBatchModel iBatch = reg_subsumContainerTableModel.getBatchList().get(i);
				for (int j = 0; j < batchInfoVOList.size(); j++) {
					BatchRegStatusTrackingVO regStatus = batchInfoVOList.get(j);
					// //System.out.println("The batch number is: " +
					// iBatch.getBatchNumberAsString());
					// //System.out.println("The notebook ref is: " +
					// jobIdBatchNoMap.get(regStatus.getJobID()));
					if (regStatus.getJobID().equals(iBatch.getRegInfo().getJobId())) {
						if (regStatus.getCompoundStatus().equals(CeNConstants.REGINFO_PASSED)) {
							iBatch.setConversationalBatchNumber(regStatus.getBatchNumber());
							iBatch.setParentBatchNumber(regStatus.getCompoundParent());
							iBatch.getCompound().setRegNumber(regStatus.getCompoundNumber());
							// TODO: Fix date conversion to String as a common utility
							// added string as seen elsewhere.  Should be using more consistent method
							iBatch.getRegInfo().setRegistrationDate(new Timestamp(regStatus.getRegDate().getTime()));  
							iBatch.getRegInfo().setJobId(regStatus.getJobID());
							if(StringUtils.isNumeric(regStatus.getBatchTrackingID().trim())) {
								iBatch.getRegInfo().setBatchTrackingId(Long.parseLong(regStatus.getBatchTrackingID().trim()));
							}
							iBatch.getRegInfo().setStatus(regStatus.getCompoundStatus());
							iBatch.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.REGISTERED);
							if (iBatch.getRegInfo().getSubmissionStatus().equals(BatchRegistrationInfo.POST_SUBMITTING)) {
								// update submission status and model, calculate
								// total amount of sample, update GUI
								iBatch.getRegInfo().setSubmissionStatus(BatchRegistrationInfo.SUBMITTED);
							}
							jobIdBatchNoMap.remove(regStatus.getJobID());
							updateBatchList(iBatch);
						} else if (regStatus.getCompoundStatus().equals(BatchRegistrationInfo.FAILED)) {
							iBatch.getRegInfo().setStatus(regStatus.getCompoundStatus());
							iBatch.getRegInfo().setErrorMsg(regStatus.getDetailString());
							iBatch.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.NOT_REGISTERED);
							if (iBatch.getRegInfo().getSubmissionStatus().equals(BatchRegistrationInfo.POST_SUBMITTING)) {
								// update submission status and model, calculate
								// total amount of sample, update GUI
								iBatch.getRegInfo().setSubmissionStatus(BatchRegistrationInfo.NOT_SUBMITTED);
							}
							jobIdBatchNoMap.remove(regStatus.getJobID());
							updateBatchList(iBatch);
							
							// if failure is due to unique constraint, automatically look in CompoundRegistration for results
							if (regStatus.getDetailString().indexOf("unique constraint") > 0 &&
								regStatus.getDetailString().indexOf("COMPOUND_BATCH") > 0) {
								pullRegistrationDetails(false);
							}
						}
						// update container and sceen status
						updateScreenAndContainerStatus(iBatch, regStatus.getCompoundStatus());
					}
				}
			}
		}
	}

	private void updateScreenAndContainerStatus(ProductBatchModel iBatch, String statusString) {
		List<BatchSubmissionToScreenModel> samplesList = iBatch.getRegInfo().getNewSubmitToBiologistTestList();
		ArrayList<BatchSubmissionContainerInfoModel> containerList = iBatch.getRegInfo().getSubmitContainerList();
		for (int j = 0; j < samplesList.size(); j++) {
			BatchSubmissionToScreenModel batchSubmissionToBiologistTest = samplesList.get(j);
			if (!batchSubmissionToBiologistTest.getSubmissionStatus().equals(BatchSubmissionToBiologistTest.SUBMITTED)) {
				if (statusString.equals(BatchRegistrationInfo.PASSED))
					batchSubmissionToBiologistTest.setSubmissionStatus(BatchSubmissionToBiologistTest.SUBMITTED);
				else
					batchSubmissionToBiologistTest.setSubmissionStatus(BatchSubmissionToBiologistTest.NOT_SUBMITTED);
			}
		}
		for (int j = 0; j < containerList.size(); j++) {
			BatchSubmissionContainerInfoModel batchSubmissionContainerInfo = containerList.get(j);
			if (!batchSubmissionContainerInfo.getSubmissionStatus().equals(BatchSubmissionToBiologistTest.SUBMITTED)) {
				if (statusString.equals(BatchRegistrationInfo.PASSED))
					batchSubmissionContainerInfo.setSubmissionStatus(BatchSubmissionContainerInfo.SUBMITTED);
				else
					batchSubmissionContainerInfo.setSubmissionStatus(BatchSubmissionContainerInfo.NOT_SUBMITTED);
			}
		}
	}

	public boolean statusDirty(ArrayList<ProductBatchModel> batchList) {
		boolean isDirty = false;
		for (ProductBatchModel pBatch : batchList) {
			if (pBatch != null && pBatch.getRegInfo().getStatus().equals(BatchRegistrationInfo.PROCESSING)) {
				isDirty = true;
				break;
			}
		}
		return isDirty;
	}

	public void buildJobIdBatchNumberMap() {
		for (int i = 0; i < batchList.size(); i++) {
			ProductBatchModel pBatch = batchList.get(i);
			if (pBatch.getRegInfo().getStatus().equals(BatchRegistrationInfo.PROCESSING)) {
				jobIdBatchNoMap.put(pBatch.getRegInfo().getJobId(), pBatch.getBatchNumberAsString());
			}
		}
	}

//	private String getUserNTDomain(String userDomainStr) {
//		String[] userDomainStringArray = userDomainStr.split(":");
//		return userDomainStringArray[0].toUpperCase();
//	}

	private String buildSDFile(ProductBatchModel pBatch, boolean forCompoundRegistrationReg) {
		return regSubHandler.buildSDFIle(pBatch, forCompoundRegistrationReg);
	}

//	/** Auto-generated main method */
//	public static void main(String[] args) {
//		showGUI();
//	}
//
//	/**
//	 * This static method creates a new instance of this class and shows it inside a new JFrame, (unless it is already a JFrame).
//	 * 
//	 * It is a convenience method for showing the GUI, but it can be copied and used as a basis for your own code. * It is
//	 * auto-generated code - the body of this method will be re-generated after any changes are made to the GUI. However, if you
//	 * delete this method it will not be re-created.
//	 */
//	public static void showGUI() {
//		try {
//			javax.swing.JFrame frame = new javax.swing.JFrame();
//			Reg_subsumContainer inst = new Reg_subsumContainer();
//			frame.setContentPane(inst);
//			frame.getContentPane().setSize(inst.getSize());
//			frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
//			frame.pack();
//			frame.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/** Auto-generated event handler method */
	protected void jTableRegBatchesMouseClicked(MouseEvent evt) {
		JTable regBatchesTable = (JTable) evt.getSource();
		int selectedRow = regBatchesTable.rowAtPoint(evt.getPoint());
		int selectedCol = regBatchesTable.columnAtPoint(evt.getPoint());
		if (evt.getButton() == MouseEvent.BUTTON1) {
			setSelectedRow(selectedRow);
			ProductBatchModel newBatch = reg_subsumContainerTableModel.getBatchList().get(selectedRow);
			if (newBatch != selectedBatch) {
				setSelectedBatch(newBatch);
				// //System.out.println("The selected row is: " +
				// getSelectedRow());
				/*TODO Bo Yang 3/14/07
				getParentDialog().getRegVnV_cont().setSelectedBatch(selectedBatch);
				getParentDialog().getRegVnV_cont().updateBatchDisplay();
				// then update compound registration page
				getParentDialog().getCompReg_cont().setSelectedBatch(selectedBatch);
				getParentDialog().getCompReg_cont().updateDisplay();
				// then update hazhandling page
				getParentDialog().getHazHandling_cont().setSelectedBatch(selectedBatch);
				getParentDialog().getHazHandling_cont().updateDisplay();
				// then update submission page
				getParentDialog().getSampleSubSum_cont().setSelectedBatch(selectedBatch);
				getParentDialog().getSampleSubSum_cont().updateDisply();
				*/
			}
		}
		// add validation here
		if (selectedBatch != null && isEditable && selectedCol == Reg_subsumContainerTableModel.SELECT_TO_REGISTER_IDX) {
			Object o_Boolean = jTableRegBatches.getValueAt(selectedRow, Reg_subsumContainerTableModel.SELECT_TO_REGISTER_IDX);
			if (o_Boolean instanceof Boolean) {
				Boolean isSelected = (Boolean) o_Boolean;
				if (!isSelected.booleanValue()) {
					jTableRegBatches.setValueAt(new Boolean(false), selectedRow,
							Reg_subsumContainerTableModel.SELECT_TO_REGISTER_IDX);
					// jTableRegBatches.setValueAt(new Boolean(false),
					// selectedRow,
					// Reg_subsumContainerTableModel.SELECT_TO_SUBMIT_IDX);
				} else {
					// validate required fields for sdfile associated with
					// the selected batch
					boolean isValid = validateBatchRegInfo(selectedBatch);
					if (!isValid) {
						reg_subsumContainerTableModel.resetRegStatus(selectedRow);
						reg_subsumContainerTableModel.fireTableDataChanged();
					} else {
						jTableRegBatches.setValueAt(new Boolean(true), selectedRow,
								Reg_subsumContainerTableModel.SELECT_TO_REGISTER_IDX);
						// jTableRegBatches.setValueAt(new Boolean(true),
						// selectedRow,
						// Reg_subsumContainerTableModel.SELECT_TO_SUBMIT_IDX);
					}
				}
			}
		}
		// if (selectedBatch != null && isEditable && selectedCol ==
		// Reg_subsumContainerTableModel.SELECT_TO_SUBMIT_IDX) {
		// //validate required fields for sdfile associated with the selected
		// batch
		// boolean isValid = validateBatchSubInfo(selectedBatch);
		// if (!isValid ) {
		// reg_subsumContainerTableModel.resetSubStatus(selectedRow);
		// reg_subsumContainerTableModel.fireTableDataChanged();
		// }
		// }
		if (selectedBatch != null && selectedCol == Reg_subsumContainerTableModel.STATUS_IDX) {
			// display the error message if the status is "FAILED"
			if (selectedBatch.getRegInfo().getStatus().equals(BatchRegistrationInfo.FAILED)) {
				JOptionPane.showMessageDialog((JInternalFrame)parentDialog, selectedBatch.getRegInfo().getErrorMsg());
			}
		}
	}

	private boolean validateBatchRegInfo(ProductBatchModel selectedBatch2) {
		boolean isValid = true;
		/*TODO Bo Yang 3/14/07
		NotebookPage page = (NotebookPage) getParentDialog().getNotebookPage();
		*/
		
		NotebookPage page = null;
		
		// FIXME  page is null from now on
		
		StringBuffer errorMessage = new StringBuffer();
		// Structure Molfile
		if (selectedBatch2.getCompound().getNativeSketch() == null || selectedBatch2.getCompound().getNativeSketch().length <= 0) {
			errorMessage.append("Compound structure is required. \n");
			isValid = false;
		}
		
		//COMPOUND_REGISTRATION_LITERATURE_REFERENCE_COMMENT
		String sMaxLitRefSize = "100";
		try { sMaxLitRefSize = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_COMPOUND_REGISTRATION_MAX_LIT_REF); } catch (Exception e) { }
		int iMaxLitRefSize = Integer.parseInt(sMaxLitRefSize);
		if (getParentDialog().getPageModel().getLiteratureRef() != null && getParentDialog().getPageModel().getLiteratureRef().trim().length() > iMaxLitRefSize) {			
			errorMessage.append("Literature Reference must not be greater than " + sMaxLitRefSize + " characters.\n");
			isValid = false;			
		}
		
		// COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_VALUE
		if (selectedBatch2.getWeightAmount().doubleValue() <= 0.0) {
			errorMessage.append("Total Amount must be greater than zero. \n");
			isValid = false;
		}
		// COMPOUND_REGISTRATION_NOTEBOOK_REFERENCE
		if (selectedBatch2.getBatchNumberAsString() == null || selectedBatch2.getBatchNumberAsString().trim().length() <= 0) {
			errorMessage.append("Notebook Reference is required. \n");
			isValid = false;
		}
		// COMPOUND_REGISTRATION_TA_CODE
		if (page.getTaCode() == null || page.getTaCode().trim().length() <= 0) {
			errorMessage.append("Therapeutic Area Code is required. \n");
			isValid = false;
		}
		// COMPOUND_REGISTRATION_PROJECT_CODE
		if (page.getProjectCode() == null || page.getProjectCode().trim().length() <= 0) {
			errorMessage.append("Project Code is required. \n");
			isValid = false;
		}
		// //COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_VALUE
		// if (batch.getWeightAmount().getValue() <= 0) {
		// errorMessage.append("Total Amount must be greater than zero. \n");
		// isValid = false;
		// }
		if (selectedBatch2.getCompound().getStereoisomerCode() != null && selectedBatch2.getCompound().getStereoisomerCode().trim().length() > 0) {
			// //System.out.println("the SIC is: " +
			// batch.getCompound().getStereoisomerCode());
			// COMPOUND_REGISTRATION_STRUCTURE_COMMENT is mandatory if
			// COMPOUND_REGISTRATION_GLOBAL_STEREOISOMER_CODE in
			// (('SNENU','LRCMX','ENENU','DSTRU','UNKWN') and
			// ((COMPOUND_REGISTRATION_GPN_TO_ASSIGN_NEW_BATCH is not defined) or
			// (COMPOUND_REGISTRATION_GPN_TO_ASSIGN_NEW_BATCH is 'NEW')))
			if (selectedBatch2.getCompound().getStereoisomerCode().equals("SNENU")
					|| selectedBatch2.getCompound().getStereoisomerCode().equals("LRCMX") /* HARDCODE */
					|| selectedBatch2.getCompound().getStereoisomerCode().equals("ENENU")
					|| selectedBatch2.getCompound().getStereoisomerCode().equals("DSTRU")
					|| selectedBatch2.getCompound().getStereoisomerCode().equals("UNKWN")) {
				if (!(selectedBatch2.getCompound().getStructureComments() != null && selectedBatch2.getCompound().getStructureComments().trim()
						.length() > 0)) {
					errorMessage.append("Structure comment is required. \n");
					isValid = false;
				}
			}
		} else {
			errorMessage.append("Stereoisomer Code is required. \n");
			isValid = false;
		}
		
		//COMPOUND_REGISTRATION_COMPOUND_SOURCE_CODE and COMPOUND_REGISTRATION_COMPOUND_SOURCE_DETAIL_CODE
		if (!(selectedBatch2.getRegInfo().getCompoundSource() != null && selectedBatch2.getRegInfo().getCompoundSource().trim().length() > 0 )) {
			errorMessage.append("Compound Source is required. \n");
			isValid = false;
		}
		if (!(selectedBatch2.getRegInfo().getCompoundSourceDetail() != null && selectedBatch2.getRegInfo().getCompoundSourceDetail().trim().length() > 0 )) {
			errorMessage.append("Compound Source Detail is required. \n");
			isValid = false;
		}
		
		//COMPOUND_REGISTRATION_COMPOUND_STATE
		if (!(selectedBatch2.getCompoundState() != null && selectedBatch2.getCompoundState().trim().length() > 0 )) {
			errorMessage.append("Compound State is required. \n");
			isValid = false;
		}
		
		//COMPOUND_REGISTRATION_COMPOUND_IS_INTERMEDIATE
		if (selectedBatch2.getRegInfo().getIntermediateOrTest() == null || selectedBatch2.getRegInfo().getIntermediateOrTest().length() == 0 ||
			!(selectedBatch2.getRegInfo().getIntermediateOrTest().equals("Y") || selectedBatch2.getRegInfo().getIntermediateOrTest().equals("N"))) {
			errorMessage.append("Test or Intermediate indication is required. \n");
			isValid = false;
		}

		// If COMPOUND_REGISTRATION_COMPOUND_SOURCE_CODE='EXTERNL' then COMPOUND_REGISTRATION_SUPPLIER_CODE and
		// COMPOUND_REGISTRATION_SUPPLIER_REGISTRY_NUMBER are required
		if (selectedBatch2.getRegInfo().getCompoundSource() != null && selectedBatch2.getRegInfo().getCompoundSource().trim().length() > 0) {
			if (selectedBatch2.getRegInfo().getCompoundSource().equals("EXTERNL")) { /* HARDCODE */
				if (!(selectedBatch2.getVendorInfo().getCode() != null && selectedBatch2.getVendorInfo().getCode().trim().length() > 0)) {
					errorMessage.append("Supplier Code is required. \n");
					isValid = false;
				}
				if (!(selectedBatch2.getVendorInfo().getSupplierCatalogRef() != null && selectedBatch2.getVendorInfo().getSupplierCatalogRef().trim()
						.length() > 0)) {
					errorMessage.append("Supplier Registry Number is required. \n");
					isValid = false;
				}
			}
		}
		// If COMPOUND_REGISTRATION_GLOBAL_SALT_CODE not like '00', then COMPOUND_REGISTRATION_GLOBAL_SALT_MOLE is required
		if (selectedBatch2.getSaltForm().getCode() != null) {
			if (!selectedBatch2.getSaltForm().getCode().equals("00")) {
				if (selectedBatch2.getSaltEquivs() <= 0) { //TODO || !batch.getSaltEquivsSet()) {
					errorMessage.append("Salt Equivalents is required and must be greater than 0. \n");
					isValid = false;
				}
			}
		}
		// cannot be reached
		// if
		// (selectedBatch.getRegInfo().getSubmissionStatus().equals("Submitting")
		// ) {
		// if (selectedBatch.getRegInfo().getSubmitContainerList()!= null &&
		// selectedBatch.getRegInfo().getSubmitContainerList().size()>0 ) {
		// if (selectedBatch.getProtectionCode() != null &&
		// selectedBatch.getProtectionCode().trim().length() <= 0 )
		// errorMessage.append("Protection Code is required. \n");
		// isValid = false;
		// }
		// }
		// //System.out.println("The isValid is; " + isValid);
		if (!isValid)
			JOptionPane.showMessageDialog((JInternalFrame)parentDialog, errorMessage.toString());
		return isValid;
	}

	public Amount calculateSubmittedScreenTotal(ProductBatchModel batch) {
		double total = 0.0;
		UnitCache uc = UnitCache.getInstance();
		List<BatchSubmissionToScreenModel> list = batch.getRegInfo().getNewSubmitToBiologistTestList();
		for (int i = 0; i < list.size(); i++) {
			BatchSubmissionToScreenModel batchSubmissionToBiologistTest = list.get(i);
			if (batchSubmissionToBiologistTest.isTestSubmittedByMm()
					&& !batchSubmissionToBiologistTest.getSubmissionStatus().equals(BatchSubmissionToBiologistTest.SUBMITTED)) {
				Amount amount = new Amount(batchSubmissionToBiologistTest.getAmountValue(), uc
						.getUnit(batchSubmissionToBiologistTest.getAmountUnit()));
				total += amount.GetValueInStdUnitsAsDouble();
			}
		}
		return new Amount(total, uc.getUnit("MG"));
	}

	public Amount calculateSubmittedScreenDirectlyTotal(ProductBatchModel row) {
		double total = 0.0;
		UnitCache uc = UnitCache.getInstance();
		List<BatchSubmissionToScreenModel> list = row.getRegInfo().getNewSubmitToBiologistTestList();
		for (int i = 0; i < list.size(); i++) {
			BatchSubmissionToScreenModel batchSubmissionToBiologistTest = list.get(i);
			if (!batchSubmissionToBiologistTest.isTestSubmittedByMm()
					&& !batchSubmissionToBiologistTest.getSubmissionStatus().equals(BatchSubmissionToBiologistTest.SUBMITTED)) {
				Amount amount = new Amount(batchSubmissionToBiologistTest.getAmountValue(), uc
						.getUnit(batchSubmissionToBiologistTest.getAmountUnit()));
				total += amount.GetValueInStdUnitsAsDouble();
			}
		}
		return new Amount(total, uc.getUnit("MG"));
	}

	public Amount calculateSubmittedContainerTotal(ProductBatchModel row) {
		double total = 0.0;
		UnitCache uc = UnitCache.getInstance();
		ArrayList<BatchSubmissionContainerInfoModel> containerList = row.getRegInfo().getSubmitContainerList();
		for (int j = 0; j < containerList.size(); j++) {
			BatchSubmissionContainerInfoModel containerInfo = containerList.get(j);
			if (!containerInfo.getSubmissionStatus().equals(BatchSubmissionContainerInfoModel.SUBMITTED)) {
				Amount amount = new Amount(containerInfo.getAmountValue(), uc.getUnit(containerInfo.getAmountUnit()));
				total += amount.GetValueInStdUnitsAsDouble();
			}
		}
		return new Amount(total, uc.getUnit("MG"));
	}

	private boolean validateBatchSubInfo(ProductBatchModel batch) {
		boolean isValid = true;
		StringBuffer errorMessage = new StringBuffer();
		if (selectedBatch.getRegInfo().getSubmissionStatus().equals("Submitting")) {
			if (selectedBatch.getRegInfo().getSubmitContainerList() != null
					&& selectedBatch.getRegInfo().getSubmitContainerListSize() > 0) {
				if (selectedBatch.getProtectionCode() != null && selectedBatch.getProtectionCode().trim().length() <= 0) {
					errorMessage.append("Protection Code is required. \n");
					isValid = false;
				}
			}
		}
		// Submitted screen total should not be greater than that of submitted
		// container
		if (calculateSubmittedScreenTotal(batch).doubleValue() > calculateSubmittedContainerTotal(batch).doubleValue()) {
			errorMessage.append("Submitted screen total should not be greater than that of submitted container(s). \n");
			isValid = false;
			/*TODO Bo Yang 3/14/07
			getParentDialog().getSampleSubSum_cont().resetScreenStatus();
			*/
		}
		// COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_VALUE must be greater than or equal to
		// COMPOUND_REGISTRATION_SENT_TO_BIOLOGISTS_AMOUNT_VALUE + the total of
		// COMPOUND_REGISTRATION_CONTAINER_AMOUNT
		double val = calculateSubmittedScreenDirectlyTotal(batch).doubleValue() + calculateSubmittedContainerTotal(batch).doubleValue();
		val = Math.round(val * 1000.0) / 1000.0;
		if (val > batch.getWeightAmount().GetValueInStdUnitsAsDouble()) {  // Values are compared in MGs
			errorMessage.append("Total amount made value must be greater than or equal to directly submitted screen(s) total + submitted container(s) total. \n");
			isValid = false;
			/*TODO Bo Yang 3/14/07
			getParentDialog().getSampleSubSum_cont().resetScreenStatus();
			*/
		}
		if (!isValid)
			JOptionPane.showMessageDialog((JInternalFrame)parentDialog, errorMessage.toString());
		return isValid;
	}

	public void updateRegInfo(VnVResultVO vnVResultVO) {
		selectedBatch.getCompound().setMolFormula(vnVResultVO.getMolFormula());
		selectedBatch.getCompound().setMolWgt(vnVResultVO.getMolWeight());
		selectedBatch.getCompound().setStereoisomerCode(vnVResultVO.getValidSIC());
		updateBatchList(selectedBatch);
	}

	public void updateBatchList(ProductBatchModel sBatch) {
		for (int i = 0; i < reg_subsumContainerTableModel.getBatchList().size(); i++) {
			if (reg_subsumContainerTableModel.getBatchList().get(i).getBatchNumberAsString().equals(
					sBatch.getBatchNumberAsString())) {
				reg_subsumContainerTableModel.getBatchList().set(i, sBatch);
				break;
			}
		}
	}

	/**
	 * @return Returns the selectedRow.
	 */
	public int getSelectedRow() {
		return selectedRow;
	}

	/**
	 * @param selectedRow
	 *            The selectedRow to set.
	 */
	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;

        if (MasterController.getUser().getNTUserID().startsWith("E_"))
    		jButtonRegSub.setEnabled(false);
        else
        	jButtonRegSub.setEnabled(selectedBatch.isEditable() && isEditable && jTableRegBatches.getRowCount() > 0);
		jButtonRegPull.setEnabled(selectedBatch.isEditable() && isEditable && jTableRegBatches.getRowCount() > 0);
		jButtonRegSDF.setEnabled(jTableRegBatches.getRowCount() > 0);
	}

	/**
	 * @return Returns the parentDialog.
	 */
	public NotebookPageGuiInterface getParentDialog() {
		return parentDialog;
	}

	/**
	 * @param parentDialog
	 *            The parentDialog to set.
	 */
	public void setParentDialog(NotebookPageGuiInterface parentDialog) {
		this.parentDialog = parentDialog;
	}


	/**
	 * @return Returns the reg_subsumContainerTableModel.
	 */
	public Reg_subsumContainerTableModel getReg_subsumContainerTableModel() {
		return reg_subsumContainerTableModel;
	}

	/**
	 * @param reg_subsumContainerTableModel
	 *            The reg_subsumContainerTableModel to set.
	 */
	public void setReg_subsumContainerTableModel(Reg_subsumContainerTableModel reg_subsumContainerTableModel) {
		this.reg_subsumContainerTableModel = reg_subsumContainerTableModel;
	}

	/**
	 * @param nbPage
	 *            The nbPage to set.
	 */
	public void setPageModel(NotebookPageModel nbPg) {
		nbPage = nbPg;
		isEditable = (nbPage != null) ? nbPage.isEditable() : false;
		if (selectedBatch == null) {
			selectedBatch = new ProductBatchModel();
			selectedBatch.setBatchType(BatchType.ACTUAL_PRODUCT);
			selectedBatch.getRegInfo().setStatus(BatchRegistrationInfo.PROCESSING);
			selectedBatch.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.PROCESSING);
		}
		regSubHandler.setPageModel(nbPg);
		postInitGUI();
	}

	/**
	 * @return Returns the selectedBatch.
	 */
	public ProductBatchModel getSelectedBatch() {
		return selectedBatch;
	}

	/**
	 * @param selectedBatch
	 *            The selectedBatch to set.
	 */
	public void setSelectedBatch(ProductBatchModel newBatch) {
		if (selectedBatch != null && !selectedBatch.equals(newBatch)) {
//			if (selectedBatch != null)
//				selectedBatch.deleteRegistrationListener(this);
			selectedBatch = newBatch;
//				selectedBatch.addRegistrationListener(this);
			// add Hazard flags if chloracnegen
			if (selectedBatch.isChloracnegen() && selectedBatch.getChloracnegenType().indexOf("Class 1") >= 0) {
				List<String> listOfHzdCodes = selectedBatch.getRegInfo().getCompoundRegistrationHazardCodes();
				// to avoid duplicate addition to list each time the tab
				// is selected
				if (!listOfHzdCodes.contains("25")) {
					selectedBatch.getRegInfo().getCompoundRegistrationHazardCodes().add("25");
				}
			} else if (selectedBatch.isChloracnegen() && selectedBatch.getChloracnegenType().indexOf("Class 2") >= 0) {
				List<String> listOfHzdCodes = selectedBatch.getRegInfo().getCompoundRegistrationHazardCodes();
				if (!listOfHzdCodes.contains("24")) {
					selectedBatch.getRegInfo().getCompoundRegistrationHazardCodes().add("24");
				}
			}
		}
	}

	/**
	 * @return Returns the nbPage.
	 */
	public NotebookPageModel getNbPage() {
		return nbPage;
	}

	//
	// Implements BatchRegistrationListener
	//
	public void batchRegistrationChanged(ChangeEvent e) {
		int i = jTableRegBatches.getSelectedRow();
		reg_subsumContainerTableModel.fireTableDataChanged();
		if (jTableRegBatches.getRowCount() > 0 && i >= 0 && i < jTableRegBatches.getRowCount()) {
			jTableRegBatches.setRowSelectionInterval(i, i);
		} else if (jTableRegBatches.getRowCount() > 0 && i == -1) {
			// Select first row
			jTableRegBatches.setRowSelectionInterval(0, 0);
		}
		updateDependents();
	}
}