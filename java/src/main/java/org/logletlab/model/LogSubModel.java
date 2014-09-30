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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.logletlab.LogletLabDataSet;
import org.logletlab.LogletLabDocument;
import org.logletlab.ui.LogSubRenderer;
import org.logletlab.ui.PlotRenderer;
import org.logletlab.util.DPoint;



/**
 * @author jyung
 *
 * Logistic Substitution
 */
public class LogSubModel extends AbstractFitModel {
	/**
	 * The total market size, that is, the aggregate data set.
	 */
	LogletLabDocument doc;
	int ndata,nsets;
	double xPts[];		// x-mesh
	double yPts[][];	// y for each data set
	private LogletLabDataSet totalMarket;
	double sig[];
	double a[], b[];

	private LogSubParameters[] params;
	int[] order;
	
	public LogSubModel() {
	}
	
	public LogSubModel(LogletLabDocument d) {
		initData(d);
	}
	
	
	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitModel#getFitName()
	 */
	public String getFitName() {
		return LogSubParameters.FITNAME;
	}

	public int[] getModes()
	{
		int[] m = {AbstractFitModel.MODE_DEFAULT };
		return m;
	}
	public String getModeName(int i)
	{
		return "none";
	}
	
	/**
	 * @return
	 */
	public LogletLabDocument getDoc() {
		return doc;
	}

	/**
	 * @param document
	 */
	public void setDoc(LogletLabDocument document) {
		doc = document;
	}

