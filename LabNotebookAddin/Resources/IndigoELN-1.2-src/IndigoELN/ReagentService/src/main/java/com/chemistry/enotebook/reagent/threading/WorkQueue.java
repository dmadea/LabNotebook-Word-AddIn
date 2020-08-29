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

import java.util.LinkedList;
/**
 * 
 *
 * The queue where all the work is placed.
 */
class WorkQueue {


  public WorkQueue() {
    work = new LinkedList();
  }

  /** Adds a job to the queue for execution by the thread group. */
  public synchronized void addWork(Runnable task) {
    work.add(task);
    notify();
  }

  /** Gets the nedd job on the queue and then removes it. */
  public synchronized Object getWork() throws InterruptedException {

    while ( work.isEmpty() ) {
      try {
//System.out.println(Thread.currentThread().getName()+ ": waiting");
        wait();
//System.out.println(Thread.currentThread().getName()+ ": running");
      }
      catch (InterruptedException ie) {
        throw ie;
      }
    }
    return work.remove(0);
  }


  /** Gets the size of the queue. */
  protected int size() {
    return work.size();
  }


  private LinkedList work;

}