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
package com.chemistry.enotebook.service.storage;

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchNumberException;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.utils.CommonUtils;

import java.util.*;
/**
 * 
 */
public class StorageServiceMock // implements StorageService
{

	public NotebookPageModel getExperiment(String nbkNumber, String pageNumber) {
		NotebookPageModel experiment = null;
		try {
			experiment = new NotebookPageModel(new NotebookRef("01234567", "123"));
			createNotebookPageHeader(experiment);
		} catch (Exception e) {
		}
		return experiment;
	}

	public NotebookPageModel createParallelExperiment(String spid, String userid) {
		NotebookPageModel experiment = null;
		try {
			experiment = new NotebookPageModel(new NotebookRef("01234567", "123"));
			createNotebookPageHeader(experiment);
			//experiment.setSynthesisPlan(getDesignSynthesisPlanHeader(spid));
			this.makeDSPSummaryPlan(experiment, true);
		} catch (Exception e) {
		}
		return experiment;
	}

	public NotebookPageModel createConceptionRecord(String userid) {
		NotebookPageModel experiment = null;
		try {
			experiment = new NotebookPageModel(new NotebookRef("12345678", "123"));
			createNotebookPageHeader4CR(experiment);
			this.makeDSPSummaryPlan(experiment, true);
		} catch (Exception e) {
		}
		return experiment;
	}

	public NotebookPageModel createSingletonExperiment(String userid) {
		return null;

	}

	public DesignSynthesisPlan getDesignSynthesisPlanHeader(String spid) {
		DesignSynthesisPlan dspPlan = new DesignSynthesisPlan("147"); // SYNTH_PLAN_ID is 158
		this.makeDSPHeader(dspPlan);
		return dspPlan;
	}

	public DesignSynthesisPlan getDesignSynthesisPlanAll(String spid, boolean includeStructures) {
		DesignSynthesisPlan dspPlan = new DesignSynthesisPlan("73");

		return dspPlan;
	}

	public NotebookPageModel getDesignSynthesisPlanWithSummaryReaction(String spid, boolean includeStructures) {
		NotebookPageModel pagemodel = null;
		DesignSynthesisPlan dspPlan = new DesignSynthesisPlan("147");
		this.makeDSPHeader(dspPlan);

		try {
			pagemodel = new NotebookPageModel(new NotebookRef("01234567", "123"));
			this.createNotebookPageHeader(pagemodel);
		} catch (Exception e) {

		}
		//pagemodel.setSynthesisPlan(dspPlan);
		this.makeDSPSummaryPlan(pagemodel, true);
		return pagemodel;
	}

	public ReactionStepModel getDesignSynthesisPlanReactionStep(String spid, String planId, boolean includeStructures) {
		return getIntermediateSteps(planId);

	}

	private void makeDSPHeader(DesignSynthesisPlan dspPlan) {
		// set sap level data
		dspPlan.setBatchOwner("USER1");
		dspPlan.setUserName("USER1");
		dspPlan.setDescription("rational");
		dspPlan.setProtocolId("56");
		dspPlan.setSeriesId("34");
		dspPlan.setNotebookId("45645645-6456");
		dspPlan.setDesignUsers(new String[] { "USER1", "USER2", "USER" });
		dspPlan.setComments("");
		dspPlan.setScreenPanels(new String[] { "", "", "" });
		dspPlan.setVrxnId("VRXN-2-00044");
		dspPlan.setVcrRegDone(false);
		dspPlan.setSummaryPlanId("855");
		dspPlan.setIntermediatePlanIds(new String[] { "80", "81" }); // only summary step so need to make default step 1
		//dspPlan.setScaleAmount(1);
		dspPlan.setSite("SITE1");
		dspPlan.setPrototypeLeadIds(new String[] { "193" });
		dspPlan.setTaCode("AN");
		dspPlan.setProjectCode("70DAN00850");
		dspPlan.setDateCreated(Calendar.getInstance().toString());
		if (dspPlan.getIntermediatePlanIds() != null && dspPlan.getSummaryPlanId() != null) {
			dspPlan.setTotalReactionSteps((dspPlan.getIntermediatePlanIds().length + 1));
		}
	}

	private void makeDSPSummaryPlan(NotebookPageModel pageModel, boolean includeStructures) {
		ReactionStepModel sumStep = new ReactionStepModel(); // Type should be SUMMARY
		// sumStep.setParentId("");
		// sumStep.setPid("855");
		// sumStep.setSpid("147");
		sumStep.setStepNumber(0);
		// prepare reaction scheme
		ReactionSchemeModel summaryScheme = new ReactionSchemeModel(); // should be SYNTHESIS
		summaryScheme
				.setStringSketchFormat("pVTbbsMgDH3PV1haX4NsgwE/t9NWaU2nVJr2/19SLq3WbHSjWUQkY8zBPj6wmT+nYYDy7U/7UzFQSRjREbLLiwxAw+ZwfMuTMYeNNaiG8C77bYnD5lBV+EiImE8ayShGWy3nWcqRJq0izE/3IG7HBYXUarVQ0t4vlCN0o6BxQV0zl9deFKoMffNyoWTpPQDML+/FW//seZ52f5LrClgHucAmeh9qQRyUV5EL1kgkqgRd8K4oUze54Ayr+GYu234USTscVxQfg6xpNP/U593G2UI3/9o47m1cql66GkdJf3It0+FKPRMb8tHXxumSrO0jKDFaauYyrcuFlnfrgYrQBKuhWVG/oCkJWoP/Hwo3hNG+52UuTW9bcP7eS5EFIRfR+VvhnQE=");
		summaryScheme.setVrxId("VRXN-2-00123");
		sumStep.setRxnScheme(summaryScheme);

		// prepare reactants
		this.createMonomersForReaction(sumStep);
		// prepare products
		this.createProductsForReaction(sumStep);

		pageModel.addReactionStep(sumStep);

	}

	private void makeDSPIntermediateSteps(DesignSynthesisPlan dspPlan, boolean includeStructures) {

	}

