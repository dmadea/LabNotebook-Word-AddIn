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
package com.chemistry.enotebook.experiment.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 
 *
 */
public class NotebookPageUtil {
	public static final int NB_REF_MAX_LENGTH = 13;
	public static final int NB_NUMBER_MAX_LENGTH = 8;
	public static final int NB_PAGE_MAX_LENGTH = 4;
	public static final int NB_BATCH_LOT_MAX_LENGTH = 6;
	public static final int NB_BATCH_NUMBER_MAX_LENGTH = 20;

	public static final int NB_PAGE_GROUP_SIZE = 50;

	public static final int NB_MAX_PAGE_NUMBER = ((int) Math.pow(10.0, (double) NB_PAGE_MAX_LENGTH) - 1);

	private static final int INDEX_OF_NB_NUMBER = 0;
	private static final int INDEX_OF_NB_PAGE = 1;
	private static final int INDEX_OF_LOT_NUMBER = 2;
	private static final int NB_REF_SPLIT_ARRAY_SIZE = 1; // zero based numbering
	private static final int NB_BATCH_NUMBER_SPLIT_ARRAY_SIZE = 2; // zero based numbering

	public static final String NB_REF_TOKEN = "-";
	public static final String NB_REF_VERSION_TOKEN = "v";
	public static final char NB_NUMBER_PAD_CHAR = '0';

	// public static final char[] NUMBERS = {'0','1','2','3','4','5','6','7','8','9'};
	public static final int INVALID_NUMBER = -1;
	public static final Pattern onlyDigitsPattern = Pattern.compile("[0-9]+");
	public static final Pattern notebookRefPattern = Pattern.compile("([0-9]{1,8})-([0-9]{1," + NB_PAGE_MAX_LENGTH + "})(v[1-9]+)?");
	public static final Pattern notebookNumberPattern = Pattern.compile("([0-9]{1,8})");
	public static final Pattern notebookPagePattern = Pattern.compile("([0-9]{1," + NB_PAGE_MAX_LENGTH + "})");
	public static final Pattern cenBatchNumberPattern = Pattern.compile("([0-9]{1,8})-([0-9]{1," + NB_PAGE_MAX_LENGTH
			+ "})-([0-9A-Za-z]{1,6})");
	public static final Pattern cenBatchLotNumberPattern = Pattern.compile("([0-9A-Za-z]{1,6})");

	// Used by stoich to differentiate Compound Lookup Services.
	public static final Pattern regNumberPattern = Pattern.compile("ABC-[0-9]{1,8}-[0-9A-Za-z]{1,2}");
	public static final Pattern parentRegNumberPattern = Pattern.compile("ABC-[0-9]{1,8}");
	public static final Pattern conversationalBatchNumberPattern = Pattern.compile("ABC-[0-9]{1,8}-[0-9]{1,2}-[0-9]{1,6}");
	public static final Pattern cenBatchExtendedIdPattern = Pattern.compile("[0-9]{0,8}-[0-9]{0," + NB_PAGE_MAX_LENGTH + "}-[0-9A-Za-z]{0,6}_[0-9A-Za-z]{1,40}");

	/**
	 * Picks off the first parts of the batch number as the notebook reference. Will format the number. Call formatNotebookRef to
	 * get a formatted version of the notebookRef before calling this function if the padded version is desired.
	 * 
	 * @param notebookRef -
	 *            full string of notebookReference
	 * @return String - representing the Notebook number associated with the reference
	 */
	public static String getNotebookRefFromBatchNumber(String batchNumber) {
		// format for a notebook reference should be notebook
		// number followed by a dash and an experiment number
		String[] result = batchNumber.split("-");
		if (result.length > 0 && batchNumber.indexOf(NB_REF_TOKEN) > 0) {
			StringBuffer nbRef = new StringBuffer(result[INDEX_OF_NB_NUMBER]);
			if (nbRef.length() < NotebookPageUtil.NB_NUMBER_MAX_LENGTH)
				nbRef.insert(0, NotebookPageUtil.getStringPad(NB_NUMBER_PAD_CHAR, (NB_NUMBER_MAX_LENGTH - nbRef.length())));
			StringBuffer nbPage = new StringBuffer(result[INDEX_OF_NB_PAGE]);
			nbRef.append("-");
			if (nbPage.length() < NotebookPageUtil.NB_PAGE_MAX_LENGTH)
				nbPage.insert(0, NotebookPageUtil.getStringPad(NB_NUMBER_PAD_CHAR, (NB_PAGE_MAX_LENGTH - nbPage.length())));
			// will pad the whole thing out if necessary. ex: "0000"
			nbRef.append(nbPage);
			return nbRef.toString(); // returns the first part of the string
		}
		return Integer.toString(INVALID_NUMBER);
	}

