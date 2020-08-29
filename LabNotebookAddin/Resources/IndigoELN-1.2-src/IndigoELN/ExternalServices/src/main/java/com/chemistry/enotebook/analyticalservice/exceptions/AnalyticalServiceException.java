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
package com.chemistry.enotebook.analyticalservice.exceptions;
/**
 * USed to throw the Exception message while accessing the AnalyticalService service
 */
public class AnalyticalServiceException 
	extends java.lang.Exception
{
    static final long serialVersionUID = 2153777739330745090L;    
    
    public AnalyticalServiceException()
    {
        super();
    }
    
    public AnalyticalServiceException(Exception e)
    {
    	super(e);
    }
    
    public AnalyticalServiceException(Throwable t)
    {
    	super(t);
    }
    
    public AnalyticalServiceException(String msg)
    {
        super(msg);
    }

	public AnalyticalServiceException(String msg, Exception e)
	{
		super(msg, e);
	}

	public AnalyticalServiceException(String msg, Throwable t)
    {
    	super(msg, t);
    }
}