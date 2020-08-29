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

import com.ggasoftware.indigo.Indigo;
import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.IndigoRenderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

public class ChemistryPanel extends JPanel {

	private static final long serialVersionUID = 6879245830760407077L;
	
	private byte[] chemistry;
	private byte[] sketch;
	
	private Dimension dimension = null;
	private Image image;
	
	private boolean isImageCreated = false;
	
	public void setData(byte[] data) {
		if (Arrays.equals(chemistry, data) || Arrays.equals(sketch, data)) {
			return;
		}
		isImageCreated = false;
		chemistry = data;
		repaint();
	}
	
	public void setData(String data) {
		setData(data.getBytes());
	}
	
	public String getData() {
		return new String(chemistry);
	}
	
	@Override
	public Dimension getPreferredSize() {
		Container parent = getParent();
		Dimension size = parent.getSize();
		size.width -= 5;
		size.height -= 5;
		
		Dimension parentParentSize = parent.getParent().getSize();		
		int maxWidth = parentParentSize.width - 5;
		int maxHeight = parentParentSize.height - 5;
		
		if (size.width > maxWidth) {
			size.width = Math.max(0, maxWidth);
		}
		if (size.height > maxHeight) {
			size.height = Math.max(0, maxHeight);
		}

        if (size.width < 15) {
        	size.width = 400;
        }
        if (size.height < 15) {
        	size.height = 200;
        }
		return size;
	}
	
	@Override
	public void paint(Graphics graphics) {
		super.paintComponent(graphics);
		if (! isImageCreated) {
			createImage();
		}
		
		if (image != null) {
			if (isImageCreated && (dimension == null || !dimension.equals(getPreferredSize()))) {
				dimension = getPreferredSize();
				createImage();				
			}

			graphics.drawImage(image, 0, 0, null);
		}
	}
	
	private void createImage() {
		if (chemistry == null) {
			image = null;
			isImageCreated = false;
			return;
		}
		
		Indigo indigo = new Indigo();
		IndigoRenderer renderer = new IndigoRenderer(indigo);
		IndigoObject indigoObject = null;
		//indigo.setOption("allow-any-reaction", true);
		
		indigo.setOption("ignore-stereochemistry-errors", "true");
		
		if (new String(chemistry).contains("$RXN")) {
			indigoObject = indigo.loadQueryReaction(chemistry);
		} else {
			indigoObject = indigo.loadQueryMolecule(chemistry);
		}

		indigo.setOption("render-output-format", "png");
		indigo.setOption("render-bond-length", 30d);
		indigo.setOption("render-label-mode", "hetero");
		indigo.setOption("render-background-color", 255.0F, 255.0F, 255.0F);
		indigo.setOption("render-image-size", getWidth(), getHeight());
		
		byte[] imageBuffer = renderer.renderToBuffer(indigoObject);

		try {
			image = ImageIO.read(new ByteArrayInputStream(imageBuffer));
			isImageCreated = true;
		} catch (IOException e) {}
	}
	
	public void setMolfileData(String molFileData) {
		this.setData(molFileData);
	}
	
	public String getMolfileData() {
		return this.getData();
	}
}
