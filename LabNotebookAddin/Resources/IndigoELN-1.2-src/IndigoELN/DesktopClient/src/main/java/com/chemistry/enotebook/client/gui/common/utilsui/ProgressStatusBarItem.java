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

public class ProgressStatusBarItem extends JPanel {
	private static final long serialVersionUID = 4194853287632423203L;
	private JProgressBar progressBar;
	private JLabel label;
	private AbstractButton cancelButton;

	private CancelCallback cancelCallback;

	public ProgressStatusBarItem() {
		label = new JLabel("");
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(false);

		this.setLayout(new BorderLayout(3, 0));

		this.add(label, BorderLayout.LINE_START);
		this.add(progressBar, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		cancelButton = createCancelButton();
	}

	public void setName(String name) {
		label.setText(name);
	}

	public String getName() {
		return label.getText();
	}

	public void setProgressStatus(String status) {
		label.setText(status);
	}

	public void setCancelCallback(CancelCallback cancelCallback) {
		this.cancelCallback = cancelCallback;
	}

	public CancelCallback getCancelCallback() {
		return this.cancelCallback;
	}

	public AbstractButton getCancelButton() {
		return cancelButton;
	}

	public void setProgress(int count) {
		progressBar.setValue(count);
	}

	public void setToolTipText(String toolTipText) {
		super.setToolTipText(toolTipText);
	}

	public void setStatus(final String status) {
		System.out.println("ProgressStatusBarItem setStatus() - " + status);
		if (SwingUtilities.isEventDispatchThread()) {
			label.setText(status);
		}
		Runnable runnable = new Runnable() {
			public void run() {
				label.setText(status);
			}
		};
		SwingUtilities.invokeLater(runnable);
	}

	public static abstract interface CancelCallback {
		public abstract void cancelPerformed();
	}

	private AbstractButton createCancelButton() {
		JButton localNullButton = new JButton("Cancel");
		localNullButton.setOpaque(false);
		localNullButton.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
		localNullButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				if ((ProgressStatusBarItem.this.getCancelCallback() == null))
					return;
				ProgressStatusBarItem.this.getCancelCallback()
						.cancelPerformed();
			}
		});

		removeFocus(localNullButton);

		return localNullButton;
	}

	public static void removeFocus(JComponent paramJComponent) {
		paramJComponent.setRequestFocusEnabled(false);
		paramJComponent.setFocusable(false);
	}
}
