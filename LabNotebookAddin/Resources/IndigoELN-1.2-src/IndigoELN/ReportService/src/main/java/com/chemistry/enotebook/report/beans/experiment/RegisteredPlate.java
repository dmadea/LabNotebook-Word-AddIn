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

import com.chemistry.enotebook.report.utils.TextUtils;

import java.util.*;

public class RegisteredPlate {

	private String plateKey = "";
	private String barcode = "";
	private String name = "";
	private Map<String, ScreenSubmittalSet> submittalSets = new HashMap<String, ScreenSubmittalSet>();
	//private ScreenSubmissions screenSubmissions = new ScreenSubmissions();
	private PurificationServiceSubmissions purificationServiceSubmissions = new PurificationServiceSubmissions();
	
	public String getPlateKey() {
		return plateKey;
	}
	public void setPlateKey(String plateKey) {
		this.plateKey = plateKey;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public PurificationServiceSubmissions getPurificationServiceSubmissions() {
		return purificationServiceSubmissions;
	}
	public void setPurificationServiceSubmissions(PurificationServiceSubmissions purificationServiceSubmissions) {
		this.purificationServiceSubmissions = purificationServiceSubmissions;
	}
	public Map<String, ScreenSubmittalSet> getSubmittalSets() {
		return submittalSets;
	}
	public void setSubmittalSets(Map<String, ScreenSubmittalSet> submittalSets) {
		this.submittalSets = submittalSets;
	}
	public String toXml() {
		StringBuffer buff = new StringBuffer("<registeredPlate>");
//		try {
//			Class c = Class.forName(this.getClass().getName());
//			Object me = this;
//			Method[] allMethods = c.getDeclaredMethods();
//			for (int i=0; i<allMethods.length; i++) {
//				Method method = allMethods[i];
//				String methodName = method.getName();
//				if (methodName.startsWith("get")) {
//					String fieldName = methodName.substring(3);
//					if (fieldName.equalsIgnoreCase("submittalSets")
//							|| fieldName.equalsIgnoreCase("purificationServiceSubmissions"))
//						continue;
//					buff.append("<").append(fieldName).append(">");
//					try {
//						buff.append((method.invoke(me, (Object[])null)).toString());
//					} catch (RuntimeException e) {
//						buff.append("");
//					}
//					buff.append("</").append(fieldName).append(">");
//				}
//			}
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		List<String> listMethodNamesToSkip = new ArrayList<String>();
		listMethodNamesToSkip.add("submittalSets");
		listMethodNamesToSkip.add("purificationServiceSubmissions");
		TextUtils.fillBufferWithClassMethods(buff, this, false, listMethodNamesToSkip);
		fillBufferWithSubmittalSetXml(buff);
		buff.append(purificationServiceSubmissions.toXml());
		buff.append("</registeredPlate>");
		return buff.toString();
	}
	
	private void fillBufferWithSubmittalSetXml(StringBuffer buff) {
		// Get the registration screening and purificationService results
		buff.append("<screenSubmittalSets>");
		for (Iterator<?> it = this.submittalSets.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			ScreenSubmittalSet sset = (ScreenSubmittalSet) this.submittalSets.get(key);
			buff.append(sset.toString());
		}
		buff.append("</screenSubmittalSets>");
	}
}
