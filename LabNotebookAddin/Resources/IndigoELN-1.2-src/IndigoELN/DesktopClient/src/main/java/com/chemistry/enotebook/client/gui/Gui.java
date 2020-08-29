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
package com.chemistry.enotebook.client.gui;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.datamodel.speedbar.CRONotebookPage;
import com.chemistry.enotebook.client.datamodel.speedbar.SpeedBarNodeInterface;
import com.chemistry.enotebook.client.datamodel.speedbar.SpeedBarPage;
import com.chemistry.enotebook.client.datamodel.speedbar.SpeedBarPageGroup;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.gui.common.utils.PageListView;
import com.chemistry.enotebook.client.gui.common.utilsui.MemoryStatusBarItem;
import com.chemistry.enotebook.client.gui.common.utilsui.ProgressStatusBarItem;
import com.chemistry.enotebook.client.gui.common.utilsui.TimeStatusBarItem;
import com.chemistry.enotebook.client.gui.contents.NotebookContentsGUI;
import com.chemistry.enotebook.client.gui.contents.NotebookContentsTableModel;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.client.gui.esig.SignatureHandler;
import com.chemistry.enotebook.client.gui.esig.SignatureTimerHandler;
import com.chemistry.enotebook.client.gui.health.HealthCheckDlg;
import com.chemistry.enotebook.client.gui.page.PageChangesCache;
import com.chemistry.enotebook.client.gui.page.ParallelNotebookPageGUI;
import com.chemistry.enotebook.client.gui.page.SingletonNotebookPageGUI;
import com.chemistry.enotebook.client.gui.page.reagents.ICancel;
import com.chemistry.enotebook.client.gui.page.reagents.ProgressBarDialog;
import com.chemistry.enotebook.client.gui.preferences.JDialogPrefs;
import com.chemistry.enotebook.client.gui.query_search.Parallel_Query_SearchContainer;
import com.chemistry.enotebook.client.gui.speedbar.CROTreeHandler;
import com.chemistry.enotebook.client.gui.speedbar.CommonHandlerInterface;
import com.chemistry.enotebook.client.gui.speedbar.SpeedBarHandler;
import com.chemistry.enotebook.client.gui.speedbar.SpeedBarModel;
import com.chemistry.enotebook.client.print.gui.PrintRequest;
import com.chemistry.enotebook.client.utils.ImageToolTip;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.experiment.utils.SystemPropertyException;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.utils.CeNJobProgressHandler;
import com.chemistry.enotebook.utils.ProgressBarJobItem;
import com.chemistry.enotebook.utils.SwingWorker;
import com.cloudgarden.layout.AnchorLayout;
import com.common.chemistry.codetable.CodeTableCache;
import com.l2fprod.common.swing.JOutlookBar;
import com.l2fprod.common.swing.StatusBar;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Gui extends JFrame {

	private static final long serialVersionUID = -2716609803452527963L;

	private static final Log log = LogFactory.getLog(Gui.class);

	private JButton jButtonLoadPage;
	private JMenuItem jMenuItemSearch;
	private static CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	
	private JPopupMenu speedbarPop = new JPopupMenu();
	private JMenuBar jMenuBar1;
	private JMenu jMenuFile;
	private JMenu jMenuNew;
	private JSeparator jSeparator2;
	private JMenuItem openFileMenuItem;
	private JMenuItem closeFileMenuItem;
	private JMenuItem closeAllMenuItem;
	private JSeparator jSeparator3;
	private JMenuItem saveMenuItem = new JMenuItem();
	private JMenuItem repeatExperimentMenuItem;
	private JMenuItem saveAllMenuItem = new JMenuItem();
	private JSeparator jSeparator4;
	private JMenuItem printSetupMenuItem;
	private JMenuItem printMenuItem;
	private JSeparator jSeparator5;
	private JMenuItem exitMenuItem;
	private JMenu jMenuEdit;
	private JMenuItem undoMenuItem;
	private JSeparator jSeparator1;
	private JMenuItem cutMenuItem;
	private JMenuItem pasteMenuItem;
	private JMenuItem copyMenuItem;
	private JMenu jMenuTools;
	private JMenuItem jMenuItemOptions;
	private JSeparator jSeparator6;
	private JMenuItem jMenuItemSAFESign;
	private JMenuItem jMenuItemSigQueue;
	private JMenu jMenuHelp;
	private JMenu helpMenuItem;
	private JMenuItem jMenuItemWebTrain;
	private JMenuItem jMenuItemQuickStart;
	private JMenuItem jMenuItemQuickRef;
	private JMenuItem jMenuItemQnA;
	private JMenuItem jMenuItemTestScripts;
	private JMenuItem jMenuItemBusinessRules;
	private JMenuItem jMenuItemGrmg;
    private JMenuItem JMenuItemRT;
    private JMenuItem jMenuItemTlc;
    private JMenuItem jMenuItemOffline;
	private JMenu jMenuSigs;
    private JMenuItem jMenuItemInstruct;
	private JMenuItem jMenuItemCertsInstruct;
	private JMenuItem jMenuItemUssiInstruct;
	private JMenuItem jMenuItemSigInstruct;
	private JMenuItem jMenuItemHealthCheck;
	private JMenuItem jMenuItemAbout;
	private JToolBar jToolBarMain;
	private JButton jButtonNewConceptPage;
	private JButton jButtonNewMedChemPage;
	private JButton jButtonNewParallelPage;
	private JButton jButtonSavePage = new JButton();
	private JButton jButtonSaveAll = new JButton();
	private JButton jButtonPrint;
	private JButton jButtonSearch;
	private JButton jButtonRefresh;
	private JPanel jPanel3;
	private JScrollPane jScrollPane1;
	private JSplitPane jSplitPane2;
	private CeNDesktopPane jDesktopPane1;
	private JOutlookBar OutlookBar;
	// private JPanel templateDocPane;
	private JTree jTreeSpeedBar;
	private JTree jTreeSpeedBarUser;
	private JTree croTree;

	private StatusBar statusBar;
	private ProgressStatusBarItem progressBar;
	private ProgressStatusBarItem.CancelCallback cancellCallback;
	private ICancel cancellableActivity;
	private MemoryStatusBarItem memoryBar;
	private SpeedBarHandler speedBarAllSiteHandler = null;
	private SpeedBarHandler speedBarMyBookHandler = null;
	private CROTreeHandler croTreeHandler = null;

	private transient guiProgressWorker worker = null;
	private boolean _shuttingDown = false;
	private GuiResizingEventListener guiResizer = null;
	// private CeNDesktopPane JDesktopPane1;
	private JScrollPane externalScroll = new JScrollPane();;
	private JScrollPane croTreeScroll = new JScrollPane();


	public Gui() {
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			externalScroll.getViewport().setBackground(Color.WHITE);
			jSplitPane2 = new JSplitPane();
			jDesktopPane1 = new CeNDesktopPane();
			jScrollPane1 = new JScrollPane();
			jPanel3 = new JPanel();
			jToolBarMain = new JToolBar();
			jButtonNewConceptPage = new JButton();
			jButtonNewMedChemPage = new JButton();
			jButtonNewParallelPage = new JButton();
			jButtonLoadPage = new JButton();

			jButtonPrint = new JButton();
			jButtonSearch = new JButton();
			jButtonRefresh = new JButton();
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			this.setTitle(CeNConstants.PROGRAM_NAME);
			this.setSize( new Dimension( 639, 384) );
			this.setEnabled(true);
			this.addWindowListener(new WindowAdapter() {
				public void windowActivated(WindowEvent evt) {
					refreshIcons();
				}

				public void windowOpened(WindowEvent evt) {
					GuiWindowOpened(evt);
				}

				public void windowClosing(WindowEvent evt) {
					GuiWindowClosing(evt);
				}
			});
			jSplitPane2.setDividerSize(8);
			jSplitPane2.setOneTouchExpandable(true);
			this.getContentPane().add(jSplitPane2, BorderLayout.CENTER);
			jDesktopPane1.setPreferredSize(new Dimension(0, 324));
			jDesktopPane1.setBackground(new Color(58, 110, 165));
			jSplitPane2.add(jDesktopPane1, JSplitPane.RIGHT);
			jScrollPane1.setPreferredSize(new Dimension(190, 305));
			jSplitPane2.add(jScrollPane1, JSplitPane.LEFT);
			BorderLayout jPanel3Layout = new BorderLayout();
			jPanel3.setLayout(jPanel3Layout);
			jPanel3Layout.setHgap(0);
			jPanel3Layout.setVgap(0);
			jPanel3.setPreferredSize(new Dimension(170, 298));
			jScrollPane1.add(jPanel3);
			jScrollPane1.setViewportView(jPanel3);
			this.getContentPane().add(jToolBarMain, BorderLayout.NORTH);

			jButtonNewMedChemPage.setHorizontalTextPosition(SwingConstants.CENTER);
			jButtonNewMedChemPage.setIconTextGap(1);
			jButtonNewMedChemPage.setBorderPainted(true);
			jButtonNewMedChemPage.setToolTipText("Create New Singleton Experiment (in last notebook written to)");
			jButtonNewMedChemPage.setPreferredSize(new Dimension(16, 16));
			jButtonNewMedChemPage.setMinimumSize(new Dimension(25, 25));
			jButtonNewMedChemPage.setMaximumSize(new Dimension(25, 25));
			jButtonNewMedChemPage.setSize(new Dimension(25, 25));
			jToolBarMain.add(jButtonNewMedChemPage);
			jButtonNewMedChemPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonNewPageActionPerformed(evt);
				}
			});

			jButtonNewParallelPage.setHorizontalTextPosition(SwingConstants.CENTER);
			jButtonNewParallelPage.setIconTextGap(1);
			jButtonNewParallelPage.setBorderPainted(true);
			jButtonNewParallelPage.setToolTipText("Create New Parallel Experiment (in last notebook written to)");
			jButtonNewParallelPage.setPreferredSize(new Dimension(16, 16));
			jButtonNewParallelPage.setMinimumSize(new Dimension(25, 25));
			jButtonNewParallelPage.setMaximumSize(new Dimension(25, 25));
			jButtonNewParallelPage.setSize(new Dimension(25, 25));
			jButtonNewParallelPage.setEnabled(MasterController.isParallelCreateEnabled());
			jToolBarMain.add(jButtonNewParallelPage);
			jButtonNewParallelPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonNewParallelPageActionPerformed(evt);
				}
			});

			jButtonNewConceptPage.setHorizontalTextPosition(SwingConstants.CENTER);
			jButtonNewConceptPage.setIconTextGap(1);
			jButtonNewConceptPage.setBorderPainted(true);
			jButtonNewConceptPage.setToolTipText("Create New Conception Record (in last notebook written to)");
			jButtonNewConceptPage.setPreferredSize(new Dimension(16, 16));
			jButtonNewConceptPage.setMinimumSize(new Dimension(25, 25));
			jButtonNewConceptPage.setMaximumSize(new Dimension(25, 25));
			jButtonNewConceptPage.setSize(new Dimension(25, 25));
			jToolBarMain.add(jButtonNewConceptPage);
			jButtonNewConceptPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonNewPageConceptionActionPerformed(evt);
				}
			});

			jButtonLoadPage.setHorizontalTextPosition(SwingConstants.CENTER);
			jButtonLoadPage.setIconTextGap(1);
			jButtonLoadPage.setBorderPainted(true);
			jButtonLoadPage.setVisible(true);
			jButtonLoadPage.setToolTipText("Open a Specific Experiment");
			jButtonLoadPage.setPreferredSize(new Dimension(25, 25));
			jButtonLoadPage.setMinimumSize(new Dimension(25, 25));
			jButtonLoadPage.setMaximumSize(new Dimension(25, 25));
			jButtonLoadPage.setSize(new Dimension(25, 25));
			jToolBarMain.add(jButtonLoadPage);
			jButtonLoadPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonLoadPageActionPerformed(evt);
				}
			});
			jButtonSavePage.setVisible(true);
			jButtonSavePage.setToolTipText("Save Experiment");
			jButtonSavePage.setPreferredSize(new Dimension(25, 25));
			jButtonSavePage.setMinimumSize(new Dimension(25, 25));
			jButtonSavePage.setMaximumSize(new Dimension(25, 25));
			jToolBarMain.add(jButtonSavePage);
			jButtonSavePage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonSavePageActionPerformed(evt);
				}
			});
			jButtonSaveAll.setVisible(true);
			jButtonSaveAll.setToolTipText("Save All Experiments");
			jButtonSaveAll.setPreferredSize(new Dimension(25, 25));
			jButtonSaveAll.setMinimumSize(new Dimension(25, 25));
			jButtonSaveAll.setMaximumSize(new Dimension(25, 25));
			jToolBarMain.add(jButtonSaveAll);
			jButtonSaveAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonSaveAllActionPerformed(evt);
				}
			});
			jButtonPrint.setVisible(true);
			jButtonPrint.setToolTipText("Print Active Experiment Being Viewed");
			jButtonPrint.setPreferredSize(new Dimension(25, 25));
			jButtonPrint.setMinimumSize(new Dimension(25, 25));
			jButtonPrint.setMaximumSize(new Dimension(25, 25));
			jToolBarMain.add(jButtonPrint);
			jButtonPrint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonPrintActionPerformed(evt);
				}
			});
			jButtonSearch.setDefaultCapable(true);
			jButtonSearch.setVisible(true);
			jButtonSearch.setToolTipText("Search");
			jButtonSearch.setPreferredSize(new Dimension(25, 25));
			jButtonSearch.setMinimumSize(new Dimension(25, 25));
			jButtonSearch.setMaximumSize(new Dimension(25, 25));
			jButtonSearch.setSize(new Dimension(25, 25));
			jToolBarMain.add(jButtonSearch);
			jButtonSearch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonSearchActionPerformed(evt);
				}
			});
			jButtonRefresh.setVisible(true);
			jButtonRefresh.setToolTipText("Refresh Speed Bar");
			jButtonRefresh.setPreferredSize(new Dimension(25, 25));
			jButtonRefresh.setMinimumSize(new Dimension(25, 25));
			jButtonRefresh.setMaximumSize(new Dimension(25, 25));
			jToolBarMain.add(jButtonRefresh);
			jButtonRefresh.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonRefreshActionPerformed(evt);
				}
			});
			jMenuBar1 = new JMenuBar();
			jMenuFile = new JMenu();
			jMenuNew = new JMenu("Create New Experiment");

			jSeparator2 = new JSeparator();
			openFileMenuItem = new JMenuItem();
			closeFileMenuItem = new JMenuItem();
			closeAllMenuItem = new JMenuItem();
			jSeparator3 = new JSeparator();

			repeatExperimentMenuItem = new JMenuItem();

			jSeparator4 = new JSeparator();
			printSetupMenuItem = new JMenuItem();
			printMenuItem = new JMenuItem();
			jSeparator5 = new JSeparator();
			exitMenuItem = new JMenuItem();
			jMenuEdit = new JMenu();
			undoMenuItem = new JMenuItem();
			jSeparator1 = new JSeparator();
			cutMenuItem = new JMenuItem();
			copyMenuItem = new JMenuItem();
			pasteMenuItem = new JMenuItem();
			jMenuTools = new JMenu();
			jMenuItemOptions = new JMenuItem();
			jMenuItemSearch = new JMenuItem();
			jSeparator6 = new JSeparator();
			jMenuItemSAFESign = new JMenuItem();
			jMenuItemSigQueue = new JMenuItem();
			jMenuHelp = new JMenu();
			helpMenuItem = new JMenu();
			jMenuItemWebTrain = new JMenuItem();
			jMenuItemQuickStart = new JMenuItem();
			jMenuItemQuickRef = new JMenuItem();
			jMenuItemQnA = new JMenuItem();
			jMenuItemTestScripts = new JMenuItem();
			jMenuItemBusinessRules = new JMenuItem();
			jMenuItemGrmg = new JMenuItem();
		    JMenuItemRT = new JMenuItem();
			jMenuSigs = new JMenu();
		    jMenuItemInstruct = new JMenuItem();
			jMenuItemCertsInstruct = new JMenuItem();
			jMenuItemUssiInstruct = new JMenuItem();
			jMenuItemSigInstruct = new JMenuItem();
			jMenuItemHealthCheck = new JMenuItem();
			jMenuItemAbout = new JMenuItem();
			jMenuItemTlc = new JMenuItem();
			jMenuItemOffline = new JMenuItem();

			setJMenuBar(jMenuBar1);
			jMenuFile.setText("File");
			jMenuFile.setVisible(true);
			jMenuBar1.add(jMenuFile);

			jMenuFile.add(jMenuNew);

			jSeparator2.setVisible(true);
			jMenuFile.add(jSeparator2);
			openFileMenuItem.setText("Open ...");
			openFileMenuItem.setVisible(true);
			jMenuFile.add(openFileMenuItem);
			openFileMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					openFileMenuItemActionPerformed(evt);
				}
			});
			closeFileMenuItem.setText("Close");
			closeFileMenuItem.setVisible(true);
			jMenuFile.add(closeFileMenuItem);
			closeFileMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					closeFileMenuItemActionPerformed(evt);
				}
			});
			closeAllMenuItem.setText("Close All");
			closeAllMenuItem.setVisible(true);
			jMenuFile.add(closeAllMenuItem);
			closeAllMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					closeAllMenuItemActionPerformed(evt);
				}
			});
			jSeparator3.setVisible(true);
			jMenuFile.add(jSeparator3);
			saveMenuItem.setText("Save");
			saveMenuItem.setVisible(true);
			jMenuFile.add(saveMenuItem);
			saveMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					saveMenuItemActionPerformed(evt);
				}
			});
			saveAllMenuItem.setText("Save All");
			saveAllMenuItem.setVisible(true);
			jMenuFile.add(saveAllMenuItem);
			saveAllMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					//saveAllMenuItemActionPerformed(evt);
					jButtonSaveAllActionPerformed(evt);
				}
			});
			repeatExperimentMenuItem.setText("Repeat This Experiment ...");
			repeatExperimentMenuItem.setVisible(true);
			jMenuFile.add(repeatExperimentMenuItem);
			repeatExperimentMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					repeatExperiment();
				}
			});
			jSeparator4.setVisible(true);
			jMenuFile.add(jSeparator4);
			printSetupMenuItem.setText("Page Setup ...");
			printSetupMenuItem.setVisible(false);
			jMenuFile.add(printSetupMenuItem);
			printMenuItem.setText("Print ...");
			printMenuItem.setVisible(true);
			jMenuFile.add(printMenuItem);
			printMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					printMenuItemActionPerformed(evt);
				}
			});
			jSeparator5.setVisible(true);
			jMenuFile.add(jSeparator5);
			exitMenuItem.setText("Exit");
			exitMenuItem.setVisible(true);
			jMenuFile.add(exitMenuItem);
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					exitMenuItemActionPerformed(evt);
				}
			});
			jMenuEdit.setText("Edit");
			jMenuEdit.setVisible(false);
			jMenuBar1.add(jMenuEdit);
			jMenuEdit.addMenuListener(new MenuListener() {
				public void menuCanceled(MenuEvent evt) {
				}

				public void menuDeselected(MenuEvent evt) {
				}

				public void menuSelected(MenuEvent evt) {
					jMenuEditMenuSelected(evt);
				}
			});
			undoMenuItem.setText("Undo");
			undoMenuItem.setVisible(true);
			pasteMenuItem.setMnemonic('Z');
			copyMenuItem.setAccelerator(KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
			jMenuEdit.add(undoMenuItem);
			undoMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					undoMenuItemActionPerformed(evt);
				}
			});
			jSeparator1.setVisible(true);
			jMenuEdit.add(jSeparator1);
			cutMenuItem.setText("Cut");
			cutMenuItem.setVisible(true);
			cutMenuItem.setMnemonic('X');
			cutMenuItem.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
			jMenuEdit.add(cutMenuItem);
			cutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					cutMenuItemActionPerformed(evt);
				}
			});
			copyMenuItem.setText("Copy");
			copyMenuItem.setVisible(true);
			copyMenuItem.setMnemonic('C');
			copyMenuItem.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
			jMenuEdit.add(copyMenuItem);
			copyMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					copyMenuItemActionPerformed(evt);
				}
			});
			pasteMenuItem.setText("Paste");
			pasteMenuItem.setVisible(true);
			pasteMenuItem.setMnemonic('V');
			pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke('V', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
			jMenuEdit.add(pasteMenuItem);
			pasteMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					pasteMenuItemActionPerformed(evt);
				}
			});
			jMenuTools.setText("Options");
			jMenuTools.setVisible(true);
			jMenuBar1.add(jMenuTools);
			jMenuItemOptions.setText("Preferences");
			jMenuTools.add(jMenuItemOptions);
			jMenuItemOptions.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {

					jMenuItemOptionsActionPerformed(evt);
				}
			});
			jMenuItemSearch.setText("Search");
			jMenuTools.add(jMenuItemSearch);
			jMenuItemSearch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jMenuItemSearchActionPerformed(evt);
				}
			});
			jMenuTools.add(jSeparator6);
			jMenuItemSAFESign.setText("Goto Signature Service ...");
			jMenuTools.add(jMenuItemSAFESign);
			jMenuItemSAFESign.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jMenuItemSAFESignActionPerformed(evt);
				}
			});
			jMenuItemSigQueue.setText("Goto My eSig Queue ...");
			jMenuTools.add(jMenuItemSigQueue);
			jMenuItemSigQueue.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jMenuItemSigQueueActionPerformed(evt);
				}
			});
			jMenuHelp.setText("Help");
			jMenuBar1.add(jMenuHelp);
			helpMenuItem.setText("Help");