	public ReactionStepModel getIntermediateSteps(String pid) {
		if (pid.compareToIgnoreCase("855") == 0) {
			// prepare step0
			ReactionStepModel intStep = new ReactionStepModel(); // Type should be SUMMARY
			// intStep.setParentId("855");
			// intStep.setPid("855");
			// intStep.setSpid("147");
			intStep.setStepNumber(0);
			// prepare reaction scheme
			ReactionSchemeModel intScheme = new ReactionSchemeModel(); // should be SYNTHESIS
			intScheme
					.setStringSketchFormat("pVTbbsMgDH3PV1haX4NsgwE/t9NWaU2nVJr2/19SLq3WbHSjWUQkY8zBPj6wmT+nYYDy7U/7UzFQSRjREbLLiwxAw+ZwfMuTMYeNNaiG8C77bYnD5lBV+EiImE8ayShGWy3nWcqRJq0izE/3IG7HBYXUarVQ0t4vlCN0o6BxQV0zl9deFKoMffNyoWTpPQDML+/FW//seZ52f5LrClgHucAmeh9qQRyUV5EL1kgkqgRd8K4oUze54Ayr+GYu234USTscVxQfg6xpNP/U593G2UI3/9o47m1cql66GkdJf3It0+FKPRMb8tHXxumSrO0jKDFaauYyrcuFlnfrgYrQBKuhWVG/oCkJWoP/Hwo3hNG+52UuTW9bcP7eS5EFIRfR+VvhnQE=");
			intScheme.setVrxId("");
			intStep.setRxnScheme(intScheme);

			// prepare reactants
			this.createMonomersForReaction(intStep);
			// prepare products
			this.createProductsForReaction(intStep);
			return intStep;

		}

		if (pid.compareToIgnoreCase("80") == 0) {
			// prepare step1
			ReactionStepModel intStep2 = new ReactionStepModel(); // Type should be SUMMARY
			// intStep2.setParentId("78");
			// intStep2.setPid("80");
			// intStep2.setSpid("73");
			intStep2.setStepNumber(2);
			// prepare reaction scheme
			ReactionSchemeModel intScheme2 = new ReactionSchemeModel(); // should be SYNTHESIS
			intScheme2
					.setStringSketchFormat("rVSxbsQgDN35CqTebNkGTBhPd5U69HJShqr7da/U/x8KhFNJykkkbcRgXtDD9vPjML2PSun8HW+3z6+PGCCRRUGkIf1irUkdLtfXtLmf+TnC54RbrU1EmyuEoN8YEfM9DmKEc2RFfIpwxqanRxT1KiyerJujwQeuWEbdzSJgeQjNXF70jlwEPFrew0Jzn1co5642ULtGj+lSUhOpi9bP4/kfBQswGO+W0pXyrt1NIgTn+C47satYTv0sBCxim7n0D0+ViwCX2rZWtF0w/i2YURP3Ciax5i7ByAEVRwigW8zjhiZFXwUjxSVLb/Q7rGLxwIH2OYwEBAdqVnTaw+KAefFmbBhkD4a53Zfu7v7V7Rly65HKqDx+GXIk1ch9Aw==");
			intStep2.setRxnScheme(intScheme2);

			// prepare reactants
			this.createMonomersForReaction(intStep2);
			// prepare products
			this.createProductsForReaction(intStep2);
			return intStep2;
		}

		if (pid.compareToIgnoreCase("81") == 0) {
			// prepare step2
			ReactionStepModel intStep3 = new ReactionStepModel(); // Type should be SUMMARY
			// intStep3.setParentId("78");
			// intStep3.setPid("81");
			// intStep3.setSpid("73");
			intStep3.setStepNumber(1);
			// prepare reaction scheme
			ReactionSchemeModel intScheme3 = new ReactionSchemeModel(); // should be SYNTHESIS
			// intScheme3.setDspReactionDefinition("kbQZAowA2DwQGg8gZFd3o^x8EYQVRjQwb$WXzqs0iSM8RjMRAST9JkciRm58QtCBlTybHDGg125g0aJsIcwhpmm7kwN8IjnA3EKzXkEqTjy7hPfMe5jkM1bThDeIlnf4xs^bpdbNdppdsHN6F0GiqyF2M4tG8oVEcRqYmwBzMfisj$S^wXYvDy1Ot8FOjm032bpEXEDhK9QAhW4zoQ6oHWdlrFpQlfrqqZqELIMK7kyLXwYUMlB7XITOUWF9OwuTxpXPiJN8HoYRFaeZ90m6cOpC13pdg$LiH$LHyirwDm9SUHuYWf57c9F1oPvW841a4pl$t0XGtaDXwQUd91BUMCSOn3uVaJd^XJsOQcO3fwh^gcy^nX^QAx16waj2blJMdqVKjKpAZUNZxiSBzU$DTRxlZTuT3sv4q4ExuG9LxmwUnxiEtI1C$IhPsxeUfaea7XhO1VjyV5tQkJkLkkuSqQ$akE6tQWFlzdLysakgmIpsrdSx0CsNnxiK6NTgm8AqzCA1bcG3TQOiRa1xiziE4LJWYFJeaj8Cda^IItqbOZZAvq4qX0y00xrLXHsWsB1odHz1cku2H51caZtswmbfpjS9fvrJpSP6bZhozvusNTOwq3O2vMQfMVzPlx6NuT8clZqHKj5ctCj5UP8WsGHHHnVrRws2jr2NYmtnhU9wBcffhPx99A");
			intScheme3
					.setStringSketchFormat("rVI9a8MwEN31Kw6aWdxXZN8YkkKGxAUNpXs6Fgr5/0NkKaGKcYhIbTQ8PYt3d+/eKn4NzkH+NqfT7/k7ASRSDCSE4y8GILc6fhzGy+3N3xPejbwCSGJnj5nBJyNirrP2CeE9woLi2yOJ+lxVOhItSImsUhmgWSV45WAL9IKdFD0Wq3vZt/ZCxecJy9nVGVan7GYsSi5SQeIiuyPA+7B7urpp3cerM09ifRm5D2utBt3+NNtl3oKEWZVm0+fsyoOzi9I6eHNmKaS0BbnmhI1eygl1KW03+1QJX8os9Z5UeIleyhJSZoPhUkv4b2Yz0mqJFw==");
			// intScheme3.setDspReactionDefinition("rVSxbsQgDN35CqTebNkGTBhPd5U69HJShqr7da/U/x8KhFNJykkkbcRgXtDD9vPjML2PSun8HW+3z6+PGCCRRUGkIf1irUkdLtfXtLmf+TnC54RbrU1EmyuEoN8YEfM9DmKEc2RFfIpwxqanRxT1KiyerJujwQeuWEbdzSJgeQjNXF70jlwEPFrew0Jzn1co5642ULtGj+lSUhOpi9bP4/kfBQswGO+W0pXyrt1NIgTn+C47satYTv0sBCxim7n0D0+ViwCX2rZWtF0w/i2YURP3Ciax5i7ByAEVRwigW8zjhiZFXwUjxSVLb/Q7rGLxwIH2OYwEBAdqVnTaw+KAefFmbBhkD4a53Zfu7v7V7Rly65HKqDx+GXIk1ch9Aw==");
			intStep3.setRxnScheme(intScheme3);

			// prepare reactants
			this.createMonomersForReaction(intStep3);
			// prepare products
			this.createProductsForReaction(intStep3);
			return intStep3;
		}

		return new ReactionStepModel();
	}

