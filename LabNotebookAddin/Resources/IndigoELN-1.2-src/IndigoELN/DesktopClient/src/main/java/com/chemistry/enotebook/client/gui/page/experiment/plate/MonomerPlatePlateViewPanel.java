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
import com.chemistry.enotebook.domain.MonomerPlate;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MonomerPlatePlateViewPanel extends JPanel implements MonomerPlateContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1076670985125693522L;
	private MonomerPlate monomerPlate;
	private JPanel plateView;
	private MonomerBatchDetailContainer batchDetail;
	private JPanel reactionsView;
	private JScrollPane scrollMonomerBatchDetailViewPane;

	public MonomerPlatePlateViewPanel(MonomerPlate monomerPlate,
									  JPanel plateView,
									  MonomerBatchDetailContainer batchDetail, // vb 7/16 JPanel batchDetail,
									  JPanel reactionsView) {
		this.monomerPlate = monomerPlate;
		this.plateView = plateView;
		this.batchDetail = batchDetail;
		this.reactionsView = reactionsView;
		scrollMonomerBatchDetailViewPane = new JScrollPane();
		scrollMonomerBatchDetailViewPane.getViewport().add(batchDetail);
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
		if (monomerPlate.getContainer().isUserDefined())
			horiBLabelText = "Custom Container : " + monomerPlate.getContainer().getContainerName();
		else
			horiBLabelText = "Compound Management Container : " + monomerPlate.getContainer().getContainerName();

		horiBLabel.setText(horiBLabelText);

		JPanel plateViewPanel = new JPanel();
		plateViewPanel.setLayout(new BorderLayout());
		plateViewPanel.add(horiTopPanel, BorderLayout.PAGE_START);
		plateViewPanel.add(vertLeftPanel, BorderLayout.LINE_START);
		plateViewPanel.add(plateView, BorderLayout.CENTER);
		plateViewPanel.add(horiBottomPanel, BorderLayout.PAGE_END);

		int position = monomerPlate.getContainer().getYPositions();
		char[] charPosition =  {((char)(64 + position))};
		topRightLabel.setText("A" + monomerPlate.getContainer().getXPositions());
		bottomLeftLabel.setText(new String(charPosition) + "1");

		if (monomerPlate.getContainer().getMajorAxis() == null)
		    middleLeftArrowLabel.setVisible(true);
		else if ( monomerPlate.getContainer().getMajorAxis().equals("X"))
			topCenterArrowLabel.setVisible(true);
		else
			middleLeftArrowLabel.setVisible(true);

		FormLayout layout = new FormLayout("pref:grow, 5dlu, 255dlu",	"pref");
		this.setLayout(layout);
		CellConstraints cc = new CellConstraints();
		this.add(plateViewPanel, cc.xy(1, 1));
		batchDetail.setBorder(new LineBorder(Color.black, 2));
		this.add(batchDetail, cc.xy(3, 1));
		//this.add(reactionsView, cc.xywh(1, 3, 1, 1));
	}

	public void refresh() {
		//Refresh the data should be goodenough.
/*		this.removeAll();
		this.scrollMonomerBatchDetailViewPane = batchDetail;
		this.buildPanel();
		this.revalidate();
		this.repaint();*/

		batchDetail.batchSelectionChanged(null);
	}

	/**
	 * @return the monomerPlate
	 */
	public MonomerPlate getMonomerPlate() {
		return monomerPlate;
	}

	/**
	 *
	 */
	public void setMonomerPlate(MonomerPlate monomerPlate) {
		this.monomerPlate = monomerPlate;
	}


}
