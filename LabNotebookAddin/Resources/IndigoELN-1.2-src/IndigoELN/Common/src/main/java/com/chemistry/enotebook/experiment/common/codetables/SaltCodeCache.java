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
package com.chemistry.enotebook.experiment.common.codetables;

import com.chemistry.enotebook.domain.SaltFormModel;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;

import java.math.BigDecimal;
import java.util.*;

/**
 * 
 * 
 *
 */
public class SaltCodeCache {

	public static final int CODE = 0;
	public static final int DESCR = 1;
	public static final int MOL_WT = 2;
	public static final int MOL_FORMULA = 3;

	private static SaltCodeCache _instance;
	LinkedHashMap<String, Properties> salts = new LinkedHashMap<String, Properties>();
	HashMap<String, String> saltsByFormula = new HashMap<String, String>();

	/**
	 * 
	 */
	private SaltCodeCache() throws CodeTableCacheException {
		super();
		init();
	}

	public static SaltCodeCache getCache() throws CodeTableCacheException {
		if (_instance == null) {
			_instance = new SaltCodeCache();
		}
		return _instance;
	}

	private void init() throws CodeTableCacheException {
		salts.clear();
		saltsByFormula.clear();
		
		List<Properties> tempSalts = CodeTableCache.getCache().getSalts();
		for (Properties tmpRow : tempSalts) {
			salts.put((String)tmpRow.get(CodeTableCache.SALTS__SALT_CODE), tmpRow);
			saltsByFormula.put((String)tmpRow.get(CodeTableCache.SALTS__SALT_FORMULA), (String)tmpRow.get(CodeTableCache.SALTS__SALT_CODE));
		}
	}

	public List<Properties> getCodesAsList() throws CodeTableCacheException {
		return CodeTableCache.getCache().getSalts();
	}

	public Map<String, Properties> getCodesAsMap() {
		return salts;
	}

	public String getDescriptionGivenCode(String queryCode) {
		if (salts.containsKey(queryCode)) {
			return (String)salts.get(queryCode).get(CodeTableCache.SALTS__SALT_DESC);
		}
		return null;
	}

	public double getMolWtGivenCode(String queryCode) {
		if (SaltFormModel.isParentCode(queryCode) == false && salts.containsKey(queryCode)) {
			return (new BigDecimal((String)salts.get(queryCode).get(CodeTableCache.SALTS__SALT_WEIGHT))).doubleValue();
		}
		return 0.0;
	}

	public String getMolFormulaGivenCode(String queryCode) {
		if (salts.containsKey(queryCode)) {
			String formula = (String) (salts.get(queryCode)).get(CodeTableCache.SALTS__SALT_FORMULA);
			return (formula != null ? formula : "");
		}
		return null;
	}
}
