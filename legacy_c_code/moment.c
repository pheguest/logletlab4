#include <math.h>

void moment(float data[], int n, float *ave, float *adev, float *sdev,
	float *var, float *skew, float *curt)
{
	void nrerror(char error_text[]);
	int j;
	float ep=0.0,s,p;

	if (n <= 1) nrerror("n must be at least 2 in moment");
	s=0.0;
	for (j=1;j<=n;j++) s += data[j];
	*ave=s/n;
	*adev=(*var)=(*skew)=(*curt)=0.0;
	for (j=1;j<=n;j++) {
		*adev += fabs(s=data[j]-(*ave));
		ep += s;
		*var += (p=s*s);
		*skew += (p *= s);
		*curt += (p *= s);
	}
	*adev /= n;
	*var=(*var-ep*ep/n)/(n-1);
	*sdev=sqrt(*var);
	if (*var) {
		*skew /= (n*(*var)*(*sdev));
		*curt=(*curt)/(n*(*var)*(*var))-3.0;
	} else nrerror("No skew/kurtosis when variance = 0 (in moment)");
}
/* (C) Copr. 1986-92 Numerical Recipes Software 0Lk.^9L. */
