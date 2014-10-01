#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#define NRANSI
#include "nr.h"
#include "nrutil.h"
#include "loglet.h"

float logfunc(float x,float *avec,int ma) {

  int i,j,m,numlogs;
  float a,k,tm,step,y_of_x;

  numlogs = ma / 3;

  a = 0.0;
  k = 0.0;
  tm = 0.0;

  y_of_x = 0.0;
	
  for (j=1,i=1;i<=numlogs;i++) {
    a = avec[j];		
    k = avec[j+1];
    tm = avec[j+2];


    y_of_x += (k) / ( 1 + exp( -1.0 * a * ( x - tm)));

    j+=3;
  }

  return y_of_x;
	

}
