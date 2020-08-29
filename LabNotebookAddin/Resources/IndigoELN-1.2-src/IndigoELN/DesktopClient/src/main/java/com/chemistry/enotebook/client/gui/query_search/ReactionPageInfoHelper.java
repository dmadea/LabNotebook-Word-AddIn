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
package com.chemistry.enotebook.client.gui.query_search;

import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.sdk.PictureProperties;
import com.chemistry.enotebook.storage.ReactionPageInfo;
import com.chemistry.enotebook.utils.Decoder;
import com.chemistry.viewer.ReactionViewer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class ReactionPageInfoHelper {
	private static final Log log = LogFactory.getLog(ReactionPageInfoHelper.class);
	
	private static ReactionViewer reactionViewer = null;
	private static Frame auxiliaryFrame = null;
	private static ChemistryPanel cps = null;
	
	private ReactionPageInfo reactionPageInfo;
	
	public ReactionPageInfoHelper(ReactionPageInfo reactionPageInfo) {
		this.reactionPageInfo = reactionPageInfo;
		
		try {
			getReactionViewer().setEditorType(MasterController.getGuiController().getDrawingTool());
			
//			ImageData imagedata = getReactionViewer().getWriteOptions();
//			imagedata.setWidth(reactionPageInfo.getWidth());
//			imagedata.setHeight(reactionPageInfo.getHeight());
//			
//			getReactionViewer().setWriteOptions(imagedata);
		} catch (Exception e) {
			log.error("Failed to initialize ReactionViewer", e);
		}
	}
	
	private static ReactionViewer getReactionViewer() throws Exception {
		if (reactionViewer == null) {
			reactionViewer = new ReactionViewer(MasterController.getGUIComponent().getTitle(), "", ReactionViewer.VIEW_NATIVE);
		}
		return reactionViewer;
	}
	
	private static Frame getAuxFrame() {
		if (auxiliaryFrame == null) {
			auxiliaryFrame = new Frame();
		}
		return auxiliaryFrame;
	}
	
	private static ChemistryPanel getChimePro() {
		if (cps == null) {
			cps = new ChemistryPanel();
		}
		return cps;
	}
	
	/**
	 * @return Returns the reactionImage.
	 */
	public byte[] getReactionImage(Dimension size) {
		byte[] reactionImage = reactionPageInfo.getReactionImage(); 
		byte[] reactionSketch = reactionPageInfo.getReactionSketch();
		
		if(reactionImage == null && reactionSketch != null) {
			reactionImage = convertRxnSketchToImage(reactionPageInfo.getReactionSketch(), size);
		}
		
		return (reactionImage == null ? new byte[0] : reactionImage);
	}
	
	private byte[] convertRxnSketchToImage(byte[] rxnBytes, Dimension size) {
		if (rxnBytes != null  &&  rxnBytes.length > 0) {
			String rxnStr = new String(rxnBytes);
			try {
				if (rxnStr != null) {  // use molfile if can be decoded
					String decodedRxnString = Decoder.decodeString(rxnStr);
					int index = decodedRxnString.indexOf("M  END");
					if (index >= 0) {
						getChimePro().setMolfileData(decodedRxnString);
						rxnBytes = getChimePro().getMolfileData().getBytes();
					}
				}
				
				getReactionViewer().setPreferredSize(size);
				getReactionViewer().setChemistry(rxnBytes);
				
				boolean reactionSchemeExists = (getReactionViewer().getNativeSketch() != null && getReactionViewer().getNativeSketch().length > 0);

				if(reactionSchemeExists){
					getAuxFrame().add(getReactionViewer());
					BufferedImage image = createImage(getReactionViewer(), size);
					getAuxFrame().remove(getReactionViewer());
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(image, "jpeg", baos);				
					baos.flush();
					
					byte[] result = baos.toByteArray();
					
					baos.close();
					
					return result;
				}
			} catch (Exception e) {
				try {
					int height = (size == null ? reactionPageInfo.getHeight() : size.height);
					int width = (size == null ? reactionPageInfo.getWidth() : size.width);
					return ServiceController.getChemistryDelegate().convertReactionToPicture(rxnBytes, PictureProperties.PNG, height, width, 1.0, 0.26458);
				} catch (Exception e1) {
					log.error("Failed to convert native reaction scheme to image: ", e);
				}
			}
		}
		
		return null;
	}
	
	private BufferedImage createImage(JComponent component, Dimension componentSize) {
		if (componentSize == null) {
			componentSize = component.getPreferredSize();
		} else {
			component.setPreferredSize(componentSize);
		}
		
		component.setDoubleBuffered(false);
		component.setSize(componentSize);
		component.addNotify();
		component.validate();
	
		BufferedImage img = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(componentSize.width, componentSize.height);
		
		Graphics2D grap = img.createGraphics();
		grap.setColor(Color.WHITE);
		grap.fillRect(0, 0, img.getWidth(), img.getHeight());
		
		component.print(grap);
		
		grap.dispose();
		
		return img;
	}
}