//			jMenuHelp.add(helpMenuItem);
			setupHelpMenu(helpMenuItem);
//			jMenuHelp.addSeparator();
			jMenuItemHealthCheck.setText("Health Check ...");
			jMenuHelp.add(jMenuItemHealthCheck);
			jMenuItemHealthCheck.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					HealthCheckDlg healthDlg = new HealthCheckDlg(MasterController.getGUIComponent());
					Point loc = getLocation();
					Dimension dim = getSize();
					healthDlg.setLocation(loc.x + (dim.width - healthDlg.getSize().width) / 2, loc.y
							+ (dim.height - healthDlg.getSize().height) / 2);
					healthDlg.checkHealthNoLogout();
				}
			});
			jMenuHelp.addSeparator();
			jMenuItemAbout.setText("About");
			jMenuHelp.add(jMenuItemAbout);
			jMenuItemAbout.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jMenuItemAboutActionPerformed(evt);
				}
			});

			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	private void repeatExperiment() {
		JInternalFrame frame = getActiveDesktopWindow();
		if (frame instanceof NotebookPageGuiInterface) {
			NotebookPageGuiInterface guiInterface = (NotebookPageGuiInterface) frame;
			NotebookPageModel pageModel = guiInterface.getPageModel();
			if (pageModel != null && pageModel.getNbRef() != null) {
				MasterController.getGuiController().repeatExperiment(pageModel.getSiteCode(), pageModel.getUserName(), pageModel.getNbRef().getNbNumber(), pageModel.getNbRef().getNbPage(), pageModel.getVersion());
			}
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
		// Need to make window grayed when disabled
		getRootPane().setGlassPane(CeNGUIUtils.createGlassPane());
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		setIconImage(CenIconFactory.getImage(CenIconFactory.General.APPLICATION));
		
		// Set the preferred gui size if different from default
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension defaultSize = new Dimension((int) (screenSize.width * 0.85), (int) (screenSize.height * 0.85));
		guiResizer = new GuiResizingEventListener(defaultSize);
		Dimension preferredGuiSize = guiResizer.getPreferredGuiSize();
		setSize(preferredGuiSize);
		setLocation((screenSize.width - preferredGuiSize.width) / 2, (screenSize.height - preferredGuiSize.height) / 2);
		
		// setup button for menu
		jButtonNewConceptPage.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.NEW_CONCEPT_PAGE));
		jButtonNewMedChemPage.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.NEW_MEDCHEM_PAGE));
		jButtonNewParallelPage.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.NEW_PARALLEL_PAGE));
		jButtonLoadPage.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.OPEN_PAGE));
		jButtonSavePage.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.SAVE_PAGE));
		jButtonSaveAll.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.SAVE_ALL));
		jButtonPrint.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.PRINT));
		jButtonSearch.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.SEARCH));
		jButtonRefresh.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.REFRESH_PAGE));
		openFileMenuItem.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.OPEN_PAGE));
		saveMenuItem.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.SAVE_PAGE));
		repeatExperimentMenuItem.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.COPY));
		saveAllMenuItem.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.SAVE_ALL));
		printMenuItem.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.PRINT));
		undoMenuItem.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.EDIT_UNDO));
		cutMenuItem.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.EDIT_CUT));
		copyMenuItem.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.EDIT_COPY));
		pasteMenuItem.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.EDIT_PASTE));
		jMenuItemOptions.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.OPTIONS));
		jMenuItemSearch.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.SEARCH));
		helpMenuItem.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.HELP));
		
		// setup window menu
		jMenuBar1.add(new WindowMenu(), jMenuBar1.getSubElements().length - 1);

		/**
		 *  My Bookmarks Speed Bar Pane
		 */
		jTreeSpeedBarUser = new JTree() {
			
			private static final long serialVersionUID = 4752851240285177806L;

			public Point getToolTipLocation(MouseEvent e) {
				return speedBarMyBookHandler.getToolTipLocation(e);
			}

			public JToolTip createToolTip() {
				TreePath tool_tip_node = speedBarMyBookHandler.getToolTipNode();
				if (tool_tip_node != null) {
					Object usrObj = ((DefaultMutableTreeNode) tool_tip_node.getLastPathComponent()).getUserObject();
					if (usrObj instanceof SpeedBarPage) {
						SpeedBarPage page = (SpeedBarPage) usrObj;
						Image img = getPageRxnImage(page);
						if (img != null) {
							return new ImageToolTip(img);
						}
					}
				}
				return new JToolTip();
			}
		};

		speedBarMyBookHandler = new SpeedBarHandler(jTreeSpeedBarUser, SpeedBarModel.SB_MY_BOOKMARKS);
		loadSubMenusForCreateNewExp();
		JScrollPane myBookmarkScroll = new JScrollPane(jTreeSpeedBarUser);

		jTreeSpeedBarUser.setLayout(new AnchorLayout());
		jTreeSpeedBarUser.setDragEnabled(true);
		jTreeSpeedBarUser.setRootVisible(false);

		jTreeSpeedBarUser.addMouseListener(speedBarMyBookHandler);
		ToolTipManager.sharedInstance().registerComponent(jTreeSpeedBarUser);
		jTreeSpeedBarUser.setCellRenderer(speedBarMyBookHandler.getNewCellRenderer());
		myBookmarkScroll.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));

		myBookmarkScroll.setViewportView(jTreeSpeedBarUser);

		jTreeSpeedBarUser.setShowsRootHandles(true);
		jTreeSpeedBarUser.addTreeWillExpandListener(speedBarMyBookHandler);
		jTreeSpeedBarUser.setModel(speedBarMyBookHandler.getModel());

		jTreeSpeedBar = new JTree() {
			
			private static final long serialVersionUID = 3913969291696348105L;

			public Point getToolTipLocation(MouseEvent e) {
				return speedBarAllSiteHandler.getToolTipLocation(e);
			}

			public JToolTip createToolTip() {
				TreePath tool_tip_node = speedBarAllSiteHandler.getToolTipNode();
				if (tool_tip_node != null) {
					Object usrObj = ((DefaultMutableTreeNode) tool_tip_node.getLastPathComponent()).getUserObject();
					if (usrObj instanceof SpeedBarPage) {
						SpeedBarPage page = (SpeedBarPage) usrObj;
						return new ImageToolTip(getPageRxnImage(page));
					}
				}
				return new JToolTip();
			}
		};

		speedBarAllSiteHandler = new SpeedBarHandler(jTreeSpeedBar, SpeedBarModel.SB_ALL_SITES);
		JScrollPane allsitescroll = new JScrollPane(jTreeSpeedBar);
		jTreeSpeedBar.setLayout(new AnchorLayout());
		jTreeSpeedBar.setDragEnabled(true);
		jTreeSpeedBar.setRootVisible(false);
		jTreeSpeedBar.addMouseListener(speedBarAllSiteHandler);
		ToolTipManager.sharedInstance().registerComponent(jTreeSpeedBar);
		jTreeSpeedBar.setCellRenderer(speedBarAllSiteHandler.getNewCellRenderer());
		allsitescroll.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		allsitescroll.setViewportView(jTreeSpeedBar);
		jTreeSpeedBar.setShowsRootHandles(true);
		jTreeSpeedBar.addTreeWillExpandListener(speedBarAllSiteHandler);
		jTreeSpeedBar.setModel(speedBarAllSiteHandler.getModel());
		
		/**
		 * External CRO Speedbar
		 */
		croTree = new JTree() {

			private static final long serialVersionUID = -1813653576425841693L;

			public Point getToolTipLocation(MouseEvent e) {
				return croTreeHandler.getToolTipLocation(e);
			}
			
			public JToolTip createToolTip() {
				TreePath tool_tip_node = croTreeHandler.getToolTipNode();
				if (tool_tip_node != null) {
					Object usrObj = ((DefaultMutableTreeNode)tool_tip_node.getLastPathComponent()).getUserObject();

					if (usrObj instanceof CRONotebookPage) {
						CRONotebookPage page = (CRONotebookPage)usrObj;
						return new ImageToolTip(getPageRxnImage(page));
					}
				}
				return new JToolTip();
			}
		};
		croTreeHandler = new CROTreeHandler(croTree, SpeedBarModel.SB_EXTERNAL);

		croTreeScroll.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		croTreeScroll.setViewportView(croTree);
		croTree.setLayout(new AnchorLayout());
		croTree.setDragEnabled(true);
		croTree.setRootVisible(true);
		croTree.addMouseListener(croTreeHandler);
		ToolTipManager.sharedInstance().registerComponent(croTree);
		croTree.setCellRenderer(croTreeHandler.getNewCellRenderer());
		croTree.setShowsRootHandles(true);
		croTree.addTreeWillExpandListener(croTreeHandler);
		croTree.setModel(croTreeHandler.getModel());

		// setup outlook bar for tree
		OutlookBar = new JOutlookBar();
		OutlookBar.add(myBookmarkScroll);
		OutlookBar.setTitleAt(OutlookBarTypes.MY_BOOKMARKS, SpeedBarModel.SB_MY_BOOKMARKS);
		OutlookBar.add(allsitescroll);
		OutlookBar.setTitleAt(OutlookBarTypes.ALL_SITES, SpeedBarModel.SB_ALL_SITES);
		OutlookBar.add(this.croTreeScroll);
		OutlookBar.setTitleAt(OutlookBarTypes.EXTERNAL, SpeedBarModel.SB_EXTERNAL);
		jPanel3.add(OutlookBar);

		// setup statusbar
		statusBar = new StatusBar();
		progressBar = new ProgressStatusBarItem();
		progressBar.setStatus("Ready");
		statusBar.add(progressBar);
		progressBar.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evt) {
				CeNJobProgressHandler.getInstance().showProgressPanel();
			}
		});

		cancellCallback = new ProgressStatusBarItem.CancelCallback(){
		    public void cancelPerformed(){
		    	cancellableActivity.cancel();
		    }
		};
		progressBar.setCancelCallback(null);
		cancellableActivity = null;
		
		AbstractButton cancelButton = progressBar.getCancelButton();
		cancelButton.setHorizontalTextPosition(SwingConstants.CENTER);
		cancelButton.setIconTextGap(1);
		cancelButton.setText("");
		cancelButton.setBorderPainted(true);
		cancelButton.setMinimumSize(new Dimension(25, 25));
		cancelButton.setMaximumSize(new Dimension(25, 25));
		cancelButton.setSize(new Dimension(25, 25));
		cancelButton.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.DELETE));
		
		TimeStatusBarItem timeBar = new TimeStatusBarItem();
		DateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
		timeBar.setTextFormat(df);
		timeBar.setTooltipFormat(df);
		statusBar.add(timeBar);
		memoryBar = new MemoryStatusBarItem();
		statusBar.add(memoryBar);
		try {
			if (CeNSystemProperties.getRunMode().equals("Development") || CeNSystemProperties.getRunMode().equals("Test"))
				memoryBar.setVisible(true);
		} catch (Exception e) { /* ignored */ }
		this.getContentPane().add(statusBar, BorderLayout.SOUTH);
	}

	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		this.getRootPane().getGlassPane().setVisible(!b);
	}
	
	private Image getPageRxnImage(SpeedBarNodeInterface page) { 
		byte[] sketch = null;
		if (page instanceof SpeedBarPage) {
			sketch = ((SpeedBarPage) page).getNativeSketch();
		}
		if (page instanceof CRONotebookPage) {
			sketch = ((CRONotebookPage) page).getNativeSketch();
		}
		if (!ArrayUtils.isEmpty(sketch)) {
			return CeNGUIUtils.createImageFromSketch(sketch, 500, 200);
		}
		return CeNGUIUtils.getEmptyImage();
	}

	public void loadSubMenusForCreateNewExp() {
		ArrayList<ArrayList<String>> userNotebooksList = getUserNotebookList();
		jMenuNew.removeAll();
		if (userNotebooksList != null) {
			if (userNotebooksList.size() == 1)
			{
				final String notebook = userNotebooksList.get(0).get(0);
	
				JMenuItem newFileMenu, new4pFileMenu, newConceptionFileMenu;
				newFileMenu = new JMenuItem();
				new4pFileMenu = new JMenuItem();
				newConceptionFileMenu = new JMenuItem();
	
				newFileMenu.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.NEW_MEDCHEM_PAGE));
				new4pFileMenu.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.NEW_PARALLEL_PAGE));
				newConceptionFileMenu.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.NEW_CONCEPT_PAGE));
	
				newFileMenu.setText("Singleton Chemistry");
				newFileMenu.setVisible(true);
	
				new4pFileMenu.setText("Parallel Chemistry");
				new4pFileMenu.setVisible(true);
				new4pFileMenu.setEnabled(MasterController.isParallelCreateEnabled());
	
				newConceptionFileMenu.setText("Conception Record");
				newConceptionFileMenu.setVisible(true);
	
				jMenuNew.add(newFileMenu);
				jMenuNew.add(new4pFileMenu);
				jMenuNew.add(newConceptionFileMenu);
	
				newFileMenu.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						newFileMenuItemActionPerformed(notebook);
					}
				});
	
				new4pFileMenu.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						newFileMenu4PItemActionPerformed(notebook);
					}
				});
	
				newConceptionFileMenu.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						newFileMenu4ConceptionItemActionPerformed(notebook);
					}
				});
			}
			else if (userNotebooksList.size() > 1)
			{
				JMenu newFileMenu, new4pFileMenu, newConceptionFileMenu;
				newFileMenu = new JMenu();
				new4pFileMenu = new JMenu();
				newConceptionFileMenu = new JMenu();
	
				newFileMenu.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.NEW_MEDCHEM_PAGE));
				new4pFileMenu.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.NEW_PARALLEL_PAGE));
				newConceptionFileMenu.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.NEW_CONCEPT_PAGE));
	
				newFileMenu.setText("Singleton Chemistry");
				newFileMenu.setVisible(true);
				new4pFileMenu.setText("Parallel Chemistry");
				new4pFileMenu.setVisible(true);
				new4pFileMenu.setEnabled(MasterController.isParallelCreateEnabled());
				newConceptionFileMenu.setText("Conception Record");
				newConceptionFileMenu.setVisible(true);
	
				for (int i=0; i<userNotebooksList.size(); i++)
				{
					String notebook = userNotebooksList.get(i).get(0);
					JMenuItem menuItem = new JMenuItem(notebook);
					newFileMenu.add(menuItem);
					menuItem.setActionCommand(notebook);
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							newFileMenuItemActionPerformed(evt.getActionCommand());
						}
					});
				}
				for (int i=0; i<userNotebooksList.size(); i++)
				{
					String notebook = userNotebooksList.get(i).get(0);
					JMenuItem menuItem = new JMenuItem(notebook);
					new4pFileMenu.add(menuItem);
					menuItem.setActionCommand(notebook);
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							newFileMenu4PItemActionPerformed(evt.getActionCommand());
						}
					});
				}
				for (int i=0; i<userNotebooksList.size(); i++)
				{
					String notebook = userNotebooksList.get(i).get(0);
					JMenuItem menuItem = new JMenuItem(notebook);
					newConceptionFileMenu.add(menuItem);
					menuItem.setActionCommand(notebook);
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							newFileMenu4ConceptionItemActionPerformed(evt.getActionCommand());
						}
					});
				}
				jMenuNew.add(newFileMenu);
				jMenuNew.add(new4pFileMenu);
				jMenuNew.add(newConceptionFileMenu);
			}
		}
	}
	
	public ArrayList<ArrayList<String>> getUserNotebookList() {
		final NotebookUser loginUser = MasterController.getUser();
		return speedBarMyBookHandler.getModel().getNotebooksForUser(loginUser.getSiteCode(), loginUser.getNTUserID());
	}

	/** Auto-generated event handler method */
	protected void GuiWindowOpened(WindowEvent evt) {
		// Activate the default speedbar
		final NotebookUser user = MasterController.getUser();
		String sPref;
		try {
			sPref = user.getPreference(NotebookUser.PREF_SB_OUTLOOK_BAR);
			if (sPref.toUpperCase().equals(NotebookUser.MY_BOOKMARKS))
				OutlookBar.setSelectedIndex(OutlookBarTypes.MY_BOOKMARKS);
			else if (sPref.toUpperCase().equals(NotebookUser.ALL_SITES))
				OutlookBar.setSelectedIndex(OutlookBarTypes.ALL_SITES);
			else
				OutlookBar.setSelectedIndex(OutlookBarTypes.MY_BOOKMARKS); // until user prefs work default to my bookmarks
		} catch (UserPreferenceException e1) {
			ceh.logExceptionMsg(null, e1);
		}
		// Determine if the last experiment should be opened
		boolean flag = false;
		try {
			sPref = user.getPreference(NotebookUser.PREF_DEFAULT_START);
			if (sPref.toUpperCase().equals(NotebookUser.LAST_EXPERIMENT))
				flag = true;
		} catch (UserPreferenceException e1) {
			ceh.logExceptionMsg(null, e1);
		}
		speedBarMyBookHandler.getModel().expandRoot();
		speedBarAllSiteHandler.getModel().expandRoot();
		this.croTreeHandler.getModel().expandRoot();
		refreshIcons();
		final boolean openFlag = flag;

		//SwingUtilities.invokeLater(new Runnable() {
		//	public void run() {
		SwingWorker worker = new com.chemistry.enotebook.utils.SwingWorker() {
			public Object construct() {
				if (log.isInfoEnabled()) {
					log.info("guiWindowOpened SwingWorker starting...");
				}
				// Navigate speedbar to the last experiment (if any)
				try {
					String sLastExperiment = user.getPreference(NotebookUser.PREF_CurrentNbRef);
					if (sLastExperiment != null && sLastExperiment.length() > 0) {
						// final int groupSize =
						// NotebookPageUtil.NB_PAGE_GROUP_SIZE;
						NotebookRef nbref = new NotebookRef(sLastExperiment);
						String nb = nbref.getNbNumber();
						String ex = nbref.getNbPage();
						String group = SpeedBarPageGroup.getGroupFromExperiment(ex);
						int lastVer = new Integer("0" + user.getPreference(NotebookUser.PREF_CurrentNbRefVer)).intValue();
						if (lastVer > 1)
							ex += " v" + lastVer;
						if (OutlookBar.getSelectedIndex() == OutlookBarTypes.MY_BOOKMARKS) {
							long startTime = System.currentTimeMillis();
							String[] expandPathBookmarks = { user.getFullName(), nb, group, ex };
							speedBarMyBookHandler.expandPath(expandPathBookmarks, openFlag);
							if (log.isInfoEnabled()) {
								log.info("My Bookmarks expandPath took " + (System.currentTimeMillis() - startTime) + " ms");
							}
						} else if (OutlookBar.getSelectedIndex() == OutlookBarTypes.ALL_SITES) {
							String siteName = CodeTableCache.getCache().getSiteDescription(user.getSiteCode());
							String[] expandPathSites = { siteName, user.getFullName(), nb, group, ex };
							speedBarAllSiteHandler.expandPath(expandPathSites, openFlag);
						}
					}
				} catch (UserPreferenceException e1) {
					ceh.logExceptionMsg(null, e1);
				} catch (Exception e) { // Error occured, just expand the roots
					ceh.logExceptionMsg(null, e);
				}
				//refreshIcons();
				if (log.isInfoEnabled()) {
					log.info("guiWindowOpened SwingWorker finished");
				}
				return null;
			}
			public void finished() {
				refreshIcons();
			}
		};
		worker.start();
		//});
		MasterController.getGuiController().startAutoSaveTimer();

		// Start the Timer that will monitor whether there are any Signature Changes.
		SignatureTimerHandler.startSignatureTimer(false);

		checkExperimentCompliance();
	}

	/** Auto-generated event handler method */
	protected void GuiWindowClosing(WindowEvent evt) {
		if (!_shuttingDown) { 
			_shuttingDown = true;
			
			final Gui thisInstance = this;
			final String progressStatus = "Exiting application...";
			CeNJobProgressHandler.getInstance().addItem(progressStatus);
						
			Thread thread = new Thread(new Runnable() {
				public void run() {
					JInternalFrame[] pages = getDesktopWindows();
					if (pages != null && pages.length > 0) {
						if (saveMultiplePages(pages)) {
							// set the preferred gui size if different from default 
							guiResizer.setUserPreferredGuiSize(thisInstance); 
							
							
							MasterController.getInstance().dispose(); // no experiments open, close app 
							System.exit(0);
						} else {
							_shuttingDown = false;
							CeNJobProgressHandler.getInstance().removeItem(progressStatus);
						}
					} else {
						// set the preferred gui size if different from default 
						guiResizer.setUserPreferredGuiSize(thisInstance); 
						
						
						MasterController.getInstance().dispose(); // no experiments open, close app 
						System.exit(0);
					}
				}				
			});
			
			thread.start();
		}
	}

	private boolean saveMultiplePages(JInternalFrame[] nbPages) {
		int unsavedPagesCount = 0;
		boolean readyToExit = false;
		
		List<JInternalFrame> unsavedPages = new ArrayList<JInternalFrame>();
		List<String> nbRefNumbers = new ArrayList<String>();
				
		for (JInternalFrame frame : nbPages) {
			if (frame instanceof NotebookPageGuiInterface) {
				NotebookPageGuiInterface pageGUI = (NotebookPageGuiInterface) frame;				
				NotebookPageModel pageModel = null;
		
				if (pageGUI.getPageModel() instanceof NotebookPageModel) {
					pageModel = pageGUI.getPageModel();
				}
				
				if (pageModel != null && pageModel.isModelChanged()) {
					unsavedPagesCount++;
					unsavedPages.add(frame);
					nbRefNumbers.add(pageModel.getNotebookRefAsString());
				}
			}
		}
		
		if (unsavedPagesCount > 0) {
			Object[] options = { "Save Selected", "Discard All", "Cancel" };
			PageListView pagesView = new PageListView(PageListView.VERTICAL_SCROLLBAR_AS_NEEDED, PageListView.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			pagesView.populateList(nbRefNumbers);
						
			int selection = JOptionPane.showOptionDialog(this, pagesView, "Select experiments to save:", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			
			if (selection == JOptionPane.CANCEL_OPTION) {
				readyToExit = false;
			} else {
				readyToExit = true;
				if (selection == JOptionPane.YES_OPTION) {
					List<String> selectedPagesToSave = pagesView.getSelectedPages();
					for (JInternalFrame frame : unsavedPages) {
						if (frame instanceof NotebookPageGuiInterface) {
							NotebookPageGuiInterface pageGUI = (NotebookPageGuiInterface) frame;
							if (selectedPagesToSave.contains(pageGUI.getPageModel().getNotebookRefAsString())) {
								boolean status = MasterController.getGuiController().saveExperiment(pageGUI.getPageModel());
								refreshIcons();
								if (status) {
									MasterController.getGuiController().removeNotebookPageFromGUI(pageGUI);
								} else {
									readyToExit = false;
								}
							} else {
								MasterController.getGuiController().removeNotebookPageFromGUI(pageGUI);
							}
						}
					}
				} else { // User does not want to save any pages, so remove Page GUIs
					for (JInternalFrame frame : nbPages) {
						if (frame instanceof NotebookPageGuiInterface) {
							MasterController.getGuiController().removeNotebookPageFromGUI((NotebookPageGuiInterface) frame);
						}
					}
				}
			}
		} else {
			readyToExit = true;
		}
		
		return readyToExit;
	}

	private void setupHelpMenu(JMenu menu) {
		jMenuSigs.setText("Electronic Signatures");
		try {
			jMenuItemWebTrain.setText("Web-based Training (How To)");
			final String prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_HELP_WEB_TRAIN);
			if (prop != null && prop.length() > 0) {
				jMenuItemWebTrain.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						try {
							Process p = Runtime.getRuntime().exec("cmd /c start " + prop);
						} catch (Exception e) {
							log.error("Failed to spin off process to show web training help.", e);
						}
					}
				});
			} else
				jMenuItemWebTrain.setEnabled(false);
		} catch (Exception e) {
			jMenuItemWebTrain.setEnabled(false);
		}
		menu.add(jMenuItemWebTrain);
		try {
			jMenuItemQuickStart.setText("Quick Start");
			final String prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_HELP_QUICK_START);
			if (prop != null && prop.length() > 0) {
				jMenuItemQuickStart.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						try {
							Process p = Runtime.getRuntime().exec("cmd /c start " + prop);
						} catch (Exception e) {
							log.error("Failed to spin off process to show quick start help.", e);
						}
					}
				});
			} else
				jMenuItemQuickStart.setEnabled(false);
		} catch (Exception e) {
			jMenuItemQuickStart.setEnabled(false);
		}
		menu.add(jMenuItemQuickStart);
		try {
			jMenuItemQuickRef.setText("Quick Reference (graphics)");
			final String prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_HELP_QUICK_REF);
			if (prop != null && prop.length() > 0) {
				jMenuItemQuickRef.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						try {
							Process p = Runtime.getRuntime().exec("cmd /c start " + prop);
						} catch (Exception e) {
							log.error("Failed to spin off process to show quick reference help.", e);
						}
					}
				});
			} else
				jMenuItemQuickRef.setEnabled(false);
		} catch (Exception e) {
			jMenuItemQuickRef.setEnabled(false);
		}
		menu.add(jMenuItemQuickRef);
		try {
			jMenuItemQnA.setText("Questions and Answers");
			final String prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_HELP_Q_A);
			if (prop != null && prop.length() > 0) {
				jMenuItemQnA.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						try {
							Process p = Runtime.getRuntime().exec("cmd /c start " + prop);
						} catch (Exception e) {
							log.error("Failed to spin off process to show FAQ help.", e);
						}
					}
				});
			} else
				jMenuItemQnA.setEnabled(false);
		} catch (Exception e) {
			jMenuItemQnA.setEnabled(false);
		}
		menu.add(jMenuItemQnA);
		try {
			jMenuItemTestScripts.setText("Test Scripts");
			final String prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_HELP_TEST_SCRIPTS);
			if (prop != null && prop.length() > 0) {
				jMenuItemTestScripts.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						try {
							Process p = Runtime.getRuntime().exec("cmd /c start " + prop);
						} catch (Exception e) {
							log.error("Failed to spin off process to show Test Scripts help.", e);
						}
					}
				});
			} else
				jMenuItemTestScripts.setEnabled(false);
		} catch (Exception e) {
			jMenuItemTestScripts.setEnabled(false);
		}
		menu.add(jMenuItemTestScripts);
		try {
			jMenuItemBusinessRules.setText("Business Rules for Structure Drawing");
			final String prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_HELP_BUSINESS_RULES);
			if (prop != null && prop.length() > 0) {
				jMenuItemBusinessRules.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						try {
							Process p = Runtime.getRuntime().exec("cmd /c start " + prop);
						} catch (Exception e) {
							log.error("Failed to spin off process to show Business Rules help.", e);
						}
					}
				});
			} else
				jMenuItemBusinessRules.setEnabled(false);
		} catch (Exception e) {
			jMenuItemBusinessRules.setEnabled(false);
		}
		menu.add(jMenuItemBusinessRules);

