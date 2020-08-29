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

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.batch.BatchResidualSolventModel;
import com.chemistry.enotebook.domain.batch.BatchSolubilitySolventModel;
import com.chemistry.enotebook.experiment.common.codetables.SaltCodeCache;
import com.chemistry.enotebook.experiment.utils.CeNXMLParser;
import com.chemistry.enotebook.formatter.UtilsDispatcher;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.storage.ReactionPageInfo;
import com.chemistry.enotebook.storage.StorageVO;
import com.common.chemistry.codetable.CodeTableCache;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.sql.Clob;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommonUtils {

	private static final Log log = LogFactory.getLog(CommonUtils.class);
	
	private static final String DATE_FORMAT = "MMM dd, yyyy hh:mm:ss z";
	private static final String[] DATE_PATTERNS = new String[] {
			DATE_FORMAT,
			"MMM dd, yyyy HH:mm:ss",
			"yyyy-MM-dd.hh.mm. ss. S",
			"yyyy-MM-dd hh:mm:ss.S zzzz",
			"yyyy-MM-dd hh:mm:ss.S",
			"EEE MMM dd hh:mm:ss z yyyy" // Tue Apr 12 05:38:16 MSD 2011
	};
	
	/**
	 * Concatenates the values in List into a comma delineated String and return it.
	 */
	public static String getConcatenatedString(List<String> valueList) {
		String paramString = "";
	
		if (valueList != null) {
		for (int i = 0; i < valueList.size(); i++) {
			if (i > 0) {
				paramString += " ,'" + valueList.get(i) + "'";
			} else {
				paramString = "'" + valueList.get(i) + "'";
			}
		}
		}
				
		return paramString;
	}

	public static boolean toBoolean(String boolStr) {
		if (boolStr != null) {
			boolStr = boolStr.trim();
			if (boolStr.length() > 0 && boolStr.equalsIgnoreCase("true")) {
				return true;
			}
		}
		return false;
	}

	public static boolean toBooleanFromChar(String boolChar) {
		return StringUtils.isNotBlank(boolChar) && boolChar.trim().equalsIgnoreCase("Y");
	}

	public static char toCharFromBoolean(boolean bool) {
		if ( bool == true) {
			return 'Y';
		}
		return 'N';
	}	
	public static double toDouble(String doubleStr) {
		double val = 0.0;
		if (doubleStr != null) {
			doubleStr = doubleStr.trim();
			if (doubleStr.length() > 0) {
				try {
					val = Double.parseDouble(doubleStr);
				} catch (Exception error) {
//					error.printStackTrace();
					// ignore if error
				}
			}
		}
		return val;
	}

	public static int toInteger(String intStr) {
		int val = -1;
		if (intStr != null ) {
			intStr = intStr.trim();
			if(!intStr.equals("null") || !intStr.equals("")){
				if (intStr.length() > 0) {
					try {
						val = Integer.parseInt(intStr);
					} catch (Exception error) {
//						error.printStackTrace();
						// ignore if error
					}
				}
			}
		}
		return val;
	}

	
	public static long toLong(String intStr) {
		long val = -1;
		if (intStr != null ) {
			intStr = intStr.trim();
			if(!intStr.equals("null") || !intStr.equals("")){
				if (intStr.length() > 0) {
					try {
						val = Integer.parseInt(intStr);
					} catch (Exception error) {
						error.printStackTrace();
						// ignore if error
					}
				}
			}
		}
		return val;
	}
	
	/**
	 * @deprecated - this does nothing but return a null for the date object.
	 * @param dateStr
	 * @return
	 */
	public static Date toDate(String dateStr) {
		Date date = null;
		if (dateStr != null) {
			dateStr = dateStr.trim();
			if (dateStr.length() > 0) {
//				try {
//					// Convert to Date Object
//				} catch (Exception error) {
//					error.printStackTrace();
//					// ignore if error
//				}
			}
		}
		return date;
	}

	/**
	 * @deprecated - does nothing but return a null calendar object.
	 * @param dateStr
	 * @return
	 */
	public static Calendar toCalender(String dateStr) {
		Calendar date = null;
		if (dateStr != null) {
			dateStr = dateStr.trim();
			if (dateStr.length() > 0) {
//				try {
//					// Convert to Calender Object
//				} catch (Exception error) {
//					error.printStackTrace();
//					// ignore if error
//				}
			}
		}
		return date;
	}

	/**
	 * blocking call.
	 * @param clob
	 * @return
	 */
	public static String toStringFromClob(Clob clob){
		if(clob != null){
			try{
				char[] ch  = new char[(int)clob.length()];
				clob.getCharacterStream().read(ch,0,(int)clob.length());
				return new String(ch);
			} catch(Exception err){
				log.error("Error extracting string from Clob object", err);
				return null;
			}
		}
		return null;
	}
	
	public static Timestamp getCurrentTimestamp(){
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		return ts;
	}
	
	public static  String toTimestampString(Timestamp timestamp){
		if(timestamp != null){
			SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss z");
			return dateFormatter.format(new Long(timestamp.getTime()));
		}
		return null;
	}
	
	public static InputStream toBinaryStream(byte[] bytes) {
		if (bytes != null) {
			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			return stream;
		}
		return null;
	}

	/*
	 * This method converts the XMLString containing array elements. Each value is surrounded by <Entry> and </Entry> xml tags. This
	 * method chops of xml and prepares and array list of the actual values.
	 */
	public static ArrayList<String> convertXMLToArrayList(String entryStr) {

		ArrayList<String> list = new ArrayList<String>();
		int index = 0;
		String valueStr = null;
		String startEntryTag = "<Entry>";
		String endEntryTag = "</Entry>";
		int valueStartIndex = 0;
		int valueEndIndex = 0;
		if (entryStr != null) {
			index = entryStr.indexOf(startEntryTag);
			while (index != -1) {
				entryStr = entryStr.substring(index + 1);
				valueStartIndex = entryStr.indexOf(">");
				valueEndIndex = entryStr.indexOf("</Entry>");
				valueStr = entryStr.substring(valueStartIndex + 1, valueEndIndex);
				list.add(valueStr);
				entryStr = entryStr.substring(valueEndIndex + endEntryTag.length());
				index = entryStr.indexOf(startEntryTag);
			}
		}
		return list;

	}

	public static String[] toStringArray(List<String> list) {
		String[] strArray = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			strArray[i] = list.get(i);
		}
		return strArray;
	}

	public static ArrayList<String> toArrayListFromString(String keys) {
		StringTokenizer token = new StringTokenizer(keys, ",");
		int count = 0;
		ArrayList<String> list = new ArrayList<String>();
		while (token.hasMoreTokens()) {
			count++;
			list.add(token.nextToken());
		}
		return list;
	}

	public static String getValuesAsMultilineString(String[] vals) {
		String val = "";
		if (vals == null || vals.length == 0)
			return val;

		int size = vals.length;
		for (int i = 0; i < size; i++) {
			val = val + vals[i] + "\n";
		}
		return val;
	}

	public boolean serializeObject(Object object, String filename) {
		try {
			// Serialize to a file
			String filenameStr = filename ;
			//+ Calendar.getInstance().getTimeInMillis();
			ObjectOutput out = new ObjectOutputStream(new FileOutputStream(filenameStr + ".ser"));
			out.writeObject(object);
			out.close();

			// Serialize to a byte array
			/*
			 * ByteArrayOutputStream bos = new ByteArrayOutputStream() ; out = new ObjectOutputStream(bos) ;
			 * out.writeObject(object); out.close();
			 */

			// Get the bytes of the serialized object
			// byte[] buf = bos.toByteArray();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Object deSerializeObject(String serFileName) {
		Object obj = null;
		try {
			File file = new File(serFileName + ".ser");
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			// Deserialize the object
			obj = in.readObject();
			in.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return obj; // Calle should be able to cast to proper class
	}

	public static boolean isNull(Object obj){
		return !isNotNull(obj);
	}

	public static boolean isNotNull(Object obj){
		if(obj == null)
			return false;
		if(obj instanceof String){
			String str = ((String)obj).trim();
			if(str.equalsIgnoreCase("null")||str.equals("")){
				return false;
			} 
		} else if (obj instanceof List) {
			if(((List<?>)obj).isEmpty())
				return false;
		} else if (obj.getClass().isArray()) {
			if(Array.getLength(obj) == 0) {
				return false;
			}
		}
		return true;
	}
	
	public static String getStackTrace(Throwable aThrowable) {
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    aThrowable.printStackTrace(printWriter);
	    return result.toString();
	  }
	
	
	public static void printNBPageData(NotebookPageModel pageModel) {
		int stepSize = pageModel.getReactionSteps().size();
		log.debug("Total Steps in exp:" + stepSize);
		for (int k = 0; k < stepSize; k++) {

			ReactionStepModel reacModel = pageModel.getReactionSteps().get(k);
			int size = reacModel.getMonomers().size();
			log.debug("-------- Step number:" + reacModel.getStepNumber());
			log.debug(" Monomer BatchesList objs " + reacModel.getMonomers());
			for (int i = 0; i < size; i++) {
				BatchesList<MonomerBatchModel> bList = reacModel.getMonomers().get(i);
				log.debug("BatchesList : "+bList.getClass());
				log.debug("Postion: " + bList.getPosition() + " Size: " + bList.getBatchModels().size());
				log.debug("Key: " + bList.getListKey() + " is Deduped: " + bList.isDedupedList());
				log.debug("Transaction order: " + bList.getStoicTransactionOrder());
			}
			int sizeP = reacModel.getProducts().size();
			log.debug(" Product BatchesList objs " + sizeP);
			for (int i = 0; i < sizeP; i++) {
				BatchesList<ProductBatchModel> bList = reacModel.getProducts().get(i);
				log.debug("Postion: " + bList.getPosition() + " Size: " + bList.getBatchModels().size());
				log.debug("Key: " + bList.getListKey() + " is Deduped: " + bList.isDedupedList());
				log.debug("Batches List "+bList);
				List<ProductBatchModel> list = bList.getBatchModels();
				int sizePB = list.size();
				for(int x =0; x < sizePB ; x++)
				{
					ProductBatchModel productBatch = list.get(x);
					if(productBatch.isModelChanged())
					{
					 log.debug("Product ID: " + productBatch.getProductId());
					 log.debug("productBatch salt equiv:"+ new Double(productBatch.getSaltEquivs()) + " primitive:"+productBatch.getSaltEquivs());
					}
				}
			}
			
			int sizeSE = reacModel.getBatchesFromStoicBatchesList().size();
			log.debug("Total Stoic element batches in the step:"+ sizeSE);
			
			int sizeMP = reacModel.getMonomerPlates().size();
			log.debug("Total monomer plates in the step:"+ sizeMP);
			
			int sizePP = reacModel.getProductPlates().size();
			log.debug("Total product plates in the step:"+ sizePP);
		}
	}


	public static String getUserContainerCode()
	{
		String code = System.currentTimeMillis()+"";
		if(code.length() >15)
		{
		 return code.substring(0, 14);	
		}else
		{
			return code;
		}
	}
	
	public static String getTrucatedRxnMolfileForSearch(byte[] rxnMolfile){
		String molfile = new String(rxnMolfile);
        int index = molfile.indexOf("$RXN");
        if(index>0){
        	molfile = molfile.substring(index, molfile.length());
        }
        index = molfile.indexOf("$DTYPE");
        if(index>0){
        	molfile = molfile.substring(0, index);
        }
        return molfile;
	}
	
	public static boolean isThisMonomerAIntermediateProduct(String monID)
	{
		// This will be of format  [A:B] or 
		if (monID.startsWith("[") || monID.endsWith("]")) {
			log.debug("This Monomer is Intrm product in prev step and participating as monomer in this step.No need to dedupe"
							+ monID);
			return true;
		}else
		{
		  return false;	
		}
	}
	
	public static String convertArrayListToXML(List<String> listofValues) {

		if(isNull(listofValues)) {
			return "";
		}
		int size = listofValues.size();
		String valueStr = "";
		String startEntryTag = "<Entry>";
		String endEntryTag = "</Entry>";
		for(int i = 0; i < size ; i ++)
		{
			valueStr = valueStr + startEntryTag + listofValues.get(i) +	endEntryTag + "\n";
		}
		
		return valueStr;

	}
	
	public static String convertLongArrayToXML(long[] listofValues) {

		if(listofValues == null || listofValues.length == 0) return "";
		int size = listofValues.length;
		String valueStr = "";
		String startEntryTag = "<Entry>";
		String endEntryTag = "</Entry>";
		for(int i = 0; i < size ; i ++)
		{
			valueStr = valueStr + startEntryTag + listofValues[i] +	endEntryTag + "\n";
		}
		
		return valueStr;

	}

	//purpose is to filter the characters that cause trouble in XML document.
	// like & , < , >
	
	public static String filterXMLSpecialCharsInText(String inputString){
		String outString = replaceSpecialCharsinXML(inputString);
		outString = replaceSpecialSymbols(outString,"<","&lt;");
		outString = replaceSpecialSymbols(outString,">","&gt;");
		
		return outString;
	}

	public static String replaceSpecialCharsinXML(String inputString){
		if(inputString == null || inputString.trim().length()==0)
			return "";
		String outString = replaceAmpersandSymbol(inputString);
		outString = replaceSpecialCharsInText(outString);
		return outString;
	}
	
	public static String replaceSpecialCharsInText(String inputTxt){
		
		String outText = replaceSpecialSymbols(inputTxt,"'","''");
		return outText;
	}
	
	
	
	public static String replaceAmpersandSymbol(String inputXmlText)
	{
		String token = "&";
		String ampersandReplace = "&amp;";
		if(inputXmlText == null || inputXmlText.trim().length()==0)
			return "";
		
		StringTokenizer strTok = new StringTokenizer(inputXmlText, token);
		
		if(strTok.countTokens() == 1) return inputXmlText;
		
		String outXMLText = inputXmlText.replaceAll(token, ampersandReplace);
		//Following will handle if &lt; and &gt; was used in the text
		outXMLText = outXMLText.replaceAll("&amp;lt;", "&lt;");
		outXMLText = outXMLText.replaceAll("&amp;gt;", "&gt;");
		
		return outXMLText;
	}
	
	public static String replaceSpecialSymbols(String inputString,String searchFor,String replaceWith)
	{
		String token = searchFor;
		if(inputString == null || inputString.trim().length()==0)
			return "";
		
		StringTokenizer strTok = new StringTokenizer(inputString, token);
		if(strTok.countTokens() == 1) return inputString;
		String outXMLText = inputString.replaceAll(searchFor, replaceWith );
		return outXMLText;
	}
	
	//This util function converts back to original symbol.
	public static String getActualSymbolfromMaskedString(String inputStr)
	{
		if(inputStr.equals("&lt;"))
		{
			return "<";
		}else if (inputStr.equals("&gt;"))
		{
			return ">";
		}else
		{
			return inputStr;
		}
	}
	
	public static String getAsPipeSeperateValues(String args[])
	{
		String result = null;
		if(args == null)
		{
			return result;
		}else if(args.length == 1)
		{
			result = args[0];
		}else
		{
			result = args[0];
			for(int i = 1; i < args.length ; i ++)
			{
				result = result+ "|"+ args[i];
			}
		}
		return result;
	}
	
	public static String[] getStringArray(String input)
	{
		ArrayList<String> list = new ArrayList<String>();
		if(input != null && !input.equals(""))
		{
			StringTokenizer token = new StringTokenizer(input, "|");
			while (token.hasMoreTokens()) {
				list.add(token.nextToken());
			}
		}
		return list.toArray(new String[]{});
	}	

	public static String getStandardTableCellFont()
	{
		return CeNConstants.TABLE_CELL_FONT;
	}
	
	public static String getAsPipeSeperateValues(ArrayList<String> stringArrayList)
	{
		if(stringArrayList == null || stringArrayList.size() == 0)
		{
			return null;
		}
		String args[] = stringArrayList.toArray(new String[]{});
		String result = null;
		if(args == null)
		{
			return result;
		}else if(args.length == 1)
		{
			result = args[0];
		}else
		{
			result = args[0];
			for(int i = 1; i < args.length ; i ++)
			{
				result = result+ "|"+ args[i];
			}
		}
		return result;
	}
	
	public static ArrayList<String> getArrayListFromPipeSeperatedString(String input)
	{
		ArrayList<String> list = new ArrayList<String>();
		if(StringUtils.isNotBlank(input))
		{
			StringTokenizer token = new StringTokenizer(input, "|");
			while (token.hasMoreTokens()) {
				list.add(token.nextToken());
			}
		}
		return list;
	}	
	

	public static boolean getProductBatchModelEditableFlag(ProductBatchModel batchModel, NotebookPageModel pageModel) {
		if (batchModel != null ) {
            if (!batchModel.isEditable()) {
                return false;
            }
        }
        return pageModel.isEditable();
	}
	
	public static String readJsp(String url) throws Exception {
		URL propertyReaderJsp = new URL(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(propertyReaderJsp.openStream()));
		StringBuffer buffer = new StringBuffer();
		String line = null;
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}
		in.close();
		return buffer.toString().trim();
	}
	
//	Logic to match compounds based on their compound IDs to identify the equivalency
	public static boolean isMatch(String compound1, String compound2) {

		if(compound1 == null || compound1.equals("") || compound2 == null || compound2.equals("") )
		{
			return false;
		}
		try
		{
//		FormattedCompoundNumber compFromatted1 = CNFHelper.getCompoundFormatInfo(compound1);
//		FormattedCompoundNumber compFromatted2 = CNFHelper.getCompoundFormatInfo(compound2);
//		String compFormattedStrCompID1 = compFromatted1.getCompID();
//		String compFormattedStrCompID2 = compFromatted2.getCompID();

		String compFormattedStrCompID1 = UtilsDispatcher.getFormatter().formatCompoundNumber(compound1);
		String compFormattedStrCompID2 = UtilsDispatcher.getFormatter().formatCompoundNumber(compound2);
		if(compFormattedStrCompID1.equals(compFormattedStrCompID2))
		{
		return true;	
		}
		//check without saltcode
//		String saltcode1 = compFromatted1.getSaltCode();
//		String saltcode2 = compFromatted2.getSaltCode();
		String saltcode1 = UtilsDispatcher.getFormatter().getSaltCode(compound1);
		String saltcode2 = UtilsDispatcher.getFormatter().getSaltCode(compound2);		
		String compParent1 = "";
		String compParent2 = "";
		int indx1 = compFormattedStrCompID1.indexOf(saltcode1);
		//parent salt code 00 is not represented in the compound id some times
		if(indx1 > 0)
		{
		compParent1 = compFormattedStrCompID1.substring(0,indx1);
		}else
		{
		compParent1 = compFormattedStrCompID1;
		}
		
		int indx2 = compFormattedStrCompID2.indexOf(saltcode2);
		if(indx2 > 0)
		{
		compParent2 = compFormattedStrCompID2.substring(0,indx2);
		}else
		{
		compParent2 = compFormattedStrCompID2;
		}
		if(compParent1.equalsIgnoreCase(compParent2))
		{
		return true;	
		}
		
		}catch(Exception e)
		{
		log.error(e);	
		
		if (compound1.equals(compound2)) {
			return true;
		}
		if (compound1.indexOf("-", 4) > -1) {
			compound1 = stripExtension(compound1);
		}
		if (compound2.indexOf("-", 4) > -1) {
			compound2 = stripExtension(compound2);
		}
		if (compound1.equals(compound2)) {
			return true;
		}
		}
		return false;

	}

	private static String stripExtension(String compound) {
		
		if (compound.length() - compound.indexOf("-", 4) < 4) {
			return compound.substring(0, compound.indexOf("-", 4));
		}
		return compound;
	}
	
	public static void copyCompoundManagementMonomerSaltInfoToSynthesisPlanMonomer(MonomerBatchModel otherMonomerBatchModel, MonomerBatchModel synthesisPlanMonomerBatchModel)
	{
		//assumed that parent id is same for both the compounds and only may differ in salt 
		try
		{
//			FormattedCompoundNumber compFromatted1 = CNFHelper.getCompoundFormatInfo(otherMonomerBatchModel.getCompoundId());
//			FormattedCompoundNumber compFromatted2 = CNFHelper.getCompoundFormatInfo(synthesisPlanMonomerBatchModel.getCompoundId());
//			String saltcode1 = compFromatted1.getSaltCode();
//			String saltcode2 = compFromatted2.getSaltCode();
			String saltcode1 = UtilsDispatcher.getFormatter().getSaltCode(otherMonomerBatchModel.getCompoundId());
			// copy salt info if both salts don't match. Design Service is not providing salt info as well. Other import 
			// gives the chance to populate the compound with proper salt info
			// if the other batch has salt and it doesn't match synthesisPlan batch salt 
			if(SaltFormModel.isParentCode(saltcode1) == false)
			{
				//salts are different. So copy the matrackMon salt to synthesisPlanMonomer salt
				double parentMW = synthesisPlanMonomerBatchModel.getCompound().getMolWgt();
				//In CompoundManagementOderAPIImpl the batch MW/MF is set to compound(which is parent actually)
				double batchMW =  otherMonomerBatchModel.getCompound().getMolWgt();
				
				//get salt mwt from salt code using Cache
				SaltCodeCache scc = SaltCodeCache.getCache();
				
				//now set the salt code and mole to synthesisPlan batch
				synthesisPlanMonomerBatchModel.setSaltForm(new SaltFormModel(saltcode1,
				                                                    scc.getDescriptionGivenCode(saltcode1),
				                                                    scc.getMolFormulaGivenCode(saltcode1),
				                                                    scc.getMolWtGivenCode(saltcode1)));
				if(batchMW > parentMW) {
					synthesisPlanMonomerBatchModel.setSaltEquivs((batchMW-parentMW)/synthesisPlanMonomerBatchModel.getSaltForm().getMolWgt());
				} else {
					// otherwise it would be zero
					synthesisPlanMonomerBatchModel.setSaltEquivs(0.0);
				}
			} else {
				//override the synthesisPlan salt with matrack no salt(parent)
				synthesisPlanMonomerBatchModel.setSaltForm(new SaltFormModel());
			}
		} catch(Exception e) {
			log.error("Failed to fill in salt information from either synthesisPlan or MatTrak", e);	
		}
	}

	public static String[] getCompoundIDsFromTheList(List<MonomerBatchModel> batchesList)
	{
		int size = batchesList.size();
		String[] array = new String[size];
		for (int i=0; i < size ; i ++) {
			MonomerBatchModel otherMonomerBatchModel = batchesList.get(i);
			array[i] = otherMonomerBatchModel.getCompoundId();
			
		}
		
		return array;
	}
	
	public static MonomerBatchModel getMatchingModelFromConcordance(List<MonomerBatchModel> otherMonomerBatchesList,String[] concrdanceIDArray)
	{
		if(concrdanceIDArray == null || otherMonomerBatchesList == null) return null;
		int listSize = otherMonomerBatchesList.size();
		int arrayLen = concrdanceIDArray.length;
		for(int i = 0 ; i < arrayLen ; i ++)
		{
			for(int k= 0 ; k< listSize ; k ++)
			{
				MonomerBatchModel model = otherMonomerBatchesList.get(k);
				if(CommonUtils.isMatch(concrdanceIDArray[i], model.getCompoundId()))
				{
					return model;
				}
			}
		}
		return null;
	}
	
	/**
	 * Converts Chloracnegen type like "CLASS_1" -> "Class 1 Chloracnegen" and back<br>
	 * to use with Chloracnegen Test service and CeN DB.
	 * @param chloracnegenType
	 * @param toDB if true, it will be "Class 1 Chloracnegen" -> "CLASS_1"
	 * @return
	 */
	public static String convertChloracnegenType(String chloracnegenType, boolean toDB) {
		if (chloracnegenType != null) {
			if (toDB) {
				if (StringUtils.contains(StringUtils.lowerCase(chloracnegenType), "class")) { // Got "Class 1 Chloracnegen"
					chloracnegenType = StringUtils.lowerCase(chloracnegenType); // Got "class 1 chloracnegen"
					int index = StringUtils.indexOf(chloracnegenType, "chloracnegen");
					if (index != -1) {
						chloracnegenType = StringUtils.substring(chloracnegenType, 0, StringUtils.indexOf(chloracnegenType, "chloracnegen")).trim(); // Got "class 1"
					}
					chloracnegenType = StringUtils.replace(chloracnegenType, " ", "_"); // Got "class_1"
					chloracnegenType = StringUtils.upperCase(chloracnegenType); // Got "CLASS_1"
				}
			} else {
				if (StringUtils.contains(chloracnegenType, "CLASS")) { // Got "CLASS_1"
					chloracnegenType = StringUtils.lowerCase(chloracnegenType); // Got "class_1"
					chloracnegenType = StringUtils.replace(chloracnegenType, "_", " "); // Got "class 1"
					chloracnegenType = StringUtils.capitalize(chloracnegenType); // Got "Class 1"
					chloracnegenType += " Chloracnegen"; // Got "Class 1 Chloracnegen"
				}	
			}
		}
		return chloracnegenType;
	}

    public static String getResidualSolventsList(ProductBatchModel batch) {
    List<BatchResidualSolventModel> list = batch.getRegInfo().getResidualSolventList();
    if (list.size() != 0) {
        String residualString = new String();
        for (int i = 0; i < list.size(); i++) {
            BatchResidualSolventModel residualSolventVO = list.get(i);
            if (residualString.length() > 0) {
                residualString += ", ";
            }
            residualString += residualSolventVO.getEqOfSolvent() + " mols of " + residualSolventVO.getResidualDescription();
        }
        return residualString;
    }
    else
        return "";
    }

    public static String getSolubilitySolventList(ProductBatchModel batch) {
        ArrayList<BatchSolubilitySolventModel> list = batch.getRegInfo().getSolubilitySolventList();
        if (list.size() != 0) {
            String solubilityString = new String();
            for (int i = 0; i < list.size(); i++) {
                BatchSolubilitySolventModel solubilitySolventVO = list.get(i);
                if (solubilitySolventVO.isQuantitative()) {
                    if (solubilityString.length() > 0)
                        solubilityString += ", ";
                    solubilityString += solubilitySolventVO.getOperator() + " " + solubilitySolventVO.getSolubilityValue() + " "
                            + solubilitySolventVO.getSolubilityUnit() + " in " + solubilitySolventVO.getCodeAndName();
                } else {
                    if (solubilityString.length() > 0)
                        solubilityString += ", ";
                    solubilityString += solubilitySolventVO.getQualiString() + " in " + solubilitySolventVO.getCodeAndName();
                }
            }
            return solubilityString;
        }
        else
            return "";
    }
    
    /**
     * Parse date given from CompoundRegistration
     * @param regDate Date string from CompoundRegistration
     * @return Date object
     * @throws ParseException
     */
    public static Date parseRegDate(String regDate) throws ParseException {
    	return DateUtils.parseDate(regDate, DATE_PATTERNS);
    }
    
    public static Timestamp parseRegDateToTimestamp(String regDate) throws ParseException {
    	return new Timestamp(parseRegDate(regDate).getTime());
    }
    
	public static String getDateAsStringInCenFormat(Date d) {
		if (d != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			return sdf.format(d);
		}
		return "";
    }
	
	public static byte[] getReportHttpResponseResult(String url) throws Exception {
		HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setSoTimeout(5 * 60 * 1000);
		
        HttpMethod method = new GetMethod(url);
        
        method.setRequestHeader("Authorization", ServiceLocator.getInstance().getServiceAuthorizationString());
        
		try {
			int statusCode = client.executeMethod(method);
			if (statusCode == HttpStatus.SC_OK) {
				return method.getResponseBody();	
			} else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
				// Find 'Error' header
				Header[] headers = method.getResponseHeaders();
				String errorValue = "Unknown Error";
				
				for (Header header : headers) {
					if (header != null) {
						if (StringUtils.equals(header.getName(), "Error")) {
							errorValue = header.getValue();
						}
					}
				}
				
				throw new Exception(errorValue);
			}
		} finally {
			method.releaseConnection();				
		}
		return null;
	}
	
	public static List<ReactionPageInfo> buildReactionPagesList(StorageVO[] rowsets) {
		List<ReactionPageInfo> pagesList = new ArrayList<ReactionPageInfo>();
		
		try {
			for (StorageVO rowset : rowsets) {
				String xmlMetadata = rowset.getString("XML_METADATA");
				
				String continued_from_rxn = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/ContinuedFromRxn");
				String continued_to_rxn = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/ContinuedToRxn");
				
				ReactionPageInfo reactionPageInfo = new ReactionPageInfo();
				// set up reation notebook experiment key
				reactionPageInfo.setVersion(rowset.getString("PAGE_VERSION"));
				reactionPageInfo.setNoteBookExperiment(rowset.getString("NOTEBOOK") + "-" + rowset.getString("EXPERIMENT"));
				// set up reaction Image
				reactionPageInfo.setReactionImage(rowset.getBytes("SKETCH_IMAGE"));
				// set up reaction scheme
				reactionPageInfo.setReactionSketch(rowset.getBytes("RXN_SKETCH"));
				// set up reaction page info
				StringBuffer pageInfo = new StringBuffer();
				String desc;
				if (rowset.getString("CREATION_DATE") != null && !rowset.getString("CREATION_DATE").equals(""))
					pageInfo.append(rowset.getString("CREATION_DATE") + "\n");
				String site = rowset.getString("SITE_CODE");
				desc = CodeTableCache.getCache().getSiteDescription(site);
				if (desc == null)
					desc = site;
				pageInfo.append(desc);
				String user = rowset.getString("USERNAME");
				reactionPageInfo.setUsername(user);
				String userFullName = rowset.getString("FULLNAME");
				if(CommonUtils.isNotNull(userFullName)) {
					pageInfo.append(", " + userFullName + "\n");
				} else
					pageInfo.append("\n");
								
				if (rowset.getString("PAGE_STATUS") != null && !rowset.getString("PAGE_STATUS").equals(""))
					pageInfo.append("Experiment Status: " + rowset.getString("PAGE_STATUS") + "\n");
				if (rowset.getString("PROJECT_CODE") != null && !rowset.getString("PROJECT_CODE").equals("")) {
					desc = CodeTableCache.getCache().getProjectsDescription(rowset.getString("PROJECT_CODE"));
					if (desc != null)
						pageInfo.append("Project: " + rowset.getString("PROJECT_CODE") + " - " + desc + "\n");
					else
						pageInfo.append("Project: " + rowset.getString("PROJECT_CODE") + "\n");
				}
				if (rowset.getString("TA_CODE") != null && !rowset.getString("TA_CODE").equals("")) {
					desc = CodeTableCache.getCache().getTAsDescription(rowset.getString("TA_CODE"));
					if (desc != null)
						pageInfo.append("TA: " + desc + "\n");
				}
				if (rowset.getString("SUBJECT") != null && !rowset.getString("SUBJECT").equals(""))
					pageInfo.append("Subject: " + rowset.getString("SUBJECT") + "\n");
				if (rowset.getString("LITERATURE_REF") != null && !rowset.getString("LITERATURE_REF").equals(""))
					pageInfo.append("Lit Ref.: " + rowset.getString("LITERATURE_REF") + "\n");
				if (continued_from_rxn != null && !continued_from_rxn.equals(""))
					pageInfo.append("Continued From: " + continued_from_rxn + "\n");
				if (continued_to_rxn != null && !continued_to_rxn.equals(""))
					pageInfo.append("Continued To: " + continued_to_rxn + "\n");
				if (rowset.getString("LOOK_N_FEEL") != null && !rowset.getString("LOOK_N_FEEL").equals("")) {
					String pageType = rowset.getString("LOOK_N_FEEL");
					reactionPageInfo.setPageType(pageType);
					pageInfo.append("Page Type: " + pageType + "\n");
				}
				
				String rxnSchemeKey = rowset.getString("RXN_SCHEME_KEY");
				if (CommonUtils.isNotNull(rxnSchemeKey)) {
					reactionPageInfo.setReactionSchemeKey(rxnSchemeKey);
				}
				// TODO: list registered PF's.
				reactionPageInfo.setPageInfo(pageInfo.toString());
				pagesList.add(reactionPageInfo);
			}
		} catch (Exception e) {
			log.error(e);
		}
		
		return pagesList;
	}
}
