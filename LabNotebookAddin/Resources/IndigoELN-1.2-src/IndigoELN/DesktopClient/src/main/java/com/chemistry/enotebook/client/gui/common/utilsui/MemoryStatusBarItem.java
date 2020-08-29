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
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MemoryStatusBarItem extends JPanel {
	private static final long serialVersionUID = -4974871925388643126L;

	private final Color foregroundColor = new Color(236, 233, 176);
	private final Color backgroundColor = new Color(240, 240, 240);
	private Timer timer;
	private int delay;
	private JProgressBar progressBar;
	
	public MemoryStatusBarItem() {
		this.delay = 1000;
		this.setPreferredSize(new Dimension(200, 30));
		this.setBounds(new Rectangle(200, 30));

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		progressBar.setUI(new BasicProgressBarUI() {
			protected Color getSelectionBackground() {
				return Color.black;
			}

			protected Color getSelectionForeground() {
				return Color.black;
			}
		});

		progressBar.setBackground(backgroundColor);
		progressBar.setForeground(foregroundColor);
		progressBar.setSize(200, 20);

		this.setLayout(new BorderLayout(3, 0));
		this.add(progressBar, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		this.timer = new Timer(this.delay, new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				Runtime localRuntime = Runtime.getRuntime();
				long freeMemory = localRuntime.freeMemory();
				long totalMemory = localRuntime.totalMemory();
				String str = Long.toString((totalMemory - freeMemory) / 1048576L)
						+ "M of " + Long.toString(totalMemory / 1048576L) + 'M';

				progressBar.setValue((int) ((1.0 * totalMemory - freeMemory) / totalMemory * 100));
				progressBar.setString(str);
			}
		});
		this.timer.start();
	}

	public void setUpdateInterval(int paramInt) {
		this.timer.stop();
		this.delay = paramInt;
		this.timer.setDelay(paramInt);
		this.timer.start();
	}

	public void stop() {
		this.timer.stop();
	}
}
