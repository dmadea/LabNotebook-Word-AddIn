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
package com.chemistry.viewer;

import com.chemistry.ChemistryEditorEvent;
import com.chemistry.ChemistryEditorListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Arrays;

public class ReactionViewer
	extends ChemistryViewer
//	implements ActionListener//, StructureEditorTypeChangeListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8242241592482299443L;
	public static final int VIEW_NATIVE = 1;
	public static final int VIEW_PARSED = 2;
	
	private int preferredViewMode = 0;
	
	private byte[] nativeSketch = null;
	private byte[] parsedSketch = null;
	
	
	public ReactionViewer(String title, String content, int viewMode)
		throws Exception 
	{
		super(title, content);
		
		setPreferredBackgroundColor(Color.CYAN);
		preferredViewMode = viewMode;
		updateCurrentView();
		
		chemEditor.addChemistryEditorListener(new ChemistryEditorListener() {
		    public void editingStarted(ChemistryEditorEvent e) { }
			public void structureChanged(ChemistryEditorEvent e) {
				byte[] currChem = getChemistry();

				if (preferredViewMode == VIEW_NATIVE)
					nativeSketch = currChem;
				else if (preferredViewMode == VIEW_PARSED)
					parsedSketch = currChem;
				
				if (currChem != null && currChem.length > 0)
					setPreferredBackgroundColor(Color.WHITE);
				else
					setPreferredBackgroundColor(Color.CYAN);
				
//				System.out.println(new String(getChemistry("MDL Rxnfile")));
			}
			public void editingStopped(ChemistryEditorEvent e) { }
		});
	}
	
	public void paintComponent(Graphics g) 
	{
		Graphics2D g2 = (Graphics2D) g;
		AffineTransform oldTransform = g2.getTransform();
		super.paintComponent(g);
		g2.setTransform(oldTransform);

		boolean dataAvailable = false;
		if (preferredViewMode == VIEW_NATIVE && nativeSketch != null && nativeSketch.length > 0) dataAvailable = true;
		if (preferredViewMode == VIEW_PARSED && parsedSketch != null && parsedSketch.length > 0) dataAvailable = true;
		
		if (super.isReadOnly() || dataAvailable) 
			return;

		String str;
			
		Font f_arial_12 = new Font("Arial", Font.BOLD, 12);

		g2.setPaint(Color.GRAY);
		g2.setFont(f_arial_12);
		FontMetrics fm12 = getFontMetrics(f_arial_12);
		int top_pos = (getHeight() - (4 * fm12.getHeight())) / 2;

		str = "Double click anywhere";
		g2.drawString(str, (getWidth() - fm12.stringWidth(str)) / 2, top_pos);
		top_pos += fm12.getHeight();

		str = "within this area to draw";
		int largest_text_width = fm12.stringWidth(str);
		g2.drawString(str, (getWidth() - largest_text_width) / 2, top_pos);
		top_pos += fm12.getHeight();

		str = "Reaction Scheme";
		g2.drawString(str, (getWidth() - fm12.stringWidth(str)) / 2, top_pos);
		top_pos += fm12.getHeight();

		g2.setPaint(Color.BLUE);
		final BasicStroke wideStroke = new BasicStroke(5.0f);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke( wideStroke );
		largest_text_width *= 1.3;
		int lineStart = (getWidth() - largest_text_width) / 2;
		g2.draw(new Line2D.Double(lineStart, top_pos, lineStart + largest_text_width, top_pos));	// ------

		lineStart = lineStart + largest_text_width - 20;
		g2.draw(new Line2D.Double(lineStart, top_pos - 10, lineStart + 20, top_pos));    			// \
		g2.draw(new Line2D.Double(lineStart, top_pos + 10, lineStart + 20, top_pos));    			// /
	}
	
	public void setReadOnly(boolean flag) 
	{
		super.setReadOnly(flag);

		boolean haveData = false;
		if (preferredViewMode == VIEW_NATIVE)
			haveData = (nativeSketch != null && nativeSketch.length > 0);
		else if (preferredViewMode == VIEW_PARSED)
			haveData = (parsedSketch != null && parsedSketch.length > 0);
		setPreferredBackgroundColor((haveData || flag) ? Color.WHITE : Color.CYAN);
		
		this.repaint();
	}

	public int getPreferredViewMode()  { return preferredViewMode; }
	public void setPreferredViewMode(int viewMode) 
	{
		if (preferredViewMode != viewMode) {
			preferredViewMode = viewMode;
			updateCurrentView();
		}
	}

	private void updateCurrentView() 
	{
		boolean haveData = false;
		
		if (preferredViewMode == VIEW_NATIVE) {
			setChemistry(nativeSketch);
			haveData = (nativeSketch != null && nativeSketch.length > 0);
		} else if (preferredViewMode == VIEW_PARSED) {
			setChemistry(parsedSketch);
			haveData = (parsedSketch != null && parsedSketch.length > 0);
		}
		
		setPreferredBackgroundColor((haveData || isReadOnly()) ? Color.WHITE : Color.CYAN);
	}
	

	public byte[] getNativeSketch() { return nativeSketch; }
	public void setNativeSketch(byte[] image)
	{
		if (Arrays.equals(nativeSketch, image)) 
			return;

		if (image == null) image = new byte[0];
	    nativeSketch = image;
	    
	    updateCurrentView();
	}
	
	public byte[] getParsedSketch() { return parsedSketch; }
	public void setParsedSketch(byte[] image)
	{
		if (image == null) image = new byte[0];
		parsedSketch = image;

	    updateCurrentView();
	}

	
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Test ReactionViewer") {
    	    /**
			 * 
			 */
			private static final long serialVersionUID = 4610029086504416862L;

			protected void processWindowEvent(WindowEvent e){
    	        super.processWindowEvent(e);

    	        if (e.getID() == WindowEvent.WINDOW_CLOSING)
    	            System.exit(0);
    	    }
    	};
    	
    	frame.setBounds(300, 300, 500, 300);
		frame.getContentPane().setLayout(new BorderLayout());

    	ReactionViewer panel=null;
    	try {
    		panel = new ReactionViewer(frame.getTitle(), "", ReactionViewer.VIEW_NATIVE);    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	panel.setEditorType(ReactionViewer.ISISDRAW_EDITOR);
    	
    	final ReactionViewer rvp = panel;
    	JPanel p = new JPanel();

    	JButton b = new JButton("View Native");
		p.add(b);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				rvp.setPreferredViewMode(ReactionViewer.VIEW_NATIVE);
				//rvp.setReadOnly(true);
			}
		});
		
		b = new JButton("View Parsed");
		p.add(b);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				rvp.setPreferredViewMode(ReactionViewer.VIEW_PARSED);
			}
		});
		
		b = new JButton("Toggle ReadOnly");
		p.add(b);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				rvp.setReadOnly(!rvp.isReadOnly());
			}
		});

		b = new JButton("Image Props");
		p.add(b);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
//				ImageData id = new ImageData(null, null);
//				id.setShowHydrogens(new Boolean(true));
//				id.setShowReactionMapNumbers(new Boolean(true));
//				id.setWidth(new Integer(rvp.getWidth()));
//				id.setHeight(new Integer(rvp.getHeight()));
//				rvp.setWriteOptions(id);
			}
		});

		frame.getContentPane().add(p, BorderLayout.NORTH);
		
    	if (panel != null) frame.getContentPane().add(panel, BorderLayout.CENTER);
    	    	
    	frame.setVisible(true);
	}
}
