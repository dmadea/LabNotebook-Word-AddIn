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
package com.chemistry.enotebook.client.gui.common.utilsui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeStatusBarItem extends JPanel {

	private static final long serialVersionUID = 3839369524864683376L;

	private JLabel label;

	private DateFormat time = DateFormat.getTimeInstance();
	private DateFormat date = DateFormat.getDateInstance();
	private int interval = 500;
	private Timer timer;
	private TimeZone timeZone = TimeZone.getDefault();
	private Locale locale = Locale.getDefault();

	public TimeStatusBarItem() {
		this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		this.setLayout(new BorderLayout(3, 0));
		
		label = new JLabel();
		this.add(label, BorderLayout.LINE_START);
		updateTime();
		this.timer = new Timer(this.interval, new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				TimeStatusBarItem.this.updateTime();
			}
		});
	}

	private void setText(String text) {
		label.setText(text);
	}

	private void setToolTip(String tooltip) {
		super.setToolTipText(tooltip);
	}

	protected void updateTime() {
		Date localDate = Calendar.getInstance(this.timeZone, this.locale).getTime();
		setText(this.time.format(localDate));
		setToolTip(this.date.format(localDate));
	}

	public void setUpdateInterval(int paramInt) {
		boolean timerRunning = this.timer.isRunning();
		if (timerRunning)
			this.timer.stop();
		this.interval = paramInt;
		this.timer.setDelay(paramInt);
		if (!(timerRunning))
			return;
		this.timer.start();
	}

	public int getUpdateInterval() {
		return this.interval;
	}

	public void setTextFormat(DateFormat paramDateFormat) {
		boolean timerRunning = this.timer.isRunning();
		if (timerRunning)
			this.timer.stop();
		if (paramDateFormat != null) {
			this.time = paramDateFormat;
		}
		this.time = DateFormat.getTimeInstance();
		updateTime();
		if (!(timerRunning))
			return;
		this.timer.start();
	}

	public void setTooltipFormat(DateFormat paramDateFormat) {
		boolean timerRunning = this.timer.isRunning();

		if (timerRunning)
			this.timer.stop();
		this.date = paramDateFormat;
		updateTime();
		if (!(timerRunning))
			return;
		this.timer.start();
	}

	public void stop() {
		if (this.timer == null)
			return;
		this.timer.stop();
	}

	public void start() {
		if (this.timer == null)
			return;
		this.timer.start();
	}

	public void addNotify() {
		super.addNotify();
		start();
	}

	public void removeNotify() {
		super.removeNotify();
		stop();
	}
}