	public void createProductsForReaction(ReactionStepModel step) {
		// this method adds enumeration of product batches
		ArrayList products = new ArrayList(10);

		// 2(A) X 3(B) --> 6 (A-B) products
		// prod 1
		// "Chem. Name", "Exact Mass", "Mol. Formula", "Mol. Weight", "mMoles","Salt Code", "Hazard Comments"
		ProductBatchModel batch = new ProductBatchModel();
		ParentCompoundModel c = new ParentCompoundModel();
		c
				.setStringSketchFormat("tVddb9NAEHyPlP9wDzwkD1i3e/b5ToJKoQWpEk1RgxBqVVnGMcUQx1WSgvrv2Tt/lMSXll7AcmJ5vJ6dHe9enOGAsVlWfMxviox1G54MB8MBw4ihYoz/sT9sWmv2CTnnFMgYiEApQcHsJQRSxzaUB3SZs3O2zeHea5owiEPd0MA2zfEzaKJAQuRWM/VSw3fVPKcoCAQHaGmEb1EYUE1RfTNqjZ40EKAKdWOxCoUnDQ9CbNUIHoeeNCLgYGmMN0of4I1EYzEzZqM8wOK2/aRS2t8bYSbHqgEdHeBNjO0wSOn/wDVvvFEA8QHecOsN0kxtWfycmVIBIvKmKIx8LVYBxFI3fYPKdxjiQChoLJbSu290AMCboiIVRt5qUENTihA88lbDQTfGhlz5FiWDUHPRth+itxod6bim4SIK/dWEUtQPHGnd8lcDUVzTcAl+3pAVof3uwSQQdmGgSXbC5oYeLBi6YOoF6YZjhxKClSOaGLQjWtKK5YiOmfnR6sGKmRbtkWhnlUQMDquIGCJHdG1VLxotSS+adMduWDlICNaO6JiZ1QgdsKN4InY9HSJGh24iRoduIqY3q3402ugeLGz0DskZY2+n9JZ2xF4dL4rb22J5k5zOj4aD7z+q9WZV/UpSFeVaJJzHuIUqFDrhgNy84tHtF/Tqt97kq3xu7598fs85gpCUY/qS85B+npvI6me+WhXzPMm+Fat0UWzuKf5ruljn3aEWlC6rZZGli2RWFot8TVHTDLIMM7PRN1xdwvVwYDBzOrIHGF9d4nVDcZbakqwisLuFZ/dlmZye7CkzcWZxlZ48kvp4cWfsYDYNkEtxe6Eqb6u75ZM27dcHe9S05V2YpzjZVKWxLKKxjM1UmZmoT5UdJzMMpMw2v+lHXndOB2nbYKZrGtrJqirTTZH9J3pDO1mv8/LL4n6HXpkE3OZAM9lbzIaJWzK0fOED5exDR9Qh2EFIa2TI9mVgf5dAdHRmKWrgd6v0psyXm+4aMbOn0kWPZNzlfVMt5/+C1/41kwzb3ly3g3Y+Oh5NbVubD3V2BjjOcPz6fEzIaDpuLohxJsJxZgW+oG04+A0=");
		batch.setCompound(c);
		HashMap props = new LinkedHashMap();
		props.put("REACTANT_A", "AXL002136");
		props.put("REACTANT_B", "MN-004018");
		props.put("RegisteredId", "5");
		props.put("Chem. Name", "Chemical");
		props.put("Mol. Formula", "C12H12N202");
		props.put("Mol. Weight", "322.113");
		props.put("Salt Code", "00");
		batch.setBatchProperties(props);
		try {
			batch.setBatchNumber(new BatchNumber("87654321-0023-0001"));
			// need to add VCR id to the product batch

		} catch (InvalidBatchNumberException e) {
			// no need to really print any or rethrow
			// e.printStackTrace();
		}

		products.add(batch);

		ProductBatchModel batch2 = new ProductBatchModel();
		ParentCompoundModel c2 = new ParentCompoundModel();
		c2
				.setStringSketchFormat("pVVda9swFH0P5D/oYQ/JQ42uZFsSrIXM3aCwJqMZY7QU4yle582Oi52u9N/vSv7okiiUOMLYyfU9J/cenRuNR4QsdfY1fcg06Re7HI/GIwKSgCKE/ne9LqUU+cYopZhICHBPSi7x0xl4oRI2lXr4mpIF2eZwXw2N7wlftTSwTRMdQRN4IQTuauaDqqG71RzTFHicAnQ0fGhTzMOeggbMlGIDacBj0letxNLnA2mo57OuGk6FP5CGexQsjdFGqhO0CZmRmBixWXiCxJ39QinVcG04k201oIITtBGsG4YwHL7hirbaSABxgjbUasNwprYkPmamQs+XoE5uytKIbqaCob4RHvdD1rl44Ewhyrf3vTAOF+yGATfWGTaAvTDHVh1htNSBsHBUgmHpyEYG5cgO0cCObEHMf9heGM8I5iBRzi6RGBxSITEEjuxGqr1sZkn2srFu4QgLc47tklwT8nGOp9wFeR/l2eNjtn6Ir1YX49HvP2W9qcrnOJFBqnhMqWBbUcm4wiiE5ohE+A0enfUmrdKVxc++f6aUAcfX1/MzzGNh0GaWf9OqylZprH9lVZJnmxfM/5nkddo/moKSdbnOdJLHyyLL0xqz5hq0ZtosvMPdLdyPR9F5FN3dsvsWdp3YNmwVYC8bXr4URXx1eaC12Mnsajfe+bkofzJtE0sNKGknSFQWj+XT+k05DtcEByroWroxuzXblIWRJkC/CmM3Y5bmq7Q+My5pAbOqLJJNpo8GGsCsrtPiR/6yA5QGSi2aGZv2mOWXPrOPsD7EcKJ9coiicWsP4z3MDkjvuU9V8lCk603/GknIW8xBMyG7FB/K9epIitcS686gi0k0mUdoken5YqonejKfanzggqkGNtXMIN7hGo/+AQ==");
		batch2.setCompound(c2);
		HashMap props2 = new LinkedHashMap();
		props2.put("REACTANT_A", "AXL002136");
		props2.put("REACTANT_B", "MN-001265");
		props2.put("RegisteredId", "1");
		props2.put("Chem. Name", "Chemical2");
		props2.put("Mol. Formula", "C22H16N503");
		props2.put("Mol. Weight", "331.303");
		props2.put("Salt Code", "00");
		batch2.setBatchProperties(props2);
		try {
			batch2.setBatchNumber(new BatchNumber("87654321-0023-0002"));
		} catch (InvalidBatchNumberException e) {
			// no need to really print any or rethrow
			// e.printStackTrace();
		}
		products.add(batch2);

		ProductBatchModel batch3 = new ProductBatchModel();
		ParentCompoundModel c3 = new ParentCompoundModel();
		c3
				.setStringSketchFormat("rVddb9MwFH2v1P/gBx7aByL72vGHBBMlAzGJtWhFCG2aopCGEWiaqe1A+/dcO0lhrbtq7qwoTU9ujs89vtdN+z1Cpnn5ubgpc7IZcNrv9XsEFAFDCHUHc+d/wxhDvgClFAPxLo+05hqvXrJIGuVCaYS3KZmQjuOxo6ERkRKmpWEPaZIn0MSRZLFfzThIDd1W85SkWMQpYx0ND00KIswpbh4GYyCQhkWghWkt1oIH0tBIQKeGUyUCaXhEmaOx3mhzhDcSrMXEmg3yCIu78pNam3BvOOhWDTPxEd4o6JpByvAFN7T1RjOmjvCGOm8Ae+qBxU/pKRkJZWSXlPaXHztIoyIupOo6nIdabNWItjWBxqELjmokFZ03MQRvW7FW0HW4kIH7DaqJtexaU6hAGh0Bj11SHK+Cty202LhOQhpmIA72RsVuwbGKQQS3pkvKqRERhDeDjGIQHQ1QEV43SoiGRlHDgmiwYIQ778B8q5MszHBf8cL2gR2YY6oeGBdxD6w8ShDWnmhkMJ5oifunJ1oR+xO6A2vcmDwkxpslEjOPVUjMYk90Y9VONDiSnWjUrRwst2FP8hjKjAfWBKgfZh4lhoBn0Szs0Y3E4NGNDLYnd6I5Ac8SYygoDywdvEVyTsi7Mb5KnpBXyby8vS0XN+nZ7KTf+/mrXq2X9Z8003FheEqpggeoBm4QBWPfQ/HxC3w/Xa2LZTFzz4++fsSbjEucY/yS4q+kvXSR9e9iuSxnRZr/KJfZvFzfY/z3bL4qNh+NoGxRL8o8m6fTqpwXK4wa5yzPIbcDz+zqkl33e8kkGbyeDK+SNx+uB4mNwMGGV5dw3TKdZy4zJ4y5w8HT+6pKz073ZJt6J/M5kB5WkMzvrDnEzcbQeN3dqKvb+m5x0LT9MtkeUV2WF3ZNR+u6sgbGWPrKtp5tnOardj1nOwaVuXK1hRU3BdNyjJZ1la3L/Dm4LMdotSqqb/P7LS5t2agjBNvYj9FMP20e3iCwgQC3RUH2sdputbxsHzXfELl9p9k4XHe2Ie+X2U1VLNabOOQnhybtiNrZ6X8CRKNhm/1tvZg9H7v7t9gV3qrrqckgGYzb0rVVPEmGST5oKhgPRPDbYDzMGxCGOfBhzi3NCxz93l8=");
		batch3.setCompound(c3);
		HashMap props3 = new LinkedHashMap();
		props3.put("REACTANT_A", "AXL002136");
		props3.put("REACTANT_B", "MN-001736");
		props3.put("RegisteredId", "3");
		props3.put("Chem. Name", "Chemical3");
		props3.put("Mol. Formula", "C12H10N203");
		props3.put("Mol. Weight", "309.343");
		props3.put("Salt Code", "00");
		batch3.setBatchProperties(props3);
		try {
			batch3.setBatchNumber(new BatchNumber("87654321-0023-0003"));
		} catch (InvalidBatchNumberException e) {
			// no need to really print any or rethrow
			// e.printStackTrace();
		}
		products.add(batch3);

		ProductBatchModel batch4 = new ProductBatchModel();
		ParentCompoundModel c4 = new ParentCompoundModel();
		c4
				.setStringSketchFormat("tVjbbiJHEH1H4h/6IQ/w4FZX31vKruTMJspKsYnWURSt10JkPHEmgRkL2ET++1T1XBKgvVnaWoQBnak5dU51VzV4OmHspqx/qh7qko0P+WY6mU6YkkwZxsR/nv8+QgjsZymEwEDGQHHvlcdPF8BtcDFUcLws2IIdcqSfHY3mToeeBg5pijNoDLdg0mqus9SIYzVnmGKeW29Ud7MCGfJMMccdyunUCB9yaQI3BtRgCnQmjefGq2gKuBJO55UYTYHQvRVrcmsDgmuIK0UlPlRzXomtCNCZMuBDpinLAUS/4MaIXFPApTfj9pM205ThTsh+wZU9ULM4i8bI2OG04Fa4zA4nU0oPHZ67i0FyMEIMCw4voNFBdWqcELnzRnEBUgwrpcILaNzLpx/VxtAuvpA4/XzmgpMaCZJoFKrxuaY8l7KrDZqSJp8GnO2nH0gvM2kcV74zhSeDze0pCNThvSnjtclWIwP0VpQSJlsNdkBfWC18rinLdRBq2H5SZqsJJriORiij89Vo248tCV7nqwHj+lPTQl5tsBQ6vp7AKBCOYWC03xMw3XACK0an3gmMe8EmUiLs0tE+ASNDitvhgZng9jidE9GBQUI3MkDCPIbSV4oUbE5TIgOtCCRgl4i2OAYSMNpJuMTQ1DIQnHCJmJRp85BIGSL3MYynrEyYJ9ikoxPmkUEmzBOcWGIkTm0fJFYpgY6plHlP3/VPo0OMPoaRWJ2W6oqxb6/xd8Nr9nX7V7Xd1vfVsvy93q7W9f7p9XTy22q9q8a3GFesP+721Za9fYPX7XTiRrx+fKybh+Xbe7zwx5/tbr9t/16uvKmCWgpj/AHqpQpLOn/729/hjxnire7j/Ze//CBAuIA3XV1fCKEF+CHRqmmbulytlzebel3tMLpYlFCWsw/Fq+JDKfFTc9t8fyfnxezVYr6Yl2UJt+/hbjqhsBgQ32B++17e9bRXqyg/ZpfTCfTwzdNms4xeU5aWn505ZX35CTlFu3lsPzb/W43npclnkg7O3tFiXe7bDVXQ4NTxsW/QeuxvalDf9QwWJPYIbVvXbbARsnEf0ubqaS+37Wa1r8svRE+0l7tdtfl1/XQ+vY0ZfEwiqE9G3psfR7YRkSOEv6nxLKE0rkskaMLQfFQ0Ig8Tss/Lp0Z2oOkbZ1l/8bvt6mFTNfsxQtDx9CkNOiUjTsfnxRwn+6Zt7r9Ysvh/CcWU7rPuht5dzIrZdewA+sMmKEHOSznH/ulRNe/aa0at1ZR66C09L9WiIL6v8DGd/AM=");
		batch4.setCompound(c4);
		HashMap props4 = new LinkedHashMap();
		props4.put("REACTANT_A", "AXL010798");
		props4.put("REACTANT_B", "MN-004018");
		props4.put("RegisteredId", "50");
		props4.put("Chem. Name", "Chemical4");
		props4.put("Mol. Formula", "C20H22N205");
		props4.put("Mol. Weight", "301.345");
		props4.put("Salt Code", "00");
		batch4.setBatchProperties(props4);
		try {
			batch4.setBatchNumber(new BatchNumber("87654321-0023-0004"));
		} catch (InvalidBatchNumberException e) {
			// no need to really print any or rethrow
			// e.printStackTrace();
		}
		products.add(batch4);

		ProductBatchModel batch5 = new ProductBatchModel();
		ParentCompoundModel c5 = new ParentCompoundModel();
		c5
				.setStringSketchFormat("rVbbbtNAEH2PlH/YBx6Sh652dr03iVYKLohKNEENQqgXWcY1xRDbVZKC+vfM+laSbIE4WFYSHc+cmbNziYcDQuZJ9iG9yxLSXfx0OBgOCJeEK0LYb/fTZa0lHzljDA0JAUGNEQZ/HQFVVlemjOJjRmZkk8N/1zQB1YFtaGCTJtyDRlIF0p/NtFc2bDubPUQRQ5WRonYWwG0/UURTjenU2TBj+9JYKiWIVhQEPWkMlUZUooAKpoN+R4yigAWNFCX7ng0wGkBVKXfEm9nsd8SKWahFSTC2pyhFAVhTcClZX1FAuZFd+7lR7CVKUs14U3ChNrKZ7UUjeTXhruCK6Z4T7kSJoJ3wvl0MnIJkrC04HEATWFFnoxnru28EZcBZWylhD6DRh28/dzbSdfERx+1nehbcZcOBOxqB2Zi+ohQNTFWfSpRSh9Do9ohl37PRVASKt31jeS8a9Aqqzx0YWwm2YSCuNTywc9iBBeE+EolbxRMSYe23Nh4YGawH1rg/PdwGh9VjbQl48kYG8OSNpu4fxgfL3ZDIAMpjjbD2WCsCxgOjHI9KNPWVwcEelYhx7hcPnpC24t6GsZDcIx4xviv+nJDXU3zvOiEvyx/pcpndplHyNVvGi2z9eDIcfIkXq7T7quzCxcNqnS7J2Sk+V9Xd4Nn9fVbcRWe3+ODb93K1XpY/o9jI1IqISWk2UMOFjRiD1v0CXwYdb3pb+U8+vWPAtEWn8+kR2nEl20BxURZZEi+ieZ4t0hVah7MEkmR0HR6H1wnHX8VV8faGj8PR8Ww8GydJAleXcIOGx2F4dclvGqrzuEq5isiHA2jg+WOeR5U+n4zon6P55EZbKYRlfl8+FH9V/Xw6/JlArZoLV5TJuszdSUkcR1M1FMqtGt91rqmbqXGYLMs8XmfJ3o7OYbJapfnnxeNejvP3nXmH8A7iuJ4C4nh0zcTcPLhpFm6gNxnrRu9YRMcCbifUE8ZF8/zNMr7L02LdGTG3N/8ULvBFbEgx9Dbvq7K4/R+8T5JWbc/PRuFoGmI3jbHrkhF23SiBcd2UI9eQRcLbjuTjBGahY3iB13DwCw==");
		batch5.setCompound(c5);
		HashMap props5 = new LinkedHashMap();
		props5.put("REACTANT_A", "AXL010798");
		props5.put("REACTANT_B", "MN-001265");
		props5.put("RegisteredId", "46");
		props5.put("Chem. Name", "Chemical5");
		props5.put("Mol. Formula", "C22H22N403");
		props5.put("Mol. Weight", "312.123");
		props5.put("Salt Code", "00");
		batch5.setBatchProperties(props5);
		try {
			batch5.setBatchNumber(new BatchNumber("87654321-0023-0005"));
		} catch (InvalidBatchNumberException e) {
			// no need to really print any or rethrow
			// e.printStackTrace();
		}
		products.add(batch5);

		ProductBatchModel batch6 = new ProductBatchModel();
		ParentCompoundModel c6 = new ParentCompoundModel();
		c6
				.setStringSketchFormat("tVjbbhs3EH0XoH/gQx+kBxOc4R1ogrqbFglQW0UcBEUcQ1DXW3dbXQxJSeG/75DcVSOLjiuqXSwkeXZ4eM7cKHk4YOyqbt81d23Ndhe+Gg6GAyYVk4YxEW+Ir/9c3nv2HoUQ5EhPJXdOOvp0Btx4G10Fp8eCTViP8bU7wShule9gYB+mOgJGcwM6z+ayiI14zOYIUcxx47RMiyWgLxPFLLdEJ7ERzpfCeK41yF4UqEIYx7WTURRwKawqCzGJAqE6KUaXxgYEVxAzFUK8z+a4EBvhIYnS4HyhKMMBRJdwrUWpKODo9K780BSK0twK7BIuzR6byVEwGmOHh4QbYQs7PIiSqu/w0ioG5KCF6BMOJ8AoLxMbK0TpvJFcAIo+U9KfAGNPn34hNjpU8RnS9HOFCQ9sEDDASGLjSkUZrqw3vSgnszDwLIzlUhnbj3SpT2CjIGUKhS4VZUMLqD7EGovPKe1stxi9MoWZIjbamb4ZVGlrOo5Sq5RwlFKUhzhVS6wb1MWxsTomnOYNquLyi6IiG8URwBaL0qh6GBSqvG6sUgnGdkfN0TBUMCq+Hpjlo04KZmAosuaw4MAsGeZAKIkmsyWZbd7bZcyE4DNmS8d3BtvRWZHx9gwyvAkBMrzJNXzByZn14ZaEACbjTWab8TZUXBkzycmoJNdcGoI5o5JsiHnxkNnSR+zHZkokJvHmsVlntlQMM+LJFW3enBFPCJgRT2aZI2iZzIn3TOKhNyHITIrJVWZSTK7ysEkuGPvhkn7cvGTfrj4363V720zr39v1bN5uH14OB7/N5ptm9xb9qvmnzbZZszev6LkZDtzO3t7ft8u76ZtbevDHn6vNdr36azpzuvFyKrR2e1aH0k+FQN8tf0u/uAJucxvXn//ykwBhPS26uDwT9DVAmn6j2XK1bOvZfHq1aOfNhryrSQ11PfpYvag+1kifltfL1zc4rkYvJuPJuK5ruP4AN8Exmq6r717fjKqwKDwbX3/Amw79YhZVRBI4HEBnvnpYLKZRck7Z9F8TyEVg+jyrarW4X31aPhubpxniE3v3At+G1J1vV4sQT00d4mJzUQTiEAhd7FJjUVxiVYf6w1RXHcb5erWYbdv6v8AKGOebTbP4df5wKtbVzzuEnQV3JvKl4yNA2wQuwlAJI1GGqbi/SWx0E3r9iZ3kDhfCqE2DK02e2N6d44/r2d2iWW533iKcS19jonJkvkRPxOwX3CDRe7zl96vl7f+8ZfxXie423vRtOhlVo8uuymNjVOOqHqVip5ss8a9RjePUSaPQRcta9m0kxzVOqgD7DV3Dwd8=");
		batch6.setCompound(c6);
		HashMap props6 = new LinkedHashMap();
		props6.put("REACTANT_A", "AXL010798");
		props6.put("REACTANT_B", "MN-001736");
		props6.put("RegisteredId", "48");
		props6.put("Chem. Name", "Chemical6");
		props6.put("Mol. Formula", "C21H16N404");
		props6.put("Mol. Weight", "322.343");
		props6.put("Salt Code", "00");
		batch6.setBatchProperties(props6);
		try {
			batch6.setBatchNumber(new BatchNumber("87654321-0023-0006"));
		} catch (InvalidBatchNumberException e) {
			// no need to really print any or rethrow
			// e.printStackTrace();
		}
		products.add(batch6);

		// step.setProducts((ProductBatchModel[])products.toArray(new ProductBatchModel[]{}));

		step.setProducts(products);
	}

