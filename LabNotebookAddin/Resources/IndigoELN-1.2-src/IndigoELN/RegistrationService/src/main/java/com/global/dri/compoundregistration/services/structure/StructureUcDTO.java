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
/*
 * Created by IntelliJ IDEA.
 * User: ITO01
 * Date: Sep 11, 2002
 * Time: 11:37:37 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.global.dri.compoundregistration.services.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StructureUcDTO implements java.io.Serializable {

	private static final long serialVersionUID = 732261410931437130L;
	
	static final int HIT_TYPE_PRIMARY_EXACT = 100;
    static final int HIT_TYPE_PRIMARY_ISOMER = 200;
    static final int HIT_TYPE_SECONDARY = 300;
    static final int HIT_TYPE_UNKNOWN = 400;
    Map hitTypes;

    boolean newParent;
    boolean curationHit;
    boolean exceptionalParent;
    int exactHitCount;
    int isomerHitCount;
    int legacyHitCount;
    List primaryKeys;
    Map intelligentKeys;
    Map stereoisomerCodes;
    Map molfiles;
    Map formulas;
    Map weights;
    Map comments;
    private final String CLASS_NAME = this.getClass().getName();

    public int getExactHitCount() {
        return exactHitCount;
    }

    public void setExactHitCount(int exactHitCount) {
        this.exactHitCount = exactHitCount;
    }

    public int getIsomerHitCount() {
        return isomerHitCount;
    }

    public void setIsomerHitCount(int isomerHitCount) {
        this.isomerHitCount = isomerHitCount;
    }

    public int getLegacyHitCount() {
        return legacyHitCount;
    }

    public void setLegacyHitCount(int legacyHitCount) {
        this.legacyHitCount = legacyHitCount;
    }

    public boolean isLegacyMatch(String primaryKey) {
        if (hitTypes.get(primaryKey) == null) {
            return false;
        }
        return (((Integer) hitTypes.get(primaryKey)).intValue() == HIT_TYPE_SECONDARY) ? true : false;
    }

    public boolean isExactMatch(String primaryKey) {
        if (hitTypes.get(primaryKey) == null) {
            return false;
        }
        return (((Integer) hitTypes.get(primaryKey)).intValue() == HIT_TYPE_PRIMARY_EXACT) ? true : false;
    }

    public boolean isIsomerMatch(String primaryKey) {
        if (hitTypes.get(primaryKey) == null) {
            return false;
        }
        return (((Integer) hitTypes.get(primaryKey)).intValue() == HIT_TYPE_PRIMARY_ISOMER) ? true : false;
    }

    public void setHitTypes(Map hitTypes) {
        this.hitTypes = hitTypes;
    }

    public boolean hasCurationHit() {
        return curationHit;
    }

    public void setCurationHit(boolean curationHit) {
        this.curationHit = curationHit;
    }

    public boolean isExceptionalParent() {
        return exceptionalParent;
    }

    public void setExceptionalParent(boolean exceptionalParent) {
        this.exceptionalParent = exceptionalParent;
    }

    public Map getFormulas() {
        return formulas;
    }

    public void setFormulas(Map formulas) {
        this.formulas = formulas;
    }

    public Map getWeights() {
        return weights;
    }

    public void setWeights(Map weights) {
        this.weights = weights;
    }

    public Map getComments() {
        return comments;
    }

    public void setComments(Map comments) {
        this.comments = comments;
    }

    public String getFormula(String primaryKey) {
        return (String) this.formulas.get(primaryKey);
    }

    public Double getWeight(String primaryKey) {
        return (Double) this.weights.get(primaryKey);
    }

    public String getComment(String primaryKey) {
        return (String) this.comments.get(primaryKey);
    }

    public String getIntelligentKey(String primaryKey) {
        return (String) this.intelligentKeys.get(primaryKey);
    }

    public String getStereoisomerCode(String primaryKey) {
        return (String) this.stereoisomerCodes.get(primaryKey);
    }

    public String getMolfile(String primaryKey) {
        return (String) this.molfiles.get(primaryKey);
    }

    public boolean isNewParent() {
        return newParent;
    }

    public void setNewParent(boolean newParent) {
        this.newParent = newParent;
    }

    public List getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public Map getIntelligentKeys() {
        return intelligentKeys;
    }

    public void setIntelligentKeys(Map intelligentKeys) {
        this.intelligentKeys = intelligentKeys;
    }

    public Map getStereoisomerCodes() {
        return stereoisomerCodes;
    }

    public void setStereoisomerCodes(Map stereoisomerCodes) {
        this.stereoisomerCodes = stereoisomerCodes;
    }

    public Map getMolfiles() {
        return molfiles;
    }

    public void setMolfiles(Map molfiles) {
        this.molfiles = molfiles;
    }

    public List getExactHitPrimaryKeys() {

        List list = new ArrayList();
        for (int i = 0; i < primaryKeys.size(); i++) {
            String primaryKey = (String) primaryKeys.get(i);
            if (isExactMatch(primaryKey)) {
                list.add(primaryKey);
            }
        }

        return list;
    }

    public String getNotUniqueString() {
    	return null;
    }

    /**
     * The incoming key is a long sequence id.  Checking to see if there
     * are any structures in this dto that do NOT match the key.
     *
     * @param oKey
     * @return
     */
    public boolean isNewParent(Object oKey) {
        if (oKey == null) return this.isNewParent() && !this.getPrimaryKeys().isEmpty();
        for (Iterator it = this.getPrimaryKeys().iterator(); it.hasNext();) {
            if (!it.next().equals(oKey)) return false;
        }
        return true;
    }
}
