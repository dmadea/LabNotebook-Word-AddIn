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
 * Created on 17-Jan-2005
 */
package com.chemistry.enotebook.client.gui.page.procedure;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * 
 */
public class cenURLStreamHandler extends URLStreamHandler implements URLStreamHandlerFactory {
	/**
	 * 
	 */
	public cenURLStreamHandler() {
		super();
		// TODO Auto-generated constructor stub
	}

	public cenURLStreamHandler(String str) {
		super();
		this.createURLStreamHandler(str);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.URLStreamHandler#openConnection(java.net.URL)
	 */
	protected URLConnection openConnection(URL arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.URLStreamHandlerFactory#createURLStreamHandler(java.lang.String)
	 */
	public URLStreamHandler createURLStreamHandler(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
