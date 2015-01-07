#####################################
#####################################
##### LOGLET LAB VERSION 4 ########## 
### CURRENT AS OF JANUARY 5, 2015 ###
#####################################
#####################################

OVERVIEW:
Loglet Lab Version 4 is an initiative to create an updated version of the last 3 iterations of Loglet Lab.
The goal is to create an open source, web-based program incorporating only the best and most commonly used features of the previous Loglets.
Our team settled on core program coded in R language with a PHP and HTML interface. 

The contents of the current program directory are:

Excel  images  jscolor  loglet.php  newPlot.R  ShowHide.css  ShowHide.js

newPlot.R : The main R script.
loglet.php : The interface and wrapper code to R.
ShowHide.css and ShowHide.js : The dynamic hide & show code for A, K, B variables.  Can easily be modified using the same approach for other dynamic hide & show elements.
Excel : Used to allow PHP to read and parse Excel files.
jscolor : Color selection tool.
images : Contains static images to be used on the webpage.

WORKFLOW PROCESS:
1. Front-end users access the program via the following URL:
   http://phe.rockefeller.edu/biostat/loglet.php
2. User uploads a datafile in the format .csv or .xls
3. User selects analysis type.
4. User inputs A, K, and B estimates (if not provided, newPlot.R attempts to estimate them).
5. User selects formatting options: Line color, X-axis font color, Y-axis font color, Point color, Title color, Title font, and Title text.
6. User clicks 'Submit' button and the job begins.  The backend PHP process is as follows:
   6a. The loglet.php code parses the input file and accepts the formatting options.
   6b. Loglet.php creates a unique working directory (timestamp) for the job.
   6c. newPlot.R is a template of the R code.  Loglet.php copies this R template into the unique job directory.
   6d. The variables unique to this job are pasted into working copy of newPlot.R.
   6e. Loglet.php moves the uploaded file ino the working directory too.
   6f. The R script is executed.
7. Based on the input file and input variables, the R script creates the following files:
   7a. Basic time series (via standard plotting method and via ggplot)
   7b. Normal distribution curve (via standard plotting method and via ggplot)
   7c. Residuals (can be either standard plotting or ggplot based on sunlogistic or sunlinear, TBD)
   7d. Standardized residuals (can be either standard plotting or ggplot based on sunlogistic or sunlinear, TBD)
   7e. Text output of residuals/standardzed residuals.
   7f. The regression curve (based on R's NLS and plotted via standard plotting method and via ggplot)
   7g. Text logfile of the job.
8. Visual plots are displayed on the screen.
9. Contents of the data file are printed to the screen too, as proof that this is the file that was uploaded and used in the job.
10. Loglet.php zips these files and displays a link on the screen for the user to download the zip file of results. 

CURRENT STATE OF THE PROGRAM AND FURTHER DIRECTION:

A blog page detailing our progress, notes, and projected timeline can be found at:
http://phe.rockefeller.edu/loglet4.html

A storyboard for the program was created and can be found at:
http://phe.rockefeller.edu/docs/LL4_Storyboard.pdf

Currently, the program is well-developed in all aspects described in the storyboard except for the R code. 

The current focus of the R analysis is on Single Logistics.  Bi- and Tri- Logistics should stem from that.

The approach used to implement the Single Logistics is R's nls() function using a defined formula (https://stat.ethz.ch/R-manual/R-patched/library/stats/html/nls.html, and see the nls() call in newPlot.R).  This function accepts initial estimates of input parameters.  nls() then calculates its own true values for these parameters.  Initial estimates must be "in the ballpark" of the actual.  In the case of our Single Logistics, these parameters are A, K, and B.  By default, Loglet 4 does not require A, K, B to be entered by the user.  The user cannot be expected to know offhand what these values are.  Rather, a simple estimation is computed in loglet.php.  R uses these guesses in nls(), which then computes accurate A, K, and B. As long as these values are "in the ballpark", nls() appears to always compute the same A, K, and B for any given dataset. This simply seems to be the nature of the function. In Loglet 3, the program accepts not one value for each, but rather an upper and lower bound for each (6 total).  The program then computes its own A, K, and B based on these estimates.  The resulting curve can fluctuate in its behavior based on the estimates of upper and lower bound.  This is a key issue yet to be fully determined and strategized for Loglet 4.  Is it sufficient to use the well-established nls() in R or will a modified/new method similar to nls, but not quite the same, need to be written where the arbitrary range of estimates affects the computation of the actual curve?

It also needs to be discussed exactly which residuals should be returned to the user.  Currently, the output from sunlinear() and sunlogistic() can be viewed as residuals.  These residulas can be computed using either R's resid() or rstandard().  Both are currently implemented in newPlot.R.

Bi- and Tri- logistics have not been attempted yet in R.  Loglet 4 can distinguish whether or not the program should run a Bi- or Tri- logistic analysis based on the user's selected option, but currently does not do any Bi- or Tri- logistic computation.

LSM (Logistic substitution) is the last phase of development for Loglet 4. LSM follows a markedly different approach than the previous 3 models mentioned.  Details for LSM have not yet been discussed in depth and no R programming has yet been done in Loglet 4.