	public void createMonomersForReaction(ReactionStepModel step) {

		ArrayList monomerBatchs = new ArrayList(2);
		ArrayList monomer_A_Batchs = new ArrayList(10);
		ArrayList monomer_B_Batchs = new ArrayList(10);

		// "Chem. Name", "Exact Mass", "Mol. Formula", "Mol. Weight", "mMoles","Salt Code", "Hazard Comments"
		// 2 A compounds
		MonomerBatchModel a_batch_1 = new MonomerBatchModel();
		ParentCompoundModel a1 = new ParentCompoundModel();
		a1
				.setStringSketchFormat("pZTLboMwEEX3/opZdAvy2LwsRUiUOAEJ7AhIW3XT//+LDo/gJu6ijUcI2XeuD2MGLKOqb42NRGSqSzM1tmMA7diOjR0nRJFzydOkEEdAAMCYzwFz8NtYoOSMAQpASfKavA22UErBmyA7W5dmyMkLEY9znimHg3pf/PPaY1PuKTTK99E/KZGrBeNEJulztWBcpKkIpLhaYKYgD6wlhEL7KDKBoT2SMYZ32tUS0mlXS9D3QhQuk187bZ6g+D2yf6XQCxHL3f1yqyqXxKOa+F5ypb6X5pnvJWjuqwQtfAJBle/NYN7nozcHRN+r5uPEU/OF+0goFu69twfQ5shKgMPl1H7qwVz7Vz2UrProOKcTK2NLcmwn3dtu0GdjS7aeZLNOmq6vXTV8vev23Ewlw1TFqPbsyQ495UtW08MbRb23G7HqyG1o9kLBvgE=");
		a1.setRegNumber("AXL002136");
		a_batch_1.setCompound(a1);
		HashMap props_a1 = new HashMap();
		// props_a1.put("COMPOUND_ID", "AXL002136");
		props_a1.put("Mol. Weight", "130.663");
		props_a1.put("Mol. Formula", "C12H1005");
		props_a1.put("No. Times Used", "3");
		props_a1.put("Salt Code", "00");
		props_a1.put("Amount(grams)", ".345");
		props_a1.put("Amount(mmol)", ".567");
		a_batch_1.setBatchProperties(props_a1);
		monomer_A_Batchs.add(a_batch_1);

		MonomerBatchModel a_batch_2 = new MonomerBatchModel();
		ParentCompoundModel a2 = new ParentCompoundModel();
		a2
				.setStringSketchFormat("rVXLjtswDLzrK3jY6xoi9QYKA2nW2RhIrIWTtEUv+/9/sbSlOMkqh1qoYATCkBxzxmIkAPpTf9rH0xmRnFTSmkBvgACAjZwWTEvmPUrjvRCAAYgjOXjd5BVCgF/E6QISjUadSIxy4UYH26X4/llWRjKLCwpTLUlbySIbRc6mHZJ0tb0gaUw7Zby9Yxn+nYUapbVPtb62l1dW5IO7uouyVlHIviCzaFOnSDVoTa51tV/6ld2VKp8XREd1ilRjEgs7JFH5O5a4hsVqr5Iv+vHUxXWK5jniXtxjL6t88YsbJp+/Opa5g6mrZRbWshB3sMxRtSK6ntj/4wvvgkFX941UQ2kaS19ijSKe7mUW1iniD0vzc/tzTaiaf7+jukS51pQol9uSl8tdmcvlvkS5PJSoh0nxExTLtwVAKnMZVUUuJ063xndUAZbaOBHtU7TUxolYauNEDE99cGUPZn7bI3oE6IY30QL8+Nj1f7txuBx/dmMrNn8OEqULfGtOwVN/7o7xMHbvQ2xFvk6nAIPd9nLYjJ+/u/59f24FWb7qzBLdxfHI8VZs2YQ92zMQRJ1ZNwcuGKIQL7zEFw==");
		a2.setRegNumber("AXL010798");
		a_batch_2.setCompound(a2);
		HashMap props_a2 = new HashMap();
		// props_a2.put("COMPOUND_ID", "AXL010798");
		props_a2.put("Mol. Weight", "67.943");
		props_a2.put("Mol. Formula", "C12H206");
		props_a2.put("No. Times Used", "3");
		props_a2.put("Salt Code", "00");
		props_a2.put("Amount(grams)", ".145");
		props_a2.put("Amount(mmol)", ".507");
		a_batch_2.setBatchProperties(props_a2);
		monomer_A_Batchs.add(a_batch_2);

		// 3 B compounds
		MonomerBatchModel b_batch_1 = new MonomerBatchModel();
		ParentCompoundModel b1 = new ParentCompoundModel();
		b1
				.setStringSketchFormat("pZPbboMwDIbv8xS+2C2RDyEm0oS0tXRDKjDRdRd7/weZC9N6SCdVEEUo/P79xTjBARTtoT0UAJhQMZAg89Y5IAEKpt6dKSX4YkR0YIM8EcpphT5yoHllUYQN/Ie4nNeUAn2FMS2iWC6xxpknossowF5K0pWUcy0F+ZJRV9ayhmK5ohx+z2hpd0EsV9ZSzrVYh7RKurKWNZSCfYwJ55NmTXJB6R+niNe/P0BOuYtqCV4j0/xFUcvL7g6PUiydp+etKlPgVg2511xl7rX3mHsNqrlq0ConGDTl3giEuVeBKPOaRJx5TSK5RzjtdqtW027X3A6g6beuBnj+2LXfzdgfu9dmrF3XF4gBqXJT8NB+Nt2wH5u3fqhdKEVn3bTdMHbH/UvtNlbju9rNGZx7suF+AA==");
		b1.setRegNumber("MN-004018");
		b_batch_1.setCompound(b1);
		HashMap props_b1 = new HashMap();
		// props_b1.put("COMPOUND_ID", "MN-004018");
		props_b1.put("Mol. Weight", "102.453");
		props_b1.put("Mol. Formula", "C15H1002");
		props_b1.put("No. Times Used", "2");
		props_b1.put("Salt Code", "00");
		props_b1.put("Amount(grams)", ".510");
		props_b1.put("Amount(mmol)", ".632");
		b_batch_1.setBatchProperties(props_b1);
		monomer_B_Batchs.add(b_batch_1);

		MonomerBatchModel b_batch_2 = new MonomerBatchModel();
		ParentCompoundModel b2 = new ParentCompoundModel();
		b2
				.setStringSketchFormat("nZLNasMwDIDvegoddk2QZdmJoQS21t0CtT2SdYe9/4NMi1dI2w1KjDH2Z+lD/gHEZpzHuUGkQB2JscR8AED0iE7pnz2EgJ9MRBqny7YzttdJQy1z5yvTXcI9/qdYd6i5Xnr76wvWrCz5YQtyy73nmitiZFstamHqaq5hMZssaFvvHNezOT3Stlrs5TZ+LCL9ylIetWg6L+MttfdUA2UZr6hdfsMtleWbXNOEGPMBBsTd+3H8ilM+p5c4DZByQ2TYO1g25/EjpnKa4msuA5je2MqVHcuUzqfnAfaCb06fvwA8aYNv");
		b2.setRegNumber("MN-001265");
		b_batch_2.setCompound(b2);
		HashMap props_b2 = new HashMap();
		// props_b2.put("COMPOUND_ID", "MN-001265");
		props_b2.put("Mol. Weight", "62.343");
		props_b2.put("Mol. Formula", "C20H1604");
		props_b2.put("No. Times Used", "2");
		props_b2.put("Salt Code", "00");
		props_b2.put("Amount(grams)", ".310");
		props_b2.put("Amount(mmol)", ".601");
		b_batch_2.setBatchProperties(props_b2);
		monomer_B_Batchs.add(b_batch_2);

		MonomerBatchModel b_batch_3 = new MonomerBatchModel();
		ParentCompoundModel b3 = new ParentCompoundModel();
		b3
				.setStringSketchFormat("pZTbTsMwDIbv8xS+4DaRnWMjoUqwdVBpbVHLuOD9HwS3DTtlCMgit7L+2J9sJ60AkO3UThIAIwa0ZFDrrRBAbjbAxejbSRZjhA+NiAJ4kdLeudlDZQnN6vEuwgZ+QpybSBmGcOVR8jIK/kbRys0ZK4VMVUY5diRJ4SWl/zOFc7XGkOZS3JFRwTm95pqg7RllKJkLe8HFMsrxjLi3YKwr6khyBWmmd5z0aboSVYyGyqZrVUVBX9/iglos2bU3m3j/nq7kk9bB33lfTtPlqnz0voxyrOWO6XKSvvrwV9XcVO3ypnOV010ey+n+phqWjAuVoVWuMjTmBP7jYR7rgSiPrYB0HhuBTKYylGxG4EDKe+PAWb0kdABNvxU1wOPbrv1sxv7QPTdjLbpeIlIwXiybU/vedMN+bF76oRZU6aSzthvG7rB/qsWGm3nlp4fBCPHAS3wB");
		b3.setRegNumber("MN-001736");
		b_batch_3.setCompound(b3);
		HashMap props_b3 = new HashMap();
		// props_b3.put("COMPOUND_ID", "MN-001736");
		props_b3.put("Mol. Weight", "62.343");
		props_b3.put("Mol. Formula", "C20H2005");
		props_b3.put("No. Times Used", "2");
		props_b3.put("Salt Code", "00");
		props_b3.put("Amount(grams)", ".103");
		props_b3.put("Amount(mmol)", ".221");
		b_batch_3.setBatchProperties(props_b3);
		monomer_B_Batchs.add(b_batch_3);

		monomerBatchs.add(monomer_A_Batchs); // ArrayList of A monomers
		monomerBatchs.add(monomer_B_Batchs); // ArrayList of B monomers

		step.setMonomers(monomerBatchs);

		// Create Plate for these monomers
		// First A plate

	}

