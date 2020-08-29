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
package com.global.dri.compoundregistration.services.structure;

public interface StructureServices {

    /**
     * Performs a uniqueness check for the given structure and stereoisomer code.
     * Set <code>isPreload</code> to <code>true</code> to avoid sending email
     * to Curators for every hit if this is preload process.
     */
    //public StructureUcDTO executeUc(String molfile, String stereoisomerCode, boolean isPreload)
    //        throws RemoteException, StructureUCException;

    /**
     * Performs a uniqueness check for the given structure and stereoisomer code.
     * This is an overloaded method and sets <code>isPreload</code> to <code>false</code>.
     */
    public StructureUcDTO executeUc(String molfile, String stereoisomerCode)
            throws StructureUCException;

}