	/**
	 * @param doc
	 * Copies all data from each data set, and converts to market share.
	 * Note that because not all sets have the same domain values, so some values
	 * may have to be interpolated where data is incomplete.
	 */
	public void initData(LogletLabDocument d) {
		doc = d;

		nsets = d.getNSets();
		
		HashMap<Double, Double> map = new HashMap<Double, Double>();
		DPoint pt;
		// Get all x-values
		for (int i=0; i<nsets; i++) {
			LogletLabDataSet ds = d.getDataSet(i);
			for (int j=0; j<ds.getNPoints(); j++) {
				pt = ds.getPoint(j);
				map.put(new Double(pt.x),new Double(pt.y));
			}
		}
		
		// Sort all x-values
		Object[] xObj = map.keySet().toArray();
		ndata = xObj.length;
		xPts = new double[ndata+1];
		for (int i=0; i<xObj.length; i++) {
			xPts[i+1] = ((Double)xObj[i]).doubleValue();
		}
		xPts[0] = Double.NEGATIVE_INFINITY;  // insures proper ordering
		Arrays.sort(xPts);
		
		/*
		for (int i=0; i<xPts.length; i++){
			System.err.println(xPts[i]);
		}
		*/
		
		// copy all data and calculate total market size at each x.
		// Interpolate y-values where necessary.
		sig = new double[xPts.length];
		yPts = new double[nsets][xPts.length];
		setTotalMarket(new LogletLabDataSet());
		for (int j=1; j<=ndata; j++) {
			double tm=0;
			for (int i=0; i<nsets; i++) {
				LogletLabDataSet ds = d.getDataSet(i);
				if (ds.getPoint(xPts[j]) != null) {
					yPts[i][j] = ds.getPoint(xPts[j]).y;
				} else {
					yPts[i][j] = ds.getInterpolatedPoint(xPts[j]).y;
				}
				if (yPts[i][j] > 0)
					tm += yPts[i][j];
			}
			getTotalMarket().addPoint(xPts[j],tm);
			sig[j] = 1.0;
		}

		setParams(new LogSubParameters[nsets]);

		// CONVERT TO MARKET SHARES
		/*
		for (int i=0; i<nsets; i++) {
			for (int j=1; j<=ndata; j++) {
				// System.err.println(xPts[j]+","+yPts[i][j]+"/"+totalMarket.getPoint(xPts[j]).y);		
				yPts[i][j] /= totalMarket.getPoint(xPts[j]).y;
			}
		}
		*/
	}



	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitModel#createParameters(java.util.Hashtable)
	 */
	public AbstractFitParameters createParameters(Map<String, String> h, LogletLabDataSet ds) {
		doc = ds.getDoc();
		return new LogSubParameters(h);
	}


	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitModel#createParameters(double[])
	 */
	public AbstractFitParameters createParameters(double[] p) {
		return null;
		/*
		if (p.length == 2) {
			return new LogSubParameters(p[0],p[1],true);
		} else if (p.length >= 4) {
			return new LogSubParameters(p[0],p[1],p[2],p[3]);
		} else {
			return null;
		}
		*/
	}


	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitModel#createParameters(LogletLab.LogletLabDataSet, java.lang.Object)
	 */
	public AbstractFitParameters createParameters(LogletLabDataSet ds, Object obj) {
		// Preconditions:
		// Market shares calculated (in initData())
		// More than one point. 
		
		DPoint p1, p2, hullPt;
		double dx, dy, dMax = 0.;
		// SET INITIAL GUESSES FOR PARAMETERS
		// Quick convex hull estimation.
		// Take chord from first to last point, and
		// find the point furthest from that line.
		// Use the triangle as ascending and descending part.
//		p1 = ds.getPoint(0);
//		hullPt = ds.getPoint(1);
//		p2 = ds.getPoint(ds.getNPoints()-1);
		p1 = transformPoint(ds.getPoint(0),null);
		hullPt = transformPoint(ds.getPoint(1),null);
		p2 = transformPoint(ds.getPoint(ds.getNPoints()-1),null);
		dy = p2.y - p1.y;
		dx = p2.x - p1.x;
		 
		for (int i=1; i<ds.getNPoints()-2; i++){
			// Calculate distance to line formed by p1 and p2.
			// see http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
			DPoint p0 = transformPoint(ds.getPoint(i),null);
			double d = Math.abs(dx*(p1.y-p0.y) - dy*(p1.x-p0.x)) / Math.sqrt(dx*dx+dy*dy);
			if (d > dMax) {
				hullPt = transformPoint(ds.getPoint(i),null);
				dMax = d;
			}
		}
		double slope = 0;
		double intercept, dt, tm, t1;
		//System.err.println(p1.x+" -> "+hullPt.x+" -> "+p2.x);
		//System.err.println(p1.y+" -> "+hullPt.y+" -> "+p2.y);
		// dt = time from FP(x)=-1 to FP(x)=1.
		// tm = where FP(x) = 0.
		if (hullPt.y > p1.y && hullPt.y < p2.y) {
			// STRICTLY INCREASING
			slope = (p2.y-hullPt.y)/(p2.x-hullPt.x);
			intercept = p2.y - slope*p2.x;
			tm = (p1.x-hullPt.y/slope);
			t1 = hullPt.x+(1-hullPt.y)/slope;
			dt = 2 * (t1-tm);
			// Use line from hull point to last point.
			// (Assumption is that points before hull point represent slow growth and aren't good for fitting.)
			// Saturation point = last point
			return new LogSubParameters(hullPt.x,p2.x,p2.x,slope,intercept,slope,intercept);
		} else if (hullPt.y > p1.y && hullPt.y > p2.y) {
			// INCREASING, THEN DECREASING; USE INCREASING
			slope = (hullPt.y-p1.y)/(hullPt.x-p1.x);
			intercept = p2.y - slope*p2.x;
			double intercept2 = p2.y + slope*p2.x;
			tm = (p1.x-p1.y/slope);
			t1 = p1.x+(1-p1.y)/slope;
			dt = 2 * (t1-tm);
			// Use line from first point to hull point.
			// Saturation point = hull point  
			return new LogSubParameters(p1.x,hullPt.x,hullPt.x,slope,intercept,-slope,intercept2);
		} else {
			// STRICTLY DESCENDING SET
			slope = (hullPt.y-p1.y)/(hullPt.x-p1.x);
			intercept = hullPt.y - slope*hullPt.x;
			tm = (p1.x-p1.y/slope);
			t1 = p1.x+(1-p1.y)/slope;
			dt = 2 * (t1-tm);
			// Use line from first point to hull point.
			// (Points after hull point are dropped for same reasons as strictly increasing.)
			// Sautration point = first point 
			return new LogSubParameters(p1.x,hullPt.x,p1.x,slope,intercept,slope,intercept);
		}
	}


	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitModel#createRenderer()
	 */
	public PlotRenderer createRenderer() {
		if (getTotalMarket() == null) {
			// Initializing after opening a document.
			initData(doc);
			for (int i=0; i<doc.getNSets(); i++) {
				getParams()[i] = (LogSubParameters) doc.getDataSet(i).getFitParameters(this);
			}
		}
		return new LogSubRenderer(this);
	}


	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitModel#eval(double, LogletLab.AbstractFitParameters)
	 */
	public double eval(double x, AbstractFitParameters fp) {
		// not applicable for an individual data set; has to be done at document level
		return 0;
	}


