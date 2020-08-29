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
package com.chemistry.enotebook.domain;

import com.chemistry.enotebook.experiment.datamodel.attachments.Attachment;

import java.util.ArrayList;
import java.util.List;

public class AttachmentCacheModel extends CeNAbstractModel{
	public static final long serialVersionUID = -7880435723238046973L;

	private ArrayList<AttachmentModel> attachmentList = new ArrayList<AttachmentModel>();

	public AttachmentCacheModel() {
	
	}
	
	/**
	 * Construct an analysisCache with the analytical models passed in the Map object.
	 * 
	 * @param allanalyticals
	 *            contains object of the same type: AttachmentModel
	 */
	public AttachmentCacheModel(List<AttachmentModel> allAttachments) {
		this();
		attachmentList.addAll(allAttachments);
	}
	
	/**
	 * @param attachment
	 */
	public void addNewAttachment(AttachmentModel attachment){
		if(attachment != null)
			this.attachmentList.add(attachment);
		setModelChanged(true);
	}
	
	public AttachmentModel getAttachment(int rowNumber){
		return (AttachmentModel)this.attachmentList.get(rowNumber);
	}
	


	/**
	 * @return the attachmentList
	 */
	public ArrayList<AttachmentModel> getAttachmentList() {
		return attachmentList;
	}

	/**
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(ArrayList<AttachmentModel> attachmentList) {
		this.attachmentList = attachmentList;
		setModelChanged(true);
	}

	public boolean hasAttachment(Attachment attachment) {
		// checks to see if attachment exists in map
		return attachmentList.contains(attachment);
	}
	
	
	public AttachmentCacheModel getClonedAttachmentModelsForInsertAndUpdate()
	 {
		 AttachmentCacheModel clonedCache = new AttachmentCacheModel();
		 ArrayList<AttachmentModel> newlist = new ArrayList<AttachmentModel>();
		 
		 int size = this.attachmentList.size();
		 for(int i = 0; i< size ; i ++)
		 {
			AttachmentModel model = (AttachmentModel) this.attachmentList.get(i);
			
			if(!model.isLoadedFromDB() && model.isSetToDelete()) continue;
			
			if(model.isModelChanged())
			{
				AttachmentModel clonedmodel = (AttachmentModel)model.deepClone();
				newlist.add(clonedmodel);
			}
		 }
		 clonedCache.setAttachmentList(newlist);
		 return clonedCache;
	 }
	public String toXML(){
		return CeNConstants.XML_VERSION_TAG;
	}

	public AttachmentCacheModel deepCopy() {
		 AttachmentCacheModel copyCache = new AttachmentCacheModel();
		 ArrayList<AttachmentModel> newlist = new ArrayList<AttachmentModel>();
		 
		 int size = this.attachmentList.size();
		 for(int i = 0; i< size ; i ++)
		 {
			AttachmentModel model = (AttachmentModel) this.attachmentList.get(i);
			AttachmentModel copyModel = new AttachmentModel();  
			copyModel.deepCopy(model);
			newlist.add(copyModel);
		 }
		 copyCache.setAttachmentList(newlist);
		 return copyCache;
	}
	
}
