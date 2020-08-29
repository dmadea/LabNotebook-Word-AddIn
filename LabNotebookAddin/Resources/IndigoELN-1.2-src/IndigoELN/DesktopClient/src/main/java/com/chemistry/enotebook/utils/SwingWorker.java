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
package com.chemistry.enotebook.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;

/**
 * This is the 3rd version of SwingWorker (also known as SwingWorker 3), an
 * abstract class that you subclass to perform GUI-related work in a dedicated
 * thread. For instructions on and examples of using this class, see:
 * 
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 * 
 * Note that the API changed slightly in the 3rd version: You must now invoke
 * start() on the SwingWorker after creating it.
 */
public abstract class SwingWorker {

	private Object value; // see getValue(), setValue()
	private boolean useStopOnInterrupt = false;
	private static final Log log = LogFactory.getLog(SwingWorker.class);

	/**
	 * Class to maintain reference to current worker thread under separate
	 * synchronization control.
	 */
	private static class ThreadVar {
		private Thread thread;

		ThreadVar(Thread t) {
			thread = t;
		}

		synchronized Thread get() {
			return thread;
		}

		synchronized void clear() {
			thread = null;
		}
	}

	private ThreadVar threadVar;

	/**
	 * Get the value produced by the worker thread, or null if it hasn't been
	 * constructed yet.
	 */
	protected synchronized Object getValue() {
		return value;
	}

	/**
	 * Set the value produced by worker thread
	 */
	private synchronized void setValue(Object x) {
		value = x;
	}

	/**
	 * Compute the value to be returned by the <code>get</code> method.
	 */
	public abstract Object construct();

	/**
	 * Called on the event dispatching thread (not on the worker thread) after
	 * the <code>construct</code> method has returned.
	 */
	public void finished() {
	}

	/**
	 * A new method that interrupts the worker thread. Call this method to force
	 * the worker to stop what it's doing.
	 */
	public void interrupt() {
		Thread t = threadVar.get();
		if (t != null) {
			t.interrupt();
			// The stop() method has been depricated as indicated in JDK
			// 1.4.2_04 AJK
			if (useStopOnInterrupt)
				t.interrupt();// t.stop();
		}
		threadVar.clear();
	}

	/**
	 * Return the value created by the <code>construct</code> method. Returns
	 * null if either the constructing thread or the current thread was
	 * interrupted before a value was produced.
	 * 
	 * @return the value created by the <code>construct</code> method
	 */
	public Object get() {
		while (true) {
			Thread t = threadVar.get();
			if (t == null) {
				return getValue();
			}
			try {
				t.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // propagate
				return null;
			}
		}
	}

	/**
	 * Start a thread that will call the <code>construct</code> method and then
	 * exit.
	 */
	public SwingWorker() {
		final Runnable doFinished = new Runnable() {
			public void run() {
				finished();
			}
		};
		Runnable doConstruct = new Runnable() {
			public void run() {
				try {
					setValue(construct());
				} catch (Exception e) {
					log.info("SwingWorker caught " + e, e);
					// **** If useStopOnInterrupt is true and thread is stopped
					// and in debug mode, will stop here, just continue. SCT
				} finally {
					threadVar.clear();
				}
				SwingUtilities.invokeLater(doFinished);
			}
		};
		Thread t = new Thread(doConstruct, "SwingWorker");
		threadVar = new ThreadVar(t);
	}

	public SwingWorker(boolean useStopOnInterrupt) {
		this();
		this.useStopOnInterrupt = useStopOnInterrupt;
	}

	/**
	 * Start the worker thread.
	 */
	public void start() {
		Thread t = threadVar.get();
		if (t != null) {
			t.start();
		}
	}
}
