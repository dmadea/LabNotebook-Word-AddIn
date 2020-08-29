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

package com.chemistry.enotebook.storage.dao;

import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.searchquery.SearchQueryParams;
import com.chemistry.enotebook.storage.jdbc.select.NotebookPageSummarySelect;
import com.chemistry.enotebook.storage.query.SearchQueryGenerator;

import java.util.ArrayList;
import java.util.List;

public class NotebookSearchDAO extends StorageDAO {

	public List<NotebookPageModel> getSearchedNotebookPages(SearchQueryParams params, List<String> nbkRefPages) throws Exception {
		List<NotebookPageModel> result = new ArrayList<NotebookPageModel>();
		
		StringBuilder nbkPages = new StringBuilder();

		for (int i = 0; i < nbkRefPages.size(); ++i) {
			if (i > 0)
				nbkPages.append(",");
			nbkPages.append("'");
			nbkPages.append(nbkRefPages.get(i));
			nbkPages.append("'");
		}

		String query = SearchQueryGenerator.getNotebookSearchQuery(params, params.getCssCenDbSearchType(), nbkPages.toString());
		NotebookPageSummarySelect searcher = new NotebookPageSummarySelect(getDataSource(), query);

		result = searcher.execute();
		
		return result;
	}
}
