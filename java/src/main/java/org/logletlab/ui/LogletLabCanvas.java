/*
 * Loglet Lab 2.0
 * 
 * Copyright (c) 2003, Program for the Human Environment, The Rockefeller University,
 * except where noted. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of The Program for the Human Environment nor The Rockefeller
 *    University nor the names of its contributors may be used to endorse or 
 *    promote products derived from this software without specific prior
 *    written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 * 
 */
/** 
 * LogletLabCanvas.java
 *
 * Description:		The main display area for Loglet Lab, where data are plotted and fits drawn.
 * @author			J
 * @version			
 */

package org.logletlab.ui;


import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;

import org.logletlab.LogletLabDataSet;
import org.logletlab.LogletLabDocument;
import org.logletlab.model.AbstractFitModel;

public class LogletLabCanvas extends javax.swing.JPanel
	implements Printable
{
	// graph offsets
	static int HMARGIN = 20;
	static int VMARGIN = 20;
	final static int DEFAULT_HMARGIN = 20;
	final static int DEFAULT_VMARGIN = 20;
	final static int RES_HEIGHT = 75;
	final static int TICK_LENGTH = 5;

	static final double NINETY_DEGREES = Math.toRadians(90.0);

	private LogletLabFrame parent;
	//static AbstractFitModel afm; // "Presiding" fit model

	// AXIS
	//private Axis tAxis, yAxis;
	private double resMax;

	private boolean isDisplay=true;
		
	// PLOT
	private int plotSize = 2;
	
	//public Color[] plotColors = { Color.BLACK, new Color(0,0,255), new Color(0,153,0), new Color(153,0,0) };
	private Dimension dims = getSize();
	
	public void paintComponent (Graphics g) {
		if (isDisplay)
			dims = getSize();
 		
 	    plot(g,dims,parent.getDoc(),parent.currentFit);
	    
		if (!isDisplay)	{
			// Add credits.
			drawCredits(g,dims);
		}

	    /*
		((Graphics2D)g).rotate(-NINETY_DEGREES);
		g.drawString(String.valueOf(tMin),HMARGIN,VMARGIN+yAxis.getAxisLength()+15);
		g.drawString(String.valueOf(tMax),HMARGIN+tAxis.Length-5,VMARGIN+yAxis.getAxisLength()+15);
		g.drawString(String.valueOf(yAxis.getMin()),HMARGIN,VMARGIN+yAxis.getAxisLength()+5);
		g.drawString(String.valueOf(yMax),HMARGIN,VMARGIN+5);
		((Graphics2D)g).rotate(NINETY_DEGREES);
		*/
		
	}
	
	public static void plot(Graphics g, Dimension d, LogletLabDocument doc, AbstractFitModel afm) {
		Rectangle plotRect = new Rectangle();
		Rectangle residualsRect = new Rectangle();

		g.setColor(Color.white);
		g.fillRect(0,0,d.width,d.height);
		// Use exception handling instead? (NullPointer and ArrayOutofBounds?
		if (doc != null && doc.getNSets() > 0 && doc.getDataSet(0).getNPoints() >= 2) {
			// TO DO: check all of the fits for behavior.
			PlotRenderer renderer;
			if (afm == null) {
				renderer = new PlotRenderer();
			} else {
				renderer = afm.createRenderer();
			}
			
			//System.err.print("Setting scale...");
			// FIND MIN/MAX FOR EACH AXIS
			renderer.setAxisScale(doc);
			//System.err.println("scale set.");
			
			FontMetrics fm = g.getFontMetrics();
			// horizontal labels
			Rectangle2D tickBounds = fm.getStringBounds(String.valueOf(renderer.tAxis.getMax()),g);
			// INCREASE BOTTOM MARGIN TO FIT T-AXIS LABELS
			int bottomMargin = (int)tickBounds.getHeight();//Math.max(fVMARGIN, tickBounds.getHeight());
			// INCREASE RIGHT MARGIN TO FIT T-AXIS LABELS
			int rightMargin = (int)(tickBounds.getWidth()/2 + tickBounds.getHeight());//Math.max(fVMARGIN, tickBounds.getWidth());
			// INCREASE LEFT MARGIN TO FIT Y-AXIS LABELS
			int leftMargin = renderer.getLeftMargin(g);//(int)tickBounds.getWidth();//Math.max(fHMARGIN, tickBounds.getWidth());

			// INCREASE TOP MARGIN TO FIT TITLE.
			Font oldFont = g.getFont();
			// Create larger font at 125% normal font.
			g.setFont(oldFont.deriveFont(Font.BOLD,1.25f*oldFont.getSize()));
			fm = g.getFontMetrics();
			Rectangle2D labelBounds = fm.getStringBounds(doc.getTitle(),g);			
			int topMargin = (int)labelBounds.getHeight();

			// SET AXIS LENGTHS
			int tAxisLength = d.width-2*HMARGIN-leftMargin-rightMargin;
			int yAxisLength = d.height-2*VMARGIN-bottomMargin-topMargin;
			
			// System.err.println("Plotting residuals...");
			int resAxisLength=0;
			if (afm != null && afm.getMode() != AbstractFitModel.MODE_UNFITTED &&
				afm.getResidualMode() != AbstractFitModel.RESIDUALS_OFF)
			{
				resAxisLength = RES_HEIGHT; //*doc.getNSets();
				yAxisLength = yAxisLength-resAxisLength;
				/*
				if (afm.getResidualMode() == AbstractFitModel.RESIDUALS_ABS) {
					tAxis.Length -= fm.getStringBounds(String.valueOf(-(int)resMax),g).getWidth();
				} else if (afm.getResidualMode() == AbstractFitModel.RESIDUALS_REL) {
					tAxis.Length -= fm.getStringBounds("-100%",g).getWidth();
				}
				*/
			}

			// Create a clipped rectangle contexts,
			// so that we only draw within the box.
			plotRect = new Rectangle(HMARGIN+leftMargin,VMARGIN+topMargin,
												tAxisLength,yAxisLength);
			residualsRect = new Rectangle(HMARGIN+leftMargin,VMARGIN+topMargin+yAxisLength,
												tAxisLength,resAxisLength);
			
			// System.err.println("Drawing labels...");
			
			// draw the axes.
			g.setColor(Color.black);
			g.drawRect(plotRect.x,plotRect.y,
						plotRect.width,plotRect.height);
			g.drawRect(residualsRect.x,residualsRect.y,
						residualsRect.width,residualsRect.height);

			// draw title.
			g.drawString(doc.getTitle(), plotRect.x+(plotRect.width/2)-(int)(labelBounds.getWidth()/2), VMARGIN);

			// draw axis labels
			g.setFont(oldFont.deriveFont(Font.BOLD));
			fm = g.getFontMetrics();
			// VERTICAL (y-axis)
			labelBounds = fm.getStringBounds(doc.getRangeLabel(),g);
			drawVerticalText(g, doc.getRangeLabel(), plotRect.x-leftMargin, plotRect.y+(plotRect.height/2));
			// HORIZONTAL (x/t-axis)
			labelBounds = fm.getStringBounds(doc.getDomainLabel(),g);
			g.drawString(doc.getDomainLabel(), plotRect.x+plotRect.width/2-(int)(labelBounds.getWidth()/2), residualsRect.y+residualsRect.height-fm.getDescent()+bottomMargin*2);
			
						
			// RESET FONT INFO.
			g.setFont(oldFont);
			fm = g.getFontMetrics();
			
			Graphics plot = g.create();
			plot.setClip(plotRect);
			
			// DRAW AXES
			plot.setColor(Color.black);
			renderer.drawXAxis(g,plotRect);
			renderer.drawYAxis(g,plotRect);
			
			// Draw the points, fit lines.
			// Iterate through data sets.
			for (int i=0; i<doc.getNSets(); i++) {
				LogletLabDataSet pts = doc.getDataSet(i);
				
				plot.setColor(pts.getColor());
				if (renderer instanceof FitRenderer) {
					// Draw fits
					if (pts.getFitParameters(afm) != null) {
						((FitRenderer)renderer).drawFitLine(pts.getFitParameters(afm), 
							renderer.tAxis.getMin(), renderer.tAxis.getMax(), plot, plotRect);
					}
					
					// Draw residuals
					if (afm.getResidualMode() != AbstractFitModel.RESIDUALS_OFF) {
						((FitRenderer)renderer).drawResiduals(pts, g, residualsRect);
					}
				}
				// MAYBE DO: Match fit parameters with fit model (OK for now b/c only one model) ?
				
//				for (int i=0; i<doc.getNFits(); i++) {
//					AbstractFitModel myAFM = (AbstractFitModel) doc.getFit(i);
//					for (java.util.Enumeration enum=pts.fits.keys(); enum.hasMoreElements(); ) {
//						AbstractFitParameters myFitParams = pts.getFitParameters((AbstractFitModel)  enum.nextElement());
//						//if (afm.getMode() != AbstractFitModel.MODE_UNFITTED) {
//						if (myAFM.getFitName().equals(myFitParams.fitName)) {
//							drawFitLine(g, myAFM.getFitLine(myFitParams, tAxis.getMin(),tAxis.getMax()));
//						}
//					}
//				}
				renderer.drawData(plot, plotRect, pts);
			}
			plot.dispose();
		    
			// draw the axes.
			g.setColor(Color.black);
			g.drawRect(plotRect.x,plotRect.y,
						plotRect.width,plotRect.height);
			g.drawRect(residualsRect.x,residualsRect.y,
						residualsRect.width,residualsRect.height);

		} else {
			String noData = "Not enough data.";
			FontMetrics fm = g.getFontMetrics();
			// horizontal labels
			Rectangle2D labelBounds = fm.getStringBounds(noData,g);
			g.setColor(Color.black);
			g.drawString(noData,d.width/2-(int)labelBounds.getWidth()/2,d.height/2);
		}
	}

	public static void drawVerticalText(Graphics g, String s, int x, int y) {
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D strBounds = fm.getStringBounds(s,g);

		g.translate(x,y);
		((Graphics2D)g).rotate(-NINETY_DEGREES);
		// centered text
		g.drawString(s,-(int)strBounds.getWidth()/2,-fm.getDescent());
		((Graphics2D)g).rotate(NINETY_DEGREES);
		g.translate(-x,-y);
	}
	
	public int print(Graphics g, PageFormat pf, int pi)
							  throws PrinterException {
		if (pi >= 1) {
			return Printable.NO_SUCH_PAGE;
		}
		// Allow override of the graph size.
		setDisplay(false);
		// Resize to the paper (accounts for orientation)
		setDims(new Dimension((int)pf.getImageableWidth(),(int)pf.getImageableHeight()));
		// Translate to the margin (accounts for orientation)
		g.translate((int)pf.getImageableX(),(int)pf.getImageableY());
		// Paint it!
		paintComponent(g);
		// Deactivate override of graph size.
		setDisplay(true);

		return Printable.PAGE_EXISTS;
		//return Printable.NO_SUCH_PAGE;
	}

	public void createExportImage(BufferedImage bi)
	{
		Graphics2D g = bi.createGraphics();
		this.setDisplay(false);
		this.setDims(new Dimension(bi.getWidth(), bi.getHeight()));
		// fill background
		g.setColor(Color.white);
		g.fill(new Rectangle(0,0,bi.getWidth(),bi.getHeight()));
		this.paintComponent(g);
		this.setDisplay(true);
	}
	
	public static void drawCredits(Graphics g, Dimension d) {
		String credits = "Created with Loglet Lab";
		String url = "http://phe.rockefeller.edu/LogletLab";
		
		Font oldFont = g.getFont();
		// Create larger font at 125% normal font.
		g.setFont(oldFont.deriveFont(Font.BOLD,0.8f*oldFont.getSize()));

		FontMetrics fm = g.getFontMetrics();
		Rectangle2D creditBounds = fm.getStringBounds(credits,g);
		Rectangle2D urlBounds = fm.getStringBounds(url,g);
		int creditWidth = (int)Math.max(creditBounds.getWidth(),urlBounds.getWidth());
		drawVerticalText(g,credits,d.width-(int)creditBounds.getHeight(),d.height-(int)creditBounds.getWidth()/2);
		drawVerticalText(g,url    ,d.width                         ,d.height-(int)urlBounds.getWidth()/2);
		
		g.setFont(oldFont);
	}
	
	public void setParent (LogletLabFrame f) {
		parent = f;
	}
	
	public void setMargins(int x, int y)
	{
		HMARGIN = x;
		VMARGIN = y;
	}
	/**
	 * @return the Dimensions of the paint context
	 */
	public Dimension getDims() {
		return dims;
	}

	/**
	 * @param dimension
	 */
	public void setDims(Dimension dimension) {
		dims = dimension;
	}

	/**
	 * @return
	 */
	public boolean isDisplay() {
		return isDisplay;
	}

	/**
	 * @param b
	 */
	public void setDisplay(boolean b) {
		isDisplay = b;
	}

	public LogletLabCanvas() {
//		tAxis = new Axis();
//		yAxis = new Axis();
	}



}


/* @(#)LogletLabCanvas.java */
