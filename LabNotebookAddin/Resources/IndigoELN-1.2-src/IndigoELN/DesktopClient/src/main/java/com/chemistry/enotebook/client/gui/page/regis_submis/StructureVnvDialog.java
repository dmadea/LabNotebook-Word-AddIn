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

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.reagents.ProgressBarDialog;
import com.chemistry.enotebook.client.gui.page.regis_submis.uc.JDialogUniquenessCheck;
import com.chemistry.enotebook.client.gui.page.regis_submis.uc.UniquenessCheckTableModel;
import com.chemistry.enotebook.delegate.VnvDelegate;
import com.chemistry.enotebook.domain.BatchVnVInfoModel;
import com.chemistry.enotebook.domain.ParentCompoundModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.datamodel.compound.Compound;
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ChemUtilInitException;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.utils.CodeTableUtils;
import com.chemistry.enotebook.utils.Decoder;
import com.chemistry.enotebook.utils.StructureLoadAndConversionUtil;
import com.chemistry.enotebook.utils.SwingWorker;
import com.chemistry.enotebook.utils.sdf.SdUnit;
import com.chemistry.enotebook.vnv.classes.UcCompoundInfo;
import com.chemistry.viewer.ChemistryViewer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;

public class StructureVnvDialog extends JDialog {

	private static final long serialVersionUID = -3805824939091663588L;

	private static String SELECT_SALT_CODE_ALERT = "Batch structure, MF, MW different from parent structure, MW, MF.";
	
	private ProductBatchModel productBatchModel;
	private Frame owner;
	private ProgressBarDialog progressBarDialog;
	private BatchVnVInfoModel vnvInfo;
	private UcCompoundInfo ucInfo;
	
	private JLabel batchTitleLabel;
	private JLabel compoundRegistrationTitleLabel;
	private JLabel batchSicLabel;
	private JLabel compoundRegistrationSicLabel;
	private JLabel batchMwMfLabel;
	private JLabel compoundRegistrationMwMfLabel;
	private JLabel vnvResultsLabel;
	private JLabel structCommentsLabel;
	private JLabel compoundNoLabel;
	
	private ChemistryViewer batchStructureViewer;
	private ChemistryViewer compoundRegistrationStructureViewer;
	
	private JPanel batchStructureViewerPanel;
	private JPanel compoundRegistrationStructureViewerPanel;
	
	private CeNComboBox batchSicComboBox;
	private CeNComboBox compoundRegistrationSicComboBox;
	
	private JTextField batchMwMfTextField;
	private JTextField compoundRegistrationMwMfTextField;
	private JTextField compoundNoTextField;
	
	private JTextArea vnvResultsTextArea;
	private JTextArea structCommentsTextArea;
	
	private JScrollPane vnvResultsScrollPane;
	private JScrollPane structCommentsScrollPane;
	
	private JButton vnvButton;
	private JButton okButton;
	private JButton cancelButton;
	
	public StructureVnvDialog(Frame owner, ProductBatchModel productBatchModel) {
		super(owner);
		this.owner = owner;
		this.productBatchModel = productBatchModel;
		this.init();
	}

	private void init() {
		createComponents();
		locateComponents();
		fillComponentsFromBatch();
				
		setTitle("Structure Validation & Verification");
		setDefaultCloseOperation(StructureVnvDialog.DISPOSE_ON_CLOSE);
		setSize(800, 530);
		setResizable(false);
		setModal(true);
		setLocationRelativeTo(owner);
	}

