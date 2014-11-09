#Test from CC


## perrin is adding this line  
source(file="linspace.R")
source(file="loglet_func.R")

#VARIABLES TO BE PASSED IN... A, K, B
####a = 5
####k = 6
####b = 7
####logobj.parameters.a0
####logobj.parameters.k0 (???)
####logobj.parameters.b0

logobj.parameters.a0 <- 40
logobj.parameters.k0 <- 200
logobj.parameters.b0 <- 30

#### psm
a0 <- logobj.parameters.a0
k0 <- logobj.parameters.k0
b0 <- logobj.parameters.b0

i <- 0
j <- 0

##### SETTING d as 0 (almost all the time?) - C.C. 11/09/2014 #####
d = 0

##### WHAT IS sl ? - C.C. 11/09/2014 ######
sl = 1.2

##### HARDCODING SUNFLOWER DATA FOR NOW - C.C. 11/09/2014 ######
logobj.data.x = c(7.0, 14, 21, 28, 35, 42, 49, 56, 63, 70, 77, 84)
logobj.data.y = c(17.93, 36.36, 67.76, 98.1, 131, 169.5, 205.5, 228.3, 247.1, 250.5, 253.8, 254.5)

xstart = logobj.parameters.b0 - sl * logobj.parameters.a0

########## IS THIS DETERMINED BY THE USER??
logobj.number_of_loglets = 1 

##### RIGHT NOW logobj.parameters IS AN ARBITRARY VALUE - C.C. 11/09/2014 ######
####logobj.parameters = 0 
logobj.parameters = 5
####logobj.parameters <- c()

xstop =  logobj.parameters[b0+(logobj.number_of_loglets-1)] + sl * logobj.parameters[a0+(logobj.number_of_loglets -1)]

logobj.curve.n = 6 

##### WHERE .length is specified, is this an evaluation of the length of the object??

  ###### the main curve ######## 
  logobj.curve.t = linspace(xstart,xstop,logobj.curve.n); 
  logobj.curve.t.length = length(logobj.curve.t) 

  logobj.curve.yfit <- c()
  ###logobj.curve.yfit <- 0

  while(i < logobj.curve.t.length) {
    logobj.curve.yfit[i] = 0;
    i = i + 1
  }
  ## psm for example 
  while(i<logobj.curve.t.length) {
    while(j < logobj.number_of_loglets) {
      logobj.curve.yfit[i] = logobj.curve.yfit[i] + 
        loglet(logobj.curve.t[i],a0,k0,b0)
      j = j + 1
    }
  i = i + 1
  }
##### PRINTING logobj.curve.yfit ########
print(logobj.curve.yfit)
####stop()


#####logobj.data.x = 0
logobj.data.x.length = length(logobj.data.x)

  #### the residuals ##### 

  logobj.data.yfit <- c()  
  logobj.data.residuals <- c()

  while(i < logobj.data.x.length) {
    logobj.data.yfit[i] = 0
    logobj.data.residuals[i] = 0
    i = i + 1
  }

##### MODIFIED AS PER DISCUSSION - C.C. 11/09/2014 ###### 

  while (i < logobj.data.x.length){
    while (j < logobj.number_of_loglets) {
      logobj.data.yfit[i] = logobj.data.yfit[i] + 
        loglet(logobj.data.x[i], a0,k0,b0)
      j = j + 1 
    }
    logobj.data.residuals[i] = logobj.data.y[i] - logobj.data.yfit[i]
    i = i + 1
  }

##### PRINTING logobj.data.residuals ######
print(logobj.data.residuals)
#####stop()

##########HOW ABOUT THESE ##############
  mean = 0.0;
  tmpsum = 0.0;
  N = logobj.data.x.length;

  iter = 0

  while (iter < N) {
    tmpsum = tmpsum + logobj.data.residuals[iter]
    iter = iter + 1
  }  

  mean = tmpsum / N
  
  sd = 0.0     ### the (estimated) standard deviation 
 
  iter = 0 
  while (iter < N) {
    sd = sd + (logobj.data.residuals[iter] - mean) * (logobj.data.residuals[iter] - mean)
    iter = iter + 1
  }  

  sd = sqrt( (1 / (N -1)) * sd);

  logobj.data.sr <- c()
  iter = 0
  while (iter < N) {
    logobj.data.sr[iter] = (logobj.data.residuals[iter] - mean) / sd
    iter = iter + 1
  }

