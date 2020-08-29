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
 * Created on Mar 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class Unzipper {
	protected static final int BUFLEN = 32768;

	public Unzipper() {
	}

	public void unzip(final String filename, final String destpath) throws IOException {
		ZipInputStream zis = new ZipInputStream(new FileInputStream(filename));
		File fout;
		File fdir;
		FileOutputStream fos;
		try {
			for (ZipEntry zentry = zis.getNextEntry(); zentry != null; zentry = zis.getNextEntry()) {
				try {
					fout = new File(destpath, zentry.getName());
					if (zentry.isDirectory() == true) {
						fout.mkdirs();
					} else {
						fdir = new File(fout.getParent());
						fdir.mkdirs();
						fout.createNewFile();
						fos = new FileOutputStream(fout);
						byte[] buffer = new byte[BUFLEN];
						int count = 0;
						try {
							do {
								count = zis.read(buffer, 0, BUFLEN);
								if (count > -1) {
									fos.write(buffer, 0, count);
								}
							} while (count > -1);
						} finally {
							fos.close();
						}
					}
				} finally {
					zis.closeEntry();
				}
			}
		} finally {
			zis.close();
		}
	}
}
