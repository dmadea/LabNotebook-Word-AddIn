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
package com.chemistry.enotebook.client.gui.page.table;

/**
 * 
 *
 */

import com.chemistry.enotebook.client.gui.common.utils.AmountCellRenderer;
import com.chemistry.enotebook.client.gui.common.utils.CeNTableCellAlignedComponent;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.experiment.table.ChimeRenderingPanel;
import com.chemistry.enotebook.client.gui.page.experiment.table.RackFlagType;
import com.chemistry.enotebook.domain.AmountModel;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.common.MolString;
import com.chemistry.enotebook.utils.DefaultPreferences;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class PCeNTableViewCellRenderer implements TableCellRenderer {
    private Color foreground = DefaultPreferences.getRowForegroundColor(); //new Color(30, 64, 124);
    private Color errorMsgForeground = DefaultPreferences.getErrorMsgRowForegroundColor(); //Color.Red
    private Color background = DefaultPreferences.getEditableRowBackgroundColor(); //new Color(255, 255, 245);
    private Color selectedForeground = DefaultPreferences.getSelectedRowForegroundColor(); //new Color(255, 255, 245);
    private Color selectedBackground = DefaultPreferences.getSelectedRowBackgroundColor(); //new Color(100, 100, 100);
    // vb 11/2
    private Font tableCellFont = DefaultPreferences.getStandardTableCellFont();
    //private Font tableHeaderFont = DefaultPreferences.getStandardTableHeaderFont();

    private Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
    private JLabel processingLabel = new JLabel("Processing ... ");
    private JLabel naLabel = new JLabel(""); // vb 11/21 Eliminate any N/A's
    //private Border bkgrnd = null;
    protected boolean m_selected;
    //private BufferedImage spotFire = null;
    AmountCellRenderer renderer = new AmountCellRenderer();
    BatchAttributeComponentUtility componentUtility;

    public PCeNTableViewCellRenderer() {
        naLabel.setFont(tableCellFont); // vb 11/2 (new java.awt.Font("Dialog", 1, 14));
        naLabel.setHorizontalAlignment(SwingConstants.LEFT);
        naLabel.setVerticalAlignment(SwingConstants.TOP);
        //naLabel.setOpaque(false);
        naLabel.setOpaque(true);
        naLabel.setBorder(noFocusBorder);
        componentUtility = BatchAttributeComponentUtility.getInstance();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                   int column) {
        //ParallelCeNTableView tableView = (ParallelCeNTableView) table;
        // before all, put default color
        String coluName = table.getColumnName(column);
        if (coluName == PCeNTableView.PLATE_COMMENTS) {
            String s = (String) value;
            JTextField textField = new JTextField();
            //label.setOpaque(false);
            textField.setBorder(noFocusBorder);
            textField.setText(s);
            textField.setToolTipText(s);
            textField.setFont(tableCellFont);
            textField.setForeground(textField.getDisabledTextColor());
            return textField;
        }
        if (coluName == PCeNTableView.STEREOISOMER) {
            ProductBatchModel model = ((ProductBatchModel) ((PCeNTableModel) table.getModel()).getConnector().getBatchModel(row));

            StereoIsomerCodeControl comboBox = createStereoIsomerCodeControl(model, null);

            Rectangle rect = table.getCellRect(row, column, true);
            comboBox.setPreferredSize(new java.awt.Dimension((int) rect.getWidth(), 20));
            JPanel comboPanel = new CeNTableCellAlignedComponent(comboBox);

            if (table.isCellEditable(row, column)) {
                if (isSelected) {
                    comboPanel.setForeground(selectedForeground);
                    comboPanel.setBackground(selectedBackground);
                } else {
                    comboPanel.setForeground(foreground);
                    comboPanel.setBackground(background);
                }
            } else {
                comboBox.setForeground(UIManager.getColor("Table.disabledForeground"));
                comboBox.setBackground(UIManager.getColor("Table.disabledBackground"));
            }

            return comboPanel;
        }
        if (value instanceof MolString) {
            ChimeRenderingPanel chimeRenderingPanel = new ChimeRenderingPanel();
            MolString molString = (MolString) value;
            String molData = molString.getMolString();
            chimeRenderingPanel.setRendererData(molData);
            if (isSelected) {
                chimeRenderingPanel.setForeground(selectedForeground);
                chimeRenderingPanel.setBackground(selectedBackground);
            } else {
                chimeRenderingPanel.setForeground(foreground);
                chimeRenderingPanel.setBackground(background);
            }

            //This numbering is not required unless sorting is needed on this column.
            //This is temporally disabled. To remove completely, remove Index field in the MolString class.
/*			int a = molString.getIndex() + 1;
			if (a != 0)
				chimeRenderingPanel.setLabel(a + "");
			// deal with selection colors
			else
				chimeRenderingPanel.setImage(null);
*/
            chimeRenderingPanel.setBorder(noFocusBorder);
            // chimeRenderingPanel.setPropertiesToolTip((Hashtable)(collection.getProperties().elementAt(realIndex)));
            return chimeRenderingPanel;
        } else if (value instanceof AmountModel) {
            // return renderer.getTableCellRendererComponent(tableView, value, isSelected, hasFocus, row, column);
            AmountModel mAmountModel = (AmountModel) value;
            double va = mAmountModel.GetValueInStdUnitsAsDouble();
            String v = "" + va;
            JLabel label = new JLabel(v);
            label.setOpaque(false);
            label.setOpaque(true);
            label.setBorder(noFocusBorder);

            label.setToolTipText(v);
            label.setFont(tableCellFont); // vb 11/2 (new java.awt.Font("Dialog", 1, 14));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.TOP);
            if (isSelected) {
                label.setForeground(selectedForeground);
                label.setBackground(selectedBackground);
            } else {
                label.setForeground(foreground);
                label.setBackground(background);
            }
            if (hasFocus) {
                label.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
                if (table.isCellEditable(row, column)) {
                    label.setForeground(UIManager.getColor("Table.focusCellForeground"));
                    label.setBackground(UIManager.getColor("Table.focusCellBackground"));
                }
            } else {
                label.setBorder(noFocusBorder);
            }
            return label;

        } else if (value instanceof String) {
            String s = (String) value;
            // //System.out.println(s);
            if (s.length() < 30) {
                JLabel label = new JLabel();
                //label.setOpaque(false);
                label.setOpaque(true);
                label.setBorder(noFocusBorder);
                label.setText(s);
                label.setToolTipText(s);
                label.setFont(tableCellFont); // vb 11/2 (new java.awt.Font("Dialog", 1, 14));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.TOP);
                if (isSelected) {
                    label.setForeground(selectedForeground);
                    label.setBackground(selectedBackground);
                } else {
                    label.setForeground(foreground);
                    label.setBackground(background);
                }
                if (value.toString().equalsIgnoreCase(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_FAILED))
                    label.setForeground(errorMsgForeground);
                if (hasFocus) {
                    label.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
                    if (table.isCellEditable(row, column)) {
                        label.setForeground(UIManager.getColor("Table.focusCellForeground"));
                        label.setBackground(UIManager.getColor("Table.focusCellBackground"));
                    }
                } else {
                    label.setBorder(noFocusBorder);
                }
                return label;
            } else {// text area
                JTextArea label = new JTextArea();
                label.setLineWrap(true);
                label.setWrapStyleWord(true);
                label.setHighlighter(null);
                label.setEditable(false);
                JScrollPane scroll = new JScrollPane(label);
                scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                scroll.setBorder(null);
                label.setText(s);
                label.setToolTipText(s);
                label.setFont(tableCellFont); // vb 11/2 (new java.awt.Font("Dialog", 1, 14));
                if (isSelected) {
                    label.setForeground(selectedForeground);
                    label.setBackground(selectedBackground);
                } else {
                    label.setForeground(foreground);
                    label.setBackground(background);
                }
                if (hasFocus) {
                    label.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
                    if (table.isCellEditable(row, column)) {
                        label.setForeground(UIManager.getColor("Table.focusCellForeground"));
                        label.setBackground(UIManager.getColor("Table.focusCellBackground"));
                    }
                } else {
                    label.setBorder(noFocusBorder);
                }
                return label;
            }
        } else if (value instanceof Boolean) {

            Boolean checked = (Boolean) value;
            if (coluName.equalsIgnoreCase("List")) {// Stoich table instance

                boolean yesno = checked.booleanValue();
                String yes = "YES";
                if (!yesno) {
                    yes = "NO";
                }
                JLabel label = new JLabel();
                label.setOpaque(false);
                label.setOpaque(true);
                label.setBorder(noFocusBorder);
                label.setText(yes);
                label.setToolTipText(yes);
                label.setFont(tableCellFont); //vb 11/2 (new java.awt.Font("Dialog", 1, 14));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    label.setForeground(selectedForeground);
                    label.setBackground(selectedBackground);
                } else {
                    label.setForeground(foreground);
                    label.setBackground(background);
                }
                if (hasFocus) {
                    label.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
                    if (table.isCellEditable(row, column)) {
                        label.setForeground(UIManager.getColor("Table.focusCellForeground"));
                        label.setBackground(UIManager.getColor("Table.focusCellBackground"));
                    }
                } else {
                    label.setBorder(noFocusBorder);
                }
                return label;
            } else {

                JCheckBox chkBox = new JCheckBox();
                JPanel chkBoxPanel = new JPanel();
                if (isSelected && !coluName.equalsIgnoreCase(PCeNTableView.SELECT_OPTION)) {
                    chkBoxPanel.setForeground(selectedForeground);
                    chkBoxPanel.setBackground(selectedBackground);
                } else {
                    chkBoxPanel.setForeground(foreground);
                    chkBoxPanel.setBackground(background);
                }
                chkBox.setSelected(checked.booleanValue());
                chkBoxPanel.add(chkBox);
                return chkBoxPanel;
            }
        } else if (value instanceof RackFlagType) {
            RackFlagType mRackFlagType = (RackFlagType) value;
            JComboBox combo = new JComboBox(mRackFlagType.getRackPlateFlagValues());
            combo.setSelectedIndex(mRackFlagType.getSelected());
            JPanel comboPanel = new JPanel();
            comboPanel.add(combo);
            if (isSelected) {
                comboPanel.setForeground(selectedForeground);
                comboPanel.setBackground(selectedBackground);
            } else {
                comboPanel.setForeground(foreground);
                comboPanel.setBackground(background);
            }
            return comboPanel;
        } else {
            if (isSelected) {
                naLabel.setForeground(selectedForeground);
                naLabel.setBackground(selectedBackground);
            } else {
                naLabel.setForeground(foreground);
                naLabel.setBackground(background);
            }
            return naLabel;
        }
    } // end method

    public static StereoIsomerCodeControl createStereoIsomerCodeControl(ProductBatchModel batch, final NotebookPageModel pageModel) {
        return new StereoIsomerCodeControl(batch, pageModel);
    }


}
