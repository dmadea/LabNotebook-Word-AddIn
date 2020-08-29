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

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.registration.ScreenSearchParams;
import com.chemistry.enotebook.registration.ScreenSearchVO;
import com.chemistry.enotebook.registration.delegate.RegistrationDelegateException;
import com.chemistry.enotebook.utils.CeNDialog;
import com.chemistry.enotebook.utils.CodeTableUtils;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class JDialogLookupAddScreen extends CeNDialog {
	
	private static final long serialVersionUID = 5242709465251404064L;
	
	private JTable jTableScreensFound;
	private JScrollPane jScrollPane1;
	private JTextField ScientistName;
	private JTextField ScreenProtocolTitle;
	private JLabel jLabelScientistName;
	private JLabel jLabelScreenProtocolTitle;
	private JTextField ScreenCode;
	private JLabel jLabelScreenCode;
	private JLabel jLabelSpecifyAddFilter;
	private JButton jButtonSearch;
	private JComboBox jComboBoxProject;
	private JComboBox jComboBoxTArea;
	private JComboBox jComboBoxRegSite;
	private JCheckBox jCheckBoxProject;
	private JCheckBox jCheckBoxTArea;
	private JCheckBox jCheckBoxRegSite;
	private JLabel jLabelAutomaticSearchFilter;
	private JPanel jPanelNoteCont;
	private JLabel jLabelNote2;
	private JLabel jLabelNote1;
	private JPanel jPanelNotes;
	private JPanel jPanelScreensFound;
	private JPanel jPanelNoOfScreens;
	private JLabel NoOfScreensFound;
	private JLabel jLabelNoOfScreensFound;
	private JPanel jPanelSearch_Filter;
	private JPanel jPanelMainCenter;
	private JPanel jPanelFiller2;
	private JPanel jPanelFiller1;
	private JButton jButtonCancel;
	private JButton jButtonAddScreen;
	private JPanel jPanelMainSouth;
	private LookUpScreensTableModel lookUpScreensTableModel;
	private RegSubHandler regSubHandler;
	private SampleSubSumContainer parentContainer;
	private List<ScreenSearchVO> screensList = new ArrayList<ScreenSearchVO>();

	public JDialogLookupAddScreen(JFrame owner, SampleSubSumContainer parentDialog) {
		super(owner);
		parentContainer = parentDialog;
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jPanelMainCenter = new JPanel();
			jPanelSearch_Filter = new JPanel();
			jLabelAutomaticSearchFilter = new JLabel();
			jCheckBoxRegSite = new JCheckBox();
			jCheckBoxTArea = new JCheckBox();
			jCheckBoxProject = new JCheckBox();
			jComboBoxRegSite = new JComboBox();
			jComboBoxTArea = new JComboBox();
			jComboBoxProject = new JComboBox();
			jButtonSearch = new JButton();
			jLabelSpecifyAddFilter = new JLabel();
			jLabelScreenCode = new JLabel();
			ScreenCode = new JTextField();
			jLabelScreenProtocolTitle = new JLabel();
			jLabelScientistName = new JLabel();
			ScreenProtocolTitle = new JTextField();
			ScientistName = new JTextField();
			jPanelNoOfScreens = new JPanel();
			jLabelNoOfScreensFound = new JLabel();
			NoOfScreensFound = new JLabel();
			jPanelScreensFound = new JPanel();
			jScrollPane1 = new JScrollPane();
			jTableScreensFound = new JTable();
			jPanelNoteCont = new JPanel();
			jPanelNotes = new JPanel();
			jLabelNote1 = new JLabel();
			jLabelNote2 = new JLabel();
			jPanelMainSouth = new JPanel();
			jPanelFiller1 = new JPanel();
			jButtonAddScreen = new JButton();
			jPanelFiller2 = new JPanel();
			jButtonCancel = new JButton();
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setTitle("Lookup & Add Screens");
			this.setResizable(false);
			this.setModal(true);
			this.setSize(new java.awt.Dimension(786, 446));
			BoxLayout jPanelMainCenterLayout = new BoxLayout(jPanelMainCenter, 1);
			jPanelMainCenter.setLayout(jPanelMainCenterLayout);
			this.getContentPane().add(jPanelMainCenter, BorderLayout.CENTER);
			jPanelSearch_Filter.setLayout(null);
			jPanelSearch_Filter.setPreferredSize(new java.awt.Dimension(779, 180));
			jPanelSearch_Filter.setSize(new java.awt.Dimension(779, 113));
			jPanelMainCenter.add(jPanelSearch_Filter);
			jLabelAutomaticSearchFilter
					.setText("<HTML><SPAN style='font-size:10.0pt;font-family:Sansserif'><u>Automatic Search Filter</u></SPAN></HTML>");
			jLabelAutomaticSearchFilter.setFont(new java.awt.Font("sansserif", 1, 11));
			jLabelAutomaticSearchFilter.setPreferredSize(new java.awt.Dimension(160, 20));
			jLabelAutomaticSearchFilter.setBounds(new java.awt.Rectangle(32, 3, 160, 20));
			jPanelSearch_Filter.add(jLabelAutomaticSearchFilter);
			jCheckBoxRegSite.setEnabled(true);
			jCheckBoxRegSite.setText("Registration Site:");
			jCheckBoxRegSite.setVisible(true);
			jCheckBoxRegSite.setPreferredSize(new java.awt.Dimension(117, 20));
			jCheckBoxRegSite.setBounds(new java.awt.Rectangle(32, 29, 117, 20));
			jPanelSearch_Filter.add(jCheckBoxRegSite);
			jCheckBoxTArea.setEnabled(false);
			jCheckBoxTArea.setText("Therapeutic Area:");
			jCheckBoxTArea.setPreferredSize(new java.awt.Dimension(117, 20));
			jCheckBoxTArea.setBounds(new java.awt.Rectangle(32, 55, 117, 20));
			jPanelSearch_Filter.add(jCheckBoxTArea);
			jCheckBoxProject.setEnabled(false);
			jCheckBoxProject.setText("Project:");
			jCheckBoxProject.setPreferredSize(new java.awt.Dimension(117, 20));
			jCheckBoxProject.setBounds(new java.awt.Rectangle(32, 80, 117, 20));
			jPanelSearch_Filter.add(jCheckBoxProject);
			jComboBoxRegSite.setEnabled(true);
			jComboBoxRegSite.setPreferredSize(new java.awt.Dimension(136, 20));
			jComboBoxRegSite.setBounds(new java.awt.Rectangle(167, 29, 136, 20));
			jPanelSearch_Filter.add(jComboBoxRegSite);
			jComboBoxTArea.setEnabled(false);
			jComboBoxTArea.setPreferredSize(new java.awt.Dimension(136, 20));
			jComboBoxTArea.setBounds(new java.awt.Rectangle(167, 55, 136, 20));
			jPanelSearch_Filter.add(jComboBoxTArea);
			jComboBoxProject.setEnabled(false);
			jComboBoxProject.setPreferredSize(new java.awt.Dimension(136, 20));
			jComboBoxProject.setBounds(new java.awt.Rectangle(167, 80, 136, 20));
			jPanelSearch_Filter.add(jComboBoxProject);
			jButtonSearch.setText("Search");
			jButtonSearch.setFont(new java.awt.Font("sansserif", 1, 11));
			jButtonSearch.setPreferredSize(new java.awt.Dimension(78, 20));
			jButtonSearch.setBounds(new java.awt.Rectangle(312, 80, 78, 20));
			jPanelSearch_Filter.add(jButtonSearch);
			jButtonSearch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonSearchActionPerformed(evt);
				}
			});
			jLabelSpecifyAddFilter
					.setText("<HTML><SPAN style='font-size:10.0pt;font-family:Sansserif'><u>Specify Additional Filters (use * for wildcard)</u></SPAN></HTML>");
			jLabelSpecifyAddFilter.setFont(new java.awt.Font("sansserif", 1, 11));
			jLabelSpecifyAddFilter.setPreferredSize(new java.awt.Dimension(300, 20));
			jLabelSpecifyAddFilter.setBounds(new java.awt.Rectangle(401, 3, 300, 20));
			jPanelSearch_Filter.add(jLabelSpecifyAddFilter);
			jLabelScreenCode.setText("Screen Code:");
			jLabelScreenCode.setPreferredSize(new java.awt.Dimension(75, 20));
			jLabelScreenCode.setBounds(new java.awt.Rectangle(405, 29, 75, 20));
			jPanelSearch_Filter.add(jLabelScreenCode);
			ScreenCode.setPreferredSize(new java.awt.Dimension(133, 20));
			ScreenCode.setBounds(new java.awt.Rectangle(511, 29, 133, 20));
			jPanelSearch_Filter.add(ScreenCode);
			jLabelScreenProtocolTitle.setText("Screen Protocol Title:");
			jLabelScreenProtocolTitle.setPreferredSize(new java.awt.Dimension(105, 20));
			jLabelScreenProtocolTitle.setBounds(new java.awt.Rectangle(405, 55, 105, 20));
			jPanelSearch_Filter.add(jLabelScreenProtocolTitle);
			jLabelScientistName.setText("Scientist Last Name:");
			jLabelScientistName.setPreferredSize(new java.awt.Dimension(106, 20));
			jLabelScientistName.setBounds(new java.awt.Rectangle(405, 80, 106, 20));
			jPanelSearch_Filter.add(jLabelScientistName);
			ScreenProtocolTitle.setPreferredSize(new java.awt.Dimension(133, 20));
			ScreenProtocolTitle.setBounds(new java.awt.Rectangle(511, 55, 133, 20));
			jPanelSearch_Filter.add(ScreenProtocolTitle);
			ScientistName.setPreferredSize(new java.awt.Dimension(133, 20));
			ScientistName.setBounds(new java.awt.Rectangle(511, 80, 133, 20));
			jPanelSearch_Filter.add(ScientistName);
			FlowLayout jPanelNoOfScreensLayout = new FlowLayout();
			jPanelNoOfScreens.setLayout(jPanelNoOfScreensLayout);
			jPanelNoOfScreensLayout.setAlignment(FlowLayout.LEFT);
			jPanelNoOfScreensLayout.setHgap(5);
			jPanelNoOfScreensLayout.setVgap(2);
			jPanelNoOfScreens.setPreferredSize(new java.awt.Dimension(544, 28));
			jPanelMainCenter.add(jPanelNoOfScreens);
			jLabelNoOfScreensFound.setText("# Of Screens found:");
			jLabelNoOfScreensFound.setHorizontalAlignment(SwingConstants.LEFT);
			jLabelNoOfScreensFound.setHorizontalTextPosition(SwingConstants.LEFT);
			jLabelNoOfScreensFound.setVerticalAlignment(SwingConstants.CENTER);
			jLabelNoOfScreensFound.setVerticalTextPosition(SwingConstants.CENTER);
			jLabelNoOfScreensFound.setFont(new java.awt.Font("Arial", 1, 15));
			jLabelNoOfScreensFound.setPreferredSize(new java.awt.Dimension(212, 24));
			jPanelNoOfScreens.add(jLabelNoOfScreensFound);
			NoOfScreensFound.setText("0");
			NoOfScreensFound.setHorizontalAlignment(SwingConstants.LEFT);
			NoOfScreensFound.setHorizontalTextPosition(SwingConstants.LEFT);
			NoOfScreensFound.setFont(new java.awt.Font("sansserif", 1, 15));
			NoOfScreensFound.setPreferredSize(new java.awt.Dimension(55, 22));
			jPanelNoOfScreens.add(NoOfScreensFound);
			BorderLayout jPanelScreensFoundLayout = new BorderLayout();
			jPanelScreensFound.setLayout(jPanelScreensFoundLayout);
			jPanelScreensFoundLayout.setHgap(0);
			jPanelScreensFoundLayout.setVgap(0);
			jPanelScreensFound.setPreferredSize(new java.awt.Dimension(779, 300));
			jPanelMainCenter.add(jPanelScreensFound);
			jScrollPane1.setVisible(true);
			jScrollPane1.setPreferredSize(new java.awt.Dimension(779, 300));
			jScrollPane1.setSize(new java.awt.Dimension(779, 198));
			jPanelScreensFound.add(jScrollPane1, BorderLayout.CENTER);
			jScrollPane1.add(jTableScreensFound);
			jScrollPane1.setViewportView(jTableScreensFound);
			{
				Object[][] data = new String[][] { { "0", "1" }, { "2", "3" } };
				Object[] columns = new String[] { "One", "Two" };
				javax.swing.table.TableModel dataModel = new javax.swing.table.DefaultTableModel(data, columns);
				jTableScreensFound.setModel(dataModel);
			}
			CardLayout jPanelNoteContLayout = new CardLayout();
			jPanelNoteCont.setLayout(jPanelNoteContLayout);
			jPanelNoteContLayout.setHgap(5);
			jPanelNoteContLayout.setVgap(0);
			jPanelMainCenter.add(jPanelNoteCont);
			BoxLayout jPanelNotesLayout = new BoxLayout(jPanelNotes, 1);
			jPanelNotes.setLayout(jPanelNotesLayout);
			jPanelNotes.setPreferredSize(new java.awt.Dimension(769, 53));
			jPanelNoteCont.add(jPanelNotes, "jPanelNotes");
			jLabelNote1
					.setText("* All available screens shown. CeN displays all unique combinations of screen code and scientist name");
			jLabelNote1.setPreferredSize(new java.awt.Dimension(522, 15));
			jPanelNotes.add(jLabelNote1);
			jLabelNote2.setText("** If a scientist could not be found, please contact your local helpdesk");
			jLabelNote2.setHorizontalAlignment(SwingConstants.LEFT);
			jLabelNote2.setHorizontalTextPosition(SwingConstants.LEFT);
			jPanelNotes.add(jLabelNote2);
			this.getContentPane().add(jPanelMainSouth, BorderLayout.SOUTH);
			jPanelFiller1.setVisible(true);
			jPanelFiller1.setPreferredSize(new java.awt.Dimension(19, 10));
			jPanelMainSouth.add(jPanelFiller1);
			jButtonAddScreen.setText("Add Selected Screens");
			jButtonAddScreen.setFont(new java.awt.Font("sansserif", 1, 11));
			jButtonAddScreen.setPreferredSize(new java.awt.Dimension(157, 25));
			jPanelMainSouth.add(jButtonAddScreen);
			jButtonAddScreen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonAddScreenActionPerformed(evt);
				}
			});
			jPanelFiller2.setVisible(true);
			jPanelFiller2.setPreferredSize(new java.awt.Dimension(48, 10));
			jPanelMainSouth.add(jPanelFiller2);
			jButtonCancel.setText("Cancel");
			jButtonCancel.setFont(new java.awt.Font("sansserif", 1, 11));
			jButtonCancel.setPreferredSize(new java.awt.Dimension(71, 25));
			jPanelMainSouth.add(jButtonCancel);
			jButtonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed(evt);
				}
			});
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					JDialogLookupAddScreen.this.setVisible(false);
					JDialogLookupAddScreen.this.dispose();
				}

				public void windowClosed(WindowEvent e) {
					JDialogLookupAddScreen.this.removeAll();
					JDialogLookupAddScreen.this.setVisible(false);
				}
			});
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
		regSubHandler = new RegSubHandler();
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		lookUpScreensTableModel = new LookUpScreensTableModel();
		jTableScreensFound.setModel(lookUpScreensTableModel);
		CodeTableUtils.fillComboBoxWithSites(jComboBoxRegSite);
		// setHeaderSize();
		// reposition JDialog to the center of the screen
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = this.getPreferredSize();
		setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2 - (labelSize.height / 2));
	}

	public void setHeaderSize() {
		// Get the column model.
		TableColumnModel colModel = jTableScreensFound.getColumnModel();
		// sets the header and column size as per the Header text
		for (int i = 0; i < colModel.getColumnCount(); i++) {
			// Get the column name of the given column.
			String value = jTableScreensFound.getColumnName(i);
			// Calculate the width required for the column.
			FontMetrics metrics = jTableScreensFound.getFontMetrics(jTableScreensFound.getFont());
			int width = metrics.stringWidth(value) + (40 * colModel.getColumnMargin());
			// Get the column at index pColumn, and set its preferred width.
			colModel.getColumn(i).setPreferredWidth(width);
		}
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
			JDialogLookupAddScreen inst = new JDialogLookupAddScreen(null, null);
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonSearchActionPerformed(ActionEvent evt) {
		// clean up table model
		lookUpScreensTableModel.clearModelData();
		lookUpScreensTableModel.fireTableDataChanged();
		ScreenSearchParams params = buildLookUpParams();
		try {
			screensList = regSubHandler.performScreenSearch(params);
		} catch (RegistrationDelegateException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		lookUpScreensTableModel.resetModelData(screensList);
		jTableScreensFound.setModel(lookUpScreensTableModel);
		lookUpScreensTableModel.fireTableDataChanged();
		NoOfScreensFound.setText(screensList.size() + "");
	}

	/**
	 * 
	 */
	private ScreenSearchParams buildLookUpParams() {
		String siteCode = "";
		if (jCheckBoxRegSite.isSelected()) {
			try {
				siteCode = CodeTableCache.getCache().getSiteCode((String) jComboBoxRegSite.getSelectedItem());
			} catch (CodeTableCacheException e) {
				// TODO Auto-generated catch block
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		} else {
			siteCode = "GBL";
		}
		ScreenSearchParams params = new ScreenSearchParams();
		params.setSiteCode(siteCode);
		params.setScreenCode(addWildCards(ScreenCode.getText().trim().toUpperCase()));
		params.setScreenProtocol(addWildCards(ScreenProtocolTitle.getText().trim()));
		params.setScientistName(addWildCards(ScientistName.getText().trim().toUpperCase()));
		// testing:
		// params.setSiteCode("SITE1");
		// params.setScreenCode("%G7%");
		// //params.setScreenProtocol("%TMK%");
		// //params.setScientistName("MILLER");
		return params;
	}

	private String addWildCards(String strSearch) {
		if (strSearch.indexOf("*") >= 0)
			return strSearch.replaceAll("[*]", "%");
		else
			return strSearch;
	}

	/** Auto-generated event handler method */
	protected void jButtonAddScreenActionPerformed(ActionEvent evt) {
		try {
			for (int i = 0; i < lookUpScreensTableModel.getRowCount(); i++) {
				if (((String) lookUpScreensTableModel.getSentByMMList().get(i)).equals("TRUE")) {
					this.getParentContainer().addScreenInfo((ScreenSearchVO) screensList.get(i), true);
				}
				if (((String) lookUpScreensTableModel.getSentByMyselfList().get(i)).equals("TRUE")) {
					this.getParentContainer().addScreenInfo((ScreenSearchVO) screensList.get(i), false);
				}
			}
			this.getParentContainer().updateBatchList(this.getParentContainer().getSelectedBatch());
			this.getParentContainer().updateBioTableModelAndDisplay();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		this.setVisible(false);
		this.dispose();
	}

	// /** Auto-generated event handler method */
	// protected void jButtonAddScreenActionPerformed(ActionEvent evt){
	// ArrayList reagentBatchList = new ArrayList();
	//
	// int[] selectedRows = this.jTableScreensFound.getSelectedRows();
	// try {
	// for (int i = 0; i < selectedRows.length; i++) {
	//
	// this.getParentContainer().addScreenInfo((ScreenSearchVO)screensList.get(selectedRows[i]),
	// lookUpScreensTableModel.isSentByMM(selectedRows[i]));
	// }
	//			
	// this.getParentContainer().updateBatchList(this.getParentContainer().getSelectedBatch());
	// this.getParentContainer().updateTableModelAndDisplay();
	//
	// } catch (Exception e) {
	// CeNErrorHandler.getInstance().logExceptionMsg(null, e);
	// }
	// this.setVisible(false);
	// }
	/** Auto-generated event handler method */
	protected void jButtonCancelActionPerformed(ActionEvent evt) {
		lookUpScreensTableModel.clearModelData();
		lookUpScreensTableModel.fireTableDataChanged();
		this.setVisible(false);
		this.dispose();
	}

	/** Auto-generated event handler method */
	protected void jTableScreensFoundMouseClicked(MouseEvent evt) {
		// TODO add your handler code here
	}

	/**
	 * @return Returns the parentContainer.
	 */
	public SampleSubSumContainer getParentContainer() {
		return parentContainer;
	}

	/**
	 * @param parentContainer
	 *            The parentContainer to set.
	 */
	public void setParentContainer(SampleSubSumContainer parentContainer) {
		this.parentContainer = parentContainer;
	}

	public void dispose() {
		regSubHandler = null;
		parentContainer = null;
		jTableScreensFound.setModel(new DefaultTableModel());
		super.dispose();
	}
}