	/**
	 * Picks off the first part of the notebook reference as the notebook number. Will format the number. Call formatNotebookRef to
	 * get a formatted version of the notebookRef before calling this function if the padded version is desired.
	 * 
	 * @param notebookRef -
	 *            full string of notebookReference
	 * @return String - representing the Notebook number associated with the reference
	 */
	public static String getNotebookNumberFromNotebookRef(String notebookRef) {
		// format for a notebook reference should be notebook
		// number followed by a dash and an experiment number
		String[] result = notebookRef.split("-");
		if (result.length > 0 && notebookRef.indexOf(NB_REF_TOKEN) > 0) {
			StringBuffer nbNumber = new StringBuffer(result[INDEX_OF_NB_NUMBER]);
			if (nbNumber.length() < NotebookPageUtil.NB_NUMBER_MAX_LENGTH)
				nbNumber.insert(0, NotebookPageUtil.getStringPad(NB_NUMBER_PAD_CHAR, (NB_NUMBER_MAX_LENGTH - nbNumber.length())));
			return nbNumber.toString(); // returns the first part of the string
		}
		return Integer.toString(INVALID_NUMBER);
	}

	/**
	 * Picks off the second part of the string as the page number Will not format the number. Call formatNotebookRef to get a
	 * formatted version of the notebookRef before calling this function if the padded version is desired.
	 * 
	 * @param notebookRef
	 * @return String - length should not be altered from entered state.
	 */
	public static String getNotebookPageFromNotebookRef(String notebookRef) {
		// format for a notebook reference should be notebook
		// number followed by a dash and an experiment number
		String[] result = notebookRef.split("-");
		if (result.length > 0 && notebookRef.indexOf(NB_REF_TOKEN) > 0) {
			String number = result[1].split(NB_REF_VERSION_TOKEN)[0];
			StringBuffer nbNumber = new StringBuffer(number);
			if (nbNumber.length() < NotebookPageUtil.NB_PAGE_MAX_LENGTH)
				nbNumber.insert(0, NotebookPageUtil.getStringPad(NB_NUMBER_PAD_CHAR, (NB_PAGE_MAX_LENGTH - nbNumber.length())));
			return nbNumber.toString(); // returns the first part of the string
		}
		return Integer.toString(INVALID_NUMBER);
	}

	public static int getNotebookPageVersionFromNotebookRef(String notebookRef) {
		String[] result = notebookRef.split(NB_REF_VERSION_TOKEN);
		if (result.length > 1 && notebookRef.indexOf(NB_REF_VERSION_TOKEN) > 0) {
			return Integer.parseInt(result[1]);
		}
		return 1;
	}
	
	/**
	 * Picks off the first part of the notebook reference as the notebook number. Will not format the number. Call formatBatchNumber
	 * to get a formatted version of the notebookRef before calling this function if the padded version is desired.
	 * 
	 * @param batchNumber
	 * @return String - representing the Notebook number associated with the reference
	 */
	public static String getNotebookNumberFromBatchNumber(String batchNumber) {
		// format for a batchNumber should be notebook
		// number followed by a dash and a possible experiment number
		// and a XXXXXX character lot number
		String[] result = batchNumber.split("-");
		if (result.length > 0 && batchNumber.indexOf(NB_REF_TOKEN) > 0) {
			StringBuffer nbNumber = new StringBuffer(result[INDEX_OF_NB_NUMBER]);
			if (nbNumber.length() >= NotebookPageUtil.NB_PAGE_MAX_LENGTH)
				nbNumber.insert(0, NotebookPageUtil.getStringPad(NB_NUMBER_PAD_CHAR, (NB_NUMBER_MAX_LENGTH - nbNumber.length())));
			return nbNumber.toString(); // returns the first part of the string
		}
		return Integer.toString(INVALID_NUMBER);
	}

