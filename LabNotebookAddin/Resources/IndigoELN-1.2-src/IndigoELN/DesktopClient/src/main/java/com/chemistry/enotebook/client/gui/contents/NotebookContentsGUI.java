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
package com.chemistry.enotebook.client.gui.contents;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.CeNInternalFrame;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.gui.query_search.ImageIconRenderer;
import com.chemistry.enotebook.client.gui.query_search.TextAreaRenderer;
import com.chemistry.enotebook.client.gui.query_search.TextPanelRenderer;
import com.chemistry.enotebook.client.print.gui.PrintRequest;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.storage.ReactionPageInfo;
import com.chemistry.enotebook.utils.SimpleJTable;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class NotebookContentsGUI extends CeNInternalFrame {

	private static final long serialVersionUID = 6586110166133160649L;
	
	private SimpleJTable jTableContents;
	private JScrollPane jScrollPaneMain;
	private JPopupMenu jPopupMenuExperiment;
	private JMenuItem jMenuItemLoadExperiment;
	private JMenuItem jMenuItemCloneExperiment;
	private JMenuItem jMenuItemPrintExperiment;

	public NotebookContentsGUI() {
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			setFrameIcon(CenIconFactory.getImageIcon(CenIconFactory.General.APPLICATION_ICON));
			jPopupMenuExperiment = new JPopupMenu();
			jMenuItemLoadExperiment = new JMenuItem("Open this Experiment");
			jMenuItemLoadExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_OPEN));
			jMenuItemLoadExperiment.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					OpenExperimentActionPerformed();
				}
			});
			jPopupMenuExperiment.add(jMenuItemLoadExperiment);
			jMenuItemCloneExperiment = new JMenuItem("Repeat this Experiment");
			jMenuItemCloneExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.COPY));
			jMenuItemCloneExperiment.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					RepeatExperimentActionPerformed();
				}
			});
			jPopupMenuExperiment.add(jMenuItemCloneExperiment);
			jMenuItemPrintExperiment = new JMenuItem("Print this Experiment");
			jMenuItemPrintExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.MenuBar.PRINT));
			jMenuItemPrintExperiment.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					PrintExperimentActionPerformed();
				}
			});
			jPopupMenuExperiment.add(jMenuItemPrintExperiment);
			jPopupMenuExperiment.addPopupMenuListener(new PopupMenuListener() {			
				public void popupMenuCanceled(PopupMenuEvent e) {}
				public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
				public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
					boolean enabled = !CeNConstants.PAGE_TYPE_PARALLEL.equals(getReactionPageInfo().getPageType()) || MasterController.isParallelViewEnabled();
					for(Component component : ((JPopupMenu)e.getSource()).getComponents()) {
						component.setEnabled(enabled);
					}
				}
			});
			jScrollPaneMain = new JScrollPane();
			jTableContents = new SimpleJTable(jPopupMenuExperiment);
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setTitle("GUI not connected to model");
			this.setResizable(true);
			this.setClosable(true);
			this.setIconifiable(true);
			this.setMaximizable(true);
			this.setVisible(true);
			this.setPreferredSize(new java.awt.Dimension(577, 330));
			this.setBounds(new java.awt.Rectangle(0, 0, 577, 330));
			this.getContentPane().add(jScrollPaneMain, BorderLayout.CENTER);
			jScrollPaneMain.add(jTableContents);
			jScrollPaneMain.setViewportView(jTableContents);
			{
				Object[][] data = new String[][] { { "0", "1" }, { "2", "3" } };
				Object[] columns = new String[] { "One", "Two" };
				javax.swing.table.TableModel dataModel = new javax.swing.table.DefaultTableModel(data, columns);
				jTableContents.setModel(dataModel);
			}
			postInitGUI();
			
			this.addInternalFrameListener(new InternalFrameAdapter() {
				public void internalFrameClosing(InternalFrameEvent e) {
					NotebookContentsGUI.this.internalFrameClosing();
				}

				public void internalFrameClosed(InternalFrameEvent e) {
					MasterController.getGUIComponent().refreshIcons();
				}
			});

		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		super.postInitGUI();
	}

	/** Auto-generated main method */
	public static void main(String[] args) {
		showGUI();
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
			NotebookContentsGUI inst = new NotebookContentsGUI();
			javax.swing.JDesktopPane jdp = new javax.swing.JDesktopPane();
			jdp.add(inst);
			jdp.setPreferredSize(inst.getPreferredSize());
			frame.setContentPane(jdp);
			frame.getContentPane().setSize(inst.getSize());
			frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void internalFrameActivated() {
		// background navigate sb to this experiment
		// if (pageModel != null) {
		// MasterController.getGuiController().speedBarNavigateTo(
		// pageModel.getSiteCode(), pageModel.getUserNTID(),
		// pageModel.getNotebookRef());
		// }
	}

	public void internalFrameClosing() {
		MasterController.getGuiController().removeNotebookContentsFromGUI(this);
	}

	public NotebookContentsTableModel getModel() {
		return (NotebookContentsTableModel) jTableContents.getModel();
	}

	public void setModel(NotebookContentsTableModel dataModel) {
		if (dataModel != null) {
			dataModel.setTable(jTableContents);
			
			jTableContents.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			jTableContents.sizeColumnsToFit(JTable.AUTO_RESIZE_OFF);
			jTableContents.setRowSelectionAllowed(true);
			jTableContents.setColumnSelectionAllowed(false);
			jTableContents.setModel(dataModel);
			JTableHeader th = jTableContents.getTableHeader();
			th.setFont(new Font(th.getFont().getFontName(), Font.BOLD, th.getFont().getSize() + 2));
			TextAreaRenderer hRenderer = new TextAreaRenderer();
			hRenderer.setBackground(Color.LIGHT_GRAY);
			th.setDefaultRenderer(hRenderer);
			th.setPreferredSize(new java.awt.Dimension(35, 35));
			th.setAutoscrolls(false);
			TableColumnModel vColumModel = jTableContents.getColumnModel();
			TableColumn column = vColumModel.getColumn(0);
			TextPanelRenderer renderer = new TextPanelRenderer();
			column.setCellRenderer(renderer);
			column.setPreferredWidth(100);
			column = vColumModel.getColumn(1);
			column.setPreferredWidth(360);
			column.setCellRenderer(new TextAreaRenderer());
			column = vColumModel.getColumn(2);
			column.setPreferredWidth(500);
			column.setCellRenderer(new ImageIconRenderer());
			jTableContents.setRowHeight(160);
			jTableContents.setSelectionBackground(Color.white);
			jTableContents.setSelectionForeground(Color.black);
			jTableContents.addMouseListener(new MouseInputAdapter() {
				public void mouseReleased(MouseEvent e) {
					if (e.getClickCount() == 2 && e.getButton() == 1) {
						OpenExperimentActionPerformed();
					}
				}
			});
		}
	}

	private void OpenExperimentActionPerformed() {
		try {
			ReactionPageInfo row = getReactionPageInfo();
			if(CeNConstants.PAGE_TYPE_PARALLEL.equals(row.getPageType()) && !MasterController.isParallelViewEnabled()) {
				return;
			}
			NotebookRef nbRefObj = new NotebookRef(row.getNoteBookExperiment());
			MasterController.getGuiController().openPCeNExperiment(getModel().getSiteCode(), nbRefObj.getNbNumber(), nbRefObj.getNbPage(), new Integer(row.getVersion()).intValue(), false, true);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	private void RepeatExperimentActionPerformed() {
		try {
			ReactionPageInfo row = getReactionPageInfo();
			NotebookRef nbRefObj = new NotebookRef(row.getNoteBookExperiment());
			MasterController.getGuiController().repeatExperiment(getModel().getSiteCode(), getModel().getUser(), nbRefObj.getNbNumber(), nbRefObj.getNbPage(), new Integer(row.getVersion()).intValue());
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	private void PrintExperimentActionPerformed() {
		try {
			ReactionPageInfo pi = getReactionPageInfo();
			NotebookRef nbRefObj = new NotebookRef(pi.getNoteBookExperiment());
			ArrayList<PrintRequest> list = new ArrayList<PrintRequest>();
			String pageType = "";
			
     if (pi.getPageInfo() != null) {
        if (pi.getPageInfo().indexOf(CeNConstants.PAGE_TYPE_CONCEPTION) != -1) {
          pageType = CeNConstants.PAGE_TYPE_CONCEPTION;
        }
        else if (pi.getPageInfo().indexOf(CeNConstants.PAGE_TYPE_MED_CHEM) != -1) {
          pageType = CeNConstants.PAGE_TYPE_MED_CHEM;
        }
        else if (pi.getPageInfo().indexOf(CeNConstants.PAGE_TYPE_PARALLEL) != -1) {
          pageType = CeNConstants.PAGE_TYPE_PARALLEL;
        }
      }
	     
			list.add(new PrintRequest(getModel().getSiteCode(), nbRefObj, new Integer(pi.getVersion()), pageType));
			MasterController.getGuiController().printExperiments(list);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}
	
	private ReactionPageInfo getReactionPageInfo() {
		int nRow = jTableContents.getSelectedRow();
		NotebookContentsTableModel model = (NotebookContentsTableModel) jTableContents.getModel();
		ReactionPageInfo row = (ReactionPageInfo) model.getPagesList().get(nRow);
		return row;
	}
}
