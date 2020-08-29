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
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.client.gui.page.SingletonNotebookPageGUI;
import com.chemistry.enotebook.client.gui.page.regis_submis.table.SingletonRegistration_SummaryViewTableModel;
import com.chemistry.enotebook.client.gui.page.stoichiometry.search.CompoundAndBatchInfoUpdater;
import com.chemistry.enotebook.compoundregistration.classes.BatchRegStatusTrackingVO;
import com.chemistry.enotebook.compoundregistration.classes.RegistrationDetailsVO;
import com.chemistry.enotebook.compoundregistration.classes.RegistrationVialDetailsVO;
import com.chemistry.enotebook.delegate.RegistrationManagerDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.common.codetables.SaltCodeCache;
import com.chemistry.enotebook.experiment.common.units.UnitCache;
import com.chemistry.enotebook.experiment.datamodel.batch.*;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.registration.delegate.RegistrationServiceDelegate;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.utils.NumberUtils;
import com.chemistry.enotebook.utils.SimpleJTable;
import com.chemistry.enotebook.utils.StructureLoadAndConversionUtil;
import com.chemistry.enotebook.utils.SwingWorker;
import info.clearthought.layout.TableLayout;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import java.util.*;
import java.util.List;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
	public class SingletonRegistration_SummaryViewContainer extends JPanel implements BatchRegistrationListener {
		
		/**
	 * 
	 */
	private static final long serialVersionUID = -8026105018418240362L;

		private static final Log log = LogFactory.getLog(SingletonRegistration_SummaryViewContainer.class);
		
		private JScrollPane jScrollPane;
		private SimpleJTable jTableRegBatches;
		private int selectedRow = -1;
		private SingletonRegSubHandler regSubHandler = new SingletonRegSubHandler();
		private SingletonRegistration_SummaryViewTableModel reg_subsumContainerTableModel = new SingletonRegistration_SummaryViewTableModel();
		//private ArrayList regInfoVOList = new ArrayList();
		private ArrayList<ProductBatchModel> batchList = new ArrayList<ProductBatchModel>();
		private HashMap jobIdBatchNoMap = new HashMap();
		private boolean isReEntrent = false;
		private boolean isPageEditable = false;
		private NotebookPageGuiInterface parentDialog;
		private NotebookPageModel nbPage = null;
		private ProductBatchModel selectedBatch = null;
		private JToolBar jToolBarOptions;
		private JButton jButtonRegSub;
		private JButton jButtonRegSDF;
		private JButton jButtonRegPull;
		private SwingWorker worker1 = null;
		private boolean isLoading = true;

		public SingletonRegistration_SummaryViewContainer() {
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
/*			if (regInfoVOList != null)
				regInfoVOList.clear();
			regInfoVOList = null;
*/			if (batchList != null)
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
					private static final long serialVersionUID = 4045597911765874552L;

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
						pullRegistrationDetails();
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
			isLoading = false;
		}

		/**
		 * Currently called only when Registration/Submission TabbedPane is clicked Refreshes the TableView and latest values of the
		 * Product batches are copied.
		 * 
		 */
		public void refresh() {
			if (getNbPage() != null) {
				batchList = new ArrayList<ProductBatchModel>();
				batchList.addAll(regSubHandler.buildBatchList());
				int i = jTableRegBatches.getSelectedRow();
				// table settings
				reg_subsumContainerTableModel.setBatchList(batchList);
				reg_subsumContainerTableModel.setEditable(isPageEditable);
				reg_subsumContainerTableModel.fireTableDataChanged();
				jTableRegBatches.setModel(reg_subsumContainerTableModel);
				jTableRegBatches.setRowHeight(20);
				setHeaderSize();
				// bug fix start
				// Artifact ID: 22431 by pavan
				if (jTableRegBatches.getRowCount() > 0 && i >= 0 && i < jTableRegBatches.getRowCount()) {
					jTableRegBatches.setRowSelectionInterval(i, i);
					setSelectedBatch((ProductBatchModel) (reg_subsumContainerTableModel.getBatchList().get(i)));
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
				
				jButtonRegSub.setEnabled(selectedBatch.isUserAdded() && selectedBatch.isEditable() && isPageEditable);
				jButtonRegPull.setEnabled(selectedBatch.isUserAdded() && selectedBatch.isEditable() && isPageEditable);
				jButtonRegSDF.setEnabled(jTableRegBatches.getRowCount() > 0);
				if (i == -1 && jTableRegBatches.getRowCount() > 0) {
					jTableRegBatches.setRowSelectionInterval(0, 0);
					setSelectedBatch((ProductBatchModel) (reg_subsumContainerTableModel.getBatchList().get(0)));
					// if (selectedBatch!=null) {
					// updateDependents(selectedBatch);
					// }
				} else if (batchList.size() == 0 && selectedBatch != null && selectedBatch.getBatchNumberAsString().length() > 0) {
					selectedBatch = new ProductBatchModel();
					selectedBatch.setBatchType(BatchType.getBatchType("ACTUAL"));
					selectedBatch.getRegInfo().setCompoundRegistrationStatus(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
				}
				//if (!nbPage.isLoading())
				if (!isLoading)
					updateDependents();
				setUpCellRenderer();
				//Jags_todo...
				//jTableRegBatches.setDefaultRenderer(String.class, new WhiteCellRenderer());
				// bug fix end
			}
		}

		/**
		 * updates the dependent containers with the selected batch
		 * 
		 * @param selectedBatch
		 */
		private void updateDependents() {
			//TODO Bo Yang 3/14/07
			//Jags_todo...
			StructureVnVContainer structureVnVContainer = ((SingletonNotebookPageGUI)getParentDialog()).getRegVnV_cont();
			if (structureVnVContainer == null)
				return;
			structureVnVContainer.setSelectedBatch(selectedBatch);
			structureVnVContainer.updateBatchDisplay();
			CompoundRegInfoContainer compoundRegInfoContainer = ((SingletonNotebookPageGUI)getParentDialog()).getCompReg_cont();
			compoundRegInfoContainer.setSelectedBatch(selectedBatch);
			compoundRegInfoContainer.updateDisplay();
			HazHandlingStorageContainer hazHandlingStorageContainer = ((SingletonNotebookPageGUI)getParentDialog()).getHazHandling_cont();
			hazHandlingStorageContainer.setSelectedBatch(selectedBatch);
			hazHandlingStorageContainer.updateDisplay();
			SampleSubSumContainer sampleSubSumContainer = ((SingletonNotebookPageGUI)getParentDialog()).getSampleSubSum_cont();
			sampleSubSumContainer.setSelectedBatch(selectedBatch);
			sampleSubSumContainer.updateDisply();
		}

		/**
		 * 
		 */
		private void setUpCellRenderer() {
			DefaultTableCellRenderer strRendr = (DefaultTableCellRenderer) jTableRegBatches.getDefaultRenderer(String.class);
			strRendr.setHorizontalAlignment(SwingConstants.CENTER);
			TableColumnModel vColumModel = jTableRegBatches.getColumnModel();
			TableColumn column0 = vColumModel.getColumn(SingletonRegistration_SummaryViewTableModel.BATCH_ID_IDX);
			// column4.setCellRenderer(new GrayOutCellRender());
			column0.setPreferredWidth(140);
			TableColumn column4 = vColumModel.getColumn(SingletonRegistration_SummaryViewTableModel.SELECT_TO_REGISTER_IDX);
			// column4.setCellRenderer(new GrayOutCellRender());
			column4.setPreferredWidth(120);
			// TableColumn column6 =
			// vColumModel.getColumn(Reg_subsumContainerTableModel.SELECT_TO_SUBMIT_IDX);
			// //column6.setCellRenderer(new GrayOutCellRender());
			// column6.setPreferredWidth(110);
			TableColumn column7 = vColumModel.getColumn(SingletonRegistration_SummaryViewTableModel.REGISTERY_NUMBER_IDX);
			// column7.setCellRenderer(new GrayOutCellRender());
			column7.setPreferredWidth(140);
			TableColumn column8 = vColumModel.getColumn(SingletonRegistration_SummaryViewTableModel.STATUS_IDX);
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
						&& vColIndex == SingletonRegistration_SummaryViewTableModel.BATCH_ID_IDX
						|| vColIndex == SingletonRegistration_SummaryViewTableModel.REGISTERY_NUMBER_IDX) {
					ProductBatchModel ab = (ProductBatchModel) (((SingletonRegistration_SummaryViewTableModel) jTableRegBatches.getModel()).getBatchList()
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
			private static final long serialVersionUID = 5308304414049464458L;
			private ProductBatchModel selectedBatch;

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
					int column) {
				Component result = super.getTableCellRendererComponent(table, value, false, hasFocus, row, column);
				if (column == SingletonRegistration_SummaryViewTableModel.AMOUNT_SUBMITTED_IDX
						|| column == SingletonRegistration_SummaryViewTableModel.REGISTERY_NUMBER_IDX)
					result.setBackground(Color.white);
				// if (value.equals("Registering") ) {
				//	        	
				// }
				setSelectedBatch((ProductBatchModel) ((SingletonRegistration_SummaryViewTableModel) table.getModel()).getBatchList().get(row));
				if (column == SingletonRegistration_SummaryViewTableModel.SELECT_TO_REGISTER_IDX) {
					if (selectedBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_PASSED)) {
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

		private void doRegistration() {
			// registration process needs to check the number of selected batches
			// if that is less than 20, then perform interactive registration,
			// otherwise
			// batch registration since singleton process is faster.
			interactiveRegistration();
			getRegistrationStatus();
		}

		public void interactiveRegistration() {
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				//try {
/*					if (MasterController.getGUIComponent().isSavePageButtonEnabled()) {
						int saveConfirmation = JOptionPane
								.showConfirmDialog(
										MasterController.getGUIComponent(),
										"All changes must be saved before Submission. Do you want to save the changes and continue?",
										"Save Confirmation", JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE);
						if (saveConfirmation == JOptionPane.YES_OPTION)
*/							
					MasterController.getGUIComponent().jButtonSavePageActionPerformed(null);
/*						else
							return null;
*/					//}					
					ProductBatchModel[] selectedBatches = reg_subsumContainerTableModel.getSelectedBatches();
					if (selectedBatches.length == 0) {
						JOptionPane.showMessageDialog((JInternalFrame)parentDialog,
							"Please first select the desired batches that should be registered by clicking the checkbox(es).");
						return null;
					}
					NotebookUser user = MasterController.getUser();
					String employeeID = user.getCompoundManagementEmployeeId();
					if (StringUtils.isNotBlank(employeeID)) {
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
								"Only Employees with Employee ID can register!");
						return null;
					}

					Map<String, String> nonPlatedBatchesMap = new HashMap<String, String>();
					String serverErrorMessage = "";					
					try {
						setCursor(Cursor.WAIT_CURSOR);
						RegistrationManagerDelegate regManager = ServiceController.getRegistrationManagerDelegate(user.getSessionIdentifier(), employeeID);					
						nonPlatedBatchesMap = regManager.submitBatchesForRegistration(selectedBatches, employeeID, nbPage);
						setBatchStatus(selectedBatches, "REGISTRATION",
								CeNConstants.REGINFO_SUBMITTED,
								CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
					} catch (Exception e) {
						serverErrorMessage += "Submit to Registration of Batches failed due to : \n";
						serverErrorMessage += e.getMessage() + "\n";
						log.error(serverErrorMessage, e);
					}

					setCursor(Cursor.DEFAULT_CURSOR);
					if (StringUtils.isNotBlank(serverErrorMessage) || nonPlatedBatchesMap == null)
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
								"Submit to Registration Failed.\n " + serverErrorMessage);
					else
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
								"Submit to Registration completed");
					// USER2: Don't attempt to save again after submmiting for registration.
					// This will cause the job data to be overwrittern.
					// SAve will done before submitting for registration.
					//if (nbPage != null) {
					//	SwingUtilities.invokeLater(new Runnable() {
					//		public void run() {
					//			MasterController.getGuiController().saveExperiment(nbPage);
					//		}
					//	});
					//}
/*				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
				}*/
				return "";
			}

			public void finished() {
				// SingletonRegistration_SummaryViewContainer.this.refresh();
			}
		};
		worker1 = worker;
		worker.start();
	}
	
		protected void setBatchStatus(ProductBatchModel[] batches, String purpose,
				String submissionStatus, String status) {
			for (int i = 0; i < batches.length; i++) {
				if (purpose.equals("REGISTRATION")) {
					batches[i].getRegInfo().setSubmissionStatus(submissionStatus);
					batches[i].getRegInfo().setStatus(status);
					batches[i].setSelected(false);
				} 
			}
			if (jTableRegBatches != null) {
				SingletonRegistration_SummaryViewTableModel model = (SingletonRegistration_SummaryViewTableModel) jTableRegBatches
						.getModel();
				model.fireTableDataChanged();
				//updateDependents();
				refresh();
			}
		}

		protected void setCursor(int cursor) {
			if (jScrollPane.getCursor().getType() != cursor)
				jScrollPane.setCursor(Cursor.getPredefinedCursor(cursor));
		}
		
		private void createRegistrationSDFile() {
			final com.chemistry.enotebook.utils.SwingWorker worker = new CreateSdFileWorker() {
				public Object construct() {
					StringBuffer sdFile = new StringBuffer();
					try {
						int[] selectedRows = jTableRegBatches.getSelectedRows();
						if (selectedRows.length <= 0) {
							JOptionPane
									.showMessageDialog((JInternalFrame)parentDialog,
											"Please select the desired batches that should be in the SD File\nUse the ctrl/shift key to select indivdual/range of batches.");
						} else {
							for (int i = 0; i < selectedRows.length; i++) {
								ProductBatchModel iBatch = (ProductBatchModel) reg_subsumContainerTableModel.getBatchList().get(selectedRows[i]);
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

		private void pullRegistrationDetails() {
			int count = 0;
			for (int i = 0; i < reg_subsumContainerTableModel.getRowCount(); i++) {
				ProductBatchModel iBatch = (ProductBatchModel) reg_subsumContainerTableModel.getBatchList().get(i);
				if (iBatch.getRegInfo().getCompoundRegistrationStatus() == null
						|| !iBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_PASSED)) {
					RegistrationDetailsVO result = null;
 					try {
 						result = regSubHandler.getRegistrationInformation(iBatch.getBatchNumberAsString());
 					} catch (Exception e) {
 						result = null;
 						CeNErrorHandler.getInstance().logExceptionMsg(null, e);
 					}
					if (result != null) {
//						iBatch.setParentBatchNumber((String) result.get(0));
//						iBatch.getCompound().setRegNumber((String) result.get(1));
//						iBatch.setConversationalBatchNumber((String) result.get(2));
//						iBatch.getRegInfo().setBatchTrackingId(Long.valueOf((String)result.get(3)).longValue());
//						try {
//							iBatch.getRegInfo().setRegistrationDate(NotebookPageUtil.getLocalDate((String) result.get(4)) + "");
//						} catch (Exception e) {  //ignored
//							e.printStackTrace();
//						}
//						iBatch.getRegInfo().setCompoundRegistrationStatus(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_PASSED);
//						iBatch.getRegInfo().setErrorMsg("");
//						//iBatch.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.REGISTERED);
//						reg_subsumContainerTableModel.fireTableDataChanged();
//						updateBatchList(iBatch);
//						count++;

						iBatch.setParentBatchNumber(result.getGlobalNumber());
 						iBatch.getCompound().setRegNumber(result.getGlobalCompoundNumber());
 						iBatch.setConversationalBatchNumber(result.getGlobalCompoundNumber());
 						iBatch.getRegInfo().setBatchTrackingId(Long.getLong(result.getBatchTrackingNo()));
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
 						Amount newAmount = new Amount(result.getAmountMade(), UnitCache.getInstance().getUnit(unit));
//TODO 						newAmount.setUnit(iBatch.getWeightAmount().getUnit());
// 						newAmount.setSigDigits(iBatch.getWeightAmount().getSigDigits());
//						iBatch.setWeightAmount(newAmount);
 						
 						// Check Structure, isomer code, Structure Comment
 						try {
     						ProductBatchModel batch = new ProductBatchModel();
     						ParentCompoundModel cmpd = batch.getCompound();
     						cmpd.setRegNumber(result.getGlobalCompoundNumber());
   							if (NumberUtils.nvl(cmpd.getRegNumber()).length() > 0) {
     							(new CompoundAndBatchInfoUpdater()).getCompoundInfoFromValue(batch, cmpd.getRegNumber(), false, false);
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
     						if (!iBatch.getSaltForm().getCode().equals(result.getSaltCode())){
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
     					ArrayList vials = iBatch.getRegInfo().getSubmitContainerList();
     					for (Iterator nV=result.getVials().iterator(); nV.hasNext(); ) {
     						RegistrationVialDetailsVO nC = (RegistrationVialDetailsVO)nV.next();
     						boolean newFlag = true;
     						for (Iterator v=vials.iterator(); v.hasNext(); ) {
     							BatchSubmissionContainerInfoModel c = (BatchSubmissionContainerInfoModel)v.next();
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
 						
 						List list = result.getResidualSolvents();
 						if (list.size() > 0) {
 							ArrayList newList = new ArrayList();
 							
 							for (Iterator it=list.iterator(); it.hasNext(); ) {
 								ArrayList entry = (ArrayList)it.next();
 								String code = (String)entry.get(0);
 								
         						boolean newFlag = true;
         						for (Iterator it2=iBatch.getRegInfo().getResidualSolventList().iterator(); it2.hasNext(); ) {
         							BatchRegistrationResidualSolvent s = (BatchRegistrationResidualSolvent)it2.next(); // FIXME impossible cast
         							if (s.getCodeAndName().equals(code)) {
         								s.setEqOfSolvent(Double.parseDouble((String)entry.get(1)));
         								newFlag = false;
         								newList.add(s);
         								break;
         							}
         						}
         						
         						if (newFlag) {
     								BatchRegistrationResidualSolvent s = new BatchRegistrationResidualSolvent();
     								s.setCodeAndName(code);
     								s.setEqOfSolvent(Double.parseDouble((String)entry.get(1)));
     								newList.add(s);
         						}
 							}
 							
 							iBatch.getRegInfo().setResidualSolventList(newList);
 						}
 						
 						list = result.getSolubilitySolvents();
 						if (list.size() > 0) {
 							ArrayList newList = new ArrayList();
 							
 							for (Iterator it=list.iterator(); it.hasNext(); ) {
 								ArrayList entry = (ArrayList)it.next();
 								String code = (String)entry.get(0);
 								
         						boolean newFlag = true;
         						for (Iterator it2=iBatch.getRegInfo().getSolubilitySolventList().iterator(); it2.hasNext(); ) {
         							BatchRegistrationSolubilitySolvent s = (BatchRegistrationSolubilitySolvent)it2.next(); // FIXME impossible cast
         							if (s.getCodeAndName().equals(code)) {
         								if (entry.get(4) != null) {
         									s.setQualitative(true);
         									s.setQualiString((String)entry.get(4));
          								} else {
         									s.setQuantitative(true);
             								s.setOperator((String)entry.get(1));
             								s.setSolubilityValue(Double.parseDouble((String)entry.get(2)));
             								s.setSolubilityUnit((String)entry.get(5));
             								if (entry.get(3) != null) s.setSolubilityUpperValue(Double.parseDouble((String)entry.get(3))); else s.setSolubilityUpperValue(0);
         								}
         								newFlag = false;
         								newList.add(s);
         								break;
         							}
         						}
         						
         						if (newFlag) {
         							BatchRegistrationSolubilitySolvent s = new BatchRegistrationSolubilitySolvent();
     								s.setCodeAndName(code);
     								if (entry.get(4) != null) {
     									s.setQualitative(true);
     									s.setQualiString((String)entry.get(4));
      								} else {
     									s.setQuantitative(true);
         								s.setOperator((String)entry.get(1));
         								s.setSolubilityValue(Double.parseDouble((String)entry.get(2)));
         								s.setSolubilityUnit((String)entry.get(5));
         								if (entry.get(3) != null) s.setSolubilityUpperValue(Double.parseDouble((String)entry.get(3))); else s.setSolubilityUpperValue(0);
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
				}
			}
			JOptionPane.showMessageDialog(SingletonRegistration_SummaryViewContainer.this, "" + count + " Record(s) updated.");
		}

/*		public void updateRegSubStatusByJobId(ProductBatchModel iBatch, String jobId, ArrayList jobErrors) {
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
*/
		public void getRegistrationStatus() {
			final SwingWorker worker = new SwingWorker() {
				public Object construct() {
					if (worker1 != null) {
						worker1.get();
					}
					ArrayList jobIdList = new ArrayList();
					int NUM_LOOPS = 15;
					int POLLING_INTERVAL = 30000;
					try {
						RegistrationServiceDelegate reg = new RegistrationServiceDelegate();
						for (int j = 0; j < NUM_LOOPS; j++) {
							// //System.out.println("Inside the status checking
							// thread...");
							jobIdList.clear();
							Iterator jobIdIterator = SingletonRegistration_SummaryViewContainer.this.jobIdBatchNoMap.keySet().iterator();
							while (jobIdIterator.hasNext()) {
								jobIdList.add(jobIdIterator.next());
							}
							if (jobIdList.size() > 0) {
								// pull registration status info and update the
								// batch
								try {
									ArrayList resultList = new ArrayList();
									//ArrayList resultList = reg.getRegistrationStatus(jobIdList);
									updateBatchRegStatus(resultList);
									// testupdateBatchStatus();
									reg_subsumContainerTableModel.fireTableDataChanged();
									keepSelectedRow();
								}/* catch (RegistrationSvcUnavailableException e4) {
									JOptionPane.showMessageDialog(SingletonRegistration_SummaryViewContainer.this,
											"Registration Service is currently unavailable, please try again later.");
									Thread.currentThread().interrupt();
								} */catch (Exception e1) {
									CeNErrorHandler.getInstance().logExceptionMsg(SingletonRegistration_SummaryViewContainer.this, e1);
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
						CeNErrorHandler.getInstance().logExceptionMsg(SingletonRegistration_SummaryViewContainer.this, e);
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

		private void updateBatchRegStatus(ArrayList batchInfoVOList) {
			if (batchInfoVOList.size() <= 0) {
				return;
			} else {
				for (int i = 0; i < reg_subsumContainerTableModel.getRowCount(); i++) {
					ProductBatchModel iBatch = (ProductBatchModel) reg_subsumContainerTableModel.getBatchList().get(i);
					for (int j = 0; j < batchInfoVOList.size(); j++) {
						BatchRegStatusTrackingVO regStatus = (BatchRegStatusTrackingVO) batchInfoVOList.get(j);
						// //System.out.println("The batch number is: " +
						// iBatch.getBatchNumberAsString());
						// //System.out.println("The notebook ref is: " +
						// jobIdBatchNoMap.get(regStatus.getJobID()));
						if (regStatus.getJobID().equals(iBatch.getRegInfo().getJobId())) {
							if (regStatus.getCompoundStatus().equals(BatchRegistrationInfo.PASSED)) {
								iBatch.setConversationalBatchNumber(regStatus.getBatchNumber());
								iBatch.setParentBatchNumber(regStatus.getCompoundParent());
								iBatch.getCompound().setRegNumber(regStatus.getCompoundNumber());
								iBatch.getRegInfo().setRegistrationDate(new Timestamp(regStatus.getRegDate().getTime()));
								iBatch.getRegInfo().setJobId(regStatus.getJobID());
								iBatch.getRegInfo().setBatchTrackingId(Long.valueOf(regStatus.getBatchTrackingID()).longValue());
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
							}
							// update container and sceen status
							updateScreenAndContainerStatus(iBatch, regStatus.getCompoundStatus());
						}
					}
				}
			}
		}

		private void updateScreenAndContainerStatus(ProductBatchModel iBatch, String statusString) {
			List samplesList = iBatch.getRegInfo().getNewSubmitToBiologistTestList();
			ArrayList containerList = iBatch.getRegInfo().getSubmitContainerList();
			for (int j = 0; j < samplesList.size(); j++) {
				BatchSubmissionToBiologistTest batchSubmissionToBiologistTest = (BatchSubmissionToBiologistTest) samplesList.get(j);
				if (!batchSubmissionToBiologistTest.getSubmissionStatus().equals(BatchSubmissionToBiologistTest.SUBMITTED)) {
					if (statusString.equals(BatchRegistrationInfo.PASSED))
						batchSubmissionToBiologistTest.setSubmissionStatus(BatchSubmissionToBiologistTest.SUBMITTED);
					else
						batchSubmissionToBiologistTest.setSubmissionStatus(BatchSubmissionToBiologistTest.NOT_SUBMITTED);
				}
			}
			for (int j = 0; j < containerList.size(); j++) {
				BatchSubmissionContainerInfo batchSubmissionContainerInfo = (BatchSubmissionContainerInfo) containerList.get(j);
				if (!batchSubmissionContainerInfo.getSubmissionStatus().equals(BatchSubmissionToBiologistTest.SUBMITTED)) {
					if (statusString.equals(BatchRegistrationInfo.PASSED))
						batchSubmissionContainerInfo.setSubmissionStatus(BatchSubmissionContainerInfo.SUBMITTED);
					else
						batchSubmissionContainerInfo.setSubmissionStatus(BatchSubmissionContainerInfo.NOT_SUBMITTED);
				}
			}
		}

		public boolean statusDirty(ArrayList batchList) {
			boolean isDirty = false;
			for (int i = 0; i < batchList.size(); i++) {
				if (batchList.get(i) instanceof ProductBatchModel) {
					ProductBatchModel pBatch = (ProductBatchModel) batchList.get(i);
					if (pBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING)) {
						isDirty = true;
						break;
					}
				}
			}
			return isDirty;
		}

		public void buildJobIdBatchNumberMap() {
			for (int i = 0; i < batchList.size(); i++) {
				if (batchList.get(i) instanceof ProductBatchModel) {
					ProductBatchModel pBatch = (ProductBatchModel) batchList.get(i);
					if (pBatch.getRegInfo().getStatus()!= null && pBatch.getRegInfo().getStatus().equals(BatchRegistrationInfo.PROCESSING)) {
						jobIdBatchNoMap.put(pBatch.getRegInfo().getJobId(), pBatch.getBatchNumberAsString());
					}
				}
			}
		}

		private String getUserNTDomain(String userDomainStr) {
			String[] userDomainStringArray = userDomainStr.split(":");
			return userDomainStringArray[0].toUpperCase();
		}

		private String buildSDFile(ProductBatchModel pBatch, boolean forCompoundRegistrationReg) {
			return regSubHandler.buildSDFIle(pBatch, forCompoundRegistrationReg);
		}

		/** Auto-generated event handler method */
		protected void jTableRegBatchesMouseClicked(MouseEvent evt) {
			JTable regBatchesTable = (JTable) evt.getSource();
			int selectedRow = regBatchesTable.rowAtPoint(evt.getPoint());
			int selectedCol = regBatchesTable.columnAtPoint(evt.getPoint());
			if (evt.getButton() == MouseEvent.BUTTON1) {
				setSelectedRow(selectedRow);
				ProductBatchModel newBatch = (ProductBatchModel) (reg_subsumContainerTableModel.getBatchList().get(selectedRow));
				if (newBatch != selectedBatch) {
					setSelectedBatch(newBatch);
					updateDependents();
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
			if (selectedBatch != null && isPageEditable && selectedCol == SingletonRegistration_SummaryViewTableModel.SELECT_TO_REGISTER_IDX) {
				Object o_Boolean = jTableRegBatches.getValueAt(selectedRow, SingletonRegistration_SummaryViewTableModel.SELECT_TO_REGISTER_IDX);
				if (o_Boolean instanceof Boolean) {
					Boolean isSelected = (Boolean) o_Boolean;
					if (!isSelected.booleanValue()) {
						jTableRegBatches.setValueAt(new Boolean(false), selectedRow,
								SingletonRegistration_SummaryViewTableModel.SELECT_TO_REGISTER_IDX);
						// jTableRegBatches.setValueAt(new Boolean(false),
						// selectedRow,
						// Reg_subsumContainerTableModel.SELECT_TO_SUBMIT_IDX);
					} else {
						// validate required fields for sdfile associated with
						// the selected batch
						RegistrationValidator validator = new RegistrationValidator(nbPage, new ArrayList<String>());
						boolean isValid = validator.validateBatchRegInfo(selectedBatch);
						if (!isValid) {
							reg_subsumContainerTableModel.resetRegStatus(selectedRow);
							reg_subsumContainerTableModel.fireTableDataChanged();
						} else {
							jTableRegBatches.setValueAt(new Boolean(true), selectedRow,
									SingletonRegistration_SummaryViewTableModel.SELECT_TO_REGISTER_IDX);
							jButtonRegSub.setEnabled(true);
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
			if (selectedBatch != null && selectedCol == SingletonRegistration_SummaryViewTableModel.STATUS_IDX) {
				// display the error message if the status is "FAILED"
				if (selectedBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_FAILED)) {
					JOptionPane.showMessageDialog((JInternalFrame)parentDialog, selectedBatch.getRegInfo().getErrorMsg());
				}
			}
		}

/*		private boolean validateBatchRegInfo(ProductBatchModel batch) {
			boolean isValid = true;
			NotebookPageModel page = nbPage;
			StringBuffer errorMessage = new StringBuffer();
			// Structure Molfile
			if (batch.getCompound().getNativeSketch() == null || batch.getCompound().getNativeSketch().length <= 0) {
				errorMessage.append("Compound structure is required. \n");
				isValid = false;
			}
			// COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_VALUE
			if (batch.getWeightAmount().doubleValue() <= 0.0) {
				errorMessage.append("Total Amount must be greater than zero. \n");
				isValid = false;
			}
			// COMPOUND_REGISTRATION_NOTEBOOK_REFERENCE
			if (batch.getBatchNumberAsString() == null || batch.getBatchNumberAsString().trim().length() <= 0) {
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
			if (batch.getCompound().getStereoisomerCode() != null && batch.getCompound().getStereoisomerCode().trim().length() > 0) {
				// //System.out.println("the SIC is: " +
				// batch.getCompound().getStereoisomerCode());
				// COMPOUND_REGISTRATION_STRUCTURE_COMMENT is mandatory if
				// COMPOUND_REGISTRATION_GLOBAL_STEREOISOMER_CODE in
				// (('SNENU','LRCMX','ENENU','DSTRU','UNKWN') and
				// ((COMPOUND_REGISTRATION_GPN_TO_ASSIGN_NEW_BATCH is not defined) or
				// (COMPOUND_REGISTRATION_GPN_TO_ASSIGN_NEW_BATCH is 'NEW')))
				if (batch.getCompound().getStereoisomerCode().equals("SNENU")
						|| batch.getCompound().getStereoisomerCode().equals("LRCMX")  HARDCODE 
						|| batch.getCompound().getStereoisomerCode().equals("ENENU")
						|| batch.getCompound().getStereoisomerCode().equals("DSTRU")
						|| batch.getCompound().getStereoisomerCode().equals("UNKWN")) {
					if (!(batch.getCompound().getStructureComments() != null && batch.getCompound().getStructureComments().trim()
							.length() > 0)) {
						errorMessage.append("Structure comment is required. \n");
						isValid = false;
					}
				}
			} else {
				errorMessage.append("Stereoisomer Code is required. \n");
				isValid = false;
			}
			// COMPOUND_REGISTRATION_COMPOUND_SOURCE_CODE and COMPOUND_REGISTRATION_COMPOUND_SOURCE_DETAIL_CODE
			if (batch.getRegInfo().getCompoundSource() != null && batch.getRegInfo().getCompoundSource().trim().length() > 0) {
				if (!(batch.getRegInfo().getCompoundSourceDetail() != null && batch.getRegInfo().getCompoundSourceDetail().trim()
						.length() > 0)) {
					errorMessage.append("Compound Source Detail is required. \n");
					isValid = false;
				}
			}
			if (!(batch.getRegInfo().getCompoundSource() != null && batch.getRegInfo().getCompoundSource().trim().length() > 0)) {
				errorMessage.append("Compound Source is required. \n");
				isValid = false;
			}
			// If COMPOUND_REGISTRATION_COMPOUND_SOURCE_CODE='EXTERNL' then COMPOUND_REGISTRATION_SUPPLIER_CODE and
			// COMPOUND_REGISTRATION_SUPPLIER_REGISTRY_NUMBER are required
			if (batch.getRegInfo().getCompoundSource() != null && batch.getRegInfo().getCompoundSource().trim().length() > 0) {
				if (batch.getRegInfo().getCompoundSource().equals("EXTERNL")) {  HARDCODE 
					if (!(batch.getVendorInfo().getCode() != null && batch.getVendorInfo().getCode().trim().length() > 0)) {
						errorMessage.append("Supplier Code is required. \n");
						isValid = false;
					}
					if (!(batch.getVendorInfo().getSupplierCatalogRef() != null && batch.getVendorInfo().getSupplierCatalogRef().trim()
							.length() > 0)) {
						errorMessage.append("Supplier Registry Number is required. \n");
						isValid = false;
					}
				}
			}
			// If COMPOUND_REGISTRATION_GLOBAL_SALT_CODE not like '00', then COMPOUND_REGISTRATION_GLOBAL_SALT_MOLE is required
			if (batch.getSaltForm().getCode() != null) {
				if (!batch.getSaltForm().getCode().equals("00")) {
					if (batch.getSaltEquivs() <= 0) {
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
				JOptionPane.showMessageDialog((JInternalFrame)parentDialog, errorMessage.toString(), "Errors for " + nbPage.getNbRef(), JOptionPane.INFORMATION_MESSAGE);
			return isValid;
		}*/

		public Amount calculateSubmittedScreenTotal(ProductBatchModel row) {
			double total = 0.0;
			UnitCache uc = UnitCache.getInstance();
			List list = row.getRegInfo().getNewSubmitToBiologistTestList();
			for (int i = 0; i < list.size(); i++) {
				BatchSubmissionToBiologistTest batchSubmissionToBiologistTest = (BatchSubmissionToBiologistTest) list.get(i);
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
			List list = row.getRegInfo().getNewSubmitToBiologistTestList();
			for (int i = 0; i < list.size(); i++) {
				BatchSubmissionToBiologistTest batchSubmissionToBiologistTest = (BatchSubmissionToBiologistTest) list.get(i);
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
			ArrayList containerList = row.getRegInfo().getSubmitContainerList();
			for (int j = 0; j < containerList.size(); j++) {
				BatchSubmissionContainerInfo containerInfo = (BatchSubmissionContainerInfo) containerList.get(j);
				if (!containerInfo.getSubmissionStatus().equals(BatchSubmissionContainerInfo.SUBMITTED)) {
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
			if (calculateSubmittedScreenDirectlyTotal(batch).doubleValue() + calculateSubmittedContainerTotal(batch).doubleValue() > batch
					.getWeightAmount().GetValueInStdUnitsAsDouble()) {
				errorMessage
						.append("Total amount made value must be greater than or equal to directly submitted screen(s) total + submitted container(s) total. \n");
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
				if (((ProductBatchModel) reg_subsumContainerTableModel.getBatchList().get(i)).getBatchNumberAsString().equals(
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
		public SingletonRegistration_SummaryViewTableModel getReg_subsumContainerTableModel() {
			return reg_subsumContainerTableModel;
		}

		/**
		 * @param reg_subsumContainerTableModel
		 *            The reg_subsumContainerTableModel to set.
		 */
		public void setReg_subsumContainerTableModel(SingletonRegistration_SummaryViewTableModel reg_subsumContainerTableModel) {
			this.reg_subsumContainerTableModel = reg_subsumContainerTableModel;
		}

		/**
		 * @param nbPage
		 *            The nbPage to set.
		 */
	/*	public void setPageModel(NotebookPage nbPg) {
			nbPage = nbPg;
			isEditable = (nbPage != null) ? nbPage.isPageEditable() : false;
			if (selectedBatch == null) {
				selectedBatch = new ProductBatch();
				selectedBatch.setType(BatchType.ACTUAL_PRODUCT);
				selectedBatch.getRegInfo().setStatus(BatchRegistrationInfo.PROCESSING);
				selectedBatch.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.PROCESSING);
			}
			regSubHandler.setPageModel(nbPg);
			postInitGUI();
		}*/
		
		public void setPageModel(NotebookPageModel nbPg) {
			nbPage = nbPg;
			isPageEditable = (nbPage != null) ? nbPage.isEditable() : false;
			if (selectedBatch == null) {
				selectedBatch = new ProductBatchModel();
				selectedBatch.setBatchType(BatchType.getBatchType("ACTUAL"));
				selectedBatch.getRegInfo().setCompoundRegistrationStatus(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
			}
			regSubHandler.setPageModel(nbPage);
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
/*				if (selectedBatch != null)
					selectedBatch.deleteRegistrationListener(this);
*/				selectedBatch = newBatch;
/*				if (selectedBatch != null) {
					selectedBatch.addRegistrationListener(this);
					// add Hazardflags if chloracnegen
					if (selectedBatch.isChloracnegen() && selectedBatch.getChloracnegenType().indexOf("Class 1") >= 0) {
						List listOfHzdCodes = selectedBatch.getRegInfo().getCompoundRegistrationHazardCodes();
						// to avoid dulicate addtion to list each time the tab
						// is selected
						if (!listOfHzdCodes.contains("25")) {
							selectedBatch.getRegInfo().getCompoundRegistrationHazardCodes().add("25");
						}
					} else if (selectedBatch.isChloracnegen() && selectedBatch.getChloracnegenType().indexOf("Class 2") >= 0) {
						List listOfHzdCodes = selectedBatch.getRegInfo().getCompoundRegistrationHazardCodes();
						if (!listOfHzdCodes.contains("24")) {
							selectedBatch.getRegInfo().getCompoundRegistrationHazardCodes().add("24");
						}
					}
				}// if not null
*/			}
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