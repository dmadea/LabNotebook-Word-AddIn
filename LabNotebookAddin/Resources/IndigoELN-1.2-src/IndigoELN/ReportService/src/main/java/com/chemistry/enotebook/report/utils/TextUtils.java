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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TextUtils {
	private static final Log log = LogFactory.getLog(TextUtils.class);
	
	private static String[] htmlNames = { "&amp;", "&lt;",   "&gt;", "&nbsp;" };
	private static String[] htmlCodes = { "&#38;",  "&#60;",  "&#62;", "&#160;" };

	public static String substituteHtmlCodes(String s) {
		//assert (htmlNames.length == htmlCodes.length) : "html namds and codes arrays are not the same length";
		for (int i=0; i<htmlNames.length; i++) {
			s = s.replaceAll(htmlNames[i], htmlCodes[i]);
		}
		return s;
	}

	public static void fillBufferWithClassMethods(StringBuffer buff, Object classToProcess) {
		TextUtils.fillBufferWithClassMethods(buff, classToProcess, false);
	}
	
	public static void fillBufferWithClassMethods(StringBuffer buff, Object classToProcess, boolean wrapWithCDATA) {
		TextUtils.fillBufferWithClassMethods(buff, classToProcess, wrapWithCDATA, null);
	}
	
	public static void fillBufferWithClassMethods(StringBuffer buff, Object classToProcess, boolean wrapWithCDATA, List<String> listMethodNamesToSkip) {
		if(classToProcess != null)
		{
			String className = classToProcess.getClass().getName();
			try {
				Class<?> c = Class.forName(classToProcess.getClass().getName());
				Object me = classToProcess;
				Method[] allMethods = c.getDeclaredMethods();
				for (int i=0; i<allMethods.length; i++) {
					Method method = allMethods[i];
					String methodName = method.getName();
					if (methodName.startsWith("get")) {
						String fieldName = methodName.substring(3);
						if(listMethodNamesToSkip != null && listMethodNamesToSkip.contains(fieldName))
							continue;
						buff.append("<").append(fieldName).append(">");
						try {
							if(wrapWithCDATA){
								buff.append("<![CDATA[");
							}
							String field = (method.invoke(me, (Object[])null)).toString();
							if(!wrapWithCDATA && !field.startsWith("<![CDATA[")){
								field = escapeSpecialCharacters(field);
							}
							buff.append(field);
							if(wrapWithCDATA){
								buff.append("]]>");;
							}
						} catch (RuntimeException e) {
							buff.append("");
						}
						buff.append("</").append(fieldName).append(">");
					}
				}
				
			} catch (ClassNotFoundException e) {
				log.error("Failed to create XML for class: " + className + ". Please ensure classpath is set properly.", e);
			} catch (IllegalArgumentException e) {
				log.error("Failed to create XML for class: " + className, e);
			} catch (IllegalAccessException e) {
				log.error("Failed to create XML for class: " + className + ". Perhaps a \"get\" method might not exist for object.", e);
			} catch (InvocationTargetException e) {
				log.error("Failed to create XML for class: " + className, e);
			}
		}
	}
	
	private static String escapeSpecialCharacters(String aText){
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(aText);
		char character =  iterator.current();
		while (character != CharacterIterator.DONE ){
			if (character == '<') {
				result.append("&lt;");
			}
			else if (character == '>') {
				result.append("&gt;");
			}
			else if (character == '\"') {
				result.append("&quot;");
			}
			else if (character == '\'') {
				result.append("&#039;");
			}
			else if (character == '&') {
				result.append("&amp;");
			}
			else {
				//the char is not a special one
				//add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}
	
	public static String getTimeForTimeZone(Date date, String timeZone) {
		if (date != null) {
			SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss z");			
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			return df.format(date);
		} 
		return "";
	}
}
