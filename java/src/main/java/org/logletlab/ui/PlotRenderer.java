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


package org.logletlab.ui;

import java.text.DecimalFormat;
import java.util.List;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.logletlab.LogletLabDataSet;
import org.logletlab.LogletLabDocument;
import org.logletlab.util.DPoint;
import org.logletlab.util.LogMath;

/**
 * @author J
 *
 * This class handles the rendering of a plot.  For a typical fit model,
 * create a subclass of PlotRenderer and implement the FitRenderer interface.
 */
public class PlotRenderer {
	Axis yAxis, tAxis;
	int plotSize=3;
	
	final static int MIN_TICK_INTERVAL = 25;
	final static int MAX_TICK_INTERVAL = 80;
	final static int TICK_LENGTH = 5;

	private int axisType;
	public static final int AXIS_LINEAR = 0;	// linear-linear
	public static final int AXIS_SEMILOG = 1;	// linear-log

	PlotRenderer() {
		tAxis = new Axis();
		yAxis = new Axis();
		axisType=AXIS_LINEAR;
	}
	
	public void drawData(Graphics g, Rectangle plotRect, LogletLabDataSet pts) {
		for (int i=0; i<pts.getNPoints(); i++ ) {
			DPoint pt = pts.getPoint(i);
			if (pt.isValid()) {
				g.setColor(pt.isExcluded?Color.red:pts.getColor());
				plot(g,plotRect,pt);
			}
		}
	}

	public void drawXAxis(Graphics g, Rectangle plotRect) {
		if (tAxis.getMin()>=tAxis.getMax()) return;

		int tickMultiplier[] = {1,2,5,10};
		int tickIndex = 0;
		// HORIZONTAL TICKS (x/t axis)
		double tickSize = getMagnitude(tAxis.getMax()-tAxis.getMin());
		//System.err.println("Init tick size: "+tickSize);
		int nTicks = Math.max(1,(int)((tAxis.getMax()-tAxis.getMin()) / tickSize));
		// Add ticks if intervals are too big
		// TODO: minimum tick interval based on StringBounds
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D tickBounds = fm.getStringBounds(String.valueOf(tAxis.getMax()),g);
		int min_interval = Math.max((int)tickBounds.getWidth(),MIN_TICK_INTERVAL);
		
		while (plotRect.width / nTicks > MAX_TICK_INTERVAL) {
			tickSize /= 10;
			nTicks = Math.max(1,(int)((tAxis.getMax()-tAxis.getMin()) / tickSize));
			//System.err.println("New tick size: "+tickSize);
		}
		// Remove ticks if intervals are too small. 
		while (plotRect.width / nTicks < min_interval) {
			tickIndex++;
			nTicks = Math.max(1,(int)((tAxis.getMax()-tAxis.getMin()) / (tickMultiplier[tickIndex]*tickSize)));
		}
		
		double t_tick = tAxis.getMin();
		double tickInterval = tickMultiplier[tickIndex]*tickSize;
		for (int i=0; tAxis.getMin()+i*tickInterval<tAxis.getMax(); i++) {
			t_tick = tAxis.getMin()+i*tickInterval;
			drawHorizontalAxisTickMark(g,plotRect,t_tick);
			drawHorizontalAxisTickMarkLabel(g,plotRect,t_tick);
		}
		drawHorizontalAxisTickMarkLabel(g,plotRect,tAxis.getMax());
	}
	
	public void drawYAxis(Graphics g, Rectangle plotRect){
		if (yAxis.getMin()>=yAxis.getMax()) return;
		
		if (getAxisType()==AXIS_LINEAR) {
			int tickMultiplier[] = {1,2,5,10};
			int tickIndex = 0;
			// VERTICAL (y axis)
			double tickSize = getMagnitude(yAxis.getMax()-yAxis.getMin());
			int nTicks = Math.max(1,(int)((yAxis.getMax()-yAxis.getMin()) / tickSize));
			// Add ticks if intervals are too big
			while (plotRect.height / nTicks > MAX_TICK_INTERVAL) {
				tickSize /= 10;
				nTicks = Math.max(1,(int)((yAxis.getMax()-yAxis.getMin()) / tickSize));
			}
			
			// Remove ticks if intervals are too small. 
			while (plotRect.height / nTicks < MIN_TICK_INTERVAL) {
				tickIndex++;
				nTicks = Math.max(1,(int)((yAxis.getMax()-yAxis.getMin()) / (tickMultiplier[tickIndex]*tickSize)));
			}
			
			double tickInterval = tickMultiplier[tickIndex]*tickSize;
			for (int i=0; yAxis.getMin()+i*tickInterval<yAxis.getMax(); i++) {
				double y_tick = yAxis.getMin()+i*tickInterval;//plotRect.height / nTicks;
		
				drawVerticalAxisTickMark(g,plotRect,y_tick);
				drawVerticalAxisTickMarkLabel(g,plotRect,y_tick);
			}
			drawVerticalAxisTickMarkLabel(g,plotRect,yAxis.getMax());
		} else if (getAxisType()==AXIS_SEMILOG) {
			//System.err.println(yAxis.getMin()+" -> "+yAxis.getMax());
			for (int i=(int)yAxis.getMin(); i<(int)yAxis.getMax(); i++) {
				for (int j=1; j<10; j++) {
					drawVerticalAxisTickMark(g,plotRect,j*Math.pow(10,i));
				}
			}
			for (int i=(int)yAxis.getMin(); i<(int)yAxis.getMax(); i++) {
				drawVerticalAxisTickMark(g,plotRect,Math.pow(10,i));
				drawVerticalAxisTickMarkLabel(g,plotRect,Math.pow(10,i));
			}
			drawVerticalAxisTickMarkLabel(g,plotRect,Math.pow(10,yAxis.getMax()));
		}
	}
	