	/**
	 * Picks off the second part of the string as the page number Will not format the number. Call formatBatchNumber to get a
	 * formatted version of the notebookRef before calling this function if the padded version is desired.
	 * 
	 * @param batchNumber
	 * @return String - length should not be altered from entered state.
	 */
	public static String getNotebookPageFromBatchNumber(String batchNumber) {
		// format for a batchNumber should be notebook
		// number followed by a dash and a possible experiment number
		// and a XXXXXX character lot number
		String[] result = batchNumber.split("-");
		if (result.length >= 1)
			return result[1]; // returns the second part of the string
		return Integer.toString(INVALID_NUMBER);
	}

	/**
	 * Picks off the third part of the string as the lot number Will not format the number. Call formatBatchNumber to get a
	 * formatted version of the notebookRef before calling this function if the padded version is desired.
	 * 
	 * @param batchNumber
	 * @return String - length should not be altered from entered state.
	 */
	public static String getLotNumberFromBatchNumber(String batchNumber) {
		// format for a batchNumber should be notebook
		// number followed by a dash and a possible experiment number
		// and a XXXXXX character lot number
		String[] result = batchNumber.split("-");
		if (result.length >= 2)
			return result[2]; // returns the third part of the string
		return Integer.toString(INVALID_NUMBER);
	}

	public static String prePadNumber(String inputNumber, int desiredLength) {
		String output = getStringPad(desiredLength) + inputNumber;
		return output;
	}

	public static String prePadNumber(String inputNumber, char pad, int desiredLength) {
		String output = getStringPad(pad, desiredLength) + inputNumber;
		return output;
	}

	/**
	 * 
	 * @param notebookRef
	 * @return
	 */
	public static String formatNotebookRef(String notebookRef) {
		StringBuffer retVal = new StringBuffer(Integer.toString(INVALID_NUMBER));
		if (isValidNotebookRef(notebookRef)) {
			retVal = new StringBuffer();
			String[] result = notebookRef.split(NB_REF_TOKEN);

			// Munge Notebook Number
			String nbNumber = result[INDEX_OF_NB_NUMBER];
			nbNumber = prePadNumber(nbNumber, NB_NUMBER_MAX_LENGTH - nbNumber.length());
			retVal.append(nbNumber);
			retVal.append(NB_REF_TOKEN);

			// Munge Notebook Page
			String pageNumber = result[INDEX_OF_NB_PAGE];
			pageNumber = prePadNumber(pageNumber, NB_PAGE_MAX_LENGTH - pageNumber.length());
			retVal.append(pageNumber);
		}
		return retVal.toString();
	}

	/**
	 * 
	 * @param notebookRef
	 * @return
	 */
	public static String formatNotebookNumber(String nbNumber) {
		if (isValidNotebookNumber(nbNumber))
			nbNumber = prePadNumber(nbNumber, NB_NUMBER_MAX_LENGTH - nbNumber.length());
		return nbNumber;
	}

	/**
	 * 
	 * @param notebookRef
	 * @return
	 */
	public static String formatNotebookPage(String nbPage) {
		if (isValidNotebookPage(nbPage))
			nbPage = prePadNumber(nbPage, NB_PAGE_MAX_LENGTH - nbPage.length());
		return nbPage;
	}

	/**
	 * 
	 * @param batchNumber
	 * @return
	 */
	public static String formatBatchNumber(String batchNumber) {
		StringBuffer retVal = new StringBuffer(Integer.toString(INVALID_NUMBER));
		if (isValidCeNBatchNumber(batchNumber)) {
			String[] result = batchNumber.split(NB_REF_TOKEN);
			String nbNumber = formatNotebookRef(result[INDEX_OF_NB_NUMBER] + NB_REF_TOKEN + result[INDEX_OF_NB_PAGE]);

			// Munge Notebook Page
			String lotNumber = result[INDEX_OF_LOT_NUMBER];
			lotNumber = prePadNumber(lotNumber, NB_BATCH_LOT_MAX_LENGTH - lotNumber.length());
			retVal.append(lotNumber);
			retVal = new StringBuffer(nbNumber + NB_REF_TOKEN + lotNumber);
		}
		return retVal.toString();
	}

