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
package com.chemistry.enotebook.client.gui.tablepreferences;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.experiment.utils.SystemPropertyException;
import com.chemistry.enotebook.experiment.utils.xml.JDomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import javax.swing.*;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import java.io.IOException;
import java.util.*;

/**
 * 
 * 
 */
public class TablePreferenceDelegate {
	public static final Log log = LogFactory.getLog(TablePreferenceDelegate.class);
	private NotebookPageModel pageModel = null;
	private NotebookUser user = null;
	private boolean pageEditable = false;
	private List columnPreferences = null;
	private StringBuffer tablePreferencePath = null;
	private String tableName = "";
	//private String systemPreferences = null;
	private String tablePreferences = null;
	private String tableElementNameXML = null;
	private String closingTableElementNameXML = null;
	public static final int TABLE_PROPS_REACTANTS = 0;
	public static final int TABLE_PROPS_INTENDED_PRODUCTS = 1;
	private static final String userPreferencePath = "/User_Properties/Preferences/Table";
	private static final String defaultPreferencePath = "/Notebook_Properties/User_Properties/Preferences/Table";
	private String[] defaultColumnNames;
	//private boolean saveFlag = false;
	private boolean usingDefaults = false;

	/**
	 * 
	 * @param tableName - This connector/controllers unique name
	 * @param page
	 * @param defaultColumnNames - The column names from the model
	 * @throws UserPreferenceException
	 */
	public TablePreferenceDelegate(String tableName, NotebookPageModel page, String[] defaultColumnNames) throws UserPreferenceException {
		try {
			this.defaultColumnNames = defaultColumnNames;
			this.pageModel = page;
			this.pageEditable = ((pageModel == null) || (pageModel.isEditable()));
			this.tableName = tableName;
			this.tableElementNameXML= "<" + tableName + ">";
			this.closingTableElementNameXML = "</" + tableName + ">";
			this.user = MasterController.getUser();
			this.tablePreferencePath = new StringBuffer(userPreferencePath);
			this.tablePreferencePath.append("/").append(tableName);
			this.tablePreferences = this.getTablePreferences();
			this.columnPreferences = this.parsePreferences(tablePreferences, tablePreferencePath.toString());
			
			if (!usingDefaults && !checkTablePreferenceValidity()) {
				this.tablePreferences = createTablePrefXmlFromDefaultColumnNames();
				usingDefaults = true;
				// Create jdom preferences for this table so it is ready if user
				// makes any changes and save button is enabled.
				this.setDefaultTablePrefsInNotebookUser();  // at this point the prefs are changed in user object

				this.columnPreferences = this.parsePreferences(tablePreferences, tablePreferencePath.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Exception thrown in constructor: " + e.getMessage());
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "TablePreferenceDelegate", e);
		} catch (JDOMException e) {
			e.printStackTrace();
			log.error("Exception thrown in constructor: " + e.getMessage());
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "TablePreferenceDelegate", e);
		} catch (RuntimeException e) {
			e.printStackTrace();
			log.error("Exception thrown in constructor: " + e.getMessage());
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "TablePreferenceDelegate", e);
		}
	}

	private boolean checkTablePreferenceValidity() {
		Set<Integer> modelIndices = new HashSet<Integer>();
		Set<Integer> columnIndices = new HashSet<Integer>();
		int modelIndexUpperLimit = defaultColumnNames.length - 1;
		
		for (int i = 0; i < columnPreferences.size(); i++) {
			TableColumnInfo tci = (TableColumnInfo)columnPreferences.get(i);
			// check model index
			if (modelIndices.contains(tci.getModelIndex()) || tci.getModelIndex() > modelIndexUpperLimit) {
				return false;
			} else {
				modelIndices.add(tci.getModelIndex());
			}
			
			// check column index
			if (columnIndices.contains(tci.getColumnIndex()) || tci.getColumnIndex() > modelIndexUpperLimit) {
				return false;
			} else {
				columnIndices.add(tci.getColumnIndex());
			}
		}
		
		return true;
	}

	/**
	 * Get the preferences (1) from the user preferences, or, if they are not there, from (2) the system preferences or,
	 * if they are not there, (3) create them from the default column names passed to the contstructor.
	 * @throws UserPreferenceException 
	 */
	private String getTablePreferences() throws UserPreferenceException {
		// Check if the user preferences for this table exists
		String preferences = "";
		preferences = user.getPreference(this.tablePreferencePath.toString());
		// If it does not exist, see if there is a system preference for this table
		// and set flags indicating that the preferences should be saved in the notebook
		// user object.
		if (preferences == null || preferences.length() == 0) {
			preferences = this.getCeNSystemDefaultTableProperties(defaultPreferencePath + "/" + tableName);
			// If there are no system preferences, create default preferences from column labels
			if (preferences == null || preferences.length() == 0) {
				preferences = createTablePrefXmlFromDefaultColumnNames();
				usingDefaults = true;
			}
			// Create jdom preferences for this table so it is ready if user
			// makes any changes and save button is enabled.
			this.setDefaultTablePrefsInNotebookUser();  // at this point the prefs are changed in user object
		}
		// Lastly check of preferences column count differs from the user or system
		// prefs column count.  If so, replace with defaults.
		// vb 10/15 replace with a method that checks if the column names 
		// have changed too.
		if (! usingDefaults && columnsChanged() ) { //! this.isColumnCountEqual(preferences)) {
			// This updates the prefs in the user object
			//this.createUserTablePreferencesFromDefault();  // at this point the prefs are changed in user object
			// This updates the prefs in the table column model
			preferences = createTablePrefXmlFromDefaultColumnNames();
		}
		// Remove the newlines
		preferences = preferences.replaceAll("\r\n", "");
		return preferences;
	}
	
	private boolean isColumnCountEqual(String xmlPreferences) {
		String[] columns = xmlPreferences.split("<Column_Index>");
		//System.out.println(this.tableName);
		//System.out.println(columns.length - 1 + " / " + defaultColumnNames.length);
		if ((columns.length - 1) != (defaultColumnNames.length))
			return false;
		else
			return true;
	}
	
	/**
	 * Not sure that it is really necessary to convert the column info list to xml.
	 * @return
	 */
	private String createTablePrefXmlFromDefaultColumnNames() {
		StringBuffer buff = new StringBuffer("<" + tableName + ">");
		List defaultColumnInfoList = this.setDefaultColumnInfo();
		for (Iterator it = defaultColumnInfoList.iterator(); it.hasNext();) {
			buff.append("<Column>");
			TableColumnInfo colinfo = (TableColumnInfo) it.next();
			//if (colinfo.getLabel().indexOf("html") >= 0 || colinfo.getLabel().indexOf("HTML") >= 0)
			//	colinfo.setLabel("![CDATA[" + colinfo.getLabel() + "]]");
			buff.append(colinfo.toXml());
			buff.append("</Column>");
		}
		buff.append("</" + tableName + ">");
		return buff.toString();
	}

	/**
	 * 
	 * @return tableProp
	 */
	private String getCeNSystemDefaultTableProperties(String xPath) {
		String temp = null;
		String result = "";
		try {
			temp = CeNSystemProperties.getCeNSystemProperty(xPath);
			if (temp != null) {
				if (!temp.equals(""))
					result = temp.replaceAll(" ", "").replaceAll("\r\n", "");
				//else
					//CeNErrorHandler.getInstance().logErrorMsg(null, "Table Preferences are Disabled",
					//		"Table Preferences are Missing", JOptionPane.ERROR_MESSAGE);
			} else
				CeNErrorHandler.getInstance().logErrorMsg(null, "Table Preferences are Disabled", "Table Preferences are Missing",
						JOptionPane.ERROR_MESSAGE);
		} catch (SystemPropertyException sysEx) {
			CeNErrorHandler.getInstance().logErrorMsg(null, sysEx.getMessage(), "Table Preference Missing",
					JOptionPane.ERROR_MESSAGE);
		}
		return result;
	}
	
	private List setDefaultColumnInfo() {
		List columnPrefs = new ArrayList();
		for (int i=0; i<defaultColumnNames.length; i++) {
			String name = defaultColumnNames[i];
			TableColumnInfo colInfo = new TableColumnInfo();
			colInfo.setColumnIndex(i);
			colInfo.setModelIndex(i);
			colInfo.setPreferredWidth(100); 
			if (name.equals(PCeNTableView.SALT_CODE)
			 || name.equals(PCeNTableView.SALT_EQ)
			 || name.equals(PCeNTableView.TIMES_USED)
			 || name.equals(PCeNTableView.PLATE)
			 || name.equals(PCeNTableView.WELL_POSITION)
			 || name.equals(PCeNTableView.LIMITING)
			 || name.equals(PCeNTableView.PARENT_MW)
			 || name.equals(PCeNTableView.DELIVERED_VOLUME)
			 || name.equals(PCeNTableView.RXN_VOLUME)
			 || name.equals(PCeNTableView.RXN_EQ)
			 || name.equals(PCeNTableView.DEAD_VOLUME)
			 || name.equals(PCeNTableView.DENSITY)
			 || name.equals(PCeNTableView.MOLARITY)
			 || name.equals(PCeNTableView.PURITY)
			 || name.equals(PCeNTableView.EXPTAB_HAZARD_COMMENTS)
			 || name.equals(PCeNTableView.COMMENTS)
			 )
				colInfo.setVisible(false);
			else
				colInfo.setVisible(true);
			colInfo.setLabel(defaultColumnNames[i]);
			columnPrefs.add(colInfo);
		}
		return columnPrefs;
	}


	public TableColumnInfo getColumnInfoFromModelIndex(int index) {
		TableColumnInfo result = null;
		if (columnPreferences == null)
			return null;
		for (int colIndex = 0; colIndex < columnPreferences.size() && result == null; colIndex++) {
			TableColumnInfo ci = (TableColumnInfo) columnPreferences.get(colIndex);
			if (ci.getModelIndex() == index) {
				result = ci;				
				break;
			}
		}
		return result;
	}

	/**
	 * This function parses a xml preferences String and extracts TableColumnInfo objecs based on the XPath All objects are stored
	 * in an ArrayList and are used to create the table view according to the stored preferences
	 * 
	 * @param prefs
	 * @param xPath
	 * @return ArrayList of TableColumnInfo objects
	 * @throws JDOMException 
	 * @throws IOException 
	 */
	private List parsePreferences(String prefs, String xPath) throws IOException, JDOMException {
		ArrayList result = new ArrayList();
		// there is no need to parse if there are not user preferences
		if (prefs != null && !prefs.equals("")) {
			// extract a table element from XML string
			//StringBuffer tableName = new StringBuffer(xPath);
			StringBuffer preferences = new StringBuffer(prefs);
			String finalPreferences = null;
			int startIndex = prefs.indexOf(tableElementNameXML);
			int endIndex = prefs.indexOf(closingTableElementNameXML);
			if (startIndex != -1) {
				finalPreferences = preferences.substring(startIndex, endIndex + closingTableElementNameXML.length());
			}
			//System.out.println("finalPreferences for " + tableElementNameXML + "\n" + finalPreferences);
			result = new ArrayList(); 
			try {
				Document doc = JDomUtils.getDocFromString(finalPreferences);
				//System.out.println();
				String path = "/" + this.tableName + "/Column";
				List childNodes = JDomUtils.getChildNodes(doc, path);
				if (childNodes != null) {
					for (int i = 0; i < childNodes.size(); i++) {
						Element item = (Element) childNodes.get(i);
						List nodes = item.getChildren();
						TableColumnInfo info = getColumnInfo(nodes);
						//if (info != null)
						result.add(info);
					}
				}
			} catch (IOException e) {
				log.error(e);
				throw e;
			} catch (JDOMException e) {
				log.error(e);
				throw e;				
			}
		}// end if
		return result;
	}

	/**
	 * Create a TableColumnInfo instance from a list of column element nodes.
	 * @param list
	 * @return
	 */
	private TableColumnInfo getColumnInfo(List list) {
		TableColumnInfo column = new TableColumnInfo();
		if (list.size() > 0) {
			for (int index = 0; index < list.size(); index++) {
				Element child = (Element) list.get(index);
				if (child != null) {
					Object columnInfo = null;
					switch (index) {
						case 0:
							columnInfo = child.getValue();
							column.setColumnIndex(new Integer((String) columnInfo).intValue());
							break;
						case 1:
							columnInfo = child.getValue();
							column.setModelIndex(new Integer((String) columnInfo).intValue());
							break;
						case 2:
							columnInfo = child.getValue();
							column.setPreferredWidth(new Integer((String) columnInfo).intValue());
							break;
						case 3:
							columnInfo = child.getValue();
							// vb column visibility problem.  Set to true as a workaround.
							column.setVisible(new Boolean((String) columnInfo).booleanValue());
							break;
						case 4:
							columnInfo = child.getValue();
							column.setLabel(columnInfo.toString());
							break;
					} // end switch
				}// end if
			} // end end for
			return column;
		} else
			return null;
	}// end method

	private Element createColumnElement(TableColumnInfo colinfo) {
		Element columnNode = new Element("Column");
		Element modelIndexNode = new Element("Column_Index");
		modelIndexNode.addContent("" + colinfo.getModelIndex());
		columnNode.addContent(modelIndexNode);
		Element columnIndexNode = new Element("Model_Index");
		columnIndexNode.addContent("" + colinfo.getColumnIndex());
		columnNode.addContent(columnIndexNode);
		Element preferredWidthNode = new Element("Preferred_Width");
		preferredWidthNode.addContent("" + colinfo.getPreferredWidthAsString());
		columnNode.addContent(preferredWidthNode);	
		Element visibleNode = new Element("Visible");
		visibleNode.addContent("" + colinfo.getVisibleAsString());
		columnNode.addContent(visibleNode);	
		Element labelNode = new Element("Label");
		if (colinfo.getLabel().indexOf("html") >= 0 || colinfo.getLabel().indexOf("HTML") >= 0)
			labelNode.addContent("![CDATA[" + colinfo.getLabel() + "]]");
		else 
			labelNode.addContent("" + colinfo.getLabel());
		columnNode.addContent(labelNode);	
		return columnNode;
	}
	
	
	/**
	 * This function is called when the user changes table preferences for the first time. First all the columns are created from
	 * defaults. The corresponding altered table column is updated as of the time of creation of table preferences
	 * 
	 * @param alteredColumn
	 *            table column info to be stored in a user cash
	 */
	public void createUserTablePreferences(TableColumnInfo alteredColumn) {
		if (alteredColumn != null) {
			try {
				Element preferenceNode = user.getUserPropertiesAsElement().getChild("Preferences");
				String trimmedTableInfoXML = tablePreferences.replaceAll("\r\n", "");
				Document doc = JDomUtils.getDocFromString(trimmedTableInfoXML);
				Element defaultTablePropertiesRootElement = doc.getRootElement();
				Element table = defaultTablePropertiesRootElement;
				List columns = table.getChildren();
				for (Iterator itr = columns.iterator(); itr.hasNext();) {
					Element column = (Element) itr.next();
					Element modelIndex = column.getChild("Model_Index");
					if (modelIndex.getText().equals(alteredColumn.getModelIndexAsString())) {
						column.getChild("Column_Index").setText(alteredColumn.getColumnIndexAsString());
						column.getChild("Preferred_Width").setText(alteredColumn.getPreferredWidthAsString());
						column.getChild("Visible").setText(alteredColumn.getVisibleAsString());
					}
				}
				// adding new branch to user cache need to synchronize
				synchronized (user) {
					if (preferenceNode.indexOf(defaultTablePropertiesRootElement) != 0) {
						preferenceNode.addContent(defaultTablePropertiesRootElement.detach());
					} else {
						if (preferenceNode.removeContent(defaultTablePropertiesRootElement))
							preferenceNode.addContent(defaultTablePropertiesRootElement.detach());
					}
					user.setPrefChanges(true); // update
				} // end syn
				/** TODO should modifing page property be syncronized... */
				String tablePref = buildNotebookPagePropertiesFromUserDoc();
				if (tablePref != null) {
					pageModel.setTableProperties(tablePref);
				}// end if
			} catch (IOException io) {
				CeNErrorHandler.getInstance().logExceptionWithoutDisplay(io,
						"IO Failure in method createUserTableFromXML : \n" + io.getMessage());
			} catch (JDOMException jdom) {
				CeNErrorHandler.getInstance().logExceptionWithoutDisplay(jdom,
						"JDOM Failed to Retrieve Element in method createUserTableFromXML " + jdom.getMessage());
			}
		} else
			CeNErrorHandler.getInstance().logErrorMsgWithoutDisplay(
					"Uninitialized argument 'alteredColumn' in method createUserTableFromXML ", "TablePreferenceDelegate Failure");
	}

