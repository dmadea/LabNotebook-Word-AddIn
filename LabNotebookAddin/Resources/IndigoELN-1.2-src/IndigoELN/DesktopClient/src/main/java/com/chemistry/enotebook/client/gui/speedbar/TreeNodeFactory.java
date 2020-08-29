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
 * TreeNodeFactory.java
 * 
 * Originally written by Joseph Bowbeer and released into the public domain.
 * This may be used for any purposes whatsoever without acknowledgment.
 */
package com.chemistry.enotebook.client.gui.speedbar;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Remote model interface for dynamic node expansion.
 * 
 * 
 * @version 1.0
 */
public interface TreeNodeFactory {
	/**
	 * Creates child nodes for a newly-expanded parent node. Called on worker thread. The userObject parameter is the newly-expanded
	 * node's link back to the remote model. Initially, each child node is assigned an allowsChildren property and a link back to
	 * the remote model.
	 */
	DefaultMutableTreeNode[] createChildren(Object userObject) throws Exception;
}
