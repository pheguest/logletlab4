###########################################
############# LOGLET VERSION 4 ############
#############     README.TXT   ############
#############  Cameron Coffran ############
#############         -        ############
#### Program for the Human Environment ####
#############        2014      ############
###########################################

This document serves as a general outline for the Loglet 4 code. 
A clean copy of the code is stored on the PHE server in
/home/www/html/biostat-clean/

The webpage can be found by accessing the loglet.php file through a web browser.

This PHP webpage  acts a wrapper for the core of the R code in newPlot.R
loglet.php accepts the input variables about the run and uploads the data file.

On the backend, a each job is given a unique ID, the timestamp.  All data relating to this run ultimately ends up in a unique subdirectory of the same name, such as /home/www/html/biostat-clean/NNNNNNNNNNN/  Where "NNNNNNNNNNN" is a digital timestamp.

Within this directory, a copy of the newPlot.R is placed in this directory and is fed certain parameters pertaining to the unqiue run.  Rather than having only one master R script, creating copies allows for easier debugging.

All output from this run is placed in a logfile housed in this same directory.

The resulting graph is returned to the user via a zip file which can be downloaded directly on the site.
