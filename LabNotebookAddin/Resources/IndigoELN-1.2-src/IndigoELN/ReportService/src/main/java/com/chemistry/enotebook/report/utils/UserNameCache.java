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
package com.chemistry.enotebook.report.utils;

import com.chemistry.enotebook.report.datamanager.ExperimentLoader;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;

import java.util.HashMap;
import java.util.Map;

public class UserNameCache {

	private static Map userNameToFullNameMap = new HashMap();
	
	public static String[] getUsersFullName(String[] userIDs) {
		if (userIDs == null || userIDs.length == 0 || userIDs[0] == null || userIDs[0].length() == 0)
			return null;
		String[] result = new String[userIDs.length];
		try {
			String tStr;
			boolean unknownflag = false;
			for (int i = 0; i < userIDs.length && !unknownflag; i++) {
				tStr = (String) userNameToFullNameMap.get(userIDs[i]);
				if (tStr != null && tStr.length() > 0)
					result[i] = tStr;
				else
					unknownflag = true;
			}
			if (unknownflag) {
				StorageDelegate sd = ExperimentLoader.getStorageDelegate();
				result = sd.getUsersFullName(userIDs);
				if (result != null) {
					for (int i = 0; i < result.length; i++) {
						if (result[i] != null && result[i].length() > 0 && !userNameToFullNameMap.containsKey(userIDs[i]))
							userNameToFullNameMap.put(userIDs[i], result[i]);
					}
				}
			}
		} catch (Exception e) {
			//CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			e.printStackTrace();
		}
		return result;
	}
}
