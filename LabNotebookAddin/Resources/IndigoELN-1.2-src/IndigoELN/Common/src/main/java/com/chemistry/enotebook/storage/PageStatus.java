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
/*
 * Created on 25-Oct-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.storage;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class PageStatus {
	// The typesafe enum pattern
	private final int ordinal;
	private final String status;

	private static final int OPEN_ORDINAL = 0;
	private static final int COMPLETE_ORDINAL = 1;
	private static final int SUBMITTED_ORDINAL = 2;
	private static final int SUBMIT_FAIL_ORDINAL = 3;
	private static final int SIGNED_ORDINAL = 4;
	private static final int ARCHIVING_ORDINAL = 5;
	private static final int ARCHIVE_FAIL_ORDINAL = 6;
	private static final int ARCHIVED_ORDINAL = 7;

	private PageStatus(String strStatus, int ordinal) {
		this.ordinal = ordinal;
		this.status = strStatus;
	}

	public int getValue() {
		return ordinal;
	}

	public String toString() {
		return status;
	}

	public static final PageStatus OPEN = new PageStatus("OPEN", OPEN_ORDINAL);
	public static final PageStatus COMPLETE = new PageStatus("COMPLETE", COMPLETE_ORDINAL);
	public static final PageStatus SUBMITTED = new PageStatus("SUBMITTED", SUBMITTED_ORDINAL);
	public static final PageStatus SUBMIT_FAILED = new PageStatus("SUBMIT_FAILED", SUBMIT_FAIL_ORDINAL);
	public static final PageStatus SIGNED = new PageStatus("SIGNED", SIGNED_ORDINAL);
	public static final PageStatus ARCHIVING = new PageStatus("ARCHIVING", ARCHIVING_ORDINAL);
	public static final PageStatus ARCHIVE_FAILED = new PageStatus("ARCHIVE_FAILED", ARCHIVE_FAIL_ORDINAL);
	public static final PageStatus ARCHIVED = new PageStatus("ARCHIVED", ARCHIVED_ORDINAL);

	private static final PageStatus[] VALS = { OPEN, COMPLETE, SUBMITTED, SUBMIT_FAILED, SIGNED, ARCHIVING, ARCHIVE_FAILED,
			ARCHIVED };

	public static final List VALUES = Arrays.asList(VALS);
}
