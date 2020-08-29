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
package com.chemistry.enotebook.report.beans.synthesis;

import com.chemistry.enotebook.report.beans.experiment.ExperimentProcedure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SynthesisPlateReport {

	String experimentNumber = "";
	String owner = "";
	List<SynthesisReactionStep> steps = new ArrayList<SynthesisReactionStep>();
	ExperimentProcedure procedure = new ExperimentProcedure();

	public String getExperimentNumber() {
		return experimentNumber;
	}
	public void setExperimentNumber(String experimentNumber) {
		this.experimentNumber = experimentNumber;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public void addStep(SynthesisReactionStep step) {
		this.steps.add(step);
	}
	
	public String toXml() {
		StringBuffer buff = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buff.append("<synthesisPlateReport>");
		buff.append("<experiment>");
		buff.append(this.getExperimentNumber());
		buff.append("</experiment>");
		buff.append("<owner>");
		buff.append(this.getOwner());
		buff.append("</owner>");
		for (Iterator it = steps.iterator(); it.hasNext();) {
			buff.append(((SynthesisReactionStep) it.next()).toXml());
		}
		buff.append(this.getProcedure().toXml());
		buff.append("</synthesisPlateReport>");
		return buff.toString();
	}
	
	public void dispose() {
		if (this.steps != null) {
			for (SynthesisReactionStep s : steps) {
				s.dispose();
			}
				
		}
	}
	public ExperimentProcedure getProcedure() {
		return procedure;
	}
	public void setProcedure(ExperimentProcedure procedure) {
		this.procedure = procedure;
	}
	
}
