## INCLUDE LIBRARIES. GGPLOT2 FOR GRAPHING. XLCONNECT FOR READING EXCEL FILES.  EXTRAFONT FOR GILL SANS FONT.  
library(ggplot2)
library(XLConnect)
library(extrafont)
loadfonts()

## IF THE FILE UPLOAD IS A CSV, READ IT AS SUCH.
if(file_ext == "csv")
{
  fileContents <- read.csv(file=fileUpload,head=TRUE,sep=",")
}

## IF THE FILE UPLOAD IS XLS, READ IT AS SUCH.
if(file_ext == "xls")
{
  fileContents <- readWorksheetFromFile(fileUpload, sheet=1)
}

## EXTRACT INFO FROM THE FILES -- COLUMN NAMES, # OF COLUMNS, RAW VALUES.
xaxis <- colnames(fileContents[1])
yaxis <- colnames(fileContents[2])
numCols <- ncol(fileContents)
numYs <- numCols - 1
dataRangeX <- range(fileContents[,1])
dataRangeY <- range(c(fileContents[,2:numCols]))
sx = fileContents[,1]
sy = fileContents[,2]

##### INITIAL ESTIMATES FOR A, K, B BASED ON THE DATA
estimateA = (max(sx) - min(sx))
estimateK = max(sy) * 1.1
estimateB = (max(sx) + min(sx))/2
##### END INITIAL ESTIMATES 

##### STANDARDIZED RESIDUALS FROM SUNLINEAR ######
sunlinear <- lm(sy ~ sx)
stdres <- rstandard(sunlinear)
SLRes <- data.frame(sx, stdres)
## FORMAT THE PLOT ####
ggplot(SLRes, aes(x=sx, y=stdres)) + xlab(xaxis) + ylab("Residual Value") + geom_point(colour=color4, size=3) + ggtitle("Standardized Residuals") + geom_abline(colour = "red", size = 2)
## SAVE THE PLOT IN PDF AND JPG FORMATS USING GGPLOT.  OTHER FORMATTING OPTIONS ARE AVAILABLE (PNG, TIFF...)
ggsave(file="ggplotStandardResiduals.pdf")
ggsave(file="ggplotStandardResiduals.jpg", width = 4, height = 4, dpi = 75, scale = 2)

## SAVE THE PLOT IN PDF AND JPG USING THE STANDARD PLOTTING METHOD, JUST TO HAVE.
pdf('standardizedResiduals.pdf', family=titleFont)
plot(sx, stdres, xlab=xaxis, main="Standardized Residuals")
jpeg('standardizedResiduals.jpg', family=titleFont)
plot(sx, stdres, xlab=xaxis, main="Standardized Residuals")
abline(0, 0) # the horizon
##### END STANDARDIZED RESIDUALS ######

## IF ANALYSIS TYPE IS SINGLE LOGISTIC...
if(analysisType == "single"){

## DEFINE SUNLOGISTIC AS A FUNCTION.
## NLS IS A FUNCTION BUILT INTO THE R LANGUAGE. IT ACCEPTS SINGLE A, K, B INITIAL ESTIMATES (COMPUTED EARLIER ABOVE). THE NLS FUNCTION USES THESE ESTIMATES TO COMPUTE ITS OWN A, K, B.  AS LONG AS INITIAL A, K, B ARE IN THE APPROPRIATE BALLPARK, NLS WILL COMPUTE ACCURATELY. IF THIS FUNCTION MUST ACCEPT A RANGE, THEN IT SEEMS IT WILL HAVE TO BE REWRITTEN IN MODIFIED FORM.
sunlogistic <- nls(sy ~ ( k / (1 + exp( (log(81)/-a) * (sx - b)))),start=c(k=estimateK, a=estimateA, b=estimateB))

## PROVIDE THE RESIDUALS FROM SUNLOGISTIC FUNCTION. RETURNED IN RAW TEXT AND GRAPHED USING GGPLOT.
myResiduals <- residuals(sunlogistic)
sink("residuals.txt", append=FALSE, split=FALSE)
writeLines(formatOL(myResiduals))
sink()

resCols <- c(sx, myResiduals)
dfRes = data.frame(sx, myResiduals)
ggplot(dfRes, aes(x=sx, y=myResiduals)) + geom_line() + xlab(xaxis) + ylab("Residual Value") + geom_point(colour="#00B3FF") + ggtitle("Residuals")
ggsave(file="ggplotResiduals.pdf")
ggsave(file="ggplotResiduals.jpg", width = 4, height = 4, dpi = 75, scale = 2)

## GET THE ACTUAL A, K, B FROM NLS.
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

## PLOT THE LOGISTIC USING STANDARD PLOTTING METHOD (PDF)
# page  281 ISwRII
pdf('singleRegression.pdf', family=titleFont)
plot(sx,sy, axes=TRUE, ann=FALSE, col=color4)
newx  <- seq(xstart,xstop,length=200)
lines(newx,predict(sunlogistic,newdata=data.frame(sx=newx)),lwd=2, col=color1)
title(main=plotTitle, col.main=color5, font.main=4)
title(xlab=xaxis, col.lab=color2)
title(ylab=yaxis, col.lab=color3)
title(sub=paste("a: ", growthRateCalc1, "k: ", carryingCapCalc1, "b: ", midpointCalc1))
sunlogistic

dev.off()

#### STANDARD PLOTTING TO JPG #####
jpeg('singleRegression.jpg')
plot(sx,sy, axes=TRUE, ann=FALSE, col=color4)
newx  <- seq(xstart,xstop,length=200)
lines(newx,predict(sunlogistic,newdata=data.frame(sx=newx)),lwd=2, col=color1)
title(main=plotTitle, col.main=color5, font.main=4)
title(xlab=xaxis, col.lab=color2)
title(ylab=yaxis, col.lab=color3)
title(sub=paste("a: ", growthRateCalc1, "k: ", carryingCapCalc1, "b: ", midpointCalc1))
sunlogistic

dev.off()

## PLOT USING GGPLOT IN JPG AND PDF
fitted = data.frame(sx=newx, sy=predict(sunlogistic, data.frame(sx=newx)) )

plotted <- ggplot(data=fileContents, aes(x=sx, y=sy,)) + geom_point(colour=color4,size=3) + geom_line(data=fitted, colour=color1)

plotted + ylab(yaxis) + xlab(xaxis) + ggtitle(plotTitle) + theme(plot.title = element_text(size=24, family=titleFont, face="bold", colour=color5, vjust=1.5), axis.title.x = element_text(family=titleFont, face="bold", colour=color2, size=20), axis.text.x = element_text(angle=90, vjust=0.5, size=12), axis.title.y = element_text(family=titleFont, face="bold", colour=color3, size=20), axis.text.y = element_text(angle=90, vjust=0.5, size=12))

ggsave(file="ggplotNLS.jpg", width = 4, height = 4, dpi = 75, scale = 2)
ggsave(file="ggplotNLS.pdf")


}######END if(analysisType == "single") 