	private NotebookPageModel createNotebookPageHeader(NotebookPageModel pagemodel) {
		pagemodel.setVersion(1);
		pagemodel.setLatestVersion("1");
		pagemodel.setUserName("BYONG");
		pagemodel.setSiteCode("SITE1");
		pagemodel.setBatchOwner("BYONG");
		pagemodel.setStatus(CeNConstants.PAGE_STATUS_OPEN);
		pagemodel.setPageType(CeNConstants.PAGE_TYPE_PARALLEL);
		pagemodel.setCenVersion("1.2");
		pagemodel.setSubject("Test Experiment");
		pagemodel.setLiteratureRef("From diffrent sources");
		pagemodel.setTaCode("DB");
		pagemodel.setProjectCode("");
		pagemodel.setCreationDateAsTimestamp(CommonUtils.getCurrentTimestamp());
		pagemodel.setCompletionDateAsTimestamp(CommonUtils.getCurrentTimestamp());
		pagemodel.setModificationDateAsTimestamp(CommonUtils.getCurrentTimestamp());
		pagemodel.setContinuedFromRxn("");
		pagemodel.setContinuedToRxn("");
		pagemodel.setProjectAlias("");
		pagemodel.setProtocolID("");
		pagemodel.setSeriesID("");
		pagemodel.setBatchCreator("BYONG");
		pagemodel.setConceptorNames("");
		pagemodel.setConceptionKeyWords("");
		pagemodel.setUssiKey("");
		pagemodel.setArchiveDate("");
		pagemodel.setSignatureUrl("");
		

		return pagemodel;
	}

