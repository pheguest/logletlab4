# use source("psm3.r")
# to load this file 

## this function seems to clear the workspace
rm(list=ls())

# start up help web browser
help.start()

# the sunflower data 
sx = scan("sunx.txt")
sy = scan("suny.txt")

sunlinear <- lm(sy ~ sx)
# this is not showing up for some reason ... 
# summary(sunlinear) 
# plot(sx,sy)
# abline(sunlinear)

sunlogistic <- nls(sy ~ ( k / (1 + exp( (log(81)/-a) * (sx - b)))),start=c(k=250, a=40, b=30))
# page  281 ISwRII
plot(sx,sy)
newx  <- seq(1,100,length=100)
lines(newx,predict(sunlogistic,newdata=data.frame(sx=newx)),lwd=2)
