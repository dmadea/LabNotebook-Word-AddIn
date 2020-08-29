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
package com.chemistry.enotebook.utils;

public class CompositeCompressedBytes implements java.io.Serializable {

	private static final long serialVersionUID = -823537411836563747L;
	
	private byte[] bytes;
	private int uncompressedSize;

	public CompositeCompressedBytes(byte[] bytes, int uncompressedSize) {
		this.bytes = bytes;
		this.uncompressedSize = uncompressedSize;
	}

	public int getUncompressedSize() {
		return uncompressedSize;
	}

	public byte[] getBytes() {
		return bytes;
	}
}
