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
package com.chemistry.enotebook.utils;

import foxtrot.Job;
import foxtrot.Worker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*
 * Created on May 16, 2005
 *
 * 
 */
/**
 * 
 * 
 * This Class is used to execute a method in a seperate Thread. And Updates the Status/Progressbar.
 */
public class CeNJob {
	private Method _methodToExecute;
	private Object[] _methodParamValues;
	private Object _source;
	private String _methodName;
	private String _jobDesc;
	private Object _returnObj;

	public synchronized static CeNJob getCeNJob() {
		return new CeNJob();
	}

	/**
	 * @param source
	 * @param jobDesc
	 * @param methodName
	 * @param methodParamTypes,
	 *            the Parameter Class Types, like String.class
	 * @param methodParamValues,
	 */
	public Object execute(final Object source, String jobDesc, String methodName, Class[] methodParamTypes,
			Object[] methodParamValues) {
		startProgressbar(jobDesc);
		_source = source;
		_methodName = methodName;
		_jobDesc = jobDesc;
		_methodParamValues = methodParamValues;
		Class cls = source.getClass();
		try {
			_methodToExecute = cls.getMethod(methodName, methodParamTypes);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}
		try {
			Worker.post(new Job() {
				public Object run() {
					try {
						_returnObj = _methodToExecute.invoke(source, _methodParamValues);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					return _returnObj;
				}
			});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			stopProgressbar(jobDesc);
		}
		return _returnObj;
	}

	/**
	 * @param message2
	 */
	private void stopProgressbar(String jobDesc) {
		CeNJobProgressHandler.getInstance().removeItem(jobDesc);
	}

	/**
	 * @param message2
	 */
	private void startProgressbar(String jobDesc) {
		CeNJobProgressHandler.getInstance().addItem(jobDesc);
	}
}