//		try {
//			jMenuItemTlc.setText("TLC Drawing Template");
//	    	final String prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_HELP_CEN_TLC);
//	    	if (prop != null && prop.length() > 0) {
//	    		jMenuItemTlc.addActionListener( new ActionListener() {
//					public void actionPerformed(ActionEvent evt) {
//						try {
//							Process p = Runtime.getRuntime().exec("cmd /c start " + prop);
//						} catch (Exception e) {
//							log.error("Failed to spin off process to show TLC help.", e);
//						}
//					}
//				});
//	    	} else
//	    		jMenuItemTlc.setEnabled(false);
//		} catch (Exception e) {
//			jMenuItemTlc.setEnabled(false);
//		}		
//		menu.add(jMenuItemTlc);

		try {
			jMenuItemOffline.setText(CeNConstants.PROGRAM_NAME + " Off-Line Template");
	    	final String prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_HELP_CEN_OFFLINE);
	    	if (prop != null && prop.length() > 0) {
	    		jMenuItemOffline.addActionListener( new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						try {
							Process p = Runtime.getRuntime().exec("cmd /c start " + prop);
						} catch (Exception e) {
							log.error("Failed to spin off process to show Off-line Training help.", e);
						}
					}
				});
	    	} else
	    		jMenuItemOffline.setEnabled(false);
		} catch (Exception e) {
			jMenuItemOffline.setEnabled(false);
		}		
		menu.add(jMenuItemOffline);
		
