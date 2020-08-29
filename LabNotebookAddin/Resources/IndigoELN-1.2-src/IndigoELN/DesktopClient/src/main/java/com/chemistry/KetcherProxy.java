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

package com.chemistry;

import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.util.KetcherHttpServer;
import org.apache.commons.codec.digest.DigestUtils;

import java.awt.*;
import java.net.URI;

public class KetcherProxy extends ChemistryEditorProxy {
	private byte[] chemistryBytes;
	
	@Override
	public byte[] getChemistry(int format) {
		if ((format == MOL_FORMAT) || (format == RXN_FORMAT)) {
			return chemistryBytes;
		} else {
			throw new RuntimeException("Unsupported format!");
		}
	}

	@Override
	protected boolean assignChemistry(byte[] chemistry, int format) {
		if ((format == MOL_FORMAT) || (format == RXN_FORMAT)) {
			chemistryBytes = chemistry;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Calls editor and waits for editing finish.
	 * @return true, if object was modified
	 *         false, if object wasn't modified
	 */
	@Override
	protected boolean callEditor() {
		try {
			if (getEditComponent() != null)
				CeNGUIUtils.findWindow(getEditComponent()).setEnabled(false);
			
			KetcherHttpServer.run();
			KetcherHttpServer ketcherHttpServer = KetcherHttpServer.getInstance();
			ketcherHttpServer.setKetcherProxy(this);
			
			String id = DigestUtils.md5Hex(this.toString());
			
			Desktop.getDesktop().browse(new URI("http://localhost:34851/?id=" + id));
		} catch (Exception e) {
			if (getEditComponent() != null)
				CeNGUIUtils.findWindow(getEditComponent()).setEnabled(true);
		}
		return false;
	}
}
