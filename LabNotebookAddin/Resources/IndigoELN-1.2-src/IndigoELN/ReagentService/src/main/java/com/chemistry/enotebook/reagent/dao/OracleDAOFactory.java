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
 * Created on Jul 12, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.reagent.dao;

import com.chemistry.enotebook.reagent.common.DAO;

public class OracleDAOFactory extends DAOFactory {

    public DAO createDao(String daoName) {
        if (daoName.equals("StructureDao")) {
            return new StructureDao();
        } else if (daoName.equals("TextSearchDao")) {
            return new TextSearchDao();
        } else if (daoName.equals("TextStructureDao")) {
            return new TextStructureDao();
        } else if (daoName.equals("MyReagentsDao")) {
            return new MyReagentsDao();
        } else if (daoName.equals("ReagentDBXMLDao")) {
            return new ReagentDBXMLDao();
        }
        return null;
    }
}
