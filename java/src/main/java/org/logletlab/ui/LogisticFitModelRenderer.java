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
import org.logletlab.model.LogisticFitModel;
import org.logletlab.model.LogisticFitParameters;
import org.logletlab.util.DPoint;

/**
 * @author J
 *
 * This class renders code for the logistic fit model.
 * Transforms available Fisher-Pry
 */
public class LogisticFitModelRenderer extends PlotRenderer 
	implements FitRenderer
{
	AbstractFitModel model;
	static final double NINETY_DEGREES = Math.toRadians(90.0);

	public LogisticFitModelRenderer(LogisticFitModel m) {
		this.model  = m;
	}
	
	public AbstractFitModel getModel() {
		return model;
	}
	public void setModel(AbstractFitModel afm) {
		model = afm;
	}
	
	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitModelRenderer#drawFitLine(double, double)
	 */
	public void drawFitLine(AbstractFitParameters params, double t0, double t1, Graphics g, Rectangle plotRect) {
		LogisticFitParameters p = (LogisticFitParameters)params;
		
		if (model.getMode() == LogisticFitModel.MODE_NORMAL || model.getMode() == LogisticFitModel.MODE_UNFITTED)
		{
			double tstep = (t1-t0) / (double)nFitPts;
			
			if (p.isBootstrapped()) {
				// Draw the confidence region
				
				LogisticFitParameters p_boot = new LogisticFitParameters(p);
				Color oldColor = g.getColor();
				g.setColor(new Color(204,204,204));
				Polygon poly = new Polygon();
				for (int i=0; i<p.getnLogistics(); i++) {
					double kMean = p.getMean("k",i);
					double kStdDev = p.getStandardDeviation("k",i);
					p_boot.k[i] = kMean + 1.645*kStdDev + p.getDisplacement();
				}
				//System.err.println(p.k[0]);
				for (int i=0; i<=nFitPts; i++) {
					double t_i = t0 + i*tstep;
					double t_j = t0 + (i+1)*tstep;
					poly.addPoint(plotX(t_i,plotRect),plotY(model.eval(t_i,p_boot),plotRect));
				}
				for (int i=0; i<p.getnLogistics(); i++) {
					double kMean = p.getMean("k",i);
					double kStdDev = p.getStandardDeviation("k",i);
					p_boot.k[i] = kMean - 1.645*kStdDev + p.getDisplacement();		
				}
				for (int i=nFitPts; i>=0; i--) {
					double t_i = t0 + i*tstep;
					double t_j = t0 + (i-1)*tstep;
					poly.addPoint(plotX(t_i,plotRect),plotY(model.eval(t_i,p_boot),plotRect));
				}
				g.fillPolygon(poly);
				g.setColor(oldColor);
			}
			
			for (int i=0; i<nFitPts; i++) {
				double t_i = t0 + i*tstep;
				double t_j = t0 + (i+1)*tstep;
				
				g.drawLine(plotX(t_i,plotRect),plotY(model.eval(t_i,p),plotRect),
						   plotX(t_j,plotRect),plotY(model.eval(t_j,p),plotRect));
			}
		} else if (model.getMode() == LogisticFitModel.MODE_DECOMP) {
			// DECOMPOSE
			for (int j=0; j<p.getnLogistics(); j++) {
				double t_j_min = p.tm[j]-Math.abs(p.dt[j]);
				double t_j_max = p.tm[j]+Math.abs(p.dt[j]);
				double tstep = (t_j_max-t_j_min) / (double)nFitPts;
				for (int i=0; i<nFitPts; i++) {
					double t_i = t_j_min + i*tstep;
					double t_j = t_j_min + (i+1)*tstep;
					//double y_i = p.k / ( 1 + Math.exp(-(Math.log(81)/p.dt)*(t_i-p.tm)));

					//System.err.println("p["+i+"] = "+t_i+","+y_i+"\t"+(-dt*(t_i-tm)));
					double y_i = model.eval(t_i,p);
					y_i = p.k[j]/(1.0 + Math.exp(-1.0*(Math.log(81)/p.dt[j])*(t_i-p.tm[j])));
					double y_j = p.k[j]/(1.0 + Math.exp(-1.0*(Math.log(81)/p.dt[j])*(t_j-p.tm[j])));

					g.drawLine(plotX(t_i,plotRect),plotY(y_i,plotRect),
							   plotX(t_j,plotRect),plotY(y_j,plotRect));
				}
			}
		} else if (model.getMode()  == LogisticFitModel.MODE_FP) {
			// DECOPMPOSE
			for (int i=0; i<p.getnLogistics(); i++) {
				// x-coordinate #1
				double xFPFit[] = new double[2];
				double yFPFit[] = new double[2];
				double yTmp;
				xFPFit[0] = p.tm[i] - Math.abs(Math.log(99)/(Math.log(81)/p.dt[i]));
				
				// Might want tMin but not sure.
				if (xFPFit[0] < 0) xFPFit[0] = 0;
				// Transformation of FITTED y-data
				yFPFit[0] = p.k[i] / ( 1+Math.exp( -(Math.log(81)/p.dt[i])*(xFPFit[0]-p.tm[i]) ) );
				//yTmp = /p.k;
				yFPFit[0] = yFPFit[0] / Math.abs(p.k[i]-yFPFit[0]); //(1-yTmp);
				yFPFit[0] = Math.log(yFPFit[0])/Math.log(10); // log_10_x = ln x / ln 10
				
				// x-coordinate #2
				xFPFit[1] = p.tm[i] + Math.abs(Math.log(99)/(Math.log(81)/p.dt[i]));
				// Transformation of FITTED y-data
				yFPFit[1] = p.k[i] / ( 1+Math.exp( -(Math.log(81)/p.dt[i])*(xFPFit[1]-p.tm[i]) ) );
				yTmp = yFPFit[1]/p.k[i];
				yFPFit[1] = yTmp / (1-yTmp);
				yFPFit[1] = Math.log(yFPFit[1])/Math.log(10); // log_10_x = ln x / ln 10

				g.drawLine(plotX(xFPFit[0],plotRect),plotY(yFPFit[0],plotRect),
						   plotX(xFPFit[1],plotRect),plotY(yFPFit[1],plotRect));

			}
		} else if (model.getMode() == LogisticFitModel.MODE_BELL) {
			// DECOMPOSE
			for (int j=0; j<p.getnLogistics(); j++) {
				double t_j_min = p.tm[j]-Math.abs(p.dt[j]);
				double t_j_max = p.tm[j]+Math.abs(p.dt[j]);
				double tstep = (t_j_max-t_j_min) / (double)nFitPts;
				for (int i=0; i<nFitPts; i++) {
					double t_i = t_j_min + i*tstep;
					double t_j = t_j_min + (i+1)*tstep;
					// y-coordinate
					double e_i = Math.exp( -(Math.log(81)/p.dt[j])*(t_i-p.tm[j]) );
					double yFit1 = (Math.log(81)/p.dt[j]) * p.k[j] * e_i / ((1+e_i)*(1+e_i));

					// y-coordinate
					double e_j = Math.exp( -(Math.log(81)/p.dt[j])*(t_j-p.tm[j]) );
					double yFit2 = (Math.log(81)/p.dt[j]) * p.k[j] * e_j / ((1+e_j)*(1+e_j));

					g.drawLine(plotX(t_i,plotRect),plotY(yFit1,plotRect),
							   plotX(t_j,plotRect),plotY(yFit2,plotRect));
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitModelRenderer#drawPoint(LogletLab.DPoint)
	 */
	public void drawData(Graphics g, Rectangle plotRect, LogletLabDataSet pts) {
		LogisticFitParameters params = (LogisticFitParameters) pts.getFitParameters(model);
	
		if (model.getMode() == LogisticFitModel.MODE_BELL)
		{
			for (int i=1; i<pts.getNPoints(); i++) {
				for (int j=0; j<params.getnLogistics(); j++) {
					DPoint p0 = new DPoint(pts.getPoint(i-1));
					// TODO: skip ahead to next non-excluded point
					DPoint p1 = new DPoint(pts.getPoint(i));
					if (p1.x > params.tm[j]-params.dt[j] &&
						p1.x < params.tm[j]+params.dt[j])
					{ 
						// subtract the other components from this one
						for (int u=0; u<params.getnLogistics(); u++) {
							if (u != j) {
								p1.y -= params.k[u]/(1.0 + Math.exp(-1.0*(Math.log(81)/params.dt[u])*(p1.x-params.tm[u])));
								p0.y -= params.k[u]/(1.0 + Math.exp(-1.0*(Math.log(81)/params.dt[u])*(p0.x-params.tm[u])));
							}
						}
						double dydt = (p1.y-p0.y)/(p1.x-p0.x);			
						plot(g,plotRect,new DPoint((p1.x+p0.x)/2,dydt));
					}
				}
			}

		}
		else if (model.getMode() == LogisticFitModel.MODE_FP)
		{
			for (int i=0; i<pts.getNPoints(); i++) {
				for (int j=0; j<params.getnLogistics(); j++) {
					DPoint p1 = new DPoint(pts.getPoint(i));
					g.setColor(p1.isExcluded?Color.red:pts.getColor());
					if (p1.x > params.tm[j]-params.dt[j] &&
						p1.x < params.tm[j]+params.dt[j])
					{
						// Decompose, apply Fisher-Pry, and plot as semilog.
						//yComp = log10 (yComp / ( fabs(k[cidx]) - yComp ));
						if (params.k[j] != p1.y) {
							// subtract the other components from this one
							for (int u=0; u<params.getnLogistics(); u++) {
								if (u != j)
									p1.y -= params.k[u]/(1.0 + Math.exp(-1.0*(Math.log(81)/params.dt[u])*(p1.x-params.tm[u])));
							}
							p1.y -= params.getDisplacement();
						double normalized = p1.y/ Math.abs(params.k[j]-p1.y);
							p1.y = Math.log(normalized)/Math.log(10);
						} else {
							p1.y = 2.0;
						}
						plot(g,plotRect,p1);
					}
				}
			}
		}
		else if (model.getMode() == LogisticFitModel.MODE_DECOMP)
		{
			for (int i=0; i<pts.getNPoints(); i++) {
				for (int j=0; j<params.getnLogistics(); j++) {
					DPoint p1 = new DPoint(pts.getPoint(i));
					g.setColor(p1.isExcluded?Color.red:pts.getColor());
					if (p1.x > params.tm[j]-params.dt[j] &&
						p1.x < params.tm[j]+params.dt[j])
					{
						// Decompose, apply Fisher-Pry, and plot as semilog.
						//yComp = log10 (yComp / ( fabs(k[cidx]) - yComp ));
						// subtract the other components from this one
						for (int u=0; u<params.getnLogistics(); u++) {
							if (u != j) {
								p1.y -= params.k[u]/(1.0 + Math.exp(-1.0*(Math.log(81)/params.dt[u])*(p1.x-params.tm[u])));
							}
						}
						p1.y -= params.getDisplacement();
						if (p1.y > 0) {
							plot(g,plotRect,p1);
						}
					}
				}
			}
		}
		else // DEFAULT
		{
			for (int i=0; i<pts.getNPoints(); i++ ) {
				DPoint pt = pts.getPoint(i);
				if (pt.isValid()) {
					g.setColor(pt.isExcluded?Color.red:pts.getColor());
					plot(g,plotRect,transformPoint(pt,params));
				}
			}
		}
	}

	public void drawResiduals(LogletLabDataSet pts, Graphics g, Rectangle residualsRect) {
		if (model.getMode() != AbstractFitModel.MODE_UNFITTED &&
			model.getResidualMode() != AbstractFitModel.RESIDUALS_OFF)
		{
				
			Graphics residualPlot = g.create();
			residualPlot.setClip(residualsRect);


			double resMax=0;	// get max residual for normalization.
			LogisticFitParameters params = (LogisticFitParameters) pts.getFitParameters(model);
			// get max residual value (magnitude)
			// use untransformed value.
			for (int i=0; i<pts.getNPoints(); i++ ) {
				DPoint pt = pts.getPoint(i);
				if (pt.isValid()) {
					resMax = Math.max(resMax, Math.abs(model.getResidualValue(pt.x,pt.y,params)));
				}
			}
			
			// Plot residuals for each point (if on)
			for (int i=0; i<pts.getNPoints(); i++ ) {
				DPoint pt = pts.getPoint(i);
				if (pt.isValid()) {
					residualPlot.setColor(pt.isExcluded?Color.red:pts.getColor());
					double resY = (model.getResidualValue(pt.x,pt.y,params))/resMax;
					//System.err.println(afm.getResidualValue(pt.x,pt.y,pts.getFitParameters(0))+"\t/ "+resmax);
					residualPlot.drawRect(plotX(pt.x,residualsRect)-plotSize,
						residualsRect.y + residualsRect.height/2 - (int)(resY*residualsRect.height/2) - plotSize,
						2*plotSize+1,2*plotSize+1);
				}
			}
			residualPlot.dispose();


			// RESIDUAL AXES
			// axis label
			drawVerticalText(g,(model.getResidualMode()==AbstractFitModel.RESIDUALS_ABS?"raw":"%")+" error",
				residualsRect.x-3,residualsRect.y+residualsRect.height/2);
			// zero-line
			g.drawLine(residualsRect.x,residualsRect.y+residualsRect.height/2,
				residualsRect.x+residualsRect.width,residualsRect.y+residualsRect.height/2);

			// show tick marks on the right vertical axis.
			drawResidualTickLabels(g,residualsRect, resMax);

			int tickMultiplier[] = {1,2,5,10};
			int tickIndex = 0;
			// HORIZONTAL TICKS (x/t axis)
			double tickSize = getMagnitude(tAxis.getMax()-tAxis.getMin());
			int nTicks = Math.max(1,(int)((tAxis.getMax()-tAxis.getMin()) / tickSize));
			// Add ticks if intervals are too big
			// TODO: minimum tick interval based on StringBounds
			while (residualsRect.width / nTicks > MAX_TICK_INTERVAL) {
				tickSize /= 10;
				nTicks = Math.max(1,(int)((tAxis.getMax()-tAxis.getMin()) / tickSize));
				//System.err.println("New tick size: "+tickSize);
			}
			// Remove ticks if intervals are too small. 
			while (residualsRect.width / nTicks < MIN_TICK_INTERVAL) {
				tickIndex++;
				nTicks = Math.max(1,(int)((tAxis.getMax()-tAxis.getMin()) / (tickMultiplier[tickIndex]*tickSize)));
			}
		
			double tickInterval = tickMultiplier[tickIndex]*tickSize;
			for (int i=0; tAxis.getMin()+i*tickInterval<tAxis.getMax(); i++) {
				double t_tick = tAxis.getMin()+i*tickInterval;
				drawHorizontalAxisTickMark(g,residualsRect,t_tick);
				drawHorizontalAxisTickMarkLabel(g,residualsRect,t_tick);
			}
			drawHorizontalAxisTickMarkLabel(g,residualsRect,tAxis.getMax());
		}
	}

	private void drawResidualTickLabels(Graphics g, Rectangle r, double resMax) {
		FontMetrics fm = g.getFontMetrics();
		if (model.getResidualMode()==AbstractFitModel.RESIDUALS_ABS) {		
			g.drawString(String.valueOf((int)resMax),r.x+r.width+3,r.y+fm.getDescent());
			g.drawString(String.valueOf(-(int)resMax),r.x+r.width+3,r.y+r.height+fm.getDescent());
		} else {
			g.drawString("100%",r.x+r.width+3,r.y+fm.getDescent());
			g.drawString("-100%",r.x+r.width+3,r.y+r.height+fm.getDescent());
		}
		g.drawString("0",r.x+r.width+3,r.y+r.height/2+fm.getDescent());
	}
	


	public DPoint transformPoint(DPoint p, AbstractFitParameters fp) {
		LogisticFitParameters params = (LogisticFitParameters) fp;
		if (model.getMode() == LogisticFitModel.MODE_FP) {
			// Apply Fisher-Pry, plot as semilog.
			DPoint p1 = new DPoint(p.x,p.y);
			//yComp = log10 (yComp / ( fabs(k[cidx]) - yComp ));
			if (params.k[0] != p.y) {
				double normalized = p1.y/ Math.abs(params.k[0]-p.y);
				p1.y = Math.log(normalized)/Math.log(10);
			} else {
				p1.y = 2.0;
			}
			//System.err.println(p1.x+"\t"+p1.y);
			return p1;
		}
		return p;
	}

	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitModelRenderer#drawYAxis()
	 */
	public void drawYAxis(Graphics g, Rectangle plotRect) {
		if (model.getMode() == LogisticFitModel.MODE_FP) {
			// 5 tick marks
			int nIntervals = 4;
			String[] labels = {"1%","10%","50%","90%","99%"};
			for (int i=0; i<=nIntervals; i++) {
				int y_tick = i*plotRect.height / nIntervals;
				
				drawVerticalAxisTickMark(g,plotRect,i-2);
			
				// custom label
				FontMetrics fm = g.getFontMetrics();
				java.awt.geom.Rectangle2D tickBounds = fm.getStringBounds(labels[i],g);
		
				g.drawString(labels[i],
					plotRect.x-(int)(tickBounds.getWidth()+3),
					plotRect.y+plotRect.height-y_tick+(int)(tickBounds.getHeight()/2)-fm.getDescent());
	
			}
		} else {
			super.drawYAxis(g,plotRect);
		}
	}
	
	protected void drawHorizontalAxisTickMarkLabel(Graphics g, Rectangle plotRect, double value) {
		if (model.getResidualMode() == AbstractFitModel.RESIDUALS_OFF) {
			super.drawHorizontalAxisTickMarkLabel(g,plotRect,value);
		} else {
			// need to draw a little lower...
			String label;
			if (tAxis.getMax() >= 1.0) {
				// cast as an int
				label = String.valueOf((int)value);
			} else {
				label = String.valueOf(value);
			}
			FontMetrics fm = g.getFontMetrics();
			java.awt.geom.Rectangle2D tickBounds = fm.getStringBounds(label,g);
			g.drawString(label,
				plotX(value,plotRect)-(int)(tickBounds.getWidth()/2),
				plotRect.y+plotRect.height+LogletLabCanvas.RES_HEIGHT+(int)(tickBounds.getHeight()));
		}
	}

	public int getLeftMargin(Graphics g)
	{
		if (model.getMode() == LogisticFitModel.MODE_FP)
		{
			FontMetrics fm = g.getFontMetrics();
			// horizontal labels
			java.awt.geom.Rectangle2D tickBounds = fm.getStringBounds("999%",g); // extra "9" is for space
			return (int)tickBounds.getWidth()+3;
		}
		else
		{
			return super.getLeftMargin(g);
		}
	}

	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitModelRenderer#setAxisScale(LogletLab.LogletLabDocument)
	 */
	public void setAxisScale(LogletLabDocument doc) {
		if (model.getMode() == LogisticFitModel.MODE_FP)
		{
			// custom y-scale
			super.setAxisScale(doc);
			yAxis.setMin(-2);
			yAxis.setMax(2);
		}
		else if (model.getMode() == LogisticFitModel.MODE_DECOMP)
		{
			super.setAxisScale(doc);

			// custom y-scale
			yAxis.setMin(0);
			double kmax=0;
			for (int i=0; i<doc.getNSets(); i++) {
				LogisticFitParameters p = (LogisticFitParameters) doc.getDataSet(i).getFitParameters(model);
				for (int j=0; j<p.getnLogistics(); j++) {
					kmax = Math.max(kmax,p.k[j]);
				}
			}
			double yMag = getMagnitude(kmax);
			yAxis.setMax(Math.ceil(kmax / yMag) * yMag);	
		}
		else if (model.getMode() == LogisticFitModel.MODE_BELL)
		{
			DPoint pt1,pt2;
			for (int i=0; i<doc.getNSets(); i++ ) {
				LogletLabDataSet pts = doc.getDataSet(i);
				for (int j=0; j<pts.getNPoints()-1; j++ ) {
					pt1 = pts.getPoint(j);
					if (pt1.isValid()) {
						int k=1;
						while (!pts.getPoint(j+k).isValid()) {
							k++;
						}
						pt2 = pts.getPoint(j+k);
						
						if (i==0 && j==0) {			
							tAxis.setMin(pt2.x);
							tAxis.setMax(pt2.x);
							double dydt = (pt2.y-pt1.y)/(pt2.x-pt1.x);
							yAxis.setMin(dydt);
							yAxis.setMax(dydt);
						} else {
							tAxis.setMin(Math.min(tAxis.getMin(),pt2.x));
							tAxis.setMax(Math.max(tAxis.getMax(),pt2.x));
							double dydt = (pt2.y-pt1.y)/(pt2.x-pt1.x);
							yAxis.setMin(Math.min(yAxis.getMin(),dydt));
							yAxis.setMax(Math.max(yAxis.getMax(),dydt));
						}
					}
				}
			}
			
			double tMag = getMagnitude(tAxis.getMax()-tAxis.getMin());
			if (tMag==0) tMag=1;
			tAxis.setMin(Math.floor(tAxis.getMin() /tMag) * tMag);
			tAxis.setMax(Math.ceil(tAxis.getMax() / tMag) * tMag);

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

		} else {
			super.setAxisScale(doc);
		}

	}
	
	private void drawVerticalText(Graphics g, String s, int x, int y) {
		FontMetrics fm = g.getFontMetrics();
		java.awt.geom.Rectangle2D strBounds = fm.getStringBounds(s,g);

		g.translate(x,y);
		((Graphics2D)g).rotate(-NINETY_DEGREES);
		// centered text
		g.drawString(s,-(int)strBounds.getWidth()/2,-fm.getDescent());
		((Graphics2D)g).rotate(NINETY_DEGREES);
		g.translate(-x,-y);
	}

}
