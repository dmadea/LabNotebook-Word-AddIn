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
package com.chemistry.enotebook.sdk.utils;

public class ChemistryUtils 
{
	static final String symb[] = {"Ac", "Ag", "Al", "Am", "As", "At", "Au", "B", "Ba", 
		"Be", "Bi", "Bk", "Br", "C", "Ca", "Cd", "Ce", "Cf", "Cl", "Co", "Cr", "Cs", "Cu",
		"Dy", "Er", "Es", "Eu", "F", "Fe", "Ga", "Gd", "Ge", "H", "Hf", "Hg", "Ho", "I",
		"In", "Ir", "K", "La", "Li", "Lu", "Lr", "Md", "Mg", "Mn", "Mo", "N", "Na", "Nb",
		"Nd", "Ni", "No", "Np", "Os", "P", "Pa", "Pb", "Pd", "Pm", "Po", "Pr", "Pt", "Pu",
		"Ra", "Rb", "Re", "Rh", "Ru", "S", "Sb", "Sc", "Se", "Si", "Sm", "Sn", "Sr", "Ta",
		"Tb", "Tc", "Te", "Th", "Ti", "Tl", "Tm", "U", "V", "W", "Y", "Yb", "Zn", "Zr", "O"};
	
	static final double ma[] = {227.0278, 107.8682, 26.98, 243.0614, 74.9216, 209.987,
		196.966, 10.811, 137.327, 9.012, 208.980, 247.07, 79.904, 12.011,
		40.078, 112.411,140.115, 251.0796, 35.4527, 58.933, 51.996, 132.905,
		63.546, 162.50, 167.26, 252.083, 151.965, 18.998, 55.847, 69.723,
		157.25, 72.61, 1.00794, 178.49, 200.59, 164.930, 126.905, 114.82,
		192.22, 39.0983,138.906, 6.941, 174.967, 260.1053, 258.099, 24.305,
		54.938, 95.94, 14.007, 22.90, 92.906, 144.24, 58.69, 259.1009, 237.048,
		190.2, 30.974, 231.036,207.2, 106.42, 146.915, 208.9824, 140.908,
		195.08, 244.064, 226.03, 85.47, 186.207, 102.91, 101.07, 32.066, 121.75,
		44.96, 78.96, 28.09, 150.36, 118.71, 87.62, 180.95, 158.93, 98.91,
		127.6, 232.04, 47.88, 204.38, 168.93, 238.029, 50.94, 183.85, 88.91,
		173.04, 65.39, 91.224, 15.994};
	
	private ChemistryUtils() { }
	
	public static double calculateMolecularWeight(String cForm)
	{
		String s[] = new String[20];		// Symbols of the elements in the chemical formula
		double coeff[] = new double[20];	// Coefficients --------------------------------
		int len = cForm.length();			// Number of characters in the formula
		char c;
		String ch, coefficient;
		int a = 0, i = 0, end = 0;
		cForm = cForm + " ";
		
		//			Lexical analysis of the chemical formula in args[0]
		do{
			ch = ""; coefficient = "1"; coeff[a] =0;
			
			//				First letter has to be uppercase
			c = cForm.charAt(i);
			if (Character.isUpperCase(c)) {
				ch = String.valueOf(c);
				s[a] = ch;
				i++;
			}
			
			//				If exists, second letter has to be lowercase
			c = cForm.charAt(i);
			if (Character.isLowerCase(c)) {
				ch = String.valueOf(c);
				s[a] =s[a] + ch; // The symbol of the element is obtained
				i++;
			}
			
			//				Then could be a number (digit)
			c = cForm.charAt(i);
			if (Character.isDigit(c)) {
				coefficient = String.valueOf(c);
				i++;
			}
			
			//				Could be again a number
			c = cForm.charAt(i);
			if (Character.isDigit(c)) {
				coefficient = coefficient + String.valueOf(c);
				i++;
			}
			
			//				Then could be a dot if it is a real number
			c = cForm.charAt(i);
			if (c == '.') {
				coefficient = coefficient + ".";
				i++;
			}
			
			//				Then could be a digit (first decimal)
			c = cForm.charAt(i);
			if (Character.isDigit(c)) {
				coefficient = coefficient + String.valueOf(c);
				i++;
			}
			
			//				Then could be again a digit (second decimal)
			c = cForm.charAt(i);
			if (Character.isDigit(c)) {
				coefficient = coefficient + String.valueOf(c);
				i++;
			}
			
			//				The next character could be a comma
			c = cForm.charAt(i);
			if (c == ',') i++;
			coeff[a] = Double.valueOf(coefficient).doubleValue();
			
			if (coeff[a]==0) coeff[a] = 1;
			a++;
		} while(i <= len-1); // End of the lexical analysis of the chemical formula
		
		end = a - 1;
		return calculateMasMol(end, s, coeff);
	}

	private static double calculateMasMol(int ed, String s[], double coeff[])
	{
		double masmol = 0.0;
		double massat[] = new double[ed + 1];
		
		for (int a = 0; a <= ed; a++) {
			for (int i = 0; i <= symb.length-1; i++) {
				if (s[a].equals(symb[i])){
					massat[a] = ma[i];
					break;
				}
			}
		}
		
		for (int a = 0; a <= ed; a++) 
			if (massat[a] > 0) 
				masmol= masmol + massat[a]*coeff[a];
			else {
				masmol=0;
				break;
			}
		
		return masmol;
	}

//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) 
//	{
//		System.out.println(ChemistryUtils.calculateMolecularWeight("C12BrHN10"));
//	}
}
