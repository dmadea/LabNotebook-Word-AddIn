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
 */
package com.chemistry.enotebook.reagent.dao;

import com.chemistry.enotebook.reagent.common.DAO;


/**
 * 
 *
 */

public abstract class DAOFactory {
	
	//List of DAO types supported by the factory
	public static final int ORACLE = 1;
	public static final int SYSBASE = 2;
	public static final int SQLSERVER = 3;
	
	public abstract DAO createDao(String daoName);
	
	
	public static DAOFactory getDAOFactory(int whichFactory){
		switch(whichFactory){
			case ORACLE:
				return new OracleDAOFactory();
			//case SYSBASE
			//case SQLSERVER
			default:
				return null;
		}
	}

}
