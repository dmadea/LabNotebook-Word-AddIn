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

public class GenericTaskWrapper {

  /**
   * Places the Runnable object into the UAL ThreadPool execution queue.
   * @param task Runnable object
   * @throws ThreadPoolException If error in getting the ThreadPool or queueing the
   *  Runnable
   */
  public static void queueTask( Runnable task ) throws ThreadPoolException {

    try {
    	ThreadPool pool = ThreadPool.getInstance();
    	pool.add(task);
    }
    catch (Throwable t) {
    	throw new ThreadPoolException("GenericTaskWrapper.queueTask(): " + t, t);
    }

  } // end public static void queueTask(...)

} // end public class GenericTaskWrapper