	private void createComponents() {
		batchTitleLabel = new JLabel("Validate & Verify Your Product Batch Structure:");
		compoundRegistrationTitleLabel = new JLabel("CompoundRegistration-Validated Parent Structure:");
		batchSicLabel = new JLabel("Stereoisomer Code:");
		compoundRegistrationSicLabel = new JLabel("Stereoisomer Code:");
		batchMwMfLabel = new JLabel("Batch MW, MF:");
		compoundRegistrationMwMfLabel = new JLabel("Parent MW, MF:");
		vnvResultsLabel = new JLabel("VnV Results / Error Messages:");
		structCommentsLabel = new JLabel("Structure Comments:");
		compoundNoLabel = new JLabel("Compound #");
		
		batchStructureViewer = new ChemistryViewer(owner.getTitle(), "Batch Structure");
		compoundRegistrationStructureViewer = new ChemistryViewer(owner.getTitle(), "VnV Structure");
		
		batchStructureViewerPanel = new JPanel(new BorderLayout());
		compoundRegistrationStructureViewerPanel = new JPanel(new BorderLayout());
		
		batchSicComboBox = new CeNComboBox();
		compoundRegistrationSicComboBox = new CeNComboBox();
		
		batchMwMfTextField = new JTextField();
		compoundRegistrationMwMfTextField = new JTextField();
		compoundNoTextField = new JTextField();
		
		vnvResultsTextArea = new JTextArea();
		structCommentsTextArea = new JTextArea();
		
		vnvResultsScrollPane = new JScrollPane(vnvResultsTextArea);
		structCommentsScrollPane = new JScrollPane(structCommentsTextArea);
		
		vnvButton = new JButton("<html><center>VnV&Novelty<br>Check</center></html>");
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		
		CeNGUIUtils.styleComponentText(Font.BOLD, batchSicLabel);
		CeNGUIUtils.styleComponentText(Font.BOLD, compoundRegistrationSicLabel);
		CeNGUIUtils.styleComponentText(Font.BOLD, batchMwMfLabel);
		CeNGUIUtils.styleComponentText(Font.BOLD, compoundRegistrationMwMfLabel);
		CeNGUIUtils.styleComponentText(Font.BOLD, vnvResultsLabel);
		CeNGUIUtils.styleComponentText(Font.BOLD, structCommentsLabel);
		CeNGUIUtils.styleComponentText(Font.BOLD, compoundNoLabel);
		CeNGUIUtils.styleComponentText(Font.BOLD, vnvButton);
		CeNGUIUtils.styleComponentText(Font.BOLD, okButton);
		CeNGUIUtils.styleComponentText(Font.BOLD, cancelButton);
				
		batchMwMfTextField.setEditable(false);
		compoundRegistrationMwMfTextField.setEditable(false);
		compoundNoTextField.setEditable(false);
		
		batchStructureViewer.setReadOnly(true);
		compoundRegistrationStructureViewer.setReadOnly(true);
		
		batchStructureViewerPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		compoundRegistrationStructureViewerPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
				
		vnvResultsTextArea.setLineWrap(true);
		structCommentsTextArea.setLineWrap(true);
		
		vnvResultsTextArea.setEnabled(false);
		structCommentsTextArea.setEnabled(false);
		
		Dimension baseDimension = new Dimension(130, 27);
        Dimension extendedDimension = new Dimension(170, 27);
        Dimension structureDimension = new Dimension(300, 270);
        
        setSize(batchSicLabel, baseDimension);
        setSize(compoundRegistrationSicLabel, baseDimension);
        setSize(batchMwMfLabel, baseDimension);
        setSize(compoundRegistrationMwMfLabel, baseDimension);
        setSize(vnvResultsLabel, extendedDimension);
        setSize(structCommentsLabel, baseDimension);
        setSize(compoundNoLabel, baseDimension);
        
        setSize(batchMwMfTextField, extendedDimension);
        setSize(compoundRegistrationMwMfTextField, extendedDimension);
        setSize(compoundNoTextField, extendedDimension);
        setSize(batchSicComboBox, extendedDimension);
        setSize(compoundRegistrationSicComboBox, extendedDimension);
				
        setSize(batchStructureViewerPanel, structureDimension);
        setSize(compoundRegistrationStructureViewerPanel, structureDimension);
        
        setSize(structCommentsScrollPane, new Dimension(166, 50));
        setSize(vnvResultsScrollPane, new Dimension(300, 60));
        
		vnvButton.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				vnvButtonActionPerformed();
			}
		});
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButtonActionPerformed();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				cancelButtonActionPerformed();
			}
		});
	}
	
	private void setSize(JComponent component, Dimension size) {
        component.setPreferredSize(size);
        component.setSize(size);
    }
	
	private void locateComponents() {
		JPanel contentPane = (JPanel) getContentPane();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		
		contentPane.setLayout(layout);
		
		batchStructureViewerPanel.add(batchStructureViewer, BorderLayout.CENTER);
		compoundRegistrationStructureViewerPanel.add(compoundRegistrationStructureViewer, BorderLayout.CENTER);
		
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.gridy = 0;
		contentPane.add(batchTitleLabel, gbc);

		gbc.gridx += 3;
		contentPane.add(compoundRegistrationTitleLabel, gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		contentPane.add(batchStructureViewerPanel, gbc);

		gbc.gridx = 2;
		gbc.gridwidth = 1;
		contentPane.add(vnvButton, gbc);

		gbc.gridwidth = 2;
		gbc.gridx = 3;
		contentPane.add(compoundRegistrationStructureViewerPanel, gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		contentPane.add(batchSicLabel, gbc);

		gbc.gridx++;
		contentPane.add(batchSicComboBox, gbc);

		gbc.gridx += 2;
		contentPane.add(compoundRegistrationSicLabel, gbc);

		gbc.gridx++;
		contentPane.add(compoundRegistrationSicComboBox, gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		contentPane.add(batchMwMfLabel, gbc);

		gbc.gridx++;
		contentPane.add(batchMwMfTextField, gbc);

		gbc.gridx += 2;
		contentPane.add(compoundRegistrationMwMfLabel, gbc);

		gbc.gridx++;
		contentPane.add(compoundRegistrationMwMfTextField, gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		contentPane.add(vnvResultsLabel, gbc);

		gbc.gridx = 3;
		gbc.gridwidth = 1;
		contentPane.add(structCommentsLabel, gbc);

		gbc.gridx++;
		gbc.gridheight = 2;
		contentPane.add(structCommentsScrollPane, gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 3;
		contentPane.add(vnvResultsScrollPane, gbc);

		gbc.gridx = 3;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.gridy++;
		contentPane.add(compoundNoLabel, gbc);

		gbc.gridx++;
		contentPane.add(compoundNoTextField, gbc);

		gbc.gridy += 2;
		gbc.gridx = 2;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.SOUTH;
		
		JPanel buttonsPanel = new JPanel(new BorderLayout(6, 6));
		
		buttonsPanel.add(okButton, BorderLayout.WEST);
		buttonsPanel.add(cancelButton, BorderLayout.CENTER);
		
		contentPane.add(buttonsPanel, gbc);
	}
	
	private void fillComponentsFromBatch() {
		CodeTableUtils.fillComboBoxWithIsomers(batchSicComboBox);
		BatchAttributeComponentUtility.updateStereoisomerComboBox(productBatchModel, batchSicComboBox);
		
		if (ArrayUtils.isEmpty(productBatchModel.getCompound().getNativeSketch())) {
			String str = Decoder.decodeString(new String(productBatchModel.getCompound().getStringSketch()));
			batchStructureViewer.setChemistry(str.getBytes());
		} else {
			batchStructureViewer.setChemistry(productBatchModel.getCompound().getNativeSketch());
		}
		
		if (productBatchModel.getMolWgt() == 0 && StringUtils.isBlank(productBatchModel.getMolecularFormula())) {
			batchMwMfTextField.setText("");
		} else {
			batchMwMfTextField.setText(formatWeight(productBatchModel.getMolWgt()) + ", " + productBatchModel.getMolecularFormula());
		}
		
		BatchVnVInfoModel batchVnvInfo = productBatchModel.getRegInfo().getBatchVnVInfo();
		
		if (batchVnvInfo != null && StringUtils.equalsIgnoreCase(batchVnvInfo.getStatus(), BatchVnVInfoModel.VNV_PASS)) {
			fillFromVnv(batchVnvInfo, new UcCompoundInfo());
			
			structCommentsTextArea.setText(productBatchModel.getCompound().getStructureComments());
			structCommentsTextArea.setCaretPosition(0);
			
			if (productBatchModel.getCompound().getMolWgt() == 0 && StringUtils.isBlank(productBatchModel.getCompound().getMolFormula())) {
				compoundRegistrationMwMfTextField.setText("");
			} else {
				compoundRegistrationMwMfTextField.setText(formatWeight(productBatchModel.getCompound().getMolWgt()) + ", " + productBatchModel.getCompound().getMolFormula());
			}
			
			compoundNoTextField.setText(productBatchModel.getCompound().getRegNumber());
			
			if (ArrayUtils.isEmpty(productBatchModel.getCompound().getNativeSketch())) {
				String str = Decoder.decodeString(new String(productBatchModel.getCompound().getStringSketch()));
				compoundRegistrationStructureViewer.setChemistry(str.getBytes());
			} else {
				compoundRegistrationStructureViewer.setChemistry(productBatchModel.getCompound().getNativeSketch());
			}

			boolean foundSelected = false;
			
			for (int i = 0; i < compoundRegistrationSicComboBox.getItemCount(); ++i) {
				String item = (String) compoundRegistrationSicComboBox.getItemAt(i);
				if (StringUtils.contains(item, productBatchModel.getCompound().getStereoisomerCode())) {
					compoundRegistrationSicComboBox.setSelectedIndex(i);
					foundSelected = true;
					break;
				}
			}
			
			if (!foundSelected) {
				String item = productBatchModel.getCompound().getStereoisomerCode() + " - " + RegCodeMaps.getInstance().getStereoisomerCodeMap().get(productBatchModel.getCompound().getStereoisomerCode());
				compoundRegistrationSicComboBox.addItem(item);
				compoundRegistrationSicComboBox.setSelectedItem(item);
			}
		}
	}

	private void fillComponentsAfterVnv() {
		if (vnvInfo != null) {
			fillFromVnv(vnvInfo, ucInfo);
			fillFromUc(ucInfo);
		}
	}
	
	private void fillFromUc(UcCompoundInfo ucInfo) {
		if (ucInfo != null) {
			structCommentsTextArea.setText(ucInfo.getComments());
			structCommentsTextArea.setCaretPosition(0);
			
			compoundNoTextField.setText(ucInfo.getRegNumber());
			
			compoundRegistrationStructureViewer.setChemistry(ucInfo.getMolStruct().getBytes());
			
			if (Double.parseDouble(ucInfo.getMolWgt()) == 0 && StringUtils.isBlank(ucInfo.getMolFormula())) {
				compoundRegistrationMwMfTextField.setText("");
			} else {
				compoundRegistrationMwMfTextField.setText(formatWeight(Double.parseDouble(ucInfo.getMolWgt())) + ", " + ucInfo.getMolFormula());
			}
			
			boolean foundSelected = false;
			
			for (int i = 0; i < compoundRegistrationSicComboBox.getItemCount(); ++i) {
				String item = (String) compoundRegistrationSicComboBox.getItemAt(i);
				if (StringUtils.contains(item, ucInfo.getIsomerCode())) {
					compoundRegistrationSicComboBox.setSelectedIndex(i);
					foundSelected = true;
					break;
				}
			}
			
			if (!foundSelected) {
				String item = ucInfo.getIsomerCode() + " - " + RegCodeMaps.getInstance().getStereoisomerCodeMap().get(ucInfo.getIsomerCode());
				compoundRegistrationSicComboBox.addItem(item);
				compoundRegistrationSicComboBox.setSelectedItem(item);
			}
			
			foundSelected = false;
			
			for (int i = 0; i < batchSicComboBox.getItemCount(); ++i) {
				String item = (String) batchSicComboBox.getItemAt(i);
				if (StringUtils.contains(item, ucInfo.getIsomerCode())) {
					batchSicComboBox.setSelectedIndex(i);
					foundSelected = true;
					break;
				}
			}
			
			if (!foundSelected) {
				String item = ucInfo.getIsomerCode() + " - " + RegCodeMaps.getInstance().getStereoisomerCodeMap().get(ucInfo.getIsomerCode());
				batchSicComboBox.addItem(item);
				batchSicComboBox.setSelectedItem(item);
			}
		}
	}
	
	private void fillFromVnv(BatchVnVInfoModel vnvInfo, UcCompoundInfo ucInfo) {
		if (vnvInfo != null) {
			compoundRegistrationSicComboBox.removeAllItems();

			vnvResultsTextArea.setText(vnvInfo.getErrorMsg());
			vnvResultsTextArea.setCaretPosition(0);
			
			if (StringUtils.equalsIgnoreCase(vnvInfo.getStatus(), "Fail")) {
				compoundRegistrationMwMfTextField.setText("");
				compoundRegistrationStructureViewer.setChemistry(new byte[0]);
				compoundNoTextField.setText("");
				structCommentsTextArea.setText("");
				ucInfo = null;
				return;
			}
			
			if (ucInfo != null) {
				if (vnvInfo.getMolWeight() == 0 && StringUtils.isBlank(vnvInfo.getMolFormula())) {
					compoundRegistrationMwMfTextField.setText("");
				} else {
					compoundRegistrationMwMfTextField.setText(formatWeight(vnvInfo.getMolWeight()) + ", " + vnvInfo.getMolFormula());
				}
						
				List<String> sicList = vnvInfo.getSuggestedSICList();
				
				if (sicList.size() > 0) {
					int selectedSIC = -1;
					int defaultSIC = -1;
					String sicode = "";
					
					for (int i = 0; i < sicList.size(); i++) {
						sicode = sicList.get(i);
						
						if (!getSelectedBatchSic().equals("HSREG")) {
							if (sicode.endsWith(getSelectedBatchSic())) {
								selectedSIC = i;
							}
							
							if (sicode.endsWith(vnvInfo.getAssignedStereoIsomerCode())) {
								defaultSIC = i;
							}
						} else if (!vnvInfo.getAssignedStereoIsomerCode().startsWith("ERROR")) {
							if (sicode.endsWith(vnvInfo.getAssignedStereoIsomerCode())) {
								selectedSIC = i;
							}
						}
						
						compoundRegistrationSicComboBox.addItem(sicode + " - " + RegCodeMaps.getInstance().getStereoisomerCodeMap().get(sicode));
		            }
					
					if (selectedSIC == -1 && defaultSIC > 0) {
						compoundRegistrationSicComboBox.setSelectedIndex(defaultSIC);
					} else {
						compoundRegistrationSicComboBox.setSelectedIndex(selectedSIC);
					}
				} else {
					String code = vnvInfo.getAssignedStereoIsomerCode();
		            
		            if (StringUtils.isBlank(code)) {
		                code = getSelectedBatchSic();
		            }
		            
		            compoundRegistrationSicComboBox.addItem(code + " - " + RegCodeMaps.getInstance().getStereoisomerCodeMap().get(code));
		            BatchAttributeComponentUtility.updateStereoisomerComboBox(compoundRegistrationSicComboBox, vnvInfo.getAssignedStereoIsomerCode());
				}
			}
		}
	}
	
	private void vnvButtonActionPerformed() {
		if (!productBatchModel.isEditable()) {
			JOptionPane.showMessageDialog(owner, "This batch is currently not editable.");
			return;
		}		
		
//		if (batchStructureViewer.isChemistryEmpty()) {
//			JOptionPane.showMessageDialog(owner, "A structure is required to perform VnV.");
//			return;
//		}
		
		progressBarDialog = new ProgressBarDialog(owner);
		progressBarDialog.setTitle("Performing VnV, please wait ...");
		doVnv(true);
		progressBarDialog.setVisible(true);
	}

	private void okButtonActionPerformed() {
		fillBatchFromVnvUc();
		dispose();
	}
	
	private void cancelButtonActionPerformed() {
		dispose();
	}
	
	private void doVnv(final boolean doUc) {
		vnvInfo = null;
		ucInfo = null;
		
		SwingWorker vnvWorker = new SwingWorker() {
			public Object construct() {
				try {
					String vnvSdFile = null;
					
					if (ArrayUtils.isEmpty(productBatchModel.getCompound().getNativeSketch())) {
						String str = Decoder.decodeString(new String(productBatchModel.getCompound().getStringSketch()));
						vnvSdFile = getVnVSdFile(str.getBytes());
					} else {
						vnvSdFile = getVnVSdFile(productBatchModel.getCompound().getNativeSketch());
					}
					
					if (StringUtils.isNotBlank(vnvSdFile)) {
						VnvDelegate vnvDelegate = new VnvDelegate();
						UcCompoundInfo ucInfo = null;
						
						BatchVnVInfoModel vnvInfo = vnvDelegate.performVnV(vnvSdFile, getSelectedBatchSic());
						
						if (vnvInfo != null) {
							if (vnvInfo.isPassed()) {
								if (compareBatchVnV(vnvInfo)) {
									vnvInfo.setErrorMsg(vnvInfo.getErrorMsg() + "\n" + SELECT_SALT_CODE_ALERT);
								}

								if (!vnvInfo.getAssignedStereoIsomerCode().startsWith("ERROR") && doUc) {
									ucInfo = doUc(vnvInfo);
								} else {
									hideProgressBarDialog();
									
									if (vnvInfo.getAssignedStereoIsomerCode().startsWith("ERROR")) {
										JOptionPane.showMessageDialog(StructureVnvDialog.this,
												"VnV result: Could not determine a default Stereoisomer Code.\n"
												+ "                   See VnV Results / Error Messages for details.\n"
												+ "                   You may need to specify an actual SIC instead of HSREG.");
									}
								}
							}

							StructureVnvDialog.this.vnvInfo = vnvInfo;
							StructureVnvDialog.this.ucInfo = ucInfo;
						}
					}
				} catch (Exception e) {
					hideProgressBarDialog();
					CeNErrorHandler.getInstance().logErrorMsg(StructureVnvDialog.this, e.toString(), "External service is down.", JOptionPane.INFORMATION_MESSAGE);
				}

				fillComponentsAfterVnv();
				
				hideProgressBarDialog();
				
				return null;
			}
		};
		
		vnvWorker.start();
	}

	private UcCompoundInfo doUc(BatchVnVInfoModel vnvInfo) {
		progressBarDialog.setTitle("Performing Novelty Check, Please Wait ...");
		
		UniquenessCheckTableModel model = new UniquenessCheckTableModel(vnvInfo.getMolData().getBytes(), vnvInfo.getAssignedStereoIsomerCode(), vnvInfo.getMolWeight(), vnvInfo.getMolFormula(), productBatchModel.getCompound().getStructureComments(), true);

		hideProgressBarDialog();
		
		UcCompoundInfo result = null;
		
		if (model != null && model.getUcResults() != null) {
			JDialogUniquenessCheck dlg = new JDialogUniquenessCheck(MasterController.getGUIComponent());
			result = dlg.displayDialog(model);
			if (dlg != null) {
				dlg.dispose();
			}
		}
		
		return result;
	}
	
	private void hideProgressBarDialog() {
		if (progressBarDialog != null) {
			progressBarDialog.setVisible(false);
			progressBarDialog.dispose();
		}
	}
		
	private String formatWeight(double weight) {
		NumberFormat formatter = NumberFormat.getInstance();
		formatter.setMaximumFractionDigits(3);
		return formatter.format(weight);
	}
	
	private boolean compareBatchVnV(BatchVnVInfoModel vnvInfo) {
		ParentCompoundModel c = productBatchModel.getCompound();
		return (Math.round(vnvInfo.getMolWeight() * 100) != Math.round(c.getMolWgt() * 100) || !StringUtils.equals(removeWhites(vnvInfo.getMolFormula()), removeWhites(c.getMolFormula())));
	}

	private String removeWhites(String s) {
		String result = "";
		char ac[] = s.toCharArray();
		for (char c : ac) {
			if (c > ' ') {
				result += c;
			}
		}
		return result;
	}
	
	private String getSelectedBatchSic() {
		return getSelectedSic(batchSicComboBox);
	}
	
	private String getSelectedCompoundRegistrationSic() {
		return getSelectedSic(compoundRegistrationSicComboBox);
	}
	
	private String getSelectedSic(JComboBox combo) {
		String selectedItem = (String) combo.getSelectedItem();
		return (selectedItem == null) ? "" : selectedItem.substring(0, 5);
	}
		
	private void fillBatchFromVnvUc() {		
		if (vnvInfo != null && !StringUtils.equalsIgnoreCase(vnvInfo.getStatus(), "Fail") && ucInfo != null) {
			updateBatchWithVnV(productBatchModel, vnvInfo);
		
			if (ucInfo != null) {
				productBatchModel.getCompound().setStereoisomerCode(getSelectedCompoundRegistrationSic());
				productBatchModel.getCompound().setMolFormula(ucInfo.getMolFormula());
				productBatchModel.getCompound().setMolWgt(Double.parseDouble(ucInfo.getMolWgt()));
				productBatchModel.getCompound().setRegNumber(ucInfo.getRegNumber());
				productBatchModel.getCompound().setStructureComments(ucInfo.getComments());
				try {
					int fmt = productBatchModel.getCompound().getFormat();
					byte[] structUC = ucInfo.getMolStruct().getBytes();
					byte[] structBatch = null;
					ChemistryDelegate chemDel = new ChemistryDelegate();
					if ((fmt == Compound.ISISDRAW || fmt == Compound.CHEMDRAW) && structUC != null && structUC.length > 0) {
						structBatch = chemDel.convertChemistry(structUC, "", productBatchModel.getCompound().getNativeSketchFormat());
					} else {
						structBatch = structUC;
					}
					if (structBatch != null && structBatch.length > 0) {
						if ((!chemDel.areMoleculesEqual(productBatchModel.getCompound().getNativeSketch(), structBatch))) {
							JOptionPane.showMessageDialog(this, "Batch Structure will be modified based on your Novelty Check selection.");
						}
						StructureLoadAndConversionUtil.loadSketch(structBatch, fmt, true, "MDL Molfile", productBatchModel.getCompound());
					}
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logErrorMsg(this, e.toString(), "Error converting structure.", JOptionPane.INFORMATION_MESSAGE);
				}
				productBatchModel.getRegInfo().getBatchVnVInfo().setModelChanged(true);
				productBatchModel.getRegInfo().setModelChanged(true);
				productBatchModel.getCompound().setModelChanged(true);
				productBatchModel.setModelChanged(true);
				MasterController.getGUIComponent().enableSaveButtons();
			}
		}
	}
			
	public static String getVnVSdFile(byte[] sketch) throws ChemUtilInitException, ChemUtilAccessException {
        ChemistryDelegate chemDel = new ChemistryDelegate();

        byte[] molFile = chemDel.convertChemistry(sketch, "", "MDL Molfile");
        SdUnit sDunit;
        if (molFile != null) {
            sDunit = new SdUnit(new String(molFile), true);
        } else {
            sDunit = new SdUnit();
        }

        return sDunit.toString();
    }
	
	public static void updateBatchWithVnV(ProductBatchModel batch, BatchVnVInfoModel vnVResultVO) {
        ParentCompoundModel compoundModel = batch.getCompound();
        compoundModel.setMolFormula(vnVResultVO.getMolFormula());
        compoundModel.setMolWgt(vnVResultVO.getMolWeight());
        compoundModel.setStereoisomerCode(vnVResultVO.getAssignedStereoIsomerCode());

        BatchVnVInfoModel batchVnVInfo = batch.getRegInfo().getBatchVnVInfo();
        batchVnVInfo.setSuggestedSICList(vnVResultVO.getSuggestedSICList());
        batchVnVInfo.setErrorMsg(vnVResultVO.getErrorMsg());
        batchVnVInfo.setStatus(BatchVnVInfoModel.VNV_PASS);
        batchVnVInfo.setMolData(vnVResultVO.getMolData());
        batchVnVInfo.setAssignedStereoIsomerCode(vnVResultVO.getAssignedStereoIsomerCode());
        batchVnVInfo.setMolFormula(vnVResultVO.getMolFormula());
        batchVnVInfo.setMolWeight(vnVResultVO.getMolWeight());
    }
}