	public DPoint transformPoint(DPoint p, AbstractFitParameters fp) {
		// Apply Fisher-Pry, plot as semilog.
		DPoint p1 = new DPoint(p.x,p.y/getTotalMarket().getPoint(p.x).y);
		//yComp = log10 (yComp / ( fabs(k[cidx]) - yComp ));
		if (p1.y < 1) {
			double normalized = p1.y/ Math.abs(1-p1.y);
			p1.y = Math.log(normalized)/Math.log(10);
		} else {
			p1.y = 2.0;
		}
		//System.err.println(p1.x+"\t"+p1.y);
		return p1;
	}



	public void fitDriver(LogletLabDocument doc) {
		initData(doc);
		// Copy params from data sets
		for (int i=0; i<doc.getNSets(); i++) {
			getParams()[i] = (LogSubParameters) doc.getDataSet(i).getFitParameters(this);
			//System.err.println("fitDriver(): "+params[i].start+"->"+params[i].stop);
		}
		
		doFit();
		
		// Copy params back
		// Maybe we don't need to do this, since the array is references.
		//for (int i=0; i<doc.getNSets(); i++) {
		//	doc.getDataSet(i).setFitParameters(this,params[i]);
		//}
	
	}
	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitModel#doFit()
	 */
	public void doFit() {
		// DO WE HAVE MORE THAN ONE DATA SET?
		if (nsets < 2) return;
		
		a = new double[nsets*2];
		b = new double[nsets*2];
		boolean[] inc = new boolean[nsets];  // increasing?
		// FIT A LINE BETWEEN START AND END
		// Do Fisher-Pry to linearize the data
		// If user-defined, use those values
		// Otherwise, do least squares linear regression
		for (int i=0; i<nsets; i++)
		{
			if (getParams()[i].isUserDefined) {
				a[i*2] = Math.log(81.0)/getParams()[i].dt[0];
				b[i*2] = -a[i*2]*getParams()[i].tm[0];
			} else if (i==0 || (i > 0 && a[(i-1)*2] > 0.0)) {
				// First data set, or last data set was increasing
				int nTechIdx = i;//order[i];

				// Get the points to fit.
				double[] shareT = new double[xPts.length];
				double[] shareY = new double[xPts.length];

				int nValidPts = 0;

				for (int j=1; j<xPts.length && xPts[j]<=getParams()[i].stop; j++)
				{
					if (xPts[j] >= getParams()[i].start) {
						// System.err.println(xPts[j]+","+yPts[i][j]);
						// Apply Fisher-Pry transform y.
						DPoint pt = transformPoint(new DPoint(xPts[j],yPts[i][j]),null);
						// If market share is significant, then use this to fit.
						if (pt.y > -10.0) {
							// System.err.println("doFit():"+pt);
							nValidPts++;
							shareT[nValidPts] = pt.x;
							// Fit using log e, not log 10.
							shareY[nValidPts] = pt.y*Math.log(10);
							// System.err.println(shareT[nValidPts]+","+shareY[nValidPts]);
						}
					}
				}
				// Do we have enough points to fit a line?
				if (nValidPts >= 2) {
					double ab[] = LeastSquaresRegression.leastSq(shareT,shareY,null,nValidPts);
					// We want at + b, but NR fits a + bx.  Whatever.
					a[i*2] = ab[1];
					b[i*2] = ab[0];
					getParams()[i].saturationPoint = getParams()[i].stop;
					// System.err.println("doFit(): i="+i+", a="+ab[1]+", b="+ab[0]);
				} else {
					// TODO: throw exception when Log Sub fails
					System.err.println("("+i+") Sorry, there aren't enough points to make a fit.");
				}
			} else {
				// Last data set was decreasing only;
				// Set the rate of increase for this set equal to
				// the previous set's rate of decrease.
				a[i*2] = -a[(i-1)*2];
				b[i*2] = -b[(i-1)*2];
			}
		}
		
		// FIND SATURATION POINTS FOR EACH DATA SET.
		for (int i=0; i<nsets; i++) {
			// System.err.println("doFit(): i="+i+", a1="+a[i*2]);
			// DECLINING ONLY; always saturate
			if (a[i*2] < 0.0) {
				getParams()[i].saturationPoint = -Double.MAX_VALUE; 
				a[i*2+1] = a[i*2];
				b[i*2+1] = b[i*2];
				inc[i] = false;
			// INCLINING ONLY;  never saturate.
			// Also, the last two data sets can't saturate.
			} else if (i>=nsets-2 || getParams()[i-1].saturationPoint >= xPts[xPts.length-1]) {
				getParams()[i].saturationPoint = Double.MAX_VALUE;
				inc[i] = true;
			}
			// BOTH DECLINING AND INCLINING; find saturation point, declining logistic.
			else
			{
				double t_sat;
				if (i==0) {
					t_sat = findSaturationPoint(i,xPts[1]);
				} else {
					t_sat = findSaturationPoint(i,Math.max(xPts[1],getParams()[i-1].saturationPoint));
				}
				// Don't set params[i].saturationPoint yet!
				// I think this screws up the GetYPrime values.
				
				// Set the declining params.
				a[i*2+1] = GetYPrime(t_sat,i);
				b[i*2+1] = GetY(t_sat,i)-a[i*2+1]*t_sat;
				System.err.println("doFit(): i="+i+"/"+nsets+", t_sat="+t_sat+", y_sat="+GetShare(t_sat,i)+", a2="+a[i*2+1]+", b2="+b[i*2+1]);
				//System.err.println("\ta1="+a[i*2]+", b1="+b[i*2]);

				getParams()[i].saturationPoint = t_sat;

				inc[i] = true;
			}
			
			/*
			// In case we want to convert to dt and tm
			 */
			double tm, t1, dt; 
			tm = -b[i*2] / a[i*2];
			t1 = (1-b[i*2]) / a[i*2];
			dt = 2 * (t1-tm);
			getParams()[i].dt[0] = a[i*2];
			getParams()[i].tm[0] = b[i*2];
			getParams()[i].dt[1] = a[i*2+1];
			getParams()[i].tm[1] = b[i*2+1];
		}
	}

