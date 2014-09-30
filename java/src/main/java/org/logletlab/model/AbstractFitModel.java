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
import org.logletlab.ui.PlotRenderer;

/**
 * @author jyung
 *
 * This is an abstract class for all fitting models.
 * All fitting models must provide the following functions:
 * <ul>
 * <li>fit a trend line to data (doFit())</li>
 * <li>provide a renderer to draw the data and fit line</li>
 * <li>provide f(x) for a given x, and the residual error for an observed point (x,y).
 * </ul>
 *
 * A recipe for creating a fit class
 * <ul>
 * <li>Create subclasses of:
	 * <ul>
	 * <li><b>AbstractFitModel</b>
	 * <li><b>AbstractFitParameters</b>
	 * <li><b>AbstractFitModelPanel</b>
	 * <li><b>AbstractFitParameterPanel</b>
	 * <li><b>PlotRenderer implements FitRenderer</b>
	 * </ul> 
 * <li>In LogletLabFrame, add create[FitName]Panel() method:
	 * <ul>
	 * <li>Get existing instance of AbstractFitModel from doc D, or create one.
	 * <li>Remove other panels
	 * <li>Create a new panel P
	 * <li>Add [FitName]Panel to P
	 * <li>For each data set S in D, add [FitName]ParameterPanel to P
		 * <ul>
		 * <li>If no params exist in S, create one using (model, Object), using best guess.
		 * <li>Display parameters to this panel.
		 * </ul>
	 * </li>
	 * <li>Add P to LogletLabFrame. 
	 * </ul>
 * </li>
 * <li>Implement a fitDriver() method.  If the fit can be done for a specific data set, implement it
 * within [FitName]ParameterPanel.  If the fit uses all data sets (Logistic Substitution, for example),
 * implement it within [FitName]Panel.
	 * <ul>
	 * <li>Copy data to AbstractFitModel M.
	 * <li>Call M.doFit().
	 * <li>Pass back parameters P to data set(s).
	 * </ul>
 * </li>
 * </ul>
 */
public abstract class AbstractFitModel
	implements java.io.Serializable
{
	protected int fitMode;		// used to indicate transformations, etc.
	protected int residualMode;	// used to indicate absolute or residual residuals.
	
	public static final int MODE_UNFITTED = 0; // don't show fit line
	public static final int MODE_DEFAULT = 1;	// default fit mode

	public static final int RESIDUALS_OFF = 0;
	public static final int RESIDUALS_ABS = 1;
	public static final int RESIDUALS_REL = 2;

	/**
	 * @return the name of the fitting model
	 */
	public abstract String getFitName();
	/**
	 * Do the fit
	 */
	public abstract void doFit();

	/**
	 * create a new set of parameters and return them.
	 * Hashtable generally used for constructing from document
	 * @param h
	 * @param d
	 * @return
	 */
	public abstract AbstractFitParameters createParameters(Map<String,String> h, LogletLabDataSet d);
	// double[] usually used for bootstrapping (see LMFitModel)
	/**
	 * 
	 * @param p
	 * @return
	 */
	public abstract AbstractFitParameters createParameters(double[] p);
	// dataset-Object used for initialization at run time
	// Object contains initialization parameters.
	public abstract AbstractFitParameters createParameters(LogletLabDataSet ds, Object obj);

	public abstract PlotRenderer createRenderer();

	/**
	 * Evaluate value of fitted model
	 * @param x
	 * @param fp
	 * @return f(x) using parameters fp
	 */
	public abstract double eval(double x, AbstractFitParameters fp);
	
	/**
	 * @param x
	 * @param y
	 * @param params
	 * @return residual error for observed point (x,y), given parameters p.
	 */
	public double getResidualValue(double x, double y, AbstractFitParameters params) {
		double delta = eval(x,params)-y;
		if (getResidualMode() == RESIDUALS_ABS) {
			return delta;
		} else {
			return delta/y;
		}
	}
	
	public abstract int[] getModes();
	public abstract String getModeName(int i);
	
	public int getMode() { return fitMode; }
	public void setMode(int m) { fitMode = m; }
	public int getResidualMode() { return residualMode; }
	public void setResidualMode(int m) { residualMode = m; }
}