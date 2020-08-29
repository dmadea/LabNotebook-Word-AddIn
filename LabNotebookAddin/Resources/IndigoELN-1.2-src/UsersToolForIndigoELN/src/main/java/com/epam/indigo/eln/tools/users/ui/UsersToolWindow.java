/****************************************************************************
 * Copyright (C) 2009-2012 EPAM Systems
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

package com.epam.indigo.eln.tools.users.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.StringUtils;

import com.epam.indigo.eln.tools.users.core.User;
import com.epam.indigo.eln.tools.users.db.DBService;

public class UsersToolWindow extends JFrame implements ActionListener {

	private static final long serialVersionUID = -6214048726840872554L;

	private JPanel contentPane;

    private JButton addButton;
	private JButton saveButton;
	private JButton deleteButton;

	private JButton xmlMetadataButton;
	private JButton myReagentsButton;
	private JButton auditLogButton;

	private JList usersList;

	private JLabel usernameLabel;
	private JLabel passwordLabel;
	private JLabel firstnameLabel;
	private JLabel lastnameLabel;
	private JLabel employeeIdLabel;
	private JLabel sitecodeLabel;
	private JLabel emailLabel;

	private JTextField usernameTextField;
	private JTextField passwordTextField;
	private JTextField firstnameTextField;
	private JTextField lastnameTextField;
	private JTextField employeeIdTextField;
	private JTextField sitecodeTextField;
	private JTextField emailTextField;

	private JCheckBox superuserCheckBox;

	private DBService dbService;
	private User selectedUser;

	private boolean addingUser = false;

	public UsersToolWindow(DBService dbService) {
		this.dbService = dbService;
		init();
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source.equals(addButton))
			addUser();

		if (source.equals(saveButton))
			saveUser();

		if (source.equals(deleteButton))
			deleteUser();

		if (source.equals(xmlMetadataButton))
			showXmlMetadata();

		if (source.equals(myReagentsButton))
			showMyReagents();

		if (source.equals(auditLogButton))
			showAuditLog();

		if (source.equals(superuserCheckBox))
			changeSuperUser();
	}

	private void init() {
		initControls();
		initWindow();
	}

	private void initControls() {
		contentPane = new JPanel();

        JPanel usersPanel = new JPanel();
        JPanel infoPanel = new JPanel();

		addButton = new JButton(UIConstants.ADD_USER);
		saveButton = new JButton(UIConstants.SAVE_USER);
		deleteButton = new JButton(UIConstants.DELETE_USER);

		xmlMetadataButton = new JButton(UIConstants.SHOW_XML_METADATA);
		myReagentsButton = new JButton(UIConstants.SHOW_MY_REAGENT_LIST);
		auditLogButton = new JButton(UIConstants.SHOW_AUDIT_LOG);

		usersList = new JList();

		usernameLabel = new JLabel(UIConstants.USERNAME);
		passwordLabel = new JLabel(UIConstants.PASSWORD);
		firstnameLabel = new JLabel(UIConstants.FIRST_NAME);
		lastnameLabel = new JLabel(UIConstants.LAST_NAME);
		employeeIdLabel = new JLabel(UIConstants.EMPLOYEE_ID);
		sitecodeLabel = new JLabel(UIConstants.SITE_CODE);
		emailLabel = new JLabel(UIConstants.EMAIL);

		usernameTextField = new JTextField();
		passwordTextField = new JPasswordField();
		firstnameTextField = new JTextField();
		lastnameTextField = new JTextField();
		employeeIdTextField = new JTextField();
		sitecodeTextField = new JTextField();
		emailTextField = new JTextField();

		superuserCheckBox = new JCheckBox(UIConstants.SUPERUSER);

		usersList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (addingUser)
					addingUser = false;
				refresh(e.getFirstIndex());
			}
		});

		addButton.addActionListener(this);
		saveButton.addActionListener(this);
		deleteButton.addActionListener(this);

		xmlMetadataButton.addActionListener(this);
		myReagentsButton.addActionListener(this);
		auditLogButton.addActionListener(this);

		superuserCheckBox.addActionListener(this);

		contentPane.setLayout(new BorderLayout());
		contentPane.add(usersPanel, BorderLayout.WEST);
		contentPane.add(infoPanel, BorderLayout.CENTER);

		JPanel usersButtonsPanel = new JPanel(new GridLayout(3, 1));
		usersButtonsPanel.add(addButton);
		usersButtonsPanel.add(saveButton);
		usersButtonsPanel.add(deleteButton);

		JScrollPane usersListScroll = new JScrollPane(usersList);

		usersPanel.setBorder(new TitledBorder(UIConstants.USERS));
		usersPanel.setLayout(new BorderLayout());
		usersPanel.add(usersListScroll, BorderLayout.CENTER);
		usersPanel.add(usersButtonsPanel, BorderLayout.SOUTH);

		JPanel infoTopPanel = new JPanel(new BorderLayout());

		JPanel infoTopLeftPanel = new JPanel(new GridLayout(8, 1, 5, 5));
		infoTopLeftPanel.add(usernameLabel);
		infoTopLeftPanel.add(passwordLabel);
		infoTopLeftPanel.add(sitecodeLabel);
		infoTopLeftPanel.add(firstnameLabel);
		infoTopLeftPanel.add(lastnameLabel);
		infoTopLeftPanel.add(employeeIdLabel);
		infoTopLeftPanel.add(emailLabel);
		infoTopLeftPanel.add(superuserCheckBox);

		JPanel infoTopCenterPanel = new JPanel(new GridLayout(8, 1, 5, 5));
		infoTopCenterPanel.add(usernameTextField);
		infoTopCenterPanel.add(passwordTextField);
		infoTopCenterPanel.add(sitecodeTextField);
		infoTopCenterPanel.add(firstnameTextField);
		infoTopCenterPanel.add(lastnameTextField);
		infoTopCenterPanel.add(employeeIdTextField);
		infoTopCenterPanel.add(emailTextField);
		infoTopCenterPanel.add(new JLabel());

		infoTopPanel.add(infoTopLeftPanel, BorderLayout.WEST);
		infoTopPanel.add(infoTopCenterPanel, BorderLayout.CENTER);

		JPanel infoButtonsPanel = new JPanel(new GridLayout(3, 1));
		infoButtonsPanel.add(xmlMetadataButton);
		infoButtonsPanel.add(myReagentsButton);
		infoButtonsPanel.add(auditLogButton);

		infoPanel.setBorder(new TitledBorder(UIConstants.USER_INFO));
		infoPanel.setLayout(new BorderLayout());
		infoPanel.add(infoTopPanel, BorderLayout.NORTH);
		infoPanel.add(infoButtonsPanel, BorderLayout.SOUTH);

		refresh();
	}

	private void addUser() {
		refresh();
		addingUser = true;
		enableControls();
		usernameTextField.requestFocus();
	}

	private void saveUser() {
		if (StringUtils.isBlank(usernameTextField.getText())) {
			JOptionPane.showMessageDialog(this, "Please enter Username!");
			usernameTextField.requestFocus();
			return;
		}

		if (selectedUser == null)
			selectedUser = new User();

		selectedUser.setUsername(usernameTextField.getText());
		selectedUser.setPassword(passwordTextField.getText());
		selectedUser.setSitecode(sitecodeTextField.getText());
		selectedUser.setFirstname(firstnameTextField.getText());
		selectedUser.setLastname(lastnameTextField.getText());
		selectedUser.setEmployeeId(employeeIdTextField.getText());
		selectedUser.setEmail(emailTextField.getText());
		selectedUser.setSuperUser(superuserCheckBox.isSelected());

		try {
			dbService.updateUser(selectedUser);
		} catch (Exception e) {
			error("Error saving user:", e);
		}

		String savedUsername = selectedUser.getUsername();

		if (addingUser)
			addingUser = false;

		refresh();

		usersList.setSelectedIndex(getListIndex(savedUsername, usersList));
	}

	private void deleteUser() {
		if (selectedUser != null) {
			try {
				dbService.deleteUser(selectedUser.getUsername());
			} catch (Exception e) {
				error("Error deleting user:", e);
			}
		}
		if (addingUser)
			addingUser = false;
		refresh();
	}

	private void changeSuperUser() {
		if (selectedUser != null) {
			selectedUser.setSuperUser(superuserCheckBox.isSelected());
		}
	}

	private void showXmlMetadata() {
		if (selectedUser != null) {
			showTextDialog(selectedUser.getUsername() + "'s "
					+ UIConstants.XML_METADATA, selectedUser.getXmlMetadata());
		}
	}

	private void showMyReagents() {
		if (selectedUser != null) {
			showTextDialog(selectedUser.getUsername() + "'s "
					+ UIConstants.MY_REAGENT_LIST,
					selectedUser.getMyReagentList());
		}
	}

	private void showAuditLog() {
		if (selectedUser != null) {
			showTextDialog(selectedUser.getUsername() + "'s "
					+ UIConstants.AUDIT_LOG, selectedUser.getAuditLog());
		}
	}

	private int getListIndex(String s, JList list) {
		ListModel model = list.getModel();
		for (int i = 0; i < model.getSize(); ++i)
			if (StringUtils.equals(s, (String) model.getElementAt(i)))
				return i;
		return -1;
	}

	private void showTextDialog(String title, String text) {
		TextDialog dialog = new TextDialog(this, text);
		dialog.setTitle(title);
		dialog.setVisible(true);
	}

	private void error(String s, Throwable t) {
		JOptionPane.showMessageDialog(this, s + "\n\n" + t.getMessage());
		t.printStackTrace();
	}

	private void refresh() {
		refresh(-1);
	}

	private void refresh(int index) {
		if (index == -1) {
			clear();

			String[] users = null;

			try {
				users = dbService.getAllUsers();
			} catch (Exception e) {
				error("Error getting all users:", e);
			}

			if (users != null)
				usersList.setListData(users);
		} else {
			selectedUser = null;

			try {
				selectedUser = dbService.getUser((String) usersList
						.getSelectedValue());
			} catch (Exception e) {
				error("Error getting user:", e);
			}

			if (selectedUser != null) {
				usernameTextField.setText(selectedUser.getUsername());
				passwordTextField.setText(selectedUser.getPassword());
				firstnameTextField.setText(selectedUser.getFirstname());
				lastnameTextField.setText(selectedUser.getLastname());
				employeeIdTextField.setText(selectedUser.getEmployeeId());
				sitecodeTextField.setText(selectedUser.getSitecode());
				emailTextField.setText(selectedUser.getEmail());
				superuserCheckBox.setSelected(selectedUser.isSuperUser());
			}
		}

		enableControls();

		usersList.requestFocus();
	}

	private void enableControls() {
		boolean enableButtons = (usersList.getSelectedIndex() != -1);
		boolean enableEditInfo = enableButtons || addingUser;

		addButton.setEnabled(!addingUser);
		saveButton.setEnabled(enableEditInfo);
		deleteButton.setEnabled(enableEditInfo);

		usernameLabel.setEnabled(enableEditInfo);
		passwordLabel.setEnabled(enableEditInfo);
		firstnameLabel.setEnabled(enableEditInfo);
		lastnameLabel.setEnabled(enableEditInfo);
		employeeIdLabel.setEnabled(enableEditInfo);
		sitecodeLabel.setEnabled(enableEditInfo);
		emailLabel.setEnabled(enableEditInfo);

		usernameTextField.setEnabled(enableEditInfo);
		passwordTextField.setEnabled(enableEditInfo);
		firstnameTextField.setEnabled(enableEditInfo);
		lastnameTextField.setEnabled(enableEditInfo);
		employeeIdTextField.setEnabled(enableEditInfo);
		sitecodeTextField.setEnabled(enableEditInfo);
		emailTextField.setEnabled(enableEditInfo);
		superuserCheckBox.setEnabled(enableEditInfo);

		xmlMetadataButton.setEnabled(enableEditInfo);
		myReagentsButton.setEnabled(enableEditInfo);
		auditLogButton.setEnabled(enableEditInfo);

		if (addingUser)
			deleteButton.setText(UIConstants.CANCEL);
		else
			deleteButton.setText(UIConstants.DELETE_USER);
	}

	private void clear() {
		usersList.clearSelection();
		usersList.setListData(new Object[0]);

		usernameTextField.setText(StringUtils.EMPTY);
		passwordTextField.setText(StringUtils.EMPTY);
		firstnameTextField.setText(StringUtils.EMPTY);
		lastnameTextField.setText(StringUtils.EMPTY);
		employeeIdTextField.setText(StringUtils.EMPTY);
		sitecodeTextField.setText(StringUtils.EMPTY);
		emailTextField.setText(StringUtils.EMPTY);

		superuserCheckBox.setSelected(false);

		selectedUser = null;
	}

	private void initWindow() {
		setContentPane(contentPane);
		setIcon();
		pack();
		setTitle(UIConstants.WINDOW_TITLE);
//		setResizable(false);
		setSize(600, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void setIcon() {
		try {
			setIconImage(ImageIO.read(UsersToolWindow.class.getClassLoader()
					.getResource("icon.gif")));
		} catch (Exception e) {
			/* Ignored */
		}
	}
}
