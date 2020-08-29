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
package com.chemistry.enotebook.client.gui.page.analytical.parallel;

import com.chemistry.enotebook.analyticalservice.delegate.AnalyticalServiceDelegate;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.page.analytical.DateComboBox;
import com.chemistry.enotebook.client.gui.page.analytical.singleton.AnalyticalServiceFileFilter;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import com.chemistry.enotebook.servicelocator.PropertyException;
import com.chemistry.enotebook.utils.CeNJobProgressHandler;
import com.chemistry.enotebook.utils.SwingWorker;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UpLoadJDialog extends javax.swing.JDialog {
	
	private static final long serialVersionUID = 3457840819995537551L;

	public static final Log log = LogFactory.getLog(UpLoadJDialog.class);
	
	private JButton jButtonCancel;
	private JButton jButtonUpload;
	private JButton jButtonBrowse;
	// private JButton jButtonInsTypes;
	private JTextField jTextFieldFileName;
	private JTextField jTextFieldExpMethod;
	private JTextField jTextFieldFileType;
	private JTextField jTextFieldUserId;
	private JComboBox jComboBoxInsTypes;
	private JTextField jTextFieldExpSites;
	private JTextField jTextFieldSampleRefs;
	private JComboBox jComboBoxDate;
	private JLabel jLabelFileName;
	private JLabel jLabelExpMethod;
	private JLabel jLabelFileType;
	private JLabel jLabelUserId;
	private JLabel jLabelInstrumentTypes;
	private JLabel jLabelExpDates;
	private JLabel jLabelExperimentSites;
	private JLabel jLabelSampleRef;
	private JLabel jLabelAsterisk;
	private String sampleRef;
	//private AnalyticalModel analyticalModel;
	private JFormattedTextField jTextFieldExpTime;
	private PAnalyticalUtility analyticalUtility;
	private List<String> al_AllInsTypes;
	private static final CeNErrorHandler ceh = CeNErrorHandler.getInstance();

	public UpLoadJDialog(String sampleRef, JFrame owner, PAnalyticalUtility analyticalUtility) {
		super(owner);
		setModal(true);
		this.sampleRef = sampleRef;
		this.analyticalUtility = analyticalUtility;
		init();
		initGUI();
	}

	/**
	 * initialize the AnalyticalService Client and get the Sites, FileTypes and instrument Types
	 * 
	 * @throws CeNAnalyticalServiceAccessException
	 * @throws PropertyException
	 * @throws RemoteException
	 * 
	 */
	public void init() {
		AnalyticalServiceDelegate analyticalServiceDelegate;
		try {
			analyticalServiceDelegate = new AnalyticalServiceDelegate();
			al_AllInsTypes = analyticalServiceDelegate.getAllInstrumentTypesFromDB();
		} catch (Exception e) {
			processException(e);
		}
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jLabelSampleRef = new JLabel();
			jTextFieldSampleRefs = new JTextField();
			jLabelExperimentSites = new JLabel();
			jTextFieldExpSites = new JTextField();
			jLabelExpDates = new JLabel();
			jComboBoxDate = new DateComboBox();
			jLabelInstrumentTypes = new JLabel();
			jComboBoxInsTypes = new JComboBox();
			jLabelUserId = new JLabel();
			jTextFieldUserId = new JTextField();
			jLabelFileType = new JLabel();
			jTextFieldFileType = new JTextField();
			jLabelExpMethod = new JLabel();
			jTextFieldExpMethod = new JTextField();
			jLabelFileName = new JLabel();
			jTextFieldFileName = new JTextField();
			jButtonUpload = new JButton();
			jButtonCancel = new JButton();
			jButtonBrowse = new JButton();
			jLabelAsterisk = new JLabel("*");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelAsterisk);
			jLabelAsterisk.setForeground(Color.RED);
			jLabelAsterisk.setVerticalTextPosition(JLabel.TOP);
			setTitle("Upload Analytical Data File");
			this.getContentPane().setLayout(null);
			this.setSize(new java.awt.Dimension(780, 270));
			setLocation(getParent().getX() + (getParent().getWidth() - getWidth()) / 2, getParent().getY() + (getParent().getHeight() / 2));
			jLabelSampleRef.setText("Sample References:");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelSampleRef);
			jLabelSampleRef.setPreferredSize(new java.awt.Dimension(130, 20));
			jLabelSampleRef.setBounds(new java.awt.Rectangle(20, 20, 130, 20));
			this.getContentPane().add(jLabelSampleRef);
			jTextFieldSampleRefs.setPreferredSize(new java.awt.Dimension(230, 20));
			jTextFieldSampleRefs.setForeground(Color.red);
			jTextFieldSampleRefs.setBounds(new java.awt.Rectangle(165, 20, 230, 20));
			this.getContentPane().add(jTextFieldSampleRefs);
			jLabelExperimentSites.setText("Experiment Site:");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelExperimentSites);
			jLabelExperimentSites.setPreferredSize(new java.awt.Dimension(100, 20));
			jLabelExperimentSites.setBounds(new java.awt.Rectangle(415, 20, 100, 20));
			this.getContentPane().add(jLabelExperimentSites);
			jTextFieldExpSites.setEnabled(false);
			jTextFieldExpSites.setPreferredSize(new java.awt.Dimension(200, 20));
			jTextFieldExpSites.setBounds(new java.awt.Rectangle(525, 20, 200, 20));
			this.getContentPane().add(jTextFieldExpSites);
			
			// jLabelExpDates.setText("<html><B><font color=RED>*</font></b>Experiment Date:</html>");
			jLabelExpDates.setText("Experiment Date/Time:");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelExpDates);
			jLabelExpDates.setPreferredSize(new java.awt.Dimension(130, 20));
			jLabelExpDates.setBounds(new java.awt.Rectangle(20, 60, 130, 20));
			this.getContentPane().add(jLabelExpDates);
			MaskFormatter mask = null;
			try {
				mask = new MaskFormatter("##:##:##");
			} catch (ParseException pe) { }
			jTextFieldExpTime = new JFormattedTextField(mask);
			this.getContentPane().add(jTextFieldExpTime);
			jTextFieldExpTime.setEnabled(true);
			jTextFieldExpTime.setBounds(new java.awt.Rectangle(330, 60, 65, 20));
			
			jComboBoxDate.setPreferredSize(new java.awt.Dimension(160, 20));
			jComboBoxDate.setBounds(new java.awt.Rectangle(165, 60, 160, 20));
			this.getContentPane().add(jComboBoxDate);
			jLabelUserId.setText("User ID:");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelUserId);
			jLabelUserId.setPreferredSize(new java.awt.Dimension(100, 20));
			jLabelUserId.setBounds(new java.awt.Rectangle(415, 60, 100, 20));
			this.getContentPane().add(jLabelUserId);
			jTextFieldUserId.setEnabled(false);
			jTextFieldUserId.setPreferredSize(new java.awt.Dimension(200, 20));
			jTextFieldUserId.setBounds(new java.awt.Rectangle(525, 60, 200, 20));
			this.getContentPane().add(jTextFieldUserId);
			// jLabelInstrumentTypes.setText("<html><B><font
			// color=RED>*</font></b>Instrument Type:</html>");
			jLabelInstrumentTypes.setText("Instrument Type:");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelInstrumentTypes);
			jLabelInstrumentTypes.setPreferredSize(new java.awt.Dimension(130, 20));
			jLabelInstrumentTypes.setBounds(new java.awt.Rectangle(20, 105, 130, 20));
			this.getContentPane().add(jLabelInstrumentTypes);
			jComboBoxInsTypes.setPreferredSize(new java.awt.Dimension(152, 20));
			jComboBoxInsTypes.setBounds(new java.awt.Rectangle(165, 105, 152, 20));
			this.getContentPane().add(jComboBoxInsTypes);
			jLabelFileType.setText("File Type:");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelFileType);
			jLabelFileType.setPreferredSize(new java.awt.Dimension(100, 20));
			jLabelFileType.setBounds(new java.awt.Rectangle(415, 105, 100, 20));
			this.getContentPane().add(jLabelFileType);
			jTextFieldFileType.setEnabled(false);
			jTextFieldFileType.setPreferredSize(new java.awt.Dimension(200, 20));
			jTextFieldFileType.setBounds(new java.awt.Rectangle(525, 105, 200, 20));
			this.getContentPane().add(jTextFieldFileType);
			jLabelFileName.setText("File Name:");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelFileName);
			jLabelFileName.setPreferredSize(new java.awt.Dimension(100, 20));
			jLabelFileName.setBounds(new java.awt.Rectangle(415, 150, 100, 20));
			this.getContentPane().add(jLabelFileName);
			jTextFieldFileName.setPreferredSize(new java.awt.Dimension(200, 20));
			jTextFieldFileName.setBounds(new java.awt.Rectangle(525, 150, 200, 20));
			this.getContentPane().add(jTextFieldFileName);
			jLabelExpMethod.setText("Experiment Method:");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelExpMethod);
			jLabelExpMethod.setPreferredSize(new java.awt.Dimension(130, 20));
			jLabelExpMethod.setBounds(new java.awt.Rectangle(20, 150, 130, 20));
			this.getContentPane().add(jLabelExpMethod);
			jTextFieldExpMethod.setPreferredSize(new java.awt.Dimension(230, 20));
			jTextFieldExpMethod.setBounds(new java.awt.Rectangle(165, 150, 230, 20));
			this.getContentPane().add(jTextFieldExpMethod);
			jButtonBrowse.setText("Browse");
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonBrowse);
			jButtonBrowse.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jButtonBrowseMouseClicked(evt);
				}
			});
			jButtonBrowse.setPreferredSize(new java.awt.Dimension(80, 20));
			jButtonBrowse.setBounds(new java.awt.Rectangle(650, 175, 80, 20));
			this.getContentPane().add(jButtonBrowse);
			jButtonUpload.setText("Upload");
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonUpload);
			jButtonUpload.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jButtonUploadMouseClicked(evt);
				}
			});
			jButtonUpload.setPreferredSize(new java.awt.Dimension(80, 20));
			jButtonUpload.setBounds(new java.awt.Rectangle(293, 200, 82, 20));
			this.getContentPane().add(jButtonUpload);
			jButtonCancel.setText("Cancel");
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonCancel);
			jButtonCancel.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jButtonCancelMouseClicked(evt);
				}
			});
			jButtonCancel.setPreferredSize(new java.awt.Dimension(80, 20));
			jButtonCancel.setBounds(new java.awt.Rectangle(403, 200, 79, 20));
			this.getContentPane().add(jButtonCancel);
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					UpLoadJDialog.this.setVisible(false);
					UpLoadJDialog.this.dispose();
				}

				public void windowClosed(WindowEvent e) {
					UpLoadJDialog.this.removeAll();
					UpLoadJDialog.this.setVisible(false);
				}
			});
			postInitGUI();
		} catch (Exception e) {
			processException(e);
		}
	}

	/**
	 * @param evt
	 */
	protected void jButtonBrowseMouseClicked(MouseEvent evt) {
		JFileChooser jfc = new JFileChooser();
		try {
			String sDir = MasterController.getUser().getPreference(NotebookUser.PREF_PATH_ANALYTICAL_UPLOAD);
			if (sDir != null && sDir.length() > 0)
				jfc.setCurrentDirectory(new File(sDir));
		} catch (UserPreferenceException e1) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e1);
		}
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileFilter(new AnalyticalServiceFileFilter());
		int result = jfc.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File attachedFile = jfc.getSelectedFile();
			if (attachedFile != null) {
				String allowedExt = AnalyticalServiceFileFilter.getRawExtensions();
				String path = attachedFile.getAbsolutePath();
				int idx = path.lastIndexOf(".");
				if (idx < 0) {
					jTextFieldFileName.setText("");
					jTextFieldFileType.setText("");
					JOptionPane.showMessageDialog(this,
							"Please select a file with a valid extension, only ("
									+ allowedExt
									+ ") are allowed for Manual Uploads.",
							"File Selection Error", JOptionPane.ERROR_MESSAGE);
				} else {
					String ext = path.substring(idx + 1).toUpperCase();
					if (allowedExt.indexOf(ext) >= 0) {
						jTextFieldFileName.setText(path);
						if (ext.equals("PDF"))
							jTextFieldFileType.setText("PDF");
						else if (ext.equals("RPT"))
							jTextFieldFileType.setText("RPT");
						else
							jTextFieldFileType.setText("RAW");

						try {
							MasterController.getUser().setPreference(NotebookUser.PREF_PATH_ANALYTICAL_UPLOAD, path);
						} catch (UserPreferenceException e1) {
							CeNErrorHandler.getInstance().logExceptionMsg(null, e1);
						}
					} else {
						jTextFieldFileName.setText("");
						jTextFieldFileType.setText("");
						JOptionPane.showMessageDialog(this,
								"Please select a file with a valid extension, only (" + allowedExt + ") are allowed for Manual Uploads.",
								"File Selection Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		jTextFieldSampleRefs.setText(sampleRef);
		try {
			jTextFieldExpSites.setText(CodeTableCache.getCache().getSiteDescription(MasterController.getUser().getSiteCode()));
		} catch (CodeTableCacheException e) {
			processException(e);
		}
		SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy");
		String datenewformat = date.format(new Date());
		datenewformat = datenewformat.replaceAll("/", " ");
		jComboBoxDate.addItem(datenewformat);
		jTextFieldExpTime.setValue((new SimpleDateFormat("HH:mm:ss")).format(new Date()));
		jTextFieldUserId.setText(MasterController.getUser().getNTUserID());
		jTextFieldFileType.setText("");
		// set default Ins type
		if (al_AllInsTypes != null && al_AllInsTypes.size() > 0) {
			for (int i = 0; i < al_AllInsTypes.size(); i++)
				jComboBoxInsTypes.addItem(al_AllInsTypes.get(i).toString());
		}
	}

	/** Auto-generated main method */
	public static void main(String[] args) {
		//showGUI();
	}

	private void jButtonCancelMouseClicked(MouseEvent evt) {
		setVisible(false);
		dispose();
	}

	private void jButtonUploadMouseClicked(MouseEvent evt) {
		if (jTextFieldSampleRefs.getText().length() < 1) {
			JOptionPane.showMessageDialog(this, "Please specify a sample reference for this file upload.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
			 return;
		}
		if (jTextFieldFileName.getText().length() < 1) {
			JOptionPane.showMessageDialog(this, "Please select a file to upload.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
			 return;
		}
		
		setVisible(false);		
	
		String selectedDateTime = jComboBoxDate.getSelectedItem().toString().replaceAll(" ", "/");
		selectedDateTime += " " + jTextFieldExpTime.getText();
		Date dateTemp = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			dateTemp = format.parse(selectedDateTime);
		} catch (ParseException e) {
			processException(e);
		}
		final Date date = dateTemp;
		final String insType = jComboBoxInsTypes.getSelectedItem().toString();
		final String fileName = jTextFieldFileName.getText();
		int lastSlash = fileName.lastIndexOf(File.separator);
		String filePart = (lastSlash < 0) ? fileName : fileName.substring(lastSlash+1);
		final String expMethod = jTextFieldExpMethod.getText();
		final String progressStatus = "Uploading " + filePart + " to Analytical Information Service ...";
		CeNJobProgressHandler.getInstance().addItem(progressStatus);
		SwingWorker fileUpload = new SwingWorker() {
			private Throwable theException = null;

			public Object construct() {
				try {
					analyticalUtility.doFileUpload(jTextFieldSampleRefs.getText(), sampleRef, date, insType, fileName, expMethod);
				} catch (Throwable t) {
					theException = t;
				}
				return null;
			}

			public void finished() {
				if (theException != null) {
					log.error(theException);
				}
				
				UpLoadJDialog.this.dispose();
				
				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
//**TODO				NotebookPageGUI page = (NotebookPageGUI) MasterController.getGUIComponent().jDesktopPane1.getSelectedFrame();
//				if (page != null) page.getAnalyticalSummary_cont().refresh();
			}
		};
		fileUpload.start();	
		
	}


	private void processException(Exception e) {
		ceh.logExceptionMsg(this, "Error occurred while performing Manual upload", e);
	}

	public void setSitesText(String arg0) {
		jTextFieldExpSites.setText(arg0);
	}

	public void dispose() {
		//this.analyticalUtility = null;
		super.dispose();
	}
}
