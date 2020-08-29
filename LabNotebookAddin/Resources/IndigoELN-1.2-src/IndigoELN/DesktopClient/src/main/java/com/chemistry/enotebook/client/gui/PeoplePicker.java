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
import com.chemistry.enotebook.client.gui.common.utils.CeNLabel;
import com.chemistry.enotebook.person.classes.IPerson;
import com.chemistry.enotebook.person.exceptions.PersonServiceException;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;
import java.util.*;
import java.util.List;

public class PeoplePicker extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -178148095789637125L;

	private static Log log = LogFactory.getLog(PeoplePicker.class.getName());

	public final static String LABEL_FIRSTNAME = "First Name:";
	public final static String LABEL_LASTNAME = "Last Name:";
	public final static String LABEL_SEARCH_RESULTS = "Search Results:";
	public final static String TITLE_PERSON_PICKER = "Find Person";
	public final static String BUTTON_LABEL_SEARCH = "Search";
	public final static String BUTTON_LABEL_CANCEL = "Done";
	public final static String BUTTON_LABEL_OK = "Add";

	public static int SELECT_MODE_PEOPLE = 0;
	public static int SELECT_MODE_PERSON = 1;

	private JTextField firstnameField = new JTextField();
	private JTextField lastnameField = new JTextField();
	private JLabel firstnameLabel = new CeNLabel(LABEL_FIRSTNAME);
	private JLabel lastnameLabel = new CeNLabel(LABEL_LASTNAME);
	private JButton searchButton = new JButton(BUTTON_LABEL_SEARCH);
	private JButton cancelButton = new JButton(BUTTON_LABEL_CANCEL);
	private JButton applyButton = new JButton(BUTTON_LABEL_OK);
	private JLabel searchresultsLabel = new CeNLabel(LABEL_SEARCH_RESULTS);
	private JList searchResultList = new JList();
	private ArrayList searchResultIdList = new ArrayList(); // vb 11/7
	private IPerson people[] = null;
	private BigInteger searchResultCnt = new BigInteger("100");
	private int mode = SELECT_MODE_PERSON;
	private PersonDelegate delegate;
	private PersonFinder personFinder;
	private boolean oneTime = true;
	
	public PeoplePicker(PersonFinder personFinder) {
		super((Frame)MasterController.getGuiComponent(), TITLE_PERSON_PICKER, true);
		this.personFinder = personFinder;
		initGUI();
		try {
			delegate = new PersonDelegate();
		} catch (Exception e) {
			log.error("Failure connecting to personService", e);
			// ExceptionHandler.showErrorMessage(e.getMessage(), "Person
			// Chooser");
		}
	}
	
	public PeoplePicker(Frame parent, PersonFinder personFinder) {
		super(parent, TITLE_PERSON_PICKER, true);
		this.personFinder = personFinder;
		initGUI();
		try {
			delegate = new PersonDelegate();
		} catch (Exception e) {
			log.error("Failure connecting to personService", e);
			// ExceptionHandler.showErrorMessage(e.getMessage(), "Person Chooser");
		}
	}

	public PeoplePicker(Frame parent, PersonFinder personFinder, boolean oneTime) {
		super(parent, TITLE_PERSON_PICKER, true);
		this.oneTime = oneTime;
		this.personFinder = personFinder;
		initGUI();
		try {
			delegate = new PersonDelegate();
		} catch (Exception e) {
			log.error("Failure connecting to personService", e);
			// ExceptionHandler.showErrorMessage(e.getMessage(), "Person Chooser");
		}
	}
	
	public final static PersonFinder EMPTY_PERSON_FINDER = new PersonFinder() {
		public void findPersonNTID(String name) {
		}

		public void setPersonFullName(String name) {
			// TODO Auto-generated method stub
			
		}

		public void setPersonList(List persons) {
			// TODO Auto-generated method stub
			
		}
	};

	/**
	 * Sets the mode to SINGLE_SELECTION for use in selecting a single person
	 * Fills in the searchResultList with any previous values
	 * 
	 * @param fullname
	 *            String "Lastname, FirstName"
	 */
	public void setMode(String fullname) {
		mode = PeoplePicker.SELECT_MODE_PERSON;
		searchResultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		reset();
		if (fullname != null) {
			IPerson person = delegate.getPersonByFullName(fullname);
			IPerson p[] = new IPerson[1];
			p[0] = person;
		}
	}
	
	public List getSelection() {
		if (people != null) {
			List list = Arrays.asList(people);
			return list;
		}
		return new ArrayList();
	}

	private void initGUI() {
		try {
			setLayout( new FormLayout(
					"max(p;10px), 60dlu, 8dlu, 80dlu:g, 40dlu, max(p;10px), max(p;10px)",
					"max(p;10px), 15dlu, max(p;10px), 15dlu, 15dlu, 15dlu:g, max(p;10px), 15dlu, max(p;10px), 15dlu, max(p;10px), 5dlu, 15dlu, 10dlu"));

			add(firstnameLabel, new CellConstraints("2, 2, 1, 1, default, default"));
			add(firstnameField, new CellConstraints("4, 2, 1, 1, default, default"));
			add(lastnameLabel, new CellConstraints("2, 4, 1, 1, default, default"));
			add(lastnameField, new CellConstraints("4, 4, 1, 1, default, default"));
			add(searchButton, new CellConstraints("6, 2, 1, 1, default, default"));
			searchButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onSearch();
				}
			});
			JScrollPane scroller = createList(searchResultList);
			add(scroller, new CellConstraints("4, 6, 3, 6, default, default"));
			searchResultList.setCellRenderer(new PeopleRenderer());
			add(searchresultsLabel, new CellConstraints("2, 6, 1, 1, default, default"));
			applyButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onApply();
				}
			});
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onCancel();
				}
			});
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(applyButton);
			buttonPanel.add(cancelButton);
			add(buttonPanel, new CellConstraints("4, 13, 3, 2, default, default"));
			// initial enables/disables
			this.searchButton.setEnabled(false);
			this.cancelButton.setEnabled(true);
			this.applyButton.setEnabled(false);
			// set text field listeners
			this.firstnameField.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					enableSearchButton();
