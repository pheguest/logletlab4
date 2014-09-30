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

import java.util.HashMap;
import java.util.Map;


public class LogisticFitParameters extends AbstractFitParameters
	implements BootstrapParameters
{
	public int nLogistics = 1;
	
	public double[] k, dt, tm;
	public Map<String,String> bootstrap;
	
	public boolean[] holdK, holdDT, holdTM;
	
	public double displacement;
	
	public static String FITNAME = "LogletLab.LogisticFitModel";
	
	public LogisticFitParameters(LogisticFitParameters params) {
		super(FITNAME);
		double p[] = new double[3*params.getnLogistics()];
		boolean hold[] = new boolean[3*params.getnLogistics()];
		for (int i=0; i<params.getnLogistics(); i++) {
			p[i*3]  =params.k[i];
			p[i*3+1]=params.dt[i];
			p[i*3+2]=params.tm[i];
			hold[i*3]=params.holdK[i];
			hold[i*3+1]=params.holdDT[i];
			hold[i*3+2]=params.holdTM[i];
		}
		init(params.getnLogistics(),p,hold,params.getDisplacement());
	}
	public LogisticFitParameters(double k0, double dt0, double tm0) {
		super(FITNAME);
		double p[] = {k0,Math.log(81)/dt0,tm0};
		boolean hold[] = {false, false, false};
		init(1,p,hold,0);
	}
	public LogisticFitParameters(double k0, double dt0, double tm0, double disp) {
		super(FITNAME);
		double p[] = {k0,Math.log(81)/dt0,tm0};
		boolean hold[] = {false, false, false};
		init(1,p,hold,disp);
	}
	public LogisticFitParameters(int n, double[] p, boolean[] hold, double disp) {
		super(FITNAME);
		init(n,p,hold,disp);
	}
	
	public LogisticFitParameters(Map<String,String> h)
	{
		super(FITNAME);
		setParams(h);
	}
	
	public void init(int n, double[] p, boolean[] hold, double disp) {
		setnLogistics(n);
		k  = new double[getnLogistics()];
		dt = new double[getnLogistics()];
		tm = new double[getnLogistics()];
		holdK  = new boolean[getnLogistics()];
		holdDT = new boolean[getnLogistics()];
		holdTM = new boolean[getnLogistics()];
		
		for (int i=0; i<getnLogistics(); i++) {
			k[i]=p[i*3]; dt[i]=p[i*3+1]; tm[i]=p[i*3+2];
			if (hold != null) {
				holdK[i] = hold[i*3]; holdDT[i] = hold[i*3+1]; holdTM[i] = hold[i*3+2];
			} else {
				holdK[i] = holdDT[i] = holdTM[i] = !LevenbergMarquardtFitModel.LM_HOLD;
			}
		}
		setDisplacement(disp);
		
		bootstrap = new HashMap<String, String>(6);
	}
	public Map<String, String> getParams() {
		Map<String, String> h = new HashMap<String, String>();
		String kStr, dtStr, tmStr;
		kStr = dtStr = tmStr = "";
		for (int i=0; i<getnLogistics(); i++) {
			kStr  += (i>0?",":"")+String.valueOf( k[i]);
			tmStr += (i>0?",":"")+String.valueOf(tm[i]);
			dtStr += (i>0?",":"")+String.valueOf(dt[i]);
		}

		h.put("k",kStr);
		h.put("dt",dtStr);
		h.put("tm",tmStr);
		h.put("displacement",String.valueOf(getDisplacement()));
		if (!bootstrap.isEmpty()) {
			// HashMaps have null entries, but Hashtable will barf
			// if you try to use them.  Therefore we should only put non-null entries.
			
			for (String e: bootstrap.keySet() ) {
				if (e != null)
					h.put(e, bootstrap.get(e));
			}

		}
				
		return h;
	}
	public void setParams(Map<String, String> h) {
		String[] kStr, dtStr, tmStr, meanStr, stddevStr;
		kStr  = h.get("k").split(",");
		tmStr = h.get("tm").split(",");
		dtStr = h.get("dt").split(",");

		if (kStr.length > 0) {
			setnLogistics(kStr.length);
			k = new double[getnLogistics()];
			dt = new double[getnLogistics()];
			tm = new double[getnLogistics()];
			holdK  = new boolean[getnLogistics()];
			holdDT = new boolean[getnLogistics()];
			holdTM = new boolean[getnLogistics()];
			for (int i=0; i<getnLogistics(); i++) {
				k[i]  = Double.parseDouble(kStr[i] );
				dt[i] = Double.parseDouble(dtStr[i]);
				tm[i] = Double.parseDouble(tmStr[i]);
				holdK[i] = holdTM[i] = holdDT[i] = false;
			}
		} else {
			setnLogistics(1);
			k = new double[getnLogistics()];
			dt = new double[getnLogistics()];
			tm = new double[getnLogistics()];
			holdK  = new boolean[getnLogistics()];
			holdDT = new boolean[getnLogistics()];
			holdTM = new boolean[getnLogistics()];
			k[0]  = Double.parseDouble(h.get("k"));
			dt[0] = Double.parseDouble(h.get("dt"));
			tm[0] = Double.parseDouble(h.get("tm"));
			holdK[0] = holdTM[0] = holdDT[0] = false;
		}
		setDisplacement(Double.parseDouble(h.get("displacement")));
		
		// get bootsrap params, if they exist
		bootstrap = new HashMap<String, String>(6*getnLogistics());
		for (int i=0; i<getnLogistics(); i++) {
			bootstrap.put("k"+i+"_mean"  ,h.get("k"+i+"_mean"));
			bootstrap.put("k"+i+"_stddev",h.get("k"+i+"_stddev"));
			bootstrap.put("dt"+i+"_mean"  ,h.get("dt"+i+"_mean"));
			bootstrap.put("dt"+i+"_stddev",h.get("dt"+i+"_stddev"));
			bootstrap.put("tm"+i+"_mean"  ,h.get("tm"+i+"_mean"));
			bootstrap.put("tm"+i+"_stddev",h.get("tm"+i+"_stddev"));
		}
	}

	public Object getHashKey() {
		return new Integer(getnLogistics());
	}
	

	/* (non-Javadoc)
	 * @see LogletLab.BootstrapParameters#getMean(java.lang.String)
	 */
	public double getMean(String paramName, int i) {
		try {
			return Double.parseDouble(bootstrap.get(paramName+i+"_mean"));
		} catch (NumberFormatException nfe) {
			return Double.NaN;
		} catch (NullPointerException npe) {
			return Double.NaN;
		}

	}

	/* (non-Javadoc)
	 * @see LogletLab.BootstrapParameters#setMean(java.lang.String, double)
	 */
	public void setMean(String paramName, int i, double mean) {
		bootstrap.put(paramName+i+"_mean",String.valueOf(mean));
	}

	/* (non-Javadoc)
	 * @see LogletLab.BootstrapParameters#getStandardDeviation(java.lang.String)
	 */
	public double getStandardDeviation(String paramName, int i) {
		try {
			return Double.parseDouble(bootstrap.get(paramName+i+"_stddev"));
		} catch (NumberFormatException nfe) {
			return Double.NaN;
		} catch (NullPointerException npe) {
			return Double.NaN;
		}
	}

	/* (non-Javadoc)
	 * @see LogletLab.BootstrapParameters#setStandardDeviation(java.lang.String, double)
	 */
	public void setStandardDeviation(String paramName, int i, double stddev) {
		bootstrap.put(paramName+i+"_stddev",String.valueOf(stddev));
	}
	/* (non-Javadoc)
	 * @see LogletLab.BootstrapParameters#isBootstrapped()
	 */
	public boolean isBootstrapped() {
		return !Double.isNaN(getMean("k",0));
	}
	public int getnLogistics() {
		return nLogistics;
	}
	public void setnLogistics(int nLogistics) {
		this.nLogistics = nLogistics;
	}
	public double getDisplacement() {
		return displacement;
	}
	public void setDisplacement(double displacement) {
		this.displacement = displacement;
	}

}
