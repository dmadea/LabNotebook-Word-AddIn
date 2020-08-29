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
package com.chemistry.enotebook.client.gui.page.attachements;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.domain.AttachmentModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.utils.CeNJobProgressHandler;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.SwingWorker;
import com.cloudgarden.layout.AnchorConstraint;
import com.cloudgarden.layout.AnchorLayout;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class AttachmentsContainer extends javax.swing.JPanel {
	
	private static final long serialVersionUID = 9019471123625792024L;

	private static final Log log = LogFactory.getLog(AttachmentsContainer.class);
	
	private JMenuItem jMenuViewDocument;
	private JMenuItem jMenuSaveDocument;
	private JSeparator jSeparator1;
	private JTable jTableAttachments;
	//private AttachmentsTableModel dataModel;
	private JScrollPane jScrollPaneAttachements;
	private JMenuItem jMenuRemoveDoc;
	private JMenuItem jMenuEditDoc;
	private JMenuItem jMenuAddDoc;
	private JPopupMenu jPopupMenu1;
	private JPanel jPanelMain;
	private JPanel jPanelButtons;
	//private NotebookPage nbPage;
	private NotebookPageModel pageModel;
	private PCeNAttachmentsTableModel mPCeNAttachmentsTableModel;
	private AttachmentUtils attachmentUtils = new AttachmentUtils();
	/**
	 * TODO Retrieve the Attachements from DB in the same order they were sent from ChemENotebook GUI Tip: Create Getter and Setter
	 * functions for a new variable (additionOrder) in Attachement.java set the order in a XML file and store in DB
	 */
	public AttachmentsContainer(NotebookPageModel pageModele) {
		initGUI(pageModele);
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI(NotebookPageModel pageModele) {
		try {
			preInitGUI();
			jPopupMenu1 = new JPopupMenu();
			jMenuAddDoc = new JMenuItem();
			jMenuEditDoc = new JMenuItem();
			jMenuRemoveDoc = new JMenuItem();
			jSeparator1 = new JSeparator();
			jMenuViewDocument = new JMenuItem();
			jMenuSaveDocument = new JMenuItem();
			jMenuAddDoc.setText("Add New Document");
			jPopupMenu1.add(jMenuAddDoc);
			jMenuAddDoc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jMenuAddDoc();
				}
			});
			jMenuEditDoc.setText("Edit Document Details");
			jPopupMenu1.add(jMenuEditDoc);
			jMenuEditDoc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jMenuEditDoc();
				}
			});
			jMenuRemoveDoc.setText("Remove Attached Document");
			jPopupMenu1.add(jMenuRemoveDoc);
			jMenuRemoveDoc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jMenuRemoveDoc();
				}
			});
			jPopupMenu1.add(jSeparator1);
			jMenuViewDocument.setText("View Attached Document");
			jPopupMenu1.add(jMenuViewDocument);
			jMenuViewDocument.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jMenuViewDoc();
				}
			});
			jMenuSaveDocument.setText("Save Attached Document to Disk");
			jPopupMenu1.add(jMenuSaveDocument);
			jMenuSaveDocument.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jMenuSaveDoc();
				}
			});
			jPanelMain = new JPanel();
			jPanelButtons = new JPanel();
			jScrollPaneAttachements = new JScrollPane();
			jTableAttachments = new JTable() {
				private static final long serialVersionUID = -4307628157592305009L;
				public String getToolTipText(MouseEvent e) {
					int row = rowAtPoint(e.getPoint());
					PCeNAttachmentsTableModel mdl = (PCeNAttachmentsTableModel) getModel();
					if (mdl != null) {
						String value = (String) mdl.getValueAt(row, 5);
						if (value != null) {
							return "<html>" + value.replaceAll("\r\n", "<br>") + "</html>";
						} else {
							return null;
						}
					} else {
						return null;
					}
				}
			};
			setPageModel(pageModele);
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setPreferredSize(new java.awt.Dimension(660, 353));
			BorderLayout jPanelMainLayout = new BorderLayout();
			jPanelMain.setLayout(jPanelMainLayout);
			jPanelMainLayout.setHgap(0);
			jPanelMainLayout.setVgap(0);
			jPanelMain.setPreferredSize(new java.awt.Dimension(660, 287));
			this.add(jPanelMain, BorderLayout.CENTER);
			AnchorLayout jPanelButtonsLayout = new AnchorLayout();
			jPanelButtons.setLayout(jPanelButtonsLayout);
			jPanelButtons.setBackground(new java.awt.Color(64, 128, 128));
			jPanelButtons.setPreferredSize(new java.awt.Dimension(660, 26));
			jPanelButtons.setMinimumSize(new java.awt.Dimension(85, 35));
			jPanelButtons.setMaximumSize(new java.awt.Dimension(32767, 35));
			jPanelButtons.setSize(new java.awt.Dimension(660, 26));
			jPanelMain.add(jPanelButtons, BorderLayout.NORTH);
			jScrollPaneAttachements.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jScrollPaneAttachements.setBackground(new java.awt.Color(255, 255, 255));
			jScrollPaneAttachements.setPreferredSize(new java.awt.Dimension(660, 327));
			jScrollPaneAttachements.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
			jPanelMain.add(jScrollPaneAttachements, BorderLayout.CENTER);
			jScrollPaneAttachements.add(jTableAttachments);
			jScrollPaneAttachements.setViewportView(jTableAttachments);
			{
		
				jTableAttachments.setModel(mPCeNAttachmentsTableModel);
			}
			jTableAttachments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jTableAttachments.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					jTableAttachmentsMouseReleased(e);
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					jTableAttachmentsMouseReleased(e);
				}
			});
			jScrollPaneAttachements.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.isPopupTrigger()) {
						displayMenu(e.getComponent(), e.getX(), e.getY());
					}
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.isPopupTrigger()) {
						displayMenu(e.getComponent(), e.getX(), e.getY());
					}
				}
			});
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
		refreshPageModel(pageModel);
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		jPanelButtons.setBackground(new Color(122, 194, 174));
		JButton jButtonAddDocument = new JButton("Options");
		jButtonAddDocument.setPreferredSize(new Dimension(90, 24));
		jButtonAddDocument.setMinimumSize(new Dimension(90, 24));
		jButtonAddDocument.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font("MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		jButtonAddDocument.setIcon(CenIconFactory.getImageIcon(CenIconFactory.General.DROP_DOWN));
		jButtonAddDocument.setHorizontalAlignment(SwingConstants.LEFT);
		jButtonAddDocument.setFocusable(false);
		jPanelButtons.add(jButtonAddDocument, new AnchorConstraint(157, 898, 928, 750, 1, 1, 1, 1));
		jButtonAddDocument.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evt) {
				jButtonAddDocumentMouseReleased(evt);
			}
		});
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
			AttachmentsContainer inst = new AttachmentsContainer(null);
			frame.setContentPane(inst);
			frame.getContentPane().setSize(inst.getSize());
			frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonAddDocumentMouseReleased(MouseEvent evt) {
		displayMenu(evt.getComponent(), 0, (evt.getComponent().getY() + evt.getComponent().getHeight() - 6));
	}

	/** Auto-generated event handler method */
	protected void jMenuAddDoc() {
		AttachDocumentJDialog.showGUI(MasterController.getGUIComponent(), "Add New Document", mPCeNAttachmentsTableModel, pageModel.getNotebookRefAsString(), !pageModel.isEditable(), 0);
	}

	public void setPageModel(NotebookPageModel nbPage) {
		this.pageModel = nbPage;
		if (nbPage != null) {
			mPCeNAttachmentsTableModel = new PCeNAttachmentsTableModel(pageModel.getAttachmentCache(), !pageModel.isEditable(), pageModel);
			initTableModel();
		} else { // we're removing the model so kill the objects
			((PCeNAttachmentsTableModel) jTableAttachments.getModel()).dispose();
			jTableAttachments.setModel(new DefaultTableModel());
			this.mPCeNAttachmentsTableModel = null;
		}
	}

	public NotebookPageModel getPageModel() {
		return pageModel;
	}

	private void initTableModel() {
		jTableAttachments.setModel(mPCeNAttachmentsTableModel);
		JTableHeader th = jTableAttachments.getTableHeader();
		th.setFont(new Font(th.getFont().getFontName(), Font.BOLD, th.getFont().getSize() + 2));
		DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) th.getDefaultRenderer();
		headerRenderer.setVerticalAlignment(SwingConstants.CENTER);
		headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		jTableAttachments.setRowHeight(20);
		jTableAttachments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jTableAttachments.setColumnSelectionAllowed(false);
		TableColumn column = null;
		int intColNum;
		int cols = jTableAttachments.getColumnCount();
		for (int i = 0; i < cols; i++) {
			if (i >= jTableAttachments.getColumnCount())
				intColNum = jTableAttachments.getColumnCount() - 1;
			else
				intColNum = i;
			column = jTableAttachments.getColumnModel().getColumn(intColNum);
			if (i == 0) {
				column.setPreferredWidth(60);
			}
			if (i == 1) {
				column.setPreferredWidth(470);
			}
			if (i == 2) {
				column.setPreferredWidth(100);
				DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
				renderer.setHorizontalAlignment(SwingConstants.RIGHT);
				column.setCellRenderer(renderer);
			}
			if (i == 3) {
				column.setPreferredWidth(150);
				DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
				renderer.setHorizontalAlignment(SwingConstants.RIGHT);
				column.setCellRenderer(renderer);
			}
			if (i == 4) {
				column.setPreferredWidth(120);
			}
			if (i == 5 || i == 6) {
				jTableAttachments.removeColumn(column);
			}
		}
		if (pageModel != null && pageModel.isEditable())
			mPCeNAttachmentsTableModel.addMouseListenerToColumnsInTable(jTableAttachments);
	}

	/** Auto-generated event handler method */
	protected void jMenuViewDoc() {
		int row = jTableAttachments.getSelectedRow();
		if (row > -1) {
			// the attachment cache is ordered so entries match the table
			// rows
			AttachmentModel doc = mPCeNAttachmentsTableModel.getAttachmentModelWhichInTableRow(row);			
//			// TODO: if the doc is null call a refresh method
//			if (doc != null && doc.getContents() == null && !doc.getType().equals(".html")) {
//				// Need to make a call to the server to load the attachment
//				// via the notebook page
//				//NotebookPageCache nbCache = MasterController.getGuiController().getPageCache();
//				HashMap tableKeys = new HashMap();
//				tableKeys.put(CeNTableName.CEN_ATTACHMENTS.toString(), doc.getKey());
//				//try {
//					//nbCache.loadNotebookPageData(pageModel, CeNConstants.PAGE_BYTE_RETRIEVE_ATTACHMENTS, tableKeys);
//				//} catch (NotebookDelegateException e) {
//				//	CeNErrorHandler.getInstance().logExceptionMsg(null, e);
//				//}
//			}
			
			File temp = null;
			String strUrl = null;
			
			if (!StringUtils.equals(doc.getType(), ".html") && !AttachmentUtils.isWebSite(doc.getOriginalFileName())) {
				// write the attachment contents to local disk
				temp = attachmentUtils.createTemporaryFile(doc);
				if (temp == null) {  // file being lazy loaded or exception
				  return;
				}
			} else {
				strUrl = doc.getOriginalFileName();
			}
			try {
				attachmentUtils.launchFile(temp, strUrl);
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
	}

	protected void jMenuSaveDoc() {
		int row = jTableAttachments.getSelectedRow();
		if (row > -1) {
			// the attachment cache is ordered so entries match the table
			// rows
			final AttachmentModel doc = pageModel.getAttachmentCache().getAttachment(row);
//			if (doc.getContents() == null && !doc.getType().equals(".html")) {
//				// Need to make a call to the server to load the attachment
//				// via the notebook page
//				//NotebookPageCache nbCache = MasterController.getGuiController().getPageCache();
//				HashMap tableKeys = new HashMap();
//				tableKeys.put(CeNTableName.CEN_ATTACHMENTS.toString(), doc.getKey());
//				//try {
//					//nbCache.loadNotebookPageData(pageModel, CeNConstants.PAGE_BYTE_RETRIEVE_ATTACHMENTS, tableKeys);
//				//} catch (NotebookDelegateException e) {
//				//	CeNErrorHandler.getInstance().logExceptionMsg(null, e);
//				//}
//			}
			if (!doc.getType().equals(".html") && !AttachmentUtils.isWebSite(doc.getOriginalFileName())) {
				String fname = doc.getOriginalFileName();
				
				JFileChooser jfc = new JFileChooser();
				try {
					String sDir = MasterController.getUser().getPreference(NotebookUser.PREF_PATH_ATTACHEMENT_SAVE);
					if (sDir != null && sDir.length() > 0)
						jfc.setCurrentDirectory(new File(sDir));
					else {
						sDir = MasterController.getUser().getPreference(NotebookUser.PREF_PATH_ATTACHEMENT_UPLOAD);
						if (sDir != null && sDir.length() > 0)
							jfc.setCurrentDirectory(new File(sDir));
					}
				} catch (UserPreferenceException e1) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e1);
				}
				
				// prompt to save attachment to disk
				jfc.setSelectedFile(new File(fname.substring(fname.lastIndexOf(File.separator) + 1)));
				jfc.setMultiSelectionEnabled(false);
				int result = jfc.showSaveDialog(this);
				if (result == JFileChooser.APPROVE_OPTION) {
					File saveFile = jfc.getSelectedFile();
					
					if (saveFile != null) {
					  if (saveFile.exists()) {
					    if (JOptionPane.showConfirmDialog(new JFrame(),
					        saveFile.getPath() + " already exists.\nDo you want to replace it?", "Save Attachment",
					        JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
					          return;
					    }
					  }
						try {
							final FileOutputStream out = new FileOutputStream(saveFile);
							
		          if (doc.getContents() == null) { // lazy load the file
		            new SwingWorker() {
		              
		              public Object construct() {
		                NotebookUser user = MasterController.getUser();
		                SessionIdentifier sessionID = user.getSessionIdentifier();
		                String progressStatus = "Loading Attachment \"" + doc.getDocumentName() + "\" ...";
		              
		                try {
		                  StorageDelegate storageDelegate = ServiceController.getStorageDelegate(sessionID);
		                  
		                  CeNJobProgressHandler.getInstance().addItem(progressStatus);
		                  AttachmentModel attachmentModel = storageDelegate.getNotebookPageExperimentAttachment(doc.getKey());
		                  
		                  doc.setContents(attachmentModel.getContents());
		                  out.write(doc.getContents());
		                  out.close();
		                }
		                catch (Exception e) {
		                  JOptionPane.showMessageDialog(MasterController.getGuiComponent(), "Error: Unable to retrieve attachment from server", "File Retrieve Error", JOptionPane.ERROR_MESSAGE);
		                  log.error("Error: Unable to retrieve attachment from server", e);
		                }
		                finally {
		                  CeNJobProgressHandler.getInstance().removeItem(progressStatus);
		                }
		                
		                return null;
		              }
		              
		              public void finished() {
		              }
		            }.start();  
		            
		            return;
		          }
							out.write(doc.getContents());
							out.close();

							try {
								MasterController.getUser().setPreference(NotebookUser.PREF_PATH_ATTACHEMENT_SAVE, saveFile.getAbsolutePath());
							} catch (UserPreferenceException e1) {
								CeNErrorHandler.getInstance().logExceptionMsg(null, e1);
							}
						} catch (IOException e) {
							CeNErrorHandler.getInstance().logExceptionMsg(null, e);
							JOptionPane.showMessageDialog(this, "Error: Unable to save attachments contents to disk", "File Write Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			} else {
				JOptionPane.showMessageDialog(this, "Cannot save URL contents to disk", "Invalid File Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/** Auto-generated event handler method */
	protected void jMenuRemoveDoc() {
		int row = jTableAttachments.getSelectedRow();
		if (row > -1) {
			// the attachment cache is ordered so entries match the table
			// rows
			AttachmentModel doc = mPCeNAttachmentsTableModel.getAttachmentModelWhichInTableRow(row);
			if (doc != null) {
				doc.setToDelete(true);
				
				mPCeNAttachmentsTableModel.populateModel(pageModel.getAttachmentCache());
				//mPCeNAttachmentsTableModel.fireTableDataChanged();
				enableSaveButton();
			}
		}
	}
	private void enableSaveButton() {
		pageModel.setModelChanged(true);
		MasterController.getGUIComponent().enableSaveButtons();
	}

	/** Auto-generated event handler method */
	protected void jMenuEditDoc() {
		final int row = jTableAttachments.getSelectedRow();
		if (row > -1) {
			// the attachment cache is ordered so entries match the table
			// rows
			// Attachment doc =
			// nbPage.getAttachmentCache().getAttachment(row);
		  final AttachmentModel doc = pageModel.getAttachmentCache().getAttachment(row);
		  
		  if (doc.getContents() == null) {  // need to lazy load the document otherwise it will be nulled out by server
		    Runnable runnable = new Runnable() {
          
          public void run() {
            NotebookUser user = MasterController.getUser();
            SessionIdentifier sessionID = user.getSessionIdentifier();
            String progressStatus = "Loading Attachment \"" + doc.getDocumentName() + "\" ...";
          
            try {
              StorageDelegate storageDelegate = ServiceController.getStorageDelegate(sessionID);
              
              CeNJobProgressHandler.getInstance().addItem(progressStatus);
              AttachmentModel attachmentModel = storageDelegate.getNotebookPageExperimentAttachment(doc.getKey());
              
              if(CommonUtils.isNotNull(attachmentModel)) {
            	  doc.setContents(attachmentModel.getContents());
              }
            }
            catch (Exception e) {
              JOptionPane.showMessageDialog(MasterController.getGuiComponent(), "Error: Unable to retrieve attachment from server", "File Retrieve Error", JOptionPane.ERROR_MESSAGE);
              log.error("Unable to retrieve attachment from server", e);
            }
            finally {
              CeNJobProgressHandler.getInstance().removeItem(progressStatus);
            }
          }
      
        };
        Thread worker = new Thread(runnable);
        
        worker.start();
//        try {
//          worker.join();
//        }
//        catch (InterruptedException e) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//        }
//        finally {
//          if (doc.getContents() == null) {
//            return; // exception occurred
//          }
//        }
      }
			AttachDocumentJDialog.showGUI(MasterController.getGUIComponent(), "Edit Document", mPCeNAttachmentsTableModel, pageModel
					.getNotebookRefAsString(), true, row);
		}
	}

	/** Auto-generated event handler method */
	protected void jTableAttachmentsMouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			Point p = e.getPoint();
			int row = jTableAttachments.rowAtPoint(p);
			jTableAttachments.addRowSelectionInterval(row, row);
			displayMenu(e.getComponent(), e.getX(), e.getY());
		}
	}

	private void displayMenu(Component component, int x, int y) {
		// bug fix 23584 start, Observe the enabled/disabled status of the menu
		// items
		boolean editable = pageModel.isEditable();
		if (jTableAttachments.getSelectedRow() < 0 || !editable) {
			jMenuEditDoc.setEnabled(false);
			jMenuRemoveDoc.setEnabled(false);
			// jMenuSaveDocument.setEnabled(false);
		} else {
			jMenuEditDoc.setEnabled(true);
			jMenuRemoveDoc.setEnabled(true);
			jMenuSaveDocument.setEnabled(true);
		}
		// bug fix end
		jMenuAddDoc.setEnabled(editable);
		jMenuViewDocument.setEnabled(jTableAttachments.getSelectedRow() >= 0);
		jMenuSaveDocument.setEnabled(jTableAttachments.getSelectedRow() >= 0);
		jPopupMenu1.show(component, x, y);
	}

	public void refreshPageModel(NotebookPageModel pageModel2) {
		boolean isEditable = pageModel2.isEditable();
		jPopupMenu1.setEnabled(isEditable);
		jMenuAddDoc.setEnabled(isEditable);
		jMenuEditDoc.setEnabled(isEditable);
		jMenuRemoveDoc.setEnabled(isEditable);
		jSeparator1.setEnabled(isEditable);
		//jMenuViewDocument.setEnabled(isEditable);
		//jMenuSaveDocument.setEnabled(isEditable);
		jPopupMenu1.setEnabled(isEditable);		
	}
}
