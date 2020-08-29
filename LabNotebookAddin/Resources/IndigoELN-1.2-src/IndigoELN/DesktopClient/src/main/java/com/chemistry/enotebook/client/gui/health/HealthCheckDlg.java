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
package com.chemistry.enotebook.client.gui.health;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.session.DBHealthStatusVO;
import com.chemistry.enotebook.utils.CeNDialog;
import com.chemistry.enotebook.utils.SwingWorker;
import com.cloudgarden.layout.AnchorConstraint;
import com.cloudgarden.layout.AnchorLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class HealthCheckDlg extends CeNDialog {

	private static final long serialVersionUID = -239293425120033230L;
	
	private JTable jTableService;
	private JTable jTableDB;
	private JScrollPane jScrollPaneService;
	private JScrollPane jScrollPaneDB;
	private JPanel jPanelServiceTitle;
	private JPanel jPanelServiceMain;
	private JPanel jPanelDBTitle;
	private JPanel jPanelDBMain;
	private JButton jButtonLogout;
	private JButton jButtonContinue;
	private JPanel jPanelBtns;
	private JLabel jLabel1;
	private JLabel jLabelDb;
	private JPanel jPanel1Service;
	private JPanel jPanelDb;
	private JLabel jLabelMsg;
	private JPanel jPanelBody;
	private JLabel jLabelTitle;
	private JPanel jPanelHeader;
	private boolean continueFlag = true;
	private HealthCheckDBTableModel healthCheckDBTableModel;
	private HealthcheckServiceTableModel healthcheckServiceTableModel;
	// private ArrayList serviceList = new ArrayList();
	private List<ServiceHealthStatus> serviceStatusList = new ArrayList<ServiceHealthStatus>();
	private List<SwingWorker> workers = new ArrayList<SwingWorker>();

	public HealthCheckDlg() {
		this(null);
	}

	public HealthCheckDlg(Frame frame) {
		super(frame);
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jPanelHeader = new JPanel();
			jLabelTitle = new JLabel();
			jLabelMsg = new JLabel();
			jPanelBody = new JPanel();
			jPanelDBMain = new JPanel();
			jPanelDBTitle = new JPanel();
			jLabelDb = new JLabel();
			jPanelDb = new JPanel();
			jScrollPaneDB = new JScrollPane();
			jTableDB = new JTable();
			jPanelServiceMain = new JPanel();
			jPanelServiceTitle = new JPanel();
			jLabel1 = new JLabel();
			jPanel1Service = new JPanel();
			jScrollPaneService = new JScrollPane();
			jTableService = new JTable();
			jPanelBtns = new JPanel();
			jButtonLogout = new JButton();
			jButtonContinue = new JButton();
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			this.setTitle("System Health Check Dialog");
			this.setModal(true);
			this.setSize(new java.awt.Dimension(740, 510));
			this.setEnabled(true);
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent evt) {
					HealthCheckDlgWindowClosing(evt);
				}
			});
			AnchorLayout jPanelHeaderLayout = new AnchorLayout();
			jPanelHeader.setLayout(jPanelHeaderLayout);
			jPanelHeader.setPreferredSize(new java.awt.Dimension(682, 56));
			this.getContentPane().add(jPanelHeader, BorderLayout.NORTH);

			jLabelTitle.setText("System Health Check Summary");
			jLabelTitle.setVerticalAlignment(SwingConstants.TOP);
			jLabelTitle.setVerticalTextPosition(SwingConstants.TOP);
			jLabelTitle.setFont(new java.awt.Font("Sans",Font.BOLD,22));
			jLabelTitle.setForeground(new java.awt.Color(0,0,220));
			jLabelTitle.setPreferredSize(new java.awt.Dimension(405,31));
			jLabelTitle.setBounds(new java.awt.Rectangle(2,1,435,31));
			jPanelHeader.add(jLabelTitle, new AnchorConstraint(26,597, 580, 3, 1, 1, 1, 1));
	
			jLabelMsg.setText("<html><span style='font-size:12.0pt;font-family:Sans Serif'>Please contact the <b></b><b>Help Desk</b>, if there are any problems you want to report as a result of running the <u>System Health Check</u>.</span></html>");
			jLabelMsg.setVerticalAlignment(SwingConstants.TOP);
			jLabelMsg.setVerticalTextPosition(SwingConstants.TOP);
			jLabelMsg.setFont(new java.awt.Font("Dialog",0,12));
			jLabelMsg.setPreferredSize(new java.awt.Dimension(670,14));
			jLabelMsg.setBounds(new java.awt.Rectangle(5,30,720,15));
			jPanelHeader.add(jLabelMsg, new AnchorConstraint(550,990, 885, 8, 1, 1, 1, 1));

			GridLayout jPanelBodyLayout = new GridLayout(1, 2);
			jPanelBody.setLayout(jPanelBodyLayout);
			jPanelBodyLayout.setRows(1);
			jPanelBodyLayout.setHgap(0);
			jPanelBodyLayout.setVgap(0);
			jPanelBodyLayout.setColumns(2);
			this.getContentPane().add(jPanelBody, BorderLayout.CENTER);
			BorderLayout jPanelDBMainLayout = new BorderLayout();
			jPanelDBMain.setLayout(jPanelDBMainLayout);
			jPanelDBMainLayout.setHgap(0);
			jPanelDBMainLayout.setVgap(0);
			jPanelBody.add(jPanelDBMain);
			BorderLayout jPanelDBTitleLayout = new BorderLayout();
			jPanelDBTitle.setLayout(jPanelDBTitleLayout);
			jPanelDBTitleLayout.setHgap(0);
			jPanelDBTitleLayout.setVgap(0);
			jPanelDBMain.add(jPanelDBTitle, BorderLayout.NORTH);
			jLabelDb.setText("  Database Connections:");
			jLabelDb.setVisible(true);
			jLabelDb.setFont(new java.awt.Font("Sans", 3, 12));
			jLabelDb.setForeground(new java.awt.Color(220, 0, 0));
			jLabelDb.setOpaque(true);
			jPanelDBTitle.add(jLabelDb, BorderLayout.CENTER);
			BorderLayout jPanelDbLayout = new BorderLayout();
			jPanelDb.setLayout(jPanelDbLayout);
			jPanelDbLayout.setHgap(0);
			jPanelDbLayout.setVgap(0);
			jPanelDb.setVisible(true);
			jPanelDb.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			jPanelDBMain.add(jPanelDb, BorderLayout.CENTER);
			jPanelDb.add(jScrollPaneDB, BorderLayout.CENTER);
			jScrollPaneDB.add(jTableDB);
			jScrollPaneDB.setViewportView(jTableDB);
			{
				Object[][] data = new String[][] { { "0", "1" }, { "2", "3" } };
				Object[] columns = new String[] { "One", "Two" };
				javax.swing.table.TableModel dataModel = new javax.swing.table.DefaultTableModel(data, columns);
				jTableDB.setModel(dataModel);
			}
			BorderLayout jPanelServiceMainLayout = new BorderLayout();
			jPanelServiceMain.setLayout(jPanelServiceMainLayout);
			jPanelServiceMainLayout.setHgap(0);
			jPanelServiceMainLayout.setVgap(0);
			jPanelBody.add(jPanelServiceMain);
			BorderLayout jPanelServiceTitleLayout = new BorderLayout();
			jPanelServiceTitle.setLayout(jPanelServiceTitleLayout);
			jPanelServiceTitleLayout.setHgap(0);
			jPanelServiceTitleLayout.setVgap(0);
			jPanelServiceTitle.setVisible(true);
			jPanelServiceMain.add(jPanelServiceTitle, BorderLayout.NORTH);
			jLabel1.setText("  Service Components:");
			jLabel1.setVisible(true);
			jLabel1.setFont(new java.awt.Font("Sans", 3, 12));
			jLabel1.setForeground(new java.awt.Color(220, 0, 0));
			jLabel1.setOpaque(true);
			jPanelServiceTitle.add(jLabel1, BorderLayout.CENTER);
			BorderLayout jPanel1ServiceLayout = new BorderLayout();
			jPanel1Service.setLayout(jPanel1ServiceLayout);
			jPanel1ServiceLayout.setHgap(0);
			jPanel1ServiceLayout.setVgap(0);
			jPanel1Service.setVisible(true);
			jPanel1Service.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			jPanelServiceMain.add(jPanel1Service, BorderLayout.CENTER);
			jPanel1Service.add(jScrollPaneService, BorderLayout.CENTER);
			jScrollPaneService.add(jTableService);
			jScrollPaneService.setViewportView(jTableService);
			{
				Object[][] data = new String[][] { { "0", "1" }, { "2", "3" } };
				Object[] columns = new String[] { "One", "Two" };
				javax.swing.table.TableModel dataModel = new javax.swing.table.DefaultTableModel(data, columns);
				jTableService.setModel(dataModel);
			}
			AnchorLayout jPanelBtnsLayout = new AnchorLayout();
			jPanelBtns.setLayout(jPanelBtnsLayout);
			jPanelBtns.setPreferredSize(new java.awt.Dimension(682, 31));
			this.getContentPane().add(jPanelBtns, BorderLayout.SOUTH);
			jButtonLogout.setText("Log-Out");
			jButtonLogout.setVisible(true);
			jButtonLogout.setFont(new java.awt.Font("Arial", 1, 12));
			jButtonLogout.setPreferredSize(new java.awt.Dimension(93, 24));
			jButtonLogout.setBounds(new java.awt.Rectangle(384, 3, 93, 24));
			jPanelBtns.add(jButtonLogout, new AnchorConstraint(112, 652, 887, 525, 1, 1, 1, 1));
			jButtonLogout.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonLogoutActionPerformed(evt);
				}
			});
			jButtonContinue.setText("Continue");
			jButtonContinue.setFont(new java.awt.Font("Arial", 1, 12));
			jButtonContinue.setPreferredSize(new java.awt.Dimension(96, 24));
			jButtonContinue.setBounds(new java.awt.Rectangle(255, 3, 96, 24));
			jPanelBtns.add(jButtonContinue, new AnchorConstraint(112, 479, 887, 348, 1, 1, 1, 1));
			jButtonContinue.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonContinueActionPerformed(evt);
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
		this.setModal(true);
		// make the buttons bold
		Font f_arial_12 = new Font("Arial", Font.BOLD, 12);
		jButtonContinue.setFont(f_arial_12);
		jButtonLogout.setFont(f_arial_12);
		// set table models
		healthCheckDBTableModel = new HealthCheckDBTableModel();
		jTableDB.setModel(healthCheckDBTableModel);
		healthcheckServiceTableModel = new HealthcheckServiceTableModel();
		jTableService.setModel(healthcheckServiceTableModel);
		// setup tables
		setupTable(jTableDB);
		setupTable(jTableService);
		if (MasterController.getHealthFlag().equals(ServiceHealthStatus.BAD_STATUS)) {
			// populate the db model
			healthCheckDBTableModel.resetModelData(HealthCheckHandler.dbStatusList);
			healthCheckDBTableModel.fireTableDataChanged();
			// popoulate the service model
			healthcheckServiceTableModel.resetModelData(HealthCheckHandler.serviceStatusList);
			healthcheckServiceTableModel.fireTableDataChanged();
		} else {
			// check database health
			checkSystemDBHealth();
			// check services health
			checkServiceHealth();
		}
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = this.getPreferredSize();
		setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2 - (labelSize.height / 2));
	}

	private void setupTable(JTable tableName) {
		TableColumnModel vColumModel = tableName.getColumnModel();
		TableColumn column = vColumModel.getColumn(0);
		column.setPreferredWidth(200);
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 7816886861809136215L;

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				setText((String) value);
				setToolTipText((String) value);
				return this;
			}
		};
		column.setCellRenderer(renderer);
		column = vColumModel.getColumn(1);
		CheckBoxCellRenderer checkBoxCellRenderer = new CheckBoxCellRenderer();
		checkBoxCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		column.setCellRenderer(checkBoxCellRenderer);
		column.setPreferredWidth(60);
		column = vColumModel.getColumn(2);
		renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		column.setCellRenderer(renderer);
		column.setPreferredWidth(40);
		tableName.setRowHeight(22);
		tableName.setRowSelectionAllowed(false);
		tableName.setColumnSelectionAllowed(false);
		setTableHeader(tableName);
	}

	public void setTableHeader(JTable table) {
		JTableHeader header = table.getTableHeader();
		final Font boldFont = header.getFont().deriveFont(Font.BOLD);
		final TableCellRenderer headerRenderer = header.getDefaultRenderer();
		header.setDefaultRenderer(new TableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				Component comp = headerRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				comp.setFont(boldFont);
				return comp;
			}
		});
	}

	/**
	 * @param healthCheckHandler
	 * @param healthCheckDBTableModel
	 */
	private void checkDBHealth() {
		List<DBHealthStatusVO> dbvoList = HealthCheckHandler.checkDSHealth();
		healthCheckDBTableModel.resetModelData(dbvoList);
		healthCheckDBTableModel.fireTableDataChanged();
	}

	public void checkSystemDBHealth() {
		final SwingWorker worker = new SwingWorker(true) {
			public Object construct() {
				try {
					checkDBHealth();
				} catch (Exception e) {
					return "Interrupted";
				} finally {
					workers.remove(this);
				}
				return "All Done";
			}
		};
		workers.add(worker);
		worker.start();
	}

	public void checkSystemServiceHealth(final ServiceHealthStatus service) {
		final SwingWorker worker = new SwingWorker(true) {
			public Object construct() {
				boolean isAvailable = false;
				try {
					isAvailable = HealthCheckHandler.checkServiceHealth(service.getServiceName());
					if (isAvailable)
						service.setHealthStatus(ServiceHealthStatus.GOOD_STATUS);
					else
						service.setHealthStatus(ServiceHealthStatus.BAD_STATUS);
					// update table model
					healthcheckServiceTableModel.updateModelData(service);
					healthcheckServiceTableModel.fireTableDataChanged();
				} catch (Exception e) {
					return "Interrupted";
				} finally {
					workers.remove(this);
				}
				return "All Done";
			}
		};
		workers.add(worker);
		worker.start();
	}

	public void checkServiceHealth() {
		final SwingWorker worker = new SwingWorker(true) {
			public Object construct() {
				try {
					HealthCheckHandler.buildServiceList();
					serviceStatusList = HealthCheckHandler.buildServiceStatusList();
					// popoulate the service list with initial status
					healthcheckServiceTableModel.resetModelData(serviceStatusList);
					healthcheckServiceTableModel.fireTableDataChanged();
					for (int i = 0; i < serviceStatusList.size(); i++) {
						ServiceHealthStatus service = (ServiceHealthStatus) serviceStatusList.get(i);
						checkSystemServiceHealth(service);
					}
				} catch (Exception e) {
					return "Interrupted";
				} finally {
					workers.remove(this);
				}
				return "All Done";
			}
		};
		workers.add(worker);
		worker.start();
	}

	/** Auto-generated main method */
	public static void main(String[] args) {
		showGUI();
	}

	/**
	 * This static method creates a new instance of this class and shows it inside a new JFrame, (unless it is already a JFrame).
	 * 
	 * It is a convenience method for showing the GUI, but it can be copied and used as a basis for your own code. * It is
	 * auto-generated code - the body of this method will be re-generated after any changes are made to the GUI. However, if you
	 * delete this method it will not be re-created.
	 */
	public static void showGUI() {
		try {
			HealthCheckDlg inst = new HealthCheckDlg();
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean checkHealth() {
		jButtonLogout.setVisible(true);
		setVisible(true);
		return continueFlag;
	}

	public boolean checkHealthNoLogout() {
		jButtonLogout.setVisible(false);
		setVisible(true);
		return continueFlag;
	}

	/** Auto-generated event handler method */
	protected void jButtonContinueActionPerformed(ActionEvent evt) {
		killThreads();
		setVisible(false);
	}

	/** Auto-generated event handler method */
	protected void jButtonLogoutActionPerformed(ActionEvent evt) {
		killThreads();
		continueFlag = false;
		setVisible(false);
	}

	/** Auto-generated event handler method */
	protected void HealthCheckDlgWindowClosing(WindowEvent evt) {
		killThreads();
		setVisible(false);
	}

	private void killThreads() {
		for (Iterator<SwingWorker> it = workers.iterator(); it.hasNext();) {
			SwingWorker worker = (SwingWorker) it.next();
			if (worker != null) {
				worker.interrupt();
			}
		}
	}

	public void resetButtons() {
		continueFlag = false;
		jButtonLogout.setVisible(true);
		jButtonContinue.setVisible(false);
	}
}
