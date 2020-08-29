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
package com.chemistry.enotebook.storage.tests;

import com.chemistry.enotebook.storage.dao.DAOFactory;
import com.chemistry.enotebook.storage.dao.DAOFactoryManager;
import com.chemistry.enotebook.storage.dao.NotebookRemoveDAO;

public class TestNotebookRemoveDAO {

	public static void testRemoveExperiment(String pageKey)throws Exception {
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookRemoveDAO dao =  factory.getNotebookRemoveDAO();
		long start = System.currentTimeMillis();
		dao.removeExperiment(pageKey);
		System.out.println("Time taken  :"+(System.currentTimeMillis()-start));
	}
	
	public static void main(String[] str)throws Exception{
		TestNotebookRemoveDAO.testRemoveExperiment("9564f281ac1eb85901da1c9d85888080e3270714");
	}
}