	/**
	 * Creates a string of NB_NUMBER_PAD_CHAR to be used for whatever
	 * 
	 * @param desiredLength -
	 *            length of pad string
	 * @return String - of desiredLength of NB_NUMBER_PAD_CHAR
	 */
	public static String getStringPad(int desiredLength) {
		return getStringPad(NB_NUMBER_PAD_CHAR, desiredLength);
	}

	/**
	 * Creates a string of padChar to be used for whatever
	 * 
	 * @param padChar
	 * @param desiredLength -
	 *            length of pad string
	 * @return String - of desiredLength of padChar
	 */
	public static String getStringPad(char padChar, int desiredLength) {
		StringBuffer retVal = new StringBuffer();
		for (int i = 0; i < desiredLength; i++) {
			retVal.append(padChar);
		}
		return retVal.toString();
	}

	/**
	 * Use this to get the next page in a series.
	 * 
	 * @param nbRef
	 * @return String indicating next notebook page in series or "" if new page is greater than the available amount of pages
	 */
	public static String incrementNotebookPage(String nbRef) {
		StringBuffer newRef = new StringBuffer();
		if (isValidNotebookRef(nbRef)) {
			int page = Integer.parseInt(NotebookPageUtil.getNotebookPageFromNotebookRef(nbRef));
			if (page < NB_MAX_PAGE_NUMBER) {
				page++;
				newRef.append(NotebookPageUtil.getNotebookNumberFromNotebookRef(nbRef)).append("-").append(
						NotebookPageUtil.formatNotebookPage(Integer.toString(page)));
			}
		}
		return newRef.toString();
	}

	/**
	 * Validates against the regular expression: notebookRefPattern
	 * 
	 * @param value =
	 *            String representation of a notebook reference (notebook number and page)
	 * @return true if whole string matches the pattern. False otherwise.
	 */
	public static boolean isValidNotebookRef(String value) {
		return matchesPattern(value, notebookRefPattern);
	}

	/**
	 * Validates against the regular expression: notebookNumberPattern
	 * 
	 * @param value =
	 *            String representation of a notebook number (does not include experiment number)
	 * @return true if whole string matches the pattern. False otherwise.
	 */
	public static boolean isValidNotebookNumber(String value) {
		return matchesPattern(value, notebookNumberPattern);
	}

	/**
	 * Validates against the regular expression: notebookPagePattern
	 * 
	 * @param value =
	 *            String representation of a notebook page number (does not include notebook number)
	 * @return true if whole string matches the pattern. False otherwise.
	 */
	public static boolean isValidNotebookPage(String value) {
		return matchesPattern(value, notebookPagePattern);
	}

	/**
	 * Validates against the regular expression: cenBatchNumberPattern
	 * 
	 * @param value -
	 *            String consisting of a test batchnumber
	 * @return true if the whole string matches the pattern. False otherwise.
	 */
	public static boolean isValidCeNBatchNumber(String value) {
		return matchesPattern(value, cenBatchNumberPattern);
	}

	/**
	 * Validates against the regular expression: cenBatchExtendedIdPattern
	 * 
	 * @param value -
	 *            String like 11111112-0810-1_fd60fd6a7a2fb3fa34132ff42107ed808f22bf81
	 * @return true if the whole string matches the pattern. False otherwise.
	 */
	public static boolean isValidCeNBatchExtendedIdPattern(String value) {
		return matchesPattern(value, cenBatchExtendedIdPattern);
	}
	
	/**
	 * Validates against the regular expression: cenBatchLotNumberPattern
	 * 
	 * @param testBatchLotNumber
	 * @return true if the whole string matches the pattern. False otherwise.
	 */
	public static boolean isValidCeNBatchLotNumber(String value) {
		return matchesPattern(value, cenBatchLotNumberPattern);
	}

	public static boolean isValidRegNumber(String value) {
		return matchesPattern(value, regNumberPattern);
	}

	public static boolean isValidParentRegNumber(String value) {
		return matchesPattern(value, parentRegNumberPattern);
	}

