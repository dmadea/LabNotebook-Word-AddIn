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
package com.chemistry.enotebook.report.beans.experiment;

import java.util.ArrayList;
import java.util.List;

public class MonomerBatchList {

	private String listName;
	private String key;
	private String stepName;
	private List<PrintableMonomerBatch> monomerBatches = new ArrayList<PrintableMonomerBatch>();
	
	public MonomerBatchList() {
	}
	public String getListName() {
		return listName;
	}
	public void setListName(String listName) {
		this.listName = listName;
	}
	public String getStepName() {
		return stepName;
	}
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	public List<PrintableMonomerBatch> getMonomerBatches() {
		return monomerBatches;
	}
	public void setMonomerBatches(List<PrintableMonomerBatch> monomerBatches) {
		this.monomerBatches = monomerBatches;
	}
	public void addMonomerBatch(PrintableMonomerBatch batch) {
		this.monomerBatches.add(batch);
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String toXml() {
		StringBuffer buff = new StringBuffer("<monomerBatchList>");
		buff.append("<listKey>").append(this.getKey()).append("</listKey>");
		buff.append("<listName>").append(this.getListName()).append("</listName>");
		buff.append("<stepName>").append(this.getStepName()).append("</stepName>");
		buff.append("<monomerBatches>");
		for (PrintableMonomerBatch monomerBatch : monomerBatches) {
			buff.append(monomerBatch.toXml());
		}
		buff.append("</monomerBatches>");
		buff.append("</monomerBatchList>");
		return buff.toString();
	}
	
}
