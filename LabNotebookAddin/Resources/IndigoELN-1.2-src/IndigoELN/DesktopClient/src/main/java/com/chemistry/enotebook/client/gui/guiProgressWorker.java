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
 * Created on 08-Sep-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui;

import com.chemistry.enotebook.client.controller.MasterController;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class guiProgressWorker extends Thread {
	private int count = 0;
	private boolean allDone = false;
	private boolean plsWait = false;

	public guiProgressWorker() {
		super();
	}

	/**
	 * @param arg0
	 */
	public guiProgressWorker(Runnable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public guiProgressWorker(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public guiProgressWorker(ThreadGroup arg0, Runnable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public guiProgressWorker(Runnable arg0, String arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public guiProgressWorker(ThreadGroup arg0, String arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public guiProgressWorker(ThreadGroup arg0, Runnable arg1, String arg2) {
		super(arg0, arg1, arg2);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public guiProgressWorker(ThreadGroup arg0, Runnable arg1, String arg2, long arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#destroy()
	 */
	public void destroy() {
		// System.out.println("finished");
		/*
		 * Set the worker to null and stop the animation, but only if we are the active worker.
		 */
		// if (worker == this) worker = null;
		// super.destroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#interrupt()
	 */
	public void interrupt() {
		super.interrupt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#isInterrupted()
	 */
	public boolean isInterrupted() {
		return super.isInterrupted();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (!Thread.interrupted()) {
			if (MasterController.getGUIComponent() == null)
				break;
			MasterController.getGUIComponent().setProgressBar(count);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
			if (count == 99)
				count = 0;
			count++;
			synchronized (this) {
				while (plsWait) {
					try {
						// Set the Progress to 0 before it goes to waiting
						MasterController.getGUIComponent().setProgressBar(0);
						wait();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		// super.run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#start()
	 */
	public synchronized void start() {
		count = 0;
		// this.start();
		// return null;
		super.start();
	}

	public void setallDone(boolean allDone) {
		this.allDone = allDone;
	}

	/**
	 * @param plsWait
	 *            The plsWait to set.
	 */
	public void setPlsWait(boolean plsWait) {
		this.plsWait = plsWait;
	}

	public void resetCount() {
		count = 0;
	}
}
