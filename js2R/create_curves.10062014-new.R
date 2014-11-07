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

sl = 1.2

logobj.data.x = c(7.0, 14, 21, 28, 35, 42, 49, 56, 63, 70, 77, 84)
logobj.data.y = c(17.93, 36.36, 67.76, 98.1, 131, 169.5, 205.5, 228.3, 247.1, 250.5, 253.8, 254.5)

xstart = logobj.parameters.b0 - sl * logobj.parameters.a0

########## IS THIS DETERMINED BY THE USER??
logobj.number_of_loglets = 1 

####logobj.parameters = 0 
logobj.parameters = 5
####logobj.parameters <- c()

xstop =  logobj.parameters[b+(logobj.number_of_loglets-1)] + sl * logobj.parameters[a+(logobj.number_of_loglets -1)]

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

  while (i < logobj.data.x.length){
    while (j < logobj.number_of_loglets) {
      logobj.data.yfit[i] = logobj.data.yfit[i] + 
        loglet(logobj.data.x[i],
               logobj.parameters[a+j],
               logobj.parameters[k+j],
               logobj.parameters[b+j]);
      logobj.data.yfit[i] = logobj.data.yfit[i] + logobj.parameters[d] 
      j = j + 1 
    }
    logobj.data.residuals[i] = logobj.data.y[i] - logobj.data.yfit[i]
    i = i + 1
  }


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
    fp[a+j] = logobj.parameters[a+j]
    ####print(fp[a+j])
    fp[k+j] = logobj.parameters[k+j]
    fp[b+j] = logobj.parameters[b+j]
    j = j + 1
  }
