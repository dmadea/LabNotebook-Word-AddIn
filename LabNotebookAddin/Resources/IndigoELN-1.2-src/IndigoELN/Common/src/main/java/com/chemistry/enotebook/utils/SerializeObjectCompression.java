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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SerializeObjectCompression {

	private static final Log log = LogFactory.getLog(SerializeObjectCompression.class);

	public static CompositeCompressedBytes serializeToCompressedBytes(Object object) {
		if (object == null)
			return null;

		long start = System.currentTimeMillis();
		
		CompositeCompressedBytes result = null;

		ByteArrayOutputStream objectStream = null;
		ObjectOutputStream objectWriter = null;

		ByteArrayOutputStream resultStream = null;
		GZIPOutputStream resultWriter = null;

		try {
			objectStream = new ByteArrayOutputStream();
			objectWriter = new ObjectOutputStream(objectStream);

			resultStream = new ByteArrayOutputStream();
			resultWriter = new GZIPOutputStream(resultStream);

			objectWriter.writeObject(object);
			resultWriter.write(objectStream.toByteArray(), 0, objectStream.size());

			resultWriter.close();
			
			result = new CompositeCompressedBytes(resultStream.toByteArray(), objectStream.size());
		} catch (Exception e) {
			log.error("Error compressing object: ", e);
		} finally {
			close(objectStream);
			close(objectWriter);
			close(resultStream);
			close(resultWriter);
		}

		long end = System.currentTimeMillis();
		
		log.debug("Compressing object " + object.getClass().getSimpleName() + " took " + (end - start) + "ms");
		
		return result;
	}

	public static Object convertCompressedBytesToObject(CompositeCompressedBytes bytes) throws Exception {
		if (bytes == null)
			return null;

		long start = System.currentTimeMillis();
		
		Object result = null;

		ByteArrayInputStream bytesStream = null;
		GZIPInputStream bytesReader = null;

		ByteArrayInputStream objectStream = null;
		ObjectInputStream objectReader = null;

		try {
			byte[] buf = new byte[bytes.getUncompressedSize()];

			bytesStream = new ByteArrayInputStream(bytes.getBytes());
			bytesReader = new GZIPInputStream(bytesStream);

			int offset = 0;
			int thisRead;
			while ((thisRead = bytesReader.read(buf, offset, buf.length - offset)) > 0) {
				offset += thisRead;
			}

			objectStream = new ByteArrayInputStream(buf);
			objectReader = new ObjectInputStream(objectStream);

			result = objectReader.readObject();
		} catch (Exception e) {
			throw new Exception("Error decompressing object: ", e);
		} finally {
			close(bytesStream);
			close(bytesReader);
			close(objectStream);
			close(objectReader);
		}

		long end = System.currentTimeMillis();
		
		log.debug("Decompressing object " + result.getClass().getSimpleName() + " took " + (end - start) + "ms");
		
		return result;
	}

	private static void close(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (Exception e) {
				log.error("Error closing stream: ", e);
			}
		}
	}
}
