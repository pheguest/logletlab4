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
 * LMFitModel.java
 *
 * Title:			Loglet Lab 2.0
 * Description:		Levenberg-Marquardt iterative fitting. Taken from
 *					Numerical Recipes.  Subclasses should implement getGradient(),
 *					which defines the derivatives of the function, to determine the steepest descent.
 * @author			J
 * @version			
 */
package org.logletlab.model;

import java.util.Random;

import org.logletlab.LogletLabDataSet;
import org.logletlab.util.DPoint;


/**
 * Levenberg–Marquardt algorithm adapted from Numerical Recipes. 
 */
public abstract class LevenbergMarquardtFitModel extends AbstractFitModel {
	protected double[] x, y, sig;
	protected int nalloc;
	protected int ndata;

	double[] a; // The final results.
	protected boolean[] ia; // hold or not to hold
	protected int ma, mfit; // # of params, # of fitted params

	protected double[][] covar, alpha;
	private double[] atry, beta, da;
	private double[][] oneda;

	private double chisq, ochisq;
	protected double alamda;

	// BOOTSTRAP VARS
	double[] mean = new double[ma];
	double[] stddev = new double[ma];
	public static boolean LM_HOLD = true; // hold constant during fit.

	/**
	 * Generate starting parameters for L-M.
	 * @param abs_p
	 */
	abstract void setInitialGuessForA(AbstractFitParameters abs_p);
	/**
	 * The function that you are trying to optimize
	 * @param t
	 * @param p
	 * @param dy_dp
	 * @param ma
	 * @return
	 */
	abstract double getGradient(double t, double p[], double dy_dp[], int ma);

	LevenbergMarquardtFitModel() {
	}

	public void initData(LogletLabDataSet parent) {
		nalloc = parent.getNPoints();

		sig = new double[nalloc + 1];

		x = new double[nalloc + 1];
		y = new double[nalloc + 1];

		ndata = 0;
		for (int i = 0; i < parent.getNPoints(); i++) {
			DPoint pt = (DPoint) parent.getPoint(i);
			if (!pt.isExcluded) {
				ndata++;
				x[ndata] = pt.x;
				y[ndata] = pt.y;
				sig[ndata] = 1.0;
			}
		}
	}

	public void initFit(int nparams) {
		ma = nparams;
		covar = new double[ma + 1][ma + 1];
		alpha = new double[ma + 1][ma + 1];
		a = new double[ma + 1];
		ia = new boolean[ma + 1];
		alamda = -1.0;
	}

	@Override
	public void doFit() {
		// Start the marquardt.
		mrqmin(); //x,y,sig,ndata,a,ia,ma,covar,alpha,chisq,alamda);

		int itst = 0;
		while (true) {
			ochisq = chisq;
			mrqmin(); //x,y,sig,ndata,a,ia,ma,covar,alpha,chisq,alamda);
			if (chisq > ochisq)
				itst = 0;
			else if (Math.abs(ochisq - chisq) < 0.1)
				itst++;
			if (itst < 4)
				continue;
			alamda = 0.0;
			mrqmin(); //x,y,sig,ndata,a,ia,ma,covar,alpha,chisq,alamda);
			break;
		}
		// results are stored in a.
	}

	//private void mrqmin() //double[] x, double[] y,  double[] sig, int ndata, 
	//double[] a, int[] ia, int ma, 
	//     double[][] covar, double[][] alpha, double[] chisq, double alamda)
	/**
	 * (C) Copr. 1986-92 Numerical Recipes Software 0Lk.^9L.
	 */
	private void mrqmin() {
		int j, k, l;

		if (alamda < 0.0) {
			atry = new double[ma + 1];
			beta = new double[ma + 1];
			da = new double[ma + 1];
			//for (mfit=0,j=1;j<=ma;j++)
			//if (ia[j]) mfit++;
			oneda = new double[mfit + 1][2];
			alamda = 0.001;
			mrqcof(sig, ndata, a, ia, ma, alpha, beta);
			ochisq = chisq;
			for (j = 1; j <= ma; j++)
				atry[j] = a[j];
		}
		for (j = 1; j <= mfit; j++) {
			for (k = 1; k <= mfit; k++)
				covar[j][k] = alpha[j][k];
			covar[j][j] = alpha[j][j] * (1.0 + alamda);
			oneda[j][1] = beta[j];
		}
		gaussj(covar, mfit, oneda, 1);
		for (j = 1; j <= mfit; j++)
			da[j] = oneda[j][1];
		if (alamda == 0.0) {
			covsrt(mfit); //covar,ma,ia,mfit);
			return;
		}
		//System.err.println("da1 "+da[1]);
		//System.err.println("ia1 "+ia[1]);
		for (j = 0, l = 1; l <= ma; l++)
			if (ia[l]) {
				atry[l] = a[l] + da[++j];
			}
		mrqcof(sig, ndata, atry, ia, ma, covar, da);
		if (chisq < ochisq) {
			alamda *= 0.1f;
			ochisq = chisq;
			for (j = 1; j <= mfit; j++) {
				for (k = 1; k <= mfit; k++)
					alpha[j][k] = covar[j][k];
				beta[j] = da[j];
			}
			for (l = 1; l <= ma; l++) {
				//System.err.print(l+"-"+atry[l]+"/");
				a[l] = atry[l];
			}
			//System.err.println();
		} else {
			alamda *= 10.0;
			chisq = ochisq;
		}
		/*  */
	}