##### PRINTING logobj.data.sr ######
print(logobj.data.sr)
###stop()

  ##### put in flot form
  iter = 0
  while (iter < logobj.data.x.length) {
    #### THIS LINE IS A PROBLEM ####logobj.flot.data.push([logobj.data.x[iter], logobj.data.y[iter]])
    iter = iter + 1
  }

  iter = 0
  while (iter < logobj.data.x.length) {
    #### THIS LINE IS A PROBLEM ####logobj.flot.residuals.push([logobj.data.x[iter], logobj.data.residuals[iter]])
    iter = iter + 1
  }

  iter = 0
  while (iter < logobj.data.x.length) {
    #### THIS LINE IS A PROBLEM ####logobj.flot.sr.push([logobj.data.x[iter], logobj.data.sr[iter]])
    iter = iter + 1
  }


  #### create zero line for residuals
  #### THIS LINE IS A PROBLEM ####logobj.flot.resline.push([logobj.curve.t[0], 0.0])
  #### THIS LINE IS A PROBLEM ####logobj.flot.resline.push([logobj.curve.t[logobj.curve.n-1], 0.0])



 #### put in flot form 
  iter = 0
  while (iter < logobj.curve.n){
    #### THIS LINE IS A PROBLEM #### logobj.flot.curve.push([logobj.curve.t[iter], logobj.curve.yfit[iter]])
    iter = iter + 1
  }


  fp = {} ##### to hold modified fp parameters and temp data
  cc = {} ##### to hold modified component parameters and temp data

  j = 0
  while (j < logobj.number_of_loglets) {
    fp[a0] = a0
    fp[k0] = k0
    fp[b0] = b0
    j = j + 1
  }
##### PRINTING fp ######
print(fp)
###stop()

  ##### makes the fisher pry plots look right ##### 
  j = 0
  while (j < logobj.number_of_loglets) {
    if ( (fp[a0] > 0.0) && (fp[k0] < 0.0) ) {
      fp[a0] = -1.0 * fp[a0]
      fp[k0] = -1.0 * fp[k0]
    }
    if (fp[a0] < 0.0 && fp[k0] < 0.0 ) {
      fp[a0] = -1.0 * fp[a0]
      fp[k0] = -1.0 * fp[k0]
    }    
  j = j + 1
  }  

##### PRINTING fp again ######
print(fp)
####stop()

  ##### lets make the fisher pry transformed data ###### 
  ##### and now compoent logisitcs (called cc for some reason #####

##### HAD TO SET THESE AS SOMETHING... - C.C. 11/09/2014 ######
    x0 = 0
    y0 = 0
    x0 = 0
    y0 = 0

  j = 0

##### INTERESTING TO NOTE: R STARTS INDEXING ITS ARRAYS AT 1 AND NOT 0.  THIS WILL LIKELY CHANGE THE INITIAL ITERATION VALUE ON SOME VARIABLES - C.C. 11/09/2014 ######

  while (j < logobj.number_of_loglets) {

    i = 1  
    while (i < logobj.data.x.length) {
      if ( (logobj.data.x[i] > (b0 - abs(a0 * sl))) && (logobj.data.x[i] < (b0 + abs(a0 * sl))) ) {

        #### THIS LINE IS A PROBLEM #### fp[x+j].push(logobj.data.x[i]) 
        #### THIS LINE IS A PROBLEM #### cc[x+j].push(logobj.data.x[i]) 
        #### THIS LINE IS A PROBLEM #### fp[y+j].push(logobj.data.y[i] - logobj.parameters[d] ) 
        #### THIS LINE IS A PROBLEM #### cc[y+j].push(logobj.data.y[i] - logobj.parameters[d] ) 
        ########## TRYING THIS AS A SOLUTION ##############
	fp[x0] <- union(fp[x0], c(logobj.data.x[i]))
        cc[x0] <- union(cc[x0], c(logobj.data.x[i]))
        fp[y0] <- union(fp[y0], c(logobj.data.y[i] - logobj.parameters[d]))
        cc[y0] <- union(cc[y0], c(logobj.data.y[i] - logobj.parameters[d]))
      }
    i = i + 1
    }
  j = j + 1
  }