	/**
	 * Calculates the endpoint of the saturation phase of the ith data set--
	 * i.e., where it switches to a downward logistic.
	 * This is where y''/y' is at a minimum, where:
	 * <pre>
	 * y[j] = log (f[j]/1-f[j]) where f[j] = 1 - (sum of f[i] over all i != j)
	 *                                f[i] = 1 / (1 + exp(-at-b)) for i != j
	 * </pre>
	 * Numerically, this is where (y'''*y') - (y''*y'') &gt; 0. 
	 * @param i
	 * @param start
	 * @return the saturation point for ith data set (where a technology
	 * switches from saturation to down-logistic)
	 */
	private double findSaturationPoint(int i, double start) {
		/* The saturation point is  where a technology
		 * switches from saturation to down-logistic.
		 */
		
		double y=-1.;
		//while(time < end && y < 0.0)
		for (double time=start+10*EPSILON; time <= xPts[xPts.length-1]; time += EPSILON)
		{
			// Fast forward until the next technology
			// has a significant market share.
			if (GetY(time,i+1) < -2) {
				continue;
			}
			double ytmp1= GetYPrime(time,i)*GetYTriplePrime(time,i);
			double ytmp2= GetYDoublePrime(time,i)*GetYDoublePrime(time,i);
			y = ytmp1 - ytmp2;
			
			if (y > 0)
				return time;
		}
	
		return Double.MAX_VALUE;
	}