	/**
	 * (C) Copr. 1986-92 Numerical Recipes Software 0Lk.^9L.
	 * @param mfit
	 */
	private void covsrt(int mfit) //double[][] covar, int ma, boolean[] ia, int mfit)
	{
		int i, j, k;
		double swap;

		for (i = mfit + 1; i <= ma; i++)
			for (j = 1; j <= i; j++)
				covar[i][j] = covar[j][i] = 0.0;
		k = mfit;
		for (j = ma; j >= 1; j--) {
			if (ia[j]) {
				for (i = 1; i <= ma; i++) {
					swap = covar[i][k];
					covar[i][k] = covar[i][j];
					covar[i][j] = swap;
				}
				for (i = 1; i <= ma; i++) {
					swap = covar[k][i];
					covar[k][i] = covar[j][i];
					covar[j][i] = swap;
				}
				k--;
			}
		}
		/*  */
	}

	/**
	 * (C) Copr. 1986-92 Numerical Recipes Software 0Lk.^9L.
	 * @param a
	 * @param n
	 * @param b
	 * @param m
	 */
	private void gaussj(double[][] a, int n, double[][] b, int m) {
		int[] indxc, indxr, ipiv;
		int i, icol = 0, irow = 0, j, k, l, ll;
		double big, dum, pivinv;
		double swap;

		indxc = new int[n + 1];
		indxr = new int[n + 1];
		ipiv = new int[n + 1];
		for (j = 1; j <= n; j++)
			ipiv[j] = 0;
		for (i = 1; i <= n; i++) {
			big = 0.0;
			for (j = 1; j <= n; j++)
				if (ipiv[j] != 1)
					for (k = 1; k <= n; k++) {
						if (ipiv[k] == 0) {
							if (Math.abs(a[j][k]) >= big) {
								big = Math.abs(a[j][k]);
								irow = j;
								icol = k;
							}
						} else if (ipiv[k] > 1) {
							System.err.println("gaussj: Singular Matrix-1");
							return;
						}
					}
			ipiv[icol]++;
			if (irow != icol) {
				for (l = 1; l <= n; l++) {
					swap = a[irow][l];
					a[irow][l] = a[icol][l];
					a[icol][l] = swap;
				}
				for (l = 1; l <= m; l++) {
					swap = b[irow][l];
					b[irow][l] = b[icol][l];
					b[icol][l] = swap;
				}
			}

			indxr[i] = irow;
			indxc[i] = icol;
			if (a[icol][icol] == 0.0) {
				System.err.println("gaussj: Singular Matrix-2");
				return;
			}
			pivinv = 1.0 / a[icol][icol];
			a[icol][icol] = 1.0;
			for (l = 1; l <= n; l++)
				a[icol][l] *= pivinv;
			for (l = 1; l <= m; l++)
				b[icol][l] *= pivinv;
			for (ll = 1; ll <= n; ll++)
				if (ll != icol) {
					dum = a[ll][icol];
					a[ll][icol] = 0.0;
					for (l = 1; l <= n; l++)
						a[ll][l] -= a[icol][l] * dum;
					for (l = 1; l <= m; l++)
						b[ll][l] -= b[icol][l] * dum;
				}
		}
		for (l = n; l >= 1; l--) {
			if (indxr[l] != indxc[l]) {
				for (k = 1; k <= n; k++) {
					swap = a[k][indxr[l]];
					a[k][indxr[l]] = a[k][indxc[l]];
					a[k][indxc[l]] = swap;
				}
			}
		}
		/* (C) Copr. 1986-92 Numerical Recipes Software 0Lk.^9L. */
	}