	private NotebookPageModel createNotebookPageHeader4CR(NotebookPageModel pagemodel) {
		pagemodel.setVersion(1);
		pagemodel.setLatestVersion("1");
		pagemodel.setUserName("USER1");
		pagemodel.setSiteCode("SITE1");
		pagemodel.setBatchOwner("USER1");
		pagemodel.setStatus(CeNConstants.PAGE_STATUS_OPEN);
		pagemodel.setPageType(CeNConstants.PAGE_TYPE_CONCEPTION);
		pagemodel.setCenVersion("1.2");
		pagemodel.setSubject("Test Experiment");
		pagemodel.setLiteratureRef("From diffrent sources");
		pagemodel.setTaCode("DB");
		pagemodel.setProjectCode("");
		pagemodel.setCreationDateAsTimestamp(CommonUtils.getCurrentTimestamp());
		pagemodel.setCompletionDateAsTimestamp(CommonUtils.getCurrentTimestamp());
		pagemodel.setModificationDateAsTimestamp(CommonUtils.getCurrentTimestamp());
		pagemodel.setContinuedFromRxn("");
		pagemodel.setContinuedToRxn("");
		pagemodel.setProjectAlias("");
		pagemodel.setProtocolID("");
		pagemodel.setSeriesID("");
		pagemodel.setBatchCreator("BYONG");
		pagemodel.setConceptorNames("");
		pagemodel.setConceptionKeyWords("");
		pagemodel.setUssiKey("");
		pagemodel.setArchiveDate("");
		pagemodel.setSignatureUrl("");
		
		return pagemodel;
	}

