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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SynthesisReactionStep {

	private int stepNumber;
	private List<DispensingPlate> dispensingPlates = new ArrayList<DispensingPlate>();
	private List<SynthesisPlate> synthesisPlates = new ArrayList<SynthesisPlate>();
	private List<ReagentSolvent> reagentsAndSolvents = new ArrayList<ReagentSolvent>();
	
	public void dispose() {
		if (this.dispensingPlates != null) {
			for (DispensingPlate p : dispensingPlates) 
				p.dispose();
			this.dispensingPlates.clear();
		}
		if (this.synthesisPlates != null) {
			for (SynthesisPlate p : synthesisPlates)
				p.dispose();
			this.synthesisPlates.clear();
		}
		if (this.reagentsAndSolvents != null) {
			for (ReagentSolvent r : reagentsAndSolvents) 
				r.dispose();
			this.reagentsAndSolvents.clear();	
		}
	}
	
	public int getStepNumber() {
		return stepNumber;
	}
	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}
	public void addDispensingPlate(DispensingPlate plate) {
		this.dispensingPlates.add(plate);
	}
	public void addSynthesisPlate(SynthesisPlate plate) {
		this.synthesisPlates.add(plate);
	}
	public void addReagentOrSolvent(ReagentSolvent rs) {
		this.reagentsAndSolvents.add(rs);
	}
	public String getStepName() {
		if (stepNumber == 0)
			return "" + ++stepNumber;
		return "" + stepNumber;
	}

	public String toXml() {
		StringBuffer buff = new StringBuffer("<reactionStep>");
		buff.append("<stepName>").append(this.getStepName()).append("</stepName>");
		buff.append("<dispensingPlates>");
		for (Iterator it = this.dispensingPlates.iterator(); it.hasNext();) {
			buff.append(((DispensingPlate) it.next()).toXml());
		}
		buff.append("</dispensingPlates>");
		buff.append("<synthesisPlates>");
		for (Iterator it = this.synthesisPlates.iterator(); it.hasNext();) {
			buff.append(((SynthesisPlate) it.next()).toXml());
		}
		buff.append("</synthesisPlates>");
		buff.append("<reagentsSolvents>");
		for (Iterator it = this.reagentsAndSolvents.iterator(); it.hasNext();) {
			buff.append(((ReagentSolvent) it.next()).toXml());
		}
		buff.append("</reagentsSolvents>");
		buff.append("</reactionStep>");
		return buff.toString();
	}	
}
