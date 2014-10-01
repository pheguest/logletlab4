
	/******MAIN FITTING ROUTINE*********/
	covar = matrix(1,ma,1,ma);
	alpha = matrix(1,ma,1,ma);
					 
	alamda = -1.0; /* used to initialize mrqmin */
	mrqmin(xclip,yclip,sigclip,nclip,a,ia,ma,covar,alpha,&chisq,log_f1,&alamda);

	iter=1;
	itst=0;
	for(;;){
		
		/*******NOISY option********/
		if (noisy_flag == 1)	{
			for (j=1;j<=num_logs * 3;j+=3) {
				printf("%2.3f\t",log(81)/a[j]);
				printf("%2.3f\t",a[j+1]);
				printf("%2.3f\t",a[j+2]);
				printf("\n");
			}
			printf("\n");
		}
		/***************************/

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
