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
/*
 * Created on 10-May-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.chemistry.enotebook.client.gui.controller;

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.controller.scheduler.TimerStatusHandler;
import com.chemistry.enotebook.client.datamodel.speedbar.SpeedBarNodeInterface;
import com.chemistry.enotebook.client.datamodel.speedbar.SpeedBarNotebook;
import com.chemistry.enotebook.client.datamodel.speedbar.SpeedBarPage;
import com.chemistry.enotebook.client.datamodel.speedbar.SpeedBarPageGroup;
import com.chemistry.enotebook.client.delegate.DesignServiceDelegate;
import com.chemistry.enotebook.client.delegate.NotebookDelegate;
import com.chemistry.enotebook.client.delegate.NotebookDelegateException;
import com.chemistry.enotebook.client.gui.Gui;
import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.client.gui.PersonDelegate;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.RepeatExpHelper;
import com.chemistry.enotebook.client.gui.contents.*;
import com.chemistry.enotebook.client.gui.esig.SignatureHandler;
import com.chemistry.enotebook.client.gui.page.*;
import com.chemistry.enotebook.client.gui.page.regis_submis.PCeNRegistration_BatchDetailsViewContainer;
import com.chemistry.enotebook.client.gui.speedbar.CommonHandlerInterface;
import com.chemistry.enotebook.client.gui.speedbar.SpeedBarHandler;
import com.chemistry.enotebook.client.print.gui.*;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.datahandlers.NotebookPageHandler;
import com.chemistry.enotebook.experiment.datamodel.compound.Compound;
import com.chemistry.enotebook.experiment.datamodel.page.*;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import com.chemistry.enotebook.person.classes.IPerson;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.signature.classes.TemplateVO;
import com.chemistry.enotebook.storage.NotebookExistsException;
import com.chemistry.enotebook.storage.NotebookInvalidException;
import com.chemistry.enotebook.storage.StorageException;
import com.chemistry.enotebook.storage.ValidationInfo;
import com.chemistry.enotebook.storage.delegate.NBKPageUpdateManager;
import com.chemistry.enotebook.storage.delegate.ProcedureImagesUpdateManager;
import com.chemistry.enotebook.storage.delegate.StorageAccessException;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.storage.exceptions.StorageInitException;
import com.chemistry.enotebook.util.EncryptionUtil;
import com.chemistry.enotebook.util.ExceptionUtils;
import com.chemistry.enotebook.utils.*;
import com.chemistry.enotebook.utils.SwingWorker;
import com.common.chemistry.codetable.CodeTableCache;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;
import java.io.*;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

/**
 * 
 * 
 */
public class GuiController implements NotebookPageChangedListener {
  // Error Handler
  private static final CeNErrorHandler ceh = CeNErrorHandler.getInstance();
  private static final String APP_TITLE = "- " + CeNConstants.PROGRAM_NAME;
  // Pool info
  // private final String POOL_NAME = "CeN Execute Threads";
  private final int POOL_MAX_SIZE = 50;
  private final int POOL_MIN_SIZE = 10;
  private final int POOL_INIT_SIZE = 5;
  private final int THREAD_TIMEOUT = 1000 * 60 * 2; // 2 minutes
  private Gui mGui;
  // private DefaultTreeModel tempSpeedBar;
  private NotebookPageCache nbPageCache = null;
  private NotebookDelegate nbDel = null;
  private HashMap<String, NotebookContentsGUI> contentsCache = new HashMap<String, NotebookContentsGUI>();
  // private transient SwingWorker backgroundGuiLoad = null;
  // private ThreadPool executePool = null;
  public static PooledExecutor threadPool = null;
  private Timer autoSaveTimer;
  private FastHashMap userNameToFullNameMap = new FastHashMap();
  private boolean saveFlag = false;
  private Object saveObj = new Object();

  private Map<String, NotebookPageModel> loadedPageModels = new HashMap<String, NotebookPageModel>();
  private static String rootDirectory = MasterController.getApplicationDirectory();
  private static String autoSaveDirName = "AutoSave";
  private static String offlineCopyFileFormat = ReportURLGenerator.DOC;
  
  private static final Log log = LogFactory.getLog(MasterController.class);
  public static String CEN_VERSION = "1.2";

  public GuiController() {
    init();
  }

  private void init() {
    mGui = new Gui();
    mGui.setVisible(false);
    ToolTipManager.sharedInstance().setDismissDelay(5000);
    ToolTipManager.sharedInstance().setInitialDelay(500);
    ToolTipManager.sharedInstance().setReshowDelay(1000);
    UIManager.put("CollapsiblePane.background", new Color(122, 194, 174));
    UIManager.put("CollapsiblePanes.gap", new Integer(0));

    // Set for printing issues.
    JComponent.setDefaultLocale(new Locale("en", "US"));

    try {
      nbDel = new NotebookDelegate(MasterController.getUser().getSessionIdentifier());
      NotebookPageCache.setRootDirectory(MasterController.getApplicationDirectory());
      nbPageCache = new NotebookPageCache();
      // executePool = new ThreadPool(POOL_NAME, POOL_MAX_SIZE);
      threadPool = new PooledExecutor();
      threadPool.setMinimumPoolSize(POOL_MIN_SIZE);
      threadPool.setMaximumPoolSize(POOL_MAX_SIZE);
      threadPool.setKeepAliveTime(THREAD_TIMEOUT);
      threadPool.createThreads(POOL_INIT_SIZE);
    } catch (NotebookDelegateException e) {
      CeNErrorHandler.getInstance().logExceptionMsg(null, e);
    } catch (Exception e) {
      CeNErrorHandler.getInstance().logExceptionMsg(null, "Most likely failed to create rootDirectory.", e);
    }

    userNameToFullNameMap.put(MasterController.getUser().getNTUserID(), MasterController.getUser().getFullName());
  }

  public void dispose() {
    // TODO: Possible thread cleanup here?
    mGui = null;
    if (nbPageCache != null)
      nbPageCache.dispose();
    threadPool.interruptAll();
    threadPool.shutdownNow();
    /*
     * if (executePool != null) { executePool.destroyThreadGroup(POOL_NAME); executePool = null; }
     */
  }

  public Gui getGUIComponent() {
    return mGui;
  }

  // Displays GUI to the world.
  public boolean showGui() {
    mGui.setTitle(MasterController.getUser().getDisplayName() + "'s " + APP_TITLE);
    mGui.setVisible(true);
    mGui.toFront();
    return true;
  }

  public NotebookPageCache getPageCache() {
    return nbPageCache;
  }

  public static PooledExecutor getExecutePool() {
    return threadPool;
  }

