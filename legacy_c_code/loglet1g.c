#include <string.h>
#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#define NRANSI
#include "nr.h"
#include "nrutil.h"
#include "loglet.h"

char version[] = "Loglet1g  24 August 1994 (c 3-15-97), Perrin Meyer, The Rockefeller University."; 
char program_name[] = "loglet1g";

void intro(void) {
	system("cls");
	printf("%s\n\n",version);
	printf("%s <script_file> \n\n",program_name);
	printf("-----example script file-----------------\n\n");
	printf("-data                       \n");
	printf("{                           \n");
	printf("1950  12  1                  \n");
	printf("1960  13  0.3  //weights are optional       \n");
	printf("1970  14  etc ...           \n");
	printf("}          \n\n");
	printf("-initial  //initial paramter estimates         \n");
	printf("{                           \n");
	printf("dt1 k1 tm1 1 1 1                    \n");
	printf("dt2 k2 tm2 0 0 1 etc ...                   \n");
	printf("}          \n");
	printf("-plot  //plot with gnuplot        \n");
	printf("-save [4 characters max]   // default is first 4 chars of script_file ;   \n");
	printf("-curve xstart xstop [numfitpoints]         \n");
	printf("-fitrange xclipstart xclipstop    \n");
	printf("-displacement disp    \n");

	printf("\n-----PRESS ENTER TO CONTINUE------------");
	
	while(!getchar());
	system("cls");

	printf("-noisy    prints stuff as program is running\n\n");
	printf("------ extension, variables, length ------------\n\n");
	printf(" _fit     xfit[], yfit[]                  (numfitpoints)   \n");
	printf(" _res     x[], resid[], resid_per_dev[]   (n)  \n");
	printf(" _dat     x[],y[],sig[]                   (n)    \n");
	printf("     \n");
	printf(" _inm     initial_matrix[][]              (num_logs,6) \n");
	printf(" _fim     final_matrix[][]                (num_logs,6) \n");
	printf("             one pulse per row; dt,k,tm; 1 = fit, 0 = hold  \n");
	printf("     \n");
	printf(" _fpd     fisher_pry_data[][]             (n,num_logs * 2)    \n");
	printf(" _fpl     fisher_pry_line[][]             (100,num_logs * 2)    \n");
	printf("              x1[],fp1[],x2[],fp2[] etc ...  \n");
	printf("          (if k < 0, a,k are multiplied by -1\n");
	printf("          (if k < 0, fpd += fabs(k))\n");
	printf(" -fpr     fisher_pry_data_raw[][]         (n,num_logs * 2)  \n");
	printf("               raw data for component logistics \n");
	printf(" -com     component_logistic[][]          (100,num_logs *2)\n");
	printf("                                                     \n");
	printf(" _dis     disp                            (displacement)    \n");
	printf(" _rng     xclipstart,xclipstop            (range)    \n");

}


