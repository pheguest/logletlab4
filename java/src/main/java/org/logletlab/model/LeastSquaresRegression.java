/*
 * Copyright (c) 2004, Program for the Human Environment, The Rockefeller University.
 * except where otherwise noted. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer. Redistributions in binary
form must reproduce the above copyright notice, this list of conditions and
the following disclaimer in the documentation and/or other materials
provided with the distribution. Neither the name of The Program for the
Human Environment or The Rockefeller University nor the names of its 
contributors may be used to endorse or promote products derived from this
software without specific prior written permission. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
DAMAGE.
 */
package org.logletlab.model;

public class LeastSquaresRegression {
	/**
	 * As described in <i>Numerical Recipes</i> (Chp. 15.2, p.665):
	 * <blockquote>
	 * Given a set of data points x[1..ndata],y[1..ndata] with individual
	 * standard deviations sig[1..ndata], fit them to a straight line y = a + bx
	 * by minimizing chi-squared. Returned are a,b and their respective probable
	 * uncertainties siga and sigb, the chi-square chi2, and the goodness-of-fit
	 * probability q (that the fit would have chi^2 this large or larger).
	 * If mwt=0 on input, then the standard deviations are assumed to be
	 * unavailable: q is returned as 1.0 and the normalization of chi2 is to unit
	 * standard deviation on all points.
	 * </blockquote>
	 * 
	 * @param x
	 * @param y
	 * @return [a,b,siga,sigb,chi2,q] as an array of six doubles.
	 */
	public static double[] leastSq(double[] x, double[] y, double[] sig, int nPts)
	{
		int i;
		double a,b;
		double siga, sigb, chi2, q;
		double wt,t,sxoss,sx=0.0,sy=0.0,st2=0.0,ss,sigdat;
		
		b=0.0;
		if (sig != null) {
			ss=0.0;
			for (i=1;i<=nPts;i++) {
				wt=1.0/Math.pow(sig[i],2);
				ss += wt;
				sx += x[i]*wt;
				sy += y[i]*wt;
			}
		} else {
			for (i=1;i<=nPts;i++) {
				sx += x[i];
				sy += y[i];
			}
			ss=nPts;
		}
		sxoss=sx/ss;
		if (sig != null) {
			for (i=1;i<=nPts;i++) {
				t=(x[i]-sxoss)/sig[i];
				st2 += t*t;
				b += t*y[i]/sig[i];
			}
		} else {
			for (i=1;i<=nPts;i++) {
				t=x[i]-sxoss;
				st2 += t*t;
				b += t*y[i];
			}
		}
		b /= st2;
		a=(sy-sx*(b))/ss;
		siga=Math.sqrt((1.0+sx*sx/(ss*st2))/ss);
		sigb=Math.sqrt(1.0/st2);
		chi2=0.0;
		q=1.0;
		if (sig == null) {
			for (i=1;i<=nPts;i++)
				chi2 += Math.pow(y[i]-a-b*x[i],2);
			sigdat=Math.sqrt(chi2/(nPts-2));
			siga *= sigdat;
			sigb *= sigdat;
		} else {
			for (i=1;i<=nPts;i++)
				chi2 += Math.pow((y[i]-a-b*x[i])/sig[i],2);
			if (nPts>2)
				q=gammq(0.5*(nPts-2),0.5*(chi2));
		}
		double ab[] = {a, b, siga, sigb, chi2, q};
		return ab;
	}
	/* (C) Copr. 1986-92 Numerical Recipes Software 0Lk.^9L. */
	
	
	private static double gammq(double a, double x)
	{
		//double gamser,gammcf,gln;
		if (x < 0.0 || a <= 0.0) 
			System.err.println("Invalid arguments in routine gammq");
		if (x < (a+1.0)) {
			return 1.0-gser(a,x);
		} else {
			return gcf(a,x);
		}
	}
	/* (C) Copr. 1986-92 Numerical Recipes Software 0Lk.^9L. */
	
	static final int ITMAX=100;
	static final double EPS=3.0e-7;
	static final double FPMIN=Double.MIN_VALUE;//1.0e-30;
	
	private static double gcf(double a, double x)
	{
		double gln;
		int i;
		double an,b,c,d,del,h;
	
		gln=gammln(a);
		b=x+1.0-a;
		c=1.0/FPMIN;
		d=1.0/b;
		h=d;
		for (i=1;i<=ITMAX;i++) {
			an = -i*(i-a);
			b += 2.0;
			d=an*d+b;
			if (Math.abs(d) < FPMIN) d=FPMIN;
			c=b+an/c;
			if (Math.abs(c) < FPMIN) c=FPMIN;
			d=1.0/d;
			del=d*c;
			h *= del;
			if (Math.abs(del-1.0) < EPS) break;
		}
		if (i > ITMAX) 
			System.err.println("a too large, ITMAX too small in gcf");
			
		return Math.exp(-x+a*Math.log(x)-(gln))*h;
	}
	/* (C) Copr. 1986-92 Numerical Recipes Software 0Lk.^9L. */
	
	private static double gser(double a, double x)
	{
		int n;
		double sum,del,ap;
	
		double gln=gammln(a);
		if (x <= 0.0) {
			if (x < 0.0) 
				System.err.println("x less than 0 in routine gser");
			return 0.0;
		} else {
			ap=a;
			del=sum=1.0/a;
			for (n=1;n<=ITMAX;n++) {
				++ap;
				del *= x/ap;
				sum += del;
				if (Math.abs(del) < Math.abs(sum)*EPS) {
					return sum*Math.exp(-x+a*Math.log(x)-(gln));
				}
			}
			System.err.println("a too large, ITMAX too small in routine gser");
			return 0.0;
		}
	}
	/* (C) Copr. 1986-92 Numerical Recipes Software 0Lk.^9L. */
	
	private static double cof[]={76.18009172947146,-86.50532032941677,
		24.01409824083091,-1.231739572450155,
		0.1208650973866179e-2,-0.5395239384953e-5};

	private static double gammln(double xx)
	{
		double x,y,tmp,ser;
		int j;
	
		y=x=xx;
		tmp=x+5.5;
		tmp -= (x+0.5)*Math.log(tmp);
		ser=1.000000000190015;
		for (j=0;j<=5;j++) ser += cof[j]/++y;
		return -tmp+Math.log(2.5066282746310005*ser/x);
	}
	/* (C) Copr. 1986-92 Numerical Recipes Software 0Lk.^9L. */


}