//	public String getUserPreferences(String rootElementXpath) {
//		String xmlPreference = null;
//		try {
//			// NotebookHandler handler = new NotebookHandler();
//			XMLDataHandler xmlHelper = new XMLDataHandler(tablePreferences);
//			xmlHelper.createNewElement(rootElementXpath, "");
//			// handler.createXMLFromModel(obj, xmlHelper, rootElement+"/",
//			// null);
//			xmlPreference = xmlHelper.getXMLString(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return xmlPreference;
//	}

	public void setUserPreferences(String xmlPreferences) throws IOException, JDOMException {
		if (xmlPreferences != null)
			if (!xmlPreferences.equals(""))
				columnPreferences = parsePreferences(xmlPreferences, tablePreferencePath.toString());
//			else
//				throw new UserPreferenceException(
//						"Invalid or Empty argument 'xmlPreferences' passed to method setUserPreferences in TablePreferenceDelegate");
//		else
//			throw new UserPreferenceException(
//					"Invalid or Empty argument 'xmlPreferences' passed to method setUserPreferences in TablePreferenceDelegate");
	}

	public void updateUserPreferences(ArrayList tableColumns) {
		columnPreferences = tableColumns;
	}

	// ColumnModelListener implementation
	public void columnMoved(TableColumnModelEvent e) {
		try {
			if (pageModel.isEditable()) {
				Object obj = e.getSource();
				int from = e.getFromIndex();
				int to = e.getToIndex();
				if (from == to) return;
				boolean fromIndexFlagSet = false;
				int initialIndex = -1;
				if (from != to) {
					if (!fromIndexFlagSet) {
						initialIndex = from;
						fromIndexFlagSet = true;
					}
				}
				if (from != to && initialIndex != -1) {
					if (obj instanceof DefaultTableColumnModel) {
						ColumnFurniture tableColumn = (ColumnFurniture) ((DefaultTableColumnModel) obj).getColumn(to);
						ColumnFurniture oldColumn = (ColumnFurniture) ((DefaultTableColumnModel) obj).getColumn(from);
						// column indices exchange
						int fromColumnIndex = oldColumn.getColumnIndex();
						int toColumnIndex = tableColumn.getColumnIndex();
						tableColumn.setColumnIndex(fromColumnIndex);
						oldColumn.setColumnIndex(toColumnIndex);
						TableColumnInfo oldColumnInfo = getModifiedTableColumnInfo(tableColumn);
						TableColumnInfo columnInfo = getModifiedTableColumnInfo(oldColumn);
//						String userProperties = getNotebookUserTableProperties();
//						// If preferences for this table do not exist, create them.
//						if (userProperties.equals("")) {
//							this.createUserTablePreferencesFromDefault();
//						}
						// Update the column preferences
						updateColumnInfoPreferencesOnColumnMoved(columnInfo, oldColumnInfo);
					}
				} 
			}
		} catch (UserPreferenceException e1) {
			log.error("update column visibility got exception " + e1.getMessage());
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "Unable to update column visibility for table " + tableName, e1);
		} catch (RuntimeException e2) {
			log.error("update column visibility got exception " + e2.getMessage());
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "Unable to update column visibility for table " + tableName, e2);
		}

	}

