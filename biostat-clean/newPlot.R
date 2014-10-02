##############################
#####CAMERON CODE#############
##############################
fileContents <- read.csv(file=fileUpload,head=TRUE,sep=",")
fileContentsHeader <- read.csv(file=fileUpload,header=FALSE,sep=",")

xaxis <- colnames(fileContents[1])
yaxis <- colnames(fileContents[2])

library(minpack.lm)
tmp.df <-data.frame(xaxis=fileContents[,1],yaxis=fileContents[,2])
res <- nls(yaxis ~ SSlogis(log(xaxis), Asym, xmid, scal), tmp.df)

## model based on a list of parameters 
getPred <- function(parS, xx)  SSlogis(log(xx), parS$Asym, parS$xmid, parS$scal)

## plot data
plot(tmp.df, main="data")

## residual function 
residFun <- function(p, observed, xx) observed - getPred(p,xx)

## starting values for parameters  
parStart <- as.list(coef(res))

## perform fit 
nls.out <- nls.lm(par=parStart, fn = residFun, observed = tmp.df$yaxis, xx = tmp.df$xaxis, control = nls.lm.control(nprint=1))

## plot model evaluated at final parameter estimates  
lines(tmp.df$xaxis,getPred(as.list(coef(nls.out)), tmp.df$xaxis), col=2, lwd=2)
fit.res <- SSlogis(log(seq(1,100,0.001)), data.frame(t(coef(res)))$Asym,data.frame(t(coef(res)))$xmid,data.frame(t(coef(res)))$scal)
lines(seq(1,100,0.001), fit.res, col=3, lwd=1,lty=4)

## summary information on parameter estimates
summary(nls.out) 
summary(res)




##############################
#######PERRIN CODE############
##############################
sx = fileContents[,1]
sy = fileContents[,2]

sunlinear <- lm(sy ~ sx)
sunlogistic <- nls(sy ~ ( k / (1 + exp( (log(81)/-a) * (sx - b)))),start=c(k=250, a=40, b=30))
# page  281 ISwRII
pdf('rplotsunflower.pdf')
plot(sx,sy, axes=TRUE, ann=FALSE, col=color4)
newx  <- seq(1,200,length=200)
lines(newx,predict(sunlogistic,newdata=data.frame(sx=newx)),lwd=2, col=color1)
title(main=plotTitle, col.main=color5, font.main=4)
title(xlab=xaxis, col.lab=color2)
title(ylab=yaxis, col.lab=color3)
sunlogistic

##dev.off()
