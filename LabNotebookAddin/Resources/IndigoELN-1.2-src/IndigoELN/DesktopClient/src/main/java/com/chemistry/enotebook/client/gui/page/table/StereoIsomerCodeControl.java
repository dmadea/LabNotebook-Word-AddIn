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

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.regis_submis.RegCodeMaps;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.utils.CodeTableUtils;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;

public class StereoIsomerCodeControl extends CeNComboBox {

	private static final long serialVersionUID = 8879811594940791229L;

	private static void addSicCode(CeNComboBox comboBox, String sicode) {
        final HashMap<String, String> sicMap = RegCodeMaps.getInstance().getStereoisomerCodeMap();
        comboBox.addItem(sicode + " - " + sicMap.get(sicode));
    }

    private ProductBatchModel batch;

    public StereoIsomerCodeControl(final ProductBatchModel batch, final NotebookPageModel pageModel) {
        super();
        this.batch = batch;
        final ArrayList<String> sicList = batch.getRegInfo().getBatchVnVInfo().getSuggestedSICList();
        String code = batch.getCompound().getStereoisomerCode();

        int selectedSIC = 0;
        String sicCode;
        if (sicList.isEmpty()) {
            CodeTableUtils.fillComboBoxWithIsomers(this);
            BatchAttributeComponentUtility.updateStereoisomerComboBox(this, code);
        } else {
            for (int i = 0; i < sicList.size(); i++) {
                sicCode = sicList.get(i);
                if (sicCode.equals(code)) {
                    selectedSIC = i;
                }
                addSicCode(this, sicCode);
            }
            this.setSelectedIndex(selectedSIC);
        }
        addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                String selectedISCode = (String) getSelectedItem();
                if (selectedISCode != null && evt.getStateChange() == ItemEvent.SELECTED) {
                    String sicSelected = selectedISCode.length() >= 5 ? selectedISCode.substring(0, 5) : selectedISCode;
                    batch.getCompound().setStereoisomerCode(sicSelected);

                    MasterController.getGUIComponent().enableSaveButtons();
                    batch.setModelChanged(true);
                    if (pageModel != null) {
                        pageModel.setModelChanged(true);
                        pageModel.setEditable(true);
                    }
                }
            }
        });
    }

    public String getValue() {
        return batch.getCompound().getStereoisomerCode();
    }
}
