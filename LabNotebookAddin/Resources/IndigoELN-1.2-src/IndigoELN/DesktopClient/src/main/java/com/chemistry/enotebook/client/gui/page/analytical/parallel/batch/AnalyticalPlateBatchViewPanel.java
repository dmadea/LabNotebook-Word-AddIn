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
package com.chemistry.enotebook.client.gui.page.analytical.parallel.batch;

import com.chemistry.enotebook.domain.ProductPlate;
import com.chemistry.enotebook.utils.ShowColorLegendDialogMenuItem;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AnalyticalPlateBatchViewPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2505957395815259117L;
	private ProductPlate productPlate;
	private JPanel plateView;
	private JPanel batchDetail; // JPanel batchDetail;
	private JPopupMenu colorLegendPopupMenu;
	
	public AnalyticalPlateBatchViewPanel (ProductPlate productPlate,
									      JPanel plateView,
									      JPanel batchDetail) {
		this.productPlate = productPlate;
		this.plateView = plateView;
		this.batchDetail = batchDetail;
		this.buildPanel();
	}
	
	private void buildPanel() {
		FormLayout layout = new FormLayout("250dlu, 5dlu, right:pref",	"pref"); 
		// Set row groups (all rows are in group)
		this.setLayout(layout);
		CellConstraints cc = new CellConstraints();
		this.add(plateView, cc.xy(1, 1));
		batchDetail.setBorder(new LineBorder(Color.black, 2));
		this.add(batchDetail, cc.xy(3, 1));	
		
		colorLegendPopupMenu = new JPopupMenu();
		colorLegendPopupMenu.add(new ShowColorLegendDialogMenuItem());
		batchDetail.setComponentPopupMenu(colorLegendPopupMenu);
		
		addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent evt) {
				if (evt.isPopupTrigger()) {// show popup
					colorLegendPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			} // end mouseReleased
			
			public void mouseClicked(MouseEvent evt) {
				if (evt.isPopupTrigger()) {// show popup
					colorLegendPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			} // end mouseClicked
		});	
	}
	
	public void refresh(JPanel batchDetail) {
		this.removeAll();
		this.batchDetail = batchDetail;
		this.buildPanel();
		this.revalidate();
		this.repaint();
	}

	/**
	 * @return the productPlate
	 */
	public ProductPlate getProductPlate() {
		return productPlate;
	}

	/**
	 * 
	 */
	public void setProductPlate(ProductPlate productPlate) {
		this.productPlate = productPlate;
	}

}
