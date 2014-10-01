#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#define NRANSI
#include "nr.h"
#include "nrutil.h"
#include "loglet.h"

/* This function takes the raw data, x and y, the predicted data, xfit and yfit,
 * and the vector of parameters avec and calculates the residuals
 * at each point. 
 */
void nlogis(float *x, float *y,int n,float *avec,int ma,float xstart,float xstop,
				int numpoints, float *xfit, float *yfit, float *resid) {

	int i,j,m,numlogs;
	float a,k,tm,step,*y_of_x;

	y_of_x = vector(1,n);

	if (xstart == 0) xstart = x[1];
	if (xstop == 0) xstop = x[n];

	numlogs = ma / 3;

	a = 0.0;
	k = 0.0;
	tm = 0.0;
	
	step = (xstop - xstart) / numpoints;
	for (i=1;i<=numpoints;i++) yfit[i] = 0.0;
	for (i=1;i<=n;i++) y_of_x[i] = 0.0;

				
	for (j=1,i=1;i<=numlogs;i++) {
		a = avec[j];		
		k = avec[j+1];
		tm = avec[j+2];

		for (m=1;m<=numpoints;m++)	{
			xfit[m] = xstart + (step * m);
			yfit[m] += (k) / ( 1 + exp( -1.0 * a * ( xfit[m] - tm)));
		}
		
		for (m=1;m<=n;m++)	{
			y_of_x[m] += (k) / ( 1 + exp( -1.0 * a * ( x[m] - tm)));
		}
		j+=3;
	}

	for (i=1;i<=n;i++) resid[i] = y[i] - y_of_x[i];
	

}

