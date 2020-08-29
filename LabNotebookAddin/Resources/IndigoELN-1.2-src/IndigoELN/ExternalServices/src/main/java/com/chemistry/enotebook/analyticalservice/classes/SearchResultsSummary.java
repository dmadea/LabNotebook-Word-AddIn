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
package com.chemistry.enotebook.analyticalservice.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchResultsSummary implements Serializable {
	private static final long serialVersionUID = -1403197200936317067L;
	protected List<SearchResult> results;
	
	public SearchResultsSummary(List<SearchResult> results) {
		this.results = results;
	}
	public int getNumberOfFilesMatchingQuery() {
		return (results != null) ? results.size() : 0;
	}
    public Iterator<SearchResult> searchResultFiles() {
    	return (results != null) ? results.iterator() : new ArrayList<SearchResult>().iterator();
    }
}
