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

package com.chemistry.enotebook.client.controller;

import com.chemistry.enotebook.client.controller.scheduler.TimerStatusHandler;
import com.chemistry.enotebook.client.gui.Gui;
import com.chemistry.enotebook.client.gui.LoginDialog;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.controller.GuiController;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.client.gui.health.HealthCheckHandler;
import com.chemistry.enotebook.client.gui.health.ServiceHealthStatus;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.JobModel;
import com.chemistry.enotebook.experiment.datamodel.user.LoginException;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.session.delegate.SessionTokenAccessException;
import com.chemistry.enotebook.storage.SignaturePageVO;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.common.chemistry.codetable.CodeTableCache;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.security.Policy;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * 
 *         is healthy. Scenario:
 */
public class MasterController {
	static {
        getApplicationDirectory();            
	}
	
	private static final Log log = LogFactory.getLog(MasterController.class);
	
	private static GuiController guiCtlr = null;
	protected static NotebookUser user = null; 
	private static MasterController instance = null;
	private static boolean systemStateValid = false;
	private static boolean superUserFlag = false;
	private DeleteTempFiles delTempFiles = null;
	private static String healthFlag = ServiceHealthStatus.GOOD_STATUS;
    
	// private String middleTierUrl = "";
	
	private static String version = "";
	/*
	 * JNDI Provider URL in format of protocol://url:port or protocol://url:port,url:port for cluster URL
	 */
	public static Date CeNStartTime = null;
	
	/**
	 * Used for handling timeout for logout
	 */
	private Timer logoutTimer = null;

    private static String impersonateUser = null;

    
	protected MasterController() {
	}

	public static MasterController getInstance() {
		if (instance == null)
			createInstance();
		return instance;
	}

	private static synchronized void createInstance() {
		if (instance == null) {
			instance = new MasterController();
			instance.init();
		}
	}

	public static void runInBackground(Runnable aRunnable) {
		Thread initThread = new Thread(aRunnable);
		initThread.setDaemon(true);
		initThread.setPriority(Thread.NORM_PRIORITY - 2);
		initThread.start();
	}

	private void init() {
		setGlobalLookAndFeel();
		version = initVersionInfoAsString();
	}

	static private void configureLogging() {
		URL path = getLogConfigFile();
		try {
			PropertyConfigurator.configure(path);
			if (log.isDebugEnabled()) {
				log.debug("log4j properties loaded from " + path);
			}
		} catch (Exception e) {
			log.warn("Failed to configure Logging from local file: " + path, e);
		}
	}

	private static URL getLogConfigFile() {
		try {
			String externalPath = getApplicationDirectory() + File.separator + "conf" + File.separator + "log4j.";
			
			if (new File(externalPath + "properties").exists()) {
				return new URL("file:/" + externalPath + "properties");
			}
			
			if (new File(externalPath + "xml").exists()) {
				return new URL("file:/" + externalPath + "xml");
			}
			
			return MasterController.class.getClassLoader().getResource("log4j.properties");
		} catch (Exception e) {
			log.warn("Error getting config file: ", e);
		}
		
		return null;
	}
	