##### PRINTING fp and cc######
print(fp)
print(cc)
###stop()

  j = 0
  while (j < logobj.number_of_loglets) {
    i = 0
    ###### while (i < cc[x+j].length) {
    while (i < length(cc[x0])) {
      iter = 0
      while (iter < logobj.number_of_loglets) {
        if ( iter != j) {
	  ##### THIS LINE DIDN'T ACTUALLY GIVE A SYNTAX ERROR, BUT STILL MODIFIED AS PER DISCUSSION - C.C. 11/09/2014 ######
          #####	cc[y0][i] = cc[y0][i] - loglet(cc[x0][i],
          #####                       logobj.parameters[a+iter],
          #####                       logobj.parameters[k+iter],
          #####                       logobj.parameters[b+iter])
          cc[y0][i] = cc[y0][i] - loglet(cc[x0][i], a0, k0, b0)
        }
      iter = iter + 1
      }
    i = i + 1
    }
  j = j + 1
  }


  j = 0
  while (j < logobj.number_of_loglets) {
    i = 0
    ######while (i < fp[x0].length) {
    while (i < length(fp[x0])) {
      iter = 0
      while (iter < logobj.number_of_loglets) {
        if ( iter != j) {
##### THIS LINE DIDN'T ACTUALLY GIVE A SYNTAX ERROR, BUT STILL MODIFIED AS PER DISCUSSION - C.C. 11/09/2014 #######
          #####fp[y0][i] = fp[y0][i] - loglet(fp[x0][i],
          #####                       logobj.parameters[a+iter],
          #####                       logobj.parameters[k+iter],
          #####                       logobj.parameters[b+iter]);

          fp[y0][i] = fp[y0][i] - loglet(fp[x0][i], a0, k0, b0)
        }
      iter = iter + 1
      }

      if (logobj.parameters[k0] < 0.0) { 
	fp[y0][i] = fp[y0][i] + (-1.0) * logobj.parameters[k0]
      }

      if (fp[y0][i] < 0.0 || fp[y0j][i] >= abs(logobj.parameters[k0])) { 
        fp[y0][i] = 0.0
      } else {
        fp[y0][i] = fp[y0][i] / ( abs( logobj.parameters[k0]) - fp[y0][i])
      }
    i = i + 1
    }
  j = j + 1
  }  

##### PRINTING fp and cc again ######
print(fp)
print(cc)
###stop()

##### IMPROVISED, FOR NOW... C.C. - 11/9/14 #####
logobj.fp <- c()
datax = 0
datay = 0
logobj.cc <- c()
x = 0

  j = 0
  while (j < logobj.number_of_loglets) {
    ####logobj.flot[fpdata+j] = c()
    ####logobj.flot[ccdata+j] = c()
    logobj.fp[datax + j] = c()
    logobj.fp[datay + j] = c()
    logobj.cc[datax + j] = c()
    logobj.cc[datay + j] = c()

    i = 0
    ######while (i < fp[x+j].length) {
    while (i < length(fp[x+j])) {
      ##### THIS LINE IS A PROBLEM logobj.flot[fpdata+j].push([fp[x+j][i],log10(fp[y+j][i])])
      ##### THIS LINE IS A PROBLEM logobj.flot[ccdata+j].push([cc[x+j][i],cc[y+j][i]])
      ##### THIS LINE IS A PROBLEM logobj.fp[datax + j].push(fp[x+j][i])
      ##### THIS LINE IS A PROBLEM logobj.fp[datay + j].push(fp[y+j][i])
      ##### THIS LINE IS A PROBLEM logobj.cc[datax + j].push(cc[x+j][i])
      ##### THIS LINE IS A PROBLEM logobj.cc[datay + j].push(cc[y+j][i])
      ############ FLOT IS FOR GRAPHING... WE CAN PROBABLY ELIMINATE ###########
    i = i + 1
    }
  j = j + 1
  }

  ###### now lets make the fisher py lines ######### 

