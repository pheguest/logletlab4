#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#define NRANSI
#include "nr.h"
#include "nrutil.h"
#include "loglet.h"


/* 3-15-97, Jason finds and error !!!!!!! */
void log_f1(float t,float p[],float *yfit,float dy_dp[],int ma){

	int i,j,num_logs;
	
	num_logs = ma / 3;


	*yfit = 0.0;
	j=1;
	
	for (i=1;i<=num_logs;i++) {
		*yfit += (p[j+1]) / ( 1 + exp(-1.0 * p[j] * ( t - p[j+2])));
		dy_dp[j] = ( p[j+1] * (t - p[j+2]) * exp(-p[j]*(t-p[j+2])) )/( (1+exp(-p[j]*(t-p[j+2]))) * (1+exp(-p[j]*(t-p[j+2]))) );
		dy_dp[j+1] = 1 / (1+exp(-p[j]*(t-p[j+2])));
		/*		dy_dp[j+2] = ( -p[j+1] * p[j] * exp(-p[j]*t) * exp(p[j]*p[j+2]) ) / ( (1+exp(-p[j]*(t-p[j+2]))) * (1+exp(-p[j]*(t-p[j+2]))) ); */
		dy_dp[j+2] = ( -p[j+1] * p[j] * exp(-p[j]*(t-p[j+2]))) / ( (1+exp(-p[j]*(t-p[j+2]))) * (1+exp(-p[j]*(t-p[j+2]))) ); 

		j+=3;
	}
}