//					if (e.getKeyCode() == KeyEvent.VK_ENTER && isSearchPossible()) {
//						onSearch();
//					}
				}
			});
			this.firstnameField.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					((JTextField)e.getComponent()).selectAll();
				}
			});
			this.lastnameField.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					enableSearchButton();
//					if (e.getKeyCode() == KeyEvent.VK_ENTER && isSearchPossible()) {
//						onSearch();
//					}
				}
			});
			this.lastnameField.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					((JTextField)e.getComponent()).selectAll();
				}
			});

//			this.addKeyListener(new KeyAdapter() {
//				public void keyPressed(KeyEvent e) {
//					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
//						onCancel();
//					}
//				}
//			});

		} catch (Exception e) {
			log.error("Layout Problem", e);
		}
		//need to center on frame owner.
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Component parent = MasterController.getGuiComponent();
		if (parent != null && parent.isShowing()) 
		{
			log.debug("Setting dialog location based on MasterController GUI component.");
			setLocationRelativeTo(parent);
		} else {
			log.debug("Setting location based on centering on the screen.");
			Dimension dialogSize = getPreferredSize();
			setLocation(screenSize.width / 2 - (dialogSize.width / 2), screenSize.height / 2 - (dialogSize.height / 2));
		}
		
		Vector order = new Vector(6);
	    order.add(firstnameField);
	    order.add(lastnameField);
	    order.add(searchButton);
	    order.add(searchResultList);
	    order.add(applyButton);
	    order.add(cancelButton);
	    MyOwnFocusTraversalPolicy newPolicy = new MyOwnFocusTraversalPolicy(order);

	    setFocusTraversalPolicy(newPolicy);
	}

	private void enableSearchButton() {
		this.searchButton.setEnabled(isSearchPossible());
	}
	
	private boolean isSearchPossible() {
		String firstName = this.firstnameField.getText();
		String lastName = this.lastnameField.getText();
		return (firstName != null && lastName != null && (firstName.length() > 0 || lastName.length() > 0));
	}

	protected void defaultApplyAction() {
		if (isSearchPossible()) {
			onSearch();
		}
	}

	protected void defaultCancelAction() {
		onCancel();	
	}
	
	private void onApply() {
		if (searchResultList.getSelectedIndex() == -1) 
			JOptionPane.showMessageDialog(this, "Please select a person first!");
		String result = (String) this.searchResultIdList.get(searchResultList.getSelectedIndex());
		personFinder.findPersonNTID(result);
		if (oneTime) closeWindow();
		if (people != null && people.length > searchResultList.getSelectedIndex()) {
			IPerson selectedPerson = people[searchResultList.getSelectedIndex()];
			StringBuffer buff = new StringBuffer();
			buff.append(selectedPerson.getLastName()).append(", ");
			buff.append(selectedPerson.getFirstName()).append(" ");
			if (selectedPerson.getMiddleName() != null)
				buff.append(selectedPerson.getMiddleName());
			personFinder.setPersonFullName(buff.toString());
		} else
			personFinder.setPersonFullName(result);
	}

	private void onCancel() {
		people = null;
		log.debug("cancel");
		closeWindow();
	}

	private void onSearch() {
		if (this.firstnameField.getText().length() == 0 && this.lastnameField.getText().length() == 0) {
			this.applyButton.setEnabled(false);

			return;
		}
		log.debug("Search");
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		people = SearchAction();
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		setListVals(people, searchResultList);
		if (people != null && people.length > 0)
			this.applyButton.setEnabled(true);
		else
			this.applyButton.setEnabled(false);
	}

	private void closeWindow() {
		setVisible(false);
		dispose();
	}

	private IPerson[] SearchAction() {
		IPerson users[] = null;
		String first = this.firstnameField.getText().trim();
		String last = this.lastnameField.getText().trim();
		try {
			users = delegate.getUsersByName(first, last, searchResultCnt);
		} catch (PersonServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return users;
	}

	public class PeopleRenderer extends DefaultListCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 478427329213619005L;

		public PeopleRenderer() { }

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof IPerson) {
				IPerson person = (IPerson) value;
				String name = person.getLastName() + "," + person.getFirstName();
				String site = person.getLocationDescr();
				String phone = person.getPhoneBusinessOffice();
				setText(name + " [" + site + " , " + phone + "]");
			}
			return this;
		}
	}

	protected String[] getListVals(JList list) {
		DefaultListModel model = (DefaultListModel) list.getModel();
		String[] names = new String[model.getSize()];
		model.copyInto(names);
		return names;
	}

	protected void setListVals(IPerson[] vals, JList list) {
		DefaultListModel model = (DefaultListModel) list.getModel();
		model.clear();
		if ((vals == null) || (vals.length == 0))
			return;
		Arrays.sort(vals, new LastNameComparator());
		// vb 11/1 BZ 11857 replace user id with the last name followed by first and middle name
		// vb 11/7 keep the nt ids in a separate list with the same indices
		this.searchResultIdList.clear();
		for (int i = 0; i < vals.length; i++) {
			IPerson person = vals[i];
			String personStr = person.getLastName() + ", "+ person.getFirstName();
			if (person.getMiddleName() != null && person.getMiddleName().length() > 0)
				personStr = personStr + " " + person.getMiddleName();
			if (person.getLocationDescr() != null && person.getLocationDescr().length() > 0)
				personStr = personStr + " [" + person.getLocationDescr() + "]";
			if (person.getLogonId() != null && person.getLogonId().length() > 0)
				this.searchResultIdList.add(person.getLogonId());
			else
				this.searchResultIdList.add("");
			model.addElement(personStr); // vb bz 11857
		}
	}

	/**
	 * clears the GUI elements - lists and text fields
	 * 
	 */
	public void reset() {
		DefaultListModel model = (DefaultListModel) searchResultList.getModel();
		model.clear();
		model.clear();
		this.firstnameField.setText("");
		this.lastnameField.setText("");
	}

	public class LastNameComparator implements Comparator {
		public int compare(Object person, Object anotherPerson) {
			String lastName1 = ((IPerson) person).getLastName().toUpperCase();
			String firstName1 = ((IPerson) person).getFirstName().toUpperCase();
			String lastName2 = ((IPerson) anotherPerson).getLastName()
					.toUpperCase();
			String firstName2 = ((IPerson) anotherPerson).getFirstName()
					.toUpperCase();
			if (!(lastName1.equals(lastName2)))
				return lastName1.compareTo(lastName2);
			else
				return firstName1.compareTo(firstName2);
		}
	}

	public class FirstNameComparator implements Comparator {
		public int compare(Object person, Object anotherPerson) {
			String lastName1 = ((IPerson) person).getLastName().toUpperCase();
			String firstName1 = ((IPerson) person).getFirstName().toUpperCase();
			String lastName2 = ((IPerson) anotherPerson).getLastName()
					.toUpperCase();
			String firstName2 = ((IPerson) anotherPerson).getFirstName()
					.toUpperCase();
			if (!(firstName1.equals(firstName2)))
				return firstName1.compareTo(firstName2);
			else
				return lastName1.compareTo(lastName2);
		}
	}

	public JScrollPane createList(JList list) {
		DefaultListModel model = new DefaultListModel();
		list.setModel(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // vb bz
																	// 11857
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(40, 40));
		return listScroller;
	}

	public static class MyOwnFocusTraversalPolicy
		extends FocusTraversalPolicy
	{
		Vector<Component> order;

		public MyOwnFocusTraversalPolicy(Vector<Component> order) {
			this.order = new Vector<Component>(order.size());
			this.order.addAll(order);
		}
		public Component getComponentAfter(Container focusCycleRoot,
				Component aComponent)
		{
			int idx = (order.indexOf(aComponent) + 1) % order.size();
			return order.get(idx);
		}

		public Component getComponentBefore(Container focusCycleRoot,
				Component aComponent)
		{
			int idx = order.indexOf(aComponent) - 1;
			if (idx < 0) {
				idx = order.size() - 1;
			}
			return order.get(idx);
		}

		public Component getDefaultComponent(Container focusCycleRoot) {
			return order.get(0);
		}

		public Component getLastComponent(Container focusCycleRoot) {
			return order.lastElement();
		}

		public Component getFirstComponent(Container focusCycleRoot) {
			return order.get(0);
		}
	}
}