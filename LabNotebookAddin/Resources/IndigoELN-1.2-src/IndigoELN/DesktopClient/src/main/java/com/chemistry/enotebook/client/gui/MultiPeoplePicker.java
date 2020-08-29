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
import com.chemistry.enotebook.person.classes.IPerson;
import com.chemistry.enotebook.utils.CeNDialog;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.*;
import java.util.List;

public class MultiPeoplePicker extends CeNDialog {

	private static final long serialVersionUID = -7080060705914850630L;

	private static final Log log = LogFactory.getLog(PeoplePicker.class);
	
	public static int SELECT_MODE_PEOPLE = 0;
	public static int SELECT_MODE_PERSON = 1;

	public final static String LABEL_FIRSTNAME = "First Name:";
	public final static String LABEL_LASTNAME = "Last Name:";
	public final static String LABEL_SELECTED_PEOPLE = "Selected People:";
	public final static String LABEL_SEARCH_RESULTS = "Search Results:";
	public final static String TITLE_PERSON_PICKER = "Find People";
	public final static String BUTTON_LABEL_SEARCH = "Search";
	public final static String BUTTON_LABEL_REMOVE = "<< Remove";
	public final static String BUTTON_LABEL_ARROW_ADD = "Add         >>";
	public final static String BUTTON_LABEL_CANCEL = "Cancel";
	public final static String BUTTON_LABEL_OK = "Ok";
	
	private JTextField firstnameField = new JTextField();
	private JTextField lastnameField = new JTextField();
	private JLabel firstnameLabel = new JLabel(LABEL_FIRSTNAME);
	private JLabel lastnameLabel = new JLabel(LABEL_LASTNAME);
	private JLabel SelectedPeopleLabel = new JLabel("Selected People:");
	private JButton SearchButton = new JButton(BUTTON_LABEL_SEARCH);
	private JButton cancelButton = new JButton(BUTTON_LABEL_CANCEL);
	private JButton applyButton = new JButton(BUTTON_LABEL_OK);
	private JLabel searchresultsLabel = new JLabel(LABEL_SEARCH_RESULTS);
	private JList selectedPersonList = new JList();
	private JButton addButton = new JButton(BUTTON_LABEL_ARROW_ADD);
	private JList searchResultList = new JList();
	private JButton clearButton = new JButton(BUTTON_LABEL_REMOVE);
	private BigInteger SearchResultCnt = new BigInteger("100");

//	private int mode = SELECT_MODE_PERSON;
	private IPerson people[] = null;
	private PersonDelegate delegate;
	private PersonFinder personFinder;
	private List<String> existingPersons;

	
	public MultiPeoplePicker(PersonFinder personFinder, List<String> existingPersons) {
		super(MasterController.getGUIComponent(), TITLE_PERSON_PICKER, true);
		this.personFinder = personFinder;
		if (existingPersons == null) {
			this.existingPersons = new ArrayList<String>();
		} else { 
			this.existingPersons = existingPersons;
		}
		initGUI();
		setResizable(false);
		//need to center on frame owner.
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Component parent = MasterController.getGuiComponent();
		if (parent != null && parent.isShowing()) {
			log.debug("Setting dialog location based on MasterController GUI component.");
			setLocationRelativeTo(parent);
		} else {
			log.debug("Setting location based on centering on the screen.");
			Dimension dialogSize = getPreferredSize();
			setLocation(screenSize.width / 2 - (dialogSize.width / 2), screenSize.height / 2 - (dialogSize.height / 2));
		}
		// initialize service delegate
		try {
			delegate = new PersonDelegate();
		} catch (Exception e) {
			log.error("Failure connecting to personService", e);
			// ExceptionHandler.showErrorMessage(e.getMessage(), "Person
			// Chooser");
		}
	}