#######stop()

  ##### makes the fisher pry plots look right ##### 
  j = 0
  while (j < logobj.number_of_loglets) {
    if ( (fp[a+j] > 0.0) && (fp[k+j] < 0.0) ) {
      fp[a+j] = -1.0 * fp[a+j]
      fp[k+j] = -1.0 * fp[k+j]
    }
    if (fp[a+j] < 0.0 && fp[k+j] < 0.0 ) {
      fp[a+j] = -1.0 * fp[a+j]
      fp[k+j] = -1.0 * fp[k+j]
    }    
  j = j + 1
  }  

  ##### lets make the fisher pry transformed data ###### 
  ##### and now compoent logisitcs (called cc for some reason #####

  j = 0
  while (j < logobj.number_of_loglets) {
    fp[x+j] = c()
    fp[y+j] = c()
    cc[x+j] = c()
    cc[y+j] = c()

    i = 0
    while (i < logobj.data.x.length) {
      if ( (logobj.data.x[i] > (logobj.parameters[b+j] - abs(logobj.parameters[a+j] * sl)))
           &&
           (logobj.data.x[i] < (logobj.parameters[b+j] + Math.abs(logobj.parameters[a+j] * sl))) ) {

        #### THIS LINE IS A PROBLEM #### fp[x+j].push(logobj.data.x[i]) 
        #### THIS LINE IS A PROBLEM #### cc[x+j].push(logobj.data.x[i]) 
        #### THIS LINE IS A PROBLEM #### fp[y+j].push(logobj.data.y[i] - logobj.parameters[d] ) 
        #### THIS LINE IS A PROBLEM #### cc[y+j].push(logobj.data.y[i] - logobj.parameters[d] ) 
        ########## TRYING THIS AS A SOLUTION ##############
	fp[x+j] <- union(fp[x+j], c(logobj.data.x[i]))
        cc[x+j] <- union(cc[x+j], c(logobj.data.x[i]))
        fp[y+j] <- union(fp[y+j], c(logobj.data.y[i] - logobj.parameters[d]))
        cc[y+j] <- union(cc[y+j], c(logobj.data.y[i] - logobj.parameters[d]))
      }
    i = i + 1
    }
  j = j + 1
  }

  j = 0
  while (j < logobj.number_of_loglets) {
    i = 0
    ###### while (i < cc[x+j].length) {
    while (i < length(cc[x+j])) {
      iter = 0
      while (iter < logobj.number_of_loglets) {
        if ( iter != j) {
          cc[y+j][i] = cc[y+j][i] - loglet(cc[x+j][i],
                                 logobj.parameters[a+iter],
                                 logobj.parameters[k+iter],
                                 logobj.parameters[b+iter])
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
    ######while (i < fp[x+j].length) {
    while (i < length(fp[x+j])) {
      iter = 0
      while (iter < logobj.number_of_loglets) {
        if ( iter != j) {
          fp[y+j][i] = fp[y+j][i] - loglet(fp[x+j][i],
                                 logobj.parameters[a+iter],
                                 logobj.parameters[k+iter],
                                 logobj.parameters[b+iter]);
        }
      iter = iter + 1
      }

      if (logobj.parameters[k+j] < 0.0) { 
	fp[y+j][i] = fp[y+j][i] + (-1.0) * logobj.parameters[k+j]
      }

      if (fp[y+j][i] < 0.0 || fp[y+j][i] >= abs(logobj.parameters[k+j])) { 
        fp[y+j][i] = 0.0
      } else {
        fp[y+j][i] = fp[y+j][i] / ( abs( logobj.parameters[k+j]) - fp[y+j][i])
      }
    i = i + 1
    }
  j = j + 1
  }  

  j = 0
  while (j < logobj.number_of_loglets) {
    logobj.flot[fpdata+j] = c()
    logobj.flot[ccdata+j] = c()
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

  j = 0
  while (j < logobj.number_of_loglets) {
    fp[xl+j] = linspace(xstart,xstop,100) ##### this looks like bug? xstart should be changing.. 
    fp[yl+j] = c()
    
    i = 0
    #####while (i < fp[xl+j].length) {
    while (i < length(fp[xl+j])) {
      fp[yl+j][i] = loglet(fp[xl+j][i], fp[a+j], fp[k+j], fp[b+j])
      fp[yl+j][i] = fp[yl+j][i] / fp[k+j]  ###### create fraction f (y/k) #####
      fp[yl+j][i] = fp[yl+j][i] / ( 1 - fp[yl+j][i]) #### create f / (1 - f) ##### 
    i = i + 1
    }
  j = j + 1
  }

  j = 0
  while (j < logobj.number_of_loglets) {
    logobj.flot[fpline+j] = c()
    logobj.fp[linex + j] = c()
    logobj.fp[liney + j] = c()

    i = 0
    #####while (i < fp[xl+j].length) {
    while (i < length(fp[xl+j])) {
      #### THIS LINE IS A PROBLEM logobj.flot[fpline+j].push([fp[xl+j][i],log10(fp[yl+j][i])])
      #### THIS LINE IS A PROBLEM logobj.fp[linex+j].push(fp[xl+j][i])
      #### THIS LINE IS A PROBLEM logobj.fp[liney+j].push(fp[yl+j][i])
      i = i + 1
    }
  j = j + 1
  }


  ##### now lets make component curves ######### 

  j = 0
  while (j < logobj.number_of_loglets) {
    xstart = logobj.parameters[b+j] - sl * logobj.parameters[a+j]
    xstop =  logobj.parameters[b+j] + sl * logobj.parameters[a+j]
    cc[xl+j] = linspace(xstart,xstop,100) ###### this looks like bug? xstart should be changing.. 
    cc[yl+j] = c()

    i = 0
    ####while (i < cc[xl+j].length) {
    while (i < length(cc[xl+j])) {
      cc[yl+j][i] = loglet(cc[xl+j][i],fp[a+j],fp[k+j], fp[b+j])
      i = i + 1
    }
  j = j + 1
  }

  j = 0
  while (j < logobj.number_of_loglets) {
    logobj.flot[cccurve+j] = c()
    logobj.cc[linex + j] = c()
    logobj.cc[liney + j] = c()

    i = 0
    while (i < length(cc[xl+j])) {
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

