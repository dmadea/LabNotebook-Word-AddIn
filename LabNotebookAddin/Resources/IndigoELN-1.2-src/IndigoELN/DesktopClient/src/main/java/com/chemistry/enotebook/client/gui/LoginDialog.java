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
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.gui.health.HealthCheckDlg;
import com.chemistry.enotebook.client.gui.health.HealthCheckHandler;
import com.chemistry.enotebook.client.gui.health.ServiceHealthStatus;
import com.chemistry.enotebook.client.gui.health.SystemHealthStatusChangeListener;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.experiment.datamodel.user.LoginException;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.utils.CeNDialog;
import com.chemistry.enotebook.utils.SwingWorker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

public class LoginDialog extends CeNDialog implements SystemHealthStatusChangeListener {
	
	private static final long serialVersionUID = -1655367236721611647L;
	
	private static final Log log = LogFactory.getLog(LoginDialog.class);
	
	private JButton cancelButton;
	private JButton okButton;
	private JPanel mainPanel;
	private JPasswordField passwordTextField;
	private JTextField usernameTextField;
	private boolean loginSuccess = false;
	private Exception loginError = null;
	private static NotebookUser tmpUser;
	private JFrame taskBarFrame = null;
	private static JButton healthButton;
	private Properties properties = new Properties();
	private ImageIcon background = null;
	private JDialog frameMsg = null;
	private String impersonateUser = null;
	
	public LoginDialog() {
		initGUI();
	}