	protected void drawHorizontalAxisTickMark(Graphics g, Rectangle r, double xPos) {
		// TOP
		g.drawLine(plotX(xPos,r), r.y, plotX(xPos,r), r.y+TICK_LENGTH);
		// BOTTOM
		g.drawLine(plotX(xPos,r), r.y+r.height,	plotX(xPos,r) ,r.y+r.height-TICK_LENGTH);
	}


	protected void drawHorizontalAxisTickMarkLabel(Graphics g, Rectangle plotRect, double value) {
		String label;
		if (getMagnitude(tAxis.getMax()-tAxis.getMin()) > 1.0) {
			// cast as an int
			label = String.valueOf((int)value);
		} else {
			DecimalFormat myFormatter = new DecimalFormat("#.##");
			label = myFormatter.format(value);
		}
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D tickBounds = fm.getStringBounds(label,g);
		g.drawString(label,
			plotX(value,plotRect)-(int)(tickBounds.getWidth()/2),
			plotRect.y+plotRect.height+(int)(tickBounds.getHeight()));
	}



	protected void drawVerticalAxisTickMark(Graphics g, Rectangle r, double yPos) {
		if (getAxisType()==AXIS_LINEAR) {
			// left
			g.drawLine(r.x, plotY(yPos,r), r.x+TICK_LENGTH, plotY(yPos,r));
			// right
			g.drawLine(r.x+r.width, plotY(yPos,r), 
				r.x+r.width-TICK_LENGTH, plotY(yPos,r));
			//plot.drawLine(HMARGIN+fHMARGIN+tAxis.Length,VMARGIN+yAxis.getAxisLength()-y_tick,
			//	HMARGIN+fHMARGIN+tAxis.Length-TICK_LENGTH,VMARGIN+yAxis.getAxisLength()-y_tick);
		} else {
			g.drawLine(r.x, plotY(yPos,r), r.x+TICK_LENGTH, plotY(yPos,r));
			g.drawLine(r.x+r.width, plotY(yPos,r), r.x+r.width-TICK_LENGTH, plotY(yPos,r));
			
		}
	}


	protected void drawVerticalAxisTickMarkLabel(Graphics g, Rectangle plotRect, double value) {
		String label;
		if (getMagnitude(yAxis.getMax()-yAxis.getMin()) > 1.0) {
			// cast as an int
			label = String.valueOf((int)value);
		} else {
			DecimalFormat myFormatter = new DecimalFormat("#.##");
			label = myFormatter.format(value);
		}
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D tickBounds = fm.getStringBounds(label,g);
		
		g.drawString(label,
			plotRect.x-(int)(tickBounds.getWidth()+3),
			plotY(value,plotRect)+(int)(tickBounds.getHeight()/2)-fm.getDescent());
	}

	public int getLeftMargin(Graphics g)
	{
		FontMetrics fm = g.getFontMetrics();
		// horizontal labels
		Rectangle2D tickBounds;
		if (getAxisType()==AXIS_LINEAR) {
			tickBounds = fm.getStringBounds(String.valueOf(yAxis.getMax()),g);
		} else {
			tickBounds = fm.getStringBounds(String.valueOf(Math.pow(10,yAxis.getMax())),g);
		}
		return (int)tickBounds.getWidth();
	}
	