##### IMPROVISED, FOR NOW... C.C. - 11/9/14 #####
xl0 = 0
yl0 = 0
linex = 0
liney = 0

  j = 0
  while (j < logobj.number_of_loglets) {
    fp[xl0] = linspace(xstart,xstop,100) ##### this looks like bug? xstart should be changing.. 
    fp[yl0] = c()
    
    i = 0
    #####while (i < fp[xl+j].length) {
    while (i < length(fp[xl0])) {
      fp[yl0][i] = loglet(fp[xl0][i], fp[a0], fp[k0], fp[b0])
      fp[yl0][i] = fp[yl0][i] / fp[k0]  ###### create fraction f (y/k) #####
      fp[yl0][i] = fp[yl0][i] / ( 1 - fp[yl0][i]) #### create f / (1 - f) ##### 
    i = i + 1
    }
  j = j + 1
  }

##### PRINTING fp  again ######
print(fp)
###stop()


  j = 0
  while (j < logobj.number_of_loglets) {
    ####logobj.flot[fpline+j] = c()
    logobj.fp[linex + j] = c()
    logobj.fp[liney + j] = c()

    i = 0
    #####while (i < fp[xl+j].length) {
    while (i < length(fp[xl0])) {
      #### THIS LINE IS A PROBLEM logobj.flot[fpline+j].push([fp[xl+j][i],log10(fp[yl+j][i])])
      #### THIS LINE IS A PROBLEM logobj.fp[linex+j].push(fp[xl+j][i])
      #### THIS LINE IS A PROBLEM logobj.fp[liney+j].push(fp[yl+j][i])
      i = i + 1
    }
  j = j + 1
  }

##### PRINTING logobj.fp ######
print(logobj.fp)
#####stop()

  ##### now lets make component curves ######### 

  j = 0
  while (j < logobj.number_of_loglets) {
    xstart = logobj.parameters[b0] - sl * logobj.parameters[a0]
    xstop =  logobj.parameters[b0] + sl * logobj.parameters[a0]
    cc[xl0] = linspace(xstart,xstop,100) ###### this looks like bug? xstart should be changing.. 
    cc[yl0] = c()

    i = 0
    ####while (i < cc[xl+j].length) {
    while (i < length(cc[xl0])) {
      cc[yl0][i] = loglet(cc[xl0][i],fp[a0],fp[k0], fp[b0])
      i = i + 1
    }
  j = j + 1
  }

##### PRINTING cc ######
print(cc)
####stop()

  j = 0
  while (j < logobj.number_of_loglets) {
    ########logobj.flot[cccurve+j] = c()
    logobj.cc[linex + j] = c()
    logobj.cc[liney + j] = c()

##### PRINTING logobj.cc ######
print(logobj.cc)
###stop()

    i = 0
    while (i < length(cc[xl0])) {
      #### THIS LINE IS A PROBLEM logobj.flot[cccurve+j].push([cc[xl+j][i],cc[yl+j][i]])
      #### THIS LINE IS A PROBLEM logobj.cc[linex+j].push(cc[xl+j][i])
      #### THIS LINE IS A PROBLEM logobj.cc[liney+j].push(cc[yl+j][i])
      
      ###logobj.flot[cccurve+j] <- union(logobj.flot[cccurve+j], c(cc[xl+j][i], cc[yl+j][i])
      ###logobj.cc[linex+j] <- union(logobj.cc[linex+j], c(cc[xl+j][i]))
      ###logobj.cc[liney+j] <- union(logobj.cc[liney+j], c(cc[yl+j][i]))
      i = i + 1
    }
  j = j + 1
  }
