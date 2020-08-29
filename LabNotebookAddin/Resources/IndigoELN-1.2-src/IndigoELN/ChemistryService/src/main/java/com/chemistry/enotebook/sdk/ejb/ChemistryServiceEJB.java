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
package com.chemistry.enotebook.sdk.ejb;

import com.chemistry.enotebook.sdk.*;
import com.chemistry.enotebook.sdk.interfaces.ChemistryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChemistryServiceEJB implements ChemistryService {

	private static final Log log = LogFactory.getLog(ChemistryServiceEJB.class);

    /********************   UTLIITY METHODS   **************************/

    /**
     * Method to convert Chemistry from one data format to another
     *
     * @ Thrown if the instance could not perform
     *                      the function requested by the container because of an system-level error.
     * 
     */
	@Override
    public boolean isChemistryEmpty(byte[] chemistry)
            throws ChemUtilAccessException {
        boolean result = false;
        ChemistryUtilInterface cu = null;

        try {
            cu = ChemistryFactory.getNewUtilityObj();
            result = cu.isChemistryEmpty(chemistry);
        } catch (Exception e) {
            throw new ChemUtilAccessException("ChemUtilitiesRemote:isChemistryEmpty failure", e);
        } finally {
            if (cu != null) {
                cu.dispose();
                cu = null;
            }
        }

        return result;
    }

    /**
     * Method to convert Chemistry from one data format to another
     *
     * @ Thrown if the instance could not perform
     *                      the function requested by the container because of an system-level error.
     * 
     */
	@Override
    public boolean isChemistryEqual(byte[] chemistry1, byte[] chemistry2) throws ChemUtilAccessException
             {
        boolean result = false;
        ChemistryUtilInterface cu = null;

        try {
            cu = ChemistryFactory.getNewUtilityObj();
            result = cu.isChemistryEqual(chemistry1, chemistry2);
        } catch (Exception e) {
            throw new ChemUtilAccessException("ChemUtilitiesRemote:isChemistryEqual failure", e);
        } finally {
            if (cu != null) {
                cu.dispose();
                cu = null;
            }
        }

        return result;
    }

    /**
     * Method to convert Chemistry from one data format to another
     *
     * @ Thrown if the instance could not perform
     *                      the function requested by the container because of an system-level error.
     * 
     */
	@Override
    public byte[] convertChemistry(byte[] inBuff, String inBuffType, String outBuffType)throws ChemUtilAccessException
             {
        byte[] result = null;
        ChemistryUtilInterface cu = null;

        try {
            cu = ChemistryFactory.getNewUtilityObj();
            result = cu.convertChemistry(inBuff, inBuffType, outBuffType);
        } catch (Exception e) {
            throw new ChemUtilAccessException("ChemUtilitiesRemote:convertChemistry failure", e);
        } finally {
            if (cu != null) {
                cu.dispose();
                cu = null;
            }
        }

        return result;
    }

    /********************   REACTION METHODS   **************************/

    /**
     * Method to determine if chemistry contains a valid reaction
     *
     * @ Thrown if the instance could not perform
     *                      the function requested by the container because of an system-level error.
     * 
     */
	@Override
    public boolean isReaction(byte[] sketch)throws ChemUtilAccessException
             {
        boolean result = false;
        ChemistryReactionInterface cr = null;

        try {
            cr = ChemistryFactory.getNewReactionObj();
            result = cr.isReaction(sketch);
        } catch (Exception e) {
            throw new ChemUtilAccessException("ChemUtilitiesRemote:isReaction failure", e);
        } finally {
            if (cr != null) {
                cr.dispose();
                cr = null;
            }
        }

        return result;
    }

    /**
     * Method to convert Chemistry from one data format to another
     *
     * @ Thrown if the instance could not perform
     *                      the function requested by the container because of an system-level error.
     * 
     */
	@Override
    public ReactionProperties extractReactionComponents(ReactionProperties rxnProp)throws ChemUtilAccessException
             {
        ReactionProperties result = null;
        ChemistryReactionInterface cr = null;

        try {
            cr = ChemistryFactory.getNewReactionObj();
            result = cr.extractReactionComponents(rxnProp);
        } catch (Exception e) {
            throw new ChemUtilAccessException("ChemUtilitiesRemote:extractReactionComponents failure", e);
        } finally {
            if (cr != null) {
                cr.dispose();
                cr = null;
            }
        }

        return result;
    }

    /**
     * Method to convert Chemistry from one data format to another
     *
     * @ Thrown if the instance could not perform
     *                      the function requested by the container because of an system-level error.
     * 
     */
	@Override
    public ReactionProperties combineReactionComponents(ReactionProperties rxnProp) throws ChemUtilAccessException
             {
        ReactionProperties result = null;
        ChemistryReactionInterface cr = null;

        try {
            cr = ChemistryFactory.getNewReactionObj();
            result = cr.combineReactionComponents(rxnProp);
        } catch (Exception e) {
            throw new ChemUtilAccessException("ChemUtilitiesRemote:combineReactionComponents failure", e);
        } finally {
            if (cr != null) {
                cr.dispose();
                cr = null;
            }
        }

        return result;
    }

    /**
     * Method to convert Chemistry from one data format to another
     *
     * @ Thrown if the instance could not perform
     *                      the function requested by the container because of an system-level error.
     * 
     */
	@Override
    public byte[] convertReactionToPicture(byte[] chemistry, int format,
                                           int height, int width, double loss, double pixelToMM)throws ChemUtilAccessException
             {
    	log.debug("convertReactionToPicture Called....");
        byte[] result = null;
        ChemistryReactionInterface cr = null;

        try {
            cr = ChemistryFactory.getNewReactionObj();
            result = cr.convertToPicture(chemistry, format, height, width, loss, pixelToMM);
            log.debug("convertReactionToPicture result lentgh = " + result!=null ? result.length: "0");
        } catch (Exception e) {
        	log.error("convertReactionToPicture failure. " + e);
            throw new ChemUtilAccessException("ChemUtilitiesRemote:convertReactionToPicture failure", e);
        } finally {
            if (cr != null) {
                cr.dispose();
                cr = null;
            }
        }

        return result;
    }

    /********************   STRUCTURE METHODS   **************************/

    /**
     * Method to convert Chemistry from one data format to another
     *
     * @ Thrown if the instance could not perform
     *                      the function requested by the container because of an system-level error.
     * 
     */
	@Override
    public boolean areMoleculesEqual(byte[] chemistry1, byte[] chemistry2) throws ChemUtilAccessException
             {
        boolean result = false;
        ChemistryStructureInterface cs = null;

        try {
            cs = ChemistryFactory.getNewStructureObj();
            result = cs.areMoleculesEqual(chemistry1, chemistry2);
        } catch (Exception e) {
            throw new ChemUtilAccessException("ChemUtilitiesRemote:areMoleculesEqual failure", e);
        } finally {
            if (cs != null) {
                cs.dispose();
                cs = null;
            }
        }

        return result;
    }

    /**
     * Method to convert Chemistry from one data format to another
     *
     * @ Thrown if the instance could not perform
     *                      the function requested by the container because of an system-level error.
     * 
     */
	@Override
    public ChemistryProperties getMolecularInformation(byte[] chemistry) throws ChemUtilAccessException
             {
        ChemistryProperties result = null;
        ChemistryStructureInterface cs = null;

        try {
            cs = ChemistryFactory.getNewStructureObj();
            result = cs.getMolecularInformation(chemistry);
        } catch (Exception e) {
            throw new ChemUtilAccessException("ChemUtilitiesRemote:getMolecularInformation failure", e);
        } finally {
            if (cs != null) {
                cs.dispose();
                cs = null;
            }
        }

        return result;
    }

    /**
     * Method to convert Chemistry from one data format to another
     *
     * @ Thrown if the instance could not perform
     *                      the function requested by the container because of an system-level error.
     * 
     */
	@Override
    public boolean isMoleculeChiral(byte[] chemistry) throws ChemUtilAccessException
             {
        boolean result = false;
        ChemistryStructureInterface cs = null;

        try {
            cs = ChemistryFactory.getNewStructureObj();
            result = cs.isMoleculeChiral(chemistry);
        } catch (Exception e) {
            throw new ChemUtilAccessException("ChemUtilitiesRemote:isMoleculeChiral failure", e);
        } finally {
            if (cs != null) {
                cs.dispose();
                cs = null;
            }
        }

        return result;
    }

    /**
     * Method to convert Chemistry from one data format to another
     *
     * @ Thrown if the instance could not perform
     *                      the function requested by the container because of an system-level error.
     * 
     */
	@Override
    public byte[] setChiralFlag(byte[] chemistry, boolean flag) throws ChemUtilAccessException
             {
        byte[] result = null;
        ChemistryStructureInterface cs = null;

        try {
            cs = ChemistryFactory.getNewStructureObj();
            result = cs.setChiralFlag(chemistry, flag);
        } catch (Exception e) {
            throw new ChemUtilAccessException("ChemUtilitiesRemote:setChiralFlag failure", e);
        } finally {
            if (cs != null) {
                cs.dispose();
                cs = null;
            }
        }

        return result;
    }


    /**
     * Method to convert Chemistry from one data format to another
     *
     * @ Thrown if the instance could not perform
     *                      the function requested by the container because of an system-level error.
     * 
     */
	@Override
    public byte[] convertStructureToPicture(byte[] chemistry, int format,
                                            int height, int width, double loss, double pixelToMM) throws ChemUtilAccessException
             {
    	log.debug("convertStructureToPicture Called....");
        byte[] result = null;
        ChemistryStructureInterface cs = null;

        try {
            cs = ChemistryFactory.getNewStructureObj();
            result = cs.convertToPicture(chemistry, format, height, width, loss, pixelToMM);
            log.debug("convertStructureToPicture result lentgh = " + result!=null ? result.length: "0");
        } catch (Exception e) {
        	log.error("convertStructureToPicture failure");
            throw new ChemUtilAccessException("ChemUtilitiesRemote:convertStructureToPicture failure", e);
        } finally {
            if (cs != null) {
                cs.dispose();
                cs = null;
            }
        }

        return result;
    }

    /**
     * Method to set the Debug Flag so that chemistry processed is recorded
     *
     * @ Thrown if the instance could not perform
     *                      the function requested by the container because of an system-level error.
     * 
     */
	@Override
    public void setDebugFlag(boolean flag)
             {
    	debugFlag = flag;
    }
    
    private boolean debugFlag;

    /**
     * Method to set the Debug Flag so that chemistry processed is recorded
     *
     * @ Thrown if the instance could not perform
     *                      the function requested by the container because of an system-level error.
     * 
     */
    @Override
    public boolean getDebugFlag()
             {
        return debugFlag;
    }
}