	private final double EPSILON = 1.0E-2;

	private double GetShare(double t, int j)
	{
		double f=0.0;
		// Get the market share of everyone else.
		for (int i=0; i< nsets; i++) {
			if (i != j) {
				double share;
				if (t < getParams()[i].saturationPoint)
					share = 1.0 / ( 1.0 + Math.exp( (-a[i*2]*t) - b[i*2]) );
				else 
					share = 1.0 / ( 1.0 + Math.exp( (-a[i*2+1]*t) - b[i*2+1]) );
				f += share;
			}
			
		}
		// My market share is the rest of market.
		if (f < 1.) {
			return 1 - f;
		} else {
			return 0;
		}
	}
	private double GetY(double t, int j)
	{
		double y;
		double f_j = GetShare(t,j);
		if (f_j > 0) {
			y = Math.log( f_j / (1-f_j) );
		} else {
			y = -Double.MAX_VALUE; // Double.NEGATIVE_INFINITY;
		}

		return y;

	}


	/* first derivative */
	private double GetYPrime(double t, int j)
	{
		double f, fh, y;
	
		f = GetY(t,j);
		fh = GetY(t+EPSILON,j);
		y = (fh-f) / (EPSILON);
		// System.err.println("GetYPrime(): fh="+fh+", f="+f+", y'="+(fh-f));

		return y;
	}


	/* second derivative */
	private double GetYDoublePrime(double t, int j)
	{
		double[] y = new double[3];
		double[] yp = new double[3];
		for (int i=0; i< 3; i++) {
			y[i] = GetY(t+((i)*EPSILON),j);
			if (i>0) {
				yp[i] = (y[i]-y[i-1]) / (EPSILON);
			}
		}
		return (yp[2]-yp[1]) / (EPSILON);
	}


	/* third derivative */
	private double GetYTriplePrime(double t, int j)
	{
		double[] y = new double[4];
		double[] yp = new double[4];
		double[] ypp = new double[4];
		for (int i=0; i< 4; i++) {
			y[i] = GetY(t+((i)*EPSILON),j);
			if (i>0) {
				//System.err.println("GetY''': y["+i+"]="+y[i]+", y["+(i-1)+"]="+y[i-1]);
				yp[i] = (y[i]-y[i-1]) / (EPSILON);
				//System.err.println("GetY''': yp["+i+"]="+yp[i]);
			}
			if (i>1)
				ypp[i] = (yp[i]-yp[i-1]) / (EPSILON);
		}
		return (ypp[3]-ypp[2]) / (EPSILON);
	}

	public LogletLabDataSet getTotalMarket() {
		return totalMarket;
	}

	public void setTotalMarket(LogletLabDataSet totalMarket) {
		this.totalMarket = totalMarket;
	}

	public LogSubParameters[] getParams() {
		return params;
	}

	public void setParams(LogSubParameters[] params) {
		this.params = params;
	}

}