	public void initGUI() {
		try {			
			mainPanel = new JPanel() {
				private static final long serialVersionUID = 853143282943062523L;
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					
					Graphics2D g2 = (Graphics2D) g;
					
					if (background != null) {
						g2.drawImage(background.getImage(), 0, 0, this);
					}
					
					g2.drawImage(CenIconFactory.getCompanyLogo(), 5, 5, this);
					
					setOpaque(true);
					
					try {
						Font f = new Font("Tahoma", Font.BOLD, 11);
						
						g2.setFont(f);
						g2.setPaint(Color.BLUE);
						
						String str = CeNSystemProperties.getRunMode() + " System " + MasterController.getVersionInfoAsString();
						g2.drawString(str, 93, 278);
					} catch (Exception e) { 
						/* Ignored */
					}
				}
			};
			
			usernameTextField = new JTextField();
			passwordTextField = new JPasswordField();
			
			okButton = new JButton();
			cancelButton = new JButton();
			healthButton = new JButton();
						
			BorderLayout thisLayout = new BorderLayout();
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			
			this.getContentPane().setLayout(thisLayout);
			
			mainPanel.setLayout(null);
			mainPanel.setPreferredSize(new java.awt.Dimension(600, 310));
			
			this.getContentPane().add(mainPanel, BorderLayout.CENTER);
			
			usernameTextField.setPreferredSize(new java.awt.Dimension(113, 20));
			usernameTextField.setSize(new java.awt.Dimension(113, 20));
			usernameTextField.setBounds(new java.awt.Rectangle(413, 88, 113, 20));
			
			mainPanel.add(usernameTextField);
			
			usernameTextField.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent evt) {
					jTextFieldUsernameKeyPressed(evt);
				}
			});
			
			passwordTextField.setPreferredSize(new java.awt.Dimension(113, 20));
			passwordTextField.setSize(new java.awt.Dimension(113, 20));
			passwordTextField.setBounds(new java.awt.Rectangle(413, 120, 113, 20));
			
			mainPanel.add(passwordTextField);
			
			passwordTextField.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent evt) {
					jTextFieldPasswordKeyPressed(evt);
				}
			});
			
			okButton.setFocusable(false);
			okButton.setPreferredSize(new java.awt.Dimension(90, 26));
			okButton.setBounds(new java.awt.Rectangle(337, 167, 90, 26));
			okButton.setBorderPainted(false);
			okButton.setIcon(CenIconFactory.getImageIcon("images/login_ok.gif"));
			okButton.setOpaque(false);
			
			mainPanel.add(okButton);
			
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOkActionPerformed(evt);
				}
			});
			
			cancelButton.setDefaultCapable(false);
			cancelButton.setFocusable(false);
			cancelButton.setPreferredSize(new java.awt.Dimension(90, 26));
			cancelButton.setBounds(new java.awt.Rectangle(435, 167, 90, 26));
			cancelButton.setBorderPainted(false);
			cancelButton.setIcon(CenIconFactory.getImageIcon("images/login_cancel.gif"));
			cancelButton.setOpaque(false);
			
			mainPanel.add(cancelButton);
			
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed(evt);
				}
			});
						
			healthButton.setBorderPainted(false);
			healthButton.setFocusable(false);
			healthButton.setPreferredSize(new java.awt.Dimension(21, 20));
			healthButton.setBounds(new java.awt.Rectangle(560, 10, 21, 20));
			healthButton.setToolTipText("Check the Health of the " + CeNConstants.PROGRAM_NAME + " System");
			healthButton.setBorderPainted(false);
			healthButton.setOpaque(false);
			
			mainPanel.add(healthButton);
			
			healthButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					btnHealthActionPerformed(evt);
				}
			});
			
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			this.setTitle("Login To " + CeNConstants.PROGRAM_NAME);
			this.setResizable(false);
			this.setUndecorated(false);
			this.setModal(true);
			this.pack();
			this.setLocationRelativeTo(null);
			
			postInitGUI();			
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		// Make textField get the focus whenever frame is activated.
		this.addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
				if (usernameTextField.getText().length() > 0) {
					passwordTextField.requestFocusInWindow();
					String pw = new String(passwordTextField.getPassword());
					passwordTextField.setSelectionStart(pw.length());
					passwordTextField.setSelectionStart(0);
				} else
					usernameTextField.requestFocusInWindow();
			}
		});

		// Read properties file.
		try {
			properties.load(new FileInputStream(MasterController.getCeNPropertiesFile()));
			usernameTextField.setText(properties.getProperty("ntUserID"));
		} catch (FileNotFoundException e1) {
			// Ignore FileNotFound
		} catch (Exception e2) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e2);
		}
		
		showSplash(true);

		// Shadow Frame to have a taskbar item for this dialog
		taskBarFrame = new MyTaskBarFrame(this.getTitle(), this);
		this.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent evt) {
				taskBarFrame.setVisible(true);
			}
			public void windowClosing(WindowEvent evt) {
				taskBarFrame.setVisible(false);
				taskBarFrame.dispose();
			}
		});

		frameMsg = new JDialog(this);
		
		JTextArea f = new JTextArea("                                                             **** NOTICE ****\n" +
				"The information contained in this "+ CeNConstants.PROGRAM_NAME + 
				" must be maintained in confidence in accordance with Company policy and the employee " +
				"agreement with the Company.  All reasonable steps to safeguard the " + CeNConstants.PROGRAM_NAME + " against unauthorized " +
				"access, tampering, copying and to prevent damage or loss must be taken.  If the " + CeNConstants.PROGRAM_NAME + " is left " +
				"open on the desktop, the employee should lock the computer if they will be away from " +
				"their desk for any significant period of time.\n");
		f.setDisabledTextColor(new Color(180, 4, 12));
		f.setFont(new java.awt.Font("sansserif", Font.BOLD, 12));
		f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory
				.createLoweredBevelBorder()));
		f.setBounds(5, 5, 5, 5);
		f.setWrapStyleWord(true);
		f.setLineWrap(true);
		f.setEnabled(false);
		f.setSize((int)(this.getWidth() * 0.78), 150);
		
		frameMsg.getContentPane().add(f);
		frameMsg.setUndecorated(true);
		frameMsg.setSize((int)(this.getWidth() * 0.78), 150);
		frameMsg.setModal(false);
		frameMsg.setLocation(this.getX() + ((this.getWidth() - frameMsg.getWidth()) / 2), this.getY() - frameMsg.getHeight());

		// register health status listener
		HealthCheckHandler.addSystemHealthStatusChangeListener(this);
        setHealthIcon(CenIconFactory.General.HEALTH_QUESTIONABLE);
	}

	public void showSplash(boolean initial) {
		try {
    		background = CenIconFactory.getImageIcon("images/splash.jpg");
			java.awt.MediaTracker tracker = new java.awt.MediaTracker(this);
			tracker.addImage(background.getImage(), 0);
			tracker.waitForAll();
		} catch (Exception e) { /* Ignored */
		}
	}

	private void setComponentsVisible(boolean visibility) {
		passwordTextField.setVisible(visibility);
		usernameTextField.setVisible(visibility);
		okButton.setVisible(visibility);
		cancelButton.setVisible(visibility);
		usernameTextField.setVisible(visibility);
		passwordTextField.setVisible(visibility);
	}

	private void resetHealthIcon() {
        final String healthFlag = MasterController.getHealthFlag();
        if (healthFlag.equals(ServiceHealthStatus.BAD_STATUS)) {
			setHealthIcon(CenIconFactory.General.HEALTH_CRITICAL);
			displayHealthStatus();
		} else if (healthFlag.equals(ServiceHealthStatus.GOOD_STATUS)) {
			showSplash(false);
			setHealthIcon(CenIconFactory.General.HEALTH_GOOD);
			setComponentsVisible(true);
		} else if (healthFlag.equals(ServiceHealthStatus.MINIMAL_STATUS)) {
			showSplash(false);
            setHealthIcon(CenIconFactory.General.HEALTH_QUESTIONABLE);
            setComponentsVisible(true);
		}
	}

    private void setHealthIcon(String icon) {
        healthButton.setIcon(CenIconFactory.getImageIcon(icon));
    }

    /**
	 * This static method creates a new instance of this class and shows it inside a new JFrame, (unless it is already a JFrame).
	 * 
	 * It is a convenience method for showing the GUI, but it can be copied and used as a basis for your own code. * It is
	 * auto-generated code - the body of this method will be re-generated after any changes are made to the GUI. However, if you
	 * delete this method it will not be re-created.
	 */
	public void showGUI(NotebookUser user, String impersonateUser) throws LoginException {
		this.impersonateUser = impersonateUser;

		try {
			if (user == null)
				tmpUser = MasterController.getUser();
			else
				tmpUser = user;

			frameMsg.setVisible(true);
			setVisible(true);

			toFront();
			if (usernameTextField.getText().length() > 0)
				passwordTextField.requestFocusInWindow();
			else
				usernameTextField.requestFocusInWindow();
		} catch (Exception e) {
			loginSuccess = false;
			throw new LoginException("Could not show dialog.", e);
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonCancelActionPerformed(ActionEvent evt) {
		loginSuccess = false;
		closeDialog(); // Returns control to calling function - allows me to clean up things.
	}

	/** Auto-generated event handler method */
	protected void jButtonOkActionPerformed(ActionEvent evt) {
		if (validateLogin())
			closeDialog();
	}

	/** Auto-generated event handler method */
	protected void jTextFieldUsernameKeyPressed(KeyEvent evt) {
		// check for key codes
		if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
			if (passwordTextField.getPassword().length > 0)
				jButtonOkActionPerformed(null);
			else
				passwordTextField.requestFocus();
		}
	}

	/** Auto-generated event handler method */
	protected void jTextFieldPasswordKeyPressed(KeyEvent evt) {
		// check for key codes
		if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
			if (usernameTextField.getText().length() > 0)
				jButtonOkActionPerformed(null);
			else
				usernameTextField.requestFocus();
		}
	}

	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			loginSuccess = false;
			closeDialog();
		}
	}

	/** Auto-generated event handler method */
	protected void btnBypassActionPerformed(ActionEvent evt) {
		// Create a dummy user called test user
		// tmpUser.setNTUserID("Tester");
		// loginSuccess = tmpUser.ByPassAuthentication();
		// closeDialog();
	}

	protected void btnHealthActionPerformed(ActionEvent evt) {
		HealthCheckDlg healthDlg = launchHealthCheckDlg();
		if (!healthDlg.checkHealth()) {
			loginSuccess = false;
			closeDialog();
		}
	}

	protected void displayHealthStatus() {
		HealthCheckDlg healthDlg = launchHealthCheckDlg();
		healthDlg.resetButtons();
		// healthDlg.toFront();
		healthDlg.setVisible(true);
		this.toBack();
		this.setVisible(false);
		this.dispose();
	}

	/**
	 * @return
	 */
	private HealthCheckDlg launchHealthCheckDlg() {
		HealthCheckDlg healthDlg = new HealthCheckDlg(taskBarFrame);
		Point loc = this.getLocation();
		Dimension dim = this.getSize();
		healthDlg.setLocation(loc.x + (dim.width - healthDlg.getSize().width) / 2, loc.y
				+ (dim.height - healthDlg.getSize().height) / 2);
		return healthDlg;
	}

	/**
	 * @return Returns the loginError.
	 */
	public Exception getLoginError() {
		return loginError;
	}

	/**
	 * @return Returns the loginSuccess.
	 */
	public boolean isLoginSuccess() {
		return loginSuccess;
	}

	private boolean validateLogin() {
		// Validate to see if user has typed information
		if (usernameTextField.getText().length() != 0 && passwordTextField.getPassword().length != 0) {
			final LoginDialog dialog = this;
			usernameTextField.setEnabled(false);
			passwordTextField.setEnabled(false);
			okButton.setEnabled(false);
			cancelButton.setEnabled(false);
			SwingWorker loginValidator = new SwingWorker() {
				public Object construct() {
					String compoundAggregationIIOPUrl = null;
					loginError = null;
					tmpUser.setNTUserID(usernameTextField.getText());
					String pw = new String(passwordTextField.getPassword());

					try {
						if(tmpUser.authenticate(pw, impersonateUser)) {
							if (impersonateUser != null)
								tmpUser.setNTUserID(impersonateUser);
						} else {
							log.error("Unhandled condition. Failed login but throws no error.");
						}
					} catch (LoginException e) {
						loginError = e;
						tmpUser.setValid(false);
					} catch(Exception e) {
						log.error("Failed to initialize CompoundAggregationManager object: for user: " + usernameTextField.getText() + " compoundAggregation Url: " + compoundAggregationIIOPUrl, e);
					}

					return null;
				}

				public void finished() {
					// Failure equates to a null token
					if (!tmpUser.isSessionValid() || loginError != null) {
						// indicate that their username/password combination was invalid.
						loginSuccess = false;
						if (loginError != null) {
							if (loginError.getMessage().indexOf("not in COMPOUND_MANAGEMENT_EMPLOYEE") > 0) {
								JOptionPane.showMessageDialog(dialog,
										"We're sorry, your User ID was not found in COMPOUND_MANAGEMENT_EMPLOYEE (or in CeN).\n"
												+ "Please contact the Help-Desk to have an Employee Record created for you.",
										"Authentication Error", JOptionPane.ERROR_MESSAGE);
								loginSuccess = false;
								closeDialog();
							} else if (loginError.getMessage().indexOf("cannot impersonate") > 0) {
								JOptionPane.showMessageDialog(dialog,
										"We're sorry, only a superuser can impersonate another user.", "Authentication Error",
										JOptionPane.ERROR_MESSAGE);
								loginSuccess = false;
								closeDialog();
							} else if (loginError.getMessage().indexOf("Invalid Credentials") > 0) {
								loginError = new LoginException("Username/Password combination invalid.");
								JOptionPane.showMessageDialog(dialog, "Username/Password combination were not valid.",
										"Authentication Error", JOptionPane.ERROR_MESSAGE);

								usernameTextField.setEnabled(true);
								passwordTextField.setEnabled(true);
								okButton.setEnabled(true);
								cancelButton.setEnabled(true);
							} else {
								CeNErrorHandler.getInstance().logExceptionMsg(dialog, loginError);
								loginSuccess = false;
								closeDialog();
							}
						} else {
							loginError = new LoginException("Username/Password combination invalid.");
							JOptionPane.showMessageDialog(dialog, "Username/Password combination were not valid.",
									"Authentication Error", JOptionPane.ERROR_MESSAGE);

							usernameTextField.setEnabled(true);
							passwordTextField.setEnabled(true);
							okButton.setEnabled(true);
							cancelButton.setEnabled(true);
						}
					} else {
						// Check to see if user is valid
						if (tmpUser.isValid() && impersonateUser == null) {
							loginSuccess = true;
							loginError = null;
							// Write properties file.
							try {
								properties.setProperty("ntUserID", tmpUser.getNTUserID());
								properties.setProperty("siteCode", tmpUser.getSiteCode());
								properties.store(new FileOutputStream(MasterController.getCeNPropertiesFile()), null);
							} catch (Exception e) {
								CeNErrorHandler.getInstance().logExceptionMsg(null, e);
							}
						} else {
							loginSuccess = false;
							loginError = new LoginException("User status entry in database is not valid");
							JOptionPane.showMessageDialog(
											dialog,
											"You have been denied access to this system.\nIf you believe this to be in error, please contact the helpdesk to have CeN privileges re-instated.",
											"User Access Denied", JOptionPane.ERROR_MESSAGE);
						}
						closeDialog(); // Returns control to calling function.
					}
				}
			};
			loginValidator.start();
		} else
			JOptionPane.showMessageDialog(this, "You must enter a username & password");
		return loginSuccess;
	}

	private void closeDialog() {
		this.setVisible(false);
		if (taskBarFrame != null) {
			taskBarFrame.setVisible(false);
			taskBarFrame.dispose();
		}
		this.setVisible(false);
	}

	// Dummy Frame that allows us to have a taskbar icon for the initial login screen
	// Activate events are forwarded to the JDialog.
	private class MyTaskBarFrame extends JFrame {

		private static final long serialVersionUID = -2230850561722045431L;

		public MyTaskBarFrame(String title, final JDialog dialog) {
			super(title);
			setBounds(0, -100, 10, 10);
			setIconImage(CenIconFactory.getImage(CenIconFactory.General.APPLICATION));
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent evt) {
					MyTaskBarFrame.this.setVisible(false);
					MyTaskBarFrame.this.dispose();
				}

				public void windowOpened(WindowEvent evt) {
					dialog.toFront();
				}

				public void windowActivated(WindowEvent evt) {
					dialog.toFront();
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chemistry.enotebook.client.gui.health.SystemHealthStatusChangeListener#updateHealthStatus(java.lang.String)
	 */
	public void updateHealthStatus() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				resetHealthIcon();
			}
		});
	}

	public void dispose() {
		healthButton = null;
		tmpUser = null;
		HealthCheckHandler.removeSystemHealthStatusChangeListener(this);
		super.dispose();
	}
}
