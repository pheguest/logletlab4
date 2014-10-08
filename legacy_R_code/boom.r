## cameron -- this R code fits a bilogistic to the us cummulative nuclear tests data
## which also exists in the javascript version (boom.json). 
# use 
# > source("boom.r")
# to load this file 

## this function seems to clear the workspace
rm(list=ls())

# start up help web browser
#help.start()

# the us nuclear test bi-logsitic  data 
bx = scan("boomx.txt")
by = scan("boomy.txt")

boombilogistic <- nls(by ~  (( k1 / (1 + exp( (log(81)/-a1) * (bx - b1)))) +
( k2 / (1 + exp( (log(81)/-a2) * (bx - b2))))),
start=c(a1=10,k1=600,b1=1960,a2=20,k2=300,b2=1980))

# page  281 ISwRII
plot(bx,by)
newx  <- seq(1930,2000,length=500)
lines(newx,predict(boombilogistic,newdata=data.frame(bx=newx)),lwd=2)
