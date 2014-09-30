Loglet Lab IV
==========

*Loglet Lab* is a software package for analyzing logistic behavior in time-series data.

Processes of growth and diffusion important for environment often follow a logistic course.
In some cases they behave as a series of logistic wavelets, or "loglets." 
In the easiest cases to recognize, a loglet appears as an S-shaped curve 
or a succession of many S-shaped curves. When loglets overlap in time, 
the overall logistic behavior of a system can be hard to discern and analyze.

In niches or markets in which several populations or technologies compete, 
the growth and decline of each entry also often exhibit logistic behavior. 
This behavior depends on interactions among the competitors. 
Namely, if a technology's market share grows, it comes at the cost of shares of others.
This process is well-described by the so-called "logistic substitution model." 

Again, discerning and quantifying the pattern can be hard. 
To advance and ease analyses of logistic behavior in time-series data,
we have developed the "Loglet Lab" software package. Loglet Lab users can fit 
logistic curves to a single time-series and apply the logistic substitution model 
to multiple time-series.

History
=======
Loglet Lab had its roots in some C and MATLAB code that Perrin Meyer had written.

*Loglet Lab 1* took Perrin's code into Visual C++ to provide a visual interface
for Windows (primarily NT/95/98).

*Loglet Lab 2* ported the work in LL1 to Java for cross-platform use 
(Windows/Mac/Linux).

*Loglet Lab 3* rewrote the code with a web interface to provide logistic analysis
to your web browser.

The objective of *Loglet Lab 4* is to take the web accessibility of LL3,
implement the full feature sets of LL1 and LL2,
and further incorporate modern web technologies and practices.