#include <string.h>
#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#define NRANSI
#include "nr.h"
#include "nrutil.h"
#include "loglet.h"


void main_loglet_fitting(float xclip[], 
			 float yclip[], 
			 float sigclip[], 
			 int nclip, 
			 float a[], 
			 int ia[],
			 int ma, 
			 float **covar, 
			 float **alpha, 
			 float chisq,
			 void (*funcs)(float, float [], float *, float [], int)
			 ){


  int iter,itst;
  float alamda;
  float ochisq;
  /******MAIN FITTING ROUTINE*********/					 

  alamda = -1.0; /* used to initialize mrqmin */
  mrqmin(xclip,yclip,sigclip,nclip,a,ia,ma,covar,alpha,&chisq,log_f1,&alamda);

  iter=1;
  itst=0;
  for(;;){

    iter++;
    ochisq = chisq;
    mrqmin(xclip,yclip,sigclip,nclip,a,ia,ma,covar,alpha,&chisq,log_f1,&alamda);
    if (chisq > ochisq)
      itst =0;
    else if (fabs(ochisq - chisq) < 0.1)
      itst++;
    if(itst < 4) continue;
    alamda = 0.0;
    mrqmin(xclip,yclip,sigclip,nclip,a,ia,ma,covar,alpha,&chisq,log_f1,&alamda);
    break;
  }
  /*****************END MAIN FITTING ROUTINE**************************/




}
