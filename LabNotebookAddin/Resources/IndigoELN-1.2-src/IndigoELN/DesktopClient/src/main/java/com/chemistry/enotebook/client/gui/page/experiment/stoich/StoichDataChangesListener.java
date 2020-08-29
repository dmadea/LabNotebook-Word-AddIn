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
package com.chemistry.enotebook.client.gui.page.experiment.stoich;

import com.chemistry.enotebook.domain.ReactionStepModel;
import com.chemistry.enotebook.domain.StoicModelInterface;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StoichDataChangesListener implements DensityChangedListener,
												  LimitingReagentChangedListener,
												  LoadingFactorChangedListener,
												  MolarityChangedListener,
												  MolesChangedListener,
												  PurityChangedListener,
												  RxnEquivChangedListener,
												  VolumeChangedListener,
												  WeightChangedListener,
												  SaltCodeChangedListener,
												  SaltEquivsChangedListener,
												  StoicBatchElementAddedListener,
												  StoicBatchElementRemovedListener,
												  RxnRoleChangedListener,
												  MolecularWeightChangedListener,
												  AnalyzeReactionListener
{
	private static final Log log = LogFactory.getLog(StoichDataChangesListener.class);
	
	private PCeNStoichCalculator calculator;
	
	public StoichDataChangesListener(ReactionStepModel mstepModel,String pageType)
	{
		calculator = new PCeNStoichCalculator(mstepModel,pageType);
	}
	public void stoicElementLimitingReagentStatusChanged(LimitingReagentChangedEvent lmre)
	{
		StoicModelInterface model = lmre.getStoicmodel();
		calculator.recalculateStoichBasedOnBatch(model, false);
		log.debug("Limiting reagent change handled in StoichDataChangesListener");
	}
	
	public void stoicElementDensityChanged(DensityChangedEvent dse)
	{
		StoicModelInterface model = dse.getStoicmodel();
		calculator.recalculateStoichBasedOnBatch(model, false);
		log.debug("Density change handled in StoichDataChangesListener");
	}
	
	public void stoicElementWeightChanged(WeightChangedEvent dse)
	{
		StoicModelInterface model = dse.getStoicmodel();
		calculator.recalculateStoichBasedOnBatch(model, false);
		log.debug("Weight change handled in StoichDataChangesListener");
	}
	
	public void stoicElementMolesChanged(MolesChangedEvent dse)
	{
		StoicModelInterface model = dse.getStoicmodel();
		calculator.recalculateStoichBasedOnBatch(model, false);
		log.debug("Moles change handled in StoichDataChangesListener");
	}
	
	public void stoicElementMolarityChanged(MolarityChangedEvent dse)
	{
		StoicModelInterface model = dse.getStoicmodel();
		calculator.recalculateStoichBasedOnBatch(model, false);
		log.debug("Molarity change handled in StoichDataChangesListener");
	}
	
	public void stoicElementLoadingFactorChanged(LoadingFactorChangedEvent dse)
	{
		StoicModelInterface model = dse.getStoicmodel();
		calculator.recalculateStoichBasedOnBatch(model, false);
		log.debug("Loading Factor change handled in StoichDataChangesListener");
	}
	
	public void stoicElementPurityChanged(PurityChangedEvent dse)
	{
		StoicModelInterface model = dse.getStoicmodel();
		calculator.recalculateStoichBasedOnBatch(model, false);
		log.debug("Purity change handled in StoichDataChangesListener");
	}
	
	public void stoicElementVolumeChanged(VolumeChangedEvent dse)
	{
		StoicModelInterface model = dse.getStoicmodel();
		calculator.recalculateStoichBasedOnBatch(model, false);
		log.debug("Volume change handled in StoichDataChangesListener");
	}
	
	public void stoicElementRxnEquivsChanged(RxnEquivChangedEvent rce)
	{
		StoicModelInterface model = rce.getStoicmodel();
		calculator.recalculateStoichBasedOnBatch(model, false);
		log.debug("Rxn equivs change handled in StoichDataChangesListener");
	}
	
	public void stoicElementSaltCodeChanged(SaltCodeChangedEvent sce)
	{
		StoicModelInterface model = sce.getStoicmodel();
		calculator.recalculateStoichBasedOnBatch(model, false);
		log.debug("Saltcode change handled in StoichDataChangesListener");
	}
	
	public void stoicElementSaltEquivsChanged(SaltEquivsChangedEvent sce)
	{
		StoicModelInterface model = sce.getStoicmodel();
		calculator.recalculateStoichBasedOnBatch(model, false);
		log.debug("Salt equivs change handled in StoichDataChangesListener");
	}
	
	public void recalculateMolarAmountForSolvent(MolarityChangedEvent dse)
	{
		StoicModelInterface model = dse.getStoicmodel();
		calculator.recalculateMolarAmountForSolvent(model.getStoicMolarAmount());
		log.debug("recalculateMolarAmountForSolvent() handled in StoichDataChangesListener");
	}
	
	public void stoicElementBatchRemoved(StoicBatchElementRemovedEvent sbe)
	{
		StoicModelInterface model = sbe.getStoicmodel();
		calculator.removeBatchFromStep(model);
		log.debug("stoicElementBatchRemoved() handled in StoichDataChangesListener");
	}
	
	public void stoicElementBatchAdded(StoicBatchElementAddedEvent sbe)
	{
		StoicModelInterface model = sbe.getStoicmodel();
		calculator.insertBatchIntoAdditionOrder(model, sbe.getPosition());
		log.debug("stoicElementBatchAdded() handled in StoichDataChangesListener");
	}
	
	public void stoicElementRxnRoleChanged(RxnRoleChangedEvent dse)
	{
		StoicModelInterface model = dse.getStoicmodel();
		calculator.recalculateStoichBasedOnBatch(model, false);
		log.debug("RxnRole change handled in StoichDataChangesListener");
	}
	
	public void stoicElementMolecularWeightChanged(MolecularWeightChangedEvent dse)
	{
		StoicModelInterface model = dse.getStoicmodel();
		//Force to reacalc Amounts like Weight etc when MWT changes
		model.recalcAmounts();
		calculator.recalculateStoichBasedOnBatch(model, false);
		log.debug("MolecularWeight change handled in StoichDataChangesListener");
	}
	
	public void stoicAnalyzeReaction(AnalyzeReactionEvent dse)
	{
		calculator.recalculateStoich();
		log.debug("AnalyzeReaction handled in StoichDataChangesListener");
	}
}
