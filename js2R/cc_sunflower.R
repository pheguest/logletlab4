#######################################
########## INPUT FROM USER ############
#######################################
## FONT STYLE
if (!exists("perrin")) {
titleFont <- "GillSans"
}

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


##### INITIAL ESTIMATES FOR A, K, B BASED ON THE DATA #######
estimateA = (max(sx) - min(sx))
estimateK = max(sy) * 1.1
estimateB = (max(sx) + min(sx))/2


##### STANDARDIZED RESIDUALS ######
sunlinear <- lm(sy ~ sx)
stdres <- rstandard(sunlinear)
SLRes <- data.frame(sx, stdres)
ggplot(SLRes, aes(x=sx, y=stdres)) + xlab(xaxis) + ylab("Residual Value") + geom_point(colour=color4, size=3) + ggtitle("Standardized Residuals") + geom_abline(colour = "red", size = 2)
ggsave(file="ggplotStandardResiduals.pdf")
ggsave(file="ggplotStandardResiduals.jpg", width = 4, height = 4, dpi = 75, scale = 2)

pdf('standardizedResiduals.pdf', family=titleFont)
plot(sx, stdres, xlab=xaxis, main="Standardized Residuals")
jpeg('standardizedResiduals.jpg', family=titleFont)
plot(sx, stdres, xlab=xaxis, main="Standardized Residuals")
abline(0, 0) # the horizon
##### END STANDARDIZED RESIDUALS ######

###########################################
#### DEFINE sunlogistic ###################
###########################################

#### PRINTING sunlinear at this point shows the following:
## Call:
## lm(formula = sy ~ sx)

## Coefficients:
## (Intercept)           sx  
##      6.287        3.452 

#### WITH USER INPUT FOR A, K, B ###sunlogistic <- nls(sy ~ ( k / (1 + exp( (log(81)/-a) * (sx - b)))),start=c(k=carryingCap1, a=growthRate1, b=midpoint1))

#### WITH PREDICTED A, K, B
sunlogistic <- nls(sy ~ ( k / (1 + exp( (log(81)/-a) * (sx - b)))),start=c(k=estimateK, a=estimateA, b=estimateB))

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
###growthrate1 <- sunlogistic()

###growthrate1 should be 50.1

## Number of iterations to convergence: 5 
## Achieved convergence tolerance: 3.069e-06

## GET THE SUNLOGISTIC RESIDUALS, WRITE THEM TO A FILE, AND PLOT THEM ########
myResiduals <- residuals(sunlogistic)
sink("residuals.txt", append=FALSE, split=FALSE)
writeLines(formatOL(myResiduals))
sink()

resCols <- c(sx, myResiduals)
dfRes = data.frame(sx, myResiduals)
ggplot(dfRes, aes(x=sx, y=myResiduals)) + geom_line() + xlab(xaxis) + ylab("Residual Value") + geom_point(colour="#00B3FF") + ggtitle("Residuals")
ggsave(file="ggplotResiduals.pdf")
ggsave(file="ggplotResiduals.jpg", width = 4, height = 4, dpi = 75, scale = 2)

## END SUNLOGISTIC RESIDUALS ########

sum_sun <- summary(sunlogistic)
names(sum_sun)
carryingCapCalc1 <- round(sum_sun$parameters[[1]], 2)
growthRateCalc1 <- round(sum_sun$parameters[[2]], 2)
midpointCalc1 <-round(sum_sun$parameters[[3]], 2)

k1 <- round(sum_sun$parameters[[1]], 2)
a1 <- round(sum_sun$parameters[[2]], 2)
b1 <-round(sum_sun$parameters[[3]], 2)

cat("k1 = ",k1,"\n")
cat("a1 = ",a1,"\n")
cat("b1 = ",b1,"\n")

sl <- 1.2  ## slice factor (makes generated fitted curves/lines longer or shorter
xstart <- b1 - (sl * a1)
xstop  <- b1 + (sl * a1)

cat("xstart = ",xstart,"\n")
cat("xstop = ",xstop,"\n")

## this should help explain line 48 of create_curves.10062014-new.R
## newx  <- seq(1,200,length=200)
## can now be DYNAMIC
## newx  <- seq(xstart,xstop,length=200)



#############################
#### GENERATE IMAGES ########
## Note: This is using R's ##
## default plot creation.  ##
## will look even better   ##
## we use GGplot.          ##
#############################

####FOR PDF#####
if (!exists("perrin")){pdf('singleRegression.pdf', family=titleFont)}
if (exists("perrin")){pdf('singleRegression.pdf')}
plot(sx,sy, axes=TRUE, ann=FALSE, col=color4)
#### newx is the span of x in the plot.  Hardcoded for now.  This will have to become 
#### dynamic in order to accomodate all datasets.
newx  <- seq(xstart,xstop,length=200)
lines(newx,predict(sunlogistic,newdata=data.frame(sx=newx)),lwd=2, col=color1)
title(main=plotTitle, col.main=color5, font.main=4)
title(xlab=xaxis, col.lab=color2)
title(ylab=yaxis, col.lab=color3)
title(sub=paste("a:",growthRateCalc1, "   k:",carryingCapCalc1, "   b:",midpointCalc1))
sunlogistic




####FOR JPEG#####
jpeg('singleRegression.jpg')
plot(sx,sy, axes=TRUE, ann=FALSE, col=color4)
newx  <- seq(xstart,xstop,length=200)
lines(newx,predict(sunlogistic,newdata=data.frame(sx=newx)),lwd=2, col=color1)
title(main=plotTitle, col.main=color5, font.main=4)
title(xlab=xaxis, col.lab=color2)
title(ylab=yaxis, col.lab=color3)
title(sub=paste("a:",growthRateCalc1, "   k:",carryingCapCalc1, "   b:",midpointCalc1))
sunlogistic

##### TURN OFF THE IMAGE CREATION DEVICE #####
dev.off()

##### PLOT USING GGPLOT ######
## FIT THE CURVE
fitted = data.frame(sx=newx, sy=predict(sunlogistic, data.frame(sx=newx)) )

## PLOT THE CURVE
plotted <- ggplot(data=fileContents, aes(x=sx, y=sy,)) + geom_point(colour=color4,size=3) + geom_line(data=fitted, colour=color1)

## ADD FORMATTING OPTIONS
plotted + ylab(yaxis) + xlab(xaxis) + ggtitle(plotTitle) + theme(plot.title = element_text(size=24, family=titleFont, face="bold", colour=color5, vjust=1.5), axis.title.x = element_text(family=titleFont, face="bold", colour=color2, size=20), axis.text.x = element_text(angle=90, vjust=0.5, size=12), axis.title.y = element_text(family=titleFont, face="bold", colour=color3, size=20), axis.text.y = element_text(angle=90, vjust=0.5, size=12))

## SAVE THE GGPLOT AS A JPG AND PDF
ggsave(file="ggplotNLS.jpg", width = 4, height = 4, dpi = 75, scale = 2)
ggsave(file="ggplotNLS.pdf")
