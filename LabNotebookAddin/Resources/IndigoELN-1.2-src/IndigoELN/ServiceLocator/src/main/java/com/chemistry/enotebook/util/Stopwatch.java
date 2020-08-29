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
package com.chemistry.enotebook.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * 
 */
public class Stopwatch {

    private static final Log log = LogFactory.getLog(Stopwatch.class);

    /* Start time of the measured operation */
    private long startTime;

    /* Indicates if clock is ticking */
    private boolean started = false;

    private String descriptionOfTask;


    public Stopwatch() {
    }


    /**
     * Call just before the operation to be measured starts.
     * @param descriptionOfTask
     */
    public void start(String descriptionOfTask) {

        if (this.started) {
        	log.debug("Watch is running, Stopping it before start.");
        	this.stop();
        }

        this.started = true;
        this.descriptionOfTask = descriptionOfTask;
        this.startTime = System.currentTimeMillis();
    }


    /**
     * Call when the operation to be measured is done.
     */
    public void stop() {

        if (!this.started) {
        	log.debug("Unable to stop watch, Not started");
            //throw new IllegalStateException("Call start first");
        }
        started = false;

        if (log.isDebugEnabled()) {
            log.debug(System.currentTimeMillis() - this.startTime +
                    " ms elapsed for [" + this.descriptionOfTask + "]");
        }
    }

    /**
     * Call when the operation to be measured is done.
     */
    public String stopString() {

        if (!this.started) {
            throw new IllegalStateException("Call start first");
        }
        started = false;

        return System.currentTimeMillis() - this.startTime + " ms";
    }
    
    /**
     * Call when the operation to be measured is done.
     */
    public long stopTime() {

        if (!this.started) {
            throw new IllegalStateException("Call start first");
        }
        started = false;

        return System.currentTimeMillis() - this.startTime;
    }

    public static void main(String[] args) {

        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start("Sleep 3 seconds");

        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        stopwatch.stop();
    }
}