	public List getNotebookPagesForUser(String userid, SessionIdentifier identifier) {
		return null;
	}

	public NotebookPageModel getNotebookPageHeaderInfo(NotebookRef nbRef, SessionIdentifier identifier) {
		return this.getDesignSynthesisPlanWithSummaryReaction("", true);
	}

	public NotebookPageModel getNotebookPageHeaderInfo(NotebookRef nbRef, int ver, SessionIdentifier identifier) {
		return this.getDesignSynthesisPlanWithSummaryReaction("", true);
	}

	public List getAllReactionsSteps(NotebookRef nbRef, SessionIdentifier identifier) {
		return null;
	}

	public List getReactionsStepDetails(NotebookRef nbRef, SessionIdentifier identifier) {
		return null;
	}

	public ReactionStepModel getAllReactionStepDetails(NotebookRef nbRef, int version, ReactionStepModel step,
			SessionIdentifier identifier) {
		return null;
	}

	public List getAllReactionStepsWithDetails(NotebookRef nbRef, SessionIdentifier identifier) {
		ArrayList reacList = new ArrayList();

		reacList.add(getIntermediateSteps("855"));
		reacList.add(getIntermediateSteps("81"));
		reacList.add(getIntermediateSteps("80"));
		return reacList;
	}

	public ReactionStepModel getSummaryReaction(NotebookRef nbRef, SessionIdentifier identifier) {
		return getIntermediateSteps("855");
	}

