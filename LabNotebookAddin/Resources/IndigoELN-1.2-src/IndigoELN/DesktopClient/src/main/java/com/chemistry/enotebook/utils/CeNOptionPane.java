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

import javax.swing.*;
import java.awt.*;

public class CeNOptionPane{

	static CeNOptionPane thisDialog = new CeNOptionPane();
	
	private  JComponent getMessageDisplayComponent(String message){
		if(message.length()>70){
			JTextArea textArea = new JTextArea(5, 40);
			textArea.setText(message);
			textArea.setEditable(false);
			textArea.setLineWrap(true);
			textArea.setBackground(new Color(204,204,204));
			return new JScrollPane(textArea,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
		} else {
			JLabel messageLabel = new JLabel(message);
			messageLabel.setFont(new Font("Dialog",Font.PLAIN,12));
			return messageLabel;
		}
	}
	
	public static void showMessageDialog(Component parentComponent, Object message){
		String textMessage = message.toString();
		JOptionPane.showMessageDialog(parentComponent,thisDialog.getMessageDisplayComponent(textMessage));
	}
	
    public static void 	showMessageDialog(Component parentComponent, Object message, String title, int messageType){
    	String textMessage = message.toString();
		JOptionPane.showMessageDialog(parentComponent,thisDialog.getMessageDisplayComponent(textMessage),title, messageType);
    }
    
    public static void 	showMessageDialog(Component parentComponent, Object message, String title, int messageType, Icon icon){
    	String textMessage = message.toString();
		JOptionPane.showMessageDialog(parentComponent,thisDialog.getMessageDisplayComponent(textMessage),title, messageType,icon);
    }
    
    public static int 	showOptionDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object initialValue){
    	String textMessage = message.toString();
		return JOptionPane.showOptionDialog(parentComponent,thisDialog.getMessageDisplayComponent(textMessage),
				title,optionType,messageType, icon, options, initialValue);
    }
	
	public static int showConfirmDialog(Component parentComponent, Object message){
		String textMessage = message.toString();
		return JOptionPane.showConfirmDialog(parentComponent,thisDialog.getMessageDisplayComponent(textMessage));
	}
    
	public static int 	showConfirmDialog(Component parentComponent, Object message, String title, int optionType){
		String textMessage = message.toString();
		return JOptionPane.showConfirmDialog(parentComponent,thisDialog.getMessageDisplayComponent(textMessage),
				title,optionType);
	}

	public static int 	showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType){
		String textMessage = message.toString();
		return JOptionPane.showConfirmDialog(parentComponent,thisDialog.getMessageDisplayComponent(textMessage),
				title,optionType,messageType);
	}
    
	public static int 	showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon){
		String textMessage = message.toString();
		return JOptionPane.showConfirmDialog(parentComponent,thisDialog.getMessageDisplayComponent(textMessage),
				title,optionType,messageType,icon);
	}
	
	public static void main(String[] str){
		String message= "abcde12345 abcde12345 abcde12345 abcde12345 abcde12345";
		//"abcde12345 abcde12345 abcde12345 abcde12345 abcde12345 abcde12345"+
		//"abcde12345 abcde12345 abcde12345 abcde12345 abcde12345 abcde12345"+
		//"abcde12345 abcde12345 abcde12345 abcde12345 abcde12345 abcde12345"+
		//"abcde12345 abcde12345 abcde12345 abcde12345 abcde12345 abcde12345"+
		//"abcde12345 abcde12345 abcde12345 abcde12345 abcde12345 abcde12345";    
		CeNOptionPane.showMessageDialog(new JFrame(),message);
	}
}
