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

public class ReactionSummary {
	private String reactionImageUri = "";

	public String getReactionImageUri() {
		return reactionImageUri;
	}

	public void setReactionImageUri(String reactionImageUri) {
		this.reactionImageUri = reactionImageUri;
	}
	
	public String toXml() {
		StringBuffer buff = new StringBuffer("<reactionSummary>");
		TextUtils.fillBufferWithClassMethods(buff, this);
		buff.append("</reactionSummary>");
		return buff.toString();
	}
}
