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

import java.awt.*;

import org.logletlab.LogletLabDataSet;
import org.logletlab.LogletLabDocument;
import org.logletlab.model.AbstractFitModel;
import org.logletlab.model.AbstractFitParameters;
import org.logletlab.model.LogSubModel;
import org.logletlab.model.LogSubParameters;
import org.logletlab.util.DPoint;


public class LogSubRenderer extends PlotRenderer 
	implements FitRenderer
{
	protected AbstractFitModel model;
	public LogSubRenderer(LogSubModel lsm) {
		setModel(lsm);
	}

	public AbstractFitModel getModel() {
		return model;
	}
	public void setModel(AbstractFitModel afm) {
		model = afm;
	}

	public void drawData(Graphics g, Rectangle plotRect, LogletLabDataSet pts) {
		LogSubParameters params = (LogSubParameters) pts.getFitParameters(model);
	
		for (int i=0; i<pts.getNPoints(); i++ ) {
			DPoint pt = pts.getPoint(i);
			if (pt.isValid()) {
				g.setColor(pt.isExcluded?Color.red:pts.getColor());
				double yFractional = pt.y / ((LogSubModel)model).getTotalMarket().getPoint(pt.x).y;
				if (yFractional > 0){
					double normalized = yFractional / Math.abs(1-yFractional);
					yFractional = Math.log(normalized)/Math.log(10);
					plot(g,plotRect,new DPoint(pt.x, yFractional));
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see LogletLab.FitRenderer#drawFitLine(LogletLab.AbstractFitParameters, double, double, java.awt.Graphics, java.awt.Rectangle)
	 */
	public void drawFitLine(AbstractFitParameters p, double t0, double t1, Graphics g, Rectangle r) {
		/* Populating the fit columns.
		 * Identify the saturating technology, calculate the total combined share of
		 * the other technologies, and subtract this from 1 to get the share
		 * of the the saturated technology.
		 * This means that the fit lines can't be rendered at the data set level--
		 * it must be done all at once.
		 */
		LogSubModel lsm = (LogSubModel)model;
		if (lsm.getParams()==null) return;
		
		int logSubFitPts = nFitPts*2;
		t0 = tAxis.getMin(); //lsm.xPts[1];
		t1 = tAxis.getMax(); //lsm.xPts[lsm.xPts.length-1];
		double tInterval = (t1-t0)/logSubFitPts;
		int saturated = 0;
		// Make sure there are defined parameters.
		while (saturated < lsm.getParams().length && lsm.getParams()[saturated] == null)
			saturated++;
			
		// Skip ahead to the first non-declining data set.
		while (saturated < lsm.getParams().length && lsm.getParams()[saturated].dt[0] < 0 && t0 >= lsm.getParams()[saturated].saturationPoint) {
			saturated++;
		}
		if (saturated >= lsm.getParams().length) return;
		
		double y[][] = new double[lsm.getParams().length][2];
		for (int i=1; i<=logSubFitPts; i++) {
			double t_u = t0 + (i-1)*tInterval;
			double t_v = t0 + i*tInterval;
			// CHECK IF NEW TECHNOLOGY IS SATURATED.
			while (saturated < lsm.getParams().length-1 && t_u > lsm.getParams()[saturated].saturationPoint) {
				saturated++;
				// System.err.println("drawFitLine(): t="+t_u+", saturated tech is now #"+saturated);
			}
			
			//System.err.println("drawFitLine(): t="+t_u+", saturated tech is #"+saturated);
			// MAKE ESTIMATED SHARE FOR NON-SATURATED SETS.
			double yFit;
			double nonSaturatedShare_u = 0., nonSaturatedShare_v = 0.;
			for (int j=0; j<lsm.getParams().length; j++) {
				if (j != saturated) {
					// copy old point from last iteration.
					// if first iteration and pre-saturation, take ascending parameters.
					// if first iteration and post-saturation, take descending parameters.
					if (i > 1) {
						y[j][0] = y[j][1];
					} else if (t_u < lsm.getParams()[j].saturationPoint) {
						// pre-saturation
						y[j][0] = 1.0 / (1.0+Math.exp( -lsm.getParams()[j].dt[0]*t_u - lsm.getParams()[j].tm[0] ));
					} else {
						// post-saturation
						y[j][0] = 1.0 / (1.0+Math.exp( -lsm.getParams()[j].dt[1]*t_u - lsm.getParams()[j].tm[1] ));
					}
					// Cast off negative shares, as well as small positive shares.
					if (y[j][0] > 0) {
						nonSaturatedShare_u += y[j][0];
					}

					// Calculate new point.
					if (t_u < lsm.getParams()[j].saturationPoint) {
						y[j][1] = 1.0 / (1.0+Math.exp( -lsm.getParams()[j].dt[0]*t_v - lsm.getParams()[j].tm[0] ));
					} else {
						y[j][1] = 1.0 / (1.0+Math.exp( -lsm.getParams()[j].dt[1]*t_v - lsm.getParams()[j].tm[1] ));
						
						//System.err.println("drawFitLine(): t="+t_u+", dt["+j+"]="+lsm.params[j].dt[1]+", tm["+j+"]="+lsm.params[j].tm[1]);
						//System.err.println("drawFitLine(): t="+t_u+", y["+j+"]="+y[j][1]);
						//System.err.println("-Tech #"+j+" is saturated, y="+y[j][1]);
					}
					//System.err.println("drawFitLine(): t="+t_u+", y["+j+"]="+y[j][1]);
					// calculate total non saturated market share.
					if (y[j][1] > 0) {
						nonSaturatedShare_v += y[j][1];
					}
				}
			}
			// REMAINING SHARE FOR SATURATED TECH.
			y[saturated][0] = 1-nonSaturatedShare_u;
			y[saturated][1] = 1-nonSaturatedShare_v;
			// System.err.println("drawFitLine(): sat="+saturated+", y[j]="+y[saturated][1]);

			// RENDER LINE
			for (int j=0; j<lsm.getParams().length; j++) {
				// Set color
				g.setColor(lsm.getDoc().getDataSet(j).getColor());
				double y_u, y_v;
				y_u = Math.log(y[j][0]/(1-y[j][0]))/Math.log(10);
				y_v = Math.log(y[j][1]/(1-y[j][1] ))/Math.log(10);
				g.drawLine(plotX(t_u,r),plotY(y_u,r),plotX(t_v,r),plotY(y_v,r));
			}
			
		}
	}

	/* (non-Javadoc)
	 * @see LogletLab.FitRenderer#drawResiduals(LogletLab.LogletLabDataSet, java.awt.Graphics, java.awt.Rectangle)
	 */
	public void drawResiduals(LogletLabDataSet pts, Graphics rg, Rectangle rect) {
		// not applicable to this model?		
	}

	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitModelRenderer#drawYAxis()
	 */
	public void drawYAxis(Graphics g, Rectangle plotRect) {
		// 5 tick marks
		int nIntervals = 4;
		String[] labels = {"1%","10%","50%","90%","99%"};
		for (int i=0; i<=nIntervals; i++) {
			int y_tick = i*plotRect.height / nIntervals;
			
			drawVerticalAxisTickMark(g,plotRect,y_tick);
			// custom label
			FontMetrics fm = g.getFontMetrics();
			java.awt.geom.Rectangle2D tickBounds = fm.getStringBounds(labels[i],g);
	
			g.drawString(labels[i],
				plotRect.x-(int)(tickBounds.getWidth()+3),
				plotRect.y+plotRect.height-y_tick+(int)(tickBounds.getHeight()/2)-fm.getDescent());

		}
	}
	/**
	 * LogSubRender doesn't use SEMILOG, so only plot points as is.
	 */
	protected int plotY(double y, Rectangle plot) {
		double yPlot;
		yPlot = (y-yAxis.getMin())* plot.height / (yAxis.getMax()-yAxis.getMin());
		 
		return plot.y+plot.height-(int)(yPlot);
	}

	public int getLeftMargin(Graphics g)
	{
		FontMetrics fm = g.getFontMetrics();
		// horizontal labels
		java.awt.geom.Rectangle2D tickBounds = fm.getStringBounds("999%",g); // extra "9" is for space
		return (int)tickBounds.getWidth()+3;
	}

	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitModelRenderer#setAxisScale(LogletLab.LogletLabDocument)
	 */
	public void setAxisScale(LogletLabDocument doc) {
		super.setAxisScale(doc);
		yAxis.setMin(-2);
		yAxis.setMax(2);
	}
}