	/**
	 * Sets the mode to SINGLE_SELECTION for use in selecting a single person Fills in the searchResultList with any previous values
	 * 
	 * @param person
	 *            String "Lastname, FirstName"
	 */
	public void setMode(String fullname) {
//		mode = PeoplePicker.SELECT_MODE_PERSON;
		searchResultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		reset();
		if (fullname != null) {
			IPerson person = delegate.getPersonByFullName(fullname);
			IPerson p[] = new IPerson[1];
			p[0] = person;
			setListVals(p, selectedPersonList);
		}
	}

	public List<IPerson> getSelection() {
		if (people != null) {
			List<IPerson> list = Arrays.asList(people);
			return list;
		}
		return new ArrayList<IPerson>();
	}

	private void initGUI() {
		try {
			Container panel = this.getContentPane();
			FormLayout thisLayout = new FormLayout(
					"0dlu, 0dlu, max(p;10px), 60dlu, 8dlu, 80dlu, 20dlu, max(p;10px), 25dlu, 30dlu, 9dlu, 30dlu, 16dlu, max(p;10px), 81dlu, 12dlu, 15dlu, 42dlu, 0dlu, max(p;10px)",
					"max(p;10px), 13dlu, max(p;10px), 15dlu, 15dlu, 15dlu, max(p;10px), 25dlu, max(p;10px), 18dlu, max(p;10px), 34dlu, 13dlu, 19dlu, max(p;10px)");
			panel.setLayout(thisLayout);
			panel.add(firstnameLabel, new CellConstraints("4, 2, 1, 1, default, default"));
			panel.add(firstnameField, new CellConstraints("6, 2, 1, 1, default, default"));
			panel.add(lastnameLabel, new CellConstraints("4, 4, 1, 1, default, default"));
			panel.add(lastnameField, new CellConstraints("6, 4, 1, 1, default, default"));
			panel.add(SearchButton, new CellConstraints("8, 2, 1, 1, default, default"));
			SearchButton.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onSearch();
				}
			});
			panel.add(clearButton, new CellConstraints("10, 10, 3, 1, default, default"));
			clearButton.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onRemove();
				}
			});
			JScrollPane scroller = createList(searchResultList, null);
			panel.add(scroller, new CellConstraints("6, 8, 3, 4, default, default"));
			addButton.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onAdd();
				}
			});
			panel.add(addButton, new CellConstraints("10, 8, 3, 1, default, default"));
			JScrollPane scroller2 = createList(selectedPersonList, existingPersons);
			panel.add(scroller2, new CellConstraints("15, 8, 4, 4, default, default"));
			searchResultList.setCellRenderer(new PeopleRenderer());
			selectedPersonList.setCellRenderer(new PeopleRenderer());
			panel.add(searchresultsLabel, new CellConstraints("4, 6, 1, 1, default, default"));
			panel.add(SelectedPeopleLabel, new CellConstraints("15, 6, 1, 1, default, default"));
			applyButton.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onApply();
				}
			});
			panel.add(applyButton, new CellConstraints("9, 13, 2, 1, default, default"));
			cancelButton = new JButton(BUTTON_LABEL_CANCEL);
			cancelButton.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onCancel();
				}
			});
			panel.add(cancelButton, new CellConstraints("12, 13, 2, 1, default, default"));
		} catch (Exception e) {
			log.error("Layout Problem", e);
		}
	}

	private void onApply() {
		log.debug("Applying - ok");
		List<String> result = getListVals(selectedPersonList);;
		personFinder.setPersonList(result);
		closeWindow();
	}

	private void onAdd() {
		log.debug("add");
		DefaultListModel model = (DefaultListModel) selectedPersonList.getModel();
		Object sel = searchResultList.getSelectedValue();
		if (sel != null && sel instanceof IPerson && !model.contains(this.getNameFromPersonObject((IPerson)sel))) {
			model.addElement(this.getNameFromPersonObject((IPerson)sel));
		}
	}

	private void onCancel() {
		people = null;
		log.debug("cancel");
		closeWindow();
	}

	private void onSearch() {
		log.debug("Search");
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		people = SearchAction();
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		setListVals(people, searchResultList);
	}

	private void onRemove() {
		DefaultListModel model = (DefaultListModel) selectedPersonList.getModel();
		for (Object selected : selectedPersonList.getSelectedValues()) {
			model.removeElement(selected);
		}
		log.debug("Clear");
	}

	protected void defaultApplyAction() {
		onSearch();
	}

	protected void defaultCancelAction() {
		onCancel();
	}	
	
	private String getNameFromPersonObject(IPerson p) {
		StringBuffer buff = new StringBuffer();
		buff.append(p.getLastName()).append(", ");
		buff.append(p.getFirstName()).append(" ");
		if (p.getMiddleName() != null) {
			buff.append(p.getMiddleName());
		}
		return buff.toString();
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
			users = delegate.getUsersByName(first, last, SearchResultCnt);
		} catch (Exception e) {
			log.error("Search for People Failed", e);
		}
		return users;
	}

	public class PeopleRenderer extends DefaultListCellRenderer {
		
		private static final long serialVersionUID = -3738628923401577174L;

		public PeopleRenderer() {
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof IPerson) {
				IPerson person = (IPerson) value;
				String name = person.getLastName() + "," + person.getFirstName();
				String site = person.getLocationDescr();
				//String phone = person.getPhoneBusinessOffice();
				setText(name + " [" + site + "]");
			}
			return this;
		}
	}

	protected List<String> getListVals(JList list) {
		DefaultListModel model = (DefaultListModel) list.getModel();
		List<String> personNames = new ArrayList<String>();
		for (int i = 0; i < model.size(); ++i) {
			personNames.add((String) model.elementAt(i));
		}
		return personNames;	
	}

	protected void setListVals(IPerson[] vals, JList list) {
		DefaultListModel model = (DefaultListModel) list.getModel();
		model.clear();
		if ((vals == null) || (vals.length == 0)) {
			return;
		}
		Arrays.sort(vals, new LastNameComparator());
		for (IPerson person : vals) {
			model.addElement(person);
		}
	}

	/**
	 * clears the GUI elements - lists and text fields
	 * 
	 */
	public void reset() {
		DefaultListModel model = (DefaultListModel) searchResultList.getModel();
		model.clear();
		model = (DefaultListModel) selectedPersonList.getModel();
		model.clear();
		this.firstnameField.setText("");
		this.lastnameField.setText("");
	}

	public class LastNameComparator implements Comparator<IPerson> {
		public int compare(IPerson person, IPerson anotherPerson) {
			String lastName1 = person.getLastName().toUpperCase();
			String firstName1 = person.getFirstName().toUpperCase();
			String lastName2 = anotherPerson.getLastName().toUpperCase();
			String firstName2 = anotherPerson.getFirstName().toUpperCase();
			if (!(lastName1.equals(lastName2))) {
				return lastName1.compareTo(lastName2);
			} else {
				return firstName1.compareTo(firstName2);
			}
		}
	}

	public class FirstNameComparator implements Comparator<IPerson> {
		public int compare(IPerson person, IPerson anotherPerson) {
			String lastName1 = person.getLastName().toUpperCase();
			String firstName1 = person.getFirstName().toUpperCase();
			String lastName2 = anotherPerson.getLastName().toUpperCase();
			String firstName2 = anotherPerson.getFirstName().toUpperCase();
			if (!(firstName1.equals(firstName2))) {
				return firstName1.compareTo(firstName2);
			} else {
				return lastName1.compareTo(lastName2);
			}
		}
	}

	private JScrollPane createList(JList list, List<?> prepopulateList) {
		DefaultListModel model = new DefaultListModel();
		if (prepopulateList != null && prepopulateList.size() > 0) {
			for (Iterator<?> it = prepopulateList.iterator(); it.hasNext();) {
				model.addElement(it.next());
			}
		}
		list.setModel(model);
		// list.setSelectionMode(ListSelectionModel.);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(40, 40));
		return listScroller;
	}
}
