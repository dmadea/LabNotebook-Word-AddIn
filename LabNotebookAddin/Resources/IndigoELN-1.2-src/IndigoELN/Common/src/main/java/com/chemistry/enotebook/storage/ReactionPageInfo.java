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
 * Created on Jan 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.storage;

import java.io.Serializable;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ReactionPageInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String noteBookExperiment;
	private String version;
	private String pageInfo;
	private String pageType;
	private byte[] reactionSketch;
	private byte[] reactionImage;
	private String username;
	private String sitecode;
	private String reactionSchemeKey;
	private int width = 500;
	private int height = 200;
	
	public ReactionPageInfo(int width, int height) {
		this.setWidth(width);
		this.setHeight(height);
	}
	
	public ReactionPageInfo() {
		noteBookExperiment = "";
		pageInfo = "";
		reactionSketch = null;
		username = "";
		sitecode = "";
		version = "0";
	}

	/**
	 * @return Returns the noteBookExperiment.
	 */
	public String getNoteBookExperiment() {
		return noteBookExperiment;
	}

	/**
	 * @param noteBookExperiment
	 *            The noteBookExperiment to set.
	 */
	public void setNoteBookExperiment(String noteBookExperiment) {
		this.noteBookExperiment = noteBookExperiment;
	}

	/**
	 * @return Returns the version.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            The version to set.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return Returns the pageInfo.
	 */
	public String getPageInfo() {
		return pageInfo;
	}

	/**
	 * @param pageInfo
	 *            The pageInfo to set.
	 */
	public void setPageInfo(String pageInfo) {
		this.pageInfo = pageInfo;
	}

	/**
	 * @return Returns the sitecode.
	 */
	public String getSitecode() {
		return sitecode;
	}

	/**
	 * @param sitecode
	 *            The sitecode to set.
	 */
	public void setSitecode(String sitecode) {
		this.sitecode = sitecode;
	}

	/**
	 * @return Returns the username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return Returns the reactionSketch.
	 */
	public byte[] getReactionSketch() {
		return reactionSketch;
	}

	/**
	 * @param reactionSketch
	 *            The reactionSketch to set.
	 */
	public void setReactionSketch(byte[] reactionSketch) {
		this.reactionSketch = reactionSketch;
	}
	
	/**
	 * @return Returns the pageType.
	 */
	public String getPageType() {
		return pageType;
	}

	/**
	 * @param pageType (MED_CHEM, PARALLEL, CONCEPTION)
	 *            
	 */
	public void setPageType(String pageType) {
		this.pageType = pageType;
	}
	
	/**
	 * @return Returns the reactionImage.
	 */
	public byte[] getReactionImage() {
		return reactionImage;
	}

	/**
	 * @param reactionSketch
	 *            The reactionImage to set.
	 */
	public void setReactionImage(byte[] reactionImage) {
		this.reactionImage = reactionImage;
	}
	
	public void setReactionSchemeKey(String reactionSchemeKey) {
		this.reactionSchemeKey = reactionSchemeKey;
	}

	public String getReactionSchemeKey() {
		return reactionSchemeKey;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}
}
