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
package com.chemistry.enotebook.utils.jtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Color;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ExcelWriter {

	private static final Log log = LogFactory.getLog(ExcelWriter.class);
	
	private OutputStream out;
	
	private Workbook workbook;
	private Sheet sheet;
	private Drawing drawing;
	
	private CellStyle plainCellStyle;
	private CellStyle boldCellStyle;
	
	private Font plainFont;
	private Font boldFont;
	
	private int row = 0;
	private int col = 0;
	private int maxCol = 0;
	private int maxWidth = 0;
		
	private Map<Point, ImageIcon> pictures;
	
	public ExcelWriter(OutputStream out) {
		this(out, false);
	}
	
	public ExcelWriter(OutputStream out, boolean xlsx) {
		this.out = out;
		init(xlsx);
	}
		
	public void addHeaderCell(Object data) {
		addCell(data, true);
	}
	
	public void addCell(Object data) {
		addCell(data, false);
	}
	
	public void newLine() {
		++row;
		maxCol = col;
		col = 0;
	}
	
	public void finish() throws IOException {
		for (int i = 0; i <= maxCol; ++i) {
			sheet.autoSizeColumn(i);
		}		
		
		addPictures();
		
		workbook.write(out);
	}

	private void init(boolean xlsx) {
		if (xlsx) {
			workbook = new XSSFWorkbook();
		} else {
			workbook = new HSSFWorkbook();
		}
		
		sheet = workbook.createSheet("Data export");
		drawing = sheet.createDrawingPatriarch();
		
		pictures = new HashMap<Point, ImageIcon>();
		
		plainCellStyle = workbook.createCellStyle();
		boldCellStyle = workbook.createCellStyle();
		
		plainFont = workbook.createFont();
		
		boldFont = workbook.createFont();
		boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		
		plainCellStyle.setFont(plainFont);
		boldCellStyle.setFont(boldFont);
	}

	private void addCell(Object data, boolean isHeader) {
		Cell currentCell = getCurrentCell();
		
		if (isHeader) {
			currentCell.setCellStyle(boldCellStyle);
		} else {
			currentCell.setCellStyle(plainCellStyle);
		}
		
		currentCell.getCellStyle().setBorderLeft(CellStyle.BORDER_THIN);
		currentCell.getCellStyle().setBorderRight(CellStyle.BORDER_THIN);
		currentCell.getCellStyle().setBorderTop(CellStyle.BORDER_THIN);
		currentCell.getCellStyle().setBorderBottom(CellStyle.BORDER_THIN);
		
		currentCell.getCellStyle().setVerticalAlignment(CellStyle.VERTICAL_TOP);
		currentCell.getCellStyle().setWrapText(true);
		
		if (data instanceof ImageIcon) {
			pictures.put(new Point(col, row), (ImageIcon) data);
		} else {
			currentCell.setCellValue(data.toString());
		}
		
		++col;
	}

	private void addPictures() {
		for (Point p : pictures.keySet()) {
			addImage(pictures.get(p), p.y, p.x);
		}
	}
	
	private void addImage(ImageIcon image, int pRow, int pCol) {
		if (image != null) {
			int height = image.getIconHeight();
			int width = image.getIconWidth();
		
			if (height < 1 || width < 1) {
				return;
			}
		
			if (width > maxWidth) {
				maxWidth = width;
			}
			
			byte[] data = null;
			
			try {
				data = getImageData(image);
			} catch (Throwable e) {
				log.error("Cannot get Image data: ", e);
			}
			
			if (data != null) {
				Row neededRow = sheet.getRow(pRow);
				
				if (neededRow != null) {
					neededRow.setHeightInPoints(getHeightInPoints(height));
				}

				sheet.setColumnWidth(pCol, getWidthInExcelUnits(maxWidth));
				
				ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
				
				anchor.setRow1(pRow);
				anchor.setCol1(pCol);
				
				Picture picture = drawing.createPicture(anchor, workbook.addPicture(data, Workbook.PICTURE_TYPE_JPEG));
				picture.resize();
			}
		}
	}

	private byte[] getImageData(ImageIcon image) throws IOException {
		int width = image.getIconWidth();
		int height = image.getIconHeight();
		
		if (height < 1 || width < 1) {
			return null;
		}
		
		BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		Graphics2D gr = bufImg.createGraphics();
		gr.drawImage(image.getImage(), 0, 0, image.getImageObserver());
		
		// FIXME Draw top and left border to be beautiful in Excel Cell
		gr.setColor(Color.BLACK);
		gr.drawLine(0, 0, width, 0);
		gr.drawLine(0, 0, 0, height);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		ImageIO.write(bufImg, "jpeg", baos);
		
		byte[] result = baos.toByteArray();
		baos.close();
		
		return result;
	}

	private float getHeightInPoints(int height) {
		// FIXME Here should be more suitable conversion from Pixels to Points
		return (float) (((float) (height)) * 0.75);
	}
		
	private int getWidthInExcelUnits(int width) {
		// FIXME Here should be more suitable conversion from Pixels to Excel Units
		Font font = workbook.getFontAt((short) 0);
		java.awt.Font ft = new java.awt.Font(font.getFontName(), java.awt.Font.PLAIN, font.getFontHeightInPoints());
		java.awt.FontMetrics metrics = new java.awt.Container().getFontMetrics(ft);
		int maxWidth = -1;
		for (int i = 0; i < 10; ++i) {
			int charWidth = metrics.charWidth(Character.forDigit(i, 10));
			if (charWidth > maxWidth) {
				maxWidth = charWidth;
			}
		}
		return (int) (width / maxWidth * 256 * 0.9);
	}
	
	private Row getCurrentRow() {
		Row currentRow = sheet.getRow(row);
		
		if (currentRow == null) {
			currentRow = sheet.createRow(row);
		}
		
		return currentRow;
	}
	
	private Cell getCurrentCell() {
		Row currentRow = getCurrentRow();
		Cell currentCell = currentRow.getCell(col);
		
		if (currentCell == null) {
			currentCell = currentRow.createCell(col);
		}
		
		return currentCell;
	}
}
