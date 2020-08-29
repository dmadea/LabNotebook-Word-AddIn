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
package com.chemistry.enotebook.client.gui.page.regis_submis;

import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.reagents.ProgressBarDialog;
import com.chemistry.enotebook.client.gui.page.regis_submis.uc.JDialogUniquenessCheck;
import com.chemistry.enotebook.client.gui.page.regis_submis.uc.UniquenessCheckTableModel;
import com.chemistry.enotebook.delegate.VnvDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.datamodel.compound.Compound;
import com.chemistry.enotebook.formatter.UtilsDispatcher;
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ChemUtilInitException;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.utils.*;
import com.chemistry.enotebook.utils.SwingWorker;
import com.chemistry.enotebook.utils.sdf.SdUnit;
import com.chemistry.enotebook.vnv.classes.UcCompoundInfo;
import com.chemistry.viewer.ChemistryViewer;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class PCeNStructureVnVContainer extends CeNDialog {
	
	private static final long serialVersionUID = -4581475658503830821L;
	
	private JTextField jTextFieldPFNum;
	private JLabel jLabelPFNum;
	private JTextField jTextFieldParent;
	private JLabel jLabelParent;
	private JTextField jTextFieldBatch;
	private JLabel jLabel6;
	private ChemistryPanel VnVchimeProSwing;
	private JLabel jLabelErrorMsg;
	private JTextArea jTextAreaErrorMessage;
	private CeNComboBox jComboBoxSICBatch;

	private JLabel jLabel8;
	private JTextArea jTextAreaStruComments;
	private JLabel jLabel5;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JPanel jPanelBatchStructure;
	private JPanel jPanelVnvStructure;
	private JButton jButtonVnV;
	private JLabel jLabel4;
	private CeNComboBox jComboBoxStereoIsoCodeResult;
	private JPanel jPanel2;
	private ChemistryViewer structQueryCanvas;
	private Map<String, String> sicMap;
	private Map<String, String> saltCodeMap;
	private NotebookPageGuiInterface parentDialog;
	private ProductBatchModel selectedBatch;
	public static String SELECT_SALT_CODE_ALERT = "Batch structure, MF, MW different from parent structure, MW, MF.";
	private BatchVnVInfoModel vnVResultVO;
	// private HashMap vnvStructures = new HashMap();
	private ProgressBarDialog progressBarDialog = null;
	private boolean isInit = false;
	private VnVPassedListener vnVPassedListener; 	

    private Runnable callback;

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    public PCeNStructureVnVContainer(VnVPassedListener vnVPassedListener, Frame owner, boolean modal) {
    	super(owner, modal);
		initGUI();
		this.vnVPassedListener = vnVPassedListener;
    }
    
	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			
			setTitle("Structure Validation & Verification");
			
			jPanel2 = new JPanel();
			jTextFieldPFNum = new JTextField();
			jLabelPFNum = new JLabel();
			jTextFieldParent = new JTextField();
			jLabelParent = new JLabel();
			jTextFieldBatch = new JTextField();
			jLabelErrorMsg = new JLabel();
			jTextAreaErrorMessage = new JTextArea();
			jComboBoxStereoIsoCodeResult = new CeNComboBox();
			jLabel4 = new JLabel();
			jButtonVnV = new JButton();
			jPanelVnvStructure = new JPanel();
			VnVchimeProSwing = new ChemistryPanel();
			jPanelBatchStructure = new JPanel();
			jLabel2 = new JLabel();
			jLabel1 = new JLabel();
			jTextAreaStruComments = new JTextArea();
			jLabel8 = new JLabel();
			jComboBoxSICBatch = new CeNComboBox();
			jLabel6 = new JLabel();
			jLabel5 = new JLabel();
			this.setPreferredSize(new java.awt.Dimension(540, 435));
			GridBagLayout jPanel2Layout = new GridBagLayout();
			jPanel2.setLayout(jPanel2Layout);
			jPanel2.setPreferredSize(new java.awt.Dimension(536, 431));
			this.add(jPanel2);
			//this.setViewportView(jPanel2);
			jTextFieldPFNum.setVisible(true);
			jTextFieldPFNum.setEnabled(false);
            final Dimension baseDimension = new Dimension(130, 27);
            final Dimension extendedDimension = new Dimension(170, 27);
            setSize(jTextFieldPFNum, baseDimension);

			jTextFieldPFNum.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jTextFieldPFNumActionPerformed(evt);
				}
			});
			jLabelPFNum.setText("Compound #:");
			jLabelPFNum.setVisible(true);
            final Font font = new Font("Sansserif", 1, 11);
            jLabelPFNum.setFont(font);
			setSize(jLabelPFNum, baseDimension);

			jTextFieldParent.setEditable(false);
			jTextFieldParent.setVisible(true);
            setSize(jTextFieldParent, extendedDimension);

			jLabelParent.setText("Parent MW, MF:");
			jLabelParent.setVisible(true);
			jLabelParent.setFont(font);
			setSize(jLabelParent, baseDimension);

			jTextFieldBatch.setEditable(false);
			jTextFieldBatch.setVisible(true);
			setSize(jTextFieldBatch, extendedDimension);

			jLabelErrorMsg.setText("VnV Results / Error Messages:");
			jLabelErrorMsg.setVisible(true);
			jLabelErrorMsg.setFont(font);
            setSize(jLabelErrorMsg, extendedDimension);
			jLabelErrorMsg.setRequestFocusEnabled(false);


			jTextAreaErrorMessage.setEditable(false);
			jTextAreaErrorMessage.setVisible(true);
			jTextAreaErrorMessage.setLineWrap(true);
			setSize(jTextAreaErrorMessage, new Dimension(290, 60));
			jTextAreaErrorMessage.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			JScrollPane resultScrollingArea = new JScrollPane(jTextAreaErrorMessage);
			resultScrollingArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

			// jPanel2.add(jTextAreaErrorMessage, new
			// AnchorConstraint(834,474, 981, 19, 1, 1, 1, 1));
			jComboBoxStereoIsoCodeResult.setVisible(true);
            setSize(jComboBoxStereoIsoCodeResult, extendedDimension);

			jComboBoxStereoIsoCodeResult.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
                    if (!isInit)
					    jComboBoxStereoIsoCodeResultItemStateChanged(evt);
				}
			});
			jLabel4.setText("Batch MW, MF:");
			jLabel4.setVisible(true);
			jLabel4.setFont(font);
			setSize(jLabel4, baseDimension);
			jLabel4.setRequestFocusEnabled(false);

            jButtonVnV.setFont(font);
            jButtonVnV.setText("<html><center>VnV&Novelty<br>Check</center></html>");
			jButtonVnV.setVisible(true);
			jButtonVnV.setOpaque(false);


			jButtonVnV.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonVnVActionPerformed(evt);
				}
			});
			BorderLayout jPanelVnvStructureLayout = new BorderLayout();
			jPanelVnvStructure.setLayout(jPanelVnvStructureLayout);
			jPanelVnvStructureLayout.setHgap(0);
			jPanelVnvStructureLayout.setVgap(0);
			jPanelVnvStructure.setVisible(true);
			jPanelVnvStructure.setBackground(new java.awt.Color(255, 255, 255));
            final Dimension structureDimension = new Dimension(300, 270);
            setSize(jPanelVnvStructure, structureDimension);
			jPanelVnvStructure.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

			jPanelVnvStructure.add(VnVchimeProSwing, BorderLayout.CENTER);
			BorderLayout jPanelBatchStructureLayout = new BorderLayout();
			jPanelBatchStructure.setLayout(jPanelBatchStructureLayout);
			jPanelBatchStructureLayout.setHgap(0);
			jPanelBatchStructureLayout.setVgap(0);
			jPanelBatchStructure.setVisible(true);
			jPanelBatchStructure.setBackground(new java.awt.Color(255, 255, 255));

			jPanelBatchStructure.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			setSize(jPanelBatchStructure, structureDimension);

			final Dimension topLabelsDimension = new Dimension(249, 27);
            jLabel2.setText(" CompoundRegistration-Validated Parent Structure:"); // ,
			// Salt
			// Code/Name:");
			jLabel2.setVisible(true);
			jLabel2.setFont(new java.awt.Font("Dialog", 1, 10));
            setSize(jLabel2, topLabelsDimension);

			jLabel1.setText(" Validate & Verify Your Product Batch Structure:");
			jLabel1.setVisible(true);
			jLabel1.setFont(new java.awt.Font("Dialog", 1, 10));
            setSize(jLabel1, topLabelsDimension);

            final Dimension structureCommentsDimension = new Dimension(170, 50);

            setSize(jTextAreaStruComments, structureCommentsDimension);
            jTextAreaStruComments.setAutoscrolls(false);
			jTextAreaStruComments.setLineWrap(true);
			jTextAreaStruComments.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

			JScrollPane scrollingArea = new JScrollPane(jTextAreaStruComments);
			scrollingArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

			jTextAreaStruComments.getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate(DocumentEvent e) {
					if (!isInit && selectedBatch != null && selectedBatch.getCompound() != null)
						selectedBatch.getCompound().setStructureComments(jTextAreaStruComments.getText().trim());
				}

				public void removeUpdate(DocumentEvent e) {
					if (!isInit && selectedBatch != null && selectedBatch.getCompound() != null)
						selectedBatch.getCompound().setStructureComments(jTextAreaStruComments.getText().trim());
				}

				public void changedUpdate(DocumentEvent e) {
					if (!isInit && selectedBatch != null && selectedBatch.getCompound() != null)
						selectedBatch.getCompound().setStructureComments(jTextAreaStruComments.getText().trim());
				}
			});
            jTextAreaStruComments.addFocusListener(new FocusAdapter() {
                public void focusLost(FocusEvent e) {
                    callback.run();
                }
            });
			jLabel8.setText("Stereoisomer Code:");
			jLabel8.setFont(font);
            setSize(jLabel8, baseDimension);

			jComboBoxSICBatch.setVisible(true);
			setSize(jComboBoxSICBatch, extendedDimension);

			jComboBoxSICBatch.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					jComboBoxSICBatchItemStateChanged(evt);
				}
			});
			jLabel6.setText("Stereoisomer Code:");
			jLabel6.setVisible(true);
			jLabel6.setFont(font);
			setSize(jLabel6, baseDimension);
			jLabel6.setRequestFocusEnabled(false);

			jLabel5.setText(" Structure Comments:");
			jLabel5.setFont(font);
			setSize(jLabel5, baseDimension);

            final JButton okButton = new JButton("OK");
            okButton.setFont(font);
            okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					defaultCancelAction();
				}
			});

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(2,2,2,2);
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            gbc.gridy = 0;
            jPanel2.add(jLabel1, gbc);
            gbc.gridx += 3;
            jPanel2.add(jLabel2, gbc);
            
            gbc.gridy +=1;
            gbc.gridx  = 0;
            jPanel2.add(jPanelBatchStructure, gbc);
            gbc.gridx = 2;
            gbc.gridwidth = 1;
            jPanel2.add(jButtonVnV, gbc);
            gbc.gridwidth = 2;
            gbc.gridx = 3;
            jPanel2.add(jPanelVnvStructure, gbc);

            gbc.gridy +=1;
            gbc.gridx  = 0;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            jPanel2.add(jLabel6, gbc);
            gbc.gridx++;
            jPanel2.add(jComboBoxSICBatch, gbc);
            gbc.gridx++;
            gbc.gridx++;
			jPanel2.add(jLabel8, gbc);
            gbc.gridx++;
            jPanel2.add(jComboBoxStereoIsoCodeResult, gbc);


            gbc.gridy +=1;
            gbc.gridx  = 0;

            jPanel2.add(jLabel4, gbc);
            gbc.gridx++;
            jPanel2.add(jTextFieldBatch, gbc);
            gbc.gridx++;
            gbc.gridx++;
            jPanel2.add(jLabelParent, gbc);
            gbc.gridx++;
            jPanel2.add(jTextFieldParent, gbc);

            gbc.gridy +=1;
            gbc.gridx  = 0;
            gbc.gridwidth = 2;
            jPanel2.add(jLabelErrorMsg, gbc);
            gbc.gridx  = 3;
            gbc.gridwidth = 1;
            jPanel2.add(jLabel5, gbc);
            gbc.gridx++;
            gbc.gridheight = 2;
            jPanel2.add(scrollingArea, gbc);

            gbc.gridy +=1;
            gbc.gridx  = 0;
            gbc.gridwidth = 2;
            gbc.gridheight = 3;
            jPanel2.add(resultScrollingArea, gbc);


            gbc.gridx  = 3;
            gbc.gridheight = 1;
            gbc.gridwidth = 1;
            gbc.gridy++;
            jPanel2.add(jLabelPFNum, gbc);
            gbc.gridx++;
            jPanel2.add(jTextFieldPFNum, gbc);

            gbc.gridy +=2;
            gbc.gridx  = 2;
            gbc.gridheight = 1;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.SOUTH;
            jPanel2.add(okButton, gbc );
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

    private void setSize(JComponent component, Dimension structureDimensions) {
        component.setPreferredSize(structureDimensions);
        component.setSize(new Dimension(structureDimensions));
    }

    public static void main(String[] args) {
        showGUI();
    }
	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		try {
			structQueryCanvas = new ChemistryViewer(MasterController.getGUIComponent().getTitle(), "Structure");
			structQueryCanvas.setReadOnly(true);
			jPanelBatchStructure.add(structQueryCanvas, BorderLayout.CENTER);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		// populate drop downs
		setSaltCodeMap(RegCodeMaps.getInstance().getSaltCodeMap());
		sicMap = RegCodeMaps.getInstance().getStereoisomerCodeMap();
		populateDropDowns();
	}

	public void populateDropDowns() {
		// populate salt code / name drop downs
		// jComboBoxVnVSaltCode.removeAllItems();
		// Iterator scIterator = saltCodeMap.keySet().iterator();
		// while(scIterator.hasNext()){
		// String saltCode = (String)scIterator.next();
		// String saltName = (String)saltCodeMap.get(saltCode);
		// jComboBoxVnVSaltCode.addItem(saltCode + " - " + saltName);
		// }
		// populate stereoisomer code / name drop downs
		CodeTableUtils.fillComboBoxWithIsomers(jComboBoxSICBatch);
	}

	public String getSelectedSIC() {
		String selectedItem = (String) jComboBoxSICBatch.getSelectedItem();
		return (selectedItem == null) ? "" : selectedItem.substring(0, 5);
	}

	public void updateBatchDisplay() {
		isInit = true;
		boolean isEditable = isEditable();
		jComboBoxSICBatch.setEnabled(isEditable);
		jComboBoxStereoIsoCodeResult.setEnabled(isEditable);
		jTextFieldBatch.setEnabled(isEditable);
		jTextFieldParent.setEnabled(isEditable);
		jTextAreaErrorMessage.setEnabled(isEditable);
		jTextAreaStruComments.setEnabled(isEditable);
		jButtonVnV.setEnabled(isEditable);

		// show structure
		structQueryCanvas.setChemistry(selectedBatch.getCompound().getNativeSketch());

		// show batch MW and MF
		if (selectedBatch.getMolWgt() == 0 && StringUtils.isBlank(selectedBatch.getMolecularFormula()))
			jTextFieldBatch.setText("");
		else {
			jTextFieldBatch.setText(formatWeight(selectedBatch.getMolWgt()) + ", " + selectedBatch.getMolecularFormula());
		}
		// set structure comments
		if (StringUtils.isNotBlank(selectedBatch.getCompound().getStructureComments())) {
			jTextAreaStruComments.setText(selectedBatch.getCompound().getStructureComments());
		} else {
			jTextAreaStruComments.setText("");
		}
		jTextAreaStruComments.setCaretPosition(0);
		// Jags_todo... Check the VnV status that gets assigned from server side and update below code.
		if (this.vnVResultVO != null && this.vnVResultVO.isPassed()) {
			// update structure
			if (StringUtils.isNotBlank(selectedBatch.getRegInfo().getBatchVnVInfo().getMolData())) {
				VnVchimeProSwing.setMolfileData(selectedBatch.getRegInfo().getBatchVnVInfo().getMolData());
			} else {
				VnVchimeProSwing.setMolfileData("");
			}
			if (selectedBatch.getCompound().getMolWgt() == 0 && StringUtils.isBlank(selectedBatch.getCompound().getMolFormula())) {
				jTextFieldParent.setText("");
			} else {
				jTextFieldParent.setText(formatWeight(selectedBatch.getCompound().getMolWgt()) + ", "
						+ selectedBatch.getCompound().getMolFormula());
			}
			jTextAreaErrorMessage.setText(selectedBatch.getRegInfo().getBatchVnVInfo().getErrorMsg());
			jTextAreaErrorMessage.setCaretPosition(0);
		} 
		//VNV Fails
		else if(this.vnVResultVO != null && this.vnVResultVO.isFailed()){
			jTextAreaErrorMessage.setText(this.vnVResultVO.getErrorMsg());
			//Set red color for the error message
			//jTextAreaErrorMessage.set
			jTextAreaErrorMessage.setCaretPosition(0);
			jTextFieldParent.setText("");
			VnVchimeProSwing.setMolfileData("");
		}
		else {
			jTextAreaErrorMessage.setText("");
			jTextFieldParent.setText("");
			VnVchimeProSwing.setMolfileData("");
		}
		if (StringUtils.isNotBlank(selectedBatch.getCompound().getRegNumber())) {
			jTextFieldPFNum.setText(selectedBatch.getCompound().getRegNumber());
		} else {
			jTextFieldPFNum.setText("");
		}
		jTextFieldBatch.requestFocus();
		jButtonVnV.requestFocus();
		isInit = false;
	}

    private void addStereoIsoCodeForSelection(String sicode) {
        jComboBoxStereoIsoCodeResult.addItem(sicode + " - " + sicMap.get(sicode));
    }

    public boolean showVnVResult() {
		boolean result = true;
		// update structure
		VnVchimeProSwing.setMolfileData(vnVResultVO.getMolData());
		jTextFieldBatch.requestFocus();
		jButtonVnV.requestFocus();
		// update sic
		jComboBoxStereoIsoCodeResult.removeAllItems();
		ArrayList<String> sicList = vnVResultVO.getSuggestedSICList();
		if (sicList.size() > 0) {
			int selectedSIC = -1;
			int defaultSIC = -1;
			String sicode = "";
			for (int i = 0; i < sicList.size(); i++) {
				sicode = sicList.get(i);
				if (!getSelectedSIC().equals("HSREG")) {
					if (sicode.endsWith(getSelectedSIC())) {
						selectedSIC = i;
					}
					if (sicode.endsWith(vnVResultVO.getAssignedStereoIsomerCode())) {
						defaultSIC = i;
					}
				} else if (!vnVResultVO.getAssignedStereoIsomerCode().startsWith("ERROR")) {
					if (sicode.endsWith(vnVResultVO.getAssignedStereoIsomerCode())) {
						selectedSIC = i;
					}
				}
                addStereoIsoCodeForSelection(sicode);
            }
			if (selectedSIC == -1 && defaultSIC > 0) {
				jComboBoxStereoIsoCodeResult.setSelectedIndex(defaultSIC);
			} else {
				jComboBoxStereoIsoCodeResult.setSelectedIndex(selectedSIC);
			}
			result = !vnVResultVO.getAssignedStereoIsomerCode().startsWith("ERROR");
		} else {
            String code = vnVResultVO.getAssignedStereoIsomerCode();
            if (StringUtils.isBlank(code)) {
                code = getSelectedSIC();
            }
            addStereoIsoCodeForSelection(code);
            BatchAttributeComponentUtility.updateStereoisomerComboBox(selectedBatch, jComboBoxStereoIsoCodeResult);
		}
		// update MF, MW
		jTextFieldParent.setText(formatWeight(vnVResultVO.getMolWeight()) + ", " + vnVResultVO.getMolFormula());
		// update message
		// check if VnV structure, MW, or MF are different than that of batch
		// note compareBatchVnV is only comparing MW and MF
		if (compareBatchVnV()) {
			jTextAreaErrorMessage.setText(vnVResultVO.getErrorMsg() + "\n" + PCeNStructureVnVContainer.SELECT_SALT_CODE_ALERT);
		} else {
			jTextAreaErrorMessage.setText(vnVResultVO.getErrorMsg());
		}
		jTextAreaErrorMessage.setCaretPosition(0);
		return result;
	}

    private String formatWeight(double weight) {
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(3);
        return formatter.format(weight);
    }

    public boolean compareBatchVnV() {
		ParentCompoundModel c = selectedBatch.getCompound();
		return (Math.round(vnVResultVO.getMolWeight() * 100) != Math.round(c.getMolWgt() * 100)
						  || !StringUtils.equals(removeWhites(vnVResultVO.getMolFormula()), removeWhites(c.getMolFormula())));
	}

	private String removeWhites(String s) {
		StringBuffer stringbuffer = new StringBuffer();
		char ac[] = s.toCharArray();
		for (int i = 0; i < ac.length; i++)
			if (ac[i] > ' ')
				stringbuffer.append(ac[i]);
		return stringbuffer.toString();
	}

	/**
	 * This static method creates a new instance of this class and shows it inside a new JFrame, (unless it is already a JFrame).
	 * 
	 * It is a convenience method for showing the GUI, but it can be copied and used as a basis for your own code. * It is
	 * auto-generated code - the body of this method will be re-generated after any changes are made to the GUI. However, if you
	 * delete this method it will not be re-created.
	 */
	public static void showGUI() {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			PCeNStructureVnVContainer inst = new PCeNStructureVnVContainer(null, frame, false);
            inst.setSize(760, 450);
            inst.pack();
            inst.setVisible(true);
			//frame.setContentPane(inst);
			//frame.getContentPane().setSize(inst.getSize());
			//frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
			//frame.pack();
			//frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Returns the saltCodeMap.
	 */
	public Map<String, String> getSaltCodeMap() {
		return saltCodeMap;
	}

	/**
	 * @param saltCodeMap
	 *            The saltCodeMap to set.
	 */
	public void setSaltCodeMap(Map<String, String> saltCodeMap) {
		this.saltCodeMap = saltCodeMap;
	}

	/** Auto-generated event handler method */
	protected void jButtonVnVActionPerformed(ActionEvent evt) {
		if (!isEditable()) {
			JOptionPane.showMessageDialog((JInternalFrame) parentDialog, "The batch is currently not editable.");
		} else if (getSelectedBatch() != null) {
			if (selectedBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMISION_PENDING)) {
				JOptionPane.showMessageDialog((JInternalFrame) parentDialog,
											  "The selected batch is currently in the process of registration.");
			} else if (selectedBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMISION_PASS)) {
				JOptionPane.showMessageDialog((JInternalFrame) parentDialog, "The selected batch is already registered.");
			} else {
				if (structQueryCanvas.getChemistry() == null)
					JOptionPane.showMessageDialog((JInternalFrame) parentDialog, "A structure is required to perform VnV.");
				else {
					progressBarDialog = new ProgressBarDialog(MasterController.getGUIComponent());
					progressBarDialog.setTitle("Performing VnV, please wait ...");
					performVnV(true);
					progressBarDialog.setVisible(true);
				}
			}
		} else
			JOptionPane.showMessageDialog(this, "Please select a batch first.");
	}

	public void performVnV(final boolean doUC) 
	{
		final SwingWorker worker = new SwingWorker() {
				public Object construct() {
			
				Boolean result = new Boolean(false);
				Object objLatch = new Object();
				BatchVnVInfoModel batchVnvInfo = null;
				try {
					String vnvSDFile = buildVnVSDFIle();
					if (StringUtils.isNotBlank(vnvSDFile)) {
						VnvDelegate vnvDelegate = new VnvDelegate();
						batchVnvInfo = vnvDelegate.performVnV(vnvSDFile, getSelectedSIC());
						// System.out.println("The vnv result is: " + vnvResult);
						if (batchVnvInfo != null) {
							PCeNStructureVnVContainer.this.setVnVResultVO(batchVnvInfo);						
							if (batchVnvInfo.isPassed()) {
									PCeNStructureVnVContainer.this.updateRegInfo(vnVResultVO);
									javax.swing.SwingUtilities.invokeLater(new Runnable() {
										public void run() {
											// update registration info display
											//((SingletonNotebookPageGUI) PCeNStructureVnVContainer.this.getParentDialog())
											//		.getCompReg_cont().setSelectedBatch(PCeNStructureVnVContainer.this.getSelectedBatch());
											//((SingletonNotebookPageGUI) PCeNStructureVnVContainer.this.getParentDialog())
											//		.getCompReg_cont().updateBatchList(PCeNStructureVnVContainer.this.getSelectedBatch());
											boolean stillDoUC = showVnVResult();
											if (doUC && stillDoUC)
												SubmitForUniquenessCheck();
											else {
												progressBarDialog.setVisible(false);
												progressBarDialog.dispose();
												if (!stillDoUC)
													JOptionPane.showMessageDialog(PCeNStructureVnVContainer.this,
																	"VnV result: Could not determine a default Stereoisomer Code.\n"
																	+ "                   See VnV Results / Error Messages for details.\n"
																	+ "                   You may need to specify an actual SIC instead of HSREG.");
											}
										}
									});
									result = new Boolean(true);
								//} else {
								//	progressBarDialog.setVisible(false);
								//	progressBarDialog.dispose();
								//	JOptionPane.showMessageDialog(PCeNStructureVnVContainer.this,
								//			"VnV result: Structure failed validation.\n" + "                   " + vnvResultCheck);
								//}
							} else {
                                jTextAreaErrorMessage.setText(vnVResultVO.getErrorMsg());
                                jTextAreaErrorMessage.setCaretPosition(0);
                                
								progressBarDialog.setVisible(false);
								progressBarDialog.dispose();
								//JOptionPane.showMessageDialog(PCeNStructureVnVContainer.this, vnVErrorMsg);
							}
						}
					}
				} catch (Exception e) {
					progressBarDialog.setVisible(false);
					progressBarDialog.dispose();
					CeNErrorHandler.getInstance().logErrorMsg(null, e.toString(), "External service is down.",
							JOptionPane.INFORMATION_MESSAGE);
				} finally {
					synchronized (objLatch) {
						objLatch.notify();
					}
				}
				return result;
			}

			public void finished() {
				Boolean result = (Boolean) get();
				if (!result.booleanValue()) {
					progressBarDialog.setVisible(false);
					progressBarDialog.dispose();
				}
				//updateBatchDisplay();   - should be used only for new batches, the VnV should be processed with its own code
				//  TODO Bo Yang 3/14/07
				//((SingletonNotebookPageGUI) PCeNStructureVnVContainer.this.getParentDialog()).getCompReg_cont().updateDisplay();
			}
		};
		worker.start();
	}

	private void SubmitForUniquenessCheck() {
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				UniquenessCheckTableModel model = null;
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						progressBarDialog.setTitle("Performing Novelty Check, Please Wait ...");
					}
				});
				ParentCompoundModel cp = getSelectedBatch().getCompound();
                model = new UniquenessCheckTableModel(getSelectedBatch().getRegInfo().getBatchVnVInfo().getMolData().getBytes(),
                        cp.getStereoisomerCode(), cp.getMolWgt(), cp.getMolFormula(), cp.getComments(), true);
				return model;
			}

			public void finished() {
				UniquenessCheckTableModel model = (UniquenessCheckTableModel) get();
				progressBarDialog.setVisible(false);
				progressBarDialog.dispose();
				UcCompoundInfo result = null;
				if (model != null && model.getUcResults() != null) {
					JDialogUniquenessCheck dlg = new JDialogUniquenessCheck(MasterController.getGUIComponent());
					result = dlg.displayDialog(model);
				} // else {
				// JOptionPane.showMessageDialog(StructureVnVContainer.this, "Failed to perform Novelty Check.");
				// }
				// update others
                if (result != null && StringUtils.isNotBlank(result.getRegNumber())) {
                    final String newIsomercode = result.getIsomerCode();
                    if (isValidUcIsomerCode(newIsomercode)) {
	                    BatchAttributeComponentUtility.updateStereoisomerComboBox(jComboBoxStereoIsoCodeResult, newIsomercode);
	                    // selectedBatch.getRegInfo().setCompoundSource("KNOWN");
	                    // selectedBatch.getRegInfo().setCompoundSourceDetail("KNWNT");
	                    jTextFieldPFNum.setText(result.getRegNumber());
	                    if (result.getComments() != null && result.getComments().length() > 0) {
	                        jTextAreaStruComments.setText(result.getComments());
	                        jTextAreaStruComments.setCaretPosition(0);
	                    }
	                    // Copy the structure over to the batch record
	                    try {
	                        int fmt = selectedBatch.getCompound().getFormat();
	                        byte[] structUC = result.getMolStruct().getBytes();
	                        byte[] structBatch = null;
	                        ChemistryDelegate chemDel = new ChemistryDelegate();
	                        if ((fmt == Compound.ISISDRAW || fmt == Compound.CHEMDRAW) && structUC != null
	                                && structUC.length > 0) {
	                            structBatch = chemDel.convertChemistry(structUC, "", 
	                                                                   selectedBatch.getCompound().getNativeSketchFormat());
	                        } else {
	                            structBatch = structUC;
	                        }
	                        if (structBatch != null && structBatch.length > 0) {
	                            if ((!chemDel.areMoleculesEqual(selectedBatch.getCompound().getNativeSketch(), structBatch))) {
	                                JOptionPane.showMessageDialog(PCeNStructureVnVContainer.this,
	                                        "Batch Structure will be modified based on your Novelty Check selection.");
	                            }
	                            StructureLoadAndConversionUtil.loadSketch(structBatch, fmt, true, "MDL Molfile", selectedBatch.getCompound());
	                            structQueryCanvas.setChemistry(selectedBatch.getCompound().getNativeSketch());
	                            // TODO: 
	                            // This call may be redundant and may be causing inconsistent problems depending on the 
	                            // timing of the VnV return.  Especially since the client has access to the display again.
	                            // Is there a way to get the SIC info from the structure in UC?  We could just assign that
	                            // and be done with it if the structure is not the Drawn Structure.
	                            //performVnV(false);
	                        }
	                    } catch (ChemUtilInitException e) {
	                        CeNErrorHandler.getInstance().logExceptionMsg(PCeNStructureVnVContainer.this,
	                                "Could not load Sketch due to a failure to initialize the Chemistry Service", e);
	                    } catch (ChemUtilAccessException e) {
	                        CeNErrorHandler.getInstance().logExceptionMsg(PCeNStructureVnVContainer.this,
	                                "Could not access Chemistry Service.  Failed to load sketch changes.", e);
	                    }
	                } else {
	                	jTextFieldPFNum.setText("");
	                	JOptionPane.showMessageDialog(PCeNStructureVnVContainer.this,
	                	                              "Based on VnV results (right side), stereoisomer code '" + result.getIsomerCode()
	                	                              + "' is not valid.\nPlease re-run VnV with HSREG stereoisomer code (left side).");
	                }
                    jTextFieldPFNumActionPerformed(null);
				}
			}
		};
		worker.start();
	}

	public void updateRegInfo(BatchVnVInfoModel vnVResultVO) {
        updateBatchWithVnV(selectedBatch, vnVResultVO);
		if (vnVPassedListener != null) vnVPassedListener.vnVPassed();
	}

    public static void updateBatchWithVnV(ProductBatchModel batch, BatchVnVInfoModel vnVResultVO) {
        final ParentCompoundModel compoundModel = batch.getCompound();
        compoundModel.setMolFormula(vnVResultVO.getMolFormula());
        compoundModel.setMolWgt(vnVResultVO.getMolWeight());
        // update vnv info
        final BatchVnVInfoModel batchVnVInfo = batch.getRegInfo().getBatchVnVInfo();
        batchVnVInfo.setSuggestedSICList(vnVResultVO.getSuggestedSICList());
        batchVnVInfo.setErrorMsg(vnVResultVO.getErrorMsg());
        batchVnVInfo.setStatus(BatchVnVInfoModel.VNV_PASS);
        batchVnVInfo.setMolData(vnVResultVO.getMolData());
        batchVnVInfo.setAssignedStereoIsomerCode(vnVResultVO.getAssignedStereoIsomerCode());
    }

    public String buildVnVSDFIle() {
		if (getSelectedBatch() != null) {
            final byte[] sketch = getSelectedBatch().getCompound().getNativeSketch();
			// vNv SDFile needs limited info for a batch
			try {
                return getVnVSdFile(sketch);
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
		return null;
	}

    public static String getVnVSdFile(byte[] sketch) throws ChemUtilInitException, ChemUtilAccessException {
        ChemistryDelegate chemDel = new ChemistryDelegate();

        byte[] molFile = chemDel.convertChemistry(sketch, "", "MDL Molfile");
        SdUnit sDunit;
        if (molFile != null)
            sDunit = new SdUnit(new String(molFile), true);
        else
            sDunit = new SdUnit();
        // System.out.println("The built sd file is: " + sDunit.toString());
        return sDunit.toString();
    }

    public void updateBatchStructure() {
		try {
			ProductBatchModel ab = getSelectedBatch();
			ParentCompoundModel c = null;
			if (ab.getCompound() == null) {
				c = new ParentCompoundModel();
				ab.setCompound(c);
			} else
				c = ab.getCompound();

			c.setCreatedByNotebook(true);
			StructureLoadAndConversionUtil.loadSketch(structQueryCanvas.getChemistry(), structQueryCanvas.getEditorType(), true, "MDL Molfile",c);
			c.setCreatedByNotebook(true);
		} catch (ChemUtilInitException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this,
					"Could not load Sketch due to a failure to initialize the Chemistry Service", e);
		} catch (ChemUtilAccessException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this,
					"Could not access Chemistry Service. Failed to load sketch changes.", e);
		}
	}

	/**
	 * @return Returns the parentDialog.
	 */
	public NotebookPageGuiInterface getParentDialog() {
		return parentDialog;
	}

	/**
	 * @param parentDialog
	 *            The parentDialog to set.
	 */
	public void setParentDialog(NotebookPageGuiInterface parentDialog) {
		this.parentDialog = parentDialog;
	}

	/**
	 * @return Returns the selectedBatch.
	 */
	public ProductBatchModel getSelectedBatch() {
		return selectedBatch;
	}

	/**
	 * @param selectedBatch
	 *            The selectedBatch to set.
	 */
	public void setSelectedBatch(ProductBatchModel selectedBatch) {
        isInit = true;
		// create native sketch if it is null or empty
		byte[] nativeSketch = selectedBatch.getCompound().getNativeSketch();
		if (nativeSketch == null || nativeSketch.length == 0) {
			try{
				String molStruct = Decoder.decodeString(selectedBatch.getCompound().getStringSketchAsString());
				byte[] structUC = molStruct.getBytes();
				ChemistryDelegate chemDel = new ChemistryDelegate();
					nativeSketch = chemDel.convertChemistry(structUC, "", selectedBatch.getCompound().getNativeSketchFormat());
			} catch (ChemUtilAccessException e) {
				CeNErrorHandler.getInstance().logExceptionMsg(PCeNStructureVnVContainer.this,
						"Could not access Chemistry Service.  Failed to load sketch changes.", e);
			}
			
			selectedBatch.getCompound().setNativeSketch(nativeSketch);
		} 

        jComboBoxStereoIsoCodeResult.removeAllItems();
		this.selectedBatch = selectedBatch;

        if (selectedBatch != null) {
            BatchAttributeComponentUtility.updateStereoisomerComboBox(selectedBatch, jComboBoxSICBatch);
            final BatchVnVInfoModel vnvInfo = selectedBatch.getRegInfo().getBatchVnVInfo();
            setVnVResultVO(vnvInfo);

            final ArrayList<String> sicList = vnvInfo.getSuggestedSICList();

            for (int i = 0; i < sicList.size(); i++) {
                addStereoIsoCodeForSelection(sicList.get(i));
            }
            BatchAttributeComponentUtility.updateStereoisomerComboBox(selectedBatch, jComboBoxStereoIsoCodeResult);            
        }
        isInit = false;
	}

	/**
	 * @return Returns the vnVResultVO.
	 */
	public BatchVnVInfoModel getVnVResultVO() {
		return vnVResultVO;
	}

	/**
	 * @param vnVResultVO
	 *            The vnVResultVO to set.
	 */
	public void setVnVResultVO(BatchVnVInfoModel vnVResultVO) {
		this.vnVResultVO = vnVResultVO;
	}

	protected void jComboBoxSICBatchItemStateChanged(ItemEvent evt) {
		if (!isInit) {
			String selectedISCode = (String) jComboBoxSICBatch.getSelectedItem();
			if (selectedISCode != null && evt.getStateChange() == ItemEvent.SELECTED && selectedBatch != null
					&& selectedBatch.getCompound() != null) {
				// String descr = (String)sicMap.get(selectedISCode.substring(0,5));
				// if (descr != null && selectedBatch != null && selectedBatch.getCompound() != null) {
				// 		if (descr.startsWith("Other")) {
				// 			if (jTextArea1.getText().trim() != null && jTextArea1.getText().trim().length() > 0 ) {
				// 				selectedBatch.getCompound().setStereoisomerCode(selectedISCode.substring(0,5));
				// 			} else {
				// 				JOptionPane.showMessageDialog(this, "Compound with this Stereoisomer Code requires structure comments.");
				// 				String oldCode = selectedBatch.getCompound().getStereoisomerCode();
				// 				jComboBoxSICBatch.setSelectedItem(oldCode + " - " + sicMap.get(oldCode));
				// 			}
				// 		} else {
				//			selectedBatch.getCompound().setStereoisomerCode(selectedISCode.substring(0, 5));
				// 		}
				// }
			}
		}
	}

	/** Auto-generated event handler method */
	protected void jComboBoxStereoIsoCodeResultItemStateChanged(ItemEvent evt) {
		if (!isInit) {
			String selectedISCode = (String) jComboBoxStereoIsoCodeResult.getSelectedItem();

            if (StringUtils.isNotBlank(selectedISCode)) {
                final String code = selectedISCode.substring(0, 5);
                if (evt.getStateChange() == ItemEvent.SELECTED && !code.equals("ERROR")) {
                    setStereoIsoCode(code);
					}
				}
			}
		}

    private void setStereoIsoCode(String code) {
        selectedBatch.getCompound().setStereoisomerCode(code);
        selectedBatch.getRegInfo().getBatchVnVInfo().setAssignedStereoIsomerCode(code);
        BatchAttributeComponentUtility.updateStereoisomerComboBox(selectedBatch, jComboBoxSICBatch);
        if (callback != null)
            callback.run();
	}

	// /** Auto-generated event handler method */
	// protected void jTextPane1PropertyChange(PropertyChangeEvent evt){
	// 		if( selectedBatch != null){
	// 			selectedBatch.getCompound().setComments(jTextArea1.getText());
	//
	// 			getParentDialog().getCompReg_cont().setSelectedBatch(getSelectedBatch());
	// 			getParentDialog().getCompReg_cont().updateBatchList(getSelectedBatch());
	// 		}
	// }
	//
	// /** Auto-generated event handler method */
	// protected void jTextPane1FocusLost(FocusEvent evt){
	// 		//set structure comments to compound
	// 		selectedBatch.getCompound().setStructureComments(jTextArea1.getText().trim());
	// 		this.getParentDialog().getRegSubSum_cont().updateBatchList(selectedBatch);
	// }
	
	/** Auto-generated event handler method */
	protected void jTextFieldPFNumActionPerformed(ActionEvent evt) {
		try {
			String regNum = jTextFieldPFNum.getText().trim();
			if (regNum.length() > 0 && !"drawn structure".equalsIgnoreCase(regNum))
				regNum = UtilsDispatcher.getFormatter().formatCompoundNumber(regNum);
				//regNum = CNFHelper.formatCompoundNumber(regNum);
			selectedBatch.getCompound().setRegNumber(regNum);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		//((SingletonNotebookPageGUI) PCeNStructureVnVContainer.this.getParentDialog()).getRegSubSum_cont().updateBatchList(selectedBatch);
	}

	private boolean isValidUcIsomerCode(String isomerCode) {
		boolean result = false;
		for (int i = 0; i < jComboBoxStereoIsoCodeResult.getItemCount() && !result; i++) {
			String sicode = (String) jComboBoxStereoIsoCodeResult.getItemAt(i);
			if (sicode.substring(0, isomerCode.length()).equals(isomerCode))
				result = true;
		}
		return result;
	}

	private boolean isEditable() {
		NotebookPageModel notebookPageModel = parentDialog.getPageModel();
		return (selectedBatch != null && parentDialog != null && notebookPageModel != null && CommonUtils.getProductBatchModelEditableFlag(selectedBatch, notebookPageModel));
	}
	
	public interface VnVPassedListener {
		public void vnVPassed();
	}
}