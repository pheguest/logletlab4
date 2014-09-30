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


public class LogSubParameters extends AbstractFitParameters {
	public static String FITNAME = "LogletLab.LogSubModel";

	public double start, stop, saturationPoint;
	public double[] dt, tm;  // two-element arrays; one set of inclining, one set declining.
	public boolean isUserDefined;
	
	public LogSubParameters(double t0, double t1, double t_s, double p0, double p1, double p2, double p3)
	{
		super(FITNAME);
		start = t0; stop = t1; saturationPoint = t_s;
		dt = new double[2];
		tm = new double[2];
		dt[0] = p0; tm[0] = p1;
		dt[1] = p2; tm[1] = p3;
		isUserDefined = false;
	}

	public LogSubParameters(double t0, double t1, double p0, double p1)
	{
		super(FITNAME);
		start = t0; stop = saturationPoint = t1;
		dt = new double[2];
		tm = new double[2];
		dt[0] = p0; tm[0] = p1;
		isUserDefined = false;
	}

	public LogSubParameters(double u, double v, boolean user)
	{
		super(FITNAME);
		dt = new double[2];
		tm = new double[2];
		isUserDefined = user;
		if (isUserDefined) {
			dt[0] = u; tm[0] = v;
		} else {
			start = u; stop = saturationPoint = v;
		}
	}

	public LogSubParameters(Map<String, String> h)
	{
		super(FITNAME);
		setParams(h);
	}

	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitParameters#getHashKey()
	 */
	public Object getHashKey() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitParameters#getParams()
	 */
	public Map<String, String> getParams() {
		Map<String, String> h = new HashMap<String, String>();
		h.put("start",String.valueOf(start));
		h.put("stop",String.valueOf(stop));
		h.put("satPt",String.valueOf(saturationPoint));
		h.put("dt-inc",String.valueOf(dt[0]));
		h.put("tm-inc",String.valueOf(tm[0]));
		h.put("dt-dec",String.valueOf(dt[1]));
		h.put("tm-dec",String.valueOf(tm[1]));
		
		return h;
	}

	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitParameters#setParams(java.util.Hashtable)
	 */
	public void setParams(Map<String,String> h) {
		start = Double.parseDouble(h.get("start"));
		stop  = Double.parseDouble(h.get("stop"));
		saturationPoint = Double.parseDouble(h.get("satPt"));
		dt = new double[2];
		tm = new double[2];
		dt[0] = Double.parseDouble(h.get("dt-inc"));
		tm[0] = Double.parseDouble(h.get("tm-inc"));
		dt[1] = Double.parseDouble(h.get("dt-dec"));
		tm[1] = Double.parseDouble(h.get("tm-dec"));
	}

}