	public static boolean isValidConversationalBatchNumber(String value) {
		return matchesPattern(value, conversationalBatchNumberPattern);
	}

	/**
	 * Regex used to determine string is all digits
	 * 
	 * @param input
	 * @return true if the whole string matches the pattern. False otherwise.
	 */
	public static boolean isStringANumber(String value) {
		return matchesPattern(value, onlyDigitsPattern);
	}

	public static boolean matchesPattern(String value, Pattern pattrn) {
		boolean result = false;
		if (value != null) {
			Matcher m = pattrn.matcher(value);
			result = m.matches();
		}
		return result;
	}

	public static String formatDate(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss z");
		return df.format(new Date());
	}

	public static String formatDateWithoutTime(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("MMMMM dd, yyyy");
		return df.format(new Date());
	}

	public static String getLocalDateString(String strDate) throws ParseException {
		// SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy HH:mm:ss z");
		// Date dateValue = sdf.parse(strDate);
		strDate = strDate.trim();
		if (!strDate.equals("")) {

			// Try Various date formats, the following being the default, before failing
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss z");
			;
			Date dateValue;
			try {
				dateValue = sdf.parse(strDate); // Try MMM d, yyyy HH:mm:ss z
			} catch (ParseException e) {
				SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
				;
				try {
					dateValue = sdf2.parse(strDate); // Try dd-MMM-yyyy HH:mm:ss
				} catch (ParseException e3) {
					sdf2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss z");
					;
					try {
						dateValue = sdf2.parse(strDate); // Try dd-MMM-yyyy HH:mm:ss z
					} catch (ParseException e4) {
						sdf2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
						;
						try {
							dateValue = sdf2.parse(strDate);
						} catch (ParseException e5) {
							try {
								sdf2 = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss");
								dateValue = sdf2.parse(strDate); // Try yyyy/mm/dd HH:mm:ss
							} catch (ParseException e6) {
								sdf2 = new SimpleDateFormat("MM dd, yyyy HH:mm:ss z");
								dateValue = sdf2.parse(strDate); // MM dd, YYYY HH:mm:ss z
							}
							/*
							 * bug fix 23558, With this kind of validation we may miss something Better way to handle date parse
							 * issue is Use the specified format in the SELECT SQL query,
							 */
						}
					}
				}
			}
			return sdf.format(dateValue);
		} else
			return "";
	}

	public static Date getLocalDate(String strDate) throws ParseException {
		// Try Various date formats, the following being the default, before failing
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss z");
		;
		Date dateValue = null;
		try {
			dateValue = sdf.parse(strDate); // Try MMM d, yyyy HH:mm:ss z
		} catch (ParseException e) {
			SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
			;
			try {
				dateValue = sdf2.parse(strDate); // Try MMM dd, yyyy HH:mm:ss
			} catch (ParseException e1) {
				sdf2 = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
				;
				try {
					dateValue = sdf2.parse(strDate); // Try MMM dd, yyyy hh:mm:ss a
				} catch (ParseException e3) {
					sdf2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss z");
					;
					try {
						dateValue = sdf2.parse(strDate); // Try dd-MMM-yyyy HH:mm:ss z
					} catch (ParseException e4) {
						sdf2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
						;
						try {
							dateValue = sdf2.parse(strDate); // Try dd-MMM-yyyy HH:mm:ss
						} catch (ParseException e5) {
							try {
								sdf2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
								if (strDate.charAt(2) != '/')
									throw new ParseException("", 0);
								dateValue = sdf2.parse(strDate); // Try mm/dd/yyyy HH:mm:ss
							} catch (ParseException e6) {
								try {
									sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
									dateValue = sdf2.parse(strDate); // Try yyyy/mm/dd HH:mm:ss
								} catch (ParseException e7) {
									sdf2 = new SimpleDateFormat("MM dd, yyyy HH:mm:ss z");
									dateValue = sdf2.parse(strDate); // MM dd, YYYY HH:mm:ss z
								}
							}
							/*
							 * bug fix 23558, With this kind of validation we may miss something Better way to handle date parse
							 * issue is Use the specified format in the SELECT SQL query,
							 */
						}
					}
				}
			}
		}

		return dateValue;
	}
}
