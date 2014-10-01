void intro(void);
void log_f1(float t,float p[],float *yfit,float dy_dp[],int ma);
void nlogis(float *x, float *y,int n,float *avec,int ma,float xstart,float xstop,
				int numpoints, float *xfit, float *yfit, float *resid);


float logfunc(float x,float *avec,int ma);


