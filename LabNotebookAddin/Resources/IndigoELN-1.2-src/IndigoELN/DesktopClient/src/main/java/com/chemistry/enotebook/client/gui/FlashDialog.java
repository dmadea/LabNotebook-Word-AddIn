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
package com.chemistry.enotebook.client.gui;

import com.chemistry.enotebook.utils.CeNDialog;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class FlashDialog extends CeNDialog implements HyperlinkListener {

	private static final long serialVersionUID = -8253881487418999303L;

	protected static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	JPanel panel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JScrollPane scroll = new JScrollPane();
	JPanel jPanel1 = new JPanel();
	JButton closeBtn = new JButton();
	JEditorPane flashEditoryPane = new JEditorPane();

	public static FlashDialog showNewsFlash(JFrame aFrame) {
		FlashDialog flashDialog = new FlashDialog(aFrame);
		Dimension fdSize = flashDialog.getPreferredSize();
		//flashDialog.setLocation((screenSize.width - fdSize.width) / 2, 10);
		flashDialog.setLocation((screenSize.width - fdSize.width) / 2, (screenSize.height - fdSize.height) / 2);		
		flashDialog.setVisible(true);
		return flashDialog;
	}

	public FlashDialog(JFrame frame) {
		super(frame, "Important Message from CeN Dev team!", false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		try {
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		panel1.setLayout(borderLayout1);
		closeBtn.setText("Close");
		closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		flashEditoryPane.setEditable(false);
		flashEditoryPane.setPage("http://com/llfetch/livelink.exe?func=pfpublic.fetch&nodeid=43849745");
		panel1.setMinimumSize(new Dimension(600, 200));
		panel1.setPreferredSize(new Dimension(600, 200));
		getContentPane().add(panel1);
		panel1.add(scroll, BorderLayout.CENTER);
		scroll.getViewport().add(flashEditoryPane, null);
		this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
		jPanel1.add(closeBtn, null);
		getRootPane().setDefaultButton(closeBtn);
		flashEditoryPane.addHyperlinkListener(this);
	}

	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {

				String[] sargs = { "cmd", "/c", "start", "iexplore", e.getURL().toString() };
				execDosCommand(sargs, false);

			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private String execDosCommand(String[] cmdArray, boolean bWait) {
		// Run the OS command and wait for its thread
		// String cmd = "dir " + exeName + "/S/P";
		StringBuffer result = new StringBuffer(32);
		Process pr = null;
		BufferedReader ins = null;

		try {
			pr = Runtime.getRuntime().exec(cmdArray);

			if (bWait) {
				pr.waitFor();

				// Buffer the process's input stream, get data by lines
				ins = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				String output;

				while ((output = ins.readLine()) != null) {
					result.append(output);
				}
			}
		} catch (InterruptedException ignored) { // waitFor()
		} catch (IOException e) { // readLine(), close()
			//log.error(Settings.class, e);
		} finally {
			try {
				if (ins != null)
					ins.close();
			} catch (IOException e1) {
				//log.error(Settings.class, e1);
			}
			if (bWait) {
				pr.destroy();
			}
		}

		return result.toString();

	}
}