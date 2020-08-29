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
package com.chemistry.enotebook.experiment.datamodel.attachments;

public class AttachmentLaunchException extends Exception {
	
	private static final long serialVersionUID = 4090332299646074330L;

	public AttachmentLaunchException() {
		super();
	}

	public AttachmentLaunchException(Exception e) {
		super(e);
	}
	
	public AttachmentLaunchException(String msg) {
		super(msg);
	}

	public AttachmentLaunchException(String msg, Exception e) {
		super(msg, e);
	}
}