//	public void columnMarginChanged(ChangeEvent e) {  // moved to PCeNTableView column model listener
////		if (pageModel.isEditable()) {
////			Object obj = e.getSource();
////			if (obj instanceof DefaultTableColumnModel) {
////				try {
////					resetTableColumnOnMarginChanged();
////				} catch (NullPointerException nullEx) {
////					CeNErrorHandler.getInstance().logExceptionWithoutDisplay(nullEx,
////							nullEx.getMessage() + "Method: columnMarginChanged");
////				}
////			} // end if
////		} // end method
//	} // end method

//	public void columnAdded(TableColumnModelEvent e) {
//	};
//
//	public void columnRemoved(TableColumnModelEvent e) {
//	};
//
//	public void columnSelectionChanged(ListSelectionEvent e) {
//	};

	public String getNotebookUserTableProperties() {
		StringBuffer buff = new StringBuffer(userPreferencePath);
		buff.append("/").append(tableName);
		try {
			return MasterController.getUser().getPreference(buff.toString());
		} catch (UserPreferenceException e) {
			return "";
		}
	}

	public String getNotebookUserPreferredUnit(String unitTypeName) throws UserPreferenceException {
		String xPath = null;
		if (unitTypeName.equals("MASS"))
			xPath = NotebookUser.PREF_Mass_Amount_Unit_Code;
		else if (unitTypeName.equals("MOLAR"))
			xPath = NotebookUser.PREF_Molar_Amount_Unit_Code;
		else if (unitTypeName.equals("VOLUME"))
			xPath = NotebookUser.PREF_Volume_Amount_Unit_Code;
		else if (unitTypeName.equals("MOLES"))
			xPath = NotebookUser.PREF_Moles_Amount_Unit_Code;
		else
			throw new UserPreferenceException(
					"Invalid xPath for UnitType Name in TablePreferenceDelegate - Method: getNotebookUserPreferredUnit-");
		return user.getTablePropertiesDescendant(xPath);
	}

	public void updateColumnInfoPreferences(TableColumnInfo alteredColumn) throws UserPreferenceException {
		try {
			Element tableElement = user.getUserPropertiesAsElement().getChild("Preferences").getChild("Table").getChild(this.tableName);
			if (tableElement == null) {
				// This should not happen now that the jdom preferences are set at initialization, so this is
				// an error.
				//this.createUserTablePreferencesFromDefault();
				log.error("Table element missing from NotebookUser jdom preferences");
				CeNErrorHandler.getInstance().logErrorMsg(MasterController.getGUIComponent(), "Unable to update column preferences", "Column preference update error");
				return;
			}

			List columns = tableElement.getChildren();
			for (Iterator itr = columns.iterator(); itr.hasNext();) {
				Element column = (Element) itr.next();
				Element modelIndex = column.getChild("Model_Index");
				if (modelIndex.getText().equals(alteredColumn.getModelIndexAsString())) {
					synchronized (user) {
						column.getChild("Column_Index").setText(alteredColumn.getColumnIndexAsString());
						column.getChild("Preferred_Width").setText(alteredColumn.getPreferredWidthAsString());
						column.getChild("Visible").setText(alteredColumn.getVisibleAsString());
						user.setPrefChanges(true);
					}
				} 
			}
			if (user.isPrefChanges()) {
				//pageModel.setModelChanged(true);
				MasterController.getGUIComponent().enableSaveButtons();
			}
		} catch (RuntimeException e) {
			log.error(e.getMessage());
			throw new UserPreferenceException(e.getMessage());
		} 
	}
	
	/**
	 * Check to see if the table columns have changes.
	 */
	private boolean columnsChanged() {
		try {
			List columnNames = Arrays.asList(this.defaultColumnNames);
			columnNames = new LinkedList(columnNames);
			Element table = user.getUserPropertiesAsElement().getChild("Preferences").getChild("Table");
			if (table == null)
				return false;
			Element tableElement = user.getUserPropertiesAsElement().getChild("Preferences").getChild("Table").getChild(this.tableName);
			if (tableElement == null) {
				return false;
			}
			List columns = tableElement.getChildren();

			// Check if all default columns are specified in DB. If not then return true. Else return false;
			
			// Check if any column name has changed
			// count labels which names contains "CDATA"
			int CDATAlabelsCounter = 0;
			for (Iterator itr = columns.iterator(); itr.hasNext();) {
				Element column = (Element) itr.next();
				String label = column.getChild("Label").getContent(0).getValue();
				
				// This is a workaround for the case when amounts of columns in default set and loaded from db set are different.
				// Previous code returned true if amounts were different. One more strange thing that I tried to leave in the new code:
				// If column label from db contains "CDATA" it doesn't matter what it contains in fact 
				
				if (columnNames.contains(label))
					columnNames.remove(label);
				else {
					if (label.indexOf("CDATA") >= 0)
						CDATAlabelsCounter++;
				}
			}
			if (columnNames.size() > CDATAlabelsCounter) {
				return true;
			}
			return false;
		} catch (RuntimeException e) {
			log.error(e.getMessage());
			return false;
		} 		
	}

	public TableColumnInfo getModifiedTableColumnInfo(ColumnFurniture tableColumn) {
		TableColumnInfo result = new TableColumnInfo();
		result.setModelIndex(tableColumn.getModelIndex());
		result.setColumnIndex(tableColumn.getColumnIndex());
		result.setPreferredWidth(tableColumn.getSavedPreferredWidth());
		result.setVisible(tableColumn.isVisible());
		result.setLabel(tableColumn.getLabel());
		return result;
	}

	public TableColumnInfo getModifiedTableColumnInfoAfterResizing(ColumnFurniture tableColumn) {
		TableColumnInfo result = new TableColumnInfo();
		result.setModelIndex(tableColumn.getModelIndex());
		result.setColumnIndex(tableColumn.getColumnIndex());
		result.setPreferredWidth(tableColumn.getPreferredWidth());
		result.setVisible(tableColumn.isVisible());
		result.setLabel(tableColumn.getLabel());
		return result;
	}

	private boolean isNotebookPrefExistForTable() {
		if (user == null) {
			// log
			return false;
		} 
		String pref;
		try {
			pref = user.getPreference(this.tablePreferencePath.toString());
		} catch (UserPreferenceException e) {
			// log
			return false;
		}
		if (pref != null && pref.length() > 0)
			return true;
		return false;
	}

