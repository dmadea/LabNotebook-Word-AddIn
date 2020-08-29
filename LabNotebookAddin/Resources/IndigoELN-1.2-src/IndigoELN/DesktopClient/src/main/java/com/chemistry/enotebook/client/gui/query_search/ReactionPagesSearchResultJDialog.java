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
package com.chemistry.enotebook.client.gui.query_search;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.print.gui.PrintRequest;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ReactionStepModel;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.storage.ReactionPageInfo;
import com.chemistry.enotebook.utils.SimpleJTable;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class ReactionPagesSearchResultJDialog extends JFrame {
	private SimpleJTable jTableReactionPages;
	// private JLabel jLabelReactionScheme;
	// private JPanel jPanelpageInfo;
	// private JLabel jLabelPageInfoTitle;
	private JScrollPane jScrollPaneResults;
	// private JPanel jPanelTitle;
	private JPanel jPanelMain;
	private JPanel navigationPane;
	private ReactionPagesTableModel reactionPagesTableModel;
	private JPopupMenu jPopupMenuExperiment;
	private JMenuItem jMenuItemLoadExperiment;
	private JMenuItem jMenuItemCloneExperiment;
	private JMenuItem jMenuItemPrintExperiment;
	private int initialCellHeight = 300;
	private int initialStructureCellWidth = 640;
	private static final long serialVersionUID = 1980635907122563550L;
	private String site = null;
	private String user = null;
	 private static final Log log = LogFactory.getLog(ReactionPagesSearchResultJDialog.class);
	public ReactionPagesSearchResultJDialog(Frame owner) {
		initGUI();
	}

	public ReactionPagesSearchResultJDialog() {
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jPanelMain = new JPanel();
			// jPanelTitle = new JPanel();
			jScrollPaneResults = new JScrollPane();
			jTableReactionPages = new SimpleJTable(jPopupMenuExperiment);
			navigationPane = new JPanel();
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setTitle("Search Results");
			this.setSize(new java.awt.Dimension(657, 330));
			BorderLayout jPanelMainLayout = new BorderLayout();
			jPanelMain.setLayout(jPanelMainLayout);
			jPanelMainLayout.setHgap(0);
			jPanelMainLayout.setVgap(0);
			jPanelMain.setPreferredSize(new java.awt.Dimension(338, 235));
			this.getContentPane().add(jPanelMain, BorderLayout.CENTER);
			// BorderLayout jPanelTitleLayout = new BorderLayout();
			// jPanelTitle.setLayout(jPanelTitleLayout);
			// jPanelTitleLayout.setHgap(0);
			// jPanelTitleLayout.setVgap(0);
			// jPanelMain.add(jPanelTitle, BorderLayout.NORTH);
			jPanelMain.add(navigationPane, BorderLayout.SOUTH);
			jPanelMain.add(jScrollPaneResults, BorderLayout.CENTER);
			jTableReactionPages.setRowHeight(160);
			jTableReactionPages.setAlignmentX(0.5f);
			jTableReactionPages.setAlignmentY(0.5f);
			jTableReactionPages.setRequestFocusEnabled(false);
			jScrollPaneResults.add(jTableReactionPages);
			jScrollPaneResults.setViewportView(jTableReactionPages);
			{
				Object[][] data = new String[][] { { "0", "1" }, { "2", "3" } };
				Object[] columns = new String[] { "One", "Two" };
				javax.swing.table.TableModel dataModel = new javax.swing.table.DefaultTableModel(data, columns);
				jTableReactionPages.setModel(dataModel);
			}
			
			JButton minus = new JButton(CenIconFactory.getImageIcon("icons/minus.png"));
			minus.addKeyListener(new KeyListenerImpl());
			minus.setToolTipText("Ctrl -");			
			navigationPane.add(minus, BorderLayout.WEST);
			minus.addActionListener(new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					resizePictures(false);
				}
			});			
			
			JButton zero = new JButton(CenIconFactory.getImageIcon("icons/zero.png"));
			zero.addKeyListener(new KeyListenerImpl());
			zero.setToolTipText("Ctrl 0");			
			navigationPane.add(zero, BorderLayout.CENTER);
			zero.addActionListener(new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					resizePictures();
				}
			});
			
			JButton plus = new JButton(CenIconFactory.getImageIcon("icons/plus.png"));
			plus.addKeyListener(new KeyListenerImpl());
			plus.setToolTipText("Ctrl +");
			navigationPane.add(plus, BorderLayout.EAST);
			plus.addActionListener(new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					resizePictures(true);
				}
			});
			
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
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
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		setSize(new Dimension(980, 500));
		reactionPagesTableModel = new ReactionPagesTableModel(jTableReactionPages);
		jTableReactionPages.setModel(reactionPagesTableModel);
		setupTable();
		// relocate JDialog to the center of the screen
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = this.getSize();
		setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2 - (labelSize.height / 2));
		jTableReactionPages.addMouseWheelListener(new MouseWheelListenerImpl());
		this.addKeyListener(new KeyListenerImpl());
		
		jTableReactionPages.addMouseListener(new MouseInputAdapter() {
			public void mouseReleased(MouseEvent e) {			
				if (e.getButton() == 2) {
					String info = (String)reactionPagesTableModel.getValueAt(jTableReactionPages.rowAtPoint(e.getPoint()), 1);
					if (info.indexOf("CONCEPT") > 0)
						jMenuItemLoadExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_CONCEPT_OPEN));
					else if (info.indexOf("MED-CHEM") > 0)
						jMenuItemLoadExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_OPEN));
					else if (info.indexOf("PARALLEL") > 0)
						jMenuItemLoadExperiment.setIcon(CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_PARALLEL_OPEN));
				} else if (e.getClickCount() == 2 && e.getButton() == 1)
					OpenExperimentActionPerformed();
			}
		});		
	}

	public ReactionPagesTableModel getModel() {
		return reactionPagesTableModel;
	}

	private void setupTable() {
		jTableReactionPages.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jTableReactionPages.sizeColumnsToFit(JTable.AUTO_RESIZE_OFF);
		jTableReactionPages.setRowSelectionAllowed(true);
		jTableReactionPages.setColumnSelectionAllowed(false);
		JTableHeader th = jTableReactionPages.getTableHeader();
		th.setFont(new Font(th.getFont().getFontName(), Font.BOLD, th.getFont().getSize() + 2));
		TextAreaRenderer hRenderer = new TextAreaRenderer();
		hRenderer.setBackground(Color.LIGHT_GRAY);
		th.setDefaultRenderer(hRenderer);
		th.setPreferredSize(new java.awt.Dimension(35, 35));
		th.setAutoscrolls(false);
		TableColumnModel vColumModel = jTableReactionPages.getColumnModel();
		TableColumn column = vColumModel.getColumn(0);
		TextPanelRenderer renderer = new TextPanelRenderer();
		column.setCellRenderer(renderer);
		column.setPreferredWidth(100);
		column = vColumModel.getColumn(1);
		column.setPreferredWidth(210);
		column.setCellRenderer(new TextAreaRenderer());
		column = vColumModel.getColumn(2);
		column.setPreferredWidth(initialStructureCellWidth);
		column.setCellRenderer(new ImageIconRenderer());
		// Make Column 4 invisible
		column = vColumModel.getColumn(3);
		column.setMaxWidth(0);
		column.setMinWidth(0);
		column = jTableReactionPages.getTableHeader().getColumnModel().getColumn(3);
		column.setMaxWidth(0);
		column.setMinWidth(0);
		jTableReactionPages.setRowHeight(initialCellHeight);
		jTableReactionPages.setSelectionBackground(Color.white);
		jTableReactionPages.setSelectionForeground(Color.black);
	}

	public void displaySearchResults(List notebookPages) {
		reactionPagesTableModel.buildReactionPagesList(retrieveSmallNotebookPages(notebookPages));
		setTitle("Search Results - " + reactionPagesTableModel.getRowCount() + " record(s) found");
		// jTableReactionPages.getColumnModel().removeColumn(getColumnModel().getColumn(1));
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
			ReactionPagesSearchResultJDialog inst = new ReactionPagesSearchResultJDialog();
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void OpenExperimentActionPerformed() {
		try {
			int row = jTableReactionPages.getSelectedRow();
			ReactionPageInfo pi = (ReactionPageInfo) jTableReactionPages.getValueAt(row, 3);
			NotebookRef nbRefObj = new NotebookRef(pi.getNoteBookExperiment());
			MasterController.getGuiController().openPCeNExperimentCombinedBkndPage(pi.getSitecode(), nbRefObj.getNotebookRef());
			toBack();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, "Open Experiment Failure.", e);
		}
	}

	private void RepeatExperimentActionPerformed() {
		try {
			int row = jTableReactionPages.getSelectedRow();
			ReactionPageInfo pi = (ReactionPageInfo) jTableReactionPages.getValueAt(row, 3);
			NotebookRef nbRefObj = new NotebookRef(pi.getNoteBookExperiment());
			MasterController.getGuiController().repeatExperiment(pi.getSitecode(), pi.getUsername(), nbRefObj.getNbNumber(),
					nbRefObj.getNbPage(), new Integer(pi.getVersion()).intValue());
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, "Repeat Experiment Failure.", e);
		}
	}

	private void PrintExperimentActionPerformed() {
		try {
			int row = jTableReactionPages.getSelectedRow();
			ReactionPageInfo pi = (ReactionPageInfo) jTableReactionPages.getValueAt(row, 3);
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
			list.add(new PrintRequest( pi.getSitecode(), nbRefObj, new Integer(pi.getVersion()), pageType));
			
			MasterController.getGuiController().printExperiments(list);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, "Print Request Failed.", e);
		}
	}

	private List retrieveSmallNotebookPages(List notebookPages) {
		List smallNotebookPages = new ArrayList(notebookPages.size());
		ChemistryDelegate chemDelegate = null;
		try
		{
		chemDelegate = new ChemistryDelegate();
		}catch(Exception e)
		{
			log.error("Unable to initialize ChemistryDelegate:"+e.getMessage());	
		}
		
		log.info("Totally " + notebookPages.size() + " sketches found");
		
		for (int i = 0; i < notebookPages.size(); i++) {
			Object obj = notebookPages.get(i);
			if (obj != null && obj instanceof NotebookPageModel) {
				NotebookPageModel page = (NotebookPageModel) obj;
				ReactionPageInfo reactionPageInfo = new ReactionPageInfo(60, 30);
				//List steps = page.getReactionSteps();
				ReactionStepModel sumStep = page.getSummaryReactionStep();
				byte[] pic = null;
				
				/*
				//-------------------------
				
				//-------------------------
			    	
				// Stream to write file
				FileOutputStream fout;	
				FileInputStream fin;
				int ch;
				
				String type;
				String stringSketch = "STRING";
				String nativeSketch = "NATIVE";
				String ext = ".skc";
				type = nativeSketch;
				byte[] sketch = sumStep.getRxnScheme().getNativeSketch();
				if (sketch == null){
					sketch = sumStep.getRxnScheme().getStringSketch();
					type = stringSketch;
					ext = ".tmp";
				}
				try
				{
				    File testFile = new File("C:\\PK_" + type + "_" + sumStep.getRxnScheme().getKey() + ext);

					if(!testFile.exists())
						testFile.createNewFile();
					
					// Open an output stream
				    fout = new FileOutputStream (testFile);
				    if (sketch != null)
				    fout.write(sketch);
				    // Close our output stream
				    fout.flush();
				    fout.close();	
				    
				    if (i == 0){
				    
				    File readFromFile = new File("C:\\read.skc");
				    
				    fin = new FileInputStream(readFromFile);
				    
				    byte fileContent[] = new byte[(int)readFromFile.length()];
				    
				 	fin.read(fileContent);
				      
				    sumStep.getRxnScheme().setNativeSketch(fileContent);   
				    
				    fin.close();
				    
				    }
				}
				// Catches any error conditions
				catch (IOException e)
				{
					System.err.println ("Unable to write to file");
				}
				
				//------------------------------
				//END OF TEST
				//------------------------------
				*/
				
				
				try {
					log.debug("STARTING Page structure key: " + sumStep.getRxnScheme().getKey());

					if (sumStep.getRxnScheme().getViewSketch() != null &&
						sumStep.getRxnScheme().getViewSketch().length > 0) 
					{
						reactionPageInfo.setReactionImage(sumStep.getRxnScheme().getViewSketch());
					} else if (sumStep.getRxnScheme().getNativeSketch() != null &&
								sumStep.getRxnScheme().getNativeSketch().length > 0) 
					{
						reactionPageInfo.setReactionSketch(sumStep.getRxnScheme().getNativeSketch());
					} else if (sumStep.getRxnScheme().getStringSketch() != null &&
								sumStep.getRxnScheme().getStringSketch().length > 0) {
						reactionPageInfo.setReactionSketch(sumStep.getRxnScheme().getStringSketch());
					}
				} catch (Exception e) {
					log.error("Unable to make JPEG Images:" + e.getMessage(), e);
				}
				reactionPageInfo.setVersion(page.getNbRef().getVersion()+"");
				reactionPageInfo.setNoteBookExperiment(page.getNbRef().getNotebookRef());
				reactionPageInfo.setSitecode(page.getSiteCode());
				// set up reaction page info
				StringBuffer pageInfo = new StringBuffer();
				String desc = null;
				site = page.getSiteCode();
				try {
					user = page.getUserName();
					if (user != null && !user.equals("")) {
						desc = MasterController.getGuiController().getUsersFullName(user);
						if (desc != null)
							pageInfo.append(desc + "\n");
					} else
						pageInfo.append("\n");
					try {
						desc = CodeTableCache.getCache().getSiteDescription(site);
					} catch (Exception e) {
					//nothing	
					}
					if (desc == null)
						desc = site;
					pageInfo.append(desc);
					pageInfo.append("\n");
					if (page.getProjectCode() != null && !page.getProjectCode().equals("")) {
						desc = CodeTableCache.getCache().getProjectsDescription(page.getProjectCode());
						if (desc != null)
							pageInfo.append("Project: " + page.getProjectCode() + " - " + desc + "\n");
						else
							pageInfo.append("Project: " + page.getProjectCode() + "\n");
					}
					pageInfo.append(page.getCreationDate());
					pageInfo.append("\n");
					if (page.getSubject() != null && !page.getSubject().equals(""))
						pageInfo.append("Subject: " + page.getSubject() + "\n");
					if (page.getLiteratureRef() != null && !page.getLiteratureRef().equals(""))
						pageInfo.append("Lit Ref.: " + page.getLiteratureRef() + "\n");
					if (page.getTaCode() != null && !page.getTaCode().equals("")) {
						desc = CodeTableCache.getCache().getTAsDescription(page.getTaCode());
						if (desc != null)
							pageInfo.append("TA: " + desc + "\n");
					}
					if (page.getStatus() != null && !page.getStatus().equals(""))
						pageInfo.append("Experiment Status: " + page.getStatus() + "\n");
					if (page.getContinuedFromRxn() != null && !page.getContinuedFromRxn().equals(""))
						pageInfo.append("Continued From: " + page.getContinuedFromRxn() + "\n");
					if (page.getContinuedToRxn() != null && !page.getContinuedToRxn().equals(""))
						pageInfo.append("Continued To: " + page.getContinuedToRxn() + "\n");
					if (page.getPageHeader() != null && page.getPageHeader().getPageType() != null && !page.getPageHeader().getPageType().equals("")) {
						pageInfo.append("Page Type: " + page.getPageHeader().getPageType() + "\n");
					}
				} catch (CodeTableCacheException codeExc) {
				}
				// TODO: list registered PF's.
				reactionPageInfo.setPageInfo(pageInfo.toString());
				smallNotebookPages.add(reactionPageInfo);
				log.debug("COMPLETED Page structure key: " + sumStep.getRxnScheme().getKey());
			}
		}
		return smallNotebookPages;
	}
	
	private void resizePictures(boolean makeBigger) {
		int sign = makeBigger ? 1 : -1;
		int delta = 30;
		TableColumn column = jTableReactionPages.getColumnModel()
				.getColumn(2);
		int oldHeight, newHeight, oldWidth, newWidth;
		oldHeight = newHeight = jTableReactionPages.getRowHeight();
		oldWidth = newWidth = column.getPreferredWidth();
		
		newHeight = newHeight + sign*delta;
		newWidth = newWidth + sign*delta;
		
		if(newHeight <= 0 || newWidth <= 0) {
			newHeight = oldHeight;
			newWidth = oldWidth;
		}
		jTableReactionPages.clearSelection();
		
		jTableReactionPages.setRowHeight(newHeight);
		column.setPreferredWidth(newWidth);
	}
	
	//return initial size to page 
	private void resizePictures() {
		TableColumn column = jTableReactionPages.getColumnModel()
				.getColumn(2);
		int newHeight, newWidth;
		
		newHeight = initialCellHeight;
		newWidth = initialStructureCellWidth;
		
		jTableReactionPages.clearSelection();
		
		jTableReactionPages.setRowHeight(newHeight);
		column.setPreferredWidth(newWidth);
	}
	
	class MouseWheelListenerImpl implements MouseWheelListener {
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.isControlDown()) {
				int delta = 10;
				TableColumn column = jTableReactionPages.getColumnModel()
						.getColumn(2);
				int newHeight = jTableReactionPages.getRowHeight()
						- e.getWheelRotation() * delta;
				int newWidth = column.getPreferredWidth()
						- e.getWheelRotation() * delta;

				if (newHeight > 0) {
					jTableReactionPages.setRowHeight(newHeight);
					column.setPreferredWidth(newWidth);
				}
				jTableReactionPages.clearSelection();
				e.consume();
			} else {
				jTableReactionPages.getParent().dispatchEvent(e);
			}
		}
	}

	class KeyListenerImpl implements KeyListener {

		public void keyPressed(KeyEvent e) {
			if (e.isControlDown()) {
				if (e.getKeyCode() == KeyEvent.VK_PLUS
						|| e.getKeyCode() == KeyEvent.VK_EQUALS
						|| e.getKeyCode() == KeyEvent.VK_ADD) {
					resizePictures(true);
				} else if (e.getKeyCode() == KeyEvent.VK_MINUS
						|| e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
					resizePictures(false);
				} else if (e.getKeyCode() == KeyEvent.VK_0
						|| e.getKeyCode() == KeyEvent.VK_NUMPAD0) {
					resizePictures();
				}
				e.consume();
			}
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}
	}
}
