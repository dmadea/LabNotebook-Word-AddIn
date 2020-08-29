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
package com.chemistry.enotebook.storage.tests;

import com.chemistry.enotebook.domain.AttachmentModel;
import com.chemistry.enotebook.storage.dao.AttachmentDAO;
import com.chemistry.enotebook.storage.dao.DAOFactory;
import com.chemistry.enotebook.storage.dao.DAOFactoryManager;

import java.util.Date;
import java.util.List;

public class TestAttachmentDAO {
	
	public static void loadAttachment() throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		AttachmentDAO dao =  factory.getAttachmentDAO();
		//load attachments for the page key
		List list = dao.loadAttachment("ff8a400794a842b240c420fa335864506e613bd9");
		System.out.println("Loaded AttachmentList size:"+list.size());
	}
	
	
	public static void insertAttachment() throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		AttachmentDAO dao =  factory.getAttachmentDAO();
//		load attachments for the page key
		List list = dao.loadAttachment("ff8a400794a842b240c420fa335864506e613bd9");
		System.out.println("Loaded AttachmentList size:"+list.size());
		for(int i=0; i<list.size();i++){
			AttachmentModel attachment = (AttachmentModel)list.get(i);
			attachment.setTempKey(attachment.getKey()+new Date().getTime());
		}
//		insert attachments for the page key
		dao.insertAttachmentList(list, "753059d5ac1ebb8244c8aeb3b3718f013113c4b9");
		System.out.println("Insert Attachment success");
	}
	
	

	public static void updateAttachment() throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		AttachmentDAO dao =  factory.getAttachmentDAO();
		List list = dao.loadAttachment("ff8a400794a842b240c420fa335864506e613bd9");
		System.out.println("Loaded AttachmentList size:"+list.size());
		AttachmentModel model =(AttachmentModel)list.get(0);
		model.setDocumentName("Testdocument for test");
		//model.setToDelete(true);
		//model.setLoadedFromDB(false);
		//model.setTempKey(GUIDUtil.generateGUID(model));
		dao.updateAttachmentList(list,"753059d5ac1ebb8244c8aeb3b3718f013113c4b9");
		System.out.println("Update Attachment success");
	}
	
	public static void main(String[] args) throws Exception{
		//loadAttachment();
		//insertAttachment();
		updateAttachment();
	}
}
