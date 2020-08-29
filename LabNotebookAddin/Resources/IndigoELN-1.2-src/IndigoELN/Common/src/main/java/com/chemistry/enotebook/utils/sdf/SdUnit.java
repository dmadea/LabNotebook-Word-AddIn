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
package com.chemistry.enotebook.utils.sdf;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SdUnit implements Serializable, Externalizable {
	class arrayComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			if (!(o1 instanceof String))
				return 0;
			if (!(o2 instanceof String))
				return 0;
			String s1 = (String) o1;
			String s2 = (String) o2;
			Object o = organizer.get(s1.toUpperCase());
			int val1;
			if (o == null)
				val1 = 0x7fffffff;
			else
				val1 = ((Integer) o).intValue();
			o = organizer.get(s2.toUpperCase());
			int val2;
			if (o == null)
				val2 = 0x7fffffff;
			else
				val2 = ((Integer) o).intValue();
			return val1 - val2;
		}

		String order[];
		HashMap organizer;

		arrayComparator(String order[]) {
			this.order = null;
			organizer = new HashMap();
			this.order = order;
			for (int x = 0; x <= order.length - 1; x++)
				organizer.put(order[x].toUpperCase(), new Integer(x));

		}
	}

	class alphaComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			if (!(o1 instanceof String))
				return 0;
			if (!(o2 instanceof String)) {
				return 0;
			} else {
				String s1 = (String) o1;
				String s2 = (String) o2;
				return s1.compareToIgnoreCase(s2);
			}
		}

		alphaComparator() {
		}
	}

	public SdUnit(String molecule) {
		this(molecule, false);
	}

	public SdUnit(String molecule, boolean molFilePortionOnly) {
		this(molecule, true, molFilePortionOnly);
	}

	public SdUnit(String molecule, boolean allKeysToUpperCase,
			boolean molFilePortionOnly) {
		molPortion = "";
		infoPortion = null;
		keyList = new ArrayList();
		valid = true;
		validString = "OK";
		is3D = false;
		numAtoms = -1;
		upperCase = true;
		rawInput = "";
		init(molecule, allKeysToUpperCase, molFilePortionOnly);
	}

	public SdUnit() {
		molPortion = "";
		infoPortion = null;
		keyList = new ArrayList();
		valid = true;
		validString = "OK";
		is3D = false;
		numAtoms = -1;
		upperCase = true;
		rawInput = "";
	}

	public void init(String molecule, boolean allKeysToUpperCase, boolean molFilePortionOnly) {
		try {		
			if (molecule == null) {
				valid = false;
				validString = "Input molecule was NULL";
				return;
			}
			rawInput = molecule;
			upperCase = allKeysToUpperCase;
			if (molecule.indexOf("\r") >= 0)
				molecule = createConsistentLineTermination(molecule);
			if (molFilePortionOnly) {
				molecule = molecule.substring(0, molecule.indexOf("M  END") + 6);
				molecule = molecule + "\n\n$$$$";
			}
			if (molecule.indexOf("M  END") < 0 || molecule.indexOf("$$$$") < 0) {
				valid = false;
				validString = "Does not contain \"M  END\" or \"$$$$\"";
				molPortion = "Not a valid molecule!";
			}
			if (molecule.indexOf("M  END") >= 0)
				setMol(molecule.substring(0, molecule.indexOf("M  END") + 6) + "\n");
			validString = validateDetail(molPortion);
			if (!validString.startsWith("OK"))
				valid = false;
				infoPortion = parseInfo(molecule, allKeysToUpperCase, keyList);
		} catch (IllegalArgumentException e) {
			valid = false;
			if (validString.startsWith("OK"))
				validString = e.getMessage();
			else
				validString = validString + " AND " + e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String createConsistentLineTermination(String in) throws IOException {
		StringBuffer out;
		StringReader sr = new StringReader(in);
		BufferedReader br = new BufferedReader(sr);
		String line = "";
		out = new StringBuffer();
		while ((line = br.readLine()) != null) {
			out.append(line);
			out.append("\n");
		}
		return out.toString();
	}

	public static SdUnit getEmptyUnit() {
		return new SdUnit(
				"\n  -ISIS-  06030214412D\n\n  0  0  0  0  0  0  0  0  0  0999 V2000\nM  END\n",
				true);
	}

	public boolean containsKey(String key) {
		if (key == null)
			return false;
		else
			return infoPortion.containsKey(key.toUpperCase());
	}

	public String getMolNameString() {
		if (!isValidMol()) {
			return "Invalid Molecule";
		} else {
			String out = "";
			out = molPortion.substring(0, molPortion.indexOf("\n"));
			return out;
		}
	}

	public void setMolNameString(String name) {
		if (!isValidMol())
			return;
		if (name != null && name.indexOf("\n") < 0) {
			StringBuffer sb = new StringBuffer(name);
			sb.append("\n");
			sb.append(molPortion.substring(molPortion.indexOf("\n") + 1));
			molPortion = sb.toString();
		}
	}

	public void setMolCommentsString(String comments) {
		int firstNewLine = molPortion.indexOf("\n");
		int secondNewLine = molPortion.indexOf("\n", firstNewLine + 1);
		int thirdNewLine = molPortion.indexOf("\n", secondNewLine + 1);
		if (!isValidMol())
			return;
		if (comments != null && comments.indexOf("\n") < 0) {
			StringBuffer sb = new StringBuffer(molPortion.substring(0,
					secondNewLine));
			sb.append("\n");
			sb.append(comments);
			sb.append(molPortion.substring(thirdNewLine));
			molPortion = sb.toString();
		}
	}

	public String getMolCommentsString() {
		int firstNewLine = molPortion.indexOf("\n");
		int secondNewLine = molPortion.indexOf("\n", firstNewLine + 1);
		int thirdNewLine = molPortion.indexOf("\n", secondNewLine + 1);
		return molPortion.substring(secondNewLine + 1, thirdNewLine);
	}

	public boolean isMol3D() {
		return is3D;
	}

//	public static String encodeUnit(SdUnit unit) throws IOException {
//		if (!unit.isValidMol())
//			return "Invalid Molecule";
//		else
//			return encodeString(unit.getUnit());
//	}
//
//	private static String encodeString(String s) throws IOException {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		GZIPOutputStream gzos = new GZIPOutputStream(baos);
//		gzos.write(s.getBytes());
//		gzos.flush();
//		gzos.finish();
//		gzos.close();
//		baos.close();
//		byte bin[] = baos.toByteArray();
//		String enc = Base64.encodeBytes(bin);
//		return enc;
//	}
//
//	public static SdUnit decodeUnit(String eunit) throws IOException {
//		String molstr = decodeString(eunit);
//		SdUnit unit = null;
//		if (molstr.indexOf("$$$$") < 0)
//			unit = new SdUnit(molstr, true);
//		else
//			unit = new SdUnit(molstr);
//		return unit;
//	}
//
//	public static String synthesisPlanEncodeUnit(SdUnit unit) throws IOException {
//		if (!unit.isValidMol())
//			return "Invalid Molecule";
//		else
//			return encodeSynthesisPlanString(unit.toString());
//	}
//
//	public static SdUnit synthesisPlanDecodeUnit(String encodedMol) throws IOException {
//		String sdString = decodeSynthesisPlanString(encodedMol);
//		if (sdString.indexOf("$$$$") < 0)
//			return new SdUnit(sdString, true);
//		else
//			return new SdUnit(sdString);
//	}
//
//	public static String unitToChimeString(SdUnit unit) throws Exception {
//		Class chimePro = Class.forName("com.mdli.chime.ChimePro");
//		Object cp = chimePro.newInstance();
//		Method getCS = chimePro.getMethod("getChimeString", new Class[0]);
//		Method setMol = chimePro.getMethod("setMolfileData",
//				new Class[] { java.lang.String.class });
//		setMol.invoke(cp, new Object[] { unit.toString() });
//		String chimeString = (String) getCS.invoke(cp, new Object[0]);
//		return chimeString;
//	}
//
//	public static SdUnit unitFromChimeString(String chimeString)
//			throws Exception {
//		Class chimePro = Class.forName("com.mdli.chime.ChimePro");
//		Object cp = chimePro.newInstance();
//		Method setCS = chimePro.getMethod("setChimeString",
//				new Class[] { java.lang.String.class });
//		Method getMol = chimePro.getMethod("getMolfileData", new Class[0]);
//		setCS.invoke(cp, new Object[] { chimeString });
//		String mol = (String) getMol.invoke(cp, new Object[0]);
//		if (mol.indexOf("$$$$") > 0)
//			return new SdUnit(mol);
//		else
//			return new SdUnit(mol, true);
//	}
//
//	private static String decodeString(String s) throws IOException {
//		byte bin[] = Base64.decode(s);
//		ByteArrayInputStream bais = new ByteArrayInputStream(bin);
//		GZIPInputStream gzis = new GZIPInputStream(bais);
//		byte buffer[] = new byte[bin.length * 100];
//		int read = 0;
//		int count;
//		for (count = 0; (read = gzis.read()) >= 0; count++)
//			buffer[count] = (byte) read;
//
//		gzis.close();
//		bais.close();
//		bin = new byte[count - 1];
//		for (int x = 0; x <= bin.length - 1; x++)
//			bin[x] = buffer[x];
//
//		return new String(bin);
//	}
//
//	private static String decodeSynthesisPlanString(String s) throws IOException {
//		ByteArrayOutputStream bos = null;
//		try {
//			byte bin[] = Base64.decode(s);
//			Inflater inflater = new Inflater(true);
//			inflater.setInput(bin);
//			bos = new ByteArrayOutputStream(bin.length);
//			byte buf[] = new byte[bin.length * 100];
//			int count = -1;
//			do {
//				if (inflater.finished())
//					break;
//				count = inflater.inflate(buf);
//				if (count == 0)
//					break;
//				bos.write(buf, 0, count);
//			} while (true);
//			bos.close();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return "";
//		}
//		return new String(bos.toByteArray());
//	}
//
//	private static String encodeSynthesisPlanString(String s) throws IOException {
//		ByteArrayOutputStream bos = null;
//		try {
//			byte bin[] = s.getBytes("US-ASCII");
//			Deflater deflater = new Deflater(9, true);
//			deflater.setInput(bin);
//			deflater.finish();
//			bos = new ByteArrayOutputStream(bin.length);
//			byte buf[] = new byte[1024];
//			int count;
//			for (; !deflater.finished(); bos.write(buf, 0, count))
//				count = deflater.deflate(buf);
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return "";
//		}
//		String result = Base64.encodeBytes(bos.toByteArray());
//		return result;
//	}

	public String getValue(String key) {
		if (valid)
			return (String) infoPortion.get(key.toUpperCase());
		else
			return "";
	}

	public void setValue(String key, String value) {
		if (valid)
			if (value == null || value.trim().equals("")) {
				removeKey(key);
				infoPortion.remove(key.toUpperCase());
			} else {
				infoPortion.put(key.toUpperCase(), value);
				replaceKey(key);
			}
	}

//	public void sortKeysAlpha() {
//		Collections.sort(keyList, new alphaComparator());
//	}
//
//	public void sortKeysByArray(String order[]) {
//		arrayComparator ac = new arrayComparator(order);
//		Collections.sort(keyList, ac);
//	}
//
//	public void sortKeysByAny(Comparator c) {
//		Collections.sort(keyList, c);
//	}

	private void removeKey(String key) {
		int len = keyList.size();
		for (int x = len - 1; x >= 0; x--) {
			String s = (String) keyList.get(x);
			if (s.equalsIgnoreCase(key))
				keyList.remove(x);
		}

	}

	private void replaceKey(String key) {
		int len = keyList.size();
		boolean replaced = false;
		int x = 0;
		do {
			if (x > len - 1)
				break;
			String s = (String) keyList.get(x);
			if (s.equalsIgnoreCase(key)) {
				keyList.set(x, key);
				replaced = true;
				break;
			}
			x++;
		} while (true);
		if (!replaced)
			keyList.add(key);
	}

	public void removeItem(String key) {
		if (valid) {
			infoPortion.remove(key.toUpperCase());
			removeKey(key);
		}
	}

	public int getAtomCount() {
		return numAtoms;
	}

	public String getMol() {
		if (valid)
			return molPortion;
		else
			return "";
	}

	public void setMol(String mol) {
		if (mol.indexOf("M  END") > 0)
			mol = mol.substring(0, mol.indexOf("M  END") + "M  END".length())
					+ "\n";
		String tmp = validateDetail(mol);
		if (tmp.startsWith("OK")) {
			if (tmp.indexOf("3D") > 0)
				is3D = true;
			String num = tmp.substring(tmp.lastIndexOf(" ") + 1).trim();
			try {
				numAtoms = Integer.parseInt(num);
			} catch (NumberFormatException nfe) {
				numAtoms = -1;
			}
			tmp = "OK";
		}
		if (!tmp.equals("OK"))
			if (validString.startsWith("OK"))
				validString = tmp;
			else
				validString = validString + " AND UPON MOL MODIFICATION " + tmp;
		if (valid)
			try {
				molPortion = createConsistentLineTermination(mol);
			} catch (Exception e) {}
	}

	public boolean isValidMol() {
		return valid;
	}

	public String getInvalidDescription() {
		return validString;
	}

	public String[] getKeys() {
		Object o[] = keyList.toArray();
		String out[] = new String[o.length];
		for (int x = 0; x <= o.length - 1; x++)
			out[x] = (String) o[x];

		return out;
	}

	public String getUnit() {
		String keys[] = getKeys();
		StringBuffer out = new StringBuffer();
		for (int x = 0; x <= keys.length - 1; x++) {
			String value = getValue(keys[x]);
			out.append(">  <");
			out.append(keys[x]);
			out.append(">\n");
			out.append(value);
			out.append("\n\n");
		}

		if (keys.length == 0)
			out.append("\n");
		out.append("$$$$\n");
		out.insert(0, molPortion);
		return out.toString();
	}

	public String getUnitWithTag(String tagkey) {
		String taginfo = "";
		taginfo = getValue(tagkey);
		if (taginfo == null) {
			taginfo = "";
		} else {
			StringBuffer sb = new StringBuffer(taginfo);
			sb.insert(0, " (");
			sb.append(")");
			taginfo = sb.toString();
		}
		String keys[] = getKeys();
		StringBuffer out = new StringBuffer();
		for (int x = 0; x <= keys.length - 1; x++) {
			String value = getValue(keys[x]);
			out.append(">  <");
			out.append(keys[x]);
			out.append(">");
			out.append(taginfo);
			out.append("\n");
			out.append(value);
			out.append("\n\n");
		}

		if (keys.length == 0)
			out.append("\n");
		out.append("$$$$\n");
		out.insert(0, molPortion);
		return out.toString();
	}

	private HashMap parseInfo(String sdInfo, boolean allKeysToUpperCase,
			ArrayList origNames) {
		HashMap out = new HashMap();
		try {
			String attrPortion = sdInfo.substring(sdInfo.indexOf("M  END") + 6,
					sdInfo.indexOf("$$$$") + 4).trim();
			String thisName = "";
			String thisOrigName = "";
			String thisValue = "";
			do {
				if (attrPortion.indexOf(">") != 0
						|| attrPortion.indexOf("<") <= 0)
					break;
				attrPortion = attrPortion
						.substring(attrPortion.indexOf("<") + 1);
				thisName = attrPortion.substring(0, attrPortion.indexOf(">"))
						.trim();
				thisOrigName = thisName;
				thisName = thisName.toUpperCase();
				attrPortion = attrPortion.substring(attrPortion.indexOf("\n"))
						.trim();
				if (attrPortion.indexOf(">") > 0)
					thisValue = attrPortion.substring(0,
							attrPortion.indexOf("\n>")).trim();
				else if (attrPortion.trim().indexOf(">") == 0
						&& attrPortion.indexOf("<") > 0)
					thisValue = "";
				else
					thisValue = attrPortion.substring(0,
							attrPortion.indexOf("$$$$")).trim();
				if (thisValue != null && !thisValue.trim().equals(""))
					if (out.containsKey(thisName)) {
						String tmp = (String) out.get(thisName);
						if (tmp != null && !tmp.equals("")) {
							StringBuffer sb = new StringBuffer(tmp);
							sb.append("\n");
							sb.append(thisValue);
							out.put(thisName, sb.toString());
						} else {
							out.put(thisName, thisValue);
							origNames.add(thisOrigName);
						}
					} else {
						out.put(thisName, thisValue);
						origNames.add(thisOrigName);
					}
				if (attrPortion.indexOf(">  <") != 0
						&& attrPortion.indexOf("> <") != 0
						&& attrPortion.indexOf("\n>") > 0)
					attrPortion = attrPortion.substring(attrPortion
							.indexOf("\n>") + 1);
			} while (true);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Error parsing sdFile attributes");
		}
		return out;
	}

	public HashMap getDataAsStringArrayHashMap() {
		HashMap out = new HashMap();
		out.put("$MOLFILEDATA$", getMol());
		Set keys = infoPortion.keySet();
		Object thiskey;
		String originalvalue;
		for (Iterator i = keys.iterator(); i.hasNext(); out.put(thiskey,
				parseStringArray(originalvalue))) {
			thiskey = i.next();
			originalvalue = (String) infoPortion.get(thiskey);
		}

		return out;
	}

	public String toString() {
		return getUnit();
	}

	public String toString(String lineTermination) throws IOException {
		StringBuffer out;
		StringReader sr = new StringReader(getUnit());
		BufferedReader br = new BufferedReader(sr);
		out = new StringBuffer();
		for (String line = ""; (line = br.readLine()) != null;) {
			out.append(line);
			out.append(lineTermination);
		}

		br.close();
		sr.close();
		return out.toString();
	}

	private String[] parseStringArray(String in) {
		String out[] = null;
		if (in.indexOf("\n") < 0) {
			out = new String[1];
			out[0] = in;
			return out;
		}
		StringTokenizer st = new StringTokenizer(in, "\n");
		ArrayList al = new ArrayList();
		String s;
		for (; st.hasMoreTokens(); al.add(s))
			s = st.nextToken();

		out = new String[al.size()];
		for (int x = 0; x <= out.length - 1; x++)
			out[x] = (String) al.get(x);

		return out;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		writeExternal(out);
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		readExternal(in);
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(4000);
		for (int byt = 0; (byt = in.read()) != -1;)
			baos.write(byt);

		baos.close();
		byte bytes[] = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		GZIPInputStream gis = new GZIPInputStream(bais);
		ByteArrayOutputStream unit = new ByteArrayOutputStream();
		int chunk = 0;
		boolean isMolData = true;
		while ((chunk = gis.read()) >= 0)
			unit.write(chunk);
		unit.close();
		String info = new String(unit.toByteArray());
		bais.close();
		gis.close();
		init(info, true, false);
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		String text = getUnit();
		byte btext[] = text.getBytes();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(btext.length);
		GZIPOutputStream gzo = new GZIPOutputStream(baos);
		gzo.write(btext);
		gzo.finish();
		baos.close();
		byte bytes[] = baos.toByteArray();
		out.write(bytes);
		out.flush();
	}

	static String validateDetail(String mol) {
		String returnString = "OK";
		int numAtoms = 0;
		try {
			if (mol.indexOf("M  END") <= 0)
				return "Does not contain \"M  END\"";
			StringReader sr = new StringReader(mol);
			BufferedReader br = new BufferedReader(sr);
			String line = br.readLine();
			if (line == null)
				return "Molecule has too few lines";
			line = br.readLine();
			if (line == null)
				return "Molecule has too few lines";
			line = br.readLine();
			if (line == null)
				return "Molecule has too few lines";
			line = br.readLine();
			if (line == null)
				return "Molecule has too few lines";
			int impossibleNumber = -1;
			numAtoms = impossibleNumber;
			int numBonds = impossibleNumber;
			try {
				numAtoms = Integer.parseInt(line.substring(0, 3).trim());
			} catch (NumberFormatException e) {
				return "Number of atoms is invalid";
			}
			if (numAtoms <= impossibleNumber)
				return "Number of atoms is invalid";
			try {
				numBonds = Integer.parseInt(line.substring(3, 6).trim());
			} catch (NumberFormatException e) {
				return "Number of bonds is invalid";
			}
			if (numAtoms <= impossibleNumber)
				return "Number of bonds is invalid";
			double improbablyLargeValue = 10000.0D;
			for (int x = 0; x <= numAtoms - 1; ++x) {
				line = br.readLine();
				if (line == null)
					return "Incorrect number of atoms and/or bonds";
				double test = improbablyLargeValue;
				try {
					test = Double.parseDouble(line.substring(0, 10).trim());
				} catch (NumberFormatException e) {
					return "Invalid X coordinate";
				}
				if (line.indexOf(".") != 5)
					return "X coordinate decimal in wrong place";
				if (Math.abs(test) >= improbablyLargeValue)
					return "Invalid X coordinate";
				test = improbablyLargeValue;
				try {
					test = Double.parseDouble(line.substring(10, 20).trim());
				} catch (NumberFormatException e) {
					return "Invalid Y coordinate";
				}
				if (Math.abs(test) >= improbablyLargeValue)
					return "Invalid Y coordinate";
				test = improbablyLargeValue;
				try {
					test = Double.parseDouble(line.substring(20, 30).trim());
				} catch (NumberFormatException e) {
					return "Invalid Z coordinate";
				}
				if (Math.abs(test) >= improbablyLargeValue)
					return "Invalid Z coordinate";
				if (test == 0.0D)
					continue;
				returnString = "OK 3D";
			}
			int impossibleAtomNumber = 0;
			for (int x = 0; x <= numBonds - 1; ++x) {
				line = br.readLine();
				if (line == null)
					return "Incorrect number of atoms and/or bonds";
				if (line.startsWith("M")) {
					System.out.println(mol);
					return "Incorrect number of atoms and/or bonds";
				}
				if (line.length() < 12)
					return "Invalid Bond Line - too short";
				int test = impossibleAtomNumber;
				try {
					test = Integer.parseInt(line.substring(0, 3).trim());
				} catch (NumberFormatException e) {
					return "Invalid Bond Line - invalid atom1 number";
				}
				if (test <= impossibleAtomNumber)
					return "Invalid Bond Line - invalid atom1 number";
				test = impossibleAtomNumber;
				try {
					test = Integer.parseInt(line.substring(3, 6).trim());
				} catch (NumberFormatException e) {
					return "Invalid Bond Line - invalid atom2 number";
				}
				if (test <= impossibleAtomNumber)
					return "Invalid Bond Line - invalid atom2 number";
			}
			line = br.readLine();
			if (line == null)
				return "Molecule has too few lines";
		} catch (Exception e) {
			return "Unexpected error parsing molecule";
		}
		if (returnString.startsWith("OK"))
			returnString = returnString + " " + Integer.toString(numAtoms);
		return returnString;
	}
	 
	static final String emptyMol = "\n  -ISIS-  06030214412D\n\n  0  0  0  0  0  0  0  0  0  0999 V2000\nM  END\n";
	transient String molPortion;
	transient HashMap infoPortion;
	transient ArrayList keyList;
	transient boolean valid;
	transient String validString;
	public static final String structureTerminator = "M  END";
	boolean is3D;
	int numAtoms;
	static final long serialVersionUID = 42L;
	boolean upperCase;
	public transient String rawInput;
}