	public void setGlobalLookAndFeel() {
		try {
			try {
				UIManager.setLookAndFeel(new WindowsLookAndFeel());
			} catch (Exception e) {
				// If Windows L&F is crashed - seems like we running on other OS, so try to use Plastic L&F
				try {
					UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
				} catch (Exception e1) {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void authenticateUser(final boolean superUserLogin) {
		if (user == null) {
			user = new NotebookUser();
		}
		user.setSuperUserFlag(superUserLogin);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                boolean retVal = false;

                LoginDialog ld = new LoginDialog();
                try {
                    ld.showGUI(user, impersonateUser);
                    retVal = ld.isLoginSuccess();
                    if (!retVal)
                        dispose();
                } catch (LoginException e) {
                    JOptionPane.showMessageDialog(ld, "The application will close.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    ld.setVisible(false);
                    ld.dispose();
                    ld = null;
                }
                processAuthResult(retVal);
            }
        });
	}

    private boolean superUserLogin;

	protected void startApp(boolean superUserLogin) {		
        this.superUserLogin = superUserLogin;

        // Initialize Error Handler
        CeNErrorHandler ceh = CeNErrorHandler.getInstance();
        try {
            // ceh.setErrorLevel(CeNErrorHandler.LOG_TO_OPTIONPANE);
            ceh.setErrorLevel((byte) (CeNErrorHandler.LOG_TO_SERVER | CeNErrorHandler.LOG_TO_OPTIONPANE));
        } catch (Exception e) {
            // no GUI to use.  Null will simply refer to the main window
            ceh.logExceptionMsg(null, e);
        }

        if (checkSystemStatus() || superUserLogin) {
            reloadCodeTableCache();
            authenticateUser(superUserLogin);
            // Check the Health of the system
            startHealthCheck();
        } else
            dispose();
    }

	private void reloadCodeTableCache() {
		new Thread(new Runnable() {
			public void run() {
				try { CodeTableCache.getCache(); } catch (Exception e) { }
			}
		}).start();
	}

	private void processAuthResult(boolean processAuthResult) {
        if (processAuthResult) {
            // setWebViewerLocation();
            if (user != null && superUserLogin && user.isSuperUser())
                log.info("Super User Status GRANTED !");
            else if (user != null && superUserLogin && !user.isSuperUser())
                log.info("Super User Status DENIED !");
            try {
                if (log.isInfoEnabled()) {
                    log.info("starting GuiController");
                }
                guiCtlr = new GuiController();
                if (log.isInfoEnabled()) {
                    log.info("starting logUsage");
                }
                // FlashDialog flash = FlashDialog.showNewsFlash(guiCtlr.getGUIComponent());// As per Paul's suggestions.
            } catch (Exception e) {
                // no GUI head may be available.
                CeNErrorHandler.getInstance().logExceptionMsg(null, e);
            }
            if (log.isInfoEnabled()) {
                log.info("starting backgroundTasks.waitTillComplete()");
            }
            if (log.isInfoEnabled()) {
                log.info("calling guiCtlr.showGui()");
            }
            guiCtlr.showGui();
            deleteTempFiles();
            if (log.isInfoEnabled()) {
                log.info("startApp finished");
            }
            
            startTimerStatusTask();
            
        } else
            dispose();
    }

    private void startTimerStatusTask() {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				try {
		        	StorageDelegate delegate = ServiceController.getStorageDelegate();
		        	TimerStatusHandler handler = TimerStatusHandler.getInstance();
		        	
		    		List<SignaturePageVO> forSign = delegate.getExperimentsBeingSigned(MasterController.getUser().getNTUserID());
		    		
		    		for (SignaturePageVO vo : forSign) {
		    			handler.addSignatureTask(vo.getNotebook() + "-" + vo.getExperiment() + "v" + vo.getVersion());
		    		}
		    		
		    		List<JobModel> forReg = new ArrayList<JobModel>();
		    		
		    		forReg.addAll(delegate.getAllRegistrationJobs(MasterController.getUser().getNTUserID(), CeNConstants.JOB_OPEN));
		    		forReg.addAll(delegate.getAllRegistrationJobs(MasterController.getUser().getNTUserID(), CeNConstants.JOB_PROGRESS));
		    		
		    		for (JobModel job : forReg) {
		    			String nbRef = job.getUserMessage().getNotebook() + "-" + job.getUserMessage().getPage() + "v" + job.getUserMessage().getVersion();
		    			handler.addRegistrationTask(nbRef, job.getJobID());
		    		}
		    	} catch (Exception e) {
		    		log.error("Error starting TimerStatusHandler: ", e);
		    	}
			}
		}).start();    	
	}

	private void deleteTempFiles() {
		delTempFiles = new DeleteTempFiles();
		delTempFiles.setPriority(Thread.MIN_PRIORITY);
		delTempFiles.start();
	}

	public void dispose() {
		logoutTimer = new Timer();

		try {
			if (delTempFiles != null) {
				delTempFiles.interrupt();
			}
		} catch (SecurityException secEx) {
			JOptionPane.showMessageDialog(null,
							"MasterController does not have access to DeleteTempFiles Thread. This message has been thrown from dispose method of MC",
							"Security Exception", JOptionPane.ERROR_MESSAGE);
		} 
		
		if (guiCtlr != null) {
			guiCtlr.dispose();
		}
		
		if (user != null) {
			if (user.isSessionValid()) {
				try {
					log.debug("Logging out...");
					
					logoutTimer.schedule(new ForceLogoutTask(), 10 * 1000);
					
					user.logout();
				} catch (SessionTokenAccessException e2) {
					log.debug("Error during logout:", e2);
				}
			}
			user.dispose();
		}
		
		System.exit(0);
	}
	
	public static Component getGuiComponent() {
		Component result = null;
		if(guiCtlr != null) {
			result = guiCtlr.getGUIComponent();
		}
		return result;
	}

	/**
	 * CeNDesktopClient entry point.
	 *
	 */
	public static void main(String[] args) {
        SLF4JBridgeHandler.install();
        
		CeNStartTime = java.util.Calendar.getInstance().getTime();
		log.info("CeN Start Time: " + CeNStartTime);
		
		configureSecurityPolicy();
		configureLogging();
		
        // Set Client side CORBA Timeout
		// parameters for CORBA timeouts 
		// <initial time to wait: max read giop header time to wait: max read message time to wait: backoff factor>

//		log.debug("client side com.sun.CORBA.transport.ORBTCPReadTimeouts is: " + System.getProperty("com.sun.CORBA.transport.ORBTCPReadTimeouts"));
//		System.setProperty("com.sun.CORBA.transport.ORBTCPReadTimeouts", "100:240000:300:5"); // set to 600 seconds, 
//		log.debug("client side com.sun.CORBA.transport.ORBTCPReadTimeouts changed to: " + System.getProperty("com.sun.CORBA.transport.ORBTCPReadTimeouts"));
//		
//		// covering bases as it appears different versions of Java use different keys for the ORB properties
//		log.debug("client side com.sun.corba.ee.transport.ORBTCPTimeouts is: " + System.getProperty("com.sun.corba.ee.transport.ORBTCPTimeouts"));
//		System.setProperty("com.sun.corba.ee.transport.ORBTCPTimeouts", "100:360000:300:5"); // set to 600 seconds, 
//		log.debug("client side com.sun.corba.ee.transport.ORBTCPTimeouts changed to: " + System.getProperty("com.sun.corba.ee.transport.ORBTCPTimeouts"));
		
		// Set client side WebLogic Complete Message Timeout: affects loading of pages.
		log.debug("client side weblogic.CompleteMessageTimeout is: " + System.getProperty("weblogic.CompleteMessageTimeout"));
		System.setProperty("weblogic.CompleteMessageTimeout", "300");
		log.debug("client side weblogic.CompleteMessageTimeout changed to: " + System.getProperty("weblogic.CompleteMessageTimeout", ""));
	
		// Set client side WebLogic Max Message Size: affects loading of pages.
		log.debug("client side weblogic.MaxMessageSize is: " + System.getProperty("weblogic.MaxMessageSize"));
		System.setProperty("weblogic.MaxMessageSize", "200000000");
		log.debug("client side weblogic.MaxMessageSize changed to: " + System.getProperty("weblogic.MaxMessageSize", ""));
	
		try {
			// PropertyConfigurator.configure("./conf/log4j.properties");
			
			systemStateValid = true;
			MasterController mc = MasterController.getInstance();
			boolean superUserLogin = false;
	        if (args != null && args.length > 0) {
	        	for (int i=0; i < args.length; i++) {
	        		if (args[i].equalsIgnoreCase("/superuser")) 
	        			superUserLogin = true;
	        		if (args[i].equalsIgnoreCase("/impersonate")) {
	        			if (i+1 >= args.length)
	        				System.out.println("Invalid use of Impersonate, username required.");
	        			else {
	        				impersonateUser = args[++i];
	        				if (impersonateUser != null) impersonateUser = impersonateUser.trim().toUpperCase();
	        			}
	        		}
	        	}
	        }
	
	        mc.startApp(superUserLogin);
		} catch (Throwable e) {
			log.error("Unexpected error encountered.  Exiting applicaiton.", e);
		}
	}

	private static void configureSecurityPolicy() {
		// this seems to be needed to grant permissions to weblogic.jar v8.1 sp4 in javaws environment
		// should not be needed otherwise
		URL policyUrl = Thread.currentThread().getContextClassLoader().getResource("indigo_eln_java.policy");
        if (policyUrl != null) {
            System.setProperty("java.security.policy", policyUrl.toString());
        }
        Policy.getPolicy().refresh();
	}
	
	public static NotebookUser getUser() {
		return user;
	}

	public static boolean getSuperUserFlag() {
		return superUserFlag;
	}

	public static GuiController getGuiController() {
		return guiCtlr;
	}

	public static Gui getGUIComponent() {
		return (guiCtlr == null) ? null : guiCtlr.getGUIComponent();
	}
		
	public static String getHealthFlag() {
		return healthFlag;
	}

	public static void setHealthFlag(String flag) {
		healthFlag = flag;
	}

	public static String getVersionInfoAsString() {
		return version;
	}
	
	public static String initVersionInfoAsString() {
		StringBuilder versionInfo = new StringBuilder();
		
		try {
		    versionInfo.append(CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_VERSION_NUMBER));
		} catch (Throwable t) {
            log.error("Error getting application version: ", t);
		}
		
		return versionInfo.toString();
	}

	public static String getApplicationDirectory() {
		String path = CeNConstants.getApplicationDirectory();
		File dir = new File(path);
		if (!dir.exists())
			if (!dir.mkdir())
				throw new RuntimeException("Could not create directory " + path);
		return path;
	}

	public static boolean isValidSystemState() {
		return systemStateValid;
	}

	private void startHealthCheck() {
		Thread healthCheckThread = new Thread() {
			public void run() {
				try {
					HealthCheckHandler.checkDBHealth();
					HealthCheckHandler.checkServiceHealth();
					// Finally notify the listener
					HealthCheckHandler.notifySystemHealthStatusChangeListeners();
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(getGuiComponent(), e);
				}
			}
		};
		healthCheckThread.start();
	}

	public boolean checkSystemStatus() {
		boolean status = true;
		String propVal;
		try {
			propVal = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_SYSTEM_STATUS);
			if (!StringUtils.equalsIgnoreCase(propVal, "enabled")) {
				propVal = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_SYSTEM_STATUS_MSG);
				StringBuilder msg = new StringBuilder("The CeN System is currently unavailable, please try again later.");
				if (StringUtils.isNotBlank(propVal)) {
					msg.append("\n\n");
					msg.append("Reason: ").append(propVal.replaceAll("/n", "\n"));
				}
				JOptionPane.showMessageDialog(null, msg.toString(), "System Down", JOptionPane.ERROR_MESSAGE);
				status = false;
			}
		} catch (Throwable t) {
			log.error("Error getting system status: ", t);
			CeNErrorHandler.getInstance().logExceptionMsg(null, t);
		}
		return status;
	}

	public class ForceLogoutTask extends TimerTask {
		@Override
		public void run() {
			logoutTimer.cancel();
			user.dispose();
			System.exit(0);
		}
	}

	class BackgroundStartup extends Thread {
		public BackgroundStartup() {
			start();
		}

		public void run() {
			// Access Code Table Service
			try {
				CodeTableCache.getCache();
			} catch (Throwable e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}

		public void waitTillComplete() {
			try {
				join();
			} catch (Throwable e) {
				log.error("Failed to connect to thread.  CeN is stuck and won't get up!", e);
//				interrupt();
			}
		}
	}

	/**
	 * Workaround Java WebStart Bug #4845341 - Force Integration.
	 * 
	 * <pre>
	 *      &lt;warning&gt;Your code might need to be signed to have access to the
	 *      EventQueue.&lt;/warning&gt;
	 *      &lt;p/&gt;
	 *      &lt;p/&gt;
	 *      The workaround is activated for 8 seconds if Java WebStart 1.4.2
	 *      is detected. After 8 seconds it will deactivate itself if it is
	 *      still active. In that time frame, if a WindowEvent about the
	 *      Desktop Integration Window is intercepted, the dialog will be forced
	 *      to YES.
	 *      &lt;p/&gt;
	 *      Instructions:
	 *      &lt;p/&gt;
	 *      // Declaration
	 *      private WorkAround4845341 wa;
	 *      &lt;p/&gt;
	 *      // Just before showing up your login dialog:
	 *      wa = new WorkAround4845341();
	 *      &lt;p/&gt;
	 *      // Before disposing your login dialog, we need to make sure that we
	 *      // poped out our EventQueue, in case it is still active. We don't want
	 *      // our thread to pop out another EventQueue that could be installed on
	 *      // the EventQueue stack when your app continue to initialize itself.
	 *      wa.shutdown();
	 *      &lt;p/&gt;
	 *      Disclaimer:
	 *      &lt;p/&gt;
	 *      This code is distributed in the hope that it will be useful,
	 *      but WITHOUT ANY WARRANTY. USE IT AT YOUR OWN RISKS.
	 *      &lt;p/&gt;
	 *      History:
	 *      Version 1.0, 1.1, 1.2 Martin Miller
	 *      Dispose the Desktop Integration Windows if detected.
	 *      &lt;p/&gt;
	 *      Version 1.3 Daniel Bridenbecker
	 *      Don't dispose the window, but answer it by selecting YES automatically.
	 *      &lt;p/&gt;
	 *      The instructions below talks about starting the workaround just before
	 *      you start your login dialog. This does give the user a change to answer
	 *      the question, but if you start it right away, you have the opportunity
	 *      to have the dialog barely appear.
	 *      &lt;p/&gt;
	 *      Version 1.4 Martin Miller
	 *      Commented out the dispose, since answering the JDialog will dispose it.
	 *      &lt;p/&gt;
	 * </pre>
	 * 
	 * 
	 * 
	 * @version 1.4 2004-01-07
	 */
	public class WorkAround4845341 extends Thread {
		private static final long timeToLive = 8 * 1000;
		private boolean shutdown = false;
		private WorkAround4845341.DesktopIntegrationKiller dik = null;

		public WorkAround4845341() {
			setDaemon(true);
			String javawsVersion = System.getProperty("javawebstart.version", "");
			if (javawsVersion.startsWith("javaws-1.4.2")) {
				// System.out.println("WA4845341: Detected Java WebStart 1.4.2*");
				try {
					EventQueue eq = Toolkit.getDefaultToolkit().getSystemEventQueue();
					dik = new WorkAround4845341.DesktopIntegrationKiller();
					eq.push(dik);
				} catch (Exception e) {
					System.err.println("WA4845341: Could not get System's EventQueue.");
				}
				start();
			}
		}

		public void run() {
			try {
				sleep(timeToLive);
			} catch (InterruptedException ie) {
			}
			shutdown();
		}

		public void shutdown() {
			if ((shutdown == false) && (dik != null)) {
				shutdown = true;
				dik.pop();
			}
		}

		/**
		 * This EventQueue looks for WindowsEvents about the Desktop Integration Window. <p/> If an event about that window is
		 * intercepted, the window will be answered to YES.
		 */
		class DesktopIntegrationKiller extends EventQueue {
			public DesktopIntegrationKiller() {
				// System.out.println("WA4845341: Started.");
			}

			/**
			 * Force Desktop Integration to add shortcut to desktop
			 */
			private void setDesktopIntegrationOptionYes(WindowEvent e) {
				Object obj = e.getSource();
				if (obj instanceof JDialog) {
					JDialog dialog = (JDialog) obj;
					Container container = dialog.getContentPane();
					Component comp = container.getComponent(0);
					if (comp instanceof JOptionPane) {
						System.out.println("WA4845341: Answering Integration Dialog to YES.");
						JOptionPane jop = (JOptionPane) comp;
						Object[] options = jop.getOptions();
						jop.setValue(options[0]); // Zero is the first
						// button (Yes)
					}
				}
			}

			protected void dispatchEvent(AWTEvent e) {
				if (e instanceof WindowEvent) {
					WindowEvent we = (WindowEvent) e;
					String name = we.getWindow().getAccessibleContext().getAccessibleName();
					if (name != null) {
						// Known to work locales: fr, en, and de
						if (name.endsWith("Integration")) {
							we.getWindow().setVisible(false);
							setDesktopIntegrationOptionYes(we); // answer dialog
							WorkAround4845341.this.shutdown();
							return;
						}
					}
				}
				super.dispatchEvent(e);
			}

			public void pop() {
				try {
					super.pop();
					// System.out.println("WA4845341: Stopped.");
				} catch (EmptyStackException ese) {
				}
			}
		}
	}

	public static String getCeNPropertiesFile() throws Exception {
		return MasterController.getApplicationDirectory() + File.separator + "IndigoELN.Properties";
	}
	
	public static boolean isParallelViewEnabled() {
		return getUser().isSuperUser() || GuiController.isPropertyEnabled(CeNSystemXmlProperties.PROP_VIEW_PARALLEL_ENABLED);
	}
	
	public static boolean isParallelRepeatEnabled() {
		return getUser().isSuperUser() || GuiController.isPropertyEnabled(CeNSystemXmlProperties.PROP_REPEAT_PARALLEL_ENABLED);
	}
	
	public static boolean isParallelCreateEnabled() {
		return getUser().isSuperUser() || GuiController.isPropertyEnabled(CeNSystemXmlProperties.PROP_NEW_PARALLEL_ENABLED);
	}
	
	public static boolean isVnvEnabled() {
		return GuiController.isPropertyEnabled(CeNSystemXmlProperties.PROP_VNV_ENABLED);
	}
	
	public static boolean isRunningOnWindows() {
		 String os = System.getProperty("os.name");
		 return (os != null && os.startsWith("Win"));
	}
}

