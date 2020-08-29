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
package com.chemistry.enotebook.client.gui.preferences;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.debug.FormDebugPanel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ColumnPreferencesPanel extends JPanel  implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2116459244600182808L;
	// always visible
	public static final String NBK_BATCH_NUM = "NBK Batch #";
	// default visible
	public static final String STATUS = "Status";
	public static final String BATCH_MW = "Batch MW";
	public static final String DELIVERED_WEIGHT = "Delivered Weight";
	public static final String DELIVERED_MMOLES = "Delivered Moles";
	public static final String MOL_FORMULA = "Mol Formula";
	public static final String DESIGN_MONOMER = "Design Monomer";
	public static final String DELIVERED_MONOMER = "Delivered Monomer";
	public static final String RXN_MOLES = "Rxn Moles";
	public static final String RXN_WEIGHT = "Rxn Weight";
	public static final String COMPOUND_ID = "Compound ID";
	// default hidden
	public static final String SALT_CODE = "Salt Code";
	public static final String RXN_ROLE = "Rxn Role";	
	public static final String TIMES_USED = "Times Used";
	public static final String BATCH_ANALYTICAL_COMMENTS = "Analytical Comments";	
	public static final String MOLARITY = "Molarity";
	public static final String TUBE_MOLARITY = "Tube Molarity";
	public static final String PARENT_MW = "Parent MW";
	public static final String DELIVERED_VOLUME = "Delivered Volume";
	public static final String EXTRA_NEEDED = "Extra Needed";
	public static final String RXN_VOLUME = "Rxn Volume";
	public static final String DEAD_VOLUME = "Dead Volume";
	public static final String PURITY = "MN Purity";
	public static final String ANALYTICAL_PURITY = "Purity";
	public static final String DENSITY = "Density";
	public static final String SALT_EQ = "Salt EQ";
	public static final String BATCH_COMMENTS = "Batch Comments";
	public static final String PLATE = "Plate";
	public static final String WELL_POSITION = "Well #";
	public static final String LIMITING = "Limiting";
	public static final String HAZARD_COMMENTS = "Hazards";
	public static final String HANDLING_PRECAUTIONS = "Handling Precations";
	public static final String STORAGE_INSTRUCTIONS = "Storage Instructions";
	public static final String EXPTAB_HAZARD_COMMENTS = "Compound Hazards";
	public static final String COMMENTS = "Comments";
	public static final String STRUCTURE = "Structure";
	public static final String MOL_WEIGHT = "Mol Wgt";
	public static final String AMT_NEEDED_RXN = "Amt Needed/Rxn";
	public static final String TOTAL_ORDERED = "Total Ordered";
	public static final String TOTAL_WEIGHT = "Total Amt. Made";
	public static final String TOTAL_VOLUME = "Total Vol. Made";
	public static final String TOTAL_MOLARITY = "Total Molarity";
	public static final String VOLUME = "Volume";
	public static final String TOTAL_DELIVERED_AMT = "Total Delivered Amount";
	public static final String NMR = "NMR";
	public static final String MS = "MS";
	public static final String SFC_MS = "SFC-MS";
	public static final String HPLC = "HPLC";
	public static final String VIEW_PURIFICATION_SERVICE = "View PurificationService";
	public static final String PURIFICATION_SERVICE_DATA = "PurificationService Data";
	public static final String LINK_BATCH = "Link Batch";
	public static final String AMT_MADE = "Amt Made";
	public static final String CONVERSATIONAL_BATCH = "Conversational Batch";
	public static final String VIRTUAL_COMP_NUM = "Virtual Compound ID";
	public static final String STEREOISOMER = "Stereoisomer";
	public static final String PRECURSORS = "Precursors";
	public static final String THEORETICAL_MMOLES = "Theo. Moles";
	public static final String PERCENT_YIELD = "%Yield";
	public static final String THEORETICAL_WEIGHT = "Theo. Wgt.";
	public static final String COMPOUND_STATE = "Compound State";
	public static final String EXT_SUPPLIER = "Ext Supplier";
	public static final String SOURCE = "Source";
	public static final String SOURCE_DETAILS = "Source Detail";
	public static final String PROJECT = "Project";
	public static final String COMPOUND_PROTECTION = "Compound Protection";
	public static final String RESIDUAL_SOLVENTS = "Residual Solvents";
	public static final String SOLUBILITY_IN_SOLVENTS = "Solubility in Solvents";
	public static final String TOTAL_MOLES_NEEDED = "Total Moles Needed";
	public static final String TOTAL_WEIGHT_NEEDED = "Total Weight Needed";
	public static final String AMT_REMAINING_WEIGHT = "Amt Remaining Wgt.";
	public static final String AMT_REMAINING_VOLUME = "Amt Remaining Vol.";
	public static final String AMT_IN_TUBE_VOL = "<html><font size=3>Amt In Tube <br>(Volume)<font></html>";
	public static final String AMT_IN_VIAL_WT = "<html><font size=3>Amt In Vial <br>(Weight)<font></html>";
	public static final String AMT_IN_WELL_WT = "<html><font size=3>Amt In Well/Tube <br>(Weight)<font></html>";
	public static final String AMT_IN_WELL_VOL = "<html><font size=3>Amt In Well/Tube <br>(Volume)<font></html>";
	public static final String AMT_IN_WELL_MOLARITY = "<html><font size=3>Amt In Well/Tube <br>(Molarity)<font></html>";


	
	private final static String BUTTON_LABEL_REMOVE = "Remove <<";
	private final static String BUTTON_LABEL_ADD = "Add    >>";
	private List includedCols = new ArrayList();
	private List excludedCols = new ArrayList();
	private JList includedColsList = new JList();
	private JList excludedColsList = new JList();
	private JButton addButton = new JButton(BUTTON_LABEL_ADD);
	private JButton removeButton = new JButton(BUTTON_LABEL_REMOVE);
	
	public ColumnPreferencesPanel(List includedCols, List excludedCols) {
		this.includedCols = includedCols;
		this.excludedCols = excludedCols;
		this.initGui();
	}

	private void initGui() {
		FormLayout layout = new FormLayout("5dlu, 70dlu:grow(0.5), 5dlu, pref, 5dlu, 70dlu:grow(0.5), 5dlu",
										   "5dlu, 70dlu, pref, 5dlu, pref, 70dlu, 5dlu");
		PanelBuilder builder = new PanelBuilder(layout, new FormDebugPanel());
		CellConstraints cc = new CellConstraints();
		JScrollPane scrollerLeft = createList(includedColsList, includedCols);
		builder.add(scrollerLeft, cc.xywh(2, 2, 1, 5));
		addButton.addActionListener(this);
		builder.add(addButton, cc.xy(4, 3));
		removeButton.addActionListener(this);
		builder.add(removeButton, cc.xy(4, 5));
		JScrollPane scrollerRight = createList(excludedColsList, excludedCols);
		builder.add(scrollerRight, cc.xywh(6, 2, 1, 5));
		this.add(builder.getPanel());
	}
	
	private JScrollPane createList(JList list, List prepopulateList) {
		DefaultListModel model = new DefaultListModel();
		if (prepopulateList != null && prepopulateList.size() > 0) {
			for (Iterator it = prepopulateList.iterator(); it.hasNext();) {
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

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equalsIgnoreCase(BUTTON_LABEL_ADD)) {
			DefaultListModel model = (DefaultListModel) excludedColsList.getModel();
			Object sel = excludedColsList.getSelectedValue();
			model.addElement(sel);
		} else if (cmd.equalsIgnoreCase(BUTTON_LABEL_REMOVE)) {
			DefaultListModel inmodel = (DefaultListModel) includedColsList.getModel();
			Object selected[] = includedColsList.getSelectedValues();
			for (int i = 0; i < selected.length; i++) {
				inmodel.removeElement(selected[i]);
			}
			DefaultListModel exmodel = (DefaultListModel) excludedColsList.getModel();
			for (int i = 0; i < selected.length; i++) {
				inmodel.addElement(selected[i]);
			}
		}
	}

	
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the 
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Column Preferences");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        List include = new ArrayList();
		for (int i=0; i<10; i++) {
			include.add("" + i + "th");
		}
		List exclude = new ArrayList();
		for (int i=10; i<20; i++) {
			exclude.add("" + i + "th");
		}
		ColumnPreferencesPanel panel = new ColumnPreferencesPanel(include, exclude);
        panel.setOpaque(true); //content panes must be opaque
        frame.setContentPane(panel);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
