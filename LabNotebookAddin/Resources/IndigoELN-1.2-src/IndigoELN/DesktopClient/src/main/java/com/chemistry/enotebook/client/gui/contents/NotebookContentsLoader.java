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
 * Created on Sep 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.client.gui.contents;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.utils.SwingWorker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class NotebookContentsLoader {
	private List listeners = new ArrayList();
	private String siteCode;
	private String notebook;
	private int startExperiment;
	private int endExperiment;

	public NotebookContentsLoader(String siteCode, String notebook) {
		this(siteCode, notebook, -1, -1);
	}

	public NotebookContentsLoader(String siteCode, String notebook, int startExperiment, int endExperiment) {
		this.siteCode = siteCode;
		this.notebook = notebook;
		this.startExperiment = startExperiment;
		this.endExperiment = endExperiment;
	}

	public void load() {
		LoadContentsWorkerData ncd = new LoadContentsWorkerData(siteCode, notebook, startExperiment, endExperiment);
		doLoadGui(ncd);
		doLoadModel(ncd, true);
	}

	public void loadModelOnly() {
		LoadContentsWorkerData ncd = new LoadContentsWorkerData(siteCode, notebook, startExperiment, endExperiment);
		doLoadModel(ncd, false);
	}

	private void doLoadGui(final LoadContentsWorkerData ncd) {
		// Start worker to do loading
		ncd.guiWorker = new SwingWorker() {
			// new thread to start page load and gui load
			public Object construct() {
				NotebookContentsGUI gui = ncd.gui;
				// //System.out.println("worker 1 - construct called");
				if (gui == null)
					gui = new NotebookContentsGUI();
				// //System.out.println("worker 1 - construct done");
				return gui;
			}

			// finished runs in swing thread
			public void finished() {
				ncd.gui = (NotebookContentsGUI) get();
				if (ncd.gui != null) {
					String title = "Contents for Notebook " + ncd.notebook;
					if (ncd.startExperiment != -1 && ncd.endExperiment != -1)
						title = title + ", Experiments " + ncd.startExperiment + " - " + ncd.endExperiment;
					ncd.gui.setTitle(title);
				}
				synchronized (ncd) {
					// //System.out.println("worker 1 - notify");
					ncd.notifyAll();
				}
			}
		};
		ncd.guiWorker.start();
	}

	private void doLoadModel(final LoadContentsWorkerData ncd, final boolean waitForGui) {
		ncd.modelWorker = new SwingWorker() {
			// new thread does the model load
			public Object construct() {
				NotebookContentsTableModel model = ncd.model;
				if (ncd.notebook != null && model == null)
					model = new NotebookContentsTableModel(ncd.siteCode, ncd.notebook, ncd.startExperiment, ncd.endExperiment);
				// This thread should wait
				synchronized (ncd) {
					if (ncd.gui == null && waitForGui) {
						try {
							ncd.wait();
						} catch (Exception e) {
							CeNErrorHandler.getInstance().logExceptionMsg(null, e);
						}
					}
				}
				return model;
			}

			public void finished() {
				ncd.model = (NotebookContentsTableModel) get();
				if (ncd.model != null) {
					if (ncd.gui != null)
						ncd.gui.setModel(ncd.model);
					NotebookContentsLoaderEvent event = new NotebookContentsLoaderEvent(this, ncd.model, ncd.gui);
					fireLoadCompleted(event);
				}
			}
		};
		ncd.modelWorker.start();
	}

	public void addLoaderListener(NotebookContentsLoaderListener listener) {
		listeners.add(listener);
	}

	public void removeLoaderListener(NotebookContentsLoaderListener listener) {
		listeners.remove(listener);
	}

	private void fireLoadCompleted(NotebookContentsLoaderEvent event) {
		Iterator iter = new ArrayList(listeners).iterator();
		while (iter.hasNext()) {
			NotebookContentsLoaderListener listener = (NotebookContentsLoaderListener) iter.next();
			listener.ContentsLoaded(event);
		}
	}

	private class LoadContentsWorkerData {
		public NotebookContentsGUI gui = null;
		public SwingWorker guiWorker = null;
		public NotebookContentsTableModel model = null;
		public SwingWorker modelWorker = null;
		public String siteCode = null;
		public String notebook = null;
		public int startExperiment = -1;
		public int endExperiment = -1;

		public LoadContentsWorkerData() {
		}

		public LoadContentsWorkerData(String siteCode, String notebook) {
			this(siteCode, notebook, -1, -1);
		}

		public LoadContentsWorkerData(String siteCode, String notebook, int startExperiment, int endExperiment) {
			this.siteCode = siteCode;
			this.notebook = notebook;
			this.startExperiment = startExperiment;
			this.endExperiment = endExperiment;
		}
	}
}