//		try {
//			jMenuItemInstruct.setText("Certs. Workflow Website");
//	    	final String prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_HELP_ESIG_WEBSITE);
//	    	if (prop != null && prop.length() > 0) {
//	    		jMenuItemInstruct.addActionListener( new ActionListener() {
//					public void actionPerformed(ActionEvent evt) {
//						try {
//							Process p = Runtime.getRuntime().exec("cmd /c start " + prop);
//						} catch (Exception e) {
//							log.error("Failed to spin off process to show eSignature help.", e);
//						}
//					}
//				});
//	    	} else
//	    		jMenuItemInstruct.setEnabled(false);
//		} catch (Exception e) {
//			jMenuItemInstruct.setEnabled(false);
//		}
//		jMenuSigs.add(jMenuItemInstruct);
//
//		try {
//			jMenuItemCertsInstruct.setText("Certs. Workflow Website");
//	    	final String prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_HELP_ESIG_WEBSITE);
//	    	if (prop != null && prop.length() > 0) {
//	    		jMenuItemCertsInstruct.addActionListener( new ActionListener() {
//					public void actionPerformed(ActionEvent evt) {
//						try {
//							Process p = Runtime.getRuntime().exec("cmd /c start " + prop);
//						} catch (Exception e) {
//							log.error("Failed to spin off process to show eSignature help.", e);
//						}
//					}
//				});
//	    	} else {
//	    		jMenuItemCertsInstruct.setEnabled(false);
//	    	}
//		} catch (Exception e) {
//			jMenuItemCertsInstruct.setEnabled(false);
//		}
//		jMenuSigs.add(jMenuItemCertsInstruct);
//
//		try {
//			jMenuItemCertsInstruct.setText("SAFE Certs. Training");
//			final String prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_HELP_CERTS);
//			if (prop != null && prop.length() > 0) {
//				jMenuItemCertsInstruct.addActionListener(new ActionListener() {
//					public void actionPerformed(ActionEvent evt) {
//						try {
//							Process p = Runtime.getRuntime().exec("cmd /c start " + prop);
//						} catch (Exception e) {
//							log.error("Failed to spin off process to show certificate help.", e);
//						}
//					}
//				});
//			} else {
//				jMenuItemCertsInstruct.setEnabled(false);
//			}
//		} catch (Exception e) {
//			jMenuItemCertsInstruct.setEnabled(false);
//		}
//		jMenuSigs.add(jMenuItemCertsInstruct);
//		
//		try {
//			jMenuItemUssiInstruct.setText("Signature Service Training");
//	    	final String prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_HELP_SAFE);
//	    	if (prop != null && prop.length() > 0) {
//	    		jMenuItemUssiInstruct.addActionListener( new ActionListener() {
//					public void actionPerformed(ActionEvent evt) {
//						try {
//							Process p = Runtime.getRuntime().exec("cmd /c start " + prop);
//						} catch (Exception e) {
//							log.error("Failed to spin off process to show SAFE Cert help.", e);
//						}
//					}
//				});
//			} else {
//				jMenuItemUssiInstruct.setEnabled(false);
//			}
//		} catch (Exception e) {
//			jMenuItemUssiInstruct.setEnabled(false);
//		}
//		jMenuSigs.add(jMenuItemUssiInstruct);
//		
//		try {
//			jMenuItemSigInstruct.setText("CeN eSignature Training");
//			final String prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_HELP_CEN_ESIG);
//			if (prop != null && prop.length() > 0) {
//				jMenuItemSigInstruct.addActionListener(new ActionListener() {
//					public void actionPerformed(ActionEvent evt) {
//						try {
//							Process p = Runtime.getRuntime().exec("cmd /c start " + prop);
//						} catch (Exception e) {
//							log.error("Failed to spin off process to show eSignature help.", e);
//						}
//					}
//				});
//			} else {
//				jMenuItemSigInstruct.setEnabled(false);
//			}
//		} catch (Exception e) {
//			jMenuItemSigInstruct.setEnabled(false);
//		}
//		jMenuSigs.add(jMenuItemSigInstruct);
//		menu.add(jMenuSigs);
	}

	/** Auto-generated event handler method */
	protected void newFileMenuItemActionPerformed(String notebookRef) {
		MasterController.getGuiController().createNewSingletonExperimentExecutor(notebookRef);
	}

	protected void newFileMenu4PItemActionPerformed(String notebookRef) {
		MasterController.getGuiController().createNewExperiment4PCeNNoSPID(notebookRef);
	}

	protected void newFileMenu4ConceptionItemActionPerformed(String notebookRef) {
		MasterController.getGuiController().createNewConceptionExecutor(notebookRef);
	}

	/** Auto-generated event handler method */
	protected void openFileMenuItemActionPerformed(ActionEvent evt) {
		MasterController.getGuiController().openExperiment();
	}

	/** Auto-generated event handler method */
	// vb todo fix this for singleton page too
	protected void closeFileMenuItemActionPerformed(ActionEvent evt) {
		if (getActiveDesktopWindow() instanceof ParallelNotebookPageGUI) {  // vb 3/6 was NotebookPageGUI
			ParallelNotebookPageGUI page = (ParallelNotebookPageGUI) getActiveDesktopWindow();

			NotebookPageModel pageModel = (NotebookPageModel) page.getPageModel(); //page.getNotebookPage();
			if (page != null) {
				if (!pageModel.isModelChanged())
					MasterController.getGuiController().removeNotebookPageFromGUI(page); // experiment
				// not modified,
				// close it
				else { // experiment modified,
					if (!MasterController.getGUIComponent().checkPendingChangesInPage())
						return;
					if (page.shouldClose())
						MasterController.getGuiController().removeNotebookPageFromGUI(page);
				}
			}
		} else if (getActiveDesktopWindow() instanceof SingletonNotebookPageGUI) {
			SingletonNotebookPageGUI page = (SingletonNotebookPageGUI) getActiveDesktopWindow();
			NotebookPageModel pageModel = (NotebookPageModel) page.getPageModel(); //page.getNotebookPage();
			if (page != null) {
				if (!pageModel.isModelChanged())
					MasterController.getGuiController().removeNotebookPageFromGUI(page); // experiment
				// not modified,
				// close it
				else { // experiment modified,
					if (!MasterController.getGUIComponent().checkPendingChangesInPage())
						return;
					if (page.shouldClose())
						MasterController.getGuiController().removeNotebookPageFromGUI(page);
				}
			}
		}
			//MasterController.getGuiController().removeNotebookContentsFromGUI((NotebookContentsGUI) getActiveDesktopWindow());
	}

	/** Auto-generated event handler method */
	protected void closeAllMenuItemActionPerformed(ActionEvent evt) {
		SwingWorker worker = new SwingWorker() {
			public Object construct() {
				return null;
			}

			public void finished() {
				JInternalFrame[] pages = getDesktopWindows();
				if (pages != null && pages.length > 0) {
					if (saveMultiplePages(pages)) {
						for (int i = 0; i < pages.length; i++) {
							if (pages[i] instanceof NotebookPageGuiInterface) {
								NotebookPageGuiInterface page = (NotebookPageGuiInterface) pages[i];
								if (!MasterController.getGUIComponent().checkPendingChangesInPage())
									continue;
								MasterController.getGuiController().removeNotebookPageFromGUI(page);
/*							if (pages.length > i + 1)
							{
								JInternalFrame frame = pages[i + 1];
								frame.moveToFront();
								try {
									frame.setSelected(true);
									frame.setMaximum(true);
								} catch (PropertyVetoException e) {
									CeNErrorHandler.getInstance().logExceptionMsg(null, e);
								}
							}
*/							}
						}
					}
				}
			}
		};
		worker.start();
	}

	/** Auto-generated event handler method */
	protected void saveMenuItemActionPerformed(ActionEvent evt) {
		jButtonSavePageActionPerformed(null);
	}

	/** Auto-generated event handler method */
	/*
	protected void saveAsMenuItemActionPerformed(ActionEvent evt) {
		SpeedBarNodeInterface sbi = getLastSelectedSpeedBarObject();
		if (sbi != null) {
			if (sbi instanceof SpeedBarPage) { // Open an Experiment
				SpeedBarPage sbp = (SpeedBarPage) sbi;
				MasterController.getGuiController().repeatExperiment(sbp.getSiteCode(), sbp.getUserID(), sbp.getNotebook(),
						sbp.getPage(), sbp.getVersion());
			} else {
				// All Others should be ignored
			}
		}
	}
	*/

	/** Auto-generated event handler method */
	/*
	protected void saveAllMenuItemActionPerformed(ActionEvent evt) {
		MasterController.getGuiController().saveAll();
	}
	*/

	/** Auto-generated event handler method */
	public void printMenuItemActionPerformed(ActionEvent evt) {
		Object frame = getActiveDesktopWindow();
		if (frame instanceof NotebookContentsGUI) {
			NotebookContentsTableModel model = ((NotebookContentsGUI) frame).getModel();
			MasterController.getGuiController().printContents(model.getSiteCode(), model.getNotebook(), model.getStartExperiment(),
					model.getStopExperiment());
			
		} else if (frame instanceof NotebookPageGuiInterface) {
			NotebookPageModel model = ((NotebookPageGuiInterface) frame).getPageModel();
			if (model != null) {
				ArrayList<PrintRequest> list = new ArrayList<PrintRequest>();
				NotebookRef nbRef = model.getNbRef();
				list.add(new PrintRequest( model.getSiteCode(), nbRef, new Integer(model.getVersion()), model.getPageType() ));
				MasterController.getGuiController().printExperiments(list);
			}
		} else {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Please open an experiment to print.", "Print Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/** Auto-generated event handler method */
	protected void exitMenuItemActionPerformed(ActionEvent evt) {
		GuiWindowClosing(null);
	}

	/** Auto-generated event handler method */
	protected void jMenuEditMenuSelected(MenuEvent evt) {
		// Clipboard clipboard =
		// Toolkit.getDefaultToolkit().getSystemClipboard();
		// Transferable contents = clipboard.getContents(null);
		undoMenuItem.setEnabled(false);
		// Component compFocusOwner =
		// KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		// if (compFocusOwner instanceof JTextField) {
		// boolean hasText =
		// ((JTextField)compFocusOwner).getSelectedText().length() > 0;
		// cutMenuItem.setEnabled(hasText);
		// copyMenuItem.setEnabled(hasText);
		// pasteMenuItem.setEnabled(contents != null &&
		// contents.isDataFlavorSupported(DataFlavor.stringFlavor));
		// } else if (compFocusOwner instanceof JTextArea) {
		// boolean hasText =
		// ((JTextArea)compFocusOwner).getSelectedText().length() > 0;
		// cutMenuItem.setEnabled(hasText);
		// copyMenuItem.setEnabled(hasText);
		// pasteMenuItem.setEnabled(contents != null);
		// } else if (compFocusOwner instanceof ELJBean) {
		// //
		// cutMenuItem.setEnabled(((ELJBean)compFocusOwner).getToolkit().get.getgetSelectedText().length()
		// > 0);
		// //
		// copyMenuItem.setEnabled(((ELJBean)compFocusOwner).getSelectedText().length()
		// > 0);
		// pasteMenuItem.setEnabled(contents != null);
		// } else {
		cutMenuItem.setEnabled(false);
		copyMenuItem.setEnabled(false);
		pasteMenuItem.setEnabled(false);
		// }
		cutMenuItem.setEnabled(true);
		copyMenuItem.setEnabled(true);
		pasteMenuItem.setEnabled(true);
	}

	/** Auto-generated event handler method */
	protected void undoMenuItemActionPerformed(ActionEvent evt) {
		// TODO add your handler code here
	}

	/** Auto-generated event handler method */
	protected void cutMenuItemActionPerformed(ActionEvent evt) {
		// TODO:
		// EventQueue q = this.getToolkit().getSystemEventQueue();
		// long time = System.currentTimeMillis();
		//
		// Component compFocusOwner =
		// KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		// KeyEvent E1 = new KeyEvent (compFocusOwner, KeyEvent.KEY_PRESSED,
		// time - 7, Event.ALT_MASK, KeyEvent.VK_CUT);
		// KeyEvent E2 = new KeyEvent (compFocusOwner, KeyEvent.KEY_RELEASED,
		// time - 6, Event.ALT_MASK, KeyEvent.VK_CUT);
		// KeyEvent E3 = new KeyEvent (compFocusOwner, KeyEvent.KEY_TYPED, time
		// - 5, Event.ALT_MASK, KeyEvent.VK_CUT);
		//
		// q.postEvent(E1);
		// q.postEvent(E2);
		// q.postEvent(E3);
	}

	/** Auto-generated event handler method */
	protected void copyMenuItemActionPerformed(ActionEvent evt) {
		// TODO:
	}

	/** Auto-generated event handler method */
	protected void pasteMenuItemActionPerformed(ActionEvent evt) {
		// TODO:
	}

	/** Auto-generated event handler method */
	protected void jMenuItemOptionsActionPerformed(ActionEvent evt) {
		JDialogPrefs prefDialog = new JDialogPrefs(MasterController.getGuiController().getGUIComponent());
		Point loc = this.getLocation();
		Dimension dim = this.getSize();
		prefDialog.setLocation(loc.x + (dim.width - prefDialog.getSize().width) / 2, loc.y + (dim.height - prefDialog.getSize().height)
				/ 2);
		prefDialog.setVisible(true);
	}

	/** Auto-generated event handler method */
	protected void jMenuItemSearchActionPerformed(ActionEvent evt) {
		Parallel_Query_SearchContainer search = Parallel_Query_SearchContainer.getInstance(null);
		search.setVisible(true);
	}

	/** Auto-generated event handler method */
	protected void jMenuItemSAFESignActionPerformed(ActionEvent evt) {
		new SignatureHandler().launchSAFESign();
	}

	/** Auto-generated event handler method */
	protected void jMenuItemSigQueueActionPerformed(ActionEvent evt) {
		new SignatureHandler().launchUssiSignatureQueue();
	}

	/** Auto-generated event handler method */
	protected void jMenuItemAboutActionPerformed(ActionEvent evt) {
		// show about dialog
		AboutDialog aboutDia = new AboutDialog(MasterController.getGUIComponent());
		aboutDia.setLocationRelativeTo(MasterController.getGUIComponent());
		aboutDia.setVisible(true);
	}

	/** Auto-generated event handler method */
	protected void jButtonNewPageActionPerformed(ActionEvent evt) {
		MasterController.getGuiController().createNewSingletonExperiment();
	}

	protected void jButtonNewParallelPageActionPerformed(ActionEvent evt) {
		final NotebookUser loginUser = MasterController.getUser();
		MasterController.getGuiController().createNewExperiment4PCeNNoSPID(getNotebookIfOnlyOneExists());
	}

	protected void jButtonNewPageConceptionActionPerformed(ActionEvent evt) {
		// TODO: Should this be based on speed bar node open or last notebook
		// page created in?
		MasterController.getGuiController().createNewConception();
	}

	/** Auto-generated event handler method */
	protected void jButtonLoadPageActionPerformed(ActionEvent evt) {
		openFileMenuItemActionPerformed(null);
	}

	/** Auto-generated event handler method */
	public boolean jButtonSavePageActionPerformed(ActionEvent evt) {
		/*
		 * NotebookPageGUI activePage = (NotebookPageGUI) getActiveDesktopWindow(); NotebookPage pageModel = (NotebookPage)
		 * activePage.getNotebookPage(); if (activePage != null && activePage.getPageModel() != null)
		 * MasterController.getGuiController().saveExperiment(pageModel);
		 */
		if (evt == null)//Synchronous
		{
			setCursor(Cursor.WAIT_CURSOR);
			jButtonSavePage.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.SAVE_PAGE_BACKGROUND));
			NotebookPageGuiInterface mNotebookPageGuiInterface = (NotebookPageGuiInterface) getActiveDesktopWindow();
			NotebookPageModel pageModel = mNotebookPageGuiInterface.getPageModel();
			boolean result = MasterController.getGuiController().saveExperiment(pageModel);
			refreshIcons();
			if(CeNConstants.PAGE_STATUS_SUBMIT_FAILED_REOPEN.equals(pageModel.getPageStatus())) {
				refreshCurrentSpeedBarElement(pageModel);
			}
			jButtonSavePage.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.SAVE_PAGE));
			setCursor(Cursor.DEFAULT_CURSOR);
			return result;
		}
		else		//ASynchronous
		{

			setCursor(Cursor.WAIT_CURSOR);
			final ProgressBarDialog progressBarDialog = new ProgressBarDialog(MasterController.getGUIComponent());
			progressBarDialog.setTitle("Saving notebook page, Please Wait ...");

			jButtonSavePage.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.SAVE_PAGE_BACKGROUND));
			SwingWorker workflowWorker = new SwingWorker() {
				public Object construct() {
					try {
						NotebookPageGuiInterface mNotebookPageGuiInterface = (NotebookPageGuiInterface) getActiveDesktopWindow();
						NotebookPageModel pageModel = mNotebookPageGuiInterface.getPageModel();
						MasterController.getGuiController().saveExperiment(pageModel);
						
						if(CeNConstants.PAGE_STATUS_SUBMIT_FAILED_REOPEN.equals(pageModel.getPageStatus())) {
							refreshCurrentSpeedBarElement(pageModel);
						}
					} catch (RuntimeException e) {
						CeNErrorHandler.getInstance().logExceptionMsg(null, e);
					}
					return null;
				}
				public void finished() {
					progressBarDialog.setVisible(false);
					refreshIcons();
					jButtonSavePage.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.SAVE_PAGE));
					setCursor(Cursor.DEFAULT_CURSOR);
				}
			};
			workflowWorker.start();
			progressBarDialog.setModal(true);
			progressBarDialog.setVisible(true);
		}
		return isSavePageButtonEnabled();
	}
	
	private void refreshCurrentSpeedBarElement(NotebookPageModel pageModel) {
		refreshIcons();
		Gui GUI = MasterController.getGUIComponent();
		String site = "";
		NotebookRef nbRef = pageModel.getNbRef();

		try {
			site = CodeTableCache.getCache().getSiteDescription(pageModel.getSiteCode());
		} catch (Exception e) { }
		String user = MasterController.getUser().getFullName();
		GUI.refreshIcons();
		SpeedBarNodeInterface sbi = null;
		sbi = GUI.getMySpeedBar().speedBarNavigateTo(site, user, nbRef, pageModel.getNbRef().getVersion());
		if (sbi != null && sbi instanceof SpeedBarPage)
			((SpeedBarPage) sbi).setPageStatus(pageModel.getPageStatus());
		GUI.getMySpeedBar().refreshCurrentNode();
		
		sbi = GUI.getAllSitesSpeedBar().speedBarNavigateTo(site, user, nbRef, pageModel.getNbRef().getVersion());
		if (sbi != null && sbi instanceof SpeedBarPage)
			((SpeedBarPage) sbi).setPageStatus(pageModel.getPageStatus());
		GUI.getAllSitesSpeedBar().refreshCurrentNode();
	}

	/** Auto-generated event handler method */
	public boolean jButtonSaveAllActionPerformed(ActionEvent evt) {
		/*
		 * JInternalFrame[] pages = getDesktopWindows(); for (int i = 0; i < pages.length; i++) { NotebookPage nbPage =
		 * (NotebookPage) ((NotebookPageGUI) pages[i]).getNotebookPage(); if (nbPage != null && nbPage.isPageEditable() &&
		 * nbPage.isModelChanged()) MasterController.getGuiController().saveExperiment(nbPage); }
		 */
		if (evt == null) // when exp close (X) action performed Synchronous
		{
			setCursor(Cursor.WAIT_CURSOR);
			jButtonSavePage.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.SAVE_PAGE_BACKGROUND));
			JInternalFrame[] pages = getDesktopWindows();
			NotebookPageModel[] pageModelArr = new NotebookPageModel[pages.length];
			for (int i = 0; i < pages.length; i++) {
				if (pages[i] instanceof NotebookPageGuiInterface) {
					NotebookPageModel pageModel = (NotebookPageModel)((NotebookPageGuiInterface) pages[i]).getPageModel();
					if (pageModel != null && pageModel.isModelChanged())
						pageModelArr[i] = pageModel;
				}
			}
			boolean result = MasterController.getGuiController().saveExperiment(pageModelArr);
			refreshIcons();
			jButtonSavePage.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.SAVE_PAGE));
			setCursor(Cursor.DEFAULT_CURSOR);
			return result;
		}
		else	//ASynchronous
		{
			setCursor(Cursor.WAIT_CURSOR);
			jButtonSavePage.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.SAVE_PAGE_BACKGROUND));
			final ProgressBarDialog progressBarDialog = new ProgressBarDialog(MasterController.getGUIComponent());
			progressBarDialog.setTitle("Saving notebook pages, Please Wait ...");
			SwingWorker workflowWorker = new SwingWorker() {
				public Object construct() {
					try {
						JInternalFrame[] pages = getDesktopWindows();
						NotebookPageModel[] pageModelArr = new NotebookPageModel[pages.length];
						for (int i = 0; i < pages.length; i++) {
							if (pages[i] instanceof NotebookPageGuiInterface) {
								NotebookPageModel pageModel = (NotebookPageModel)((NotebookPageGuiInterface) pages[i]).getPageModel();
								if (pageModel != null && pageModel.isModelChanged())
									pageModelArr[i] = pageModel;
							}
						}

						MasterController.getGuiController().saveExperiment(pageModelArr);
					} catch (RuntimeException e) {
						CeNErrorHandler.getInstance().logExceptionMsg(null, e);
					}
					return null;
				}
				public void finished() {
					progressBarDialog.setVisible(false);
					refreshIcons();
					jButtonSavePage.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.SAVE_PAGE));
					setCursor(Cursor.DEFAULT_CURSOR);
				}
			};
			workflowWorker.start();
			progressBarDialog.setModal(true);
			progressBarDialog.setVisible(true);
			return isSaveAllButtonEnabled();
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonPrintActionPerformed(ActionEvent evt) {
		printMenuItemActionPerformed(null);
	}

	/** Auto-generated event handler method */
	protected void jButtonSearchActionPerformed(ActionEvent evt) {
		jMenuItemSearchActionPerformed(null);
	}

	/** Auto-generated event handler method */
	protected void jButtonRefreshActionPerformed(ActionEvent evt) {
		final NotebookUser loginUser = MasterController.getUser();
		speedBarMyBookHandler.getModel().refreshNotebooksForUser(loginUser.getSiteCode(), loginUser.getNTUserID());
		
		if (getActiveSpeedBarHandler() != null)
			getActiveSpeedBarHandler().refresh();
		loadSubMenusForCreateNewExp();
		SignatureTimerHandler.startSignatureTimer(true);
	}

	/** Auto-generated event handler method */
	protected void jTabbedPaneMainStateChanged(ChangeEvent evt) {
		JTabbedPane pane = (JTabbedPane) evt.getSource();
		// Get current tab
		int sel = pane.getSelectedIndex();
		for (int c = 0; c <= (pane.getTabCount() - 1); c++) {
			pane.setBackgroundAt(c, Color.lightGray);
		}
		pane.setBackgroundAt(sel, Color.WHITE);
	}

	public class WindowMenu extends JMenu {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2985281137892456324L;
		private JDesktopPane desktop;
		private JMenuItem tile = new JMenuItem("Tile");
		private JMenuItem cascade = new JMenuItem("Cascade");
		private JMenuItem arrange = new JMenuItem("Arrange Icons");
		private JMenuItem close = new JMenuItem("Close All");

		public WindowMenu() {
			desktop = jDesktopPane1;
			setText("Window");
			cascade.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.CASCADE));
			tile.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.TILE));
			close.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.CLOSE_ALL));
			tile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					// How many frames do we have?
					JInternalFrame[] allframes = desktop.getAllFrames();
					int count = allframes.length;
					if (count == 0)
						return;
					// Determine the necessary grid size
					int sqrt = (int) Math.sqrt(count);
					int rows = sqrt;
					int cols = sqrt;
					if (rows * cols < count) {
						cols++;
						if (rows * cols < count)
							rows++;
					}
					// Define some initial values for size & location
					Dimension size = desktop.getSize();
					int x = 0;
					int y = 0;
					int w = size.width / cols;
					int h = size.height / rows;
					// Iterate over the frames, deiconifying any iconified
					// frames and then
					// relocating & resizing each
					for (int i = 0; i < rows; i++) {
						for (int j = 0; j < cols && ((i * cols) + j < count); j++) {
							JInternalFrame f = allframes[(i * cols) + j];
							if ((f.isClosed() == false) && (f.isIcon() == true)) {
								try {
									f.setIcon(false);
								} catch (PropertyVetoException ex) {
								}
							}
							try {
								f.setMaximum(false);
							} catch (PropertyVetoException ex) {
							}
							desktop.getDesktopManager().resizeFrame(f, x, y, w, h);
							x += w;
						}
						y += h; // start the next row
						x = 0;
					}
				}
			});
			cascade.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					JInternalFrame ifs[] = desktop.getAllFrames();
					for (int i = 0; i < ifs.length; i++) {
						try {
							ifs[i].setMaximum(false);
						} catch (PropertyVetoException ex) {
						}
						ifs[i].setBounds(i * 20, i * 20, 400, 400);
						try {
							ifs[i].setSelected(true);
						} catch (PropertyVetoException ex) {
						}
					}
				}
			});
			arrange.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					// WindowMenu.this.desktop.arrangeFrames();
				}
			});
			close.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					closeAllMenuItemActionPerformed(null);
				}
			});
			addMenuListener(new MenuListener() {
				public void menuCanceled(MenuEvent e) {
				}

				public void menuDeselected(MenuEvent e) {
					removeAll();
				}

				public void menuSelected(MenuEvent e) {
					buildChildMenus();
				}
			});
		}

		/* Sets up the children menus depending on the current desktop state */
		private void buildChildMenus() {
			JInternalFrame[] pages = getDesktopWindows();
			JInternalFrame activePane = (JInternalFrame) getActiveDesktopWindow();
			add(tile);
			add(cascade);
			add(arrange);
			add(close);
			if (pages.length > 0)
				addSeparator();
			tile.setEnabled(pages.length > 0);
			cascade.setEnabled(pages.length > 0);
			arrange.setEnabled(pages.length > 0);
			close.setEnabled(pages.length > 0);
			for (int i = 0; i < pages.length; i++) {
				ChildMenuItem menu = new ChildMenuItem(pages[i]);
				menu.setState(pages[i].equals(activePane));
				menu.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						JInternalFrame frame = ((ChildMenuItem) ae.getSource()).getFrame();
						frame.moveToFront();
						try {
							frame.setSelected(true);
							frame.setMaximum(true);
						} catch (PropertyVetoException e) {
							CeNErrorHandler.getInstance().logExceptionMsg(null, e);
						}
					}
				});
				menu.setIcon(pages[i].getFrameIcon());
				add(menu);
			}
		}

		/*
		 * This JCheckBoxMenuItem descendant is used to track the child frame that corresponds to a give menu.
		 */
		class ChildMenuItem extends JCheckBoxMenuItem {
			/**
			 * 
			 */
			private static final long serialVersionUID = -873773456376852769L;
			private JInternalFrame frame;

			public ChildMenuItem(JInternalFrame frame) {
				super(frame.getTitle());
				this.frame = frame;
			}

			public JInternalFrame getFrame() {
				return frame;
			}
		}
	}

	// Method to return selected User object depending on current mode of
	// outlook bar
	public SpeedBarNodeInterface getLastSelectedSpeedBarObject() {
		if (OutlookBar.getSelectedIndex() == OutlookBarTypes.MY_BOOKMARKS)
			return speedBarMyBookHandler.getLastSelectedUserObject();
		else if (OutlookBar.getSelectedIndex() == OutlookBarTypes.ALL_SITES)
			return speedBarAllSiteHandler.getLastSelectedUserObject();
		else
			return null;
	}

	public SpeedBarNodeInterface[] getSelectedSpeedBarObjects() {
		try {
			if (OutlookBar.getSelectedIndex() == OutlookBarTypes.MY_BOOKMARKS)
				return speedBarMyBookHandler.getSelectedUserObjects();
			else if (OutlookBar.getSelectedIndex() == OutlookBarTypes.ALL_SITES)
				return speedBarAllSiteHandler.getSelectedUserObjects();
			else
				return null;
		} catch (Exception e) {
			log.error("Failed to get Selected Speedbar Objects.", e);
		}
		return null;
	}

	public SpeedBarHandler getMySpeedBar() {
		return speedBarMyBookHandler;
	}

	public SpeedBarHandler getAllSitesSpeedBar() {
		return speedBarAllSiteHandler;
	}

	public SpeedBarNodeInterface findSpeedBarNode(String siteName, String users_fullname, String notebook, String experiment) {
		SpeedBarNodeInterface sbi = null;
		//SpeedBarHandler hndlr = getActiveSpeedBarHandler();
		CommonHandlerInterface hndlr = getActiveSpeedBarHandler();
		if (hndlr != null) {
			String group = SpeedBarPageGroup.getGroupFromExperiment(experiment);
			if (hndlr.getSpeedBarType().equals(SpeedBarModel.SB_MY_BOOKMARKS)) {
				String[] path = { users_fullname, notebook, group, experiment };
				sbi = hndlr.getSpeedBarNode(path);
			} else {
				String[] path = { siteName, users_fullname, notebook, group, experiment };
				sbi = hndlr.getSpeedBarNode(path);
			}
		}
		return sbi;
	}

	public int getCurrentSpeedBarType() {
		return OutlookBar.getSelectedIndex();
	}

	public void setCurrentSpeedBarType(int idx) {
		OutlookBar.setSelectedIndex(idx);
	}

	//public SpeedBarHandler getActiveSpeedBarHandler() {
	public CommonHandlerInterface getActiveSpeedBarHandler() {
		if (OutlookBar.getSelectedIndex() == OutlookBarTypes.MY_BOOKMARKS)
			return speedBarMyBookHandler;
		else if (OutlookBar.getSelectedIndex() == OutlookBarTypes.ALL_SITES)
			return speedBarAllSiteHandler;
		else if (OutlookBar.getSelectedIndex() == OutlookBarTypes.EXTERNAL)
			return this.croTreeHandler;  // vb 3/10
		else
			return null;
	}

	public static class OutlookBarTypes {
		public final static int MY_BOOKMARKS = 0;
		public final static int ALL_SITES = 1;
		public final static int EXTERNAL = 2;
	}

	public void startProgressBar() {
		this.progressBar.setProgressStatus("Busy");
		disableCancelActivityButton();
		resumeWorker();
	}

	public int startProgressBar(String message) {
		this.progressBar.setProgressStatus(message);
		int progressBarIndex = ProgressBarJobItem.getInstance().addItem(progressBar, message);
		resumeWorker();
		return progressBarIndex;
	}

	private void resumeWorker() {
		if (worker != null) {
			// resume worker
			synchronized (worker) {
				worker.resetCount();
				worker.setPlsWait(false);
				worker.notify();
			}
		} else {
			worker = new guiProgressWorker();
			worker.start();
		}
	}

	public int startProgressBar(String message, ICancel cancellableActivity) {
		enableCancelActivityButton(message, cancellableActivity);
		return startProgressBar(message);
	}
	
	public void stopProgressBar() {
		this.progressBar.setProgressStatus("Ready");
		disableCancelActivityButton();		
		synchronized (worker) {
			if (worker != null) {
				worker.setPlsWait(true);
			}
		}
		this.progressBar.setProgress(0);
	}

	public void stopProgressBar(int i) {
		this.progressBar.setProgressStatus("Ready");
		ProgressBarJobItem.getInstance().removeItem(i);
		disableCancelActivityButton();
		
		synchronized (worker) {
			if (worker != null) {
				worker.setPlsWait(true);
			}
		}
		this.progressBar.setProgress(0);
	}

	public void enableCancelActivityButton(String message, ICancel cancellableActivity) {
		this.cancellableActivity = cancellableActivity;
		this.progressBar.setCancelCallback(cancellCallback);
		
		AbstractButton cancelButton = this.progressBar.getCancelButton();		
		cancelButton.setToolTipText("Cancel " + message);
	}

	public void disableCancelActivityButton() {
		this.progressBar.setCancelCallback(null);
	}

	public void setProgressBar(int progress) {
		this.progressBar.setProgress(progress);
	}

	public void setActiveDesktopWindow(JInternalFrame frame) {
		frame.moveToFront();
		jDesktopPane1.setSelectedFrame(frame);
	}

	public JInternalFrame getActiveDesktopWindow() {
		return jDesktopPane1.getSelectedFrame();
	}

	public JInternalFrame[] getDesktopWindows() {
		try {
			return jDesktopPane1.getAllFrames();
		} catch (Exception e) {
			// Try again, something happened during the first try
			try {
				return jDesktopPane1.getAllFrames();
			} catch (Exception e2) {
				return null;
			}
		}
	}

	// Method to refresh toolbar icons so only those applicable are active
	public void refreshIcons() {
		if (_shuttingDown) {
			return;
		}
		ImageIcon saveIcon = CenIconFactory.getImageIcon(CenIconFactory.MenuBar.SAVE_PAGE);
		JInternalFrame[] pages = getDesktopWindows();
		if (pages == null || pages.length == 0) {
			closeFileMenuItem.setEnabled(false);
			closeAllMenuItem.setEnabled(false);
			saveMenuItem.setEnabled(false);
			jButtonSavePage.setEnabled(false);
			repeatExperimentMenuItem.setEnabled(false);
			saveAllMenuItem.setEnabled(false);
			jButtonSaveAll.setEnabled(false);
			printMenuItem.setEnabled(false);
			jButtonPrint.setEnabled(false);
		} else {
			JInternalFrame currentWindow = getActiveDesktopWindow();
			closeFileMenuItem.setEnabled(currentWindow != null);
			closeAllMenuItem.setEnabled(true);
			saveMenuItem.setEnabled(false);
			jButtonSavePage.setEnabled(false);
			repeatExperimentMenuItem.setEnabled(false);
			jButtonPrint.setEnabled(false);
			printMenuItem.setEnabled(false);
			
			if (currentWindow != null) {
				
				if (currentWindow instanceof NotebookPageGuiInterface) {
					NotebookPageModel pageModel = ((NotebookPageGuiInterface)currentWindow).getPageModel();
					// setup save buttons here...
					if (pageModel != null) {
						if(pageModel.isChanging()) {
							saveIcon = CenIconFactory.getImageIcon(CenIconFactory.MenuBar.SAVE_PAGE_BACKGROUND);
						}
						saveMenuItem.setEnabled((pageModel.isEditable() && pageModel.isModelChanged()));
						jButtonSavePage.setEnabled(saveMenuItem.isEnabled());
						jButtonPrint.setEnabled(true);
						printMenuItem.setEnabled(true);
						repeatExperimentMenuItem.setEnabled(StringUtils.equals(pageModel.getLatestVersion(), "Y"));
						if(currentWindow instanceof ParallelNotebookPageGUI) {
							repeatExperimentMenuItem.setEnabled(MasterController.isParallelRepeatEnabled());
							// vb put this in here originally, so I kept it.  Not sure why page header treated different for only
							// parallel experiments, but don't wish to break parallel.
							saveMenuItem.setEnabled((saveMenuItem.isEnabled() || pageModel.getPageHeader().isModelChanged()));
						}
					} else {
						saveMenuItem.setEnabled(false);
						jButtonSavePage.setEnabled(false);
						jButtonPrint.setEnabled(false);
						printMenuItem.setEnabled(false);
						repeatExperimentMenuItem.setEnabled(false);
					}
				}
			}
			boolean saveAllActive = jButtonSavePage.isEnabled();
			for (int i = 0; i < pages.length && !saveAllActive; i++) {
				if (pages[i] instanceof NotebookPageGuiInterface) {
					NotebookPageModel model = ((NotebookPageGuiInterface) pages[i]).getPageModel();
					if (model != null && model.isModelChanged()) 
						saveAllActive = true;
				}
			}
			saveAllMenuItem.setEnabled(saveAllActive);
			jButtonSaveAll.setEnabled(saveAllActive);
		}
		saveMenuItem.setIcon(saveIcon);
		jButtonSavePage.setIcon(saveIcon);
	}

	/**
	 * Enable "Save" and "Save all" buttons and corresponding menu items
	 */
	public void enableSaveButtons() {
		if (!jButtonSavePage.isEnabled()) {
			JInternalFrame frame = getActiveDesktopWindow();
			if (frame instanceof NotebookPageGuiInterface) {
				NotebookPageGuiInterface pageGui = (NotebookPageGuiInterface) frame;
				NotebookPageModel pageModel = pageGui.getPageModel();
				
				if (pageModel != null && pageModel.isEditable()) {
					pageModel.setModelChanged(true);
					
					refreshIcons();
					repaint();
				}
			}
		}
	}
	
	public ProgressStatusBarItem getProgressBar() {
		return progressBar;
	}

	public CeNDesktopPane getJDesktopPane1() {
		return jDesktopPane1;
	}

	public void updateMemStatsView(boolean flag) {
		memoryBar.setVisible(flag);
	}

	public boolean isSaveAllButtonEnabled()
	{
		return jButtonSaveAll.isEnabled();
	}

	public boolean isSavePageButtonEnabled()
	{
		return jButtonSavePage.isEnabled();
	}

	public boolean checkPendingChangesInPage() {
		if (isSavePageButtonEnabled()) {
			int saveOption = JOptionPane.showConfirmDialog(MasterController.getGUIComponent(), "Do you want to save the changes?","Save Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);
			if (saveOption != JOptionPane.CANCEL_OPTION) {
				if (saveOption == JOptionPane.YES_OPTION) {
					jButtonSavePageActionPerformed(null);
					if (MasterController.getGUIComponent().isSavePageButtonEnabled()) {
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Notebook " /*+ pageModel.getNbRef().getNotebookRef() */ + " save failed. Please try again later.", "Unexpected error", JOptionPane.ERROR_MESSAGE);
						return false;
					}
				}
				if (saveOption == JOptionPane.NO_OPTION) {
					PageChangesCache.restorePage(((NotebookPageGuiInterface) getActiveDesktopWindow()).getPageModel());
				}
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	
	public String getNotebookIfOnlyOneExists() {
		ArrayList<ArrayList<String>> notebooksList = getUserNotebookList();
		if (notebooksList.size() > 0)
			return notebooksList.get(0).get(0);
		else
			return null;

	}

	private void checkExperimentCompliance() {
	try {
		StorageDelegate sDel = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());

		String  numComplianceDaysString = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_NUM_COMPLANCE_DAYS);
		int numComplianceDays = 0;
		if (numComplianceDaysString != null)
			numComplianceDays = Integer.parseInt(numComplianceDaysString);
		int count = sDel.checkCompliance(MasterController.getUser().getNTUserID(), numComplianceDays);
		if (count > 0) {
			int num_days = 0;
	           try {
	           	num_days = Integer.parseInt(CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_NUM_COMPLANCE_DAYS));
	           } catch (SystemPropertyException e) { }

       		JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
					count + " uncompleted experiment(s) were found which have not been modified in the last " + num_days + " days\n" +
					"and are therefore out of compliance with Records Management policies.\n\n" +
					"Note: An experiment is considered uncomplete until it is Archived and has a lock symbol.\n\n" +
					"Please complete and archive these experiments in a timely manner.", 
					"Records Management Compliance Issue", JOptionPane.WARNING_MESSAGE);
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}
}
