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
 * Created on Sep 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.reagent.threading;

import java.util.Date;


/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ThreadPool {
	/**
	 * Null constructor so no one can intialize a new pool
	 */
	private ThreadPool() {
	}
	
	
	/** Gets an instance of the ThreadPool.
	 *
	 * @throws ThreadPoolException
	 * @return ThreadPool
	 * */
	public static synchronized ThreadPool getInstance() 
		throws ThreadPoolException 
	{
		if (instance == null) {
			try {
				System.out.println("ThreadPool > New ThreadPool instance starting with "+DEFAULT_SIZE+" threads: ");
				
				instance = new ThreadPool();
				instance.destroyThreadGroups();
				instance.workQueue = new WorkQueue();
				instance.pool = new WorkerThread[DEFAULT_SIZE];
				instance.startPool();
			} catch(Throwable t) {
				throw new ThreadPoolException("ThreadPool.getInstance(): "+t, t);
			}
		} 
		return instance;
	}
	
	
	/**
	 * Starts the thread pool running. Each thread in the pool waits
	 * for work to be added using the add() method.
	 * @throws ThreadPoolException
	 */
	protected void startPool() 
		throws ThreadPoolException 
	{	
		if ( !started ) {
			System.out.println("ThreadPool > Starting Thread Group: "
					+THREAD_GROUP_NAME_PREFIX+": "+THREAD_GROUP_SERIAL_NUMBER);
			try {
				started = true;
				
				ThreadGroup groupMain = getMainThreadGroup();
				ThreadGroup groupThreadPool = new ThreadGroup(groupMain,
						THREAD_GROUP_NAME_PREFIX+": "+THREAD_GROUP_SERIAL_NUMBER);
				for (int i = 0; i < pool.length; i++) {
					pool[i] = new WorkerThread(groupThreadPool,
							"ExecuteThread: '" + new Integer(i).toString()
							+ "' for queue: 'com.rgate.ThreadPool.ThreadPool'");
					pool[i].setPriority(Thread.NORM_PRIORITY);
					pool[i].start();
				}
				// Give the pool a little time to setup
				Thread.sleep(1000);
			} catch (Throwable t) {
				throw new ThreadPoolException("ThreadPool.startPool(): " + t, t);
			}	
		} 
	}
	
	/** Destroys ThreadPool Thread Groups orphaned by the WebLogic deployment process
	 * or because it is intended to destroy them. */
	public void destroyThreadGroups() {
		System.out.println("ThreadPool > Looking for ThreadPool Thread Group(s) to Destroy: ");
		
		// Get the main group
		ThreadGroup group = getMainThreadGroup();
		
		ThreadGroup[] groups = new ThreadGroup[group.activeGroupCount()];
		int cntGroups = group.enumerate(groups, true);
		
		for (int i = 0; i < groups.length; i++) {
			if ( groups[i].getName().startsWith( THREAD_GROUP_NAME_PREFIX )) {
				//&& !groups[i].getName().endsWith( ""+this.THREAD_GROUP_SERIAL_NUMBER+"" )) {
				try {
					System.out.println("ThreadPool > Found ThreadPool Thread Group: " + groups[i].getName());
					
					// Interrup the group, may have a thread is an state that cannot be interrupted, if so try again
					System.out.println("ThreadPool > Attempting to interrupt threads in group: ");
					try {
						groups[i].interrupt();
						System.out.println("ThreadPool > Interrupted threads in group: ");
					} catch (Throwable t){
						System.out.println("ThreadPool > First attempt to interrupt failed, trying again: ");
						Thread.sleep(5000);
						groups[i].interrupt();
						System.out.println("ThreadPool > Interrupted threads in group: ");
					}
					
					// Destroy the group
					if (!groups[i].isDestroyed()) {
						System.out.println("ThreadPool > Attempting to Destroy Thread Group: " + groups[i].getName() );
						Thread.sleep(5000);
						groups[i].destroy();
						System.out.println("ThreadPool > Destroyed Thread Group: " + groups[i].getName() );
					} else {
						System.out.println("ThreadPool > Thread Group: " + groups[i].getName()+ " already destroyed" );
					}
					for (int x=0; x<5; x++) {
						Thread.sleep(100);
						System.gc();
					}
				} catch (Throwable t) {
					System.out.println("ThreadPool > ThreadPool.destroyThreadGroups(): " + i + ": "+groups[i].getName()+" "+ t);
				}
			}
		}
	}
	
	/** Gets the main thread group needed as the parent of this threadpool's group. */
	private ThreadGroup getMainThreadGroup(){
		ThreadGroup[] groups = new ThreadGroup[] {};
		
		// Move the group levels until we get to the main group, a child of system
		ThreadGroup groupMain = Thread.currentThread().getThreadGroup();
		int z=0;
		while ( !groupMain.getName().equals("main") ) {
			z++;
			groupMain = Thread.currentThread().getThreadGroup().getParent();
			if (groupMain.getName().equals("system") || z==10) {
				// Went to far up
				break;
			}
		} 
		
		// If we missed main and got system then look inside system for main
		if ( !groupMain.getName().equals("system") ) {
			groupMain.enumerate(groups);
			for (int i = 0; i < groups.length; i++) {
				if (groups[i].getName().equals("main")) {
					groupMain = groups[i];
					break;
				}
			}
		} 
		
		if (!groupMain.getName().equals("main")) {
			System.out.println("Listener > WARNING: Unable to find main thread group");
		}
		
		return groupMain;
	}
	
	
	/**
	 * Determines if a thread group already exists. Available to all applications.
	 * @param groupName the thread group name to verify existance for
	 * @throws ThreadPoolException
	 * @return boolean true is the group exists
	 */
	public boolean groupExists(String groupName) throws ThreadPoolException {
		ThreadGroup systemGroup = Thread.currentThread().getThreadGroup().getParent().getParent();
		ThreadGroup[] groups = new ThreadGroup[ systemGroup.activeGroupCount() ];
		int cnt = systemGroup.enumerate(groups, true);
		for (int i=0; i<cnt; i++) {
			if ( groupName.toLowerCase().equals(groups[i].getName().toLowerCase()) ) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Gets a ThreadGroup if it exists. Available to all applications.
	 */
	public ThreadGroup getThreadGroup(String groupName) throws ThreadPoolException {
		ThreadGroup systemGroup = Thread.currentThread().getThreadGroup().getParent();
		ThreadGroup[] groups = new ThreadGroup[systemGroup.activeGroupCount()];
		int cnt = systemGroup.enumerate(groups, true);
		for (int i = 0; i < cnt; i++) {
			if (groupName.toLowerCase().equals(groups[i].getName().toLowerCase())) {
				return groups[i];
			}
		}
		return null;
	}
	
	/** Gets the name prefix of the Thread Group used by the ThreadPool Thread Pool.
	 * <p>Available to all applications.
	 * * @deprecated use getThreadGroupNamePrefix, this method name had a typo
	 */
	public String getTheadGroupNamePrefix() {
		return THREAD_GROUP_NAME_PREFIX;
	}
	
	/** Gets the name prefix of the Thread Group used by the ThreadPool Thread Pool.
	 * <p>Available to all applications.
	 */
	public String getThreadGroupNamePrefix() {
		return THREAD_GROUP_NAME_PREFIX;
	}	
	
	/** Gets the serial number of the current Thread Group used by the ThreadPool Thread Pool.
	 * <p>There may be older orphaned groups left over by WebLogic redeployments.
	 * <p>Available to all applications.
	 * @deprecated use getThreadGroupSerialNumber, this method name had a typo
	 */
	public long getTheadGroupSerialNumber() {
		return THREAD_GROUP_SERIAL_NUMBER;
	}
	
	/** Gets the serial number of the current Thread Group used by the ThreadPool Thread Pool.
	 *  <p>There may be older orphaned groups left over by WebLogic redeployments.
	 *  <p>Available to all applications.
	 */
	public long getThreadGroupSerialNumber() {
		return THREAD_GROUP_SERIAL_NUMBER;
	}
	
	/**
	 * Stop the pool.
	 */
	protected void stopPool() {
		shouldRun = false;
	}
	
	/**
	 * Add work to the queue.
	 * @param Runnable task - the task that is to be run.
	 */
	protected void add(Runnable task) {
		workQueue.addWork(task);
	}
	
	/**
	 * Determines if a thread exists.
	 */
	public boolean exists(int numb) throws ThreadPoolException{
		boolean flag = false;
		try {
			flag = pool[numb].ping();
		} catch(Throwable t){
			return false;
		}
		return flag;
	}
	
	/**
	 * Gets the status of the thread, 0=idle, 1=running, -1=not found.
	 */
	public int getStatus(int numb) throws ThreadPoolException {
		try {
			return pool[numb].status;
		} catch (Throwable t) {
			return -1;
		}
	}	
	
	/**
	 * Gets the created timestamp of the thread.
	 */
	public Date getCreatedDttm(int numb) throws ThreadPoolException {
		try {
			return pool[numb].created;
		}
		catch (Throwable t) {
			return null;
		}
	}
	
	/**
	 * Gets the last run timestamp of the thread.
	 */
	public Date getLastDttm(int numb) throws ThreadPoolException {
		try {
			return pool[numb].lastRun;
		} catch (Throwable t) {
			return null;
		}
	}
	
	/**
	 * Gets the description of the thread.
	 */
	public String getDescription(int numb) throws ThreadPoolException {
		try {
			return pool[numb].describe;
		} catch (Throwable t) {
			return null;
		}
	}	
	
	/**
	 * Gets the size of the work queue that supports the ThreadPool thread pool.
	 */
	public int getQueueSize() throws ThreadPoolException {
		return workQueue.size();
	}
	
	/**
	 * Gets the size of the pool.
	 */
	public int getSize() throws ThreadPoolException {
		return pool.length;
	}
		
	protected void finalize() throws Throwable{
		System.out.println("Thread pool finalize method fired: "+THREAD_GROUP_SERIAL_NUMBER);
		this.stopPool();
	}
	
	
	// ----- FIELDS ----- //
	
	static private ThreadPool instance = null;
	/** Time in milliseconds the singleton was started. */
	protected final long STARTED = System.currentTimeMillis();
	private WorkerThread[] pool;
	private WorkQueue workQueue;
	
	public static final int DEFAULT_SIZE = 25;
	private volatile boolean shouldRun = true;
	private boolean started = false;
	
	private static final String THREAD_GROUP_NAME_PREFIX = "Thread Group for ThreadPool";
	private static final long THREAD_GROUP_SERIAL_NUMBER = new Date().getTime();
	
	
	// ----- INNER CLASSES ----- //
	
	
	/**
	 * Inner class that does all the thread work
	 */
	private class WorkerThread 
		extends Thread 
	{
		private WorkerThread(ThreadGroup group, String name) {
			super(group, name);
			setName(name);
			setDaemon(true);
		}
		
		private WorkerThread() {
			super();
		}
		
		public boolean ping() {
			return true;
		}
		
		protected int status = 0; // 0=waiting, 1=running
		protected Date created = new Date();
		protected Date lastRun = new Date();
		protected String describe = "";
		
		public void run() {
			while (shouldRun) {
				try {
					status = 0;
					//describe = "";
					Runnable work = (Runnable) workQueue.getWork();
					status = 1;
					lastRun = new Date();
					// Let describe remember the last few objects it ran
					int len = describe.length() > 256 ? 256 : describe.length();
					describe = work.toString()+ " --- "+ describe.substring(0,len);
					work.run();
				} catch (InterruptedException ie) {
					//System.out.println("ThreadPool > Thread "+getName().substring(0,28)+" interrupted");
					//shouldRun = false;
				} catch (Throwable t) {
					System.out.println("ThreadPool > Thread "+getName().substring(0,28)+" errored: "+t);
					//shouldRun = false;
				}
			}
		}
	}
}