##### BASIC TIME SERIES IN PDF AND JPG USING STANDARD PLOTTING METHOD #######
curveMean <- mean(sy)
curveSD <- sd(sy)
curveLB <- min(sy) - 10
curveUB <- max(sy) + 10

pdf('timeSeries.pdf')
jpeg('timeSeries.jpg')
plot(dataRangeX, dataRangeY, type="o", xlab=xaxis, ylab=yaxis)

colors <- rainbow(numYs)
linetype <- c(1:numYs)
plotchar <- seq(18,18+numYs,1)

# add lines
for (i in 1:numYs) {
  lines(fileContents[,1], fileContents[,i+1], type="b", lwd=1.5, lty=linetype[i], col=colors[i], pch=plotchar[i])
}
dev.off()

## BASIC TIME SERIES IN GGPLOT ####
ggplot(fileContents, aes(x=sx, y=sy)) + geom_line() + xlab(xaxis) + ylab(yaxis) + geom_point(colour="#00B3FF") + ggtitle("Basic Time Series")
ggsave(file="ggplotTimeSeries.pdf")
ggsave(file="ggplotTimeSeries.jpg", width = 4, height = 4, dpi = 75, scale = 2)


#### NORMAL DISTRIBUTION CURVE IN STANDARD PLOTTING METHOD (JPG AND PDF) #######
pdf('normalDistCurve.pdf')
jpeg('normalDistCurve.jpg')
curve(dnorm(x, curveMean, curveSD), curveLB, curveUB)
dev.off()

#### NORMAL DISTRIBUTION CURVE IN GGPLOT (JPG AND PDF) #######
yDNorm <- dnorm(sy, curveMean, curveSD)
ggplot(data.frame(sy), aes(sy)) + stat_function(fun = dnorm, args = list(mean = curveMean, sd = curveSD)) + xlab("Density") + ylab(" ") + ggtitle("Normal Distribution")
ggsave(file="ggplotNormDist.pdf")
ggsave(file="ggplotNormDist.jpg", width = 4, height = 4, dpi = 75, scale = 2)

## IF ANALYSIS TYPE IS BILOGISTIC...
if(analysisType == "bilogistic"){
## JUST A PLACEHOLDER.  CREATES AN EMPTY PLOT.
pdf('BilogisticCurve.pdf')
jpeg('BilogisticCurve.jpg')
plot(0, 0, type='n')
title(main='This will be a bi-logistic curve')
title(sub=paste("a1: ", growthRate1, "k1: ", carryingCap1, "b1: ", midpoint1, "a2: ", growthRate2, "k2: ", carryingCap2, "b2: ", midpoint2))
dev.off()
}###### END if(analysisType == "bilogistic")

## IF ANALYSIS TYPE IS TRILOGISTIC...
if(analysisType == "trilogistic"){
## JUST A PLACEHOLDER.  CREATES AN EMPTY PLOT.
pdf('TrilogisticCurve.pdf')
jpeg('TrilogisticCurve.jpg')
plot(0, 0, type='n')
title(main='This will be a tri-logistic curve')
title(sub=paste("a1: ", growthRate1, "k1: ", carryingCap1, "b1: ", midpoint1, "a2: ", growthRate2, "k2: ", carryingCap2, "b2: ", midpoint2, "a3: ", growthRate3, "k3: ", carryingCap3, "b3: ", midpoint3))
dev.off()
}####### END if(analysisType == "trilogistic")

## IF ANALYSIS TYPE IS LOGISTIC REGRESSION...
if(analysisType == "logisticregression"){
## JUST A PLACEHOLDER.  CREATES AN EMPTY PLOT.
pdf('LogisticRegression.pdf')
plot(0, 0, type='n')
title(main='This will be Logistic regression')
}##### END if(analysisType == "logisticregression")