	public ReactionStepModel getIntermediateReactionStep(NotebookRef nbRef, int stepno, SessionIdentifier identifier) {

		if (stepno == 0) {
			return getIntermediateSteps("855");
		} else if (stepno == 1) {
			return getIntermediateSteps("80");
		} else if (stepno == 2) {
			return getIntermediateSteps("81");
		} else {
			return null;
		}

	}

	public NotebookPageModel getNotebookPageExperimentInfo(NotebookRef nbRef, SessionIdentifier identifier) {
		return null;
	}

	// Update only NotebookPage Header data
	public boolean upadateNotebookPageHeader(NotebookRef nbkRef, int version, NotebookPageHeaderModel pageHeader,
			SessionIdentifier identifier) {
		return true;
	};

	// Update only Reaction step and Reaction scheme
	public boolean updateReactionStep(ReactionStepModel model, SessionIdentifier identifier) {
		return true;
	};

	// Updates a plate and wells in it
	public boolean updateRegisteredPlate(ProductPlate registerdPlate, SessionIdentifier identifier) {
		return true;
	};

	// Updates a set of plates( wells in it )
	public boolean updateRegisteredPlates(ProductPlate[] registerdPlate, SessionIdentifier identifier) {
		return true;
	};

	// Updates a product plate and wells in it
	public boolean updateProductPlate(ProductPlate productPlate, SessionIdentifier identifier) {
		return true;
	};

	public boolean updateProductPlates(ProductPlate[] productPlates, SessionIdentifier identifier) {
		return true;
	};

	public boolean updateMonomerPlate(MonomerPlate monomerPlate, SessionIdentifier identifier) {
		return true;
	};

	public boolean updateMonomerPlates(MonomerPlate[] monomerPlates, SessionIdentifier identifier) {
		return true;
	};

	// Updates a product batch
	public boolean updateProductBatch(ProductBatchModel productBatch, SessionIdentifier identifier) {
		return true;
	};

	// updates a set of product batches ( same method call for RegisteredBatches )
	public boolean updateProductBatches(ProductBatchModel[] productBatch, SessionIdentifier identifier) {
		return true;
	};

	public boolean updateMonomerBatch(MonomerBatchModel monomerBatch, SessionIdentifier identifier) {
		return true;
	};

	public boolean updateMonomerBatches(MonomerBatchModel[] monomerBatch, SessionIdentifier identifier) {
		return true;
	};

	// add only Reaction step and Reaction scheme
	public boolean addReactionStep(NotebookRef nbRef, int version, ReactionStepModel model, SessionIdentifier identifier) {
		return true;
	};

	// add a plate and wells in it
	public boolean addRegisteredPlate(NotebookRef nbRef, int version, ProductPlate registerdPlate, SessionIdentifier identifier) {
		return true;
	};

	// add a set of plates( wells in it )
	public boolean addRegisteredPlates(NotebookRef nbRef, int version, ProductPlate[] registerdPlate,
			SessionIdentifier identifier) {
		return true;
	};

	// add a product plate and wells in it
	public boolean addProductPlate(String stepKey, ProductPlate productPlate, SessionIdentifier identifier) {
		return true;
	};

	public boolean addProductPlates(String stepKey, ProductPlate[] productPlates, SessionIdentifier identifier) {
		return true;
	};

	public boolean addMonomerPlate(String stepKey, MonomerPlate monomerPlate, SessionIdentifier identifier) {
		return true;
	};

	public boolean addMonomerPlates(String stepKey, MonomerPlate[] monomerPlates, SessionIdentifier identifier) {
		return true;
	};

	// add a product batch
	public boolean addProductBatch(String stepKey, ProductBatchModel productBatch, SessionIdentifier identifier) {
		return true;
	};

	// add a set of product batches ( same method call for RegisteredBatches )
	public boolean addProductBatches(String stepKey, ProductBatchModel[] productBatch, SessionIdentifier identifier) {
		return true;
	};

	public boolean addMonomerBatch(String stepKey, MonomerBatchModel monomerBatch, SessionIdentifier identifier) {
		return true;
	};

	public boolean addMonomerBatches(String stepKey, MonomerBatchModel[] monomerBatch, SessionIdentifier identifier) {
		return true;
	};

}
