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

import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.dao.DAOFactory;
import com.chemistry.enotebook.storage.dao.DAOFactoryManager;
import com.chemistry.enotebook.storage.dao.NotebookDAO;

public class TestNotebookDAO {

	public static void testCreateNotebook() throws DAOException{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookDAO dao = factory.getNotebookDAO();
		dao.createNotebook("SITE1", "USER", "87654321", "USER");
		DAOFactoryManager.release(factory);
	}
	
	public static void main(String[] args) throws Exception{
		testCreateNotebook();
	}
}
