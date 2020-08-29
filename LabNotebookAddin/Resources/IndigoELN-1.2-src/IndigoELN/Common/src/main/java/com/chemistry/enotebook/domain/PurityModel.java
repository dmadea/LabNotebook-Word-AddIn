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
package com.chemistry.enotebook.domain;

import com.chemistry.enotebook.experiment.common.units.UnitType;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class PurityModel extends GenericCodeModel{
	public static final long serialVersionUID = 7526472295622776147L;
//	 method = code = Ex: HPLC, LCMS, NMR, etc.
	// descr = What these methods mean.

	private String operator; // =, >, <, ~
	private AmountModel purityValue = new AmountModel(UnitType.SCALAR);
	private Date date = new Date(System.currentTimeMillis());
	private String comments="";
	private String sourceFile="";
	private boolean isRepresentativePurity;
	
	public PurityModel()
	{
		super();	
	}
	public PurityModel(String method, 
				       String description, 
				       String operator, 
				       AmountModel value, 
				       Date dateMeasured, 
				       String comments,
				       boolean isRepresentative,
				       String sourceFile) 
	{
		super(method,description);	
		this.operator = operator;
		purityValue = value;
		date = dateMeasured;
		this.comments = comments;
		this.isRepresentativePurity = isRepresentative;
		this.sourceFile = sourceFile;
	}
	
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isRepresentativePurity() {
		return isRepresentativePurity;
	}

	public void setRepresentativePurity(boolean isRepresentativePurity) {
		this.isRepresentativePurity = isRepresentativePurity;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public AmountModel getPurityValue() {
		return purityValue;
	}

	public void setPurityValue(AmountModel purityValue) {
		this.purityValue = purityValue;
	}

	public String getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * Uses HTML rendering for the new Lines
	 * 
	 * @param purities
	 * @return
	 */
	public static String toToolTipString(List purities) {
		StringBuffer result = new StringBuffer("<html><font name='Arial' size='3'>");
		if (purities != null || purities.size() > 0) {
			for (Iterator i = purities.iterator(); i.hasNext();) {
				result.append(((PurityModel) i.next()).toString() + "<br>");
			}
		}
		result.append("</font></html>");
		return result.toString();
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append(getCode() + " purity " + getOperator() + " " + getPurityValue().GetValueInStdUnitsAsDouble() + "% " + getComments());
		if (this.isRepresentativePurity) 
			buff.append(" (representative purity)");
		return buff.toString();
	}
	
	public String toXML(){
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append(super.toXML());
		xmlbuff.append(this.getPurityValue().toXMLWithStartingTag("Purity_Value"));
		xmlbuff.append("<Operator>");
		xmlbuff.append(escapePurityOperator(this.operator));
		xmlbuff.append("</Operator>");
		xmlbuff.append("<DateMeasured>");
		xmlbuff.append(this.getDate());
		xmlbuff.append("</DateMeasured>");
		xmlbuff.append("<SourceFile>");
		xmlbuff.append(this.getSourceFile());
		xmlbuff.append("</SourceFile>");
		xmlbuff.append("<IsRepresentativePurity>");
		xmlbuff.append(this.isRepresentativePurity());
		xmlbuff.append("</IsRepresentativePurity>");
		xmlbuff.append("<Comments>");
		xmlbuff.append(this.getComments());
		xmlbuff.append("</Comments>");
		
		return xmlbuff.toString();
	}
	
	private String escapePurityOperator(String voperator) {
		if (StringUtils.equals(voperator, ">")) {
			return "&gt;";
		} else if (StringUtils.equals(voperator, "<")) {
			return "&lt;";
		} else {
			return voperator;
		}
	}
	
	private String unescapePurityOperator(String unescpOperator)
	{

		if(unescpOperator.equals("&gt;"))
		{
		 return ">";	
		}else if(unescpOperator.equals("&lt;"))
		{
		return "<";	
		}else
		{
			return unescpOperator;
		}
	}
	
}
