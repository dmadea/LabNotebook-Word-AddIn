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
package com.chemistry.enotebook.compoundregistration.classes;

import java.io.Serializable;
import java.sql.Timestamp;

public class CompoundRegistrationJobStatus implements Serializable {
	private static final long serialVersionUID = -4137627391303100900L;

	private Timestamp createDate;
	private String details;
	private String dryRun;
	private String fileName;
	private String jobId;
	private String numberDryPassed;
	private String numberFailed;
	private String numberPassed;
	private String percentComplete;
	private String provider;
	private String registrar;
	private Timestamp startTime;
	private String status;
	private Timestamp stopTime;
	private String totalCompounds;

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getDryRun() {
		return dryRun;
	}

	public void setDryRun(String dryRun) {
		this.dryRun = dryRun;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getNumberDryPassed() {
		return numberDryPassed;
	}

	public void setNumberDryPassed(String numberDryPassed) {
		this.numberDryPassed = numberDryPassed;
	}

	public String getNumberFailed() {
		return numberFailed;
	}

	public void setNumberFailed(String numberFailed) {
		this.numberFailed = numberFailed;
	}

	public String getNumberPassed() {
		return numberPassed;
	}

	public void setNumberPassed(String numberPassed) {
		this.numberPassed = numberPassed;
	}

	public String getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(String percentComplete) {
		this.percentComplete = percentComplete;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getRegistrar() {
		return registrar;
	}

	public void setRegistrar(String registrar) {
		this.registrar = registrar;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getStopTime() {
		return stopTime;
	}

	public void setStopTime(Timestamp stopTime) {
		this.stopTime = stopTime;
	}

	public String getTotalCompounds() {
		return totalCompounds;
	}

	public void setTotalCompounds(String totalCompounds) {
		this.totalCompounds = totalCompounds;
	}

	public CompoundRegistrationJobStatus() {

	}

	public String toXML() {
		return "";
	}
}
