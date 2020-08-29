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
package com.chemistry.enotebook.client.gui.page.batch;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.batch.events.PlateSelectionChangedListener;
import com.chemistry.enotebook.client.gui.page.experiment.stoich.StoichDataChangesListener;
import com.chemistry.enotebook.client.gui.page.table.PCeNProductTableModelConnector;
import com.chemistry.enotebook.domain.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class PCeNSummaryViewControllerUtility implements PCeNProductTableModelConnector {

	public static final Log log = LogFactory.getLog(PCeNSummaryViewControllerUtility.class);
	
	public static final String CPS_ON_PLATE = "<html>CPs on <br></br>Plate</html>";
	public static final String CPS_PASSED = "<html># CPs <br></br>Passed</html>";
	public static final String CPS_FAILED = "<html># CPs <br></br>Failed</html>";
	public static final String CPS_SUSPECT = "<html># CPs <br></br>Suspect</html>";
	public static final String CPS_NOT_MADE = "<html># CPs <br></br>Not Made</html>";
	public static final String CPS_REGISTERED = "<html># CPs <br></br>Registered</html>";
	public static final String CPS_PURIFIED = "<html># of CPs <br></br>Purified</html>";
	public static final String CPS_SENT_TO_SCREENING = "<html># CPs Sent to <br></br>Screening</html>";
	public static final String CONTAINER_REGISTRATION = "<html>Container <br></br>Registration</html>"; 
	public static final String PURIFICATION_STATUS = "<html>Purification <br></br>Status</html>";
	public static final String SCREENING_STATUS = "<html>Screening <br></br>Status</html>";
	public static final String PRODUCT_PLATE_STAGE = "<html>Product <br></br>Plate Stage</html>";
	public static final String ORIGINATED_IN_STEP = "<html>Originated <br></br>in Step #</html>";	
	public static final String PLATE_COMMENTS = "<html>Plate <br></br>Comments</html>";
	public static final String GENERATE_BARCODE = "<html>Generate <br></br>Barcode</html>";
	
	protected ArrayList<String> headerNames;
	protected List<ProductPlate> productPlates;
	protected List<PlateSelectionChangedListener> plateSelectionChangedListeners = new ArrayList<PlateSelectionChangedListener>();
	protected PseudoProductPlate pseudoPlate;
	private NotebookPageModel pageModel;

	public void addPlateSelectionChangedListener(PlateSelectionChangedListener plateSelectionChangedListener) {
		plateSelectionChangedListeners.add(plateSelectionChangedListener);
	}

	public PCeNSummaryViewControllerUtility(List<ProductPlate> productPlates, NotebookPageModel pageModel) {
		this.productPlates = productPlates;
		this.pageModel = pageModel;
		Collections.sort(productPlates);
		headerNames = new ArrayList<String>();
		headerNames.add("View"); //0
		headerNames.add("Product Plate"); //1
		headerNames.add(ORIGINATED_IN_STEP);//2
		headerNames.add(CPS_PASSED); //3
		headerNames.add(CPS_FAILED); //4
		headerNames.add(CPS_SUSPECT); //5
		headerNames.add(CPS_NOT_MADE); //6
	}

	public void dispose() {
		try {
			if (this.productPlates != null) {
				this.productPlates.clear();
				this.productPlates = null;
			}
			if (this.plateSelectionChangedListeners != null) {
				this.plateSelectionChangedListeners.clear();
				this.plateSelectionChangedListeners = null;
			}
			pseudoPlate = null;
			headerNames = null;
		} catch (RuntimeException e) {
			if (log.isDebugEnabled()) {
				log.debug("dispose got exception: " + e.getMessage());
			}
			CeNErrorHandler.getInstance().logExceptionMsg(e);
		}
	}

	public String[] getHeaderNames() {
		return headerNames.toArray(new String[]{});
	}

	public Object getValue(int rowIndex, int colIndex) {
		if (productPlates == null)
			return " ";
		ProductPlate plate = (ProductPlate) productPlates.get(rowIndex);
		if (colIndex == 0) {//"Select to View"
			return new Boolean(plate.isSelect());
		} else if (colIndex == 1) {//"Product Plate" 1
			return (plate.getPlateNumber().equals(CeNConstants.PLATE_TYPE_PRODUCT_PSEUDO) ? CeNConstants.PSEUDO_PLATE_LABEL : plate.getPlateNumber());
		} else if (colIndex == 2) {//"Originated in Step #" 2
			if (plate instanceof PseudoProductPlate  || plate.getPlateType().equals(CeNConstants.PLATE_TYPE_REGISTRATION))
				return "N/A";
			else
				return plate.getStepNumber();
		} else if (colIndex == 3) {//"# of Cmpds Passed" 3
			return plate.getNumPassed() + "";
		} else if (colIndex == 4) {//"# of Cmpds Failed" 4
			return plate.getNumFailed() + "";
		} else if (colIndex == 5) {//"# of Cmpds Suspect" 5
			return plate.getNumSuspect() + "";
		} else if (colIndex == 6) {//"Compounds not Made" 6
			return plate.getNotMade() + "";
		}
		else if (headerNames.get(colIndex) == PLATE_COMMENTS) {//"Plate Comments"
			return  (plate.getPlateComments() == null) ? "" : plate.getPlateComments(); 
		}
		return new String("In development");
	}
	
	protected boolean isCellEditable(String columnName) {
		if (!pageModel.isEditable())
			return false;
		if (
				columnName.equalsIgnoreCase("View")
				|| columnName .equalsIgnoreCase("Product Plate")
				|| columnName.equalsIgnoreCase(ORIGINATED_IN_STEP)
				|| columnName.equalsIgnoreCase(CPS_PASSED)
				|| columnName.equalsIgnoreCase(CPS_FAILED)
				|| columnName.equalsIgnoreCase(CPS_SUSPECT)
				|| columnName.equalsIgnoreCase(CPS_NOT_MADE)
			) {
			return false;
		} else
			return true;
	}
	public abstract boolean isCellEditable(int rowIndex, int colIndex);
		
	public boolean isColumnEditable(String columnName) {
		if (columnName.equals(PLATE_COMMENTS) ) 
			return true;
		 else
			return false;
	}
	
	public void setValue(Object value, int rowIndex, int colIndex) {
		ProductPlate plate = productPlates.get(rowIndex);
/*		if (colIndex == 0) {
			plate.setSelect(((Boolean) value).booleanValue());

			if (mPlateSelectionChangedListener != null) {
				PlateSelectionChangedEvent mPlateSelectionChangedEvent = new PlateSelectionChangedEvent(this, plate,
						getSelectedPlates());
				mPlateSelectionChangedListener.plateSelectionChanged(mPlateSelectionChangedEvent);
			}

			Boolean boo = (boolean)value;
			plate.setSelect(boo.booleanValue());
		} else if (colIndex == 1) {//"Product Plate", //1
			plate.setPlateNumber((String) value);
			} else if (colIndex==4) {
				return plate.getNumPassed()+"";
			} else if (colIndex==5) {
				return plate.getNumFailed() + "";
			} else if (colIndex==6) {
				return plate.getNumSuspect() + "";
			} else if (colIndex==3) {
				return plate.getListOfProductBatches().size() + "";
		} else if (colIndex == 2) {//"Originated in Step #",//2
			plate.setStepNumber((String) value);
		}  */
		 if (headerNames.get(colIndex) == PLATE_COMMENTS) {//"Plate barcode" };//11
				plate.setPlateComments(value.toString());
				if (plate instanceof PseudoProductPlate) {
				  if (this.pageModel.getPseudoProductPlate(false) != null) {
				    pageModel.getPseudoProductPlate(false).setPlateComments(value.toString());
				  }
				}
				this.enableSaveButton();
		} 
	}
	
	public void enableSaveButton() {
		MasterController.getGUIComponent().enableSaveButtons();
	}


	public boolean isStructureHidden() {
		return true;
	}

	//Method not applicable in this controller. Need to revise.
	public List getAbstractBatches() {
		return null;
	}
	
	//Method not applicable in this controller. Need to revise.
	public int getRowCount() {
		if (productPlates == null)
			return 0;
		return productPlates.size();
	}
	
	/**
	 * @return the productPlates
	 */
	public List<ProductPlate> getProductPlates() {
		return productPlates;
	}

	public void addProductPlates(List<ProductPlate> pPlates) {
		productPlates.addAll(pPlates);
		Collections.sort(productPlates);
	}

	public void replacePseudoProductPlate(PseudoProductPlate pseudoPlate) {
		ProductPlate tempPlate = null;
		for (int i=0; i<productPlates.size(); i++)
		{
			tempPlate = productPlates.get(i);
			if (tempPlate instanceof PseudoProductPlate)
			{
				pseudoPlate.setSelect(tempPlate.isSelect());
				productPlates.add(i, pseudoPlate);
				productPlates.remove(i + 1);
				return;
			}
		}
		productPlates.add(pseudoPlate);
	}

	public void removeProductPlates(List<ProductPlate> plates) {
		for (Iterator<ProductPlate> iterator = plates.iterator(); iterator.hasNext();) {
			ProductPlate plate = iterator.next();
			productPlates.remove(plate);	
		}
		Collections.sort(productPlates);
	}

	/**
	 * @param productPlates
	 *            the productPlates to set
	 */
	public void setProductPlates(List<ProductPlate> productPlates) {
		this.productPlates = productPlates;
	}
	
	public void removeBatchFromPseudoPlate(ProductBatchModel batch) {
		if (pseudoPlate != null)
			pseudoPlate.removeBatch(batch);
	}	
	
	public String getTableHeaderTooltip(String headerName) {
		return null;
	}

	public boolean isSortable(int col) {
		return true;
	}

	//	This method is not applicable here
	public List getStoicElementListInTransactionOrder() {
		return null;
	}
	
	public void addProductBatchModel(ProductBatchModel batchModel) {
	}

	public void addProductBatchModel(ProductBatchModel batch,
			ProductPlate pseudoPlate) {
	}

	public StoicModelInterface getBatchModel(int selectedRowIndex) {
		return null;
	}

	public boolean isMoreDeletableRows(ReactionStepModel reactionStepModel) {
		return false;
	}

	public void removeProductBatchModel(
			ProductBatchModel selectedProductBatchModel) {
	
	}

	public void updateProductBatchModel(ProductBatchModel batch) {
	}

	//Let it go with super class implementation
/*	public boolean isColumnEditable(String columnName) {
		return super.isColumnEditable(columnName);
	}*/

	public void addStoichDataChangesListener(StoichDataChangesListener stoichDataChangesListener)
		throws Exception {}

	public void updateColumn(String cloumnname, Object newValue) {
	}

	public void sortBatches(int colIndex, boolean ascending) {
	}

	public NotebookPageModel getPageModel() {
		return pageModel;
	}
}
