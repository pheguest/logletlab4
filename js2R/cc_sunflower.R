#######################################
########## INPUT FROM USER ############
#######################################
## FONT STYLE
titleFont <- "GillSans"

## A, K, B
midpoint1 <- "30"
carryingCap1 <- "250"
growthRate1 <- "40"

## SINGLE, BI, TRI ANALYSIS
analysisType <- "single"

## FILENAME
fileUpload <- "sunflower1.csv"

## COLORS TO BE USED ON THE PLOT
color1 <- "#FF1E05"
color2 <- "#FFFF1F"
color3 <- "#44FF3D"
color4 <- "#24DAFF"
color5 <- "#F830FF"

## PLOT TITLE
plotTitle <- "Single Logistic -- Sunflower plot"

## FILETYPE -- CSV OR XLS
file_ext <- "csv"

#######################################
####### LOAD LIBRARIES ################
#######################################

## GGPLOT2 FOR GRAPHING
library(ggplot2)
## XLCONNECT FOR READING EXCEL FILES
library(XLConnect)
## EXTRAFONT FOR GILL SANS
library(extrafont)
loadfonts()

## READ THE FILE
fileContents <- read.csv(file=fileUpload,head=TRUE,sep=",")

#######################################
#### GET INFO ABOUT THE DATAFILE ######
#######################################
## THE X-AXIS TITLE (CELL A1)
xaxis <- colnames(fileContents[1])
## THE Y-AXIS TITLE (CELL B1)
yaxis <- colnames(fileContents[2])
## TOTAL NUMBER OF COLUMNS IN THE FILE
numCols <- ncol(fileContents)
## NUMBER OF Y-AXIS COLUMNS 
numYs <- numCols - 1
## DATA RANGE OF X VALUES
dataRangeX <- range(fileContents[,1])
## DATA RANGE OF Y VALUES
dataRangeY <- range(c(fileContents[,2:numCols]))
## ALL THE X-AXIS VALUES (IN THE CASE OF SUNFLOWER, DAYS))
sx = fileContents[,1]
## ALL THE X-AXIS VALUES (IN THE CASE OF SUNFLOWER, HEIGHT))
sy = fileContents[,2]

###########################################
#### DEFINE sunlinear AND sunlogistic #####
###########################################
sunlinear <- lm(sy ~ sx)

#### PRINTING sunlinear at this point shows the following:
## Call:
## lm(formula = sy ~ sx)

## Coefficients:
## (Intercept)           sx  
##      6.287        3.452 

sunlogistic <- nls(sy ~ ( k / (1 + exp( (log(81)/-a) * (sx - b)))),start=c(k=carryingCap1, a=growthRate1, b=midpoint1))

#### PRINTING sunlinear at this point shows the following (a, k, b seem correctly predicted):
## Nonlinear regression model
##  model: sy ~ (k/(1 + exp((log(81)/-a) * (sx - b))))
##   data: parent.frame()
##     k      a      b 
## 261.04  50.10  34.27 
## residual sum-of-squares: 127.1

## ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
##    PSM -- these are the fitted parameters!!!!!!!
## 
growthrate1 <- sunlogistic()

growthrate1 should be 50.1

## Number of iterations to convergence: 5 
## Achieved convergence tolerance: 3.069e-06


#############################
#### GENERATE IMAGES ########
## Note: This is using R's ##
## default plot creation.  ##
## will look even better   ##
## we use GGplot.          ##
#############################

####FOR PDF#####
pdf('singleRegression.pdf', family=titleFont)
plot(sx,sy, axes=TRUE, ann=FALSE, col=color4)
#### newx is the span of x in the plot.  Hardcoded for now.  This will have to become 
#### dynamic in order to accomodate all datasets.
newx  <- seq(1,200,length=200)
lines(newx,predict(sunlogistic,newdata=data.frame(sx=newx)),lwd=2, col=color1)
title(main=plotTitle, col.main=color5, font.main=4)
title(xlab=xaxis, col.lab=color2)
title(ylab=yaxis, col.lab=color3)
title(sub=paste("a:",growthRate1, "   k:",carryingCap1, "   b:",midpoint1))
sunlogistic




####FOR JPEG#####
jpeg('singleRegression.jpg')
plot(sx,sy, axes=TRUE, ann=FALSE, col=color4)
newx  <- seq(1,200,length=200)
lines(newx,predict(sunlogistic,newdata=data.frame(sx=newx)),lwd=2, col=color1)
title(main=plotTitle, col.main=color5, font.main=4)
title(xlab=xaxis, col.lab=color2)
title(ylab=yaxis, col.lab=color3)
title(sub=paste("a:",growthRate1, "   k:",carryingCap1, "   b:",midpoint1))
sunlogistic

##### TURN OFF THE IMAGE CREATION DEVICE #####
dev.off()

