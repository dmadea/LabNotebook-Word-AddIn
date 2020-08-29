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
 * Created on Sep 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.reagent.threading;


//import java.util.Date;
//import weblogic.application.ApplicationLifecycleEvent;
//import weblogic.application.ApplicationLifecycleListener;

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;

/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Listener {

    public static PooledExecutor getExecutor() {
        if (threadPool == null) {
            try {
                threadPool = new PooledExecutor();
                threadPool.setMinimumPoolSize(10);
                threadPool.setMaximumPoolSize(50);
                threadPool.setKeepAliveTime(1000 * 60 * 2);
                threadPool.createThreads(5);
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        System.err.println("Shutdown PooledExecutor");
                        try {
                            threadPool.interruptAll();
                            Thread.sleep(5000);
                            threadPool.shutdownNow();
                        } catch (Exception e) {
                            //TODO
                            e.printStackTrace();
                        }
                    }

                    ;
                });
            } catch (Throwable t) {
                System.out.println("Listener > postStart.Throwable: " + t);
            }
        }
        return threadPool;
    }

    private static PooledExecutor threadPool = null;
//public class Listener extends ApplicationLifecycleListener {
//
//  public static PooledExecutor threadPool = null;
//  
//  public Listener() {
//    //System.out.println("Listener > constructor: " + new Date());
//   } // Listener
//
//
//  public void preStart(ApplicationLifecycleEvent evt) {
//    try {
//      System.out.println
//          ("Listener > ----------------------------------------------");
//      System.out.println
//          ("Listener > Starting Application @" + new Date());
//      System.out.println("Listener > preStart: ");
//   }
//    catch(Throwable t) {
//      System.out.println("Listener > preStart.Throwable: "+t);
//    }
//   } // preStart
//
//
//  public void postStart(ApplicationLifecycleEvent evt) {
//    try {
//      System.out.println("Listener > postStart: ");
//
//      System.out.println("Listener > postStart: start  ThreadGroup");
//     // ThreadPool pool = ThreadPool.getInstance();
//      	threadPool = new PooledExecutor();
//      	threadPool.setMinimumPoolSize(10);
//        threadPool.setMaximumPoolSize(50);
//        threadPool.setKeepAliveTime(1000*60*2);
//        threadPool.createThreads(5);
//    }
//    catch (Throwable t) {
//        
//      System.out.println("Listener > postStart.Throwable: " + t);
//    }
//   } // postStart
//
//
//
//  public void preStop(ApplicationLifecycleEvent evt) {
//    try {
//      System.out.println
//          ("Listener > ----------------------------------------------");
//      System.out.println
//          ("Listener > Stopping Application @" + new Date());
//
//      System.out.println("Listener > preStop: ");
//
//      
//      // ThreadPool pool = ThreadPool.getInstance();
//      System.out.println("Listener > preStop: stop  Thread Group");
//      //pool.destroyThreadGroups();
//      
//      System.out.println("Listener > Interrupting all the threads");
//      threadPool.interruptAll();
//      //    Sleep while the threads above are interrupted
//      Thread.sleep(5000);
//
//      threadPool.shutdownNow();
//      
//    }
//    catch (Throwable t) {
//      System.out.println("Listener > preStop.Throwable: " + t);
//    }
//   } // preStop
//
//
//
//
//  public void postStop(ApplicationLifecycleEvent evt) {
//    try {
//      System.out.println("Listener > postStop: ");
//
//    }
//    catch (Throwable t) {
//      System.out.println("Listener > postStop.Throwable: " + t);
//    }
//   } // postStop
//
//
//
//
//   /** Not used. */
//   public static void main(String[] args) {
//     System.out.println("Listener > postStart: should never see.");
//   } // main
}
