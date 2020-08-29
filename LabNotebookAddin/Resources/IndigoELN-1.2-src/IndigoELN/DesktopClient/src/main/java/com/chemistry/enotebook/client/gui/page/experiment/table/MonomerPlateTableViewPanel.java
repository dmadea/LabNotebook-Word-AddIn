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
package com.chemistry.enotebook.client.gui.page.experiment.table;

import com.chemistry.enotebook.client.gui.page.experiment.plate.MonomerPlateContainer;
import com.chemistry.enotebook.domain.MonomerPlate;

import javax.swing.*;
import java.awt.*;

public class MonomerPlateTableViewPanel extends JPanel 
	implements MonomerPlateContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4746725167373776591L;
	private MonomerPlate monomerPlate;
	
	public MonomerPlateTableViewPanel(LayoutManager layout) {
		super(layout);
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
