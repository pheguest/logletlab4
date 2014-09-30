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
package org.logletlab.model;

import java.util.Map;

import org.logletlab.LogletLabDataSet;
import org.logletlab.ui.LogisticFitModelRenderer;
import org.logletlab.ui.PlotRenderer;
import org.logletlab.util.DPoint;

public class LogisticFitModel extends LevenbergMarquardtFitModel
{
	private double displacement; // lower bound; normalizes function so that the first point is 0.
	public int nLogistics;
	
	public static final int MODE_NORMAL = 1;	// default mode.
	public static final int MODE_DECOMP = 2;
	public static final int MODE_FP = 3;
	public static final int MODE_BELL = 4;
	
	public static final int FITTYPE_NORMAL = 1;
	public static final int FITTYPE_CM = 2;
	

	public LogisticFitModel() {}
	
	/**
	 * Number of logistics to use
	 * @param n
	 */
	public LogisticFitModel(int n) {
		nLogistics = n;
	}

	public String getFitName() {
		return this.getClass().getName();
	}
	

	public AbstractFitParameters createParameters(Map<String, String> h, LogletLabDataSet ds) {
//		String[] kStr, dtStr, tmStr;
//		kStr  = ((String) h.get("k")).split(",");
//		tmStr = ((String) h.get("tm")).split(",");
//		dtStr = ((String) h.get("dt")).split(",");
//		
//		// initialize myself!
//		nLogistics = kStr.length;
//		
//		double p[] = new double[nLogistics*3];
//		for (int i=0; i<nLogistics; i++) {
//			p[i*3  ] = Double.parseDouble(kStr[i] );
//			p[i*3+1] = Double.parseDouble(dtStr[i]);
//			p[i*3+2] = Double.parseDouble(tmStr[i]);
//		}
//		double disp = Double.parseDouble((String) h.get("displacement"));
		LogisticFitParameters p = new LogisticFitParameters(h); 
		nLogistics = p.getnLogistics();
		return p;
	}
	public AbstractFitParameters createParameters(LogletLabDataSet ds, Object obj) {
		initData(ds,0);
		// set initial guesses for parameters
		// TODO: create better heuristics for initial guess
		int n = ((Integer)obj).intValue();
		double p[] = new double[n*3];
		boolean h[] = new boolean[n*3];
		for (int i=0; i<n; i++) {
//			gdt = (int) ((bl.m_xmax-bl.m_xmin)/(lwa.m_nLogistics+1));
//			gk  = (int) (bl.m_ymax * 1.1 / lwa.m_nLogistics) - lwa.m_nDisplacement;
//			gtm = (int) (((k+1)*(bl.m_xmax-bl.m_xmin)/(lwa.m_nLogistics+1)) + bl.m_xmin);

			//k
			p[i*3  ] = ((y[ndata]*1.01)/n)-displacement; 
			//dt
			p[i*3+1] = (x[ndata]-x[1])/(n+1);
			//tm
			p[i*3+2] = x[1] + (i+1)*(x[ndata]-x[1])/(n+1);

			h[i*3  ] = h[i*3+1] = h[i*3+2] = false; 
		}
		return new LogisticFitParameters(n,p,h,0);
	}
	public AbstractFitParameters createParameters(double[] p) {
		if (p.length % 3 == 0) {
			int n = p.length/3;
			for (int i=0; i<n; i++) {
				p[i*3+1] = Math.log(81)/p[i*3+1];
			}
			
			boolean h[] = new boolean[p.length];
			for (int i=0; i<n; i++) {
				if (ia == null) {
					h[i*3  ] = h[i*3+1] = h[i*3+2] = !LM_HOLD;
				} else {
					h[i*3  ] = ia[i*3+1]; 
					h[i*3+1] = ia[i*3+2]; 
					h[i*3+2] = ia[i*3+3]; 
				}
			}
			
			LogisticFitParameters lfp = new LogisticFitParameters(n,p,h,0);
			if (ia != null) {
				for (int i=0; i<n; i++) {
					lfp.holdK[i]  = !ia[i*3+1]; 
					lfp.holdDT[i] = !ia[i*3+2]; 
					lfp.holdTM[i] = !ia[i*3+3]; 
				}
			}
			return lfp;
		} else if (p.length % 3 == 1) {
			int n = p.length/3;

			boolean h[] = new boolean[p.length];
			for (int i=0; i<n; i++) {
				if (ia == null) {
					h[i*3  ] = h[i*3+1] = h[i*3+2] = !LM_HOLD; 
				} else {
					h[i*3  ] = ia[i*3+1]; 
					h[i*3+1] = ia[i*3+2]; 
					h[i*3+2] = ia[i*3+3]; 
				}
			}

			LogisticFitParameters lfp = new LogisticFitParameters(n,p,ia,p[p.length-1]);
			if (ia != null) {
				for (int i=0; i<n; i++) {
					lfp.holdK[i]  = !ia[i*3+1]; 
					lfp.holdDT[i] = !ia[i*3+2]; 
					lfp.holdTM[i] = !ia[i*3+3]; 
				}
			}
			return lfp;
		} else {
			return null;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitModel#getRenderer()
	 */
	public PlotRenderer createRenderer() {
		return new LogisticFitModelRenderer(this);
	}

	public void initData(LogletLabDataSet parent, double disp) {
		nalloc = parent.getNPoints();

		sig = new double[nalloc+1];

		x = new double[nalloc+1];
		y = new double[nalloc+1];

	    ndata = 0;
	    displacement = disp;
	    for (int i=0; i<parent.getNPoints(); i++) {
			DPoint pt = (DPoint) parent.getPoint(i);
			if (pt.isValid() && !pt.isExcluded) {
				ndata++;
				x[ndata] = pt.x;
				y[ndata] = pt.y - displacement;
				sig[ndata] = 1.0;
			}
	    }
	}

	public void fitDriver(LogletLabDataSet dataset, int fitType) {
		LogisticFitParameters params = (LogisticFitParameters)dataset.getFitParameters(this);
		
		// reload the data (in case changes have been made in the data since the last fit)
		initData(dataset,params.getDisplacement());
		
		if (fitType == FITTYPE_NORMAL) {
			// Do Levenberg-Marquardt least-squares
			// reinitialize the LM variables.
			initFit(3*nLogistics);

			// enter the initial values for each parameter.
			setInitialGuessForA(params);
		
			/*
			for (int i=1; i<a.length; i++) {
				System.err.println("a["+i+"] = "+a[i]);
				System.err.println("hold["+i+"] = "+ia[i]);
			}
			*/
			
			// Execute bootstrap, which will run the Levenberg-Marquardt
			bootstrap(50);
			
//			for (int i=0; i<nLogistics; i++) {
//				System.err.println(a[i*3+2]);
//			}

			setMode(MODE_NORMAL);
		} else if (fitType == FITTYPE_CM) {
			// Use the (Cesare) Marchetti method, where
			// we fit a straight line to linearized data.
			//  This is done by applying the Fisher-Pry transformation.
			// (1-F)/F, where F = y(t)/K.
			
			// Pick min/max K values.
			double delta = 0.05;
			double kMin=(y[ndata]*(1-delta))-displacement; 
			double kMax=(y[ndata]*(1+delta))-displacement;
			int N_KVALUES=20;
			
			LogisticFitParameters[] paramsArray = new LogisticFitParameters[N_KVALUES];
			double bestFit = 0;
			int bestFitIndex = 0;
			for (int i=0; i<N_KVALUES; i++) {
				double k_i = kMin + (i*(kMax-kMin)/N_KVALUES);
				// for each k[i], do Fisher-Pry, then linear regression.
				double[] xTmp = new double[ndata+1];
				double[] yTmp = new double[ndata+1];
				// System.err.println("k["+i+"]="+k_i);
				int nValidPts=0;
				for (int j=1, k=1; j<=ndata; j++,k++) {
					while (j <= ndata && k_i <= y[j]) {
						j++;
					}
					if (j > ndata) break;
					
					xTmp[k] = x[j];
					yTmp[k] = Math.log( (y[j]/k_i) / (1-(y[j]/k_i)));
					// System.err.println(x[j]+","+yTmp[k]+",");
					
					nValidPts++;
				}
				// use weights to emphasize the middle points and
				// demphasize the end points.
				// We can use a Gaussian curve, which is bell shaped.
				// f(x) = e^(-(x-mean)^2)/(2*variance)
				// The narrower the spread, the less weight is given to the endpoints.
				 
				double[] sig = new double[nValidPts+1];
				double SPREAD = 50;
				double mean = (xTmp[1]+xTmp[nValidPts])/2;
				double var = (mean-xTmp[1])*SPREAD;
				
				@SuppressWarnings("unused")
				double max = 1/(Math.sqrt(var)*Math.sqrt(2*Math.PI));
				for (int j=1; j<=nValidPts; j++) {
					sig[j] = (Math.exp(-Math.pow(xTmp[j]-mean,2)/(2*var)));
					//System.err.println("sig["+j+"]="+sig[j]);			
				}
				double ab[] = LeastSquaresRegression.leastSq(xTmp,yTmp,sig,nValidPts);
				// System.err.println("a="+ab[0]+", b="+ab[1]+", q="+ab[5]);
				
				// Map from a + bx to dt and tm:
				double tm, dt; 
				tm = -ab[0] / ab[1];
				dt = ab[1]; // Math.log(81)/a transform done in constructor
				paramsArray[i] = new LogisticFitParameters(k_i,dt,tm);
				if (ab[5] > bestFit) {
					bestFit = ab[5];
					bestFitIndex = i;
				}
			}
			// Pick the "best" line.
			System.err.println("Best fit: #"+bestFitIndex+", q="+bestFit);
			
			a = new double[nLogistics*3 + 1];
			ia = new boolean[nLogistics*3 + 1];

			for (int i=0; i<nLogistics; i++) {
				a[i*3+1]=paramsArray[bestFitIndex].k[0];
				a[i*3+2]=paramsArray[bestFitIndex].dt[0];
				a[i*3+3]=paramsArray[bestFitIndex].tm[0];
				ia[i*3+1]=ia[i*3+2]=ia[i*3+3]=LevenbergMarquardtFitModel.LM_HOLD;
			}

			// Set visualization to Fisher-Pry.
			setMode(MODE_FP);
		}
		
		
		
		double p[] = new double[3*nLogistics];
		boolean h[] = new boolean[3*nLogistics];
		for (int i=0; i<nLogistics; i++) {
			p[i*3]   = a[i*3+1]+displacement;
			p[i*3+1] = a[i*3+2];  // no need to transform; done in createParameters()
			p[i*3+2] = a[i*3+3];
			h[i*3  ] = !ia[i*3+1];
			h[i*3+1] = !ia[i*3+2];
			h[i*3+2] = !ia[i*3+3];
		}
		
		params = new LogisticFitParameters (nLogistics,p,h,displacement);
		if (mean.length >= 3) {
			for (int i=0; i<nLogistics; i++) {
				params.setMean("k" ,i,mean[i*3]);
				params.setMean("dt",i,mean[i*3+1]);
				params.setMean("tm",i,mean[i*3+2]);
			}
		}
		if (stddev.length >= 3) {
			for (int i=0; i<nLogistics; i++) {
				params.setStandardDeviation("k" ,i,stddev[i*3  ]);
				params.setStandardDeviation("dt",i,stddev[i*3+1]);
				params.setStandardDeviation("tm",i,stddev[i*3+2]);
			}
		}
		dataset.setFitParameters(this, params);
	}

	// this is called LogletF in 1.0
	public double getGradient(double t,double p[],double dy_dp[],int ma) {
		int num_logs = ma / 3;
		double yfit = 0.0;

		for (int i=1;i<=num_logs;i++) {
			int j= (i-1)*3 + 1;
			yfit += (p[j]) / ( 1 + Math.exp(-1.0 * p[j+1]*(t-p[j+2])));
			dy_dp[j] = 1 / (1+Math.exp(-p[j+1]*(t-p[j+2])));
			dy_dp[j+1] = ( (p[j] * (t - p[j+2])) * Math.exp(-p[j+1]*(t-p[j+2])) ) / 
				( (1+Math.exp(-p[j+1]*(t-p[j+2]))) * (1+Math.exp(-p[j+1]*(t-p[j+2]))) );
			dy_dp[j+2] = ( -p[j] * p[j+1] * Math.exp(-p[j+1]*(t-p[j+2])) ) / 
				( (1+Math.exp(-p[j+1]*(t-p[j+2]))) * (1+Math.exp(-p[j+1]*(t-p[j+2]))) );
		}
		return yfit;
	}
  
	public void setInitialGuessForA (AbstractFitParameters abs_p)
	{
		LogisticFitParameters p = (LogisticFitParameters) abs_p;
		mfit = 0;
		for (int i=0; i<p.getnLogistics(); i++) {
			a[i*3+1] = p.k[i];
			a[i*3+2] = (double) Math.log(81) / p.dt[i];
			a[i*3+3] = p.tm[i];
			
			if (p.holdK[i]) {
				ia[i*3+1] = false;
			} else {
				ia[i*3+1] = true;
				mfit++;
			}
			if (p.holdDT[i]) {
				ia[i*3+2] = false;
			} else {
				ia[i*3+2] = true;
				mfit++;
			}
			if (p.holdTM[i]) {
				ia[i*3+3] = false;
			} else {
				ia[i*3+3] = true;
				mfit++;
			}
		}
	}
	
	public double eval(double x, AbstractFitParameters params) {
		LogisticFitParameters p = (LogisticFitParameters) params;
		double y = p.getDisplacement();
		for (int i=0; i<p.getnLogistics(); i++) {
			y += (p.k[i] / ( 1 + Math.exp(-(Math.log(81)/p.dt[i])*(x-p.tm[i]))));
		}
		return y;
	}
	
	public DPoint transformPoint(DPoint p, AbstractFitParameters fp) {
		LogisticFitParameters params = (LogisticFitParameters) fp;
		if (getMode() == MODE_FP) {
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

	public int[] getModes()
	{
		int[] m = {LogisticFitModel.MODE_NORMAL,
			LogisticFitModel.MODE_DECOMP,
			LogisticFitModel.MODE_FP,
			LogisticFitModel.MODE_BELL };
		return m;
	}
	public String getModeName(int i)
	{
		switch (i) {
			case LogisticFitModel.MODE_NORMAL:
				return "none";
			case LogisticFitModel.MODE_DECOMP:
				return "decomposed";
			case LogisticFitModel.MODE_FP:
				return "Fisher-Pry";
			case LogisticFitModel.MODE_BELL:
				return "bell curves";
			default:
				return "none";
		}
	}

}