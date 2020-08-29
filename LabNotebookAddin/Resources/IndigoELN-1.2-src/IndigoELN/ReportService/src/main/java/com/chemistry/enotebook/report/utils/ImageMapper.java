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
package com.chemistry.enotebook.report.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageMapper {

	private static final Log log = LogFactory.getLog(ImageMapper.class);
	
	public static final String EMPTY_IMAGE = "emptyImage";
	public static final String JPG = "jpg";

	private static final int emptyImageSize = 16;
	private static byte[] emptyImage;
	
	private ImageMapper() {
	}

	public static byte[] getBarcodeImage(String barcode) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
            BitmapCanvasProvider provider = new BitmapCanvasProvider(baos, "image/jpeg", 120, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            new Code128Bean().generateBarcode(provider, barcode);
            provider.finish();

            return baos.toByteArray();
		} catch (Exception e) {
			log.error("Cannot create image for barcode '" + barcode + "': ", e);
		} finally {
            try { baos.close(); } catch (IOException ignored) {}
        }

		return null;
	}
    
	public static byte[] getEmptyImage() {
		if (emptyImage == null) {
			emptyImage = generateEmptyImage();
		}
		return emptyImage;
	}
	
	private static byte[] generateEmptyImage() {
		byte[] result = null;

		try {
			BufferedImage image = new BufferedImage(emptyImageSize, emptyImageSize, BufferedImage.TYPE_INT_RGB);
			for (int i = 0; i < emptyImageSize; ++i)
				for (int j = 0; j < emptyImageSize; ++j)
					image.setRGB(i, j, Color.WHITE.getRGB());
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, JPG, os);
			
			result = os.toByteArray();
		} catch (Exception e) {
			log.error("Cannot process empty image: ", e);
		}
		
		return result;
	}
}