	/**
	 * (C) Copr. 1986-92 Numerical Recipes Software 0Lk.^9L.
	 * @param sig
	 * @param ndata
	 * @param a
	 * @param ia
	 * @param ma
	 * @param alpha
	 * @param beta
	 */
	private void mrqcof(
		double[] sig,
		int ndata,
		double[] a,
		boolean[] ia,
		int ma,
		double[][] alpha,
		double[] beta) {
		int i, j, k, l, m;
		double ymod = 0, wt, sig2i, dy;
		double[] dyda;

		dyda = new double[ma + 1];
		//for (j=1;j<=ma;j++)
		//  if (ia[j]) mfit++;
		for (j = 1; j <= mfit; j++) {
			for (k = 1; k <= j; k++)
				alpha[j][k] = 0.0;
			beta[j] = 0.0;
		}
		chisq = 0.0;
		for (i = 1; i <= ndata; i++) {
			ymod = getGradient(x[i], a, dyda, ma);
			sig2i = 1.0 / (sig[i] * sig[i]);
			dy = y[i] - ymod;
			for (j = 0, l = 1; l <= ma; l++) {
				if (ia[l]) {
					wt = dyda[l] * sig2i;
					for (j++, k = 0, m = 1; m <= l; m++)
						if (ia[m])
							alpha[j][++k] += wt * dyda[m];
					beta[j] += dy * wt;
				}
			}
			chisq += dy * dy * sig2i;
		}
		for (j = 2; j <= mfit; j++)
			for (k = 1; k < j; k++)
				alpha[k][j] = alpha[j][k];
		/* (C) Copr. 1986-92 Numerical Recipes Software 0Lk.^9L. */
	}

	/**
	 * Synthesize data from statistical error and see what fit parameters we get
	 * @see http://en.wikipedia.org/wiki/Bootstrapping_(statistics)
	 * @param nIterations
	 */
	public void bootstrap(int nIterations) {
		boolean useSeed = true;
		Random random;
		double[][] paramArray; // store params from synthesized sets
		double[] tmpParamArray;
		
		paramArray = new double[ma][nIterations + 1];
		tmpParamArray = new double[ma];
		// Do an initial fit.
		this.doFit();

		for (int i = 0; i < ma; i++) {
			paramArray[i][0] = a[i + 1];
			tmpParamArray[i] = a[i + 1];
		}
		AbstractFitParameters p = createParameters(tmpParamArray);

		// CALCULATE THE RESIDUALS, COPY ORIGINAL Y-VALUES
		double[] residuals = new double[ndata];
		for (int i = 1; i <= ndata; i++) {
			// CALCULATE RESIDUAL
			residuals[i-1] = eval(x[i], p) - y[i];
			// System.err.println(i+": "+residuals[i-1]);
		}

		// INITIALIZE THE SEED
		if (useSeed) {
			random = new Random(1234);
		} else {
			// create a new seed
			random = new Random();
		}

		// SYNTHESIZE SETS, DO FITS
		for (int j = 0; j < nIterations; j++) {
			// SYNTHESIZE A NEW SET OF DATA
			// FOR EACH POINT, PICK A RANDOM RESIDUAL AND ADD IT TO THE PREDICTED Y-VALUE
			for (int i = 1; i <= ndata; i++) {
				double res = residuals[random.nextInt(residuals.length)];
				y[i] = eval(x[i], p) + res;
			}
			// reset initial guess to first result
			this.initFit(ma);
			this.setInitialGuessForA(p);
			
			// DO A FIT ON THIS DATA
			this.doFit();

			// STORE THE FIT PARAMETERS FOR THE SYNTHESIZED SET
			for (int i = 0; i < ma; i++) {
				//System.err.print("\t"+a[i+1]);
				//System.err.println();
				paramArray[i][j + 1] = a[i + 1];
			}
		}

		// CALCULATE MEAN AND STANDARD DEVIATION FOR EACH PARAMETER
		mean = new double[ma];
		stddev = new double[ma];
		double[] error = new double[ma];
		double[] var = new double[ma];

		// INITIALIZE arrays
		for (int k = 0; k < ma; k++) {
			mean[k] = 0.;
			error[k] = 0.;
			var[k] = 0.;
		}
		// COMPUTE MEAN
		for (int i = 0; i < ma; i++) {
			for (int j = 0; j < nIterations; j++) {
				mean[i] += paramArray[i][j + 1];
			}
			mean[i] /= nIterations;
		}

		// COMPUTE STDDEV
		for (int i = 0; i < ma; i++) {
			for (int j = 0; j < nIterations; j++) {
				double s = paramArray[i][j + 1] - mean[i];
				error[i] += s;
				var[i] += s * s;
			}
		}
		for (int i = 0; i < ma; i++) {
			// System.err.println("M/SD/VAR = "+mean[i]+"\t"+error[i]+"\t"+var[i]);
			stddev[i] =	Math.sqrt((var[i] - (error[i] * error[i] / nIterations))/ (nIterations - 1));
		}

		// debug
//		for (int q = 0; q < ma; q++) {
//			System.err.println(
//				String.valueOf(mean[q] + (1.645 * stddev[q])) + '\t' + String.valueOf(mean[q] - (1.645 * stddev[q]))
//			);
//		}
		
		// RESTORE FIRST FIT INTO A
		for (int i=0; i<ma; i++) {
			a[i+1] = tmpParamArray[i];
		}
	}
}
