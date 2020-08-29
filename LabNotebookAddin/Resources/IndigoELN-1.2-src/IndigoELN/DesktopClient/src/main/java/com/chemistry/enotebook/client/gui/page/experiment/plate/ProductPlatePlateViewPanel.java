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
package com.chemistry.enotebook.client.gui.page.experiment.plate;

import com.chemistry.enotebook.client.gui.common.utilsui.VerticalLabelUI;
import com.chemistry.enotebook.domain.ProductPlate;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.virtuan.plateVisualizer.StaticPlateRenderer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ProductPlatePlateViewPanel extends JPanel implements ProductPlateContainer{

	private static final long serialVersionUID = 6767254157692371441L;
	
	private ProductPlate productPlate;
	private StaticPlateRenderer plateView;
	private JScrollPane batchDetail; // JPanel batchDetail;
	private ProductBatchDetailContainer batchContents;
	private JPanel reactionsView;

	public ProductPlatePlateViewPanel(ProductPlate productPlate,
									  StaticPlateRenderer plateView,
									  JScrollPane batchDetail, // vb 6/28 JPanel batchDetail,
									  ProductBatchDetailContainer batchContents,
									  JPanel reactionsView) {
		this.productPlate = productPlate;
		this.plateView = plateView;
		this.batchDetail = batchDetail;
		this.batchContents = batchContents;
		this.reactionsView = reactionsView;
		this.buildPanel();
	}

	private void buildPanel() {
		JPanel horiTopPanel = new JPanel();
		JPanel vertLeftPanel = new JPanel();
		JPanel horiBottomPanel = new JPanel();

		horiTopPanel.setLayout(new BorderLayout());
		JLabel topLeftLabel = new JLabel("A1");
		JLabel topRightLabel = new JLabel();
		JLabel topCenterArrowLabel = new JLabel("Fill Order-->");
		topCenterArrowLabel.setVisible(false);
		topCenterArrowLabel.setHorizontalAlignment(JLabel.CENTER);
		horiTopPanel.add(topLeftLabel, BorderLayout.LINE_START);
		horiTopPanel.add(topCenterArrowLabel, BorderLayout.CENTER);
		horiTopPanel.add(topRightLabel, BorderLayout.LINE_END);

		vertLeftPanel.setLayout(new BorderLayout());
		JLabel middleLeftArrowLabel = new JLabel("Fill Order-->");
		middleLeftArrowLabel.setUI( new VerticalLabelUI(true) );
		middleLeftArrowLabel.setHorizontalAlignment(JLabel.CENTER);
		JLabel bottomLeftLabel = new JLabel("");
		middleLeftArrowLabel.setVisible(false);
		vertLeftPanel.add(middleLeftArrowLabel, BorderLayout.CENTER);
		vertLeftPanel.add(bottomLeftLabel, BorderLayout.PAGE_END);

		horiBottomPanel.setLayout(new BorderLayout());
		JLabel horiBLabel = new JLabel();
		horiBottomPanel.add(horiBLabel);
		horiBLabel.setHorizontalAlignment(JLabel.CENTER);
		String horiBLabelText = "";
		if (productPlate.getContainer().isUserDefined())
			horiBLabelText = "Custom Container : " + productPlate.getContainer().getContainerName();
		else
			horiBLabelText = "Compound Management Container : " + productPlate.getContainer().getContainerName();

		horiBLabel.setText(horiBLabelText);

		JPanel plateViewPanel = new JPanel();
		plateViewPanel.setLayout(new BorderLayout());
		plateViewPanel.add(horiTopPanel, BorderLayout.PAGE_START);
		plateViewPanel.add(vertLeftPanel, BorderLayout.LINE_START);
		plateViewPanel.add(plateView, BorderLayout.CENTER);
		plateViewPanel.add(horiBottomPanel, BorderLayout.PAGE_END);

		int position = productPlate.getContainer().getYPositions();
		char[] charPosition =  {((char)(64 + position))};
		topRightLabel.setText("A" + productPlate.getContainer().getXPositions());
		bottomLeftLabel.setText(new String(charPosition) + "1");

		if (productPlate.getContainer().getMajorAxis() == null)
		    middleLeftArrowLabel.setVisible(true);
		else if ( productPlate.getContainer().getMajorAxis().equals("X"))
			topCenterArrowLabel.setVisible(true);
		else
			middleLeftArrowLabel.setVisible(true);

		FormLayout layout = new FormLayout("pref:grow, 5dlu, 255dlu",	"top:150dlu, 5dlu, 150dlu");
		// Set row groups (all rows are in group)
		int[][] rowGroups = new int[][] {{1,3}};
		layout.setRowGroups(rowGroups);
		this.setLayout(layout);
		CellConstraints cc = new CellConstraints();
		this.add(plateViewPanel, cc.xy(1, 1));
		batchDetail.setBorder(new LineBorder(Color.black, 2));
		this.add(batchDetail, cc.xy(3, 1));
		this.add(reactionsView, cc.xyw(1, 3, 3));
	}

	public void refresh() {
		this.removeAll();
		this.plateView.clearSelection();
		this.batchContents.refresh();
		this.buildPanel();
		this.revalidate();
		//this.repaint();
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
