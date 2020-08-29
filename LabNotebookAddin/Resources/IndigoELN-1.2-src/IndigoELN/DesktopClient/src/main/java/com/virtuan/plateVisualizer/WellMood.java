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
package com.virtuan.plateVisualizer;

import com.chemistry.enotebook.client.gui.CeNGUIConstants;
import com.chemistry.enotebook.domain.*;

import java.awt.*;

/**
 * Title: WellMood Description: This class represents the well visual characteristics
 */
public class WellMood implements Cloneable {
	private String _name;
	private Stroke _rimWidth;
	private int _rimOffset;
	private int _rimDiameterIncrease;
	private Color _rimColor;
	private Color _startWellColor;
	private Color _endWellColor;
	private double _diameterOffset;
	private int _wellShape;
	public static int SHAPE_CIRCULAR = 1;
	public static int SHAPE_SQUARE = 2;

//	private Color C1 = new Color(84, 139, 93);// dark green -"Soluble - Continue" or "Passed - Continue" ==0
//	private Color C2 = Color.YELLOW;// yellow -"Unavailable - Discontinue" or "Failed - Discontinue" ==1
//	private Color C3 = new Color(93, 84, 139);// pink-"Passed and Registered - Continue" ==2
//
//	private Color C4 = new Color(255, 0, 0);// red - "Insoluble - Continue" or "Failed - Continue" ==3
//	private Color C5 = new Color(155, 0, 0);// dark red - "Insoluble - Discontinue" ==4
//	private Color C6 = new Color(0, 171, 255);// light blue - "Suspect - Continue" ==5
//
//	private Color C7 = Color.LIGHT_GRAY;// grey -"Empty Well" ==6
//	private Color C8 = new Color(0, 0, 255);// blue -"Suspect - Discontinue" ==7
//	private Color C9 = Color.WHITE;// white -"Not Made - Discontinue" ==8

	private Color[] wellColors = { CeNGUIConstants.LIGHT_GREEN, 
			CeNGUIConstants.YELLOW, 
			CeNGUIConstants.DARK_BLUE, 
			CeNGUIConstants.ORANGE, 
			CeNGUIConstants.LIGHT_RED, 
			CeNGUIConstants.LIGHT_BLUE, 
			CeNGUIConstants.LIGHT_GRAY,
			CeNGUIConstants.BLUE, 
			CeNGUIConstants.WHITE };
	private PlateWell userWellObject = null;

	/**
	 * paraterized constructor
	 * 
	 * @param rimWidth
	 * @param rimColor
	 * @param wellColor
	 */
	public WellMood(String name, Stroke rimWidth, Color rimColor, Color startWellColor, Color endWellColor, double diameterOffset,
			int wellShape, int rimOffset, int rimDiameterIncrease, PlateWell well) {
		_name = name;
		_rimWidth = rimWidth;
		_rimColor = rimColor;
		_startWellColor = startWellColor;
		_endWellColor = endWellColor;
		_diameterOffset = diameterOffset;
		_wellShape = wellShape;
		_rimOffset = rimOffset;
		_rimDiameterIncrease = rimDiameterIncrease;
		userWellObject = well;
	}

	public WellMood(String name, Stroke rimWidth, Color rimColor, Color startWellColor, Color endWellColor, double diameterOffset,
			int wellShape, int rimOffset, int rimDiameterIncrease) {
		_name = name;
		_rimWidth = rimWidth;
		_rimColor = rimColor;
		_startWellColor = startWellColor;
		_endWellColor = endWellColor;
		_diameterOffset = diameterOffset;
		_wellShape = wellShape;
		_rimOffset = rimOffset;
		_rimDiameterIncrease = rimDiameterIncrease;
		userWellObject = null;
	}

	/**
	 * method to return the well rim width
	 * 
	 * @return Stroke value of the wells rim width
	 */
	public Stroke getRimWidth() {
		return _rimWidth;
	}

	/**
	 * method to return the value of the rim's color
	 * 
	 * @return Color value of the rim
	 */
	public Color getRimColor() {
		return _rimColor;
	}