//	public String extractSingleTableElementFromUserProperties() throws UserPreferenceException {
////		String result, xPath = null;
////		if (tableName == TABLE_PROPS_REACTANTS) {
////			xPath = NotebookUser.PREF_Reactants_Viewer;
////		} else if (tableName == TABLE_PROPS_INTENDED_PRODUCTS) {
////			xPath = NotebookUser.PREF_Intended_Products;
////		}
////		try {
////			if (xPath != null) {
////				String prefNoWhiteSpace = user.getTableAsString(xPath).replaceAll(" ", "").replaceAll("\r\n", "");
////				result = prefNoWhiteSpace;
////				return result;
////			} else
////				return null;
////		} catch (UserPreferenceException usExp) {
////			throw new UserPreferenceException("Failed to Extract TableElement from UserProperties in TablePreferenceDelegate");
////		}
//		return "";
//	}

	/**
	 * This function is called when the user changes table preferences for the first time. First all the columns are created from
	 * defaults. The corresponding altered table column is updated as of the time of creation of table preferences
	 * 
	 * @param oldColumn
	 *            table column info to be stored in a user cash
	 */
	private void setDefaultTablePrefsInNotebookUser() {
		synchronized (user) {
			try {
				Element preferenceDoc = user.getUserPropertiesAsElement().getChild("Preferences");
				Element tablePreferencesNode = JDomUtils.findElement(preferenceDoc, "Table");
				if (tablePreferencesNode == null) {
					tablePreferencesNode = new Element("Table");
					preferenceDoc.addContent(tablePreferencesNode);
				}
				Element tableNode = JDomUtils.findElement(preferenceDoc, this.tableName);
				// This table does not exist in the preferences, so create a the column nodes from the defaults.
				if (tableNode == null) {
					tableNode = new Element(this.tableName);
					List defaultColumnInfoList = this.setDefaultColumnInfo();
					for (Iterator it = defaultColumnInfoList.iterator(); it.hasNext();) {
						TableColumnInfo colinfo = (TableColumnInfo) it.next();
						Element columnElement = this.createColumnElement(colinfo);
						tableNode.addContent(columnElement);
					}
					tablePreferencesNode.addContent(tableNode);
				} else {
					tableNode.removeContent();
					List defaultColumnInfoList = this.setDefaultColumnInfo();
					for (Iterator it = defaultColumnInfoList.iterator(); it.hasNext();) {
						TableColumnInfo colinfo = (TableColumnInfo) it.next();
						Element columnElement = this.createColumnElement(colinfo);
						tableNode.addContent(columnElement);
					}
					//System.out.println();
					//tablePreferencesNode.addContent(tableNode);
				}
			} catch (RuntimeException e) {
				log.error("createUserTablePreferences got exception " + e.getMessage());
				CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "Unable to create user preferences for table " + tableName, e);
			}
		}
	}
	
	private void updateColumnInfoPreferencesOnColumnMoved(TableColumnInfo draggedColumn, TableColumnInfo oldColumn) 
		throws UserPreferenceException {
		try {
			Element tableElement = user.getUserPropertiesAsElement().getChild("Preferences").getChild("Table").getChild(this.tableName);
			if (tableElement == null) {
				// This should not happen now that the jdom preferences are set at initialization, so this is
				// an error.
				//this.createUserTablePreferencesFromDefault();
				log.error("Table element missing from NotebookUser jdom preferences");
				CeNErrorHandler.getInstance().logErrorMsg(MasterController.getGUIComponent(), "Unable to update column preferences", "Column preference update error");
				return;
			}

			List columns = tableElement.getChildren();
			for (Iterator itr = columns.iterator(); itr.hasNext();) {
				Element column = (Element) itr.next();
				Element modelIndex = column.getChild("Model_Index");
				if (modelIndex.getText().equals(
						draggedColumn.getModelIndexAsString())) {
					synchronized (user) {
						column.getChild("Column_Index").setText(
								draggedColumn.getColumnIndexAsString());
						column.getChild("Preferred_Width").setText(
								draggedColumn.getPreferredWidthAsString());
						column.getChild("Visible").setText(
								draggedColumn.getVisibleAsString());
						user.setPrefChanges(true);
					}
				}
				if (modelIndex.getText().equals(
						oldColumn.getModelIndexAsString())) {
					synchronized (user) {
						column.getChild("Column_Index").setText(
								oldColumn.getColumnIndexAsString());
						column.getChild("Preferred_Width").setText(
								oldColumn.getPreferredWidthAsString());
						column.getChild("Visible").setText(
								oldColumn.getVisibleAsString());
						user.setPrefChanges(true);
					}
				}
			} // end for
			if (user.isPrefChanges())
				MasterController.getGUIComponent().enableSaveButtons();
		} catch (RuntimeException e) {
			log.error(e.getMessage());
			throw new UserPreferenceException(e.getMessage());
		}
	} // end method

	public void resetTableColumnOnMarginChanged(ColumnFurniture column) {
//		Object obj = tableReference.getTableHeader().getResizingColumn();
//		if (obj instanceof ColumnFurniture) {
//			ColumnFurniture tableColumn = (ColumnFurniture) obj;
//			System.out.println(tableColumn.toString());
			String userProperties = getNotebookUserTableProperties();
			TableColumnInfo columnInfo = getModifiedTableColumnInfoAfterResizing(column);
//			if (userProperties.equals(""))
//				this.createUserTablePreferencesFromDefault();
				//System.out.println("Empty user properties...what do we do?????");
				//createUserTableFromXML(columnInfo);
			try {
				updateColumnInfoPreferences(columnInfo);
			} catch (UserPreferenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		} // end if
//		String tablePref = buildNotebookPagePropertiesFromUserDoc();
//		if (tablePref != null)
//			pageModel.setTableProperties(tablePref);
	} // end method

	/** TODO add a name for BatchInfoTable */
	public String buildNotebookPagePropertiesFromUserDoc() {
		StringBuffer result = new StringBuffer();
		try {
			String props = user.getPreference(this.tablePreferencePath.toString());
			props = props.replaceAll(" ", "").replaceAll("\r\n", "");
			result.append(props);
//			String reactantsTable = user.getTableAsString(NotebookUser.PREF_Reactants_Viewer);
//			String trimmedReactantsTable = null;
//			if (reactantsTable != null)
//				trimmedReactantsTable = reactantsTable.replaceAll(" ", "").replaceAll("\r\n", "");
//			String intendedProductsTable = user.getTableAsString(NotebookUser.PREF_Intended_Products);
//			String trimmedIntendedProductsTable = null;
//			if (intendedProductsTable != null)
//				trimmedIntendedProductsTable = intendedProductsTable.replaceAll(" ", "").replaceAll("\r\n", "");
//			String massAmountPreferredUnit = user.getPreferredUnitAsString(NotebookUser.PREF_Mass_Amount_Unit_Code);
//			String molarAmountPreferredUnit = user.getPreferredUnitAsString(NotebookUser.PREF_Molar_Amount_Unit_Code);
//			String molesAmountPreferredUnit = user.getPreferredUnitAsString(NotebookUser.PREF_Moles_Amount_Unit_Code);
//			String volumeAmountPreferredUnit = user.getPreferredUnitAsString(NotebookUser.PREF_Volume_Amount_Unit_Code);
//			// add Reactants Table to Page
//			if (reactantsTable != null)
//				if (!reactantsTable.equals("")) {
//					result = new StringBuffer(trimmedReactantsTable);
//				}
//			// add Intended Products Table to Page
//			if (intendedProductsTable != null)
//				if (!intendedProductsTable.equals("")) {
//					if (result == null) {
//						result = new StringBuffer(trimmedIntendedProductsTable);
//					} else {
//						result.append(trimmedIntendedProductsTable);
//					} // end if
//				} // end if
//			// add Amount of Type Mass preferred Unit
//			if (massAmountPreferredUnit != null)
//				if (!massAmountPreferredUnit.equals("")) {
//					if (result == null) {
//						result = new StringBuffer(massAmountPreferredUnit);
//					} else {
//						result.append(massAmountPreferredUnit);
//					} // end if
//				} // end if
//			// add Amount of Type Molar preferred Unit
//			if (molarAmountPreferredUnit != null)
//				if (!molarAmountPreferredUnit.equals("")) {
//					if (result == null) {
//						result = new StringBuffer(molarAmountPreferredUnit);
//					} else {
//						result.append(molarAmountPreferredUnit);
//					} // end if
//				} // end if
//			// add Amount of Type Molar preferred Unit
//			if (molesAmountPreferredUnit != null)
//				if (!molesAmountPreferredUnit.equals("")) {
//					if (result == null) {
//						result = new StringBuffer(molesAmountPreferredUnit);
//					} else {
//						result.append(molesAmountPreferredUnit);
//					} // end if
//				} // end if
//			// add Amount of Type Volume preferred Unit
//			if (volumeAmountPreferredUnit != null)
//				if (!volumeAmountPreferredUnit.equals("")) {
//					if (result == null) {
//						result = new StringBuffer(volumeAmountPreferredUnit);
//					} else {
//						result.append(volumeAmountPreferredUnit);
//					} // end if
//				} // end if
		} catch (UserPreferenceException usExp) {
			CeNErrorHandler.getInstance().logErrorMsgWithoutDisplay("Method: buildNotebookPageProperties" + usExp.getMessage(),
					"TablePreferenceDelegate Failure");
			;
		}
		
		return (result != null) ? result.toString() : null;
	} // end method

	public void createAmountPreferredUnit(String xPath, String value) throws UserPreferenceException {
		try {
			user.createAmountPreferredUnitElement(xPath, value);
		} catch (UserPreferenceException usExp) {
			throw new UserPreferenceException(usExp.getMessage());
		}
	}

	public List getColumnPreferences() {
		return columnPreferences;
	}

	/**
	 * Because the structure column can be changed in two places, we need to check to make sure we are not 
	 * setting an invisible structure column to invisible.  This will set the saved width to zero and it will
	 * never appear again.
	 * @param table
	 * @param modelIndex
	 * @param visible
	 */
	public void changeColumnVisibility(JTable table, int modelIndex, boolean visible, boolean updatePreferences) { // update
		int columnIndex = table.convertColumnIndexToView(modelIndex);
		ColumnFurniture cf = ((ColumnFurniture) table.getColumnModel().getColumn(columnIndex));
		if (cf.isVisible() && visible) {
			return;
		} else if (!cf.isVisible() && !visible) {
			return;
		}
		cf.setVisible(visible);
		columnIndex = cf.getColumnIndex();
		if (visible) {
			cf.setMaxWidth(cf.getSavedMaxWidth());
			cf.setMinWidth(cf.getSavedMinWidth());
			cf.setPreferredWidth(cf.getSavedPreferredWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMaxWidth(cf.getSavedMaxWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMinWidth(cf.getSavedMinWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setPreferredWidth(cf.getSavedPreferredWidth());
		} else {
			cf.setSavedMinWidth(cf.getMinWidth());
			cf.setSavedMaxWidth(cf.getMaxWidth());
			cf.setSavedPreferredWidth(cf.getPreferredWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMaxWidth(cf.getSavedMaxWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMinWidth(cf.getSavedMinWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setPreferredWidth(cf.getSavedPreferredWidth());
			cf.setMinWidth(0);
			cf.setMaxWidth(0);
			cf.setPreferredWidth(0);
		}
		if (updatePreferences) {
			try {
				this.updateColumnInfoPreferences(this.convertColFurnitureToTableColInfo(cf, visible));
			} catch (UserPreferenceException e) {
				log.error("update column visibility got exception " + e.getMessage());
				CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "Unable to update column visibility for table " + tableName, e);
			}
		}
	}

	private TableColumnInfo convertColFurnitureToTableColInfo(ColumnFurniture cf, boolean visible) {
		TableColumnInfo colinfo = new TableColumnInfo();
		colinfo.setModelIndex(cf.getModelIndex());
		colinfo.setColumnIndex(cf.getColumnIndex());
		colinfo.setPreferredWidth(cf.getSavedPreferredWidth());
		colinfo.setVisible(visible);
		colinfo.setLabel(cf.getLabel());
		return colinfo;
	}
	
	public void initColumnVisibility(PCeNTableView table) {
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			ColumnFurniture column = (ColumnFurniture) table.getColumnModel().getColumn(i);
			if (! column.isVisible()) {
				//changeColumnVisibility(table, i, false);
				this.updateColumn(column, table);
			}
		}
	}
	
	/**
	 * Used to initialize the settings of the preference or default invisible columns.
	 * @param cf
	 * @param table
	 */
	public void updateColumn(ColumnFurniture cf, JTable table) {
		int columnModelIndex = cf.getModelIndex();
		boolean visible = cf.isVisible();
		if (visible) {
			cf.setMaxWidth(cf.getSavedMaxWidth());
			cf.setMinWidth(cf.getSavedMinWidth());
			cf.setPreferredWidth(cf.getSavedPreferredWidth());
			table.getTableHeader().getColumnModel().getColumn(columnModelIndex).setMaxWidth(cf.getSavedMaxWidth());
			table.getTableHeader().getColumnModel().getColumn(columnModelIndex).setMinWidth(cf.getSavedMinWidth());
			table.getTableHeader().getColumnModel().getColumn(columnModelIndex).setPreferredWidth(cf.getSavedPreferredWidth());
		} else {
			cf.setSavedMinWidth(cf.getMinWidth());
			cf.setSavedMaxWidth(cf.getMaxWidth());
			cf.setSavedPreferredWidth(cf.getPreferredWidth());
			table.getTableHeader().getColumnModel().getColumn(columnModelIndex).setMaxWidth(cf.getSavedMaxWidth());
			table.getTableHeader().getColumnModel().getColumn(columnModelIndex).setMinWidth(cf.getSavedMinWidth());
			table.getTableHeader().getColumnModel().getColumn(columnModelIndex).setPreferredWidth(cf.getSavedPreferredWidth());
			cf.setMinWidth(0);
			cf.setMaxWidth(0);
			cf.setPreferredWidth(0);
		}
	}

	
}