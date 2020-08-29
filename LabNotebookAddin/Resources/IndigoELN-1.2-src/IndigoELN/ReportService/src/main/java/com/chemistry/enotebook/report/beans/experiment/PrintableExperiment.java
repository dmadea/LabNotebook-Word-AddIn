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

public class PrintableExperiment {

	private ExperimentHeader header;
	private ExperimentDetails details;
	private List<ReactionStep> reactionSteps = new ArrayList<ReactionStep>();
	private ExperimentProcedure procedure;
	private ReactionSummary reactionSummary;
	private AnalyticalSummaryList analyticalSummaryList = new AnalyticalSummaryList();
	private List<RegisteredPlate> registeredPlates = new ArrayList<RegisteredPlate>();
	
	public ExperimentHeader getHeader() {
		return header;
	}
	public void setHeader(ExperimentHeader header) {
		this.header = header;
	}
	public ExperimentDetails getDetails() {
		return details;
	}
	public void setDetails(ExperimentDetails details) {
		this.details = details;
	}
	public List<ReactionStep> getReactionSteps() {
		return reactionSteps;
	}
	public void setReactionSteps(List<ReactionStep> steps) {
		this.reactionSteps = steps;
	}
	public ExperimentProcedure getProcedure() {
		return procedure;
	}
	public void setProcedure(ExperimentProcedure procedure) {
		this.procedure = procedure;
	}
	public ReactionSummary getReactionSummary() {
		return reactionSummary;
	}
	public void setReactionSummary(ReactionSummary reactionSummary) {
		this.reactionSummary = reactionSummary;
	}
	public AnalyticalSummaryList getAnalyticalSummaryList() {
		return analyticalSummaryList;
	}
	public void setAnalyticalSummaryList(AnalyticalSummaryList analyticalSummaryList) {
		this.analyticalSummaryList = analyticalSummaryList;
	}
	public void addRegisteredPlate(RegisteredPlate plate) {
		this.registeredPlates.add(plate);
	}
	public String toXml() {
		StringBuffer buff = new StringBuffer("<printableExperiment>");
		buff.append(getHeader().toXml());
		buff.append(getDetails().toXml());
		if (getReactionSummary() != null) {
			buff.append(getReactionSummary().toXml());
		}
		for (ReactionStep step : reactionSteps) {
			if (step != null) {
				buff.append(step.toXml());
			}
		}
		buff.append(this.getProcedure().toXml());
		buff.append(this.getAnalyticalSummaryList().toXml());
		buff.append("<registeredPlates>");
		for (RegisteredPlate regPlate : registeredPlates) {
			buff.append(regPlate.toXml());
		}
		buff.append("</registeredPlates>");
		buff.append("</printableExperiment>");
		return buff.toString();
	}	
	public void dispose() {
		if (this.reactionSteps != null) {
			for (ReactionStep s : reactionSteps)
				s.dispose();
			this.reactionSteps.clear();
		}
	}
}