	/**
	 * method to return the start well color
	 * 
	 * @return Color value of the well
	 */
	public Color getStartWellColor() {
		// return _startWellColor;
		if (userWellObject != null) {
			BatchModel batchModel = userWellObject.getBatch();

			int status = 0;

			if (batchModel instanceof ProductBatchModel) {
				ProductBatchModel pBatch = (ProductBatchModel) batchModel;

				// pBatch.getSelectivityStatus()== CeNConstants.BATCH_STATUS_PASS
				// pBatch.getContinueStatus()==CeNConstants.BATCH_STATUS_CONTINUE

				if (pBatch.getSelectivityStatus() == CeNConstants.BATCH_STATUS_PASS && pBatch.getContinueStatus() == CeNConstants.BATCH_STATUS_CONTINUE) {
					status = 0;
				} else if (pBatch.getSelectivityStatus() == CeNConstants.BATCH_STATUS_PASS && pBatch.getContinueStatus() == CeNConstants.BATCH_STATUS_DISCONTINUE) {
					status = 5; // vb 7/11 doesn't exist
				} else if (pBatch.getSelectivityStatus() == CeNConstants.BATCH_STATUS_FAIL && pBatch.getContinueStatus() == CeNConstants.BATCH_STATUS_CONTINUE) {
					status = 3;
				} else if (pBatch.getSelectivityStatus() == CeNConstants.BATCH_STATUS_FAIL && pBatch.getContinueStatus() == CeNConstants.BATCH_STATUS_DISCONTINUE) {
					status = 4;
				} else if (pBatch.getSelectivityStatus() == CeNConstants.BATCH_STATUS_SUSPECT && pBatch.getContinueStatus() == CeNConstants.BATCH_STATUS_CONTINUE) {
					status = 0;
				} else if (pBatch.getSelectivityStatus() == CeNConstants.BATCH_STATUS_SUSPECT && pBatch.getContinueStatus() == CeNConstants.BATCH_STATUS_DISCONTINUE) {
					status = 1;
				} else if (pBatch.getSelectivityStatus() == CeNConstants.BATCH_STATUS_NOT_MADE && pBatch.getContinueStatus() == CeNConstants.BATCH_STATUS_DISCONTINUE) {
					status = 8;
				}

			} else if (batchModel instanceof MonomerBatchModel) {
				MonomerBatchModel mBatch = (MonomerBatchModel) batchModel;
				if (mBatch.getStatus() == null) // vb 8/21 this should be set!!!
					status = 0;
				else if (mBatch.getStatus().equalsIgnoreCase(CeNConstants.SOLUBLE_CONTINUE))
					status = 0;
				else if (mBatch.getStatus().equalsIgnoreCase(CeNConstants.INSOLUBLE_CONTINUE))
					status = 5;				
				else if (mBatch.getStatus().equalsIgnoreCase(CeNConstants.INSOLUBLE_DISCONTINUE))
					status = 3;		
				else if (mBatch.getStatus().equalsIgnoreCase(CeNConstants.UNAVAILABLE_DISCONTINUE))
					status = 4;		
				else
					status = 0;
			}

			return wellColors[status];
		}
		return wellColors[6];
	}

	/**
	 * method to return the end well color
	 * 
	 * @return Color value of the well
	 */
	public Color getEndWellColor() {
		return _endWellColor;
	}

	/**
	 * method to return the well diameter offset (this value is the amount increased or descreased of the standard well size
	 * 
	 * @return double value of the well diameter offset
	 */
	public double getDiameterOffset() {
		return _diameterOffset;
	}

	/**
	 * method for returning the well shape
	 * 
	 * @return int value of the well shape
	 */
	public int getWellShape() {
		return _wellShape;
	}

	/**
	 * method for returning the rim offset
	 * 
	 * @return int value of the rim offset
	 */
	public int getRimOffset() {
		return _rimOffset;
	}

	/**
	 * method for returning the rim diameter increase
	 * 
	 * @return int value of the rim diameter increase
	 */
	public int getRimDiameterIncrease() {
		return _rimDiameterIncrease;
	}

	/**
	 * method to return the name of the well mood
	 * 
	 * @return String value of the name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * method to set the well rim width
	 * 
	 * @param Stroke
	 *            value of the wells rim width
	 */
	public void setRimWidth(Stroke rimWidth) {
		_rimWidth = rimWidth;
	}

	/**
	 * method to set the value of the rim's color
	 * 
	 * @param Color
	 *            value of the rim
	 */
	public void setRimColor(Color rimColor) {
		_rimColor = rimColor;
	}

	/**
	 * method to set the start well color
	 * 
	 * @param Color
	 *            start value of the well
	 */
	public void setStartWellColor(Color startWellColor) {
		_startWellColor = startWellColor;
	}

	/**
	 * method to set the end well color
	 * 
	 * @param Color
	 *            value of the well
	 */
	public void setEndWellColor(Color endWellColor) {
		_endWellColor = endWellColor;
	}

	/**
	 * method to set the well diameter offset (this value is the amount increased or decreased of the standard well size
	 * 
	 * @return double value of the well diameter offset
	 */
	public void setDiameterOffset(double diameterOffset) {
		_diameterOffset = diameterOffset;
	}

	/**
	 * method to set the well shape
	 * 
	 * @return int value of the well shape
	 */
	public void setWellShape(int wellShape) {
		_wellShape = wellShape;
	}

	/**
	 * method for setting the rim offset
	 * 
	 * @return int value of the rim offset
	 */
	public void setRimOffset(int rimOffset) {
		_rimOffset = rimOffset;
	}

	/**
	 * method for returning the rim diameter increase
	 * 
	 * @return int value of the rim diameter increase
	 */
	public void setRimDiameterIncrease(int rimDiameterIncrease) {
		_rimDiameterIncrease = rimDiameterIncrease;
	}

	/**
	 * method to set the name of the well mood
	 * 
	 * @param name
	 */
	public void setName(String name) {
		_name = name;
	}

	/**
	 * method allowing users to clone the Well Mood
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public PlateWell getUserWellObject() {
		return userWellObject;
	}

	public void setUserWellObject(PlateWell userWellObject) {
		this.userWellObject = userWellObject;
	}
}
