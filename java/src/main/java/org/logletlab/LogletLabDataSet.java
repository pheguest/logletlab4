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
package org.logletlab;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.logletlab.model.AbstractFitModel;
import org.logletlab.model.AbstractFitParameters;
import org.logletlab.util.DPoint;

public class LogletLabDataSet
	implements java.io.Serializable
{
	/**
	 * Title for the data set
	 */
	String dataSetName = "Data Set";
	/**
	 * The actual data
	 */
	Map<Double, DPoint> data;
	/**
	 * 
	 */
	double[] xIndex;
	/**
	 * Fits already applied to these data
	 */
	Map<AbstractFitModel, AbstractFitParameters> fits;	
	/**
	 * color for rendering these data
	 */
	java.awt.Color color = java.awt.Color.black;
	/**
	 * the parent document
	 */
	private LogletLabDocument doc;
	
	public LogletLabDataSet() {
		initDataSet();
	}
	public LogletLabDataSet(String s) {
		initDataSet();
		this.setDataSetName(s);
	}
	private void initDataSet() {
		data = new HashMap<Double, DPoint>();//Vector();
		fits = new HashMap<AbstractFitModel, AbstractFitParameters>();	
	}
	public void setDoc(LogletLabDocument d) { doc = d; }
	public LogletLabDocument getDoc() { return doc; }
		
	public void addPoint(DPoint p) {
		data.put(new Double(p.x), p);
		reindex();
		//data.add(p);
	}
	public void addPoint(double x, double y) {
		data.put(new Double(x), new DPoint(x,y));
		reindex();
		//data.add(new DPoint(x,y));
	}
	public void addPoint(double x, double y, boolean ex) {
		data.put(new Double(x),new DPoint(x,y,ex));
		reindex();
		//data.add(new DPoint(x,y,ex));
	}

	/**
	 * Pre-sorts all x values for the data set to speed up sequential lookup.
	 */
	public void reindex() {
		Object[] xObj = data.keySet().toArray();
		xIndex = new double[xObj.length];
		for (int j=0; j<xObj.length; j++) {
			xIndex[j] = ((Double)xObj[j]).doubleValue();
		}
		Arrays.sort(xIndex);
		//System.err.println("indexed "+xIndex.length+" points");
	}
	
	public void addFit(AbstractFitModel model, Object obj) {
		fits.put(model,model.createParameters(this,obj));
	}

	
	public void setPoint(DPoint p) {
		data.put(new Double(p.x),p);
	}

	public DPoint getPoint(double x) {
		// May return null!
		return data.get(new Double(x));
	}

	public DPoint getPoint(int i) {
		//System.err.println("getPoint:"+i+"/"+x.length);
		if (xIndex == null) return null;
		if (i >= xIndex.length)
			return null;
		else {
			return data.get(new Double(xIndex[i]));
		}
	}
	
	public void replacePoint(int i, DPoint pt) {
		//System.err.println("replacePoint:"+i+"/"+xIndex.length);
		if (xIndex == null) return;
		if (i >= xIndex.length)
			return;
		else {
			DPoint oldPt = getPoint(i);
			data.remove(new Double(oldPt.x));
			addPoint(pt);
		}
	}


	public DPoint getInterpolatedPoint(double x) {
		int i = Arrays.binarySearch(xIndex,x);
		if (i >= 0) {
			return getPoint(i);
		}
		else
		{
			i = -(i+1);
			double dy;
			if ( i==0 ) {
				// insert before first element;
				// use first two points.
				dy = getPoint(i+1).y - getPoint(i).y;
			} else if ( i < xIndex.length ) {
				// insert before ith element.
				dy = getPoint(i).y - getPoint(i-1).y;
			} else {
				// after last point;
				// use last two points
				dy = getPoint(xIndex.length-1).y - getPoint(xIndex.length-2).y;
				i=xIndex.length-1;
			}
		 
			double y = getPoint(i).y + (x-getPoint(i).x)*dy;
			return new DPoint(x,y);
		}
	}

	
	public List<DPoint> getAllPoints() {
		//System.err.println("getAllPoints:"+xIndex.length);
		List<DPoint> v = new ArrayList<DPoint>();
		if (xIndex != null) {
			for (int i=0; i<xIndex.length; i++) {
				if (data.get(new Double(xIndex[i])) != null)
					v.add(data.get(new Double(xIndex[i])));
			}
		}
		return v;
	}
	
	public AbstractFitParameters getFitParameters(AbstractFitModel afm) {
		// Object should be nLogistics
		return fits.get(afm);
	}
	public void setFitParameters(AbstractFitModel afm,AbstractFitParameters params) {
		fits.put(afm,params);
	}

	public int getNPoints() {
		return data.size();
	}
	public int getNFits() {
		return fits.size();
	}

	/**
	 * @return the name of this data set
	 */
	public String getDataSetName() {
		return dataSetName;
	}

	/**
	 * @param string
	 */
	public void setDataSetName(String string) {
		dataSetName = string;
	}

	/**
	 * @return the plot color for this data set.
	 */
	public java.awt.Color getColor() {
		return color;
	}

	/**
	 * @param color
	 */
	public void setColor(java.awt.Color color) {
		this.color = color;
	}

}