int main(int argc,char *argv[]){

	char *script_file,*g,*s[100],*ct;
	char *dm[2000][30],*save_char,*save_fit,*save_res,*save_fpd,*save_fpl;
	char *save_dat,*save_inm,*save_fim,*save_dis,*save_rng,*save_fpr,*save_com;
	int toks_per_line[500],j,i,ctemp,tks,numlines;
	FILE *fp,*fp_data_gnu,*fp_xyfit,*fp_plot_gnu;
	FILE *fp_save_fit,*fp_save_res,*fp_save_fpd,*fp_save_fpl;
	FILE *fp_save_dat,*fp_save_inm,*fp_save_fim,*fp_save_char,*fp_save_dis;
	FILE *fp_save_rng,*fp_save_fpr,*fp_save_com;
	float *x,*y,*sig,**initial_matrix,*a,**fisher_pry_data_raw;
	int n,k,data_start,num_logs,*ia,ma;
	float **covar,alamda,chisq,**alpha,ochisq,**fit_matrix;
	float *xfit,*yfit,*resid,*resid_per_dev,xstart,xstop,disp;
	float **fisher_pry_data,**fisher_pry_line,sl,step;
	float *xclip,*yclip,*sigclip,xclipstart,xclipstop,**fp_matrix;
	float **component_logistic;
	int iter,itst,numfitpoints,fitrangeflag,nclip;
	int plot_flag,save_flag,column,wlog,m,noisy_flag,tmp;
	
	if (argc == 1) {
		intro();
		exit(0);
	}

	script_file=calloc(20,sizeof(char));
	script_file = strcpy(script_file,argv[1]);


	
	/**** anounce to outside world that program has started *****/
	printf("\n%s\n",version);
	printf("\nscript file = %s\n",script_file);


	/*******************START FILE PARSING******************************/

	/*CREATE dm[][], MATRIX OF TOKENS IN FILE*/
	fp=fopen(script_file,"ra");
	g =calloc(100,sizeof(char));
	i=0;
	while( fgets(g,100,fp) != NULL ) {
		if	(g[0] == '/' && g[1] == '/') continue;
		if (g[0] == '\n') continue;
		ct=" \t,\n";
		s[0] = strtok(g,ct);
		for (j=1;(s[j] = strtok(NULL,ct)) != NULL;j++);
		ctemp = j;
		toks_per_line[i] = 0;
		for(j=0;j<ctemp;j++) {
			if (s[j][0] == '/' && s[j][1] == '/') break;
			dm[i][j] = calloc(50,sizeof(char));
			strcpy(dm[i][j],s[j]);
			toks_per_line[i] ++;
		}

		i++;
	}
	numlines = i;
	fclose(fp);

	/*****FILE PARSING FLAG VALUES*****/
	n=0;
	num_logs = 0;
	save_flag = 0;
	numfitpoints = 200;
	xstart = 0.0;	    /* i.e. x[1]	from -curve*/
	xstop = 0.0;		 /* i.e. x[n]  */
	xclipstart = 0.0;   /*fit on all data, from -fitrange*/
	xclipstop  = 0.0;   /*fit on all data */
	plot_flag = 0;	 
	disp = 0.0;     /* displacement value default, of course, is zero */
	fitrangeflag = 0;
	noisy_flag = 0;

	/***READ IN DATA***/
	for (i=0;i<numlines;i++) {
		if (strcmp(dm[i][0],"-data")==0)	{
			i+=2 ;
			data_start = i;
			for (;;) {
				if (strcmp(dm[i][0],"}") == 0) break;
				n++;
				i++;
			}
			x = vector(1,n);
			y = vector(1,n);
			sig = vector(1,n);
			i=data_start;
			k=1;
			for (;;)	{
				if (strcmp(dm[i][0],"}") == 0) break;
				x[k] = atof(dm[i][0]);
				y[k] = atof(dm[i][1]);
				if (toks_per_line[i] == 3) sig[k]  = atof(dm[i][2]);
					else sig[k]	= 1.0;
				k++;
				i++;
			}
		}
	}
	
	/***READ IN INTIAL PARAMETER ESTIMATES MATRIX***/
	for (i=0;i<numlines;i++) {
		if (strcmp(dm[i][0],"-initial")==0)	{
			i+=2 ;
			data_start = i;
			for (;;) {
				if (strcmp(dm[i][0],"}") == 0) break;
				num_logs++;
				i++;
			}
			initial_matrix = matrix(1,num_logs,1,6);
			i=data_start;
			k=1;
			for (;;)	{
				if (strcmp(dm[i][0],"}") == 0) break;
				initial_matrix[k][1] = atof(dm[i][0]);
				initial_matrix[k][2] = atof(dm[i][1]);
				initial_matrix[k][3] = atof(dm[i][2]);

				if (toks_per_line[i] == 6) {
					initial_matrix[k][4] = atof(dm[i][3]);
					initial_matrix[k][5] = atof(dm[i][4]);
					initial_matrix[k][6] = atof(dm[i][5]);
				}
				else {
					initial_matrix[k][4] = 1.0;
					initial_matrix[k][5] = 1.0;
					initial_matrix[k][6] = 1.0;
				}
				k++;
				i++;
			}
		}
	}
	
	/*****READ IN PLOT PARAMETERS*****/
	for (i=0;i<numlines;i++) {
		if (strcmp(dm[i][0],"-plot")==0) {
			plot_flag = 1;
		}
	}
	
	/*****READ IN NOISY PARAMETERS*****/
	for (i=0;i<numlines;i++) {
		if (strcmp(dm[i][0],"-noisy")==0) {
			noisy_flag = 1;
		}
	}

	/*****READ IN DISPLACEMENT PARAMETER*****/
	for (i=0;i<numlines;i++) {
		if (strcmp(dm[i][0],"-displacement")==0) {
			disp = atof(dm[i][1]);
		}
	}
	
	/*****READ IN FITRANGE PARAMETERS*****/
	for (i=0;i<numlines;i++) {
		if (strcmp(dm[i][0],"-fitrange")==0) {
			fitrangeflag = 1;
			xclipstart= atof(dm[i][1]);
			xclipstop = atof(dm[i][2]);
		}
	}

	/*****READ IN 4 CHARACTERS FOR SAVE FUNCTION*****/
	for (i=0;i<numlines;i++) {
		if (strcmp(dm[i][0],"-save")==0) {
			save_flag = 1;
			save_char = calloc(5,sizeof(char));
			if (toks_per_line[i] == 1)	{
				if (strlen(script_file) < 8) save_char = strncpy(save_char,script_file,strlen(script_file)-4);
					else save_char = strncpy(save_char,script_file,4);
			}
			else save_char = strcpy(save_char,dm[i][1]);
		}
	}
		
	/*****READ IN CURVE PARAMETERS*****/
	for (i=0;i<numlines;i++) {
		if (strcmp(dm[i][0],"-curve")==0) {
			if (toks_per_line[i] >= 3) {;
				xstart = atof(dm[i][1]);
				xstop = atof(dm[i][2]);
				if (toks_per_line[i] == 3) numfitpoints = 200; 
					else numfitpoints = atoi(dm[i][3]);
			}
		}
	}
	
	/*******END FILE PARSING**********************************************/


	/******* SUBTRACT DISPLACEMENT VALUE FROM Y[] IF disp != ZERO ******/
	if (disp != 0)	{
		for (i=1;i<=n;i++) y[i] -= disp;
	}

	/***CREATING a[] and ia[] (note converting dt to a) ***/
	ma = num_logs * 3;
	a=vector(1,ma);
	ia=ivector(1,ma);

	for (i=1,j=1;i<=num_logs;i++) {
		a[j]   = log(81) / initial_matrix[i][1] ;
		a[j+1] = initial_matrix[i][2] ;
		a[j+2] = initial_matrix[i][3] ;

		ia[j]   = (int)initial_matrix[i][4];
		ia[j+1] = (int)initial_matrix[i][5];
		ia[j+2] = (int)initial_matrix[i][6];
		
		j += 3;
	}

	/*****CREATE XCLIP,YCLIP,SIGCLIP,NCLIP************/
	if (fitrangeflag == 0) {
		xclip = vector(1,n);
		yclip = vector(1,n);
		sigclip = vector(1,n);
		nclip = n;

		for (i=1;i<=n;i++) {
			xclip[i] = x[i];
			yclip[i] = y[i];
			sigclip[i] = sig[i];
		}
	}
	if (fitrangeflag == 1) {
		for (nclip=0,i=1;i<=n;i++)	{
			if (x[i] >= xclipstart && x[i] <= xclipstop)	nclip++;
		}
		xclip = vector(1,nclip);
		yclip = vector(1,nclip);
		sigclip = vector(1,nclip);

		for (j=1,i=1;i<=n;i++) {
			if (x[i] >= xclipstart && x[i] <= xclipstop)	{
				xclip[j] = x[i];
				yclip[j] = y[i];
				sigclip[j] = sig[i];
				j++;
			}
		}
	}

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

	if (noisy_flag ==1) printf("\nFinished Main Fitting Routine\n");

	/****CREATING NEW MATRIX WITH FIT VALUES*****/
	fit_matrix = matrix(1,num_logs,1,6);
	for (i=1,j=1;i<=num_logs;i++) {
		fit_matrix[i][1] = log(81) / a[j];
		fit_matrix[i][2] = a[j+1];
		fit_matrix[i][3] = a[j+2];
		fit_matrix[i][4] = initial_matrix[i][4];
		fit_matrix[i][5] = initial_matrix[i][5];
		fit_matrix[i][6] = initial_matrix[i][6];
		j += 3;
	}

	/*****CALL NLOGIS TO GENERATE FIT AND RESIDUALS******/
	
	xfit = vector(1,numfitpoints);
	yfit = vector(1,numfitpoints);
	resid = vector(1,n);
	resid_per_dev = vector(1,n);

	nlogis(x,y,n,a,ma,xstart,xstop,numfitpoints,xfit,yfit,resid);

	for (i=1;i<=n;i++) resid_per_dev[i] = (resid[i] / y[i]) * 100.0;

	/***********CREATE FISHER_PRY_DATA[] AND FISHER_PRY_LINE[]********/
	fisher_pry_data = matrix(1,n,1,num_logs * 2);
	fisher_pry_data_raw = matrix(1,n,1,num_logs * 2);
	fisher_pry_line = matrix(1,100,1,num_logs * 2);
	component_logistic = matrix(1,100,1,num_logs * 2);

	fp_matrix = matrix(1,num_logs,1,3);

	sl = 1.2 ;  /* slice factor * dt  */

	/***** change sign of parameters to make fisher-pry lines look right *****/
	for (i=1;i<=num_logs;i++) {
		fp_matrix[i][1] = fit_matrix[i][1];
  		fp_matrix[i][2] = fit_matrix[i][2];
  		fp_matrix[i][3] = fit_matrix[i][3];

		if (fit_matrix[i][1] > 0.0 && fit_matrix[i][2] < 0.0)	{
			fp_matrix[i][1] = -1.0 * fit_matrix[i][1];
			fp_matrix[i][2] = -1.0 * fit_matrix[i][2];
			fp_matrix[i][3] =        fit_matrix[i][3];
		}
		if (fit_matrix[i][1] < 0.0 && fit_matrix[i][2] < 0.0)	{
			fp_matrix[i][1] = -1.0 * fit_matrix[i][1];
			fp_matrix[i][2] = -1.0 * fit_matrix[i][2];
			fp_matrix[i][3] =        fit_matrix[i][3];
		}
	}

	for (column = 1,wlog = 1;wlog <= num_logs; wlog++, column+=2) {
		for (j=1,i=1;i<=n;i++) {
			if ( (x[i] > (fit_matrix[wlog][3] - fabs(fit_matrix[wlog][1]*sl))) && (x[i] < (fit_matrix[wlog][3] + fabs(fit_matrix[wlog][1]*sl)))) {
				fisher_pry_data[j][column] = x[i];
				fisher_pry_data[j][column+1] = y[i];
				for (m=1;m<=num_logs;m++) {
					if (m != wlog) fisher_pry_data[j][column+1] -= (fit_matrix[m][2]) / ( 1 + exp(-1.0 * (log(81) / fit_matrix[m][1]) * (x[i] - fit_matrix[m][3])));
				}
				
				/***this looks silly,but this came second****/
				fisher_pry_data_raw[j][column] = x[i];
				fisher_pry_data_raw[j][column+1] = fisher_pry_data[j][column+1];
				
				if (fit_matrix[wlog][2] < 0.0 ) fisher_pry_data[j][column+1] += -1.0 * fit_matrix[wlog][2];
				if (fisher_pry_data[j][column+1] <= 0.0 || fisher_pry_data[j][column+1] >= fabs(fit_matrix[wlog][2]))	fisher_pry_data[j][column+1] = 0.0;
					else 	fisher_pry_data[j][column+1] = fisher_pry_data[j][column+1] / (fabs(fit_matrix[wlog][2]) - fisher_pry_data[j][column+1]);
				
				j++;
			}
		}
		if (j <= n) {
			for (;j<=n;j++) {
				fisher_pry_data[j][column] = 0.0;
				fisher_pry_data[j][column+1] = 0.0;
				fisher_pry_data_raw[j][column+1] = 0.0;
				fisher_pry_data_raw[j][column+1] = 0.0;
			}
		}
	}

	for (column = 1,wlog = 1;wlog <= num_logs; wlog++, column+=2) {
		step = 2.0 * sl * fp_matrix[wlog][1] / 100.0;
		fisher_pry_line[1][column] = fp_matrix[wlog][3] - sl * fp_matrix[wlog][1];
		fisher_pry_line[1][column+1] = 1.0 / (1 + exp(-1.0 * (log(81) / fp_matrix[wlog][1]) * (fisher_pry_line[1][column] - fp_matrix[wlog][3])));
		for (i=2;i<=100;i++)	{
			fisher_pry_line[i][column] = fisher_pry_line[i-1][column] + step ;
			fisher_pry_line[i][column+1] = 1.0 / (1 + exp(-1.0 * (log(81) / fp_matrix[wlog][1]) * (fisher_pry_line[i][column] - fp_matrix[wlog][3])));
		}
		for (i=1;i<=100;i++) fisher_pry_line[i][column+1] = fisher_pry_line[i][column+1] / ( 1 - fisher_pry_line[i][column+1]);
		
		step = 2.0 * sl * fabs(fit_matrix[wlog][1]) / 100.0;
		component_logistic[1][column] = fit_matrix[wlog][3] - sl * fabs(fit_matrix[wlog][1]);
		component_logistic[1][column+1] = fit_matrix[wlog][2] / (1 + exp(-1.0 * (log(81) / fit_matrix[wlog][1]) * (component_logistic[1][column] - fit_matrix[wlog][3])));
	  	for (i=2;i<=100;i++)	{
			component_logistic[i][column] = component_logistic[i-1][column] + step ;
			component_logistic[i][column+1] = fit_matrix[wlog][2] / (1 + exp(-1.0 * (log(81) / fit_matrix[wlog][1]) * (component_logistic[i][column] - fit_matrix[wlog][3])));
		}	
	}
	
	/*** ADD DISPLACEMENT VALUE BACK IF != ZERO  ******/
	if (disp != 0.0) {
		for (i=1;i<=n;i++) y[i] += disp;
		for (i=1;i<=numfitpoints;i++) yfit[i] += disp;
	}

  
	/*********PLOT STUFF*************************************************/
	if (plot_flag == 1) {
		fp_xyfit = fopen("xyfit.tmp","wa");
		fp_data_gnu = fopen("data_gnu.tmp","wa");
		fp_plot_gnu = fopen("plot_gnu.tmp","wa");
		
		for (i=1;i<=numfitpoints;i++)fprintf(fp_xyfit,"%f\t%f\n",xfit[i],yfit[i]);
		for (i=1;i<=n;i++)fprintf(fp_data_gnu,"%f\t%f\n",x[i],y[i]);
		
		system("cls");
		printf("%s\n\n\n\n",version);
		if (disp != 0 ) printf("DISPLACEMENT VALUE = %f ",disp);
		
		printf("\n\n----Initial Matrix------\n");
		for (i=1;i<=num_logs;i++) {
			for (j=1;j<=6;j++) {
				printf("%2.2f\t",initial_matrix[i][j]);
			}
			printf("\n");
		}
		printf("\n");
		printf("----Fit Matrix------\n");
		for (i=1;i<=num_logs;i++) {
			for (j=1;j<=6;j++) {
				printf("%2.2f\t",fit_matrix[i][j]);
			}
			printf("\n");
		}
		if (fitrangeflag == 0) printf("\n\nFIT ON ALL DATA \n");
		if (fitrangeflag == 1) printf("\n\nFIT RANGE FROM %2.3f to %2.3f \n",xclipstart,xclipstop);
	
		printf("\n\nHIT ENTER TO PLOT");
		while(!getchar());

		fprintf(fp_plot_gnu,"set title \"%s    Script File = %s\n",program_name,argv[1]);
		if (fitrangeflag == 0 ) fprintf(fp_plot_gnu,"set xlabel \"FIT ON ALL DATA \" \n");
		if (fitrangeflag == 1 ) fprintf(fp_plot_gnu,"set xlabel \"FIT RANGE FROM = %2.3f to %2.3f \" \n",xclipstart,xclipstop);
		if (disp != 0 ) fprintf(fp_plot_gnu,"set ylabel \"DISPLACEMENT VALUE = %f\" \n",disp);
		fprintf(fp_plot_gnu,"set nokey\n");
		fprintf(fp_plot_gnu,"set noyzeroaxis\n");
		fprintf(fp_plot_gnu,"plot \"data_gnu.tmp\" with points ");
		fprintf(fp_plot_gnu,", \"xyfit.tmp\" with line ");
		fprintf(fp_plot_gnu,"\n");
		fprintf(fp_plot_gnu,"pause -1\n");

		fclose(fp_plot_gnu);
		fclose(fp_data_gnu);
		fclose(fp_xyfit);


		system("gnuplot plot_gnu.tmp");

	} /* plot_flag == 1 */

	/*******************  SAVE FUNCTION  *******************/
	if (save_flag == 1) {
		save_fit = calloc(15,sizeof(char));
		save_res = calloc(15,sizeof(char));
		save_fpd = calloc(15,sizeof(char));
		save_fpl = calloc(15,sizeof(char));
		save_dat = calloc(15,sizeof(char));
		save_inm = calloc(15,sizeof(char));
		save_fim = calloc(15,sizeof(char));
		save_dis = calloc(15,sizeof(char));
		save_rng = calloc(15,sizeof(char));
		save_fpr = calloc(15,sizeof(char));
		save_com = calloc(15,sizeof(char));

		sprintf(save_fit,"%s_fit.asc",save_char);
		sprintf(save_res,"%s_res.asc",save_char);
		sprintf(save_fpd,"%s_fpd.asc",save_char);
		sprintf(save_fpl,"%s_fpl.asc",save_char);
		sprintf(save_dat,"%s_dat.asc",save_char);
		sprintf(save_inm,"%s_inm.asc",save_char);
		sprintf(save_fim,"%s_fim.asc",save_char);
		sprintf(save_dis,"%s_dis.asc",save_char);
		sprintf(save_rng,"%s_rng.asc",save_char);
		sprintf(save_fpr,"%s_fpr.asc",save_char);
		sprintf(save_com,"%s_com.asc",save_char);
	
		fp_save_fit = fopen(save_fit,"wa");
		fp_save_res = fopen(save_res,"wa");
		fp_save_fpd = fopen(save_fpd,"wa");
		fp_save_fpl = fopen(save_fpl,"wa");
		fp_save_dat = fopen(save_dat,"wa");
		fp_save_inm = fopen(save_inm,"wa");
		fp_save_fim = fopen(save_fim,"wa");
		fp_save_dis = fopen(save_dis,"wa");
		fp_save_rng = fopen(save_rng,"wa");
		fp_save_fpr = fopen(save_fpr,"wa");
		fp_save_com = fopen(save_com,"wa");
	
		for (i=1;i<=numfitpoints;i++)fprintf(fp_save_fit,"%f\t%f\n",xfit[i],yfit[i]);
		for (i=1;i<=n;i++)fprintf(fp_save_res,"%f\t%f\t%f\n",x[i],resid[i],resid_per_dev[i]);
		for (i=1;i<=n;i++) {
			for (j=1;j<=num_logs * 2;j++)	fprintf(fp_save_fpd,"%f\t",fisher_pry_data[i][j]);
			fprintf(fp_save_fpd,"\n");
		}
		for (i=1;i<=100;i++) {
			for (j=1;j<=num_logs * 2;j++)	fprintf(fp_save_fpl,"%f\t",fisher_pry_line[i][j]);
			fprintf(fp_save_fpl,"\n");
		}
		for (i=1;i<=n;i++) fprintf(fp_save_dat,"%f\t%f\t%f\n",x[i],y[i],sig[i]);

		for (i=1;i<=num_logs;i++) {
			for (j=1;j<=6;j++) {
				fprintf(fp_save_inm,"%f\t",initial_matrix[i][j]);
			}
			fprintf(fp_save_inm,"\n");
		}
		for (i=1;i<=num_logs;i++) {
			for (j=1;j<=6;j++) {
				fprintf(fp_save_fim,"%f\t",fit_matrix[i][j]);
			}
			fprintf(fp_save_fim,"\n");
		}
		fprintf(fp_save_dis,"%f\n",disp);
		fprintf(fp_save_rng,"%f\t%f\n",xclipstart,xclipstop);
		for (i=1;i<=n;i++) {
			for (j=1;j<=num_logs * 2;j++)	fprintf(fp_save_fpr,"%f\t",fisher_pry_data_raw[i][j]);
			fprintf(fp_save_fpr,"\n");
		}
		for (i=1;i<=100;i++) {
			for (j=1;j<=num_logs * 2;j++)	fprintf(fp_save_com,"%f\t",component_logistic[i][j]);
			fprintf(fp_save_com,"\n");
		}

		fclose(fp_save_fit);
		fclose(fp_save_res);
		fclose(fp_save_fpd);
		fclose(fp_save_fpl);
		fclose(fp_save_dat);
		fclose(fp_save_inm);
		fclose(fp_save_fim);
		fclose(fp_save_dis);
		fclose(fp_save_rng);
		fclose(fp_save_fpr);
		fclose(fp_save_com);

		fp_save_char = fopen("savechar.tmp","wa");
		for (i=0;i<strlen(save_char);i++) fprintf(fp_save_char,"%i ",save_char[i]);
		fclose(fp_save_char);

	}
	/******* END SAVE FUNCTION *******/
	
	if (noisy_flag ==1) printf("\nFinished Program\n");

} /**MAIN**/