  public void startAutoSaveTimer() {
    String sInterval = "5";
    try {
      sInterval = MasterController.getUser().getPreference(NotebookUser.PREF_AUTOSAVE_INTERVAL);
    } catch (UserPreferenceException e) {
      ceh.logExceptionMsg(null, e);
    }
    if (sInterval == null || sInterval.length() == 0)
      sInterval = "5";
    int iInterval = new Integer(sInterval).intValue();
    iInterval *= (60 * 1000); // convert to milli-seconds
    autoSaveTimer = new Timer(iInterval, new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        // disable the timer so no more autosaves will fire until restarted
        autoSaveTimer.stop();
        if (mGui != null)// **TODO && !mGui.isShuttingDown())
          autoSaveTimerActionPerformed();
      }
    });
    autoSaveTimer.start();
  }

  private void autoSaveTimerActionPerformed() {
    _waitOnSave();

    final String progressStatus = "Creating Auto-Recover file(s) ...";
    CeNJobProgressHandler.getInstance().addItem(progressStatus);
    SwingWorker worker = new SwingWorker() {
      public Object construct() {
        try {
          NotebookPageModel currentModel;

          // TODO: save all pages ever opened
          JInternalFrame[] mNotebookPageGuiInterfaces = (JInternalFrame[]) GuiController.this.mGui.getDesktopWindows();
          for (JInternalFrame mNotebookPageGuiInterface : mNotebookPageGuiInterfaces) {
            if (mNotebookPageGuiInterface != null && mNotebookPageGuiInterface instanceof NotebookPageGuiInterface) {
              currentModel = ((NotebookPageGuiInterface) mNotebookPageGuiInterface).getPageModel();
              // NotebookPageModel cachedModel =
              // GuiController.this.loadedPageModels.get(currentModel.getNotebookRefAsString());
              if (currentModel != null && currentModel.isModelChanged() && currentModel.isEditable()) {
                if (checkIfAutoSaveExists(currentModel.getNbRef()))
                  deleteAutoSaveFile(currentModel);

                autoSave(currentModel, getAutoSaveLocation());
              }
            }
          }
        } catch (Exception e) {
          CeNErrorHandler.getInstance().logExceptionMsg(mGui, "Unable to create Auto-Recover files", e);
        } finally {
          saveComplete();
        }
        autoSaveTimer.start();
        try {
          MasterController.getUser().updateUserPrefs();
        } catch (Exception e) { /* ignored */
        }
        return null;
      }

      public void finished() {
        CeNJobProgressHandler.getInstance().removeItem(progressStatus);
      }
    };
    worker.start();
  }

  private void saveComplete() {
    synchronized (saveObj) {
      // System.out.println("Save process complete");
      saveFlag = false;
      saveObj.notify();
    }
  }

  private void _waitOnSave() {
    synchronized (saveObj) {
      if (saveFlag) {
        while (true) {
          try {
            System.out.println("Waiting on Save to complete ...");
            saveObj.wait();
            System.out.println("Done waiting for Save... Save process started ...");
            saveFlag = true;
            break;
          } catch (InterruptedException ie) { /* Ignored */
          }
        }
      } else {
        System.out.println("Save process started ...");
        saveFlag = true;
      }
    }
  }

  /*
  public void saveAll() {
    _waitOnSave();
    // Check for database modifications
    try {
      NotebookDelegate delegate = new NotebookDelegate(MasterController.getUser().getSessionIdentifier());
      Collection pages = nbPageCache.getLoadedNotebookPages();
      synchronized (pages) {
        for (Iterator it = pages.iterator(); it.hasNext();) {
          NotebookPage nbPage = (NotebookPage) it.next();
          if (delegate.isNbDbPageUpdated(nbPage))
            if (!continueSave(nbPage.getNotebookRefAsString()))
              nbPage.setModified(false);
        }
      }
      nbPageCache.saveAllNotebookPages(false);
      saveComplete();
    } catch (Exception e) {
      saveComplete();
      CeNErrorHandler.getInstance().logExceptionMsg(null, e);
    }

    try {
      MasterController.getUser().updateUserPrefs();
    } catch (Exception e) { }
  }
    */
    

  private void addNotebookPageGuiInterfaceToGUI(NotebookPageGuiInterface notebookPage) {
    // USER2 Don't change any thing here.
    // Autorecover loading needs to have model changed true and db loading needs to have model changed false.
    // notebookPage.getPageModel().setModelChanged(false); // While loading the page to GUI it should not have
    // pageModel.setModified(true).
    JInternalFrame notebookPageGUI = (JInternalFrame) notebookPage;
    notebookPageGUI.setPreferredSize(new Dimension(100, 100));
    mGui.getJDesktopPane1().add(notebookPageGUI);
    mGui.getJDesktopPane1().getDesktopManager().minimizeFrame(notebookPageGUI);
    mGui.getJDesktopPane1().getDesktopManager().maximizeFrame(notebookPageGUI);
    try {
      notebookPageGUI.setSelected(true);
      notebookPageGUI.setMaximum(true);
    } catch (PropertyVetoException e) {
      e.printStackTrace();
    }
  }

  public void removeNotebookPageFromGUI(NotebookPageGuiInterface pageGui) {
    // Need to create a dummy frame here if there is only one NotebookPageGUI object
    // otherwise it won't allow the frame to be destroyed and hence garbage collected
    if (mGui.getJDesktopPane1().getAllFrames().length == 1) {
      // for some reason this gives a nullpointerException, so redo it and it's ok
      try {
        mGui.getJDesktopPane1().getDesktopManager().activateFrame(new JInternalFrame());
      } catch (NullPointerException npe) {
        mGui.getJDesktopPane1().getDesktopManager().activateFrame(new JInternalFrame());
      }
    }
    mGui.getJDesktopPane1().getDesktopManager().deactivateFrame((JInternalFrame) pageGui);
    if (pageGui instanceof NotebookPageGuiInterface) {
      NotebookPageModel pageModel = pageGui.getPageModel();
      if (pageModel != null) {
        try {
          nbPageCache.removeNotebookPage(pageModel.getSiteCode(), pageModel.getNbRef(), pageModel.getVersion());
          pageModel.dispose();
          pageGui.dispose();
        } catch (Throwable e) {
          e.printStackTrace();
        }
        pageModel = null;
      }
    }
    // else if (pageGui instanceof ConceptionNotebookPageGUI) {
    // NotebookPageModel pageModel = pageGui.getPageModel();
    // if (pageModel != null) {
    // try {
    // //nbPageCache.removeNotebookPage(pageModel.getSiteCode(),
    // pageModel.getNbRef(), pageModel.getVersion()); //
    // pageModel.dispose();
    // } catch (Throwable e) { // TODO Auto-generated
    // e.printStackTrace();
    // }
    // pageModel = null;
    // }
    // } else if (pageGui instanceof SingletonNotebookPageGUI) {
    // NotebookPageModel pageModel = pageGui.getPageModel();
    // if (pageModel != null) {
    // pageModel = null;
    // }
    // nbPageCache.dispose();
    // }

    pageGui.dispose();
    mGui.getJDesktopPane1().getDesktopManager().closeFrame((JInternalFrame) pageGui);
    pageGui.setPageModel(null);
    pageGui = null;
    System.gc();
    mGui.refreshIcons();
    System.gc();

    // Select next frame.
    JInternalFrame[] allFrames = mGui.getJDesktopPane1().getAllFrames();
    if (allFrames.length > 0) {
      allFrames[0].moveToFront();
      mGui.getJDesktopPane1().setSelectedFrame(allFrames[0]);
      try {
        allFrames[0].setSelected(true);
        allFrames[0].setMaximum(true);
      } catch (PropertyVetoException e) {
        e.printStackTrace();
      }
    }
  }

  public void addContentsToGUI(String siteCode, String notebook) {
    addContentsToGUI(siteCode, notebook, -1, -1);
  }

  public void addContentsToGUI(final String siteCode, final String notebook, final int start, final int end) {
    mGui.startProgressBar();
    final NotebookContentsLoader ncl = new NotebookContentsLoader(siteCode, notebook, start, end);
    ncl.addLoaderListener(new NotebookContentsLoaderAction() {
      public void ContentsLoaded(final NotebookContentsLoaderEvent evt) {
        final NotebookContentsGUI gui = evt.getContentsGui();
        contentsCache.put(siteCode + notebook + "," + start + "," + end, gui);
        mGui.getJDesktopPane1().add(gui);
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            try {
              gui.setMaximum(true);
            } catch (Exception e) {
            }
            mGui.setActiveDesktopWindow(gui);
            mGui.stopProgressBar();
          }
        });
      }
    });
    ncl.load();
  }

  public void removeNotebookContentsFromGUI(NotebookContentsGUI contents) {
    contentsCache.remove(contents.getModel().getSiteCode()
        + contents.getModel().getNotebook() + ","
        + contents.getModel().getStartExperiment() + ","
        + contents.getModel().getStopExperiment());
    NotebookContentsTableModel model = contents.getModel();
    // Need to create a dummy frame here if there is only one NotebookPageGUI object
    // otherwise it won't allow the frame to be destroyed and hence garbage collected
    if (mGui.getJDesktopPane1().getAllFrames().length == 1) {
      // for some reason this gives a nullpointerException, so redo it and it's ok
      try {
        mGui.getJDesktopPane1().getDesktopManager().activateFrame(new JInternalFrame());
      } catch (NullPointerException npe) {
        mGui.getJDesktopPane1().getDesktopManager().activateFrame(new JInternalFrame());
      }
    }
    mGui.getJDesktopPane1().getDesktopManager().deactivateFrame(contents);
    contents.setModel(null);
    model.dispose();
    model = null;
    contents.dispose();
    mGui.getJDesktopPane1().getDesktopManager().closeFrame(contents);
    contents = null;
    System.gc();
    mGui.refreshIcons();
    System.gc();
  }

  private void addNotebookToSpeedBar(final String site, final String user, final String notebook) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (notebook != null) {
          mGui.getMySpeedBar().addNotebook(site, user, notebook,
              (mGui.getActiveSpeedBarHandler() == mGui.getMySpeedBar()));
          mGui.getAllSitesSpeedBar().addNotebook(site, user, notebook,
              (mGui.getActiveSpeedBarHandler() == mGui.getAllSitesSpeedBar()));
        }
      }
    });
  }

  private void addNotebookPageToSpeedBar(final NotebookPage page) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        mGui.getMySpeedBar().addPage(page, (mGui.getActiveSpeedBarHandler() == mGui.getMySpeedBar()));
        mGui.getAllSitesSpeedBar().addPage(page, (mGui.getActiveSpeedBarHandler() == mGui.getAllSitesSpeedBar()));
      }
    });
  }

  //
  // Utilities
  //
  public void createNewNotebook(final String site, final String siteCode, final String user, final String userID) {
    // Validate that user has write access to notebook specified
    // TODO: Group access to notebook should be considered
    final NotebookUser loginUser = MasterController.getUser();
    if (loginUser != null) {
      if (!loginUser.getFullName().equals(user) && !loginUser.isSuperUser())
        JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
                        "You cannot create a Notebook in another users area.",
                        "New Notebook Error", 
                        JOptionPane.ERROR_MESSAGE);
      else {
        SpeedBarHandler handler = (SpeedBarHandler) MasterController.getGUIComponent().getActiveSpeedBarHandler();
        if (handler != null) {
          String newNotebook = JOptionPane.showInputDialog(MasterController.getGuiComponent(), "Enter a new Notebook # in '########' format");
          if (newNotebook != null) {
            newNotebook = NotebookPageUtil.formatNotebookNumber(newNotebook);
            if (!NotebookPageUtil.isValidNotebookNumber(newNotebook))
              JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Error:  Invalid Notebook '"
                  + newNotebook + "'.", "New Notebook Error", JOptionPane.ERROR_MESSAGE);
            else {
              final String nb = newNotebook;
              /*
               * SwingUtilities.invokeLater(new Runnable() { public void run() {
               */
              try {
                if (!loginUser.getFullName().equals(user))
                  nbDel.createNotebook(siteCode, userID, nb);
                else
                  nbDel.createNotebook(nb);
                addNotebookToSpeedBar(site, user, nb);
                String firstExp = nb + "-" + NotebookPageUtil.getStringPad(NotebookPageUtil.NB_PAGE_MAX_LENGTH);
                MasterController.getUser().setPreference(NotebookUser.PREF_CurrentNbRef, firstExp);
                MasterController.getUser().setPreference(NotebookUser.PREF_CurrentNbRefVer, "0");
                MasterController.getUser().setPreference(NotebookUser.PREF_LastNBRef, firstExp);
                MasterController.getUser().updateUserPrefs();
              } catch (UserPreferenceException e1) {
                ceh.logExceptionWithoutDisplay(e1, "Failed saving user preferences.");
              } catch (NotebookInvalidException e2) {
                ceh.logExceptionMsg(MasterController.getGUIComponent(), e2);
              } catch (NotebookExistsException e2) {
                JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
                                "Error:  Notebook '" + nb + "' is already in use.", 
                                "New Notebook Error", 
                                JOptionPane.ERROR_MESSAGE);
              } catch (StorageAccessException e3) {
                ceh.logExceptionMsg(null, e3);
              }
              /*
               * } });
               */}
          }
        } else
          JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
              "Failed to determine Speedbar handler",
              "New Notebook Error", 
              JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public void createNewExperiment4PCeNNoSPID(String notebookRef) {
		if (StringUtils.isBlank(notebookRef)) {
			notebookRef = getNotebookRef();
		}
		String spidString = null;
		JTextField spidField = new JTextField();
		spidField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
			}

			public void focusLost(FocusEvent arg0) {
				JTextField spidField = (JTextField) arg0.getComponent();
				String computedSPID = "";
				String enteredSPID = spidField.getText();
				if (enteredSPID.length() == 0)
					return;
				enteredSPID = enteredSPID.replaceFirst("sp", "SP");
				enteredSPID = enteredSPID.replaceFirst("V", "v");
				if (enteredSPID.length() == 15)
					computedSPID = enteredSPID;
				else {
					if (enteredSPID.indexOf("v") < 0)
						enteredSPID = enteredSPID.concat("v00");
					else if (enteredSPID.substring(enteredSPID.indexOf("v")).length() < 3)
						enteredSPID = enteredSPID.substring(0, (enteredSPID.indexOf("v"))) +
										"v0" + enteredSPID.substring(enteredSPID.indexOf("v") + 1);

					int versionIndex = enteredSPID.indexOf("v");
					String spNo = enteredSPID.substring(0, versionIndex);
					String versionNo = enteredSPID.substring(versionIndex);
					if (spNo.length() != 12) {
						if (spNo.indexOf("SP") > -1) {
							spNo = spNo.substring(2);// Remove "SP" from the input string.
						}
						spNo = formatNumber(spNo);
						spNo = "SP".concat(spNo);
					}
					computedSPID = spNo + versionNo;// Add "SP" from the input string.
				}
				spidField.setText(computedSPID);
			}

			private String formatNumber(String spNo) {
				int standardLength = 10;
				int length = spNo.length();
				if (length != standardLength) {
					for (int i = 0; i < (standardLength - length); i++)
						spNo = "0" + spNo;
				}
				return spNo;
			}

		});

		String message = "Enter SPID No:";
		Object[] options = { "Create", "Preview", "Search in Design Service", "Cancel" };
		int result = JOptionPane.showOptionDialog(	mGui,
													new Object[] { message, spidField },
													"New Experiment for Parallel CeN",
													JOptionPane.OK_CANCEL_OPTION,
													JOptionPane.QUESTION_MESSAGE,
													null,
													options,
													null);
		if (result == 0 || result == 1 || result == 2) {
			spidString = spidField.getText();
			if (spidString == null || spidString.length() < 12) {
				JOptionPane.showMessageDialog(	MasterController.getGUIComponent(),
												"Invalid SPID",
												"SPID Load Error",
												JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		if (result == 0) {
			createNewExperiment4PCeNExecutor(notebookRef, spidString);
		} else if (result == 1) {

		} else if (result == 2) {// search
			// display the tree bar
			mGui.setCurrentSpeedBarType(2);
			// mGui.showDSPSearchPanel();
			try {

			} catch (RuntimeException e) {
				CeNErrorHandler.getInstance().logErrorMsg(MasterController.getGUIComponent(), "Design Service is not available", "Design Service Error");
			}
			/*
			 * BasicSearchInputPanel basicSearchInputPanel = new BasicSearchInputPanel();
			 * 
			 * basicSearchInputPanel.addDSPListener(new DSPListener(){ public void DSPEventOccurred(DSPEvent dspEvent) {
			 * System.out.println(dspEvent); } });
			 * 
			 * JDialog dialog=new JDialog(GUI, "Design Service Search", true); dialog.getContentPane().add(basicSearchInputPanel);
			 * dialog.setLocationRelativeTo(GUI); dialog.setSize(basicSearchInputPanel.getPreferredSize()); dialog.show();
			 */
			return;
		} else {// cancel
			return;
		}
	}

  public void createNewExperiment4PCeN(final String fSPID) {
    String notebookRef = getNotebookRef();
    if (notebookRef != null && !notebookRef.equals(""))
      createNewExperiment4PCeNExecutor(notebookRef, fSPID);
  }

  public void createNewExperiment4PCeNExecutor(String lastNotebook, final String fSPID) {
    final String lastNotebookRef = lastNotebook + "-0000";// Just to pass
    if (isPropertyEnabled(CeNSystemXmlProperties.PROP_NEW_PARALLEL_ENABLED) == true
        || MasterController.getUser().isSuperUser() == true) 
    {
      new SwingWorker() {
        final String progressStatus = "Loading Design Synthesis Plan ...";
        String message = "";
  
        public Object construct() {
          CeNJobProgressHandler.getInstance().addItem(progressStatus);
          NotebookPageModel pageModel = null;
          final NotebookUser user = MasterController.getUser();
          try {
            NotebookRef lastNbRef = null;
            // String lastNotebook = MasterController.getUser().getPreference(NotebookUser.PREF_LastNBRef);
            lastNbRef = new NotebookRef(lastNotebookRef);
            DesignServiceDelegate dspDel = new DesignServiceDelegate();
            pageModel = dspDel.getNotebookPageModelFromDSP(user.getSessionIdentifier(), 
                                                           fSPID, 
                                                           lastNbRef.getNbNumber(), 
                                                           user.getSiteCode(), 
                                                           user.getNTUserID());
            if (log.isInfoEnabled()) {
              log.info("createNewExperiment4PCeN from " + fSPID);
              for (ReactionStepModel step : pageModel.getReactionSteps()) {
                List<BatchesList<ProductBatchModel>> batchesLists = step.getProductBatchLists();
                for (BatchesList<ProductBatchModel> batchesList : batchesLists) {
                  List<ProductBatchModel> productBatches = batchesList.getBatchModels();
                  log.info("Reaction step " + step.getStepNumber() + " has " + productBatches.size()
                      + " product batches");
                  if (productBatches.size() == 0)
                    log.info("This step has no product batches");
                  else {
                	  for (ProductBatchModel batch : productBatches) {
                		  if (batch != null) {
                			  batch.setOwner(pageModel.getBatchOwner());
                		  }
                	  }
                    Collections.sort(productBatches, new NbkBatchNumberComparator<ProductBatchModel>());
                    log.info("First batch number is "
                        + productBatches.get(0).getBatchNumberAsString());
                    log.info("Last batch number is "
                        + productBatches.get(productBatches.size() - 1).getBatchNumberAsString());
                  }
                }
              }
              log.info("");
            }
  
          }
          /*
           * catch (UserPreferenceException e) { // TODO Auto-generated catch block
           * JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Could not create a parallel experiment page",
           * "New Experiment Error", JOptionPane.ERROR_MESSAGE); } catch (InvalidNotebookRefException e) { // TODO
           * Auto-generated catch block e.printStackTrace(); } catch (StorageAccessException e) { // TODO Auto-generated catch
           * block e.printStackTrace(); }
           */
          catch (Exception e) {
            // JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
            // "There is an error when creating a parallel experiment page.", "New Experiment Error",
            // JOptionPane.ERROR_MESSAGE);
            // ceh.logExceptionMsg(null, e);
            if (ExceptionUtils.getRootCause(e) != null) {
              this.message = ExceptionUtils.getRootCause(e).getMessage();
              // Trim message if the error is from DesignService
              if (message != null) {
                int i = message.lastIndexOf("DesignServiceException");
                if (i > 0 && message.length() > (i + 23)) {
                  message = message.substring((i + 23));
                }
  
              }
            } else {
              this.message = e.getMessage();
            }
            ceh.logExceptionWithoutDisplay(e, "Issue creating Parallel experiment from SPID(" + fSPID + ")."
                    + this.message);
          }
  
          // former "finished()"
          final String progressStatus2 = "Saving Parallel Experiment ...";
          final NotebookPageModel pageModelToSave = pageModel;
  
          if (pageModel == null) {
            JOptionPane.showMessageDialog(MasterController.getGUIComponent(), this.message,
                "New Parallel Experiment Error", JOptionPane.ERROR_MESSAGE);
            CeNJobProgressHandler.getInstance().removeItem(progressStatus);
            return null;
          }
          // Launch a new thread for saving the NBPage model for parallel exp
          new SwingWorker() {
            public Object construct() {
              CeNJobProgressHandler.getInstance().addItem(progressStatus2);
              log.debug("Thread to save NBKPage model started..");
              try {
            	StorageDelegate storageDelegate = ServiceController.getStorageDelegate(user.getSessionIdentifier());
                storageDelegate.addNotebookPage(pageModelToSave,MasterController.getUser().getSessionIdentifier());
                log.debug("NBKPage model save sucessfull.");
              } catch (Exception e) {
				JOptionPane.showMessageDialog(	MasterController.getGUIComponent(),
												"Problem saving new parallel experiment page",
												"New Parallel Experiment Error",
												JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionMsg(null, e);
              }
  
              return null;
            };
  
            public void finished() {
              CeNJobProgressHandler.getInstance().removeItem(progressStatus2);
              log.debug("Thread to save NBKPage model finshed..");
            }
          }.start();
  
          if (user != null) {
            NotebookRef lastNbRef = null;
            if (NotebookPageUtil.isValidNotebookRef(pageModel.getNbRef().getNotebookRef())) {
				try {
				  lastNbRef = new NotebookRef(pageModel.getNbRef().getNotebookRef());
				} catch (Exception e) {
					// won't get here
				}

            } else if (NotebookPageUtil.isValidNotebookRef(lastNotebookRef)) {
	            try {
	              lastNbRef = new NotebookRef(lastNotebookRef);
	            } catch (Exception e) {
	            	// won't get here
	            }
            } else {
            	log.error("Invalid notebook references available: lastNotebookRef = " + lastNotebookRef + 
            	          " and current pageModel ref: " + pageModel.getNbRef().getNotebookRef());
                JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
                                              "Couldn't establish proper Notebook Reference \n" +
                                              "from page model nor from previous experiments.\n " +
                                              "Last Notebook Ref: " + lastNotebookRef + "\n" +
                                              "PageModel Ref: " + pageModel.getNbRef().getNotebookRef(),
                                              "New Parallel Experiment Error", JOptionPane.ERROR_MESSAGE);
            	return null;
            }
            // Display new page
            try {
              // Check this number hasn't already been reserved in the cache
              while (nbPageCache.hasPage(user.getSiteCode(), lastNbRef, 1)) {
                lastNbRef.setNbPage(String.valueOf(Integer.parseInt(lastNbRef.getNbPage()) + 1));
              }
              log.debug("NBKPage UI construction started..");
              ParallelNotebookPageGUI parallelNotebookPageGUI = new ParallelNotebookPageGUI(pageModel);
              addNotebookPageGuiInterfaceToGUI(parallelNotebookPageGUI);
              if (mGui != null)
                addNotebookPageToSpeedBar(pageModel);
              // nbPageCache.reserveNotebookPage(user.getSiteCode(), lastNbRef, 1);
              // NotebookPage nbPage = NotebookPageFactory.create(lastNbRef.getNotebookRef(), user, true, true);
              // nbPageCache.addNotebookPage(nbPage);
              // addNotebookPageToSpeedBar(nbPage);
              // addNotebookPageToGUI(nbPage);
              // saveExperiment(nbPage);
              
              String firstExp = pageModel.getNbRef().toString();
              try {
                MasterController.getUser().setPreference(NotebookUser.PREF_CurrentNbRef, firstExp);
                MasterController.getUser().setPreference(NotebookUser.PREF_CurrentNbRefVer, "0");
                MasterController.getUser().setPreference(NotebookUser.PREF_LastNBRef, firstExp);
                MasterController.getUser().updateUserPrefs();
              } catch (UserPreferenceException e) {
                ceh.logExceptionWithoutDisplay(e, null);
              }
              
              PageChangesCache.storePage(pageModel);
              log.debug("NBKPage UI construction completed.");
            } catch (InvalidNotebookRefException ine) {
              ceh.logExceptionMsg(null, ine);
            }
          }
  
          CeNJobProgressHandler.getInstance().removeItem(progressStatus);
          
          return null;
        };
  
        /**
         * Finished is executed as a synchronized portion of the SwingWorker.
         * This causes all actions to occur in the AWT Event thread
         */
        public void finished() {
        }
      }.start();
    } else {
      log.info("Parallel Experiment creation disabled");
      JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
          "Parallel Experiment creation disabled.  No parallel experiment will be created.", "New Parallel Experiment Error",
          JOptionPane.ERROR_MESSAGE);
    }
  
  }

  public void createNewConception() {
    String notebookRef = getNotebookRef();
    if (notebookRef != null && !notebookRef.equals(""))
      createNewConceptionExecutor(notebookRef);
  }

  private String getNotebookRef() {
    SpeedBarNodeInterface selectedNodes[] = this.getGUIComponent().getSelectedSpeedBarObjects();
    SpeedBarNotebook selectedNotebook = null;
    String notebookRef = "";
    if (selectedNodes != null) {
      for (int i = 0; i < selectedNodes.length; i++) {
        if (selectedNodes[i] instanceof SpeedBarNotebook) {
        	notebookRef = ((SpeedBarNotebook) selectedNodes[i]).getNotebook();
          break;
        } else if(selectedNodes[i] instanceof SpeedBarPageGroup) {
        	notebookRef = ((SpeedBarPageGroup) selectedNodes[i]).getNotebook();
            break;
        } else if(selectedNodes[i] instanceof SpeedBarPage) {
        	notebookRef = ((SpeedBarPage) selectedNodes[i]).getNotebook();
        }
      }
    }
    if (CommonUtils.isNull(notebookRef))
      notebookRef = getGUIComponent().getNotebookIfOnlyOneExists();
    if (CommonUtils.isNull(notebookRef)) {
      JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
          "Could not determine which Notebook to create experiment.\n"
              + "Select desired notebook and select Create New Experiment.", "New Experiment Error",
          JOptionPane.ERROR_MESSAGE);
    }
    return notebookRef;
  }

  public void createNewConceptionExecutor(String lastNotebook) {
    final String lastNotebookRef = lastNotebook + "-0000";// Just to pass
    // the validation logic.
    // allow page types to be disabled
    if (isPropertyEnabled(CeNSystemXmlProperties.PROP_NEW_CONCEPTION_ENABLED) == true
        || MasterController.getUser().isSuperUser() == true) 
    {
      SwingWorker worker = new SwingWorker() {
        final String progressStatus = "Creating New Conception Record ...";
  
        public Object construct() {
          CeNJobProgressHandler.getInstance().addItem(progressStatus);
          NotebookPageModel pageModel = null;
          NotebookUser user = MasterController.getUser();
          try {
        	StorageDelegate service = ServiceController.getStorageDelegate(user.getSessionIdentifier());
            NotebookRef lastNbRef = new NotebookRef(lastNotebookRef);
            pageModel = service.createConceptionRecord(user.getNTUserID(), lastNbRef.getNbNumber(),MasterController.getUser().getSessionIdentifier());
            
            boolean useTA = Boolean.parseBoolean(MasterController.getUser().getPreference(NotebookUser.PREF_UseTA));
            
            if (useTA) {
            	pageModel.setTaCode(MasterController.getUser().getPreference(NotebookUser.PREF_TA));
            	pageModel.setProjectCode(MasterController.getUser().getPreference(NotebookUser.PREF_PC));
            } else {
            	pageModel.setTaCode(MasterController.getUser().getPreference(NotebookUser.PREF_LastTA));
            	pageModel.setProjectCode(MasterController.getUser().getPreference(NotebookUser.PREF_LastProject));
            }
            
            NBKPageUpdateManager manager = NBKPageUpdateManager.getNBKPageUpdateManagerInstance(MasterController.getUser().getSessionIdentifier());
            manager.updateNotebookPageData(pageModel);
            
            pageModel.setModelChanged(false);
          } catch (InvalidNotebookRefException inve) {
            log.error("Failed to load notebook reference: " + lastNotebookRef, inve);
          } catch (Exception se) {
            log.error("Storage Service Failure: Failed to load notebook reference: " + lastNotebookRef, se);
          }

          CeNJobProgressHandler.getInstance().removeItem(progressStatus);
          return pageModel;
        };
  
        public void finished() {
          CeNJobProgressHandler.getInstance().removeItem(progressStatus);
          NotebookPageModel pageModel = (NotebookPageModel) get();
          NotebookUser user = MasterController.getUser();
          if (user != null) {
            if (StringUtils.isNotBlank(lastNotebookRef)) {
              NotebookRef lastNbRef = null;
              try {
                lastNbRef = new NotebookRef(lastNotebookRef);
              } catch (Exception e) {
                log.error("Failed getting notebook reference object for lastNotebookRef = " + lastNotebookRef);
              }
              ConceptionNotebookPageGUI conceptionNotebookPageGUI = new ConceptionNotebookPageGUI(pageModel);
              addNotebookPageGuiInterfaceToGUI(conceptionNotebookPageGUI);
              addNotebookPageToSpeedBar(pageModel);
              
              String firstExp = pageModel.getNbRef().toString();
              try {
                MasterController.getUser().setPreference(NotebookUser.PREF_CurrentNbRef, firstExp);
                MasterController.getUser().setPreference(NotebookUser.PREF_CurrentNbRefVer, "0");
                MasterController.getUser().setPreference(NotebookUser.PREF_LastNBRef, firstExp);
                MasterController.getUser().updateUserPrefs();
              } catch (UserPreferenceException e) {
                ceh.logExceptionWithoutDisplay(e, null);
              }
              PageChangesCache.storePage(pageModel);
            }
          }
        }
      };
      worker.start();
      worker.get();
    } else {
      log.info("Conception Record creation disabled");
      JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
          "Conception Record creation disabled.  No conception record will be created.", "New Conception Record Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  public void createNewSingletonExperiment() {
    String notebookRef = getNotebookRef();
    if (StringUtils.isNotBlank(notebookRef))
      createNewSingletonExperimentExecutor(notebookRef);
  }

  /**
   * Defaults to true if no property value is found.  
   * 
   * @param propertyXPath
   * @return
   */
  public static boolean isPropertyEnabled(String propertyXPath) {
    boolean enabled = true;
    if (StringUtils.isNotBlank(propertyXPath)) {
      try {
        String shouldEnable = CeNSystemProperties.getCeNSystemProperty(propertyXPath);
        if(StringUtils.isNotBlank(shouldEnable)) {
          enabled = CeNSystemXmlProperties.TRUE_VALUES_LIST.contains(shouldEnable);
        } else {
          // we don't really care to know the flag is not set.
        }
      } catch (Exception e) {
        log.error("Failed check for property: " + propertyXPath + "\n" +
                  "Defaulting to enabled = true.", e);
      }
    }
    return enabled;
  }
  public void createNewSingletonExperimentExecutor(String notebook) {
    final String lastNotebookRef = notebook + "-0000";// Just to pass the
    // validation logic.
    // allow ability to shut off portions of CeN.
    if (isPropertyEnabled(CeNSystemXmlProperties.PROP_NEW_SINGLETON_ENABLED) == true
        || MasterController.getUser().isSuperUser() == true) 
    {
      new SwingWorker() {
        final String progressStatus = "Creating New Singleton Experiment ...";

        public Object construct() {
          CeNJobProgressHandler.getInstance().addItem(progressStatus);
          NotebookUser user = MasterController.getUser();
          NotebookPageModel pageModel = null;

          try {
        	StorageDelegate service = ServiceController.getStorageDelegate(user.getSessionIdentifier());
            // **TODO** Ensure can't create exp in closed notebook,
            // ensure can create first experiment
            NotebookRef lastNbRef = new NotebookRef(lastNotebookRef);
            
            pageModel = service.createSingletonExperiment(user.getNTUserID(), lastNbRef.getNbNumber(),MasterController.getUser().getSessionIdentifier());
            
            boolean useTA = Boolean.parseBoolean(MasterController.getUser().getPreference(NotebookUser.PREF_UseTA));
            
            if (useTA) {
            	pageModel.setTaCode(MasterController.getUser().getPreference(NotebookUser.PREF_TA));
            	pageModel.setProjectCode(MasterController.getUser().getPreference(NotebookUser.PREF_PC));
            } else {
            	pageModel.setTaCode(MasterController.getUser().getPreference(NotebookUser.PREF_LastTA));
            	pageModel.setProjectCode(MasterController.getUser().getPreference(NotebookUser.PREF_LastProject));
            }
            
            NBKPageUpdateManager manager = NBKPageUpdateManager.getNBKPageUpdateManagerInstance(MasterController.getUser().getSessionIdentifier());
            manager.updateNotebookPageData(pageModel);
            
            pageModel.setModelChanged(false);
            // System.out.println("pageModel.getPageType() "+pageModel.getPageType());
          } catch (InvalidNotebookRefException inve) {
            log.error("Failed to create notebook reference for: " + lastNotebookRef, inve);
          } catch (Exception se) {
            log.error("Failed to access CeN Storage Service", se);
          }
          CeNJobProgressHandler.getInstance().removeItem(progressStatus);
          return pageModel;
        };

        public void finished() {
          CeNJobProgressHandler.getInstance().removeItem(progressStatus);
          NotebookPageModel pageModel = (NotebookPageModel) get();
          NotebookUser user = MasterController.getUser();
          if (user != null) {
            if (lastNotebookRef != null) {
              NotebookRef lastNbRef = null;
              try {
                lastNbRef = new NotebookRef(lastNotebookRef);
              } catch (Exception e) {
                log.error("Failed to create notebook reference for: " + lastNotebookRef, e);
              }

              SingletonNotebookPageGUI singletonNotebookPageGUI = new SingletonNotebookPageGUI(pageModel);
              addNotebookPageGuiInterfaceToGUI(singletonNotebookPageGUI);
              if (mGui != null)
                addNotebookPageToSpeedBar(pageModel);

              String firstExp = ((NotebookPageModel)get()).getNbRef().toString();             
              try {
                MasterController.getUser().setPreference(NotebookUser.PREF_CurrentNbRef, firstExp);
                MasterController.getUser().setPreference(NotebookUser.PREF_CurrentNbRefVer, "0");
                MasterController.getUser().setPreference(NotebookUser.PREF_LastNBRef, firstExp);
                MasterController.getUser().updateUserPrefs();
              } catch (UserPreferenceException e) {
                ceh.logExceptionWithoutDisplay(e, "Failed updating user preferences for current and last notebook reference.");
              }
              PageChangesCache.storePage(pageModel);
            }
          }
        }
      }.start();
    } else {
      log.info("Singleton/Med-Chem Experiment creation disabled");
      JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
          "Singleton/Med-Chem Experiment creation disabled.  No experiment will be created.", "New Experiment Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

	public NotebookPageGuiInterface getLoadedPageGui(String siteCode,
			NotebookRef nbRef, int version) {
		JInternalFrame[] frames = MasterController.getGUIComponent()
				.getDesktopWindows();

		for (JInternalFrame frame : frames) {
			if (frame instanceof NotebookPageGuiInterface) {
				NotebookPageGuiInterface notebookPageGUI = (NotebookPageGuiInterface) frame;
				if (version >= 1) {
					if (notebookPageGUI.getPageModel().isSameNotebook(
							nbRef.getNotebookRef(), version)) {
						return notebookPageGUI;
					}
				} else {
					if (notebookPageGUI.getPageModel().isLatestVesrion(
							nbRef.getNotebookRef())) {
						return notebookPageGUI;
					}
				}
			}
		}
		return null;
	}

  // TODO: redo threading to keep GUI active during save process.
  // 1. Copy the page and pass the copy to the thread for saving. Saves GUI
  // locking.
  // 2. Indicate that this page is being saved and shouldn't be saved
  // again until we come back from previous save.
  // 3. Create a callback routine to update the notebook page saving
  // status to false once complete
  public boolean _saveExperiment(final NotebookPage nbPage, final boolean updateUserPreferences, final boolean retrySave) {
    boolean doSave = true;
    // waitOnSave();
    if (!nbPage.isSaving() && nbPage.isModelChanged() || retrySave) {
      try {
        // Check modified date in the database
        // TODO: isDBExperimentModified should be threaded.
        if (nbPage.existsInDB() && nbPageCache.isDBExperimentModified(nbPage))
          if (!continueSave(nbPage.getNotebookRefAsString())) {
            saveComplete();
            doSave = false;
          }
      } catch (NotebookDelegateException e2) {
        saveComplete();
        doSave = false;
        CeNErrorHandler.getInstance().logExceptionMsg(null, e2);
      }
      if (doSave) {
        Object objLatch = new Object();
        log.debug("saveExperiment for " + nbPage.getNotebookRefAsString());
        mGui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        // We need to run the save in a new thread but lock the GUI
        // until
        // the updates to the client rowset are complete
        final NotebookPage tempPage = nbPage;
        nbPageCache.setObjectLatch(objLatch);
        final String progressStatus = "Saving Experiment " + tempPage.getNotebookRefAsString() + " ...";
        /*
         * Following thread has been created out of SwingWorker thread's construct() method. Due the spin lock between the
         * main thread and the SwingWorker, the finished() method from SwingWorker thread is not called by the EventQueue.
         * In order to handle this situation The following Thread has been introduced. SwingWorker finished() method is
         * converted into construct method in finisher thread and called at the end of the saveWorker thread.
         */
        Thread saveWorker = new Thread() {
          NotebookPageGuiInterface pageGUI = null;
          boolean blnSuccess = false;

          public void run() {
            if (tempPage != null) {
              if (tempPage.isModelChanged()) {
                try {
                  CeNJobProgressHandler.getInstance().addItem(progressStatus);
                  blnSuccess = nbPageCache.saveNotebookPage(tempPage, false);
                } catch (NotebookDelegateException e) {
                  CeNErrorHandler.getInstance().logExceptionMsg(null, e);
                }
                if (updateUserPreferences) {
                  try {
                    MasterController.getUser().setPreference(NotebookUser.PREF_CurrentNbRef,
                        tempPage.getNotebookRefAsString());
                    MasterController.getUser().setPreference(NotebookUser.PREF_CurrentNbRefVer,
                        "" + tempPage.getVersion());
                    MasterController.getUser().updateUserPrefs();
                  } catch (UserPreferenceException e1) { /* Ignored */
                  }
                }
                pageGUI = (NotebookPageGuiInterface) GuiController.this.getLoadedPageGui(tempPage.getSiteCode(), tempPage
                    .getNotebookRef(), tempPage.getVersion());
                if (pageGUI != null) {
                  if (blnSuccess) {
                    if (pageGUI.isClosing())
                      MasterController.getGuiController().removeNotebookPageFromGUI(pageGUI);
                  } else {
                    pageGUI.setClosing(false);
                    pageGUI.setEnabled(true);
                    // pageGUI.setGlassPane(null);
                  }
                }
                mGui.refreshIcons();
              }
            } else {
              CeNErrorHandler.getInstance().logErrorMsg(null, "Attempt to save a page not currently loaded.",
                  "Experiment Save Error");
            }
            // Starting the SwingWorker to make a clear exit from
            // save.
            finisher.start();
          }

          // Nested Class within saveWorker thread.
          SwingWorker finisher = new SwingWorker() {
            public void finished() {
              // do nothing
            }

            public Object construct() {
              System.out.println("Save Finisher doing the clean exit.");
              try {
                CeNJobProgressHandler.getInstance().removeItem(progressStatus);
                if (pageGUI != null) {
                  pageGUI.setEnabled(true);
                }
                if (!blnSuccess) {
                  if (tempPage.getStatus() != CeNConstants.PAGE_STATUS_COMPLETE) {
                    int selection = JOptionPane.showConfirmDialog(mGui,
                        "There was a problem saving experiment " + tempPage.getNotebookRefAsString()
                            + " to the database. Do you want to retry the save? ",
                        "Middle tier failure", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (selection == JOptionPane.YES_OPTION) {
                      // TODO bo
                      // saveExperiment(tempPage, false,
                      // true);
                    }
                  } else {
                    tempPage.setStatus(CeNConstants.PAGE_STATUS_OPEN);
                  }
                }
                tempPage.setSavingFlag(false);
                mGui.refreshIcons();
              } catch (Exception error) {
                log.error("Failed to update gui with save status.", error);
              }
              return "";
            }
          };
        };
        saveWorker.start();
        if (nbPage.isModelChanged()) {
          synchronized (objLatch) {
            try {
              objLatch.wait();
            } catch (InterruptedException ie) {
              // Ignored
            } finally {
              CeNJobProgressHandler.getInstance().removeItem(progressStatus);
            }
          }
        }
        saveComplete();
        nbPageCache.setObjectLatch(null);
        mGui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        // if the page is no longer saving there has been an error
        return nbPage.isSaving();
//        if (!nbPage.isSaving())
//          return false;
//        else
//          return true;
      } else
        return false;
    } else {
      saveComplete();
      return false;
    }
  }

  public void printExperiments(ArrayList<PrintRequest> list) {
    PrintExperimentSetup.displayPrintExperimentsDialog(MasterController.getGuiController().getGUIComponent(), list);
  }

  public void printNotebooks(ArrayList list) {
    Object[] params = (Object[]) list.get(0);
    log.debug("printNotebook for " + params[0] + ", " + params[1]);
    // TODO: should go up to server? if not pages can be loaded w/ just model & no GUI
  }

  public void printPageGroups(ArrayList list) {
    Object[] params = (Object[]) list.get(0);
    log.debug("printPageGroup for " + params[0] + ", " + params[1] + ", " + params[2]);
    // TODO: should go up to server? if not pages can be loaded w/ just
    // model & no GUI
  }

  public void repeatExperiment(final String siteCode, String users_fullname, final String notebook, final String page,
      final int version) {
    System.out.println("repeatExperiment for " + siteCode + ", " + users_fullname + ", " + notebook + ", " + page);
    final String progressStatus = "Repeating Experiment " + notebook + "-" + page + "...";
    CeNJobProgressHandler.getInstance().addItem(progressStatus);
    SwingWorker worker = new SwingWorker() {
      public Object construct() {
        boolean blnSuccess = false;
        NotebookRef sourcenbRef = null;
        NotebookPageModel pageModelSrc = null;
        NotebookPageModel pageModelNew = null;
        try {
          sourcenbRef = new NotebookRef(notebook + "-" + page);
        } catch (Exception e) {
          ceh.logErrorMsg(getGUIComponent(), "Invalid Notebook reference", "Invalid Notebook Ref",
              JOptionPane.ERROR_MESSAGE);
          CeNJobProgressHandler.getInstance().removeItem(progressStatus);
          return null;
        }

        CeNJobProgressHandler.getInstance().removeItem(progressStatus);
        final String progressStatus = "Loading Experiment " + sourcenbRef.toString() + " ...";
        CeNJobProgressHandler.getInstance().addItem(progressStatus);

		if (sourcenbRef != null) {
			NotebookUser user = MasterController.getUser();
			try {
				NotebookPageGuiInterface loadedInterface = getLoadedPageGui(siteCode, sourcenbRef, version);
				if (loadedInterface != null) {
					pageModelSrc = loadedInterface.getPageModel();
				} else {
					StorageDelegate storageDelegate = ServiceController.getStorageDelegate(user.getSessionIdentifier());
					pageModelSrc = storageDelegate.getNotebookPageExperimentInfo(sourcenbRef, siteCode,MasterController.getUser().getSessionIdentifier());
					ProcedureImagesUpdateManager.updateProcedureOnLoad(pageModelSrc);
				}
			} catch (Exception e) {
				ceh.logExceptionMsg(getGUIComponent(), "Failed to retrieve source experiment", e);
				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
				return null;
			}
          CeNJobProgressHandler.getInstance().removeItem(progressStatus);

          if (pageModelSrc != null && user != null) {
            NotebookRef lastNbRef = null;
            try {
              String lastNbRefStr = user.getPreference(NotebookUser.PREF_CurrentNbRef);
              lastNbRef = new NotebookRef(lastNbRefStr);
              // lastNbRef.deepCopy(pageModelSrc.getNbRef());
              // if (lastNotebook == null || lastNotebook.length()
              // == 0 ||
              // !NotebookPageUtil.isValidNotebookRef(lastNotebook))
              // {
              // SpeedBarHandler hndlr =
              // MasterController.getGUIComponent().getActiveSpeedBarHandler();
              // String[] nbs =
              // hndlr.getNotebooksForUser(user.getSiteCode(),
              // user.getFullName());
              // //TODO: Need to pic nb if only one or list
              // notebooks or error if none
              // }
              /*
               * try { lastNbRef = new NotebookRef(lastNotebook); } catch (Exception e) { }
               */
              if (lastNbRef != null) {
                ValidationInfo vInfo = null;
                try {
                  vInfo = nbDel.validateNotebook(user.getSiteCode(), lastNbRef.getNbNumber(), null);
                } catch (Exception e) {
                  vInfo = null;
                  log.error("repeatExperiment failed to validate notebook: " + lastNbRef.getNbNumber());
                }

                if (vInfo != null && vInfo.status != null && vInfo.status.equals("COMPLETE")) {
                  JOptionPane.showMessageDialog(
                          MasterController.getGUIComponent(),
                          "Notebook "
                              + lastNbRef.getNbNumber()
                              + " is Complete.  No new experiments may be added to that Notebook.\n"
                              + "Right-Click on another notebook and select 'Create New Experiment' to establish a new working notebook.",
                          "Repeat Experiment Error", JOptionPane.ERROR_MESSAGE);
                } else {
                  String nextExperiment = getNextExperimentInNotebook(user.getSiteCode(), lastNbRef.getNbNumber());
                  if (nextExperiment == null || nextExperiment.length() == 0) {
                    JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Notebook "
                        + lastNbRef.getNbNumber() + " is invalid or full.", "Repeat Experiment Error",
                        JOptionPane.ERROR_MESSAGE);
                    return null;
                  }

                  final String progressStatus2 = "Preparing and saving experiment " + nextExperiment.toString()
                      + " ...";
                  CeNJobProgressHandler.getInstance().addItem(progressStatus2);

                  pageModelNew = new NotebookPageModel();
                  pageModelNew.setNbRef(new NotebookRef(nextExperiment));
                  pageModelNew.getPageHeader().setBatchOwner(user.getNTUserID());
                  pageModelNew.getPageHeader().setSiteCode(user.getSiteCode());
                  pageModelNew.getPageHeader().setBatchCreator(user.getNTUserID());
                  pageModelNew.getPageHeader().setUserName(user.getNTUserID());
                  RepeatExpHelper.copyDesign(pageModelSrc, pageModelNew);

                  if (pageModelNew.getPageType().equals(CeNConstants.PAGE_TYPE_PARALLEL)) {
                    pageModelNew.getPageHeader().setTotalReactionSteps(pageModelNew.getReactionSteps().size());

                    ParallelExpDuperDeDuper parallelExpDuperDeDuper = new ParallelExpDuperDeDuper();
                    pageModelNew = parallelExpDuperDeDuper.deDupeParallelExperiment(pageModelNew);

                    ParallelExpModelUtils parallelExpModelUtils = new ParallelExpModelUtils(pageModelNew);
                    populateProductIdForProducts(pageModelNew, pageModelSrc);
                    parallelExpModelUtils.setOrRefreshGuiPseudoProductPlate();
                    parallelExpModelUtils.linkProductBatchesAnalyticalModelInAnalysisCache();
                    parallelExpModelUtils.setPrecursorsForProductsAndMonomerBatches();
                    parallelExpModelUtils.linkBatchesWithPlateWells();
                    parallelExpModelUtils.populateMonomerAndProductHashMaps();
                    parallelExpModelUtils.updateBatchesListAmountFlags();
                    RepeatExpHelper.setNotebookBatchNumbers(pageModelNew);
                    pageModelNew.getPseudoProductPlate(true);// refresh this
                    pageModelNew.onLoadonAfterSaveSetModelChanged(false, true);// Reset all flags.
                  } else if (pageModelNew.getPageType().equals(CeNConstants.PAGE_TYPE_CONCEPTION)) {
                    ParallelExpModelUtils utils = new ParallelExpModelUtils(pageModelNew);
                    // Populate Intended Products area for Conception pages.
                    populateProductIdForProducts(pageModelNew, pageModelSrc);
                    RepeatExpHelper.setNotebookBatchNumbers(pageModelNew);
                    utils.populateMonomerAndProductHashMaps();
                    utils.linkProductBatchesAnalyticalModelInAnalysisCache();
                  } else if (pageModelNew.getPageType().equals(CeNConstants.PAGE_TYPE_MED_CHEM)) {
                    ParallelExpModelUtils utils = new ParallelExpModelUtils(pageModelNew);
                    // why does this seem to work for populating the monomers?
                    populateProductIdForProducts(pageModelNew, pageModelSrc);
                    RepeatExpHelper.setNotebookBatchNumbers(pageModelNew);
                    utils.populateMonomerAndProductHashMaps();
                    utils.linkProductBatchesAnalyticalModelInAnalysisCache();
// removed the two lines below because they were not tested on check-in to remove limitation on using singleton pages
//                    pageModelNew.getPseudoProductPlate(true);// refresh this
//                    pageModelNew.onLoadonAfterSaveSetModelChanged(true, true);// Reset all flags.
                  }
                  markReactionStepsForInsertingToCus(pageModelNew);
                  
                  pageModelNew.setStatus(CeNConstants.PAGE_STATUS_OPEN);

                  try {
                    pageModelNew.setCompletionDateAsTimestamp(null);
                  } catch (Exception e) {
                    log.error("failed to set completion date to null", e);
                  }
                  
                  // For testing Save is commented. Pls
                  // uncomment it //**TODO
                  try {
                    log.debug("Repeat experiment Page model");
                    StorageDelegate storageDelegate = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
                    storageDelegate.addNotebookPage(pageModelNew,MasterController.getUser().getSessionIdentifier());
                    ProcedureImagesUpdateManager.updateProcedureOnLoad(pageModelNew);
                    log.debug("Repeat experiment Page model save sucessfull.");
                  } catch (Exception e) {
                    JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
                        "Problem saving Repeat experiment page", "Repeat experiment Experiment Error",
                        JOptionPane.ERROR_MESSAGE);
                    ceh.logExceptionMsg(MasterController.getGUIComponent(), e);
                    CeNJobProgressHandler.getInstance().removeItem(progressStatus2);
                    return null;
                  }
                  CeNJobProgressHandler.getInstance().removeItem(progressStatus2);
                  SpeedBarHandler hndlr = (SpeedBarHandler) MasterController.getGUIComponent()
                      .getActiveSpeedBarHandler();
                  hndlr.refreshCurrentNode();
                  blnSuccess = true;
                }
              } // if there is NB # in user prefers and it is
              // valid
            } catch (Exception e) {
              ceh.logExceptionMsg(MasterController.getGUIComponent(), e);
            }
          }
        } else
          CeNErrorHandler.showMsgOptionPane(MasterController.getGUIComponent(), "Repeat Experiment Error",
              "Unable to determine experiment that is to be repeated", JOptionPane.ERROR_MESSAGE);
        return (blnSuccess) ? pageModelNew : null;
      }

      public void finished() {
        try {
          NotebookPageModel pageModelNew = (NotebookPageModel) get();
          if (pageModelNew != null) {
            NotebookPageGuiInterface internalFrame = null;
            if (pageModelNew.getPageType().equals(CeNConstants.PAGE_TYPE_CONCEPTION))
              internalFrame = new ConceptionNotebookPageGUI(pageModelNew);
            else if (pageModelNew.getPageType().equals(CeNConstants.PAGE_TYPE_PARALLEL))
              internalFrame = new ParallelNotebookPageGUI(pageModelNew);
            else if (pageModelNew.getPageType().equals(CeNConstants.PAGE_TYPE_MED_CHEM))
              internalFrame = new SingletonNotebookPageGUI(pageModelNew);
            addNotebookPageGuiInterfaceToGUI(internalFrame);
            addNotebookPageToSpeedBar(pageModelNew);

            String firstExp = pageModelNew.getNbRef().toString();
            try {
              MasterController.getUser().setPreference(NotebookUser.PREF_CurrentNbRef, firstExp);
              MasterController.getUser().setPreference(NotebookUser.PREF_CurrentNbRefVer, "0");
              MasterController.getUser().setPreference(NotebookUser.PREF_LastNBRef, firstExp);
              MasterController.getUser().updateUserPrefs();
            } catch (UserPreferenceException e) {
              ceh.logExceptionWithoutDisplay(e, null);
            }
          }
        } catch (Exception e) {
          ceh.logExceptionMsg(MasterController.getGUIComponent(), e);
        }
        CeNJobProgressHandler.getInstance().removeItem(progressStatus);
      }
    };
    worker.start();
  }
  
  private void markReactionStepsForInsertingToCus(NotebookPageModel pageModel) {
	  if(pageModel != null) {
		  for (ReactionStepModel reacModel : pageModel.getReactionSteps()) {
			  ReactionSchemeModel scheme = reacModel.getRxnScheme();
			  if (scheme != null) {
				  scheme.setToInsertToCus(true);
			  }
		  }
	  }
  }

  public void markCompleteExperiment(final String site, final String users_fullname, final String notebook, final String page,
      final int version, final SpeedBarPage sbp) {
    final String progressStatus = "Preparing to Complete Experiment " + notebook + "-" + page + "...";
    CeNJobProgressHandler.getInstance().addItem(progressStatus);

    SwingWorker worker = new SwingWorker() {
      public Object construct() {
        boolean useSig = MasterController.getUser().isEsigEnabled();
        final SignatureHandler sigHandler = new SignatureHandler();
        if (useSig) {
          int status = sigHandler.canDoSubmissions();
          if (status != 1) {
            if (status == -2)
              JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
                  "Submission to Signature Service is currently unavailable, please try again later.", "Experiment Republish",
                  JOptionPane.ERROR_MESSAGE);
            sbp.setCompleteInProgress(false);
            return null;
          }
          if (!sigHandler.hasTemplates()) {
            Object[] options = { "OK", "Goto Signature Service" };
            int result = JOptionPane.showOptionDialog(MasterController.getGUIComponent(),
                "Electronic Signature is enabled, however, you have no Signature Templates defined\n"
                    + "that contain at least one signature block named 'Author'.\n\n"
                    + "Please go to Signature Service and define a template that meets these criteria then try again.\n"
                    + "If you do not wish to use eSigs, you can go to Tools | Options and disable it.",
                "Experiment Completion", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options,
                null);
            if (result == JOptionPane.NO_OPTION)
              sigHandler.launchSAFESign();
            sbp.setCompleteInProgress(false);
            return null;
          }
        }
        int selection = JOptionPane.showConfirmDialog(mGui, "Are you sure you want to mark experiment " + notebook + "-"
            + page + " Complete?", "Experiment Completion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (selection == JOptionPane.YES_OPTION) {
          TemplateVO template = null;
          if (useSig) {
            template = sigHandler.selectTemplate();
            if (template == null) {
              sbp.setCompleteInProgress(false);
              return null;
            }
          }

          NotebookRef nbRef = null;
          String siteCode = convertSiteNameToCode(site);
          try {
            nbRef = new NotebookRef(notebook + "-" + page);
            nbRef.setVersion(version);
          } catch (Exception e) {
            ceh.logExceptionMsg(MasterController.getGUIComponent(), e);
            sbp.setCompleteInProgress(false);
            return null;
          }

          CeNJobProgressHandler.getInstance().removeItem(progressStatus);

          // if page is in cache, update gui, then update speedbar if page is not in cache, load just exp page info then
          // do above
          NotebookPageGuiInterface notebookPageGUI = getLoadedPageGui(siteCode, nbRef, version);
          if (notebookPageGUI == null) {
            String progressStatus = "Loading Experiment " + nbRef.toString() + " ...";
            CeNJobProgressHandler.getInstance().addItem(progressStatus);

            NotebookPageModel pageModel = null;
            try {
              StorageDelegate storageDelegate = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
              // NotebookPageModel pageModel = null;
              // String lastNotebook = MasterController.getUser().getPreference(NotebookUser.PREF_LastNBRef);
              //TODO try to eliminate getting pageModel 
              pageModel = storageDelegate.getNotebookPageExperimentInfo(nbRef, siteCode,MasterController.getUser().getSessionIdentifier());
              ProcedureImagesUpdateManager.updateProcedureOnLoad(pageModel);
            } catch (Exception e) {
              JOptionPane.showOptionDialog(MasterController.getGUIComponent(),
                  "Unexpected error occured while opening the notebook. Please try again later",
                  "Notebook error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
              CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(),
                  "Unexpected error occured while opening the notebook. ", e);
            }

            // Availability of Auto-Save file has to be check here.
            if (pageModel == null) {
              CeNJobProgressHandler.getInstance().removeItem(progressStatus);
              CeNErrorHandler.getInstance().logErrorMsg(MasterController.getGUIComponent(),
                  "changeStatus: Failed to load Page", "");
              sbp.setCompleteInProgress(false);
            } else {
              CeNJobProgressHandler.getInstance().removeItem(progressStatus);

              //TODO move checkTherapeuticAreaAndProjectCodeBeforeCompletion above - it seems it should be first condition
              if (pageModel != null && checkTherapeuticAreaAndProjectCodeBeforeCompletion(pageModel, useSig)) {
                pageModel.setChanging(true);
                progressStatus = "Completing experiment " + pageModel.getNotebookRefAsString() + " ...";
                CeNJobProgressHandler.getInstance().addItem(progressStatus);
                if (!completeExperiment(pageModel, template)) {
                  sbp.setCompleteInProgress(false);
                } else {
                	launchSignatureUrl(nbRef, siteCode);
                }
                CeNJobProgressHandler.getInstance().removeItem(progressStatus);
              } else
                sbp.setCompleteInProgress(false);
            }
            // CompleteTimer.startTimer(pageModel, notebookPageGUI);
          } else {
            NotebookPageModel pageModel = notebookPageGUI.getPageModel();
            if (pageModel == null || !checkTherapeuticAreaAndProjectCodeBeforeCompletion(pageModel, useSig)) {
              sbp.setCompleteInProgress(false);
              return null; // if null then page has been reserved but not loaded, ignore request
            }

            if (notebookPageGUI != null && pageModel.isModelChanged()) {
              String progressStatus = "Saving experiment " + pageModel.getNotebookRefAsString() + " ...";
              CeNJobProgressHandler.getInstance().addItem(progressStatus);
              saveExperiment(pageModel);
              CeNJobProgressHandler.getInstance().removeItem(progressStatus);
            }

            pageModel.setChanging(true);
            String progressStatus = "Completing experiment " + pageModel.getNotebookRefAsString() + " ...";
            CeNJobProgressHandler.getInstance().addItem(progressStatus);
            if (!completeExperiment(pageModel, template)) {
              sbp.setCompleteInProgress(false);
        	} else {
        		launchSignatureUrl(nbRef, siteCode);
        	}
            CeNJobProgressHandler.getInstance().removeItem(progressStatus);
          }
        } else
          sbp.setCompleteInProgress(false);
        return null;
      }

      public void finished() {
        CeNJobProgressHandler.getInstance().removeItem(progressStatus);
      }
    };
    worker.start();
  }
  
	private boolean checkTherapeuticAreaAndProjectCodeBeforeCompletion(NotebookPageModel pageModel, boolean useSig) {
		if (useSig) {
			boolean isTaCode = CommonUtils.isNotNull(pageModel.getTaCode());
			boolean isProjectCode = CommonUtils.isNotNull(pageModel.getProjectCode());
			if (!isTaCode || !isProjectCode) {
				boolean both = !isTaCode && !isProjectCode;
				String fields = both ? "Fields " : "Field ";
				String therapeuticArea = !isTaCode ? "'Therapeutic Area' " : "";
				String and = both ? "and " : "";
				String projectCode = !isProjectCode ? "'Project code' " : "";
				String message = fields + therapeuticArea + and + projectCode + "should be filled before submission.";
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), message, "Experiment Completion", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		}
		return true;
	}
  
  private void launchSignatureUrl(NotebookRef nbRef, String siteCode) {
	  NotebookPageModel pageModel = null;
	  SignatureHandler sigHandler = new SignatureHandler();
	  try {
		//take the page model from db with new signature url  
		pageModel = getStorageDelegate().getNotebookPageExperimentInfo(nbRef, siteCode,MasterController.getUser().getSessionIdentifier());
	  } catch (Exception e) {}
	  if (CommonUtils.isNotNull(pageModel.getSignatureUrl()) && sigHandler.shouldLaunchUrl()) {
		  SignatureHandler.fixAndLaunchUrl(pageModel.getSignatureUrl().toString());
	  }
  }
  
  private StorageDelegate getStorageDelegate() {
	  try {
		return ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
	} catch (StorageInitException e) {
		e.printStackTrace();
		return null;
	}
  }

  private boolean completeExperiment(NotebookPageModel pageModel, TemplateVO template) {
    boolean result = false;
    StorageDelegate delegate = null;
    try {
      delegate = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());

      String pageStatusToSave = null;

      if (template != null) {
    	  pageStatusToSave = CeNConstants.PAGE_STATUS_SUBMITTED;
      } else {
    	  pageStatusToSave = CeNConstants.PAGE_STATUS_COMPLETE;
      }
      // Set Completion date on PageModel fix for CEN-1088
      pageModel.setCompletionDateAsTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));
      ProcedureImagesUpdateManager.updateProcedureOnSave(pageModel);
      delegate.updateNotebookPageModel(pageModel,MasterController.getUser().getSessionIdentifier()); // Page model already saved, just update modification timestamp
      
	  delegate.updateNotebookPageStatus(	pageModel.getSiteCode(),
				pageModel.getNbRef().getNbRef(),
				pageModel.getVersion(),
				pageStatusToSave);
	  result = delegate.completeExperiment(	pageModel.getSiteCode(),
             	pageModel.getNbRef().getNbRef(),
             	pageModel.getVersion(),
             	template,
             	MasterController.getUser(), TimeZone.getDefault().getID());
	  
	  if (result && template != null) {
		  TimerStatusHandler.getInstance().addSignatureTask(pageModel.getNotebookRefAsString());
	  }
	  
	  String status = delegate.getNotebookPageCompleteStatus(pageModel.getSiteCode(), pageModel.getNotebookRefAsString(), pageModel.getVersion());
	  
	  TimerStatusHandler.updateSpeedBarWithStatus(status, pageModel.getSiteCode(), MasterController.getUser().getFullName(), pageModel.getNbRef(), pageModel.getVersion());

	  NotebookPageGuiInterface loadedPage = MasterController.getGuiController().getLoadedPageGui(pageModel.getSiteCode(), pageModel.getNbRef(), pageModel.getVersion());
	  if (loadedPage != null) {
		  loadedPage.dispose();
		  MasterController.getGuiController().openPCeNExperiment(pageModel.getSiteCode(), pageModel.getNbRef().getNbNumber(), pageModel.getNbRef().getNbPage(), pageModel.getVersion());
	  }
    } catch (Exception e) {
        if (delegate != null) {
        	try {
				delegate.updateNotebookPageStatus(	pageModel.getSiteCode(),
						pageModel.getNbRef().getNbRef(),
						pageModel.getVersion(),
						CeNConstants.PAGE_STATUS_OPEN);
        	} catch (Exception e1) {
                log.error("Completing experiment error", e1);
        	}
        }
        errorCompleting(pageModel, false, e);
    }
    return result;
  }

  	public static void errorCompleting(NotebookPageModel pageModel, boolean reSubmit, Exception e) {
  		Throwable cause = e;
  		
  		while (cause.getCause() != null) {
  			cause = cause.getCause();
  		}
      
  		String comp = (reSubmit ? "re-submitting" : "completing");
  		
  		if (StringUtils.contains(cause.getMessage(), CeNConstants.REPORT_STRUCTURE_LOAD_ERROR)) {
  			String msg = "Error " + comp + " experiment:\nError creating experiment report - Unable to load structure image(s)";
  			
  			msg += "\n\n";
  			msg += "Error message from server:\n";
  			msg += cause.getMessage();
  			
  			JOptionPane.showMessageDialog(MasterController.getGuiComponent(), msg, "Notebook error", JOptionPane.ERROR_MESSAGE);
  			CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, msg);
  			return;
  		}
      
  		JOptionPane.showMessageDialog(MasterController.getGuiComponent(), "Unexpected error occured while " + comp + " the notebook. Please try again later.\n " + cause.getMessage(), "Notebook error", JOptionPane.ERROR_MESSAGE);
  		CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "Unexpected error occured while " + comp + " the notebook.");
  }

    public void versionExperiment(final String siteCode, final String site, final String users_fullname, final String notebook, final String page, final int version) {
    	log.debug("versionExperiment for " + site + ", " + users_fullname + ", " + notebook + ", " + page + " , V" + version);
    
    	int selection = JOptionPane.showConfirmDialog(mGui, "Are you sure you want to version experiment " + notebook + "-" + page + "v" + version + "?", "Version Experiment", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    
    	if (selection == JOptionPane.YES_OPTION) {
    		final int newVersion = version + 1;
    		final String progressStatus = "Versioning Experiment " + notebook + "-" + page + "v" + version + "...";
    		CeNJobProgressHandler.getInstance().addItem(progressStatus);
      
    		SwingWorker worker = new SwingWorker() {
    			public Object construct() {
    				boolean blnSuccess = false;
    				NotebookUser user = MasterController.getUser();
    				NotebookRef nbRef = null;
    				NotebookPageModel pageModelSrc = null;
    				NotebookPageModel pageModelNew = null;
    				StorageDelegate storageDelegate = null;
          
    				try {
    					storageDelegate = ServiceController.getStorageDelegate(user.getSessionIdentifier());
    				} catch (Exception e) {
    					ceh.logExceptionMsg(getGUIComponent(), "Failed to initialize StorageDelegate", e);
    					CeNJobProgressHandler.getInstance().removeItem(progressStatus);
    					return null;
    				}
          
    				try {
    					nbRef = new NotebookRef(notebook + "-" + page);
    					nbRef.setVersion(version);
    				} catch (Exception e) {
    					ceh.logErrorMsg(getGUIComponent(), "Invalid Notebook reference", "Invalid Notebook Ref", JOptionPane.ERROR_MESSAGE);
    					CeNJobProgressHandler.getInstance().removeItem(progressStatus);
    					return null;
    				}
    				
    				final String progressStatus = "Loading Experiment " + nbRef.getNotebookRef() + "v" + version + "...";
    				CeNJobProgressHandler.getInstance().addItem(progressStatus);
    				
    				try {
    					NotebookPageGuiInterface loadedInterface = getLoadedPageGui(siteCode, nbRef, version);
    					if (loadedInterface != null) {
    						pageModelSrc = loadedInterface.getPageModel();
    					} else {
    						pageModelSrc = storageDelegate.getNotebookPageExperimentInfo(nbRef, siteCode, MasterController.getUser().getSessionIdentifier());
    						ProcedureImagesUpdateManager.updateProcedureOnLoad(pageModelSrc);
    					}
    				} catch (Exception e) {
    					ceh.logExceptionMsg(getGUIComponent(), "Failed to retrieve original experiment", e);
    					CeNJobProgressHandler.getInstance().removeItem(progressStatus);
    					return null;
    				}
    				
    				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
          
    				if (pageModelSrc != null && user != null) {
    					try {
    						pageModelNew = new NotebookPageModel();
    						
    						pageModelNew.setNbRef(new NotebookRef());
    		                pageModelNew.deepCopy(pageModelSrc);

    		                // Work with attachments
    		                List<AttachmentModel> srcList = pageModelSrc.getAttachmentCache().getAttachmentList();
    		                List<AttachmentModel> newList = pageModelNew.getAttachmentCache().getAttachmentList();
    		                newList.clear();
    		                for (AttachmentModel srcModel : srcList) {
    		                	if (srcModel != null) {
    		                		AttachmentModel srcFromDb = storageDelegate.getNotebookPageExperimentAttachment(srcModel.getKey());
    		                		AttachmentModel newModel = new AttachmentModel();
    		                		newModel.deepCopy(srcFromDb);
    		                		newList.add(newModel);
    		                	}
    		                }
    		                
    						if (pageModelNew.getPageType().equals(CeNConstants.PAGE_TYPE_PARALLEL)) {
    							pageModelNew.getPageHeader().setTotalReactionSteps(pageModelNew.getReactionSteps().size());
    							
    							ParallelExpDuperDeDuper parallelExpDuperDeDuper = new ParallelExpDuperDeDuper();
    							pageModelNew = parallelExpDuperDeDuper.deDupeParallelExperiment(pageModelNew);

    							ParallelExpModelUtils parallelExpModelUtils = new ParallelExpModelUtils(pageModelNew);
    							populateProductIdForProducts(pageModelNew, pageModelSrc);
    							parallelExpModelUtils.setOrRefreshGuiPseudoProductPlate();
    							parallelExpModelUtils.linkProductBatchesAnalyticalModelInAnalysisCache();
    							parallelExpModelUtils.setPrecursorsForProductsAndMonomerBatches();
    							parallelExpModelUtils.linkBatchesWithPlateWells();
    							parallelExpModelUtils.populateMonomerAndProductHashMaps();
    							parallelExpModelUtils.updateBatchesListAmountFlags();
    							pageModelNew.onLoadonAfterSaveSetModelChanged(false, true);// Reset all flags.
    						} else if (pageModelNew.getPageType().equals(CeNConstants.PAGE_TYPE_CONCEPTION)) {
    							log.debug("************To Find out what code needs to be here**************"); // **TODO
    						} else if (pageModelNew.getPageType().equals(CeNConstants.PAGE_TYPE_MED_CHEM)) {
    							ParallelExpModelUtils utils = new ParallelExpModelUtils(pageModelNew);
    							utils.populateMonomerAndProductHashMaps();
    							utils.linkProductBatchesAnalyticalModelInAnalysisCache();
    						}

    						pageModelNew.setVersion(newVersion);
    						pageModelNew.setCenVersion(newVersion + "");
    						pageModelNew.getNbRef().setVersion(newVersion);
    						pageModelNew.setLatestVersion("Y");
    						pageModelNew.setStatus(CeNConstants.PAGE_STATUS_OPEN);
    						
    						try {
    							pageModelNew.setCompletionDateAsTimestamp(null);
    						} catch (Exception e) {
    							log.error("Failed to set completion date to null", e);
    						}
    						
    						// Clone Procedure to not break it in v1
    						pageModelNew.getPageHeader().setProcedure(new String(pageModelSrc.getProcedure()));
    						pageModelNew.getPageHeader().setProcedureImages(null);
    						
    						// Save new page
    						try {
    							//TODO workaround - mark reaction as changed just to send it to cus    							
    							List<ReactionStepModel> reactions = pageModelNew.getReactionSteps();
    							for (ReactionStepModel reactionStepModel : reactions) {
    								reactionStepModel.getRxnScheme().setModelChanged(true);
								}    							
    							pageModelNew.setUssiKey("0");
    							storageDelegate.addNotebookPage(pageModelNew, MasterController.getUser().getSessionIdentifier());    
    							ProcedureImagesUpdateManager.updateProcedureOnLoad(pageModelNew);
    							log.debug("Versioned Page model save sucessfull.");
    						} catch (StorageException e) {
    							JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Problem saving new version page", "Version Experiment Error", JOptionPane.ERROR_MESSAGE);
    							ceh.logExceptionMsg(MasterController.getGUIComponent(), e);
    							return null;
    						}

    						pageModelSrc.setLatestVersion("N");
    						pageModelSrc.setEditable(false);
    						saveExperiment(pageModelSrc);
    						
    						Gui gui = MasterController.getGUIComponent();
    						
    						if (gui != null) {
    							SpeedBarNodeInterface sbi = gui.getMySpeedBar().speedBarNavigateTo(pageModelSrc.getSiteCode(), pageModelSrc.getUserName(), pageModelSrc.getNbRef(), pageModelSrc.getNbRef().getVersion());
    							if (sbi != null && sbi instanceof SpeedBarPage) {
    								((SpeedBarPage) sbi).setLatestVersion("N");
    							}
    							gui.getMySpeedBar().refreshCurrentNode();
    						
    							sbi = gui.getAllSitesSpeedBar().speedBarNavigateTo(pageModelSrc.getSiteCode(), pageModelSrc.getUserName(), pageModelSrc.getNbRef(), pageModelSrc.getNbRef().getVersion());
    							if (sbi != null && sbi instanceof SpeedBarPage) {
    								((SpeedBarPage) sbi).setLatestVersion("N");
    							}
    							gui.getAllSitesSpeedBar().refreshCurrentNode();
    						}
    						
    						SpeedBarHandler hndlr = (SpeedBarHandler) MasterController.getGUIComponent().getActiveSpeedBarHandler();
    						hndlr.refreshCurrentNode();
    						blnSuccess = true;
    					} catch (Exception e) {
    						ceh.logExceptionMsg(MasterController.getGUIComponent(), e);
    					}
    				}
    				return (blnSuccess) ? pageModelNew : null;
    			}

    			public void finished() {
    				try {
    					NotebookPageModel pageModelNew = (NotebookPageModel) get();
    					if (pageModelNew != null) {
    						NotebookPageGuiInterface internalFrame = null;
    						if (pageModelNew.getPageType().equals(CeNConstants.PAGE_TYPE_CONCEPTION)) {
    							internalFrame = new ConceptionNotebookPageGUI(pageModelNew);
    						} else if (pageModelNew.getPageType().equals(CeNConstants.PAGE_TYPE_PARALLEL)) {
    							internalFrame = new ParallelNotebookPageGUI(pageModelNew);
    						} else if (pageModelNew.getPageType().equals(CeNConstants.PAGE_TYPE_MED_CHEM)) {
    							internalFrame = new SingletonNotebookPageGUI(pageModelNew);
    						}
    						addNotebookPageGuiInterfaceToGUI(internalFrame);
    						addNotebookPageToSpeedBar(pageModelNew);
    					}
    				} catch (Exception e) {
    					ceh.logExceptionMsg(MasterController.getGUIComponent(), e);
    				}
    				
    				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
    			}
    		};
    		
    		worker.start();
    	}
    }

  protected void replacePseudoWells(PseudoProductPlate pageModelPseudoProductPlate, PseudoProductPlate guiPseudoProductPlate) {
    PlateWell<ProductBatchModel> pageModelWells[] = pageModelPseudoProductPlate.getWells();
    PlateWell<ProductBatchModel> guiWells[] = guiPseudoProductPlate.getWells();
    for (PlateWell<ProductBatchModel> pageWell : pageModelWells) {
      BatchModel pageBatchModel = pageWell.getBatch();
      for (PlateWell<ProductBatchModel> guiWell : guiWells) {
    	ProductBatchModel guiBatchModel = guiWell.getBatch();
        if (pageBatchModel.getBatchNumber().getBatchNumber().equals(guiBatchModel.getBatchNumber().getBatchNumber()))
          pageWell.setBatch(guiBatchModel);
      }
    }
  }

  private void populateProductIdForProducts(NotebookPageModel pageModelNew, NotebookPageModel pageModelSrc) {
    List<ProductBatchModel> productBatchesList = pageModelNew.getAllProductBatchModelsInThisPage();
    List<MonomerBatchModel> monometBatchesList = new ArrayList<MonomerBatchModel>();
    List<ReactionStepModel> reactionStepsList = pageModelSrc.getReactionSteps();
    for (ReactionStepModel reactionStepModel : reactionStepsList) {
      monometBatchesList.addAll(reactionStepModel.getAllMonomerBatchModelsInThisStep());
    }
    for (ProductBatchModel productBatchModel : productBatchesList) {
      List<String> reactantBatchKeysList = productBatchModel.getReactantBatchKeys();
      StringBuffer prodId = new StringBuffer();
      for (int j = 0; j < reactantBatchKeysList.size(); j++) {
        for (MonomerBatchModel monomerBatchModel : monometBatchesList) {
          if (reactantBatchKeysList.get(j).equals(monomerBatchModel.getKey())) {
            prodId.append(monomerBatchModel.getMonomerId());
            break;
          }
        }
        if (prodId.toString().length() > 0 && j < (reactantBatchKeysList.size() - 1))
          prodId.append(":");
      }
      if (prodId.toString().indexOf(":") > 0) {
        if (prodId.indexOf("[") > -1) {
          prodId.replace(prodId.indexOf("["), prodId.indexOf("[") + 1, "");
          prodId.replace(prodId.indexOf("]"), prodId.indexOf("]") + 1, "");
        }
        productBatchModel.setProductId(prodId.toString());
      }
    }
  }

  private void addNotebookPageToSpeedBar(final NotebookPageModel page) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (mGui != null) {
          // mGui.getActiveSpeedBarHandler().refresh();
          mGui.getMySpeedBar().addPage(page, (mGui.getActiveSpeedBarHandler() == mGui.getMySpeedBar()));
          mGui.getAllSitesSpeedBar().addPage(page, (mGui.getActiveSpeedBarHandler() == mGui.getAllSitesSpeedBar()));
        }
      }
    });
  }

  public void openContents(String siteCode, String notebook, int start, int end) {
    if (!contentsCache.containsKey(siteCode + notebook + "," + start + "," + end))
      addContentsToGUI(siteCode, notebook, start, end);
    else {
      NotebookContentsGUI frame = contentsCache.get(siteCode + notebook + "," + start + "," + end);
      frame.moveToFront();
      try {
        frame.setSelected(true);
        frame.setMaximum(true);
      } catch (Exception e) { /* Ignored */
      }
    }
  }

  public void loadContents(String siteCode, String notebook, int start, int end, final NotebookContentsLoaderListener notify) {
    if (!contentsCache.containsKey(siteCode + notebook + "," + start + "," + end)) {
      final NotebookContentsLoader ncl = new NotebookContentsLoader(siteCode, notebook, start, end);
      ncl.addLoaderListener(new NotebookContentsLoaderAction() {
        public void ContentsLoaded(NotebookContentsLoaderEvent evt) {
          notify.ContentsLoaded(evt);
        }
      });
      ncl.loadModelOnly();
    } else {
      NotebookContentsLoaderEvent event = new NotebookContentsLoaderEvent(this, contentsCache
          .get(siteCode + notebook + "," + start + "," + end).getModel());
      notify.ContentsLoaded(event);
    }
  }

  public void printContents(String siteCode, String notebook, int start, int end) {
    // show print experiment dialog
    PrintExperimentDialog.displayPrintContentsDialog(MasterController.getGuiController().getGUIComponent(), siteCode, notebook,
        start, end);
  }

  public void speedBarLookupNavigateTo(String siteCode, String userID, NotebookRef nbRef, int version) {
    try {
		speedBarNavigateTo(CodeTableCache.getCache().getSiteDescription(siteCode), getUsersFullName(userID), nbRef, version);
    } catch (Exception e) {
      CeNErrorHandler.getInstance().logExceptionMsg(null, e);
    }
  }

  public void speedBarNavigateTo(String site, String users_fullname, NotebookRef nbRef, int version) {
    NotebookUser user = MasterController.getUser();

	// Different user, if in mybookmarks, switch to all sites / users
	int sbi = MasterController.getGUIComponent().getCurrentSpeedBarType();
	if (!user.getFullName().equals(users_fullname)) {
		if (sbi == Gui.OutlookBarTypes.MY_BOOKMARKS)
			MasterController.getGUIComponent().setCurrentSpeedBarType(Gui.OutlookBarTypes.ALL_SITES);
	}

	// sbi = MasterController.getGUIComponent().getCurrentSpeedBarType();
	
	// Prevent ClassCastException if External Collaborations is opened
	CommonHandlerInterface handlerInterface = MasterController.getGUIComponent().getActiveSpeedBarHandler();
	if (handlerInterface instanceof SpeedBarHandler) {
		SpeedBarHandler sbh = (SpeedBarHandler) handlerInterface;
		sbh.speedBarNavigateTo(site, users_fullname, nbRef, version);
	}
	
	// final int groupSize = NotebookPageUtil.NB_PAGE_GROUP_SIZE;
	// String nb = nbRef.getNbNumber();
	// String ex = nbRef.getNbPage();
	// String group = SpeedBarPageGroup.getGroupFromExperiment(ex);
	//
	// if (version > 1) ex = ex + " v" + version;
	//        
	// if (sbi == Gui.OutlookBarTypes.MY_BOOKMARKS) {
	//    String[] expandPath = { user.getFullName(), nb, group, ex };
	//    sbh.expandPath(expandPath, false);
	// } else {
	//    // need to translate userid to a user's fullname
	//    String[] expandPath = { site, users_fullname, nb, group, ex };
	//    sbh.expandPath(expandPath, false);
	// }
	}

  /*
   * (non-Javadoc)
   * 
   * @seecom.chemistry.enotebook.client.gui.page. NotebookPageChangedListener
   * #reactionChanged(com.chemistry.enotebook .client.gui.page.NotebookPageChangedEvent)
   */
  public void reactionChanged(NotebookPageChangedEvent event) {
    NotebookPageGuiInterface gui = (NotebookPageGuiInterface) event.getSource();
//    ReactionScheme scheme = (ReactionScheme) event.getSubObject();
    NotebookPageModel page = gui.getPageModel();
    // Navigate SpeedBar to this page. probably already here since we
    // navigate on page gui activate
    speedBarLookupNavigateTo(page.getSiteCode(), page.getUserName(), page.getNbRef(), page.getVersion());
//    SpeedBarNodeInterface sbi = MasterController.getGUIComponent().getLastSelectedSpeedBarObject();
//    if (sbi instanceof SpeedBarPage)
//      ((SpeedBarPage) sbi).setViewImage(scheme.getViewSketch());
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.chemistry.enotebook.client.gui.page. NotebookPageChangedListener
   * #detailsChanged(com.chemistry.enotebook .client.gui.page.NotebookPageChangedEvent)
   */
  public void detailsChanged(NotebookPageChangedEvent event) {
    NotebookPageModel page = (NotebookPageModel) event.getSubObject();
    // Navigate SpeedBar to this page. probably already here since we
    // navigate on page gui activate
    speedBarLookupNavigateTo(page.getSiteCode(), page.getUserName(), page.getNbRef(), page.getVersion());
    SpeedBarNodeInterface sbi = MasterController.getGUIComponent().getLastSelectedSpeedBarObject();
    if (sbi instanceof SpeedBarPage) {
      SpeedBarPage sbp = (SpeedBarPage) sbi;
      if (sbp.getSubject() == null || !sbp.getSubject().equals(page.getSubject()))
        sbp.setSubject(page.getSubject());
      if (sbp.getProject() == null || !sbp.getProject().equals(page.getProjectCode()))
        sbp.setProject(page.getProjectCode());
    }
  }

  public String getUsersFullName(String userID) {
    String[] list = new String[] { userID };
    String[] result = getUsersFullName(list);
    if (result != null)
      return result[0];
    else
      return "";
  }

  public String[] getUsersFullName(String[] userIDs) {
    if (userIDs == null || userIDs.length == 0 || userIDs[0] == null || userIDs[0].length() == 0)
      return null;

    String[] result = new String[userIDs.length];
    try {
      String tStr;
      boolean unknownflag = false;
      for (int i = 0; i < userIDs.length && !unknownflag; i++) {
        tStr = (String) userNameToFullNameMap.get(userIDs[i]);
        if (tStr != null && tStr.length() > 0)
          result[i] = tStr;
        else
          unknownflag = true;
      }
      if (unknownflag) {
        StorageDelegate sd = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
        result = sd.getUsersFullName(userIDs);
        if (result != null) {
          for (int i = 0; i < result.length; i++) {
            if (result[i] != null && result[i].length() > 0 && !userNameToFullNameMap.containsKey(userIDs[i]))
              userNameToFullNameMap.put(userIDs[i], result[i]);
            // if there is no such user in CeN DB ask Person Service
            else {
            	PersonDelegate pDelegate = new PersonDelegate();
            	IPerson person = pDelegate.userIDtoPerson(userIDs[i]);
            	if(person != null) {
            		result[i] = person.getLastName() + ", " + person.getFirstName();
            	}
                if (result[i] != null && result[i].length() > 0 && !userNameToFullNameMap.containsKey(userIDs[i]))
                    userNameToFullNameMap.put(userIDs[i], result[i]);
            }
          }
        }
      }
    } catch (Exception e) {
      CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), e);
    }
    return result;
  }

  // Returns fully formatted notebook-experiment for last experiment
  // Based on actual database vs. speedbar
  private String getNextExperimentInNotebook(String siteCode, String notebook) {
    String result = null;
    try {
      // convert the site name to a site code
      int lastExp = nbDel.getNextExperimentForNotebook(siteCode, notebook);
      if (lastExp > 0)
        result = notebook + "-" + NotebookPageUtil.formatNotebookPage("" + lastExp);
    } catch (Exception e) {
      CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), e);
    }
    return result;
  }

  public boolean continueSave(String nbRef) {
    int value = JOptionPane.showConfirmDialog(mGui, "Experiment " + nbRef + " has been updated from another location.\n"
        + "Do you wish to overwrite these changes with the current page ?", "Database Updates Found",
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (value == 0)
      return true;
    else
      return false;
  }

  /**
   * restart the timer when User Preferences are changed
   * 
   */
  public void reStartAutoSaveTimer() {
    if (autoSaveTimer != null) {
      autoSaveTimer.stop();
    }
    startAutoSaveTimer();
  }

  public int getDrawingTool() {
    String IDrawEnabled = "true";
    String CSDrawEnabled = "true";
    try {
      IDrawEnabled = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_EDITOR_IDRAW);
      CSDrawEnabled = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_EDITOR_CSDRAW);
    } catch (Exception e) {
    }
    int userPref = MasterController.getUser().getPreferredDrawingTool();
    if (userPref <= 0)
      userPref = Compound.ISISDRAW;
    if (userPref == Compound.CHEMDRAW && !CSDrawEnabled.equalsIgnoreCase("true"))
      userPref = Compound.ISISDRAW;
    else if (userPref == Compound.ISISDRAW && !IDrawEnabled.equalsIgnoreCase("true"))
      userPref = Compound.CHEMDRAW;
    return userPref;
  }

  public boolean checkForModsEnabled() {
    try {
      String val = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_VERIFY_NO_CHANGE);
      return (val != null && val.equalsIgnoreCase("true"));
    } catch (Exception e) {
      log.error("Failed to determine whether or not check for Mods is enabled", e);
      return false;
    }
  }

  public void openPCeNExperiment(String siteCode, String notebook, String page, int version) {
	  openPCeNExperiment(siteCode, notebook, page, version, false);
  }
  
  public void openPCeNExperiment(String siteCode, String notebook, String page, int version, boolean submitFailedReopen) {
	  openPCeNExperiment(siteCode, notebook, page, version, submitFailedReopen, false);
  }
  
  public void openPCeNExperiment(String siteCode, String notebook, String page, int version, boolean submitFailedReopen, boolean openLastVersion) {
    String pageAll = notebook + "-" + page;
    NotebookRef nbRef = null;
    try {
    	nbRef = new NotebookRef(pageAll);
    	if(version > 0) {
    		nbRef.setVersion(version);
    	}
    	if(openLastVersion) {
    		nbRef.setVersion(0);
    	}
    } catch (Exception e) {
        JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
                                      "Notebook Reference '" + pageAll + "-" + page + "' is Invalid.\n" + 
                                      "Notebook reference format must be ########-####",
                                      "Experiment Load Error",
                                      JOptionPane.ERROR_MESSAGE);
        log.error("Invalid Notebook Reference: " + pageAll + "-" + page);
    }
    // Once successfully validating our notebook reference open the experiment.
    if(nbRef != null) {
    	openPCeNExperimentCombinedBkndPage(siteCode, nbRef, submitFailedReopen);
    }

  }

	public void openExperiment() {
		String newNbRef = JOptionPane.showInputDialog(MasterController.getGuiComponent(), "Enter a Notebook Reference (########-####):");
		if (StringUtils.isNotBlank(newNbRef)) { // if ok clicked
			openPCeNExperiment("", newNbRef.substring(0, 8), newNbRef.substring(9));
		}
	}
  
  /** Array of notebooks, which is opening now */
  private ArrayList<NotebookRef> currentOpening = new ArrayList<NotebookRef>();

  /** Check, if notebook is opening now */
  private boolean isExperimentOpeningNow(NotebookRef notebookRef) {
    for (NotebookRef ref : currentOpening) {
      if (ref.getNbNumber().equals(notebookRef.getNbNumber()) && ref.getNbPage().equals(notebookRef.getNbPage())
          && ref.getVersion() == notebookRef.getVersion()) {
        return true;
      }
    }
    return false;
  }
    
  public void openPCeNExperimentCombinedBkndPage(String siteCode, String pageAll, int version) {
	  try {
		  final NotebookRef nbReference = new NotebookRef(pageAll);
		  if (version > 0) {
			  nbReference.setVersion(version);
		  }
		  openPCeNExperimentCombinedBkndPage(siteCode, nbReference, false);
	  } catch (Exception e) {
		  JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Invalid notebook.");
    		log.error("Invalid Notebook", e);
    	}
  }
  
  public void openPCeNExperimentCombinedBkndPage(final String siteCode,
                                                 final NotebookRef nbReference, 
                                                 final boolean submitFailedReopen) 
  {
	  // Is this notebook is already opening now?
	  if (isExperimentOpeningNow(nbReference)) {
		  JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "This notebook is opening now.");
		  return;
	  }
	  // Is experiment already opened?
	  NotebookPageGuiInterface nbPgGui = getLoadedPageGui(siteCode, nbReference, nbReference.getVersion());
	  log.info("getLoadedPageGui "+nbPgGui);
	  if (nbPgGui != null) { // version is not known
		  log.info("pageStatus "+nbPgGui.getPageModel().getPageStatus());		  
		  if(submitFailedReopen && 
		  	 (CeNConstants.PAGE_STATUS_COMPLETE.equals(nbPgGui.getPageModel().getPageStatus()) ||
		  	  CeNConstants.PAGE_STATUS_SUBMIT_FAILED.equals(nbPgGui.getPageModel().getPageStatus()) ||
		  	  CeNConstants.PAGE_STATUS_SUBMITTED.equals(nbPgGui.getPageModel().getPageStatus()))) 
		  {
    				nbPgGui.refreshPage();
    				nbPgGui.dispose();
		  } else {
			  moveToFront((JInternalFrame)nbPgGui);
			  return;
		  }
	  }

	  try {
    		new SwingWorker() {
    			long startTime = System.currentTimeMillis();
    			final String progressStatus = "Loading Experiment : " + nbReference;

    			public Object construct() {
    				NotebookPageModel pageModel = null;
          
    				// This notebook is opening now
    				currentOpening.add(nbReference);
    				CeNJobProgressHandler.getInstance().addItem(progressStatus);
          
    				// Remove autosave file if notebook is completed, archived etc
    				String pageStatus = null;
    				try {
    					log.info("Retrieving Notebook Page Status for current page: " + nbReference + " version: " + nbReference.getVersion());
    					StorageDelegate storageDelegate = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
    					pageStatus = storageDelegate.getNotebookPageCompleteStatus(siteCode, 
    					                                                           nbReference.getNotebookRef(), 
    					                                                           nbReference.getVersion());
    				} catch (Exception e) {
						log.error("Error retrieving pageStatus for notebook reference: " + nbReference + " version: " + nbReference.getVersion(), e);
						currentOpening.remove(nbReference);
					}
    					
					if (checkIfAutoSaveExists(nbReference)) {
						if (!StringUtils.equals(pageStatus, CeNConstants.PAGE_STATUS_OPEN)) {
							try {
								log.info("Attempting to remove auto save file for notebook reference: " + nbReference + " version: " + nbReference.getVersion());
								deleteAutoSaveFile(nbReference);
							} catch (Exception e) {
								log.error("Error removing autosave file for notebook reference: " + nbReference + " version: " + nbReference.getVersion(), e);
								currentOpening.remove(nbReference);
							}
						} else {
	    					int selection = JOptionPane.showConfirmDialog(mGui, 
	    					                                              "Auto-Recover file(s) found for " + nbReference.getNbRef() + "v" + nbReference.getVersion() + ".  Do you wish to recover from these files?",
	    					                                              "AutoRecover",
	    					                                              JOptionPane.YES_NO_CANCEL_OPTION,
	    					                                              JOptionPane.QUESTION_MESSAGE);
	    					if (selection == JOptionPane.YES_OPTION) {
	    						try {
	    							pageModel = autoRecover(nbReference, getAutoSaveLocation());
	    							if (pageModel != null) {
	    								log.debug("Autorecover loaded successfully...");
	    								// Enable save so that user has to save it or the auto save with save it again
	    								pageModel.setModelChanged(true);
	    							}
	    							
	    							// now delete the autosave file
	    							deleteAutoSaveFile(pageModel);
	    						} catch (NotebookDelegateException e) {
	    							JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
	    					    								  "Invalid Notebook number entered. Please correct and continue.", 
	    					    								  "Invalid Notebook",
	    					    								  JOptionPane.ERROR_MESSAGE);
	    							log.error("Invalid Notebook", e);
	    							currentOpening.remove(nbReference);
	    						} catch (Exception e) {
	    							JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
	    							                              "An unexpected error occurred while attempting to auto recover\n" +
	    							                              "Notebook reference: " + nbReference, 
	    							                              "Auto Recover Error", 
	    							                              JOptionPane.ERROR_MESSAGE);
	    							log.error("Problem Retrieving Notebook", e);
	    							currentOpening.remove(nbReference);
	    						}
	    					} else if (selection == JOptionPane.NO_OPTION) {
	    						try {
	    							StorageDelegate storageDelegate = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
	    							pageModel = storageDelegate.getNotebookPageExperimentInfo(nbReference, siteCode, MasterController.getUser().getSessionIdentifier());
	                
	    							// update procedure images and their references 
	    							ProcedureImagesUpdateManager.updateProcedureOnLoad(pageModel);
	                
	    							if (checkIfAutoSaveExists(nbReference)) {
	    								deleteAutoSaveFile(pageModel);
	    							}
	    						} catch (InvalidNotebookRefException e) {
	    							JOptionPane.showOptionDialog(MasterController.getGUIComponent(),
	    									"Invalid Notebook number entered. Please correct and continue.", "Invalid Notebook",
	    									JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
	    							currentOpening.remove(nbReference);
	    							log.error("Invalid Notebook Reference was used. User was notified.", e);
	    						} catch (Exception e) {
	    							JOptionPane.showOptionDialog(MasterController.getGUIComponent(),
	    									"Unexpected error occured while opening the notebook. Please try again later",
	    									"Notebook error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
	    							CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(),
	    									"Unexpected error occured while opening the notebook. ", e);
	    							currentOpening.remove(nbReference);
	    						}
	    					}
						}
    				} else {
    					// If there is no autosave file go directly to DB
    					try {
    						StorageDelegate storageDelegate = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
    						pageModel = storageDelegate.getNotebookPageExperimentInfo(nbReference, siteCode, MasterController.getUser().getSessionIdentifier());
    						// update procedure images and their references 
    						ProcedureImagesUpdateManager.updateProcedureOnLoad(pageModel);
    					} catch (InvalidNotebookRefException e) {
    						log.error("Error opening experiment " + nbReference.getNotebookRef() + ": ", e);
    						JOptionPane.showOptionDialog(MasterController.getGUIComponent(),
    								"Invalid Notebook number entered: " + nbReference.getNotebookRef() + "\nPlease correct and continue.",
    								"Invalid Notebook",
    								JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
    						currentOpening.remove(nbReference);
    					} catch (Exception e) {
    						log.error("Error opening experiment " + nbReference.getNotebookRef() + ": ", e);
    						JOptionPane.showOptionDialog(MasterController.getGUIComponent(),
    								"Unexpected error occured while opening the notebook: " + nbReference.getNotebookRef() + "\nPlease try again later",
    								"Notebook error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
    						CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "Unexpected error occured while opening the notebook: " + nbReference.getNotebookRef());
    						currentOpening.remove(nbReference);
    					}
    				}

    				// former "finished()"
    				if (pageModel == null) {
    					CeNJobProgressHandler.getInstance().removeItem(progressStatus);
    					currentOpening.remove(nbReference);
    					return null;
    				}

                    if (CeNConstants.PAGE_STATUS_SUBMIT_FAILED.equals(pageModel.getPageStatus()) ||
                            CeNConstants.PAGE_STATUS_SUBMITTED.equals(pageModel.getPageStatus())) {
                        if (submitFailedReopen) {
                            pageModel.setPageStatus(CeNConstants.PAGE_STATUS_OPEN);
                            pageModel.setEditable(true);
                            saveExperiment(pageModel);
                        } else {
                            pageModel.setEditable(false);
                        }
                    }
                    
                    PageChangesCache.storePage(pageModel);
                    
                    //refresh icon on speedbar
                    Gui gui = MasterController.getGUIComponent();

                    NotebookRef nbRef = pageModel.getNbRef();
                    
                    SpeedBarNodeInterface sbi = gui.getMySpeedBar().speedBarNavigateTo(pageModel.getSiteCode(), pageModel.getUserName(), nbRef, nbRef.getVersion());
                    if (sbi != null && sbi instanceof SpeedBarPage) {
        				((SpeedBarPage) sbi).setPageModel(pageModel);
        				((SpeedBarPage) sbi).setPageStatus(pageModel.getPageStatus());
                    }
        			gui.getMySpeedBar().refreshCurrentNode();
            		
        			sbi = gui.getAllSitesSpeedBar().speedBarNavigateTo(pageModel.getSiteCode(), pageModel.getUserName(), nbRef, nbRef.getVersion());
        			if (sbi != null && sbi instanceof SpeedBarPage) {
        				((SpeedBarPage) sbi).setPageModel(pageModel);
        				((SpeedBarPage) sbi).setPageStatus(pageModel.getPageStatus());
        			}
        			gui.getAllSitesSpeedBar().refreshCurrentNode();
                    
                    String pageType = pageModel.getPageHeader().getPageType();
    				if (pageType.equals(CeNConstants.PAGE_TYPE_PARALLEL)) {
    					ParallelNotebookPageGUI parallelNotebookPageGUI = new ParallelNotebookPageGUI(pageModel);
    					addNotebookPageGuiInterfaceToGUI(parallelNotebookPageGUI);
    				} else if (pageType.equals(CeNConstants.PAGE_TYPE_MED_CHEM)) {
    					SingletonNotebookPageGUI singletonNotebookPageGUI = new SingletonNotebookPageGUI(pageModel);
    					addNotebookPageGuiInterfaceToGUI(singletonNotebookPageGUI);
    				} else if (pageType.equals(CeNConstants.PAGE_TYPE_CONCEPTION)) {
    					ConceptionNotebookPageGUI conceptionNotebookPageGUI = new ConceptionNotebookPageGUI(pageModel);
    					addNotebookPageGuiInterfaceToGUI(conceptionNotebookPageGUI);
    				}

    				NotebookPageModel loadedModel = (NotebookPageModel) pageModel.deepClone();
    				loadedModel.setNbRef(pageModel.getNbRef());
    				GuiController.this.loadedPageModels.put(loadedModel.getNotebookRefAsString(), loadedModel);

    				try {
    					NotebookPage nbPage = NotebookPageFactory.createEmpty();
    					NotebookPageHandler.getInstance().processNotebookPage(pageModel, nbPage, true);
    					nbPageCache.addNotebookPage(nbPage);
    				} catch (Exception e) {
    					log.error("Unable to save local version of experiment!", e);
    				}
          
    				// This notebook opened
    				currentOpening.remove(nbReference);
    				CeNJobProgressHandler.getInstance().removeItem(progressStatus);

    				long endTime = System.currentTimeMillis();
    				if (log.isInfoEnabled()) {
    					log.info(" opening an experiment took " + (endTime - startTime) + " ms");
    				}

    				return null;
    			};

    			public void finished() {
    				
    			}
    		}.start();
    	} catch (Exception e) {
    		JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Invalid notebook.");
    		log.error("Invalid Notebook", e);
    	}
    }

  public static void moveToFront(final JInternalFrame fr) {
    if (fr != null) {
      processOnSwingEventThread(new Runnable() {
        public void run() {
          fr.moveToFront();
          fr.setVisible(true);
          try {
            fr.setSelected(true);
            if (fr.isIcon()) {
              fr.setIcon(false);
            }
            fr.setSelected(true);
          } catch (PropertyVetoException ex) {

          }
          fr.requestFocus();
        }
      });
    }

}
  public static void processOnSwingEventThread(Runnable todo) {
    processOnSwingEventThread(todo, false);
  }

  public static void processOnSwingEventThread(Runnable todo, boolean wait) {
    if (todo == null) {
      throw new IllegalArgumentException("Runnable == null");
    }

    if (wait) {
      if (SwingUtilities.isEventDispatchThread()) {
        todo.run();
      } else {
        try {
          SwingUtilities.invokeAndWait(todo);
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
      }
    } else {
      if (SwingUtilities.isEventDispatchThread()) {
        todo.run();
      } else {
        SwingUtilities.invokeLater(todo);
      }
    }
  }

  private boolean checkIfAutoSaveExists(NotebookRef nbRef) {

    String tempPath;

    try {
      tempPath = getAutoSaveLocation() + File.separator;
      File dir = new File(tempPath);
      final String tempRef = getSaveFileNamePrefix(nbRef);
      FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return (name.endsWith(".SAV") && name.startsWith(tempRef));
        }
      };
      String[] fileList = dir.list(filter);
      if (fileList.length > 0) {
        File tempFile = new File(tempPath + fileList[fileList.length - 1]);
        if (tempFile.exists())
          return true;
      }

      return false;

    } catch (Exception e) {
      log.error("Failed lookup of autosave file.", e);
      return false;
    }
  }
  
  /**
   * name of autosave file name without extension
   * @param nbRef
   * @return
   */
  private String getSaveFileNamePrefix(NotebookRef nbRef) {
	  return CeNConstants.PROGRAM_NAME + CEN_VERSION + "-" + nbRef.getNbRef() + "v" + nbRef.getVersion();
  }

  public boolean saveExperiment(NotebookPageModel pageModel) {
        boolean result = false;
    // to ensure save procedure
    pageModel.setModelChanged(true);

      final String notebookRef = pageModel.getNotebookRefWithoutVersion();
      try {
          long startSave = System.currentTimeMillis();
          NBKPageUpdateManager manager = NBKPageUpdateManager.getNBKPageUpdateManagerInstance(MasterController.getUser().getSessionIdentifier());
          manager.updateNotebookPageData(pageModel);
          System.out.println("Notebook saved for " + (System.currentTimeMillis() - startSave) + " milliseconds");
          long startDeleteFiles = System.currentTimeMillis();
          deleteAutoSaveFile(pageModel);
          System.out.println("Autosave files deleted for " + (System.currentTimeMillis() - startDeleteFiles) + " milliseconds");
          String str = MasterController.getUser().getPreference(NotebookUser.PREF_ENABLE_OFFLINE_REPORT);
          if ("YES".equals(str)) {
        	  saveAsRtf(pageModel, true);
          }
          result = true;
    } catch (Exception e) {
        final String message = "Unable to save notebook page " + notebookRef;
        CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, message);
        JOptionPane.showMessageDialog(MasterController.getGUIComponent(), message +
                "\n\nError message: " + e.getMessage(), "Save Failure", JOptionPane.ERROR_MESSAGE);
        log.error(message, e);
    }
    // Update user preference changes
    // if (MasterController.getUser().isPrefChanges()) {
    try {
      MasterController.getUser().setPreference(NotebookUser.PREF_CurrentNbRef, notebookRef);
      MasterController.getUser().setPreference(NotebookUser.PREF_CurrentNbRefVer, "" + pageModel.getNbRef().getVersion());
      MasterController.getUser().updateUserPrefs();
      result = result && true;
    } catch (UserPreferenceException e) {
      JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Error saving the user preferences " + notebookRef + "\n\nError message:"
          + e.getMessage(), "Update preferences Failure", JOptionPane.ERROR_MESSAGE);
      log.error("Failed to save user preferences!", e);
    }
    // }
    PageChangesCache.storePage(pageModel);
    return result;
  }

  public boolean saveExperiment(NotebookPageModel[] pageModel) {
    List<NotebookPageModel> failedPages = new Vector<NotebookPageModel>(Arrays.asList(pageModel));
    
    try {
      SessionIdentifier sessionID = MasterController.getUser().getSessionIdentifier();
      NBKPageUpdateManager manager = NBKPageUpdateManager.getNBKPageUpdateManagerInstance(sessionID);
      for (int i = 0; i < pageModel.length; ++i) {
        if (pageModel[i] != null) {
          pageModel[i].setModelChanged(true);
          manager.updateNotebookPageData(pageModel[i]);
          deleteAutoSaveFile(pageModel[i]);
          String str = MasterController.getUser().getPreference(NotebookUser.PREF_ENABLE_OFFLINE_REPORT);
          if ("YES".equals(str)) {
        	  saveAsRtf(pageModel[i], true);
          }
          pageModel[i].setModelChanged(false);
          failedPages.remove(pageModel[i]);
        }
      }
    } catch (Exception e) {
    	String failedNbNumbers = "";
    	
    	for (NotebookPageModel failedPage : failedPages) {
    		failedNbNumbers += (failedPage.getNotebookRefAsString() + "\n");
    	}

    	CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "Failed to save the following pages:\n\n" + failedNbNumbers + "\nError message: " + e.getMessage());
    			
    	JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Failed to save the following pages:\n\n" + failedNbNumbers + "\nError message: " + e.getMessage(), "Save Failure", JOptionPane.ERROR_MESSAGE);
    	
      log.error("Error saving the notebook: " + e.toString(), e);
      return false;
    }
    return true;
  }

  /**
   * Updates batch registration information on page with given notebook reference
   * @param clientNbRef notebook page reference
   * @param pageKey notebook page key
   * @param jobId registration job ID
   * @return true if success
   */
  public boolean updateNotebookPageBatchRegInfo(NotebookRef clientNbRef, String pageKey, String jobId) {
    log.info("updateNotebookPageBatchRegInfo(NotebookRef, String, String) start");
    
    boolean result = updateNotebookPageBatchRegInfo(clientNbRef, pageKey, jobId, false);
    
    log.info("updateNotebookPageBatchRegInfo(NotebookRef, String, String) end");
    return result;
  }
  
  /**
   * Updates batch registration information on page with given notebook reference and set jobId to "-1" if resetJobId is true
   * @param clientNbRef notebook page reference
   * @param pageKey notebook page key
   * @param jobId registration job ID
   * @return true if success
   */
  public boolean updateNotebookPageBatchRegInfo(NotebookRef clientNbRef, String pageKey, String jobId, boolean resetJobId) {
    log.info("updateNotebookPageBatchRegInfo(NotebookRef, String, String, boolean) start");
    
    log.info("jobId = " + jobId);
    
    boolean result = false;
    
    try {
      String siteCode = MasterController.getUser().getSiteCode();
      StorageDelegate storageDelegate = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
      List<BatchRegInfoModel> regInfos = storageDelegate.getAllRegisteredBatchesForJobid(jobId, MasterController.getUser().getSessionIdentifier());
      NotebookPageGuiInterface pageGui = getLoadedPageGui(siteCode, clientNbRef, clientNbRef.getVersion());
      if (pageGui != null && regInfos != null) {
        NotebookPageModel pageModel = pageGui.getPageModel();
        List<ProductBatchModel> batches = pageModel.getAllProductBatchModelsInThisPage();
        if (batches != null) {
          for (BatchRegInfoModel regInfo : regInfos) {
            if (regInfo != null) {
              for (ProductBatchModel batch : batches) {
                if (batch != null && batch.getRegInfo() != null) {
                  if (StringUtils.equals(batch.getKey(), regInfo.getBatchKey())) {
                    log.info("CompoundRegistration status: " + regInfo.getCompoundRegistrationStatus());
                    if (resetJobId) {
                      regInfo.setJobId("-1");
                    }
                    batch.setRegInfo(regInfo);
                    PCeNRegistration_BatchDetailsViewContainer.removeBatchFrommErrorMap(batch);
                    break;
                  }
                }
              }
            }
          }
        }
        pageModel.setModelChanged(false);
        result = true;
      }
    } catch (Exception e) {
      log.error("Error updating batch registration information:", e);
      result = false;
    }
    
    log.info("updateNotebookPageBatchRegInfo(NotebookRef, String, String, boolean) end");
    return result;
  }

  public void openPCeNExperiment(final String siteCode, final String notebook, final String page) {
    openPCeNExperiment(siteCode, notebook, page, 0);// Version is not
    // passed.
  }

  public void openPCeNExperimentCombinedBkndPage(String sitecode, String notebookRef) {
    openPCeNExperimentCombinedBkndPage(sitecode, notebookRef, 0); // 0 by default
  }

  private static String getAutoSaveLocation() throws Exception {
    String path = rootDirectory + File.separator + autoSaveDirName;
    if (!validateDirectory(path))
      throw new Exception("Could note create directory " + path);
    return path;
  }

  private static boolean validateDirectory(String path) throws Exception {
    boolean result = false;
    if (path != null && path.length() > 0) {
      File dir = new File(path);
      if (!dir.exists()) {
        if (dir.mkdir())
          result = true;
      } else
        result = true;
    }
    return result;
  }

  private boolean autoSave(NotebookPageModel model, String autoSaveLocation) throws NotebookDelegateException {
    boolean encryptFailure = false;
    // saves the latest version of the notebook page to the local C:drive
    // this can then be used to recover the latest changes in case of
    // storage failure.
    try {
      // create a new temporary outputFile
      String tempPath = autoSaveLocation + File.separator;
      File tmpFile = new File(tempPath + getSaveFileNamePrefix(model.getNbRef()) + ".TMP");
      // encrypt the notebookpage context
      // write the encrypted notebook page to the file
      FileOutputStream fout = new FileOutputStream(tmpFile);
      if (EncryptionUtil.encryptObjectToFile(fout, model)) {
        // check for existing versions on the c: drive
        File dir = new File(tempPath);
        final String tempRef = getSaveFileNamePrefix(model.getNbRef());
        FilenameFilter filter = new FilenameFilter() {
          public boolean accept(File dir, String name) {
            return (name.endsWith(".SAV") && name.startsWith(tempRef));
          }
        };
        String[] fileList = dir.list(filter);
        String autoSaveNumber = (new DecimalFormat("000")).format(fileList.length + 1);
        File autoSaveFile = new File(tempPath + getSaveFileNamePrefix(model.getNbRef()) + "-" + autoSaveNumber + ".SAV");
        if (autoSaveFile.exists())
          autoSaveFile.delete();
        // rename the temp file
        tmpFile.renameTo(autoSaveFile);
      } else
        encryptFailure = true;
    } catch (FileNotFoundException fnfe) {
      // Error in creating the output file
      throw new NotebookDelegateException("Error accessing local file system", fnfe);
    } catch (Exception e) {
      // Error in creating the output file
      throw new NotebookDelegateException("Error retrieving temporary folder property", e);
    }
    if (encryptFailure)
      throw new NotebookDelegateException("Error creating encrypted notebook page.");
    log.debug("Save process completed ...");
    return true;
  }

  private NotebookPageModel autoRecover(NotebookRef nbRef, String autoSaveLocation) throws NotebookDelegateException {
    String recoverError = null;
    NotebookPageModel model = null;
    try {
      String tempPath = autoSaveLocation + File.separator;
      File dir = new File(tempPath);
      final String tempRef = getSaveFileNamePrefix(nbRef);
      FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return (name.endsWith(".SAV") && name.startsWith(tempRef));
        }
      };
      String[] fileList = dir.list(filter);
      if (fileList.length > 0) {
        File tempFile = new File(tempPath + fileList[fileList.length - 1]);
        // this is currently just recovering from the latest autosave file
        // as autosaves are not currently incremental saves
        // for (int i=0; i < fileList.length; i++) {
        // File autoSaveFile = new File(tempPath + fileList[i]);
        File autoSaveFile = tempFile;
        if (autoSaveFile.exists()) {
          FileInputStream fin = new FileInputStream(autoSaveFile);
          // decode the byte array
          Object savedObject = EncryptionUtil.decryptObjectFromFile(fin);
          if (savedObject != null) {
            model = (NotebookPageModel) savedObject;
            // saves autosave file to database and deletes .SAV files
            // USER2: Don't save the exp from auto recover file. Let the user choose from main UI.
            // this.saveExperiment(model);
          } else {
            recoverError = "Error in decrypting the autosave file " + autoSaveFile.getName();
            // break;
          }
        } else {
          recoverError = "Error in finding the autosave file " + autoSaveFile.getName();
          // break;
        }
      }
      // }
    } catch (FileNotFoundException fnfe) {
      // file not exists is already checked
    } catch (Exception ioe) {
      throw new NotebookDelegateException("Error recovering from autosave files", ioe);
    }
    if (recoverError != null)
      throw new NotebookDelegateException(recoverError);

    return model;
  }

    private void deleteAutoSaveFile(NotebookPageModel model) {
    	deleteAutoSaveFile(model.getNbRef());
    }

    private void deleteAutoSaveFile(NotebookRef nbRef) {
    	if (checkIfAutoSaveExists(nbRef)) {
    		try {
    			String tempPath = getAutoSaveLocation() + File.separator;
    			File dir = new File(tempPath);
    			final String tempRef = getSaveFileNamePrefix(nbRef);
    			
    			FilenameFilter filter = new FilenameFilter() {
    				public boolean accept(File dir, String name) {
    					return (name.endsWith(".SAV") && name.startsWith(tempRef));
    				}
    			};

    			String[] fileList = dir.list(filter);
    			
    			if (fileList.length > 0) {
    				File tempFile = new File(tempPath + fileList[fileList.length - 1]);
    				if (tempFile.exists()) {
    					boolean success = tempFile.delete();
    					if (success && log.isInfoEnabled()) {
    						log.info("Deleting autosave file successfully: " + tempFile.getName());
    					}
    				}
    			}
    		} catch (Exception e) {
    			log.error("Error deleting autosave file:", e);
    		}
    	}
    }

  private void saveAsRtf(NotebookPageModel pageModel, boolean autoSave) {

    try {
      String output_path = NotebookPageCache.getOfflineAccessLocation();
      String file_name = pageModel.getNotebookRefAsString();

      renameExistingFiles(output_path, file_name + "." + offlineCopyFileFormat);
      saveAsRtf(pageModel, output_path + File.separator + file_name, autoSave);
    } catch (Exception e) {
      // do not raise exception, just log error since we do not want
      // to interfere with saving experiment to database
      log.error("saveAsRtf failed for [" + pageModel.getNotebookRefAsString() + "] ", e);
    }
  }

  private void renameExistingFiles(String output_path, final String file_name) {
    // add tilde to existing files with this name as backup
    // existing tilde files with this base name are deleted
    File dir = new File(output_path);

    FilenameFilter filter = new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.startsWith(file_name);
      }
    };

    String[] fileList = dir.list(filter);
    for (int i = 0; i < fileList.length; i++) {

      String f_name = fileList[i];
      if (f_name.endsWith("~") == false) {
        // make backup copy (but not of prios backups)
        File f = new File(output_path + File.separator + f_name);
        File renameToFile = new File(output_path + File.separator + f_name + "~");
        if (renameToFile.exists()) {
          renameToFile.delete();
        }
        f.renameTo(renameToFile);
      }
    }
  }

  private void saveAsRtf(final NotebookPageModel pageModel, final String output_path, boolean autoSave) throws Exception {
	  SwingWorker worker = new SwingWorker() {
		  public Object construct() {
	    	  long startSaveAsRtf = System.currentTimeMillis();
	    	  final String progressStatus = "Creating offline copy for " + pageModel.getNotebookRefAsString() + " ...";
	          CeNJobProgressHandler.getInstance().addItem(progressStatus);
	
	          ReportURLGenerator urlgen = new ReportURLGenerator();
	          IExperimentTypePrintOptions printOptions = null;    
	
	          urlgen.addParameter(PrintSetupConstants.TIME_ZONE, TimeZone.getDefault().getID());
	          if (pageModel.getPageType().equals(CeNConstants.PAGE_TYPE_CONCEPTION)) {
	        	  printOptions = new ConceptionPrintOptions();
	        	  urlgen.addParameter(PrintSetupConstants.REPORT_NAME, "conception.rptdesign");
	          } else if (pageModel.getPageType().equals(CeNConstants.PAGE_TYPE_PARALLEL)) {
	        	  printOptions = new ParallelPrintOptions();
	        	  urlgen.addParameter(PrintSetupConstants.REPORT_NAME, "parallel.rptdesign");
	          } else if (pageModel.getPageType().equals(CeNConstants.PAGE_TYPE_MED_CHEM)) {
					printOptions = new SingletonPrintOptions();
					urlgen.addParameter(PrintSetupConstants.REPORT_NAME, "medchem.rptdesign");
	          }
	          printOptions.setOptionsRequiredForIP();
	          NotebookRef ref = pageModel.getNbRef();
	          urlgen.addParameter(PrintSetupConstants.NOTEBOOK_NUMBER, ref.getNbNumber());
	          urlgen.addParameter(PrintSetupConstants.PAGE_NUMBER, ref.getNbPage());
	          urlgen.addParameter(PrintSetupConstants.PAGE_VERSION, Integer.toString(ref.getVersion()));
	          urlgen.addParameter(PrintSetupConstants.OUTPUT_FORMAT, offlineCopyFileFormat);
	          urlgen.addParameter(PrintSetupConstants.STOP_AFTER_IMAGE_LOAD_ERROR, "false");
	
	          StringBuffer buff = new StringBuffer();
	          buff.append(ref.getNbNumber()).append("-").append(ref.getNbPage());
	          buff.append("v").append(String.valueOf(ref.getVersion()));
	          String filename = buff.toString();
	          urlgen.addParameter(PrintSetupConstants.FILE_NAME, filename);
	          Map<String, String> otherOptions = printOptions.getOptions();
	          Iterator<String> it = otherOptions.keySet().iterator();
	          while (it.hasNext()) {
	        	  String key = (String) it.next();
	        	  urlgen.addParameter(key, (String) otherOptions.get(key));
	          }
	
	          try {
	        	  if (urlgen.isComplete()) {
                      byte[] result = CommonUtils.getReportHttpResponseResult(urlgen.getURL());

                      long start = System.currentTimeMillis();

                      OutputStream bos = new BufferedOutputStream(new FileOutputStream(output_path + "." + offlineCopyFileFormat));

                      bos.write(result);
                      bos.close();

                      log.debug("File saved to disk in " + (System.currentTimeMillis() - start) + "ms");
                  }
	          } catch (Exception e) {
	        	  log.error("Failed creating offline report service", e);
	          } 
	
	          CeNJobProgressHandler.getInstance().removeItem(progressStatus);
              
	          return null;
	      }
	  };
	  worker.start();
  }

	private String convertSiteNameToCode(String site) {
		String sc = null;
		try {
			sc = CodeTableCache.getCache().getSiteCode(site);
		} catch (Exception e) {
			ceh.logExceptionMsg(null, e);
		}
		return sc;
	}
}