	/**
	 * Default axis scaling algorithm.  Subclass may want to override or supplement this method
	 * to get custom bounds.
	 * @param doc
	 */
	public void setAxisScale(LogletLabDocument doc) {
		List<DPoint> pts = doc.getDataSet(0).getAllPoints();
		DPoint pt;

		if (pts != null) {
			pt = (DPoint) pts.get(0);
		} else {
			pt = new DPoint(0,0);
		}
		
		if (doc.isAutoXAxisOn()) {
			tAxis.setMin(pt.x);
			tAxis.setMax(pt.x);
		} else {
			tAxis.setMin(doc.getTMin());
			tAxis.setMax(doc.getTMax());
		}
		
		if (doc.isAutoYAxisOn()) {
			yAxis.setMin(pt.y);
			yAxis.setMax(pt.y);
		} else {
			yAxis.setMin(doc.getYMin());
			yAxis.setMax(doc.getYMax());
		}

		// FIND MIN/MAX FOR EACH AXIS
		for (int i=0; i<doc.getNSets(); i++) {
			//Vector myPts = (Vector) dataSet.data(i);
			checkBounds(doc.getDataSet(i),doc.isAutoXAxisOn(),doc.isAutoYAxisOn());
		}
		
		// RESCALE AXES
		// Take magnitude of range, and push axes out to nearest interval
		if (doc.isAutoXAxisOn()) {
			double tMag = getMagnitude(tAxis.getMax()-tAxis.getMin());
			if (tMag==0) tMag=1;
			tAxis.setMin(Math.floor(tAxis.getMin() /tMag) * tMag);
			tAxis.setMax(Math.ceil(tAxis.getMax() / tMag) * tMag);
			doc.setTMin(tAxis.getMin());
			doc.setTMax(tAxis.getMax());
		}

		if (doc.isAutoYAxisOn()) {
			if (doc.getAxisType()==AXIS_LINEAR) {
				double yMag;
				if (yAxis.getMin() > 0) {
					yMag = getMagnitude(yAxis.getMax()-yAxis.getMin());
				} else {
					// lower magnitude
					yMag = getMagnitude((yAxis.getMax()-yAxis.getMin())/5)*2;			
				}
				if (yMag==0) yMag=1;
	
				yAxis.setMin(Math.floor(yAxis.getMin() / yMag) * yMag);
				yAxis.setMax(Math.ceil(yAxis.getMax() / yMag) * yMag);
				doc.setYMin(yAxis.getMin());
				doc.setYMax(yAxis.getMax());
			} else if (doc.getAxisType()==AXIS_SEMILOG) {
				//System.err.println("Before log: "+yAxis.getMin()+" -> "+yAxis.getMax());
				double logMin = LogMath.log(yAxis.getMin(),10);
				double logMax = LogMath.log(yAxis.getMax(),10);
				yAxis.setMin(Math.floor(logMin));
				yAxis.setMax(Math.ceil(logMax));
				doc.setYMin(yAxis.getMin());
				doc.setYMax(yAxis.getMax());
			}
			setAxisType(doc.getAxisType());
		}
	}
	
	protected double getMagnitude(double d) {
		return Math.pow(10,Math.floor(Math.log(d)/Math.log(10))); // log_10_x = ln x / ln 10
	}
	
	protected void checkBounds(LogletLabDataSet dataset, boolean setT, boolean setY) {
		DPoint pt;
		List<DPoint> pts = dataset.getAllPoints();
		for (int i=0; i<pts.size(); i++ ) {
			pt = (DPoint)pts.get(i);
			if (pt.isValid()) {
				//if (model != null) {
					// TO DO: match params with model
					// -- need to get the right number of logistics
					//pt = model.transformPoint(pt,pts.getFitParameters(model));
				//}
				if (setT) {
					tAxis.setMin(Math.min(tAxis.getMin(),pt.x));
					tAxis.setMax(Math.max(tAxis.getMax(),pt.x));
				}
				if (setY) {
					yAxis.setMin(Math.min(yAxis.getMin(),pt.y));
					yAxis.setMax(Math.max(yAxis.getMax(),pt.y));
				} 
			}
		}
	}

	protected void plot(java.awt.Graphics g, Rectangle r, DPoint pt) {
		// System.err.println(pt.x+"/"+tAxis.getMax()+" : "+plotX(pt.x,r)+"\t"+pt.y+"/"+yAxis.getMax()+" : "+plotY(pt.y,r));
		g.drawOval(plotX(pt.x,r)-plotSize,plotY(pt.y,r)-plotSize,2*plotSize+1,2*plotSize+1);
	}

	protected int plotX(double t, Rectangle plot) {
		double xPlot = (t-tAxis.getMin()) * plot.width / (tAxis.getMax()-tAxis.getMin());
		return (int)xPlot+plot.x;
	}

	protected int plotY(double y, Rectangle plot) {
		double yPlot;
		if (getAxisType()==AXIS_LINEAR) {
			yPlot = (y-yAxis.getMin())* plot.height / (yAxis.getMax()-yAxis.getMin());
		} else {
			yPlot = (LogMath.log(y,10)-yAxis.getMin()) * plot.height / (yAxis.getMax()-yAxis.getMin());
		}
		 
		return plot.y+plot.height-(int)(yPlot);
	}

	/**
	 * @return
	 */
	public int getAxisType() {
		return axisType;
	}

	/**
	 * @param i
	 */
	public void setAxisType(int i) {
		axisType = i;
	}

}
